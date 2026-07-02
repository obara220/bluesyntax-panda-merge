package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author :	idol
 * @description : 结算切换参数dto
 * @date: 2022-2-19 15:36:40
 * @ModificationHistory Who When What -------- ---------
 *                      --------------------------
 */
@Data
@ApiModel(description = "结算切换参数dto")
public class MatchSettleSwitcherDto extends AbstructMatchSettleDto implements Serializable   {

	//linkid
	private String LinkId;

	private Long sportId;
	/**
	 * 操作对象赛事管理id
	 */
	private Long matchId;

	/**
	 * 1. 结算1.0  2 结算2.0
	 */
	private Integer settleType;


	private Long matchScoreId;

	public MatchSettleSwitcherDto(){

	}
	public MatchSettleSwitcherDto(String linkId, Long sportId, Long matchId, Integer settleType,Long matchScoreId) {
		LinkId = linkId;
		this.sportId = sportId;
		this.matchId = matchId;
		this.settleType = settleType;
		this.matchScoreId = matchScoreId;
	}

	@Override
	public String toString() {
		return "MatchSettleSwitcherDto{" +
				"LinkId='" + LinkId + '\'' +
				", sportId=" + sportId +
				", matchId=" + matchId +
				", settleType=" + settleType +
				", matchScoreId=" + matchScoreId +
				'}';
	}
}