package com.panda.merge.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.dto
 * @Description: 体育种类查询参数对象
 * @date 2019/9/2 11:55
 * @ModificationHistory Who    When    What
 */
@Data
public class StandardSportTypeDTO implements Serializable {
    /**
     * 开始时间 ，utc时间戳
     */
	@NotNull(message = "开始时间不能为null!")
	private Long beginTime;
	/**
	 * 结束时间 ，utc时间戳
	 */
	private Long endTime;
}
