package com.panda.merge.advertise.dto;

import com.panda.merge.dto.CommonItem;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class PDMatchAdvertiseVo  implements Serializable {
    /***
     * 赛事信息
     */
    private String thirdMatchId;
    private Long standardMatchId;
    /**
     * 倒计时
     * */
    private Long matchTime;
    /**
     * 开赛时间
     * */
    private Long matchBeginTime;
    /**
     * 阶段
     * */
    private Long period;
    /**
     * 时间是否暂停  1 不暂停  0暂停
     * */
    private String isGo;
    /**
     * 事件时间
     * */
    private Long eventTime;
    /**
     * 赛制
     * */
    private Integer matchLength;
    /**
     * 比分信息
     * */
    private BasketBallScoreVo basketBallScore;
    /**
     * 6分钟比分统计
     * */
    private Map<String, CommonItem> sixMinuteScoresMap;

    /**
     * 比分信息
     * */
    private IceHockeyScoreVo iceHockeyScore;

    private Long sysTime;

    /**
     * 篮球4*12中场休息倒计时
     */
    private Long restTime;
}
