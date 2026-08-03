package com.panda.merge.dao;

import com.panda.merge.model.StandardSportMarketOdds;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.dao
 * @description : TODO
 * @date: 2020-10-16 14:24
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Repository
public interface StandardSportMarketOddsDao {

    List<StandardSportMarketOdds> getMarketOddsByMatchIdList(@Param("marketIdList") List<Long> marketIdList);

    /**
     * 批量创建
     */
    int insertList(@Param("list") List<StandardSportMarketOdds> standardSportMarketOdds, @Param("tableShardingValue") Long tableShardingValue);

    /**
     * 批量修改
     *
     * @param standardSportMarketOdds
     * @return
     */
    int upDataList(@Param("list") List<StandardSportMarketOdds> standardSportMarketOdds, @Param("tableShardingValue") Long tableShardingValue);

}
