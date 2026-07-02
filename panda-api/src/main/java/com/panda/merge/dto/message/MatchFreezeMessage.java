package com.panda.merge.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author :	idol
 * @description : 赛事冻结参数dto
 * @date: 2022-5-14 14:11:43
 * @ModificationHistory Who When What -------- ---------
 * --------------------------
 */
@Data
@ApiModel(description = "赛事冻结参数dto")
public class MatchFreezeMessage implements Serializable {

    //linkid
    private String LinkId;

    private Long sportId;


    /**
     * 操作对象赛事id
     */
    private Long matchId;

    /**
     * 0. 未冻结   1. 冻结
     */
    private Integer freezeSettleStatus;

    /**
     * 操作用户名称
     */
    private String operatorName;

    /**
     * 操作用户名称
     */
    private String operatorId;
	/**
	 * 操作事件编码
	 */
	private String  eventCode;
	/**
	 * 事件比分id
	 */
	private String eventId;


    private String settleNum;

    // 事件次序
    private Integer eventOrder;

    @ApiModelProperty(value = "操作级别: 1赛事,2玩法级,3比分阶段级")
    private Integer level;

    @ApiModelProperty(value = "玩法级,1进球,2角球,3罚牌")
    private Integer playCategory;
    // 冻结分钟
    private Integer mins;
    // 冻结时间日期
    private Long freezeTime;
    // 创建时间日期
    private Long createTime;

    @Override
    public String toString() {
        return "MatchFreezeMessage{" +
                "LinkId='" + LinkId + '\'' +
                ", sportId=" + sportId +
                ", matchId=" + matchId +
                ", freezeSettleStatus=" + freezeSettleStatus +
                ", operatorName='" + operatorName + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", eventCode='" + eventCode + '\'' +
                ", eventId='" + eventId + '\'' +
                ", settleNum='" + settleNum + '\'' +
                ", eventOrder=" + eventOrder +
                ", level=" + level +
                ", playCategory=" + playCategory +
                ", mins=" + mins +
                ", freezeTime=" + freezeTime +
                ", createTime=" + createTime +
                '}';
    }
}