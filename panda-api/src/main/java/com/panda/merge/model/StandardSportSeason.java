package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportSeason implements Serializable {
    @ApiModelProperty(value = "标准赛季id")
    private Long id;

    @ApiModelProperty(value = "三方赛季ID")
    private Long thirdSeasonId;

    @ApiModelProperty(value = "赛季名称中文名")
    private String seasonName;

    @ApiModelProperty(value = "赛季多语言编码")
    private Long nameCode;

    @ApiModelProperty(value = "标准联赛ID")
    private Long standardTournamentId;

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

    public Long getThirdSeasonId() {
        return thirdSeasonId;
    }

    public void setThirdSeasonId(Long thirdSeasonId) {
        this.thirdSeasonId = thirdSeasonId;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
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
        sb.append(", thirdSeasonId=").append(thirdSeasonId);
        sb.append(", seasonName=").append(seasonName);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", standardTournamentId=").append(standardTournamentId);
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