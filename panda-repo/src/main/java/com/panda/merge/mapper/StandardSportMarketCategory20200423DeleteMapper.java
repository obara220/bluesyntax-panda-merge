package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportMarketCategory20200423Delete;
import com.panda.merge.model.StandardSportMarketCategory20200423DeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportMarketCategory20200423DeleteMapper {
    long countByExample(StandardSportMarketCategory20200423DeleteExample example);

    int deleteByExample(StandardSportMarketCategory20200423DeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportMarketCategory20200423Delete record);

    int insertSelective(StandardSportMarketCategory20200423Delete record);

    List<StandardSportMarketCategory20200423Delete> selectByExample(StandardSportMarketCategory20200423DeleteExample example);

    StandardSportMarketCategory20200423Delete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportMarketCategory20200423Delete record, @Param("example") StandardSportMarketCategory20200423DeleteExample example);

    int updateByExample(@Param("record") StandardSportMarketCategory20200423Delete record, @Param("example") StandardSportMarketCategory20200423DeleteExample example);

    int updateByPrimaryKeySelective(StandardSportMarketCategory20200423Delete record);

    int updateByPrimaryKey(StandardSportMarketCategory20200423Delete record);
}