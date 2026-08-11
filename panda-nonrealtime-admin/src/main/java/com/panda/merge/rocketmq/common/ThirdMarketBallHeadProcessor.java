package com.panda.merge.rocketmq.common;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CategoryOppositeConfig;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.ThirdSportMarketMergeProducer;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * 下发三方球头A01
 */
@Slf4j
@Component
public class ThirdMarketBallHeadProcessor extends BaseProcessor {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private ThirdSportMarketMergeProducer standardMarketOddsProducer;
    @Autowired
    private RedisService redisService;

    /**
     * 下发三方赔率球头给AO
     * 下发标准赛事挂载的三方数据源球头
     * 1.数据源维度：缓存数据商开盘盘口 全场让球、全场大小、上半场让球、上半场大小、格式：Map<数据源,三方球头盘口>
     * 2.计算盘口投注项赔率绝对值，以数据商状态、赔率差 升序排序 获取差值最小的主盘
     * 3.下发AO服务topic
     */
    public void thirdMarketBallHead(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOs, Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap
            , Map<Long, StandardMatchInfo> standardMatchInfoBasedIdMap) {
        List<ThirdMatchInfo> thirdMatchInfoAO = thirdMatchInfoService.getItemsByStandardIdAndDataSourceCode(new ArrayList<>(standardMatchInfoBasedIdMap.keySet()), DataSourceCodeEnum.AO.code);
        //map<标准赛事ID,A01赛事>
        Map<Long, ThirdMatchInfo> thirdMatchInfoAOMap = new ConcurrentHashMap<>();
        if (!CollectionUtils.isEmpty(thirdMatchInfoAO)) {
            thirdMatchInfoAOMap = thirdMatchInfoAO.stream().collect(Collectors.toMap(ThirdMatchInfo::getReferenceId, thi -> thi, (oldValue, newValue) -> newValue));
        }
        Map<Long, ThirdMatchInfo> finalThirdMatchInfoAOMap = thirdMatchInfoAOMap;
        thirdMarketDTOs = thirdMarketDTOs.stream().filter(t -> {
            if (t.getStandardSourceId() == null
                    || standardMatchInfoBasedIdMap.get(t.getStandardSourceId()) == null
                    || finalThirdMatchInfoAOMap.get(t.getStandardSourceId()) == null
                    || thirdMatchInfoBasedIdMap.get(Long.valueOf(t.getThirdMatchSourceId())) == null
                    || !StandardSportTypeEnum.FootBall.code.equals(standardMatchInfoBasedIdMap.get(t.getStandardSourceId()).getSportId())) {
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
        //只下发需要处理玩法盘口
        List<OddsWrapper<ThirdMarketDTO>> thirdMarketList = thirdMarketDTOs.stream().filter(m -> MarginCategoryConfig.BALL_HEAD_AO_CATEGORY.contains(m.getMarketCategoryId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdMarketList)) {
            return;
        }
        //获取缓存最新球头盘口 Map<dataSourceCode,Map<标准玩法ID，球头盘口数据>>
        List<String> keys = thirdMarketList.stream().map(t -> Constant.REDIS_KEY.THIRD_ALL_MARKET_HEAD + t.getStandardSourceId() + "_" + t.getDataSourceCode()).collect(Collectors.toList());
        Map<String, Object> thirdMarketHeadCacheMap = redisService.syncObtainMultiGetAll(keys);

        Map<String, List<OddsWrapper<ThirdMarketDTO>>> marketMapBasedLinkId = thirdMarketList.stream().collect(Collectors.groupingBy(t -> t.getLinkId()));
        for (Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entryBasedLinkId : marketMapBasedLinkId.entrySet()) {
            String linkId = entryBasedLinkId.getKey();
            List<OddsWrapper<ThirdMarketDTO>> oddsWrapperList = entryBasedLinkId.getValue();
            Map<Long, List<OddsWrapper<ThirdMarketDTO>>> thirdMarketsDataMap = oddsWrapperList.stream().collect(Collectors.groupingBy(t -> t.getMarketCategoryId()));
            //入缓存
            Map<String, ThirdMarketDTO> thirdMarketHeadCacheNewMap = new ConcurrentHashMap<>();
            //下发下游
            Map<Long, ThirdMarketDTO> sendThirdMarketHeadCacheNewMap = new ConcurrentHashMap<>();
            String dataSourceCode = oddsWrapperList.get(0).getDataSourceCode();
            String prefixKey = Constant.REDIS_KEY.THIRD_ALL_MARKET_HEAD + oddsWrapperList.get(0).getStandardSourceId() + "_" + dataSourceCode;
            StandardMatchInfo standardMatchInfo = standardMatchInfoBasedIdMap.get(oddsWrapperList.get(0).getStandardSourceId());
            ThirdMatchInfo thirdMatchInfo = finalThirdMatchInfoAOMap.get(oddsWrapperList.get(0).getStandardSourceId());
            Long dataSourceTime = oddsWrapperList.get(0).getDataSourceTime();
            for (Map.Entry<Long, List<OddsWrapper<ThirdMarketDTO>>> entry : thirdMarketsDataMap.entrySet()) {
                Long marketCategoryId = entry.getKey();
                List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOList = entry.getValue();
                List<ThirdMarketDTO> thirdMarketDTOArrayList = Collections.synchronizedList(new ArrayList());
                Boolean isTrue = Boolean.FALSE;
                Map<String, OddsWrapper<ThirdMarketDTO>> thirdMarketDTOMap = thirdMarketDTOList.stream().collect(Collectors.toMap(thi -> thi.getData().getThirdMarketSourceId(), thi -> thi, (oldValue, newValue) -> newValue));
                for (OddsWrapper<ThirdMarketDTO> thirdMarketDTO : thirdMarketDTOList) {
                    //AO、TX（足球）只取坑位1开盘盘口玩法，TX(其他球种需先取每个坑位最新的盘口，再排序，这期不做)
                    String key = prefixKey + "-" + marketCategoryId;
                    ThirdMarketDTO thirdMarketHeadCache = (ThirdMarketDTO) thirdMarketHeadCacheMap.get(key);
                    if (thirdMarketDTO.getDataSourceCode().contains("TX")) {
                        if (thirdMarketDTO.getData().getOfferLineId() == 1L) {
                            if (null != thirdMarketHeadCache) {
                                if (thirdMarketHeadCache.getThirdMarketSourceId().equals(thirdMarketDTO.getData().getThirdMarketSourceId())) {
                                    //中场休息 忽略上半场关盘 ，其他阶段正常关盘
                                    if (standardMatchInfo.getMatchPeriodId() == 31 &&
                                            (18L == marketCategoryId || 19L == marketCategoryId)) {
                                        thirdMarketDTOArrayList.add(thirdMarketHeadCache);
                                    } else {
                                        thirdMarketDTOArrayList.add(thirdMarketDTO.getData());
                                    }
                                } else {
                                    thirdMarketDTOArrayList.add(thirdMarketDTO.getData());
                                }
                            } else {
                                thirdMarketDTOArrayList.add(thirdMarketDTO.getData());
                            }
                        }
                    } else {

                        if (null != thirdMarketHeadCache) {
                            if (thirdMarketHeadCache.getThirdMarketSourceId().equals(thirdMarketDTO.getData().getThirdMarketSourceId())) {
                                //中场休息 忽略上半场关盘 ，其他阶段正常关盘
                                if (standardMatchInfo.getMatchPeriodId() == 31 && (18L == marketCategoryId || 19L == marketCategoryId)) {
                                    thirdMarketDTOArrayList.add(thirdMarketHeadCache);
                                } else {
                                    if (!Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarketDTO.getData().getStatus())) {
                                        thirdMarketHeadCache.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                                        thirdMarketDTOArrayList.add(thirdMarketHeadCache);
                                    } else {
                                        thirdMarketDTOArrayList.add(thirdMarketDTO.getData());
                                    }
                                }
                            } else {
                                if (!isTrue && null == thirdMarketDTOMap.get(thirdMarketHeadCache.getThirdMarketSourceId())) {
                                    thirdMarketDTOArrayList.add(thirdMarketHeadCache);
                                    isTrue = Boolean.TRUE;
                                }
                                thirdMarketDTOArrayList.add(thirdMarketDTO.getData());
                            }
                        } else if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarketDTO.getData().getStatus())) {
                            thirdMarketDTOArrayList.add(thirdMarketDTO.getData());
                        }
                    }
                }
                if (CollectionUtils.isEmpty(thirdMarketDTOArrayList)) {
                    continue;
                }
                //计算出投注项赔率差
                thirdMarketDTOArrayList.forEach(m -> {
                    if (!CollectionUtils.isEmpty(m.getMarketOddsList())) {
                        m.setOddsMetric(m.getMarketOddsList().stream().map(ThirdMarketOddsDTO::getOriginalOddsValue).reduce(0, (a, b) -> a >= b ? a - b : b - a));
                    } else {
                        m.setOddsMetric(999999);
                    }
                });
                //数据商状态、赔率差 升序排序
                ListUtils.sort(thirdMarketDTOArrayList, true, "status", "oddsMetric");
                ThirdMarketDTO thirdMarketNewHeadFinal = thirdMarketDTOArrayList.get(0);
                log.info("::{}::百家赔下发三方赔率球头给AO,玩法ID:{}，缓存:{}", linkId, marketCategoryId, JSONObject.toJSONString(thirdMarketNewHeadFinal));
                String baseDataSourceCode = resolveBaseDataSourceCode(thirdMarketNewHeadFinal.getDataSourceCode());
                ThirdMatchInfo thirdMatchInfoHomeAwayOpposite = thirdMatchInfoService.getItem(standardMatchInfo.getId(), baseDataSourceCode);
                if (null != thirdMatchInfoHomeAwayOpposite) {
                    //主客队相反盘口、投注项相关内容处理
                    if (ONE.equals(thirdMatchInfoHomeAwayOpposite.getHomeAwayOpposite()) && CategoryOppositeConfig.FootBall.containsCategory(thirdMarketNewHeadFinal.getMarketCategoryId())) {
                        {
                            thirdMarketNewHeadFinal = JSONObject.parseObject(JSONObject.toJSONString(thirdMarketNewHeadFinal), ThirdMarketDTO.class);
                            ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(baseDataSourceCode, thirdMarketNewHeadFinal.getThirdMarketCategorySourceId());
                            if (null != thirdMarketCategory) {
                                changeStandardMarketContent(linkId, baseDataSourceCode, thirdMarketCategory, thirdMarketNewHeadFinal);
                                thirdMarketNewHeadFinal.setMarketCategoryId(thirdMarketCategory.getReferenceId());
                                log.info("::{}::百家赔下发三方赔率球头给AO,玩法ID:{}，主客队相反后：{}", linkId, marketCategoryId, JSONObject.toJSONString(thirdMarketNewHeadFinal));
                            }
                        }
                    }
                }
                thirdMarketHeadCacheNewMap.put(marketCategoryId.toString(), thirdMarketNewHeadFinal);
                sendThirdMarketHeadCacheNewMap.put(marketCategoryId, thirdMarketNewHeadFinal);
            }
            //重新缓存数据源球头
            log.info("::{}::百家赔下发三方赔率球头给AO,最终缓存：{}", linkId, JSONObject.toJSONString(thirdMarketHeadCacheNewMap));
            redisService.hSetAllSync(prefixKey, thirdMarketHeadCacheNewMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            standardMarketOddsProducer.sendThirdBallHeadMarketAoAsync(linkId, thirdMatchInfo, standardMatchInfo, sendThirdMarketHeadCacheNewMap, dataSourceCode, dataSourceTime);
        }
    }

    /**
     * 下发三方赔率球头给AO
     * 下发标准赛事挂载的三方数据源球头
     * 1.数据源维度：缓存数据商开盘盘口 全场让球、全场大小、上半场让球、上半场大小、格式：Map<数据源,三方球头盘口>
     * 2.计算盘口投注项赔率绝对值，以数据商状态、赔率差 升序排序 获取差值最小的主盘
     * 3.下发AO服务topic
     */
    public void thirdBasketballMarketBallHead(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOs, Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap, Map<Long, StandardMatchInfo> standardMatchInfoBasedIdMap) {
        List<ThirdMatchInfo> thirdMatchInfoAO = thirdMatchInfoService.getItemsByStandardIdAndDataSourceCode(new ArrayList<>(standardMatchInfoBasedIdMap.keySet()), DataSourceCodeEnum.AO.code);
        //map<标准赛事ID,A01赛事>
        Map<Long, ThirdMatchInfo> thirdMatchInfoAOMap = new ConcurrentHashMap<>();
        if (!CollectionUtils.isEmpty(thirdMatchInfoAO)) {
            thirdMatchInfoAOMap = thirdMatchInfoAO.stream().collect(Collectors.toMap(ThirdMatchInfo::getReferenceId, thi -> thi, (oldValue, newValue) -> newValue));
        }
        Map<Long, ThirdMatchInfo> finalThirdMatchInfoAOMap = thirdMatchInfoAOMap;
        thirdMarketDTOs = thirdMarketDTOs.stream().filter(t -> {
            if (t.getStandardSourceId() == null
                    || standardMatchInfoBasedIdMap.get(t.getStandardSourceId()) == null
                    || finalThirdMatchInfoAOMap.get(t.getStandardSourceId()) == null
                    || thirdMatchInfoBasedIdMap.get(Long.valueOf(t.getThirdMatchSourceId())) == null
                    || !StandardSportTypeEnum.Basketball.code.equals(standardMatchInfoBasedIdMap.get(t.getStandardSourceId()).getSportId())) {
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());

        //只下发需要处理玩法盘口
        List<OddsWrapper<ThirdMarketDTO>> thirdMarketList = thirdMarketDTOs.stream().filter(m -> MarginCategoryConfig.BASKETBALL_HEAD_AO_CATEGORY.contains(m.getMarketCategoryId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdMarketList)) {
            return;
        }
        //获取缓存最新球头盘口 Map<dataSourceCode,Map<标准玩法ID，球头盘口数据>>
        List<String> keys = thirdMarketList.stream().map(t -> Constant.REDIS_KEY.THIRD_ALL_BASKETBALL_MARKET_HEAD + t.getStandardSourceId() + "_" + t.getDataSourceCode()).collect(Collectors.toList());
        Map<String, Object> thirdMarketHeadCacheMap = redisService.syncObtainMultiGetAll(keys);

        Map<String, List<OddsWrapper<ThirdMarketDTO>>> marketMapBasedLinkId = thirdMarketList.stream().collect(Collectors.groupingBy(t -> t.getLinkId()));

        for (Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entryBasedLinkId : marketMapBasedLinkId.entrySet()) {
            String linkId = entryBasedLinkId.getKey();
            List<OddsWrapper<ThirdMarketDTO>> oddsWrapperList = entryBasedLinkId.getValue();
            Map<Long, List<OddsWrapper<ThirdMarketDTO>>> thirdMarketsDataMap = oddsWrapperList.stream().collect(Collectors.groupingBy(t -> t.getMarketCategoryId()));
            //入缓存
            Map<String, List<ThirdMarketDTO>> thirdMarketHeadCacheNewMap = new ConcurrentHashMap<>();
            //下发下游
            Map<Long, List<ThirdMarketDTO>> sendThirdMarketHeadCacheNewMap = new ConcurrentHashMap<>();
            String dataSourceCode = oddsWrapperList.get(0).getDataSourceCode();
            String prefixKey = Constant.REDIS_KEY.THIRD_ALL_BASKETBALL_MARKET_HEAD + oddsWrapperList.get(0).getStandardSourceId() + "_" + dataSourceCode;
            StandardMatchInfo standardMatchInfo = standardMatchInfoBasedIdMap.get(oddsWrapperList.get(0).getStandardSourceId());
            ThirdMatchInfo thirdMatchInfo = finalThirdMatchInfoAOMap.get(oddsWrapperList.get(0).getStandardSourceId());
            Long dataSourceTime = oddsWrapperList.get(0).getDataSourceTime();
            for (Map.Entry<Long, List<OddsWrapper<ThirdMarketDTO>>> entry : thirdMarketsDataMap.entrySet()) {
                Long marketCategoryId = entry.getKey();
                List<ThirdMarketDTO> thirdMarketNewHeadFinal = new ArrayList<>();
                List<ThirdMarketDTO> thirdMarketDTOList = entry.getValue().stream().map(t -> t.getData()).collect(Collectors.toList());
                List<ThirdMarketDTO> thirdMarketDTOArrayList = new ArrayList<>();
                //T01 A01用他们的排序
                if (dataSourceCode.contains("TX")) {
                    String prefixTAKey = Constant.REDIS_KEY.THIRD_T_A_BASKETBALL_MARKET_HEAD + oddsWrapperList.get(0).getStandardSourceId() + "_" + dataSourceCode + "_" + marketCategoryId;
                    //缓存格式 map<坑位，盘口>
                    Map<String, ThirdMarketDTO> thirdTAMarketHeadCacheMap = redisService.hGetAll(prefixTAKey);
                    if (MapUtils.isEmpty(thirdTAMarketHeadCacheMap)) {
                        thirdTAMarketHeadCacheMap = new HashMap<>();
                    }
                    //替换缓存的坑位盘口
                    for (ThirdMarketDTO thirdMarket : thirdMarketDTOList) {
                        thirdTAMarketHeadCacheMap.put(thirdMarket.getOfferLineId().toString(), thirdMarket);
                    }
                    //入缓存
                    redisService.hSetAll(prefixTAKey, thirdTAMarketHeadCacheMap, marketCacheTime(standardMatchInfo.getBeginTime()));
                    //所有坑位最新盘口处理
                    List<ThirdMarketDTO> thirdMarketListFinal = thirdTAMarketHeadCacheMap.values().stream().collect(Collectors.toList());
                    ThirdMarketDTO thirdMarket2 = null;
                    for (ThirdMarketDTO thirdMarket : thirdMarketListFinal) {
                        if (thirdMarket.getOfferLineId() == 1) {
                            thirdMarketNewHeadFinal.add(thirdMarket);
                        }
                        //总分大小 下发最后附加盘，
                        if (thirdMarket.getMarketCategoryId() == 38L || thirdMarket.getMarketCategoryId() == 39L
                                || thirdMarket.getMarketCategoryId() == 45L || thirdMarket.getMarketCategoryId() == 46L
                                || thirdMarket.getMarketCategoryId() == 57L || thirdMarket.getMarketCategoryId() == 58L
                                || thirdMarket.getMarketCategoryId() == 18L || thirdMarket.getMarketCategoryId() == 19L) {
                            if (thirdMarket.getOfferLineId() != 1 && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarket.getStatus())) {
                                //下发最后的附加盘
                                thirdMarket2 = thirdMarket;
                            }
                        }
                    }
                    if (null != thirdMarket2) {
                        thirdMarketNewHeadFinal.add(thirdMarket2);
                    }
                    sendThirdMarketHeadCacheNewMap.put(marketCategoryId, thirdMarketNewHeadFinal);
                } else {
                    String key = prefixKey + "-" + marketCategoryId;
                    List<ThirdMarketDTO> thirdMarketHeadCache = (List<ThirdMarketDTO>) thirdMarketHeadCacheMap.get(key);
                    //第一次缓存不存在 ，直接用数据商的盘口
                    if (CollectionUtils.isEmpty(thirdMarketHeadCache)) {
                        thirdMarketDTOArrayList.addAll(thirdMarketDTOList);
                    } else {
                        //数据商盘口 三方盘口id分组
                        Map<String, ThirdMarketDTO> thirdMarketDTOMap = thirdMarketDTOList.stream().collect(Collectors.toMap(ThirdMarketDTO::getThirdMarketSourceId, thi -> thi, (oldValue, newValue) -> newValue));
                        //缓存盘口 三方盘口id分组
                        Map<String, ThirdMarketDTO> thirdMarketDTOCachaMap = thirdMarketHeadCache.stream().collect(Collectors.toMap(ThirdMarketDTO::getThirdMarketSourceId, thi -> thi, (oldValue, newValue) -> newValue));
                        //数据商Map 合并 缓存盘口Map
                        Map<String, ThirdMarketDTO> newMap = new HashMap<>();
                        newMap.putAll(thirdMarketDTOCachaMap);
                        newMap.putAll(thirdMarketDTOMap);
                        thirdMarketDTOArrayList = new ArrayList(newMap.values());
                    }
                    //计算出投注项赔率差
                    thirdMarketDTOArrayList.forEach(m -> {
                        if (CollectionUtils.isEmpty(m.getMarketOddsList())) {
                            m.setOddsMetric(999999);
                        } else {
                            m.setOddsMetric(m.getMarketOddsList().stream().map(ThirdMarketOddsDTO::getOriginalOddsValue).reduce(0, (a, b) -> a >= b ? a - b : b - a));
                        }
                    });
                    //数据商状态、赔率差 升序排序
                    ListUtils.sort(thirdMarketDTOArrayList, true, "status", "oddsMetric");

                    int num = 1;
                    ThirdMarketDTO thirdMarket2 = null;
                    for (ThirdMarketDTO thirdMarket : thirdMarketDTOArrayList) {
                        thirdMarket.setOfferLineId(num);
                        if (num == 1) {
                            thirdMarketNewHeadFinal.add(thirdMarket);
                        }
                        //总分大小 下发最后附加盘，
                        if (thirdMarket.getMarketCategoryId() == 38L || thirdMarket.getMarketCategoryId() == 39L
                                || thirdMarket.getMarketCategoryId() == 45L || thirdMarket.getMarketCategoryId() == 46L
                                || thirdMarket.getMarketCategoryId() == 57L || thirdMarket.getMarketCategoryId() == 58L
                                || thirdMarket.getMarketCategoryId() == 18L || thirdMarket.getMarketCategoryId() == 19L) {
                            if (num != 1 && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarket.getStatus())) {
                                //下发最后的附加盘
                                thirdMarket2 = thirdMarket;
                            }
                        }
                        num++;
                    }
                    if (null != thirdMarket2) {
                        thirdMarketNewHeadFinal.add(thirdMarket2);
                    }
                    thirdMarketHeadCacheNewMap.put(marketCategoryId.toString(), thirdMarketNewHeadFinal);
                    sendThirdMarketHeadCacheNewMap.put(marketCategoryId, thirdMarketNewHeadFinal);
                }
            }
            if (!MarginCategoryConfig.SPORT_TX_LOGIC.contains(dataSourceCode)) {
                //重新缓存数据源球头
                redisService.hSetAllSync(prefixKey, thirdMarketHeadCacheNewMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
            standardMarketOddsProducer.sendBasketballThirdBallHeadMarketAoAsync(linkId, thirdMatchInfo, standardMatchInfo, sendThirdMarketHeadCacheNewMap, dataSourceCode, dataSourceTime);
        }
    }
}
