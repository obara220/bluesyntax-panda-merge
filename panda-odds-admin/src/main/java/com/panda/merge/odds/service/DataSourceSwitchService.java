package com.panda.merge.odds.service;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dao.CategoryDataSourceCodeDao;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.UpdateMarketCategoryDataSourceCodeDTO;
import com.panda.merge.dto.odds.CategoryDataSource;
import com.panda.merge.model.CategoryDatasourcecodeChange;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.odds.ThirdMarketMonitor;
import com.panda.merge.odds.cache.DataSourceCategoryPriorityCacheService;
import com.panda.merge.odds.cache.MatchLiveCacheService;
import com.panda.merge.odds.utils.DataSourceUtils;
import com.panda.merge.rocketmq.processor.SoldMessageToOddsProcessor;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DataSourceSwitchService
 *
 * @description: 数据源切换
 * @date: 5/2/2025
 **/
@Service
@Slf4j
public class DataSourceSwitchService {

    @Resource(name = "dataSourceSwitchPool")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private StandardSportMarketSellLogService standardSportMarketSellLogService;

    @Autowired
    private ConfigMarketOddsStatusService configMarketOddsStatusService;

    @Autowired
    private SoldMessageToOddsProcessor soldMessageToOddsProcessor;

    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private CategoryDataSourceCodeDao categoryDataSourceCodeDao;

    @Autowired
    private DiffService diffService;

   @Autowired
   private DataSourceCategoryPriorityCacheService priorityCacheService;

