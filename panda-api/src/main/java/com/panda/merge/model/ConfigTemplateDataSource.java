package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigTemplateDataSource implements Serializable {
    private Long id;

    @ApiModelProperty(value = "模板id")
    private Long templateId;

    @ApiModelProperty(value = "SR权重")
    private Integer srWeight;

    @ApiModelProperty(value = "BC权重")
    private Integer bcWeight;

    @ApiModelProperty(value = "BG权重")
    private Integer bgWeight;

    @ApiModelProperty(value = "最大盘口数")
    private Integer displayMarketCount;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getSrWeight() {
        return srWeight;
    }

    public void setSrWeight(Integer srWeight) {
        this.srWeight = srWeight;
    }

    public Integer getBcWeight() {
        return bcWeight;
    }

    public void setBcWeight(Integer bcWeight) {
        this.bcWeight = bcWeight;
    }

    public Integer getBgWeight() {
        return bgWeight;
    }

    public void setBgWeight(Integer bgWeight) {
        this.bgWeight = bgWeight;
    }

    public Integer getDisplayMarketCount() {
        return displayMarketCount;
    }

    public void setDisplayMarketCount(Integer displayMarketCount) {
        this.displayMarketCount = displayMarketCount;
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
        sb.append(", templateId=").append(templateId);
        sb.append(", srWeight=").append(srWeight);
        sb.append(", bcWeight=").append(bcWeight);
        sb.append(", bgWeight=").append(bgWeight);
        sb.append(", displayMarketCount=").append(displayMarketCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}