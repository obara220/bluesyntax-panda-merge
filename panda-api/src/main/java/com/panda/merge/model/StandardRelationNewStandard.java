package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardRelationNewStandard implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "源标准赛事id")
    private Long sourceStandardId;

    @ApiModelProperty(value = "新标准赛事id")
    private Long newStandardId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceStandardId() {
        return sourceStandardId;
    }

    public void setSourceStandardId(Long sourceStandardId) {
        this.sourceStandardId = sourceStandardId;
    }

    public Long getNewStandardId() {
        return newStandardId;
    }

    public void setNewStandardId(Long newStandardId) {
        this.newStandardId = newStandardId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sourceStandardId=").append(sourceStandardId);
        sb.append(", newStandardId=").append(newStandardId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}