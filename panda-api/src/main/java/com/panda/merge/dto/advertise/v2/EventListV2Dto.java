package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;

import java.util.List;

@Data
public class EventListV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    private List<Long> setNums;
    /** 筛选重要事件 flag = 1重要事件 */
    private Long flag;
}
