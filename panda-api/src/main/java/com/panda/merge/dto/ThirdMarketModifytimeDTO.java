package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class ThirdMarketModifytimeDTO implements Serializable {
    private String dateSourceCode;

    private Long matchId;

    private Long lastModifyTime;

    private Long categoryId;

    /** 盘口类型：0滚球 1早盘 */
    private Integer marketType;

    /** 0绿灯 1黄灯 2红灯 */
    private int  level;

    private int status;

}
