package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 冠军操盘-清概率差DTO
 *
 * @author raulvii
 */
@Data
public class TradeClearProbabilityValueDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 需要清理的标准赛事id
     */
    @NotNull(message = "标准赛事id不能为空")
    private Long standardMatchId;

    /**
     * 需要清理的标准赛事id
     */
    @NotNull(message = "标准盘口id不能为空")
    private Long standardMarketId;
    /**
     * 需要清理的玩法集合
     */
    @NotNull(message = "投注项id集合不能为空")
    private List<Long> oddsIdList;
}
