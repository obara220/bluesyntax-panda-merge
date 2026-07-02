package com.panda.merge.dto;

import lombok.Data;

/**
 * 给下游传值的json
 */
@Data
public class FootballScoresDownStream{

    private CommonItem corner ;

    private CommonItem redCard ;

    private CommonItem yellowCard ;

    private CommonItem faCard ;

    private CommonItem goal ;

    private CommonItem  kickOff ;

    private CommonItem attack ;

    private CommonItem dangerousAttack ;

    private CommonItem possession ;

    private CommonItem shotOn ;

    private CommonItem shotOff ;

    private CommonItem shot;

    private CommonItem substitution ;

    private CommonItem offside ;

    private CommonItem penaltyAwarded;

    private CommonItem freeKickScore;
}
