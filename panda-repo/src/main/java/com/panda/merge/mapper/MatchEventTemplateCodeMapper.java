package com.panda.merge.mapper;

import com.panda.merge.model.MatchEventTemplateCode;
import com.panda.merge.model.MatchEventTemplateCodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEventTemplateCodeMapper {
    long countByExample(MatchEventTemplateCodeExample example);

    int deleteByExample(MatchEventTemplateCodeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchEventTemplateCode record);

    int insertSelective(MatchEventTemplateCode record);

    List<MatchEventTemplateCode> selectByExample(MatchEventTemplateCodeExample example);

    MatchEventTemplateCode selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchEventTemplateCode record, @Param("example") MatchEventTemplateCodeExample example);

    int updateByExample(@Param("record") MatchEventTemplateCode record, @Param("example") MatchEventTemplateCodeExample example);

    int updateByPrimaryKeySelective(MatchEventTemplateCode record);

    int updateByPrimaryKey(MatchEventTemplateCode record);
}