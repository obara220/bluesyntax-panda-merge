package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportMarketOddsTrade implements Serializable {
    @ApiModelProperty(value = "表ID,自增")
    private Long id;

    private Long relationMarketOddsId;

    @ApiModelProperty(value = "盘口IDstandard_sport_market.id")
    private Long marketId;

    private Long relationMarketId;

    @ApiModelProperty(value = "标准投注项模板id对应standard_sport_odds_fields_templet.id")
    private Long oddsFieldsTemplateId;

    @ApiModelProperty(value = "三方投注项模板源ID")
    private String thirdTemplateSourceId;

    @ApiModelProperty(value = "当前投注项是否被激活.1激活;0未激活(锁盘)")
    private Integer active;

    @ApiModelProperty(value = "投注项结算结果文本")
    private String settlementResultText;

    @ApiModelProperty(value = "投注项结算结果文本")
    private String settlementResult;

    @ApiModelProperty(value = "赛果已确认:Confirmed,盘中事件确认:LiveScouted,未知:Unknown")
    private String betSettlementCertainty;

    @ApiModelProperty(value = "投注项类型")
    private String oddsType;

    @ApiModelProperty(value = "附加字段1")
    private String addition1;

    @ApiModelProperty(value = "附加字段2")
    private String addition2;

    @ApiModelProperty(value = "附加字段3")
    private String addition3;

    @ApiModelProperty(value = "附加字段4")
    private String addition4;

    @ApiModelProperty(value = "附加字段5")
    private String addition5;

    @ApiModelProperty(value = "名称编码.用于多语言.投注项可能有也可能没有该字段.需要的时候填入")
    private Long nameCode;

    @ApiModelProperty(value = "投注项名称.")
    private String name;

    @ApiModelProperty(value = "投注项名称中包含的表达式的值")
    private String nameExpressionValue;

    @ApiModelProperty(value = "马来赔")
    private Double malayOddsValue;

    @ApiModelProperty(value = "投注项赔率.单位:0.0001")
    private Integer oddsValue;

    @ApiModelProperty(value = "投注项PA赔率.单位:0.0001")
    private Integer paOddsValue;

    @ApiModelProperty(value = "投注项原始赔率.单位:0.0001")
    private Integer originalOddsValue;

    @ApiModelProperty(value = "投注给哪一方:T1主队,T2客队,")
    private String targetSide;

    @ApiModelProperty(value = "用于排序,大于1,越小越靠前")
    private Integer orderOdds;

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "该字段用于做风控时,需要替换成风控服务商提供的投注项id.如果数据源发生切换,当前字段需要更新.")
    private String thirdOddsFieldSourceId;

    private String extraInfo;

    private String remark;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRelationMarketOddsId() {
        return relationMarketOddsId;
    }

    public void setRelationMarketOddsId(Long relationMarketOddsId) {
        this.relationMarketOddsId = relationMarketOddsId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getRelationMarketId() {
        return relationMarketId;
    }

    public void setRelationMarketId(Long relationMarketId) {
        this.relationMarketId = relationMarketId;
    }

    public Long getOddsFieldsTemplateId() {
        return oddsFieldsTemplateId;
    }

    public void setOddsFieldsTemplateId(Long oddsFieldsTemplateId) {
        this.oddsFieldsTemplateId = oddsFieldsTemplateId;
    }

    public String getThirdTemplateSourceId() {
        return thirdTemplateSourceId;
    }

    public void setThirdTemplateSourceId(String thirdTemplateSourceId) {
        this.thirdTemplateSourceId = thirdTemplateSourceId;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getSettlementResultText() {
        return settlementResultText;
    }

    public void setSettlementResultText(String settlementResultText) {
        this.settlementResultText = settlementResultText;
    }

    public String getSettlementResult() {
        return settlementResult;
    }

    public void setSettlementResult(String settlementResult) {
        this.settlementResult = settlementResult;
    }

    public String getBetSettlementCertainty() {
        return betSettlementCertainty;
    }

    public void setBetSettlementCertainty(String betSettlementCertainty) {
        this.betSettlementCertainty = betSettlementCertainty;
    }

    public String getOddsType() {
        return oddsType;
    }

    public void setOddsType(String oddsType) {
        this.oddsType = oddsType;
    }

    public String getAddition1() {
        return addition1;
    }

    public void setAddition1(String addition1) {
        this.addition1 = addition1;
    }

    public String getAddition2() {
        return addition2;
    }

    public void setAddition2(String addition2) {
        this.addition2 = addition2;
    }

    public String getAddition3() {
        return addition3;
    }

    public void setAddition3(String addition3) {
        this.addition3 = addition3;
    }

    public String getAddition4() {
        return addition4;
    }

    public void setAddition4(String addition4) {
        this.addition4 = addition4;
    }

    public String getAddition5() {
        return addition5;
    }

    public void setAddition5(String addition5) {
        this.addition5 = addition5;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameExpressionValue() {
        return nameExpressionValue;
    }

    public void setNameExpressionValue(String nameExpressionValue) {
        this.nameExpressionValue = nameExpressionValue;
    }

    public Double getMalayOddsValue() {
        return malayOddsValue;
    }

    public void setMalayOddsValue(Double malayOddsValue) {
        this.malayOddsValue = malayOddsValue;
    }

    public Integer getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(Integer oddsValue) {
        this.oddsValue = oddsValue;
    }

    public Integer getPaOddsValue() {
        return paOddsValue;
    }

    public void setPaOddsValue(Integer paOddsValue) {
        this.paOddsValue = paOddsValue;
    }

    public Integer getOriginalOddsValue() {
        return originalOddsValue;
    }

    public void setOriginalOddsValue(Integer originalOddsValue) {
        this.originalOddsValue = originalOddsValue;
    }

    public String getTargetSide() {
        return targetSide;
    }

    public void setTargetSide(String targetSide) {
        this.targetSide = targetSide;
    }

    public Integer getOrderOdds() {
        return orderOdds;
    }

    public void setOrderOdds(Integer orderOdds) {
        this.orderOdds = orderOdds;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getThirdOddsFieldSourceId() {
        return thirdOddsFieldSourceId;
    }

    public void setThirdOddsFieldSourceId(String thirdOddsFieldSourceId) {
        this.thirdOddsFieldSourceId = thirdOddsFieldSourceId;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        sb.append(", relationMarketOddsId=").append(relationMarketOddsId);
        sb.append(", marketId=").append(marketId);
        sb.append(", relationMarketId=").append(relationMarketId);
        sb.append(", oddsFieldsTemplateId=").append(oddsFieldsTemplateId);
        sb.append(", thirdTemplateSourceId=").append(thirdTemplateSourceId);
        sb.append(", active=").append(active);
        sb.append(", settlementResultText=").append(settlementResultText);
        sb.append(", settlementResult=").append(settlementResult);
        sb.append(", betSettlementCertainty=").append(betSettlementCertainty);
        sb.append(", oddsType=").append(oddsType);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", name=").append(name);
        sb.append(", nameExpressionValue=").append(nameExpressionValue);
        sb.append(", malayOddsValue=").append(malayOddsValue);
        sb.append(", oddsValue=").append(oddsValue);
        sb.append(", paOddsValue=").append(paOddsValue);
        sb.append(", originalOddsValue=").append(originalOddsValue);
        sb.append(", targetSide=").append(targetSide);
        sb.append(", orderOdds=").append(orderOdds);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdOddsFieldSourceId=").append(thirdOddsFieldSourceId);
        sb.append(", extraInfo=").append(extraInfo);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}