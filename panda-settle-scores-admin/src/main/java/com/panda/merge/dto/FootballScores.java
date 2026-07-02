package com.panda.merge.dto;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;


@Slf4j
@Data
public class FootballScores implements Serializable {


    private CommonItem corner ;


    private CommonItem redCard ;


    private CommonItem yellowCard ;


    private CommonItem faCard ;


    private CommonItem goal ;

    private CommonItem  kickOff ;

}
