package com.panda.merge.service;

import com.panda.merge.model.ThirdSportSeason;
import com.panda.merge.model.ThirdSportTournament;

/**
 * @Author Kepa
 * @Date 2021/2/10 15:36
 * @Version 1.0
 */
public interface ThirdSportSeasonService {

    /**
     * 根据 数据源+标准运动类型+三方数据源赛季ID 获取三方赛季信息
     * @param dataSourceCode   数据来源
     * @param sportId          运动类型
     * @param thirdSourceSeasonId          三方数据源赛季ID
     * @return ThirdSportSeason
     * */
    ThirdSportSeason getOneItem(String dataSourceCode, Long sportId, String thirdSourceSeasonId);

    /**
     * 保存或是修改三方赛季
     * @param item   三方赛季
     * @return ThirdSportSeason
     * */
    ThirdSportSeason saveOrupdate(ThirdSportSeason item);
}
