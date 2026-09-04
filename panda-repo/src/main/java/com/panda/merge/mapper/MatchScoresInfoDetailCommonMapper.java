package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresInfoDetailCommon;
import com.panda.merge.model.MatchScoresInfoDetailCommonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScoresInfoDetailCommonMapper {
    long countByExample(MatchScoresInfoDetailCommonExample example);

    int deleteByExample(MatchScoresInfoDetailCommonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchScoresInfoDetailCommon record);

    int insertSelective(MatchScoresInfoDetailCommon record);

    List<MatchScoresInfoDetailCommon> selectByExample(MatchScoresInfoDetailCommonExample example);

    MatchScoresInfoDetailCommon selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresInfoDetailCommon record, @Param("example") MatchScoresInfoDetailCommonExample example);

    int updateByExample(@Param("record") MatchScoresInfoDetailCommon record, @Param("example") MatchScoresInfoDetailCommonExample example);

    int updateByPrimaryKeySelective(MatchScoresInfoDetailCommon record);

    int updateByPrimaryKey(MatchScoresInfoDetailCommon record);
}