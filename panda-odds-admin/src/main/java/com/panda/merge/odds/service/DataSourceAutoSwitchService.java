package com.panda.merge.odds.service;

import com.panda.merge.common.enums.DataSourceEncrypEnum;
import com.panda.merge.component.MaintainDataSourceProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.odds.CategoryDataSource;
import com.panda.merge.dto.odds.DataSourceAutoSwitchConfig;
import com.panda.merge.dto.odds.MatchCategoryDataSourcesDTO;
import com.panda.merge.dto.odds.MatchDataSourceDTO;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.odds.AutoSwitchConfigService;
import com.panda.merge.odds.ThirdMarketMonitor;
import com.panda.merge.odds.cache.MatchLiveCacheService;
import com.panda.merge.odds.utils.DataSourceUtils;
import com.panda.merge.service.MarketCategorySellService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DataSourceAutoSwitchService
 *
 * @description: 数据源自动切换服务
 * @date: 5/6/2025
 **/
@Service
@Slf4j
public class DataSourceAutoSwitchService {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private MatchLiveCacheService matchLiveCacheService;

    @Resource(name = "dataSourceSwitchPool")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private DataSourceSwitchService dataSourceSwitchService;

    @Autowired
    private ThirdMarketMonitor thirdMarketMonitor;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private AutoSwitchConfigService autoSwitchConfigService;

    @Autowired
    private MaintainDataSourceProcessor maintainDataSourceProcessor;

    public Response<MatchDataSourceDTO> autoSwitch(Request<MatchDataSourceDTO> request) {
        MatchDataSourceDTO data = request.getData();
        Long standardMatchInfoId = data.getMatchId();
        StandardMatchInfo standardMatch = standardMatchInfoService.getItem(standardMatchInfoId);
        if (Objects.isNull(standardMatch)) {
            log.info("::{}::data source autoSwitch, id:{} 找不到标准赛事 ", request.getLinkId(), standardMatchInfoId);
            return Response.failed("未找到标准赛事");
        }
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(standardMatchInfoId);
        if (CollectionUtils.isEmpty(thirdMatchInfoList)) {
            String message = String.format("标准赛事未查询到第三方赛事，标准赛事id: %s", standardMatchInfoId);
            log.info("::{}::data source autoSwitch, {}", request.getLinkId(), message);
            return Response.failed("标准赛事没有三方赛事");
        }

        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfoId);
        if (standardSportMarketSell == null) {
            return Response.failed("找不到玩法开售信息");
        }

        Map<String, ThirdMatchInfo> dataSourceMatchMap = thirdMatchInfoList
                .stream()
                .collect(Collectors.toMap(ThirdMatchInfo::getDataSourceCode, Function.identity(), (v1, v2) -> v2));
        int marketType = matchLiveCacheService.getMarketType(standardMatch.getId());

        List<MarketCategorySell> categorySellList =
                marketCategorySellService.getItem(standardMatchInfoId, String.valueOf(marketType));
        if (CollectionUtils.isEmpty(categorySellList)) {
            return Response.failed("玩法未开售,不能切换数据源");
        }
        Map<String, MarketCategorySell> categorySellMap = categorySellList
                .stream()
                .collect(Collectors.toMap(e -> e.getMatchId() + "_" + e.getMarketType() + "_" + e.getMarketCategoryId(),
                                          e -> e,
                                          (oldValue, newValue) -> newValue));

        for (MatchDataSourceDTO.CategoryDataSourceDTO cds : data.getCategoryDataSources()) {
            MarketCategorySell marketCategorySell =
                    categorySellMap.get(standardMatchInfoId + "_" + marketType + "_" + cds.getCategoryId());
            if (Objects.isNull(marketCategorySell)) {
                return Response.failed("categoryId:" + cds.getCategoryId() + " 玩法未开售,不能切换数据源");
            }
        }

