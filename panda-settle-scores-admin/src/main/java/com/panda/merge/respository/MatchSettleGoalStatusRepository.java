//package com.panda.merge.respository;
//
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.mapper.MatchSettleGoalStatusMapper;
//import com.panda.merge.mapper.MatchSettleTemplateMapper;
//import com.panda.merge.mapper.MatchSettleTemplateRelationMapper;
//import com.panda.merge.mapper.SettleTemplateExtMappper;
//import com.panda.merge.model.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.ApplicationContext;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//import static com.panda.merge.constant.RepositoryConstant.*;
//
//
//@Service
//@Slf4j
//public class MatchSettleGoalStatusRepository {
//
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleGoalStatusMapper matchSettleGoalStatusMapper;
//
//    @Autowired
//    ApplicationContext applicationContext;
//
//
//    public MatchSettleGoalStatus getMatchSettleGoalStatus(Long id) {
//        String key = MATCH_SETTLE_GOAL_STATUS+id;
//        Object o = null;
//        try{
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常MatchSettleGoalStatus：key:"+key, e);
//        }
//        MatchSettleGoalStatus matchSettleGoalStatus =null;
//        if (o != null) {
//            matchSettleGoalStatus = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleGoalStatus.class);
//                return matchSettleGoalStatus;
//        }else{
//            matchSettleGoalStatus = matchSettleGoalStatusMapper.selectByPrimaryKey(id);
//            if (null!= matchSettleGoalStatus){
//                try{
//                    redisService.set(key,JSONObject.toJSON(matchSettleGoalStatus),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("MatchSettleGoalStatus:redis写入异常key=[{}]MatchSettleGoalStatus[{}]", key,JSONObject.toJSON(matchSettleGoalStatus), e);
//                }
//
//            }
//        }
//        return matchSettleGoalStatus;
//    }
//
//    public void updateOrInsertMatchSettleGoalStatus(MatchSettleGoalStatus matchSettleGoalStatus, boolean isInsert){
//        String key = MATCH_SETTLE_GOAL_STATUS+matchSettleGoalStatus.getId();
//        Object o = null;
//        try{
//            redisService.set(key,JSONObject.toJSON(matchSettleGoalStatus),REDIS_THREE_TIME);
//            applicationContext.getBean(MatchSettleGoalStatusRepository.class).updateOrInsertMatchSettleGoalStatusByDataBase(matchSettleGoalStatus, isInsert);
//        }catch (Exception e){
//            log.error("redis写入异常MatchSettleGoalStatus：key=[{}]", key, e);
//        }
//    }
//
//    @Async("RemoveDBThreadPool")
//    void updateOrInsertMatchSettleGoalStatusByDataBase(MatchSettleGoalStatus matchSettleGoalStatus, boolean isInsert){
//        if (isInsert) {
//            matchSettleGoalStatusMapper.insert(matchSettleGoalStatus);
//        } else {
//            matchSettleGoalStatusMapper.updateByPrimaryKey(matchSettleGoalStatus);
//        }
//    }
//}
