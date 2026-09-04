package com.panda.merge.component;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.MarketTipsLanguageEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.model.ConfigMarketCategoryPlace;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketCategory;
import com.panda.merge.service.ConfigMarketCategoryPlaceService;
import com.panda.merge.service.StandardSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 玩法开关封锁
 */
@Slf4j
@Component
public class DealMarketStatusProcessor {
    @Autowired
    private ConfigMarketCategoryPlaceService configMarketCategoryPlaceService;

    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private RedisService redisService;

    /**
     * 盘口信息集合 按开关优先级 (A > B > C )调整盘口状态
     *
     * @param linkId
     * @param standardMarketMessages
     * @param standardMatchInfo
     */
    public void dealMarketStatusList(String linkId, List<StandardMarketMessage> standardMarketMessages, StandardMatchInfo standardMatchInfo) {
        //玩法集玩法状态
        String redisCategorySetKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_SET_STATUS + standardMatchInfo.getId());
        Map<String, Integer> categorySetStatusMap = redisService.hGetAll(redisCategorySetKey);
        //玩法坑位状态key
        List<String> categoryIdsKey = new ArrayList<>();
        String cacheConfigMarketPlaceKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfo.getId();
        standardMarketMessages.stream().forEach(standardMarketMessage -> {
            categoryIdsKey.add(standardMatchInfo.getId() + "_" + standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getPlaceNum());
            categoryIdsKey.add(standardMatchInfo.getId() + "_" + standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getChildMarketCategoryId() + "_" + standardMarketMessage.getPlaceNum());
        });
        List<Object> values = redisService.hMulGetBasedBucket(cacheConfigMarketPlaceKey, categoryIdsKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
        Map<String, ConfigMarketCategoryPlace> configMarketCategoryPlaceMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(values)) {
            for (int i = 0; i < values.size(); i++) {
                ConfigMarketCategoryPlace categoryPlace = (ConfigMarketCategoryPlace) values.get(i);
                if (null != categoryPlace) {
                    configMarketCategoryPlaceMap.put(categoryPlace.getStandardCategoryId() + "_" + categoryPlace.getChildStandardCategoryId() + "_" + categoryPlace.getPlaceNum(), categoryPlace);
                }
            }
        }
        log.info("::{}:: dealMarketStatusList, 开始批量获取标准球种玩法信息", linkId);
        // 批量获取赛事开售信息
        List<Pair<Long, Long>> standardCategories = standardMarketMessages.stream()
                .map(t -> Pair.of(t.getMarketCategoryId(), standardMatchInfo.getSportId()))
                .collect(Collectors.toList());
        List<StandardSportMarketCategory> standardSportMarketCategories = standardSportMarketCategoryService.getItemsByStandardCategories(standardCategories);
        Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap = standardSportMarketCategories.stream()
                .collect(Collectors.toMap(t -> t.getMarketCategoryId() + "-" + t.getSportId(),
                        Function.identity(), (v1, v2) -> v1));
        log.info("::{}:: dealMarketStatusList, 批量获取标准球种玩法信息结束，最终获取记录数:{}", linkId, standardSportMarketCategoryMap.size());
        Map<Long, List<StandardMarketMessage>> standardMarketMessageMap = standardMarketMessages.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        for (Map.Entry<Long, List<StandardMarketMessage>> standardMarketMessageEntry : standardMarketMessageMap.entrySet()) {
            Long marketCategoryId = standardMarketMessageEntry.getKey();
            //单球种玩法关闭,
            StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryMap.get(marketCategoryId+"-"+standardMatchInfo.getSportId());
            List<StandardMarketMessage> standardMarketMessageList = standardMarketMessageEntry.getValue();
            for (StandardMarketMessage standardMarketMessage : standardMarketMessageList) {
                marketStatus(linkId, standardMarketMessage, standardMatchInfo, categorySetStatusMap, standardSportMarketCategory, configMarketCategoryPlaceMap);
            }
        }

    }


    /**
     * 开关封锁逻辑判断处理
     *
     * @param linkId
     * @param standardMarketMessage
     * @param standardMatchInfo
     * @param categorySetStatusMap         玩法集开关
     * @param standardSportMarketCategory  赛种开关
     * @param configMarketCategoryPlaceMap 玩法状态
     */
    public void marketStatus(String linkId, StandardMarketMessage standardMarketMessage, StandardMatchInfo standardMatchInfo, Map<String, Integer> categorySetStatusMap, StandardSportMarketCategory standardSportMarketCategory, Map<String, ConfigMarketCategoryPlace> configMarketCategoryPlaceMap) {
        //A:数据源状态
        Integer status = standardMarketMessage.getThirdMarketSourceStatus();
        //B:操盘赛事级别状态
        Integer operatorStatus = standardMatchInfo.getOperateMatchStatus() != -1 ? standardMatchInfo.getOperateMatchStatus() : Constant.SPORT_MARKET.STATUS.ACTIVE;
        //C:盘口位置状态
        Integer placeNumStatus = 0;
        if (null != standardMarketMessage.getPlaceNum()) {
            //总玩法状态
            ConfigMarketCategoryPlace config = configMarketCategoryPlaceMap.get(standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getPlaceNum());
            if (config != null) {
                placeNumStatus = Integer.valueOf(config.getPlaceNumStatus());
                standardMarketMessage.setPlaceNumStatusDisplay(config.getPlaceNumStatusDisplay());
            }
            //位置状态 跟着子玩法
            ConfigMarketCategoryPlace childConfig = configMarketCategoryPlaceMap.get(standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getChildMarketCategoryId() + "_" + standardMarketMessage.getPlaceNum());
            if (childConfig != null) {
                Integer ChildPlaceNumStatus = Integer.valueOf(childConfig.getPlaceNumStatus());
                standardMarketMessage.setPlaceNumStatus(ChildPlaceNumStatus);
                standardMarketMessage.setPlaceNumStatusDisplay(childConfig.getPlaceNumStatusDisplay());
                if (placeNumStatus == 0) {
                    placeNumStatus = Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(placeNumStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(ChildPlaceNumStatus)));
                }
            } else {
                standardMarketMessage.setPlaceNumStatus(0);
                log.info("::{}::赛事id:{},盘口id:{},位置:{},子玩法为空:{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getPlaceNum(), standardMarketMessage.getChildMarketCategoryId());
            }
        }
//        if (Constant.SPORT_MARKET.STATUS.ENDED.equals(standardMarketMessage.getPlaceNumStatus())) {
//            if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) && MarginCategoryConfig.FootBall_MAIN3484_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())) {
//                placeNumStatus = placeNumStatus == 13 ? 0 : placeNumStatus;
//            } else if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) && MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())) {
//                placeNumStatus = placeNumStatus == 13 ? 0 : placeNumStatus;
//            }
//        }
        //6分钟玩法类玩法 坑位2/3固定操盘盘口位置关
        if (MarginCategoryConfig.SIX_PLACE_NUM_CATEGORY_CLOSE.contains(standardMarketMessage.getMarketCategoryId())
                && MarginCategoryConfig.SIX_PLACE_NUM_CLOSE.contains(standardMarketMessage.getPlaceNum())) {
            placeNumStatus = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
        }
        Integer categoryStatus = 0;
        Integer categorySetStatus = categorySetStatusMap.get(standardMarketMessage.getMarketCategoryId().toString());
        if (null != categorySetStatus) {
            categoryStatus = categorySetStatus;
        }
        //D:盘口校验状态
        Integer paStatus = standardMarketMessage.getPaStatus();

        //最终推送给下游的表现状态resultStatus
        Integer resultStatus = Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(status), Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(paStatus)));
        if (Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(operatorStatus) > Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(resultStatus)) {
            resultStatus = operatorStatus;
            standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.MARCH_STATUS.getCode(), operatorStatus.toString()));
        }
        if (Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(placeNumStatus) > Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(resultStatus)) {
            resultStatus = placeNumStatus;
            standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.PLACE_STATUS.getCode(), placeNumStatus.toString()));
        }
        if (Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(categoryStatus) > Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(resultStatus)) {
            resultStatus = categoryStatus;
            standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.CATEGORY_SET_STATUS.getCode(), categoryStatus.toString()));
        }

        //3515封盘能收单
        Integer endedStatus = Constant.SPORT_MARKET.MARKET_STATUS_RESULT2_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(operatorStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(standardMarketMessage.getPlaceNumStatus())));
        endedStatus = Constant.SPORT_MARKET.MARKET_STATUS_RESULT2_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(endedStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(categoryStatus)));
        if (Constant.SPORT_MARKET.STATUS.ENDED.equals(endedStatus)) {
            standardMarketMessage.setEndEdStatus(1);
            //操盘状态为收盘 关盘
            if (operatorStatus.equals(Constant.SPORT_MARKET.STATUS.ENDED)) {
                resultStatus = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
            }
        } else {
            if (standardMarketMessage.getEndEdStatus() == null || standardMarketMessage.getEndEdStatus() == 1) {
                standardMarketMessage.setEndEdStatus(0);
            }
        }
