package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScoreCodeDto implements Serializable {
    private String scoreCode;
    private Integer t1;
    private Integer t2;
}
