package com.panda.merge.dto.message;

import com.panda.merge.dto.MarketOrderDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author raulvii
 */
@Data
public class OutrightMarketOrderMessage implements Serializable {

    /**
     * 标准冠军赛事Id
     */
    @NotNull(message = "标准赛事id不能为空")
    private Long standardMatchId;

    
    /**
     * 冠军盘口排序列表
     */
    @NotNull(message = "盘口排序列表不能为空")
    private List<MarketOrderDTO> marketOrderDTOList;

}
