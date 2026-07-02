package com.panda.merge.advertise.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.service.TennisScoreService;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.TennisScores;
import com.panda.merge.dto.advertise.TennisEditSecondScoreDto;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


@Service
@Slf4j
public class TennisScoreServiceImpl implements TennisScoreService {
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    ScoresProducer scoresProducer;

    @Override
    public MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo,Long periodId) {
        MatchScoreCommonVo matchScoreCommonVo=new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(matchScoresInfo.getT1());
        matchScoreCommonVo.setT2(matchScoresInfo.getT2());
        matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores periodSores= allPeriodScores.get(periodId);
        FootballScores wholeScore= allPeriodScores.get(WHOLE_MATCH);
        matchScoreCommonVo.setT1(wholeScore.getGoal().getHome());
        matchScoreCommonVo.setT2(wholeScore.getGoal().getAway());
        if(periodSores==null){
            if(SportPeriodConstant.FootballPeriod.contans(periodId)){
                periodSores= new FootballScores(0l);
                allPeriodScores.put(periodId,periodSores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
                matchScoresInfoMapper.updateByPrimaryKey( matchScoresInfo);
            }
            matchScoreCommonVo.setPeriodT1(0);
            matchScoreCommonVo.setPeriodT2(0);
        }else {
            matchScoreCommonVo.setPeriodT1(periodSores.getGoal().getHome());
            matchScoreCommonVo.setPeriodT2(periodSores.getShot().getAway());
        }
        if(periodId.equals(999l)){
            matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
            matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        }
        return matchScoreCommonVo;
    }

    @Override
    public MatchScoreCommonVo countScore(MatchScoreAndTimeVo data, TennisEditSecondScoreDto editSecondScoreDto)
    {
        String linkId = editSecondScoreDto.getLinkedId();
        log.info("::{}::网球计算当前比分入参:{}, 编辑参数:{}", linkId, JSON.toJSONString(data), JSON.toJSONString(editSecondScoreDto));
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        MatchScoreCommonVo matchScoreCommonVo = new MatchScoreCommonVo();

        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);

        TennisScores periodSores = allPeriodScores.get(matchTimeInfo.getPeriod());
        if(periodSores == null)
        {
            periodSores = new TennisScores(matchTimeInfo.getPeriod());
            allPeriodScores.put(matchTimeInfo.getPeriod(), periodSores);
            data.getMatchScoresInfo().setT1(matchScoreCommonVo.getT1());
            data.getMatchScoresInfo().setT2(matchScoreCommonVo.getT2());
            data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
            matchScoresInfoMapper.updateByPrimaryKey(data.getMatchScoresInfo());
        }
        Integer addT1 = editSecondScoreDto.getT1() - periodSores.getMatchScore().getHome();
        Integer addT2 = editSecondScoreDto.getT2() - periodSores.getMatchScore().getAway();
        if(addT1>0)
        {
            matchScoreCommonVo.setHomeAway("home");
        }
        else if(addT2>0)
        {
            matchScoreCommonVo.setHomeAway("away");
        }
        else
        {
            matchScoreCommonVo.setHomeAway("home");
        }
        matchScoreCommonVo.setPeriodT1( editSecondScoreDto.getT1());
        matchScoreCommonVo.setPeriodT2(editSecondScoreDto.getT2());
        Integer t1 = 0;
        Integer t2 = 0;
        for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet())
        {
            if(entry.getKey().equals(WHOLE_MATCH))
            {
                continue;
            }
            else if(!entry.getKey().equals(matchTimeInfo.getPeriod()))
            {
                t1 += entry.getValue().getMatchScore().getHome();
                t2 += entry.getValue().getMatchScore().getAway();
            }
        }
        t1 += editSecondScoreDto.getT1();
        t2 += editSecondScoreDto.getT2();
        matchScoreCommonVo.setT1(t1);
        matchScoreCommonVo.setT2(t2);
        return matchScoreCommonVo;
    }


}
