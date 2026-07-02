package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

public class MatchSettleCheckInfo implements Serializable {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "结算比分或者事件ID")
    private Long settleScoreEventId;

    @ApiModelProperty(value = "核对状态：0未编辑1已编辑2已确认待核对3已确认核对成功4已确认核对失败")
    private Integer checkStatus;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "核对数据类型1数据商2用户输入")
    private Integer checkDataType;

    @ApiModelProperty(value = "源三方结算比分事件ID")
    private Long thirdSettleScoreEventId;

    @ApiModelProperty(value = "1.比分表的比分2.事件表的事件")
    private Integer checkType;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;

    @ApiModelProperty(value = "盘主队比分")
    private Integer firstT1;

    @ApiModelProperty(value = "盘客队比分")
    private Integer firstT2;

    @ApiModelProperty(value = "局主队比分")
    private Integer secondT1;

    @ApiModelProperty(value = "局客队比分")
    private Integer secondT2;

    @ApiModelProperty(value = "主客队")
    private String homeAway;

    @ApiModelProperty(value = "事件序号")
    private Integer eventOrder;

    @ApiModelProperty(value = "附加字段")
    private String extryInfo;

    @ApiModelProperty(value = "核对次序")
    private Integer checkNumber;

    @ApiModelProperty(value = "事件编码")
    private String eventCode;

    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "五分钟区间（5，10，15......90，每个区间+5；上半场绝杀49，下半场绝杀99，无进球0")
    private String fiveMinSection;
    @Transient
    private String fifteenMinSection;
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

    public Long getSettleScoreEventId() {
        return settleScoreEventId;
    }

    public void setSettleScoreEventId(Long settleScoreEventId) {
        this.settleScoreEventId = settleScoreEventId;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCheckDataType() {
        return checkDataType;
    }

    public void setCheckDataType(Integer checkDataType) {
        this.checkDataType = checkDataType;
    }

    public Long getThirdSettleScoreEventId() {
        return thirdSettleScoreEventId;
    }

    public void setThirdSettleScoreEventId(Long thirdSettleScoreEventId) {
        this.thirdSettleScoreEventId = thirdSettleScoreEventId;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getIsGrey() {
        return isGrey;
    }

    public void setIsGrey(Integer isGrey) {
        this.isGrey = isGrey;
    }

    public Integer getT1() {
        return t1;
    }

    public void setT1(Integer t1) {
        this.t1 = t1;
    }

    public Integer getT2() {
        return t2;
    }

    public void setT2(Integer t2) {
        this.t2 = t2;
    }

    public Integer getFirstT1() {
        return firstT1;
    }

    public void setFirstT1(Integer firstT1) {
        this.firstT1 = firstT1;
    }

    public Integer getFirstT2() {
        return firstT2;
    }

    public void setFirstT2(Integer firstT2) {
        this.firstT2 = firstT2;
    }

    public Integer getSecondT1() {
        return secondT1;
    }

    public void setSecondT1(Integer secondT1) {
        this.secondT1 = secondT1;
    }

    public Integer getSecondT2() {
        return secondT2;
    }

    public void setSecondT2(Integer secondT2) {
        this.secondT2 = secondT2;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Integer getEventOrder() {
        return eventOrder;
    }

    public void setEventOrder(Integer eventOrder) {
        this.eventOrder = eventOrder;
    }

    public String getExtryInfo() {
        return extryInfo;
    }

    public void setExtryInfo(String extryInfo) {
        this.extryInfo = extryInfo;
    }

    public Integer getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Integer checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Integer getGoWaterStatus() {
        return goWaterStatus;
    }

    public void setGoWaterStatus(Integer goWaterStatus) {
        this.goWaterStatus = goWaterStatus;
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

    public String getFiveMinSection() {
        return fiveMinSection;
    }

    public void setFiveMinSection(String fiveMinSection) {
        this.fiveMinSection = fiveMinSection;
    }
    public String getFifteenMinSection() {
        return fifteenMinSection;
    }

    public void setFifteenMinSection(String fifteenMinSection) {
        this.fifteenMinSection = fifteenMinSection;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", settleScoreEventId=").append(settleScoreEventId);
        sb.append(", checkStatus=").append(checkStatus);
        sb.append(", userName=").append(userName);
        sb.append(", checkDataType=").append(checkDataType);
        sb.append(", thirdSettleScoreEventId=").append(thirdSettleScoreEventId);
        sb.append(", checkType=").append(checkType);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", isGrey=").append(isGrey);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", firstT1=").append(firstT1);
        sb.append(", firstT2=").append(firstT2);
        sb.append(", secondT1=").append(secondT1);
        sb.append(", secondT2=").append(secondT2);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", eventOrder=").append(eventOrder);
        sb.append(", extryInfo=").append(extryInfo);
        sb.append(", checkNumber=").append(checkNumber);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", goWaterStatus=").append(goWaterStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", fiveMinSection=").append(fiveMinSection);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}