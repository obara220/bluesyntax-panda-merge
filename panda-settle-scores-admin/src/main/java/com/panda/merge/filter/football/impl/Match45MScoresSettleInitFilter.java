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
public class Match45MScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {

                //查询比分
                Long period6035 = 6035L;
                FootballScores scores6035 = footballScoresMap.get(period6035.toString());

            //判断30:00 - 34:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 2099
            && scores6035!=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1040");
                matchSettleScore.setEventName("30:00 - 34:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6035.getGoal().getHome());
                matchSettleScore.setT2(scores6035.getGoal().getAway());
                list.add(matchSettleScore);

            }

                //查询比分
                Long period6040 = 6040L;
                FootballScores scores6040 = footballScoresMap.get(period6040.toString());

            //判断35:00 - 39:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 2399
                    && scores6040!=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1041");
                matchSettleScore.setEventName("35:00 - 39:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6040.getGoal().getHome());
                matchSettleScore.setT2(scores6040.getGoal().getAway());
                list.add(matchSettleScore);

            }

            //查询比分
            Long period6045 = 6045L;
            FootballScores scores6045 = footballScoresMap.get(period6045.toString());

            //判断40:00 - 45:00分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 2700
            && scores6045 != null ) {

                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1042");
                matchSettleScore.setEventName("40:00 - 45:00");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6045.getGoal().getHome());
                matchSettleScore.setT2(scores6045.getGoal().getAway());
                list.add(matchSettleScore);

            }
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match45MScoresSettleInitFilter error:",e);
        }
        return list;
    }
    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //上半場
            if(data.getMatchPeriodId()>6||data.getSecondsFromStart()<=1799){
                return list;
            }
            //30-35 分钟
            if(data.getSecondsFromStart()<=2099){
                if(data.getEventCode().equals("goal")){
                    list.add("104");
                    list.add("1040");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2013");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("303");

                }
                return list;
            }
            //35-40
            if(data.getSecondsFromStart()<=2399){
                if(data.getEventCode().equals("goal")){
                    list.add("104");
                    list.add("1041");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2013");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("303");

                }
                return list;
            }
            //40-45
            if(data.getSecondsFromStart()<=2699){
                if(data.getEventCode().equals("goal")){
                    list.add("104");
                    list.add("1042");

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
