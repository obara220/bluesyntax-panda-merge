package com.panda.merge.repository;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.MatchTimeInfoExample;
import com.panda.merge.mq.producer.MatchTimeInfoProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


import java.util.List;

import static com.panda.merge.constant.RepositoryConstant.MATCH_TIME_INFO;
@Slf4j
@Service
public class MatchTimeInfoRepository {

    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    MatchTimeInfoProducer matchTimeInfoProducer;
    @Autowired
    RedisService redisService;
    public MatchTimeInfo selectByPrimaryKey(Long id) {
        MatchTimeInfo matchTimeInfo =null;
        try {
            String key = MATCH_TIME_INFO + id;
            Object o = redisService.get(key);
            if (o != null) {
                if (o instanceof MatchTimeInfo) {
                    matchTimeInfo = JSON.parseObject(JSON.toJSONString(o), MatchTimeInfo.class);
                } else {
                    matchTimeInfo = JSONObject.toJavaObject(JSONObject.parseObject(String.valueOf(o)), MatchTimeInfo.class);
                }
                return matchTimeInfo;
            }
            matchTimeInfo = matchTimeInfoMapper.selectByPrimaryKey(id);
            if(matchTimeInfo!=null){
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchTimeInfo);
                redisService.set(key, jsonObject.toJSONString(), RepositoryConstant.REDIS_THREE_TIME);
            }
        }catch (Exception e){
            log.error("查询赛事时间异常：",e);
        }
        return matchTimeInfo;
    }
    public MatchTimeInfo selectByThirdMatchId(Long thirdMatchId,Integer dataSourceType) {
        String key = MATCH_TIME_INFO + thirdMatchId + "_" + dataSourceType;
        Object o = redisService.get(key);
        MatchTimeInfo matchTimeInfo;
        if (ObjectUtils.isEmpty(o)) {
            MatchTimeInfoExample example = new MatchTimeInfoExample();
            example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId);
            List<MatchTimeInfo> matchTimeInfoList = matchTimeInfoMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(matchTimeInfoList)) {
                return null;
            }
            matchTimeInfo = matchTimeInfoList.get(0);
            redisService.set(key, ((JSONObject) JSONObject.toJSON(matchTimeInfo)).toJSONString(), RepositoryConstant.REDIS_THREE_TIME);
        } else {
            if (o instanceof MatchTimeInfo) {
                matchTimeInfo = JSON.parseObject(JSON.toJSONString(o), MatchTimeInfo.class);
            } else {
                matchTimeInfo = JSON.toJavaObject(JSONObject.parseObject(String.valueOf(o)), MatchTimeInfo.class);
            }
        }
        return matchTimeInfo;
    }

    public void updateByPrimaryKey(MatchTimeInfo matchTimeInfo) {
        String key = MATCH_TIME_INFO +matchTimeInfo.getId();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchTimeInfo);
        redisService.set(key, jsonObject.toJSONString(), RepositoryConstant.REDIS_THREE_TIME);
        String thirdMatchIdKey = MATCH_TIME_INFO + matchTimeInfo.getThirdMatchId() + "_" + matchTimeInfo.getDataSourceType();
        redisService.set(thirdMatchIdKey, jsonObject.toJSONString(), RepositoryConstant.REDIS_THREE_TIME);
        matchTimeInfoProducer.updateMatchTimesInfoByMq(matchTimeInfo);
    }
    //根据三方赛事id 和 比分类型查询

    //根据主键更新


}
