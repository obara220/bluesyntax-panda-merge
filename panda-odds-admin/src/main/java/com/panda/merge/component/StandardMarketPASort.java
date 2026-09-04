package com.panda.merge.component;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.odds.MergeMarketStatusEnum;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 77794
 * 最后的pa赔率重新排序,只处理足球主列表 全场让球、半场让球
 */
@Slf4j
@Component
public class StandardMarketPASort {

    public static List<Long> CARKETCATEGORY_ID = Arrays.asList(4L, 19L, 113L, 121L, 306L, 308L, 128L, 130L, 1100414L, 1100416L, 1100406L, 1100409L, 334L, 100L);

    public void sort(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessages, int marketType, boolean matchTradType) {
        if (0 == marketType || matchTradType || !standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code)) {
            return;
        }
        try {
            Map<Long, List<StandardMarketMessage>> standardMarketMessagesMap = standardMarketMessages.stream().filter(s -> CARKETCATEGORY_ID.contains(s.getChildMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketMessage::getChildMarketCategoryId));
            for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessagesMap.entrySet()) {
                List<StandardMarketMessage> standardMarketMessage = entry.getValue();
                if (standardMarketMessage.stream().anyMatch(m ->
                        Objects.equals(MergeMarketStatusEnum.CLOSE_DISPLAY.code, m.getMergeMarketStatus()))) {
                    log.info("::{}::关转封批次跳过PA排序,标准赛事id:{},子玩法:{}",
                            linkId, standardMatchInfo.getId(), entry.getKey());
                    continue;
                }
                //取盘口中有投注项的有效数据
                List<StandardMarketMessage> standardMarketsValid = standardMarketMessage.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList()) && e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
                //计算出投注项赔率差
                standardMarketsValid.forEach(m -> {
                    if (!CollectionUtils.isEmpty(m.getMarketOddsList())) {
                        m.setOddsMetric(m.getMarketOddsList().stream().map(StandardMarketOddsMessage::getOddsValue).reduce(0, (a, b) -> a >= b ? a - b : b - a));
                    } else {
                        m.setOddsMetric(999999);
                    }
                });
                //数据商状态、赔率差 升序排序
                ListUtils.sort(standardMarketsValid, true, "thirdMarketSourceStatus", "oddsMetric");
                //排序字段placeNum
                Integer placeNum = 1;
                for (StandardMarketMessage marketDataMessage : standardMarketsValid) {
                    if (!marketDataMessage.getPlaceNum().equals(placeNum)) {
                        log.info("::{}::重新盘口排序后有效盘口,标准赛事id:{},统一盘口id:{},玩法:{},子玩法:{},盘口位置:{},三方盘口源id:{},三方盘口源状态:{},盘口状态:{},赔率差值:{},", linkId, standardMatchInfo.getId(), marketDataMessage.getId(), marketDataMessage.getMarketCategoryId(), marketDataMessage.getChildMarketCategoryId(), marketDataMessage.getPlaceNum() + "-" + placeNum, marketDataMessage.getThirdMarketSourceId(), marketDataMessage.getThirdMarketSourceStatus(), marketDataMessage.getStatus(), marketDataMessage.getOddsMetric());
                    }
                    marketDataMessage.setPlaceNum(placeNum);
                    placeNum = placeNum + 1;
                }
                //无效排序
                List<StandardMarketMessage> standardMarketsInvalids = standardMarketMessage.stream().filter(e -> CollectionUtils.isEmpty(e.getMarketOddsList()) || e.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(standardMarketsInvalids)) {
                    //盘口位置排序
                    ListUtils.sort(standardMarketsInvalids, true, "placeNum");
                    for (StandardMarketMessage marketDataMessage : standardMarketsInvalids) {
                        if (!marketDataMessage.getPlaceNum().equals(placeNum)) {
                            log.info("::{}::重新盘口排序后无效盘口,标准赛事id:{},统一盘口id:{},玩法:{},子玩法:{},盘口位置:{},三方盘口源id:{},三方盘口源状态:{},盘口状态:{},赔率差值:{},", linkId, standardMatchInfo.getId(), marketDataMessage.getId(), marketDataMessage.getMarketCategoryId(), marketDataMessage.getChildMarketCategoryId(), marketDataMessage.getPlaceNum() + "-" + placeNum, marketDataMessage.getThirdMarketSourceId(), marketDataMessage.getThirdMarketSourceStatus(), marketDataMessage.getStatus(), marketDataMessage.getOddsMetric());
                        }
                        marketDataMessage.setPlaceNum(placeNum);
                        placeNum = placeNum + 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("::" + linkId + "::重新盘口排序后无效盘口,出现异常,", e);
        }
    }

}
