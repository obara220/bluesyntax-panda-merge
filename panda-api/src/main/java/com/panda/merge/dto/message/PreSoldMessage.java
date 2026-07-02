package com.panda.merge.dto.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class PreSoldMessage implements Serializable {

    /**
     * 标准赛事Id
     */
    @NotNull(message="标准赛事id不能为空")
    private Long matchId;

    /**
     * 盘口类型. 1: 赛前盘; 0: 滚球盘.
     */
    @NotNull(message="盘口类型不能为空")
    private Integer marketType;

    /**
     * 操盘平台.
     */
    private String riskManagerCode;

    /**
     * 0:普通赛事、1：冠军赛事
     **/
    @NotNull(message="是否是冠军玩法")
    String isOutRight;

    /**
     * 标准盘口玩法
     */
    @NotNull(message="开售玩法列表不能为空")
    private List<Long> marketCategoryIds;

    public PreSoldMessage(){

    }

    public PreSoldMessage(Long matchId, Integer marketType, String riskManagerCode, List<Long> marketCategoryIds, String isOutRight) {
        this.matchId = matchId;
        this.marketType = marketType;
        this.riskManagerCode = riskManagerCode;
        this.marketCategoryIds = marketCategoryIds;
        this.isOutRight = isOutRight;
    }
}
