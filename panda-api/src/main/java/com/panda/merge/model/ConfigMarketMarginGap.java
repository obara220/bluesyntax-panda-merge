package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigMarketMarginGap implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准赛事ID")
    private Long matchId;

    @ApiModelProperty(value = "标准玩法ID")
    private Long marketCategoryId;

    @ApiModelProperty(value = "投注项类型")
    private String oddsType;

    @ApiModelProperty(value = "联动模式：0(否),1(是)")
    private Integer linkageMode;

    @ApiModelProperty(value = "margin")
    private Double margin;

    @ApiModelProperty(value = "水差")
    private Double diffValue;

    @ApiModelProperty(value = "概率差")
    private Double probability;

    @ApiModelProperty(value = "描点：0(否),1(是)")
    private Integer anchor;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

    private String linkId;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "保存时间")
    private Long createTime;

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

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public Integer getLinkageMode() {
        return linkageMode;
    }

    public void setLinkageMode(Integer linkageMode) {
        this.linkageMode = linkageMode;
    }

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public Double getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(Double diffValue) {
        this.diffValue = diffValue;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getAnchor() {
        return anchor;
    }

    public void setAnchor(Integer anchor) {
        this.anchor = anchor;
    }

    public Long getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(Long operaterId) {
        this.operaterId = operaterId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
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
        sb.append(", matchId=").append(matchId);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", oddsType=").append(oddsType);
        sb.append(", linkageMode=").append(linkageMode);
        sb.append(", margin=").append(margin);
        sb.append(", diffValue=").append(diffValue);
        sb.append(", probability=").append(probability);
        sb.append(", anchor=").append(anchor);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", linkId=").append(linkId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", placeNum=").append(placeNum);
        sb.append(", childStandardCategoryId=").append(childStandardCategoryId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}