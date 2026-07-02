package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MarketCategoryEventCodeRelation implements Serializable {
    private Long id;

    @ApiModelProperty(value = "赛种ID")
    private Long sportId;

    @ApiModelProperty(value = "玩法ID")
    private Long marketCategoryId;

    @ApiModelProperty(value = "事件编码ID")
    private Long eventCodeId;

    @ApiModelProperty(value = "所属汇总阶段")
    private Integer matchPeriodId;

    @ApiModelProperty(value = "是否普通阶段（非阶段结束）事件1是0否")
    private Boolean normalFlag;

    @ApiModelProperty(value = "是否动态玩法事件1是0否")
    private Boolean dynamicFlag;

    @ApiModelProperty(value = "是否阶段动态玩法事件1是0否")
    private Boolean periodDynamicFlag;

    @ApiModelProperty(value = "是否动态区间玩法事件1是0否")
    private Boolean rangeFlag;

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

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public Long getEventCodeId() {
        return eventCodeId;
    }

    public void setEventCodeId(Long eventCodeId) {
        this.eventCodeId = eventCodeId;
    }

    public Integer getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(Integer matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
    }

    public Boolean getNormalFlag() {
        return normalFlag;
    }

    public void setNormalFlag(Boolean normalFlag) {
        this.normalFlag = normalFlag;
    }

    public Boolean getDynamicFlag() {
        return dynamicFlag;
    }

    public void setDynamicFlag(Boolean dynamicFlag) {
        this.dynamicFlag = dynamicFlag;
    }

    public Boolean getPeriodDynamicFlag() {
        return periodDynamicFlag;
    }

    public void setPeriodDynamicFlag(Boolean periodDynamicFlag) {
        this.periodDynamicFlag = periodDynamicFlag;
    }

    public Boolean getRangeFlag() {
        return rangeFlag;
    }

    public void setRangeFlag(Boolean rangeFlag) {
        this.rangeFlag = rangeFlag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", eventCodeId=").append(eventCodeId);
        sb.append(", matchPeriodId=").append(matchPeriodId);
        sb.append(", normalFlag=").append(normalFlag);
        sb.append(", dynamicFlag=").append(dynamicFlag);
        sb.append(", periodDynamicFlag=").append(periodDynamicFlag);
        sb.append(", rangeFlag=").append(rangeFlag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}