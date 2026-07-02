package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class ConfigTournamentTradeItem implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "联赛ID")
    private Long tournamentId;

    @ApiModelProperty(value = "1：早盘；0：滚球")
    private Integer matchType;

    @ApiModelProperty(value = "马来最大赔率")
    private BigDecimal spreadMaxOdds;

    @ApiModelProperty(value = "马来最小赔率")
    private BigDecimal spreadMinOdds;

    @ApiModelProperty(value = "欧赔最大赔率")
    private BigDecimal marginMaxOdds;

    @ApiModelProperty(value = "欧赔最小赔率")
    private BigDecimal marginMinOdds;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

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

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public BigDecimal getSpreadMaxOdds() {
        return spreadMaxOdds;
    }

    public void setSpreadMaxOdds(BigDecimal spreadMaxOdds) {
        this.spreadMaxOdds = spreadMaxOdds;
    }

    public BigDecimal getSpreadMinOdds() {
        return spreadMinOdds;
    }

    public void setSpreadMinOdds(BigDecimal spreadMinOdds) {
        this.spreadMinOdds = spreadMinOdds;
    }

    public BigDecimal getMarginMaxOdds() {
        return marginMaxOdds;
    }

    public void setMarginMaxOdds(BigDecimal marginMaxOdds) {
        this.marginMaxOdds = marginMaxOdds;
    }

    public BigDecimal getMarginMinOdds() {
        return marginMinOdds;
    }

    public void setMarginMinOdds(BigDecimal marginMinOdds) {
        this.marginMinOdds = marginMinOdds;
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

    public Long getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(Long operaterId) {
        this.operaterId = operaterId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", tournamentId=").append(tournamentId);
        sb.append(", matchType=").append(matchType);
        sb.append(", spreadMaxOdds=").append(spreadMaxOdds);
        sb.append(", spreadMinOdds=").append(spreadMinOdds);
        sb.append(", marginMaxOdds=").append(marginMaxOdds);
        sb.append(", marginMinOdds=").append(marginMinOdds);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}