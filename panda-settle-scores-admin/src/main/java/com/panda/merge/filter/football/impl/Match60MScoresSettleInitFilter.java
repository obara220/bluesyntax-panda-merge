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
public class Match60MScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);

            //查询比分
            Long period2999 = 7050L;
            FootballScores scores7050 = footballScoresMap.get(period2999.toString());

            //判断45:00 - 49:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 2999
                    && scores7050!=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1044");
                matchSettleScore.setEventName("45:00 - 49:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7050.getGoal().getHome());
                matchSettleScore.setT2(scores7050.getGoal().getAway());
                list.add(matchSettleScore);

            }

            //查询比分
            Long period7055 = 7055L;
            FootballScores scores7055 = footballScoresMap.get(period7055.toString());

            //判断35:00 - 39:59分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 3299
                    && scores7055 !=null) {
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1045");
                matchSettleScore.setEventName("50:00 - 54:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7055.getGoal().getHome());
                matchSettleScore.setT2(scores7055.getGoal().getAway());
                list.add(matchSettleScore);

            }

            //查询比分
            Long period7060= 7060L;
            FootballScores scores7060 = footballScoresMap.get(period7060.toString());

            //判断55:00 - 59:59	分钟是否符合条件
            if (standardScoresDto.getPeriodId() >= 7 && standardScoresDto.getSecondFromStart() > 3599
                    && scores7060 != null ) {

                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("1046");
                matchSettleScore.setEventName("55:00 - 59:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores7060.getGoal().getHome());
                matchSettleScore.setT2(scores7060.getGoal().getAway());
                list.add(matchSettleScore);
            }




                //查询比分
                Long period599 = 73599L;
                FootballScores scores599 = footballScoresMap.get(period599.toString());
            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >=7 && standardScoresDto.getSecondFromStart() >3599
            && scores599!=null) {
                //获取当前阶段的比分 如15分钟的进球
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("106");
                matchSettleScore.setEventName("1HT - 59:59");
                matchSettleScore.setPeriodId(7l);
                matchSettleScore.setT1(scores599.getGoal().getHome());
                matchSettleScore.setT2(scores599.getGoal().getAway());
                list.add(matchSettleScore);
                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("2014");
                matchSettleScore2.setEventName("CR HT - 59:59");
                matchSettleScore2.setPeriodId(7l);
                matchSettleScore2.setT1(scores599.getCorner().getHome());
                matchSettleScore2.setT2(scores599.getCorner().getAway());
                list.add(matchSettleScore2);
                //获取当前阶段的比分 如15分钟的罚牌
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("305");
                matchSettleScore3.setEventName("BK HT - 59:59");
                matchSettleScore3.setPeriodId(7l);
                matchSettleScore3.setT1(scores599.getFaCard().getHome());
                matchSettleScore3.setT2(scores599.getFaCard().getAway());
                list.add(matchSettleScore3);
            }
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match60MScoresSettleInitFilter error:",e);
        }
        return list;
    }
    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //下半場
            if(!data.getMatchPeriodId().equals(7L)){
                return list;
            }
            //45-50 分钟
            if(data.getSecondsFromStart()<=2999){
                if(data.getEventCode().equals("goal")){
                    list.add("106");
                    list.add("1044");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2014");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("305");

                }
                return list;
            }
            //50-55
            if(data.getSecondsFromStart()<=3299){
                if(data.getEventCode().equals("goal")){
                    list.add("106");
                    list.add("1045");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2014");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("305");
                }
                return list;
            }
            //55-60
            if(data.getSecondsFromStart()<=3599){
                if(data.getEventCode().equals("goal")){
                    list.add("106");
                    list.add("1046");

                }else if(data.getEventCode().equals("corner")){

                    list.add("2014");
                }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                    list.add("305");
                }
                return list;
            }
        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
