package com.panda.merge.dto.scores;


import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑比分中心比分时，是否校验区间比分
 */
@Slf4j
@Data
public class ScoresCenterCheckSwitchDTO extends AbstructAdvertiseDto {
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 赛事管理ID
     */
    private String matchManageId;

    /**
     * 编辑 是否校验区间比分
     */
    private Boolean minScoresCheck;


}
