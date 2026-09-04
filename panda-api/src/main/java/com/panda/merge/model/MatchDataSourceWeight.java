package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchDataSourceWeight implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "标准赛事Id")
    private Long standardMatchId;

    @ApiModelProperty(value = "联赛等级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "盘口类型1早盘0滚球")
    private String marketType;

    @ApiModelProperty(value = "SR权重")
    private Integer srWeight;

    @ApiModelProperty(value = "BC权重")
    private Integer bcWeight;

    @ApiModelProperty(value = "BG权重")
    private Integer bgWeight;

    @ApiModelProperty(value = "PD权重")
    private Integer pdWeight;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "操作人ID")
    private Long operaterId;

    @ApiModelProperty(value = "tx权重")
    private Integer txWeight;

    @ApiModelProperty(value = "RB数据源权重")
    private Integer rbWeight;

    @ApiModelProperty(value = "AO数据源权重")
    private Integer aoWeight;

    @ApiModelProperty(value = "PI数据源权重")
    private Integer piWeight;

    @ApiModelProperty(value = "LS数据源权重")
    private Integer lsWeight;

    @ApiModelProperty(value = "BT数据源权重")
    private Integer btWeight;

    @ApiModelProperty(value = "be权重")
    private Integer beWeight;

    @ApiModelProperty(value = "ko权重")
    private Integer koWeight;

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

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
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

    public Integer getPdWeight() {
        return pdWeight;
    }

    public void setPdWeight(Integer pdWeight) {
        this.pdWeight = pdWeight;
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

    public Integer getTxWeight() {
        return txWeight;
    }

    public void setTxWeight(Integer txWeight) {
        this.txWeight = txWeight;
    }

    public Integer getRbWeight() {
        return rbWeight;
    }

    public void setRbWeight(Integer rbWeight) {
        this.rbWeight = rbWeight;
    }

    public Integer getAoWeight() {
        return aoWeight;
    }

    public void setAoWeight(Integer aoWeight) {
        this.aoWeight = aoWeight;
    }

    public Integer getPiWeight() {
        return piWeight;
    }

    public void setPiWeight(Integer piWeight) {
        this.piWeight = piWeight;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", marketType=").append(marketType);
        sb.append(", srWeight=").append(srWeight);
        sb.append(", bcWeight=").append(bcWeight);
        sb.append(", bgWeight=").append(bgWeight);
        sb.append(", pdWeight=").append(pdWeight);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", operaterId=").append(operaterId);
        sb.append(", txWeight=").append(txWeight);
        sb.append(", rbWeight=").append(rbWeight);
        sb.append(", aoWeight=").append(aoWeight);
        sb.append(", piWeight=").append(piWeight);
        sb.append(", lsWeight=").append(lsWeight);
        sb.append(", btWeight=").append(btWeight);
        sb.append(", beWeight=").append(beWeight);
        sb.append(", koWeight=").append(koWeight);
        sb.append(", odWeight=").append(odWeight);
        sb.append(", n01Weight=").append(n01Weight);
        sb.append(", n02Weight=").append(n02Weight);
        sb.append(", f01Weight=").append(f01Weight);
        sb.append(", n03Weight=").append(n03Weight);
        sb.append(", l02Weight=").append(l02Weight);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}