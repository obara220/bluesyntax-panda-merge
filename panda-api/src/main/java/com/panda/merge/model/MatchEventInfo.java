package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchEventInfo implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "体育种类id.对应standard_sport_type.id")
    private Long sportId;

    @ApiModelProperty(value = "是否被取消.1被取消;0:没有被取消")
    private Integer canceled;

    @ApiModelProperty(value = "对应data_source.code")
    private String dataSourceCode;

    @ApiModelProperty(value = "事件编码.对应match_event_type.event_code")
    private String eventCode;

    @ApiModelProperty(value = "事件发生时间.UTC时间")
    private Long eventTime;

    @ApiModelProperty(value = "扩展信息")
    private String extraInfo;

    private String addition1;

    @ApiModelProperty(value = "主客场.主场队:home;客场队:away")
    private String homeAway;

    @ApiModelProperty(value = "当前第几局")
    private Integer secondNum;

    @ApiModelProperty(value = "盘主队比分")
    private Integer firstT1;

    @ApiModelProperty(value = "盘客队比分")
    private Integer firstT2;

    @ApiModelProperty(value = "局主队比分")
    private Integer secondT1;

    @ApiModelProperty(value = "局客队比分")
    private Integer secondT2;

    @ApiModelProperty(value = "当前盘数")
    private Integer firstNum;

    @ApiModelProperty(value = "比赛阶段id.system_item_dict.value")
    private Long matchPeriodId;

    @ApiModelProperty(value = "球员1的id")
    private Long player1Id;

    @ApiModelProperty(value = "球员1的名称")
    private String player1Name;

    @ApiModelProperty(value = "球员2的id")
    private Long player2Id;

    @ApiModelProperty(value = "球员2的名称")
    private String player2Name;

    @ApiModelProperty(value = "距离比赛开始多少秒")
    private Long secondsFromStart;

    @ApiModelProperty(value = "当前节阶段剩余时间")
    private Long periodRemainingSeconds;

    @ApiModelProperty(value = "标准赛事的id.对应standard_match_info.id")
    private Long standardMatchId;

    @ApiModelProperty(value = "标准球队ID.对应standard_sport_team.id")
    private Long standardTeamId;

    @ApiModelProperty(value = "主队数量")
    private Integer t1;

    @ApiModelProperty(value = "客队数量")
    private Integer t2;

    @ApiModelProperty(value = "第三方数据源提供的该事件id.")
    private String thirdEventId;

    @ApiModelProperty(value = "第三方赛事的id.对应third_match_info.id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "比赛在数据源中的ID")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "第三方球队id.对应third_sport_team.id")
    private Long thirdTeamId;

    @ApiModelProperty(value = "数据来源类型.0:UOF;1:ScoringFeed")
    private Integer sourceType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间.UTC时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.UTC时间")
    private Long modifyTime;

    @ApiModelProperty(value = "扩展字段")
    private String addition3;

    @ApiModelProperty(value = "扩展字段")
    private String addition4;

    @ApiModelProperty(value = "扩展字段")
    private String addition5;

    @ApiModelProperty(value = "附加字段")
    private String addition6;

    @ApiModelProperty(value = "附加字段")
    private String addition7;

    @ApiModelProperty(value = "附加字段")
    private String addition8;

    @ApiModelProperty(value = "附加字段")
    private String addition9;

    @ApiModelProperty(value = "附加字段")
    private String addition10;

    @ApiModelProperty(value = "扩展字段2")
    private String addition2;

    @ApiModelProperty(value = "下发数据标识：Y:已下发,N:未下发")
    private String sendData;

    @ApiModelProperty(value = "事件最新一次下发的linkId")
    private String linkId;


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

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getAddition1() {
        return addition1;
    }

    public void setAddition1(String addition1) {
        this.addition1 = addition1;
    }

    public String getHomeAway() {
        return homeAway;
    }

    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    public Integer getSecondNum() {
        return secondNum;
    }

    public void setSecondNum(Integer secondNum) {
        this.secondNum = secondNum;
    }

    public Integer getFirstT1() {
        return firstT1;
    }

    public void setFirstT1(Integer firstT1) {
        this.firstT1 = firstT1;
    }

    public Integer getFirstT2() {
        return firstT2;
    }

    public void setFirstT2(Integer firstT2) {
        this.firstT2 = firstT2;
    }

    public Integer getSecondT1() {
        return secondT1;
    }

    public void setSecondT1(Integer secondT1) {
        this.secondT1 = secondT1;
    }

    public Integer getSecondT2() {
        return secondT2;
    }

    public void setSecondT2(Integer secondT2) {
        this.secondT2 = secondT2;
    }

    public Integer getFirstNum() {
        return firstNum;
    }

    public void setFirstNum(Integer firstNum) {
        this.firstNum = firstNum;
    }

    public Long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(Long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public Long getSecondsFromStart() {
        return secondsFromStart;
    }

    public void setSecondsFromStart(Long secondsFromStart) {
        this.secondsFromStart = secondsFromStart;
    }

    public Long getPeriodRemainingSeconds() {
        return periodRemainingSeconds;
    }

    public void setPeriodRemainingSeconds(Long periodRemainingSeconds) {
        this.periodRemainingSeconds = periodRemainingSeconds;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getStandardTeamId() {
        return standardTeamId;
    }

    public void setStandardTeamId(Long standardTeamId) {
        this.standardTeamId = standardTeamId;
    }

    public Integer getT1() {
        return t1;
    }

    public void setT1(Integer t1) {
        this.t1 = t1;
    }

    public Integer getT2() {
        return t2;
    }

    public void setT2(Integer t2) {
        this.t2 = t2;
    }

    public String getThirdEventId() {
        return thirdEventId;
    }

    public void setThirdEventId(String thirdEventId) {
        this.thirdEventId = thirdEventId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public Long getThirdTeamId() {
        return thirdTeamId;
    }

    public void setThirdTeamId(Long thirdTeamId) {
        this.thirdTeamId = thirdTeamId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
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

    public String getAddition6() {
        return addition6;
    }

    public void setAddition6(String addition6) {
        this.addition6 = addition6;
    }

    public String getAddition7() {
        return addition7;
    }

    public void setAddition7(String addition7) {
        this.addition7 = addition7;
    }

    public String getAddition8() {
        return addition8;
    }

    public void setAddition8(String addition8) {
        this.addition8 = addition8;
    }

    public String getAddition9() {
        return addition9;
    }

    public void setAddition9(String addition9) {
        this.addition9 = addition9;
    }

    public String getAddition10() {
        return addition10;
    }

    public void setAddition10(String addition10) {
        this.addition10 = addition10;
    }

    public String getAddition2() {
        return addition2;
    }

    public void setAddition2(String addition2) {
        this.addition2 = addition2;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", canceled=").append(canceled);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", eventCode=").append(eventCode);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", extraInfo=").append(extraInfo);
        sb.append(", addition1=").append(addition1);
        sb.append(", homeAway=").append(homeAway);
        sb.append(", secondNum=").append(secondNum);
        sb.append(", firstT1=").append(firstT1);
        sb.append(", firstT2=").append(firstT2);
        sb.append(", secondT1=").append(secondT1);
        sb.append(", secondT2=").append(secondT2);
        sb.append(", firstNum=").append(firstNum);
        sb.append(", matchPeriodId=").append(matchPeriodId);
        sb.append(", player1Id=").append(player1Id);
        sb.append(", player1Name=").append(player1Name);
        sb.append(", player2Id=").append(player2Id);
        sb.append(", player2Name=").append(player2Name);
        sb.append(", secondsFromStart=").append(secondsFromStart);
        sb.append(", periodRemainingSeconds=").append(periodRemainingSeconds);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", standardTeamId=").append(standardTeamId);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", thirdEventId=").append(thirdEventId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", thirdTeamId=").append(thirdTeamId);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", addition6=").append(addition6);
        sb.append(", addition7=").append(addition7);
        sb.append(", addition8=").append(addition8);
        sb.append(", addition9=").append(addition9);
        sb.append(", addition10=").append(addition10);
        sb.append(", addition2=").append(addition2);
        sb.append(", sendData=").append(sendData);
        sb.append(", linkId=").append(linkId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}