package com.panda.merge.mq.message;

import lombok.Data;

import java.util.Map;

/**
 * 比分MQ消费延迟下发业务的消息体
 */
@Data
public class DelayEventMessageDto {
    private String linkedId;
    //1.标准赛事ID
    private Long standardMatchId;
    //2.赛事阶段
    private Long periodId;
    //3.赛种
    private Long sportId;
    //4.数据源
    private String dataSourceCode;
    //事件发生时间
    private Long eventTime;
    //事件下发时间
    private Long createTime;
    //比分处理时间
    private Long scoresTime;
    //延迟时间
    private Long delayTime;

}
