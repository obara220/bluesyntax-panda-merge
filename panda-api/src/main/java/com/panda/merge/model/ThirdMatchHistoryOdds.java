package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdMatchHistoryOdds implements Serializable {
    @ApiModelProperty(value = "数据来源ID+赛事源ID+供应商ID+玩法ID+盘口类型")
    private String id;

    @ApiModelProperty(value = "赛事源ID")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "供应商ID")
    private Integer bookId;

    @ApiModelProperty(value = "供应商中文名称")
    private String bookCnName;

    @ApiModelProperty(value = "供应商英文名称")
    private String bookEnName;

    @ApiModelProperty(value = "玩法ID")
    private Integer typeId;

    @ApiModelProperty(value = "玩法名称")
    private String typeName;

    @ApiModelProperty(value = "盘口类型(1:赛前盘;0:滚球盘)")
    private Integer marketType;

    @ApiModelProperty(value = "初始盘口值")
    private String value0;

    @ApiModelProperty(value = "即时盘口值")
    private String value;

    @ApiModelProperty(value = "投注项值")
    private String oddsJson;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookCnName() {
        return bookCnName;
    }

    public void setBookCnName(String bookCnName) {
        this.bookCnName = bookCnName;
    }

    public String getBookEnName() {
        return bookEnName;
    }

    public void setBookEnName(String bookEnName) {
        this.bookEnName = bookEnName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getMarketType() {
        return marketType;
    }

    public void setMarketType(Integer marketType) {
        this.marketType = marketType;
    }

    public String getValue0() {
        return value0;
    }

    public void setValue0(String value0) {
        this.value0 = value0;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOddsJson() {
        return oddsJson;
    }

    public void setOddsJson(String oddsJson) {
        this.oddsJson = oddsJson;
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
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", sportId=").append(sportId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", bookId=").append(bookId);
        sb.append(", bookCnName=").append(bookCnName);
        sb.append(", bookEnName=").append(bookEnName);
        sb.append(", typeId=").append(typeId);
        sb.append(", typeName=").append(typeName);
        sb.append(", marketType=").append(marketType);
        sb.append(", value0=").append(value0);
        sb.append(", value=").append(value);
        sb.append(", oddsJson=").append(oddsJson);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}