package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdOutrightMatchInfo implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "区域id")
    private Long regionId;

    @ApiModelProperty(value = "三方联赛id")
    private Long tournamentId;

    @ApiModelProperty(value = "三方冠军赛事英文")
    private String thirdMatchNameEn;

    @ApiModelProperty(value = "三方冠军赛事中文")
    private String thirdMatchNameCn;

    @ApiModelProperty(value = "数据源sr,bc,bg")
    private String dataSourceCode;

    @ApiModelProperty(value = "三方冠军赛事开始时间")
    private Long thirdOutrightBeginTime;

    @ApiModelProperty(value = "三方冠军赛事结束时间")
    private Long thirdOutrightEndTime;

    @ApiModelProperty(value = "三方冠军赛事源id")
    private String thirdOutrightSourceId;

    @ApiModelProperty(value = "标准管理id")
    private String standardOutrightManagerId;

    @ApiModelProperty(value = "标准冠军赛id")
    private Long referenceId;

    @ApiModelProperty(value = "赛季id")
    private String seasonId;

    @ApiModelProperty(value = "三方冠军赛事赛季名称")
    private String thirdOutrightYear;

    @ApiModelProperty(value = "是否订阅0未订阅1已订阅")
    private Integer booked;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getThirdMatchNameEn() {
        return thirdMatchNameEn;
    }

    public void setThirdMatchNameEn(String thirdMatchNameEn) {
        this.thirdMatchNameEn = thirdMatchNameEn;
    }

    public String getThirdMatchNameCn() {
        return thirdMatchNameCn;
    }

    public void setThirdMatchNameCn(String thirdMatchNameCn) {
        this.thirdMatchNameCn = thirdMatchNameCn;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getThirdOutrightBeginTime() {
        return thirdOutrightBeginTime;
    }

    public void setThirdOutrightBeginTime(Long thirdOutrightBeginTime) {
        this.thirdOutrightBeginTime = thirdOutrightBeginTime;
    }

    public Long getThirdOutrightEndTime() {
        return thirdOutrightEndTime;
    }

    public void setThirdOutrightEndTime(Long thirdOutrightEndTime) {
        this.thirdOutrightEndTime = thirdOutrightEndTime;
    }

    public String getThirdOutrightSourceId() {
        return thirdOutrightSourceId;
    }

    public void setThirdOutrightSourceId(String thirdOutrightSourceId) {
        this.thirdOutrightSourceId = thirdOutrightSourceId;
    }

    public String getStandardOutrightManagerId() {
        return standardOutrightManagerId;
    }

    public void setStandardOutrightManagerId(String standardOutrightManagerId) {
        this.standardOutrightManagerId = standardOutrightManagerId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getThirdOutrightYear() {
        return thirdOutrightYear;
    }

    public void setThirdOutrightYear(String thirdOutrightYear) {
        this.thirdOutrightYear = thirdOutrightYear;
    }

    public Integer getBooked() {
        return booked;
    }

    public void setBooked(Integer booked) {
        this.booked = booked;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        sb.append(", sportId=").append(sportId);
        sb.append(", regionId=").append(regionId);
        sb.append(", tournamentId=").append(tournamentId);
        sb.append(", thirdMatchNameEn=").append(thirdMatchNameEn);
        sb.append(", thirdMatchNameCn=").append(thirdMatchNameCn);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdOutrightBeginTime=").append(thirdOutrightBeginTime);
        sb.append(", thirdOutrightEndTime=").append(thirdOutrightEndTime);
        sb.append(", thirdOutrightSourceId=").append(thirdOutrightSourceId);
        sb.append(", standardOutrightManagerId=").append(standardOutrightManagerId);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", seasonId=").append(seasonId);
        sb.append(", thirdOutrightYear=").append(thirdOutrightYear);
        sb.append(", booked=").append(booked);
        sb.append(", remark=").append(remark);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}