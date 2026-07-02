package com.panda.merge.dao;

import com.panda.merge.dto.MatchEventInfoDetail;
import com.panda.merge.model.MatchEventInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @date: 2020-09-10 10:33
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface MatchEventInfoDao {

    /**
     * @param standardMatchId 标准比赛ID
     * @param dataSoureCode   数据源
     * @return List<MatchEventInfo>
     */
    List<MatchEventInfo> getItemByStandardMatchIdAndDataSoureCode(@Param("standardMatchId") Long standardMatchId, @Param("dataSoureCode") String dataSoureCode);

    void updateBatch(@Param("matchEventInfoList") List<MatchEventInfoDetail> matchEventInfoList);

    void saveBatch(@Param("matchEventInfoDetail") MatchEventInfoDetail matchEventInfoDetail);


    List<MatchEventInfo> getEventHistoryByEventTime(MatchEventInfoDetail item);

    List<MatchEventInfo> getMatchEvenIdsByDayDateTime(MatchEventInfoDetail item);

    Integer deleteMatchEvenIdsByDayDateTime(MatchEventInfoDetail item);

    /**
     *  三方赛事绑定标准赛事，事件关联到标准赛事下
     * */
    void matchEvent2StandardMatch(MatchEventInfoDetail item);

    /**
     *  三方事件转换为标准事件
     * */
    void matchEvent2StandardEvent(MatchEventInfoDetail item);

}
