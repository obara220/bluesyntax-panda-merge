package com.panda.merge.service;

import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.MatchEventInfoDetail;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportTeam;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service <br>
 */
public interface MatchEventInfoService {

    MatchEventInfo create(MatchEventInfoDTO matchEventInfoDTO, ThirdMatchInfo thirdMatchInfo, ThirdSportTeam thirdSportTeam, Long sportId, String linkId);

    MatchEventInfo getItem(String thirdEventId,String dataSourceCode,Integer sourceType,String thirdMatchSourceId, Integer canceled);

    MatchEventInfo getItem(String thirdEventId, String dataSourceCode,String thirdMatchSourceId);

    List<MatchEventInfo> getMatchEventInfoByThird(Long matchPeriodId, String eventCode, String thirdMatchSourceId, String dataSoureCode,Integer canceled);

    /**
     * 判断是否是取消事件如果是就进行取消事件设置
     * @param linkId
     * @param matchEventInfoDTO
     * @param matchEventInfo
     * @param matchEventInfoList 本次批量处理的事件列表
     * @param thirdMatchInfo
     */
    void processDeleteEvent(String linkId, MatchEventInfoDTO matchEventInfoDTO, MatchEventInfo matchEventInfo, List<MatchEventInfo> matchEventInfoList, Boolean pandaDbIsError, ThirdMatchInfo thirdMatchInfo);

    /**
     * 根据标准赛事ID和数据源获取库中未下发的事件列表
     * @param standardMatchId 标准比赛ID
     * @param dataSoureCode   数据源
     * @return List<MatchEventInfo>
     */
    List<MatchEventInfo> getItemByStandardMatchIdAndDataSoureCode(Long standardMatchId, String dataSoureCode);

    /**
     * 根据三方赛事ID获取赛事事件信息
     * @param thirdMatchId 三方赛事ID
     * @param sendData     下发数据标识：Y:已下发,N:未下发
     * @return List<MatchEventInfo>
     */
    List<MatchEventInfo> getItemByThirdMatchIdAndSendData(Long thirdMatchId, String dataSoureCode,String sendData);

    MatchEventInfo getMatchEventInfo(Long thirdMatchId,String dataSource,String eventCode);

    /**
     * 根据三方赛事ID获取赛事事件信息
     * @param thirdMatchId 三方赛事ID
     * @param dataSource   数据源编码
     * @return List<MatchEventInfo>
     */
     List<MatchEventInfo> getItemByThirdMatchIdAndDataSoureCode(Long thirdMatchId,String dataSource);

    void save(MatchEventInfo matchEventInfo);

    /**
     * 批量新增
     * @param matchEventInfoList 事件
     */
    void saveBatch(List<MatchEventInfo> matchEventInfoList,String linkId);

    /**
     * 批量修改或者新增
     * @param matchEventInfoList 事件
     */
    void upOrSaveBatch(List<MatchEventInfo> matchEventInfoList,String linkId);

    /**
     * 批量修改
     * @param matchEventInfoList 事件
     */
    void updateBatch(List<MatchEventInfo> matchEventInfoList);

    /**
     *  更新
     * @param matchEventInfo
     */
    void updateById(MatchEventInfo matchEventInfo);

    List<MatchEventInfo> getEventHistoryByEndEvent(MatchEventInfo matchEventInfo);

    MatchEventInfo getMatchEventInfo(MatchEventInfo matchEventInfo);

    List<MatchEventInfo> getEventHistoryByEventTime(MatchEventInfoDetail matchEventInfo);

    List<MatchEventInfo> getMatchEvenIdsByDayDateTime(MatchEventInfoDetail matchEventInfo);

    Integer deleteMatchEvenIdsByDayDateTime(MatchEventInfoDetail matchEventInfo);

    /**
     *  三方事件关联到标准赛事下
     * */
    void matchEvent2StandardMatch(String linkId, ThirdMatchInfo thirdMatchInfo);

    /**
     *  三方事件转换为标准事件
     * */
    void matchEvent2StandardEvent(String linkId, ThirdMatchInfo thirdMatchInfo);

}
