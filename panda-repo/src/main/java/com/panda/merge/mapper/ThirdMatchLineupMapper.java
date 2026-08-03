package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchLineup;
import com.panda.merge.model.ThirdMatchLineupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchLineupMapper {
    long countByExample(ThirdMatchLineupExample example);

    int deleteByExample(ThirdMatchLineupExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdMatchLineup record);

    int insertSelective(ThirdMatchLineup record);

    List<ThirdMatchLineup> selectByExample(ThirdMatchLineupExample example);

    ThirdMatchLineup selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdMatchLineup record, @Param("example") ThirdMatchLineupExample example);

    int updateByExample(@Param("record") ThirdMatchLineup record, @Param("example") ThirdMatchLineupExample example);

    int updateByPrimaryKeySelective(ThirdMatchLineup record);

    int updateByPrimaryKey(ThirdMatchLineup record);
}