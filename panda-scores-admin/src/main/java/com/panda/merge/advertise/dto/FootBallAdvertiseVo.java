package com.panda.merge.advertise.dto;

import com.panda.merge.dto.advertise.EventOperationDto;
import com.panda.merge.dto.advertise.InjuryTimeEventDto;
import com.panda.merge.model.MatchTimeInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 足球报球版详情
 * */
@Data
public class FootBallAdvertiseVo implements Serializable {
    // 赛事信息相关
    /***
     * 赛事Id
     */
    private String thirdMatchId;
    /**
     * 标准赛事ID
     * */
    private Long standardMatchId;

    /**
     * 标准赛事长ID
     */
    private String matchManageId;
    /**
     * 开赛时间
     * */
    private Long matchBeginTime;
    //赛事时间阶段相关
    /**
     * 阶段
     * */
    private Long period;
    /**
     * 倒计时
     * */
    private Long matchTime;
    /**
     * 事件时间
     * */
    private Long eventTime;
    /**
     * 数据商编码
     * */
    private String dataSourceCode;

    /**
     * 赛事比分相关
      */
    private FootBallScoreVo footBallScore;

    // 赛事事件状态相关
    private FootBallEventStatusVo footBallEventStatus;

    //危险安全
    private boolean  danger;

    //当前事件
    private String  currentEventCode;

    private Integer isGo;

    /**
     * 1 常  2. 常 加  3.常 加 点
      */
    private  Integer  hasPeriod;

    /**
     * 15分钟进球比分dto -map
     */
    private FootBallGoalScore15Vo footBallGoal15Score;

    /**
     * 点球大战每轮比分 dto
     */
    private FootBallPenaltyScoreVo footBallPenaltyScore;

    /**
     * 15分钟角球球比分dto -map
     */
    private FootBallCornerScore15Vo footBallCorner15Score;

    /**
     * 15分钟进球比分dto -map
     */
    private FootBallGoalScore5Vo footBallGoal5Score;

    // 15分钟黄牌
    private FootBallYellowCard15Vo footBallYellowCard15Vo;

    private FootBallRedCard15Vo footBallRedCard15Vo;

    private FootBallFaCard15Vo footBallFaCard15Vo;

    // 赛事统计-技术统计
    private FootballScoreboardVo footballScoreboardVo;

    // 15分钟阶段进球
    private FootballPeriodTimeVo footballPeriodTimeVo;

    // 伤补和时间状态
    private MatchTimeInfo injuryAndtimeStatus;

    // VAR事件
    private EventOperationDto eventOperationDto;

    /**
     * PD参与结算的状态
     * 存redis
     * */
    private Integer settleStatus;

    /**
     * 赛事控制类型: 1 开始   2 暂停  3 继续  4 结束
     */
    private Integer controlType;

    /**
     * 点球进球重踢时：主队点球进球值为away，客队点球进球值为home，其它情况置空
     */
    private String retakePen;

    /**
     * 事件来源类型(0:其他，1:现场（VENUE）,2电视（TV）)
     */
    private Integer liveEventSource;
}
