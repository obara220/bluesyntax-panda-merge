package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasketBallPopupSettleScoreDto extends AbstructMatchSettleDto{
    /**
     *  赛事比分ID
     * */
    private Long matchScoreId;
    /**
     * 赛事ID
     * */
    private Long standardMatchId;
    /**
     * 主队比分
     * */
    private Integer t1;
    /**
     * 客队比分
     * */
    private Integer t2;

    private String eventCode;
    /**
     * 结算比分序号
     * */
    private String settleNum;

    /**
     * 排序字段
     */
    private int sort;
}
