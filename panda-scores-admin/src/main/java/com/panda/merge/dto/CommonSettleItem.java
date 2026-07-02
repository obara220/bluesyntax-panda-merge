package com.panda.merge.dto;

import java.io.Serializable;


public class CommonSettleItem implements Serializable{
    /**
     * 正在使用的比分
     * */
    private Integer away;
    private Integer home;
    /**
     * 事件记录的比分 或者UOF比分
     * */
    private Integer eventAway;
    private Integer eventHome;
    /**
     * 结算记录的比分
     * */
    private Integer settleAway;
    private Integer settleHome;
    /**
     * 1 已经结算取   settleHome  settleAway
     * 0 null 未结算取 eventHome  eventAway
     * */
    private Integer settleStatus;

    public void setAway(Integer away) {
        this.away = away;
    }

    public void setHome(Integer home) {
        this.home = home;
    }

    public Integer getEventAway() {
        return eventAway;
    }

    public Integer getEventHome() {
        return eventHome;
    }

    public Integer getSettleAway() {
        return settleAway;
    }

    public Integer getSettleHome() {
        return settleHome;
    }

    public CommonSettleItem(){
        away=0;
        home=0;
        eventAway=0;
        eventHome=0;
        settleAway=0;
        settleHome=0;
        settleStatus=0;
    }

    public Integer getAway() {
        return away;
    }

    public Integer getHome() {
        return home;
    }

    public void setEventAway(Integer away) {
        //为null则为旧数据，走旧逻辑
        if(settleStatus == null){
            this.away = away;
        }else if(settleStatus == 0){
            //状态为0则未结算
            this.away = away;
            this.eventAway = away;
        }else if(settleStatus == 1){
            this.eventAway = away;
        }
    }

    public void setEventHome(Integer home) {
        this.eventHome = home;
        if(settleStatus == null){
            this.home = home;
        }else if(settleStatus == 0){
            this.home = home;
            this.eventHome = home;
        }else if(settleStatus == 1){
            this.eventHome = home;
        }
    }

    public void setSettleAway(Integer settleAway) {
        if(settleStatus == null){
            this.away = settleAway;
        }else {
            this.settleAway = settleAway;
            this.away = settleAway;
            settleStatus = 1;
        }
    }

    public void setSettleHome(Integer settleHome) {
        if(settleStatus == null){
            this.home = settleHome;
        }else {
            this.settleHome = settleHome;
            this.home = settleHome;
            settleStatus = 1;
        }
    }

    public Integer getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(Integer settleStatus) {
        if(settleStatus == null){
            return;
        }
        this.settleStatus = settleStatus;
        if(settleStatus == 0 && this.eventHome != null){
            this.home = this.eventHome;
            this.away = this.eventAway;
        }else if(settleStatus == 1 && this.settleHome != null){
            this.home = this.settleHome;
            this.away = this.settleAway;
        }
    }
}
