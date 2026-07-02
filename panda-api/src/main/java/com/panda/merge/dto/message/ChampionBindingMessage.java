package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Kepa
 */
@Data
public class ChampionBindingMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** * 链路id */
    private String linkId;

    /** * 标准联赛id */
    private Long tournamentId;

    /** * 冠军赛事id */
    private Long matchId;

    /** * 冠军盘口id */
    private Long standardMarketId;

    /** * 绑定状态 0:解绑  1:绑定 */
    private Integer relationStatus;
}
