package com.panda.merge.repository;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.StandardMatchScoresProducer;
import com.panda.merge.utils.MessageGZIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Slf4j
@Service
public class ScoresRedisHelp {

    @Autowired
    RedisService redisService;

    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    StandardMatchScoresMapper standardMatchScoresMapper;
    @Autowired
    StandardMatchScoresProducer standardMatchScoresProducer;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;


    /**
     * 标准赛事ID获取标准比分
     * redis缓存通过json转换，不直接使用对象存取，避免数据转换异常
     * @param matchId
     * @return
     */
    public  StandardMatchScores  getCatchStandScoreByMatchId(Long matchId) {
        Object redisObj =redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchScores:" + matchId);
        try{
            if(redisObj!=null){
                StandardMatchScores scores = JSONUtil.toBean(JSONUtil.toJsonStr(redisObj), StandardMatchScores.class);
                return scores;
            }else{
                StandardMatchScores scores = standardMatchScoresMapper.loadByMatchId(matchId);
                if(scores!=null){
                    redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchScores:" + matchId,scores);
                }
                return scores;
            }
        }catch(Exception e){
            log.error("获取比分异常：{}",matchId,e);
        }
        return null;
    }

    /**
     * 保存标准比分到缓存
     * @param score
     */
    public  void  saveCatchStandScore(StandardMatchScores score) {
        if(score!=null){
            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchScores:" + score.getMatchId(),score,RedisConfig.REDIS_THREE_TIME);
            standardMatchScoresProducer.updateStandardMatchScoresByMq(score);
        }
    }


}
