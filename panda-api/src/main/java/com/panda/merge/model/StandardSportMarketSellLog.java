package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class StandardSportMarketSellLog implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "盘口开售id")
    private Long standardSportMarketSellId;

    @ApiModelProperty(value = "赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "日志")
    private String log;

    @ApiModelProperty(value = "日志英文")
    private String logEn;

    @ApiModelProperty(value = "操作员id")
    private String operateId;

    @ApiModelProperty(value = "操作员名")
    private String operateName;

    @ApiModelProperty(value = "操作员类型all:全部、pre_match:赛前操盘手、live_odd:滚球操盘手、match_result:赛果审核员")
    private String operateType;

    @ApiModelProperty(value = "操作时间")
    private Long operateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getStandardSportMarketSellId() {
        return standardSportMarketSellId;
    }

    public void setStandardSportMarketSellId(Long standardSportMarketSellId) {
        this.standardSportMarketSellId = standardSportMarketSellId;
    }

    public Long getStandardMatchId() {
        return standardMatchId;
    }

    public void setStandardMatchId(Long standardMatchId) {
        this.standardMatchId = standardMatchId;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLogEn() {
        return logEn;
    }

    public void setLogEn(String logEn) {
        this.logEn = logEn;
    }

    public String getOperateId() {
        return operateId;
    }

    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", standardSportMarketSellId=").append(standardSportMarketSellId);
        sb.append(", standardMatchId=").append(standardMatchId);
        sb.append(", log=").append(log);
        sb.append(", operateId=").append(operateId);
        sb.append(", operateName=").append(operateName);
        sb.append(", operateType=").append(operateType);
        sb.append(", operateTime=").append(operateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}