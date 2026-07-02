package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdSportMarketCategory20200423Delete implements Serializable {
    @ApiModelProperty(value = "表ID,自增")
    private Long id;

    @ApiModelProperty(value = "运动种类id.对应表sport.id")
    private Long sportId;

    @ApiModelProperty(value = "第三方玩法原始ID.")
    private String thirdMarketCategorySourceId;

    @ApiModelProperty(value = "例如:total")
    private String type;

    @ApiModelProperty(value = "玩法标识.")
    private String typeIdentify;

    @ApiModelProperty(value = "标准玩法id")
    private Long referenceId;

    @ApiModelProperty(value = "玩法名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "该玩法是否生效.1生效;0不生效.默认不生效")
    private Integer active;

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "投注项数量")
    private Integer fieldsNum;

    @ApiModelProperty(value = "盘口阶段id.对应system_item_dict.value")
    private String scopeId;

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

    @ApiModelProperty(value = "玩法构成盘口的数据格式.例如:Total[total]in15minutesinterval[from]-[to]玩法下:当前字段的值是from/to/total")
    private String dataFormate;

    private String description;

    private String remark;

    private Long createTime;

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

    public String getThirdMarketCategorySourceId() {
        return thirdMarketCategorySourceId;
    }

    public void setThirdMarketCategorySourceId(String thirdMarketCategorySourceId) {
        this.thirdMarketCategorySourceId = thirdMarketCategorySourceId;
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

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getFieldsNum() {
        return fieldsNum;
    }

    public void setFieldsNum(Integer fieldsNum) {
        this.fieldsNum = fieldsNum;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
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
        sb.append(", thirdMarketCategorySourceId=").append(thirdMarketCategorySourceId);
        sb.append(", type=").append(type);
        sb.append(", typeIdentify=").append(typeIdentify);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", active=").append(active);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", fieldsNum=").append(fieldsNum);
        sb.append(", scopeId=").append(scopeId);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
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