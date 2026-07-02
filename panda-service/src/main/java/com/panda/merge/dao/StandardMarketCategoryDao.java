package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.StandardMarketCategoryDetail;
import com.panda.merge.dto.StandardSportMarketCategoryDTO;
import org.springframework.stereotype.Repository;

/**
 * 标准玩法信息自定义dao
 * @author     tell
 * @since      2020年10月7日09:46:54
 */
@Repository
public interface StandardMarketCategoryDao {

    /**
     * 根据修改时间筛选，分页查询标准标准玩法信息（含多语言）
     * @param   StandardSportMarketCategoryDTO
     * @return  Page<StandardMarketCategoryDetail>
     */
    Page<StandardMarketCategoryDetail> getItemPageByModifyTime(StandardSportMarketCategoryDTO StandardSportMarketCategoryDTO);



}
