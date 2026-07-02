package com.panda.merge.rocketmq.common;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.dao.ThirdMatchInfoDao;
import com.panda.merge.dto.MatchEventInfoDetail;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.MatchSaleOverJobProducer;
import com.panda.merge.service.MatchEventInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketOddsService;
import com.panda.merge.service.ThirdSportMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

@Service
@Slf4j
@Async("cleanHisDataPool")
public class RcsHisDataServiceImpl implements RcsHisDataService{

    @Autowired
    CategoryDatasourcecodeChangeMapper categoryDatasourcecodeChangeMapper;
    @Autowired
    ConfigurationMatchDataSourceMapper configurationMatchDataSourceMapper;
    @Autowired
    ConfigCashOutTradeItemMapper configCashOutTradeItemMapper;
    @Autowired
    ConfigCategoryAutoDiffTradeMapper configCategoryAutoDiffTradeMapper;
    @Autowired
    ConfigMarketAutoDiffTradeMapper configMarketAutoDiffTradeMapper;
    @Autowired
    ConfigMarketAutoDiffTradeLogMapper configMarketAutoDiffTradeLogMapper;
    @Autowired
    ConfigMarketCategoryHeadMapper configMarketCategoryHeadMapper;
    @Autowired
    ConfigMarketCategoryHeadLogMapper configMarketCategoryHeadLogMapper;
    @Autowired
    ConfigMarketCategoryMarginMapper configMarketCategoryMarginMapper;
    @Autowired
    ConfigMarketCategoryPlaceMapper configMarketCategoryPlaceMapper;
    @Autowired
    ConfigMarketDisplayTradeMapper configMarketDisplayTradeMapper;
    @Autowired
    ConfigMarketCategoryMarginLogMapper configMarketCategoryMarginLogMapper;
    @Autowired
    ConfigMarketDisplayTradeLogMapper configMarketDisplayTradeLogMapper;
    @Autowired
    ConfigMarketMarginGapMapper configMarketMarginGapMapper;
    @Autowired
    ConfigMarketMarginGapLogMapper configMarketMarginGapLogMapper;
    @Autowired
    ConfigMarketStatusTradeMapper configMarketStatusTradeMapper;
    @Autowired
    ConfigMarketTradeItemMapper configMarketTradeItemMapper;
    @Autowired
    ConfigMarketTradeItemLogMapper configMarketTradeItemLogMapper;
    @Autowired
    ConfigPlacenumAutoDiffTradeMapper configPlacenumAutoDiffTradeMapper;
    @Autowired
    ConfigTradeTypeMapper configTradeTypeMapper;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    ThirdSportMarketService thirdSportMarketService;
    @Autowired
    ThirdSportMarketMapper thirdSportMarketMapper;
    @Autowired
    ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Autowired
    ThirdSportMarketOddsMapper thirdSportMarketOddsMapper;
    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    StandardSportMarketMMapper standardSportMarketMMapper;
    @Autowired
    StandardSportMarketOddsMapper standardSportMarketOddsMapper;
    @Autowired
    MarketCategorySellMapper marketCategorySellMapper;
    @Autowired
    ConfigTradeMarketLogMapper configTradeMarketLogMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    StandardSportMarketSellLogMapper standardSportMarketSellLogMapper;

    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private ThirdMatchInfoDao thirdMatchInfoDao;
    @Autowired
    private MatchSaleOverJobProducer matchSaleOverJobProducer;
    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    private MatchStatisticsInfoMapper matchStatisticsInfoMapper;
    @Autowired
    private MatchStatisticsInfoDetailMapper matchStatisticsInfoDetailMapper;
    @Autowired
    private MatchAutoAssociationMapper matchAutoAssociationMapper;
    @Autowired
    private MatchAutoAssociationDetailMapper matchAutoAssociationDetailMapper;
    @Autowired
    private ThirdMatchTeamRelationMapper thirdMatchTeamRelationMapper;
    @Autowired
    private StandardMatchInfoHisMapper standardMatchInfoHisMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private MatchDataSourceWeightMapper matchDataSourceWeightMapper;
    @Autowired
    private StandardMatchTeamRelationMapper standardMatchTeamRelationMapper;
    @Autowired
    private MatchEventInfoScoresMapper matchEventInfoScoresMapper;
    @Autowired
    private ConfigMatchStatusMapper configMatchStatusMapper;

    @Autowired
    private LeagueTeamMatchLogMapper leagueTeamMatchLogMapper;
    @Autowired
    private PlsThirdMatchRelationMapper plsThirdMatchRelationMapper;

