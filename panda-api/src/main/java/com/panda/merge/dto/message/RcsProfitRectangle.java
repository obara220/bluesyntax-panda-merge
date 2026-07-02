/**
 *
 */
package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description  :  通知风控清除水差
 * @author       :  bevan
 * @Date: 2020年10月30日15:10:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsProfitRectangle implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 玩法ID
     */
    private Long playId;

}
