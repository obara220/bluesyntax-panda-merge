package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class A99MatchOddsDiffenceDTO implements Serializable {

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 玩法集id
     */
    private String categorySetId;

    /**
     * 赛事类型 0:滚球 1:早盘
     */
    private Integer matchType;

    /**
     * 赔率变化差值
     */
    private Double diffValue;

}
