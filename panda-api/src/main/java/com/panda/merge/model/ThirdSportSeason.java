package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdSportSeason implements Serializable {
    @ApiModelProperty(value = "赛季id")
    private Long id;

    @ApiModelProperty(value = "三方原始赛季id")
    private String thirdSourceSeasonId;

    @ApiModelProperty(value = "关联赛季id")
    private Long referenceId;

    @ApiModelProperty(value = "运动种类ID")
    private Long sportId;

    @ApiModelProperty(value = "数据来源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "赛季名称中文名")
    private String seasonName;

    @ApiModelProperty(value = "三方联赛ID")
    private Long thirdTournamentId;

    @ApiModelProperty(value = "赛季多语言编码")
    private Long nameCode;

    @ApiModelProperty(value = "赛季开始时间")
    private Long startDate;

    @ApiModelProperty(value = "赛季结束时间")
    private Long endDate;

    @ApiModelProperty(value = "赛季年份")
    private String year;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThirdSourceSeasonId() {
        return thirdSourceSeasonId;
    }

    public void setThirdSourceSeasonId(String thirdSourceSeasonId) {
        this.thirdSourceSeasonId = thirdSourceSeasonId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
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

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public Long getThirdTournamentId() {
        return thirdTournamentId;
    }

    public void setThirdTournamentId(Long thirdTournamentId) {
        this.thirdTournamentId = thirdTournamentId;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", thirdSourceSeasonId=").append(thirdSourceSeasonId);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", sportId=").append(sportId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", seasonName=").append(seasonName);
        sb.append(", thirdTournamentId=").append(thirdTournamentId);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", year=").append(year);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}