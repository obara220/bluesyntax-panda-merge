package com.panda.merge.odds.cache;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * AutoCloseCacheService
 *
 * @description: 自动关盘缓存
 *
 * @date: 4/20/2025
 **/
@Service
@Slf4j
public class AutoCloseCacheService {

    @Autowired
    private RedisService redisService;


    public Object get(Long matchId, Long categoryId) {
        String autoCloseRedisKey = DigestUtil.md5Hex(
                Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + matchId);
        return redisService.hGet(autoCloseRedisKey, String.valueOf(categoryId));

    }


    public boolean autoClose(Long matchId,Long categoryId) {
        Object cache = get(matchId, categoryId);
        return !Objects.isNull(cache);
    }







}
