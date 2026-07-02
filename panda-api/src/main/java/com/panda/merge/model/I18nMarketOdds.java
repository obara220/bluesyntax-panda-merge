package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class I18nMarketOdds implements Serializable {
    @ApiModelProperty(value = "三方投注项源id")
    private String oddsSourceId;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "语言类型.zhjpen等")
    private String languageType;

    @ApiModelProperty(value = "文字内容.")
    private String text;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public String getOddsSourceId() {
        return oddsSourceId;
    }

    public void setOddsSourceId(String oddsSourceId) {
        this.oddsSourceId = oddsSourceId;
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
        sb.append(", oddsSourceId=").append(oddsSourceId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", languageType=").append(languageType);
        sb.append(", text=").append(text);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}