package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class MatchAutoAssociation implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器匹配每组赛事数量,冗余字段")
    private Integer matchNum;

    @ApiModelProperty(value = "运动种类id.对应sport.id")
    private Long sportId;

    @ApiModelProperty(value = "第三方赛事ID，这一组匹配时计算的依据赛事")
    private Long thirdMatchId;

    @ApiModelProperty(value = "同一组内,优先级最高数据源的比赛开始时间.UTC时间")
    private Long maxBeginTime;

    @ApiModelProperty(value = "审核状态,0初始化;1审核通过;2审核不通过;3部分审核通过")
    private Integer status;

    @ApiModelProperty(value = "商业数据源匹配状态0初始化;1审核通过;2审核不通过;3部分审核通过")
    private Integer commerceStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间.UTC时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.UTC时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMatchNum() {
        return matchNum;
    }

    public void setMatchNum(Integer matchNum) {
        this.matchNum = matchNum;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getThirdMatchId() {
        return thirdMatchId;
    }

    public void setThirdMatchId(Long thirdMatchId) {
        this.thirdMatchId = thirdMatchId;
    }

    public Long getMaxBeginTime() {
        return maxBeginTime;
    }

    public void setMaxBeginTime(Long maxBeginTime) {
        this.maxBeginTime = maxBeginTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCommerceStatus() {
        return commerceStatus;
    }

    public void setCommerceStatus(Integer commerceStatus) {
        this.commerceStatus = commerceStatus;
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
        sb.append(", matchNum=").append(matchNum);
        sb.append(", sportId=").append(sportId);
        sb.append(", thirdMatchId=").append(thirdMatchId);
        sb.append(", maxBeginTime=").append(maxBeginTime);
        sb.append(", status=").append(status);
        sb.append(", commerceStatus=").append(commerceStatus);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}