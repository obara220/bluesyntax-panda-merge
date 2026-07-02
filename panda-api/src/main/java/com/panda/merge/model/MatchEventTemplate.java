package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchEventTemplate implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Boolean sportId;

    @ApiModelProperty(value = "序号")
    private Integer orderNo;

    @ApiModelProperty(value = "触发事件编码")
    private String triggerCode;

    @ApiModelProperty(value = "触发事件阶段ID")
    private String triggerPeriodId;

    @ApiModelProperty(value = "事件编码")
    private String eventCode;

    @ApiModelProperty(value = "事件文本模板")
    private String templateText;

    @ApiModelProperty(value = "事件格式化模板")
    private String templateFormat;

    @ApiModelProperty(value = "赛事事件模板号1阶段2次序3开球")
    private Boolean templateNo;

    @ApiModelProperty(value = "事件审核倒计时时间默认值")
    private Integer auditTime;

    @ApiModelProperty(value = "事件结算倒计时时间默认值")
    private Integer billTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSportId() {
        return sportId;
    }

    public void setSportId(Boolean sportId) {
        this.sportId = sportId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getTriggerCode() {
        return triggerCode;
    }

    public void setTriggerCode(String triggerCode) {
        this.triggerCode = triggerCode;
    }

    public String getTriggerPeriodId() {
        return triggerPeriodId;
    }

    public void setTriggerPeriodId(String triggerPeriodId) {
        this.triggerPeriodId = triggerPeriodId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

    public String getTemplateFormat() {
        return templateFormat;
    }

    public void setTemplateFormat(String templateFormat) {
        this.templateFormat = templateFormat;
    }

    public Boolean getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(Boolean templateNo) {
        this.templateNo = templateNo;
    }

    public Integer getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Integer auditTime) {
        this.auditTime = auditTime;
    }

    public Integer getBillTime() {
        return billTime;
    }

    public void setBillTime(Integer billTime) {
        this.billTime = billTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", triggerCode=").append(triggerCode);
        sb.append(", triggerPeriodId=").append(triggerPeriodId);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", templateText=").append(templateText);
        sb.append(", templateFormat=").append(templateFormat);
        sb.append(", templateNo=").append(templateNo);
        sb.append(", auditTime=").append(auditTime);
        sb.append(", billTime=").append(billTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}