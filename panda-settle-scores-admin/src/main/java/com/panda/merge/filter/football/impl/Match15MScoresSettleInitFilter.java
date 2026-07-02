package com.panda.merge.filter.football.impl;


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
public class Match15MScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter( Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);
            //BT数据源 过滤 15分钟的操作
            if (standardScoresDto.getPeriodId() > 0&& (!standardScoresDto.getDataSourceCode().equals("BT")&&!standardScoresDto.getDataSourceCode().equals("LS")&& (!standardScoresDto.getDataSourceCode().equals("1X")))) {
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("kick_off");
                matchSettleScore.setSettleNum("101");
                matchSettleScore.setEventName("Kick-off");
                matchSettleScore.setPeriodId(6l);
                //查询比分
                FootballScores wholeScore = footballScoresMap.get(JsonMapUtils.WHOLE_SCORE_PERIOD);
                if(wholeScore==null){
                    return list;
                }
                //比分必须有 1  阶段>0 即可触发
                if(wholeScore.getKickOff().getHome()!=null&&wholeScore.getKickOff().getAway()!=null&&
                        (wholeScore.getKickOff().getHome()!=0||wholeScore.getKickOff().getAway()!=0)){
                    matchSettleScore.setT1(wholeScore.getKickOff().getHome());
                    matchSettleScore.setT2(wholeScore.getKickOff().getAway());
                    list.add(matchSettleScore);
                }

            }

            //查询比分
            Long period6005 = 6005L;
            FootballScores scores6005 = footballScoresMap.get(period6005.toString());

            //判断5分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 299
                    && scores6005 != null) {


                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1034");
                matchSettleScore.setEventName("00:00 - 04:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6005.getGoal().getHome());
                matchSettleScore.setT2(scores6005.getGoal().getAway());
                list.add(matchSettleScore);

            }

            //查询比分
            Long period6010 = 6010L;
            FootballScores scores6010 = footballScoresMap.get(period6010.toString());

            //判断5-10分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 599 && scores6010 != null) {

                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1035");
                matchSettleScore.setEventName("5:00 - 9:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6010.getGoal().getHome());
                matchSettleScore.setT2(scores6010.getGoal().getAway());
                list.add(matchSettleScore);


            }

                //查询比分
                Long period6015 = 6015L;
                FootballScores scores6015 = footballScoresMap.get(period6015.toString());

            //判断10-15分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() > 899
                    && scores6015!=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1036");
                matchSettleScore.setEventName("10:00 - 14:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores6015.getGoal().getHome());
                matchSettleScore.setT2(scores6015.getGoal().getAway());
                list.add(matchSettleScore);
            }


            //查询比分
            Long period15 = 60899L;
            FootballScores scores15 = footballScoresMap.get(period15.toString());
            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >= 6 && standardScoresDto.getSecondFromStart() >
                    899 && scores15!=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("102");
                matchSettleScore.setEventName("00:00 - 14:59");
                matchSettleScore.setPeriodId(6l);
                matchSettleScore.setT1(scores15.getGoal().getHome());
                matchSettleScore.setT2(scores15.getGoal().getAway());
                list.add(matchSettleScore);

                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("2011");
                matchSettleScore2.setEventName("CR 00:00 - 14:59");
                matchSettleScore2.setPeriodId(6l);
                matchSettleScore2.setT1(scores15.getCorner().getHome());
                matchSettleScore2.setT2(scores15.getCorner().getAway());
                list.add(matchSettleScore2);
                //加0-15 罚牌

                //获取当前阶段的比分 如15分钟的罚牌
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("301");
                matchSettleScore3.setEventName("BK 00:00 - 14:59");
                matchSettleScore3.setPeriodId(6l);
                matchSettleScore3.setT1(scores15.getFaCard().getHome());
                matchSettleScore3.setT2(scores15.getFaCard().getAway());
                list.add(matchSettleScore3);


            }

        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //上半場
            if(data.getMatchPeriodId()>6){
                return list;
            }
            //0-5 分钟
            if(data.getSecondsFromStart()<=299){
                if(data.getEventCode().equals("goal")){
                    list.add("102");
                    list.add("1034");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2011");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("301");

                }
                return list;
            }
            //5-10
            if(data.getSecondsFromStart()<=599){
                if(data.getEventCode().equals("goal")){
                    list.add("102");
                    list.add("1035");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2011");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("301");

                }
                return list;
            }
            //10-15
            if(data.getSecondsFromStart()<=899){
                if(data.getEventCode().equals("goal")){
                    list.add("102");
                    list.add("1036");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2011");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("301");

                }
                return list;
            }
        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
