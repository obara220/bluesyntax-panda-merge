package com.panda.merge.odds.validate;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.cache.CommonItem;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.component.ClosedMarketPlaceSortHelper;
import com.panda.merge.component.FootballMarketsSoreProcessor;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.odds.MarketControlStatusEnum;
import com.panda.merge.dto.odds.MergeMarketStatusEnum;
import com.panda.merge.model.ConfigMarketCategoryPlace;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketCategory;
import com.panda.merge.odds.cache.*;
import com.panda.merge.odds.enums.MarketHandlingEnum;
import com.panda.merge.odds.model.CategoryMarketMessageData;
import com.panda.merge.odds.model.MatchMarketMessageData;
import com.panda.merge.odds.service.FlowControlService;
import com.panda.merge.odds.utils.MarketUtils;
import com.panda.merge.service.MarketCategorySellService;
import com.panda.merge.service.StandardSportMarketCategoryService;
import com.panda.merge.service.StandardSportMarketNewService;
import com.panda.merge.service.StandardSportMarketOddsService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.odds.constants.CategoryConstant.*;

/**
 * FootballValidateService
 *
 * @description: 足球盘口校验服务
 * @date: 4/12/2025
 **/
@Slf4j
@Service
public class FootballMarketValidateService {

    @Getter
    @NacosValue(value = "${market.validate.enabled.football:true}", autoRefreshed = true)
    private boolean footballValidateEnabled;

    @Getter
    @NacosValue(value = "${market.validate.enabled.expire:31000}", autoRefreshed = true)
    private Long footballValidateExpire;

    @Autowired
    private FootballMarketsSoreProcessor footballMarketsSoreProcessor;

    @Autowired
    private CategorySetCacheService categorySetCacheService;

    @Autowired
    private CategoryMarketPlaceCacheService categoryMarketPlaceCacheService;

    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private AutoCloseCacheService autoCloseCacheService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MatchLiveCacheService matchLiveCacheService;

    @Autowired
    private FootballScoreCacheService footballScoreCacheService;

    @Autowired
    private BallHeadValidationService ballHeadValidationService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketNewService;

    @Autowired
    private StandardSportMarketOddsService standardSportMarketOddsService;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private FlowControlService flowControlService;

    @Autowired
    private ClosedMarketPlaceSortHelper closedMarketPlaceSortHelper;

    @Value("#{'${ballhead.remove.decimal.categoryId}'.split(',')}")
    private Set<Long> ballHeadRemoveDecimalCategoryIdSet;

    public boolean shouldValidateFootball(StandardMatchInfo matchInfo) {
        if (Objects.isNull(matchInfo)) {
            return false;
        }
        Long sportId = matchInfo.getSportId();
        if (!Objects.equals(sportId, StandardSportTypeEnum.FootBall.code)) {
            return false;
        }
        if (Objects.equals(matchInfo.getDataSourceCode(), DataSourceCodeEnum.OD.getCode())){
            return false;
        }
        return footballValidateEnabled;
    }

    public List<StandardMarketMessage> validateFootball(String linkId,
                                 StandardMatchInfo standardMatchInfo,
                                 List<StandardMarketMessage> marketMessageList,
                                 MarketHandlingEnum operationType) {
        if (!shouldValidateFootball(standardMatchInfo)) {
            return marketMessageList;
        }
        if (CollectionUtils.isEmpty(marketMessageList)) {
            return marketMessageList;
        }
        if (flowControlService.inFlowControl(standardMatchInfo.getId())) {
            log.info("::{}::matchId:{} flow control, do not validate football match", linkId,standardMatchInfo.getId());
            return marketMessageList;
        }
        log.info("linkId:{},matchId:{}, validate football market,market size:{}",
                 linkId,
                 standardMatchInfo.getId(),
                 marketMessageList.size());
        MatchMarketMessageData matchMarketMessageData =
                MatchMarketMessageData.create(linkId, standardMatchInfo, marketMessageList, operationType);
        preValidate(matchMarketMessageData);
        validateMatch(matchMarketMessageData);
        postValidate(matchMarketMessageData);
        log.info("linkId:{},matchId:{}, validate finished",linkId,standardMatchInfo.getId());
        return matchMarketMessageData.toList();
    }

    /**
     * 盘口信息集合 按开关优先级 (A > B > C )调整盘口状态
     */
    public void validateMatch(MatchMarketMessageData matchData) {
        String linkId = matchData.linkId;
        StandardMatchInfo standardMatchInfo = matchData.standardMatchInfo;

        Map<String, Integer> categorySetStatusMap = categorySetCacheService.get(standardMatchInfo.getId());

        Map<String, ConfigMarketCategoryPlace> configMarketCategoryPlaceMap =
                categoryMarketPlaceCacheService.getMap(standardMatchInfo, matchData.marketMessageList);
        log.info("::{}:: dealMarketStatusList, 开始批量获取标准球种玩法信息", linkId);
        // 批量获取赛事开售信息
        List<Pair<Long, Long>> standardCategories = matchData.marketMessageList
                .stream()
                .map(t -> Pair.of(t.getMarketCategoryId(), standardMatchInfo.getSportId()))
                .collect(Collectors.toList());
        List<StandardSportMarketCategory> standardSportMarketCategories =
                standardSportMarketCategoryService.getItemsByStandardCategories(standardCategories);
        Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap = standardSportMarketCategories
                .stream()
                .collect(Collectors.toMap(t -> t.getMarketCategoryId() + "-" + t.getSportId(),
                                          Function.identity(),
                                          (v1, v2) -> v1));
        log.info("::{}:: dealMarketStatusList, 批量获取标准球种玩法信息结束，最终获取记录数:{}",
                 linkId,
                 standardSportMarketCategoryMap.size());
        for (Map.Entry<Long, CategoryMarketMessageData> categoryDataEntry : matchData.categoryMarkets.entrySet()) {
            validateCategory(categoryDataEntry.getValue(),
                             categorySetStatusMap,
                             configMarketCategoryPlaceMap,
                             standardSportMarketCategoryMap);
        }

    }

