package com.panda.merge.dto.advertise.v2;

import com.panda.merge.cache.CommonItem;
import lombok.Data;

import java.io.Serializable;

/**
 * 乒乓球报球板单局/全场比分聚合视图。
 * 与 {@code com.panda.merge.tabletennis.dto.TableTennisV2Scores} 一一对应。
 */
@Data
public class PDTableTennisEventDto implements Serializable {

    private Long sportId;
    private String thirdMatchId;
    private Integer setNum;
    private Integer controlType;

    private CommonItem matchScore;
    private CommonItem setScore;
    private CommonItem serve;
    private CommonItem kickoff;
    private CommonItem reServe;
    private CommonItem yellowCard;
    private CommonItem redCard;
    private CommonItem expediteMode;
    private CommonItem yellowRedCardSameHand;
}
