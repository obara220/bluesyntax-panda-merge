package com.panda.merge.dao;

import com.github.pagehelper.Page;
import com.panda.merge.dto.StandardSportPlayerDTO;
import com.panda.merge.dto.StandardSportPlayerDetail;
import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.StandardSportPlayerDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标准球员信息自定义dao
 * @author     tell
 * @since      2020年9月4日12:07:19
 */
@Repository
public interface StandardSportPlayerDao {
    /**
     * 根据修改时间分页查询标准球员信息
     */
    Page<StandardSportPlayerDetail> getPageItemGreaterThanOrModifyTime(StandardSportPlayerDTO standardSportPlayerDTO);

    /**
     * 根据赛事id查询标准球员 提供给操盘结算2.0
     * @param standardSportPlayerDTO
     * @return
     */
    List<StandardSportPlayerDo> selectPalyerMyMatchid(StandardSportPlayerDTO standardSportPlayerDTO);

    List<LanguageInternation>  getLanguageInternation(@Param("nameCode") List<String> nameCode);

}
