package com.panda.merge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * job静态配置信息
 * @author :  tell
 * @since     2020年9月11日19:20:50
 */
@Data
@Component
@ConfigurationProperties(prefix = "job-time")
public class JobExecuteTimeConfig {
    /**
     * 操盘手的完赛分钟间隔控制
     */
    private int finishOperateMatchMins;
    /**
     * 爬虫的完赛状态分钟控制
     */
    private int  finishScrapyMatchMins;
    /**
     * 最大完赛时间控制小时
     */
    private int maxMatchTimeOverHour;
    /**
     * 对完赛状态临时态补偿分钟控制
     */
    private int fixMatchOverTempStatusMins;
}
