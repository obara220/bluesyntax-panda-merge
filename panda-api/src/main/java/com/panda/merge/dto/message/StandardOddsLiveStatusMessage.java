package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardOddsLiveStatusMessage implements Serializable {

    /**
     * 标准赛事Id
     */
    private Long standardMatchId;

    /**
     * 标准赛事Id
     */
    private Long sportId;

    /**
     * 数据源
     */
    private String dataSourceCode;

    /** 是否提前开赛 0否，1是*/
    private Integer advance = 0;

}
