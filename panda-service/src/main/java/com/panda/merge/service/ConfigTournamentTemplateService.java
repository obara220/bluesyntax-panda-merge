package com.panda.merge.service;

import com.panda.merge.dto.TradeTournamentTemplateConfigDTO;
import com.panda.merge.model.ConfigTournamentTemplate;

import java.util.List;

/**
 * @author :  Riben
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service
 * @Description :  TODO
 * @Date: 2020-09-10 17:27
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ConfigTournamentTemplateService {

    /**
     * 根据盘口类型，及联赛id获取联赛绑定关系记录
     * @param tournamentId
     * @param marketType
     * @return
     */
    List<ConfigTournamentTemplate> getTournamentTemplateInfoByIdAndMarketType(Long tournamentId, Integer marketType);

    /**
     * 保存联赛与模板绑定关系记录
     * @param relationParams
     * @return
     */
    ConfigTournamentTemplate save(TradeTournamentTemplateConfigDTO relationParams);

    /**
     * 更新联赛对应的绑定关系
     * @param realtionEntity
     */
    void update(ConfigTournamentTemplate realtionEntity);
}
