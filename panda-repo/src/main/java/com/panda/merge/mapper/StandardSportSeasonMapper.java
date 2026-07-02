package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportSeason;
import com.panda.merge.model.StandardSportSeasonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardSportSeasonMapper {
    long countByExample(StandardSportSeasonExample example);

    int deleteByExample(StandardSportSeasonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportSeason record);

    int insertSelective(StandardSportSeason record);

    List<StandardSportSeason> selectByExample(StandardSportSeasonExample example);

    StandardSportSeason selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportSeason record, @Param("example") StandardSportSeasonExample example);

    int updateByExample(@Param("record") StandardSportSeason record, @Param("example") StandardSportSeasonExample example);

    int updateByPrimaryKeySelective(StandardSportSeason record);

    int updateByPrimaryKey(StandardSportSeason record);
}