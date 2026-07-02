package com.panda.merge.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * 数据源维护时间MQ消费者
 * 消费数据源维护时间消息，写入Redis（30天）
 * @author system
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "DATA_SOURCE_MAINTENANCE_NOTICE", consumerGroup = "settle-group-DATA_SOURCE_MAINTENANCE_TIME", consumeTimeout = 10000L)
@DependsOn("settleScoresAdminApplication")
public class DataSourceMaintenanceTimeConsumer implements RocketMQListener<DataSourceMaintenanceTimeConsumer.DataSourceMaintenanceTimeMessage> {
    
    @Autowired
    private RedisService redisService;
    
    /**
     * 数据源名称映射：将前端传入的数据源名称映射到实际的数据源代码
     * 前端传入的代码: S01, L01, T01, G01, B02, L02, N01, N02, N03
     * 实际代码: N01, N02, N03, BG, TX, RB, LS, KO, F01, PD
     *
     */
    private static final java.util.Map<String, String> DATA_SOURCE_CODE_MAPPING = new java.util.HashMap<String, String>() {{
        put("S01", "SR");
        put("L01", "LS");
        put("T01", "TX");
        put("G01", "BG");
        put("B02", "BC");
        put("R01", "RB");
        put("K01", "KO");
    }};
    
    @Override
    public void onMessage(DataSourceMaintenanceTimeMessage message) {
        String linkId = message.getLinkId();
        
        log.info("linkId::{}::DataSourceMaintenanceTimeConsumer收到数据源维护时间消息, enableSwitch:{}, dataSourceCode:{}, beginTime:{}, endTime:{}", 
                linkId, message.getEnableSwitch(), message.getDataSourceCode(), message.getBeginTime(), message.getEndTime());
        
        try {
            if (message == null || message.getDataSourceCode() == null) {
                log.warn("linkId::{}::数据源维护时间消息数据为空", linkId);
                return;
            }
            
            // 映射数据源名称到实际的数据源代码
            String actualDataSourceCode = mapDataSourceCode(message.getDataSourceCode());
            log.info("linkId::{}::数据源名称映射: {} -> {}", linkId, message.getDataSourceCode(), actualDataSourceCode);
            
            String maintenanceKey = CommonConstant.DATASOURCE_MAINTENANCE_TIME + actualDataSourceCode;
            
            // 根据enableSwitch来设置或移除维护时间
            Integer enableSwitch = message.getEnableSwitch();
            if (enableSwitch != null && enableSwitch == 1) {
                // enableSwitch = 1：设置维护时间
                if (message.getBeginTime() == null || message.getEndTime() == null) {
                    log.warn("linkId::{}::设置维护时间时，beginTime或endTime为空, beginTime:{}, endTime:{}", 
                            linkId, message.getBeginTime(), message.getEndTime());
                    return;
                }
                
                // Redis存储格式: "startTime,endTime"
                String value = message.getBeginTime() + "," + message.getEndTime();
                
                // 写入Redis，设置过期时间为30天
                redisService.set(maintenanceKey, value, RedisConfig.REDIS_MONTH_TIME);
                
                log.info("linkId::{}::数据源维护时间写入Redis成功, key:{}, value:{}", linkId, maintenanceKey, value);
            } else if (enableSwitch != null && enableSwitch == 0) {
                // enableSwitch = 0：移除维护时间
                redisService.del(maintenanceKey);
                
                log.info("linkId::{}::数据源维护时间从Redis移除成功, key:{}", linkId, maintenanceKey);
            } else {
                log.warn("linkId::{}::enableSwitch值无效: {}, 跳过处理", linkId, enableSwitch);
            }
        } catch (Exception e) {
            log.error("linkId::{}::处理数据源维护时间消息失败, message:{}", linkId, JSON.toJSONString(message), e);
        }
    }
    
    /**
     * 映射数据源名称到实际的数据源代码
     * @param dataSourceCode 前端传入的数据源名称
     * @return 实际的数据源代码
     */
    private String mapDataSourceCode(String dataSourceCode) {
        // 如果映射表中存在，则使用映射后的值
        if (DATA_SOURCE_CODE_MAPPING.containsKey(dataSourceCode)) {
            return DATA_SOURCE_CODE_MAPPING.get(dataSourceCode);
        }
        // 否则直接使用原值（如N01, N02, N03等）
        return dataSourceCode;
    }
    
    /**
     * 数据源维护时间消息体
     */
    public static class DataSourceMaintenanceTimeMessage {
        private String linkId;
        private Integer enableSwitch;  // 1=设置维护时间，0=移除维护时间
        private String dataSourceCode;  // 数据源名称（可能是S01, L01等，需要映射）
        private Long beginTime;         // 维护开始时间（enableSwitch=1时必填）
        private Long endTime;           // 维护结束时间（enableSwitch=1时必填）
        
        public Integer getEnableSwitch() {
            return enableSwitch;
        }
        
        public void setEnableSwitch(Integer enableSwitch) {
            this.enableSwitch = enableSwitch;
        }
        
        public String getDataSourceCode() {
            return dataSourceCode;
        }
        
        public void setDataSourceCode(String dataSourceCode) {
            this.dataSourceCode = dataSourceCode;
        }
        
        public Long getBeginTime() {
            return beginTime;
        }
        
        public void setBeginTime(Long beginTime) {
            this.beginTime = beginTime;
        }
        
        public Long getEndTime() {
            return endTime;
        }
        
        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        public String getLinkId() {
            return linkId;
        }
    }
}






