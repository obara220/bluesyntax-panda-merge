package com.panda.merge.dto.scores;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchScoresRequestDTO implements Serializable {
    private Long matchId;
    private boolean isStandard =false;
    /**
     * 是否展示右边比分版
     */
    private boolean attention =false;
}
