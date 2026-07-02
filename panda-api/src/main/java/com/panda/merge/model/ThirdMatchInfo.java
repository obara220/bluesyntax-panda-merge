package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class ThirdMatchInfo implements Serializable {
    @ApiModelProperty(value = "id.id")
    private Long id;

    @ApiModelProperty(value = "运动种类id.对应sport.id")
    private Long sportId;

    @ApiModelProperty(value = "运动区域id.对应sport_region.id")
    private Long regionId;

    @ApiModelProperty(value = "联赛id.对应sport_tournament.id")
    private Long tournamentId;

    @ApiModelProperty(value = "父赛事id")
    private Long parentId;

    @ApiModelProperty(value = "比赛暂停.0:未暂停;1:暂停.")
    private Integer whetherStop;

    @ApiModelProperty(value = "比赛是否被激活.1:激活;0:未激活.激活的比赛可以进行下注.")
    private Integer active;

    @ApiModelProperty(value = "比赛是否可见.1:可见;0:不可见")
    private Integer visible;

    @ApiModelProperty(value = "数据源是否支持滚球.取值为1或0.1=支持;0=不支持")
    private Integer liveOddSupport;

    @ApiModelProperty(value = "赛前盘下注状态.赛前盘:1可下注;0不可下注;用于数据源控制下注状态")
    private Integer preMatchBetStatus;

    @ApiModelProperty(value = "赛事双方的对阵信息.格式:主场队名称VS客场队名称")
    private String homeAwayInfo;

    @ApiModelProperty(value = "赛事可下注状态.0:betstart;1:betstop")
    private Integer betStatus;

    @ApiModelProperty(value = "滚球下注状态.滚球中使用:1可下注;0不可下注;用于数据源控制下注状态")
    private Integer liveOddsBetStatus;

    @ApiModelProperty(value = "距离开赛时间.单位:秒")
    private Integer secondsMatchStart;

    @ApiModelProperty(value = "seconds_match_start被修改时刻的时间戳.单位:毫秒")
    private Long secondsMatchModifyTime;

    @ApiModelProperty(value = "主客队是否相反.与标准球队相比,主客队是否相反.0:否;1:是")
    private Integer homeAwayOpposite;

    @ApiModelProperty(value = "标准赛事的id.关联后的赛事ID.例如:AB2个记录,融合结束后在标准赛事表中生成一个新纪录.则当前记录的该字段使用新纪录的concern_event,id作为该字段值")
    private Long referenceId;

    @ApiModelProperty(value = "比赛开始时间.UTC时间")
    private Long beginTime;

    @ApiModelProperty(value = "是否为中立场.取值为0和1.1:是中立场,0:非中立场.操盘人员可手动处理")
    private Integer neutralGround;

    @ApiModelProperty(value = "当前比赛是否被预定.是否预定,0:否;1:是")
    private Integer booked;

    @ApiModelProperty(value = "数据来源编码.取值:SRBC分别代表:SportRadar、FeedConstruc.详情见data_source")
    private String dataSourceCode;

    @ApiModelProperty(value = "完赛状态，0否，1是，2中间态用于临时处理锁定数据用")
    private Integer matchOver;

    @ApiModelProperty(value = "局数(赛制).数字,例如:5,7,代表最多打5局7局")
    private Integer roundType;

    @ApiModelProperty(value = "彩票号.(爬虫爬取)")
    private String lotteryNumber;

    @ApiModelProperty(value = "是否有动画或视频（0:否，1:是）")
    private Integer lmtMode;

    @ApiModelProperty(value = "小编操作动画状态：OPEN:开CLOSE:关闭（仅tyson数据有该值）")
    private String sellStatus;

    @ApiModelProperty(value = "场地类型0：室外泥地球场、1：室外硬地球场、2：室内硬地球场、3：室外草地球场")
    private Integer siteType;

    @ApiModelProperty(value = "赛事状态.0:not_started;1:live;2:suspended;3:ended;4:closed;5:cancelled;6:abandoned;7:delayed;8:unknown;9:post")
    private Integer matchStatus;

    @ApiModelProperty(value = "赛事类型（默认1）1：普通赛事、2：电竞赛事")
    private Integer matchType;

    @ApiModelProperty(value = "比赛时长")
    private Integer matchLength;

    @ApiModelProperty(value = "比赛场地的国际化编码.")
    private Long matchPositionNameCode;

    @ApiModelProperty(value = "比赛场地名称,仅限中文.用于查看mysql时使用.")
    private String matchPositionName;

    @ApiModelProperty(value = "第三方赛事原始id.比如:SportRadar发送数据时,这场比赛的ID.")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "比赛阶段id.")
    private String matchPeriod;

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

    @ApiModelProperty(value = "赛季id")
    private String seasonId;

    @ApiModelProperty(value = "备注.")
    private String remark;

    @ApiModelProperty(value = "创建时间.")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.")
    private Long modifyTime;

    @ApiModelProperty(value = "主队阵型(TS)")
    private String homeFormation;

    @ApiModelProperty(value = "客队阵型(TS)")
    private String awayFormation;

    @ApiModelProperty(value = "球队是否变更（0:未变更(默认），1:主客队对调，2:主队或者客队变更）")
    private Integer teamChangeStatus;

    @ApiModelProperty(value = "主客队球员名称(主名称,客名称)")
    private String homeAwayPlayerName;

    @ApiModelProperty(value = "赛事对阵类型(0:人类，1:机器人)")
    private Integer competitorType;

    @ApiModelProperty(value = "赛事事件加速系数")
    private String accelerationFactor;

    @ApiModelProperty(value = "三方赛事联赛名是否变更,0:否,1:是")
    private Integer tournamentChangeStatus;

    @ApiModelProperty(value = "三方赛事是否出现过中断或取消状态,0:否,1:是")
    private Integer interruptionCancellationStatus;

    @ApiModelProperty(value = "事件来源类型(0:其他，1:现场（VENUE）,2电视（TV）)")
    private Integer liveEventSource;

    @ApiModelProperty(value = "主队预期进球xG")
    private BigDecimal homeExpectationXg;

    @ApiModelProperty(value = "主队预期失球")
    private BigDecimal homeExpectationLoss;

    @ApiModelProperty(value = "客队预期进球xG")
    private BigDecimal awayExpectationXg;

    @ApiModelProperty(value = "客队预期失球")
    private BigDecimal awayExpectationLoss;

    @ApiModelProperty(value = "赛事包含的所有球队多语言信息,json串,冗余字段,用于赛程页面查询")
    private String teamName;

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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
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

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Integer getLiveOddSupport() {
        return liveOddSupport;
    }

    public void setLiveOddSupport(Integer liveOddSupport) {
        this.liveOddSupport = liveOddSupport;
    }

    public Integer getPreMatchBetStatus() {
        return preMatchBetStatus;
    }

    public void setPreMatchBetStatus(Integer preMatchBetStatus) {
        this.preMatchBetStatus = preMatchBetStatus;
    }

    public String getHomeAwayInfo() {
        return homeAwayInfo;
    }

    public void setHomeAwayInfo(String homeAwayInfo) {
        this.homeAwayInfo = homeAwayInfo;
    }

    public Integer getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(Integer betStatus) {
        this.betStatus = betStatus;
    }

    public Integer getLiveOddsBetStatus() {
        return liveOddsBetStatus;
    }

    public void setLiveOddsBetStatus(Integer liveOddsBetStatus) {
        this.liveOddsBetStatus = liveOddsBetStatus;
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

    public Integer getHomeAwayOpposite() {
        return homeAwayOpposite;
    }

    public void setHomeAwayOpposite(Integer homeAwayOpposite) {
        this.homeAwayOpposite = homeAwayOpposite;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getNeutralGround() {
        return neutralGround;
    }

    public void setNeutralGround(Integer neutralGround) {
        this.neutralGround = neutralGround;
    }

    public Integer getBooked() {
        return booked;
    }

    public void setBooked(Integer booked) {
        this.booked = booked;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getMatchOver() {
        return matchOver;
    }

    public void setMatchOver(Integer matchOver) {
        this.matchOver = matchOver;
    }

    public Integer getRoundType() {
        return roundType;
    }

    public void setRoundType(Integer roundType) {
        this.roundType = roundType;
    }

    public String getLotteryNumber() {
        return lotteryNumber;
    }

    public void setLotteryNumber(String lotteryNumber) {
        this.lotteryNumber = lotteryNumber;
    }

    public Integer getLmtMode() {
        return lmtMode;
    }

    public void setLmtMode(Integer lmtMode) {
        this.lmtMode = lmtMode;
    }

    public String getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(String sellStatus) {
        this.sellStatus = sellStatus;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(Integer matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public Integer getMatchLength() {
        return matchLength;
    }

    public void setMatchLength(Integer matchLength) {
        this.matchLength = matchLength;
    }

    public Long getMatchPositionNameCode() {
        return matchPositionNameCode;
    }

    public void setMatchPositionNameCode(Long matchPositionNameCode) {
        this.matchPositionNameCode = matchPositionNameCode;
    }

    public String getMatchPositionName() {
        return matchPositionName;
    }

    public void setMatchPositionName(String matchPositionName) {
        this.matchPositionName = matchPositionName;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public String getMatchPeriod() {
        return matchPeriod;
    }

    public void setMatchPeriod(String matchPeriod) {
        this.matchPeriod = matchPeriod;
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

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
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

    public String getHomeFormation() {
        return homeFormation;
    }

    public void setHomeFormation(String homeFormation) {
        this.homeFormation = homeFormation;
    }

    public String getAwayFormation() {
        return awayFormation;
    }

    public void setAwayFormation(String awayFormation) {
        this.awayFormation = awayFormation;
    }

    public Integer getTeamChangeStatus() {
        return teamChangeStatus;
    }

    public void setTeamChangeStatus(Integer teamChangeStatus) {
        this.teamChangeStatus = teamChangeStatus;
    }

    public String getHomeAwayPlayerName() {
        return homeAwayPlayerName;
    }

    public void setHomeAwayPlayerName(String homeAwayPlayerName) {
        this.homeAwayPlayerName = homeAwayPlayerName;
    }

    public Integer getCompetitorType() {
        return competitorType;
    }

    public void setCompetitorType(Integer competitorType) {
        this.competitorType = competitorType;
    }

    public String getAccelerationFactor() {
        return accelerationFactor;
    }

    public void setAccelerationFactor(String accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
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

    public Integer getLiveEventSource() {
        return liveEventSource;
    }

    public void setLiveEventSource(Integer liveEventSource) {
        this.liveEventSource = liveEventSource;
    }

    public BigDecimal getHomeExpectationXg() {
        return homeExpectationXg;
    }

    public void setHomeExpectationXg(BigDecimal homeExpectationXg) {
        this.homeExpectationXg = homeExpectationXg;
    }

    public BigDecimal getHomeExpectationLoss() {
        return homeExpectationLoss;
    }

    public void setHomeExpectationLoss(BigDecimal homeExpectationLoss) {
        this.homeExpectationLoss = homeExpectationLoss;
    }

    public BigDecimal getAwayExpectationXg() {
        return awayExpectationXg;
    }

    public void setAwayExpectationXg(BigDecimal awayExpectationXg) {
        this.awayExpectationXg = awayExpectationXg;
    }

    public BigDecimal getAwayExpectationLoss() {
        return awayExpectationLoss;
    }

    public void setAwayExpectationLoss(BigDecimal awayExpectationLoss) {
        this.awayExpectationLoss = awayExpectationLoss;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", regionId=").append(regionId);
        sb.append(", tournamentId=").append(tournamentId);
        sb.append(", parentId=").append(parentId);
        sb.append(", whetherStop=").append(whetherStop);
        sb.append(", active=").append(active);
        sb.append(", visible=").append(visible);
        sb.append(", liveOddSupport=").append(liveOddSupport);
        sb.append(", preMatchBetStatus=").append(preMatchBetStatus);
        sb.append(", homeAwayInfo=").append(homeAwayInfo);
        sb.append(", betStatus=").append(betStatus);
        sb.append(", liveOddsBetStatus=").append(liveOddsBetStatus);
        sb.append(", secondsMatchStart=").append(secondsMatchStart);
        sb.append(", secondsMatchModifyTime=").append(secondsMatchModifyTime);
        sb.append(", homeAwayOpposite=").append(homeAwayOpposite);
        sb.append(", referenceId=").append(referenceId);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", neutralGround=").append(neutralGround);
        sb.append(", booked=").append(booked);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", matchOver=").append(matchOver);
        sb.append(", roundType=").append(roundType);
        sb.append(", lotteryNumber=").append(lotteryNumber);
        sb.append(", lmtMode=").append(lmtMode);
        sb.append(", sellStatus=").append(sellStatus);
        sb.append(", siteType=").append(siteType);
        sb.append(", matchStatus=").append(matchStatus);
        sb.append(", matchType=").append(matchType);
        sb.append(", matchLength=").append(matchLength);
        sb.append(", matchPositionNameCode=").append(matchPositionNameCode);
        sb.append(", matchPositionName=").append(matchPositionName);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", matchPeriod=").append(matchPeriod);
        sb.append(", tournamentRoundType=").append(tournamentRoundType);
        sb.append(", tournamentRoundNumber=").append(tournamentRoundNumber);
        sb.append(", tournamentRoundGroup=").append(tournamentRoundGroup);
        sb.append(", tournamentRoundName=").append(tournamentRoundName);
        sb.append(", tournamentRoundPhase=").append(tournamentRoundPhase);
        sb.append(", seasonId=").append(seasonId);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", homeFormation=").append(homeFormation);
        sb.append(", awayFormation=").append(awayFormation);
        sb.append(", teamChangeStatus=").append(teamChangeStatus);
        sb.append(", homeAwayPlayerName=").append(homeAwayPlayerName);
        sb.append(", competitorType=").append(competitorType);
        sb.append(", accelerationFactor=").append(accelerationFactor);
        sb.append(", tournamentChangeStatus=").append(tournamentChangeStatus);
        sb.append(", interruptionCancellationStatus=").append(interruptionCancellationStatus);
        sb.append(", liveEventSource=").append(liveEventSource);
        sb.append(", homeExpectationXg=").append(homeExpectationXg);
        sb.append(", homeExpectationLoss=").append(homeExpectationLoss);
        sb.append(", awayExpectationXg=").append(awayExpectationXg);
        sb.append(", awayExpectationLoss=").append(awayExpectationLoss);
        sb.append(", teamName=").append(teamName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}