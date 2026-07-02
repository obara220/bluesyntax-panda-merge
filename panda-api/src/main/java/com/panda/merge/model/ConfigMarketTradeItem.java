package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigMarketTradeItem implements Serializable {
    @ApiModelProperty(value = "标准盘口Id,主键")
    private Long marketId;

    @ApiModelProperty(value = "标准赛事Id")
    private Long matchId;

    @ApiModelProperty(value = "标准玩法Id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "盘口位置")
    private Integer placeNum;

    @ApiModelProperty(value = "最大赔率")
    private Double maxOddsValue;

    @ApiModelProperty(value = "最小赔率")
    private Double minOddsValue;

    private String linkId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

    @ApiModelProperty(value = "子玩法ID")
    private Long childStandardCategoryId;

    private static final long serialVersionUID = 1L;

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public Integer getPlaceNum() {
        return placeNum;
    }

    public void setPlaceNum(Integer placeNum) {
        this.placeNum = placeNum;
    }

    public Double getMaxOddsValue() {
        return maxOddsValue;
    }

    public void setMaxOddsValue(Double maxOddsValue) {
        this.maxOddsValue = maxOddsValue;
    }

    public Double getMinOddsValue() {
        return minOddsValue;
    }

    public void setMinOddsValue(Double minOddsValue) {
        this.minOddsValue = minOddsValue;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
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

    public Long getChildStandardCategoryId() {
        return childStandardCategoryId;
    }

    public void setChildStandardCategoryId(Long childStandardCategoryId) {
        this.childStandardCategoryId = childStandardCategoryId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", marketId=").append(marketId);
        sb.append(", matchId=").append(matchId);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", placeNum=").append(placeNum);
        sb.append(", maxOddsValue=").append(maxOddsValue);
        sb.append(", minOddsValue=").append(minOddsValue);
        sb.append(", linkId=").append(linkId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", childStandardCategoryId=").append(childStandardCategoryId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}