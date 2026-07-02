package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportRegion implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "如果当前记录对外起作用,则该visible为1,否则为0.默认true")
    private Integer visible;

    @ApiModelProperty(value = "区域名称编码.用于多语言.存放体育区域名称")
    private Long nameCode;

    @ApiModelProperty(value = "英文名称")
    private String introductionEn;

    @ApiModelProperty(value = "中文名称")
    private String introduction;

    @ApiModelProperty(value = "排序序号")
    private Integer orderNumber;

    private String nationalFlagUrl;

    @ApiModelProperty(value = "区域名称大写字母拼写")
    private String spell;

    private String remark;

    private Long createTime;

    private Long modifyTime;

    @ApiModelProperty(value = "繁体名称")
    private String introductionZh;

    @ApiModelProperty(value = "越南语名称")
    private String introductionVi;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public String getIntroductionEn() {
        return introductionEn;
    }

    public void setIntroductionEn(String introductionEn) {
        this.introductionEn = introductionEn;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getNationalFlagUrl() {
        return nationalFlagUrl;
    }

    public void setNationalFlagUrl(String nationalFlagUrl) {
        this.nationalFlagUrl = nationalFlagUrl;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
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

    public String getIntroductionZh() {
        return introductionZh;
    }

    public void setIntroductionZh(String introductionZh) {
        this.introductionZh = introductionZh;
    }

    public String getIntroductionVi() {
        return introductionVi;
    }

    public void setIntroductionVi(String introductionVi) {
        this.introductionVi = introductionVi;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", visible=").append(visible);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", introductionEn=").append(introductionEn);
        sb.append(", introduction=").append(introduction);
        sb.append(", orderNumber=").append(orderNumber);
        sb.append(", nationalFlagUrl=").append(nationalFlagUrl);
        sb.append(", spell=").append(spell);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", introductionZh=").append(introductionZh);
        sb.append(", introductionVi=").append(introductionVi);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}