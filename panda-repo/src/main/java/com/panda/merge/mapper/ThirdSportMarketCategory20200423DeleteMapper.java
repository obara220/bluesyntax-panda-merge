package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportMarketCategory20200423Delete;
import com.panda.merge.model.ThirdSportMarketCategory20200423DeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportMarketCategory20200423DeleteMapper {
    long countByExample(ThirdSportMarketCategory20200423DeleteExample example);

    int deleteByExample(ThirdSportMarketCategory20200423DeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportMarketCategory20200423Delete record);

    int insertSelective(ThirdSportMarketCategory20200423Delete record);

    List<ThirdSportMarketCategory20200423Delete> selectByExample(ThirdSportMarketCategory20200423DeleteExample example);

    ThirdSportMarketCategory20200423Delete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportMarketCategory20200423Delete record, @Param("example") ThirdSportMarketCategory20200423DeleteExample example);

    int updateByExample(@Param("record") ThirdSportMarketCategory20200423Delete record, @Param("example") ThirdSportMarketCategory20200423DeleteExample example);

    int updateByPrimaryKeySelective(ThirdSportMarketCategory20200423Delete record);

    int updateByPrimaryKey(ThirdSportMarketCategory20200423Delete record);
}