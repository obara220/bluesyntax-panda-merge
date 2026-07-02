package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description : 滚动操盘-自动水差配置项（坑位）
 */
@Data
public class TradePlaceNumAutoDiffConfigItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 玩法ID
     */
    @NotNull(message = "玩法id不能为空")
    private Long marketCategoryId;

    /**
     * 盘口ID
     */
    @NotNull(message = "坑位不能为空")
    private Integer placeNum;

    /**
     * 投注项类型
     */
    private String oddType;

    /**
     * 水差值
     */
    @NotNull(message = "水差值不能为空")
    private Double diffValue;

    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;
}
