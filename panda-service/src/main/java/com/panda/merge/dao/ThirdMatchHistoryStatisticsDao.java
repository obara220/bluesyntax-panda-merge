package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.model.ThirdMatchHistoryStatistics;
import org.springframework.stereotype.Repository;

/**
 * 三方赛事历史统计信息
 * @author tell
 * @since  2021年2月10日17:20:13
 */
@Repository
public interface ThirdMatchHistoryStatisticsDao {


    /**
     * 根据修改时间筛选，分页查询标准赛事信息
     * @param   standardMatchInfoDTO
     * @return  Page<ThirdMatchHistoryStatistics>
     */
    Page<ThirdMatchHistoryStatistics> getItemPageByModifyTime(StandardMatchInfoDTO standardMatchInfoDTO);


}
