package com.panda.merge.filter.football.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.CommonStandardScoresDto;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballPenaltyScores;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.filter.football.IMatchEventSettleInitFilter;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.utils.FootballPenaltySettleEventUtils;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class MatchPenaltyEventSettleInitFilter implements IMatchEventSettleInitFilter {

    @Override
    public List<MatchSettleEvent> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleEvent> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        //这里主要是生成前五轮比分和 点球大战总比分
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);
            //判断是否符合条件
            if (footballScoresMap.get("50") != null &&(standardScoresDto.getPeriodId().equals(120l)||standardScoresDto.getPeriodId().equals(999l))) {
                //查询比分
                if(standardScoresDto.getExtraScores()==null){
                    log.warn("MatchPenaltyEventSettleInitFilter :standardScoresDto.getExtraScores()==null,linkedId:",standardScoresDto.getLinkedId());
                    return list;
                }
                //前5轮比分暂时不做处理
//                FootballPenaltyScores footballPenaltyScores = JSONObject.toJavaObject(standardScoresDto.getExtraScores(),FootballPenaltyScores.class);
//                MatchSettleEvent matchSettleScore11 = FootballPenaltySettleEventUtils.initPenaltySettleEvent(standardScoresDto.getStandardMatchId());
//                matchSettleScore11.setEventCode("goal");
//                matchSettleScore11.setSettleNum("1029");
//                matchSettleScore11.setPeriodId(50l);
//                matchSettleScore11.setFirstNum(5);
//                matchSettleScore11.setT1(footballPenaltyScores.getRound5Scores().getHome());
//                matchSettleScore11.setT2(footballPenaltyScores.getRound5Scores().getAway());
//                list.add(matchSettleScore11);
                //点球大战总比分
                FootballScores footballScores =footballScoresMap.get("50");

                MatchSettleEvent matchSettleScore12 =FootballPenaltySettleEventUtils.initPenaltySettleEvent(standardScoresDto.getStandardMatchId());
                matchSettleScore12.setEventCode("goal");
                matchSettleScore12.setSettleNum("1028");
                matchSettleScore12.setPeriodId(120l);
                matchSettleScore12.setT1(footballScores.getGoal().getHome());
                matchSettleScore12.setT2(footballScores.getGoal().getAway());
                list.add(matchSettleScore12);
            }
        }catch (Exception e){
            log.error("MatchPenaltyEventSettleInitFilter error:",e);
        }
        return list;
    }
}
