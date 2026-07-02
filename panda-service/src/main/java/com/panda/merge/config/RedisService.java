package com.panda.merge.config;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * redis操作Service
 * Created by macro on 2020/3/3.
 */
public interface RedisService {

    /**
     * 根据命名空间获取命名空间下全部key值
     * */
    Set<String> keys(String key);

    /**
     * 批量删除key值
     * */
    Long delete(Set<String> keys);

    /**
     * 保存属性
     */
    void set(String key, Object value, long time);

    Boolean setIfGreater(String linkId,String thirdMarketSourdeId,String key, Long newValue, long expireTime);

    /**
     * 保存属性
     */
    void set(String key, Object value);

    /**
     * 保存属性 显式声明时间单位
     */
    void set(String key, Object value, long time, TimeUnit unit);

    Boolean setIfNotExist(String key, Object value,long time,TimeUnit unit);

    /**
     * 获取属性
     */
    Object get(String key);

    /**
     * 保存属性
     */
    void mSet(Map<String, Object> map);

    /**
     * 保存属性，设置失效期
     */
    void mSetExpire(Map<String, Object> map, Integer expire);

    /**
     * 获取属性
     */
    List<Object> mGet(List<String> keys);

    /**
     * 删除属性
     */
    Boolean del(String key);

    /**
     * 批量删除属性
     */
    Long del(List<String> keys);

    /**
     * 设置过期时间
     */
    Boolean expire(String key, long time);

    /**
     * 获取过期时间
     */
    Long getExpire(String key);

    /**
     * 判断是否有该属性
     */
    Boolean hasKey(String key);

    /**
     * 按delta递增
     */
    Long incr(String key, long delta);

    /**
     * 按delta递减
     */
    Long decr(String key, long delta);

    /**
     * 获取Hash结构中的属性
     */
    Object hGet(String key, String hashKey);

    /**
     * 向Hash结构中放入一个属性
     */
    Boolean hSet(String key, String hashKey, Object value, long time);

    /**
     * 获取 multiple hash
     */
    List<Object> hMulGet(String key, List<String> hashKey);

    /**
     * 向Hash结构中放入一个属性
     */
    void hSet(String key, String hashKey, Object value);

    void hSetSync(String key, String hashKey, Object value);

    /**
     * 直接获取整个Hash结构
     */
    <V,E> Map<V, E> hGetAll(String key);

    /**
     * 直接设置整个Hash结构
     */
    <E> Boolean hSetAll(String key, Map<String, E> map, long time);

    <E> void hSetAllSync(String key, Map<String, E> map, long time);

    /**
     * 直接设置整个Hash结构
     */
    void hSetAll(String key, Map<String, ?> map);

    /**
     * 删除Hash结构中的属性
     */
    void hDel(String key, Object... hashKey);

    /**
     * 判断Hash结构中是否有该属性
     */
    Boolean hHasKey(String key, String hashKey);

    /**
     * Hash结构中属性递增
     */
    Long hIncr(String key, String hashKey, Long delta);

    /**
     * Hash结构中属性递减
     */
    Long hDecr(String key, String hashKey, Long delta);

    /**
     * 获取Set结构
     */
    Set<Object> sMembers(String key);

    /**
     * 向Set结构中添加属性
     */
    Long sAdd(String key, Object... values);

    /**
     * 向Set结构中添加属性
     */
    Long sAdd(String key, long time, Object... values);

    /**
     * 是否为Set中的属性
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 获取Set结构的长度
     */
    Long sSize(String key);

    /**
     * 删除Set结构中的属性
     */
    Long sRemove(String key, Object... values);

    /**
     * 获取List结构中的属性
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取List结构的长度
     */
    Long lSize(String key);

    /**
     * 根据索引获取List中的属性
     */
    Object lIndex(String key, long index);

    /**
     * 向List结构中添加属性
     */
    Long lPush(String key, Object value);

    /**
     * 向List结构中添加属性
     */
    Long lPush(String key, Object value, long time);

