package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdSportTeamRanking;
import com.panda.merge.model.ThirdSportTeamRankingExample;

import java.util.List;

/**
 * 联赛下球队排行榜单(泰森独有)
 * @author tell
 * @since  2020年10月18日09:01:35
 */
public interface ThirdSportTeamRankingService {

    /**
     * 分页查询联赛下球队排行榜单列表
     * */
    Page<ThirdSportTeamRanking> getItemPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page);

    /**
     * 根据三方赛事ID和数据源赛季ID获取球队榜单
     * */
    public List<ThirdSportTeamRanking> getTeamRankingBySeasonIdAndMatchId(QueryThirdRankingInfoDTO dto);

    /**
     * 获取联赛下球队排行榜单列表
     * @param  ids  id列表
     * @return List<ThirdSportTeamRanking>
     * */
    List<ThirdSportTeamRanking> getItems(List<String> ids);

    /**
     * 根据三方数据源赛季ID列表获取联赛下球队排行榜单列表
     * @param  seasonIds  三方数据源赛季ID列表
     * @return List<ThirdSportTeamRanking>
     * */
    List<ThirdSportTeamRanking> getItemsInSeasonIds(List<String> seasonIds);

    /**
     * 新增或修改
     * @param  item  对象信息
     * @return ThirdSportTeamRanking
     * */
    ThirdSportTeamRanking saveTeamRanking(ThirdSportTeamRanking item,String linkId);

    ThirdSportTeamRanking updateTeamRanking(ThirdSportTeamRanking upItem,String linkId);


    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdSportTeamRankingExample example);
}
