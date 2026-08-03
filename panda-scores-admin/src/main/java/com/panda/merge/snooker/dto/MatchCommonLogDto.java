package com.panda.merge.snooker.dto;


import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import com.panda.merge.util.CategoryUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class MatchCommonLogDto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    private Integer controlType;   //1:比赛开始 2：比赛中断  3：比赛重开 4： 赛事结束 5.赛事中断  6：赛事重开
    private Long periodId;
    private String howAway;
    private String eventCode;
    private String beforeVal = CategoryUtils.SPLIT_LINE;
    private String afterVal = CategoryUtils.SPLIT_LINE;
}
