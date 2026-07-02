package com.panda.merge.dto.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LocalCacheRefreshMessage
 *
 * @description: 本地缓存消息
 * @date: 1/20/2025
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalCacheRefreshMessage {
    private String linkId;
    private String cacheServiceName;
    private String key;
    private String message;

}
