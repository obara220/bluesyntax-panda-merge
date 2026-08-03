package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleGrayCheck implements Serializable {
    private Long id;

    private Long standardMatchId;

    private String settleScoreEventId;

    private String homeAway;

    private Integer t1;

    private Integer t2;

    private String dataSourceCode;

    private String settleNum;

    @ApiModelProperty(value = "0:未结算,1：已结算")
    private Integer status;

    private Long eventTime;

    private Long modifyTime;

    private Long createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getSettleScoreEventId() {
        return settleScoreEventId;
    }

    public void setSettleScoreEventId(String settleScoreEventId) {
        this.settleScoreEventId = settleScoreEventId;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Integer getT1() {
        return t1;
    }

    public void setT1(Integer t1) {
        this.t1 = t1;
    }

    public Integer getT2() {
        return t2;
    }

    public void setT2(Integer t2) {
        this.t2 = t2;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getSettleNum() {
        return settleNum;
    }

    public void setSettleNum(String settleNum) {
        this.settleNum = settleNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", settleScoreEventId=").append(settleScoreEventId);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", settleNum=").append(settleNum);
        sb.append(", status=").append(status);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}