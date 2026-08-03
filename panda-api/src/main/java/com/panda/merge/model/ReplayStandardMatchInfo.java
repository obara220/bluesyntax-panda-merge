package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ReplayStandardMatchInfo implements Serializable {
    private Long id;

    private Long standardMatchId;

    private Long standardTournamentId;

    @ApiModelProperty(value = "0待拉取 1拉取中 2已拉取")
    private Integer syncStatus;

    @ApiModelProperty(value = "重播数量")
    private Integer replayCount;

    private Long createTime;

    private Long modifyTime;

    @ApiModelProperty(value = "重播场次")
    private Integer replayNumber;

    @ApiModelProperty(value = "重播赛事数量")
    private Integer replayMatchCount;

    @ApiModelProperty(value = "事件数量")
    private Integer matchEventCount;

    @ApiModelProperty(value = "赔率数量")
    private Integer oddsCount;

    @ApiModelProperty(value = "赛果数量")
    private Integer matchResultCount;

    @ApiModelProperty(value = "赛事状态数量")
    private Integer matchStatusCount;

    private Long replayBeginTime;

    @ApiModelProperty(value = "上次重播结束时间")
    private Long lastTimeReplayEndTime;

    @ApiModelProperty(value = "0 停止重播 1 正在重播")
    private Integer replayStatus;

    @ApiModelProperty(value = "重播延时时间(秒)")
    private Integer replayDelaySeconds;

    @ApiModelProperty(value = "拉取数据子任务状态")
    private String subSyncStatus;

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

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Integer getReplayCount() {
        return replayCount;
    }

    public void setReplayCount(Integer replayCount) {
        this.replayCount = replayCount;
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

    public Integer getReplayNumber() {
        return replayNumber;
    }

    public void setReplayNumber(Integer replayNumber) {
        this.replayNumber = replayNumber;
    }

    public Integer getReplayMatchCount() {
        return replayMatchCount;
    }

    public void setReplayMatchCount(Integer replayMatchCount) {
        this.replayMatchCount = replayMatchCount;
    }

    public Integer getMatchEventCount() {
        return matchEventCount;
    }

    public void setMatchEventCount(Integer matchEventCount) {
        this.matchEventCount = matchEventCount;
    }

    public Integer getOddsCount() {
        return oddsCount;
    }

    public void setOddsCount(Integer oddsCount) {
        this.oddsCount = oddsCount;
    }

    public Integer getMatchResultCount() {
        return matchResultCount;
    }

    public void setMatchResultCount(Integer matchResultCount) {
        this.matchResultCount = matchResultCount;
    }

    public Integer getMatchStatusCount() {
        return matchStatusCount;
    }

    public void setMatchStatusCount(Integer matchStatusCount) {
        this.matchStatusCount = matchStatusCount;
    }

    public Long getReplayBeginTime() {
        return replayBeginTime;
    }

    public void setReplayBeginTime(Long replayBeginTime) {
        this.replayBeginTime = replayBeginTime;
    }

    public Long getLastTimeReplayEndTime() {
        return lastTimeReplayEndTime;
    }

    public void setLastTimeReplayEndTime(Long lastTimeReplayEndTime) {
        this.lastTimeReplayEndTime = lastTimeReplayEndTime;
    }

    public Integer getReplayStatus() {
        return replayStatus;
    }

    public void setReplayStatus(Integer replayStatus) {
        this.replayStatus = replayStatus;
    }

    public Integer getReplayDelaySeconds() {
        return replayDelaySeconds;
    }

    public void setReplayDelaySeconds(Integer replayDelaySeconds) {
        this.replayDelaySeconds = replayDelaySeconds;
    }

    public String getSubSyncStatus() {
        return subSyncStatus;
    }

    public void setSubSyncStatus(String subSyncStatus) {
        this.subSyncStatus = subSyncStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", standardTournamentId=").append(standardTournamentId);
        sb.append(", syncStatus=").append(syncStatus);
        sb.append(", replayCount=").append(replayCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", replayNumber=").append(replayNumber);
        sb.append(", replayMatchCount=").append(replayMatchCount);
        sb.append(", matchEventCount=").append(matchEventCount);
        sb.append(", oddsCount=").append(oddsCount);
        sb.append(", matchResultCount=").append(matchResultCount);
        sb.append(", matchStatusCount=").append(matchStatusCount);
        sb.append(", replayBeginTime=").append(replayBeginTime);
        sb.append(", lastTimeReplayEndTime=").append(lastTimeReplayEndTime);
        sb.append(", replayStatus=").append(replayStatus);
        sb.append(", replayDelaySeconds=").append(replayDelaySeconds);
        sb.append(", subSyncStatus=").append(subSyncStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}