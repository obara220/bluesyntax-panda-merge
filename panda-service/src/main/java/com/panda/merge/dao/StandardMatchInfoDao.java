package com.panda.merge.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.model.StandardMatchInfo;

/**
 * 标准赛事信息自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface StandardMatchInfoDao {

    /**
     * 分页查询标准赛事数据，比赛是否结束为（0:未结束，2:临时状态）和比赛开盘标识为（1:开盘，2:关盘，3:封盘）的标准赛事
     * @return  Page<StandardMatchInfo>
     */
    Page<StandardMatchInfoDetail> getItemPage();


    /**
     * 根据修改时间筛选，分页查询标准赛事信息
     * @param   standardMatchInfoDTO
     * @return  Page<StandardMatchInfo>
     */
    Page<StandardMatchInfoDetail> getItemPageByModifyTime(StandardMatchInfoDTO standardMatchInfoDTO);

    /**
     * 根据三方数据源赛事信息查询标准赛事
     * @param standardMatchInfoDTO
     *      dataSourceCode   数据来源
     *      thirdSportId     运动类型
     *      thirdSourceId    三方数据源赛事ID
     * @return  StandardMatchInfo
     */
    StandardMatchInfoDetail getItemByThirdSourceId(StandardMatchInfoDTO standardMatchInfoDTO);


    /**
     * 根据标准赛事ID查询标准赛事
     * @param id
     * @return  StandardMatchInfo
     */
    StandardMatchInfoDetail getItemById(Long id);

    /**
     * 根据玩法Id和赛种Id查询当前未结束的赛事
     * @param categoryId
     * @param sportId
     * @return List<StandardMatchInfo>
     */
    List<StandardMatchInfo> selectActiveByMarketCategoryIdAndSportId(@Param("categoryId") Long categoryId, @Param("sportId")Long sportId);

    List<StandardMatchInfo> selectByConfigCashOutItem(String dataSourceCode);
}
