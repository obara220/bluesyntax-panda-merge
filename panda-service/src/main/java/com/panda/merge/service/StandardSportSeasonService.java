package com.panda.merge.service;

import com.panda.merge.model.StandardSportSeason;

/**
 * @Author Kepa
 * @Date 2021/2/10 17:38
 * @Version 1.0
 */
public interface StandardSportSeasonService {

    /**
     * 保存或是修改标准赛季
     * @param item   标准赛季
     * @return StandardSportSeason
     * */
    StandardSportSeason saveOrupdate(StandardSportSeason item);
}
