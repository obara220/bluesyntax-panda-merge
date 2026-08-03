package com.panda.merge.odds;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.DataSourceEncrypEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.odds.CategoryDataSource;
import com.panda.merge.dto.odds.CategoryDataSourceHighPriority;
import com.panda.merge.dto.odds.DataSourceAutoSwitchConfig;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.odds.cache.DataSourceCategoryPriorityCacheService;
import com.panda.merge.odds.cache.NumberCacheService;
import com.panda.merge.service.MarketCategorySellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED;
import static com.panda.merge.common.enums.Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED;
import static com.panda.merge.odds.constants.CategoryConstant.CATEGORY_SCORE_TYPE_MAP;

/**
 * ThirdMarketMonitor
 *
 * @description: 三方盘口状态监控
 * <p>
 * 玩法盘口状态缓存
 * <p>
 * key dss:standardMatchId:standardMarketCategoryId:marketType:dataSourceId
 * <p>
 * value timestamp
 * @date: 4/27/2025
 **/
@Component
@Slf4j
public class ThirdMarketMonitor {

    private static final Set<String> INTERNAL_SITE_DATA_SOURCES = new HashSet<>(Arrays.asList("TX", "LS"));

    @Autowired
    private AutoSwitchConfigService autoSwitchConfigService;

    @Resource(name = "CallRedisThreadPool")
    private ThreadPoolTaskExecutor redisExecutor;

    @Autowired
    private NumberCacheService numberCacheService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private DataSourceCategoryPriorityCacheService priorityCacheService;

    @Autowired
    private CategoryPriorityProducer categoryPriorityProducer;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private XtsMonitor xtsMonitor;

    @Async("monitorExecutor")
    public void monitor(Long uuid,
                        List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOList,
                        Map<String, MarketCategorySell> marketCategorySellMap) {
        log.info("uuid: {}, third market monitor,size:{} ", uuid, thirdMarketDTOList.size());
        doMonitor(uuid, thirdMarketDTOList, marketCategorySellMap, this::filter);
        xtsMonitor.monitor(uuid,thirdMarketDTOList);
    }

