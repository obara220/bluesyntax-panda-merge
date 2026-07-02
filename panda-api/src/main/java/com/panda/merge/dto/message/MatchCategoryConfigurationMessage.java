package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dto
 * @Description :  TODO
 * @Date: 2020-09-17 13:17
 * @ModificationHistory Who    When    What --------  ---------  --------------------------
 */
@Data
public class MatchCategoryConfigurationMessage implements Serializable {
    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 玩法数据源
     */
    private String dataSource;
    /**
     * 玩法名称
     */
    private String playName;
    /**
     * 是否开售 1：是  0：否
     */
    private Integer isSell;
    /**
     * 足球自动关盘时间设置：6、上半场期间 41、加时赛上半场 7、下半场期间 42、加时赛下半场 篮球自动关盘时间设置：13、第1节 14、第2节 15、第3节 16、第4节 40、加时
     */
    private Integer autoCloseMarket;
    /**
     * 最大盘口数
     */
    private Integer marketCount;
    /**
     * 比赛进程时间
     */
    private Integer matchProgressTime;
    /**
     * 补时时间
     */
    private Integer injuryTime;
    /**
     * 支持串关，1:是 0:否
     */
    private Integer isSeries;
    /**
     * 相邻盘口差值
     */
    private BigDecimal marketNearDiff;
    /**
     * 相邻盘口赔率差值
     */
    private BigDecimal marketNearOddsDiff;
    /**
     * 是否特殊抽水 1:是 0:否
     */
    private Integer isSpecialPumping;
    /**
     * 特殊抽水赔率区间
     */
    private String specialOddsInterval;
    /**
     * 最小球头
     */
    private BigDecimal minBallHead;
    /**
     * 最大球头
     */
    private BigDecimal maxBallHead;
    /**
     * 自动开盘阶段
     */
    private Integer autoOpenMarket;
    /**
     * 自动开盘时间
     */
    private Integer autoOpenTime;
}
