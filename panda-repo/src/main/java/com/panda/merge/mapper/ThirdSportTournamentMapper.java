package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportTournament;
import com.panda.merge.model.ThirdSportTournamentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportTournamentMapper {
    long countByExample(ThirdSportTournamentExample example);

    int deleteByExample(ThirdSportTournamentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportTournament record);

    int insertSelective(ThirdSportTournament record);

    List<ThirdSportTournament> selectByExampleWithBLOBs(ThirdSportTournamentExample example);

    List<ThirdSportTournament> selectByExample(ThirdSportTournamentExample example);

    ThirdSportTournament selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportTournament record, @Param("example") ThirdSportTournamentExample example);

    int updateByExampleWithBLOBs(@Param("record") ThirdSportTournament record, @Param("example") ThirdSportTournamentExample example);

    int updateByExample(@Param("record") ThirdSportTournament record, @Param("example") ThirdSportTournamentExample example);

    int updateByPrimaryKeySelective(ThirdSportTournament record);

    int updateByPrimaryKeyWithBLOBs(ThirdSportTournament record);

    int updateByPrimaryKey(ThirdSportTournament record);
}