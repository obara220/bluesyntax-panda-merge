package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleCheckResultDto implements Serializable {

    private Long id ;

    private String settleNum;
    /**
     *  0 检查不通过  1检查通过
     * */
    private Integer checkResult;
}
