package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventCommon;
import com.panda.merge.model.MatchEventCommonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventCommonMapper {
    long countByExample(MatchEventCommonExample example);

    int deleteByExample(MatchEventCommonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventCommon record);

    int insertSelective(MatchEventCommon record);

    List<MatchEventCommon> selectByExample(MatchEventCommonExample example);

    MatchEventCommon selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventCommon record, @Param("example") MatchEventCommonExample example);

    int updateByExample(@Param("record") MatchEventCommon record, @Param("example") MatchEventCommonExample example);

    int updateByPrimaryKeySelective(MatchEventCommon record);

    int updateByPrimaryKey(MatchEventCommon record);
}