package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventTemplate;
import com.panda.merge.model.MatchEventTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventTemplateMapper {
    long countByExample(MatchEventTemplateExample example);

    int deleteByExample(MatchEventTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventTemplate record);

    int insertSelective(MatchEventTemplate record);

    List<MatchEventTemplate> selectByExample(MatchEventTemplateExample example);

    MatchEventTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventTemplate record, @Param("example") MatchEventTemplateExample example);

    int updateByExample(@Param("record") MatchEventTemplate record, @Param("example") MatchEventTemplateExample example);

    int updateByPrimaryKeySelective(MatchEventTemplate record);

    int updateByPrimaryKey(MatchEventTemplate record);
}