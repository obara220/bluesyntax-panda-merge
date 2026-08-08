package com.panda.merge.rocketmq.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.MatchEventMarketXCloseProcessor;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchCategoryConfigurationMessage;
import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  Riben
 * @Description :   开盘数据服务商及需要开盘玩法的配置
 * @since :  2020年12月9日13:43:05
 */
@Slf4j
@Component
@Validated
public class MatchCategoryConfigruationProcessor extends BaseProcessor {

    @Autowired
    MatchDataSourceWeightService dataSourceWeightService;

    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    StandardMatchInfoService standardMatchInfoService;

    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    StandardSportMarketSellService marketSellService;

    @Autowired
    MarketCategorySellService categorySellService;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    MatchEventMarketXCloseProcessor matchEventMarketXCloseProcessor;

    @Autowired
    RedisService redisService;
    @Autowired
    ThirdSportTournamentService thirdSportTournamentService;

    @Autowired
    ThirdMatchTeamRelationService thirdMatchTeamRelationService;

    @Autowired
    ThirdSportTeamService thirdSportTeamService;

    @Autowired
    LanguageInternationService languageInternationService;

    /**
     * 刷新玩法数据
     *
     * @param message
     */
    public void refreshMarketCategory(@Valid Request<MatchMarketCategoryConfigurationMessage> message) {
        String linkId = message.getLinkId();
        log.info("::{}::refreshMarketCategory，接收处理开始...", linkId);
        MatchMarketCategoryConfigurationMessage categoryConfigutaionInfo = message.getData();
        if (null == categoryConfigutaionInfo) {
            log.info("::{}::refreshMarketCategory ,传入参数信息不能为空！", linkId);
            return;
        }
        Long standardMatchId = categoryConfigutaionInfo.getStandardMatchId();
        Integer marketType = categoryConfigutaionInfo.getMarketType();
        Integer srWeight = categoryConfigutaionInfo.getSrWeight();
        Integer bcWeight = categoryConfigutaionInfo.getBcWeight();
        Integer bgWeight = categoryConfigutaionInfo.getBgWeight();
        Integer piWeight = categoryConfigutaionInfo.getPiWeight();
        Integer aoWeight = categoryConfigutaionInfo.getAoWeight();
        Integer beWeight = categoryConfigutaionInfo.getBeWeight();
        Integer txWeight = categoryConfigutaionInfo.getTxWeight();
        Integer lsWeight = categoryConfigutaionInfo.getLsWeight();
        StandardSportMarketSell marketSellInfo = marketSellService.refreshCache(standardMatchId);
        if (marketSellInfo == null) {
            log.info("::{}::{}:refreshMarketCategory ,找不到对应的开售赛事！", linkId, standardMatchId);
            return;
        }
        String dataSourceCode = marketType == 1 ? marketSellInfo.getPreMatchDataProviderCode() : marketSellInfo.getLiveMatchDataProviderCode();

        List<MatchCategoryConfigurationMessage> matchCategoryConfigurationMessageList = categoryConfigutaionInfo.getCategoryList();
        List<MarketCategorySell> existCategorySellConfigurations = categorySellService.getItem(standardMatchId,
                Integer.toString(marketType));
        List<Long> existCategoryIds =
                existCategorySellConfigurations.stream().map(e -> e.getMarketCategoryId()).collect(Collectors.toList());
        List<Long> addCategoryIds =matchCategoryConfigurationMessageList.stream().map(e -> e.getPlayId()).collect(Collectors.toList());
        List<Long> addCategoryIdList = new ArrayList<>();
        for(Long addCategoryId:addCategoryIds){
            if(existCategoryIds.contains(addCategoryId)){
                continue;
            }
            addCategoryIdList.add(addCategoryId);
        }
        if(addCategoryIdList.size() == 0){
            log.info("::{}::{}::{}refreshMarketCategory ,包含有数据库存在的玩法请风控处理！", linkId, standardMatchId, marketType);
            return;
        }
        log.info("::{}::{}::{}:refreshMarketCategory 本次新增的玩法ID:{}", linkId, standardMatchId, marketType, addCategoryIdList);
        List<MatchCategoryConfigurationMessage> saveMatchCategoryConfiguration = matchCategoryConfigurationMessageList.stream().filter(e -> addCategoryIdList.contains(e.getPlayId())).collect(Collectors.toList());
        //玩法开售对应的数据源
        String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchId + "_" + marketType;
        Map<String, String> stringStringMap = redisService.hGetAll(categoryRedisKey);
        Long now = TimeUtils.millsSecondsEast8ZoneGmt();
        List<MarketCategorySell> marketCategorySellList = new ArrayList<>();
        for(MatchCategoryConfigurationMessage categoryConfigInfo : saveMatchCategoryConfiguration){
            MarketCategorySell marketCategorySell = new MarketCategorySell();
            BeanUtils.copyProperties(categoryConfigInfo, marketCategorySell);
            marketCategorySell.setModifyTime(now);
            marketCategorySell.setLinkId(message.getLinkId());
            marketCategorySell.setMarketType(Integer.toString(marketType));
            marketCategorySell.setMatchId(standardMatchId);
            marketCategorySell.setMarketCategoryId(categoryConfigInfo.getPlayId());
            marketCategorySell.setDataSourceCode(dataSourceCode);
            marketCategorySell.setSrWeight(srWeight);
            marketCategorySell.setBcWeight(bcWeight);
            marketCategorySell.setBgWeight(bgWeight);
            marketCategorySell.setBeWeight(beWeight);
            marketCategorySell.setPiWeight(piWeight);
            marketCategorySell.setAoWeight(aoWeight);
            marketCategorySell.setLsWeight(lsWeight);
            marketCategorySell.setCreateTime(now);
            if (marketType == 1) {
                marketCategorySell.setSellStatus(SellStatusEnum.UNSOLD.getValue());
            } else {
               if(marketSellInfo.getLiveMatchSellStatus().equals(SellStatusEnum.SOLD.getValue())){
                   marketCategorySell.setSellStatus(SellStatusEnum.SOLD.getValue());
                   marketCategorySell.setSellTime(TimeUtils.millsSecondsEast8ZoneGmt());
                   //开售玩法刷新玩法开售对应的数据源
                   String marketCategoryIdStr = marketCategorySell.getMarketCategoryId().toString();
                   if (MapUtil.isNotEmpty(stringStringMap)) {
                       stringStringMap.put(marketCategoryIdStr, dataSourceCode);
                   }
               } else{
                   marketCategorySell.setSellStatus(SellStatusEnum.UNSOLD.getValue());
               }
                matchEventMarketXCloseProcessor.marketCategoryApportionToPeriod(message.getLinkId(), marketSellInfo.getSportId(), standardMatchId, marketCategorySell.getMarketCategoryId(), marketCategorySell.getAutoCloseMarket(), marketCategorySell.getMatchProgressTime());
            }
            if (MapUtil.isNotEmpty(stringStringMap)) {
                redisService.hSetAll(categoryRedisKey, stringStringMap, marketCacheTime(marketSellInfo.getBeginTime()));
            }
            marketCategorySell.setIsSend(Constant.INTEGER_FLAG_ZERO);
            marketCategorySell.setTournamentLevel(marketSellInfo.getTournamentLevel());
            marketCategorySellList.add(marketCategorySell);
        }
        categorySellService.saveBatch(standardMatchId, marketType, marketCategorySellList);
    }

