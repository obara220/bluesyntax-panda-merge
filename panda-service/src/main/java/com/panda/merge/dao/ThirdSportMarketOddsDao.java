package com.panda.merge.dao;

import com.panda.merge.model.ThirdSportMarketOdds;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ThirdSportMarketOddsDao {
    /**
     * 获取最大Id
     */
    Long getMaxId();

    List<ThirdSportMarketOdds> getListByParam(@Param("id") Long id, @Param("limit") Long limit);

    /**
     * 批量创建
     */
    //int insertList(@Param("list") List<ThirdSportMarketOdds> thirdSportMarketOdds);

    int upDataList(@Param("list") List<ThirdSportMarketOdds> list, @Param("dataSourceCode") String dataSourceCode);
}
