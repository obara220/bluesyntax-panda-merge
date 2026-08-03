package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchResultHis;
import com.panda.merge.model.StandardMatchResultHisExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardMatchResultHisMapper {
    long countByExample(StandardMatchResultHisExample example);

    int deleteByExample(StandardMatchResultHisExample example);

    int deleteByPrimaryKey(@Param("id") Long id, @Param("ptMonth") Integer ptMonth);

    int insert(StandardMatchResultHis record);

    int insertSelective(StandardMatchResultHis record);

    List<StandardMatchResultHis> selectByExample(StandardMatchResultHisExample example);

    StandardMatchResultHis selectByPrimaryKey(@Param("id") Long id, @Param("ptMonth") Integer ptMonth);

    int updateByExampleSelective(@Param("record") StandardMatchResultHis record, @Param("example") StandardMatchResultHisExample example);

    int updateByExample(@Param("record") StandardMatchResultHis record, @Param("example") StandardMatchResultHisExample example);

    int updateByPrimaryKeySelective(StandardMatchResultHis record);

    int updateByPrimaryKey(StandardMatchResultHis record);
}