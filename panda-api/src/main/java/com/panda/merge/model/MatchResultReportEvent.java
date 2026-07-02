package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchResultReportEvent implements Serializable {
    private Long id;

    private Long standardEventId;

    private Long standardMatchId;

    private Long thirdMatchId;

    private Integer sportId;

    @ApiModelProperty(value = "联赛ID")
    private Long tournamentSportId;

    @ApiModelProperty(value = "联赛等级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "修改次数")
    private Integer editTimes;

    private String confirmUserId;

    @ApiModelProperty(value = "审核员名称")
    private String confirmUser;

    @ApiModelProperty(value = "一次审核时长，毫秒")
    private Long confirmTakeTime;

    @ApiModelProperty(value = "审核时间")
    private Long confirmTime;

    @ApiModelProperty(value = "赛事关联数据源")
    private String matchDataSourceCodeList;

    @ApiModelProperty(value = "0:非新增事件1:新增事件")
    private Boolean isAddEvent;

    @ApiModelProperty(value = "开赛时间")
    private Long matchStartTime;

    @ApiModelProperty(value = "事件时间")
    private Long eventTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardEventId() {
        return standardEventId;
    }

    public void setStandardEventId(Long standardEventId) {
        this.standardEventId = standardEventId;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public Long getTournamentSportId() {
        return tournamentSportId;
    }

    public void setTournamentSportId(Long tournamentSportId) {
        this.tournamentSportId = tournamentSportId;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Integer getEditTimes() {
        return editTimes;
    }

    public void setEditTimes(Integer editTimes) {
        this.editTimes = editTimes;
    }

    public String getConfirmUserId() {
        return confirmUserId;
    }

    public void setConfirmUserId(String confirmUserId) {
        this.confirmUserId = confirmUserId;
    }

    public String getConfirmUser() {
        return confirmUser;
    }

    public void setConfirmUser(String confirmUser) {
        this.confirmUser = confirmUser;
    }

    public Long getConfirmTakeTime() {
        return confirmTakeTime;
    }

    public void setConfirmTakeTime(Long confirmTakeTime) {
        this.confirmTakeTime = confirmTakeTime;
    }

    public Long getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Long confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getMatchDataSourceCodeList() {
        return matchDataSourceCodeList;
    }

    public void setMatchDataSourceCodeList(String matchDataSourceCodeList) {
        this.matchDataSourceCodeList = matchDataSourceCodeList;
    }

    public Boolean getIsAddEvent() {
        return isAddEvent;
    }

    public void setIsAddEvent(Boolean isAddEvent) {
        this.isAddEvent = isAddEvent;
    }

    public Long getMatchStartTime() {
        return matchStartTime;
    }

    public void setMatchStartTime(Long matchStartTime) {
        this.matchStartTime = matchStartTime;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
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
        sb.append(", standardEventId=").append(standardEventId);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", sportId=").append(sportId);
        sb.append(", tournamentSportId=").append(tournamentSportId);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", editTimes=").append(editTimes);
        sb.append(", confirmUserId=").append(confirmUserId);
        sb.append(", confirmUser=").append(confirmUser);
        sb.append(", confirmTakeTime=").append(confirmTakeTime);
        sb.append(", confirmTime=").append(confirmTime);
        sb.append(", matchDataSourceCodeList=").append(matchDataSourceCodeList);
        sb.append(", isAddEvent=").append(isAddEvent);
        sb.append(", matchStartTime=").append(matchStartTime);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}