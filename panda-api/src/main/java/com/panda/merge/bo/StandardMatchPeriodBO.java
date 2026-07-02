package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardMatchPeriodBO implements Serializable {
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
     * 比赛阶段id.取system_item_dic中的value字段
     */
    private Long matchPeriodId;
    /**
     * 比赛进行时间.单位:秒.例如:3分钟11秒,则该值是191
     */
    private Integer secondsMatchStart;
    /**
     * 比赛是否结束.0:未结束(不属于历史赛事);1:结束.2:临时状态
     */
    private Integer matchOver;
    /**
     * PLS标准联赛ID
     */
    private Long plsStandardTournamentId;
}
