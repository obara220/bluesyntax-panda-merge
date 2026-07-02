package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StandardSettleScoresPushDto implements Serializable {
    private Long standardMatchId;
    private String eventCode;
    private List data;
}
