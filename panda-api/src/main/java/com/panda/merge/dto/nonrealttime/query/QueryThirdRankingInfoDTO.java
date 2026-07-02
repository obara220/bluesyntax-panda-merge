package com.panda.merge.dto.nonrealttime.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 三方联赛球队榜单数据同步参数类
 * @author     tell
 * @since     2020年10月18日10:06:39
 */
@Data
public class QueryThirdRankingInfoDTO implements Serializable {

    /** 开始utc时间戳 (beginTime,seasonId,thirdTournamentSourceId不能同时为空) */
    private Long beginTime;

    /** 运动类型列表，为空查询全部*/
    private List<Long> sportIds;

    /** 榜单对应的赛季ID*/
    private String seasonId;

    /** 数据源联赛ID*/
    private String thirdTournamentSourceId;
}
