package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchProperty;
import com.panda.merge.model.StandardMatchPropertyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMatchPropertyMapper {
    long countByExample(StandardMatchPropertyExample example);

    int deleteByExample(StandardMatchPropertyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMatchProperty record);

    int insertSelective(StandardMatchProperty record);

    List<StandardMatchProperty> selectByExample(StandardMatchPropertyExample example);

    StandardMatchProperty selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMatchProperty record, @Param("example") StandardMatchPropertyExample example);

    int updateByExample(@Param("record") StandardMatchProperty record, @Param("example") StandardMatchPropertyExample example);

    int updateByPrimaryKeySelective(StandardMatchProperty record);

    int updateByPrimaryKey(StandardMatchProperty record);
}