    /**
     * 开关封锁逻辑判断处理
     *
     * @param linkId
     * @param standardMarketMessage
     * @param standardMatchInfo
     */
    public void validateMarket(String linkId,
                               StandardMarketMessage standardMarketMessage,
                               StandardMatchInfo standardMatchInfo,
                               Map<String, Integer> categorySetStatusMap,
                               Map<String, ConfigMarketCategoryPlace> configMarketCategoryPlaceMap,
                               CategoryMarketMessageData categoryData) {
        if (standardMarketMessage.isValidated()) {
            return;
        }
        try {
            //A:数据源状态
            Integer status = standardMarketMessage.getThirdMarketSourceStatus();
            //B:操盘赛事级别状态
            Integer operatorStatus =
                    standardMatchInfo.getOperateMatchStatus() != -1 ? standardMatchInfo.getOperateMatchStatus() :
                            Constant.SPORT_MARKET.STATUS.ACTIVE;
            //C:盘口位置状态
            Integer placeNumStatus = 0;
            if (null != standardMarketMessage.getPlaceNum()) {
                //总玩法状态
                ConfigMarketCategoryPlace config = configMarketCategoryPlaceMap.get(
                        standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getMarketCategoryId() +
                                "_" + standardMarketMessage.getPlaceNum());
                if (config != null) {
                    placeNumStatus = Integer.valueOf(config.getPlaceNumStatus());
                    standardMarketMessage.setPlaceNumStatusDisplay(config.getPlaceNumStatusDisplay());
                }
                //位置状态 跟着子玩法
                ConfigMarketCategoryPlace childConfig = configMarketCategoryPlaceMap.get(
                        standardMarketMessage.getMarketCategoryId() + "_" +
                                standardMarketMessage.getChildMarketCategoryId() + "_" +
                                standardMarketMessage.getPlaceNum());
                if (childConfig != null) {
                    Integer ChildPlaceNumStatus = Integer.valueOf(childConfig.getPlaceNumStatus());
                    standardMarketMessage.setPlaceNumStatus(ChildPlaceNumStatus);
                    standardMarketMessage.setPlaceNumStatusDisplay(childConfig.getPlaceNumStatusDisplay());
                    if (placeNumStatus == 0) {
                        placeNumStatus =
                                Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(
                                                                                                    placeNumStatus),
                                                                                            Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(
                                                                                                    ChildPlaceNumStatus)));
                    }
                } else {
                    standardMarketMessage.setPlaceNumStatus(0);
                    log.info("::{}::赛事id:{},盘口id:{},位置:{},子玩法为空:{}",
                             linkId,
                             standardMatchInfo.getId(),
                             standardMarketMessage.getId(),
                             standardMarketMessage.getPlaceNum(),
                             standardMarketMessage.getChildMarketCategoryId());
                }
            }
            //6分钟玩法类玩法 坑位2/3固定操盘盘口位置关
            if (MarginCategoryConfig.SIX_PLACE_NUM_CATEGORY_CLOSE.contains(standardMarketMessage.getMarketCategoryId()) &&
                    MarginCategoryConfig.SIX_PLACE_NUM_CLOSE.contains(standardMarketMessage.getPlaceNum())) {
                placeNumStatus = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
            }
            Integer categoryStatus = 0;
            Integer categorySetStatus = categorySetStatusMap.get(standardMarketMessage.getMarketCategoryId().toString());
            if (null != categorySetStatus) {
                categoryStatus = categorySetStatus;
            }
            log.info("::{}::玩法集状态缓存={},当前玩法状态={}",linkId,categorySetStatusMap,categorySetStatus);
            //D:盘口校验状态
            Integer paStatus = standardMarketMessage.getPaStatus();

