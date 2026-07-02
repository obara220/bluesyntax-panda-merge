package com.panda.merge.service;

import com.panda.merge.dto.MatchTeamIDResponseDTO;
import com.panda.merge.dto.MatchTeamRequestDTO;
import com.panda.merge.model.StandardSportTeam;
import com.panda.merge.model.ThirdSportTeam;

import java.util.List;

public interface IMatchTeamService {
    List<ThirdSportTeam> getThirdMatchTeamByUpdateTime(Long updateTime);

    List<StandardSportTeam> getStandardMatchTeamByUpdateTime(Long updateTime);

     MatchTeamIDResponseDTO searchESMatchTeam(MatchTeamRequestDTO requestDTO,List<String> commerceList, boolean queryThirdFlag,  List<String> queryList,Integer queryStandardCount);
}
