package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.CommonAsyncService;
import com.panda.merge.component.MarketOddsPlaceProcessor;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.CategoryOppositeConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RiskManagerCodeEnums;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.odds.MarketControlStatusEnum;
import com.panda.merge.dto.odds.MergeMarketStatusEnum;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.PaDataServiceLogProducer;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.*;
import com.panda.merge.odds.service.PlayRiskManagerService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/28 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
public class SoldMessageToOddsProcessor extends BaseProcessor {
    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;

    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;

    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;

    @Autowired
    private I18nOutrightMarketOddsService i18nOutrightMarketOddsService;

    @Autowired
    private I18nOutrightMarketService i18nOutrightMarketService;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private PaDataServiceLogProducer paDataServiceLogProducer;

    @Autowired
    private StandardSportTeamService standardSportTeamService;

    @Autowired
    public LanguageInternationService languageInternationService;

    @Autowired
    public CommonAsyncService commonAsyncService;

    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;

    @Autowired
    private StandardMarketCategoryService standardMarketCategoryService;
    @Autowired
    private MarketOddsPlaceProcessor marketOddsPlaceProcessor;

    @Autowired
    private CategoryCodeProcessor categoryCodeProcessor;

    @Autowired
    private PlayRiskManagerService playRiskManagerService;


    public void soldMessageToOdds(@Valid Request<SoldMessage> request) {
        StopWatch stopWatch = new StopWatch("soldMessageToOdds_" + UUIdUtils.getId());
        stopWatch.start();
        String linkId = request.getLinkId();
        validateLinkId("soldMessageToOdds", request);
        log.info("::{}::soldMessage赔率下发，逻辑处理开始，request={}", linkId, JSON.toJSONString(request));
        SoldMessage soldMessage = request.getData();
        Long standardMatchId = soldMessage.getMatchId();
        /*String dataSourceWeight = thirdMatchMarketProcessor.getDataSourceWeight(standardMatchId, soldMessage.getMarketType());
        if (null == dataSourceWeight) {
            log.info("::{}::soldMessage赔率下发，查询数据源权重为空,标准赛事id={}", linkId, standardMatchId);
            return;
        }*/
        /*String dataSourceWeight = soldMessage.getDataSource();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchId, dataSourceWeight);
        if (null == thirdMatchInfo) {
            log.info("::{}::soldMessage赔率下发，查询三方赛事为空,标准赛事id={},数据源权重={}", linkId, standardMatchId, dataSourceWeight);
            return;
        }*/
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (null == standardMatchInfo) {
            log.info("::{}::soldMessage赔率下发，查询标准赛事为空,标准赛事id={}", linkId, standardMatchId);
            return;
        }
        log.info("::{}:: soldMessage,标准赛事:{}", request.getLinkId(), JSON.toJSONString(standardMatchInfo));
        //刷新开售缓存并返回最新开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(standardMatchId);
        if (null == standardSportMarketSell) {
            log.info("::{}::soldMessage赔率下发，未找到预开售信息,标准赛事id={}", linkId, standardMatchId);
            return;
        }
        refreshStandardMatchBeginTimeByMatchId(linkId,standardMatchInfo);
        //清除玩法开售表缓存
        soldMessage.getMarketCategoryIds().forEach(x -> marketCategorySellService.removeCache(soldMessage.getMatchId(), soldMessage.getMarketType(), x.getCategoryId()));
        //赔率处理
        Map<Long, String> marketCategoryIds = new HashMap<>();
        soldMessage.getMarketCategoryIds().forEach(v -> {
            marketCategoryIds.put(v.getCategoryId(), v.getDataSourceCode());
        });
        soldHandler(linkId, standardMatchInfo, standardSportMarketSell, marketCategoryIds, soldMessage.getMarketType(), false, false);

        //统计处理耗时
        stopWatch.stop();
        paDataServiceLogProducer.sendPaDataServiceLog(
                getPaDataServiceLogDTO(request.getLinkId(), "odds-admin", "SOLD_MESSAGE", "三方赛事信息接入",
                        stopWatch.getTotalTimeMillis(), 200, null)
        );
    }


