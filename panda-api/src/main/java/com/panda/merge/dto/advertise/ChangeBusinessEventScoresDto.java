package com.panda.merge.dto.advertise;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangeBusinessEventScoresDto implements Serializable {
    private Long standardMatchId;
    private String dataSourceCode;
}
