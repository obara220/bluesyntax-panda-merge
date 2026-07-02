package com.panda.merge.dao;

import com.panda.merge.dto.ThirdSportPlayerDetail;
import com.panda.merge.model.ThirdSportPlayer;
import com.panda.merge.model.ThirdSportTeam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 球队球员自定义dao
 * @author     tell
 * @since      2020年9月4日12:07:19
 */
@Repository
public interface ThirdSportPlayerDao {


    /**
     * 根据数据来源，运动类型，三方库球队ID 获取 三方库人员列表
     * @param team      球队参数
     *   teamId           三方库球队ID（必填）
     *   dataSourceCode   数据来源（非必填）
     *   sportId          运动类型（非必填）
     * @return  List<ThirdSportPlayer>
     * */
    List<ThirdSportPlayerDetail> getItemsByTeamId(@Param("team") ThirdSportTeam team);

    /**
     * 批量创建
     * @param   list
     * @return  int
     */
    int insertList(@Param("list") List<ThirdSportPlayer> list);

    /**
     * 批量更新
     * @param   list
     * @return  int
     */
    int updateList(@Param("list") List<ThirdSportPlayer> list);
}
