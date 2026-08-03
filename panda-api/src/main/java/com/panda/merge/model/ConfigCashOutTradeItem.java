package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigCashOutTradeItem implements Serializable {
    private Long id;

    @ApiModelProperty(value = "赛事ID")
    private Long matchId;

    @ApiModelProperty(value = "1:赛前盘;0:滚球盘")
    private Integer marketType;

    @ApiModelProperty(value = "玩法ID")
    private Long marketCategoryId;

    @ApiModelProperty(value = "赛事级别提前结算开关,0:关1:开")
    private Integer matchPreStatus;

    @ApiModelProperty(value = "玩法级别提前结算开关,0:关1:开")
    private Integer categoryPreStatus;

    @ApiModelProperty(value = "cashOutMargin")
    private Long cashOutMargin;

    @ApiModelProperty(value = "等级：1：赛事、2：玩法")
    private Integer leve;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Integer getMarketType() {
        return marketType;
    }

    public void setMarketType(Integer marketType) {
        this.marketType = marketType;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public Integer getMatchPreStatus() {
        return matchPreStatus;
    }

    public void setMatchPreStatus(Integer matchPreStatus) {
        this.matchPreStatus = matchPreStatus;
    }

    public Integer getCategoryPreStatus() {
        return categoryPreStatus;
    }

    public void setCategoryPreStatus(Integer categoryPreStatus) {
        this.categoryPreStatus = categoryPreStatus;
    }

    public Long getCashOutMargin() {
        return cashOutMargin;
    }

    public void setCashOutMargin(Long cashOutMargin) {
        this.cashOutMargin = cashOutMargin;
    }

    public Integer getLeve() {
        return leve;
    }

    public void setLeve(Integer leve) {
        this.leve = leve;
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

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", matchId=").append(matchId);
        sb.append(", marketType=").append(marketType);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", matchPreStatus=").append(matchPreStatus);
        sb.append(", categoryPreStatus=").append(categoryPreStatus);
        sb.append(", cashOutMargin=").append(cashOutMargin);
        sb.append(", leve=").append(leve);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}