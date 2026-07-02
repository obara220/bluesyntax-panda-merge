package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.odds.model.StandardMatchCategoryRemovedDto;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_STANDARD_MATCH_CATEGORY_REMOVED;

@Component
@Slf4j
@Validated
public class StandardMatchCategoryRemovedProcessor extends BaseProcessor {

    @Autowired
    private RedisService redisService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    public void processor(Request<StandardMatchCategoryRemovedDto> request) {
        log.info("::{}::玩法中途下架", request);
        String linkId = request.getLinkId() + "_CATEGORY_REMOVED";
        try {
            StandardMatchCategoryRemovedDto data = request.getData();
            Set<Long> marketCategoryIds = data.getMarketCategoryIds();
            Long standardMatchId = data.getStandardMatchId();
            Integer marketType = data.getMarketType();
            if (CollectionUtils.isEmpty(marketCategoryIds)) {
                return;
            }
            //标准赛事不存在不处理
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (null == standardMatchInfo) {
                log.info("::{}::玩法中途下架,标准赛事不存在:{}", linkId, standardMatchId);
                return;
            }
            //查找开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
            if (null == standardSportMarketSell) {
                log.info("::{}::玩法中途下架，未找到预开售信息,标准赛事id={}", linkId, standardMatchId);
                return;
            }
            Map<String, Integer> categoryMap = marketCategoryIds.stream().collect(Collectors.toMap(Objects::toString, i -> data.getStatus()));
            redisService.hSetAll(RONGHE_STANDARD_MATCH_CATEGORY_REMOVED + standardMatchId + "_" + marketType, categoryMap, marketCacheTime(standardMatchInfo.getBeginTime()));
            log.info("::{}::玩法中途下架,缓存完成：{}", categoryMap);
            int oddsLive = isOddsLive(standardMatchId);
            if (oddsLive != marketType) {
                log.info("::{}::玩法中途下架,早盘不匹配不处理：{}", categoryMap, oddsLive, marketType);
                return;
            }
            //触发当前玩法关闭 获取缓存中的盘口数据
            Map<String, StandardMarketDataMessage> map = thirdMatchMarketProcessor.getStringStandardMarketDataMessageMap(marketCategoryIds, linkId, standardMatchInfo, standardSportMarketSell);
            if (CollectionUtils.isEmpty(map)) {
                log.info("::{}::玩法中途下架，当前赛事没有找到盘口,标准赛事id={}", linkId, standardMatchId);
                return;
            }
            thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(), request.getOddsSource(), request.getOperaterId(), standardMatchInfo, marketCategoryIds, map, TimeUtils.millsSecondsEast8ZoneGmt(), standardSportMarketSell, new HashMap<>());
        } catch (Exception e) {
            log.error("::" + linkId + "::玩法中途下架，出现异常", e);
        }
    }


    /**
     * 下架
     *
     * @param linkId
     * @param aoMatchId
     */
    public void marketRemovedClose(String linkId, Long matchId, List<StandardMarketMessage> standardMarketMessageAllList) {
        if (CollectionUtils.isEmpty(standardMarketMessageAllList)) {
            return;
        }
        Integer marketType = standardMarketMessageAllList.get(0).getMarketType();
        Map<String, Integer> categoryRemovedMap = redisService.hGetAll(RONGHE_STANDARD_MATCH_CATEGORY_REMOVED + matchId + "_" + marketType);
        if (MapUtil.isEmpty(categoryRemovedMap)) {
            return;
        }
        log.info("::{}::下架盘口,赛事id:{},categoryRemovedMap：{}", linkId, matchId, categoryRemovedMap);
        standardMarketMessageAllList.stream().forEach(standardMarketMessage -> {
            if (1 == categoryRemovedMap.getOrDefault(standardMarketMessage.getMarketCategoryId().toString(), 0)) {
                standardMarketMessage.setIsShelves(1);//下架标识
                standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketMessage.setRemark("操盘已下架");
            }
        });
    }
}
