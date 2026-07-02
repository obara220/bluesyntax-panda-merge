package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class MarketCategorySell implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "标准赛事id")
    private Long matchId;

    @ApiModelProperty(value = "盘口类型")
    private String marketType;

    @ApiModelProperty(value = "玩法id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "赛事数据源编号")
    private String dataSourceCode;

    @ApiModelProperty(value = "sr权重")
    private Integer srWeight;

    @ApiModelProperty(value = "bc权重")
    private Integer bcWeight;

    @ApiModelProperty(value = "bg权重")
    private Integer bgWeight;

    @ApiModelProperty(value = "开售状态未售Unsold,开售Sold'")
    private String sellStatus;

    @ApiModelProperty(value = "开售时间")
    private Long sellTime;

    @ApiModelProperty(value = "是否下发赔率")
    private Integer isSend;

    @ApiModelProperty(value = "联赛等级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "最大盘口数")
    private Integer marketCount;

    @ApiModelProperty(value = "支持串关，1:是0:否")
    private Integer isSeries;

    @ApiModelProperty(value = "足球自动关盘时间设置：6、上半场期间41、加时赛上半场7、下半场期间42、加时赛下半场篮球自动关盘时间设置：13、第1节14、第2节15、第3节16、第4节40、加时")
    private Integer autoCloseMarket;

    @ApiModelProperty(value = "比赛进程时间")
    private Integer matchProgressTime;

    @ApiModelProperty(value = "补时时间")
    private Integer injuryTime;

    @ApiModelProperty(value = "相邻盘口差值")
    private BigDecimal marketNearDiff;

    @ApiModelProperty(value = "相邻盘口赔率差值")
    private BigDecimal marketNearOddsDiff;

    @ApiModelProperty(value = "是否开售1：是0：否")
    private Integer isSell;

    private String linkId;

    @ApiModelProperty(value = "是否特殊抽水1:是0:否")
    private Integer isSpecialPumping;

    @ApiModelProperty(value = "特殊抽水赔率区间")
    private String specialOddsInterval;

    @ApiModelProperty(value = "最小球头")
    private BigDecimal minBallHead;

    @ApiModelProperty(value = "最大球头")
    private BigDecimal maxBallHead;

    @ApiModelProperty(value = "PI数据源权重")
    private Integer piWeight;

    @ApiModelProperty(value = "ao数据源权重")
    private Integer aoWeight;

    @ApiModelProperty(value = "LS数据权重")
    private Integer lsWeight;

    @ApiModelProperty(value = "BT权重")
    private Integer btWeight;

    @ApiModelProperty(value = "be权重")
    private Integer beWeight;

    @ApiModelProperty(value = "ko权重")
    private Integer koWeight;

    @ApiModelProperty(value = "RB数据源权重")
    private Integer rbWeight;

    @ApiModelProperty(value = "RB数据源权重")
    private Integer txWeight;

    @ApiModelProperty(value = "od数据源权重")
    private Integer odWeight;

    @ApiModelProperty(value = "N01权重")
    private Integer n01Weight;

    @ApiModelProperty(value = "N02权重")
    private Integer n02Weight;

    @ApiModelProperty(value = "F01权重")
    private Integer f01Weight;

    @ApiModelProperty(value = "N03权重")
    private Integer n03Weight;

    @ApiModelProperty(value = "L02权重")
    private Integer l02Weight;

    @ApiModelProperty(value = "自动开盘阶段")
    private Integer autoOpenMarket;

    @ApiModelProperty(value = "自动开盘时间")
    private Integer autoOpenTime;

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

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
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

    public String getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(String sellStatus) {
        this.sellStatus = sellStatus;
    }

    public Long getSellTime() {
        return sellTime;
    }

    public void setSellTime(Long sellTime) {
        this.sellTime = sellTime;
    }

    public Integer getIsSend() {
        return isSend;
    }

    public void setIsSend(Integer isSend) {
        this.isSend = isSend;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
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

    public Integer getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(Integer marketCount) {
        this.marketCount = marketCount;
    }

    public Integer getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(Integer isSeries) {
        this.isSeries = isSeries;
    }

    public Integer getAutoCloseMarket() {
        return autoCloseMarket;
    }

    public void setAutoCloseMarket(Integer autoCloseMarket) {
        this.autoCloseMarket = autoCloseMarket;
    }

    public Integer getMatchProgressTime() {
        return matchProgressTime;
    }

    public void setMatchProgressTime(Integer matchProgressTime) {
        this.matchProgressTime = matchProgressTime;
    }

    public Integer getInjuryTime() {
        return injuryTime;
    }

    public void setInjuryTime(Integer injuryTime) {
        this.injuryTime = injuryTime;
    }

    public BigDecimal getMarketNearDiff() {
        return marketNearDiff;
    }

    public void setMarketNearDiff(BigDecimal marketNearDiff) {
        this.marketNearDiff = marketNearDiff;
    }

    public BigDecimal getMarketNearOddsDiff() {
        return marketNearOddsDiff;
    }

    public void setMarketNearOddsDiff(BigDecimal marketNearOddsDiff) {
        this.marketNearOddsDiff = marketNearOddsDiff;
    }

    public Integer getIsSell() {
        return isSell;
    }

    public void setIsSell(Integer isSell) {
        this.isSell = isSell;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Integer getIsSpecialPumping() {
        return isSpecialPumping;
    }

    public void setIsSpecialPumping(Integer isSpecialPumping) {
        this.isSpecialPumping = isSpecialPumping;
    }

    public String getSpecialOddsInterval() {
        return specialOddsInterval;
    }

    public void setSpecialOddsInterval(String specialOddsInterval) {
        this.specialOddsInterval = specialOddsInterval;
    }

    public BigDecimal getMinBallHead() {
        return minBallHead;
    }

    public void setMinBallHead(BigDecimal minBallHead) {
        this.minBallHead = minBallHead;
    }

    public BigDecimal getMaxBallHead() {
        return maxBallHead;
    }

    public void setMaxBallHead(BigDecimal maxBallHead) {
        this.maxBallHead = maxBallHead;
    }

    public Integer getPiWeight() {
        return piWeight;
    }

    public void setPiWeight(Integer piWeight) {
        this.piWeight = piWeight;
    }

    public Integer getAoWeight() {
        return aoWeight;
    }

    public void setAoWeight(Integer aoWeight) {
        this.aoWeight = aoWeight;
    }

    public Integer getLsWeight() {
        return lsWeight;
    }

    public void setLsWeight(Integer lsWeight) {
        this.lsWeight = lsWeight;
    }

    public Integer getBtWeight() {
        return btWeight;
    }

    public void setBtWeight(Integer btWeight) {
        this.btWeight = btWeight;
    }

    public Integer getBeWeight() {
        return beWeight;
    }

    public void setBeWeight(Integer beWeight) {
        this.beWeight = beWeight;
    }

    public Integer getKoWeight() {
        return koWeight;
    }

    public void setKoWeight(Integer koWeight) {
        this.koWeight = koWeight;
    }

    public Integer getRbWeight() {
        return rbWeight;
    }

    public void setRbWeight(Integer rbWeight) {
        this.rbWeight = rbWeight;
    }

    public Integer getTxWeight() {
        return txWeight;
    }

    public void setTxWeight(Integer txWeight) {
        this.txWeight = txWeight;
    }

    public Integer getOdWeight() {
        return odWeight;
    }

    public void setOdWeight(Integer odWeight) {
        this.odWeight = odWeight;
    }

    public Integer getN01Weight() {
        return n01Weight;
    }

    public void setN01Weight(Integer n01Weight) {
        this.n01Weight = n01Weight;
    }

    public Integer getN02Weight() {
        return n02Weight;
    }

    public void setN02Weight(Integer n02Weight) {
        this.n02Weight = n02Weight;
    }

    public Integer getF01Weight() {
        return f01Weight;
    }

    public void setF01Weight(Integer f01Weight) {
        this.f01Weight = f01Weight;
    }

    public Integer getN03Weight() {
        return n03Weight;
    }

    public void setN03Weight(Integer n03Weight) {
        this.n03Weight = n03Weight;
    }

    public Integer getL02Weight() {
        return l02Weight;
    }

    public void setL02Weight(Integer l02Weight) {
        this.l02Weight = l02Weight;
    }

    public Integer getAutoOpenMarket() {
        return autoOpenMarket;
    }

    public void setAutoOpenMarket(Integer autoOpenMarket) {
        this.autoOpenMarket = autoOpenMarket;
    }

    public Integer getAutoOpenTime() {
        return autoOpenTime;
    }

    public void setAutoOpenTime(Integer autoOpenTime) {
        this.autoOpenTime = autoOpenTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", matchId=").append(matchId);
        sb.append(", marketType=").append(marketType);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", srWeight=").append(srWeight);
        sb.append(", bcWeight=").append(bcWeight);
        sb.append(", bgWeight=").append(bgWeight);
        sb.append(", sellStatus=").append(sellStatus);
        sb.append(", sellTime=").append(sellTime);
        sb.append(", isSend=").append(isSend);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", marketCount=").append(marketCount);
        sb.append(", isSeries=").append(isSeries);
        sb.append(", autoCloseMarket=").append(autoCloseMarket);
        sb.append(", matchProgressTime=").append(matchProgressTime);
        sb.append(", injuryTime=").append(injuryTime);
        sb.append(", marketNearDiff=").append(marketNearDiff);
        sb.append(", marketNearOddsDiff=").append(marketNearOddsDiff);
        sb.append(", isSell=").append(isSell);
        sb.append(", linkId=").append(linkId);
        sb.append(", isSpecialPumping=").append(isSpecialPumping);
        sb.append(", specialOddsInterval=").append(specialOddsInterval);
        sb.append(", minBallHead=").append(minBallHead);
        sb.append(", maxBallHead=").append(maxBallHead);
        sb.append(", piWeight=").append(piWeight);
        sb.append(", aoWeight=").append(aoWeight);
        sb.append(", lsWeight=").append(lsWeight);
        sb.append(", btWeight=").append(btWeight);
        sb.append(", beWeight=").append(beWeight);
        sb.append(", koWeight=").append(koWeight);
        sb.append(", rbWeight=").append(rbWeight);
        sb.append(", txWeight=").append(txWeight);
        sb.append(", odWeight=").append(odWeight);
        sb.append(", n01Weight=").append(n01Weight);
        sb.append(", n02Weight=").append(n02Weight);
        sb.append(", f01Weight=").append(f01Weight);
        sb.append(", n03Weight=").append(n03Weight);
        sb.append(", l02Weight=").append(l02Weight);
        sb.append(", autoOpenMarket=").append(autoOpenMarket);
        sb.append(", autoOpenTime=").append(autoOpenTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}