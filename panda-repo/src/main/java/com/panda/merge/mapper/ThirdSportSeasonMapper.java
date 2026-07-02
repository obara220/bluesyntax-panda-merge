package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportSeason;
import com.panda.merge.model.ThirdSportSeasonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdSportSeasonMapper {
    long countByExample(ThirdSportSeasonExample example);

    int deleteByExample(ThirdSportSeasonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportSeason record);

    int insertSelective(ThirdSportSeason record);

    List<ThirdSportSeason> selectByExample(ThirdSportSeasonExample example);

    ThirdSportSeason selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportSeason record, @Param("example") ThirdSportSeasonExample example);

    int updateByExample(@Param("record") ThirdSportSeason record, @Param("example") ThirdSportSeasonExample example);

    int updateByPrimaryKeySelective(ThirdSportSeason record);

    int updateByPrimaryKey(ThirdSportSeason record);
}