    @Transactional(rollbackFor = Exception.class)
    public void switchDataSource(Request<List<UpdateMarketCategoryDataSourceCodeDTO>> request,
                                 List<UpdateMarketCategoryDataSourceCodeDTO> switchList,
                                 StandardSportMarketSell standardSportMarketSell,
                                 Map<String, MarketCategorySell> standardMarketCategorySellMap,
                                 Integer marketType,
                                 List<Long> categoryList,
                                 StandardMatchInfo standardMatchInfo) {
        List<MarketCategorySell> sellList = new ArrayList<>();
        String linkId = request.getLinkId();
        String operaterName = request.getData().get(0).getUserName();

        for (UpdateMarketCategoryDataSourceCodeDTO updateMarketCategoryDataSourceCodeDTO : switchList) {
            String dataSourceCode;
            if (StringUtils.isBlank(updateMarketCategoryDataSourceCodeDTO.getDataSourceCode())) {
                dataSourceCode =
                        "0".equals(marketType.toString()) ? standardSportMarketSell.getLiveMatchDataProviderCode() :
                                standardSportMarketSell.getPreMatchDataProviderCode();
            } else {
                dataSourceCode =
                        DataSourceUtils.transformDataSourceCode(updateMarketCategoryDataSourceCodeDTO.getDataSourceCode());
            }

            MarketCategorySell marketCategorySell = new MarketCategorySell();
            marketCategorySell.setSrWeight((DataSourceCodeEnum.SR.code.equals(dataSourceCode) ? 1 : 0));
            marketCategorySell.setBcWeight((DataSourceCodeEnum.BC.code.equals(dataSourceCode) ? 1 : 0));
            marketCategorySell.setBgWeight((DataSourceCodeEnum.BG.code.equals(dataSourceCode) ? 1 : 0));
            marketCategorySell.setDataSourceCode(dataSourceCode);
            marketCategorySell.setMarketCategoryId(updateMarketCategoryDataSourceCodeDTO.getMarketCategoryId());
            marketCategorySell.setMatchId(updateMarketCategoryDataSourceCodeDTO.getMatchId());
            marketCategorySell.setMarketType(updateMarketCategoryDataSourceCodeDTO.getMarketType());
            marketCategorySell.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            marketCategorySell.setLinkId(linkId);
            String key = marketCategorySell.getMatchId() + "_" + marketCategorySell.getMarketType() + "_" +
                    marketCategorySell.getMarketCategoryId();
            MarketCategorySell marketCategorySellDB = standardMarketCategorySellMap.get(key);
            if (null != marketCategorySellDB) {
                marketCategorySell.setId(marketCategorySellDB.getId());
                sellList.add(marketCategorySell);
            } else {
                log.info("::{}::玩法开售表没有找到记录:{}", linkId, JSONObject.toJSONString(marketCategorySell));
            }
        }
        marketCategorySellService.batchUpdateById(standardMatchInfo.getId(), marketType, sellList);
        priorityCacheService.batchClearPriority(linkId,request.getData());
        //维护赛事开售表的使用过数据源的字段   方便取消关联做判断
        standardSportMarketSellLogService.log(switchList,
                                              standardSportMarketSell,
                                              linkId,
                                              marketType,
                                              request.getOperaterId(),
                                              operaterName);
        //清除玩法开售表缓存
        switchList.forEach(x -> marketCategorySellService.removeCache(standardMatchInfo.getId(),
                                                                      marketType,
                                                                      x.getMarketCategoryId()));
        //清水差
        diffService.delDiffByMatchIdAndCategoryList(linkId,
                                                    standardSportMarketSell.getMatchInfoId(),
                                                    categoryList,
                                                    standardSportMarketSell.getSportId().intValue());
        //清除投注项赔率配置
        configMarketOddsStatusService.updateStatusByMatchIdAndCategoryIds(linkId,
                                                                          standardSportMarketSell.getMatchInfoId(),
                                                                          categoryList,
                                                                          Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE);
        //下发最新赔率，这里需要做一下开售逻辑，因为玩法切换新赔率源，可能存在新赔率源的玩法还没有入标准库，需要走开售逻辑
        Map<Long, String> marketCategoryIds = switchList
                .stream()
                .collect(Collectors.toMap(UpdateMarketCategoryDataSourceCodeDTO::getMarketCategoryId,
                                          UpdateMarketCategoryDataSourceCodeDTO::getDataSourceCode,
                                          (o, n) -> o));
        soldMessageToOddsProcessor.soldHandler(request.getLinkId(),
                                               standardMatchInfo,
                                               standardSportMarketSell,
                                               marketCategoryIds,
                                               marketType,
                                               true,
                                               true);
        List<CategoryDatasourcecodeChange> list = new ArrayList<>();
        request.getData().forEach(e -> {
            val change = new CategoryDatasourcecodeChange();
            change.setId(UUIdUtils.getId());
            change.setStandardMatchId(e.getMatchId());
            change.setMarketType(Integer.parseInt(e.getMarketType()));
            change.setStandardCategoryId(e.getMarketCategoryId());
            change.setSellStatus(e.getSellStatus());
            change.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            change.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            change.setDataSourceCode(e.getDataSourceCode());
            list.add(change);
        });
        if (!CollectionUtils.isEmpty(list)) {
            categoryDataSourceCodeDao.insertList(list);
        }
        // 删除玩法对应的缓存
        Set<String> categorySet =
                categoryList.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toSet());
        thirdMatchMarketProcessor.delCategoryCloseCache(linkId, standardMatchInfo.getId(), categorySet);
    }

    @Transactional(rollbackFor = Exception.class)
    public void switchCategory(CategoryDataSource cds) {
        log.info("linkId:{},matchId:{},categoryId:{},switch data source, original:{}, target:{}",
                 cds.linkId,
                 cds.standardMatchInfo.getId(),
                 cds.categoryId,cds.internalOds,cds.internalTds);
        if (cds.marketCategorySell != null
                && DataSourceUtils.isSameDataSourceCode(cds.marketCategorySell.getDataSourceCode(), cds.internalTds)) {
            log.info("linkId:{},matchId:{},categoryId:{}, switchCategory skip, already on target ds:{}, current:{}",
                     cds.linkId, cds.standardMatchInfo.getId(), cds.categoryId,
                     cds.internalTds, cds.marketCategorySell.getDataSourceCode());
            cds.status = 0;
            return;
        }
        String dataSourceCode = cds.tds;
        MarketCategorySell marketCategorySell = new MarketCategorySell();
        marketCategorySell.setSrWeight((DataSourceCodeEnum.SR.code.equals(dataSourceCode) ? 1 : 0));
        marketCategorySell.setBcWeight((DataSourceCodeEnum.BC.code.equals(dataSourceCode) ? 1 : 0));
        marketCategorySell.setBgWeight((DataSourceCodeEnum.BG.code.equals(dataSourceCode) ? 1 : 0));
        marketCategorySell.setDataSourceCode(dataSourceCode);
        marketCategorySell.setMarketCategoryId(cds.categoryId);
        marketCategorySell.setMatchId(cds.standardMatchInfo.getId());
        marketCategorySell.setMarketType(String.valueOf(cds.marketType));
        marketCategorySell.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        marketCategorySell.setLinkId(cds.linkId);
        marketCategorySell.setId(cds.marketCategorySell.getId());
        marketCategorySellService.update(marketCategorySell);
        priorityCacheService.clearPriority(cds.linkId, cds.standardMatchInfo.getId(), cds.marketType, cds.categoryId);

        //维护赛事开售表的使用过数据源的字段   方便取消关联做判断
        standardSportMarketSellLogService.log(cds);
        //清除玩法开售表缓存
        marketCategorySellService.removeCache(cds.standardMatchInfo.getId(), cds.marketType, cds.categoryId);
        //清水差
        diffService.delDiffByMatchIdAndCategoryList(cds.linkId,
                                                    cds.standardSportMarketSell.getMatchInfoId(),
                                                    Collections.singletonList(cds.categoryId),
                                                    cds.standardSportMarketSell.getSportId().intValue());
        //清除投注项赔率配置
        configMarketOddsStatusService.updateStatusByMatchIdAndCategoryIds(cds.linkId,
                                                                          cds.standardSportMarketSell.getMatchInfoId(),
                                                                          Collections.singletonList(cds.categoryId),
                                                                          Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE);
        //下发最新赔率，这里需要做一下开售逻辑，因为玩法切换新赔率源，可能存在新赔率源的玩法还没有入标准库，需要走开售逻辑
        Map<Long, String> soldMap = Collections.singletonMap(cds.categoryId, cds.internalTds);
        soldMessageToOddsProcessor.soldHandler(cds.linkId,
                                               cds.standardMatchInfo,
                                               cds.standardSportMarketSell,
                                               soldMap,
                                               cds.marketType,
                                               true,
                                               true);

        val change = new CategoryDatasourcecodeChange();
        change.setId(UUIdUtils.getId());
        change.setStandardMatchId(cds.standardMatchInfo.getId());
        change.setMarketType(cds.marketType);
        change.setStandardCategoryId(cds.categoryId);
        change.setSellStatus(cds.marketCategorySell.getSellStatus());
        change.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        change.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        change.setDataSourceCode(cds.internalTds);
        categoryDataSourceCodeDao.insertList(Collections.singletonList(change));
        thirdMatchMarketProcessor.delCategoryCloseCache(cds.linkId,
                                                        cds.standardMatchInfo.getId(),
                                                        Collections.singleton(cds.categoryId.toString()));
        cds.status = 0;
        log.info("linkId:{},matchId:{},categoryId:{},switch category success",cds.linkId,cds.standardMatchInfo.getId(),
                 cds.categoryId);

    }

}
