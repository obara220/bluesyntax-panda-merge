package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleTemplateTournamentDto implements Serializable {
    /**
     * 联赛id
     * */
    private Long tournamentId;
    /**
     * 联赛id
     * */
    private Integer tournamentLevel;
    /**
     * 赛种
     * */
    private Long sportId;
    /**
     * 联赛管理ID
     * */
    private String tournamentManagerId;
    /**
     * 联赛中文名
     * */
    private String   tournamentNameCn;
    /**
     * 联赛英文名
     * */
    private String  tournamentNameEn;
    /**
     * 数据商权重名称
     * */
    private String dataSourceWeightName;
    /**
     * 数据商权重id
     * */
    private Long dataSourceWeightId;
    /**
     * 灰色区间名称
     * */
    private String  grayAreaSetName;
    /**
     * 灰色区间id
     * */
    private Long  grayAreaSetId;
    /**
     * 结算延迟名称
     * */
    private String countDownName;
    /**
     * 结算延迟id
     * */
    private Long countDownId;

    private String tournamentNameCode;
}
