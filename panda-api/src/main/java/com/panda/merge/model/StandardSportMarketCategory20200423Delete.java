package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportMarketCategory20200423Delete implements Serializable {
    @ApiModelProperty(value = "表ID,自增")
    private Long id;

    @ApiModelProperty(value = "运动种类id.对应表sport.id")
    private Long sportId;

    @ApiModelProperty(value = "例如:total")
    private String type;

    @ApiModelProperty(value = "玩法标识.")
    private String typeIdentify;

    @ApiModelProperty(value = "激活.激活为1,否则为0.默认1")
    private Integer active;

    @ApiModelProperty(value = "玩法名称编码.用于多语言.")
    private Long nameCode;

    @ApiModelProperty(value = "玩法状态.0已关闭;1已创建;2待二次校验;3已开启;.默认已创建")
    private Integer status;

    @ApiModelProperty(value = "盘口阶段id.对应system_item_dict.value")
    private String scopeId;

    @ApiModelProperty(value = "是否属于多盘口玩法.0no;1yes.默认no")
    private Integer multiMarket;

    @ApiModelProperty(value = "排序值.")
    private Integer orderNo;

    @ApiModelProperty(value = "投注项数量")
    private Integer fieldsNum;

    @ApiModelProperty(value = "是否角球玩法Y：是N：否")
    private String cornerCategory;

    @ApiModelProperty(value = "是否罚牌玩法Y:是N:否")
    private String penaltyCardCategory;

    @ApiModelProperty(value = "附加字段1")
    private String addition1;

    @ApiModelProperty(value = "附加字段2")
    private String addition2;

    @ApiModelProperty(value = "附加字段3")
    private String addition3;

    @ApiModelProperty(value = "附加字段4")
    private String addition4;

    @ApiModelProperty(value = "附加字段5")
    private String addition5;

    @ApiModelProperty(value = "赔率切换system_item_dict.id列表保存多个用逗号隔开")
    private String oddsSwitch;

    @ApiModelProperty(value = "选项展示Yes展示No关闭")
    private String optionToShow;

    @ApiModelProperty(value = "模板展示")
    private Long templateShowing;

    @ApiModelProperty(value = "玩法构成盘口的数据格式.例如:Total[total]in15minutesinterval[from]-[to]玩法下:当前字段的值是from/to/total")
    private String dataFormate;

    @ApiModelProperty(value = "玩法详细说明")
    private String description;

    @ApiModelProperty(value = "备注.长度不超过130个字符.")
    private String remark;

    @ApiModelProperty(value = "创建时间.UTC时间,精确到毫秒")
    private Long createTime;

    @ApiModelProperty(value = "更新时间.UTC时间,精确到毫秒")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeIdentify() {
        return typeIdentify;
    }

    public void setTypeIdentify(String typeIdentify) {
        this.typeIdentify = typeIdentify;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public Integer getMultiMarket() {
        return multiMarket;
    }

    public void setMultiMarket(Integer multiMarket) {
        this.multiMarket = multiMarket;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getFieldsNum() {
        return fieldsNum;
    }

    public void setFieldsNum(Integer fieldsNum) {
        this.fieldsNum = fieldsNum;
    }

    public String getCornerCategory() {
        return cornerCategory;
    }

    public void setCornerCategory(String cornerCategory) {
        this.cornerCategory = cornerCategory;
    }

    public String getPenaltyCardCategory() {
        return penaltyCardCategory;
    }

    public void setPenaltyCardCategory(String penaltyCardCategory) {
        this.penaltyCardCategory = penaltyCardCategory;
    }

    public String getAddition1() {
        return addition1;
    }

    public void setAddition1(String addition1) {
        this.addition1 = addition1;
    }

    public String getAddition2() {
        return addition2;
    }

    public void setAddition2(String addition2) {
        this.addition2 = addition2;
    }

    public String getAddition3() {
        return addition3;
    }

    public void setAddition3(String addition3) {
        this.addition3 = addition3;
    }

    public String getAddition4() {
        return addition4;
    }

    public void setAddition4(String addition4) {
        this.addition4 = addition4;
    }

    public String getAddition5() {
        return addition5;
    }

    public void setAddition5(String addition5) {
        this.addition5 = addition5;
    }

    public String getOddsSwitch() {
        return oddsSwitch;
    }

    public void setOddsSwitch(String oddsSwitch) {
        this.oddsSwitch = oddsSwitch;
    }

    public String getOptionToShow() {
        return optionToShow;
    }

    public void setOptionToShow(String optionToShow) {
        this.optionToShow = optionToShow;
    }

    public Long getTemplateShowing() {
        return templateShowing;
    }

    public void setTemplateShowing(Long templateShowing) {
        this.templateShowing = templateShowing;
    }

    public String getDataFormate() {
        return dataFormate;
    }

    public void setDataFormate(String dataFormate) {
        this.dataFormate = dataFormate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        sb.append(", sportId=").append(sportId);
        sb.append(", type=").append(type);
        sb.append(", typeIdentify=").append(typeIdentify);
        sb.append(", active=").append(active);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", status=").append(status);
        sb.append(", scopeId=").append(scopeId);
        sb.append(", multiMarket=").append(multiMarket);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", fieldsNum=").append(fieldsNum);
        sb.append(", cornerCategory=").append(cornerCategory);
        sb.append(", penaltyCardCategory=").append(penaltyCardCategory);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", oddsSwitch=").append(oddsSwitch);
        sb.append(", optionToShow=").append(optionToShow);
        sb.append(", templateShowing=").append(templateShowing);
        sb.append(", dataFormate=").append(dataFormate);
        sb.append(", description=").append(description);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}