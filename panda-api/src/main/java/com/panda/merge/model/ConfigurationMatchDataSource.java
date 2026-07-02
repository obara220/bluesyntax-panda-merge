package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigurationMatchDataSource implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "盘口类型1早盘0滚球")
    private Integer marketType;

    @ApiModelProperty(value = "SR权重")
    private Integer srWeight;

    @ApiModelProperty(value = "BC权重")
    private Integer bcWeight;

    @ApiModelProperty(value = "BG权重")
    private Integer bgWeight;

    @ApiModelProperty(value = "比分源1:SR(LiveData)2:UOF,注意：比分源还有为null的情况，需适配")
    private Integer scoreSource;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "TXodds权重")
    private Integer txWeight;

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

    public Integer getMarketType() {
        return marketType;
    }

    public void setMarketType(Integer marketType) {
        this.marketType = marketType;
    }

    public Integer getSrWeight() {
        return srWeight;
    }

    public void setSrWeight(Integer srWeight) {
        this.srWeight = srWeight;
    }

    public Integer getBcWeight() {
        return bcWeight;
    }

    public void setBcWeight(Integer bcWeight) {
        this.bcWeight = bcWeight;
    }

    public Integer getBgWeight() {
        return bgWeight;
    }

    public void setBgWeight(Integer bgWeight) {
        this.bgWeight = bgWeight;
    }

    public Integer getScoreSource() {
        return scoreSource;
    }

    public void setScoreSource(Integer scoreSource) {
        this.scoreSource = scoreSource;
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

    public Integer getTxWeight() {
        return txWeight;
    }

    public void setTxWeight(Integer txWeight) {
        this.txWeight = txWeight;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", marketType=").append(marketType);
        sb.append(", srWeight=").append(srWeight);
        sb.append(", bcWeight=").append(bcWeight);
        sb.append(", bgWeight=").append(bgWeight);
        sb.append(", scoreSource=").append(scoreSource);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", txWeight=").append(txWeight);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}