    @Override
    public void configDataHandler(String linkId, Long standardMatchId) {
        //category_datasourcecode_change 清理
        CategoryDatasourcecodeChangeExample categoryDatasourcecodeChangeExample = new CategoryDatasourcecodeChangeExample();
        categoryDatasourcecodeChangeExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num0 = categoryDatasourcecodeChangeMapper.deleteByExample(categoryDatasourcecodeChangeExample);

        //configuration_match_data_source 清理
        ConfigurationMatchDataSourceExample configurationMatchDataSourceExample = new ConfigurationMatchDataSourceExample();
        configurationMatchDataSourceExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num1 = configurationMatchDataSourceMapper.deleteByExample(configurationMatchDataSourceExample);

        //config_cash_out_trade_item 清理
        ConfigCashOutTradeItemExample configCashOutTradeItemExample = new ConfigCashOutTradeItemExample();
        configCashOutTradeItemExample.createCriteria().andMatchIdEqualTo(standardMatchId);
        int num2 = configCashOutTradeItemMapper.deleteByExample(configCashOutTradeItemExample);

        //config_category_auto_diff_trade 清理
        ConfigCategoryAutoDiffTradeExample configCategoryAutoDiffTradeExample = new ConfigCategoryAutoDiffTradeExample();
        configCategoryAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num3 = configCategoryAutoDiffTradeMapper.deleteByExample(configCategoryAutoDiffTradeExample);

        //config_market_auto_diff_trade 清理
        ConfigMarketAutoDiffTradeExample configMarketAutoDiffTradeExample = new ConfigMarketAutoDiffTradeExample();
        configMarketAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num4 = configMarketAutoDiffTradeMapper.deleteByExample(configMarketAutoDiffTradeExample);

        //config_market_category_head 清理
        ConfigMarketCategoryHeadExample configMarketCategoryHeadExample = new ConfigMarketCategoryHeadExample();
        configMarketCategoryHeadExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        int num5 = configMarketCategoryHeadMapper.deleteByExample(configMarketCategoryHeadExample);

        //config_market_category_margin 清理
        ConfigMarketCategoryMarginExample configMarketCategoryMarginExample = new ConfigMarketCategoryMarginExample();
        configMarketCategoryMarginExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        int num6 = configMarketCategoryMarginMapper.deleteByExample(configMarketCategoryMarginExample);

        //config_market_category_place 清理
        ConfigMarketCategoryPlaceExample configMarketCategoryPlaceExample = new ConfigMarketCategoryPlaceExample();
        configMarketCategoryPlaceExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        int num7 = configMarketCategoryPlaceMapper.deleteByExample(configMarketCategoryPlaceExample);

        //config_market_display_trade 清理
        ConfigMarketDisplayTradeExample configMarketDisplayTradeExample = new ConfigMarketDisplayTradeExample();
        configMarketDisplayTradeExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num8 = configMarketDisplayTradeMapper.deleteByExample(configMarketDisplayTradeExample);

        //config_market_margin_gap 清理
        ConfigMarketMarginGapExample configMarketMarginGapExample = new ConfigMarketMarginGapExample();
        configMarketMarginGapExample.createCriteria().andMatchIdEqualTo(standardMatchId);
        int num9 = configMarketMarginGapMapper.deleteByExample(configMarketMarginGapExample);

        //config_market_status_trade 清理
        ConfigMarketStatusTradeExample configMarketStatusTradeExample = new ConfigMarketStatusTradeExample();
        configMarketStatusTradeExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        int num10 = configMarketStatusTradeMapper.deleteByExample(configMarketStatusTradeExample);

        //config_market_trade_item 清理
        ConfigMarketTradeItemExample configMarketTradeItemExample = new ConfigMarketTradeItemExample();
        configMarketTradeItemExample.createCriteria().andMatchIdEqualTo(standardMatchId);
        int num11 = configMarketTradeItemMapper.deleteByExample(configMarketTradeItemExample);

        //config_placenum_auto_diff_trade
        ConfigPlacenumAutoDiffTradeExample configPlacenumAutoDiffTradeExample = new ConfigPlacenumAutoDiffTradeExample();
        configPlacenumAutoDiffTradeExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num12 = configPlacenumAutoDiffTradeMapper.deleteByExample(configPlacenumAutoDiffTradeExample);

        //config_trade_type
        ConfigTradeTypeExample configTradeTypeExample = new ConfigTradeTypeExample();
        configTradeTypeExample.createCriteria().andStandardMatchIdEqualTo(String.valueOf(standardMatchId));
        int num13 = configTradeTypeMapper.deleteByExample(configTradeTypeExample);

        //standard_sport_market_sell
        StandardSportMarketSellExample standardSportMarketSellExample = new StandardSportMarketSellExample();
        standardSportMarketSellExample.createCriteria().andMatchInfoIdEqualTo(standardMatchId);
        int num14 = standardSportMarketSellMapper.deleteByExample(standardSportMarketSellExample);
        int count = num0 + num1 + num2 + num3 + num4 + num5 + num6 + num7 + num8 + num9 + num10 + num11 + num12 + num13 + num14;
        log.info("::{}::数据清理configDataHandler,赛事ID:{},num0:{},num1:{},num2:{},num3:{},num4:{},num5:{},num6:{},num7:{},num8:{},num9:{},num10:{},num11:{},num12:{},num13:{},num14:{},总条数:{}",
                linkId, standardMatchId, num0, num1, num2, num3, num4, num5, num6, num7, num8, num9, num10, num11, num12, num13, num14, count);
    }

    @Override
    public void standardSportMarketDataHandler(String linkId, Long standardMatchId) {
        //standard_sport_market_m
        StandardSportMarketMExample standardSportMarketMExample = new StandardSportMarketMExample();
        standardSportMarketMExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        standardSportMarketMMapper.deleteByExample(standardSportMarketMExample);
        //standard_sport_market
        StandardSportMarketExample standardSportMarketExample = new StandardSportMarketExample();
        standardSportMarketExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        List<StandardSportMarket> standardSportMarkets = standardSportMarketMapper.selectByExample(standardSportMarketExample);
        if (CollectionUtils.isEmpty(standardSportMarkets)) {
            return ;
        }
        int num0 = standardSportMarketMapper.deleteByExample(standardSportMarketExample);

        List<Long> standardMarketIdList = standardSportMarkets.stream().map(StandardSportMarket::getId).collect(Collectors.toList());
        //standard_sport_market_odds_{0-9}
        StandardSportMarketOddsExample standardSportMarketOddsExample = new StandardSportMarketOddsExample();
        standardSportMarketOddsExample.createCriteria().andMarketIdIn(standardMarketIdList);
        int num1 = standardSportMarketOddsMapper.deleteByExample(standardSportMarketOddsExample);
        int count = num0 + num1;
        log.info("::{}::数据清理standardSportMarketDataHandler,赛事ID:{},标准盘口条数:{},投注项条数:{},总条数:{}",
                linkId, standardMatchId, num0, num1, count);
    }

