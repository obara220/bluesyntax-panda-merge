package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdBetOperationLog implements Serializable {
    private Long id;

    @ApiModelProperty(value = "消息所属产品类型1=LiveOdds,2=MTS,3=BetradarCtrl,4=Betpal,5=premiumcricket")
    private String product;

    @ApiModelProperty(value = "枚举值：")
    private String operationType;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "数据源事件产生时间")
    private Long sourceTimeStamp;

    @ApiModelProperty(value = "数据接入模块发送消息时间")
    private Long sendTimeStamp;

    @ApiModelProperty(value = "融合下发时间")
    private Long sendTimeStampPandaData;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "三方赛事源ID")
    private String thirdMatchSourceId;

    private Long createTime;

    private String marketData;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getSourceTimeStamp() {
        return sourceTimeStamp;
    }

    public void setSourceTimeStamp(Long sourceTimeStamp) {
        this.sourceTimeStamp = sourceTimeStamp;
    }

    public Long getSendTimeStamp() {
        return sendTimeStamp;
    }

    public void setSendTimeStamp(Long sendTimeStamp) {
        this.sendTimeStamp = sendTimeStamp;
    }

    public Long getSendTimeStampPandaData() {
        return sendTimeStampPandaData;
    }

    public void setSendTimeStampPandaData(Long sendTimeStampPandaData) {
        this.sendTimeStampPandaData = sendTimeStampPandaData;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getMarketData() {
        return marketData;
    }

    public void setMarketData(String marketData) {
        this.marketData = marketData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", product=").append(product);
        sb.append(", operationType=").append(operationType);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", sourceTimeStamp=").append(sourceTimeStamp);
        sb.append(", sendTimeStamp=").append(sendTimeStamp);
        sb.append(", sendTimeStampPandaData=").append(sendTimeStampPandaData);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", createTime=").append(createTime);
        sb.append(", marketData=").append(marketData);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}