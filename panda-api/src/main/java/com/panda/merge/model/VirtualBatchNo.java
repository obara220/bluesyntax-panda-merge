package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class VirtualBatchNo implements Serializable {
    private Long id;

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "标准联赛ID")
    private Long standardTournamentId;

    @ApiModelProperty(value = "三方联赛ID")
    private Long thirdTournamentId;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    private Long sportId;

    @ApiModelProperty(value = "开始投注时间")
    private Long betStartTime;

    @ApiModelProperty(value = "停止投注时间")
    private Long betEndTime;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
    }

    public Long getThirdTournamentId() {
        return thirdTournamentId;
    }

    public void setThirdTournamentId(Long thirdTournamentId) {
        this.thirdTournamentId = thirdTournamentId;
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

    public Long getBetStartTime() {
        return betStartTime;
    }

    public void setBetStartTime(Long betStartTime) {
        this.betStartTime = betStartTime;
    }

    public Long getBetEndTime() {
        return betEndTime;
    }

    public void setBetEndTime(Long betEndTime) {
        this.betEndTime = betEndTime;
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
        sb.append(", batchNo=").append(batchNo);
        sb.append(", standardTournamentId=").append(standardTournamentId);
        sb.append(", thirdTournamentId=").append(thirdTournamentId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", sportId=").append(sportId);
        sb.append(", betStartTime=").append(betStartTime);
        sb.append(", betEndTime=").append(betEndTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}