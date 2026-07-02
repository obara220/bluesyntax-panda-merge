package com.panda.merge.mapper;

import com.panda.merge.model.StandardTournamentProperty;
import com.panda.merge.model.StandardTournamentPropertyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardTournamentPropertyMapper {
    long countByExample(StandardTournamentPropertyExample example);

    int deleteByExample(StandardTournamentPropertyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardTournamentProperty record);

    int insertSelective(StandardTournamentProperty record);

    List<StandardTournamentProperty> selectByExample(StandardTournamentPropertyExample example);

    StandardTournamentProperty selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardTournamentProperty record, @Param("example") StandardTournamentPropertyExample example);

    int updateByExample(@Param("record") StandardTournamentProperty record, @Param("example") StandardTournamentPropertyExample example);

    int updateByPrimaryKeySelective(StandardTournamentProperty record);

    int updateByPrimaryKey(StandardTournamentProperty record);
}