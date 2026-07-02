package com.panda.merge.mq.message;

import lombok.Data;

import java.util.Map;

@Data
public class BussinessScoreMessage {
    private String linkedId;
    //1.标准赛事ID
    private Long standardMatchId;
    //2.赛事阶段
    private Long periodId;
    //3.赛种
    private Long sportId;
    //4.数据源
    private String dataSourceCode;
    //5.比分
    private Map scores;
    //6.事件源类型
    private Integer eventSourceType;

}
