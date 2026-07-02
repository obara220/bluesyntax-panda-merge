package com.panda.merge.dubbo;

import com.panda.merge.api.IMatchTeamSearchApi;
import com.panda.merge.dto.MatchTeamIDResponseDTO;
import com.panda.merge.dto.MatchTeamRequestDTO;
import com.panda.merge.service.IMatchTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@org.apache.dubbo.config.annotation.Service
public class MatchTeamSearchApiImpl implements IMatchTeamSearchApi {

    @Autowired
    IMatchTeamService matchTeamService;
   public   MatchTeamIDResponseDTO searchES(MatchTeamRequestDTO requestDTO, List<String> commerceList, boolean queryThirdFlag, List<String> queryList, Integer queryStandardCount){
       return matchTeamService.searchESMatchTeam(requestDTO,commerceList,queryThirdFlag,queryList,queryStandardCount);
   }
}
