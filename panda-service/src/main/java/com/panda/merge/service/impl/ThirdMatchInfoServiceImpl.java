package com.panda.merge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.EntityEqualsUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.ThirdMatchInfoDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.THREE;
import static com.panda.merge.constant.ConstantSystem.TWO;

/**
 * <Description> 三方赛事信息
 *
 * @author tell
 * @since 2020年9月10日10:35:50
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdMatchInfoServiceImpl extends BaseServiceImpl<ThirdMatchInfo> implements ThirdMatchInfoService {

    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Autowired
    private ThirdMatchInfoDao thirdMatchInfoDao;

    @Resource
    private RedisHelper redisHelper;

    @Override
    public Page<ThirdMatchInfoDetail> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page) {
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdMatchInfoDao.getItemPageByModifyTime(page.getData());
    }

    /*********************三方赛事接入MQ专用开始（和其它地方处理方式不一样）**************************/
    @Override
    @Cacheable(key = "'ThirdMatchInfoDetail:' + #dataSourceCode +  '-' + #thirdMatchSourceId", unless = "#result == null ")
    public ThirdMatchInfoDetail getItemDetail(String dataSourceCode, String thirdMatchSourceId) {
        ThirdMatchInfoDTO item = new ThirdMatchInfoDTO();
        item.setDataSourceCode(dataSourceCode);
        item.setThirdMatchSourceId(thirdMatchSourceId);
        return thirdMatchInfoDao.getItemByThirdMatchSourceId(item);
    }


    @Override
