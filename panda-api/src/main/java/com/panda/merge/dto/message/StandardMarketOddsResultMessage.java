package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Horus
 * @Description :  TODO
 * @Date: 2019/10/7 17:10
 * @ModificationHistory Who    What   When
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketOddsResultMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准投注项ID
     */
    private Long id;

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
    
    /**
     * 	结算赛果
     * 	赛果已确认：Confirmed，盘中事件确认：LiveScouted，未知：Unknown
     */
    private String betSettlementCertainty;
    
    /**
     * 	第三方投注项原始id
     */
    private String marketOddsId;

    /**
     * 附加字段1
     */
    private String addition1;

    /**
     * 附加字段2
     */
    private String addition2;

    /**
     * 附加字段3
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
    
}
