package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class MatchSettleDataSourceWeightConfig implements Serializable {
    private Long id;

    @ApiModelProperty(value = "数据商编码,BG、BT")
    private String dataSourceCode;

    @ApiModelProperty(value = "联赛等级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "球种,1:足球、2:篮球")
    private Long sportId;

    @ApiModelProperty(value = "开关状态,0:关闭、1:开启")
    private Integer status;

    @ApiModelProperty(value = "数据商权重值")
    private Integer weightNum;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWeightNum() {
        return weightNum;
    }

    public void setWeightNum(Integer weightNum) {
        this.weightNum = weightNum;
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
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", sportId=").append(sportId);
        sb.append(", status=").append(status);
        sb.append(", weightNum=").append(weightNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}