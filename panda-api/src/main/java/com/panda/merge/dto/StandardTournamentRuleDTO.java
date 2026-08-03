package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class StandardTournamentRuleDTO implements Serializable {

    /**
     * 开始时间 ，utc时间戳
     */
	@NotNull(message = "开始时间不能为null!")
	private Long beginTime;

	private Long endTime;

	private String dataSourceCode;
}

