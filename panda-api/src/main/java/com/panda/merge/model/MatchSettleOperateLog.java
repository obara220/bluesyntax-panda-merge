package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleOperateLog implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "操作页面")
    private String operateModule;

    @ApiModelProperty(value = "操作对象Id")
    private String operateId;

    @ApiModelProperty(value = "操作玩法名称")
    private String operateName;

    @ApiModelProperty(value = "操作对象赛事管理id")
    private String operateMatchId;

    @ApiModelProperty(value = "赛事信息")
    private String operateMatchName;

    @ApiModelProperty(value = "操作类型")
    private String operateType;

    @ApiModelProperty(value = "参数名称")
    private String operateParaName;

    @ApiModelProperty(value = "操作前")
    private String operateForwText;

    @ApiModelProperty(value = "操作后")
    private String operateRearText;

    @ApiModelProperty(value = "事件序号")
    private String eventOrder;

    @ApiModelProperty(value = "赛事阶段")
    private Long periodId;

    @ApiModelProperty(value = "操作员名称")
    private String operateUserName;

    @ApiModelProperty(value = "操作用户ip地址")
    private String ipAddress;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "操作时间")
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

    public String getOperateModule() {
        return operateModule;
    }

    public void setOperateModule(String operateModule) {
        this.operateModule = operateModule;
    }

    public String getOperateId() {
        return operateId;
    }

    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public String getOperateMatchId() {
        return operateMatchId;
    }

    public void setOperateMatchId(String operateMatchId) {
        this.operateMatchId = operateMatchId;
    }

    public String getOperateMatchName() {
        return operateMatchName;
    }

    public void setOperateMatchName(String operateMatchName) {
        this.operateMatchName = operateMatchName;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateParaName() {
        return operateParaName;
    }

    public void setOperateParaName(String operateParaName) {
        this.operateParaName = operateParaName;
    }

    public String getOperateForwText() {
        return operateForwText;
    }

    public void setOperateForwText(String operateForwText) {
        this.operateForwText = operateForwText;
    }

    public String getOperateRearText() {
        return operateRearText;
    }

    public void setOperateRearText(String operateRearText) {
        this.operateRearText = operateRearText;
    }

    public String getEventOrder() {
        return eventOrder;
    }

    public void setEventOrder(String eventOrder) {
        this.eventOrder = eventOrder;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        sb.append(", operateModule=").append(operateModule);
        sb.append(", operateId=").append(operateId);
        sb.append(", operateName=").append(operateName);
        sb.append(", operateMatchId=").append(operateMatchId);
        sb.append(", operateMatchName=").append(operateMatchName);
        sb.append(", operateType=").append(operateType);
        sb.append(", operateParaName=").append(operateParaName);
        sb.append(", operateForwText=").append(operateForwText);
        sb.append(", operateRearText=").append(operateRearText);
        sb.append(", eventOrder=").append(eventOrder);
        sb.append(", periodId=").append(periodId);
        sb.append(", operateUserName=").append(operateUserName);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}