package com.panda.merge.filter.football.impl;

import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.filter.football.IMatchScoresSettleInitFilter;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.utils.FootBallMatchSettleScoreUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class Match75MScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {


            //查询比分
            Long period7065 = 7065L;
            FootballScores scores7065 = footballScoresMap.get(period7065.toString());

            //判断60:00 - 64:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 3899
                    && scores7065!=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1047");
                matchSettleScore.setEventName("60:00 - 64:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7065.getGoal().getHome());
                matchSettleScore.setT2(scores7065.getGoal().getAway());
                list.add(matchSettleScore);

            }

            //查询比分
            Long period7070 = 7070L;
            FootballScores scores7070 = footballScoresMap.get(period7070.toString());

            //判断50:00 - 54:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 4199 && scores7070 !=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1048");
                matchSettleScore.setEventName("65:00 - 69:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7070.getGoal().getHome());
                matchSettleScore.setT2(scores7070.getGoal().getAway());
                list.add(matchSettleScore);

            }

            //查询比分
            Long period7075= 7075L;
            FootballScores scores7075 = footballScoresMap.get(period7075.toString());
            //判断70:00 - 74:59	分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 4499
                    && scores7075 != null ) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1049");
                matchSettleScore.setEventName("70:00 - 74:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7075.getGoal().getHome());
                matchSettleScore.setT2(scores7075.getGoal().getAway());
                list.add(matchSettleScore);
            }

                //查询比分
                Long period15 = 74499L;
                FootballScores scores4499 = footballScoresMap.get(period15.toString());
            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >=7 && standardScoresDto.getSecondFromStart() >4499
                    && scores4499 != null) {
                //获取当前阶段的比分 如15分钟的进球
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("107");
                matchSettleScore.setEventName("60:00 - 74:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores4499.getGoal().getHome());
                matchSettleScore.setT2(scores4499.getGoal().getAway());
                list.add(matchSettleScore);
                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("2015");
                matchSettleScore2.setEventName("CR 60:00 - 74:59");
                matchSettleScore2.setPeriodId(7l);
                matchSettleScore2.setT1(scores4499.getCorner().getHome());
                matchSettleScore2.setT2(scores4499.getCorner().getAway());
                list.add(matchSettleScore2);
                //获取当前阶段的比分 如15分钟的罚牌
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("306");
                matchSettleScore3.setEventName("BK 60:00 - 74:59");
                matchSettleScore3.setPeriodId(7l);
                matchSettleScore3.setT1(scores4499.getFaCard().getHome());
                matchSettleScore3.setT2(scores4499.getFaCard().getAway());
                list.add(matchSettleScore3);
            }
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match75MScoresSettleInitFilter error:",e);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //下半場
            if(!data.getMatchPeriodId().equals(7L)||data.getSecondsFromStart()<=3599){
                return list;
            }
            //60-65 分钟
            if(data.getSecondsFromStart()<=3899){
                if(data.getEventCode().equals("goal")){
                    list.add("107");
                    list.add("1047");
                }else if(data.getEventCode().equals("corner")){
                    list.add("2015");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("306");
                }
                return list;
            }
            //65-70
            if(data.getSecondsFromStart()<=4199){
                if(data.getEventCode().equals("goal")){
                    list.add("107");
                    list.add("1048");
                }else if(data.getEventCode().equals("corner")){
                    list.add("2015");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("306");
                }
                return list;
            }
            //70-75
            if(data.getSecondsFromStart()<=4499){
                if(data.getEventCode().equals("goal")){
                    list.add("107");
                    list.add("1049");
                }else if(data.getEventCode().equals("corner")){
                    list.add("2015");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("306");
                }
                return list;
            }
        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
