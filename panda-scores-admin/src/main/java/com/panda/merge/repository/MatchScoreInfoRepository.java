package com.panda.merge.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.dto.scores.B02ScoresSourceDTO;
import com.panda.merge.mapper.B02ScoresSourceMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.model.B02ScoresSource;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresInfoExample;
import com.panda.merge.mq.producer.MatchScoresInfoProducer;
import com.panda.merge.utils.MessageGZIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.panda.merge.constant.RepositoryConstant.MATCH_SCORES_INFO;

@Service
@Slf4j
public class MatchScoreInfoRepository {

    @Autowired
    RedisService redisService;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    MatchScoresInfoProducer matchScoresInfoProducer;
    @Autowired
    B02ScoresSourceMapper b02ScoresSourceMapper;

    public MatchScoresInfo selectByExample(Long thirdMatchId, Integer sourceType) {
        //1.查询redis
        MatchScoresInfo matchScoresInfo = null;
        String key = MATCH_SCORES_INFO + thirdMatchId + "_" + sourceType;
        Object o = redisService.get(key);
        if (o != null) {
            if(o instanceof  MatchScoresInfo){
                return JSON.parseObject(JSON.toJSONString(o), MatchScoresInfo.class);
            }else{
                //解压缩
                String str = MessageGZIP.uncompressToString((byte[]) o);
                matchScoresInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
                return matchScoresInfo;
            }
        }else{
            //2.如果redis 没有就查库
            MatchScoresInfoExample example = new MatchScoresInfoExample();
            example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo(sourceType.toString());
            List<MatchScoresInfo> list = matchScoresInfoMapper.selectByExample(example);
            if (!list.isEmpty()) {
                matchScoresInfo = list.get(0);
            }
            return matchScoresInfo;
        }

    }

    /**
     * 按主键优先查redis
     *
     * @param id 主键
     * @return 比分数据
     */
    public MatchScoresInfo selectByPrimaryKey(Long id) {
        //1.查询redis
        MatchScoresInfo matchScoresInfo = null;
        String key = MATCH_SCORES_INFO + id;
        Object o = redisService.get(key);
        if (o != null) {
            if (o instanceof MatchScoresInfo) {
                return JSON.parseObject(JSON.toJSONString(o), MatchScoresInfo.class);
            } else {
                //解压缩
                String str = MessageGZIP.uncompressToString((byte[]) o);
                matchScoresInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
                return matchScoresInfo;
            }
        } else {
            //2.如果redis 没有就查库
            matchScoresInfo = matchScoresInfoMapper.selectByPrimaryKey(id);
            if (!ObjectUtils.isEmpty(matchScoresInfo)) {
                redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
            }
            return matchScoresInfo;
        }
    }

    public MatchScoresInfo queryMatchScoreFromDB(Long id) {
        return matchScoresInfoMapper.selectByPrimaryKey(id);
    }


    /**
     * 只更新缓存不操作数据库
     *
     * @param matchScoresInfo
     */
    public void updateScoresInfoCache(MatchScoresInfo matchScoresInfo) {
        //1.先更新缓存
        String key = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchScoresInfo);
        redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()));
        redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(jsonObject.toJSONString()));
    }

    //根据主键更新 todo
    public void updateScoresInfo(MatchScoresInfo matchScoresInfo) {
        //1.先更新缓存
        String key = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchScoresInfo);
        redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()));
        redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(jsonObject.toJSONString()));
//        //2.再MQ推送更新数据库
        matchScoresInfoProducer.updateScoresInfoByMq(matchScoresInfo);
    }


    /**
     * 校验B02数据源比分通道
     * @param sportId
     * @return
     */
    public Long checkB02ScoresSource(Long sportId) {
        Object obj = redisService.get("B02_SCORES_SOURCE_"+ sportId);
        if(obj!=null){
            return Long.parseLong(obj.toString());
        }else{
            List<B02ScoresSource> scoreSourceType = b02ScoresSourceMapper.queryBySportId(sportId);
            if(scoreSourceType!=null && !scoreSourceType.isEmpty()){
                redisService.set("B02_SCORES_SOURCE_"+ sportId,scoreSourceType.get(0).getDataSourceType(), RedisConfig.REDIS_MONTH_TIME);
                return scoreSourceType.get(0).getDataSourceType();
            }
        }
        return 0L;
    }


}
