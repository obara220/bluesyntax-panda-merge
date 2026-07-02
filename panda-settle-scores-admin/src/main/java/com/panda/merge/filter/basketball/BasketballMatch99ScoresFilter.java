package com.panda.merge.filter.basketball;


import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class BasketballMatch99ScoresFilter implements IBascketballScoresFilter {


    @Override
    public  void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        if (standardScoresDto.getDataSourceCode().equals("BFZX")){
            return ;
        }
        try {
            //阶段过滤
            //获得主客队需要阶段比分
            Long periodFT = -1l;
            BasketballScores scoresFT = basketballScoresMap.get(periodFT.toString());
            //第一节没有直接返回
            if(scoresFT==null){
                return ;
            }
            //根据list 循环得到当前需要结算的比分
                for (MatchSettleScore matchSettleScore : before) {
                    //特殊玩法匹配比分
                    if (matchSettleScore.getSettleNum().contains("bk_1st_")) {
                        Integer infoScore = Integer.parseInt(matchSettleScore.getExtryInfo());
                        Integer compareInfoScore = BasketBallSettleScoreUtils.compareInfoScore(scoresFT.getMatchScore().getHome(), scoresFT.getMatchScore().getAway(), infoScore);
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
                    if(!( standardScoresDto.getPeriodId().equals(100L)||standardScoresDto.getPeriodId().equals(32L)||standardScoresDto.getPeriodId().equals(999L))){
                        continue;
                    }
                    //全场得分匹配
                    if((standardScoresDto.getPeriodId().equals(100l)||standardScoresDto.getPeriodId().equals(32L))&&matchSettleScore.getSettleNum().equals("bk_ft_rg")){
                        matchSettleScore.setT1(scoresFT.getMatchScore().getHome());
                        matchSettleScore.setT2(scoresFT.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                    //全场含加时匹配
                    if(standardScoresDto.getPeriodId().equals(999L)&&matchSettleScore.getSettleNum().equals("bk_ft_et")){
                        matchSettleScore.setT1(scoresFT.getMatchScore().getHome());
                        matchSettleScore.setT2(scoresFT.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }

                    if(StringUtils.isNotEmpty(matchSettleScore.getExtryInfo())){
                        Integer infoScore= Integer.parseInt(matchSettleScore.getExtryInfo());
                        Integer compareInfoScore = BasketBallSettleScoreUtils.beforeInfoScore(scoresFT.getMatchScore().getHome(),scoresFT.getMatchScore().getAway(),infoScore);
                        if(compareInfoScore==0){
                            matchSettleScore.setT1(0);
                            matchSettleScore.setT2(0);
                            after.add(matchSettleScore);
                        }
                    }
                }
            //返回需要匹配的结果~到下一个过滤任务
        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":BasketballMatch99ScoresFilter error:",e);
        }
        return ;
    }

    @Override
    public List<String> deleteEventPeriodScoreFilter(MatchEventInfo data, List<String> list) {
        return list;
    }
}
