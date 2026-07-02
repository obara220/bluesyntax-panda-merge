package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author raulvii
 * @Description  :  冠军操盘-概率DTO
 */
@Data
public class OutrightTradeProbabilityConfigDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标准赛事id
	 */
	@NotNull(message = "标准赛事id不能为空")
	private Long standardMatchId;

	/**
	 * 标准盘口id
	 */
	@NotNull(message = "标准盘口id不能为空")
	private Long standardMarketId;

	/**
	 * 标准投注项id
	 */
	@NotNull(message = "标准投注项id不能为空")
	private Long standardMarketOddsId;

	/**
	 * 概率差
	 */
	@NotNull(message = "概率差不能为空")
	private Double probability;

	/**
	 * 配置修改人
	 */
	private Long operaterId;

	/**
	 * 配置修改时间
	 */
	private Long modifyTime;

}