    @Async("monitorExecutor")
    public void monitorInternalSite(Long uuid, List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOList) {
        log.info("uuid:{}, third market internal site monitor,size:{} ", uuid, thirdMarketDTOList.size());

        // 获取玩法开售
        List<String> marketSellKeys = thirdMarketDTOList.stream().map(inner -> {
            if (inner.getStandardSourceId() != null) {
                return inner.getStandardSourceId() + "-" + inner.getMarketCategoryId() + "-" + inner.getMarketType();
            }
            return null;
        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<MarketCategorySell> marketCategorySells = marketCategorySellService.getItems(marketSellKeys);
        Map<String, MarketCategorySell> marketCategorySellMap = marketCategorySells
                .stream()
                .collect(Collectors.toMap(t -> t.getMatchId() + "-" + t.getMarketType() + "-" + t.getMarketCategoryId(),
                                          Function.identity(),
                                          (v1, v2) -> v1));
        doMonitor(uuid, thirdMarketDTOList, marketCategorySellMap, this::filterInternalSite);
    }

    public List<String> getAvailableDataSources(String linkId,
                                                Long categoryId,
                                                Long matchId,
                                                Integer marketType,
                                                List<String> candidates,
                                                Integer validSecond,
                                                List<String> underMaintenances) {
        List<String> keys =
                candidates.stream().map(ds -> getKey(ds, matchId, categoryId, marketType)).collect(Collectors.toList());
        List<Object> caches = redisService.mGet(keys);
        log.info("linkId:{},categoryId:{},currentTime:{}, candidates:{}, validaSecond:{},caches:{},underMaintenances:{}",
                 linkId,
                 categoryId,
                 System.currentTimeMillis(),
                 candidates,
                 validSecond,
                 caches,
                underMaintenances);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < caches.size(); i++) {
            if (isAvailable((Long) caches.get(i), marketType, validSecond)) {
                String resultDataSource = candidates.get(i);
                //维护中的数据源不返回
                if (!underMaintenances.contains(DataSourceEncrypEnum.getDataSourceVal(resultDataSource.split("-")[0]))) {
                    result.add(resultDataSource);
                }
            }
        }
        return result;
    }

    public void getAvailableDatasource(CategoryDataSource cds) {
        DataSourceAutoSwitchConfig config =
                autoSwitchConfigService.getConfig(cds.standardMatchInfo.getId(), cds.marketType);
        List<String> dataSources =
                config.getDataSourceList();
        if (CollectionUtils.isEmpty(dataSources)) {
            log.error("linkId:{},categoryId:{}, auto Switch config dataSources is  empty", cds.linkId, cds.categoryId);
            return;
        }
        Integer validSecond = config.getValidSecond();
        if (validSecond == null || validSecond <= 0) {
            log.error("linkId:{},categoryId:{}, auto Switch config validSecond is null or <= 0",
                      cds.linkId,
                      cds.categoryId);
            return;
        }

        List<String> candidates = dataSources
                .stream()
                .filter(ds -> !StringUtils.equalsIgnoreCase(cds.internalOds, ds))
                .collect(Collectors.toList());

        List<String> keys = candidates
                .stream()
                .map(ds -> getKey(ds, cds.standardMatchInfo.getId(), cds.categoryId, cds.marketType))
                .collect(Collectors.toList());
        List<Object> caches = redisService.mGet(keys);
        log.info("linkId:{},categoryId:{},currentTime:{}, candidates:{}, validaSecond:{},caches:{}",
                 cds.linkId,
                 cds.categoryId,
                 System.currentTimeMillis(),
                 candidates,
                 validSecond,
                 caches);
        for (int i = 0; i < caches.size(); i++) {
            if (isAvailable((Long) caches.get(i), cds.marketType, validSecond)) {
                cds.setTdsFromInternal(candidates.get(i));
                log.info("linkId:{},categoryId:{}, auto Switch datasource target:{}",
                         cds.linkId,
                         cds.categoryId,
                         candidates.get(i));
                break;
            }
        }

    }

    private String getKey(String dataSourceCode, Long matchId, Long categoryId, Integer marketType) {
        return String.format("dss:%s:%s:%s:%s", matchId, categoryId, marketType, dataSourceCode);
    }

    private boolean filter(OddsWrapper<ThirdMarketDTO> oddsWrapper) {
        ThirdMarketDTO data = oddsWrapper.getData();
        if (StringUtils.isEmpty(data.getDataSourceCode()) || null == data.getMarketCategoryId() ||
                null == data.getMarketType() || null == oddsWrapper.getStandardSourceId() ||
                null == oddsWrapper.getDataSourceTime()) {
            return false;
        }
        return !INTERNAL_SITE_DATA_SOURCES.contains(data.getDataSourceCode().toUpperCase());
    }

    private boolean filterInternalSite(OddsWrapper<ThirdMarketDTO> oddsWrapper) {
        ThirdMarketDTO data = oddsWrapper.getData();
        if (StringUtils.isEmpty(data.getDataSourceCode()) || null == data.getMarketCategoryId() ||
                null == data.getMarketType() || null == oddsWrapper.getStandardSourceId() ||
                null == oddsWrapper.getDataSourceTime()) {
            return false;
        }
        return INTERNAL_SITE_DATA_SOURCES.contains(data.getDataSourceCode().toUpperCase());
    }

    private void doMonitor(Long uuid, List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOList,
                           Map<String, MarketCategorySell> marketCategorySellMap,
                           Predicate<OddsWrapper<ThirdMarketDTO>> filter) {
        if (CollectionUtils.isEmpty(thirdMarketDTOList))
            return;

        Map<Long, Map<Integer, Map<Long, Map<String, OddsWrapper<ThirdMarketDTO>>>>> matchOdds = thirdMarketDTOList
                .stream()
                .filter(filter)
                .collect(Collectors.groupingBy(OddsWrapper::getStandardSourceId,
                                               Collectors.groupingBy(OddsWrapper::getMarketType,
                                                                     Collectors.groupingBy(OddsWrapper::getMarketCategoryId,
                                                                                           Collectors.toMap(this::getDataSourceCode,
                                                                                                            Function.identity(),
                                                                                                            (o1, o2) -> {
                                                                                                                if (o1.getDataSourceTime() >
                                                                                                                        o2.getDataSourceTime()) {
                                                                                                                    return o1;
                                                                                                                }
                                                                                                                return o2;
                                                                                                            })))));
        log.info("uuid:{}, third market do monitor,size:{} ", uuid, matchOdds.size());
        matchOdds.forEach((matchId, marketTypeMap) -> marketTypeMap.forEach((marketType, categoryMap) -> {
            DataSourceAutoSwitchConfig config = autoSwitchConfigService.getConfig(matchId, marketType);
            if (Objects.isNull(config) || !config.isEnabled()) {
                return;
            }
            categoryMap.forEach((categoryId, dataSourceMap) -> {
                dataSourceMap.forEach((dataSourceCode, oddsWrapper) -> {
                    updateCategoryTime(oddsWrapper);
                });
                Set<String> dataSourceCodeSet =
                        dataSourceMap.entrySet().stream().filter(entry -> {
                            ThirdMarketDTO market = entry.getValue().getData();
                            return !Objects.equals(market.getStatus(), DEACTIVATED) &&
                                    !Objects.equals(market.getStatus(), SUSPENDED);
                        }). map(Map.Entry::getKey).collect(Collectors.toSet());
                String sellMapKey =  matchId + "-" + marketType + "-" + categoryId;
                try {
                    Long sportId = dataSourceMap
                            .values()
                            .stream()
                            .map(OddsWrapper::getSportId)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);
                    updatePriority(uuid, dataSourceCodeSet, marketCategorySellMap.get(sellMapKey), config, sportId);
                } catch (Exception e) {
                    log.error("uuid:{},updatePriority failed, {}", uuid, JSON.toJSONString(dataSourceMap), e);
                }
            });
        }));

    }

