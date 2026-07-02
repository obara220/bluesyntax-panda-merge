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
    public CommonItem(String eventCode, String eventName){
        away=0;
        home=0;
//        this.eventCode=eventCode;
//        this.eventName=eventName;
    }
    public String doCountScoreStr(){
        return home+"-"+away;
    }
}
