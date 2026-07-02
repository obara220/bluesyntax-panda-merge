package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigOutrightTradeProbability implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准比赛IDstandard_match_info.id")
    private Long standardMatchId;

    @ApiModelProperty(value = "统一盘口id")
    private Long standardMarketId;

    @ApiModelProperty(value = "标准投注项ID")
    private Long standardMarketOddsId;

    @ApiModelProperty(value = "概率")
    private Double probability;

    @ApiModelProperty(value = "日志id")
    private String linkId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

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

    public Long getStandardMarketId() {
        return standardMarketId;
    }

    public void setStandardMarketId(Long standardMarketId) {
        this.standardMarketId = standardMarketId;
    }

    public Long getStandardMarketOddsId() {
        return standardMarketOddsId;
    }

    public void setStandardMarketOddsId(Long standardMarketOddsId) {
        this.standardMarketOddsId = standardMarketOddsId;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
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

    public Long getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(Long operaterId) {
        this.operaterId = operaterId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", standardMarketId=").append(standardMarketId);
        sb.append(", standardMarketOddsId=").append(standardMarketOddsId);
        sb.append(", probability=").append(probability);
        sb.append(", linkId=").append(linkId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}