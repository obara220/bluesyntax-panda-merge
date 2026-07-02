package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchHistoryStatistics implements Serializable {
    @ApiModelProperty(value = "数据源编码ID+数据源赛事id")
    private String id;

    @ApiModelProperty(value = "数据源赛事id")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "数据源联赛id")
    private String thirdTournamentSourceId;

    @ApiModelProperty(value = "数据源赛季id")
    private String thirdSeasonSourceId;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "开赛时间")
    private Long beginTime;

    @ApiModelProperty(value = "赛事状态")
    private String matchStatus;

    @ApiModelProperty(value = "数据源主队ID")
    private String homeTeamId;

    @ApiModelProperty(value = "数据源客队ID")
    private String awayTeamId;

    @ApiModelProperty(value = "主队名称")
    private String homeTeamName;

    @ApiModelProperty(value = "客队名称")
    private String awayTeamName;

    @ApiModelProperty(value = "主队得分(7:5表示全场:点球大战)")
    private String homeTeamScore;

    @ApiModelProperty(value = "客队得分(7:5表示全场:点球大战)")
    private String awayTeamScore;

    @ApiModelProperty(value = "初盘让球盘口值")
    private String handicapVal;

    @ApiModelProperty(value = "初盘大小盘口值")
    private String overUnderVal;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "是否分组赛（0：否，1：是）")
    private Integer matchGroup;

    @ApiModelProperty(value = "联赛类别(0:其他,1联赛,2杯赛)")
    private Integer tournamentType;

    @ApiModelProperty(value = "分组id对应分组信息")
    private String groupId;

    @ApiModelProperty(value = "轮次中文名示例：组A")
    private String round;

    @ApiModelProperty(value = "轮次类型中文名示例：分组赛")
    private String roundType;

    @ApiModelProperty(value = "天气")
    private String weatherDesc;

    @ApiModelProperty(value = "场馆地址")
    private String googleMapsCoordinates;

    @ApiModelProperty(value = "场馆名称")
    private String stadiumNames;

    @ApiModelProperty(value = "0:自动1:手动")
    private Integer editStatus;

    @ApiModelProperty(value = "常规赛事主队得分")
    private String homeTeamScoreD01;

    @ApiModelProperty(value = "常规赛事客队得分")
    private String awayTeamScoreD01;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public String getThirdTournamentSourceId() {
        return thirdTournamentSourceId;
    }

    public void setThirdTournamentSourceId(String thirdTournamentSourceId) {
        this.thirdTournamentSourceId = thirdTournamentSourceId;
    }

    public String getThirdSeasonSourceId() {
        return thirdSeasonSourceId;
    }

    public void setThirdSeasonSourceId(String thirdSeasonSourceId) {
        this.thirdSeasonSourceId = thirdSeasonSourceId;
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

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(String homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public String getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(String awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public String getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public String getHandicapVal() {
        return handicapVal;
    }

    public void setHandicapVal(String handicapVal) {
        this.handicapVal = handicapVal;
    }

    public String getOverUnderVal() {
        return overUnderVal;
    }

    public void setOverUnderVal(String overUnderVal) {
        this.overUnderVal = overUnderVal;
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

    public Integer getMatchGroup() {
        return matchGroup;
    }

    public void setMatchGroup(Integer matchGroup) {
        this.matchGroup = matchGroup;
    }

    public Integer getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(Integer tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getRoundType() {
        return roundType;
    }

    public void setRoundType(String roundType) {
        this.roundType = roundType;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public String getGoogleMapsCoordinates() {
        return googleMapsCoordinates;
    }

    public void setGoogleMapsCoordinates(String googleMapsCoordinates) {
        this.googleMapsCoordinates = googleMapsCoordinates;
    }

    public String getStadiumNames() {
        return stadiumNames;
    }

    public void setStadiumNames(String stadiumNames) {
        this.stadiumNames = stadiumNames;
    }

    public Integer getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(Integer editStatus) {
        this.editStatus = editStatus;
    }

    public String getHomeTeamScoreD01() {
        return homeTeamScoreD01;
    }

    public void setHomeTeamScoreD01(String homeTeamScoreD01) {
        this.homeTeamScoreD01 = homeTeamScoreD01;
    }

    public String getAwayTeamScoreD01() {
        return awayTeamScoreD01;
    }

    public void setAwayTeamScoreD01(String awayTeamScoreD01) {
        this.awayTeamScoreD01 = awayTeamScoreD01;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", thirdTournamentSourceId=").append(thirdTournamentSourceId);
        sb.append(", thirdSeasonSourceId=").append(thirdSeasonSourceId);
        sb.append(", sportId=").append(sportId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", matchStatus=").append(matchStatus);
        sb.append(", homeTeamId=").append(homeTeamId);
        sb.append(", awayTeamId=").append(awayTeamId);
        sb.append(", homeTeamName=").append(homeTeamName);
        sb.append(", awayTeamName=").append(awayTeamName);
        sb.append(", homeTeamScore=").append(homeTeamScore);
        sb.append(", awayTeamScore=").append(awayTeamScore);
        sb.append(", handicapVal=").append(handicapVal);
        sb.append(", overUnderVal=").append(overUnderVal);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", matchGroup=").append(matchGroup);
        sb.append(", tournamentType=").append(tournamentType);
        sb.append(", groupId=").append(groupId);
        sb.append(", round=").append(round);
        sb.append(", roundType=").append(roundType);
        sb.append(", weatherDesc=").append(weatherDesc);
        sb.append(", googleMapsCoordinates=").append(googleMapsCoordinates);
        sb.append(", stadiumNames=").append(stadiumNames);
        sb.append(", editStatus=").append(editStatus);
        sb.append(", homeTeamScoreD01=").append(homeTeamScoreD01);
        sb.append(", awayTeamScoreD01=").append(awayTeamScoreD01);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}