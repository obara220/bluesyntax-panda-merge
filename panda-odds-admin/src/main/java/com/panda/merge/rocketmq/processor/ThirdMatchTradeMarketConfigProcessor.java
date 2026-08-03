package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.component.CommonAsyncService;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.CategoryDataSourceCodeDao;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.*;
import com.panda.merge.odds.XtsMonitor;
import com.panda.merge.odds.service.DataSourceAutoSwitchService;
import com.panda.merge.odds.service.DataSourceSwitchService;
import com.panda.merge.rocketmq.producer.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Validated
public class ThirdMatchTradeMarketConfigProcessor extends BaseProcessor {
    @Autowired
    private ConfigTradeMarketLogService configTradeMarketLogService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private ConfigTradeTypeService configTradeTypeService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private RedisService redisService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private ConfigMarketDisplayTradeService configMarketDisplayTradeService;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    @Autowired
    private ConfigMarketCategoryPlaceService configMarketCategoryPlaceService;

    @Autowired
    private ConfigMarketTradeItemService configMarketTradeItemService;

    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;

    @Autowired
    private ConfigMarketDisplayProducer configMarketDisplayProducer;

    @Autowired
    private MatchDataSourceWeightService matchDataSourceWeightService;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private ConfigMarketCategoryMarginService configMarketCategoryMarginService;

    @Resource(name = "ProcessTradeSystemThreadPool")
    private ThreadPoolTaskExecutor processTradeSystemThreadPool;
    @Resource(name = "ProcessUiInterfaceThreadPool")
    private ThreadPoolTaskExecutor processUiInterfaceThreadPool;

    @Autowired
    private ConfigMarketHeadGapService configMarketHeadGapService;
    @Autowired
    private ConfigMarketCategoryMarginLogService marketCategoryMarginLogService;
    @Autowired
    private ConfigMarketAutoDiffTradeLogService configMarketAutoDiffTradeLogService;
    @Autowired
    private ConfigMarketHeadGapLogService configMarketHeadGapLogService;

    @Autowired
    private StandardCategoryAutoCloseProducer standardCategoryAutoCloseProducer;
    @Autowired
    private ConfigMarketStatusTradeService configMarketStatusTradeService;
    @Lazy
    @Autowired
    private ChangeSoldMessageProcessor changeSoldMessageProcessor;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;
    @Autowired
    private ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;
    //    @Reference(check = false, lazy = true)
//    private IAuthRequiredPermission iAuthRequiredPermission;
    @Autowired
    private StandardSportMarketSellLogService standardSportMarketSellLogService;
    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private SoldMessageToOddsProcessor soldMessageToOddsProcessor;

    @Autowired
    private MarketCategorySellProducer marketCategorySellProducer;

    @Autowired
    private StandardMatchSwitchStatusProducer switchStatusProducer;

    @Autowired
    private ConfigMarketMarginGapLogService configMarketMarginGapLogService;

    @Autowired
    private ConfigMarketMarginGapService configMarketMarginGapService;

    @Autowired
    private DataMerchantBaffleProducer dataMerchantBaffleProducer;

    @Autowired
    private CategoryDataSourceCodeDao categoryDataSourceCodeDao;

    @Autowired
    private StandardClearCategoryDiffProducer standardClearCategoryDiffProducer;

    @Autowired
    private ConfigTournamentTradeItemService configTournamentTradeItemService;

    @Autowired
    private ConfigCashOutTradeItemService configCashOutTradeItemService;
    @Lazy
    @Autowired
    private ThirdMarketPreResultProcessor thirdMarketPreResultProcessor;

    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;

    @Autowired
    private ModifyMatchInfoProducer modifyMatchInfoProducer;

    @Autowired
    private AoMatchDiffAndMarginProducer aoMatchDiffAndMarginProducer;

    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;

    @Autowired
    private PdMatchScoreLogService pdMatchScoreLogService;

    @Autowired
    private StandardCategorySetStatusMessageProducer standardCategorySetStatusMessageProducer;

    @Autowired
    private ConfigMarketOddsStatusService configMarketOddsStatusService;

