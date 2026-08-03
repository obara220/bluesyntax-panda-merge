package com.panda.merge.component;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.service.ConfigTradeTypeService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketNewService;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 盘口排序
 */
@Slf4j
@Component
public class MarketOddsPlaceProcessor extends BaseProcessor {

    @Autowired
    private ConfigTradeTypeService configTradeTypeService;
    @Autowired
    private FootballMarketsSoreProcessor footballMarketsSoreProcessor;
    @Autowired
    private CommonAsyncService commonAsyncService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    public void setOddsOrderByOddsValue(String linkId, Map<String, StandardMarketDataMessage> standardMarketMessageMap, StandardMatchInfo standardMatchInfo, Set<Long> marketCategoryIdSet, Set<Long> oddsTypeIdSet, Set<Long> categorySet, Set<Long> riskCategorySet, Boolean isTrue) {
        int liveFlag = isOddsLive(standardMatchInfo.getId());
        //设置子玩法id
        standardMarketMessageMap.forEach((k, v) -> {
            v.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(linkId, v.getMarketCategoryId(), v.getAddition1(), v.getAddition2(), v.getAddition3(), v.getAddition4(), v.getAddition5(), String.valueOf(v.getStandardMatchInfoId())));
            //盘口时间戳判断关盘
            marketTimeClose(linkId, liveFlag, standardMatchInfo, v);
        });
        //取本次有改变的玩法,排序
        Map<Long, List<StandardMarketDataMessage>> standardMarketMapMTS = standardMarketMessageMap.values().stream().filter(e -> marketCategoryIdSet.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        if (CollectionUtils.isEmpty(standardMarketMapMTS)) {
            return;
        }
        Boolean sportVerify = (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Badminton.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Soccer.code.equals(standardMatchInfo.getSportId()));
        Boolean sportOrder = (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Tennis.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.TableTennis.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Vollyball.code.equals(standardMatchInfo.getSportId()));
        Map<Long, Integer> tradeTypeMap = configTradeTypeService.getItemByMatchAndCategorys(standardMatchInfo.getId().toString(), marketCategoryIdSet);
        //当所有关盘时，需要用缓存替换的A+盘口数据
        Set<Long> AMarketCategoryIds = tradeTypeMap.entrySet()
                .stream()
                .filter(entry -> Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(entry.getValue())
                && standardMarketMapMTS.containsKey(entry.getKey())
                && !standardMarketMapMTS.get(entry.getKey()).stream().anyMatch(message -> message.getThirdMarketSourceStatus() < 2))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        log.info("::{}::数据源赔率开始排序以及计算挡板:操盘方式:{}", linkId, tradeTypeMap);
        //循环遍历盘口信息,设置低赔和赔率差
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapMTS.entrySet()) {
            log.info("linkId:{},categoryId:{}, check sort and flip market size :{}", linkId, entry.getKey(), entry.getValue().size());
            //上一次球头
            String lastMarket = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET_ODDS_LAST + standardMatchInfo.getId();
            StandardMarketMessage lastMarketMessage = (StandardMarketMessage) redisService.hGet(lastMarket, String.valueOf(entry.getKey()));
            log.info("::{}::盘口上次球头值:{},赛事ID:{}", linkId, lastMarketMessage, standardMatchInfo.getId());
            //获取key对应的盘口对象集合
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            standardMarketStatusCheck(linkId, standardMatchInfo, standardMarketDataMessages);
            //只有数据商相关操盘才需要走挡板校验
            boolean isNeedFlap = Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO_PLUS.equals(tradeTypeMap.get(standardMarketDataMessages.get(0).getMarketCategoryId())) || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeMap.get(standardMarketDataMessages.get(0).getMarketCategoryId()));
            //判断盘口来源是否有数据商盘口
            boolean isMarketSource = standardMarketDataMessages.stream().filter(e -> e.getMarketType() == 0).anyMatch(s -> s.getMarketSource() == 0);
            if (isMarketSource) {
                //关盘构建滚球盘口
                standardMarketDataMessages.forEach(s -> {
                    if (s.getMarketSource() == 1) {
                        log.info("::{}::盘口来源存在数据商盘口,赛事ID:{},盘口ID:{},三方盘口ID:{},,玩法ID:{},关闭构建盘口。", linkId, standardMatchInfo.getId(), s.getRelationMarketId(), s.getThirdMarketSourceId(), s.getMarketCategoryId());
                        s.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        s.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        s.setMarketSource(0);
                    }
                });
                //根据玩法删除构建盘口缓存
                delConvertMarket(linkId, standardMatchInfo.getId(), entry.getKey());
            }
            aoAndtxMarketPlaceMerge(linkId, standardMatchInfo, standardMarketDataMessages, standardMarketMessageMap);
            //比分兜底，足球
            footballMarketsSoreProcessor.check(linkId, standardMatchInfo, standardMarketDataMessages);
            //取盘口中有投注项的有效数据
            List<StandardMarketDataMessage> standardMarketsValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList()) && e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
            //排序字段placeNum
            int placeNum = 1;
            //------------处理有效盘口的排序-----------
            if (!CollectionUtils.isEmpty(standardMarketsValid)) {
                //第一步：计算赔率差和低赔
                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
                    //获取盘口投注项
                    List<StandardMarketOddsDataMessage> marketOddsList = standardMarketDataMessage.getMarketOddsList();
                    Integer minOddsValue = 0;
                    Integer maxOddsValue = 0;
                    //循环遍历盘口投注项
                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : marketOddsList) {
                        //设置pa赔率：数据源抽水赔率
                        if (null == standardMarketOddsDataMessage.getOddsValue()) {
                            standardMarketOddsDataMessage.setOddsValue(0);
                        }
                        if (null == standardMarketOddsDataMessage.getOriginalOddsValue()) {
                            standardMarketOddsDataMessage.setOriginalOddsValue(0);
                        }
                        if (standardMarketOddsDataMessage.getOriginalOddsValue() > maxOddsValue) {
                            maxOddsValue = standardMarketOddsDataMessage.getOriginalOddsValue();
                        }
                        if (standardMarketOddsDataMessage.getOriginalOddsValue() < minOddsValue || minOddsValue == 0) {
                            minOddsValue = standardMarketOddsDataMessage.getOriginalOddsValue();
                        }
                    }
                    //计算赔率差
                    Integer oddsMetric = maxOddsValue - minOddsValue;
                    standardMarketDataMessage.setOddsMetric(oddsMetric);
                }
                //第二步：排序，依据三方源盘口状态、赔率差、低赔
                ListUtils.sort(standardMarketsValid, true, "status", "oddsMetric", "oddsValue");
                //排序，篮球特殊处理
                if (sportOrder && MarginCategoryConfig.CHANGE_FLAP1.contains(standardMarketDataMessages.get(0).getMarketCategoryId()) && standardMarketsValid.size() > 1 && isNeedFlap) {
                    basketSetOrderByCategory(standardMarketsValid);
                }
                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
                    //初始化赔率分控挡板状态，避免之前缓存中的盘口数据影响
                    standardMarketDataMessage.setCategorySuspended(0);
                    if (placeNum == 1 && sportVerify && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getThirdMarketSourceStatus()) && isNeedFlap) {
                        log.info("::{}::处理赔率挡板逻辑,:{}",linkId,oddsTypeIdSet);
                        if (null != oddsTypeIdSet) {
                            //处理赔率挡板逻辑
                            if (MarginCategoryConfig.CHANGE_FLAP.contains(standardMarketDataMessage.getMarketCategoryId())) {
                                basketDataFlap(linkId, standardMarketDataMessage, lastMarketMessage, oddsTypeIdSet, riskCategorySet);
                            }
                        }
                        if (null != lastMarketMessage && !CollectionUtils.isEmpty(categorySet) && categorySet.contains(standardMarketDataMessage.getMarketCategoryId())) {
                            //处理球头挡板逻辑
                            String lastMarketAddition1 = StringUtils.isEmpty(lastMarketMessage.getAddition5()) ? lastMarketMessage.getAddition1() : lastMarketMessage.getAddition5();
                            if (Math.abs(Double.parseDouble(lastMarketAddition1) - Double.parseDouble(standardMarketDataMessage.getAddition1())) >= MarginCategoryConfig.BASKETBALL_FLAP_ADDTION1_DOUBLE) {
                                log.info("::{}::数据源挡板计算后需要下发报警消息，flag:{},oldAddtion1:{},newAddtion1:{}", linkId, true, lastMarketAddition1, standardMarketDataMessage.getAddition1());
                                riskCategorySet.add(standardMarketDataMessage.getMarketCategoryId());
                                standardMarketDataMessage.setCategorySuspended(1);
                                // 发送操盘日志给风控
                                StandardMarketMessage logData = new StandardMarketMessage();
                                logData.setMarketCategoryId(standardMarketDataMessage.getMarketCategoryId());
                                logData.setMarketType(standardMarketDataMessage.getMarketType());
                                logData.setPaStatus(standardMarketDataMessage.getStatus());
                                logData.setId(standardMarketDataMessage.getId());
                                commonAsyncService.sendDeactivatedBySystemLogToRisk(standardMatchInfo, logData);
                            }
                        }
                    }
                    /*if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.AO.code) && MarginCategoryConfig.CHANGE_AO_FLAP_BAK1.contains(standardMarketDataMessages.get(0).getMarketCategoryId())){
                        standardMarketDataMessage.setPlaceNum(placeNum);
                    }*/
                    if (!MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarketDataMessage.getDataSourceCode()) || null == standardMarketDataMessage.getPlaceNum()) {
                        standardMarketDataMessage.setPlaceNum(placeNum);
                    }
                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarketDataMessage.getMarketOddsList()) {
                        //设置pa赔率：数据源抽水赔率
                        standardMarketOddsDataMessage.setPaOddsValue(standardMarketOddsDataMessage.getOriginalOddsValue());
                    }
                    log.info("::{}::盘口排序后,标准赛事id:{},标准盘口id:{},统一盘口id:{},玩法:{},子玩法:{},盘口位置:{},三方盘口源id:{},三方盘口源状态:{},盘口状态:{},赔率差值:{},低赔:{},球头:{},盘口时间:{}", linkId, standardMatchInfo.getId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum(), standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getThirdMarketSourceStatus(), standardMarketDataMessage.getStatus(), standardMarketDataMessage.getOddsMetric(), standardMarketDataMessage.getPaOddsValue(), standardMarketDataMessage.getMarketOddsValue(), standardMarketDataMessage.getModifyTime());
                    placeNum = placeNum + 1;
                }
            }

            //------------处理无效盘口的排序（关盘）-----------
            List<StandardMarketDataMessage> standardMarketsInvalids = standardMarketDataMessages.stream()
                    .filter(e -> CollectionUtils.isEmpty(e.getMarketOddsList()) || e.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(standardMarketsInvalids)) {
                standardMarketsInvalids.forEach(m -> {
                    Long format = format(m.getModifyTime());
                    m.setModifyTimeFormat(format);
                    if (!CollectionUtils.isEmpty(m.getMarketOddsList())) {
                        m.setOddsMetric(m.getMarketOddsList().stream().map(StandardMarketOddsDataMessage::getOriginalOddsValue). reduce(0, (a, b) -> a >= b ? a - b : b - a));
                    } else {
                        m.setOddsMetric(999999);
                    }
                });
                Comparator<StandardMarketDataMessage> comparator = Comparator.comparing(StandardMarketDataMessage::getModifyTimeFormat, Comparator.reverseOrder())
                        .thenComparing(StandardMarketDataMessage::getThirdMarketSourceStatus, Comparator.reverseOrder())
                        .thenComparingLong(StandardMarketDataMessage::getOddsMetric);
                standardMarketsInvalids = standardMarketsInvalids.stream().sorted(comparator).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(standardMarketsValid)) {
                    // 存在非关盘（开盘/封盘）：保持原逻辑，关盘盘接在有效盘后继续编号
                    invalidMarketSort(placeNum, standardMarketsInvalids, AMarketCategoryIds);
                } else {
                    // 全部关盘：PA 置 0 后，按上次下发顺序排序；未出现在上次下发中的盘口 placeNum=999
                    invalidMarketSortPaOnly(standardMarketsInvalids);
                    closedMarketPlaceSortHelper.sortClosedStandardMarketDataMessages(
                            linkId, standardMatchInfo.getId(), entry.getKey(), standardMarketDataMessages);
                    for (StandardMarketDataMessage s : entry.getValue()) {
                        if (s.getPlaceNum() == null) {
                            s.setPlaceNum(ClosedMarketPlaceSortHelper.UNKNOWN_PLACE_NUM);
                        }
                    }
                }
            }

            //A+盘口排序特殊处理
            /*List<StandardMarketDataMessage> standardMarketsInvalidsA = standardMarketDataMessages.stream()
                    .filter(e -> AMarketCategoryIds.contains(e.getMarketCategoryId()) && (CollectionUtils.isEmpty(e.getMarketOddsList()) || e.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.SUSPENDED)).collect(Collectors.toList());
            invalidMarketSort(placeNum, standardMarketsInvalidsA, AMarketCategoryIds);*/
        }
    }
    @Autowired
    public RedisService redisService;

    @Autowired
    private ClosedMarketPlaceSortHelper closedMarketPlaceSortHelper;

    /**
     * 全部关盘时仅处理关盘 PA，不分配坑位（坑位由 {@link ClosedMarketPlaceSortHelper} 统一分配）
     */
    private void invalidMarketSortPaOnly(List<StandardMarketDataMessage> standardMarketsInvalid) {
        if (CollectionUtils.isEmpty(standardMarketsInvalid)) {
            return;
        }
        for (StandardMarketDataMessage standardMarket : standardMarketsInvalid) {
            if (Constant.SPORT_MARKET.STATUS.SETTLED.equals(standardMarket.getThirdMarketSourceStatus())
                    || Constant.SPORT_MARKET.STATUS.CANCELLED.equals(standardMarket.getThirdMarketSourceStatus())) {
                standardMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            }
            if (!CollectionUtils.isEmpty(standardMarket.getMarketOddsList())) {
                for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarket.getMarketOddsList()) {
                    standardMarketOddsDataMessage.setPaOddsValue(0);
                }
            }
        }
    }

    private  void invalidMarketSort(int placeNum, List<StandardMarketDataMessage> standardMarketsInvalid,Set<Long> AMarketCategoryIds) {
        Long marektId = null;
        if (!CollectionUtils.isEmpty(standardMarketsInvalid)) {
            /*if (!CollectionUtils.isEmpty(AMarketCategoryIds) && AMarketCategoryIds.contains(standardMarketsInvalid.get(0).getMarketCategoryId())){
                String redisOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_A_MARKETODDS + standardMarketsInvalid.get(0).getStandardMatchInfoId());
                Object obj =  redisService.hGet(redisOddsKey,String.valueOf(standardMarketsInvalid.get(0).getMarketCategoryId()));
                if (obj!=null){
                    List<StandardMarketMessage> standardMarketMessageList = (List<StandardMarketMessage>) obj;
                    List<StandardMarketMessage> standardMarketMessageListTemp = standardMarketMessageList.stream().filter(e->e.getPlaceNum() == 1).collect(Collectors.toList());
                    if (!standardMarketMessageListTemp.isEmpty()&&standardMarketMessageList.size()>=1)
                        marektId = standardMarketMessageListTemp.get(0).getRelationMarketId();
                }
            }*/
            if (marektId == null){
                for (StandardMarketDataMessage standardMarket : standardMarketsInvalid) {
                    if (Constant.SPORT_MARKET.STATUS.SETTLED.equals(standardMarket.getThirdMarketSourceStatus())
                            || Constant.SPORT_MARKET.STATUS.CANCELLED.equals(standardMarket.getThirdMarketSourceStatus())) {
                        standardMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    }
                    if (!MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarket.getDataSourceCode())) {
                        standardMarket.setPlaceNum(placeNum++);
                    }
                    if (!CollectionUtils.isEmpty(standardMarket.getMarketOddsList())) {
                        for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarket.getMarketOddsList()) {
                            //设置pa赔率：数据源抽水赔率
                            standardMarketOddsDataMessage.setPaOddsValue(0);
                        }
                    }
                }
            }else{
                for (StandardMarketDataMessage standardMarket : standardMarketsInvalid) {
                    if (standardMarket.getRelationMarketId() == marektId || standardMarket.getRelationMarketId().equals(marektId)){
                        standardMarket.setPlaceNum(1);
                        standardMarket.setRemark(standardMarket.getRemark()+",A+排序");
                    }else{
                        if (Constant.SPORT_MARKET.STATUS.SETTLED.equals(standardMarket.getThirdMarketSourceStatus())
                                || Constant.SPORT_MARKET.STATUS.CANCELLED.equals(standardMarket.getThirdMarketSourceStatus())) {
                            standardMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        }
                        if (!MarginCategoryConfig.SPORT_TX_LOGIC.contains(standardMarket.getDataSourceCode())) {
                            standardMarket.setPlaceNum(2);
                        }
                    }
                    if (!CollectionUtils.isEmpty(standardMarket.getMarketOddsList())) {
                        for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarket.getMarketOddsList()) {
                            //设置pa赔率：数据源抽水赔率
                            standardMarketOddsDataMessage.setPaOddsValue(0);
                        }
                    }
                }
            }

        }
    }

    /**
     * 盘口时间校验，超过阈值关盘 ，主流程不参与排序
     *
     * @param linkId
     * @param standardMatchInfo
     * @param marketDataMessage
     */
    public void marketTimeClose(String linkId, Integer liveFlag, StandardMatchInfo standardMatchInfo, StandardMarketDataMessage marketDataMessage) {
        if (0 == marketDataMessage.getMarketSource() && StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) && Constant.WARNING_DATA_SOURCE_CODE.contains(marketDataMessage.getDataSourceCode()) && liveFlag == 0 && !Constant.FOOT_BALL_PERIOD_FILTER_WARNING.contains(standardMatchInfo.getMatchPeriodId()) && MarginCategoryConfig.TWO_NO_UPDATE.contains(marketDataMessage.getMarketCategoryId()) && marketDataMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            if (DataSourceCodeEnum.BG.code.equals(marketDataMessage.getDataSourceCode()) && MarginCategoryConfig.FLAT_HANDICAP_DISPOSE.contains(marketDataMessage.getAddition2())) {
                log.info("::{}::时间戳兜底,盘口id:{},时间戳兜底平盘不处理关盘", linkId, marketDataMessage.getRelationMarketId());
                return;
            }
            Long warningTime = 180000L;
            log.info("::{}::时间戳兜底,盘口id:{},当前时间：{}，盘口时间：{},告警时间：{}，最终：{}", linkId, marketDataMessage.getRelationMarketId(), System.currentTimeMillis(), marketDataMessage.getModifyTime(), warningTime, System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime);
            if (System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime) {
                log.info("::{}::时间戳兜底,盘口id:{},时间戳兜底关盘：{}", linkId, marketDataMessage.getRelationMarketId(), System.currentTimeMillis() - marketDataMessage.getModifyTime() >= warningTime);
                marketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketDataMessage.setRemark("时间戳兜底关盘");
                String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + marketDataMessage.getDataSourceCode() + "_" + marketDataMessage.getMarketCategoryId());
                redisService.hSet(marketKey, marketDataMessage.getRelationMarketId().toString(), marketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
        }
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
                    log.info("::{}::盘口来源删除构建盘口缓存,赛事ID:{},盘口ID:{},k:{},三方盘口ID:{},玩法ID:{}", linkId, standardMatchInId, v.getRelationMarketId(), k, v.getThirdMarketSourceId(), v.getMarketCategoryId());
                }
            });
        } else {
            log.info("::{}::盘口来源删除构建盘口缓存不存在,赛事ID:{},玩法ID:{}", linkId, standardMatchInId, clearMarketCategoryId);
        }
    }


    /**
     * AO坑位盘口处理
     *
     * @param linkId
     * @param standardMarketDataMessagesAUTO
     */
    public void aoAndtxMarketPlaceMerge(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessagesAUTO, Map<String, StandardMarketDataMessage> standardMarketMessageMap) {
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGrop = standardMarketDataMessagesAUTO.stream().filter(e -> e.getDataSourceCode().equals(DataSourceCodeEnum.AO.code) || e.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        if (CollectionUtils.isEmpty(marketCategoryGrop)) {
            log.info("::{}::非AO/TX盘口数据融合处理条数为：{}", linkId, standardMarketDataMessagesAUTO.size());
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
                    standardMarketMessageMap.remove(placeMarket.getRelationMarketId().toString());
                    if (num == 0) {
                        standardMarketDataMessagesAUTO.add(placeMarket);
                        standardMarketMessageMap.put(placeMarket.getRelationMarketId().toString(), placeMarket);
                        log.info("::{}::aoAndtxMarketPlaceMerge,三方盘口ID:{},AO/TX盘口相同坑位最新盘口", linkId, placeMarket.getThirdMarketSourceId());
                    }
                    num++;
                }
            }
        }
    }

    /**
     * 缓存赔率
     *
     * @param standardMatchInfo
     * @param standardMarketMessage
     */
    public void setLastMarketMessage(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage) {
        Boolean sportVerify = (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Badminton.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Soccer.code.equals(standardMatchInfo.getSportId()));
        Boolean sportOrder = (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Tennis.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.TableTennis.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Vollyball.code.equals(standardMatchInfo.getSportId()));
        //数据商关盘不覆盖上一次球头
        if (Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(standardMarketMessage.getThirdMarketSourceStatus())){
            return;
        }
        //设置坑位1 赔率
        //挡板球头
        //大于三项上一次的球头赔率
        if (1 == standardMarketMessage.getPlaceNum() &&
                ((sportVerify && MarginCategoryConfig.CHANGE_FLAP.contains(standardMarketMessage.getMarketCategoryId()))
                        || ((sportOrder && MarginCategoryConfig.CHANGE_FLAP1.contains(standardMarketMessage.getMarketCategoryId()))
                        || (StandardSportTypeEnum.FootBall.getCode().equals(standardMatchInfo.getSportId()) && MarginCategoryConfig.THREE_CATEGORY.contains(standardMarketMessage.getMarketCategoryId()))))) {
            String last = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET_ODDS_LAST + standardMatchInfo.getId();
            redisService.hSet(last, String.valueOf(standardMarketMessage.getMarketCategoryId()), standardMarketMessage, RedisConfig.REDIS_HOUR_TIME);
        }
    }


    /**
     * 篮球排序-结合足球的规则得出主盘后再次细分
     * 总分类玩法：第1副盘为球头值大一个阶梯 第2副盘为球头值小一个阶梯，
     * 如盘口数递增则以此类推奇数为大一阶梯，偶数为小一阶梯，
     * 阶梯为球头差每个赛事有可能会不一样
     * 让分类玩法 主队为让球方：第1副盘为球头值大一个阶梯 第2副盘为球头值小一个阶梯 ，
     * 如盘口数递增则以此类，推奇数为大一阶梯，偶数为小一阶梯
     * 让分类玩法 客队为让球方：第1副盘为球头值小一个阶梯 第2副盘为球头值大一个阶梯，
     * 如盘口数递增则以此类推，奇数为小一阶梯，偶数为大一阶梯
     *
     * @param standardMarketsValid
     */
    private void basketSetOrderByCategory(List<StandardMarketDataMessage> standardMarketsValid) {
        List<StandardMarketDataMessage> bigZeroList = new ArrayList<>();
        List<StandardMarketDataMessage> smallZeroList = new ArrayList<>();
        StandardMarketDataMessage standardMarketDataMessageOne = standardMarketsValid.get(0);
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
            Double temp = Double.parseDouble(standardMarketDataMessageOne.getAddition1()) - Double.parseDouble(standardMarketDataMessage.getAddition1());
            if (temp > 0) {
                bigZeroList.add(standardMarketDataMessage);
            } else if (temp < 0) {
                smallZeroList.add(standardMarketDataMessage);
            }
            standardMarketDataMessage.setMarketOddsValue(Math.abs(temp));
        }
        if (bigZeroList.size() > 1) {
            ListUtils.sort(bigZeroList, true, "status", "marketOddsValue");
        }
        if (smallZeroList.size() > 1) {
            ListUtils.sort(smallZeroList, true, "status", "marketOddsValue");
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
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @param oddsTypeIdSet
     * @param riskCategorySet
     */
    private void basketDataFlap(String linkId, StandardMarketDataMessage standardMarketDataMessage, StandardMarketMessage lastMarketMessage, Set<Long> oddsTypeIdSet, Set<Long> riskCategorySet) {
        Map<String, StandardMarketOddsMessage> lastOddsMap = new HashMap<>();
        if (null != lastMarketMessage && !CollectionUtils.isEmpty(lastMarketMessage.getMarketOddsList())) {
            lastOddsMap = lastMarketMessage.getMarketOddsList().stream().collect(Collectors.toMap(thi -> thi.getOddsType(), thi -> thi, (oldValue, newValue) -> newValue));
        }
        boolean flag = true;
        for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : standardMarketDataMessage.getMarketOddsList()) {
            if (standardMarketDataMessage.getMarketType() == 0 && MarginCategoryConfig.CHANGE_FLAP_BAK.contains(standardMarketDataMessage.getMarketCategoryId())) {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},盘口类型：{}", linkId, standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getMarketType());
                break;
            }
            if (MapUtils.isEmpty(lastOddsMap) || lastOddsMap.get(standardMarketOddsDataMessage.getOddsType()).getOriginalOddsValue().equals(0)) {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},paoddsvalue：{}", linkId, standardMarketDataMessage.getRelationMarketId(), standardMarketOddsDataMessage.getPaOddsValue());
                break;
            }
            if (!oddsTypeIdSet.contains(standardMarketOddsDataMessage.getRelationMarketOddsId())) {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},getRelationMarketOddsId:{}", linkId, standardMarketDataMessage.getRelationMarketId(), standardMarketOddsDataMessage.getRelationMarketOddsId());
                break;
            }
            if (Math.abs(lastOddsMap.get(standardMarketOddsDataMessage.getOddsType()).getOriginalOddsValue() - standardMarketOddsDataMessage.getOriginalOddsValue()) < MarginCategoryConfig.BASKETBALL_FLAP_ODDSVALUE_DOUBLE) {
                flag = false;
                log.info("::{}::数据源赔率开始排序以及计算挡板:不需要下发报警消息,统一盘口id:{},result:{}", linkId, standardMarketDataMessage.getRelationMarketId(), Math.abs(lastOddsMap.get(standardMarketOddsDataMessage.getOddsType()).getOriginalOddsValue() - standardMarketOddsDataMessage.getOriginalOddsValue()) < MarginCategoryConfig.BASKETBALL_FLAP_ODDSVALUE_DOUBLE);
                break;
            }
        }
        if (flag) {
            log.info("::{}::数据源挡板计算后需要下发报警消息，flag:{},standardMarketOddsDataMessage:{}", linkId, true, JSONUtil.toJsonStr(standardMarketDataMessage));
            riskCategorySet.add(standardMarketDataMessage.getMarketCategoryId());
        }
    }
    /**
     * 盘口状态检查
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    public void standardMarketStatusCheck(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessageList) {
        Map<String, List<StandardMarketDataMessage>> standardMarketMessageMap = standardMarketMessageList.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getDataSourceCode));
        for (Map.Entry<String, List<StandardMarketDataMessage>> lastEnty : standardMarketMessageMap.entrySet()) {
            String dataSourceCode = lastEnty.getKey();
            List<StandardMarketDataMessage> standardMarketMessages = lastEnty.getValue();
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
            for (StandardMarketDataMessage marketMessage : standardMarketMessages) {
                //下发数据 1：融合构建 不处理
                if (1 == marketMessage.getMarketSource()) {
                    continue;
                }
                ThirdSportMarket thirdSportMarket = thirdSportMarketMap.get(marketMessage.getThirdMarketSourceId());
                if (null == thirdSportMarket) {
                    thirdSportMarket = thirdSportMarketService.getItem(marketMessage.getDataSourceCode(), marketMessage.getThirdMarketSourceId(), thirdMatchInfo.getId());
                    log.info("::{}::,standardMarketStatusCheck,赛事id:{},玩法：{}，当前盘口ID:{},缓存不存在查询库：{}",
                            linkId, standardMatchInfo.getId(), marketMessage.getMarketCategoryId(), marketMessage.getRelationMarketId(), JSONObject.toJSONString(thirdSportMarket));
                }
                if (null != thirdSportMarket
                        && thirdSportMarket.getDataSourceCode().equals(marketMessage.getDataSourceCode())
                        && !marketMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)
                        && !thirdSportMarket.getStatus().equals(marketMessage.getThirdMarketSourceStatus())) {
                    log.info("::{}::,standardMarketStatusCheck,赛事id:{},玩法：{}，当前盘口ID:{}与最新缓存盘口状态不一致：{}-{},marketMessage:{}",
                            linkId, standardMatchInfo.getId(), marketMessage.getMarketCategoryId(), marketMessage.getRelationMarketId(), marketMessage.getThirdMarketSourceStatus(), thirdSportMarket.getThirdMarketSourceStatus(), JSONObject.toJSONString(marketMessage));
                    marketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketMessage.setRemark("与最新缓存盘口状态不一致关盘处理");
                    String marketKey = DigestUtil.md5Hex(
                            Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" +
                                    thirdSportMarket.getDataSourceCode() + "_" + thirdSportMarket.getMarketCategoryId());
                    redisService.hSet(marketKey, marketMessage.getRelationMarketId().toString(), marketMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                }
            }
        }
    }
}
