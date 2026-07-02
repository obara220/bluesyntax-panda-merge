package com.panda.merge.dto.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class OutrightMarketSoldMessage implements Serializable {

    /**
     * 标准冠军赛事Id
     */
    @NotNull(message = "标准赛事id不能为空")
    private Long standardOutrightMatchId;

    @NotNull(message = "数据源不能为空")
    private String dataSourceCode;
    
    /**
     * 冠军盘口id列表
     */
    @NotNull(message = "开售冠军盘口id列表不能为空")
    private List<Long> marketIdList;

}
