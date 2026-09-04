package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardMatchInfo implements Serializable {
    @ApiModelProperty(value = "id.id")
    private Long id;

    @ApiModelProperty(value = "体育种类id.运动种类id对应sport.id")
    private Long sportId;

    @ApiModelProperty(value = "赛事排序值")
    private Integer sortValue;

    @ApiModelProperty(value = "标准联赛id.对应联赛id对应standard_sport_tournament.id")
    private Long standardTournamentId;

    @ApiModelProperty(value = "第三方比赛id.第三方比赛在表third_match_info中的id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "比赛进行时间.单位:秒.例如:3分钟11秒,则该值是191")
    private Integer secondsMatchStart;

    @ApiModelProperty(value = "seconds_match_start被修改时刻的时间戳.单位:毫秒")
    private Long secondsMatchModifyTime;

    @ApiModelProperty(value = "赛事是否开放赛前盘.取值为1或0.1=开放;0=不开放")
    private Integer preMatchBusiness;

    @ApiModelProperty(value = "赛事是否开放滚球.取值为1或0.1=开放;0=不开放")
    private Integer liveOddBusiness;

    @ApiModelProperty(value = "比赛开盘标识.0:开盘;1:封盘;2:关盘;11:锁盘;")
    private Integer operateMatchStatus;

    @ApiModelProperty(value = "比赛开始时间.比赛开始时间UTC时间")
    private Long beginTime;

    @ApiModelProperty(value = "0:系统更新1:人工更新")
    private Integer beginTimeStatus;

    @ApiModelProperty(value = "比赛是否被激活.1:激活;0:未激活.激活的比赛可以进行下注.")
    private Integer active;

    @ApiModelProperty(value = "赛事状态.比如:未开赛,滚球,取消,延迟等.取system_item_dic中的value字段")
    private Integer matchStatus;

    @ApiModelProperty(value = "是否为中立场.取值为0和1.1:是中立场,0:非中立场.操盘人员可手动处理")
    private Integer neutralGround;

    @ApiModelProperty(value = "0:系统更新1:人工更新")
    private Integer neutralGroundStatus;

    @ApiModelProperty(value = "标准赛事编码.用于管理的赛事id")
    private String matchManageId;

    @ApiModelProperty(value = "比赛场地名称,仅限中文.用于查看mysql时使用.")
    private String matchPositionName;

    @ApiModelProperty(value = "比赛场地的国际化编码")
    private Long matchPositionNameCode;

    @ApiModelProperty(value = "风控服务商编码.详见数据源表data_source中的code字段")
    private String riskManagerCode;

    @ApiModelProperty(value = "数据来源编码.取值见:data_source.code")
    private String dataSourceCode;

    @ApiModelProperty(value = "关联数据源编码列表.数据样例:SR,BC,188;SR,188;BC,188")
    private String relatedDataSourceCoderList;

    @ApiModelProperty(value = "关联数据源数量")
    private Integer relatedDataSourceCoderNum;

    @ApiModelProperty(value = "数据供应商编码.取值见:data_source.code")
    private String matchDataProviderCode;

    @ApiModelProperty(value = "第三方赛事原始id.")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "赛事双方的对阵信息.格式:主场队名称VS客场队名称")
    private String homeAwayInfo;

    @ApiModelProperty(value = "Reverse:相反,Positive")
    private String reverseStatus;

    @ApiModelProperty(value = "父赛事id")
    private Long parentId;

    @ApiModelProperty(value = "比赛暂停.0:未暂停;1:暂停.")
    private Integer whetherStop;

    @ApiModelProperty(value = "赛事可下注状态.0:betstart;1:betstop")
    private Integer betStatus;

    @ApiModelProperty(value = "比赛阶段id.取system_item_dic中的value字段")
    private Long matchPeriodId;

    @ApiModelProperty(value = "赛事类型（默认1）1：普通赛事、2：电竞赛事")
    private Integer matchType;

    @ApiModelProperty(value = "赛季id")
    private String seasonId;

    @ApiModelProperty(value = "轮次类型：Group，Cup，Qualification")
    private String tournamentRoundType;

    @ApiModelProperty(value = "当类型是group时存在值，原始示例：number='1'")
    private Integer tournamentRoundNumber;

    @ApiModelProperty(value = "当类型是group时存在值，原始示例：group='A'")
    private String tournamentRoundGroup;

    @ApiModelProperty(value = "如果存在值就传，原始示例：name='semifinal'")
    private String tournamentRoundName;

    @ApiModelProperty(value = "如果存在值就传，原始示例：phase='playoffs'")
    private String tournamentRoundPhase;

    @ApiModelProperty(value = "局数(赛制).数字,例如:5,7,代表最多打5局7局")
    private Integer roundType;

    @ApiModelProperty(value = "赛制是否人工编辑(对阵管理编辑)0:系统1:人工")
    private Integer roundOperateStatus;

    @ApiModelProperty(value = "移出Out,移入In")
    private String soldFlag;

    @ApiModelProperty(value = "彩票号.(爬虫爬取)")
    private String lotteryNumber;

    @ApiModelProperty(value = "比赛时长")
    private Integer matchLength;

    @ApiModelProperty(value = "场地类型0：室外泥地球场、1：室外硬地球场、2：室内硬地球场、3：室外草地球场")
    private Integer siteType;

    @ApiModelProperty(value = "比赛是否结束.0:未结束(不属于历史赛事);1:结束.2:临时状态")
    private Integer matchOver;

    @ApiModelProperty(value = "是否高热度赛事：0否1是")
    private Integer hotMatchStatus;

    @ApiModelProperty(value = "账务时间.UTC时间,精确到毫秒.")
    private Long financialTime;

    @ApiModelProperty(value = "备注.")
    private String remark;

    @ApiModelProperty(value = "创建时间.")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.")
    private Long modifyTime;

    @ApiModelProperty(value = "是否接受到滚球赔率：0(否),1(是)")
    private Integer oddsLive;

    @ApiModelProperty(value = "联赛别名称编码.联赛名称编码.用于多语言")
    private Long tournamentNameCode;

    @ApiModelProperty(value = "事件自动审核开启标识:1开启0关闭")
    private Integer autoAuditFlag;

    @ApiModelProperty(value = "是否编辑比分标识：1已编辑0未编辑")
    private Integer scoreOpflag;

    @ApiModelProperty(value = "球队是否变更0否1是")
    private Integer teamChangeStatus;

    @ApiModelProperty(value = "赛事轮次0未修改1人工修改")
    private Integer tournamentNumberStatus;

    @ApiModelProperty(value = "开赛时间变更标识0未变更,1变更")
    private Integer beginTimeChangeStatus;

    @ApiModelProperty(value = "标准赛事联赛名是否变更,0:否,1:是")
    private Integer tournamentChangeStatus;

    @ApiModelProperty(value = "标准赛事是否出现过中断或取消状态,0:否,1:是")
    private Integer interruptionCancellationStatus;

    @ApiModelProperty(value = "赛事排序坑位")
    private String orderNo;

    @ApiModelProperty(value = "赛事包含的所有球队多语言信息,json串,冗余字段,用于赛程页面查询")
    private String teamName;

    @ApiModelProperty(value = "赛事包含的所有球队id信息,json串,冗余字段,用于赛程页面查询")
    private String teamManageId;

    @ApiModelProperty(value = "重播赛事Code")
    private String replayMatchCode;

    @ApiModelProperty(value = "'PLS标准赛事ID")
    private Long plsStandardMatchId;

    @ApiModelProperty(value = "事件来源类型(0:其他，1:现场（VENUE）,2电视（TV）)")
    private Integer liveEventSource;

    public Integer getLiveEventSource() {
        return liveEventSource;
    }

    public void setLiveEventSource(Integer liveEventSource) {
        this.liveEventSource = liveEventSource;
    }

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

    public Integer getSortValue() {
        return sortValue;
    }

    public void setSortValue(Integer sortValue) {
        this.sortValue = sortValue;
    }

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public Integer getSecondsMatchStart() {
        return secondsMatchStart;
    }

    public void setSecondsMatchStart(Integer secondsMatchStart) {
        this.secondsMatchStart = secondsMatchStart;
    }

    public Long getSecondsMatchModifyTime() {
        return secondsMatchModifyTime;
    }

    public void setSecondsMatchModifyTime(Long secondsMatchModifyTime) {
        this.secondsMatchModifyTime = secondsMatchModifyTime;
    }

    public Integer getPreMatchBusiness() {
        return preMatchBusiness;
    }

    public void setPreMatchBusiness(Integer preMatchBusiness) {
        this.preMatchBusiness = preMatchBusiness;
    }

    public Integer getLiveOddBusiness() {
        return liveOddBusiness;
    }

    public void setLiveOddBusiness(Integer liveOddBusiness) {
        this.liveOddBusiness = liveOddBusiness;
    }

    public Integer getOperateMatchStatus() {
        return operateMatchStatus;
    }

    public void setOperateMatchStatus(Integer operateMatchStatus) {
        this.operateMatchStatus = operateMatchStatus;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getBeginTimeStatus() {
        return beginTimeStatus;
    }

    public void setBeginTimeStatus(Integer beginTimeStatus) {
        this.beginTimeStatus = beginTimeStatus;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(Integer matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Integer getNeutralGround() {
        return neutralGround;
    }

    public void setNeutralGround(Integer neutralGround) {
        this.neutralGround = neutralGround;
    }

    public Integer getNeutralGroundStatus() {
        return neutralGroundStatus;
    }

    public void setNeutralGroundStatus(Integer neutralGroundStatus) {
        this.neutralGroundStatus = neutralGroundStatus;
    }

    public String getMatchManageId() {
        return matchManageId;
    }

    public void setMatchManageId(String matchManageId) {
        this.matchManageId = matchManageId;
    }

    public String getMatchPositionName() {
        return matchPositionName;
    }

    public void setMatchPositionName(String matchPositionName) {
        this.matchPositionName = matchPositionName;
    }

    public Long getMatchPositionNameCode() {
        return matchPositionNameCode;
    }

    public void setMatchPositionNameCode(Long matchPositionNameCode) {
        this.matchPositionNameCode = matchPositionNameCode;
    }

    public String getRiskManagerCode() {
        return riskManagerCode;
    }

    public void setRiskManagerCode(String riskManagerCode) {
        this.riskManagerCode = riskManagerCode;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getRelatedDataSourceCoderList() {
        return relatedDataSourceCoderList;
    }

    public void setRelatedDataSourceCoderList(String relatedDataSourceCoderList) {
        this.relatedDataSourceCoderList = relatedDataSourceCoderList;
    }

    public Integer getRelatedDataSourceCoderNum() {
        return relatedDataSourceCoderNum;
    }

    public void setRelatedDataSourceCoderNum(Integer relatedDataSourceCoderNum) {
        this.relatedDataSourceCoderNum = relatedDataSourceCoderNum;
    }

    public String getMatchDataProviderCode() {
        return matchDataProviderCode;
    }

    public void setMatchDataProviderCode(String matchDataProviderCode) {
        this.matchDataProviderCode = matchDataProviderCode;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public String getHomeAwayInfo() {
        return homeAwayInfo;
    }

    public void setHomeAwayInfo(String homeAwayInfo) {
        this.homeAwayInfo = homeAwayInfo;
    }

    public String getReverseStatus() {
        return reverseStatus;
    }

    public void setReverseStatus(String reverseStatus) {
        this.reverseStatus = reverseStatus;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getWhetherStop() {
        return whetherStop;
    }

    public void setWhetherStop(Integer whetherStop) {
        this.whetherStop = whetherStop;
    }

    public Integer getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(Integer betStatus) {
        this.betStatus = betStatus;
    }

    public Long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(Long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getTournamentRoundType() {
        return tournamentRoundType;
    }

    public void setTournamentRoundType(String tournamentRoundType) {
        this.tournamentRoundType = tournamentRoundType;
    }

    public Integer getTournamentRoundNumber() {
        return tournamentRoundNumber;
    }

    public void setTournamentRoundNumber(Integer tournamentRoundNumber) {
        this.tournamentRoundNumber = tournamentRoundNumber;
    }

    public String getTournamentRoundGroup() {
        return tournamentRoundGroup;
    }

    public void setTournamentRoundGroup(String tournamentRoundGroup) {
        this.tournamentRoundGroup = tournamentRoundGroup;
    }

    public String getTournamentRoundName() {
        return tournamentRoundName;
    }

    public void setTournamentRoundName(String tournamentRoundName) {
        this.tournamentRoundName = tournamentRoundName;
    }

    public String getTournamentRoundPhase() {
        return tournamentRoundPhase;
    }

    public void setTournamentRoundPhase(String tournamentRoundPhase) {
        this.tournamentRoundPhase = tournamentRoundPhase;
    }

    public Integer getRoundType() {
        return roundType;
    }

    public void setRoundType(Integer roundType) {
        this.roundType = roundType;
    }

    public Integer getRoundOperateStatus() {
        return roundOperateStatus;
    }

    public void setRoundOperateStatus(Integer roundOperateStatus) {
        this.roundOperateStatus = roundOperateStatus;
    }

    public String getSoldFlag() {
        return soldFlag;
    }

    public void setSoldFlag(String soldFlag) {
        this.soldFlag = soldFlag;
    }

    public String getLotteryNumber() {
        return lotteryNumber;
    }

    public void setLotteryNumber(String lotteryNumber) {
        this.lotteryNumber = lotteryNumber;
    }

    public Integer getMatchLength() {
        return matchLength;
    }

    public void setMatchLength(Integer matchLength) {
        this.matchLength = matchLength;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getMatchOver() {
        return matchOver;
    }

    public void setMatchOver(Integer matchOver) {
        this.matchOver = matchOver;
    }

    public Integer getHotMatchStatus() {
        return hotMatchStatus;
    }

    public void setHotMatchStatus(Integer hotMatchStatus) {
        this.hotMatchStatus = hotMatchStatus;
    }

    public Long getFinancialTime() {
        return financialTime;
    }

    public void setFinancialTime(Long financialTime) {
        this.financialTime = financialTime;
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

    public Integer getOddsLive() {
        return oddsLive;
    }

    public void setOddsLive(Integer oddsLive) {
        this.oddsLive = oddsLive;
    }

    public Long getTournamentNameCode() {
        return tournamentNameCode;
    }

    public void setTournamentNameCode(Long tournamentNameCode) {
        this.tournamentNameCode = tournamentNameCode;
    }

    public Integer getAutoAuditFlag() {
        return autoAuditFlag;
    }

    public void setAutoAuditFlag(Integer autoAuditFlag) {
        this.autoAuditFlag = autoAuditFlag;
    }

    public Integer getScoreOpflag() {
        return scoreOpflag;
    }

    public void setScoreOpflag(Integer scoreOpflag) {
        this.scoreOpflag = scoreOpflag;
    }

    public Integer getTeamChangeStatus() {
        return teamChangeStatus;
    }

    public void setTeamChangeStatus(Integer teamChangeStatus) {
        this.teamChangeStatus = teamChangeStatus;
    }

    public Integer getTournamentNumberStatus() {
        return tournamentNumberStatus;
    }

    public void setTournamentNumberStatus(Integer tournamentNumberStatus) {
        this.tournamentNumberStatus = tournamentNumberStatus;
    }

    public Integer getBeginTimeChangeStatus() {
        return beginTimeChangeStatus;
    }

    public void setBeginTimeChangeStatus(Integer beginTimeChangeStatus) {
        this.beginTimeChangeStatus = beginTimeChangeStatus;
    }

    public Integer getTournamentChangeStatus() {
        return tournamentChangeStatus;
    }

    public void setTournamentChangeStatus(Integer tournamentChangeStatus) {
        this.tournamentChangeStatus = tournamentChangeStatus;
    }

    public Integer getInterruptionCancellationStatus() {
        return interruptionCancellationStatus;
    }

    public void setInterruptionCancellationStatus(Integer interruptionCancellationStatus) {
        this.interruptionCancellationStatus = interruptionCancellationStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamManageId() {
        return teamManageId;
    }

    public void setTeamManageId(String teamManageId) {
        this.teamManageId = teamManageId;
    }

    public String getReplayMatchCode() {
        return replayMatchCode;
    }

    public void setReplayMatchCode(String replayMatchCode) {
        this.replayMatchCode = replayMatchCode;
    }

    public Long getPlsStandardMatchId() {
        return plsStandardMatchId;
    }

    public void setPlsStandardMatchId(Long plsStandardMatchId) {
        this.plsStandardMatchId = plsStandardMatchId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", sortValue=").append(sortValue);
        sb.append(", standardTournamentId=").append(standardTournamentId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", secondsMatchStart=").append(secondsMatchStart);
        sb.append(", secondsMatchModifyTime=").append(secondsMatchModifyTime);
        sb.append(", preMatchBusiness=").append(preMatchBusiness);
        sb.append(", liveOddBusiness=").append(liveOddBusiness);
        sb.append(", operateMatchStatus=").append(operateMatchStatus);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", beginTimeStatus=").append(beginTimeStatus);
        sb.append(", active=").append(active);
        sb.append(", matchStatus=").append(matchStatus);
        sb.append(", neutralGround=").append(neutralGround);
        sb.append(", neutralGroundStatus=").append(neutralGroundStatus);
        sb.append(", matchManageId=").append(matchManageId);
        sb.append(", matchPositionName=").append(matchPositionName);
        sb.append(", matchPositionNameCode=").append(matchPositionNameCode);
        sb.append(", riskManagerCode=").append(riskManagerCode);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", relatedDataSourceCoderList=").append(relatedDataSourceCoderList);
        sb.append(", relatedDataSourceCoderNum=").append(relatedDataSourceCoderNum);
        sb.append(", matchDataProviderCode=").append(matchDataProviderCode);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", homeAwayInfo=").append(homeAwayInfo);
        sb.append(", reverseStatus=").append(reverseStatus);
        sb.append(", parentId=").append(parentId);
        sb.append(", whetherStop=").append(whetherStop);
        sb.append(", betStatus=").append(betStatus);
        sb.append(", matchPeriodId=").append(matchPeriodId);
        sb.append(", matchType=").append(matchType);
        sb.append(", seasonId=").append(seasonId);
        sb.append(", tournamentRoundType=").append(tournamentRoundType);
        sb.append(", tournamentRoundNumber=").append(tournamentRoundNumber);
        sb.append(", tournamentRoundGroup=").append(tournamentRoundGroup);
        sb.append(", tournamentRoundName=").append(tournamentRoundName);
        sb.append(", tournamentRoundPhase=").append(tournamentRoundPhase);
        sb.append(", roundType=").append(roundType);
        sb.append(", roundOperateStatus=").append(roundOperateStatus);
        sb.append(", soldFlag=").append(soldFlag);
        sb.append(", lotteryNumber=").append(lotteryNumber);
        sb.append(", matchLength=").append(matchLength);
        sb.append(", siteType=").append(siteType);
        sb.append(", matchOver=").append(matchOver);
        sb.append(", hotMatchStatus=").append(hotMatchStatus);
        sb.append(", financialTime=").append(financialTime);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", oddsLive=").append(oddsLive);
        sb.append(", tournamentNameCode=").append(tournamentNameCode);
        sb.append(", autoAuditFlag=").append(autoAuditFlag);
        sb.append(", scoreOpflag=").append(scoreOpflag);
        sb.append(", teamChangeStatus=").append(teamChangeStatus);
        sb.append(", tournamentNumberStatus=").append(tournamentNumberStatus);
        sb.append(", beginTimeChangeStatus=").append(beginTimeChangeStatus);
        sb.append(", tournamentChangeStatus=").append(tournamentChangeStatus);
        sb.append(", interruptionCancellationStatus=").append(interruptionCancellationStatus);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", teamName=").append(teamName);
        sb.append(", teamManageId=").append(teamManageId);
        sb.append(", replayMatchCode=").append(replayMatchCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", plsStandardMatchId=").append(plsStandardMatchId);
        sb.append("]");
        return sb.toString();
    }
}