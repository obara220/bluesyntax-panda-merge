package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonItem implements Serializable {
//    private String eventCode;
//    private String eventName;
    private Integer away;
    private Integer home;
    public CommonItem(){
        away=0;
        home=0;
    }
    public CommonItem(String eventCode,String eventName){
        away=0;
        home=0;
//        this.eventCode=eventCode;
//        this.eventName=eventName;
    }

    public CommonItem(Integer home, Integer away, boolean flag) {
        this.home = home;
        this.away = away;
    }
    public CommonItem(Integer home, Integer away) {
        this.home = home;
        this.away = away;
    }
    public String doCountScoreStr(){
        return home+"-"+away;
    }
    public String doScoreStr(){
        return home+":"+away;
    }
}
