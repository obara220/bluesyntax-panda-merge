package com.panda.merge.mq.message;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.CommonItem;
import lombok.Data;

import java.util.Map;


@Data
public class CommonThirdScoresDto {
    private String linkedId;
    //1.标准赛事ID
    private Long thirdMatchId;
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
    //比分计算的时间
    private Long scoreTime;

    private Map<String, Object> allScores;
    //1. 15分钟阶段比分
    private Map  minuteScores;

    private  Long secondFromStart;

    private Long eventId;
    /**
     * 暂时放点球大战比分
     * */
    private JSONObject extraScores;

    private String userName;

    /**
     * "比赛暂停.0:未暂停;1:暂停."
     */
    private Integer whetherStop;
}
