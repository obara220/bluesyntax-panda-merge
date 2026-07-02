package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportType;
import com.panda.merge.model.StandardSportTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportTypeMapper {
    long countByExample(StandardSportTypeExample example);

    int deleteByExample(StandardSportTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportType record);

    int insertSelective(StandardSportType record);

    List<StandardSportType> selectByExample(StandardSportTypeExample example);

    StandardSportType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportType record, @Param("example") StandardSportTypeExample example);

    int updateByExample(@Param("record") StandardSportType record, @Param("example") StandardSportTypeExample example);

    int updateByPrimaryKeySelective(StandardSportType record);

    int updateByPrimaryKey(StandardSportType record);
}