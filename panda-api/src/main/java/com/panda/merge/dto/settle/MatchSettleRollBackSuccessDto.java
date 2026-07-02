package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchSettleRollBackSuccessDto implements Serializable {

    private String matchId;

    private Long sportId;

    /**
     * 二次结算原因 操作人id
     */
    private String optId;

    /**
     * 二次结算原因 操作人名字
     */
    private String optUser;

    /**
     * 事件回滚id.
     */
    private Long evenRollBackId;

    /**
     * 总订单数.
     */
    private Integer betTotal;

    /**
     * 已回滚的订单数.
     */
    private Integer rollBackBetTotal;

    private String linkId;
}
