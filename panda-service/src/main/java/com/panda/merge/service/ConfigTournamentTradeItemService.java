package com.panda.merge.service;

import com.panda.merge.dto.ConfigTournamentTradeItemDTO;
import com.panda.merge.model.ConfigTournamentTradeItem;

public interface ConfigTournamentTradeItemService {
    ConfigTournamentTradeItem getItem(Long sportId, Long tournamentId, Integer matchType);

    ConfigTournamentTradeItem create(ConfigTournamentTradeItemDTO dto);

    void update(ConfigTournamentTradeItem configTournamentTradeItem, ConfigTournamentTradeItemDTO dto);

}
