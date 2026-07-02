/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description  :  操盘配置-盘口展示配置DTO
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TradeMarketDisplayConfigDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 标准赛事ID
	 */
	private Long matchId;

	/**
	 * 早盘盘口显示数量
	 */
	private Integer preMarketNum;

	/**
	 * 滚球盘口显示数量
	 */
	private Integer liveMarketNum;
	
	/**
	 * 是否显示角球
	 */
	private boolean displayCorner;
	
	/**
	 * 是否显示罚球
	 */
	private boolean displayPenalty;
	
}