    @Override
    public void marketCategorySellDataHandler(String linkId, Long standardMatchId) {
        //market_category_sell
        MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
        marketCategorySellExample.createCriteria().andMatchIdEqualTo(standardMatchId);
        int num = marketCategorySellMapper.deleteByExample(marketCategorySellExample);
        log.info("::{}::数据清理marketCategorySellDataHandler,赛事ID:{},玩法开售条数:{}", linkId, standardMatchId, num);

    }

    @Override
    public void operatorDataHandler(String linkId, Long standardMatchId) {
        //config_market_auto_diff_trade_log 清理
        ConfigMarketAutoDiffTradeLogExample configMarketAutoDiffTradeLogExample = new ConfigMarketAutoDiffTradeLogExample();
        configMarketAutoDiffTradeLogExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num0 = configMarketAutoDiffTradeLogMapper.deleteByExample(configMarketAutoDiffTradeLogExample);

        //config_market_category_head_log 清理
        ConfigMarketCategoryHeadLogExample configMarketCategoryHeadLogExample = new ConfigMarketCategoryHeadLogExample();
        configMarketCategoryHeadLogExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        int num1 = configMarketCategoryHeadLogMapper.deleteByExample(configMarketCategoryHeadLogExample);

        //config_market_category_margin_log 清理
        ConfigMarketCategoryMarginLogExample configMarketCategoryMarginLogExample = new ConfigMarketCategoryMarginLogExample();
        configMarketCategoryMarginLogExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchId);
        int num2 = configMarketCategoryMarginLogMapper.deleteByExample(configMarketCategoryMarginLogExample);

        //config_market_display_trade_log 清理
        ConfigMarketDisplayTradeLogExample configMarketDisplayTradeLogExample = new ConfigMarketDisplayTradeLogExample();
        configMarketDisplayTradeLogExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num3 = configMarketDisplayTradeLogMapper.deleteByExample(configMarketDisplayTradeLogExample);

        //config_market_margin_gap_log 清理
        ConfigMarketMarginGapLogExample configMarketMarginGapLogExample = new ConfigMarketMarginGapLogExample();
        configMarketMarginGapLogExample.createCriteria().andMatchIdEqualTo(standardMatchId);
        int num4 = configMarketMarginGapLogMapper.deleteByExample(configMarketMarginGapLogExample);

        //config_market_trade_item_log 清理
        ConfigMarketTradeItemLogExample configMarketTradeItemLogExample = new ConfigMarketTradeItemLogExample();
        configMarketTradeItemLogExample.createCriteria().andMatchIdEqualTo(standardMatchId);
        int num5 = configMarketTradeItemLogMapper.deleteByExample(configMarketTradeItemLogExample);

        //config_trade_market_log 清理
        ConfigTradeMarketLogExample configTradeMarketLogExample = new ConfigTradeMarketLogExample();
        configTradeMarketLogExample.createCriteria().andTargetIdEqualTo(String.valueOf(standardMatchId));
        int num6 = configTradeMarketLogMapper.deleteByExample(configTradeMarketLogExample);

        //standard_sport_market_sell_log
        StandardSportMarketSellLogExample standardSportMarketSellLogExample = new StandardSportMarketSellLogExample();
        standardSportMarketSellLogExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        int num7 = standardSportMarketSellLogMapper.deleteByExample(standardSportMarketSellLogExample);

