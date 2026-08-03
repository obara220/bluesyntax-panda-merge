package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.dto
 * @Description :  TODO
 * @Date: 2020-10-03 10:19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TradeMarketHeadGapConfigDTO implements Serializable {

    private static final long serialVersionUID = 6952054112161012798L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;

    /**
     * 标准玩法ID
     */
    private Long standardCategoryId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 盘口差
     */
    private Double marketHeadGap;

    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;
    /**
     * 盘口差最初值
     */
    private Double marketHeadGapInitial;

}