            //最终推送给下游的表现状态resultStatus
            MergeMarketStatusEnum internalStatusEnum = null;
            Integer resultStatus =
                    Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(
                            status), Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(paStatus)));

            if (operatorStatus > 0 && Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(operatorStatus) >=
                    Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(resultStatus)) {
                resultStatus = operatorStatus;
                internalStatusEnum = MergeMarketStatusEnum.MATCH;
            }
            if (placeNumStatus > 0 && Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(placeNumStatus) >=
                    Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(resultStatus)) {
                resultStatus = placeNumStatus;
                internalStatusEnum = MergeMarketStatusEnum.PLACE_NUM;
            }
            if (categoryStatus > 0 && Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(categoryStatus) >=
                    Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(resultStatus)) {
                resultStatus = categoryStatus;
                internalStatusEnum = MergeMarketStatusEnum.CATEGORY_SET;
            }

            //3515封盘能收单
            Integer endedStatus =
                    Constant.SPORT_MARKET.MARKET_STATUS_RESULT2_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(
                                                                                         operatorStatus),
                                                                                 Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(
                                                                                         standardMarketMessage.getPlaceNumStatus())));
            endedStatus =
                    Constant.SPORT_MARKET.MARKET_STATUS_RESULT2_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(
                            endedStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER2_MAP.get(categoryStatus)));
            if (Constant.SPORT_MARKET.STATUS.ENDED.equals(endedStatus)) {
                standardMarketMessage.setEndEdStatus(1);
                //操盘状态为收盘 关盘
                if (Constant.SPORT_MARKET.STATUS.ENDED.equals(operatorStatus)) {
                    resultStatus = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
                    internalStatusEnum = MergeMarketStatusEnum.MATCH_END;
                }
//                else if (Constant.SPORT_MARKET.STATUS.ENDED.equals(standardMarketMessage.getPlaceNumStatus())) {
//                    resultStatus = Constant.SPORT_MARKET.STATUS.DEACTIVATED;
//                    internalStatusEnum = MergeMarketStatusEnum.MARKET_END;
//                }
            } else {
                if (standardMarketMessage.getEndEdStatus() == null || standardMarketMessage.getEndEdStatus() == 1) {
                    standardMarketMessage.setEndEdStatus(0);
                }
            }
            //最终状态赋值
            updateMarket(internalStatusEnum,resultStatus,operatorStatus,categoryStatus,placeNumStatus,
                         standardMarketMessage,
                         categoryData);

            //设置操盘后台盘口状态
            Integer riskStatus =
                    Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(
                            operatorStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(placeNumStatus)));
            riskStatus =
                    Constant.SPORT_MARKET.MARKET_STATUS_RESULT_MAP.get(Math.max(Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(
                            riskStatus), Constant.SPORT_MARKET.MARKET_STATUS_ORDER_MAP.get(categoryStatus)));
            standardMarketMessage.setRiskStatus(riskStatus);
            //收盘状态3749也不拒单
            if (standardMarketMessage.getRiskStatus() == Constant.SPORT_MARKET.STATUS.ENDED) {
                standardMarketMessage.setRiskStatus(Constant.SPORT_MARKET.STATUS.ACTIVE);
            }
            log.info("::{}::标准赛事id:{},开关封锁处理,统一盘口id:{}," +
                             "三方数据源id:{},数据源状态A:status={},操盘赛事级别状态B:operatorStatus={}," +
                             "盘口位置状态C:placeNumStatus={},玩法集状态：categoryStatus={},盘口校验状态D:paStatus={},输出结果状态={},盘口状态变化原因：{}",
                     linkId,
                     standardMatchInfo.getId(),
                     standardMarketMessage.getId(),
                     standardMarketMessage.getThirdMarketSourceId(), standardMarketMessage.getThirdMarketSourceStatus(),
                     operatorStatus,
                     placeNumStatus,
                     categoryStatus,
                     paStatus,
                     resultStatus,
                     standardMarketMessage.getPaStatusReason());
        } catch (Exception e) {
            log.error("linkId:{},matchId:{},marketId:{}, validate failed",linkId,standardMatchInfo.getId(),standardMarketMessage.getId(),e);
        }


    }

    /**
     * 最后下发赔率 ，自动关盘兜底
     */
    public void automaticClosing(String linkId,
                                 StandardMatchInfo standardMatchInfo,
                                 CategoryMarketMessageData categoryData) {
        Object a01ExtendedTimeObjects  = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + standardMatchInfo.getId());
        Integer a01ExtendedTimeStatus = 0;
        if (!Objects.isNull(a01ExtendedTimeObjects)) {
            a01ExtendedTimeStatus = (Integer) a01ExtendedTimeObjects;
        }
        if (autoCloseCacheService.autoClose(standardMatchInfo.getId(), categoryData.categoryId)) {
            Integer finalA01ExtendedTimeStatus = a01ExtendedTimeStatus;
            boolean allMarketsAutoClosed = true;
            for (StandardMarketMessage standardMarketMessage : categoryData.marketMessages) {
                if (finalA01ExtendedTimeStatus == 1 && standardMarketMessage.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)
                        && MarginCategoryConfig.A01_EXTENDED_TIME_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())) {
                    allMarketsAutoClosed = false;
                    continue;
                }
                standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketMessage.setEndEdStatus(0);
                standardMarketMessage.setRiskStatus(2);
                standardMarketMessage.addRemark(MergeMarketStatusEnum.AUTO_CLOSE.name());
                log.info("::{}::automaticClosing关盘兜底,三方盘口：{}，标准盘口：{}",
                         linkId,
                         standardMarketMessage.getThirdMarketSourceId(),
                         standardMarketMessage.getId());
            }
            // A01 加时玩法被豁免时仍需走 validateMarket，避免 third=2 与 status=1 不一致
            categoryData.categoryClose = allMarketsAutoClosed;
        }
    }

    /**
     * 操盘需求
     * 球头大一方的赔率必须大于球头小一方的 （赔率校验不通过）
     * 球头大一方的赔率必须小于球头小一方的 （赔率通过）
     */
    public void checkMarketOddsByAddtion1(CategoryMarketMessageData categoryData) {
        Long categoryId = categoryData.categoryId;
        String linkId = categoryData.matchData.linkId;
        StandardMatchInfo standardMatchInfo = categoryData.matchData.standardMatchInfo;
        if (MarginCategoryConfig.VERIFY_SPORT.contains(standardMatchInfo.getSportId())) {
            List<StandardMarketMessage> marketMessages = categoryData.marketMessages
                    .stream()
                    .filter(e -> MarginCategoryConfig.CHANGE_FLAP1.contains(e.getMarketCategoryId()) &&
                            Constant.SPORT_MARKET.STATUS.ACTIVE.equals(e.getStatus()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(marketMessages) || marketMessages.size() <= 1) {
                return;
            }
            //按照球头降序排列
            marketMessages.sort((o1, o2) -> Double.compare(Double.parseDouble(o2.getAddition1()),
                                                           Double.parseDouble(o1.getAddition1())));
            //让分类，算主队
            boolean flag = true;
            if (MarginCategoryConfig.CHANGE_FLAP3.contains(categoryId)) {
                for (int i = 0; i < marketMessages.size() - 1; i++) {
                    if ((getPaOddsValueByOddsType(marketMessages.get(i).getMarketOddsList(), "1") -
                            getPaOddsValueByOddsType(marketMessages.get(i + 1).getMarketOddsList(), "1")) >= 0) {
                        flag = false;
                        break;
                    }
                }
            }
            //总分类，算Under
            else if (MarginCategoryConfig.CHANGE_FLAP2.contains(categoryId)) {
                for (int i = 0; i < marketMessages.size() - 1; i++) {
                    if ((getPaOddsValueByOddsType(marketMessages.get(i).getMarketOddsList(), "Under") -
                            getPaOddsValueByOddsType(marketMessages.get(i + 1).getMarketOddsList(), "Under")) >= 0) {
                        flag = false;
                        break;
                    }
                }
            }
            if (!flag) {
                for (StandardMarketMessage standardMarketMessage : marketMessages) {
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                    standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.THE_ODDS_ARE_NOT_SATISFIED.getCode(),
                                                                                           null));
                    log.info(
                            "::{}::赔率合法性校验,标准赛事id:{},统一盘口id:{},标准玩法id:{},赔率不满足 Odd0.5（2.05）>Odd1.5（1.90）>Odd2.5(1.75) 规则，盘口封盘",
                            linkId,
                            standardMatchInfo.getId(),
                            standardMarketMessage.getId(),
                            standardMarketMessage.getMarketCategoryId());
                }
            }

        }
    }

    /**
     * 盘口时间戳校验
     * 1.根据当前盘口 modifyTime,去对比数据商盘口时间戳
     * 2.当前盘口 modifyTime < 数据商盘口时间戳  为旧盘口
     * 3.出现旧盘口 ，直接使用上一次下发的盘口去替换当前盘口
     */
    public void standardMarketVerifyModifyTime(CategoryMarketMessageData categoryData) {
        String linkId = categoryData.matchData.linkId;
        StandardMatchInfo standardMatchInfo = categoryData.matchData.standardMatchInfo;
        List<StandardMarketMessage> standardMarketMessageList = categoryData.marketMessages;
        Long marketCategoryId = categoryData.categoryId;
        List<Long> verifyModifyTimeCategoryIds =
                MarginCategoryConfig.VERIFY_MODIFY_TIME_CATEGORY.get(standardMatchInfo.getSportId());
        if (CollectionUtils.isEmpty(verifyModifyTimeCategoryIds)) {
            return;
        }
        //不对 T01 A01 数据源处理
        List<StandardMarketMessage> marketList = standardMarketMessageList
                .stream()
                .filter(e -> verifyModifyTimeCategoryIds.contains(e.getMarketCategoryId()) &&
                        !MarginCategoryConfig.SPORT_TX_LOGIC.contains(e.getDataSourceCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(marketList)) {
            return;
        }
        //获取上一次下发的最新盘口 ，上一次不存在不处理
        String lastMarketOddsKey =
                DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
        //需要删除的盘口
        List<StandardMarketMessage> removeMarket = new ArrayList<>();
        //需要加入的盘口
        List<StandardMarketMessage> addMarket = new ArrayList<>();
        //有变动的玩法
        Set<Long> marketCategoryIds = new HashSet<>();

        List<StandardMarketMessage> lastStandardMarketMessages =
                (List<StandardMarketMessage>) redisService.hGet(lastMarketOddsKey, String.valueOf(marketCategoryId));
        //            log.info("::{}::,standardMarketVerifyModifyTime,玩法ID:{}，上一次盘口：{}",
        //                    linkId, marketCategoryId, JSONObject.toJSONString(lastStandardMarketMessages));
        if (CollectionUtils.isEmpty(lastStandardMarketMessages)) {
            return;
        }
        //根据盘口id分组
        Map<Long, StandardMarketMessage> lastStandardMarketMessageMap = lastStandardMarketMessages
                .stream()
                .collect(Collectors.toMap(StandardMarketMessage::getId, e -> e, (oldValue, newValue) -> newValue));
        //当前盘口时间戳 对比 上一次下发的时间戳
        for (StandardMarketMessage marketMessage : marketList) {
            //下发数据 1：融合构建 不处理
            if (1 == marketMessage.getMarketSource()) {
                continue;
            }
            //上一次不存在不处理,时间不存在不处理
            StandardMarketMessage lastStandardMarketMessage = lastStandardMarketMessageMap.get(marketMessage.getId());
            if (null == lastStandardMarketMessage || null == lastStandardMarketMessage.getVerifyModifyTime() ||
                    0 == lastStandardMarketMessage.getVerifyModifyTime()) {
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
            if (marketMessage.getDataSourceCode().equals(lastStandardMarketMessage.getDataSourceCode()) &&
                    marketMessage.getVerifyModifyTime() < lastStandardMarketMessage.getVerifyModifyTime()) {
                marketCategoryIds.add(marketCategoryId);
                removeMarket.add(marketMessage);
                addMarket.add(lastStandardMarketMessage);
                log.info("::{}::,standardMarketVerifyModifyTime,玩法：{}，当前盘口ID:{}与上一次盘口校验时间：{}-{}，不通过使用上一次的盘口",
                         linkId,
                         marketCategoryId,
                         marketMessage.getId(),
                         marketMessage.getVerifyModifyTime(),
                         lastStandardMarketMessage.getVerifyModifyTime());
            }
        }

        //去除盘口时间戳不通过的盘口，加入上一次盘口
        if (!org.springframework.util.CollectionUtils.isEmpty(removeMarket)) {
            standardMarketMessageList.removeAll(removeMarket);
            standardMarketMessageList.addAll(addMarket);
            //找出变动玩法下所有盘口重新排序
            againSortPlaceNum(linkId, standardMatchInfo.getId(), standardMarketMessageList, marketCategoryIds);
        }
    }

    private void preValidate(MatchMarketMessageData matchData) {
        if (MarketHandlingEnum.REDIS == matchData.handlingType) {
            matchData.marketMessageList.forEach(standardMarketMessage -> {
                standardMarketMessage.setPaStatusReason(null);
            });
        }
    }

    private void updateMarket(MergeMarketStatusEnum statusEnum,
                              Integer resultStatus,
                              Integer operatorStatus,
                              Integer categorySetStatus,
                              Integer placeNumStatus,
                              StandardMarketMessage standardMarketMessage,
                              CategoryMarketMessageData categoryData) {
        standardMarketMessage.setStatus(resultStatus);
        if (Objects.isNull(statusEnum)) {
            return;
        }
        switch (statusEnum) {
            case MATCH:
                if (operatorStatus == Constant.SPORT_MARKET.STATUS.DEACTIVATED){
                    categoryData.operatorCount++;
                }
                standardMarketMessage.setMergeMarketStatus(MergeMarketStatusEnum.MATCH.code);
                standardMarketMessage.addRemark(MergeMarketStatusEnum.MATCH.name());
                standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.MARCH_STATUS.getCode(),
                                                                                       operatorStatus.toString()));
                break;
            case MATCH_END:
                categoryData.operatorCount++;
                standardMarketMessage.setMergeMarketStatus(MergeMarketStatusEnum.MATCH_END.code);
                standardMarketMessage.addRemark(MergeMarketStatusEnum.MATCH_END.name());
                break;
            case PLACE_NUM:
                if (placeNumStatus == Constant.SPORT_MARKET.STATUS.DEACTIVATED){
                    categoryData.operatorCount++;
                }
                standardMarketMessage.setMergeMarketStatus(MergeMarketStatusEnum.PLACE_NUM.code);
                standardMarketMessage.addRemark(MergeMarketStatusEnum.PLACE_NUM.name());
                standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.PLACE_STATUS.getCode(),
                                                                                       placeNumStatus.toString()));
                break;
            case CATEGORY_SET:
                if (categorySetStatus == Constant.SPORT_MARKET.STATUS.DEACTIVATED){
                    categoryData.operatorCount++;
                }
                standardMarketMessage.setMergeMarketStatus(MergeMarketStatusEnum.CATEGORY_SET.code);
                standardMarketMessage.addRemark(MergeMarketStatusEnum.CATEGORY_SET.name());
                standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.CATEGORY_SET_STATUS.getCode(),
                                                                                       categorySetStatus.toString()));
                break;
            case MARKET_END:
                categoryData.operatorCount++;
                standardMarketMessage.setMergeMarketStatus(MergeMarketStatusEnum.PLACE_NUM.code);
                standardMarketMessage.setThirdMarketSourceStatus(resultStatus);
                standardMarketMessage.addRemark(MergeMarketStatusEnum.MARKET_END.name());
                break;
            default:

        }

    }

    public String getSellKey(Long matchId, Long marketCategoryId, Integer marketType) {
        return matchId + "-" + marketCategoryId + "-" + marketType;
    }

    private void validateCategory(CategoryMarketMessageData categoryData,
                                  Map<String, Integer> categorySetStatusMap,
                                  Map<String, ConfigMarketCategoryPlace> configMarketCategoryPlaceMap,
                                  Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap) {
        String linkId = categoryData.matchData.linkId;
        StandardMatchInfo standardMatchInfo = categoryData.matchData.standardMatchInfo;
        validateSportMarketCategory(linkId, categoryData, standardSportMarketCategoryMap, standardMatchInfo);

        automaticClosing(linkId, standardMatchInfo, categoryData);

        if (!categoryData.categoryClose ) {
            validatePandaCategory(categoryData, linkId, standardMatchInfo);
            validateCategoryScore(categoryData);
            List<StandardMarketMessage> standardMarketMessageList = categoryData.marketMessages;

            validateDataSource(categoryData);
            for (StandardMarketMessage standardMarketMessage : standardMarketMessageList) {
                validateMarket(linkId,
                               standardMarketMessage,
                               standardMatchInfo,
                               categorySetStatusMap,
                               configMarketCategoryPlaceMap,
                               categoryData);
            }
        }
        clearInvalidOdds(categoryData.marketMessages);
    }

    private void validateDataSource(CategoryMarketMessageData categoryData) {
        if (categoryData.matchData.handlingType == MarketHandlingEnum.MTS ||
                !CATEGORY_SCORE_TYPE_MAP.containsKey(categoryData.categoryId)) {
            return;
        }
        MarketCategorySell marketCategorySell = getCategorySell(categoryData);
        if (marketCategorySell == null) {
            log.error("linkId:{},matchId:{},categoryId:{}, marketCategorySell is null",
                      categoryData.matchData.linkId,
                      categoryData.matchData.standardMatchInfo.getId(),
                      categoryData.categoryId);
            return;
        }
        if ((StringUtils.equalsAnyIgnoreCase(marketCategorySell.getDataSourceCode(), "LS", "TX","L02")) ||
                categoryData.hasMultipleDataSources()) {
            return;
        }
        for (StandardMarketMessage standardMarketMessage : categoryData.marketMessages) {
            if (StringUtils.equalsAnyIgnoreCase(standardMarketMessage.getDataSourceCode(), "LS", "TX","L02")){
                return;
            }
            if (standardMarketMessage.isValidated()) {
                return;
            }
            if (!StringUtils.equals(standardMarketMessage.getDataSourceCode(),
                                    marketCategorySell.getDataSourceCode()) &&
                    Objects.equals(standardMarketMessage.getStatus(), Constant.SPORT_MARKET.STATUS.ACTIVE)&&
                    standardMarketMessage.getMarketSource()!=1 && !StringUtils.equals(standardMarketMessage.getDataSourceCode(),
                    "A99")) {
               standardMarketMessage.invalidDataSource();
            }
        }
    }

    private MarketCategorySell getCategorySell(CategoryMarketMessageData categoryData) {

        MatchMarketMessageData matchData = categoryData.matchData;
        Map<String, MarketCategorySell> categorySellMap = matchData.categorySellMap;
        if (null == categorySellMap) {
            categorySellMap = getMatchSellMap(matchData);
        }
        return categorySellMap.get(getSellKey(matchData.standardMatchInfo.getId(),
                                              categoryData.categoryId,
                                              matchLiveCacheService.getMarketType(matchData)));
    }

    private Map<String, MarketCategorySell> getMatchSellMap(MatchMarketMessageData matchData) {
        if (MapUtils.isEmpty(matchData.categoryMarkets)) {
            return matchData.categorySellMap = Collections.emptyMap();
        }
        List<String> keys = matchData.categoryMarkets
                .keySet()
                .stream()
                .map(categoryId -> marketCategorySellService.getKey(matchData.standardMatchInfo.getId(),
                                                                    categoryId,
                                                                    matchLiveCacheService.getMarketType(matchData)))
                .collect(Collectors.toList());
        return matchData.categorySellMap = marketCategorySellService
                .getItems(keys)
                .stream()
                .collect(Collectors.toMap(categorySell -> getSellKey(categorySell.getMatchId(),
                                                                     categorySell.getMarketCategoryId(),
                                                                     Integer.valueOf(categorySell.getMarketType())),
                                          Function.identity(),
                                          (v1, v2) -> v1));

    }

    private void validateCategoryScore(CategoryMarketMessageData categoryData) {
        footballMarketsSoreProcessor.check(categoryData);
    }

    private void validateSportMarketCategory(String linkId,
                                             CategoryMarketMessageData categoryData,
                                             Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap,
                                             StandardMatchInfo standardMatchInfo) {
        Long marketCategoryId = categoryData.categoryId;
        StandardSportMarketCategory standardSportMarketCategory =
                standardSportMarketCategoryMap.get(marketCategoryId + "-" + standardMatchInfo.getSportId());

        //单球种玩法关闭
        if (standardSportMarketCategory == null ||
                Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getStatus())) {
            log.info("linkId:{},matchId:{},category invalid id: {}",
                     linkId,
                     standardMatchInfo.getId(),
                     marketCategoryId);
            categoryData.marketMessages.forEach(standardMarketMessage -> {
                standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.PLAY_STATUS_CLOSE.getCode(),
                                                                                       "0"));
                standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                categoryData.categoryClose = true;
                standardMarketMessage.addRemark(MergeMarketStatusEnum.CATEGORY_INVALID.name());
            });
        }
    }

    private void validatePandaCategory(CategoryMarketMessageData categoryData,
                                       String linkId,
                                       StandardMatchInfo standardMatchInfo) {
        if (MarketHandlingEnum.PANDA == categoryData.matchData.handlingType) {

            //滚球阶段关闭赛前盘兜底
            closePreByLive(linkId, standardMatchInfo, categoryData);
        }
    }

    private void postValidate(MatchMarketMessageData matchData) {
        Long matchId = matchData.getMatchId();
        //获取缓存最新球头盘口 Map<dataSourceCode,Map<标准玩法ID，球头盘口数据>>
        List<String> keys = Arrays.asList(Constant.REDIS_KEY.THIRD_MARKET_HEAD + matchId + "_" + DataSourceCodeEnum.AO.code);
        Map<String, Object> thirdMarketHeadCacheMap = redisService.syncObtainMultiGetAll(keys);

        matchData.categoryMarkets.forEach((categoryId, categoryData) -> {

            closeDisplay(categoryData,thirdMarketHeadCacheMap);

        });

    }

    private static void doMarketCloseDisplayCommon(StandardMarketMessage marketMessage, CommonItem commonItem) {
        marketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
        marketMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
        marketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
        marketMessage.setMergeMarketStatus(MergeMarketStatusEnum.CLOSE_DISPLAY.code);
        marketMessage.addRemark(MergeMarketStatusEnum.CLOSE_DISPLAY.name());
        //只要走了关转封，就需要给附加字段赋值
        if (!Objects.isNull(commonItem) && !Objects.isNull(commonItem.getHome()) &&
                !Objects.isNull(commonItem.getAway())) {
            marketMessage.setAddition3(commonItem.getHome().toString());
            marketMessage.setAddition4(commonItem.getAway().toString());
        }

        marketMessage.getMarketOddsList().forEach(e -> {
            // 将Pa赔率设置为抽水赔率
            if (null == e.getOddsValue() || 0 == e.getOddsValue()) {
                e.setPaOddsValue(e.getOriginalOddsValue());
            } else {
                e.setPaOddsValue(e.getOddsValue());
            }
        });
    }

    private void closeDisplay(CategoryMarketMessageData categoryData, Map<String, Object> thirdMarketHeadCacheMap) {
        if (!CATEGORY_SCORE_TYPE_MAP.containsKey(categoryData.categoryId)) {
            return;
        }
        if (categoryData.categoryClose) {
            return;
        }
        if (categoryData.operatorCount > 0) {
            return;
        }
        if (!categoryData.allClosed()) {
            return;
        }
        if (!categoryData.marketSource()) {
            return;
        }

        Long categoryId = categoryData.categoryId;
        String linkId = categoryData.matchData.linkId;
        CommonItem commonItem = footballScoreCacheService.getCacheScoreMarketScoreType(categoryData);
        Integer cacheScoreSum = Objects.isNull(commonItem) ? 0 : commonItem.getScoreSum();
        List<StandardMarketMessage> marketMessages = categoryData.marketMessages;
        boolean isOverUnder = OVER_UNDER_SET.contains(categoryId);
        boolean isHandicap = HANDICAP_SET.contains(categoryId);

        try {
            String prefixKey = Constant.REDIS_KEY.THIRD_MARKET_HEAD + categoryData.matchData.getMatchId() + "_"
                    + DataSourceCodeEnum.AO.code;
            String key = prefixKey + "-" + categoryId;
            ThirdMarketDTO thirdMarketHeadCache = (ThirdMarketDTO) thirdMarketHeadCacheMap.get(key);
            if (thirdMarketHeadCache != null
                    && (System.currentTimeMillis() - thirdMarketHeadCache.getModifyTime()) < footballValidateExpire
                    && thirdMarketHeadCache.getStatus() < 2) {
                againSortPlaceNum(linkId, categoryData.matchData.getMatchId(), marketMessages,
                        new HashSet<>(Collections.singletonList(categoryId)));
                for (StandardMarketMessage marketMessage : marketMessages) {
                    if (skipMarketCloseDisplay(marketMessage, categoryData)) {
                        continue;
                    }
                    if (marketMessage.getPlaceNum() == 1) {
                        marketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        marketMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        marketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        marketMessage.setMergeMarketStatus(MergeMarketStatusEnum.CLOSE_DISPLAY.code);
                        marketMessage.addRemark(MergeMarketStatusEnum.CLOSE_DISPLAY.name());
                        marketMessage.setAddition1(thirdMarketHeadCache.getAddition1());
                        marketMessage.setAddition2(thirdMarketHeadCache.getAddition2());
                        marketMessage.setAddition3(thirdMarketHeadCache.getAddition3());
                        marketMessage.setAddition4(thirdMarketHeadCache.getAddition4());
                        marketMessage.setAddition5(thirdMarketHeadCache.getAddition5());
                        marketMessage.setPlaceNum(1);
                        marketMessage.addRemark(String.format("ballhead from %s to %s",
                                marketMessage.getThirdMarketSourceId(), thirdMarketHeadCache.getThirdMarketSourceId()));
                        ballHeadsRemoveDecimal(marketMessage);
                        refreshRelationMarketId(categoryData, marketMessage);
                        redisService.hSet(Constant.REDIS_KEY.THIRD_MARKET_HEAD_CLOSE + categoryData.matchData.getMatchId(),
                                categoryId.toString(), 1);
                        dedupeByRelationMarketIdKeepMinPlaceNum(linkId, categoryData);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::closeDisplay AO球头处理异常,categoryId:{}", linkId, categoryId, e);
        }

        againSortPlaceNum(linkId, categoryData.matchData.getMatchId(), marketMessages,
                new HashSet<>(Collections.singletonList(categoryId)));

        for (StandardMarketMessage marketMessage : marketMessages) {
            if (skipMarketCloseDisplay(marketMessage, categoryData)) {
                continue;
            }
            if (isOverUnder) {
                applyOverUnderCloseDisplay(categoryData, marketMessage, commonItem, cacheScoreSum);
            } else if (isHandicap) {
                applyHandicapCloseDisplay(categoryData, marketMessage, commonItem, cacheScoreSum);
            } else {
                applyStatusOnlyCloseDisplay(categoryData, marketMessage, commonItem);
            }
        }
        dedupeByRelationMarketIdKeepMinPlaceNum(linkId, categoryData);
    }

    private void applyOverUnderCloseDisplay(CategoryMarketMessageData categoryData,
                                            StandardMarketMessage marketMessage,
                                            CommonItem commonItem,
                                            Integer cacheScoreSum) {
        if (isScoreMismatch(marketMessage, commonItem)) {
            BigDecimal oHead = null;
            BigDecimal nHead = null;
            String oHeadStr = MarketUtils.getBallhead(marketMessage);
            Integer marketScoreSum = marketMessage.scoreSum();
            if (StringUtils.isNotEmpty(oHeadStr)) {
                oHead = new BigDecimal(oHeadStr);
                nHead = oHead;
                if (Objects.nonNull(cacheScoreSum) && Objects.nonNull(marketScoreSum)) {
                    nHead = oHead
                            .add(BigDecimal.valueOf(cacheScoreSum))
                            .subtract(BigDecimal.valueOf(marketScoreSum))
                            .max(BigDecimal.valueOf(cacheScoreSum).add(BigDecimal.valueOf(0.5)));
                }
            }
            if (odNoScore(marketMessage, marketScoreSum)
                    || nHead == null
                    || !ballHeadValidationService.validate(nHead, cacheScoreSum, marketMessage, categoryData)) {
                return;
            }
            doMarketCloseDisplay(categoryData, marketMessage, commonItem, oHead, nHead, cacheScoreSum);
            return;
        }
        doMarketCloseDisplayCommon(marketMessage, commonItem);
        refreshRelationMarketId(categoryData, marketMessage);
    }

    private void applyHandicapCloseDisplay(CategoryMarketMessageData categoryData,
                                           StandardMarketMessage marketMessage,
                                           CommonItem commonItem,
                                           Integer cacheScoreSum) {
        if (isScoreMismatch(marketMessage, commonItem)) {
            if (Objects.isNull(commonItem) || Objects.isNull(commonItem.getHome()) || Objects.isNull(commonItem.getAway())) {
                return;
            }
            if (StringUtils.isBlank(marketMessage.getAddition1())) {
                return;
            }
            BigDecimal add1 = new BigDecimal(marketMessage.getAddition1());
            int cacheHome = commonItem.getHome();
            int cacheAway = commonItem.getAway();
            BigDecimal newAdd2 = add1.subtract(BigDecimal.valueOf(cacheHome - cacheAway));
            if (!ballHeadValidationService.validate(newAdd2, cacheScoreSum, marketMessage, categoryData)) {
                return;
            }
            String oldAdd2 = marketMessage.getAddition2();
            doMarketCloseDisplayCommon(marketMessage, commonItem);
            marketMessage.setAddition2(newAdd2.stripTrailingZeros().toPlainString());
            if (oldAdd2 != null && !oldAdd2.equals(marketMessage.getAddition2())) {
                marketMessage.addRemark(String.format("market value from %s to %s", oldAdd2, marketMessage.getAddition2()));
            }
            ballHeadsRemoveDecimal(marketMessage);
            refreshRelationMarketId(categoryData, marketMessage);
            log.info("linkId:{},standardMatchId:{},categoryId:{},marketId:{}, handicap closeDisplay, add1:{}, add2:{}",
                    categoryData.matchData.linkId,
                    categoryData.matchData.standardMatchInfo.getId(),
                    categoryData.categoryId,
                    marketMessage.getRelationMarketId(),
                    marketMessage.getAddition1(),
                    marketMessage.getAddition2());
            return;
        }
        doMarketCloseDisplayCommon(marketMessage, commonItem);
        refreshRelationMarketId(categoryData, marketMessage);
    }

    private void applyStatusOnlyCloseDisplay(CategoryMarketMessageData categoryData,
                                             StandardMarketMessage marketMessage,
                                             CommonItem commonItem) {
        doMarketCloseDisplayCommon(marketMessage, commonItem);
        refreshRelationMarketId(categoryData, marketMessage);
    }

    private boolean isScoreMismatch(StandardMarketMessage marketMessage, CommonItem cacheScore) {
        if (Objects.isNull(cacheScore) || Objects.isNull(cacheScore.getHome()) || Objects.isNull(cacheScore.getAway())) {
            return false;
        }
        return !StringUtils.equals(marketMessage.score(), cacheScore.getHome() + "_" + cacheScore.getAway());
    }

    private void dedupeByRelationMarketIdKeepMinPlaceNum(String linkId, CategoryMarketMessageData categoryData) {
        List<StandardMarketMessage> messages = categoryData.marketMessages;
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        Map<Long, StandardMarketMessage> bestByRelationId = new HashMap<>();
        List<StandardMarketMessage> dropped = new ArrayList<>();
        for (StandardMarketMessage market : messages) {
            Long relationMarketId = market.getRelationMarketId();
            if (relationMarketId == null) {
                continue;
            }
            StandardMarketMessage existing = bestByRelationId.get(relationMarketId);
            if (existing == null) {
                bestByRelationId.put(relationMarketId, market);
                continue;
            }
            StandardMarketMessage kept = pickCloseDisplayBySmallerPlaceNum(existing, market);
            StandardMarketMessage removed = kept == existing ? market : existing;
            dropped.add(removed);
            log.info("::{}::关转封统一盘口id去重,categoryId:{},relationMarketId:{},保留坑位:{},剔除坑位:{}",
                    linkId, categoryData.categoryId, relationMarketId, kept.getPlaceNum(), removed.getPlaceNum());
            bestByRelationId.put(relationMarketId, kept);
        }
        if (dropped.isEmpty()) {
            return;
        }
        messages.removeAll(dropped);
        closedMarketPlaceSortHelper.compactPlaceNumsAfterDedupe(linkId, categoryData.categoryId, messages);
        log.info("::{}::关转封统一盘口id去重完成,categoryId:{},剔除:{}",
                linkId, categoryData.categoryId, dropped.size());
    }

    private StandardMarketMessage pickCloseDisplayBySmallerPlaceNum(StandardMarketMessage m1, StandardMarketMessage m2) {
        Integer p1 = m1.getPlaceNum() == null ? ClosedMarketPlaceSortHelper.UNKNOWN_PLACE_NUM : m1.getPlaceNum();
        Integer p2 = m2.getPlaceNum() == null ? ClosedMarketPlaceSortHelper.UNKNOWN_PLACE_NUM : m2.getPlaceNum();
        return p1 <= p2 ? m1 : m2;
    }
    public StandardMarketMessage ballHeadsRemoveDecimal(StandardMarketMessage standardMarketMessage) {
        //if (ballHeadRemoveDecimalCategoryIdSet.contains(standardMarketMessage.getMarketCategoryId())) {
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition1()) && standardMarketMessage.getAddition1().contains(".0")) {
                standardMarketMessage.setAddition1(standardMarketMessage.getAddition1().replace(".0", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition2()) && standardMarketMessage.getAddition2().contains(".0")) {
                standardMarketMessage.setAddition2(standardMarketMessage.getAddition2().replace(".0", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition3()) && standardMarketMessage.getAddition3().contains(".0")) {
                standardMarketMessage.setAddition3(standardMarketMessage.getAddition3().replace(".0", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition4()) && standardMarketMessage.getAddition4().contains(".0")) {
                standardMarketMessage.setAddition4(standardMarketMessage.getAddition4().replace(".0", ""));
            }
            if (StringUtils.isNotBlank(standardMarketMessage.getAddition5()) && standardMarketMessage.getAddition5().contains(".0")) {
                standardMarketMessage.setAddition5(standardMarketMessage.getAddition5().replace(".0", ""));
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
       // }
        return standardMarketMessage;
    }
    private void doMarketCloseDisplay(CategoryMarketMessageData categoryData,
                                      StandardMarketMessage marketMessage,
                                      CommonItem commonItem,
                                      BigDecimal oValue,
                                      BigDecimal nValue,
                                      Integer cacheScoreSum) {
        doMarketCloseDisplayCommon(marketMessage, commonItem);

        if (oValue != null && !oValue.equals(nValue)) {
            marketMessage.addRemark(String.format("ballhead from %s to %s", oValue, nValue));
            MarketUtils.setBallhead(marketMessage, String.valueOf(nValue));
        }
        ballHeadsRemoveDecimal(marketMessage);
        refreshRelationMarketId(categoryData, marketMessage);
        log.info("linkId:{},standardMatchId:{},categoryId:{},markedId:{}, closeDisplay, old ballhead:{},new " +
                         "ballhead:{}," + "marketScoreSum:{}," + "cacheScoreSum:{}",
                 categoryData.matchData.linkId,
                 categoryData.matchData.standardMatchInfo.getId(),
                 categoryData.categoryId, marketMessage.getRelationMarketId(), oValue, nValue,
                 marketMessage.scoreSum(),
                 cacheScoreSum);
    }

    private boolean skipMarketCloseDisplay(StandardMarketMessage marketMessage,
                                           CategoryMarketMessageData categoryData) {
        if (marketMessage.isFinal()) {
            return true;
        }
        if (Objects.equals(marketMessage.getMarketType(), MarketTypeEnum.PREMATCH.getCode())) {
            return true;
        }
        if (StringUtils.equalsAnyIgnoreCase(marketMessage.getDataSourceCode(), "AO")) {
            return true;
        }
        if (categoryData.hasMultipleDataSources() && marketMessage.isOldClose()) {
            return true;
        }
        if (CollectionUtils.isEmpty(marketMessage.getMarketOddsList())) {
            return true;
        }
        return false;
    }

    private void refreshRelationMarketId(CategoryMarketMessageData categoryData, StandardMarketMessage marketMessage) {
        MatchMarketMessageData matchData = categoryData.matchData;

        Long relationMarketId = standardSportMarketNewService.createRelationMarketId(matchData.linkId,
                                                                                     matchData.standardMatchInfo.getId(),
                                                                                     marketMessage);
        marketMessage.setRelationMarketId(relationMarketId);
        Optional.ofNullable(marketMessage.getMarketOddsList()).ifPresent(oddsList -> {
            oddsList.forEach(odds -> {
                odds.setRelationMarketId(relationMarketId);
                odds.setId(standardSportMarketOddsService.createRelationMarketOddsId(odds, marketMessage));
            });
        });
    }

    private void clearInvalidOdds(List<StandardMarketMessage> marketMessages) {
        marketMessages.forEach(standardMarketMessage -> {
            //存在关盘但是有投注项的盘口，并且paoddsvalue有为null的投注项，直接清空投注项
            Integer resultStatus = standardMarketMessage.getStatus();
            if (Constant.SPORT_MARKET.STATUS.DEACTIVATED.equals(resultStatus) &&
                    !org.springframework.util.CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())) {
                for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                    if (null == standardMarketOddsMessage.getPaOddsValue()) {
                        standardMarketMessage.getMarketOddsList().clear();
                        break;
                    }
                }
            }
        });
    }

    private Integer getPaOddsValueByOddsType(List<StandardMarketOddsMessage> standardMarketOddsMessages,
                                             String oddsType) {
        if (org.springframework.util.CollectionUtils.isEmpty(standardMarketOddsMessages)) {
            return 0;
        }
        for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketOddsMessages) {
            if (standardMarketOddsMessage.getOddsType().equalsIgnoreCase(oddsType) &&
                    Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(standardMarketOddsMessage.getActive())) {
                return standardMarketOddsMessage.getPaOddsValue();
            }
        }
        return 0;
    }

    /**
     * 重新排序
     *
     * @param standardMarketMessageList
     */
    private void againSortPlaceNum(String linkId, Long matchId, List<StandardMarketMessage> standardMarketMessageList, Set<Long> marketCategoryIds) {
        Map<Long, List<StandardMarketMessage>> standardMarketMessagesMap = standardMarketMessageList
                .stream()
                .filter(standardMarketMessage -> marketCategoryIds.contains(standardMarketMessage.getMarketCategoryId()))
                .collect(Collectors.groupingBy(StandardMarketMessage::getChildMarketCategoryId));
        for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessagesMap.entrySet()) {
            List<StandardMarketMessage> standardMarketMessages = entry.getValue();
            if (closedMarketPlaceSortHelper.isAllClosedForPlaceSort(standardMarketMessages)) {
                closedMarketPlaceSortHelper.sortClosedStandardMarkets(linkId, matchId, entry.getKey(), standardMarketMessages, standardMarketMessageList);
                continue;
            }
            // 算出投注项赔率差
            standardMarketMessages.forEach(m -> {
                if (!CollectionUtils.isEmpty(m.getMarketOddsList())) {
                    IntSummaryStatistics summary = m
                            .getMarketOddsList()
                            .stream()
                            .mapToInt(StandardMarketOddsMessage::getOriginalOddsValue)
                            .summaryStatistics();
                    m.setOddsMetric(summary.getMax() - summary.getMin());

                } else {
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
     * 滚球阶段关闭赛前
     */
    private void closePreByLive(String linkId,
                                StandardMatchInfo standardMatchInfo, CategoryMarketMessageData categoryData) {
        List<StandardMarketMessage> standardMarketMessageList = categoryData.marketMessages;
        boolean oddsLive = matchLiveCacheService.isOddsLive(categoryData.matchData);
        //设置子玩法id
        standardMarketMessageList.forEach(marketMessage -> {
            if (oddsLive && marketMessage.getMarketType() == 1 &&
                    marketMessage.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
                marketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketMessage.setControlStatus(MarketControlStatusEnum.FINAL.code);
                marketMessage.setRemark("关闭赛前盘兜底");
                log.info("::{}::赛事ID:{},三方盘口ID:{},标准盘口ID:{},关闭赛前盘兜底。",
                         linkId,
                         standardMatchInfo.getId(),
                         marketMessage.getThirdMarketSourceId(),
                         marketMessage.getId());
            }
        });
    }

    private boolean odNoScore(StandardMarketMessage marketMessage, Integer marketScoreSum) {
        return StringUtils.equalsIgnoreCase(marketMessage.getDataSourceCode(), "OD") &&
                Objects.isNull(marketScoreSum);
    }

}
