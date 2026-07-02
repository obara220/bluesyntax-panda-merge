//package com.panda.merge.respository;
//
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.mapper.MatchSettleDataSourceConfigMapper;
//import com.panda.merge.model.MatchSettleDataSourceConfig;
//import com.panda.merge.model.MatchSettleInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import static com.panda.merge.constant.RepositoryConstant.MATCH_SETTLE_DATA_SOURCE_CONFIG;
//import static com.panda.merge.constant.RepositoryConstant.REDIS_THREE_TIME;
//
//
//@Service
//@Slf4j
//public class MatchSettleDataSourceConfigRepository {
//
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleDataSourceConfigMapper matchSettleDataSourceConfigMapper;
//
//
//    public MatchSettleDataSourceConfig getMatchSettleDataSourceConfig(Long id) {
//        String key = MATCH_SETTLE_DATA_SOURCE_CONFIG+id;
//        Object o = null;
//        try{
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常MatchSettleDataSourceConfig：key:"+key, e);
//        }
//        MatchSettleDataSourceConfig matchSettleDataSourceConfig =null;
//        if (o != null) {
//            matchSettleDataSourceConfig = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleDataSourceConfig.class);
//                return matchSettleDataSourceConfig;
//        }else{
//            matchSettleDataSourceConfig = matchSettleDataSourceConfigMapper.selectByPrimaryKey(id);
//            if (null!= matchSettleDataSourceConfig){
//                try{
//                    redisService.set(key,JSONObject.toJSON(matchSettleDataSourceConfig),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("MatchSettleDataSourceConfig:redis写入异常key=[{}]MatchSettleDataSourceConfig[{}]", key,JSONObject.toJSON(matchSettleDataSourceConfig), e);
//                }
//
//            }
//        }
//        return matchSettleDataSourceConfig;
//    }
//
//}
