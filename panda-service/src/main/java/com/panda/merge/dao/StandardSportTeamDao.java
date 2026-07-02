package com.panda.merge.dao;


import com.panda.merge.dto.StandardSportTeamDetail;
import com.panda.merge.model.StandardSportTeam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标准球队信息自定义dao
 * @author     tell
 * @since      2020年9月10日09:44:48
 */
@Repository
public interface StandardSportTeamDao {

    /**
     * 根据标准赛事ID查询标准球队数据（含多语言，和赛事球队关系）
     * @return  Page<StandardMatchInfo>
     */
    List<StandardSportTeamDetail> getItemByStandardMatchId(@Param("standardMatchId") Long standardMatchId);


    /**
     * 根据标准球队D列表查询标准球队数据（含多语言）
     * @return  Page<StandardSportTeamDetail>
     */
    List<StandardSportTeamDetail> getItemByStandardTeamIds(@Param("standardTeamIds") List<Long> standardTeamIds);

    /**
     * 新增球队信息，并返回主键
     * */
    int saveItem(StandardSportTeam item);

}
