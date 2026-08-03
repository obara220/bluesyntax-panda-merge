package com.panda.merge.mapper;

import com.panda.merge.model.MatchGrayInterval;
import com.panda.merge.model.MatchGrayIntervalExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchGrayIntervalMapper {
    long countByExample(MatchGrayIntervalExample example);

    int deleteByExample(MatchGrayIntervalExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MatchGrayInterval record);

    int insertSelective(MatchGrayInterval record);

    List<MatchGrayInterval> selectByExample(MatchGrayIntervalExample example);

    MatchGrayInterval selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MatchGrayInterval record, @Param("example") MatchGrayIntervalExample example);

    int updateByExample(@Param("record") MatchGrayInterval record, @Param("example") MatchGrayIntervalExample example);

    int updateByPrimaryKeySelective(MatchGrayInterval record);

    int updateByPrimaryKey(MatchGrayInterval record);
}