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
public class ThirdBetCancelItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 三方盘口原始ID
     */
    private String thirdSourceMarketId;

    /**
     * 取消原因id
     */
    private String reason;
    /**
     * 取消原因描述（EN）
     */
    private String reasonStr;

}
