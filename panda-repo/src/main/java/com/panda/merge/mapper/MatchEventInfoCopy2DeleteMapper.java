package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventInfoCopy2Delete;
import com.panda.merge.model.MatchEventInfoCopy2DeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventInfoCopy2DeleteMapper {
    long countByExample(MatchEventInfoCopy2DeleteExample example);

    int deleteByExample(MatchEventInfoCopy2DeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventInfoCopy2Delete record);

    int insertSelective(MatchEventInfoCopy2Delete record);

    List<MatchEventInfoCopy2Delete> selectByExample(MatchEventInfoCopy2DeleteExample example);

    MatchEventInfoCopy2Delete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventInfoCopy2Delete record, @Param("example") MatchEventInfoCopy2DeleteExample example);

    int updateByExample(@Param("record") MatchEventInfoCopy2Delete record, @Param("example") MatchEventInfoCopy2DeleteExample example);

    int updateByPrimaryKeySelective(MatchEventInfoCopy2Delete record);

    int updateByPrimaryKey(MatchEventInfoCopy2Delete record);
}