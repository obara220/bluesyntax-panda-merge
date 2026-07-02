package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class StandardSportMarketSell implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动种类id")
    private Long sportId;

    @ApiModelProperty(value = "标准赛事id")
    private Long matchInfoId;

    @ApiModelProperty(value = "局数(赛制).数字,例如:5,7,代表最多打5局7局")
    private Integer roundType;

    @ApiModelProperty(value = "赛事管理id")
    private String matchManageId;

    @ApiModelProperty(value = "赛前操盘平台如：SR")
    private String preRiskManagerCode;

    @ApiModelProperty(value = "赛前数据服务商")
    private String preMatchDataProviderCode;

    @ApiModelProperty(value = "滚球操盘平台如：SR、MTS")
    private String liveRiskManagerCode;

    @ApiModelProperty(value = "滚球数据服务商")
    private String liveMatchDataProviderCode;

    @ApiModelProperty(value = "联赛级别")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "是否支持滚球1支持0不支持")
    private Integer liveOddBusiness;

    @ApiModelProperty(value = "联赛id")
    private Long tournamentId;

    @ApiModelProperty(value = "主队id")
    private Long teamHomeId;

    @ApiModelProperty(value = "客队id")
    private Long teamAwayId;

    @ApiModelProperty(value = "赛前开售时间")
    private Long preMatchTime;

    @ApiModelProperty(value = "滚球开售时间")
    private Long liveOddTime;

    @ApiModelProperty(value = "开赛时间")
    private Long beginTime;

    @ApiModelProperty(value = "赛前操盘手id")
    private String preTraderId;

    @ApiModelProperty(value = "赛前操盘手")
    private String preTrader;

    private String preTraderDepartmentId;

    @ApiModelProperty(value = "赛前操盘手状态：未设置Not_Set，取消未设置Cancel_Sold_Not_Set,待审批Pending_Approval，已设置Setted")
    private String preTraderStatus;

    @ApiModelProperty(value = "滚球操盘手id")
    private String liveTraderId;

    @ApiModelProperty(value = "滚球操盘手")
    private String liveTrader;

    private String liveTraderDepartmentId;

    @ApiModelProperty(value = "滚球操盘手状态：未设置Not_Set，取消未设置Cancel_Sold_Not_Set，待审批Pending_Approval，已设置Setted")
    private String liveTraderStatus;

    @ApiModelProperty(value = "赛果审核员id")
    private String auditorId;

    @ApiModelProperty(value = "赛果审核员")
    private String auditor;

    private String auditorDepartmentId;

    @ApiModelProperty(value = "审核员状态：未设置Not_Set，待审批Pending_Approval，已设置Setted")
    private String auditorStatus;

    @ApiModelProperty(value = "报球员")
    private String reporter;

    /**
     * 报球员PD2
     */
    @ApiModelProperty(value = "报球员PD2")
    private String reporter2;

    @ApiModelProperty(value = "商业事件源编码如：SR,BC,BG")
    private String businessEvent;

    @ApiModelProperty(value = "未售Unsold，逾期未售Overdue_Unsold，申请延期Apply_Delay，开售Sold，取消预售Cancel_Sold,申请取消Apply_Cancel_Sold，停售Stop_Sold")
    private String preMatchSellStatus;

    @ApiModelProperty(value = "未售Unsold，逾期未售Overdue_Unsold，申请延期Apply_Delay，开售Sold，取消预售Cancel_Sold,申请取消Apply_Cancel_Sold，停售Stop_Sold")
    private String liveMatchSellStatus;

    @ApiModelProperty(value = "中立厂0否1是")
    private Integer neutralGround;

    @ApiModelProperty(value = "正常Enable,移除Move_Out,完赛End")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "赛事状态源")
    private String matchStatusSourceCode;

    @ApiModelProperty(value = "赛事标签")
    private Integer label;

    @ApiModelProperty(value = "结算时间")
    private Long settlementTime;

    @ApiModelProperty(value = "赛前使用过的赔率源,即开售后切换的赔率源(包括当前赔率源)用逗号隔开")
    private String preUsedOddsCodes;

    @ApiModelProperty(value = "滚球使用过的赔率源,即开售后切换的赔率源(包括当前赔率源)用逗号隔开")
    private String liveUsedOddsCodes;

    @ApiModelProperty(value = "开售时与开赛时间差:毫秒值")
    private Long sellBeginDiffer;

    @ApiModelProperty(value = "视频源")
    private String videoCode;

    @ApiModelProperty(value = "动画源")
    private String animationCode;

    @ApiModelProperty(value = "展示赛果比分状态: 1 展示 0 不展示")
    private Integer showResultStatus;

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

    public Long getMatchInfoId() {
        return matchInfoId;
    }

    public void setMatchInfoId(Long matchInfoId) {
        this.matchInfoId = matchInfoId;
    }

    public Integer getRoundType() {
        return roundType;
    }

    public void setRoundType(Integer roundType) {
        this.roundType = roundType;
    }

    public String getMatchManageId() {
        return matchManageId;
    }

    public void setMatchManageId(String matchManageId) {
        this.matchManageId = matchManageId;
    }

    public String getPreRiskManagerCode() {
        return preRiskManagerCode;
    }

    public void setPreRiskManagerCode(String preRiskManagerCode) {
        this.preRiskManagerCode = preRiskManagerCode;
    }

    public String getPreMatchDataProviderCode() {
        return preMatchDataProviderCode;
    }

    public void setPreMatchDataProviderCode(String preMatchDataProviderCode) {
        this.preMatchDataProviderCode = preMatchDataProviderCode;
    }

    public String getLiveRiskManagerCode() {
        return liveRiskManagerCode;
    }

    public void setLiveRiskManagerCode(String liveRiskManagerCode) {
        this.liveRiskManagerCode = liveRiskManagerCode;
    }

    public String getLiveMatchDataProviderCode() {
        return liveMatchDataProviderCode;
    }

    public void setLiveMatchDataProviderCode(String liveMatchDataProviderCode) {
        this.liveMatchDataProviderCode = liveMatchDataProviderCode;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Integer getLiveOddBusiness() {
        return liveOddBusiness;
    }

    public void setLiveOddBusiness(Integer liveOddBusiness) {
        this.liveOddBusiness = liveOddBusiness;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getTeamHomeId() {
        return teamHomeId;
    }

    public void setTeamHomeId(Long teamHomeId) {
        this.teamHomeId = teamHomeId;
    }

    public Long getTeamAwayId() {
        return teamAwayId;
    }

    public void setTeamAwayId(Long teamAwayId) {
        this.teamAwayId = teamAwayId;
    }

    public Long getPreMatchTime() {
        return preMatchTime;
    }

    public void setPreMatchTime(Long preMatchTime) {
        this.preMatchTime = preMatchTime;
    }

    public Long getLiveOddTime() {
        return liveOddTime;
    }

    public void setLiveOddTime(Long liveOddTime) {
        this.liveOddTime = liveOddTime;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public String getPreTraderId() {
        return preTraderId;
    }

    public void setPreTraderId(String preTraderId) {
        this.preTraderId = preTraderId;
    }

    public String getPreTrader() {
        return preTrader;
    }

    public void setPreTrader(String preTrader) {
        this.preTrader = preTrader;
    }

    public String getPreTraderDepartmentId() {
        return preTraderDepartmentId;
    }

    public void setPreTraderDepartmentId(String preTraderDepartmentId) {
        this.preTraderDepartmentId = preTraderDepartmentId;
    }

    public String getPreTraderStatus() {
        return preTraderStatus;
    }

    public void setPreTraderStatus(String preTraderStatus) {
        this.preTraderStatus = preTraderStatus;
    }

    public String getLiveTraderId() {
        return liveTraderId;
    }

    public void setLiveTraderId(String liveTraderId) {
        this.liveTraderId = liveTraderId;
    }

    public String getLiveTrader() {
        return liveTrader;
    }

    public void setLiveTrader(String liveTrader) {
        this.liveTrader = liveTrader;
    }

    public String getLiveTraderDepartmentId() {
        return liveTraderDepartmentId;
    }

    public void setLiveTraderDepartmentId(String liveTraderDepartmentId) {
        this.liveTraderDepartmentId = liveTraderDepartmentId;
    }

    public String getLiveTraderStatus() {
        return liveTraderStatus;
    }

    public void setLiveTraderStatus(String liveTraderStatus) {
        this.liveTraderStatus = liveTraderStatus;
    }

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getAuditorDepartmentId() {
        return auditorDepartmentId;
    }

    public void setAuditorDepartmentId(String auditorDepartmentId) {
        this.auditorDepartmentId = auditorDepartmentId;
    }

    public String getAuditorStatus() {
        return auditorStatus;
    }

    public void setAuditorStatus(String auditorStatus) {
        this.auditorStatus = auditorStatus;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporter2() {
        return reporter2;
    }

    public void setReporter2(String reporter2) {
        this.reporter2 = reporter2;
    }

    public String getBusinessEvent() {
        return businessEvent;
    }

    public void setBusinessEvent(String businessEvent) {
        this.businessEvent = businessEvent;
    }

    public String getPreMatchSellStatus() {
        return preMatchSellStatus;
    }

    public void setPreMatchSellStatus(String preMatchSellStatus) {
        this.preMatchSellStatus = preMatchSellStatus;
    }

    public String getLiveMatchSellStatus() {
        return liveMatchSellStatus;
    }

    public void setLiveMatchSellStatus(String liveMatchSellStatus) {
        this.liveMatchSellStatus = liveMatchSellStatus;
    }

    public Integer getNeutralGround() {
        return neutralGround;
    }

    public void setNeutralGround(Integer neutralGround) {
        this.neutralGround = neutralGround;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMatchStatusSourceCode() {
        return matchStatusSourceCode;
    }

    public void setMatchStatusSourceCode(String matchStatusSourceCode) {
        this.matchStatusSourceCode = matchStatusSourceCode;
    }

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    public Long getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(Long settlementTime) {
        this.settlementTime = settlementTime;
    }

    public String getPreUsedOddsCodes() {
        return preUsedOddsCodes;
    }

    public void setPreUsedOddsCodes(String preUsedOddsCodes) {
        this.preUsedOddsCodes = preUsedOddsCodes;
    }

    public String getLiveUsedOddsCodes() {
        return liveUsedOddsCodes;
    }

    public void setLiveUsedOddsCodes(String liveUsedOddsCodes) {
        this.liveUsedOddsCodes = liveUsedOddsCodes;
    }

    public Long getSellBeginDiffer() {
        return sellBeginDiffer;
    }

    public void setSellBeginDiffer(Long sellBeginDiffer) {
        this.sellBeginDiffer = sellBeginDiffer;
    }

    public String getVideoCode() {
        return videoCode;
    }

    public void setVideoCode(String videoCode) {
        this.videoCode = videoCode;
    }

    public String getAnimationCode() {
        return animationCode;
    }

    public void setAnimationCode(String animationCode) {
        this.animationCode = animationCode;
    }

    public Integer getShowResultStatus() {
        return showResultStatus;
    }

    public void setShowResultStatus(Integer showResultStatus) {
        this.showResultStatus = showResultStatus;
    }

    public boolean preMatchSold() {
        return StringUtils.equalsIgnoreCase(preMatchSellStatus, "Sold");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", matchInfoId=").append(matchInfoId);
        sb.append(", roundType=").append(roundType);
        sb.append(", matchManageId=").append(matchManageId);
        sb.append(", preRiskManagerCode=").append(preRiskManagerCode);
        sb.append(", preMatchDataProviderCode=").append(preMatchDataProviderCode);
        sb.append(", liveRiskManagerCode=").append(liveRiskManagerCode);
        sb.append(", liveMatchDataProviderCode=").append(liveMatchDataProviderCode);
        sb.append(", tournamentLevel=").append(tournamentLevel);
        sb.append(", liveOddBusiness=").append(liveOddBusiness);
        sb.append(", tournamentId=").append(tournamentId);
        sb.append(", teamHomeId=").append(teamHomeId);
        sb.append(", teamAwayId=").append(teamAwayId);
        sb.append(", preMatchTime=").append(preMatchTime);
        sb.append(", liveOddTime=").append(liveOddTime);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", preTraderId=").append(preTraderId);
        sb.append(", preTrader=").append(preTrader);
        sb.append(", preTraderDepartmentId=").append(preTraderDepartmentId);
        sb.append(", preTraderStatus=").append(preTraderStatus);
        sb.append(", liveTraderId=").append(liveTraderId);
        sb.append(", liveTrader=").append(liveTrader);
        sb.append(", liveTraderDepartmentId=").append(liveTraderDepartmentId);
        sb.append(", liveTraderStatus=").append(liveTraderStatus);
        sb.append(", auditorId=").append(auditorId);
        sb.append(", auditor=").append(auditor);
        sb.append(", auditorDepartmentId=").append(auditorDepartmentId);
        sb.append(", auditorStatus=").append(auditorStatus);
        sb.append(", reporter=").append(reporter);
        sb.append(", reporter2=").append(reporter2);
        sb.append(", businessEvent=").append(businessEvent);
        sb.append(", preMatchSellStatus=").append(preMatchSellStatus);
        sb.append(", liveMatchSellStatus=").append(liveMatchSellStatus);
        sb.append(", neutralGround=").append(neutralGround);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", matchStatusSourceCode=").append(matchStatusSourceCode);
        sb.append(", label=").append(label);
        sb.append(", settlementTime=").append(settlementTime);
        sb.append(", preUsedOddsCodes=").append(preUsedOddsCodes);
        sb.append(", liveUsedOddsCodes=").append(liveUsedOddsCodes);
        sb.append(", sellBeginDiffer=").append(sellBeginDiffer);
        sb.append(", videoCode=").append(videoCode);
        sb.append(", animationCode=").append(animationCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
