package com.panda.merge.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dao.ThirdSportMarketDao;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdSportMarketDTO;
import com.panda.merge.mapper.StandardSportMarketMapper;
import com.panda.merge.mapper.ThirdSportMarketMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.I18nOutrightMarketService;
import com.panda.merge.service.SportMarketRelationService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/15 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ThirdSportMarketServiceImpl implements ThirdSportMarketService {

    @Autowired
    private I18nOutrightMarketService i18nOutrightMarketService;
    @Autowired
    private ThirdSportMarketMapper thirdSportMarketMapper;
    @Autowired
    private ThirdSportMarketDao thirdSportMarketDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SportMarketRelationService sportMarketRelationService;
    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Override
    public ThirdSportMarket getItem(String thirdMarketSourceId) {
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andThirdMarketSourceIdEqualTo(thirdMarketSourceId);
        List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
        if(CollectionUtils.isEmpty(thirdSportMarkets)){
            return null;
        }
        return thirdSportMarkets.get(0);
    }

    @Override
    @Cacheable(key = "'ThirdSportMarket:' + #thirdMatchInfoId+  '-' + #thirdMarketSourceId",unless = "#result == null ")
    public ThirdSportMarket getItem(String dataSourceCode, String thirdMarketSourceId, Long thirdMatchInfoId) {
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode)
                .andThirdMarketSourceIdEqualTo(thirdMarketSourceId)
                .andMatchIdEqualTo(thirdMatchInfoId);
        List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
        if(CollectionUtils.isEmpty(thirdSportMarkets)){
            return null;
        }
        return thirdSportMarkets.get(0);
    }

    /**
     * @param thirdMatchSourceId
     * @param marketType
     * @param marketCategoryIds
     * @return
     */
    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchSourceId, String dataSourceWeight, Integer marketType, List<Long> marketCategoryIds) {
        if(CollectionUtils.isEmpty(marketCategoryIds)){
            return new LinkedList<>();
        }
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchSourceId).andDataSourceCodeEqualTo(dataSourceWeight)
                .andMarketTypeEqualTo(marketType).andMarketCategoryIdIn(marketCategoryIds);
        return thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
    }

    /**
     * 开售时只开售开跟封的盘口
     * @param thirdMatchSourceId
     * @param dataSourceWeight
     * @param marketType
     * @param marketCategoryIds
     * @param status
     * @return
     */
    @Override
    public List<ThirdSportMarket> getItemListByStatus(Long thirdMatchSourceId, String dataSourceWeight, Integer marketType, List<Long> marketCategoryIds,Integer status) {
        if(CollectionUtils.isEmpty(marketCategoryIds)){
            return new LinkedList<>();
        }
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchSourceId).andDataSourceCodeEqualTo(dataSourceWeight)
                .andStatusLessThan(status)
                .andMarketTypeEqualTo(marketType).andMarketCategoryIdIn(marketCategoryIds);
        return thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
    }

    @Override
    public List<ThirdSportMarket> getItem(Long thirdMatchId, String dataSourceCode, Long categoryId, String addtion1) {
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchId).andDataSourceCodeEqualTo(dataSourceCode).andMarketCategoryIdEqualTo(categoryId)
                .andAddition1EqualTo(addtion1).andStatusNotEqualTo(2);
        List<ThirdSportMarket> sportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
        if (CollectionUtils.isEmpty(sportMarkets)) {
            return null;
        }
        return sportMarkets;
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchId, String dataSourceCode, Long marketCategoryId) {
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchId).andDataSourceCodeEqualTo(dataSourceCode).andMarketCategoryIdEqualTo(marketCategoryId);
        List<ThirdSportMarket> sportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
        if (CollectionUtils.isEmpty(sportMarkets)) {
            return null;
        }
        return sportMarkets;
    }

    @Override
    public Long getRelationMarketId(String linkId, Long standardMatchId, Long categoryId, String addition1, String addition2, String addition3, String addition4,String addition5, Integer marketType, String thirdMarketSourceId) {
        String redisKey = RelationKeyFactory.getMarketRelationKeyByThirdInfo(linkId,standardMatchId,categoryId,addition1,addition2,
                                                                            addition3,addition4,addition5,marketType,thirdMarketSourceId);
        Long relationMarketId;
        Object obj = redisService.get(redisKey);
        if (obj == null || StringUtils.isEmpty(obj.toString())) {
            relationMarketId = MD5Utils.getLongByMD5(redisKey);//UUIdUtils.getId();
        } else {
            relationMarketId = Long.valueOf(obj.toString());
        }
        return relationMarketId;
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchId, String dataSourceCode, Long marketCategoryId, Integer marketType) {
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchId).andDataSourceCodeEqualTo(dataSourceCode).
                andMarketCategoryIdEqualTo(marketCategoryId).andMarketTypeEqualTo(marketType);
        List<ThirdSportMarket> sportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
        if (CollectionUtils.isEmpty(sportMarkets)) {
            return null;
        }
        return sportMarkets;
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchSourceId) {
        if(thirdMatchSourceId == null){
            return null;
        }
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchSourceId);
        return thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long matchId, int marketType) {
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdEqualTo(matchId).andMarketTypeEqualTo(marketType);
        return thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
    }

    @Override
    public List<ThirdSportMarket> getItemList(Long thirdMatchId, Long standardMatchId) {
        Map<String, Long> map = new HashMap();
        map.put("thirdMatchId", thirdMatchId);
        //map.put("standardMatchId", standardMatchId);
        List<ThirdSportMarket> activeMarketList = Lists.newArrayList();
        List<ThirdSportMarket> queryMarketList = thirdSportMarketDao.selectThirdSportMarketList(map);
        if (!CollectionUtils.isEmpty(queryMarketList)) {
            List<String> thirdSourceIdList = queryMarketList.stream().filter( tsm -> !StringUtils.isEmpty(tsm.getThirdMarketSourceId()))
                    .map(ThirdSportMarket::getThirdMarketSourceId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(thirdSourceIdList)) {

                StandardSportMarketExample standardSportMarketExample = new StandardSportMarketExample();
                standardSportMarketExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId).andThirdMarketSourceIdIn(thirdSourceIdList);
                List<StandardSportMarket> filterMarketList = standardSportMarketMapper.selectByExample(standardSportMarketExample);

                if (CollectionUtils.isEmpty(filterMarketList)) {
                    activeMarketList.addAll(queryMarketList);
                } else {
                    Set<String> filterSourceIdSet = filterMarketList.stream().map(StandardSportMarket::getThirdMarketSourceId).collect(Collectors.toSet());
                    for (ThirdSportMarket thirdSportMarket : queryMarketList ) {
                        String thirdMarketSourceId = thirdSportMarket.getThirdMarketSourceId();
                        if ( !filterSourceIdSet.contains(thirdMarketSourceId) ) {
                            activeMarketList.add(thirdSportMarket);
                        }
                    }
                }
            }
        }
        return activeMarketList;
    }

    /**
     * 创建三方盘口
     * @param linkId
     * @param thirdMarketDTO
     * @param thirdMatchInfoId
     * @param standardSportMarketCategory
     * @return
     */
    @CachePut(key = "'ThirdSportMarket:' + #thirdMatchInfoId+  '-' + #thirdMarketDTO.thirdMarketSourceId",unless = "#result == null ")
    @Override
    public ThirdSportMarket create(String linkId, ThirdMarketDTO thirdMarketDTO, Long thirdMatchInfoId, StandardSportMarketCategory standardSportMarketCategory) {
        ThirdSportMarket thirdSportMarket = new ThirdSportMarket();
        BeanUtils.copyProperties(thirdMarketDTO, thirdSportMarket);
        thirdSportMarket.setMatchId(thirdMatchInfoId);
        thirdSportMarket.setMarketCategoryId(standardSportMarketCategory.getMarketCategoryId());
        thirdSportMarket.setId(UUIdUtils.getId());
        thirdSportMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdSportMarket.setScopeId(standardSportMarketCategory.getScopeId());
        thirdSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
        //tx的创建时间必须严格使用上游的时间
        if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode())) {
            thirdSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
        }
        thirdSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
        thirdSportMarket.setNameCode(UUIdUtils.getId());
        thirdSportMarket.setOfferLineId(thirdMarketDTO.getOfferLineId());
        MergeFunctionUtils.setNumberOfWinners( thirdSportMarket, thirdMarketDTO.getNumberOfWinners());
        thirdSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
        thirdSportMarket.setEventType(thirdMarketDTO.getEventType());
        try {
            //发送mq
            //marketDbProducer.sendThirdMarketInsertInfo(linkId, Arrays.asList(thirdSportMarket));
            thirdSportMarketMapper.insertSelective(thirdSportMarket);
            //三方冠军盘口名称多语言处理
            if (thirdMarketDTO.getMarketType() == 2 && !CollectionUtils.isEmpty(thirdMarketDTO.getI18nNames())) {
                List<I18nOutrightMarket> i18nOutrightMarketList = new ArrayList<>();
                thirdMarketDTO.getI18nNames().forEach(i18nItemDTO -> {
                    I18nOutrightMarket i18nOutrightMarket = new I18nOutrightMarket();
                    BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarket);
                    i18nOutrightMarket.setNameCode(thirdSportMarket.getNameCode());
                    i18nOutrightMarket.setDataSourceCode(thirdSportMarket.getDataSourceCode());
                    i18nOutrightMarketList.add(i18nOutrightMarket);
                });
                i18nOutrightMarketService.saveBatch(i18nOutrightMarketList);
            }
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口依然需要投递给下游
            log.info("::{}::insert三方盘口唯一约束冲突，尝试重新入库,matchId:{},三方盘口ID:{}", linkId,
                    thirdSportMarket.getMatchId(), thirdSportMarket.getThirdMarketSourceId());
            //createReplenish(linkId,thirdSportMarket);
        }
        return thirdSportMarket;
    }

    /**
     * 创建失败后第二次尝试
     */
    @Override
    @CachePut(key = "'ThirdSportMarket:' + #thirdSportMarket.matchInfoId+  '-' + #thirdSportMarket.thirdMarketSourceId",unless = "#result == null ")
    public ThirdSportMarket createReplenish(String linkId,ThirdSportMarket thirdSportMarket) {
        try {
            ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
            thirdSportMarketExample.createCriteria().andThirdMarketSourceIdEqualTo(thirdSportMarket.getThirdMarketSourceId())
                    .andDataSourceCodeEqualTo(thirdSportMarket.getDataSourceCode());
            List<ThirdSportMarket> list = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
            if(CollectionUtils.isEmpty(list)) {
                //重新入库
                thirdSportMarket.setId(UUIdUtils.getId());
                thirdSportMarketMapper.insert(thirdSportMarket);
            }else {
                int count = updateByMarketIdAndThirdMarketSourceId(linkId,thirdSportMarket);
                if(count > 0) {
                    log.info("::{}:: update三方盘口信息 成功，matchId:{},三方盘口ID:{}",
                            linkId, thirdSportMarket.getMatchId(), thirdSportMarket.getThirdMarketSourceId());
                }else {
                    log.info("::{}:: update三方盘口信息 发现此数据已经属于旧数据，matchId:{},三方盘口ID:{}",
                            linkId, thirdSportMarket.getMatchId(), thirdSportMarket.getThirdMarketSourceId());
                    return list.get(0);
                }
            }
        } catch (DuplicateKeyException d) {
            log.info("::{}:: insert 三方盘口失败2，matchId:{},三方盘口ID:{}，error",
                    linkId, thirdSportMarket.getMatchId(), thirdSportMarket.getThirdMarketSourceId(), d);
        }
        return thirdSportMarket;
    }



    public int updateByMarketIdAndThirdMarketSourceId(String linkId, ThirdSportMarket thirdSportMarket){
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andThirdMarketSourceIdEqualTo(thirdSportMarket.getThirdMarketSourceId())
                .andDataSourceCodeEqualTo(thirdSportMarket.getDataSourceCode());
        int count = thirdSportMarketMapper.updateByExampleSelective(thirdSportMarket, thirdSportMarketExample);
        return count;
    }

    @Override
    @CachePut(key = "'ThirdSportMarket:' + #thirdSportMarket.matchId+ '-' + #thirdSportMarket.thirdMarketSourceId")
    //@Async("ThirdSportMarketThreadPool")
    public ThirdSportMarket updateByPrimaryKeySelective(ThirdSportMarket thirdSportMarket) {
        //发送mq
        //marketDbProducer.sendThirdMarketUpdateInfo("", Arrays.asList(thirdSportMarket));
        thirdSportMarketMapper.updateByPrimaryKeySelective(thirdSportMarket);
        return thirdSportMarket;
    }

    @Override
    public int updateByExampleSelective(Integer status, String dataSource, Long thirdMatchInfoId,List<Integer> statusList,List<Integer> marketTypeList) {
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andDataSourceCodeEqualTo(dataSource)
                .andMatchIdEqualTo(thirdMatchInfoId)
                .andStatusIn(statusList)
                .andMarketTypeIn(marketTypeList);
         //清空三方盘口的缓存
         List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
         if(CollectionUtils.isEmpty(thirdSportMarkets)){
             return 0;
         }
         List keyList = new ArrayList();
         thirdSportMarkets.forEach(thirdSportMarket -> {
             String key = RedisConfig.REDIS_KEY_DATABASE +"::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId();
             keyList.add(key);
         });
         redisService.del(keyList);
         //更新盘口
         ThirdSportMarket thirdSportMarketNew = new ThirdSportMarket();
         thirdSportMarketNew.setStatus(status);
         thirdSportMarketNew.setThirdMarketSourceStatus(status);
         thirdSportMarketNew.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
         return thirdSportMarketMapper.updateByExampleSelective(thirdSportMarketNew, thirdSportMarketExample);
     }


    @Override
    public Page<ThirdSportMarket> getItemPageByModifyTime(PageModel<ThirdSportMarketDTO> page){
        PageHelper.startPage(page.getCurrent(), page.getSize());
        return thirdSportMarketDao.getItemPageByModifyTime(page.getData());
    }




}
