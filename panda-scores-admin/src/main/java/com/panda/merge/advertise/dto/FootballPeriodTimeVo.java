package com.panda.merge.advertise.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * PA报球板-按分钟统计阶段
 *
 * @author warren
 * @since 2023/12/11 21:22:02
 */
@Data
public class FootballPeriodTimeVo implements Serializable {
    /**
     * 足球报球版详情-按时间阶段分类
     */
    private FootBallPeriod15Vo period15;

    private FootBallPeriod15Vo period30;

    private FootBallPeriod15Vo period45;

    private FootBallPeriod15Vo period60;

    private FootBallPeriod15Vo period75;

    private FootBallPeriod15Vo period90;
}
