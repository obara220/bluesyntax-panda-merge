package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class ThirdSportPlayerRanking implements Serializable {
    @ApiModelProperty(value = "ID(赛季源ID+榜单类型+球员源ID)")
    private String id;

    @ApiModelProperty(value = "三方数据源联赛ID")
    private String thirdTournamentSourceId;

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

    @ApiModelProperty(value = "参数场数")
    private Integer matchCount;

    @ApiModelProperty(value = "榜单值")
    private Integer rankingValue;

    @ApiModelProperty(value = "榜单序号")
    private Integer rankingSort;

    @ApiModelProperty(value = "榜单类型（足球类：1射手榜,24助攻榜|篮球类：24助攻榜,59盖帽榜,60得分榜,61篮板榜,62抢断榜,63技术犯规榜,64失误榜,65投篮次数榜,66进球次数榜,67效率榜,69二分命中数榜,70二分投球次数榜,71三分命中数榜,72三分投中次数榜,73罚中次数榜,74罚球次数榜）")
    private Integer rankingType;

    @ApiModelProperty(value = "三方数据源球队ID")
    private String thirdTeamSourceId;

    @ApiModelProperty(value = "球队中文名称")
    private String teamCnName;

    @ApiModelProperty(value = "球队英文名称")
    private String teamEnName;

    @ApiModelProperty(value = "球队logo")
    private String teamLogo;

    @ApiModelProperty(value = "三方数据源球员ID")
    private String thirdPlayerSourceId;

    @ApiModelProperty(value = "球员中文名称")
    private String playerCnName;

    @ApiModelProperty(value = "球员英文名称")
    private String playerEnName;

    @ApiModelProperty(value = "球员logo")
    private String playerLogo;

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

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }

    public Integer getRankingValue() {
        return rankingValue;
    }

    public void setRankingValue(Integer rankingValue) {
        this.rankingValue = rankingValue;
    }

    public Integer getRankingSort() {
        return rankingSort;
    }

    public void setRankingSort(Integer rankingSort) {
        this.rankingSort = rankingSort;
    }

    public Integer getRankingType() {
        return rankingType;
    }

    public void setRankingType(Integer rankingType) {
        this.rankingType = rankingType;
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

    public String getThirdPlayerSourceId() {
        return thirdPlayerSourceId;
    }

    public void setThirdPlayerSourceId(String thirdPlayerSourceId) {
        this.thirdPlayerSourceId = thirdPlayerSourceId;
    }

    public String getPlayerCnName() {
        return playerCnName;
    }

    public void setPlayerCnName(String playerCnName) {
        this.playerCnName = playerCnName;
    }

    public String getPlayerEnName() {
        return playerEnName;
    }

    public void setPlayerEnName(String playerEnName) {
        this.playerEnName = playerEnName;
    }

    public String getPlayerLogo() {
        return playerLogo;
    }

    public void setPlayerLogo(String playerLogo) {
        this.playerLogo = playerLogo;
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
        sb.append(", sportId=").append(sportId);
        sb.append(", thirdSourceSeasonId=").append(thirdSourceSeasonId);
        sb.append(", thirdSourceSeasonName=").append(thirdSourceSeasonName);
        sb.append(", thirdSourceSeasonBeginTime=").append(thirdSourceSeasonBeginTime);
        sb.append(", thirdSourceSeasonEndTime=").append(thirdSourceSeasonEndTime);
        sb.append(", matchCount=").append(matchCount);
        sb.append(", rankingValue=").append(rankingValue);
        sb.append(", rankingSort=").append(rankingSort);
        sb.append(", rankingType=").append(rankingType);
        sb.append(", thirdTeamSourceId=").append(thirdTeamSourceId);
        sb.append(", teamCnName=").append(teamCnName);
        sb.append(", teamEnName=").append(teamEnName);
        sb.append(", teamLogo=").append(teamLogo);
        sb.append(", thirdPlayerSourceId=").append(thirdPlayerSourceId);
        sb.append(", playerCnName=").append(playerCnName);
        sb.append(", playerEnName=").append(playerEnName);
        sb.append(", playerLogo=").append(playerLogo);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}