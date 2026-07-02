package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleResult implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "结算2.0的结算事件或者结算比分id")
    private Long settleScoreEventId;

    @ApiModelProperty(value = "1.阶段比分结算2.次序事件阶段")
    private Integer scoresType;

    @ApiModelProperty(value = "体育种类id.对应standard_sport_type.id")
    private Long sportId;

    @ApiModelProperty(value = "比分编码，对应业务的S10001之类的")
    private String scoreCode;

    @ApiModelProperty(value = "标准赛事的id.对应standard_match_info.id")
    private Long standardMatchId;

    @ApiModelProperty(value = "主队数量")
    private Integer t1;

    @ApiModelProperty(value = "客队数量")
    private Integer t2;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "结算时间")
    private Long settleTime;

    @ApiModelProperty(value = "创建时间.UTC时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.UTC时间")
    private Long modifyTime;

    @ApiModelProperty(value = "事件最新一次下发的linkId")
    private String linkId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSettleScoreEventId() {
        return settleScoreEventId;
    }

    public void setSettleScoreEventId(Long settleScoreEventId) {
        this.settleScoreEventId = settleScoreEventId;
    }

    public Integer getScoresType() {
        return scoresType;
    }

    public void setScoresType(Integer scoresType) {
        this.scoresType = scoresType;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getScoreCode() {
        return scoreCode;
    }

    public void setScoreCode(String scoreCode) {
        this.scoreCode = scoreCode;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Long settleTime) {
        this.settleTime = settleTime;
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
        sb.append(", settleScoreEventId=").append(settleScoreEventId);
        sb.append(", scoresType=").append(scoresType);
        sb.append(", sportId=").append(sportId);
        sb.append(", scoreCode=").append(scoreCode);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", t1=").append(t1);
        sb.append(", t2=").append(t2);
        sb.append(", remark=").append(remark);
        sb.append(", settleTime=").append(settleTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", linkId=").append(linkId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}