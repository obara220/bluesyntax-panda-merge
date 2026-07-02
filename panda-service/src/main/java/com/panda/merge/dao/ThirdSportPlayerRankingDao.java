package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdSportPlayerRanking;
import org.springframework.stereotype.Repository;

/**
 * 联赛球员排行榜单数据自定义dao
 * @author     tell
 * @since      2021年4月15日21:52:39
 */
@Repository
public interface ThirdSportPlayerRankingDao {


    /**
     * 分页查询联赛下球员排行榜单列表
     * @return  Page<ThirdSportPlayerRanking>
     */
    Page<ThirdSportPlayerRanking> getItemPageByModifyTime(QueryThirdRankingInfoDTO item);



}
