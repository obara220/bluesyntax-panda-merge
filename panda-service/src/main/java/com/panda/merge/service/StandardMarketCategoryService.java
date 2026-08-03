package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardMarketCategoryDetail;
import com.panda.merge.dto.StandardSportMarketCategoryDTO;
import com.panda.merge.model.StandardMarketCategory;

/**
 *  标准玩法信息
 * @author     tell
 * @since     2020年10月7日09:47:37
 */
public interface StandardMarketCategoryService {

    /**
     * 根据修改时间筛选，分页查询标准玩法玩信息
     * @param  page  分页对象信息
     * @return Page<StandardMarketCategoryChild>
     * */
    Page<StandardMarketCategoryDetail> getItemPageByModifyTime(PageModel<StandardSportMarketCategoryDTO> page);

    StandardMarketCategory getItemById(Long id);

    /**
     * 清理全量缓存
     * */
    int delRedisByAll();
}
