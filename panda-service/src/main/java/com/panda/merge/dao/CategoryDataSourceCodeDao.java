package com.panda.merge.dao;

import com.panda.merge.model.CategoryDatasourcecodeChange;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDataSourceCodeDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<CategoryDatasourcecodeChange> categoryDatasourcecodeChanges);
}
