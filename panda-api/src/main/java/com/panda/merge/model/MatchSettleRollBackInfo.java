package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleRollBackInfo implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "比分，事件id")
    private Long settleScoreEventId;

    @ApiModelProperty(value = "数据类型，1比分，2事件")
    private Integer dataType;

    @ApiModelProperty(value = "回滚状态")
    private Integer rollBackStatus;

    @ApiModelProperty(value = "回滚时间")
    private Long rollBackTime;

    @ApiModelProperty(value = "赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "回滚订单数")
    private Long rollBackOrderCount;

    @ApiModelProperty(value = "订单总数")
    private Long orderCount;

    @ApiModelProperty(value = "回调时间")
    private Long rollBackSuccessTime;

    private Long modifyTime;

    private Long createTime;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "1是点球大战")
    private Integer isDianQiu;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSettleScoreEventId() {
        return settleScoreEventId;
    }

    public void setSettleScoreEventId(Long settleScoreEventId) {
        this.settleScoreEventId = settleScoreEventId;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getRollBackStatus() {
        return rollBackStatus;
    }

    public void setRollBackStatus(Integer rollBackStatus) {
        this.rollBackStatus = rollBackStatus;
    }

    public Long getRollBackTime() {
        return rollBackTime;
    }

    public void setRollBackTime(Long rollBackTime) {
        this.rollBackTime = rollBackTime;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getRollBackOrderCount() {
        return rollBackOrderCount;
    }

    public void setRollBackOrderCount(Long rollBackOrderCount) {
        this.rollBackOrderCount = rollBackOrderCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getRollBackSuccessTime() {
        return rollBackSuccessTime;
    }

    public void setRollBackSuccessTime(Long rollBackSuccessTime) {
        this.rollBackSuccessTime = rollBackSuccessTime;
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

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Integer getIsDianQiu() {
        return isDianQiu;
    }

    public void setIsDianQiu(Integer isDianQiu) {
        this.isDianQiu = isDianQiu;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", settleScoreEventId=").append(settleScoreEventId);
        sb.append(", dataType=").append(dataType);
        sb.append(", rollBackStatus=").append(rollBackStatus);
        sb.append(", rollBackTime=").append(rollBackTime);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", rollBackOrderCount=").append(rollBackOrderCount);
        sb.append(", orderCount=").append(orderCount);
        sb.append(", rollBackSuccessTime=").append(rollBackSuccessTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", isDianQiu=").append(isDianQiu);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}