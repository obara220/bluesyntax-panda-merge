package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdSportPlayerDao;
import com.panda.merge.dto.ThirdSportPlayerDetail;
import com.panda.merge.mapper.ThirdSportPlayerMapper;
import com.panda.merge.mapper.ThirdTeamPlayerRelationMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.ThirdSportPlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_KEY_DATABASE;
import static com.panda.merge.constant.ConstantSystem.FIX;
import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * 球队球员信息
 * @author      tell
 * @since       2020年9月3日15:24:52
 */
@Slf4j
@Service
@CacheConfig(cacheNames = REDIS_KEY_DATABASE)
public class ThirdSportPlayerServiceImpl extends BaseServiceImpl<ThirdSportPlayerDetail> implements ThirdSportPlayerService {

    @Autowired
    private ThirdSportPlayerMapper thirdSportPlayerMapper;

    @Autowired
    private ThirdTeamPlayerRelationMapper thirdTeamPlayerRelationMapper;

    @Autowired
    private ThirdSportPlayerDao thirdSportPlayerDao;

    @Override
    public Map<String, ThirdSportPlayerDetail> getUnique2ItemByTeamId(ThirdSportTeam team){
        //容错处理，同一个球队和同一个球员应该只有一个关系，多余的关系删除
        ThirdTeamPlayerRelationExample example = new ThirdTeamPlayerRelationExample();
        example.createCriteria().andTeamIdEqualTo(team.getId());
        Map<Long, List<ThirdTeamPlayerRelation>> playerId2List = thirdTeamPlayerRelationMapper.selectByExample(example).stream().collect(Collectors.groupingBy(obj -> obj.getPlayerId()));
        for (Long key: playerId2List.keySet()) {
            List<ThirdTeamPlayerRelation> thirdTeamPlayerRelationList = playerId2List.get(key);
            //保留第一个关系，删除多余的关系
            for (int i=0;i<thirdTeamPlayerRelationList.size();i++) {
                if(i > 0){
                    //删除多余的关系
                    thirdTeamPlayerRelationMapper.deleteByPrimaryKey(thirdTeamPlayerRelationList.get(i).getId());
                }
            }
        }
        //容错处理后的数据
        List<ThirdSportPlayerDetail> resList = thirdSportPlayerDao.getItemsByTeamId(team);
        return resList.stream().collect(Collectors.toMap(thi->thi.getDataSourceCode()+FIX+thi.getSportId()+FIX+thi.getThirdSourcePlayerId(), i -> i));
    }

    @Override
    @Cacheable(key = "'ThirdSportPlayerDetail:'+ #dataSourceCode +'-'+ #sportId +'-'+ #thirdSourcePlayerId",unless="#result == null")
    public ThirdSportPlayerDetail getItem(String dataSourceCode, Long sportId, String thirdSourcePlayerId){
        ThirdSportPlayerExample example = new ThirdSportPlayerExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andSportIdEqualTo(sportId).andThirdSourcePlayerIdEqualTo(thirdSourcePlayerId);
        List<ThirdSportPlayer> resList = thirdSportPlayerMapper.selectByExampleWithBLOBs(example);
        if(CollectionUtils.isEmpty(resList)){
            return null;
        }
        ThirdSportPlayerDetail thirdSportPlayerChild = new ThirdSportPlayerDetail();
        BeanUtils.copyProperties(resList.get(0), thirdSportPlayerChild);
        return thirdSportPlayerChild;
    }

    @Override
    public ThirdSportPlayerDetail getItemByPrimaryKey(Long id){
        ThirdSportPlayer thirdSportPlayer = thirdSportPlayerMapper.selectByPrimaryKey(id);
        ThirdSportPlayerDetail thirdSportPlayerChild = new ThirdSportPlayerDetail();
        BeanUtils.copyProperties(thirdSportPlayer, thirdSportPlayerChild);
        return refreshCache(thirdSportPlayerChild);
    }

    @Override
//    @CachePut(key = "'ThirdSportPlayerDetail:' + #upItem.dataSourceCode +'-'+ #upItem.sportId +'-'+ #upItem.thirdSourcePlayerId",unless="#result == null")
    public ThirdSportPlayerDetail saveOrupdate(ThirdSportPlayerDetail upItem, JSONObject affectedObject){
        int affected = 0;
        /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
        if(null != upItem.getCreateTime()){
            upItem.setModifyTime(upItem.getCreateTime());
            ThirdSportPlayer item = new ThirdSportPlayer();
            BeanUtil.copyProperties(upItem,item);
            //2S内不允许重复入库
            String lockKey = String.format(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportPlayerDetail:DbLock:%s_%s_%s", item.getDataSourceCode(), item.getSportId(), item.getThirdSourcePlayerId());
            //如果是新增,避免同时新增出现唯一索引异常
            if(redisService.tryLockOnce(lockKey,lockKey,TWO)){
                try{
                    affected = thirdSportPlayerMapper.insertSelective(item);
                }catch (DataAccessException e){
                    log.error("linkId="+upItem.getLinkId()+",dataSourceCode="+upItem.getDataSourceCode()+",SportId="+item.getSportId()+",源球员ID="+item.getThirdSourcePlayerId()+" 新增球员,唯一主键冲突");
                    redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportPlayerDetail:" + item.getDataSourceCode()+'-'+ item.getSportId()+'-'+ item.getThirdSourcePlayerId());
                }
            }else{
                log.info("linkId={},ThirdSportPlayer,数据源编码={}，源赛事ID={},2S内不允许重复入库",upItem.getLinkId(),item.getDataSourceCode(),item.getThirdSourcePlayerId());
            }
        }else{
            if(null != upItem.getModifyTime()){
                ThirdSportPlayer item = new ThirdSportPlayer();
                //数据来源，运动类型，三方数据源人员ID 无需修改
                BeanUtil.copyProperties(upItem,item,"dataSourceCode","sportId","thirdSourcePlayerId","createTime");
                affected = thirdSportPlayerMapper.updateByPrimaryKeySelective(item);
            }
        }
        affectedObject.put("affected",affected);
        return refreshCache(upItem);
    }

    /** 刷新缓存*/
    public ThirdSportPlayerDetail refreshCache(ThirdSportPlayerDetail item){
        if(null != item){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportPlayerDetail:" + item.getDataSourceCode()+'-'+ item.getSportId()+'-'+ item.getThirdSourcePlayerId(), item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }

}
