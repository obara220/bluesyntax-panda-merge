package com.panda.merge.dto.advertise;


import lombok.Data;



@Data
public class ChangeMatchScoreDto extends AbstructAdvertiseDto {
    //Long thirdMatchId,Integer firstNum,Integer home ,Integer away
    private Long thirdMatchId;
    private Long period;
    private Integer periodT1;
    private Integer periodT2;
    private Long matchTime;
    private Integer type;

}
