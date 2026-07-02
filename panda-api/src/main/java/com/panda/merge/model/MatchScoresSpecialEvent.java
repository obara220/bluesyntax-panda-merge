package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchScoresSpecialEvent implements Serializable {
    private Long id;

    private Long sportId;

    private String sourceEventId;

    private String dataSourceCode;

    private String dataSourceEventId;

    private Long thirdMatchId;

    @ApiModelProperty(value = "自定义事件code")
    private String pandaEventCode;

    private String homeaway;

    private Integer t1;

    private Integer t2;

    private Integer secondT1;

    private Integer secondT2;

    private Integer firstNum;

    private Integer secondNum;

    private String addition1;

    private String addition2;

    private String addition3;

    private String addition4;

    private String addition5;

    private String extrainfo;

    private String addition6;

    private Long eventTime;

    private Long createTime;

    private Long modifyTime;

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

    public String getSourceEventId() {
        return sourceEventId;
    }

    public void setSourceEventId(String sourceEventId) {
        this.sourceEventId = sourceEventId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getDataSourceEventId() {
        return dataSourceEventId;
    }

    public void setDataSourceEventId(String dataSourceEventId) {
        this.dataSourceEventId = dataSourceEventId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getPandaEventCode() {
        return pandaEventCode;
    }

    public void setPandaEventCode(String pandaEventCode) {
        this.pandaEventCode = pandaEventCode;
    }

    public String getHomeaway() {
        return homeaway;
    }

    public void setHomeaway(String homeaway) {
        this.homeaway = homeaway;
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

    public Integer getSecondNum() {
        return secondNum;
    }

    public void setSecondNum(Integer secondNum) {
        this.secondNum = secondNum;
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

    public String getExtrainfo() {
        return extrainfo;
    }

    public void setExtrainfo(String extrainfo) {
        this.extrainfo = extrainfo;
    }

    public String getAddition6() {
        return addition6;
    }

    public void setAddition6(String addition6) {
        this.addition6 = addition6;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
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
        sb.append(", sportId=").append(sportId);
        sb.append(", sourceEventId=").append(sourceEventId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", dataSourceEventId=").append(dataSourceEventId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", pandaEventCode=").append(pandaEventCode);
        sb.append(", homeaway=").append(homeaway);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", secondT1=").append(secondT1);
        sb.append(", secondT2=").append(secondT2);
        sb.append(", firstNum=").append(firstNum);
        sb.append(", secondNum=").append(secondNum);
        sb.append(", addition1=").append(addition1);
        sb.append(", addition2=").append(addition2);
        sb.append(", addition3=").append(addition3);
        sb.append(", addition4=").append(addition4);
        sb.append(", addition5=").append(addition5);
        sb.append(", extrainfo=").append(extrainfo);
        sb.append(", addition6=").append(addition6);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}