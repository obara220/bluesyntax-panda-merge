package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportTournament;
import com.panda.merge.model.StandardSportTournamentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardSportTournamentMapper {
    long countByExample(StandardSportTournamentExample example);

    int deleteByExample(StandardSportTournamentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportTournament record);

    int insertSelective(StandardSportTournament record);

    List<StandardSportTournament> selectByExampleWithBLOBs(StandardSportTournamentExample example);

    List<StandardSportTournament> selectByExample(StandardSportTournamentExample example);

    StandardSportTournament selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportTournament record, @Param("example") StandardSportTournamentExample example);

    int updateByExampleWithBLOBs(@Param("record") StandardSportTournament record, @Param("example") StandardSportTournamentExample example);

    int updateByExample(@Param("record") StandardSportTournament record, @Param("example") StandardSportTournamentExample example);

    int updateByPrimaryKeySelective(StandardSportTournament record);

    int updateByPrimaryKeyWithBLOBs(StandardSportTournament record);

    int updateByPrimaryKey(StandardSportTournament record);
}