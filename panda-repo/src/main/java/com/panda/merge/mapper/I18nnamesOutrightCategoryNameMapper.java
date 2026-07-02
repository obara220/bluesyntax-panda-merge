package com.panda.merge.mapper;

import com.panda.merge.model.I18nnamesOutrightCategoryName;
import com.panda.merge.model.I18nnamesOutrightCategoryNameExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nnamesOutrightCategoryNameMapper {
    long countByExample(I18nnamesOutrightCategoryNameExample example);

    int deleteByExample(I18nnamesOutrightCategoryNameExample example);

    int deleteByPrimaryKey(Long id);

    int insert(I18nnamesOutrightCategoryName record);

    int insertSelective(I18nnamesOutrightCategoryName record);

    List<I18nnamesOutrightCategoryName> selectByExample(I18nnamesOutrightCategoryNameExample example);

    I18nnamesOutrightCategoryName selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") I18nnamesOutrightCategoryName record, @Param("example") I18nnamesOutrightCategoryNameExample example);

    int updateByExample(@Param("record") I18nnamesOutrightCategoryName record, @Param("example") I18nnamesOutrightCategoryNameExample example);

    int updateByPrimaryKeySelective(I18nnamesOutrightCategoryName record);

    int updateByPrimaryKey(I18nnamesOutrightCategoryName record);
}