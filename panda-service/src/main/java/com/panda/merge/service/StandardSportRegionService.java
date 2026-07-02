package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.CommonPage;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardSportRegionDTO;
import com.panda.merge.model.StandardSportRegion;

/**
 * 标准区域信息 <br>
 * @author   tell
 * @since    2020年9月10日10:32:26
 */
public interface StandardSportRegionService {

        /**
         * 根据修改时间筛选，分页查询标准区域信息
         * @param  page  分页对象信息
         * @return Page<StandardSportRegionament>
         * */
        Page<StandardSportRegion> getItemPageByModifyTime(PageModel<StandardSportRegionDTO> page);


        StandardSportRegion getStandardSportRegion(Long id);
}
