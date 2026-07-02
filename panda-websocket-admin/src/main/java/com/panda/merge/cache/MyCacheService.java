package com.panda.merge.cache;


import com.panda.merge.dto.request.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MyCacheService {
    public static ConcurrentHashMap<String , PdSubCacheVo> sessionIdPdMatchMap =new ConcurrentHashMap();
    public static ConcurrentHashMap<String , SettleMatchSubCacheVo> sessionIdSettleMatchMap =new ConcurrentHashMap();
    public static ConcurrentHashMap<String , SettleMatchListSubCacheVo> sessionIdSettleMatchListMap =new ConcurrentHashMap();
    public static ConcurrentHashMap<String , AutoSettleDataSourceSubCacheVo> sessionIdAutoSettleDataSourceMap =new ConcurrentHashMap();
    public static ConcurrentHashMap<String , MatchSettleRollBackVo> sessionIdMatchSettleRollBackSourceMap =new ConcurrentHashMap();
    public static ConcurrentHashMap<String , StandardMatchScoreCatchVo> sessionIdMatchScoreMap =new ConcurrentHashMap();
    public static ConcurrentHashMap<String , OperatorOnlineCatchVo> sessionOperatorOnlineMap = new ConcurrentHashMap<>();
}
