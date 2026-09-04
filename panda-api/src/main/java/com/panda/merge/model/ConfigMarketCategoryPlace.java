package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigMarketCategoryPlace implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准比赛IDstandard_match_info.id")
    private Long standardMatchInfoId;

    @ApiModelProperty(value = "标准玩法ID")
    private Long standardCategoryId;

    @ApiModelProperty(value = "盘口位置")
    private Integer placeNum;

    @ApiModelProperty(value = "盘口位置状态")
    private String placeNumStatus;

    @ApiModelProperty(value = "linkId")
    private String linkId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

    @ApiModelProperty(value = "子玩法ID")
    private Long childStandardCategoryId;

    /**
     * 风控防封，累封 需求状态透传给风控 ，不入库
     */
    private Integer placeNumStatusDisplay = 1;
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

    public Integer getPlaceNum() {
        return placeNum;
    }

    public void setPlaceNum(Integer placeNum) {
        this.placeNum = placeNum;
    }

    public String getPlaceNumStatus() {
        return placeNumStatus;
    }

    public void setPlaceNumStatus(String placeNumStatus) {
        this.placeNumStatus = placeNumStatus;
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

    public Integer getPlaceNumStatusDisplay() {
        return placeNumStatusDisplay;
    }

    public void setPlaceNumStatusDisplay(Integer placeNumStatusDisplay) {
        this.placeNumStatusDisplay = placeNumStatusDisplay;
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
        sb.append(", placeNum=").append(placeNum);
        sb.append(", placeNumStatus=").append(placeNumStatus);
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