package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mirro
 * @Project Name :  panda_data_realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description:
 * @date 2019/10/4 16:07
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdMarketResultDTO implements Serializable{
	private static final long serialVersionUID = 1L;

    /**
     * 	第三方盘口原始id
     */
    private String thirdMarketId;
    
    /**
     *  赛果明细
     */
    private List<ThirdMarketOddsResultDTO> marketOddsResultList;

    /**
     * 异常结算原因id（可为空）
     */
    private Integer reasonId;

}
