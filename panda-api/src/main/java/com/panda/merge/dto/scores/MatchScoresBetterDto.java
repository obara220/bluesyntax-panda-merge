package com.panda.merge.dto.scores;


import lombok.Data;

@Data
public class MatchScoresBetterDto {

    private String matchId;

    private Long sportId;

    private Integer matchStatus;
    /**
     * 数据源
     * */
    private String dataSourceCode;

    /**当前阶段*/
    private Long periodNow;
    /**阶段时间*/
    private Long remainingTime;
    /**比赛进行时间*/
    private Long secondsMatchStart;
    /**是否暂停 ：1 正常  0  暂停 */
    private Integer isTimeGo;
    /**当前系统时间 */
    private Long nowSystemTime;
    /**事件当前时间*/
    private Long eventTime;

    private Long thirdMatchId;

    private String dataSourceType;

    private String scoresJson;

    private Integer matchLength;

    private Integer t1;

    private Integer t2;

    private  Integer periodT1;

    private  Integer periodT2;

    //当前第几局
    private Integer currentRound;

    //当前盘数
    private Integer currentSet;

    private  Integer homeAwayOpposite;

    //操盘手
    private String operator;

    // 按前端要求将PD2数据(数据源、报球员、当前阶段)设置到该属性
    private PdTwoInfo pdTwoInfo;

    // 按前端要求将PD1数据(数据源、报球员、当前阶段)设置到该属性
    private PdOneInfo pdOneInfo;

    //推送暂时存放板球轮次
    private String scoresJsonExtra;
    private String scoresJsonType;

    public String getScoresJsonType() {
        return scoresJsonType;
    }

    public void setScoresJsonType(String scoresJsonType) {
        this.scoresJsonType = scoresJsonType;
    }
}