    /**
     * 向List结构中批量添加属性
     */
    Long lPushAll(String key, Object... values);

    /**
     * 向List结构中批量添加属性
     */
    Long lPushAll(String key, Long time, Object... values);

    /**
     * 从List结构中移除属性
     */
    Long lRemove(String key, long count, Object value);

    /**基于redis的set操作实现加锁
     * @param  key
     * @param  hashValue 锁ID（可以理解成自己随机生成的,用于标识,一定是唯一不重复的，防止被竞争者删除）
     *                      当前redis锁时谁占有的
     * @param  expireTime key 失效时间 (秒)
     * @param  timeOut 重试时间 (秒)
     * @return
     */
    boolean tryLock(String key, String hashValue, int expireTime, int timeOut);

    /**
     * 基于reids lua脚本实现原子
     * @param key redis 锁key
     * @param hashValue 当前key与value相对应一致
     * @return
     */
    boolean unLock(String key, String hashValue);



    /**基于redis的set操作实现加锁一次
     * @param  key
     * @param  hashValue 锁ID（可以理解成自己随机生成的,用于标识,一定是唯一不重复的，防止被竞争者删除）
     *                      当前redis锁时谁占有的
     * @param  expireTime key 失效时间 (秒)
     * @return
     */
    boolean tryLockOnce(String key, String hashValue, int expireTime);

    /**
     * 保存属性--无过期时间
     */
    void setLongTime(String key, Object value);

    /**
     * 对于大key，进行拆分，生成新key
     */
    String genNewHashKey(String key, String fieldKey, Integer bucketQuantity);

    Map<String, String> genNewHashKeys(String key, List<String> fieldKeys, Integer bucketQuantity);

    /**
     * 已知所处bucket，生成新key
     */
    String genNewHashKeyWithSplitedBucket(String key, Integer bucketNum);

    String genNewHashKeyWithSplitedBucketOld(String key, Integer bucketNum);

    /**基于桶(对于大key)，获取所有key的所有数据
     * @param  key redis 主key
     * @param  bucketQuantity 桶的数量
     * @return 返回key的所有数据，其类型为map类型
     */
    <V, E> Map<V, E> hGetAllBasedBucket(String key, Integer bucketQuantity);

    <V, E> Map<V, E> hGetAllBasedBucketOld(String key, Integer bucketQuantity);

    List<Object> hMulGetBasedBucket(String key, List<String> hashKey, Integer bucketQuantity);


    /**对于大key，进行拆分存储
     * @param  key redis 主key
     * @param  bucketQuantity 桶的数量
     * @param  storeData 存储的数据
     */
    <E> void hSetAllBasedBucket(String key, Integer bucketQuantity, Map<String, E> storeData);

    /**对于大key，进行拆分存储 with expire time
     * @param  key redis 主key
     * @param  bucketQuantity 桶的数量
     * @param  storeData 存储的数据
     * @param  expireTime 过期时间
     */
    <E> void hSetAllBasedBucket(String key, Integer bucketQuantity, Map<String, E> storeData, long expireTime);

    /**
     * 对于hashkey数据类型，基于getAll，想同时获取多个key
     * @param keys
     * @return
     */
    Map<String, Object> syncObtainMultiGetAll(List<String> keys);

    <T> Map<String, T> syncOddsMultiGetAll(List<String> keys);

    Map<String, Map<String, Object>> syncObtainMultiGetAllWithoutMerge(List<String> keys);

    /**
     * 获取多个hash 返回 nested Map
     * 使用场景：
     *   1. 多玩法id 查询标准盘口缓存
     *
     * @param ids id列表 例如 玩法id
     * @param keyMapper hash key映射
     * @return nested Map
     * @param <K1> id
     * @param <V> cache value
     */
    <K1,V> Map<K1, Map<String, V>> syncObtainMultiGetAllWithoutMerge(Collection<K1> ids, Function<K1,String> keyMapper);


    void batchSetWithExpire(Map<String, Object> map, Long seconds);

    void batchHsetWithExpire(String matchBeginKey,Map<String, Object> map);
}
