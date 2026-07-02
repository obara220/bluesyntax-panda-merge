package com.panda.merge.dao;


import com.github.pagehelper.Page;
import com.panda.merge.dto.StandardSportRegionDTO;
import com.panda.merge.model.StandardSportRegion;
import org.springframework.stereotype.Repository;

/**
 * 标准区域信息自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface StandardSportRegionDao {

    /**
     * 根据修改时间筛选，分页查询标准区域信息（含多语言）
     * @param   standardSportRegionDTO
     * @return  Page<StandardSportRegion>
     */
    Page<StandardSportRegion> getItemPageByModifyTime(StandardSportRegionDTO standardSportRegionDTO);



}
