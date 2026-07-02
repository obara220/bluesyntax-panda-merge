package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description:
 * @date 2019/10/4 16:07
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdMarketOddsResultDTO implements Serializable{
	private static final long serialVersionUID = 1L;
    /**
     * 	附加字段1
     */
    private String addition1;
    /**
     * 	附加字段2
     */
    private String addition2;
    /**
     * 	附加字段3
     */
    private String addition3;
    /**
     * 	附加字段4
     */
    private String addition4;
    /**
     * 	附加字段5
     */
    private String addition5;
    
    /**
     * 	结算赛果
     * 	赛果已确认：Confirmed，盘中事件确认：LiveScouted，未知：Unknown
     */
    private String betSettlementCertainty;
    
    /**
     * 	第三方投注项id
     */
    private String marketOddsId;

    /**
     * 	投注项结算结果
	 *	0 - Not Resulted
	 *	1 - Place*
	 *	2 - Return
	 *	3 - Lost
	 *	4 - Won
	 *	5 - Win Return
	 *	6 - Loose Return
     */
    private String settlementResult;
}
