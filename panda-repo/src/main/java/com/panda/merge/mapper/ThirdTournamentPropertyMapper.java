package com.panda.merge.mapper;

import com.panda.merge.model.ThirdTournamentProperty;
import com.panda.merge.model.ThirdTournamentPropertyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdTournamentPropertyMapper {
    long countByExample(ThirdTournamentPropertyExample example);

    int deleteByExample(ThirdTournamentPropertyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdTournamentProperty record);

    int insertSelective(ThirdTournamentProperty record);

    List<ThirdTournamentProperty> selectByExample(ThirdTournamentPropertyExample example);

    ThirdTournamentProperty selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdTournamentProperty record, @Param("example") ThirdTournamentPropertyExample example);

    int updateByExample(@Param("record") ThirdTournamentProperty record, @Param("example") ThirdTournamentPropertyExample example);

    int updateByPrimaryKeySelective(ThirdTournamentProperty record);

    int updateByPrimaryKey(ThirdTournamentProperty record);
}