package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class EventListDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    /** 筛选重要事件 flag = 1重要事件 */
    private Long flag;

    private Long periodId;
}
