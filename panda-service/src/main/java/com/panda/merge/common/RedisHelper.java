package com.panda.merge.common;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @description: Redis Helper
 * @author: Henry Wang
 * @create: 2024-03-02 19:13
 **/
@Component
public class RedisHelper<T, V> {
    public void postProcMget(List<T> keys, List<Object> values, List<V> existedResponse, List<T> requiredCallItems) {
        for (int i=0; i < values.size(); i++) {
            Object object = values.get(i);
            if(object == null) {
                requiredCallItems.add(keys.get(i));
            } else {
                V matchInfo = (V) object;
                existedResponse.add(matchInfo);
            }
        }
    }

    /**
     * 盘口id
     * @param keys 盘口、投注项 唯一标识
     * @param values 缓存获取数据
     * @param existedResponse  返回数据   Map<盘口唯一标识字符串,盘口id>
     * @param requiredCallItems 不存在数据
     */
    public void postMarketkeyProcMget(List<String> keys, List<Object> values, Map<String,String> existedResponse, List<String> requiredCallItems) {
        for (int i=0; i < values.size(); i++) {
            Object object = values.get(i);
            if(object == null) {
                requiredCallItems.add(keys.get(i));
            } else {
                existedResponse.put(keys.get(i),object.toString());
            }
        }
    }
}
