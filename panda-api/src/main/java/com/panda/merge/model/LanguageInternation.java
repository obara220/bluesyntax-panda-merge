package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class LanguageInternation implements Serializable {
    private Long id;

    @ApiModelProperty(value = "文字对应的编码")
    private Long nameCode;

    @ApiModelProperty(value = "1人工2系统")
    private Integer flag;

    @ApiModelProperty(value = "数据来源编码.SRBC等")
    private String dataSourceCode;

    @ApiModelProperty(value = "语言类型.zhjpen等")
    private String languageType;

    @ApiModelProperty(value = "文字内容.name_code代表文字在language_type代表语言下的具体内容.比如:中国在英文的表示是China.")
    private String text;

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

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        sb.append(", nameCode=").append(nameCode);
        sb.append(", flag=").append(flag);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", languageType=").append(languageType);
        sb.append(", text=").append(text);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}