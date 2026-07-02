package com.panda.merge.mapper;

import com.panda.merge.model.MatchScoresSpecialEvent;
import com.panda.merge.model.MatchScoresSpecialEventExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScoresSpecialEventMapper {
    long countByExample(MatchScoresSpecialEventExample example);

    int deleteByExample(MatchScoresSpecialEventExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchScoresSpecialEvent record);

    int insertSelective(MatchScoresSpecialEvent record);

    List<MatchScoresSpecialEvent> selectByExample(MatchScoresSpecialEventExample example);

    MatchScoresSpecialEvent selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresSpecialEvent record, @Param("example") MatchScoresSpecialEventExample example);

    int updateByExample(@Param("record") MatchScoresSpecialEvent record, @Param("example") MatchScoresSpecialEventExample example);

    int updateByPrimaryKeySelective(MatchScoresSpecialEvent record);

    int updateByPrimaryKey(MatchScoresSpecialEvent record);
}