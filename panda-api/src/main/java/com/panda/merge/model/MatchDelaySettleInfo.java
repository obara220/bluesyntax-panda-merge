package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;

import java.io.Serializable;

public class MatchDelaySettleInfo implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "比分id")
    private Long scoreId;

    @ApiModelProperty(value = "match_check_info_id")
    private Long checkInfoId;

    @ApiModelProperty(value = "标准赛事Id")
    private Long standardMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "延迟结算时间")
    private Long delayTime;


    @ApiModelProperty(value = "延迟结算秒数")
    private Long delayTimeSecond;

    @ApiModelProperty(value = "延迟类型1比分 2事件")
    private Integer delayType;

    @ApiModelProperty(value = "是否已经结算0未结算 3已结算")
    private Integer settleStatus;

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



    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public Long getScoreId() {
        return scoreId;
    }
    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public Long getCheckInfoId() {
        return checkInfoId;
    }
    public void setCheckInfoId(Long checkInfoId) {
        this.checkInfoId = checkInfoId;
    }

    public Long getDelayTime() {
        return delayTime;
    }
    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }
    public void setDataSourceCode( String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }



    public Long getDelayTimeSecond() {
        return delayTimeSecond;
    }
    public void setDelayTimeSecond(Long delayTimeSecond) {
        this.delayTimeSecond = delayTimeSecond;
    }


    public Integer getDelayType(){
        return delayType;
    }

    public void setDelayType(Integer delayType){
        this.delayType = delayType;
    }

    public Integer getSettleStatus(){
        return  settleStatus;
    }

    public void setSettleStatus(Integer settleStatus){
        this.settleStatus=settleStatus;
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
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", scoreId=").append(scoreId);
        sb.append(", delayTime=").append(delayTime);
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", delayTimeSecond=").append(delayTimeSecond);
        sb.append(", delayType=").append(delayType);
        sb.append(", settleStatus=").append(settleStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}