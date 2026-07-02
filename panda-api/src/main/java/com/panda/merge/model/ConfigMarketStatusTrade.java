package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigMarketStatusTrade implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准盘口id")
    private Long relationMarketId;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchInfoId;

    @ApiModelProperty(value = "标准玩法id")
    private Long standardCategoryId;

    @ApiModelProperty(value = "盘口类型")
    private Integer marketType;

    @ApiModelProperty(value = "盘口值")
    private String addtion;

    @ApiModelProperty(value = "盘口状态，12-弃用，其他-开启")
    private Integer marketStatus;

    @ApiModelProperty(value = "操作日志id")
    private String linkId;

    @ApiModelProperty(value = "配置修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRelationMarketId() {
        return relationMarketId;
    }

    public void setRelationMarketId(Long relationMarketId) {
        this.relationMarketId = relationMarketId;
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

    public String getAddtion() {
        return addtion;
    }

    public void setAddtion(String addtion) {
        this.addtion = addtion;
    }

    public Integer getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(Integer marketStatus) {
        this.marketStatus = marketStatus;
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
        sb.append(", relationMarketId=").append(relationMarketId);
        sb.append(", standardMatchInfoId=").append(standardMatchInfoId);
        sb.append(", standardCategoryId=").append(standardCategoryId);
        sb.append(", marketType=").append(marketType);
        sb.append(", addtion=").append(addtion);
        sb.append(", marketStatus=").append(marketStatus);
        sb.append(", linkId=").append(linkId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}