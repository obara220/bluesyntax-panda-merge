package com.panda.merge.filter.basketball;


import com.alibaba.fastjson.JSON;
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
public class BasketballMatch16ScoresFilter implements IBascketballScoresFilter {



    @Override
    public void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return ;
        }
        try {
            //阶段过滤
            Long period2h = 2l;
            Long period15h = 15l;
            Long period16h = 16l;
            Long periodFT = -1l;
            Long period1606 =1606l;
            Long period1612 =1612l;
            //获得主客队需要阶段比分
            BasketballScores scores2H = basketballScoresMap.get(period2h.toString());
            //第一节没有直接返回
            BasketballScores scores15H = basketballScoresMap.get(period15h.toString());
            BasketballScores scores16H = basketballScoresMap.get(period16h.toString());
            BasketballScores scoresFT = basketballScoresMap.get(periodFT.toString());
            //第一节没有直接返回
            if(period16h==null&&period2h==null){
                return ;
            }
            if(!( standardScoresDto.getPeriodId().equals(16L)||standardScoresDto.getPeriodId().equals(100L)||standardScoresDto.getPeriodId().equals(2L)||standardScoresDto.getPeriodId().equals(32L)||standardScoresDto.getPeriodId().equals(999L))){
                return;
            }
            //根据list 循环得到当前需要结算的比分
            if(scores16H!=null&&scores15H!=null){
                for (MatchSettleScore matchSettleScore : before) {
                    //特殊玩法匹配比分
                    if(standardScoresDto.getPeriodId().equals(16L)) {
                        if (matchSettleScore.getPeriodId().equals(16l)) {
                            if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())) {
                                Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                                Integer compareInfoScore = BasketBallSettleScoreUtils.compareInfoScore(scores16H.getMatchScore().getHome(), scores16H.getMatchScore().getAway(), infoScore);
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
                            if (matchSettleScore.getSettleNum().equals("bk_q4041")&&standardScoresDto.getSecondFromStart()<360){
                                BasketballScores scores1612 = basketballScoresMap.get(period1612.toString());
                                if (scores1612!=null){
                                    matchSettleScore.setT1(scores1612.getMatchScore().getHome());
                                    matchSettleScore.setT2(scores1612.getMatchScore().getAway());
                                    after.add(matchSettleScore);
                                }
                            }

                        }
                    }
                    if(standardScoresDto.getPeriodId().equals(100L) || standardScoresDto.getPeriodId().equals(999L) || standardScoresDto.getPeriodId().equals(32L)) {
                        if (matchSettleScore.getPeriodId().equals(16l)) {

                            //第一节第二个六分钟
                            if (matchSettleScore.getSettleNum().equals("bk_q4042")){
                                BasketballScores scores1606 = basketballScoresMap.get(period1606.toString());
                                if (scores1606!=null){
                                    matchSettleScore.setT1(scores1606.getMatchScore().getHome());
                                    matchSettleScore.setT2(scores1606.getMatchScore().getAway());
                                    after.add(matchSettleScore);
                                }
                            }

                            if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())) {
                                Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                                Integer compareInfoScore = BasketBallSettleScoreUtils.beforeInfoScore(scores16H.getMatchScore().getHome(), scores16H.getMatchScore().getAway(), infoScore);
                                if (compareInfoScore == 0) {
                                    matchSettleScore.setT1(0);
                                    matchSettleScore.setT2(0);
                                    after.add(matchSettleScore);
                                }
                            }
                        }
                        //一般玩法匹配比分
                        if (matchSettleScore.getPeriodId().equals(304l) && (standardScoresDto.getPeriodId().equals(100L)|| standardScoresDto.getPeriodId().equals(999L) || standardScoresDto.getPeriodId().equals(32L))) {
                            //第4节
                            matchSettleScore.setT1(scores16H.getMatchScore().getHome());
                            matchSettleScore.setT2(scores16H.getMatchScore().getAway());
                            after.add(matchSettleScore);
                            continue;
                        }

                    }
                    //上半场 1l
                    if(standardScoresDto.getPeriodId().equals(100L) || standardScoresDto.getPeriodId().equals(999L) || standardScoresDto.getPeriodId().equals(32L)){
                        if(matchSettleScore.getPeriodId().equals(8l)){
                            if(scores15H==null){
                                //第一节比分不存在无法结算上半场
                                continue;
                            }
                            //第二节+第一介比分
                            matchSettleScore.setT1(scores15H.getMatchScore().getHome()+scores16H.getMatchScore().getHome());
                            matchSettleScore.setT2(scores15H.getMatchScore().getAway()+scores16H.getMatchScore().getAway());
                            //录入下半场的时候总比分不能相同
                            if(!scoresFT.getMatchScore().getHome().equals(scoresFT.getMatchScore().getAway())){
                                after.add(matchSettleScore);
                            }else {
                                //相等的时候只能拿下半场非加时
                                if(matchSettleScore.getSettleNum().equals("bk_2ht")){
                                    after.add(matchSettleScore);
                                }
                            }
                        }
                    }
                }
            }else if(scores2H!=null){
                for (MatchSettleScore matchSettleScore : before) {
                    if(standardScoresDto.getPeriodId().equals(100L) || standardScoresDto.getPeriodId().equals(999L) || standardScoresDto.getPeriodId().equals(32L)) {
                        if (matchSettleScore.getPeriodId().equals(8L)) {
                            //下半场
                            matchSettleScore.setT1(scores2H.getMatchScore().getHome() );
                            matchSettleScore.setT2(scores2H.getMatchScore().getAway() );
                            //录入下半场的时候总比分不能相同
                            if(!scoresFT.getMatchScore().getHome().equals(scoresFT.getMatchScore().getAway())){
                                after.add(matchSettleScore);
                            }else {
                                //相等的时候只能拿下半场非加时
                                if(matchSettleScore.getSettleNum().equals("bk_2ht")){
                                    after.add(matchSettleScore);
                                }
                            }
                          //  break;
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
            //不是第四节
            if (!data.getMatchPeriodId().equals(16l)&&!data.getMatchPeriodId().equals(2l)&&!data.getMatchPeriodId().equals(21l)){
                return list;
            }
            if (data.getMatchPeriodId().equals(16l)){
                list.add("bk_q404");
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