    private void updatePriority(Long uuid, Set<String> dsSet,
                                MarketCategorySell marketCategorySell,
                                DataSourceAutoSwitchConfig monitorConfig, Long sportId) {
        if (Objects.isNull(marketCategorySell) || Objects.isNull(marketCategorySell.getDataSourceCode())) {
            return;
        }
        log.info("uuid:{},updatePriority, matchId:{},categoryId:{},",
                 uuid, marketCategorySell.getMatchId(), marketCategorySell.getMarketCategoryId());
        CategoryDataSourceHighPriority priority =
                autoSwitchConfigService.getHighPriority(uuid, dsSet, marketCategorySell, monitorConfig,sportId);
        if (Objects.equals(sportId, StandardSportTypeEnum.FootBall.code)){
             if (CATEGORY_SCORE_TYPE_MAP.containsKey(marketCategorySell.getMarketCategoryId())){
                 if (priorityCacheService.updatePriority(priority)) {
                     categoryPriorityProducer.send(priority);
                 }
             }
        }else{
            if (priorityCacheService.updatePriority(priority)) {
                categoryPriorityProducer.send(priority);
            }
        }

    }

    private boolean isAvailable(Long timestamp, int marketType, Integer validSecond) {
        if (Objects.isNull(timestamp)) {
            return false;
        }
        return (System.currentTimeMillis() - timestamp) <= validSecond * 1000;
    }

    private void updateCategoryTime(OddsWrapper<ThirdMarketDTO> wrapper) {
        ThirdMarketDTO market = wrapper.getData();
        if (Objects.equals(market.getStatus(), DEACTIVATED) || Objects.equals(market.getStatus(),SUSPENDED)) {
            redisService.del(getKey(wrapper));
            return;
        }
        redisExecutor.execute(() -> {
            String key = getKey(wrapper);
            Long time = wrapper.getDataSourceTime();
            Integer expireSeconds = getExpireSeconds(wrapper);
            numberCacheService.update(key, time, expireSeconds);
        });
    }

    private Integer getExpireSeconds(OddsWrapper<ThirdMarketDTO> wrapper) {
        Integer marketType = wrapper.getMarketType();
        return autoSwitchConfigService.getExpireSeconds(marketType, wrapper.getStandardSourceId());
    }

    private String getKey(OddsWrapper<ThirdMarketDTO> wrapper) {
        ThirdMarketDTO market = wrapper.getData();
        String dataSourceCode = StringUtils.isEmpty(market.getInternalDataSourceCode()) ? market.getDataSourceCode() :
                market.getInternalDataSourceCode();
        Long matchId = wrapper.getStandardSourceId();
        Long categoryId = market.getMarketCategoryId();
        Integer marketType = market.getMarketType();
        return getKey(dataSourceCode, matchId, categoryId, marketType);
    }

    private String getDataSourceCode(OddsWrapper<ThirdMarketDTO> wrapper) {
        ThirdMarketDTO market = wrapper.getData();
        return StringUtils.isEmpty(market.getInternalDataSourceCode()) ? market.getDataSourceCode() :
                market.getInternalDataSourceCode();
    }

}
