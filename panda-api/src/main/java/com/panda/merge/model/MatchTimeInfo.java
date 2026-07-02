package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchTimeInfo implements Serializable {
    private Long id;

    private Long thirdMatchId;

    @ApiModelProperty(value = "事件源类型:1livedata0uof")
    private String dataSourceType;

    private Long period;

    @ApiModelProperty(value = "是否时间暂停:默认1不暂停0暂停")
    private Integer timeGo;

    private Long secondFromStart;

    @ApiModelProperty(value = "中场休息")
    private Long halfTime;

    /**
     * 篮球4*12中场休息倒计时
     */
    private Long restTime;

    private Long eventTime;

    private Long remainingTime;

    @ApiModelProperty(value = "局制:1长盘制,2抢七制,3单人抢十,4双人抢十,5特")
    private Integer matchLength;

    @ApiModelProperty(value = "盘切换展示:1,2,3,4,5")
    private Integer firstNum;

    private Long periodLength;

    @ApiModelProperty(value = "每盘的长度:{'1':15,'2':13,'3':13}")
    private String periodLengthJson;

    @ApiModelProperty(value = "每盘的局制:{'1':2,'2':2,'3':2}1长盘制,2抢七制,3单人抢十,4双人抢十,5特")
    private String matchLengthJson;

    @ApiModelProperty(value = "伤停补时, 单位: S 格式 场次:补时时间")
    private String timeOutList;

    @ApiModelProperty(value = "3:3盘5:5盘")
    private Integer roundType;

    @ApiModelProperty(value = "当前盘数")
    private Integer currentSet;

    @ApiModelProperty(value = "当前局数")
    private Integer currentRound;

    private Long createTime;

    private Long modifyTime;

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

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Integer getTimeGo() {
        return timeGo;
    }

    public void setTimeGo(Integer timeGo) {
        this.timeGo = timeGo;
    }

    public Long getSecondFromStart() {
        return secondFromStart;
    }

    public void setSecondFromStart(Long secondFromStart) {
        this.secondFromStart = secondFromStart;
    }

    public Long getHalfTime() {
        return halfTime;
    }

    public void setHalfTime(Long halfTime) {
        this.halfTime = halfTime;
    }

    public Long getRestTime() {
        return restTime;
    }

    public void setRestTime(Long restTime) {
        this.restTime = restTime;
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

    public Integer getMatchLength() {
        return matchLength;
    }

    public void setMatchLength(Integer matchLength) {
        this.matchLength = matchLength;
    }

    public Integer getFirstNum() {
        return firstNum;
    }

    public void setFirstNum(Integer firstNum) {
        this.firstNum = firstNum;
    }

    public Long getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(Long periodLength) {
        this.periodLength = periodLength;
    }

    public String getPeriodLengthJson() {
        return periodLengthJson;
    }

    public void setPeriodLengthJson(String periodLengthJson) {
        this.periodLengthJson = periodLengthJson;
    }

    public String getTimeOutList() {
        return timeOutList;
    }

    public void setTimeOutList(String timeOutList) {
        this.timeOutList = timeOutList;
    }

    public String getMatchLengthJson() {
        return matchLengthJson;
    }

    public void setMatchLengthJson(String matchLengthJson) {
        this.matchLengthJson = matchLengthJson;
    }

    public Integer getRoundType() {
        return roundType;
    }

    public void setRoundType(Integer roundType) {
        this.roundType = roundType;
    }

    public Integer getCurrentSet() {
        return currentSet;
    }

    public void setCurrentSet(Integer currentSet) {
        this.currentSet = currentSet;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", dataSourceType=").append(dataSourceType);
        sb.append(", period=").append(period);
        sb.append(", timeGo=").append(timeGo);
        sb.append(", secondFromStart=").append(secondFromStart);
        sb.append(", halfTime=").append(halfTime);
        sb.append(", restTime=").append(restTime);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", remainingTime=").append(remainingTime);
        sb.append(", matchLength=").append(matchLength);
        sb.append(", firstNum=").append(firstNum);
        sb.append(", periodLength=").append(periodLength);
        sb.append(", periodLengthJson=").append(periodLengthJson);
        sb.append(", timeOutList=").append(timeOutList);
        sb.append(", matchLengthJson=").append(matchLengthJson);
        sb.append(", roundType=").append(roundType);
        sb.append(", currentSet=").append(currentSet);
        sb.append(", currentRound=").append(currentRound);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}