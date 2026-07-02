package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class ThirdMatchSeasonStatistics implements Serializable {
    @ApiModelProperty(value = "三方数据源赛季ID+数据来源+运动类型")
    private String id;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "三方数据源赛季ID")
    private String thirdSourceSeasonId;

    @ApiModelProperty(value = "三方数据源赛季名称")
    private String thirdSourceSeasonName;

    @ApiModelProperty(value = "三方数据源联赛ID")
    private String thirdTournamentSourceId;

    @ApiModelProperty(value = "联赛类别(0:其他,1联赛,2杯赛)")
    private Integer tournamentType;

    @ApiModelProperty(value = "0:自动1:手动")
    private Integer editStatus;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "高于1.5占比")
    private BigDecimal percentThanOne;

    @ApiModelProperty(value = "高于2.5占比")
    private BigDecimal percentThanTwo;

    @ApiModelProperty(value = "高于3.5占比")
    private BigDecimal percentThanThree;

    @ApiModelProperty(value = "均场入球")
    private BigDecimal averageGoal;

    @ApiModelProperty(value = "罚牌")
    private BigDecimal averageCard;

    @ApiModelProperty(value = "角球")
    private BigDecimal averageCorner;

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

    public String getThirdTournamentSourceId() {
        return thirdTournamentSourceId;
    }

    public void setThirdTournamentSourceId(String thirdTournamentSourceId) {
        this.thirdTournamentSourceId = thirdTournamentSourceId;
    }

    public Integer getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(Integer tournamentType) {
        this.tournamentType = tournamentType;
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

    public BigDecimal getPercentThanOne() {
        return percentThanOne;
    }

    public void setPercentThanOne(BigDecimal percentThanOne) {
        this.percentThanOne = percentThanOne;
    }

    public BigDecimal getPercentThanTwo() {
        return percentThanTwo;
    }

    public void setPercentThanTwo(BigDecimal percentThanTwo) {
        this.percentThanTwo = percentThanTwo;
    }

    public BigDecimal getPercentThanThree() {
        return percentThanThree;
    }

    public void setPercentThanThree(BigDecimal percentThanThree) {
        this.percentThanThree = percentThanThree;
    }

    public BigDecimal getAverageGoal() {
        return averageGoal;
    }

    public void setAverageGoal(BigDecimal averageGoal) {
        this.averageGoal = averageGoal;
    }

    public BigDecimal getAverageCard() {
        return averageCard;
    }

    public void setAverageCard(BigDecimal averageCard) {
        this.averageCard = averageCard;
    }

    public BigDecimal getAverageCorner() {
        return averageCorner;
    }

    public void setAverageCorner(BigDecimal averageCorner) {
        this.averageCorner = averageCorner;
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
        sb.append(", thirdSourceSeasonId=").append(thirdSourceSeasonId);
        sb.append(", thirdSourceSeasonName=").append(thirdSourceSeasonName);
        sb.append(", thirdTournamentSourceId=").append(thirdTournamentSourceId);
        sb.append(", tournamentType=").append(tournamentType);
        sb.append(", editStatus=").append(editStatus);
        sb.append(", sportId=").append(sportId);
        sb.append(", percentThanOne=").append(percentThanOne);
        sb.append(", percentThanTwo=").append(percentThanTwo);
        sb.append(", percentThanThree=").append(percentThanThree);
        sb.append(", averageGoal=").append(averageGoal);
        sb.append(", averageCard=").append(averageCard);
        sb.append(", averageCorner=").append(averageCorner);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}