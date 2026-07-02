package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchEventCode implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Integer sportId;

    @ApiModelProperty(value = "事件编码")
    private String eventCode;

    @ApiModelProperty(value = "事件名称")
    private String eventName;

    @ApiModelProperty(value = "是否业务关注事件(info到common）：1关注0不关注")
    private Boolean confirmEvent;

    @ApiModelProperty(value = "是否生成标准赛果(third到standard）：1生成0不生成")
    private Boolean standardEvent;

    @ApiModelProperty(value = "是否需要推送WEB页面：1推送0不推送")
    private Boolean pushEvent;

    @ApiModelProperty(value = "是否报错关注：1关注0不关注")
    private Boolean errorEvent;

    @ApiModelProperty(value = "是否特殊编码：1是0否")
    private Boolean specialEvent;

    @ApiModelProperty(value = "赛事事件模板号1阶段2次序3开球")
    private Integer templateNo;

    @ApiModelProperty(value = "新增事件编码：1是0否")
    private Boolean addEvent;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Boolean getConfirmEvent() {
        return confirmEvent;
    }

    public void setConfirmEvent(Boolean confirmEvent) {
        this.confirmEvent = confirmEvent;
    }

    public Boolean getStandardEvent() {
        return standardEvent;
    }

    public void setStandardEvent(Boolean standardEvent) {
        this.standardEvent = standardEvent;
    }

    public Boolean getPushEvent() {
        return pushEvent;
    }

    public void setPushEvent(Boolean pushEvent) {
        this.pushEvent = pushEvent;
    }

    public Boolean getErrorEvent() {
        return errorEvent;
    }

    public void setErrorEvent(Boolean errorEvent) {
        this.errorEvent = errorEvent;
    }

    public Boolean getSpecialEvent() {
        return specialEvent;
    }

    public void setSpecialEvent(Boolean specialEvent) {
        this.specialEvent = specialEvent;
    }

    public Integer getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(Integer templateNo) {
        this.templateNo = templateNo;
    }

    public Boolean getAddEvent() {
        return addEvent;
    }

    public void setAddEvent(Boolean addEvent) {
        this.addEvent = addEvent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", eventName=").append(eventName);
        sb.append(", confirmEvent=").append(confirmEvent);
        sb.append(", standardEvent=").append(standardEvent);
        sb.append(", pushEvent=").append(pushEvent);
        sb.append(", errorEvent=").append(errorEvent);
        sb.append(", specialEvent=").append(specialEvent);
        sb.append(", templateNo=").append(templateNo);
        sb.append(", addEvent=").append(addEvent);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}