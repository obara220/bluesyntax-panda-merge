package com.panda.merge.dto.resultScore;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 比分修正消息体
 */
@Data
public class MatchResultScoreMsgVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 赛种id
	 */
	private Long sportId;
	
	/**
	 * 赛事id
	 */
	private Long matchId;

	/**
	 * 比分,格式：["S1|0:2","S120|9:11","S121|9:11","S122|5:3"]
	 */
	private List<String> score;

	/**
	 * 修改时间
	 */
	private Long modifyTime;
}