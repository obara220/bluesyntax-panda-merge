package com.panda.merge.mapper;

import com.panda.merge.dto.scores.B02ScoresSourceDTO;
import com.panda.merge.model.B02ScoresSource;
import com.panda.merge.model.CategoryDatasourcecodeChangeExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface B02ScoresSourceMapper {

    /**
     * 新增
     **/
    int insert(B02ScoresSource b02ScoresSource);
    /**
     * 更新
     **/
    int update(@Param("query") B02ScoresSourceDTO b02ScoresSource);

    /**
     * 查询 分页查询
     * @date 2023/11/24
     **/
    List<B02ScoresSource> pageList(@Param("query") B02ScoresSourceDTO query);

    /**
     *
     * @param sportId
     * @return
     */
    List<B02ScoresSource> queryBySportId(@Param("sportId") Long sportId);

    /**
     * 查询 分页查询 count
     * @date 2023/11/24
     **/
    int pageListCount(@Param("query") B02ScoresSourceDTO query);

    int delete(@Param("obj") B02ScoresSourceDTO query);
}