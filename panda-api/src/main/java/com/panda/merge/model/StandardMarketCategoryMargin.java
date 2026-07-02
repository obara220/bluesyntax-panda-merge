package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardMarketCategoryMargin implements Serializable {
    private Long id;

    private Long timeFrame;

    private Integer margin;

    @ApiModelProperty(value = "标准玩法ID")
    private Long standardMarketCategoryId;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(Long timeFrame) {
        this.timeFrame = timeFrame;
    }

    public Integer getMargin() {
        return margin;
    }

    public void setMargin(Integer margin) {
        this.margin = margin;
    }

    public Long getStandardMarketCategoryId() {
        return standardMarketCategoryId;
    }

    public void setStandardMarketCategoryId(Long standardMarketCategoryId) {
        this.standardMarketCategoryId = standardMarketCategoryId;
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
        sb.append(", timeFrame=").append(timeFrame);
        sb.append(", margin=").append(margin);
        sb.append(", standardMarketCategoryId=").append(standardMarketCategoryId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}