package com.panda.merge.dto.advertise.v2;


import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;


@Data
public class ChangeMatchScoreV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long periodId;
    private Long thirdMatchId;
    private String eventCode;
    private String homeAway;
    private Long   secondFromStart;
    private Boolean  isFoul;

//    //TODO 不确定
//    private Integer periodT1;
//    private Integer periodT2;
//    private Long matchTime;
//    private Integer type;

}