        return Response.success(new MatchDataSourceDTO(standardMatchInfoId,
                                                       dispatch(data,
                                                                standardMatch,
                                                                standardSportMarketSell,
                                                                dataSourceMatchMap,
                                                                marketType,
                                                                categorySellMap,
                                                                request.getLinkId())
                                                        ));
    }

    public Response<MatchCategoryDataSourcesDTO> getDataSources(Request<MatchCategoryDataSourcesDTO> request) {
        MatchCategoryDataSourcesDTO data = request.getData();
        Long matchId = data.getMatchId();
        int marketType = matchLiveCacheService.getMarketType(matchId);
        DataSourceAutoSwitchConfig config = autoSwitchConfigService.getConfig(matchId, marketType);
        if (Objects.isNull(config) || CollectionUtils.isEmpty(config.getDataSourceList())) {
            return Response.failed("invalid config");
        }
        List<String> candidates = config.getDataSourceList();
        //维护中的数据源需要剔除
        List<String> underMaintenances = maintainDataSourceProcessor.underMaintenance(request.getLinkId(), matchId);
        MatchCategoryDataSourcesDTO result = new MatchCategoryDataSourcesDTO(matchId,data.getValidityPeriod(),new ArrayList<>());
        for (MatchCategoryDataSourcesDTO.CategoryDataSources categoryInfo : data.getCategoryDataSources()) {
            List<String> categoryDataSources = thirdMarketMonitor.getAvailableDataSources(request.getLinkId(),
                                                                                          categoryInfo.getCategory(),
                                                                                          matchId,
                                                                                          marketType,
                                                                                          candidates,
                                                                                          config.getValidSecond(),
                                                                                          underMaintenances);

            //判断 categoryDataSources 集合 是存在维护中的数据源
            MatchCategoryDataSourcesDTO.CategoryDataSources categoryResult =
                    new MatchCategoryDataSourcesDTO.CategoryDataSources(categoryInfo.getCategory(), categoryDataSources);
            result.getCategoryDataSources().add(categoryResult);

        }
        log.info("linkId:{},matchId:{},getStandardCategoryAvailableDataSources result:{}",
                 request.getLinkId(),
                 matchId,
                 result);
        return Response.success(result);
    }

    private List<MatchDataSourceDTO.CategoryDataSourceDTO> dispatch(MatchDataSourceDTO data,
                                                                    StandardMatchInfo standardMatch,
                                                                    StandardSportMarketSell standardSportMarketSell,
                                                                    Map<String, ThirdMatchInfo> dataSourceMatchMap,
                                                                    int marketType,
                                                                    Map<String, MarketCategorySell> categorySellMap,
                                                                    String linkId) {
        //维护中的数据源需要剔除
        List<String> underMaintenances = maintainDataSourceProcessor.underMaintenance(linkId, standardMatch.getId());

        List<Future<CategoryDataSource>> futures = data
                .getCategoryDataSources()
                .stream()
                .map(cds -> taskExecutor.submit(() -> switchCategory(dataSourceMatchMap,
                                                                     standardMatch,
                                                                     standardSportMarketSell,
                                                                     linkId,
                                                                     marketType,
                                                                     categorySellMap.get(
                                                                             standardMatch.getId() + "_" + marketType +
                                                                                     "_" + cds.getCategoryId()),
                                                                     cds,
                                                                     underMaintenances)))
                .collect(Collectors.toList());
        List<MatchDataSourceDTO.CategoryDataSourceDTO> results = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            Future<CategoryDataSource> future = futures.get(i);
            try {

                CategoryDataSource categoryDataSource = future.get();
                MatchDataSourceDTO.CategoryDataSourceDTO response = categoryDataSource.createResponse();
                /*log.info("linkId:{},matchId:{}, categoryId:{},autoSwitchDataSource result:{}",
                         categoryDataSource.linkId,categoryDataSource.standardMatchInfo.getId(),
                         categoryDataSource.categoryId,
                         response);*/
                results.add(response);
            } catch (Exception e) {
                MatchDataSourceDTO.CategoryDataSourceDTO categoryDataSourceDTO = data.getCategoryDataSources().get(i);
                log.error("linkId:{},matchId:{},categoryId:{},autoSwitchDataSource error:",linkId,
                          standardMatch.getId(),categoryDataSourceDTO.getCategoryId(),e);
                categoryDataSourceDTO.setStatus(-1);
                categoryDataSourceDTO.setRemark(e.getMessage());
            }
        }
        return results;
    }

    private CategoryDataSource switchCategory(Map<String, ThirdMatchInfo> dataSourceMatchMap,
                                              StandardMatchInfo standardMatch,
                                              StandardSportMarketSell standardSportMarketSell,
                                              String linkId,
                                              int marketType,
                                              MarketCategorySell marketCategorySell,
                                              MatchDataSourceDTO.CategoryDataSourceDTO cds,
                                              List<String> underMaintenances) {
        log.info("linkId:{},matchId:{},categoryId:{},underMaintenances：{}, auto switchCategory",linkId,standardMatch.getId(),
                 cds.getCategoryId(),underMaintenances);
        CategoryDataSource categoryDataSource =
                CategoryDataSource.createFromInternal(cds.getCategoryId(), cds.getOds(), marketType, linkId);

        categoryDataSource.marketCategorySell = marketCategorySell;
        categoryDataSource.standardSportMarketSell = standardSportMarketSell;
        categoryDataSource.standardMatchInfo = standardMatch;
        thirdMarketMonitor.getAvailableDatasource(categoryDataSource);
        ThirdMatchInfo thirdMatch;
        if (StringUtils.isEmpty(categoryDataSource.tds)) {
            categoryDataSource.failed("没有可供切换数据源");
            return categoryDataSource;
        }
        //维护数据源
        if (underMaintenances.contains(DataSourceEncrypEnum.getDataSourceVal(categoryDataSource.tds.split("-")[0]))) {
            categoryDataSource.failed("数据源已经维护");
            return categoryDataSource;
        }
        if ((thirdMatch = dataSourceMatchMap.get(categoryDataSource.tds)) == null) {
            categoryDataSource.failed("三方赛事不存在");
            return categoryDataSource;
        }
        categoryDataSource.thirdMatchInfo = thirdMatch;

        if (Objects.isNull(marketCategorySell)) {
            categoryDataSource.failed("玩法未开售");
            return categoryDataSource;
        }

        // 手动切换完成后，数据商推盘会触发 monitor → 自动切换；若 DB 已是目标源则不再走 soldHandler 关旧，避免关盘覆盖刚开的盘
        if (DataSourceUtils.isSameDataSourceCode(marketCategorySell.getDataSourceCode(),
                                                 categoryDataSource.internalTds)) {
            log.info("linkId:{},matchId:{},categoryId:{}, auto switch skip, already on target ds:{}, current:{}",
                     linkId, standardMatch.getId(), cds.getCategoryId(),
                     categoryDataSource.internalTds, marketCategorySell.getDataSourceCode());
            categoryDataSource.status = 0;
            return categoryDataSource;
        }

        dataSourceSwitchService.switchCategory(categoryDataSource);
        return categoryDataSource;
    }

}
