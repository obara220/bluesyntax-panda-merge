package com.panda.merge.service.impl;


import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ServiceImpl的公告父类
 * @author     tell
 * @since      2020年9月4日20:19:27
 * */
@Service
public class BaseServiceImpl<T> {

    @Autowired
    public RedisService redisService;


    /**
     * 刷新Hash缓存
     * @param key       命名空间
     * @param hashKey   map的key值
     * @param item      实体类对象
     * */
    public T refreshHashCache(String key,String hashKey,T item){
        //获取缓存
        Object obj = redisService.get(key);
        //只有缓存中存在才能刷新
        if(!Objects.isNull(obj)){
            Map<String, T> unique2Item = (Map<String, T>) obj;
            //缓存设最新的值
            unique2Item.put(hashKey,item);
            //刷新缓存
            redisService.set(key,unique2Item, RedisConfig.REDIS_MY_TIME);
        }
        return item;
    }

}
