package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.ThirdSportMarketDTO;
import com.panda.merge.model.ThirdSportMarket;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 三方盘口信息自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface ThirdSportMarketDao {

    /**
     * 根据修改时间筛选，分页查询三方盘口信息（含多语言）
     * @param   ThirdSportMarketDTO
     * @return  Page<ThirdSportMarket>
     */
    Page<ThirdSportMarket> getItemPageByModifyTime(ThirdSportMarketDTO ThirdSportMarketDTO);

    List<ThirdSportMarket> selectThirdSportMarketList(Map<String, Long> map);



}
