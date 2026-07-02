package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleSpOdds implements Serializable {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "盘口Id")
    private Long marketId;

    @ApiModelProperty(value = "投注项英文名")
    private String oddsNameEn;

    @ApiModelProperty(value = "投注项中文名")
    private String oddsNameCn;

    @ApiModelProperty(value = "结算状态int(8)默认01编辑2确认3结算")
    private Integer settleStatus;

    @ApiModelProperty(value = "结算总次数，不能回滚")
    private Integer settleCount;

    @ApiModelProperty(value = "当前结算次数")
    private Integer settleTimes;

    @ApiModelProperty(value = "1输2赢3取消4走水")
    private Integer settleResult;

    @ApiModelProperty(value = "审核次序")
    private Integer checkNumber;

    @ApiModelProperty(value = "是否自动结算0否1是")
    private Integer isAutoSettle;

    @ApiModelProperty(value = "二次结算原因")
    private Integer settleReason;

    @ApiModelProperty(value = "二次结算详细原因")
    private String settleReasonDetail;

    @ApiModelProperty(value = "结算冻结0:未冻结1:冻结")
    private Integer settleFreeze;

    @ApiModelProperty(value = "排序")
    private Integer orderOdds;

    @ApiModelProperty(value = "操作人")
    private String operater;

    @ApiModelProperty(value = "用户id")
    private String userid;

    @ApiModelProperty(value = "操作类型1结算2回滚结算3重新结算")
    private Integer operateType;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
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

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getOddsNameEn() {
        return oddsNameEn;
    }

    public void setOddsNameEn(String oddsNameEn) {
        this.oddsNameEn = oddsNameEn;
    }

    public String getOddsNameCn() {
        return oddsNameCn;
    }

    public void setOddsNameCn(String oddsNameCn) {
        this.oddsNameCn = oddsNameCn;
    }

    public Integer getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(Integer settleStatus) {
        this.settleStatus = settleStatus;
    }

    public Integer getOrderOdds() {
        return orderOdds;
    }
    public void setOrderOdds(Integer orderOdds) {
        this.orderOdds = orderOdds;
    }
    public Integer getSettleCount() {
        return settleCount;
    }

    public void setSettleCount(Integer settleCount) {
        this.settleCount = settleCount;
    }

    public Integer getSettleTimes() {
        return settleTimes;
    }

    public void setSettleTimes(Integer settleTimes) {
        this.settleTimes = settleTimes;
    }

    public Integer getSettleResult() {
        return settleResult;
    }

    public void setSettleResult(Integer settleResult) {
        this.settleResult = settleResult;
    }

    public Integer getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Integer checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Integer getIsAutoSettle() {
        return isAutoSettle;
    }

    public void setIsAutoSettle(Integer isAutoSettle) {
        this.isAutoSettle = isAutoSettle;
    }

    public Integer getSettleReason() {
        return settleReason;
    }

    public void setSettleReason(Integer settleReason) {
        this.settleReason = settleReason;
    }

    public String getSettleReasonDetail() {
        return settleReasonDetail;
    }

    public void setSettleReasonDetail(String settleReasonDetail) {
        this.settleReasonDetail = settleReasonDetail;
    }

    public Integer getSettleFreeze() {
        return settleFreeze;
    }

    public void setSettleFreeze(Integer settleFreeze) {
        this.settleFreeze = settleFreeze;
    }

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
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
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", sportId=").append(sportId);
        sb.append(", marketId=").append(marketId);
        sb.append(", oddsNameEn=").append(oddsNameEn);
        sb.append(", oddsNameCn=").append(oddsNameCn);
        sb.append(", settleStatus=").append(settleStatus);
        sb.append(", settleCount=").append(settleCount);
        sb.append(", settleTimes=").append(settleTimes);
        sb.append(", settleResult=").append(settleResult);
        sb.append(", checkNumber=").append(checkNumber);
        sb.append(", isAutoSettle=").append(isAutoSettle);
        sb.append(", settleReason=").append(settleReason);
        sb.append(", settleReasonDetail=").append(settleReasonDetail);
        sb.append(", orderOdds=").append(orderOdds);
        sb.append(", settleFreeze=").append(settleFreeze);
        sb.append(", operater=").append(operater);
        sb.append(", userid=").append(userid);
        sb.append(", operateType=").append(operateType);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}