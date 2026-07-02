package com.panda.merge.odds.validate;

import com.panda.merge.common.enums.MatchPeriodForMatchOverEnum;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.odds.cache.FootballTimeCacheService;
import com.panda.merge.odds.model.CategoryMarketMessageData;
import com.panda.merge.odds.model.MatchMarketMessageData;
import com.panda.merge.odds.model.MatchTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

import static com.panda.merge.odds.constants.ValidatorConstant.*;

/**
 * BallHeadValidationService
 *
 * @description:
 * @date: 5/16/2025
 **/
@Service
@Slf4j
public class BallHeadValidationService {

    @Autowired
    private FootballTimeCacheService footballTimeCacheService;

    public boolean validate(BigDecimal ballHead,
                            Integer cacheScoreSum,
                            StandardMarketMessage marketMessage,
                            CategoryMarketMessageData categoryData) {
        if (ballHead == null) {
            return true;
        }
        StandardMatchInfo standardMatchInfo = categoryData.matchData.standardMatchInfo;
        Long matchPeriodId = standardMatchInfo.getMatchPeriodId();
        if (matchPeriodId == null) {
            return true;
        }
        MatchPeriodForMatchOverEnum matchPeriodEnum = getMatchPeriodEnum(categoryData.matchData);
        if (matchPeriodEnum == null) {
            return true;
        }

        switch (matchPeriodEnum) {
            case MATCH_1H:
                return validateFirstHalf(ballHead, cacheScoreSum, marketMessage, categoryData);
            case MATCH_2H:
                return validateSecondHalf(ballHead, cacheScoreSum, marketMessage, categoryData);
            case OverTime_1H:
                return validateOverTimeFirstHalf(ballHead, cacheScoreSum, marketMessage, categoryData);
            case OverTime_2H:
                return validateOverTimeSecondHalf(ballHead, cacheScoreSum, marketMessage, categoryData);
            default:
                return true;
        }
    }

    private MatchPeriodForMatchOverEnum getMatchPeriodEnum(MatchMarketMessageData matchData) {
        if (matchData.matchPeriodEnum == null) {
            Long matchPeriodId = matchData.standardMatchInfo.getMatchPeriodId();
            if (matchPeriodId == null) {
                return null;
            }
            matchData.matchPeriodEnum = MatchPeriodForMatchOverEnum.getEnum(matchPeriodId);
        }
        return matchData.matchPeriodEnum;
    }

    private boolean validateFirstHalf(BigDecimal ballHead,
                                      Integer cacheScoreSum,
                                      StandardMarketMessage marketMessage,
                                      CategoryMarketMessageData categoryData) {
        BallHeadValidator ballHeadValidator = BALLHEAD_VALIDATOR_MAP_HT.get(categoryData.categoryId);
        if (ballHeadValidator == null) {
            return true;
        }
        MatchMarketMessageData matchData = categoryData.matchData;
        if (validateTime(matchData,2400))
            return true;

        return ballHeadValidator.validate(ballHead, cacheScoreSum);
    }

    private boolean validateSecondHalf(BigDecimal ballHead,
                                       Integer cacheScoreSum,
                                       StandardMarketMessage marketMessage,
                                       CategoryMarketMessageData categoryData) {
        BallHeadValidator ballHeadValidator = BALLHEAD_VALIDATOR_MAP_FT.get(categoryData.categoryId);
        if (ballHeadValidator == null) {
            return true;
        }
        MatchMarketMessageData matchData = categoryData.matchData;
        if (validateTime(matchData,5100))
            return true;

        return ballHeadValidator.validate(ballHead, cacheScoreSum);
    }

    private boolean validateOverTimeFirstHalf(BigDecimal ballHead,
                                              Integer cacheScoreSum,
                                              StandardMarketMessage marketMessage,
                                              CategoryMarketMessageData categoryData) {
        BallHeadValidator ballHeadValidator = BALLHEAD_VALIDATOR_MAP_OVERTIME_HT.get(categoryData.categoryId);
        if (ballHeadValidator == null) {
            return true;
        }
        MatchMarketMessageData matchData = categoryData.matchData;
        if (validateTime(matchData,720+90*60))
            return true;

        return ballHeadValidator.validate(ballHead, cacheScoreSum);
    }

    private boolean validateOverTimeSecondHalf(BigDecimal ballHead,
                                               Integer cacheScoreSum,
                                               StandardMarketMessage marketMessage,
                                               CategoryMarketMessageData categoryData) {
        BallHeadValidator ballHeadValidator = BALLHEAD_VALIDATOR_MAP_OVERTIME_FT.get(categoryData.categoryId);
        if (ballHeadValidator == null) {
            return true;
        }
        MatchMarketMessageData matchData = categoryData.matchData;
        if (validateTime(matchData,1620+90*60))
            return true;

        return ballHeadValidator.validate(ballHead, cacheScoreSum);
    }

    private boolean validateTime(MatchMarketMessageData matchData,int validateSeconds) {
        MatchTime matchTime = footballTimeCacheService.get(matchData);
        if (matchTime == null || matchTime.matchPeriodId == null || matchTime.secondsMatchStart == null) {
            return true;
        }
        if (!Objects.equals(matchTime.matchPeriodId, matchData.matchPeriodEnum.value)) {
            log.error("linkId:{},matchId:{},match period not the same, cache:{}, matchPeriodId:{},",
                      matchData.linkId,
                      matchData.standardMatchInfo.getId(),
                      matchTime,
                      matchData.standardMatchInfo.getMatchPeriodId()
                     );
            return true;
        }
        if (matchTime.secondsMatchStart <= validateSeconds) {
            return true;
        }
        return false;
    }

}
