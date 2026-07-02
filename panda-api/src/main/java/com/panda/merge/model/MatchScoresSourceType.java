package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchScoresSourceType implements Serializable {
    private Long id;

    @ApiModelProperty(value = "1.livedata2uof")
    private String sourceType;

    @ApiModelProperty(value = "1.自动2.手动")
    private Integer activeType;

    @ApiModelProperty(value = "是否支持自动切换1.只自动切换2.不自动切换")
    private Integer activeMode;

    private Long thirdMatchId;

    private Long modifyTime;

    private Long createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getActiveType() {
        return activeType;
    }

    public void setActiveType(Integer activeType) {
        this.activeType = activeType;
    }

    public Integer getActiveMode() {
        return activeMode;
    }

    public void setActiveMode(Integer activeMode) {
        this.activeMode = activeMode;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
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
        sb.append(", sourceType=").append(sourceType);
        sb.append(", activeType=").append(activeType);
        sb.append(", activeMode=").append(activeMode);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}