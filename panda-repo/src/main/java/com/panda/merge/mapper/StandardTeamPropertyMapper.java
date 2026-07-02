package com.panda.merge.mapper;

import com.panda.merge.model.StandardTeamProperty;
import com.panda.merge.model.StandardTeamPropertyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardTeamPropertyMapper {
    long countByExample(StandardTeamPropertyExample example);

    int deleteByExample(StandardTeamPropertyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardTeamProperty record);

    int insertSelective(StandardTeamProperty record);

    List<StandardTeamProperty> selectByExample(StandardTeamPropertyExample example);

    StandardTeamProperty selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardTeamProperty record, @Param("example") StandardTeamPropertyExample example);

    int updateByExample(@Param("record") StandardTeamProperty record, @Param("example") StandardTeamPropertyExample example);

    int updateByPrimaryKeySelective(StandardTeamProperty record);

    int updateByPrimaryKey(StandardTeamProperty record);
}