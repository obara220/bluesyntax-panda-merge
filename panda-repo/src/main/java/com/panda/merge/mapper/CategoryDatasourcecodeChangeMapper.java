package com.panda.merge.mapper;

import com.panda.merge.model.CategoryDatasourcecodeChange;
import com.panda.merge.model.CategoryDatasourcecodeChangeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDatasourcecodeChangeMapper {
    long countByExample(CategoryDatasourcecodeChangeExample example);

    int deleteByExample(CategoryDatasourcecodeChangeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CategoryDatasourcecodeChange record);

    int insertSelective(CategoryDatasourcecodeChange record);

    List<CategoryDatasourcecodeChange> selectByExample(CategoryDatasourcecodeChangeExample example);

    CategoryDatasourcecodeChange selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CategoryDatasourcecodeChange record, @Param("example") CategoryDatasourcecodeChangeExample example);

    int updateByExample(@Param("record") CategoryDatasourcecodeChange record, @Param("example") CategoryDatasourcecodeChangeExample example);

    int updateByPrimaryKeySelective(CategoryDatasourcecodeChange record);

    int updateByPrimaryKey(CategoryDatasourcecodeChange record);
}