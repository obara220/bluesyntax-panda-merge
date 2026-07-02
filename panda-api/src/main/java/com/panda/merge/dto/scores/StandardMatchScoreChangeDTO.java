package com.panda.merge.dto.scores;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardMatchScoreChangeDTO implements Serializable {

    private Long matchId;

    /**
     * 变化类型 1-比分，2-罚牌，3-角球
     */
    private Integer type;


}
