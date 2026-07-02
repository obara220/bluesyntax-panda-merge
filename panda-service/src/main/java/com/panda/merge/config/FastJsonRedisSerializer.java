package com.panda.merge.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * Value 序列化
 * 支持类名映射，用于处理包名迁移时的兼容性问题
 */
@Slf4j
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        
        // 在反序列化前，进行类名映射替换，以支持包名迁移
        // 如果 Redis 中存储的数据使用了旧的包名，这里会将其替换为新的包名
        try {
            str = ClassNameMappingUtil.replaceClassNameInJson(str);
        } catch (Exception e) {
            // 如果映射失败，记录日志但不影响反序列化流程
            log.warn("Failed to apply class name mapping during deserialization: {}", e.getMessage());
        }
        
        return (T) JSON.parseObject(str, clazz);
    }

}
