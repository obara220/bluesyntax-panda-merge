package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 三方 消费数据源盘口提前结算信息
 *
 * @author Mirro
 * @Project Name :  panda_data_realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description:
 * @date 2019/10/4 16:07
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdMarketPreResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 第三方赛事原始id
     */
    private String thirdMatchId;

    /**
     * 第三方盘口原始id
     */
    private String thirdMarketId;

    /**
     * 三方玩法源id
     */
    private String thirdMarketCategorySourceId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 概率赛事阶段
     */
    private Integer matchPeriod;

    /**
     * 提前结算投注项信息
     */
    @NotNull(message = "提前结算投注项信息不能为空")
    private List<ThirdMarketOddsPreResultDTO> marketOddsResultList;


    /**
     * 盘口提前结算状态
     * 1 AVAILABLE (available for cashout)
     * -1 UNAVAILABLE (temporarily unavailable for cashout)
     * -2 CLOSED (permanently unavailable for cashout)
     */
    private Integer cashOutStatus;

    /**
     * 和局计算概率（只有存在平局可能性的两项盘才会存在这个数据）
     */
    private BigDecimal drawCalcProb;

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

    private Integer a01Verify;

    private Integer verifyG0;

    private Double htG0Left;

    private Double ftG0Left;

    private BigDecimal htScoreProb;

    private BigDecimal ftScoreProb;

}
