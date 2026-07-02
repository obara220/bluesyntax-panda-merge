package com.panda.merge.repository;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


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

//    /**
//     * 三方赛事ID获取三方赛事
//     * @param thirdMatchId
//     * @return
//     */
//    public  ThirdMatchInfo getCatchThirdMatchInfoByPrimaryKey(Long thirdMatchId) {
//        Object redisObj =redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + thirdMatchId);
//        if(redisObj!=null){
//            return JSON.parseObject(redisObj.toString(), new TypeReference<ThirdMatchInfo>(){});
//        }else{
//            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
//            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + thirdMatchId,JSONObject.toJSON(thirdMatchInfo).toString());
//            return thirdMatchInfo;
//        }
//    }
//    /**
//     * 三方数据源ID+数据源编码获取三方赛事
//     * @param thirdMatchSourceId
//     * @param dataSourceCode
//     * @return
//     */
//    public ThirdMatchInfo getCatchThirdMatchInfoBySourceCodeAndSourceId(String thirdMatchSourceId, String dataSourceCode) {
//        Object redisObj =redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + dataSourceCode + '-' + thirdMatchSourceId);
//        if(redisObj!=null){
//            return(ThirdMatchInfo) redisObj;
//        }else{
//
//            ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
//            thirdMatchInfoExample.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andDataSourceCodeEqualTo(dataSourceCode);
//            List<ThirdMatchInfo> thirdMatchInfos =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
//            if(!thirdMatchInfos.isEmpty()){
//                redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + dataSourceCode + '-' + thirdMatchSourceId,thirdMatchInfos.get(0));
//                return thirdMatchInfos.get(0);
//            }
//            return null;
//        }
//    }
//    /**
//     * 标准赛事ID+数据源编码获取三方赛事
//     * @param referenceId
//     * @param sourceCode   @TODO
//     * @return
//     */
//    public ThirdMatchInfo getCatchThirdMatchInfoByReferenceIdAndSourceCode(Long referenceId, String sourceCode) {
//        String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdMatchInfo:" + referenceId + '-' + sourceCode;
//        ThirdMatchInfo thirdMatchInfo = null;
//        Object thirdMatchCache = redisService.get(key);
//        if (thirdMatchCache != null) {
//            thirdMatchInfo = JSONObject.toJavaObject(JSONObject.parseObject(thirdMatchCache.toString()), ThirdMatchInfo.class);
//            if (thirdMatchInfo != null && thirdMatchInfo.getId() != null && thirdMatchInfo.getId() > 0) {
//                return thirdMatchInfo;
//            }
//        }
//        ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
//        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(referenceId).andDataSourceCodeEqualTo(sourceCode);
//        List<ThirdMatchInfo> list = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
//        if(!list.isEmpty()){
//            thirdMatchInfo =list.get(0);
//            redisService.set(key, thirdMatchInfo,RepositoryConstant.REDIS_THREE_TIME);
//            return thirdMatchInfo;
//        }
//        return thirdMatchInfo;
//    }
//
//    /**
//     * 标准赛事ID获取三方赛事集合
//     * @param referenceId
//     * @return
//     */
//    public List<ThirdMatchInfo> getCatchThirdMatchInfoListByReferenceId(Long referenceId) {
//        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
//        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(referenceId);
//        List<ThirdMatchInfo> thirdMatchInfos =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
//        if(!thirdMatchInfos.isEmpty()){
//            return thirdMatchInfos;
//        }
//        return null;
//    }
//    /**
//     * 标准赛事ID获取标准赛事
//     * @param matchId
//     * @return
//     */
//    public  StandardMatchInfo getCatchStandMatchInfoByPrimaryKey(Long matchId) {
//        StandardMatchInfo standardMatchInfo = null;
//        Object redisObj =redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + matchId);
//        if(redisObj!=null){
//            return(StandardMatchInfo) redisObj;
//        }else{
//            standardMatchInfo=standardMatchInfoMapper.selectByPrimaryKey(matchId);
//            redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:" + matchId,standardMatchInfo);
//            return standardMatchInfo;
//        }
//    }

    /**
     * 标准赛事ID获取标准比分
     * @param matchId
     * @return
     */
    public  StandardMatchScores  getCatchStandScoreByMatchId(Long matchId) {
        Object redisObj =redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchScores:" + matchId);
        if(redisObj!=null){
            return(StandardMatchScores) redisObj;
        }else{
            StandardMatchScores scores = standardMatchScoresMapper.loadByMatchId(matchId);
            if(scores!=null){
                redisService.set(RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchScores:" + matchId,scores);
            }
            return scores;
        }
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

//    /**
//     * 标准赛事ID获取开售表数据
//     * @param matchId
//     * @return
//     */
//    public  StandardSportMarketSell getCatchSportMarketSellByMatchId(Long matchId) {
//        Object redisObj =redisService.get(RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketSell:" + matchId);
//        if(redisObj!=null){
//            return(StandardSportMarketSell) redisObj;
//        }else{
//            //刷新开售信息缓存
//            StandardSportMarketSellExample standardSportMarketSellExample = new StandardSportMarketSellExample();
//            standardSportMarketSellExample.createCriteria().andMatchInfoIdEqualTo(matchId);
//            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(standardSportMarketSellExample);
//            if(CollectionUtils.isEmpty(standardSportMarketSells)){
//                return null;
//            }
//            return standardSportMarketSells.get(0);
//        }
//    }


//    /**
//     * 三方赛事ID+数据类型获取三方比分
//     * @param thirdMatchId
//     * @param sourceType
//     * @return
//     */
//    public MatchScoresInfo getCatchScoresByThirdIdAndSourceType(Long thirdMatchId, Integer sourceType) {
//        //1.查询redis
//        MatchScoresInfo matchScoresInfo = null;
//        String key = MATCH_SCORES_INFO + thirdMatchId + "_" + sourceType;
//        Object o = redisService.get(key);
//        if (o != null) {
//            //解压缩
//            String str = MessageGZIP.uncompressToString((byte[]) o);
//            matchScoresInfo = JSON.toJavaObject(JSONObject.parseObject(str), MatchScoresInfo.class);
//            return matchScoresInfo;
//        }
//        //2.如果redis 没有就查库
//        MatchScoresInfoExample example = new MatchScoresInfoExample();
//        example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo(sourceType.toString());
//        List<MatchScoresInfo> list = matchScoresInfoMapper.selectByExample(example);
//        if (!list.isEmpty()) {
//            matchScoresInfo = list.get(0);
//            redisService.set(key,matchScoresInfo);
//        }
//        return matchScoresInfo;
//    }

//    /**
//     * 标准赛事ID+数据源类型获取三方比分
//     * @param matchId
//     * @param sourceType
//     * @return
//     */
//    public MatchScoresInfo getCatchScoresByStandardId(Long matchId, Integer sourceType) {
//        StandardSportMarketSell standardSportMarketSell = this.getCatchSportMarketSellByMatchId(matchId);
//        if (standardSportMarketSell == null){
//            return null;
//        String dataSourceCode = standardSportMarketSell.getBusinessEvent();
//        ThirdMatchInfo thirdMatchInfo = this.getCatchThirdMatchInfoByReferenceIdAndSourceCode(matchId,dataSourceCode);
//
//        return  matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), sourceType);
//    }

}
