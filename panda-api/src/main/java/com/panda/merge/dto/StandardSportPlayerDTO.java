package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 同步标准球员信息参数类
 * @author  tell
 * @since   2021年1月13日10:57:53
 */
@Data
public class StandardSportPlayerDTO implements Serializable {

	/** 最近修改时间（默认1L）*/
	private Long modifyTime = 1L;
	/** 数据来源*/
	private String dataSourceCode;
	/** 三方球员ID*/
	private String thirdSourcePlayerId;
	/** 运动类型*/
	private Long thirdSportId;
	/** 赛事id*/
	private Long matchId;




}
