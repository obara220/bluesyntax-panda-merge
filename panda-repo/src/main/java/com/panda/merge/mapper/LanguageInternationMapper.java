package com.panda.merge.mapper;

import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.LanguageInternationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageInternationMapper {
    long countByExample(LanguageInternationExample example);

    int deleteByExample(LanguageInternationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(LanguageInternation record);

    int insertSelective(LanguageInternation record);

    List<LanguageInternation> selectByExample(LanguageInternationExample example);

    LanguageInternation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") LanguageInternation record, @Param("example") LanguageInternationExample example);

    int updateByExample(@Param("record") LanguageInternation record, @Param("example") LanguageInternationExample example);

    int updateByPrimaryKeySelective(LanguageInternation record);

    int updateByPrimaryKey(LanguageInternation record);
}