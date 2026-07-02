package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 自动跳分跳水的盘口
 */
@Data
public class AutoDiffMarketOddsMessage implements Serializable {
    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 玩法
     */
    private Long standardCategoryId;
    /**
     * 投注项类型
     */
    private String oddType;
    /**
     * 盘口id
     */
    private Long marketId;
}
