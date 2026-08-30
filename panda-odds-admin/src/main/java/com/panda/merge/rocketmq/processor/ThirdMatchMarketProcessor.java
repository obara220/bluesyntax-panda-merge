package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.panda.merge.cache.CategoryStatsTimeData;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.*;
import com.panda.merge.config.MarketDbProducer;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.*;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.dto.odds.MergeMarketStatusEnum;
import com.panda.merge.odds.cache.AutoCloseCacheService;
import com.panda.merge.odds.cache.FootballScoreCacheService;
import com.panda.merge.odds.enums.MarketHandlingEnum;
import com.panda.merge.odds.service.PlayRiskManagerService;
import com.panda.merge.odds.validate.FootballMarketOddsValidateService;
import com.panda.merge.odds.validate.FootballMarketValidateService;
import com.panda.merge.proxy.UpdateOperateProxy;
import com.panda.merge.rocketmq.producer.*;
import com.panda.merge.service.*;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_TRAD_CONFIG;
import static com.panda.merge.component.AutoDiffCountMarketMalay.subDoubleTwo;
import static com.panda.merge.component.ThirdMarketSaveProcessor.checkA01ExtendedTimeStatus;
import static com.panda.merge.constant.ConstantSystem.ONE;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_MARKET_API;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/13 <br>
 */
@Component
@Slf4j
@Validated
public class ThirdMatchMarketProcessor extends BaseProcessor {

    @Autowired
    private I18nOutrightMarketOddsService i18nOutrightMarketOddsService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;

    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;

    @Autowired
    private AutoDiffCountMarketMalay autoDiffCountMarketMalay;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private ConfigMarketTradeItemService configMarketTradeItemService;

    @Autowired
    private ConfigMarketCategoryPlaceService configMarketCategoryPlaceService;

    @Autowired
    private ConfigTradeTypeService configTradeTypeService;

    @Autowired
    private PlayRiskManagerService playRiskManagerService;

    @Autowired
    private ConfigMarketCategoryMarginService configMarketCategoryMarginService;

    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;

    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private StandardOutrightMatchInfoService standardOutrightMatchInfoService;

    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @Autowired
    InitializeComponent initializeComponent;

    @Autowired
    private ConfigMarketHeadGapService configMarketHeadGapService;

    @Autowired
    private ConfigMarketStatusTradeService configMarketStatusTradeService;

    @Autowired
    private StandardMatchSwitchStatusProducer switchStatusProducer;

    @Autowired
    private ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;
    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;
    @Autowired
    private DataMerchantBaffleProducer dataMerchantBaffleProducer;
    @Autowired
    private MatchOddWarningProducer matchoddWarningProducer;
    @Autowired
    private StandardSportPlayerService standardSportPlayerService;
    @Autowired
    private ThirdSportPlayerService thirdSportPlayerService;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    @Autowired
    private ThirdMatchTeamRelationService thirdMatchTeamRelationService;

    @Autowired
    private OutrightTradeTypeConfigService outrightTradeTypeConfigService;
    @Autowired
    private OutrightTradeMarketConfigService outrightTradeMarketConfigService;
    @Autowired
    private OutrightTradeOddsConfigService outrightTradeOddsConfigService;
    @Autowired
    private OutrightTradeProbabilityConfigService outrightTradeProbabilityConfigService;
    @Autowired
    private ThirdSportTeamService thirdSportTeamService;
    @Autowired
    private ConfigTournamentTradeItemService configTournamentTradeItemService;
    @Autowired
    private CommonAsyncService commonAsyncService;
    @Lazy
    @Autowired
    private ThirdMarketPreResultProcessor thirdMarketPreResultProcessor;
    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;
    @Autowired
    private UpdateOperateProxy updateOperateProxy;
    @Autowired
    private ConfigMarketOddsStatusService configMarketOddsStatusService;
    @Autowired
    private ConfigMatchStatusService configMatchStatusService;
    @Autowired
    private ThreadPoolConfig threadPoolConfig;
    @Autowired
    private BasketballZeroProcessor basketballZeroProcessor;
    @Autowired
    private MyCalculationMarketProcessor myCalculationMarketProcessor;
    @Autowired
    private ThirdMarketSaveProcessor thirdMarketSaveProcessor;

    @Value("#{'${ballhead.remove.decimal.categoryId}'.split(',')}")
    private Set<Long> ballHeadRemoveDecimalCategoryIdSet;

    /**
     * 自动开盘开关 1开 0关
     */
    @Value("${open.market.status}")
    private Integer openMarketStatus;

    @Autowired
    private MarketDbProducer marketDbProducer;
    @Autowired
    private StandardMatchMarketOddsLinkageProcessor standardMatchMarketOddsLinkageProcessor;
    @Autowired
    private DealMarketStatusProcessor dealMarketStatusProcessor;

    @Autowired
    private MarketOddsPlaceProcessor marketOddsPlaceProcessor;

    @Autowired
    private ClosedMarketPlaceSortHelper closedMarketPlaceSortHelper;

    @Autowired
    private StandardMarketPASort standardMarketPASort;

    @Autowired
    private FootballMarketsSoreProcessor checkMarketsSoreProcessor;

    @Autowired
    private FootballMarketValidateService footballMarketValidateService;
    @Autowired
    private AutoCloseCacheService autoCloseCacheService;
    @Autowired
    private BasketballOriginalOddsLimitProcessor basketballOriginalOddsLimitProcessor;

    @Autowired
    private FootballMarketOddsValidateService footballMarketOddsValidateService;
    @Resource
    private ThirdMarketBallHeadProcessor thirdMarketBallHeadProcessor;

    @Autowired
    private CorrectScorelOddsLinkageProcessor correctScorelOddsLinkageProcessor;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    /**
     * 玩法全封或全关转为关的等待处理开关
     */
    @NacosValue(value = "${category.waitCloseTime.Switch:true}", autoRefreshed = true)
    private boolean waitCloseTimeSwitch;
    @Autowired
    private FootballScoreCacheService footballScoreCacheService;
    /**
     * 处理收到的数据源赔率
     *
     * @param request
     */
    @Async("AccessMatchMarketData")
    @ExceptionHelper
    public void accessMatchMarketData(@Valid Request<ThirdMatchMarketDTO> request) {
        String linkId = request.getLinkId();
        // 至于Id 我觉得给UUID是可行的~
        StopWatch swRedis = new StopWatch("赔率服务数据源赔率入口主流程_" + UUIdUtils.getId());
        swRedis.start("校验linkId耗时");
        validateLinkId(THIRD_MATCH_MARKET_API, request);
        swRedis.stop();
        ThirdMatchMarketDTO thirdMatchMarketDTO = request.getData();
        String dataSourceCode = thirdMatchMarketDTO.getDataSourceCode();
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        Integer marketType = thirdMatchMarketDTO.getMarketList().get(0).getMarketType();
        log.info("::{}::接收数据源赔率开始,三方赛事id:{}", linkId, thirdMatchSourceId);
        swRedis.start("校验运动类型，三方标准赛事，开售信息");
        //校验是否数据源运动类型是否存在
        validateSportId(dataSourceCode, String.valueOf(thirdMatchMarketDTO.getSportId()));
        //判断冠军玩法
        boolean isOutRight = Arrays.asList(MarginCategoryConfig.THIRD_OUTRIGHT_CATEGORY).contains(thirdMatchMarketDTO.getMarketList().get(0).getThirdMarketCategorySourceId());
        //兼容冠军玩法，获取三方赛事信息
        ThirdMatchInfo thirdMatchInfo = getThirdMatchInfo(isOutRight, dataSourceCode, thirdMatchSourceId);
        if (thirdMatchInfo == null) {
            log.error("::{}::三方赛事不存在,三方数据源id:{},冠军玩法:{}", linkId, thirdMatchSourceId, isOutRight);
            swRedis.stop();
            thirdMatchNotExistCacheMarket(linkId, thirdMatchMarketDTO, isOutRight);
            return;
        }
        //兼容冠军玩法，获取标准赛事信息
        StandardMatchInfoDetail standardMatchInfo = getStandardMatchInfo(isOutRight, thirdMatchInfo.getReferenceId());
        if (null != standardMatchInfo) {
            if (1 == standardMatchInfo.getMatchOver()) {
                log.error("::{}::标准赛事已经完赛，不接受赔率数据:{}", linkId, JSONObject.toJSONString(standardMatchInfo));
                return;
            }
        }
        //兼容冠军玩法，获取赛事开售信息
        StandardSportMarketSell standardSportMarketSell = getStandardSportMarketSell(isOutRight, thirdMatchInfo.getReferenceId());
        swRedis.stop();
        //-----------循环处理盘口数据---------------

        //以下数据库逻辑操作有并发问题，这里需要以赛事维度加redis锁
        swRedis.start("获取分布式锁，循环处理三方盘口，标准盘口");
        //TODO:优化加锁机制：
        // 1.只有生成了标准的才加锁，因为只有在有标准的情况下，才会入缓存，才会要求对缓存的操作必须顺序性执行
        // 2.对标准赛事加锁，不区分数据源
        boolean isLock = false;
        AtomicBoolean isClosePreMarket = new AtomicBoolean(Boolean.FALSE);
        //三方赔率修改
        List<ThirdSportMarketOdds> thirdSportMarketOddsUpdate = Collections.synchronizedList(new ArrayList());
        //标准赔率修改
        List<StandardSportMarketOdds> standardSportMarketOddsUpdate = Collections.synchronizedList(new ArrayList());
        //存储需要下发的三方数据商盘口集合
        List<ThirdSportMarketMessage> thirdSportMarketMessages = Collections.synchronizedList(new ArrayList());
        //三方盘口入库
        List<ThirdMarketDTO> thirdMarketDTOS = thirdMarketSaveProcessor.marketSaveProcessor(linkId, thirdMatchInfo, standardMatchInfo, thirdMatchMarketDTO, marketType, thirdSportMarketMessages, request.getDataSourceTime(),thirdSportMarketOddsUpdate);
        Map<Boolean, List<ThirdMarketDTO>> thirdMarketDTOMap = thirdMarketDTOS.stream().collect(Collectors.groupingBy(ThirdMarketDTO::getLock));
        //不需要加锁
        List<ThirdMarketDTO> thirdMarketDTONotLock = Collections.synchronizedList(new ArrayList());
        if (null != thirdMarketDTOMap.get(Boolean.FALSE)) {
            thirdMarketDTONotLock = thirdMarketDTOMap.get(Boolean.FALSE);
        }
        //需要加锁的盘口
        List<ThirdMarketDTO> thirdMarketDTOLock = Collections.synchronizedList(new ArrayList());
        if (null != thirdMarketDTOMap.get(Boolean.TRUE)) {
            thirdMarketDTOLock = thirdMarketDTOMap.get(Boolean.TRUE);
        }
        if (standardMatchInfo != null && !CollectionUtils.isEmpty(thirdMarketDTOLock)) {
            isLock = true;
            if (marketType == 1 && !isOutRight && standardSportMarketSell != null) {

                String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + standardMatchInfo.getId();
                if (Objects.isNull(redisService.get(key))) {
                    //重新查赛事开赛时间，赛程修改开赛时间不会同步缓存
                    StandardMatchInfo refreshStandardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchInfo.getId());
                    if (null != refreshStandardMatchInfo) {
                        Long beginTime = refreshStandardMatchInfo.getBeginTime();
                        if (TimeUtils.timeCalendar(beginTime)) {
                            log.info("::{}::缓存标准赛事开赛时间,标准赛事ID:{},开赛时间:{}", linkId, standardMatchInfo.getId(), beginTime);
                            String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
                            String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                            redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), beginTime,Integer.MAX_VALUE);
                        } else {
                            log.info("::{}::缓存标准赛事开赛时间,大于7天后时间不入缓存,标准赛事ID:{},开赛时间:{}", linkId, standardMatchInfo.getId(), beginTime);
                        }
                    } else {
                        isLock = false;
                    }
                } else {
                    log.info("::{}::标准赛事已经下发过自动构建赔率,标准赛事ID:{}", linkId, standardMatchInfo.getId());
                }
            }
        }

        //存储当前数据里面的盘口id
        Set<Long> marketIdSet = Collections.synchronizedSet(new HashSet());
        //存储当前数据里面的玩法
        Set<Long> marketCategoryIdSet = Collections.synchronizedSet(new HashSet());
        //存储需要缓存的盘口数据
        List<StandardSportMarket> standardSportMarketList = Collections.synchronizedList(new ArrayList());
        List<StandardMarketDataMessage> standardMarketDataMessageList = Collections.synchronizedList(new ArrayList());
        //最终下发的玩法盘口
        Map<String, StandardMarketDataMessage> standardCategoryMarketMessageMap = new ConcurrentHashMap<>();
        //最终需要推送给风控的报警的玩法集合
        Set<Long> riskCategorySet = Collections.synchronizedSet(new HashSet());
        //记录数据源赔率变动的玩法对应的投注项
        Map<Long, List<String>> changeCategoryOddsType = new ConcurrentHashMap<>();
        //测试联赛需要处理的三方盘口数据
        List<ThirdMarketDTO> thirdMarketDTOList = Collections.synchronizedList(new ArrayList());
        //当前时间前100秒 1852需求兜底
        long befTime = System.currentTimeMillis() - 100000;
        try {
            if (!CollectionUtils.isEmpty(thirdMarketDTOLock)) {
                StopWatch marketStop = new StopWatch("循环处理三方盘口入库总耗时");
                marketStop.start();
                //异步循环执行
                List<CompletableFuture<?>> futures = new ArrayList<>();
                TaskExecutor taskExecutor = threadPoolConfig.getThirdAndStandardMarketProcess();
                //根据三方玩法ID分组，减少交互
                Map<String, List<ThirdMarketDTO>> ThirdMarketMapDTO = thirdMarketDTOLock.stream().collect(Collectors.groupingBy(ThirdMarketDTO::getThirdMarketCategorySourceId));
                for (Map.Entry<String, List<ThirdMarketDTO>> entry : ThirdMarketMapDTO.entrySet()) {
                    //三方盘口ID
                    String thirdCategorySourceId = entry.getKey();
                    //获取盘口的三方玩法
                    ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSourceCode, thirdCategorySourceId);
                    if (thirdMarketCategory == null) {
                        log.info("::{}::未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                        continue;
                    }
                    Long referenceId = thirdMarketCategory.getReferenceId();
                    if (null == referenceId || 0L == referenceId) {
                        log.info("::{}::三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                        continue;
                    }
                    //-------------------收到滚球赔率下发赛事滚球标识，并赛事级别封盘(滚球未开售：赛前需要和下发的数据源进行对比一致才下发)--------------------
                    Boolean isLive = !Objects.isNull(redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId()));
                    //玩法维度锁
                    String lockValue = UUIdUtils.getId() + "_" + linkId;
                    String redisLocKey = Constant.REDIS_KEY.RONGHE_CATEGORY_LOCK + standardMatchInfo.getId() + "_" + referenceId;
                    log.info("::{}::主流程准备获取分布式锁:{},玩法：{},缓存滚球标识：{}", linkId, redisLocKey, referenceId, isLive);
                    redisService.tryLock(redisLocKey, lockValue, 2, 2);
                    log.info("::{}::主流程准备获取到分布式锁:{},玩法：{}", linkId, redisLocKey, referenceId);
                    //存储需要校验数据源挡板的投注项集合
                    Set<Long> oddsTypeIdSet = new HashSet();
                    //存储需要校验报警的玩法集合
                    Set<Long> categorySet = new HashSet();
                    try {
                        List<ThirdMarketDTO> thirdMarketDTOsLock = entry.getValue();
                        futures.add(CompletableFuture.supplyAsync(() -> {
                            for (ThirdMarketDTO thirdMarketDTO : thirdMarketDTOsLock) {
                                //如果这个时候来了BC的早盘关盘，需要去关滚球盘
                                if (isLive && marketType == 1) {
                                    if (DataSourceCodeEnum.BC.code.equalsIgnoreCase(dataSourceCode)
                                            && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
                                        thirdMarketDTO.setMarketType(0);
                                    } else {
                                        log.info("::{}::标准赛事已经进入即将开赛阶段，不处理任何早盘数据，直接返回", linkId);
                                        continue;
                                    }
                                }
                                // 至于Id 我觉得给UUID是可行的~
                                StopWatch sw = new StopWatch(UUID.randomUUID().toString());
                                //三方盘口的赛种Id要修改为融合的标识赛种id
                                thirdMarketDTO.setSportId(thirdMatchInfo.getSportId());
                                thirdMarketDTO.setMarketCategoryId(referenceId);
                                //两项盘数据源赔率合法性验证
                                if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarketDTO.getStatus()) && !CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList()) && thirdMarketDTO.getMarketOddsList().size() == 2) {
                                    if (thirdMarketDTO.getMarketOddsList().get(0).getOriginalOddsValue() < 1.01 * 100000 || thirdMarketDTO.getMarketOddsList().get(1).getOriginalOddsValue() < 1.01 * 100000) {
                                        //如果是A01赔率 判断是否开启延长开售才封盘 开启则不封盘/不开启则正常处理 注:(玩法id 2 4 18 19)
                                        Object  a01ExtendedTimeObjects = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY +thirdMatchInfo.getReferenceId());
                                        if(!thirdMarketDTO.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)||!checkA01ExtendedTimeStatus(thirdMarketDTO,a01ExtendedTimeObjects)){
                                            thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                                            log.info("::{}::两项盘(三方盘口源id):{},如果存在一个投注项原始赔率小于1.01,合法性封盘", linkId, thirdMarketDTO.getThirdMarketSourceId());
                                        }
                                    }
                                }
                                // 判断盘口时间戳先后，比当前盘口时间戳小的不处理
                                sw.start("时间戳校验耗时");
                                String dataSourceTimeKey;
                                if (dataSourceCode.equals(DataSourceCodeEnum.TX.code)) {
                                    dataSourceTimeKey = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + thirdMatchSourceId + "_" + thirdMarketDTO.getThirdMarketCategorySourceId() + "_" + thirdMarketDTO.getOfferLineId();
                                } else {
                                    dataSourceTimeKey = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + thirdMarketDTO.getThirdMarketSourceId();
                                }
                                String dataSourceTimeKeyMd5 = DigestUtil.md5Hex(dataSourceTimeKey);
                                Long oldTime = (Long) redisService.get(dataSourceTimeKeyMd5);
                                if (oldTime != null && oldTime > thirdMarketDTO.getModifyTime()) {
                                    if (dataSourceCode.equals(DataSourceCodeEnum.TX.code) && null != standardMatchInfo) {
                                        configMatchStatusService.processTXTimestamps(linkId, thirdMarketDTO, standardMatchInfo.getId(), dataSourceCode, standardMatchInfo.getBeginTime());
                                    }
                                    log.info("::{}::盘口时间戳小于当前盘口时间戳,三方源盘口id:{},RedisKEY:{},旧时间戳:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), dataSourceTimeKey, oldTime);
                                    sw.stop();
                                    continue;
                                }
                                redisService.set(dataSourceTimeKeyMd5, thirdMarketDTO.getModifyTime(), RedisConfig.REDIS_MY_TIME);
                                if (StandardSportTypeEnum.FootBall.code.equals(thirdMatchInfo.getSportId())
                                        && !MarginCategoryConfig.IGNORE_SCORE_DATASOURCE_CODE.contains(dataSourceCode)) {                                    Long marketCategoryId = referenceId;
                                    //TX让球比分处理
                                    txHandicapDispose(linkId, marketCategoryId, standardMatchInfo, thirdMarketDTO, thirdMatchInfo, dataSourceCode);
                                    //LS让球比分处理
                                    //lsHandicapDispose(linkId, marketCategoryId, standardMatchInfo, thirdMarketDTO, thirdMatchInfo, dataSourceCode);
                                    //数据源角球基准分计算
                                    cornerScore(linkId, marketCategoryId, standardMatchInfo, thirdMarketDTO, thirdMatchInfo, dataSourceCode);
                                    //15分钟进球/角球基准分计算
                                    fifteenMinutesScore(linkId, marketCategoryId, standardMatchInfo, thirdMarketDTO);
                                }
                                sw.stop();
                                //-------------处理三方盘口及投注项数据------------
                                // 至于Id 我觉得给UUID是可行的~
                                sw.start("处理三方盘口耗时");
                                ThirdSportMarketMessage thirdSportMarketMessage = processThirdSportMarket(linkId, dataSourceCode, thirdMatchInfo, thirdMarketDTO, thirdMarketCategory,thirdSportMarketOddsUpdate);
                                if (thirdSportMarketMessage != null) {
                                    thirdSportMarketMessages.add(thirdSportMarketMessage);
                                }
                                thirdMarketDTOList.add(thirdMarketDTO);
                                sw.stop();
                                log.info("::{}::三方盘口id:{},三方盘口处理耗时{}ms," + sw.prettyPrint(), linkId, thirdMarketDTO.getThirdMarketSourceId(), sw.getTotalTimeMillis());
                                //-------------处理标准盘口及投注项数据------------
                                StopWatch swStandard = new StopWatch(UUID.randomUUID().toString());
                                swStandard.start("处理标准盘口耗时");
                                //主客队相反盘口、投注项相关内容处理（A01/AO与主数据源一致，接入侧已处理，不再重复翻转）
                                if (ONE.equals(thirdMatchInfo.getHomeAwayOpposite()) && thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code)) {
                                    if (!CategoryOppositeConfig.FootBall.containsCategory(referenceId)) {
                                        continue;
                                    }
                                    if (!skipHomeAwayOppositeForDataSource(dataSourceCode)) {
                                        changeStandardMarketContent(linkId, dataSourceCode, thirdMarketCategory, thirdMarketDTO);
                                    }
                                }
                                StandardMarketDataMessage standardMarketDataMessage = processStandardSportMarket(linkId, standardMatchInfo, thirdMarketDTO, thirdMarketCategory, standardSportMarketSell, standardSportMarketList, request.getDataSourceTime(), oddsTypeIdSet, categorySet, changeCategoryOddsType,standardSportMarketOddsUpdate);
                                swStandard.stop();
                                log.info("::{}::三方盘口id:{},标准盘口处理耗时{}ms," + swStandard.prettyPrint(), linkId, thirdMarketDTO.getThirdMarketSourceId(), swStandard.getTotalTimeMillis());
                                if (standardMarketDataMessage == null) {
                                    continue;
                                }
                                marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
                                marketIdSet.add(standardMarketDataMessage.getRelationMarketId());
                                standardMarketDataMessageList.add(standardMarketDataMessage);
                            }
                            return null;
                        }, taskExecutor));
                        //等待盘口异步处理
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                        //本次有改变的盘口为空，赔率不下发
                        if (CollectionUtils.isEmpty(standardMarketDataMessageList)) {
                            log.info("::{}:: 本次有改变的盘口为空，赔率不下发1, 玩法={}", linkId, referenceId);
                            continue;
                        }
                        log.info("::{}:: 主流程处理玩法完成准备排序, 玩法={}", linkId, referenceId);
                        //冠军盘口不处理
                        String redisKey;
                        if (marketType == 2) {
                            redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode;
                        } else {
                            redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + referenceId);
                        }
                        //获取本次玩法下面所有盘口
                        Map<String, StandardMarketDataMessage> standardMarketMessageNewMap = redisService.hGetAll(redisKey);
                        //100秒之内下发的赔率 && 当前玩法不是MTS链路（4405：玩法级判断）
                        String playRiskManagerCode = playRiskManagerService.getPlayRiskManagerCode(linkId, standardMatchInfo.getId(), marketType, referenceId, standardSportMarketSell);
                        boolean isMtsFamily = playRiskManagerService.isMtsFamily(playRiskManagerCode);
                        if (request.getDataSourceTime() > befTime && (!isMtsFamily) && MarginCategoryConfig.NO_CLOS_SPORT.contains(standardMatchInfo.getSportId()) && MarginCategoryConfig.NO_CLOS_DATA_SOURCE_CODE.contains(dataSourceCode)) {
                            //判断出所有盘口状态为封盘的玩法，执行操盘2.0操作(所有盘口为封盘的玩法，并设置了2.0操盘开启。执行盘口重开操作)1852
                            StopWatch swj = new StopWatch(UUID.randomUUID().toString());
                            swj.start("拒接三方数据源封/关处理");
                            configMatchStatusService.processConfigMatchStatus(linkId, standardMarketMessageNewMap, dataSourceCode, standardMatchInfo.getId(),
                                    standardMatchInfo.getSportId(), marketType, standardMatchInfo.getBeginTime(), request.getDataSourceTime(), marketCategoryIdSet, standardMatchInfo.getMatchPeriodId());
                            swj.stop();
                            log.info("::{}::拒接三方数据源封/关处理耗时{}ms," + swj.prettyPrint(), linkId, swj.getTotalTimeMillis());
                        }
                        //对本次改变的玩法进行排序
                        marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketMessageNewMap, standardMatchInfo, Sets.newHashSet(referenceId), oddsTypeIdSet, categorySet, riskCategorySet, true);
                        //排完排序后放入缓存
                        redisService.hSetAll(redisKey, standardMarketMessageNewMap, marketCacheTime(standardMatchInfo.getBeginTime()));
                        standardCategoryMarketMessageMap.putAll(standardMarketMessageNewMap);
                        log.info("::{}:: accessMatchMarketData刷新缓存盘口信息, key={},玩法={}", linkId, redisKey, referenceId);
                    } finally {
                        redisService.unLock(redisLocKey, lockValue);
                        log.info("::{}::主流程释放分布式锁:{},玩法：{}", linkId, redisLocKey, referenceId);
                    }
                }
                //数据源赔率告警，监听赔率数据下发时间
                matchOddsWarning(linkId, marketType, standardMatchInfo, marketCategoryIdSet);
                marketStop.stop();
                log.info("::{}::三方标准盘口处理总耗时{}ms," + marketStop.prettyPrint(), linkId, marketStop.getTotalTimeMillis());
            }
            if(DataSourceCodeEnum.AO.code.equals(dataSourceCode)){
                request.setDataSourceTime(System.currentTimeMillis());
            }
            //三方赔率修改
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsUpdate)) {
                //发送mq
                marketDbProducer.sendThirdMarketOddsUpdateInfo(linkId, thirdSportMarketOddsUpdate);
            }
            //标准盘口赔率修改
            if (!CollectionUtils.isEmpty(standardSportMarketOddsUpdate)) {
                standardSportMarketOddsService.upStandardOddsList(linkId, standardMatchInfo.getId(), standardSportMarketOddsUpdate);
            }
            //冠军赛事初始化盘口开售表
            if (isOutRight && !CollectionUtils.isEmpty(standardSportMarketList)) {
                String autoSellStatus = "Yes".equals(standardMatchInfo.getAutoSellStatus()) ? SellStatusEnum.SOLD.value : SellStatusEnum.UNSOLD.value;
                log.info(":{}::初始化盘口开售表,标准赛事id:{},autoSellStatus:{},size:{}", linkId, thirdMatchInfo.getReferenceId(), autoSellStatus, standardSportMarketList.size());
                List<StandardOutrightMarket> standardOutrightMarketList = new ArrayList<>();
                standardSportMarketList.forEach((v) -> {
                    if (null != v.getRelationMarketId()) {
                        StandardOutrightMarket standardOutrightMarket = new StandardOutrightMarket();
                        standardOutrightMarket.setId(v.getRelationMarketId());
                        standardOutrightMarket.setStandardMatchId(thirdMatchInfo.getReferenceId());
                        standardOutrightMarket.setMarketCategoryId(v.getMarketCategoryId());
                        standardOutrightMarket.setMarketStatus(v.getStatus());
                        standardOutrightMarket.setNameCode(v.getNameCode());
                        standardOutrightMarket.setLinkId(linkId);
                        standardOutrightMarket.setMarketSellStatus(autoSellStatus);
                        standardOutrightMarketList.add(standardOutrightMarket);
                    }
                });
                standardOutrightMarketService.saveBatch(standardOutrightMarketList);
            }
            swRedis.stop();
            //标准赛事不存在，赔率不下发
            if (standardMatchInfo == null) {
                log.error("::{}::标准赛事不存在,三方赛事id:{}", linkId, thirdMatchInfo.getId());
                return;
            }
            //赛事未开售，赔率不下发
            if (standardSportMarketSell == null) {
                log.info("::{}::赛事未开售赔率不下发,标准赛事id:{}", linkId, standardMatchInfo.getId());
                return;
            }
            //更新赛事玩法赔率最新更新时间 1848
            processMonitorCategoryMessage(linkId, standardMatchInfo.getSportId(), standardMatchInfo.getId(), marketCategoryIdSet, standardMatchInfo.getBeginTime(), request.getDataSourceTime(), marketType);
            if (!CollectionUtils.isEmpty(thirdSportMarketMessages)) {
                commonAsyncService.sendMessageToRisk(linkId + "_third", standardMatchInfo, thirdSportMarketMessages);
            }
            //把不需要加锁的盘口加入到list走需求
            if (!CollectionUtils.isEmpty(thirdMarketDTONotLock)) {
                thirdMarketDTOList.addAll(thirdMarketDTONotLock);
            }
            //本次有改变的盘口为空，赔率不下发
            if (CollectionUtils.isEmpty(standardMarketDataMessageList)) {
                log.info("::{}::标准赛事id:{},本次有改变的盘口为空,赔率不下发", linkId, standardMatchInfo.getId());
                //检查是否是测试联赛
                if (standardMatchInfo != null) {
                    //检查是否有绑定测试赛事
                    checkIsTestMatch(linkId, standardMatchInfo, thirdMatchMarketDTO, request, thirdMarketDTOList);
                }
                return;
            }
            //-------------------收到滚球赔率下发赛事滚球标识，并下发赛前关盘--------------------
            newClosePreMarkets(linkId, standardSportMarketSell, marketType, standardMatchInfo, request.getDataSourceTime(),true,new ArrayList<>(),0);
        } finally {
        }
        //测试联赛先不不处理
//        if (CollectionUtils.isEmpty(standardMarketMessageMap)) {
//            log.info("::{}::标准赛事id:{},standardMarketMessageMap is null", linkId, standardMatchInfo.getId());
//            //检查是否是测试联赛
//            if (standardMatchInfo != null) {
//                //检查是否有绑定测试赛事
//                checkIsTestMatch(linkId, standardMatchInfo, thirdMatchMarketDTO, request, thirdMarketDTOList);
//            }
//            return;
//        }
        //冠军盘处理
        Map<String, StandardMarketDataMessage> standardMarketMessageMap = new HashMap<>();
        if (isOutRight) {
            List<StandardOutrightMarket> outrightMarketList = standardOutrightMarketService.selectOutrightMarketSellList(standardMatchInfo.getId());
            if (CollectionUtils.isEmpty(outrightMarketList)) {
                log.info("::{}:: 冠军赛事未开售盘口,赔率不下发,冠军赛事id:{}", linkId, standardMatchInfo.getId());
                return;
            }
            //最后下发重新获缓存最新盘口进行下发
            String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode;
            Map<String, StandardMarketDataMessage> standardMarketMessageMapCache = redisService.hGetAll(redisKey);
            List<Long> marketIdList = outrightMarketList.stream().map(x -> x.getId()).collect(Collectors.toList());
            standardMarketMessageMap = standardMarketMessageMapCache.entrySet().stream().filter(map -> marketIdList.contains(map.getValue().getRelationMarketId())).collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> e.getValue()));
        }
        if (marketCategoryIdSet.size() == 0) {
            log.info("::{}::标准赛事id:{},本次没有需要处理的标准玩法id:{}", linkId, standardMatchInfo.getId(), marketCategoryIdSet);
            //检查是否是测试联赛
            if (standardMatchInfo != null) {
                //检查是否有绑定测试赛事
                checkIsTestMatch(linkId, standardMatchInfo, thirdMatchMarketDTO, request, thirdMarketDTOList);
            }
            return;
        }
        if (marketType == 2) {
            //-------------------冠军操盘，直接下发赔率--------------------
            StopWatch swMTS = new StopWatch(UUID.randomUUID().toString());
            swMTS.start("冠军操盘盘口处理耗时");
            //对盘口进行排序，封装
            processOddsByOutright(linkId, standardMatchInfo, marketIdSet, standardMarketMessageMap, request.getDataSourceTime(), changeCategoryOddsType);
            swMTS.stop();
            log.info("::{}::冠军操盘盘口处理耗时{}ms," + swMTS.prettyPrint(), linkId, swMTS.getTotalTimeMillis());
        } else {
            // 4405：按玩法级操盘模式分组（MTS组 / 非MTS组），同场支持混合操盘
            Set<Long> mtsCategoryIds = new HashSet<>();
            Set<Long> pandaCategoryIds = new HashSet<>();
            for (Long categoryId : marketCategoryIdSet) {
                String code = playRiskManagerService.getPlayRiskManagerCode(linkId, standardMatchInfo.getId(), marketType, categoryId, standardSportMarketSell);
                if (playRiskManagerService.isMtsFamily(code)) {
                    mtsCategoryIds.add(categoryId);
                } else {
                    pandaCategoryIds.add(categoryId);
                }
            }

            if (!CollectionUtils.isEmpty(mtsCategoryIds)) {
                //-------------------MTS操盘，直接下发赔率--------------------
                StopWatch swMTS = new StopWatch(UUID.randomUUID().toString());
                swMTS.start("MTS操盘盘口处理耗时(玩法级分组)");
                processOddsByMts(linkId, standardMatchInfo, mtsCategoryIds, standardCategoryMarketMessageMap, request.getDataSourceTime(), Boolean.TRUE);
                swMTS.stop();
                log.info("::{}::MTS操盘盘口处理耗时{}ms," + swMTS.prettyPrint(), linkId, swMTS.getTotalTimeMillis());
            }

            if (!CollectionUtils.isEmpty(pandaCategoryIds)) {
                //-----------panda操盘，计算和下发逻辑（仅非MTS玩法组）--------------------------
                StopWatch swPanda = new StopWatch(UUID.randomUUID().toString());
                swPanda.start("processOddsByPanda耗时(玩法级分组)");

                Set<Long> pandaCategoryIdSet = new HashSet<>(pandaCategoryIds);
                Set<Long> setAdd = new HashSet<>();
                for (Long marketCategoryId : pandaCategoryIdSet) {
                    if (MarginCategoryConfig.HANDICAP_CATEGORY_SUBSECTION.contains(marketCategoryId)) {
                        Long addCategoryId = MarginCategoryConfig.HANDICAP_WINNER_MAP.get(marketCategoryId);
                        setAdd.add(addCategoryId);
                        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + addCategoryId);
                        Map<String, StandardMarketDataMessage> standardMarketAddCache = redisService.hGetAll(redisKey);
                        if (MapUtils.isNotEmpty(standardMarketAddCache)) {
                            standardCategoryMarketMessageMap.putAll(standardMarketAddCache);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(setAdd)) {
                    //兼容历史数据
                    marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardCategoryMarketMessageMap, standardMatchInfo, setAdd, null, null, null, false);
                    pandaCategoryIdSet.addAll(setAdd);
                }
                processOddsByPanda(request.getLinkId(), request.getOddsSource(), request.getOperaterId(), standardMatchInfo, pandaCategoryIdSet, standardCategoryMarketMessageMap, request.getDataSourceTime(), changeCategoryOddsType, Boolean.TRUE);
                swPanda.stop();
                log.info("::{}::processOddsByPanda耗时{}ms," + swPanda.prettyPrint(), linkId, swPanda.getTotalTimeMillis());
            }
        }
        if (!CollectionUtils.isEmpty(riskCategorySet)) {
            dataMerchantBaffleProducer.sendCategoryListToRiskMQ(linkId + "_riskCategorySet", standardMatchInfo.getId(), thirdMatchInfo.getSportId(), riskCategorySet, 3);
        }
        log.info("::{}::accessMatchMarketData主流程处理耗时{}ms," + swRedis.prettyPrint(), linkId, swRedis.getTotalTimeMillis());

        //检查是否是测试联赛
        if (standardMatchInfo != null) {
            //检查是否有绑定测试赛事
            checkIsTestMatch(linkId, standardMatchInfo, thirdMatchMarketDTO, request, thirdMarketDTOList);
        }
        //统计处理耗时
        paDataServiceLogProducer.sendPaDataServiceLog(getPaDataServiceLogDTO(request.getLinkId(), "odds-admin", "THIRD_MATCH_MARKET_API", "商业数据源赔率主流程", swRedis.getTotalTimeMillis(), 200, null));
    }

    /**
     * 获取赛事操盘模式
     * @param marketType
     * @param preType 赛前
     * @param playType 滚球
     * @return
     */
    private boolean getMatchTradeType(Integer marketType, String preType, String playType) {
        if (marketType == 1 && StringUtils.equals(preType, RiskManagerCodeEnums.MTS.name())) {
            return true;
        }
        if (marketType == 1 && StringUtils.equals(preType, RiskManagerCodeEnums.GTS.name())) {
            return true;
        }
        if (marketType == 1 && StringUtils.equals(preType, RiskManagerCodeEnums.OTS.name())) {
            return true;
        }
        if (marketType == 1 && StringUtils.equals(preType, RiskManagerCodeEnums.CTS.name())) {
            return true;
        }
        if (marketType == 1 && StringUtils.equals(preType, RiskManagerCodeEnums.F2TS.name())) {
            return true;
        }
        if (marketType == 0 && StringUtils.equals(playType, RiskManagerCodeEnums.MTS.name())) {
            return true;
        }
        if (marketType == 0 && StringUtils.equals(playType, RiskManagerCodeEnums.GTS.name())) {
            return true;
        }
        if (marketType == 0 && StringUtils.equals(playType, RiskManagerCodeEnums.OTS.name())) {
            return true;
        }
        if (marketType == 0 && StringUtils.equals(playType, RiskManagerCodeEnums.CTS.name())) {
            return true;
        }
        if (marketType == 0 && StringUtils.equals(playType, RiskManagerCodeEnums.F2TS.name())) {
            return true;
        }
        return false;
    }

	/**
     * 处理1848 赛事玩法赔率最新更新时间监控数据
     * @param linkId
     * @param matchId
     * @param marketCategoryIdSet
     * @param beginTime
     * @param dataSourceTime
     */
    private void processMonitorCategoryMessage(String linkId, Long sportId, Long matchId, Set<Long> marketCategoryIdSet, Long beginTime, Long dataSourceTime, Integer marketType) {
    	Long matchPeriodId = (Long) redisService.get(Constant.REDIS_KEY.RONGHE_MATCH_CURRENT_PERIODID+matchId);
    	if(matchPeriodId == null) {//没有阶段数据的时候，默认为未开赛
    		matchPeriodId = 0l;
    	}
    	standardMarketOddsProducer.thirdCategoryOddsUpdateTimeSend(linkId, sportId, matchId, matchPeriodId, marketCategoryIdSet, dataSourceTime, marketType);
    }

    /**
     * SR/BG/BC 根据三方盘口ID缓存最新数据
     * TX 根据玩法 缓存每个坑位最新数据
     *
     * @param linkId
     * @param thirdMatchMarketDTO
     */
    private void thirdMatchNotExistCacheMarket(String linkId, ThirdMatchMarketDTO thirdMatchMarketDTO, boolean isOutRight) {
        if (isOutRight) {
            return;
        }
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        String dataSourceCode = thirdMatchMarketDTO.getDataSourceCode();
        //缓存KEY
        String thirdMarketKey = Constant.REDIS_KEY.RONGHE_THIRD_MARKET + thirdMatchSourceId + "_" + dataSourceCode;
        for (ThirdMarketDTO thirdMarketDTO : thirdMatchMarketDTO.getMarketList()) {
            //三方玩法源ID
            String thirdMarketCategorySourceId = thirdMarketDTO.getThirdMarketCategorySourceId();
            String thirdMarketSourceId = thirdMarketDTO.getThirdMarketSourceId();
            //TX根据三方玩法 缓存坑位最新数据  Map<三方玩法,Map<坑位,盘口数据>>
            if (DataSourceCodeEnum.TX.code.equals(dataSourceCode)) {
                //TX坑位
                Integer offerLineId = thirdMarketDTO.getOfferLineId();
                Map<Integer, ThirdMarketDTO> categoryPlaceMap = new HashMap<>();
                Object o = redisService.hGet(thirdMarketKey, thirdMarketCategorySourceId);
                if (!Objects.isNull(o)) {
                    categoryPlaceMap = (Map<Integer, ThirdMarketDTO>) o;
                }
                categoryPlaceMap.put(offerLineId, thirdMarketDTO);
                redisService.hSet(thirdMarketKey, thirdMarketCategorySourceId, categoryPlaceMap, RedisConfig.REDIS_HOUR_TIME);
                log.error("::{}::三方赛事不存在,缓存TX盘口,三方赛事数据源ID:{},三方盘口ID:{},玩法:{},坑位:{}",
                        linkId, thirdMatchSourceId, thirdMarketSourceId, thirdMarketCategorySourceId, offerLineId);
            } else {
                //SR/BG/BC 根据三方盘口ID缓存最新数据 Map<三方盘口ID,盘口信息>
                redisService.hSet(thirdMarketKey, thirdMarketSourceId, thirdMarketDTO, RedisConfig.REDIS_HOUR_TIME);
                log.error("::{}::三方赛事不存在,缓存盘口,三方赛事数据源ID:{},三方盘口ID:{}", linkId, thirdMatchSourceId, thirdMarketSourceId);
            }
        }
    }


    /**
     * 此方法保证滚球赛前不会同时存在的前提下
     * 赔率下发主流程入口，主要是针对pa操盘以及mts操盘做区分
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     * @param standardMarketMessageMap
     * @param dataSourceTime
     * @param standardSportMarketSell
     */
    @Async("ProcessOddsByPandaThreadPool")
    public void processOddsByAll(String linkId,int oddsSource,Long operaterId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet,  Map<String, StandardMarketDataMessage> standardMarketMessageMap, Long dataSourceTime,StandardSportMarketSell standardSportMarketSell,Map<Long, List<String>> changeCategoryOddsType)
    {
        if (CollectionUtils.isEmpty(marketCategoryIdSet))
        {
            log.info("::{}::标准赛事id:{},玩法集合为空,赔率不下发,玩法集合:{}", linkId, standardMatchInfo.getId(), JSON.toJSONString(marketCategoryIdSet));
            return;
        }
        if (MapUtils.isEmpty(standardMarketMessageMap))
        {
            log.info("::{}::标准赛事id:{},盘口集合为空,赔率不下发", linkId, standardMatchInfo.getId());
            return;
        }
        marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketMessageMap,standardMatchInfo,marketCategoryIdSet,null,null,null,false);
        AtomicInteger marketType = new AtomicInteger(-1);
        AtomicBoolean isChampionMarket = new AtomicBoolean(Boolean.FALSE);
        standardMatchInfo.setMatchType(0);
        standardMarketMessageMap.forEach((k, v) -> {
            if (v.getMarketType() == 2) {
                isChampionMarket.set(Boolean.TRUE);
                standardMatchInfo.setMatchType(1);
            }
           if (v.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED)
           {
               marketType.set(v.getMarketType());
               return;
           }
        });
        if (isChampionMarket.get())
        {
            processOddsByOutright(linkId, standardMatchInfo, marketCategoryIdSet, standardMarketMessageMap, dataSourceTime, changeCategoryOddsType);
            return;
        }
        // 4405：按玩法级操盘模式分组（MTS组 / 非MTS组），同场支持混合操盘
        Set<Long> mtsCategoryIds = new HashSet<>();
        Set<Long> pandaCategoryIds = new HashSet<>();
        Integer mt = marketType.get();
        for (Long categoryId : marketCategoryIdSet) {
            String code = playRiskManagerService.getPlayRiskManagerCode(linkId, standardMatchInfo.getId(), mt, categoryId, standardSportMarketSell);
            if (playRiskManagerService.isMtsFamily(code)) {
                mtsCategoryIds.add(categoryId);
            } else {
                pandaCategoryIds.add(categoryId);
            }
        }
        if (!CollectionUtils.isEmpty(mtsCategoryIds)) {
            processOddsByMts(linkId+"_XTS", standardMatchInfo, mtsCategoryIds, standardMarketMessageMap, dataSourceTime, Boolean.FALSE);
        }
        if (!CollectionUtils.isEmpty(pandaCategoryIds)) {
            processOddsByPanda(linkId+"_PANDA", oddsSource, operaterId, standardMatchInfo, pandaCategoryIds, standardMarketMessageMap, dataSourceTime, new HashMap<>(), Boolean.FALSE);
        }
    }

    /**
     * 处理操作的盘口进行计算，排序并下发（操盘后台)
     *
     * @param linkId                   链路id
     * @param standardMatchInfo        标准赛事信息
     * @param marketCategoryIdSet      本次要下发的玩法数据
     * @param standardMarketMessageMap 某个数据源的全部缓存原始盘口数据
     * @param dataSourceTime           盘口下发时间
     * @param changeCategoryOddsType   数据源赔率变更记录玩法 - 投注项
     */
    public void processOddsByPanda(String linkId,int oddsSource,Long operaterId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, Map<String, StandardMarketDataMessage> standardMarketMessageMap, Long dataSourceTime, Map<Long, List<String>> changeCategoryOddsType, Boolean isMain) {
        log.info("::{}::标准赛事id:{},本次处理的标准玩法id:{},缓存map集合大小:{},数据源赔率变动数据商:{}", linkId, standardMatchInfo.getId(), marketCategoryIdSet, standardMarketMessageMap.size(), changeCategoryOddsType);
        if (CollectionUtils.isEmpty(marketCategoryIdSet)) {
            log.info("::{}::标准赛事id:{},玩法集合为空,赔率不下发,玩法集合:{}", linkId, standardMatchInfo.getId(), JSON.toJSONString(marketCategoryIdSet));
            return;
        }
        try {
            //计算盘口及排序
            // 至于Id 我觉得给UUID是可行的~
            StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
            //获取盘口的三方玩法
            swCalculate.start("panda操盘获取玩法手自动配置");
            //玩法维度的操盘配置,直接一次性从库查出赛事玩法级的手自动类型，比循环查更快
            Map<Long, Integer> tradeTypeMap = configTradeTypeService.getItemByMatchAndCategorys(standardMatchInfo.getId().toString(), marketCategoryIdSet);
            swCalculate.stop();
            swCalculate.start("panda操盘全部盘口计算耗时");
            //过滤出需要下发的盘口
            List<StandardMarketDataMessage> collect = standardMarketMessageMap.values().stream().filter(e -> marketCategoryIdSet.contains(e.getMarketCategoryId())).collect(Collectors.toList());
            //--------------从缓存中取A+操盘的盘口（仅篮球有A+）------------
            List<StandardMarketDataMessage> standardMarketMessageListAUTO_PLUS = new ArrayList<>();//collect.stream().filter(e -> Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(tradeTypeMap.get(e.getMarketCategoryId()))).collect(Collectors.toList());
            Map<Long, List<StandardMarketDataMessage>> collectAUTO_PLUS = collect.stream().filter(e -> Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(tradeTypeMap.get(e.getMarketCategoryId()))).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
            setOddsMetricAndLowOddsForAUTO_PLUS(linkId, standardMatchInfo, collectAUTO_PLUS, standardMarketMessageListAUTO_PLUS);
            //标准盘口初盘A+
            standardFirstMarketBallHead(linkId, standardMatchInfo, standardMarketMessageListAUTO_PLUS);
            //构建下发给下游的list集合
            List<StandardMarketMessage> standardMarketMessageSendListAUTO_PLUS = new ArrayList<>();
            //封装为可投递的StandardMarketMessage
            standardMarketMessageListAUTO_PLUS.forEach(standardMarketDataMessage -> {
                //A+模式下，附加字段1需要特殊处理，不然会出现xx.00
                if (standardMatchInfo.getSportId() == 2) {
                    //根据赛事、玩法id查询盘口差配置数据
                    if (StringUtils.isNotBlank(standardMarketDataMessage.getAddition1())
                            && MarginCategoryConfig.BASKETBALL_MY_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())) {
                        Double addition1 = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition1())).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                        standardMarketDataMessage.setAddition1(String.valueOf(addition1).replace(".0", ""));
                    }
                    //A+模式 下发原始盘口值 附加字段5
                    if (MarginCategoryConfig.CATEGORY_ORIGINAL_BALL.contains(standardMarketDataMessage.getMarketCategoryId())) {
                        standardMarketDataMessage.setAddition5(standardMarketDataMessage.getAddition1());
                    }
                }
                //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
                StandardMarketMessage standardMarketMessage = convertStandardMarketMessage(linkId, standardMarketDataMessage, standardMatchInfo.getOperateMatchStatus(), true, false, new HashMap<>());
                standardMarketMessage.setTradeType(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS);
                if (StringUtils.equals(String.valueOf(standardMarketMessage.getPlaceNum()), "1")) {
                    //篮球主玩法a+ 关转封
                    if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.getCode())
                            && MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())
                            && standardMarketMessage.getMarketType() == 0
                            && standardMarketMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)
                            && !CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())
                            && standardMarketMessage.getMarketSource() != 1) {
                        standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketMessage.getMarketOddsList().forEach(e -> {
                            // 将Pa赔率设置为抽水赔率
                            if (null == e.getOddsValue() || 0 == e.getOddsValue()){
                                e.setPaOddsValue(e.getOriginalOddsValue());
                            }else {
                                e.setPaOddsValue(e.getOddsValue());
                            }
                            e.setActive(1);
                        });
                    }
                    standardMarketMessageSendListAUTO_PLUS.add(standardMarketMessage);
                    marketOddsPlaceProcessor.setLastMarketMessage(linkId, standardMatchInfo, standardMarketMessage);
                }
            });
            //-------------从缓存中取A操盘的盘口------------
            List<StandardMarketDataMessage> collectAUTO = collect.stream().filter(e -> {
                Integer tradeType = 0;
                if (tradeTypeMap.get(e.getMarketCategoryId()) != null) {
                    tradeType = tradeTypeMap.get(e.getMarketCategoryId());
                }
                if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeType)) {
                    return true;
                }
                log.info("::{}::标准赛事id:{},盘口id:{},统一盘口id:{},标准玩法id:{},三方盘口源id:{},M和A+模式不下发赔率,操盘类型:{}",
                        linkId, standardMatchInfo.getId(), e.getId(), e.getRelationMarketId(), e.getMarketCategoryId(), e.getThirdMarketSourceId(), tradeType);
                return false;
            }).collect(Collectors.toList());
            List<StandardMarketDataMessage> standardMarketDataMessagesAUTO = processPandaCalculate(linkId, standardMatchInfo, marketCategoryIdSet, collectAUTO);
            swCalculate.stop();
            //构建下发给下游的list集合
            List<StandardMarketMessage> standardMarketMessageSendListAUTO = new ArrayList<>();
            swCalculate.start("panda操盘获取盘口赔率最大最小值");
            //获取该赛事的所有盘口位置最大最小值,一次获取比循环获取快
            Map<String, ConfigMarketTradeItem> configMarketTradeItemMap = configMarketTradeItemService.getItemByMatchAndCategorys(standardMatchInfo.getId(), marketCategoryIdSet);
            swCalculate.stop();
            swCalculate.start("综合操盘获取赛事联赛最大最小值");
            //综合操盘获取赛事联赛最大最小值
            int marketType = isOddsLive(standardMatchInfo.getId());
            ConfigTournamentTradeItem tournamentTradeItem = configTournamentTradeItemService.getItem(standardMatchInfo.getSportId(), standardMatchInfo.getStandardTournamentId(), marketType);
            swCalculate.stop();
            swCalculate.start("panda操盘全部盘口开关封锁及合法性校验");
            Set<Long> riskCategorySet = new HashSet();
            //TX 位置不同 相同盘口 融合处理
            txMarketMerge(linkId, standardMatchInfo, standardMarketDataMessagesAUTO);
            //AO坑位处理
            aoMarketPlaceMerge(linkId, standardMatchInfo, standardMarketDataMessagesAUTO, true);
            //球头下发给AO
            thirdMarketBallHeadProcessor.sendBallHeadAo(linkId, standardMatchInfo, standardMarketDataMessagesAUTO, dataSourceTime);
            //标准盘口初盘
            standardFirstMarketBallHead(linkId, standardMatchInfo, standardMarketDataMessagesAUTO);
            basketballOriginalOddsLimitProcessor.basketballOriginalOdds(linkId,standardMatchInfo,standardMarketDataMessagesAUTO);
            for (StandardMarketDataMessage marketDataMsg : standardMarketDataMessagesAUTO) {
                //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
                StandardMarketMessage standardMarketMessage = convertStandardMarketMessage(linkId, marketDataMsg, standardMatchInfo.getOperateMatchStatus(), false, false, changeCategoryOddsType);
                standardMarketMessage.setTradeType(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO);
                //赔率合法性校验
                marketOddsVerify(linkId, standardMatchInfo, configMarketTradeItemMap, standardMarketMessage, tournamentTradeItem);
                //篮球、让球玩法特殊的盘口值
                processHandicapCategory(linkId, standardMatchInfo, standardMarketMessage, riskCategorySet);
                if (MarginCategoryConfig.BASKETBALL_PLAYER_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())) {
                    processPlayerTeamFlag(linkId, standardMatchInfo, standardMarketMessage);
                }
                //球员玩法多语言name_code处理
                processPlayerNameCode(linkId, standardMatchInfo.getSportId(), standardMarketMessage);
                //赔率优化(两项盘小数位优化)
                processOddsValueDecimals(linkId, standardMarketMessage, standardMatchInfo);
                //球头优化(去掉 .0的小数位)
                standardMarketMessage = ballHeadsRemoveDecimal(standardMarketMessage);
                //网球、乒乓球 球头校验
                ballVerify(linkId, marketType, standardMatchInfo, standardMarketMessage);
                marketOddsPlaceProcessor.setLastMarketMessage(linkId, standardMatchInfo, standardMarketMessage);
                standardMarketMessageSendListAUTO.add(standardMarketMessage);
            }
            //处理投注项赔率设置
            configMarketOddsStatusService.processConfigOddsValue(linkId, standardMarketMessageSendListAUTO, standardMatchInfo);
            swCalculate.stop();

        //-------------赔率下发-----------------
        if (!CollectionUtils.isEmpty(standardMarketMessageSendListAUTO)) {
            //盘口状态校验
            //standardMarketStatusCheck(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            if (footballMarketValidateService.shouldValidateFootball(standardMatchInfo)) {
                standardMarketMessageSendListAUTO = footballMarketValidateService.validateFootball(linkId,
                                                               standardMatchInfo,
                                                               standardMarketMessageSendListAUTO,
                                                               MarketHandlingEnum.PANDA);
            } else {

                // 数据商全封和全关判断
                transformStatIfSatisfyCond(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
                //开盘比分校验
                checkMarketsSoreProcessor.check(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
                //盘口开关封锁 按开关优先级 (A > B > C )调整盘口状态
                dealMarketStatusProcessor.dealMarketStatusList(linkId,
                                                               standardMarketMessageSendListAUTO,
                                                               standardMatchInfo);
                //篮球自动开盘玩法
                processAutoOpenMarketCategory(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);

                checkMarketOddsByAddtion1(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
                //盘口时间戳校验
                //standardMarketVerifyModifyTime(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
                //盘口坑位时间戳校验
                //standardMarketPlaceVerifyModifyTime(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);

                //滚球阶段关闭赛前盘兜底
                closePreByLive(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);

                //最后下发赔率 ，自动关盘兜底
                //automaticClosing(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            }
            // 特殊玩法兜底
            specialClosing(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            //波胆赔率联动
            correctScorelOddsLinkageProcessor.oddsLinkage(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            //检查标准数据源与赔率盘口数据源
            standardDataSourceCodeCheck(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            saveTheLastMarketOddsToReids(linkId, standardMatchInfo, marketCategoryIdSet, standardMarketMessageSendListAUTO, dataSourceTime, isMain);
            standardMatchMarketOddsLinkageProcessor.matchMarketOddsMainLinkage(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            //standardMarketOriginalCheckProcessor.standardMarketOriginalProcessor(linkId, standardMatchInfo);
            //融合M模式子玩法下发
            addStandardMarketM(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            standardMarketPASort.sort(linkId, standardMatchInfo, standardMarketMessageSendListAUTO, marketType, false);
            checkMarketsSoreProcessor.checkStandardMarketOddsValuse(linkId,standardMatchInfo,standardMarketMessageSendListAUTO);
            thirdMarketStatusProcess(standardMatchInfo,standardMarketMessageSendListAUTO);
            //最后下发赔率 ，自动关盘兜底
            automaticClosing(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            saveTheLastAMarketOddsToReids(linkId, standardMatchInfo, marketCategoryIdSet, standardMarketMessageSendListAUTO, dataSourceTime, isMain);
            dataSourceTime = checkMarketsModifytime(linkId, standardMatchInfo, marketCategoryIdSet, standardMarketMessageSendListAUTO, dataSourceTime, isMain);
            //计算关盘盘口的马来赔率
            //setDeactivatedMayOdds(linkId, standardMatchInfo, marketCategoryIdSet, standardMarketMessageSendListAUTO, dataSourceTime, isMain);
            if (!standardMarketMessageSendListAUTO.isEmpty())
                standardMarketOddsProducer.standardMarketOddsAsyncSend(linkId,oddsSource,operaterId, standardMatchInfo, standardMarketMessageSendListAUTO, dataSourceTime, false);
        }
        if (!CollectionUtils.isEmpty(standardMarketMessageSendListAUTO_PLUS)) {
            //篮球自动开盘玩法
            processAutoOpenMarketCategory(linkId, standardMatchInfo, standardMarketMessageSendListAUTO_PLUS);
            //滚球阶段关闭赛前盘兜底
            closePreByLive(linkId, standardMatchInfo, standardMarketMessageSendListAUTO_PLUS);
            //最后下发赔率 ，自动关盘兜底
            automaticClosing(linkId, standardMatchInfo, standardMarketMessageSendListAUTO_PLUS);
            standardDataSourceCodeCheck(linkId, standardMatchInfo, standardMarketMessageSendListAUTO_PLUS);
            thirdMarketStatusProcess(standardMatchInfo,standardMarketMessageSendListAUTO_PLUS);
            saveTheLastAMarketOddsToReids(linkId, standardMatchInfo, marketCategoryIdSet, standardMarketMessageSendListAUTO_PLUS, dataSourceTime, isMain);
            standardMarketOddsProducer.standardMarketOddsRiskAsyncSend(linkId, standardMatchInfo, standardMarketMessageSendListAUTO_PLUS, dataSourceTime);
        }
        if (!CollectionUtils.isEmpty(riskCategorySet)) {
            log.info("::{}::标准赛事id:{} 玩法:{}特殊的球头±0.5,独赢玩法自动封盘", linkId, standardMatchInfo.getId(), riskCategorySet);
            dataMerchantBaffleProducer.sendCategoryListToRiskMQ(linkId + "_riskCategorySet", standardMatchInfo.getId(), standardMatchInfo.getSportId(), riskCategorySet, 11);
        }
        log.info("::{}::panda操盘全部盘口计算耗时{}ms," + swCalculate.prettyPrint(), linkId, swCalculate.getTotalTimeMillis());

        }catch (Exception e ){
            e.printStackTrace();
            log.error(linkId + "::processOddsByPanda,出现异常", e);
        }
    }

    /**
     * 101470
     *针对足球玩法，（早滚都需要）
     *
     * 1、所有次要的足球玩法（除所有主玩法外），数据源非开，做关盘处理（客户端不展示，操盘后台源封但是是关盘），数据源打开自动开，不需要人为手动开；（47319场景）
     *
     * 2、针对两项盘玩法（除所有主玩法外），有一个投注项数据源非开，都做关盘处理（客户端不展示，操盘后台源封但是是关盘），数据源打开自动开，不需要人为手动开。
      */
    private void thirdMarketStatusProcess(StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList){
        if (standardMarketMessageList.isEmpty()
            ||!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())){
            return;
        }
        standardMarketMessageList.stream().forEach(e->{
            if (shouldSkipThirdMarketStatusProcess(standardMatchInfo, e)) {
                return;
            }
            if (e.getThirdMarketSourceStatus() != 0 && !MarginCategoryConfig.FootBall_MAIN3484_CATEGORY.contains(e.getMarketCategoryId()) && e.getMarketCategoryId()!=133L){
                e.setStatus(2);
                e.setPaStatus(2);
                e.setPlaceNumStatus(2);
                e.setThirdMarketSourceStatus(1);
                e.setPaStatusReason(e.getPaStatusReason()+","+"101470");
            }
        });
    }

    private static final String PRE_LIVE_CLOSE_REMARK = "关闭赛前盘兜底";

    /**
     * 自动关盘、早转滚关盘等场景，101470 不再改写为源封
     */
    private boolean shouldSkipThirdMarketStatusProcess(StandardMatchInfo standardMatchInfo, StandardMarketMessage market) {
        return isAutoClosedMarket(standardMatchInfo, market) || isPreLiveClosedMarket(standardMatchInfo, market);
    }

    /**
     * 玩法已触发自动关盘，101470 不再改写三方数据源状态
     */
    private boolean isAutoClosedMarket(StandardMatchInfo standardMatchInfo, StandardMarketMessage market) {
        if (Constant.AOTU_CLOSE_STATUS.equals(market.getAutoCloseStatus())) {
            return true;
        }
        if (StringUtils.isNotBlank(market.getRemark())
                && market.getRemark().contains(MergeMarketStatusEnum.AUTO_CLOSE.name())) {
            return true;
        }
        Long matchId = standardMatchInfo.getId();
        if (autoCloseCacheService.autoClose(matchId, market.getMarketCategoryId())) {
            return true;
        }
        return market.getChildMarketCategoryId() != null
                && autoCloseCacheService.autoClose(matchId, market.getChildMarketCategoryId());
    }

    /**
     * 早转滚 / 滚球阶段关闭的赛前盘，101470 不再改写为源封
     */
    private boolean isPreLiveClosedMarket(StandardMatchInfo standardMatchInfo, StandardMarketMessage market) {
        if (StringUtils.isNotBlank(market.getRemark()) && market.getRemark().contains(PRE_LIVE_CLOSE_REMARK)) {
            return true;
        }
        if (!Objects.equals(market.getMarketType(), Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS)) {
            return false;
        }
        if (isOddsLive(standardMatchInfo.getId()) != 0) {
            return false;
        }
        return Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(market.getStatus())
                || Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(market.getThirdMarketSourceStatus());
    }

    /**
     * 特殊玩法关盘兜底
     */
    private void specialClosing(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList){
        if (standardMarketMessageList.isEmpty()){
            return;
        }
        com.panda.merge.dto.FootballCacheScores footballCacheScores = footballScoreCacheService.getCache(linkId, standardMatchInfo.getId());
        for(StandardMarketMessage standardMarketMessage : standardMarketMessageList){
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            if ((standardMarketMessage.getStatus()==0||standardMarketMessage.getStatus() == 1)
                && MarginCategoryConfig.SPECIAL_CATEGORY_CLOSING.contains(marketCategoryId)
                && footballCacheScores!=null){
                CommonItem goal = footballCacheScores.getGoal();
                if (goal.getAway()!=null && goal.getAway() > 0
                     &&goal.getHome() !=null && goal.getHome() > 0){
                    standardMarketMessage.oldClose("当两队都进球时，关盘");
                }
            }
        }
    }
    /**
     * 把下发到业务的最新的盘口赔率全部缓存，开关封锁时不需要计算赔率
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     * @param standardMarketMessageSendListAUTO
     * @param dataSourceTime
     */
    public void saveTheLastMarketOddsToReids(String linkId,StandardMatchInfo standardMatchInfo,Set<Long> marketCategoryIdSet,List<StandardMarketMessage> standardMarketMessageSendListAUTO,Long dataSourceTime,Boolean isMain)
    {
        if (!CollectionUtils.isEmpty(marketCategoryIdSet))
        {
            Map<Long,List<StandardMarketMessage>> listMap = standardMarketMessageSendListAUTO.stream().
                    filter(e->marketCategoryIdSet.contains(e.getMarketCategoryId())).
                    collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));

            for (Map.Entry<Long, List<StandardMarketMessage>> entry : listMap.entrySet())
            {
                Long marketCategoryId = entry.getKey();
                List<StandardMarketMessage> standardMarketMessageList = entry.getValue();
                String redisDateKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS_DATE + standardMatchInfo.getId()+"_"+marketCategoryId);
                Object oldTime = redisService.get(redisDateKey);
                //时间戳校验只对主流程数据商赔率校验并入缓存，操盘请求过来不校验
                if(oldTime == null || !isMain || (isMain && ((Long) oldTime) <= dataSourceTime))
                {
                    if (isMain) {
                        redisService.set(redisDateKey, dataSourceTime, RedisConfig.REDIS_MY_TIME);
                    }
                    String redisOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS+standardMatchInfo.getId());
                    redisService.hSet(redisOddsKey, String.valueOf(marketCategoryId), standardMarketMessageList, marketCacheTime(standardMatchInfo.getBeginTime()));
                }
                else
                {
                    log.info("::{}::开始缓存下发的最新的盘口赔率数据，赛事ID：{}，玩法ID：{},不缓存。", linkId, standardMatchInfo.getId(), marketCategoryId);
                }
            }
        }
    }

    /**
     * 把下发到业务的最新的盘口赔率全部缓存，开关封锁时不需要计算赔率
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     * @param standardMarketMessageSendListAUTO
     * @param dataSourceTime
     */
    public void saveTheLastAMarketOddsToReids(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, List<StandardMarketMessage> standardMarketMessageSendListAUTO, Long dataSourceTime, Boolean isMain) {
        if (!CollectionUtils.isEmpty(marketCategoryIdSet)) {
            Map<Long, List<StandardMarketMessage>> listMap = standardMarketMessageSendListAUTO.stream().filter(e -> marketCategoryIdSet.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));

            for (Map.Entry<Long, List<StandardMarketMessage>> entry : listMap.entrySet()) {
                Long marketCategoryId = entry.getKey();
                List<StandardMarketMessage> standardMarketMessageList = entry.getValue();
                String redisDateKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_A_MARKETODDS_DATE + standardMatchInfo.getId() + "_" + marketCategoryId);
                Object oldTime = redisService.get(redisDateKey);
                String redisOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_A_MARKETODDS + standardMatchInfo.getId());
                redisService.hSet(redisOddsKey, String.valueOf(marketCategoryId), standardMarketMessageList, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
        }
    }



    public void setDeactivatedMayOdds(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, List<StandardMarketMessage> standardMarketMessageSendListAUTO, Long dataSourceTime, Boolean isMain) {
        if (isMain){
            for (StandardMarketMessage standardMarketMessage : standardMarketMessageSendListAUTO){
                if (standardMarketMessage.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)
                        && (
                        MarginCategoryConfig.MY_ODDS_GRACEFUL_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())
                                || MarginCategoryConfig.COMPLEX_MY_CATEGORY_ODDS_VERIFY.contains(standardMarketMessage.getMarketCategoryId())
                )){
                    List<StandardMarketOddsMessage> odds = standardMarketMessage.getMarketOddsList();
                    if (odds!=null&& !odds.isEmpty()){
                        for (StandardMarketOddsMessage standardMarketOddsMessage : odds){
                            if (standardMarketOddsMessage.getPaOddsValue()!=null && standardMarketOddsMessage.getPaOddsValue() > 101000){
                                standardMarketOddsMessage.setMalayOddsValue(initializeComponent.getConvertEuropeToMalay(standardMarketOddsMessage.getPaOddsValue()));
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 盘口下发之前检查标准盘口时间戳跟request时间戳，防止下游丢弃不该丢弃的盘口数据
     * 校验标准盘口时间戳是否最新，如果是最新必须保证request时间戳最新，不然下游会丢弃
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     * @param standardMarketMessageSendListAUTO
     * @param dataSourceTime
     */
    public Long checkMarketsModifytime(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, List<StandardMarketMessage> standardMarketMessageSendListAUTO, Long dataSourceTime, Boolean isMain) {

        List<StandardMarketMessage> deleteList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(standardMarketMessageSendListAUTO)) {
            for (StandardMarketMessage entry : standardMarketMessageSendListAUTO) {
                if (entry.getMarketCategoryId()==335L){
                    String redisOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS_TIME +"requesttime"+ standardMatchInfo.getId()+"335-"+entry.getPlaceNum());
                    String redisOddsKey2 = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS_TIME +"modifytime"+  standardMatchInfo.getId()+"335-"+entry.getPlaceNum());
                    if (redisService.setIfGreater(linkId,entry.getRelationMarketId().toString(),redisOddsKey,dataSourceTime,RedisConfig.REDIS_MY_TIME)
                            &&!redisService.setIfGreater(linkId,entry.getRelationMarketId().toString(),redisOddsKey2,entry.getModifyTime(),RedisConfig.REDIS_MY_TIME)){
                        deleteList.add(entry);
                        log.info("::{}::checkMarketsModifytime,标准盘口时间戳校验，盘口id:{},标准盘口时间戳校验不通过，该盘口数据不下发", linkId,entry.getRelationMarketId());
                        continue;
                    }
                    if (redisService.setIfGreater(linkId,entry.getRelationMarketId().toString(),redisOddsKey2,entry.getModifyTime(),RedisConfig.REDIS_MY_TIME)
                            &&!redisService.setIfGreater(linkId,entry.getRelationMarketId().toString(),redisOddsKey,dataSourceTime,RedisConfig.REDIS_MY_TIME)){
                        dataSourceTime = System.currentTimeMillis();
                        redisService.setIfGreater(linkId,entry.getRelationMarketId().toString(),redisOddsKey,dataSourceTime,RedisConfig.REDIS_MY_TIME);
                    }
                }
            }
        }
        if (!deleteList.isEmpty()){
            standardMarketMessageSendListAUTO.removeAll(deleteList);
        }
        return dataSourceTime;
    }

    /**
     * 提升操盘性能，不走赔率计算
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     * @param dataSourceTime
     */
    @Async("ProcessCategoryStatusThreadPool")
    public void processByRedis(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, Long dataSourceTime) {
        if (!CollectionUtils.isEmpty(marketCategoryIdSet)) {
            List<StandardMarketMessage> standardMarketMessages = new ArrayList<>();
            String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
            List<String> marketCategorysKey = marketCategoryIdSet.stream().map(marketCategoryId -> marketCategoryId + "").collect(Collectors.toList());
            List<Object> obj = redisService.hMulGet(redisKey, marketCategorysKey);
            obj.forEach(o -> {
                if (null != o) {
                    List<StandardMarketMessage> list = (List<StandardMarketMessage>) o;
                    standardMarketMessages.addAll(list);
                }
            });
            if (!CollectionUtils.isEmpty(standardMarketMessages)) {
                int liveFlag = isOddsLive(standardMatchInfo.getId());
                log.info("::{}::processByRedis,异步执行开始", linkId);
                Object a01ExtendedTimeObjects  = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + standardMatchInfo.getId());
                Integer a01ExtendedTimeStatus = 0;
                if (!Objects.isNull(a01ExtendedTimeObjects)) {
                    a01ExtendedTimeStatus = (Integer) a01ExtendedTimeObjects;
                }
                //玩法分组
                Map<Long, List<StandardMarketMessage>> standardMarketMessageMap = standardMarketMessages.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
                for (Map.Entry<Long, List<StandardMarketMessage>> standardMarketMessagesEntry : standardMarketMessageMap.entrySet()) {
                    Long marketCategoryId = standardMarketMessagesEntry.getKey();
                    log.info("::{}::processByRedis,异步执行开始,玩法：{}", linkId, marketCategoryId);
                    List<StandardMarketMessage> marketMessages = standardMarketMessagesEntry.getValue();
                    //自动关盘玩法
                    String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
                    Object autoCloseMap = redisService.hGet(autoCloseRedisKey, marketCategoryId.toString());
                    //查询玩法对应数据源
                    String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + isOddsLive(standardMatchInfo.getId());
                    Map<String, String> changeCategoryMap = redisService.hGetAll(categoryRedisKey);
                    for (StandardMarketMessage standardMarketMessage : marketMessages) {
                        //盘口兜底
                        cacheMarketTimeClose(linkId, liveFlag, standardMatchInfo, standardMarketMessage, autoCloseMap, changeCategoryMap,a01ExtendedTimeStatus);
                    }
                    log.info("::{}::processByRedis,异步执行开始,玩法：{},盘口兜底完成", linkId, marketCategoryId);

                    //融合M模式子玩法下发
                    addStandardMarketM(linkId, standardMatchInfo, marketMessages);
                    if (StandardMatchMarketOddsLinkageProcessor.CATEGORY.contains(marketCategoryId)) {
                        standardMatchMarketOddsLinkageProcessor.matchMarketOddsMainLinkage(linkId, standardMatchInfo, marketMessages);
                    }
                    log.info("::{}::processByRedis,异步执行开始,玩法：{},融合M模式子玩法下发完成", linkId, marketCategoryId);
                }
                log.info("::{}::processByRedis,异步执行完成,下发", linkId);
                //盘口状态
//                standardMarketStatusCheck(linkId, standardMatchInfo, standardMarketMessages);
                log.info("::{}::processByRedis,异步执行开始，盘口状态完成", linkId);
                //开关封锁逻辑判断处理
                List<StandardMarketMessage> finalMessages = standardMarketMessages;
                if (footballMarketValidateService.shouldValidateFootball(standardMatchInfo)) {
                    finalMessages = footballMarketValidateService.validateFootball(linkId,
                                                                                                standardMatchInfo,
                                                                   standardMarketMessages, MarketHandlingEnum.REDIS);
                } else {

                    dealMarketStatusProcessor.dealMarketStatusList(linkId, finalMessages, standardMatchInfo);
                }
                log.info("::{}::processByRedis,异步执行开始,开关封锁逻辑判断处理完成", linkId);
                checkMarketsSoreProcessor.checkStandardMarketOddsValuse(linkId,standardMatchInfo,finalMessages);
                standardMarketOddsProducer.standardMarketOddsAsyncSend(linkId, standardMatchInfo, finalMessages, dataSourceTime, false);
            }
        }
    }

    /**
     * 盘口时间校验，超过阈值关盘 ，主流程不参与排序
     * @param linkId
     * @param standardMatchInfo
     * @param marketDataMessage
     */
    public void marketTimeClose(String linkId, Integer liveFlag, StandardMatchInfo standardMatchInfo, StandardMarketDataMessage marketDataMessage) {
        if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())
                && Constant.WARNING_DATA_SOURCE_CODE.contains(marketDataMessage.getDataSourceCode())
                && liveFlag == 0
                && !Constant.FOOT_BALL_PERIOD_FILTER_WARNING.contains(standardMatchInfo.getMatchPeriodId())
                && MarginCategoryConfig.TWO_NO_UPDATE.contains(marketDataMessage.getMarketCategoryId())
                && marketDataMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            if(DataSourceCodeEnum.BG.code.equals(marketDataMessage.getDataSourceCode())
                    && MarginCategoryConfig.FLAT_HANDICAP_DISPOSE.contains(marketDataMessage.getAddition2())){
                log.info("::{}::时间戳兜底,盘口id:{},时间戳兜底平盘不处理关盘", linkId, marketDataMessage.getRelationMarketId());
                return;
            }
            Long warningTime = 180000L;
            log.info("::{}::时间戳兜底,盘口id:{},当前时间：{}，盘口时间：{},告警时间：{}，最终：{}", linkId, marketDataMessage.getRelationMarketId(), System.currentTimeMillis(), marketDataMessage.getModifyTime(),
                    warningTime, System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime);
            if (System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime) {
                log.info("::{}::时间戳兜底,盘口id:{},时间戳兜底关盘：{}", linkId, marketDataMessage.getRelationMarketId(), System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime);
                marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setRemark("时间戳兜底关盘");
            }
        }
    }
    /**
     * 盘口时间校验，超过阈值关盘
     * @param linkId
     * @param standardMatchInfo
     * @param marketDataMessage
     */
    public void cacheMarketTimeClose(String linkId, Integer liveFlag, StandardMatchInfo standardMatchInfo, StandardMarketMessage marketDataMessage, Object autoCloseMap,
                                     Map<String, String> changeCategoryMap, Integer a01ExtendedTimeStatus) {
        if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())
                && Constant.WARNING_DATA_SOURCE_CODE.contains(marketDataMessage.getDataSourceCode())
                && liveFlag == 0
                && !Constant.FOOT_BALL_PERIOD_FILTER_WARNING.contains(standardMatchInfo.getMatchPeriodId())
                && MarginCategoryConfig.TWO_NO_UPDATE.contains(marketDataMessage.getMarketCategoryId())
                && marketDataMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            if(DataSourceCodeEnum.BG.code.equals(marketDataMessage.getDataSourceCode())
                    && MarginCategoryConfig.FLAT_HANDICAP_DISPOSE.contains(marketDataMessage.getAddition2())){
                log.info("::{}::时间戳兜底,盘口id:{},时间戳兜底平盘不处理关盘", linkId, marketDataMessage.getId());
                return;
            }
            Long warningTime = 180000L;
            log.info("::{}::标准时间戳兜底,盘口id:{},当前时间：{}，盘口时间：{},告警时间：{}，最终：{}", linkId, marketDataMessage.getId(), System.currentTimeMillis(), marketDataMessage.getModifyTime(),
                    warningTime, System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime);
            if (System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime) {
                log.info("::{}::标准时间戳兜底,盘口id:{},时间戳兜底关盘：{}", linkId, marketDataMessage.getId(), System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime);
                marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setRemark("标准时间戳兜底关盘");
                return;
            }
        }
        //滚球阶段关闭赛前盘兜底
        if (0 == liveFlag && marketDataMessage.getMarketType() == 1 && marketDataMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            marketDataMessage.setRemark("关闭赛前盘兜底");
            log.info("::{}::赛事ID:{},三方盘口ID:{},标准盘口ID:{},关闭赛前盘兜底。", linkId, standardMatchInfo.getId(), marketDataMessage.getThirdMarketSourceId(), marketDataMessage.getId());
            return;
        }
        //自动关盘玩法兜底
        if (!Objects.isNull(autoCloseMap)) {
            if (a01ExtendedTimeStatus == 1 && marketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.AO.code) && MarginCategoryConfig.A01_EXTENDED_TIME_CATEGORY.contains(marketDataMessage.getMarketCategoryId())) {
            } else {
                marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setEndEdStatus(0);
                marketDataMessage.setRiskStatus(2);
                log.info("::{}::automaticClosing关盘兜底,三方盘口：{}，标准盘口：{}", linkId, marketDataMessage.getThirdMarketSourceId(), marketDataMessage.getId());
                return;
            }
        }
        //最后下发赔率 ，检查标准数据源与赔率盘口数据源
        if (MapUtil.isNotEmpty(changeCategoryMap)) {
            Long marketCategoryId = marketDataMessage.getMarketCategoryId();
            //篮球 足球 主玩法不判断
            if ((standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.getCode()) && MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY.contains(marketCategoryId))
                    || (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode()) && MarginCategoryConfig.FootBall_MAIN3484_CATEGORY.contains(marketCategoryId))) {
                return;
            }
            if (null != changeCategoryMap.get(String.valueOf(marketCategoryId))) {
                if (!marketDataMessage.getDataSourceCode().equals(changeCategoryMap.get(String.valueOf(marketCategoryId)))) {
                    marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::standardDataSourceCodeCheck标准数据源不匹配关盘,三方盘口：{}，标准盘口：{}", linkId, marketDataMessage.getThirdMarketSourceId(), marketDataMessage.getId());
                }
            } else {
                marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::standardDataSourceCodeCheck标准数据源不匹配关盘,三方盘口：{}，标准盘口：{}", linkId, marketDataMessage.getThirdMarketSourceId(), marketDataMessage.getId());
            }
            //波胆
            Long mainMarketCategoryId = MarginCategoryConfig.A01_MARGIN_CATEGORY_CHEACK.get(marketCategoryId);
            if (null != mainMarketCategoryId) {
                if (null != changeCategoryMap.get(mainMarketCategoryId) && !DataSourceCodeEnum.AO.code.equals(changeCategoryMap.get(String.valueOf(mainMarketCategoryId)))) {
                    marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::standardDataSourceCodeCheck主玩法不是a01进行关盘,三方盘口：{}，标准盘口：{}", linkId, marketDataMessage.getThirdMarketSourceId(), marketDataMessage.getId());
                }
            }
            return;
        }
    }


    /**
     * 提升操盘性能，不走赔率计算
     * @param linkId
     * @param standardMatchInfos
     * @param marketCategoryId
     */
    public void processByRedis(String linkId,List<StandardMatchInfo> standardMatchInfos,Long marketCategoryId)
    {
        if (!CollectionUtils.isEmpty(standardMatchInfos) && marketCategoryId != null && marketCategoryId.longValue() != 0)
        {
        	String marketRedisKey = null;
        	String redisKey = null;
            for (StandardMatchInfo standardMatchInfo : standardMatchInfos)
            {
                redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
                Object obj = redisService.hGet(redisKey,marketCategoryId.toString());
                if (ObjectUtil.isNotEmpty(obj))
                {
                    List<StandardMarketMessage> list = (List<StandardMarketMessage>)obj;
                    for (StandardMarketMessage standardMarketMessage : list)
                    {
                    	dealMarketDeactivated(linkId, standardMarketMessage, standardMatchInfo);
                        marketRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketMessage.getDataSourceCode()+"_"+standardMarketMessage.getMarketCategoryId());
                        redisService.hDel(marketRedisKey,standardMarketMessage.getId().toString());
                    }
                    standardMarketOddsProducer.standardMarketOddsAsyncSend(linkId+"_"+standardMatchInfo.getId(), standardMatchInfo, list, System.currentTimeMillis(),false);
                    redisService.hDel(redisKey,marketCategoryId.toString());
                }
            }
            log.info("::{}::玩法关盘逻辑处理完成，赛事数量：{}，玩法ID：{}",
                    linkId,standardMatchInfos.size(),marketCategoryId);
        }
        log.info("::{}::玩法关盘逻辑结束，玩法ID：{}",linkId,marketCategoryId);
    }

    /**
     * 提升操盘性能，不走赔率计算(这里只操作AO玩法状态)
     * @param linkId
     * @param standardMatchInfos
     * @param marketCategoryId
     */
    public void processAoByRedis(String linkId,List<StandardMatchInfo> standardMatchInfos,Long marketCategoryId)
    {
        if (!CollectionUtils.isEmpty(standardMatchInfos) && marketCategoryId != null && marketCategoryId.longValue() != 0)
        {
            String marketRedisKey = null;
            String redisKey = null;
            for (StandardMatchInfo standardMatchInfo : standardMatchInfos)
            {
                redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
                Object obj = redisService.hGet(redisKey,marketCategoryId.toString());
                if (ObjectUtil.isNotEmpty(obj))
                {
                    List<StandardMarketMessage> list = (List<StandardMarketMessage>)obj;
                    if (list.get(0).getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.AO.code))
                    {
                        for (StandardMarketMessage standardMarketMessage : list)
                        {
                            dealMarketDeactivated(linkId, standardMarketMessage, standardMatchInfo);
                            marketRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketMessage.getDataSourceCode() + "_" + standardMarketMessage.getMarketCategoryId());
                            redisService.hDel(marketRedisKey,standardMarketMessage.getId().toString());
                        }
                        standardMarketOddsProducer.standardMarketOddsAsyncSend(linkId+"_"+standardMatchInfo.getId(), standardMatchInfo, list, System.currentTimeMillis(),false);
                        redisService.hDel(redisKey,marketCategoryId.toString());
                    }
                }
            }
            log.info("::{}::AO玩法关盘逻辑处理完成，赛事数量：{}，玩法ID：{}",
                    linkId,standardMatchInfos.size(),marketCategoryId);
        }
        log.info("::{}::AO玩法关盘逻辑结束，玩法ID：{}",linkId,marketCategoryId);
    }

    /**
     * 关盘逻辑判断处理
     *
     * @param linkId
     * @param standardMarketMessage
     * @param standardMatchInfo
     */
    public void dealMarketDeactivated(String linkId, StandardMarketMessage standardMarketMessage, StandardMatchInfo standardMatchInfo) {
        if (null != standardMarketMessage.getPlaceNum())
        {
            //位置状态 跟着子玩法
            ConfigMarketCategoryPlace childConfig = configMarketCategoryPlaceService.getConfigMarketPlaceCache(standardMatchInfo.getId(), standardMarketMessage.getMarketCategoryId(), standardMarketMessage.getChildMarketCategoryId(), standardMarketMessage.getPlaceNum());
            if (childConfig != null) {
                Integer ChildPlaceNumStatus = Integer.valueOf(childConfig.getPlaceNumStatus());
                standardMarketMessage.setPlaceNumStatus(ChildPlaceNumStatus);
            } else {
                standardMarketMessage.setPlaceNumStatus(0);
                log.info("::{}::赛事id:{},盘口id:{},位置:{},子玩法为空:{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getPlaceNum(),standardMarketMessage.getChildMarketCategoryId());
            }
        }
        //最终状态赋值
        standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        log.info("::{}::标准赛事id:{},关盘处理,统一盘口id:{},三方数据源id:{},盘口状态变化原因：{}",
                linkId, standardMatchInfo.getId(), standardMarketMessage.getId(),
                standardMarketMessage.getThirdMarketSourceId(),
                standardMarketMessage.getPaStatusReason());

        //存在关盘但是有投注项的盘口，并且paoddsvalue有为null的投注项，直接清空投注项
        if (!CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList()))
        {
            for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketMessage.getMarketOddsList())
            {
                if (null == standardMarketOddsMessage.getPaOddsValue())
                {
                    standardMarketMessage.getMarketOddsList().clear();
                    break;
                }
            }
        }
    }

    /**
     * 赔率合法性校验
     * 综合球种：PA赔率小于最小配置下发最小配置，大于最大配置下发最大配置赔率
     *
     * @param linkId
     * @param standardMatchInfo
     * @param configMarketTradeItemMap
     * @param standardMarketMessage
     * @param tournamentTradeItem
     */
    public void marketOddsVerify(String linkId, StandardMatchInfo standardMatchInfo, Map<String, ConfigMarketTradeItem> configMarketTradeItemMap,
                                  StandardMarketMessage standardMarketMessage, ConfigTournamentTradeItem tournamentTradeItem) {
        Long sportId = standardMatchInfo.getSportId();
        if (MarginCategoryConfig.COMPLEX_SPORTIDS.contains(sportId)) {
            //综合球种 羽毛球/排球/斯诺克 赔率合法性校验
            if (tournamentTradeItem == null) {
                log.info("::{}::赛事ID:{},综合球种:{}，联赛ID:{},最大最小值不存在，不处理赔率校验。",
                        linkId, standardMatchInfo.getId(), sportId, standardMatchInfo.getStandardTournamentId());
                return;
            }
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            if (CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())) {
                return;
            }
            for (StandardMarketOddsMessage marketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                if (null == marketOddsMessage.getPaOddsValue() || marketOddsMessage.getPaOddsValue() == 0) {
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.ODDS_ARE_NOT_LEGAL.getCode(),null));
                    continue;
                }
                StringBuffer sb = new StringBuffer();
                Double max = 0D;
                Double min = 0D;
                //margin算法 赔率校验
                if (MarginCategoryConfig.COMPLEX_EU_CATEGORY_ODDS_VERIFY.contains(marketCategoryId)) {
                    max = BigDecimalUtils.changeZero(tournamentTradeItem.getMarginMaxOdds());
                    min = BigDecimalUtils.changeZero(tournamentTradeItem.getMarginMinOdds());
                } else if (MarginCategoryConfig.COMPLEX_MY_CATEGORY_ODDS_VERIFY.contains(marketCategoryId)) {
                    //spread算法  赔率校验
                    max = BigDecimalUtils.changeZero(tournamentTradeItem.getSpreadMaxOdds());
                    min = BigDecimalUtils.changeZero(tournamentTradeItem.getSpreadMinOdds());
                } else {
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.INSERT_PRE_TRADER.getCode(),null));
                    sb.append("综合球种未知玩法直接关盘");
                    log.info("::{}::赛事ID:{},统一盘口id:{},三方盘口源id:{},,综合球种未知玩法:{}，直接关盘。",
                            linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), marketCategoryId);
                }
                if (max == 0 || min == 0) {
                    log.info("::{}::赛事ID:{},统一盘口id:{},三方盘口源id:{},,综合球种未配置最大最小赔率:{}，直接关盘。",
                            linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), marketCategoryId);
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.COMPREHENSIVE_BALL_SPECIES_UNKNOWN.getCode(),null));
                    return;
                }
                sb.append("赔率校验,最小最大欧赔：" + min + "-" + max);
                if (marketOddsMessage.getPaOddsValue() >= max * 100000) {
                    sb.append(" ,Pa赔率：" + marketOddsMessage.getPaOddsValue() + ",大于最大配置下发最大配置赔率");
                    marketOddsMessage.setPaOddsValue(BigDecimalUtils.multiply(max, 100000).intValue());
                }
                if (marketOddsMessage.getPaOddsValue() <= min * 100000) {
                    sb.append(" ,Pa赔率：" + marketOddsMessage.getPaOddsValue() + ",小于最小配置下发最小配置赔率");
                    marketOddsMessage.setPaOddsValue(BigDecimalUtils.multiply(min, 100000).intValue());
                }
                //赔率合法性校验
                checkMarketOddsValid(linkId, standardMatchInfo, standardMarketMessage);
            }
        } else {
            //赔率合法性校验,三项盘或以上玩法需要检查投注项赔率是否需要封投注项
            if (MarginCategoryConfig.EUROPE_MY_MARGIN_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())
                    || (null != standardMarketMessage.getMarketOddsList() && standardMarketMessage.getMarketOddsList().size() >= 3)) {
                checkMarketOddsValid(linkId, standardMatchInfo.getId(), standardMarketMessage,
                        configMarketTradeItemMap, sportId, standardMatchInfo);
            }
        }
    }

    /**
     * 综合球种赔率 合法性校验
     */
    public void checkMarketOddsValid(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketMessage marketDataMessage) {
        boolean isNegativeAll = true;
        List<Integer> paOddsValues = new ArrayList<>();
        //投注项集合
        for (StandardMarketOddsMessage message : marketDataMessage.getMarketOddsList()) {
            Integer paOddsValue = message.getPaOddsValue();
            //如果投注项active=0/3，或者赔率paOddsValue=0，则把赔率设置为1.001，该设置只用于后面公式的验证
            if (Constant.SPORT_MARKET.ODDS_STATUS.UNACTIVE.equals(message.getActive())
                    || Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED.equals(message.getActive())
                    || null == paOddsValue
                    || 0 == paOddsValue) {
                paOddsValues.add(100100);
                paOddsValue = 100100;
            } else {
                paOddsValues.add(paOddsValue);
            }
            if (paOddsValue >= 0) {
                isNegativeAll = false;
            }
        }
        //赔率值不能都为负数,全都为负数的话等于转成欧赔上下盘都大于2了。玩家稳赢
        if (isNegativeAll) {
            setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.SUSPENDED);
            marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.ODDS_CANNOT_BE_NEGATIVE.getCode(),null));
            log.info("::{}::赔率合法性校验,标准赛事id:{},统一盘口id:{},三方盘口源id:{},赔率值不能都为负数",
                    linkId, standardMatchInfo.getId(), marketDataMessage.getId(), marketDataMessage.getThirdMarketSourceId());
            return;
        }
        //多项盘 1/(1/o1 + 1/o2 + ... + 1/on) 不要大于0.99
        BigDecimal sumDecimal = new BigDecimal(0);
        for (Integer aLong : paOddsValues) {
            if (null == aLong || 0 == aLong) {
                continue;
            }
            BigDecimal bigDecimal = new BigDecimal(100000).divide(new BigDecimal(aLong), 2, BigDecimal.ROUND_HALF_UP);
            sumDecimal = sumDecimal.add(bigDecimal);
        }
        if (!sumDecimal.equals(BigDecimal.ZERO)) {
            double result = new BigDecimal(1).divide(sumDecimal, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (result > 0.99) {
                setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.SUSPENDED);
                marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.MULTIPLE_DISCS_DO_NOT_MEET_THE_RULES.getCode(),null));
                log.info("::{}::赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},1/(1/o1 + ... + 1/on)大于0.99,赔率合法性检查不通过",
                        linkId, standardMatchInfo.getId(), marketDataMessage.getId(), marketDataMessage.getThirdMarketSourceId(), paOddsValues);
                return;
            }
        }
    }

    /**
     * 38900 盘口值处理优化，去掉.5
     * @param standardMarketMessage
     */
    public StandardMarketMessage ballHeadsRemoveDecimal(StandardMarketMessage standardMarketMessage){
        if (ballHeadRemoveDecimalCategoryIdSet.contains(standardMarketMessage.getMarketCategoryId())){
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition1())&&standardMarketMessage.getAddition1().contains(".0")){
                standardMarketMessage.setAddition1(standardMarketMessage.getAddition1().replace(".0",""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition2())&&standardMarketMessage.getAddition2().contains(".0")){
                  standardMarketMessage.setAddition2(standardMarketMessage.getAddition2().replace(".0",""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition3())&&standardMarketMessage.getAddition3().contains(".0")){
                standardMarketMessage.setAddition3(standardMarketMessage.getAddition3().replace(".0",""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition4())&&standardMarketMessage.getAddition4().contains(".0")){
                standardMarketMessage.setAddition4(standardMarketMessage.getAddition4().replace(".0",""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition5())&&standardMarketMessage.getAddition5().contains(".0")){
                standardMarketMessage.setAddition5(standardMarketMessage.getAddition5().replace(".0",""));
            }

            if (StringUtils.isNotBlank(standardMarketMessage.getAddition1()) && standardMarketMessage.getAddition1().contains(".00")) {
                standardMarketMessage.setAddition1(standardMarketMessage.getAddition1().replace(".00", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition2()) && standardMarketMessage.getAddition2().contains(".00")) {
                standardMarketMessage.setAddition2(standardMarketMessage.getAddition2().replace(".00", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition3()) && standardMarketMessage.getAddition3().contains(".00")) {
                standardMarketMessage.setAddition3(standardMarketMessage.getAddition3().replace(".00", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition4()) && standardMarketMessage.getAddition4().contains(".00")) {
                standardMarketMessage.setAddition4(standardMarketMessage.getAddition4().replace(".00", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition5()) && standardMarketMessage.getAddition5().contains(".00")) {
                standardMarketMessage.setAddition5(standardMarketMessage.getAddition5().replace(".00", ""));
            }

            if (StringUtils.isNotBlank(standardMarketMessage.getAddition1()) && standardMarketMessage.getAddition1().contains(".50")) {
                standardMarketMessage.setAddition1(standardMarketMessage.getAddition1().replace(".50", ".5"));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition2()) && standardMarketMessage.getAddition2().contains(".50")) {
                standardMarketMessage.setAddition2(standardMarketMessage.getAddition2().replace(".50", ".5"));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition3()) && standardMarketMessage.getAddition3().contains(".50")) {
                standardMarketMessage.setAddition3(standardMarketMessage.getAddition3().replace(".50", ".5"));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition4()) && standardMarketMessage.getAddition4().contains(".50")) {
                standardMarketMessage.setAddition4(standardMarketMessage.getAddition4().replace(".50", ".5"));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition5()) && standardMarketMessage.getAddition5().contains(".50")) {
                standardMarketMessage.setAddition5(standardMarketMessage.getAddition5().replace(".50", ".5"));
            }
        }
        return standardMarketMessage;
    }

    /**
     * 篮球、网球 让球玩法特殊盘口值 封盘独赢玩法
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessage
     * @param riskCategorySet
     */
    public void processHandicapCategory(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage, Set<Long> riskCategorySet) {
/*        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.code) && MarginCategoryConfig.HANDICAP_CATEGORY_SUBSECTION.contains(standardMarketMessage.getMarketCategoryId()) && MarginCategoryConfig.HANDICAP_MARKET_DISPOSE.contains(standardMarketMessage.getAddition1()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketMessage.getStatus()) && 1 == standardMarketMessage.getPlaceNum()) {
            riskCategorySet.add(MarginCategoryConfig.HANDICAP_WINNER_MAP.get(standardMarketMessage.getMarketCategoryId()));
        }*/

        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.IceBall.code) && MarginCategoryConfig.HANDICAP_CATEGORY_SUBSECTION.contains(standardMarketMessage.getMarketCategoryId()) && MarginCategoryConfig.HANDICAP_MARKET_DISPOSE.contains(standardMarketMessage.getAddition1()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketMessage.getStatus())) {
            riskCategorySet.add(MarginCategoryConfig.HANDICAP_WINNER_MAP.get(standardMarketMessage.getMarketCategoryId()));
        }
    }

    /**
     * 网球/乒乓球球头校验
     * 让分及大小类玩法的 球头（含盘口差）超过最小最大球头时对应盘口自动封盘， 当球头回到正常范围则自动开盘；
     */
    public void ballVerify(String linkId, Integer marketType, StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage) {
        Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
        if (StringUtils.isEmpty(standardMarketMessage.getAddition1())) {
            return;
        }
        Integer status = standardMarketMessage.getStatus();
        if (status < Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED)
        {
            if (null != MarginCategoryConfig.DYNAMIC_SPORT.get(standardMatchInfo.getSportId())) {
            List<Long> marketCategoryIds = MarginCategoryConfig.DYNAMIC_SPORT.get(standardMatchInfo.getSportId());
            if (!marketCategoryIds.contains(marketCategoryId)) {
                return;
            }

            Integer tempStatus = Math.max(standardMarketMessage.getStatus(), standardMarketMessage.getPaStatus());
            //只有开,封需要做下面的校验
            if (tempStatus >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
                log.info("::{}::球头校验,标准赛事id:{},统一盘口id:{},不是有效盘口状态",
                        linkId, standardMatchInfo.getId(), standardMarketMessage.getId());
                return;
            }
            String standardSportName = StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getMsg();
            MarketCategorySell item = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), marketType, marketCategoryId);
            if (null == item) {
                log.info("::{}::标准赛事id:{},{}球头校验开售玩法为空,玩法:{}", linkId, standardMatchInfo.getId(), standardSportName, marketCategoryId);
                return;
            }
            Double addition1 = null;
            Integer statusTemp = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
            if (MarginCategoryConfig.DYNAMIC_NO_ABS.contains(marketCategoryId))
            {
                addition1 =  Double.parseDouble((standardMarketMessage.getAddition1()));
                if (addition1 < 0)
                    statusTemp = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
            }
            else
            {
                addition1 = Math.abs(Double.parseDouble((standardMarketMessage.getAddition1())));
            }

//            //斯诺克、乒乓球、排球 小于最小球头
                if (null != item.getMaxBallHead() && item.getMaxBallHead().doubleValue() != 0D) {
                    if (addition1 < item.getMinBallHead().doubleValue()) {
                        standardMarketMessage.setStatus(statusTemp);
                        standardMarketMessage.setPaStatus(statusTemp);
                        standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.HANDICAP_MIN.getCode(), item.getMinBallHead().toString()));
                        log.info("::{}::标准赛事id:{},{}小于最小球头,盘口关盘,盘口ID:{},玩法:{},信息:{}", linkId, standardMatchInfo.getId(), standardSportName, standardMarketMessage.getId(), marketCategoryId, item.getMinBallHead());
                        // 发送操盘日志给风控
                        commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo, standardMarketMessage);

                    }
                    if (addition1 > item.getMaxBallHead().doubleValue()) {
                        standardMarketMessage.setStatus(statusTemp);
                        standardMarketMessage.setPaStatus(statusTemp);
                        standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.HANDICAP_MAX.getCode(), item.getMaxBallHead().toString()));
                        log.info("::{}::标准赛事id:{},{}大于最大球头,盘口关盘,盘口ID:{},玩法:{},信息:{}", linkId, standardMatchInfo.getId(), standardSportName, standardMarketMessage.getId(), marketCategoryId, item.getMaxBallHead());
                        // 发送操盘日志给风控
                        commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo,standardMarketMessage);
                    }
                } else {
                    if (null != item.getMinBallHead() && addition1 < item.getMinBallHead().doubleValue()) {
                        standardMarketMessage.setStatus(statusTemp);
                        standardMarketMessage.setPaStatus(statusTemp);
                        standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.HANDICAP_MIN.getCode(), item.getMinBallHead().toString()));
                        log.info("::{}::标准赛事id:{},{}球头小于最小配置,盘口关盘,盘口ID:{},玩法:{},信息:{}", linkId, standardMatchInfo.getId(), standardSportName, standardMarketMessage.getId(), marketCategoryId, JSONObject.toJSONString(item));
                        // 发送操盘日志给风控
                        commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo,standardMarketMessage);
                    }
                }
        }
            else if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code)
                    && MarginCategoryConfig.ADD1_VERIFY.contains(standardMarketMessage.getDataSourceCode())
                    && MarginCategoryConfig.ADD1_CATEGORY.contains(marketCategoryId))
            {
                Double addition1 =  Double.parseDouble((standardMarketMessage.getAddition1()));
                if (addition1 < 0.5)
                {
                    standardMarketMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED);
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.BALL_HEAD_NOT_SATISFIED.getCode(),null));
                    log.info("::{}::标准赛事id:{},盘口封盘,盘口ID:{},玩法ID:{}",
                            linkId,standardMatchInfo.getId(),standardMarketMessage.getId(),marketCategoryId);
                }
            }
        }
    }

    /**
     * 需求：2505
     * 篮球滚球 自动开盘玩法
     */
    public void processAutoOpenMarketCategory(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageSendList) {
        //需求配置开关
        if(openMarketStatus == 0){
            return;
        }
        //只处理篮球
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.code)) {
            return;
        }
        //赛前不处理
        if (1 == isOddsLive(standardMatchInfo.getId())) {
            return;
        }
        //过滤出需要处理的玩法盘口
        Map<Long, List<StandardMarketMessage>> marketCategoryGrop = standardMarketMessageSendList.stream().
                filter(standardMarketMessage -> MarginCategoryConfig.BASKETBALL_AUTO_OPEN_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        if (MapUtils.isEmpty(marketCategoryGrop)) {
            return;
        }
        for (Map.Entry<Long, List<StandardMarketMessage>> entry : marketCategoryGrop.entrySet()) {
            Long marketCategoryId = entry.getKey();
            List<StandardMarketMessage> standardMarketMessages = entry.getValue();
            MarketCategorySell marketCategorySell = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), 0, marketCategoryId);
            if (null == marketCategorySell) {
                continue;
            }
            if (null == marketCategorySell.getAutoOpenMarket() || 0 == marketCategorySell.getAutoOpenMarket()
                    || null == marketCategorySell.getAutoOpenTime() || 0 == marketCategorySell.getAutoOpenTime()) {
                log.info("::{}::篮球滚球自动开盘玩法,玩法配置：{}，不存在不处理", linkId, marketCategoryId);
                continue;
            }
            String key = Constant.REDIS_KEY.RONGHE_AUTO_OPEN_MARKET_CATEGORY + standardMatchInfo.getId();
            Object o = redisService.hGet(key, String.valueOf(marketCategoryId));
            log.info("::{}::篮球滚球自动开盘玩法,玩法：{}，是否关盘:{}", linkId, marketCategoryId, o);
            if (ObjectUtil.isNull(o) || !Boolean.parseBoolean(o.toString())) {
                standardMarketMessages.forEach(standardMarketMessage -> {
                    standardMarketMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                    standardMarketMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                });
            }
        }
    }


    public void processOddsValueDecimals(String linkId, StandardMarketMessage standardMarketMessage, StandardMatchInfo standardMatchInfo) {
        //校验赛种,玩法MY赔率不走赔率优化计算
        /*if (MarginCategoryConfig.ODDS_GRACEFUL_SPORT.contains(standardMatchInfo.getSportId())) {
            List<Long> marketCategoryIdMALAY = MarginCategoryConfig.SPORT_MY_CATEGORY.get(standardMatchInfo.getSportId());
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            if (marketCategoryIdMALAY.contains(marketCategoryId)) {
                log.info("::{}::MY玩法不做赔率优化,赛事ID:{},标准盘口ID:{},玩法:{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), marketCategoryId);
                return;
            }
        }*/
        //使用数据源抽水赔，走赔率优化
        if(!CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList()))
        {
            log.info("::{}:: 开始处理 processOddsValueDecimals,标准盘口id:{}",linkId, standardMarketMessage.getId());
            for(StandardMarketOddsMessage oddsMessage : standardMarketMessage.getMarketOddsList()){
                try{
                    if (null == oddsMessage.getPaOddsValue() || 0 == oddsMessage.getPaOddsValue()) {
                        continue;
                    }
                    BigDecimal bigDecimal = new BigDecimal(oddsMessage.getPaOddsValue()).divide(new BigDecimal(100000),2, BigDecimal.ROUND_DOWN);
                    int left = bigDecimal.intValue();
                    int right = bigDecimal.subtract(new BigDecimal(left)).multiply(new BigDecimal(100)).intValue();
                    Integer paOddsValue = 0;
                    if(left >=3 && left < 5)
                    {
                        if(right < 5){
                            paOddsValue = bigDecimal.intValue() * 100000;
                        }else{
                            BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(5), 0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(0.05));
                            paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
                        }
                    }
                    else if(left >= 5 && left < 10)
                    {
                        if(right < 10){
                            paOddsValue = bigDecimal.intValue() * 100000;
                        }else{
                            BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(100), 1, BigDecimal.ROUND_DOWN);
                            paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
                        }
                    }
                    else if(left >=10 && left < 20)
                    {
                        if(right < 50){
                            paOddsValue = bigDecimal.intValue() * 100000;
                        }else{
                            paOddsValue = new BigDecimal(left).add(new BigDecimal(0.5)).multiply(new BigDecimal(100000)).intValue();
                        }
                    }
                    else if(left >= 20)
                    {
                        paOddsValue = left * 100000;
                    }
                    BigDecimal oneHundredThousand = new BigDecimal("100000");
                    log.info("::{}::processOddsValueDecimals,投注项id:{},left:{},right:{},优化前paOddsValue:{},优化后paOddsValue:{}", linkId, oddsMessage.getId(), left, right, oddsMessage.getPaOddsValue(), paOddsValue);
                    if (paOddsValue != 0) {
                        oddsMessage.setPaOddsValue(new BigDecimal(paOddsValue).divide(oneHundredThousand).setScale(2, BigDecimal.ROUND_DOWN).multiply(oneHundredThousand).intValue());
                        if (oddsMessage.getMalayOddsValue()!=null){
                            oddsMessage.setMalayOddsValue(initializeComponent.getConvertEuropeToMalay(oddsMessage.getPaOddsValue()));
                        }
                    } else {
                        oddsMessage.setPaOddsValue(bigDecimal.multiply(oneHundredThousand).intValue());
                    }
                } catch (Exception e) {
                    log.error("::{}::processOddsValueDecimals标准投注项id:{},两项盘小数位优化error:{}", linkId, oddsMessage.getId(), e);
                }
            }
        }
    }

    public void processPlayerTeamFlag(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage) {
        //三方球员源id
        String addition2 = standardMarketMessage.getAddition2();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), standardMarketMessage.getDataSourceCode());
        try {
            List<ThirdMatchTeamRelationDetail> list = thirdMatchTeamRelationService.getItemsByMatchId(thirdMatchInfo.getId());
            if (CollectionUtils.isEmpty(list)) {
                standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.error("::{}::list为空，赛事:{},球员:{}无法区分主客队、盘口关盘", linkId, thirdMatchInfo.getId(), addition2);
                return;
            }
            Map<String, String> map = new HashMap<>();
            for (ThirdMatchTeamRelationDetail item: list) {
                if(null == map.get(item.getThirdSourcePlayerId())){
                    map.put(item.getThirdSourcePlayerId(),item.getMatchPosition());
                }
            }
            if (null == map.get(addition2)) {
                standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.error("::{}::三方赛事id:{},没有获取到盘口id:{},球员:{},的主客队标识，盘口关盘", linkId, thirdMatchInfo.getId(), standardMarketMessage.getId(), addition2);
                return;
            }
            if (ConstantSystem.AWAY.equalsIgnoreCase(map.get(addition2))) {
                standardMarketMessage.setAddition4(ConstantSystem.TWO.toString());
            } else if (ConstantSystem.HOME.equalsIgnoreCase(map.get(addition2))) {
                standardMarketMessage.setAddition4(ConstantSystem.ONE.toString());
            } else {
                standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.error("::{}::赛事:{},球员:{}的主客队标识不是away或home，盘口关盘", linkId, thirdMatchInfo.getId(), addition2);
            }
        } catch (Exception e) {
            log.error("::{}::获取赛事:{},球员:{}的主客队标识,error:{}", linkId, thirdMatchInfo.getId(), addition2, e);
        }
    }

    public void processPlayerNameCode(String linkId, Long sportId, StandardMarketMessage standardMarketMessage) {
        Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
        String addition2 = standardMarketMessage.getAddition2();
        try {
            //篮球玩法
            if (MarginCategoryConfig.BASKETBALL_PLAYER_CATEGORY.contains(marketCategoryId)) {
                StandardSportPlayer standardSportPlayer = standardSportPlayerService.getItem(sportId, addition2);
                if (null != standardSportPlayer) {
                    standardMarketMessage.setAddition3(standardSportPlayer.getNameCode().toString());
                    return;
                }else{
                    standardMarketMessage.setAddition3("-1");
                }
                standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.PLAYER_NOT_FOUND.getCode(),null));
            }
            //足球玩法
            if (MarginCategoryConfig.PLAYER_CATEGORY_ODDS.contains(marketCategoryId) && !CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())) {
                for (StandardMarketOddsMessage marketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                    if(MarginCategoryConfig.PLAYER_CATEGORY_ODDS_TYPE.contains(marketOddsMessage.getOddsType())){
                        log.info("::{}::processPlayerNameCode,投注项类型【None,OwnGoal,Other】没有三方球员源id,不做nameCode处理", linkId);
                        footballMarketOddsValidateService.validatePlayerOdds(marketOddsMessage, marketCategoryId);
                        continue;
                    }
                    StandardSportPlayer standardSportPlayer = null;
                    //BG球员 先查询三方 再根据 三方标准ID 查询标准
                    if (DataSourceCodeEnum.BG.code.equals(standardMarketMessage.getDataSourceCode())) {
                        ThirdSportPlayerDetail thirdSportPlayerDetail = thirdSportPlayerService.getItem(standardMarketMessage.getDataSourceCode(), sportId, marketOddsMessage.getAddition1());
                        if (null != thirdSportPlayerDetail) {
                            Long referenceId = thirdSportPlayerDetail.getReferenceId();
                            standardSportPlayer = standardSportPlayerService.getItemById(referenceId);
                        }
                    } else {
                        standardSportPlayer = standardSportPlayerService.getItem(sportId, marketOddsMessage.getAddition1());
                    }
                    if (null != standardSportPlayer) {
                        marketOddsMessage.setOddsType(standardSportPlayer.getNameCode().toString());
                        continue;
                    }
                    marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.UNACTIVE);
                    marketOddsMessage.setPaActiveReason("【" + linkId + "】足球三方球员源id【" + marketOddsMessage.getAddition1() + "】未找到标准球员，投注项关闭");
                }
            }
            //三方球员id转标准球员id
            if(337L == marketCategoryId && !CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())){
                for (StandardMarketOddsMessage marketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                    ThirdSportTeam oneItem = thirdSportTeamService.getOneItem(marketOddsMessage.getDataSourceCode(), sportId, marketOddsMessage.getAddition1());
                    if (null != oneItem) {
                        marketOddsMessage.setAddition1(String.valueOf(oneItem.getReferenceId()));
                        continue;
                    }
                    //0为平局，没有球队id
                    if(!"0".equals(marketOddsMessage.getAddition1())){
                        marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.UNACTIVE);
                        marketOddsMessage.setPaActiveReason("【" + linkId + "】三方球队源id【" + marketOddsMessage.getAddition1() + "】未找到标准球队，投注项关闭");
                    }
                }
            }
            if(339L == marketCategoryId && !CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())){
                for (StandardMarketOddsMessage marketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                    ThirdSportTeam oneItem = thirdSportTeamService.getOneItem(marketOddsMessage.getDataSourceCode(), sportId, marketOddsMessage.getAddition2());
                    if (null != oneItem) {
                        marketOddsMessage.setAddition2(String.valueOf(oneItem.getReferenceId()));
                        continue;
                    }
                    //0为平局，没有球队id
                    if(!"0".equals(marketOddsMessage.getAddition2())){
                        marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.UNACTIVE);
                        marketOddsMessage.setPaActiveReason("【" + linkId + "】三方球队源id【" + marketOddsMessage.getAddition1() + "】未找到标准球队，投注项关闭");
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::processPlayerNameCode标准盘口id:{},球员玩法多语言处理error:{}", linkId, standardMarketMessage.getId(), e);
        }
    }

    private void setOddsMetricAndLowOddsForAUTO_PLUS(String linkId, StandardMatchInfo standardMatchInfo, Map<Long,List<StandardMarketDataMessage>> collectAUTO_PLUS_Map, List<StandardMarketDataMessage> standardMarketMessageListAUTO_PLUS) {
        if(MapUtils.isNotEmpty(collectAUTO_PLUS_Map)){
            for (Map.Entry<Long,List<StandardMarketDataMessage>> entry:collectAUTO_PLUS_Map.entrySet())
            {
                List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
                //------- TX A+ 模式处理 start -------
                String dataSourceCode = standardMarketDataMessages.get(0).getDataSourceCode();
                if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(dataSourceCode)) {
                    //数据太多，不打印投注项
                    SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                    filter.getExcludes().add("marketOddsList");
                    log.info("::{}::,TX盘口A+模式处理,赛事ID:{},玩法:{},盘口数据:{}", linkId, standardMatchInfo.getId(), entry.getKey(), JSONObject.toJSONString(standardMarketDataMessages, filter));
                    txPlaceAPProcess(linkId, standardMatchInfo, standardMarketDataMessages);
                }
                //------- TX A+ 模式处理 end -------

                //TODO:A+模式需要把最新的主盘口数据下发到操盘后台，所以这里对关盘的重新按照创建时间，修改时间降序排序
                //排序字段placeNum
                //取盘口中无投注项的数据
                //获取key对应的盘口对象集合
                List<StandardMarketDataMessage> standardMarketsInvalid = standardMarketDataMessages.stream().filter(e -> CollectionUtils.isEmpty(e.getMarketOddsList()) || e.getThirdMarketSourceStatus()>= Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
                ListUtils.sort(standardMarketsInvalid, false, "createTime","modifyTime");
                if (!CollectionUtils.isEmpty(standardMarketsInvalid)) {
                    int placeNum = standardMarketDataMessages.size()-standardMarketsInvalid.size()+1;
                    for (StandardMarketDataMessage standardMarket : standardMarketsInvalid) {
                        if(!MarginCategoryConfig.SPORT_TX_LOGIC.contains(dataSourceCode)){
                            standardMarket.setPlaceNum(placeNum++);
                        }
                    }
                }
                List<StandardMarketDataMessage> collectAUTO_PLUS = entry.getValue();
                //处理最大盘口数量设置，多余的盘口设置为DEACTIVATED
                processConfigMarketDisplayTrade(linkId, collectAUTO_PLUS,standardMatchInfo);
                standardMarketMessageListAUTO_PLUS.addAll(collectAUTO_PLUS);
            }
        }
    }

    /**
     * 坑位1 关，取坑位2  有效坑位盘口开
     */
    private void txPlaceAPProcess(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessages) {
        List<StandardMarketDataMessage> txSortStandardMarketsOne = standardMarketDataMessages.stream().filter(e -> e.getPlaceNum() == 1).sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
        List<StandardMarketDataMessage> txSortStandardMarketsTwo = standardMarketDataMessages.stream().filter(e -> e.getPlaceNum() == 2).sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
        Boolean isTrue = Boolean.FALSE;
        if(!CollectionUtils.isEmpty(txSortStandardMarketsOne)){
            int num = 0;
            for (StandardMarketDataMessage t : txSortStandardMarketsOne) {
                if (num == 0 && t.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)) {
                    t.setPlaceNum(1);
                    isTrue = Boolean.TRUE;
                } else {
                    t.setPlaceNum(999);
                }
                num++;
            }
        }
        if(isTrue){
            return;
        }
        if(!CollectionUtils.isEmpty(txSortStandardMarketsTwo)){
            int num = 0;
            for (StandardMarketDataMessage t : txSortStandardMarketsTwo) {
                if (num == 0 && t.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)) {
                    t.setPlaceNum(1);
                    isTrue = Boolean.TRUE;
                } else {
                    t.setPlaceNum(999);
                }
                num++;
            }
        }
        if (!isTrue) {
            int num = 0;
            for (StandardMarketDataMessage t : txSortStandardMarketsOne) {
                if (num == 0) {
                    t.setPlaceNum(1);
                } else {
                    t.setPlaceNum(999);
                }
                num++;
            }
        }
    }

    /**
     * 操盘球种 盘口差处理
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketDataMessageList
     * @param marketCategoryIdMALAY
     */
    private void handlerBasketAddtion(String linkId,StandardMatchInfo standardMatchInfo,List<StandardMarketDataMessage> standardMarketDataMessageList,Set<Long> marketCategoryIdMALAY)
    {
        if (MarginCategoryConfig.SPORT_HEAD.contains(standardMatchInfo.getSportId())) {
            for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessageList) {
                if (!marketCategoryIdMALAY.contains(standardMarketDataMessage.getMarketCategoryId()))
                {
                    continue;
                }
                //篮球玩法走新的盘口差需求
                if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.getCode()) &&
                        MarginCategoryConfig.BASKETBALL_HEAD_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())) {
                    continue;
                }
                //根据赛事、玩法id查询盘口差配置数据
                ConfigMarketCategoryHead configMarketCategoryHead = configMarketHeadGapService.getItemCache(linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId());
                //定义盘口差
                Double marketHeadGap = 0D;
                //盘口值
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("::" + linkId + "::,盘口差计算,统一盘口id:" + standardMarketDataMessage.getRelationMarketId() + ",标准玩法id:" + standardMarketDataMessage.getMarketCategoryId() +
                        ",三方盘口源id:" + standardMarketDataMessage.getThirdMarketSourceId() + ",赔率差:" + standardMarketDataMessage.getOddsMetric() + ",低赔:" + standardMarketDataMessage.getPaOddsValue());
                //数据源原始球头
                if (MarginCategoryConfig.CATEGORY_ORIGINAL_BALL.contains(standardMarketDataMessage.getMarketCategoryId())) {
                    standardMarketDataMessage.setAddition5(standardMarketDataMessage.getAddition1());
                }
                if (null != configMarketCategoryHead && standardMarketDataMessage.getMarketType().equals(configMarketCategoryHead.getMarketType()) && configMarketCategoryHead.getMarketHeadGap()!=0) {
                    marketHeadGap = configMarketCategoryHead.getMarketHeadGap();
                    //附加字段1玩法分类
                    if (StringUtils.isNotBlank(standardMarketDataMessage.getAddition1())
                            && MarginCategoryConfig.MARKET_CATEGORY_HEAD.contains(standardMarketDataMessage.getMarketCategoryId())) {
                        stringBuffer.append(",计算前球头:" + standardMarketDataMessage.getMarketOddsValue() + ",计算前附加字段1:" + standardMarketDataMessage.getAddition1() + ",盘口差:" + marketHeadGap);
                        Double marketHead = 0D;
                        if (standardMatchInfo.getSportId() == 2) {
                            marketHead = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition1())).add(new BigDecimal(Double.toString(marketHeadGap))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                        } else {
                            marketHead = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition1())).add(new BigDecimal(Double.toString(marketHeadGap))).doubleValue();
                        }
                        if(StringUtils.isNotBlank(standardMarketDataMessage.getAddition2())
                                && MarginCategoryConfig.BASKETBALL_ADDTION2_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())){
                            Double marketHead2 = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition2())).add(new BigDecimal(Double.toString(marketHeadGap))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                            standardMarketDataMessage.setAddition2(String.valueOf(marketHead2).replace(".0", ""));
                        }
                        specialBasketBallHead(standardMarketDataMessage, stringBuffer, marketHead, marketHeadGap);
                    }
                    //设置盘口差
                    standardMarketDataMessage.setMarketHeadGap(marketHeadGap);
                    //通过附加字段，找到新的统一盘口id，统一投注项id
                    StandardSportMarket standardSportMarket = new StandardSportMarket();
                    BeanUtils.copyProperties(standardMarketDataMessage, standardSportMarket);
                    //T01单独处理盘口ID
                    if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.getCode())) {
                        standardMarketDataMessage.setSendData(standardSportMarketService.createRelationMarketId(linkId, standardSportMarket).toString());
                    } else {
                        standardMarketDataMessage.setRelationMarketId(standardSportMarketService.getRelationMarketId(linkId, standardSportMarket));
                    }
                    stringBuffer.append(",盘口差后的统一盘口id:" + standardMarketDataMessage.getRelationMarketId());
                    //关盘的盘口存在没有投注项的可能
                    if (!CollectionUtils.isEmpty(standardMarketDataMessage.getMarketOddsList()))
                    {
                        standardMarketDataMessage.getMarketOddsList().forEach(standardMarketOddsDataMessage -> {
                            StandardSportMarketOdds standardSportMarketOdds = new StandardSportMarketOdds();
                            standardMarketOddsDataMessage.setRelationMarketId(standardMarketDataMessage.getRelationMarketId());
                            BeanUtils.copyProperties(standardMarketOddsDataMessage, standardSportMarketOdds);
                            standardMarketOddsDataMessage.setRelationMarketOddsId(standardSportMarketOddsService.getRelationMarketOddsId(standardSportMarketOdds,standardMarketDataMessage.getMarketCategoryId()));
                            //T01单独处理盘口投注项ID
                            if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.getCode())) {
                                standardMarketOddsDataMessage.setRemark(standardSportMarketOddsService.adjustmentTxCreateRelationMarketOddsId(standardSportMarketOdds, standardMarketDataMessage));
                            }
                            stringBuffer.append(",盘口差后的统一盘口投注项id:" + standardMarketOddsDataMessage.getRelationMarketOddsId());
                        });
                    }
                    log.info(stringBuffer.toString());
                }
            }
            //篮球玩法走新的盘口差需求
            if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.getCode())) {
                basketballZeroProcessor.zeroProcessor(linkId, standardMatchInfo, standardMarketDataMessageList);
            }
        }
    }

    /**
     * 篮球全场让分球头处理
     * 1. 出现±0.5的球头时：从第二个±0.5的球头开始，同边后面所有的球头±1（所在等差数列递增则+，递减则-）
     * 2. 原始球头与盘口差计算出±0.5 球头改为 0
     * 3. 原始球头与盘口差计算出0 球头改为 ±1
     *
     * @param standardMarketDataMessage 标准盘口数据
     * @param stringBuffer              日志
     * @param marketHead                原始球头与盘口差 计算值
     * @param marketHeadGap             盘口差
     */
    private void specialBasketBallHead(StandardMarketDataMessage standardMarketDataMessage, StringBuffer stringBuffer, Double marketHead, Double marketHeadGap) {
        stringBuffer.append(",原始球头与盘口差计算值:" + marketHead);
        Double newMarketHead = marketHead;
        BigDecimal marketHeadB = new BigDecimal(Double.toString(marketHead));
        int tailNum = marketHeadB.remainder(BigDecimal.ONE).movePointRight(marketHeadB.scale()).abs().intValue();
        if (standardMarketDataMessage.getMarketCategoryId().equals(MarginCategoryConfig.HANDICAP_CATEGORY)) {
            //Step1:出现±0.5的球头或者是一个正数时：从第二个±0.5的球头开始，同边后面所有的球头±1（所在等差数列递增则+，递减则-）,只对小数点后为5的绝对值±球头
            if (tailNum == 5 || new BigDecimal(marketHeadB.intValue()).compareTo(marketHeadB) == 0) {
                if (Double.parseDouble(standardMarketDataMessage.getAddition1()) > 0) {
                    if (marketHead <= 0) {
                        newMarketHead = marketHeadB.subtract(BigDecimal.ONE).doubleValue();
                    } else if (marketHead == 0.5D) {
                        //Step2:原始球头与盘口差计算出 +0.5 球头改为 0
                        newMarketHead = 0D;
                    }
                } else if (Double.parseDouble(standardMarketDataMessage.getAddition1()) < 0) {
                    if (marketHead >= 0) {
                        newMarketHead = marketHeadB.add(BigDecimal.ONE).doubleValue();
                    } else if (marketHead == -0.5D) {
                        //Step2:原始球头与盘口差计算出 -0.5 球头改为 0
                        newMarketHead = 0D;
                    }
                    //原始盘口为0
                } else {
                    if (marketHeadGap < 0) {
                        newMarketHead = marketHeadB.subtract(new BigDecimal("-0.5")).doubleValue();
                    } else if (marketHeadGap > 0) {
                        newMarketHead = marketHeadB.add(new BigDecimal("0.5")).doubleValue();
                    }
                    stringBuffer.append(",原始盘口为:" + standardMarketDataMessage.getAddition1());
                }
                stringBuffer.append(",计算后附加字段1(±0.5后等差数列递增/±0.5球头改为0)最终值:" + newMarketHead);
            }
        }
        //设置新的球头,绝对值
        standardMarketDataMessage.setMarketOddsValue(Math.abs(newMarketHead));
        stringBuffer.append(",计算后球头:" + standardMarketDataMessage.getMarketOddsValue() + ",盘口差计算后附加字段1:" + marketHead);
        standardMarketDataMessage.setAddition1(String.valueOf(newMarketHead).replace(".0", ""));
    }

    /**
     * 对盘口进行计算和排序(自动操盘处理方法)
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     * @param standardMarketDataMessageList
     * @return
     */
    public List<StandardMarketDataMessage> processPandaCalculate(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, List< StandardMarketDataMessage> standardMarketDataMessageList) {
        //MY玩法
        Set<Long> marketCategoryIdMALAY = new HashSet<>();
        //两项盘EU玩法
        List<Long> marketCategoryIdEu = new ArrayList<>();
        //玩法分类 MY/EU
        marketCategoryDistinguish(marketCategoryIdSet, marketCategoryIdMALAY, marketCategoryIdEu, standardMatchInfo.getSportId());
        if (marketCategoryIdMALAY.size() > 0) {
            handlerBasketAddtion(linkId, standardMatchInfo, standardMarketDataMessageList, marketCategoryIdMALAY);
        }
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        //A操盘，盘口状态增加弃用状态校验，弃用状态盘口不参与排位计算
        List< StandardMarketDataMessage> giveUpMarketList = new ArrayList<>();
        List<ConfigMarketStatusTrade> configMarketStatusTrades = new ArrayList<>();
        if (!CollectionUtils.isEmpty(standardMarketDataMessageList))
        {
            sw.start("弃用盘口状态查询");
            int marketType = isOddsLive(standardMatchInfo.getId());
            configMarketStatusTrades = configMarketStatusTradeService.getItemList(standardMatchInfo.getId(), marketType, marketCategoryIdSet);
            sw.stop();
            if (!CollectionUtils.isEmpty(configMarketStatusTrades))
            {
                sw.start("弃用盘口状态处理及排序");
                log.info("::{}::盘口开启跟弃用操作，需要变更的盘口集合：{}",linkId, JSON.toJSONString(configMarketStatusTrades));

                configMarketStatusTrades.forEach(e->{
                    standardMarketDataMessageList.forEach(v->{
                        //TX统一盘口ID 需要转换
                        Long relationMarketId = convertRelationMarketId(linkId, v);
                        if (relationMarketId.equals(e.getRelationMarketId())
                                && e.getMarketStatus() == Constant.SPORT_MARKET.STATUS.LOSE
                                && e.getMarketType() == v.getMarketType())
                        {
                            v.setStatus(Constant.SPORT_MARKET.STATUS.LOSE);
                            v.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            v.setRemark("操盘后台弃用此盘口");
                            //TX弃用位置不处理 最后会统一处理
                            if(!MarginCategoryConfig.SPORT_TX_LOGIC.contains(v.getDataSourceCode())){
                                v.setPlaceNum(999);
                                v.setPlaceNumId(v.getStandardMatchInfoId() + "_" + v.getMarketCategoryId() + "_" + v.getPlaceNum());
                            }
                            //不对A01删除弃用
                            if(!DataSourceCodeEnum.AO.getCode().equals(v.getDataSourceCode())){
                                giveUpMarketList.add(v);
                            }
                        }
                    });
                });
                Set<Long>  set = configMarketStatusTrades.stream().map(ConfigMarketStatusTrade::getStandardCategoryId).collect(Collectors.toSet());
                //有弃用盘口，需要重新排序
                Map<String, StandardMarketDataMessage> standardMarketMessageMap = standardMarketDataMessageList.stream().collect(Collectors.toMap(e -> e.getRelationMarketId().toString(), e -> e,(oldValue,newValue)->newValue));
                marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketMessageMap,standardMatchInfo,set,null,null,null,false);
                sw.stop();
            }
        }
        //篮球两项盘有盘口差设置，所以在这里再做删除弃用列表操作
        if (!CollectionUtils.isEmpty(giveUpMarketList))
        {
            standardMarketDataMessageList.removeAll(giveUpMarketList);
        }

        //取操盘两项盘玩法id集
        //取操盘两项盘玩法id集,并根据玩法分组
        Map<Long, List<StandardMarketDataMessage>> standardMarketMapMALAY = standardMarketDataMessageList.stream().filter(e -> marketCategoryIdMALAY.contains(e.getMarketCategoryId()))
                .collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));

        //篮球两项盘/足球三项盘口(两项盘)margin计算玩法 根据玩法分组
        Set<Long> finalMarketCategoryIdEUROPE = marketCategoryIdSet.stream().filter(marketCategoryIdEu::contains).collect(Collectors.toSet());
        Map<Long, List<StandardMarketDataMessage>> standardMarketMapEUROPE = standardMarketDataMessageList.stream().filter(e -> finalMarketCategoryIdEUROPE.contains(e.getMarketCategoryId()))
                .collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));

        //取剩余玩法id集，并根据玩法分组
        Set<Long> marketCategoryIdOTHER = marketCategoryIdSet.stream().filter(e -> !marketCategoryIdMALAY.contains(e) && !finalMarketCategoryIdEUROPE.contains(e)).collect(Collectors.toSet());
        Map<Long, List<StandardMarketDataMessage>> standardMarketMapOTHER = standardMarketDataMessageList.stream().filter(e -> marketCategoryIdOTHER.contains(e.getMarketCategoryId()))
                .collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));

        List<StandardMarketDataMessage> standardMarketMessageList = new ArrayList<>();
        //--------------操盘两项盘spread计算------------------
        if (!CollectionUtils.isEmpty(standardMarketMapMALAY)) {
            sw.start("两项盘MY计算总耗时");
            standardMarketMapMalay(linkId, standardMatchInfo, standardMarketMessageList, standardMarketMapMALAY);
            sw.stop();
        }
        //--------------三项盘/两项盘margin计算,其他球类使用数据商抽水赔率------------------
        if (!CollectionUtils.isEmpty(standardMarketMapEUROPE)) {
            sw.start("三项盘/两项盘EU计算总耗时");
            standardMarketMapEurope(linkId, standardMatchInfo, standardMarketMessageList, standardMarketMapEUROPE);
            sw.stop();
        }
        //------------其余玩法处理--------------------
        if (!CollectionUtils.isEmpty(standardMarketMapOTHER)) {
            sw.start("其余玩法计算总耗时");
            //循环遍历盘口信息，设置低赔和赔率差
            setOddsMetricAndLowOddsForMTS(linkId, standardMarketMessageList, standardMarketMapOTHER,standardMatchInfo);
            sw.stop();
        }
        if (!CollectionUtils.isEmpty(giveUpMarketList))
        {
            standardMarketMessageList.addAll(giveUpMarketList);
        }
        return standardMarketMessageList;
    }

    /**
     * 玩法分类计算 MY/EU
     *
     * @param marketCategoryIdSet
     * @param marketCategoryIdMALAY
     * @param marketCategoryIdEu
     * @param sportId
     */
    private static void marketCategoryDistinguish(Set<Long> marketCategoryIdSet, Set<Long> marketCategoryIdMALAY, List<Long> marketCategoryIdEu, Long sportId) {


        switch (sportId.intValue()) {
            case 1: //足球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.FootBall_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.FootBall_EU_CATEGORY);
                break;
            case 2: //篮球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BASKETBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BASKETBALL_EU_CATEGORY);
                break;
            case 3: //棒球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BASEBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BASEBALL_EU_CATEGORY);
                break;
            case 4: //冰球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.ICEBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.ICEBALL_EU_CATEGORY);
                break;
            case 5: //网球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.TENNIS_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.TENNIS_EU_CATEGORY);
                break;
            case 6: //美式足球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.AMERICAN_FOOTBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.AMERICAN_FOOTBALL_EU_CATEGORY);
                break;
            case 7: //斯诺克
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.SNOOKER_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.SNOOKER_EU_CATEGORY);
                break;
            case 9: //排球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.VOLLEYBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.VOLLEYBALL_EU_CATEGORY);
                break;
            case 8://乒乓球
            case 10://羽毛球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.TABLETENNIS_AND_BADMINTON_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.TABLETENNIS_AND_BADMINTON_EU_CATEGORY);
                break;
            case 11: //手球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.HANDBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.HANDBALL_EU_CATEGORY);
                break;
            case 12: //拳击
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BOXING_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BOXING_EU_CATEGORY);
                break;
            case 13: //沙滩排球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BEACH_VOLLEYBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BEACH_VOLLEYBALL_EU_CATEGORY);
                break;
            case 14: //橄榄球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.UK_FOOTBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.UK_FOOTBALL_EU_CATEGORY);
                break;
            case 15: //曲棍球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.HOCKEY_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.HOCKEY_EU_CATEGORY);
                break;
            case 16: //水球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.WATER_BALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.WATER_BALL_EU_CATEGORY);
                break;
            case 37: //板球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.CRICKET_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.CRICKET_EU_CATEGORY);
                break;
            default:
                break;
        }
    }

    private void standardMarketMapEurope(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessageList, Map<Long, List<StandardMarketDataMessage>> standardMarketMapEUROPE) {
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapEUROPE.entrySet()) {
            //获取玩法id
            Long marketCategoryId = entry.getKey();
            //获取key对应的盘口对象
            List<StandardMarketDataMessage> standardMarketDataMessagesList = entry.getValue();
            for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessagesList) {
                //操盘球种EU计算
                if (MarginCategoryConfig.SOPRT_TYPE.contains(standardMatchInfo.getSportId())) {
                    if (!CollectionUtils.isEmpty(standardMarketDataMessage.getMarketOddsList())) {
                        marginCalculateTransfer(linkId, standardMatchInfo.getId(), standardMarketDataMessage, marketCategoryId, standardMatchInfo.getSportId());
                    }
                } else {
                    //其他球种 盘口投注项 非空判断
                    if (!CollectionUtils.isEmpty(standardMarketDataMessage.getMarketOddsList())) {
                        //直接给数据商抽水赔率，不计算
                        standardMarketDataMessage.getMarketOddsList().stream().forEach(e -> e.setPaOddsValue(e.getOddsValue()));
                    }
                }
            }
            //处理最大盘口数量设置，多余的盘口设置为DEACTIVATED，margin优化计算， 有玩法支持附加盘 需要做最大盘口数校验
            if (MarginCategoryConfig.MARGIN_SPECIAL_CATEGORY.contains(marketCategoryId)) {
                processConfigMarketDisplayTrade(linkId, standardMarketDataMessagesList, standardMatchInfo);
            }
            standardMarketMessageList.addAll(standardMarketDataMessagesList);
        }
    }

    /**
     * 判断投注项数量
     * 足球margin计算存在两项盘
     */
    private void marginCalculateTransfer(String linkId, Long standardMatchId, StandardMarketDataMessage standardMarketDataMessage, Long marketCategoryId, Long sportId) {
        //计算有效盘口
        if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            return;
        }
        if (standardMarketDataMessage.getMarketOddsList().size() == 3) {
            processStandardMarketMarginEUROPE(linkId, standardMatchId, standardMarketDataMessage, marketCategoryId);
        } else if (standardMarketDataMessage.getMarketOddsList().size() == 2) {
            if (DataSourceCodeEnum.AO.code.equals(standardMarketDataMessage.getDataSourceCode())) {
                if (marketCategoryId == 352
                        || MarginCategoryConfig.BASKETBALL_EU_CATEGORY.contains(marketCategoryId)
                        || MarginCategoryConfig.TABLETENNIS_AND_BADMINTON_EU_CATEGORY.contains(marketCategoryId)) {
                    processStandardMarketTwoEUROPEAO(linkId, standardMatchId, standardMarketDataMessage, marketCategoryId, sportId);
                } else {
                    //直接给数据商抽水赔率，不计算
                    standardMarketDataMessage.getMarketOddsList().stream().forEach(e -> e.setPaOddsValue(e.getOddsValue()));
                }
            } else {
                processStandardMarketTwoEUROPE(linkId, standardMatchId, standardMarketDataMessage, marketCategoryId, sportId);
            }
        } else {
            log.info("::{}::marginCalculateTransfer投注项数量错误,标准赛事ID:{},玩法:{},standardMarketDataMessage:{}",
                    linkId, standardMatchId, marketCategoryId, JSON.toJSON(standardMarketDataMessage));
        }
    }

    private void standardMarketMapMalay(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessageList, Map<Long, List<StandardMarketDataMessage>> standardMarketMapMALAY) {
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapMALAY.entrySet()) {
            //获取key对应的盘口对象集合
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            //盘口弃用 AO TX盘口排序
            marketPlaceNumSort(linkId, standardMarketDataMessages);
            //---------处理有效盘口------------
            List<StandardMarketDataMessage> standardMarketsValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(standardMarketsValid)) {
                //开盘中的盘口计算和封装
                if (MarginCategoryConfig.TRADER_SUPPORT_SPORT.contains(standardMatchInfo.getSportId())) {
                    //AO不计算抽水，只计算水差
                    List<StandardMarketDataMessage> standardMarketDataMessageAo = standardMarketsValid.stream().filter(e -> DataSourceCodeEnum.AO.code.equals(e.getDataSourceCode())).collect(Collectors.toList());
                    processStandardMarketMALAYOther(linkId, standardMarketDataMessageAo);

                    List<StandardMarketDataMessage> standardMarketDataMessage = standardMarketsValid.stream().filter(e -> !DataSourceCodeEnum.AO.code.equals(e.getDataSourceCode())).collect(Collectors.toList());
                    //新算法计算
                    myCalculationMarketProcessor.calculationMarketProcessor(linkId, standardMatchInfo, entry.getKey(), standardMarketDataMessage);
                } else {
                    processStandardMarketMALAYOther(linkId, standardMarketsValid);
                }
            }
            //处理最大盘口数量设置，多余的盘口设置为DEACTIVATED
            processConfigMarketDisplayTrade(linkId, standardMarketDataMessages,standardMatchInfo);
            standardMarketDataMessages.forEach(standardMarketDataMessage -> standardMarketDataMessage.setCategoryType("MY"));
            standardMarketMessageList.addAll(standardMarketDataMessages);
        }
    }

    /**
     * 处理最大盘口数量设置，多余的盘口设置为DEACTIVATED
     *
     * @param standardMarketDataMessages
     */
    private void processConfigMarketDisplayTrade(String linkId, List<StandardMarketDataMessage> standardMarketDataMessages,StandardMatchInfo standardMatchInfo) {
        //收集不下发篮球的盘口数据
        List<StandardMarketDataMessage> standardMarketDataMessagesBasket = new ArrayList<>();
        //盘口类型
        Integer marketCountType = isOddsLive(standardMatchInfo.getId());
        //查询赛事玩法下的盘口设置
        int marketCount = 3;
        MarketCategorySell marketCategorySell = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), marketCountType, standardMarketDataMessages.get(0).getMarketCategoryId());
        for (StandardMarketDataMessage standardMarket : standardMarketDataMessages) {
            //a01盘口状态是弃用不处理最大盘数
            if(standardMarket.getDataSourceCode().equals(DataSourceCodeEnum.AO.getCode())
                    && Constant.SPORT_MARKET.STATUS.LOSE.equals(standardMarket.getStatus())){
                continue;
            }
            //不过滤最大盘口数
            if (MarginCategoryConfig.GREEN_MATEGORY.contains(standardMarket.getMarketCategoryId()))
            {
                continue;
            }
            if (null != marketCategorySell && null != marketCategorySell.getMarketCount()) {
                marketCount = marketCategorySell.getMarketCount();
            }
            //冠军玩法,不进行超过最大盘口数判断
            Integer marketType = standardMarket.getMarketType();
            if (marketType != 2 && null != standardMarket.getPlaceNum() && standardMarket.getPlaceNum() > marketCount) {
                standardMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarket.setRemark("超过最大盘口数，最大："+marketCount+",当前位置："+standardMarket.getPlaceNum());
                //不下发超过最大盘口数的盘口
                if (MarginCategoryConfig.TRADER_SUPPORT_SPORT.contains(standardMatchInfo.getSportId())) {
                    standardMarketDataMessagesBasket.add(standardMarket);
                }

                log.info("::{}::标准赛事id:{},运动种类:{},标准玩法:{},盘口类型:{},标准盘口id:{},统一盘口id:{},三方源盘口id:{},盘口位置:{},大于盘口数:{},盘口关盘",
                        linkId, standardMarket.getStandardMatchInfoId(), standardMatchInfo.getSportId(), standardMarket.getMarketCategoryId(), marketCountType,
                        standardMarket.getId(), standardMarket.getRelationMarketId(), standardMarket.getThirdMarketSourceId(), standardMarket.getPlaceNum(), marketCount);
            } else {
                log.info("::{}::标准赛事id:{},标准玩法:{},盘口类型:{},标准盘口id:{},统一盘口id:{},盘口位置:{},三方源盘口id:{},未超过最大盘口数，不进行关盘",
                        linkId, standardMarket.getStandardMatchInfoId(), standardMarket.getMarketCategoryId(), marketCountType,
                        standardMarket.getId(), standardMarket.getRelationMarketId(), standardMarket.getPlaceNum(), standardMarket.getThirdMarketSourceId());
            }
        }
        //移除多余盘口
        if (MarginCategoryConfig.TRADER_SUPPORT_SPORT.contains(standardMatchInfo.getSportId())) {
            if (standardMarketDataMessages.size() > standardMarketDataMessagesBasket.size())
                standardMarketDataMessages.removeAll(standardMarketDataMessagesBasket);
        }
    }

    /**
     * 坑位排序 AO
     */
    private void marketPlaceNumSort(String linkId, List<StandardMarketDataMessage> standardMarketDataMessages) {
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGroupAo = standardMarketDataMessages.stream().filter(e ->
                        e.getDataSourceCode().equals(DataSourceCodeEnum.AO.code) && e.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE))
                .collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        placeNumSort(linkId, marketCategoryGroupAo);
    }

    /**
     * @param marketCategoryGroup Map<玩法，盘集合>
     */
    private void placeNumSort(String linkId, Map<Long, List<StandardMarketDataMessage>> marketCategoryGroup) {
        if (MapUtils.isEmpty(marketCategoryGroup)) {
            return;
        }
        for (Map.Entry<Long, List<StandardMarketDataMessage>> marketCategory : marketCategoryGroup.entrySet()) {
            List<StandardMarketDataMessage> marketCategoryEntryValue = marketCategory.getValue();
            //坑位分组
            int b = 1;
            Map<Integer, List<StandardMarketDataMessage>> groupPlace = marketCategoryEntryValue.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
            for (Map.Entry<Integer, List<StandardMarketDataMessage>> placeEntry : groupPlace.entrySet()) {
                if (placeEntry.getKey() == 999) {
                    continue;
                }
                //盘口时间升序
                List<StandardMarketDataMessage> resultPlaceMarketDataMessages = placeEntry.getValue().stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                //坑位最新的盘口是弃用 ，设置当前坑位所有盘口坑位999
                if (Constant.SPORT_MARKET.STATUS.LOSE.equals(resultPlaceMarketDataMessages.get(0).getStatus())){
                    for (StandardMarketDataMessage marketDataMessage : resultPlaceMarketDataMessages) {
                        marketDataMessage.setPlaceNum(999);
                    }
                    continue;
                }
                for (StandardMarketDataMessage marketDataMessage : resultPlaceMarketDataMessages) {
                    marketDataMessage.setPlaceNum(b);
                }
                b++;
            }
        }
    }


    /**
     * 处理冠军盘口排序下发
     *  @param linkId
     * @param standardMatchInfo
     * @param marketIdSet
     * @param standardMarketMessageMap
     * @param dataSourceTime
     * @param changeCategoryOddsType
     */
    public void processOddsByOutright(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketIdSet, Map<String, StandardMarketDataMessage> standardMarketMessageMap, Long dataSourceTime, Map<Long, List<String>> changeCategoryOddsType) {
        if(CollectionUtils.isEmpty(marketIdSet)){
            log.info("::{}::标准赛事id:{},盘口集合为空,赔率不下发,盘口集合:{}", linkId, standardMatchInfo.getId(), JSON.toJSONString(marketIdSet));
            return;
        }
        //计算盘口及排序
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("panda冠军操盘获取玩法手自动配置");
        //盘口维度的操盘配置,直接一次性从库查出赛事玩法级的手自动类型，比循环查更快
        Map<Long, Integer> tradeTypeMap = outrightTradeTypeConfigService.getTradeTypeMapByMatchId(standardMatchInfo.getId(),marketIdSet);
        swCalculate.stop();
        swCalculate.start("panda冠军操盘全部盘口计算耗时");
        List<StandardMarketDataMessage> collect = standardMarketMessageMap.values().stream().filter(e -> marketIdSet.contains(e.getRelationMarketId())).collect(Collectors.toList());

        List<StandardMarketDataMessage> collectAUTO = Lists.newLinkedList();
        if ( null!=OutrightMarketOrderProcessor.orderMatchLocal.get() && OutrightMarketOrderProcessor.orderMatchLocal.get() == standardMatchInfo.getId()) {
            collectAUTO = collect;
        } else
        //-------------从缓存中取A操盘的盘口------------
        {
            collectAUTO = collect.stream().filter(e -> {
                Integer tradeType = 0;
                if(tradeTypeMap.get(e.getRelationMarketId()) != null){
                    tradeType =  tradeTypeMap.get(e.getRelationMarketId());
                }
                if(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeType)){
                    return true;
                }
                log.info("::{}::标准赛事id:{},盘口id:{},统一盘口id:{},三方盘口源id:{},M和A+模式不下发赔率,操盘类型:{}",
                        linkId, standardMatchInfo.getId(), e.getId(), e.getRelationMarketId(),  e.getThirdMarketSourceId(), tradeType);
                return false;
            }).collect(Collectors.toList());
        };

        //兼容冠军盘口的排序值(数据较少)
        List<StandardOutrightMarket> outrightMarketList = standardOutrightMarketService.selectOutrightMarketSellList(standardMatchInfo.getId());
            Map<Long, Integer> marketOrderMap = Maps.newConcurrentMap();
            if (!CollectionUtils.isEmpty(outrightMarketList)) {
            marketOrderMap = outrightMarketList.stream().collect(Collectors.toMap(StandardOutrightMarket::getId, StandardOutrightMarket::getMarketOrderNumber));
        }

        //构建下发给下游的list集合
        List<StandardMarketMessage> standardMarketMessageSendListAUTO = new ArrayList<>();
        for (StandardMarketDataMessage marketDataMsg : collectAUTO) {
            //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
            StandardMarketMessage standardMarketMessage = convertStandardMarketMessage(linkId, marketDataMsg, standardMatchInfo.getOperateMatchStatus(), true,true, changeCategoryOddsType);
            standardMarketMessage.setTradeType(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO);
            //盘口状态处理
            ConfigOutrightTradeMarket configOutrightTradeMarket = outrightTradeMarketConfigService.selectItem(standardMatchInfo.getId(), standardMarketMessage.getId());
            if ( null != configOutrightTradeMarket &&  null != configOutrightTradeMarket.getMarketStatus() ) {
                Integer status = standardMarketMessage.getStatus();
                Integer marketStatus = configOutrightTradeMarket.getMarketStatus();
                log.info("::{}::processOddsByOutright,oldStatus:{},marketStatus:{}", linkId, status, marketStatus);
                standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.CHAMPION_HANDICAP_STATUS.getCode(), configOutrightTradeMarket.getMarketStatus().toString()));
                standardMarketMessage.setPaStatus(marketStatus);
                standardMarketMessage.setStatus(marketStatus);
            }

            if (marketOrderMap.containsKey(standardMarketMessage.getId())) {
                standardMarketMessage.setOrderNo(marketOrderMap.get( standardMarketMessage.getId()) );
            }


            if(!CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())){
                for(StandardMarketOddsMessage marketOddsMessage : standardMarketMessage.getMarketOddsList()){
                    //三方数据源状态赋值
                    marketOddsMessage.setThirdSourceActive(marketOddsMessage.getActive());
                    //投注项状态处理
                    ConfigOutrightTradeOdds configOutrightTradeOdds = outrightTradeOddsConfigService.selectItem(standardMatchInfo.getId(), marketOddsMessage.getId());
                    if(null != configOutrightTradeOdds){
                        marketOddsMessage.setActive(configOutrightTradeOdds.getOddsStatus());
                        marketOddsMessage.setPaActiveReason("冠军操盘，投注项状态为：【"+configOutrightTradeOdds.getOddsStatus() + "】");
                    }
                    /**
                     * P1=1/抽水赔率，（截取保留4位小数）
                     * P2=概率变化/100 （截取保留4位小数）
                     * odds=1/(P1+P2)；(赔率截取保留2位小数)；
                     */
                    ConfigOutrightTradeProbability configOutrightTradeProbability = outrightTradeProbabilityConfigService.selectItem(standardMatchInfo.getId(), marketOddsMessage.getId());
                    Integer paOddsValue = marketOddsMessage.getOddsValue();
                    if(null != configOutrightTradeProbability && null != paOddsValue  && 0 != paOddsValue){
                        BigDecimal p1 = new BigDecimal(100000).divide(new BigDecimal(paOddsValue),4, BigDecimal.ROUND_DOWN);
                        BigDecimal p2 = new BigDecimal(configOutrightTradeProbability.getProbability()).divide(new BigDecimal(100),4, BigDecimal.ROUND_DOWN);
                        BigDecimal divide = new BigDecimal(1).divide(p1.add(p2), 4, BigDecimal.ROUND_HALF_UP);
                        DecimalFormat dFormat = new DecimalFormat();
                        dFormat.setMaximumFractionDigits(2);
                        dFormat.setGroupingSize(0);
                        dFormat.setRoundingMode(RoundingMode.FLOOR);
                        Integer oddsValue = new BigDecimal(dFormat.format(divide)).multiply(new BigDecimal(100000)).intValue();
                        marketOddsMessage.setPaOddsValue(oddsValue);
                        if(oddsValue < 1.01 * 100000){
                            marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                            marketOddsMessage.setPaActiveReason("跳水后，赔率值不得低于1.01,賠率值:" + oddsValue);
                        }else if(oddsValue > 1001 * 100000){
                            marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                            marketOddsMessage.setPaActiveReason("跳水后，赔率值最高不能超过1001,賠率值:" + oddsValue);
                        }
                        log.info("::{}::processOddsByOutright,盘口id:{},投注项id:{},概率差:{}，计算前赔率：{}，计算后赔率：{}", linkId, standardMarketMessage.getId(), marketOddsMessage.getId(),
                                configOutrightTradeProbability.getProbability(), paOddsValue, oddsValue);

                    }
                }
            }
            //赔率合法性校验
            if (null != standardMarketMessage.getMarketOddsList()) {
                Integer activeOddsNum = 0;
                for (StandardMarketOddsMessage message : standardMarketMessage.getMarketOddsList()) {
                    if(message.getPaOddsValue() == null || message.getPaOddsValue() <= 100000){
                        //投注项赔率不合法时，只封当前投注项
                        message.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                        message.setPaActiveReason("投注项赔率不合法，赔率小于1，投注项封盘");
                        log.info("::{}::processOddsByOutright赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率:{}",
                                linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), message.getPaOddsValue());
                    }
                    Integer activeStatus = message.getActive();
                    if ( Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(activeStatus) ) {
                        activeOddsNum  += 1;
                    }
                }
                if ( 0 == activeOddsNum ) {
                    standardMarketMessage.setPaStatusReason("无开售投注项，盘口封盘");
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::无开售投注项，盘口封盘, 盘口id:{}", linkId, standardMarketMessage.getId() );
                }
            }
            //赔率优化(两项盘小数位优化)
            processOddsValueDecimals(linkId, standardMarketMessage, standardMatchInfo);
            if ( null == standardMarketMessage.getNumberOfWinners() || standardMarketMessage.getNumberOfWinners() < 1 ) {
                standardMarketMessage.setNumberOfWinners(1);
            }
            standardMarketMessageSendListAUTO.add(standardMarketMessage);
        }
        //-------------赔率下发-----------------
        if(!CollectionUtils.isEmpty(standardMarketMessageSendListAUTO)){
            standardMatchMarketOddsLinkageProcessor.championMarketOddsMainLinkage(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            int batchSize = 5;
            for (int i = 0; i < standardMarketMessageSendListAUTO.size(); i += batchSize) {
                List<StandardMarketMessage> batch = standardMarketMessageSendListAUTO.subList(i, Math.min(i + batchSize, standardMarketMessageSendListAUTO.size()));
                String batchLinkId = linkId + (i / batchSize + 1);
                List<Long> batchMarketIds = batch.stream().map(StandardMarketMessage::getId).collect(Collectors.toList());
                log.info("::{}::赔率分批下发,批次:{},本批standardMarketId:{}", batchLinkId, i / batchSize + 1, batchMarketIds);
                standardMarketOddsProducer.standardMarketOddsAsyncSend(batchLinkId, standardMatchInfo, batch, dataSourceTime, false);
            }
        }
        log.info("::{}::panda冠军操盘全部盘口计算耗时{}ms," + swCalculate.prettyPrint(), linkId, swCalculate.getTotalTimeMillis());

    }

    /**
     * 处理MTS的盘口排序下发（MTS)
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     * @param standardMarketMessageMap
     * @param dataSourceTime
     */
    public void processOddsByMts(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, Map<String, StandardMarketDataMessage> standardMarketMessageMap, Long dataSourceTime, Boolean isMain) {
        log.info("::{}::标准赛事id:{},mts操盘本次处理的标准玩法id:{},缓存map集合大小:{}", linkId, standardMatchInfo.getId(), marketCategoryIdSet, standardMarketMessageMap.size());
        //取本次有改变的玩法,排序
        Map<Long, List<StandardMarketDataMessage>> standardMarketMapMTS = standardMarketMessageMap.values().stream().filter(e -> marketCategoryIdSet.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
        if (CollectionUtils.isEmpty(standardMarketMapMTS)) {
            return;
        }
        //删除球头
        redisService.del(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET_ODDS_LAST + standardMatchInfo.getId());
        //盘口处理，排序，设置抽水赔
        List<StandardMarketDataMessage> standardMarketMessageList = new ArrayList<>();

        //循环遍历盘口信息，设置低赔和赔率差
        setOddsMetricAndLowOddsForMTS(linkId, standardMarketMessageList, standardMarketMapMTS, standardMatchInfo);
        //球头下发给AO
        thirdMarketBallHeadProcessor.sendBallHeadAo(linkId, standardMatchInfo, standardMarketMessageList, dataSourceTime);
        //构建下发给下游的list集合
        List<StandardMarketMessage> standardMarketMessageSendListMTS = new ArrayList<>();
        //封装为可投递的StandardMarketMessage
        standardMarketMessageList.forEach(standardMarketDataMessage -> {
            //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
            StandardMarketMessage standardMarketMessage = convertStandardMarketMessage(linkId, standardMarketDataMessage, standardMatchInfo.getOperateMatchStatus(), true, false, new HashMap<>());
            if (MarginCategoryConfig.BASKETBALL_PLAYER_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())) {
                processPlayerTeamFlag(linkId, standardMatchInfo, standardMarketMessage);
            }
            //球员玩法多语言name_code处理
            processPlayerNameCode(linkId, standardMatchInfo.getSportId(), standardMarketMessage);
            standardMarketMessageSendListMTS.add(standardMarketMessage);
        });
        //XTS 两项盘有一个投注项未激活改为关
        xtsMarketOddsActive(linkId, standardMarketMessageSendListMTS);
        //小数点优化
        processOddsValueDecimalsXts(standardMarketMessageSendListMTS);
        //设置马来赔
        convertXtsMalayOddsValue(standardMarketMessageSendListMTS);
        List<StandardMarketMessage> finalMessages = standardMarketMessageSendListMTS;
        if (!footballMarketValidateService.shouldValidateFootball(standardMatchInfo)) {
            // 数据商全封和全关判断
            transformStatIfSatisfyCond(linkId, standardMatchInfo, finalMessages);
            //开盘比分校验
            checkMarketsSoreProcessor.check(linkId, standardMatchInfo, finalMessages);
            //盘口开关封锁 按开关优先级 (A > B > C )调整盘口状态
            dealMarketStatusProcessor.dealMarketStatusList(linkId, finalMessages, standardMatchInfo);
            //盘口时间戳校验
            //standardMarketVerifyModifyTime(linkId, standardMatchInfo, finalMessages);
            //盘口状态校验
            //standardMarketStatusCheck(linkId, standardMatchInfo, finalMessages);
            //最后下发赔率 ，自动关盘兜底
            automaticClosing(linkId, standardMatchInfo, finalMessages);
        } else {
            finalMessages = footballMarketValidateService.validateFootball(linkId,
                                                           standardMatchInfo,
                                                           standardMarketMessageSendListMTS,
                                                           MarketHandlingEnum.MTS);
        }
        saveTheLastMarketOddsToReids(linkId, standardMatchInfo, marketCategoryIdSet, finalMessages, dataSourceTime, isMain);
        saveTheLastAMarketOddsToReids(linkId, standardMatchInfo, marketCategoryIdSet, finalMessages, dataSourceTime, isMain);
        thirdMarketStatusProcess(standardMatchInfo,finalMessages);
        //-------------赔率下发-----------------
        standardMarketOddsProducer.standardMarketOddsAsyncSendByRisk(linkId, standardMatchInfo, finalMessages, dataSourceTime,true);
    }

    /**
     * 删除构建缓存
     *
     * @param linkId
     * @param standardMatchInId
     * @param clearMarketCategoryId
     */
    public void delConvertMarket(String linkId, Long standardMatchInId, Long clearMarketCategoryId) {
        String redisConvertKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CONVERT_MARKET + standardMatchInId);
        Map<String, StandardMarketDataMessage> standardMarketConvertMessageMap = redisService.hGetAll(redisConvertKey);
        if (!CollectionUtils.isEmpty(standardMarketConvertMessageMap)) {
            standardMarketConvertMessageMap.forEach((k, v) -> {
                if (clearMarketCategoryId.equals(v.getMarketCategoryId())) {
                    redisService.hDel(redisConvertKey, k);
                    log.info("::{}::盘口来源删除构建盘口缓存,赛事ID:{},盘口ID:{},k:{},三方盘口ID:{},玩法ID:{}",
                            linkId, standardMatchInId, v.getRelationMarketId(), k, v.getThirdMarketSourceId(), v.getMarketCategoryId());
                }
            });
        } else {
            log.info("::{}::盘口来源删除构建盘口缓存不存在,赛事ID:{},玩法ID:{}", linkId, standardMatchInId, clearMarketCategoryId);
        }
    }
     /**
     * TX 排序
     * 坑位水差设置在坑位：1
     * 坑位：1关 坑位：2开  坑位：3开
     * 坑位：2 - > 1 用坑位1水差  坑位：3 - > 2
     */
    public void txMarketSort(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessages) {
        if (!MarginCategoryConfig.TX_SORT_SPORT.contains(standardMatchInfo.getSportId())) {
            return;
        }
        int a = 1;
        //其他数据源开盘条数，只会在切换数据源才处理
        List<StandardMarketDataMessage> standardMarketDataMessageOther = standardMarketDataMessages.stream().filter(e ->
                !DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(standardMarketDataMessageOther)) {
            a = standardMarketDataMessageOther.size() + 1;
        }
        //只处理TX 开封盘口
        List<StandardMarketDataMessage> txStandardMarketDataMessage = standardMarketDataMessages.stream().filter(e ->
                DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());


        int b = txMarketSortDispose(linkId, marketCategoryId, standardMatchInfo, a, txStandardMarketDataMessage, "处理开封盘口");

        //处理TX 非开封盘口
        List<StandardMarketDataMessage> txStandardMarketDataMessageError = standardMarketDataMessages.stream().filter(e ->
                DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && e.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(txStandardMarketDataMessageError)) {
            return;
        }
        txMarketSortDispose(linkId, marketCategoryId, standardMatchInfo, b, txStandardMarketDataMessageError, "处理非开封盘口");
    }

    public int txMarketSortDispose(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, int a,
                                   List<StandardMarketDataMessage> standardMarketDataMessage, String remark) {
        if (CollectionUtils.isEmpty(standardMarketDataMessage)) {
            return a;
        }
        //TX位置分组,
        Map<Integer, List<StandardMarketDataMessage>> groupPlace = standardMarketDataMessage.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
        //Map<TX坑位, 排序后坑位>
        Map<Integer, Integer> sortPlaceMap = new HashMap<>();
        Set<Integer> placeNums = groupPlace.keySet();
        for (int placeNum : placeNums) {
            sortPlaceMap.put(placeNum, a);
            a++;
        }
        log.info("::{}::{},TX位置排序,赛事ID:{},玩法:{},排序信息:{},处理条数:{}",
                linkId, remark, standardMatchInfo.getId(), marketCategoryId, JSONObject.toJSONString(sortPlaceMap), standardMarketDataMessage.size());
        standardMarketDataMessage.forEach(t -> {
            t.setTxPlaceNum(sortPlaceMap.get(t.getPlaceNum()));
            t.setPlaceNumId(t.getStandardMatchInfoId() + "_" + t.getMarketCategoryId() + "_" + t.getChildMarketCategoryId() + "_" + t.getPlaceNum());

        });
        return a;
    }

    /**
     * 根据数据源的抽水赔率排序
     * @param linkId
     * @param standardMarketMessageMap
     * @param standardMatchInfo
     * @param marketCategoryIdSet
     */
//    public void setOddsOrderByOddsValue(String linkId, Map<String, StandardMarketDataMessage> standardMarketMessageMap, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, Set<Long> oddsTypeIdSet, Set<Long> categorySet, Set<Long> riskCategorySet, Boolean isTrue)
//    {
//        //下发滚球标识赛前盘直接关盘处理，兜底 newClosePreMarkets()
//        int liveFlag = isOddsLive(standardMatchInfo.getId());
//        //设置子玩法id
//        standardMarketMessageMap.forEach((k,v)->{
//            if (liveFlag == 0 && v.getMarketType() == 1 && v.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
//                v.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//                v.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//                log.info("::{}::赛事ID:{},三方盘口ID:{},标准盘口ID:{},关闭赛前盘兜底。"
//                        , linkId, standardMatchInfo.getId(), v.getThirdMarketSourceId(), v.getRelationMarketId());
//            }
//            v.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(linkId,v.getMarketCategoryId(),
//                    v.getAddition1(),v.getAddition2(),v.getAddition3(),
//                    v.getAddition4(),v.getAddition5(),String.valueOf(v.getStandardMatchInfoId())));
//            //盘口时间戳判断关盘
//            marketTimeClose(linkId, liveFlag, standardMatchInfo, v);
//        });
//        //取本次有改变的玩法,排序
//        Map<Long, List<StandardMarketDataMessage>> standardMarketMapMTS = standardMarketMessageMap.values().stream().filter(e -> marketCategoryIdSet.contains(e.getMarketCategoryId()))
//                .collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
//        if (CollectionUtils.isEmpty(standardMarketMapMTS)) {
//            return;
//        }
//        Map<Long, Integer> tradeTypeMap = configTradeTypeService.getItemByMatchAndCategorys(standardMatchInfo.getId().toString(),marketCategoryIdSet);
//        log.info("::{}::数据源赔率开始排序以及计算挡板:操盘方式:{}",linkId,tradeTypeMap);
//        //循环遍历盘口信息,设置低赔和赔率差
//        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapMTS.entrySet()) {
//            //获取key对应的盘口对象集合
//            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
//            //只有数据商相关操盘才需要走挡板校验
//            boolean isNeedFlap = Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(tradeTypeMap.get(standardMarketDataMessages.get(0).getMarketCategoryId()))
//                    ||Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeMap.get(standardMarketDataMessages.get(0).getMarketCategoryId()));
//            //TX坑位处理
//            //txMarketSort(linkId,entry.getKey(),standardMatchInfo,standardMarketDataMessages);
//            //判断盘口来源是否有数据商盘口
//            boolean isMarketSource = standardMarketDataMessages.stream().filter(e-> e.getMarketType() == 0 ).anyMatch(s -> s.getMarketSource() == 0);
//            if (isMarketSource) {
//                //关盘构建滚球盘口
//                standardMarketDataMessages.forEach(s -> {
//                    if (s.getMarketSource() == 1) {
//                        log.info("::{}::盘口来源存在数据商盘口,赛事ID:{},盘口ID:{},三方盘口ID:{},,玩法ID:{},关闭构建盘口。",
//                                linkId, standardMatchInfo.getId(), s.getRelationMarketId(), s.getThirdMarketSourceId(), s.getMarketCategoryId());
//                        s.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//                        s.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//                        s.setMarketSource(0);
//                    }
//                });
//                //根据玩法删除构建盘口缓存
//                delConvertMarket(linkId, standardMatchInfo.getId(), entry.getKey());
//            }
//            //篮球AO坑位
//            if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
//                aoMarketPlaceMerge(linkId, standardMatchInfo, standardMarketDataMessages, false);
//                txMarketPlaceMerge(linkId, standardMatchInfo, standardMarketDataMessages, false);
//            }
//            //比分兜底
//            scoreHandicapProcessor.scoreMatchingMarket(linkId, standardMatchInfo, standardMarketDataMessages);
//            //取盘口中有投注项的有效数据
//            List<StandardMarketDataMessage> standardMarketsValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList())&&e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
//            //排序字段placeNum
//            int placeNum = 1;
//            //------------处理有效盘口的排序-----------
//            if (!CollectionUtils.isEmpty(standardMarketsValid)) {
//                //第一步：计算赔率差和低赔
//                Map<Long,String> oldAddtion1Map = new HashMap<>();
//                AtomicBoolean isOne = new AtomicBoolean(Boolean.TRUE);
//                //找出原初始主盘球头
//                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessages) {
//                    if (!DataSourceCodeEnum.TX.code.equals(standardMarketDataMessage.getDataSourceCode()) &&
//                            !DataSourceCodeEnum.AO.code.equals(standardMarketDataMessage.getDataSourceCode())) {
//                        if (standardMarketDataMessage.getPlaceNum() != null
//                                && !CollectionUtils.isEmpty(categorySet)
//                                && standardMarketDataMessage.getPlaceNum() == 1
//                                && categorySet.contains(standardMarketDataMessage.getMarketCategoryId())) {
//                            oldAddtion1Map.put(standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getOldAddition1());
//                            break;
//                        }
//                    } else {
//                        if (isOne.get()) {
//                            //TX获取缓存最新的球头值
//                            String setBall = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_BALL + standardMatchInfo.getId();
//                            Object ballObj = redisService.hGet(setBall, String.valueOf(entry.getKey()));
//                            if (!Objects.isNull(ballObj)) {
//                                oldAddtion1Map.put(entry.getKey(), String.valueOf(ballObj));
//                                log.info("::{}::TX上次球头值:{},赛事ID:{}", linkId, oldAddtion1Map, standardMatchInfo.getId());
//                            }
//                            isOne.set(Boolean.FALSE);
//                        }
//                    }
//                }
//                //LS bet365盘口处理
//                //betMarketValueProcessor(linkId, standardMatchInfo, entry.getKey(), standardMarketDataMessages, oldAddtion1Map);
//                //第一步：计算赔率差和低赔
//                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
//                    //获取盘口投注项
//                    List<StandardMarketOddsDataMessage> marketOddsList = standardMarketDataMessage.getMarketOddsList();
//                    Integer minOddsValue = 0;
//                    Integer maxOddsValue = 0;
//                    //循环遍历盘口投注项
//                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : marketOddsList) {
//                        //设置pa赔率：数据源抽水赔率
//                        if (null == standardMarketOddsDataMessage.getOddsValue()) {
//                            standardMarketOddsDataMessage.setOddsValue(0);
//                        }
//                        if (null == standardMarketOddsDataMessage.getOriginalOddsValue()) {
//                            standardMarketOddsDataMessage.setOriginalOddsValue(0);
//                        }
//                        if (standardMarketOddsDataMessage.getOriginalOddsValue() > maxOddsValue) {
//                            maxOddsValue = standardMarketOddsDataMessage.getOriginalOddsValue();
//                        }
//                        if (standardMarketOddsDataMessage.getOriginalOddsValue() < minOddsValue || minOddsValue == 0) {
//                            minOddsValue = standardMarketOddsDataMessage.getOriginalOddsValue();
//                        }
//                    }
//                    //计算赔率差
//                    Integer oddsMetric = maxOddsValue - minOddsValue;
//                    standardMarketDataMessage.setOddsMetric(oddsMetric);
//                }
//                log.info("::{}::数据源赔率开始排序以及计算挡板:主盘球头map:{}",linkId,oldAddtion1Map);
//                //第二步：排序，依据三方源盘口状态、赔率差、低赔
//                ListUtils.sort(standardMarketsValid, true, "status", "oddsMetric", "oddsValue");
//                //排序，篮球特殊处理
//                if ((StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Tennis.code.equals(standardMatchInfo.getSportId()) ||
//                        StandardSportTypeEnum.TableTennis.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Vollyball.code.equals(standardMatchInfo.getSportId()) )
//                        && MarginCategoryConfig.CHANGE_FLAP1.contains(standardMarketDataMessages.get(0).getMarketCategoryId())
//                        && standardMarketsValid.size() > 1
//                        && isNeedFlap)
//                {
//                    basketSetOrderByCategory(standardMarketsValid);
//                }
//                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
//                    //初始化赔率分控挡板状态，避免之前缓存中的盘口数据影响
//                    standardMarketDataMessage.setCategorySuspended(0);
//                    if (placeNum == 1 && (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) ||
//                            StandardSportTypeEnum.Badminton.code.equals(standardMatchInfo.getSportId()) ||
//                            StandardSportTypeEnum.Soccer.code.equals(standardMatchInfo.getSportId()))
//                            && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getThirdMarketSourceStatus())
//                            && isNeedFlap)//处理篮球挡板逻辑
//                    {
//                        if (null != oddsTypeIdSet)//处理赔率挡板逻辑
//                        {
//                            //篮球
//                            if (MarginCategoryConfig.CHANGE_FLAP.contains(standardMarketDataMessage.getMarketCategoryId()))
//                            {
//                                basketDataFlap(linkId,standardMarketDataMessage,oddsTypeIdSet,riskCategorySet);
//                            }
//                        }
//                        if(null != oldAddtion1Map.get(standardMarketDataMessage.getMarketCategoryId())
//                                &&!CollectionUtils.isEmpty(categorySet)
//                                && categorySet.contains(standardMarketDataMessage.getMarketCategoryId()))//处理球头挡板逻辑
//                        {
//                            //篮球、网球
//                            if (Math.abs(Double.parseDouble(oldAddtion1Map.get(standardMarketDataMessage.getMarketCategoryId()))-Double.parseDouble(standardMarketDataMessage.getAddition1())) >= MarginCategoryConfig.BASKETBALL_FLAP_ADDTION1_DOUBLE)
//                            {
//                                log.info("::{}::数据源挡板计算后需要下发报警消息，flag:{},oldAddtion1:{},newAddtion1:{}",linkId,true,oldAddtion1Map.get(standardMarketDataMessage.getMarketCategoryId()),standardMarketDataMessage.getAddition1());
//                                riskCategorySet.add(standardMarketDataMessage.getMarketCategoryId());
//                                standardMarketDataMessage.setCategorySuspended(1);
//                                // 发送操盘日志给风控
//                                StandardMarketMessage logData = new StandardMarketMessage();
//                                logData.setMarketCategoryId(standardMarketDataMessage.getMarketCategoryId());
//                                logData.setMarketType(standardMarketDataMessage.getMarketType());
//                                logData.setPaStatus(standardMarketDataMessage.getStatus());
//                                logData.setId(standardMarketDataMessage.getId());
//                                commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo, logData);
//                            }
//                        }
//                    }
//                    if (!MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarketDataMessage.getDataSourceCode()) || null == standardMarketDataMessage.getPlaceNum()) {
//                        standardMarketDataMessage.setPlaceNum(placeNum);
//                    }
//                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarketDataMessage.getMarketOddsList()) {
//                        //设置pa赔率：数据源抽水赔率
//                        standardMarketOddsDataMessage.setPaOddsValue(standardMarketOddsDataMessage.getOriginalOddsValue());
//                    }
//
//                    log.info("::{}::盘口排序后,标准赛事id:{},标准盘口id:{},统一盘口id:{},玩法:{},子玩法:{},盘口位置:{},三方盘口源id:{},三方盘口源状态:{},盘口状态:{},赔率差值:{},低赔:{},球头:{},盘口时间:{}",
//                            linkId, standardMatchInfo.getId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getMarketCategoryId(),standardMarketDataMessage.getChildMarketCategoryId(),
//                            standardMarketDataMessage.getPlaceNum(), standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getThirdMarketSourceStatus(),
//                            standardMarketDataMessage.getStatus(), standardMarketDataMessage.getOddsMetric(), standardMarketDataMessage.getPaOddsValue(), standardMarketDataMessage.getMarketOddsValue(), standardMarketDataMessage.getModifyTime());
//                    placeNum = placeNum + 1;
//                }
//            }
//            //------------处理无效盘口的排序-----------
//            //取盘口中无投注项的数据，下面的操作是为了保证关盘时，不影响盘口之前的排序
//            List<StandardMarketDataMessage> standardMarketsInvalid = standardMarketDataMessages.stream().filter(e -> (CollectionUtils.isEmpty(e.getMarketOddsList()) || e.getThirdMarketSourceStatus()>= Constant.SPORT_MARKET.STATUS.DEACTIVATED)&&e.getPlaceNum()!=null).collect(Collectors.toList());
//            ListUtils.sort(standardMarketsInvalid, true, "placeNum");
//            List<StandardMarketDataMessage> standardMarketsInvalid2 = standardMarketDataMessages.stream().filter(e -> (CollectionUtils.isEmpty(e.getMarketOddsList()) || e.getThirdMarketSourceStatus()>= Constant.SPORT_MARKET.STATUS.DEACTIVATED)&&e.getPlaceNum()==null).collect(Collectors.toList());
//            if (!CollectionUtils.isEmpty(standardMarketsInvalid2)) {
//                standardMarketsInvalid.addAll(standardMarketsInvalid2);
//            }
//            if (!CollectionUtils.isEmpty(standardMarketsInvalid)) {
//                for (StandardMarketDataMessage standardMarket : standardMarketsInvalid) {
//                    if (Constant.SPORT_MARKET.STATUS.SETTLED.equals(standardMarket.getThirdMarketSourceStatus())
//                            || Constant.SPORT_MARKET.STATUS.CANCELLED.equals(standardMarket.getThirdMarketSourceStatus())) {
//                        standardMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//                    }
//                    if (!MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarket.getDataSourceCode())) {
//                        standardMarket.setPlaceNum(placeNum++);
//                    }
//                    if (!CollectionUtils.isEmpty(standardMarket.getMarketOddsList()))
//                    {
//                        for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarket.getMarketOddsList()) {
//                            //设置pa赔率：数据源抽水赔率
//                            standardMarketOddsDataMessage.setPaOddsValue(0);
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * 篮球排序-结合足球的规则得出主盘后再次细分
     * 总分类玩法：第1副盘为球头值大一个阶梯 第2副盘为球头值小一个阶梯，
     * 如盘口数递增则以此类推奇数为大一阶梯，偶数为小一阶梯，
     * 阶梯为球头差每个赛事有可能会不一样
     * 让分类玩法 主队为让球方：第1副盘为球头值大一个阶梯 第2副盘为球头值小一个阶梯 ，
     * 如盘口数递增则以此类，推奇数为大一阶梯，偶数为小一阶梯
     * 让分类玩法 客队为让球方：第1副盘为球头值小一个阶梯 第2副盘为球头值大一个阶梯，
     * 如盘口数递增则以此类推，奇数为小一阶梯，偶数为大一阶梯
     * @param standardMarketsValid
     */
    private void basketSetOrderByCategory(List<StandardMarketDataMessage> standardMarketsValid)
    {
        List<StandardMarketDataMessage> bigZeroList = new ArrayList<>();
        List<StandardMarketDataMessage> smallZeroList = new ArrayList<>();
        StandardMarketDataMessage standardMarketDataMessageOne = standardMarketsValid.get(0);
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
            Double temp = Double.parseDouble(standardMarketDataMessageOne.getAddition1()) - Double.parseDouble(standardMarketDataMessage.getAddition1());
            if (temp > 0)
            {
                bigZeroList.add(standardMarketDataMessage);
            }
            else if (temp < 0)
            {
                smallZeroList.add(standardMarketDataMessage);
            }
            standardMarketDataMessage.setMarketOddsValue(Math.abs(temp));
        }
        if (bigZeroList.size() > 1)
        {
            ListUtils.sort(bigZeroList, true, "status","marketOddsValue");
        }
        if (smallZeroList.size() > 1)
        {
            ListUtils.sort(smallZeroList, true, "status","marketOddsValue");
        }
        standardMarketsValid.clear();
        standardMarketsValid.add(standardMarketDataMessageOne);
        int i = 0;
        boolean CHANGE_FLAP2_FLAG = MarginCategoryConfig.CHANGE_FLAP2.contains(standardMarketDataMessageOne.getMarketCategoryId());
        boolean CHANGE_FLAP3_FLAG = MarginCategoryConfig.CHANGE_FLAP3.contains(standardMarketDataMessageOne.getMarketCategoryId());
        do {
            if (CHANGE_FLAP2_FLAG) {
                if (smallZeroList.size() > i) {
                    standardMarketsValid.add(smallZeroList.get(i));
                }
                if (bigZeroList.size() > i) {
                    standardMarketsValid.add(bigZeroList.get(i));
                }
            } else if (CHANGE_FLAP3_FLAG) {
                if (bigZeroList.size() > i) {
                    standardMarketsValid.add(bigZeroList.get(i));
                }
                if (smallZeroList.size() > i) {
                    standardMarketsValid.add(smallZeroList.get(i));
                }
            }
            i++;
        } while (i < smallZeroList.size() || i < bigZeroList.size());
    }

    /**
     * 篮球-独赢类型 首先获得x分类型：当赔率单次变动大于等于0.3则程序主动封盘
     * @param linkId
     * @param standardMarketDataMessage
     * @param oddsTypeIdSet
     * @param riskCategorySet
     */
    private void basketDataFlap(String linkId,StandardMarketDataMessage standardMarketDataMessage,Set<Long> oddsTypeIdSet,Set<Long> riskCategorySet)
    {
        boolean flag = true;
        for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarketDataMessage.getMarketOddsList()) {
            if (standardMarketDataMessage.getMarketType() == 0 && MarginCategoryConfig.CHANGE_FLAP_BAK.contains(standardMarketDataMessage.getMarketCategoryId()))
            {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},盘口类型：{}",linkId,standardMarketDataMessage.getRelationMarketId(),standardMarketDataMessage.getMarketType());
                break;
            }
            if (standardMarketOddsDataMessage.getOldOriginalOddsValue() == null || standardMarketOddsDataMessage.getOldOriginalOddsValue().equals(0))
            {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},paoddsvalue：{}",linkId,standardMarketDataMessage.getRelationMarketId(),standardMarketOddsDataMessage.getPaOddsValue());
                break;
            }
            if (!oddsTypeIdSet.contains(standardMarketOddsDataMessage.getRelationMarketOddsId()))
            {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},getRelationMarketOddsId:{}",linkId,standardMarketDataMessage.getRelationMarketId(),standardMarketOddsDataMessage.getRelationMarketOddsId());
                break;
            }
            if (Math.abs(standardMarketOddsDataMessage.getOldOriginalOddsValue() - standardMarketOddsDataMessage.getOriginalOddsValue()) < MarginCategoryConfig.BASKETBALL_FLAP_ODDSVALUE_DOUBLE)
            {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},result:{}",linkId,standardMarketDataMessage.getRelationMarketId(),Math.abs(standardMarketOddsDataMessage.getOldOriginalOddsValue() - standardMarketOddsDataMessage.getOriginalOddsValue()) < MarginCategoryConfig.BASKETBALL_FLAP_ODDSVALUE_DOUBLE);
                break;
            }
        }
        if (flag)
        {
            log.info("::{}::数据源挡板计算后需要下发报警消息，flag:{},standardMarketOddsDataMessage:{}",linkId,true,JSONUtil.toJsonStr(standardMarketDataMessage));
            riskCategorySet.add(standardMarketDataMessage.getMarketCategoryId());
        }
    }
    /**
     * 给MTS的盘口设置排序，抽水赔
     *
     * @param standardMarketMapMTS
     */
    public void setOddsMetricAndLowOddsForMTS(String linkId, List<StandardMarketDataMessage> standardMarketMessageList, Map<Long, List<StandardMarketDataMessage>> standardMarketMapMTS, StandardMatchInfo standardMatchInfo) {
        //循环遍历盘口信息,设置低赔和赔率差
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapMTS.entrySet()) {
            //获取key对应的盘口对象集合
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            //取盘口中有投注项的有效数据
            List<StandardMarketDataMessage> standardMarketsValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList())).collect(Collectors.toList());
            //------------处理有效盘口的排序-----------
            if (!CollectionUtils.isEmpty(standardMarketsValid)) {
                //第一步：计算赔率差和低赔
                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
                    //计算有效盘口
                    if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
                        continue;
                    }
                    //852需求 查询独赢配置获取多项盘概率差,只对足球处理
                    Map<String, ConfigMarketMarginGap> marginGapMap = new HashMap<>();
                    Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
                    if ((standardMatchInfo.getSportId() == 1 || standardMatchInfo.getSportId() == 2 || MarginCategoryConfig.COMPLEX_SPORTIDS.contains(standardMatchInfo.getSportId())) && standardMarketDataMessage.getMarketOddsList().size()>3 ) {
                        log.info("::{}::查询独赢配置获取多项盘概率差,赛事ID:{},统一盘口ID:{},玩法ID:{},坑位ID:{}",
                                linkId, standardMatchInfo.getId(), standardMarketDataMessage.getRelationMarketId(), marketCategoryId, standardMarketDataMessage.getPlaceNum());
                        if (standardMarketDataMessage.getPlaceNum() != null) {
                            List<ConfigMarketMarginGap> itemList = configMarketMarginGapService.getItemList(standardMatchInfo.getId(), marketCategoryId,standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum());
                            if (!CollectionUtils.isEmpty(itemList)) {
                                marginGapMap = itemList.stream().collect(Collectors.toMap(ConfigMarketMarginGap::getOddsType, a -> a, (k1, k2) -> k1));
                            }
                        }
                    }
                    //获取盘口投注项
                    List<StandardMarketOddsDataMessage> marketOddsList = standardMarketDataMessage.getMarketOddsList();
                    Integer minOddsValue = 0;
                    Integer maxOddsValue = 0;
                    //循环遍历盘口投注项
                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : marketOddsList) {
                        //设置pa赔率：数据源抽水赔率
                        standardMarketOddsDataMessage.setPaOddsValue(standardMarketOddsDataMessage.getOddsValue());
                        if (null == standardMarketOddsDataMessage.getPaOddsValue()) {
                            standardMarketOddsDataMessage.setPaOddsValue(0);
                        }
                        if (standardMarketOddsDataMessage.getPaOddsValue() > maxOddsValue) {
                            maxOddsValue = standardMarketOddsDataMessage.getPaOddsValue();
                        }
                        if (standardMarketOddsDataMessage.getPaOddsValue() < minOddsValue || minOddsValue == 0) {
                            minOddsValue = standardMarketOddsDataMessage.getPaOddsValue();
                        }
                        //852需求 数据源抽水赔率转概率赔率 + 概率差
                        String oddsType = standardMarketOddsDataMessage.getOddsType();
                        //球员玩法上游传的是中文，传递给下游是namecode,独赢配置存的是namecode
                        if (MarginCategoryConfig.PLAYER_CATEGORY_ODDS.contains(standardMarketDataMessage.getMarketCategoryId())) {
                            if (!MarginCategoryConfig.PLAYER_CATEGORY_ODDS_TYPE.contains(standardMarketOddsDataMessage.getOddsType())) {
                                StandardSportPlayer standardSportPlayer = standardSportPlayerService.getItem(standardMatchInfo.getSportId(), standardMarketOddsDataMessage.getAddition1());
                                if (null != standardSportPlayer) {
                                    oddsType = standardSportPlayer.getNameCode().toString();
                                }
                            }
                            log.info("::{}::数据商抽水赔率加概率球员类玩法投注类型转换,赛事ID:{},玩法ID:{},oddsType:{}",
                                    linkId, standardMatchInfo.getId(), marketCategoryId, oddsType);
                        }
                        if (!CollectionUtils.isEmpty(marginGapMap) && marginGapMap.get(oddsType) != null) {
                            Integer paOddsValue = standardMarketOddsDataMessage.getPaOddsValue();
                            if (paOddsValue != 0) {
                                ConfigMarketMarginGap configMarketMarginGaps = marginGapMap.get(oddsType);
                                //最终PA赔率 原始概率赔率加上水差
                                Double probability = BigDecimalUtils.divide(BigDecimalUtils.changeZero(configMarketMarginGaps.getProbability()), 100);
                                if (probability != 0) {
                                    //原始概率保留4位小数 : 1/原始赔率
                                    double probabilityOdds = BigDecimalUtils.divide(1, BigDecimalUtils.divide(paOddsValue, 100000), 4);
                                    Double finalPaOddsValue = BigDecimalUtils.divide(1, BigDecimalUtils.add(probabilityOdds, probability));
                                    log.info("::{}::数据商抽水赔率加概率差,赛事ID:{},玩法ID:{},计算前赔率:{},计算后赔率:{},margin配置信息:{}",
                                            linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketOddsDataMessage.getPaOddsValue(), finalPaOddsValue, JSON.toJSON(configMarketMarginGaps));
                                    standardMarketOddsDataMessage.setPaOddsValue(BigDecimalUtils.multiply(finalPaOddsValue, 100000).intValue());
                                    standardMarketOddsDataMessage.setProbability(BigDecimalUtils.multiply(probability, 100));
                                }
                            }
                        }
                        //欧赔转下马来
                        Double malayOdds = initializeComponent.getEuropeConvertMalayMap().get(standardMarketOddsDataMessage.getPaOddsValue());
                        standardMarketOddsDataMessage.setMalayOddsValue(malayOdds == null ? 0D : malayOdds);
                    }
                    //设置低赔
                    standardMarketDataMessage.setPaOddsValue(minOddsValue);
                }
            }
            //处理最大盘口数量设置，多余的盘口设置为DEACTIVATED
            processConfigMarketDisplayTrade(linkId, standardMarketDataMessages, standardMatchInfo);
            standardMarketMessageList.addAll(standardMarketDataMessages);
        }
    }

    /**
     * 1.数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
     * 2.开关封锁逻辑判断处理
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @param operatorStatus
     * @return
     */
    public StandardMarketMessage convertStandardMarketMessage(String linkId, StandardMarketDataMessage standardMarketDataMessage, Integer operatorStatus, boolean isMTS, boolean isOutRight, Map<Long, List<String>> changeCategoryOddsType) {
        StandardMarketMessage standardMarketMessage = new StandardMarketMessage();
        BeanUtils.copyProperties(standardMarketDataMessage, standardMarketMessage);
        if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarketDataMessage.getDataSourceCode())) {
            standardMarketMessage.setModifyTime(!Objects.isNull(standardMarketMessage.getModifyTime()) ? standardMarketMessage.getModifyTime() : TimeUtils.millsSecondsEast8ZoneGmt());
            Integer txPlaceNum = standardMarketDataMessage.getTxPlaceNum();
            if (!isMTS && txPlaceNum != null) {
                log.info("::{}::convertStandardMarketMessage,玩法ID:{},盘口ID:{},三方盘口ID:{},TX位置变更前:{},变更后:{}",
                        linkId, standardMarketDataMessage.getMarketCategoryId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), standardMarketDataMessage.getPlaceNum(), txPlaceNum);
                standardMarketMessage.setPlaceNum(txPlaceNum);
            }
        }else{
            standardMarketMessage.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            standardMarketMessage.setVerifyModifyTime(standardMarketDataMessage.getModifyTime());
        }
        if (!StringUtils.isEmpty(standardMarketDataMessage.getRemark()))
        {
            standardMarketMessage.setPaStatus(standardMarketDataMessage.getStatus());
        }
        else
        {
            standardMarketMessage.setPaStatus(0);
        }
        if (null != standardMarketDataMessage.getRelationMarketId()) {
            //统一盘口id 作为下游的盘口id
            if (standardMarketMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) {
                standardMarketMessage.setId(Long.valueOf(standardMarketDataMessage.getSendData()));
            } else {
                standardMarketMessage.setId(standardMarketDataMessage.getRelationMarketId());
            }
        }
        List<StandardMarketOddsDataMessage> marketOddsDataMessageList = standardMarketDataMessage.getMarketOddsList();
        if (!CollectionUtils.isEmpty(marketOddsDataMessageList)) {
            List<StandardMarketOddsMessage> marketOddsMessageList = new ArrayList<>();
            marketOddsDataMessageList.forEach(standardMarketOddsDataMessage -> {
                StandardMarketOddsMessage standardMarketOddsMessage = new StandardMarketOddsMessage();
                BeanUtils.copyProperties(standardMarketOddsDataMessage, standardMarketOddsMessage);
                standardMarketOddsMessage.setMarketId(standardMarketMessage.getId());
                if (standardMarketMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) {
                    if (null != standardMarketOddsDataMessage.getRelationMarketOddsId()) {
                        standardMarketOddsMessage.setId(Long.valueOf(standardMarketOddsDataMessage.getRemark()));
                    }
                } else {
                    if (null != standardMarketOddsDataMessage.getRelationMarketOddsId()) {
                        standardMarketOddsMessage.setId(standardMarketOddsDataMessage.getRelationMarketOddsId());
                    }
                }
                if(isOutRight){
                    standardMarketOddsMessage.setPaOddsValue(standardMarketOddsMessage.getOddsValue());
                    //数据源赔率变动玩法投注项，下发标识
                    if (!CollectionUtils.isEmpty(changeCategoryOddsType) && changeCategoryOddsType.get(standardMarketMessage.getMarketCategoryId()) != null) {
                        List<String> oddsTypeList = changeCategoryOddsType.get(standardMarketMessage.getMarketCategoryId());
                        if (oddsTypeList.contains(standardMarketOddsMessage.getOddsType())) {
                            standardMarketOddsMessage.setClearProbability(1);
                        }
                    }
                }else
                    //如果是MTS,固定玩法是a01 ，使用数据商抽水赔
                    if ((MarginCategoryConfig.A01_MARGIN_CATEGORY_NOT.contains(standardMarketDataMessage.getMarketCategoryId()) && standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)) || isMTS) {
                        //设置pa赔率
                        standardMarketOddsMessage.setPaOddsValue(standardMarketOddsMessage.getOddsValue());
                    } else {
                        //数据源赔率变动玩法投注项，下发标识
                        if (!CollectionUtils.isEmpty(changeCategoryOddsType) && changeCategoryOddsType.get(standardMarketMessage.getMarketCategoryId()) != null) {
                            List<String> oddsTypeList = changeCategoryOddsType.get(standardMarketMessage.getMarketCategoryId());
                            if (oddsTypeList.contains(standardMarketOddsMessage.getOddsType())) {
                                standardMarketOddsMessage.setClearProbability(1);
                            }
                        }
                    }
                standardMarketOddsMessage.setActive(!standardMarketOddsMessage.getActive().equals(Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE) ? Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED : standardMarketOddsMessage.getActive());
                standardMarketOddsMessage.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                marketOddsMessageList.add(standardMarketOddsMessage);
            });
            standardMarketMessage.setMarketOddsList(marketOddsMessageList);
        }
        if (null != standardMarketDataMessage.getPlaceNum()) {
            //设置盘口位置id
            standardMarketMessage.setPlaceNumId(standardMarketDataMessage.getStandardMatchInfoId() + "_" + standardMarketDataMessage.getMarketCategoryId() + "_" + standardMarketDataMessage.getChildMarketCategoryId() + "_" + standardMarketDataMessage.getPlaceNum());
        }
        return standardMarketMessage;
    }



    /**
     * 操盘需求-球头大一方的赔率必须大于球头小一方的
     * @param linkId
     * @param standardMatchInfo
     * @param marketDataMessage
     */
    public void checkMarketOddsByAddtion1(String linkId,StandardMatchInfo standardMatchInfo,List<StandardMarketMessage> marketDataMessage)
    {
        if (MarginCategoryConfig.VERIFY_SPORT.contains(standardMatchInfo.getSportId()))
        {
            Map<Long, List<StandardMarketMessage>> map = marketDataMessage.stream().filter(e ->
                    MarginCategoryConfig.CHANGE_FLAP1.contains(e.getMarketCategoryId()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(e.getStatus()))
                    .collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
            for (Map.Entry<Long, List<StandardMarketMessage>> entry : map.entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue()) || entry.getValue().size() <= 1)
                {
                    continue;
                }
                //按照球头降序排列
                entry.getValue().sort((o1, o2) -> Double.compare(Double.parseDouble(o2.getAddition1()), Double.parseDouble(o1.getAddition1())));
                //让分类，算主队
                boolean flag = true;
                if (MarginCategoryConfig.CHANGE_FLAP3.contains(entry.getKey()))
                {
                    for (int i = 0;i<entry.getValue().size()-1;i++)
                    {
                        if ((getPaOddsValueByOddsType(entry.getValue().get(i).getMarketOddsList(),"1") - getPaOddsValueByOddsType(entry.getValue().get(i+1).getMarketOddsList(),"1")) >= 0)
                        {
                            flag = false;
                            break;
                        }
                    }
                }
                //总分类，算Under
                else if (MarginCategoryConfig.CHANGE_FLAP2.contains(entry.getKey()))
                {
                    for (int i = 0;i<entry.getValue().size()-1;i++)
                    {
                        if ((getPaOddsValueByOddsType(entry.getValue().get(i).getMarketOddsList(),"Under") - getPaOddsValueByOddsType(entry.getValue().get(i+1).getMarketOddsList(),"Under")) >= 0)
                        {
                            flag = false;
                            break;
                        }
                    }
                }
                if (!flag)
                {
                    for (StandardMarketMessage standardMarketMessage:entry.getValue())
                    {
                        standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.THE_ODDS_ARE_NOT_SATISFIED.getCode(),null));
                        log.info("::{}::赔率合法性校验,标准赛事id:{},统一盘口id:{},标准玩法id:{},赔率不满足 Odd0.5（2.05）>Odd1.5（1.90）>Odd2.5(1.75) 规则，盘口封盘",
                                linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getMarketCategoryId());
                    }
                }
            }
        }
    }
    private Integer getPaOddsValueByOddsType(List<StandardMarketOddsMessage> standardMarketOddsMessages,String oddsType)
    {
        if (CollectionUtils.isEmpty(standardMarketOddsMessages))
        {
            return 0;
        }
        for (StandardMarketOddsMessage standardMarketOddsMessage:standardMarketOddsMessages)
        {
            if (standardMarketOddsMessage.getOddsType().equalsIgnoreCase(oddsType)
                    && Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(standardMarketOddsMessage.getActive()))
            {
                return standardMarketOddsMessage.getPaOddsValue();
            }
        }
        return 0;
    }
    /**
     * 校验赔率的合法性
     *
     * @param linkId
     * @param standardMatchId
     * @param marketDataMessage
     * @return
     */
    public boolean checkMarketOddsValid(String linkId, Long standardMatchId, StandardMarketMessage marketDataMessage,
                                        Map<String, ConfigMarketTradeItem> configMarketTradeItemMap, Long sportId,
                                        StandardMatchInfo standardMatchInfo) {
        if (null == marketDataMessage || CollectionUtils.isEmpty(marketDataMessage.getMarketOddsList())) {
            log.info("::{}::赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{}, marketDataMessage 或 marketOddsList 对象为空",
                    linkId, standardMatchId, marketDataMessage.getId(), marketDataMessage.getThirdMarketSourceId());
            return true;
        }
        if (352 == marketDataMessage.getMarketCategoryId()) {
            log.info("::{}::赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{}, 该玩法不做赔率合法校验",
                    linkId, standardMatchId, marketDataMessage.getId(), marketDataMessage.getThirdMarketSourceId());
            return true;
        }
        Long marketId = marketDataMessage.getId();
        Integer tempStatus = Math.max(Math.max(marketDataMessage.getStatus(), marketDataMessage.getPaStatus()),marketDataMessage.getThirdMarketSourceStatus());
        //只有开,封需要做下面的校验
        if (!Constant.SPORT_MARKET.STATUS.ACTIVE.equals(tempStatus) && !Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(tempStatus))
        {
            log.info("::{}::赔率合法性校验,标准赛事id:{},统一盘口id:{},三方盘口源id:{},盘口状态为关盘", linkId, standardMatchId, marketId, marketDataMessage.getThirdMarketSourceId());
            return true;
        }
        boolean isNegativeAll = true;
        List<Integer> paOddsValues = new ArrayList<>();
        //投注项集合
        List<StandardMarketOddsMessage> messageList = new ArrayList<>();
        for (StandardMarketOddsMessage message : marketDataMessage.getMarketOddsList()) {
            messageList.add(message);
            Integer paOddsValue = message.getPaOddsValue();
            //如果投注项active=0/3，或者赔率paOddsValue=0，则把赔率设置为1.001，该设置只用于后面公式的验证
            if (Constant.SPORT_MARKET.ODDS_STATUS.UNACTIVE.equals(message.getActive())
                    || Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED.equals(message.getActive())
                    || null == paOddsValue || 0 == paOddsValue ) {
                paOddsValues.add(100100);
                paOddsValue = 100100;
            } else {
                paOddsValues.add(paOddsValue);
            }
            if (paOddsValue >= 0) {
                isNegativeAll = false;
            }
        }
        //赔率值不能都为负数,全都为负数的话等于转成欧赔上下盘都大于2了。玩家稳赢
        if (isNegativeAll) {
            setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.SUSPENDED);
            marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.ODDS_CANNOT_BE_NEGATIVE.getCode(),null));
            //marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            log.info("::{}::赔率合法性校验,标准赛事id:{},统一盘口id:{},三方盘口源id:{},赔率值不能都为负数", linkId, standardMatchId, marketId, marketDataMessage.getThirdMarketSourceId());
            return false;
        }
        boolean result = true;
        try {
            result = checkMarketOddsValid(linkId, standardMatchId, marketId, paOddsValues, marketDataMessage,
                    configMarketTradeItemMap, messageList, sportId, standardMatchInfo);
        } catch (Exception e) {
            log.info("::{}::赔率合法性校验,标准赛事id:{},统一盘口id:{},三方盘口源id:{},error={}", linkId, standardMatchId, marketId, marketDataMessage.getThirdMarketSourceId(), e);
        }
        return result;
    }

    /**
     * 对赔率合法性做校验:
     * 1.如果只有一个投注项，则该盘口无效
     * 2.多项盘 1/(1/o1 + 1/o2 + ... + 1/on)大于0.99
     * 3.赔率值不符合最大最小赔率配置规定
     */
    private void setPaStatusWithSyncStatus(StandardMarketMessage marketDataMessage, Integer marketStatus) {
        marketDataMessage.setPaStatus(marketStatus);
        marketDataMessage.setStatus(marketStatus);
    }

    private boolean checkMarketOddsValid(String linkId, Long standardMatchId, Long standardMarketId, List<Integer> paOddsValues,
                                         StandardMarketMessage marketDataMessage, Map<String, ConfigMarketTradeItem> configMarketTradeItemMap,
                                         List<StandardMarketOddsMessage> messageList, Long sportId,StandardMatchInfo standardMatchInfo) {
        //如果只有一个投注项，则该盘口无效
        if (paOddsValues.size() == 1) {
            setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.SUSPENDED);
            marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.ONLY_ONE_BET.getCode(),null));
            //marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            log.info("::{}::赔率合法性校验，标准赛事id:{}, 统一盘口id:{},三方盘口源id:{},pa赔率集合:{},只有一个投注项,赔率合法性检查不通过",
                    linkId, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues);
            return false;
        }
        //多项盘 1/(1/o1 + 1/o2 + ... + 1/on) 不要大于0.99
        BigDecimal sumDecimal = new BigDecimal(0);
        for (Integer aLong : paOddsValues) {
            if (null == aLong || 0 == aLong) {
                continue;
            }
            BigDecimal bigDecimal = new BigDecimal(100000).divide(new BigDecimal(aLong), 2, BigDecimal.ROUND_HALF_UP);
            sumDecimal = sumDecimal.add(bigDecimal);
        }
        if (!sumDecimal.equals(BigDecimal.ZERO) && !MarginCategoryConfig.ignoreCheckCategoryMarketOddsOnValid.contains(marketDataMessage.getMarketCategoryId())) {
            double result = new BigDecimal(1).divide(sumDecimal, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (result > 0.99) {
                setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.SUSPENDED);
                marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.MULTIPLE_DISCS_DO_NOT_MEET_THE_RULES.getCode(),null));
                //marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},1/(1/o1 + ... + 1/on)大于0.99,赔率合法性检查不通过",
                        linkId, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues);
                return false;
            }
        }
        //获取最大最小赔率配置
        ConfigMarketTradeItem configMarketTradeItem = configMarketTradeItemMap.get(marketDataMessage.getMarketCategoryId() + "-" + marketDataMessage.getChildMarketCategoryId() + "-" + marketDataMessage.getPlaceNum());
        if (null == configMarketTradeItem) {
            //获取总玩法
            configMarketTradeItem = configMarketTradeItemMap.get(marketDataMessage.getMarketCategoryId() + "-" + marketDataMessage.getMarketCategoryId() + "1");
            if (null == configMarketTradeItem) {
                return true;
            }
        }
        Double max = configMarketTradeItem.getMaxOddsValue();
        Double min = configMarketTradeItem.getMinOddsValue();
        //最大值小于等于1的肯定是马来赔，需要把马来赔转成欧赔再校验
        if(null != max && max <= 1)
        {
            max = initializeComponent.getConvertMalayToEurope(max);
            min = min!=null?initializeComponent.getConvertMalayToEurope(min):null;
        }
        boolean twoOddsCategory = marketDataMessage.getMarketOddsList().size() == 2;
        //三项盘以上玩法，赔率超过最大小马来赔，只封单个投注项
        boolean contains = marketDataMessage.getMarketOddsList().size() >= 3;
        //激活的投注项个数
        int totalActive = 0;
        for (StandardMarketOddsMessage marketOddsMessage : marketDataMessage.getMarketOddsList()) {
            if (null == marketOddsMessage.getPaOddsValue()) {
                continue;
            }
            if ( Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(marketOddsMessage.getActive())){
                totalActive++;
            }
            //投注项为未激活的不用校验赔率是否在最大最小范围
            if ( Constant.SPORT_MARKET.ODDS_STATUS.UNACTIVE.equals(marketOddsMessage.getActive())
                    || Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED.equals(marketOddsMessage.getActive())) {
                //针对两项盘 有一项封盘的都做成了盘口级别封盘
                if (twoOddsCategory) {
                    marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.A_CLOSURE.getCode(), null));
                    marketDataMessage.setRemark(marketDataMessage.getRemark()+","+MarketTipsLanguageEnum.A_CLOSURE.getEn());
                    break;
                }
            }
        }
        for (StandardMarketOddsMessage marketOddsMessage : marketDataMessage.getMarketOddsList()) {
            if (null == marketOddsMessage.getPaOddsValue()) {
                continue;
            }
            if (StandardSportTypeEnum.FootBall.code.equals(sportId)) {
                //两项盘最小赔率判断 等于不封盘 小于封盘
                if (!contains) {
                    if ((null != max && marketOddsMessage.getPaOddsValue() >= max * 100000)) {
                        setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.ILLEGAL_BET_ODDS.getCode(),configMarketTradeItem.getMaxOddsValue().toString()));
                        return false;
                    }
                    if ((null != min && marketOddsMessage.getPaOddsValue() <= min * 100000)) {
                        if (marketOddsMessage.getPaOddsValue() != 1.01 * 100000) {
                            setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.SUSPENDED);
                            marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.ILLEGAL_BET_ODDS_MIN.getCode(),configMarketTradeItem.getMinOddsValue().toString()));
                            return false;
                        }
                    }
                    //三项盘以上玩法最小赔率小于等于1.01不封盘，其他小于等于封盘 ，超出最大赔率不封盘下发附加字段5
                } else {
                    if ((null != min && marketOddsMessage.getPaOddsValue() <= min * 100000)) {
                        if (marketOddsMessage.getPaOddsValue() != 1.01 * 100000) {
                            //投注项赔率不合法时，只封当前投注项
                            marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                            marketOddsMessage.setPaActiveReason("投注项赔率不合法，超过最小赔率[" + configMarketTradeItem.getMinOddsValue() + "]，投注项封盘");
                            log.info("::{}::足球赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},转换前最大最小赔率[{},{}],转换后最大最小赔率[{},{}] ,赔率值超过最小配置",
                                    linkId, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues, configMarketTradeItem.getMaxOddsValue(), configMarketTradeItem.getMinOddsValue(), max, min);
                        }
                    }
                    if (null != max && marketOddsMessage.getPaOddsValue() >  max * 100000) {
                        log.info("::{}::足球赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{} 原PA赔:{},超出最大赔率设置最大赔率:{}",
                                linkId, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), marketOddsMessage.getPaOddsValue(), max);
                    }
                    //下发配置赔率最大值
                    Integer maxOddsValue = processOddsValueDecimals(linkId,BigDecimal.valueOf(max).multiply(new BigDecimal(Double.toString(100000))).intValue());
                    marketDataMessage.setAddition5(String.valueOf(maxOddsValue));
                }
            } else if (MarginCategoryConfig.VERIFY_SPORT.contains(sportId)) {
                String sportName = StandardSportTypeEnum.getEnum(sportId).getMsg();
                if (!contains) {
                    // 只处理篮球，改为关盘
                    if (Objects.equals(sportId, StandardSportTypeEnum.Basketball.getCode())
                            && marketOddsMessage.getOriginalOddsValue() < 1.01 * 100000) {
                        setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        marketDataMessage.setPaStatusReason("两项盘赔率合法性校验,存在一个投注项原始赔率小于1.01,合法性关盘");
                        log.info("::{}::{}两项盘赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},存在一个投注项原始赔率小于1.01,合法性关盘",
                                linkId, sportName, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(),
                                paOddsValues);
                        // 发送操盘日志给风控
                        commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo,marketDataMessage);
                        return false;
                    }
                    if (Objects.equals(sportId, StandardSportTypeEnum.Basketball.getCode()) && marketDataMessage.getMarketCategoryId() == 37L) {
                        if ((null != max && marketOddsMessage.getPaOddsValue() >= max * 100000) || (null != min && marketOddsMessage.getPaOddsValue() < min * 100000)) {
                            setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.EXCEEDS_MAX_AND_MIN_ODDS.getCode(), configMarketTradeItem.getMaxOddsValue().toString(), configMarketTradeItem.getMinOddsValue().toString()));
                            log.info("::{}::{}两项盘赔率合法性校验1，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},转换前最大最小赔率[{},{}],转换后最大最小赔率[{},{}] ,赔率值超过最大最小配置", linkId, sportName, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues, configMarketTradeItem.getMaxOddsValue(), configMarketTradeItem.getMinOddsValue(), max, min);
                            // 发送操盘日志给风控
                            commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo, marketDataMessage);
                            return false;
                        }
                    } else {
                        if ((null != max && marketOddsMessage.getPaOddsValue() >= max * 100000) || (null != min && marketOddsMessage.getPaOddsValue() <= min * 100000)) {
                            setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.EXCEEDS_MAX_AND_MIN_ODDS.getCode(), configMarketTradeItem.getMaxOddsValue().toString(), configMarketTradeItem.getMinOddsValue().toString()));
                            log.info("::{}::{}两项盘赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},转换前最大最小赔率[{},{}],转换后最大最小赔率[{},{}] ,赔率值超过最大最小配置", linkId, sportName, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues, configMarketTradeItem.getMaxOddsValue(), configMarketTradeItem.getMinOddsValue(), max, min);
                            // 发送操盘日志给风控
                            commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo, marketDataMessage);
                            return false;
                        }
                    }
                } else {
                    if ((null != min && marketOddsMessage.getPaOddsValue() <= min * 100000)) {
                        //三项盘最小投注项赔率不合法时，只封当前投注项
                        marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                        marketOddsMessage.setPaActiveReason("投注项赔率不合法，超过最小赔率[" + configMarketTradeItem.getMinOddsValue() + "]，投注项封盘");
                        log.info("::{}::{}赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},转换前最大最小赔率[{},{}],转换后最大最小赔率[{},{}] ,赔率值超过最小配置",
                                linkId, sportName, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues, configMarketTradeItem.getMaxOddsValue(), configMarketTradeItem.getMinOddsValue(), max, min);
                    }
                    if (null != max && marketOddsMessage.getPaOddsValue() >= max * 100000) {
                        log.info("::{}::{}赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{} 原PA赔:{},超出最大赔率设置最大赔率:{}",
                                linkId, sportName, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), marketOddsMessage.getPaOddsValue(), max);
                    }
                    //下发配置赔率最大值
                    Integer maxOddsValue = processOddsValueDecimals(linkId, BigDecimal.valueOf(max).multiply(new BigDecimal(Double.toString(100000))).intValue());
                    marketDataMessage.setAddition5(String.valueOf(maxOddsValue));
                }
            } else {
                if (contains) {
                    if ((null != min && marketOddsMessage.getPaOddsValue() <= min * 100000)) {
                        //投注项赔率不合法时，只封当前投注项
                        marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED);
                        marketOddsMessage.setPaActiveReason("投注项赔率不合法，超过最小赔率[" + configMarketTradeItem.getMinOddsValue() + "]，投注项封盘");
                        log.info("::{}::赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},转换前最大最小赔率[{},{}],转换后最大最小赔率[{},{}] ,赔率值超过最小配置",
                                linkId, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues, configMarketTradeItem.getMaxOddsValue(), configMarketTradeItem.getMinOddsValue(), max, min);
                    }
                } else {
                    if ((null != max && marketOddsMessage.getPaOddsValue() >= max * 100000) || (null != min && marketOddsMessage.getPaOddsValue() <= min * 100000)) {
                        setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.EXCEEDS_MAX_AND_MIN_ODDS.getCode(), configMarketTradeItem.getMaxOddsValue().toString(), configMarketTradeItem.getMinOddsValue().toString()));
                        log.info("::{}::赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率集合:{},转换前最大最小赔率[{},{}],转换后最大最小赔率[{},{}] ,赔率值超过最大最小配置", linkId, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId(), paOddsValues, configMarketTradeItem.getMaxOddsValue(), configMarketTradeItem.getMinOddsValue(), max, min);
                        // 发送操盘日志给风控
                        commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo, marketDataMessage);
                        return false;
                    }
                }
            }
        }
        List<Integer> activeNums = marketDataMessage.getMarketOddsList().stream()
                .filter(t -> t.getActive().equals(Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE))
                .map(StandardMarketOddsMessage::getActive).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(activeNums) || activeNums.size() <= 1) {
            marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            setPaStatusWithSyncStatus(marketDataMessage, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            marketDataMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.ONLY_ONE_BET.getCode(), null));
            log.info("::{}::赔率合法性校验，标准赛事id:{}, 统一盘口id:{},三方盘口源id:{},只有一个投注项为开,盘口关盘处理,赔率合法性检查不通过", linkId, standardMatchId, standardMarketId, marketDataMessage.getThirdMarketSourceId());
        }
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code) && marketDataMessage.getMarketOddsList().size() > 3) {
            List<StandardMarketOddsMessage> marketOddsList = marketDataMessage.getMarketOddsList();
            marketOddsList.stream().forEach(m -> {
                Integer active = m.getActive();
                m.setActive(Constant.SPORT_MARKET.ODDS_STATUS.DEACTIVATED.equals(active) ? Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED : active);
            });
        }
        return true;
    }

    /**
     * 操盘方式判断是否下发（手动操盘不下发）
     */
    public ConfigTradeType isSendMarketOddsByTradeType(String linkId, Long matchId, Long marketCategoryId) {
        //先判断玩法级别
        ConfigTradeType itemCategory = configTradeTypeService.getItemCategory(matchId.toString(), marketCategoryId.toString());
        if (itemCategory != null) {
            if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.MANUAL.equals(itemCategory.getTradeType())
                    || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_MODEL.equals(itemCategory.getTradeType())
                    || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_NEW.equals(itemCategory.getTradeType())) {
                log.info("{}::标准赛事id={},玩法id={},缓存玩法级操盘配置为手动配置", linkId, matchId, marketCategoryId);
                return itemCategory;
            } else if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(itemCategory.getTradeType())) {
                //自动加强模式A+ 操盘
                return itemCategory;
            }
        }
        return itemCategory;
    }

    /**
     * 三项独赢盘计算
     * P：原始赔率的概率
     * Margin: 抽水的概率百分比
     * M：划分到每一个投注项上的margin平均值
     * PGap：每个投注项的设置概率差
     * OddGap: 每个投注项设置的水差
     * Anchor：瞄点标识
     * MOdds：概率差赔率
     * PaOdds：最终赔率
     * 计算方式：
     * (1/((1/抽水赔 截取八位小数) + 概率差 )) + 水差
     *
     * @param linkId
     * @param standardMatchInfoId
     * @param standardMarketDataMessage
     * @param marketCategoryId
     */
    private void processStandardMarketMarginEUROPE(String linkId, Long standardMatchInfoId, StandardMarketDataMessage standardMarketDataMessage, Long marketCategoryId) {
        //转换统一盘口ID
        Long relationMarketId = convertRelationMarketId(linkId, standardMarketDataMessage);
        if (standardMarketDataMessage.getPlaceNum() == null) {
            standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
            log.info("::{}::三项盘独赢坑位为null盘口关盘处理,标准赛事ID:{},玩法ID{},统一盘口ID:{},坑位:{}", linkId, standardMatchInfoId, marketCategoryId, relationMarketId, standardMarketDataMessage.getPlaceNum());
            return;
        }
        Map<String, ConfigMarketMarginGap> marginGapMap = new HashMap<>();
        Double initMargin = 110D;
        //查询独赢配置
        List<ConfigMarketMarginGap> itemList = configMarketMarginGapService.getItemList(standardMatchInfoId, marketCategoryId, standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum());
        if (CollectionUtils.isEmpty(itemList)) {
            itemList = configMarketMarginGapService.getItemList(standardMatchInfoId, marketCategoryId, marketCategoryId, standardMarketDataMessage.getPlaceNum());
        }
        if (!CollectionUtils.isEmpty(itemList)) {
            marginGapMap = itemList.stream().collect(Collectors.toMap(ConfigMarketMarginGap::getOddsType, a -> a, (k1, k2) -> k1));
        }
        try {
            for (StandardMarketOddsDataMessage marketOdds : standardMarketDataMessage.getMarketOddsList()) {
                String oddsType = marketOdds.getOddsType();
                ConfigMarketMarginGap configMarketMarginGap = new ConfigMarketMarginGap();
                //配置设置不存在默认值
                if (CollectionUtils.isEmpty(marginGapMap)) {
                    configMarketMarginGap.setMargin(initMargin);
                    //默认不联动
                    configMarketMarginGap.setLinkageMode(0);
                } else {
                    if (marginGapMap.get(oddsType) != null) {
                        configMarketMarginGap = marginGapMap.get(oddsType);
                    }
                }
                //概率差- PGap
                Double probability = BigDecimalUtils.divide(BigDecimalUtils.changeZero(configMarketMarginGap.getProbability()), 100);
                //A模式联动；不联动则概率差不平摊到其他选项 ：0(否),1(是)
                Integer linkageMode = BigDecimalUtils.changeZero(configMarketMarginGap.getLinkageMode());
                //水差- OddsGap
                Double diffValue = BigDecimalUtils.changeZero(configMarketMarginGap.getDiffValue());
                //描点 ：0(否),1(是)
                Integer anchor = configMarketMarginGap.getAnchor();
                //抽水赔率
                Integer oddsValue = marketOdds.getOddsValue();
                //原始赔率为0 ,水差就是最终赔率
                if (oddsValue == 0) {
                    marketOdds.setMarketDiffValue(BigDecimalUtils.multiply(diffValue, 100));
                    marketOdds.setProbability(BigDecimalUtils.multiply(probability, 100));
                    marketOdds.setProbabilityOdds(0);
                    marketOdds.setMargin(configMarketMarginGap.getMargin());
                    standardMarketDataMessage.setLinkageMode(linkageMode);
                    marketOdds.setPaOddsValue(processOddsValueDecimals(linkId, BigDecimalUtils.multiply(diffValue, 100000).intValue()));
                    marketOdds.setAnchor(anchor);
                    standardMarketDataMessage.setRemark("投注项原始存在为:0");
                    log.info("::{}::三项盘独赢计算:{},标准盘口:{},统一盘口id:{},原始赔率为:0，不再计算,封盘口和投注项:{}",
                            linkId, standardMatchInfoId, standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), marketOdds.getOddsType());
                    continue;
                }
                //Step1:原始赔率转为小数点，原始概率： P = 1/抽水赔率
                double changOriginalOdds = BigDecimalUtils.divide(oddsValue, 100000D, 2);
                double p = BigDecimalUtils.divide(1, changOriginalOdds, 8);
                //Step2:计算概率差赔率probabilityOdds, 公式: 1/(P+M+PGap)
                double probabilityOdds = BigDecimalUtils.add(p, probability);
                probabilityOdds = BigDecimalUtils.divide(1, probabilityOdds, 2);
                //最终PA赔率 所有投注项概率赔率加上水差：paOdds = odds + oddsGap
                Double paOddsValue = BigDecimalUtils.add(probabilityOdds, diffValue);
                marketOdds.setPaOddsValue(BigDecimalUtils.multiply(paOddsValue, 100000).intValue());
                marketOdds.setAnchor(anchor);
                //水差*100 返回给前端
                marketOdds.setMarketDiffValue(BigDecimalUtils.multiply(diffValue, 100));
                marketOdds.setProbability(BigDecimalUtils.multiply(probability, 100));
                marketOdds.setMargin(configMarketMarginGap.getMargin());
                standardMarketDataMessage.setLinkageMode(linkageMode);
                marketOdds.setProbabilityOdds(BigDecimalUtils.multiply(probabilityOdds, 100000).intValue());
                marketOdds.setMarginProbabilityOdds(BigDecimalUtils.multiply(changOriginalOdds, 100000).intValue());
                //最终赔率小数点处理
                marketOdds.setPaOddsValue(processOddsValueDecimals(linkId, BigDecimalUtils.multiply(paOddsValue, 100000).intValue()));
                log.info("::{}::三项盘独赢计算,标准赛事id:{},标准盘口ID:{},转换统一盘口ID:{},统一盘口id:{},三方盘口源id:{},投注项类型:{},瞄点(0否/1是):{},原始赔率:{},P原始概率:{},抽水原始概率:{},概率差:{},概率差赔率:{},水差:{},最终PA赔率:{},联动0(否)/1(是):{},配置信息:{}",
                        linkId, standardMatchInfoId, standardMarketDataMessage.getId(), relationMarketId, standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId(),
                        oddsType, anchor, changOriginalOdds, p, changOriginalOdds, probability, probabilityOdds, diffValue, paOddsValue, linkageMode, JSONObject.toJSON(configMarketMarginGap));
            }
        } catch (Exception e) {
            //出现异常盘口封盘
            standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
            log.info("::{}::三项独赢盘计算出现异常,盘口ID:{},玩法ID:{},三方盘口ID:{},e:{}", linkId, relationMarketId, marketCategoryId, standardMarketDataMessage.getThirdMarketSourceId(), e);
        }
    }

    /**
     * 第一次计算赔率 margin配置不存在，默认锚点为：0否  锚点：0(否),1(是)
     *
     * 三项盘总共两个描点:
     * 投注项为1X2默认情况x是描点，另外一个描点是1和2之间赔率较小者，如果1,2赔率相同，则是1
     * 投注项非1X2默认情况，都用赔率更低的两项
     * 两项盘 描点按最小赔率为描点 ，如果1,2赔率相同，则是1
     */
    public static void initAnchor(StandardMarketDataMessage standardMarketDataMessage) {
        Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
        List<StandardMarketOddsDataMessage> oddsList = standardMarketDataMessage.getMarketOddsList();
        //1X2投注项，两项盘 默认锚点
        if (oddsList.size() == 2 || MarginCategoryConfig.PRESET_ODDS_TYPE_ANCHOR_CATEGORY.contains(marketCategoryId)) {
            List<StandardMarketOddsDataMessage> oddsDataMessages = oddsList.stream().peek(m -> {
                m.setAnchor("X".equals(m.getOddsType()) ? 1 : 0);
            }).filter(o -> !"X".equals(o.getOddsType())).collect(Collectors.toList());
            threeAndTwoItemsCompare(oddsDataMessages , oddsList.size());
        } else if (oddsList.size() == 3) {
            //非1X2投注项默认锚点,都用赔率更低的两项
            twoItemsCompare(oddsList);
            StandardMarketOddsDataMessage standardMarketOddsDataMessage1 = oddsList.get(0);
            StandardMarketOddsDataMessage standardMarketOddsDataMessage2 = oddsList.get(1);
            StandardMarketOddsDataMessage standardMarketOddsDataMessage3 = oddsList.get(2);
            standardMarketOddsDataMessage1.setAnchor(1);
            standardMarketOddsDataMessage2.setAnchor(1);
            standardMarketOddsDataMessage3.setAnchor(0);
        }
    }

    /**
     * 赔率正序 排序
     * 投注项非1X2默认情况，都用赔率更低的两项
     *
     * @param standardMarketOddsDataMessage
     */
    public static void twoItemsCompare(List<StandardMarketOddsDataMessage> standardMarketOddsDataMessage) {
        //非1X2投注项默认锚点
        Collections.sort(standardMarketOddsDataMessage, new Comparator<StandardMarketOddsDataMessage>() {
            @Override
            public int compare(StandardMarketOddsDataMessage o1, StandardMarketOddsDataMessage o2) {
                if (null != o1 && null != o2) {
                    if (o1.getOriginalOddsValue() > o2.getOriginalOddsValue()) {
                        return 1;
                    } else if (o1.getOriginalOddsValue() == o2.getOriginalOddsValue()) {
                        return 0;
                    }
                }
                return -1;
            }
        });
    }

    /**
     * 三项盘总共两个描点:
     * 投注项为1X2默认情况x是描点，另外一个描点是1和2之间赔率较小者，如果1,2赔率相同，则是1
     * 两项盘 描点按最小赔率为描点 ，如果1,2赔率相同，则是1
     *
     * @param standardMarketOddsDataMessage
     */
    public static void threeAndTwoItemsCompare(List<StandardMarketOddsDataMessage> standardMarketOddsDataMessage, int size) {
        Collections.sort(standardMarketOddsDataMessage, new Comparator<StandardMarketOddsDataMessage>() {
            @Override
            public int compare(StandardMarketOddsDataMessage o1, StandardMarketOddsDataMessage o2) {
                Integer originalOddsValue1 = size == 3 ? o1.getOriginalOddsValue() : o1.getPaOddsValue();
                Integer originalOddsValue2 = size == 3 ? o2.getOriginalOddsValue() : o2.getPaOddsValue();
                //两个赔率相等 设置锚点投注项类型：1
                if (originalOddsValue1.equals(originalOddsValue2)) {
                    if ("1".equals(o1.getOddsType()) || "Yes".equals(o1.getOddsType())) {
                        o1.setAnchor(1);
                    }
                    if ("1".equals(o2.getOddsType()) || "Yes".equals(o2.getOddsType())) {
                        o2.setAnchor(1);
                    }
                    return 0;
                }
                //两个赔率最小的设置为锚点
                int min = Math.min(originalOddsValue1, originalOddsValue2);
                if (originalOddsValue1 == min) {
                    o1.setAnchor(1);
                }
                if (originalOddsValue2 == min) {
                    o2.setAnchor(1);
                }
                return 0;
            }
        });
    }

    /**
     * 两项盘 margin计算
     * **操盘球种计算方式 如:
     * 下盘： 原始赔率  水差     margin均分
     * 4.66
     * 1/( (1/4.66 + 0.02 ) + (1.1-1)/2 )
     * 上盘：
     * 1.27
     * 1/( (1/1.27 - 0.02 ) + (1.1-1)/2 )
     * <p>
     * 计算出小数点都是截取
     * **综合球种计算方式 如:
     * 下盘： 原始赔率  水差
     * 4.66
     * 1/( (1/4.66 + 0.02 ))
     * 上盘：
     * 1.27
     * 1/( (1/1.27 - 0.02 ))
     *
     * @param linkId
     * @param matchId
     * @param standardMarketDataMessage
     * @param marketCategoryId
     */
    private void processStandardMarketTwoEUROPE(String linkId, Long matchId, StandardMarketDataMessage standardMarketDataMessage, Long marketCategoryId, Long sportId) {
        //只计算有效盘口
        if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            return;
        }
        //TRUE:操盘球种（数据商原始赔率） ，FALSE:综合球种（数据商抽水赔率）
        Boolean isTrue = MarginCategoryConfig.TRADER_SUPPORT_SPORT.contains(sportId) ? Boolean.TRUE : Boolean.FALSE;
        Long relationMarketId = convertRelationMarketId(linkId, standardMarketDataMessage);
        StringBuffer sb = new StringBuffer("标准赛事ID:" + matchId + "统一盘口ID:" + relationMarketId + "玩法:" + marketCategoryId);
        //TX坑位查询水差配置处理
        Integer placeNum = standardMarketDataMessage.getPlaceNum();
        if (DataSourceCodeEnum.TX.code.equals(standardMarketDataMessage.getDataSourceCode())
                && standardMarketDataMessage.getTxPlaceNum() != null) {
            placeNum = standardMarketDataMessage.getTxPlaceNum();
            log.info("::{}::新margin计算,三方盘口ID:{},标准盘口ID:{},TX坑位变更前:{},后:{}",
                    linkId, standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getTxPlaceNum(), placeNum);
        }
        Long childMarketCategoryId = standardMarketDataMessage.getChildMarketCategoryId();
        //子玩法margin不存在，查询总玩法
        ConfigMarketCategoryMargin configMargin = configMarketCategoryMarginService.getItemTwo(linkId, matchId, marketCategoryId, childMarketCategoryId, placeNum == null ? 1 : placeNum);
        if (configMargin == null) {
            sb.append("，子玩法margin,查询总玩法margin");
            configMargin = configMarketCategoryMarginService.getItemTwo(linkId, matchId, marketCategoryId, marketCategoryId, 1);
        }
        Double marginOdds = 110D;
        if (configMargin != null && configMargin.getMargin() >= 1) {
            marginOdds = configMargin.getMargin();
            sb.append("，玩法margin：" + marginOdds);
        }
        double diffValue = 0D;
        //足球/综合球种 盘口水差
        if (sportId == 1L || MarginCategoryConfig.COMPLEX_SPORTIDS.contains(sportId)) {
            String oddsType = MarginCategoryConfig.COMPLEX_SPORT_CATEGORY_ODDS_TYPE_NO.contains(marketCategoryId) ? "No" : "2";
            ConfigMarketAutoDiffTrade marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId,matchId, relationMarketId, oddsType);
            if (marketAutoDiffTrade != null) {
                diffValue = marketAutoDiffTrade.getDiffValue();
            }
            sb.append("，足球/综合球种,盘口水差 " + diffValue + ",查询下盘投注项：" + oddsType);
        } else {
            //其他球总坑位水差
            //水差子玩法不存在
            ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = configPlaceNumAutoDiffTradeService.getItem(linkId, matchId, marketCategoryId, childMarketCategoryId, placeNum);
            if (configPlacenumAutoDiffTrade != null) {
                diffValue = configPlacenumAutoDiffTrade.getDiffValue();
            }
            sb.append("，水差值 " + diffValue + ",查询水差坑位：" + placeNum);
        }
        sb.append("，盘口信息：" + JSONObject.toJSONString(standardMarketDataMessage));
        for (StandardMarketOddsDataMessage marketOdds : standardMarketDataMessage.getMarketOddsList()) {
            Boolean place = Boolean.TRUE;
            if (StringUtils.equals(marketOdds.getOddsType(), "2") || StringUtils.equals(marketOdds.getOddsType(), "No")) {
                place = Boolean.FALSE;
            }
            //设置margin
            marketOdds.setMargin(marginOdds);
            marketOdds.setMarketDiffValue(diffValue);
            // 原始赔率为0 日志补全不计算
            if (null == marketOdds.getOriginalOddsValue() || marketOdds.getOriginalOddsValue() == 0) {
                marketOdds.setPaOddsValue(0);
                sb.append("，原始赔率为0不计算----------");
                continue;
            }
            //--------------------margin计算--------------------//
            //原始赔率转换为小数点后两位
            double changOriginalOdds = BigDecimalUtils.divide((isTrue ? marketOdds.getOriginalOddsValue() : marketOdds.getOddsValue()), 100000, 2);
            //原始概率 = 1 / 原始赔率
            double originalProbability = BigDecimalUtils.divide(1, changOriginalOdds, 4);
            //水差赔率概率 = 原始概率 + -  水差
            double diffOdds;
            if (place) {
                diffOdds = BigDecimalUtils.subtract(originalProbability, diffValue);
            } else {
                diffOdds = BigDecimalUtils.add(originalProbability, diffValue);
            }
            sb.append(" --- 投注项：" + marketOdds.getOddsType() + "，原始赔率：" + changOriginalOdds + "，原始概率：" + originalProbability + "，水差赔率概率：" + diffOdds);
            //水差赔率概率校验：小于等于：0   大于等于：1 盘口封盘
            if (diffOdds <= 0 || diffOdds >= 1) {
                sb.append("，水差赔率概率校验：小于等于：0 、 大于等于：1 盘口封盘 ----------");
                continue;
            }
            //操盘球种才有margin均分
            if (isTrue) {
                //margin均分
                double marginAverage = BigDecimalUtils.divide(BigDecimalUtils.subtract(marginOdds, 100), 200);
                //PA赔率概率
                double paOddsProbability = BigDecimalUtils.add(diffOdds, marginAverage);
                //最终PA赔率概率
                double paOdds = BigDecimalUtils.divide(1, paOddsProbability);
                marketOdds.setPaOddsValue(BigDecimalUtils.multiply(paOdds, 100000).intValue());
                sb.append("，margin均分：" + marginAverage + "，PA赔率概率：" + paOddsProbability + "，最终PA赔率：" + paOdds);
            } else {
                //最终PA赔率概率
                double paOdds = BigDecimalUtils.divide(1, diffOdds);
                marketOdds.setPaOddsValue(BigDecimalUtils.multiply(paOdds, 100000).intValue());
                sb.append("，最终PA赔率：" + paOdds);
            }

        }
    }

    /**
     * AO两项盘 margin计算
     * 计算出小数点都是截取
     * 下盘： 原始赔率  水差
     * 4.66
     * 1/( (1/4.66 + 0.02 ))
     * 上盘：
     * 1.27
     * 1/( (1/1.27 - 0.02 ))
     *
     * @param linkId
     * @param matchId
     * @param standardMarketDataMessage
     * @param marketCategoryId
     */
    private void processStandardMarketTwoEUROPEAO(String linkId, Long matchId, StandardMarketDataMessage standardMarketDataMessage, Long marketCategoryId, Long sportId) {
        //只计算有效盘口
        if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            return;
        }
        Long relationMarketId = standardMarketDataMessage.getRelationMarketId();
        Integer placeNum = standardMarketDataMessage.getPlaceNum();
        Long childMarketCategoryId = standardMarketDataMessage.getChildMarketCategoryId();
        //子玩法margin不存在，查询总玩法
        ConfigMarketCategoryMargin configMargin = configMarketCategoryMarginService.getItemTwo(linkId, matchId, marketCategoryId, childMarketCategoryId, placeNum == null ? 1 : placeNum);
        if (configMargin == null) {
            configMargin = configMarketCategoryMarginService.getItemTwo(linkId, matchId, marketCategoryId, marketCategoryId, 1);
        }
        Double marginOdds = 110D;
        if (configMargin != null && configMargin.getMargin() >= 1) {
            marginOdds = configMargin.getMargin();
        }
        StringBuffer sb = new StringBuffer("标准赛事ID:" + matchId + "统一盘口ID:" + relationMarketId + "玩法:" + marketCategoryId + ",margin:" + marginOdds);
        double diffValue = 0D;
        ConfigMarketAutoDiffTrade marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId,matchId, relationMarketId, "2");
        if (marketAutoDiffTrade != null) {
            diffValue = marketAutoDiffTrade.getDiffValue();
        }
        //玩法坑位水差
        ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = configPlaceNumAutoDiffTradeService.getItem(linkId, matchId, marketCategoryId, childMarketCategoryId, placeNum);
        if (configPlacenumAutoDiffTrade != null) {
            diffValue = configPlacenumAutoDiffTrade.getDiffValue();
        }
        sb.append("，盘口信息：" + JSONObject.toJSONString(standardMarketDataMessage));
        for (StandardMarketOddsDataMessage marketOdds : standardMarketDataMessage.getMarketOddsList()) {
            Boolean place = Boolean.TRUE;
            if (StringUtils.equals(marketOdds.getOddsType(), "2") || StringUtils.equals(marketOdds.getOddsType(), "No")) {
                place = Boolean.FALSE;
            }
            //设置margin
            marketOdds.setMargin(marginOdds);
            marketOdds.setMarketDiffValue(diffValue);
            // 原始赔率为0 不计算
            if (null == marketOdds.getOriginalOddsValue() || marketOdds.getOriginalOddsValue() == 0
                    || null == marketOdds.getOddsValue() || marketOdds.getOddsValue() == 0) {
                marketOdds.setPaOddsValue(0);
                standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                sb.append("，原始赔率为0不计算----------");
                continue;
            }
            //--------------------margin计算--------------------//
            //抽水赔率转换为小数点后两位
            double oddsValue = BigDecimalUtils.divide((marketOdds.getOddsValue()), 100000, 2);
            //原始概率 = 1 / 原始赔率
            double originalProbability = BigDecimalUtils.divide(1, oddsValue, 4);
            //水差赔率概率 = 原始概率 + -  水差
            double diffOdds;
            if (place) {
                diffOdds = BigDecimalUtils.subtract(originalProbability, diffValue);
            } else {
                diffOdds = BigDecimalUtils.add(originalProbability, diffValue);
            }
            sb.append(" --- 投注项：" + marketOdds.getOddsType() + "，抽水赔率：" + oddsValue + "，抽水概率：" + originalProbability + "，水差赔率概率：" + diffOdds);
            //水差赔率概率校验：小于等于：0   大于等于：1 盘口封盘
            if (diffOdds <= 0 || diffOdds >= 1) {
                marketOdds.setPaOddsValue(0);
                standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                sb.append("，水差赔率概率校验：小于等于：0 、 大于等于：1 盘口封盘 ----------");
                continue;
            }
            //最终PA赔率概率
            double paOdds = BigDecimalUtils.divide(1, diffOdds);
            marketOdds.setPaOddsValue(BigDecimalUtils.multiply(BigDecimalUtils.scaleCrop(paOdds, 2), 100000).intValue());
            sb.append("，最终PA赔率：" + paOdds);
        }
        log.info("::{}::,processStandardMarketTwoEUROPEAO：{}",linkId,sb.toString());
    }

    /**
     * 两项盘盘口值计算排序规则（足球）
     * <p>
     * 1. 数据源的各个赔率减去malay spread/2；
     * 2. 再加上水差
     * 3. 以此得到的赔率差值，最小赔，盘口绝对值由小到大排序；
     * 4. 按照排序进到各自坑位重新计算上盘赔率，此后不再因为上盘赔率并更而再次变更坑位。
     * 只有数据源赔率发生变更或者自动水差发生变更进而导致受让/小球选项赔率发生变更，才会触发新的排序计算。
     *
     * @param standardMarketDataMessages
     */
    private void processStandardMarketMALAY(String linkId,StandardMatchInfo standardMatchInfo,Long standardCategoryId, List<StandardMarketDataMessage> standardMarketDataMessages) {
        if (CollectionUtils.isEmpty(standardMarketDataMessages)) {
            return;
        }
        Integer marketType = isOddsLive(standardMatchInfo.getId());
        //查询玩法开售表
        MarketCategorySell marketCategorySell = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), marketType, standardCategoryId);
        //收集存在问题的盘口数据
        List<StandardMarketDataMessage> standardMarketDataMessagesError = new ArrayList<>();
        ConfigMarketCategoryMargin configMarketCategoryMarginOne = null;
        ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade = null;
        ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = null;
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessages) {
            StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
            swCalculate.start("两项盘MY计算盘口级别详情耗时");
            Integer placeNum = standardMarketDataMessage.getPlaceNum();
            if (MarginCategoryConfig.TX_SORT_SPORT.contains(standardMatchInfo.getSportId())
                    && DataSourceCodeEnum.TX.code.equals(standardMarketDataMessage.getDataSourceCode())
                    && standardMarketDataMessage.getTxPlaceNum() != null) {
                placeNum = standardMarketDataMessage.getTxPlaceNum();
                log.info("::{}::processStandardMarketMALAY,三方盘口ID:{},标准盘口ID:{},TX坑位变更前:{},后:{}",
                        linkId, standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getTxPlaceNum(), placeNum);
            }
            //单独打印异常盘口
            if (standardMarketDataMessage.getMarketOddsList().size() < 2) {
                log.info("::{}::赛事ID:{},三方盘口ID:{},标准盘口ID:{},投注项小于2。"
                        , linkId, standardMatchInfo.getId(), standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getRelationMarketId());
                standardMarketDataMessagesError.add(standardMarketDataMessage);
                swCalculate.stop();
                continue;
            }
            //剔除非开封的盘口，不需要计算和排序
            if(!standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE) && !standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.SUSPENDED)){
                swCalculate.stop();
                continue;
            }
            //-------第一步：通过margin和水差计算后得出赔率差，最小赔--------
            configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId,standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId(), placeNum ==null?1: placeNum);
            Double spread = 0.1D;
            //子玩法不存在配置查询标准玩法margin
            if (configMarketCategoryMarginOne == null) {
                configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getMarketCategoryId(), 1);
            }
            if (null != configMarketCategoryMarginOne) {
                spread = configMarketCategoryMarginOne.getMargin();
            }
            //margin 和 水差计算
            //只有篮球/网球/乒乓球 才有坑位水差跟玩法水差
            if (MarginCategoryConfig.SPORT_HEAD.contains(standardMatchInfo.getSportId()))
            {
                configCategoryAutoDiffTrade = configCategoryAutoDiffTradeService.getItem(linkId, standardMatchInfo.getId(), standardCategoryId, standardMarketDataMessage.getChildMarketCategoryId());
                configPlacenumAutoDiffTrade = configPlaceNumAutoDiffTradeService.getItem(linkId, standardMatchInfo.getId(), standardCategoryId, standardMarketDataMessage.getChildMarketCategoryId(), placeNum);
            }
            boolean isOk = calculationMarketAuto(linkId, standardMarketDataMessage, spread,configCategoryAutoDiffTrade,configPlacenumAutoDiffTrade,standardMatchInfo);
            //如果计算失败
            if (!isOk) {
                log.error("::{}::标准赛事id:{},盘口id:{},统一盘口id:{},三方盘口源id:{},盘口赔率不正常，计算失败。",
                        linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId());
                standardMarketDataMessagesError.add(standardMarketDataMessage);
                swCalculate.stop();
                continue;
            }
            //将马来赔转欧赔后，计算赔率差绝对值
            Integer myOddsValue2Europe = BigDecimal.valueOf(initializeComponent.getConvertMalayToEurope(standardMarketDataMessage.getMarketOddsList().get(0).getMalayOddsValue())).multiply(new BigDecimal(Double.toString(100000))).intValue();
            Integer myOddsValue1Europe = BigDecimal.valueOf(initializeComponent.getConvertMalayToEurope(standardMarketDataMessage.getMarketOddsList().get(1).getMalayOddsValue())).multiply(new BigDecimal(Double.toString(100000))).intValue();
            //设置赔率差值
            standardMarketDataMessage.setOddsMetric(Math.abs(myOddsValue2Europe - myOddsValue1Europe));
            //设置最小赔
            standardMarketDataMessage.setPaOddsValue(myOddsValue2Europe - myOddsValue1Europe > 0 ? myOddsValue1Europe : myOddsValue2Europe);
            //最终的马来赔转欧赔
            for (StandardMarketOddsDataMessage marketOdds : standardMarketDataMessage.getMarketOddsList()) {
                //设置最终的paOddsValue
                //将马来赔转欧赔后，计算赔率差绝对值
                Integer paOddsValue = BigDecimal.valueOf(initializeComponent.getConvertMalayToEurope(marketOdds.getMalayOddsValue())).multiply(new BigDecimal(Double.toString(100000))).intValue();
                marketOdds.setPaOddsValue(paOddsValue);
            }
            swCalculate.stop();
            swCalculate.start("特殊抽水计算耗时");
            //特殊抽水计算
            autoDiffCountMarketMalay.standardMarketPumping(linkId, standardCategoryId, standardMatchInfo.getId(), marketCategorySell, standardMarketDataMessage);
            swCalculate.stop();
            Long relationMarketId = convertRelationMarketId(linkId, standardMarketDataMessage);
        }
        //----------第四步：给非开封及赔率不合法的盘口排序------------
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessagesError) {
            log.info("::{}::标准赛事id:{},盘口id:{},主盘统一盘口id:{},三方盘口源id:{},赔率不合法，关盘处理",
                    linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId());
            standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
        }
    }

    /**
     * 需求：1112
     * 1.判断spread是否为单数，拆分spread,如7分水 拆分为 4 ，3
     * 2.原始赔率转马来赔
     * 3.判断马来赔是否相等：固定下盘抽水更多
     * 4.判断欧赔：赔率最小一方抽水更多
     *
     * @param linkId
     * @param spread
     * @param marketValue
     * @param relationMarketId
     * @param standardMarketDataMessage
     * @return
     */
    public Map<Long, Double> spreadSingular(String linkId, Double spread, Double marketValue, Long relationMarketId, StandardMatchInfo standardMatchInfo, StandardMarketDataMessage standardMarketDataMessage) {
        //Map<统一投注项ID, Spread>
        Map<Long, Double> map = new HashMap<>();
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
            return map;
        }
        int scaleNumD = BigDecimalUtils.scaleNum(spread);
        //step1:判断spread是否为单数，拆分spread
        BigDecimal d = BigDecimal.valueOf(spread);
        BigInteger decimal = d.remainder(BigDecimal.ONE).movePointRight(d.scale()).abs().toBigInteger();
        if (decimal.intValue() % 2 != 0 && scaleNumD == 2 && spread != 0.01D) {
            spread = spread / 2;
            int scaleNum = BigDecimalUtils.scaleNum(spread);
            //抽水多的
            Double maxSpread = BigDecimalUtils.scale(spread, scaleNum - 1);
            //抽少水的
            Double minSpread = BigDecimalUtils.scaleCrop(spread, scaleNum - 1);
            //step2:原始赔率转马来赔
            StandardMarketOddsDataMessage standardMarketOddsDataMessage0 = standardMarketDataMessage.getMarketOddsList().get(0);
            StandardMarketOddsDataMessage standardMarketOddsDataMessage1 = standardMarketDataMessage.getMarketOddsList().get(1);
            Integer originalOddsValue0 = standardMarketOddsDataMessage0.getOriginalOddsValue();
            Integer originalOddsValue1 = standardMarketOddsDataMessage1.getOriginalOddsValue();
            Double europeToMalay1 = initializeComponent.getEuropeConvertMalayMap().get(subDoubleTwo(BigDecimal.valueOf(originalOddsValue0).divide(new BigDecimal(Double.toString(100000))).doubleValue()));
            Double europeToMalay2 = initializeComponent.getEuropeConvertMalayMap().get(subDoubleTwo(BigDecimal.valueOf(originalOddsValue1).divide(new BigDecimal(Double.toString(100000))).doubleValue()));
            log.info("::{}::spreadSingular,odds0欧赔:{},odds0马来赔:{},odds1欧赔:{},odds1马来赔:{},盘口ID:{},marketOddsList:{}",
                    linkId, originalOddsValue0, europeToMalay1, originalOddsValue1, europeToMalay2, relationMarketId, JSONObject.toJSONString(standardMarketDataMessage.getMarketOddsList()));
            if (null == europeToMalay1 || null == europeToMalay2) {
                return map;
            }
            //step3：判断马来赔是否相等,固定下盘抽更多水
            boolean isEqual = BigDecimalUtils.equalTo(europeToMalay1, europeToMalay2);
            //按照原始赔率排序 小-大
            List<StandardMarketOddsDataMessage> marketOddsSortedList = standardMarketDataMessage.getMarketOddsList().stream().sorted(Comparator.comparing(StandardMarketOddsDataMessage::getOriginalOddsValue)).collect(Collectors.toList());
            log.info("::{}::spreadSingular,按照原始赔率排序,盘口ID:{},排序结果:{}", linkId, relationMarketId, JSONObject.toJSONString(marketOddsSortedList));
            //找出下盘
            for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
                if (standardSportMarketOdds.getOddsType().equals("Under") || standardSportMarketOdds.getOddsType().equals("Even")
                        || standardSportMarketOdds.getOddsType().equals("No")
                        || (MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardSportMarketOdds.getOddsType().equals("X"))
                        || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))
                        || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))) {
                    //固定下盘抽更多水
                    if (isEqual) {
                        map.put(standardSportMarketOdds.getRelationMarketOddsId(), maxSpread);
                        log.info("::{}::spreadSingular,马来赔相等,固定下盘抽更多水,盘口ID:{},map:{}", linkId, relationMarketId, JSONObject.toJSONString(map));
                    } else {
                        //step4:判断欧赔：赔率最小一方抽水更多
                        StandardMarketOddsDataMessage minMarketOddsDataMessage = marketOddsSortedList.get(0);
                        StandardMarketOddsDataMessage maxMarketOddsDataMessage = marketOddsSortedList.get(1);
                        StringBuffer sb = new StringBuffer();
                        //下盘抽水多
                        if (minMarketOddsDataMessage.getRelationMarketOddsId().equals(standardSportMarketOdds.getRelationMarketOddsId())) {
                            map.put(standardSportMarketOdds.getRelationMarketOddsId(), maxSpread);
                            log.info("::{}::spreadSingular,下盘抽水多,上盘抽水少,盘口ID:{},maxSpread:{}", linkId, relationMarketId, maxSpread);
                        } else if (maxMarketOddsDataMessage.getRelationMarketOddsId().equals(standardSportMarketOdds.getRelationMarketOddsId())) {
                            //下盘抽水少，上盘抽水多
                            map.put(standardSportMarketOdds.getRelationMarketOddsId(), minSpread);
                            log.info("::{}::spreadSingular,下盘抽水少,上盘抽水多,盘口ID:{},minSpread:{}", linkId, relationMarketId, minSpread);
                        } else {
                            log.info("::{}::spreadSingular,欧赔赔率最小一方抽水不匹配,盘口ID:{}", linkId, relationMarketId);
                        }
                    }
                }
            }
        }
        return map;
    }


    /**
     * margin 和 水差计算
     *
     * @param standardMarketDataMessage
     * @param spread
     */
    private boolean calculationMarketAuto(String linkId, StandardMarketDataMessage standardMarketDataMessage, Double spread,ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade,ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade,StandardMatchInfo standardMatchInfo) {
        //水差只取下盘或者受让盘的,盘口水差，坑位水差，玩法水差
        ConfigMarketAutoDiffTrade marketAutoDiffTrade = null;
        //赔率只取下盘或者受让盘的赔率，通过马来赔取反得出上盘或者让球盘马来赔率
        Double underOriginalMalayOdds = 0D;
        //盘口值
        //收集足球、篮球附加字段玩法
        List<Long> add1List = new ArrayList<>();
        add1List.addAll(MarginCategoryConfig.FootBall_MY_CATEGORY);
        add1List.addAll(MarginCategoryConfig.BASKETBALL_MY_CATEGORY);
        Double marketValue = 0D;
        if (StringUtils.isNotBlank(standardMarketDataMessage.getAddition1()) &&
                add1List.contains(standardMarketDataMessage.getMarketCategoryId())) {
            marketValue = Double.parseDouble(standardMarketDataMessage.getAddition1());
        }
        //转换统一盘口ID
        Long relationMarketId = convertRelationMarketId(linkId, standardMarketDataMessage);
        StringBuffer stringBuffer = new StringBuffer();
        //计算后的下盘值
        Double underMalayOdds = 0D;
        Map<Long, Double> spreadSingularMap = spreadSingular(linkId, spread, marketValue, relationMarketId, standardMatchInfo, standardMarketDataMessage);
        stringBuffer.append("::" + linkId + "::两项盘,盘口主键id:" + standardMarketDataMessage.getId() + ",统一盘口id:" + standardMarketDataMessage.getRelationMarketId() + "水差统一盘口id:" + relationMarketId + ",三方盘口源id:" + standardMarketDataMessage.getThirdMarketSourceId() + ",盘口位置:" + standardMarketDataMessage.getPlaceNum() + ",参与计算spread:" + spread / 2);
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            //设置margin
            standardSportMarketOdds.setMargin(spread / 2);
            if (standardSportMarketOdds.getOddsType().equals("Under") || standardSportMarketOdds.getOddsType().equals("Even")
                    || standardSportMarketOdds.getOddsType().equals("No")
                    || (MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardSportMarketOdds.getOddsType().equals("X"))
                    || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))
                    || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2")  && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))) {
                stringBuffer.append(",下盘投注项主键id:" + standardSportMarketOdds.getId() + ",统一投注项id:" + standardSportMarketOdds.getRelationMarketOddsId() + ",投注项类型:" + standardSportMarketOdds.getOddsType());
                stringBuffer.append(",投注项原始赔率赔率:" + standardSportMarketOdds.getOriginalOddsValue());
                stringBuffer.append(",投注项抽水赔率:" + standardSportMarketOdds.getOddsValue());
                //标记下盘投注项标记，特殊抽水计算需要
                standardSportMarketOdds.setOddsTypeTag(Boolean.TRUE);
                //转换赔率为马来赔
                if (null == standardSportMarketOdds.getOriginalOddsValue()) {
                    log.error("::{}::盘口赔率不正常1，计算失败。三方投注项id:{}", linkId, standardSportMarketOdds.getThirdOddsFieldSourceId());
                    standardMarketDataMessage.setRemark("盘口赔率不正常，原始赔率为空，计算失败,盘口关盘");
                    log.info(stringBuffer.toString());
                    return false;
                }
                Double originalOddsValue = subDoubleTwo(BigDecimal.valueOf(standardSportMarketOdds.getOriginalOddsValue()).divide(new BigDecimal(Double.toString(100000))).doubleValue());
                underOriginalMalayOdds = initializeComponent.getEuropeConvertMalayMap().get(originalOddsValue);
                if (underOriginalMalayOdds == null) {
                    log.error("::{}::盘口赔率不正常2，计算失败。三方投注项id:{}", linkId, standardSportMarketOdds.getThirdOddsFieldSourceId());
                    standardMarketDataMessage.setRemark("盘口赔率不正常，根据原始赔率获取马来赔率为空，计算失败,盘口关盘");
                    //这里需要关数据源的盘口，因为上游的数据问题可能会导致下游处理报错
                    standardMarketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info(stringBuffer.toString());
                    return false;
                }
                //设置马来赔
                standardSportMarketOdds.setMalayOddsValue(underOriginalMalayOdds);
                stringBuffer.append(",计算前下盘马来赔率:" + underOriginalMalayOdds);
                //margin计算--------
                Double diffValue = spreadSingularMap.get(standardSportMarketOdds.getRelationMarketOddsId()) == null ? spread / 2 : spreadSingularMap.get(standardSportMarketOdds.getRelationMarketOddsId());
                stringBuffer.append(",参与spread计算diffValue:" + diffValue);
                boolean isOk = autoDiffCountMarketMalay.arithmeticMALAY(linkId,standardMarketDataMessage, diffValue ,spread, standardSportMarketOdds, true);
                if (!isOk) {
                    log.error("::{}::盘口赔率不正常3，计算失败。三方投注项id:{}", linkId, standardSportMarketOdds.getThirdOddsFieldSourceId());
                    standardMarketDataMessage.setRemark("盘口赔率不正常，margin计算失败,盘口关盘");
                    log.info(stringBuffer.toString());
                    return false;
                }
                stringBuffer.append(",抽水计算后的下盘马来赔率:" + standardSportMarketOdds.getMalayOddsValue());
                //获取水差----------
                //只有足球有盘口水差
                if (standardMatchInfo.getSportId() == 1)
                {
                    marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId,standardMatchInfo.getId(),relationMarketId, standardSportMarketOdds.getOddsType());
                }
                Double diffTrade = 0.0;
                Double marketDiffTrade = 0.0;
                Double placenumDiffTrade = 0.0;
                Double categoryDiffTrade = 0.0;
                //盘口水差
                if (marketAutoDiffTrade != null)
                {
                    marketDiffTrade = marketAutoDiffTrade.getDiffValue();
                    stringBuffer.append(",操盘后台设置的盘口水差值："+marketDiffTrade);
                }
                //玩法水差，当有玩法水差时，坑位水差跟着玩法水差的投注项走（理论上，有坑位水差一定有玩法水差，默认水差值0）
                String categoryOddsType = null;
                if (configCategoryAutoDiffTrade != null)
                {
                    categoryOddsType = configCategoryAutoDiffTrade.getOddsType();
                    if (categoryOddsType.equalsIgnoreCase(standardSportMarketOdds.getOddsType()))
                    {
                        categoryDiffTrade = configCategoryAutoDiffTrade.getDiffValue();
                    }
                    else
                    {
                        categoryDiffTrade = -configCategoryAutoDiffTrade.getDiffValue();
                    }
                    stringBuffer.append(",操盘后台设置的玩法水差值："+categoryDiffTrade);
                }
                //坑位水差
                if (null != configPlacenumAutoDiffTrade)
                {
                    if (null != categoryOddsType)
                    {
                        configPlacenumAutoDiffTrade.setOddsType(categoryOddsType);
                    }
                    if (configPlacenumAutoDiffTrade.getOddsType().equalsIgnoreCase(standardSportMarketOdds.getOddsType()))
                    {
                        placenumDiffTrade = configPlacenumAutoDiffTrade.getDiffValue();
                    }
                    else
                    {
                        placenumDiffTrade = -configPlacenumAutoDiffTrade.getDiffValue();
                    }
                    stringBuffer.append(",操盘后台设置的坑位水差值："+placenumDiffTrade);
                }
                diffTrade = marketDiffTrade + placenumDiffTrade + categoryDiffTrade;
                //水差计算------
                if (!diffTrade.equals(0.0)) {
                    boolean isOkTwo = autoDiffCountMarketMalay.arithmeticMALAY(linkId,standardMarketDataMessage, diffTrade,spread, standardSportMarketOdds, false);
                    if (!isOkTwo) {
                        log.error("::{}::盘口赔率不正常4，计算失败。三方投注项id:{}", linkId, standardSportMarketOdds.getThirdOddsFieldSourceId());
                        standardMarketDataMessage.setRemark("盘口赔率不正常，水差计算失败,盘口关盘");
                        log.info(stringBuffer.toString());
                        return false;
                    }
                    //设置水差
                    standardSportMarketOdds.setMarketDiffValue(diffTrade);
                    stringBuffer.append(",下盘参与计算的水差值:" + diffTrade);
                    stringBuffer.append(",水差计算后下盘马来赔率:" + standardSportMarketOdds.getMalayOddsValue());
                }else{
                    standardSportMarketOdds.setMarketDiffValue(diffTrade);
                }
                underMalayOdds = standardSportMarketOdds.getMalayOddsValue();
            }
            else
            {
                if (null != configCategoryAutoDiffTrade
                        && configCategoryAutoDiffTrade.getOddsType().equalsIgnoreCase(standardSportMarketOdds.getOddsType()))
                {
                    Double tempDiffValue = configCategoryAutoDiffTrade.getDiffValue();
                    if (null != configPlacenumAutoDiffTrade)
                    {
                        tempDiffValue += configPlacenumAutoDiffTrade.getDiffValue();
                    }
                    standardSportMarketOdds.setMarketDiffValue(tempDiffValue);
                }
            }
        }
        //下盘计算上盘
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            if (standardSportMarketOdds.getOddsType().equals("Under") || standardSportMarketOdds.getOddsType().equals("Even")
                    || standardSportMarketOdds.getOddsType().equals("No")
                    || (MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardSportMarketOdds.getOddsType().equals("X"))
                    || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))
                    || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2")  && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))) {
                //此次不用计算
            } else {
                //通过取反得出上盘或者让球盘马来赔率
                Double overOriginalMalayOdds = underOriginalMalayOdds*(-1);
                if(underOriginalMalayOdds == 1){
                    overOriginalMalayOdds = 1D;
                }
                //需要转回成欧赔赋值给原始欧赔
                Double overOriginalOddsValue = initializeComponent.getConvertMalayToEurope(overOriginalMalayOdds);
                standardSportMarketOdds.setOriginalOddsValue(BigDecimal.valueOf(overOriginalOddsValue).multiply(new BigDecimal(Double.toString(100000))).intValue());
                stringBuffer.append(",上盘投注项主键id:" + standardSportMarketOdds.getId() + ",统一投注项id:" + standardSportMarketOdds.getRelationMarketOddsId() + ",投注项类型:" + standardSportMarketOdds.getOddsType());
                stringBuffer.append(",投注项原始赔率赔率:" + standardSportMarketOdds.getOriginalOddsValue());
                stringBuffer.append(",投注项抽水赔率:" + standardSportMarketOdds.getOddsValue());
                //判断 新的下盘值加上spread是否>=1
                Double oddsSpeadValue = subDoubleTwo(new BigDecimal(Double.toString(underMalayOdds)).add(new BigDecimal(Double.toString(spread))).doubleValue());
                stringBuffer.append(",开始计算上盘,计算后下盘马来赔率:"+underMalayOdds+",加上spread:"+spread+",后等于：" + oddsSpeadValue);
                if(oddsSpeadValue >=1){
                    //上盘= 2-（新的下盘值+spread）
                    Double oddsValue = subDoubleTwo(new BigDecimal(2).subtract(new BigDecimal(Double.toString(oddsSpeadValue))).doubleValue());
                    standardSportMarketOdds.setMalayOddsValue(oddsValue);
                }else {
                   //上盘= -（新的下盘值+spread）
                    standardSportMarketOdds.setMalayOddsValue(oddsSpeadValue*(-1));
                }
                stringBuffer.append(",计算后投注项马来赔率:" + standardSportMarketOdds.getMalayOddsValue());
            }
        }
        log.info(stringBuffer.toString());
        return true;
    }

    /**
     * 综合球种计算MY赔/EU赔率
     *
     * @param standardMarketDataMessages
     */
    private void processStandardMarketMALAYOther(String linkId, List<StandardMarketDataMessage> standardMarketDataMessages) {
        if (CollectionUtils.isEmpty(standardMarketDataMessages)) {
            return;
        }
        //收集存在问题的盘口数据
        List<StandardMarketDataMessage> standardMarketDataMessagesError = new ArrayList<>();
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessages) {
            ConfigMarketCategoryMargin configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum() == null ? 1 : standardMarketDataMessage.getPlaceNum());
            Double spread = 0.1D;
            //子玩法不存在配置查询标准玩法margin
            if (configMarketCategoryMarginOne == null) {
                configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getMarketCategoryId(), 1);
            }
            if (null != configMarketCategoryMarginOne) {
                spread = configMarketCategoryMarginOne.getMargin();
            }
            //计算有效盘口
            if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
                standardMarketDataMessagesError.add(standardMarketDataMessage);
                continue;
            }
            //单独打印异常盘口
            if (standardMarketDataMessage.getMarketOddsList().size() < 2) {
                log.info("::{}::综合球种,三方盘口ID:{},标准盘口ID:{},投注项小于2。"
                        , linkId, standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getRelationMarketId());
                standardMarketDataMessagesError.add(standardMarketDataMessage);
                continue;
            }
            //计算水差
            boolean isOkMarket = calculationMarketAutoOther(linkId, standardMarketDataMessage, spread);
            //如果计算失败
            if (!isOkMarket) {
                log.error("::{}::综合球种,标准比赛id:{},盘口id:{},统一盘口id:{},三方盘口源id:{},盘口赔率不正常，计算失败。", linkId, standardMarketDataMessage.getStandardMatchInfoId(),
                        standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId());
                standardMarketDataMessagesError.add(standardMarketDataMessage);
                continue;
            }
            standardMarketDataMessage.getMarketOddsList().forEach(marketOdds -> {
                //设置最终的paOddsValue，将马来赔转欧赔后，计算赔率差绝对值
                Integer paOddsValue = BigDecimal.valueOf(initializeComponent.getConvertMalayToEurope(marketOdds.getMalayOddsValue())).multiply(new BigDecimal(Double.toString(100000))).intValue();
                marketOdds.setPaOddsValue(paOddsValue);
            });
        }
        //---------给赔率不合法的盘口排序------------
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessagesError) {
            standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
        }
    }

    /**
     * 抽水赔率
     * * 计算方式：抽水赔率转马来赔 + 水差
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @return
     */
    private boolean calculationMarketAutoOther(String linkId, StandardMarketDataMessage standardMarketDataMessage, Double spread) {
        //统一盘口ID
        Long relationMarketId = standardMarketDataMessage.getRelationMarketId();
        StringBuffer stringBuffer = new StringBuffer();
        Double diffValue = 0D;
        //收集足球、篮球附加字段玩法
        List<Long> add1List = new ArrayList<>();
        add1List.addAll(MarginCategoryConfig.FootBall_MY_CATEGORY);
        add1List.addAll(MarginCategoryConfig.BASKETBALL_MY_CATEGORY);
        Double marketValue = 0D;
        if (StringUtils.isNotBlank(standardMarketDataMessage.getAddition1()) &&
                add1List.contains(standardMarketDataMessage.getMarketCategoryId())) {
            marketValue = Double.parseDouble(standardMarketDataMessage.getAddition1());
        }
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            if (null == standardSportMarketOdds.getOddsValue() || 0 == standardSportMarketOdds.getOddsValue()) {
                log.error("::{}::综合球种,标准赛事id:{},盘口id:{},统一盘口id:{},三方盘口源id:{},盘口赔率不正常，计算失败。三方投注项id:{}",
                        linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getId(), relationMarketId, standardMarketDataMessage.getThirdMarketSourceId(), standardSportMarketOdds.getThirdOddsFieldSourceId());
                standardMarketDataMessage.setRemark("盘口赔率不正常，原始赔率为空，计算失败，盘口关盘");
                return false;
            }
            standardSportMarketOdds.setMargin(spread);
            //计算下盘
            if (standardSportMarketOdds.getOddsType().equals("SecondHalf") ||
                    standardSportMarketOdds.getOddsType().equals("Under") || standardSportMarketOdds.getOddsType().equals("Even")
                    || standardSportMarketOdds.getOddsType().equals("No")
                    || (MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardSportMarketOdds.getOddsType().equals("X"))
                    || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))
                    || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))) {
                //抽水转换赔率为马来赔
                Double oddsValue = subDoubleTwo(BigDecimal.valueOf(standardSportMarketOdds.getOddsValue()).divide(new BigDecimal(Double.toString(100000))).doubleValue());
                standardSportMarketOdds.setMalayOddsValue(initializeComponent.getEuropeConvertMalayMap().get(oddsValue));
                stringBuffer.append(",下盘统一投注项id:" + standardSportMarketOdds.getRelationMarketOddsId() + ",下盘投注项类型:" + standardSportMarketOdds.getOddsType());
                stringBuffer.append(",下盘投注项抽水赔率:" + standardSportMarketOdds.getOddsValue() + ",下盘投注项马来赔:" + standardSportMarketOdds.getMalayOddsValue());
                //获取水差配置

                ConfigMarketAutoDiffTrade marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId,standardMarketDataMessage.getStandardMatchInfoId(), relationMarketId, standardSportMarketOdds.getOddsType());
                ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = configPlaceNumAutoDiffTradeService.getItem(linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum());
                if (marketAutoDiffTrade != null) {
                    diffValue = marketAutoDiffTrade.getDiffValue();
                }
                if (configPlacenumAutoDiffTrade != null) {
                    if (configPlacenumAutoDiffTrade.getOddsType().equalsIgnoreCase(standardSportMarketOdds.getOddsType())) {
                        diffValue = configPlacenumAutoDiffTrade.getDiffValue();
                    } else {
                        diffValue = -configPlacenumAutoDiffTrade.getDiffValue();
                    }
                }
                if (diffValue != 0D) {
                    stringBuffer.append(",下盘口水差:" + diffValue);
                    boolean isOk = autoDiffCountMarketMalay.arithmeticMALAY(linkId, standardMarketDataMessage, diffValue, 0D, standardSportMarketOdds, false);
                    //如果计算失败
                    if (!isOk) {
                        log.error("::{}::综合球种,下盘口赔率不正常，水差计算失败。盘口ID:{},信息:{}", linkId, relationMarketId, stringBuffer.toString());
                        standardMarketDataMessage.setRemark("盘口赔率不正常，水差计算失败，盘口关盘");
                        return false;
                    }
                    stringBuffer.append(",抽水计算后的下盘马来赔率:" + standardSportMarketOdds.getMalayOddsValue());
                    //设置水差
                    standardSportMarketOdds.setMarketDiffValue(diffValue);
                } else {
                    stringBuffer.append(",下盘投注项:" + standardSportMarketOdds.getOddsType() + ",盘口水差不存在。");
                }
            }
        }
        //计算上盘
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            if (standardSportMarketOdds.getOddsType().equals("Under") || standardSportMarketOdds.getOddsType().equals("Even")
                    || standardSportMarketOdds.getOddsType().equals("SecondHalf")
                    || standardSportMarketOdds.getOddsType().equals("No")
                    || (MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardSportMarketOdds.getOddsType().equals("X"))
                    || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))
                    || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))) {
            } else {
                //抽水转换赔率为马来赔
                Double oddsValue = subDoubleTwo(BigDecimal.valueOf(standardSportMarketOdds.getOddsValue()).divide(new BigDecimal(Double.toString(100000))).doubleValue());
                standardSportMarketOdds.setMalayOddsValue(initializeComponent.getEuropeConvertMalayMap().get(oddsValue));
                stringBuffer.append(",上盘统一投注项id:" + standardSportMarketOdds.getRelationMarketOddsId() + ",上盘投注项类型:" + standardSportMarketOdds.getOddsType());
                stringBuffer.append(",上盘投注项抽水赔率:" + standardSportMarketOdds.getOddsValue() + ",上盘投注项马来赔:" + standardSportMarketOdds.getMalayOddsValue());
                if (diffValue != 0D) {
                    boolean isOk = autoDiffCountMarketMalay.arithmeticMALAY(linkId, standardMarketDataMessage, diffValue, 0D, standardSportMarketOdds, true);
                    if (!isOk) {
                        log.error("::{}::综合球种,上盘口赔率不正常，计算失败。盘口ID:{},信息:{}", linkId, relationMarketId, stringBuffer.toString());
                        standardMarketDataMessage.setRemark("盘口赔率不正常，水差计算失败，盘口关盘");
                        return false;
                    }
                    stringBuffer.append(",抽水计算后的上盘马来赔率:" + standardSportMarketOdds.getMalayOddsValue());
                }
            }
        }
        log.info("::{}::综合球种,计算成功。盘口ID:{},信息:{}", linkId, relationMarketId, stringBuffer.toString());
        return true;
    }

    /**
     * 根据数据源提供受让方或者小球的马来赔率和Malay Spread计算出让球方或者大球的赔率
     * 可以批判性地参考：
     * IF (malay2+Malay Spread>=1)
     * malay1=2-(malay2+Magin Spread)；
     * ELSE
     * malay1=-(malay2+Magin Spread)；
     *
     * @param myOddsValue2
     * @return
     */
    private Double underOrTransfereeAddMargin(Double spread, Double myOddsValue2) {
        Double myOddsValue1;//马来赔率加上margin
        BigDecimal malayAndMargin = new BigDecimal(myOddsValue2 + spread);
        Double malaysia = malayAndMargin.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (malaysia >= 1) {
            //  2 - malaysia
            myOddsValue1 = new BigDecimal(2).subtract(BigDecimal.valueOf(malaysia)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            //0 - malaysia
            myOddsValue1 = new BigDecimal(0).subtract(BigDecimal.valueOf(malaysia)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return myOddsValue1;
    }

    /**
     * 处理三方盘口和投注项赔率
     *
     * @param dataSourceCode
     * @param thirdMatchInfo
     * @param thirdMarketDTO
     * @param thirdMarketCategory
     * @return
     */
    public ThirdSportMarketMessage processThirdSportMarket(String linkId, String dataSourceCode, ThirdMatchInfo thirdMatchInfo, ThirdMarketDTO thirdMarketDTO, ThirdMarketCategory thirdMarketCategory ,List<ThirdSportMarketOdds> thirdSportMarketOddsUpdate) {
        log.info("::{}::processThirdSportMarket 三方盘口数据的处理, ThirdMarketDTO:{}", linkId, JSON.toJSONString(thirdMarketDTO));
        //获取赛种玩法
        StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryService.getItem(thirdMarketCategory.getReferenceId(), thirdMatchInfo.getSportId());
        //赛种不支持玩法
        if (standardSportMarketCategory == null || standardSportMarketCategory.getStatus() == 0) {
            log.info("::{}::三方赛事:{},赛种id:{},不支持玩法id:{}", linkId, thirdMatchInfo.getThirdMatchSourceId(), thirdMatchInfo.getSportId(), thirdMarketCategory.getReferenceId());
            return null;
        }
        ThirdSportMarketMessage thirdSportMarketMessage = new ThirdSportMarketMessage();
        //处理三方盘口数据，不存在新增，存在更新
        ThirdSportMarket thirdSportMarket = thirdSportMarketService.getItem(dataSourceCode, thirdMarketDTO.getThirdMarketSourceId(), thirdMatchInfo.getId());
        if (thirdSportMarket == null) {
            thirdSportMarket = thirdSportMarketService.create(linkId, thirdMarketDTO, thirdMatchInfo.getId(), standardSportMarketCategory);
        } else {
            //处理盘口移交状态,当前盘口是滚球还是赛前，赛前就关盘，滚球的忽略handover
            if (Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(thirdSportMarket.getMarketType())
                    && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)) {
                thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::三方handover处理,三方盘口源id:{},当前盘口类型:{},三方盘口源状态:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), thirdSportMarket.getMarketType(), thirdMarketDTO.getStatus());
            }
            if(Constant.SPORT_MARKET.MARKET_TYPE.LIVE_ODD_BUSINESS.equals(thirdSportMarket.getMarketType())
                    && Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(thirdMarketDTO.getMarketType())
                    && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)){
                log.info("::{}::handover处理滚球数据,忽略赛前,三方盘口源id:{},当前盘口类型:{},三方盘口源状态:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), thirdSportMarket.getMarketType(), thirdMarketDTO.getStatus());
                return null;
            }
            thirdSportMarket.setStatus(thirdMarketDTO.getStatus());
            thirdSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
            thirdSportMarket.setMarketType(thirdMarketDTO.getMarketType());
            thirdSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //tx的修改时间必须严格使用上游的修改时间
            if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode())) {
                thirdSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
            }
            thirdSportMarket.setOddsName(thirdMarketDTO.getOddsName());
            thirdSportMarket.setAddition1(thirdMarketDTO.getAddition1());
            thirdSportMarket.setAddition2(thirdMarketDTO.getAddition2());
            thirdSportMarket.setAddition3(thirdMarketDTO.getAddition3());
            thirdSportMarket.setAddition4(thirdMarketDTO.getAddition4());
            thirdSportMarket.setNumberOfWinners(thirdMarketDTO.getNumberOfWinners());
            thirdSportMarket.setOfferLineId(thirdMarketDTO.getOfferLineId());
            thirdSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
            updateOperateProxy.updateThirdSportMarket(thirdSportMarket,linkId);
        }
        BeanUtils.copyProperties(thirdSportMarket,thirdSportMarketMessage);
        thirdSportMarketMessage.setThirdSportMarketOddsList(new ArrayList<ThirdSportMarketOdds>());
        //处理三方盘口投注项
        if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
            //批量修改投注项
            List<ThirdSportMarketOdds> upOddsList = new ArrayList<>();
            for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                //获取三方玩法投注项模板
                ThirdMarketCategoryField thirdMarketCategoryField = thirdMarketCategoryFieldService.getItem(thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdTempletSourceId(), thirdMarketCategory.getId());
                if (thirdMarketCategoryField == null) {
                    log.info("::{}::三方投注项模板为空，数据源:{}，数据源原始模板id:{}，融合三方玩法id:{}", linkId, thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdTempletSourceId(), thirdMarketCategory.getId());
                    continue;
                }
                //查询三方盘口投注项信息是否存在，不存在新增，存在更新
                ThirdSportMarketOdds thirdSportMarketOdds = thirdSportMarketOddsService.getItem(dataSourceCode, thirdMarketOddsDTO.getThirdOddsFieldSourceId(), thirdSportMarket.getId());
                if (thirdSportMarketOdds == null) {
                	if(DataSourceCodeEnum.TX.code.equals(dataSourceCode)) {
                		thirdMarketOddsDTO.setModifyTime(thirdMarketDTO.getModifyTime());
                	}
                    thirdSportMarketOdds = thirdSportMarketOddsService.create(thirdMarketOddsDTO.getDataSourceCode(),linkId, thirdMarketDTO.getMarketType() == 2, thirdMarketOddsDTO, thirdSportMarket, thirdMarketCategoryField.getId());
                } else {
                    thirdSportMarketOdds.setOddsValue(thirdMarketOddsDTO.getOddsValue());
                    thirdSportMarketOdds.setOriginalOddsValue(thirdMarketOddsDTO.getOriginalOddsValue());
                    thirdSportMarketOdds.setActive(thirdMarketOddsDTO.getActive());
                    thirdSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    thirdSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
                    thirdSportMarketOdds.setOddsType(thirdMarketOddsDTO.getOddsType());
                    if ( thirdMarketDTO.getMarketType() == 2 ) {
                        thirdSportMarketOdds.setAddition1(thirdMarketOddsDTO.getAddition1());
                    }
                    //冠军投注项多语言历史数据兼容
                    if (Arrays.asList(Constant.ACTIVE_CHAMPION_DATA_SOURCE).contains(dataSourceCode)) {

                        List<I18nOutrightMarketOdds> i18nOutrightMarketOddsOld =
                                i18nOutrightMarketOddsService.selectI18nOutRightMarketOddsList(thirdMarketOddsDTO.getDataSourceCode(), thirdSportMarketOdds.getNameCode());

                        Map<String, I18nOutrightMarketOdds> oldLanguageMap = Maps.newHashMap();
                        if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsOld)) {
                            oldLanguageMap = i18nOutrightMarketOddsOld.stream().collect(Collectors.toMap(I18nOutrightMarketOdds::getLanguageType, i -> i));
                        }
                        if ( thirdMarketDTO.getMarketType() == 2 && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames())) {
                            if (thirdSportMarketOdds.getNameCode() == null) {
                                thirdSportMarketOdds.setNameCode(thirdSportMarketOdds.getId());
                            }
                            List<I18nOutrightMarketOdds> i18nMarketOddsList = new ArrayList<>();
                            List<I18nOutrightMarketOdds> i18nOutrightMarketOddsListUpdate = Lists.newArrayList();
                            for (I18nItemDTO dto : thirdMarketOddsDTO.getI18nNames()) {
                                I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                                if ( !oldLanguageMap.isEmpty()  && oldLanguageMap.containsKey(dto.getLanguageType())) {
                                    BeanUtils.copyProperties(oldLanguageMap.get(dto.getLanguageType()), i18nOutrightMarketOdds);
                                    i18nOutrightMarketOdds.setText(dto.getText());
                                    i18nOutrightMarketOddsListUpdate.add(i18nOutrightMarketOdds);
                                } else {
                                    BeanUtils.copyProperties(dto, i18nOutrightMarketOdds);
                                    i18nOutrightMarketOdds.setNameCode(thirdSportMarketOdds.getNameCode());
                                    i18nOutrightMarketOdds.setDataSourceCode(thirdSportMarketOdds.getDataSourceCode());
                                    i18nMarketOddsList.add(i18nOutrightMarketOdds);
                                }
                            }
                            try{
                                if (!CollectionUtils.isEmpty(i18nMarketOddsList)) {
                                    i18nOutrightMarketOddsService.saveBatch(i18nMarketOddsList);
                                }
                            }catch (DuplicateKeyException e) {
                                //此处只打印异常，即使入库失败
                                log.info("::{}::insert三方投注项多语言唯一约束冲突，error",linkId,e);
                            }
                        }
                    }
                    upOddsList.add(thirdSportMarketOdds);
                }
                thirdSportMarketMessage.getThirdSportMarketOddsList().add(thirdSportMarketOdds);
            }
            //批量修改投注项 ,三方盘口为关不修改投注项
            if (!CollectionUtils.isEmpty(upOddsList) && !thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
                thirdSportMarketOddsService.upThirdOddsAsyncList(linkId, thirdMatchInfo.getDataSourceCode(), upOddsList, thirdMarketDTO.getMarketOddsList());
                thirdSportMarketOddsUpdate.addAll(upOddsList);
            }
        }
        return thirdSportMarketMessage;
    }

    /**
     * 处理标准投注项和赔率
     * @param linkId
     * @param standardMatchInfo
     * @param thirdMarketDTO
     * @param thirdMarketCategory
     * @param standardSportMarketSell
     * @param standardSportMarketList
     * @param dataSourceTime
     * @param oddsTypeIdSet
     * @param categorySet
     * @param changeCategoryOddsType
     * @return
     */


    private StandardMarketDataMessage processStandardSportMarket(String linkId, StandardMatchInfo standardMatchInfo, ThirdMarketDTO thirdMarketDTO,
                                                                 ThirdMarketCategory thirdMarketCategory, StandardSportMarketSell standardSportMarketSell,
                                                                 List<StandardSportMarket> standardSportMarketList,Long dataSourceTime,Set<Long> oddsTypeIdSet,
                                                                 Set<Long> categorySet,Map<Long, List<String>> changeCategoryOddsType, List<StandardSportMarketOdds> standardSportMarketOddsUpdate) {
        //判断赛事是否为空
        if (null == standardMatchInfo) {
            log.info("::{}::processMarketAndOddsData error:{}", linkId, "标准赛事不存在");
            return null;
        }
        log.info("::{}::processStandardSportMarket,标准赛事:{}", linkId, JSON.toJSONString(standardMatchInfo));
        //判断赛事是否结束
        if (MatchStatusEnum.Closed.value.equals(standardMatchInfo.getMatchStatus()) || MatchStatusEnum.Ended.value.equals(standardMatchInfo.getMatchStatus())) {
            log.info("::{}::processMarketAndOddsData error:{}", linkId, "标准赛事已经结束,标准赛事id:" + standardMatchInfo.getId());
            return null;
        }

      //获取赛种玩法,判断当前赛种是否支持玩法
        StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryService.getItem(thirdMarketCategory.getReferenceId(), standardMatchInfo.getSportId());
        if (standardSportMarketCategory == null
                || Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getStatus())
                || (thirdMarketDTO.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.AO.code) &&Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getAoStatus()))) {
            log.info("::{}::processStandardSportMarket 玩法状态为关闭，关闭赛事盘口，标准赛事id:{},赛种id:{},玩法id:{}", linkId, standardMatchInfo.getId(), standardMatchInfo.getSportId(), thirdMarketCategory.getReferenceId());
            thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
        }

        //兼容冠军玩法
        boolean isOutRight = 2 == thirdMarketDTO.getMarketType();
        //判断赛事预开售信息
        if (null == standardSportMarketSell && !isOutRight) {
            log.info("::{}::processMarketAndOddsData error:{}", linkId, "标准赛事没有开售信息,标准赛事id:" + standardMatchInfo.getId());
            return null;
        }
        //新赛种暂时过滤开售环节
        MarketCategorySell marketCategorySell = null;
        if (!isOutRight) {
            //当未开售时增加校验，取开售缓存再校验一次,并且开售缓存里面的一定是开售了的
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId()+"_"+thirdMarketDTO.getMarketType();
            Map<String,String> oldStringHashMap = redisService.hGetAll(categoryRedisKey);
            //获取玩法开售
            //marketCategorySell = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), thirdMarketDTO.getMarketType(), thirdMarketCategory.getReferenceId());
            if (oldStringHashMap != null && oldStringHashMap.containsKey(thirdMarketCategory.getReferenceId().toString())){
                marketCategorySell = new MarketCategorySell();
                marketCategorySell.setSellStatus(SaleMatchSellStausEnum.Sold.name());
                marketCategorySell.setDataSourceCode(oldStringHashMap.get(thirdMarketCategory.getReferenceId().toString()));
            }
            if (MapUtils.isEmpty(oldStringHashMap)){
                marketCategorySell = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), thirdMarketDTO.getMarketType(), thirdMarketCategory.getReferenceId());
            }
            //开售信息不存在
            if (marketCategorySell == null) {
                log.info("::{}::标准赛事id:{},开售类型:{},未开售玩法id:{},不下发三方盘口源id:{}", linkId, standardMatchInfo.getId(), thirdMarketDTO.getMarketType(), thirdMarketCategory.getReferenceId(), thirdMarketDTO.getThirdMarketSourceId());
                return null;
            }
            if (marketCategorySell.getDataSourceCode() == null)
            {
                //当盘口是赛前盘且赛前盘开售不是该数据源时，不入标准表
                if ( thirdMarketDTO.getMarketType() == 1 && !thirdMarketDTO.getDataSourceCode().equals(standardSportMarketSell.getPreMatchDataProviderCode())) {
                    log.info("::{}::processMarketAndOddsData 标准赛事开售赛前盘:{},不是该数据源:{},标准赛事id:{}", linkId, standardSportMarketSell.getPreMatchDataProviderCode(), thirdMarketDTO.getDataSourceCode(), standardMatchInfo.getId());
                    //获取第三方盘口集合并下发
                    return null;
                }
                //当盘口是滚球盘且滚球盘开售不是该数据源时，不入标准表
                if (thirdMarketDTO.getMarketType() == 0 && !thirdMarketDTO.getDataSourceCode().equals(standardSportMarketSell.getLiveMatchDataProviderCode())) {
                    log.info("::{}::processMarketAndOddsData 标准赛事开售滚球盘:{},不是该数据源:{},标准赛事id:{}", linkId, standardSportMarketSell.getLiveMatchDataProviderCode(), thirdMarketDTO.getDataSourceCode(), standardMatchInfo.getId());
                    //获取第三方盘口集合并下发
                    return null;
                }
            }
            else
            {
                //开售状态判断是否开售
                if (!SaleMatchSellStausEnum.Sold.name().equalsIgnoreCase(marketCategorySell.getSellStatus())) {
                    if (oldStringHashMap == null)
                    {
                        log.info("::{}::标准赛事id:{},开售类型:{},开售玩法id:{},三方盘口源id:{},玩法开售表中开售状态:{}", linkId, standardMatchInfo.getId(), thirdMarketDTO.getMarketType(), thirdMarketCategory.getReferenceId(), thirdMarketDTO.getThirdMarketSourceId(), marketCategorySell.getSellStatus());
                        return null;
                    }
                    if (!oldStringHashMap.containsKey(thirdMarketCategory.getReferenceId().toString()))
                    {
                        log.info("::{}::标准赛事id:{},开售类型:{},开售玩法id:{},三方盘口源id:{},玩法开售表中开售状态:{}", linkId, standardMatchInfo.getId(), thirdMarketDTO.getMarketType(), thirdMarketCategory.getReferenceId(), thirdMarketDTO.getThirdMarketSourceId(), marketCategorySell.getSellStatus());
                        return null;
                    }
                }
                if (!marketCategorySell.getDataSourceCode().equalsIgnoreCase(thirdMarketDTO.getDataSourceCode()))
                {
                    log.info("::{}::标准赛事id:{},开售类型:{},玩法id:{},该玩法开售的赔率不是该数据源,开售赔率源:{},三方数据源:{}", linkId, standardMatchInfo.getId(), thirdMarketDTO.getMarketType(), thirdMarketCategory.getReferenceId(), marketCategorySell.getDataSourceCode(),thirdMarketDTO.getDataSourceCode());
                    return null;
                }
            }
        }

        // 处理标准盘口信息，不存在新增，存在更新
        StandardSportMarket standardSportMarket = standardSportMarketService.getItem(thirdMarketDTO.getDataSourceCode(), thirdMarketDTO.getThirdMarketSourceId(), standardMatchInfo.getId());
        // 至于Id 我觉得给UUID是可行的~
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        //是否需要校验数据源变动挡板标识
        boolean isCheckOdds = false;
        boolean hasRecoed = true;
        if (standardSportMarket == null) {
            //玩法没有关闭，生成标准盘口
            if (null != standardSportMarketCategory && standardSportMarketCategory.getStatus() != 0)
            {
                sw.start("标准盘口入库耗时");
                standardSportMarket = standardSportMarketService.create(linkId, standardMatchInfo, thirdMarketDTO, standardSportMarketCategory);
                standardSportMarketList.add(standardSportMarket);
                sw.stop();
            }
            else
            {
                log.info("::{}::标准玩法未打开,标准玩法id:{},标准赛事id:{},三方盘口源id:{},三方盘口源状态:{}", linkId,thirdMarketCategory.getReferenceId(), standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getStatus());
                return null;
            }
            if (standardSportMarket == null) {
                log.info("::{}::标准盘口创建失败,标准赛事id:{},三方盘口源id:{},三方盘口源状态:{}", linkId, standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getStatus());
                return null;
            }
        } else {
            //处理盘口移交状态,当前盘口是滚球还是赛前，赛前就关盘，滚球的话这个handover就忽略
            if(Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(standardSportMarket.getMarketType())
                    && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)){
                thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::handover处理,标准赛事id:{},盘口id:{},统一盘口id:{},三方数据源id:{},标准盘口类型:{},三方盘口源状态:{}",
                        linkId, standardMatchInfo.getId(), standardSportMarket.getId(), standardSportMarket.getRelationMarketId(), thirdMarketDTO.getThirdMarketSourceId(), standardSportMarket.getMarketType(), thirdMarketDTO.getStatus());
            }
            if(Constant.SPORT_MARKET.MARKET_TYPE.LIVE_ODD_BUSINESS.equals(standardSportMarket.getMarketType())
                    && Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(thirdMarketDTO.getMarketType())
                    && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)){
                log.info("::{}::handover处理滚球数据忽略赛前,标准赛事id:{},盘口id:{},统一盘口id:{},三方数据源id:{},标准盘口类型:{},三方盘口源状态:{}",
                        linkId, standardMatchInfo.getId(), standardSportMarket.getId(), standardSportMarket.getRelationMarketId(), thirdMarketDTO.getThirdMarketSourceId(), standardSportMarket.getMarketType(), thirdMarketDTO.getStatus());
                return null;
            }
            //新的盘口状态跟旧的盘口状态都是开才需要校验
            if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarketDTO.getStatus())
                    && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardSportMarket.getThirdMarketSourceStatus()))
            {
                isCheckOdds = true;
            }

            //判断该盘口是否已经人工编辑
            String modifyKey = ConstantSystem.CHAMPION_CACHE + standardSportMarket.getRelationMarketId();
            hasRecoed = Objects.isNull(redisService.get(modifyKey));
            //冠军盘SR的结束预警
            if( isOutRight && (
                    (StringUtils.isNotBlank(thirdMarketDTO.getAddition3()) && StringUtils.isNotBlank(standardSportMarket.getAddition3()) && !thirdMarketDTO.getAddition3().equals(standardSportMarket.getAddition3())) ||
                            ((StringUtils.isNotBlank(thirdMarketDTO.getAddition1()) && StringUtils.isNotBlank(standardSportMarket.getAddition1()) && !thirdMarketDTO.getAddition1().equals(standardSportMarket.getAddition1()))  )
            )
            ) {
                log.info("{}::满足冠军报警，盘口ID:{}", linkId,thirdMarketDTO.getThirdMarketSourceId());
                //判断redis缓存中是否存在该盘口的警报缓存，有则不执行下发警报，没有则下发警报并存入警报缓存
                Map<String,Object> alarmMap= redisService.hGetAll(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM+thirdMarketDTO.getThirdMarketSourceId());
                log.info("{}::冠军报警 alarmMap:{}", linkId, JSON.toJSONString(alarmMap));
                if (CollectionUtils.isEmpty(alarmMap)) {
                    log.info("{}::满足冠军报警，报警缓存不存在，进入报警方法，盘口ID:{}", linkId,thirdMarketDTO.getThirdMarketSourceId());
                    //未开售的盘口不下发预警
                    if (null != standardSportMarket.getRelationMarketId() ) {
                        StandardOutrightMarket standardOutrightMarket = standardOutrightMarketService.selectByExample(standardSportMarket.getRelationMarketId());
                        if ( null != standardOutrightMarket && SaleMatchSellStausEnum.Sold.name().equals(standardOutrightMarket.getMarketSellStatus()) ) {
                            TaskExecutor processTradeSystemThreadPool = threadPoolConfig.getProcessTradeSystemThreadPool();
                            processTradeSystemThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // buildChampionMatchCloseMessage(linkId, standardMatchInfo, thirdMarketDTO);
                                    } catch (Exception e) {
                                        log.error("{}::buildChampionMatchCloseMessage ERROR:{}", linkId, e);
                                    }
                                }
                            });
                        }
                    }
                }
            } else {
                //当盘口结束时间 & 下次封盘时间 修改正确后删除警报对应的Redis缓存 停止继续警报
                log.info("{}::冠军报警删除对应正确盘口的缓存，盘口ID:{}", linkId,thirdMarketDTO.getThirdMarketSourceId());
                redisService.del(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM+thirdMarketDTO.getThirdMarketSourceId());
                Set<String> keys=null;
                keys=(Set<String>) redisService.get(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM_K);
                if (null!=keys)
                {
                    keys.remove(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM+thirdMarketDTO.getThirdMarketSourceId());
                    redisService.set(Constant.REDIS_KEY.RONGHE_OUTRIGHTMARKET_ALARM_K,keys);
                }
            }
            standardSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
            //standardSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //tx的修改时间必须严格使用上游的修改时间
//            if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode())) {
            standardSportMarket.setModifyTime(null == thirdMarketDTO.getModifyTime() ? TimeUtils.millsSecondsEast8ZoneGmt() : thirdMarketDTO.getModifyTime());
//            }
            standardSportMarket.setThirdMarketSourceStatus(standardSportMarketCategory.getStatus()==0?Constant.SPORT_MARKET.STATUS.DEACTIVATED:thirdMarketDTO.getStatus());
            standardSportMarket.setStatus(standardSportMarketCategory.getStatus()==0?Constant.SPORT_MARKET.STATUS.DEACTIVATED:thirdMarketDTO.getStatus());
            standardSportMarket.setMarketType(thirdMarketDTO.getMarketType());
            standardSportMarket.setLinkId(linkId);
            standardSportMarket.setOddsName(thirdMarketDTO.getOddsName());
            standardSportMarket.setAddition1(thirdMarketDTO.getAddition1());
            standardSportMarket.setAddition2(thirdMarketDTO.getAddition2());
            standardSportMarket.setAddition3(thirdMarketDTO.getAddition3());
            if ( !hasRecoed ) {
                StandardSportMarket cacheStandardSportMarket = (StandardSportMarket) redisService.get(modifyKey);
                standardSportMarket.setAddition2(cacheStandardSportMarket.getAddition2());
                standardSportMarket.setAddition3(cacheStandardSportMarket.getAddition3());
            }
            standardSportMarket.setAddition4(thirdMarketDTO.getAddition4());
            standardSportMarket.setNumberOfWinners(thirdMarketDTO.getNumberOfWinners());
            //TX旧数据重新赋值盘口ID
            if (standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.TX.code) &&
                    (StringUtils.isBlank(standardSportMarket.getSendData()) || "''".equals(standardSportMarket.getSendData()))) {
                standardSportMarket.setRelationMarketId(Long.valueOf(standardSportMarketService.txCreateRelationMarketId(standardSportMarket.getThirdMarketSourceId())));
                standardSportMarket.setSendData(standardSportMarketService.createRelationMarketId(linkId, standardSportMarket).toString());
            }
            //异步执行更新
//            standardSportMarketService.updateByPrimaryKeySelective(standardSportMarket);
            updateOperateProxy.updateStandardSportMarket(standardSportMarket,linkId);
        }
        //需要校验球头的玩法
        if (standardMatchInfo.getSportId() == 2
                && MarginCategoryConfig.CHANGE_FLAP1.contains(standardSportMarket.getMarketCategoryId()))
        {
            categorySet.add(standardSportMarket.getMarketCategoryId());
        }

        log.info("::{}:: 修改缓存的参数: {} ,hasRecoed:{}",linkId, JSON.toJSONString(standardSportMarket), hasRecoed);

        //玩法自动关盘, 不再下发开盘和封盘的盘口
        Long relationMarketId = standardSportMarket.getRelationMarketId();
        if (null != relationMarketId) {
            String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
            Object autoCloseMap = redisService.hGet(autoCloseRedisKey, standardSportMarket.getMarketCategoryId().toString());
            if (!Objects.isNull(autoCloseMap)) {
                Object a01ExtendedTimeObjects = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + standardMatchInfo.getId());
                if (!Objects.isNull(a01ExtendedTimeObjects)) {
                    Integer a01ExtendedTimeStatus = (Integer) a01ExtendedTimeObjects;
                    if (a01ExtendedTimeStatus == 1 && thirdMarketDTO.getDataSourceCode().equals(DataSourceCodeEnum.AO.code) && MarginCategoryConfig.A01_EXTENDED_TIME_CATEGORY.contains(thirdMarketDTO.getMarketCategoryId())) {
                    } else {
                        if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarketDTO.getStatus())) {
                            log.info("::{}::自动关盘不再下发开盘和封盘的盘口,标准赛事id:{},盘口id:{},统一盘口id:{},三方盘口源id:{},三方盘口源状态:{}", linkId, standardMatchInfo.getId(), standardSportMarket.getId(), standardSportMarket.getRelationMarketId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getStatus());
                        } else {
                            standardSportMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            standardSportMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        }
                    }
                }
            }
        }
        if (isOutRight && !CollectionUtils.isEmpty(thirdMarketDTO.getI18nNames())) {
            //人工编辑多语言不允许修改
            if (hasRecoed) {
                StandardSportMarket finalStandardSportMarket = standardSportMarket;
                List<I18nOutrightMarket> i18nOutrightMarketList = new ArrayList<>();
                thirdMarketDTO.getI18nNames().forEach(i18nItemDTO -> {
                    I18nOutrightMarket i18nOutrightMarket = new I18nOutrightMarket();
                    BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarket);
                    i18nOutrightMarket.setFlag(2);
                    i18nOutrightMarket.setNameCode(finalStandardSportMarket.getNameCode());
                    i18nOutrightMarket.setDataSourceCode(finalStandardSportMarket.getDataSourceCode());
                    i18nOutrightMarketList.add(i18nOutrightMarket);
                });
                standardMarketOddsProducer.marketNameI18nSend(linkId + "name_code", i18nOutrightMarketList,
                                                              standardMatchInfo.getId());
            }
        }
        //将盘口及盘口投注项封装到一起
        StandardMarketDataMessage standardMarketDataMessage = null;
        //将最新盘口刷入缓存
        //将最新盘口刷入缓存,兼容冠军盘
        String marketKey;
        if (isOutRight) {
            marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + thirdMarketDTO.getDataSourceCode();
        } else {
            marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdMarketDTO.getDataSourceCode() + "_" + standardSportMarket.getMarketCategoryId());
        }
        //设置上一轮的盘口位置，投注项pa赔率，因为校验数据源挡板时需要上一轮操作的主盘位置以及抽水赔率
        Object obj = redisService.hGet(marketKey,relationMarketId.toString());
        //处理标准盘口投注项数据  投注项永远存最后一次有效的投注项集合(1852内容部分)
        List<StandardSportMarketOdds> standardSportMarketOddsList = new ArrayList<>();
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code)
                && MarginCategoryConfig.NO_CLOS_SPORT.contains(standardMatchInfo.getSportId())
                && MarginCategoryConfig.NO_CLOS_DATA_SOURCE_CODE.contains(thirdMarketDTO.getDataSourceCode())
                && MarginCategoryConfig.NO_CLOS_CATEGORY.contains(standardSportMarket.getMarketCategoryId())
                && thirdMarketDTO.getStatus()>=Constant.SPORT_MARKET.STATUS.DEACTIVATED
                && null != obj)
        {
            StandardMarketDataMessage standardMarketDataMessageTemp = (StandardMarketDataMessage)obj;
            standardMarketDataMessage = convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket,dataSourceTime);
            standardMarketDataMessage.setMarketOddsList(standardMarketDataMessageTemp.getMarketOddsList());
        }
        else
        {
            if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList = new ArrayList<>();
                for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                    //获取三方玩法投注项模板
                    ThirdMarketCategoryField thirdMarketCategoryField = thirdMarketCategoryFieldService.getItem(thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdTempletSourceId(), thirdMarketCategory.getId());
                    if (thirdMarketCategoryField == null) {
                        log.info("::{}::三方投注项模板为空,标准赛事id:{},盘口id:{},统一盘口id:{},三方数据源:{},数据源原始模板id:{},融合三方玩法id:{}",
                                linkId, standardMatchInfo.getId(), standardSportMarket.getId(), standardSportMarket.getRelationMarketId(), thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdTempletSourceId(), thirdMarketCategory.getId());
                        continue;
                    }
                    //TODO 校验盘口投注项是否满足标准玩法投注项条件
                    //查询标准盘口投注项信息是否存在，不存在新增，存在更新
                    StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsService.getItem(thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdOddsFieldSourceId(), standardSportMarket.getId());
                    if (standardSportMarketOdds == null) {
                        //生成并保存标准投注项
                        sw.start(thirdMarketOddsDTO.getThirdOddsFieldSourceId() + "标准盘口投注项入库耗时");
                        standardSportMarketOdds = standardSportMarketOddsService.create(linkId, isOutRight, standardSportMarket, thirdMarketOddsDTO, thirdMarketCategoryField);
                        processOdddsI18n(isOutRight, i18nOutrightMarketOddsList, thirdMarketOddsDTO, standardSportMarketOdds);
                        sw.stop();
                    } else {
                        //需要校验投注项赔率的投注项id
                        if (isCheckOdds
                                && Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(thirdMarketOddsDTO.getActive())
                                && Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(standardSportMarketOdds.getActive())
                                && (standardMatchInfo.getSportId() == 1 || standardMatchInfo.getSportId() == 2))
                        {
                            oddsTypeIdSet.add(standardSportMarketOdds.getRelationMarketOddsId());
                        }
                        standardSportMarketOdds.setI18nNames(thirdMarketOddsDTO.getI18nNames());
                        standardSportMarketOdds.setActive(thirdMarketOddsDTO.getActive());
                        standardSportMarketOdds.setOddsValue(thirdMarketOddsDTO.getOddsValue());
                        standardSportMarketOdds.setOriginalOddsValue(thirdMarketOddsDTO.getOriginalOddsValue());
                        standardSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        standardSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
                        standardSportMarketOdds.setOddsFieldsTemplateId(thirdMarketCategoryField.getReferenceId());
                        standardSportMarketOdds.setOddsType(thirdMarketOddsDTO.getOddsType());
                        if ( isOutRight ) {
                            standardSportMarketOdds.setAddition1(thirdMarketOddsDTO.getAddition1());
                        }
                        //TX旧数据重新赋值盘口投注项ID
                        if ((standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) && StringUtils.isBlank(standardSportMarketOdds.getRemark())) {
                            standardSportMarketOdds.setRelationMarketOddsId(standardSportMarketOddsService.createRelationMarketOddsId(standardSportMarketOdds, standardSportMarket));
                        }
                        //冠军投注项多语言历史数据兼容
                        if ( isOutRight && Arrays.asList(Constant.ACTIVE_CHAMPION_DATA_SOURCE).contains(standardMatchInfo.getDataSourceCode())
                                && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames()) ) {
                            if (hasRecoed) {
                                if (null == standardSportMarketOdds.getNameCode()) {
                                    standardSportMarketOdds.setNameCode(standardSportMarketOdds.getId());
                                }
                                StandardSportMarketOdds finalStandardSportMarketOdds = standardSportMarketOdds;
                                List<I18nOutrightMarketOdds> i18nOutrightMarketOddsOld =
                                        i18nOutrightMarketOddsService.getListOutrightMarketOdds(Arrays.asList(finalStandardSportMarketOdds.getNameCode()), thirdMarketOddsDTO.getDataSourceCode());
                                Map<String, I18nOutrightMarketOdds> oldLanguageMap = i18nOutrightMarketOddsOld.stream().collect(Collectors.toMap(I18nOutrightMarketOdds::getLanguageType, i -> i));
                                List<I18nOutrightMarketOdds> i18nOutrightMarketOddsListAdd = new ArrayList<>();
                                List<I18nOutrightMarketOdds> i18nOutrightMarketOddsListUpdate = new ArrayList<>();
                                thirdMarketOddsDTO.getI18nNames().forEach(i18nItemDTO -> {
                                    I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                                    if ( !oldLanguageMap.isEmpty()  && oldLanguageMap.containsKey(i18nItemDTO.getLanguageType()) ) {
                                        BeanUtils.copyProperties(oldLanguageMap.get(i18nItemDTO.getLanguageType()), i18nOutrightMarketOdds);
                                        if ( null == i18nOutrightMarketOdds.getFlag()) {
                                            i18nOutrightMarketOdds.setFlag(2);
                                        }
                                        i18nOutrightMarketOdds.setText(i18nItemDTO.getText());
                                        i18nOutrightMarketOddsListUpdate.add(i18nOutrightMarketOdds);
                                    } else {
                                        BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarketOdds);
                                        i18nOutrightMarketOdds.setFlag(2);
                                        i18nOutrightMarketOdds.setNameCode(finalStandardSportMarketOdds.getNameCode());
                                        i18nOutrightMarketOdds.setDataSourceCode(finalStandardSportMarketOdds.getDataSourceCode());
                                        i18nOutrightMarketOddsListAdd.add(i18nOutrightMarketOdds);
                                    }
                                });
                                if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsListAdd)) {
                                    i18nOutrightMarketOddsList.addAll(i18nOutrightMarketOddsListAdd);
                                }
                                if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsListUpdate)) {
                                    i18nOutrightMarketOddsList.addAll(i18nOutrightMarketOddsListUpdate);
                                }
                                try {
                                    if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsListAdd)) {
                                        i18nOutrightMarketOddsService.saveBatch(i18nOutrightMarketOddsListAdd);
                                    }
                                } catch (DuplicateKeyException e) {
                                    //此处只打印异常，即使入库失败投注项多语言依然需要投递给下游
                                    log.info("::{}::insert标准投注项多语言唯一约束冲突，error", linkId, e);
                                }
                            }
                        }
                        //异步执行更新
//                    standardSportMarketOddsService.updateByPrimaryKeySelective(standardSportMarketOdds);
                        //三方盘口为关，不修改投注项
                        if (!thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
                            //updateOperateProxy.updateStandardSportMarketOdds(standardSportMarketOdds, linkId);
                            standardSportMarketOddsUpdate.add(standardSportMarketOdds);
                        }
                    }
                    standardSportMarketOddsList.add(standardSportMarketOdds);
                }

                //冠军投注项多语言下发
                if ( isOutRight && !CollectionUtils.isEmpty(i18nOutrightMarketOddsList)) {
                    standardMarketOddsProducer.marketOddsNameI18nSend(linkId, i18nOutrightMarketOddsList,
                                                                      standardMatchInfo.getId());
                }
            }
            //新增玩法投注项排序
            if (!CollectionUtils.isEmpty(standardSportMarketOddsList) && MarginCategoryConfig.ODDS_ORDER.contains(standardSportMarket.getMarketCategoryId()))
            {
                oddsOrderByOddsType(standardSportMarketOddsList,standardSportMarket.getMarketCategoryId());
                for (int i = 0;i<standardSportMarketOddsList.size();i++)
                {
                    standardSportMarketOddsList.get(i).setOrderOdds(i+1);
                }
            }
            standardMarketDataMessage = convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket,dataSourceTime);
        }


        log.info("{}:准备缓存赔率信息，relationMarketId={}, standardMarketDataMessage={},时间:{}", linkId, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage,TimeUtils.millsSecondsEast8ZoneGmt());
        //记录赔率变动投注项
        List<String> changeOddsType = new ArrayList<>();
        if (null != obj)
        {
            StandardMarketDataMessage standardMarketDataMessage1 = (StandardMarketDataMessage)obj;
            standardMarketDataMessage.setPlaceNum(standardMarketDataMessage1.getPlaceNum());
            standardMarketDataMessage.setOldAddition1(standardMarketDataMessage1.getAddition1());
            List<StandardMarketOddsDataMessage> standardMarketOddsDataMessages = standardMarketDataMessage.getMarketOddsList();
            List<StandardMarketOddsDataMessage> standardMarketOddsDataMessages1 = standardMarketDataMessage1.getMarketOddsList();
            if (null != standardMarketOddsDataMessages && null != standardMarketOddsDataMessages1)
            {
                for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarketOddsDataMessages)
                {
                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage1 : standardMarketOddsDataMessages1)
                    {
                        if (standardMarketOddsDataMessage.getRelationMarketOddsId().equals(standardMarketOddsDataMessage1.getRelationMarketOddsId())
                                &&!standardMarketOddsDataMessage.getOddsValue().equals(standardMarketOddsDataMessage1.getOddsValue()))
                        {
                            standardMarketOddsDataMessage.setPaOddsValue(standardMarketOddsDataMessage1.getPaOddsValue());
                        }
                        if (standardMarketOddsDataMessage.getRelationMarketOddsId().equals(standardMarketOddsDataMessage1.getRelationMarketOddsId()))
                        {
                            standardMarketOddsDataMessage.setOldOriginalOddsValue(standardMarketOddsDataMessage1.getOriginalOddsValue());
                        }
                        //851冠军操盘 Au模式下，数据源下发新的赔率，Panda赔率跟随变化，同时清空该投注项的累计投注额和跳水次数
                        if(isOutRight  && standardMarketOddsDataMessage.getOddsType().equals(standardMarketOddsDataMessage1.getOddsType())){
                            changeOddsType.add(standardMarketOddsDataMessage.getOddsType());
                            log.info("::{}::数据源冠军赔率变动则清该项跳水产生的概率差,赛事ID:{},标准玩法ID:{},三方盘口源ID:{},投注项：{},改变前赔率:{},改变后赔率:{}",
                                    linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(), thirdMarketDTO.getThirdMarketSourceId(), standardMarketOddsDataMessage.getOddsType(),
                                    standardMarketOddsDataMessage1.getOddsValue(), standardMarketOddsDataMessage.getOddsValue());
                        }else
                        //852足球需求 某项数据源赔率变动则清该项跳水产生的概率差 ,并下发标识给风控
                        if (standardMatchInfo.getSportId() == 1 && MarginCategoryConfig.THREE_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())
                                && standardMarketOddsDataMessage.getOddsType().equals(standardMarketOddsDataMessage1.getOddsType())) {
                            if (!standardMarketOddsDataMessage.getOddsValue().equals(standardMarketOddsDataMessage1.getOddsValue())) {
                                //球员玩法上游传的是中文，传递给下游是namecode,独赢配置存的是namecode
                                if (MarginCategoryConfig.PLAYER_CATEGORY_ODDS.contains(standardMarketDataMessage.getMarketCategoryId())) {
                                    String oddsType = "";
                                    if (!MarginCategoryConfig.PLAYER_CATEGORY_ODDS_TYPE.contains(standardMarketOddsDataMessage1.getOddsType())) {
                                        StandardSportPlayer standardSportPlayer = standardSportPlayerService.getItem(standardMatchInfo.getSportId(), standardMarketOddsDataMessage.getAddition1());
                                        if (null != standardSportPlayer) {
                                            oddsType = standardSportPlayer.getNameCode().toString();
                                            changeOddsType.add(oddsType);
                                        }
                                    }
                                    log.info("::{}::数据源赔率变动则清该项跳水产生的概率差球员类玩法投注类型转换,赛事ID:{},标准玩法ID:{},三方盘口源ID:{},投注项：{},位置:{}",
                                            linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(), thirdMarketDTO.getThirdMarketSourceId(), oddsType, standardMarketDataMessage.getPlaceNum());
                                }
                                //球员类玩法OddsType转换前后都需要
                                changeOddsType.add(standardMarketOddsDataMessage.getOddsType());
                                log.info("::{}::数据源赔率变动则清该项跳水产生的概率差,赛事ID:{},标准玩法ID:{},三方盘口源ID:{},投注项：{},改变前赔率:{},改变后赔率:{},位置:{}",
                                        linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(), thirdMarketDTO.getThirdMarketSourceId(), standardMarketOddsDataMessage.getOddsType(),
                                        standardMarketOddsDataMessage1.getOddsValue(), standardMarketOddsDataMessage.getOddsValue(), standardMarketDataMessage.getPlaceNum());
                            }
                        }
                    }
                }
            }
            standardMarketDataMessage.setOrderNo(standardMarketDataMessage1.getOrderNo());
            //构建盘口来源更改
            if (standardMarketDataMessage1.getMarketSource() == 1) {
                //1.数据商开盘才能改变盘口来源
                if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getThirdMarketSourceStatus())) {
                    standardMarketDataMessage.setMarketSource(0);
                    log.info("::{}::构建盘口来源更改成功,标准玩法id:{},标准赛事id:{},三方盘口源id:{},三方盘口源状态:{}", linkId, thirdMarketCategory.getReferenceId(), standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getStatus());
                    //删除构建缓存
                    delConvertMarket(linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId());
                } else {
                    //2.数据商非开盘都不下发
                    log.info("::{}::构建盘口来源更改失败,标准玩法id:{},标准赛事id:{},三方盘口源id:{},三方盘口源状态:{},不下发", linkId, thirdMarketCategory.getReferenceId(), standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getStatus());
                    return null;
                }
            }
        }
        if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode()))
        {
            standardMarketDataMessage.setPlaceNum(thirdMarketDTO.getOfferLineId());
        }
        //记录赔率变更玩法投注项
        if (!CollectionUtils.isEmpty(changeOddsType)) {
            if (standardMarketDataMessage.getPlaceNum() != null) {
                //清除概率差
                standardMarketDataMessage.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(linkId,standardMarketDataMessage.getMarketCategoryId(),standardMarketDataMessage.getAddition1(),standardMarketDataMessage.getAddition2(),standardMarketDataMessage.getAddition3(),standardMarketDataMessage.getAddition4(),standardMarketDataMessage.getAddition5(),String.valueOf(standardMarketDataMessage.getStandardMatchInfoId())));
                configMarketMarginGapService.upProbabilityByMatchIdAndCategoryId(linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(),standardMarketDataMessage.getChildMarketCategoryId(), changeOddsType, standardMarketDataMessage.getPlaceNum());
                changeCategoryOddsType.put(standardMarketDataMessage.getMarketCategoryId(), changeOddsType);
            }
        }
        standardMarketDataMessage.setMarketSource(0);
        if (standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE)) {
            standardMarketDataMessage.setOldThirdMarketSourceStatus(null);
        }
        //并发问题设置子玩法
        standardMarketDataMessage.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(linkId, standardMarketDataMessage.getMarketCategoryId(),
                standardMarketDataMessage.getAddition1(), standardMarketDataMessage.getAddition2(), standardMarketDataMessage.getAddition3(),
                standardMarketDataMessage.getAddition4(), standardMarketDataMessage.getAddition5(), String.valueOf(standardMarketDataMessage.getStandardMatchInfoId())));
        boolean flag = redisService.hSet(marketKey, standardMarketDataMessage.getRelationMarketId().toString(),standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
        String marketKey1 = Constant.REDIS_KEY.RONGHE_ORDER_STANDARD_MARKET + standardMatchInfo.getId();
        redisService.hSet(marketKey1, standardMarketDataMessage.getMarketCategoryId().toString(), 1, marketCacheTime(standardMatchInfo.getBeginTime()));
        if(!flag){
            log.error("::{}::标准赛事id:{},relationMarketId={},刷入缓存失败,赔率处理异常", linkId, standardMatchInfo.getId(),standardMarketDataMessage.getRelationMarketId());
        }
        log.info("::{}::标准赛事id:{},标准盘口id:{},统一盘口id:{},标准盘口状态:{},三方数据源id:{},三方盘口源状态:{},处理总耗时{}ms",
                linkId, standardMatchInfo.getId(), standardSportMarket.getId(), standardSportMarket.getRelationMarketId(), standardSportMarket.getStatus(), thirdMarketDTO.getThirdMarketSourceId(), standardSportMarket.getThirdMarketSourceStatus(), sw.getTotalTimeMillis());
        return standardMarketDataMessage;
    }


    public void processOdddsI18n(boolean isOutRight, List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList, ThirdMarketOddsDTO thirdMarketOddsDTO, StandardSportMarketOdds standardSportMarketOdds) {
        if (isOutRight && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames())) {
            if (null == standardSportMarketOdds.getNameCode()) {
                standardSportMarketOdds.setNameCode(standardSportMarketOdds.getId());
            }
            StandardSportMarketOdds finalStandardSportMarketOdds = standardSportMarketOdds;
            thirdMarketOddsDTO.getI18nNames().forEach(i18nItemDTO -> {
                I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarketOdds);
                i18nOutrightMarketOdds.setFlag(2);
                i18nOutrightMarketOdds.setNameCode(finalStandardSportMarketOdds.getNameCode());
                i18nOutrightMarketOdds.setDataSourceCode(finalStandardSportMarketOdds.getDataSourceCode());
                i18nOutrightMarketOddsList.add(i18nOutrightMarketOdds);
            });
        }
    }
    /**
     * -......+
     * 1X,X2,12--70,72
     * yes,no---76
     * @param standardSportMarketOddsList
     */
    public void oddsOrderByOddsType(List<StandardSportMarketOdds> standardSportMarketOddsList,Long standardGategoryId)
    {
        if (standardGategoryId == 70L || standardGategoryId == 72L || standardGategoryId == 76L)
        {
            //不做任何处理，按照上游给的数据默认排序
        }
        else if (standardGategoryId == 337L)
        {
            StandardSportMarketOdds draw = null;
            standardSportMarketOddsList.sort(Comparator.comparing(StandardSportMarketOdds::getOrderOdds));
            //337平局放最后
            for (StandardSportMarketOdds s : standardSportMarketOddsList)
            {
                if (s.getOddsType().equalsIgnoreCase("draw") || s.getOddsType().equalsIgnoreCase("x"))
                {
                    standardSportMarketOddsList.remove(s);
                    draw = s;
                    break;
                }
            }
            if (draw != null)
            {
                standardSportMarketOddsList.add(draw);
            }
        }
        else
        {
            List<StandardSportMarketOdds> tempList = new ArrayList<>(standardSportMarketOddsList);
            standardSportMarketOddsList.clear();
            StandardSportMarketOdds start = null;
            StandardSportMarketOdds end = null;
            List<StandardSportMarketOdds> del = new ArrayList<>();
            for (StandardSportMarketOdds s : tempList)
            {
                if (s.getOddsType().contains("-"))
                {
                    start = s;
                    del.add(s);
                    standardSportMarketOddsList.add(start);
                }
                if (s.getOddsType().contains("+"))
                {
                    end = s;
                    del.add(s);
                }
            }
            tempList.removeAll(del);
            tempList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getOddsType())));
            standardSportMarketOddsList.addAll(tempList);
            if (end != null)
            {
                standardSportMarketOddsList.add(end);
            }
        }
    }

    @Override
    public void delDiffByMatchIdAndCategoryList(String linkId, Long standardMatchId, List<Long> categoryList, Integer sportId) {
        super.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList, sportId);
    }

    @Override
    public Map<String, Long> validateSportIds(String dataSourceCode, Set<String> thirdSportIds) {
        return super.validateSportIds(dataSourceCode, thirdSportIds);
    }

    /**
     * 封装投递给下游的标准赔率数据结构
     *
     * @param standardSportMarketOddsList
     * @param standardSportMarket
     * @return
     */
    public StandardMarketDataMessage convertToStandardMarketDataMessage(List<StandardSportMarketOdds> standardSportMarketOddsList, StandardSportMarket standardSportMarket,Long dataSourceTime) {
        StandardMarketDataMessage standardMarketMessage = new StandardMarketDataMessage();
        BeanUtils.copyProperties(standardSportMarket, standardMarketMessage);
        //收集足球、篮球附加字段玩法
        List<Long> add1List = new ArrayList<>();
        add1List.addAll(MarginCategoryConfig.FootBall_MY_CATEGORY);
        add1List.addAll(MarginCategoryConfig.BASKETBALL_MY_CATEGORY);
        //盘口值的绝对值  addition1
        if (StringUtils.isNotBlank(standardMarketMessage.getAddition1()) &&
                add1List.contains(standardMarketMessage.getMarketCategoryId())) {
            standardMarketMessage.setMarketOddsValue(Math.abs(Double.parseDouble(standardMarketMessage.getAddition1())));
        } else {
            standardMarketMessage.setMarketOddsValue(0D);
        }

        if (!CollectionUtils.isEmpty(standardSportMarketOddsList)) {
            List<StandardMarketOddsDataMessage> standardMarketOddsMessageList = new ArrayList<>();
            for (StandardSportMarketOdds standardSportMarketOdds : standardSportMarketOddsList) {
                StandardMarketOddsDataMessage standardMarketOddsMessage = new StandardMarketOddsDataMessage();
                BeanUtils.copyProperties(standardSportMarketOdds, standardMarketOddsMessage);
                standardMarketOddsMessageList.add(standardMarketOddsMessage);
            }
            standardMarketMessage.setMarketOddsList(standardMarketOddsMessageList);
        }
        standardMarketMessage.setModifyTime(null == standardMarketMessage.getModifyTime() ? dataSourceTime : standardMarketMessage.getModifyTime());
        return standardMarketMessage;
    }

    public ThirdMatchInfo getThirdMatchInfoByMatchId(boolean isOutRight, Long standardMatchId, String dataSourcecode) {
        if (isOutRight) {
            ThirdOutrightMatchInfo thirdOutrightMatchInfo = thirdOutrightMatchInfoService.getItemByMatchId(standardMatchId, dataSourcecode);
            if (thirdOutrightMatchInfo == null) {
                return null;
            }
            //三方赛事信息转换
            ThirdMatchInfo thirdMatchInfo = new ThirdMatchInfo();
            thirdMatchInfo.setId(thirdOutrightMatchInfo.getId());
            thirdMatchInfo.setSportId(thirdOutrightMatchInfo.getSportId());
            thirdMatchInfo.setReferenceId(thirdOutrightMatchInfo.getReferenceId());
            thirdMatchInfo.setDataSourceCode(thirdOutrightMatchInfo.getDataSourceCode());
            thirdMatchInfo.setThirdMatchSourceId(thirdOutrightMatchInfo.getThirdOutrightSourceId());
            return thirdMatchInfo;
        }
        return thirdMatchInfoService.getItem(standardMatchId, dataSourcecode);
    }

    public ThirdMatchInfo getThirdMatchInfo(boolean isOutRight, String dataSourceCode, String thirdMatchSourceId) {
        if (isOutRight) {
            ThirdOutrightMatchInfo thirdOutrightMatchInfo = thirdOutrightMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
            if (thirdOutrightMatchInfo == null) {
                return null;
            }
            //三方赛事信息转换
            ThirdMatchInfo thirdMatchInfo = new ThirdMatchInfo();
            thirdMatchInfo.setId(thirdOutrightMatchInfo.getId());
            thirdMatchInfo.setSportId(thirdOutrightMatchInfo.getSportId());
            thirdMatchInfo.setReferenceId(thirdOutrightMatchInfo.getReferenceId());
            thirdMatchInfo.setDataSourceCode(thirdOutrightMatchInfo.getDataSourceCode());
            thirdMatchInfo.setThirdMatchSourceId(thirdOutrightMatchInfo.getThirdOutrightSourceId());
            return thirdMatchInfo;
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
        if (thirdMatchInfo == null) {
            return null;
        }
        //兼容缓存覆盖问题
        if (thirdMatchInfo.getReferenceId() == null || thirdMatchInfo.getReferenceId() == 0) {
            return thirdMatchInfoService.getItem(thirdMatchInfo.getId());
        }
        return thirdMatchInfo;
    }

    public StandardMatchInfoDetail getStandardMatchInfo(boolean isOutRight, Long standardMatchId) {
        StandardMatchInfoDetail standardMatchInfoDetail = new StandardMatchInfoDetail();
        if (isOutRight) {
            StandardOutrightMatchInfo standardOutrightMatchInfo = standardOutrightMatchInfoService.getItem(standardMatchId);
            if (null == standardOutrightMatchInfo) {
                return null;
            }
            //标准赛事信息转换
            BeanUtils.copyProperties(standardOutrightMatchInfo, standardMatchInfoDetail);
            standardMatchInfoDetail.setId(standardOutrightMatchInfo.getId());
            standardMatchInfoDetail.setSportId(standardOutrightMatchInfo.getSportId());
            standardMatchInfoDetail.setDataSourceCode(standardOutrightMatchInfo.getDataSourceCode());
            standardMatchInfoDetail.setOperateMatchStatus(standardOutrightMatchInfo.getMatchMarketStatus());
            standardMatchInfoDetail.setMatchType(1);
            standardMatchInfoDetail.setAutoSellStatus(standardOutrightMatchInfo.getAutoSellStatus());
            //冠军赛事结束时间赋值给beginTime 用于盘口缓存时间计算
            standardMatchInfoDetail.setBeginTime(standardOutrightMatchInfo.getStandrdOutrightMatchEndTime());
            return standardMatchInfoDetail;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        //standardMatchInfo==null
        if (ObjectUtils.isEmpty(standardMatchInfo))
        {
            return null;
        }
        BeanUtils.copyProperties(standardMatchInfo, standardMatchInfoDetail);
        standardMatchInfoDetail.setMatchType(0);
        return standardMatchInfoDetail;
    }

    public StandardSportMarketSell getStandardSportMarketSell(boolean isOutRight, Long standardMatchId) {
        if (isOutRight) {
            StandardOutrightMatchInfo standardOutrightMatchInfo = standardOutrightMatchInfoService.getItem(standardMatchId);
            if (standardOutrightMatchInfo == null) {
                return null;
            }
            StandardSportMarketSell standardSportMarketSell = new StandardSportMarketSell();
            standardSportMarketSell.setMatchInfoId(standardOutrightMatchInfo.getId());
            standardSportMarketSell.setPreMatchDataProviderCode(standardOutrightMatchInfo.getDataSourceCode());
            standardSportMarketSell.setPreMatchSellStatus(standardOutrightMatchInfo.getSellStatus());
            return standardSportMarketSell;
        }
        return standardSportMarketSellService.getItem(standardMatchId);
    }

    /**
     * 获取缓存中的所有盘口，赛前数据商和滚球数据商
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardSportMarketSell
     * @return
     */
    public Map<String, StandardMarketDataMessage> getStringStandardMarketDataMessageMap(Set<Long> marketCategoryIds,String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
        int oddsLive = isOddsLive(standardMatchInfo.getId());
        //赛前数据源
        String preProviderCode = standardSportMarketSell.getPreMatchDataProviderCode();
        //滚球数据源
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        log.info("::{}:: 标准赛事id：{},赛前服务商={},滚球服务商={} ", linkId, standardMatchInfo.getId(), preProviderCode,
                liveProviderCode);
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
        if (StringUtils.isNotBlank(preProviderCode) && 1 == oddsLive) {
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
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                    //只取赛前盘
                    Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1))
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(preMap);
                    log.info("::{}:: sendMatchMarketData,赛前玩法开售key={}, 赛前redisKey={},赛前盘缓存总数据：{} ",
                            linkId, categoryRedisKey, tempRedisKey, preMap.size());
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
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(redisKey);
                    //只取赛前盘
                    Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(preMap);
                    log.info("::{}:: sendMatchMarketData 赛前redisKey={},赛前盘缓存总数据：{} ", linkId, redisKey, preMap.size());
                }
            }
        }
        if (StringUtils.isNotBlank(liveProviderCode) && 0 == oddsLive) {
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
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                    //只取滚球盘
                    Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e ->
                            e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(liveMap);
                    log.info("::{}:: sendMatchMarketData,滚球玩法开售key={}, 滚球redisKey={},滚球盘缓存总数据：{} ",
                            linkId, categoryRedisKey, tempRedisKey, liveMap.size());
                }
            } else {
                //获取玩法开售表，滚球玩法数据源
                List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItemByDataSourceCodeAndMarketType(standardMatchInfo.getId(), liveProviderCode, "0");
                if (CollectionUtils.isEmpty(marketCategorySell)) {
                    return stringStandardMarketDataMessageMap;
                }
                Set<Long> marketCategorySellIdSet = marketCategorySell.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
                for (Long marketCategorySellId : marketCategorySellIdSet) {
                    //获取缓存中所有赛前盘口
                    String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + liveProviderCode + "_" + marketCategorySellId);
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(redisKey);
                    //只取滚球盘
                    Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(liveMap);
                    log.info("::{}:: sendMatchMarketData 滚球redisKey={}，滚球盘缓存总数据：{} ", linkId, redisKey, liveMap.size());
                }
            }
        }
        return stringStandardMarketDataMessageMap;
    }


    /**
     * 冠军盘获取缓存中的所有盘口，赛前数据商和滚球数据商
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardSportMarketSell
     * @return
     */
    public Map<String, StandardMarketDataMessage> getChampionStandardMarketDataMessageMap(String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
//        //以下数据库逻辑操作有并发问题，这里需要以赛事维度加redis锁
//        String lockValue = UUIdUtils.getId() + "_" + linkId;
//        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
//        redisService.tryLock(redisLocKey, lockValue, 5, 3);
        try {
            //赛前数据源
            String preProviderCode = standardSportMarketSell.getPreMatchDataProviderCode();
            //滚球数据源
            String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
            log.info("::{}:: 标准赛事id：{},赛前服务商={},滚球服务商={} ", linkId, standardMatchInfo.getId(), preProviderCode, liveProviderCode);
            Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
            if (StringUtils.isNotBlank(preProviderCode)) {
                String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
                Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
                if (MapUtil.isNotEmpty(stringHashMap)) {
                    stringHashMap.values().stream().distinct().forEach(dataSourceCode -> {
                        String tempRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode;
                        Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                        //只取赛前盘
                        Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1) && stringHashMap.containsKey(String.valueOf(e.getValue().getMarketCategoryId())) && stringHashMap.get(String.valueOf(e.getValue().getMarketCategoryId())).equals(dataSourceCode)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                        stringStandardMarketDataMessageMap.putAll(preMap);
                        log.info("::{}:: sendMatchMarketData,赛前玩法开售key={}, 赛前redisKey={},赛前盘缓存总数据：{} ", linkId, categoryRedisKey, tempRedisKey, preMap.size());
                    });
                } else {
                    //获取缓存中所有赛前盘口
                    String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + preProviderCode;
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(redisKey);
                    //只取赛前盘
                    Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && (e.getValue().getMarketType().equals(1) || e.getValue().getMarketType().equals(2))).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(preMap);
                    log.info("::{}:: sendMatchMarketData 赛前redisKey={},赛前盘缓存总数据：{} ", linkId, redisKey, preMap.size());
                }
            }
            if (StringUtils.isNotBlank(liveProviderCode)) {
                String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 0;
                Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
                if (MapUtil.isNotEmpty(stringHashMap)) {
                    stringHashMap.values().stream().distinct().forEach(dataSourceCode -> {
                        String tempRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode;
                        Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                        //只取滚球盘
                        Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0) && stringHashMap.containsKey(String.valueOf(e.getValue().getMarketCategoryId())) && stringHashMap.get(String.valueOf(e.getValue().getMarketCategoryId())).equals(dataSourceCode)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                        stringStandardMarketDataMessageMap.putAll(liveMap);
                        log.info("::{}:: sendMatchMarketData,滚球玩法开售key={}, 滚球redisKey={},滚球盘缓存总数据：{} ", linkId, categoryRedisKey, tempRedisKey, liveMap.size());
                    });
                } else {
                    //获取缓存中所有滚球盘口
                    String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + liveProviderCode;
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(redisKey);
                    //只取滚球盘
                    Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(liveMap);
                    log.info("::{}:: sendMatchMarketData 滚球redisKey={}，滚球盘缓存总数据：{} ", linkId, redisKey, liveMap.size());
                }
            }
            if ( MapUtils.isNotEmpty(stringStandardMarketDataMessageMap)) {
                for ( Map.Entry<String, StandardMarketDataMessage> entry : stringStandardMarketDataMessageMap.entrySet()) {
                    StandardMarketDataMessage standardMarketMessage = entry.getValue();
                    if ( null == standardMarketMessage.getNumberOfWinners() || standardMarketMessage.getNumberOfWinners() < 1 )
                    {
                        standardMarketMessage.setNumberOfWinners(1);
                    }
                }
            }
            return stringStandardMarketDataMessageMap;
        } finally {
//            redisService.unLock(redisLocKey, lockValue);
        }
    }

    /**
     *  100s满足的关盘盘口数据源
     * @param linkId
     * @param standardMatchInfo
     * @param sourceCategoryIdsMap
     * @return
     */
    public List<StandardMarketDataMessage> getStringStandardMarketDataMessageByDataSourceCode(String linkId, StandardMatchInfo standardMatchInfo, Map<String, List<Long>> sourceCategoryIdsMap) {
        //以下数据库逻辑操作有并发问题，这里需要以赛事维度加redis锁
        String lockValue = UUIdUtils.getId() + "_" + linkId;
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
        redisService.tryLock(redisLocKey, lockValue, 5, 3);
        Integer oddsLive = isOddsLive(standardMatchInfo.getId());
        try {
            List<StandardMarketDataMessage> standardMarketDataMessageList = new ArrayList<>();
            sourceCategoryIdsMap.keySet().forEach(dataSourceCode -> {
                List<Long> categoryIds = sourceCategoryIdsMap.get(dataSourceCode);
                for (Long categoryId : categoryIds) {
                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode+"_"+categoryId);
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                    Map<String, StandardMarketDataMessage> marketMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(oddsLive) && categoryIds.contains(e.getValue().getMarketCategoryId())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    if (!MapUtils.isEmpty(marketMap)) {
                        standardMarketDataMessageList.addAll(marketMap.values());
                    }
                }
            });
            return standardMarketDataMessageList;
        } finally {
            redisService.unLock(redisLocKey, lockValue);
            log.info("::{}::getStringStandardMarketDataMessageByDataSourceCode,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        }
    }


    /**
     * 处理赛前盘数据下发
     *
     * @param linkId
     * @param preCode           赛前数据源KEY
     * @param standardMatchInfo 标准赛事
     * @param dataSourceTime
     */
    @Deprecated
    public void preMatchDeactivatedDispose(String linkId, String preCode, StandardMatchInfo standardMatchInfo, Long dataSourceTime) {
        linkId = linkId + "_close_pre";
        //获取赛前缓存盘口数据
        String preRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + preCode;
        Map<String, StandardMarketDataMessage> preMarketDataMessageMap = redisService.hGetAll(preRedisKey);
        if (CollectionUtils.isEmpty(preMarketDataMessageMap)) {
            return;
        }
        log.info("::{}::preMatchDeactivatedDispose,标准赛事id:{},数据源缓存redisKey:{},盘口集合:{}", linkId, standardMatchInfo.getId(), preRedisKey, preMarketDataMessageMap.size());
        //下发关盘玩法
        Set<Long> marketCategoryIdSet = new HashSet<>();
        //记录关盘的盘口
        Map<String, StandardMarketDataMessage> deactivatedMarket = new HashMap<String, StandardMarketDataMessage>();
        Integer deactivated = Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED;
        String finalLinkId = linkId;
        preMarketDataMessageMap.values().forEach(market -> {
            //判断盘口状态、数据源状态 开封锁修改为关
            if (!deactivated.equals(market.getStatus()) || !deactivated.equals(market.getThirdMarketSourceStatus())) {
                market.setStatus(deactivated);
                market.setThirdMarketSourceStatus(deactivated);
                market.setLinkId(finalLinkId);
                marketCategoryIdSet.add(market.getMarketCategoryId());
                deactivatedMarket.put(market.getRelationMarketId().toString(), market);
            }
        });
        if (CollectionUtils.isEmpty(deactivatedMarket)) {
            log.info("::{}::preMatchDeactivatedDispose,滚球盘时下发赛前盘关盘,标准赛事id:{},需要关盘的盘口为空:{}", linkId, standardMatchInfo.getId(), marketCategoryIdSet);
            return;
        }
        //关盘的盘口回刷缓存
        redisService.hSetAll(preRedisKey, deactivatedMarket, marketCacheTime(standardMatchInfo.getBeginTime()));
        log.info("::{}::preMatchDeactivatedDispose,滚球盘时下发赛前盘关盘,标准赛事id:{},处理关盘玩法数据:{}", linkId, standardMatchInfo.getId(), marketCategoryIdSet);
        processOddsByPanda(linkId,-1,null, standardMatchInfo, marketCategoryIdSet, deactivatedMarket, dataSourceTime, new HashMap<>(), Boolean.FALSE);
    }

    /**
     * 下发滚球赔率标识
     *
     * @param linkId
     * @param dataSourceCode
     * @param standardMatchId
     * @param sportId         运动ID
     * @param isSend          是否推送赛事级别封盘
     */
    public void sendOddsLive(String linkId, String dataSourceCode, Long standardMatchId, Long sportId, boolean isSend,Long dataSourceTime,Integer advance) {
        String switchLiveRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchId;
        Object obj = redisService.get(switchLiveRedisKey);
        if (Objects.isNull(obj)) {
            thirdMarketPreResultProcessor.liveCloseCashOutStatus(linkId+"_CLOSE_CASHOUT_STATUS", standardMatchId, dataSourceTime,"1",false);
            String switchValue = dataSourceCode + standardMatchId;
            redisService.set(switchLiveRedisKey, switchValue, RedisConfig.REDIS_WEEK_TIME);
            //通知下游
            switchStatusProducer.standardMatchSwitchStatus(linkId, standardMatchId, Constant.ODDS_LIVE, dataSourceCode, sportId, false,advance);
            //清除投注项配置
            configMarketOddsStatusService.updateStatusByMatchId(linkId, standardMatchId, Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE);
            //推送赛事级别状态，只封足、蓝
            List<Long> sportIdList = Lists.newArrayList(StandardSportTypeEnum.Basketball.code);
            if (isSend && sportIdList.contains(sportId)) {
//                matchStatusConfig(linkId, standardMatchId, dataSourceTime);
                dataMerchantBaffleProducer.switchDataSourceSendRiskMQ(linkId, standardMatchId, sportId);
            }
            //修改足球/篮球三方球头状态
            if (sportId.equals(StandardSportTypeEnum.FootBall.code) || sportId.equals(StandardSportTypeEnum.Basketball.code)) {
                upThirdAoMarketStatus(linkId, standardMatchId);
            }
        }
    }

    /**
     * 修改足球三方球头状态
     *
     * @param linkId
     * @param standardMatchId
     * @param
     */
    public void upThirdAoMarketStatus(String linkId, Long standardMatchId) {
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchId, DataSourceCodeEnum.AO.code);
        if (null == thirdMatchInfo) {
            return;
        }
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(standardMatchId);
        Set<String> dataSourceCodes = new HashSet<>();
        List<String> redisKeys = new ArrayList<>();
        thirdMatchInfoList.forEach(thirdMatch -> {
            //足球
            redisKeys.add(Constant.REDIS_KEY.THIRD_MARKET_HEAD + standardMatchId + "_" + thirdMatch.getDataSourceCode());
            //篮球
            redisKeys.add(Constant.REDIS_KEY.THIRD_BASKETBALL_MARKET_HEAD + standardMatchId + "_" + thirdMatch.getDataSourceCode());
            dataSourceCodes.add(thirdMatch.getDataSourceCode());
        });
        redisService.del(redisKeys);
        log.info("::{}::赛前转滚球删除三方球头数据：{}", linkId, redisKeys);
        //下发
        standardMarketOddsProducer.sendAoThirdMarketUpStatusAsync(linkId, thirdMatchInfo.getThirdMatchSourceId(),thirdMatchInfo.getSportId(),dataSourceCodes);
    }

    /**
     * 设置数据源滚球赔率第一次下发时间
     * @param standardMatchInfo
     */
    public void setLiveOddsTime( StandardMatchInfo standardMatchInfo){
        if (!StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) ) {
            return;
        }

        //没有下发过滚球赔率，需要记录第一次下发的时间
        String switchLiveTimeRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_LIVE_TIME ;
        redisService.hSet(switchLiveTimeRedisKey,standardMatchInfo.getId().toString(),System.currentTimeMillis(),marketCacheTime(standardMatchInfo.getBeginTime()));
        log.info("checkLiveTime,构建盘计时开始,标准赛事id:{},time:{}", standardMatchInfo.getId(), System.currentTimeMillis());
    }
    @Async("AccessMatchMarketData")
    public void checkLiveTime(){
        try{
            String switchLiveTimeRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_LIVE_TIME ;
            Map<String,Long> liveOddsTime = redisService.hGetAll(switchLiveTimeRedisKey);
            if (!MapUtils.isEmpty(liveOddsTime)){
                liveOddsTime.forEach((k,v)->{
                    long currentTimestamp = System.currentTimeMillis();
                    long cachedTimestamp = v; // 从缓存获取时间戳
                    long diffInMillis = currentTimestamp - cachedTimestamp;
                    long diffInSeconds = diffInMillis / 1000;
                    boolean within6Seconds = diffInSeconds >= 120;
                    if (within6Seconds){
                        redisService.hDel(switchLiveTimeRedisKey,k);
                        String linkId = UUIdUtils.getId()+"_autoclose_2min";
                        StandardMatchInfo standardMatchInfo =
                                standardMatchInfoService.getItem(Long.valueOf(k));
                        if (standardMatchInfo == null) {
                            log.info("::{}::checkLiveTime,标准赛事未找到，标准赛事id:{}", linkId,
                                    k);
                            return;
                        }
                        if (!StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) ) {
                            return;
                        }

                        StandardSportMarketSell standardSportMarketSell =
                                standardSportMarketSellService.getItem(standardMatchInfo.getId());
                        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap = getStringStandardMarketDataMessageMap(new HashSet<>(), linkId,  standardMatchInfo,  standardSportMarketSell);
                        List<StandardMarketDataMessage> standardMarketDataMessages = standardMarketDataMessageMap.values().stream().filter(e->e.getMarketSource() == 1||e.getMarketType()==1).collect(Collectors.toList());
                        if (!standardMarketDataMessages.isEmpty()){
                            Set<Long> marketCategoryIdSet = standardMarketDataMessages.stream().map(e->e.getMarketCategoryId()).collect(Collectors.toSet());

                            standardMarketDataMessages.stream().forEach(data->{
                                String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + data.getStandardMatchInfoId() + "_" + data.getDataSourceCode() + "_" + data.getMarketCategoryId());
                                data.setStatus(2);
                                data.setThirdMarketSourceStatus(2);
                                data.setOldThirdMarketSourceStatus(2);
                                boolean flag = redisService.hSet(marketKey, data.getRelationMarketId().toString(), data, marketCacheTime(standardMatchInfo.getBeginTime()));
                            });
                            Map<String,StandardMarketDataMessage> deactivatedMarket = standardMarketDataMessages.stream().collect(Collectors.toMap(e->e.getRelationMarketId().toString(), Function.identity(), (v1, v2)->v1));
                            log.info("::{}::checkLiveTime,构建盘超时关闭,标准赛事id:{},已过秒数:{},关闭玩法数:{},玩法:{}", linkId, standardMatchInfo.getId(), diffInSeconds, marketCategoryIdSet.size(), marketCategoryIdSet);
                            processOddsByPanda(linkId,-1,null, standardMatchInfo, marketCategoryIdSet, deactivatedMarket, System.currentTimeMillis(), new HashMap<>(), Boolean.FALSE);
                        }
                    }
                });
            }
        }catch (Exception e){
            log.error("checkLiveTime,构建盘超时关闭任务异常", e);
        }
    }
    /**
     * 关闭赛前盘
     * 1.已经下发过滚球，来了赛前盘不下发
     * 2.第一次下发的滚球盘，需要关闭赛前盘
     * @param linkId
     * @param standardSportMarketSell
     * @param marketType
     * @param standardMatchInfo
     * @return
     */
    public Long newClosePreMarkets(String linkId, StandardSportMarketSell standardSportMarketSell, Integer marketType, StandardMatchInfo standardMatchInfo,
                                   Long dataSourceTime, Boolean isSend, List<Long> footbalLiveCategory, Integer advance) {
        Set<Long> marketCategoryIdSet = new HashSet<>();
        String switchLiveRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId();
        Object obj = redisService.get(switchLiveRedisKey);
        if (marketType == 1) {
            if (!Objects.isNull(obj)) {
                log.info("::{}:: 标准赛事id：{},已经下发过滚球盘口,赛前盘不下发", linkId, standardMatchInfo.getId());
                return dataSourceTime;
            }
        } else {
            if (Objects.isNull(obj)) {
                setLiveOddsTime(standardMatchInfo);
                //先下发滚球标识给下游
                //if (!standardSportMarketSell.getLiveMatchSellStatus().equalsIgnoreCase("Unsold")) {
                    sendOddsLive(linkId, standardSportMarketSell.getLiveMatchDataProviderCode(), standardMatchInfo.getId(), standardMatchInfo.getSportId(),
                            isSend, dataSourceTime, advance);
                //}
                //赛前数据源
                String preProviderCode = standardSportMarketSell.getPreMatchDataProviderCode();
                //滚球数据源
                String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
                log.info("::{}:: 标准赛事id：{},赛前转滚球赛前服务商={},滚球服务商={} ", linkId, standardMatchInfo.getId(), preProviderCode, liveProviderCode);
                Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
                if (StringUtils.isNotBlank(preProviderCode)) {
                    String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
                    Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
                    if (MapUtil.isNotEmpty(stringHashMap)) {
                        for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                            String marketCategoryId = dataSourceCodeEntry.getKey();
                            String dataSourceCode = dataSourceCodeEntry.getValue();
                            String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                            Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                            //只取赛前盘
                            Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1))
                                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                            //足球主列表玩法不关盘
                            if(StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())
                                    && footbalLiveCategory.contains(Long.valueOf(marketCategoryId))){
                                log.info("::{}:: 标准赛事id：{},获取玩法开售表，赛前玩法数据源：{}，足球主列表玩法不关盘 ", linkId, standardMatchInfo.getId(),marketCategoryId);
                                if(!preMap.isEmpty()){
                                    redisService.hDel(tempRedisKey, preMap.keySet().toArray());
                                    log.info("::{}:: sendMatchMarketData:{},1.赛前转滚球删除盘口：{} ", linkId, dataSourceCode, preMap.keySet());
                                }
                                continue;
                            }
                            marketCategoryIdSet.add(Long.valueOf(marketCategoryId));
                            stringStandardMarketDataMessageMap.putAll(preMap);
                            log.info("::{}:: sendMatchMarketData,赛前玩法开售key={}, 赛前redisKey={},赛前盘缓存总数据：{} ", linkId, categoryRedisKey, tempRedisKey, preMap.size());
                            if(!preMap.isEmpty()){
                                redisService.hDel(tempRedisKey, preMap.keySet().toArray());
                                log.info("::{}:: sendMatchMarketData:{},2.赛前转滚球删除盘口：{} ", linkId, dataSourceCode, preMap.keySet());
                            }
                        }
                    } else {
                        log.info("::{}:: 标准赛事id：{},获取玩法开售表，赛前玩法数据源 ", linkId, standardMatchInfo.getId());
                        //获取玩法开售表，赛前玩法数据源
                        List<MarketCategorySell> marketCategorySell = marketCategorySellService.getItemByMarketType(standardMatchInfo.getId(), "1");
                        if (CollectionUtils.isEmpty(marketCategorySell)) {
                            log.info("::{}:: 标准赛事id：{},获取玩法开售表，赛前玩法数据源不存在 ", linkId, standardMatchInfo.getId());
                            return dataSourceTime;
                        }
                        Set<Long> marketCategorySellIdSet = marketCategorySell.stream().map(MarketCategorySell::getMarketCategoryId).collect(Collectors.toSet());
                        log.info("::{}:: 标准赛事id：{},获取玩法开售表，赛前玩法数据源：{} ", linkId, standardMatchInfo.getId(),marketCategorySellIdSet);
                        for (Long marketCategorySellId : marketCategorySellIdSet) {
                            //获取缓存中所有赛前盘口
                            String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + preProviderCode + "_" + marketCategorySellId);
                            Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(redisKey);
                            //只取赛前盘
                            Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                            //足球主列表玩法不关盘
                            if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())
                                    && footbalLiveCategory.contains(marketCategorySellId)) {
                                log.info("::{}:: 标准赛事id：{},获取玩法开售表，赛前玩法数据源：{}，足球主列表玩法不关盘 ", linkId, standardMatchInfo.getId(),marketCategorySellIdSet);
                                if(!preMap.isEmpty()){
                                    redisService.hDel(redisKey, preMap.keySet().toArray());
                                    log.info("::{}:: sendMatchMarketData:{},3.赛前转滚球删除盘口：{} ", linkId, preProviderCode, preMap.keySet());
                                }
                                continue;
                            }
                            marketCategoryIdSet.add(marketCategorySellId);
                            stringStandardMarketDataMessageMap.putAll(preMap);
                            log.info("::{}:: sendMatchMarketData 赛前redisKey={},赛前盘缓存总数据：{} ", linkId, redisKey, preMap.size());
                            if(!preMap.isEmpty()){
                                redisService.hDel(redisKey, preMap.keySet().toArray());
                                log.info("::{}:: sendMatchMarketData:{},4.赛前转滚球删除盘口：{} ", linkId, preProviderCode, preMap.keySet());
                            }
                        }
                    }
                }
                //先下发赛前盘关盘
                if (MapUtils.isNotEmpty(stringStandardMarketDataMessageMap)) {
                    //清除投注项配置
                    configMarketOddsStatusService.updateStatusByMatchId(linkId, standardMatchInfo.getId(), Constant.CONFIG_MARKET_ODDS_STATUS.CLOSE);
                    //关闭赛前设置子玩法
                    stringStandardMarketDataMessageMap.forEach((k, v) -> {
                        v.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        v.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        if (v.getChildMarketCategoryId() == null) {
                            v.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(linkId, v.getMarketCategoryId(), v.getAddition1(), v.getAddition2(), v.getAddition3(), v.getAddition4(), v.getAddition5(), String.valueOf(v.getStandardMatchInfoId())));
                        }
                    });
                    Long dataSourceTimeNew = System.currentTimeMillis();
                    //在下发关闭赛事盘口
                    marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId + "_PRE_CLOSE", stringStandardMarketDataMessageMap,standardMatchInfo,marketCategoryIdSet,null,null,null,false);
                    processOddsByPanda(linkId + "_PRE_CLOSE",-1,null, standardMatchInfo, marketCategoryIdSet, stringStandardMarketDataMessageMap, dataSourceTimeNew, new HashMap<>(), Boolean.FALSE);
                    return dataSourceTimeNew + 1;
                }
            }
            return dataSourceTime;
        }
        return dataSourceTime;
    }

    /**
     * 1.TX让球比分处理 addition3:主队比分 addition4:客队比分
     * 2.计算全场盘口值 、替换三方盘口源ID
     *
     * @param linkId
     * @param marketCategoryId
     * @param standardMatchInfo
     * @param thirdMarketDTO
     * @param thirdMatchInfo
     */
    public void txHandicapDispose(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, ThirdMarketDTO thirdMarketDTO, ThirdMatchInfo thirdMatchInfo, String dataSourceCode) {
        if (dataSourceCode.equals(DataSourceCodeEnum.TX.code)) {
            String addition3 = thirdMarketDTO.getAddition3();
            String addition4 = thirdMarketDTO.getAddition4();
            if ("0".equals(addition3) && "0".equals(addition4)) {
                //获取比分中心提供主客队比分
                CommonItem goalObj = getFootballCacheScores(linkId, standardMatchInfo, marketCategoryId);
                if (goalObj == null) {
                    return;
                }
                Integer goalHome = goalObj.getHome();
                Integer goalAway = goalObj.getAway();
                //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
                String addition2 = Double.toString(Double.valueOf(thirdMarketDTO.getAddition1()) - (goalHome - goalAway)).replace(".0", "");
                //三方盘口替换
                String thirdCategoryId = thirdMarketDTO.getThirdMarketSourceId().split("_")[1];
                String calculateThirdMarketSourceId = thirdMatchInfo.getThirdMatchSourceId() + "_" + thirdCategoryId + "_" + addition2 + "_" + thirdMarketDTO.getOfferLineId();
                log.info("::{}::标准赛事ID:{},TX主客队比分更换,源主队比分:{},源客队比分:{},源三方源盘口id:{},源基准分盘口值:{},主队比分:{},客队比分:{},计算出三方源盘口id:{},计算出全场盘口值:{},标准玩法ID:{},比分:{}",
                        linkId, standardMatchInfo.getId(), addition3, addition4, thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getAddition1(), goalHome, goalAway, calculateThirdMarketSourceId,
                        addition2, marketCategoryId, goalObj);
                thirdMarketDTO.setThirdMarketSourceId(calculateThirdMarketSourceId);
                thirdMarketDTO.setAddition2(addition2);
                thirdMarketDTO.setAddition3(String.valueOf(goalHome));
                thirdMarketDTO.setAddition4(String.valueOf(goalAway));
                //第三方投注项原始ID替换
                List<ThirdMarketOddsDTO> thirdMarketOddsList = thirdMarketDTO.getMarketOddsList();
                if (!CollectionUtils.isEmpty(thirdMarketOddsList)) {
                    thirdMarketOddsList.forEach(o -> {
                        //2048839_33_0_1_1
                        String thirdOddsFieldSourceId = o.getThirdOddsFieldSourceId();
                        if (StringUtils.isNotEmpty(thirdOddsFieldSourceId)) {
                            String[] split = thirdOddsFieldSourceId.split("_");
                            String thirdOddsFieldSourceIdStr = split[0] + "_" + split[1] + "_" + addition2 + "_" + split[3] + "_" + split[4];
                            o.setThirdOddsFieldSourceId(thirdOddsFieldSourceIdStr);
                            log.info("::{}::标准赛事ID:{},TX让球三方投注项原始ID替换,计算后三方源盘口id:{},替换前投注项原始ID:{},替换后投注项原始ID:{}",
                                    linkId, standardMatchInfo.getId(), calculateThirdMarketSourceId, thirdOddsFieldSourceId, thirdOddsFieldSourceIdStr);
                        }
                    });
                }
            }
        }
    }

    /**
     * 足球获取缓存比分
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryId
     */
    private CommonItem getFootballCacheScores(String linkId, StandardMatchInfo standardMatchInfo, Long marketCategoryId) {
        //获取比分中心提供主客队比分
        FootballCacheScores scores = new FootballCacheScores();
        Object scoreObj = redisService.get(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + standardMatchInfo.getId()));
        if (!Objects.isNull(scoreObj)) {
            scores = JSONObject.parseObject(scoreObj.toString(), FootballCacheScores.class);
            if (scores.getGoal() == null) {
                scores.setGoal(new CommonItem(0, 0));
            }
            if (scores.getGoalOverTime() == null) {
                scores.setGoalOverTime(new CommonItem(0, 0));
            }
            if (scores.getGoalPenalty() == null) {
                scores.setGoalPenalty(new CommonItem(0, 0));
            }
            if (scores.getRedCard() == null) {
                scores.setRedCard(new CommonItem(0, 0));
            }
            if (scores.getYellowCard() == null) {
                scores.setYellowCard(new CommonItem(0, 0));
            }
        } else {
            scores = preScoreBuild();
        }
        CommonItem goalObj = new CommonItem();
        if (MarginCategoryConfig.FOOTBALL_SCORE_CATEGORY.contains(marketCategoryId)) {
            //常规进球玩法
            goalObj = scores.getGoal();
        } else if (MarginCategoryConfig.FOOTBALL_OVERTIME_SCORE_CATEGORY.contains(marketCategoryId)) {
            //加时赛比分
            goalObj = scores.getGoalOverTime();
        } else if (MarginCategoryConfig.FOOTBALL_PENALTY_SCORE_CATEGORY.contains(marketCategoryId)) {
            //点球大战比分
            goalObj = scores.getGoalPenalty();
        } else if (MarginCategoryConfig.FOOTBALL_RAD_SCORE_CATEGORY.contains(marketCategoryId)) {
            //罚牌比分处理
            Integer home = scores.getRedCard().getHome() * 2 + scores.getYellowCard().getHome();
            Integer away = scores.getRedCard().getAway() * 2 + scores.getYellowCard().getAway();
            scores.setFaCard(new CommonItem(home, away));
            goalObj = scores.getFaCard();
        } else if (MarginCategoryConfig.FOOTBALL_YELLOW_SCORE_CATEGORY.contains(marketCategoryId)) {
            //黄牌比分处理
            goalObj = scores.getYellowCard();
        } else {
            return null;
        }
        return goalObj;
    }

    /**
     * 比分中心 角球让球盘、上半场角球让球盘 基准分计算
     *
     * @param linkId
     * @param marketCategoryId
     * @param standardMatchInfo
     * @param thirdMarketDTO
     */
    public void cornerScore(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, ThirdMarketDTO thirdMarketDTO, ThirdMatchInfo thirdMatchInfo, String dataSourceCode) {
        if (MarginCategoryConfig.FOOTBALL_CORNER_SCORE_CATEGORY.contains(marketCategoryId)) {
            //获取比分中心提供主客队比分
            FootballCacheScores scores = new FootballCacheScores();
            Object scoreObj = redisService.get(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + standardMatchInfo.getId()));
            if (!Objects.isNull(scoreObj)) {
                scores = JSONObject.parseObject(scoreObj.toString(), FootballCacheScores.class);
                if (scores.getCorner() == null) {
                    scores.setCorner(new CommonItem(0, 0));
                }
            } else {
                scores = preScoreBuild();
            }
            //角球玩法
            CommonItem goalObj = scores.getCorner();
            Integer cornerHome = goalObj.getHome();
            Integer cornerAway = goalObj.getAway();
            //TX根据基准分计算角球全场盘口值
            if (dataSourceCode.equals(DataSourceCodeEnum.TX.code)) {
                //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
                String addition2 = Double.toString(Double.valueOf(thirdMarketDTO.getAddition1()) - (cornerHome - cornerAway)).replace(".0", "");
                //三方盘口替换
                String thirdCategoryId = thirdMarketDTO.getThirdMarketSourceId().split("_")[1];
                String calculateThirdMarketSourceId = thirdMatchInfo.getThirdMatchSourceId() + "_" + thirdCategoryId + "_" + addition2 + "_" + thirdMarketDTO.getOfferLineId();
                log.info("::{}::标准赛事ID:{},TX角球计算全场盘口值,源三方源盘口id:{},源基准分盘口值:{},主队比分:{},客队比分:{},计算出三方源盘口id:{},计算出全场盘口值:{},标准玩法ID:{},比分:{}",
                        linkId, standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getAddition1(), cornerHome, cornerAway, calculateThirdMarketSourceId,
                        addition2, marketCategoryId, scores);
                thirdMarketDTO.setThirdMarketSourceId(calculateThirdMarketSourceId);
                thirdMarketDTO.setAddition2(addition2);
                thirdMarketDTO.setAddition3(String.valueOf(cornerHome));
                thirdMarketDTO.setAddition4(String.valueOf(cornerAway));
                //第三方投注项原始ID替换
                List<ThirdMarketOddsDTO> thirdMarketOddsList = thirdMarketDTO.getMarketOddsList();
                if (!CollectionUtils.isEmpty(thirdMarketOddsList)) {
                    thirdMarketOddsList.forEach(o -> {
                        String thirdOddsFieldSourceId = o.getThirdOddsFieldSourceId();
                        if (StringUtils.isNotEmpty(thirdOddsFieldSourceId)) {
                            String[] split = thirdOddsFieldSourceId.split("_");
                            String thirdOddsFieldSourceIdStr = split[0] + "_" + split[1] + "_" + addition2 + "_" + split[3] + "_" + split[4];
                            o.setThirdOddsFieldSourceId(thirdOddsFieldSourceIdStr);
                            log.info("::{}::标准赛事ID:{},TX角球三方投注项原始ID替换,计算后三方源盘口id:{},替换前投注项原始ID:{},替换后投注项原始ID:{}",
                                    linkId, standardMatchInfo.getId(), calculateThirdMarketSourceId, thirdOddsFieldSourceId, thirdOddsFieldSourceIdStr);
                        }
                    });
                }
            } else if (!dataSourceCode.equals(DataSourceCodeEnum.BC.code) && !dataSourceCode.equals(DataSourceCodeEnum.LS.code)) {
                    //基准分盘口值 = 全场盘口值 + (主队比分 - 客队比分)
                    Double addition1 = Double.valueOf(thirdMarketDTO.getAddition2()) + (cornerHome - cornerAway);
                    log.info("::{}::标准赛事ID:{},三方源盘口id:{},标准玩法ID:{},角球玩法计算出基准分盘口值:{},源全场盘口值:{},主队比分:{},客队比分:{}",
                            linkId, standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), marketCategoryId, addition1, thirdMarketDTO.getAddition2(), cornerHome, cornerAway);
                    thirdMarketDTO.setAddition1(String.valueOf(addition1));
                    thirdMarketDTO.setAddition3(String.valueOf(cornerHome));
                    thirdMarketDTO.setAddition4(String.valueOf(cornerAway));
            }
        }
    }

    /**
     * 15分钟玩法 缓存获取 默认比分
     *
     * @param linkId
     * @param marketCategoryId
     * @param standardMatchInfo
     * @param thirdMarketDTO
     */
    public void fifteenMinutesScore(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, ThirdMarketDTO thirdMarketDTO) {
        if (MarginCategoryConfig.FIFTEEN_MINUTES_SCORE.get(marketCategoryId) != null) {
            String add2 = thirdMarketDTO.getAddition2();
            String add5 = thirdMarketDTO.getAddition5();
            if (StringUtils.isEmpty(add2) || StringUtils.isEmpty(add5)) {
                thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::15分钟基准分计算,参数不完整关盘处理,赛事ID:{},三方盘口源ID:{},标准玩法ID:{},add2:{},add5:{}",
                        linkId, standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), marketCategoryId, add2, add5);
                return;
            }
            //缓存KEY 不存在默认比分 0 - 0
            CommonItem scores = new CommonItem();
            //获取比分中心提供比分
            Object o = redisService.get(DigestUtil.md5Hex("ABSCORES:" + standardMatchInfo.getId()));
            if (!ObjectUtils.isEmpty(o)) {
                String key = add5.replace(",", "-");
                Map<String, JSONObject> matchAbScores = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), Map.class);
                FootballCacheScores cacheScores = JSONObject.toJavaObject(matchAbScores.get(key), FootballCacheScores.class);
                if (cacheScores != null) {
                    if ("goal".equals(MarginCategoryConfig.FIFTEEN_MINUTES_SCORE.get(marketCategoryId))) {
                        scores = cacheScores.getGoal();
                    } else {
                        scores = cacheScores.getCorner();
                    }
                }
                log.info("::{}::15分钟基准分计算,赛事ID:{},三方盘口源ID:{},标准玩法ID:{},赛事缓存比分数据:{}",
                        linkId, standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), marketCategoryId, o);
                fifteenMinutesScoreCalculate(linkId, standardMatchInfo.getId(), thirdMarketDTO, scores);
            } else {
                //缓存不存在默认 0-0
                log.info("::{}::15分钟基准分计算,赛事缓存KEY不存在,默认0-0比分计算,赛事ID:{},三方盘口源ID:{},标准玩法ID:{}",
                        linkId, standardMatchInfo.getId(), thirdMarketDTO.getThirdMarketSourceId(), marketCategoryId);
                fifteenMinutesScoreCalculate(linkId, standardMatchInfo.getId(), thirdMarketDTO, scores);
            }
        }
    }

    /**
     * 15分钟玩法 计算基准分
     *
     * @param linkId
     * @param standardMatchId
     * @param thirdMarketDTO
     * @param scores
     */
    public void fifteenMinutesScoreCalculate(String linkId, Long standardMatchId, ThirdMarketDTO thirdMarketDTO, CommonItem scores) {
        Integer home = scores.getHome();
        Integer away = scores.getAway();
        thirdMarketDTO.setAddition3(String.valueOf(home));
        thirdMarketDTO.setAddition4(String.valueOf(away));
        //基准分盘口值 = 全场盘口值 + (主队比分 - 客队比分)
        Double add1 = Double.valueOf(thirdMarketDTO.getAddition2()) + (home - away);
        thirdMarketDTO.setAddition1(String.valueOf(add1));
        log.info("::{}::15分钟基准分计算,标准赛事ID:{},三方源盘口id:{},计算出基准分盘口值:{},全场盘口值:{},主队比分:{},客队比分:{},缓存数据:{}",
                linkId, standardMatchId, thirdMarketDTO.getThirdMarketSourceId(), add1, thirdMarketDTO.getAddition2(), home, away, JSONObject.toJSONString(scores));
    }

    /**
     * 赛前盘比分中心没有比分 需要自己构建 0 比分
     */
    public static FootballCacheScores preScoreBuild() {
        FootballCacheScores footballCacheScores = new FootballCacheScores();
        footballCacheScores.setCorner(new CommonItem(0, 0));
        footballCacheScores.setGoal(new CommonItem(0, 0));
        footballCacheScores.setGoalOverTime(new CommonItem(0, 0));
        footballCacheScores.setGoalPenalty(new CommonItem(0, 0));
        footballCacheScores.setRedCard(new CommonItem(0, 0));
        footballCacheScores.setYellowCard(new CommonItem(0, 0));
        return footballCacheScores;
    }

    /**
     * 解除告警
     *
     * @param linkId
     * @param marketType
     * @param standardMatchInfo
     * @param categoryList
     */
    @Deprecated
    public void matchOddsWarning(String linkId, Integer marketType, StandardMatchInfo standardMatchInfo, Set<Long> categoryList) {
//        if (null == standardMatchInfo || marketType != 0) {
//            return;
//        }
//        //足球主流玩法报警机制
//        if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
//            String oddsWarningKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_WARNING;
//            for (Long marketCategoryId : categoryList) {
//                if (!MarginCategoryConfig.MATCH_CATEGORY_ODDS_WARNING.contains(marketCategoryId)) {
//                    continue;
//                }
//                Map<Long, Map<String, Object>> warningListMap = new HashMap<>();
//                Object obj = redisService.hGet(oddsWarningKey, String.valueOf(standardMatchInfo.getId()));
//                if (!Objects.isNull(obj)) {
//                    warningListMap = (Map<Long, Map<String, Object>>) obj;
//                }
//                Map<String, Object> objectMap = warningListMap.get(marketCategoryId);
//                if (!CollectionUtils.isEmpty(objectMap)) {
//                    boolean sign = (boolean) objectMap.get("sign");
//                    if (sign) {
//                        //下发风控解除报警 false
//                        matchoddWarningProducer.sendMatchOddsWarningRisk(linkId, standardMatchInfo.getId(), marketCategoryId, false);
//                    } else {
//                        log.info("::{}::标准赛事ID:{},标准玩法ID:{},刷新赔率告警缓存时间", linkId, standardMatchInfo.getId(), marketCategoryId);
//                    }
//                }
//                Map<String, Object> warningMap = new HashMap<>();
//                warningMap.put("sign", false);
//                warningMap.put("time", TimeUtils.millsSecondsEast8ZoneGmt());
//                warningListMap.put(marketCategoryId, warningMap);
//                redisService.hSet(oddsWarningKey, String.valueOf(standardMatchInfo.getId()), warningListMap);
//            }
//        }
    }

    /**
     * 步骤1
     * 1.玩法分组、坑位分组
     * 2.获取每个坑位上修改时间最新的盘口，不是最新的盘口改为数据商关盘，修改坑位为999
     * 步骤2
     * 1.根据统一盘口ID 分组 （sendData）
     * 2.数据源状态存在 开关盘口数据 取开盘的盘口 ，两个以上的开盘取修改时间最新的
     * 3.数据源状态存在 关关的盘口数据 ，取修改时间最新的盘口数据
     *
     * @param linkId
     * @param standardMarketDataMessagesAUTO
     */
    public void txMarketMerge(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessagesAUTO) {
        log.info("::{}::TX盘口数据融合处理进入:{}", linkId, standardMarketDataMessagesAUTO.size());
        List<StandardMarketDataMessage> resultMarketMerge = new ArrayList<>();
        List<StandardMarketDataMessage> marketMerge = new ArrayList<>();
        List<StandardMarketDataMessage> collect = new ArrayList<>();
        //Step1:同坑位处理 玩法分组 ，再坑位分组
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGrop = standardMarketDataMessagesAUTO.stream().filter(e -> e.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        if (CollectionUtils.isEmpty(marketCategoryGrop)) {
            log.info("::{}::非TX盘口数据融合处理条数为：{}", linkId, standardMarketDataMessagesAUTO.size());
            return;
        }
        Set<Long> changeCategoryIdS = new HashSet<>();
        //数据太多，不打印投注项
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("marketOddsList");
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGrop.entrySet()) {
            List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
            collect.addAll(marketDataMessages);
            changeCategoryIdS.add(entry.getKey());
            //相同坑位分组
            Map<Integer, List<StandardMarketDataMessage>> placeNumGrop = marketDataMessages.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
            for (Map.Entry<Integer, List<StandardMarketDataMessage>> placeEntry : placeNumGrop.entrySet()) {
                List<StandardMarketDataMessage> placeMarketDataMessages = placeEntry.getValue();
                //TX旧数据赋值
                for (StandardMarketDataMessage market : placeMarketDataMessages) {
                    if (StringUtils.isBlank(market.getSendData()) || "''".equals(market.getSendData())) {
                        market.setSendData(market.getRelationMarketId().toString());
                        if (!CollectionUtils.isEmpty(market.getMarketOddsList())) {
                            for (StandardMarketOddsDataMessage marketOdds : market.getMarketOddsList()) {
                                marketOdds.setRemark(marketOdds.getRelationMarketOddsId().toString());
                            }
                        }
                    }
                }
                //Step1:相同坑位根据盘口修改时间升序，第一个盘口不做状态处理其他盘口改为关盘
                List<StandardMarketDataMessage> resultPlaceMarketDataMessages = placeMarketDataMessages.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                log.info("::{}::标准玩法:{},TX相同坑位:{},相同坑位根据盘口修改时间升序:{}", linkId, entry.getKey(), placeEntry.getKey(), JSONObject.toJSONString(resultPlaceMarketDataMessages, filter));
                int num = 0;
                for (StandardMarketDataMessage placeMarket : resultPlaceMarketDataMessages) {
                    if (num == 0) {
                        if (placeMarket.getStatus().equals(Constant.SPORT_MARKET.STATUS.LOSE)) {
                            placeMarket.setPlaceNum(999);
                        }
                        resultMarketMerge.add(placeMarket);
                        log.info("::{}::三方盘口ID:{},TX盘口相同坑位最新盘口", linkId, placeMarket.getThirdMarketSourceId());
                    }
                    num++;
                }
            }
        }
        //Step1:根据同一盘口ID分组
        Map<String, List<StandardMarketDataMessage>> relationMarketGrop = resultMarketMerge.stream().filter(e -> StringUtils.isNotBlank(e.getSendData())).peek(m -> m.setRelationMarketId(Long.valueOf(m.getSendData()))).collect(Collectors.groupingBy(StandardMarketDataMessage::getSendData));
        log.info("::{}::TX盘口相同坑位根据时间戳处理结果:{}", linkId, JSONObject.toJSONString(relationMarketGrop, filter));
        if (!CollectionUtils.isEmpty(relationMarketGrop)) {
            for (Map.Entry<String, List<StandardMarketDataMessage>> entry : relationMarketGrop.entrySet()) {
                String relationMarketId = entry.getKey();
                List<StandardMarketDataMessage> relationMarket = entry.getValue();
                //Step2:数据源状态存在 开关盘口数据 取开盘的盘口 ，如果有多条开盘数据 取最新的修改时间对应的盘口数据
                List<StandardMarketDataMessage> openMarket = relationMarket.stream().filter(e -> Constant.SPORT_MARKET.STATUS.ACTIVE.equals(e.getThirdMarketSourceStatus())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(openMarket)) {
                    //说明有两个以上的开 取修改时间最新的
                    if (openMarket.size() > 1) {
                        relationMarket = relationMarket.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                        log.info("::{}::统一盘口ID:{},TX盘口数据取开盘或者关盘的最新修改时间数据:{}", linkId, relationMarketId, JSON.toJSONString(relationMarket.get(0)));
                        marketMerge.add(relationMarket.get(0));
                    } else {
                        //只有一个开
                        log.info("::{}::统一盘口ID:{},TX盘口数据取开盘数据:{}", linkId, relationMarketId, JSON.toJSONString(openMarket.get(0)));
                        marketMerge.add(openMarket.get(0));
                    }
                    continue;
                }
                //Step3:数据源状态存在关关的盘口数据 ，取修改时间最新的盘口数据
                relationMarket = relationMarket.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                log.info("::{}::统一盘口ID:{},TX盘口数据取开盘或者关盘的最新修改时间数据:{}", linkId, relationMarketId, JSON.toJSONString(relationMarket.get(0)));
                marketMerge.add(relationMarket.get(0));
            }
            if (!CollectionUtils.isEmpty(collect)) {
                log.info("::{}::总数:{},TX处理盘口数:{},处理后盘口数:{},", linkId, standardMarketDataMessagesAUTO.size(), collect.size(), marketMerge.size());
                //清除TX 非融合前的盘口
                standardMarketDataMessagesAUTO.removeAll(collect);
                //添加融合后的盘口
                standardMarketDataMessagesAUTO.addAll(marketMerge);
                //只取有改变的多玩法数据源 TX盘口数据 根据统一盘口ID去重 相同统一盘口ID取开盘的
                Map<Long, List<StandardMarketDataMessage>> changeMarketDataMessage = standardMarketDataMessagesAUTO.stream().filter(e -> changeCategoryIdS.contains(e.getChildMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getRelationMarketId));
                for (Map.Entry<Long, List<StandardMarketDataMessage>> placeEntry : changeMarketDataMessage.entrySet()) {
                    List<StandardMarketDataMessage> marketDataMessages = placeEntry.getValue();
                    standardMarketDataMessagesAUTO.removeAll(marketDataMessages);
                    List<StandardMarketDataMessage> openMarketData = marketDataMessages.stream().filter(e -> e.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(openMarketData)) {
                        openMarketData = marketDataMessages.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                    }
                    standardMarketDataMessagesAUTO.add(openMarketData.get(0));
                }
                //TX坑位最后排序
                Map<Long, List<StandardMarketDataMessage>> marketCategoryGropfinal = standardMarketDataMessagesAUTO.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
                for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGropfinal.entrySet()) {
                    List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
                    txMarketSort(linkId, entry.getKey(), standardMatchInfo, marketDataMessages);
                }
            }
        }
    }

    /**
     * TX A模式 ，A+模式 设置缓存
     * Map<玩法ID，球头>
     *
     * @param standardMatchInfo
     * @param standardMarketDataMessage
     */
    private void setBall(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketDataMessage standardMarketDataMessage, String addition1) {
        if (StringUtils.isEmpty(addition1)) {
            return;
        }
        //TX篮球设置球头值缓存 Map<玩法ID，球头>
        if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())
                && (DataSourceCodeEnum.TX.code.equals(standardMarketDataMessage.getDataSourceCode())
                || DataSourceCodeEnum.AO.code.equals(standardMarketDataMessage.getDataSourceCode()))
                && standardMarketDataMessage.getPlaceNum() == 1) {
            Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
            log.info("::{}::TX/AO缓存最新球头,赛事ID:{},玩法:{},球头值:{},", linkId, standardMatchInfo.getId(), marketCategoryId, addition1);
            String setBall = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_BALL + standardMatchInfo.getId();
            redisService.hSet(setBall, String.valueOf(marketCategoryId), addition1, RedisConfig.REDIS_HOUR_TIME);
        }
    }

    /**
     * AO坑位盘口处理
     *
     * @param linkId
     * @param standardMarketDataMessagesAUTO
     */
    public void aoMarketPlaceMerge(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessagesAUTO, Boolean isTrue) {
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGrop = standardMarketDataMessagesAUTO.stream().filter(e ->
                e.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        if (CollectionUtils.isEmpty(marketCategoryGrop)) {
            log.info("::{}::非AO盘口数据融合处理条数为：{}", linkId, standardMarketDataMessagesAUTO.size());
            return;
        }
        Set<Long> categoryids = new HashSet<>();
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGrop.entrySet()) {
            List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
            standardMarketDataMessagesAUTO.removeAll(marketDataMessages);
            //相同坑位分组
            Map<Integer, List<StandardMarketDataMessage>> placeNumGrop = marketDataMessages.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
            for (Map.Entry<Integer, List<StandardMarketDataMessage>> placeEntry : placeNumGrop.entrySet()) {
                List<StandardMarketDataMessage> placeMarketDataMessages = placeEntry.getValue();
                //Step1:相同坑位根据盘口修改时间升序，第一个盘口不做状态处理其他盘口改为关盘
                List<StandardMarketDataMessage> resultPlaceMarketDataMessages = placeMarketDataMessages.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                int num = 0;
                for (StandardMarketDataMessage placeMarket : resultPlaceMarketDataMessages) {
                    if (num == 0) {
                        if (placeMarket.getStatus().equals(Constant.SPORT_MARKET.STATUS.LOSE)) {
                            placeMarket.setPlaceNum(999);
                            categoryids.add(placeMarket.getMarketCategoryId());
                        }
                        standardMarketDataMessagesAUTO.add(placeMarket);
                        log.info("::{}::三方盘口ID:{},AO盘口相同坑位最新盘口", linkId, placeMarket.getThirdMarketSourceId());
                    }
                    num++;
                }
            }
        }
        if (CollectionUtils.isEmpty(categoryids)){
            return;
        }
        //坑位最后排序
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGropfinal = standardMarketDataMessagesAUTO.stream().filter(s->categoryids.contains(s.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGropfinal.entrySet()) {
            List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
            Map<Integer, List<StandardMarketDataMessage>> groupPlace = marketDataMessages.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
            //Map<坑位, 排序后坑位>
            Map<Integer, Integer> sortPlaceMap = new HashMap<>();
            Set<Integer> placeNums = groupPlace.keySet();
            log.info("::{}::placeNums:{}",linkId,placeNums);
            int a=1;
            for (int placeNum : placeNums) {
                sortPlaceMap.put(placeNum, a);
                a++;
            }
            marketDataMessages.forEach(t -> {
                if (t.getPlaceNum() == 999){
                    t.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    t.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }
                t.setPlaceNum(sortPlaceMap.get(t.getPlaceNum()));
                t.setPlaceNumId(t.getStandardMatchInfoId() + "_" + t.getMarketCategoryId() + "_" + t.getChildMarketCategoryId() + "_" + t.getPlaceNum());
            });
            log.info("::{}::坑位最后排序:{}",linkId,JSONObject.toJSONString(marketDataMessages));
        }
    }
    /**
     * tx坑位盘口处理
     *
     * @param linkId
     * @param standardMarketDataMessagesAUTO
     */
    public void txMarketPlaceMerge(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessagesAUTO, Boolean isTrue) {
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGrop = standardMarketDataMessagesAUTO.stream().filter(e -> e.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        if (CollectionUtils.isEmpty(marketCategoryGrop)) {
            log.info("::{}::非tx盘口数据融合处理条数为：{}", linkId, standardMarketDataMessagesAUTO.size());
            return;
        }
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGrop.entrySet()) {
            List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
            standardMarketDataMessagesAUTO.removeAll(marketDataMessages);
            //相同坑位分组
            Map<Integer, List<StandardMarketDataMessage>> placeNumGrop = marketDataMessages.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
            for (Map.Entry<Integer, List<StandardMarketDataMessage>> placeEntry : placeNumGrop.entrySet()) {
                List<StandardMarketDataMessage> placeMarketDataMessages = placeEntry.getValue();
                //Step1:相同坑位根据盘口修改时间升序，第一个盘口不做状态处理其他盘口改为关盘
                List<StandardMarketDataMessage> resultPlaceMarketDataMessages = placeMarketDataMessages.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                int num = 0;
                for (StandardMarketDataMessage placeMarket : resultPlaceMarketDataMessages) {
                    if (num == 0) {
                        if (placeMarket.getStatus().equals(Constant.SPORT_MARKET.STATUS.LOSE)) {
                            placeMarket.setPlaceNum(999);
                        }
                        standardMarketDataMessagesAUTO.add(placeMarket);
                        log.info("::{}::三方盘口ID:{},T01盘口相同坑位最新盘口", linkId, placeMarket.getThirdMarketSourceId());
                    }
                    num++;
                }
                if (isTrue) {
                    //缓存TX球头
                    StandardMarketDataMessage fistStandardMarketDataMessage = resultPlaceMarketDataMessages.get(0);
                    if (MarginCategoryConfig.CHANGE_FLAP1.contains(fistStandardMarketDataMessage.getMarketCategoryId())) {
                        String addition = StringUtils.isEmpty(fistStandardMarketDataMessage.getAddition5()) ? fistStandardMarketDataMessage.getAddition1() : fistStandardMarketDataMessage.getAddition5();
                        setBall(linkId, standardMatchInfo, fistStandardMarketDataMessage, addition);
                    }
                }
            }
        }
    }
    /**
     * 赛前构建滚球盘口
     * @param request
     */
    public void accessMatchLiveOddsData(@Valid Request<Long> request) {
        Long matchId = request.getData();
        String linkId = request.getLinkId();
        log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{}", linkId, JSONUtil.toJsonStr(request));
        String switchLiveRedisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + matchId;
        Object obj = redisService.get(switchLiveRedisKey);
        if (obj != null) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},已下发过滚球标识，直接返回", linkId, JSONUtil.toJsonStr(request));
            return;
        }
        //兼容冠军玩法，获取标准赛事信息
        StandardMatchInfoDetail standardMatchInfo = getStandardMatchInfo(false, matchId);
        if (standardMatchInfo == null) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},标准赛事不存在，直接返回", linkId, JSONUtil.toJsonStr(request));
            return;
        }
        //兼容冠军玩法，获取赛事开售信息
        StandardSportMarketSell standardSportMarketSell = getStandardSportMarketSell(false, matchId);
        if (null == standardSportMarketSell) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，赛事ID:{},标准赛事未开售，直接返回", linkId, matchId);
            return;
        }
        long dataSoureTime = System.currentTimeMillis();
        //最新下发的盘口赔率
        String lastMarketOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
        Map<String, List<StandardMarketMessage>> lastMarketOddsMap = redisService.hGetAll(lastMarketOddsKey);
        String liveRiskManagerCode = standardSportMarketSell.getLiveRiskManagerCode();
        Boolean isTrue = Boolean.TRUE;
        if (!RiskManagerCodeEnums.PA.name().equals(liveRiskManagerCode)) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},滚球为操作平台为非PA，直接返回", linkId, JSONUtil.toJsonStr(request));
            isTrue = Boolean.FALSE;
        }
        if (standardSportMarketSell.getLiveOddBusiness() == 0) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},不支持滚球，直接返回", linkId, JSONUtil.toJsonStr(request));
            isTrue = Boolean.FALSE;
        }
        if (StringUtils.isEmpty(standardSportMarketSell.getLiveMatchDataProviderCode())) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},未设置滚球数据服务商，直接返回", linkId, JSONUtil.toJsonStr(request));
            isTrue = Boolean.FALSE;
        }
        if (standardSportMarketSell.getLiveMatchSellStatus().equalsIgnoreCase("Unsold")) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},滚球未开售，不处理，直接返回", linkId, JSONUtil.toJsonStr(request));
            isTrue = Boolean.FALSE;
        }
        //第一步，关闭赛前盘  足球 && isTrue为 true 不关主玩法
        newClosePreMarkets(linkId, standardSportMarketSell, 0, standardMatchInfo, dataSoureTime, true,
                StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) && isTrue ? MarginCategoryConfig.FootBall_PRE_LIVE_CATEGORY : new ArrayList<>(), 0
        );
        if (!isTrue){
            return;
        }
        //第三步，找出需要下发的玩法早盘数据，构建滚球盘下发  全场让分/大小/独赢，上半让分/大小/独赢
        //获取玩法开售
        List<Long> list = new ArrayList<>();
        if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
            list.addAll(MarginCategoryConfig.FootBall_PRE_LIVE_CATEGORY);
        } else if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())) {
            String key = Constant.REDIS_KEY.RONGHE_BASKET_EARLY_CONFIG;
            Map<String,Boolean> map = redisService.hGetAll(key);
            StandardSportTournament standardSportTournament =
                    standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
            if (map == null || standardSportTournament==null){
                log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},联赛没有配置早转滚，直接返回", linkId, JSONUtil.toJsonStr(request));
                return;
            }
            boolean tournamentStatus = map.getOrDefault(standardSportTournament.getTournamentLevel().toString(),Boolean.FALSE);
            if (!tournamentStatus){
                log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},联赛配置早转滚为关，直接返回", linkId, JSONUtil.toJsonStr(request));
                return;
            }
            list.addAll(MarginCategoryConfig.Basketball_PRE_LIVE_CATEGORY);
        } else {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},不支持运动类型，直接返回", linkId, JSONUtil.toJsonStr(request));
            return;
        }
        List<MarketCategorySell> marketCategorySells = marketCategorySellService.getItem(matchId, list);
        if (CollectionUtils.isEmpty(marketCategorySells)) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},赛前或者滚球玩法开售信息为空，直接返回", linkId, JSONUtil.toJsonStr(request));
            return;
        }
        //赛前、滚球 盘口类型分组
        Map<String, List<MarketCategorySell>> marketCategorySellGroup = marketCategorySells.stream().collect(Collectors.groupingBy(MarketCategorySell::getMarketType));
        List<MarketCategorySell> liveMarketCategorySell = marketCategorySellGroup.get("0");
        if (liveMarketCategorySell == null) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},滚球玩法开售信息为空，直接返回", linkId, JSONUtil.toJsonStr(request));
            return;
        }
        //滚球玩法分组，用于替换建滚球盘口数据源为滚球数据源
        Map<Long, MarketCategorySell> liveMarketCategorySellGroup = liveMarketCategorySell.stream().collect(Collectors.toMap(MarketCategorySell::getMarketCategoryId, a -> a, (k1, k2) -> k1));
        //赛前玩法构建滚球数据
        List<MarketCategorySell> preMarketCategorySell = marketCategorySellGroup.get("1");
        if (CollectionUtils.isEmpty(preMarketCategorySell)) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息，request:{},赛前开售信息为空，直接返回", linkId, JSONUtil.toJsonStr(request));
            return;
        }
        List<ThirdSportMarket> thirdSportMarketArrayList = new ArrayList<>();
        for (MarketCategorySell m : preMarketCategorySell) {
            //滚球没有开售不处理
            if (liveMarketCategorySellGroup.get(m.getMarketCategoryId()) == null) {
                log.info("::{}:: 收到自动构建滚球赔率MQ消息，滚球玩法没有开售不处理，标准赛事ID:{},玩法数据源：{}，赛前玩法:{}",
                        linkId, matchId, m.getDataSourceCode(), m.getMarketCategoryId());
                continue;
            }
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(matchId, m.getDataSourceCode());
            if (thirdMatchInfo == null) {
                log.info("::{}::收到自动构建滚球赔率MQ消息,三方赛事映射不存在,跳过,标准赛事id:{},数据源:{},玩法:{}", linkId, matchId, m.getDataSourceCode(), m.getMarketCategoryId());
                continue;
            }
            //主客队相反,玩法过滤处理
            if (ONE.equals(thirdMatchInfo.getHomeAwayOpposite()) && thirdMatchInfo.getSportId().equals(1L)) {
                if (!CategoryOppositeConfig.FootBall.containsCategory(m.getMarketCategoryId())) {
                    log.info("::{}::，主客队相反需求过滤部分玩法，主客相反需求不包含该玩法：{}", linkId, m.getMarketCategoryId());
                    continue;
                }
            }
            List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketService.getItemList(thirdMatchInfo.getId(), m.getDataSourceCode(), m.getMarketCategoryId());
            if (CollectionUtils.isEmpty(thirdSportMarkets)) {
                //测试联赛
                StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItemByNewId(matchId);
                if (null == standardRelationNewStandard) {
                    log.info("::{}:: 收到自动构建滚球赔率MQ消息，测试联赛对应标准赛事不存在，标准赛事ID:{}", linkId, matchId);
                    continue;
                }
                Long sourceStandardId = standardRelationNewStandard.getSourceStandardId();
                thirdMatchInfo = thirdMatchInfoService.getItem(sourceStandardId, m.getDataSourceCode());
                if (null == thirdMatchInfo) {
                    log.info("::{}:: 收到自动构建滚球赔率MQ消息，测试联赛对应标准赛事的三方赛事不存在，标准赛事ID:{}", linkId, sourceStandardId);
                    continue;
                }
                thirdSportMarkets = thirdSportMarketService.getItemList(thirdMatchInfo.getId(), m.getDataSourceCode(), m.getMarketCategoryId());
                if (CollectionUtils.isEmpty(thirdSportMarkets)) {
                    log.info("::{}:: 收到自动构建滚球赔率MQ消息，三方玩法赔率为空，标准赛事ID:{},三方赛事id:{},玩法数据源：{}，玩法:{}",
                            linkId, matchId, thirdMatchInfo.getId(), m.getDataSourceCode(), m.getMarketCategoryId());
                    continue;
                }
            }
            for (ThirdSportMarket thirdSportMarket : thirdSportMarkets) {
                //thirdSportMarket.setThirdMarketSourceStatus(0);
                //thirdSportMarket.setStatus(0);
                thirdSportMarket.setMarketType(0);
            }
            thirdSportMarketArrayList.addAll(thirdSportMarkets);
        }
        if (CollectionUtils.isEmpty(lastMarketOddsMap)) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息,标准赛事ID:{},没有最新下发赔率。", linkId, matchId);
            return;
        }
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap = processMarketBySold(linkId, standardMatchInfo, thirdSportMarketArrayList);
        if (CollectionUtils.isEmpty(standardMarketDataMessageMap)) {
            log.info("::{}:: 收到自动构建滚球赔率MQ消息,标准赛事ID:{},standardMarketMessageMap is null", linkId, matchId);
            return;
        }
        //对缓存所有数据进行排序
        Set<Long> set = new HashSet(list);
        marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketDataMessageMap, standardMatchInfo, set, null, null, null, false);
        Map<String, List<StandardMarketDataMessage>> stringStandardMarketDataMessageMap = standardMarketDataMessageMap.values().stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getDataSourceCode));
        Map<String, StandardMarketDataMessage> newTemp = new HashMap<>();
        stringStandardMarketDataMessageMap.forEach((k, v) -> {
            Map<String, StandardMarketDataMessage> temp = v.stream().collect(Collectors.toMap(e -> e.getRelationMarketId().toString(), e -> e, (oldValue, newValue) -> newValue));
            //替换赛前玩法数据源为滚球数据源
            temp.values().forEach(e -> {
                MarketCategorySell marketCategorySell = liveMarketCategorySellGroup.get(e.getMarketCategoryId());
                //删除标准赔率缓存，不删除会有缓存和数据源不匹配
                String redisKey1 = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + e.getDataSourceCode() + "_" + e.getMarketCategoryId());
                redisService.hDel(redisKey1, e.getRelationMarketId().toString());
                e.setDataSourceCode(marketCategorySell.getDataSourceCode());
                //设置主客队得分
                if (e.getMarketCategoryId() == 4L || e.getMarketCategoryId() == 19L) {
                    e.setAddition3("0");
                    e.setAddition4("0");
                }
                //TX盘口ID需要特殊处理  //TX生成统一盘口ID特殊处理 盘口值规则生成赋值:SendData ,三方数据源盘口ID生成赋值:RelationMarketId
                StandardSportMarket standardSportMarket = new StandardSportMarket();
                BeanUtils.copyProperties(e, standardSportMarket);
                //标准投注项ID
                Long relationMarketId = e.getRelationMarketId();
                if (DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode())) {
                    e.setRelationMarketId(Long.valueOf(standardSportMarketService.txCreateRelationMarketId(e.getThirdMarketSourceId())));
                    e.setSendData(standardSportMarketService.createRelationMarketId(linkId, standardSportMarket).toString());
                    standardSportMarket.setSendData(e.getSendData());
                    relationMarketId = Long.valueOf(e.getSendData());
                } else {
                    e.setRelationMarketId(standardSportMarketService.createRelationMarketId(linkId, standardSportMarket));
                }
                List<StandardMarketOddsDataMessage> marketOddsList = e.getMarketOddsList();
                if (!CollectionUtils.isEmpty(marketOddsList)) {
                    marketOddsList.forEach(o -> {
                        o.setRelationMarketId(e.getRelationMarketId());
                        StandardSportMarketOdds marketOdds = new StandardSportMarketOdds();
                        BeanUtils.copyProperties(o, marketOdds);
                        o.setRelationMarketOddsId(standardSportMarketOddsService.createRelationMarketOddsId(marketOdds, standardSportMarket));
                        o.setActive(Constant.TRADE_MARKET_CONFIG.ACTIVE.ACTIVE);
                        if (DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode())) {
                            o.setRemark(marketOdds.getRemark());
                        }
                    });
                } else {
                    e.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }
                //转换赛前盘口最后一批盘口为滚球
                List<StandardMarketMessage> standardMarketMessages = lastMarketOddsMap.get(e.getMarketCategoryId().toString());
                if (CollectionUtils.isEmpty(standardMarketMessages)) {
                    log.info("::{}::构建滚球盘,该玩法无最新下发赔率,跳过,标准赛事id:{},玩法:{}", linkId, standardMatchInfo.getId(), e.getMarketCategoryId());
                    return;
                }
                Map<Long, StandardMarketMessage> standardMarketMessagesGroup = standardMarketMessages.stream().collect(Collectors.toMap(StandardMarketMessage::getId, a -> a, (k1, k2) -> k1));
                StandardMarketMessage standardMarketMessage = standardMarketMessagesGroup.get(relationMarketId);
                if (null != standardMarketMessage) {
                    //赛前转滚球设置内部数据源
                    String key = Constant.REDIS_KEY.THIRD_MATCH_WITCH_DATA_SOURCE_KEY + standardMatchInfo.getId() + "_" + e.getDataSourceCode();
                    Object o = redisService.get(key);
                    log.info("::{}::赛前转滚球设置内部数据源:{},obj:{}",linkId,key,o);
                    e.setInternalDataSourceCode(null == o ? null : o.toString());
                    //缓存标准盘口缓存
                    String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + marketCategorySell.getDataSourceCode() + "_" + e.getMarketCategoryId());
                    redisService.hSet(redisKey, e.getRelationMarketId().toString(), e, marketCacheTime(standardMatchInfo.getBeginTime()));
                    log.info("::{}:: 收到自动构建滚球赔率MQ消息,刷新缓存盘口信息,标准赛事ID:{} key={},marketIdList={},result={}", linkId, matchId, redisKey, set, e);
                    newTemp.put(e.getRelationMarketId().toString(), e);
                } else {
                    log.info("::{}::构建滚球盘,最后赔率未匹配统一盘口,跳过,标准赛事id:{},玩法:{},盘口:{}", linkId, standardMatchInfo.getId(), e.getMarketCategoryId(), relationMarketId);
                }
            });
        });
        Set<Long> builtCats = newTemp.values().stream().map(StandardMarketDataMessage::getMarketCategoryId).collect(Collectors.toSet());
        Set<Long> missingCats = new HashSet<>(list); missingCats.removeAll(builtCats);
        log.info("::{}::构建滚球盘完整性核对,标准赛事id:{},候选数:{},建成数:{},缺失玩法:{}", linkId, matchId, list.size(), builtCats.size(), missingCats);
        if (MapUtils.isNotEmpty(newTemp)) {
            //盘口缓存到构建盘口缓存，用于玩法切换
            String redisConvertKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CONVERT_MARKET + standardMatchInfo.getId());
            redisService.hSetAll(redisConvertKey, newTemp, marketCacheTime(standardMatchInfo.getBeginTime()));
        }
        //下发当前最新赔率
        processOddsByAll(linkId,-1,null, standardMatchInfo, set, newTemp, dataSoureTime, standardSportMarketSell, new HashMap<>());
    }

    /**
     * 处理赔率数据
     *
     * @param linkId
     * @param standardMatchInfo
     * @param thirdSportMarketList
     */
    public Map<String, StandardMarketDataMessage> processMarketBySold(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarket> thirdSportMarketList) {
        Map<String, StandardMarketDataMessage> map = new HashMap<>();
        thirdSportMarketList.forEach(thirdSportMarket -> {
            //查询标准盘口数据
            StandardSportMarket standardSportMarket = standardSportMarketService.getItem(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getThirdMarketSourceId(), standardMatchInfo.getId());
            StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryService.getItem(thirdSportMarket.getMarketCategoryId(), standardMatchInfo.getSportId());
            if (standardSportMarketCategory == null || standardSportMarketCategory.getStatus() == 0) {
                log.info("::{}::标准赛种玩法不存在,标准玩法id={}, 赛种={}", linkId, thirdSportMarket.getMarketCategoryId(), standardMatchInfo.getSportId());
                return;
            }
            //处理标准盘口，不存在新增，存在更新
            if (standardSportMarket == null) {
                standardSportMarket = standardSportMarketService.create(linkId, standardMatchInfo, thirdSportMarket, standardSportMarketCategory.getScopeId());
                if (standardSportMarket == null) {
                    log.info("::{}::标准盘口创建失败,标准赛事id={},三方盘口={},三方盘口状态={}", linkId, standardMatchInfo.getId(), thirdSportMarket.getThirdMarketSourceId(), thirdSportMarket.getStatus());
                    return;
                }
                if (standardSportMarket.getMarketType() == 2) {
//                if (DataSourceCodeEnum.PA.name().equals(standardSportMarket.getDataSourceCode())) {
                    //通知风控进行操盘方式的初始化
                    standardMarketOddsProducer.toInitTradeType(linkId, standardSportMarket );
//                }
                }
            } else {
                standardSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                standardSportMarket.setStatus(thirdSportMarket.getStatus());
                standardSportMarket.setThirdMarketSourceStatus(thirdSportMarket.getStatus());
                standardSportMarket.setMarketType(thirdSportMarket.getMarketType());
                standardSportMarket.setLinkId(linkId);
                standardSportMarket.setOddsName(thirdSportMarket.getOddsName());
                standardSportMarket.setAddition1(thirdSportMarket.getAddition1());
                standardSportMarket.setAddition2(thirdSportMarket.getAddition2());
                standardSportMarket.setAddition3(thirdSportMarket.getAddition3());
                standardSportMarket.setAddition4(thirdSportMarket.getAddition4());
                standardSportMarket.setNumberOfWinners(thirdSportMarket.getNumberOfWinners());

                //异步执行更新
                standardSportMarketService.updateByPrimaryKeySelective(standardSportMarket);
                //updateOperateProxy.updateStandardSportMarket(standardSportMarket,linkId);
            }
            //处理标准盘口投注项
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketOddsService.getItemList(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getId());
            List<StandardSportMarketOdds> standardSportMarketOddsList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                    //查询标准盘口投注项信息是否存在，不存在新增，存在更新
                    StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsService.getItem(thirdSportMarket.getDataSourceCode(), thirdSportMarketOdds.getThirdOddsFieldSourceId(), standardSportMarket.getId());
                    //获取三方玩法投注项模板
                    ThirdMarketCategoryField thirdMarketCategoryField = thirdMarketCategoryFieldService.getItem(thirdSportMarketOdds.getOddsFieldsTemplateId(), thirdSportMarketOdds.getThirdTemplateSourceId());
                    //获取三方玩法投注项模板
                    if (thirdMarketCategoryField == null) {
                        log.info("::{}::三方投注项模板为空，融合三方投注项模板id={}", linkId, thirdSportMarketOdds.getOddsFieldsTemplateId());
                        break;
                    }
                    //TODO 校验盘口投注项是否满足标准玩法投注项条件
                    if (standardSportMarketOdds == null) {
                        //生成并保存标准投注项
                        standardSportMarketOdds = new StandardSportMarketOdds();
                        BeanUtils.copyProperties(thirdSportMarketOdds, standardSportMarketOdds);
                        standardSportMarketOdds.setId(UUIdUtils.getId());
                        standardSportMarketOdds.setMarketId(standardSportMarket.getId());
                        standardSportMarketOdds.setRelationMarketId(standardSportMarket.getRelationMarketId());
                        standardSportMarketOdds.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        standardSportMarketOdds.setRelationMarketOddsId(standardSportMarketOddsService.createRelationMarketOddsId(standardSportMarketOdds, standardSportMarket));
                        standardSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        standardSportMarketOdds.setOddsFieldsTemplateId(thirdMarketCategoryField.getReferenceId());
                        standardSportMarketOdds.setThirdTemplateSourceId(thirdMarketCategoryField.getThirdSourceId());
                        standardSportMarketOdds.setStandardMatchId(standardSportMarket.getStandardMatchInfoId());
                        standardSportMarketOdds.setNameCode(standardSportMarketOdds.getId());
                        //转换标准球队id
                        standardSportMarketOddsService.convertStandardTeam(linkId, standardSportMarketOdds, standardSportMarket);
                        standardSportMarketOddsService.create(linkId, standardSportMarketOdds);
                        if (standardSportMarket.getMarketType() == 2 && null != thirdSportMarketOdds.getNameCode()) {
                            List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList = i18nOutrightMarketOddsService.selectI18nOutRightMarketOddsList(thirdSportMarketOdds.getDataSourceCode(), thirdSportMarketOdds.getNameCode());
                            if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsList)) {
                                StandardSportMarketOdds finalStandardSportMarketOdds = standardSportMarketOdds;
                                i18nOutrightMarketOddsList.forEach(i18nOutrightMarket -> {
                                    i18nOutrightMarket.setNameCode(finalStandardSportMarketOdds.getNameCode());
                                });
                                i18nOutrightMarketOddsService.saveBatch(i18nOutrightMarketOddsList);
                                standardMarketOddsProducer.marketOddsNameI18nSend(linkId, i18nOutrightMarketOddsList,
                                                                                  standardMatchInfo.getId());
                            } else {
                                log.info("::{}::processMarketBySold冠军标准盘口开售create,盘口id:{},三方nameCode:{},国际化信息为空", linkId, standardSportMarket.getRelationMarketId(), thirdSportMarket.getNameCode());
                            }
                        }
                    } else {
                        standardSportMarketOdds.setActive(thirdSportMarketOdds.getActive());
                        standardSportMarketOdds.setOddsValue(thirdSportMarketOdds.getOddsValue());
                        standardSportMarketOdds.setOriginalOddsValue(thirdSportMarketOdds.getOriginalOddsValue());
                        standardSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        if (standardSportMarket.getMarketType() != 2) {
                            // 更新附加字段
                            standardSportMarketOdds.setAddition1(thirdSportMarketOdds.getAddition1());
                            standardSportMarketOdds.setAddition2(thirdSportMarketOdds.getAddition2());
                            standardSportMarketOdds.setAddition3(thirdSportMarketOdds.getAddition3());
                            standardSportMarketOdds.setAddition4(thirdSportMarketOdds.getAddition4());
                            standardSportMarketOdds.setAddition5(thirdSportMarketOdds.getAddition5());
                            //标准球队转换
                            standardSportMarketOddsService.convertStandardTeam(linkId, standardSportMarketOdds, standardSportMarket);
                        }
                        //异步执行更新
                        standardSportMarketOddsService.updateByPrimaryKeySelective(standardSportMarketOdds);
                        //updateOperateProxy.updateStandardSportMarketOdds(standardSportMarketOdds,linkId);
                    }
                    standardSportMarketOddsList.add(standardSportMarketOdds);
                }
            }
            //新增玩法投注项排序
            if (!CollectionUtils.isEmpty(standardSportMarketOddsList) && MarginCategoryConfig.ODDS_ORDER.contains(standardSportMarket.getMarketCategoryId()))
            {
                oddsOrderByOddsType(standardSportMarketOddsList,standardSportMarket.getMarketCategoryId());
                for (int i = 0;i<standardSportMarketOddsList.size();i++)
                {
                    standardSportMarketOddsList.get(i).setOrderOdds(i+1);
                }
            }
            //将盘口及盘口投注项封装到一起
            StandardMarketDataMessage standardMarketDataMessage = convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket,TimeUtils.millsSecondsEast8ZoneGmt()-10*1000);
            standardMarketDataMessage.setMarketSource(1);


            //将最新盘口刷入缓存
            String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdSportMarket.getDataSourceCode()+"_"+thirdSportMarket.getMarketCategoryId());
            log.info("::{}::准备缓存赔率信息，key={}, hashKey={}, hashValue={}", linkId, marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage);
            if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getStatus())) {
                standardMarketDataMessage.setShowMarketResult(1);
            }
            if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarketDataMessage.getDataSourceCode())) {
                standardMarketDataMessage.setPlaceNum(null == thirdSportMarket.getOfferLineId() ? 999 : thirdSportMarket.getOfferLineId());
            }
            map.put(String.valueOf(standardMarketDataMessage.getRelationMarketId()), standardMarketDataMessage);
//          redisService.hSet(marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
            String marketKey1 = Constant.REDIS_KEY.RONGHE_ORDER_STANDARD_MARKET + standardMatchInfo.getId();
            redisService.hSet(marketKey1, standardMarketDataMessage.getMarketCategoryId().toString(), 1, marketCacheTime(standardMatchInfo.getBeginTime()));
        });
        return map;
    }

    /**
     * 主客队相反：盘口、投注项内容替换
     * @param linkId
     * @param dataSourceCode
     * @param thirdMarketCategory
     * @param thirdMarketDTO
     */
    public void changeStandardMarketContent(String linkId, String dataSourceCode, ThirdMarketCategory thirdMarketCategory, ThirdMarketDTO thirdMarketDTO) {
        Long standardCategoryId = thirdMarketCategory.getReferenceId();
        log.info("::{}::changeStandardMarketContent盘口信息, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, standardCategoryId,
                thirdMarketDTO.getAddition1(), thirdMarketDTO.getAddition2(),thirdMarketDTO.getAddition3(),thirdMarketDTO.getAddition4());
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(standardCategoryId))
        {
            Long newCategoryId = CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.get(thirdMarketCategory.getReferenceId());
            List<ThirdMarketCategory> marketCategoryList = thirdMarketCategoryService.getItem(dataSourceCode, newCategoryId);
            if(!CollectionUtils.isEmpty(marketCategoryList)){
                BeanUtils.copyProperties(marketCategoryList.get(0),thirdMarketCategory);
                if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList()))
                {
                    List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldService.queryThirdMarketCategoryFieldDetail(dataSourceCode, thirdMarketCategory.getReferenceId());
                    if(!CollectionUtils.isEmpty(thirdMarketCategoryFieldDetails)){
                        Map<String, String> stringMap = thirdMarketCategoryFieldDetails.stream().collect(
                                Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getThirdSourceId,(o,n)->n));
                        for (ThirdMarketOddsDTO thirdMarketOddsDTO: thirdMarketDTO.getMarketOddsList()) {
                            thirdMarketOddsDTO.setThirdTempletSourceId(stringMap.get(thirdMarketOddsDTO.getOddsType().toLowerCase()));
                        }
                    }
                }
            }
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_2.contains(standardCategoryId))
        {
            String add1 = thirdMarketDTO.getAddition1().contains("-")?thirdMarketDTO.getAddition1().replace("-",""):"-"+thirdMarketDTO.getAddition1();
            thirdMarketDTO.setAddition1(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_3.contains(standardCategoryId))
        {
            String add2 = thirdMarketDTO.getAddition2().contains("-")?thirdMarketDTO.getAddition2().replace("-",""):"-"+thirdMarketDTO.getAddition2();
            thirdMarketDTO.setAddition2(add2);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_4.contains(standardCategoryId))
        {
            String add3 = thirdMarketDTO.getAddition3();
            String add4 = thirdMarketDTO.getAddition4();
            thirdMarketDTO.setAddition3(add4);
            thirdMarketDTO.setAddition4(add3);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_6.contains(standardCategoryId))
        {
            String add1 = thirdMarketDTO.getAddition1();
            String add2 = thirdMarketDTO.getAddition2();
            thirdMarketDTO.setAddition1(add2);
            thirdMarketDTO.setAddition2(add1);
        }
        if(CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())){
           return;
        }
        Map<String,String> thirdTemplateSourceIdMap = new HashMap<>();
        Map<String,List<I18nItemDTO>> i18Map = new HashMap<>();
        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList())
        {
            thirdTemplateSourceIdMap.put(thirdMarketOddsDTO.getOddsType(),thirdMarketOddsDTO.getThirdTempletSourceId());
            i18Map.put(thirdMarketOddsDTO.getOddsType(), thirdMarketOddsDTO.getI18nNames());
        }
        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList())
        {
            /*log.info("::{}::changeStandardMarketContent投注项信息,标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, standardCategoryId,
                    thirdMarketOddsDTO.getAddition1(), thirdMarketOddsDTO.getAddition2(),thirdMarketOddsDTO.getAddition3(),thirdMarketOddsDTO.getAddition4());*/
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_7.contains(standardCategoryId))
            {
                String add1 = thirdMarketOddsDTO.getAddition1();
                String add2 = thirdMarketOddsDTO.getAddition2();
                thirdMarketOddsDTO.setAddition1(add2);
                thirdMarketOddsDTO.setAddition2(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_8.contains(standardCategoryId))
            {
                String add3 = thirdMarketOddsDTO.getAddition3();
                String add4 = thirdMarketOddsDTO.getAddition4();
                thirdMarketOddsDTO.setAddition3(add4);
                thirdMarketOddsDTO.setAddition4(add3);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_5.contains(standardCategoryId))
            {
                if (standardCategoryId == 104L)
                {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.containsKey(thirdMarketOddsDTO.getOddsType()))
                    {
                        String oddsType = thirdMarketOddsDTO.getOddsType();
                        thirdMarketOddsDTO.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.get(oddsType));
                        thirdMarketOddsDTO.setThirdTempletSourceId(thirdTemplateSourceIdMap.get(thirdMarketOddsDTO.getOddsType()));
                        thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                    }
                }
                else if (standardCategoryId == 103L)
                {
                    String str1 = (thirdMarketOddsDTO.getAddition1() == null||thirdMarketOddsDTO.getAddition1().contains("+"))?thirdMarketOddsDTO.getAddition1():thirdMarketOddsDTO.getAddition1() + ":" + thirdMarketOddsDTO.getAddition2();
                    String str2 = (thirdMarketOddsDTO.getAddition3() == null||thirdMarketOddsDTO.getAddition3().contains("+"))?thirdMarketOddsDTO.getAddition3():thirdMarketOddsDTO.getAddition3()+":"+thirdMarketOddsDTO.getAddition4();
                    thirdMarketOddsDTO.setOddsType(str1 + " " + str2);
                    thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                }
                else
                {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.containsKey(thirdMarketOddsDTO.getOddsType()))
                    {
                        String oddsType = thirdMarketOddsDTO.getOddsType();
                        thirdMarketOddsDTO.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.get(oddsType));
                        thirdMarketOddsDTO.setThirdTempletSourceId(thirdTemplateSourceIdMap.get(thirdMarketOddsDTO.getOddsType()));
                        thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                    }
                    else
                    {
                        if (thirdMarketOddsDTO.getOddsType().contains(":"))
                        {
                            String[] strArr = thirdMarketOddsDTO.getOddsType().split(":");
                            if (strArr.length == 2)
                            {
                                thirdMarketOddsDTO.setOddsType(strArr[1]+":"+strArr[0]);
                                thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                            }
                        }
                    }
                }
            }
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_10.contains(thirdMarketDTO.getMarketCategoryId())) {
            String add3 = thirdMarketDTO.getAddition3();
            String add4 = thirdMarketDTO.getAddition4();
            thirdMarketDTO.setAddition3(add4);
            thirdMarketDTO.setAddition4(add3);
        }
    }

    /**
     * A01(AO)与主数据源一致，接入侧已处理主客相反，赔率服务主流程不再重复翻转。
     */
    public boolean skipHomeAwayOppositeForDataSource(String dataSourceCode) {
        return super.skipHomeAwayOppositeForDataSource(dataSourceCode);
    }

    /**
     * 主客相反三方盘口内容替换
     * @param linkId
     * @param thirdSportMarket
     */
    public void changeThirdMarketContent(String linkId, ThirdSportMarket thirdSportMarket)
    {
            log.info("::{}::changeThirdMarketContent, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, thirdSportMarket.getMarketCategoryId(),
                    thirdSportMarket.getAddition1(), thirdSportMarket.getAddition2(),thirdSportMarket.getAddition3(),thirdSportMarket.getAddition4());
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(thirdSportMarket.getMarketCategoryId()))
            {
                thirdSportMarket.setMarketCategoryId(CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.get(thirdSportMarket.getMarketCategoryId()));
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_2.contains(thirdSportMarket.getMarketCategoryId()))
            {
                String add1 = thirdSportMarket.getAddition1().contains("-")?thirdSportMarket.getAddition1().replace("-",""):"-"+thirdSportMarket.getAddition1();
                thirdSportMarket.setAddition1(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_3.contains(thirdSportMarket.getMarketCategoryId()))
            {
                String add2 = thirdSportMarket.getAddition2().contains("-")?thirdSportMarket.getAddition2().replace("-",""):"-"+thirdSportMarket.getAddition2();
                thirdSportMarket.setAddition2(add2);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_4.contains(thirdSportMarket.getMarketCategoryId()))
            {
                String add3 = thirdSportMarket.getAddition3();
                String add4 = thirdSportMarket.getAddition4();
                thirdSportMarket.setAddition3(add4);
                thirdSportMarket.setAddition4(add3);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_6.contains(thirdSportMarket.getMarketCategoryId()))
            {
                String add1 = thirdSportMarket.getAddition1();
                String add2 = thirdSportMarket.getAddition2();
                thirdSportMarket.setAddition1(add2);
                thirdSportMarket.setAddition2(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_10.contains(thirdSportMarket.getMarketCategoryId())) {
                String add3 = thirdSportMarket.getAddition3();
                String add4 = thirdSportMarket.getAddition4();
                thirdSportMarket.setAddition3(add4);
                thirdSportMarket.setAddition4(add3);
            }
    }

    /**
     * 主客相反改变投注项内容
     * @param linkId
     * @param thirdSportMarketOddsList
     * @param thirdSportMarket
     */
    public void changeThirdMarketOddsContent(String linkId, List<ThirdSportMarketOdds> thirdSportMarketOddsList, ThirdSportMarket thirdSportMarket)
    {
        if (CollectionUtils.isEmpty(thirdSportMarketOddsList))
        {
            log.info("::{}:: changeThirdMarketOddsContent fail, thirdSportMarketOddsList is Empty", linkId);
            return;
        }
        Map<String,Long> oddsFieldTemplateMap = new HashMap<>();
        Map<String,String> thirdTemplateSourceIdMap = new HashMap<>();
        Map<String,String> oddsNameMap = new HashMap<>();
        for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList)
        {
            thirdTemplateSourceIdMap.put(thirdSportMarketOdds.getOddsType(),thirdSportMarketOdds.getThirdTemplateSourceId());
            oddsNameMap.put(thirdSportMarketOdds.getOddsType(),thirdSportMarketOdds.getName());
            oddsFieldTemplateMap.put(thirdSportMarketOdds.getOddsType(),thirdSportMarketOdds.getOddsFieldsTemplateId());
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(thirdSportMarket.getMarketCategoryId()))
        {
            List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldService.queryThirdMarketCategoryFieldDetail(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getMarketCategoryId());
            if(!CollectionUtils.isEmpty(thirdMarketCategoryFieldDetails)){
                Map<String, Long> longMap = thirdMarketCategoryFieldDetails.stream().collect(
                        Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getReferenceId));
                Map<String, String> stringMap = thirdMarketCategoryFieldDetails.stream().collect(
                        Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getThirdSourceId));
                for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                    thirdSportMarketOdds.setOddsFieldsTemplateId(longMap.get(thirdSportMarketOdds.getOddsType().toLowerCase()));
                    thirdSportMarketOdds.setThirdTemplateSourceId(stringMap.get(thirdSportMarketOdds.getOddsType().toLowerCase()));
                }
            }
        }
        for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList)
        {
            /*log.info("::{}::changeThirdMarketContent, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, thirdSportMarket.getMarketCategoryId(),
                    thirdSportMarketOdds.getAddition1(),thirdSportMarketOdds.getAddition2(),thirdSportMarketOdds.getAddition3(),thirdSportMarketOdds.getAddition4());*/
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_7.contains(thirdSportMarket.getMarketCategoryId()))
            {
                String add1 = thirdSportMarketOdds.getAddition1();
                String add2 = thirdSportMarketOdds.getAddition2();
                thirdSportMarketOdds.setAddition1(add2);
                thirdSportMarketOdds.setAddition2(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_8.contains(thirdSportMarket.getMarketCategoryId()))
            {
                String add3 = thirdSportMarketOdds.getAddition3();
                String add4 = thirdSportMarketOdds.getAddition4();
                thirdSportMarketOdds.setAddition3(add4);
                thirdSportMarketOdds.setAddition4(add3);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_5.contains(thirdSportMarket.getMarketCategoryId()))
            {
                if (thirdSportMarket.getMarketCategoryId() == 104L)
                {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.containsKey(thirdSportMarketOdds.getOddsType()))
                    {
                        String oddsType = thirdSportMarketOdds.getOddsType();
                        thirdSportMarketOdds.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.get(oddsType));
                        thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                    }
                }
                else if (thirdSportMarket.getMarketCategoryId() == 103L)
                {
                    String str1 = (thirdSportMarketOdds.getAddition1() == null||thirdSportMarketOdds.getAddition1().contains("+"))?thirdSportMarketOdds.getAddition1():thirdSportMarketOdds.getAddition1() + ":" + thirdSportMarketOdds.getAddition2();
                    String str2 = (thirdSportMarketOdds.getAddition3() == null||thirdSportMarketOdds.getAddition3().contains("+"))?thirdSportMarketOdds.getAddition3():thirdSportMarketOdds.getAddition3()+":"+thirdSportMarketOdds.getAddition4();
                    thirdSportMarketOdds.setOddsType(str1 + " " + str2);
                    thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                    thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                    thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                }
                else
                {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.containsKey(thirdSportMarketOdds.getOddsType()))
                    {
                        String oddsType = thirdSportMarketOdds.getOddsType();
                        thirdSportMarketOdds.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.get(oddsType));
                        thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                        thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                    }
                    else
                    {
                        if (thirdSportMarketOdds.getOddsType().contains(":"))
                        {
                            String[] strArr = thirdSportMarketOdds.getOddsType().split(":");
                            if (strArr.length == 2)
                            {
                                thirdSportMarketOdds.setOddsType(strArr[1]+":"+strArr[0]);
                                thirdSportMarketOdds.setName(oddsNameMap.get(thirdSportMarketOdds.getOddsType()));
                                thirdSportMarketOdds.setOddsFieldsTemplateId(oddsFieldTemplateMap.get(thirdSportMarketOdds.getOddsType()));
                                thirdSportMarketOdds.setThirdTemplateSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                            }
                        }
                    }
                }
            }
        }
    }
    private void checkIsTestMatch(String linkId,StandardMatchInfo standardMatchInfo,ThirdMatchMarketDTO thirdMatchMarketDTO,Request<ThirdMatchMarketDTO> request,List<ThirdMarketDTO> thirdMarketDTOList)
    {
        //测试联赛不用处理AO数据
        if(DataSourceCodeEnum.AO.code.equalsIgnoreCase(thirdMatchMarketDTO.getDataSourceCode()))
        {
            log.info("::{}::测试联赛，接收数据源赔率开始,赔率数据元素AO，直接不处理", linkId);
            return;
        }
        if (null == standardMatchInfo)
        {
            return;
        }
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItem(standardMatchInfo.getId());
        if (null == standardRelationNewStandard)
        {
            return;
        }
        else
        {
            //对测试联赛加锁
            String lockValue = UUIdUtils.getId()+"_"+linkId+"_new_match";
            boolean isLock = false;
            try{
                String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardRelationNewStandard.getNewStandardId();
                isLock = true;
                redisService.tryLock(redisLocKey, lockValue, 5, 3);
                testMatchHandler(linkId+"_new_match",standardRelationNewStandard,standardMatchInfo,request,thirdMarketDTOList);
            }finally {
                if (isLock)
                {
                    String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardRelationNewStandard.getNewStandardId();
                    redisService.unLock(redisLocKey,lockValue);
                }
            }
        }
    }
    private void testMatchHandler(String linkId,StandardRelationNewStandard standardRelationNewStandard,StandardMatchInfo standardMatchInfo1,Request<ThirdMatchMarketDTO> request,List<ThirdMarketDTO> thirdMarketDTOList)
    {
        if (CollectionUtils.isEmpty(thirdMarketDTOList))
        {
            return;
        }

        ThirdMatchMarketDTO thirdMatchMarketDTO = request.getData();
        String dataSourceCode = thirdMatchMarketDTO.getDataSourceCode();
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        Integer marketType = thirdMatchMarketDTO.getMarketList().get(0).getMarketType();

        //判断冠军玩法
        boolean isOutRight = Arrays.asList(MarginCategoryConfig.THIRD_OUTRIGHT_CATEGORY).contains(thirdMatchMarketDTO.getMarketList().get(0).getThirdMarketCategorySourceId());
        //兼容冠军玩法，获取标准赛事信息
        StandardMatchInfoDetail standardMatchInfo = getStandardMatchInfo(isOutRight, standardRelationNewStandard.getNewStandardId());
        log.info("::{}::测试联赛，接收数据源赔率开始,三方赛事id:{}", linkId, thirdMatchSourceId);
        //兼容冠军玩法，获取三方赛事信息
        ThirdMatchInfo thirdMatchInfo = getThirdMatchInfo(isOutRight, dataSourceCode, thirdMatchSourceId);
        if (thirdMatchInfo == null) {
            log.error("::{}::测试联赛，三方赛事不存在,三方数据源id:{},冠军玩法:{}", linkId, thirdMatchSourceId, isOutRight);
            return;
        }
        StandardSportMarketSell standardSportMarketSell = getStandardSportMarketSell(isOutRight,standardMatchInfo.getId());

        //存储当前数据里面的盘口id
        Set<Long> marketIdSet = new HashSet();
        //存储当前数据里面的玩法
        Set<Long> marketCategoryIdSet = new HashSet();
        //存储需要缓存的盘口数据
        List<StandardSportMarket> standardSportMarketList = new ArrayList<>();
        List<StandardMarketDataMessage> standardMarketDataMessageList = new ArrayList<>();
        Map<String, StandardMarketDataMessage> standardMarketMessageMap = new HashMap<>();
        //存储需要校验数据源挡板的投注项集合
        Set<Long> oddsTypeIdSet = new HashSet();
        //存储需要校验报警的玩法集合
        Set<Long> categorySet = new HashSet();
        //最终需要推送给风控的报警的玩法集合
        Set<Long> riskCategorySet = new HashSet();
        //记录数据源赔率变动的玩法对应的投注项
        Map<Long, List<String>> changeCategoryOddsType = new HashMap<>();
        for (ThirdMarketDTO thirdMarketDTO : thirdMarketDTOList) {
            StopWatch sw = new StopWatch(UUID.randomUUID().toString());
            String thirdCategorySourceId = thirdMarketDTO.getThirdMarketCategorySourceId();
            //获取盘口的三方玩法
            sw.start("查询盘口的三方玩法耗时");
            ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSourceCode, thirdCategorySourceId);
            sw.stop();
            if (thirdMarketCategory == null) {
                log.info("::{}::测试联赛，未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                continue;
            }
            if (null == thirdMarketCategory.getReferenceId() || 0L == thirdMarketCategory.getReferenceId()) {
                log.info("::{}::测试联赛，三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                continue;
            }

            StandardMarketDataMessage standardMarketDataMessage = processStandardSportMarket(linkId, standardMatchInfo, thirdMarketDTO, thirdMarketCategory, standardSportMarketSell, standardSportMarketList,request.getDataSourceTime(), oddsTypeIdSet,categorySet,changeCategoryOddsType,new ArrayList<>());
            if (standardMarketDataMessage == null) {
                continue;
            }
            marketCategoryIdSet.add(standardMarketDataMessage.getMarketCategoryId());
            marketIdSet.add(standardMarketDataMessage.getRelationMarketId());
            standardMarketDataMessageList.add(standardMarketDataMessage);
        }
        //数据源赔率告警，监听赔率数据下发时间
        matchOddsWarning(linkId, marketType, standardMatchInfo, marketCategoryIdSet);
        if(dataSourceCode.equals(DataSourceCodeEnum.TX.code)){
            request.setDataSourceTime(System.currentTimeMillis());
        }
        //标准赛事不存在，赔率不下发
        if (standardMatchInfo == null) {
            log.error("::{}::测试联赛，标准赛事不存在,三方赛事id:{}", linkId, thirdMatchInfo.getId());
            return;
        }
        //赛事未开售，赔率不下发
        if (standardSportMarketSell == null) {
            log.info("::{}::测试联赛，赛事未开售赔率不下发,标准赛事id:{}", linkId, standardMatchInfo.getId());
            return;
        }
        //本次有改变的盘口为空，赔率不下发
        if(CollectionUtils.isEmpty(standardMarketDataMessageList)){
            log.info("::{}::测试联赛，标准赛事id:{},本次有改变的盘口为空,赔率不下发", linkId, standardMatchInfo.getId());
            return;
        }
        //-------------------收到滚球赔率下发赛事滚球标识，并下发赛前关盘--------------------
        newClosePreMarkets(linkId, standardSportMarketSell, marketType, standardMatchInfo, request.getDataSourceTime(),true,new ArrayList<>(),0);
        //去重
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMapNew = standardMarketDataMessageList.stream().collect(Collectors.toMap(e -> e.getRelationMarketId().toString(), e -> e,(oldValue,newValue)->newValue));
        //获取当前数据源缓存中所有的盘口
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode;
        log.info("::{}::测试联赛，标准赛事id:{},数据源缓存redisKey:{}", linkId, standardMatchInfo.getId(), redisKey);
        standardMarketMessageMap = redisService.hGetAll(redisKey);
        if(standardMarketMessageMap == null){
            standardMarketMessageMap = new HashMap<>();
        }
        //此处是防止redis写入慢导致取到的还是旧赔率，必须用新值替换一次
        standardMarketMessageMap.putAll(standardMarketDataMessageMapNew);
        //对缓存所有数据进行排序
        marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketMessageMap,standardMatchInfo,marketCategoryIdSet,oddsTypeIdSet,categorySet,riskCategorySet,false);
        boolean redisResult = redisService.hSetAll(redisKey, standardMarketMessageMap, marketCacheTime(standardMatchInfo.getBeginTime()));
        log.info("::{}:: 测试联赛，accessMatchMarketData刷新缓存盘口信息, key={},marketIdList={},result={}", linkId, redisKey, marketCategoryIdSet, redisResult);

        if (CollectionUtils.isEmpty(standardMarketMessageMap)) {
            log.info("::{}::标准赛事id:{},standardMarketMessageMap is null", linkId, standardMatchInfo.getId());
            return;
        }

        if (isOutRight) {
            List<StandardOutrightMarket> outrightMarketList = standardOutrightMarketService.selectOutrightMarketSellList(standardMatchInfo.getId());
            if (CollectionUtils.isEmpty(outrightMarketList)) {
                log.info("::{}:: 测试联赛，冠军赛事未开售盘口,赔率不下发,冠军赛事id:{}", linkId, standardMatchInfo.getId());
                return;
            }
            List<Long> marketIdList = outrightMarketList.stream().map(x -> x.getId()).collect(Collectors.toList());
            standardMarketMessageMap = standardMarketMessageMap.entrySet().stream()
                    .filter(map -> marketIdList.contains(map.getValue().getRelationMarketId()))
                    .collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> e.getValue()));
        }

        if (marketCategoryIdSet.size() == 0)
        {
            log.info("::{}::测试联赛，标准赛事id:{},本次没有需要处理的标准玩法id:{}", linkId, standardMatchInfo.getId(), marketCategoryIdSet);
            return;
        }
        if(marketType == 2){
            //-------------------冠军操盘，直接下发赔率--------------------
            StopWatch swMTS = new StopWatch(UUID.randomUUID().toString());
            swMTS.start("冠军操盘盘口处理耗时");
            //对盘口进行排序，封装
            processOddsByOutright(linkId, standardMatchInfo, marketIdSet, standardMarketMessageMap, request.getDataSourceTime(), changeCategoryOddsType);
            swMTS.stop();
            log.info("::{}::测试联赛，冠军操盘盘口处理耗时{}ms," + swMTS.prettyPrint(), linkId, swMTS.getTotalTimeMillis());
        } else {
            // 4405：测试联赛同样按玩法级操盘模式分组（同场支持混合操盘）
            Set<Long> mtsCategoryIds = new HashSet<>();
            Set<Long> pandaCategoryIds = new HashSet<>();
            for (Long categoryId : marketCategoryIdSet) {
                String code = playRiskManagerService.getPlayRiskManagerCode(linkId, standardMatchInfo.getId(), marketType, categoryId, standardSportMarketSell);
                if (playRiskManagerService.isMtsFamily(code)) {
                    mtsCategoryIds.add(categoryId);
                } else {
                    pandaCategoryIds.add(categoryId);
                }
            }
            if (!CollectionUtils.isEmpty(mtsCategoryIds)) {
                //-------------------MTS操盘，直接下发赔率--------------------
                StopWatch swMTS = new StopWatch(UUID.randomUUID().toString());
                swMTS.start("MTS操盘盘口处理耗时");
                processOddsByMts(linkId, standardMatchInfo, mtsCategoryIds, standardMarketMessageMap, request.getDataSourceTime(), Boolean.TRUE);
                swMTS.stop();
                log.info("::{}::测试联赛，MTS操盘盘口处理耗时{}ms," + swMTS.prettyPrint(), linkId, swMTS.getTotalTimeMillis());
            }
            if (!CollectionUtils.isEmpty(pandaCategoryIds)) {
                //-----------panda操盘，计算和下发逻辑--------------------------
                StopWatch swPanda = new StopWatch(UUID.randomUUID().toString());
                swPanda.start("processOddsByPanda耗时");
                Set<Long> setAdd = new HashSet<>();
                pandaCategoryIds.forEach(x -> {
                    if (MarginCategoryConfig.HANDICAP_CATEGORY_SUBSECTION.contains(x)) {
                        setAdd.add(MarginCategoryConfig.HANDICAP_WINNER_MAP.get(x));
                    }
                });
                if (!CollectionUtils.isEmpty(setAdd)) {
                    //兼容历史数据
                    marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketMessageMap, standardMatchInfo, setAdd, null, null, null, false);
                    pandaCategoryIds.addAll(setAdd);
                }
                processOddsByPanda(linkId, -1, null, standardMatchInfo, pandaCategoryIds, standardMarketMessageMap, request.getDataSourceTime(), changeCategoryOddsType, Boolean.TRUE);
                swPanda.stop();
                log.info("::{}::测试联赛，processOddsByPanda耗时{}ms," + swPanda.prettyPrint(), linkId, swPanda.getTotalTimeMillis());
            }
        }
        if (!CollectionUtils.isEmpty(riskCategorySet)) {
            dataMerchantBaffleProducer.sendCategoryListToRiskMQ(linkId + "_riskCategorySet", standardMatchInfo.getId(), thirdMatchInfo.getSportId(), riskCategorySet, 3);
        }
    }


    /**
     * 标准盘口初盘
     *
     * @param likId
     * @param standardMatchInfo
     * @param standardMarketDataMessageList
     */
    private void standardFirstMarketBallHead(String likId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessageList) {
        Long sportId = standardMatchInfo.getSportId();
        if (!StandardSportTypeEnum.FootBall.code.equals(sportId) && !StandardSportTypeEnum.Basketball.code.equals(sportId)) {
            return;
        }
        //玩法过滤
        Map<Long, List<StandardMarketDataMessage>> listMap = standardMarketDataMessageList.stream().
                filter(e -> MarginCategoryConfig.THIRD_FIRST_MARKET_BALL_HEAD_CATEGORY.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
        if (MapUtils.isEmpty(listMap)) {
            return;
        }
        String fistMatchKey = Constant.REDIS_KEY.THIRD_FIST_MATCH;
        String fistKey = Constant.REDIS_KEY.THIRD_FIST_MARKET_HEAD + standardMatchInfo.getId();
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : listMap.entrySet()) {
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            standardMarketDataMessages.forEach(marketDataMessage -> {
                // //PA排序PlaceNum，取球头
                if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(marketDataMessage.getThirdMarketSourceStatus())
                        && 1 == marketDataMessage.getPlaceNum()) {
                    Integer marketType = marketDataMessage.getMarketType();
                    String dataSourceCode = marketDataMessage.getDataSourceCode();
                    Long marketCategoryId = marketDataMessage.getMarketCategoryId();
                    String key = "PA_" + dataSourceCode + "_" + marketCategoryId + "_" + marketType;
                    Object obj = redisService.hGet(fistKey, key);
                    if (Objects.isNull(obj)) {
                        marketDataMessage.setDataSourceCodePA("PA");
                        redisService.hSet(fistKey, key, marketDataMessage);
                        redisService.hSet(fistMatchKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime());
                        //初盘已写入缓存，下发消息不再携带该标记，避免下游按PA数据源匹配不到盘口
                        marketDataMessage.setDataSourceCodePA(null);
                    }
                }
            });
        }
    }
    /**
     * 滚球阶段关闭赛前
     */
    private void closePreByLive(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        int liveFlag = isOddsLive(standardMatchInfo.getId());
        //设置子玩法id
        standardMarketMessageList.forEach(marketMessage -> {
            if (liveFlag == 0 && marketMessage.getMarketType() == 1 && marketMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
                marketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketMessage.setRemark("关闭赛前盘兜底");
                log.info("::{}::赛事ID:{},三方盘口ID:{},标准盘口ID:{},关闭赛前盘兜底。"
                        , linkId, standardMatchInfo.getId(), marketMessage.getThirdMarketSourceId(), marketMessage.getId());
            }
        });
    }

    /**
     * 最后下发赔率 ，自动关盘兜底
     */
    public void automaticClosing(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
        Object a01ExtendedTimeObjects = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + standardMatchInfo.getId());
        Integer a01ExtendedTimeStatus = 0;
        if (!Objects.isNull(a01ExtendedTimeObjects)) {
            a01ExtendedTimeStatus = (Integer) a01ExtendedTimeObjects;
        }
        Integer finalA01ExtendedTimeStatus = a01ExtendedTimeStatus;
        standardMarketMessageList.forEach(standardSportMarket -> {
            Object autoCloseMap = redisService.hGet(autoCloseRedisKey, standardSportMarket.getChildMarketCategoryId().toString());
            if (!Objects.isNull(autoCloseMap)) {
                if (finalA01ExtendedTimeStatus == 1 && standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.AO.code) && MarginCategoryConfig.A01_EXTENDED_TIME_CATEGORY.contains(standardSportMarket.getMarketCategoryId())) {
                } else {
                    standardSportMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    standardSportMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    standardSportMarket.setEndEdStatus(0);
                    standardSportMarket.setRiskStatus(2);
                    log.info("::{}::automaticClosing关盘兜底,三方盘口：{}，标准盘口：{}", linkId, standardSportMarket.getThirdMarketSourceId(), standardSportMarket.getId());
                }
            }
        });
    }

    /**
     * 最后下发赔率 ，检查标准数据源与赔率盘口数据源
     */
    private void standardDataSourceCodeCheck(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        //查询玩法对应数据源
        String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + isOddsLive(standardMatchInfo.getId());
        Map<String, String> changeCategoryMap = redisService.hGetAll(categoryRedisKey);
        if (MapUtil.isNotEmpty(changeCategoryMap)) {
            standardMarketMessageList.forEach(standardSportMarket -> {
                Long marketCategoryId = standardSportMarket.getMarketCategoryId();
                //篮球 足球 主玩法不判断
                if ((standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.getCode()) && MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY.contains(marketCategoryId))
                        || (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode()) && MarginCategoryConfig.FootBall_MAIN3484_CATEGORY.contains(marketCategoryId))) {
                    return;
                }
                if (null != changeCategoryMap.get(String.valueOf(marketCategoryId))) {
                    if (!standardSportMarket.getDataSourceCode().equals(changeCategoryMap.get(String.valueOf(marketCategoryId)))) {
                        standardSportMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        standardSportMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        log.info("::{}::standardDataSourceCodeCheck标准数据源不匹配关盘,三方盘口：{}，标准盘口：{}",
                                linkId, standardSportMarket.getThirdMarketSourceId(), standardSportMarket.getId());
                    }
                } else {
                    standardSportMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    standardSportMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::standardDataSourceCodeCheck标准数据源不匹配关盘,三方盘口：{}，标准盘口：{}",
                            linkId, standardSportMarket.getThirdMarketSourceId(), standardSportMarket.getId());
                }
                //波胆
                Long mainMarketCategoryId = MarginCategoryConfig.A01_MARGIN_CATEGORY_CHEACK.get(standardSportMarket.getMarketCategoryId());
                if (null != mainMarketCategoryId) {
                    if (null != changeCategoryMap.get(mainMarketCategoryId) && !DataSourceCodeEnum.AO.code.equals(changeCategoryMap.get(String.valueOf(mainMarketCategoryId)))) {
                        standardSportMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        standardSportMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        log.info("::{}::standardDataSourceCodeCheck主玩法不是a01进行关盘,三方盘口：{}，标准盘口：{}", linkId, standardSportMarket.getThirdMarketSourceId(), standardSportMarket.getId());
                    }
                }
            });
        }
    }

    /**
     * 盘口时间戳校验
     * 1.根据当前盘口 modifyTime,去对比数据商盘口时间戳
     * 2.当前盘口 modifyTime < 数据商盘口时间戳  为旧盘口
     * 3.出现旧盘口 ，直接使用上一次下发的盘口去替换当前盘口
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    public void standardMarketVerifyModifyTime(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        List<Long> verifyModifyTimeCategoryIds = MarginCategoryConfig.VERIFY_MODIFY_TIME_CATEGORY.get(standardMatchInfo.getSportId());
        if (CollectionUtils.isEmpty(verifyModifyTimeCategoryIds)) {
            return;
        }
        //不对 T01 A01 数据源处理
        Map<Long, List<StandardMarketMessage>> standardMarketMessageMap = standardMarketMessageList.stream().filter(e -> verifyModifyTimeCategoryIds.contains(e.getMarketCategoryId()) && !MarginCategoryConfig.SPORT_TX_LOGIC.contains(e.getDataSourceCode())).collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        if (MapUtil.isEmpty(standardMarketMessageMap)) {
            return;
        }
        //获取上一次下发的最新盘口 ，上一次不存在不处理
        String lastMarketOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
        //需要删除的盘口
        List<StandardMarketMessage> removeMarket = new ArrayList<>();
        //需要加入的盘口
        List<StandardMarketMessage> addMarket = new ArrayList<>();
        //有变动的玩法
        Set<Long> marketCategoryIds = new HashSet<>();
        for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessageMap.entrySet()) {
            Long marketCategoryId = entry.getKey();
            List<StandardMarketMessage> lastStandardMarketMessages = (List<StandardMarketMessage>) redisService.hGet(lastMarketOddsKey, String.valueOf(marketCategoryId));
//            log.info("::{}::,standardMarketVerifyModifyTime,玩法ID:{}，上一次盘口：{}",
//                    linkId, marketCategoryId, JSONObject.toJSONString(lastStandardMarketMessages));
            if (CollectionUtils.isEmpty(lastStandardMarketMessages)) {
                continue;
            }
            ThirdMatchInfo thirdMatchInfo = null;
            //根据盘口id分组
            Map<Long, StandardMarketMessage> lastStandardMarketMessageMap = lastStandardMarketMessages.stream().collect(Collectors.toMap(StandardMarketMessage::getId, e -> e, (oldValue, newValue) -> newValue));
            //当前盘口时间戳 对比 上一次下发的时间戳
            for (StandardMarketMessage marketMessage : entry.getValue()) {
                //下发数据 1：融合构建 不处理
                if (1 == marketMessage.getMarketSource()) {
                    continue;
                }
                if (null == thirdMatchInfo) {
                    thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), marketMessage.getDataSourceCode());
                }
                ThirdSportMarket thirdSportMarket = thirdSportMarketService.getItem(marketMessage.getDataSourceCode(), marketMessage.getThirdMarketSourceId(), thirdMatchInfo.getId());
                if (null != thirdSportMarket && !thirdSportMarket.getStatus().equals(marketMessage.getThirdMarketSourceStatus())) {
                    marketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::,standardMarketVerifyModifyTime,玩法：{}，当前盘口ID:{}与最新缓存盘口状态不一致：{}-{}", linkId, marketCategoryId, marketMessage.getId(), marketMessage.getThirdMarketSourceStatus(), thirdSportMarket.getThirdMarketSourceStatus());
                    continue;
                }
                //上一次不存在不处理,时间不存在不处理
                StandardMarketMessage lastStandardMarketMessage = lastStandardMarketMessageMap.get(marketMessage.getId());
                if (null == lastStandardMarketMessage || null == lastStandardMarketMessage.getVerifyModifyTime() || 0 == lastStandardMarketMessage.getVerifyModifyTime()) {
                    continue;
                }
                //缓存数据 1：融合构建 不处理
                if (1 == lastStandardMarketMessage.getMarketSource()) {
                    continue;
                }
                //不存在的时间不处理
                if (null == marketMessage.getVerifyModifyTime() || 0 == marketMessage.getVerifyModifyTime()) {
                    continue;
                }

                //当前下发盘口校验时间 < 上一次盘口校验时间 ，为旧盘口 ，直接使用上一次的盘口数据
                if (marketMessage.getDataSourceCode().equals(lastStandardMarketMessage.getDataSourceCode()) && marketMessage.getVerifyModifyTime() < lastStandardMarketMessage.getVerifyModifyTime()) {
                    marketCategoryIds.add(marketCategoryId);
                    removeMarket.add(marketMessage);
                    addMarket.add(lastStandardMarketMessage);
                    log.info("::{}::,standardMarketVerifyModifyTime,玩法：{}，当前盘口ID:{}与上一次盘口校验时间：{}-{}，不通过使用上一次的盘口", linkId, marketCategoryId, marketMessage.getId(), marketMessage.getVerifyModifyTime(), lastStandardMarketMessage.getVerifyModifyTime());
                }
            }
        }
        //去除盘口时间戳不通过的盘口，加入上一次盘口
        if (!CollectionUtils.isEmpty(removeMarket)) {
            standardMarketMessageList.removeAll(removeMarket);
            standardMarketMessageList.addAll(addMarket);
            //找出变动玩法下所有盘口重新排序
            againSortPlaceNum(linkId, standardMatchInfo.getId(), standardMarketMessageList, marketCategoryIds);
        }
    }

    /**
     * 重新排序
     *
     * @param standardMarketMessageList
     */
    private void againSortPlaceNum(String linkId, Long matchId, List<StandardMarketMessage> standardMarketMessageList, Set<Long> marketCategoryIds) {
        Map<Long, List<StandardMarketMessage>> standardMarketMessagesMap = standardMarketMessageList.stream().filter(standardMarketMessage -> marketCategoryIds.contains(standardMarketMessage.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketMessage::getChildMarketCategoryId));
        for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessagesMap.entrySet()) {
            List<StandardMarketMessage> standardMarketMessages = entry.getValue();
            if (closedMarketPlaceSortHelper.isAllClosedForPlaceSort(standardMarketMessages)) {
                closedMarketPlaceSortHelper.sortClosedStandardMarkets(linkId, matchId, entry.getKey(), standardMarketMessages);
                continue;
            }
            // 算出投注项赔率差
            standardMarketMessages.forEach(m -> {
                if (!CollectionUtils.isEmpty(m.getMarketOddsList())) {
                    m.setOddsMetric(m.getMarketOddsList().stream().map(StandardMarketOddsMessage::getOriginalOddsValue).reduce(0, (a, b) -> a >= b ? a - b : b - a));
                }else{
                    m.setOddsMetric(999999);
                }
            });
            //数据商状态、赔率差 升序排序
            ListUtils.sort(standardMarketMessages, true, "status", "oddsMetric", "oddsValue");
            int placeNum = 1;
            for (StandardMarketMessage standardMarketMessage : standardMarketMessages) {
                standardMarketMessage.setPlaceNum(placeNum);
                placeNum = placeNum + 1;
            }
        }
    }

    /**
     * 盘口坑位时间戳校验
     * 1.根据当前坑位盘口 modifyTime,去对比数据商盘口时间戳
     * 2.当前坑位盘口 modifyTime < 数据商盘口时间戳  为旧盘口
     * 3.出现旧盘口 ，直接使用上一次下发的盘口去替换当前盘口
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    private void standardMarketPlaceVerifyModifyTime(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        List<Long> verifyModifyTimeCategoryIds = MarginCategoryConfig.VERIFY_MODIFY_TIME_CATEGORY.get(standardMatchInfo.getSportId());
        if (CollectionUtils.isEmpty(verifyModifyTimeCategoryIds)) {
            return;
        }
        //A01 数据源处理
        Map<Long, List<StandardMarketMessage>> standardMarketMessageMap = standardMarketMessageList.stream().filter(e -> verifyModifyTimeCategoryIds.contains(e.getMarketCategoryId()) && DataSourceCodeEnum.AO.code.equals(e.getDataSourceCode())).collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        if (MapUtil.isEmpty(standardMarketMessageMap)) {
            return;
        }
        //获取上一次下发的最新盘口 ，上一次不存在不处理
        String lastMarketOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
        //需要删除的盘口
        List<StandardMarketMessage> removeMarket = new ArrayList<>();
        //需要加入的盘口
        List<StandardMarketMessage> addMarket = new ArrayList<>();
        for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessageMap.entrySet()) {
            Long marketCategoryId = entry.getKey();
            List<StandardMarketMessage> lastStandardMarketMessages = (List<StandardMarketMessage>) redisService.hGet(lastMarketOddsKey, String.valueOf(marketCategoryId));
            if (CollectionUtils.isEmpty(lastStandardMarketMessages)) {
                continue;
            }
            //根据坑位ID分组
            Map<Integer, StandardMarketMessage> lastStandardMarketMessageMap = lastStandardMarketMessages.stream().collect(Collectors.toMap(StandardMarketMessage::getPlaceNum, e -> e, (oldValue, newValue) -> newValue));
            //当前盘口时间戳 对比 上一次下发的时间戳
            for (StandardMarketMessage marketMessage : entry.getValue()) {
                //下发数据 1：融合构建 不处理
                if (1 == marketMessage.getMarketSource()) {
                    continue;
                }
                //上一次不存在不处理,时间不存在不处理
                StandardMarketMessage lastStandardMarketMessage = lastStandardMarketMessageMap.get(marketMessage.getPlaceNum());
                if (null == lastStandardMarketMessage || null == lastStandardMarketMessage.getModifyTime() || 0 == lastStandardMarketMessage.getModifyTime()) {
                    continue;
                }
                //缓存数据 1：融合构建 不处理
                if (1 == lastStandardMarketMessage.getMarketSource()) {
                    continue;
                }
                //不存在的时间不处理
                if (null == marketMessage.getModifyTime() || 0 == marketMessage.getModifyTime()) {
                    continue;
                }
                //当前下发盘口校验时间 < 上一次盘口校验时间 ，为旧盘口 ，直接使用上一次的盘口数据
                if (marketMessage.getDataSourceCode().equals(lastStandardMarketMessage.getDataSourceCode()) && marketMessage.getModifyTime() < lastStandardMarketMessage.getModifyTime()) {
                    removeMarket.add(marketMessage);
                    addMarket.add(lastStandardMarketMessage);
                    log.info("::{}::,standardMarketPlaceVerifyModifyTime,盘口坑位,玩法：{}，当前盘口ID:{}与上一次盘口校验时间：{}-{}，不通过使用上一次的盘口", linkId, marketCategoryId, marketMessage.getId(), marketMessage.getVerifyModifyTime(), lastStandardMarketMessage.getVerifyModifyTime());
                }
            }
        }
        //去除盘口时间戳不通过的盘口，加入上一次盘口
        if (!CollectionUtils.isEmpty(removeMarket)) {
            standardMarketMessageList.removeAll(removeMarket);
            standardMarketMessageList.addAll(addMarket);
        }
    }


    /**
     * 盘口状态检查
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    public void standardMarketStatusCheck(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        Map<String, List<StandardMarketMessage>> standardMarketMessageMap = standardMarketMessageList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getDataSourceCode));
        for (Map.Entry<String, List<StandardMarketMessage>> lastEnty : standardMarketMessageMap.entrySet()) {
            String dataSourceCode = lastEnty.getKey();
            List<StandardMarketMessage> standardMarketMessages = lastEnty.getValue();
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), dataSourceCode);
            if (null == thirdMatchInfo) {
                continue;
            }
            List<String> keys = standardMarketMessages.stream().filter(s -> 0 == s.getMarketSource()).map(t -> RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdMatchInfo.getId() + "-" + t.getThirdMarketSourceId()).collect(Collectors.toList());
            List<Object> thirdSportMarketObj = redisService.mGet(keys);
            Map<String, ThirdSportMarket> thirdSportMarketMap = new HashMap<>();
            for (Object obj : thirdSportMarketObj) {
                if (null != obj) {
                    ThirdSportMarket thirdSportMarketMessage = (ThirdSportMarket) obj;
                    thirdSportMarketMap.put(thirdSportMarketMessage.getThirdMarketSourceId(), thirdSportMarketMessage);
                }
            }
            for (StandardMarketMessage marketMessage : standardMarketMessages) {
                //下发数据 1：融合构建 不处理
                if (1 == marketMessage.getMarketSource()) {
                    continue;
                }
                ThirdSportMarket thirdSportMarket = thirdSportMarketMap.get(marketMessage.getThirdMarketSourceId());
                if (null != thirdSportMarket
                        && thirdSportMarket.getDataSourceCode().equals(marketMessage.getDataSourceCode())
                        && !thirdSportMarket.getStatus().equals(marketMessage.getThirdMarketSourceStatus())) {
                    marketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::,standardMarketStatusCheck,玩法：{}，当前盘口ID:{}与最新缓存盘口状态不一致：{}-{}", linkId, marketMessage.getMarketCategoryId(), marketMessage.getId(), marketMessage.getThirdMarketSourceStatus(), thirdSportMarket.getThirdMarketSourceStatus());
                }
            }
        }
    }

    /**
     * 49578
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketCategoryId
     * @param standardMarketDataMessages
     * @param oldAddtion1Map
     */
    private void betMarketValueProcessor(String linkId, StandardMatchInfo standardMatchInfo, Long marketCategoryId, List<StandardMarketDataMessage> standardMarketDataMessages, Map<Long, String> oldAddtion1Map) {
        //只处理篮球，38玩法，上一次球头存在的数据
        if (!StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || 38L != marketCategoryId || null == oldAddtion1Map.get(marketCategoryId)) {
            return;
        }
        //只处理LS的 LS-1XBet数据 , 球头 - 盘口值 >=5  关盘处理
        for (StandardMarketDataMessage marketDataMessage : standardMarketDataMessages) {
            if (marketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.LS.getCode()) && StringUtils.equals(marketDataMessage.getInternalDataSourceCode(), Constant.LS_Bet365) && marketDataMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
                if (Math.abs(Double.parseDouble(oldAddtion1Map.get(marketDataMessage.getMarketCategoryId())) - Double.parseDouble(marketDataMessage.getAddition1())) >= MarginCategoryConfig.BASKETBALL_FLAP_ADDTION1_DOUBLE) {
                    marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketDataMessage.setRemark("LS-1XBet 上一次球头：'" + oldAddtion1Map.get(marketCategoryId) + "' - 盘口值 >=5 关盘处理 ");
                }
            }
        }
    }

    /**
     *  融合M模式子玩法下发
     *  找出总玩法下 存在m模式的子玩法 ，添加到A模式下发集合
     * @param linkId
     * @param standardMatchInfo
     * @param sendStandardMarketMessageList
     */
    private void addStandardMarketM(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> sendStandardMarketMessageList) {
        //赛种支持总玩法下的子玩法切换模式
        List<Long> switchModeChildCategory = MarginCategoryConfig.SWITCH_MODE_CHILD_CATEGORY.get(standardMatchInfo.getSportId());
        if (CollectionUtils.isEmpty(switchModeChildCategory)) {
            return;
        }
        //需要处理的玩法盘口
        Map<Long, List<StandardMarketMessage>> standardMarketMap = sendStandardMarketMessageList.stream().filter(s -> switchModeChildCategory.contains(s.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketMessage::getChildMarketCategoryId));
        if (MapUtils.isEmpty(standardMarketMap)) {
            return;
        }
        Set<Long> categoryIds = sendStandardMarketMessageList.stream().map(StandardMarketMessage::getMarketCategoryId).collect(Collectors.toSet());
        List<StandardMarketMessage> delete = new ArrayList<>();
        List<StandardMarketMessage> add = new ArrayList<>();
        //获取M上一次下发的最新盘口
        String standardMarketMKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_CHILD_MARKET_M_CATEGORY + standardMatchInfo.getId());
        //得到M模式最新盘口找到子玩法ID盘口，加入到A模式下发集合中
        Map<String, List<StandardMarketMessage>> lastMStandardMarketMessages = redisService.hGetAll(standardMarketMKey);
        if (MapUtils.isEmpty(lastMStandardMarketMessages)) {
            log.info("::{}::addStandardMarketM，m模式盘口不存在不处理", linkId);
            return;
        }
        //Map<子玩法, List<StandardMarketMessage>> ,在缓存总根据玩法得到最终加入到A模式下发集合中
        for (Map.Entry<String, List<StandardMarketMessage>> lastEnty : lastMStandardMarketMessages.entrySet()) {
            List<StandardMarketMessage> lastStandardMarketMessage = lastEnty.getValue();
            if(!categoryIds.contains(lastStandardMarketMessage.get(0).getMarketCategoryId())){
                continue;
            }
            Long lastChildMarketCategoryId = Long.valueOf(lastEnty.getKey());
            ConfigTradeType configTradeType = isSendMarketOddsByTradeType(linkId, standardMatchInfo.getId(), lastChildMarketCategoryId);
            //查询子玩法是不是m模式如果是放入到a模式集合下发
            Integer tradeTypeDB = 0;
            if (null != configTradeType) {
                tradeTypeDB = configTradeType.getTradeType();
            }
            if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeDB)) {
                add.addAll(lastStandardMarketMessage);
                List<StandardMarketMessage> standardMarketMessagesDet = standardMarketMap.get(lastChildMarketCategoryId);
                if (!CollectionUtils.isEmpty(standardMarketMessagesDet)) {
                    delete.addAll(standardMarketMessagesDet);
                }
            }
        }
        if (!CollectionUtils.isEmpty(add)) {
            sendStandardMarketMessageList.removeAll(delete);
            sendStandardMarketMessageList.addAll(add);

        }
    }


    /**
     * 数据商全封和全关判断处理
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    public void transformStatIfSatisfyCond(String linkId, StandardMatchInfo standardMatchInfo,
                                            List<StandardMarketMessage> standardMarketMessageList){
        if(!waitCloseTimeSwitch) {
            log.info("::{}::赛事id:{},transformStatIfSatisfyCond处理开关为关闭状态，不处理,", linkId, standardMatchInfo.getId());
            return;
        }
        List<Long> supportSportIdList = Arrays.asList(StandardSportTypeEnum.FootBall.code, StandardSportTypeEnum.Basketball.code);
        if (!supportSportIdList.contains(standardMatchInfo.getSportId())) {
            log.info("::{}::赛事id:{},transformStatIfSatisfyCond只处理足球和篮球,", linkId, standardMatchInfo.getId());
            return;
        }
        int liveFlag = isOddsLive(standardMatchInfo.getId());
        // 只处理滚球，早盘不处理
        if(liveFlag == 1) {
            return;
        }
        List<String> excludeDataSourceCodes = Arrays.asList(DataSourceCodeEnum.BE.getCode(),DataSourceCodeEnum.OD.getCode());
        //支持的玩法
        List<Long> supportCategory = StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) ? MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY
                    :StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) ? MarginCategoryConfig.FootBall_MAIN3484_CATEGORY: new ArrayList<>();
        //B03数据不做处理
        List<StandardMarketMessage> marketListExcludeA01 = standardMarketMessageList.stream()
                .filter(m -> !excludeDataSourceCodes.contains(m.getDataSourceCode()))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(marketListExcludeA01)) {
            log.info("linkId:{}只存在B03数据，不处理",linkId);
            return;
        }
        List<StandardMarketMessage> standardMarketMessageListNew = marketListExcludeA01;
        if (!CollectionUtils.isEmpty(supportCategory)) {
            standardMarketMessageListNew = marketListExcludeA01.stream().filter(m -> m.getColseMarket() == 1 && supportCategory.contains(m.getMarketCategoryId()))
                    .collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(standardMarketMessageListNew)) {
            log.info("linkId:{}过滤后玩法为空，不处理",linkId);
            return;
        }
        // 根据玩法分组
        Map<Long, List<StandardMarketMessage>> marketGroupByCategoryMap = standardMarketMessageListNew.stream()
                .collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        log.info("linkId:{},对玩法:{}开始进行全封和全关判断", linkId, marketGroupByCategoryMap.keySet());
        Set<String> dealCategorySet = new HashSet<>();
        // 遍历所有分组
        marketGroupByCategoryMap.forEach((category, maketList) -> {
            // 列表中有开，删除缓存
            boolean containsActive = maketList.stream()
                    .anyMatch(m -> Constant.SPORT_MARKET.STATUS.ACTIVE.equals(m.getThirdMarketSourceStatus())
                            || Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(m.getThirdMarketSourceStatus()));
            if (containsActive) {
                return;
            }
            // 获取关盘列表
            List<StandardMarketMessage> deactivatedMarketList = maketList.stream()
                    .filter(m -> Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(m.getThirdMarketSourceStatus()))
                    .collect(Collectors.toList());
            boolean isAllDeactivated = maketList.size() == deactivatedMarketList.size();
            // 是否为数据商全关
            if (isAllDeactivated) {
                // 判断是否有投注项，无投注项，盘口不处理
                List<StandardMarketMessage> oddsExistList = maketList.stream()
                        .filter(e -> checkMarketValue(linkId,standardMatchInfo,e))
                        .collect(Collectors.toList());

                if (CollectionUtils.isEmpty(oddsExistList)) {
                    return;
                }
                // 玩法下盘口状态转换处理
                oddsExistList.forEach(m -> {
                    m.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    m.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    m.getMarketOddsList().forEach(e -> {
                        // 将Pa赔率设置为抽水赔率
                        if (null == e.getOddsValue() || 0 == e.getOddsValue()){
                            e.setPaOddsValue(e.getOriginalOddsValue());
                        }else {
                            e.setPaOddsValue(e.getOddsValue());
                        }
                    });
                });
            }
        });
        log.info("linkId:{},玩法全封和全关判断结束,处理玩法有{}", linkId, dealCategorySet.toArray());
    }

    /**
     * 检查球头是否合法，不合法的球头直接过滤掉（保持关盘）
     * @param standardMarketMessage
     * @return
     */
    private boolean checkMarketValue(String linkId,StandardMatchInfo standardMatchInfo,StandardMarketMessage standardMarketMessage){
        if (ObjectUtil.isNull(standardMarketMessage)) {
            return false;
        }
        if (standardMarketMessage.getMarketOddsList() == null || CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())){
            return false;
        }
        if (!MarginCategoryConfig.CHECK_BASKETBALL_MARKET_VALUE.contains(standardMarketMessage.getMarketCategoryId())
            &&!MarginCategoryConfig.CHECK_FOOTBALL_MARKET_VALUE.contains(standardMarketMessage.getMarketCategoryId())){
            return true;
        }
        try{
            String key = "";
            if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())){
                key = DigestUtil.md5Hex("BASKETBALL_STANDARD_MATCH_SCORES:" + standardMatchInfo.getId());
                Object obj = redisService.get(key);
                if (null != obj){
                    log.info("::{}::赛事id:{},checkMarketValue，obj:{},", linkId, standardMatchInfo.getId(),obj);
                    String json = (String)obj;
                    BasketballCacheScores basketballCacheScores = JSONUtil.toBean(json,BasketballCacheScores.class);
                    log.info("::{}::赛事id:{},checkMarketValue，basketballCacheScores:{},", linkId, standardMatchInfo.getId(),JSON.toJSONString(basketballCacheScores));
                    //38L,18L,45L,51L,57L,63L,26L
                    if (StringUtils.isEmpty(standardMarketMessage.getAddition1())){
                        return false;
                    }
                    double marketValue = Double.parseDouble(standardMarketMessage.getAddition1()) - 0.5;
                    if (standardMarketMessage.getMarketCategoryId() == 38L){
                        if (basketballCacheScores.getWholeScores() == null)
                            basketballCacheScores.setWholeScores(new CommonItem(0,0));
                        if (marketValue < (basketballCacheScores.getWholeScores().getAway() +basketballCacheScores.getWholeScores().getHome())){
                            return false;
                        }
                    }
                    else if (standardMarketMessage.getMarketCategoryId() == 18L){
                        if (basketballCacheScores.getPeriodOneScore() == null)
                            basketballCacheScores.setPeriodOneScore(new CommonItem(0,0));
                        if (marketValue < (basketballCacheScores.getPeriodOneScore().getAway() +basketballCacheScores.getPeriodOneScore().getHome())){
                            return false;
                        }
                    }
                    else if (standardMarketMessage.getMarketCategoryId() == 45L){
                        if (basketballCacheScores.getFirstScores() == null)
                            basketballCacheScores.setFirstScores(new CommonItem(0,0));
                        if (marketValue < (basketballCacheScores.getFirstScores().getAway() +basketballCacheScores.getFirstScores().getHome())){
                            return false;
                        }
                    }
                    else if (standardMarketMessage.getMarketCategoryId() == 51L){
                        if (basketballCacheScores.getSecondScores() == null)
                            basketballCacheScores.setSecondScores(new CommonItem(0,0));
                        if (marketValue < (basketballCacheScores.getSecondScores().getAway() +basketballCacheScores.getSecondScores().getHome())){
                            return false;
                        }
                    }
                    else if (standardMarketMessage.getMarketCategoryId() == 57L){
                        if (basketballCacheScores.getThirdScores() == null)
                            basketballCacheScores.setThirdScores(new CommonItem(0,0));
                        if (marketValue < (basketballCacheScores.getThirdScores().getAway() +basketballCacheScores.getThirdScores().getHome())){
                            return false;
                        }
                    }
                    else if (standardMarketMessage.getMarketCategoryId() == 63L){
                        if (basketballCacheScores.getFourthScores() == null)
                            basketballCacheScores.setFourthScores(new CommonItem(0,0));
                        if (marketValue < (basketballCacheScores.getFourthScores().getAway() +basketballCacheScores.getFourthScores().getHome())){
                            return false;
                        }
                    }
                    else if (standardMarketMessage.getMarketCategoryId() == 26L){
                        if (basketballCacheScores.getPeriodTwoScore() == null)
                            basketballCacheScores.setPeriodTwoScore(new CommonItem(0,0));
                        if (marketValue < (basketballCacheScores.getPeriodTwoScore().getAway() +basketballCacheScores.getPeriodTwoScore().getHome())){
                            return false;
                        }
                    }
                }
            }else if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())){
                key = DigestUtil.md5Hex("FOOTBALL_STANDARD_MATCH_SCORES:" + standardMatchInfo.getId());
                Object obj = redisService.get(key);
                if (null != obj){
                    log.info("::{}::赛事id:{},checkMarketValue，obj:{},", linkId, standardMatchInfo.getId(),obj);
                    String json = (String)obj;
                    com.panda.merge.dto.FootballCacheScores footballCacheScores = JSONUtil.toBean(json,com.panda.merge.dto.FootballCacheScores.class);
                    log.info("::{}::赛事id:{},checkMarketValue，footballCacheScores:{},", linkId, standardMatchInfo.getId(),JSON.toJSONString(footballCacheScores));
                    if (StringUtils.isEmpty(standardMarketMessage.getAddition1())){
                        return false;
                    }
                    double marketValue = Double.parseDouble(standardMarketMessage.getAddition1()) - 0.5;
                    if (standardMarketMessage.getMarketCategoryId() == 2L){
                        if (footballCacheScores.getGoal() == null)
                            footballCacheScores.setGoal(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getGoal().getAway() +footballCacheScores.getGoal().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 114L){
                        if (footballCacheScores.getCorner() == null)
                            footballCacheScores.setCorner(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getCorner().getAway() +footballCacheScores.getCorner().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 307L){
                        if (footballCacheScores.getFaCard() == null)
                            footballCacheScores.setFaCard(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getFaCard().getAway() +footballCacheScores.getFaCard().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 127L){
                        if (footballCacheScores.getOverTimeGoal() == null)
                            footballCacheScores.setOverTimeGoal(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getOverTimeGoal().getAway() +footballCacheScores.getOverTimeGoal().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 331L){
                        if (footballCacheScores.getOverTimeCorner() == null)
                            footballCacheScores.setOverTimeCorner(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getOverTimeCorner().getAway() +footballCacheScores.getOverTimeCorner().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 1100407L){
                        if (footballCacheScores.getOverTimeFaCard() == null)
                            footballCacheScores.setOverTimeFaCard(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getOverTimeFaCard().getAway() +footballCacheScores.getOverTimeFaCard().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 335L){
                        if (footballCacheScores.getPenaltyScores() == null)
                            footballCacheScores.setPenaltyScores(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getPenaltyScores().getAway() +footballCacheScores.getPenaltyScores().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 18L){
                        if  (footballCacheScores.getHfGoal() == null)
                            footballCacheScores.setHfGoal(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getHfGoal().getAway() +footballCacheScores.getHfGoal().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 122L){
                        if (footballCacheScores.getHfCorner() == null)
                            footballCacheScores.setHfCorner(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getHfCorner().getAway() +footballCacheScores.getHfCorner().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 309L){
                        if (footballCacheScores.getHfFaCard() == null)
                            footballCacheScores.setHfFaCard(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getHfFaCard().getAway() +footballCacheScores.getHfFaCard().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 332L){
                        if (footballCacheScores.getOverTimeHfGoal() == null)
                            footballCacheScores.setOverTimeHfGoal(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getOverTimeHfGoal().getAway() +footballCacheScores.getOverTimeHfGoal().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 1100417L){
                        if (footballCacheScores.getOverTimeHfCorner() == null)
                            footballCacheScores.setOverTimeHfCorner(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getOverTimeHfCorner().getAway() +footballCacheScores.getOverTimeHfCorner().getHome())){
                            return false;
                        }
                    }else if (standardMarketMessage.getMarketCategoryId() == 1100410L){
                        if (footballCacheScores.getOverTimeHfFaCard() == null)
                            footballCacheScores.setOverTimeHfFaCard(new CommonItem(0,0));
                        if (marketValue < (footballCacheScores.getOverTimeHfFaCard().getAway() +footballCacheScores.getOverTimeHfFaCard().getHome())){
                            return false;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("::{}::赛事id:{},checkMarketValue，e:{},", linkId, standardMatchInfo.getId(),e.toString());
        }
        return true;
    }

    public void delCategoryCloseCache(String linkId, Long matchId, Set<String> needDelCacheCategorySet) {
        String categoryTimingProcessingKey = DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_PROCESSING + matchId);
        if (!CollectionUtils.isEmpty(needDelCacheCategorySet)) {
            // 将缓存中对应的玩法删除
            log.info("linkId:{},赛事id:{}delCategoryCloseCache此次删除缓存玩法有{}", linkId, matchId, JSON.toJSON(needDelCacheCategorySet));
            redisService.hDel(categoryTimingProcessingKey, needDelCacheCategorySet.toArray());
        }
    }

    private void processMarketStatusConvert(Long category, Map<String, CategoryStatsTimeData> categoryTimingStatData,
                                            Map<String, CategoryStatsTimeData> needAdd2Cache, List<StandardMarketMessage> oddsExistList,
                                            Set<String> dealCategorySet) {
        // 判断缓存是否存在
        if (CollectionUtils.isEmpty(categoryTimingStatData)
                || !categoryTimingStatData.containsKey(category.toString())) {
            CategoryStatsTimeData cacheData = new CategoryStatsTimeData();
            cacheData.setTime(System.currentTimeMillis());
            cacheData.setHaveAlreadySend(false);
            // 没有缓存,则添加缓存
            needAdd2Cache.put(category.toString(), cacheData);
        }
        if (!CollectionUtils.isEmpty(categoryTimingStatData)
                && categoryTimingStatData.containsKey(category.toString())) {
            CategoryStatsTimeData categoryStatsTimeDataCacheData = categoryTimingStatData.get(category.toString());
            if (categoryStatsTimeDataCacheData.isHaveAlreadySend()) {
                // 有缓存已发送，直接处理成关下发
                oddsExistList.forEach(m -> {
                    m.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    m.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                });
            } else {
                // 有缓存未下发过关，继续下发封
                oddsExistList.forEach(m -> {
                    m.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    m.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    m.getMarketOddsList().forEach(e -> {
                        // 将Pa赔率设置为抽水赔率
                        if (null == e.getOddsValue() || 0 == e.getOddsValue()){
                            e.setPaOddsValue(e.getOriginalOddsValue());
                        }else {
                            e.setPaOddsValue(e.getOddsValue());
                        }
                    });
                });
            }
        } else {
            // 无缓存，下发封
            oddsExistList.forEach(m -> {
                m.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                m.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                m.getMarketOddsList().forEach(e -> {
                    // 将Pa赔率设置为抽水赔率
                    if (null == e.getOddsValue() || 0 == e.getOddsValue()){
                        e.setPaOddsValue(e.getOriginalOddsValue());
                    }else {
                        e.setPaOddsValue(e.getOddsValue());
                    }
                });
            });
        }
        // 记录转换处理过的玩法
        dealCategorySet.add(category.toString());
    }

    private void addCategoryClose2Cache(StandardMatchInfo standardMatchInfo, Map<String, CategoryStatsTimeData> needAdd2Cache,
                                        Map<String, CategoryStatsTimeData> categoryTimingStatData, String categoryTimingProcessingKey) {
        if (!CollectionUtils.isEmpty(needAdd2Cache)) {
            // 缓存赛事id
            if (redisService.hasKey(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS)) {
                Boolean exist = redisService.sIsMember(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS,
                        standardMatchInfo.getId());
                if (!exist) {
                    redisService.sAdd(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS, (Object) standardMatchInfo.getId());
                }
            } else {
                redisService.sAdd(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS, (Object) standardMatchInfo.getId());
                redisService.expire(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
            // 缓存赛事下玩法数据
            categoryTimingStatData.putAll(needAdd2Cache);
            if (!redisService.hasKey(categoryTimingProcessingKey)) {
                redisService.hSetAll(categoryTimingProcessingKey, categoryTimingStatData,
                        marketCacheTime(standardMatchInfo.getBeginTime()));
            } else {
                redisService.hSetAll(categoryTimingProcessingKey, categoryTimingStatData);
            }
        }
    }

    /**
     * XTS 两项盘有一个投注项未激活改为关
     *
     * @param standardMarketMessageSendListMTS
     */
    public void xtsMarketOddsActive(String linkId, List<StandardMarketMessage> standardMarketMessageSendListMTS) {
        standardMarketMessageSendListMTS.forEach(standardMarketOddsMessage -> {
            List<StandardMarketOddsMessage> marketOddsList = standardMarketOddsMessage.getMarketOddsList();
            if (!CollectionUtils.isEmpty(standardMarketOddsMessage.getMarketOddsList())
                    && standardMarketOddsMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED
                    && standardMarketOddsMessage.getMarketOddsList().size() <= 3) {
                boolean notActive = marketOddsList.stream().anyMatch(t -> 1 != t.getActive());
                if (notActive) {
                    standardMarketOddsMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    standardMarketOddsMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::XTS两项盘有效盘口投注项只有一个激活，改为数据商关盘下发：{}-{} ", linkId, standardMarketOddsMessage.getId(), standardMarketOddsMessage.getThirdMarketSourceId());
                }
            }
        });
    }

    /**
     *  xts  欧赔转马来
     * @param standardMarketMessageSendListMTS
     */
    public void convertXtsMalayOddsValue(List<StandardMarketMessage> standardMarketMessageSendListMTS) {
        for (StandardMarketMessage xts : standardMarketMessageSendListMTS) {
            Long marketCategoryId = xts.getMarketCategoryId();
            if (MarginCategoryConfig.MY_ODDS_GRACEFUL_CATEGORY.contains(marketCategoryId)
                    || MarginCategoryConfig.COMPLEX_MY_CATEGORY_ODDS_VERIFY.contains(marketCategoryId)) {
                xts.setCategoryType("MY");
                List<StandardMarketOddsMessage> marketOddsList = xts.getMarketOddsList();
                if (!CollectionUtils.isEmpty(marketOddsList)) {
                    for (StandardMarketOddsMessage standardMarketOddsMessage : marketOddsList) {
                        standardMarketOddsMessage.setMalayOddsValue(initializeComponent.getConvertEuropeToMalay(standardMarketOddsMessage.getPaOddsValue()));
                    }
                }
            }
        }
    }

    /**
     * xts 赔率优化截取小数点后两位
     * @param standardMarketMessageSendListMTS
     */
    public void processOddsValueDecimalsXts(List<StandardMarketMessage> standardMarketMessageSendListMTS) {
        for (StandardMarketMessage xts : standardMarketMessageSendListMTS) {
            List<StandardMarketOddsMessage> marketOddsList = xts.getMarketOddsList();
            if (!CollectionUtils.isEmpty(marketOddsList)) {
                for (StandardMarketOddsMessage standardMarketOddsMessage : marketOddsList) {
                    if (null == standardMarketOddsMessage.getOddsValue() || 0 == standardMarketOddsMessage.getOddsValue()) {
                        continue;
                    }
                    BigDecimal oneHundredThousand = new BigDecimal("100000");
                    standardMarketOddsMessage.setPaOddsValue(new BigDecimal(standardMarketOddsMessage.getOddsValue()).divide(oneHundredThousand).setScale(2, BigDecimal.ROUND_DOWN).multiply(oneHundredThousand).intValue());
                }
            }
        }
    }


}
