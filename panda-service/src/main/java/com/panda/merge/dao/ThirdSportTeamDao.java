package com.panda.merge.dao;

import com.panda.merge.dto.ThirdSportTeamDTO;
import com.panda.merge.dto.ThirdSportTeamDetail;
import com.panda.merge.model.ThirdSportTeam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 三方球队自定义dao
 * @author     tell
 * @since      2020年9月4日12:07:19
 */
@Repository
public interface ThirdSportTeamDao {

    /**
     * 根据三方数据源球队ID获取三方球队信息（含三方球队多语言信息）
     * @param   thirdSportTeamDTO
     * @return  ThirdSportTeamDetail
     */
    ThirdSportTeamDetail getItemByThirdTeamSourceId(ThirdSportTeamDTO thirdSportTeamDTO);


    /**
     * 根据三方库球队ID列表查询三方库球队信息
     * @param   thirdTeamIds
     * @return  List<ThirdSportTeamDetail>
     */
    List<ThirdSportTeamDetail> getItemsByTeamIds(@Param("thirdTeamIds") List<Long> thirdTeamIds);

    /**
     * 批量创建
     */
    int insertList(@Param("list") List<ThirdSportTeam> list);

    /**
     * 批量更新
     */
    int updateList(@Param("list") List<ThirdSportTeam> list);
}
