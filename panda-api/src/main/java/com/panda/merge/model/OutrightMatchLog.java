package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class OutrightMatchLog implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "操作目标id，赛事id,盘口id...")
    private Long operateTargetId;

    @ApiModelProperty(value = "操作人id")
    private Long operatorId;

    @ApiModelProperty(value = "操作人名称")
    private String operatorName;

    @ApiModelProperty(value = "操作模块")
    private String operatorModle;

    @ApiModelProperty(value = "操作批次编号(uuid)")
    private String operatorNumber;

    @ApiModelProperty(value = "操作内容")
    private String operatorText;

    @ApiModelProperty(value = "操作时间")
    private Long operatorTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOperateTargetId() {
        return operateTargetId;
    }

    public void setOperateTargetId(Long operateTargetId) {
        this.operateTargetId = operateTargetId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorModle() {
        return operatorModle;
    }

    public void setOperatorModle(String operatorModle) {
        this.operatorModle = operatorModle;
    }

    public String getOperatorNumber() {
        return operatorNumber;
    }

    public void setOperatorNumber(String operatorNumber) {
        this.operatorNumber = operatorNumber;
    }

    public String getOperatorText() {
        return operatorText;
    }

    public void setOperatorText(String operatorText) {
        this.operatorText = operatorText;
    }

    public Long getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Long operatorTime) {
        this.operatorTime = operatorTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", operateTargetId=").append(operateTargetId);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", operatorModle=").append(operatorModle);
        sb.append(", operatorNumber=").append(operatorNumber);
        sb.append(", operatorText=").append(operatorText);
        sb.append(", operatorTime=").append(operatorTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}