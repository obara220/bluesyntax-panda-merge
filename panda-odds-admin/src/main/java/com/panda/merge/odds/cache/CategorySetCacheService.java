package com.panda.merge.odds.cache;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * CategorySetCacheService
 *
 * @description: 玩法集状态缓存服务
 *
 * @date: 4/19/2025
 **/
@Service
@Slf4j
public class CategorySetCacheService {

    @Autowired
    private RedisService redisService;

    public Map<String, Integer> get(Long matchId) {
        //玩法集玩法状态
        String redisCategorySetKey = getKey(matchId);
        return redisService.hGetAll(redisCategorySetKey);
    }

    private String getKey(Long matchId) {
        return DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_SET_STATUS + matchId);
    }

}
