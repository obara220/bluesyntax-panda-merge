package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MarketOddsChangeHistory implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "赛事id.对应third_match_info.id")
    private Long matchId;

    @ApiModelProperty(value = "盘口id")
    private Long marketId;

    @ApiModelProperty(value = "投注项id.")
    private Long sportMarketOddsId;

    @ApiModelProperty(value = "赔率值.")
    private Long odds;

    @ApiModelProperty(value = "第三方原始赔率")
    private Long originalPrice;

    @ApiModelProperty(value = "数据来源编码.对应data_source.code")
    private String dataSourceCode;

    @ApiModelProperty(value = "赔率的状态.")
    private Integer statusId;

    @ApiModelProperty(value = "第三方赔率时间戳")
    private Long thirdOddsTimestamp;

    @ApiModelProperty(value = "创建时间.UTC时间,精确到毫秒")
    private Long createTime;

    @ApiModelProperty(value = "更新时间.UTC时间,精确到毫秒")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getSportMarketOddsId() {
        return sportMarketOddsId;
    }

    public void setSportMarketOddsId(Long sportMarketOddsId) {
        this.sportMarketOddsId = sportMarketOddsId;
    }

    public Long getOdds() {
        return odds;
    }

    public void setOdds(Long odds) {
        this.odds = odds;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Long getThirdOddsTimestamp() {
        return thirdOddsTimestamp;
    }

    public void setThirdOddsTimestamp(Long thirdOddsTimestamp) {
        this.thirdOddsTimestamp = thirdOddsTimestamp;
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
        sb.append(", matchId=").append(matchId);
        sb.append(", marketId=").append(marketId);
        sb.append(", sportMarketOddsId=").append(sportMarketOddsId);
        sb.append(", odds=").append(odds);
        sb.append(", originalPrice=").append(originalPrice);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", statusId=").append(statusId);
        sb.append(", thirdOddsTimestamp=").append(thirdOddsTimestamp);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}