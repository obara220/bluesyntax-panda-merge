package com.panda.merge.odds.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * DataSourceTimeService
 *
 * @description: 数据源时间戳校验
 * @date: 3/23/2025
 **/
@Slf4j
@Service
public class NumberCacheService {

    // 更新较大值
    private static final String SCRIPT = "local current = redis.call('get', KEYS[1]) " +
            "if current and tonumber(current) >= tonumber(ARGV[1]) then " + "   return current " + "else " +
            "   redis.call('set', KEYS[1], ARGV[1], 'ex', ARGV[2]) " +
            "   return ARGV[1] " + "end";



    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public boolean validate(String key, Long timeStamp, Integer expireSeconds) {
        Long result = update(key, timeStamp, expireSeconds);
        return timeStamp.equals(result);
    }

    public Long update(String key, Long timeStamp, Integer expireSeconds) {
        if (timeStamp == null || expireSeconds == null || expireSeconds <= 0) {
            return null;
        }
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(SCRIPT, String.class);
        return doUpdate(redisScript, key, timeStamp, expireSeconds);
    }



    private Long doUpdate(RedisScript<String> script, String key, Long timeStamp, Integer expireSeconds) {

        String result = redisTemplate.execute(script,
                                              RedisSerializer.string(),
                                              RedisSerializer.string(),
                                              Collections.singletonList(key),
                                              String.valueOf(timeStamp),
                                              String.valueOf(expireSeconds));
        return Long.valueOf(result);
    }



}
