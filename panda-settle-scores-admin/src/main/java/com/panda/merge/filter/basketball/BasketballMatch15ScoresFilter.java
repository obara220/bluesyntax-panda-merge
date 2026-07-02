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
public class BasketballMatch15ScoresFilter implements IBascketballScoresFilter {


    @Override
    public void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return ;
        }
        try {
            //阶段过滤
            Long period15h = 15l;
            Long period1506 =1506l;
            Long period1512 =1512l;
            //获得主客队需要阶段比分
            BasketballScores scores15H = basketballScoresMap.get(period15h.toString());
            //第一节没有直接返回
            if(scores15H==null){
                return ;
            }
            if(!( standardScoresDto.getPeriodId().equals(15L)||standardScoresDto.getPeriodId().equals(303L))){
                return;
            }
            //根据list 循环得到当前需要结算的比分
            for (MatchSettleScore matchSettleScore : before) {
                //特殊玩法匹配比分
                if(standardScoresDto.getPeriodId().equals(15L)) {
                    if (matchSettleScore.getPeriodId().equals(15L)) {
                        if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())) {
                            Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                            Integer compareInfoScore = BasketBallSettleScoreUtils.compareInfoScore(scores15H.getMatchScore().getHome(), scores15H.getMatchScore().getAway(), infoScore);
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
                        if (matchSettleScore.getSettleNum().equals("bk_q3041")&&standardScoresDto.getSecondFromStart()<360){
                            BasketballScores scores1512 = basketballScoresMap.get(period1512.toString());
                            if (scores1512!=null){
                                matchSettleScore.setT1(scores1512.getMatchScore().getHome());
                                matchSettleScore.setT2(scores1512.getMatchScore().getAway());
                                after.add(matchSettleScore);
                            }
                        }


                    }
                }
                //一般玩法匹配比分
                if(standardScoresDto.getPeriodId().equals(303L)) {
                    if(matchSettleScore.getPeriodId().equals(15L)){
                        //第一节第二个六分钟
                        if (matchSettleScore.getSettleNum().equals("bk_q3042")){
                            BasketballScores scores1506 = basketballScoresMap.get(period1506.toString());
                            if (scores1506!=null){
                                matchSettleScore.setT1(scores1506.getMatchScore().getHome());
                                matchSettleScore.setT2(scores1506.getMatchScore().getAway());
                                after.add(matchSettleScore);
                            }
                        }

                        if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())) {
                            Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                            Integer compareInfoScore = BasketBallSettleScoreUtils.beforeInfoScore(scores15H.getMatchScore().getHome(), scores15H.getMatchScore().getAway(), infoScore);
                            if (compareInfoScore == 0) {
                                matchSettleScore.setT1(0);
                                matchSettleScore.setT2(0);
                                after.add(matchSettleScore);
                            }
                        }
                    }
                    if (matchSettleScore.getPeriodId().equals(303L) && standardScoresDto.getPeriodId().equals(303L)) {
                        matchSettleScore.setT1(scores15H.getMatchScore().getHome());
                        matchSettleScore.setT2(scores15H.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }

            }
            //返回需要匹配的结果~到下一个过滤任务
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":BasketballMatch15ScoresFilter error:",e);
        }
        return ;
    }

    @Override
    public List<String> deleteEventPeriodScoreFilter(MatchEventInfo data, List<String> list) {
        try {
            log.info("deleteEvent:"+data.getLinkId()+":"+data.getMatchPeriodId());
            //不是第三节
            if (!data.getMatchPeriodId().equals(15l)&&!data.getMatchPeriodId().equals(2l)&&!data.getMatchPeriodId().equals(21l)){
                return list;
            }
            if (data.getMatchPeriodId().equals(15l)){
                list.add("bk_q304");
            }
            if (data.getMatchPeriodId().equals(2l)){
                list.add("bk_2ht");
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
