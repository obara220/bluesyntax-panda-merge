//package com.panda.merge.respository;
//
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.mapper.MatchSettleRollBackInfoMapper;
//import com.panda.merge.model.MatchSettleInfo;
//import com.panda.merge.model.MatchSettleRollBackInfo;
//import com.panda.merge.model.MatchSettleRollBackInfoExample;
//import com.panda.merge.model.StandardMatchInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//
//import static com.panda.merge.constant.RepositoryConstant.*;
//
//
//@Service
//@Slf4j
//public class MatchSettleRollBackInfoRepository {
//
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleRollBackInfoMapper matchSettleRollBackInfoMapper;
//    @Autowired
//    ApplicationContext applicationContext;
//
//    public MatchSettleRollBackInfo getMatchSettleRollBackInfo(Long id) {
//        String key = MATCH_SETTLE_ROLL_BACK_INFO+id;
//        Object o = null;
//        try{
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常MatchSettleRollBackInfo：key:"+key, e);
//        }
//        MatchSettleRollBackInfo matchSettleRollBackInfo =null;
//        if (o != null) {
//            matchSettleRollBackInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleRollBackInfo.class);
//                return matchSettleRollBackInfo;
//        }else{
//
//
//            matchSettleRollBackInfo = matchSettleRollBackInfoMapper.selectByPrimaryKey(id);
//            if (null!= matchSettleRollBackInfo){
//                try{
//                    redisService.set(key,JSONObject.toJSON(matchSettleRollBackInfo),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("MatchSettleRollBackInfo:redis写入异常key=[{}]MatchSettleRollBackInfo[{}]", key,JSONObject.toJSON(matchSettleRollBackInfo), e);
//                }
//
//            }
//        }
//        return matchSettleRollBackInfo;
//    }
//
//    public List<MatchSettleRollBackInfo> getMatchSettleRollBackInfoByStandardMatchId(Long standardMatchId) {
//        String key = MATCH_SETTLE_ROLL_BACK_INFO+standardMatchId;
//        Object o = null;
//        try{
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常MatchSettleRollBackInfo：key:"+key, e);
//        }
//        List<MatchSettleRollBackInfo> matchSettleRollBackInfos =null;
//        if (o != null) {
//            matchSettleRollBackInfos = JSONObject.parseArray(o.toString(), MatchSettleRollBackInfo.class);
//            return matchSettleRollBackInfos;
//        }else{
//            MatchSettleRollBackInfoExample example =new MatchSettleRollBackInfoExample();
//            example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
//            matchSettleRollBackInfos =matchSettleRollBackInfoMapper.selectByExample(example);
//
//            if (!CollectionUtils.isEmpty(matchSettleRollBackInfos)){
//                try{
//                    redisService.set(key,JSONObject.toJSON(matchSettleRollBackInfos),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("MatchSettleRollBackInfo:redis写入异常key=[{}]MatchSettleRollBackInfos[{}]", key,JSONObject.toJSON(matchSettleRollBackInfos), e);
//                }
//
//            }
//        }
//        return matchSettleRollBackInfos;
//    }
//    public  void updateMatchSettleRollBackInfoToRedis(MatchSettleRollBackInfo info,boolean isInsert){
//        String key = MATCH_SETTLE_ROLL_BACK_INFO+info.getId();
//        try {
//            redisService.set(key,JSONObject.toJSON(info),REDIS_THREE_TIME);
//            applicationContext.getBean(MatchSettleRollBackInfoRepository.class).updateOrInsertMatchSettleRollBackInfoByDataBase(info,isInsert);
//        }catch (Exception e){
//            log.error("updateMatchSettleRollBackInfoToRedis:redis插入异常：key=[{}]MatchSettleRollBackInfo[{}]Msg[{}]", key,JSONObject.toJSON(info), e);
//        }
//    }
//
//    @Async("RemoveDBThreadPool")
//    void updateOrInsertMatchSettleRollBackInfoByDataBase(MatchSettleRollBackInfo info, boolean isInsert){
//        if (isInsert) {
//            matchSettleRollBackInfoMapper.insert(info);
//        } else {
//            matchSettleRollBackInfoMapper.updateByPrimaryKey(info);
//        }
//    }
//}
