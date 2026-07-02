package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.dto
 * @Description: 赛事分页查询参数对象
 * @date 2019/9/2 16:12
 * @ModificationHistory Who    When    What
 */
@Data
public class StandardMatchInfoDTO implements Serializable {

    /** 开始utc时间戳 */
	private Long beginTime;
	/** 结束utc时间戳 */
	private Long endTime;


	/** 三方赛事ID*/
	private String thirdMatchId;
	/** 数据源赛事ID*/
	private String thirdMatchSourceId;
	/** 数据来源*/
	private String dataSourceCode;
	/** 运动类型*/
	private Long thirdSportId;


	/** 数据源联赛ID*/
	private String thirdTournamentSourceId;
	/** 数据源赛季ID*/
	private String thirdSeasonSourceId;

}
