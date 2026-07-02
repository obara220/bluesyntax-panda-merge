package com.panda.merge.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data

public class BaseballScores   {


    private CommonItem matchScore ;


    private CommonItem setScore ;

    private CommonItem hit ;

    private Integer goodBall ;


    private Integer badBall ;


    private Integer runnerOut ;


    private Integer firstBase ;


    private Integer secondBase ;


    private Integer thirdBase ;


    private Integer baseHit ;


    private Integer score ;


    private Integer hitNumber ;


    private Integer baseNumber ;


    private Integer ballNumber ;


    private Integer safeBall ;


    private Integer bodyBall ;

}
