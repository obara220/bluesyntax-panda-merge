package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigMarketLevel implements Serializable {
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "行情等级")
    private Integer level;

    @ApiModelProperty(value = "运动种类id")
    private Long sportId;

    @ApiModelProperty(value = "联赛等级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "盘口类型：2：两项盘，3：多项盘")
    private Integer marketType;

    @ApiModelProperty(value = "盘口类型细分：21：常规玩法，22：50/50玩法，31：1.0-2.0 32：2.01-5.0 33：5.01-10.0")
    private Integer marketTypeDetail;

    @ApiModelProperty(value = "等级水差")
    private Double diffValue;

    @ApiModelProperty(value = "操作人ID")
    private String operaterId;

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Integer getMarketType() {
        return marketType;
    }

    public void setMarketType(Integer marketType) {
        this.marketType = marketType;
    }

    public Integer getMarketTypeDetail() {
        return marketTypeDetail;
    }

    public void setMarketTypeDetail(Integer marketTypeDetail) {
        this.marketTypeDetail = marketTypeDetail;
    }

    public Double getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(Double diffValue) {
        this.diffValue = diffValue;
    }

    public String getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(String operaterId) {
        this.operaterId = operaterId;
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
        sb.append(", level=").append(level);
        sb.append(", sportId=").append(sportId);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", marketType=").append(marketType);
        sb.append(", marketTypeDetail=").append(marketTypeDetail);
        sb.append(", diffValue=").append(diffValue);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
