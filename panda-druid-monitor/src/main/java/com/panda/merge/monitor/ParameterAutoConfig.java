package com.panda.merge.monitor;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class ParameterAutoConfig {
    /**
     * 使用的是springcloud框架的nacos还是springboot框架的nacos
     */
    @Value("${druid.monitor.is.nacos.cloud:false}")
    private boolean isNacosCloud;

    /**
     * 监控采集开关 - springCloud版本
     */
    @Value("${druid.monitor.enabled:true}")
    private boolean enabled;
    /**
     * 应用服务名称 - springCloud版本
     */
    @Value("${spring.application.name}")
    private String applicationName;
    /**
     * 监控采集间隔 - springCloud版本
     */
    @Value("${druid.monitor.collect.interval.sec:600}")
    private long collectInterval;
    /**
     * SQL异常重置时间 - springCloud版本
     */
    @Value("${druid.monitor.sql.error.reset.sec:1800}")
    private long errorResetSeconds;
    /**
     * 活动连接数告警比率 - springCloud版本
     */
    @Value("${druid.monitor.active.connection.alert.ratio:0.7}")
    private double activeConnectionAlertRatio;
    /**
     * 上报消息主题名称 - springCloud版本
     */
    @Value("${druid.monitor.sql.stat.topic:data_druid_message_handler}")
    private String sqlStatTopic;

    /**
     * 监控采集开关 - springBoot版本
     */
    @NacosValue(value = "${druid.monitor.enabled:true}", autoRefreshed = true)
    private boolean enabledBoot;
    /**
     * 应用服务名称 - springBoot版本
     */
    @NacosValue(value = "${spring.application.name}", autoRefreshed = true)
    private String applicationNameBoot;
    /**
     * 监控采集间隔 - springBoot版本
     */
    @NacosValue(value = "${druid.monitor.collect.interval.sec:600}", autoRefreshed = true)
    private long collectIntervalBoot;
    /**
     * SQL异常重置时间 - springBoot版本
     */
    @NacosValue(value = "${druid.monitor.sql.error.reset.sec:1800}", autoRefreshed = true)
    private long errorResetSecondsBoot;
    /**
     * 活动连接数告警比率 - springBoot版本
     */
    @NacosValue(value = "${druid.monitor.active.connection.alert.ratio:0.7}", autoRefreshed = true)
    private double activeConnectionAlertRatioBoot;
    /**
     * 上报消息主题名称 - springBoot版本
     */
    @NacosValue(value = "${druid.monitor.sql.stat.topic:data_druid_message_handler}", autoRefreshed = true)
    private String sqlStatTopicBoot;

    public Boolean getEnabled() {
        return isNacosCloud ? enabled : enabledBoot;
    }

    public String getApplicationName() {
        return isNacosCloud ? applicationName : applicationNameBoot;
    }

    public long getCollectInterval() {
        return isNacosCloud ? collectInterval : collectIntervalBoot;
    }

    public long getErrorResetSeconds() {
        return isNacosCloud ? errorResetSeconds : errorResetSecondsBoot;
    }

    public double getActiveConnectionAlertRatio() {
        return isNacosCloud ? activeConnectionAlertRatio : activeConnectionAlertRatioBoot;
    }
    public String getSqlStatTopic() {
        return isNacosCloud ? sqlStatTopic : sqlStatTopicBoot;
    }
}