    @Autowired
    private CategoryCodeProcessor categoryCodeProcessor;

    @Autowired
    private CommonAsyncService commonAsyncService;

    @Autowired
    private Validator validator;

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketNewService;

    @Autowired
    private DataSourceSwitchService dataSourceSwitchService;

    @Autowired
    private DataSourceAutoSwitchService dataSourceAutoSwitchService;

    @Autowired
    private XtsMonitor xtsMonitor;
    /**
     * 处理三方赛事内部编码
     * @param request
     */
    public void processThirdMatchTradeMarketConfig(@Valid Request<TradeMarketConfigDTO> request) {
        log.info("::{}::processThirdMatchTradeMarketConfig入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeMarketConfigDTO tradeMarketConfigDTO = request.getData();
        configTradeMarketLogService.create(request.getLinkId(), tradeMarketConfigDTO);
        pdMatchScoreLogService.updateMarketStatusLog(tradeMarketConfigDTO);
        //--------如果为三方数据源配置-----------
        if (Constant.TRADE_MARKET_CONFIG.SOURCE_SYSTEM.THIRD_DATA_SOURCE.equals(tradeMarketConfigDTO.getSourceSystem())) {
            //数据商当前只有赛事级别配置
            if (!Constant.TRADE_MARKET_CONFIG.LEVEL.MATCH.equals(tradeMarketConfigDTO.getLevel())) {
                log.info("::{}::processThirdMatchTradeMarketConfig,当前仅支持数据源的赛事级别配置", request.getLinkId());
                return ;
            }
            //对三方源的开盘配置不处理
            if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE.equals(tradeMarketConfigDTO.getMarketStatus())) {
                log.info("::{}::processThirdMatchTradeMarketConfig,三方源的赛事开盘配置不处理", request.getLinkId());
                return ;
            }
            //判断赛事类型
            boolean isOutRight = StringUtils.equals("1", tradeMarketConfigDTO.getMatchType());
            ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfo(isOutRight,
                    tradeMarketConfigDTO.getAddition1(), tradeMarketConfigDTO.getTargetId());
            if (thirdMatchInfo == null) {
                log.info("::{}::processThirdMatchTradeMarketConfig,数据源数据TargetID对应的三方赛事未找到，三方赛事id:{}", request.getLinkId(),
                        tradeMarketConfigDTO.getTargetId());
                return ;
            }
            //判断数据商下发的赛事关盘有没有区分赛前和滚球，未传默认全部
            Integer[] marketType;
            if (StringUtils.isBlank(tradeMarketConfigDTO.getAddition3())) {
                // F01没有早滚的概念，单独区分
                if(DataSourceCodeEnum.F01.code.equals(tradeMarketConfigDTO.getAddition1())) {
                    int liveFlag = isOddsLive(thirdMatchInfo.getReferenceId());
                    // 只处理滚球，早盘不处理 0为滚球，1为早盘
                    if(liveFlag == 1) {
                        marketType = new Integer[]{2, 1};
                    } else {
                        marketType = new Integer[]{2, 0};
                    }
                } else {
                    //2:冠军盘口;1:赛前盘;0:滚球盘
                    marketType = new Integer[]{2, 1, 0};
                }
            } else if (isOutRight) {
                marketType = new Integer[]{1, 2};
            } else {
                Integer marketTypeInt = Integer.parseInt(tradeMarketConfigDTO.getAddition3());
                marketType = new Integer[]{marketTypeInt};
            }
            List statusList = new ArrayList();
            if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                statusList = Arrays.asList(new Integer[]{0, 1, 11});
            } else if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                statusList = Arrays.asList(new Integer[]{0, 11});
            }
            //三方盘口处理
            log.info("{}::更新三方盘口状态为:{}，三方赛事Id:{},盘口类型:{},条件状态:{}", request.getLinkId(), tradeMarketConfigDTO.getMarketStatus(),
                    tradeMarketConfigDTO.getTargetId(), marketType, statusList);
            //更新三方赛事状态
            thirdSportMarketService.updateByExampleSelective(tradeMarketConfigDTO.getMarketStatus(),
                    tradeMarketConfigDTO.getAddition1(), thirdMatchInfo.getId(), statusList, Arrays.asList(marketType));
            StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutRight,
                    thirdMatchInfo.getReferenceId());
            if (standardMatchInfo == null) {
                log.info("::{}::processThirdMatchTradeMarketConfig,数据源数据TargetID对应的标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                        thirdMatchInfo.getReferenceId());
                return ;
            }
            //获取开售信息
            StandardSportMarketSell standardSportMarketSell =
                    thirdMatchMarketProcessor.getStandardSportMarketSell(isOutRight, thirdMatchInfo.getReferenceId());
            if (standardSportMarketSell == null) {
                log.info("::{}::processThirdMatchTradeMarketConfig,数据源数据TargetID对应的标准赛事未开售，标准赛事id:{}", request.getLinkId(),
                        thirdMatchInfo.getReferenceId());
                return ;
            }

            //以下和三方盘口接口有并发问题，这里需要以赛事维度加redis锁
            String lockValue = UUIdUtils.getId() + "_" + request.getLinkId();
            String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
            log.info("::{}::processThirdMatchTradeMarketConfig,redisLocKey:{},准备获取分布式锁,lockValue:{}", request.getLinkId(),
                    redisLocKey, lockValue);
            redisService.tryLock(redisLocKey, lockValue, 5, 3);
            log.info("::{}::processThirdMatchTradeMarketConfig,redisLocKey:{},获取到分布式锁,lockValue:{}", request.getLinkId(),
                    redisLocKey, lockValue);
            try {
                //如果是赛事级别关盘，需要把所有盘口关闭
                if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                    if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.OD.code)) {
                        // 本次修改的标准赛事
                        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
                        upStandardMatchInfo.setId(standardMatchInfo.getId());
                        // 修改标准赛事的操盘赛事状态
                        upStandardMatchInfo.setOperateMatchStatus(tradeMarketConfigDTO.getMarketStatus());
                        // 修改标准赛事
                        standardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);

                        standardMarketOddsProducer.standardMarketOddsStateSend(request.getLinkId(), standardMatchInfo, request.getDataSourceTime());
                        // 下发赛事级别关盘给风控
                        dataMerchantBaffleProducer.changeMatchStatusSendRiskMQ(request.getLinkId(), standardMatchInfo.getId(),
                                Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                        // 下发赛事状态给到业务
                        standardMarketOddsProducer.standardMarketOddsAsyncSend(request.getLinkId(), standardMatchInfo,
                                null, request.getDataSourceTime(), false);
                        log.info("::{}::OD数据源,标准赛事id：{},赛事级别关盘，赛事下架", request.getLinkId(), standardMatchInfo.getId());
                        return ;
                    }

                    //获取标准盘口里面的玩法ID
                    List<StandardSportMarket> standardSportMarkets = standardSportMarketService.getItemByMatchIdAndDataSourceCode(thirdMatchInfo.getReferenceId(), tradeMarketConfigDTO.getAddition1(), Arrays.asList(marketType));
                    //盘口没有开出去，没必要走下面流程
                    if (org.springframework.util.CollectionUtils.isEmpty(standardSportMarkets)) {
                        return ;
                    }
                    //刷新数据库所有相关标准盘口的状态为关盘
                    standardSportMarketService.updateByExampleSelective(tradeMarketConfigDTO.getMarketStatus(),
                            tradeMarketConfigDTO.getAddition1(), thirdMatchInfo.getReferenceId(),
                            Arrays.asList(new Integer[]{0, 1, 11}), Arrays.asList(marketType));
                    //记录关盘的盘口id列表
                    List<Long> marketIdList = new ArrayList<>();
                    //过滤当前数据里面的玩法
                    Set<Long> marketIdSet = new HashSet();
                    Set<Long> marketCategoryIdSet = new HashSet();

                    Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
                    //获取当前数据源所有盘口缓存 ，兼容冠军盘口
                    if (isOutRight) {
                        String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode();
                        stringStandardMarketDataMessageMap = redisService.hGetAll(marketKey);
                    } else {
                        //得到标准盘口玩法集合，获取标准赔率
                        Set<Long> standardMarketCategoryIdSet = standardSportMarkets.stream().map(StandardSportMarket::getMarketCategoryId).collect(Collectors.toSet());
                        for (Long marketCategoryId : standardMarketCategoryIdSet) {
                            String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode() + "_" + marketCategoryId);
                            Map<String, StandardMarketDataMessage> standardMarketDataMessageMap = redisService.hGetAll(marketKey);
                            if (MapUtils.isNotEmpty(standardMarketDataMessageMap)) {
                                stringStandardMarketDataMessageMap.putAll(standardMarketDataMessageMap);
                            }
                        }
                    }
                    for (StandardMarketDataMessage standardMarketDataMessage : stringStandardMarketDataMessageMap.values()) {
                        //关盘只关封盘状态跟活跃状态的盘口,构建盘口不关闭
                        if (standardMarketDataMessage.getThirdMarketSourceStatus() < Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED) {
                            if (standardMarketDataMessage.getMarketSource() == 1) {
                                log.info("::{}::构建盘口不关闭,标准赛事ID:{},三方盘口ID:{}",
                                        request.getLinkId(), standardMatchInfo.getId(), standardMarketDataMessage.getThirdMarketSourceId());
                                continue;
                            }
                            standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                            standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                            marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                            marketIdList.add(standardMarketDataMessage.getRelationMarketId());
                            marketIdSet.add(standardMarketDataMessage.getRelationMarketId());

                            //刷新盘口数据，兼容冠军盘口
                            String marketPutKey;
                            if (isOutRight) {
                                marketPutKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode();
                            } else {
                                marketPutKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
                            }
                            //刷新缓存中的所有相关盘口的状态
                            Boolean result = redisService.hSet(marketPutKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                            log.info("::{}:: processThirdMatchTradeMarketConfig刷新缓存信息为关盘, key={},marketId={},result={}",
                                    request.getLinkId(), marketPutKey, standardMarketDataMessage.getRelationMarketId(), result);
                        } else {
                            log.info("::{}::processThirdMatchTradeMarketConfig数据源赛事级关盘,标准赛事id:{},盘口id:{}统一盘口id:{},三方盘口源id:{}," +
                                            "盘口三方源状态:{}",
                                    request.getLinkId(), standardMatchInfo.getId(),
                                    standardMarketDataMessage.getId(),
                                    standardMarketDataMessage.getRelationMarketId(),
                                    standardMarketDataMessage.getThirdMarketSourceId(),
                                    standardMarketDataMessage.getThirdMarketSourceStatus());
                        }
                    }
                    if (isOutRight) {
                        marketCategoryIdSet = marketIdSet;
                    }
                    //盘口下发
                    thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                            marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                            standardSportMarketSell, new HashMap<>());
                    //通知风控
                    sendRscMatchStatus(request, tradeMarketConfigDTO, standardMatchInfo);
                }
                //如果是赛事级别封盘，需要把所有开盘中的盘口封盘
                if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                    //TX下发赛事级别封盘，不走流程，直接通知风控赛事级别封盘
                    if (DataSourceCodeEnum.TX.code.equals(tradeMarketConfigDTO.getAddition1())) {
                        sendRscMatchStatus(request, tradeMarketConfigDTO, standardMatchInfo);
                        return ;
                    }
                    //获取标准盘口里面的玩法ID
                    List<StandardSportMarket> standardSportMarkets = standardSportMarketService.getItemByMatchIdAndDataSourceCode(thirdMatchInfo.getReferenceId(), tradeMarketConfigDTO.getAddition1(), Arrays.asList(marketType));
                    //盘口没有开出去，没必要走下面流程
                    if (CollectionUtils.isEmpty(standardSportMarkets)) {
                        return ;
                    }
                    //刷新数据库所有相关开盘盘口的状态为封盘
                    standardSportMarketService.updateByExampleSelective(tradeMarketConfigDTO.getMarketStatus(),
                            tradeMarketConfigDTO.getAddition1(), thirdMatchInfo.getReferenceId(),
                            Arrays.asList(new Integer[]{0, 11}), Arrays.asList(marketType));
                    //记录封盘的盘口id列表
                    List<Long> marketIdList = new ArrayList<>();
                    //过滤当前数据里面的玩法
                    Set<Long> marketIdSet = new HashSet();
                    Set<Long> marketCategoryIdSet = new HashSet();
                    Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
                    //获取当前数据源所有盘口缓存 ，兼容冠军盘口
                    if (isOutRight) {
                        String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode();
                        stringStandardMarketDataMessageMap = redisService.hGetAll(marketKey);
                    } else {
                        //得到标准盘口玩法集合，获取标准赔率
                        Set<Long> standardMarketCategoryIdSet = standardSportMarkets.stream().map(StandardSportMarket::getMarketCategoryId).collect(Collectors.toSet());
                        for (Long marketCategoryId : standardMarketCategoryIdSet) {
                            String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode() + "_" + marketCategoryId);
                            Map<String, StandardMarketDataMessage> standardMarketDataMessageMap = redisService.hGetAll(marketKey);
                            if (MapUtils.isNotEmpty(standardMarketDataMessageMap)) {
                                stringStandardMarketDataMessageMap.putAll(standardMarketDataMessageMap);
                            }
                        }
                    }
                    for (StandardMarketDataMessage standardMarketDataMessage : stringStandardMarketDataMessageMap.values()) {
                        //只处理开盘的盘口为封盘,构建盘口不处理
                        if (standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE)) {
                            if (standardMarketDataMessage.getMarketSource() == 1) {
                                log.info("::{}::构建盘口不关闭,标准赛事ID:{},三方盘口ID:{}",
                                        request.getLinkId(), standardMatchInfo.getId(), standardMarketDataMessage.getThirdMarketSourceId());
                                continue;
                            }
                            standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED);
                            standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED);
                            marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                            marketIdList.add(standardMarketDataMessage.getRelationMarketId());
                            marketIdSet.add(standardMarketDataMessage.getRelationMarketId());
                            //刷新盘口数据，兼容冠军盘口
                            String marketPutKey;
                            if (isOutRight) {
                                marketPutKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode();
                            } else {
                                marketPutKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
                            }
                            //刷新缓存中的所有相关盘口的状态
                            Boolean result = redisService.hSet(marketPutKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                            log.info("::{}:: processThirdMatchTradeMarketConfig刷新缓存信息为封盘, key={},marketId={},result={}",
                                    request.getLinkId(), marketPutKey, standardMarketDataMessage.getRelationMarketId(), result);
                        } else {
                            log.info("::{}::processThirdMatchTradeMarketConfig数据源赛事级封盘,标准赛事id:{},盘口id:{}统一盘口id:{},三方盘口源id:{}," +
                                            "盘口三方源状态:{}",
                                    request.getLinkId(), standardMatchInfo.getId(),
                                    standardMarketDataMessage.getId(),
                                    standardMarketDataMessage.getRelationMarketId(),
                                    standardMarketDataMessage.getThirdMarketSourceId(),
                                    standardMarketDataMessage.getThirdMarketSourceStatus());
                        }
                    }
                    if (isOutRight) {
                        marketCategoryIdSet = marketIdSet;
                    }
                    //盘口推送
                    thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                            marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                            standardSportMarketSell, new HashMap<>());
                }
                testMatchTradeMarketConfig(request, standardMatchInfo.getId(), marketType);
                //提前结算收到赛事级别状态
                if (thirdMatchInfo.getDataSourceCode().equals(DataSourceCodeEnum.SR.code)) {
                    thirdMarketPreResultProcessor.liveCloseCashOutStatus(request.getLinkId() + "_MATCH_STATUS",
                            standardMatchInfo.getId(), request.getDataSourceTime(), tradeMarketConfigDTO.getAddition3(), true);
                }
            } finally {
                redisService.unLock(redisLocKey, lockValue);
                log.info("::{}::processThirdMatchTradeMarketConfig,redisLocKey:{},释放分布式锁,lockValue:{}", request.getLinkId(),
                        redisLocKey, lockValue);
            }
        }
    }


    private void sendRscMatchStatus(Request<TradeMarketConfigDTO> request, TradeMarketConfigDTO tradeMarketConfigDTO, StandardMatchInfo standardMatchInfo) {
        //查询赛事是否切换了TX玩法
        Boolean isChangeTx = Boolean.FALSE;
        //TX数据关盘状态清除水差
        List<Long> clearDiffList = new ArrayList<>();
        Boolean isDeactivated = Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus());
        if (DataSourceCodeEnum.TX.code.equals(tradeMarketConfigDTO.getAddition1())) {
            //根据当前赛事状态区分赛前滚球，查出缓存中是否存在TX玩法
            Integer matchStatus = standardMatchInfo.getMatchStatus();
            Integer changeMarketType = isOddsLive(standardMatchInfo.getId());
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + changeMarketType;
            Map<String, String> changeCategoryMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(changeCategoryMap)) {
                isChangeTx = changeCategoryMap.containsValue(DataSourceCodeEnum.TX.code);
                if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                    if (isChangeTx) {
                        changeCategoryMap.forEach((k, v) -> {
                            if (v.equals(DataSourceCodeEnum.TX.code)) {
                                clearDiffList.add(Long.valueOf(k));
                            }
                        });
                    }
                }
            }
        }
        if (DataSourceCodeEnum.LS.code.equals(tradeMarketConfigDTO.getAddition1()) && DataSourceCodeEnum.LS.code.equals(tradeMarketConfigDTO.getAddition4())) {
            //根据当前赛事状态区分赛前滚球，查出缓存中是否存在TX玩法
            Integer matchStatus = standardMatchInfo.getMatchStatus();
            Integer changeMarketType = isOddsLive(standardMatchInfo.getId());
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + changeMarketType;
            Map<String, String> changeCategoryMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(changeCategoryMap)) {
                isChangeTx = changeCategoryMap.containsValue(DataSourceCodeEnum.LS.code);
                if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                    if (isChangeTx) {
                        changeCategoryMap.forEach((k, v) -> {
                            if (v.equals(DataSourceCodeEnum.LS.code)) {
                                clearDiffList.add(Long.valueOf(k));
                            }
                        });
                    }
                }
            }
        }
        //通知风控清除玩法水差
        if (!CollectionUtils.isEmpty(clearDiffList)) {
            log.info("::{}::标准赛事ID:{},TX/LS切换通知风控清除玩法水差:{}", request.getLinkId(), standardMatchInfo.getId(), clearDiffList);
            delDiffByMatchIdAndCategoryList(request.getLinkId(), standardMatchInfo.getId(), clearDiffList, standardMatchInfo.getSportId().intValue());
            standardClearCategoryDiffProducer.sendStandardClearCategoryDiffRisk(request.getLinkId(), standardMatchInfo, clearDiffList);
        }
        //1.TX/ls内部切换 存在TX/LS玩法 下发封盘
        //2.数据源关盘 SR的水球 下发关盘
        //3.数据源关盘 拳击 下发关盘
        if ((isChangeTx && (DataSourceCodeEnum.TX.code.equals(tradeMarketConfigDTO.getAddition1()) || DataSourceCodeEnum.LS.code.equals(tradeMarketConfigDTO.getAddition1())))
                || (isDeactivated && DataSourceCodeEnum.SR.code.equals(standardMatchInfo.getDataSourceCode()) && standardMatchInfo.getSportId().equals(StandardSportTypeEnum.WaterPolo.code))
                || (isDeactivated && standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Boxing.code))) {
            dataMerchantBaffleProducer.switchDataSourceSendRiskMQ(request.getLinkId(), standardMatchInfo.getId(), standardMatchInfo.getSportId());
        }
    }

    private void testMatchTradeMarketConfig(Request<TradeMarketConfigDTO> request, Long matchId, Integer[] marketType) {
        String linkId = request.getLinkId() + "_new_match";
        TradeMarketConfigDTO tradeMarketConfigDTO = request.getData();
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItem(matchId);
        if (standardRelationNewStandard == null) {
            return;
        }
        //判断赛事类型
        boolean isOutRight = StringUtils.equals("1", tradeMarketConfigDTO.getMatchType());

        StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutRight,
                standardRelationNewStandard.getNewStandardId());
        if (standardMatchInfo == null) {
            return;
        }

        ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfo(isOutRight,
                tradeMarketConfigDTO.getAddition1(), tradeMarketConfigDTO.getTargetId());
        if (thirdMatchInfo == null) {
            log.info("::{}::测试联赛，putTradeMarketConfig,数据源数据TargetID对应的三方赛事未找到，三方赛事id:{}", linkId,
                    tradeMarketConfigDTO.getTargetId());
        }
        //获取开售信息
        StandardSportMarketSell standardSportMarketSell =
                thirdMatchMarketProcessor.getStandardSportMarketSell(isOutRight, standardMatchInfo.getId());
        if (standardSportMarketSell == null) {
            log.info("::{}::测试联赛，putTradeMarketConfig,数据源数据TargetID对应的标准赛事未开售，标准赛事id:{}", linkId,
                    standardMatchInfo.getId());
        }

        //对测试联赛加锁
        String lockValue = UUIdUtils.getId() + "_" + linkId + "_new_match";
        boolean isLock = false;
        try {
            String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardRelationNewStandard.getNewStandardId();
            isLock = true;
            log.info("::{}::测试联赛，putTradeMarketConfig,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
            redisService.tryLock(redisLocKey, lockValue, 5, 3);
            log.info("::{}::测试联赛，putTradeMarketConfig,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);

            //获取当前数据源所有盘口缓存
            String marketKey =
                    Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode();
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                    redisService.hGetAll(marketKey);
            //如果是赛事级别关盘，需要把所有盘口关闭
            if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                //刷新数据库所有相关标准盘口的状态为关盘
                standardSportMarketService.updateByExampleSelective(tradeMarketConfigDTO.getMarketStatus(),
                        tradeMarketConfigDTO.getAddition1(), standardMatchInfo.getId(),
                        Arrays.asList(new Integer[]{0, 1, 11}), Arrays.asList(marketType));
                //记录关盘的盘口id列表
                List<Long> marketIdList = new ArrayList<>();
                //过滤当前数据里面的玩法
                Set<Long> marketIdSet = new HashSet();
                Set<Long> marketCategoryIdSet = new HashSet();
                for (StandardMarketDataMessage standardMarketDataMessage :
                        stringStandardMarketDataMessageMap.values()) {
                    //关盘只关封盘状态跟活跃状态的盘口,构建盘口不关闭
                    if (standardMarketDataMessage.getThirdMarketSourceStatus() < Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED) {
                        if (standardMarketDataMessage.getMarketSource() == 1) {
                            log.info("::{}::测试联赛，构建盘口不关闭,标准赛事ID:{},三方盘口ID:{}",
                                    linkId, standardMatchInfo.getId(), standardMarketDataMessage.getThirdMarketSourceId());
                            continue;
                        }
                        standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                        standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                        marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                        marketIdList.add(standardMarketDataMessage.getRelationMarketId());
                        marketIdSet.add(standardMarketDataMessage.getRelationMarketId());
                    } else {
                        log.info("::{}::测试联赛，putTradeMarketConfig数据源赛事级关盘,标准赛事id:{},盘口id:{}统一盘口id:{},三方盘口源id:{}," +
                                        "盘口三方源状态:{}",
                                linkId, standardMatchInfo.getId(),
                                standardMarketDataMessage.getId(),
                                standardMarketDataMessage.getRelationMarketId(),
                                standardMarketDataMessage.getThirdMarketSourceId(),
                                standardMarketDataMessage.getThirdMarketSourceStatus());
                    }
                }
                //刷新缓存中的所有相关盘口的状态
                Boolean result = redisService.hSetAll(marketKey, stringStandardMarketDataMessageMap,
                        marketCacheTime(standardMatchInfo.getBeginTime()));
                log.info("::{}::测试联赛， putTradeMarketConfig刷新缓存信息为关盘, key={},marketIdList={},result={}",
                        linkId, marketKey, marketIdList, result);
                if (isOutRight) {
                    marketCategoryIdSet = marketIdSet;
                }
                //盘口下发
                thirdMatchMarketProcessor.processOddsByAll(linkId,request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                        marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                        standardSportMarketSell, new HashMap<>());
                //通知风控
                sendRscMatchStatus(request, tradeMarketConfigDTO, standardMatchInfo);
            }
            //如果是赛事级别封盘，需要把所有开盘中的盘口封盘
            if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED.equals(tradeMarketConfigDTO.getMarketStatus())) {
                //TX下发赛事级别封盘，不走流程，直接通知风控赛事级别封盘
                if (DataSourceCodeEnum.TX.code.equals(tradeMarketConfigDTO.getAddition1())) {
                    sendRscMatchStatus(request, tradeMarketConfigDTO, standardMatchInfo);
                    return;
                }
                //刷新数据库所有相关开盘盘口的状态为封盘
                standardSportMarketService.updateByExampleSelective(tradeMarketConfigDTO.getMarketStatus(),
                        tradeMarketConfigDTO.getAddition1(), standardMatchInfo.getId(),
                        Arrays.asList(new Integer[]{0, 11}), Arrays.asList(marketType));
                //记录封盘的盘口id列表
                List<Long> marketIdList = new ArrayList<>();
                //过滤当前数据里面的玩法
                Set<Long> marketIdSet = new HashSet();
                Set<Long> marketCategoryIdSet = new HashSet();
                for (StandardMarketDataMessage standardMarketDataMessage :
                        stringStandardMarketDataMessageMap.values()) {
                    //只处理开盘的盘口为封盘,构建盘口不处理
                    if (standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE)) {
                        if (standardMarketDataMessage.getMarketSource() == 1) {
                            log.info("::{}::测试联赛，构建盘口不关闭,标准赛事ID:{},三方盘口ID:{}",
                                    linkId, standardMatchInfo.getId(), standardMarketDataMessage.getThirdMarketSourceId());
                            continue;
                        }
                        standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED);
                        standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED);
                        marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                        marketIdList.add(standardMarketDataMessage.getRelationMarketId());
                        marketIdSet.add(standardMarketDataMessage.getRelationMarketId());
                    } else {
                        log.info("::{}::测试联赛，putTradeMarketConfig数据源赛事级封盘,标准赛事id:{},盘口id:{}统一盘口id:{},三方盘口源id:{}," +
                                        "盘口三方源状态:{}",
                                linkId, standardMatchInfo.getId(),
                                standardMarketDataMessage.getId(),
                                standardMarketDataMessage.getRelationMarketId(),
                                standardMarketDataMessage.getThirdMarketSourceId(),
                                standardMarketDataMessage.getThirdMarketSourceStatus());
                    }
                }
                //刷新缓存中的所有相关盘口的状态
                Boolean result = redisService.hSetAll(marketKey, stringStandardMarketDataMessageMap,
                        marketCacheTime(standardMatchInfo.getBeginTime()));
                log.info("::{}::测试联赛， putTradeMarketConfig刷新缓存信息为封盘，key={}, marketIdList={},result={}",
                        linkId, marketKey, marketIdList, result);
                if (isOutRight) {
                    marketCategoryIdSet = marketIdSet;
                }
                //盘口推送
                thirdMatchMarketProcessor.processOddsByAll(linkId,request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                        marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                        standardSportMarketSell, new HashMap<>());
            }

        } finally {
            if (isLock) {
                String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardRelationNewStandard.getNewStandardId();
                redisService.unLock(redisLocKey, lockValue);
                log.info("::{}::测试联赛，接收数据源赔率开始,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
            }
        }
    }


}
