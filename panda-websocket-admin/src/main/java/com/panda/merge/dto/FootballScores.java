package com.panda.merge.dto;


import lombok.Data;

import java.lang.reflect.Field;


@Data
public class FootballScores  {


    private CommonItem corner ;


    private CommonItem redCard ;


    private CommonItem yellowCard ;


    private CommonItem faCard ;


    private CommonItem goal ;


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


    private CommonItem  kickOff ;

    public  void changeHomeAwayScore() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(field.getType().equals(com.panda.merge.dto.CommonItem.class)){
                field.setAccessible(true);
                CommonItem commonItem = (com.panda.merge.dto.CommonItem)field.get(this);
                if(commonItem!=null){
                    Integer home = commonItem.getHome();
                    Integer away = commonItem.getAway();
                    commonItem.setHome(away);
                    commonItem.setAway(home);
                }
            }
        }
    }

}