//    @CachePut(key = "'ThirdMatchInfoDetail:' + #upItem.dataSourceCode +  '-' + #upItem.thirdMatchSourceId",unless="#result == null")
    public ThirdMatchInfoDetail saveOrupdate(ThirdMatchInfoDetail upItem,String linkId) {
//        try {
//            Long.valueOf(upItem.getMatchPeriod());
//        } catch (Exception e) {
//            log.info("【linkId="+linkId+",源赛事ID={}】,三方赛事阶段转换异常,原始值:{},转换为'0'", upItem.getThirdMatchSourceId(), upItem.getMatchPeriod());
//            upItem.setMatchPeriod("0");
//        }
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "::lock:ThirdMatchInfo:" + upItem.getDataSourceCode()+ upItem.getSportId()+ upItem.getThirdMatchSourceId();
        boolean flag = false;
        try {
            flag = redisService.tryLock(redisKey,redisKey,2,2);
            /** 根据创建时间来区分新增或修改（创建时间不为空是为新增）*/
            if (null != upItem.getCreateTime()) {
                upItem.setModifyTime(upItem.getCreateTime());
                thirdMatchInfoMapper.insertSelective(upItem);
            } else {
                ThirdMatchInfo item = updateByequalsThirdMatchInfo(upItem,linkId);
                //忽略空值拷贝
                BeanUtil.copyProperties(item, upItem, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                return refreshCacheDetail(upItem);
            }
        }finally {
            if(flag){
                redisService.unLock(redisKey,redisKey);
            }
        }
        return upItem;
    }

    /**
     * 刷新缓存
     */
    @Override
    public ThirdMatchInfoDetail refreshCacheDetail(ThirdMatchInfoDetail item) {
        if (null != item) {
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfoDetail:" + item.getDataSourceCode() + '-' + item.getThirdMatchSourceId(), item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }

    /*********************三方赛事接入MQ专用结束（和其它地方处理方式不一样）**************************/

    @Override
    @Cacheable(key = "'ThirdMatchInfo:' + #dataSourceCode +  '-' + #thirdMatchSourceId", unless = "#result == null ")
    public ThirdMatchInfo getItem(String dataSourceCode, String thirdMatchSourceId) {
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExampleWithBLOBs(example);
        if (CollectionUtils.isEmpty(thirdMatchInfos)) {
            return null;
        }
        return refreshCache(thirdMatchInfos.get(0));
    }

    @Override
    public List<ThirdMatchInfo> getItemsByMarketDTO(List<OddsWrapper<ThirdMatchMarketDTO>> marketDto) {
        if(CollectionUtils.isEmpty(marketDto)) {
            return Collections.emptyList();
        }
        List<ThirdMatchInfo> result = new ArrayList<>();
        List<OddsWrapper<ThirdMatchMarketDTO>> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = marketDto.stream().map(t-> RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + t.getDataSourceCode() +  '-' + t.getData().getThirdMatchSourceId()).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(marketDto, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }

        // Obtaining remained data from mysql
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        for(OddsWrapper<ThirdMatchMarketDTO> match : requiredCallItems) {
            example.or().andDataSourceCodeEqualTo(match.getDataSourceCode()).andThirdMatchSourceIdEqualTo(match.getThirdMatchSourceId());
        }
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(example);
        Map<String, ThirdMatchInfo> thirdMatchInfosMap = thirdMatchInfos.stream().collect(Collectors.toMap(t->
                t.getDataSourceCode()+"-"+t.getThirdMatchSourceId(), Function.identity(), (v1, v2)->v1));
        List<ThirdMatchInfo> filteredThirdMatchInfos = thirdMatchInfosMap.values().stream().collect(Collectors.toList());
        result.addAll(filteredThirdMatchInfos);

        // Storing the remained data into redis
        Map<String, Object> redisVal = filteredThirdMatchInfos.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + t.getDataSourceCode() +  '-' + t.getThirdMatchSourceId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    public List<ThirdMatchInfo> getItemsByStandardIdAndDataSourceCode(List<Long> standardMatchIds, String dataSourceCode) {
        if (CollectionUtils.isEmpty(standardMatchIds)) {
            return Collections.emptyList();
        }
        List<ThirdMatchInfo> result = new ArrayList<>();
        List<Long> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = standardMatchIds.stream().map(t -> RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + t + "-" + dataSourceCode).collect(Collectors.toList());
        List<Object> objectList = redisService.mGet(keys);
        redisHelper.postProcMget(standardMatchIds, objectList, result, requiredCallItems);
        if (CollectionUtils.isEmpty(requiredCallItems)) {
            return result;
        }

        // Obtaining remained data from mysql
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andReferenceIdIn(requiredCallItems).andDataSourceCodeEqualTo(dataSourceCode);

        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExampleWithBLOBs(example);
        if(CollectionUtils.isEmpty(thirdMatchInfos)){
            return result;
        }
        result.addAll(thirdMatchInfos);

        // Storing the remained data into redis
        Map<String, Object> redisVal = thirdMatchInfos.stream().collect(Collectors.toMap(t ->
                RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + t.getReferenceId() +  '-' + t.getDataSourceCode(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    @Cacheable(key = "'ThirdMatchInfo:' + #referenceId +  '-' + #dataSourceCode", unless = "#result == null ")
    public ThirdMatchInfo getItem(Long referenceId, String dataSourceCode) {
        return getItemNoCache(referenceId,dataSourceCode);
    }

    @Override
    public ThirdMatchInfo getItemNoCache(Long referenceId, String dataSourceCode) {
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andReferenceIdEqualTo(referenceId).andDataSourceCodeEqualTo(dataSourceCode);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExampleWithBLOBs(example);
        if (CollectionUtils.isEmpty(thirdMatchInfos)) {
            return null;
        }
        return refreshCache(thirdMatchInfos.get(0));
    }

    @Override
    public ThirdMatchInfo getItemByThirdMatchSourceId(String thirdMatchSourceId) {
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId) ;
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExampleWithBLOBs(example);
        if (CollectionUtils.isEmpty(thirdMatchInfos)) {
            return null;
        }
        return refreshCache(thirdMatchInfos.get(0));
    }

    @Override
    public List<ThirdMatchInfo> getItems(List<Long> referenceIds, String dataSourceCode) {
        if (CollectionUtils.isEmpty(referenceIds)) {
            return new LinkedList<>();
        }
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        if (StringUtils.isNotBlank(dataSourceCode)) {
            example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andReferenceIdIn(referenceIds);
        } else {
            example.createCriteria().andReferenceIdIn(referenceIds);
        }
        return thirdMatchInfoMapper.selectByExample(example);
    }

    @Override
    public List<ThirdMatchInfo> getItemsByThirdMatchSourceIds(List<String> dataSourceCodes, List<String> thirdMatchSourceIds) {
        if(CollectionUtils.isEmpty(dataSourceCodes) || CollectionUtils.isEmpty(thirdMatchSourceIds)){
            return new LinkedList<>();
        }
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andDataSourceCodeIn(dataSourceCodes).andThirdMatchSourceIdIn(thirdMatchSourceIds);
        return thirdMatchInfoMapper.selectByExample(example);
    }

    @Override
    public List<ThirdMatchInfo> getItems(Long referenceId) {
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andReferenceIdEqualTo(referenceId);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExampleWithBLOBs(example);
        if (CollectionUtils.isEmpty(thirdMatchInfos)) {
            return null;
        }
        return thirdMatchInfos;
    }

    @Override
    @Cacheable(key = "'ThirdMatchInfo:' + #id", unless = "#result == null ")
    public ThirdMatchInfo getItem(Long id) {
        return thirdMatchInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ThirdMatchInfo> getItems(List<Long> ids) {
        List<ThirdMatchInfo> result = new ArrayList<>();
        List<Long> requiredCallItems = new ArrayList<>();

        // Obtaining data from redis
        List<String> keys = ids.stream().map(t->RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + t).collect(Collectors.toList());
        List<Object> objectList= redisService.mGet(keys);
        redisHelper.postProcMget(ids, objectList, result, requiredCallItems);
        if(CollectionUtils.isEmpty(requiredCallItems)){
            return result;
        }

        // Obtaining remained data from mysql
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andIdIn(requiredCallItems);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExampleWithBLOBs(example);
        result.addAll(thirdMatchInfos);

        // Storing the remained data into redis
        Map<String, Object> redisVal = thirdMatchInfos.stream().collect(Collectors.toMap(t->
                RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + t.getId(), Function.identity(), (v1, v2) -> v1));
        redisService.mSet(redisVal);
        return result;
    }

    @Override
    public ThirdMatchInfo getItemByPrimaryKey(Long id) {
        return refreshCache(thirdMatchInfoMapper.selectByPrimaryKey(id));
    }

    @Override
    public ThirdMatchInfo updateByPrimaryKeySelective(ThirdMatchInfo item,String linkId) {
        return updateByequalsThirdMatchInfo(item,linkId);
    }

    @Override
    public void processThirdMatchOver(Long referenceId) {
        //当前标准赛事关联的三方赛事
        List<ThirdMatchInfo> thirdMatchInfoList = getItems(referenceId);
        if (!CollectionUtils.isEmpty(thirdMatchInfoList)) {
            long time = System.currentTimeMillis();
            //完赛的三方赛事列表
            List<ThirdMatchInfo> overThirdMatchInfoList = thirdMatchInfoList.stream().filter(obj -> obj.getBeginTime() < time && YesNoEnum.Y.value.equals(obj.getMatchOver())).collect(Collectors.toList());
            //处理未完赛的数据
            if (overThirdMatchInfoList.size() >= THREE) {
                thirdMatchInfoList.forEach(thirdMatchInfo -> {
                    if (thirdMatchInfo.getMatchOver().equals(YesNoEnum.N.value) && thirdMatchInfo.getBeginTime() < time) {
                        ThirdMatchInfo upThirdMatchInfo = new ThirdMatchInfo();
                        upThirdMatchInfo.setId(thirdMatchInfo.getId());
                        upThirdMatchInfo.setMatchOver(YesNoEnum.Y.value);
                        updateByPrimaryKeySelective(upThirdMatchInfo,"processThirdMatchOver");
                    }
                });
            }
        }
    }

    /**
     * 刷新缓存
     */
    @Override
    public ThirdMatchInfo refreshCache(ThirdMatchInfo item) {
        if (null != item) {
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + item.getId(), item, RedisConfig.REDIS_MY_TIME);
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + item.getDataSourceCode() + '-' + item.getThirdMatchSourceId(), item, RedisConfig.REDIS_MY_TIME);
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + item.getReferenceId() + '-' + item.getDataSourceCode(), item, RedisConfig.REDIS_MY_TIME);
//            redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfoDetail:" + item.getDataSourceCode() + '-' + item.getThirdMatchSourceId());
        }
        return item;
    }

    @Override
    @Cacheable(key = "'ThirdMatchInfo:' + #businessEvent+ #standardMatchId", unless = "#result == null ")
    public List<ThirdMatchInfo> getThirdMatchInfoForSettle(Long standardMatchId, String businessEvent) {
        ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(standardMatchId).andDataSourceCodeEqualTo(businessEvent);
        List<ThirdMatchInfo> list = thirdMatchInfoMapper .selectByExample(thirdMatchInfoExample);
        return list;
    }

    /**
     * 获取缓存
     */
    private ThirdMatchInfo getCache(Long id) {
        Object itemObj = redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + id);
        ThirdMatchInfo oldItem;
        if (null != itemObj) {
            oldItem = JSON.parseObject(JSON.toJSONString(itemObj), ThirdMatchInfo.class);
        } else {
            oldItem = getItemByPrimaryKey(id);
        }
        return oldItem;
    }

    /**
     * 比较需要修改的三方赛事和数据库中赛事是否存在差异，存在则修改
     */
    private ThirdMatchInfo updateByequalsThirdMatchInfo(ThirdMatchInfo upItem,String linkId) {
        //获取库中三方赛事
        ThirdMatchInfo oldItem = getCache(upItem.getId());
        if (null != oldItem) {
            String redisKey = RedisConfig.REDIS_KEY_DATABASE + "::lock:ThirdMatchInfo:" + oldItem.getDataSourceCode()+ oldItem.getSportId()+ oldItem.getThirdMatchSourceId();
            boolean flag = false;
            try {
                flag = redisService.tryLock(redisKey,redisKey,5,5);
                upItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                //需要修改的三方赛事比较类
                ThirdMatchInfo checkUpItem = new ThirdMatchInfo();
                BeanUtil.copyProperties(oldItem, checkUpItem);
                //忽略空值拷贝
                BeanUtil.copyProperties(upItem, checkUpItem, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                //比较如果本次修改数据和库中数据不一致，则需要修改
                if (!EntityEqualsUtils.equalsIsObjToString(checkUpItem, oldItem)) {
                    ThirdMatchInfo item = new ThirdMatchInfo();
                    //数据来源，三方数据源赛事ID,创建时间无需修改（运动类型调整为可以更新）
                    BeanUtil.copyProperties(upItem, item, "dataSourceCode", "thirdMatchSourceId", "createTime");
                    int num = thirdMatchInfoMapper.updateByPrimaryKeySelective(item);
                    if (num > 0) {
                        //刷新缓存
                        refreshCache(checkUpItem);
                        log.info("【linkId="+linkId+"】updateByequalsThirdMatchInfo,修改三方赛事数据成功！数据源赛事ID={},数据源编码={},赛事状态={}",oldItem.getThirdMatchSourceId(),oldItem.getDataSourceCode(),item.getMatchStatus());
                    }else{
                        log.info("【linkId="+linkId+"】updateByequalsThirdMatchInfo,修改三方赛事数据失败！数据源赛事ID={},数据源编码={}",oldItem.getThirdMatchSourceId(),oldItem.getDataSourceCode());
                    }
                }else{
                    log.info("【linkId="+linkId+"】updateByequalsThirdMatchInfo,修改三方赛事数据,数据一致无需修改！数据源赛事ID={},数据源编码={}",oldItem.getThirdMatchSourceId(),oldItem.getDataSourceCode());
                }
                return checkUpItem;
            } finally {
                if(flag){
                    redisService.unLock(redisKey,redisKey);
                }
            }
        }
        return upItem;
    }


}
