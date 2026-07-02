/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  滚动操盘-自动水差参数
 * @author       :  Vito
 * @Date:  2019年11月6日 下午2:22:06
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 * v2.6                   vito  2020/5/10  增加盘口配置参数      
 */
@Data
public class TradeMarketAutoDiffConfigDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 标准赛事ID
	 */
	@NotNull(message = "赛事id不能为空")
	private Long matchId;
	
	/**
	 * 水差配置
	 */
	@Valid
	private List<TradeMarketAutoDiffConfigItemDTO> diffConfigs;
}
