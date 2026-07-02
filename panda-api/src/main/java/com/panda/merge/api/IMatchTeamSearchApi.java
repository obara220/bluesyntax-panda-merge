package com.panda.merge.api;

import com.panda.merge.dto.MatchTeamIDResponseDTO;
import com.panda.merge.dto.MatchTeamRequestDTO;

import java.util.List;

public interface IMatchTeamSearchApi {
    MatchTeamIDResponseDTO  searchES(MatchTeamRequestDTO requestDTO, List<String> commerceList , boolean queryThirdFlag, List<String> queryList, Integer queryStandardCount);
}
