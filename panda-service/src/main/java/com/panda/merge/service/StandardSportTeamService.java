package com.panda.merge.service;

import com.panda.merge.dto.StandardSportTeamDetail;
import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.model.ThirdSportTeam;

import java.util.List;

/**
 * 标准球队信息 <br>
 * @author   tell
 * @since    2020年9月10日10:32:26
 */
public interface StandardSportTeamService {

        /**
         * 根据标准赛事ID查询标准球队数据
         * @param  standardMatchId  标准赛事ID
         * @return List<StandardSportTeam>
         * */
        List<StandardSportTeamDetail> getItemByStandardMatchId(Long standardMatchId);


        /**
         * 根据标准球队ID列表查询标准球队数据
         * @param  standardTeamIds  标准赛事ID
         * @return List<StandardSportTeam>
         * */
        List<StandardSportTeamDetail> getItemByStandardTeamIds(List<Long> standardTeamIds);


        /**
         * 新增标准球队信息
         * @param item  对象信息
         * @return StandardSportTeam
         * */
        StandardSportTeamDetail saveItem(StandardSportTeamDetail item,String linkId);

        /**
         * 根据三方球队ID列表查询标准球队数据
         * @param sportId
         * @param thirdTeamId
         * @return
         */
        StandardSportTeam getItemByThirdTeamId(Long sportId, Long thirdTeamId);

        /**
         *  根据数据源、赛种、betRadarId 查询对应的球队
         */
        StandardSportTeam getStandardTeamByBetRadarId(Long sportId, String dataSourceCode, Integer betRadarId);
}
