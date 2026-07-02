package com.panda.merge.mapper;

import com.panda.merge.model.TournamenMatchMap;
import com.panda.merge.model.TournamenMatchMapExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamenMatchMapMapper {
    long countByExample(TournamenMatchMapExample example);

    int deleteByExample(TournamenMatchMapExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TournamenMatchMap record);

    int insertSelective(TournamenMatchMap record);

    List<TournamenMatchMap> selectByExample(TournamenMatchMapExample example);

    TournamenMatchMap selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TournamenMatchMap record, @Param("example") TournamenMatchMapExample example);

    int updateByExample(@Param("record") TournamenMatchMap record, @Param("example") TournamenMatchMapExample example);

    int updateByPrimaryKeySelective(TournamenMatchMap record);

    int updateByPrimaryKey(TournamenMatchMap record);
}