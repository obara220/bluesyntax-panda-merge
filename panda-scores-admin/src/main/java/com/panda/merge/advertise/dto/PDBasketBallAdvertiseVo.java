package com.panda.merge.advertise.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.BasketballScoresExtra;
import com.panda.merge.dto.BasketballScoresPDDto;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.advertise.FreeThrowDetailDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class PDBasketBallAdvertiseVo implements Serializable {

    private String thirdMatchId;

    private Long standardMatchId;
    /**
     * 倒计时
     * */
    private Long matchTime;
    /**
     * 开赛时间
     * */
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 统计信息 全Map
     * */
    private Map<String, BasketballScoresPDDto> allScoreMap;
    /**
     * 6分钟比分统计
     * */
    private Map<String, CommonItem> sixMinuteScoresMap;

    private Long sysTime;

    /**
     * 篮球4*12中场休息倒计时
     */
    private Long restTime;

    /**
     * 中断(80)时当前阶段比分
     */
    private CommonItem interruptPeriodScore;

    /**
     * 罚球状态
     */
    private FreeThrowDetailDto freeThrowDetailDto;

    /**
     * 篮球每次加时阶段暂停统计
     */
    private BasketballScoresExtra basketballScoresExtra;
}
