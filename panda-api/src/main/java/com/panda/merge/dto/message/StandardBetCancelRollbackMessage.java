/**
 *
 */
package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  rollback bet cancel
 * @author       :  Vito
 * @Date: 2019年11月6日 下午2:22:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardBetCancelRollbackMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息所属产品类型
     * 1=LiveOdds, 2=MTS, 3=BetradarCtrl, 4=Betpal, 5=premium cricket
     */
    private Integer product;

    /**
     * 开始时间：若为0表示无开始时间
     */
    private Long startTime;

    /**
     * 结束时间：若为0表示无结束时间
     */
    private Long endTime;

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
    private List<StandardBetCancelRollbackItemMessage> markets;
}
