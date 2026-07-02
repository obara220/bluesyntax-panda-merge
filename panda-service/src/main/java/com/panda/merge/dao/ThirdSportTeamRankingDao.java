package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdSportTeamRanking;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 联赛球队排行榜单数据自定义dao
 * @author     tell
 * @since      2021年4月15日21:52:39
 */
@Repository
public interface ThirdSportTeamRankingDao {


    /**
     * 分页查询联赛下球队排行榜单列表
     * @return  Page<ThirdSportTeamRanking>
     */
    Page<ThirdSportTeamRanking> getItemPageByModifyTime(QueryThirdRankingInfoDTO item);


    List<ThirdSportTeamRanking> getTeamRankingBySeasonIdAndMatchId(QueryThirdRankingInfoDTO item);
}
