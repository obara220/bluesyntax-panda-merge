package com.panda.merge.model;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class MatchSettleDataSourceSwitch extends AbstructMatchSettleDto implements Serializable {

    private Long id;

    @ApiModelProperty(value = "球种")
    private Long sportId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "进球开关")
    private Integer goal;

    @ApiModelProperty(value = "角球开关")
    private Integer corner;
    @ApiModelProperty(value = "罚牌开关")
    private Integer booking;
    @ApiModelProperty(value = "灰色区间开关")
    private Integer gray;
    @ApiModelProperty(value = "权重上限开关")
    private Integer topWeight;

    @ApiModelProperty(value = "数据商心跳开关")
    private Integer dataSourceHeartbeat;

    @ApiModelProperty(value = "单数据源结算开关")
    private Integer singleDataSourceSettle;

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

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Integer getCorner() {
        return corner;
    }

    public void setCorner(Integer corner) {
        this.corner = corner;
    }

    public Integer getBooking() {
        return booking;
    }

    public void setBooking(Integer booking) {
        this.booking = booking;
    }

    public Integer getGray() {
        return gray;
    }

    public void setGray(Integer gray) {
        this.gray = gray;
    }

    public Integer getTopWeight() {
        return topWeight;
    }

    public void setTopWeight(Integer topWeight) {
        this.topWeight = topWeight;
    }

    public Integer getDataSourceHeartbeat() {
        return dataSourceHeartbeat;
    }

    public void setDataSourceHeartbeat(Integer dataSourceHeartbeat) {
        this.dataSourceHeartbeat = dataSourceHeartbeat;
    }

    public Integer getSingleDataSourceSettle() {
        return singleDataSourceSettle;
    }

    public void setSingleDataSourceSettle(Integer singleDataSourceSettle) {
        this.singleDataSourceSettle = singleDataSourceSettle;
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
        sb.append(", dataSourceCode=").append(dataSourceCode);
        sb.append(", goal=").append(goal);
        sb.append(", corner=").append(corner);
        sb.append(", booking=").append(booking);
        sb.append(", gray=").append(gray);
        sb.append(", topWeight=").append(topWeight);
        sb.append(", dataSourceHeartbeat=").append(dataSourceHeartbeat);
        sb.append(", singleDataSourceSettle=").append(singleDataSourceSettle);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}