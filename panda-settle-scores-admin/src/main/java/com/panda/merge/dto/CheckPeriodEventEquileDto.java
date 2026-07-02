package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckPeriodEventEquileDto implements Serializable {
    private Integer eventT1;
    private Integer eventT2;
    //發牌 需要 另外 2個 比分
    private Integer eventFirstT1;
    private Integer eventFirstT2;
    private Integer eventSecondT1;
    private Integer eventSecondT2;
    private Long period;
    private Integer orderNum;

    private boolean isPassCheck=true;

    private boolean isNeedNoneEvent =false;
}
