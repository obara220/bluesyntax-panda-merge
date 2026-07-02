package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class I18nnamesOutrightMatchName implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "玩法赛事投注项")
    private Long matchCategoryFiled;

    @ApiModelProperty(value = "1三方赛事2标准赛事3投注项")
    private Integer type;

    @ApiModelProperty(value = "数据源PASRBG")
    private String dataSourceCode;

    @ApiModelProperty(value = "语言类型")
    private String languageType;

    @ApiModelProperty(value = "值")
    private String text;

    @ApiModelProperty(value = "1人工2系统")
    private Integer flag;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modfiyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchCategoryFiled() {
        return matchCategoryFiled;
    }

    public void setMatchCategoryFiled(Long matchCategoryFiled) {
        this.matchCategoryFiled = matchCategoryFiled;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModfiyTime() {
        return modfiyTime;
    }

    public void setModfiyTime(Long modfiyTime) {
        this.modfiyTime = modfiyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", matchCategoryFiled=").append(matchCategoryFiled);
        sb.append(", type=").append(type);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", languageType=").append(languageType);
        sb.append(", text=").append(text);
        sb.append(", flag=").append(flag);
        sb.append(", createTime=").append(createTime);
        sb.append(", modfiyTime=").append(modfiyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}