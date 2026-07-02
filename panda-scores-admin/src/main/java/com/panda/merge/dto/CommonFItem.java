package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonFItem implements Serializable {
//    private String eventCode;
//    private String eventName;
    private float away;
    private float home;
    public CommonFItem(){
        away=0f;
        home=0f;
    }
    public CommonFItem(String eventCode, String eventName){
        away=0f;
        home=0f;
//        this.eventCode=eventCode;
//        this.eventName=eventName;
    }
}
