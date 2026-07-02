package com.panda.merge.advertise.dto;

import com.panda.merge.mq.message.CommonStandardScoresDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchEndCancelDto implements Serializable {
    /**
     * MATCH_CANCEL_END 回滚
     * */
    //标准赛事短id
    Long  standardMatchId;
    // 赛种
    Long  sportId;

    Long  periodId;//  修改后的比赛阶段

    Long secondFromStart;//  修改后的比赛进行时长

    Long  matchStatus;// 修改后的比赛状态

    CommonStandardScoresDto matchScore;//   STANDARD_MATCH_SCORE topic  下发的 比分中心组装的比分MQ 消息体
}
