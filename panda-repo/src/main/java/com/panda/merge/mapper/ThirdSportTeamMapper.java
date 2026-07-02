package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportTeam;
import com.panda.merge.model.ThirdSportTeamExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportTeamMapper {
    long countByExample(ThirdSportTeamExample example);

    int deleteByExample(ThirdSportTeamExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportTeam record);

    int insertSelective(ThirdSportTeam record);

    List<ThirdSportTeam> selectByExample(ThirdSportTeamExample example);

    ThirdSportTeam selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportTeam record, @Param("example") ThirdSportTeamExample example);

    int updateByExample(@Param("record") ThirdSportTeam record, @Param("example") ThirdSportTeamExample example);

    int updateByPrimaryKeySelective(ThirdSportTeam record);

    int updateByPrimaryKey(ThirdSportTeam record);
}