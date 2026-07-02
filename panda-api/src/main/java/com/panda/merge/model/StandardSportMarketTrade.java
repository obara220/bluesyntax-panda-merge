package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportMarketTrade implements Serializable {
    @ApiModelProperty(value = "数据库id,自增")
    private Long id;

    private Long relationMarketId;

    @ApiModelProperty(value = "所属联赛IDstandard_sport_tournament.id")
    private Long standardTournamentId;

    @ApiModelProperty(value = "标准比赛IDstandard_match_info.id")
    private Long standardMatchInfoId;

    @ApiModelProperty(value = "标准玩法idstandard_sport_market_category.id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "盘口类型.属于赛前盘或者滚球盘.1:赛前盘;0:滚球盘.")
    private Integer marketType;

    @ApiModelProperty(value = "操盘方式：0自动操盘，1手动操盘")
    private Integer tradeType;

    @ApiModelProperty(value = "盘口名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "该盘口具体显示的值.例如:大小球中,大小界限是:3.5")
    private String oddsValue;

    @ApiModelProperty(value = "盘口名称,V1.2统一命名规则.")
    private String oddsName;

    @ApiModelProperty(value = "排序类型")
    private String orderType;

    @ApiModelProperty(value = "盘口级别，数字越小优先级越高")
    private Long oddsMetric;

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

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "盘口状态0-5.0:active,1:suspended,2:deactivated,3:settled,4:cancelled,5:handedOver")
    private Integer status;

    @ApiModelProperty(value = "盘口阶段id.对应对应system_item_dict.value")
    private String scopeId;

    @ApiModelProperty(value = "接收到第三方数据后,可以通过该字段快速定位到当前的盘口.通过玩法和具体内容确认盘口的唯一性.SR提供的盘口数据id生成算法:Type_Typeid_Subtypeid_Specialoddsvalue")
    private String thirdOddsType;

    @ApiModelProperty(value = "该字段用于做风控时，需要替换成风控服务商提供的盘口id。如果数据源发生切换，当前字段需要更新。")
    private String thirdMarketSourceId;

    @ApiModelProperty(value = "是否下发数据：Y是N否")
    private String sendData;

    @ApiModelProperty(value = "最近一次下发数据的Linkid")
    private String linkId;

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

    public Long getRelationMarketId() {
        return relationMarketId;
    }

    public void setRelationMarketId(Long relationMarketId) {
        this.relationMarketId = relationMarketId;
    }

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
    }

    public Long getStandardMatchInfoId() {
        return standardMatchInfoId;
    }

    public void setStandardMatchInfoId(Long standardMatchInfoId) {
        this.standardMatchInfoId = standardMatchInfoId;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public Integer getMarketType() {
        return marketType;
    }

    public void setMarketType(Integer marketType) {
        this.marketType = marketType;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public String getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(String oddsValue) {
        this.oddsValue = oddsValue;
    }

    public String getOddsName() {
        return oddsName;
    }

    public void setOddsName(String oddsName) {
        this.oddsName = oddsName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getOddsMetric() {
        return oddsMetric;
    }

    public void setOddsMetric(Long oddsMetric) {
        this.oddsMetric = oddsMetric;
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

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getThirdOddsType() {
        return thirdOddsType;
    }

    public void setThirdOddsType(String thirdOddsType) {
        this.thirdOddsType = thirdOddsType;
    }

    public String getThirdMarketSourceId() {
        return thirdMarketSourceId;
    }

    public void setThirdMarketSourceId(String thirdMarketSourceId) {
        this.thirdMarketSourceId = thirdMarketSourceId;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
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
        sb.append(", relationMarketId=").append(relationMarketId);
        sb.append(", standardTournamentId=").append(standardTournamentId);
        sb.append(", standardMatchInfoId=").append(standardMatchInfoId);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", marketType=").append(marketType);
        sb.append(", tradeType=").append(tradeType);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", oddsValue=").append(oddsValue);
        sb.append(", oddsName=").append(oddsName);
        sb.append(", orderType=").append(orderType);
        sb.append(", oddsMetric=").append(oddsMetric);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", status=").append(status);
        sb.append(", scopeId=").append(scopeId);
        sb.append(", thirdOddsType=").append(thirdOddsType);
        sb.append(", thirdMarketSourceId=").append(thirdMarketSourceId);
        sb.append(", sendData=").append(sendData);
        sb.append(", linkId=").append(linkId);
        sb.append(", extraInfo=").append(extraInfo);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}