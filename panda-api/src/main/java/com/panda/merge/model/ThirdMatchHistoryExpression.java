package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class ThirdMatchHistoryExpression implements Serializable {
    @ApiModelProperty(value = "三方数据源联赛ID+三方数据源球队ID+数据来源+数据类型+运动类型")
    private String id;

    @ApiModelProperty(value = "三方数据源联赛ID")
    private String thirdTournamentSourceId;

    @ApiModelProperty(value = "三方数据源球队ID")
    private String thirdTeamSourceId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "球队中文名称")
    private String teamCnName;

    @ApiModelProperty(value = "球队英文名称")
    private String teamEnName;

    @ApiModelProperty(value = "0:自动1:手动")
    private Integer editStatus;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "联赛表现排名:如10/20")
    private String expressionRanking;

    @ApiModelProperty(value = "数据类型,0:总体1:主队2:客队")
    private Integer expressingType;

    @ApiModelProperty(value = "最近第1场赛事状态,0:赢1:平2:输")
    private Integer firstStatus;

    @ApiModelProperty(value = "最近第2场赛事状态,0:赢1:平2:输")
    private Integer secondStatus;

    @ApiModelProperty(value = "最近第3场赛事状态,0:赢1:平2:输")
    private Integer thirdStatus;

    @ApiModelProperty(value = "最近第4场赛事状态,0:赢1:平2:输")
    private Integer fourthStatus;

    @ApiModelProperty(value = "最近第5场赛事状态,0:赢1:平2:输")
    private Integer fifthStatus;

    @ApiModelProperty(value = "最近5场进球数")
    private Integer goalsForTotal;

    @ApiModelProperty(value = "最近5场均进球数")
    private BigDecimal averageGoal;

    @ApiModelProperty(value = "最近5场赢球占比")
    private BigDecimal winPercent;

    @ApiModelProperty(value = "两队都得分占比")
    private BigDecimal bothGoalPercent;

    @ApiModelProperty(value = "没有失球占比")
    private BigDecimal notLostPercent;

    @ApiModelProperty(value = "第一队入球占比")
    private BigDecimal firstGoalPercent;

    @ApiModelProperty(value = "平均进球占比")
    private BigDecimal averageGoalPercent;

    @ApiModelProperty(value = "得分占比")
    private BigDecimal goalPercent;

    @ApiModelProperty(value = "失球占比")
    private BigDecimal lostGoalPercent;

    @ApiModelProperty(value = "xG")
    private BigDecimal goalXg;

    @ApiModelProperty(value = "xGA")
    private BigDecimal goalXga;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThirdTournamentSourceId() {
        return thirdTournamentSourceId;
    }

    public void setThirdTournamentSourceId(String thirdTournamentSourceId) {
        this.thirdTournamentSourceId = thirdTournamentSourceId;
    }

    public String getThirdTeamSourceId() {
        return thirdTeamSourceId;
    }

    public void setThirdTeamSourceId(String thirdTeamSourceId) {
        this.thirdTeamSourceId = thirdTeamSourceId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getTeamCnName() {
        return teamCnName;
    }

    public void setTeamCnName(String teamCnName) {
        this.teamCnName = teamCnName;
    }

    public String getTeamEnName() {
        return teamEnName;
    }

    public void setTeamEnName(String teamEnName) {
        this.teamEnName = teamEnName;
    }

    public Integer getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(Integer editStatus) {
        this.editStatus = editStatus;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getExpressionRanking() {
        return expressionRanking;
    }

    public void setExpressionRanking(String expressionRanking) {
        this.expressionRanking = expressionRanking;
    }

    public Integer getExpressingType() {
        return expressingType;
    }

    public void setExpressingType(Integer expressingType) {
        this.expressingType = expressingType;
    }

    public Integer getFirstStatus() {
        return firstStatus;
    }

    public void setFirstStatus(Integer firstStatus) {
        this.firstStatus = firstStatus;
    }

    public Integer getSecondStatus() {
        return secondStatus;
    }

    public void setSecondStatus(Integer secondStatus) {
        this.secondStatus = secondStatus;
    }

    public Integer getThirdStatus() {
        return thirdStatus;
    }

    public void setThirdStatus(Integer thirdStatus) {
        this.thirdStatus = thirdStatus;
    }

    public Integer getFourthStatus() {
        return fourthStatus;
    }

    public void setFourthStatus(Integer fourthStatus) {
        this.fourthStatus = fourthStatus;
    }

    public Integer getFifthStatus() {
        return fifthStatus;
    }

    public void setFifthStatus(Integer fifthStatus) {
        this.fifthStatus = fifthStatus;
    }

    public Integer getGoalsForTotal() {
        return goalsForTotal;
    }

    public void setGoalsForTotal(Integer goalsForTotal) {
        this.goalsForTotal = goalsForTotal;
    }

    public BigDecimal getAverageGoal() {
        return averageGoal;
    }

    public void setAverageGoal(BigDecimal averageGoal) {
        this.averageGoal = averageGoal;
    }

    public BigDecimal getWinPercent() {
        return winPercent;
    }

    public void setWinPercent(BigDecimal winPercent) {
        this.winPercent = winPercent;
    }

    public BigDecimal getBothGoalPercent() {
        return bothGoalPercent;
    }

    public void setBothGoalPercent(BigDecimal bothGoalPercent) {
        this.bothGoalPercent = bothGoalPercent;
    }

    public BigDecimal getNotLostPercent() {
        return notLostPercent;
    }

    public void setNotLostPercent(BigDecimal notLostPercent) {
        this.notLostPercent = notLostPercent;
    }

    public BigDecimal getFirstGoalPercent() {
        return firstGoalPercent;
    }

    public void setFirstGoalPercent(BigDecimal firstGoalPercent) {
        this.firstGoalPercent = firstGoalPercent;
    }

    public BigDecimal getAverageGoalPercent() {
        return averageGoalPercent;
    }

    public void setAverageGoalPercent(BigDecimal averageGoalPercent) {
        this.averageGoalPercent = averageGoalPercent;
    }

    public BigDecimal getGoalPercent() {
        return goalPercent;
    }

    public void setGoalPercent(BigDecimal goalPercent) {
        this.goalPercent = goalPercent;
    }

    public BigDecimal getLostGoalPercent() {
        return lostGoalPercent;
    }

    public void setLostGoalPercent(BigDecimal lostGoalPercent) {
        this.lostGoalPercent = lostGoalPercent;
    }

    public BigDecimal getGoalXg() {
        return goalXg;
    }

    public void setGoalXg(BigDecimal goalXg) {
        this.goalXg = goalXg;
    }

    public BigDecimal getGoalXga() {
        return goalXga;
    }

    public void setGoalXga(BigDecimal goalXga) {
        this.goalXga = goalXga;
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
        sb.append(", thirdTournamentSourceId=").append(thirdTournamentSourceId);
        sb.append(", thirdTeamSourceId=").append(thirdTeamSourceId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", teamCnName=").append(teamCnName);
        sb.append(", teamEnName=").append(teamEnName);
        sb.append(", editStatus=").append(editStatus);
        sb.append(", sportId=").append(sportId);
        sb.append(", expressionRanking=").append(expressionRanking);
        sb.append(", expressingType=").append(expressingType);
        sb.append(", firstStatus=").append(firstStatus);
        sb.append(", secondStatus=").append(secondStatus);
        sb.append(", thirdStatus=").append(thirdStatus);
        sb.append(", fourthStatus=").append(fourthStatus);
        sb.append(", fifthStatus=").append(fifthStatus);
        sb.append(", goalsForTotal=").append(goalsForTotal);
        sb.append(", averageGoal=").append(averageGoal);
        sb.append(", winPercent=").append(winPercent);
        sb.append(", bothGoalPercent=").append(bothGoalPercent);
        sb.append(", notLostPercent=").append(notLostPercent);
        sb.append(", firstGoalPercent=").append(firstGoalPercent);
        sb.append(", averageGoalPercent=").append(averageGoalPercent);
        sb.append(", goalPercent=").append(goalPercent);
        sb.append(", lostGoalPercent=").append(lostGoalPercent);
        sb.append(", goalXg=").append(goalXg);
        sb.append(", goalXga=").append(goalXga);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}