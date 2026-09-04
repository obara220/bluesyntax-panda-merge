package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.StandardTournamentRuleDTO;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdSportTournament;
import org.springframework.stereotype.Repository;

/**
 * 三方联赛信息自定义dao
 * @author     tell
 * @since      2021年4月15日18:15:35
 */
@Repository
public interface ThirdSportTournamentDao {


    /**
     * 根据修改时间筛选，分页查询三方联赛信息
     * @return  Page<ThirdSportTournament>
     */
    Page<ThirdSportTournament> getItemPageByModifyTime(QueryThirdRankingInfoDTO queryThirdRankingInfoDTO);


    Page<ThirdSportTournament> getTournamentRulePage(StandardTournamentRuleDTO query);
}
