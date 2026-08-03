package com.panda.merge.component;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.panda.merge.component.AutoDiffCountMarketMalay.subDoubleTwo;

@Component
@Slf4j
public class BasketballOriginalOddsLimitProcessor {

    @Autowired
    RedisService redisService;

    public void basketballOriginalOdds(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessageList) {
        try {
            if (!StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())) {
                return;
            }
            List<StandardMarketDataMessage> standardMarketMessages = standardMarketMessageList.stream()
                    .filter(m -> m.getMarketCategoryId().equals(37L)
                            && m.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED
                            && !CollectionUtils.isEmpty(m.getMarketOddsList())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(standardMarketMessages)) {
                return;
            }
            Object obj = redisService.get(Constant.REDIS_KEY.RCS_BASKETBALL_ORIGINAL_ODDS_LIMIT);
            if (ObjectUtils.isEmpty(obj)) {
                return;
            }
            Double originalOddsLimit = (Double) obj;
            for (StandardMarketDataMessage standardMarketMessage : standardMarketMessages) {
                String oddsType = null;
                for (StandardMarketOddsDataMessage marketOdds : standardMarketMessage.getMarketOddsList()) {
                    if (null == marketOdds.getOriginalOddsValue() || marketOdds.getOriginalOddsValue() == 0) {
                        return;
                    }
                    if (null == marketOdds.getPaOddsValue() || marketOdds.getPaOddsValue() == 0) {
                        return;
                    }

                    double changOriginalOdds = BigDecimalUtils.divide(marketOdds.getOriginalOddsValue(), 100000, 2);
                    double changPaOdds = BigDecimalUtils.divide(marketOdds.getPaOddsValue(), 100000, 2);
                    log.info("::{}::赛事：{}，盘口：{}，basketballOriginalOdds开始计算,changOriginalOdds：{} ，changPaOdds：{}，originalOddsLimit：{},oddsType:{}",
                            linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), changOriginalOdds, changPaOdds, originalOddsLimit, marketOdds.getOddsType());
                    if (changOriginalOdds >= originalOddsLimit && changPaOdds < 1.01) {
                        marketOdds.setPaOddsValue(101000);
                        oddsType = marketOdds.getOddsType();
                    }
                }
                if (StringUtils.isNotEmpty(oddsType)) {
                    String finalOddsType = oddsType;
                    StandardMarketOddsDataMessage marketOdds = standardMarketMessage.getMarketOddsList().stream().filter(o -> !finalOddsType.equals(o.getOddsType())).findAny().orElse(null);
                    if (null == marketOdds) {
                        return;
                    }
                    Double paodds = subDoubleTwo(1 / (1 - 1 / (1.01 * (marketOdds.getMargin() / 100))) / (marketOdds.getMargin() / 100)) * 100000;
                    log.info("::{}::赛事：{}，盘口：{}，basketballOriginalOdds计算完成：{}，margin：{},oddsType:{}",
                            linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), paodds, marketOdds.getMargin(), marketOdds.getOddsType());
                    marketOdds.setPaOddsValue(paodds.intValue());
                }
            }
        } catch (Exception e) {
            log.error("::" + linkId + "::basketballOriginalOdds,出现异常", e);
        }

    }

}
