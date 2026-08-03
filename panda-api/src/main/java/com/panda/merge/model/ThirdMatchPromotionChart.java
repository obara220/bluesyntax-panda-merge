package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class ThirdMatchPromotionChart implements Serializable {
    @ApiModelProperty(value = "数据来源ID:源赛季ID:系列赛ID")
    private String id;

    @ApiModelProperty(value = "源联赛ID")
    private String tournamentId;

    @ApiModelProperty(value = "源赛季ID")
    private String seasonId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "中文榜单名称")
    private String cnName;

    @ApiModelProperty(value = "英文榜单名称")
    private String enName;

    @ApiModelProperty(value = "组ID")
    private Long groupId;

    @ApiModelProperty(value = "系列赛ID")
    private String seriesId;

    @ApiModelProperty(value = "系列赛开始时间")
    private Date beginTime;

    @ApiModelProperty(value = "队伍1的ID(主队)")
    private String team1Id;

    @ApiModelProperty(value = "队伍2的ID(客队)")
    private String team2Id;

    @ApiModelProperty(value = "主队名称，通常在没有队伍1ID的时候，请显示该名称，那时候该名称将表示资格名单编号")
    private String team1Name;

    @ApiModelProperty(value = "客队名称，通常在没有队伍2ID的时候，请显示该名称，那时候该名称将表示资格名单编号")
    private String team2Name;

    @ApiModelProperty(value = "主队得分(1(5)表示全场:点球大战)")
    private String team1Score;

    @ApiModelProperty(value = "主队得分(1(5)表示全场:点球大战)")
    private String team2Score;

    @ApiModelProperty(value = "队伍1从哪个系列赛来,系列赛ID（仅对双败淘汰赛）")
    private Integer team1ComeFrom;

    @ApiModelProperty(value = "队伍2从哪个系列赛来,系列赛ID（仅对双败淘汰赛）")
    private Integer team2ComeFrom;

    @ApiModelProperty(value = "该系列赛包含的比赛ID列表,多个比赛ID用逗号隔开")
    private String matchIds;

    @ApiModelProperty(value = "轮次序号,从右边数,1开始")
    private Integer roundOrder;

    @ApiModelProperty(value = "纵向序号,从上至下,1开始")
    private Integer lineOrder;

    @ApiModelProperty(value = "双败淘汰赛组别(1.胜者组,2.败者组,3.决赛)")
    private Integer doubleEliminationGroup;

    @ApiModelProperty(value = "系列赛状态(0.占位,1.未开始,2.进行中,3.完成)")
    private Integer status;

    @ApiModelProperty(value = "胜利者(1:主,2:客)")
    private Integer winner;

    @ApiModelProperty(value = "轮次文字描述")
    private String roundDescription;

    @ApiModelProperty(value = "上一级系列赛的ID")
    private Integer parentId;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "源赛事ID")
    private String matchId;

    @ApiModelProperty(value = "是否失效(0:否,1:是)")
    private Integer invalid;

    @ApiModelProperty(value = "主队标准时间内得分足篮球：主队标准时间内的分、足球90分钟，篮球40/48分钟")
    private String homeTeamNormalTimeScore;

    @ApiModelProperty(value = "客队标准时间内得分足篮球：客队标准时间内的分、足球90分钟，篮球40/48分钟")
    private String awayTeamNormalTimeScore;

    @ApiModelProperty(value = "足球主队加时赛上半场得分")
    private String homeExtraTimeFirstHalfScore;

    @ApiModelProperty(value = "足球客队加时赛上半场得分")
    private String awayExtraTimeFirstHalfScore;

    @ApiModelProperty(value = "足球主队加时赛下半场得分")
    private String homeExtraTimeSecondHalfScore;

    @ApiModelProperty(value = "足球客队加时赛下半场得分")
    private String awayExtraTimeSecondHalfScore;

    @ApiModelProperty(value = "主队标准时间内得分足篮球：主队标准时间内的分")
    private String homeTeamHalfTimeScore;

    @ApiModelProperty(value = "客队标准时间内得分足篮球：客队标准时间内的分")
    private String awayTeamHalfTimeScore;

    @ApiModelProperty(value = "是否当前赛季(0:否,1:是)")
    private Integer isCurrentSeason;

    @ApiModelProperty(value = "轮次文字描述（中文）")
    private String zsRoundDescription;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name(String team1Name) {
        this.team1Name = team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public void setTeam2Name(String team2Name) {
        this.team2Name = team2Name;
    }

    public String getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(String team1Score) {
        this.team1Score = team1Score;
    }

    public String getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(String team2Score) {
        this.team2Score = team2Score;
    }

    public Integer getTeam1ComeFrom() {
        return team1ComeFrom;
    }

    public void setTeam1ComeFrom(Integer team1ComeFrom) {
        this.team1ComeFrom = team1ComeFrom;
    }

    public Integer getTeam2ComeFrom() {
        return team2ComeFrom;
    }

    public void setTeam2ComeFrom(Integer team2ComeFrom) {
        this.team2ComeFrom = team2ComeFrom;
    }

    public String getMatchIds() {
        return matchIds;
    }

    public void setMatchIds(String matchIds) {
        this.matchIds = matchIds;
    }

    public Integer getRoundOrder() {
        return roundOrder;
    }

    public void setRoundOrder(Integer roundOrder) {
        this.roundOrder = roundOrder;
    }

    public Integer getLineOrder() {
        return lineOrder;
    }

    public void setLineOrder(Integer lineOrder) {
        this.lineOrder = lineOrder;
    }

    public Integer getDoubleEliminationGroup() {
        return doubleEliminationGroup;
    }

    public void setDoubleEliminationGroup(Integer doubleEliminationGroup) {
        this.doubleEliminationGroup = doubleEliminationGroup;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWinner() {
        return winner;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public String getRoundDescription() {
        return roundDescription;
    }

    public void setRoundDescription(String roundDescription) {
        this.roundDescription = roundDescription;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
    }

    public String getHomeTeamNormalTimeScore() {
        return homeTeamNormalTimeScore;
    }

    public void setHomeTeamNormalTimeScore(String homeTeamNormalTimeScore) {
        this.homeTeamNormalTimeScore = homeTeamNormalTimeScore;
    }

    public String getAwayTeamNormalTimeScore() {
        return awayTeamNormalTimeScore;
    }

    public void setAwayTeamNormalTimeScore(String awayTeamNormalTimeScore) {
        this.awayTeamNormalTimeScore = awayTeamNormalTimeScore;
    }

    public String getHomeExtraTimeFirstHalfScore() {
        return homeExtraTimeFirstHalfScore;
    }

    public void setHomeExtraTimeFirstHalfScore(String homeExtraTimeFirstHalfScore) {
        this.homeExtraTimeFirstHalfScore = homeExtraTimeFirstHalfScore;
    }

    public String getAwayExtraTimeFirstHalfScore() {
        return awayExtraTimeFirstHalfScore;
    }

    public void setAwayExtraTimeFirstHalfScore(String awayExtraTimeFirstHalfScore) {
        this.awayExtraTimeFirstHalfScore = awayExtraTimeFirstHalfScore;
    }

    public String getHomeExtraTimeSecondHalfScore() {
        return homeExtraTimeSecondHalfScore;
    }

    public void setHomeExtraTimeSecondHalfScore(String homeExtraTimeSecondHalfScore) {
        this.homeExtraTimeSecondHalfScore = homeExtraTimeSecondHalfScore;
    }

    public String getAwayExtraTimeSecondHalfScore() {
        return awayExtraTimeSecondHalfScore;
    }

    public void setAwayExtraTimeSecondHalfScore(String awayExtraTimeSecondHalfScore) {
        this.awayExtraTimeSecondHalfScore = awayExtraTimeSecondHalfScore;
    }

    public String getHomeTeamHalfTimeScore() {
        return homeTeamHalfTimeScore;
    }

    public void setHomeTeamHalfTimeScore(String homeTeamHalfTimeScore) {
        this.homeTeamHalfTimeScore = homeTeamHalfTimeScore;
    }

    public String getAwayTeamHalfTimeScore() {
        return awayTeamHalfTimeScore;
    }

    public void setAwayTeamHalfTimeScore(String awayTeamHalfTimeScore) {
        this.awayTeamHalfTimeScore = awayTeamHalfTimeScore;
    }

    public Integer getIsCurrentSeason() {
        return isCurrentSeason;
    }

    public void setIsCurrentSeason(Integer isCurrentSeason) {
        this.isCurrentSeason = isCurrentSeason;
    }

    public String getZsRoundDescription() {
        return zsRoundDescription;
    }

    public void setZsRoundDescription(String zsRoundDescription) {
        this.zsRoundDescription = zsRoundDescription;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", tournamentId=").append(tournamentId);
        sb.append(", seasonId=").append(seasonId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", sportId=").append(sportId);
        sb.append(", cnName=").append(cnName);
        sb.append(", enName=").append(enName);
        sb.append(", groupId=").append(groupId);
        sb.append(", seriesId=").append(seriesId);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", team1Id=").append(team1Id);
        sb.append(", team2Id=").append(team2Id);
        sb.append(", team1Name=").append(team1Name);
        sb.append(", team2Name=").append(team2Name);
        sb.append(", team1Score=").append(team1Score);
        sb.append(", team2Score=").append(team2Score);
        sb.append(", team1ComeFrom=").append(team1ComeFrom);
        sb.append(", team2ComeFrom=").append(team2ComeFrom);
        sb.append(", matchIds=").append(matchIds);
        sb.append(", roundOrder=").append(roundOrder);
        sb.append(", lineOrder=").append(lineOrder);
        sb.append(", doubleEliminationGroup=").append(doubleEliminationGroup);
        sb.append(", status=").append(status);
        sb.append(", winner=").append(winner);
        sb.append(", roundDescription=").append(roundDescription);
        sb.append(", parentId=").append(parentId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", matchId=").append(matchId);
        sb.append(", invalid=").append(invalid);
        sb.append(", homeTeamNormalTimeScore=").append(homeTeamNormalTimeScore);
        sb.append(", awayTeamNormalTimeScore=").append(awayTeamNormalTimeScore);
        sb.append(", homeExtraTimeFirstHalfScore=").append(homeExtraTimeFirstHalfScore);
        sb.append(", awayExtraTimeFirstHalfScore=").append(awayExtraTimeFirstHalfScore);
        sb.append(", homeExtraTimeSecondHalfScore=").append(homeExtraTimeSecondHalfScore);
        sb.append(", awayExtraTimeSecondHalfScore=").append(awayExtraTimeSecondHalfScore);
        sb.append(", homeTeamHalfTimeScore=").append(homeTeamHalfTimeScore);
        sb.append(", awayTeamHalfTimeScore=").append(awayTeamHalfTimeScore);
        sb.append(", isCurrentSeason=").append(isCurrentSeason);
        sb.append(", zsRoundDescription=").append(zsRoundDescription);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}