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
public class Match30MScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {

                //查询比分
                Long period6020L = 6020L;
                FootballScores scores6020 = footballScoresMap.get(period6020L.toString());

            //判断15-20分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 1199
                    && scores6020!=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1037");
                matchSettleScore.setEventName("15:00 - 19:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6020.getGoal().getHome());
                matchSettleScore.setT2(scores6020.getGoal().getAway());
                list.add(matchSettleScore);


            }

                //查询比分
                Long period6025 = 6025L;
                FootballScores scores6025 = footballScoresMap.get(period6025.toString());

                //获取当前阶段的比分 如15分钟的
                //判断20-25分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 1499
                    && scores6025!= null) {
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1038");
                matchSettleScore.setEventName("20:00 - 24:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6025.getGoal().getHome());
                matchSettleScore.setT2(scores6025.getGoal().getAway());
                list.add(matchSettleScore);

            }

                //查询比分
                Long period30 = 6030L;
                FootballScores scores30 = footballScoresMap.get(period30.toString());

            //判断25-30分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 1799
            && scores30 != null ) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1039");
                matchSettleScore.setEventName("25:00 - 29:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores30.getGoal().getHome());
                matchSettleScore.setT2(scores30.getGoal().getAway());
                list.add(matchSettleScore);


            }

                //查询比分
                Long period799 = 61799L;
                FootballScores scores799= footballScoresMap.get(period799.toString());

            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 1799 && scores799!=null) {

                //获取当前阶段的比分 如15分钟的进球
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("103");
                matchSettleScore.setEventName("15:00 - 29:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores799.getGoal().getHome());
                matchSettleScore.setT2(scores799.getGoal().getAway());
                list.add(matchSettleScore);
                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("2012");
                matchSettleScore2.setEventName("CR 15:00 - 29:59");
                matchSettleScore2.setPeriodId(6l);
                matchSettleScore2.setT1(scores799.getCorner().getHome());
                matchSettleScore2.setT2(scores799.getCorner().getAway());
                list.add(matchSettleScore2);
                //获取当前阶段的比分 如15分钟的罚牌
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("302");
                matchSettleScore3.setEventName("BK 15:00 - 29:59");
                matchSettleScore3.setPeriodId(6l);
                matchSettleScore3.setT1(scores799.getFaCard().getHome());
                matchSettleScore3.setT2(scores799.getFaCard().getAway());
                list.add(matchSettleScore3);
            }
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match30MScoresSettleInitFilter error:",e);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //上半場
            if(data.getMatchPeriodId()>6||data.getSecondsFromStart()<=899){
                return list;
            }
            //15-20 分钟
            if(data.getSecondsFromStart()<=1199){
                if(data.getEventCode().equals("goal")){
                    list.add("103");
                    list.add("1037");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2012");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("302");

                }
                return list;
            }
            //20-25
            if(data.getSecondsFromStart()<=1499){
                if(data.getEventCode().equals("goal")){
                    list.add("103");
                    list.add("1038");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2012");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("302");

                }
                return list;
            }
            //25-30
            if(data.getSecondsFromStart()<=1799){
                if(data.getEventCode().equals("goal")){
                    list.add("103");
                    list.add("1039");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2012");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("302");

                }
                return list;
            }
        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }

}
