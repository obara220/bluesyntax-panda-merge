package com.panda.merge.odds.cache;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.ConfigMarketCategoryPlace;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MarketPlaceCacheService
 *
 * @description: 盘口坑位缓存服务
 * @date: 4/19/2025
 **/
@Service
@Slf4j
public class CategoryMarketPlaceCacheService {

    @Autowired
    private RedisService redisService;

    public Map<String, ConfigMarketCategoryPlace> getMap(StandardMatchInfo standardMatchInfo,
                                                         List<StandardMarketMessage> standardMarketMessages) {
        String cacheConfigMarketPlaceKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfo.getId();

        Set<String> categoryKeys = new HashSet<>();

        standardMarketMessages.forEach(standardMarketMessage -> {
            categoryKeys.add(standardMatchInfo.getId() + "_" + standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getPlaceNum());
            categoryKeys.add(standardMatchInfo.getId() + "_" + standardMarketMessage.getMarketCategoryId() + "_" + standardMarketMessage.getChildMarketCategoryId() + "_" + standardMarketMessage.getPlaceNum());
        });
        List<Object> values = redisService.hMulGetBasedBucket(cacheConfigMarketPlaceKey,
                                                              new ArrayList<>(categoryKeys),
                                                              ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
        return values
                .stream()
                .filter(Objects::nonNull)
                .map(value -> (ConfigMarketCategoryPlace) value)
                .collect(Collectors.toMap(categoryPlace -> categoryPlace.getStandardCategoryId() + "_" +
                                                  categoryPlace.getChildStandardCategoryId() + "_" + categoryPlace.getPlaceNum(),
                                          categoryPlace -> categoryPlace,
                                          (c1, c2) -> c1));
    }
}
