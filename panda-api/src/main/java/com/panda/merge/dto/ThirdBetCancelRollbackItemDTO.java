/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description  :  TODO
 * @author       :  Vito
 * @Date: 2020年1月16日 下午12:30:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdBetCancelRollbackItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 三方盘口原始ID
     */
    private String thirdSourceMarketId;

}
