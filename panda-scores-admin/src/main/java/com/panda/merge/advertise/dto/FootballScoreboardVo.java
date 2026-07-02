package com.panda.merge.advertise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 足球报球板-赛事统计-技术统计
 *
 * @author warren
 * @since 2023/12/11 21:01:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballScoreboardVo implements Serializable {
    /**
     * 进球
     */
    private FootballMatchStageVo goal;
    /**
     * 角球
     */
    private FootballMatchStageVo corner;
    /**
     * 球门球
     */
    private FootballMatchStageVo goalKick;
    /**
     * 界外球
     */
    private FootballMatchStageVo throwIn;

    /**
     * 按球率
     */
    private FootballMatchStageVo ballPossessionPercentage;

    /**
     * 持球数(持球事件下发次数)
     */
    private FootballMatchStageVo possessionCount;

    /**
     * 进攻
     */
    private FootballMatchStageVo attack;
    /**
     * 危险进攻
     */
    private FootballMatchStageVo dangerousAttack;
    /**
     * 黄牌
     */
    private FootballMatchStageVo yellowCard;
    /**
     * 红牌
     */
    private FootballMatchStageVo redCard;
    /**
     * 射正
     */
    private FootballMatchStageVo shotOnTarget;
    /**
     * 射偏
     */
    private FootballMatchStageVo shotOffTarget;
    /**
     * 任意球
     */
    private FootballMatchStageVo freeKick;
    /**
     * 越位
     */
    private FootballMatchStageVo offside;
}
