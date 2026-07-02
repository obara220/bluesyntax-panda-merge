package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchPromotionChart;
import org.springframework.stereotype.Repository;

/**
 * 杯赛淘汰赛
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Repository
public interface ThirdMatchPromotionChartDao {


    /**
     * 根据修改时间筛选，分页查询
     * @param   thirdMatchInfoDTO
     * @return  Page<ThirdMatchPromotionChart>
     */
    Page<ThirdMatchPromotionChart> getItemPageByModifyTime(ThirdMatchInfoDTO thirdMatchInfoDTO);


}
