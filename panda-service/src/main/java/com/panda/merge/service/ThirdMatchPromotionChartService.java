package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchPromotionChart;
import com.panda.merge.model.ThirdMatchPromotionChartExample;

/**
 * 杯赛淘汰赛
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
public interface ThirdMatchPromotionChartService {

    /**
     * 根据修改时间筛选,分页查询
     * @return Page<ThirdMatchPromotionChart>
     */
    Page<ThirdMatchPromotionChart> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page);

    ThirdMatchPromotionChart getItem(String id);

    ThirdMatchPromotionChart saveItem(ThirdMatchPromotionChart item, String linkId);

    ThirdMatchPromotionChart updateItem(ThirdMatchPromotionChart item);

    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchPromotionChartExample example);

}
