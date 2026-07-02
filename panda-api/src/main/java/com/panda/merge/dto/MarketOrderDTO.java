package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @Description  : 标准盘口与投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MarketOrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
     *  盘口id
     */
    @NotNull(message="盘口id不能为空")
    private String standardMarketId;

    /**
     *  盘口位置
     */
    @NotNull(message="盘口位置不能为空")
    private Integer marketOrderNumber;

}
