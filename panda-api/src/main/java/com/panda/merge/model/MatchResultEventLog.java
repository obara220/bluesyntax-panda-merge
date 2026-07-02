package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchResultEventLog implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "数据源")
    private String dataSourceCode;

    private String eventCode;

    @ApiModelProperty(value = "事件名称")
    private String eventName;

    @ApiModelProperty(value = "额外信息,一般用作事件的子类型")
    private String extraInfo;

    @ApiModelProperty(value = "待确认时间.被阻塞的事件会因为前边阻塞的事件导致待确认时间延长,此该时间必须保存")
    private Integer confirmTime;

    @ApiModelProperty(value = "可选值home,awayhome:主队away:客队")
    private String homeAway;

    @ApiModelProperty(value = "比较阶段value，对应字典：t.parent_type_id=8ANDt.addition1=体育类型;如足球对应字典：t.parent_type_id=8ANDt.addition1=1;")
    private Long matchPeriodId;

    @ApiModelProperty(value = "当前事件对应的比分.格式为:126:146")
    private String matchScore;

    private Long referenceId;

    @ApiModelProperty(value = "球员1的id")
    private Long player1Id;

    @ApiModelProperty(value = "球员1的名称")
    private String player1Name;

    @ApiModelProperty(value = "球员2的id")
    private Long player2Id;

    @ApiModelProperty(value = "球员2的名称")
    private String player2Name;

    @ApiModelProperty(value = "距离比赛开始多少秒")
    private Integer secondsFromStart;

    @ApiModelProperty(value = "标准赛事的id.对应standard_match_info.id")
    private Long standardMatchId;

    @ApiModelProperty(value = "标准球队ID.对应standard_sport_team.id")
    private Long standardTeamId;

    @ApiModelProperty(value = "主队数量")
    private Integer eventHomeScore;

    private Integer eventAwayScore;

    @ApiModelProperty(value = "第三方数据源提供的该事件id.")
    private String thirdEventId;

    @ApiModelProperty(value = "第三方赛事的id.对应third_match_info.id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "比赛在数据源中的ID")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "第三方球队id.对应third_sport_team.id")
    private Long thirdTeamId;

    @ApiModelProperty(value = "赛果事件状态：0：待确认1：待自动确认2:已确认3：已暂停4:无效")
    private Integer status;

    @ApiModelProperty(value = "操作类型：0：创建1：启动自动确认2：自动确认3：手动确认4：赛果编辑5：赛果修正6：赛果忽略7：数据源移除8：事件取消9：确认暂停")
    private Integer operateType;

    @ApiModelProperty(value = "确认次数")
    private Integer confirmTimes;

    @ApiModelProperty(value = "暂停时间（秒）")
    private Integer suspendTime;

    @ApiModelProperty(value = "预警类型：0：无1:已确认赛果变更预警2：阻塞预警")
    private Integer alertType;

    private String operateId;

    @ApiModelProperty(value = "数据来源类型：0:UOF1:ScoringFeed")
    private Integer sourceType;

    @ApiModelProperty(value = "商业数据源,格式如：SR,BC")
    private String commercialDatasources;

    @ApiModelProperty(value = "竞品数据源，如188,QT")
    private String competitionDatasources;

    private String remark;

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

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Integer getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Integer confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(Long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
    }

    public String getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public Integer getSecondsFromStart() {
        return secondsFromStart;
    }

    public void setSecondsFromStart(Integer secondsFromStart) {
        this.secondsFromStart = secondsFromStart;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getStandardTeamId() {
        return standardTeamId;
    }

    public void setStandardTeamId(Long standardTeamId) {
        this.standardTeamId = standardTeamId;
    }

    public Integer getEventHomeScore() {
        return eventHomeScore;
    }

    public void setEventHomeScore(Integer eventHomeScore) {
        this.eventHomeScore = eventHomeScore;
    }

    public Integer getEventAwayScore() {
        return eventAwayScore;
    }

    public void setEventAwayScore(Integer eventAwayScore) {
        this.eventAwayScore = eventAwayScore;
    }

    public String getThirdEventId() {
        return thirdEventId;
    }

    public void setThirdEventId(String thirdEventId) {
        this.thirdEventId = thirdEventId;
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

    public Long getThirdTeamId() {
        return thirdTeamId;
    }

    public void setThirdTeamId(Long thirdTeamId) {
        this.thirdTeamId = thirdTeamId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getConfirmTimes() {
        return confirmTimes;
    }

    public void setConfirmTimes(Integer confirmTimes) {
        this.confirmTimes = confirmTimes;
    }

    public Integer getSuspendTime() {
        return suspendTime;
    }

    public void setSuspendTime(Integer suspendTime) {
        this.suspendTime = suspendTime;
    }

    public Integer getAlertType() {
        return alertType;
    }

    public void setAlertType(Integer alertType) {
        this.alertType = alertType;
    }

    public String getOperateId() {
        return operateId;
    }

    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getCommercialDatasources() {
        return commercialDatasources;
    }

    public void setCommercialDatasources(String commercialDatasources) {
        this.commercialDatasources = commercialDatasources;
    }

    public String getCompetitionDatasources() {
        return competitionDatasources;
    }

    public void setCompetitionDatasources(String competitionDatasources) {
        this.competitionDatasources = competitionDatasources;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        sb.append(", sportId=").append(sportId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", eventName=").append(eventName);
        sb.append(", extraInfo=").append(extraInfo);
        sb.append(", confirmTime=").append(confirmTime);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", matchPeriodId=").append(matchPeriodId);
        sb.append(", matchScore=").append(matchScore);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", player1Id=").append(player1Id);
        sb.append(", player1Name=").append(player1Name);
        sb.append(", player2Id=").append(player2Id);
        sb.append(", player2Name=").append(player2Name);
        sb.append(", secondsFromStart=").append(secondsFromStart);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", standardTeamId=").append(standardTeamId);
        sb.append(", eventHomeScore=").append(eventHomeScore);
        sb.append(", eventAwayScore=").append(eventAwayScore);
        sb.append(", thirdEventId=").append(thirdEventId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", thirdTeamId=").append(thirdTeamId);
        sb.append(", status=").append(status);
        sb.append(", operateType=").append(operateType);
        sb.append(", confirmTimes=").append(confirmTimes);
        sb.append(", suspendTime=").append(suspendTime);
        sb.append(", alertType=").append(alertType);
        sb.append(", operateId=").append(operateId);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", commercialDatasources=").append(commercialDatasources);
        sb.append(", competitionDatasources=").append(competitionDatasources);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}