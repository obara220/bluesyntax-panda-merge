package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class A99MatchSwitchDTO implements Serializable {

    /**
     * 球种ID
     */
    private Long sportId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 1早 0滚
     */
    private int matchType;

    /**
     * 0关 1开
     */
    private int status;

    /**
     * 玩法集id
     */
    private String categorySetId;

}
