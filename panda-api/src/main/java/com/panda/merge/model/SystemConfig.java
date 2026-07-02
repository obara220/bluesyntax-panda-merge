package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class SystemConfig implements Serializable {
    private Long id;

    @ApiModelProperty(value = "后台配置名称.sys_name")
    private String sysName;

    @ApiModelProperty(value = "配置值.sys_value")
    private String sysValue;

    @ApiModelProperty(value = "配置类型.sys_type")
    private String sysType;

    @ApiModelProperty(value = "参数类型.input_type")
    private String inputType;

    @ApiModelProperty(value = "备注.remark")
    private String remark;

    @ApiModelProperty(value = "创建时间.create_time")
    private Long createTime;

    @ApiModelProperty(value = "更新时间.modify_time")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysValue() {
        return sysValue;
    }

    public void setSysValue(String sysValue) {
        this.sysValue = sysValue;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
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
        sb.append(", sysName=").append(sysName);
        sb.append(", sysValue=").append(sysValue);
        sb.append(", sysType=").append(sysType);
        sb.append(", inputType=").append(inputType);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}