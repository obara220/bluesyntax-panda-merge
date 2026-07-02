package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class SystemOperateLog implements Serializable {
    private Long id;

    @ApiModelProperty(value = "每一次账号登录系统会自动生成一个编号作为该账号本次登录的唯一标识,时间（20191019）+7个随机小写字母，比如20191019hqjkgba")
    private String loginCode;

    @ApiModelProperty(value = "账户")
    private String account;

    @ApiModelProperty(value = "账户编码,账户的原始关联id或者code编码")
    private String accountCode;

    @ApiModelProperty(value = "部门id")
    private String departmentId;

    @ApiModelProperty(value = "系统类型大后台Main_Mangement_System，赛程管理Match_Management_System等")
    private String sysType;

    @ApiModelProperty(value = "取值为全部+大后台的所有资源模块名：全部、子后台管理、组织架构管理、账号管理、角色管理、资源管理")
    private String sysModel;

    @ApiModelProperty(value = "是否成功Y是，N否Oother")
    private String isSuccess;

    @ApiModelProperty(value = "事件")
    private String event;

    @ApiModelProperty(value = "数据id或者code唯一标记数据")
    private String dataCode;

    @ApiModelProperty(value = "操作前")
    private String beforeOpt;

    @ApiModelProperty(value = "操作后")
    private String afterOpt;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "备注允许64个汉字，126个英文字符")
    private String remark;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getSysModel() {
        return sysModel;
    }

    public void setSysModel(String sysModel) {
        this.sysModel = sysModel;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getBeforeOpt() {
        return beforeOpt;
    }

    public void setBeforeOpt(String beforeOpt) {
        this.beforeOpt = beforeOpt;
    }

    public String getAfterOpt() {
        return afterOpt;
    }

    public void setAfterOpt(String afterOpt) {
        this.afterOpt = afterOpt;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", loginCode=").append(loginCode);
        sb.append(", account=").append(account);
        sb.append(", accountCode=").append(accountCode);
        sb.append(", departmentId=").append(departmentId);
        sb.append(", sysType=").append(sysType);
        sb.append(", sysModel=").append(sysModel);
        sb.append(", isSuccess=").append(isSuccess);
        sb.append(", event=").append(event);
        sb.append(", dataCode=").append(dataCode);
        sb.append(", beforeOpt=").append(beforeOpt);
        sb.append(", afterOpt=").append(afterOpt);
        sb.append(", createTime=").append(createTime);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}