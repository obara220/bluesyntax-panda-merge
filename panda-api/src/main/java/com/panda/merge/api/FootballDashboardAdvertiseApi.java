package com.panda.merge.api;

import com.panda.merge.bo.UserLoginStatusBO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.model.MatchTimeInfo;

/**
 * PA报球板事件接口
 *
 * @author warren
 * @since 2023/12/06 13:07:29
 */
public interface FootballDashboardAdvertiseApi {

    /**
     * 伤停补时事件
     *
     * @param injuryTimeEventDto 伤停实时赛事信息
     * @return 响应
     */
    Response injuryTimeEvent(InjuryTimeEventDto injuryTimeEventDto);

    /**
     * 时间状态事件，用于重置比赛时间
     *
     * @param timeStatusEventDto 时间状态赛事信息
     * @return 响应
     */
    Response timeStatusEvent(TimeStatusEventDto timeStatusEventDto);

    /**
     * 赛事统计事件
     *
     * @param matchCountDto 赛事统计入参对象
     * @return 响应
     */
//    Response matchCountEvent(MatchCountDto matchCountDto);

    /**
     * 按ID获取更新数据
     *
     * @param id 主键
     * @return 更新后的数据
     */
    MatchTimeInfo getMatchTimeInfoUpdated(Long id);

    /**
     * 更改比赛状态
     *
     * @param changeMatchStatus 比赛状态
     * @return 响应
     */
    Response changeMatchStatus(ChangeMatchStatusDto changeMatchStatus);

    Response<UserLoginStatusBO> forceUserSignOut(ForceUserSignOutDto forceUserSignOutDto);
}
