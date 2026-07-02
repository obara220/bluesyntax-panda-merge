package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchResultAmend;
import com.panda.merge.model.StandardMatchResultAmendExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMatchResultAmendMapper {
    long countByExample(StandardMatchResultAmendExample example);

    int deleteByExample(StandardMatchResultAmendExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMatchResultAmend record);

    int insertSelective(StandardMatchResultAmend record);

    List<StandardMatchResultAmend> selectByExample(StandardMatchResultAmendExample example);

    StandardMatchResultAmend selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMatchResultAmend record, @Param("example") StandardMatchResultAmendExample example);

    int updateByExample(@Param("record") StandardMatchResultAmend record, @Param("example") StandardMatchResultAmendExample example);

    int updateByPrimaryKeySelective(StandardMatchResultAmend record);

    int updateByPrimaryKey(StandardMatchResultAmend record);
}