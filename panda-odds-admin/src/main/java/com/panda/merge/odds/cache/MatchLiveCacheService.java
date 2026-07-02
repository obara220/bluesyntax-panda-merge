package com.panda.merge.odds.cache;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.odds.model.MatchMarketMessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * MatchLiveCacheService
 *
 * @description:
 * @date: 4/14/2025
 **/
@Service
public class MatchLiveCacheService {

    @Autowired
    private RedisService redisService;

    /**
     * 查询缓存是否进入滚球
     *
     * @return 结果
     */
    public boolean isOddsLive(Long standardMatchInfoId) {
        Object marketTypeObj =
                redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfoId);
        return !Objects.isNull(marketTypeObj);
    }

    public int getMarketType(Long standardMatchInfoId) {
        Object marketTypeObj =
                redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfoId);
        return Objects.isNull(marketTypeObj) ? 1 : 0;
    }

    public int getMarketType(MatchMarketMessageData matchMarketMessageData) {
        if (matchMarketMessageData.marketType == null) {
            matchMarketMessageData.marketType = getMarketType(matchMarketMessageData.standardMatchInfo.getId());
        }
        return matchMarketMessageData.marketType;
    }

    public boolean isOddsLive(MatchMarketMessageData matchMarketMessageData) {
        int marketType = getMarketType(matchMarketMessageData);
        return marketType == 0;
    }

}
