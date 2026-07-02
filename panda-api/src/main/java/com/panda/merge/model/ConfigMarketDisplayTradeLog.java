package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigMarketDisplayTradeLog implements Serializable {
    private Long id;

    private Long standardMatchId;

    @ApiModelProperty(value = "滚球盘口显示数量")
    private Integer liveMarketCount;

    @ApiModelProperty(value = "早盘盘口显示数量")
    private Integer displayMarketCount;

    @ApiModelProperty(value = "Y:展示，N：不展示")
    private String displayCorner;

    @ApiModelProperty(value = "Y:展示，N：不展示")
    private String displayPenaltyCard;

    private Long createTime;

    private Long modifyTime;

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

    public Integer getLiveMarketCount() {
        return liveMarketCount;
    }

    public void setLiveMarketCount(Integer liveMarketCount) {
        this.liveMarketCount = liveMarketCount;
    }

    public Integer getDisplayMarketCount() {
        return displayMarketCount;
    }

    public void setDisplayMarketCount(Integer displayMarketCount) {
        this.displayMarketCount = displayMarketCount;
    }

    public String getDisplayCorner() {
        return displayCorner;
    }

    public void setDisplayCorner(String displayCorner) {
        this.displayCorner = displayCorner;
    }

    public String getDisplayPenaltyCard() {
        return displayPenaltyCard;
    }

    public void setDisplayPenaltyCard(String displayPenaltyCard) {
        this.displayPenaltyCard = displayPenaltyCard;
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
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", liveMarketCount=").append(liveMarketCount);
        sb.append(", displayMarketCount=").append(displayMarketCount);
        sb.append(", displayCorner=").append(displayCorner);
        sb.append(", displayPenaltyCard=").append(displayPenaltyCard);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}