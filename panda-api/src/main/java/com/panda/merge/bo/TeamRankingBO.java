package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jitwxs
 * @date 2023年09月20日 17:31
 */
@Deprecated
@Data
public class TeamRankingBO implements Serializable {

    /**
     * 三方数据源赛季ID+榜单ID+球队ID'
     */
    private String  id;

    /**
     * 标准球队ID
     * */
    private Long standardTeamId;

    /** 数据来源*/
    private String dataSourceCode;

    /**
     * 自建赛事主客队是否包含在联赛球队榜单中（0:否  1:是）
     * */
    private Integer status;

    /**
     * 参数场数
     */
    private Integer matchCount;

    /**
     * 胜场数
     */
    private Integer winTotal;

    /**
     * 平局数
     */
    private Integer drawTotal;

    /**
     * 负场数
     */

    private Integer lossTotal;

    /**
     * 积分数
     */
    private Integer pointsTotal;

    /**
     * 进球数
     */
    private Integer goalsForTotal;

    /**
     * 失球数
     */
    private Integer goalsAgainstTotal;

    /**
     * 净胜球数
     */
    private Integer goalDiffTotal;

    /**
     * 排名值
     * */
    private Integer positionTotal;

    /**
     * 联赛类别(0:其他,1联赛,2杯赛)
     * */
    private Integer tournamentType;

    /**
     * 三方数据源赛季ID
     * */
    private String thirdSourceSeasonId;

    /**
     * 三方数据源联赛ID
     * */
    private String thirdTournamentSourceId;

    /**
     *  榜单ID(类似于该赛季下榜单类型)
     * */
    private String rankingId;

    /**
     * 组ID
     * */
    private String groupId;
    /**
     * 球队最近5场战绩 D-W-L-N-N
     */
    private String record5;

    /**
     * 组名称
     * */
    private String groupCnName;

    /**
     * 榜单中文名称
     */
    private String rankingCnName;

    /**
     * 榜单英文名称
     */
    private String rankingEnName;

    /**
     * 小组赛冠军赛事盘口投注id(赛事id-盘口盘口id-统一盘口id-投注项id)
     */
    private String winnerMarketOddsid;

    /**
     * 小组赛晋级赛事盘口投注id(赛事id-盘口盘口id-统一盘口id-投注项id)
     */
    private String advanceMarketOddsid;


    /**
     *  球队多语言
     * */
    private List<I18nItemBO> teamNameIl8nList;

    /** 0:自动1:手动*/
    private Boolean editStatus;
}
