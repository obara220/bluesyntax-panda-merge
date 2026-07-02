package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigMarketCategoryMarginLog implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准比赛IDstandard_match_info.id")
    private Long standardMatchInfoId;

    @ApiModelProperty(value = "标准玩法ID")
    private Long standardCategoryId;

    @ApiModelProperty(value = "盘口类型.属于赛前盘或者滚球盘.1:赛前盘;0:滚球盘.")
    private Integer marketType;

    @ApiModelProperty(value = "投注项类型")
    private String oddsType;

    @ApiModelProperty(value = "分时")
    private Long timeFrame;

    @ApiModelProperty(value = "margin值")
    private Double margin;

    @ApiModelProperty(value = "日志id")
    private String linkId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

    @ApiModelProperty(value = "位置")
    private Integer placeNum;

    @ApiModelProperty(value = "子玩法ID")
    private Long childStandardCategoryId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardMatchInfoId() {
        return standardMatchInfoId;
    }

    public void setStandardMatchInfoId(Long standardMatchInfoId) {
        this.standardMatchInfoId = standardMatchInfoId;
    }

    public Long getStandardCategoryId() {
        return standardCategoryId;
    }

    public void setStandardCategoryId(Long standardCategoryId) {
        this.standardCategoryId = standardCategoryId;
    }

    public Integer getMarketType() {
        return marketType;
    }

    public void setMarketType(Integer marketType) {
        this.marketType = marketType;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public Long getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(Long timeFrame) {
        this.timeFrame = timeFrame;
    }

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
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

    public Integer getPlaceNum() {
        return placeNum;
    }

    public void setPlaceNum(Integer placeNum) {
        this.placeNum = placeNum;
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
        sb.append(", id=").append(id);
        sb.append(", standardMatchInfoId=").append(standardMatchInfoId);
        sb.append(", standardCategoryId=").append(standardCategoryId);
        sb.append(", marketType=").append(marketType);
        sb.append(", oddsType=").append(oddsType);
        sb.append(", timeFrame=").append(timeFrame);
        sb.append(", margin=").append(margin);
        sb.append(", linkId=").append(linkId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", placeNum=").append(placeNum);
        sb.append(", childStandardCategoryId=").append(childStandardCategoryId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}