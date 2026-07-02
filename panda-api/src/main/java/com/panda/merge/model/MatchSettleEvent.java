package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

public class MatchSettleEvent implements Serializable {
    private Long id;

    private Long standardMatchId;

    private Long periodId;

    private Long thirdEventSourceId;

    @ApiModelProperty(value = "1.进球比分事件2.进球方式和球员")
    private Integer eventType;

    private String eventCode;

    private Integer t1;

    private Integer t2;

    @ApiModelProperty(value = "结算编码")
    private String settleNum;

    @ApiModelProperty(value = "事件次序")
    private Integer eventOrder;

    private String eventName;

    @ApiModelProperty(value = "1.未确认2.已确认3.已结算")
    private Integer status;

    private String homeAway;

    @ApiModelProperty(value = "球员名")
    private String playerName;

    @ApiModelProperty(value = "球员namecode")
    private String playerNameCode;

    private String dataSourceCode;

    private Long sportId;

    @ApiModelProperty(value = "附加字段:进球方式等")
    private String extryInfo;

    @ApiModelProperty(value = "盘数")
    private Integer firstNum;

    @ApiModelProperty(value = "局数")
    private Integer secondNum;

    @ApiModelProperty(value = "主队盘比分")
    private Integer firstT1;

    @ApiModelProperty(value = "客队盘比分")
    private Integer firstT2;

    @ApiModelProperty(value = "主队局比分")
    private Integer secondT1;

    @ApiModelProperty(value = "客队局比分")
    private Integer secondT2;

    @ApiModelProperty(value = "操作类型:1.结算2.回滚结算3.重新结算")
    private Integer operateType;

