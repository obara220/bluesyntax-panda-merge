package com.panda.merge.config;

import com.google.common.hash.Hashing;
import com.panda.merge.constant.ConstantSystem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * redis操作实现类
 * Created by macro on 2020/3/3.
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Lazy
    @Resource(name = "CallRedisThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Lazy
    @Resource(name = "CallOddsRedisThreadPool")
    private ThreadPoolTaskExecutor oddsThreadPoolTaskExecutor;

    @Resource
    private FastJsonRedisSerializer fastJsonRedisSerializer;

    @Resource
    StringRedisSerializer stringRedisSerializer;

    Random random = new Random();

    /**
     * 加锁脚本
     */
    String SRIPT_LOCK = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end else return 0 end";
    /**
     * 解锁脚本
     */
    String SRIPT_UNLOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 插入三方盘口时间戳
     */
    String THIRD_MARKET_TIME_SCRIPT =
            "local v = redis.call('get', KEYS[1]) " +
                    // 增加类型检查：只有当 v 是数字字符串时才转换，否则视为旧数据直接覆盖
                    "local old_v = tonumber(v) " +
                    "local new_v = tonumber(ARGV[1]) " +
                    "if not old_v or new_v >= old_v then " +
                    "    redis.call('setex', KEYS[1], ARGV[2], ARGV[1]) " +
                    "    return 'SUCCESS' " +
                    "else " +
                    "    return v " + // 失败返回旧值
                    "end";

    private static final Long SUCCESS = 1L;

    @Override
    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    @Override
    public Long delete(Set<String> keys) {
        return redisTemplate.delete(keys);
    }


    @Override
    public void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    @Override
    public Boolean setIfGreater(String linkId,String thirdMarketSourceId,String key, Long newValue, long expireTime) {
        try {
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(THIRD_MARKET_TIME_SCRIPT, String.class);
            String result = (String)redisTemplate.execute(
                    redisScript,
                    redisTemplate.getStringSerializer(), // Key 序列化
                    redisTemplate.getStringSerializer(), // Value 序列化（关键：确保 ARGV 是纯字符串）
                    Collections.singletonList(key),
                    String.valueOf(newValue),     // ARGV[1]: 新时间戳
                    String.valueOf(expireTime)
            );
            // 判断逻辑
            if ("SUCCESS".equals(result)) {
                return true;
            } else {
                // 插入失败，打印新旧时间戳对比（result 即为拿到的旧值）
                log.info("::{}::Redis时间戳校验未通过 - thirdMarketSourceId: {}, 现有旧值: {}, 尝试写入的新值: {}",
                        linkId,thirdMarketSourceId, result, newValue);
                return false;
            }
        } catch (Exception e) {
            log.error("::{}::Redis setIfGreater error, key: {}", linkId,key, e);
            return false;
        }
    }

    @Override
    public void set(String key, Object value) {
        //默认过期时间1天
        redisTemplate.opsForValue().set(key, value, RedisConfig.REDIS_DEFAULT_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value, long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, time, unit);
    }

    @Override
    public Boolean setIfNotExist(String key, Object value, long time, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, unit);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void mSet(Map<String, Object> map) {
        //redisTemplate.opsForValue().multiSet(map);
        batchSetWithExpire(map, RedisConfig.REDIS_DEFAULT_TIME.longValue());
    }

    @Override
    public void mSetExpire(Map<String, Object> map, Integer expire) {
        batchSetWithExpire(map, Long.valueOf(expire));
    }

    @Override
    public List<Object> mGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time);
    }

    @Override
    public List<Object> hMulGet(String key, List<String> hashKey) {
        return redisTemplate.opsForHash().multiGet(key, hashKey);
    }

    @Override
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public void hSetSync(String key, String hashKey, Object value) {
        threadPoolTaskExecutor.execute(() -> hSet(key, hashKey, value));
    }

    @Override
    public <V,E> Map<V, E> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public <E>Boolean hSetAll(String key, Map<String, E> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time);
    }

    @Override
    public <E> void hSetAllSync(String key, Map<String, E> map, long time) {
        threadPoolTaskExecutor.execute(() -> hSetAll(key, map, time));
    }

    @Override
    public void hSetAll(String key, Map<String, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    @Override
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long sAdd(String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long lPush(String key, Object value, long time) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return index;
    }

    @Override
    public Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public Long lPushAll(String key, Long time, Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * @param key  需要加锁的对象，如赛事id
     * @param hashValue  锁ID（可以理解成自己随机生成的,用于标识）
     *                   当前redis锁时谁占有的,如linkId
     * @param expireTime key 失效时间 (秒)
     * @param timeOut    重试时间 (秒)
     * @return
     */
    @Override
    public boolean tryLock(String key, String hashValue, int expireTime, int timeOut) {
        //log.info("开始尝试获取锁，key：{},hashValue:{}",key,hashValue);
        Long waitMax = TimeUnit.SECONDS.toMillis(timeOut);
        Long waitAlready = 0L;
        // 尝试操作
        boolean succeed = false;
        while(waitAlready <= waitMax) {
            succeed = lock(key,hashValue,expireTime);
            if (succeed) {
                break;
            }
            try {
                //如果一个Client无法获得锁，它将在一个随机延时后开始重试。
                // 使用随机延时的目的是为了与其他申请同一个锁的Client错开申请时间，减少脑裂(split brain)发生的可能性
                int randomTime = random.nextInt(10) + 2;
                Thread.sleep(randomTime);
                waitAlready += randomTime;
            } catch (Exception e) {
                log.info(String.format("分布式锁创建失败, key: %s value: %s", key, hashValue), e);
            }
        }
        if (!succeed)
        {
            log.info(String.format("获取分布式锁返回false, key: %s value: %s", key, hashValue));
        }
        return succeed;
    }

    /**
     * @param key   需要释放锁的对象，如赛事id
     * @param hashValue 锁ID（可以理解成自己随机生成的,用于标识）
     *                         当前redis锁时谁占有的,如linkId
     * @return
     */
    @Override
    public boolean unLock(String key, String hashValue) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SRIPT_UNLOCK, Long.class);
        Long result = (Long)redisTemplate.execute(redisScript, Collections.singletonList(key),hashValue);
        return SUCCESS.equals(result);
    }

    @Override
    public boolean tryLockOnce(String key, String hashValue, int expireTime) {
        return lock(key,hashValue,expireTime);
    }

    /**
     * @param key     锁的键，通常是 Redis 中的某个唯一标识
     * @param lockId  锁的标识符，通常是某个特定的客户端或线程 ID
     * @param expireTime 锁的过期时间，防止死锁
     * */
    private boolean lock(String key,String lockId,int expireTime) {
        try{
            // 创建一个 DefaultRedisScript 实例
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            // 设置 Lua 脚本，这个脚本应该是用于获取锁的脚本
            redisScript.setScriptText(SRIPT_LOCK);
            // 设置脚本执行的返回类型
            redisScript.setResultType(Long.class);
            // 执行脚本，尝试获取锁
            Long result = (Long)redisTemplate.execute(redisScript, Collections.singletonList(key),lockId,expireTime);
            // 判断返回值，成功获取锁时返回 true
            return SUCCESS.equals(result);
        }catch(Exception e){
            log.error("创建redis锁时发生异常",e);
        }
        // 如果异常或者获取锁失败，返回 false
        return false;
    }

    @Override
    public void setLongTime(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String genNewHashKey(String key, String fieldKey, Integer bucketQuantity) {
        if(key == null || fieldKey == null || bucketQuantity < 1) {
            log.warn("Input params are not valid");
            return "";
        }
        long bucketNum = (Hashing.crc32c().hashBytes(fieldKey.getBytes()).asInt() & 0xFFFFFFFL) % bucketQuantity;
        return key + ":" + bucketNum;
    }

    @Override
    public Map<String, String> genNewHashKeys(String key, List<String> fieldKeys, Integer bucketQuantity) {
        if(key == null || CollectionUtils.isEmpty(fieldKeys) || bucketQuantity < 1) {
            log.warn("Input params are not valid");
            return Collections.emptyMap();
        }
        return fieldKeys.stream().collect(Collectors.toMap(t->t, fieldKey->{
            long bucketNum = (Hashing.crc32c().hashBytes(fieldKey.getBytes()).asInt() & 0xFFFFFFFL) % bucketQuantity;
            return key + ":" + bucketNum;
        }, (v1, v2) -> v1));
    }

    @Override
    public String genNewHashKeyWithSplitedBucket(String key, Integer bucketNum) {
        if(key == null || bucketNum < 0) {
            log.warn("Input params are not valid");
            return "";
        }
        return key + ":" + bucketNum;
    }

    @Override
    public String genNewHashKeyWithSplitedBucketOld(String key, Integer bucketNum) {
        if(key == null || bucketNum < 0) {
            log.warn("Input params are not valid");
            return "";
        }
        long newKey = (Hashing.crc32c().hashBytes(key.getBytes()).asInt() & 0xFFFFFFFL) + bucketNum;
        return String.valueOf(newKey);
    }

    @Override
    public <V, E> Map<V, E> hGetAllBasedBucket(String key, Integer bucketQuantity) {
        Map<V, E> result = new HashMap<>();
        try {
            List<Future<Map<V, E>>> futureList = new ArrayList<>();
            for (int i = 0; i < bucketQuantity; i++) {
                String updatedKey = genNewHashKeyWithSplitedBucket(key, i);
                Future<Map<V, E>> future = threadPoolTaskExecutor.submit(() -> hGetAll(updatedKey));
                futureList.add(future);
            }
            for (Future<Map<V, E>> future : futureList) {
                Map<V, E> subMatchMap = Optional.ofNullable(future.get()).orElse(Collections.emptyMap());
                result.putAll(subMatchMap);
            }
        } catch (Exception e) {
            log.error("[RedisServiceImpl] hGetAllBasedBucket key:{} call error: ",key, e);
        }
        return result;
    }

    @Override
    public <V, E> Map<V, E> hGetAllBasedBucketOld(String key, Integer bucketQuantity) {
        Map<V, E> result = new HashMap<>();
        try {
            List<Future<Map<V, E>>> futureList = new ArrayList<>();
            for (int i = 0; i < bucketQuantity; i++) {
                String updatedKey = genNewHashKeyWithSplitedBucketOld(key, i);
                Future<Map<V, E>> future = threadPoolTaskExecutor.submit(() -> hGetAll(updatedKey));
                futureList.add(future);
            }
            for (Future<Map<V, E>> future : futureList) {
                Map<V, E> subMatchMap = Optional.ofNullable(future.get()).orElse(Collections.emptyMap());
                result.putAll(subMatchMap);
            }
        } catch (Exception e) {
            log.error("[RedisServiceImpl] hGetAllBasedBucket key:{} call error: ",key, e);
        }
        return result;
    }

    @Override
    public List<Object> hMulGetBasedBucket(String key, List<String> hashKey, Integer bucketQuantity) {
        if(key == null || CollectionUtils.isEmpty(hashKey) || bucketQuantity < 1) {
            log.warn("[hMulGetBasedBucket] Input params are not valid");
            return Collections.emptyList();
        }
        Map<String, String> updatedKeysMap = genNewHashKeys(key, hashKey, bucketQuantity);
        Map<String, List<String>> updatedKeysByGrouup = updatedKeysMap.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue,
                Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        List<Pair<String, Future<List<Object>>>> futureList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : updatedKeysByGrouup.entrySet()) {
            Future<List<Object>> future = threadPoolTaskExecutor.submit(()-> hMulGet(entry.getKey(), entry.getValue()));
            futureList.add(Pair.of(entry.getKey(), future));
        }
        Map<String, Object> combinedResultMap = new HashMap<>();
        for (Pair<String, Future<List<Object>>> pair : futureList) {
            String redisKey = pair.getLeft();
            List<String> originalFields = updatedKeysByGrouup.get(redisKey);
            try {
                List<Object> subMatchList = Optional.ofNullable(pair.getRight().get()).orElse(Collections.emptyList());
                if(subMatchList.size() != originalFields.size()){
                    log.error("[phMulGetBasedBucket]从redis中获取fields{}失败!", originalFields);
                    continue;
                }
                for(int i = 0; i < subMatchList.size(); i++) {
                    combinedResultMap.put(originalFields.get(i), subMatchList.get(i));
                }
            } catch (Exception e) {
                log.error("[RedisServiceImpl] syncObtainMultiGetAll redis key: {} hashKey:{} redisKey:{} call failure with error: ",
                        key, hashKey, redisKey, e);
            }
        }
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < hashKey.size(); i++) {
            result.add(combinedResultMap.getOrDefault(hashKey.get(i), null));
        }
        return result;
    }

    @Override
    public <E> void hSetAllBasedBucket(String key, Integer bucketQuantity, Map<String, E> storeData) {
        hSetAllBasedBucket(key, bucketQuantity, storeData, 0);
    }

    @Override
    public <E> void hSetAllBasedBucket(String key, Integer bucketQuantity, Map<String, E> storeData, long expireTime) {
        Map<String, Map<String, E>> updatedOddsMap = storeData.entrySet().stream().collect(
                Collectors.groupingBy(entry -> genNewHashKey(key, entry.getKey(), bucketQuantity),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue)->newValue)));

        if (expireTime == 0) {
            for (Map.Entry<String, Map<String, E>> entry: updatedOddsMap.entrySet()) {
                threadPoolTaskExecutor.execute(() -> hSetAll(entry.getKey(), entry.getValue()));
            }
        } else {
            for (Map.Entry<String, Map<String, E>> entry: updatedOddsMap.entrySet()) {
                threadPoolTaskExecutor.execute(() -> hSetAll(entry.getKey(), entry.getValue(), expireTime));
            }
        }
    }

    @Override
    public Map<String, Object> syncObtainMultiGetAll(List<String> keys) {
        Map<String, Object> result = new HashMap<>();
        List<Pair<String, Future<Map<String, String>>>> futureList = new ArrayList<>();
        for (String key : keys) {
            Future<Map<String, String>> future = threadPoolTaskExecutor.submit(()-> hGetAll(key));
            futureList.add(Pair.of(key, future));
        }
        for (Pair<String, Future<Map<String, String>>> pair : futureList) {
            String redisKey = pair.getLeft();
            try {
                Map<String, String> subMatchMap = Optional.ofNullable(pair.getRight().get()).orElse(Collections.emptyMap());
                subMatchMap.entrySet().forEach(t->result.put(redisKey+"-"+t.getKey(), t.getValue()));
            } catch (Exception e) {
                log.error("[RedisServiceImpl] syncObtainMultiGetAll redis keys: {},key: {} call failure with error: ", keys, redisKey, e);
            }
        }
        return result;
    }


    @Override
    public <T> Map<String, T> syncOddsMultiGetAll(List<String> keys) {
        Map<String, T> result = new HashMap<>();
        List<Pair<String, Future<Map<String, T>>>> futureList = new ArrayList<>();
        for (String key : keys) {
            Future<Map<String, T>> future = oddsThreadPoolTaskExecutor.submit(()-> hGetAll(key));
            futureList.add(Pair.of(key, future));
        }
        for (Pair<String, Future<Map<String, T>>> pair : futureList) {
            String redisKey = pair.getLeft();
            try {
                Map<String, T> subMatchMap = Optional.ofNullable(pair.getRight().get()).orElse(Collections.emptyMap());
                result.putAll(subMatchMap);
            } catch (Exception e) {
                log.error("[RedisServiceImpl] syncOddsMultiGetAll redis keys: {}, key: {} call failure with error:", keys ,redisKey, e);
            }
        }
        return result;
    }

    @Override
    public Map<String, Map<String, Object>> syncObtainMultiGetAllWithoutMerge(List<String> keys) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        List<Pair<String, Future<Map<String, Object>>>> futureList = new ArrayList<>();
        for (String key : keys) {
            Future<Map<String, Object>> future = threadPoolTaskExecutor.submit(()-> hGetAll(key));
            futureList.add(Pair.of(key, future));
        }
        for (Pair<String, Future<Map<String, Object>>> pair : futureList) {
            String redisKey = pair.getLeft();
            try {
                Map<String, Object> subMatchMap = Optional.ofNullable(pair.getRight().get()).orElse(Collections.emptyMap());
                result.put(redisKey, subMatchMap);
            } catch (Exception e) {
                log.error("[RedisServiceImpl] syncObtainMultiGetAll redis keys:{} ,key: {} call failure with error: ", keys, redisKey, e);
            }
        }
        return result;
    }

    @Override
    public <K1, V> Map<K1, Map<String, V>> syncObtainMultiGetAllWithoutMerge(Collection<K1> ids,
                                                                             Function<K1, String> keyMapper) {
        Map<K1, Map<String, V>> result = new HashMap<>();
        List<Pair<K1, Future<Map<String, V>>>> futureList = new ArrayList<>();
        for (K1 id : ids) {
            String redisKey = keyMapper.apply(id);
            Future<Map<String, V>> future = threadPoolTaskExecutor.submit(() -> hGetAll(redisKey));
            futureList.add(Pair.of(id, future));
        }
        for (Pair<K1, Future<Map<String, V>>> pair : futureList) {
            K1 id = pair.getLeft();
            try {
                Map<String, V> subMatchMap = Optional.ofNullable(pair.getRight().get()).orElse(Collections.emptyMap());
                result.put(id, subMatchMap);
            } catch (Exception e) {
                log.error("[RedisServiceImpl] syncObtainMultiGetAll redis keys:{} ,key: {} call failure with error: ",
                          ids,
                          id,
                          e);
            }
        }
        return result;
    }

    @Override
    public void batchSetWithExpire(Map<String, Object> map, Long seconds) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                map.forEach((key, value) -> {
                    redisConnection.set(stringRedisSerializer.serialize(key), fastJsonRedisSerializer.serialize(value), Expiration.seconds(seconds), RedisStringCommands.SetOption.UPSERT);
                });
                return null;
            }
        }, fastJsonRedisSerializer);
    }

    @Override
    public void batchHsetWithExpire(String matchBeginKey,Map<String, Object> map) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                // 批量写入 Hash 字段
                map.forEach((key, value) -> {
                    String updatedKey = genNewHashKey(matchBeginKey, key, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                    byte[] serializedField = stringRedisSerializer.serialize(key);
                    byte[] serializedValue = fastJsonRedisSerializer.serialize(value);
                    connection.hSet(stringRedisSerializer.serialize(updatedKey), serializedField, serializedValue);
                    expire(updatedKey,RedisConfig.REDIS_DEFAULT_TIME.longValue());
                });
                return null;
            }
        }, fastJsonRedisSerializer);
    }

}
