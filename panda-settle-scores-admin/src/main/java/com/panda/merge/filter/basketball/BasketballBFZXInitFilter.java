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

/**
 * 篮球第一节过滤器 上半场 放第二节过滤器  下半场 放第四节过滤器  全场过滤器 放99过滤 加时放40过滤
 * */
@Slf4j
public class BasketballBFZXInitFilter implements IBascketballScoresFilter {


    @Override
    public void filter(Map<String, BasketballScores> basketballScoresMap, CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> before, List<MatchSettleScore> after) {
        try {
            //阶段过滤

            Long period1h = 1l; //上半场
            Long period2h = 2l; //下半场
            Long periodFT = -1l; //全场
            Long periodET = 40l;//加时赛

            Long period13h = 13l;  //第1节比分
            Long period1306 =1306l; //第1节第2个六分钟
            Long period1312 =1312l; //第1节第1个六分钟

            Long period14h = 14l; //第2节比分
            Long period1406 =1406l;//第2节第2个六分钟
            Long period1412 =1412l;//第2节第1个六分钟

            Long period15h = 15l;//第3节比分
            Long period1506 =1506l;//第3节第2个六分钟
            Long period1512 =1512l;//第3节第1个六分钟

            Long period16h = 16l;//第4节比分
            Long period1606 =1606l;//第4节第2个六分钟
            Long period1612 =1612l;//第4节第1个六分钟

            //获得主客队需要阶段比分
            BasketballScores scores1H = basketballScoresMap.get(period1h.toString());
            BasketballScores scores2H = basketballScoresMap.get(period2h.toString());
            BasketballScores scoresFT = basketballScoresMap.get(periodFT.toString());
            BasketballScores scoresET = basketballScoresMap.get(periodET.toString());

            BasketballScores scores13h = basketballScoresMap.get(period13h.toString());
            BasketballScores scores1306 = basketballScoresMap.get(period1306.toString());
            BasketballScores scores1312 = basketballScoresMap.get(period1312.toString());

            BasketballScores scores14h = basketballScoresMap.get(period14h.toString());
            BasketballScores scores1406 = basketballScoresMap.get(period1406.toString());
            BasketballScores scores1412 = basketballScoresMap.get(period1412.toString());

            BasketballScores scores15h = basketballScoresMap.get(period15h.toString());
            BasketballScores scores1506 = basketballScoresMap.get(period1506.toString());
            BasketballScores scores1512 = basketballScoresMap.get(period1512.toString());

            BasketballScores scores16h = basketballScoresMap.get(period16h.toString());
            BasketballScores scores1606 = basketballScoresMap.get(period1606.toString());
            BasketballScores scores1612 = basketballScoresMap.get(period1612.toString());

            //没有全场比分或者数据源不匹配直接返回
            if(scoresFT==null||!standardScoresDto.getDataSourceCode().equals("BFZX")){
                return ;
            }
            //根据list 循环得到当前需要结算的比分
            for (MatchSettleScore matchSettleScore : before) {

                //第1节
                if (matchSettleScore.getSettleNum().equals("bk_q104")){
                    if (scores13h!=null){
                        matchSettleScore.setT1(scores13h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores13h.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q1042")){
                    if (scores1306!=null){
                        matchSettleScore.setT1(scores1306.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1306.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q1041")){
                    if (scores1312!=null){
                        matchSettleScore.setT1(scores1312.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1312.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }

                //第2节
                if (matchSettleScore.getSettleNum().equals("bk_q204")){
                    if (scores14h!=null){
                        matchSettleScore.setT1(scores14h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores14h.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q2042")){
                    if (scores1406!=null){
                        matchSettleScore.setT1(scores1406.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1406.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q2041")){
                    if (scores1412!=null){
                        matchSettleScore.setT1(scores1412.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1412.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }

                //第3节
                if (matchSettleScore.getSettleNum().equals("bk_q304")){
                    if (scores15h!=null){
                        matchSettleScore.setT1(scores15h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores15h.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q3042")){
                    if (scores1506!=null){
                        matchSettleScore.setT1(scores1506.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1506.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q3041")){
                    if (scores1512!=null){
                        matchSettleScore.setT1(scores1512.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1512.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }

                //第4节
                if (matchSettleScore.getSettleNum().equals("bk_q404")){
                    if (scores16h!=null){
                        matchSettleScore.setT1(scores16h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores16h.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q4042")){
                    if (scores1606!=null){
                        matchSettleScore.setT1(scores1606.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1606.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                if (matchSettleScore.getSettleNum().equals("bk_q4041")){
                    if (scores1612!=null){
                        matchSettleScore.setT1(scores1612.getMatchScore().getHome());
                        matchSettleScore.setT2(scores1612.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }

                //上半场
                if (matchSettleScore.getSettleNum().equals("bk_1ht")){
                    if (scores13h!=null&&scores14h!=null){
                        matchSettleScore.setT1(scores13h.getMatchScore().getHome()+scores14h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores13h.getMatchScore().getAway()+scores14h.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }

                }
                //下半场
                if (matchSettleScore.getSettleNum().equals("bk_2ht")){
                    if (scores15h!=null&&scores16h!=null){
                        matchSettleScore.setT1(scores15h.getMatchScore().getHome()+scores16h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores15h.getMatchScore().getAway()+scores16h.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }

                }
                //加时赛
                if (matchSettleScore.getSettleNum().equals("bk_et")){
                    if (scoresET!=null){
                        matchSettleScore.setT1(scoresET.getMatchScore().getHome());
                        matchSettleScore.setT2(scoresET.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                //全场常规赛
                if (matchSettleScore.getSettleNum().equals("bk_ft_rg")){
                    if (scores13h!=null&&scores14h!=null&&scores15h!=null&&scores16h!=null){
                        matchSettleScore.setT1(scores13h.getMatchScore().getHome()+scores14h.getMatchScore().getHome()+scores15h.getMatchScore().getHome()+scores16h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores13h.getMatchScore().getAway()+scores14h.getMatchScore().getAway()+scores15h.getMatchScore().getAway()+scores16h.getMatchScore().getAway());
                        after.add(matchSettleScore);
                    }
                }
                //下半场+加时
                if (matchSettleScore.getSettleNum().equals("bk_2htet")){
                    if (scores15h!=null&&scores16h!=null){
                        matchSettleScore.setT1(scores15h.getMatchScore().getHome()+scores16h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores15h.getMatchScore().getAway()+scores16h.getMatchScore().getAway());
//                        after.add(matchSettleScore);
                    }
                    if (scoresET!=null){
                        matchSettleScore.setT1(matchSettleScore.getT1()+scoresET.getMatchScore().getHome());
                        matchSettleScore.setT2(matchSettleScore.getT2()+scoresET.getMatchScore().getAway());
                    }
                    after.add(matchSettleScore);
                }
                //全场含加时
                if (matchSettleScore.getSettleNum().equals("bk_ft_et")){
                    if (scores13h!=null&&scores14h!=null&&scores15h!=null&&scores16h!=null){
                        matchSettleScore.setT1(scores13h.getMatchScore().getHome()+scores14h.getMatchScore().getHome()+scores15h.getMatchScore().getHome()+scores16h.getMatchScore().getHome());
                        matchSettleScore.setT2(scores13h.getMatchScore().getAway()+scores14h.getMatchScore().getAway()+scores15h.getMatchScore().getAway()+scores16h.getMatchScore().getAway());
//                        after.add(matchSettleScore);
                    }
                    if (scoresET!=null){
                        matchSettleScore.setT1(matchSettleScore.getT1()+scoresET.getMatchScore().getHome());
                        matchSettleScore.setT2(matchSettleScore.getT2()+scoresET.getMatchScore().getAway());
                    }
//                    matchSettleScore.setT1(scoresFT.getMatchScore().getHome());
//                    matchSettleScore.setT2(scoresFT.getMatchScore().getAway());
                    after.add(matchSettleScore);
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
        return list;
    }

}
