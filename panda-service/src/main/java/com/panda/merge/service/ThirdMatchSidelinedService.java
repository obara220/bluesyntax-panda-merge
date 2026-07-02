package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchSidelined;
import com.panda.merge.model.ThirdMatchSidelinedExample;

/**
 * 三方赛事伤停球员信息
 * @author      tell
 * @since       2021年4月22日16:18:56
 */
public interface ThirdMatchSidelinedService {


    /**
     * 根据修改时间筛选,分页查询
     * @return Page<ThirdMatchSidelined>
     */
    Page<ThirdMatchSidelined> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page);


    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchSidelinedExample example);

    void delItemById(String id);
}
