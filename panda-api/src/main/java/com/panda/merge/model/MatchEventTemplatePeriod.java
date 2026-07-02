package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchEventTemplatePeriod implements Serializable {
    private Long id;

    @ApiModelProperty(value = "事件模板ID")
    private Long templateId;

    @ApiModelProperty(value = "触发事件阶段ID")
    private Integer triggerPeriodId;

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

    public Integer getTriggerPeriodId() {
        return triggerPeriodId;
    }

    public void setTriggerPeriodId(Integer triggerPeriodId) {
        this.triggerPeriodId = triggerPeriodId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", templateId=").append(templateId);
        sb.append(", triggerPeriodId=").append(triggerPeriodId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}