package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardSportMarketSellMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标准赛事ID*/
    private Long standardMatchId;

    /** 赛事管理id*/
    private String matchManageId;

    /** 赛前操盘平台如：SR*/
    private String preRiskManagerCode;

    /** 滚球操盘平台如：SR、MTS*/
    private String liveRiskManagerCode;

}