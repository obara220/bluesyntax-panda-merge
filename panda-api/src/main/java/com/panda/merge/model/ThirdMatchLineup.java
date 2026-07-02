package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchLineup implements Serializable {
    @ApiModelProperty(value = "数据来源ID:数据源赛事ID:数据源球队ID:数据源球员ID")
    private String id;

    @ApiModelProperty(value = "数据源赛事id")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "数据源球队id")
    private String thirdTeamSourceId;

    @ApiModelProperty(value = "数据源球员id")
    private String thirdPlayerSourceId;

    @ApiModelProperty(value = "数据源球员名称")
    private String thirdPlayerName;

    @ApiModelProperty(value = "球员头像")
    private String thirdPlayerPicUrl;

    @ApiModelProperty(value = "球员位置")
    private Integer position;

    @ApiModelProperty(value = "球衣号码")
    private Integer shirtNumber;

    @ApiModelProperty(value = "是否替补(0:否,1:是)")
    private Integer substitute;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "主客队标识(1主队,2客队)")
    private Integer homeAway;

    @ApiModelProperty(value = "球员位置名称(前锋，中场，后卫)")
    private String positionName;

    @ApiModelProperty(value = "是否失效(0:否,1:是)")
    private Integer invalid;

    @ApiModelProperty(value = "综合评分")
    private String overallRatings;

    @ApiModelProperty(value = "人员英文名称")
    private String thirdPlayerEnName;

    @ApiModelProperty(value = "0:自动1:手动")
    private Integer editStatus;

    @ApiModelProperty(value = "主队阵型")
    private String homeFormation;

    @ApiModelProperty(value = "客队阵型")
    private String awayFormation;

    @ApiModelProperty(value = "上场时间（分钟）")
    private String playTime;

    @ApiModelProperty(value = "助攻")
    private String assist;

    @ApiModelProperty(value = "篮板")
    private String rebound;

    @ApiModelProperty(value = "得分")
    private String point;

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

    public String getThirdTeamSourceId() {
        return thirdTeamSourceId;
    }

    public void setThirdTeamSourceId(String thirdTeamSourceId) {
        this.thirdTeamSourceId = thirdTeamSourceId;
    }

    public String getThirdPlayerSourceId() {
        return thirdPlayerSourceId;
    }

    public void setThirdPlayerSourceId(String thirdPlayerSourceId) {
        this.thirdPlayerSourceId = thirdPlayerSourceId;
    }

    public String getThirdPlayerName() {
        return thirdPlayerName;
    }

    public void setThirdPlayerName(String thirdPlayerName) {
        this.thirdPlayerName = thirdPlayerName;
    }

    public String getThirdPlayerPicUrl() {
        return thirdPlayerPicUrl;
    }

    public void setThirdPlayerPicUrl(String thirdPlayerPicUrl) {
        this.thirdPlayerPicUrl = thirdPlayerPicUrl;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getShirtNumber() {
        return shirtNumber;
    }

    public void setShirtNumber(Integer shirtNumber) {
        this.shirtNumber = shirtNumber;
    }

    public Integer getSubstitute() {
        return substitute;
    }

    public void setSubstitute(Integer substitute) {
        this.substitute = substitute;
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

    public Integer getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(Integer homeAway) {
        this.homeAway = homeAway;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
    }

    public String getOverallRatings() {
        return overallRatings;
    }

    public void setOverallRatings(String overallRatings) {
        this.overallRatings = overallRatings;
    }

    public String getThirdPlayerEnName() {
        return thirdPlayerEnName;
    }

    public void setThirdPlayerEnName(String thirdPlayerEnName) {
        this.thirdPlayerEnName = thirdPlayerEnName;
    }

    public Integer getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(Integer editStatus) {
        this.editStatus = editStatus;
    }

    public String getHomeFormation() {
        return homeFormation;
    }

    public void setHomeFormation(String homeFormation) {
        this.homeFormation = homeFormation;
    }

    public String getAwayFormation() {
        return awayFormation;
    }

    public void setAwayFormation(String awayFormation) {
        this.awayFormation = awayFormation;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public String getAssist() {
        return assist;
    }

    public void setAssist(String assist) {
        this.assist = assist;
    }

    public String getRebound() {
        return rebound;
    }

    public void setRebound(String rebound) {
        this.rebound = rebound;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", sportId=").append(sportId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdTeamSourceId=").append(thirdTeamSourceId);
        sb.append(", thirdPlayerSourceId=").append(thirdPlayerSourceId);
        sb.append(", thirdPlayerName=").append(thirdPlayerName);
        sb.append(", thirdPlayerPicUrl=").append(thirdPlayerPicUrl);
        sb.append(", position=").append(position);
        sb.append(", shirtNumber=").append(shirtNumber);
        sb.append(", substitute=").append(substitute);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", positionName=").append(positionName);
        sb.append(", invalid=").append(invalid);
        sb.append(", overallRatings=").append(overallRatings);
        sb.append(", thirdPlayerEnName=").append(thirdPlayerEnName);
        sb.append(", editStatus=").append(editStatus);
        sb.append(", homeFormation=").append(homeFormation);
        sb.append(", awayFormation=").append(awayFormation);
        sb.append(", playTime=").append(playTime);
        sb.append(", assist=").append(assist);
        sb.append(", rebound=").append(rebound);
        sb.append(", point=").append(point);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}