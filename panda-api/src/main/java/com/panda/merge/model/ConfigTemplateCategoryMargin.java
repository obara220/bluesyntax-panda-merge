package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigTemplateCategoryMargin implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准玩法ID")
    private Long standardCategoryId;

    @ApiModelProperty(value = "分时")
    private Long timeFrame;

    @ApiModelProperty(value = "margin值")
    private Double margin;

    @ApiModelProperty(value = "对应config_tournament_category.id")
    private Long templateCategoryId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "是否被取消.1被取消;0:没有被取消")
    private Integer canceled;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardCategoryId() {
        return standardCategoryId;
    }

    public void setStandardCategoryId(Long standardCategoryId) {
        this.standardCategoryId = standardCategoryId;
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

    public Long getTemplateCategoryId() {
        return templateCategoryId;
    }

    public void setTemplateCategoryId(Long templateCategoryId) {
        this.templateCategoryId = templateCategoryId;
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

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardCategoryId=").append(standardCategoryId);
        sb.append(", timeFrame=").append(timeFrame);
        sb.append(", margin=").append(margin);
        sb.append(", templateCategoryId=").append(templateCategoryId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", canceled=").append(canceled);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}