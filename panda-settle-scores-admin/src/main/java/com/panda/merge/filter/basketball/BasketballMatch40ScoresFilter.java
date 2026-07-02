package com.panda.merge.filter.basketball;


import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.filter.football.IMatchScoresSettleInitFilter;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class BasketballMatch40ScoresFilter implements IBascketballScoresFilter {


    @Override
    public void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return ;
        }
        try {
            //加时赛处理机制
            //110 阶段才处理
            Long period2h = 2l;
            Long period15h = 15l;
            Long period16h = 16l;
            Long periodET = 40l;
            Long periodFT = -1l;
            //获得主客队需要阶段比分
            BasketballScores scores2H = basketballScoresMap.get(period2h.toString());
            //第一节没有直接返回
            BasketballScores scores15H = basketballScoresMap.get(period15h.toString());
            BasketballScores scores16H = basketballScoresMap.get(period16h.toString());
            BasketballScores scoresFT = basketballScoresMap.get(periodFT.toString());

            BasketballScores scoresET = basketballScoresMap.get(periodET.toString());

            //第一节没有直接返回
            if(period16h==null&&period2h==null){
                return ;
            }

            if(!( standardScoresDto.getPeriodId().equals(110L)||standardScoresDto.getPeriodId().equals(999L))){
                return;
            }

            //根据list 循环得到当前需要结算的比分
            if(scores16H!=null&&scores15H!=null){
                for (MatchSettleScore matchSettleScore : before) {
                    //上半场 1l
                    if(standardScoresDto.getPeriodId().equals(110L)||standardScoresDto.getPeriodId().equals(999L)){
                        //必须下半场而且含加时
                        if(matchSettleScore.getPeriodId().equals(8l)&&matchSettleScore.getSettleNum().equals("bk_2htet")){
                            if(scores15H==null){
                                //第一节比分不存在无法结算上半场
                                continue;
                            }
                            //第二节+第一介比分
                            matchSettleScore.setT1(scores15H.getMatchScore().getHome()+scores16H.getMatchScore().getHome());
                            matchSettleScore.setT2(scores15H.getMatchScore().getAway()+scores16H.getMatchScore().getAway());
                            //加时赛
                            if(scoresET!=null){
                                matchSettleScore.setT1(matchSettleScore.getT1()+scoresET.getMatchScore().getHome());
                                matchSettleScore.setT2(matchSettleScore.getT2()+scoresET.getMatchScore().getAway());
                            }
                            //录入下半场的时候总比分不能相同
                            if(!scoresFT.getMatchScore().getHome().equals(scoresFT.getMatchScore().getAway())){
                                after.add(matchSettleScore);
                            }
                        }
                        if (matchSettleScore.getPeriodId().equals(9L)&&matchSettleScore.getSettleNum().equals("bk_et")){
                            matchSettleScore.setT1(scoresET.getMatchScore().getHome());
                            matchSettleScore.setT2(scoresET.getMatchScore().getAway());
                            after.add(matchSettleScore);
                        }
                    }
                }
            }else if(scores2H!=null){
                for (MatchSettleScore matchSettleScore : before) {
                    if(standardScoresDto.getPeriodId().equals(110L)||standardScoresDto.getPeriodId().equals(999L)) {
                        //必须下半场而且含加时
                        if (matchSettleScore.getPeriodId().equals(8L)&&matchSettleScore.getSettleNum().equals("bk_2htet")) {
                            //下半场
                            matchSettleScore.setT1(scores2H.getMatchScore().getHome() );
                            matchSettleScore.setT2(scores2H.getMatchScore().getAway() );
                            //加时赛
                            if(scoresET!=null){
                                matchSettleScore.setT1(matchSettleScore.getT1()+scoresET.getMatchScore().getHome());
                                matchSettleScore.setT2(matchSettleScore.getT2()+scoresET.getMatchScore().getAway());
                            }
                            //录入下半场的时候总比分不能相同
                            if(!scoresFT.getMatchScore().getHome().equals(scoresFT.getMatchScore().getAway())){
                                after.add(matchSettleScore);
                            }
                        }
                        if (matchSettleScore.getPeriodId().equals(9L)&&matchSettleScore.getSettleNum().equals("bk_et")){
                            matchSettleScore.setT1(scoresET.getMatchScore().getHome());
                            matchSettleScore.setT2(scoresET.getMatchScore().getAway());
                            after.add(matchSettleScore);
                        }
                    }
                }
            }


        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match15MScoresSettleInitFilter error:",e);
        }
        return ;
    }

    @Override
    public List<String> deleteEventPeriodScoreFilter(MatchEventInfo data, List<String> list) {
        try {
            log.info("deleteEvent:"+data.getLinkId()+":"+data.getMatchPeriodId());
            //不是加时赛
            if (data.getMatchPeriodId()!=40){
                return list;
            }
            list.add("bk_et");

        }catch (Exception e){
            log.error("BasketballMatch13ScoresFilter deleteEventPeriodScoreFilter error",e);
        }
        return list;
    }
}
