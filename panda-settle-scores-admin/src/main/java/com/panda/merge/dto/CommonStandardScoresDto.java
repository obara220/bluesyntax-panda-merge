package com.panda.merge.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Map;


@Data
public class CommonStandardScoresDto {
    private String linkedId;
    //1.标准赛事ID
    private Long standardMatchId;
    //2.赛事阶段
    private Long periodId;
    //局数
    private Integer secondNum;
    //3.赛种
    private Long sportId;
    //4.数据源
    private String dataSourceCode;
    //5.比分
    private Map scores;
    //6.事件源类型
    private Integer eventSourceType;
    //比分计算的时间
    private Long scoreTime;

    private Map<String, Object> allScores;
    //1. 15分钟阶段比分
    private Map  minuteScores;
    /**
     * 暂时放点球大战比分
     * */
    private JSONObject extraScores;
    /**
     * AO赛事ID
     * */
    private Long aoMatchId;
    /**
     * 赛事进行时长
     * */
    private Long secondFromStart;

    private String userName;
}
