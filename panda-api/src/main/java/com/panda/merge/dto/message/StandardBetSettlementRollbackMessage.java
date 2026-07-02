/**
 *
 */
package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  Rollback Bet Settlement
 * @author       :  Vito
 * @Date: 2019年11月6日 下午2:22:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardBetSettlementRollbackMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息所属产品类型
     * 1=LiveOdds, 2=MTS, 3=BetradarCtrl, 4=Betpal, 5=premium cricket
     */
    private Integer product;

    /**
     * 发送消息时间
     */
    private Long sendTimestamp;

    /**
     * 标准赛事ID
     */
    private Long matchId;

    /**
     * 标准sportId
     */
    private Long sportId;

    /**
     * 盘口列表
     */
    private List<StandardBetSettlementRollbackItemMessage> markets;
}
