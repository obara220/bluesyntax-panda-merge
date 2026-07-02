package com.panda.merge.dto.advertise;

import lombok.Data;



@Data
public class ChangeMatchStatusDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;
    /**
     * 赛事控制类型:
     * 1 开始   2 中断  3 开始(中断后开始)  4 结束
     * */
    private Integer controlType;

    private Long periodId;

    // 页面时间
    private Long timeFromStartSecond;

    /**
     * 赛事控制类型:
     * 1 跳球开始 0  其他开始
     */
    private Integer isJump;

    private String dataSourceCode;
}
