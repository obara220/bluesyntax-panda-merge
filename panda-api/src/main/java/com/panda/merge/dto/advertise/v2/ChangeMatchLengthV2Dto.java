package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;


@Data
public class ChangeMatchLengthV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    /**
     * 分钟 10 ，12  20  加时赛默认 5分钟 半场
     * */
    private Integer minutes;  //对于足球是传分钟然后来判断赛制，对于斯诺克是直接传赛制：如3，就是3局两胜

}
