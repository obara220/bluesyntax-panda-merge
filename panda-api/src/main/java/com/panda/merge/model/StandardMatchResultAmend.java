package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardMatchResultAmend implements Serializable {
    private Long id;

    @ApiModelProperty(value = "修正标准赛果ID")
    private Long matchResultId;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "是否被取消.0:未没取消；1:取消;")
    private Integer canceled;

    @ApiModelProperty(value = "数据源")
    private String dataSourceCode;

    private String eventCode;

    @ApiModelProperty(value = "事件名称")
    private String eventName;

    @ApiModelProperty(value = "修改时间")
    private Long eventTime;

    @ApiModelProperty(value = "额外信息,一般用作事件的子类型")
    private String extraInfo;

    @ApiModelProperty(value = "扩展字段1")
    private String addition1;

    @ApiModelProperty(value = "扩展字段2")
    private String addition2;

    @ApiModelProperty(value = "扩展字段3")
    private String addition3;

    @ApiModelProperty(value = "扩展字段4")
    private String addition4;

    @ApiModelProperty(value = "扩展字段5")
    private String addition5;

    @ApiModelProperty(value = "扩展字段6")
    private String addition6;

    @ApiModelProperty(value = "扩展字段7")
    private String addition7;

    @ApiModelProperty(value = "扩展字段8")
    private String addition8;

    @ApiModelProperty(value = "扩展字段9")
    private String addition9;

    @ApiModelProperty(value = "扩展字段10")
    private String addition10;

    @ApiModelProperty(value = "阶段剩余秒数")
    private Integer periodRemainingSeconds;

    @ApiModelProperty(value = "待自动确认时长(单位:秒).被阻塞的事件会因为前边阻塞的事件导致待确认时间延长,此该时间必须保存")
    private Integer autoConfirmTime;

    private Integer autoConfirmUsedTime;

    @ApiModelProperty(value = "可选值home,awayhome:主队away:客队")
    private String homeAway;

    @ApiModelProperty(value = "当前第几局")
    private Integer secondNumber;

    @ApiModelProperty(value = "当前盘数")
    private Integer firstNumber;

    @ApiModelProperty(value = "盘主队比分")
    private Integer homeFirstNumber;

    @ApiModelProperty(value = "盘客队比分")
    private Integer awayFirstNumber;

    @ApiModelProperty(value = "局主队比分")
    private Integer homeSecondNumber;

    @ApiModelProperty(value = "局客队比分")
    private Integer awaySecondNumber;

    @ApiModelProperty(value = "比较阶段value，对应字典：t.parent_type_id=8ANDt.addition1=体育类型;如足球对应字典：t.parent_type_id=8ANDt.addition1=1;")
    private Long matchPeriodId;

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

    private String teamNameZs;

    @ApiModelProperty(value = "当前事件对应的主队数量")
    private Integer eventHomeNumber;

    @ApiModelProperty(value = "当前事件对应的客队数量")
    private Integer eventAwayNumber;

    @ApiModelProperty(value = "第三方数据源提供的该事件id.")
    private String thirdEventId;

    @ApiModelProperty(value = "第三方赛事的id.对应third_match_info.id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "比赛在数据源中的ID")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "第三方球队id.对应third_sport_team.id")
    private Long thirdTeamId;

    @ApiModelProperty(value = "赛果事件状态：0：待确认；1：待自动确认；2：已确认；3：已暂停；4：无效；5：待二次审核中；6：二次确认；7：二次拒绝")
    private Integer status;

    @ApiModelProperty(value = "操作类型：0：创建1：启动自动确认2：自动确认3：手动确认4：赛果编辑5：赛果修正6：赛果忽略7：数据源移除8：事件取消9：确认暂停")
    private Integer operateType;

    @ApiModelProperty(value = "手动确认次数")
    private Integer confirmOperationTimes;

    private Long autoConfirmStartTimestamp;

    @ApiModelProperty(value = "预警类型：0：无1:已确认赛果变更预警2：阻塞预警")
    private Integer alertType;

    @ApiModelProperty(value = "数据来源类型：0:UOF1:ScoringFeed")
    private Integer sourceType;

    @ApiModelProperty(value = "当前事件权重")
    private Integer eventWeight;

    @ApiModelProperty(value = "商业数据源,格式如：SR,BC")
    private String commercialDatasources;

    @ApiModelProperty(value = "竞品数据源，如188,QT")
    private String competitionDatasources;

    @ApiModelProperty(value = "比分网提供的数据源.比如:QT")
    private String scoreDatasources;

    @ApiModelProperty(value = "被删除的数据源")
    private String deletedDataSourceCode;

    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "体现比分类型score_type：0：全场比分（默认），1：盘比分，2：局比分")
    private Boolean scoreType;

    @ApiModelProperty(value = "赛果确认时间")
    private Long confirmTime;

    @ApiModelProperty(value = "赛事事件审核序号")
    private Integer auditOrderNo;

    @ApiModelProperty(value = "赛事事件模板号")
    private Integer templateNo;

    @ApiModelProperty(value = "赛事事件顺序号")
    private Integer eventOrder;

    @ApiModelProperty(value = "是否符合玩法显式审核：0:非显式审核1:玩法赛果显式审核")
    private Boolean auditType;

    @ApiModelProperty(value = "事件模板ID")
    private Long templateId;

    @ApiModelProperty(value = "事件模板内容")
    private String templateText;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchResultId() {
        return matchResultId;
    }

    public void setMatchResultId(Long matchResultId) {
        this.matchResultId = matchResultId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
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

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getAddition1() {
        return addition1;
    }

    public void setAddition1(String addition1) {
        this.addition1 = addition1;
    }

    public String getAddition2() {
        return addition2;
    }

    public void setAddition2(String addition2) {
        this.addition2 = addition2;
    }

    public String getAddition3() {
        return addition3;
    }

    public void setAddition3(String addition3) {
        this.addition3 = addition3;
    }

    public String getAddition4() {
        return addition4;
    }

    public void setAddition4(String addition4) {
        this.addition4 = addition4;
    }

    public String getAddition5() {
        return addition5;
    }

    public void setAddition5(String addition5) {
        this.addition5 = addition5;
    }

    public String getAddition6() {
        return addition6;
    }

    public void setAddition6(String addition6) {
        this.addition6 = addition6;
    }

    public String getAddition7() {
        return addition7;
    }

    public void setAddition7(String addition7) {
        this.addition7 = addition7;
    }

    public String getAddition8() {
        return addition8;
    }

    public void setAddition8(String addition8) {
        this.addition8 = addition8;
    }

    public String getAddition9() {
        return addition9;
    }

    public void setAddition9(String addition9) {
        this.addition9 = addition9;
    }

    public String getAddition10() {
        return addition10;
    }

    public void setAddition10(String addition10) {
        this.addition10 = addition10;
    }

    public Integer getPeriodRemainingSeconds() {
        return periodRemainingSeconds;
    }

    public void setPeriodRemainingSeconds(Integer periodRemainingSeconds) {
        this.periodRemainingSeconds = periodRemainingSeconds;
    }

    public Integer getAutoConfirmTime() {
        return autoConfirmTime;
    }

    public void setAutoConfirmTime(Integer autoConfirmTime) {
        this.autoConfirmTime = autoConfirmTime;
    }

    public Integer getAutoConfirmUsedTime() {
        return autoConfirmUsedTime;
    }

    public void setAutoConfirmUsedTime(Integer autoConfirmUsedTime) {
        this.autoConfirmUsedTime = autoConfirmUsedTime;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Integer getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(Integer secondNumber) {
        this.secondNumber = secondNumber;
    }

    public Integer getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(Integer firstNumber) {
        this.firstNumber = firstNumber;
    }

    public Integer getHomeFirstNumber() {
        return homeFirstNumber;
    }

    public void setHomeFirstNumber(Integer homeFirstNumber) {
        this.homeFirstNumber = homeFirstNumber;
    }

    public Integer getAwayFirstNumber() {
        return awayFirstNumber;
    }

    public void setAwayFirstNumber(Integer awayFirstNumber) {
        this.awayFirstNumber = awayFirstNumber;
    }

    public Integer getHomeSecondNumber() {
        return homeSecondNumber;
    }

    public void setHomeSecondNumber(Integer homeSecondNumber) {
        this.homeSecondNumber = homeSecondNumber;
    }

    public Integer getAwaySecondNumber() {
        return awaySecondNumber;
    }

    public void setAwaySecondNumber(Integer awaySecondNumber) {
        this.awaySecondNumber = awaySecondNumber;
    }

    public Long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(Long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
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

    public String getTeamNameZs() {
        return teamNameZs;
    }

    public void setTeamNameZs(String teamNameZs) {
        this.teamNameZs = teamNameZs;
    }

    public Integer getEventHomeNumber() {
        return eventHomeNumber;
    }

    public void setEventHomeNumber(Integer eventHomeNumber) {
        this.eventHomeNumber = eventHomeNumber;
    }

    public Integer getEventAwayNumber() {
        return eventAwayNumber;
    }

    public void setEventAwayNumber(Integer eventAwayNumber) {
        this.eventAwayNumber = eventAwayNumber;
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

    public Integer getConfirmOperationTimes() {
        return confirmOperationTimes;
    }

    public void setConfirmOperationTimes(Integer confirmOperationTimes) {
        this.confirmOperationTimes = confirmOperationTimes;
    }

    public Long getAutoConfirmStartTimestamp() {
        return autoConfirmStartTimestamp;
    }

    public void setAutoConfirmStartTimestamp(Long autoConfirmStartTimestamp) {
        this.autoConfirmStartTimestamp = autoConfirmStartTimestamp;
    }

    public Integer getAlertType() {
        return alertType;
    }

    public void setAlertType(Integer alertType) {
        this.alertType = alertType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getEventWeight() {
        return eventWeight;
    }

    public void setEventWeight(Integer eventWeight) {
        this.eventWeight = eventWeight;
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

    public String getScoreDatasources() {
        return scoreDatasources;
    }

    public void setScoreDatasources(String scoreDatasources) {
        this.scoreDatasources = scoreDatasources;
    }

    public String getDeletedDataSourceCode() {
        return deletedDataSourceCode;
    }

    public void setDeletedDataSourceCode(String deletedDataSourceCode) {
        this.deletedDataSourceCode = deletedDataSourceCode;
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

    public Boolean getScoreType() {
        return scoreType;
    }

    public void setScoreType(Boolean scoreType) {
        this.scoreType = scoreType;
    }

    public Long getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Long confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Integer getAuditOrderNo() {
        return auditOrderNo;
    }

    public void setAuditOrderNo(Integer auditOrderNo) {
        this.auditOrderNo = auditOrderNo;
    }

    public Integer getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(Integer templateNo) {
        this.templateNo = templateNo;
    }

    public Integer getEventOrder() {
        return eventOrder;
    }

    public void setEventOrder(Integer eventOrder) {
        this.eventOrder = eventOrder;
    }

    public Boolean getAuditType() {
        return auditType;
    }

    public void setAuditType(Boolean auditType) {
        this.auditType = auditType;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", matchResultId=").append(matchResultId);
        sb.append(", sportId=").append(sportId);
        sb.append(", canceled=").append(canceled);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", eventName=").append(eventName);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", extraInfo=").append(extraInfo);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", addition6=").append(addition6);
        sb.append(", addition7=").append(addition7);
        sb.append(", addition8=").append(addition8);
        sb.append(", addition9=").append(addition9);
        sb.append(", addition10=").append(addition10);
        sb.append(", periodRemainingSeconds=").append(periodRemainingSeconds);
        sb.append(", autoConfirmTime=").append(autoConfirmTime);
        sb.append(", autoConfirmUsedTime=").append(autoConfirmUsedTime);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", secondNumber=").append(secondNumber);
        sb.append(", firstNumber=").append(firstNumber);
        sb.append(", homeFirstNumber=").append(homeFirstNumber);
        sb.append(", awayFirstNumber=").append(awayFirstNumber);
        sb.append(", homeSecondNumber=").append(homeSecondNumber);
        sb.append(", awaySecondNumber=").append(awaySecondNumber);
        sb.append(", matchPeriodId=").append(matchPeriodId);
        sb.append(", player1Id=").append(player1Id);
        sb.append(", player1Name=").append(player1Name);
        sb.append(", player2Id=").append(player2Id);
        sb.append(", player2Name=").append(player2Name);
        sb.append(", secondsFromStart=").append(secondsFromStart);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", standardTeamId=").append(standardTeamId);
        sb.append(", teamNameZs=").append(teamNameZs);
        sb.append(", eventHomeNumber=").append(eventHomeNumber);
        sb.append(", eventAwayNumber=").append(eventAwayNumber);
        sb.append(", thirdEventId=").append(thirdEventId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", thirdTeamId=").append(thirdTeamId);
        sb.append(", status=").append(status);
        sb.append(", operateType=").append(operateType);
        sb.append(", confirmOperationTimes=").append(confirmOperationTimes);
        sb.append(", autoConfirmStartTimestamp=").append(autoConfirmStartTimestamp);
        sb.append(", alertType=").append(alertType);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", eventWeight=").append(eventWeight);
        sb.append(", commercialDatasources=").append(commercialDatasources);
        sb.append(", competitionDatasources=").append(competitionDatasources);
        sb.append(", scoreDatasources=").append(scoreDatasources);
        sb.append(", deletedDataSourceCode=").append(deletedDataSourceCode);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", scoreType=").append(scoreType);
        sb.append(", confirmTime=").append(confirmTime);
        sb.append(", auditOrderNo=").append(auditOrderNo);
        sb.append(", templateNo=").append(templateNo);
        sb.append(", eventOrder=").append(eventOrder);
        sb.append(", auditType=").append(auditType);
        sb.append(", templateId=").append(templateId);
        sb.append(", templateText=").append(templateText);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}