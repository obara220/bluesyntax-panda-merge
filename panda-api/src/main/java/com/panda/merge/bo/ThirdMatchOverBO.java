package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThirdMatchOverBO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 三方赛事ID
     */
    private Long thirdMatchId;


    private Integer matchOver;
}
