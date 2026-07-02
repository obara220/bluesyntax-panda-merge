package com.panda.merge.dto.settle;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @description: settle event delete request
 * @author: Henry Wang
 * @create: 2024-08-31 16:25
 **/

@Data
public class SettleEventDeleteRequest extends AbstructMatchSettleDto {

    @NotNull
    private Long matchId;

    @NotNull
    private Long matchScoreId;

    @Min(1)
    private Integer mentionType;      // 1： 删除事件 2：数据不匹配

    private String eventCode;

    private Long sportId = 1L;
}
