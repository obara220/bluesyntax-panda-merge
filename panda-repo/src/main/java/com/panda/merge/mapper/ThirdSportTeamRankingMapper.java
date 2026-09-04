package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportTeamRanking;
import com.panda.merge.model.ThirdSportTeamRankingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdSportTeamRankingMapper {
    long countByExample(ThirdSportTeamRankingExample example);

    int deleteByExample(ThirdSportTeamRankingExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdSportTeamRanking record);

    int insertSelective(ThirdSportTeamRanking record);

    List<ThirdSportTeamRanking> selectByExampleWithBLOBs(ThirdSportTeamRankingExample example);

    List<ThirdSportTeamRanking> selectByExample(ThirdSportTeamRankingExample example);

    ThirdSportTeamRanking selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdSportTeamRanking record, @Param("example") ThirdSportTeamRankingExample example);

    int updateByExampleWithBLOBs(@Param("record") ThirdSportTeamRanking record, @Param("example") ThirdSportTeamRankingExample example);

    int updateByExample(@Param("record") ThirdSportTeamRanking record, @Param("example") ThirdSportTeamRankingExample example);

    int updateByPrimaryKeySelective(ThirdSportTeamRanking record);

    int updateByPrimaryKeyWithBLOBs(ThirdSportTeamRanking record);

    int updateByPrimaryKey(ThirdSportTeamRanking record);
}