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

import java.util.Set;

import static com.panda.merge.constant.ConstantSystem.XIN;

/**
 * 根据传入key值获取普通缓存，返回结果是单个对象信息
 * @author   tell
 * @since    2020年11月1日09:40:50
 */
@Slf4j
@Component
@JobHandler(value = "GetRedisCachJob")
public class GetRedisCacheJob extends IJobHandler {

    @Autowired
    private RedisService redisService;

    /**
     * 根据传入key值获取缓存
     */
    @Override
    public ReturnT<String> execute(String parKey){
        log.info("【GetRedisCacheJob 根据传入key值获取缓存】 处理开始,入参: {}",parKey);
        XxlJobLogger.log("【GetRedisCacheJob 根据传入key值获取缓存】 处理开始,入参: {}",parKey);
        try {
            if(StringUtils.isNotBlank(parKey)){
                if(parKey.contains(XIN)){
                    Set<String> keys = redisService.keys(parKey);
                    log.info("【GetRedisCacheJob 根据传入key值获取缓存】 根据传入key获取到缓存个数：{}",keys.size());
                    XxlJobLogger.log("【GetRedisCacheJob 根据传入key值获取缓存】 根据传入key获取到缓存个数：{}",keys.size());
                    keys.forEach(key->{
                        Object obj = redisService.get(key);
                        String jsonStr = null;
                        if(null != obj){
                            jsonStr = JSON.toJSONString(obj);
                        }
                        log.info("【GetRedisCacheJob 根据传入key值获取缓存】 key：{},val：{}",key,jsonStr);
                        XxlJobLogger.log("【GetRedisCacheJob 根据传入key值获取缓存】 key：{},val：{}",key,jsonStr);
                    });
                }else{
                    Object obj = redisService.get(parKey);
                    String jsonStr = null;
                    if(null != obj){
                        jsonStr = JSON.toJSONString(obj);
                    }
                    log.info("【GetRedisCacheJob 根据传入key值获取缓存】 key：{},val：{}",parKey,jsonStr);
                    XxlJobLogger.log("【GetRedisCacheJob 根据传入key值获取缓存】 key：{},val：{}",parKey,jsonStr);
                }
            }
        } catch (Exception e) {
            log.error("【GetRedisCacheJob 根据传入key值获取缓存】 Exception:", e);
            XxlJobLogger.log("【GetRedisCacheJob 根据传入key值获取缓存】 Exception:"+e.getMessage());
        }
        log.info("【GetRedisCacheJob 根据传入key值获取缓存】 处理结束");
        XxlJobLogger.log("【GetRedisCacheJob 根据传入key值获取缓存】 处理结束");
        return ReturnT.SUCCESS;
    }


}
