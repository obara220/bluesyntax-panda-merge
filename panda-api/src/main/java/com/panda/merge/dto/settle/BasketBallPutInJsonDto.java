package com.panda.merge.dto.settle;

import lombok.Data;

@Data
public class BasketBallPutInJsonDto extends AbstructMatchSettleDto{

    private String putInJson; //带入的结算信息Json
    private Long standardMatchId;
    private Long matchScoreId;

}
