package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardMatchTeamRelation implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "标准球队id")
    private Long standardTeamId;

    @ApiModelProperty(value = "球队名称（中文）")
    private String teamName;

    @ApiModelProperty(value = "标准比赛id")
    private Long standardMatchId;

    @ApiModelProperty(value = "比赛中的作用.足球:主客队或者其他.home:主场队;away:客场队")
    private String matchPosition;

    @ApiModelProperty(value = "显示顺序.默认不使用")
    private Integer displayOrder;

    private String remark;

    private Long createTime;

    private Long modifyTime;

    @ApiModelProperty(value = "球队名称快照")
    private String teamNameRecord;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStandardTeamId() {
        return standardTeamId;
    }

    public void setStandardTeamId(Long standardTeamId) {
        this.standardTeamId = standardTeamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getMatchPosition() {
        return matchPosition;
    }

    public void setMatchPosition(String matchPosition) {
        this.matchPosition = matchPosition;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public String getTeamNameRecord() {
        return teamNameRecord;
    }

    public void setTeamNameRecord(String teamNameRecord) {
        this.teamNameRecord = teamNameRecord;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardTeamId=").append(standardTeamId);
        sb.append(", teamName=").append(teamName);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", matchPosition=").append(matchPosition);
        sb.append(", displayOrder=").append(displayOrder);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", teamNameRecord=").append(teamNameRecord);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}