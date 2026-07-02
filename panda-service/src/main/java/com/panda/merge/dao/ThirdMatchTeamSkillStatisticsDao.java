package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchTeamSkillStatistics;
import org.springframework.stereotype.Repository;

/**
 * 赛事球队技术统计
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Repository
public interface ThirdMatchTeamSkillStatisticsDao {


    /**
     * 根据修改时间筛选，分页查询
     * @param   thirdMatchInfoDTO
     * @return  Page<ThirdMatchTeamSkillStatistics>
     */
    Page<ThirdMatchTeamSkillStatistics> getItemPageByModifyTime(ThirdMatchInfoDTO thirdMatchInfoDTO);


}
