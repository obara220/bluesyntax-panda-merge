package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;


@Data
public class ChangeMatchStatusV2Dto extends AbstructAdvertiseDto {

    private Long sportId;
    private Long thirdMatchId;
    private Integer controlType;   //1:比赛开始 2：比赛中断  3：比赛重开 4： 赛事结束 5.赛事中断  6：赛事重开
    private Long periodId;
    private Long startTimeSecond;
    private String dataSourceCode;
    private String homeAway;

}
