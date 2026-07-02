package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdSportOddsFieldsTemplet2020421Delete implements Serializable {
    @ApiModelProperty(value = "表ID，自增")
    private Long id;

    @ApiModelProperty(value = "第三方玩法id。")
    private Long marketCategoryId;

    @ApiModelProperty(value = "标准投注项id。")
    private Long standardTempletId;

    @ApiModelProperty(value = "第三方投注项原始ID。")
    private String thirdTempletSourceId;

    @ApiModelProperty(value = "投注项名称编码.用于多语言.")
    private Long nameCode;

    @ApiModelProperty(value = "投注项名称。")
    private String name;

    @ApiModelProperty(value = "排序值。")
    private Integer orderNo;

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

    public Long getStandardTempletId() {
        return standardTempletId;
    }

    public void setStandardTempletId(Long standardTempletId) {
        this.standardTempletId = standardTempletId;
    }

    public String getThirdTempletSourceId() {
        return thirdTempletSourceId;
    }

    public void setThirdTempletSourceId(String thirdTempletSourceId) {
        this.thirdTempletSourceId = thirdTempletSourceId;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
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
        sb.append(", standardTempletId=").append(standardTempletId);
        sb.append(", thirdTempletSourceId=").append(thirdTempletSourceId);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", name=").append(name);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}