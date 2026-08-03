package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardTournamentRuleBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 标准联赛id*/
    private Long tournamentId;

    /** 联赛规则 */
    private String zsTournamentRule;

}
