package com.panda.merge.rocketmq.processor;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.BaseBatchProcessor;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.*;
import com.panda.merge.config.MarketDbProducer;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.constant.*;
import com.panda.merge.converter.ThirdMatchMarketConverter;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.*;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.odds.enums.MarketHandlingEnum;
import com.panda.merge.odds.service.FlowControlService;
import com.panda.merge.odds.service.PreSoldReportService;
import com.panda.merge.odds.ThirdMarket108048CacheService;
import com.panda.merge.odds.ThirdMarketMonitor;
import com.panda.merge.odds.validate.FootballMarketValidateService;
import com.panda.merge.proxy.UpdateOperateProxy;
import com.panda.merge.rocketmq.producer.DataMerchantBaffleProducer;
import com.panda.merge.rocketmq.producer.MatchOddWarningProducer;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.*;
import com.panda.merge.service.impl.StandardSportMarketOddsServiceImpl;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.panda.merge.constant.ConstantSystem.ONE;
import static com.panda.merge.constant.ConstantSystem.ZERO;
import static com.panda.merge.service.impl.StandardSportMarketOddsNewServiceImpl.getOddsName;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/13 <br>
 */
@Slf4j
@Component
@Validated
public class ThirdMatchMarketBatchProcessor extends BaseBatchProcessor {

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
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private StandardOutrightMatchInfoService standardOutrightMatchInfoService;

    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @Autowired
    private DataMerchantBaffleProducer dataMerchantBaffleProducer;
    @Autowired
    private MatchOddWarningProducer matchoddWarningProducer;
    @Autowired
    private StandardSportPlayerService standardSportPlayerService;
    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    @Autowired
    private OutrightTradeTypeConfigService outrightTradeTypeConfigService;
    @Autowired
    private OutrightTradeMarketConfigService outrightTradeMarketConfigService;
    @Autowired
    private OutrightTradeOddsConfigService outrightTradeOddsConfigService;
    @Autowired
    private OutrightTradeProbabilityConfigService outrightTradeProbabilityConfigService;
    @Autowired
    private UpdateOperateProxy updateOperateProxy;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;
    @Autowired
    private I18nOutrightMarketService i18nOutrightMarketService;
    @Autowired
    private CommonAsyncService commonAsyncService;
    @Resource
    private ThirdMatchMarketConverter thirdMatchMarketConverter;


    @Autowired
    private MarketDbProducer marketDbProducer;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ThirdMarketBatchSaveProcessor thirdMarketBatchSaveProcessor;

    @Autowired
    private DealMarketStatusProcessor dealMarketStatusProcessor;

    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private MarketOddsPlaceProcessor marketOddsPlaceProcessor;

    @Autowired
    private FootballMarketsSoreProcessor footballMarketsSoreProcessor;

    @Autowired
    private PreSoldReportService preSoldReportService;

    @Autowired
    private FootballMarketValidateService footballMarketValidateService;

    @Autowired
    private ThirdMarketMonitor thirdMarketMonitor;

    @Autowired
    private FlowControlService flowControlService;

    @Autowired
    private ThirdMarket108048CacheService thirdMarket108048CacheService;

    @Autowired
    @Qualifier("championMarketThreadPool")
    private TaskExecutor championMarketExecutor;

    @Autowired
    private StandardMatchMarketOddsLinkageProcessor standardMatchMarketOddsLinkageProcessor;


    /**
     * 处理收到的数据源赔率
     *
     * @param requests
     */
    @Async("AccessMatchMarketData")
    @ExceptionHelper
    public void accessMatchMarketData(@Valid List<Request<ThirdMatchMarketDTO>> requests) {
        String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
        Long uuid = UUIdUtils.getId();
        log.info("::{}:: accessMatchMarketData start UUID: {} 请求size: {}", linkIds, uuid, requests.size());
        // 至于Id 我觉得给UUID是可行的~
        StopWatch swRedis = new StopWatch("赔率服务数据源赔率入口主流程_" + UUIdUtils.getId());
        swRedis.start("三方盘口入库前耗时");
        Map<String, Request<ThirdMatchMarketDTO>> requestMap = requests.stream().collect(Collectors.toMap(Request::getLinkId, Function.identity(), (v1, v2) -> v1));
        //获取数据源运动类型存在的数据
        List<OddsWrapper<ThirdMatchMarketDTO>> validRequest = validateSportId(requestMap);
        validRequest.forEach(t -> t.setUuid(uuid));
        log.info("::{}:: accessMatchMarketData 运动类型验证后size:{}", uuid, requests.size());
        //判断冠军玩法
        Map<String, String> outrightCategoryMap = Stream.of(MarginCategoryConfig.THIRD_OUTRIGHT_CATEGORY).collect(Collectors.toMap(t -> t, Function.identity(), (v1, v2) -> v1));
        validRequest.forEach(t -> {
            Boolean isOutright = outrightCategoryMap.containsKey(t.getData().getMarketList().get(0).getThirdMarketCategorySourceId());
            t.setIsOutRight(isOutright);
        });

        //三方赛事信息
        List<ThirdMatchInfo> thirdMatchInfoList = getThirdMatchInfo(validRequest);
        log.info("::{}:: 三方赛事信息size: {}", uuid, thirdMatchInfoList.size());
        Map<String, ThirdMatchInfo> thirdMatchInfoMap = thirdMatchInfoList.stream().collect(Collectors.toMap(t -> t.getDataSourceCode() + "-" + t.getThirdMatchSourceId(), Function.identity(), (v1, v2) -> v1));
        Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap = thirdMatchInfoList.stream().collect(Collectors.toMap(ThirdMatchInfo::getId, Function.identity(), (v1, v2) -> v1));
        List<OddsWrapper<ThirdMatchMarketDTO>> thirdMatchNotExistRequest = Collections.synchronizedList(new ArrayList());
        validRequest = validRequest.stream().filter(t -> {
            String key = t.getDataSourceCode() + "-" + t.getThirdMatchSourceId();
            if (thirdMatchInfoMap.containsKey(key)) {
                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMap.get(key);
                t.setThirdMatchId(thirdMatchInfo.getId());
                t.setStandardSourceId(thirdMatchInfo.getReferenceId());
                return true;
            }
            if (!t.getIsOutRight()) {
                thirdMatchNotExistRequest.add(t);
            }
            return false;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(thirdMatchNotExistRequest)) {
            List<String> NotExistLinkIds = thirdMatchNotExistRequest.stream().map(OddsWrapper::getLinkId).collect(Collectors.toList());
            log.info("::{}:: accessMatchMarketData 三方赛事不存在请求信息: {} size:{}", uuid, NotExistLinkIds, thirdMatchNotExistRequest.size());
            applicationContext.getBean(ThirdMatchMarketBatchProcessor.class).thirdMatchNotExistCacheMarket(thirdMatchNotExistRequest);
        }
        validRequest = flowControlService.filter(validRequest);
        if (CollectionUtils.isEmpty(validRequest)) {
            log.info("::{}:: 三方赛事后请求为空，直接返回！", uuid);
            return;
        }
        //兼容冠军玩法，获取标准赛事信息
        List<StandardMatchInfoDetail> standardMatchInfoList = getStandardMatchInfo(validRequest);
        log.info("::{}:: 标准赛事信息size: {}", uuid, standardMatchInfoList.size());
        Map<Long, StandardMatchInfoDetail> standardMatchInfoMap = standardMatchInfoList.stream().collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity()));
        validRequest = validRequest.stream().filter(t -> {
            Long standardId = t.getStandardSourceId();
            if (standardId != null && !standardMatchInfoMap.containsKey(standardId)) {
                t.setStandardSourceId(null);
            }
            return true;
        }).collect(Collectors.toList());

        //兼容冠军玩法，获取赛事开售信息
        List<StandardSportMarketSell> standardSportMarketSell = getStandardSportMarketSell(validRequest);
        log.info("::{}:: 赛事开售信息size: {}", uuid, standardSportMarketSell.size());

        Map<Long, StandardSportMarketSell> standardSportMarketSellMap = standardSportMarketSell.stream().collect(Collectors.toMap(StandardSportMarketSell::getMatchInfoId, Function.identity(), (v1, v2) -> v1));

        //-----------循环处理盘口数据---------------
        //标准赔率修改
        Map<String, List<StandardSportMarketOdds>> standardSportMarketOddsUpdateMap = new ConcurrentHashMap<>();
        //存储需要下发的三方数据商盘口集合
        Map<String, List<ThirdSportMarketMessage>> thirdSportMarketMessagesMap = new ConcurrentHashMap<>();
        //三方赔率修改
        Map<String, List<ThirdSportMarketOdds>> thirdSportMarketOddsUpdateMap = new ConcurrentHashMap<>();

        Map<String,MarketCategorySell> marketCategorySellCache = new ConcurrentHashMap<>();
        swRedis.stop();

        //三方盘口入库
        swRedis.start("三方盘口入库耗时");
        log.info("::{}:: 开始三方盘口入库size: {}", uuid, validRequest.size());
        List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOS = thirdMarketBatchSaveProcessor.marketBatchSaveProcessor(
                validRequest,
                thirdMatchInfoBasedIdMap,
                standardMatchInfoMap,
                thirdSportMarketMessagesMap,
                thirdSportMarketOddsUpdateMap,
                standardSportMarketSellMap,
                marketCategorySellCache);
        thirdMarket108048CacheService.cacheFromWrappers(thirdMarketDTOS);
        log.info("::{}:: accessMatchMarketData v1标准赔率修改 size:{}", uuid, standardSportMarketOddsUpdateMap.size());
        log.info("::{}:: accessMatchMarketData v1存储需要下发的三方数据商盘口集合 size:{}", uuid, thirdSportMarketMessagesMap.size());
        log.info("::{}:: accessMatchMarketData v1三方赔率修改 size:{}", uuid, thirdSportMarketOddsUpdateMap.size());
        swRedis.stop();
        swRedis.start("三方盘口入库后到加锁数据处理前耗时");
        Map<Boolean, List<OddsWrapper<ThirdMarketDTO>>> thirdMarketDTOMap = thirdMarketDTOS.stream().collect(Collectors.groupingBy(t -> t.getData().getLock()));
        Map<String, OddsWrapper<ThirdMarketDTO>> linkMarketDTOMap = thirdMarketDTOS.stream().collect(Collectors.toMap(t -> t.getLinkId(), Function.identity(), (v1, v2) -> v1));

        //不需要加锁
        List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTONotLock = thirdMarketDTOMap.getOrDefault(Boolean.FALSE, Collections.synchronizedList(new ArrayList()));
        preSoldReportService.report(thirdMarketDTONotLock, standardSportMarketSellMap);

        log.info("::{}:: accessMatchMarketData 不需要加锁size:{}", uuid, thirdMarketDTONotLock.size());
        //需要加锁的盘口
        List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOLock = thirdMarketDTOMap.getOrDefault(Boolean.TRUE, Collections.synchronizedList(new ArrayList()));
        log.info("::{}:: accessMatchMarketData 需要加锁的盘口size:{}", uuid, thirdMarketDTOLock.size());

