package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author :	idol
 * @description : 赛事冻结参数dto
 * @date: 2022-2-19 15:36:40
 * @ModificationHistory Who When What -------- ---------
 * --------------------------
 */
@Data
@ApiModel(description = "赛事冻结参数dto")
public class MatchFreezeDto extends AbstructMatchSettleDto {

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
    // 冻结分钟
    private Integer mins;
    // 冻结时间日期
    private Long freezeTime;
    // 开始时间
    private Long createTime;
    @Override
    public String toString() {
        return "MatchFreezeDto{" +
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
				", mins=" + mins +
				", freezeTime=" + freezeTime +
				", createTime=" + createTime +
                '}';
    }
}