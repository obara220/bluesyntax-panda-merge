package com.panda.merge.service;

import com.github.pagehelper.Page;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;

import java.util.List;

/**
 * 标准赛事信息 <br>
 *
 * @author tell
 * @since 2020年9月10日10:32:26
 */
public interface StandardMatchInfoService {

    /**
     * 分页查询标准赛事数据，比赛是否结束为（0:未结束，2:临时状态）和比赛开盘标识为（1:开盘，2:关盘，3:封盘）的标准赛事
     *
     * @param page 分页对象信息
     * @return Page<StandardMatchInfo>
     */
    Page<StandardMatchInfoDetail> getStandardMatchInfoPage(PageModel<StandardMatchInfoDTO> page);

    /**
     * 根据修改时间筛选，分页查询标准赛事信息
     *
     * @return Page<StandardMatchInfo>
     */
    Page<StandardMatchInfoDetail> getItemPageByModifyTime(PageModel<StandardMatchInfoDTO> page);

    /**
     * 根据 数据源+标准运动类型+三方数据源赛事ID 获取标准赛事信息
     *
     * @param dataSourceCode 数据来源
     * @param sportId        运动类型
     * @param thirdSourceId  三方数据源赛事ID
     * @return StandardMatchInfo
     */
    StandardMatchInfoDetail getStandardMatchInfoByThirdSourceId(String dataSourceCode, Long sportId, String thirdSourceId);

    /**
     * 根据标准赛事ID获取标准赛事(含联赛级别字段)
     * @param id 标准赛事ID
     * @return StandardMatchInfo
     */
    StandardMatchInfoDetail getDetailItem(Long id);

    /**
     * 根据标准赛事ID获取标准赛事，先查缓存，再查数据库
     * @param id 标准赛事ID
     * @return StandardMatchInfo
     */
    StandardMatchInfo getItem(Long id);

    /**
     * 根据标准赛事IDs获取标准赛事，先查缓存，再查数据库
     * @param ids 标准赛事ID
     * @return List<StandardMatchInfo>
     */
    List<StandardMatchInfo> getItems(List<Long> ids);

    /**
     * 根据标准赛事管理ID获取标准赛事
     * @param MatchManageId 标准赛事ID
     * @return StandardMatchInfo
     */
    StandardMatchInfo getItemByMatchManageId(String MatchManageId);
    /**
     * 根据标准赛事ID获取标准赛事，直接查询数据库
     * @param id 标准赛事ID
     * @return StandardMatchInfo
     */
    StandardMatchInfo getItemByPrimaryKey(Long id);

    List<StandardMatchInfo> getItemByPrimaryKeys(List<Long> ids);

    /**
     * 更新标准赛事信息,只更新有值的数据
     * @param standardMatchInfo 标准赛事信息
     * @return int
     */
    StandardMatchInfo updateByPrimaryKeySelective(StandardMatchInfo standardMatchInfo);


    /**
     * 更新标准赛事信息,只更新有值的数据
     * @param standardMatchInfo 标准赛事信息
     * @return int
     */
    StandardMatchInfo updateByPrimaryKeySelective(StandardMatchInfo standardMatchInfo,String linkId);


    /**
     * 更新标准赛事
     * @param standardMatchInfo  标准赛事信息
     */
    StandardMatchInfo updateByPrimaryKey(StandardMatchInfo standardMatchInfo);

    int updateByExampleSelective(StandardMatchInfo standardMatchInfo, StandardMatchInfoExample example);

    StandardMatchInfo refreshCache(StandardMatchInfo item);
    
    /**
     * 根据玩法Id和赛种Id查询当前未结束的赛事
     * @param categoryId
     * @param sportId
     * @return List<StandardMatchInfo>
     */
    public List<StandardMatchInfo> selectActiveByMarketCategoryIdAndSportId(Long categoryId, Long sportId);

    StandardMatchInfo getItemByPlsStandardMatchId(Long plsStandardMatchId);
}
