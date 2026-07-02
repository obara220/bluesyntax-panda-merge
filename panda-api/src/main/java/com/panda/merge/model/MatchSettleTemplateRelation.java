package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchSettleTemplateRelation implements Serializable {
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "结算权重模版id")
    private Long templateSettleWeightId;

    @ApiModelProperty(value = "结算延迟模版id")
    private Long templateCountDowenId;

    @ApiModelProperty(value = "灰色区间设置模版id")
    private Long templateGrayAreaId;

    @ApiModelProperty(value = "标准联赛id")
    private Long standardTournamentId;

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

    public Long getTemplateSettleWeightId() {
        return templateSettleWeightId;
    }

    public void setTemplateSettleWeightId(Long templateSettleWeightId) {
        this.templateSettleWeightId = templateSettleWeightId;
    }

    public Long getTemplateCountDowenId() {
        return templateCountDowenId;
    }

    public void setTemplateCountDowenId(Long templateCountDowenId) {
        this.templateCountDowenId = templateCountDowenId;
    }

    public Long getTemplateGrayAreaId() {
        return templateGrayAreaId;
    }

    public void setTemplateGrayAreaId(Long templateGrayAreaId) {
        this.templateGrayAreaId = templateGrayAreaId;
    }

    public Long getStandardTournamentId() {
        return standardTournamentId;
    }

    public void setStandardTournamentId(Long standardTournamentId) {
        this.standardTournamentId = standardTournamentId;
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
        sb.append(", templateSettleWeightId=").append(templateSettleWeightId);
        sb.append(", templateCountDowenId=").append(templateCountDowenId);
        sb.append(", templateGrayAreaId=").append(templateGrayAreaId);
        sb.append(", standardTournamentId=").append(standardTournamentId);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}