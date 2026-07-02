package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author
 * @Project Name : panda_data_nonrealtime
 * @Package Name : com.panda.sport.data.nonrealtime.api.query.dto
 * @Description: 体育标准赛果查询参数对象
 * @date
 * @ModificationHistory Who When What
 */
@Data
public class StandardMatchResultDTO implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -4133052631370152590L;
	/**
	 * 标准赛事ID
	 */
	@NotNull(message = "标准赛事ID不能为null!")
	private Long standardMatchId;
	/**
	 * 赛事阶段ID
	 */
	@NotNull(message = "赛事阶段ID不能为null!")
	private Long matchPeriodId;
	/**
	 * 局号
	 */
	private Integer firstNumber;
	/**
	 * 盘号
	 */
	private Integer secondNumber;
}
