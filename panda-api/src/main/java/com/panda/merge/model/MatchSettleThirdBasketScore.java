package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleThirdBasketScore implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "三方赛事id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "主队总分")
    private Integer t1;

    @ApiModelProperty(value = "客队总分")
    private Integer t2;

    @ApiModelProperty(value = "当前半场主队比分")
    private Integer firstT1;

    @ApiModelProperty(value = "当前半场客队比分")
    private Integer firstT2;

    @ApiModelProperty(value = "当前节主队比分")
    private Integer secondT1;

    @ApiModelProperty(value = "当前节客队比分")
    private Integer secondT2;

    @ApiModelProperty(value = "追踪link")
    private String linkId;

    @ApiModelProperty(value = "总分和")
    private Integer sumScore;

    @ApiModelProperty(value = "结算总分")
    private Integer settleSumScore;

    @ApiModelProperty(value = "阶段")
    private Long periodId;

    @ApiModelProperty(value = "当前进行时长(篮球倒计时)")
    private Integer secondFromStart;

    @ApiModelProperty(value = "比分的发生时间")
    private Long eventTime;

    @ApiModelProperty(value = "三方赛事事件id")
    private String thirdEventId;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

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

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
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

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Integer getSumScore() {
        return sumScore;
    }

    public void setSumScore(Integer sumScore) {
        this.sumScore = sumScore;
    }

    public Integer getSettleSumScore() {
        return settleSumScore;
    }

    public void setSettleSumScore(Integer settleSumScore) {
        this.settleSumScore = settleSumScore;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public Integer getSecondFromStart() {
        return secondFromStart;
    }

    public void setSecondFromStart(Integer secondFromStart) {
        this.secondFromStart = secondFromStart;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getThirdEventId() {
        return thirdEventId;
    }

    public void setThirdEventId(String thirdEventId) {
        this.thirdEventId = thirdEventId;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sportId=").append(sportId);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", firstT1=").append(firstT1);
        sb.append(", firstT2=").append(firstT2);
        sb.append(", secondT1=").append(secondT1);
        sb.append(", secondT2=").append(secondT2);
        sb.append(", linkId=").append(linkId);
        sb.append(", sumScore=").append(sumScore);
        sb.append(", settleSumScore=").append(settleSumScore);
        sb.append(", periodId=").append(periodId);
        sb.append(", secondFromStart=").append(secondFromStart);
        sb.append(", eventTime=").append(eventTime);
        sb.append(", thirdEventId=").append(thirdEventId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}