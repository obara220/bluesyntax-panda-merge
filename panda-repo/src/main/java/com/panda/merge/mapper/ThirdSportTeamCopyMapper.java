package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportTeamCopy;
import com.panda.merge.model.ThirdSportTeamCopyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportTeamCopyMapper {
    long countByExample(ThirdSportTeamCopyExample example);

    int deleteByExample(ThirdSportTeamCopyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportTeamCopy record);

    int insertSelective(ThirdSportTeamCopy record);

    List<ThirdSportTeamCopy> selectByExample(ThirdSportTeamCopyExample example);

    ThirdSportTeamCopy selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportTeamCopy record, @Param("example") ThirdSportTeamCopyExample example);

    int updateByExample(@Param("record") ThirdSportTeamCopy record, @Param("example") ThirdSportTeamCopyExample example);

    int updateByPrimaryKeySelective(ThirdSportTeamCopy record);

    int updateByPrimaryKey(ThirdSportTeamCopy record);
}