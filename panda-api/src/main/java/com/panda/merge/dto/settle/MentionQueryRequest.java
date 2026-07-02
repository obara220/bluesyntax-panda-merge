package com.panda.merge.dto.settle;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: mention query
 * @author: Henry Wang
 * @create: 2024-08-31 13:56
 **/

@Data
public class MentionQueryRequest implements Serializable {

    @NotNull
    private Long matchId;

    @Min(0)
    private Integer mentionType = 0; //0:all 1:deleteStatus  2:dataMismatchStatus

    private Integer mentionDetail = 1;   // 0: 不展示 1: 展示  默认1

    private Long sportId = 1L;

}
