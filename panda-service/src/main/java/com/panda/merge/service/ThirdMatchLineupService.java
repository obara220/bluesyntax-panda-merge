package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;
import com.panda.merge.model.ThirdMatchLineup;
import com.panda.merge.model.ThirdMatchLineupExample;

import java.util.List;

/**
 * 赛事首发阵容信息
 * @author      tell
 * @since       2021年2月6日17:50:52
 */
public interface ThirdMatchLineupService {


    /**
     * 根据修改时间筛选,分页查询
     * @return Page<ThirdMatchLineup>
     */
    Page<ThirdMatchLineup> getItemPageByModifyTime(PageModel<ThirdMatchInfoDTO> page);

    /**
     * 根据修改时间筛选
     * @return List<ThirdMatchLineup>
     */
    List<ThirdMatchLineup> getItemByModifyTime(ThirdMatchInfoDTO item);

    /**
     * 获取赛事首发阵容列表
     * @param thirdMatchSourceId
     * @param dataSourceCode
     * @return
     */
    List<ThirdMatchLineup> getItemList(String thirdMatchSourceId,String dataSourceCode);

    /**
     * 获取赛事首发阵容列表
     * @param thirdMatchSourceIds
     * @param dataSourceCode
     * @return
     */
    List<ThirdMatchLineup> getItemList(List<String> thirdMatchSourceIds,String dataSourceCode);

    /**
     * 根据Id获取信息
     * @param id
     * @return
     */
    ThirdMatchLineup getItem(String id);

    ThirdMatchLineup getItemByPrimaryKey(String id);

    ThirdMatchLineup saveItem(ThirdMatchLineup item,String linkId);

    ThirdMatchLineup updateItem(ThirdMatchLineup item);

    /**
     *  通过主键更新部分数据
     *  @param ThirdMatchLineup
     */
    ThirdMatchLineup updateByPrimaryKeySelective(ThirdMatchLineup ThirdMatchLineup);

    /**
     *  刷新缓存
     * @param ThirdMatchLineup
     */
    ThirdMatchLineup refreshCache(ThirdMatchLineup ThirdMatchLineup);


    void delItemById(String id);


    /**
     * 根据条件修改当前数据的修改时间
     * @param modifyTime 时间戳
     * @param example    修改条件
     * @return int 成功条数
     */
    int updateModifyTimeByExampleSelective(Long modifyTime, ThirdMatchLineupExample example);
}