        // 赛前盘缓存开赛时间
        List<OddsWrapper<ThirdMarketDTO>> filteredThirdMarketDTOLock = thirdMarketDTOLock.stream().filter(t -> t.getStandardSourceId() != null && t.getMarketType() == 1 && !t.getIsOutRight() && standardSportMarketSellMap.containsKey(t.getStandardSourceId())).collect(Collectors.toList());
        List<String> standardIdKeys = filteredThirdMarketDTOLock.stream().map(t -> Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + t.getStandardSourceId()).distinct().collect(Collectors.toList());
        List<Object> standardIdValues = redisService.mGet(standardIdKeys);
        List<Long> requiredStoredStandardIds = Collections.synchronizedList(new ArrayList());
        for (int i = 0; i < standardIdKeys.size(); i++) {
            if (standardIdValues.get(i) == null) {
                requiredStoredStandardIds.add(Long.parseLong(standardIdKeys.get(i).substring(Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET.length())));
            }
        }
        List<StandardMatchInfo> refreshStandardMatchInfos = standardMatchInfoService.getItemByPrimaryKeys(requiredStoredStandardIds);
        refreshStandardMatchInfos.stream().filter(t -> TimeUtils.timeCalendar(t.getBeginTime())).forEach(standardMatchInfo -> {
            String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
            String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
            redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime(),marketCacheTime(standardMatchInfo.getBeginTime()));
            log.info("赛事：{},KEY:{},赛前盘缓存开赛时间:{}", standardMatchInfo.getId(), updatedKey, standardMatchInfo.getBeginTime());
        });

        //存储当前数据里面的盘口id
        Map<String, Set<Long>> marketIdSetMap = new ConcurrentHashMap<>();
        //存储当前数据里面的玩法
        Map<String, Set<Long>> marketCategoryIdSetMap = new ConcurrentHashMap<>();
        //存储需要缓存的盘口数据
        Map<String, List<StandardSportMarket>> standardSportMarketMap = new ConcurrentHashMap<>();
        Map<String, List<StandardMarketDataMessage>> standardMarketDataMessageMap = new ConcurrentHashMap<>();
        //最终下发的玩法盘口
        Map<String, Map<String, StandardMarketDataMessage>> standardCategoryMarketMessageMap = new ConcurrentHashMap<>();
        //最终需要推送给风控的报警的玩法集合
        Map<String, Set<Long>> riskCategoryMap = new ConcurrentHashMap<>();
        //记录数据源赔率变动的玩法对应的投注项
        Map<String, Map<Long, List<String>>> changeCategoryOddsTypeMap = new ConcurrentHashMap<>();
        //测试联赛需要处理的三方盘口数据
        List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOList = Collections.synchronizedList(thirdMarketDTONotLock);
        //当前时间前100秒 1852需求兜底
        swRedis.stop();
        swRedis.start("三方和标准盘口处理");
        long totalTime = 0;
        List<Long> thirdMarketMillis = Collections.synchronizedList(new ArrayList<>());
        List<Long> standardMarketMillis = Collections.synchronizedList(new ArrayList<>());
        List<Long> standardSize = Collections.synchronizedList(new ArrayList<>());
        try {
            if (!CollectionUtils.isEmpty(thirdMarketDTOLock)) {
                Map<String, List<OddsWrapper<ThirdMarketDTO>>> thirdMarketMapDTO = thirdMarketDTOLock.stream().collect(Collectors.groupingBy(t -> t.getData().getThirdMarketCategorySourceId()));
                log.info("::{}:: accessMatchMarketData 加锁的玩法size:{}", uuid, thirdMarketMapDTO.size());
                // 需要进行足球处理的数据，如TX让球比分处理等
                Map<String, Object> resultForSwitchStatus = new ConcurrentHashMap<>();
                Map<String, Object> resultForStandardScores = new ConcurrentHashMap<>();
                Map<String, Object> resultForscoreCenter = new ConcurrentHashMap<>();
                thirdMarketBatchSaveProcessor.doFootballProcess(thirdMarketDTOLock, resultForSwitchStatus, resultForStandardScores, resultForscoreCenter, thirdMatchInfoBasedIdMap);
                // 获取开售缓存
                List<OddsWrapper<ThirdMarketDTO>> dataForObtainMarketCategorySell = thirdMarketDTOLock.stream().filter(t -> !t.getIsOutRight()).collect(Collectors.toList());
                Pair<Map<String, Object>, Map<String, MarketCategorySell>> allMarketCategorySell = thirdMarketBatchSaveProcessor.obtainAllMarketCategorySell(dataForObtainMarketCategorySell);
                long startTime = System.currentTimeMillis();
                List<CompletableFuture<?>> futures = new ArrayList<>();
                TaskExecutor taskExecutor = threadPoolConfig.getThirdAndStandardMarketProcess();
                Map<String, List<ThirdSportMarketMessage>> finalThirdSportMarketMessagesMap = thirdSportMarketMessagesMap;
                Map<String, List<StandardMarketDataMessage>> finalStandardMarketDataMessageMap1 = standardMarketDataMessageMap;
                Map<String, Set<Long>> finalMarketCategoryIdSetMap = marketCategoryIdSetMap;
                Map<String, Map<String, StandardMarketDataMessage>> finalStandardCategoryMarketMessageMap = standardCategoryMarketMessageMap;
                for (Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entry : thirdMarketMapDTO.entrySet()) {
                    futures.add(CompletableFuture.supplyAsync(() -> {
                        try {

                        StopWatch smm = new StopWatch(UUIdUtils.getId().toString());
                        smm.start("玩法级别三方赔率入库耗时");
                        List<OddsWrapper<ThirdMarketDTO>> storeData = Collections.synchronizedList(new ArrayList());
                        //存储需要校验数据源挡板的投注项集合
                        Map<String, Set<Long>> oddsTypeIdSetMap = new ConcurrentHashMap<>();
                        //存储需要校验报警的玩法集合
                        Map<String, Set<Long>> categorySetMap = new ConcurrentHashMap<>();
                        thirdMarketBatchSaveProcessor.doProcess(uuid, entry, thirdMatchInfoBasedIdMap, resultForSwitchStatus, resultForStandardScores, resultForscoreCenter, storeData, standardMatchInfoMap, Boolean.TRUE, Boolean.FALSE);
                        //三方赔率入库
                        List<OddsWrapper<ThirdSportMarketMessage>> marketMessages = processThirdSportMarket(storeData, thirdMatchInfoBasedIdMap, thirdSportMarketOddsUpdateMap);


                        //百家赔
                        for (OddsWrapper<ThirdSportMarketMessage> item : marketMessages) {
                            if (finalThirdSportMarketMessagesMap.containsKey(item.getLinkId())) {
                                finalThirdSportMarketMessagesMap.get(item.getLinkId()).add(item.getData());
                            } else {
                                List<ThirdSportMarketMessage> newItems = Collections.synchronizedList(new ArrayList());
                                newItems.add(item.getData());
                                finalThirdSportMarketMessagesMap.put(item.getLinkId(), newItems);
                            }
                        }
                        thirdMarketDTOList.addAll(storeData);
                        smm.stop();
                        thirdMarketMillis.add(smm.getLastTaskTimeMillis());
                        smm.start("主客队相反耗时");
                        //主客队相反盘口、投注项相关内容处理
                        for (OddsWrapper<ThirdMarketDTO> inner : storeData) {
                            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoBasedIdMap.get(inner.getThirdMatchId());
                            if (ONE.equals(thirdMatchInfo.getHomeAwayOpposite()) && thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code)) {
                                if (!CategoryOppositeConfig.FootBall.containsCategory(inner.getMarketCategoryId())) {
                                    continue;
                                }
                                Long newCategoryId = changeStandardMarketContent(inner.getLinkId(), inner.getDataSourceCode(), inner.getMarketCategoryId(), inner.getData());
                                if (newCategoryId != null) {
                                    inner.setMarketCategoryId(newCategoryId);
                                }
                            }
                        }
                        smm.stop();
                        smm.start("标准盘口入库总耗时");
                        //-------------处理标准盘口及投注项数据------------
                        standardSize.add((long) storeData.size());

                        List<StandardMarketDataMessage> standardMarketDataMessages = processStandardSportMarket(storeData, standardMatchInfoMap, standardSportMarketSellMap, standardSportMarketMap, oddsTypeIdSetMap, categorySetMap, changeCategoryOddsTypeMap, standardSportMarketOddsUpdateMap, allMarketCategorySell);
                        smm.stop();
                        standardMarketMillis.add(smm.getLastTaskTimeMillis());
                        for (StandardMarketDataMessage item : standardMarketDataMessages) {
                            if (finalStandardMarketDataMessageMap1.containsKey(item.getLinkId())) {
                                finalStandardMarketDataMessageMap1.get(item.getLinkId()).add(item);
                            } else {
                                List<StandardMarketDataMessage> newItems = Collections.synchronizedList(new ArrayList());
                                newItems.add(item);
                                finalStandardMarketDataMessageMap1.put(item.getLinkId(), newItems);
                            }
                            if (marketIdSetMap.containsKey(item.getLinkId())) {
                                marketIdSetMap.get(item.getLinkId()).add(item.getRelationMarketId());
                            } else {
                                Set<Long> newItems = Collections.synchronizedSet(new HashSet());
                                newItems.add(item.getRelationMarketId());
                                marketIdSetMap.put(item.getLinkId(), newItems);
                            }
                            if (finalMarketCategoryIdSetMap.containsKey(item.getLinkId())) {
                                finalMarketCategoryIdSetMap.get(item.getLinkId()).add(item.getMarketCategoryId());
                            } else {
                                Set<Long> newItems = Collections.synchronizedSet(new HashSet());
                                newItems.add(item.getMarketCategoryId());
                                finalMarketCategoryIdSetMap.put(item.getLinkId(), newItems);
                            }
                        }
                        smm.start("玩法级别真正的加锁总耗时");
                        if (standardMarketDataMessages != null) {
                            Map<String, OddsWrapper<ThirdMarketDTO>> matchIdMap = storeData.stream().collect(Collectors.toMap(OddsWrapper::getLinkId, Function.identity(), (v1, v2) -> v1));
                            for (Map.Entry<String, OddsWrapper<ThirdMarketDTO>> item : matchIdMap.entrySet()) {
                                syncObtainMarketsWithLock(item, standardMatchInfoMap, finalStandardCategoryMarketMessageMap, oddsTypeIdSetMap, categorySetMap, riskCategoryMap);
                            }
                        }
                        smm.stop();
                        log.info("::{}::循环处理盘口入库和投注项总耗时{}ms," + smm.prettyPrint(), uuid, smm.getTotalTimeMillis());
                        }catch (Throwable t){
                            log.error("::" + uuid + ":: accessMatchMarketData 循环出现异常，Throwable ", t);
                        }
                        return null;
                    }, taskExecutor));
                }
                //等待盘口异步处理
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                //等待盘口异步处理
                long endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                //数据源赔率告警，监听赔率数据下发时间
                batchMatchOddsWarning(thirdMarketDTOLock, standardMatchInfoMap, marketCategoryIdSetMap);
            }
            log.info("三方和标准totalTime is {}", totalTime);
            log.info("三方totalTime is {}", thirdMarketMillis);
            log.info("标准totalTime is {}", standardMarketMillis);
            log.info("标准totalSize is {}", standardSize);
            swRedis.stop();
            thirdMarketMonitor.monitor(uuid , thirdMarketDTOList,marketCategorySellCache);
            swRedis.start("三方和标准数据存储耗时");
            StopWatch storeDataSW = new StopWatch(UUIdUtils.getId().toString());
            storeDataSW.start("AO耗时");
            for (Map.Entry<String, Request<ThirdMatchMarketDTO>> entry : requestMap.entrySet()) {
                if (DataSourceCodeEnum.AO.code.equals(entry.getValue().getData().getDataSourceCode())) {
                    requestMap.get(entry.getKey()).setDataSourceTime(System.currentTimeMillis());
                }
            }
            storeDataSW.stop();
            storeDataSW.start("三方赔率修改耗时");
            log.info("::{}:: accessMatchMarketData v2标准赔率修改 size:{}", uuid, standardSportMarketOddsUpdateMap.size());
            log.info("::{}:: accessMatchMarketData v2存储需要下发的三方数据商盘口集合 size:{}", uuid, thirdSportMarketMessagesMap.size());
            log.info("::{}:: accessMatchMarketData v2三方赔率修改 size:{}", uuid, thirdSportMarketOddsUpdateMap.size());
            //三方赔率修改
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsUpdateMap)) {
                //发送mq
                log.info("::{}:: 标准盘口update入库standard_sport_market_update size:{}", uuid, thirdSportMarketOddsUpdateMap.size());
                thirdSportMarketOddsUpdateMap.entrySet().forEach(entry -> {
                    marketDbProducer.sendThirdMarketOddsUpdateInfo(entry.getKey(), entry.getValue());
                });
            }
            storeDataSW.stop();
            storeDataSW.start("标准盘口赔率修改耗时");
            //标准盘口赔率修改
            Map<String, OddsWrapper<ThirdMarketDTO>> finalLinkMarketDTOMap1 = linkMarketDTOMap;
            if (!CollectionUtils.isEmpty(standardSportMarketOddsUpdateMap)) {
                log.info("::{}:: 标准盘口投注项update入库standard_sport_market_odds_update data size:{}", uuid, standardSportMarketOddsUpdateMap.size());
                standardSportMarketOddsUpdateMap.entrySet().forEach(entry -> {
                    standardSportMarketOddsService.upStandardOddsList(entry.getKey(), finalLinkMarketDTOMap1.get(entry.getKey()).getStandardSourceId(), entry.getValue());
                });
            }
            storeDataSW.stop();
            storeDataSW.start("冠军赛事初始化盘口开售表耗时");
            //冠军赛事初始化盘口开售表
            List<StandardOutrightMarket> standardOutrightMarketList = Collections.synchronizedList(new ArrayList());
            standardSportMarketMap.entrySet().forEach(entry -> {
                OddsWrapper<ThirdMarketDTO> wrapper = finalLinkMarketDTOMap1.get(entry.getKey());
                if (wrapper.getIsOutRight()) {
                    StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(wrapper.getStandardSourceId());
                    String autoSellStatus = "Yes".equals(standardMatchInfo.getAutoSellStatus()) ? SellStatusEnum.SOLD.value : SellStatusEnum.UNSOLD.value;
                    entry.getValue().forEach(v -> {
                        if (null != v.getRelationMarketId()) {
                            StandardOutrightMarket standardOutrightMarket = new StandardOutrightMarket();
                            standardOutrightMarket.setId(v.getRelationMarketId());
                            standardOutrightMarket.setStandardMatchId(standardMatchInfo.getId());
                            standardOutrightMarket.setMarketCategoryId(v.getMarketCategoryId());
                            standardOutrightMarket.setMarketStatus(v.getStatus());
                            standardOutrightMarket.setNameCode(v.getNameCode());
                            standardOutrightMarket.setLinkId(entry.getKey());
                            standardOutrightMarket.setMarketSellStatus(autoSellStatus);
                            standardOutrightMarketList.add(standardOutrightMarket);
                        }
                    });
                }
            });
            if (!CollectionUtils.isEmpty(standardOutrightMarketList)) {
                standardOutrightMarketService.saveBatch(standardOutrightMarketList);
            }

            storeDataSW.stop();
            storeDataSW.start("更新赛事玩法赔率最新更新时间耗时");
            //标准赛事不存在或者赛事未开售，赔率不下发
            thirdMarketDTOS = thirdMarketDTOS.stream().filter(t -> t.getStandardSourceId() != null && standardSportMarketSellMap.containsKey(t.getStandardSourceId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(thirdMarketDTOS)) {
                return;
            }
            //基于标准赛事不存在或者赛事未开售来过滤
            linkMarketDTOMap = thirdMarketDTOS.stream().collect(Collectors.toMap(OddsWrapper::getLinkId, Function.identity(), (v1, v2) -> v1));
            Map<String, OddsWrapper<ThirdMarketDTO>> finalLinkMarketDTOMap = linkMarketDTOMap;
            thirdSportMarketMessagesMap = thirdSportMarketMessagesMap.entrySet().stream().filter(t -> finalLinkMarketDTOMap.containsKey(t.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            thirdMarketDTONotLock = thirdMarketDTONotLock.stream().filter(t -> finalLinkMarketDTOMap.containsKey(t.getLinkId())).collect(Collectors.toList());
            standardMarketDataMessageMap = standardMarketDataMessageMap.entrySet().stream().filter(t -> finalLinkMarketDTOMap.containsKey(t.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            //更新赛事玩法赔率最新更新时间 1848
            Map<String, Set<Long>> finalMarketCategoryIdSetMap1 = marketCategoryIdSetMap;
            if (!CollectionUtils.isEmpty(linkMarketDTOMap)) {
                List<String> matchIds = linkMarketDTOMap.values().stream().map(t -> Constant.REDIS_KEY.RONGHE_MATCH_CURRENT_PERIODID + t.getStandardSourceId()).distinct().collect(Collectors.toList());
                List<Object> values = redisService.mGet(matchIds);
                Map<String, Object> matchValueMap = new ConcurrentHashMap<>();
                for (int i = 0; i < matchIds.size(); i++) {
                    if(null == values.get(i)){
                        continue;
                    }
                    matchValueMap.put(matchIds.get(i), values.get(i));
                }
                linkMarketDTOMap.entrySet().forEach(entry -> {
                    OddsWrapper<ThirdMarketDTO> wrapper = entry.getValue();
                    StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(wrapper.getStandardSourceId());
                    String key = Constant.REDIS_KEY.RONGHE_MATCH_CURRENT_PERIODID + standardMatchInfo.getId();
                    Long matchPeriodId = (Long) matchValueMap.get(key);
                    if (matchPeriodId == null) {//没有阶段数据的时候，默认为未开赛
                        matchPeriodId = 0l;
                    }
                    standardMarketOddsProducer.thirdCategoryOddsUpdateTimeSend(entry.getKey(), standardMatchInfo.getSportId(), standardMatchInfo.getId(), matchPeriodId, finalMarketCategoryIdSetMap1.get(entry.getKey()), wrapper.getDataSourceTime(), wrapper.getMarketType());
                });
            }
            storeDataSW.stop();
            storeDataSW.start("sendMessageToRisk耗时");
            if (!CollectionUtils.isEmpty(thirdSportMarketMessagesMap)) {
                thirdSportMarketMessagesMap.entrySet().forEach(entry -> {
                    OddsWrapper<ThirdMarketDTO> dto = finalLinkMarketDTOMap.get(entry.getKey());
                    commonAsyncService.sendMessageToRisk(entry.getKey() + "_third", standardMatchInfoMap.get(dto.getStandardSourceId()), entry.getValue());
                });
            }

            storeDataSW.stop();
            storeDataSW.start("本次有改变的盘口为空，赔率不下发耗时");
            //本次有改变的盘口为空，赔率不下发
            Map<String, List<StandardMarketDataMessage>> finalStandardMarketDataMessageMap = standardMarketDataMessageMap;
            Map<String, Long> filteredLinkIdMap = thirdMarketDTOS.stream().filter(t -> !finalStandardMarketDataMessageMap.containsKey(t.getLinkId())).collect(Collectors.toMap(OddsWrapper::getLinkId, OddsWrapper::getStandardSourceId, (v1, v2) -> v1));
            if (!CollectionUtils.isEmpty(filteredLinkIdMap)) {
                thirdMarketDTOS = thirdMarketDTOS.stream().filter(t -> !filteredLinkIdMap.containsKey(t.getLinkId())).collect(Collectors.toList());
            }
            storeDataSW.stop();
            storeDataSW.start("收到滚球赔率下发赛事滚球标识，并下发赛前关盘耗时");
            //-------------------收到滚球赔率下发赛事滚球标识，并下发赛前关盘--------------------
            thirdMarketDTOS.forEach(t -> {
                StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(t.getStandardSourceId());
                Long dataSourceTimeNew = thirdMatchMarketProcessor.newClosePreMarkets(t.getLinkId(), standardSportMarketSellMap.get(t.getStandardSourceId()), t.getMarketType(), standardMatchInfo, t.getDataSourceTime(), true, new ArrayList<>(), 0);
                t.setDataSourceTime(dataSourceTimeNew);
            });
            storeDataSW.stop();
            log.info("::{}::所有数据存储耗时{}ms," + storeDataSW.prettyPrint(), uuid, storeDataSW.getTotalTimeMillis());
        } catch (Throwable e) {
            log.error("::" + uuid + ":: accessMatchMarketData 出现异常，Throwable ", e);
            e.printStackTrace();
        } finally {
            swRedis.stop();
        }

        log.info("::{}:: accessMatchMarketData 需要加锁的盘口size:{}", uuid, thirdMarketDTOLock.size());

        swRedis.start("操盘数据开始处理");
        //冠军盘处理
        Map<String, Map<String, StandardMarketDataMessage>> standardMarketMessageMap = new ConcurrentHashMap<>();
        List<Long> standardMatchInfoIdsWithOutright = thirdMarketDTOS.stream().filter(OddsWrapper::getIsOutRight).map(OddsWrapper::getStandardSourceId).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(standardMatchInfoIdsWithOutright)) {
            List<StandardOutrightMarket> outrightMarketList = standardOutrightMarketService.selectOutrightMarketSellListByIds(standardMatchInfoIdsWithOutright);
            Map<Long, List<StandardOutrightMarket>> outrightMarketMap = outrightMarketList.stream().collect(Collectors.groupingBy(StandardOutrightMarket::getStandardMatchId));
            log.info("::{}::accessMatchMarketData-champion-outrightMarketSellList-size:{},outrightMarketMap-keys:{}", uuid, outrightMarketList.size(), outrightMarketMap.keySet());
            List<OddsWrapper<ThirdMarketDTO>> outRightThirdMarketDTOS = thirdMarketDTOS.stream().filter(t -> t.getIsOutRight() && outrightMarketMap.containsKey(t.getStandardSourceId())).collect(Collectors.toList());
            log.info("::{}::accessMatchMarketData-champion-outRightThirdMarketDTOS-size:{}", uuid, outRightThirdMarketDTOS.size());
            List<String> redisKeys = outRightThirdMarketDTOS.stream().map(t -> Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + t.getStandardSourceId() + "_" + t.getDataSourceCode()).collect(Collectors.toList());
            //过滤掉是冠军赛事同时standardOutrightMarketService返回值为空的数据
            thirdMarketDTOS = thirdMarketDTOS.stream().filter(t -> t.getIsOutRight() && outrightMarketMap.containsKey(t.getStandardSourceId())).collect(Collectors.toList());
            log.info("::{}::accessMatchMarketData-champion-thirdMarketDTOS-size:{},redisKeys:{}", uuid, thirdMarketDTOS.size(), redisKeys);
            Map<String, Map<String, Object>> standardMarketMessageMapCache = redisService.syncObtainMultiGetAllWithoutMerge(redisKeys);
            for (int i = 0; i < redisKeys.size(); i++) {
                OddsWrapper<ThirdMarketDTO> wrapper = outRightThirdMarketDTOS.get(i);
                Set<Long> marketIdWithOutrightSet = outrightMarketMap.get(wrapper.getStandardSourceId()).stream().map(StandardOutrightMarket::getId).collect(Collectors.toSet());
                Map<String, Object> item = standardMarketMessageMapCache.get(redisKeys.get(i));
                if (item == null) {
                    log.warn("::{}::accessMatchMarketData-champion-redis-miss,linkId:{},redisKey:{}", uuid, wrapper.getLinkId(), redisKeys.get(i));
                    continue;
                }
                Map<String, StandardMarketDataMessage> itemMap = item.entrySet().stream().filter(map -> marketIdWithOutrightSet.contains(((StandardMarketDataMessage) map.getValue()).getRelationMarketId())).collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> (StandardMarketDataMessage) e.getValue()));
                log.info("::{}::accessMatchMarketData-champion-standardMarketMessageMap,linkId:{},itemMap-size:{}", uuid, wrapper.getLinkId(), itemMap.size());
                standardMarketMessageMap.put(wrapper.getLinkId(), itemMap);
            }
        }

        //检查是否有绑定测试赛事
        Map<String, Set<Long>> finalMarketCategoryIdSetMap2 = marketCategoryIdSetMap;
        List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOSForTest = thirdMarketDTOS.stream().filter(t -> !finalMarketCategoryIdSetMap2.containsKey(t.getLinkId())).collect(Collectors.toList());
        thirdMarketDTOS = thirdMarketDTOS.stream().filter(t -> finalMarketCategoryIdSetMap2.containsKey(t.getLinkId())).collect(Collectors.toList());
        Map<String, Long> filteredLinkIdMap = thirdMarketDTOSForTest.stream().collect(Collectors.toMap(OddsWrapper::getLinkId, OddsWrapper::getStandardSourceId, (v1, v2) -> v1));
        //checkTestMatch(filteredLinkIdMap, standardMatchInfoMap, requestMap, thirdMarketDTOListMap);

        //-------------------操盘，直接下发赔率（4405：同场按玩法级混合操盘）--------------------
        List<OddsWrapper<ThirdMarketDTO>> outrightMarketDTOs = Collections.synchronizedList(new ArrayList());
        Map<String, OddsWrapper<ThirdMarketDTO>> normalMarketDTOsMap = new ConcurrentHashMap<>();
        for (OddsWrapper<ThirdMarketDTO> thirdMatchMarketDTO : thirdMarketDTOS) {
            if (thirdMatchMarketDTO.getMarketType() == 2) {
                outrightMarketDTOs.add(thirdMatchMarketDTO);
            } else {
                normalMarketDTOsMap.put(thirdMatchMarketDTO.getLinkId(), thirdMatchMarketDTO);
            }
        }
        //------------------------直接下发赔率-----------------------
        //-------------------冠军操盘，直接下发赔率--------------------
        if (!CollectionUtils.isEmpty(outrightMarketDTOs)) {
            log.info("::{}:: accessMatchMarketData 冠军操盘，直接下发赔率: size:{}", uuid, outrightMarketDTOs.size());
            batchProcessOddsByOutright(outrightMarketDTOs, standardMatchInfoMap, marketIdSetMap, standardMarketMessageMap, changeCategoryOddsTypeMap);
        }
        //-------------------按玩法级分组：统一走 processOddsByAll（内部再分 MTS/Panda）--------------------
        if (!CollectionUtils.isEmpty(normalMarketDTOsMap)) {
            log.info("::{}:: accessMatchMarketData 混合操盘，直接下发赔率: size:{}", uuid, normalMarketDTOsMap.size());
            List<CompletableFuture<?>> futures = new ArrayList<>();
            TaskExecutor taskExecutor = threadPoolConfig.getProcessOddsByPandaThreadPool();
            Map<String, Set<Long>> finalMarketCategoryIdSetMap3 = marketCategoryIdSetMap;
            Map<String, Map<String, StandardMarketDataMessage>> finalStandardCategoryMarketMessageMap1 = standardCategoryMarketMessageMap;
            for (Map.Entry<String, Set<Long>> entry : marketCategoryIdSetMap.entrySet()) {
                String linkId = entry.getKey();
                OddsWrapper<ThirdMarketDTO> dto = normalMarketDTOsMap.get(linkId);
                if (dto == null) {
                    continue;
                }
                Long dataSourceTime = dto.getDataSourceTime();
                Long matchId = dto.getStandardSourceId();
                String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + matchId + "_" + isOddsLive(matchId);
                Map<String, String> marketCategorySellMap = redisService.hGetAll(categoryRedisKey);
                StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(matchId);
                Set<Long> setAdd = Collections.synchronizedSet(new HashSet());
                for (Long marketCategoryId : entry.getValue()) {
                    if (MarginCategoryConfig.HANDICAP_CATEGORY_SUBSECTION.contains(marketCategoryId)) {
                        if (null == marketCategorySellMap.get(String.valueOf(marketCategoryId))) {
                            continue;
                        }
                        //如果篮球这些玩法都是非开盘的，那么对应的玩法就没必要再添加到后续逻辑里面
                        String redisKeyOld = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + matchId + "_" + marketCategorySellMap.get(String.valueOf(marketCategoryId)) + "_" + marketCategoryId);
                        Map<String, StandardMarketDataMessage> standardMarketOldCache = redisService.hGetAll(redisKeyOld);
                        boolean hasActive = standardMarketOldCache.values().stream().anyMatch(e -> e.getStatus() == Constant.SPORT_MARKET.STATUS.ACTIVE);
                        if (!hasActive) {
                            continue;
                        }
                        Long addCategoryId = MarginCategoryConfig.HANDICAP_WINNER_MAP.get(marketCategoryId);
                        String sellCategoryDataSourceCode = marketCategorySellMap.get(String.valueOf(addCategoryId));
                        setAdd.add(addCategoryId);
                        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + matchId + "_" + sellCategoryDataSourceCode + "_" + addCategoryId);
                        Map<String, StandardMarketDataMessage> standardMarketAddCache = redisService.hGetAll(redisKey);
                        if (MapUtils.isNotEmpty(standardMarketAddCache)) {
                            if (standardCategoryMarketMessageMap.containsKey(linkId)) {
                                standardCategoryMarketMessageMap.get(linkId).putAll(standardMarketAddCache);
                            } else {
                                Map<String, StandardMarketDataMessage> tempMap = new ConcurrentHashMap<>();
                                tempMap.putAll(standardMarketAddCache);
                                standardCategoryMarketMessageMap.put(linkId, tempMap);
                            }
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(setAdd)) {
                    //兼容历史数据
                    marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardCategoryMarketMessageMap.getOrDefault(linkId, new HashMap<>()), standardMatchInfo, setAdd, null, null, null, false);
                    if (marketCategoryIdSetMap.containsKey(linkId)) {
                        marketCategoryIdSetMap.get(linkId).addAll(setAdd);
                    } else {
                        Set<Long> tempSet = Collections.synchronizedSet(new HashSet());
                        tempSet.addAll(setAdd);
                        marketCategoryIdSetMap.put(linkId, tempSet);
                    }
                }
                futures.add(CompletableFuture.supplyAsync(() -> {
                    thirdMatchMarketProcessor.processOddsByAll(
                            linkId,
                            0,
                            null,
                            standardMatchInfo,
                            finalMarketCategoryIdSetMap3.getOrDefault(linkId, new HashSet<>()),
                            finalStandardCategoryMarketMessageMap1.getOrDefault(linkId, new HashMap<>()),
                            dataSourceTime,
                            standardSportMarketSellMap.get(matchId),
                            changeCategoryOddsTypeMap.getOrDefault(linkId, new HashMap<>())
                    );
                    return null;
                }, taskExecutor));
            }
            //等待盘口异步处理
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        // 发送风险玩法集合到风控
        if (!CollectionUtils.isEmpty(riskCategoryMap)) {
            log.info("::{}:: accessMatchMarketData 发送风险玩法集合到风控: size:{}", uuid, riskCategoryMap.size());
            dataMerchantBaffleProducer.batchSendCategoryListToRiskMQ(thirdMarketDTOS, standardMatchInfoMap, standardSportMarketSellMap, riskCategoryMap, thirdMatchInfoBasedIdMap, 3);
        }
        //检查是否是测试联赛
        filteredLinkIdMap = thirdMarketDTOS.stream().collect(Collectors.toMap(OddsWrapper::getLinkId, OddsWrapper::getStandardSourceId, (v1, v2) -> v1));
        //checkTestMatch(filteredLinkIdMap, standardMatchInfoMap, requestMap, thirdMarketDTOListMap);
        swRedis.stop();
        log.info("::{}:: uuid: {} accessMatchMarketData主流程处理耗时{}ms," + swRedis.prettyPrint(), linkIds, uuid, swRedis.getTotalTimeMillis());
        //统计处理耗时
        paDataServiceLogProducer.sendPaDataServiceLog(getPaDataServiceLogDTO(linkIds, "odds-admin", "THIRD_MATCH_MARKET_API", "商业数据源赔率主流程", swRedis.getTotalTimeMillis(), 200, null));
        log.info("::{}:: uuid:{} accessMatchMarketData end！", linkIds, uuid);
    }

    /**
     * SR/BG/BC 根据三方盘口ID缓存最新数据
     * TX 根据玩法 缓存每个坑位最新数据
     *
     * @param thirdMatchNotExistRequest
     */
    @Async("ProcessOddsByPandaThreadPool")
    void thirdMatchNotExistCacheMarket(List<OddsWrapper<ThirdMatchMarketDTO>> thirdMatchNotExistRequest) {
        List<String> linkIds = thirdMatchNotExistRequest.stream().map(OddsWrapper::getLinkId).collect(Collectors.toList());
        log.error("[ThirdMatchMarketProcessor] thirdMatchNotExistCacheMarket 三方赛事不存在linkIds: {}", linkIds);
        for (OddsWrapper<ThirdMatchMarketDTO> request : thirdMatchNotExistRequest) {
            String thirdMatchSourceId = request.getThirdMatchSourceId();
            String dataSourceCode = request.getDataSourceCode();
            //缓存KEY
            String thirdMarketKey = Constant.REDIS_KEY.RONGHE_THIRD_MARKET + thirdMatchSourceId + "_" + dataSourceCode;
            List<ThirdMarketDTO> marketDTOS = request.getData().getMarketList();

            if (DataSourceCodeEnum.TX.code.equals(dataSourceCode)) {
                Map<String, List<ThirdMarketDTO>> thirdMarketDTOMap = marketDTOS.stream().collect(Collectors.groupingBy(ThirdMarketDTO::getThirdMarketCategorySourceId));
                List<String> thirdMarketCategorySourceIds = thirdMarketDTOMap.keySet().stream().collect(Collectors.toList());
                List<Object> values = redisService.hMulGet(thirdMarketKey, thirdMarketCategorySourceIds);
                for (int i = 0; i < values.size(); i++) {
                    Map<Integer, ThirdMarketDTO> categoryPlaceMap = new ConcurrentHashMap<>();
                    if (!Objects.isNull(values.get(i))) {
                        categoryPlaceMap = (Map<Integer, ThirdMarketDTO>) values.get(i);
                    }
                    List<ThirdMarketDTO> thirdMarketDTOs = thirdMarketDTOMap.get(thirdMarketCategorySourceIds.get(i));
                    for (ThirdMarketDTO inner : thirdMarketDTOs) {
                        Integer offerLineId = inner.getOfferLineId();
                        categoryPlaceMap.put(offerLineId, inner);
                    }
                    redisService.hSet(thirdMarketKey, thirdMarketCategorySourceIds.get(i), categoryPlaceMap, RedisConfig.REDIS_HOUR_TIME);
                }
            } else {
                for (ThirdMarketDTO marketDTO : marketDTOS) {
                    redisService.hSet(thirdMarketKey, marketDTO.getThirdMarketSourceId(), marketDTO, RedisConfig.REDIS_HOUR_TIME);
                }
            }
        }
        log.error("[ThirdMatchMarketProcessor] thirdMatchNotExistCacheMarket 三方赛事不存在,完成盘口缓存.");
    }


    public List<ThirdMatchInfo> getThirdMatchInfo(List<OddsWrapper<ThirdMatchMarketDTO>> validRequest) {
        Map<Boolean, List<OddsWrapper<ThirdMatchMarketDTO>>> marketMap = validRequest.stream().collect(Collectors.groupingBy(t -> t.getIsOutRight()));
        //冠军赛事
        List<ThirdOutrightMatchInfo> thirdOutrightMatchInfos = thirdOutrightMatchInfoService.getItems(marketMap.get(Boolean.TRUE));
        List<ThirdMatchInfo> outrightNormalizedMatchInfo = thirdMatchMarketConverter.convertOutrightToThirdMatchInfo(thirdOutrightMatchInfos);
        //常规赛事
        List<ThirdMatchInfo> regularNormalizedMatchInfo = thirdMatchInfoService.getItemsByMarketDTO(marketMap.get(Boolean.FALSE));
        //兼容缓存覆盖问题
        List<Long> remainedIds = regularNormalizedMatchInfo.stream().filter(t -> {
            if (t.getReferenceId() == null || t.getReferenceId() == 0) {
                return true;
            } else {
                outrightNormalizedMatchInfo.add(t);
                return false;
            }
        }).map(ThirdMatchInfo::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(remainedIds)) {
            List<ThirdMatchInfo> remainedMatchInfos = thirdMatchInfoService.getItems(remainedIds);
            outrightNormalizedMatchInfo.addAll(remainedMatchInfos);
        }
        return outrightNormalizedMatchInfo;
    }

    public List<StandardMatchInfoDetail> getStandardMatchInfo(List<OddsWrapper<ThirdMatchMarketDTO>> validRequest) {
        Map<Boolean, List<OddsWrapper<ThirdMatchMarketDTO>>> marketMap = validRequest.stream().filter(t -> null != t.getStandardSourceId() && 0 != t.getStandardSourceId()).collect(Collectors.groupingBy(t -> t.getIsOutRight()));
        List<Long> outRightMatcheIds = marketMap.getOrDefault(Boolean.TRUE, Collections.emptyList()).stream().map(OddsWrapper::getStandardSourceId).distinct().collect(Collectors.toList());
        List<Long> regularMatcheIds = marketMap.getOrDefault(Boolean.FALSE, Collections.emptyList()).stream().map(OddsWrapper::getStandardSourceId).distinct().collect(Collectors.toList());
        //冠军赛事
        List<StandardOutrightMatchInfo> standardOutrightMatchInfos = standardOutrightMatchInfoService.getItems(outRightMatcheIds);
        List<StandardMatchInfoDetail> standardOutrightMatchInfoDetails = thirdMatchMarketConverter.convertOutrightToStandardDetails(standardOutrightMatchInfos);
        //常规赛事
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(regularMatcheIds);
        List<StandardMatchInfoDetail> standardMatchInfoDetails = thirdMatchMarketConverter.convertstandardToStandardDetails(standardMatchInfos);
        standardOutrightMatchInfoDetails.addAll(standardMatchInfoDetails);
        return standardOutrightMatchInfoDetails;
    }

    public List<StandardSportMarketSell> getStandardSportMarketSell(List<OddsWrapper<ThirdMatchMarketDTO>> validRequest) {
        Map<Boolean, List<OddsWrapper<ThirdMatchMarketDTO>>> marketMap = validRequest.stream().filter(t -> t.getStandardSourceId() != null).collect(Collectors.groupingBy(t -> t.getIsOutRight()));
        List<Long> outRightMatcheIds = marketMap.getOrDefault(Boolean.TRUE, Collections.emptyList()).stream().map(OddsWrapper::getStandardSourceId).collect(Collectors.toList());
        List<Long> regularMatcheIds = marketMap.getOrDefault(Boolean.FALSE, Collections.emptyList()).stream().map(OddsWrapper::getStandardSourceId).collect(Collectors.toList());
        //冠军赛事
        List<StandardOutrightMatchInfo> standardOutrightMatchInfo = standardOutrightMatchInfoService.getItems(outRightMatcheIds);
        List<StandardSportMarketSell> outrightSportMarketSells = thirdMatchMarketConverter.convertOutrightToStandardSportMarketSell(standardOutrightMatchInfo);
        //常规赛事
        List<StandardSportMarketSell> sportMarketSells = standardSportMarketSellService.getItems(regularMatcheIds);
        outrightSportMarketSells.addAll(sportMarketSells);
        return outrightSportMarketSells;
    }


    public List<StandardMarketDataMessage> processStandardSportMarket(List<OddsWrapper<ThirdMarketDTO>> oddsWrappers, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap,
                                                                      Map<Long, StandardSportMarketSell> standardSportMarketSellMap, Map<String, List<StandardSportMarket>> standardSportMarketListMap,
                                                                      Map<String, Set<Long>> oddsTypeIdSetMap, Map<String, Set<Long>> categorySetMap, Map<String,
            Map<Long, List<String>>> changeCategoryOddsTypeMap, Map<String, List<StandardSportMarketOdds>> standardSportMarketOddsUpdateMap, Pair<Map<String, Object>, Map<String, MarketCategorySell>> allMarketCategorySell) {
        if (CollectionUtils.isEmpty(oddsWrappers)) {
            return Collections.emptyList();
        }
        Long uuid = oddsWrappers.get(0).getUuid();
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("标准盘口赛种玩法耗时");
        //判断赛事是否为空和兼容冠军玩法
        List<OddsWrapper<ThirdMarketDTO>> validData = oddsWrappers.stream().filter(t -> {
            if (t.getStandardSourceId() == null) {
                return false;
            }
            StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(t.getStandardSourceId());
            if (standardMatchInfo == null || MatchStatusEnum.Closed.value.equals(standardMatchInfo.getMatchStatus()) || MatchStatusEnum.Ended.value.equals(standardMatchInfo.getMatchStatus())) {
                return false;
            }
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellMap.get(t.getStandardSourceId());
            if (standardSportMarketSell == null && !t.getIsOutRight()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        //获取赛种玩法,判断当前赛种是否支持玩法
        List<Pair<Long, Long>> standardCategories = validData.stream().map(t -> Pair.of(t.getMarketCategoryId(), standardMatchInfoMap.get(t.getStandardSourceId()).getSportId())).collect(Collectors.toList());
        List<StandardSportMarketCategory> standardSportMarketCategories = standardSportMarketCategoryService.getItemsByStandardCategories(standardCategories);
        Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap = standardSportMarketCategories.stream().collect(Collectors.toMap(t -> t.getMarketCategoryId() + "-" + t.getSportId(), Function.identity(), (v1, v2) -> v1));

        validData.forEach(t -> {
            String key = t.getMarketCategoryId() + "-" + standardMatchInfoMap.get(t.getStandardSourceId()).getSportId();
            StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryMap.get(key);
            if (standardSportMarketCategory == null || Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getStatus()) || (t.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.AO.code) && Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getAoStatus()))) {
                t.getData().setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            }
            t.setIsCheckOdds(false);
            t.setHasRecord(true);
        });
        sw.stop();

        sw.start("标准盘口开售环节耗时");
        //新赛种暂时过滤开售环节
        validData = validData.stream().filter(inner -> doFilterForMarketCategorySell(inner, allMarketCategorySell, standardSportMarketSellMap)).collect(Collectors.toList());
        sw.stop();

        sw.start("标准盘口获取盘口耗时");
        //处理标准盘口信息，不存在新增，存在更新
        List<StandardSportMarket> standardSportMarkets = standardSportMarketService.getItems(validData);
        Map<String, StandardSportMarket> standardSportMarketMap = standardSportMarkets.stream().collect(Collectors.toMap(t -> t.getStandardMatchInfoId() + "-" + t.getThirdMarketSourceId() + "-" + t.getDataSourceCode(), Function.identity(), (v1, v2) -> v1));
        sw.stop();

        sw.start("标准盘口处理盘口耗时");
        List<StandardSportMarket> insertStandardSportMarket = Collections.synchronizedList(new ArrayList());
        List<StandardSportMarket> updateStandardSportMarket = Collections.synchronizedList(new ArrayList());
        List<I18nOutrightMarket> insertI18nOutrightMarket = Collections.synchronizedList(new ArrayList());
        List<StandardSportMarket> championMarketList = Collections.synchronizedList(new ArrayList());
        validData = validData.stream().filter(t -> {
            String key = t.getStandardSourceId() + "-" + t.getData().getThirdMarketSourceId() + "-" + t.getDataSourceCode();
            StandardSportMarket standardSportMarket = standardSportMarketMap.get(key);
            ThirdMarketDTO thirdMarketDTO = t.getData();
            String categoryKey = t.getMarketCategoryId() + "-" + standardMatchInfoMap.get(t.getStandardSourceId()).getSportId();
            StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryMap.get(categoryKey);
            if (standardSportMarket == null) {
                if (null != standardSportMarketCategory && standardSportMarketCategory.getStatus() != 0) {
                    standardSportMarket = new StandardSportMarket();
                    BeanUtils.copyProperties(thirdMarketDTO, standardSportMarket);
                    standardSportMarket.setMarketCategoryId(standardSportMarketCategory.getMarketCategoryId());
                    standardSportMarket.setStandardMatchInfoId(t.getStandardSourceId());
                    if (Integer.valueOf(2).equals(thirdMarketDTO.getMarketType())) {
                        Integer normalizedStatus = Integer.valueOf(0).equals(thirdMarketDTO.getStatus())
                                ? Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE
                                : Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED;
                        standardSportMarket.setThirdMarketSourceStatus(normalizedStatus);
                        standardSportMarket.setStatus(normalizedStatus);
                    } else {
                        standardSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
                        standardSportMarket.setStatus(thirdMarketDTO.getStatus());
                    }
                    standardSportMarket.setThirdMarketSourceId(thirdMarketDTO.getThirdMarketSourceId());
                    standardSportMarket.setNumberOfWinners(thirdMarketDTO.getNumberOfWinners());
                    standardSportMarket.setId(UUIdUtils.getId());
                    standardSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
                    standardSportMarket.setScopeId(standardSportMarketCategory.getScopeId());
                    standardSportMarket.setTradeType(0);
                    standardSportMarket.setLinkId(t.getLinkId());
                    standardSportMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    standardSportMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    //tx的修改时间必须严格使用上游的修改时间
                    if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode())) {
                        standardSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
                    }
                    standardSportMarket.setNameCode(UUIdUtils.getId());
                    //标准冠军盘口名称多语言处理
                    if (2 == standardSportMarket.getMarketType() && !CollectionUtils.isEmpty(thirdMarketDTO.getI18nNames())) {
                        StandardSportMarket finalStandardSportMarket = standardSportMarket;
                        thirdMarketDTO.getI18nNames().forEach(i18nItemDTO -> {
                            I18nOutrightMarket i18nOutrightMarket = new I18nOutrightMarket();
                            BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarket);
                            i18nOutrightMarket.setFlag(2);
                            i18nOutrightMarket.setNameCode(finalStandardSportMarket.getNameCode());
                            i18nOutrightMarket.setDataSourceCode(finalStandardSportMarket.getDataSourceCode());
                            insertI18nOutrightMarket.add(i18nOutrightMarket);
                        });
                    }
                    t.setStandardSportMarketId(standardSportMarket.getId());
                    insertStandardSportMarket.add(standardSportMarket);
                    return true;
                }
                return false;
            } else {
                //处理盘口移交状态,当前盘口是滚球还是赛前，赛前就关盘，滚球的话这个handover就忽略
                if (Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(standardSportMarket.getMarketType()) && t.getData().getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)) {
                    t.getData().setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }
                if (Constant.SPORT_MARKET.MARKET_TYPE.LIVE_ODD_BUSINESS.equals(standardSportMarket.getMarketType()) && Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(t.getMarketType()) && t.getData().getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)) {
                    return false;
                }
                Long lastModifyTime = standardSportMarket.getModifyTime();
                //现在三方盘口时间戳原子操作，可以不用这段代码
                /*if(null != lastModifyTime && null != thirdMarketDTO.getModifyTime()){
                    //最新时间戳 小于 标准盘口时间戳，不处理
                    if (thirdMarketDTO.getModifyTime() < lastModifyTime) {
                        log.info("::{}::标准盘口:{},最新时间戳小于标准盘口时间戳，不处理:{}-{}", uuid, standardSportMarket.getRelationMarketId(), lastModifyTime, thirdMarketDTO.getModifyTime());
                        return false;
                    }
                }*/
                //新的盘口状态跟旧的盘口状态都是开才需要校验
                if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(t.getData().getStatus()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardSportMarket.getThirdMarketSourceStatus())) {
                    t.setIsCheckOdds(true);
                }
                standardSportMarket.setModifyTime(null == thirdMarketDTO.getModifyTime() ? TimeUtils.millsSecondsEast8ZoneGmt() : thirdMarketDTO.getModifyTime());
                if (null != standardSportMarketCategory) {
                    standardSportMarket.setThirdMarketSourceStatus(standardSportMarketCategory.getStatus() == 0 ? Constant.SPORT_MARKET.STATUS.DEACTIVATED : thirdMarketDTO.getStatus());
                    standardSportMarket.setStatus(standardSportMarketCategory.getStatus() == 0 ? Constant.SPORT_MARKET.STATUS.DEACTIVATED : thirdMarketDTO.getStatus());
                }
                standardSportMarket.setMarketType(thirdMarketDTO.getMarketType());
                standardSportMarket.setLinkId(t.getLinkId());
                standardSportMarket.setOddsName(thirdMarketDTO.getOddsName());
                standardSportMarket.setAddition1(thirdMarketDTO.getAddition1());
                standardSportMarket.setAddition2(thirdMarketDTO.getAddition2());
                standardSportMarket.setAddition3(thirdMarketDTO.getAddition3());
                standardSportMarket.setAddition4(thirdMarketDTO.getAddition4());
                standardSportMarket.setNumberOfWinners(thirdMarketDTO.getNumberOfWinners());
                standardSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
                //TX旧数据重新赋值盘口ID
                if (standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.TX.code) && (StringUtils.isBlank(standardSportMarket.getSendData()) || "''".equals(standardSportMarket.getSendData()))) {
                    standardSportMarket.setRelationMarketId(Long.valueOf(standardSportMarketService.txCreateRelationMarketId(standardSportMarket.getThirdMarketSourceId())));
                    standardSportMarket.setSendData(standardSportMarketService.createRelationMarketId(t.getLinkId(), standardSportMarket).toString());
                }
                if (Integer.valueOf(2).equals(standardSportMarket.getMarketType())) {
                    Integer normalizedStatus = Integer.valueOf(0).equals(thirdMarketDTO.getStatus())
                            ? Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE
                            : Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED;
                    standardSportMarket.setThirdMarketSourceStatus(normalizedStatus);
                    standardSportMarket.setStatus(normalizedStatus);
                }
                //异步执行更新
                t.setStandardSportMarketId(standardSportMarket.getId());
                updateStandardSportMarket.add(standardSportMarket);

            }
            if ( 2 == standardSportMarket.getMarketType() && null != standardSportMarket.getRelationMarketId() ) {
                championMarketList.add(standardSportMarket);
            }
            return true;
        }).collect(Collectors.toList());
        sw.stop();

        if ( !CollectionUtils.isEmpty(championMarketList) ) {
            List<Long> championIds = championMarketList.stream().map( StandardSportMarket::getRelationMarketId).collect(Collectors.toList());
            log.info("::{}::dataSourceChampionMarketExecute-执行的冠军盘id:{}", uuid, championIds);
            List<CompletableFuture<?>> championFuture = new ArrayList<>();
            championFuture.add(CompletableFuture.supplyAsync(() -> {
                dataSourceChampionMarketExecute( uuid, championMarketList);
                return null;
            }, championMarketExecutor));
            CompletableFuture.allOf(championFuture.toArray(new CompletableFuture[0])).join();
        }

        sw.start("标准盘口insertStandardSportMarket耗时");
        //保存insertStandardSportMarket
        if (!CollectionUtils.isEmpty(insertStandardSportMarket)) {
            log.info("::{}:: 标准盘口create入库standard_sport_market_insert data:{}", uuid, insertStandardSportMarket);
            List<String> thirdMarketSourceIds = insertStandardSportMarket.stream().map(StandardSportMarket::getThirdMarketSourceId).collect(Collectors.toList());
            List<Object> txRelationMarketIds = standardSportMarketService.txCreateRelationMarketIds(thirdMarketSourceIds);
            List<Object> relationMarketIds = standardSportMarketService.createRelationMarketIds(insertStandardSportMarket);
            for (int i = 0; i < insertStandardSportMarket.size(); i++) {
                if (insertStandardSportMarket.get(i).getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) {
                    insertStandardSportMarket.get(i).setRelationMarketId(Long.valueOf(txRelationMarketIds.get(i).toString()));
                    insertStandardSportMarket.get(i).setSendData(relationMarketIds.get(i).toString());
                } else {
                    insertStandardSportMarket.get(i).setRelationMarketId(Long.valueOf(relationMarketIds.get(i).toString()));
                }
            }
            //发送mq新增
            standardSportMarketListMap = insertStandardSportMarket.stream().collect(Collectors.groupingBy(StandardSportMarket::getLinkId));
            for (Map.Entry<String, List<StandardSportMarket>> item : standardSportMarketListMap.entrySet()) {
                marketDbProducer.sendStandardMarketInsertInfo(item.getKey(), item.getValue());
            }
        }
        sw.stop();
        sw.start("标准盘口updateStandardSportMarket耗时");
        //更新updateStandardSportMarket
        if (!CollectionUtils.isEmpty(updateStandardSportMarket)) {
            //判断该盘口是否已经人工编辑
            log.info("::{}:: 标准盘口update入库standard_sport_market_update data:{}", uuid, updateStandardSportMarket);
            List<String> relationMarketIds = updateStandardSportMarket.stream().map(t -> ConstantSystem.CHAMPION_CACHE + t.getRelationMarketId()).collect(Collectors.toList());
            List<Object> relationMarketValues = redisService.mGet(relationMarketIds);
            Map<Long, List<OddsWrapper<ThirdMarketDTO>>> validDataMap = validData.stream().collect(Collectors.groupingBy(t -> t.getStandardSportMarketId()));
            for (int i = 0; i < updateStandardSportMarket.size(); i++) {
                if (!Objects.isNull(relationMarketValues.get(i))) {
                    validDataMap.get(updateStandardSportMarket.get(i).getId()).forEach(t -> t.setHasRecord(false));
                    StandardSportMarket cacheStandardSportMarket = (StandardSportMarket) relationMarketValues.get(i);
                    updateStandardSportMarket.get(i).setAddition2(cacheStandardSportMarket.getAddition2());
                    updateStandardSportMarket.get(i).setAddition3(cacheStandardSportMarket.getAddition3());
                }
//                updateStandardSportMarket.get(i).setDataSourceCode(null);
//                updateStandardSportMarket.get(i).setThirdMarketSourceId(null);
//                updateStandardSportMarket.get(i).setCreateTime(null);
            }
            validData = validDataMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            marketDbProducer.sendStandardMarketUpdateInfo("", updateStandardSportMarket);
        }
        //保存多语言
        sw.stop();
        sw.start("标准盘口insertI18nOutrightMarket耗时");
        if (!CollectionUtils.isEmpty(insertI18nOutrightMarket)) {
            i18nOutrightMarketService.saveBatch(insertI18nOutrightMarket);
        }

        List<StandardSportMarket> AllStandardSportMarkets = Collections.synchronizedList(new ArrayList());
        AllStandardSportMarkets.addAll(insertStandardSportMarket);
        AllStandardSportMarkets.addAll(updateStandardSportMarket);
        Map<Long, StandardSportMarket> standardSportMarketMapById = AllStandardSportMarkets.stream().collect(Collectors.toMap(t -> t.getId(), Function.identity(), (v1, v2) -> v1));

        sw.stop();
        sw.start("标准盘口校验球头耗时");
        //需要校验球头的玩法
        validData.forEach(t -> {
            if (standardMatchInfoMap.get(t.getStandardSourceId()).getSportId() == 2 && MarginCategoryConfig.CHANGE_FLAP1.contains(standardSportMarketMapById.get(t.getStandardSportMarketId()).getMarketCategoryId())) {
                Long categoryId = standardSportMarketMapById.get(t.getStandardSportMarketId()).getMarketCategoryId();
                if (categorySetMap.containsKey(t.getLinkId())) {
                    categorySetMap.get(t.getLinkId()).add(categoryId);
                } else {
                    Set<Long> sets = Collections.synchronizedSet(new HashSet());
                    sets.add(categoryId);
                    categorySetMap.put(t.getLinkId(), sets);
                }
            }
        });

        //玩法自动关盘, 不再下发开盘和封盘的盘口
        Map<String, List<String>> autoCloseRedisKeys = validData.stream().collect(Collectors.groupingBy(t -> DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + t.getStandardSourceId()), Collectors.mapping(t -> standardSportMarketMapById.get(t.getStandardSportMarketId()).getMarketCategoryId().toString(), Collectors.toList())));
        Map<String, Object> autoCloseMap = new ConcurrentHashMap<>();
        obtainMultiKeyForHash(autoCloseRedisKeys, autoCloseMap);

        validData = validData.stream().filter(t -> {
            StandardSportMarket standardSportMarket = standardSportMarketMapById.get(t.getStandardSportMarketId());
            if (standardSportMarket.getRelationMarketId() != null) {
                String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + t.getStandardSportMarketId()) + "-" + standardSportMarket.getMarketCategoryId();
                if (!Objects.isNull(autoCloseRedisKeys.get(autoCloseRedisKey))) {
                    Object a01ExtendedTimeObjects  = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + t.getStandardSportMarketId());
                    if (!Objects.isNull(a01ExtendedTimeObjects)){
                        Integer a01ExtendedTimeStatus = (Integer) a01ExtendedTimeObjects;
                        if (a01ExtendedTimeStatus == 1 && t.getData().getDataSourceCode().equals(DataSourceCodeEnum.AO.code)
                                && MarginCategoryConfig.A01_EXTENDED_TIME_CATEGORY.contains(t.getMarketCategoryId())) {
                            return true;
                        }
                    }
                        if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(t.getData().getStatus())) {
                        return false;
                    } else {
                        standardSportMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        standardSportMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    }
                }
            }
            return true;
        }).collect(Collectors.toList());

        //人工编辑多语言不允许修改
        doManualLang(validData, standardSportMarketMapById);
        //处理标准投注项
        //从redis中获取StandardMarketDataMessage
        Map<String, List<String>> marketKeyMap = validData.stream().collect(Collectors.groupingBy(t -> genMarketKey(t), Collectors.mapping(t -> standardSportMarketMapById.get(t.getStandardSportMarketId()).getRelationMarketId().toString(), Collectors.toList())));
        Map<String, Object> marketResultMap = new ConcurrentHashMap<>();
        obtainMultiKeyForHash(marketKeyMap, marketResultMap);
        log.info("::{}::从redis中获取StandardMarketDataMessage,marketKeyMap：{}，marketResultMap：{}",
                uuid,JSONObject.toJSONString(marketKeyMap),JSONObject.toJSONString(marketResultMap));
        sw.stop();
        sw.start("标准盘口处理投注项耗时");
        List<StandardMarketDataMessage> messageResults = storeMarketSportIntoRedis(marketResultMap, validData, standardSportMarketMapById, standardMatchInfoMap, oddsTypeIdSetMap, standardSportMarketOddsUpdateMap);
        Map<Long, StandardMarketDataMessage> messageResultMap = messageResults.stream().collect(Collectors.toMap(StandardMarketDataMessage::getId, Function.identity(), (v1, v2) -> v1));
        sw.stop();
        sw.start("标准盘口记录赔率变动投注项耗时");
        log.info("::{}::记录赔率变动投注项,messageResultMap：{}，",
                uuid,JSONObject.toJSONString(messageResultMap));
        //记录赔率变动投注项
        List<String> changeOddsType = Collections.synchronizedList(new ArrayList());
        validData = validData.stream().filter(t -> {
            StandardMarketMessage lastMarketMessage = null;
            if (MarginCategoryConfig.THREE_CATEGORY.contains(t.getMarketCategoryId())) {
                String lastMarket = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET_ODDS_LAST + t.getStandardSourceId();
                lastMarketMessage = (StandardMarketMessage) redisService.hGet(lastMarket, String.valueOf(t.getMarketCategoryId()));
            }
            StandardMarketDataMessage standardMarketDataMessage = messageResultMap.get(t.getStandardSportMarketId());
            log.info("::{}::记录赔率变动投注项,obj：{}，standardMarketDataMessage：{}",
                    uuid,JSONObject.toJSONString(lastMarketMessage),JSONObject.toJSONString(standardMarketDataMessage));
            if (null != lastMarketMessage) {
                List<StandardMarketOddsDataMessage> standardMarketOddsDataMessages = standardMarketDataMessage.getMarketOddsList();
                List<StandardMarketOddsMessage> standardMarketOddsDataMessages1 = lastMarketMessage.getMarketOddsList();
                if (null != standardMarketOddsDataMessages && null != standardMarketOddsDataMessages1) {
                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarketOddsDataMessages) {
                        for (StandardMarketOddsMessage standardMarketOddsDataMessage1 : standardMarketOddsDataMessages1) {
                            //851冠军操盘 Au模式下，数据源下发新的赔率，Panda赔率跟随变化，同时清空该投注项的累计投注额和跳水次数
                            if (t.getIsOutRight() && standardMarketOddsDataMessage.getOddsType().equals(standardMarketOddsDataMessage1.getOddsType())) {
                                changeOddsType.add(standardMarketOddsDataMessage.getOddsType());
//                                log.info("::{}::数据源冠军赔率变动则清该项跳水产生的概率差,赛事ID:{},标准玩法ID:{},三方盘口源ID:{},投注项：{},改变前赔率:{},改变后赔率:{}",
//                                        linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(), thirdMarketDTO.getThirdMarketSourceId(), standardMarketOddsDataMessage.getOddsType(),
//                                        standardMarketOddsDataMessage1.getOddsValue(), standardMarketOddsDataMessage.getOddsValue());
                            } else
                                //852足球需求 某项数据源赔率变动则清该项跳水产生的概率差 ,并下发标识给风控
                                if (t.getData().getSportId() == 1 && MarginCategoryConfig.THREE_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardMarketOddsDataMessage.getOddsType().equals(standardMarketOddsDataMessage1.getOddsType())) {
                                    if (!standardMarketOddsDataMessage.getOddsValue().equals(standardMarketOddsDataMessage1.getOddsValue())) {
                                        //球员玩法上游传的是中文，传递给下游是namecode,独赢配置存的是namecode
                                        if (MarginCategoryConfig.PLAYER_CATEGORY_ODDS.contains(standardMarketDataMessage.getMarketCategoryId())) {
                                            String oddsType = "";
                                            if (!MarginCategoryConfig.PLAYER_CATEGORY_ODDS_TYPE.contains(standardMarketOddsDataMessage1.getOddsType())) {
                                                StandardSportPlayer standardSportPlayer = standardSportPlayerService.getItem(t.getData().getSportId(), standardMarketOddsDataMessage.getAddition1());
                                                if (null != standardSportPlayer) {
                                                    oddsType = standardSportPlayer.getNameCode().toString();
                                                    changeOddsType.add(oddsType);
                                                }
                                            }
//                                            log.info("::{}::数据源赔率变动则清该项跳水产生的概率差球员类玩法投注类型转换,赛事ID:{},标准玩法ID:{},三方盘口源ID:{},投注项：{},位置:{}",
//                                                    linkId, standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(), thirdMarketDTO.getThirdMarketSourceId(), oddsType, standardMarketDataMessage.getPlaceNum());
                                        }
                                        //球员类玩法OddsType转换前后都需要
                                        changeOddsType.add(standardMarketOddsDataMessage.getOddsType());
                                    }
                                }
                        }
                    }
                }
                standardMarketDataMessage.setOrderNo(lastMarketMessage.getOrderNo());
                //构建盘口来源更改
                if (lastMarketMessage.getMarketSource() == 1) {
                    //1.数据商开盘才能改变盘口来源
                    if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getThirdMarketSourceStatus())) {
                        standardMarketDataMessage.setMarketSource(0);
                        //删除构建缓存
                        thirdMatchMarketProcessor.delConvertMarket(t.getLinkId(), t.getStandardSourceId(), standardMarketDataMessage.getMarketCategoryId());
                    } else {
                        //2.数据商非开盘都不下发
                        return false;
                    }
                }
            }
            return true;
        }).collect(Collectors.toList());
        sw.stop();
        sw.start("标准盘口后期处理耗时");
        for (OddsWrapper<ThirdMarketDTO> thirdMarketDTO : validData) {
            StandardMarketDataMessage standardMarketDataMessage = messageResultMap.get(thirdMarketDTO.getStandardSportMarketId());
            StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(thirdMarketDTO.getStandardSourceId());
            if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdMarketDTO.getDataSourceCode())) {
                standardMarketDataMessage.setPlaceNum(thirdMarketDTO.getData().getOfferLineId());
            }
            //记录赔率变更玩法投注项
            if (!CollectionUtils.isEmpty(changeOddsType)) {
                //清除概率差
                standardMarketDataMessage.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(thirdMarketDTO.getLinkId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getAddition1(), standardMarketDataMessage.getAddition2(), standardMarketDataMessage.getAddition3(), standardMarketDataMessage.getAddition4(), standardMarketDataMessage.getAddition5(), String.valueOf(standardMarketDataMessage.getStandardMatchInfoId())));
                configMarketMarginGapService.upProbabilityByMatchIdAndCategoryId(thirdMarketDTO.getLinkId(), standardMatchInfo.getId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId(), changeOddsType, 1);
                Map<Long, List<String>> oddsTypeMap = new HashMap<>();
                oddsTypeMap.put(standardMarketDataMessage.getMarketCategoryId(), changeOddsType);
                changeCategoryOddsTypeMap.put(thirdMarketDTO.getLinkId(),oddsTypeMap);
            }
            standardMarketDataMessage.setMarketSource(0);
            if (standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE)) {
                standardMarketDataMessage.setOldThirdMarketSourceStatus(null);
            }
            String marketKey = genMarketKey(thirdMarketDTO);
            Object oldMarketObj = marketResultMap.get(marketKey + standardMarketDataMessage.getRelationMarketId());
            if (oldMarketObj instanceof StandardMarketDataMessage) {
                StandardMarketDataMessage oldMarket = (StandardMarketDataMessage) oldMarketObj;
                if (oldMarket.getPlaceNum() != null && (standardMarketDataMessage.getPlaceNum() == null || standardMarketDataMessage.getPlaceNum() == 999)) {
                    standardMarketDataMessage.setPlaceNum(oldMarket.getPlaceNum());
                }
            }
            // 关转封限定足球主玩法
            redisService.hDel(Constant.REDIS_KEY.THIRD_MARKET_HEAD_CLOSE + standardMarketDataMessage.getStandardMatchInfoId(),standardMarketDataMessage.getMarketCategoryId().toString());
            //并发问题设置子玩法
            standardMarketDataMessage.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(thirdMarketDTO.getLinkId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getAddition1(), standardMarketDataMessage.getAddition2(), standardMarketDataMessage.getAddition3(), standardMarketDataMessage.getAddition4(), standardMarketDataMessage.getAddition5(), String.valueOf(standardMarketDataMessage.getStandardMatchInfoId())));
            boolean flag = redisService.hSet(marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
            String marketKey1 = Constant.REDIS_KEY.RONGHE_ORDER_STANDARD_MARKET + standardMatchInfo.getId();
            redisService.hSet(marketKey1, standardMarketDataMessage.getMarketCategoryId().toString(), 1, marketCacheTime(standardMatchInfo.getBeginTime()));
            log.info("::{}::标准赛事id:{},三方盘口ID:{},盘口时间：{}，relationMarketId={},开始刷入赔率缓存:{},changeCategoryOddsTypeMap:{}", thirdMarketDTO.getLinkId(), standardMatchInfo.getId(), standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getModifyTime(), standardMarketDataMessage.getRelationMarketId(),changeCategoryOddsTypeMap);
            if (!flag) {
                log.error("::{}::标准赛事id:{},relationMarketId={},刷入缓存失败,赔率处理异常", thirdMarketDTO.getLinkId(), standardMatchInfo.getId(), standardMarketDataMessage.getRelationMarketId());
            }
        }
        sw.stop();
        log.info("::{}::标准盘口和投注项注入总耗时{}ms," + sw.prettyPrint(), oddsWrappers.get(0).getUuid(), sw.getTotalTimeMillis());
        return new ArrayList<>(messageResultMap.values());
    }

    private String genMarketKey(OddsWrapper<ThirdMarketDTO> data) {
        if (data.getIsOutRight()) {
            return Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + data.getStandardSourceId() + "_" + data.getDataSourceCode();
        } else {
            return DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + data.getStandardSourceId() + "_" + data.getDataSourceCode() + "_" + data.getMarketCategoryId());
        }
    }

    /**
     * 处理三方盘口和投注项赔率
     *
     * @param thirdMarketDTOs
     * @param thirdSportMarketOddsUpdateMap
     * @return
     */
    public List<OddsWrapper<ThirdSportMarketMessage>> processThirdSportMarket(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOs, Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap, Map<String, List<ThirdSportMarketOdds>> thirdSportMarketOddsUpdateMap) {
        if (CollectionUtils.isEmpty(thirdMarketDTOs)) {
            return Collections.emptyList();
        }
        Long uuid = thirdMarketDTOs.get(0).getUuid();
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("三方盘口和投注项处理赛种耗时");
        //根据赛种玩法进行过滤
        List<Pair<Long, Long>> standardCategoryIds = thirdMarketDTOs.stream().map(t -> Pair.of(t.getMarketCategoryId(), thirdMatchInfoBasedIdMap.get(t.getThirdMatchId()).getSportId())).collect(Collectors.toList());
        Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap = standardSportMarketCategoryService.getItemsByStandardCategories(standardCategoryIds).stream().collect(Collectors.toMap(t -> t.getMarketCategoryId() + "-" + t.getSportId(), Function.identity(), (v1, v2) -> v1));
        thirdMarketDTOs = thirdMarketDTOs.stream().filter(t -> {
            String key = t.getMarketCategoryId() + "-" + thirdMatchInfoBasedIdMap.get(t.getThirdMatchId()).getSportId();
            return standardSportMarketCategoryMap.containsKey(key);
        }).collect(Collectors.toList());
        sw.stop();
        sw.start("三方盘口和投注项获取三方盘口信息耗时");
        //处理三方盘口数据，不存在新增，存在更新
        // 获取三方盘口信息
        List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketService.getItemByMarketDTO(thirdMarketDTOs);
        Map<String, ThirdSportMarket> thirdSportMarketMap = thirdSportMarkets.stream().collect(Collectors.toMap(t -> t.getDataSourceCode() + "-" + t.getThirdMarketSourceId() + "-" + t.getMatchId(), Function.identity(), (v1, v2) -> v1));
        sw.stop();
        sw.start("三方盘口和投注项开始处理三方盘口信息耗时");
        // 处理盘口信息
        List<OddsWrapper<ThirdSportMarket>> insertThirdSportMarket = Collections.synchronizedList(new ArrayList());
        List<OddsWrapper<ThirdSportMarket>> updatedThirdSportMarket = Collections.synchronizedList(new ArrayList());
        List<I18nOutrightMarket> i18nOutrightMarketList = Collections.synchronizedList(new ArrayList());

        procThirdSportMarket(thirdMarketDTOs, standardSportMarketCategoryMap, thirdSportMarketMap, insertThirdSportMarket, updatedThirdSportMarket, i18nOutrightMarketList);
        sw.stop();
        sw.start("三方盘口和投注项insertThirdSportMarket耗时");
        if (!CollectionUtils.isEmpty(insertThirdSportMarket)) {
            log.info("::{}:: 盘口create入库third_sport_market_insert data size:{}", uuid, insertThirdSportMarket.size());
            marketDbProducer.sendThirdMarketInsertInfo("", insertThirdSportMarket.stream().map(t -> t.getData()).collect(Collectors.toList()));
        }
        sw.stop();
        sw.start("三方盘口和投注项i18nOutrightMarketList耗时");
        if (!CollectionUtils.isEmpty(i18nOutrightMarketList)) {
            log.info("::{}:: 盘口create多语言入库 data size:{}", uuid, i18nOutrightMarketList.size());
            i18nOutrightMarketService.saveBatch(i18nOutrightMarketList);
        }
        sw.stop();
        sw.start("三方盘口和投注项updatedThirdSportMarket耗时");
        if (!CollectionUtils.isEmpty(updatedThirdSportMarket)) {
            log.info("::{}:: 盘口update入库third_sport_market_update data size:{}", uuid, updatedThirdSportMarket.size());
            marketDbProducer.sendThirdMarketUpdateInfo(uuid.toString(), updatedThirdSportMarket.stream().map(t -> t.getData()).collect(Collectors.toList()));
        }
        insertThirdSportMarket.addAll(updatedThirdSportMarket);
        sw.stop();
        sw.start("三方盘口和投注项开始处理三方投注项信息耗时");
        log.info("::{}::打印insertThirdSportMarket :{}",uuid,JSONObject.toJSONString(insertThirdSportMarket));
        List<OddsWrapper<ThirdSportMarketMessage>> thirdSportMarketMessages = thirdMatchMarketConverter.convertThirdSportMarket(insertThirdSportMarket);
        log.info("::{}::打印thirdSportMarketMessages :{}",uuid,JSONObject.toJSONString(thirdSportMarketMessages));
        // 处理三方盘口投注项信息
        procThirdSportMarketOdds(thirdMarketDTOs, thirdSportMarketOddsUpdateMap, thirdSportMarketMessages);
        sw.stop();
        log.info("::{}::三方盘口和投注项耗时{}ms," + sw.prettyPrint(), uuid, sw.getTotalTimeMillis());
        return thirdSportMarketMessages;
    }

    /**
     * 处理三方盘口信息
     *
     * @param thirdMarketDTOs
     * @param standardSportMarketCategoryMap
     * @param thirdSportMarketMap
     * @param insertThirdSportMarket
     * @param updatedThirdSportMarket
     * @param i18nOutrightMarketList
     */
    private void procThirdSportMarket(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOs, Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap, Map<String, ThirdSportMarket> thirdSportMarketMap, List<OddsWrapper<ThirdSportMarket>> insertThirdSportMarket, List<OddsWrapper<ThirdSportMarket>> updatedThirdSportMarket, List<I18nOutrightMarket> i18nOutrightMarketList) {
        Map<String, Object> thirdSportMarketRedisMap = new ConcurrentHashMap<>();
        for (OddsWrapper<ThirdMarketDTO> thirdMarketDTOWrapper : thirdMarketDTOs) {
            ThirdMarketDTO thirdMarketDTO = thirdMarketDTOWrapper.getData();
            String key = thirdMarketDTO.getDataSourceCode() + "-" + thirdMarketDTO.getThirdMarketSourceId() + "-" + thirdMarketDTOWrapper.getThirdMatchId();
            String linkId = thirdMarketDTOWrapper.getLinkId();
            ThirdSportMarket thirdSportMarket = thirdSportMarketMap.get(key);
            if (thirdSportMarket == null) {
                String matchKey = thirdMarketDTOWrapper.getMarketCategoryId() + "-" + thirdMarketDTOWrapper.getData().getSportId();
                StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryMap.get(matchKey);
                thirdSportMarket = new ThirdSportMarket();
                BeanUtils.copyProperties(thirdMarketDTO, thirdSportMarket);
                thirdSportMarket.setMatchId(thirdMarketDTOWrapper.getThirdMatchId());
                thirdSportMarket.setMarketCategoryId(thirdMarketDTO.getMarketCategoryId());
                thirdSportMarket.setId(UUIdUtils.getId());
                thirdSportMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                thirdSportMarket.setScopeId(standardSportMarketCategory == null ? null : standardSportMarketCategory.getScopeId());
                thirdSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
                thirdSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
                thirdSportMarket.setNameCode(UUIdUtils.getId());
                thirdSportMarket.setOfferLineId(thirdMarketDTO.getOfferLineId());
                thirdSportMarket.setNumberOfWinners(thirdMarketDTO.getNumberOfWinners());
                thirdSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
                OddsWrapper<ThirdSportMarket> thirdSportMarketWrapper = new OddsWrapper<>();
                thirdSportMarketWrapper.setLinkId(thirdMarketDTOWrapper.getLinkId());
                thirdSportMarketWrapper.setData(thirdSportMarket);
                insertThirdSportMarket.add(thirdSportMarketWrapper);
                //三方冠军盘口名称多语言处理
                if (thirdMarketDTO.getMarketType() == 2 && !CollectionUtils.isEmpty(thirdMarketDTO.getI18nNames())) {
                    ThirdSportMarket finalThirdSportMarket = thirdSportMarket;
                    thirdMarketDTO.getI18nNames().forEach(i18nItemDTO -> {
                        I18nOutrightMarket i18nOutrightMarket = new I18nOutrightMarket();
                        BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarket);
                        i18nOutrightMarket.setNameCode(finalThirdSportMarket.getNameCode());
                        i18nOutrightMarket.setDataSourceCode(finalThirdSportMarket.getDataSourceCode());
                        i18nOutrightMarketList.add(i18nOutrightMarket);
                    });
                }
            } else {
                //处理盘口移交状态,当前盘口是滚球还是赛前，赛前就关盘，滚球的忽略handover
                if (Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(thirdSportMarket.getMarketType()) && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)) {
                    thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::三方handover处理,三方盘口源id:{},当前盘口类型:{},三方盘口源状态:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), thirdSportMarket.getMarketType(), thirdMarketDTO.getStatus());
                }
                if (Constant.SPORT_MARKET.MARKET_TYPE.LIVE_ODD_BUSINESS.equals(thirdSportMarket.getMarketType()) && Constant.SPORT_MARKET.MARKET_TYPE.PRE_MATCH_BUSINESS.equals(thirdMarketDTO.getMarketType()) && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.HANDEDOVER)) {
                    log.info("::{}::handover处理滚球数据,忽略赛前,三方盘口源id:{},当前盘口类型:{},三方盘口源状态:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), thirdSportMarket.getMarketType(), thirdMarketDTO.getStatus());
                    continue;
                }
                thirdSportMarket.setStatus(thirdMarketDTO.getStatus());
                thirdSportMarket.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
                thirdSportMarket.setMarketType(thirdMarketDTO.getMarketType());
                thirdSportMarket.setModifyTime(thirdMarketDTO.getModifyTime());
                thirdSportMarket.setOddsName(thirdMarketDTO.getOddsName());
                thirdSportMarket.setAddition1(thirdMarketDTO.getAddition1());
                thirdSportMarket.setAddition2(thirdMarketDTO.getAddition2());
                thirdSportMarket.setAddition3(thirdMarketDTO.getAddition3());
                thirdSportMarket.setAddition4(thirdMarketDTO.getAddition4());
                thirdSportMarket.setNumberOfWinners(thirdMarketDTO.getNumberOfWinners());
                thirdSportMarket.setOfferLineId(thirdMarketDTO.getOfferLineId());
                thirdSportMarket.setInternalDataSourceCode(thirdMarketDTO.getInternalDataSourceCode());
                OddsWrapper<ThirdSportMarket> thirdSportMarketWrapper = new OddsWrapper<>();
                thirdSportMarketWrapper.setLinkId(thirdMarketDTOWrapper.getLinkId());
                thirdSportMarketWrapper.setData(thirdSportMarket);
                updatedThirdSportMarket.add(thirdSportMarketWrapper);

                String redisKey = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId();
                String redisKeyCode = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId() + "-" + thirdSportMarket.getDataSourceCode();
                thirdSportMarketRedisMap.put(redisKey, thirdSportMarket);
                thirdSportMarketRedisMap.put(redisKeyCode, thirdSportMarket);
            }
            thirdMarketDTOWrapper.setThirdSportMarketId(thirdSportMarket.getId());
        }
        if (!thirdSportMarketRedisMap.isEmpty()) {
            redisService.mSet(thirdSportMarketRedisMap);
        }
    }

    /**
     * 处理三方盘口投注项
     *
     * @param thirdMarketDTOs
     * @param thirdSportMarketOddsUpdateMap
     * @param thirdSportMarketMessages
     */
    private void procThirdSportMarketOdds(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOs, Map<String, List<ThirdSportMarketOdds>> thirdSportMarketOddsUpdateMap, List<OddsWrapper<ThirdSportMarketMessage>> thirdSportMarketMessages) {
        if (CollectionUtils.isEmpty(thirdMarketDTOs)) {
            return;
        }
        Long uuid = thirdMarketDTOs.get(0).getUuid();
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("三方盘口和投注项处理投注项模板耗时");
        Map<Long, OddsWrapper<ThirdSportMarketMessage>> marketMessageMap = thirdSportMarketMessages.stream().collect(Collectors.toMap(t -> t.getData().getId(), Function.identity()));
        //根据三方玩法投注项模板进行过滤
        Set<String> templateIds = thirdMarketDTOs.stream().filter(t -> null != t.getData().getMarketOddsList()).flatMap(t -> t.getData().getMarketOddsList().stream().map(inner -> inner.getThirdTempletSourceId() + "-" + t.getThirdMarketCategoryId())).collect(Collectors.toSet());
        List<ThirdMarketCategoryField> thirdMarketCategoryFields = thirdMarketCategoryFieldService.queryThirdSportOddsFieldsList(templateIds);
        Map<String, ThirdMarketCategoryField> thirdMarketCategoryFieldMap = thirdMarketCategoryFields.stream().collect(Collectors.toMap(t -> t.getThirdSourceId() + "-" + t.getMarketCategoryId(), Function.identity(), (v1, v2) -> v1));

        sw.stop();
        sw.start("三方盘口和投注项处理查询三方投注项耗时");
        //查询三方盘口投注项信息
        Map<String, List<OddsWrapper<ThirdMarketDTO>>> thirdMarketDTOMap = thirdMarketDTOs.stream().collect(Collectors.groupingBy(OddsWrapper::getDataSourceCode));
        List<ThirdSportMarketOdds> thirdSportMarketOdds = Collections.synchronizedList(new ArrayList());
        for (Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entry : thirdMarketDTOMap.entrySet()) {
            List<ThirdSportMarketOdds> thirdSportMarketOddsEach = thirdSportMarketOddsService.getItems(entry.getValue());
            thirdSportMarketOdds.addAll(thirdSportMarketOddsEach);
        }
        Map<String, ThirdSportMarketOdds> thirdSportMarketOddsMap = thirdSportMarketOdds.stream().collect(Collectors.toMap(t -> t.getDataSourceCode() + "-" + t.getThirdOddsFieldSourceId() + "-" + t.getMarketId(), Function.identity(), (v1, v2) -> v1));

        sw.stop();
        sw.start("三方盘口和投注项处理查询I18nOutrightMarketOdds耗时");
        //查询I18nOutrightMarketOdds
        List<Pair<String, Long>> i18nPairs = thirdMarketDTOs.stream().flatMap(t -> {
            String dataSourceCode = t.getDataSourceCode();
            Long marketId = t.getThirdSportMarketId();
            List<ThirdMarketOddsDTO> oddsList = t.getData().getMarketOddsList();
            if (CollectionUtils.isEmpty(oddsList) || !Arrays.asList(Constant.ACTIVE_CHAMPION_DATA_SOURCE).contains(dataSourceCode)) {
                return null;
            }
            return oddsList.stream().map(inner -> {
                String oddKey = dataSourceCode + "-" + inner.getThirdOddsFieldSourceId() + "-" + marketId;
                ThirdSportMarketOdds thirdSportMarketOdd = thirdSportMarketOddsMap.get(oddKey);
                if (thirdSportMarketOdd != null) {
                    return Pair.of(dataSourceCode, thirdSportMarketOdd.getNameCode());
                }
                return null;
            });
        }).filter(Objects::nonNull).collect(Collectors.toList());
        Map<String, I18nOutrightMarketOdds> i18nOutrightMarketOddsMap = i18nOutrightMarketOddsService.selectI18nOutRightMarketOddsList(i18nPairs).stream().collect(Collectors.toMap(t -> t.getNameCode() + "-" + t.getDataSourceCode() + "-" + t.getLanguageType(), Function.identity(), (v1, v2) -> v1));
        sw.stop();
        sw.start("三方盘口和投注项处理开始处理投注项耗时");
        List<ThirdSportMarketOdds> insertOddsList = Collections.synchronizedList(new ArrayList());
        Map<String, List<ThirdSportMarketOdds>> updateOddsMap = new ConcurrentHashMap<>();
        List<I18nOutrightMarketOdds> insertI18nMarketOddsList = Collections.synchronizedList(new ArrayList());
        List<I18nOutrightMarketOdds> updateI18nMarketOddsList = Collections.synchronizedList(new ArrayList());
        for (OddsWrapper<ThirdMarketDTO> thirdMarketDTOWrapper : thirdMarketDTOs) {
            ThirdMarketDTO thirdMarketDTO = thirdMarketDTOWrapper.getData();
            Long thirdMarketCategoryId = thirdMarketDTOWrapper.getThirdMarketCategoryId();
            String linkId = thirdMarketDTOWrapper.getLinkId();
            OddsWrapper<ThirdSportMarketMessage> thirdSportMarketMessage = marketMessageMap.get(thirdMarketDTOWrapper.getThirdSportMarketId());
            if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                //批量修改投注项
                for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                    ThirdMarketCategoryField thirdMarketCategoryField = thirdMarketCategoryFieldMap.get(thirdMarketOddsDTO.getThirdTempletSourceId() + "-" + thirdMarketCategoryId);
                    if (thirdMarketCategoryField == null) {
                        log.info("::{}::三方投注项模板为空，数据源:{}，数据源原始模板id:{}，融合三方玩法id:{}", linkId, thirdMarketDTO.getDataSourceCode(), thirdMarketOddsDTO.getThirdTempletSourceId(), thirdMarketCategoryId);
                        continue;
                    }
                    String oddKey = thirdMarketDTOWrapper.getDataSourceCode() + "-" + thirdMarketOddsDTO.getThirdOddsFieldSourceId() + "-" + thirdMarketDTOWrapper.getThirdSportMarketId();
                    ThirdSportMarketOdds thirdSportMarketOdd = thirdSportMarketOddsMap.get(oddKey);
                    if (thirdSportMarketOdd == null) {
                        if (DataSourceCodeEnum.TX.code.equals(thirdMarketDTOWrapper.getDataSourceCode())) {
                            thirdMarketOddsDTO.setModifyTime(thirdMarketDTO.getModifyTime());
                        }
                        thirdSportMarketOdd = new ThirdSportMarketOdds();
                        BeanUtils.copyProperties(thirdMarketOddsDTO, thirdSportMarketOdd);
                        thirdSportMarketOdd.setId(UUIdUtils.getId());
                        thirdSportMarketOdd.setMarketId(thirdMarketDTOWrapper.getThirdSportMarketId());
                        thirdSportMarketOdd.setOddsFieldsTemplateId(thirdMarketCategoryField.getId());
                        thirdSportMarketOdd.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
                        thirdSportMarketOdd.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        thirdSportMarketOdd.setModifyTime(thirdMarketOddsDTO.getModifyTime());
                        if (null == thirdSportMarketOdd.getModifyTime()) {
                            thirdSportMarketOdd.setModifyTime(thirdMarketDTOWrapper.getData().getModifyTime());
                        }
                        thirdSportMarketOdd.setThirdMatchId(thirdMarketDTOWrapper.getThirdMatchId());
                        thirdSportMarketOdd.setName(StandardSportMarketOddsServiceImpl.getOddsName(thirdMarketOddsDTO.getI18nNames()));
                        thirdSportMarketOdd.setNameCode(thirdSportMarketOdd.getId());
                        insertOddsList.add(thirdSportMarketOdd);


                        //处理投注项国际化
                        if (thirdMarketDTO.getMarketType() == 2 && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames())) {
                            for (I18nItemDTO dto : thirdMarketOddsDTO.getI18nNames()) {
                                I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                                BeanUtils.copyProperties(dto, i18nOutrightMarketOdds);
                                i18nOutrightMarketOdds.setNameCode(thirdSportMarketOdd.getNameCode());
                                i18nOutrightMarketOdds.setDataSourceCode(thirdSportMarketOdd.getDataSourceCode());
                                insertI18nMarketOddsList.add(i18nOutrightMarketOdds);
                            }
                        }
                    } else {
                        thirdSportMarketOdd.setOddsValue(thirdMarketOddsDTO.getOddsValue());
                        thirdSportMarketOdd.setOriginalOddsValue(thirdMarketOddsDTO.getOriginalOddsValue());
                        thirdSportMarketOdd.setActive(thirdMarketOddsDTO.getActive());
                        thirdSportMarketOdd.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        thirdSportMarketOdd.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
                        thirdSportMarketOdd.setOddsType(thirdMarketOddsDTO.getOddsType());
                        thirdSportMarketOdd.setExtraInfo(thirdMarketOddsDTO.getExtraInfo());
                        if (thirdMarketDTO.getMarketType() == 2) {
                            thirdSportMarketOdd.setAddition1(thirdMarketOddsDTO.getAddition1());
                        } else {
                            thirdSportMarketOdd.setAddition1(thirdMarketOddsDTO.getAddition1());
                            thirdSportMarketOdd.setAddition2(thirdMarketOddsDTO.getAddition2());
                            thirdSportMarketOdd.setAddition3(thirdMarketOddsDTO.getAddition3());
                            thirdSportMarketOdd.setAddition4(thirdMarketOddsDTO.getAddition4());
                            thirdSportMarketOdd.setAddition5(thirdMarketOddsDTO.getAddition5());
                        }
                        //98331 【日常】【隔离】冠军玩法操盘赔率排序顺序与s01数据商不一致
                        if (Arrays.asList(Constant.ACTIVE_CHAMPION_DATA_SOURCE).contains(thirdMarketDTOWrapper.getDataSourceCode())
                                && thirdMarketDTO.getMarketType() == 2) {
                            thirdSportMarketOdd.setOrderOdds(thirdMarketOddsDTO.getOrderOdds());
                        }
                        //冠军投注项多语言历史数据兼容
                        if (Arrays.asList(Constant.ACTIVE_CHAMPION_DATA_SOURCE).contains(thirdMarketDTOWrapper.getDataSourceCode()) && thirdMarketDTO.getMarketType() == 2 && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames())) {
                            if (thirdSportMarketOdd.getNameCode() == null) {
                                thirdSportMarketOdd.setNameCode(thirdSportMarketOdd.getId());
                            }
                            for (I18nItemDTO dto : thirdMarketOddsDTO.getI18nNames()) {
                                I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                                String langKey = thirdSportMarketOdd.getNameCode() + "-" + thirdMarketDTOWrapper.getDataSourceCode() + "-" + dto.getLanguageType();
                                if (i18nOutrightMarketOddsMap.containsKey(langKey)) {
                                    BeanUtils.copyProperties(i18nOutrightMarketOddsMap.get(langKey), i18nOutrightMarketOdds);
                                    i18nOutrightMarketOdds.setText(dto.getText());
                                    updateI18nMarketOddsList.add(i18nOutrightMarketOdds);
                                } else {
                                    BeanUtils.copyProperties(dto, i18nOutrightMarketOdds);
                                    i18nOutrightMarketOdds.setNameCode(thirdSportMarketOdd.getNameCode());
                                    i18nOutrightMarketOdds.setDataSourceCode(thirdSportMarketOdd.getDataSourceCode());
                                    insertI18nMarketOddsList.add(i18nOutrightMarketOdds);
                                }
                            }
                        }
                        if (!thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
                            if (updateOddsMap.containsKey(linkId)) {
                                updateOddsMap.get(linkId).add(thirdSportMarketOdd);
                            } else {
                                List<ThirdSportMarketOdds> tempList = Collections.synchronizedList(new ArrayList());
                                tempList.add(thirdSportMarketOdd);
                                updateOddsMap.put(linkId, tempList);
                            }
                        }
                    }
                    if (null != thirdSportMarketMessage) {
                        thirdSportMarketMessage.getData().getThirdSportMarketOddsList().add(thirdSportMarketOdd);
                    }
                }
            }
        }
        sw.stop();
        sw.start("三方盘口和投注项insertOddsList耗时");
        if (!CollectionUtils.isEmpty(insertOddsList)) {
            log.info("::{}:: 三方投注项create入库third_sport_market_odds_insert data size:{}", uuid, insertOddsList.size());
            marketDbProducer.sendThirdMarketOddsInsertInfo("", insertOddsList);
        }

        if (!CollectionUtils.isEmpty(insertI18nMarketOddsList)) {
            log.info("::{}:: 三方投注项create多语言 data size:{}", uuid, insertI18nMarketOddsList.size());
            i18nOutrightMarketOddsService.saveBatch(insertI18nMarketOddsList);
        }
        sw.stop();
        sw.start("三方盘口和投注项处理upThirdOddsAsyncList耗时");
        //批量修改投注项 ,三方盘口为关不修改投注项
        if (!CollectionUtils.isEmpty(updateOddsMap)) {
            log.info("::{}:: 三方投注项update进入redis data size:{}", uuid, updateOddsMap.size());
            thirdSportMarketOddsService.upThirdOddsAsyncList(updateOddsMap.values().stream().flatMap(t -> t.stream()).collect(Collectors.toList()));
            for (Map.Entry<String, List<ThirdSportMarketOdds>> entry : updateOddsMap.entrySet()) {
                if (thirdSportMarketOddsUpdateMap.containsKey(entry.getKey())) {
                    List<ThirdSportMarketOdds> existItems = thirdSportMarketOddsUpdateMap.get(entry.getKey());
                    if (!CollectionUtils.isEmpty(existItems)) {
                        existItems.addAll(entry.getValue());
                        thirdSportMarketOddsUpdateMap.put(entry.getKey(), existItems);
                    }
                } else {
                    List<ThirdSportMarketOdds> newItems = Collections.synchronizedList(new ArrayList());
                    newItems.addAll(entry.getValue());
                    thirdSportMarketOddsUpdateMap.put(entry.getKey(), newItems);
                }
            }
        }
        sw.stop();
        log.info("::{}::三方投注项耗时{}ms," + sw.prettyPrint(), uuid, sw.getTotalTimeMillis());
    }

    /**
     * 批量处理冠军盘口排序下发
     *
     * @param outrightMarketDTOs
     * @param standardMatchInfoMap
     * @param marketIdSetMap
     * @param standardMarketMessageMap
     * @param changeCategoryOddsTypeMap
     */
    public void batchProcessOddsByOutright(List<OddsWrapper<ThirdMarketDTO>> outrightMarketDTOs, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap, Map<String, Set<Long>> marketIdSetMap, Map<String, Map<String, StandardMarketDataMessage>> standardMarketMessageMap, Map<String, Map<Long, List<String>>> changeCategoryOddsTypeMap) {
        Map<String, OddsWrapper<ThirdMarketDTO>> linkMatchIdMap = outrightMarketDTOs.stream().collect(Collectors.toMap(OddsWrapper::getLinkId, Function.identity(), (v1, v2) -> v1));
        marketIdSetMap = marketIdSetMap.entrySet().stream().filter(entry -> linkMatchIdMap.containsKey(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
        if (CollectionUtils.isEmpty(marketIdSetMap)) {
            return;
        }
        //计算盘口及排序
        //盘口维度的操盘配置,直接一次性从库查出赛事玩法级的手自动类型，比循环查更快
        Map<Long, Set<Long>> marketIdByMatchMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, Set<Long>> ea : marketIdSetMap.entrySet()) {
            Long standardSourceId = linkMatchIdMap.get(ea.getKey()).getStandardSourceId();
            if (marketIdByMatchMap.containsKey(standardSourceId)) {
                marketIdByMatchMap.get(standardSourceId).addAll(ea.getValue());
            } else {
                Set<Long> tempSet =Collections.synchronizedSet(new HashSet());
                tempSet.addAll(ea.getValue());
                marketIdByMatchMap.put(standardSourceId, tempSet);
            }
        }


        Map<Long, Integer> tradeTypeMap = outrightTradeTypeConfigService.getTradeTypeMapByMatchIds(marketIdByMatchMap);
        log.info("::batchProcessOddsByOutright-champion-tradeTypeMap:{},standardMarketMessageMap-keys:{},marketIdSetMap-keys:{}", tradeTypeMap, standardMarketMessageMap.keySet(), marketIdSetMap.keySet());

        // 批量查询冠军盘口排序类型 (order_type: 0=自动, 1=手动)
        Map<Long, Integer> orderTypeMap = standardOutrightMarketService
                .selectOutrightMarketSellListByIds(new ArrayList<>(marketIdByMatchMap.keySet())).stream()
                .collect(Collectors.toMap(StandardOutrightMarket::getId, m -> m.getOrderType() != null ? m.getOrderType() : 0, (v1, v2) -> v1));
        Map<String, Set<Long>> finalMarketIdSetMap = marketIdSetMap;
        Map<String, List<StandardMarketDataMessage>> collectMap = standardMarketMessageMap.entrySet().stream().filter(t -> finalMarketIdSetMap.containsKey(t.getKey())).map(t -> {
            String linkId = t.getKey();
            List<StandardMarketDataMessage> values = t.getValue().values().stream().filter(e -> finalMarketIdSetMap.get(linkId).contains(e.getRelationMarketId())).collect(Collectors.toList());
            return Pair.of(linkId, values);
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (v1, v2) -> v1));
        log.info("::batchProcessOddsByOutright-champion-collectMap-size:{}", collectMap.size());

        for (Map.Entry<String, List<StandardMarketDataMessage>> entry : collectMap.entrySet()) {
            String linkId = entry.getKey();
            OddsWrapper<ThirdMarketDTO> oddsWrapper = linkMatchIdMap.get(linkId);
            Long dataSourceTime = oddsWrapper.getDataSourceTime();
            StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(oddsWrapper.getStandardSourceId());
            List<StandardMarketDataMessage> collect = entry.getValue();
            Map<Long, List<String>> changeCategoryOddsType = changeCategoryOddsTypeMap.getOrDefault(linkId, new ConcurrentHashMap<>());

            List<StandardMarketDataMessage> collectAUTO = Lists.newLinkedList();
//            if (null != OutrightMarketOrderProcessor.orderMatchLocal.get() && OutrightMarketOrderProcessor.orderMatchLocal.get() == standardMatchInfo.getId()) {
//                collectAUTO = collect;
//            } else
//            //-------------从缓存中取A操盘的盘口------------
//            {
                collectAUTO = collect.stream().filter(e -> {
                    Integer tradeType = 0;
                    if ( MapUtils.isNotEmpty(tradeTypeMap) && tradeTypeMap.get(e.getRelationMarketId()) != null) {
                        tradeType = tradeTypeMap.get(e.getRelationMarketId());
                    }
                    if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeType)) {
                        return true;
                    }
                    log.info("::{}::标准赛事id:{},盘口id:{},统一盘口id:{},三方盘口源id:{},M和A+模式不下发赔率,操盘类型:{}", linkId, standardMatchInfo.getId(), e.getId(), e.getRelationMarketId(), e.getThirdMarketSourceId(), tradeType);
                    return false;
                }).collect(Collectors.toList());
//            }


            //构建下发给下游的list集合
            List<StandardMarketMessage> standardMarketMessageSendListAUTO = Collections.synchronizedList(new ArrayList());
            for (StandardMarketDataMessage marketDataMsg : collectAUTO) {
                //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
                StandardMarketMessage standardMarketMessage = thirdMatchMarketProcessor.convertStandardMarketMessage(linkId, marketDataMsg, standardMatchInfo.getOperateMatchStatus(), true, true, changeCategoryOddsType);
                standardMarketMessage.setTradeType(Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO);
                Integer marketTradeType = (MapUtils.isNotEmpty(tradeTypeMap) && tradeTypeMap.get(marketDataMsg.getRelationMarketId()) != null)
                        ? tradeTypeMap.get(marketDataMsg.getRelationMarketId()) : Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO;
                if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(marketTradeType)) {
                    ConfigOutrightTradeMarket configOutrightTradeMarket = outrightTradeMarketConfigService.selectItem(standardMatchInfo.getId(), standardMarketMessage.getId());
                    if (null != configOutrightTradeMarket) {
                        Integer status = standardMarketMessage.getStatus();
                        if (configOutrightTradeMarket.getMarketStatus() > status) {
                            status = configOutrightTradeMarket.getMarketStatus();
                            standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.CHAMPION_HANDICAP_STATUS.getCode(), configOutrightTradeMarket.getMarketStatus().toString()));
                            standardMarketMessage.setPaStatus(configOutrightTradeMarket.getMarketStatus());
                        }
                        standardMarketMessage.setStatus(status);
                    }
                } else {
                    Integer sourceStatus = standardMarketMessage.getStatus();
                    Integer syncMarketStatus = Integer.valueOf(0).equals(sourceStatus) ? 0 : 2;
                    ConfigOutrightTradeMarket tradeMarketConfig = outrightTradeMarketConfigService.selectItem(standardMatchInfo.getId(), standardMarketMessage.getId());
                    if (tradeMarketConfig != null) {
                        if (!syncMarketStatus.equals(tradeMarketConfig.getMarketStatus())) {
                            tradeMarketConfig.setMarketStatus(syncMarketStatus);
                            tradeMarketConfig.setLinkId(linkId);
                            outrightTradeMarketConfigService.updateItem(tradeMarketConfig);
                            log.info("::{}::AUTO模式同步config_outright_trade_market,matchId:{},marketId:{},sourceStatus:{},syncMarketStatus:{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), sourceStatus, syncMarketStatus);
                        }
                    } else {
                        ConfigOutrightTradeMarket newConfig = new ConfigOutrightTradeMarket();
                        newConfig.setStandardMatchId(standardMatchInfo.getId());
                        newConfig.setStandardMarketId(standardMarketMessage.getId());
                        newConfig.setMarketStatus(syncMarketStatus);
                        newConfig.setLinkId(linkId);
                        outrightTradeMarketConfigService.saveBatch(Collections.singletonList(newConfig));
                        log.info("::{}::AUTO模式新增config_outright_trade_market,matchId:{},marketId:{},sourceStatus:{},syncMarketStatus:{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), sourceStatus, syncMarketStatus);
                    }
                }

                // order_type=0(自动)时 order_odds 跟随 oddsValue; order_type=1(手动)时不修改
                Integer marketOrderType = orderTypeMap.getOrDefault(marketDataMsg.getRelationMarketId(), 0);

                if (!CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())) {
                    for (StandardMarketOddsMessage marketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                        //三方数据源状态赋值
                        marketOddsMessage.setThirdSourceActive(marketOddsMessage.getActive());
                        //投注项状态处理
                        ConfigOutrightTradeOdds configOutrightTradeOdds = outrightTradeOddsConfigService.selectItem(standardMatchInfo.getId(), marketOddsMessage.getId());
                        if (null != configOutrightTradeOdds) {
                            marketOddsMessage.setActive(configOutrightTradeOdds.getOddsStatus());
                            marketOddsMessage.setPaActiveReason("冠军操盘，投注项状态为：【" + configOutrightTradeOdds.getOddsStatus() + "】");
                        }

                        // 自动排序：order_odds 跟随当前赔率值；手动排序：order_odds 不变
                        if ( ZERO.equals(marketOrderType) ) {
                            marketOddsMessage.setOrderOdds(marketOddsMessage.getOddsValue());
                        }

                        /**
                         * P1=1/抽水赔率，（截取保留4位小数）
                         * P2=概率变化/100 （截取保留4位小数）
                         * odds=1/(P1+P2)；(赔率截取保留2位小数)；
                         */
                        ConfigOutrightTradeProbability configOutrightTradeProbability = outrightTradeProbabilityConfigService.selectItem(standardMatchInfo.getId(), marketOddsMessage.getId());
                        Integer paOddsValue = marketOddsMessage.getOddsValue();
                        if (null != configOutrightTradeProbability && null != paOddsValue && 0 != paOddsValue) {
                            BigDecimal p1 = new BigDecimal(100000).divide(new BigDecimal(paOddsValue), 4, BigDecimal.ROUND_DOWN);
                            BigDecimal p2 = new BigDecimal(configOutrightTradeProbability.getProbability()).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
                            BigDecimal divide = new BigDecimal(1).divide(p1.add(p2), 4, BigDecimal.ROUND_HALF_UP);
                            DecimalFormat dFormat = new DecimalFormat();
                            dFormat.setMaximumFractionDigits(2);
                            dFormat.setGroupingSize(0);
                            dFormat.setRoundingMode(RoundingMode.FLOOR);
                            Integer oddsValue = new BigDecimal(dFormat.format(divide)).multiply(new BigDecimal(100000)).intValue();
                            marketOddsMessage.setPaOddsValue(oddsValue);
                            if (oddsValue < 1.01 * 100000) {
                                marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                marketOddsMessage.setPaActiveReason("跳水后，赔率值不得低于1.01,賠率值:" + oddsValue);
                            } else if (oddsValue > 1001 * 100000) {
                                marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                marketOddsMessage.setPaActiveReason("跳水后，赔率值最高不能超过1001,賠率值:" + oddsValue);
                            }
                            log.info("::{}::processOddsByOutright,盘口id:{},投注项id:{},概率差:{}，计算前赔率：{}，计算后赔率：{}", linkId, standardMarketMessage.getId(), marketOddsMessage.getId(), configOutrightTradeProbability.getProbability(), paOddsValue, oddsValue);

                        }
                    }
                }
                //赔率合法性校验
                if (null != standardMarketMessage.getMarketOddsList()) {
                    for (StandardMarketOddsMessage message : standardMarketMessage.getMarketOddsList()) {
                        if (message.getPaOddsValue() == null || message.getPaOddsValue() <= 100000) {
                            //投注项赔率不合法时，只封当前投注项
                            message.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                            message.setPaActiveReason("投注项赔率不合法，赔率小于1，投注项封盘");
                            log.info("::{}::processOddsByOutright赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率:{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), message.getPaOddsValue());
                        }
                    }
                }
                //赔率优化(两项盘小数位优化)
                thirdMatchMarketProcessor.processOddsValueDecimals(linkId, standardMarketMessage, standardMatchInfo);
                standardMarketMessageSendListAUTO.add(standardMarketMessage);
            }
            //-------------赔率下发-----------------
            if (!CollectionUtils.isEmpty(standardMarketMessageSendListAUTO)) {
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
        }
    }

    /**
     * 批量处理MTS的盘口排序下发（MTS)
     *
     * @param mtsMarketDTOs
     * @param standardMatchInfoMap
     * @param marketCategoryIdSetMap
     * @param standardMarketMessageMaps
     * @param isMain
     */
    public void batchProcessOddsByMts(List<OddsWrapper<ThirdMarketDTO>> mtsMarketDTOs, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap, Map<String, Set<Long>> marketCategoryIdSetMap, Map<String, Map<String, StandardMarketDataMessage>> standardMarketMessageMaps, Boolean isMain) {
        Map<String, OddsWrapper<ThirdMarketDTO>> wrapperMap = mtsMarketDTOs.stream().collect(Collectors.toMap(OddsWrapper::getLinkId, Function.identity(), (v1, v2) -> v1));
        standardMarketMessageMaps = standardMarketMessageMaps.entrySet().stream().filter(entry -> wrapperMap.containsKey(entry.getKey())).collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue(), (v1, v2) -> v1));
        for (Map.Entry<String, Map<String, StandardMarketDataMessage>> entry : standardMarketMessageMaps.entrySet()) {
            String linkId = entry.getKey();
            OddsWrapper<ThirdMarketDTO> oddsWrapper = wrapperMap.get(linkId);
            StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(oddsWrapper.getStandardSourceId());
            //删除球头
            redisService.del(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET_ODDS_LAST + standardMatchInfo.getId());
            Long dataSourceTime = oddsWrapper.getDataSourceTime();
            Set<Long> marketCategoryIdSet = marketCategoryIdSetMap.get(entry.getKey());
            Map<String, StandardMarketDataMessage> standardMarketMessageMap = entry.getValue();
            //取本次有改变的玩法,排序
            Map<Long, List<StandardMarketDataMessage>> standardMarketMapMTS = standardMarketMessageMap.values().stream().filter(e -> marketCategoryIdSet.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
            if (CollectionUtils.isEmpty(standardMarketMapMTS)) {
                continue;
            }
            //盘口处理，排序，设置抽水赔
            List<StandardMarketDataMessage> standardMarketMessageList = Collections.synchronizedList(new ArrayList());

            //循环遍历盘口信息，设置低赔和赔率差
            thirdMatchMarketProcessor.setOddsMetricAndLowOddsForMTS(linkId, standardMarketMessageList, standardMarketMapMTS, standardMatchInfo);
            //构建下发给下游的list集合
            List<StandardMarketMessage> standardMarketMessageSendListMTS = Collections.synchronizedList(new ArrayList());
            //封装为可投递的StandardMarketMessage
            standardMarketMessageList.forEach(standardMarketDataMessage -> {
                //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
                StandardMarketMessage standardMarketMessage = thirdMatchMarketProcessor.convertStandardMarketMessage(linkId, standardMarketDataMessage, standardMatchInfo.getOperateMatchStatus(), true, false, new HashMap<>());
                if (MarginCategoryConfig.BASKETBALL_PLAYER_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())) {
                    thirdMatchMarketProcessor.processPlayerTeamFlag(linkId, standardMatchInfo, standardMarketMessage);
                }
                //球员玩法多语言name_code处理
                thirdMatchMarketProcessor.processPlayerNameCode(linkId, standardMatchInfo.getSportId(), standardMarketMessage);
                standardMarketMessageSendListMTS.add(standardMarketMessage);
            });
            //小数点优化
            thirdMatchMarketProcessor.processOddsValueDecimalsXts(standardMarketMessageSendListMTS);
            //设置马来赔
            thirdMatchMarketProcessor.convertXtsMalayOddsValue(standardMarketMessageSendListMTS);
            thirdMatchMarketProcessor.clearMalayOddsForEuropeCategory(standardMarketMessageSendListMTS, standardMatchInfo.getSportId());

            //XTS 两项盘有一个投注项未激活改为关
            thirdMatchMarketProcessor.xtsMarketOddsActive(linkId, standardMarketMessageSendListMTS);
            List<StandardMarketMessage> finalMessages = standardMarketMessageSendListMTS;
            if (!footballMarketValidateService.shouldValidateFootball(standardMatchInfo)) {
                // 数据商全封和全关判断
                thirdMatchMarketProcessor.transformStatIfSatisfyCond(linkId,
                                                                     standardMatchInfo,
                                                                     standardMarketMessageSendListMTS);
                //开盘比分校验
                footballMarketsSoreProcessor.check(linkId, standardMatchInfo, standardMarketMessageSendListMTS);
                //盘口开关封锁 按开关优先级 (A > B > C )调整盘口状态
                dealMarketStatusProcessor.dealMarketStatusList(linkId,
                                                               standardMarketMessageSendListMTS,
                                                               standardMatchInfo);
                //盘口时间戳校验
                //thirdMatchMarketProcessor.standardMarketVerifyModifyTime(linkId, standardMatchInfo, standardMarketMessageSendListMTS);
                //盘口状态校验
                //thirdMatchMarketProcessor.standardMarketStatusCheck(linkId, standardMatchInfo, standardMarketMessageSendListMTS);
                //最后下发赔率 ，自动关盘兜底
                thirdMatchMarketProcessor.automaticClosing(linkId, standardMatchInfo, standardMarketMessageSendListMTS);
            } else {
                finalMessages = footballMarketValidateService.validateFootball(linkId,
                                                               standardMatchInfo,
                                                               standardMarketMessageSendListMTS,
                                                               MarketHandlingEnum.MTS);
            }

            thirdMatchMarketProcessor.saveTheLastMarketOddsToReids(linkId, standardMatchInfo, marketCategoryIdSet, finalMessages, dataSourceTime, isMain);
            thirdMatchMarketProcessor.saveTheLastAMarketOddsToReids(linkId, standardMatchInfo, marketCategoryIdSet, finalMessages, dataSourceTime, isMain);
            //-------------赔率下发-----------------
            standardMarketOddsProducer.standardMarketOddsAsyncSend(linkId, standardMatchInfo, finalMessages, dataSourceTime, true);
        }
    }

    private void obtainMultiKeyForHash(Map<String, List<String>> multiRedisKeys, Map<String, Object> result) {
        for (Map.Entry<String, List<String>> redisKey : multiRedisKeys.entrySet()) {
            List<Object> values = redisService.hMulGet(redisKey.getKey(), redisKey.getValue());
            for (int i = 0; i < redisKey.getValue().size(); i++) {
                if(null == values.get(i)){
                    continue;
                }
                String mKey = redisKey.getKey() + "-" + redisKey.getValue().get(i);
                result.put(mKey, values.get(i));
            }
        }
    }

    public void syncObtainMarketsWithLock(Map.Entry<String, OddsWrapper<ThirdMarketDTO>> item, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap, Map<String, Map<String, StandardMarketDataMessage>> standardCategoryMarketMessageMap, Map<String, Set<Long>> oddsTypeIdSetMap, Map<String, Set<Long>> categorySetMap, Map<String, Set<Long>> riskCategoryMap) {
        OddsWrapper<ThirdMarketDTO> oddsWrapper = item.getValue();
        // 冠军盘口不在此方法处理
        if (Objects.equals(oddsWrapper.getMarketType(), Constant.SPORT_MARKET.MARKET_TYPE.OUTRIGHT_BUSINESS)) {
            return;
        }
        StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(oddsWrapper.getStandardSourceId());
        Long referenceId = oddsWrapper.getMarketCategoryId();
        //玩法维度锁
        String lockValue = UUIdUtils.getId() + "_" + oddsWrapper.getLinkId();
        String redisLocKey = Constant.REDIS_KEY.RONGHE_CATEGORY_LOCK + oddsWrapper.getLinkId() + "_" + referenceId;
        if (redisService.tryLock(redisLocKey, lockValue, 15, 15)) {
            try {
                String redisKey = DigestUtil.md5Hex(
                        Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + oddsWrapper.getStandardSourceId() + "_" +
                                oddsWrapper.getDataSourceCode() + "_" + referenceId);

                //获取本次玩法下面所有盘口
                Map<String, StandardMarketDataMessage> standardMarketMessageNewMap = redisService.hGetAll(redisKey);

                Set<Long> oddsTypeIdSet = oddsTypeIdSetMap.getOrDefault(oddsWrapper.getLinkId(), Collections.synchronizedSet(new HashSet()));
                Set<Long> categorySet = categorySetMap.getOrDefault(oddsWrapper.getLinkId(), Collections.synchronizedSet(new HashSet()));
                Set<Long> riskCategorySet = riskCategoryMap.getOrDefault(oddsWrapper.getLinkId(), Collections.synchronizedSet(new HashSet()));
                log.info("::{}::对本次改变的玩法:{},进行排序:{},赛事ID:{},排序前，oddsTypeIdSet：{}，categorySet：{},riskCategorySet:{}",
                        oddsWrapper.getLinkId(), referenceId, riskCategoryMap.get(oddsWrapper.getLinkId()), standardMatchInfo.getId(), oddsTypeIdSetMap, categorySetMap, riskCategoryMap);
                //对本次改变的玩法进行排序
                marketOddsPlaceProcessor.setOddsOrderByOddsValue(oddsWrapper.getLinkId(), standardMarketMessageNewMap, standardMatchInfo, Sets.newHashSet(referenceId), oddsTypeIdSet, categorySet, riskCategorySet, true);
                oddsTypeIdSetMap.put(oddsWrapper.getLinkId(), oddsTypeIdSet);
                categorySetMap.put(oddsWrapper.getLinkId(), categorySet);
                riskCategoryMap.put(oddsWrapper.getLinkId(), riskCategorySet);
                log.info("::{}::对本次改变的玩法:{},进行排序:{},赛事ID:{},排序后:{}", oddsWrapper.getLinkId(), referenceId, riskCategoryMap.get(oddsWrapper.getLinkId()),standardMatchInfo.getId(),standardMarketMessageNewMap.size());
                //排完排序后放入缓存
                if (!CollectionUtils.isEmpty(standardMarketMessageNewMap)) {
//                    log.info("::{}::对本次改变的玩法:{},进行排序,赛事ID:{},排序后，放缓存前", oddsWrapper.getLinkId(), referenceId, standardMatchInfo.getId());
//                    redisService.hSetAll(redisKey, standardMarketMessageNewMap, marketCacheTime(standardMatchInfo.getBeginTime()));
//                    log.info("::{}::对本次改变的玩法:{},进行排序,赛事ID:{},排序后，放缓存后", oddsWrapper.getLinkId(), referenceId, standardMatchInfo.getId());
                    if (standardCategoryMarketMessageMap.containsKey(oddsWrapper.getLinkId())) {
                        standardCategoryMarketMessageMap.get(oddsWrapper.getLinkId()).putAll(standardMarketMessageNewMap);
                    } else {
                        Map<String, StandardMarketDataMessage> tempMap = new ConcurrentHashMap<>();
                        tempMap.putAll(standardMarketMessageNewMap);
                        standardCategoryMarketMessageMap.put(oddsWrapper.getLinkId(), tempMap);
                    }
                }
            } finally {
                redisService.unLock(redisLocKey, lockValue);
            }
        } else {
            log.error("[ThirdMatchMarketBatchProcessor] syncObtainMarketsWithLock 获取redis key: {} 失败！", redisLocKey);
        }
    }

    public void batchMatchOddsWarning(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOLock, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap, Map<String, Set<Long>> marketCategoryIdSetMap) {
        List<OddsWrapper<ThirdMarketDTO>> filteredItems = thirdMarketDTOLock.stream().filter(t -> t.getStandardSourceId() != null && t.getMarketType() == 0 && MarginCategoryConfig.MATCH_CATEGORY_ODDS_WARNING.contains(t.getMarketCategoryId()) && StandardSportTypeEnum.FootBall.code.equals(standardMatchInfoMap.get(t.getStandardSourceId()).getSportId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filteredItems)) {
            return;
        }

        String oddsWarningKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_WARNING_NEW;
        List<String> matchIds = filteredItems.stream().map(p -> p.getStandardSourceId() + "_" + p.getMarketCategoryId()).distinct().collect(Collectors.toList());
        List<Object> redisVals = redisService.hMulGet(oddsWarningKey, matchIds);

        Map<String, Map<String, Object>> redisMap = new HashMap<>();
        for (int i = 0; i < matchIds.size(); i++) {
            if (!Objects.isNull(redisVals.get(i))) {
                Map<String, Object> warningListMap = (Map<String, Object>) redisVals.get(i);
                redisMap.put(matchIds.get(i), warningListMap);
            } else {
                Map<String, Object> warningMap = new HashMap<>();
                warningMap.put("sign", false);
                warningMap.put("time", TimeUtils.millsSecondsEast8ZoneGmt());
                redisService.hSetSync(oddsWarningKey, matchIds.get(i), warningMap);
            }
        }
        for (Map.Entry<String, Map<String, Object>> entry : redisMap.entrySet()) {
            String[] key = entry.getKey().split("_");
            Long standardMatchId = Long.valueOf(key[0]);
            Long marketCategoryId = Long.valueOf(key[1]);
            Map<String, Object> objectMap = entry.getValue();
            boolean sign = (boolean) objectMap.get("sign");
            if (sign) {
                //下发风控解除报警 false
                matchoddWarningProducer.sendMatchOddsWarningRisk(standardMatchId.toString(), standardMatchId, marketCategoryId, false);
            } else {
                log.info("::{}::标准赛事ID:{},标准玩法ID:{},刷新赔率告警缓存时间", standardMatchId, standardMatchId, marketCategoryId);
            }
            Map<String, Object> warningMap = new HashMap<>();
            warningMap.put("sign", false);
            warningMap.put("time", TimeUtils.millsSecondsEast8ZoneGmt());
            redisService.hSetSync(oddsWarningKey, entry.getKey(), warningMap);
        }
    }

    /**
     * 主客队相反：盘口、投注项内容替换
     *
     * @param linkId
     * @param dataSourceCode
     * @param standardCategoryId
     * @param thirdMarketDTO
     */
    public Long changeStandardMarketContent(String linkId, String dataSourceCode, Long standardCategoryId, ThirdMarketDTO thirdMarketDTO) {
        log.info("::{}::changeStandardMarketContent盘口信息, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, standardCategoryId, thirdMarketDTO.getAddition1(), thirdMarketDTO.getAddition2(), thirdMarketDTO.getAddition3(), thirdMarketDTO.getAddition4());
        Long newMarketCategoryId = null;
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(standardCategoryId)) {
            Long newCategoryId = CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.get(standardCategoryId);
            List<ThirdMarketCategory> marketCategoryList = thirdMarketCategoryService.getItem(dataSourceCode, newCategoryId);
            if (!CollectionUtils.isEmpty(marketCategoryList)) {
                newMarketCategoryId = marketCategoryList.get(0).getReferenceId();
                if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                    List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldService.queryThirdMarketCategoryFieldDetail(dataSourceCode, newMarketCategoryId);
                    if (!CollectionUtils.isEmpty(thirdMarketCategoryFieldDetails)) {
                        Map<String, String> stringMap = thirdMarketCategoryFieldDetails.stream().collect(Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getThirdSourceId, (o, n) -> n));
                        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                            thirdMarketOddsDTO.setThirdTempletSourceId(stringMap.get(thirdMarketOddsDTO.getOddsType().toLowerCase()));
                        }
                    }
                }
            }
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_2.contains(standardCategoryId)) {
            String add1 = thirdMarketDTO.getAddition1().contains("-") ? thirdMarketDTO.getAddition1().replace("-", "") : "-" + thirdMarketDTO.getAddition1();
            thirdMarketDTO.setAddition1(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_3.contains(standardCategoryId)) {
            String add2 = thirdMarketDTO.getAddition2().contains("-") ? thirdMarketDTO.getAddition2().replace("-", "") : "-" + thirdMarketDTO.getAddition2();
            thirdMarketDTO.setAddition2(add2);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_4.contains(standardCategoryId)) {
            String add3 = thirdMarketDTO.getAddition3();
            String add4 = thirdMarketDTO.getAddition4();
            thirdMarketDTO.setAddition3(add4);
            thirdMarketDTO.setAddition4(add3);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_6.contains(standardCategoryId)) {
            String add1 = thirdMarketDTO.getAddition1();
            String add2 = thirdMarketDTO.getAddition2();
            thirdMarketDTO.setAddition1(add2);
            thirdMarketDTO.setAddition2(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_10.contains(standardCategoryId)) {
            String add3 = thirdMarketDTO.getAddition3();
            String add4 = thirdMarketDTO.getAddition4();
            thirdMarketDTO.setAddition3(add4);
            thirdMarketDTO.setAddition4(add3);
        }
        if (CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
            return null;
        }
        Map<String, String> thirdTemplateSourceIdMap = new ConcurrentHashMap<>();
        Map<String, List<I18nItemDTO>> i18Map = new ConcurrentHashMap<>();
        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
            thirdTemplateSourceIdMap.put(thirdMarketOddsDTO.getOddsType(), thirdMarketOddsDTO.getThirdTempletSourceId());
            i18Map.put(thirdMarketOddsDTO.getOddsType(), thirdMarketOddsDTO.getI18nNames());
        }
        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
            /*log.info("::{}::changeStandardMarketContent投注项信息,标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, standardCategoryId,
                    thirdMarketOddsDTO.getAddition1(), thirdMarketOddsDTO.getAddition2(),thirdMarketOddsDTO.getAddition3(),thirdMarketOddsDTO.getAddition4());*/
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_7.contains(standardCategoryId)) {
                String add1 = thirdMarketOddsDTO.getAddition1();
                String add2 = thirdMarketOddsDTO.getAddition2();
                thirdMarketOddsDTO.setAddition1(add2);
                thirdMarketOddsDTO.setAddition2(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_8.contains(standardCategoryId)) {
                String add3 = thirdMarketOddsDTO.getAddition3();
                String add4 = thirdMarketOddsDTO.getAddition4();
                thirdMarketOddsDTO.setAddition3(add4);
                thirdMarketOddsDTO.setAddition4(add3);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_5.contains(standardCategoryId)) {
                if (standardCategoryId == 104L) {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.containsKey(thirdMarketOddsDTO.getOddsType())) {
                        String oddsType = thirdMarketOddsDTO.getOddsType();
                        thirdMarketOddsDTO.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.get(oddsType));
                        thirdMarketOddsDTO.setThirdTempletSourceId(thirdTemplateSourceIdMap.get(thirdMarketOddsDTO.getOddsType()));
                        thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                    }
                } else if (standardCategoryId == 103L) {
                    String str1 = (thirdMarketOddsDTO.getAddition1() == null || thirdMarketOddsDTO.getAddition1().contains("+")) ? thirdMarketOddsDTO.getAddition1() : thirdMarketOddsDTO.getAddition1() + ":" + thirdMarketOddsDTO.getAddition2();
                    String str2 = (thirdMarketOddsDTO.getAddition3() == null || thirdMarketOddsDTO.getAddition3().contains("+")) ? thirdMarketOddsDTO.getAddition3() : thirdMarketOddsDTO.getAddition3() + ":" + thirdMarketOddsDTO.getAddition4();
                    thirdMarketOddsDTO.setOddsType(str1 + " " + str2);
                    thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                } else {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.containsKey(thirdMarketOddsDTO.getOddsType())) {
                        String oddsType = thirdMarketOddsDTO.getOddsType();
                        thirdMarketOddsDTO.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.get(oddsType));
                        thirdMarketOddsDTO.setThirdTempletSourceId(thirdTemplateSourceIdMap.get(thirdMarketOddsDTO.getOddsType()));
                        thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                    } else {
                        if (thirdMarketOddsDTO.getOddsType().contains(":")) {
                            String[] strArr = thirdMarketOddsDTO.getOddsType().split(":");
                            if (strArr.length == 2) {
                                thirdMarketOddsDTO.setOddsType(strArr[1] + ":" + strArr[0]);
                                thirdMarketOddsDTO.setI18nNames(i18Map.get(thirdMarketOddsDTO.getOddsType()));
                            }
                        }
                    }
                }
            }
        }
        return newMarketCategoryId;
    }

    private boolean doFilterForMarketCategorySell(OddsWrapper<ThirdMarketDTO> inner, Pair<Map<String, Object>, Map<String, MarketCategorySell>> allMarketCategorySell, Map<Long, StandardSportMarketSell> standardSportMarketSellMap) {
        if (inner.getIsOutRight()) {
            return true;
        }
        String marketCategoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + inner.getStandardSourceId() + "_" + inner.getMarketType() + "-" + inner.getMarketCategoryId();
        MarketCategorySell marketCategorySell = null;
        if (allMarketCategorySell.getLeft().containsKey(marketCategoryRedisKey)) {
            marketCategorySell = new MarketCategorySell();
            marketCategorySell.setSellStatus(SaleMatchSellStausEnum.Sold.name());
            marketCategorySell.setDataSourceCode((String) allMarketCategorySell.getLeft().get(marketCategoryRedisKey));
        }
        if (marketCategorySell == null) {
            String sellKey = inner.getStandardSourceId() + "-" + inner.getMarketType() + "-" + inner.getMarketCategoryId();
            marketCategorySell = allMarketCategorySell.getRight().get(sellKey);
        }
        if (null == marketCategorySell) {
            return false;
        }
        if (marketCategorySell.getDataSourceCode() == null) {
            //当盘口是赛前盘且赛前盘开售不是该数据源时，不入标准表
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellMap.get(inner.getStandardSourceId());
            if (inner.getMarketType() == 1 && !inner.getDataSourceCode().equals(standardSportMarketSell.getPreMatchDataProviderCode())) {
                return false;
            }
            //当盘口是滚球盘且滚球盘开售不是该数据源时，不入标准表
            if (inner.getMarketType() == 0 && !inner.getDataSourceCode().equals(standardSportMarketSell.getLiveMatchDataProviderCode())) {
                return false;
            }
        } else {
            //开售状态判断是否开售
            if (!SaleMatchSellStausEnum.Sold.name().equalsIgnoreCase(marketCategorySell.getSellStatus()) && (allMarketCategorySell.getLeft() == null || !allMarketCategorySell.getLeft().containsKey(marketCategoryRedisKey))) {
                return false;
            }
            if (!marketCategorySell.getDataSourceCode().equalsIgnoreCase(inner.getDataSourceCode())) {
                return false;
            }
        }
        if (supportA99(inner.getLinkId(),inner.getStandardSourceId(),inner.getMarketType(),inner.getMarketCategoryId())){
            log.info("::{}::A99过滤标准玩法下发,赛事id：{}，盘口类型：{}，玩法id：{}",inner.getLinkId(),inner.getStandardSourceId(),inner.getMarketType(),inner.getMarketCategoryId());
            return false;
        }
        return true;
    }

    //人工编辑多语言不允许修改
    private void doManualLang(List<OddsWrapper<ThirdMarketDTO>> validData, Map<Long, StandardSportMarket> standardSportMarketMapById) {
        List<I18nOutrightMarket> i18nOutrightMarketList = Collections.synchronizedList(new ArrayList());
        Map<String, List<OddsWrapper<ThirdMarketDTO>>> dataByLinkId = validData.stream().collect(Collectors.groupingBy(t -> t.getLinkId()));
        for (Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entry : dataByLinkId.entrySet()) {
            for (OddsWrapper<ThirdMarketDTO> data : entry.getValue()) {
                if (data.getIsOutRight() && !CollectionUtils.isEmpty(data.getData().getI18nNames())) {
                    if (data.getHasRecord()) {
                        StandardSportMarket finalStandardSportMarket = standardSportMarketMapById.get(data.getStandardSportMarketId());
                        data.getData().getI18nNames().forEach(i18nItemDTO -> {
                            I18nOutrightMarket i18nOutrightMarket = new I18nOutrightMarket();
                            BeanUtils.copyProperties(i18nItemDTO, i18nOutrightMarket);
                            i18nOutrightMarket.setFlag(2);
                            i18nOutrightMarket.setNameCode(finalStandardSportMarket.getNameCode());
                            i18nOutrightMarket.setDataSourceCode(finalStandardSportMarket.getDataSourceCode());
                            i18nOutrightMarketList.add(i18nOutrightMarket);
                        });
                    }
                }
            }
            standardMarketOddsProducer.marketNameI18nSend(entry.getKey() + "name_code", i18nOutrightMarketList,null);
        }
    }

    //将最新盘口刷入缓存,兼容冠军盘
    private List<StandardMarketDataMessage> storeMarketSportIntoRedis(Map<String, Object> marketResultMap, List<OddsWrapper<ThirdMarketDTO>> validData, Map<Long, StandardSportMarket> standardSportMarketMapById, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap, Map<String, Set<Long>> oddsTypeIdSetMap, Map<String, List<StandardSportMarketOdds>> standardSportMarketOddsUpdateMap) {
        if (CollectionUtils.isEmpty(validData)) {
            return Collections.emptyList();
        }
        Long uuid = validData.get(0).getUuid();
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("标准投注项获取模版耗时");
        //获取投注项模版
        Set<String> thirdTemplateSourceIds = Collections.synchronizedSet(new HashSet());
        Set<String> standardSportMarketOddsParam = Collections.synchronizedSet(new HashSet());
        for (OddsWrapper<ThirdMarketDTO> data : validData) {
            List<ThirdMarketOddsDTO> oddsDTOS = data.getData().getMarketOddsList();
            if(CollectionUtils.isEmpty(oddsDTOS)){
                continue;
            }
            Long thirdMarketCategoryId = data.getThirdMarketCategoryId();
            for (ThirdMarketOddsDTO thirdMarketOddsDTO : oddsDTOS) {
                thirdTemplateSourceIds.add(thirdMarketOddsDTO.getThirdTempletSourceId() + "-" + thirdMarketCategoryId);
                String key = data.getDataSourceCode() + "->" + thirdMarketOddsDTO.getThirdOddsFieldSourceId() + "->" + data.getStandardSportMarketId();
                standardSportMarketOddsParam.add(key);
            }
        }
        List<ThirdMarketCategoryField> categoryFields = thirdMarketCategoryFieldService.queryThirdSportOddsFieldsList(thirdTemplateSourceIds);
        Map<String, ThirdMarketCategoryField> categoryFieldMap = categoryFields.stream().collect(Collectors.toMap(t -> t.getMarketCategoryId() + "-" + t.getThirdSourceId(), Function.identity(), (v1, v2) -> v1));
        sw.stop();
        sw.start("标准投注项获取赛事盘口投注项表耗时");
        //获取赛事盘口投注项表
        List<StandardSportMarketOdds> standardSportMarketOddsTableList = standardSportMarketOddsService.getItems(standardSportMarketOddsParam.stream().collect(Collectors.toList()));
        Map<String, StandardSportMarketOdds> standardSportMarketOddsMap = standardSportMarketOddsTableList.stream().collect(Collectors.toMap(t -> t.getDataSourceCode() + "-" + t.getThirdOddsFieldSourceId() + "-" + t.getMarketId(), Function.identity(), (v1, v2) -> v1));
        sw.stop();
        sw.start("标准投注项真实处理耗时");
        //将盘口及盘口投注项封装到一起
        List<StandardMarketDataMessage> messageResults = Collections.synchronizedList(new ArrayList());
        List<StandardSportMarketOdds> insertStandardSportMarketOdds = Collections.synchronizedList(new ArrayList());
        List<StandardSportMarketOdds> updateStandardSportMarketOdds = Collections.synchronizedList(new ArrayList());
        List<I18nOutrightMarketOdds> insertI18nMarketOddsList = Collections.synchronizedList(new ArrayList());

        // 批量查询冠军盘口排序类型，用于DB写入时设置order_odds (order_type: 0=自动, 1=手动)
        Set<Long> outrightMatchIds = validData.stream()
                .filter(OddsWrapper::getIsOutRight)
                .map(OddsWrapper::getStandardSourceId)
                .collect(Collectors.toSet());
        Map<Long, Integer> outrightOrderTypeMap = Collections.emptyMap();
        if (!outrightMatchIds.isEmpty()) {
            outrightOrderTypeMap = standardOutrightMarketService
                    .selectOutrightMarketSellListByIds(new ArrayList<>(outrightMatchIds)).stream()
                    .collect(Collectors.toMap(StandardOutrightMarket::getId,
                            m -> m.getOrderType() != null ? m.getOrderType() : 0, (v1, v2) -> v1));
        }

        //处理标准盘口投注项数据  投注项永远存最后一次有效的投注项集合(1852内容部分)
        sw.stop();
        sw.start("标准投注项validData耗时");
        for (OddsWrapper<ThirdMarketDTO> wrapper : validData) {
            List<StandardSportMarketOdds> standardSportMarketOddsList = Collections.synchronizedList(new ArrayList());
            ThirdMarketDTO thirdMarketDTO = wrapper.getData();
            StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(wrapper.getStandardSourceId());
            StandardSportMarket standardSportMarket = standardSportMarketMapById.get(wrapper.getStandardSportMarketId());
            standardSportMarket.setInternalDataSourceCode(wrapper.getData().getInternalDataSourceCode());
            Object obj = marketResultMap.get(genMarketKey(wrapper) + standardSportMarket.getRelationMarketId());
            if ((standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code) && MarginCategoryConfig.NO_CLOS_SPORT.contains(standardMatchInfo.getSportId()) && MarginCategoryConfig.NO_CLOS_DATA_SOURCE_CODE.contains(thirdMarketDTO.getDataSourceCode()) && MarginCategoryConfig.NO_CLOS_CATEGORY.contains(standardSportMarket.getMarketCategoryId()) && thirdMarketDTO.getStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED && null != obj)
                    ||
                    (CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())
                            && thirdMarketDTO.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.SR.getCode())
                            && (thirdMarketDTO.getStatus() == Constant.SPORT_MARKET.STATUS.DEACTIVATED || thirdMarketDTO.getStatus() == Constant.SPORT_MARKET.STATUS.HANDEDOVER)
                            && null != obj)
                ) {
                StandardMarketDataMessage standardMarketDataMessageTemp = (StandardMarketDataMessage) obj;
                StandardMarketDataMessage standardMarketDataMessage = thirdMatchMarketProcessor.convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket, wrapper.getDataSourceTime());
                standardMarketDataMessage.setMarketOddsList(standardMarketDataMessageTemp.getMarketOddsList());
                messageResults.add(standardMarketDataMessage);
            } else {
                if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                    Set<Long> oddsIds = Collections.synchronizedSet(new HashSet());
                    List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList = new ArrayList<>();
                    for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                        //获取三方玩法投注项模板
                        ThirdMarketCategoryField thirdMarketCategoryField = categoryFieldMap.get(wrapper.getThirdMarketCategoryId() + "-" + thirdMarketOddsDTO.getThirdTempletSourceId());
                        if (thirdMarketCategoryField == null) {
                            log.error(" linkId::{}::三方玩法id::{} 三方玩法投注项模板为空" ,wrapper.getThirdMarketCategoryId(),wrapper.getLinkId());
                            continue;
                        }
                        //TODO 校验盘口投注项是否满足标准玩法投注项条件
                        //查询标准盘口投注项信息是否存在，不存在新增，存在更新
                        String queryKey = thirdMarketDTO.getDataSourceCode() + "-" + thirdMarketOddsDTO.getThirdOddsFieldSourceId() + "-" + standardSportMarket.getId();
                        StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsMap.get(queryKey);
                        if (standardSportMarketOdds == null) {
                            //生成并保存标准投注项
                            standardSportMarketOdds = new StandardSportMarketOdds();
                            BeanUtils.copyProperties(thirdMarketOddsDTO, standardSportMarketOdds);
                            standardSportMarketOdds.setMarketId(standardSportMarket.getId());
                            standardSportMarketOdds.setRelationMarketId(standardSportMarket.getRelationMarketId());
                            standardSportMarketOdds.setOddsFieldsTemplateId(thirdMarketCategoryField.getReferenceId());
                            standardSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
                            standardSportMarketOdds.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                            standardSportMarketOdds.setId(UUIdUtils.getId());
                            standardSportMarketOdds.setRelationMarketOddsId(standardSportMarketOddsService.createRelationMarketOddsId(standardSportMarketOdds, standardSportMarket));
                            standardSportMarketOdds.setStandardMatchId(standardSportMarket.getStandardMatchInfoId());
                            standardSportMarketOdds.setName(getOddsName(thirdMarketOddsDTO.getI18nNames()));
                            standardSportMarketOdds.setNameCode(standardSportMarketOdds.getId());
                            //标准球队转换
                            standardSportMarketOddsService.convertStandardTeam(wrapper.getLinkId(), standardSportMarketOdds, standardSportMarket);
                            if (wrapper.getIsOutRight() && ZERO.equals(outrightOrderTypeMap.getOrDefault(standardSportMarket.getRelationMarketId(), 0))) {
                                standardSportMarketOdds.setOrderOdds(thirdMarketOddsDTO.getOddsValue());
                            }
                            insertStandardSportMarketOdds.add(standardSportMarketOdds);
                            standardSportMarketOdds.setI18nNames(thirdMarketOddsDTO.getI18nNames());
                            thirdMatchMarketProcessor.processOdddsI18n(wrapper.getIsOutRight(), i18nOutrightMarketOddsList, thirdMarketOddsDTO, standardSportMarketOdds);
                        } else {
                            //需要校验投注项赔率的投注项id
                            if (wrapper.getIsCheckOdds() && Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(thirdMarketOddsDTO.getActive()) && Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(standardSportMarketOdds.getActive()) && (standardMatchInfo.getSportId() == 1 || standardMatchInfo.getSportId() == 2)) {
                                oddsIds.add(standardSportMarketOdds.getRelationMarketOddsId());
                            }
                            standardSportMarketOdds.setI18nNames(thirdMarketOddsDTO.getI18nNames());
                            standardSportMarketOdds.setActive(thirdMarketOddsDTO.getActive());
                            standardSportMarketOdds.setOddsValue(thirdMarketOddsDTO.getOddsValue());
                            if (wrapper.getIsOutRight() && ZERO.equals(outrightOrderTypeMap.getOrDefault(standardSportMarket.getRelationMarketId(), 0))) {
                                standardSportMarketOdds.setOrderOdds(thirdMarketOddsDTO.getOddsValue());
                            }
                            standardSportMarketOdds.setOriginalOddsValue(thirdMarketOddsDTO.getOriginalOddsValue());
                            standardSportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                            standardSportMarketOdds.setThirdTemplateSourceId(thirdMarketOddsDTO.getThirdTempletSourceId());
                            standardSportMarketOdds.setOddsFieldsTemplateId(thirdMarketCategoryField.getReferenceId());
                            standardSportMarketOdds.setOddsType(thirdMarketOddsDTO.getOddsType());
                            if (wrapper.getIsOutRight()) {
                                standardSportMarketOdds.setAddition1(thirdMarketOddsDTO.getAddition1());
                            } else {
                                // 更新附加字段
                                standardSportMarketOdds.setAddition1(thirdMarketOddsDTO.getAddition1());
                                standardSportMarketOdds.setAddition2(thirdMarketOddsDTO.getAddition2());
                                standardSportMarketOdds.setAddition3(thirdMarketOddsDTO.getAddition3());
                                standardSportMarketOdds.setAddition4(thirdMarketOddsDTO.getAddition4());
                                standardSportMarketOdds.setAddition5(thirdMarketOddsDTO.getAddition5());
                                //标准球队转换
                                standardSportMarketOddsService.convertStandardTeam(wrapper.getLinkId(), standardSportMarketOdds, standardSportMarket);
                            }
                            if (null == standardSportMarketOdds.getNameCode()) {
                                standardSportMarketOdds.setNameCode(standardSportMarketOdds.getId());
                            }
                            //TX旧数据重新赋值盘口投注项ID
                            if ((standardSportMarket.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)) && StringUtils.isBlank(standardSportMarketOdds.getRemark())) {
                                standardSportMarketOdds.setRelationMarketOddsId(standardSportMarketOddsService.createRelationMarketOddsId(standardSportMarketOdds, standardSportMarket));
                            }
                            //冠军投注项多语言历史数据兼容
                            if ((wrapper.getIsOutRight() && Arrays.asList(Constant.ACTIVE_CHAMPION_DATA_SOURCE).contains(standardMatchInfo.getDataSourceCode()) && !CollectionUtils.isEmpty(thirdMarketOddsDTO.getI18nNames()))) {
                                if (wrapper.getHasRecord()) {
                                    if (null == standardSportMarketOdds.getNameCode()) {
                                        standardSportMarketOdds.setNameCode(standardSportMarketOdds.getId());
                                    }
                                    StandardSportMarketOdds finalStandardSportMarketOdds = standardSportMarketOdds;
                                    List<I18nOutrightMarketOdds> i18nOutrightMarketOddsOld = i18nOutrightMarketOddsService.getListOutrightMarketOdds(Arrays.asList(finalStandardSportMarketOdds.getNameCode()), thirdMarketOddsDTO.getDataSourceCode());
                                    Map<String, I18nOutrightMarketOdds> oldLanguageMap = i18nOutrightMarketOddsOld.stream().collect(Collectors.toMap(I18nOutrightMarketOdds::getLanguageType, i -> i));
                                    List<I18nOutrightMarketOdds> i18nOutrightMarketOddsListAdd = new ArrayList<>();
                                    List<I18nOutrightMarketOdds> i18nOutrightMarketOddsListUpdate = new ArrayList<>();
                                    thirdMarketOddsDTO.getI18nNames().forEach(i18nItemDTO -> {
                                        I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                                        if (!oldLanguageMap.isEmpty() && oldLanguageMap.containsKey(i18nItemDTO.getLanguageType())) {
                                            BeanUtils.copyProperties(oldLanguageMap.get(i18nItemDTO.getLanguageType()), i18nOutrightMarketOdds);
                                            if (null == i18nOutrightMarketOdds.getFlag()) {
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
                                        insertI18nMarketOddsList.addAll(i18nOutrightMarketOddsListAdd);
                                    }
                                    if (!CollectionUtils.isEmpty(i18nOutrightMarketOddsListUpdate)) {
                                        i18nOutrightMarketOddsList.addAll(i18nOutrightMarketOddsListUpdate);
                                    }
                                }
                            }
                            //异步执行更新
//                    standardSportMarketOddsService.updateByPrimaryKeySelective(standardSportMarketOdds);
                            //三方盘口为关，不修改投注项
                            if (!thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
                                //updateOperateProxy.updateStandardSportMarketOdds(standardSportMarketOdds, linkId);
                                if (standardSportMarketOddsUpdateMap.containsKey(wrapper.getLinkId())) {
                                    standardSportMarketOddsUpdateMap.get(wrapper.getLinkId()).add(standardSportMarketOdds);
                                } else {
                                    List<StandardSportMarketOdds> tempList = Collections.synchronizedList(new ArrayList());
                                    tempList.add(standardSportMarketOdds);
                                    standardSportMarketOddsUpdateMap.put(wrapper.getLinkId(), tempList);
                                }
                            }
                            updateStandardSportMarketOdds.add(standardSportMarketOdds);
                        }
                        standardSportMarketOddsList.add(standardSportMarketOdds);
                    }
                    if (!CollectionUtils.isEmpty(oddsIds)) {
                        if (oddsTypeIdSetMap.containsKey(wrapper.getLinkId())) {
                            oddsTypeIdSetMap.get(wrapper.getLinkId()).addAll(oddsIds);
                        } else {
                            oddsTypeIdSetMap.put(wrapper.getLinkId(), oddsIds);
                        }
                    }
                    //冠军投注项多语言下发
                    if (wrapper.getIsOutRight() && !CollectionUtils.isEmpty(i18nOutrightMarketOddsList)) {
                        standardMarketOddsProducer.marketOddsNameI18nSend(wrapper.getLinkId(),
                                                                          i18nOutrightMarketOddsList,null);
                    }
                }
                //新增玩法投注项排序
                if (!CollectionUtils.isEmpty(standardSportMarketOddsList) && MarginCategoryConfig.ODDS_ORDER.contains(standardSportMarket.getMarketCategoryId())) {
                    thirdMatchMarketProcessor.oddsOrderByOddsType(standardSportMarketOddsList, standardSportMarket.getMarketCategoryId());
                    for (int i = 0; i < standardSportMarketOddsList.size(); i++) {
                        standardSportMarketOddsList.get(i).setOrderOdds(i + 1);
                    }
                }
                StandardMarketDataMessage standardMarketDataMessage = thirdMatchMarketProcessor.convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket, wrapper.getDataSourceTime());
                messageResults.add(standardMarketDataMessage);
            }
        }
        sw.stop();
        sw.start("标准投注项insertStandardSportMarketOdds耗时");
        if (!CollectionUtils.isEmpty(insertStandardSportMarketOdds)) {
            Map<Long, String> MakertLinkMap = validData.stream().collect(Collectors.toMap(t -> t.getStandardSportMarketId(), t -> t.getLinkId(), (v1, v2) -> v1));
            Map<String, List<StandardSportMarketOdds>> linkItemMap = insertStandardSportMarketOdds.stream().map(t -> Pair.of(MakertLinkMap.get(t.getMarketId()), t)).collect(Collectors.groupingBy(t -> t.getLeft(), Collectors.mapping(t -> t.getRight(), Collectors.toList())));
            log.info("::{}:: 标准盘口create入库standard_sport_market_odds_insert data:{}", uuid, linkItemMap.values());
            for (Map.Entry<String, List<StandardSportMarketOdds>> entry : linkItemMap.entrySet()) {
                marketDbProducer.sendStandardMarketOddsInsertInfo(entry.getKey(), entry.getValue());
            }
        }
        sw.stop();
        sw.start("标准投注项insertI18nMarketOddsList耗时");
        if (!CollectionUtils.isEmpty(insertI18nMarketOddsList)) {
            i18nOutrightMarketOddsService.saveBatch(insertI18nMarketOddsList);
        }
        sw.stop();
        log.info("::{}::标准投注项注入总耗时{}ms," + sw.prettyPrint(), uuid, sw.getTotalTimeMillis());
        return messageResults;
    }



    /**
     * 1.TX让球比分处理 addition3:主队比分 addition4:客队比分
     * 2.计算全场盘口值 、替换三方盘口源ID
     *
     * @param thirdMarketDTOOddsWrapper
     * @param redisResult
     */
    public void txHandicapDispose(OddsWrapper<ThirdMarketDTO> thirdMarketDTOOddsWrapper, Map<String, Object> redisResult) {
        ThirdMarketDTO thirdMarketDTO = thirdMarketDTOOddsWrapper.getData();
        if (DataSourceCodeEnum.TX.code.equals(thirdMarketDTOOddsWrapper.getDataSourceCode())) {
            String addition3 = thirdMarketDTO.getAddition3();
            String addition4 = thirdMarketDTO.getAddition4();
            if ("0".equals(addition3) && "0".equals(addition4)) {
                //获取比分中心提供主客队比分
                CommonItem goalObj = getFootballCacheScores(redisResult, thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTOOddsWrapper.getMarketCategoryId());
                if (goalObj == null) {
                    return;
                }
                Integer goalHome = goalObj.getHome();
                Integer goalAway = goalObj.getAway();
                //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
                String addition2 = Double.toString(Double.valueOf(thirdMarketDTO.getAddition1()) - (goalHome - goalAway)).replace(".0", "");
                //三方盘口替换
                String thirdCategoryId = thirdMarketDTO.getThirdMarketSourceId().split("_")[1];
                String calculateThirdMarketSourceId = thirdMarketDTOOddsWrapper.getThirdMatchSourceId() + "_" + thirdCategoryId + "_" + addition2 + "_" + thirdMarketDTO.getOfferLineId();
                log.info("::{}::标准赛事ID:{},TX主客队比分更换,源主队比分:{},源客队比分:{},源三方源盘口id:{},源基准分盘口值:{},主队比分:{},客队比分:{},计算出三方源盘口id:{},计算出全场盘口值:{},标准玩法ID:{},比分:{}",
                        thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), addition3, addition4, thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getAddition1(), goalHome, goalAway, calculateThirdMarketSourceId,
                        addition2, thirdMarketDTOOddsWrapper.getMarketCategoryId(), goalObj);
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
                                    thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), calculateThirdMarketSourceId, thirdOddsFieldSourceId, thirdOddsFieldSourceIdStr);
                        }
                    });
                }
            }
        }
    }

    /**
     * 足球获取缓存比分
     * @param redisResult
     * @param standardMatchId
     * @param marketCategoryId
     */
    private CommonItem getFootballCacheScores(Map<String, Object> redisResult, Long standardMatchId, Long marketCategoryId) {
        //获取比分中心提供主客队比分
        FootballCacheScores scores = new FootballCacheScores();
        Object scoreObj = redisResult.get(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + standardMatchId));
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
            scores = thirdMatchMarketProcessor.preScoreBuild();
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
     * @param thirdMarketDTOOddsWrapper
     * @param redisResult
     */
    public void cornerScore(OddsWrapper<ThirdMarketDTO> thirdMarketDTOOddsWrapper, Map<String, Object> redisResult) {
        ThirdMarketDTO thirdMarketDTO = thirdMarketDTOOddsWrapper.getData();
        if (MarginCategoryConfig.FOOTBALL_CORNER_SCORE_CATEGORY.contains(thirdMarketDTOOddsWrapper.getMarketCategoryId())) {
            //获取比分中心提供主客队比分
            FootballCacheScores scores = new FootballCacheScores();
            Object scoreObj = redisResult.get(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + thirdMarketDTOOddsWrapper.getStandardSourceId()));
            if (!Objects.isNull(scoreObj)) {
                scores = JSONObject.parseObject(scoreObj.toString(), FootballCacheScores.class);
                if (scores.getCorner() == null) {
                    scores.setCorner(new CommonItem(0, 0));
                }
            } else {
                scores = thirdMatchMarketProcessor.preScoreBuild();
            }
            //角球玩法
            CommonItem goalObj = scores.getCorner();
            Integer cornerHome = goalObj.getHome();
            Integer cornerAway = goalObj.getAway();
            //TX根据基准分计算角球全场盘口值
            if (DataSourceCodeEnum.TX.code.equals(thirdMarketDTOOddsWrapper.getDataSourceCode())) {
                //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
                String addition2 = Double.toString(Double.valueOf(thirdMarketDTO.getAddition1()) - (cornerHome - cornerAway)).replace(".0", "");
                //三方盘口替换
                String thirdCategoryId = thirdMarketDTO.getThirdMarketSourceId().split("_")[1];
                String calculateThirdMarketSourceId = thirdMarketDTOOddsWrapper.getThirdMatchSourceId() + "_" + thirdCategoryId + "_" + addition2 + "_" + thirdMarketDTO.getOfferLineId();
                log.info("::{}::标准赛事ID:{},TX角球计算全场盘口值,源三方源盘口id:{},源基准分盘口值:{},主队比分:{},客队比分:{},计算出三方源盘口id:{},计算出全场盘口值:{},标准玩法ID:{},比分:{}",
                        thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO.getAddition1(), cornerHome, cornerAway, calculateThirdMarketSourceId,
                        addition2, thirdMarketDTOOddsWrapper.getMarketCategoryId(), scores);
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
                                    thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), calculateThirdMarketSourceId, thirdOddsFieldSourceId, thirdOddsFieldSourceIdStr);
                        }
                    });
                }
            } else if (!DataSourceCodeEnum.BC.code.equals(thirdMarketDTOOddsWrapper.getDataSourceCode()) && !DataSourceCodeEnum.LS.code.equals(thirdMarketDTOOddsWrapper.getDataSourceCode())) {
                //基准分盘口值 = 全场盘口值 + (主队比分 - 客队比分)
                Double addition1 = Double.valueOf(thirdMarketDTO.getAddition2()) + (cornerHome - cornerAway);
                log.info("::{}::标准赛事ID:{},三方源盘口id:{},标准玩法ID:{},角球玩法计算出基准分盘口值:{},源全场盘口值:{},主队比分:{},客队比分:{}",
                        thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTOOddsWrapper.getMarketCategoryId(), addition1, thirdMarketDTO.getAddition2(), cornerHome, cornerAway);
                thirdMarketDTO.setAddition1(String.valueOf(addition1));
                thirdMarketDTO.setAddition3(String.valueOf(cornerHome));
                thirdMarketDTO.setAddition4(String.valueOf(cornerAway));
            }
        }
    }

    /**
     * 15分钟玩法 缓存获取 默认比分
     *
     * @param thirdMarketDTOOddsWrapper
     * @param resultForscoreCenter
     */
    public void fifteenMinutesScore(OddsWrapper<ThirdMarketDTO> thirdMarketDTOOddsWrapper, Map<String, Object> resultForscoreCenter) {
        ThirdMarketDTO thirdMarketDTO = thirdMarketDTOOddsWrapper.getData();
        if (MarginCategoryConfig.FIFTEEN_MINUTES_SCORE.get(thirdMarketDTOOddsWrapper.getMarketCategoryId()) != null) {
            String add2 = thirdMarketDTO.getAddition2();
            String add5 = thirdMarketDTO.getAddition5();
            if (StringUtils.isEmpty(add2) || StringUtils.isEmpty(add5)) {
                thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::15分钟基准分计算,参数不完整关盘处理,赛事ID:{},三方盘口源ID:{},标准玩法ID:{},add2:{},add5:{}",
                        thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTOOddsWrapper.getMarketCategoryId(), add2, add5);
                return;
            }
            //缓存KEY 不存在默认比分 0 - 0
            CommonItem scores = new CommonItem();
            //获取比分中心提供比分
            Object o = resultForscoreCenter.get(DigestUtil.md5Hex("ABSCORES:" + thirdMarketDTOOddsWrapper.getStandardSourceId()));
            if (!ObjectUtils.isEmpty(o)) {
                String key = add5.replace(",", "-");
                Map<String, JSONObject> matchAbScores = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), Map.class);
                FootballCacheScores cacheScores = JSONObject.toJavaObject(matchAbScores.get(key), FootballCacheScores.class);
                if (cacheScores != null) {
                    if ("goal".equals(MarginCategoryConfig.FIFTEEN_MINUTES_SCORE.get(thirdMarketDTOOddsWrapper.getMarketCategoryId()))) {
                        scores = cacheScores.getGoal();
                    } else {
                        scores = cacheScores.getCorner();
                    }
                }
                log.info("::{}::15分钟基准分计算,赛事ID:{},三方盘口源ID:{},标准玩法ID:{},赛事缓存比分数据:{}",
                        thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTOOddsWrapper.getMarketCategoryId(), o);
                thirdMatchMarketProcessor.fifteenMinutesScoreCalculate(thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTO, scores);
            } else {
                //缓存不存在默认 0-0
                log.info("::{}::15分钟基准分计算,赛事缓存KEY不存在,默认0-0比分计算,赛事ID:{},三方盘口源ID:{},标准玩法ID:{}",
                        thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTOOddsWrapper.getMarketCategoryId());
                thirdMatchMarketProcessor.fifteenMinutesScoreCalculate(thirdMarketDTOOddsWrapper.getLinkId(), thirdMarketDTOOddsWrapper.getStandardSourceId(), thirdMarketDTO, scores);
            }
        }
    }


    private void dataSourceChampionMarketExecute( Long uuid, List<StandardSportMarket> championMarketList)
    {
        log.info("::{}::dataSourceChampionMarketExecute开始", uuid);
        List<Long> marketIds = championMarketList.stream().map(StandardSportMarket::getRelationMarketId).collect(Collectors.toList());
        List<ConfigOutrightTradeMarket> tradeMarketList = outrightTradeMarketConfigService.selectList(marketIds);
        Map<Long, ConfigOutrightTradeMarket> tradeMarketMap = null;
        if ( !CollectionUtils.isEmpty(tradeMarketList) ) {
            tradeMarketMap = tradeMarketList.stream().collect(Collectors.toMap(ConfigOutrightTradeMarket::getStandardMarketId, Function.identity()));
        }

        Map<Long, StandardOutrightMarket> outrightMarketMap = null;
        List<StandardOutrightMarket> outrightMarketList = standardOutrightMarketService.queryChampionMarket(marketIds);
        if ( !CollectionUtils.isEmpty(outrightMarketList) ) {
            outrightMarketMap = outrightMarketList.stream().collect( Collectors.toMap(StandardOutrightMarket::getId, Function.identity()));
        }

        List<ConfigOutrightTradeMarket> updateBatchMarketTrade = Lists.newArrayList();
        List<ConfigOutrightTradeMarket> saveBatchMarketTrade = Lists.newArrayList();
        List<StandardOutrightMarket> updateBatchOutrightMarket = Lists.newArrayList();
        for ( StandardSportMarket standardSportMarket : championMarketList ) {

            // 盘口开关的确认
            boolean addStatus = false;
            if ( MapUtils.isNotEmpty(tradeMarketMap) ) {
                if ( tradeMarketMap.containsKey(standardSportMarket.getRelationMarketId()) ) {
                    ConfigOutrightTradeMarket configOutrightTradeMarket = tradeMarketMap.get(standardSportMarket.getRelationMarketId());
                    if ( !Constant.OUTRIGHT_ONE.equals(configOutrightTradeMarket.getOperateType()) &&
                        ( null == configOutrightTradeMarket.getMarketStatus() || !standardSportMarket.getStatus().equals(configOutrightTradeMarket.getMarketStatus()))
                    ) {
                        configOutrightTradeMarket.setMarketStatus(standardSportMarket.getStatus());
                        configOutrightTradeMarket.setLinkId(uuid + "");
                        configOutrightTradeMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        updateBatchMarketTrade.add(configOutrightTradeMarket);
                    }

                    // 冠军盘口的确认
                    if ( MapUtils.isNotEmpty(outrightMarketMap) && outrightMarketMap.containsKey(standardSportMarket.getRelationMarketId()) &&
                            !Constant.OUTRIGHT_ONE.equals(configOutrightTradeMarket.getOperateType()) ) {
                        StandardOutrightMarket standardOutrightMarket = outrightMarketMap.get(standardSportMarket.getRelationMarketId());
                        if ( null == standardOutrightMarket.getMarketStatus() || !standardSportMarket.getStatus().equals(standardOutrightMarket.getMarketStatus()) ) {
                            standardOutrightMarket.setMarketStatus( Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                            standardOutrightMarket.setModfiyTime( TimeUtils.millsSecondsEast8ZoneGmt());
                            updateBatchOutrightMarket.add(standardOutrightMarket);
                        }
                    }

                } else {
                    addStatus = true;
                }
            } else {
                addStatus = true;
            }

            if ( addStatus ) {
                ConfigOutrightTradeMarket configOutrightTradeMarket = new ConfigOutrightTradeMarket();
                configOutrightTradeMarket.setId(UUIdUtils.getId());
                configOutrightTradeMarket.setStandardMatchId(standardSportMarket.getStandardMatchInfoId());
                configOutrightTradeMarket.setStandardMarketId(standardSportMarket.getRelationMarketId());
                configOutrightTradeMarket.setMarketStatus(standardSportMarket.getStatus());
                configOutrightTradeMarket.setLinkId(uuid + "");
                configOutrightTradeMarket.setOperaterId(1L);
                configOutrightTradeMarket.setOperateType(Constant.OUTRIGHT_ZERO);
                configOutrightTradeMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                configOutrightTradeMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                saveBatchMarketTrade.add(configOutrightTradeMarket);
            }

        }

        if ( !CollectionUtils.isEmpty(updateBatchMarketTrade) ) {
            outrightTradeMarketConfigService.updateBatchById(updateBatchMarketTrade);
        }

        if ( !CollectionUtils.isEmpty(saveBatchMarketTrade) ) {
            outrightTradeMarketConfigService.saveBatch(saveBatchMarketTrade);
        }

        if (  !CollectionUtils.isEmpty(updateBatchOutrightMarket) ) {
            standardOutrightMarketService.updateBatchById(updateBatchOutrightMarket);
        }

    }

}
