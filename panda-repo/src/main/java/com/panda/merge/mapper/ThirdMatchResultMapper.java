package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchResult;
import com.panda.merge.model.ThirdMatchResultExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdMatchResultMapper {
    long countByExample(ThirdMatchResultExample example);

    int deleteByExample(ThirdMatchResultExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdMatchResult record);

    int insertSelective(ThirdMatchResult record);

    List<ThirdMatchResult> selectByExample(ThirdMatchResultExample example);

    ThirdMatchResult selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdMatchResult record, @Param("example") ThirdMatchResultExample example);

    int updateByExample(@Param("record") ThirdMatchResult record, @Param("example") ThirdMatchResultExample example);

    int updateByPrimaryKeySelective(ThirdMatchResult record);

    int updateByPrimaryKey(ThirdMatchResult record);
}