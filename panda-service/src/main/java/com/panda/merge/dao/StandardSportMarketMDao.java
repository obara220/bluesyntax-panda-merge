package com.panda.merge.dao;

import com.panda.merge.model.StandardSportMarketM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StandardSportMarketMDao {

    void insertList(@Param("list") List<StandardSportMarketM> list);

    void updateBatch(@Param("list") List<StandardSportMarketM> list);
}
