package com.panda.merge.dto.message;


import lombok.Data;

import java.io.Serializable;

/**
 * 标准赛事 提前结算开关
 */
@Data
public class StandardMatchPreStatusMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchId;

    private Double value;
}
