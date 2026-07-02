package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.model.ThirdMatchHistoryStatistics;
import com.panda.merge.model.ThirdMatchHistoryStatisticsExample;

/**
 * 三方赛事历史统计信息
 * @author tell
 * @since  2021年2月10日17:20:13
 */
public interface ThirdMatchHistoryStatisticsService {

    /**
     * 根据修改时间筛选，分页查询
     * @return Page<StandardMatchInfo>
     */
    Page<ThirdMatchHistoryStatistics> getItemPageByModifyTime(PageModel<StandardMatchInfoDTO> page);

    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchHistoryStatisticsExample example);
}
