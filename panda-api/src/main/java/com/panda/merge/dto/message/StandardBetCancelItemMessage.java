/**
 *
 */
package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description  :  bet cancel
 * @author       :  Vito
 * @Date: 2020年1月16日 下午12:30:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardBetCancelItemMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准盘口ID
     */
    private Long marketId;

    /**
     * 取消原因
     */
    private String reason;

}
