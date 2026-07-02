package com.panda.merge.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.ConfigMatchStatus;

public interface ConfigMatchStatusService {
	ConfigMatchStatus getItem(String linkId, Long matchId, Integer marketType);

    ConfigMatchStatus create(String linkId,ConfigMatchStatus configMatchStatus);

    ConfigMatchStatus update(String linkId,ConfigMatchStatus configMatchStatus);

    void processConfigMatchStatus(String linkId, Map<String, StandardMarketDataMessage> standardMarketMessageMap,
                                  String dataSourceCode, Long matchId, Long sportId, Integer marketType, Long beginTime, Long dataSourceTime, Set<Long> marketCategoryIdSet, Long matchPeriodId);
    
    void deleteRedisData(String linkId,Long matchId);
    
    void saveMatchMarketLastActiveDeaOddsOfRedis(String linkId,Long matchId, Long marketCategoryId,List<StandardMarketMessage> standardMarketMessageList,Long beginTime,String dataSourceCode);
    
    void processTXTimestamps(String linkId,ThirdMarketDTO thirdMarketDTO,Long matchId,String dataSourceCode, Long beginTime);
    
    void saveDeaMarketOfRedis(String linkId, Long matchId, Set<StandardMarketDataMessage> newDea, Long beginTime);
}
