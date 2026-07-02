package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;
import com.panda.merge.model.ThirdMatchHistoryExpression;
import com.panda.merge.model.ThirdMatchHistoryExpressionExample;
import com.panda.merge.model.ThirdMatchSidelinedExample;

import java.util.List;

/**
 * 三方联赛球队历史表现数据
 * @author tell
 * @since  2020年10月18日09:01:35
 */
public interface ThirdMatchHistoryExpressionService {

    /**
     * 分页查询联赛球队历史表现列表
     * */
    Page<ThirdMatchHistoryExpression> getHistoryExpressionPageByModifyTime(PageModel<QueryThirdRankingInfoDTO> page);


    /**
     * 获取联赛球队历史表现列表
     * @param  ids  id列表
     * @return List<ThirdMatchHistoryExpression>
     * */
    List<ThirdMatchHistoryExpression> getItems(List<String> ids);

    /**
     * 新增或修改
     * @param  item  对象信息
     * @return ThirdMatchHistoryExpression
     * */
    ThirdMatchHistoryExpression saveOrUpdate(ThirdMatchHistoryExpression item);


    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchHistoryExpressionExample example);
}
