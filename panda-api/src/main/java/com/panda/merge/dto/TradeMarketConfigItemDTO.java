/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description  :  操盘盘口参数配置类
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TradeMarketConfigItemDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 玩法ID
	 */
	@NotNull(message = "玩法id不能为空")
	private Long marketCategoryId;

	/**
	 * 盘口ID
	 */
	@Deprecated
	private Long marketId;

	/**
	 * 配置项：最小赔率值
	 */
	private Double minOddsValue;

	/**
	 * 配置项：最大小大赔率值
	 */
	private Double maxOddsValue;


	/**
	 * 子玩法ID
	 */
	private Long childStandardCategoryId;
}
