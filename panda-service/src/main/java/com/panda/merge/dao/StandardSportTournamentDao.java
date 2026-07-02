package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.StandardSportTournamentDetail;
import com.panda.merge.dto.StandardSportTournamentDTO;
import org.springframework.stereotype.Repository;

/**
 * 标准联赛信息自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface StandardSportTournamentDao {

    /**
     * 根据修改时间筛选，分页查询标准联赛信息（含多语言）
     * @param   standardSportTournamentDTO
     * @return  Page<StandardSportTournamentChild>
     */
    Page<StandardSportTournamentDetail> getItemPageByModifyTime(StandardSportTournamentDTO standardSportTournamentDTO);



}
