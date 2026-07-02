package com.panda.merge.respository;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.MatchSettleGoalStatus;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.panda.merge.constant.RepositoryConstant.*;


@Service
@Slf4j
public class StandardMatchInfoRepository {

    @Autowired
    RedisService redisService;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    ApplicationContext applicationContext;
//    public StandardMatchInfo getStandardMatchInfo(Long id) {
//        String key = STANDARD_MATCH_INFO+id;
//        Object o = null;
//        try{
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常StandardMatchInfo：key:"+key, e);
//        }
//        StandardMatchInfo standardMatchInfo =null;
//        if (o != null) {
//            standardMatchInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), StandardMatchInfo.class);
//                return standardMatchInfo;
//        }else{
//            standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(id);
//            if (null!= standardMatchInfo){
//                try{
//                    redisService.set(key,JSONObject.toJSON(standardMatchInfo),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("StandardMatchInfo:redis写入异常StandardMatchInfo：key=[{}]StandardMatchInfo[{}]", key,JSONObject.toJSON(standardMatchInfo), e);
//                }
//
//            }
//        }
//        return standardMatchInfo;
//    }

    public void updateStandardMatchInfoToRedis(StandardMatchInfo info,boolean isInsert){
        String key = STANDARD_MATCH_INFO+info.getId();
        try {
            redisService.set(key,JSONObject.toJSON(info),REDIS_THREE_TIME);
            applicationContext.getBean(StandardMatchInfoRepository.class).updateOrInsertStandardMatchInfoByDataBase(info, isInsert);
        }catch (Exception e){
            log.error("updateStandardMatchInfoToRedis:redis插入异常：key=[{}]StandardMatchInfo[{}]Msg[{}]", key,JSONObject.toJSON(info), e);
        }
    }
    @Async("RemoveDBThreadPool")
    void updateOrInsertStandardMatchInfoByDataBase(StandardMatchInfo info, boolean isInsert){
        if (isInsert) {
            standardMatchInfoMapper.insert(info);
        } else {
            standardMatchInfoMapper.updateByPrimaryKey(info);
        }
    }
}
