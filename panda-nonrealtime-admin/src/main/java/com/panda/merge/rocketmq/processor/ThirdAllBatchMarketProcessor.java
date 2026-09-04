package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.RateLimiterHandler;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.component.InitializeComponent;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.odds.ThirdMarketMonitor;
import com.panda.merge.rocketmq.common.MyCalculationMarketProcessor;
import com.panda.merge.rocketmq.common.ThirdMarketBallHeadProcessor;
import com.panda.merge.rocketmq.producer.ThirdSportMarketMergeProducer;
import com.panda.merge.service.*;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Component
@Async("ProcessAllThirdMarketThreadPool")
public class ThirdAllBatchMarketProcessor extends BaseProcessor {
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    public ThirdSportMarketMergeProducer thirdSportMarketMergeProducer;
    @Autowired
    RedisService redisService;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;
    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private MyCalculationMarketProcessor myCalculationMarketProcessor;
    @Autowired
    private StandardSportPlayerService standardSportPlayerService;
    @Autowired
    InitializeComponent initializeComponent;
    @Autowired
    private ConfigMarketCategoryMarginService configMarketCategoryMarginService;
    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;
    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;
    @Autowired
    private ThirdMarketBallHeadProcessor thirdMarketBallHeadProcessor;

    @Autowired
    private ThirdMarketMonitor thirdMarketMonitor;

    @Resource
    private RateLimiterHandler rateLimiterHandler;

