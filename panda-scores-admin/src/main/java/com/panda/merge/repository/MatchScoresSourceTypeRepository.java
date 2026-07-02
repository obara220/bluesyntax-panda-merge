package com.panda.merge.repository;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.mapper.MatchScoresSourceTypeMapper;
import com.panda.merge.model.MatchScoresSourceType;
import com.panda.merge.mq.producer.MatchScoreSourceTypeProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MatchScoresSourceTypeRepository {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;

    @Autowired
    private MatchScoreSourceTypeProducer matchScoreSourceTypeProducer;

    //根据赛事获取三方赛事
    public MatchScoresSourceType selectSourceSourceTypeByThirdMatchId(Long thirdMatchId) {
        MatchScoresSourceType matchScoresSourceType = null;
        //先查缓存，没有则查数据，然后存缓存，缓存时间 3小时
        try {
            Object matchScoresSourceTypeCache = redisService.get(RepositoryConstant.MATCH_SCORES_SOURCE_TYPE + thirdMatchId);
            if (matchScoresSourceTypeCache != null) {
                matchScoresSourceType = JSONObject.toJavaObject(JSONObject.parseObject(matchScoresSourceTypeCache.toString()), MatchScoresSourceType.class);
                if (matchScoresSourceType != null && matchScoresSourceType.getId() != null && matchScoresSourceType.getId() > 0) {
                    return matchScoresSourceType;
                }
            }
             matchScoresSourceType =matchScoresSourceTypeMapper.selectByPrimaryKey(thirdMatchId);
            if (matchScoresSourceType!=null) {
                redisService.set(RepositoryConstant.MATCH_SCORES_SOURCE_TYPE + thirdMatchId, JSONObject.toJSON(matchScoresSourceType), RepositoryConstant.REDIS_THREE_TIME);
            }
        }catch (Exception e){
            log.error("获取赛事事件源类型关系异常：",e);
        }
        return matchScoresSourceType;
    }

    public void updateScoresSourceType(MatchScoresSourceType scoresSourceType) {
        //2.再MQ推送更新数据库
        redisService.set(RepositoryConstant.MATCH_SCORES_SOURCE_TYPE + scoresSourceType.getThirdMatchId(), JSONObject.toJSON(scoresSourceType), RepositoryConstant.REDIS_THREE_TIME);

        matchScoreSourceTypeProducer.updateSourceTypeByMq(scoresSourceType);
    }

    public void insertScoresSourceType(MatchScoresSourceType scoresSourceType) {
        //2.再MQ推送更新数据库
        redisService.set(RepositoryConstant.MATCH_SCORES_SOURCE_TYPE + scoresSourceType.getThirdMatchId(), JSONObject.toJSON(scoresSourceType), RepositoryConstant.REDIS_THREE_TIME);
        try {
            matchScoresSourceTypeMapper.insert(scoresSourceType);
            log.info("thirdMatchId::{}::UOFScoresConsumer&LiveDataScoresConsumer,比分切换关联表插入成功", scoresSourceType.getThirdMatchId());
        } catch (Exception e) {
            log.error("thirdMatchId::{}::UOFScoresConsumer&LiveDataScoresConsumer,比分切换关联表插入失败,数据库插入异常信息---{}", scoresSourceType.getThirdMatchId(), e.getMessage(), e);
        }
    }
}
