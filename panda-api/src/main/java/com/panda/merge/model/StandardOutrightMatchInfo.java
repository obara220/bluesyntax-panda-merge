package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardOutrightMatchInfo implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "赛种id")
    private Long sportId;

    @ApiModelProperty(value = "区域id")
    private Long regionId;

    @ApiModelProperty(value = "标准联赛id")
    private Long standardTournamentId;

    @ApiModelProperty(value = "冠军赛事中文名称")
    private String standardOutrightNameEn;

    @ApiModelProperty(value = "冠军赛事英文名称")
    private String standardOutrightNameCn;

    @ApiModelProperty(value = "数据源")
    private String dataSourceCode;

    @ApiModelProperty(value = "赛事开关封锁-1未开0:开、2:关、1:封、11")
    private Integer matchMarketStatus;

    @ApiModelProperty(value = "冠军赛事管理id")
    private String standardOutrightManagerId;

    @ApiModelProperty(value = "三方冠军赛事id")
    private Long thirdOutrightMatchId;

    @ApiModelProperty(value = "三方冠军赛事源id")
    private String thirdOutrightMatchSourceId;

    @ApiModelProperty(value = "标准冠军赛事开始时间")
    private Long standrdOutrightMatchBegionTime;

    @ApiModelProperty(value = "标准冠军赛事结束时间")
    private Long standrdOutrightMatchEndTime;

    @ApiModelProperty(value = "冠军赛事开售状态Sold开售Unsold未售")
    private String sellStatus;

    @ApiModelProperty(value = "是否自动开售新盘口Yes是No否")
    private String autoSellStatus;

    @ApiModelProperty(value = "赛季id")
    private String seasonId;

    @ApiModelProperty(value = "标准冠军赛事赛季名称")
    private String standardOutrightYear;

    @ApiModelProperty(value = "是否订阅0未订阅1已订阅")
    private Integer booked;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "新增时间")
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

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
    }

    public String getStandardOutrightNameEn() {
        return standardOutrightNameEn;
    }

    public void setStandardOutrightNameEn(String standardOutrightNameEn) {
        this.standardOutrightNameEn = standardOutrightNameEn;
    }

    public String getStandardOutrightNameCn() {
        return standardOutrightNameCn;
    }

    public void setStandardOutrightNameCn(String standardOutrightNameCn) {
        this.standardOutrightNameCn = standardOutrightNameCn;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getMatchMarketStatus() {
        return matchMarketStatus;
    }

    public void setMatchMarketStatus(Integer matchMarketStatus) {
        this.matchMarketStatus = matchMarketStatus;
    }

    public String getStandardOutrightManagerId() {
        return standardOutrightManagerId;
    }

    public void setStandardOutrightManagerId(String standardOutrightManagerId) {
        this.standardOutrightManagerId = standardOutrightManagerId;
    }

    public Long getThirdOutrightMatchId() {
        return thirdOutrightMatchId;
    }

    public void setThirdOutrightMatchId(Long thirdOutrightMatchId) {
        this.thirdOutrightMatchId = thirdOutrightMatchId;
    }

    public String getThirdOutrightMatchSourceId() {
        return thirdOutrightMatchSourceId;
    }

    public void setThirdOutrightMatchSourceId(String thirdOutrightMatchSourceId) {
        this.thirdOutrightMatchSourceId = thirdOutrightMatchSourceId;
    }

    public Long getStandrdOutrightMatchBegionTime() {
        return standrdOutrightMatchBegionTime;
    }

    public void setStandrdOutrightMatchBegionTime(Long standrdOutrightMatchBegionTime) {
        this.standrdOutrightMatchBegionTime = standrdOutrightMatchBegionTime;
    }

    public Long getStandrdOutrightMatchEndTime() {
        return standrdOutrightMatchEndTime;
    }

    public void setStandrdOutrightMatchEndTime(Long standrdOutrightMatchEndTime) {
        this.standrdOutrightMatchEndTime = standrdOutrightMatchEndTime;
    }

    public String getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(String sellStatus) {
        this.sellStatus = sellStatus;
    }

    public String getAutoSellStatus() {
        return autoSellStatus;
    }

    public void setAutoSellStatus(String autoSellStatus) {
        this.autoSellStatus = autoSellStatus;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getStandardOutrightYear() {
        return standardOutrightYear;
    }

    public void setStandardOutrightYear(String standardOutrightYear) {
        this.standardOutrightYear = standardOutrightYear;
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
        sb.append(", sportId=").append(sportId);
        sb.append(", regionId=").append(regionId);
        sb.append(", standardTournamentId=").append(standardTournamentId);
        sb.append(", standardOutrightNameEn=").append(standardOutrightNameEn);
        sb.append(", standardOutrightNameCn=").append(standardOutrightNameCn);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", matchMarketStatus=").append(matchMarketStatus);
        sb.append(", standardOutrightManagerId=").append(standardOutrightManagerId);
        sb.append(", thirdOutrightMatchId=").append(thirdOutrightMatchId);
        sb.append(", thirdOutrightMatchSourceId=").append(thirdOutrightMatchSourceId);
        sb.append(", standrdOutrightMatchBegionTime=").append(standrdOutrightMatchBegionTime);
        sb.append(", standrdOutrightMatchEndTime=").append(standrdOutrightMatchEndTime);
        sb.append(", sellStatus=").append(sellStatus);
        sb.append(", autoSellStatus=").append(autoSellStatus);
        sb.append(", seasonId=").append(seasonId);
        sb.append(", standardOutrightYear=").append(standardOutrightYear);
        sb.append(", booked=").append(booked);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}