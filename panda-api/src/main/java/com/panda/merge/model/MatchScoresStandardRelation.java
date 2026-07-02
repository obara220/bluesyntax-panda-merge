package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchScoresStandardRelation implements Serializable {
    private Long id;

    private Long standardMatchId;

    private Long matchScoresInfoId;

    private String dataSourceCode;

    private Long thirdMatchId;

    private String type;

    private Integer standardActive;

    private String activeType;

    private String activeMode;

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

    public Long getMatchScoresInfoId() {
        return matchScoresInfoId;
    }

    public void setMatchScoresInfoId(Long matchScoresInfoId) {
        this.matchScoresInfoId = matchScoresInfoId;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStandardActive() {
        return standardActive;
    }

    public void setStandardActive(Integer standardActive) {
        this.standardActive = standardActive;
    }

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public String getActiveMode() {
        return activeMode;
    }

    public void setActiveMode(String activeMode) {
        this.activeMode = activeMode;
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
        sb.append(", matchScoresInfoId=").append(matchScoresInfoId);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", type=").append(type);
        sb.append(", standardActive=").append(standardActive);
        sb.append(", activeType=").append(activeType);
        sb.append(", activeMode=").append(activeMode);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}