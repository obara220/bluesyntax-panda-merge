package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author :	idol
 * @description : 比分阶段(次序)冻结参数dto
 * @date: 2022-2-19 15:36:40
 * @ModificationHistory Who When What -------- ---------
 * --------------------------
 */
@Data
@ApiModel(description = "赛事冻结参数dto")
public class ScoresPeriodOrderFreezeDto  extends AbstructMatchSettleDto {

    //linkid
    private String LinkId;

    private Long sportId;


    /**
     * 操作对象赛事id
     */
    private Long matchId;


    /**
     * 结算比分编号
     */
    private String settleNum;

	/**
	 * 0. 未冻结   1. 冻结
	 */
	private Integer freezeStatus;

   //"操作对象编码"
    private String  eventCode;


    //"事件比分id"
    private String eventId;


    /**
     *     操作用户名称
     */
    private String operatorName;


    private String OperatorId;

    // 冻结分钟
    private Integer mins;

    // 冻结时间日期
    private Long freezeTime;
    // 冻结时间日期
    private Long createTime;
}