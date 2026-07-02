package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ThirdSportMarketCopy1 implements Serializable {
    @ApiModelProperty(value = "数据库id,自增")
    private Long id;

    @ApiModelProperty(value = "所属联赛ID")
    private Long tournamentId;

    @ApiModelProperty(value = "比赛ID:third_match_info.id")
    private Long matchId;

    @ApiModelProperty(value = "第三方玩法idstandard_sport_market_category.id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "第三提供的id。SR:报文中有id字段。")
    private String thirdMarketSourceId;

    @ApiModelProperty(value = "如果当前盘口与标准盘口中的B记录玩法相同且盘口显示内容相同,则该记录的当前字段值为B.ID")
    private Long referenceId;

    @ApiModelProperty(value = "盘口类型.属于赛前盘或者滚球盘.1:赛前盘;0:滚球盘.")
    private Integer marketType;

    @ApiModelProperty(value = "取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "盘口状态0-5.0:active,1:suspended,2:deactivated,3:settled,4:cancelled,5:handedOver")
    private Integer status;

    @ApiModelProperty(value = "盘口阶段id.对应对应system_item_dict.value")
    private String scopeId;

    @ApiModelProperty(value = "盘口名称编码.用于多语言")
    private Long nameCode;

    @ApiModelProperty(value = "玩法的中文名称.仅用用于数据库操作人员使用.")
    private String oddsTypeName;

    @ApiModelProperty(value = "接收到第三方数据后,可以通过该字段快速定位到当前的盘口.通过玩法和具体内容确认盘口的唯一性.SR提供的盘口数据id生成算法:Type_Typeid_Subtypeid_Specialoddsvalue")
    private String thirdOddsType;

    @ApiModelProperty(value = "该盘口具体显示的值.例如:大小球中,大小界限是:3.5")
    private String oddsValue;

    @ApiModelProperty(value = "排序类型")
    private String orderType;

    @ApiModelProperty(value = "盘口名称.")
    private String oddsName;

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

    private String remark;

    private Long createTime;

    private Long modifyTime;

    private String extraInfo;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public String getThirdMarketSourceId() {
        return thirdMarketSourceId;
    }

    public void setThirdMarketSourceId(String thirdMarketSourceId) {
        this.thirdMarketSourceId = thirdMarketSourceId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getMarketType() {
        return marketType;
    }

    public void setMarketType(Integer marketType) {
        this.marketType = marketType;
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

    public Long getNameCode() {
        return nameCode;
    }

    public void setNameCode(Long nameCode) {
        this.nameCode = nameCode;
    }

    public String getOddsTypeName() {
        return oddsTypeName;
    }

    public void setOddsTypeName(String oddsTypeName) {
        this.oddsTypeName = oddsTypeName;
    }

    public String getThirdOddsType() {
        return thirdOddsType;
    }

    public void setThirdOddsType(String thirdOddsType) {
        this.thirdOddsType = thirdOddsType;
    }

    public String getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(String oddsValue) {
        this.oddsValue = oddsValue;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOddsName() {
        return oddsName;
    }

    public void setOddsName(String oddsName) {
        this.oddsName = oddsName;
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

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", tournamentId=").append(tournamentId);
        sb.append(", matchId=").append(matchId);
        sb.append(", marketCategoryId=").append(marketCategoryId);
        sb.append(", thirdMarketSourceId=").append(thirdMarketSourceId);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", marketType=").append(marketType);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", status=").append(status);
        sb.append(", scopeId=").append(scopeId);
        sb.append(", nameCode=").append(nameCode);
        sb.append(", oddsTypeName=").append(oddsTypeName);
        sb.append(", thirdOddsType=").append(thirdOddsType);
        sb.append(", oddsValue=").append(oddsValue);
        sb.append(", orderType=").append(orderType);
        sb.append(", oddsName=").append(oddsName);
        sb.append(", oddsMetric=").append(oddsMetric);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", extraInfo=").append(extraInfo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}