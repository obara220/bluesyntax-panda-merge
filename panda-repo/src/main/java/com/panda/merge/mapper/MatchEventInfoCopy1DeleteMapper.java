package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventInfoCopy1Delete;
import com.panda.merge.model.MatchEventInfoCopy1DeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventInfoCopy1DeleteMapper {
    long countByExample(MatchEventInfoCopy1DeleteExample example);

    int deleteByExample(MatchEventInfoCopy1DeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventInfoCopy1Delete record);

    int insertSelective(MatchEventInfoCopy1Delete record);

    List<MatchEventInfoCopy1Delete> selectByExample(MatchEventInfoCopy1DeleteExample example);

    MatchEventInfoCopy1Delete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventInfoCopy1Delete record, @Param("example") MatchEventInfoCopy1DeleteExample example);

    int updateByExample(@Param("record") MatchEventInfoCopy1Delete record, @Param("example") MatchEventInfoCopy1DeleteExample example);

    int updateByPrimaryKeySelective(MatchEventInfoCopy1Delete record);

    int updateByPrimaryKey(MatchEventInfoCopy1Delete record);
}