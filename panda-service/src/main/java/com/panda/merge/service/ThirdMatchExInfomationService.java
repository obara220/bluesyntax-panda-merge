package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchExInfomation;
import com.panda.merge.model.ThirdMatchExInfomationExample;

/**
 * 三方赛事比赛情报综合资讯数据
 * @author      tell
 * @since       2021年4月23日13:44:33
 */
public interface ThirdMatchExInfomationService {


    /**
     * 根据修改时间筛选,分页查询
     * @return Page<ThirdMatchExInfomation>
     */
    Page<ThirdMatchExInfomation> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page);

    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchExInfomationExample example);

    void delItemById(String id);
}
