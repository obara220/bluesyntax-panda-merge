package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleAbnormal implements Serializable {
    private Long id;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;

    private String extryInfo;

    private String eventName;

    @ApiModelProperty(value = "操作人")
    private String operater;

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "结算次数")
    private Integer settleTimes;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;

    @ApiModelProperty(value = "结算比分编号")
    private String settleNum;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "比分阶段")
    private Long periodId;

    @ApiModelProperty(value = "是比分还是事件1:比分2:事件")
    private Integer isScores;

    @ApiModelProperty(value = "状态0.未编辑1.未确认2.已确认3.已结算")
    private Integer status;

    private Long modifyTime;

    private Long createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getExtryInfo() {
        return extryInfo;
    }

    public void setExtryInfo(String extryInfo) {
        this.extryInfo = extryInfo;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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

    public String getSettleNum() {
        return settleNum;
    }

    public void setSettleNum(String settleNum) {
        this.settleNum = settleNum;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
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

    public Integer getIsScores() {
        return isScores;
    }

    public void setIsScores(Integer isScores) {
        this.isScores = isScores;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", extryInfo=").append(extryInfo);
        sb.append(", eventName=").append(eventName);
        sb.append(", operater=").append(operater);
        sb.append(", userid=").append(userid);
        sb.append(", settleTimes=").append(settleTimes);
        sb.append(", settleCount=").append(settleCount);
        sb.append(", settleNum=").append(settleNum);
        sb.append(", sportId=").append(sportId);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", periodId=").append(periodId);
        sb.append(", isScores=").append(isScores);
        sb.append(", status=").append(status);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}