package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdRelationNewThird implements Serializable {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "源三方赛事id")
    private Long sourceThirdId;

    @ApiModelProperty(value = "新三方赛事id")
    private Long newThirdId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceThirdId() {
        return sourceThirdId;
    }

    public void setSourceThirdId(Long sourceThirdId) {
        this.sourceThirdId = sourceThirdId;
    }

    public Long getNewThirdId() {
        return newThirdId;
    }

    public void setNewThirdId(Long newThirdId) {
        this.newThirdId = newThirdId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sourceThirdId=").append(sourceThirdId);
        sb.append(", newThirdId=").append(newThirdId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}