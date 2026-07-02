package com.panda.merge.mapper;

import com.panda.merge.model.LanguageType;
import com.panda.merge.model.LanguageTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageTypeMapper {
    long countByExample(LanguageTypeExample example);

    int deleteByExample(LanguageTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(LanguageType record);

    int insertSelective(LanguageType record);

    List<LanguageType> selectByExample(LanguageTypeExample example);

    LanguageType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") LanguageType record, @Param("example") LanguageTypeExample example);

    int updateByExample(@Param("record") LanguageType record, @Param("example") LanguageTypeExample example);

    int updateByPrimaryKeySelective(LanguageType record);

    int updateByPrimaryKey(LanguageType record);
}