        int count = num0 + num1 + num2 + num3 + num4 + num5 + num6 + num7;
        log.info("::{}::数据清理operatorDataHandler,赛事ID:{},num0:{},num1:{},num2:{},num3:{},num4:{},num5:{},num6:{},num7:{},总条数:{}",
                linkId, standardMatchId, num0, num1, num2, num3, num4, num5, num6, num7, count);
    }

    @Override
    public void thirdSportMarketDataHandler(String linkId, Map<Long, String> mapIds, String dataSourceCode) {
        Set<Long> thirdMatchIdList = mapIds.keySet();
        //third_sport_market
        ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
        thirdSportMarketExample.createCriteria().andMatchIdIn(new ArrayList<>(thirdMatchIdList));
        List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
        if (CollectionUtils.isEmpty(thirdSportMarkets)) {
            return;
        }
        List<Long> thirdSportMarketIds = thirdSportMarkets.stream().map(ThirdSportMarket::getId).collect(Collectors.toList());

        int num0 = thirdSportMarketMapper.deleteByExample(thirdSportMarketExample);
        //third_sport_market_odds{bc/bg/pa/sr/tx}
        ThirdSportMarketOddsExample thirdSportMarketOddsExample = new ThirdSportMarketOddsExample();
        thirdSportMarketOddsExample.createCriteria().andMarketIdIn(new ArrayList<>(thirdSportMarketIds))
                .andDataSourceCodeEqualTo(dataSourceCode);
        int num1 = thirdSportMarketOddsMapper.deleteByExample(thirdSportMarketOddsExample);

        int count = num0 + num1;
        log.info("::{}::数据清理thirdSportMarketDataHandler,三方赛事Id集合:{},num0:{},num1:{},总条数:{}", linkId, thirdMatchIdList, num0, num1, count);
    }

    @Override
    public void thirdTradeDataHandler(String linkId, Map<Long, String> idMap, String dataSourceCode) {
        List<String> thirdMatchSoureIdList = new ArrayList<>(idMap.values());
        //config_trade_market_log 清理
        ConfigTradeMarketLogExample configTradeMarketLogExample = new ConfigTradeMarketLogExample();
        configTradeMarketLogExample.createCriteria().andTargetIdIn(thirdMatchSoureIdList);
        int num0 = configTradeMarketLogMapper.deleteByExample(configTradeMarketLogExample);
        log.info("::{}::数据清理thirdTradeDataHandler,三方原始赛事ID集合:{},num0:{}", linkId, thirdMatchSoureIdList, num0);
    }

    @Override
    public void cleanEndedDayStandardMatch(Long dayDateTime,Integer matchNum,Integer matchOver,Integer deleteEvent) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_standard_");
        log.info("linkId=【"+linkId+"】,cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息开始，dayDateTime：{}，matchNum：{},matchOver:{}",dayDateTime,matchNum,matchOver);
        try{
            //清除完赛的标准赛事相关信息
            StandardMatchInfoExample example = new StandardMatchInfoExample();
            if(null != matchOver){
                example.createCriteria().andMatchOverEqualTo(matchOver).andBeginTimeLessThanOrEqualTo(dayDateTime);
            }else{
                example.createCriteria().andBeginTimeLessThanOrEqualTo(dayDateTime);
            }
            long matchCount = standardMatchInfoMapper.countByExample(example);
            log.info("linkId=【"+linkId+"】,cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息,还需要清理的总条数={}", matchCount);
            PageHelper.startPage(ONE, matchNum);
            List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
            if(!CollectionUtils.isEmpty(standardMatchInfoList)){
                int size = standardMatchInfoList.size();
                //按数据源分组
                Map<String, List<StandardMatchInfo>> dataSourceCode2Map = standardMatchInfoList.stream().collect(Collectors.groupingBy(obj -> obj.getDataSourceCode()));
                log.info("linkId=【"+linkId+"】,cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息条数,本次需要清理的总条数={},包含数据源编码={}", size,JSON.toJSONString(dataSourceCode2Map.keySet()));
                for (String dataSourceCode: dataSourceCode2Map.keySet()) {
                    List<StandardMatchInfo> standardMatchInfosByCode = dataSourceCode2Map.get(dataSourceCode);
                    log.info("linkId=【"+linkId+"】,cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息,数据源{}本次需要清理的总条数={}",dataSourceCode,standardMatchInfosByCode.size());
                    List<List<StandardMatchInfo>> matchLists = CommUtils.groupList(standardMatchInfosByCode, 50);
                    for (int i=0;i<matchLists.size();i++) {
                        Request<List<Long>> request = new Request<>();
                        try{
                            //本次处理的标准赛事信息
                            List<StandardMatchInfo> standardMatchInfos = matchLists.get(i);
                            List<Long> matchIds = standardMatchInfos.stream().map(match -> match.getId()).collect(Collectors.toList());
//                            List<String> matchManageIds = standardMatchInfos.stream().map(match -> match.getMatchManageId()).collect(Collectors.toList());

                            request.setLinkId(StringUtils.join(linkId, dataSourceCode+"_"+i));
                            request.setData(matchIds);
                            request.setDataSourceCode(dataSourceCode);
                            request.setDataType(STANDARD_MATCH_OVER_DAY_CLEAN);
                            request.setTag(String.valueOf(dayDateTime));
                            matchSaleOverJobProducer.sendCleanEndedDayMatch(request);

//                            if(deleteEvent == 1){
//                                try{
//                                    if(DataSourceCodeEnum.getEventCodeList().contains(dataSourceCode)){
//                                        //赛事盘中事件
//                                        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
//                                        //分表字段，在删除的时候必须带上
//                                        matchEventInfoExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andStandardMatchIdIn(matchIds);
//                                        long matchEventCount = matchEventInfoMapper.countByExample(matchEventInfoExample);
//                                        int matchEventInfoNum = matchEventInfoMapper.deleteByExample(matchEventInfoExample);
//                                        log.info("cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事: 清除三方赛事盘中事件：{},matchEventCount={},dataSourceCode={}",matchEventInfoNum,matchEventCount,dataSourceCode);
//                                    }
//                                }catch (Exception e){
//                                    log.error("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除三方赛事盘中事件,Exception:", e);
//                                }
//                            }

                            //标准赛事比分事件信息（该数据是提供给大数据项目组的，对应需求编号：1549）
                            MatchEventInfoScoresExample matchEventInfoScoresExample = new MatchEventInfoScoresExample();
                            matchEventInfoScoresExample.createCriteria().andStandardMatchIdIn(matchIds);
                            int matchEventInfoScoresNum = matchEventInfoScoresMapper.deleteByExample(matchEventInfoScoresExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除标准赛事比分事件信息：{}", matchEventInfoScoresNum);

                            //赛事统计
                            MatchStatisticsInfoExample matchStatisticsInfoExample = new MatchStatisticsInfoExample();
                            matchStatisticsInfoExample.createCriteria().andStandardMatchIdIn(matchIds);
                            List<MatchStatisticsInfo> matchStatisticsInfos = matchStatisticsInfoMapper.selectByExample(matchStatisticsInfoExample);
                            if(!CollectionUtils.isEmpty(matchStatisticsInfos)){
                                int matchStatisticsInfoNum = matchStatisticsInfoMapper.deleteByExample(matchStatisticsInfoExample);
                                log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除赛事统计：{}", matchStatisticsInfoNum);

                                //根据赛事统计ID清理赛事统计详情
                                List<Long> matchStatisticsInfoIds = matchStatisticsInfos.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                                MatchStatisticsInfoDetailExample matchStatisticsInfoDetailExample = new MatchStatisticsInfoDetailExample();
                                matchStatisticsInfoDetailExample.createCriteria().andMatchStatisticsInfoIdIn(matchStatisticsInfoIds);
                                int matchStatisticsInfoDetailNum = matchStatisticsInfoDetailMapper.deleteByExample(matchStatisticsInfoDetailExample);
                                log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除赛事统计详情：{}", matchStatisticsInfoDetailNum);
                            }

                            //数据源权重表
                            MatchDataSourceWeightExample dataSourceWeightExample= new MatchDataSourceWeightExample();
                            dataSourceWeightExample.createCriteria().andStandardMatchIdIn(matchIds);
                            int dataSourceWeightNum = matchDataSourceWeightMapper.deleteByExample(dataSourceWeightExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除数据源权重条数：{}", dataSourceWeightNum);

                            //标准盘口开售
                            StandardSportMarketSellExample standardSportMarketSellExample = new StandardSportMarketSellExample();
                            standardSportMarketSellExample.createCriteria().andMatchInfoIdIn(matchIds);
                            int standardSportMarketSellNum = standardSportMarketSellMapper.deleteByExample(standardSportMarketSellExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除标准盘口开售条数：{}", standardSportMarketSellNum);

                            //标准盘口开售日志
                            StandardSportMarketSellLogExample standardSportMarketSellLogExample = new StandardSportMarketSellLogExample();
                            standardSportMarketSellLogExample.createCriteria().andStandardMatchIdIn(matchIds);
                            int standardSportMarketSellLogNum = standardSportMarketSellLogMapper.deleteByExample(standardSportMarketSellLogExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除标准盘口开售日志条数：{}", standardSportMarketSellLogNum);

                            //标准盘口开售
                            MarketCategorySellExample marketCategorySellExample = new MarketCategorySellExample();
                            marketCategorySellExample.createCriteria().andMatchIdIn(matchIds);
                            int marketCategorySellNum = marketCategorySellMapper.deleteByExample(marketCategorySellExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除标准盘口开售条数：{}", marketCategorySellNum);

                            //标准赛事球队关系
                            StandardMatchTeamRelationExample relationExample= new StandardMatchTeamRelationExample();
                            relationExample.createCriteria().andStandardMatchIdIn(matchIds);
                            int relationNum = standardMatchTeamRelationMapper.deleteByExample(relationExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除标准赛事球队关系条数：{}", relationNum);

                            //标准赛事风控配置信息
                            ConfigMatchStatusExample configExample= new ConfigMatchStatusExample();
                            configExample.createCriteria().andStandardMatchInfoIdIn(matchIds);
                            int configNum = configMatchStatusMapper.deleteByExample(configExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事风控配置,清除标准赛事风控配置信息条数：{}", configNum);

                            LeagueTeamMatchLogExample logExample = new LeagueTeamMatchLogExample();
                            logExample.createCriteria().andOperateTargetIdIn(matchIds);
                            int logNum = leagueTeamMatchLogMapper.deleteByExample(logExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事日志信息,清除标准赛事日志信息数：{}", logNum);
                            // 标准赛事与PLS映射数据清理集合
                            List<Long> plsStandardMatchIdList = new ArrayList<>();
                            //标准赛事保存到历史表
                            for (StandardMatchInfo item: standardMatchInfos) {
                                try{
                                    StandardMatchInfoHis matchHis = new StandardMatchInfoHis();
                                    BeanUtils.copyProperties(item, matchHis);
                                    standardMatchInfoHisMapper.insertSelective(matchHis);
                                }catch (Exception e){
                                    log.error("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,拷贝标准赛事到历史表,Exception:", e);
                                }

                                Long plsStandardMatchId = item.getPlsStandardMatchId();
                                if (null != plsStandardMatchId && !plsStandardMatchId.equals(0L)) {
                                    plsStandardMatchIdList.add(plsStandardMatchId);
                                }
                            }
                            try {
                                if (plsStandardMatchIdList.size() > 0) {
                                    PlsThirdMatchRelationExample plsThirdMatchRelationExample = new PlsThirdMatchRelationExample();
                                    plsThirdMatchRelationExample.createCriteria().andPlsStandardMatchIdIn(plsStandardMatchIdList);
                                    int plsThirdMatchRelationNum = plsThirdMatchRelationMapper.deleteByExample(plsThirdMatchRelationExample);
                                    log.info("linkId=【" + request.getLinkId() + "】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事关联的PLS赛事关系,清除标准赛事条数：{}", plsThirdMatchRelationNum);
                                }
                            } catch (Exception e) {
                                log.error("linkId=【"+request.getLinkId()+"】cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事关联的PLS赛事关系信息异常 request : "+JSON.toJSONString(request)+",Exception:", e);
                            }
                            //标准赛事信息
                            StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
                            standardMatchInfoExample.createCriteria().andIdIn(matchIds).andDataSourceCodeEqualTo(dataSourceCode);
                            int standardMatchNum = standardMatchInfoMapper.deleteByExample(standardMatchInfoExample);
                            log.info("linkId=【"+request.getLinkId()+"】,cleanEndedDayStandardMatch,每天清理N天前完赛赛事标准赛事,清除标准赛事条数：{}", standardMatchNum);
                        }catch (Exception e){
                            log.error("linkId=【"+request.getLinkId()+"】cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息异常 request : "+JSON.toJSONString(request)+",Exception:", e);
                        }
                    }
                    log.info("linkId=【"+linkId+"】,cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息,dataSourceCode:{},size:{}清理完成", dataSourceCode,matchLists.size());
                }
            }
            //兜底清除历史赛事事件数据
//            cleanMatchEventInfoData(linkId,matchNum,dayDateTime,"cleanEndedDayStandardMatch",deleteEvent);
        }catch (Exception e){
            log.error("linkId=【"+linkId+"】,cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息执行异常，Exception:", e);
        }
        stopWatch.stop();
        log.info("linkId=【"+linkId+"】,cleanEndedDayStandardMatch,每天清理N天前完赛标准赛事信息执行用时{}毫秒",stopWatch.getTotalTimeMillis());
    }


    @Autowired
    private MatchEventCommonMapper matchEventCommonMapper;
    @Autowired
    private ThirdMatchResultMapper thirdMatchResultMapper;
    @Autowired
    private StandardMatchResultMapper standardMatchResultMapper;

    @Override
    public void cleanEndedDayThirdMatch(Long dayDateTime,Integer matchNum,Integer matchOver,Integer deleteEvent) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_third_");
        log.info("linkId=【"+linkId+"】,cleanEndedDayThirdMatch,每天清理N天前完赛三方赛事信息开始，dayDateTime：{}，matchNum：{},matchOver:{}",dayDateTime,matchNum,matchOver);
        try{
            //清除完赛一周的三方赛事相关信息
            ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
            if(null != matchOver){
                thirdMatchInfoExample.createCriteria().andMatchOverEqualTo(matchOver).andBeginTimeLessThanOrEqualTo(dayDateTime);
            }else{
                thirdMatchInfoExample.createCriteria().andBeginTimeLessThanOrEqualTo(dayDateTime);
            }
            long matchCount = thirdMatchInfoMapper.countByExample(thirdMatchInfoExample);
            log.info("linkId=【"+linkId+"】,cleanEndedDayThirdMatch,每天清理N天前完赛三方赛事信息,还需要清理的总条数：{}", matchCount);
            PageHelper.startPage(ONE, matchNum);
            List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
            if(!CollectionUtils.isEmpty(thirdMatchInfoList)){
                int size = thirdMatchInfoList.size();
                //按数据源分组
                Map<String, List<ThirdMatchInfo>> dataSourceCode2Map = thirdMatchInfoList.stream().collect(Collectors.groupingBy(obj -> obj.getDataSourceCode()));
                log.info("linkId=【"+linkId+"】,cleanEndedDayThirdMatch,每天清理N天前完赛三方赛事信息条数,本次需要清理的总条数={},包含数据源编码={}", size,JSON.toJSONString(dataSourceCode2Map.keySet()));
                for (String dataSourceCode: dataSourceCode2Map.keySet()) {
                    List<ThirdMatchInfo> thirdMatchInfosByCode = dataSourceCode2Map.get(dataSourceCode);
                    log.info("linkId=【"+linkId+"】,cleanEndedDayThirdMatch,每天清理N天前完赛三方赛事信息,数据源{}本次需要清理的总条数：{}",dataSourceCode,thirdMatchInfosByCode.size());
                    List<List<ThirdMatchInfo>> matchLists = CommUtils.groupList(thirdMatchInfosByCode, 50);
                    for (int i=0;i<matchLists.size();i++) {
                        Request<Map<Long, String>> request = new Request<>();
                        try{
                            List<ThirdMatchInfo> thirdMatchInfos = matchLists.get(i);
                            //三方赛事ID和数据源赛事ID关系
                            Map<Long, String> id2ThirdMatchSourceId = thirdMatchInfos.stream().collect(Collectors.toMap(ThirdMatchInfo::getId, obj -> obj.getThirdMatchSourceId()));
                            request.setLinkId(StringUtils.join(linkId, dataSourceCode+"_"+i));
                            request.setData(id2ThirdMatchSourceId);
                            request.setDataSourceCode(dataSourceCode);
                            request.setDataType(THIRD_MATCH_OVER_DAY_CLEAN);
                            request.setTag(String.valueOf(dayDateTime));
                            matchSaleOverJobProducer.sendCleanEndedDayMatch(request);

                            //本次处理的三方赛事ID
                            List<Long> matchIds = Lists.newArrayList(id2ThirdMatchSourceId.keySet());
//                            List<String> thirdMatchSourceIds = thirdMatchInfos.stream().map(third -> third.getThirdMatchSourceId()).collect(Collectors.toList());
//                            if(deleteEvent == 1){
//                                try{
//                                    if(DataSourceCodeEnum.getEventCodeList().contains(dataSourceCode)){
//                                        //赛事盘中事件
//                                        MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
//                                        //分表字段，在删除的时候必须带上
//                                        matchEventInfoExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andThirdMatchIdIn(matchIds);
//                                        long matchEventCount = matchEventInfoMapper.countByExample(matchEventInfoExample);
//                                        int matchEventInfoNum = matchEventInfoMapper.deleteByExample(matchEventInfoExample);
//                                        log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除三方赛事盘中事件：{},matchEventCount={},dataSourceCode={}",matchEventInfoNum,matchEventCount,dataSourceCode);
//                                    }
//                                }catch (Exception e){
//                                    log.error("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除三方赛事盘中事件,Exception:", e);
//                                }
//                            }

                            //赛事统计
                            MatchStatisticsInfoExample matchStatisticsInfoExample = new MatchStatisticsInfoExample();
                            matchStatisticsInfoExample.createCriteria().andThirdMatchIdIn(matchIds).andDataSourceCodeEqualTo(dataSourceCode);
                            List<MatchStatisticsInfo> matchStatisticsInfos = matchStatisticsInfoMapper.selectByExample(matchStatisticsInfoExample);
                            if(!CollectionUtils.isEmpty(matchStatisticsInfos)){
                                int matchStatisticsInfoNum = matchStatisticsInfoMapper.deleteByExample(matchStatisticsInfoExample);
                                log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除赛事统计：{}", matchStatisticsInfoNum);
                                //根据赛事统计ID清理赛事统计详情
                                List<Long> matchStatisticsInfoIds = matchStatisticsInfos.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                                MatchStatisticsInfoDetailExample matchStatisticsInfoDetailExample = new MatchStatisticsInfoDetailExample();
                                matchStatisticsInfoDetailExample.createCriteria().andMatchStatisticsInfoIdIn(matchStatisticsInfoIds);
                                int matchStatisticsInfoDetailNum = matchStatisticsInfoDetailMapper.deleteByExample(matchStatisticsInfoDetailExample);
                                log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除赛事统计详情：{}", matchStatisticsInfoDetailNum);
                            }

                            //事件审核原始事件表
                            MatchEventCommonExample matchEventCommonExample = new MatchEventCommonExample();
                            matchEventCommonExample.createCriteria().andThirdMatchIdIn(matchIds).andDataSourceCodeEqualTo(dataSourceCode);
                            List<MatchEventCommon> matchEventCommons = matchEventCommonMapper.selectByExample(matchEventCommonExample);
                            if(!CollectionUtils.isEmpty(matchEventCommons)){
                                int matchEventCommonNum = matchEventCommonMapper.deleteByExample(matchEventCommonExample);
                                log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除事件审核原始事件：{}", matchEventCommonNum);
                            }
                            //事件审核赛果赛果表
                            ThirdMatchResultExample thirdMatchResultExample = new ThirdMatchResultExample();
                            thirdMatchResultExample.createCriteria().andThirdMatchIdIn(matchIds).andDataSourceCodeEqualTo(dataSourceCode);
                            List<ThirdMatchResult> thirdMatchResults = thirdMatchResultMapper.selectByExample(thirdMatchResultExample);
                            if(!CollectionUtils.isEmpty(thirdMatchResults)){
                                int thirdMatchResultNum = thirdMatchResultMapper.deleteByExample(thirdMatchResultExample);
                                log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除事件审核三方赛果：{}", thirdMatchResultNum);
                            }
                            //事件审核赛果赛果表
                            StandardMatchResultExample standardMatchResultExample = new StandardMatchResultExample();
                            standardMatchResultExample.createCriteria().andThirdMatchIdIn(matchIds).andDataSourceCodeEqualTo(dataSourceCode);
                            List<StandardMatchResult> standardMatchResults = standardMatchResultMapper.selectByExample(standardMatchResultExample);
                            if(!CollectionUtils.isEmpty(standardMatchResults)){
                                int standardMatchResultNum = standardMatchResultMapper.deleteByExample(standardMatchResultExample);
                                log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除事件审核标准赛果：{}", standardMatchResultNum);
                            }

                            //赛程自动融合表
                            MatchAutoAssociationExample matchAutoAssociationExample= new MatchAutoAssociationExample();
                            matchAutoAssociationExample.createCriteria().andThirdMatchIdIn(matchIds);
                            int matchAutoAssociationNum = matchAutoAssociationMapper.deleteByExample(matchAutoAssociationExample);
                            log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除赛程自动融合表条数：{}", matchAutoAssociationNum);
                            //赛程自动融合详情表
                            MatchAutoAssociationDetailExample matchAutoAssociationDetailExample= new MatchAutoAssociationDetailExample();
                            matchAutoAssociationDetailExample.createCriteria().andThirdMatchIdIn(matchIds);
                            int matchAutoAssociationDetailNum = matchAutoAssociationDetailMapper.deleteByExample(matchAutoAssociationDetailExample);
                            log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除赛程自动融合详情表条数：{}", matchAutoAssociationDetailNum);

                            //三方赛事球队关系
                            ThirdMatchTeamRelationExample relationExample= new ThirdMatchTeamRelationExample();
                            relationExample.createCriteria().andMatchIdIn(matchIds);
                            int relationNum = thirdMatchTeamRelationMapper.deleteByExample(relationExample);
                            log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除三方赛事球队关系条数：{}", relationNum);

                            //三方赛事信息
                            ThirdMatchInfoExample thirdMatchInfo= new ThirdMatchInfoExample();
                            thirdMatchInfo.createCriteria().andIdIn(matchIds).andDataSourceCodeEqualTo(dataSourceCode);
                            int  thirdMatchNum = thirdMatchInfoMapper.deleteByExample(thirdMatchInfo);
                            log.info("linkId=【"+request.getLinkId()+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清除三方赛事条数：{}", thirdMatchNum);
                        }catch (Exception e){
                            log.error("linkId=【"+request.getLinkId()+"】,cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 request : "+ JSON.toJSONString(request)+",Exception:", e);
                        }
                    }
                    log.info("linkId=【"+linkId+"】,cleanEndedDayThirdMatch,每天清理N天前完赛三方赛事信息,dataSourceCode:{},size:{}清理完成", dataSourceCode,matchLists.size());
                }
            }
            //三方赛事球队关系
            List<ThirdMatchInfoDetail> list = thirdMatchInfoDao.getThirdRelationByNotInMatchId(matchNum);
            if(!CollectionUtils.isEmpty(list)){
                List<Long> thirdRelationIds = list.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchTeamRelationExample relationExample= new ThirdMatchTeamRelationExample();
                relationExample.createCriteria().andIdIn(thirdRelationIds);
                int relationNum = thirdMatchTeamRelationMapper.deleteByExample(relationExample);
                log.info("linkId=【"+linkId+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 清理三方赛事球队关系脏数据条数：{}", relationNum);
            }else{
                log.info("linkId=【"+linkId+"】cleanEndedDayThirdMatch,每天清理N天前完赛赛三方赛事 : 查询到三方赛事球队关系脏数据条数：{}", 0);
            }

            //兜底清除历史赛事事件数据
            cleanMatchEventInfoData(linkId,matchNum,dayDateTime,"cleanEndedDayThirdMatch",deleteEvent);
        }catch (Exception e){
            log.error("linkId=【"+linkId+"】,cleanEndedDayThirdMatch,每天清理N天前完赛三方赛事信息执行异常，Exception:", e);
        }
        stopWatch.stop();
        log.info("linkId=【"+linkId+"】,cleanEndedDayThirdMatch,每天清理N天前完赛三方赛事信息执行用时{}毫秒",stopWatch.getTotalTimeMillis());
    }


    @Autowired
    private MatchEventInfoService matchEventInfoService;


    /**
     * 兜底清除历史赛事事件数据
     * */
    public void cleanMatchEventInfoData(String linkId,Integer matchNum,Long dayDateTime,String methodName,Integer deleteEvent) {
        if(deleteEvent == 1){
            //兜底清除历史赛事事件数据
            int matchEventNum = matchNum * TEN;
            int matchEventInfoNum = 0;
            for (String dataSourceCode: DataSourceCodeEnum.getEventCodeList()) {
                try{
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
//                //赛事盘中事件
//                MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
//                //分表字段，在删除的时候必须带上
//                matchEventInfoExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andModifyTimeLessThanOrEqualTo(dayDateTime);
//                long matchEventCount = matchEventInfoMapper.countByExample(matchEventInfoExample);
//                log.info("linkId=【"+linkId+"】,"+methodName+",每天清理N天前完赛三方赛事信息 : {}需清理三方赛事事件脏数据总条数：{},分页条数:{},dayDateTime:{}"
//                        ,dataSourceCode,matchEventCount,matchEventNum,dayDateTime);
//                if(matchEventCount > 0){
//                    PageHelper.startPage(ONE, matchEventNum);
//                    List<MatchEventInfo> resMatchEventInfoList = matchEventInfoMapper.selectByExample(matchEventInfoExample);
//                    if(!CollectionUtils.isEmpty(resMatchEventInfoList)){
//                        List<Long> eventIds = resMatchEventInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
//                        MatchEventInfoExample matchEventInfoDelExample = new MatchEventInfoExample();
//                        matchEventInfoDelExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andIdIn(eventIds);
//                        matchEventInfoNum = matchEventInfoMapper.deleteByExample(matchEventInfoDelExample);
//                    }
//                }
                    matchEventInfoNum = cleanMatchEventInfoData(dataSourceCode,dayDateTime,matchEventNum);
                    stopWatch.stop();
                    log.info("linkId=【"+linkId+"】,"+methodName+",每天清理N天前完赛三方赛事信息={},本次清理三方赛事事件脏数据条数={},耗时={}",dataSourceCode, matchEventInfoNum,stopWatch.getTotalTimeMillis());
                }catch (Exception e){
                    log.error("linkId=【"+linkId+"】,"+methodName+",每天清理N天前完赛三方赛事信息 : "+dataSourceCode+"本次清理三方赛事事件脏数据异常，Exception:",e);
                }
            }
        }
    }

    /**
     * 清理赛事事件信息
     * @param dataSourceCode 数据源编码
     * @param dayDateTime    某一天的时间戳
     * @param matchEventNum  本次需要清理的事件条数
     */
    public Integer cleanMatchEventInfoData(String dataSourceCode,Long dayDateTime,Integer matchEventNum){
        MatchEventInfoDetail matchEventInfoDetail = new MatchEventInfoDetail();
        matchEventInfoDetail.setTableName("match_event_info_"+dataSourceCode.toLowerCase(Locale.ROOT));
        matchEventInfoDetail.setDataSourceCode(dataSourceCode);
        matchEventInfoDetail.setDayDateTime(dayDateTime);
        matchEventInfoDetail.setSize(matchEventNum);
        PageHelper.startPage(ONE, matchEventNum);
        List<MatchEventInfo> resMatchEventInfoList = matchEventInfoService.getMatchEvenIdsByDayDateTime(matchEventInfoDetail);
        if(!CollectionUtils.isEmpty(resMatchEventInfoList)){
            List<Long> eventIds = resMatchEventInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
            MatchEventInfoExample matchEventInfoDelExample = new MatchEventInfoExample();
            matchEventInfoDelExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andIdIn(eventIds);
            return matchEventInfoMapper.deleteByExample(matchEventInfoDelExample);
        }
        return 0;
    }

}
