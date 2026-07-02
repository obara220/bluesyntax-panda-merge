package com.panda.merge.dto.advertise;

import lombok.Data;



@Data
public class ChangeMatchLengthDto extends AbstructAdvertiseDto {
    /**
     * 三方赛事ID
     * */
    private Long thirdMatchId;
    /**
     * 分钟 10 ，12  20  加时赛默认 5分钟 半场
     * */
    private Integer minutes;

}
