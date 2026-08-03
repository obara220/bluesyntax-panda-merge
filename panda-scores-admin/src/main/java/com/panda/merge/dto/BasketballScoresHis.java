package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;

import java.io.Serializable;

/**
 * 加时赛轮次历史记录
 * @author vere
 * @date 2025-07-13
 * @version 1.0.0
 */
public class BasketballScoresHis implements Serializable {
    @ScoresProperty(eventName = "轮次")
    private Integer no;
    @ScoresProperty(eventName = "主客队记录")
    private CommonItem timeout;
    @ScoresProperty(eventName = "变更时间")
    private String changedTime;
    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }
    public CommonItem getTimeout() {
        return timeout;
    }

    public void setTimeout(CommonItem timeout) {
        this.timeout = timeout;
    }

    public String getChangedTime() {
        return changedTime;
    }

    public void setChangedTime(String changedTime) {
        this.changedTime = changedTime;
    }
}
