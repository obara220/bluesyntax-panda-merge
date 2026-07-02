package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 赛果展示开关
 */
@Data
public class ScoreShowStatus implements Serializable {
    private Long sportId;
    private Long standardMatchId;
    private Integer status;
    private Integer type;
}
