package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdMatchHistoryExpression;
import org.springframework.stereotype.Repository;

/**
 * 三方联赛球队历史表现数据
 * @author tell
 * @since  2021年4月23日13:46:21
 */
@Repository
public interface ThirdMatchHistoryExpressionDao {


    /**
     * 根据修改时间筛选，分页查询
     * @param   item
     * @return  Page<ThirdMatchHistoryExpression>
     */
    Page<ThirdMatchHistoryExpression> getHistoryExpressionPageByModifyTime(QueryThirdRankingInfoDTO item);


}
