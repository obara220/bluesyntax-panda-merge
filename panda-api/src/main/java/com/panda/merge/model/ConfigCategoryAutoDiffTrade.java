package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigCategoryAutoDiffTrade implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "标准玩法id")
    private Long standardCategoryId;

    @ApiModelProperty(value = "投注项类型")
    private String oddsType;

    private Double diffValue;

    @ApiModelProperty(value = "日志id")
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

    public Long getStandardCategoryId() {
        return standardCategoryId;
    }

    public void setStandardCategoryId(Long standardCategoryId) {
        this.standardCategoryId = standardCategoryId;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public Double getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(Double diffValue) {
        this.diffValue = diffValue;
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
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", standardCategoryId=").append(standardCategoryId);
        sb.append(", oddsType=").append(oddsType);
        sb.append(", diffValue=").append(diffValue);
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