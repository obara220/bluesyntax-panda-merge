package com.panda.merge.dao;

import com.panda.merge.model.StandardSportMarket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @description : TODO
 * @date: 2020-09-09 15:39
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public interface StandardSportMarketDao {

    List<StandardSportMarket> getItemByThirdMarketSourceIdsAndDataSourceCode(@Param("strList") List<String> strList, @Param("dataSourceCode") String dataSourceCode, @Param("standardMatchId") Long standardMatchId);

    List<StandardSportMarket> getMarketByMatchIdList(@Param("standardMatchIdList") List<Long> standardMatchIdList);

    StandardSportMarket getMarketByRelationId(Long relationMarketId);

    /**
     * 批量创建
     */
    int insertList(@Param("list") List<StandardSportMarket> standardSportMarkets);

    /**
     * 批量修改
     * @param standardSportMarkets
     * @return
     */
    int upDataList(@Param("list") List<StandardSportMarket> standardSportMarkets);

}
