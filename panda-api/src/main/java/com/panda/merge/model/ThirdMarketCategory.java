package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMarketCategory implements Serializable {
    @ApiModelProperty(value = "表ID,自增")
    private Long id;

    @ApiModelProperty(value = "玩法名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "投注项数量")
    private Integer fieldsNum;

    @ApiModelProperty(value = "第三方玩法原始ID.")
    private String thirdSourceId;

    @ApiModelProperty(value = "标准玩法id")
    private Long referenceId;

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "该玩法是否生效.1生效;0不生效.默认不生效")
    private Integer active;

    private Long createTime;

    private Long modifyTime;

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

    public String getThirdSourceId() {
        return thirdSourceId;
    }

    public void setThirdSourceId(String thirdSourceId) {
        this.thirdSourceId = thirdSourceId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
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
        sb.append(", nameCode=").append(nameCode);
        sb.append(", fieldsNum=").append(fieldsNum);
        sb.append(", thirdSourceId=").append(thirdSourceId);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", active=").append(active);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}