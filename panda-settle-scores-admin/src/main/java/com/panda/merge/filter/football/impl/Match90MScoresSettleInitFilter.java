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
public class Match90MScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);



            //查询比分
            Long period7080 = 7080L;
            FootballScores scores7080 = footballScoresMap.get(period7080.toString());
            //判断75:00 - 79:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 4799
                    && scores7080!=null) {
                //获取当前阶段的比分 如5分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1050");
                matchSettleScore.setEventName("75:00 - 79:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7080.getGoal().getHome());
                matchSettleScore.setT2(scores7080.getGoal().getAway());
                list.add(matchSettleScore);
            }

            //查询比分
            Long period7085 = 7085L;
            FootballScores scores7085 = footballScoresMap.get(period7085.toString());
            //判断50:00 - 54:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 5099 && scores7085 !=null) {
                //获取当前阶段的比分 如5分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1051");
                matchSettleScore.setEventName("80:00 - 84:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7085.getGoal().getHome());
                matchSettleScore.setT2(scores7085.getGoal().getAway());
                list.add(matchSettleScore);
            }

            //查询比分
            Long period7090= 7090L;
            FootballScores scores7090 = footballScoresMap.get(period7090.toString());
            //判断85:00 - 90:00	分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 5400
                    && scores7090 != null ) {
                //获取当前阶段的比分 如5分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1052");
                matchSettleScore.setEventName("85:00 - 90:00");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7090.getGoal().getHome());
                matchSettleScore.setT2(scores7090.getGoal().getAway());
                list.add(matchSettleScore);

            }

            //查询比分
            Long period7095 = 7095L;
            FootballScores scores7095 = footballScoresMap.get(period7095.toString());

            //判断大于90分钟的绝杀球 (大于上半场  超过90分钟)
            if (standardScoresDto.getPeriodId() > 7
                    && standardScoresDto.getSecondFromStart() > 5400
                    && scores7095 != null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1053");
                matchSettleScore.setEventName("2H Last-minute Goal (Injury Time)");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7095.getGoal().getHome());
                matchSettleScore.setT2(scores7095.getGoal().getAway());
                list.add(matchSettleScore);
            }

            //查询比分
            Long period15 = 75399L;
            FootballScores scores90 = footballScoresMap.get(period15.toString());

            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >7 && (!standardScoresDto.getPeriodId().equals(31l))
                    && MatchLengthConstant.isPeriodTimeRight(standardScoresDto,100l)  && scores90 !=null) {
                //获取当前阶段的比分 如15分钟的进球
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("108");
                matchSettleScore.setEventName("75:00 - FT");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores90.getGoal().getHome());
                matchSettleScore.setT2(scores90.getGoal().getAway());
                list.add(matchSettleScore);
                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("2016");
                matchSettleScore2.setEventName("CR 75:00 - FT");
                matchSettleScore2.setPeriodId(7l);
                matchSettleScore2.setT1(scores90.getCorner().getHome());
                matchSettleScore2.setT2(scores90.getCorner().getAway());
                list.add(matchSettleScore2);
                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("307");
                matchSettleScore3.setEventName("BK 75:00 - FT");
                matchSettleScore3.setPeriodId(7l);
                matchSettleScore3.setT1(scores90.getFaCard().getHome());
                matchSettleScore3.setT2(scores90.getFaCard().getAway());
                list.add(matchSettleScore3);
            }


        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match90MScoresSettleInitFilter error:",e);
        }
        return list;
    }
    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //下半場
            if(!data.getMatchPeriodId().equals(7L)||data.getSecondsFromStart()<=4499){
                return list;
            }
            //75-80 分钟
            if(data.getSecondsFromStart()<=4799){
                if(data.getEventCode().equals("goal")){
                    list.add("108");
                    list.add("1050");
                }else if(data.getEventCode().equals("corner")){
                    list.add("2016");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("307");
                }
                return list;
            }
            //80-85
            if(data.getSecondsFromStart()<=5099){
                if(data.getEventCode().equals("goal")){
                    list.add("108");
                    list.add("1051");
                }else if(data.getEventCode().equals("corner")){
                    list.add("2016");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("307");
                }
                return list;
            }
            //85-90
            if(data.getSecondsFromStart()<=5399){
                if(data.getEventCode().equals("goal")){
                    list.add("108");
                    list.add("1052");
                }else if(data.getEventCode().equals("corner")){
                    list.add("2016");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("307");
                }
                return list;
            }
        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
