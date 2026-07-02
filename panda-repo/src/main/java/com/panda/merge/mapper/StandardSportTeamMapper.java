package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.model.StandardSportTeamExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportTeamMapper {
    long countByExample(StandardSportTeamExample example);

    int deleteByExample(StandardSportTeamExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportTeam record);

    int insertSelective(StandardSportTeam record);

    List<StandardSportTeam> selectByExample(StandardSportTeamExample example);

    StandardSportTeam selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportTeam record, @Param("example") StandardSportTeamExample example);

    int updateByExample(@Param("record") StandardSportTeam record, @Param("example") StandardSportTeamExample example);

    int updateByPrimaryKeySelective(StandardSportTeam record);

    int updateByPrimaryKey(StandardSportTeam record);
}