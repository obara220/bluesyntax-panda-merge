package com.panda.merge.dto.settle;


import lombok.Data;

@Data
public class EditMatchSettleSPOddsDto extends AbstructMatchSettleDto {

    /**
     * 被编辑的投注项id
     * */
    private Long oddsId;
    /**
     * 标准赛事id
     * */
    private Long standardMatchId;
    /**
     * 赛果   2 走水 3输  4赢  7取消 
     * */
    private Integer settleResult;

    /**
     * 盘口id
     */
    private Long marketId;


}
