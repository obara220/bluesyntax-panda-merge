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
public class Match90ScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);
            //判断是否符合条件
            if (standardScoresDto.getPeriodId() >31
                    && MatchLengthConstant.isPeriodTimeRight(standardScoresDto,100l)) {
                //查询比分
                Long period1h = 6l;
                Long period2h = 7l;
                FootballScores scores1H = footballScoresMap.get(period1h.toString());
                if(scores1H==null){
                    return list;
                }
                FootballScores scores2H = footballScoresMap.get(period2h.toString());
                if(scores2H==null){
                    return list;
                }
                //半场进球比分
                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore.setEventCode("goal");
                matchSettleScore.setSettleNum("109");
                matchSettleScore.setEventName("2HT");
                matchSettleScore.setPeriodId(8l);
                matchSettleScore.setT1(scores2H.getGoal().getHome());
                matchSettleScore.setT2(scores2H.getGoal().getAway());
                list.add(matchSettleScore);
                //半场角球比分
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("202");
                matchSettleScore2.setEventName("2HT CR");
                matchSettleScore2.setPeriodId(8L);
                matchSettleScore2.setT1(scores2H.getCorner().getHome());
                matchSettleScore2.setT2(scores2H.getCorner().getAway());
                list.add(matchSettleScore2);
                //半场罚牌比分
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("308");
                matchSettleScore3.setEventName("BK 2HT");
                matchSettleScore3.setPeriodId(8l);
                matchSettleScore3.setT1(scores2H.getFaCard().getHome());
                matchSettleScore3.setT2(scores2H.getFaCard().getAway());
                list.add(matchSettleScore3);

                //常规赛全场生成规则
                //半场进球比分
                MatchSettleScore matchSettleScoreAll = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreAll.setEventCode("goal");
                matchSettleScoreAll.setSettleNum("1010");
                matchSettleScoreAll.setEventName("FT");
                matchSettleScoreAll.setPeriodId(100l);
                matchSettleScoreAll.setT1(scores2H.getGoal().getHome()+scores1H.getGoal().getHome());
                matchSettleScoreAll.setT2(scores2H.getGoal().getAway()+scores1H.getGoal().getAway());
                list.add(matchSettleScoreAll);
                //半场角球比分
                MatchSettleScore matchSettleScore2All = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2All.setEventCode("corner");
                matchSettleScore2All.setSettleNum("203");
                matchSettleScore2All.setEventName("FT CR");
                matchSettleScore2All.setPeriodId(100L);
                matchSettleScore2All.setT1(scores2H.getCorner().getHome()+scores1H.getCorner().getHome());
                matchSettleScore2All.setT2(scores2H.getCorner().getAway()+scores1H.getCorner().getAway());
                list.add(matchSettleScore2All);
                //半场罚牌比分
                MatchSettleScore matchSettleScore3All = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3All.setEventCode("fa_card");
                matchSettleScore3All.setSettleNum("309");
                matchSettleScore3All.setEventName("BK FT");
                matchSettleScore3All.setPeriodId(100l);
                matchSettleScore3All.setT1(scores2H.getFaCard().getHome()+scores1H.getFaCard().getHome());
                matchSettleScore3All.setT2(scores2H.getFaCard().getAway()+scores1H.getFaCard().getAway());
                list.add(matchSettleScore3All);
            }



        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match90ScoresSettleInitFilter error:",e);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //下半場
            if(!data.getMatchPeriodId().equals(7L)||data.getSecondsFromStart()<=5399){
                return list;
            }
            //90- 分钟

            if(data.getEventCode().equals("goal")){
                list.add("108");
                list.add("1053");
            }else if(data.getEventCode().equals("corner")){
                list.add("2016");
            }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                list.add("307");
            }
            return list;

        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
