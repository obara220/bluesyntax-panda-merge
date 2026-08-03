package com.panda.merge.dubbo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.CommonAsyncService;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dao.CategoryDataSourceCodeDao;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.ChangeSoldMessage;
import com.panda.merge.dto.message.StandardCategoryAutoCloseMessage;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.odds.*;
import com.panda.merge.model.*;
import com.panda.merge.odds.ThirdMarket108048Helper;
import com.panda.merge.odds.XtsMonitor;
import com.panda.merge.odds.service.DataSourceAutoSwitchService;
import com.panda.merge.odds.service.DataSourceSwitchService;
import com.panda.merge.odds.service.PlayRiskManagerService;
import com.panda.merge.odds.utils.AutoMapSplitterUtils;
import com.panda.merge.odds.utils.DataSourceUtils;
import com.panda.merge.rocketmq.processor.*;
import com.panda.merge.rocketmq.producer.*;
import com.panda.merge.service.*;
import com.panda.merge.validator.ValidatorUtils;
import com.panda.sport.manager.api.IMarketCategorySellApi;
import com.panda.sport.manager.api.dto.ChangeBusinessEventSaleDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.validation.Validator;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_XTS_MATCH_AUTO_SWITCH;

//import com.panda.sports.auth.rpc.IAuthRequiredPermission;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 * @see com.panda.merge.dubbo <br>
 */
@Slf4j
@Component
@DubboService
public class TradeMarketConfigApiServiceImpl extends BaseProcessor implements ITradeMarketConfigApi {

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
    private PlayRiskManagerService playRiskManagerService;

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

    @Autowired
    private OutrightTradeMarketConfigService outrightTradeMarketConfigService;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @DubboReference
    private IMarketCategorySellApi iMarketCategorySellApi;

    @Override
    public Response putTradeMarketConfig(Request<TradeMarketConfigDTO> request) {
        //validateLinkId("putTradeMarketConfig", request);
        log.info("::{}::putTradeMarketConfig入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeMarketConfigDTO tradeMarketConfigDTO = request.getData();
        //保存configTradeMarketLog，该表只记录，不会作为业务使用
        configTradeMarketLogService.create(request.getLinkId(), tradeMarketConfigDTO);
        pdMatchScoreLogService.updateMarketStatusLog(tradeMarketConfigDTO);
        //--------如果为三方数据源配置-----------
        if (Constant.TRADE_MARKET_CONFIG.SOURCE_SYSTEM.THIRD_DATA_SOURCE.equals(tradeMarketConfigDTO.getSourceSystem())) {
            //数据商当前只有赛事级别配置
            if (!Constant.TRADE_MARKET_CONFIG.LEVEL.MATCH.equals(tradeMarketConfigDTO.getLevel())) {
                log.info("::{}::putTradeMarketConfig,当前仅支持数据源的赛事级别配置", request.getLinkId());
                return Response.failed("当前仅支持数据源的赛事级别配置");
            }
            //对三方源的开盘配置不处理
            if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE.equals(tradeMarketConfigDTO.getMarketStatus())) {
                log.info("::{}::putTradeMarketConfig,三方源的赛事开盘配置不处理", request.getLinkId());
                return Response.failed("三方源的赛事开盘配置不处理");
            }
            //判断赛事类型
            boolean isOutRight = StringUtils.equals("1", tradeMarketConfigDTO.getMatchType());
            ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfo(isOutRight,
                    tradeMarketConfigDTO.getAddition1(), tradeMarketConfigDTO.getTargetId());
            if (thirdMatchInfo == null) {
                log.info("::{}::putTradeMarketConfig,数据源数据TargetID对应的三方赛事未找到，三方赛事id:{}", request.getLinkId(),
                        tradeMarketConfigDTO.getTargetId());
                return Response.failed("数据源数据TargetID对应的三方赛事未找到");
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
                log.info("::{}::putTradeMarketConfig,数据源数据TargetID对应的标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                        thirdMatchInfo.getReferenceId());
                return Response.failed("数据源数据TargetID对应的标准赛事未找到");
            }
            //获取开售信息
            StandardSportMarketSell standardSportMarketSell =
                    thirdMatchMarketProcessor.getStandardSportMarketSell(isOutRight, thirdMatchInfo.getReferenceId());
            if (standardSportMarketSell == null) {
                log.info("::{}::putTradeMarketConfig,数据源数据TargetID对应的标准赛事未开售，标准赛事id:{}", request.getLinkId(),
                        thirdMatchInfo.getReferenceId());
                return Response.failed("数据源数据TargetID对应的标准赛事未开售");
            }

            //以下和三方盘口接口有并发问题，这里需要以赛事维度加redis锁
            String lockValue = UUIdUtils.getId() + "_" + request.getLinkId();
            String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
            log.info("::{}::putTradeMarketConfig,redisLocKey:{},准备获取分布式锁,lockValue:{}", request.getLinkId(),
                    redisLocKey, lockValue);
            redisService.tryLock(redisLocKey, lockValue, 5, 3);
            log.info("::{}::putTradeMarketConfig,redisLocKey:{},获取到分布式锁,lockValue:{}", request.getLinkId(),
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
                        return Response.success();
                    }

                    //获取标准盘口里面的玩法ID
                    List<StandardSportMarket> standardSportMarkets = standardSportMarketService.getItemByMatchIdAndDataSourceCode(thirdMatchInfo.getReferenceId(), tradeMarketConfigDTO.getAddition1(), Arrays.asList(marketType));
                    //盘口没有开出去，没必要走下面流程
                    if (CollectionUtils.isEmpty(standardSportMarkets)) {
                        return Response.success();
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
                            log.info("::{}:: putTradeMarketConfig刷新缓存信息为关盘, key={},marketId={},result={}",
                                    request.getLinkId(), marketPutKey, standardMarketDataMessage.getRelationMarketId(), result);
                        } else {
                            log.info("::{}::putTradeMarketConfig数据源赛事级关盘,标准赛事id:{},盘口id:{}统一盘口id:{},三方盘口源id:{}," +
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
                        // 操盘记录
                        closeAllMarketByMatch( request.getLinkId(), standardMatchInfo.getId());
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
                        return Response.success();
                    }
                    //获取标准盘口里面的玩法ID
                    List<StandardSportMarket> standardSportMarkets = standardSportMarketService.getItemByMatchIdAndDataSourceCode(thirdMatchInfo.getReferenceId(), tradeMarketConfigDTO.getAddition1(), Arrays.asList(marketType));
                    //盘口没有开出去，没必要走下面流程
                    if (CollectionUtils.isEmpty(standardSportMarkets)) {
                        return Response.success();
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
                            log.info("::{}:: putTradeMarketConfig刷新缓存信息为封盘, key={},marketId={},result={}",
                                    request.getLinkId(), marketPutKey, standardMarketDataMessage.getRelationMarketId(), result);
                        } else {
                            log.info("::{}::putTradeMarketConfig数据源赛事级封盘,标准赛事id:{},盘口id:{}统一盘口id:{},三方盘口源id:{}," +
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
                        // 操盘记录
                        closeAllMarketByMatch(request.getLinkId(), standardMatchInfo.getId());
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
                log.info("::{}::putTradeMarketConfig,redisLocKey:{},释放分布式锁,lockValue:{}", request.getLinkId(),
                        redisLocKey, lockValue);
            }
            return Response.success();
        }
        //--------如果为操盘后台配置,异步处理-----------
        //查询标准赛事是否存在
        StandardMatchInfo oldStandardMatchInfo =
                standardMatchInfoService.getItem(Long.valueOf(tradeMarketConfigDTO.getTargetId()));
        if (oldStandardMatchInfo == null) {
            log.info("::{}::putTradeMarketConfig,操盘数据TargetID对应的标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    tradeMarketConfigDTO.getTargetId());
            return Response.failed("操盘数据TargetID对应的标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(oldStandardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeMarketConfig ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
                    oldStandardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        // 获取缓存中的所有盘口（赛事级开关/操盘操作：不应依赖 oddsLive，只要缓存存在就应取到并下发；否则会出现“开关一开赔率消失”）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMapForTradeOperation(new HashSet<>(), request.getLinkId(), oldStandardMatchInfo, standardSportMarketSell);
        if (null != tradeMarketConfigDTO.getTradeType()
                && (tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO) || tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS))
                && Constant.TRADE_MARKET_CONFIG.LEVEL.MARKET_CATEGORY.equals(tradeMarketConfigDTO.getLevel())) {
            //含有新增的玩法，提示失败
            List<Long> marketgoryList = checkHaveAddMarketgory(tradeMarketConfigDTO,
                    stringStandardMarketDataMessageMap);
            if (marketgoryList.size() > 0) {
                log.info("::{}::putTradeMarketConfig ,包含新增玩法，切换操盘方式失败，标准赛事id：{}", request.getLinkId(), oldStandardMatchInfo.getId());
                return Response.changeTradeFailed("包含新增玩法，切换操盘方式失败", marketgoryList);
            }
        }
        processTradeSystemThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    processTradeSystem(request, tradeMarketConfigDTO, oldStandardMatchInfo, standardSportMarketSell,
                            stringStandardMarketDataMessageMap);
                } catch (Exception e) {
                    log.error("{}::processTradeSystem ERROR:", request.getLinkId(), e);
                }
            }
        });
        return Response.success();
    }