    public void handleCategoryConfigrations(@Valid Request<MatchMarketCategoryConfigurationMessage> message) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Long standardMatchId = null;
        try{
            //校验LinkId和缓存中是否重复
            validateLinkId("Tournament_Template_Play",message);

            log.info("::{}::handleCategoryConfigrations，接收处理开始... {}",message.getLinkId(), JSONUtil.toJsonStr(message));
            MatchMarketCategoryConfigurationMessage categoryConfigutaionInfo = message.getData();
            if(null == categoryConfigutaionInfo){
                log.info("::{}::handleCategoryConfigrations ,传入参数信息不能为空！",message.getLinkId());
                return;
            }
            standardMatchId = categoryConfigutaionInfo.getStandardMatchId();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if(null == standardMatchInfo){
                log.info("::{}::handleCategoryConfigrations，根据标准赛事ID:{}未查到对应标准赛事信息!", message.getLinkId(), standardMatchId);
                return;
            }
            //刷新开售缓存并返回最新开售信息
            StandardSportMarketSell marketSellInfo = marketSellService.refreshCache(standardMatchId);
            if(null == marketSellInfo){
                log.info("::{}::handleCategoryConfigrations，根据标准赛事:{}未查到对应开售记录!", message.getLinkId(), standardMatchId);
                return;
            }
            /** 需要打印当前赛事开始记录的当前早盘/滚球的开售状态时间及数据服务商 */
            log.info("::{}::handleCategoryConfigrations，根据标准赛事：{} " +
                            "获取到的开售记录消息，早盘开售状态：{}，早盘开售时间：{}，早盘数据服务商：{}；滚球开售状态：{}，滚球开售时间{}，滚球数据服务商：{}",
                    message.getLinkId(), standardMatchId, marketSellInfo.getPreMatchSellStatus(),
                    marketSellInfo.getPreMatchTime(), marketSellInfo.getPreMatchDataProviderCode(),
                    marketSellInfo.getLiveMatchSellStatus(), marketSellInfo.getPreMatchTime(),
                    marketSellInfo.getLiveMatchDataProviderCode());


            //将赛事对应的数据权重及玩法开售配置入库
            this.handlDataSourceAndCategorySellConfigruations(message.getLinkId(), categoryConfigutaionInfo, message.getOperaterId(), standardMatchId, marketSellInfo, standardMatchInfo);
        }finally {
            stopWatch.stop();
            log.info("::{}::handleCategoryConfigrations，标准赛事:{}处理完毕...，耗时：{}",message.getLinkId(),standardMatchId,stopWatch.getTotalTimeMillis());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void handlDataSourceAndCategorySellConfigruations(String linkId,MatchMarketCategoryConfigurationMessage categoryConfigutaionInfo,Long operaterId,
                                                             Long standardMatchId,StandardSportMarketSell marketSellInfo,StandardMatchInfo standardMatchInfo) {
        //盘口类型1：早盘；0：滚球
        Integer marketType = categoryConfigutaionInfo.getMarketType();
        //风控操盘管理数据源
        String riskManagerCode = categoryConfigutaionInfo.getRiskManagerCode();
        //处理数据源权重使用状态配置信息
        MatchDataSourceWeight dataSourceWeight = dataSourceWeightService.getItem(standardMatchId, marketType);
        Long sportId = standardMatchInfo.getSportId();
        boolean basketballCodeFlag = isBasketballCodeFlag(sportId);
        if (null == dataSourceWeight && basketballCodeFlag && StringUtils.isNotBlank(linkId) && !linkId.endsWith("_trade")) {
            log.info("::{}::handleCategoryConfigrations，对应标准赛事:{},篮球赛事非操盘手下发的数据权重表未初始化需过滤", linkId, standardMatchId);
            return;
        }
        //开售时间(早盘或滚球)
        Long sellTime = null;
        Integer tournamentLevel = null;
        if (marketSellInfo != null) {
            tournamentLevel = marketSellInfo.getTournamentLevel();
            //根据 marketType 0 为滚球， 1 为早盘，从赛事开售记录中获取对应盘口类型开盘时间
            sellTime = Constant.INTEGER_FLAG_ZERO.equals(marketType) ? marketSellInfo.getLiveOddTime() : marketSellInfo.getPreMatchTime();
        }
        Long now = TimeUtils.millsSecondsEast8ZoneGmt();
        Integer srWeight = 0;
        Integer bcWeight = 0;
        Integer bgWeight = 0;
        Integer txWeight = 0;
        Integer rbWeight = 0;
        Integer pdWeight = 0;
        Integer piWeight = 0;
        Integer aoWeight = 0;
        Integer lsWeight = 0;
        Integer beWeight = 0;
        Integer koWeight = 0;
        Integer btWeight = 0;
        Integer odWeight = 0;
        Integer n01Weight = 0;
        Integer n02Weight = 0;
        Integer f01Weight = 0;
        Integer n03Weight = 0;
        Integer l02Weight = 0;
        //根据标准赛事对应的存在的商业数据源
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchId);
        //只有赛事权重配置不存在时，才根据赛事开盘配置数据获取需要使用的数据源
        String oddsUsingDataSource ="";
        if (null == dataSourceWeight) {
            //获取页面配置的数据源权重信息
            Map<String, Integer> dataWeightMap = this.getDataSourceMapFromMessage(categoryConfigutaionInfo);

            if(CollectionUtils.isEmpty(thirdMatchInfos)) {
                log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}无三方数据源,无法配置操盘手", linkId, standardMatchId);
                throw new RuntimeException(linkId + "::" + standardMatchId + "无三方数据源,无法配置操盘手");
            }
            Set<String> dataMatchSourceCodes = null;
            //如果滚球操盘配置，则需要过滤掉不支持滚球的第三方赛事
            //TX 可以配合报球版单独开售 DataSourceCodeEnum.TX.getCode().equalsIgnoreCase(thirdMatchInfos.get(0).getDataSourceCode())
            if(marketType == 0) {
//                if (thirdMatchInfos.size() == 1 && DataSourceCodeEnum.RB.getCode().equalsIgnoreCase(thirdMatchInfos.get(0).getDataSourceCode())) {
//                    log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}只有RB一个数据源,无法配置滚球操盘手", linkId, standardMatchId);
//                    throw new RuntimeException(linkId + "::" + standardMatchId + "赛事只有RB一个数据源,无法配置滚球操盘手!");
//                }
                dataMatchSourceCodes = this.getLiveMatchOddsDataSourcesByThirdMatchInfos(thirdMatchInfos);
            }else {
                dataMatchSourceCodes = this.getDataSourcesByThirdMatchInfos(thirdMatchInfos);
            }

            this.checkRiskManagerCode(linkId,riskManagerCode,dataMatchSourceCodes,standardMatchId);
            /**根据标准赛事对应的存在的商业数据源及页面数据源权重配置确定需要使用的数据源**/
            log.info("::{}::dataWeightMap，获取页面配置的数据源权重信息:{},:::riskManagerCode:{},dataMatchSourceCodes:::{}", linkId, dataWeightMap,riskManagerCode,dataMatchSourceCodes);
            oddsUsingDataSource = this.getUsingDataSourceByDataWeightAndMatchInfos(riskManagerCode, dataWeightMap, dataMatchSourceCodes);
            if (StringUtils.isBlank(oddsUsingDataSource)) {
                log.info("::{}::handleCategoryConfigrations，按照规则找不出对应的开售数据源，下挂的三方赛事为{}", linkId, JSON.toJSONString(dataMatchSourceCodes));
            }


            /** 赛事状态源及事件源默认与赔率源一致 **/
            String matchStatusUsingSource = oddsUsingDataSource;
            /**
             * 设置滚球操盘时剔除 TX/LS/N01/N02/N03，按原有逻辑确认赛事状态源及事件源
             * TX add_by riben 2020-12-23；LS add_by runner 2022-7-15；N系列对齐扩展
             ***/
            if (marketType == 0 && (dataMatchSourceCodes.contains(DataSourceCodeEnum.TX.getCode())
                    || dataMatchSourceCodes.contains(DataSourceCodeEnum.LS.getCode())
                    || dataMatchSourceCodes.contains(DataSourceCodeEnum.N01.getCode())
                    || dataMatchSourceCodes.contains(DataSourceCodeEnum.N02.getCode())
                    || dataMatchSourceCodes.contains(DataSourceCodeEnum.N03.getCode()))) {
                thirdMatchInfos = thirdMatchInfos.stream()
                        .filter(e -> !DataSourceCodeEnum.TX.getCode().equals(e.getDataSourceCode())
                                && !DataSourceCodeEnum.LS.getCode().equals(e.getDataSourceCode())
                                && !DataSourceCodeEnum.N01.getCode().equals(e.getDataSourceCode())
                                && !DataSourceCodeEnum.N02.getCode().equals(e.getDataSourceCode())
                                && !DataSourceCodeEnum.N03.getCode().equals(e.getDataSourceCode()))
                        .collect(Collectors.toList());
                dataMatchSourceCodes = this.getLiveMatchOddsDataSourcesByThirdMatchInfos(thirdMatchInfos);
                matchStatusUsingSource = this.getUsingDataSourceByDataWeightAndMatchInfos(riskManagerCode,
                        dataWeightMap, dataMatchSourceCodes);
            }

            // 需求2550范特西赛事事件源为FTS
            if(dataMatchSourceCodes.contains(DataSourceCodeEnum.FTS.getCode())) {
                matchStatusUsingSource = DataSourceCodeEnum.FTS.getCode();
            }

//            /**
//             * 设置滚球操盘时如果该赛事数据源包含BE，则剔除掉BE按原有逻辑确认赛事状态源及事件源
//             ***/
//            if(marketType == 0 && dataMatchSourceCodes.contains(DataSourceCodeEnum.BE.getCode())) {
//                thirdMatchInfos = thirdMatchInfos.stream().filter(e -> !DataSourceCodeEnum.BE.getCode().equals(e.getDataSourceCode())).collect(Collectors.toList());
//                dataMatchSourceCodes = this.getLiveMatchOddsDataSourcesByThirdMatchInfos(thirdMatchInfos);
//                matchStatusUsingSource = this.getUsingDataSourceByDataWeightAndMatchInfos(riskManagerCode,
//                        dataWeightMap, dataMatchSourceCodes);
//            }

            log.info("::{}::handleCategoryConfigrations，标准赛事:最终获取到的赔率源为：{},{},{}", linkId, oddsUsingDataSource, matchStatusUsingSource, dataWeightMap);
            this.updateMarketSellRecByRiskManagerCode(linkId,riskManagerCode, oddsUsingDataSource, matchStatusUsingSource, marketType, marketSellInfo, dataWeightMap);
            log.info("::{}::handleCategoryConfigrations，标准赛事：获取到的赔率源为{}", linkId, oddsUsingDataSource);

            //根据当前赛事正在使用的数据源，依次给数据源使用状态赋值，这三个数据源为1的标志确认使用的数据源
            srWeight = DataSourceCodeEnum.SR.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            bcWeight = DataSourceCodeEnum.BC.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            bgWeight = DataSourceCodeEnum.BG.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            txWeight = DataSourceCodeEnum.TX.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            rbWeight = DataSourceCodeEnum.RB.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            pdWeight = DataSourceCodeEnum.PD.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            piWeight = DataSourceCodeEnum.PI.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            aoWeight = DataSourceCodeEnum.AO.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            lsWeight = DataSourceCodeEnum.LS.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            beWeight = DataSourceCodeEnum.BE.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            btWeight = DataSourceCodeEnum.BT.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            koWeight = DataSourceCodeEnum.KO.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            odWeight = DataSourceCodeEnum.OD.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            n01Weight = DataSourceCodeEnum.N01.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            n02Weight = DataSourceCodeEnum.N02.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            f01Weight = DataSourceCodeEnum.F01.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            n03Weight = DataSourceCodeEnum.N03.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            l02Weight = DataSourceCodeEnum.L02.getCode().equals(oddsUsingDataSource) ? 1 : 0;
            //如果当前赛事开盘非MTS开盘，且赛事开售记录对应的盘口类型数据服务商为空，则将当前计算出的数据商更新给赛事开盘数据服务商,开售记录的赛事状态源，商业事件源编码
            dataSourceWeightService.save(srWeight, bcWeight, bgWeight, txWeight, rbWeight, pdWeight, piWeight, aoWeight, lsWeight,beWeight,koWeight,btWeight,odWeight,n01Weight,n02Weight,f01Weight,n03Weight,l02Weight, tournamentLevel, now, categoryConfigutaionInfo, operaterId);
            standardMatchInfo.setDataSourceCode(oddsUsingDataSource);
        }

