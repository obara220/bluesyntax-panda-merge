package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import lombok.Data;

@Data
public class SendFreeThrowDto extends AbstructMatchSettleDto {
    /**
     * PD赛事id
     * */
    private Long thirdMatchId;
    /**
     * 罚球总次数
     * */
    private Integer freeThrowNumber;
    /**
     * score  得分
     * */
    private Integer score;

    private String homeAway;

    /**
     * 罚球点完单个事件后点确定 true,
     * 输入框输入 进/总 false
     */
    private boolean type;
}
