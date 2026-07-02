package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleSpMarket implements Serializable {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "玩法Id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "子玩法Id")
    private Long childMarketCategoryId;

    @ApiModelProperty(value = "玩法英文名")
    private String categoryNameEn;

    @ApiModelProperty(value = "玩法中文名")
    private String categoryNameCn;

    @ApiModelProperty(value = "1.比分表的比分2.事件表的事件")
    private Integer checkType;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public Long getChildMarketCategoryId() {
        return childMarketCategoryId;
    }

    public void setChildMarketCategoryId(Long childMarketCategoryId) {
        this.childMarketCategoryId = childMarketCategoryId;
    }

    public String getCategoryNameEn() {
        return categoryNameEn;
    }

    public void setCategoryNameEn(String categoryNameEn) {
        this.categoryNameEn = categoryNameEn;
    }

    public String getCategoryNameCn() {
        return categoryNameCn;
    }

    public void setCategoryNameCn(String categoryNameCn) {
        this.categoryNameCn = categoryNameCn;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
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
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", sportId=").append(sportId);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", childMarketCategoryId=").append(childMarketCategoryId);
        sb.append(", categoryNameEn=").append(categoryNameEn);
        sb.append(", categoryNameCn=").append(categoryNameCn);
        sb.append(", checkType=").append(checkType);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}