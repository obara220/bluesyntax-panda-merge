package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class TradeClearDiffValueDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 需要清理的运动类型
     */
    @NotNull(message = "运动类型不能为空")
    private Integer sportId;
    /**
     * 需要清理的标准赛事id
     */
    @NotNull(message = "标准赛事id不能为空")
    private Long standardMatchId;
    /**
     * 需要清理的玩法集合
     */
    @NotNull(message = "玩法集合不能为空")
    private List<Long> categoryList;
}
