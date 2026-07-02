package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchScoresInfo implements Serializable {
    private Long id;

    private Long thirdMatchId;

    private String thirdMatchSourceId;

    private Long sportId;

    private Long period;

    private Integer periodLength;

    private Integer matchLength;

    private Long eventTime;

    private Long remainingTime;

    private Long secondsMatchStart;

    private String dataSourceCode;

    private String scoresJson;

    @ApiModelProperty(value = "比分模板类型")
    private String scoresJsonType;

    private Integer t1;

    private Integer t2;

    private Integer periodT1;

    private Integer periodT2;

    @ApiModelProperty(value = "livedatauof")
    private String dataSourceType;

    private Long modifyTime;

    private Long createTime;

    private String scoresJsonExtra;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Integer getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(Integer periodLength) {
        this.periodLength = periodLength;
    }

    public Integer getMatchLength() {
        return matchLength;
    }

    public void setMatchLength(Integer matchLength) {
        this.matchLength = matchLength;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public Long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Long getSecondsMatchStart() {
        return secondsMatchStart;
    }

    public void setSecondsMatchStart(Long secondsMatchStart) {
        this.secondsMatchStart = secondsMatchStart;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getScoresJson() {
        return scoresJson;
    }

    public void setScoresJson(String scoresJson) {
        this.scoresJson = scoresJson;
    }

    public String getScoresJsonType() {
        return scoresJsonType;
    }

    public void setScoresJsonType(String scoresJsonType) {
        this.scoresJsonType = scoresJsonType;
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

    public Integer getPeriodT1() {
        return periodT1;
    }

    public void setPeriodT1(Integer periodT1) {
        this.periodT1 = periodT1;
    }

    public Integer getPeriodT2() {
        return periodT2;
    }

    public void setPeriodT2(Integer periodT2) {
        this.periodT2 = periodT2;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
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

    public String getScoresJsonExtra() {
        return scoresJsonExtra;
    }

    public void setScoresJsonExtra(String scoresJsonExtra) {
        this.scoresJsonExtra = scoresJsonExtra;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", sportId=").append(sportId);
        sb.append(", period=").append(period);
        sb.append(", periodLength=").append(periodLength);
        sb.append(", matchLength=").append(matchLength);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", remainingTime=").append(remainingTime);
        sb.append(", secondsMatchStart=").append(secondsMatchStart);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", scoresJson=").append(scoresJson);
        sb.append(", scoresJsonType=").append(scoresJsonType);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", periodT1=").append(periodT1);
        sb.append(", periodT2=").append(periodT2);
        sb.append(", dataSourceType=").append(dataSourceType);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", scoresJsonExtra=").append(scoresJsonExtra);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}