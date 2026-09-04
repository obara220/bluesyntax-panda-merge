package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdSportTeamDao;
import com.panda.merge.dto.ThirdSportTeamDetail;
import com.panda.merge.mapper.ThirdSportTeamMapper;
import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.ThirdSportTeam;
import com.panda.merge.model.ThirdSportTeamExample;
import com.panda.merge.service.LanguageInternationService;
import com.panda.merge.service.ThirdSportTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/29 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportTeamServiceImpl extends BaseServiceImpl<ThirdSportTeam> implements ThirdSportTeamService {

    @Autowired
    private ThirdSportTeamMapper thirdSportTeamMapper;

    @Autowired
    private ThirdSportTeamDao thirdSportTeamDao;

    @Autowired
    private LanguageInternationService languageInternationService;

    @Override
    @Cacheable(key = "'ThirdSportTeam:' + #id",unless = "#result == null")
    public ThirdSportTeam getItem(Long id) {
        return refreshCache(thirdSportTeamMapper.selectByPrimaryKey(id));
    }

    @Override
    public ThirdSportTeam getItemByPrimaryKey(Long id){
        return refreshCache(thirdSportTeamMapper.selectByPrimaryKey(id));
    }

    @Override
    public List<ThirdSportTeamDetail> getItems(List<Long> thirdTeamIds){
        if(CollectionUtils.isEmpty(thirdTeamIds)){
            return new LinkedList<>();
        }
        return setLanguages(thirdSportTeamDao.getItemsByTeamIds(thirdTeamIds));
    }

    private List<ThirdSportTeamDetail> setLanguages(List<ThirdSportTeamDetail> list){
        if(!CollectionUtils.isEmpty(list)){
            Set<Long> nameCodes = list.stream().map(obj -> obj.getNameCode()).collect(Collectors.toSet());
            Map<Long, List<LanguageInternation>> nameCode2Languages = languageInternationService.getItemsByNameCodes(Lists.newArrayList(nameCodes));
            for (ThirdSportTeamDetail item : list) {
                item.setIl8nNameList(nameCode2Languages.get(item.getNameCode()));
            }
        }
        return list;
    }

    @Override
    public List<ThirdSportTeam> getItemsByThirdTeamSourceIds(List<String> dataSourceCodes, Long sportId, List<String> thirdTeamSourceIds){
        if(CollectionUtils.isEmpty(dataSourceCodes) || CollectionUtils.isEmpty(thirdTeamSourceIds)){
            return new LinkedList<>();
        }
        ThirdSportTeamExample example = new ThirdSportTeamExample();
        if(null != sportId){
            example.createCriteria().andDataSourceCodeIn(dataSourceCodes).andSportIdEqualTo(sportId).andThirdTeamSourceIdIn(thirdTeamSourceIds);
        }else{
            example.createCriteria().andDataSourceCodeIn(dataSourceCodes).andThirdTeamSourceIdIn(thirdTeamSourceIds);
        }
        return thirdSportTeamMapper.selectByExample(example);
    }


    @Override
    @Cacheable(key = "'ThirdSportTeam:'+#dataSourceCode+'-'+#sportId+'-'+#thirdTeamSourceId",unless="#result == null")
    public ThirdSportTeam getOneItem(String dataSourceCode, Long sportId, String thirdTeamSourceId) {
        return getItemByExample(dataSourceCode,sportId,thirdTeamSourceId);
    }

    @Override
    public ThirdSportTeam getItemByExample(String dataSourceCode, Long sportId, String thirdTeamSourceId){
        ThirdSportTeamExample example = new ThirdSportTeamExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andSportIdEqualTo(sportId).andThirdTeamSourceIdEqualTo(thirdTeamSourceId);
        List<ThirdSportTeam> resList = thirdSportTeamMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(resList)){
            return null;
        }
        return refreshCache(resList.get(0));
    }

    @Override
    public ThirdSportTeam saveOrupdate(ThirdSportTeam upItem){
        return updateByequalsThirdSportTeam(upItem);
    }

    @Override
    public ThirdSportTeam getItemByExampleNoSportId(String dataSourceCode, String thirdTeamSourceId) {
        ThirdSportTeamExample example = new ThirdSportTeamExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdTeamSourceIdEqualTo(thirdTeamSourceId);
        List<ThirdSportTeam> resList = thirdSportTeamMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(resList)){
            return null;
        }
        return refreshCache(resList.get(0));
    }


    /** 刷新缓存*/
    private ThirdSportTeam refreshCache(ThirdSportTeam item){
        if(null != item){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportTeam:" + item.getId(), item, RedisConfig.REDIS_MY_TIME);
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportTeam:" + item.getDataSourceCode()+"-"+item.getSportId()+"-"+item.getThirdTeamSourceId(),item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }

    /** 获取缓存*/
    private ThirdSportTeam getCache(Long id){
        Object itemObj = redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportTeam:" + id);
        ThirdSportTeam oldItem;
        if(null != itemObj){
            oldItem = JSON.parseObject(JSON.toJSONString(itemObj),ThirdSportTeam.class);
        }else{
            oldItem = getItemByPrimaryKey(id);
        }
        return oldItem;
    }

    /**
     * 比较需要修改的三方球队和数据库中赛事是否存在差异，存在则修改
     * */
    private ThirdSportTeam updateByequalsThirdSportTeam(ThirdSportTeam upItem){
        //获取库中三方球队
        ThirdSportTeam oldItem = getCache(upItem.getId());
        if(null != oldItem){
            upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //需要修改的三方赛事比较类
            ThirdSportTeam checkUpItem = new ThirdSportTeam();
            BeanUtil.copyProperties(oldItem,checkUpItem);
            //忽略空值拷贝
            BeanUtil.copyProperties(upItem,checkUpItem, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            //比较如果本次修改数据和库中数据不一致，则需要修改
            if(!EntityEqualsUtils.equalsIsObjToString(checkUpItem,oldItem)){
                ThirdSportTeam item = new ThirdSportTeam();
                //数据来源，运动类型，三方数据源球队ID无需修改
                BeanUtil.copyProperties(upItem,item,"dataSourceCode","sportId","thirdTeamSourceId","createTime");
                thirdSportTeamMapper.updateByPrimaryKeySelective(item);
                //刷新缓存
                refreshCache(checkUpItem);
            }
            return checkUpItem;
        }else{
            upItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            upItem.setModifyTime(upItem.getCreateTime());
            thirdSportTeamMapper.insertSelective(upItem);
            return refreshCache(upItem);
        }
    }

}
