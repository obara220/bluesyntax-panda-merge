package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerDeleteStatus implements Serializable {
    //进球 状态  =1 有灰色 2 有删除  0没有
    private Integer goalCurrentEventStatus= 0;
    //角球 状态  =1 有灰色 2 有删除  0没有
    private Integer cornerCurrentEventStatus= 0;
    //罚牌 状态  =1 有灰色 2 有删除  0没有
    private Integer facardCurrentEventStatus= 0;

    private Long standardMatchId;
}
