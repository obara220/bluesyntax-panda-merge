package com.panda.merge.mapper;

import com.panda.merge.model.ThirdTeamProperty;
import com.panda.merge.model.ThirdTeamPropertyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdTeamPropertyMapper {
    long countByExample(ThirdTeamPropertyExample example);

    int deleteByExample(ThirdTeamPropertyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdTeamProperty record);

    int insertSelective(ThirdTeamProperty record);

    List<ThirdTeamProperty> selectByExample(ThirdTeamPropertyExample example);

    ThirdTeamProperty selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdTeamProperty record, @Param("example") ThirdTeamPropertyExample example);

    int updateByExample(@Param("record") ThirdTeamProperty record, @Param("example") ThirdTeamPropertyExample example);

    int updateByPrimaryKeySelective(ThirdTeamProperty record);

    int updateByPrimaryKey(ThirdTeamProperty record);
}