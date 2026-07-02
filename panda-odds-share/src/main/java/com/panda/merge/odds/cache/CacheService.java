package com.panda.merge.odds.cache;


/**
 * CacheService
 *
 * @description: 缓存服务
 * @date: 1/20/2025
 **/
public interface  CacheService {
    // 刷新缓存
    default void refresh() {
        refresh(null);
    }

    void refresh(String key);
}
