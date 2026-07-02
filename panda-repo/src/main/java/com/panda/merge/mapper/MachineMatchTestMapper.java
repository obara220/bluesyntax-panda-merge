package com.panda.merge.mapper;

import com.panda.merge.model.MachineMatchTest;
import com.panda.merge.model.MachineMatchTestExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineMatchTestMapper {
    long countByExample(MachineMatchTestExample example);

    int deleteByExample(MachineMatchTestExample example);

    int deleteByPrimaryKey(@Param("id") Long id, @Param("relationId") String relationId, @Param("thirdMatchId") String thirdMatchId);

    int insert(MachineMatchTest record);

    int insertSelective(MachineMatchTest record);

    List<MachineMatchTest> selectByExample(MachineMatchTestExample example);

    MachineMatchTest selectByPrimaryKey(@Param("id") Long id, @Param("relationId") String relationId, @Param("thirdMatchId") String thirdMatchId);

    int updateByExampleSelective(@Param("record") MachineMatchTest record, @Param("example") MachineMatchTestExample example);

    int updateByExample(@Param("record") MachineMatchTest record, @Param("example") MachineMatchTestExample example);

    int updateByPrimaryKeySelective(MachineMatchTest record);

    int updateByPrimaryKey(MachineMatchTest record);
}