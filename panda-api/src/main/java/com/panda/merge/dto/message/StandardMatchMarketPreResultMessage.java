package com.panda.merge.dto.message;

import com.panda.merge.common.enums.MatchStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 标准 提前结算盘口信息
 */
@Getter
@Setter
public class StandardMatchMarketPreResultMessage implements Serializable {

    /**
     * 标准盘口id
     */
    private Long id;

    /**
     * 标准玩法id
     */
    private Long marketCategoryId;

    /**
     * 第三方赛事原始id
     */
    private String thirdMatchId;

    /**
     * 第三方盘口ID
     */
    private String thirdMarketId;

    /**
     * 盘口提前结算状态
     * 1 AVAILABLE (available for cashout)
     * -1 UNAVAILABLE (temporarily unavailable for cashout)
     * -2 CLOSED (permanently unavailable for cashout)
     */
    private Integer cashOutStatus;
    /**
     * 第三方数据商下发数据时间
     */
    private Long thirdSportSendTime;
    /**
     * 和局计算概率（只有存在平局可能性的两项盘才会存在这个数据）
     */
    private BigDecimal drawCalcProb;
    /**
     * 赛事级别提前结算开关 1开 ，0关 业务用
     */
    private Integer matchPreStatus = 0 ;
    /**
     * 赛事级别提前结算开关 1开 ，0关 风控用
     */
    private Integer matchPreStatusRisk = 0 ;
    /**
     * 概率赛事阶段 默认 0
     */
    private Integer matchPeriod = MatchStatusEnum.Not_Started.value;

    /**
     * 玩法级别提前结算开关 1开 0关
     */
    private Integer categoryPreStatus = 0;
    /**
     * spread
     */
    private Double spread = 0D ;
    /**
     * cashOut Margin
     */
    private BigDecimal cashOutMargin = BigDecimal.ZERO;
    /**
     * 盘口状态，默认关
     */
    private Integer status = 2 ;
    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;
    /**
     * 附加字段1
     */
    private String addition1;
    /**
     * 附加字段2
     */
    private String addition2;
    /**
     * 附加字段3
     */
    private String addition3;
    /**
     * 附加字段4
     */
    private String addition4;
    /**
     * 附加字段5
     */
    private String addition5;
    /**
     * 提前结算投注项信息
     */
    private List<StandardMatchMarketOddsPreResultMessage> marketOddsPreResultMessages;

    private Integer a01Verify;

    private Integer verifyG0;

    private Double htG0Left;

    private Double ftG0Left;

    private BigDecimal htScoreProb;

    private BigDecimal ftScoreProb;
}
