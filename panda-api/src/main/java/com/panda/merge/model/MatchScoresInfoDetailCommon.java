package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchScoresInfoDetailCommon implements Serializable {
    private Long id;

    private Long matchScoresInfoId;

    private String eventCode;

    private Long firtPeriod;

    private Long secondPeriod;

    private Integer t1;

    private Integer t2;

    private Long modifyTime;

    private Long createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchScoresInfoId() {
        return matchScoresInfoId;
    }

    public void setMatchScoresInfoId(Long matchScoresInfoId) {
        this.matchScoresInfoId = matchScoresInfoId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Long getFirtPeriod() {
        return firtPeriod;
    }

    public void setFirtPeriod(Long firtPeriod) {
        this.firtPeriod = firtPeriod;
    }

    public Long getSecondPeriod() {
        return secondPeriod;
    }

    public void setSecondPeriod(Long secondPeriod) {
        this.secondPeriod = secondPeriod;
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
        sb.append(", matchScoresInfoId=").append(matchScoresInfoId);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", firtPeriod=").append(firtPeriod);
        sb.append(", secondPeriod=").append(secondPeriod);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}