//        if (13 == standardMarketMessage.getPlaceNumStatus() || placeNumStatus == 13) {
//            standardMarketMessage.setEndEdStatus(1);
//            resultStatus = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
//        }
        //玩法开售状态校验
        if (!Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(resultStatus)) {
            //单球种玩法关闭
            if (standardSportMarketCategory == null || Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getStatus())) {
                standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.PLAY_STATUS_CLOSE.getCode(), placeNumStatus.toString()));
                resultStatus = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
            }
        }
        //最终状态赋值
        standardMarketMessage.setStatus(resultStatus);

        //设置操盘后台盘口状态
        Integer riskStatus = Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(operatorStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(placeNumStatus)));
        riskStatus = Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(riskStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(categoryStatus)));
        standardMarketMessage.setRiskStatus(riskStatus);
        //收盘状态3749也不拒单
        if (standardMarketMessage.getRiskStatus() == Constant.SPORT_MARKET.STATUS.ENDED){
            standardMarketMessage.setRiskStatus(Constant.SPORT_MARKET.STATUS.ACTIVE);
        }
//        if (standardMarketMessage.getPlaceNumStatus() == 13) {
//            standardMarketMessage.setPlaceNumStatus(0);
//        }
        log.info("::{}::标准赛事id:{},开关封锁处理,统一盘口id:{}," + "三方数据源id:{},数据源状态A:status={},操盘赛事级别状态B:operatorStatus={}," + "盘口位置状态C:placeNumStatus={},玩法集状态：categoryStatus={},盘口校验状态D:paStatus={},输出结果状态={},盘口状态变化原因：{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), status, operatorStatus, placeNumStatus, categoryStatus, paStatus, resultStatus, standardMarketMessage.getPaStatusReason());

        //存在关盘但是有投注项的盘口，并且paoddsvalue有为null的投注项，直接清空投注项
        if (Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(resultStatus) && !CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())) {
            for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                if (null == standardMarketOddsMessage.getPaOddsValue()) {
                    standardMarketMessage.getMarketOddsList().clear();
                    break;
                }
            }
        }

    }
}
