package com.panda.merge.rocketmq.processor;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.dto.message.CategoryMessage;
import com.panda.merge.dto.message.ChangeSoldMessage;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.ChangeSoldProducer;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Validated
public class ChangeSoldMessageProcessor extends BaseProcessor {
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ChangeSoldProducer changeSoldProducer;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;
    @Autowired
    private ConfigTradeMarketLogService configTradeMarketLogService;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;
    @Lazy
    @DubboReference
    private ITradeMarketConfigApi iTradeMarketConfigApi;
    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private SoldMessageToOddsProcessor soldMessageToOddsProcessor;

    public void changeSoldMessage(@Valid Request<ChangeSoldMessage> request)
    {
        StopWatch stopWatch = new StopWatch("changeSoldMessage_"+UUIdUtils.getId());
        stopWatch.start();
        String linkId = request.getLinkId();
        validateLinkId("changeSoldMessage", request);
        log.info("::{}::changeSoldMessage逻辑处理开始，request={}", linkId, JSON.toJSONString(request));
        ChangeSoldMessage changeSoldMessage = request.getData();
        ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(changeSoldMessage.getOldDataSource(),changeSoldMessage.getOldThirdMatchId());
        if (null == oldThirdMatchInfo)
        {
            log.info("::{}::changeSoldMessage，查询三方赛事为空,三方赛事id={}", linkId,changeSoldMessage.getOldThirdMatchId());
            return;
        }
        StandardMatchInfo oldStandardMatchInfo = standardMatchInfoService.getItem(changeSoldMessage.getMatchId());
        if (null == oldStandardMatchInfo)
        {
            log.info("::{}::changeSoldMessage，查询标准赛事为空,标准赛事id={}", linkId,changeSoldMessage.getMatchId());
            return;
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(changeSoldMessage.getMatchId());
        if (null == standardSportMarketSell)
        {
            log.info("::{}::changeSoldMessage，查询标准赛事盘口开售信息为空,标准赛事id={}", linkId,changeSoldMessage.getMatchId());
            return;
        }
        Map<Long, String> marketCategoryIdMap = new HashMap<>();
        if (CollectionUtils.isEmpty(changeSoldMessage.getMarketCategoryIds()))
        {
            log.info("::{}::changeSoldMessage，赛事级别开售玩法集合为空，赛事级别为空", linkId);
            return;
        }
        String dataSourceCode = changeSoldMessage.getDataSource();
        changeSoldMessage.getMarketCategoryIds().forEach(e->{
            marketCategoryIdMap.put(e,dataSourceCode);
        });
        int marketType = changeSoldMessage.getMarketType();
        //TODO:
        // 1.切早盘肯定要关盘
        // 2.切滚球，当已经进入滚球或者新切入的数据源有滚球，需要关盘，并且下发oddslive=1
        //1.关盘，切换滚球的时候如果还没有滚球不用关盘，其余情况都需要关闭
        /*if (changeSoldMessage.getMarketType() == 0)
        {
            String switchLiveRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + changeSoldMessage.getMatchId();
            Object obj = redisService.get(switchLiveRedisKey);
            List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketService.getItemList(oldThirdMatchInfo.getId(),0);
            if (ObjectUtil.isNotEmpty(obj) || !CollectionUtils.isEmpty(thirdSportMarkets))
            {
                thirdMatchMarketProcessor.sendOddsLive(linkId, changeSoldMessage.getDataSource(), changeSoldMessage.getMatchId(), oldThirdMatchInfo.getSportId(), false, request.getDataSourceTime());
                //oldClose(linkId,oldThirdMatchInfo,changeSoldMessage.getMarketType(),changeSoldMessage.getMatchId());
            }
        }
        else
        {
            //oldClose(linkId,oldThirdMatchInfo,changeSoldMessage.getMarketType(),changeSoldMessage.getMatchId());
        }*/

        soldMessageToOddsProcessor.soldHandler(request.getLinkId(), oldStandardMatchInfo, standardSportMarketSell, marketCategoryIdMap, marketType, true, false);
        //3.下发新赔率
        //newOpen(linkId,changeSoldMessage.getDataSource(),changeSoldMessage.getMatchId(),changeSoldMessage.getMarketType(),changeSoldMessage.getMarketCategoryIds());
        //统计处理耗时
        stopWatch.stop();
        paDataServiceLogProducer.sendPaDataServiceLog(
                getPaDataServiceLogDTO(request.getLinkId(),"odds-admin","CHANGE_SOLD_MESSAGE","切换开售数据源",
                        stopWatch.getTotalTimeMillis(),200,null)
        );
    }

    /**
     * 关闭三方赛事盘口,关盘前先清水差，盘口差
     * @param linkId
     * @param oldThirdMatchInfo
     * @param marketType
     * @return
     */
    public boolean oldClose(String linkId, ThirdMatchInfo oldThirdMatchInfo, int marketType,Long matchId)
    {
            //清除水差 盘口差
            delDiffByMatchInfoId(linkId, matchId, oldThirdMatchInfo.getSportId());
            //关盘
            /*TradeMarketConfigDTO tradeMarketConfigDTO = new TradeMarketConfigDTO();
            tradeMarketConfigDTO.setSourceSystem(Constant.TRADE_MARKET_CONFIG.SOURCE_SYSTEM.THIRD_DATA_SOURCE);
            tradeMarketConfigDTO.setLevel(Constant.TRADE_MARKET_CONFIG.LEVEL.MATCH);
            tradeMarketConfigDTO.setActive(1);
            tradeMarketConfigDTO.setMarketStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            tradeMarketConfigDTO.setTradeType(null);
            tradeMarketConfigDTO.setModifyTime(System.currentTimeMillis());
            tradeMarketConfigDTO.setMatchType("0");
            tradeMarketConfigDTO.setAddition1(oldThirdMatchInfo.getDataSourceCode());
            tradeMarketConfigDTO.setTargetId(oldThirdMatchInfo.getThirdMatchSourceId());
            tradeMarketConfigDTO.setConfigId(oldThirdMatchInfo.getThirdMatchSourceId());
            tradeMarketConfigDTO.setAddition3(String.valueOf(marketType));

            Request<TradeMarketConfigDTO> tradeDto = new Request<>();
            tradeDto.setData(tradeMarketConfigDTO);
            tradeDto.setDataSourceTime(System.currentTimeMillis());
            tradeDto.setLinkId(linkId + "_ClOSE");
            Response response = putTradeMarketConfigChangeSold(tradeDto);
            log.info("::{}::changeSoldMessage，原始三方赛事盘口关盘处理结果 response={}", linkId, response);*/

            return true;
    }

    /**
     * 对标准赛事进行操盘赛事级别封盘操作，即修改operateMatchStatus=1
     * @param matchId
     */
    public void operateMatchStatus(Long matchId)
    {
        //用于本次修改的标准赛事
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        upStandardMatchInfo.setId(matchId);
        //修改标准赛事表
        upStandardMatchInfo.setOperateMatchStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
        //修改标准赛事
        standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
    }
    /**
     * 对于新的数据源走开售流程
     * @param linkId
     * @param dataSourceCode
     * @param matchId
     * @param marketType
     */
    public void newOpen(String linkId, String dataSourceCode, Long matchId, int marketType, List<Long> marketCategoryIds)
    {
        standardSportMarketSellService.refreshCache(matchId);
        List<CategoryMessage> list = new ArrayList<>();
        marketCategoryIds.forEach(e->{
            CategoryMessage categoryMessage = new CategoryMessage();
            categoryMessage.setCategoryId(e);
            categoryMessage.setDataSourceCode(dataSourceCode);
            list.add(categoryMessage);
        });
        //2.再开售
        Request<SoldMessage> soldMessageRequest = new Request<SoldMessage>();
        soldMessageRequest.setLinkId(linkId+"_SOLD");
        SoldMessage soldMessage = new SoldMessage();
        soldMessage.setMatchId(matchId);
        soldMessage.setMarketType(marketType);
        soldMessage.setIsOutRight("0");
        soldMessage.setMarketCategoryIds(list);
        soldMessage.setRiskManagerCode(dataSourceCode);
        soldMessage.setDataSource(dataSourceCode);
        soldMessageRequest.setDataSourceTime(System.currentTimeMillis()+100);
        soldMessageRequest.setData(soldMessage);
        soldMessageRequest.setDataSourceCode(dataSourceCode);
        changeSoldProducer.sendChangeSoldMessageToMQ(soldMessageRequest);
    }
    private Response putTradeMarketConfigChangeSold(Request<TradeMarketConfigDTO> request) {
        validateLinkId("putTradeMarketConfigChangeSold", request);
        log.info("::{}::putTradeMarketConfigChangeSold入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeMarketConfigDTO tradeMarketConfigDTO =  request.getData();
        configTradeMarketLogService.create(request.getLinkId(), tradeMarketConfigDTO);
        //--------如果为三方数据源配置-----------
        if(Constant.TRADE_MARKET_CONFIG.SOURCE_SYSTEM.THIRD_DATA_SOURCE.equals(tradeMarketConfigDTO.getSourceSystem())){
            //数据商当前只有赛事级别配置
            if(!Constant.TRADE_MARKET_CONFIG.LEVEL.MATCH.equals(tradeMarketConfigDTO.getLevel())){
                log.info("::{}::putTradeMarketConfigChangeSold,当前仅支持数据源的赛事级别配置",request.getLinkId());
                return Response.failed("当前仅支持数据源的赛事级别配置");
            }
            //对三方源的开盘配置不处理
            if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE.equals(tradeMarketConfigDTO.getMarketStatus())) {
                log.info("::{}::putTradeMarketConfigChangeSold,三方源的赛事开盘配置不处理", request.getLinkId());
                return Response.failed("三方源的赛事开盘配置不处理");
            }
            //判断赛事类型
            boolean isOutRight = StringUtils.equals("1", tradeMarketConfigDTO.getMatchType());
            ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfo(isOutRight, tradeMarketConfigDTO.getAddition1(), tradeMarketConfigDTO.getTargetId());
            if(thirdMatchInfo == null){
                log.info("::{}::putTradeMarketConfigChangeSold,数据源数据TargetID对应的三方赛事未找到，三方赛事id:{}",request.getLinkId(),tradeMarketConfigDTO.getTargetId());
                return Response.failed("数据源数据TargetID对应的三方赛事未找到");
            }
            //三方盘口处理
            log.info("{}::更新三方盘口状态为{}，三方赛事Id={}", request.getLinkId(),tradeMarketConfigDTO.getMarketStatus(), tradeMarketConfigDTO.getTargetId());
            StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutRight, thirdMatchInfo.getReferenceId());
            if(standardMatchInfo == null){
                log.info("::{}::putTradeMarketConfigChangeSold,数据源数据TargetID对应的标准赛事未找到，标准赛事id:{}",request.getLinkId(),thirdMatchInfo.getReferenceId());
                return Response.failed("数据源数据TargetID对应的标准赛事未找到");
            }
            //获取开售信息
            StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(isOutRight, thirdMatchInfo.getReferenceId());
            if(standardSportMarketSell == null){
                log.info("::{}::putTradeMarketConfigChangeSold,数据源数据TargetID对应的标准赛事未开售，标准赛事id:{}",request.getLinkId(),thirdMatchInfo.getReferenceId());
                return Response.failed("数据源数据TargetID对应的标准赛事未开售");
            }

            //以下和三方盘口接口有并发问题，这里需要以赛事维度加redis锁
            String lockValue = UUIdUtils.getId()+"_"+request.getLinkId();
            String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
            log.info("::{}::putTradeMarketConfigChangeSold,redisLocKey:{},准备获取分布式锁,lockValue:{}", request.getLinkId(),redisLocKey, lockValue);
            redisService.tryLock(redisLocKey, lockValue, 10, 8);
            log.info("::{}::putTradeMarketConfigChangeSold,redisLocKey:{},获取到分布式锁,lockValue:{}", request.getLinkId(),redisLocKey, lockValue);
            try{
                Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = iTradeMarketConfigApi.getStringStandardMarketDataMessageMap(new HashSet<>(), request.getLinkId(), standardMatchInfo, standardSportMarketSell);
                String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + tradeMarketConfigDTO.getMatchType();
                Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
                if (MapUtils.isNotEmpty(stringHashMap)) {
                    for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                        String marketCategoryId = dataSourceCodeEntry.getKey();
                        String dataSourceCode = dataSourceCodeEntry.getValue();
                        String key = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode+"_"+marketCategoryId);
                        redisService.del(key);
                        log.info("::{}::putTradeMarketConfigChangeSold,删除旧数据源赛事赔率:{}", request.getLinkId(), key);
                    }
                } else {
                    //获取玩法开售表
                    List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItemByDataSourceCodeAndMarketType(standardMatchInfo.getId(), thirdMatchInfo.getDataSourceCode(), tradeMarketConfigDTO.getMatchType());
                    if (!CollectionUtils.isEmpty(marketCategorySell)) {
                        Set<Long> marketCategorySellIdSet = marketCategorySell.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
                        for (Long marketCategorySellId : marketCategorySellIdSet) {
                            String key = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode()+"_"+marketCategorySellId);
                            redisService.del(key);
                            log.info("::{}::putTradeMarketConfigChangeSold2,删除旧数据源赛事赔率:{}", request.getLinkId(), key);
                        }
                    }
                }
                //如果是赛事级别关盘，需要把所有盘口关闭
                if(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus())){
                    //过滤当前数据里面的玩法
                    Set<Long> marketCategoryIdSet = new HashSet();
                    for(StandardMarketDataMessage standardMarketDataMessage : stringStandardMarketDataMessageMap.values()){
                        //关盘只关封盘状态跟活跃状态的盘口
                        if(standardMarketDataMessage.getThirdMarketSourceStatus() < Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED)
                        {
                            standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                            standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                            marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                        }else{
                            log.info("::{}::putTradeMarketConfigChangeSold数据源赛事级关盘,标准赛事id:{},盘口id:{}统一盘口id:{},三方盘口源id:{},盘口三方源状态:{}",
                                    request.getLinkId(), standardMatchInfo.getId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(),
                                    standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getThirdMarketSourceStatus());
                        }
                    }
                    //盘口下发
                    thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),standardSportMarketSell, new HashMap<>());
                }
            }catch (Exception e) {
                throw e;
            }finally {
                redisService.unLock(redisLocKey,lockValue);
                log.info("::{}::putTradeMarketConfigChangeSold,redisLocKey:{},释放分布式锁,lockValue:{}", request.getLinkId(),redisLocKey, lockValue);
            }
            return Response.success();
        }
        return Response.success();
    }

    /**
     * 赛事级别切换玩法解除告警
     *
     * @param linkId
     * @param marketType
     * @param standardMatchId
     */
    @Deprecated
    public void liftedOddsWarning(String linkId, Integer marketType, Long standardMatchId) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        String oddsWarningKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_WARNING;
        Object obj = redisService.hGet(oddsWarningKey, String.valueOf(standardMatchInfo.getId()));
        if (Objects.isNull(obj)) {
            return;
        }
        Map<Long, Map<String, Object>> warningListMap = (Map<Long, Map<String, Object>>) obj;
        thirdMatchMarketProcessor.matchOddsWarning(linkId, marketType, standardMatchInfo, warningListMap.keySet());
    }
}
