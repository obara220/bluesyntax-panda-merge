/**
 *
 */
package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description  :  Rollback Bet Settlement
 * @author       :  Vito
 * @Date: 2020年1月16日 下午12:30:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardBetSettlementRollbackItemMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准盘口ID
     */
    private Long marketId;

}
