package com.panda.merge.filter.basketball;


import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.filter.football.IMatchScoresSettleInitFilter;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import com.panda.merge.utils.FootBallMatchSettleScoreUtils;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class BasketballMatch14ScoresFilter implements IBascketballScoresFilter {



    @Override
    public void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return ;
        }
        try {
            //阶段过滤
            Long period1h = 1l;
            Long period14h = 14l;
            Long period13h = 13l;
            Long period1406 =1406l;
            Long period1412 =1412l;
            //获得主客队需要阶段比分
            BasketballScores scores1H = basketballScoresMap.get(period1h.toString());
            //第一节没有直接返回
            BasketballScores scores14H = basketballScoresMap.get(period14h.toString());
            BasketballScores scores13H = basketballScoresMap.get(period13h.toString());
            //第一节没有直接返回
            if(scores14H==null&&scores1H==null){
                return ;
            }

            if(!( standardScoresDto.getPeriodId().equals(14L)||standardScoresDto.getPeriodId().equals(302L)||standardScoresDto.getPeriodId().equals(1L)||standardScoresDto.getPeriodId().equals(31L))){
                return;
            }
            //根据list 循环得到当前需要结算的比分
            if(scores14H!=null){
                for (MatchSettleScore matchSettleScore : before) {
                    //特殊玩法匹配比分
                    if(standardScoresDto.getPeriodId().equals(14L)) {
                        if (matchSettleScore.getPeriodId().equals(14l)) {
                            if (StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())) {
                                Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                                Integer compareInfoScore = BasketBallSettleScoreUtils.compareInfoScore(scores14H.getMatchScore().getHome(), scores14H.getMatchScore().getAway(), infoScore);
                                if (compareInfoScore == 1) {
                                    matchSettleScore.setT1(1);
                                    matchSettleScore.setT2(0);
                                    after.add(matchSettleScore);
                                } else if (compareInfoScore == -1) {
                                    matchSettleScore.setT1(0);
                                    matchSettleScore.setT2(1);
                                    after.add(matchSettleScore);
                                } else {

                                }
                            }
                            //第一节第一个六分钟
                            if (matchSettleScore.getSettleNum().equals("bk_q2041")&&standardScoresDto.getSecondFromStart()<360){
                                BasketballScores scores1412 = basketballScoresMap.get(period1412.toString());
                                if (scores1412!=null){
                                    matchSettleScore.setT1(scores1412.getMatchScore().getHome());
                                    matchSettleScore.setT2(scores1412.getMatchScore().getAway());
                                    after.add(matchSettleScore);
                                }
                            }

                        }
                    }
                    //一般玩法匹配比分
                    if(standardScoresDto.getPeriodId().equals(302l)){
                        if(matchSettleScore.getPeriodId().equals(14L)){
                            //第一节第二个六分钟
                            if (matchSettleScore.getSettleNum().equals("bk_q2042")){
                                BasketballScores scores1406 = basketballScoresMap.get(period1406.toString());
                                if (scores1406!=null){
                                    matchSettleScore.setT1(scores1406.getMatchScore().getHome());
                                    matchSettleScore.setT2(scores1406.getMatchScore().getAway());
                                    after.add(matchSettleScore);
                                }
                            }
                            if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())) {
                                Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                                Integer compareInfoScore = BasketBallSettleScoreUtils.beforeInfoScore(scores14H.getMatchScore().getHome(), scores14H.getMatchScore().getAway(), infoScore);
                                if (compareInfoScore == 0) {
                                    matchSettleScore.setT1(0);
                                    matchSettleScore.setT2(0);
                                    after.add(matchSettleScore);
                                }
                            }
                        }
                        //第二节
                        if (matchSettleScore.getPeriodId().equals(302l)) {
                            matchSettleScore.setT1(scores14H.getMatchScore().getHome());
                            matchSettleScore.setT2(scores14H.getMatchScore().getAway());
                            after.add(matchSettleScore);
                            continue;
                        }

                    }
                    //上半场 1l
                    if(standardScoresDto.getPeriodId().equals(302L)){
                        if(matchSettleScore.getPeriodId().equals(31l)){
                            if(scores13H==null){
                                //第一节比分不存在无法结算上半场
                                continue;
                            }
                            //第二节+第一介比分
                            matchSettleScore.setT1(scores13H.getMatchScore().getHome()+scores14H.getMatchScore().getHome());
                            matchSettleScore.setT2(scores13H.getMatchScore().getAway()+scores14H.getMatchScore().getAway());
                            after.add(matchSettleScore);

                        }
                    }
                }
            }else if(scores1H!=null){
                for (MatchSettleScore matchSettleScore : before) {
                    if(standardScoresDto.getPeriodId().equals(31L)) {
                        if (matchSettleScore.getPeriodId().equals(31L)) {
                            //上半场
                            matchSettleScore.setT1(scores1H.getMatchScore().getHome() );
                            matchSettleScore.setT2(scores1H.getMatchScore().getAway() );
                            after.add(matchSettleScore);
                            break;
                        }
                    }
                }
            }

            //返回需要匹配的结果~到下一个过滤任务
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":BasketballMatch14ScoresFilter error:",e);
        }
        return ;
    }

    @Override
    public List<String> deleteEventPeriodScoreFilter(MatchEventInfo data, List<String> list) {
        try {
            log.info("deleteEvent:"+data.getLinkId()+":"+data.getMatchPeriodId());
            //不是第二节或者第二节休息
            if (!data.getMatchPeriodId().equals(14l)&&!data.getMatchPeriodId().equals(1l)&&!data.getMatchPeriodId().equals(21l)){
                return list;
            }
            if (data.getMatchPeriodId().equals(14l)){
                list.add("bk_q204");
            }
            if (data.getMatchPeriodId().equals(1l)){
                list.add("bk_1ht");
            }
            if (data.getMatchPeriodId().equals(21l)){
                list.add("bk_ft_rg");
            }

        }catch (Exception e){
            log.error("BasketballMatch13ScoresFilter deleteEventPeriodScoreFilter error",e);
        }
        return list;
    }
}
