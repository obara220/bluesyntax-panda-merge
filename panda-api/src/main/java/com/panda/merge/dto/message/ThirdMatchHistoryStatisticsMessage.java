package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 三方赛事历史统计信息
 * @author  tell
 * @since   2021年2月9日15:52:11
 */
@Data
public class ThirdMatchHistoryStatisticsMessage implements Serializable{

	private static final long serialVersionUID = 1L;

    /**"数据源编码ID+数据源赛事id"*/
    private String id;

    /**"数据源赛事id"*/
    private String thirdMatchSourceId;

    /**"数据源联赛id"*/
    private String thirdTournamentSourceId;

    /**"数据源赛季id"*/
    private String thirdSeasonSourceId;

    /**"运动类型"*/
    private Long sportId;

    /**"数据来源"*/
    private String dataSourceCode;

    /**"开赛时间"*/
    private Long beginTime;

    /**"赛事状态"*/
    private String matchStatus;

    /**"数据源主队ID"*/
    private String homeTeamid;

    /**"数据源客队ID"*/
    private String awayTeamid;

    /**"主队名称"*/
    private String homeTeamName;

    /**"客队名称"*/
    private String awayTeamName;

    /**"主队得分"*/
    private String homeTeamScore;

    /**"主队得分"*/
    private String awayTeamScore;

    /**"初盘让球盘口值"*/
    private String handicapVal;

    /**"初盘大小盘口值"*/
    private String overunderVal;

    /**"初盘胜平负投注项值"*/
    private String winnerOdds;

    /**"初盘让球投注项值"*/
    private String handicapOdds;

    /**"初盘大小投注项值"*/
    private String overunderOdds;

    /**"修改时间"*/
    private Long modifyTime;

    /**"创建时间"*/
    private Long createTime;

}