    @ExceptionHelper
    public void execute(@Valid List<Request<ThirdMatchMarketDTO>> requests) {
        // 3929 【融合】数据商异常下发告警&数据下发限频
        if (!rateLimiterHandler.filter()) {
            log.info("进入限流状态,百家赔不下发!");
            return ;
        }

        long befTimeMain = System.currentTimeMillis();
        String linkIds = requests.stream().map(Request::getLinkId).collect(Collectors.joining("-"));
        Long uuid = UUIdUtils.getId();
        log.info("::{}:: 百家赔批量拉取开始: {} 请求size: {}", linkIds, uuid, requests.size());
        //组装
        List<OddsWrapper<ThirdMatchMarketDTO>> oddsWrappers = requests.stream().filter(t -> !CollectionUtils.isEmpty(t.getData().getMarketList())).map(t -> {
            OddsWrapper<ThirdMatchMarketDTO> oddsWrapper = new OddsWrapper<>();
            ThirdMatchMarketDTO dto = t.getData();
            oddsWrapper.setData(dto);
            oddsWrapper.setLinkId(t.getLinkId());
            oddsWrapper.setDataSourceTime(t.getDataSourceTime());
            oddsWrapper.setDataSourceCode(dto.getDataSourceCode().split("-")[0].toUpperCase());
            oddsWrapper.setThirdMatchSourceId(dto.getThirdMatchSourceId());
            oddsWrapper.setMarketType(dto.getMarketList().get(0).getMarketType());
            oddsWrapper.setSportId(dto.getSportId());
            return oddsWrapper;
        }).collect(Collectors.toList());

        //三方获取赛事
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItemsByMarketDTO(oddsWrappers);
        List<String> thirdMatchInfoIdsIsNull = thirdMatchInfos.stream().filter(thirdMatchInfo -> null == thirdMatchInfo.getReferenceId()).map(ThirdMatchInfo::getThirdMatchSourceId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(thirdMatchInfoIdsIsNull)) {
            cacheThirdMarket(thirdMatchInfoIdsIsNull, oddsWrappers);
        }
        //三方赛事源id，标准赛事ID
        Map<String, Long> thirdAndStandardMatchIdMap = thirdMatchInfos.stream().filter(thirdMatchInfo -> null != thirdMatchInfo.getReferenceId()).collect(Collectors.toMap(ThirdMatchInfo::getThirdMatchSourceId, ThirdMatchInfo::getReferenceId, (v1, v2) -> v1));
        if (thirdAndStandardMatchIdMap.isEmpty()) {
            return;
        }
        // 三方数据源id，三方赛事ID
        Map<String, Long> thirdAndThirdMatchIdMap = thirdMatchInfos.stream().filter(thirdMatchInfo -> null != thirdMatchInfo.getReferenceId()).collect(Collectors.toMap(ThirdMatchInfo::getThirdMatchSourceId, ThirdMatchInfo::getId, (v1, v2) -> v1));

        //获取标准赛事信息
        List<Long> standardMatchIds = thirdMatchInfos.stream().filter(thirdMatchInfo -> null != thirdMatchInfo.getReferenceId() && 0 != thirdMatchInfo.getReferenceId()).map(ThirdMatchInfo::getReferenceId).collect(Collectors.toList());
        //标准赛事
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(standardMatchIds);
        //标准赛事ID,标准赛事信息
        Map<Long, StandardMatchInfo> standardMatchInfoMap = standardMatchInfos.stream().collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity(), (v1, v2) -> v1));
        Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap = thirdMatchInfos.stream().filter(thirdMatchInfo -> null != thirdMatchInfo.getReferenceId()).collect(Collectors.toMap(ThirdMatchInfo::getId, Function.identity(), (v1, v2) -> v1));
        //查询标准玩法KEY
        Map<String, List<OddsWrapper<ThirdMarketDTO>>> thirdMarketMapDTO = oddsWrappers.stream().flatMap(t -> t.getData().getMarketList().stream().map(market -> {
            OddsWrapper<ThirdMarketDTO> oddsWrapper = new OddsWrapper();
            oddsWrapper.setLinkId(t.getLinkId());
            oddsWrapper.setDataSourceCode(t.getDataSourceCode());
            oddsWrapper.setDataSourceTime(t.getDataSourceTime());
            oddsWrapper.setThirdMatchId(t.getThirdMatchId());
            oddsWrapper.setStandardSourceId(t.getStandardSourceId());
            oddsWrapper.setThirdMatchSourceId(t.getThirdMatchSourceId());
            oddsWrapper.setMarketType(t.getMarketType());
            oddsWrapper.setData(market);
            oddsWrapper.setSportId(thirdMatchInfoBasedIdMap.getOrDefault(t.getStandardSourceId(),new ThirdMatchInfo()).getSportId());
            oddsWrapper.setUuid(t.getUuid());
            return oddsWrapper;
        })).collect(Collectors.groupingBy(t -> t.getDataSourceCode() + "-" + t.getData().getThirdMarketCategorySourceId()));
        //
        List<OddsWrapper<ThirdMarketDTO>> ballThirdMarketDTOs  = new ArrayList<>();
        List<OddsWrapper<ThirdMarketDTO>> monitorMarkets = new ArrayList<>();

        //获取到标准玩法
        List<ThirdMarketCategory> thirdMarketCategories = thirdMarketCategoryService.getItems(thirdMarketMapDTO.keySet().stream().collect(Collectors.toList()));
        //三方玩法id,标准玩法信息
        Map<String, ThirdMarketCategory> thirdMarketCategorysMap = thirdMarketCategories.stream().collect(Collectors.toMap(ThirdMarketCategory::getThirdSourceId, thi -> thi));
        //linkid分组后处理信息
        Map<String, OddsWrapper<ThirdMatchMarketDTO>> thirdMatchMarketMap = oddsWrappers.stream().collect(Collectors.toMap(t -> t.getLinkId(), thi -> thi));
        log.info("::{}:: 百家赔批量拉取开始, 查询完成开始处理数据: {}", uuid, thirdMatchMarketMap.size());
        Map<String, Long> autoOpenDataSourceCodeMatchMap = redisService.hGetAll(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE_MATCH);
        //处理数据
        for (Map.Entry<String, OddsWrapper<ThirdMatchMarketDTO>> thirdMatchMarketDTOEntry : thirdMatchMarketMap.entrySet()) {
            long befTime = System.currentTimeMillis();
            String linkId = thirdMatchMarketDTOEntry.getKey();
            ThirdMatchMarketDTO thirdMatchMarketDTOValue = thirdMatchMarketDTOEntry.getValue().getData();
            String dataSourceCode = thirdMatchMarketDTOValue.getDataSourceCode();
            String thirdMatchSourceId = thirdMatchMarketDTOValue.getThirdMatchSourceId();
            Long standardMatchId = thirdAndStandardMatchIdMap.get(thirdMatchSourceId);
            Long modifyTime = thirdMatchMarketDTOValue.getModifyTime();
            if (null == standardMatchId || standardMatchId == 0) {
                log.info("::{}::百家赔：标准赛事未找到,三方赛事ID:{}", linkId, thirdMatchSourceId);
                continue;
            }
            //存储需要下发的三方数据商盘口集合
            List<ThirdSportMarketMessage> thirdSportMarketMessages = new ArrayList<>();
            StandardMatchInfo standardMatchInfo = standardMatchInfoMap.get(standardMatchId);
            if (null == standardMatchInfo) {
                continue;
            }
            //赛事ID,玩法:数据源 集合
            Map<Long, Set<String>> autoOpenCategoryMap = new ConcurrentHashMap<>();
            Map<String, List<ThirdMarketDTO>> thirdMarketDTOMap = thirdMatchMarketDTOValue.getMarketList().stream().collect(Collectors.groupingBy(ThirdMarketDTO::getThirdMarketCategorySourceId));
            for (Map.Entry<String, List<ThirdMarketDTO>> entry : thirdMarketDTOMap.entrySet()) {
                String thirdCategorySourceId = entry.getKey();
                ThirdMarketCategory thirdMarketCategory = thirdMarketCategorysMap.get(thirdCategorySourceId);
                if (null == thirdMarketCategory) {
                    log.info("::{}::百家赔：未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                    continue;
                }

                log.info("::{}::百家赔:接收数据源赔率开始,开始处理盘口数据:{}", linkId, thirdCategorySourceId);
                List<ThirdMarketDTO> value = entry.getValue();
                log.info("::{}::百家赔:接收数据源赔率开始,玩法id:{} ,异步处理数据开始", linkId, thirdCategorySourceId);

                for (ThirdMarketDTO thirdMarketDTO : value) {
                    thirdMarketDTO.setMarketCategoryId(thirdMarketCategory.getReferenceId());
                    ThirdSportMarketMessage thirdSportMarketMessage = copyThirdMarketDTO(thirdMarketDTO);
                    List<ThirdSportMarketOdds> thirdSportMarketOddsList = new ArrayList<>();
                    thirdSportMarketMessage.setMarketCategoryId(thirdMarketCategory.getReferenceId());
                    thirdSportMarketMessage.setDataSourceCode(dataSourceCode);
                    thirdSportMarketMessage.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
                    thirdSportMarketMessage.setStatus(thirdMarketDTO.getStatus());
                    //thirdSportMarketMessage.setRelationMarketId(thirdSportMarketService.getRelationMarketId(linkId, standardMatchInfo.getId(), thirdSportMarketMessage.getMarketCategoryId(), thirdSportMarketMessage.getAddition1(), thirdSportMarketMessage.getAddition2(), thirdSportMarketMessage.getAddition3(), thirdSportMarketMessage.getAddition4(), thirdSportMarketMessage.getAddition5(), thirdSportMarketMessage.getMarketType(), thirdSportMarketMessage.getThirdMarketSourceId()));
                    thirdSportMarketMessage.setReferenceId(standardMatchId);
                    //自动开盘站点校验
                    autoOpenMarket(autoOpenDataSourceCodeMatchMap, linkId, dataSourceCode, standardMatchId, standardMatchInfo, autoOpenCategoryMap, thirdMarketCategory, thirdSportMarketMessage);
                    if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                        for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                            ThirdSportMarketOdds thirdSportMarketOdds = copyThirdMarketOddsDTO(thirdMarketOddsDTO);
                            thirdSportMarketOdds.setDataSourceCode(dataSourceCode);
                            //thirdSportMarketOdds.setMarketId(thirdSportMarketMessage.getRelationMarketId());
                            //thirdSportMarketOdds.setId(thirdSportMarketOddsService.getRelationMarketOddsId(thirdSportMarketMessage.getRelationMarketId(), thirdSportMarketOdds.getOddsType(), thirdSportMarketOdds.getThirdOddsFieldSourceId(), thirdSportMarketOdds.getAddition1(), thirdSportMarketMessage.getMarketCategoryId()));
                            thirdSportMarketOddsList.add(thirdSportMarketOdds);
                        }
                    }
                    thirdSportMarketMessage.setThirdSportMarketOddsList(thirdSportMarketOddsList);
                    thirdSportMarketMessages.add(thirdSportMarketMessage);
                    if (dataSourceCode.contains("TX") || dataSourceCode.contains("LS") || dataSourceCode.contains("L02")) {
                        OddsWrapper<ThirdMarketDTO> oddsWrapper = new OddsWrapper();
                        oddsWrapper.setLinkId(linkId);
                        oddsWrapper.setDataSourceCode(dataSourceCode);
                        oddsWrapper.setDataSourceTime(modifyTime);
                        oddsWrapper.setStandardSourceId(standardMatchId);
                        oddsWrapper.setMarketCategoryId(thirdMarketCategory.getReferenceId());
                        oddsWrapper.setThirdMatchSourceId(thirdAndThirdMatchIdMap.get(thirdMatchSourceId) + "");
                        oddsWrapper.setMarketType(thirdMarketDTO.getMarketType());
                        oddsWrapper.setSportId(thirdMatchMarketDTOEntry.getValue().getSportId());
                        oddsWrapper.setData(thirdMarketDTO);
                        monitorMarkets.add(oddsWrapper);
                        //球头参数
                        if ((standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode()) &&
                                MarginCategoryConfig.BALL_HEAD_AO_CATEGORY.contains(thirdMarketDTO.getMarketCategoryId())) ||
                                (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.getCode()) &&
                                        MarginCategoryConfig.BASKETBALL_HEAD_AO_CATEGORY.contains(thirdMarketDTO.getMarketCategoryId()))) {

                            ballThirdMarketDTOs.add(oddsWrapper);
                        }
                    }
                }
                log.info("::{}::百家赔:接收数据源赔率开始,玩法id:{} ,处理盘口信息完成:{}", linkId, thirdCategorySourceId, value.size());
            }
            if (!CollectionUtils.isEmpty(thirdSportMarketMessages)) {
                //设置盘口id
                getMarketId(linkId, standardMatchInfo, thirdSportMarketMessages);
                //TX百家赔 初盘
                allTxThirdFistMarket(dataSourceCode, standardMatchInfo, thirdMatchMarketDTOValue.getMarketList());
                //LS百家赔 初盘
                allLsThirdFistMarket(dataSourceCode, standardMatchInfo, thirdMatchMarketDTOValue.getMarketList());
                //N01/N02数据源处理
                //n0nDataSourceOddsHandle(dataSourceCode, linkId, standardMatchInfo, thirdSportMarketMessages, standardMatchInfo.getSportId());
                thirdSportMarketMergeProducer.sendThirdSportMarketMessageToMQ(linkId + "_" + dataSourceCode + "_third", standardMatchInfo, thirdSportMarketMessages, modifyTime);
                log.info("::{}::百家赔:接收数据源赔率开始,三方赛事id:{},数据源编码={},耗时：{}", linkId, thirdMatchSourceId,dataSourceCode, System.currentTimeMillis() - befTime);
            }
            log.info("::{}:: 百家赔批量拉取开始: {} 处理完成: {}", uuid, System.currentTimeMillis() - befTimeMain, requests.size());
            cheackAutoOpenCategoryMap(linkId, autoOpenCategoryMap);
        }
        thirdMarketMonitor.monitorInternalSite(uuid, monitorMarkets);
        //百家赔球头 T01 L01
        thirdMarketBallHeadProcessor.thirdMarketBallHead(ballThirdMarketDTOs, thirdMatchInfoBasedIdMap, standardMatchInfoMap);
        thirdMarketBallHeadProcessor.thirdBasketballMarketBallHead(ballThirdMarketDTOs, thirdMatchInfoBasedIdMap, standardMatchInfoMap);
    }

    private void cheackAutoOpenCategoryMap(String linkId, Map<Long, Set<String>> autoOpenCategoryMap) {
        if (MapUtils.isNotEmpty(autoOpenCategoryMap)) {
            //赛事级别
            for (Long key : autoOpenCategoryMap.keySet()) {
                Map<String, String> cacheCategoryDataSourceCodeMap = redisService.hGetAll(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + key);
                if (MapUtils.isEmpty(cacheCategoryDataSourceCodeMap)) {
                    redisService.hDel(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE_MATCH, key + "");
                    continue;
                }
                //玩法，数据源
                Map<String, String> categoryDataSourceCodeMap = autoOpenCategoryMap.get(key).stream().map(c -> c.split(":")).collect(Collectors.toMap(keyValue ->keyValue[0], keyValue -> keyValue[1]));
                log.info("::{}::categoryDataSourceCodeMap:{}，cacheCategoryDataSourceCodeMap：{},key:{}", linkId,categoryDataSourceCodeMap,cacheCategoryDataSourceCodeMap,Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + key);
                //通知风控切换
                Map<String, String> categoryDataSourceCodeNew = new ConcurrentHashMap<>();
                Set<String> dels = Collections.synchronizedSet(new HashSet());
                for (String cacheCategoryId : cacheCategoryDataSourceCodeMap.keySet()) {
                    String dataSourceCodeCache = cacheCategoryDataSourceCodeMap.get(cacheCategoryId);
                    String dataSourceCode = categoryDataSourceCodeMap.get(cacheCategoryId);
                    if (null != dataSourceCode) {
                        if (StringUtils.equals(dataSourceCodeCache, dataSourceCode)) {
                            categoryDataSourceCodeNew.put(cacheCategoryId, dataSourceCodeCache);
                            dels.add(cacheCategoryId);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(dels)) {
                    redisService.hDel(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + key, dels.toArray());
                    //通知风控
                    thirdSportMarketMergeProducer.sendAutoOpenDataSourceCodeNewToMq(linkId + "_NEW", key, categoryDataSourceCodeNew);
                }
            }
        }
    }

    private static void autoOpenMarket(Map<String, Long> autoOpenDataSourceCodeMatchMap, String linkId, String dataSourceCode, Long standardMatchId, StandardMatchInfo standardMatchInfo, Map<Long, Set<String>> autoOpenCategoryMap, ThirdMarketCategory thirdMarketCategory, ThirdSportMarketMessage thirdSportMarketMessage) {
        //auto open 校验开盘盘口 只对站点处理
        if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())
                && (dataSourceCode.contains("TX") || dataSourceCode.contains("LS")|| dataSourceCode.contains("L02"))
                && MarginCategoryConfig.FootBall_MAIN_CATEGORY.contains(thirdMarketCategory.getReferenceId())
                && thirdSportMarketMessage.getMarketType() == 0
                && (MapUtil.isNotEmpty(autoOpenDataSourceCodeMatchMap) && autoOpenDataSourceCodeMatchMap.containsKey(standardMatchId + ""))) {
            String dataSourceCodeFinal = dataSourceCode;
            if (dataSourceCode.contains(DataSourceCodeEnum.TX.code)) {
                dataSourceCodeFinal = "T01";
            } else if (dataSourceCode.contains(DataSourceCodeEnum.LS.code)) {
                dataSourceCodeFinal = "L01";
            } else if (dataSourceCode.contains(DataSourceCodeEnum.L02.code)) {
                dataSourceCodeFinal = "L02";
            }
            if (thirdSportMarketMessage.getStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)) {
                if (autoOpenCategoryMap.containsKey(standardMatchId)) {
                    Set<String> existItems = autoOpenCategoryMap.get(standardMatchId);
                    existItems.add(thirdMarketCategory.getReferenceId() + ":" + dataSourceCodeFinal);
                    autoOpenCategoryMap.put(standardMatchId, existItems);
                } else {
                    Set<String> newItems = Collections.synchronizedSet(new HashSet());
                    newItems.add(thirdMarketCategory.getReferenceId() + ":" + dataSourceCodeFinal);
                    autoOpenCategoryMap.put(standardMatchId, newItems);
                }
            }
            log.info("::{}::autoOpenCategoryMap:{},matchId:{}", linkId, autoOpenCategoryMap, standardMatchId);
        }
    }

    private void n0nDataSourceOddsHandle(String dataSourceCode, String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessagesSort, Long sportId) {
        try {
            List<ThirdSportMarketMessage> thirdSportMarketMessages = thirdSportMarketMessagesSort.stream().filter(e -> (MarginCategoryConfig.FootBall_3446_3447_CATEGORY.contains(e.getMarketCategoryId()) || MarginCategoryConfig.BasketBall_3446_3447_CATEGORY.contains(e.getMarketCategoryId()))).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(thirdSportMarketMessages)) {
                return;
            }
            String dataSourceCodeDB = dataSourceCode.split("-")[0].toUpperCase();
              if (!DataSourceCodeEnum.N01.getCode().equals(dataSourceCodeDB) && !DataSourceCodeEnum.N02.getCode().equals(dataSourceCodeDB)) {
                return;
            }
            log.info("::{}::n0nDataSourceOddsHandle,赔率计算开始", linkId);
            log.info("::{}::n0nDataSourceOddsHandle,开始排序", linkId);
            thirdSportMarketMergeProducer.setPlaceNum(thirdSportMarketMessages);
            log.info("::{}::n0nDataSourceOddsHandle,排序完成", linkId);
            //三方盘口消息体先转换成标准盘口消息体
            List<StandardMarketDataMessage> standardMarketDataMessages = thirdSportMarketMessages.stream().map(e -> {
                StandardMarketDataMessage v = thirdMarketConvertStandard(e);
                if (null != v) {
                    v.setChildMarketCategoryId((CategoryUtils.getChildCategoryId(linkId, v.getMarketCategoryId(), v.getAddition1(), v.getAddition2(), v.getAddition3(), v.getAddition4(), v.getAddition5(), String.valueOf(v.getStandardMatchInfoId()))));
                }
                return v;
            }).collect(Collectors.toList());


            Set<Long> marketCategoryIdSet = standardMarketDataMessages.stream().map(StandardMarketDataMessage::getMarketCategoryId).collect(Collectors.toSet());
            //MY玩法
            Set<Long> marketCategoryIdMALAY = new HashSet<>();
            //两项盘EU玩法
            List<Long> marketCategoryIdEu = new ArrayList<>();
            marketCategoryDistinguish(marketCategoryIdSet, marketCategoryIdMALAY, marketCategoryIdEu, sportId);
            //取操盘两项盘玩法id集
            //取操盘两项盘玩法id集,并根据玩法分组
            Map<Long, List<StandardMarketDataMessage>> standardMarketMapMALAY = standardMarketDataMessages.stream().filter(e -> marketCategoryIdMALAY.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));

            //篮球两项盘/足球三项盘口(两项盘)margin计算玩法 根据玩法分组
            Set<Long> finalMarketCategoryIdEUROPE = marketCategoryIdSet.stream().filter(marketCategoryIdEu::contains).collect(Collectors.toSet());
            Map<Long, List<StandardMarketDataMessage>> standardMarketMapEUROPE = standardMarketDataMessages.stream().filter(e -> finalMarketCategoryIdEUROPE.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));

            //取剩余玩法id集，并根据玩法分组
            Set<Long> marketCategoryIdOTHER = marketCategoryIdSet.stream().filter(e -> !marketCategoryIdMALAY.contains(e) && !finalMarketCategoryIdEUROPE.contains(e)).collect(Collectors.toSet());
            Map<Long, List<StandardMarketDataMessage>> standardMarketMapOTHER = standardMarketDataMessages.stream().filter(e -> marketCategoryIdOTHER.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
            List<StandardMarketDataMessage> standardMarketMessageList = new ArrayList<>();
            //--------------操盘两项盘spread计算------------------
            if (!CollectionUtils.isEmpty(standardMarketMapMALAY)) {
                standardMarketMapMalay(linkId, standardMatchInfo, standardMarketMessageList, standardMarketMapMALAY);
            }
            //--------------三项盘/两项盘margin计算,其他球类使用数据商抽水赔率------------------
            if (!CollectionUtils.isEmpty(standardMarketMapEUROPE)) {
                standardMarketMapEurope(linkId, standardMatchInfo, standardMarketMessageList, standardMarketMapEUROPE);
            }
            //------------其余玩法处理--------------------
            if (!CollectionUtils.isEmpty(standardMarketMapOTHER)) {
                //循环遍历盘口信息，设置低赔和赔率差
                setOddsMetricAndLowOddsForMTS(linkId, standardMarketMessageList, standardMarketMapOTHER, standardMatchInfo);
            }
            //设置三方盘口信息数据
            thirdSportMarketMessages.forEach(e -> {
                standardMarketConvertThird(standardMarketMessageList, e);
            });
        } catch (Exception e) {
            log.info("::{}::n0nDataSourceOddsHandle,赔率计算,异常:{}", linkId, e.getMessage(), e);
        }
        log.info("::{}::n0nDataSourceOddsHandle,赔率计算完成", linkId);
    }

    private StandardMarketDataMessage thirdMarketConvertStandard(ThirdSportMarketMessage thirdSportMarketMessage) {
        StandardMarketDataMessage standardMarketDataMessage = new StandardMarketDataMessage();
        BeanUtils.copyProperties(thirdSportMarketMessage, standardMarketDataMessage);
        standardMarketDataMessage.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
        standardMarketDataMessage.setThirdMarketSourceStatus(thirdSportMarketMessage.getThirdMarketSourceStatus());
        standardMarketDataMessage.setStatus(thirdSportMarketMessage.getStatus());
        standardMarketDataMessage.setMarketType(thirdSportMarketMessage.getMarketType());
        standardMarketDataMessage.setPlaceNum(thirdSportMarketMessage.getPlaceNum());
        standardMarketDataMessage.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
        MergeFunctionUtils.setNumberOfWinners( standardMarketDataMessage, thirdSportMarketMessage.getNumberOfWinners());
        standardMarketDataMessage.setAddition1(thirdSportMarketMessage.getAddition1());
        standardMarketDataMessage.setAddition2(thirdSportMarketMessage.getAddition2());
        standardMarketDataMessage.setAddition3(thirdSportMarketMessage.getAddition3());
        standardMarketDataMessage.setAddition4(thirdSportMarketMessage.getAddition4());
        standardMarketDataMessage.setAddition5(thirdSportMarketMessage.getAddition5());
        standardMarketDataMessage.setMarketCategoryId(thirdSportMarketMessage.getMarketCategoryId());
        standardMarketDataMessage.setOldThirdMarketSourceStatus(thirdSportMarketMessage.getThirdMarketSourceStatus());
        standardMarketDataMessage.setDataSourceCode(thirdSportMarketMessage.getDataSourceCode());
        standardMarketDataMessage.setThirdMarketSourceId(thirdSportMarketMessage.getThirdMarketSourceId());
        standardMarketDataMessage.setStandardMatchInfoId(thirdSportMarketMessage.getReferenceId());
        if (!CollectionUtils.isEmpty(thirdSportMarketMessage.getThirdSportMarketOddsList())) {
            standardMarketDataMessage.setMarketOddsList(thirdSportMarketMessage.getThirdSportMarketOddsList().stream().map(e -> {
                StandardMarketOddsDataMessage standardMarketOddsDataMessage = new StandardMarketOddsDataMessage();
                BeanUtils.copyProperties(e, standardMarketOddsDataMessage);
                standardMarketOddsDataMessage.setRelationMarketOddsId(e.getId());
                //N01/N02数据源默认使用公平赔率
                standardMarketOddsDataMessage.setPaOddsValue(e.getOriginalOddsValue());
                standardMarketOddsDataMessage.setOddsValue(e.getOddsValue());
                standardMarketOddsDataMessage.setOriginalOddsValue(e.getOriginalOddsValue());
                standardMarketOddsDataMessage.setThirdOddsFieldSourceId(e.getThirdOddsFieldSourceId());
                standardMarketOddsDataMessage.setOddsFieldsTemplateId(e.getOddsFieldsTemplateId());
                return standardMarketOddsDataMessage;
            }).collect(Collectors.toList()));
        }
        return standardMarketDataMessage;
    }

    private void thirdOddsConvertStandard() {

    }

    private void standardMarketConvertThird(List<StandardMarketDataMessage> standardMarketDataMessages, ThirdSportMarketMessage thirdSportMarketMessage) {
        standardMarketDataMessages.stream().filter(e -> e.getRelationMarketId().equals(thirdSportMarketMessage.getRelationMarketId())).findFirst().ifPresent(e -> {
            thirdSportMarketMessage.setThirdMarketSourceStatus(e.getThirdMarketSourceStatus());
            thirdSportMarketMessage.setStatus(e.getStatus());
            if (!CollectionUtils.isEmpty(e.getMarketOddsList()) && !thirdSportMarketMessage.getThirdSportMarketOddsList().isEmpty()) {
                standardOddsConvertThird(e.getMarketOddsList(), thirdSportMarketMessage.getThirdSportMarketOddsList());
            }
        });
    }

    private void standardOddsConvertThird(List<StandardMarketOddsDataMessage> standardOddsDataMessages, List<ThirdSportMarketOdds> thirdSportMarketOddsList) {
        for (StandardMarketOddsDataMessage standardOddsDataMessage : standardOddsDataMessages) {
            thirdSportMarketOddsList.stream().filter(e -> e.getId().equals(standardOddsDataMessage.getRelationMarketOddsId())).findFirst().ifPresent(e -> {
                e.setActive(standardOddsDataMessage.getActive());
                e.setPaOddsValue(standardOddsDataMessage.getPaOddsValue());
                //N01/N02数据源需要用反算的赔率
                e.setOddsValue(standardOddsDataMessage.getPaOddsValue());
            });
        }
    }

    private void standardMarketMapMalay(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessageList, Map<Long, List<StandardMarketDataMessage>> standardMarketMapMALAY) {
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapMALAY.entrySet()) {
            //获取key对应的盘口对象集合
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            //---------处理有效盘口------------
            List<StandardMarketDataMessage> standardMarketsValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(standardMarketsValid)) {
                //开盘中的盘口计算和封装
                if (MarginCategoryConfig.TRADER_SUPPORT_SPORT.contains(standardMatchInfo.getSportId())) {
                    //新算法计算
                    myCalculationMarketProcessor.calculationMarketProcessor(linkId, standardMatchInfo, entry.getKey(), standardMarketsValid);
                }
            }
            //standardMarketDataMessages.forEach(standardMarketDataMessage -> standardMarketDataMessage.setCategoryType("MY"));
            standardMarketMessageList.addAll(standardMarketDataMessages);
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
                }
            }
            standardMarketMessageList.addAll(standardMarketDataMessagesList);
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
                    //获取盘口投注项
                    List<StandardMarketOddsDataMessage> marketOddsList = standardMarketDataMessage.getMarketOddsList();
                    //852需求 查询独赢配置获取多项盘概率差,只对足球处理
                    Map<String, ConfigMarketMarginGap> marginGapMap = new HashMap<>();
                    Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
                    if (!CollectionUtils.isEmpty(marketOddsList) && marketOddsList.size() > 3) {
                        log.info("::{}::查询独赢配置获取多项盘概率差,赛事ID:{},统一盘口ID:{},玩法ID:{},坑位ID:{}", linkId, standardMatchInfo.getId(), standardMarketDataMessage.getRelationMarketId(), marketCategoryId, standardMarketDataMessage.getPlaceNum());
                        if (standardMarketDataMessage.getPlaceNum() != null) {
                            List<ConfigMarketMarginGap> itemList = configMarketMarginGapService.getItemList(standardMatchInfo.getId(), marketCategoryId, standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum());
                            if (!CollectionUtils.isEmpty(itemList)) {
                                marginGapMap = itemList.stream().collect(Collectors.toMap(ConfigMarketMarginGap::getOddsType, a -> a, (k1, k2) -> k1));
                            }
                        }
                    }
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
                            log.info("::{}::数据商抽水赔率加概率球员类玩法投注类型转换,赛事ID:{},玩法ID:{},oddsType:{}", linkId, standardMatchInfo.getId(), marketCategoryId, oddsType);
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
                                    log.info("::{}::数据商抽水赔率加概率差,赛事ID:{},玩法ID:{},计算前赔率:{},计算后赔率:{},margin配置信息:{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketOddsDataMessage.getPaOddsValue(), finalPaOddsValue, JSON.toJSON(configMarketMarginGaps));
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
            standardMarketMessageList.addAll(standardMarketDataMessages);
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
            processStandardMarketTwoEUROPE(linkId, standardMatchId, standardMarketDataMessage, marketCategoryId, sportId);
        } else {
            log.info("::{}::marginCalculateTransfer投注项数量错误,标准赛事ID:{},玩法:{},standardMarketDataMessage:{}", linkId, standardMatchId, marketCategoryId, JSON.toJSON(standardMarketDataMessage));
        }
    }

    /*** 计算方式：
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
                    log.info("::{}::三项盘独赢计算:{},标准盘口:{},统一盘口id:{},原始赔率为:0，不再计算,封盘口和投注项:{}", linkId, standardMatchInfoId, standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), marketOdds.getOddsType());
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
                log.info("::{}::三项盘独赢计算,标准赛事id:{},标准盘口ID:{},转换统一盘口ID:{},统一盘口id:{},三方盘口源id:{},投注项类型:{},瞄点(0否/1是):{},原始赔率:{},P原始概率:{},抽水原始概率:{},概率差:{},概率差赔率:{},水差:{},最终PA赔率:{},联动0(否)/1(是):{},配置信息:{}", linkId, standardMatchInfoId, standardMarketDataMessage.getId(), relationMarketId, standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId(), oddsType, anchor, changOriginalOdds, p, changOriginalOdds, probability, probabilityOdds, diffValue, paOddsValue, linkageMode, JSONObject.toJSON(configMarketMarginGap));
            }
        } catch (Exception e) {
            //出现异常盘口封盘
            standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
            log.info("::{}::三项独赢盘计算出现异常,盘口ID:{},玩法ID:{},三方盘口ID:{},e:{}", linkId, relationMarketId, marketCategoryId, standardMarketDataMessage.getThirdMarketSourceId(), e);
        }
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
        if (DataSourceCodeEnum.TX.code.equals(standardMarketDataMessage.getDataSourceCode()) && standardMarketDataMessage.getTxPlaceNum() != null) {
            placeNum = standardMarketDataMessage.getTxPlaceNum();
            log.info("::{}::新margin计算,三方盘口ID:{},标准盘口ID:{},TX坑位变更前:{},后:{}", linkId, standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getTxPlaceNum(), placeNum);
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
            ConfigMarketAutoDiffTrade marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId, matchId, relationMarketId, oddsType);
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
            default:
                break;
        }
    }


    /**
     * 获取盘口id
     *
     * @param linkId
     * @param standardMatchInfo
     * @param thirdSportMarketMessages
     */
    public void getMarketId(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessages) {
        try {
            //map<盘口id，盘口标识>
            Map<String, String> marketMap = new HashMap<>();
            List<String> keys = new ArrayList<>();
            thirdSportMarketMessages.stream().forEach(t -> {
                String key = RelationKeyFactory.getMarketRelationKeyByThirdInfo(linkId, standardMatchInfo.getId(), t.getMarketCategoryId(), t.getAddition1(), t.getAddition2(), t.getAddition3(), t.getAddition4(), t.getAddition5(), t.getMarketType(), t.getThirdMarketSourceId());
                keys.add(key);
                marketMap.put(t.getThirdMarketSourceId(), key);
            });
            log.info("::{}::开始拉取getMarketIdredis:{}", linkId, keys);
            List<Object> objectList = redisService.mGet(keys);
            log.info("::{}::开始拉取getMarketIdredis完成", linkId);
            Map<String, String> result = new HashMap<>();
            List<String> requiredCallItems = new ArrayList<>();
            redisHelper.postMarketkeyProcMget(keys, objectList, result, requiredCallItems);
            if (!CollectionUtils.isEmpty(requiredCallItems)) {
                Map<String, Object> mset = new HashMap<>();
                for (String requiredCallItem : requiredCallItems) {
                    if (StringUtils.isEmpty(requiredCallItem)) {
                        continue;
                    }
                    String relationMarketIdstr = MD5Utils.getLongByMD5(requiredCallItem) + "";
                    mset.put(requiredCallItem, relationMarketIdstr);
                    result.put(requiredCallItem, relationMarketIdstr);
                }
                //set
                redisService.mSet(mset);
                log.info("::{}::开始set getMarketIdredis:{}", linkId, keys);
            }
            thirdSportMarketMessages.stream().forEach(thirdSportMarketMessage -> {
                thirdSportMarketMessage.setRelationMarketId(Long.valueOf(result.get(marketMap.get(thirdSportMarketMessage.getThirdMarketSourceId()))));
            });
            //设置投注项id
            log.info("::{}::开始设置投注项id getMarketOddsIdredis:{}", linkId);
            getMarketOddsId(linkId, standardMatchInfo, thirdSportMarketMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMarketOddsId(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessages) {
        List<String> keys = new ArrayList<>();
        //map<投注项id，投注项标识>
        Map<String, String> marketoDDSMap = new HashMap<>();
        for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages) {
            Long relationMarketId = thirdSportMarketMessage.getRelationMarketId();
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                for (ThirdSportMarketOdds t : thirdSportMarketOddsList) {
                    String marketOddsRelationKey = RelationKeyFactory.getMarketOddsRelationKeyByThirdOddsInfo(relationMarketId, t.getOddsType(), t.getThirdOddsFieldSourceId(), t.getAddition1(), thirdSportMarketMessage.getMarketCategoryId());
                    keys.add(marketOddsRelationKey);
                    marketoDDSMap.put(t.getThirdOddsFieldSourceId(), marketOddsRelationKey);
                }
            }
        }
        log.info("::{}::开始设置投注项id getMarketOddsIdredis:{}", linkId, keys);
        List<Object> objectList = redisService.mGet(keys);
        log.info("::{}::开始设置投注项id getMarketOddsIdredis完成:{}", linkId, keys);
        Map<String, String> result = new HashMap<>();
        List<String> requiredCallItems = new ArrayList<>();
        redisHelper.postMarketkeyProcMget(keys, objectList, result, requiredCallItems);
        if (!CollectionUtils.isEmpty(requiredCallItems)) {
            Map<String, Object> mset = new HashMap<>();
            for (String requiredCallItem : requiredCallItems) {
                String relationMarketOddsIdstr = MD5Utils.getLongByMD5(requiredCallItem) + "";
                mset.put(requiredCallItem, relationMarketOddsIdstr);
                result.put(requiredCallItem, relationMarketOddsIdstr);
            }
            //set
            redisService.mSet(mset);
            log.info("::{}::开始set投注项id getMarketOddsIdredis完成:{}", linkId, keys);
        }
        String key = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS + standardMatchInfo.getId();
        Map<String,Integer> oddsMap = redisService.hGetAllBasedBucket(key, ConstantSystem.BUCKET_QUANTITY_EIGHT);
        if (oddsMap == null) {
            oddsMap = new HashMap<String, Integer>();
        }
        String key2 = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS2+  standardMatchInfo.getId();
        Map<String, Integer> finalOddsMap2 = new HashMap<String, Integer>();
        log.info("::{}::处理RONGHE_AO_MARKET_ORIGINAL_ODDS", linkId);
        //赋值
        for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages) {
            String dataSourceCode = thirdSportMarketMessage.getDataSourceCode();
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                Map<String, Integer> finalOddsMap = oddsMap;
                thirdSportMarketOddsList.stream().forEach(sportMarketOdds -> {
                    sportMarketOdds.setMarketId(thirdSportMarketMessage.getRelationMarketId());
                    sportMarketOdds.setId(Long.valueOf(result.get(marketoDDSMap.get(sportMarketOdds.getThirdOddsFieldSourceId()))));
                    //缓存 AO原始赔率
                    if (DataSourceCodeEnum.AO.code.equals(dataSourceCode)) {
                        finalOddsMap2.put(sportMarketOdds.getId().toString(), sportMarketOdds.getOriginalOddsValue());
                    }
                    //缓存 AO原始赔率
                    if (DataSourceCodeEnum.AO.code.equals(dataSourceCode) && MarginCategoryConfig.FootBall_MAIN_CATEGORY.contains(thirdSportMarketMessage.getMarketCategoryId())) {
                        finalOddsMap.put(sportMarketOdds.getId().toString(), sportMarketOdds.getOriginalOddsValue());
                    }
                });
            }
        }
        log.info("::{}::处理投注项id完成", linkId);

        redisService.hSetAll(key2,finalOddsMap2,marketCacheTime(standardMatchInfo.getBeginTime()));
        redisService.hSetAllBasedBucket(key, ConstantSystem.BUCKET_QUANTITY_EIGHT, oddsMap, marketCacheTime(standardMatchInfo.getBeginTime()));
        log.info("::{}::处理RONGHE_AO_MARKET_ORIGINAL_ODDS完成", linkId);

    }

    /**
     * 赛事不存在缓存赔率
     *
     * @param thirdMatchInfoIdsIsNull
     * @param oddsWrappers
     */
    public void cacheThirdMarket(List<String> thirdMatchInfoIdsIsNull, List<OddsWrapper<ThirdMatchMarketDTO>> oddsWrappers) {
        Map<String, OddsWrapper<ThirdMatchMarketDTO>> thirdMatchMarketMap = oddsWrappers.stream().filter(o -> thirdMatchInfoIdsIsNull.contains(o.getThirdMatchSourceId())).collect(Collectors.toMap(t -> t.getLinkId(), thi -> thi));
        for (Map.Entry<String, OddsWrapper<ThirdMatchMarketDTO>> thirdMatchMarketDTOEntry : thirdMatchMarketMap.entrySet()) {
            String linkId = thirdMatchMarketDTOEntry.getKey();
            ThirdMatchMarketDTO thirdMatchMarketDTO = thirdMatchMarketDTOEntry.getValue().getData();
            String dataSourceCode = thirdMatchMarketDTO.getDataSourceCode();
            String dataSourceCodeDB = dataSourceCode.split("-")[0].toUpperCase();
            if (DataSourceCodeEnum.LS.code.equals(dataSourceCodeDB)) {
                cacheLsThirdMarket(linkId, thirdMatchMarketDTO, dataSourceCode);
                return;
            }
            String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
            //缓存KEY
            String thirdMarketKey = Constant.REDIS_KEY.RONGHE_THIRD_STANDARD_MARKET + thirdMatchSourceId;
            //赛事id-数据源-三方盘口id-offerLineId-Dto
            Map<String, Map<Integer, ThirdMarketDTO>> mapMap = null;
            Object obj = redisService.hGet(thirdMarketKey, dataSourceCode);
            if (!Objects.isNull(obj)) {
                mapMap = (Map<String, Map<Integer, ThirdMarketDTO>>) obj;
            }

            for (ThirdMarketDTO thirdMarketDTO : thirdMatchMarketDTO.getMarketList()) {
                //三方玩法源ID
                String thirdMarketCategorySourceId = thirdMarketDTO.getThirdMarketCategorySourceId();
                //TX根据三方玩法 缓存坑位最新数据  Map<三方玩法,Map<坑位,盘口数据>>
                //TX坑位
                Map<Integer, ThirdMarketDTO> categoryPlaceMap = new HashMap<>();
                Integer offerLineId = thirdMarketDTO.getOfferLineId();
                thirdMarketDTO.setDataSourceCode(dataSourceCode);
                categoryPlaceMap.put(offerLineId, thirdMarketDTO);
                if (!Objects.isNull(mapMap)) {
                    if (mapMap.get(thirdMarketCategorySourceId) != null) {
                        mapMap.get(thirdMarketCategorySourceId).put(offerLineId, thirdMarketDTO);
                    } else {
                        mapMap.put(thirdMarketCategorySourceId, categoryPlaceMap);
                    }
                } else {
                    mapMap = new HashMap<>();
                    mapMap.put(thirdMarketCategorySourceId, categoryPlaceMap);
                }
            }
            if (!Objects.isNull(mapMap)) {
                redisService.hSet(thirdMarketKey, dataSourceCode, mapMap, RedisConfig.REDIS_WEEK_TIME);
                log.error("::{}::百家赔：三方赛事不存在,缓存盘口,三方赛事数据源ID:{},dataSourceCode:{},map:{}", linkId, thirdMatchSourceId, dataSourceCode, mapMap);
            }

        }


    }

    /**
     * 缓存LS盘口
     *
     * @param linkId
     * @param thirdMatchMarketDTO
     * @param dataSourceCode
     */
    public void cacheLsThirdMarket(String linkId, ThirdMatchMarketDTO thirdMatchMarketDTO, String dataSourceCode) {
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        //缓存KEY
        String thirdMarketKey = Constant.REDIS_KEY.RONGHE_LS_THIRD_STANDARD_MARKET + thirdMatchSourceId;
        //三方盘口id，Dto
        Map<String, ThirdMarketDTO> mapMap = new HashMap<>();
        Object obj = redisService.hGet(thirdMarketKey, dataSourceCode);
        if (!Objects.isNull(obj)) {
            mapMap = (Map<String, ThirdMarketDTO>) obj;
        }
        for (ThirdMarketDTO thirdMarketDTO : thirdMatchMarketDTO.getMarketList()) {
            thirdMarketDTO.setDataSourceCode(dataSourceCode);
            mapMap.put(thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO);
        }
        redisService.hSet(thirdMarketKey, dataSourceCode, mapMap, RedisConfig.REDIS_WEEK_TIME);
    }


    /**
     * TX百家赔球头
     *
     * @param
     * @param standardMatchInfo
     * @param thirdMarkets
     */
    private void allTxThirdFistMarket(String dataSourceCode, StandardMatchInfo standardMatchInfo, List<ThirdMarketDTO> thirdMarkets) {
        String dataSourceCodeDB = dataSourceCode.split("-")[0].toUpperCase();
        if (DataSourceCodeEnum.TX.getCode().equals(dataSourceCodeDB)) {
            String fistMatchKey = Constant.REDIS_KEY.THIRD_FIST_MATCH;
            //百家赔三方初盘
            String fistKey = Constant.REDIS_KEY.THIRD_FIST_MARKET_HEAD + standardMatchInfo.getId();
            //需要处理的玩法分组
            Map<Long, List<ThirdMarketDTO>> marketListsMap = thirdMarkets.stream().filter(t -> MarginCategoryConfig.THIRD_FIRST_MARKET_BALL_HEAD_CATEGORY.contains(t.getMarketCategoryId()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(t.getStatus()) && t.getOfferLineId() == 1L).collect(Collectors.groupingBy(ThirdMarketDTO::getMarketCategoryId));
            for (Map.Entry<Long, List<ThirdMarketDTO>> entry : marketListsMap.entrySet()) {
                ThirdMarketDTO thirdMarketDto = entry.getValue().get(0);
                String key = "THIRD_All_" + dataSourceCode + "_" + thirdMarketDto.getMarketCategoryId() + "_" + thirdMarketDto.getMarketType();
                Object obj = redisService.hGet(fistKey, key);
                if (Objects.isNull(obj)) {
                    StandardMarketDataMessage standardMarketDataMessage = thirdConvertStandardMarket(thirdMarketDto);
                    if (null != standardMarketDataMessage) {
                        standardMarketDataMessage.setMarketCategoryId(thirdMarketDto.getMarketCategoryId());
                        standardMarketDataMessage.setDataSourceCode(dataSourceCode);
                        redisService.hSet(fistKey, key, standardMarketDataMessage);
                    }
                    redisService.hSet(fistMatchKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime());
                }
            }
        }
    }

    /**
     * LS百家赔球头
     *
     * @param
     * @param standardMatchInfo
     * @param marketLists
     */
    private void allLsThirdFistMarket(String dataSourceCode, StandardMatchInfo standardMatchInfo, List<ThirdMarketDTO> marketLists) {
        String dataSourceCodeDB = dataSourceCode.split("-")[0].toUpperCase();
        if (DataSourceCodeEnum.LS.getCode().equals(dataSourceCodeDB)) {
            String fistMatchKey = Constant.REDIS_KEY.THIRD_FIST_MATCH;
            //百家赔三方初盘
            String fistKey = Constant.REDIS_KEY.THIRD_FIST_MARKET_HEAD + standardMatchInfo.getId();
            //需要处理的玩法分组
            Map<Long, List<ThirdMarketDTO>> marketListsMap = marketLists.stream().filter(t -> MarginCategoryConfig.THIRD_FIRST_MARKET_BALL_HEAD_CATEGORY.contains(t.getMarketCategoryId()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(t.getStatus())).collect(Collectors.groupingBy(ThirdMarketDTO::getMarketCategoryId));
            for (Map.Entry<Long, List<ThirdMarketDTO>> entry : marketListsMap.entrySet()) {
                List<ThirdMarketDTO> thirdMarketDtos = entry.getValue();
                //计算出投注项赔率差
                thirdMarketDtos.forEach(m -> {
                    m.setOddsMetric(m.getMarketOddsList().stream().map(ThirdMarketOddsDTO::getOriginalOddsValue).reduce(0, (a, b) -> a >= b ? a - b : b - a));
                });
                //数据商状态、赔率差 升序排序
                ListUtils.sort(thirdMarketDtos, true, "status", "oddsMetric");
                ThirdMarketDTO thirdMarketNewHeadFinal = thirdMarketDtos.get(0);
                String key = "THIRD_All_" + dataSourceCode + "_" + thirdMarketNewHeadFinal.getMarketCategoryId() + "_" + thirdMarketNewHeadFinal.getMarketType();
                Object obj = redisService.hGet(fistKey, key);
                if (Objects.isNull(obj)) {
                    StandardMarketDataMessage standardMarketDataMessage = thirdConvertStandardMarket(thirdMarketNewHeadFinal);
                    if (null != standardMarketDataMessage) {
                        standardMarketDataMessage.setDataSourceCode(dataSourceCode);
                        standardMarketDataMessage.setMarketCategoryId(thirdMarketNewHeadFinal.getMarketCategoryId());
                        redisService.hSet(fistKey, key, standardMarketDataMessage);
                    }
                    redisService.hSet(fistMatchKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime());
                }

            }
        }
    }

    /**
     * 赋值盘口
     *
     * @param thirdMarketDTO
     */
    private ThirdSportMarketMessage copyThirdMarketDTO(ThirdMarketDTO thirdMarketDTO) {
        ThirdSportMarketMessage thirdSportMarketMessage = new ThirdSportMarketMessage();
        thirdSportMarketMessage.setAddition1(thirdMarketDTO.getAddition1()!=null&&thirdMarketDTO.getAddition1().equals("0")?"0":thirdMarketDTO.getAddition1());
        thirdSportMarketMessage.setAddition2(thirdMarketDTO.getAddition2()!=null&&thirdMarketDTO.getAddition2().equals("0")?"0":thirdMarketDTO.getAddition2());
        thirdSportMarketMessage.setAddition3(thirdMarketDTO.getAddition3());
        thirdSportMarketMessage.setAddition4(thirdMarketDTO.getAddition4());
        thirdSportMarketMessage.setAddition5(thirdMarketDTO.getAddition5());
        thirdSportMarketMessage.setMarketType(thirdMarketDTO.getMarketType());
        thirdSportMarketMessage.setThirdMarketSourceId(thirdMarketDTO.getThirdMarketSourceId());
        thirdSportMarketMessage.setPlaceNum(thirdMarketDTO.getOfferLineId());
        thirdSportMarketMessage.setOfferLineId(thirdMarketDTO.getOfferLineId());
        thirdSportMarketMessage.setModifyTime(thirdMarketDTO.getModifyTime());
        thirdSportMarketMessage.setOddsName(thirdMarketDTO.getOddsName());
        MergeFunctionUtils.setNumberOfWinners( thirdSportMarketMessage, thirdMarketDTO.getNumberOfWinners());
        return thirdSportMarketMessage;
    }

    /**
     * 赋值盘口赔率
     *
     * @param thirdMarketOddsDTO
     */
    private ThirdSportMarketOdds copyThirdMarketOddsDTO(ThirdMarketOddsDTO thirdMarketOddsDTO) {
        ThirdSportMarketOdds thirdSportMarketOdds = new ThirdSportMarketOdds();
        thirdSportMarketOdds.setOddsType(thirdMarketOddsDTO.getOddsType());
        thirdSportMarketOdds.setThirdOddsFieldSourceId(thirdMarketOddsDTO.getThirdOddsFieldSourceId());
        thirdSportMarketOdds.setAddition1(thirdMarketOddsDTO.getAddition1());
        thirdSportMarketOdds.setOddsValue(thirdMarketOddsDTO.getOddsValue());
        thirdSportMarketOdds.setOriginalOddsValue(thirdMarketOddsDTO.getOriginalOddsValue());
        thirdSportMarketOdds.setActive(thirdMarketOddsDTO.getActive());
        thirdSportMarketOdds.setModifyTime(thirdMarketOddsDTO.getModifyTime());
        thirdSportMarketOdds.setOrderOdds(thirdMarketOddsDTO.getOrderOdds());
        return thirdSportMarketOdds;
    }
}
