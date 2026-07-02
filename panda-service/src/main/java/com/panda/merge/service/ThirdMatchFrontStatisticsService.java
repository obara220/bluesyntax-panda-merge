package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchFrontStatistics;
import com.panda.merge.model.ThirdMatchFrontStatisticsExample;

/**
 * 三方赛事正面交手数据
 * @author      tell
 * @since       2021年4月23日13:44:33
 */
public interface ThirdMatchFrontStatisticsService {


    /**
     * 根据修改时间筛选,分页查询
     * @return Page<ThirdMatchFrontStatistics>
     */
    Page<ThirdMatchFrontStatistics> getFrontStatisticsPageByModifyTime(PageModel<ThirdMatchInfoDTO> page);

    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchFrontStatisticsExample example);

    void delItemById(String id);
}
