package com.panda.merge.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


/**
 * fymen
 * 用来接收自动开盘的信息的消息体
 * 2713需求，监听到自动开盘信息后，发送危险事件到风控
 */
@Data
@Slf4j
public class RcsMarketFootballStatusDTO extends Request<Object> implements Serializable {


    private Long aoMatchId;

    private Long id;

    private String linkId;
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 开关盘状态 0开盘
     */
    private Integer status;
    /**
     * 事件发生时间
     */
    private Long updateTime;

    /**
     * 比赛进行时长
     */
    private Long secondsFromStart;

//    {
//      "aoMatchId":444918957948354562,
//      "id":3675063,
//      "linkId":"AO_0af40b942024040714103696039f99d6",
//      "matchId":3675063,
//      "secondsFromStart":2845,
//      "status":1,
//      "updateTime":1712470236924
//      }
}
