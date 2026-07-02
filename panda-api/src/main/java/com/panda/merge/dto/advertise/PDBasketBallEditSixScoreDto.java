package com.panda.merge.dto.advertise;

import com.panda.merge.cache.CommonItem;
import lombok.Data;

@Data
public class PDBasketBallEditSixScoreDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    private CommonItem period1306;
    private CommonItem period1312;
    private CommonItem period1406;
    private CommonItem period1412;
    private CommonItem period1506;
    private CommonItem period1512;
    private CommonItem period1606;
    private CommonItem period1612;
    private Long periodId;
}
