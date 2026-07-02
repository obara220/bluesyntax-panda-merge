package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchStatisticsInfo implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "第三方原始事件id")
    private String thirdSourceEventId;

    @ApiModelProperty(value = "第三方赛事原始id")
    private String thirdSourceMatchId;

    @ApiModelProperty(value = "第三方赛事id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "统计时间点.UTC标准时间")
    private Long eventTime;

    @ApiModelProperty(value = "第三方事件类型id")
    private Long thirdEventTypeId;

    @ApiModelProperty(value = "第三方事件类型")
    private String thirdEventType;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "主客队信息.home:主场队;away:客场队")
    private String homeAway;

    @ApiModelProperty(value = "当前比赛进行时间.单位:秒")
    private Integer secondsMatchStart;

    @ApiModelProperty(value = "预计比赛时长.单位:秒")
    private Integer matchLength;

    @ApiModelProperty(value = "当前比分信息")
    private String score;

    @ApiModelProperty(value = "角球比分")
    private String cornerScore;

    @ApiModelProperty(value = "黄牌比分")
    private String yellowCardScore;

    @ApiModelProperty(value = "红牌比分")
    private String redCardScore;

    @ApiModelProperty(value = "射正比分")
    private String shotOnTargetScore;

    @ApiModelProperty(value = "射偏比分")
    private String shotOffTargetScore;

    @ApiModelProperty(value = "危险进攻次数比分")
    private String dangerousAttackScore;

    @ApiModelProperty(value = "发球得分")
    private String acesScore;

    @ApiModelProperty(value = "两次发球失误比分")
    private String doubleFaultScore;

    @ApiModelProperty(value = "标准运动种类id.对应standard_sport_type.id")
    private Long sportId;

    @ApiModelProperty(value = "阶段比分")
    private String periodScore;

    @ApiModelProperty(value = "四分之一节比分")
    private String quarterScore;

    @ApiModelProperty(value = "汇总比分")
    private String setScore;

    @ApiModelProperty(value = "汇总比分1")
    private String set1Score;

    @ApiModelProperty(value = "汇总比分2")
    private String set2Score;

    @ApiModelProperty(value = "汇总比分3")
    private String set3Score;

    @ApiModelProperty(value = "汇总比分4")
    private String set4Score;

    @ApiModelProperty(value = "汇总比分5")
    private String set5Score;

    @ApiModelProperty(value = "汇总比分6")
    private String set6Score;

    @ApiModelProperty(value = "汇总比分7")
    private String set7Score;

    @ApiModelProperty(value = "汇总比分8")
    private String set8Score;

    @ApiModelProperty(value = "汇总比分9")
    private String set9Score;

    @ApiModelProperty(value = "汇总比分10")
    private String set10Score;

    @ApiModelProperty(value = "一局比分(网球)")
    private String gameScore;

    @ApiModelProperty(value = "发球人")
    private Integer server;

    @ApiModelProperty(value = "Gameshortinfo")
    private String info;

    @ApiModelProperty(value = "比赛剩余时间.单位:秒")
    private Integer remainingTime;

    @ApiModelProperty(value = "比赛阶段")
    private Integer period;

    @ApiModelProperty(value = "比赛阶段个数")
    private Integer periodLength;

    @ApiModelProperty(value = "Totalsetcount")
    private Integer setCount;

    @ApiModelProperty(value = "点球比分")
    private String penaltyScore;

    @ApiModelProperty(value = "任意球比分")
    private String freeKickScore;

    @ApiModelProperty(value = "加时赛比分")
    private String extraTimeScore;

    @ApiModelProperty(value = "set1的黄牌比分")
    private String set1YellowCardScore;

    @ApiModelProperty(value = "set1的红牌比分")
    private String set1RedCardScore;

    @ApiModelProperty(value = "set1的角球比分")
    private String set1CornerScore;

    @ApiModelProperty(value = "set2的黄牌比分")
    private String set2YellowCardScore;

    @ApiModelProperty(value = "set2的红牌比分")
    private String set2RedCardScore;

    @ApiModelProperty(value = "set2的角球比分")
    private String set2CornerScore;

    @ApiModelProperty(value = "备注")
    private String remark;

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

    public String getThirdSourceEventId() {
        return thirdSourceEventId;
    }

    public void setThirdSourceEventId(String thirdSourceEventId) {
        this.thirdSourceEventId = thirdSourceEventId;
    }

    public String getThirdSourceMatchId() {
        return thirdSourceMatchId;
    }

    public void setThirdSourceMatchId(String thirdSourceMatchId) {
        this.thirdSourceMatchId = thirdSourceMatchId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public Long getThirdEventTypeId() {
        return thirdEventTypeId;
    }

    public void setThirdEventTypeId(Long thirdEventTypeId) {
        this.thirdEventTypeId = thirdEventTypeId;
    }

    public String getThirdEventType() {
        return thirdEventType;
    }

    public void setThirdEventType(String thirdEventType) {
        this.thirdEventType = thirdEventType;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Integer getSecondsMatchStart() {
        return secondsMatchStart;
    }

    public void setSecondsMatchStart(Integer secondsMatchStart) {
        this.secondsMatchStart = secondsMatchStart;
    }

    public Integer getMatchLength() {
        return matchLength;
    }

    public void setMatchLength(Integer matchLength) {
        this.matchLength = matchLength;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCornerScore() {
        return cornerScore;
    }

    public void setCornerScore(String cornerScore) {
        this.cornerScore = cornerScore;
    }

    public String getYellowCardScore() {
        return yellowCardScore;
    }

    public void setYellowCardScore(String yellowCardScore) {
        this.yellowCardScore = yellowCardScore;
    }

    public String getRedCardScore() {
        return redCardScore;
    }

    public void setRedCardScore(String redCardScore) {
        this.redCardScore = redCardScore;
    }

    public String getShotOnTargetScore() {
        return shotOnTargetScore;
    }

    public void setShotOnTargetScore(String shotOnTargetScore) {
        this.shotOnTargetScore = shotOnTargetScore;
    }

    public String getShotOffTargetScore() {
        return shotOffTargetScore;
    }

    public void setShotOffTargetScore(String shotOffTargetScore) {
        this.shotOffTargetScore = shotOffTargetScore;
    }

    public String getDangerousAttackScore() {
        return dangerousAttackScore;
    }

    public void setDangerousAttackScore(String dangerousAttackScore) {
        this.dangerousAttackScore = dangerousAttackScore;
    }

    public String getAcesScore() {
        return acesScore;
    }

    public void setAcesScore(String acesScore) {
        this.acesScore = acesScore;
    }

    public String getDoubleFaultScore() {
        return doubleFaultScore;
    }

    public void setDoubleFaultScore(String doubleFaultScore) {
        this.doubleFaultScore = doubleFaultScore;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getPeriodScore() {
        return periodScore;
    }

    public void setPeriodScore(String periodScore) {
        this.periodScore = periodScore;
    }

    public String getQuarterScore() {
        return quarterScore;
    }

    public void setQuarterScore(String quarterScore) {
        this.quarterScore = quarterScore;
    }

    public String getSetScore() {
        return setScore;
    }

    public void setSetScore(String setScore) {
        this.setScore = setScore;
    }

    public String getSet1Score() {
        return set1Score;
    }

    public void setSet1Score(String set1Score) {
        this.set1Score = set1Score;
    }

    public String getSet2Score() {
        return set2Score;
    }

    public void setSet2Score(String set2Score) {
        this.set2Score = set2Score;
    }

    public String getSet3Score() {
        return set3Score;
    }

    public void setSet3Score(String set3Score) {
        this.set3Score = set3Score;
    }

    public String getSet4Score() {
        return set4Score;
    }

    public void setSet4Score(String set4Score) {
        this.set4Score = set4Score;
    }

    public String getSet5Score() {
        return set5Score;
    }

    public void setSet5Score(String set5Score) {
        this.set5Score = set5Score;
    }

    public String getSet6Score() {
        return set6Score;
    }

    public void setSet6Score(String set6Score) {
        this.set6Score = set6Score;
    }

    public String getSet7Score() {
        return set7Score;
    }

    public void setSet7Score(String set7Score) {
        this.set7Score = set7Score;
    }

    public String getSet8Score() {
        return set8Score;
    }

    public void setSet8Score(String set8Score) {
        this.set8Score = set8Score;
    }

    public String getSet9Score() {
        return set9Score;
    }

    public void setSet9Score(String set9Score) {
        this.set9Score = set9Score;
    }

    public String getSet10Score() {
        return set10Score;
    }

    public void setSet10Score(String set10Score) {
        this.set10Score = set10Score;
    }

    public String getGameScore() {
        return gameScore;
    }

    public void setGameScore(String gameScore) {
        this.gameScore = gameScore;
    }

    public Integer getServer() {
        return server;
    }

    public void setServer(Integer server) {
        this.server = server;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(Integer periodLength) {
        this.periodLength = periodLength;
    }

    public Integer getSetCount() {
        return setCount;
    }

    public void setSetCount(Integer setCount) {
        this.setCount = setCount;
    }

    public String getPenaltyScore() {
        return penaltyScore;
    }

    public void setPenaltyScore(String penaltyScore) {
        this.penaltyScore = penaltyScore;
    }

    public String getFreeKickScore() {
        return freeKickScore;
    }

    public void setFreeKickScore(String freeKickScore) {
        this.freeKickScore = freeKickScore;
    }

    public String getExtraTimeScore() {
        return extraTimeScore;
    }

    public void setExtraTimeScore(String extraTimeScore) {
        this.extraTimeScore = extraTimeScore;
    }

    public String getSet1YellowCardScore() {
        return set1YellowCardScore;
    }

    public void setSet1YellowCardScore(String set1YellowCardScore) {
        this.set1YellowCardScore = set1YellowCardScore;
    }

    public String getSet1RedCardScore() {
        return set1RedCardScore;
    }

    public void setSet1RedCardScore(String set1RedCardScore) {
        this.set1RedCardScore = set1RedCardScore;
    }

    public String getSet1CornerScore() {
        return set1CornerScore;
    }

    public void setSet1CornerScore(String set1CornerScore) {
        this.set1CornerScore = set1CornerScore;
    }

    public String getSet2YellowCardScore() {
        return set2YellowCardScore;
    }

    public void setSet2YellowCardScore(String set2YellowCardScore) {
        this.set2YellowCardScore = set2YellowCardScore;
    }

    public String getSet2RedCardScore() {
        return set2RedCardScore;
    }

    public void setSet2RedCardScore(String set2RedCardScore) {
        this.set2RedCardScore = set2RedCardScore;
    }

    public String getSet2CornerScore() {
        return set2CornerScore;
    }

    public void setSet2CornerScore(String set2CornerScore) {
        this.set2CornerScore = set2CornerScore;
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
        sb.append(", thirdSourceEventId=").append(thirdSourceEventId);
        sb.append(", thirdSourceMatchId=").append(thirdSourceMatchId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", thirdEventTypeId=").append(thirdEventTypeId);
        sb.append(", thirdEventType=").append(thirdEventType);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", secondsMatchStart=").append(secondsMatchStart);
        sb.append(", matchLength=").append(matchLength);
        sb.append(", score=").append(score);
        sb.append(", cornerScore=").append(cornerScore);
        sb.append(", yellowCardScore=").append(yellowCardScore);
        sb.append(", redCardScore=").append(redCardScore);
        sb.append(", shotOnTargetScore=").append(shotOnTargetScore);
        sb.append(", shotOffTargetScore=").append(shotOffTargetScore);
        sb.append(", dangerousAttackScore=").append(dangerousAttackScore);
        sb.append(", acesScore=").append(acesScore);
        sb.append(", doubleFaultScore=").append(doubleFaultScore);
        sb.append(", sportId=").append(sportId);
        sb.append(", periodScore=").append(periodScore);
        sb.append(", quarterScore=").append(quarterScore);
        sb.append(", setScore=").append(setScore);
        sb.append(", set1Score=").append(set1Score);
        sb.append(", set2Score=").append(set2Score);
        sb.append(", set3Score=").append(set3Score);
        sb.append(", set4Score=").append(set4Score);
        sb.append(", set5Score=").append(set5Score);
        sb.append(", set6Score=").append(set6Score);
        sb.append(", set7Score=").append(set7Score);
        sb.append(", set8Score=").append(set8Score);
        sb.append(", set9Score=").append(set9Score);
        sb.append(", set10Score=").append(set10Score);
        sb.append(", gameScore=").append(gameScore);
        sb.append(", server=").append(server);
        sb.append(", info=").append(info);
        sb.append(", remainingTime=").append(remainingTime);
        sb.append(", period=").append(period);
        sb.append(", periodLength=").append(periodLength);
        sb.append(", setCount=").append(setCount);
        sb.append(", penaltyScore=").append(penaltyScore);
        sb.append(", freeKickScore=").append(freeKickScore);
        sb.append(", extraTimeScore=").append(extraTimeScore);
        sb.append(", set1YellowCardScore=").append(set1YellowCardScore);
        sb.append(", set1RedCardScore=").append(set1RedCardScore);
        sb.append(", set1CornerScore=").append(set1CornerScore);
        sb.append(", set2YellowCardScore=").append(set2YellowCardScore);
        sb.append(", set2RedCardScore=").append(set2RedCardScore);
        sb.append(", set2CornerScore=").append(set2CornerScore);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}