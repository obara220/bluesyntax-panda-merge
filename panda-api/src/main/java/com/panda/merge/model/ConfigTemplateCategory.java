package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigTemplateCategory implements Serializable {
    private Long id;

    @ApiModelProperty(value = "模板id")
    private Long templateId;

    @ApiModelProperty(value = "标准玩法ID")
    private Long standardCategoryId;

    @ApiModelProperty(value = "投注类型.my:马来盘;eu:欧盘")
    private String marketOddsType;

    @ApiModelProperty(value = "比赛阶段id")
    private Long matchPeriodId;

    @ApiModelProperty(value = "比赛进程时间")
    private Long matchProgressTime;

    @ApiModelProperty(value = "补时时间")
    private Long injuryTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "是否被取消.1被取消;0:没有被取消")
    private Integer canceled;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getStandardCategoryId() {
        return standardCategoryId;
    }

    public void setStandardCategoryId(Long standardCategoryId) {
        this.standardCategoryId = standardCategoryId;
    }

    public String getMarketOddsType() {
        return marketOddsType;
    }

    public void setMarketOddsType(String marketOddsType) {
        this.marketOddsType = marketOddsType;
    }

    public Long getMatchPeriodId() {
        return matchPeriodId;
    }

    public void setMatchPeriodId(Long matchPeriodId) {
        this.matchPeriodId = matchPeriodId;
    }

    public Long getMatchProgressTime() {
        return matchProgressTime;
    }

    public void setMatchProgressTime(Long matchProgressTime) {
        this.matchProgressTime = matchProgressTime;
    }

    public Long getInjuryTime() {
        return injuryTime;
    }

    public void setInjuryTime(Long injuryTime) {
        this.injuryTime = injuryTime;
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

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", templateId=").append(templateId);
        sb.append(", standardCategoryId=").append(standardCategoryId);
        sb.append(", marketOddsType=").append(marketOddsType);
        sb.append(", matchPeriodId=").append(matchPeriodId);
        sb.append(", matchProgressTime=").append(matchProgressTime);
        sb.append(", injuryTime=").append(injuryTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", canceled=").append(canceled);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}