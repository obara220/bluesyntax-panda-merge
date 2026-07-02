package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdSportPlayerRanking;
import com.panda.merge.model.ThirdSportPlayerRankingExample;

import java.util.List;

/**
 * 联赛下球员排行榜单(泰森独有)
 * @author tell
 * @since  2020年10月18日09:01:35
 */
public interface ThirdSportPlayerRankingService {

    /**
     * 分页查询联赛下球员排行榜单列表
     * */
    Page<ThirdSportPlayerRanking> getItemPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page);

    /**
     * 联赛下球队排行榜单列表
     * @param  ids  id列表
     * @return List<ThirdSportPlayerRanking>
     * */
    List<ThirdSportPlayerRanking> getItems(List<String> ids);

    /**
     * 根据三方数据源赛季ID列表获取联赛下球队排行榜单列表
     * @param  seasonIds      三方数据源赛季ID列表
     * @return List<ThirdSportTeamRanking>
     * */
    List<ThirdSportPlayerRanking> getItemsInSeasonIds(List<String> seasonIds);

    /**
     * 新增或修改
     * @param  item  对象信息
     * @return ThirdSportPlayerRanking
     * */
    ThirdSportPlayerRanking saveOrUpdate(ThirdSportPlayerRanking item,String linkId);


    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdSportPlayerRankingExample example);
}
