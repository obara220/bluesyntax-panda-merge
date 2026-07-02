package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class ThirdSportTeamRanking implements Serializable {
    @ApiModelProperty(value = "三方数据源赛季ID+榜单ID+球队ID")
    private String id;

    @ApiModelProperty(value = "三方数据源联赛ID")
    private String thirdTournamentSourceId;

    @ApiModelProperty(value = "0:自动1:手动")
    private Boolean editStatus;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "三方数据源赛季ID")
    private String thirdSourceSeasonId;

    @ApiModelProperty(value = "三方数据源赛季名称")
    private String thirdSourceSeasonName;

    @ApiModelProperty(value = "三方数据源赛季开始时间")
    private Date thirdSourceSeasonBeginTime;

    @ApiModelProperty(value = "三方数据源赛季结束始时间")
    private Date thirdSourceSeasonEndTime;

    @ApiModelProperty(value = "榜单ID")
    private String rankingId;

    @ApiModelProperty(value = "榜单中文名称")
    private String rankingCnName;

    @ApiModelProperty(value = "榜单英文名称")
    private String rankingEnName;

    @ApiModelProperty(value = "参数场数")
    private Integer matchCount;

    @ApiModelProperty(value = "三方数据源球队ID")
    private String thirdTeamSourceId;

    @ApiModelProperty(value = "球队中文名称")
    private String teamCnName;

    @ApiModelProperty(value = "球队英文名称")
    private String teamEnName;

    @ApiModelProperty(value = "球队logo")
    private String teamLogo;

    @ApiModelProperty(value = "排名值")
    private Integer positionTotal;

    @ApiModelProperty(value = "胜场数")
    private Integer winTotal;

    @ApiModelProperty(value = "平局数")
    private Integer drawTotal;

    @ApiModelProperty(value = "负场数")
    private Integer lossTotal;

    @ApiModelProperty(value = "积分数")
    private Integer pointsTotal;

    @ApiModelProperty(value = "进球数")
    private Integer goalsForTotal;

    @ApiModelProperty(value = "失球数")
    private Integer goalsAgainstTotal;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "净胜球数")
    private Integer goalDiffTotal;

    @ApiModelProperty(value = "组ID")
    private String groupId;

    @ApiModelProperty(value = "组名称")
    private String groupCnName;

    @ApiModelProperty(value = "联赛类别(0:其他,1联赛,2杯赛)")
    private Integer tournamentType;

    @ApiModelProperty(value = "小组赛冠军赛事盘口投注id")
    private String winnerMarketOddsid;

    @ApiModelProperty(value = "小组赛晋级赛事盘口投注id")
    private String advanceMarketOddsid;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "是否失效(0:否,1:是)")
    private Integer invalid;

    @ApiModelProperty(value = "赛事总数")
    private Long totalMatches;

    @ApiModelProperty(value = "已完成赛事数")
    private Long matchesCompleted;

    @ApiModelProperty(value = "晋级中文名")
    private String promotionCnName;

    @ApiModelProperty(value = "晋级中英名")
    private String promotionEnName;

    @ApiModelProperty(value = "晋级id")
    private String promotionId;

    @ApiModelProperty(value = "主场场次")
    private Integer homeMatchesTotal;

    @ApiModelProperty(value = "客场场次")
    private Integer awayMatchesTotal;

    @ApiModelProperty(value = "篮球近10场胜")
    private Integer winLast10;

    @ApiModelProperty(value = "篮球近10场负")
    private Integer lossLast10;

    @ApiModelProperty(value = "连续战绩:+连胜,-连败")
    private Integer streak;

    @ApiModelProperty(value = "胜率")
    private String winPctTotal;

    @ApiModelProperty(value = "胜场差")
    private String gameBehind;

    @ApiModelProperty(value = "是否当前赛季(0:否,1:是)")
    private Integer isCurrentSeason;

    @ApiModelProperty(value = "球队最近5场战绩JOSN字符串（[{“id”:'赛事ID'，“winner”:“胜平负（WDL）”},...]）")
    private String record5;

    @ApiModelProperty(value = "榜单明星球员JOSN字符串（[{'player_id':'数据源球员ID','player_logo':'数据源球员logo','zs':'中文名称','en':'英文名称','position':'位置'},...]）")
    private String starPlayers;

    @ApiModelProperty(value = "球队多语言JOSN字符串（{'zs':'简体','zh':'繁体','en':'英文'}）")
    private String teamNames;

    @ApiModelProperty(value = "分组多语言JOSN字符串（{'zs':'简体','zh':'繁体','en':'英文'}）")
    private String groupNames;

    @ApiModelProperty(value = "教练信息JOSN字符串（{'coach_logo':'教练logo','zs':'中文名称','en':'英文名称''}）")
    private String coachInfo;

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

    public Boolean getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(Boolean editStatus) {
        this.editStatus = editStatus;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getThirdSourceSeasonId() {
        return thirdSourceSeasonId;
    }

    public void setThirdSourceSeasonId(String thirdSourceSeasonId) {
        this.thirdSourceSeasonId = thirdSourceSeasonId;
    }

    public String getThirdSourceSeasonName() {
        return thirdSourceSeasonName;
    }

    public void setThirdSourceSeasonName(String thirdSourceSeasonName) {
        this.thirdSourceSeasonName = thirdSourceSeasonName;
    }

    public Date getThirdSourceSeasonBeginTime() {
        return thirdSourceSeasonBeginTime;
    }

    public void setThirdSourceSeasonBeginTime(Date thirdSourceSeasonBeginTime) {
        this.thirdSourceSeasonBeginTime = thirdSourceSeasonBeginTime;
    }

    public Date getThirdSourceSeasonEndTime() {
        return thirdSourceSeasonEndTime;
    }

    public void setThirdSourceSeasonEndTime(Date thirdSourceSeasonEndTime) {
        this.thirdSourceSeasonEndTime = thirdSourceSeasonEndTime;
    }

    public String getRankingId() {
        return rankingId;
    }

    public void setRankingId(String rankingId) {
        this.rankingId = rankingId;
    }

    public String getRankingCnName() {
        return rankingCnName;
    }

    public void setRankingCnName(String rankingCnName) {
        this.rankingCnName = rankingCnName;
    }

    public String getRankingEnName() {
        return rankingEnName;
    }

    public void setRankingEnName(String rankingEnName) {
        this.rankingEnName = rankingEnName;
    }

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }

    public String getThirdTeamSourceId() {
        return thirdTeamSourceId;
    }

    public void setThirdTeamSourceId(String thirdTeamSourceId) {
        this.thirdTeamSourceId = thirdTeamSourceId;
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

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public Integer getPositionTotal() {
        return positionTotal;
    }

    public void setPositionTotal(Integer positionTotal) {
        this.positionTotal = positionTotal;
    }

    public Integer getWinTotal() {
        return winTotal;
    }

    public void setWinTotal(Integer winTotal) {
        this.winTotal = winTotal;
    }

    public Integer getDrawTotal() {
        return drawTotal;
    }

    public void setDrawTotal(Integer drawTotal) {
        this.drawTotal = drawTotal;
    }

    public Integer getLossTotal() {
        return lossTotal;
    }

    public void setLossTotal(Integer lossTotal) {
        this.lossTotal = lossTotal;
    }

    public Integer getPointsTotal() {
        return pointsTotal;
    }

    public void setPointsTotal(Integer pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public Integer getGoalsForTotal() {
        return goalsForTotal;
    }

    public void setGoalsForTotal(Integer goalsForTotal) {
        this.goalsForTotal = goalsForTotal;
    }

    public Integer getGoalsAgainstTotal() {
        return goalsAgainstTotal;
    }

    public void setGoalsAgainstTotal(Integer goalsAgainstTotal) {
        this.goalsAgainstTotal = goalsAgainstTotal;
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

    public Integer getGoalDiffTotal() {
        return goalDiffTotal;
    }

    public void setGoalDiffTotal(Integer goalDiffTotal) {
        this.goalDiffTotal = goalDiffTotal;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupCnName() {
        return groupCnName;
    }

    public void setGroupCnName(String groupCnName) {
        this.groupCnName = groupCnName;
    }

    public Integer getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(Integer tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getWinnerMarketOddsid() {
        return winnerMarketOddsid;
    }

    public void setWinnerMarketOddsid(String winnerMarketOddsid) {
        this.winnerMarketOddsid = winnerMarketOddsid;
    }

    public String getAdvanceMarketOddsid() {
        return advanceMarketOddsid;
    }

    public void setAdvanceMarketOddsid(String advanceMarketOddsid) {
        this.advanceMarketOddsid = advanceMarketOddsid;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
    }

    public Long getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(Long totalMatches) {
        this.totalMatches = totalMatches;
    }

    public Long getMatchesCompleted() {
        return matchesCompleted;
    }

    public void setMatchesCompleted(Long matchesCompleted) {
        this.matchesCompleted = matchesCompleted;
    }

    public String getPromotionCnName() {
        return promotionCnName;
    }

    public void setPromotionCnName(String promotionCnName) {
        this.promotionCnName = promotionCnName;
    }

    public String getPromotionEnName() {
        return promotionEnName;
    }

    public void setPromotionEnName(String promotionEnName) {
        this.promotionEnName = promotionEnName;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getHomeMatchesTotal() {
        return homeMatchesTotal;
    }

    public void setHomeMatchesTotal(Integer homeMatchesTotal) {
        this.homeMatchesTotal = homeMatchesTotal;
    }

    public Integer getAwayMatchesTotal() {
        return awayMatchesTotal;
    }

    public void setAwayMatchesTotal(Integer awayMatchesTotal) {
        this.awayMatchesTotal = awayMatchesTotal;
    }

    public Integer getWinLast10() {
        return winLast10;
    }

    public void setWinLast10(Integer winLast10) {
        this.winLast10 = winLast10;
    }

    public Integer getLossLast10() {
        return lossLast10;
    }

    public void setLossLast10(Integer lossLast10) {
        this.lossLast10 = lossLast10;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }

    public String getWinPctTotal() {
        return winPctTotal;
    }

    public void setWinPctTotal(String winPctTotal) {
        this.winPctTotal = winPctTotal;
    }

    public String getGameBehind() {
        return gameBehind;
    }

    public void setGameBehind(String gameBehind) {
        this.gameBehind = gameBehind;
    }

    public Integer getIsCurrentSeason() {
        return isCurrentSeason;
    }

    public void setIsCurrentSeason(Integer isCurrentSeason) {
        this.isCurrentSeason = isCurrentSeason;
    }

    public String getRecord5() {
        return record5;
    }

    public void setRecord5(String record5) {
        this.record5 = record5;
    }

    public String getStarPlayers() {
        return starPlayers;
    }

    public void setStarPlayers(String starPlayers) {
        this.starPlayers = starPlayers;
    }

    public String getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(String teamNames) {
        this.teamNames = teamNames;
    }

    public String getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String groupNames) {
        this.groupNames = groupNames;
    }

    public String getCoachInfo() {
        return coachInfo;
    }

    public void setCoachInfo(String coachInfo) {
        this.coachInfo = coachInfo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", thirdTournamentSourceId=").append(thirdTournamentSourceId);
        sb.append(", editStatus=").append(editStatus);
        sb.append(", sportId=").append(sportId);
        sb.append(", thirdSourceSeasonId=").append(thirdSourceSeasonId);
        sb.append(", thirdSourceSeasonName=").append(thirdSourceSeasonName);
        sb.append(", thirdSourceSeasonBeginTime=").append(thirdSourceSeasonBeginTime);
        sb.append(", thirdSourceSeasonEndTime=").append(thirdSourceSeasonEndTime);
        sb.append(", rankingId=").append(rankingId);
        sb.append(", rankingCnName=").append(rankingCnName);
        sb.append(", rankingEnName=").append(rankingEnName);
        sb.append(", matchCount=").append(matchCount);
        sb.append(", thirdTeamSourceId=").append(thirdTeamSourceId);
        sb.append(", teamCnName=").append(teamCnName);
        sb.append(", teamEnName=").append(teamEnName);
        sb.append(", teamLogo=").append(teamLogo);
        sb.append(", positionTotal=").append(positionTotal);
        sb.append(", winTotal=").append(winTotal);
        sb.append(", drawTotal=").append(drawTotal);
        sb.append(", lossTotal=").append(lossTotal);
        sb.append(", pointsTotal=").append(pointsTotal);
        sb.append(", goalsForTotal=").append(goalsForTotal);
        sb.append(", goalsAgainstTotal=").append(goalsAgainstTotal);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", goalDiffTotal=").append(goalDiffTotal);
        sb.append(", groupId=").append(groupId);
        sb.append(", groupCnName=").append(groupCnName);
        sb.append(", tournamentType=").append(tournamentType);
        sb.append(", winnerMarketOddsid=").append(winnerMarketOddsid);
        sb.append(", advanceMarketOddsid=").append(advanceMarketOddsid);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", invalid=").append(invalid);
        sb.append(", totalMatches=").append(totalMatches);
        sb.append(", matchesCompleted=").append(matchesCompleted);
        sb.append(", promotionCnName=").append(promotionCnName);
        sb.append(", promotionEnName=").append(promotionEnName);
        sb.append(", promotionId=").append(promotionId);
        sb.append(", homeMatchesTotal=").append(homeMatchesTotal);
        sb.append(", awayMatchesTotal=").append(awayMatchesTotal);
        sb.append(", winLast10=").append(winLast10);
        sb.append(", lossLast10=").append(lossLast10);
        sb.append(", streak=").append(streak);
        sb.append(", winPctTotal=").append(winPctTotal);
        sb.append(", gameBehind=").append(gameBehind);
        sb.append(", isCurrentSeason=").append(isCurrentSeason);
        sb.append(", record5=").append(record5);
        sb.append(", starPlayers=").append(starPlayers);
        sb.append(", teamNames=").append(teamNames);
        sb.append(", groupNames=").append(groupNames);
        sb.append(", coachInfo=").append(coachInfo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}