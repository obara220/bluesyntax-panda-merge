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

/**
 * 篮球第一节过滤器 上半场 放第二节过滤器  下半场 放第四节过滤器  全场过滤器 放99过滤 加时放40过滤
 * */
@Slf4j
public class  BasketballMatch13ScoresFilter implements IBascketballScoresFilter {


    @Override
    public void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return ;
        }
        try {
            //阶段过滤
            Long period1h = 13l;

            Long period1306 =1306l;
            Long period1312 =1312l;

            //获得主客队需要阶段比分
            BasketballScores scores1H = basketballScoresMap.get(period1h.toString());
            //第一节没有直接返回
            if(scores1H==null){
                return ;
            }
            if(!( standardScoresDto.getPeriodId().equals(13L)||standardScoresDto.getPeriodId().equals(301L))){
                return;
            }
            //根据list 循环得到当前需要结算的比分
            for (MatchSettleScore matchSettleScore : before) {
                //特殊玩法匹配比分
                if(standardScoresDto.getPeriodId().equals(13L)) {
                    if (matchSettleScore.getPeriodId().equals(13l)) {
                        if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())) {
                            Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                            Integer compareInfoScore = BasketBallSettleScoreUtils.compareInfoScore(scores1H.getMatchScore().getHome(), scores1H.getMatchScore().getAway(), infoScore);
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
                        if (matchSettleScore.getSettleNum().equals("bk_q1041")&&standardScoresDto.getSecondFromStart()<360){
                            BasketballScores scores1312 = basketballScoresMap.get(period1312.toString());
                            if (scores1312!=null){
                                matchSettleScore.setT1(scores1312.getMatchScore().getHome());
                                matchSettleScore.setT2(scores1312.getMatchScore().getAway());
                                after.add(matchSettleScore);
                            }
                        }
                    }
                }
                //一般玩法匹配比分 第一节结束才有第一节比分
                if(standardScoresDto.getPeriodId().equals(301L)) {
                    if(matchSettleScore.getPeriodId().equals(13l)){

                        //第一节第二个六分钟
                        if (matchSettleScore.getSettleNum().equals("bk_q1042")){
                            BasketballScores scores1306 = basketballScoresMap.get(period1306.toString());
                            if (scores1306!=null){
                                matchSettleScore.setT1(scores1306.getMatchScore().getHome());
                                matchSettleScore.setT2(scores1306.getMatchScore().getAway());
                                after.add(matchSettleScore);
                            }
                        }

                        if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())){
                            Integer infoScore= Integer.parseInt(matchSettleScore.getExtryInfo());
                            Integer compareInfoScore = BasketBallSettleScoreUtils.beforeInfoScore(scores1H.getMatchScore().getHome(),scores1H.getMatchScore().getAway(),infoScore);
                            if(compareInfoScore==0){
                                matchSettleScore.setT1(0);
                                matchSettleScore.setT2(0);
                                after.add(matchSettleScore);
                            }
                        }
                    }
                    if (matchSettleScore.getPeriodId().equals(301l)) {
                        matchSettleScore.setT1(scores1H.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1H.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }

                }
            }
            //返回需要匹配的结果~到下一个过滤任务
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":BasketballMatch13ScoresFilter error:",e);
        }
        return ;
    }

    @Override
    public List<String> deleteEventPeriodScoreFilter(MatchEventInfo data, List<String> list) {
        try {
            log.info("deleteEvent:"+data.getLinkId()+":"+data.getMatchPeriodId());
            //不是第一节
            if (!data.getMatchPeriodId().equals(13l)&&!data.getMatchPeriodId().equals(1l)&&!data.getMatchPeriodId().equals(21l)){
                return list;
            }
            if (data.getMatchPeriodId().equals(13l)){
                list.add("bk_q104");
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
