package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchFrontStatistics implements Serializable {
    @ApiModelProperty(value = "数据源赛事id+数据来源+运动类型")
    private String id;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "数据源赛事id")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "数据源主队ID")
    private String homeTeamId;

    @ApiModelProperty(value = "数据源客队ID")
    private String awayTeamId;

    @ApiModelProperty(value = "主队名称")
    private String homeTeamName;

    @ApiModelProperty(value = "客队名称")
    private String awayTeamName;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "0:自动1:手动")
    private Integer editStatus;

    @ApiModelProperty(value = "总场数")
    private Integer countTotal;

    @ApiModelProperty(value = "主队赢场数")
    private Integer homeWin;

    @ApiModelProperty(value = "客队赢场数")
    private Integer awayWin;

    @ApiModelProperty(value = "和局场数")
    private Integer dogfallTotal;

    @ApiModelProperty(value = "高于1.5场数")
    private Integer moreThanOne;

    @ApiModelProperty(value = "高于2.5场数")
    private Integer moreThanTwo;

    @ApiModelProperty(value = "高于3.5场数")
    private Integer moreThanThree;

    @ApiModelProperty(value = "两队都得分场数")
    private Integer allScores;

    @ApiModelProperty(value = "主队没有失球场数")
    private Integer homeNotLost;

    @ApiModelProperty(value = "客队没有失球场数")
    private Integer awayNotLost;

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

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
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

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Integer getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(Integer editStatus) {
        this.editStatus = editStatus;
    }

    public Integer getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(Integer countTotal) {
        this.countTotal = countTotal;
    }

    public Integer getHomeWin() {
        return homeWin;
    }

    public void setHomeWin(Integer homeWin) {
        this.homeWin = homeWin;
    }

    public Integer getAwayWin() {
        return awayWin;
    }

    public void setAwayWin(Integer awayWin) {
        this.awayWin = awayWin;
    }

    public Integer getDogfallTotal() {
        return dogfallTotal;
    }

    public void setDogfallTotal(Integer dogfallTotal) {
        this.dogfallTotal = dogfallTotal;
    }

    public Integer getMoreThanOne() {
        return moreThanOne;
    }

    public void setMoreThanOne(Integer moreThanOne) {
        this.moreThanOne = moreThanOne;
    }

    public Integer getMoreThanTwo() {
        return moreThanTwo;
    }

    public void setMoreThanTwo(Integer moreThanTwo) {
        this.moreThanTwo = moreThanTwo;
    }

    public Integer getMoreThanThree() {
        return moreThanThree;
    }

    public void setMoreThanThree(Integer moreThanThree) {
        this.moreThanThree = moreThanThree;
    }

    public Integer getAllScores() {
        return allScores;
    }

    public void setAllScores(Integer allScores) {
        this.allScores = allScores;
    }

    public Integer getHomeNotLost() {
        return homeNotLost;
    }

    public void setHomeNotLost(Integer homeNotLost) {
        this.homeNotLost = homeNotLost;
    }

    public Integer getAwayNotLost() {
        return awayNotLost;
    }

    public void setAwayNotLost(Integer awayNotLost) {
        this.awayNotLost = awayNotLost;
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
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", homeTeamId=").append(homeTeamId);
        sb.append(", awayTeamId=").append(awayTeamId);
        sb.append(", homeTeamName=").append(homeTeamName);
        sb.append(", awayTeamName=").append(awayTeamName);
        sb.append(", sportId=").append(sportId);
        sb.append(", editStatus=").append(editStatus);
        sb.append(", countTotal=").append(countTotal);
        sb.append(", homeWin=").append(homeWin);
        sb.append(", awayWin=").append(awayWin);
        sb.append(", dogfallTotal=").append(dogfallTotal);
        sb.append(", moreThanOne=").append(moreThanOne);
        sb.append(", moreThanTwo=").append(moreThanTwo);
        sb.append(", moreThanThree=").append(moreThanThree);
        sb.append(", allScores=").append(allScores);
        sb.append(", homeNotLost=").append(homeNotLost);
        sb.append(", awayNotLost=").append(awayNotLost);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}