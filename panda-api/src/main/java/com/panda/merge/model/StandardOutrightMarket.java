package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardOutrightMarket implements Serializable {
    @ApiModelProperty(value = "标准盘口id")
    private Long id;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "标准玩法id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "盘口开售状态Sold开售Unsold未售")
    private String marketSellStatus;

    @ApiModelProperty(value = "盘口国际信息")
    private Long nameCode;

    @ApiModelProperty(value = "标准盘口状态")
    private Integer marketStatus;

    @ApiModelProperty(value = "投注项排序类型, 0:自动  1:手动")
    private Integer orderType;

    @ApiModelProperty(value = "盘口的排序值")
    private Integer marketOrderNumber;

    @ApiModelProperty(value = "下次封盘时间")
    private Long nextClosingTime;

    private String linkId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modfiyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public String getMarketSellStatus() {
        return marketSellStatus;
    }

    public void setMarketSellStatus(String marketSellStatus) {
        this.marketSellStatus = marketSellStatus;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(Integer marketStatus) {
        this.marketStatus = marketStatus;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getMarketOrderNumber() {
        return marketOrderNumber;
    }

    public void setMarketOrderNumber(Integer marketOrderNumber) {
        this.marketOrderNumber = marketOrderNumber;
    }

    public Long getNextClosingTime() {
        return nextClosingTime;
    }

    public void setNextClosingTime(Long nextClosingTime) {
        this.nextClosingTime = nextClosingTime;
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

    public Long getModfiyTime() {
        return modfiyTime;
    }

    public void setModfiyTime(Long modfiyTime) {
        this.modfiyTime = modfiyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", marketSellStatus=").append(marketSellStatus);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", marketStatus=").append(marketStatus);
        sb.append(", orderType=").append(orderType);
        sb.append(", marketOrderNumber=").append(marketOrderNumber);
        sb.append(", nextClosingTime=").append(nextClosingTime);
        sb.append(", linkId=").append(linkId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modfiyTime=").append(modfiyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}