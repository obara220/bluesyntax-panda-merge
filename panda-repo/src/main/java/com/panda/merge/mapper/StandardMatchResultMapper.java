package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchResult;
import com.panda.merge.model.StandardMatchResultExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMatchResultMapper {
    long countByExample(StandardMatchResultExample example);

    int deleteByExample(StandardMatchResultExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMatchResult record);

    int insertSelective(StandardMatchResult record);

    List<StandardMatchResult> selectByExample(StandardMatchResultExample example);

    StandardMatchResult selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMatchResult record, @Param("example") StandardMatchResultExample example);

    int updateByExample(@Param("record") StandardMatchResult record, @Param("example") StandardMatchResultExample example);

    int updateByPrimaryKeySelective(StandardMatchResult record);

    int updateByPrimaryKey(StandardMatchResult record);
}