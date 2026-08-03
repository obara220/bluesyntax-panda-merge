package com.panda.merge.mq.message;

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
    private Integer firstNum;
    //局数
    private Integer secondNum;
    //3.赛种
    private Long sportId;
    //4.数据源
    private String dataSourceCode;
    //5.比分
    private Map scores;
    //6.事件源类型 LIVEDATA UOF
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
    /**
     * 是否需要全量覆盖:
     * 1: 需要覆盖
     * 0: 不需要覆盖
     * */
    private Integer needOverScores = 0;

    /**
     * "比赛暂停.0:未暂停;1:暂停."
     */
    private Integer whetherStop;

    /**
     * 事件编码
     * 用于足球监控角球罚牌变化，其他球种存在match_status与比分事件的转换，所以只能参考，不可做判定
     */
    private String eventCode;
    /**
     * 得分队伍
     * 用于足球监控角球罚牌变化，其他球种存在match_status与比分事件的转换，所以只能参考，不可做判定
     */
    private String homeAway;

    //板球轮数
    private String over;

    /**
     * 事件用来记录是否有异常的标识  1为异常
     */
    private String addition5;

    /**
     * 标记是否标准比分中心下发
     * 1
     */
    private String dataFrom;
    /**
     * pls标准联赛ID
     */
    private Long plsStandardTournamentId;
    /**
     * pls赛事ID
     */
    private Long plsStandardMatchId;
    /**
     * 赛事状态 发给比分网
     */
    private Integer matchStatus;

}
