package com.panda.merge.odds;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.MarketTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.odds.producer.XtsRecoverProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.panda.merge.odds.constants.CacheConstant.EXPIRE_THREE_DAY;
import static com.panda.merge.odds.constants.CategoryConstant.CATEGORY_SCORE_TYPE_MAP;

/**
 * XtsMonitor
 *
 * @description: xts异常自动切换pa恢复后通知风控
 * <p>
 * key   dss:xts:{matchId}:{marketType}
 * value xtsDataSource
 * @date: 7/12/2025
 **/

@Service
@Slf4j
public class XtsMonitor {

    private static Set<String> XTS_DATA_SOURCE_LIST = new HashSet<>(Arrays.asList("SR", "BG", "BC", "BE", "F01"));

    @Autowired
    private RedisService redisService;

    @Autowired
    private XtsRecoverProducer xtsRecoverProducer;

    public void set(StandardMatchInfo matchInfo, int marketType, String dataSource, String linkId) {
        log.info("LinkId:{}, set xtsMonitor matchId:{},marketType:{},dataSource:{}",
                 linkId,
                 matchInfo,
                 marketType,
                 dataSource);
        redisService.set(getKey(matchInfo.getId(), marketType), dataSource, getExpireSeconds(matchInfo, marketType));

    }

    public void monitor(Long uuid, List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOList) {
        if (CollectionUtils.isEmpty(thirdMarketDTOList)) {
            return;
        }
        List<String> keySet = thirdMarketDTOList
                .stream()
                .filter(this::filter)
                .map(this::getKey)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(keySet)) {
            return;
        }
        List<Object> xtsCaches = redisService.mGet(keySet);
        if (CollectionUtils.isEmpty(xtsCaches)) {
            return;
        }
        for (int i = 0; i < xtsCaches.size(); i++) {

            Object cache = xtsCaches.get(i);
            if (cache == null) {
                continue;
            }
            String key = keySet.get(i);
            if (redisService.del(key)) {
                String[] split = key.split(":");
                Long matchId = Long.valueOf(split[2]);
                int marketType = Integer.parseInt(split[3]);
                xtsRecoverProducer.send(uuid, matchId,marketType);
            }

        }
    }

    private boolean filter(OddsWrapper<ThirdMarketDTO> wrapper) {
        if (wrapper.getSportId() == null || wrapper.getSportId() != 1L) {
            return false;
        }
        if (!XTS_DATA_SOURCE_LIST.contains(wrapper.getDataSourceCode())) {
            return false;
        }
        return CATEGORY_SCORE_TYPE_MAP.containsKey(wrapper.getMarketCategoryId());

    }

    private String getKey(Long matchId, int marketType) {
        return String.format("dss:xts:%s:%s", matchId, marketType);
    }

    private String getKey(OddsWrapper<ThirdMarketDTO> wrapper) {
        return getKey(wrapper.getStandardSourceId(), wrapper.getMarketType());
    }

    private int getExpireSeconds(StandardMatchInfo matchInfo, int marketType) {
        if (marketType == MarketTypeEnum.LIVE.getCode()) {
            return EXPIRE_THREE_DAY;
        }
        //排除计算出小数情况
        return (int) Math.ceil(Math.abs(matchInfo.getBeginTime() - System.currentTimeMillis()) / 1000) + EXPIRE_THREE_DAY;
    }

}
