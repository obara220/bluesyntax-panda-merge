//package com.panda.merge.respository;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.RepositoryConstant;
//import com.panda.merge.mapper.MatchSettleInfoMapper;
//import com.panda.merge.model.MatchSettleInfo;
//import com.panda.merge.model.MatchSettleInfoExample;
//import com.panda.merge.model.StandardMatchInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.panda.merge.constant.RepositoryConstant.MATCH_SETTLE_INFO;
//import static com.panda.merge.constant.RepositoryConstant.REDIS_THREE_TIME;
//
//
//@Service
//@Slf4j
//public class MatchSettleInfoRepository {
//
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleInfoMapper matchSettleInfoMapper;
//    @Autowired
//    ApplicationContext applicationContext;
//
//    @Value("${match.settle.refresh.redis.settle.info.limit:1000}")
//    private Integer settleInfoLimit;
//
//    public MatchSettleInfo getMatchSettleInfo(Long id) {
//        String key = MATCH_SETTLE_INFO+id;
//        Object o = null;
//        try{
//            o = redisService.get(key);
//        }catch (Exception e){
//            log.error("redis读异常MatchSettleInfo：key:"+key, e);
//        }
//        MatchSettleInfo matchSettleInfo =null;
//        if (o != null) {
//            matchSettleInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleInfo.class);
//                return matchSettleInfo;
//        }else{
//            matchSettleInfo = matchSettleInfoMapper.selectByPrimaryKey(id);
//            if (null!= matchSettleInfo){
//                try{
//                    redisService.set(key,JSONObject.toJSON(matchSettleInfo),REDIS_THREE_TIME);
//                }catch (Exception e){
//                    log.error("MatchSettleInfo:redis写入异常key=[{}]MatchSettleInfo[{}]", key,JSONObject.toJSON(matchSettleInfo), e);
//                }
//
//            }
//        }
//        return matchSettleInfo;
//    }
//
//    public  void updateMatchSettleInfoToRedis(MatchSettleInfo info,boolean isInsert ){
//        String key = MATCH_SETTLE_INFO+info.getId();
//        try {
//            redisService.set(key,JSONObject.toJSON(info),REDIS_THREE_TIME);
//            applicationContext.getBean(MatchSettleInfoRepository.class).updateOrInsertMatchSettleInfoByDataBase(info, isInsert);
//        }catch (Exception e){
//            log.error("updateMatchSettleInfoToRedis:redis插入异常：key=[{}]MatchSettleInfo[{}]Msg[{}]", key,JSONObject.toJSON(info), e);
//        }
//    }
//    @Async("RemoveDBThreadPool")
//    void updateOrInsertMatchSettleInfoByDataBase(MatchSettleInfo info, boolean isInsert){
//        if (isInsert) {
//            matchSettleInfoMapper.insert(info);
//        } else {
//            matchSettleInfoMapper.updateByPrimaryKeySelective(info);
//        }
//    }
//
//    @Async("PushStandardSettleEventThreadPool")
//    public void deleteAllCacheBasedIdKeys(){
//        log.info("deleteAllCacheBasedIdKeys start!");
//        Long curId = 0l;
//        while (true) {
//            List<MatchSettleInfo> matchSettleInfos = selectByCurIdAndLimit(curId, settleInfoLimit);
//            if (CollectionUtils.isEmpty(matchSettleInfos)) {
//                log.info("deleteAllCacheBasedIdKeys end!");
//                return;
//            }
//            List<String> redisKeys = matchSettleInfos.stream().map(t-> RepositoryConstant.MATCH_SETTLE_INFO + t.getId()).collect(Collectors.toList());
//            log.info("deleteAllCacheBasedIdKeys redisKeys size:{}", redisKeys.size());
//            // 删除 Redis 中的这些键
//            redisService.del(redisKeys);
//            log.info("deleteAllCacheBasedIdKeys 删除key");
//            curId = matchSettleInfos.get(matchSettleInfos.size()-1).getId();
//        }
//    }
//
//    public List<MatchSettleInfo> selectByCurIdAndLimit(Long curId, int limit) {
//        MatchSettleInfoExample example = new MatchSettleInfoExample();
//        example.createCriteria().andIdGreaterThan(curId);
//        example.setOrderByClause(" id asc limit " + limit);
//        return  matchSettleInfoMapper.selectByExample(example);
//    }
//
//}
