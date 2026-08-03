package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;

@Data
public class EditScoreV2Dto extends AbstructAdvertiseDto {

    private Long sportId;
    private Long thirdMatchId;
    /** 筛选重要事件 flag = 1重要事件 */
    private String scores;            //eg. {'set1':{home:1,away:1}, 'set2':{home:1, away:1}}
}