        log.info("::{}::handleCategoryConfigrations，标准赛事：{}权重配置处理完成！", linkId, standardMatchId);
        List<MatchCategoryConfigurationMessage> configurationInfos = categoryConfigutaionInfo.getCategoryList();
        if(CollectionUtils.isEmpty(configurationInfos)) {
            log.info("::{}::handleCategoryConfigrations，standardMatchId： {} 赛事，盘口类型： {}无玩法配置数据！赛事玩法开售配置处理结束！", linkId,standardMatchId,marketType);
            return;
        }
        log.info("::{},srWeight:{}, bcWeight:{}, bgWeight:{}, txWeight:{}, piWeight:{}, aoWeight:{}, lsWeight:{},beWeight:{},koWeight:{},btWeight:{},odWeight:{},n01Weight:{},n02Weight:{},f01Weight:{},n03Weight:{},l02Weight:{}",
                linkId, srWeight, bcWeight, bgWeight, txWeight, piWeight, aoWeight, lsWeight,beWeight,koWeight,btWeight,odWeight,n01Weight,n02Weight,f01Weight,n03Weight,l02Weight);

        this.dealCategoryConfigurations(categoryConfigutaionInfo, linkId, sportId, srWeight, bcWeight, bgWeight, txWeight, piWeight, aoWeight, lsWeight,beWeight,koWeight,btWeight,odWeight,n01Weight,n02Weight,f01Weight, n03Weight, l02Weight, now,
                tournamentLevel, sellTime, oddsUsingDataSource, configurationInfos, thirdMatchInfos);
        log.info("::{}::handleCategoryConfigrations， 标准赛事：{}赛事玩法开售配置保存完成，权重数据源:::{}", linkId, standardMatchId,oddsUsingDataSource);
        //查询标准赛事开售信息 ，对应的三方开赛时间写入赛前转滚球缓存内，到开赛时间下发滚球标识
        this.refreshStandardMatchBeginTimeByMatchId(linkId, standardMatchInfo);
    }

    /**
     * 判断是否是篮球
     * @param sportId
     * @return
     */
    private boolean isBasketballCodeFlag(Long sportId) {
        boolean basketballCodeFlag = null != sportId && StandardSportTypeEnum.Basketball.getCode().equals(sportId) ? true : false;
        return basketballCodeFlag;
    }


    private Set<String> getLiveMatchOddsDataSourcesByThirdMatchInfos(List<ThirdMatchInfo> thirdMatchInfos) {
        List<ThirdMatchInfo> tempthirdMatchInfos =
                thirdMatchInfos.stream().filter(e -> e.getLiveOddSupport().equals(1) && dataSourceService.getCommerceDataSources().contains(e.getDataSourceCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tempthirdMatchInfos)) {
            tempthirdMatchInfos = thirdMatchInfos;
        }
        return getDataSourcesByThirdMatchInfos(tempthirdMatchInfos);
    }

    private Set<String> getDataSourcesByThirdMatchInfos(List<ThirdMatchInfo> thirdMatchInfos) {
        return thirdMatchInfos.stream().map(e -> e.getDataSourceCode().toUpperCase()).collect(Collectors.toSet());
    }

    public void checkRiskManagerCode(String linkId, String riskManagerCode, Set<String> dataMatchSourceCodes,Long standardMatchId){
        if(DataSourceCodeEnum.MTS.getCode().equalsIgnoreCase(riskManagerCode) && !dataMatchSourceCodes.contains(DataSourceCodeEnum.SR.getCode())) {
            log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}无SR数据源,无法配置MTS操盘自动开售", linkId, standardMatchId);
            throw new RuntimeException(linkId + "::" + standardMatchId + "赛事无SR数据源,无法配置MTS操盘自动开售!");
        }
        if(DataSourceCodeEnum.CTS.getCode().equalsIgnoreCase(riskManagerCode) && !dataMatchSourceCodes.contains(DataSourceCodeEnum.BC.getCode())) {
            log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}无BC数据源,无法配置CTS操盘自动开售", linkId, standardMatchId);
            throw new RuntimeException(linkId + "::" + standardMatchId + "赛事无BC数据源,无法配置CTS操盘自动开售!");
        }
        if(DataSourceCodeEnum.GTS.getCode().equalsIgnoreCase(riskManagerCode) && !dataMatchSourceCodes.contains(DataSourceCodeEnum.BG.getCode())) {
            log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}无BG数据源,无法配置GTS操盘自动开售", linkId, standardMatchId);
            throw new RuntimeException(linkId + "::" + standardMatchId + "赛事无BC数据源,无法配置GTS操盘自动开售!");
        }
        if(DataSourceCodeEnum.OTS.getCode().equalsIgnoreCase(riskManagerCode) && !dataMatchSourceCodes.contains(DataSourceCodeEnum.OD.getCode())) {
            log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}无OD数据源,无法配置OTS操盘自动开售", linkId, standardMatchId);
            throw new RuntimeException(linkId + "::" + standardMatchId + "赛事无BC数据源,无法配置OTS操盘自动开售!");
        }
        if(DataSourceCodeEnum.BTS.getCode().equalsIgnoreCase(riskManagerCode) && !dataMatchSourceCodes.contains(DataSourceCodeEnum.BE.getCode())) {
            log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}无BE数据源,无法配置BTS操盘自动开售", linkId, standardMatchId);
            throw new RuntimeException(linkId + "::" + standardMatchId + "赛事无BE数据源,无法配置BTS操盘自动开售!");
        }
        if(DataSourceCodeEnum.F2TS.getCode().equalsIgnoreCase(riskManagerCode) && !dataMatchSourceCodes.contains(DataSourceCodeEnum.F01.getCode())) {
            log.info("::{}::handleCategoryConfigrations，对应标准赛事:{}无F01数据源,无法配置F2TS操盘自动开售", linkId, standardMatchId);
            throw new RuntimeException(linkId + "::" + standardMatchId + "赛事无BE数据源,无法配置BTS操盘自动开售!");
        }
    }
    /**
     * 如果当前赛事开盘非MTS开盘，且赛事开售记录对应的盘口类型数据服务商为空， 则将当前计算出的数据商更新给赛事开盘数据服务商
     *
     * @param riskManagerCode
     * @param usingDataSource
     * @param matchStatusUsingSource
     * @param marketType
     * @param marketSellInfo
     */
    private void updateMarketSellRecByRiskManagerCode(String linkId,String riskManagerCode, String usingDataSource,
                                                      String matchStatusUsingSource, Integer marketType,
                                                      StandardSportMarketSell marketSellInfo, Map<String, Integer> dataWeightMap) {

        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(marketSellInfo.getMatchInfoId());

        String matchSellProvideDataSource = Constant.INTEGER_FLAG_ZERO == marketType ?
                marketSellInfo.getLiveMatchDataProviderCode() : marketSellInfo.getPreMatchDataProviderCode();
        StandardSportMarketSell updateMarketSellObj = new StandardSportMarketSell();
        updateMarketSellObj.setId(marketSellInfo.getId());
        updateMarketSellObj.setMatchInfoId(marketSellInfo.getMatchInfoId());

        if (!DataSourceCodeEnum.MTS.getCode().equalsIgnoreCase(riskManagerCode) && StringUtils.isEmpty(matchSellProvideDataSource)) {
            //根据marketType，把设置信息中使用的数据源更新到该赛事对应开售记录的早盘或滚球数据提供源
            if (marketType == 1) {
                updateMarketSellObj.setPreMatchDataProviderCode(usingDataSource);
                updateMarketSellObj.setPreUsedOddsCodes(usingDataSource);
            }
            if (marketType == 0) {
                updateMarketSellObj.setLiveMatchDataProviderCode(usingDataSource);
                updateMarketSellObj.setLiveUsedOddsCodes(usingDataSource);
            }
        }
        //如果是滚球操盘，或者早盘操盘但未设置滚球操盘数据服务商则更新开售记录的赛事状态源，商业事件源编码
        if (marketType == 0 || StringUtils.isEmpty(marketSellInfo.getLiveMatchDataProviderCode())) {
            String usinngDataSource = matchStatusUsingSource;
            /**matchStatusUsingSource默认为赔率的数据源（matchStatusUsingSource默认为赔率的数据源 == usingDataSource），
             * 若当前的赛事包含TX数据源，则赛事状态源及商业事件源用剔除TX后获取的数据源
             * add_by Riben 2020-12-23
             **/
            updateMarketSellObj.setMatchStatusSourceCode(matchStatusUsingSource);
            log.info("{} 赛事模版事件源:::{}",linkId,matchStatusUsingSource);
            if (!CollectionUtils.isEmpty(thirdMatchInfos)){
                if (StringUtils.isNotBlank(matchStatusUsingSource) && matchStatusUsingSource.equals(DataSourceCodeEnum.AO.code)) {
                    List<ThirdMatchInfo> noAoThirdMatchs = thirdMatchInfos.stream().filter(e -> !e.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(noAoThirdMatchs)){
                        Set<String> dataMatchSourceCodes = this.getLiveMatchOddsDataSourcesByThirdMatchInfos(noAoThirdMatchs);
                        Optional<Map.Entry<String, Integer>> maxWeightCode = Maps.filterKeys(dataWeightMap, e -> dataMatchSourceCodes.contains(e)).entrySet().stream().min(Map.Entry.comparingByValue(Comparator.naturalOrder()));
                        if (maxWeightCode.isPresent()) {
                            usinngDataSource = maxWeightCode.get().getKey();
                            updateMarketSellObj.setMatchStatusSourceCode(usinngDataSource);
                            log.info("{} 非A01赛事模版事件源:::{}", linkId, usinngDataSource);
                        }
                    }
                }
            }
            this.setBusinessEvent(linkId, matchStatusUsingSource, marketSellInfo, updateMarketSellObj, usinngDataSource);
        }
        updateMarketSellObj.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        marketSellService.update(updateMarketSellObj);
    }

    /**
     *根据赔率源查询三方赛事是否可切换事件源
     * @param linkId
     * @param matchStatusUsingSource
     * @param marketSellInfo
     * @param updateMarketSellObj
     * @param usinngDataSource
     */
    private void setBusinessEvent(String linkId, String matchStatusUsingSource, StandardSportMarketSell marketSellInfo, StandardSportMarketSell updateMarketSellObj, String usinngDataSource) {
        log.info("::{}::setBusinessEvent 能否切换事件源校验,matchStatusUsingSource:{},usinngDataSource:{}", linkId, matchStatusUsingSource, usinngDataSource);
        String businessEventCode = "";
        try {
            if (StringUtils.isBlank(matchStatusUsingSource) && StringUtils.isBlank(usinngDataSource)) {
                return;
            }
            businessEventCode = matchStatusUsingSource.equals(usinngDataSource) ? matchStatusUsingSource : usinngDataSource;
            log.info("::{}::setBusinessEvent 能否切换事件源校验,businessEventCode:{}", linkId, businessEventCode);
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(marketSellInfo.getMatchInfoId());
            if (null != standardMatchInfo && standardMatchInfo.getMatchStatus().equals(MatchStatusEnum.Live.value)) {
                String thirdMatchPeriod = null;
                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), businessEventCode);
                if (null != thirdMatchInfo) {
                    thirdMatchPeriod = thirdMatchInfo.getMatchPeriod();
                }
                log.info("::{}::setBusinessEvent 能否切换事件源校验,businessEventCode:{},matchPeriod:{}", linkId, businessEventCode, thirdMatchPeriod);
                if (StringUtils.isNotBlank(thirdMatchPeriod) && !"0".equals(thirdMatchPeriod)) {
                    updateMarketSellObj.setBusinessEvent(businessEventCode);
                    log.info("::{}::setBusinessEvent matchId:{},businessEvent:{},matchPeriod:{},赛事可切换事件源", linkId, marketSellInfo.getMatchInfoId(), businessEventCode, thirdMatchInfo.getMatchPeriod());
                }
            } else {
                updateMarketSellObj.setBusinessEvent(businessEventCode);
                log.info("::{}::setBusinessEvent matchId:{},businessEvent:{},matchStatus:{},赛事可切换事件源", linkId, marketSellInfo.getMatchInfoId(), businessEventCode, standardMatchInfo.getMatchStatus());
            }
        } catch (Exception e) {
            log.error("::{}::setBusinessEvent matchId:{},businessEvent:{},赛事切换事件源异常,异常原因:{}", linkId, marketSellInfo.getMatchInfoId(), businessEventCode, e.getMessage(), e);
        }
    }

    /**
     * 保存玩法配置信息
     *
     * @param categoryConfigutaionInfo
     * @param linkId
     * @param srWeight
     * @param bcWeight
     * @param bgWeight
     * @param now
     * @param touranmentLevel
     * @param sellTime
     * @param standardMatchDataSource
     * @param categoryConfigInfos
     */
    private void dealCategoryConfigurations(MatchMarketCategoryConfigurationMessage categoryConfigutaionInfo,
                                            String linkId, Long sportId, Integer srWeight, Integer bcWeight, Integer bgWeight, Integer txWeight, Integer piWeight, Integer aoWeight, Integer lsWeight,Integer beWeight,
            Integer koWeight,Integer btWeight,Integer odWeight,Integer n01Weight,Integer n02Weight,Integer f01Weight,Integer n03Weight,Integer l02Weight,
                                            Long now, Integer touranmentLevel, Long sellTime,
                                            String standardMatchDataSource,
                                            List<MatchCategoryConfigurationMessage> categoryConfigInfos,
                                            List<ThirdMatchInfo> thirdMatchInfos) {
        //如果存在玩法则更新
        Long standardMatchId = categoryConfigutaionInfo.getStandardMatchId();
        Integer marketType = categoryConfigutaionInfo.getMarketType();

        List<MarketCategorySell> existCategorySellConfigurations = categorySellService.getItem(standardMatchId,
                Integer.toString(marketType));

        if (!CollectionUtils.isEmpty(existCategorySellConfigurations)) {
            List<Long> existCategoryIds = existCategorySellConfigurations.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toList());
            Map<Long, List<MatchCategoryConfigurationMessage>> updateCategoryInfos =
                    categoryConfigInfos.stream().filter(e -> existCategoryIds.contains(e.getPlayId())).collect(Collectors.groupingBy(MatchCategoryConfigurationMessage::getPlayId));

            for (MarketCategorySell categoryItem : existCategorySellConfigurations) {
                List<MatchCategoryConfigurationMessage> tempMessages = updateCategoryInfos.get(categoryItem.getMarketCategoryId());

                if (CollectionUtils.isEmpty(tempMessages)) {
                    continue;
                }
                MatchCategoryConfigurationMessage configParam = tempMessages.get(0);
                BeanUtil.copyProperties(configParam,categoryItem, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                //判断是否修改最大最小赔率，风控忽略的配置会给null ，直接给对应的配置最大值
                if ((configParam.getMinBallHead() == null && configParam.getMaxBallHead() != null)
                        || (configParam.getMinBallHead() != null && configParam.getMaxBallHead() == null)) {
                    if (configParam.getMinBallHead() == null) {
                        categoryItem.setMinBallHead(new BigDecimal("-999"));
                    }
                    if (configParam.getMaxBallHead() == null) {
                        categoryItem.setMaxBallHead(new BigDecimal("999"));
                    }
                }
                categoryItem.setLinkId(linkId);
                categoryItem.setSellTime(sellTime);
                //业务需求,无论是否是MTS 操盘，玩法开售都不自动开售,如果已存在玩法开售记录，则保持记录原开售状态  modify_by Riben 2020-11-11
                //                if(DataSourceCodeEnum.MTS.getCode().equalsIgnoreCase(riskManagerCode)){
                //                    categoryItem.setSellStatus(SellStatusEnum.SOLD.getValue());
                //                }
                //                categoryItem.setSrWeight(srWeight);
                //                categoryItem.setBcWeight(bcWeight);
                //                categoryItem.setBgWeight(bgWeight);
                categorySellService.update(categoryItem);
                matchEventMarketXCloseProcessor.marketCategoryApportionToPeriod(linkId, sportId, standardMatchId, categoryItem.getMarketCategoryId(), categoryItem.getAutoCloseMarket(), categoryItem.getMatchProgressTime());
            }

            //过滤出DB中已存在的配置信息，让下面不再做重复新增
            categoryConfigInfos =
                    categoryConfigInfos.stream().filter(e -> !existCategoryIds.contains(e.getPlayId())).collect(Collectors.toList());
        }
        List<String> thirdDataSourceCodeList =
                thirdMatchInfos.stream().map(ThirdMatchInfo::getDataSourceCode).distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(categoryConfigInfos)) {
            List<MarketCategorySell> categorySellConfigurations = new ArrayList<>();

            for (MatchCategoryConfigurationMessage categoryConfigInfo : categoryConfigInfos) {
                MarketCategorySell configuration = new MarketCategorySell();
                BeanUtils.copyProperties(categoryConfigInfo, configuration);
                configuration.setMarketCategoryId(categoryConfigInfo.getPlayId());
                configuration.setLinkId(linkId);
                configuration.setMatchId(standardMatchId);
                //业务需求,无论是否是MTS 操盘，玩法开售都不自动开售  modify_by Riben 2020-11-11
                configuration.setSellStatus(SellStatusEnum.UNSOLD.getValue());
                configuration.setIsSend(Constant.INTEGER_FLAG_ZERO);
                configuration.setSellTime(sellTime);
                configuration.setTournamentLevel(touranmentLevel);
                configuration.setMarketType(Integer.toString(marketType));
                configuration.setCreateTime(now);
                configuration.setModifyTime(now);
                if (StringUtils.isNotBlank(categoryConfigInfo.getDataSource()) && thirdDataSourceCodeList.contains(categoryConfigInfo.getDataSource())) {
                    configuration.setDataSourceCode(categoryConfigInfo.getDataSource());
                    configuration.setSrWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.SR.getCode()) ? 1 : 0);
                    configuration.setBcWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.BC.getCode()) ? 1 : 0);
                    configuration.setBgWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.BG.getCode()) ? 1 : 0);
                    configuration.setAoWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.AO.getCode()) ? 1 : 0);
                    configuration.setPiWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.PI.getCode()) ? 1 : 0);
                    configuration.setPiWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.LS.getCode()) ? 1 : 0);
                    configuration.setBeWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.BE.getCode()) ? 1 : 0);
                    configuration.setKoWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.KO.getCode()) ? 1 : 0);
                    configuration.setBtWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.BT.getCode()) ? 1 : 0);
                    configuration.setRbWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.RB.getCode()) ? 1 : 0);
                    configuration.setOdWeight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.OD.getCode()) ? 1 : 0);
                    configuration.setN01Weight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.N01.getCode()) ? 1 : 0);
                    configuration.setN02Weight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.N02.getCode()) ? 1 : 0);
                    configuration.setF01Weight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.F01.getCode()) ? 1 : 0);
                    configuration.setN03Weight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.N03.getCode()) ? 1 : 0);
                    configuration.setL02Weight(categoryConfigInfo.getDataSource().equals(DataSourceCodeEnum.L02.getCode()) ? 1 : 0);
                } else {
                    configuration.setDataSourceCode(standardMatchDataSource);
                    configuration.setSrWeight(srWeight);
                    configuration.setBcWeight(bcWeight);
                    configuration.setBgWeight(bgWeight);
                    configuration.setAoWeight(aoWeight);
                    configuration.setPiWeight(piWeight);
                    configuration.setLsWeight(lsWeight);
                    configuration.setBeWeight(beWeight);
                    configuration.setKoWeight(koWeight);
                    configuration.setBtWeight(btWeight);
                    configuration.setTxWeight(txWeight);
                    configuration.setOdWeight(odWeight);
                    configuration.setN01Weight(n01Weight);
                    configuration.setN02Weight(n02Weight);
                    configuration.setF01Weight(f01Weight);
                    configuration.setN03Weight(n03Weight);
                    configuration.setL02Weight(l02Weight);
                   // configuration.setr(btWeight);
                }
                // 需要校验玩法数据源是否为空,并发下发分时节点存在影响
                if (StringUtils.isNotBlank(configuration.getDataSourceCode())) {
                    categorySellConfigurations.add(configuration);
                }
                matchEventMarketXCloseProcessor.marketCategoryApportionToPeriod(linkId, sportId, standardMatchId, configuration.getMarketCategoryId(), configuration.getAutoCloseMarket(), configuration.getMatchProgressTime());
            }
            if(!categorySellConfigurations.isEmpty()&&!categoryConfigutaionInfo.getRiskManagerCode().equals(DataSourceCodeEnum.PA.getCode())&&!CollectionUtils.isEmpty(categoryConfigutaionInfo.getCategoryIds4405())){
                handleCategorySell(categorySellConfigurations,categoryConfigutaionInfo.getCategoryIds4405());
            }
            if (!categorySellConfigurations.isEmpty()) {
                categorySellService.saveBatch(standardMatchId, marketType, categorySellConfigurations);
            }
        }
        log.info("standardMatchId： {} 赛事，盘口类型： {}, 玩法配置数据入库完成！", standardMatchId, marketType);
    }

    public void handleCategorySell(List<MarketCategorySell> categorySellConfigurations,List<Long> categoryIds4405){
        for(MarketCategorySell categorySell:categorySellConfigurations){
            if(categoryIds4405.contains(categorySell.getMarketCategoryId())){
                categorySell.setDataSourceCode(DataSourceCodeEnum.AO.getCode());
            }
        }
    }

    /**
     * 获取页面配置的数据源权重信息
     *
     * @param categoryConfigutaionInfo
     * @return
     */
    private Map<String, Integer> getDataSourceMapFromMessage(MatchMarketCategoryConfigurationMessage categoryConfigutaionInfo) {
        Method[] methods = categoryConfigutaionInfo.getClass().getMethods();
        Map<String, Integer> dataWeightMap = new TreeMap<>();
        Arrays.stream(methods).filter(e -> e.getName().startsWith("get") && e.getName().endsWith("Weight")).forEach(e -> {
            try {
                if (e.getName().startsWith("getN0") && e.getName().endsWith("Weight")){

                    dataWeightMap.put(e.getName().substring(3, 6).toUpperCase(),
                            (Integer) e.invoke(categoryConfigutaionInfo, null));
                }else if (e.getName().startsWith("getF0") && e.getName().endsWith("Weight")){
                    dataWeightMap.put(e.getName().substring(3, 6).toUpperCase(),
                            (Integer) e.invoke(categoryConfigutaionInfo, null));
                } else if (e.getName().startsWith("getL0") && e.getName().endsWith("Weight")){
	                dataWeightMap.put(e.getName().substring(3, 6).toUpperCase(),
			                (Integer) e.invoke(categoryConfigutaionInfo, null));
                }
				else {
                    dataWeightMap.put(e.getName().substring(3, 5).toUpperCase(),
                            (Integer) e.invoke(categoryConfigutaionInfo, null));
                }

            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        });
        return dataWeightMap;
    }

    /**
     * 根据标准赛事对应的存在的商业数据源及页面数据源权重配置确定需要使用的数据源
     *
     * @param dataWeightMap
     * @param dataMatchSourceCodes
     * @return
     */
    private String getUsingDataSourceByDataWeightAndMatchInfos(String riskManagerCode,
                                                               Map<String, Integer> dataWeightMap,
                                                               Set<String> dataMatchSourceCodes) {
        /**如果是MTS 数据源，且改标准赛事下挂载了SR 的数据源则该赛事开盘使用SR 数据源，
         * 非MTS操盘则根据标准赛事对应的存在的商业数据源及页面数据源权重配置确定需要使用的数据源
         * 2020-11-22 modify_by riben
         **/
        if (DataSourceCodeEnum.MTS.getCode().equalsIgnoreCase(riskManagerCode)) {
            return DataSourceCodeEnum.SR.getCode();
        }
        if (DataSourceCodeEnum.CTS.getCode().equalsIgnoreCase(riskManagerCode)) {
            return DataSourceCodeEnum.BC.getCode();
        }
        if (DataSourceCodeEnum.BTS.getCode().equalsIgnoreCase(riskManagerCode)) {
            return DataSourceCodeEnum.BE.getCode();
        }
        if (DataSourceCodeEnum.GTS.getCode().equalsIgnoreCase(riskManagerCode)) {
            return DataSourceCodeEnum.BG.getCode();
        }
        if (DataSourceCodeEnum.OTS.getCode().equalsIgnoreCase(riskManagerCode)) {
            return DataSourceCodeEnum.OD.getCode();
        }
        if (DataSourceCodeEnum.FTS.getCode().equalsIgnoreCase(riskManagerCode)) {
            return DataSourceCodeEnum.AO.getCode();
        }
        if (DataSourceCodeEnum.F2TS.getCode().equalsIgnoreCase(riskManagerCode)) {
            return DataSourceCodeEnum.F01.getCode();
        }

        String usinngDataSource = null;
        Optional<Map.Entry<String, Integer>> maxWeightCode = Maps.filterKeys(dataWeightMap, e -> dataMatchSourceCodes.contains(e)).entrySet().stream().min(Map.Entry.comparingByValue(Comparator.naturalOrder()));
            if (maxWeightCode.isPresent()) {
                usinngDataSource = maxWeightCode.get().getKey();
            }
        return usinngDataSource;
    }

    /**
     * 批量新增国际化
     *
     * @param newNameCode
     * @param oldNameCode
     * @param languageInternationAllList
     */
    private void addLanguageInternation(Long newNameCode, Long oldNameCode, List<LanguageInternation> languageInternationAllList) {
        List<LanguageInternation> languageInternationList = languageInternationService.getLanguageInternationByNameCode(oldNameCode);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(languageInternationList)) {
            return;
        }
        for (LanguageInternation languageInternation : languageInternationList) {
            languageInternation.setId(null);
            languageInternation.setNameCode(newNameCode);
            languageInternation.setDataSourceCode(DataSourceCodeEnum.PD.code);
            languageInternation.setCreateTime(Calendar.getInstance().getTimeInMillis());
            languageInternation.setModifyTime(Calendar.getInstance().getTimeInMillis());
            languageInternationAllList.add(languageInternation);
        }
    }
}
