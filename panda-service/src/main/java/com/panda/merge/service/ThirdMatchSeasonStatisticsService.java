package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdMatchSeasonStatistics;
import com.panda.merge.model.ThirdMatchSeasonStatisticsExample;

import java.util.List;

/**
 * 三方联赛赛季统计数据
 * @author tell
 * @since  2020年10月18日09:01:35
 */
public interface ThirdMatchSeasonStatisticsService {

    /**
     * 分页查询联赛赛季统计数据列表
     * */
    Page<ThirdMatchSeasonStatistics> getSeasonStatisticsPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page);

    /**
     * 获取联赛赛季统计数据列表
     * @param  ids  id列表
     * @return List<ThirdMatchSeasonStatistics>
     * */
    List<ThirdMatchSeasonStatistics> getItems(List<String> ids);

    /**
     * 根据三方数据源赛季ID列表获取联赛赛季统计数据列表
     * @param  seasonIds  三方数据源赛季ID列表
     * @return List<ThirdMatchSeasonStatistics>
     * */
    List<ThirdMatchSeasonStatistics> getItemsInSeasonIds(List<String> seasonIds);

    /**
     * 新增或修改
     * @param  item  对象信息
     * @return ThirdMatchSeasonStatistics
     * */
    ThirdMatchSeasonStatistics saveOrUpdate(ThirdMatchSeasonStatistics item);

    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchSeasonStatisticsExample example);

}
