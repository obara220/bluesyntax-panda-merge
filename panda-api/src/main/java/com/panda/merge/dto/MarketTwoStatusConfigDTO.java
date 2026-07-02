package com.panda.merge.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class MarketTwoStatusConfigDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    //@NotNull(message = "标准赛事ID不能为空！")
    private Long standardMatchId;

    /**
     * 0-关，1-开
     */
    //@NotNull(message = "2.0接拒状态开关，不能为空！")
    private Integer status;
    
    /**
     * 1:赛前盘;0:滚球盘  
     */
    private Integer marketType;
}
