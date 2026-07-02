package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleInfo implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    private Long sportId;

    @ApiModelProperty(value = "主队全场比分")
    private Integer ftT1;

    @ApiModelProperty(value = "客队全场比分")
    private Integer ftT2;

    @ApiModelProperty(value = "主队上半场")
    private Integer h1T1;

    @ApiModelProperty(value = "客队上半场")
    private Integer h1T2;

    @ApiModelProperty(value = "赛事id")
    private Long standardMatchId;

    private String scoresJson;

    private String scoresJsonExtra;

    @ApiModelProperty(value = "0未冻结1冻结")
    private Integer freezeStatus;

    @ApiModelProperty(value = "1.结算1.02结算2.0")
    private Integer settleType;

    @ApiModelProperty(value = "全部操盘手")
    private String allLiveTrader;

    @ApiModelProperty(value = "操盘手")
    private String liveTrader;

    @ApiModelProperty(value = "操盘手id")
    private String liveTraderId;

    @ApiModelProperty(value = "被限制操作用户名称array")
    private String limitUserArray;

    @ApiModelProperty(value = "全部审核员")
    private String auditorJson;

    @ApiModelProperty(value = "是否开启数据商自动结算:1是0否")
    private Integer isAutoSettleDataSource;

    @ApiModelProperty(value = "进球数据商自动结算:1是0否")
    private Integer goalAutoSettleDataSource;

    @ApiModelProperty(value = "角球数据商自动结算:1是0否")
    private Integer cornerAutoSettleDataSource;

    @ApiModelProperty(value = "当前事件状态：0无1灰色区间2删除事件")
    private Integer currentEventStatus;

    @ApiModelProperty(value = "0无1灰色区间")
    private Integer isGray;

    @ApiModelProperty(value = "有删除事件:1是0否")
    private Integer hasDeleteEvent;

    @ApiModelProperty(value = "有数据源与结算比分不一致提示:1是0否")
    private Integer currentEventTag;

    @ApiModelProperty(value = "罚牌数据商自动结算:1是0否")
    private Integer bookingAutoSettleDataSource;

    @ApiModelProperty(value = "是否有备忘录1:有0:无")
    private Integer ismemo;

    @ApiModelProperty(value = "结算顺序开关:0:开,1:关")
    private Integer settleOrderClosed;

    @ApiModelProperty(value = "是否开启五分钟玩法0:否1:是")
    private Integer fiveMinSwitch;

    @ApiModelProperty(value = "可操作的审核员")
    private String auditorActiveArray;

    @ApiModelProperty(value = "操作时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "玩法类型")
    private String categoryFreezeStatus;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Integer getFtT1() {
        return ftT1;
    }

    public void setFtT1(Integer ftT1) {
        this.ftT1 = ftT1;
    }

    public Integer getFtT2() {
        return ftT2;
    }

    public void setFtT2(Integer ftT2) {
        this.ftT2 = ftT2;
    }

    public Integer getH1T1() {
        return h1T1;
    }

    public void setH1T1(Integer h1T1) {
        this.h1T1 = h1T1;
    }

    public Integer getH1T2() {
        return h1T2;
    }

    public void setH1T2(Integer h1T2) {
        this.h1T2 = h1T2;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getScoresJson() {
        return scoresJson;
    }

    public void setScoresJson(String scoresJson) {
        this.scoresJson = scoresJson;
    }

    public String getScoresJsonExtra() {
        return scoresJsonExtra;
    }

    public void setScoresJsonExtra(String scoresJsonExtra) {
        this.scoresJsonExtra = scoresJsonExtra;
    }

    public Integer getFreezeStatus() {
        return freezeStatus;
    }

    public void setFreezeStatus(Integer freezeStatus) {
        this.freezeStatus = freezeStatus;
    }

    public Integer getSettleType() {
        return settleType;
    }

    public void setSettleType(Integer settleType) {
        this.settleType = settleType;
    }

    public String getAllLiveTrader() {
        return allLiveTrader;
    }

    public void setAllLiveTrader(String allLiveTrader) {
        this.allLiveTrader = allLiveTrader;
    }

    public String getLiveTrader() {
        return liveTrader;
    }

    public void setLiveTrader(String liveTrader) {
        this.liveTrader = liveTrader;
    }

    public String getLiveTraderId() {
        return liveTraderId;
    }

    public void setLiveTraderId(String liveTraderId) {
        this.liveTraderId = liveTraderId;
    }

    public String getLimitUserArray() {
        return limitUserArray;
    }

    public void setLimitUserArray(String limitUserArray) {
        this.limitUserArray = limitUserArray;
    }

    public String getAuditorJson() {
        return auditorJson;
    }

    public void setAuditorJson(String auditorJson) {
        this.auditorJson = auditorJson;
    }

    public Integer getIsAutoSettleDataSource() {
        return isAutoSettleDataSource;
    }

    public void setIsAutoSettleDataSource(Integer isAutoSettleDataSource) {
        this.isAutoSettleDataSource = isAutoSettleDataSource;
    }

    public Integer getGoalAutoSettleDataSource() {
        return goalAutoSettleDataSource;
    }

    public void setGoalAutoSettleDataSource(Integer goalAutoSettleDataSource) {
        this.goalAutoSettleDataSource = goalAutoSettleDataSource;
    }

    public Integer getCornerAutoSettleDataSource() {
        return cornerAutoSettleDataSource;
    }

    public void setCornerAutoSettleDataSource(Integer cornerAutoSettleDataSource) {
        this.cornerAutoSettleDataSource = cornerAutoSettleDataSource;
    }

    public Integer getCurrentEventStatus() {
        return currentEventStatus;
    }

    public void setCurrentEventStatus(Integer currentEventStatus) {
        this.currentEventStatus = currentEventStatus;
    }

    public Integer getIsGray() {
        return isGray;
    }

    public void setIsGray(Integer isGray) {
        this.isGray = isGray;
    }

    public Integer getHasDeleteEvent() {
        return hasDeleteEvent;
    }

    public void setHasDeleteEvent(Integer hasDeleteEvent) {
        this.hasDeleteEvent = hasDeleteEvent;
    }

    public Integer getCurrentEventTag() {
        return currentEventTag;
    }

    public void setCurrentEventTag(Integer currentEventTag) {
        this.currentEventTag = currentEventTag;
    }

    public Integer getBookingAutoSettleDataSource() {
        return bookingAutoSettleDataSource;
    }

    public void setBookingAutoSettleDataSource(Integer bookingAutoSettleDataSource) {
        this.bookingAutoSettleDataSource = bookingAutoSettleDataSource;
    }

    public Integer getIsmemo() {
        return ismemo;
    }

    public void setIsmemo(Integer ismemo) {
        this.ismemo = ismemo;
    }

    public Integer getSettleOrderClosed() {
        return settleOrderClosed;
    }

    public void setSettleOrderClosed(Integer settleOrderClosed) {
        this.settleOrderClosed = settleOrderClosed;
    }

    public Integer getFiveMinSwitch() {
        return fiveMinSwitch;
    }

    public void setFiveMinSwitch(Integer fiveMinSwitch) {
        this.fiveMinSwitch = fiveMinSwitch;
    }

    public String getAuditorActiveArray() {
        return auditorActiveArray;
    }

    public void setAuditorActiveArray(String auditorActiveArray) {
        this.auditorActiveArray = auditorActiveArray;
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

    public String getCategoryFreezeStatus() {
        return categoryFreezeStatus;
    }

    public void setCategoryFreezeStatus(String categoryFreezeStatus) {
        this.categoryFreezeStatus = categoryFreezeStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", ftT1=").append(ftT1);
        sb.append(", ftT2=").append(ftT2);
        sb.append(", h1T1=").append(h1T1);
        sb.append(", h1T2=").append(h1T2);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", scoresJson=").append(scoresJson);
        sb.append(", scoresJsonExtra=").append(scoresJsonExtra);
        sb.append(", freezeStatus=").append(freezeStatus);
        sb.append(", settleType=").append(settleType);
        sb.append(", allLiveTrader=").append(allLiveTrader);
        sb.append(", liveTrader=").append(liveTrader);
        sb.append(", liveTraderId=").append(liveTraderId);
        sb.append(", limitUserArray=").append(limitUserArray);
        sb.append(", auditorJson=").append(auditorJson);
        sb.append(", isAutoSettleDataSource=").append(isAutoSettleDataSource);
        sb.append(", goalAutoSettleDataSource=").append(goalAutoSettleDataSource);
        sb.append(", cornerAutoSettleDataSource=").append(cornerAutoSettleDataSource);
        sb.append(", currentEventStatus=").append(currentEventStatus);
        sb.append(", isGray=").append(isGray);
        sb.append(", hasDeleteEvent=").append(hasDeleteEvent);
        sb.append(", bookingAutoSettleDataSource=").append(bookingAutoSettleDataSource);
        sb.append(", ismemo=").append(ismemo);
        sb.append(", settleOrderClosed=").append(settleOrderClosed);
        sb.append(", fiveMinSwitch=").append(fiveMinSwitch);
        sb.append(", auditorActiveArray=").append(auditorActiveArray);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", categoryFreezeStatus=").append(categoryFreezeStatus);
        sb.append(", currentEventTag=").append(currentEventTag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}