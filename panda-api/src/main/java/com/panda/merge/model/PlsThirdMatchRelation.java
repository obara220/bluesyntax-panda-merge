package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class PlsThirdMatchRelation implements Serializable {
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "球种ID")
    private Long sportId;

    @ApiModelProperty(value = "PLS标准赛事ID")
    private Long plsStandardMatchId;

    @ApiModelProperty(value = "PLS标准赛事管理ID")
    private String plsMatchManageId;

    @ApiModelProperty(value = "三方赛事ID")
    private Long thirdMatchId;

    @ApiModelProperty(value = "三方赛事源ID")
    private String thirdMatchSourceId;

    @ApiModelProperty(value = "PLS标准赛事已下发到赛程后台生成标准1是0否")
    private Integer isStandardFlag;

    @ApiModelProperty(value = "赛程后台对应的标准赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "标准赛事管理ID")
    private String matchManageId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
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

    public Long getPlsStandardMatchId() {
        return plsStandardMatchId;
    }

    public void setPlsStandardMatchId(Long plsStandardMatchId) {
        this.plsStandardMatchId = plsStandardMatchId;
    }

    public String getPlsMatchManageId() {
        return plsMatchManageId;
    }

    public void setPlsMatchManageId(String plsMatchManageId) {
        this.plsMatchManageId = plsMatchManageId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public String getThirdMatchSourceId() {
        return thirdMatchSourceId;
    }

    public void setThirdMatchSourceId(String thirdMatchSourceId) {
        this.thirdMatchSourceId = thirdMatchSourceId;
    }

    public Integer getIsStandardFlag() {
        return isStandardFlag;
    }

    public void setIsStandardFlag(Integer isStandardFlag) {
        this.isStandardFlag = isStandardFlag;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getMatchManageId() {
        return matchManageId;
    }

    public void setMatchManageId(String matchManageId) {
        this.matchManageId = matchManageId;
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
        sb.append(", plsStandardMatchId=").append(plsStandardMatchId);
        sb.append(", plsMatchManageId=").append(plsMatchManageId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", thirdMatchSourceId=").append(thirdMatchSourceId);
        sb.append(", isStandardFlag=").append(isStandardFlag);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", matchManageId=").append(matchManageId);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}