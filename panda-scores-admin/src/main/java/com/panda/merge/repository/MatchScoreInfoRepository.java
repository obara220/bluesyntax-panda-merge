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
            //解压缩
            String str = MessageGZIP.uncompressToString((byte[]) o);
            matchScoresInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
            return matchScoresInfo;
        }
        //2.如果redis 没有就查库
        MatchScoresInfoExample example = new MatchScoresInfoExample();
        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo(sourceType.toString());
        List<MatchScoresInfo> list = matchScoresInfoMapper.selectByExample(example);
        if (list.size() != 0) {
            matchScoresInfo = list.get(0);
        }
        return matchScoresInfo;
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


    //根据三方赛事id 和 比分类型查询
    public void updateScoresInfoCache(MatchScoresInfo matchScoresInfo) {
        //1.先更新缓存
        String key = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchScoresInfo);
        redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()), RepositoryConstant.REDIS_THREE_TIME);
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

    public static void main(String[] xx){
        String str="{\"7055\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6010\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6020\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6045\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"-1\":{\"redCard\":{\"away\":1,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"shotOff\":{\"away\":4,\"home\":3},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":1,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":1},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":42,\"home\":33},\"yellowCard\":{\"away\":1,\"home\":0},\"freeKickScore\":{\"away\":6,\"home\":6},\"faCard\":{\"away\":3,\"home\":0},\"shot\":{\"away\":5,\"home\":4},\"shotOn\":{\"away\":1,\"home\":1},\"offside\":{\"away\":1,\"home\":3},\"corner\":{\"away\":2,\"home\":3},\"attack\":{\"away\":67,\"home\":65}},\"6025\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6035\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6005\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"73599\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":1},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6015\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"62699\":{\"redCard\":{\"away\":1,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0},\"yellowCard\":{\"away\":1,\"home\":0},\"faCard\":{\"away\":3,\"home\":0}},\"7060\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"61799\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"corner\":{\"away\":0,\"home\":1},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"7050\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6030\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6040\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"60899\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":1},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6\":{\"redCard\":{\"away\":1,\"home\":0},\"goal\":{\"away\":0,\"home\":1},\"shotOff\":{\"away\":4,\"home\":1},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":1,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":1},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":34,\"home\":27},\"yellowCard\":{\"away\":1,\"home\":0},\"freeKickScore\":{\"away\":3,\"home\":5},\"faCard\":{\"away\":3,\"home\":0},\"shot\":{\"away\":4,\"home\":1},\"shotOn\":{\"away\":0,\"home\":0},\"offside\":{\"away\":0,\"home\":3},\"corner\":{\"away\":1,\"home\":2},\"attack\":{\"away\":56,\"home\":54}},\"7\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"shotOff\":{\"away\":0,\"home\":2},\"substitution\":{\"away\":0,\"home\":0},\"kickOff\":{\"away\":0,\"home\":0},\"penaltyAwarded\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0,\"home\":0},\"dangerousAttack\":{\"away\":8,\"home\":6},\"yellowCard\":{\"away\":0,\"home\":0},\"freeKickScore\":{\"away\":3,\"home\":1},\"faCard\":{\"away\":0,\"home\":0},\"shot\":{\"away\":1,\"home\":3},\"shotOn\":{\"away\":1,\"home\":1},\"offside\":{\"away\":1,\"home\":0},\"corner\":{\"away\":1,\"home\":1},\"attack\":{\"away\":11,\"home\":11}}},\"secondFromStart\":3374,\"sportId\":1,\"standardMatchId\":2689935,\"thirdMatchId\":1694300215811461122},\"dataSourceTime\":1693107882309,\"linkId\":\"BG_0af5087f202308271144423122cab170a29e\"}{\"7055\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":1,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6010\":{\"redCard\":{\"away\":0,\"home\":0},\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"yellowCard\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}},\"6020\"";
        byte[] x= str.getBytes();
        System.out.println(x.length);
        byte[] XX=  MessageGZIP.compressToByte(str);
        System.out.println(XX.length);
        String strX = MessageGZIP.uncompressToString((byte[]) XX);
        byte[] xX= strX.getBytes();
        System.out.println(xX.length);
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
