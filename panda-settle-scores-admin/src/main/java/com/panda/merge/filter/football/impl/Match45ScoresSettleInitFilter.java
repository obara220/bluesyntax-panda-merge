package com.panda.merge.filter.football.impl;

import com.panda.merge.constant.MatchLengthConstant;
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
public class Match45ScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);

            //查询比分
            Long period6050 = 6050L;
            FootballScores scores6050 = footballScoresMap.get(period6050.toString());

            //判断大于45分钟的绝杀球 (大于上半场  超过45分钟)
            if (standardScoresDto.getPeriodId() > 6
                    && standardScoresDto.getSecondFromStart() > 2699
                    && scores6050 != null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1043");
                matchSettleScore.setEventName("1H Last-minute Goal (Injury Time)");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6050.getGoal().getHome());
                matchSettleScore.setT2(scores6050.getGoal().getAway());
                list.add(matchSettleScore);


            }




            Long period62699 = 62699L;
            FootballScores scores62699 = footballScoresMap.get(period62699.toString());

            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >6
                    && MatchLengthConstant.isPeriodTimeRight(standardScoresDto,31l) && scores62699 != null) {
                //查询比分
                //获取当前阶段的比分 如15分钟的进球
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("104");
                matchSettleScore.setEventName("30:00 - 1HT");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores62699.getGoal().getHome());
                matchSettleScore.setT2(scores62699.getGoal().getAway());
                list.add(matchSettleScore);
                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("2013");
                matchSettleScore2.setEventName("CR 30:00 - HT");
                matchSettleScore2.setPeriodId(6l);
                matchSettleScore2.setT1(scores62699.getCorner().getHome());
                matchSettleScore2.setT2(scores62699.getCorner().getAway());
                list.add(matchSettleScore2);
                //
                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("303");
                matchSettleScore3.setEventName("BK 30:00 - HT");
                matchSettleScore3.setPeriodId(6l);
                matchSettleScore3.setT1(scores62699.getFaCard().getHome());
                matchSettleScore3.setT2(scores62699.getFaCard().getAway());
                list.add(matchSettleScore3);
            }



            //查询比分
                Long period15 = 6l;
                FootballScores scores1H = footballScoresMap.get(period15.toString());
            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >6&& MatchLengthConstant.isPeriodTimeRight(standardScoresDto,31l)
            && scores1H !=null ) {

                //上半场进球比分
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("105");
                matchSettleScore.setEventName("1HT");
                matchSettleScore.setPeriodId(31l);
                matchSettleScore.setT1(scores1H.getGoal().getHome());
                matchSettleScore.setT2(scores1H.getGoal().getAway());
                list.add(matchSettleScore);
                //上半场角球比分
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("201");
                matchSettleScore2.setEventName("1HT CR");
                matchSettleScore2.setPeriodId(31L);
                matchSettleScore2.setT1(scores1H.getCorner().getHome());
                matchSettleScore2.setT2(scores1H.getCorner().getAway());
                list.add(matchSettleScore2);
                //上半场罚牌比分
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("304");
                matchSettleScore3.setEventName("BK 1HT");
                matchSettleScore3.setPeriodId(31l);
                matchSettleScore3.setT1(scores1H.getFaCard().getHome());
                matchSettleScore3.setT2(scores1H.getFaCard().getAway());
                list.add(matchSettleScore3);

                //上半场红牌比分
                MatchSettleScore matchSettleScore4 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore4.setEventCode("red_card");
                matchSettleScore4.setSettleNum("3041");
                matchSettleScore4.setEventName("1st Half Bookings -red card");
                matchSettleScore4.setPeriodId(31l);
                matchSettleScore4.setT1(scores1H.getRedCard().getHome());
                matchSettleScore4.setT2(scores1H.getRedCard().getAway());
                list.add(matchSettleScore4);


            }



        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match45ScoresSettleInitFilter error:",e);
        }
        return list;
    }
    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //上半場
            //45- 分钟
            if(data.getMatchPeriodId().equals(6l)&&data.getSecondsFromStart()>2699){
                if(data.getEventCode().equals("goal")){
                    list.add("104");
                    list.add("1043");
                }else if(data.getEventCode().equals("corner")){
                    list.add("2013");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("303");
                }
                return list;
            }

        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
