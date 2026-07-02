package com.panda.merge.mapper;

import com.panda.merge.model.StandardOutrightMatchCategory;
import com.panda.merge.model.StandardOutrightMatchCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardOutrightMatchCategoryMapper {
    long countByExample(StandardOutrightMatchCategoryExample example);

    int deleteByExample(StandardOutrightMatchCategoryExample example);

    int insert(StandardOutrightMatchCategory record);

    int insertSelective(StandardOutrightMatchCategory record);

    List<StandardOutrightMatchCategory> selectByExample(StandardOutrightMatchCategoryExample example);

    int updateByExampleSelective(@Param("record") StandardOutrightMatchCategory record, @Param("example") StandardOutrightMatchCategoryExample example);

    int updateByExample(@Param("record") StandardOutrightMatchCategory record, @Param("example") StandardOutrightMatchCategoryExample example);
}