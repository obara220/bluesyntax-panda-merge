package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardMatchOverBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchId;
    /**
     * PLS标准赛事ID
     */
    private Long plsStandardMatchId;

    private Integer matchOver;
}
