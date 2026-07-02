package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 根据传入key值获取hash缓存，返回结果是hash对象
 * @author   tell
 * @since    2021年3月14日12:42:22
 */
@Slf4j
@Component
@JobHandler(value = "GetRedisCacheMapJob")
public class GetRedisCacheMapJob extends IJobHandler {

    @Autowired
    private RedisService redisService;

    /**
     * 根据传入key值获取缓存
     */
    @Override
    public ReturnT<String> execute(String param){
        log.info("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 处理开始,入参: {}",param);
        XxlJobLogger.log("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 处理开始,入参: {}",param);
        try {
            if(StringUtils.isNotBlank(param)){
                Map<String, String> parMap = JSON.parseObject(param, Map.class);
                //rediskey : 缓存map对象的key
                String rediskey = parMap.get("rediskey");
                //hashKey：map对象中的单个key
                String hashKey = parMap.get("hashKey");
                if(StringUtils.isNotBlank(hashKey)){
                    Object item = redisService.hGet(rediskey, hashKey);
                    log.info("【GetRedisCacheMapJob 根据传入hashKey获取缓存】 val：{}",item);
                    XxlJobLogger.log("【GetRedisCacheMapJob 根据传入hashKey获取缓存】 val：{}",item);
                }
                if(StringUtils.isNotBlank(rediskey)){
                    Map<Object, Object> resMap = redisService.hGetAll(rediskey);
                    log.info("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 key：{},总条数：{}",rediskey,resMap.size());
                    XxlJobLogger.log("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 key：{},总条数：{}",rediskey,resMap.size());
                    for (Object key: resMap.keySet()) {
                        log.info("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 key：{},val：{}",key,resMap.get(key));
                        XxlJobLogger.log("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 key：{},val：{}",key,resMap.get(key));
                    }
                }
                //hash集合中某一个小key
                String delHashKey = parMap.get("delHashKey");
                if(StringUtils.isNotBlank(delHashKey)){
                    redisService.del(delHashKey);
                    log.info("【CleanRedisCachJob 根据传入key值清除hash缓存】 成功清除单条hash缓存");
                    XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除hash缓存】 成功清除单条hash缓存");
                }
                //hash集合大Key
                String delRediskey = parMap.get("delRediskey");
                if(StringUtils.isNotBlank(delRediskey)){
                    redisService.del(delRediskey);
                    log.info("【CleanRedisCachJob 根据传入key值清除hash缓存】 成功清除hash集合缓存");
                    XxlJobLogger.log("【CleanRedisCachJob 根据传入key值清除hash缓存】 成功清除hash集合缓存");
                }
            }
        } catch (Exception e) {
            log.error("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 Exception:", e);
            XxlJobLogger.log("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 Exception:"+e.getMessage());
        }
        log.info("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 处理结束");
        XxlJobLogger.log("【GetRedisCacheMapJob 根据传入key值获取hash缓存】 处理结束");
        return ReturnT.SUCCESS;
    }


}