    /**
     * 赛事级/操盘后台操作读取缓存：
     * 不依赖 isOddsLive（赛前/滚球切换标识），避免“标识异常存在时只取到一侧缓存”导致下发空赔率。
     */
    private Map<String, StandardMarketDataMessage> getStringStandardMarketDataMessageMapForTradeOperation(Set<Long> marketCategoryIds,
                                                                                                         String linkId,
                                                                                                         StandardMatchInfo standardMatchInfo,
                                                                                                         StandardSportMarketSell standardSportMarketSell) {
        Map<String, StandardMarketDataMessage> result = new HashMap<>();
        // 赛前
        {
            List<String> preKeys = new ArrayList<>();
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
            Map<String, String> categoryDsMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(categoryDsMap)) {
                for (Map.Entry<String, String> e : categoryDsMap.entrySet()) {
                    Long categoryId = Long.valueOf(e.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(categoryId)) {
                        continue;
                    }
                    String ds = normalizeMarketCategorySellDataSourceCode(e.getValue());
                    String dataSourceCode = supportA99(linkId,standardMatchInfo.getId(),1,categoryId)?"A99":ds;
                    preKeys.add(DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + categoryId));
                }
            } else {
                // 缓存缺失时，不按赛事级 providerCode 过滤（可能存在玩法级切源/混合数据源），直接取该盘型下全部玩法开售数据源
                List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItem(standardMatchInfo.getId(), "1");
                if (!CollectionUtils.isEmpty(marketCategorySell)) {
                    for (MarketCategorySell m : marketCategorySell) {
                        Long categoryId = m.getMarketCategoryId();
                        if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(categoryId)) {
                            continue;
                        }
                        String ds = normalizeMarketCategorySellDataSourceCode(m.getDataSourceCode());
                        preKeys.add(DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + ds + "_" + categoryId));
                    }
                }
            }
            Map<String, StandardMarketDataMessage> preMap = redisService.syncOddsMultiGetAll(preKeys);
            if (MapUtils.isNotEmpty(preMap)) {
                // 容错：不要依赖缓存里的 marketType 字段做过滤（历史/异常数据可能为空或不一致），只要缓存存在就拿来下发
                result.putAll(preMap.entrySet().stream()
                        .filter(x -> x.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
        }
        // 滚球
        {
            List<String> liveKeys = new ArrayList<>();
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 0;
            Map<String, String> categoryDsMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(categoryDsMap)) {
                for (Map.Entry<String, String> e : categoryDsMap.entrySet()) {
                    Long categoryId = Long.valueOf(e.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(categoryId)) {
                        continue;
                    }
                    String ds = normalizeMarketCategorySellDataSourceCode(e.getValue());
                    String dataSourceCode = supportA99(linkId,standardMatchInfo.getId(),0,categoryId)?"A99":ds;
                    liveKeys.add(DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + categoryId));
                }
            } else {
                // 缓存缺失时，不按赛事级 providerCode 过滤，直接取该盘型下全部玩法开售数据源
                List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItem(standardMatchInfo.getId(), "0");
                if (!CollectionUtils.isEmpty(marketCategorySell)) {
                    for (MarketCategorySell m : marketCategorySell) {
                        Long categoryId = m.getMarketCategoryId();
                        if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(categoryId)) {
                            continue;
                        }
                        String ds = normalizeMarketCategorySellDataSourceCode(m.getDataSourceCode());
                        liveKeys.add(DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + ds + "_" + categoryId));
                    }
                }
            }
            Map<String, StandardMarketDataMessage> liveMap = redisService.syncOddsMultiGetAll(liveKeys);
            if (MapUtils.isNotEmpty(liveMap)) {
                // 容错：不要依赖缓存里的 marketType 字段做过滤（历史/异常数据可能为空或不一致），只要缓存存在就拿来下发
                result.putAll(liveMap.entrySet().stream()
                        .filter(x -> x.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
        }
        log.info("::{}:: tradeOperation getMarketData cacheSize={}", linkId, result.size());
        return result;
    }

    /**
     * MarketCategorySell.dataSourceCode 可能包含内部站点前缀（如 T01/L01/L02），此处统一归一到缓存使用的数据源编码。
     */
    private String normalizeMarketCategorySellDataSourceCode(String code) {
        if (StringUtils.isBlank(code)) {
            return code;
        }
        if (code.startsWith("T01")) {
            return DataSourceCodeEnum.TX.getCode();
        }
        if (code.startsWith("L01")) {
            return DataSourceCodeEnum.LS.getCode();
        }
        if (code.startsWith("L02")) {
            return DataSourceCodeEnum.L02.getCode();
        }
        return code;
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
                thirdMatchMarketProcessor.processOddsByAll(linkId,request.getOddsSource(), request.getOperaterId(),standardMatchInfo,
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

    /**
     * 通知风控赛事级别状态
     *
     * @param request
     * @param tradeMarketConfigDTO
     * @param standardMatchInfo
     */
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

    public void processTradeSystem(Request<TradeMarketConfigDTO> request, TradeMarketConfigDTO tradeMarketConfigDTO,
                                   StandardMatchInfo oldStandardMatchInfo,
                                   StandardSportMarketSell standardSportMarketSell, Map<String,
            StandardMarketDataMessage> stringStandardMarketDataMessageMap) {
        if (Constant.TRADE_MARKET_CONFIG.SOURCE_SYSTEM.TRADER_SYSEM.equals(tradeMarketConfigDTO.getSourceSystem())) {
            log.info("::{}::putTradeMarketConfig ,标准赛事id：{},获取缓存数据总数：{}", request.getLinkId(),
                    oldStandardMatchInfo.getId(), stringStandardMarketDataMessageMap.size());
            //--------------当为赛事级别操盘时----------------
            if (Constant.TRADE_MARKET_CONFIG.LEVEL.MATCH.equals(tradeMarketConfigDTO.getLevel())) {
                //------------如果是赛事级别手自动切换----------
                if (tradeMarketConfigDTO.getTradeType() != null) {
                    log.info("::{}::赛事切换操盘方式。tradeType={}, standardMatchId={}", request.getLinkId(),
                            tradeMarketConfigDTO.getTradeType(), oldStandardMatchInfo.getId());
                    //获取当前赛事级别手自动配置,不存在新增，存在更新
                    ConfigTradeType configTradeType =
                            configTradeTypeService.getItemMatch(oldStandardMatchInfo.getId().toString());
                    if (configTradeType == null) {
                        configTradeTypeService.createMatch(tradeMarketConfigDTO);
                    } else {
                        configTradeType.setTradeType(tradeMarketConfigDTO.getTradeType());
                        configTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        configTradeType.setOperaterId(tradeMarketConfigDTO.getOperaterId());
                        configTradeTypeService.updateMatch(configTradeType);
                    }
                    //玩法级别的手自动设置>赛事级别的手自动设置。所以当切换赛事级别手自动时，需要删除玩法级别的手自动设置
                    configTradeTypeService.deleteCategoryByStandardMatchId(tradeMarketConfigDTO.getTargetId());
                    //存储当前数据里面的玩法
                    Set<Long> marketCategoryIdSet = new HashSet();
                    //根据手自动切换类型处理数据
                    stringStandardMarketDataMessageMap.values().forEach(standardMarketDataMessage -> {
                        //----------------如果是自动切手动，下发强制封盘-------------
                        if (tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.MANUAL)
                                || tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_MODEL)
                                || tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_NEW)) {
                            if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getThirdMarketSourceStatus())) {
                                standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                            }
                        }
                        marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                    });
                    //---------------处理盘口计算和排序-----------------
                    thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), oldStandardMatchInfo,
                            marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                            standardSportMarketSell, new HashMap<>());
                    return;
                }
                //-----------如果是操盘赛事级别开关封锁---------
                //用于本次修改的标准赛事
                StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
                upStandardMatchInfo.setId(oldStandardMatchInfo.getId());
                //修改标准赛事的操盘赛事状态
                //todo operateMatchStatus直接设值需要执行相关脚本，且需要告知业务同步更改
                upStandardMatchInfo.setOperateMatchStatus(tradeMarketConfigDTO.getMarketStatus());
                //修改标准赛事
                oldStandardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
                //bug 47948 : 操盘赛事级别 开关封锁，单独下发topic，以免赔率topic堆积，导致赛事状态不能及时更新
                standardMarketOddsProducer.standardMarketOddsStateSend(request.getLinkId(), oldStandardMatchInfo, request.getDataSourceTime());
                log.info("::{}::putTradeMarketConfig ,标准赛事id:{},修改赛事状态:{},获取缓存数据总数111：{}", request.getLinkId(),
                        oldStandardMatchInfo.getId(), tradeMarketConfigDTO.getMarketStatus(),
                        stringStandardMarketDataMessageMap.size());
                //如果是操盘关，封，锁，直接下发赛事状态
                if (!Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE.equals(tradeMarketConfigDTO.getMarketStatus())) {
                    standardMarketOddsProducer.standardMarketOddsAsyncSend(request.getLinkId(), oldStandardMatchInfo,
                            null, request.getDataSourceTime(), false);
                    log.info("::{}::putTradeMarketConfig ,标准赛事id：{},获取缓存数据总数2222：{}", request.getLinkId(),
                            oldStandardMatchInfo.getId(), stringStandardMarketDataMessageMap.size());
                } else {
                    //过滤当前数据里面的玩法
                    log.info("::{}::putTradeMarketConfig ,标准赛事id：{},获取缓存数据总数3333：{}", request.getLinkId(),
                            oldStandardMatchInfo.getId(), stringStandardMarketDataMessageMap.size());
                    Set<Long> marketCategoryIdSet = new HashSet();
                    stringStandardMarketDataMessageMap.values().forEach(standardMarketDataMessage -> {
                        marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                    });
                    //如果是操盘赛事级别开，下发当前最新赔率
                    log.info("::{}::putTradeMarketConfig ,标准赛事id：{},marketCategoryIdSet：{}", request.getLinkId(),
                            oldStandardMatchInfo.getId(), marketCategoryIdSet.size());
                    thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), oldStandardMatchInfo,
                            marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                            standardSportMarketSell, new HashMap<>());
                }
            }
            //--------------当为玩法级别操盘时----------------
            if (Constant.TRADE_MARKET_CONFIG.LEVEL.MARKET_CATEGORY.equals(tradeMarketConfigDTO.getLevel())) {
                //玩法操盘当前只有手自动切换，玩法开关封锁合并到到盘口位置接口
                if (tradeMarketConfigDTO.getTradeType() == null) {
                    log.info("::{}::玩法操盘只支持手自动切换", request.getLinkId());
                    return;
                }
                log.info("::{}::玩法切换操盘方式。操盘类型:{}, 标准赛事id:{}，categoryId:{}", request.getLinkId(),
                        tradeMarketConfigDTO.getTradeType(), oldStandardMatchInfo.getId(),
                        tradeMarketConfigDTO.getAddition1());
                //获取当前玩法级别手自动配置,不存在新增，存在更新
                String[] categoryIds = tradeMarketConfigDTO.getAddition1().split(",");
                //当前缓存盘口数据里面的玩法
                Set<Long> marketCategoryIdSet = new HashSet();
                //玩法维度的操盘配置
                Map<Long, Integer> tradeTypeMap = new HashMap<>();
                for (String categoryId : categoryIds) {
                    //根据风控要求，在切换操盘方式的时候，需要融合内部组装玩法级封盘操作
                    createTradeMarketPlaceConfig(request.getLinkId() + "_" + categoryId, oldStandardMatchInfo.getId(), Long.parseLong(categoryId), request.getOperaterId());

                    ConfigTradeType itemCategory =
                            configTradeTypeService.getItemCategory(tradeMarketConfigDTO.getTargetId(), categoryId);
                    if (itemCategory == null) {
                        configTradeTypeService.createCategory(tradeMarketConfigDTO, categoryId);
                    } else {
                        itemCategory.setTradeType(tradeMarketConfigDTO.getTradeType());
                        itemCategory.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        itemCategory.setOperaterId(tradeMarketConfigDTO.getOperaterId());
                        configTradeTypeService.updateCategory(itemCategory);
                    }
                    marketCategoryIdSet.add(Long.valueOf(categoryId));
                    tradeTypeMap.put(Long.parseLong(categoryId), tradeMarketConfigDTO.getTradeType());
                }

                // 关键修复：玩法切换导致“强制封盘”时，只下发【数据商当前仍为ACTIVE】的盘口。
                // 否则历史/残留的ACTIVE盘口（数据商已不再提供）也会被封盘下发，导致操盘列表统计盘口数量错误。
                boolean forceSuspend =
                        tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.MANUAL)
                                || tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS)
                                || tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_MODEL)
                                || tradeMarketConfigDTO.getTradeType().equals(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_NEW);
                if (forceSuspend && !CollectionUtils.isEmpty(marketCategoryIdSet) && MapUtils.isNotEmpty(stringStandardMarketDataMessageMap)) {
                    boolean isOutRight = StringUtils.equals("1", tradeMarketConfigDTO.getMatchType());
                    // key = dataSourceCode_marketType
                    Map<String, Set<String>> activeThirdMarketSourceIds = new HashMap<>();
                    // 仅按本次涉及的玩法，从缓存中找出需要校验的数据源+盘类型组合
                    Set<Long> finalMarketCategoryIdSet = marketCategoryIdSet;
                    Set<String> dsTypeKeySet = stringStandardMarketDataMessageMap.values().stream()
                            .filter(m -> m != null
                                    && finalMarketCategoryIdSet.contains(m.getMarketCategoryId())
                                    && m.getMarketType() != null
                                    && !org.springframework.util.StringUtils.isEmpty(m.getDataSourceCode()))
                            .map(m -> m.getDataSourceCode() + "_" + m.getMarketType())
                            .collect(Collectors.toSet());
                    for (String dsTypeKey : dsTypeKeySet) {
                        String[] parts = dsTypeKey.split("_");
                        if (parts.length != 2) {
                            continue;
                        }
                        String dataSourceCode = parts[0];
                        Integer marketType = Integer.valueOf(parts[1]);
                        ThirdMatchInfo thirdMatchInfo = thirdMatchMarketProcessor.getThirdMatchInfoByMatchId(isOutRight, oldStandardMatchInfo.getId(), dataSourceCode);
                        if (thirdMatchInfo == null) {
                            log.info("::{}::玩法切换强制封盘-无法获取三方赛事，matchId={},dataSourceCode={}", request.getLinkId(), oldStandardMatchInfo.getId(), dataSourceCode);
                            continue;
                        }
                        List<ThirdSportMarket> thirdSportMarkets =
                                thirdSportMarketService.getItemList(thirdMatchInfo.getId(), dataSourceCode, marketType, new ArrayList<>(marketCategoryIdSet));
                        if (CollectionUtils.isEmpty(thirdSportMarkets)) {
                            activeThirdMarketSourceIds.put(dsTypeKey, Collections.emptySet());
                            continue;
                        }
                        Set<String> activeSet = thirdSportMarkets.stream()
                                .filter(m -> m != null
                                        && m.getThirdMarketSourceStatus() != null
                                        && m.getThirdMarketSourceStatus().equals(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE)
                                        && !org.springframework.util.StringUtils.isEmpty(m.getThirdMarketSourceId()))
                                .map(ThirdSportMarket::getThirdMarketSourceId)
                                .collect(Collectors.toSet());
                        activeThirdMarketSourceIds.put(dsTypeKey, activeSet);
                    }

                    // 对本次涉及玩法：剔除“数据商当前非ACTIVE”的盘口，避免被下发到操盘导致数量异常
                    Iterator<Map.Entry<String, StandardMarketDataMessage>> it = stringStandardMarketDataMessageMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, StandardMarketDataMessage> e = it.next();
                        StandardMarketDataMessage m = e.getValue();
                        if (m == null || !marketCategoryIdSet.contains(m.getMarketCategoryId())) {
                            continue;
                        }
                        String dsTypeKey = m.getDataSourceCode() + "_" + m.getMarketType();
                        Set<String> activeSet = activeThirdMarketSourceIds.get(dsTypeKey);
                        // activeSet==null 表示无法校验（缺少三方赛事信息等），此时不做剔除，避免误删；但若能校验且不在active集合，则剔除
                        if (activeSet != null && (org.springframework.util.StringUtils.isEmpty(m.getThirdMarketSourceId()) || !activeSet.contains(m.getThirdMarketSourceId()))) {
                            it.remove();
                            continue;
                        }
                        // 能校验且为ACTIVE盘口时，执行强制封盘
                        if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(m.getThirdMarketSourceStatus())) {
                            m.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        }
                    }

                    // 重新计算本次仍有盘口可下发的玩法集合
                    Set<Long> finalMarketCategoryIdSet1 = marketCategoryIdSet;
                    Set<Long> sendCategorySet = stringStandardMarketDataMessageMap.values().stream()
                            .filter(m -> m != null && finalMarketCategoryIdSet1.contains(m.getMarketCategoryId()))
                            .map(StandardMarketDataMessage::getMarketCategoryId)
                            .collect(Collectors.toSet());
                    marketCategoryIdSet = sendCategorySet;
                }

                //---------------处理盘口计算和排序-----------------
                thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(), request.getOperaterId(),oldStandardMatchInfo,
                        marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                        standardSportMarketSell, new HashMap<>());
            }
        }
        return;
    }

    /**
     * 这里增加一个逻辑，判断风控传来的玩法是否含有新增的，如果含有新增的玩法，提示失败
     *
     * @param tradeMarketConfigDTO
     * @param stringStandardMarketDataMessageMap
     * @return
     */
    private List<Long> checkHaveAddMarketgory(TradeMarketConfigDTO tradeMarketConfigDTO, Map<String,
            StandardMarketDataMessage> stringStandardMarketDataMessageMap) {
        String[] categoryIds = tradeMarketConfigDTO.getAddition1().split(",");
        Set<Long> marketSetIds =
                stringStandardMarketDataMessageMap.values().stream().map(StandardMarketDataMessage::getMarketCategoryId).collect(Collectors.toSet());
        List<Long> cates = new ArrayList<Long>();
        for (String categoryId : categoryIds) {
            cates.add(Long.valueOf(categoryId));
        }
        cates.removeAll(marketSetIds);
        return cates;
    }

    /**
     * 融合自己创建玩法级封盘
     *
     * @param linkId
     * @param standardMatchInfoId
     * @param standardCategoryId
     * @param operaterId
     */
    public void createTradeMarketPlaceConfig(String linkId, Long standardMatchInfoId, Long standardCategoryId, Long operaterId) {
        log.info("::{}::createTradeMarketPlaceConfig入参 :standardMatchInfoId={},standardCategoryId={},处理开始", linkId, standardMatchInfoId, standardCategoryId);
        //收集需要保存的数据，批量保存
        List<ConfigMarketCategoryPlace> categoryPlaceList = new ArrayList<>();
        //收集需要删除盘口位置玩法
        Set<Long> delMarketCategoryIdSet = new HashSet();
        //先删除玩法已经存在的盘口位置状态
        delMarketCategoryIdSet.add(standardCategoryId);
        //缓存盘口位置
        for (int i = 1; i < 11; i++) {
            ConfigMarketCategoryPlace categoryPlace = new ConfigMarketCategoryPlace();
            categoryPlace.setId(UUIdUtils.getId());
            categoryPlace.setLinkId(linkId);
            categoryPlace.setStandardMatchInfoId(standardMatchInfoId);
            categoryPlace.setPlaceNum(i);
            categoryPlace.setStandardCategoryId(standardCategoryId);
            categoryPlace.setChildStandardCategoryId(standardCategoryId);
            categoryPlace.setPlaceNumStatus(String.valueOf(Constant.SPORT_MARKET.STATUS.SUSPENDED));
            categoryPlace.setOperaterId(operaterId);
            categoryPlace.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            categoryPlace.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            categoryPlaceList.add(categoryPlace);
        }
        //批量插入盘口位置
        if (!CollectionUtils.isEmpty(categoryPlaceList)) {
            configMarketCategoryPlaceService.cacheConfigMarketPlace(categoryPlaceList, linkId, standardMatchInfoId);
        }
        log.info("::{}::createTradeMarketPlaceConfig入参 :standardMatchInfoId={},standardCategoryId={},处理结束", linkId, standardMatchInfoId, standardCategoryId);
    }

    @Override
    public Response putTradeMarketPlaceConfig(Request<TradeMarketPlaceConfigDTO> request) {
        StopWatch swRedis = new StopWatch(UUID.randomUUID().toString());
        swRedis.start("盘口查询");
        //判断赛事类型
        String linkId = request.getLinkId();
        //validateLinkId("putTradeMarketPlaceConfig", request);
        log.info("::{}::putTradeMarketPlaceConfig入参 ={}", linkId, JSON.toJSONString(request));
        TradeMarketPlaceConfigDTO marketPlaceConfigDTO = request.getData();
        Long standardMatchInfoId = marketPlaceConfigDTO.getStandardMatchInfoId();
        boolean isOutRight = StringUtils.equals("1", marketPlaceConfigDTO.getMatchType());

        //获取标准赛事
        StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutRight,
                standardMatchInfoId);
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeMarketPlaceConfig,标准赛事未找到，标准赛事id:{}", linkId, standardMatchInfoId);
            return Response.failed("标准赛事未找到");
        }
        //获取开售信息
        StandardSportMarketSell standardSportMarketSell =
                thirdMatchMarketProcessor.getStandardSportMarketSell(isOutRight, standardMatchInfoId);
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeMarketPlaceConfig,标准赛事未开售，标准赛事id:{}", linkId, standardMatchInfoId);
            return Response.failed("标准赛事未开售");
        }
        swRedis.stop();
        log.info("::{}::盘口查询{}ms," + swRedis.prettyPrint(), linkId, swRedis.getTotalTimeMillis());

        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("盘口位置入库处理");
        //收集需要保存的数据，批量保存
        List<ConfigMarketCategoryPlace> categoryPlaceList = new ArrayList<>();
        //收集本次有改变的玩法
        Set<Long> marketCategoryIdSet = new HashSet();
        Long operaterId = request.getOperaterId();
        //循环处理配置数据
        marketPlaceConfigDTO.getMarketPlaceDtlDTOList().forEach(marketPlaceDtlDTO -> {
            if (null == marketPlaceDtlDTO.getChildStandardCategoryId()) {
                marketPlaceDtlDTO.setChildStandardCategoryId(marketPlaceDtlDTO.getStandardCategoryId());
            }
            if (marketPlaceDtlDTO.getPlaceNum() == -1) {
                //缓存盘口位置
                for (int i = 1; i < 11; i++) {
                    ConfigMarketCategoryPlace categoryPlace = new ConfigMarketCategoryPlace();
                    categoryPlace.setId(UUIdUtils.getId());
                    categoryPlace.setLinkId(linkId);
                    categoryPlace.setStandardMatchInfoId(standardMatchInfoId);
                    categoryPlace.setPlaceNum(i);
                    categoryPlace.setStandardCategoryId(marketPlaceDtlDTO.getStandardCategoryId());
                    categoryPlace.setChildStandardCategoryId(marketPlaceDtlDTO.getChildStandardCategoryId());
                    categoryPlace.setPlaceNumStatus(marketPlaceDtlDTO.getPlaceNumStatus());
                    categoryPlace.setOperaterId(operaterId);
                    categoryPlace.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    categoryPlace.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    categoryPlaceList.add(categoryPlace);
                }
            } else {
                ConfigMarketCategoryPlace categoryPlace = new ConfigMarketCategoryPlace();
                categoryPlace.setId(UUIdUtils.getId());
                categoryPlace.setLinkId(linkId);
                categoryPlace.setStandardMatchInfoId(standardMatchInfoId);
                categoryPlace.setPlaceNum(marketPlaceDtlDTO.getPlaceNum());
                categoryPlace.setStandardCategoryId(marketPlaceDtlDTO.getStandardCategoryId());
                categoryPlace.setChildStandardCategoryId(marketPlaceDtlDTO.getChildStandardCategoryId());
                categoryPlace.setPlaceNumStatus(marketPlaceDtlDTO.getPlaceNumStatus());
                categoryPlace.setOperaterId(operaterId);
                categoryPlace.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                categoryPlace.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                categoryPlaceList.add(categoryPlace);
            }
            marketCategoryIdSet.add(marketPlaceDtlDTO.getStandardCategoryId());
        });
        if (!CollectionUtils.isEmpty(categoryPlaceList)) {
            configMarketCategoryPlaceService.cacheConfigMarketPlace(categoryPlaceList, linkId, standardMatchInfoId);
        }
        log.info("::{}::盘口位置及状态处理完成", linkId);
        sw.stop();
        log.info("::{}::盘口位置入库处理总耗时{}ms," + sw.prettyPrint(), linkId, sw.getTotalTimeMillis());
        //模式判断
        Set<Long> changeCategoryIdSet = new HashSet<>();
        marketCategoryIdSet.forEach(categoryId -> {
            ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(linkId, standardMatchInfo.getId(), categoryId);
            Integer tradeTypeDB = 0;
            if (null != configTradeType) {
                tradeTypeDB = configTradeType.getTradeType();
            }
            if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeDB)) {
                log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", linkId, standardMatchInfoId, categoryId);
                return;
            }
            changeCategoryIdSet.add(categoryId);
        });
        if (CollectionUtils.isEmpty(changeCategoryIdSet)) {
            log.info("::{}::putTradeMarketPlaceConfig,下发玩法不存在", linkId);
            return Response.success();
        }
        //下发当前最新赔率
        //获取缓存中的所有盘口
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(changeCategoryIdSet, linkId, standardMatchInfo, standardSportMarketSell);
        //--------操盘后台操作开关封锁,异步处理-----------
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, changeCategoryIdSet,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }


    @Override
    public Response putTradeMarketUiConfig(Request<TradeMarketUiConfigDTO> request) {
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("盘口位置、最大最小值、margin、水差入库处理");
        //判断赛事类型
        String linkId = request.getLinkId();
        //validateLinkId("putTradeMarketUiConfig", request);
        log.info("::{}::putTradeMarketUiConfig入参 ={}", linkId, JSON.toJSONString(request));
        TradeMarketUiConfigDTO marketPlaceConfigDTO = request.getData();
        Long operaterId = request.getOperaterId();
        Long standardMatchInfoId = marketPlaceConfigDTO.getStandardMatchInfoId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo == null) {
            log.info("::{}::标准赛事未找到，标准赛事id:{}", request.getLinkId(), standardMatchInfoId);
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::赛事未开售，标准赛事id：{}", request.getLinkId(), standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        Boolean isExistChildCategoryId = Boolean.TRUE;
        if (ObjectUtils.isEmpty(marketPlaceConfigDTO.getChildStandardCategoryId())) {
            isExistChildCategoryId = Boolean.FALSE;
            marketPlaceConfigDTO.setChildStandardCategoryId(marketPlaceConfigDTO.getStandardCategoryId());
        }
        //--------处理最大最小赔率---------
        List<TradeMarketConfigItemDTO> marketConfigs = marketPlaceConfigDTO.getMarketConfigs();
        if (!CollectionUtils.isEmpty(marketConfigs)) {
            marketConfigs.forEach(marketConfig -> {
                if (ObjectUtils.isEmpty(marketPlaceConfigDTO.getStandardCategoryId())) {
                    marketPlaceConfigDTO.setStandardCategoryId(marketConfig.getMarketCategoryId());
                }
                if (ObjectUtils.isEmpty(marketConfig.getChildStandardCategoryId())) {
                    marketConfig.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                }
                ConfigMarketTradeItem item = configMarketTradeItemService.getItem(standardMatchInfoId,
                        marketConfig.getMarketCategoryId(), marketPlaceConfigDTO.getChildStandardCategoryId(), marketPlaceConfigDTO.getPlaceNum());
                if (item == null) {
                    configMarketTradeItemService.create(request.getLinkId(), marketConfig, standardMatchInfoId,
                            marketPlaceConfigDTO.getPlaceNum(), operaterId);
                } else {
                    item.setMaxOddsValue(marketConfig.getMaxOddsValue());
                    item.setMinOddsValue(marketConfig.getMinOddsValue());
                    item.setOperaterId(operaterId);
                    item.setLinkId(request.getLinkId());
                    item.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                    configMarketTradeItemService.update(item);
                }
            });
        }
        //-------处理margin配置-----------
        List<MarketMarginDtlDTO> marketMarginDtlDTOList = marketPlaceConfigDTO.getMarketMarginDtlDTOList();
        if (!CollectionUtils.isEmpty(marketMarginDtlDTOList)) {
            //处理margin设置
            marketMarginDtlDTOList.forEach(marketMarginDtlDTO -> {
                if (ObjectUtils.isEmpty(marketMarginDtlDTO.getChildStandardCategoryId())) {
                    marketMarginDtlDTO.setChildStandardCategoryId(marketPlaceConfigDTO.getStandardCategoryId());
                }
                if (ObjectUtils.isEmpty(marketMarginDtlDTO.getPlaceNum())){
                    marketMarginDtlDTO.setPlaceNum(marketPlaceConfigDTO.getPlaceNum());
                }
                //收集日志
                marketCategoryMarginLogService.create(request.getLinkId(),
                        marketPlaceConfigDTO.getStandardMatchInfoId(), marketPlaceConfigDTO.getStandardCategoryId(),
                        marketPlaceConfigDTO.getChildStandardCategoryId(),
                        marketPlaceConfigDTO.getMarketType(),
                        marketPlaceConfigDTO.getPlaceNum(), marketMarginDtlDTO, operaterId);
                //二项盘的设置
                ConfigMarketCategoryMargin itemTwo = configMarketCategoryMarginService.getItemTwo(linkId,
                        standardMatchInfoId, marketPlaceConfigDTO.getStandardCategoryId(), marketPlaceConfigDTO.getChildStandardCategoryId(),
                        marketMarginDtlDTO.getPlaceNum());
                if (itemTwo == null) {
                    ConfigMarketCategoryMargin configMarketCategoryMargin = new ConfigMarketCategoryMargin();
                    configMarketCategoryMargin.setId(UUIdUtils.getId());
                    configMarketCategoryMargin.setStandardMatchInfoId(marketPlaceConfigDTO.getStandardMatchInfoId());
                    configMarketCategoryMargin.setStandardCategoryId(marketPlaceConfigDTO.getStandardCategoryId());
                    configMarketCategoryMargin.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                    configMarketCategoryMargin.setMarketType(marketPlaceConfigDTO.getMarketType());
                    configMarketCategoryMargin.setPlaceNum(marketMarginDtlDTO.getPlaceNum());
                    configMarketCategoryMargin.setTimeFrame(marketMarginDtlDTO.getTimeFrame());
                    configMarketCategoryMargin.setMargin(marketMarginDtlDTO.getMargin());
                    configMarketCategoryMargin.setLinkId(request.getLinkId());
                    configMarketCategoryMargin.setOperaterId(operaterId);
                    configMarketCategoryMargin.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    configMarketCategoryMargin.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    //创建两项盘的spread
                    configMarketCategoryMarginService.createTwo(configMarketCategoryMargin);
                } else {
                    itemTwo.setMarketType(marketPlaceConfigDTO.getMarketType());
                    itemTwo.setMargin(marketMarginDtlDTO.getMargin());
                    itemTwo.setOperaterId(operaterId);
                    itemTwo.setLinkId(request.getLinkId());
                    //更新两项盘的spread
                    configMarketCategoryMarginService.updateTwo(itemTwo);
                }
            });
            //总玩法分时margin，批量修改子玩法margin为分时margin ,700需求带{X}玩法
            if (!isExistChildCategoryId && MarginCategoryConfig.CATEGORY_X_UPDATE_MARGIN.contains(marketPlaceConfigDTO.getStandardCategoryId())) {
                Double margin = marketMarginDtlDTOList.get(0).getMargin();
                configMarketCategoryMarginService.updateByCategory(linkId, standardMatchInfoId, marketPlaceConfigDTO.getStandardCategoryId(), margin);
            }
        }
        //--------处理位置开关封锁配置---------------
        boolean isOutRight = StringUtils.equals("1", marketPlaceConfigDTO.getMatchType());
        //收集需要保存的数据，批量保存
        List<ConfigMarketCategoryPlace> categoryPlaceList = new ArrayList<>();
        //收集需要修改的数据，批量修改
        List<ConfigMarketCategoryPlace> categoryPlaceUpdateList = new ArrayList<>();
        //循环处理配置数据
        List<MarketPlaceDtlDTO> marketPlaceDtlDTOList = marketPlaceConfigDTO.getMarketPlaceDtlDTOList();
        if (!CollectionUtils.isEmpty(marketPlaceDtlDTOList)) {
            marketPlaceDtlDTOList.forEach(marketPlaceDtlDTO -> {
                if (null == marketPlaceDtlDTO.getChildStandardCategoryId()) {
                    marketPlaceDtlDTO.setChildStandardCategoryId(marketPlaceDtlDTO.getStandardCategoryId());
                }
                if (ObjectUtils.isEmpty(marketPlaceDtlDTO.getChildStandardCategoryId())) {
                    marketPlaceDtlDTO.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                }
                if (marketPlaceDtlDTO.getPlaceNum() == -1) {
                    //缓存盘口位置
                    for (int i = 1; i < 11; i++) {
                        ConfigMarketCategoryPlace categoryPlace = new ConfigMarketCategoryPlace();
                        categoryPlace.setId(UUIdUtils.getId());
                        categoryPlace.setLinkId(linkId);
                        categoryPlace.setStandardMatchInfoId(standardMatchInfoId);
                        categoryPlace.setPlaceNum(i);
                        categoryPlace.setStandardCategoryId(marketPlaceDtlDTO.getStandardCategoryId());
                        categoryPlace.setChildStandardCategoryId(marketPlaceDtlDTO.getChildStandardCategoryId());
                        categoryPlace.setPlaceNumStatus(marketPlaceDtlDTO.getPlaceNumStatus());
                        categoryPlace.setOperaterId(operaterId);
                        categoryPlace.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        categoryPlace.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        categoryPlaceList.add(categoryPlace);
                    }
                } else {
                    ConfigMarketCategoryPlace categoryPlace = new ConfigMarketCategoryPlace();
                    BeanUtils.copyProperties(marketPlaceDtlDTO, categoryPlace);
                    categoryPlace.setId(UUIdUtils.getId());
                    categoryPlace.setLinkId(linkId);
                    categoryPlace.setStandardMatchInfoId(standardMatchInfoId);
                    categoryPlace.setChildStandardCategoryId(marketPlaceDtlDTO.getChildStandardCategoryId());
                    categoryPlace.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    categoryPlace.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    categoryPlace.setOperaterId(operaterId);
                    categoryPlaceList.add(categoryPlace);
                }
            });
            if (!CollectionUtils.isEmpty(categoryPlaceList)) {
                configMarketCategoryPlaceService.cacheConfigMarketPlace(categoryPlaceList, linkId, standardMatchInfoId);
            }
        }
        //--------处理盘口水差配置------------
        List<TradeMarketAutoDiffConfigItemDTO> diffConfigs = marketPlaceConfigDTO.getDiffConfigs();
        if (!CollectionUtils.isEmpty(diffConfigs)) {
            diffConfigs.forEach(diffConfig -> {
                if (ObjectUtils.isEmpty(marketPlaceConfigDTO.getStandardCategoryId())) {
                    marketPlaceConfigDTO.setStandardCategoryId(diffConfig.getMarketCategoryId());
                }
                if (ObjectUtils.isEmpty(diffConfig.getChildStandardCategoryId())) {
                    diffConfig.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                }
                //收集日志
                configMarketAutoDiffTradeLogService.create(request.getLinkId(), diffConfig, standardMatchInfoId,
                        operaterId);
                ConfigMarketAutoDiffTrade item = configMarketAutoDiffTradeService.getItem(linkId, standardMatchInfoId,
                        diffConfig.getMarketId(), diffConfig.getOddType());
                if (item == null) {
                    configMarketAutoDiffTradeService.create(request.getLinkId(), diffConfig, standardMatchInfoId,
                            operaterId);
                } else {
                    item.setDiffValue(diffConfig.getDiffValue());
                    item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    item.setLinkId(request.getLinkId());
                    item.setOperaterId(operaterId);
                    configMarketAutoDiffTradeService.updata(item);
                }
            });
        }

        //处理位置水差配置
        List<TradePlaceNumAutoDiffConfigItemDTO> placeNumDiffConfigs = marketPlaceConfigDTO.getPlaceNumDiffConfigs();
        //处理玩法水差配置
        TradeCategoryAutoDiffConfigItemDTO categoryDiffConfig = marketPlaceConfigDTO.getCategoryDiffConfig();

        if (!CollectionUtils.isEmpty(placeNumDiffConfigs)) {
            placeNumDiffConfigs.forEach(placeNumDiffConfig -> {
                if (ObjectUtil.isNotEmpty(placeNumDiffConfig)) {
                    if (ObjectUtils.isEmpty(marketPlaceConfigDTO.getStandardCategoryId())) {
                        marketPlaceConfigDTO.setStandardCategoryId(placeNumDiffConfig.getMarketCategoryId());
                    }
                    if (ObjectUtils.isEmpty(placeNumDiffConfig.getChildStandardCategoryId())) {
                        placeNumDiffConfig.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                    }
                    ConfigPlacenumAutoDiffTrade item = configPlaceNumAutoDiffTradeService.getItem(request.getLinkId()
                            , marketPlaceConfigDTO.getStandardMatchInfoId(), placeNumDiffConfig.getMarketCategoryId(), marketPlaceConfigDTO.getChildStandardCategoryId(),
                            placeNumDiffConfig.getPlaceNum());
                    if (item == null) {
                        configPlaceNumAutoDiffTradeService.create(request.getLinkId(), placeNumDiffConfig,
                                marketPlaceConfigDTO.getStandardMatchInfoId(), operaterId);
                    } else {
                        item.setDiffValue(placeNumDiffConfig.getDiffValue());
                        item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        item.setLinkId(request.getLinkId());
                        item.setOperaterId(operaterId);
                        configPlaceNumAutoDiffTradeService.updata(item);
                    }
                    if (ObjectUtil.isEmpty(categoryDiffConfig)) {
                        //如果玩法水差为空，则设置玩法水差主盘的下盘投注项
                        ConfigCategoryAutoDiffTrade categoryAutoDiffTrade =
                                configCategoryAutoDiffTradeService.getItem(request.getLinkId(),
                                        marketPlaceConfigDTO.getStandardMatchInfoId(),
                                        placeNumDiffConfig.getMarketCategoryId(), marketPlaceConfigDTO.getChildStandardCategoryId());
                        if (null == categoryAutoDiffTrade) {
                            TradeCategoryAutoDiffConfigItemDTO tradeCategoryAutoDiffConfigItemDTO =
                                    new TradeCategoryAutoDiffConfigItemDTO();
                            tradeCategoryAutoDiffConfigItemDTO.setMarketCategoryId(placeNumDiffConfig.getMarketCategoryId());
                            tradeCategoryAutoDiffConfigItemDTO.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                            tradeCategoryAutoDiffConfigItemDTO.setDiffValue(0.0);
                            tradeCategoryAutoDiffConfigItemDTO.setOddType(placeNumDiffConfig.getOddType());
                            configCategoryAutoDiffTradeService.create(request.getLinkId(),
                                    tradeCategoryAutoDiffConfigItemDTO, marketPlaceConfigDTO.getStandardMatchInfoId(),
                                    operaterId);
                        }
                    }
                }
            });
        }


        if (ObjectUtil.isNotEmpty(categoryDiffConfig)) {
            if (ObjectUtils.isEmpty(marketPlaceConfigDTO.getStandardCategoryId())) {
                marketPlaceConfigDTO.setStandardCategoryId(categoryDiffConfig.getMarketCategoryId());
            }
            if (ObjectUtils.isEmpty(categoryDiffConfig.getChildStandardCategoryId())) {
                categoryDiffConfig.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
            }
            ConfigCategoryAutoDiffTrade item = configCategoryAutoDiffTradeService.getItem(request.getLinkId(),
                    marketPlaceConfigDTO.getStandardMatchInfoId(), categoryDiffConfig.getMarketCategoryId(), categoryDiffConfig.getChildStandardCategoryId());
            if (item == null) {
                configCategoryAutoDiffTradeService.create(request.getLinkId(), categoryDiffConfig,
                        marketPlaceConfigDTO.getStandardMatchInfoId(), operaterId);
            } else {
                item.setDiffValue(categoryDiffConfig.getDiffValue());
                item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                item.setLinkId(request.getLinkId());
                item.setOperaterId(operaterId);
                configCategoryAutoDiffTradeService.updata(item);
            }
        }
        //独赢盘配置
        List<MarketMarginGapDtlDTO> marginGapDtlDTOList = marketPlaceConfigDTO.getMarginGapDtlDTOList();
        if (!CollectionUtils.isEmpty(marginGapDtlDTOList)) {
            List<ConfigMarketMarginGap> createList = new ArrayList<>();
            List<ConfigMarketMarginGap> updateList = new ArrayList<>();
            List<ConfigMarketMarginGapLog> logList = new ArrayList<>();
            for (MarketMarginGapDtlDTO itemDto : marginGapDtlDTOList) {
                ConfigMarketMarginGap marketMargin = new ConfigMarketMarginGap();
                ConfigMarketMarginGapLog log = new ConfigMarketMarginGapLog();
                //查询配置
                ConfigMarketMarginGap configMarketMarginGap = configMarketMarginGapService.getItem(standardMatchInfoId, marketPlaceConfigDTO.getStandardCategoryId(), marketPlaceConfigDTO.getChildStandardCategoryId(), itemDto.getOddsType(), marketPlaceConfigDTO.getPlaceNum());
                if (null == configMarketMarginGap) {
                    marketMargin.setMatchId(marketPlaceConfigDTO.getStandardMatchInfoId());
                    marketMargin.setMarketCategoryId(marketPlaceConfigDTO.getStandardCategoryId());
                    marketMargin.setLinkageMode(marketPlaceConfigDTO.getLinkageMode());
                    BeanUtils.copyProperties(itemDto, marketMargin);
                    marketMargin.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                    marketMargin.setId(IdWorker.getId());
                    marketMargin.setPlaceNum(marketPlaceConfigDTO.getPlaceNum());
                    marketMargin.setOperaterId(operaterId);
                    marketMargin.setLinkId(linkId);
                    createList.add(marketMargin);
                } else {
                    BeanUtils.copyProperties(itemDto, marketMargin);
                    marketMargin.setLinkageMode(marketPlaceConfigDTO.getLinkageMode());
                    marketMargin.setMatchId(marketPlaceConfigDTO.getStandardMatchInfoId());
                    marketMargin.setMarketCategoryId(marketPlaceConfigDTO.getStandardCategoryId());
                    marketMargin.setChildStandardCategoryId(marketPlaceConfigDTO.getChildStandardCategoryId());
                    marketMargin.setId(configMarketMarginGap.getId());
                    marketMargin.setOperaterId(operaterId);
                    marketMargin.setPlaceNum(marketPlaceConfigDTO.getPlaceNum());
                    marketMargin.setLinkId(linkId);
                    updateList.add(marketMargin);
                }
                BeanUtils.copyProperties(marketMargin, log);
                log.setId(IdWorker.getId());
                logList.add(log);
            }
            if (!CollectionUtils.isEmpty(logList)) {
                //configMarketMarginGapLogService.createList(logList);
            }
            if (!CollectionUtils.isEmpty(createList)) {
                configMarketMarginGapService.insertList(linkId, standardMatchInfoId, createList);
            }
            if (!CollectionUtils.isEmpty(updateList)) {
                configMarketMarginGapService.updateList(linkId, standardMatchInfoId, updateList);
            }
            //总玩法分时margin，批量修改子玩法margin为分时margin ,700需求带{X}玩法
            if (!isExistChildCategoryId && MarginCategoryConfig.CATEGORY_X_UPDATE_MARGIN.contains(marketPlaceConfigDTO.getStandardCategoryId())) {
                Double margin = marginGapDtlDTOList.get(0).getMargin();
                configMarketMarginGapService.updateByCategory(linkId, standardMatchInfoId, marketPlaceConfigDTO.getStandardCategoryId(), margin);
            }
        }
//        //提前结算配置
//        ConfigCashOutTradeItemDTO cashOutTradeItemDTO = marketPlaceConfigDTO.getConfigCashOutTradeItemDTO();
//        if (cashOutTradeItemDTO != null) {
//            log.info("打印cashOutTradeItemDTO模版:"+JSON.toJSONString(cashOutTradeItemDTO));
//            //根据赛事级别状态来判断 赛事 还是 玩法级别 1：赛事、2：玩法
//            Integer matchPreStatus = cashOutTradeItemDTO.getMatchPreStatus();
//            //判断是赛事级操作 还是玩法级操作
//            Boolean isMatch = matchPreStatus == null ? Boolean.FALSE : Boolean.TRUE;
//            ConfigCashOutTradeItem item = null;
//            if (isMatch) {
//                log.info("{}::赛事级玩法提前结算开关变更,准备下发数据", cashOutTradeItemDTO.getMatchId());
//                //赛事级操作
//                cashOutTradeItemDTO.setLeve(1);
//                item = configCashOutTradeItemService.getItem(cashOutTradeItemDTO.getMatchId(), cashOutTradeItemDTO.getMarketType(), 1);
//                //cashOutTradeItemDTO对比item,下发旧的赛事级提前结算
//                thirdMarketPreResultProcessor.changeAndSendConfigCashOutTradeItem(linkId,standardMatchInfo,cashOutTradeItemDTO,item);
//            }else {
//                //玩法级操作
//                cashOutTradeItemDTO.setLeve(2);
//                if(null == cashOutTradeItemDTO.getMarketCategoryId()){
//                    return Response.failed("提前结算玩法不能为空");
//                }
//                item = configCashOutTradeItemService.getItem(cashOutTradeItemDTO.getMatchId(), cashOutTradeItemDTO.getMarketType(), cashOutTradeItemDTO.getMarketCategoryId());
//            }
//            if (item == null) {
//                configCashOutTradeItemService.create(cashOutTradeItemDTO);
//            } else {
//                configCashOutTradeItemService.update(item, cashOutTradeItemDTO);
//            }
//            if(isMatch){
//                thirdMarketPreResultProcessor.sendThirdPreMarket(linkId, standardMatchInfo, null, request.getDataSourceTime());
//            }
//        }
        sw.stop();
        log.info("::{}::盘口位置、最大最小值、margin、水差入库处理总耗时{}ms," + sw.prettyPrint(), linkId, sw.getTotalTimeMillis());
        if (marketPlaceConfigDTO.getStandardCategoryId() == null) {
            log.info("::{}::玩法为空不下发，标准赛事id={},玩法id={}", linkId, standardMatchInfoId,
                    marketPlaceConfigDTO.getStandardCategoryId());
            return Response.success();
        }
        ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(linkId,
                standardMatchInfo.getId(), marketPlaceConfigDTO.getStandardCategoryId());
        Integer tradeType = 0;
        if (null != configTradeType) {
            tradeType = configTradeType.getTradeType();
        }
        //下发水差/margin配置给AO
        List<TradeMarketDiffAndMarginConfigDTO> sendAo = aoMarketDiffAndMarginConfig(linkId, standardMatchInfo, null, marketPlaceConfigDTO, null, null);
        if (!CollectionUtils.isEmpty(sendAo)) {
            aoMatchDiffAndMarginProducer.sendConfig(linkId, sendAo);
        }
        if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.MANUAL.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_MODEL.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_NEW.equals(tradeType)) {
            log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", linkId, standardMatchInfoId,
                    marketPlaceConfigDTO.getStandardCategoryId());
            return Response.success();
        }
        processUiInterfaceThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //下发当前最新赔率
                    Set<Long> marketCategoryIdSet = new HashSet<>();
                    marketCategoryIdSet.add(marketPlaceConfigDTO.getStandardCategoryId());
                    //获取缓存中的所有盘口
                    Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                            getStringStandardMarketDataMessageMap(marketCategoryIdSet, linkId, standardMatchInfo, standardSportMarketSell);
                    //--------操盘后台操作开关封锁,异步处理-----------
                    thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, marketCategoryIdSet,
                            stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
                } catch (Exception e) {
                    log.error("{}::processUiInterfaceThreadPool ERROR:", request.getLinkId(), e);
                }
            }
        });
        log.info("::{}::putTradeMarketUiConfig入参返回", linkId);
        return Response.success();
    }

    @Override
    public ConfigMarketDisplayTrade getConfigMarketDisplayTrade(@NotEmpty Long matchId) {
        ConfigMarketDisplayTrade configMarketDisplayTrade = configMarketDisplayTradeService.getItem(matchId);
        if (configMarketDisplayTrade == null) {
            configMarketDisplayTrade = new ConfigMarketDisplayTrade();
            configMarketDisplayTrade.setLiveMarketCount(3);
            configMarketDisplayTrade.setDisplayMarketCount(3);
            configMarketDisplayTrade.setDisplayPenaltyCard("Y");
            configMarketDisplayTrade.setDisplayCorner("Y");
        }
        return configMarketDisplayTrade;
    }

    @Override
    public Response putTradeMarketAutoDiffConfig(Request<TradeMarketAutoDiffConfigDTO> request) {
        //validateLinkId("putTradeMarketAutoDiffConfig", request);
        log.info("::{}:: putTradeMarketAutoDiffConfig入参, req={}", request.getLinkId(), JSON.toJSONString(request));
        TradeMarketAutoDiffConfigDTO tradeMarketAutoDiffConfigDTO = request.getData();
        Long operaterId = request.getOperaterId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(tradeMarketAutoDiffConfigDTO.getMatchId());
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeMarketAutoDiffConfig,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    tradeMarketAutoDiffConfigDTO.getMatchId());
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeMarketAutoDiffConfig ,赛事未开售，标准赛事id：{}", request.getLinkId(),
                    standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        //存储当前数据里面的玩法
        Set<Long> marketCategoryIdSet = new HashSet();
        //处理水差配置
        List<TradeMarketAutoDiffConfigItemDTO> diffConfigList = tradeMarketAutoDiffConfigDTO.getDiffConfigs();
        if (!CollectionUtils.isEmpty(diffConfigList)) {
            diffConfigList.forEach(diffConfig -> {
                //收集日志
                configMarketAutoDiffTradeLogService.create(request.getLinkId(), diffConfig,
                        tradeMarketAutoDiffConfigDTO.getMatchId(), operaterId);
                ConfigMarketAutoDiffTrade item = configMarketAutoDiffTradeService.getItem(request.getLinkId(), standardMatchInfo.getId(),
                        diffConfig.getMarketId(), diffConfig.getOddType());
                if (item == null) {
                    configMarketAutoDiffTradeService.create(request.getLinkId(), diffConfig,
                            tradeMarketAutoDiffConfigDTO.getMatchId(), operaterId);
                } else {
                    item.setDiffValue(diffConfig.getDiffValue());
                    item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    item.setLinkId(request.getLinkId());
                    item.setOperaterId(operaterId);
                    configMarketAutoDiffTradeService.updata(item);
                }
                marketCategoryIdSet.add(diffConfig.getMarketCategoryId());
            });
        } else {
            return Response.failed("水差配置不能为空");
        }
        //获取缓存中的所有盘口
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(marketCategoryIdSet, request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
        //下发盘口水差配置给AO
        List<TradeMarketDiffAndMarginConfigDTO> sendAo = aoMarketDiffAndMarginConfig(request.getLinkId(), standardMatchInfo, stringStandardMarketDataMessageMap, null, diffConfigList, null);
        if (!CollectionUtils.isEmpty(sendAo)) {
            aoMatchDiffAndMarginProducer.sendConfig(request.getLinkId(), sendAo);
        }
        //下发当前最新赔率
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, marketCategoryIdSet,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Response putTradePlaceNumAutoDiffConfig(Request<TradePlaceNumAutoDiffConfigDTO> request) {
        //validateLinkId("putTradePlaceNumAutoDiffConfig", request);
        log.info("::{}:: putTradePlaceNumAutoDiffConfig入参, req={}", request.getLinkId(), JSON.toJSONString(request));
        TradePlaceNumAutoDiffConfigDTO tradePlaceNumAutoDiffConfigDTO = request.getData();
        Long operaterId = request.getOperaterId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(tradePlaceNumAutoDiffConfigDTO.getMatchId());
        if (standardMatchInfo == null) {
            log.info("::{}::putTradePlaceNumAutoDiffConfig,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    tradePlaceNumAutoDiffConfigDTO.getMatchId());
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradePlaceNumAutoDiffConfig ,赛事未开售，标准赛事id：{}", request.getLinkId(),
                    standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        //存储当前数据里面的玩法
        Set<Long> marketCategoryIdSet = new HashSet();
        //处理水差配置
        TradePlaceNumAutoDiffConfigItemDTO diffConfig = tradePlaceNumAutoDiffConfigDTO.getDiffConfigs();

        if (ObjectUtil.isNotEmpty(diffConfig)) {
            if (ObjectUtils.isEmpty(diffConfig.getChildStandardCategoryId())) {
                diffConfig.setChildStandardCategoryId(diffConfig.getMarketCategoryId());
            }
            ConfigPlacenumAutoDiffTrade item = configPlaceNumAutoDiffTradeService.getItem(request.getLinkId(),
                    tradePlaceNumAutoDiffConfigDTO.getMatchId(), diffConfig.getMarketCategoryId(), diffConfig.getChildStandardCategoryId(),
                    diffConfig.getPlaceNum());
            if (item == null) {
                configPlaceNumAutoDiffTradeService.create(request.getLinkId(), diffConfig,
                        tradePlaceNumAutoDiffConfigDTO.getMatchId(), operaterId);
            } else {
                item.setDiffValue(diffConfig.getDiffValue());
                item.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                item.setLinkId(request.getLinkId());
                item.setOperaterId(operaterId);
                configPlaceNumAutoDiffTradeService.updata(item);
            }
            marketCategoryIdSet.add(diffConfig.getMarketCategoryId());
            //如果玩法水差为空，则设置玩法水差的主盘下盘投注项
            ConfigCategoryAutoDiffTrade categoryAutoDiffTrade =
                    configCategoryAutoDiffTradeService.getItem(request.getLinkId(),
                            tradePlaceNumAutoDiffConfigDTO.getMatchId(), diffConfig.getMarketCategoryId(), diffConfig.getChildStandardCategoryId());
            if (null == categoryAutoDiffTrade) {
                TradeCategoryAutoDiffConfigItemDTO tradeCategoryAutoDiffConfigItemDTO =
                        new TradeCategoryAutoDiffConfigItemDTO();
                tradeCategoryAutoDiffConfigItemDTO.setMarketCategoryId(diffConfig.getMarketCategoryId());
                tradeCategoryAutoDiffConfigItemDTO.setDiffValue(0.0);
                tradeCategoryAutoDiffConfigItemDTO.setOddType(diffConfig.getOddType());
                configCategoryAutoDiffTradeService.create(request.getLinkId(), tradeCategoryAutoDiffConfigItemDTO,
                        tradePlaceNumAutoDiffConfigDTO.getMatchId(), operaterId);
            }
        }
        //获取缓存中的所有盘口
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(marketCategoryIdSet, request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
        //下发当前最新赔率
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketCategoryIdSet,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Response putTradeMarketDisplayConfig(Request<TradeMarketDisplayConfigDTO> request) {
        //validateLinkId("putTradeMarketDisplayConfig", request);
        log.info("::{}::putTradeMarketDisplayConfig ={}", request.getLinkId(), JSON.toJSONString(request));
        TradeMarketDisplayConfigDTO displayConfigDTO = request.getData();
        ConfigMarketDisplayTrade configMarketDisplayTrade =
                configMarketDisplayTradeService.getItem(displayConfigDTO.getMatchId());

        if (configMarketDisplayTrade == null) {
            configMarketDisplayTrade = configMarketDisplayTradeService.create(displayConfigDTO);
        } else {
            if (displayConfigDTO.getPreMarketNum() != null) {
                configMarketDisplayTrade.setDisplayMarketCount(displayConfigDTO.getPreMarketNum());
            }
            if (displayConfigDTO.getLiveMarketNum() != null) {
                configMarketDisplayTrade.setLiveMarketCount(displayConfigDTO.getLiveMarketNum());
            }
            configMarketDisplayTrade.setDisplayCorner(!displayConfigDTO.isDisplayCorner() ? "N" : "Y");
            configMarketDisplayTrade.setDisplayPenaltyCard(!displayConfigDTO.isDisplayPenalty() ? "N" : "Y");
            configMarketDisplayTrade = configMarketDisplayTradeService.update(configMarketDisplayTrade);
        }
        configMarketDisplayProducer.sendConfigMarketDisplayToMQ(request.getLinkId(), configMarketDisplayTrade);
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateRiskManagerCode(Request<RiskManagerCodeDTO> request) {
        //validateLinkId("updateRiskManagerCode", request);
        //是否需要切换数据源标识，当对应滚球或赛前的数据商不为sr时，需要切换数据源
        boolean isChangeSoldMessage = false;
        log.info("::{}::操盘平台修改操盘平台：{}", request.getLinkId(), JSON.toJSONString(request));
        RiskManagerCodeDTO updateRiskManagerCodeDTO = request.getData();
        Long operaterId = request.getOperaterId();
        if (StringUtils.isBlank(updateRiskManagerCodeDTO.getRiskManagerCode())) {
            log.info("::{}:: updateRiskManagerCode 操盘平台不能为空 ", request.getLinkId());
            return Response.failed("操盘平台不能为空");
        }
        if (operaterId == null) {
            return Response.failed("当前操作用户id不能为空");
        }
        //查询标准赛事是否存在
        StandardMatchInfo oldStandardMatchInfo = standardMatchInfoService.getItem(updateRiskManagerCodeDTO.getMatchId());
        if (oldStandardMatchInfo == null) {
            log.info("::{}::updateRiskManagerCode,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    updateRiskManagerCodeDTO.getMatchId());
            return Response.failed("标准赛事未找到");
        }
        Integer marketType = updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? 1 : 0;
        //xts 切换标识
        redisService.set(RONGHE_XTS_MATCH_AUTO_SWITCH + updateRiskManagerCodeDTO.getMatchId() + "_" + marketType, updateRiskManagerCodeDTO.getXtsMatchAutoSwitch(), marketCacheTime(oldStandardMatchInfo.getBeginTime()));

        /**
         * 4405：玩法级操盘模式切换（复用 updateRiskManagerCode 入口）
         * 触发条件：categoryIds4405 非 null（语义：不切换玩法列表）
         * 行为：
         *  - 实际切换集合 = 全量开售玩法 - categoryIds4405（排除列表）
         *  - 覆盖写入 Ronghe:playRiskManager:{matchId}:{marketType}[categoryId] = riskManagerCode
         *  - 仅对被切玩法触发开售重建/下发（soldHandler）
         *  - 不改赛事级 sell.pre/liveRiskManagerCode
         */
        if (updateRiskManagerCodeDTO.getCategoryIds4405() != null &&StandardSportTypeEnum.FootBall.code.equals(oldStandardMatchInfo.getSportId()) ) {
            // 刷新开售缓存（用于兜底/重建下发）
            StandardSportMarketSell standardSportMarketSell =
                    standardSportMarketSellService.refreshCache(updateRiskManagerCodeDTO.getMatchId());
            if (standardSportMarketSell == null) {
                return Response.failed("赛事未开售");
            }
            // 玩法级操盘缓存TTL与盘口缓存TTL保持一致，保证同一场赛事生命周期内行为一致
            Long ttlSeconds = marketCacheTime(oldStandardMatchInfo.getBeginTime());
            // 构造 category->dataSourceCode（用于4405规则判定 + soldHandler重建下发）
            String mt = Objects.equals(marketType, 1) ? "1" : "0";
            // 全量开售玩法（当前赛事 + 盘型）
            List<MarketCategorySell> marketCategorySells = marketCategorySellService.getItem(updateRiskManagerCodeDTO.getMatchId(), mt);
            if (CollectionUtils.isEmpty(marketCategorySells)) {
                return Response.failed("该赛事没有玩法开售数据");
            }
            // 4405：玩法级写入需区分数据源对应关系
            // - 若目标操盘为 PA：仅“等于赛事数据源”的玩法写 PA；其他玩法按其数据源映射到 XTS 家族（未知回落 PA）
            // - 若目标操盘为 XTS 家族：仅“对应数据源”的玩法写该 XTS；其他数据源玩法纠偏写 PA
            // 本次切换目标操盘模式（例如 PA/MTS/GTS/CTS/BTS/F2TS）
            String targetRiskManagerCode = updateRiskManagerCodeDTO.getRiskManagerCode();
            // 目标操盘模式对应的数据源（例如 MTS->SR、GTS->BG）；PA 返回 null
            String expectedDataSource = playRiskManagerService.expectedDataSourceCodeForRiskManager(targetRiskManagerCode);
            // 是否目标为PA（PA分支与XTS分支规则不同）
            boolean isTargetPa = StringUtils.equalsIgnoreCase(targetRiskManagerCode, RiskManagerCodeEnums.PA.name());
            //kasa---------------------PA切XTS操盘 4405集合id 强制AO 其余全部切为目标数据源---------------
            if(!isTargetPa){
                marketCategorySells.forEach(obj->{
                    obj.setLinkId(request.getLinkId());
                    obj.setSellStatus(SaleMatchSellStausEnum.Sold.name());
                    if(updateRiskManagerCodeDTO.getCategoryIds4405().contains(obj.getMarketCategoryId())){
                        obj.setDataSourceCode(DataSourceCodeEnum.AO.getCode());
                    }else {
                        obj.setDataSourceCode(expectedDataSource);
                    }
                });
                //对应开售玩法数据源入库
                marketCategorySellService.batchUpdateById(standardSportMarketSell.getMatchInfoId(), marketType, marketCategorySells);
            }
            //没有else 是因为PA操盘的时候 貌似没有看到 4405ids  所以不会进这个4405 的if
            //kasa--------------------------------下面这段 我感觉都不需要--------------------------------------
            Set<Long> allCategories = marketCategorySells.stream()
                    .map(MarketCategorySell::getMarketCategoryId)
                    .collect(Collectors.toSet());
            // 4405新语义：categoryIds4405 为“不切换玩法列表”，实际切换集合=全量-排除
            Set<Long> excluded = new HashSet<>(updateRiskManagerCodeDTO.getCategoryIds4405());
            Set<Long> target = new HashSet<>(allCategories);
            target.removeAll(excluded);
            if (CollectionUtils.isEmpty(target)) {
                log.info("::{}::4405 no target categories after exclude, matchId={}, marketType={}, excluded={}",
                        request.getLinkId(), updateRiskManagerCodeDTO.getMatchId(), marketType, excluded);
                return Response.success();
            }
            Map<Long, String> categoryDataSourceMap = new HashMap<>();
            // 1) 优先使用入参携带的玩法数据源映射（如果齐全）
            if (updateRiskManagerCodeDTO.getCategoryDataSourceMap4405() != null && !updateRiskManagerCodeDTO.getCategoryDataSourceMap4405().isEmpty()) {
                categoryDataSourceMap.putAll(updateRiskManagerCodeDTO.getCategoryDataSourceMap4405());
            }
            // 仅保留本次实际要切换的玩法映射，避免排除列表玩法参与计算
            categoryDataSourceMap.entrySet().removeIf(e -> !target.contains(e.getKey()));
            // 判断入参映射是否覆盖了本次全部目标玩法
            boolean mappingComplete = categoryDataSourceMap.keySet().containsAll(target);
            // 2) 不齐全则回落查库（玩法开售表）
            if (!mappingComplete) {
                // 仅保留本次目标玩法，避免把整场玩法都带入本次切换计算
                categoryDataSourceMap = marketCategorySells.stream()
                        .filter(m -> target.contains(m.getMarketCategoryId()))
                        .collect(Collectors.toMap(MarketCategorySell::getMarketCategoryId, MarketCategorySell::getDataSourceCode, (v1, v2) -> v1));
                if (categoryDataSourceMap.size() != target.size()) {
                    Set<Long> missing = new HashSet<>(target);
                    missing.removeAll(categoryDataSourceMap.keySet());
                    return Response.failed("玩法开售数据缺失，缺失玩法=" + missing);
                }
            }
            // 统一规范数据源编码，避免 T01/TX 等编码差异导致“误判不匹配”
            for (Map.Entry<Long, String> e : new HashMap<>(categoryDataSourceMap).entrySet()) {
                categoryDataSourceMap.put(e.getKey(), playRiskManagerService.normalizeSellDataSourceCode(e.getValue()));
            }

            for(Long categoryId:updateRiskManagerCodeDTO.getCategoryIds4405()){
                categoryDataSourceMap.put(categoryId,DataSourceCodeEnum.AO.getCode());
            }
            //kasa-----------------------------------到这里------------------------------------------------
            try {
                if (isTargetPa) {
                    // PA场景：先取赛事级数据源（早盘/滚球），作为“PA对应数据源”基准
                    String matchDataSourceCode = Objects.equals(marketType, 1)
                            ? standardSportMarketSell.getPreMatchDataProviderCode()
                            : standardSportMarketSell.getLiveMatchDataProviderCode();
                    matchDataSourceCode = playRiskManagerService.normalizeSellDataSourceCode(matchDataSourceCode);
                    // paCategories: 与赛事数据源一致，写PA
                    Set<Long> paCategories = new HashSet<>();
                    // xTsByRiskCode: 非PA对应数据源，按玩法数据源映射XTS家族后分组写入
                    Map<String, Set<Long>> xTsByRiskCode = new HashMap<>();
                    // 遍历每个目标玩法，按“是否等于赛事数据源”分流
                    for (Long cid : target) {
                        // 玩法数据源（已normalize）
                        String ds = playRiskManagerService.normalizeSellDataSourceCode(categoryDataSourceMap.get(cid));
                        // 与赛事数据源一致：该玩法写PA
                        if (StringUtils.isNotBlank(matchDataSourceCode) && StringUtils.equalsIgnoreCase(matchDataSourceCode, ds)) {
                            paCategories.add(cid);
                            continue;
                        }
                        // 非PA对应数据源：按玩法数据源推断 MTS/GTS/CTS/BTS/F2TS
                        String inferred = playRiskManagerService.inferRiskManagerCodeByDataSource(ds);
                        if (StringUtils.isBlank(inferred)) {
                            // 未知数据源不阻断，回落PA
                            inferred = RiskManagerCodeEnums.PA.name();
                        }
                        xTsByRiskCode.computeIfAbsent(inferred, k -> new HashSet<>()).add(cid);
                    }
                    // 第一批写入：PA对应数据源玩法 -> PA
                    if (!CollectionUtils.isEmpty(paCategories)) {
                        playRiskManagerService.batchSet(
                                request.getLinkId(),
                                updateRiskManagerCodeDTO.getMatchId(),
                                marketType,
                                paCategories,
                                RiskManagerCodeEnums.PA.name(),
                                ttlSeconds
                        );
                    }
                    // 第二批写入：非PA对应数据源玩法 -> 其映射的XTS家族（分组批量写）
                    for (Map.Entry<String, Set<Long>> entry : xTsByRiskCode.entrySet()) {
                        // 空分组直接跳过
                        if (CollectionUtils.isEmpty(entry.getValue())) {
                            continue;
                        }
                        playRiskManagerService.batchSet(
                                request.getLinkId(),
                                updateRiskManagerCodeDTO.getMatchId(),
                                marketType,
                                entry.getValue(),
                                entry.getKey(),
                                ttlSeconds
                        );
                    }
                } else {
                    // XTS场景：
                    // xtsOk = 数据源匹配目标XTS的数据源映射，保持目标XTS
                    // mismatch = 非对应数据源，纠偏回PA
                    Set<Long> xtsOk = new HashSet<>();
                    Set<Long> mismatch = new HashSet<>();
                    // 目标XTS可识别：按“是否匹配目标XTS对应数据源”分组
                    if (StringUtils.isNotBlank(expectedDataSource)) {
                        for (Long cid : target) {
                            // 玩法数据源（已normalize）
                            String ds = playRiskManagerService.normalizeSellDataSourceCode(categoryDataSourceMap.get(cid));
                            // 匹配：保持目标XTS
                            if (StringUtils.equalsIgnoreCase(expectedDataSource, ds)) {
                                xtsOk.add(cid);
                            } else {
                                // 不匹配：纠偏回PA
                                mismatch.add(cid);
                            }
                        }
                    } else {
                        // 目标操盘未识别到对应数据源：兼容逻辑，全部按目标操盘写入
                        xtsOk.addAll(target);
                    }
                    // 先写匹配玩法（目标XTS）
                    if (!CollectionUtils.isEmpty(xtsOk)) {
                        playRiskManagerService.batchSet(
                                request.getLinkId(),
                                updateRiskManagerCodeDTO.getMatchId(),
                                marketType,
                                xtsOk,
                                targetRiskManagerCode,
                                ttlSeconds
                        );
                    }
                    // 再写不匹配玩法（回PA）
                    if (!CollectionUtils.isEmpty(mismatch)) {
                        playRiskManagerService.batchSet(
                                request.getLinkId(),
                                updateRiskManagerCodeDTO.getMatchId(),
                                marketType,
                                mismatch,
                                RiskManagerCodeEnums.PA.name(),
                                ttlSeconds
                        );
                        log.info("::{}::4405 playRiskManager mismatch datasource -> fallback PA, matchId={}, marketType={}, expectedDs={}, mismatchCategories={}",
                                request.getLinkId(), updateRiskManagerCodeDTO.getMatchId(), marketType, expectedDataSource, mismatch);
                    }
                }
            } catch (Throwable t) {
                log.error("::{}::4405 batchSet playRiskManager failed, matchId={}, marketType={}, categories={}, code={}",
                        request.getLinkId(),
                        updateRiskManagerCodeDTO.getMatchId(),
                        marketType,
                        target,
                        targetRiskManagerCode,
                        t);
                return Response.failed("玩法级操盘模式写入失败");
            }


            StopWatch stopWatch = new StopWatch("操盘平台修改MTS/GTS操盘耗时：" + UUIdUtils.getId());
            //记录切换操作日志
            String oldRiskManagerCode=Objects.equals(marketType,1)?standardSportMarketSell.getPreRiskManagerCode():standardSportMarketSell.getLiveRiskManagerCode();
            addCountLog(stopWatch,operaterId,updateRiskManagerCodeDTO,oldRiskManagerCode,targetRiskManagerCode,request.getLinkId(),standardSportMarketSell,marketType);

            // 4405补充：同步更新赛事级riskManagerCode（给只看赛事级字段的下游/风控口径）
            // - PRE 更新 preRiskManagerCode
            // - LIVE 更新 liveRiskManagerCode
            // 注意：该同步不改变玩法级分流结果，玩法级仍以 playRiskManager 为准
            stopWatch.start("更新赛事开售表事件源/状态源");
            log.info("::{}::更新开售表数据源/事件源/状态源，标准赛事id={}, 盘口类型={},当前:{},{},{}--目标:{},{}",request.getLinkId(),standardSportMarketSell.getMatchInfoId(),marketType,standardSportMarketSell.getPreRiskManagerCode(),standardSportMarketSell.getBusinessEvent(),standardSportMarketSell.getMatchStatusSourceCode(),targetRiskManagerCode,expectedDataSource);
            if (Objects.equals(marketType, 1)) {
                standardSportMarketSell.setPreRiskManagerCode(targetRiskManagerCode);
                standardSportMarketSell.setPreMatchDataProviderCode(expectedDataSource);
                if (!standardSportMarketSell.getLiveTraderStatus().equals(PreTraderStatusEnum.Setted.name())) {
                    String businessEventSource = resolveBusinessEventSource(expectedDataSource,
                            standardSportMarketSell.getBusinessEvent(), request.getLinkId());
                    if (businessEventSource != null) {
                        standardSportMarketSell.setBusinessEvent(businessEventSource);
                    }
                    standardSportMarketSell.setMatchStatusSourceCode(expectedDataSource);
                }
            } else {
                standardSportMarketSell.setLiveRiskManagerCode(targetRiskManagerCode);
                standardSportMarketSell.setLiveMatchDataProviderCode(expectedDataSource);
                String businessEventSource = resolveBusinessEventSource(expectedDataSource,
                        standardSportMarketSell.getBusinessEvent(), request.getLinkId());
                if (businessEventSource != null) {
                    List<ThirdMatchInfo> thirdMatchInfolist = thirdMatchInfoService.getItems(oldStandardMatchInfo.getId());
                    thirdMatchInfolist
                            .stream()
                            .filter(thirdMatchInfo -> thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(businessEventSource))
                            .findFirst()
                            .ifPresent(thirdMatchInfo -> {
                                String matchPeriod = thirdMatchInfo.getMatchPeriod();
                                if (StringUtils.isNumeric(matchPeriod) && Integer.parseInt(matchPeriod) != 0) {
                                    standardSportMarketSell.setBusinessEvent(businessEventSource);
                                }
                            });
                }
                standardSportMarketSell.setMatchStatusSourceCode(expectedDataSource);
            }
            standardSportMarketSellService.update(standardSportMarketSell);
            StandardSportMarketSell sportMarketSell = standardSportMarketSellService.refreshCache(oldStandardMatchInfo.getId());
            String delKey = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + oldStandardMatchInfo.getId();
            redisService.set(delKey, sportMarketSell);
            log.info("::{}::4405 sync match riskManagerCode, matchId={}, marketType={}, targetCode={}, categoryDataSourceMapSize={} ,refreshedSell={}",
                    request.getLinkId(), oldStandardMatchInfo.getId(), marketType, targetRiskManagerCode, categoryDataSourceMap.size(), JSONObject.toJSONString(sportMarketSell));
            stopWatch.stop();
            //玩法数据源下发风控 -kasa
            Map<String, List<Long>> playDataSources =
                    marketCategorySells.stream().collect(Collectors.groupingBy(MarketCategorySell::getDataSourceCode, Collectors.mapping(MarketCategorySell::getMarketCategoryId, Collectors.toList())));
            marketCategorySellProducer.sendStandardMarketCategorySell(standardSportMarketSell.getMatchInfoId(), updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? 1 :
                    0, playDataSources);
            // 只重建被切玩法。isRefresh=true 代表“切换重建链路”，会走老缓存比对/关盘等逻辑
            //处理玩法过多时  开售流程时长太长问题   分组开售  大小可调配


            List<Map<Long,String>> categoryDataSourceMapList=AutoMapSplitterUtils.autoSplitMap(categoryDataSourceMap,20,20);
            if(!CollectionUtils.isEmpty(categoryDataSourceMapList)){
                int i=1;
                for(Map<Long,String> categoryDataSourceMaps:categoryDataSourceMapList){
                    soldMessageToOddsProcessor.soldHandler(request.getLinkId()+"_"+i, oldStandardMatchInfo, standardSportMarketSell, categoryDataSourceMaps, marketType, true, false);
                    i++;
                }
            }

            // 异步下发变更信息（用于审计/通知）
            Long standardMatchId = oldStandardMatchInfo.getId();
            processTradeSystemThreadPool.execute(() -> {
                try {
                    modifyMatchInfoProducer.pushModifyMatchInfoMessage(request.getLinkId(), standardMatchId, "操盘方式变更(玩法级)", operaterId);
                } catch (Exception e) {
                    log.error("{}::pushModifyMatchInfoMessage(4405) ERROR:", request.getLinkId(), e);
                }
            });

            return Response.success();
        }

        if (RiskManagerCodeEnums.PA.name().equalsIgnoreCase(updateRiskManagerCodeDTO.getRiskManagerCode())) {
            log.info("::{}::操盘平台修改PA操盘：{}", request.getLinkId(), JSON.toJSONString(request));
            return updateRiskManagerCode2PA(request, updateRiskManagerCodeDTO);
        }
        AtomicBoolean needChangeBusiness = new AtomicBoolean(false);
        boolean isGTS = RiskManagerCodeEnums.GTS.name().equalsIgnoreCase(updateRiskManagerCodeDTO.getRiskManagerCode());
        boolean isMTS = RiskManagerCodeEnums.MTS.name().equalsIgnoreCase(updateRiskManagerCodeDTO.getRiskManagerCode());
        boolean isCTS = RiskManagerCodeEnums.CTS.name().equalsIgnoreCase(updateRiskManagerCodeDTO.getRiskManagerCode());
        boolean isBTS = RiskManagerCodeEnums.BTS.name().equalsIgnoreCase(updateRiskManagerCodeDTO.getRiskManagerCode());
        boolean isF2TS = RiskManagerCodeEnums.F2TS.name().equalsIgnoreCase(updateRiskManagerCodeDTO.getRiskManagerCode());
        log.info("::{}::操盘平台修改MTS/GTS/CTS操盘：{}", request.getLinkId(), JSON.toJSONString(request));

        StopWatch stopWatch = new StopWatch("操盘平台修改MTS/GTS操盘耗时：" + UUIdUtils.getId());
        stopWatch.start("查询三方赛事");
        //查询该赛事下所有关联的三方赛事信息
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(oldStandardMatchInfo.getId());
        if (isMTS && (thirdMatchInfos.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.SR.code)).collect(Collectors.toList()).size() != 1)) {
            stopWatch.stop();
            log.info("::{}::updateRiskManagerCode,第三方sr赛事未找到，标准赛事id:{}", request.getLinkId(),
                    updateRiskManagerCodeDTO.getMatchId());
            return Response.failed("MTS操盘只支持SR数据源");
        }
        if (isGTS && (thirdMatchInfos.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.BG.code)).collect(Collectors.toList()).size() != 1)) {
            stopWatch.stop();
            log.info("::{}::updateRiskManagerCode,第三方BG赛事未找到，标准赛事id:{}", request.getLinkId(),
                    updateRiskManagerCodeDTO.getMatchId());
            return Response.failed("GTS操盘只支持BG数据源");
        }
        if (isCTS && (thirdMatchInfos.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.BC.code)).collect(Collectors.toList()).size() != 1)) {
            stopWatch.stop();
            log.info("::{}::updateRiskManagerCode,第三方BC赛事未找到，标准赛事id:{}", request.getLinkId(),
                    updateRiskManagerCodeDTO.getMatchId());
            return Response.failed("CTS操盘只支持BG数据源");
        }
        if (isBTS && (thirdMatchInfos.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.BE.code)).collect(Collectors.toList()).size() != 1)) {
            stopWatch.stop();
            log.info("::{}::updateRiskManagerCode,第三方BE赛事未找到，标准赛事id:{}", request.getLinkId(),
                    updateRiskManagerCodeDTO.getMatchId());
            return Response.failed("BTS操盘只支持BE数据源");
        }
        if (isF2TS && (thirdMatchInfos.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.F01.code)).collect(Collectors.toList()).size() != 1)) {
            stopWatch.stop();
            log.info("::{}::updateRiskManagerCode,第三方F01赛事未找到，标准赛事id:{}", request.getLinkId(),
                    updateRiskManagerCodeDTO.getMatchId());
            return Response.failed("F2TS操盘只支持F01数据源");
        }
        stopWatch.stop();

        //刷新开售缓存并返回最新开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.refreshCache(oldStandardMatchInfo.getId());
        //赛事未开售
        if (standardSportMarketSell == null) {
            log.info("::{}::updateRiskManagerCode ,赛事未开售，标准赛事id：{}", request.getLinkId(), oldStandardMatchInfo.getId());
            return Response.failed("赛事未开售");
        }
        String oldDataSourceCode = null;
        if (isMTS) {
            if ((updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) && !standardSportMarketSell.getPreMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.SR.code))
                    || (updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.LIVE.name()) && !standardSportMarketSell.getLiveMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.SR.code))) {
                oldDataSourceCode = updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ?
                        standardSportMarketSell.getPreMatchDataProviderCode() :
                        standardSportMarketSell.getLiveMatchDataProviderCode();
                isChangeSoldMessage = true;
            }
        }
        if (isGTS) {
            if ((updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) && !standardSportMarketSell.getPreMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.BG.code))
                    || (updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.LIVE.name()) && !standardSportMarketSell.getLiveMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.BG.code))) {
                oldDataSourceCode = updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ?
                        standardSportMarketSell.getPreMatchDataProviderCode() :
                        standardSportMarketSell.getLiveMatchDataProviderCode();
                isChangeSoldMessage = true;
            }
        }
        if (isCTS) {
            if ((updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) && !standardSportMarketSell.getPreMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.BC.code))
                    || (updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.LIVE.name()) && !standardSportMarketSell.getLiveMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.BC.code))) {
                oldDataSourceCode = updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ?
                        standardSportMarketSell.getPreMatchDataProviderCode() :
                        standardSportMarketSell.getLiveMatchDataProviderCode();
                isChangeSoldMessage = true;
            }
        }
        if (isF2TS) {
            if ((updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) && !standardSportMarketSell.getPreMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.F01.code))
                    || (updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.LIVE.name()) && !standardSportMarketSell.getLiveMatchDataProviderCode().equalsIgnoreCase(DataSourceCodeEnum.F01.code))) {
                oldDataSourceCode = updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ?
                        standardSportMarketSell.getPreMatchDataProviderCode() :
                        standardSportMarketSell.getLiveMatchDataProviderCode();
                isChangeSoldMessage = true;
            }
        }
        //查询赛事权重表
        MatchDataSourceWeight matchDataSourceWeight =
                matchDataSourceWeightService.getItem(updateRiskManagerCodeDTO.getMatchId(),
                        updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? 1 : 0);
        if (matchDataSourceWeight == null) {
            log.info("::{}::updateRiskManagerCode ,赛事未设置权重表，标准赛事id：{}", request.getLinkId(),
                    oldStandardMatchInfo.getId());
            return Response.failed("赛事未设置权重表");
        }

        //查询赛事玩法表
        stopWatch.start("查询赛事玩法表");
        List<MarketCategorySell> marketCategorySells =
                marketCategorySellService.getItem(updateRiskManagerCodeDTO.getMatchId(),
                        updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? "1" : "0");
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            stopWatch.stop();
            log.info("::{}::updateRiskManagerCode ,赛事未玩法开售数据，标准赛事id：{}", request.getLinkId(),
                    oldStandardMatchInfo.getId());
            return Response.failed("该赛事没有设置玩法开售数据");
        }
        stopWatch.stop();
        String tempDataSourceCode = isMTS ? DataSourceCodeEnum.SR.code : isGTS ? DataSourceCodeEnum.BG.code : isCTS ? DataSourceCodeEnum.BC.code : DataSourceCodeEnum.F01.code;
        stopWatch.start("修改赛事开售表");
        String oldRiskManageCode = null;//updateRiskManagerCodeDTO.getRiskManagerCode().equals(RiskManagerCodeEnums.MTS.name()) ? RiskManagerCodeEnums.PA.name() : RiskManagerCodeEnums.MTS.name();
        Boolean isSell = Boolean.TRUE;
        if (updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name())) {
            if (!standardSportMarketSell.getPreMatchSellStatus().equals(SaleMatchSellStausEnum.Sold.name())) {
                isSell = Boolean.FALSE;
            }
            oldRiskManageCode = standardSportMarketSell.getPreRiskManagerCode();
            standardSportMarketSell.setPreRiskManagerCode(updateRiskManagerCodeDTO.getRiskManagerCode());
            standardSportMarketSell.setPreMatchDataProviderCode(tempDataSourceCode);
            if (!standardSportMarketSell.getLiveTraderStatus().equals(PreTraderStatusEnum.Setted.name())) {
                String businessEventSource = resolveBusinessEventSource(tempDataSourceCode,
                        standardSportMarketSell.getBusinessEvent(), request.getLinkId());
                if (businessEventSource != null) {
                    standardSportMarketSell.setBusinessEvent(businessEventSource);
                    needChangeBusiness.set(true);
                }
                standardSportMarketSell.setMatchStatusSourceCode(tempDataSourceCode);
            }
        } else {
            if (!standardSportMarketSell.getLiveMatchSellStatus().equals(SaleMatchSellStausEnum.Sold.name())) {
                isSell = Boolean.FALSE;
            }
            oldRiskManageCode = standardSportMarketSell.getLiveRiskManagerCode();
            standardSportMarketSell.setLiveRiskManagerCode(updateRiskManagerCodeDTO.getRiskManagerCode());
            standardSportMarketSell.setLiveMatchDataProviderCode(tempDataSourceCode);
            String businessEventSource = resolveBusinessEventSource(tempDataSourceCode,
                    standardSportMarketSell.getBusinessEvent(), request.getLinkId());
            if (businessEventSource != null) {
                thirdMatchInfos
                        .stream()
                        .filter(thirdMatchInfo -> thirdMatchInfo.getDataSourceCode().equalsIgnoreCase(businessEventSource))
                        .findFirst()
                        .ifPresent(thirdMatchInfo -> {

                            String matchPeriod = thirdMatchInfo.getMatchPeriod();
                            if (StringUtils.isNumeric(matchPeriod) && Integer.parseInt(matchPeriod) != 0) {
                                standardSportMarketSell.setBusinessEvent(businessEventSource);
                                needChangeBusiness.set(true);
                            }
                        });
            }
            standardSportMarketSell.setMatchStatusSourceCode(tempDataSourceCode);
        }
        standardSportMarketSellService.update(standardSportMarketSell);
        stopWatch.stop();

        StandardSportMarketSell sportMarketSell = standardSportMarketSellService.refreshCache(oldStandardMatchInfo.getId());
        String delKey = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + oldStandardMatchInfo.getId();
        redisService.set(delKey, sportMarketSell);
        log.info("::{}::重新清除开售表缓存,刷新后开售信息:{},修改开售信息:{}",
                request.getLinkId(), JSONObject.toJSONString(sportMarketSell), JSONObject.toJSONString(standardSportMarketSell));

        String newRiskManageCode = updateRiskManagerCodeDTO.getRiskManagerCode();
        //记录切换操作日志
        addCountLog(stopWatch,operaterId,updateRiskManagerCodeDTO,oldRiskManageCode,newRiskManageCode,request.getLinkId(),standardSportMarketSell,marketType);
        //通过用户id查询用户信息

        if (!isSell) {
            return Response.success();
        }
        //用于本次修改的标准赛事
        stopWatch.start("修改标准赛事");
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        upStandardMatchInfo.setId(oldStandardMatchInfo.getId());
        //修改标准赛事表
        //如果有任何一个开售则 手动 开盘 为 1
        upStandardMatchInfo.setPreMatchBusiness(standardSportMarketSell.getPreMatchSellStatus().equals(SaleMatchSellStausEnum.Sold.name()) ? 1 : 0);
        upStandardMatchInfo.setLiveOddBusiness(standardSportMarketSell.getLiveMatchSellStatus().equals(SaleMatchSellStausEnum.Sold.name()) ? 1 : 0);
        upStandardMatchInfo.setMatchDataProviderCode(standardSportMarketSell.getLiveMatchDataProviderCode());
        upStandardMatchInfo.setRiskManagerCode(standardSportMarketSell.getLiveRiskManagerCode());
        upStandardMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //修改标准赛事
        oldStandardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
        stopWatch.stop();

        //修改权重表
        stopWatch.start("修改权重表");
        if (isMTS) {
            matchDataSourceWeight.setSrWeight(1);
            matchDataSourceWeight.setBgWeight(0);
            matchDataSourceWeight.setBcWeight(0);
            matchDataSourceWeight.setF01Weight(0);
        } else if (isGTS) {
            matchDataSourceWeight.setSrWeight(0);
            matchDataSourceWeight.setBgWeight(1);
            matchDataSourceWeight.setBcWeight(0);
            matchDataSourceWeight.setF01Weight(0);
        } else if (isF2TS) {
            matchDataSourceWeight.setSrWeight(0);
            matchDataSourceWeight.setBgWeight(0);
            matchDataSourceWeight.setBcWeight(0);
            matchDataSourceWeight.setF01Weight(1);
        } else {
            matchDataSourceWeight.setSrWeight(0);
            matchDataSourceWeight.setBgWeight(0);
            matchDataSourceWeight.setBcWeight(1);
            matchDataSourceWeight.setF01Weight(0);
        }
        matchDataSourceWeight.setAoWeight(0);
        matchDataSourceWeight.setBeWeight(0);
        matchDataSourceWeight.setBtWeight(0);
        matchDataSourceWeight.setKoWeight(0);
        matchDataSourceWeight.setLsWeight(0);
        matchDataSourceWeight.setTxWeight(0);
        matchDataSourceWeight.setOdWeight(0);
        matchDataSourceWeight.setPiWeight(0);
        matchDataSourceWeight.setRbWeight(0);

        matchDataSourceWeight.setOperaterId(operaterId);
        matchDataSourceWeightService.update(matchDataSourceWeight);
        stopWatch.stop();

        //修改玩法开售信息
        stopWatch.start("修改玩法开售信息");
        Set<Long> marketCategoryIdSet = new HashSet();
        Map<Long, String> marketCategoryIdMap = new HashMap();
        marketCategorySells.forEach(marketCategorySell -> {
            // 4405：玩法开售表按玩法自身 dataSourceCode 设置权重（用于后续按数据源对应关系判定 XTS/PA）
            // 仅当玩法 dataSourceCode 为空时，才用本次切换的 tempDataSourceCode 兜底。
            String sellDs = tempDataSourceCode;
/*            String sellDs = marketCategorySell.getDataSourceCode();
            if (StringUtils.isBlank(sellDs)) {
                sellDs = tempDataSourceCode;
            }*/
            sellDs = playRiskManagerService.normalizeSellDataSourceCode(sellDs);
            marketCategorySell.setDataSourceCode(sellDs);

            // reset all weights
            marketCategorySell.setSrWeight(0);
            marketCategorySell.setBgWeight(0);
            marketCategorySell.setBcWeight(0);
            marketCategorySell.setF01Weight(0);
            marketCategorySell.setAoWeight(0);
            marketCategorySell.setBeWeight(0);
            marketCategorySell.setBtWeight(0);
            marketCategorySell.setKoWeight(0);
            marketCategorySell.setLsWeight(0);
            marketCategorySell.setTxWeight(0);
            marketCategorySell.setOdWeight(0);
            marketCategorySell.setPiWeight(0);
            marketCategorySell.setRbWeight(0);

            if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.SR.code)) {
                marketCategorySell.setSrWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.BG.code)) {
                marketCategorySell.setBgWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.BC.code)) {
                marketCategorySell.setBcWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.F01.code)) {
                marketCategorySell.setF01Weight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.AO.code)) {
                marketCategorySell.setAoWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.BE.code)) {
                marketCategorySell.setBeWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.BT.code)) {
                marketCategorySell.setBtWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.KO.code)) {
                marketCategorySell.setKoWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.LS.code)) {
                marketCategorySell.setLsWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.TX.code)) {
                marketCategorySell.setTxWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.OD.code)) {
                marketCategorySell.setOdWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.PI.code)) {
                marketCategorySell.setPiWeight(1);
            } else if (StringUtils.equalsIgnoreCase(sellDs, DataSourceCodeEnum.RB.code)) {
                marketCategorySell.setRbWeight(1);
            }
            marketCategorySell.setSellStatus(SaleMatchSellStausEnum.Sold.name());
            marketCategorySell.setLinkId(request.getLinkId());
            marketCategoryIdSet.add(marketCategorySell.getMarketCategoryId());
            marketCategoryIdMap.put(marketCategorySell.getMarketCategoryId(), marketCategorySell.getDataSourceCode());
        });
        Map<String, List<Long>> playDataSource =
                marketCategorySells.stream().collect(Collectors.groupingBy(MarketCategorySell::getDataSourceCode, Collectors.mapping(MarketCategorySell::getMarketCategoryId, Collectors.toList())));
        marketCategorySellService.batchUpdateById(standardSportMarketSell.getMatchInfoId(), marketType, marketCategorySells);
        stopWatch.stop();

        //1.mts切pa，2.切滚球，3.切换清水差
        stopWatch.start("清水差");
        delDiffByMatchIdAndCategoryList(request.getLinkId(), updateRiskManagerCodeDTO.getMatchId(), new ArrayList<>(marketCategoryIdSet), oldStandardMatchInfo.getSportId().intValue());
        stopWatch.stop();

        stopWatch.start("玩法开售流程");
        if (isChangeSoldMessage) {
            Request<ChangeSoldMessage> changeSoleMessageRequest = new Request<ChangeSoldMessage>();
            ChangeSoldMessage changeSoldMessage = new ChangeSoldMessage();
            changeSoldMessage.setOldDataSource(oldDataSourceCode);
            changeSoldMessage.setDataSource(tempDataSourceCode);
            String finalOldDataSourceCode = oldDataSourceCode;
            changeSoldMessage.setOldThirdMatchId(thirdMatchInfos.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(finalOldDataSourceCode)).map(e -> e.getThirdMatchSourceId()).collect(Collectors.toList()).get(0));
            changeSoldMessage.setNewThirdMatchId(thirdMatchInfos.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(tempDataSourceCode)).map(e -> e.getThirdMatchSourceId()).collect(Collectors.toList()).get(0));
            changeSoldMessage.setMatchId(oldStandardMatchInfo.getId());
            changeSoldMessage.setIsOutRight("0");
            changeSoldMessage.setMarketType(updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? 1 :
                    0);
            changeSoldMessage.setMarketCategoryIds(new ArrayList<>(marketCategoryIdSet));
            changeSoleMessageRequest.setData(changeSoldMessage);
            changeSoleMessageRequest.setLinkId(request.getLinkId() + "_change");
            changeSoldMessageProcessor.changeSoldMessage(changeSoleMessageRequest);
        } else {
            List<Map<Long,String>> categoryDataSourceMapList=AutoMapSplitterUtils.autoSplitMap(marketCategoryIdMap,20,20);
            if(!CollectionUtils.isEmpty(categoryDataSourceMapList)){
                int i=1;
                for(Map<Long,String> marketCategoryIdMaps:categoryDataSourceMapList){
                    soldMessageToOddsProcessor.soldHandler(request.getLinkId()+"_"+i, oldStandardMatchInfo, standardSportMarketSell, marketCategoryIdMaps, marketType, true, false);
                    i++;
                }
            }
        }
        stopWatch.stop();

        log.info("::{}::操盘平台修改MTS操盘耗时{}ms," + stopWatch.prettyPrint(), request.getLinkId(), stopWatch.getTotalTimeMillis());
        //玩法数据源下发风控
        marketCategorySellProducer.sendStandardMarketCategorySell(standardSportMarketSell.getMatchInfoId(), updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? 1 :
                0, playDataSource);

        //下发赛事变更信息
        Long standardMatchId = oldStandardMatchInfo.getId();
        String standardMMatchId = oldStandardMatchInfo.getMatchManageId();
        processTradeSystemThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    modifyMatchInfoProducer.pushModifyMatchInfoMessage(request.getLinkId(), standardMatchId, "操盘方式变更", operaterId);
                    if (needChangeBusiness.get()){
                        try{
                            ChangeBusinessEventSaleDTO changeBusinessEventSaleDTO = new ChangeBusinessEventSaleDTO();
                            changeBusinessEventSaleDTO.setId(Long.valueOf(standardMMatchId));
                            changeBusinessEventSaleDTO.setDataType(standardSportMarketSell.getBusinessEvent());
                            changeBusinessEventSaleDTO.setSportId(standardSportMarketSell.getSportId());
                            changeBusinessEventSaleDTO.setUserId(request.getOperaterId()+"");
                            Request<ChangeBusinessEventSaleDTO> request1 = new Request();
                            request1.setData(changeBusinessEventSaleDTO);
                            request1.setLinkId(request.getLinkId()+"_changeBusinessEvent");
                            iMarketCategorySellApi.changeBusinessEvent(request1);
                        }catch (Exception e){

                        }
                    }
                } catch (Exception e) {
                    log.error("{}::pushModifyMatchInfoMessage ERROR:", request.getLinkId(), e);
                }
            }
        });


        return Response.success();
    }

    /**
     * N01/N02 不允许作为商业事件源；若目标为这两者则返回 null，调用方维持原 businessEvent 不变。
     */
    private String resolveBusinessEventSource(String targetDataSource, String currentBusinessEvent, String linkId) {
        if (StringUtils.isBlank(targetDataSource)) {
            return null;
        }
        if (DataSourceUtils.isN01OrN02DataSource(targetDataSource)) {
            log.info("::{}::目标事件源 {} 为 N01/N02，维持原事件源 {}", linkId, targetDataSource, currentBusinessEvent);
            return null;
        }
        return targetDataSource;
    }

    public void addCountLog(StopWatch stopWatch,Long operaterId,RiskManagerCodeDTO updateRiskManagerCodeDTO,String oldRiskManageCode, String newRiskManageCode,String linkId,StandardSportMarketSell standardSportMarketSell,Integer marketType){
        stopWatch.start("记录操作日志");
        String logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_RISK_MANAGE_CODE.getMessageZh()
                .replace("userId", String.valueOf(operaterId))
                .replace("userName", updateRiskManagerCodeDTO.getUserName())
                .replace("nowTime", DateUtil.now())
                .replace("oldRiskManageCode", oldRiskManageCode)
                .replace("newRiskManageCode", newRiskManageCode)
                .replace("linkId", linkId);
        String logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_RISK_MANAGE_CODE.getMessageEn()
                .replace("userId", String.valueOf(operaterId))
                .replace("userName", updateRiskManagerCodeDTO.getUserName())
                .replace("nowTime", DateUtil.now())
                .replace("oldRiskManageCode", oldRiskManageCode)
                .replace("newRiskManageCode", newRiskManageCode)
                .replace("linkId", linkId);
        // 记录操作日志
        standardSportMarketSellLogService.AssemblyAndInsertStandardSportMarketSellLog(standardSportMarketSell.getMatchInfoId(),
                standardSportMarketSell.getId(), logInfoZh, logInfoEn,
                marketType == 1 ? SaleOperateTypeEnum.pre_match.name() : SaleOperateTypeEnum.live_odd.name(),
                String.valueOf(operaterId), String.valueOf(operaterId));
        stopWatch.stop();
    }

    /**
     * MTS切换到PA操盘
     *
     * @param request
     * @return
     */
    private Response updateRiskManagerCode2PA(Request<RiskManagerCodeDTO> request, RiskManagerCodeDTO updateRiskManagerCodeDTO) {
        Long operaterId = request.getOperaterId();
        Integer marketType = updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? 1 : 0;
        //查询标准赛事是否存在
        StandardMatchInfo oldStandardMatchInfo =
                standardMatchInfoService.getItem(updateRiskManagerCodeDTO.getMatchId());
        if (oldStandardMatchInfo == null) {
            log.info("::{}::updateRiskManagerCode,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    updateRiskManagerCodeDTO.getMatchId());
            return Response.failed("标准赛事未找到");
        }
        StopWatch stopWatch = new StopWatch("操盘平台修改PA操盘耗时：" + UUIdUtils.getId());

        //刷新开售缓存并返回最新开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.refreshCache(oldStandardMatchInfo.getId());
        //赛事未开售
        if (standardSportMarketSell == null) {
            log.info("::{}::updateRiskManagerCode ,赛事未开售，标准赛事id：{}", request.getLinkId(), oldStandardMatchInfo.getId());
            return Response.failed("赛事未开售");
        }
        //跟风控沟通，当没有传玩法集合时，默认所有MTS玩法都开售
        //玩法集
        /*if (CollectionUtils.isEmpty(updateRiskManagerCodeDTO.getCategoryIds()))
        {
            log.info("::{}::updateRiskManagerCode ,玩法集合为空，切换到PA操盘失败", request.getLinkId());
            return Response.failed("赛事未开售");
        }*/

        //查询赛事玩法表
        stopWatch.start("查询赛事玩法表");
        List<MarketCategorySell> marketCategorySellsMTS =
                marketCategorySellService.getItem(updateRiskManagerCodeDTO.getMatchId(),
                        updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name()) ? "1" : "0");
        if (CollectionUtils.isEmpty(marketCategorySellsMTS)) {
            stopWatch.stop();
            log.info("::{}::updateRiskManagerCode ,赛事未玩法开售数据，标准赛事id：{}", request.getLinkId(),
                    oldStandardMatchInfo.getId());
            return Response.failed("该赛事没有设置玩法开售数据");
        }
        stopWatch.stop();

        stopWatch.start("修改赛事开售表");
        Boolean isSell = Boolean.TRUE;
        String oldRiskManager = "";
        if (updateRiskManagerCodeDTO.getType().equals(SellTypeEnums.PRE.name())) {
            if (!standardSportMarketSell.getPreMatchSellStatus().equals(SaleMatchSellStausEnum.Sold.name())) {
                isSell = Boolean.FALSE;
            }
            oldRiskManager = standardSportMarketSell.getPreRiskManagerCode();
            standardSportMarketSell.setPreRiskManagerCode(updateRiskManagerCodeDTO.getRiskManagerCode());
        } else {
            if (!standardSportMarketSell.getLiveMatchSellStatus().equals(SaleMatchSellStausEnum.Sold.name())) {
                isSell = Boolean.FALSE;
            }
            oldRiskManager = standardSportMarketSell.getLiveRiskManagerCode();
            standardSportMarketSell.setLiveRiskManagerCode(updateRiskManagerCodeDTO.getRiskManagerCode());
        }
        Map<String, String> dataSourceCodeMap = new HashMap<>();
        dataSourceCodeMap.put("MTS", DataSourceCodeEnum.SR.code);
        dataSourceCodeMap.put("GTS", DataSourceCodeEnum.BG.code);
        dataSourceCodeMap.put("CTS", DataSourceCodeEnum.BC.code);
        dataSourceCodeMap.put("BTS", DataSourceCodeEnum.BE.code);
        dataSourceCodeMap.put("F2TS", DataSourceCodeEnum.F01.code);
        boolean isGTS = RiskManagerCodeEnums.GTS.name().equalsIgnoreCase(oldRiskManager);
        boolean isMTS = RiskManagerCodeEnums.MTS.name().equalsIgnoreCase(oldRiskManager);
        boolean isCTS = RiskManagerCodeEnums.CTS.name().equalsIgnoreCase(oldRiskManager);
        boolean isBTS = RiskManagerCodeEnums.BTS.name().equalsIgnoreCase(oldRiskManager);
//        String tempDataSourceCode = isMTS?DataSourceCodeEnum.SR.code:isGTS?DataSourceCodeEnum.BG.code:DataSourceCodeEnum.BC.code;
        String tempDataSourceCode = dataSourceCodeMap.get(oldRiskManager);
        standardSportMarketSellService.update(standardSportMarketSell);
        stopWatch.stop();

        StandardSportMarketSell sportMarketSell = standardSportMarketSellService.refreshCache(oldStandardMatchInfo.getId());
        String delKey = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + oldStandardMatchInfo.getId();
        redisService.set(delKey, sportMarketSell);
        log.info("::{}::重新清除开售表缓存,刷新后开售信息:{},修改开售信息:{}",
                request.getLinkId(), JSONObject.toJSONString(sportMarketSell), JSONObject.toJSONString(standardSportMarketSell));
        String oldRiskManageCode = oldRiskManager;
        String newRiskManageCode = updateRiskManagerCodeDTO.getRiskManagerCode();

        //通过用户id查询用户信息
        stopWatch.start("记录操作日志");
        String logInfoZh = PreSaleTraderLogConfigMessageEnum.UPDATE_RISK_MANAGE_CODE.getMessageZh()
                .replace("userId", String.valueOf(operaterId))
                .replace("userName", updateRiskManagerCodeDTO.getUserName())
                .replace("nowTime", DateUtil.now())
                .replace("oldRiskManageCode", oldRiskManageCode)
                .replace("newRiskManageCode", newRiskManageCode)
                .replace("linkId", request.getLinkId());
        String logInfoEn = PreSaleTraderLogConfigMessageEnum.UPDATE_RISK_MANAGE_CODE.getMessageEn()
                .replace("userId", String.valueOf(operaterId))
                .replace("userName", updateRiskManagerCodeDTO.getUserName())
                .replace("nowTime", DateUtil.now())
                .replace("oldRiskManageCode", oldRiskManageCode)
                .replace("newRiskManageCode", newRiskManageCode)
                .replace("linkId", request.getLinkId());
        // 记录操作日志
        standardSportMarketSellLogService.AssemblyAndInsertStandardSportMarketSellLog(standardSportMarketSell.getMatchInfoId(),
                standardSportMarketSell.getId(), logInfoZh, logInfoEn,
                marketType == 1 ? SaleOperateTypeEnum.pre_match.name() : SaleOperateTypeEnum.live_odd.name(),
                String.valueOf(operaterId), String.valueOf(operaterId));
        stopWatch.stop();
        if (!isSell) {
            return Response.success();
        }
        //用于本次修改的标准赛事
        stopWatch.start("修改标准赛事");
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        upStandardMatchInfo.setId(oldStandardMatchInfo.getId());
        //修改标准赛事表
        upStandardMatchInfo.setRiskManagerCode(standardSportMarketSell.getLiveRiskManagerCode());
        upStandardMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        //修改标准赛事
        oldStandardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
        stopWatch.stop();


        //修改玩法开售信息
        stopWatch.start("修改玩法开售信息");
        Set<Long> marketCategoryIdSet = new HashSet();
        Set<Long> closeCategoryIds = new HashSet<>();
        Map<Long, String> marketCategoryIdMap = new HashMap();
        marketCategorySellsMTS.forEach(marketCategorySell -> {
            marketCategorySell.setLinkId(request.getLinkId());
            if (updateRiskManagerCodeDTO.getCategoryIds() != null &&
                    !updateRiskManagerCodeDTO.getCategoryIds().contains(marketCategorySell.getMarketCategoryId())) {
                closeCategoryIds.add(marketCategorySell.getMarketCategoryId());
                marketCategorySell.setSellStatus(SaleMatchSellStausEnum.Unsold.name());
            } else {
                marketCategorySell.setSellStatus(SaleMatchSellStausEnum.Sold.name());
            }
            marketCategorySell.setDataSourceCode(tempDataSourceCode);
            marketCategoryIdSet.add(marketCategorySell.getMarketCategoryId());
            //玩法id
            marketCategoryIdMap.put(marketCategorySell.getMarketCategoryId(), tempDataSourceCode);
        });
        //Map<String, List<Long>> playDataSource = marketCategorySellsMTS.stream().collect(Collectors.groupingBy(MarketCategorySell::getDataSourceCode, Collectors.mapping(MarketCategorySell::getMarketCategoryId, Collectors.toList())));
        if (!CollectionUtils.isEmpty(updateRiskManagerCodeDTO.getCategoryIds())) {
            marketCategorySellService.batchUpdateById(standardSportMarketSell.getMatchInfoId(), marketType, marketCategorySellsMTS);
        }
        stopWatch.stop();

        stopWatch.start("玩法开售流程");
        //下发赔率，关闭多余的玩法
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(marketCategoryIdSet, request.getLinkId(), oldStandardMatchInfo, standardSportMarketSell);
        if (MapUtils.isNotEmpty(stringStandardMarketDataMessageMap)) {
            StandardMatchInfo finalOldStandardMatchInfo = oldStandardMatchInfo;
            stringStandardMarketDataMessageMap.forEach((k, v) -> {
                if (closeCategoryIds.contains(v.getMarketCategoryId())) {
                    v.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    String redisDelKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + finalOldStandardMatchInfo.getId() + "_" + v.getDataSourceCode() + "_" + v.getMarketCategoryId());
                    redisService.hDel(redisDelKey, v.getRelationMarketId().toString());
                }
            });
        }

        //--------操盘后台操作开关封锁,异步处理-----------
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId() + "_OLD_CLOSE",request.getOddsSource(), request.getOperaterId(),oldStandardMatchInfo, marketCategoryIdSet,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        stopWatch.stop();

        Map<Long, String> categoryids = new HashMap<Long, String>();
        updateRiskManagerCodeDTO.getCategoryIds().forEach(e -> {
            categoryids.put(e, tempDataSourceCode);
        });
        soldMessageToOddsProcessor.soldHandler(request.getLinkId(), oldStandardMatchInfo, standardSportMarketSell, categoryids, marketType, true, false);

        log.info("::{}::操盘平台修改PA操盘耗时{}ms," + stopWatch.prettyPrint(), request.getLinkId(), stopWatch.getTotalTimeMillis());

        //下发赛事变更信息
        Long standardMatchId = oldStandardMatchInfo.getId();
        processTradeSystemThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    modifyMatchInfoProducer.pushModifyMatchInfoMessage(request.getLinkId(), standardMatchId, "操盘方式变更", operaterId);
                } catch (Exception e) {
                    log.error("{}::pushModifyMatchInfoMessage ERROR:", request.getLinkId(), e);
                }
            }
        });
        xtsMonitor.set(oldStandardMatchInfo,marketType,tempDataSourceCode,request.getLinkId());
        return Response.success();
    }

    @Override
    public Response putTradeMarketMarginConfig(Request<TradeMarketMarginConfigDTO> request) {
        //validateLinkId("putTradeMarketMarginConfig", request);
        log.info("::{}::盘口配置margin入参: {}", request.getLinkId(), JSON.toJSONString(request));
        StopWatch sw = new StopWatch(UUID.randomUUID().toString() + ":putTradeMarketMarginConfig");
        sw.start("putTradeMarketMarginConfig [获取赛事信息]计算耗时");
        TradeMarketMarginConfigDTO tradeMarketMarginConfigDTO = request.getData();
        Long operaterId = request.getOperaterId();
        List<MarketMarginDtlDTO> marketMarginDtlDTOList = tradeMarketMarginConfigDTO.getMarketMarginDtlDTOList();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(tradeMarketMarginConfigDTO.getStandardMatchInfoId());
        sw.stop();
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeMarketMarginConfig,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    tradeMarketMarginConfigDTO.getStandardMatchInfoId());
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        sw.start("putTradeMarketMarginConfig [获取开售信息]计算耗时");
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        sw.stop();
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeMarketMarginConfig ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
                    standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        //处理margin设置
        sw.start("putTradeMarketMarginConfig [遍历玩法信息]计算耗时");
        marketMarginDtlDTOList.forEach(marketMarginDtlDTO -> {
            if (ObjectUtils.isEmpty(marketMarginDtlDTO.getChildStandardCategoryId())) {
                marketMarginDtlDTO.setChildStandardCategoryId(tradeMarketMarginConfigDTO.getStandardCategoryId());
            }
            //收集日志
            marketCategoryMarginLogService.create(request.getLinkId(),
                    tradeMarketMarginConfigDTO.getStandardMatchInfoId(),
                    tradeMarketMarginConfigDTO.getStandardCategoryId(),
                    marketMarginDtlDTO.getChildStandardCategoryId(),
                    tradeMarketMarginConfigDTO.getMarketType(),
                    tradeMarketMarginConfigDTO.getPlaceNum(), marketMarginDtlDTO, operaterId);
            //二项盘的设置
            ConfigMarketCategoryMargin itemTwo = configMarketCategoryMarginService.getItemTwo(request.getLinkId()
                    , tradeMarketMarginConfigDTO.getStandardMatchInfoId(),
                    tradeMarketMarginConfigDTO.getStandardCategoryId(), marketMarginDtlDTO.getChildStandardCategoryId(), tradeMarketMarginConfigDTO.getPlaceNum());
            if (itemTwo == null) {
                ConfigMarketCategoryMargin configMarketCategoryMargin = new ConfigMarketCategoryMargin();
                configMarketCategoryMargin.setId(UUIdUtils.getId());
                configMarketCategoryMargin.setStandardMatchInfoId(tradeMarketMarginConfigDTO.getStandardMatchInfoId());
                configMarketCategoryMargin.setStandardCategoryId(tradeMarketMarginConfigDTO.getStandardCategoryId());
                configMarketCategoryMargin.setChildStandardCategoryId(marketMarginDtlDTO.getChildStandardCategoryId());
                configMarketCategoryMargin.setMarketType(tradeMarketMarginConfigDTO.getMarketType());
                configMarketCategoryMargin.setPlaceNum(tradeMarketMarginConfigDTO.getPlaceNum());
                configMarketCategoryMargin.setTimeFrame(marketMarginDtlDTO.getTimeFrame());
                configMarketCategoryMargin.setMargin(marketMarginDtlDTO.getMargin());
                configMarketCategoryMargin.setLinkId(request.getLinkId());
                configMarketCategoryMargin.setOperaterId(operaterId);
                configMarketCategoryMargin.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                configMarketCategoryMargin.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                //创建两项盘的spread
                configMarketCategoryMarginService.createTwo(configMarketCategoryMargin);
            } else {
                itemTwo.setMarketType(tradeMarketMarginConfigDTO.getMarketType());
                itemTwo.setMargin(marketMarginDtlDTO.getMargin());
                itemTwo.setLinkId(request.getLinkId());
                itemTwo.setOperaterId(operaterId);
                //更新两项盘的spread
                configMarketCategoryMarginService.updateTwo(itemTwo);
            }
        });
        sw.stop();
        //下发最新赔率
        ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(request.getLinkId(),
                standardMatchInfo.getId(), tradeMarketMarginConfigDTO.getStandardCategoryId());
        Integer tradeType = 0;
        if (null != configTradeType) {
            tradeType = configTradeType.getTradeType();
        }
        //下发margin配置给AO
        List<TradeMarketDiffAndMarginConfigDTO> sendAo = aoMarketDiffAndMarginConfig(request.getLinkId(), standardMatchInfo, null, null, null, Lists.newArrayList(tradeMarketMarginConfigDTO));
        if (!CollectionUtils.isEmpty(sendAo)) {
            aoMatchDiffAndMarginProducer.sendConfig(request.getLinkId(), sendAo);
        }
        if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.MANUAL.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_MODEL.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_NEW.equals(tradeType)) {
            log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", request.getLinkId(), standardMatchInfo.getId(),
                    tradeMarketMarginConfigDTO.getStandardCategoryId());
            return Response.success();
        }
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        sw.start("putTradeMarketMarginConfig [获取缓存盘口信息]计算耗时");
        Set<Long> marketCategoryIds = new HashSet<>();
        marketCategoryIds.add(tradeMarketMarginConfigDTO.getStandardCategoryId());
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(marketCategoryIds, request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
        sw.stop();
        //盘口下发
        sw.start("putTradeMarketMarginConfig [遍历处理盘口信息]计算耗时");
        //异步处理
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                marketCategoryIds, stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        sw.stop();
        log.info("::{}::putTradeMarketMarginConfig处理流程总计算耗时{}ms," + sw.prettyPrint(), request.getLinkId(),
                sw.getTotalTimeMillis());
        return Response.success();
    }

    @Override
    public Response putTradeMarketMarginConfigList(Request<List<TradeMarketMarginConfigDTO>> request) {
        //validateLinkId("putTradeMarketMarginConfigList", request);
        log.info("::{}::盘口配置margin入参: {}", request.getLinkId(), JSON.toJSONString(request));
        List<TradeMarketMarginConfigDTO> tradeMarketMarginConfigDTOList = request.getData();
        Long operaterId = request.getOperaterId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(tradeMarketMarginConfigDTOList.get(0).getStandardMatchInfoId());
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeMarketMarginConfig,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    tradeMarketMarginConfigDTOList.get(0).getStandardMatchInfoId());
            return Response.failed("标准赛事未找到");
        }
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeMarketMarginConfig ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
                    standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        StopWatch sw = new StopWatch(UUID.randomUUID().toString() + ":putTradeMarketMarginConfigList");
        sw.start();
        //收集玩法
        Set<Long> marketCategorys = new HashSet<>();
        //收集需要保存的数据，批量保存两项盘配置
        List<ConfigMarketCategoryMargin> configMarketCategoryMarginSaveListTwo = new ArrayList<>();
        //收集需要修改的数据，批量修改两项盘配置
        List<ConfigMarketCategoryMargin> configMarketCategoryMarginUpdateListTwo = new ArrayList<>();
        for (TradeMarketMarginConfigDTO tradeMarketMarginConfigDTO : tradeMarketMarginConfigDTOList) {
            marketCategorys.add(tradeMarketMarginConfigDTO.getStandardCategoryId());
            List<MarketMarginDtlDTO> marketMarginDtlDTOList = tradeMarketMarginConfigDTO.getMarketMarginDtlDTOList();
            marketMarginDtlDTOList.forEach(marketMarginDtlDTO -> {
                if (ObjectUtils.isEmpty(marketMarginDtlDTO.getChildStandardCategoryId())) {
                    marketMarginDtlDTO.setChildStandardCategoryId(tradeMarketMarginConfigDTO.getStandardCategoryId());
                }
                //如果是二项盘的设置
                ConfigMarketCategoryMargin itemTwo =
                        configMarketCategoryMarginService.getItemTwo(request.getLinkId(),
                                tradeMarketMarginConfigDTO.getStandardMatchInfoId(),
                                tradeMarketMarginConfigDTO.getStandardCategoryId(),
                                marketMarginDtlDTO.getChildStandardCategoryId(),
                                tradeMarketMarginConfigDTO.getPlaceNum());
                if (itemTwo == null) {
                    ConfigMarketCategoryMargin configMarketCategoryMargin = new ConfigMarketCategoryMargin();
                    configMarketCategoryMargin.setId(UUIdUtils.getId());
                    configMarketCategoryMargin.setStandardMatchInfoId(tradeMarketMarginConfigDTO.getStandardMatchInfoId());
                    configMarketCategoryMargin.setStandardCategoryId(tradeMarketMarginConfigDTO.getStandardCategoryId());
                    configMarketCategoryMargin.setChildStandardCategoryId(marketMarginDtlDTO.getChildStandardCategoryId());
                    configMarketCategoryMargin.setMarketType(tradeMarketMarginConfigDTO.getMarketType());
                    configMarketCategoryMargin.setPlaceNum(tradeMarketMarginConfigDTO.getPlaceNum());
                    configMarketCategoryMargin.setTimeFrame(marketMarginDtlDTO.getTimeFrame());
                    configMarketCategoryMargin.setMargin(marketMarginDtlDTO.getMargin());
                    configMarketCategoryMargin.setLinkId(request.getLinkId());
                    configMarketCategoryMargin.setOperaterId(operaterId);
                    configMarketCategoryMargin.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    configMarketCategoryMargin.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    //创建二项盘的spread
                    configMarketCategoryMarginSaveListTwo.add(configMarketCategoryMargin);
                } else {
                    itemTwo.setMarketType(tradeMarketMarginConfigDTO.getMarketType());
                    itemTwo.setMargin(marketMarginDtlDTO.getMargin());
                    itemTwo.setLinkId(request.getLinkId());
                    itemTwo.setOperaterId(operaterId);
                    //更新两项盘的spread
                    configMarketCategoryMarginUpdateListTwo.add(itemTwo);
                }
            });
        }
        //批量插入二项盘的spread
        if (!CollectionUtils.isEmpty(configMarketCategoryMarginSaveListTwo)) {
            configMarketCategoryMarginService.insertListTwo(configMarketCategoryMarginSaveListTwo);
        }
        //批量更新二项盘的spread
        if (!CollectionUtils.isEmpty(configMarketCategoryMarginUpdateListTwo)) {
            configMarketCategoryMarginService.updateListTwo(configMarketCategoryMarginUpdateListTwo);
        }
        sw.stop();
        log.info("::{}::putTradeMarketMarginConfigList处理入库总计算耗时{}ms," + sw.prettyPrint(), request.getLinkId(), sw.getTotalTimeMillis());
        //玩法维度的操盘配置,直接一次性从库查出赛事玩法级的手自动类型，比循环查更快
        if (CollectionUtils.isEmpty(marketCategorys)) {
            log.info("::{}::玩法集合为空，赔率不下发，标准赛事id={},玩法集合={}", request.getLinkId(), standardMatchInfo.getId(),
                    JSON.toJSONString(marketCategorys));
            return Response.failed("玩法集合为空，赔率不下发");
        }
        Map<Long, Integer> tradeTypeMap = configTradeTypeService.getItemByMatchAndCategorys(standardMatchInfo.getId().toString(), marketCategorys);
        //筛选出需要下发的玩法
        Set<Long> marketCategoryValid = new HashSet<>();
        for (Long marketCategory : marketCategorys) {
            //如果tradeTypeMap.get(marketCategory) == null，默认是自动
            if (tradeTypeMap.get(marketCategory) == null || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeMap.get(marketCategory))) {
                marketCategoryValid.add(marketCategory);
            } else {
                log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", request.getLinkId(),
                        standardMatchInfo.getId(), marketCategory);
            }
        }
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(marketCategoryValid, request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
        //下发marginList配置给AO
        List<TradeMarketDiffAndMarginConfigDTO> sendAo = aoMarketDiffAndMarginConfig(request.getLinkId(), standardMatchInfo, stringStandardMarketDataMessageMap, null, null, tradeMarketMarginConfigDTOList);
        if (!CollectionUtils.isEmpty(sendAo)) {
            aoMatchDiffAndMarginProducer.sendConfig(request.getLinkId(), sendAo);
        }
        //异步处理
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, marketCategoryValid,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }
    /**
     * 获取缓存中的所有盘口，赛前数据商和滚球数据商
     * 自动关盘，需要关所有数据源下此玩法数据
     * @param linkId
     * @param standardMatchInfo
     * @param standardSportMarketSell
     * @return
     */
    public Map<String, StandardMarketDataMessage> getStringStandardMarketDataMessageMapAutoClose(Set<Long> marketCategoryIds,
                                                                                        String linkId,
                                                                                        StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(standardMatchInfo.getId());
        Set<String> dataSourceCodes = thirdMatchInfoList.stream().map(e->e.getDataSourceCode()).collect(Collectors.toSet());
        int oddsLive = isOddsLive(standardMatchInfo.getId());
        //赛前数据源
        String preProviderCode = standardSportMarketSell.getPreMatchDataProviderCode();
        //滚球数据源
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        log.info("::{}:: 标准赛事id：{},赛前服务商={},滚球服务商={} ,oddsLive:{} ", linkId, standardMatchInfo.getId(), preProviderCode,
                liveProviderCode, oddsLive);
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
        if (StringUtils.isNotBlank(preProviderCode) && 1 == oddsLive) {
            List<String> preKeys = new ArrayList<>();
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    String dataSourceCode = dataSourceCodeEntry.getValue();
                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    preKeys.add(tempRedisKey);
                }
            } else {
                //获取玩法开售表，赛前玩法数据源
                List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItemByDataSourceCodeAndMarketType(standardMatchInfo.getId(), preProviderCode, "1");
                if (CollectionUtils.isEmpty(marketCategorySell)) {
                    return stringStandardMarketDataMessageMap;
                }
                Set<Long> marketCategorySellIdSet = marketCategorySell.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
                for (Long marketCategorySellId : marketCategorySellIdSet) {
                    //获取缓存中所有赛前盘口
                    String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + preProviderCode + "_" + marketCategorySellId);
                    preKeys.add(redisKey);
                }
            }
            Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.syncOddsMultiGetAll(preKeys);
            if (MapUtils.isNotEmpty(standardMarketMessageMap)) {
                log.info("::{}:: sendMatchMarketData 赛前redisKey={}，赛前盘缓存总数据：{} ", linkId, preKeys, standardMarketMessageMap.size());
                Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                stringStandardMarketDataMessageMap.putAll(preMap);
            }
        }
        if (StringUtils.isNotBlank(liveProviderCode) && 0 == oddsLive) {
            List<String> liveKeys = new ArrayList<>();
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 0;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    for (String dataSourceCode : dataSourceCodes){
                        String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                        liveKeys.add(tempRedisKey);
                    }
                    String dataSourceCode = dataSourceCodeEntry.getValue();
                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    liveKeys.add(tempRedisKey);
                }
            } else {
                //获取玩法开售表，滚球玩法数据源
                List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItemByDataSourceCodeAndMarketType(standardMatchInfo.getId(), liveProviderCode, "0");
                if (CollectionUtils.isEmpty(marketCategorySell)) {
                    return stringStandardMarketDataMessageMap;
                }
                Set<Long> marketCategorySellIdSet = marketCategorySell.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
                for (Long marketCategorySellId : marketCategorySellIdSet) {
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategorySellId)) {
                        continue;
                    }
                    //获取缓存中所有赛前盘口
                    String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + liveProviderCode + "_" + marketCategorySellId);
                    liveKeys.add(redisKey);
                }
            }
            Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.syncOddsMultiGetAll(liveKeys);
            if (MapUtils.isNotEmpty(standardMarketMessageMap)) {
                log.info("::{}:: sendMatchMarketData 滚球redisKey={}，滚球盘缓存总数据：{} ", linkId, liveKeys, standardMarketMessageMap.size());
                Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                stringStandardMarketDataMessageMap.putAll(liveMap);
            }
        }
        log.info("::{}:: 标准赛事id：{},从缓存获取数据总数：{} ", linkId, standardMatchInfo.getId(), stringStandardMarketDataMessageMap.size());
        return stringStandardMarketDataMessageMap;
    }

    /**
     * 获取缓存中的所有盘口，赛前数据商和滚球数据商
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardSportMarketSell
     * @return
     */
    public Map<String, StandardMarketDataMessage> getStringStandardMarketDataMessageMap(Set<Long> marketCategoryIds,
                                                                                        String linkId,
                                                                                        StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
        int oddsLive = isOddsLive(standardMatchInfo.getId());
        //赛前数据源
        String preProviderCode = standardSportMarketSell.getPreMatchDataProviderCode();
        //滚球数据源
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        log.info("::{}:: 标准赛事id：{},赛前服务商={},滚球服务商={} ,oddsLive:{} ", linkId, standardMatchInfo.getId(), preProviderCode,
                liveProviderCode, oddsLive);
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
        if (StringUtils.isNotBlank(preProviderCode) && 1 == oddsLive) {
            List<String> preKeys = new ArrayList<>();
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    String dataSourceCode = supportA99(linkId,standardMatchInfo.getId(),1,marketCategoryId)?"A99":dataSourceCodeEntry.getValue();
                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    preKeys.add(tempRedisKey);
                }
            } else {
                //获取玩法开售表，赛前玩法数据源
                List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItemByDataSourceCodeAndMarketType(standardMatchInfo.getId(), preProviderCode, "1");
                if (CollectionUtils.isEmpty(marketCategorySell)) {
                    return stringStandardMarketDataMessageMap;
                }
                Set<Long> marketCategorySellIdSet = marketCategorySell.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
                for (Long marketCategorySellId : marketCategorySellIdSet) {
                    //获取缓存中所有赛前盘口
                    String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + preProviderCode + "_" + marketCategorySellId);
                    preKeys.add(redisKey);
                }
            }
            Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.syncOddsMultiGetAll(preKeys);
            if (MapUtils.isNotEmpty(standardMarketMessageMap)) {
                log.info("::{}:: sendMatchMarketData 赛前redisKey={}，赛前盘缓存总数据：{} ", linkId, preKeys, standardMarketMessageMap.size());
                Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                stringStandardMarketDataMessageMap.putAll(preMap);
            }
        }
        if (StringUtils.isNotBlank(liveProviderCode) && 0 == oddsLive) {
            List<String> liveKeys = new ArrayList<>();
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 0;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    String dataSourceCode = supportA99(linkId,standardMatchInfo.getId(),0,marketCategoryId)?"A99":dataSourceCodeEntry.getValue();
                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    liveKeys.add(tempRedisKey);
                }
            } else {
                //获取玩法开售表，滚球玩法数据源
                List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItemByDataSourceCodeAndMarketType(standardMatchInfo.getId(), liveProviderCode, "0");
                if (CollectionUtils.isEmpty(marketCategorySell)) {
                    return stringStandardMarketDataMessageMap;
                }
                Set<Long> marketCategorySellIdSet = marketCategorySell.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
                for (Long marketCategorySellId : marketCategorySellIdSet) {
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategorySellId)) {
                        continue;
                    }
                    //获取缓存中所有赛前盘口
                    String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + liveProviderCode + "_" + marketCategorySellId);
                    liveKeys.add(redisKey);
                }
            }
            Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.syncOddsMultiGetAll(liveKeys);
            if (MapUtils.isNotEmpty(standardMarketMessageMap)) {
                log.info("::{}:: sendMatchMarketData 滚球redisKey={}，滚球盘缓存总数据：{} ", linkId, liveKeys, standardMarketMessageMap.size());
                Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                stringStandardMarketDataMessageMap.putAll(liveMap);
            }
        }
        log.info("::{}:: 标准赛事id：{},从缓存获取数据总数：{} ", linkId, standardMatchInfo.getId(), stringStandardMarketDataMessageMap.size());
        return stringStandardMarketDataMessageMap;
    }

    @Autowired
    private ConfigMatchStatusService configMatchStatusService;

    @Override
    public Response putMarketTwoStatusConfig(Request<MarketTwoStatusConfigDTO> request) {
        log.info("::{}::putMarketTwoStatusConfig 入参:{}", request.getLinkId(), JSONUtil.toJsonStr(request));
        String linkId = request.getLinkId();
        MarketTwoStatusConfigDTO marketTwoStatusConfigDTO = request.getData();
        Long standardMatchId = marketTwoStatusConfigDTO.getStandardMatchId();
        Integer marketType = marketTwoStatusConfigDTO.getMarketType();
        if (marketType == null) {
            log.info("::{}::putMarketTwoStatusConfig,marketType参数为空，不做逻辑处理，标准赛事id:{}，赛事类别:{}", linkId, standardMatchId, marketType);
            return Response.failed("marketType参数为空，不做逻辑处理，标准赛事id：" + standardMatchId);
        }
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            log.info("::{}::putMarketTwoStatusConfig,对应的标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
            return Response.failed("标准赛事未找到，标准赛事id：" + standardMatchId);
        }
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code)) {
            log.info("::{}::putMarketTwoStatusConfig,只支持足球，标准赛事id:{}", linkId, standardMatchId);
            return Response.failed("只支持足球，标准赛事id：" + standardMatchId);
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putMarketTwoStatusConfig ,赛事未开售，标准赛事id：{}", linkId, standardMatchId);
            return Response.failed("赛事未开售，标准赛事id：" + standardMatchId);
        }
        ConfigMatchStatus configMatchStatus = configMatchStatusService.getItem(linkId, standardMatchId, marketType);
        if (configMatchStatus == null) {
            ConfigMatchStatus configMatchStatus1 = new ConfigMatchStatus();
            configMatchStatus1.setId(UUIdUtils.getId());
            configMatchStatus1.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMatchStatus1.setLinkId(linkId);
            configMatchStatus1.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMatchStatus1.setOperaterId(request.getOperaterId());
            configMatchStatus1.setStandardMatchInfoId(standardMatchId);
            configMatchStatus1.setStatus(marketTwoStatusConfigDTO.getStatus());
            configMatchStatus1.setMarketType(marketTwoStatusConfigDTO.getMarketType());
            configMatchStatusService.create(linkId, configMatchStatus1);
        } else {
            configMatchStatus.setStatus(marketTwoStatusConfigDTO.getStatus());
            configMatchStatus.setOperaterId(request.getOperaterId());
            configMatchStatus.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMatchStatus.setLinkId(linkId);
            configMatchStatus.setMarketType(marketTwoStatusConfigDTO.getMarketType());
            configMatchStatusService.update(linkId, configMatchStatus);
        }
        return Response.success();
    }

    @Override
    public void autoCloseMarket(String linkId, Long standardMatchId, Set<Long> marketCategoryIds, Long dataSourceTime) {
        log.info("::{}::autoCloseMarket标准赛事id:{},未过滤关盘玩法:{}", linkId, standardMatchId, marketCategoryIds);
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            log.info("::{}::autoCloseMarket,对应的标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
            return;
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::autoCloseMarket ,赛事未开售赔率不下发，标准赛事id：{}", linkId, standardMatchId);
            return;
        }
        if (DataSourceUtils.isN01OrN02DataSource(standardSportMarketSell.getBusinessEvent())) {
            log.info("::{}::autoCloseMarket skip, 事件源为 N01/N02 不触发自动关盘, matchId={}, businessEvent={}",
                    linkId, standardMatchId, standardSportMarketSell.getBusinessEvent());
            return;
        }
        //未设置滚球数据服务商不下发
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        if (StringUtils.isBlank(liveProviderCode)) {
            log.info("::{}::autoCloseMarket ,赛事的开售未设置滚球操盘,标准赛事id：{}", linkId, standardMatchId);
            return;
        }
        //获取所有多数据源盘口赔率（包含赛前）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMapAutoClose(marketCategoryIds, linkId, standardMatchInfo, standardSportMarketSell);
        //判断是否是足球，足球需要收盘，需求1042
        boolean isFootBall = standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code);
        //只取滚球数据
        Map<String, StandardMarketDataMessage> standardMarketLiveDataMessageMap = stringStandardMarketDataMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketLiveDataMessageMap.values()) {
            String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
            //只修改有改变的玩法 状态改为关盘
            Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
            Object a01ExtendedTimeObjects  = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + standardMatchInfo.getId());
            if (!Objects.isNull(a01ExtendedTimeObjects)) {
                Integer a01ExtendedTimeStatus = (Integer) a01ExtendedTimeObjects;
                if (a01ExtendedTimeStatus == 1 && standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)
                        && MarginCategoryConfig.A01_EXTENDED_TIME_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())) {
                    continue;
                }
            }
            if (marketCategoryIds.contains(marketCategoryId)) {
                standardMarketDataMessage.setAutoCloseStatus(Constant.AOTU_CLOSE_STATUS);
                standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                if (isFootBall) {
                    standardMarketDataMessage.setEndEdStatus(1);
                }
                //这里是唯一改变数据源状态的地方
                standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                standardMarketDataMessage.setLinkId(linkId);
                standardMarketDataMessage.setRemark("玩法自动关盘");
                log.info("::{}:autoCloseMarket，玩法自动关盘准备缓存赔率信息，relationMarketId={}",
                        linkId, standardMarketDataMessage.getRelationMarketId().toString());
                boolean flag = redisService.hSet(redisKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                // 发送操盘日志给风控
                commonAsyncService.sendDeactivatedBySystemLogToRisk(linkId,standardMatchInfo, convertLog(standardMarketDataMessage));
                if (!flag) {
                    log.error("::{}::autoCloseMarket，标准赛事id:{},relationMarketId={},刷入缓存失败,赔率处理异常",
                            linkId, standardMatchInfo.getId(), standardMarketDataMessage.getRelationMarketId());
                }
            }
        }
        //刷新标准玩法状态为关盘
        standardSportMarketService.updateBySelective(standardMatchId, marketCategoryIds, Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
        //--------------------------玩法级关盘、玩法已经自动关盘，不下发--------------------------
        String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
        Map<String, String> autoCloseMap = redisService.hGetAll(autoCloseRedisKey);
        //最终关盘玩法
        Set<Long> finalMarketCategoryIdSet = new HashSet();
        marketCategoryIds.forEach(categoryId -> {
            // 已下发玩法、不再下发
            if (autoCloseMap != null && !autoCloseMap.containsKey(categoryId.toString())) {
                finalMarketCategoryIdSet.add(categoryId);
                redisService.hSet(autoCloseRedisKey, String.valueOf(categoryId), TimeUtils.millsSecondsEast8ZoneGmt(), RedisConfig.REDIS_WEEK_TIME);
            }
        });
        if (CollectionUtils.isEmpty(finalMarketCategoryIdSet)) {
            log.info("::{}::autoCloseMarket标准赛事id:{},最终关盘玩法为空", linkId, standardMatchId);
            return;
        }
        log.info("::{}::autoCloseMarket标准赛事id:{},最终关盘玩法:{},", linkId, standardMatchId, finalMarketCategoryIdSet);
        //将自动关闭的玩法推送给操盘（操盘A+和M模式需要用）
        StandardCategoryAutoCloseMessage standardCategoryAutoCloseMessage = new StandardCategoryAutoCloseMessage();
        standardCategoryAutoCloseMessage.setStandardMatchId(standardMatchInfo.getId());
        standardCategoryAutoCloseMessage.setStandardCategoryList(new ArrayList<>(finalMarketCategoryIdSet));
        standardCategoryAutoCloseProducer.sendStandardCategoryAutoClose(linkId, standardCategoryAutoCloseMessage);
        //盘口推送
        thirdMatchMarketProcessor.processOddsByAll(linkId,-1, null,standardMatchInfo, finalMarketCategoryIdSet, standardMarketLiveDataMessageMap, dataSourceTime, standardSportMarketSell, new HashMap<>());
    }

    @Override
    public void autoOpenMarket(String linkId, Long standardMatchId, Set<Long> marketCategoryIds, Long dataSourceTime) {
        log.info("::{}::autoOpenMarket标准赛事id:{},开盘玩法:{}", linkId, standardMatchId, marketCategoryIds);
        if (marketCategoryIds.isEmpty()){
            return;
        }
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            log.info("::{}::autoOpenMarket,对应的标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
            return;
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::autoOpenMarket ,赛事未开售赔率不下发，标准赛事id：{}", linkId, standardMatchId);
            return;
        }
        //未设置滚球数据服务商不下发
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        if (StringUtils.isBlank(liveProviderCode)) {
            log.info("::{}::autoOpenMarket ,赛事的开售未设置滚球操盘,标准赛事id：{}", linkId, standardMatchId);
            return;
        }
        //获取所有多数据源盘口赔率（包含赛前）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMapAutoClose(marketCategoryIds, linkId, standardMatchInfo, standardSportMarketSell);
        //只取滚球数据
        Map<String, StandardMarketDataMessage> standardMarketLiveDataMessageMap = stringStandardMarketDataMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        //盘口推送
        thirdMatchMarketProcessor.processOddsByAll(linkId,-1, null,standardMatchInfo, marketCategoryIds, standardMarketLiveDataMessageMap, dataSourceTime, standardSportMarketSell, new HashMap<>());
    }

    @Override
    public void autoCloseChildMarketCategory(String linkId, Long standardMatchId, Pair<Set<Long>, Map<String,JSONObject>> map, Long dataSourceTime) {
        if (null == map || map.getLeft().size() == 0) {
            return;
        }
        log.info("::{}::autoCloseChildMarketCategory:{},未过滤关盘玩法:{}", linkId, standardMatchId, map);
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            log.info("::{}::autoCloseChildMarketCategory,对应的标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
            return;
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::autoCloseChildMarketCategory ,赛事未开售赔率不下发，标准赛事id：{}", linkId, standardMatchId);
            return;
        }
        if (DataSourceUtils.isN01OrN02DataSource(standardSportMarketSell.getBusinessEvent())) {
            log.info("::{}::autoCloseChildMarketCategory skip, 事件源为 N01/N02 不触发自动关盘, matchId={}, businessEvent={}",
                    linkId, standardMatchId, standardSportMarketSell.getBusinessEvent());
            return;
        }
        //未设置滚球数据服务商不下发
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        if (StringUtils.isBlank(liveProviderCode)) {
            log.info("::{}::autoCloseChildMarketCategory ,赛事的开售未设置滚球操盘,标准赛事id：{}", linkId, standardMatchId);
            return;
        }
        // Map<子玩法,玩法>
        Map<Long,Long> childMarketCategoryIdsMap = new HashMap<>();
        // Map<子玩法,关盘玩法子玩法信息>
        Map<Long,JSONObject> childMarketCategoryObjMap = new HashMap<>();
        //获取所有多数据源盘口赔率（包含赛前）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(map.getLeft(), linkId, standardMatchInfo, standardSportMarketSell);
        //只取滚球数据
        Map<String, StandardMarketDataMessage> standardMarketLiveDataMessageMap = stringStandardMarketDataMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketLiveDataMessageMap.values()) {
            String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
            //只修改有改变的玩法 状态改为关盘
            Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
            if (map.getLeft().contains(marketCategoryId)) {
                JSONObject object = map.getRight().get(marketCategoryId+"");
                String addition1 = standardMarketDataMessage.getAddition1();
                String addition2 = standardMarketDataMessage.getAddition2();
                String target = object.getString("target");
                String xValue = object.getString("x");
                boolean isTrue = true;
                if (target.equals("add1") && xValue.equals(addition1)){
                }else if (target.equals("add2")&& xValue.equals(addition2)){
                }else {
                    isTrue = false;
                }
                if (isTrue){
                    childMarketCategoryIdsMap.put(standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getMarketCategoryId());
                    childMarketCategoryObjMap.put(standardMarketDataMessage.getChildMarketCategoryId(), object);
                    standardMarketDataMessage.setAutoCloseStatus(Constant.AOTU_CLOSE_STATUS);
                    standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                    //这里是唯一改变数据源状态的地方
                    standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                    standardMarketDataMessage.setLinkId(linkId);
                    standardMarketDataMessage.setRemark("子玩法自动关盘");
                    log.info("::{}:autoCloseChildMarketCategory，子玩法:{},自动关盘准备缓存赔率信息，relationMarketId={}",
                            linkId, standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getRelationMarketId().toString());
                    boolean flag = redisService.hSet(redisKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                    if (!flag) {
                        log.error("::{}::autoCloseChildMarketCategory，标准赛事id:{},relationMarketId={},刷入缓存失败,赔率处理异常",
                                linkId, standardMatchInfo.getId(), standardMarketDataMessage.getRelationMarketId());
                    }
                    // 发送操盘日志给风控
                    commonAsyncService.sendDeactivatedBySystemLogToRisk(linkId,standardMatchInfo, convertLog(standardMarketDataMessage));
                }
            }
        }
        //--------------------------玩法级关盘、玩法已经自动关盘，不下发--------------------------
        String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
        Map<String, String> autoCloseMap = redisService.hGetAll(autoCloseRedisKey);
        //最终关盘玩法
        Set<Long> finalMarketCategoryIdSet = new HashSet();
        //最终关盘玩法子玩法
       List<JSONObject> finalChildMarketCategoryObjSet = new ArrayList<>();
        childMarketCategoryIdsMap.keySet().forEach(childCategoryId -> {
            // 已下发玩法、不再下发
            if (autoCloseMap != null && !autoCloseMap.containsKey(childCategoryId.toString())) {
                finalMarketCategoryIdSet.add(childMarketCategoryIdsMap.get(childCategoryId));
                finalChildMarketCategoryObjSet.add(childMarketCategoryObjMap.get(childCategoryId));
                redisService.hSet(autoCloseRedisKey, String.valueOf(childCategoryId), TimeUtils.millsSecondsEast8ZoneGmt(), RedisConfig.REDIS_WEEK_TIME);
            }
        });
        if (CollectionUtils.isEmpty(finalMarketCategoryIdSet)) {
            log.info("::{}::autoCloseChildMarketCategory标准赛事id:{},最终关盘子玩法为空", linkId, standardMatchId);
            return;
        }
        log.info("::{}::autoCloseChildMarketCategory标准赛事id:{},最终关盘子玩法:{}-{},", linkId, standardMatchId, finalMarketCategoryIdSet,finalChildMarketCategoryObjSet);
        //将自动关闭的玩法推送给操盘（操盘A+和M模式需要用）
        StandardCategoryAutoCloseMessage standardCategoryAutoCloseMessage = new StandardCategoryAutoCloseMessage();
        standardCategoryAutoCloseMessage.setStandardMatchId(standardMatchInfo.getId());
        standardCategoryAutoCloseMessage.setStandardCategoryJsonList(finalChildMarketCategoryObjSet);
        standardCategoryAutoCloseProducer.sendStandardChildCategoryAutoClose(linkId, standardCategoryAutoCloseMessage);
        //盘口推送
        thirdMatchMarketProcessor.processOddsByAll(linkId,-1,null, standardMatchInfo, finalMarketCategoryIdSet, standardMarketLiveDataMessageMap, dataSourceTime, standardSportMarketSell, new HashMap<>());
    }

    @Override
    public Response putTradeMarketHeadGapConfig(Request<TradeMarketHeadGapConfigDTO> request) {
        //validateLinkId("putTradeMarketHeadGapConfig", request);
        log.info("::{}::putTradeMarketHeadGapConfig入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeMarketHeadGapConfigDTO tradeMarketHeadGapConfigDTO = request.getData();
        Long operaterId = request.getOperaterId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(tradeMarketHeadGapConfigDTO.getStandardMatchInfoId());
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeMarketHeadGapConfig,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    tradeMarketHeadGapConfigDTO.getStandardMatchInfoId());
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeMarketHeadGapConfig ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
                    standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        if (ObjectUtils.isEmpty(tradeMarketHeadGapConfigDTO.getChildStandardCategoryId())) {
            tradeMarketHeadGapConfigDTO.setChildStandardCategoryId(tradeMarketHeadGapConfigDTO.getStandardCategoryId());
        }
        //收集日志
        configMarketHeadGapLogService.create(request.getLinkId(), operaterId, tradeMarketHeadGapConfigDTO);
        ConfigMarketCategoryHead item = configMarketHeadGapService.getItemCache(request.getLinkId(),
                tradeMarketHeadGapConfigDTO.getStandardMatchInfoId(),
                tradeMarketHeadGapConfigDTO.getStandardCategoryId(), tradeMarketHeadGapConfigDTO.getChildStandardCategoryId());
        if (item == null) {
            ConfigMarketCategoryHead configMarketCategoryHead = new ConfigMarketCategoryHead();
            configMarketCategoryHead.setId(UUIdUtils.getId());
            configMarketCategoryHead.setStandardMatchInfoId(tradeMarketHeadGapConfigDTO.getStandardMatchInfoId());
            configMarketCategoryHead.setStandardCategoryId(tradeMarketHeadGapConfigDTO.getStandardCategoryId());
            configMarketCategoryHead.setChildStandardCategoryId(tradeMarketHeadGapConfigDTO.getChildStandardCategoryId());
            configMarketCategoryHead.setMarketType(tradeMarketHeadGapConfigDTO.getMarketType());
            configMarketCategoryHead.setMarketHeadGap(tradeMarketHeadGapConfigDTO.getMarketHeadGap());
            configMarketCategoryHead.setMarketHeadGapInitial(tradeMarketHeadGapConfigDTO.getMarketHeadGapInitial());
            configMarketCategoryHead.setLinkId(request.getLinkId());
            configMarketCategoryHead.setOperaterId(operaterId);
            configMarketCategoryHead.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMarketCategoryHead.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMarketHeadGapService.saveOrUpdateCache(request.getLinkId(), configMarketCategoryHead);
        } else {
            item.setMarketHeadGap(tradeMarketHeadGapConfigDTO.getMarketHeadGap());
            item.setMarketHeadGapInitial(tradeMarketHeadGapConfigDTO.getMarketHeadGapInitial());
            item.setMarketType(tradeMarketHeadGapConfigDTO.getMarketType());
            item.setLinkId(request.getLinkId());
            item.setOperaterId(operaterId);
            configMarketHeadGapService.saveOrUpdateCache(request.getLinkId(), item);
        }
        ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(request.getLinkId(),
                standardMatchInfo.getId(), tradeMarketHeadGapConfigDTO.getStandardCategoryId());
        Integer tradeType = 0;
        if (null != configTradeType) {
            tradeType = configTradeType.getTradeType();
        }
        if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.MANUAL.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_MODEL.equals(tradeType)
                || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_NEW.equals(tradeType)) {
            log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", request.getLinkId(), standardMatchInfo.getId(),
                    tradeMarketHeadGapConfigDTO.getStandardCategoryId());
            return Response.success();
        }
        //盘口下发
        Long categoryId = tradeMarketHeadGapConfigDTO.getStandardCategoryId();
        Set<Long> set = new HashSet<>();
        set.add(categoryId);
        if (MarginCategoryConfig.HANDICAP_CATEGORY_SUBSECTION.contains(categoryId)) {
            set.add(MarginCategoryConfig.HANDICAP_WINNER_MAP.get(categoryId));
        }
        //下发最新赔率
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(set, request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                set, stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Response putTradeMarketStatusConfig(Request<TradeMarketStatusConfigDTO> request) {
        //validateLinkId("putTradeMarketStatusConfig", request);
        log.info("::{}::putTradeMarketStatusConfig入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeMarketStatusConfigDTO tradeMarketStatusConfigDTO = request.getData();
        Long operaterId = request.getOperaterId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(tradeMarketStatusConfigDTO.getStandardMatchInfoId());
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeMarketStatusConfig,标准赛事未找到，标准赛事id:{}", request.getLinkId(),
                    tradeMarketStatusConfigDTO.getStandardMatchInfoId());
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeMarketStatusConfig ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
                    standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }
        ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(request.getLinkId(),
                standardMatchInfo.getId(), tradeMarketStatusConfigDTO.getStandardCategoryId());
        Integer tradeType = 0;
        if (null != configTradeType) {
            tradeType = configTradeType.getTradeType();
        }
        if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeType)) {
            log.info("::{}::putTradeMarketStatusConfig 赛事id:{},标准玩法id:{},只有A模式才有盘口值的弃用与开启操作", request.getLinkId(),
                    standardMatchInfo.getId(), tradeMarketStatusConfigDTO.getStandardCategoryId());
            return Response.failed("只有A模式才有盘口值的弃用与开启操作");
        }
        //校验通过，配置入库操作
        ConfigMarketStatusTrade configMarketStatusTrade =
                configMarketStatusTradeService.getItemOne(tradeMarketStatusConfigDTO.getStandardMatchInfoId(),tradeMarketStatusConfigDTO.getRelationMarketId(),
                        tradeMarketStatusConfigDTO.getMarketType());
        if (null == configMarketStatusTrade) {
            ConfigMarketStatusTrade configMarketStatusTrade1 = new ConfigMarketStatusTrade();
            configMarketStatusTrade1.setId(UUIdUtils.getId());
            configMarketStatusTrade1.setRelationMarketId(tradeMarketStatusConfigDTO.getRelationMarketId());
            configMarketStatusTrade1.setMarketStatus(tradeMarketStatusConfigDTO.getMarketStatus() == Constant.SPORT_MARKET.STATUS.LOSE ? Constant.SPORT_MARKET.STATUS.LOSE : Constant.SPORT_MARKET.STATUS.ACTIVE);
            configMarketStatusTrade1.setAddtion(tradeMarketStatusConfigDTO.getAddtion());
            configMarketStatusTrade1.setLinkId(request.getLinkId());
            configMarketStatusTrade1.setMarketType(tradeMarketStatusConfigDTO.getMarketType());
            configMarketStatusTrade1.setStandardCategoryId(tradeMarketStatusConfigDTO.getStandardCategoryId());
            configMarketStatusTrade1.setStandardMatchInfoId(tradeMarketStatusConfigDTO.getStandardMatchInfoId());
            configMarketStatusTrade1.setOperaterId(operaterId);
            configMarketStatusTrade1.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMarketStatusTrade1.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMarketStatusTradeService.create(configMarketStatusTrade1);
        } else {
            configMarketStatusTrade.setLinkId(request.getLinkId());
            configMarketStatusTrade.setOperaterId(operaterId);
            configMarketStatusTrade.setMarketStatus(tradeMarketStatusConfigDTO.getMarketStatus() == Constant.SPORT_MARKET.STATUS.LOSE ? Constant.SPORT_MARKET.STATUS.LOSE : Constant.SPORT_MARKET.STATUS.ACTIVE);
            configMarketStatusTrade.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configMarketStatusTradeService.update(configMarketStatusTrade);
        }
        Set<Long> marketCategoryIds = new HashSet<>();
        marketCategoryIds.add(tradeMarketStatusConfigDTO.getStandardCategoryId());
        //下发最新赔率
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(marketCategoryIds, request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
        //盘口下发
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                marketCategoryIds, stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Response checkChangeOpeartor(Request<TradeCloseOpeartorDTO> request) {
        log.info("::{}::checkChangeOpeartor入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeCloseOpeartorDTO tradeCloseOpeartorDTO = request.getData();
        ThirdMatchInfo oldThirdMatchInfo = thirdMatchInfoService.getItem(tradeCloseOpeartorDTO.getMatchId(),
                tradeCloseOpeartorDTO.getDataSourceCode());
        if (null == oldThirdMatchInfo) {
            log.info("::{}::checkChangeOpeartor入参，查询三方赛事为空,赛事id={}", request.getLinkId(),
                    tradeCloseOpeartorDTO.getMatchId());
            return Response.failed("checkChangeOpeartor入参，查询三方赛事为空");
        }
        if (0 != tradeCloseOpeartorDTO.getMarketType()) {
            log.info("::{}::checkChangeOpeartor入参，只有滚球盘口才需要检查,赛事id={}", request.getLinkId(),
                    tradeCloseOpeartorDTO.getMatchId());
            return Response.failed("checkChangeOpeartor入参，只有滚球盘口才需要检查");
        }
        //当已经进入滚球或者新切入的数据源有滚球，需要关盘
        String switchLiveRedisKey =
                Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + tradeCloseOpeartorDTO.getMatchId();
        Object obj = redisService.get(switchLiveRedisKey);
        List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketService.getItemList(oldThirdMatchInfo.getId(), 0);
        if (ObjectUtil.isNotEmpty(obj) || !CollectionUtils.isEmpty(thirdSportMarkets)) {
            return Response.needClose("需要关盘");
        }
        return Response.noNeedClose("不需要关盘");
    }

    @Override
    public Response clearCacheMarketCategoryId(Long standardMatchId, Long marketCategoryId) {
        String linkId = IdWorker.getId() + "_clear_category";
        log.info("::{}::手动清除已经自动关盘缓存玩法,标准赛事ID:{},标准玩法ID:{}", linkId, standardMatchId, marketCategoryId);
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            log.info("::{}::clearAutoCloseCategory,对应的标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
            return Response.failed(String.format("::linkId=%s::对应的标准赛事未找到，标准赛事id:%d", linkId, standardMatchId));
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        if (standardSportMarketSell == null) {
            log.info("::{}::clearAutoCloseCategory ,赛事未开售赔率不下发，标准赛事id：{}", linkId, standardMatchId);
            return Response.failed(String.format("::linkId=%s::赛事未开售赔率不下发，标准赛事id:%d", linkId, standardMatchId));
        }
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        if (StringUtils.isBlank(liveProviderCode)) {
            return Response.failed(String.format("::linkId=%s::赛事的开售未设置滚球操盘，标准赛事id:%d", linkId, standardMatchId));
        }
        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchId);
        Object o = redisService.hGet(redisKey, String.valueOf(marketCategoryId));
        if (Objects.isNull(o)) {
            return Response.failed(String.format("::linkId=%s::标准赛事id:%d,该玩法缓存中不存在:%d", linkId, standardMatchId,
                    marketCategoryId));
        }
        redisService.hDel(redisKey, String.valueOf(marketCategoryId));
        log.info("::{}::手动清除已经自动关盘缓存玩法清理成功,标准赛事id:{},滚球数据源:{},标准玩法id:{}", linkId, standardMatchId, liveProviderCode,
                marketCategoryId);

        return Response.success(String.format("::linkId=%s::缓存清理成功,标准赛事id:%d,滚球数据源:%s,标准玩法id:%d", linkId,
                standardMatchId, liveProviderCode, marketCategoryId));
    }

    @Override
    public Response clearCacheMarketCategoryIdByPeriod(Long standardMatchId, Integer period) {
        String linkId = IdWorker.getId() + "_clear_category_period";
        log.info("::{}::手动清除已经自动关盘缓存玩法,标准赛事ID:{},标准玩法ID:{}", linkId, standardMatchId, period);
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            log.info("::{}::clearAutoCloseCategory,对应的标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
            return Response.failed(String.format("::linkId=%s::对应的标准赛事未找到，标准赛事id:%d", linkId, standardMatchId));
        }
        List<MarketCategorySell> marketCategorySells = marketCategorySellService.getItem(standardMatchId, "0");
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            log.info("::{}::clearAutoCloseCategory,滚球开售玩法不存在，标准赛事id:{}", linkId, standardMatchId);
            return Response.failed(String.format("::linkId=%s::滚球开售玩法不存在，标准赛事id:%d", linkId, standardMatchId));
        }
        Set<String> marketCategoryIds = marketCategorySells.stream()
                .filter(sell -> null != sell.getAutoCloseMarket() && period.equals(sell.getAutoCloseMarket()))
                .map(s -> s.getMarketCategoryId().toString()).
                collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(marketCategoryIds)) {
            log.info("::{}::clearAutoCloseCategory,阶段对应的玩法不存在，标准赛事id:{}", linkId, standardMatchId);
            return Response.failed(String.format("::linkId=%s::阶段对应的玩法不存在，标准赛事id:%d", linkId, standardMatchId));
        }
        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchId);
        redisService.hDel(redisKey, marketCategoryIds.toArray());
        return Response.success(String.format("::linkId=%s::缓存清理成功,标准赛事id:%d,标准玩法:%s", linkId,
                standardMatchId, String.join(",", marketCategoryIds)));
    }

    @Override
    public Response checkChangeMTS(Request<TradeCloseOpeartorDTO> request) {
        log.info("::{}::checkChangeMTS入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeCloseOpeartorDTO tradeCloseOpeartorDTO = request.getData();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(tradeCloseOpeartorDTO.getMatchId(),
                tradeCloseOpeartorDTO.getDataSourceCode());
        if (null == thirdMatchInfo) {
            log.info("::{}::checkChangeMTS入参，查询三方赛事为空,赛事id={}", request.getLinkId(),
                    tradeCloseOpeartorDTO.getMatchId());
            return Response.failed("该赛事未与'" + tradeCloseOpeartorDTO.getDataSourceCode() + "'数据源关联，不支持切换");
        }
        if (thirdMatchInfo.getLiveOddSupport().equals(0)) {
            return Response.failed("该'" + tradeCloseOpeartorDTO.getDataSourceCode() + "'三方赛事不支持滚球，不允许切换");
        }
        return Response.success();
    }

    @Override
    public Response checkChangeGTS(Request<TradeCloseOpeartorDTO> request) {
        log.info("::{}::checkChangeGTS入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeCloseOpeartorDTO tradeCloseOpeartorDTO = request.getData();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(tradeCloseOpeartorDTO.getMatchId(),
                tradeCloseOpeartorDTO.getDataSourceCode());
        if (null == thirdMatchInfo) {
            log.info("::{}::checkChangeGTS入参，查询三方赛事为空,赛事id={}", request.getLinkId(),
                    tradeCloseOpeartorDTO.getMatchId());
            return Response.failed("该赛事未与BG数据源关联，不支持切换GTS");
        }
        if (thirdMatchInfo.getLiveOddSupport().equals(0)) {
            return Response.failed("该BG三方赛事不支持滚球，不允许切换GTS");
        }
        if (thirdMatchInfo.getBooked().equals(0)) {
            return Response.failed("该BG三方赛事没有预定，不允许切换MTS");
        }
        return Response.success();
    }

    @Override
    public Response checkChangeOTS(Request<TradeCloseOpeartorDTO> request) {
        log.info("::{}::checkChangeOTS入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeCloseOpeartorDTO tradeCloseOpeartorDTO = request.getData();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(tradeCloseOpeartorDTO.getMatchId(),
                tradeCloseOpeartorDTO.getDataSourceCode());
        if (null == thirdMatchInfo) {
            log.info("::{}::checkChangeOTS入参，查询三方赛事为空,赛事id={}", request.getLinkId(),
                    tradeCloseOpeartorDTO.getMatchId());
            return Response.failed("该赛事未与OD数据源关联，不支持切换OTS");
        }
        if (thirdMatchInfo.getLiveOddSupport().equals(0)) {
            return Response.failed("该OD三方赛事不支持滚球，不允许切换OTS");
        }
        return Response.success();
    }

    @Override
    public Response checkChangeCTS(Request<TradeCloseOpeartorDTO> request) {
        log.info("::{}::checkChangeCTS入参: {}", request.getLinkId(), JSON.toJSONString(request));
        TradeCloseOpeartorDTO tradeCloseOpeartorDTO = request.getData();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(tradeCloseOpeartorDTO.getMatchId(),
                tradeCloseOpeartorDTO.getDataSourceCode());
        if (null == thirdMatchInfo) {
            log.info("::{}::checkChangeCTS入参，查询三方赛事为空,赛事id={}", request.getLinkId(),
                    tradeCloseOpeartorDTO.getMatchId());
            return Response.failed("该赛事未与BC数据源关联，不支持切换CTS");
        }
        if (thirdMatchInfo.getLiveOddSupport().equals(0)) {
            return Response.failed("该BC三方赛事不支持滚球，不允许切换CTS");
        }
        return Response.success();
    }

    @Override
    public Response clearDiffValue(Request<TradeClearDiffValueDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}::clearDiffValue入参: {}", linkId, JSON.toJSONString(request));
        TradeClearDiffValueDTO tradeClearDiffValueDTO = request.getData();
        Long standardMatchId = tradeClearDiffValueDTO.getStandardMatchId();
        List<Long> categoryList = tradeClearDiffValueDTO.getCategoryList();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            log.info("::{}::clearDiffValue入参,标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        if (standardSportMarketSell == null) {
            log.info("::{}::clearDiffValue入参 ,赛事未开售赔率不下发，标准赛事id：{}", linkId, standardMatchId);
            return Response.failed(String.format("::linkId=%s::赛事未开售赔率不下发，标准赛事id:%d", linkId, standardMatchId));
        }
        //清除水差
        delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList, tradeClearDiffValueDTO.getSportId());
        int liveFlag = isOddsLive(standardMatchInfo.getId());
        if (0 == liveFlag && categoryList.size() > 1) {
            log.info("::{}::clearDiffValue入参 ,滚球清理水差玩法大于1，不触发赔率下发，标准赛事id：{}", linkId, standardMatchId);
            return Response.success();
        }
        //下发最新赔率
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                getStringStandardMarketDataMessageMap(new HashSet<Long>(categoryList), request.getLinkId(), standardMatchInfo,
                        standardSportMarketSell);
        //盘口下发
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(), request.getOperaterId(),standardMatchInfo,
                new HashSet<Long>(categoryList), stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                standardSportMarketSell, new HashMap<>());

        return Response.success();
    }

    @Override
    public Response queryAutoCloseMarket(Long standardMatchId) {
        if (null == standardMatchId) {
            return Response.failed("标准赛事不能为空！");
        }
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            return Response.failed("标准赛事未找到！");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        if (standardSportMarketSell == null) {
            return Response.failed(String.format("赛事未开售，标准赛事id:%d", standardMatchId));
        }
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        if (StringUtils.isBlank(liveProviderCode)) {
            return Response.failed(String.format("滚球未设置，标准赛事id:%d", standardMatchId));
        }
        String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchId);
        Map<String, Long> autoCloseMap = redisService.hGetAll(autoCloseRedisKey);
        if (!CollectionUtils.isEmpty(autoCloseMap)) {
            //value 升序
            autoCloseMap = autoCloseMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        }
        JSONObject obj = new JSONObject();
        obj.put("standardMatchId", standardMatchId);
        obj.put("categoryAutoClose", autoCloseMap);
        return Response.success(obj);
    }

    @Override
    public Response editCategoryClearCache(Long marketCategoryId, Long sportId, Integer status) {
        String linkId = IdWorker.getId() + "_c_clear";
        log.info("::{}::editCategoryClearCache,marketCategoryId:{}，sportId:{},status:{}", linkId, marketCategoryId, sportId, status);
        if (sportId != null && sportId.longValue() != 0) {
            //清除玩法赛种表缓存
            String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketCategory:" + marketCategoryId + "-" + sportId;
            Boolean del = redisService.del(key);
            log.info("::{}::editCategoryClearCache, key:{},result:{}", linkId, key, del);
        }
        //判断标准玩法状态&&判断AO玩法状态
        if (null == sportId) {
            if ((null == status || status.equals(Constant.SPORT_MARKET_CATEGORY.STATUS.EFFICIENT))) {
                List<StandardSportMarketCategory> standardSportMarketCategorys = standardSportMarketCategoryService.selectByCategoryId(marketCategoryId);
                if (!CollectionUtils.isEmpty(standardSportMarketCategorys)) {
                    for (StandardSportMarketCategory s : standardSportMarketCategorys) {
                        //清除玩法赛种表缓存
                        String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketCategory:" + marketCategoryId + "-" + s.getSportId();
                        Boolean del = redisService.del(key);
                        log.info("::{}::运动类型不存在清除全部缓存editCategoryClearCache, key:{},result:{}", linkId, key, del);
                    }
                }
                return Response.success(linkId + "玩法缓存清理成功,标准玩法id:" + marketCategoryId + "sportId:" + sportId);
            }
        } else {
            StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryService.getItem(marketCategoryId, sportId);
            if ((null == status || status.equals(Constant.SPORT_MARKET_CATEGORY.STATUS.EFFICIENT))
                    && (null == standardSportMarketCategory || null == standardSportMarketCategory.getAoStatus() || standardSportMarketCategory.getAoStatus().equals(Constant.SPORT_MARKET_CATEGORY.STATUS.EFFICIENT))) {
                return Response.success(linkId + "玩法缓存清理成功,标准玩法id:" + marketCategoryId + "sportId:" + sportId);
            }
            log.info("::{}::editCategoryClearCache,marketCategoryId:{}，sportId:{},status:{},aoStatus:{}", linkId, marketCategoryId, sportId, status, standardSportMarketCategory.getAoStatus());
        }
        //操作各个未结束赛事的玩法盘口进行关盘操作
        processTradeSystemThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sportId != null) {
                        changeCategoryMarketStatus(marketCategoryId, sportId, linkId, status);
                    } else {
                        changeCategoryMarketStatus(marketCategoryId, linkId, status);
                    }

                } catch (Exception e) {
                    log.error("{}::editCategoryClearCache ERROR:", linkId, e);
                }
            }
        });
        return Response.success(linkId + "玩法缓存清理成功,标准玩法id:" + marketCategoryId + "sportId:" + sportId);
    }

    /**
     * 玩法关闭 后 执行关盘
     *
     * @param marketCategoryId
     * @param sportId
     * @param linkId
     */
    private void changeCategoryMarketStatus(Long marketCategoryId, Long sportId, String linkId, Integer status) {
        log.info("::{}::changeCategoryMarketStatus, 操作各个未结束赛事的玩法盘口进行关盘操作 sportId:{},marketCategoryId:{},status:{}", linkId, sportId, marketCategoryId, status);
        StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryService.getItem(marketCategoryId, sportId);
        processMatchMarket(marketCategoryId, sportId, linkId, standardSportMarketCategory, status);
    }

    /**
     * 玩法关闭 后 执行关盘
     *
     * @param marketCategoryId
     * @param linkId
     */
    private void changeCategoryMarketStatus(Long marketCategoryId, String linkId, Integer status) {
        log.info("::{}::changeCategoryMarketStatus, 操作各个未结束赛事的玩法盘口进行关盘操作(多赛种) sportId:{},marketCategoryId:{}", linkId, marketCategoryId);
        List<StandardSportMarketCategory> standardSportMarketCategorys = standardSportMarketCategoryService.selectByCategoryId(marketCategoryId);
        for (StandardSportMarketCategory s : standardSportMarketCategorys) {
            processMatchMarket(marketCategoryId, s.getSportId(), linkId, s, status);
            //清除玩法赛种表缓存
            String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketCategory:" + marketCategoryId + "-" + s.getSportId();
            Boolean del = redisService.del(key);
            log.info("::{}::全部editCategoryClearCache, key:{},result:{}", linkId, key, del);
        }
    }

    /**
     * 关闭玩法后处理盘口数据
     *
     * @param marketCategoryId
     * @param sportId
     * @param linkId
     * @param standardSportMarketCategory
     */
    private void processMatchMarket(Long marketCategoryId, Long sportId, String linkId, StandardSportMarketCategory standardSportMarketCategory, Integer status) {
        if (standardSportMarketCategory == null) {
            log.info("::{}::changeCategoryMarketStatus, 指定赛种下没有绑定玩法 sportId:{},marketCategoryId:{}", linkId, sportId, marketCategoryId);
            return;
        }
        if (Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(status)) {
            List<StandardMatchInfo> tandardMatchInfos = standardMatchInfoService.selectActiveByMarketCategoryIdAndSportId(marketCategoryId, standardSportMarketCategory.getSportId());
            if (!CollectionUtils.isEmpty(tandardMatchInfos)) {
                log.info("::{}::changeCategoryMarketStatus, 指定赛种下玩法有效，执行玩法下盘口关盘逻辑 sportId:{},marketCategoryId:{}", linkId, standardSportMarketCategory.getSportId(), marketCategoryId);
                thirdMatchMarketProcessor.processByRedis(linkId, tandardMatchInfos, marketCategoryId);
            } else {
                log.info("::{}::changeCategoryMarketStatus, 指定赛种玩法没有赛事 sportId:{},marketCategoryId:{}", linkId, standardSportMarketCategory.getSportId(), marketCategoryId);
            }
        } else if (Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getAoStatus())) {
            List<StandardMatchInfo> tandardMatchInfos = standardMatchInfoService.selectActiveByMarketCategoryIdAndSportId(marketCategoryId, standardSportMarketCategory.getSportId());
            if (!CollectionUtils.isEmpty(tandardMatchInfos)) {
                log.info("::{}::changeCategoryMarketStatus-AO, 指定赛种下玩法有效，执行玩法下盘口关盘逻辑 sportId:{},marketCategoryId:{}", linkId, standardSportMarketCategory.getSportId(), marketCategoryId);
                thirdMatchMarketProcessor.processAoByRedis(linkId, tandardMatchInfos, marketCategoryId);
            } else {
                log.info("::{}::changeCategoryMarketStatus-AO, 指定赛种玩法没有赛事 sportId:{},marketCategoryId:{}", linkId, standardSportMarketCategory.getSportId(), marketCategoryId);
            }
        } else {
            log.info("::{}::changeCategoryMarketStatus, 指定赛种下玩法有效，执行玩法下盘口开盘逻辑(忽略) sportId:{},marketCategoryId:{}", linkId, standardSportMarketCategory.getSportId(), marketCategoryId);
        }
    }

    @Override
    public Response updateMarketCategoryDataSourceCode(Request<List<UpdateMarketCategoryDataSourceCodeDTO>> updateMarketCategoryDataSourceCodeRequest) {
        String linkId = updateMarketCategoryDataSourceCodeRequest.getLinkId();
        log.info("::{}::风控修改玩法数据源：{}", linkId, JSON.toJSONString(updateMarketCategoryDataSourceCodeRequest));
        Long operaterId = updateMarketCategoryDataSourceCodeRequest.getOperaterId();
        if (operaterId == null) {
            return Response.failed("当前操作用户id不能为空");
        }
        List<UpdateMarketCategoryDataSourceCodeDTO> marketCategoryDataSourceCodeList =
                updateMarketCategoryDataSourceCodeRequest.getData();
        if (CollectionUtils.isEmpty(marketCategoryDataSourceCodeList)) {
            return Response.failed("修改玩法数据源数据不能为空");
        }

        Long standardMarchId = marketCategoryDataSourceCodeList.get(0).getMatchId();
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMarchId);
        if (standardSportMarketSell == null) {
            return Response.failed("找不到玩法开售信息");
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMarchId);
        if (standardMatchInfo == null) {
            return Response.failed("找不到标准赛事信息");
        }
        Integer marketType = Integer.valueOf(marketCategoryDataSourceCodeList.get(0).getMarketType());
        List<Long> categoryList =
                marketCategoryDataSourceCodeList.stream().map(UpdateMarketCategoryDataSourceCodeDTO::getMarketCategoryId).distinct().collect(Collectors.toList());
        List<MarketCategorySell> marketCategorySellResultList = marketCategorySellService.getItem(standardMarchId, marketType.toString());
        if (CollectionUtils.isEmpty(marketCategorySellResultList)) {
            return Response.failed("有玩法未开售,不能切换数据源");
        }
        Map<String, MarketCategorySell> standardMarketCategorySellMap = marketCategorySellResultList.stream()
                .collect(Collectors.toMap(e -> e.getMatchId() + "_" + e.getMarketType() + "_" + e.getMarketCategoryId(), e -> e, (oldValue, newValue) -> newValue));
        marketCategorySellResultList = marketCategorySellResultList.stream().filter(e -> categoryList.contains(e.getMarketCategoryId()) && e.getSellStatus().equals(SellStatusEnum.SOLD.value)).collect(Collectors.toList());
        if (marketCategorySellResultList.size() != categoryList.size()) {
            return Response.failed("有玩法未开售,不能切换数据源");
        }

        dataSourceSwitchService.switchDataSource(updateMarketCategoryDataSourceCodeRequest,
                                                 marketCategoryDataSourceCodeList,
                                                 standardSportMarketSell,
                                                 standardMarketCategorySellMap,
                                                 marketType,
                                                 categoryList,
                                                 standardMatchInfo);
        return Response.success();
    }

    @Override
    public Response upMatchOddsLiveStatus(Long standardMatchId) {
        String linkId = IdWorker.getId() + "_UPDATE_MATCH_ODDS_LIVE_STATUS";
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            standardMatchInfo = standardMatchInfoService.getItemByMatchManageId(String.valueOf(standardMatchId));
            if (standardMatchInfo == null) {
                return Response.failed(String.format("::linkId=%s::标准赛事未找到，标准赛事ID:%d", linkId, standardMatchId));
            }
        }
        String lockValue = UUIdUtils.getId() + "_" + linkId;
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
        log.info("::{}::oldMarketHandler,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId,
                redisLocKey, lockValue);
        redisService.tryLock(redisLocKey, lockValue, 5, 3);
        log.info("::{}::oldMarketHandler,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId,
                redisLocKey, lockValue);
        try {
            //查询赛事的开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
            if (standardSportMarketSell == null) {
                return Response.failed(String.format("::linkId=%s::赛事未开售，标准赛事id:%d", linkId, standardMatchId));
            }
            String switchLiveRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId();

            //删除是否构建盘口标识
            String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + standardMatchInfo.getId();
            //删除构建盘口缓存
            String redisConvertKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CONVERT_MARKET + standardMatchInfo.getId());
            //step:1 下发滚球数据为关盘
            //获取所有多数据源盘口赔率（包含赛前）
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(new HashSet<>(), linkId, standardMatchInfo, standardSportMarketSell);
            //只取滚球数据
            Map<String, StandardMarketDataMessage> standardMarketLiveDataMessageMap = stringStandardMarketDataMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            List<String> dels = new ArrayList<>();
            dels.add(key);
            dels.add(redisConvertKey);
            dels.add(switchLiveRedisKey);
            redisService.del(dels);
            //step:2 通知风控业务修改滚球标识
            switchStatusProducer.standardMatchSwitchStatus(linkId, standardMatchInfo.getId(), Constant.NOT_ODDS_LIVE, standardMatchInfo.getDataSourceCode(), standardMatchInfo.getSportId(), true,0);
            Set<Long> marketCategoryIds = new HashSet<>();
            for (StandardMarketDataMessage standardMarketDataMessage : standardMarketLiveDataMessageMap.values()) {
                marketCategoryIds.add(standardMarketDataMessage.getMarketCategoryId());
                //根据滚球数据源写入缓存
                String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
                standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                //这里是唯一改变数据源状态的地方
                standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                standardMarketDataMessage.setLinkId(linkId);
                standardMarketDataMessage.setRemark("切换滚球标识关盘");
                standardMarketDataMessage.setMarketSource(0);
                log.info("::{}:upMatchOddsLiveStatus，切换滚球标识关盘准备缓存赔率信息，relationMarketId={}, standardMarketDataMessage={}",
                        linkId, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage);
                redisService.del(redisKey);
            }
            thirdMatchMarketProcessor.processOddsByAll(linkId,-1,null, standardMatchInfo, marketCategoryIds, standardMarketLiveDataMessageMap, TimeUtils.millsSecondsEast8ZoneGmt(), standardSportMarketSell, new HashMap<>());
        } catch (Exception e) {
            log.info("::{}::切换滚球标识关盘失败", linkId, e);
            return Response.failed(String.format("::linkId=%s::切换滚球标识关盘失败，标准赛事id:%d,错误信息:%s", linkId, standardMatchId, e));
        }finally {
            redisService.unLock(redisLocKey, lockValue);
        }
        return Response.success(true, String.format("::linkId=%s::切换成功，标准赛事id:%d", linkId, standardMatchId));
    }
    @Override
    public Response upMatchBeginTimesOddsLiveStatus(Long standardMatchId,Long beginTime,Long oldBeginTime) {
        String linkId = IdWorker.getId() + "_UPDATE_MATCH_ODDS_LIVE_STATUS";
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            standardMatchInfo = standardMatchInfoService.getItemByMatchManageId(String.valueOf(standardMatchId));
            if (standardMatchInfo == null) {
                return Response.failed(String.format("::linkId=%s::标准赛事未找到，标准赛事ID:%d", linkId, standardMatchId));
            }
        }
        String lockValue = UUIdUtils.getId() + "_" + linkId;
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
        log.info("::{}::oldMarketHandler,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId,
                redisLocKey, lockValue);
        redisService.tryLock(redisLocKey, lockValue, 5, 3);
        log.info("::{}::oldMarketHandler,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId,
                redisLocKey, lockValue);
        try {
            //查询赛事的开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
            if (standardSportMarketSell == null) {
                return Response.failed(String.format("::linkId=%s::赛事未开售，标准赛事id:%d", linkId, standardMatchId));
            }
            //修改开赛时间，需要刷早盘玩法开售缓存，以及标准赔率缓存过期时间
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
            redisService.expire(categoryRedisKey,marketCacheTime(beginTime));
            //修改缓存开赛时间
            String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
            String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
            redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), beginTime,Integer.MAX_VALUE);

            String switchLiveRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId();
            //bug:74932
            Long nowTime = Calendar.getInstance().getTimeInMillis();
            // 88540
            //大于一小时会回退
            //小于一小时不回退
            int oddsLive = thirdMatchMarketProcessor.isOddsLive(standardMatchInfo.getId());
            if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode())){
                if (oddsLive == 1 || beginTime < (oldBeginTime +60*60*1000L)  || beginTime < nowTime) {
                    log.info("::{}::upMatchBeginTimesOddsLiveStatus参数:oddsLive={}, beginTime={}赛事已开盘，不需要切换滚球标识,matchId:{}", linkId,thirdMatchMarketProcessor.isOddsLive(standardMatchInfo.getId()),beginTime,standardMatchInfo.getId());
                    return Response.success(true, String.format("::linkId=%s::切换成功，标准赛事id:%d", linkId, standardMatchId));
                }
            }else {
                if (oddsLive == 1 || (oddsLive == 0 && beginTime < (oldBeginTime +15*60*1000L)) || beginTime < nowTime) {
                    log.info("::{}::upMatchBeginTimesOddsLiveStatus参数:oddsLive={}, beginTime={}赛事已开盘，不需要切换滚球标识,matchId:{}", linkId,thirdMatchMarketProcessor.isOddsLive(standardMatchInfo.getId()),beginTime,standardMatchInfo.getId());
                    return Response.success(true, String.format("::linkId=%s::切换成功，标准赛事id:%d", linkId, standardMatchId));
                }
            }


            //删除是否构建盘口标识
            String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + standardMatchInfo.getId();
            //删除构建盘口缓存
            String redisConvertKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CONVERT_MARKET + standardMatchInfo.getId());
            //step:1 下发滚球数据为关盘
            //获取所有多数据源盘口赔率（包含赛前）
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(new HashSet<>(), linkId, standardMatchInfo, standardSportMarketSell);
            //只取滚球数据
            Map<String, StandardMarketDataMessage> standardMarketLiveDataMessageMap = stringStandardMarketDataMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            List<String> dels = new ArrayList<>();
            dels.add(key);
            dels.add(redisConvertKey);
            dels.add(switchLiveRedisKey);
            redisService.del(dels);
            //step:2 通知风控业务修改滚球标识
            switchStatusProducer.standardMatchSwitchStatus(linkId, standardMatchInfo.getId(), Constant.NOT_ODDS_LIVE, standardMatchInfo.getDataSourceCode(), standardMatchInfo.getSportId(), true, 0);
            Set<Long> marketCategoryIds = new HashSet<>();
            for (StandardMarketDataMessage standardMarketDataMessage : standardMarketLiveDataMessageMap.values()) {
                marketCategoryIds.add(standardMarketDataMessage.getMarketCategoryId());
                //根据滚球数据源写入缓存
                String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
                standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                //这里是唯一改变数据源状态的地方
                standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                standardMarketDataMessage.setLinkId(linkId);
                standardMarketDataMessage.setRemark("切换滚球标识关盘");
                standardMarketDataMessage.setMarketSource(0);
                log.info("::{}:upMatchBeginTimesOddsLiveStatus，切换滚球标识关盘准备缓存赔率信息，relationMarketId={}, standardMarketDataMessage={}",
                        linkId, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage);
                redisService.del(redisKey);
            }
            thirdMatchMarketProcessor.processOddsByAll(linkId,-1, null,standardMatchInfo, marketCategoryIds, standardMarketLiveDataMessageMap, TimeUtils.millsSecondsEast8ZoneGmt(), standardSportMarketSell, new HashMap<>());
        } catch (Exception e) {
            log.info("::{}:: upMatchBeginTimesOddsLiveStatus 切换滚球标识关盘失败", linkId, e);
            return Response.failed(String.format("::linkId=%s::upMatchBeginTimesOddsLiveStatus 切换滚球标识关盘失败，标准赛事id:%d,错误信息:%s", linkId, standardMatchId, e));
        }finally {
            redisService.unLock(redisLocKey, lockValue);
        }
        return Response.success(true, String.format("::linkId=%s:: upMatchBeginTimesOddsLiveStatus 切换成功，标准赛事id:%d", linkId, standardMatchId));
    }

    @Override
    public Response putTradeMarketMarginGapConfig(Request<TradeMarketMarginGapConfigDTO> request) {
        //validateLinkId("putTradeMarketMarginGapConfig", request);
        String linkId = request.getLinkId();
        log.info("::{}::putTradeMarketMarginGapConfig入参: {}", linkId, JSON.toJSONString(request));
        TradeMarketMarginGapConfigDTO tradeMarketHeadGapConfigDTO = request.getData();
        Long operaterId = request.getOperaterId();
        Long standardMatchInfoId = tradeMarketHeadGapConfigDTO.getStandardMatchInfoId();
        Long standardCategoryId = tradeMarketHeadGapConfigDTO.getStandardCategoryId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeMarketMarginGapConfig,标准赛事未找到，标准赛事id:{}", linkId, standardMatchInfoId);
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfoId);
        if (standardSportMarketSell == null) {
            return Response.failed(String.format("::linkId=%s::赛事未开售，标准赛事id:%d", linkId, standardMatchInfoId));
        }
        if (ObjectUtils.isEmpty(tradeMarketHeadGapConfigDTO.getChildStandardCategoryId())) {
            tradeMarketHeadGapConfigDTO.setChildStandardCategoryId(tradeMarketHeadGapConfigDTO.getStandardCategoryId());
        }
        List<ConfigMarketMarginGap> createList = new ArrayList<>();
        List<ConfigMarketMarginGap> updateList = new ArrayList<>();
        List<ConfigMarketMarginGapLog> logList = new ArrayList<>();
        for (MarketMarginGapDtlDTO itemDto : tradeMarketHeadGapConfigDTO.getList()) {
            ConfigMarketMarginGap marketMargin = new ConfigMarketMarginGap();
            ConfigMarketMarginGapLog log = new ConfigMarketMarginGapLog();
            //查询配置
            ConfigMarketMarginGap configMarketMarginGap = configMarketMarginGapService.getItem(standardMatchInfoId, standardCategoryId, tradeMarketHeadGapConfigDTO.getChildStandardCategoryId(), itemDto.getOddsType(), tradeMarketHeadGapConfigDTO.getPlaceNum());
            if (null == configMarketMarginGap) {
                marketMargin.setMatchId(tradeMarketHeadGapConfigDTO.getStandardMatchInfoId());
                marketMargin.setMarketCategoryId(tradeMarketHeadGapConfigDTO.getStandardCategoryId());
                marketMargin.setLinkageMode(tradeMarketHeadGapConfigDTO.getLinkageMode());
                BeanUtils.copyProperties(itemDto, marketMargin);
                marketMargin.setChildStandardCategoryId(tradeMarketHeadGapConfigDTO.getChildStandardCategoryId());
                marketMargin.setId(IdWorker.getId());
                marketMargin.setOperaterId(operaterId);
                marketMargin.setPlaceNum(tradeMarketHeadGapConfigDTO.getPlaceNum());
                marketMargin.setLinkId(linkId);
                createList.add(marketMargin);
            } else {
                marketMargin.setMatchId(tradeMarketHeadGapConfigDTO.getStandardMatchInfoId());
                marketMargin.setMarketCategoryId(tradeMarketHeadGapConfigDTO.getStandardCategoryId());
                marketMargin.setLinkageMode(tradeMarketHeadGapConfigDTO.getLinkageMode());
                BeanUtils.copyProperties(itemDto, marketMargin);
                marketMargin.setChildStandardCategoryId(tradeMarketHeadGapConfigDTO.getChildStandardCategoryId());
                marketMargin.setId(configMarketMarginGap.getId());
                marketMargin.setOperaterId(operaterId);
                marketMargin.setPlaceNum(tradeMarketHeadGapConfigDTO.getPlaceNum());
                marketMargin.setLinkId(linkId);
                //修改风控给null，不修改之前配置
                if (null == itemDto.getProbability()){
                    marketMargin.setProbability(configMarketMarginGap.getProbability());
                }
                if (null == itemDto.getDiffValue()){
                    marketMargin.setDiffValue(configMarketMarginGap.getDiffValue());
                }
                updateList.add(marketMargin);
            }
            BeanUtils.copyProperties(marketMargin, log);
            log.setId(IdWorker.getId());
            logList.add(log);
        }
        if (!CollectionUtils.isEmpty(logList)) {
            configMarketMarginGapLogService.createList(logList);
        }
        if (!CollectionUtils.isEmpty(createList)) {
            configMarketMarginGapService.insertList(linkId, standardMatchInfoId, createList);
        }
        if (!CollectionUtils.isEmpty(updateList)) {
            configMarketMarginGapService.updateList(linkId, standardMatchInfoId, updateList);
        }
        Set<Long> marketCategoryIds = new HashSet<>();
        marketCategoryIds.add(tradeMarketHeadGapConfigDTO.getStandardCategoryId());
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(
                marketCategoryIds, request.getLinkId(), standardMatchInfo, standardSportMarketSell);
        //盘口下发
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketCategoryIds, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Map<Long, List<Long>> getCategoryByMatchId(List<Long> matchIds) {
        Map<Long, List<Long>> result = new HashMap<>();
        matchIds.forEach(matchId -> {
            String redisKey = Constant.REDIS_KEY.RONGHE_ORDER_STANDARD_MARKET + matchId;
            Map<String, Object> categories = redisService.hGetAll(redisKey);
            if (MapUtils.isNotEmpty(categories)) {
                Set<Long> keys = categories.keySet().stream().map(Long::parseLong).collect(Collectors.toSet());
                result.put(matchId, new ArrayList<>(keys));
            }
        });
        return result;
    }

    @Override
    public Response putConfigTournamentTradeItem(Request<ConfigTournamentTradeItemDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}::联赛维度最大最小赔率配置:{}", linkId, JSONObject.toJSONString(request));
        ConfigTournamentTradeItemDTO dto = request.getData();
        Long sportId = dto.getSportId();
        Long tournamentId = dto.getTournamentId();
        Integer matchType = dto.getMatchType();
        Long operaterId = request.getOperaterId();
        dto.setOperaterId(operaterId);
        ConfigTournamentTradeItem configTournamentTradeItem = configTournamentTradeItemService.getItem(sportId, tournamentId, matchType);
        if (configTournamentTradeItem == null) {
            configTournamentTradeItemService.create(dto);
        } else {
            configTournamentTradeItemService.update(configTournamentTradeItem, dto);
        }
        return Response.success();
    }


    @Override
    public Response putCategoryStatusConfig(Request<PlaySetStatusConfigDTO> request) {
        String linkId = request.getLinkId();
        log.info("::{}:: putCategoryStatusConfig 入参:{}", linkId, JSONObject.toJSONString(request));
        PlaySetStatusConfigDTO playSetStatusConfigDTO = request.getData();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(playSetStatusConfigDTO.getMatchId());
        if (standardMatchInfo == null) {
            log.info("::{}::putCategoryStatusConfig,标准赛事未找到，标准赛事id:{}", linkId, playSetStatusConfigDTO.getMatchId());
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
        if (standardSportMarketSell == null) {
            return Response.failed(String.format("::linkId=%s::赛事未开售，标准赛事id:%d", linkId, standardMatchInfo.getId()));
        }
        Map<String, Integer> categoryStatusMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(playSetStatusConfigDTO.getPlayIds())) {
            playSetStatusConfigDTO.getPlayIds().forEach(e -> {
                categoryStatusMap.put(String.valueOf(e), playSetStatusConfigDTO.getStatus());
            });
            String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_SET_STATUS + standardMatchInfo.getId());
            //每次请求都需要删除上一次玩法集玩法 ，以最新的为准
            String categoryKeyOld = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_SETCODE_CACHE + standardMatchInfo.getId() + "_" + playSetStatusConfigDTO.getPlaySetCode();
            Object obj = redisService.get(categoryKeyOld);
            log.info("::{}::每次请求都需要删除上一次玩法集:{},玩法:{}", linkId, categoryKeyOld, obj);
            if (obj != null) {
                List<Long> categoryListOld = (List<Long>) obj;
                redisService.hDel(redisKey, categoryListOld.toArray());
            }
            //缓存最新玩法集玩法
            redisService.set(categoryKeyOld, playSetStatusConfigDTO.getPlayIds(), marketCacheTime(standardMatchInfo.getBeginTime()));
            log.info("::{}::每次请求都需要删除上一次玩法集:{},玩法:{},缓存后", linkId, categoryKeyOld, redisService.get(categoryKeyOld));
            log.info("::{}::缓存最新玩法状态:{},玩法:{},缓存前", linkId, redisKey, redisService.hGetAll(redisKey));
            //缓存最新玩法
            redisService.hSetAll(redisKey, categoryStatusMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            log.info("::{}::缓存最新玩法状态:{},玩法:{},缓存后", linkId, redisKey, redisService.hGetAll(redisKey));
            //玩法级状态下发风控
            standardCategorySetStatusMessageProducer.sendStandardCategorySetStatus(linkId, standardMatchInfo);
            //模式判断
            Set<Long> changeCategoryIdSet = new HashSet<>();
            Map<String, ConfigTradeType> configTradeTypesMap = configTradeTypeService.getItemMatchDB(standardMatchInfo.getId());

            for (Long playId : playSetStatusConfigDTO.getPlayIds()) {
                ConfigTradeType configTradeType = configTradeTypesMap.get(playId.toString());
                Integer tradeTypeDB = 0;
                if (null != configTradeType) {
                    tradeTypeDB = configTradeType.getTradeType();
                }
                if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeDB)) {
                    log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", linkId, standardMatchInfo.getId(), playId);
                    continue;
                }
                changeCategoryIdSet.add(playId);
            }
            //玩法分批下发
           /* AtomicInteger num = new AtomicInteger();
            ListUtils.partition(changeCategoryIdSet, 10).forEach(t -> {
                thirdMatchMarketProcessor.processByRedis(linkId + "_" + num, standardMatchInfo, new HashSet<>(t), request.getDataSourceTime());
                num.getAndIncrement();
            });*/

            //获取缓存中的所有盘口（赛前数据商和滚球数据商）
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(changeCategoryIdSet, request.getLinkId(), standardMatchInfo, standardSportMarketSell);
            thirdMatchMarketProcessor.processOddsByAll(linkId,request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                    changeCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                    standardSportMarketSell, new HashMap<>());
            log.info("::{}:: putCategoryStatusConfig 处理完成", linkId);
        }
        return Response.success();
    }

    @Override
    public Response putTradeTypeConfig(Request<UpdateTradeTypeDTO> request) {
        log.info("::{}::putTradeTypeConfig 入参：{}", request.getLinkId(), JSON.toJSONString(request));
        UpdateTradeTypeDTO updateTradeTypeDTO = request.getData();
        Long standardMatchInfoId = updateTradeTypeDTO.getMatchId();
        Long operaterId = request.getOperaterId();
        String linkId = request.getLinkId();
        Integer tradeType = updateTradeTypeDTO.getTradeType();
        List<Long> playIds = updateTradeTypeDTO.getPlayIds();
        List<Long> childCategoryIds = updateTradeTypeDTO.getChildCategoryIds();
        List<MarketPlaceDtlDTO> marketPlaceDtlDTOS = updateTradeTypeDTO.getPlaceNumStatusList();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo == null) {
            log.info("::{}::putTradeTypeConfig,标准赛事未找到，标准赛事id:{}", linkId, standardMatchInfoId);
            return Response.failed("标准赛事未找到");
        }
        //查询赛事的开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.getItem(standardMatchInfo.getId());
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::putTradeTypeConfig ,赛事未开售赔率不下发，标准赛事id：{}", request.getLinkId(),
                    standardMatchInfo.getId());
            return Response.failed("赛事未开售赔率不下发");
        }

        log.info("::{}::putTradeTypeConfig,玩法切换操盘方式。操盘类型:{}, 标准赛事id:{}，categoryId:{},childCategoryIds:{}",
                request.getLinkId(), tradeType, standardMatchInfoId, playIds, childCategoryIds);

        Set<Long> marketCategoryIdSet = new HashSet<>();
        List<ConfigMarketCategoryPlace> categoryPlaceList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(marketPlaceDtlDTOS)) {
            //循环处理配置数据
            marketPlaceDtlDTOS.forEach(marketPlaceDtlDTO -> {
                if (marketPlaceDtlDTO.getChildStandardCategoryId() == null) {
                    marketPlaceDtlDTO.setChildStandardCategoryId(marketPlaceDtlDTO.getStandardCategoryId());
                }
                if (marketPlaceDtlDTO.getPlaceNum() == -1) {
                    //缓存盘口位置
                    for (int i = 1; i < 11; i++) {
                        ConfigMarketCategoryPlace categoryPlace = new ConfigMarketCategoryPlace();
                        categoryPlace.setId(UUIdUtils.getId());
                        categoryPlace.setLinkId(linkId);
                        categoryPlace.setStandardMatchInfoId(standardMatchInfoId);
                        categoryPlace.setPlaceNum(i);
                        categoryPlace.setStandardCategoryId(marketPlaceDtlDTO.getStandardCategoryId());
                        categoryPlace.setChildStandardCategoryId(marketPlaceDtlDTO.getChildStandardCategoryId());
                        categoryPlace.setPlaceNumStatus(marketPlaceDtlDTO.getPlaceNumStatus());
                        categoryPlace.setOperaterId(operaterId);
                        categoryPlace.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        categoryPlace.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        categoryPlaceList.add(categoryPlace);
                    }
                } else {
                    ConfigMarketCategoryPlace categoryPlace = new ConfigMarketCategoryPlace();
                    BeanUtils.copyProperties(marketPlaceDtlDTO, categoryPlace);
                    categoryPlace.setId(UUIdUtils.getId());
                    categoryPlace.setLinkId(linkId);
                    categoryPlace.setStandardMatchInfoId(standardMatchInfoId);
                    categoryPlace.setOperaterId(operaterId);
                    categoryPlace.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    categoryPlace.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    categoryPlaceList.add(categoryPlace);
                }
                marketCategoryIdSet.add(marketPlaceDtlDTO.getStandardCategoryId());
            });
        }

        if (!CollectionUtils.isEmpty(categoryPlaceList)) {
            configMarketCategoryPlaceService.cacheConfigMarketPlace(categoryPlaceList, linkId, standardMatchInfoId);
        }
        if (!CollectionUtils.isEmpty(playIds)) {
            //缓存子玩法支持切换模式的id
            String childMarketCategoryIdKey = Constant.REDIS_KEY.RONGHE_CHILD_MARKET_CATEGORY_ID + standardMatchInfoId;
            if (!CollectionUtils.isEmpty(childCategoryIds)) {
                Map<String, String> childMarketCategoryIds = new HashMap<>();
                childCategoryIds.forEach(childId -> {
                    childMarketCategoryIds.put(childId.toString(), childId.toString());
                });
                redisService.hSetAll(childMarketCategoryIdKey, childMarketCategoryIds, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
            //子玩法存在数据以子玩法入库
            List<Long> newPlayIds = CollectionUtils.isEmpty(childCategoryIds) ? playIds : childCategoryIds;
            List<ConfigTradeType> insertList = new ArrayList<>();
            List<ConfigTradeType> updateList = new ArrayList<>();
            log.info("::{}::putTradeTypeConfig,开始处理玩法级ConfigTradeType入库或修改,玩法数量:{}", linkId, newPlayIds.size());
            Set<String> categoryIdSet = new HashSet<>();
            for (Long categoryId : newPlayIds) {
                ConfigTradeType itemCategory =
                        configTradeTypeService.getItemCategory(String.valueOf(standardMatchInfoId), String.valueOf(categoryId));
                Integer oldTradeType = null;
                if (itemCategory == null) {
                    ConfigTradeType configTradeType = new ConfigTradeType();
                    configTradeType.setId(UUIdUtils.getId());
                    configTradeType.setLevel(Constant.TRADE_MARKET_CONFIG.LEVEL.MARKET_CATEGORY);
                    configTradeType.setStandardMatchId(String.valueOf(standardMatchInfoId));
                    configTradeType.setTradeType(tradeType);
                    configTradeType.setStandardCategoryId(String.valueOf(categoryId));
                    configTradeType.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    configTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    configTradeType.setOperaterId(operaterId);
                    insertList.add(configTradeType);
                } else {
                    oldTradeType = itemCategory.getTradeType();
                    itemCategory.setTradeType(tradeType);
                    itemCategory.setOperaterId(operaterId);
                    updateList.add(itemCategory);
                }
                // 最开始为A模式，但是没有ConfigTradeType记录
                if (Objects.isNull(itemCategory) && tradeType != 0) {
                    categoryIdSet.add(categoryId.toString());
                }
                // 从A切换非A模式
                if (Objects.nonNull(itemCategory)
                        && Objects.nonNull(oldTradeType)
                        && oldTradeType == 0
                        && tradeType != 0) {
                    categoryIdSet.add(categoryId.toString());
                }
            }
            if (!CollectionUtils.isEmpty(categoryIdSet)) {
                // 删除玩法对应的缓存
                thirdMatchMarketProcessor.delCategoryCloseCache(linkId, standardMatchInfo.getId(), categoryIdSet);
            }
            if (!CollectionUtils.isEmpty(insertList)) {
                log.info("::{}::putTradeTypeConfig,批量新增玩法级ConfigTradeType记录数:{}", linkId, insertList.size());
                configTradeTypeService.saveBatch(linkId, standardMatchInfoId, insertList);
            }
            if (!CollectionUtils.isEmpty(updateList)) {
                log.info("::{}::putTradeTypeConfig,批量修改玩法级ConfigTradeType记录数:{}", linkId, updateList.size());
                long modifyTime = TimeUtils.millsSecondsEast8ZoneGmt();
                updateList.forEach(e -> e.setModifyTime(modifyTime));
                // 根据条件批量更新数据
                ConfigTradeType updateConfigTradeType = new ConfigTradeType();
                updateConfigTradeType.setTradeType(tradeType);
                updateConfigTradeType.setModifyTime(modifyTime);
                updateConfigTradeType.setOperaterId(operaterId);
                configTradeTypeService.updateByExample(standardMatchInfoId, updateList, updateConfigTradeType);
            }
            marketCategoryIdSet.addAll(playIds);
        }
        log.info("::{}::putTradeTypeConfig,玩法级ConfigTradeType入库或修改处理结束", linkId);
        //---------------处理盘口计算和排序-----------------
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(marketCategoryIdSet, request.getLinkId(), standardMatchInfo, standardSportMarketSell);
        thirdMatchMarketProcessor.processOddsByAll(linkId,request.getOddsSource(),request.getOperaterId(), standardMatchInfo,
                marketCategoryIdSet, stringStandardMarketDataMessageMap, request.getDataSourceTime(),
                standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    /**
     * 1852 进球事件触发强转过的盘口 进行关盘处理
     *
     * @param linkId
     * @param standardMatchId
     * @param dataSourceTime
     */
    public void autoCloseOldMarket(String linkId, Long standardMatchId, Long dataSourceTime) {
        String lockValue = UUIdUtils.getId() + "_" + linkId;
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchId;
        log.info("::{}:: 进球事件触发强转过的盘口,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        boolean isLock = redisService.tryLock(redisLocKey, lockValue, 1, 1);
        log.info("::{}:: 进球事件触发强转过的盘口,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        try {
            log.info("::{}::autoCloseOldMarket标准赛事id:{}", linkId, standardMatchId);
            //查询标准赛事是否存在
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo == null) {
                log.info("::{}::autoCloseOldMarket,对应的标准赛事未找到，标准赛事id:{}", linkId, standardMatchId);
                return;
            }
            if (!MarginCategoryConfig.NO_CLOS_SPORT.contains(standardMatchInfo.getSportId())) {
                log.info("::{}::autoCloseOldMarket,对应的标准赛事的赛种不匹配，标准赛事id:{}", linkId, standardMatchId);
                return;
            }
            //查询赛事的开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
            //赛事未开售，赔率不下发
            if (standardSportMarketSell == null) {
                log.info("::{}::autoCloseOldMarket ,赛事未开售赔率不下发，标准赛事id：{}", linkId, standardMatchId);
                return;
            }
            //未设置滚球数据服务商不下发
            String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
            if (StringUtils.isBlank(liveProviderCode)) {
                log.info("::{}::autoCloseOldMarket ,赛事的开售未设置滚球操盘,标准赛事id：{}", linkId, standardMatchId);
                return;
            }
            //获取需要关闭的盘口
            String closeKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME + standardMatchInfo.getId();
            Map<String, Long> closeMarket = redisService.hGetAll(closeKey);
            log.info("::{}::autoCloseOldMarket ,标准赛事id:{},进球事件需要处理的盘口:{},", linkId, standardMatchId, closeMarket);
            //获取所有多数据源盘口赔率（包含赛前）
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(new HashSet<>(), linkId, standardMatchInfo, standardSportMarketSell);
            //只取滚球数据
            Map<String, StandardMarketDataMessage> standardMarketLiveDataMessageMap = stringStandardMarketDataMessageMap.values().stream()
                    .filter(e -> e.getMarketType() != null && e.getMarketType().equals(0) &&
                            (closeMarket.containsKey(e.getMarketCategoryId() + "_" + e.getRelationMarketId() + "_" + e.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.DEACTIVATED) ||
                                    closeMarket.containsKey(e.getMarketCategoryId() + "_" + e.getRelationMarketId() + "_" + e.getDataSourceCode() + "_" + Constant.SPORT_MARKET.STATUS.SUSPENDED)))
                    .collect(Collectors.toMap(e -> e.getRelationMarketId().toString(), e -> e, (oldValue, newValue) -> newValue));
            Set<Long> categoryIds = new HashSet<Long>();
            Set<StandardMarketDataMessage> closeMarketSet = new HashSet<StandardMarketDataMessage>();
            for (StandardMarketDataMessage standardMarketDataMessage : standardMarketLiveDataMessageMap.values()) {
                String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
                //只修改有改变的玩法 状态改为关盘
                Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
                if (standardMarketDataMessage.getOldThirdMarketSourceStatus() != null &&
                        standardMarketDataMessage.getOldThirdMarketSourceStatus() == Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED) {
                    standardMarketDataMessage.setAutoCloseStatus(Constant.AOTU_CLOSE_STATUS);
                    standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                    standardMarketDataMessage.setEndEdStatus(1);
                    //这里是唯一改变数据源状态的地方
                    standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                    standardMarketDataMessage.setLinkId(linkId);
                    standardMarketDataMessage.setRemark("进球事件触发强转过的盘口 进行关盘处理");
                    log.info("::{}:autoCloseOldMarket，进球事件触发强转过的盘口 进行关盘处理缓存赔率信息，relationMarketId={}",
                            linkId, standardMarketDataMessage.getRelationMarketId().toString());
                    boolean flag = redisService.hSet(redisKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                    categoryIds.add(marketCategoryId);
                    closeMarketSet.add(standardMarketDataMessage);
                    if (!flag) {
                        log.error("::{}::autoCloseOldMarket，标准赛事id:{},relationMarketId={},刷入缓存失败,赔率处理异常",
                                linkId, standardMatchInfo.getId(), standardMarketDataMessage.getRelationMarketId());
                    }
                }
            }
            //盘口推送
            thirdMatchMarketProcessor.processOddsByAll(linkId,-1,null, standardMatchInfo, categoryIds, standardMarketLiveDataMessageMap, dataSourceTime, standardSportMarketSell, new HashMap<>());
            //清除掉100s 盘口记时
            thirdSportMarketOddsService.deleteMatchMarketOddsOfRedis(linkId, standardMatchInfo.getId(), closeMarketSet, dataSourceTime);
            configMatchStatusService.saveDeaMarketOfRedis(linkId, standardMatchInfo.getId(), closeMarketSet, standardMatchInfo.getBeginTime());

        } finally {
            redisService.unLock(redisLocKey, lockValue);
            log.info("::{}:: autoCloseOldMarket 进球事件触发强转过的盘口 进行关盘处理,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        }
    }

    @Override
    public Response<Boolean> emergencyOperationOfMarket(Long matchId, Integer marketStatus) {
        try {
            log.info("emergencyOperationOfMarket入参,赛事id:{},赛事级别盘口状态:{}", matchId, marketStatus);
            if (Objects.isNull(marketStatus)) {
                return Response.failed("赛事级别盘口状态不能为空");
            }
            String linkId = IdWorker.getId() + "_emergency_operate_market";
            log.info("::{}::手动赛事级别开关盘操作,赛事ID:{}, 赛事级别盘口状态:{}", linkId, matchId, marketStatus);

            // 先使用标准赛事id查询 未查询到则使用管理id查询，
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
            if (Objects.isNull(standardMatchInfo)) {
                log.info("::{}::使用标准赛事id的方式未获取到赛事信息，id:{}", linkId, matchId);
                standardMatchInfo = standardMatchInfoService.getItemByMatchManageId(String.valueOf(matchId));
                if (Objects.isNull(standardMatchInfo)) {
                    return Response.failed(String.format("标准赛事未找到，赛事ID:%d", matchId));
                }
            }

            // 本次修改的标准赛事
            StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
            upStandardMatchInfo.setId(standardMatchInfo.getId());
            // 修改标准赛事的操盘赛事状态
            upStandardMatchInfo.setOperateMatchStatus(marketStatus);
            // 修改标准赛事
            standardMatchInfo = standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);

            long dataSourceTime = System.currentTimeMillis();
            // bug 47948 : 操盘赛事级别 开关封锁，单独下发topic，以免赔率topic堆积，导致赛事状态不能及时更新
            standardMarketOddsProducer.standardMarketOddsStateSend(linkId, standardMatchInfo, dataSourceTime);

            if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(marketStatus)) {
                // 下发赛事级别关盘给风控
                dataMerchantBaffleProducer.changeMatchStatusSendRiskMQ(linkId, standardMatchInfo.getId(),
                        Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                // 下发赛事状态给到业务
                standardMarketOddsProducer.standardMarketOddsAsyncSend(linkId, standardMatchInfo,
                        null, dataSourceTime, false);
                log.info("::{}::emergencyOperationOfMarket,标准赛事id：{},赛事级别关盘", linkId, standardMatchInfo.getId());
                return Response.success(Boolean.TRUE, "关盘操作成功");
            } else if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE.equals(marketStatus)) {
                //查询赛事的开售信息
                StandardSportMarketSell standardSportMarketSell =
                        standardSportMarketSellService.getItem(standardMatchInfo.getId());
                //赛事未开售，赔率不下发
                if (standardSportMarketSell == null) {
                    log.info("::{}::emergencyOperationOfMarket,赛事未开售赔率不下发，标准赛事id：{}", linkId,
                            standardMatchInfo.getId());
                    return Response.failed("赛事未开售赔率不下发");
                }
                // 下发赛事级别开状态给风控
                dataMerchantBaffleProducer.changeMatchStatusSendRiskMQ(linkId, standardMatchInfo.getId(),
                        Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE);
                // 获取缓存中的所有盘口（赛前数据商和滚球数据商）
                Map<String, StandardMarketDataMessage> marketDataMessageMap =
                        getStringStandardMarketDataMessageMap(new HashSet<>(), linkId, standardMatchInfo, standardSportMarketSell);
                log.info("::{}::emergencyOperationOfMarket,标准赛事id：{},获取缓存数据总数：{}", linkId,
                        standardMatchInfo.getId(), marketDataMessageMap.size());
                // 获取数据里面的玩法
                Set<Long> marketCategoryIdSet = new HashSet<>();
                marketDataMessageMap.values().forEach(standardMarketDataMessage ->
                        marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId()));
                // 赛事级别开，下发当前最新赔率
                log.info("::{}::emergencyOperationOfMarket,标准赛事id：{},marketCategoryIdSet玩法数量：{}", linkId,
                        standardMatchInfo.getId(), marketCategoryIdSet.size());
                thirdMatchMarketProcessor.processOddsByAll(linkId,-1,null, standardMatchInfo, marketCategoryIdSet,
                        marketDataMessageMap, dataSourceTime, standardSportMarketSell, new HashMap<>());
                return Response.success(Boolean.TRUE, "开盘操作成功");
            } else {
                log.info("::{}::emergencyOperationOfMarket, 盘口状态类型{}不做处理", linkId, marketStatus);
                return Response.failed(String.format("操作失败，不支持操作的盘口状态类型，赛事ID:%d,错误信息:%d", matchId, marketStatus));
            }
        } catch (Exception e) {
            log.error("赛事级别开关盘紧急操作发生异常,赛事id:{}", matchId, e);
            return Response.failed(String.format("赛事级别开关盘紧急操作发生异常，赛事ID:%d,错误信息:%s", matchId, e));
        }
    }

    @Override
    public Response putDiscountOddsConfig(Request<List<DiscountOddsConfigDTO>> requests) {
        String linkId = requests.getLinkId();
        log.info("::{}::putDiscountOddsConfig,请求参数：{}", linkId, JSON.toJSONString(requests.getData()));
        List<DiscountOddsConfigDTO> discountOddsConfigDTOList = requests.getData();
        if (CollectionUtils.isEmpty(discountOddsConfigDTOList)) {
            return Response.failed("putDiscountOddsConfig,请求参数不能为空");
        }
        Map<Long, List<DiscountOddsConfigDTO>> discountOddsConfigMap = discountOddsConfigDTOList.stream().collect(Collectors.groupingBy(DiscountOddsConfigDTO::getMatchId));
        for (Map.Entry<Long, List<DiscountOddsConfigDTO>> entry : discountOddsConfigMap.entrySet()) {
            Long standardMatchInfoId = entry.getKey();
            List<DiscountOddsConfigDTO> discountOddsConfigDTOListTemp = entry.getValue();
            if (CollectionUtils.isEmpty(discountOddsConfigDTOListTemp)) {
                continue;
            }
            Set<Long> marketCategoryIdSet = discountOddsConfigDTOListTemp.stream().map(DiscountOddsConfigDTO::getMarketCategoryId).collect(Collectors.toSet());
            String linkIdTemp = linkId+"_"+standardMatchInfoId;
            //查询标准赛事是否存在
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
            if (standardMatchInfo == null) {
                log.info("::{}::putDiscountOddsConfig,标准赛事未找到，标准赛事id:{}", linkId, standardMatchInfoId);
                continue;
            }
            //查询赛事的开售信息
            StandardSportMarketSell standardSportMarketSell =
                    standardSportMarketSellService.getItem(standardMatchInfo.getId());
            //赛事未开售，赔率不下发
            if (standardSportMarketSell == null) {
                log.info("::{}::putDiscountOddsConfig ,赛事未开售赔率不下发，标准赛事id：{}", linkId,
                        standardMatchInfo.getId());
                continue;
            }
            Map<String,DiscountOddsConfigDTO> discountOddsConfigDTOMap = discountOddsConfigDTOListTemp.stream().collect(Collectors.toMap(e->e.getOddsId().toString(), Function.identity(), (v1, v2) -> v2));
            //缓存配置数据
            String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_DISCOUNT_ODDS_CONFIG+standardMatchInfo.getId());
            redisService.hSetAll(redisKey, discountOddsConfigDTOMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            //下发配置数据
            //---------------处理盘口计算和排序-----------------
            //获取缓存中的所有盘口（赛前数据商和滚球数据商）
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(marketCategoryIdSet, linkIdTemp, standardMatchInfo, standardSportMarketSell);
            if (stringStandardMarketDataMessageMap.isEmpty()) {
                log.info("::{}::putDiscountOddsConfig,缓存中未获取到数据，标准赛事id：{}", linkId, standardMatchInfoId);
                continue;
            }
            //下发配置数据
            thirdMatchMarketProcessor.processOddsByAll(linkIdTemp,-1,null, standardMatchInfo,
                    marketCategoryIdSet, stringStandardMarketDataMessageMap, requests.getDataSourceTime(),
                    standardSportMarketSell, new HashMap<>());
        }
        return Response.success();
    }

    @Override
    public Response<StandardCategoryDataSourceCodeDTO> getStandardCategoryDataSourceCode(Request<Long> request) {
        log.info("::{}::getStandardCategoryDataSourceCode,请求参数：{}", request.getLinkId(), JSON.toJSONString(request.getData()));
        Long standardMatchInfoId = request.getData();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo == null) {
            log.info("::{}::getStandardCategoryDataSourceCode 使用标准赛事id的方式未获取到赛事信息，id:{}", request.getLinkId(), standardMatchInfoId);
            return Response.failed("标准赛事未找到，标准赛事id:" + standardMatchInfoId);
        }
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(standardMatchInfoId);
        if (CollectionUtils.isEmpty(thirdMatchInfoList)) {
            log.info("::{}::getStandardCategoryDataSourceCode,未查询到第三方赛事信息，标准赛事id:{}", request.getLinkId(), standardMatchInfoId);
            return Response.failed("未查询到第三方赛事信息，标准赛事id:" + standardMatchInfoId);
        }
        List<ThirdMatchInfo> thirdMatchInfoLTList = thirdMatchInfoList.stream().filter(e -> e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.TX.getCode()) || e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.LS.getCode())|| e.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.L02.getCode())).collect(Collectors.toList());
        return Response.success(categoryCodeProcessor.processStandardMatchInternalCode(request.getLinkId(),
                                                                                       thirdMatchInfoLTList,
                                                                                       standardMatchInfoId));
    }

    @Override
    public Response<MatchCategoryDataSourcesDTO> getStandardCategoryAvailableDataSources(Request<MatchCategoryDataSourcesDTO> request) {
        ValidatorUtils.validate(validator, request);
        MatchCategoryDataSourcesDTO data = request.getData();
        log.info("::{}::getStandardCategoryAvailableDataSources,请求参数：{}",
                 request.getLinkId(),
                 JSON.toJSONString(data));
        return dataSourceAutoSwitchService.getDataSources(request);

    }

    @Override
    public Response<List<ResDiscountOddsDTO>> checkDisCountOdds(Request<List<CheckDiscountOddsDTO>> request) {
        log.info("::{}::checkDisCountOdds,请求参数：{}", request.getLinkId(), JSON.toJSONString(request.getData()));
        List<CheckDiscountOddsDTO> list = request.getData();
        if (CollectionUtils.isEmpty(list)){
            return Response.failed();
        }
        List<ResDiscountOddsDTO> resDiscountOddsDTOs = new ArrayList<>();
        for (CheckDiscountOddsDTO checkDiscountOddsDTO : list){
            ResDiscountOddsDTO resDiscountOddsDTO = new ResDiscountOddsDTO();
            resDiscountOddsDTO.setOddsId(checkDiscountOddsDTO.getOddsId());
            //AO初盘第一条赔率
            String key = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS2+checkDiscountOddsDTO.getMatchId();
            Object obj = redisService.hGet(key,checkDiscountOddsDTO.getOddsId().toString());
            if (obj != null){
                resDiscountOddsDTO.setOriginalOddsValue((Integer)obj);
                log.info("::{}::checkDisCountOdds,oddsId:{},请求内容：{}", request.getLinkId(), checkDiscountOddsDTO.getOddsId(),obj.toString());
            }
            resDiscountOddsDTOs.add(resDiscountOddsDTO);
        }
        log.info("::{}::checkDisCountOdds,请求返回：{}", request.getLinkId(), JSON.toJSONString(resDiscountOddsDTOs));
        return Response.success(resDiscountOddsDTOs);
    }

    @Override
    public Response<MatchDataSourceDTO> autoSwitchDataSource(Request<MatchDataSourceDTO> request) {
        log.info("linkId:{},autoSwitchDataSource args:{}", request.getLinkId(), JSON.toJSONString(request.getData()));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response<MatchDataSourceDTO> result = dataSourceAutoSwitchService.autoSwitch(request);
        stopWatch.stop();
        log.info("linkId:{},autoSwitchDataSource, millis:{}, result:{}",
                 request.getLinkId(),
                 stopWatch.getTotalTimeMillis(),
                 JSON.toJSONString(result));
        return result;
    }




    /**
     * 关闭赛事下的所有盘口
     * @param linkId
     * @param matchId
     */
    private void closeAllMarketByMatch(String linkId, Long matchId){
        log.info("::{}::closeAllMarketByMatch处理入参:{}", linkId, matchId);
        List<StandardOutrightMarket> championMarketList = standardOutrightMarketService.queryChampionMarket(matchId);
        if ( CollectionUtils.isEmpty(championMarketList) ) {
            log.info("::{}::冠军盘口不存在", linkId);
            return;
        }

        List<ConfigOutrightTradeMarket> batchUpdate = Lists.newArrayList();
        List<ConfigOutrightTradeMarket> batchSave = Lists.newArrayList();
        List<StandardOutrightMarket> batchUpdateOutrightMarket = Lists.newArrayList();

        Map<Long, ConfigOutrightTradeMarket> marketTradeMap = null;
        List<ConfigOutrightTradeMarket> configOutrightTradeMarkets = outrightTradeMarketConfigService.selectListItem(matchId);
        if ( !CollectionUtils.isEmpty(configOutrightTradeMarkets) ) {
            marketTradeMap = configOutrightTradeMarkets.stream()
                    .collect(Collectors.toMap(ConfigOutrightTradeMarket::getStandardMarketId, Function.identity()));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("linkId:" + linkId +",matchId:" + matchId + "人工操盘过过滤盘口");
        for ( StandardOutrightMarket championMarket : championMarketList ) {

            if ( MapUtils.isNotEmpty(marketTradeMap) && marketTradeMap.containsKey(championMarket.getId()) ) {
                ConfigOutrightTradeMarket tradeMarket = marketTradeMap.get(championMarket.getId());
                if ( null != tradeMarket.getOperateType() && Constant.OUTRIGHT_ONE.equals(tradeMarket.getOperateType()) ) {
                    sb.append( "," +  championMarket.getId());
                    continue;
                }
                tradeMarket.setMarketStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                tradeMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                if ( StringUtils.isNotEmpty(linkId)) {
                    tradeMarket.setLinkId(linkId);
                }
                batchUpdate.add(tradeMarket);
            } else {
                ConfigOutrightTradeMarket configOutrightTradeMarket = new ConfigOutrightTradeMarket();
                configOutrightTradeMarket.setId(UUIdUtils.getId());
                configOutrightTradeMarket.setStandardMatchId(matchId);
                configOutrightTradeMarket.setStandardMarketId(championMarket.getId());
                configOutrightTradeMarket.setMarketStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                configOutrightTradeMarket.setLinkId(linkId);
                configOutrightTradeMarket.setOperaterId(101L);
                configOutrightTradeMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                configOutrightTradeMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                batchSave.add(configOutrightTradeMarket);
            }

            championMarket.setMarketStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            if ( StringUtils.isNotEmpty(linkId)) {
                championMarket.setLinkId(linkId);
            }
            championMarket.setModfiyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            batchUpdateOutrightMarket.add(championMarket);
        }
        log.info(sb.toString());

        if ( !CollectionUtils.isEmpty(batchUpdate) ) {
            outrightTradeMarketConfigService.updateBatchById(batchUpdate);
        }

        if ( !CollectionUtils.isEmpty(batchSave) ) {
            outrightTradeMarketConfigService.saveBatch(batchSave);
        }

        // Au模式下的盘口关闭
        if ( !CollectionUtils.isEmpty(batchUpdateOutrightMarket) ) {
            standardOutrightMarketService.updateBatchById(batchUpdateOutrightMarket);
        }

        // 三方的赔率列表的调整
        List<StandardSportMarket> standardSportMarketList = standardSportMarketService.getItemList(matchId);
        if ( !CollectionUtils.isEmpty(standardSportMarketList) ) {
            for ( StandardSportMarket ssm : standardSportMarketList ) {
                ssm.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                ssm.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                standardSportMarketService.updateByPrimaryKeySelective(ssm);
            }
        }


        // 获取标准赛事
        StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo( true, matchId);
        // 获取标准赛事开售信息
        StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell( true, standardMatchInfo.getId());
        // 获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap =
                thirdMatchMarketProcessor.getChampionStandardMarketDataMessageMap( linkId, standardMatchInfo,  standardSportMarketSell);
        // 有效id
        Set<Long> marketIdSet = championMarketList.stream().map(StandardOutrightMarket::getId).collect(Collectors.toSet());
        // 异步处理
        thirdMatchMarketProcessor.processOddsByAll( linkId, 0, null, standardMatchInfo, marketIdSet,
                standardMarketDataMessageMap, TimeUtils.millsSecondsEast8ZoneGmt(), standardSportMarketSell, new HashMap<>());
    }

    @Override
    public Response basketballWinnerConfig(Request<List<BasketballConfigDTO>> request) {
        log.info("::{}::basketballWinnerConfig处理入参:{}", request.getLinkId(),JSON.toJSONString(request.getData()));

        String key = Constant.REDIS_KEY.RONGHE_BASKET_MARKET_WINNER_CONFIG;
        Map<String,BasketballConfigDTO> map = request.getData().stream().collect(Collectors.toMap(BasketballConfigDTO::getAdd1,Function.identity(),(o,v)->v));
        redisService.hSetAll(key,map,Integer.MAX_VALUE);
        return Response.success("操作成功");
    }

    @Override
    public Response basketballEarlyRollLeagueSwitchConfig(Request<List<LeagueSwitchConfigDTO>> request) {
        log.info("::{}::basketballEarlyRollLeagueSwitchConfig处理入参:{}", request.getLinkId(),JSON.toJSONString(request.getData()));
        String key = Constant.REDIS_KEY.RONGHE_BASKET_EARLY_CONFIG;
        Map<String,Boolean> map = request.getData().stream().collect(Collectors.toMap(LeagueSwitchConfigDTO::getLeagueLevel,LeagueSwitchConfigDTO::isStatus));
        redisService.hSetAll(key,map,Integer.MAX_VALUE);
        String key2 = Constant.REDIS_KEY.RONGHE_BASKET_ADD_CONFIG;
        Map<String,Boolean> map2 = request.getData().stream().collect(Collectors.toMap(LeagueSwitchConfigDTO::getLeagueLevel,LeagueSwitchConfigDTO::isAddMarketStatus));
        redisService.hSetAll(key2,map2,Integer.MAX_VALUE);
        return Response.success("操作成功");
    }

    @Override
    public Response tournamentLevelChange(Request<TournamentLevelChangeDTO> request) {
        log.info("::{}::tournamentLevelChange处理入参:{}", request.getLinkId(),JSON.toJSONString(request.getData()));
        List<Long> matchIds = request.getData().getMatchIds();
        List<StandardMatchInfo> matchs = standardMatchInfoService.getItems(matchIds);
        List<StandardMatchInfo> tempMatch = matchs.stream().filter(e->e.getMatchOver() == 0 && e.getSportId().equals(StandardSportTypeEnum.Basketball.getCode())).collect(Collectors.toList());

        Set<Long> marketCategoryIdSet = new HashSet<>(MarginCategoryConfig.A_MARGIN_CATEGORY);
        for (StandardMatchInfo standardMatchInfo : tempMatch){
            String linkIdTemp = request.getLinkId()+"_"+standardMatchInfo.getId();
            StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell( true, standardMatchInfo.getId());
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(marketCategoryIdSet, linkIdTemp, standardMatchInfo, standardSportMarketSell);
            if (stringStandardMarketDataMessageMap.isEmpty()) {
                log.info("::{}::tournamentLevelChange,缓存中未获取到数据，标准赛事id：{}", linkIdTemp, standardMatchInfo.getId());
                continue;
            }
            //下发配置数据
            thirdMatchMarketProcessor.processOddsByAll(linkIdTemp,-1,null, standardMatchInfo,
                    marketCategoryIdSet, stringStandardMarketDataMessageMap, System.currentTimeMillis(),
                    standardSportMarketSell, new HashMap<>());
        }
        return Response.success("操作成功");
    }

    @Override
    public Response footballWinnerConfig(Request<FootballConfigDTO> request) {
        log.info("::{}::footballWinnerConfig处理入参:{}", request.getLinkId(),JSON.toJSONString(request.getData()));
        String key = Constant.REDIS_KEY.RONGHE_FOOT_MARKET_WINNER_CONFIG;
        redisService.set(key,request.getData().getStatus(),Integer.MAX_VALUE);
        return Response.success("操作成功");
    }

    @Override
    public Response<Map<String, ThirdMarketModifytimeDTO>> getThirdMarletLastModifyTime(Request<ThirdMarketLastModifyTimeDTO> request) {
        log.info("::{}::getThirdMarletLastModifyTime处理入参:{}", request.getLinkId(),JSON.toJSONString(request.getData()));
        String thirdKey = Constant.REDIS_KEY.THIRD_MARKET_108048+request.getData().getMatchId();
        Map<String, ThirdMarketModifytimeDTO> map = redisService.hGetAll(thirdKey);
        if (MapUtils.isEmpty(map)){
            return Response.success(new HashMap<>());
        }
        Set<Long> categoryFilter = CollectionUtils.isEmpty(request.getData().getCategoryIds())
                ? null
                : new HashSet<>(request.getData().getCategoryIds());
        long now = System.currentTimeMillis();
        Map<String, ThirdMarketModifytimeDTO> result = new HashMap<>(map.size());
        map.forEach((fieldKey, dto) -> {
            if (dto == null) {
                return;
            }
            if (categoryFilter != null) {
                Long categoryId = ThirdMarket108048Helper.parseCategoryId(fieldKey);
                if (categoryId == null || !categoryFilter.contains(categoryId)) {
                    return;
                }
            }
            ThirdMarket108048Helper.applyLevel(dto, now);
            result.put(fieldKey, dto);
        });
        return Response.success(result);
    }

}
