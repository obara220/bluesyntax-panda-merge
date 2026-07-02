package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardMarketCategory implements Serializable {
    @ApiModelProperty(value = "表ID,自增")
    private Long id;

    @ApiModelProperty(value = "玩法名称编码.用于多语言.")
    private Long nameCode;

    @ApiModelProperty(value = "投注项数量")
    private Integer fieldsNum;

    @ApiModelProperty(value = "是否属于多盘口玩法.0no;1yes.默认no")
    private Integer multiMarket;

    @ApiModelProperty(value = "支持赔率类型,1：支持欧式、英式、美式、香港、马来、印尼赔率；2：支持欧式、英式、美式赔率")
    private String supportOdds;

    @ApiModelProperty(value = "PC模板展示")
    private Integer templatePc;

    @ApiModelProperty(value = "h5模板展示")
    private Integer templateH5;

    @ApiModelProperty(value = "玩法状态.0无效;1有效")
    private Integer status;

    @ApiModelProperty(value = "对外商户状态0:关闭;1:开启")
    private Integer merchantStatus;

    @ApiModelProperty(value = "排序值.")
    private Integer orderNo;

    @ApiModelProperty(value = "创建时间.UTC时间,精确到毫秒")
    private Long createTime;

    @ApiModelProperty(value = "更新时间.UTC时间,精确到毫秒")
    private Long modifyTime;

    @ApiModelProperty(value = "客户端PC模板展示")
    private Integer templatePcClient;

    @ApiModelProperty(value = "客户端h5模板展示")
    private Integer templateH5Client;

    @ApiModelProperty(value = "AO玩法状态.0无效;1有效")
    private Integer aoStatus;

    @ApiModelProperty(value = "对外商户编码集合")
    private String merchantApiCodeList;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getFieldsNum() {
        return fieldsNum;
    }

    public void setFieldsNum(Integer fieldsNum) {
        this.fieldsNum = fieldsNum;
    }

    public Integer getMultiMarket() {
        return multiMarket;
    }

    public void setMultiMarket(Integer multiMarket) {
        this.multiMarket = multiMarket;
    }

    public String getSupportOdds() {
        return supportOdds;
    }

    public void setSupportOdds(String supportOdds) {
        this.supportOdds = supportOdds;
    }

    public Integer getTemplatePc() {
        return templatePc;
    }

    public void setTemplatePc(Integer templatePc) {
        this.templatePc = templatePc;
    }

    public Integer getTemplateH5() {
        return templateH5;
    }

    public void setTemplateH5(Integer templateH5) {
        this.templateH5 = templateH5;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMerchantStatus() {
        return merchantStatus;
    }

    public void setMerchantStatus(Integer merchantStatus) {
        this.merchantStatus = merchantStatus;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
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

    public Integer getTemplatePcClient() {
        return templatePcClient;
    }

    public void setTemplatePcClient(Integer templatePcClient) {
        this.templatePcClient = templatePcClient;
    }

    public Integer getTemplateH5Client() {
        return templateH5Client;
    }

    public void setTemplateH5Client(Integer templateH5Client) {
        this.templateH5Client = templateH5Client;
    }

    public Integer getAoStatus() {
        return aoStatus;
    }

    public void setAoStatus(Integer aoStatus) {
        this.aoStatus = aoStatus;
    }

    public String getMerchantApiCodeList() {
        return merchantApiCodeList;
    }

    public void setMerchantApiCodeList(String merchantApiCodeList) {
        this.merchantApiCodeList = merchantApiCodeList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", fieldsNum=").append(fieldsNum);
        sb.append(", multiMarket=").append(multiMarket);
        sb.append(", supportOdds=").append(supportOdds);
        sb.append(", templatePc=").append(templatePc);
        sb.append(", templateH5=").append(templateH5);
        sb.append(", status=").append(status);
        sb.append(", merchantStatus=").append(merchantStatus);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", templatePcClient=").append(templatePcClient);
        sb.append(", templateH5Client=").append(templateH5Client);
        sb.append(", aoStatus=").append(aoStatus);
        sb.append(", merchantApiCodeList=").append(merchantApiCodeList);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}