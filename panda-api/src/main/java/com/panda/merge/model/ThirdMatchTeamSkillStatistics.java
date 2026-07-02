package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchTeamSkillStatistics implements Serializable {
    @ApiModelProperty(value = "数据来源ID:源赛事ID:源球队ID")
    private String id;

    @ApiModelProperty(value = "源赛事ID")
    private String matchId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "源球队ID")
    private String teamId;

    @ApiModelProperty(value = "主客队（1:主,2:客）")
    private String homeAway;

    @ApiModelProperty(value = "篮板总数，包括进攻篮板和防守篮板")
    private Integer rebound;

    @ApiModelProperty(value = "进攻篮板数量")
    private Integer offensiveRebound;

    @ApiModelProperty(value = "防守篮板数量")
    private Integer defensiveRebound;

    @ApiModelProperty(value = "助攻数量")
    private Integer assist;

    @ApiModelProperty(value = "盖帽数量")
    private Integer block;

    @ApiModelProperty(value = "抢断数量")
    private Integer steal;

    @ApiModelProperty(value = "失误数量")
    private Integer turnover;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @ApiModelProperty(value = "犯规数量")
    private Integer fouls;

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

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
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

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Integer getRebound() {
        return rebound;
    }

    public void setRebound(Integer rebound) {
        this.rebound = rebound;
    }

    public Integer getOffensiveRebound() {
        return offensiveRebound;
    }

    public void setOffensiveRebound(Integer offensiveRebound) {
        this.offensiveRebound = offensiveRebound;
    }

    public Integer getDefensiveRebound() {
        return defensiveRebound;
    }

    public void setDefensiveRebound(Integer defensiveRebound) {
        this.defensiveRebound = defensiveRebound;
    }

    public Integer getAssist() {
        return assist;
    }

    public void setAssist(Integer assist) {
        this.assist = assist;
    }

    public Integer getBlock() {
        return block;
    }

    public void setBlock(Integer block) {
        this.block = block;
    }

    public Integer getSteal() {
        return steal;
    }

    public void setSteal(Integer steal) {
        this.steal = steal;
    }

    public Integer getTurnover() {
        return turnover;
    }

    public void setTurnover(Integer turnover) {
        this.turnover = turnover;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getFouls() {
        return fouls;
    }

    public void setFouls(Integer fouls) {
        this.fouls = fouls;
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
        sb.append(", matchId=").append(matchId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", sportId=").append(sportId);
        sb.append(", teamId=").append(teamId);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", rebound=").append(rebound);
        sb.append(", offensiveRebound=").append(offensiveRebound);
        sb.append(", defensiveRebound=").append(defensiveRebound);
        sb.append(", assist=").append(assist);
        sb.append(", block=").append(block);
        sb.append(", steal=").append(steal);
        sb.append(", turnover=").append(turnover);
        sb.append(", score=").append(score);
        sb.append(", fouls=").append(fouls);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}