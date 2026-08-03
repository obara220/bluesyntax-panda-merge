package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author raulvii
 * @Description  :  冠军操盘-盘口状态DTO
 */
@Data
public class OutrightTradeMarketConfigDTO implements Serializable {

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
	 * 盘口状态
	 */
	@NotNull(message = "盘口状态不能为空")
	private Integer marketStatus;

	/**
	 * 配置修改人
	 */
	private Long operaterId;

	/**
	 * 配置类型
	 */
	private Integer operateType;

	/**
	 * 配置修改时间
	 */
	private Long modifyTime;

}
