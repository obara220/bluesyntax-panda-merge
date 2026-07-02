package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardMatchStatusBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchId;
    /**
     * PLS标准赛事ID
     */
    private Long plsStandardMatchId;
    /**
     * 赛事状态.
     * 字典数据，对应 parent_type_id = 5
     */
    private Integer matchStatus;
    /**
     * 比赛是否结束.0:未结束(不属于历史赛事);1:结束.2:临时状态
     */
    private Integer matchOver;
    /**
     * 标准赛事是否出现过中断或取消状态,0:否,1:是
     */
    private Integer interruptionCancellationStatus;
    /**
     * PLS标准联赛ID
     */
    private Long plsStandardTournamentId;
}
