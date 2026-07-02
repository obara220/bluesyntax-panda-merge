package com.panda.merge.filter.football.impl;

import com.panda.merge.constant.MatchLengthConstant;
import com.panda.merge.dto.CommonStandardScoresDto;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.filter.football.IMatchScoresSettleInitFilter;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.utils.FootBallMatchSettleScoreUtils;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class MatchET1ScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);
            //判断是否符合条件
            if (standardScoresDto.getPeriodId().equals(33l)||footballScoresMap.get("42")!=null) {
                if(!MatchLengthConstant.isPeriodTimeRight(standardScoresDto,33l)){
                    return list;
                }
                //查询比分
                Long period15 = 41l;
                FootballScores scores1H = footballScoresMap.get(period15.toString());
                if(scores1H==null){
                    return list;
                }
                //上半场进球比分
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1014");
                matchSettleScore.setEventName("1ET");
                matchSettleScore.setPeriodId(33l);
                matchSettleScore.setT1(scores1H.getGoal().getHome());
                matchSettleScore.setT2(scores1H.getGoal().getAway());
                list.add(matchSettleScore);
                //上半场角球比分
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("206");
                matchSettleScore2.setEventName("1ET CR");
                matchSettleScore2.setPeriodId(33L);
                matchSettleScore2.setT1(scores1H.getCorner().getHome());
                matchSettleScore2.setT2(scores1H.getCorner().getAway());
                list.add(matchSettleScore2);
                //上半场罚牌比分
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("3013");
                matchSettleScore3.setEventName("1ET BK");
                matchSettleScore3.setPeriodId(33L);
                matchSettleScore3.setT1(scores1H.getFaCard().getHome());
                matchSettleScore3.setT2(scores1H.getFaCard().getAway());
                list.add(matchSettleScore3);
            }
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":MatchET1ScoresSettleInitFilter error:",e);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            ////駕駛上半場
            if(!data.getMatchPeriodId().equals(41l)){
                return list;
            }
            //90- 分钟

            if(data.getEventCode().equals("goal")){
                list.add("1014");
            }else if(data.getEventCode().equals("corner")){
                list.add("206");
            }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                list.add("3013");
            }
            return list;

        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
