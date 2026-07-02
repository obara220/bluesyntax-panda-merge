package com.panda.merge.advertise.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.service.IceHockeyScoreService;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.HockeyScores;
import com.panda.merge.dto.IceHockeyScores;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageGZIP;
import com.panda.merge.utils.ScoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

import static com.panda.merge.constant.RepositoryConstant.MATCH_SCORES_INFO;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;
import com.panda.merge.config.RedisService;

@Slf4j
@Service
public class IceHockeyScoreServiceImpl implements IceHockeyScoreService {

    @Autowired
    private ScoreUtils scoreUtils;

    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    private  RedisService redisService;

    @Override
    public MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo, Long periodId) {
        MatchScoreCommonVo matchScoreCommonVo=new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(matchScoresInfo.getT1());
        matchScoreCommonVo.setT2(matchScoresInfo.getT2());
        matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());

        Map<Long, IceHockeyScores> allPeriodScores = scoreUtils.periodJson(matchScoresInfo.getScoresJson(), IceHockeyScores.class);
        IceHockeyScores periodSores = allPeriodScores.get(periodId);
        IceHockeyScores wholeScore= allPeriodScores.get(WHOLE_MATCH);

        matchScoreCommonVo.setT1(wholeScore.getMatchScore().getHome());
        matchScoreCommonVo.setT2(wholeScore.getMatchScore().getAway());

        if( null == periodSores ){
            if(SportPeriodConstant.BasketballPeriod.contans(periodId,0)){
                periodSores= new IceHockeyScores(0L);
                allPeriodScores.put(periodId,periodSores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
                matchScoresInfoMapper.updateByPrimaryKey( matchScoresInfo);
            }

            matchScoreCommonVo.setPeriodT1(0);
            matchScoreCommonVo.setPeriodT2(0);
        } else {
            matchScoreCommonVo.setPeriodT1(periodSores.getMatchScore().getHome());
            matchScoreCommonVo.setPeriodT2(periodSores.getMatchScore().getAway());
        }
        if(periodId.equals(999L)){
            matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
            matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        }
        return matchScoreCommonVo;
    }

    /**
     * 暂使用篮球的时段数据类型
     * @param linkId
     * @param matchScoresInfo
     * @param changeMatchScoreDto
     * @return
     */
    @Override
    public boolean checkScoreChangeDelete(String linkId, MatchScoresInfo matchScoresInfo, ChangeMatchScoreDto changeMatchScoreDto) {
        log.info("::{}::checkScoreChangeDelete入参;matchScoresInfo：{}; changeMatchScoreDto:{}",
                linkId, JSON.toJSONString(matchScoresInfo), JSON.toJSONString(changeMatchScoreDto));
        //1.如果 主队比分 或者 客队比分 比 传入的新比分 大 则为修正事件
        Map<Long, IceHockeyScores> periodMap = scoreUtils.periodJson(matchScoresInfo.getScoresJson(), IceHockeyScores.class);
        if ( periodMap.size()==0 || !periodMap.containsKey(changeMatchScoreDto.getPeriod()) ) {
            return false;
        }
        IceHockeyScores periodSores= periodMap.get(changeMatchScoreDto.getPeriod());
        //如果是当前阶段变更则为正常比分变更 暂时
        if(matchScoresInfo.getPeriod().equals(changeMatchScoreDto.getPeriod())){
            return false;
        }
        if( periodSores.getMatchScore().getHome()!= changeMatchScoreDto.getPeriodT1() ||
                periodSores.getMatchScore().getAway() != changeMatchScoreDto.getPeriodT2() ){
            return true;
        }
        return false;
    }

    @Override
    public MatchScoreCommonVo countScore(String linkId, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
        MatchScoreCommonVo matchScoreCommonVo = new MatchScoreCommonVo();
        Map<Long, IceHockeyScores> allPeriodScores = scoreUtils.periodJson(data.getMatchScoresInfo().getScoresJson(), IceHockeyScores.class);
        IceHockeyScores periodSores = allPeriodScores.get(changeMatchScoreDto.getPeriod());
        if(periodSores == null){
            periodSores = new IceHockeyScores(changeMatchScoreDto.getPeriod());
            allPeriodScores.put(changeMatchScoreDto.getPeriod(), periodSores);
            data.getMatchScoresInfo().setT1(matchScoreCommonVo.getT1());
            data.getMatchScoresInfo().setT2(matchScoreCommonVo.getT2());
            data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
            matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        }
        Integer addT1 = changeMatchScoreDto.getPeriodT1() - periodSores.getMatchScore().getHome();
        Integer addT2 = changeMatchScoreDto.getPeriodT2() - periodSores.getMatchScore().getAway();
        if( addT1>0 ){
            matchScoreCommonVo.setHomeAway("home");
        } else if( addT2>0 ){
            matchScoreCommonVo.setHomeAway("away");
        } else {
            matchScoreCommonVo.setHomeAway("home");
        }
        matchScoreCommonVo.setPeriodT1( changeMatchScoreDto.getPeriodT1() );
        matchScoreCommonVo.setPeriodT2( changeMatchScoreDto.getPeriodT2() );
        Integer t1 =0;
        Integer t2 =0;
        for (Map.Entry<Long, IceHockeyScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(WHOLE_MATCH)){
                continue;
            } else if ( !entry.getKey().equals(changeMatchScoreDto.getPeriod()) && Arrays.asList(1L, 2L, 3L, 40L).contains(changeMatchScoreDto.getPeriod()) ){
                t1 += entry.getValue().getMatchScore().getHome();
                t2 += entry.getValue().getMatchScore().getAway();
            }
        }
        t1 += changeMatchScoreDto.getPeriodT1();
        t2 += changeMatchScoreDto.getPeriodT2();
        matchScoreCommonVo.setT1(t1);
        matchScoreCommonVo.setT2(t2);
        return matchScoreCommonVo;
    }


    @Override
    public void updateScore(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long period, String linkId) {
        log.info("::{}::IceHockeyScoreServiceImpl_updateScore:{},period:{},score", linkId, period, JSONObject.toJSONString(matchScoreCommonVo) );

        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        Map<Long, IceHockeyScores> allPeriodScores = scoreUtils.periodJson(data.getMatchScoresInfo().getScoresJson(), IceHockeyScores.class);
        IceHockeyScores periodSores = allPeriodScores.get(period);
        IceHockeyScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        if(periodSores == null){
            periodSores = new IceHockeyScores(period);
            allPeriodScores.put(period,periodSores);
        }
        periodSores.getMatchScore().setHome(matchScoreCommonVo.getPeriodT1());
        periodSores.getMatchScore().setAway(matchScoreCommonVo.getPeriodT2());
        wholeSores.getMatchScore().setHome(matchScoreCommonVo.getT1());
        wholeSores.getMatchScore().setAway(matchScoreCommonVo.getT2());
        if(matchScoresInfo.getPeriod().equals(period)){
            matchScoresInfo.setPeriodT1(matchScoreCommonVo.getPeriodT1());
            matchScoresInfo.setPeriodT2(matchScoreCommonVo.getPeriodT2());
        }
        //总比分计算=  前几个阶段+  冰球点球大战 谁获胜获得1分
        Integer t1=0;
        Integer t2=0;
        for (Map.Entry<Long, IceHockeyScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
//            if(entry.getKey().equals(50L)){
//                continue;
//            }
            t1+=entry.getValue().getMatchScore().getHome();
            t2+=entry.getValue().getMatchScore().getAway();
        }
        wholeSores.getMatchScore().setHome(t1);
        wholeSores.getMatchScore().setAway(t2);
        matchScoresInfo.setT1(t1);
        matchScoresInfo.setT2(t2);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        String key = MATCH_SCORES_INFO + matchScoresInfo.getThirdMatchId() + "_" + matchScoresInfo.getDataSourceType();
        redisService.set(key, MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
        redisService.set(MATCH_SCORES_INFO + matchScoresInfo.getId(), MessageGZIP.compressToByte(((JSONObject) JSONObject.toJSON(matchScoresInfo)).toJSONString()));
        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        log.info("::{}::IceHockeyScoreServiceImpl_updateScore;score:{}", linkId, JSONObject.toJSONString(matchScoresInfo));
    }

}
