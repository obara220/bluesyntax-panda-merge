package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMarketCategoryField implements Serializable {
    @ApiModelProperty(value = "表ID，自增")
    private Long id;

    @ApiModelProperty(value = "第三方玩法id。")
    private Long marketCategoryId;

    @ApiModelProperty(value = "投注项名称编码.用于多语言.")
    private Long nameCode;

    @ApiModelProperty(value = "第三方投注项原始ID。")
    private String thirdSourceId;

    @ApiModelProperty(value = "标准玩法投注项id。")
    private Long referenceId;

    @ApiModelProperty(value = "排序值。")
    private Integer orderNo;

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "创建时间.UTC时间，精确到毫秒")
    private Long createTime;

    @ApiModelProperty(value = "更新时间.UTC时间，精确到毫秒")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
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

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
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
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", thirdSourceId=").append(thirdSourceId);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}