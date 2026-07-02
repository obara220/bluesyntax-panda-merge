/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description : 滚动操盘-自动水差配置项
 */
@Data
public class TradeMarketAutoDiffConfigItemDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 玩法ID
	 */
    @NotNull(message = "玩法id不能为空")
	private Long marketCategoryId;

	/**
	 * 盘口ID
	 */
	@NotNull(message = "盘口id不能为空")
	private Long marketId;

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
