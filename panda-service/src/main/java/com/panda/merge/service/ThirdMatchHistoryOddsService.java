package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchHistoryOdds;

/**
 * 赛事百家赔信息
 * @author      tell
 * @since       2021年4月22日16:18:56
 */
public interface ThirdMatchHistoryOddsService {


    /**
     * 根据修改时间筛选,分页查询
     * @return Page<ThirdMatchHistoryOdds>
     */
    Page<ThirdMatchHistoryOdds> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page);

    void delItemById(String id);
}
