package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author :  Horus
 * @Description :  TODO
 * @Date: 2019/9/23 19:30
 * @ModificationHistory Who    What   When
 * --------  ---------  --------------------------
 */
@Data
public class StandardSportRegionDTO implements Serializable {

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
