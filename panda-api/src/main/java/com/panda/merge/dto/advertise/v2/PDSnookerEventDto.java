package com.panda.merge.dto.advertise.v2;

import com.panda.merge.cache.CommonItem;
import lombok.Data;

import java.io.Serializable;

@Data
public class PDSnookerEventDto implements Serializable {
    private Long sportId;
    private String thirdMatchId;
    private Integer setNum;
    private Integer controlType;
    private CommonItem matchScore;
    private CommonItem setScore;
    private CommonItem winingScore;
    private CommonItem redWiningScore;
    private CommonItem yellowWiningScore;
    private CommonItem greenWiningScore;
    private CommonItem brownWiningScore;
    private CommonItem blueWiningScore;
    private CommonItem pinkWiningScore;
    private CommonItem blackWiningScore;
    private CommonItem foulScore;
    private CommonItem redFoulScore;
    private CommonItem yellowFoulScore;
    private CommonItem greenFoulScore;
    private CommonItem brownFoulScore;
    private CommonItem blueFoulScore;
    private CommonItem pinkFoulScore;
    private CommonItem blackFoulScore;
    private CommonItem strick;
    private CommonItem freeball;
    private CommonItem freeballNoPot;
    private CommonItem kickoff;
    private Integer rerack;
    private CommonItem penaltyFour;
    private CommonItem penaltySeven;
    /** 直接认输 / 直接判输次数（snooker_foul_lose） */
    private CommonItem directLoss;
    /** 认输次数（snooker_concede） */
    private CommonItem concede;
}
