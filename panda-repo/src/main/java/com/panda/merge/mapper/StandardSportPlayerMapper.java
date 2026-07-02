package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportPlayer;
import com.panda.merge.model.StandardSportPlayerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportPlayerMapper {
    long countByExample(StandardSportPlayerExample example);

    int deleteByExample(StandardSportPlayerExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportPlayer record);

    int insertSelective(StandardSportPlayer record);

    List<StandardSportPlayer> selectByExampleWithBLOBs(StandardSportPlayerExample example);

    List<StandardSportPlayer> selectByExample(StandardSportPlayerExample example);

    StandardSportPlayer selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportPlayer record, @Param("example") StandardSportPlayerExample example);

    int updateByExampleWithBLOBs(@Param("record") StandardSportPlayer record, @Param("example") StandardSportPlayerExample example);

    int updateByExample(@Param("record") StandardSportPlayer record, @Param("example") StandardSportPlayerExample example);

    int updateByPrimaryKeySelective(StandardSportPlayer record);

    int updateByPrimaryKeyWithBLOBs(StandardSportPlayer record);

    int updateByPrimaryKey(StandardSportPlayer record);
}