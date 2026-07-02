package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class MatchAdvertiseQueryDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;

    /**
     * 标准赛事ID
     */
    private Long standardMatchId;
    /**
     * PD
     * OR PD2
     * */
    private String  dataSourceCode;

    /**
     * 语言
     */
    private String language;

}
