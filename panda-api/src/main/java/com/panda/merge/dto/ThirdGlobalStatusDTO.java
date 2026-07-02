/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description  :  数据接入模块状态服务
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdGlobalStatusDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 数据来源标识
	 */
	@NotEmpty(message = "数据源不能为空")
	private String dataSourceCode;

	/**
	 * 服务状态：可选值 UP, DOWN
	 * UP：服务正常
	 * DOWN：服务异常
	 */
	@NotEmpty(message = "服务状态不能为空")
	private String status;

	/**
	 * 数据源事件产生时间
	 */
	@NotNull(message = "事件产生时间不能为null")
	@Min(value = 0, message = "必须为正数")
	private Long sourceTimestamp;

	/**
	 * 数据接入模块发送消息时间
	 */
	@NotNull(message = "发送消息时间不能为null")
	@Min(value = 0, message = "必须为正数")
	private Long sendTimestamp;
}
