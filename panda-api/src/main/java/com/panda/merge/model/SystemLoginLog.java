package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class SystemLoginLog implements Serializable {
    private Long id;

    @ApiModelProperty(value = "每一次账号登录系统会自动生成一个编号作为该账号本次登录的唯一标识,时间（20191019）+7个随机小写字母，比如20191019hqjkgba")
    private String loginCode;

    @ApiModelProperty(value = "账户")
    private String account;

    @ApiModelProperty(value = "账户编码,账户的原始关联id或者code编码")
    private String accountCode;

    @ApiModelProperty(value = "角色名")
    private String role;

    @ApiModelProperty(value = "系统类型大后台Main_Mangement_System，赛程管理Match_Management_System等")
    private String sysType;

    @ApiModelProperty(value = "取值为全部+大后台的所有资源模块名：全部、子后台管理、组织架构管理、账号管理、角色管理、资源管理")
    private String sysModel;

    @ApiModelProperty(value = "是否成功Y是，N否Oother")
    private String isSuccess;

    @ApiModelProperty(value = "事件:登录Login，登出Logout,会话自动过期Session_Expired")
    private String event;

    @ApiModelProperty(value = "状态:正常登录Success，登录失败Failed，异常登录Abnormal")
    private String status;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "登录地址")
    private String loginAddress;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginAddress() {
        return loginAddress;
    }

    public void setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
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
        sb.append(", role=").append(role);
        sb.append(", sysType=").append(sysType);
        sb.append(", sysModel=").append(sysModel);
        sb.append(", isSuccess=").append(isSuccess);
        sb.append(", event=").append(event);
        sb.append(", status=").append(status);
        sb.append(", ip=").append(ip);
        sb.append(", loginAddress=").append(loginAddress);
        sb.append(", createTime=").append(createTime);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}