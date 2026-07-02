package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportPlayerRanking;
import com.panda.merge.model.ThirdSportPlayerRankingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdSportPlayerRankingMapper {
    long countByExample(ThirdSportPlayerRankingExample example);

    int deleteByExample(ThirdSportPlayerRankingExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdSportPlayerRanking record);

    int insertSelective(ThirdSportPlayerRanking record);

    List<ThirdSportPlayerRanking> selectByExample(ThirdSportPlayerRankingExample example);

    ThirdSportPlayerRanking selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdSportPlayerRanking record, @Param("example") ThirdSportPlayerRankingExample example);

    int updateByExample(@Param("record") ThirdSportPlayerRanking record, @Param("example") ThirdSportPlayerRankingExample example);

    int updateByPrimaryKeySelective(ThirdSportPlayerRanking record);

    int updateByPrimaryKey(ThirdSportPlayerRanking record);
}