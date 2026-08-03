package com.panda.merge.dto.advertise.v2;

import com.panda.merge.cache.CommonItem;
import lombok.Data;

import java.io.Serializable;

/**
 * 排球报球板单局/全场比分聚合视图。
 * 与 {@code com.panda.merge.volleyball.dto.VolleyballV2Scores} 一一对应。
 */
@Data
public class PDVolleyballEventDto implements Serializable {

    private Long sportId;
    private String thirdMatchId;
    private Integer setNum;
    private Integer controlType;

    private CommonItem matchScore;
    private CommonItem setScore;
    private CommonItem serve;
    private CommonItem serviceError;
    private CommonItem out;
    private CommonItem ace;
    private CommonItem kill;
    private CommonItem block;
    private CommonItem expulsion;
    private CommonItem disqualification;
    private CommonItem penalty;
    private CommonItem error;
    private CommonItem kickoff;
}