    /**
     * 刷历史开售数据到缓存
     *
     * @param standardMatchInfo
     * @param standardSportMarketSell
     */
    private void refreshSoldInfo(StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell, Map<String, String> stringHashMap, Integer marketType) {
        String preCode = standardSportMarketSell.getPreMatchDataProviderCode();
        String liveCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        if (marketType == 1) {
            List<MarketCategorySell> pre = marketCategorySellService.getItem(standardMatchInfo.getId(), "1");
            if (!CollectionUtils.isEmpty(pre) && StringUtils.isNotEmpty(preCode)) {
                String preRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
                Map<String, String> perMap = new HashMap<>();
                pre.forEach(e -> {
                    perMap.put(String.valueOf(e.getMarketCategoryId()), e.getDataSourceCode());
                });
                redisService.hSetAll(preRedisKey, perMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
        } else {
            List<MarketCategorySell> live = marketCategorySellService.getItem(standardMatchInfo.getId(), "0");
            if (!CollectionUtils.isEmpty(live) && StringUtils.isNotEmpty(liveCode)) {
                String liveRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 0;
                Map<String, String> liveMap = new HashMap<>();
                live.forEach(e -> {
                    liveMap.put(String.valueOf(e.getMarketCategoryId()), e.getDataSourceCode());
                });
                redisService.hSetAll(liveRedisKey, liveMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
        }
    }

    /**
     * 玩法开售流程
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardSportMarketSell
     * @param categoryDataSourceMap
     * @param marketType
     * @param isRefresh
     */
    @Async("ProcessTradeSystemThreadPool")
    public void soldHandler(String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell, Map<Long, String> categoryDataSourceMap, Integer marketType, boolean isRefresh, boolean isConvertMarket) {
        String preCode = standardSportMarketSell.getPreMatchDataProviderCode();
        String liveCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        log.info("::{}::soldMessage,赛前数据源:{},滚球数据源:{}", linkId, preCode, liveCode);

        // 4405：开售/重建时初始化玩法级操盘模式（只补不盖）
        // 规则（结合赛事开售表/玩法开售表）：
        // - 赛事级操盘=PA：只有“等于赛事数据源”的玩法默认 PA；其他数据源玩法默认走其数据源对应的 XTS 家族（未知则 PA）
        // - 赛事级操盘=XTS 家族：仅“对应数据源”的玩法默认走该 XTS；其他数据源玩法默认走 PA
        // 当前盘型对应的赛事操盘模式（早盘取pre，滚球取live）
        String sellRiskManagerCode = Objects.equals(marketType, 1) ? standardSportMarketSell.getPreRiskManagerCode() : standardSportMarketSell.getLiveRiskManagerCode();
        // 玩法级操盘缓存过期时间（与盘口缓存保持一致）
        Long ttlSeconds = marketCacheTime(standardMatchInfo.getBeginTime());
        // 这里使用入参 categoryDataSourceMap 做玩法级初始化，不在此处强制回源查表。
        // 回源查表发生在下方“isRefresh且soldCache为空”的分支（refreshSoldInfo）。
        if (StringUtils.isNotBlank(sellRiskManagerCode) && !CollectionUtils.isEmpty(categoryDataSourceMap)) {
            // 赛事操盘对应的数据源（XTS家族才有固定映射；PA为null）
            String expectedDataSource = playRiskManagerService.expectedDataSourceCodeForRiskManager(sellRiskManagerCode);
            // 是否PA场景
            boolean isPa = StringUtils.equalsIgnoreCase(sellRiskManagerCode, RiskManagerCodeEnums.PA.name());
            if (isPa) {
                // PA场景基准：赛事当前盘型（早盘/滚球）的赛事数据源
                String matchDataSourceCode = Objects.equals(marketType, 1) ? preCode : liveCode;
                matchDataSourceCode = playRiskManagerService.normalizeSellDataSourceCode(matchDataSourceCode);
                // 与赛事数据源一致 -> PA
                Set<Long> paCategories = new HashSet<>();
                paCategories.addAll(categoryDataSourceMap.keySet());
                    playRiskManagerService.initIfAbsent(
                            linkId,
                            standardMatchInfo.getId(),
                            marketType,
                            paCategories,
                            RiskManagerCodeEnums.PA.name(),
                            ttlSeconds
                    );
            } else if (StringUtils.isBlank(expectedDataSource)) {
                // 未识别的赛事操盘模式：兜底把所有玩法默认写成 sellRiskManagerCode
                playRiskManagerService.initIfAbsent(
                        linkId,
                        standardMatchInfo.getId(),
                        marketType,
                        categoryDataSourceMap.keySet(),
                        sellRiskManagerCode,
                        ttlSeconds
                );
            } else {
                // XTS场景：仅对应数据源玩法走赛事操盘，非对应数据源玩法纠偏为PA
                // 对应数据源玩法（保留XTS）
                Set<Long> xtsCategories = new HashSet<>();
                // 非对应数据源玩法（纠偏PA）
                Set<Long> paCategories = new HashSet<>();
                for (Map.Entry<Long, String> e : categoryDataSourceMap.entrySet()) {
                    // 玩法ID
                    Long categoryId = e.getKey();
                    // 玩法数据源（normalize后参与比对）
                    String ds = playRiskManagerService.normalizeSellDataSourceCode(e.getValue());
                    // 匹配XTS对应数据源 -> 走XTS
                    if (StringUtils.equalsIgnoreCase(expectedDataSource, ds)) {
                        xtsCategories.add(categoryId);
                    } else {
                        // 不匹配 -> 回PA
                        paCategories.add(categoryId);
                    }
                }
                if (!CollectionUtils.isEmpty(xtsCategories)) {
                    playRiskManagerService.initIfAbsent(
                            linkId,
                            standardMatchInfo.getId(),
                            marketType,
                            xtsCategories,
                            sellRiskManagerCode,
                            ttlSeconds
                    );
                }
                if (!CollectionUtils.isEmpty(paCategories)) {
                    playRiskManagerService.initIfAbsent(
                            linkId,
                            standardMatchInfo.getId(),
                            marketType,
                            paCategories,
                            RiskManagerCodeEnums.PA.name(),
                            ttlSeconds
                    );
                }
            }
        }
        //存在关盘盘口玩法 k:数据商,value:玩法集合
        Map<String, List<Long>> closeMarketCategoryIdMap = new HashMap<>();
        //开盘玩法
        Set<Long> openMarketCategoryIdList = new HashSet<>();

        //查询三方赛事赔率
        Map<Long, String> longStringHashMap = new HashMap<>();

        //-------------2785 start---------------
        //todo 2785 如果需要回滚，只需把flag设置为false即可
        AtomicBoolean flag = new AtomicBoolean(false);
        //通知接入的玩法id跟内部站点对应关系map
        Map<Long,String> categoryTxMap = new HashMap<>();
        Map<Long,String> categoryLsMap = new HashMap<>();
        Map<Long,String> categoryL02Map = new HashMap<>();
        // 把玩法->数据源映射做统一标准化分发：
        // 1) longStringHashMap 作为后续核心输入（玩法->标准化数据源）
        // 2) categoryTxMap/categoryLsMap/categoryL02Map 仅用于站点兼容通知
        categoryDataSourceMap.forEach((k, v) -> {
            // T01前缀内部码，统一归并为TX
            if (v.startsWith("T01")){
                flag.set(true);
                categoryTxMap.put(k,v);
                longStringHashMap.put(k,DataSourceCodeEnum.TX.getCode());
            }
            // L01前缀内部码，统一归并为LS
            else if (v.startsWith("L01")){
                flag.set(true);
                categoryLsMap.put(k,v);
                longStringHashMap.put(k,DataSourceCodeEnum.LS.getCode());
            // L02前缀内部码，归并为L02
            }else if (v.startsWith("L02")){
                flag.set(true);
                categoryL02Map.put(k,v);
                longStringHashMap.put(k,DataSourceCodeEnum.L02.getCode());
            }else{
                // 普通数据源保持原样
                longStringHashMap.put(k,v);
            }
        });
        //-------------2785 end---------------
        //k:marketcategory,value:datasourcecode
        Map<String, String> stringHashMap = new HashMap<String, String>();
        //k:datasourcecode,value:marketcategorylist
        Map<String, List<Long>> stringListHashMap = new HashMap<>();
        // 再构建两张索引：
        // stringHashMap: 玩法->数据源（落开售缓存）
        // stringListHashMap: 数据源->玩法列表（按数据源拉三方盘口）
        longStringHashMap.forEach((k, v) -> {
            stringHashMap.put(String.valueOf(k), v);
            if (stringListHashMap.containsKey(v)) {
                stringListHashMap.get(v).add(k);
            } else {
                List<Long> list = new ArrayList<>();
                list.add(k);
                stringListHashMap.put(v, list);
            }
        });
        Long standardMatchInfoId = null;
        //测试联赛不需要下发
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItemByNewId(standardMatchInfo.getId());
        if (null == standardRelationNewStandard) {
            //找出所有其他数据源的三方盘口并下发到风控
            commonAsyncService.getAllThirdSportMarketList(linkId + "_third", standardMatchInfo, marketType, longStringHashMap);
            standardMatchInfoId = standardMatchInfo.getId();
        } else {
            standardMatchInfoId = standardMatchInfo.getId();
            //standardMatchInfoId = standardRelationNewStandard.getSourceStandardId();
        }

        List<ThirdSportMarket> thirdSportMarketList = new ArrayList<>();
        Long finalStandardMatchInfoId = standardMatchInfoId;
        stringListHashMap.forEach((x, y) -> {
            //Ls和Tx数据源的赛事不下发,通知接入重新下发赔率
            if (flag.get() && (x.equals(DataSourceCodeEnum.TX.getCode()) || x.equals(DataSourceCodeEnum.LS.getCode()) || x.equals(DataSourceCodeEnum.L02.getCode()))) {
                return;
            }
            ThirdMatchInfo tempThirdMatchInfo = thirdMatchInfoService.getItemNoCache(finalStandardMatchInfoId, x);
            if (null == tempThirdMatchInfo) {
                log.info("::{}::soldMessage赔率下发，查询三方赛事为空,标准赛事id={},数据源权重={}", linkId, standardMatchInfo.getId(), x);
                return;
            }
            //主客队相反,玩法过滤处理
            log.info("::{}::soldMessage赔率下发，主客队相反需求过滤部分玩法，条件：{},thirdMatchInfo:{}", linkId, ONE.equals(tempThirdMatchInfo.getHomeAwayOpposite()), tempThirdMatchInfo);
            if (ONE.equals(tempThirdMatchInfo.getHomeAwayOpposite())) {
                List<Long> categoryList = new ArrayList<>();
                for (Long category : y) {
                    if (CategoryOppositeConfig.FootBall.containsCategory(category)) {
                        categoryList.add(category);
                    }
                }
                log.info("::{}::soldMessage赔率下发，主客队相反需求过滤部分玩法，原玩法集合：{}，过滤后玩法集合：{}", linkId, y, categoryList);
                y.clear();
                y.addAll(categoryList);
                log.info("::{}::soldMessage赔率下发，主客队相反需求过滤部分玩法，最终开售玩法集合：{}", linkId, y);
            }
            List<ThirdSportMarket> tempList = thirdSportMarketService.getItemList(tempThirdMatchInfo.getId(), x, marketType, y);
            if (isConvertMarket && marketType == 0) {
                if (!CollectionUtils.isEmpty(tempList)) {
                    //找出开盘 盘口玩法
                    List<ThirdSportMarket> openThirdSportMarket = tempList.stream().filter(e -> e.getThirdMarketSourceStatus().equals(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(openThirdSportMarket)) {
                        Set<Long> openMarketCategoryId = openThirdSportMarket.stream().map(ThirdSportMarket::getMarketCategoryId).collect(Collectors.toSet());
                        openMarketCategoryIdList.addAll(openMarketCategoryId);
                        //得到关盘 玩法
                        log.info("::{}::切换数据源:{},切换玩法:{},开盘玩法:{}", linkId, x, y, openMarketCategoryId);
                        y.removeAll(openMarketCategoryId);
                        closeMarketCategoryIdMap.put(x, y);
                    } else {
                        //没有开盘玩法
                        closeMarketCategoryIdMap.put(x, y);
                        log.info("::{}::切换数据:{}，1没有开盘玩法:{}", linkId, x, y);
                    }
                } else {
                    //没有开盘玩法
                    closeMarketCategoryIdMap.put(x, y);
                    log.info("::{}::切换数据源:{}，2没有开盘玩法:{}", linkId, x, y);
                }
            }
            log.info("::{}::tempList,三方赛事ID:{},数据源:{},玩法:{},条数:{}", linkId, tempThirdMatchInfo.getId(), x, y, tempList.size());
            thirdSportMarketList.addAll(tempList);
        });
        /*if (CollectionUtils.isEmpty(thirdSportMarketList)) {
            log.info("::{}::soldMessage赔率下发，三方盘口列表为空, 操盘类型={}，玩法集合={}", linkId,marketType, marketCategoryIds);
            return;
        }*/
        // 开售滚球判断是否有滚球数据 存在关闭赛前盘 ，并下发即将开赛标识。
        if (0 == marketType) {
            //下发滚球标识 ，清除水差，盘口差
            if (StringUtils.isNotBlank(liveCode)) {
                List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketList.stream().filter(e -> e.getMarketType() == 0).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(thirdSportMarkets)) {
                    //滚球关闭赛前盘
                    if (StringUtils.isNotBlank(preCode)) {
                        thirdMatchMarketProcessor.newClosePreMarkets(linkId, standardSportMarketSell, marketType, standardMatchInfo, System.currentTimeMillis(), false, new ArrayList<>(), 0);
                    }
                }
            }
        }

        //是否在早盘阶段操作滚球赛事级权重切换
        boolean preTradeLive  = thirdMatchMarketProcessor.isOddsLive(standardMatchInfo.getId())==1 && marketType == 0;
        //以下数据库逻辑操作有并发问题，这里需要以赛事维度加redis锁
        String lockValue = UUIdUtils.getId() + "_" + linkId;
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
        log.info("::{}::soldMessage赔率下发,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        redisService.tryLock(redisLocKey, lockValue, 10, 8);
        log.info("::{}::soldMessage赔率下发,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap1 = new HashMap<>();
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap = new HashMap<>();
        try {
            //处理玩法开售对应的数据源,存储开售过的玩法以及数据源的对应关系
            //这个key也做为判断是否是历史数据，如果没有说明是历史数据，需要走之前的缓存逻辑，如果有说明是新数据，走新的缓存逻辑
            //开售这里不需要判断，都走新逻辑
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + marketType;
            Map<String, String> soldCache = redisService.hGetAll(categoryRedisKey);
            //如果非空，说明是切换逻辑
            //切换逻辑：1.找出原开售的玩法，需要做关盘处理，2.缓存清除，3.更新新的赔率源code，4.新的三方玩法需要走开售流程
            //历史数据第一次切换玩法赔率源时，需要把开售的玩法刷到缓存
            // 仅在“切换重建场景 && 玩法开售缓存为空”时，回源查询 market_category_sell 并回填缓存。
            // 这一步是 soldHandler 内部唯一的查表入口。
            // 命中该条件才“回源查表”：
            // - isRefresh=true（手动切换/重建流程）
            // - soldCache为空（玩法开售缓存不存在）
            if (MapUtil.isEmpty(soldCache) && isRefresh) {
                // 通过 marketCategorySellService.getItem(matchId, marketType) 查表并回填缓存
                refreshSoldInfo(standardMatchInfo, standardSportMarketSell, stringHashMap, marketType);
                // 回填后重新读取缓存，供 oldMarketHandler 对比使用
                soldCache = redisService.hGetAll(categoryRedisKey);
            }
            if (isRefresh && !preTradeLive) {
                standardMarketDataMessageMap1 = oldMarketHandler(linkId, standardMatchInfo, soldCache, categoryDataSourceMap,marketType);
            }

            redisService.hSetAll(categoryRedisKey, stringHashMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            marketCategorySellService.removeCashes(standardMatchInfoId,marketType,categoryDataSourceMap.keySet());
            if (preTradeLive){
                //早盘阶段操作滚球赛事级权重切换，操作完玩法开售缓存直接结束
                return;
            }
            Map<String, StandardMarketDataMessage> standardMarketDataMessageMap2 = processMarketBySold(linkId, standardMatchInfo, thirdSportMarketList);
            //获取构建盘口缓存
            String redisConvertKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CONVERT_MARKET + standardMatchInfo.getId());
            Map<String, StandardMarketDataMessage> standardMarketConvertMessageMap = redisService.hGetAll(redisConvertKey);
//            log.info("::{}::closeMarketCategoryIdMap:{},standardMarketConvertMessageMap:{},standardMarketDataMessageMap2:{},stringListHashMap:{},standardMarketDataMessageMap1：{},thirdSportMarketList：{}"
//                    , linkId, closeMarketCategoryIdMap, JSONObject.toJSONString(standardMarketConvertMessageMap),
//                    JSONObject.toJSONString(standardMarketDataMessageMap2), stringListHashMap, standardMarketDataMessageMap1, thirdSportMarketList.size());
            if (!CollectionUtils.isEmpty(standardMarketConvertMessageMap)) {
                // 关盘玩法获取构建盘口缓存，并赋值给标准赔率缓存
                if (!CollectionUtils.isEmpty(closeMarketCategoryIdMap)) {
                    //玩法分组
                    Map<Long, List<StandardMarketDataMessage>> standardMarketConvertMessage = standardMarketConvertMessageMap.values().stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
                    //关盘玩法 找出构建盘口赔率
                    closeMarketCategoryIdMap.forEach((x, y) -> {
                        y.forEach(closeMarketCategoryId -> {
                            //构建盘口赔率
                            List<StandardMarketDataMessage> convertMarketDataMessages = standardMarketConvertMessage.get(closeMarketCategoryId);
                            if (!CollectionUtils.isEmpty(convertMarketDataMessages)) {
                                log.info("::{}::关盘玩法找出构建盘口赔率,数据源:{},玩法ID:{},条数:{}", linkId, x, closeMarketCategoryId, convertMarketDataMessages.size());
                                //修改数据商为切换数据商
                                convertMarketDataMessages.forEach(m -> {
                                    //删除标准赔率缓存，不删除会有缓存和数据源不匹配
                                    String redisKey1 = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + m.getDataSourceCode()+"_"+m.getMarketCategoryId());
                                    redisService.hDel(redisKey1, m.getRelationMarketId().toString());
                                    m.setDataSourceCode(x);
                                    //TX盘口ID需要特殊处理  //TX生成统一盘口ID特殊处理 盘口值规则生成赋值:SendData ,三方数据源盘口ID生成赋值:RelationMarketId
                                    StandardSportMarket standardSportMarket = new StandardSportMarket();
                                    BeanUtils.copyProperties(m, standardSportMarket);
                                    if (DataSourceCodeEnum.TX.code.equals(m.getDataSourceCode())) {
                                        m.setRelationMarketId(Long.valueOf(standardSportMarketService.txCreateRelationMarketId(m.getThirdMarketSourceId())));
                                        m.setSendData(standardSportMarketService.createRelationMarketId(linkId, standardSportMarket).toString());
                                        standardSportMarket.setSendData(m.getSendData());
                                    } else {
                                        m.setRelationMarketId(standardSportMarketService.createRelationMarketId(linkId, standardSportMarket));
                                    }
                                    List<StandardMarketOddsDataMessage> marketOddsList = m.getMarketOddsList();
                                    if (!CollectionUtils.isEmpty(marketOddsList)) {
                                        marketOddsList.forEach(o -> {
                                            o.setRelationMarketId(m.getRelationMarketId());
                                            StandardSportMarketOdds marketOdds = new StandardSportMarketOdds();
                                            BeanUtils.copyProperties(o, marketOdds);
                                            o.setRelationMarketOddsId(standardSportMarketOddsService.createRelationMarketOddsId(marketOdds, standardSportMarket));
                                            if (DataSourceCodeEnum.TX.code.equals(m.getDataSourceCode())) {
                                                o.setRemark(marketOdds.getRemark());
                                            }
                                        });
                                    }
                                    //赛前转滚球设置内部数据源
                                    String key = Constant.REDIS_KEY.THIRD_MATCH_WITCH_DATA_SOURCE_KEY + standardMatchInfo.getId() + "_" + x;
                                    Object o = redisService.get(key);
                                    log.info("::{}::赛前转滚球设置内部数据源:{},obj:{}",linkId,key,o);
                                    m.setInternalDataSourceCode(null == o ? null : o.toString());
                                    //加入标准盘口缓存
                                    standardMarketDataMessageMap2.put(m.getRelationMarketId().toString(), m);
                                    //刷新构建盘口缓存
                                    redisService.hSet(redisConvertKey, m.getRelationMarketId().toString(), m, marketCacheTime(standardMatchInfo.getBeginTime()));
                                });
                            }
                        });
                    });
                }
//                log.info("::{}::openMarketCategoryIdList:{},standardMarketConvertMessageMap:{}"
//                        , linkId, openMarketCategoryIdList, JSONObject.toJSONString(standardMarketConvertMessageMap));
                //其他数据源有滚球赔率 删除构建缓存
                if (!CollectionUtils.isEmpty(openMarketCategoryIdList)) {
                    standardMarketConvertMessageMap.forEach((k, v) -> {
                        if (openMarketCategoryIdList.contains(v.getMarketCategoryId())) {
                            StandardMarketDataMessage standardMarketDataMessage = standardMarketDataMessageMap2.get(v.getRelationMarketId().toString());
                            if (standardMarketDataMessage != null) {
                                //删除标准赔率缓存，不删除会有缓存和数据源不匹配
                                String redisKey1 = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode()+"_"+standardMarketDataMessage.getMarketCategoryId());
                                redisService.hDel(redisKey1, v.getRelationMarketId().toString());
//                                standardMarketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//                                standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
//                                standardMarketDataMessageMap2.put(v.getRelationMarketId().toString(), standardMarketDataMessage);
                            }
                            redisService.hDel(redisConvertKey, k);
                            log.info("::{}::切换数据源,盘口来源删除构建盘口缓存,盘口ID:{},k:{},三方盘口ID:{},玩法ID:{}",
                                    linkId, v.getRelationMarketId(), k, v.getThirdMarketSourceId(), v.getMarketCategoryId());
                        }
                    });
                }
            }
//            log.info("::{}::对缓存所有数据进行排序,marketCategoryIds：{}，standardMarketDataMessageMap2:{}", linkId, categoryDataSourceMap.keySet(), standardMarketDataMessageMap2);
            //对缓存所有数据进行排序
            marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketDataMessageMap2, standardMatchInfo, categoryDataSourceMap.keySet(), null, null, null, false);
            Map<String, List<StandardMarketDataMessage>> stringStandardMarketDataMessageMap = standardMarketDataMessageMap2.values().stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getDataSourceCode));
            stringStandardMarketDataMessageMap.forEach((k, v) -> {
                for (StandardMarketDataMessage marketDataMessage : v) {
                    String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + k + "_" + marketDataMessage.getMarketCategoryId());
                    boolean result = redisService.hSet(redisKey, marketDataMessage.getRelationMarketId().toString(),marketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                    log.info("::{}:: soldMessage刷新缓存盘口信息, key={},marketId={},result={}", linkId, redisKey, marketDataMessage.getRelationMarketId(), result);
                }
            });
            log.info("::{}::处理赔率数据,marketCategoryIds:{},soldCache:{}",
                    linkId, categoryDataSourceMap,soldCache);
            //处理赔率数据
            if (MapUtil.isNotEmpty(standardMarketDataMessageMap1)) {
                marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkId, standardMarketDataMessageMap1, standardMatchInfo, categoryDataSourceMap.keySet(), null, null, null, false);
                //log.info("::{}::standardMarketDataMessageMap1:{}", linkId, JSONObject.toJSONString(standardMarketDataMessageMap1));
                log.info("::{}::categoryDataSourceMap size={}", linkId, categoryDataSourceMap.size());
                log.info("::{}::standardMarketDataMessageMap1 size={}", linkId, standardMarketDataMessageMap1.size());
                List<StandardMarketDataMessage> list2 = standardMarketDataMessageMap2.values().stream().collect(Collectors.toList());
                Map<Long,List<StandardMarketDataMessage>> map2 = list2.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
                for (String key : standardMarketDataMessageMap1.keySet()) {
                    StandardMarketDataMessage marketDataMessage = standardMarketDataMessageMap1.get(key);
                    if (MapUtil.isEmpty(standardMarketDataMessageMap2) || !standardMarketDataMessageMap2.containsKey(key)) {
                        if ((categoryDataSourceMap.get(marketDataMessage.getMarketCategoryId()).contains("L01") || categoryDataSourceMap.get(marketDataMessage.getMarketCategoryId()).contains("T01") || categoryDataSourceMap.get(marketDataMessage.getMarketCategoryId()).contains("L02"))
                                && (soldCache.get(marketDataMessage.getMarketCategoryId() + "").contains("LS") || soldCache.get(marketDataMessage.getMarketCategoryId() + "").contains("TX")|| soldCache.get(marketDataMessage.getMarketCategoryId() + "").contains("L02"))) {
                            continue;
                        }
                        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + marketDataMessage.getDataSourceCode() + "_" + marketDataMessage.getMarketCategoryId());
                        redisService.hDel(redisKey, marketDataMessage.getRelationMarketId().toString());
                        marketDataMessage.setRemark("操盘后台切换玩法数据源1，该数据源玩法关盘");
                        if (shouldMarkSwitchOldClose(marketDataMessage)) {
                            marketDataMessage.oldClose();
                        }
                    }
                    if (isRefresh
                            && marketDataMessage.getMarketType()==0
                            && marketDataMessage.getMarketSource()==1){
                        marketDataMessage.invalidDataSource();
                        continue;
                    }
                    marketDataMessage.setDataSourceCode(longStringHashMap.get(marketDataMessage.getMarketCategoryId()));
                    marketDataMessage.setInternalDataSourceCode("");
                    if ((StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())||StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()))
                            &&marketDataMessage.getPlaceNum() == 1
                            && !map2.containsKey(marketDataMessage.getMarketCategoryId())
                            && !isXtsManager(linkId,standardMatchInfo,marketType)) {
                        if (categoryDataSourceMap.get(marketDataMessage.getMarketCategoryId()).contains("L01") || categoryDataSourceMap.get(marketDataMessage.getMarketCategoryId()).contains("L02")) {
                            String tempDataSourceCode = categoryDataSourceMap.get(marketDataMessage.getMarketCategoryId()).contains("L01") ? DataSourceCodeEnum.LS.getCode() : DataSourceCodeEnum.L02.getCode();
                            marketDataMessage.setDataSourceCode(tempDataSourceCode);
                            marketDataMessage.setInternalDataSourceCode(categoryDataSourceMap.get(marketDataMessage.getMarketCategoryId()));
                        }
                    }
                }
                standardMarketDataMessageMap.putAll(standardMarketDataMessageMap1);
            }
            if (MapUtil.isNotEmpty(standardMarketDataMessageMap2)) {
                log.info("::{}::standardMarketDataMessageMap2:{}", linkId, standardMarketDataMessageMap2.size());
                standardMarketDataMessageMap.putAll(standardMarketDataMessageMap2);
            }
            //如果开售了赛前盘并且有滚球盘口下来，需要把所有赛前盘关掉
            //玩法分组，
            if (MapUtils.isNotEmpty(standardMarketDataMessageMap)) {
                Map<Long, List<StandardMarketDataMessage>> standardMarketMap = standardMarketDataMessageMap.values().stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
                for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMap.entrySet()) {
                    Long marketCategoryId = entry.getKey();
                    // 关转封只针对外部数据源盘口，内部站点 LS/TX/L02/L01/T01 不参与
                    List<StandardMarketDataMessage> standardMarketDataMessageStream = entry.getValue().stream()
                            .filter(s -> !isInternalOddsSource(s.getDataSourceCode()))
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(standardMarketDataMessageStream)) {
                        boolean anyMatch = standardMarketDataMessageStream.stream().anyMatch(f -> f.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        if (!anyMatch) {
                            standardMarketDataMessageStream.forEach(s -> s.setColseMarket(2));
                        }
                    }
                    //L01 T01 切换 其他数据，其他数据源存在关盘不走关转封
                    if (soldCache.containsKey(marketCategoryId + "") && categoryDataSourceMap.containsKey(marketCategoryId)) {
                        if ((soldCache.get(marketCategoryId + "").contains("LS") || soldCache.get(marketCategoryId + "").contains("TX")|| soldCache.get(marketCategoryId + "").contains("L02"))
                                && (!categoryDataSourceMap.get(marketCategoryId).contains("L01") && !categoryDataSourceMap.get(marketCategoryId).contains("T01")&& !categoryDataSourceMap.get(marketCategoryId).contains("L02"))) {
                            entry.getValue().stream().filter(s -> s.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.SUSPENDED).forEach(s -> s.setColseMarket(2));
                        }
                    }
                    // 新数据源是 L01/T01/L02：本次切换 oldClose 的外部盘走关转封；切换前已是关盘的不 reopen
                    if (categoryDataSourceMap.containsKey(marketCategoryId)
                            && isInternalOddsSource(categoryDataSourceMap.get(marketCategoryId))) {
                        boolean isMainCategory =
                                (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())
                                        && MarginCategoryConfig.FootBall_MAIN3484_CATEGORY.contains(marketCategoryId))
                                        || (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())
                                        && MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY.contains(marketCategoryId));
                        if (isMainCategory) {
                            // map1 流程 617 行会把旧盘 dataSourceCode 改成 LS/TX，不能仅按外部源过滤
                            applyInternalSwitchCloseDisplay(linkId, entry.getValue());
                        }
                    }
                }
            }
        } finally {
            redisService.unLock(redisLocKey, lockValue);
            log.info("::{}::soldMessage赔率下发,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        }
        // 篮球滚球中途开售：初始化自动开盘 Redis，避免 PANDA 路径误开未来节次玩法
        initAutoOpenMarketOnSold(linkId, standardMatchInfo, marketType, categoryDataSourceMap.keySet());
        //下发当前最新赔率
        thirdMatchMarketProcessor.processOddsByAll(linkId,-1,null, standardMatchInfo, categoryDataSourceMap.keySet(), standardMarketDataMessageMap, System.currentTimeMillis(), standardSportMarketSell, new HashMap<>());
        //通知LS，TX下发赔率
        if (MapUtils.isNotEmpty(categoryTxMap)){
            ThirdMatchInfo tempThirdMatchInfo = thirdMatchInfoService.getItemNoCache(finalStandardMatchInfoId, DataSourceCodeEnum.TX.code);
            categoryCodeProcessor.processToApi(linkId+"_TX", tempThirdMatchInfo,categoryTxMap,marketType);
        }
        if (MapUtils.isNotEmpty(categoryLsMap)){
            ThirdMatchInfo tempThirdMatchInfo = thirdMatchInfoService.getItemNoCache(finalStandardMatchInfoId, DataSourceCodeEnum.LS.code);
            categoryCodeProcessor.processToApi(linkId+"_LS", tempThirdMatchInfo,categoryLsMap,marketType);
        }
        if (MapUtils.isNotEmpty(categoryL02Map)){
            ThirdMatchInfo tempThirdMatchInfo = thirdMatchInfoService.getItemNoCache(finalStandardMatchInfoId, DataSourceCodeEnum.L02.code);
            categoryCodeProcessor.processToApi(linkId+"_L02", tempThirdMatchInfo,categoryL02Map,marketType);
        }


        //刷新玩法开售缓存
        /*List<String> redisKey = new ArrayList<>();
        longStringHashMap.forEach((k, v) -> {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::MarketCategorySell:" + standardMatchInfo.getId() + "-" + k + "-" + marketType;
            redisKey.add(key);
        });
        redisService.del(redisKey);
        log.info("::{}::刷新玩法开售缓存,赛事ID:{},clearRedisKey:{}", linkId, standardMatchInfo.getId(), redisKey);*/
    }

    /**
     * 旧的数据源盘口关盘处理，删除缓存标准盘口
     *
     * @param standardMatchInfo
     * @param oldStringHashMap
     * @param marketCategoryIds
     */
    private Map<String, StandardMarketDataMessage> oldMarketHandler(String linkId, StandardMatchInfo standardMatchInfo, Map<String, String> oldStringHashMap, Map<Long, String> marketCategoryIds,int marketType) {
        Map<String, String> oldData = oldStringHashMap.entrySet().stream().filter(e -> marketCategoryIds.containsKey(Long.parseLong(e.getKey())) && !marketCategoryIds.get(Long.parseLong(e.getKey())).equalsIgnoreCase(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String, StandardMarketDataMessage> finalStandardMarketDataMessageMap = new HashMap<>();
        //        Map<String, List<Long>> oldStringListHashMap = new HashMap<>();
//        oldData.forEach((k, v) -> {
//            if (oldStringListHashMap.containsKey(v)) {
//                oldStringListHashMap.get(v).add(Long.parseLong(k));
//            } else {
//                List<Long> list = new ArrayList<>();
//                list.add(Long.parseLong(k));
//                oldStringListHashMap.put(v, list);
//            }
//        });

        //以下数据库逻辑操作有并发问题，这里需要以赛事维度加redis锁
        /*String lockValue = UUIdUtils.getId() + "_" + linkId;
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
        log.info("::{}::oldMarketHandler,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId,
                redisLocKey, lockValue);
        redisService.tryLock(redisLocKey, lockValue, 5, 3);
        log.info("::{}::oldMarketHandler,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId,
                redisLocKey, lockValue);*/
        try {
            log.info("::{}::oldMarketHandler:{},",linkId,oldData);
            boolean isXTS = isXtsManager(linkId, standardMatchInfo, marketType);
            oldData.forEach((k, v) -> {
                // 目标数据源：决定是否允许保留旧内部盘
                String targetDataSource = marketCategoryIds.get(Long.parseLong(k));
                boolean targetIsInternal = isInternalOddsSource(targetDataSource);
                String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + v + "_" + k);
                Map<String, StandardMarketDataMessage> temp = redisService.hGetAll(redisKey);
                if(MapUtils.isEmpty(temp)){
                    return;
                }
                temp.values().forEach(e -> {
                    if (isXTS && shouldMarkSwitchOldClose(e)) {
                        e.oldClose();
                    }
                    boolean oldIsInternal = isInternalOddsSource(e.getDataSourceCode());
                    boolean isMainCategory =
                            (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) && MarginCategoryConfig.FootBall_MAIN3484_CATEGORY.contains(e.getMarketCategoryId()))
                                    || (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) && MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY.contains(e.getMarketCategoryId()));

                    // 关键修复：从内部盘(L01/T01/L02)切到外部盘(BC/BG/SR/...)时，旧内部盘必须 oldClose，否则投注项仍可能继续使用 L01。
                    // 只有当“目标仍是内部盘”时，才允许延续旧内部盘逻辑（避免空盘/保留内部盘展示习惯）。
                    // 切换前已是关盘的不再 oldClose，避免覆盖自动关盘/手动关盘等历史状态，后续关转封才能正确区分。
                    if (!(isMainCategory && oldIsInternal && targetIsInternal) && shouldMarkSwitchOldClose(e)) {
                        e.oldClose();
                    }
                    redisService.hDel(redisKey, e.getRelationMarketId().toString());
                    finalStandardMarketDataMessageMap.put(String.valueOf(e.getRelationMarketId()), e);
                });
            });
        } finally {
            /*redisService.unLock(redisLocKey, lockValue);
            log.info("::{}::oldMarketHandler,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);*/
        }
        return finalStandardMarketDataMessageMap;
    }

    private boolean isInternalOddsSource(String dataSourceCode) {
        if (StringUtils.isBlank(dataSourceCode)) {
            return false;
        }
        String ds = dataSourceCode.toUpperCase(Locale.ROOT);
        // 内部盘：LS(L01)/TX(T01)/L02 既可能是外层 code，也可能是内部站点 code 前缀
        return ds.contains("LS") || ds.contains("TX") || ds.contains("L02") || ds.startsWith("L01") || ds.startsWith("T01");
    }

    /**
     * 切换时需要标记为「旧源关盘」的盘口：当前仍为开/封状态（非历史关盘）。
     */
    private boolean shouldMarkSwitchOldClose(StandardMarketDataMessage market) {
        return market.getThirdMarketSourceStatus() != null
                && market.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED;
    }

    /**
     * 切到内部站点时的关转封处理（作用于玩法下全部盘口，含 617 行改写 dataSourceCode 后的旧盘）。
     * - 历史关盘：colseMarket=2，保持 DEACTIVATED
     * - 本次切换 oldClose 的旧源盘：colseMarket=1，DEACTIVATED → SUSPENDED
     * - 新内部站点盘：不干预
     */
    private void applyInternalSwitchCloseDisplay(String linkId, List<StandardMarketDataMessage> categoryMarkets) {
        categoryMarkets.forEach(market -> {
            if (shouldKeepClosedOnInternalSwitch(market)) {
                market.setColseMarket(2);
                log.info("::{}::切换内部站点,历史关盘保持关盘,colseMarket=2,relationMarketId={},thirdStatus={},oldClose={}",
                        linkId, market.getRelationMarketId(), market.getThirdMarketSourceStatus(), market.isOldClose());
                return;
            }
            if (!market.isOldClose()) {
                return;
            }
            market.setColseMarket(1);
            if (!CollectionUtils.isEmpty(market.getMarketOddsList())) {
                market.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                market.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
            }
        });
    }

    private boolean shouldKeepClosedOnInternalSwitch(StandardMarketDataMessage market) {
        if (Constant.AOTU_CLOSE_STATUS.equals(market.getAutoCloseStatus())) {
            return true;
        }
        if (Integer.valueOf(1).equals(market.getEndEdStatus())) {
            return true;
        }
        // 切换前已是关盘（非本次 oldMarketHandler oldClose）的不做关转封
        boolean deactivated = Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(market.getThirdMarketSourceStatus())
                || Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(market.getStatus());
        return deactivated && !market.isOldClose();
    }

    private boolean isXtsManager(String linkId,StandardMatchInfo standardMatchInfo,int marketType){
        boolean isXts = false;
        try{
            String delKey = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + standardMatchInfo.getId();
            Object obj = redisService.get(delKey);
            if (obj != null){
                StandardSportMarketSell standardSportMarketSell = (StandardSportMarketSell)obj;
                if (standardSportMarketSell!=null){
                    if (marketType == 0){
                        isXts = standardSportMarketSell.getLiveRiskManagerCode().equalsIgnoreCase(RiskManagerCodeEnums.PA.name())?false:true;
                    }else{
                        isXts = standardSportMarketSell.getPreRiskManagerCode().equalsIgnoreCase(RiskManagerCodeEnums.PA.name())?false:true;
                    }
                }
            }
        }catch (Exception e){

        }
        return isXts;
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
        if (CollectionUtils.isEmpty(thirdSportMarketList)) {
            return map;
        }

        thirdSportMarketList.forEach(thirdSportMarket -> {
            // 主客队相反：盘口处理
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), thirdSportMarket.getDataSourceCode());
            log.info("::{}::玩法开售流程，三方赛事信息 thirdMatchInfo：{}，null != thirdMatchInfo：{}，判断条件：{}", linkId, thirdMatchInfo, null != thirdMatchInfo, (null != thirdMatchInfo && thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code) && ONE.equals(thirdMatchInfo.getHomeAwayOpposite())));
            if (null != thirdMatchInfo && thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code) && ONE.equals(thirdMatchInfo.getHomeAwayOpposite())) {
                thirdMatchMarketProcessor.changeThirdMarketContent(linkId, thirdSportMarket);
            }
            //获取赛种玩法
            StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryService.getItem(thirdSportMarket.getMarketCategoryId(), standardMatchInfo.getSportId());
            log.info("::{}::判断玩法是否开启, sportId:{},categoryId:{},standardSportMarketCategory{},thirdSportMarket:{}",
                    linkId, standardMatchInfo.getSportId(), thirdSportMarket.getMarketCategoryId(), standardSportMarketCategory, JSONObject.toJSONString(thirdSportMarket));
            //赛种不支持玩法
            if (standardSportMarketCategory == null
                    || Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getStatus())
                    || (thirdSportMarket.getDataSourceCode().equalsIgnoreCase(DataSourceCodeEnum.AO.code) && Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getAoStatus()))) {
                log.info("::{}::processMarketBySold 玩法状态为关闭，关闭赛事盘口，标准赛事id:{},赛种id:{},玩法id:{}", linkId, standardMatchInfo.getId(), standardMatchInfo.getSportId(), thirdSportMarket.getMarketCategoryId());
                return;
            }

            //查询标准盘口数据
            StandardSportMarket standardSportMarket = standardSportMarketService.getItem(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getThirdMarketSourceId(), standardMatchInfo.getId());
            //处理标准盘口，不存在新增，存在更新
            if (standardSportMarket == null) {
                standardSportMarket = standardSportMarketService.create(linkId, standardMatchInfo, thirdSportMarket, standardSportMarketCategory.getScopeId());
                if (standardSportMarket == null) {
                    log.info("::{}::标准盘口创建失败,标准赛事id={},三方盘口={},三方盘口状态={}", linkId, standardMatchInfo.getId(), thirdSportMarket.getThirdMarketSourceId(), thirdSportMarket.getStatus());
                    return;
                }
                if (standardSportMarket.getMarketType() == 2) {
                    List<I18nOutrightMarket> i18nOutrightMarketList = i18nOutrightMarketService.selectI18nOutrightMarketList(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getNameCode());
                    log.info("::{}::processMarketBySold冠军标准盘口下发多语言:{}", linkId, i18nOutrightMarketList);
                    if (!CollectionUtils.isEmpty(i18nOutrightMarketList)) {
                        StandardSportMarket finalStandardSportMarket = standardSportMarket;
                        i18nOutrightMarketList.forEach(i18nOutrightMarket -> {
                            i18nOutrightMarket.setNameCode(finalStandardSportMarket.getNameCode());
                        });
                        standardMarketOddsProducer.marketNameI18nSend(linkId + "cname_code", i18nOutrightMarketList,
                                                                      standardMatchInfo.getId());
                    } else {
                        log.info("::{}::processMarketBySold冠军标准盘口开售create,盘口id:{},三方nameCode:{},国际化信息为空", linkId, standardSportMarket.getRelationMarketId(), thirdSportMarket.getNameCode());
                    }
//                if (DataSourceCodeEnum.PA.name().equals(standardSportMarket.getDataSourceCode())) {
                    //通知风控进行操盘方式的初始化
                    standardMarketOddsProducer.toInitTradeType( linkId, standardSportMarket );
//                }
                }
            } else {
                if (standardSportMarket.getMarketType() == 2) {
                    List<I18nOutrightMarket> i18nOutrightMarketList = i18nOutrightMarketService.selectI18nOutrightMarketList(standardSportMarket.getDataSourceCode(), standardSportMarket.getNameCode());
                    if (!CollectionUtils.isEmpty(i18nOutrightMarketList)) {
                        standardMarketOddsProducer.marketNameI18nSend(linkId + "name_code", i18nOutrightMarketList,
                                                                      standardMatchInfo.getId());
                    } else {
                        log.info("::{}::processMarketBySold冠军标准盘口开售,盘口id:{},nameCode:{},国际化信息为空", linkId, standardSportMarket.getRelationMarketId(), standardSportMarket.getNameCode());
                    }
                }
                //TX修改时间 需要用三方表的最新修改时间 修改时间为空取保存时间
                log.info("::{}::需要用三方表的最新修改时间,数据源：{}，三方盘口id:{},三方表修改时间：{}", linkId, standardSportMarket.getDataSourceCode(), standardSportMarket.getThirdMarketSourceId(), thirdSportMarket.getModifyTime());
                standardSportMarket.setModifyTime(!Objects.isNull(thirdSportMarket.getModifyTime()) ? thirdSportMarket.getModifyTime() : thirdSportMarket.getCreateTime());
                standardSportMarket.setStatus(thirdSportMarket.getStatus());
                standardSportMarket.setThirdMarketSourceStatus(thirdSportMarket.getStatus());
                standardSportMarket.setMarketType(thirdSportMarket.getMarketType());
                standardSportMarket.setLinkId(linkId);
                standardSportMarket.setOddsName(thirdSportMarket.getOddsName());
                standardSportMarket.setAddition1(thirdSportMarket.getAddition1());
                standardSportMarket.setAddition2(thirdSportMarket.getAddition2());
                standardSportMarket.setAddition3(thirdSportMarket.getAddition3());
                standardSportMarket.setAddition4(thirdSportMarket.getAddition4());
                MergeFunctionUtils.setNumberOfWinners( standardSportMarket, thirdSportMarket.getNumberOfWinners());
                standardSportMarket.setInternalDataSourceCode(thirdSportMarket.getInternalDataSourceCode());
                standardSportMarket.setEventType(thirdSportMarket.getEventType());
                //异步执行更新
                standardSportMarketService.updateByPrimaryKeySelective(standardSportMarket);
            }
            //处理标准盘口投注项
            log.info("::{}::三方投注项数据查询, 数据源:{}, 三方盘口id:{}", linkId, thirdSportMarket.getDataSourceCode(), thirdSportMarket.getId() );
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketOddsService.getItemList(thirdSportMarket.getDataSourceCode(), thirdSportMarket.getId());
            List<StandardSportMarketOdds> standardSportMarketOddsList = new ArrayList<>();
            if (null != thirdMatchInfo && thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code) && ONE.equals(thirdMatchInfo.getHomeAwayOpposite())) {
                thirdMatchMarketProcessor.changeThirdMarketOddsContent(linkId, thirdSportMarketOddsList, thirdSportMarket);
            }
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                log.info("::{}::处理标准盘口投注项,赛事ID:{},三方盘口ID:{},数据源:{},三方表盘口ID:{},条数:{}",
                        linkId, standardMatchInfo.getId(), thirdSportMarket.getId(), thirdSportMarket.getDataSourceCode(), JSONObject.toJSONString(thirdSportMarketOddsList));
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
                            //查询1133需求投注项是否有对应球队的多语言
                            /*if (StringUtils.isNotEmpty(thirdSportMarketOdds.getAddition1())) {
                                this.toMegerBetRadarTeamName(linkId, standardMatchInfo.getSportId(), standardMatchInfo.getDataSourceCode(), thirdSportMarketOdds, i18nOutrightMarketOddsList);
                            }*/
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
                        standardSportMarketOdds.setOddsType(thirdSportMarketOdds.getOddsType());
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
                    }
                    standardSportMarketOddsList.add(standardSportMarketOdds);
                }
            }
            //新增玩法投注项排序
            if (!CollectionUtils.isEmpty(standardSportMarketOddsList) && MarginCategoryConfig.ODDS_ORDER.contains(standardSportMarket.getMarketCategoryId())) {
                thirdMatchMarketProcessor.oddsOrderByOddsType(standardSportMarketOddsList, standardSportMarket.getMarketCategoryId());
                for (int i = 0; i < standardSportMarketOddsList.size(); i++) {
                    standardSportMarketOddsList.get(i).setOrderOdds(i + 1);
                }
            }
            //将盘口及盘口投注项封装到一起
            StandardMarketDataMessage standardMarketDataMessage = thirdMatchMarketProcessor.convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket, TimeUtils.millsSecondsEast8ZoneGmt() - 10 * 1000);
            map.put(String.valueOf(standardMarketDataMessage.getRelationMarketId()), standardMarketDataMessage);
            //将最新盘口刷入缓存
            //将最新盘口刷入缓存,兼容冠军盘
            String marketKey;
            if (2 == standardSportMarket.getMarketType()) {
                marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode();
            } else {
                marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + standardMarketDataMessage.getDataSourceCode() + "_" + standardMarketDataMessage.getMarketCategoryId());
            }
            log.info("::{}::准备缓存赔率信息，key={}, hashKey={}, hashValue={}", linkId, marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage);
            if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getStatus())) {
                standardMarketDataMessage.setShowMarketResult(1);
            }
            if (MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarketDataMessage.getDataSourceCode())) {
                standardMarketDataMessage.setPlaceNum(null == thirdSportMarket.getOfferLineId() ? 999 : thirdSportMarket.getOfferLineId());
            }
            redisService.hSet(marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
            String marketKey1 = Constant.REDIS_KEY.RONGHE_ORDER_STANDARD_MARKET + standardMatchInfo.getId();
            redisService.hSet(marketKey1, standardMarketDataMessage.getMarketCategoryId().toString(), 1, marketCacheTime(standardMatchInfo.getBeginTime()));
        });
        return map;
    }

    /**
     * 根据 1133需求，冠军赛事投注项的多语言如果与球队的多语言可以对应，那么直接使用球队的多语言；
     * 优先级为数据商下发的投注项多语言，为空则取值球队的多语言
     *
     * @return
     */
    private void toMegerBetRadarTeamName(String linkId, Long sportId, String dataSourceCode, ThirdSportMarketOdds thirdSportMarketOdds, List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList) {
        Map<String, I18nOutrightMarketOdds> marketOddsMap = i18nOutrightMarketOddsList.stream().collect(Collectors.toMap(I18nOutrightMarketOdds::getLanguageType, Function.identity()));
        //根据
        Integer betRadarId = Integer.parseInt(thirdSportMarketOdds.getAddition1());
        if (betRadarId > 1) {
            StandardSportTeam standardSportTeam = standardSportTeamService.getStandardTeamByBetRadarId(sportId, dataSourceCode, betRadarId);
            if (standardSportTeam != null) {
                log.info("::{}::, 冠军赛事球队信息:{}", linkId, JSON.toJSONString(standardSportTeam));
                if (Objects.isNull(standardSportTeam.getNameCode())) {
                    return;
                }
                List<LanguageInternation> languageInternations = languageInternationService.getLanguageInternationByNameCode(standardSportTeam.getNameCode());
                if (languageInternations.isEmpty()) {
                    return;
                }
                Map<String, String> liMap = languageInternations.stream().collect(Collectors.toMap(LanguageInternation::getLanguageType, LanguageInternation::getText));
                //老数据的同步
                for (I18nOutrightMarketOdds i18nOutrightMarketOdds : i18nOutrightMarketOddsList) {
                    if (liMap.containsKey(i18nOutrightMarketOdds.getLanguageType())) {
                        i18nOutrightMarketOdds.setText(liMap.get(i18nOutrightMarketOdds.getLanguageType()));
                        i18nOutrightMarketOdds.setFlag(ConstantSystem.ONE);
                        i18nOutrightMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                    }
                }
                //新数据的添加
                for (LanguageInternation li : languageInternations) {
                    if (!marketOddsMap.containsKey(li.getLanguageType())) {
                        I18nOutrightMarketOdds i18nOutrightMarketOdds = new I18nOutrightMarketOdds();
                        i18nOutrightMarketOdds.setLanguageType(li.getLanguageType());
                        i18nOutrightMarketOdds.setText(li.getText());
                        i18nOutrightMarketOdds.setDataSourceCode(dataSourceCode);
                        i18nOutrightMarketOdds.setFlag(ConstantSystem.ONE);
                        i18nOutrightMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        i18nOutrightMarketOdds.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
                        i18nOutrightMarketOddsList.add(i18nOutrightMarketOdds);
                    }
                }
            }
        }
    }
}
