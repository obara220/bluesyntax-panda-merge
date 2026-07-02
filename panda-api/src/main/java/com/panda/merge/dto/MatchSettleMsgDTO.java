package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Kepa
 * @Date 2021/8/31 15:52
 * @Version 1.0
 * 给上游接入发消息
 * 当赛事中已存在所有订单已结算时发消息
 * 当订单有取消时发送消息
 * 当订单有结算回滚时发送
 */
@Data
public class MatchSettleMsgDTO implements Serializable {

    private static final long serialVersionUID = -2951045180023891548L;

    /*** 发送时间戳 ***/
    private Long sendTime;

    /*** 操作类型 1：已结算；2：未结算 ***/
    private Integer operateType;

    /*** 赛事id***/
    private Long matchId;

    /*** 盘口id***/
    private Long marketId;

    /*** 原因***/
    private String reason;

    /*** 操作来源 ***/
    private String operateSource;

    /*** linkid ***/
    private String linkId;

}
