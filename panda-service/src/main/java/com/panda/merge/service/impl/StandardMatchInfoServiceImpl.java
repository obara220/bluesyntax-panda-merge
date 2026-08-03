package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.StandardMatchInfoDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 标准赛事信息 <br>
 *
 * @author tell
 * @since 2020年9月10日10:32:26
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardMatchInfoServiceImpl implements StandardMatchInfoService {

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    private StandardMatchInfoDao standardMatchInfoDao;

    @Resource
    private RedisHelper redisHelper;

    @Override
    public Page<StandardMatchInfoDetail> getStandardMatchInfoPage(PageModel<StandardMatchInfoDTO> page) {
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return standardMatchInfoDao.getItemPage();
    }

    @Override
    public Page<StandardMatchInfoDetail> getItemPageByModifyTime(PageModel<StandardMatchInfoDTO> page) {
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return standardMatchInfoDao.getItemPageByModifyTime(page.getData());
    }

    @Override
    public StandardMatchInfoDetail getStandardMatchInfoByThirdSourceId(String dataSourceCode, Long sportId, String thirdSourceId) {
        StandardMatchInfoDTO standardMatchInfoDTO = new StandardMatchInfoDTO();
        standardMatchInfoDTO.setDataSourceCode(dataSourceCode);
        standardMatchInfoDTO.setThirdSportId(sportId);
        standardMatchInfoDTO.setThirdMatchSourceId(thirdSourceId);
        StandardMatchInfoDetail item = standardMatchInfoDao.getItemByThirdSourceId(standardMatchInfoDTO);
        refreshCache(item);
        return item;
    }

    @Override
    public StandardMatchInfoDetail getDetailItem(Long id) {
        StandardMatchInfoDetail item = standardMatchInfoDao.getItemById(id);
        refreshCache(item);
        return item;
    }

    @Override
    @Cacheable(key = "'StandardMatchInfo:'+#id", unless = "#result == null")
    public StandardMatchInfo getItem(Long id) {
        return standardMatchInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<StandardMatchInfo> getItems(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) {
            return Collections.EMPTY_LIST;
        }
        List<StandardMatchInfo> result = new ArrayList<>();
        List<Long> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = ids.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + t).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(ids, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }
        log.info("2724,查询标准赛事数据库：{}", requiredCallItems);
        // Obtaining remained data from mysql
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andIdIn(requiredCallItems);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        result.addAll(standardMatchInfos);

        // Storing the remained data into redis
        Map<String, Object> redisVal = standardMatchInfos.stream().collect(Collectors.toMap(t->RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + t.getId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    public StandardMatchInfo getItemByMatchManageId(String MatchManageId) {
        StandardMatchInfoExample example =new  StandardMatchInfoExample();
        example.createCriteria().andMatchManageIdEqualTo(MatchManageId);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(standardMatchInfos))
        {
            return null;
        }
        return standardMatchInfos.get(0);
    }

    @Override
    public StandardMatchInfo getItemByPrimaryKey(Long id){
        return refreshCache(standardMatchInfoMapper.selectByPrimaryKey(id));
    }

    @Override
    public List<StandardMatchInfo> getItemByPrimaryKeys(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andIdIn(ids);
        return standardMatchInfoMapper.selectByExample(example);
    }

    @Override
//    @CachePut(key = "'StandardMatchInfo:'+ #standardMatchInfo.id", unless = "#result == null")
    public StandardMatchInfo updateByPrimaryKeySelective(StandardMatchInfo standardMatchInfo) {
        return updateByPrimaryKeySelective(standardMatchInfo,null);
    }


    @Override
    public StandardMatchInfo updateByPrimaryKeySelective(StandardMatchInfo standardMatchInfo,String linkId) {
        String hashValue = UUIdUtils.getId()+"_lock_StandardMatchInfo";
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "lock::StandardMatchInfo:" + standardMatchInfo.getId();
        try {
            redisService.tryLock(redisKey,hashValue,10,10);
            StandardMatchInfo oldStandardMatchInfo = getCache(standardMatchInfo.getId());
            if(null != oldStandardMatchInfo){
                standardMatchInfo.setCreateTime(null);
                standardMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                int num = standardMatchInfoMapper.updateByPrimaryKeySelective(standardMatchInfo);
                //修改完成后，将修改后的字段忽略空值拷贝到原对象
                BeanUtil.copyProperties(standardMatchInfo,oldStandardMatchInfo,CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                log.info("linkId=【{}】【标准赛事ID="+standardMatchInfo.getId()+"】updateByPrimaryKeySelective,修改三方赛事数据条数num={},修改后赛事状态={}",linkId,num,oldStandardMatchInfo.getMatchStatus());
            }
            return refreshCache(oldStandardMatchInfo);
        } finally {
            redisService.unLock(redisKey,hashValue);
        }
    }

    @Override
//    @CachePut(key = "'StandardMatchInfo:'+ #standardMatchInfo.id", unless = "#result == null")
    public StandardMatchInfo updateByPrimaryKey(StandardMatchInfo standardMatchInfo) {
        //todo 此处传入的标准赛事信息必须是全字段，不然覆盖缓存后，会遗漏字段值
        standardMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
        return refreshCache(standardMatchInfo);
    }

    @Autowired
    public RedisService redisService;

    @Override
    public int updateByExampleSelective(StandardMatchInfo standardMatchInfo, StandardMatchInfoExample example) {
        int num = 0;
        StandardMatchInfo oldStandardMatchInfo = getCache(standardMatchInfo.getId());
        if(null != oldStandardMatchInfo){
            standardMatchInfo.setCreateTime(null);
            standardMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            num = standardMatchInfoMapper.updateByExampleSelective(standardMatchInfo, example);
            if(num > 0){
                //忽略空值拷贝
                BeanUtil.copyProperties(standardMatchInfo,oldStandardMatchInfo,CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            }
            refreshCache(oldStandardMatchInfo);
        }
        return num;
    }

    /** 获取缓存*/
    private StandardMatchInfo getCache(Long id){
        Object itemObj = redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + id);
        StandardMatchInfo oldItem;
        if(null != itemObj){
            oldItem = JSON.parseObject(JSON.toJSONString(itemObj),StandardMatchInfo.class);
        }else{
            oldItem = getItem(id);
        }
        return oldItem;
    }

    /** 刷新缓存*/
    @Override
    public StandardMatchInfo refreshCache(StandardMatchInfo item){
        if(null != item){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + item.getId(), item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }
    
    /**
     * 根据玩法Id和赛种Id查询当前未结束的赛事
     * @param categoryId
     * @param sportId
     * @return List<StandardMatchInfo>
     */
    public List<StandardMatchInfo> selectActiveByMarketCategoryIdAndSportId(Long categoryId, Long sportId){
        return standardMatchInfoDao.selectActiveByMarketCategoryIdAndSportId(categoryId, sportId);
    }

    @Override
    public StandardMatchInfo getItemByPlsStandardMatchId(Long plsStandardMatchId) {
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andPlsStandardMatchIdEqualTo(plsStandardMatchId);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(standardMatchInfos)) {
            return standardMatchInfos.get(0);
        }
        return null;
    }
}
