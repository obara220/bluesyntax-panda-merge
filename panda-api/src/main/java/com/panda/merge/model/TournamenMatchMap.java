package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class TournamenMatchMap implements Serializable {
    @ApiModelProperty(value = "数据库维护,自增")
    private Long id;

    @ApiModelProperty(value = "0:联赛;1:比赛;")
    private Long type;

    @ApiModelProperty(value = "映射关系中的源id.第三方数据的ID")
    private Long sourceId;

    @ApiModelProperty(value = "映射关系中的目标id.标准数据的ID")
    private Long targetId;

    @ApiModelProperty(value = "主客队相反标记0否,1是")
    private Integer homeAwayOpposite;

    private String remark;

    private Long createTime;

    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Integer getHomeAwayOpposite() {
        return homeAwayOpposite;
    }

    public void setHomeAwayOpposite(Integer homeAwayOpposite) {
        this.homeAwayOpposite = homeAwayOpposite;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", sourceId=").append(sourceId);
        sb.append(", targetId=").append(targetId);
        sb.append(", homeAwayOpposite=").append(homeAwayOpposite);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}