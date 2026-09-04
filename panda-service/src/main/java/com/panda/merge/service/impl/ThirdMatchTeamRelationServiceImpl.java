package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchTeamRelationDao;
import com.panda.merge.dto.ThirdMatchTeamRelationDetail;
import com.panda.merge.mapper.ThirdMatchTeamRelationMapper;
import com.panda.merge.model.ThirdMatchTeamRelation;
import com.panda.merge.model.ThirdMatchTeamRelationExample;
import com.panda.merge.service.ThirdMatchTeamRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.FIX;
import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * <Description> 三方赛事球队关联关系信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchTeamRelationServiceImpl extends BaseServiceImpl<ThirdMatchTeamRelation> implements ThirdMatchTeamRelationService {

    @Autowired
    private ThirdMatchTeamRelationMapper thirdMatchTeamRelationMapper;

    @Autowired
    private ThirdMatchTeamRelationDao thirdMatchTeamRelationDao;

    @Override
    @Cacheable(key = "'ThirdMatchTeamRelationMap:'+#matchId",unless="#result == null || #result.size() == 0")
    public Map<String,ThirdMatchTeamRelation> getMatchIdAndPosition2ItemByMatchId(Long matchId){
        ThirdMatchTeamRelationExample example = new ThirdMatchTeamRelationExample();
        example.createCriteria().andMatchIdEqualTo(matchId);
        List<ThirdMatchTeamRelation> resList = thirdMatchTeamRelationMapper.selectByExample(example);
        return resList.stream().collect(Collectors.toMap(thi->thi.getMatchId()+FIX+thi.getMatchPosition(), i -> i));
    }

    @Override
    public List<ThirdMatchTeamRelation> getItemsByMatchIds(List<Long> matchIds){
        if(CollectionUtils.isEmpty(matchIds)){
            return new LinkedList<>();
        }
        ThirdMatchTeamRelationExample example = new ThirdMatchTeamRelationExample();
        example.createCriteria().andMatchIdIn(matchIds);
        return thirdMatchTeamRelationMapper.selectByExample(example);
    }


    @Override
    @Cacheable(key = "'ThirdMatchTeamRelation:'+#matchId+'-'+#teamId",unless="#result == null")
    public ThirdMatchTeamRelation getItemByMatchIdAndTeamId(Long matchId,Long teamId){
        ThirdMatchTeamRelationExample example = new ThirdMatchTeamRelationExample();
        example.createCriteria().andMatchIdEqualTo(matchId).andTeamIdEqualTo(teamId);
        List<ThirdMatchTeamRelation> resList = thirdMatchTeamRelationMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(resList)) {
            return null;
        }
        return resList.get(0);
    }

    @Override
    @Deprecated
    public ThirdMatchTeamRelation saveOrupdate(ThirdMatchTeamRelation upItem){
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        if(!Objects.isNull(upItem.getCreateTime())){
            thirdMatchTeamRelationMapper.insertSelective(upItem);
            refreshCache(upItem);
        }else{
            if(!Objects.isNull(upItem.getModifyTime())){
                ThirdMatchTeamRelation item = new ThirdMatchTeamRelation();
                //赛事ID，主客队位置，创建时间 无需修改
                BeanUtil.copyProperties(upItem,item,"matchId","matchPosition","createTime");
                thirdMatchTeamRelationMapper.updateByPrimaryKey(item);
                refreshCache(upItem);
            }
        }
        return upItem;
    }

    @Override
    public List<ThirdMatchTeamRelation> saveOrupdateList(List<ThirdMatchTeamRelation> list,String linkId){
        if(CollectionUtils.isEmpty(list)){
            return list;
        }
        Long matchId = list.get(0).getMatchId();
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        List<ThirdMatchTeamRelation> addList = list.stream().filter(obj -> !Objects.isNull(obj.getCreateTime())).collect(Collectors.toList());
        List<ThirdMatchTeamRelation> updateList = list.stream().filter(obj -> Objects.isNull(obj.getCreateTime()) && !Objects.isNull(obj.getModifyTime())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(addList)){
            try{
                List<ThirdMatchTeamRelation> newAddList = new ArrayList<>();
                for (ThirdMatchTeamRelation item: addList) {
                    //2S内不允许重复入库
                    String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamRelation:DbLock:%s_%s", item.getMatchId(), item.getTeamId());
                    //如果是新增,避免同时新增出现唯一索引异常
                    if(redisService.tryLockOnce(lockKey,lockKey,TWO)){
                        newAddList.add(item);
                    }else{
                        log.info("linkId={},saveOrupdateList,ThirdMatchTeamRelation,三方赛事ID={}，三方球队ID={},2S内不允许重复入库",linkId,item.getMatchId(),item.getTeamId());
                    }
                }
                if(!CollectionUtils.isEmpty(newAddList)){
                    thirdMatchTeamRelationDao.insertList(newAddList);
                }
            }catch (DataAccessException e){
                redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamRelationMap:"+ matchId);
                log.error("linkId:"+linkId+",新增三方赛事球队关系，唯一主键冲突，Exception:",e);
            }
        }
        if(!CollectionUtils.isEmpty(updateList)){
            thirdMatchTeamRelationDao.updateList(updateList);
        }
        if(list.size() > 1){
            Map<String, ThirdMatchTeamRelation> matchId2Obj = list.stream().collect(Collectors.toMap(thi -> thi.getMatchId() + FIX + thi.getMatchPosition(), i -> i));
            redisService.hSetAll(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamRelationMap:"+ matchId,matchId2Obj);
        }else{
            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamRelationMap:"+ matchId);
        }
        return list;
    }

    @Override
    @Cacheable(key = "'ThirdMatchTeamRelationDetail:'+#matchId",unless="#result == null || #result.size() == 0")
    public List<ThirdMatchTeamRelationDetail> getItemsByMatchId(Long matchId) {
        return thirdMatchTeamRelationDao.getItemsByMatchId(matchId);
    }

    /**
     * 更新缓存
     * */
    private ThirdMatchTeamRelation refreshCache(ThirdMatchTeamRelation item){
        refreshHashCache(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamRelationMap:"+ item.getMatchId(),item.getMatchId()+FIX+item.getMatchPosition(),item);
        redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamRelationDetail:" + item.getMatchId());
        redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchTeamRelation:" + item.getMatchId()+'-'+ item.getTeamId(),item);
        return item;
    }

    @Override
    public List<ThirdMatchTeamRelation> listByTeamId(Long teamId){
        ThirdMatchTeamRelationExample example = new ThirdMatchTeamRelationExample();
        example.createCriteria().andTeamIdEqualTo(teamId);
        return  thirdMatchTeamRelationMapper.selectByExample(example);
    }
}
