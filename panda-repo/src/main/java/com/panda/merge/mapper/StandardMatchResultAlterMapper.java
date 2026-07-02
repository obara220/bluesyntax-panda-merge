package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchResultAlter;
import com.panda.merge.model.StandardMatchResultAlterExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardMatchResultAlterMapper {
    long countByExample(StandardMatchResultAlterExample example);

    int deleteByExample(StandardMatchResultAlterExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMatchResultAlter record);

    int insertSelective(StandardMatchResultAlter record);

    List<StandardMatchResultAlter> selectByExample(StandardMatchResultAlterExample example);

    StandardMatchResultAlter selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMatchResultAlter record, @Param("example") StandardMatchResultAlterExample example);

    int updateByExample(@Param("record") StandardMatchResultAlter record, @Param("example") StandardMatchResultAlterExample example);

    int updateByPrimaryKeySelective(StandardMatchResultAlter record);

    int updateByPrimaryKey(StandardMatchResultAlter record);
}