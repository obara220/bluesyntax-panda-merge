package com.panda.merge.v2.service.impl;

import com.panda.merge.model.MatchSettleDataSourceSwitch;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.v2.repository.MatchSettleDataSourceConfigRepository;
import com.panda.merge.v2.repository.MatchSettleDataSourceSwitchRepository;
import com.panda.merge.v2.service.IMatchSettleDataSourceConfigService;
import com.panda.merge.v2.service.IMatchSettleDataSourceSwitchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("MatchSettleDataSourceSwitchServiceImplV2")
public class MatchSettleDataSourceSwitchServiceImpl implements IMatchSettleDataSourceSwitchService {

    @Autowired
    private MatchSettleDataSourceSwitchRepository matchSettleDataSourceSwitchRepository;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Override
    public Map<String, Integer> getTournamentLevelStatuses(Long standardMatchId, String dataSourceCode, List<String> eventCodes) {
        Map<String, Integer> result = new HashMap<>();
        try {
            //1、查询标准赛事对应的联赛Id,并根据联赛Id查询出联赛的等级
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo != null && standardMatchInfo.getStandardTournamentId() != null) {
                //3139需求 足球开关由结算设置控制
                if (standardMatchInfo.getSportId().equals(1L)){
                    List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getMatchSettleDataSourceSwitchByRedis(standardMatchInfo.getSportId(),dataSourceCode);
                    for (String eventCode : eventCodes) {
                        Integer status = null;
                        if (!switches.isEmpty()){
                            MatchSettleDataSourceSwitch sourceSwitch = switches.get(0);
                            if(eventCode.equals("corner")) {
                                status = sourceSwitch.getCorner();
                            }else if(eventCode.equals("goal")||eventCode.equals("kick_off")){
                                status = sourceSwitch.getGoal();
                            }else {
                                status = sourceSwitch.getBooking();
                            }
                        }
                        if (dataSourceCode.equals("PA")){
                            status =1;
                        }
                        result.put(eventCode, status);
                    }

                }else if (standardMatchInfo.getSportId().equals(2L)){
                    List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getMatchSettleDataSourceSwitchByRedis(standardMatchInfo.getSportId(),dataSourceCode);
                    for (String eventCode : eventCodes) {
                        Integer status = null;
                        if (!switches.isEmpty()){
                            MatchSettleDataSourceSwitch sourceSwitch = switches.get(0);
                            if(eventCode.equals("score_change")) {
                                status = sourceSwitch.getGoal();
                            }
                        }
                        if (dataSourceCode.equals("PA")){
                            status =1;
                        }
                        result.put(eventCode, status);
                    }
                }
            }
        } catch (Exception e) {
            log.error("::::根据标准赛事Id:{},结算查询联赛对应的数据源:{},状态异常信息:{}eventCodes:{}", standardMatchId,dataSourceCode,eventCodes, e);
        }
        return result;
    }

}
