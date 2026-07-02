package com.panda.merge.rocketmq.common;

import java.util.Map;

public interface RcsHisDataService {
    /**
     * 操盘后台config系列配置表清理
     * @param standardMatchId
     * @return
     */
    void configDataHandler(String linkId, Long standardMatchId);

    /**
     * 标准盘口数据清理
     * @param standardMatchId
     * @return
     */
    void standardSportMarketDataHandler(String linkId, Long standardMatchId);

    /**
     * 玩法开售数据清理
     * @param standardMatchId
     * @return
     */
    void marketCategorySellDataHandler(String linkId, Long standardMatchId);

    /**
     * 操作员数据表清理
     * @param standardMatchId
     * @return
     */
    void operatorDataHandler(String linkId, Long standardMatchId);

    /**
     * 三方盘口数据清理
     * Map<三方赛事ID,三方赛事源ID>
     *
     * @param mapIds
     * @param dataSourceCode
     * @return
     */
    void thirdSportMarketDataHandler(String linkId, Map<Long, String> mapIds, String dataSourceCode);

    /**
     * Map<三方赛事ID,三方赛事源ID>
     * 三方赛事ID，三方赛事源ID
     *
     * @param idMap
     * @param dataSourceCode
     * @return
     */
    void thirdTradeDataHandler(String linkId, Map<Long, String> idMap, String dataSourceCode);

    /**
     * 清理标准赛事信息
     * @param dayDateTime  某一天的时间戳
     * @param matchNum     一次最多清理的赛事条数
     */
    void cleanEndedDayStandardMatch(Long dayDateTime,Integer matchNum,Integer matchOver,Integer deleteEvent);

    /**
     * 清理三方赛事信息
     * @param dayDateTime  某一天的时间戳
     * @param matchNum     一次最多清理的赛事条数
     */
    void cleanEndedDayThirdMatch(Long dayDateTime,Integer matchNum,Integer matchOver,Integer deleteEvent);

    /**
     * 清理赛事事件信息
     * @param dataSourceCode 数据源编码
     * @param dayDateTime    某一天的时间戳
     * @param matchEventNum  本次需要清理的事件条数
     */
    Integer cleanMatchEventInfoData(String dataSourceCode,Long dayDateTime,Integer matchEventNum);
}