    @ApiModelProperty(value = "操作人")
    private String operater;

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "结算次数")
    private Integer settleTimes;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;

    @ApiModelProperty(value = "二次结算原因")
    private Integer settleReason;

    @ApiModelProperty(value = "其他详细原因")
    private String settleReasonDetail;

    @ApiModelProperty(value = "结算事件冻结0:未冻结1:冻结")
    private Integer settleFreeze;

    @ApiModelProperty(value = "是否次序结算1是0不是")
    private Integer isSequenceSettle;

    @ApiModelProperty(value = "当前已核对人员序号")
    private Integer checkNumber;

    @ApiModelProperty(value = "是否自动结算:1是0不是")
    private Integer isAutoSettle;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    @ApiModelProperty(value = "有删除事件:1是0否")
    private Integer hasDeleteEvent;

    @ApiModelProperty(value = "附加字段1")
    private String addition1;

    @ApiModelProperty(value = "附加字段2")
    private String addition2;

    @ApiModelProperty(value = "当前事件状态：0无1灰色区间2删除事件")
    private Integer currentEventStatus;

    private Long modifyTime;

    private Long createTime;

    @ApiModelProperty(value = "五分钟区间（5，10，15......90，每个区间+5；上半场绝杀49，下半场绝杀99，无进球0）")
    private String fiveMinSection;
    @Transient
    private String fifteenMinSection;
    @ApiModelProperty(value = "事件时间")
    private Long eventTime;

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

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public Long getThirdEventSourceId() {
        return thirdEventSourceId;
    }

    public void setThirdEventSourceId(Long thirdEventSourceId) {
        this.thirdEventSourceId = thirdEventSourceId;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
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

    public String getSettleNum() {
        return settleNum;
    }

    public void setSettleNum(String settleNum) {
        this.settleNum = settleNum;
    }

    public Integer getEventOrder() {
        return eventOrder;
    }

    public void setEventOrder(Integer eventOrder) {
        this.eventOrder = eventOrder;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerNameCode() {
        return playerNameCode;
    }

    public void setPlayerNameCode(String playerNameCode) {
        this.playerNameCode = playerNameCode;
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

    public String getExtryInfo() {
        return extryInfo;
    }

    public void setExtryInfo(String extryInfo) {
        this.extryInfo = extryInfo;
    }

    public Integer getFirstNum() {
        return firstNum;
    }

    public void setFirstNum(Integer firstNum) {
        this.firstNum = firstNum;
    }

    public Integer getSecondNum() {
        return secondNum;
    }

    public void setSecondNum(Integer secondNum) {
        this.secondNum = secondNum;
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

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getSettleTimes() {
        return settleTimes;
    }

    public void setSettleTimes(Integer settleTimes) {
        this.settleTimes = settleTimes;
    }

    public Integer getSettleCount() {
        return settleCount;
    }

    public void setSettleCount(Integer settleCount) {
        this.settleCount = settleCount;
    }

    public Integer getSettleReason() {
        return settleReason;
    }

    public void setSettleReason(Integer settleReason) {
        this.settleReason = settleReason;
    }

    public String getSettleReasonDetail() {
        return settleReasonDetail;
    }

    public void setSettleReasonDetail(String settleReasonDetail) {
        this.settleReasonDetail = settleReasonDetail;
    }

    public Integer getSettleFreeze() {
        return settleFreeze;
    }

    public void setSettleFreeze(Integer settleFreeze) {
        this.settleFreeze = settleFreeze;
    }

    public Integer getIsSequenceSettle() {
        return isSequenceSettle;
    }

    public void setIsSequenceSettle(Integer isSequenceSettle) {
        this.isSequenceSettle = isSequenceSettle;
    }

    public Integer getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Integer checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Integer getIsAutoSettle() {
        return isAutoSettle;
    }

    public void setIsAutoSettle(Integer isAutoSettle) {
        this.isAutoSettle = isAutoSettle;
    }

    public Integer getIsGrey() {
        return isGrey;
    }

    public void setIsGrey(Integer isGrey) {
        this.isGrey = isGrey;
    }

    public Integer getGoWaterStatus() {
        return goWaterStatus;
    }

    public void setGoWaterStatus(Integer goWaterStatus) {
        this.goWaterStatus = goWaterStatus;
    }

    public Integer getHasDeleteEvent() {
        return hasDeleteEvent;
    }

    public void setHasDeleteEvent(Integer hasDeleteEvent) {
        this.hasDeleteEvent = hasDeleteEvent;
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

    public Integer getCurrentEventStatus() {
        return currentEventStatus;
    }

    public void setCurrentEventStatus(Integer currentEventStatus) {
        this.currentEventStatus = currentEventStatus;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getFiveMinSection() {
        return fiveMinSection;
    }

    public void setFiveMinSection(String fiveMinSection) {
        this.fiveMinSection = fiveMinSection;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
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
        sb.append(", periodId=").append(periodId);
        sb.append(", thirdEventSourceId=").append(thirdEventSourceId);
        sb.append(", eventType=").append(eventType);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", settleNum=").append(settleNum);
        sb.append(", eventOrder=").append(eventOrder);
        sb.append(", eventName=").append(eventName);
        sb.append(", status=").append(status);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", playerName=").append(playerName);
        sb.append(", playerNameCode=").append(playerNameCode);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", sportId=").append(sportId);
        sb.append(", extryInfo=").append(extryInfo);
        sb.append(", firstNum=").append(firstNum);
        sb.append(", secondNum=").append(secondNum);
        sb.append(", firstT1=").append(firstT1);
        sb.append(", firstT2=").append(firstT2);
        sb.append(", secondT1=").append(secondT1);
        sb.append(", secondT2=").append(secondT2);
        sb.append(", operateType=").append(operateType);
        sb.append(", operater=").append(operater);
        sb.append(", userid=").append(userid);
        sb.append(", settleTimes=").append(settleTimes);
        sb.append(", settleCount=").append(settleCount);
        sb.append(", settleReason=").append(settleReason);
        sb.append(", settleReasonDetail=").append(settleReasonDetail);
        sb.append(", settleFreeze=").append(settleFreeze);
        sb.append(", isSequenceSettle=").append(isSequenceSettle);
        sb.append(", checkNumber=").append(checkNumber);
        sb.append(", isAutoSettle=").append(isAutoSettle);
        sb.append(", isGrey=").append(isGrey);
        sb.append(", goWaterStatus=").append(goWaterStatus);
        sb.append(", hasDeleteEvent=").append(hasDeleteEvent);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", currentEventStatus=").append(currentEventStatus);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", fiveMinSection=").append(fiveMinSection);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}