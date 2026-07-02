package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author raulvii
 * @Description  :  冠军操盘-手动自动切换DTO
 */
@Data
public class OutrightTradeTypeConfigDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 配置生效级别: 0:盘口级 1:赛事级
	 */
	private Integer level;

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
	 * 操盘类型: 0:自动操盘,1:手动操盘
	 */
	@NotNull(message = "操盘类型不能为空")
	private Integer tradeType;

	/**
	 * 配置修改人
	 */
	private Long operaterId;

	/**
	 * 配置修改时间
	 */
	private Long modifyTime;

}
