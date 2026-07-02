package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchScoresStandardChangeLog implements Serializable {
    private Long id;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchId;

    private Integer sportId;

    @ApiModelProperty(value = "转换前的比分")
    private Long beforeScoresId;

    @ApiModelProperty(value = "转换后的比分")
    private Long afterScoresId;

    @ApiModelProperty(value = "automatic,manual")
    private String changeType;

    @ApiModelProperty(value = "livedata中断")
    private String reason;

    @ApiModelProperty(value = "系统自动")
    private String changer;

    private Long modifyTime;

    private Long createTime;

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

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public Long getBeforeScoresId() {
        return beforeScoresId;
    }

    public void setBeforeScoresId(Long beforeScoresId) {
        this.beforeScoresId = beforeScoresId;
    }

    public Long getAfterScoresId() {
        return afterScoresId;
    }

    public void setAfterScoresId(Long afterScoresId) {
        this.afterScoresId = afterScoresId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getChanger() {
        return changer;
    }

    public void setChanger(String changer) {
        this.changer = changer;
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
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", sportId=").append(sportId);
        sb.append(", beforeScoresId=").append(beforeScoresId);
        sb.append(", afterScoresId=").append(afterScoresId);
        sb.append(", changeType=").append(changeType);
        sb.append(", reason=").append(reason);
        sb.append(", changer=").append(changer);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}