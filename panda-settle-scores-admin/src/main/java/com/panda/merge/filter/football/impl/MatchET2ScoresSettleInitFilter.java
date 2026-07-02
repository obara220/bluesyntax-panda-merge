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
public class MatchET2ScoresSettleInitFilter implements IMatchScoresSettleInitFilter {

    @Override
    public List<MatchSettleScore> filter(Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return list;
        }
        try {
//            Map<String, Object> allPeriodScores = standardScoresDto.getScores();
//            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);
            //判断是否符合条件
            if (footballScoresMap.get("42")!=null&&(!standardScoresDto.getPeriodId().equals(42l))&&(!standardScoresDto.getPeriodId().equals(33l))) {
                if(!MatchLengthConstant.isPeriodTimeRight(standardScoresDto,110l)){
                    return list;
                }
                //查询比分
                Long period1h = 41l;
                Long period2h = 42l;
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
                matchSettleScore.setSettleNum("1018");
                matchSettleScore.setEventName("2ET");
                matchSettleScore.setPeriodId(43l);
                matchSettleScore.setT1(scores2H.getGoal().getHome());
                matchSettleScore.setT2(scores2H.getGoal().getAway());
                list.add(matchSettleScore);
                //半场角球比分
                MatchSettleScore matchSettleScore2 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2.setEventCode("corner");
                matchSettleScore2.setSettleNum("207");
                matchSettleScore2.setEventName("2ET CR");
                matchSettleScore2.setPeriodId(43L);
                matchSettleScore2.setT1(scores2H.getCorner().getHome());
                matchSettleScore2.setT2(scores2H.getCorner().getAway());
                list.add(matchSettleScore2);
                //半场罚牌比分
                MatchSettleScore matchSettleScore3 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3.setEventCode("fa_card");
                matchSettleScore3.setSettleNum("3017");
                matchSettleScore3.setEventName("2ET BK");
                matchSettleScore3.setPeriodId(43l);
                matchSettleScore3.setT1(scores2H.getFaCard().getHome());
                matchSettleScore3.setT2(scores2H.getFaCard().getAway());
                list.add(matchSettleScore3);

                //加时赛全场生成规则
                //加时赛全场进球比分
                MatchSettleScore matchSettleScoreAll = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreAll.setEventCode("goal");
                matchSettleScoreAll.setSettleNum("1019");
                matchSettleScoreAll.setEventName("ET");
                matchSettleScoreAll.setPeriodId(110l);
                matchSettleScoreAll.setT1(scores2H.getGoal().getHome()+scores1H.getGoal().getHome());
                matchSettleScoreAll.setT2(scores2H.getGoal().getAway()+scores1H.getGoal().getAway());
                list.add(matchSettleScoreAll);
                //加时赛全场角球比分
                MatchSettleScore matchSettleScore2All = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2All.setEventCode("corner");
                matchSettleScore2All.setSettleNum("208");
                matchSettleScore2All.setEventName("ET CR");
                matchSettleScore2All.setPeriodId(110L);
                matchSettleScore2All.setT1(scores2H.getCorner().getHome()+scores1H.getCorner().getHome());
                matchSettleScore2All.setT2(scores2H.getCorner().getAway()+scores1H.getCorner().getAway());
                list.add(matchSettleScore2All);
                //加时赛全场罚牌比分
                MatchSettleScore matchSettleScore3All = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3All.setEventCode("fa_card");
                matchSettleScore3All.setSettleNum("3018");
                matchSettleScore3All.setEventName("ET BK");
                matchSettleScore3All.setPeriodId(110l);
                matchSettleScore3All.setT1(scores2H.getFaCard().getHome()+scores1H.getFaCard().getHome());
                matchSettleScore3All.setT2(scores2H.getFaCard().getAway()+scores1H.getFaCard().getAway());
                list.add(matchSettleScore3All);
            }
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":MatchET2ScoresSettleInitFilter error:",e);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {

        try {
            //駕駛下半場
            if(!data.getMatchPeriodId().equals(42L)){
                return list;
            }

            if(data.getEventCode().equals("goal")){
                list.add("1018");
            }else if(data.getEventCode().equals("corner")){
                list.add("207");
            }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
                list.add("3017");
            }
            return list;

        }catch (Exception e){
            log.error("Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }
}
