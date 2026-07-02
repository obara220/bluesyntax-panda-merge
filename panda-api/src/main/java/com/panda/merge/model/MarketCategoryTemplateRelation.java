package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MarketCategoryTemplateRelation implements Serializable {
    private Long id;

    @ApiModelProperty(value = "玩法ID")
    private Integer marketCategoryId;

    @ApiModelProperty(value = "模板ID")
    private Long templateId;

    private Integer dynamicFlag;

    private Integer sportId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Integer marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getDynamicFlag() {
		return dynamicFlag;
	}

	public void setDynamicFlag(Integer dynamicFlag) {
		this.dynamicFlag = dynamicFlag;
	}

	public Integer getSportId() {
		return sportId;
	}

	public void setSportId(Integer sportId) {
		this.sportId = sportId;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", templateId=").append(templateId);
        sb.append(", dynamicFlag=").append(dynamicFlag);
        sb.append(", sportId=").append(sportId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}