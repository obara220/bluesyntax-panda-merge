package com.panda.merge.filter.football.impl;


import com.panda.merge.constant.MatchLengthConstant;
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
public class MatchBFZXScoresSettleInitFilter implements IMatchScoresSettleInitFilter {


    @Override
    public List<MatchSettleScore> filter( Map<String, FootballScores> footballScoresMap,CommonThirdScoresDto standardScoresDto, List<MatchSettleScore> list) {
        try {
            if (!standardScoresDto.getDataSourceCode().equals("BFZX")){
                return list;
            }
//            //查询比分
            FootballScores wholeScore = footballScoresMap.get(JsonMapUtils.WHOLE_SCORE_PERIOD);
//            if(wholeScore==null){
//                return list;
//            }

            //开球
//            if (standardScoresDto.getPeriodId() > 0) {
//                MatchSettleScore matchSettleScore = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
//                matchSettleScore.setEventCode("kick_off");
//                matchSettleScore.setSettleNum("101");
//                matchSettleScore.setEventName("Kick-off");
//                matchSettleScore.setPeriodId(6l);
//                //比分必须有 1  阶段>0 即可触发
//                if(wholeScore.getKickOff().getHome()!=null&&wholeScore.getKickOff().getAway()!=null&&
//                        (wholeScore.getKickOff().getHome()!=0||wholeScore.getKickOff().getAway()!=0)){
//                    matchSettleScore.setT1(wholeScore.getKickOff().getHome());
//                    matchSettleScore.setT2(wholeScore.getKickOff().getAway());
//                    list.add(matchSettleScore);
//                }
//
//            }

            //查询比分
            /*----------------------------------第1个15分钟----------------------*/
            Long period6005 = 6005L;
            FootballScores scores6005 = footballScoresMap.get(period6005.toString());
            if (scores6005!=null){
                MatchSettleScore matchSettleScore6005 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6005.setEventCode("goal");
                matchSettleScore6005.setSettleNum("1034");
                matchSettleScore6005.setEventName("00:00 - 04:59");
                matchSettleScore6005.setPeriodId(6l);
                matchSettleScore6005.setT1(scores6005.getGoal().getHome());
                matchSettleScore6005.setT2(scores6005.getGoal().getAway());
                list.add(matchSettleScore6005);
            }


            Long period6010 = 6010L;
            FootballScores scores6010 = footballScoresMap.get(period6010.toString());
            if (scores6010!=null){
                MatchSettleScore matchSettleScore6010 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6010.setEventCode("goal");
                matchSettleScore6010.setSettleNum("1035");
                matchSettleScore6010.setEventName("5:00 - 9:59");
                matchSettleScore6010.setPeriodId(6l);
                matchSettleScore6010.setT1(scores6010.getGoal().getHome());
                matchSettleScore6010.setT2(scores6010.getGoal().getAway());
                list.add(matchSettleScore6010);
            }


            Long period6015 = 6015L;
            FootballScores scores6015 = footballScoresMap.get(period6015.toString());
            if (scores6015!=null){
                MatchSettleScore matchSettleScore6015 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6015.setEventCode("goal");
                matchSettleScore6015.setSettleNum("1036");
                matchSettleScore6015.setEventName("10:00 - 14:59");
                matchSettleScore6015.setPeriodId(6l);
                matchSettleScore6015.setT1(scores6015.getGoal().getHome());
                matchSettleScore6015.setT2(scores6015.getGoal().getAway());
                list.add(matchSettleScore6015);
            }


            Long period15 = 60899L;
            FootballScores scores15 = footballScoresMap.get(period15.toString());
            if(scores15!=null){
                //15分钟进球
                MatchSettleScore matchSettleScoreGoal15 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal15.setEventCode("goal");
                matchSettleScoreGoal15.setSettleNum("102");
                matchSettleScoreGoal15.setEventName("00:00 - 14:59");
                matchSettleScoreGoal15.setPeriodId(6l);
                matchSettleScoreGoal15.setT1(scores15.getGoal().getHome());
                matchSettleScoreGoal15.setT2(scores15.getGoal().getAway());
                list.add(matchSettleScoreGoal15);
                //当15分钟进球比分0-0时候补全5分钟比分
                if (scores15.getGoal().getHome()==0&&scores15.getGoal().getAway()==0){
                    if (scores6005==null){
                        MatchSettleScore matchSettleScore6005 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6005.setEventCode("goal");
                        matchSettleScore6005.setSettleNum("1034");
                        matchSettleScore6005.setEventName("00:00 - 04:59");
                        matchSettleScore6005.setPeriodId(6l);
                        matchSettleScore6005.setT1(0);
                        matchSettleScore6005.setT2(0);
                        list.add(matchSettleScore6005);
                    }
                    if (scores6010==null){
                        MatchSettleScore matchSettleScore6010 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6010.setEventCode("goal");
                        matchSettleScore6010.setSettleNum("1035");
                        matchSettleScore6010.setEventName("5:00 - 9:59");
                        matchSettleScore6010.setPeriodId(6l);
                        matchSettleScore6010.setT1(0);
                        matchSettleScore6010.setT2(0);
                        list.add(matchSettleScore6010);
                    }
                    if (scores6015==null){
                        MatchSettleScore matchSettleScore6015 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6015.setEventCode("goal");
                        matchSettleScore6015.setSettleNum("1036");
                        matchSettleScore6015.setEventName("10:00 - 14:59");
                        matchSettleScore6015.setPeriodId(6l);
                        matchSettleScore6015.setT1(0);
                        matchSettleScore6015.setT2(0);
                        list.add(matchSettleScore6015);
                    }

                }
                //15分钟角球
                MatchSettleScore matchSettleScoreCorner15 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner15.setEventCode("corner");
                matchSettleScoreCorner15.setSettleNum("2011");
                matchSettleScoreCorner15.setEventName("CR 00:00 - 14:59");
                matchSettleScoreCorner15.setPeriodId(6l);
                matchSettleScoreCorner15.setT1(scores15.getCorner().getHome());
                matchSettleScoreCorner15.setT2(scores15.getCorner().getAway());
                list.add(matchSettleScoreCorner15);
                //15分钟罚牌
                MatchSettleScore matchSettleScoreFaCard15 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard15.setEventCode("fa_card");
                matchSettleScoreFaCard15.setSettleNum("301");
                matchSettleScoreFaCard15.setEventName("BK 00:00 - 14:59");
                matchSettleScoreFaCard15.setPeriodId(6l);
                matchSettleScoreFaCard15.setT1(scores15.getFaCard().getHome());
                matchSettleScoreFaCard15.setT2(scores15.getFaCard().getAway());
                list.add(matchSettleScoreFaCard15);
            }


            /*----------------------------------第2个15分钟----------------------*/
            //查询比分
            Long period6020L = 6020L;
            FootballScores scores6020 = footballScoresMap.get(period6020L.toString());
            if (scores6020!=null){
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore6020 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6020.setEventCode("goal");
                matchSettleScore6020.setSettleNum("1037");
                matchSettleScore6020.setEventName("15:00 - 19:59");
                matchSettleScore6020.setPeriodId(6l);
                matchSettleScore6020.setT1(scores6020.getGoal().getHome());
                matchSettleScore6020.setT2(scores6020.getGoal().getAway());
                list.add(matchSettleScore6020);
            }


            Long period6025 = 6025L;
            FootballScores scores6025 = footballScoresMap.get(period6025.toString());
            if (scores6025!=null){
                MatchSettleScore matchSettleScore6025 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6025.setEventCode("goal");
                matchSettleScore6025.setSettleNum("1038");
                matchSettleScore6025.setEventName("20:00 - 24:59");
                matchSettleScore6025.setPeriodId(6l);
                matchSettleScore6025.setT1(scores6025.getGoal().getHome());
                matchSettleScore6025.setT2(scores6025.getGoal().getAway());
                list.add(matchSettleScore6025);
            }


            Long period30 = 6030L;
            FootballScores scores30 = footballScoresMap.get(period30.toString());
            if (scores30!=null){
                //获取当前阶段的比分 如15分钟的
                MatchSettleScore matchSettleScore6030 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6030.setEventCode("goal");
                matchSettleScore6030.setSettleNum("1039");
                matchSettleScore6030.setEventName("25:00 - 29:59");
                matchSettleScore6030.setPeriodId(6l);
                matchSettleScore6030.setT1(scores30.getGoal().getHome());
                matchSettleScore6030.setT2(scores30.getGoal().getAway());
                list.add(matchSettleScore6030);
            }


            //查询比分
            Long period799 = 61799L;
            FootballScores scores799= footballScoresMap.get(period799.toString());
            if (scores799!=null){
                //获取当前阶段的比分 如15分钟的进球
                MatchSettleScore matchSettleScoreGoal799 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal799.setEventCode("goal");
                matchSettleScoreGoal799.setSettleNum("103");
                matchSettleScoreGoal799.setEventName("15:00 - 29:59");
                matchSettleScoreGoal799.setPeriodId(6l);
                matchSettleScoreGoal799.setT1(scores799.getGoal().getHome());
                matchSettleScoreGoal799.setT2(scores799.getGoal().getAway());
                list.add(matchSettleScoreGoal799);

                if (scores799.getGoal().getHome()==0&&scores799.getGoal().getAway()==0){
                    if (scores6020==null){
                        //获取当前阶段的比分 如15分钟的
                        MatchSettleScore matchSettleScore6020 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6020.setEventCode("goal");
                        matchSettleScore6020.setSettleNum("1037");
                        matchSettleScore6020.setEventName("15:00 - 19:59");
                        matchSettleScore6020.setPeriodId(6l);
                        matchSettleScore6020.setT1(0);
                        matchSettleScore6020.setT2(0);
                        list.add(matchSettleScore6020);
                    }
                    if (scores6025==null){
                        MatchSettleScore matchSettleScore6025 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6025.setEventCode("goal");
                        matchSettleScore6025.setSettleNum("1038");
                        matchSettleScore6025.setEventName("20:00 - 24:59");
                        matchSettleScore6025.setPeriodId(6l);
                        matchSettleScore6025.setT1(0);
                        matchSettleScore6025.setT2(0);
                        list.add(matchSettleScore6025);
                    }
                    if (scores30==null){
                        //获取当前阶段的比分 如15分钟的
                        MatchSettleScore matchSettleScore6030 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6030.setEventCode("goal");
                        matchSettleScore6030.setSettleNum("1039");
                        matchSettleScore6030.setEventName("25:00 - 29:59");
                        matchSettleScore6030.setPeriodId(6l);
                        matchSettleScore6030.setT1(0);
                        matchSettleScore6030.setT2(0);
                        list.add(matchSettleScore6030);
                    }
                }

                //获取当前阶段的比分 如15分钟的角球
                MatchSettleScore matchSettleScoreCorner799 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner799.setEventCode("corner");
                matchSettleScoreCorner799.setSettleNum("2012");
                matchSettleScoreCorner799.setEventName("CR 15:00 - 29:59");
                matchSettleScoreCorner799.setPeriodId(6l);
                matchSettleScoreCorner799.setT1(scores799.getCorner().getHome());
                matchSettleScoreCorner799.setT2(scores799.getCorner().getAway());
                list.add(matchSettleScoreCorner799);
                //获取当前阶段的比分 如15分钟的罚牌
                MatchSettleScore matchSettleScoreFaCard799 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard799.setEventCode("fa_card");
                matchSettleScoreFaCard799.setSettleNum("302");
                matchSettleScoreFaCard799.setEventName("BK 15:00 - 29:59");
                matchSettleScoreFaCard799.setPeriodId(6l);
                matchSettleScoreFaCard799.setT1(scores799.getFaCard().getHome());
                matchSettleScoreFaCard799.setT2(scores799.getFaCard().getAway());
                list.add(matchSettleScoreFaCard799);
            }


            /*------------------第3个15分钟----------------*/

            //查询比分
            Long period6035 = 6035L;
            FootballScores scores6035 = footballScoresMap.get(period6035.toString());
            if (scores6035!=null){
                MatchSettleScore matchSettleScore6035 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6035.setEventCode("goal");
                matchSettleScore6035.setSettleNum("1040");
                matchSettleScore6035.setEventName("30:00 - 34:59");
                matchSettleScore6035.setPeriodId(6l);
                matchSettleScore6035.setT1(scores6035.getGoal().getHome());
                matchSettleScore6035.setT2(scores6035.getGoal().getAway());
                list.add(matchSettleScore6035);
            }



            //查询比分
            Long period6040 = 6040L;
            FootballScores scores6040 = footballScoresMap.get(period6040.toString());
            if (scores6040!=null){
                MatchSettleScore matchSettleScore6040 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6040.setEventCode("goal");
                matchSettleScore6040.setSettleNum("1041");
                matchSettleScore6040.setEventName("35:00 - 39:59");
                matchSettleScore6040.setPeriodId(6l);
                matchSettleScore6040.setT1(scores6040.getGoal().getHome());
                matchSettleScore6040.setT2(scores6040.getGoal().getAway());
                list.add(matchSettleScore6040);
            }



            //查询比分
            Long period6045 = 6045L;
            FootballScores scores6045 = footballScoresMap.get(period6045.toString());
            if (scores6045!=null){
                MatchSettleScore matchSettleScore6045 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6045.setEventCode("goal");
                matchSettleScore6045.setSettleNum("1042");
                matchSettleScore6045.setEventName("40:00 - 45:00");
                matchSettleScore6045.setPeriodId(6l);
                matchSettleScore6045.setT1(scores6045.getGoal().getHome());
                matchSettleScore6045.setT2(scores6045.getGoal().getAway());
                list.add(matchSettleScore6045);
            }


            Long period62699 = 62699L;
            FootballScores scores62699 = footballScoresMap.get(period62699.toString());
            if (scores62699!=null){
                MatchSettleScore matchSettleScoreGoal62699  = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal62699.setEventCode("goal");
                matchSettleScoreGoal62699.setSettleNum("104");
                matchSettleScoreGoal62699.setEventName("30:00 - 1HT");
                matchSettleScoreGoal62699.setPeriodId(6l);
                matchSettleScoreGoal62699.setT1(scores62699.getGoal().getHome());
                matchSettleScoreGoal62699.setT2(scores62699.getGoal().getAway());
                list.add(matchSettleScoreGoal62699);
                if (scores62699.getGoal().getHome()==0&&scores62699.getGoal().getAway()==0){
                    if (scores6035==null){
                        MatchSettleScore matchSettleScore6035 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6035.setEventCode("goal");
                        matchSettleScore6035.setSettleNum("1040");
                        matchSettleScore6035.setEventName("30:00 - 34:59");
                        matchSettleScore6035.setPeriodId(6l);
                        matchSettleScore6035.setT1(0);
                        matchSettleScore6035.setT2(0);
                        list.add(matchSettleScore6035);
                    }
                    if (scores6040==null){
                        MatchSettleScore matchSettleScore6040 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6040.setEventCode("goal");
                        matchSettleScore6040.setSettleNum("1041");
                        matchSettleScore6040.setEventName("35:00 - 39:59");
                        matchSettleScore6040.setPeriodId(6l);
                        matchSettleScore6040.setT1(0);
                        matchSettleScore6040.setT2(0);
                        list.add(matchSettleScore6040);
                    }
                    if (scores6045==null){
                        MatchSettleScore matchSettleScore6045 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore6045.setEventCode("goal");
                        matchSettleScore6045.setSettleNum("1042");
                        matchSettleScore6045.setEventName("40:00 - 45:00");
                        matchSettleScore6045.setPeriodId(6l);
                        matchSettleScore6045.setT1(0);
                        matchSettleScore6045.setT2(0);
                        list.add(matchSettleScore6045);
                    }
                }

                MatchSettleScore matchSettleScoreCorner62699 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner62699.setEventCode("corner");
                matchSettleScoreCorner62699.setSettleNum("2013");
                matchSettleScoreCorner62699.setEventName("CR 30:00 - HT");
                matchSettleScoreCorner62699.setPeriodId(6l);
                matchSettleScoreCorner62699.setT1(scores62699.getCorner().getHome());
                matchSettleScoreCorner62699.setT2(scores62699.getCorner().getAway());
                list.add(matchSettleScoreCorner62699);

                MatchSettleScore matchSettleScoreFaCard62699 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard62699.setEventCode("fa_card");
                matchSettleScoreFaCard62699.setSettleNum("303");
                matchSettleScoreFaCard62699.setEventName("BK 30:00 - HT");
                matchSettleScoreFaCard62699.setPeriodId(6l);
                matchSettleScoreFaCard62699.setT1(scores62699.getFaCard().getHome());
                matchSettleScoreFaCard62699.setT2(scores62699.getFaCard().getAway());
                list.add(matchSettleScoreFaCard62699);
            }


            /*--------------------上半场阶段------------------*/
            //上半场绝杀球
            Long period6050 = 6050L;
            FootballScores scores6050 = footballScoresMap.get(period6050.toString());
            if (scores6050!=null){
                MatchSettleScore matchSettleScore6050 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore6050.setEventCode("goal");
                matchSettleScore6050.setSettleNum("1043");
                matchSettleScore6050.setEventName("1H Last-minute Goal (Injury Time)");
                matchSettleScore6050.setPeriodId(6l);
                matchSettleScore6050.setT1(scores6050.getGoal().getHome());
                matchSettleScore6050.setT2(scores6050.getGoal().getAway());
                list.add(matchSettleScore6050);
            }


            Long period6 = 6l;
            FootballScores scores1H = footballScoresMap.get(period6.toString());
            if (scores1H!=null){
                MatchSettleScore matchSettleScoreGoal6 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal6.setEventCode("goal");
                matchSettleScoreGoal6.setSettleNum("105");
                matchSettleScoreGoal6.setEventName("1HT");
                matchSettleScoreGoal6.setPeriodId(31l);
                matchSettleScoreGoal6.setT1(scores1H.getGoal().getHome());
                matchSettleScoreGoal6.setT2(scores1H.getGoal().getAway());
                list.add(matchSettleScoreGoal6);

                MatchSettleScore matchSettleScoreCorner6 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner6.setEventCode("corner");
                matchSettleScoreCorner6.setSettleNum("201");
                matchSettleScoreCorner6.setEventName("1HT CR");
                matchSettleScoreCorner6.setPeriodId(31L);
                matchSettleScoreCorner6.setT1(scores1H.getCorner().getHome());
                matchSettleScoreCorner6.setT2(scores1H.getCorner().getAway());
                list.add(matchSettleScoreCorner6);
                //上半场罚牌比分
                MatchSettleScore matchSettleScoreFardCard6 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFardCard6.setEventCode("fa_card");
                matchSettleScoreFardCard6.setSettleNum("304");
                matchSettleScoreFardCard6.setEventName("BK 1HT");
                matchSettleScoreFardCard6.setPeriodId(31l);
                matchSettleScoreFardCard6.setT1(scores1H.getFaCard().getHome());
                matchSettleScoreFardCard6.setT2(scores1H.getFaCard().getAway());
                list.add(matchSettleScoreFardCard6);

                //上半场红牌比分
                MatchSettleScore matchSettleScoreRedCard6 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreRedCard6.setEventCode("red_card");
                matchSettleScoreRedCard6.setSettleNum("3041");
                matchSettleScoreRedCard6.setEventName("1st Half Bookings -red card");
                matchSettleScoreRedCard6.setPeriodId(31l);
                matchSettleScoreRedCard6.setT1(scores1H.getRedCard().getHome());
                matchSettleScoreRedCard6.setT2(scores1H.getRedCard().getAway());
                list.add(matchSettleScoreRedCard6);
            }


            /*--------------------第4个15分钟--------------------*/

            Long period2999 = 7050L;
            FootballScores scores7050 = footballScoresMap.get(period2999.toString());
            if (scores7050!=null){
                MatchSettleScore matchSettleScore7050 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7050.setEventCode("goal");
                matchSettleScore7050.setSettleNum("1044");
                matchSettleScore7050.setEventName("45:00 - 49:59");
                matchSettleScore7050.setPeriodId(7l);
                matchSettleScore7050.setT1(scores7050.getGoal().getHome());
                matchSettleScore7050.setT2(scores7050.getGoal().getAway());
                list.add(matchSettleScore7050);
            }


            Long period7055 = 7055L;
            FootballScores scores7055 = footballScoresMap.get(period7055.toString());
            if (scores7055!=null){
                MatchSettleScore matchSettleScore7055 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7055.setEventCode("goal");
                matchSettleScore7055.setSettleNum("1045");
                matchSettleScore7055.setEventName("50:00 - 54:59");
                matchSettleScore7055.setPeriodId(7l);
                matchSettleScore7055.setT1(scores7055.getGoal().getHome());
                matchSettleScore7055.setT2(scores7055.getGoal().getAway());
                list.add(matchSettleScore7055);
            }


            Long period7060= 7060L;
            FootballScores scores7060 = footballScoresMap.get(period7060.toString());
            if (scores7060!=null){
                MatchSettleScore matchSettleScore7060 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7060.setEventCode("goal");
                matchSettleScore7060.setSettleNum("1046");
                matchSettleScore7060.setEventName("55:00 - 59:59");
                matchSettleScore7060.setPeriodId(7l);
                matchSettleScore7060.setT1(scores7060.getGoal().getHome());
                matchSettleScore7060.setT2(scores7060.getGoal().getAway());
                list.add(matchSettleScore7060);
            }


            //查询比分
            Long period599 = 73599L;
            FootballScores scores599 = footballScoresMap.get(period599.toString());
            if (scores599!=null){
                MatchSettleScore matchSettleScoreGoal599 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal599.setEventCode("goal");
                matchSettleScoreGoal599.setSettleNum("106");
                matchSettleScoreGoal599.setEventName("1HT - 59:59");
                matchSettleScoreGoal599.setPeriodId(7l);
                matchSettleScoreGoal599.setT1(scores599.getGoal().getHome());
                matchSettleScoreGoal599.setT2(scores599.getGoal().getAway());
                list.add(matchSettleScoreGoal599);
                if (scores599.getGoal().getHome()==0&&scores599.getGoal().getAway()==0){
                    if (scores7050==null){
                        MatchSettleScore matchSettleScore7050 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7050.setEventCode("goal");
                        matchSettleScore7050.setSettleNum("1044");
                        matchSettleScore7050.setEventName("45:00 - 49:59");
                        matchSettleScore7050.setPeriodId(7l);
                        matchSettleScore7050.setT1(0);
                        matchSettleScore7050.setT2(0);
                        list.add(matchSettleScore7050);
                    }
                    if (scores7055==null){
                        MatchSettleScore matchSettleScore7055 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7055.setEventCode("goal");
                        matchSettleScore7055.setSettleNum("1045");
                        matchSettleScore7055.setEventName("50:00 - 54:59");
                        matchSettleScore7055.setPeriodId(7l);
                        matchSettleScore7055.setT1(0);
                        matchSettleScore7055.setT2(0);
                        list.add(matchSettleScore7055);
                    }
                    if (scores7060==null){
                        MatchSettleScore matchSettleScore7060 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7060.setEventCode("goal");
                        matchSettleScore7060.setSettleNum("1046");
                        matchSettleScore7060.setEventName("55:00 - 59:59");
                        matchSettleScore7060.setPeriodId(7l);
                        matchSettleScore7060.setT1(0);
                        matchSettleScore7060.setT2(0);
                        list.add(matchSettleScore7060);
                    }
                }

                MatchSettleScore matchSettleScoreCorner599 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner599.setEventCode("corner");
                matchSettleScoreCorner599.setSettleNum("2014");
                matchSettleScoreCorner599.setEventName("CR HT - 59:59");
                matchSettleScoreCorner599.setPeriodId(7l);
                matchSettleScoreCorner599.setT1(scores599.getCorner().getHome());
                matchSettleScoreCorner599.setT2(scores599.getCorner().getAway());
                list.add(matchSettleScoreCorner599);

                MatchSettleScore matchSettleScoreFaCard599 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard599.setEventCode("fa_card");
                matchSettleScoreFaCard599.setSettleNum("305");
                matchSettleScoreFaCard599.setEventName("BK HT - 59:59");
                matchSettleScoreFaCard599.setPeriodId(7l);
                matchSettleScoreFaCard599.setT1(scores599.getFaCard().getHome());
                matchSettleScoreFaCard599.setT2(scores599.getFaCard().getAway());
                list.add(matchSettleScoreFaCard599);
            }


            /*-------------------第5个15分钟----------------------*/
            Long period7065 = 7065L;
            FootballScores scores7065 = footballScoresMap.get(period7065.toString());
            if (scores7065!=null){
                MatchSettleScore matchSettleScore7065 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7065.setEventCode("goal");
                matchSettleScore7065.setSettleNum("1047");
                matchSettleScore7065.setEventName("60:00 - 64:59");
                matchSettleScore7065.setPeriodId(7l);
                matchSettleScore7065.setT1(scores7065.getGoal().getHome());
                matchSettleScore7065.setT2(scores7065.getGoal().getAway());
                list.add(matchSettleScore7065);
            }


            Long period7070 = 7070L;
            FootballScores scores7070 = footballScoresMap.get(period7070.toString());
            if (scores7070!=null){
                MatchSettleScore matchSettleScore7070 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7070.setEventCode("goal");
                matchSettleScore7070.setSettleNum("1048");
                matchSettleScore7070.setEventName("65:00 - 69:59");
                matchSettleScore7070.setPeriodId(7l);
                matchSettleScore7070.setT1(scores7070.getGoal().getHome());
                matchSettleScore7070.setT2(scores7070.getGoal().getAway());
                list.add(matchSettleScore7070);
            }


            Long period7075= 7075L;
            FootballScores scores7075 = footballScoresMap.get(period7075.toString());
            if (scores7075!=null){
                MatchSettleScore matchSettleScore7075 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7075.setEventCode("goal");
                matchSettleScore7075.setSettleNum("1049");
                matchSettleScore7075.setEventName("70:00 - 74:59");
                matchSettleScore7075.setPeriodId(7l);
                matchSettleScore7075.setT1(scores7075.getGoal().getHome());
                matchSettleScore7075.setT2(scores7075.getGoal().getAway());
                list.add(matchSettleScore7075);
            }


            //查询比分
            Long period74499 = 74499L;
            FootballScores scores4499 = footballScoresMap.get(period74499.toString());
            if (scores4499!=null){
                MatchSettleScore matchSettleScoreGoal4499 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal4499.setEventCode("goal");
                matchSettleScoreGoal4499.setSettleNum("107");
                matchSettleScoreGoal4499.setEventName("60:00 - 74:59");
                matchSettleScoreGoal4499.setPeriodId(7l);
                matchSettleScoreGoal4499.setT1(scores4499.getGoal().getHome());
                matchSettleScoreGoal4499.setT2(scores4499.getGoal().getAway());
                list.add(matchSettleScoreGoal4499);

                if (scores4499.getGoal().getHome()==0&&scores4499.getGoal().getAway()==0){
                    if (scores7065==null){
                        MatchSettleScore matchSettleScore7065 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7065.setEventCode("goal");
                        matchSettleScore7065.setSettleNum("1047");
                        matchSettleScore7065.setEventName("60:00 - 64:59");
                        matchSettleScore7065.setPeriodId(7l);
                        matchSettleScore7065.setT1(0);
                        matchSettleScore7065.setT2(0);
                        list.add(matchSettleScore7065);
                    }
                    if (scores7070==null){
                        MatchSettleScore matchSettleScore7070 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7070.setEventCode("goal");
                        matchSettleScore7070.setSettleNum("1048");
                        matchSettleScore7070.setEventName("65:00 - 69:59");
                        matchSettleScore7070.setPeriodId(7l);
                        matchSettleScore7070.setT1(0);
                        matchSettleScore7070.setT2(0);
                        list.add(matchSettleScore7070);
                    }
                    if (scores7075==null){
                        MatchSettleScore matchSettleScore7075 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7075.setEventCode("goal");
                        matchSettleScore7075.setSettleNum("1049");
                        matchSettleScore7075.setEventName("70:00 - 74:59");
                        matchSettleScore7075.setPeriodId(7l);
                        matchSettleScore7075.setT1(0);
                        matchSettleScore7075.setT2(0);
                        list.add(matchSettleScore7075);
                    }
                }
                MatchSettleScore matchSettleScoreCorner4499 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner4499.setEventCode("corner");
                matchSettleScoreCorner4499.setSettleNum("2015");
                matchSettleScoreCorner4499.setEventName("CR 60:00 - 74:59");
                matchSettleScoreCorner4499.setPeriodId(7l);
                matchSettleScoreCorner4499.setT1(scores4499.getCorner().getHome());
                matchSettleScoreCorner4499.setT2(scores4499.getCorner().getAway());
                list.add(matchSettleScoreCorner4499);

                MatchSettleScore matchSettleScoreFaCard4499 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard4499.setEventCode("fa_card");
                matchSettleScoreFaCard4499.setSettleNum("306");
                matchSettleScoreFaCard4499.setEventName("BK 60:00 - 74:59");
                matchSettleScoreFaCard4499.setPeriodId(7l);
                matchSettleScoreFaCard4499.setT1(scores4499.getFaCard().getHome());
                matchSettleScoreFaCard4499.setT2(scores4499.getFaCard().getAway());
                list.add(matchSettleScoreFaCard4499);
            }


            /*-------------------第6个15分钟------------------*/
            //查询比分
            Long period7080 = 7080L;
            FootballScores scores7080 = footballScoresMap.get(period7080.toString());
            if (scores7080!=null){
                MatchSettleScore matchSettleScore7080 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7080.setEventCode("goal");
                matchSettleScore7080.setSettleNum("1050");
                matchSettleScore7080.setEventName("75:00 - 79:59");
                matchSettleScore7080.setPeriodId(7l);
                matchSettleScore7080.setT1(scores7080.getGoal().getHome());
                matchSettleScore7080.setT2(scores7080.getGoal().getAway());
                list.add(matchSettleScore7080);
            }


            Long period7085 = 7085L;
            FootballScores scores7085 = footballScoresMap.get(period7085.toString());
            if (scores7085!=null){
                MatchSettleScore matchSettleScore7085 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7085.setEventCode("goal");
                matchSettleScore7085.setSettleNum("1051");
                matchSettleScore7085.setEventName("80:00 - 84:59");
                matchSettleScore7085.setPeriodId(7l);
                matchSettleScore7085.setT1(scores7085.getGoal().getHome());
                matchSettleScore7085.setT2(scores7085.getGoal().getAway());
                list.add(matchSettleScore7085);
            }


            Long period7090= 7090L;
            FootballScores scores7090 = footballScoresMap.get(period7090.toString());
            if (scores7090!=null){
                MatchSettleScore matchSettleScore7090 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7090.setEventCode("goal");
                matchSettleScore7090.setSettleNum("1052");
                matchSettleScore7090.setEventName("85:00 - 90:00");
                matchSettleScore7090.setPeriodId(7l);
                matchSettleScore7090.setT1(scores7090.getGoal().getHome());
                matchSettleScore7090.setT2(scores7090.getGoal().getAway());
                list.add(matchSettleScore7090);
            }


            //查询比分
            Long period7095 = 7095L;
            FootballScores scores7095 = footballScoresMap.get(period7095.toString());
            if (scores7095!=null){
                MatchSettleScore matchSettleScore7095 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore7095.setEventCode("goal");
                matchSettleScore7095.setSettleNum("1053");
                matchSettleScore7095.setEventName("2H Last-minute Goal (Injury Time)");
                matchSettleScore7095.setPeriodId(7l);
                matchSettleScore7095.setT1(scores7095.getGoal().getHome());
                matchSettleScore7095.setT2(scores7095.getGoal().getAway());
                list.add(matchSettleScore7095);
            }


            //查询比分
            Long period75399 = 75399L;
            FootballScores scores90 = footballScoresMap.get(period75399.toString());
            if (scores90!=null){
                MatchSettleScore matchSettleScoreGoal5399 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal5399.setEventCode("goal");
                matchSettleScoreGoal5399.setSettleNum("108");
                matchSettleScoreGoal5399.setEventName("75:00 - FT");
                matchSettleScoreGoal5399.setPeriodId(7l);
                matchSettleScoreGoal5399.setT1(scores90.getGoal().getHome());
                matchSettleScoreGoal5399.setT2(scores90.getGoal().getAway());
                list.add(matchSettleScoreGoal5399);
                if (scores90.getGoal().getHome()==0&&scores90.getGoal().getAway()==0){
                    if (scores7080==null){
                        MatchSettleScore matchSettleScore7080 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7080.setEventCode("goal");
                        matchSettleScore7080.setSettleNum("1050");
                        matchSettleScore7080.setEventName("75:00 - 79:59");
                        matchSettleScore7080.setPeriodId(7l);
                        matchSettleScore7080.setT1(0);
                        matchSettleScore7080.setT2(0);
                        list.add(matchSettleScore7080);
                    }
                    if (scores7085==null){
                        MatchSettleScore matchSettleScore7085 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7085.setEventCode("goal");
                        matchSettleScore7085.setSettleNum("1051");
                        matchSettleScore7085.setEventName("80:00 - 84:59");
                        matchSettleScore7085.setPeriodId(7l);
                        matchSettleScore7085.setT1(0);
                        matchSettleScore7085.setT2(0);
                        list.add(matchSettleScore7085);
                    }
                    if (scores7090==null){
                        MatchSettleScore matchSettleScore7090 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                        matchSettleScore7090.setEventCode("goal");
                        matchSettleScore7090.setSettleNum("1052");
                        matchSettleScore7090.setEventName("85:00 - 90:00");
                        matchSettleScore7090.setPeriodId(7l);
                        matchSettleScore7090.setT1(0);
                        matchSettleScore7090.setT2(0);
                        list.add(matchSettleScore7090);
                    }
                }

                MatchSettleScore matchSettleScoreCorner5399 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner5399.setEventCode("corner");
                matchSettleScoreCorner5399.setSettleNum("2016");
                matchSettleScoreCorner5399.setEventName("CR 75:00 - FT");
                matchSettleScoreCorner5399.setPeriodId(7l);
                matchSettleScoreCorner5399.setT1(scores90.getCorner().getHome());
                matchSettleScoreCorner5399.setT2(scores90.getCorner().getAway());
                list.add(matchSettleScoreCorner5399);

                MatchSettleScore matchSettleScoreFaCard5399 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard5399.setEventCode("fa_card");
                matchSettleScoreFaCard5399.setSettleNum("307");
                matchSettleScoreFaCard5399.setEventName("BK 75:00 - FT");
                matchSettleScoreFaCard5399.setPeriodId(7l);
                matchSettleScoreFaCard5399.setT1(scores90.getFaCard().getHome());
                matchSettleScoreFaCard5399.setT2(scores90.getFaCard().getAway());
                list.add(matchSettleScoreFaCard5399);
            }


            /*------------------下半场,全场常规----------------------*/
            Long period7 = 7l;
            FootballScores scores2H = footballScoresMap.get(period7.toString());
            if (scores2H!=null){
                //半场进球比分
                MatchSettleScore matchSettleScoreGoal7 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal7.setEventCode("goal");
                matchSettleScoreGoal7.setSettleNum("109");
                matchSettleScoreGoal7.setEventName("2HT");
                matchSettleScoreGoal7.setPeriodId(8l);
                matchSettleScoreGoal7.setT1(scores2H.getGoal().getHome());
                matchSettleScoreGoal7.setT2(scores2H.getGoal().getAway());
                list.add(matchSettleScoreGoal7);
                //半场角球比分
                MatchSettleScore matchSettleScoreCorner7 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner7.setEventCode("corner");
                matchSettleScoreCorner7.setSettleNum("202");
                matchSettleScoreCorner7.setEventName("2HT CR");
                matchSettleScoreCorner7.setPeriodId(8L);
                matchSettleScoreCorner7.setT1(scores2H.getCorner().getHome());
                matchSettleScoreCorner7.setT2(scores2H.getCorner().getAway());
                list.add(matchSettleScoreCorner7);
                //半场罚牌比分
                MatchSettleScore matchSettleScoreFaCard7 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard7.setEventCode("fa_card");
                matchSettleScoreFaCard7.setSettleNum("308");
                matchSettleScoreFaCard7.setEventName("BK 2HT");
                matchSettleScoreFaCard7.setPeriodId(8l);
                matchSettleScoreFaCard7.setT1(scores2H.getFaCard().getHome());
                matchSettleScoreFaCard7.setT2(scores2H.getFaCard().getAway());
                list.add(matchSettleScoreFaCard7);

                MatchSettleScore matchSettleScoreGoalAll = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoalAll.setEventCode("goal");
                matchSettleScoreGoalAll.setSettleNum("1010");
                matchSettleScoreGoalAll.setEventName("FT");
                matchSettleScoreGoalAll.setPeriodId(100l);
                matchSettleScoreGoalAll.setT1(scores2H.getGoal().getHome()+scores1H.getGoal().getHome());
                matchSettleScoreGoalAll.setT2(scores2H.getGoal().getAway()+scores1H.getGoal().getAway());
                list.add(matchSettleScoreGoalAll);

                MatchSettleScore matchSettleScoreCornerAll = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCornerAll.setEventCode("corner");
                matchSettleScoreCornerAll.setSettleNum("203");
                matchSettleScoreCornerAll.setEventName("FT CR");
                matchSettleScoreCornerAll.setPeriodId(100L);
                matchSettleScoreCornerAll.setT1(scores2H.getCorner().getHome()+scores1H.getCorner().getHome());
                matchSettleScoreCornerAll.setT2(scores2H.getCorner().getAway()+scores1H.getCorner().getAway());
                list.add(matchSettleScoreCornerAll);
                //半场罚牌比分
                MatchSettleScore matchSettleScoreFaCardAll = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCardAll.setEventCode("fa_card");
                matchSettleScoreFaCardAll.setSettleNum("309");
                matchSettleScoreFaCardAll.setEventName("BK FT");
                matchSettleScoreFaCardAll.setPeriodId(100l);
                matchSettleScoreFaCardAll.setT1(scores2H.getFaCard().getHome()+scores1H.getFaCard().getHome());
                matchSettleScoreFaCardAll.setT2(scores2H.getFaCard().getAway()+scores1H.getFaCard().getAway());
                list.add(matchSettleScoreFaCardAll);
            }




            /*---------------------加时赛-------------*/
            Long period41 = 41l;
            FootballScores scores41 = footballScoresMap.get(period41.toString());
            if (scores41!=null){
                MatchSettleScore matchSettleScoreGoal41 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal41.setEventCode("goal");
                matchSettleScoreGoal41.setSettleNum("1014");
                matchSettleScoreGoal41.setEventName("1ET");
                matchSettleScoreGoal41.setPeriodId(33l);
                matchSettleScoreGoal41.setT1(scores41.getGoal().getHome());
                matchSettleScoreGoal41.setT2(scores41.getGoal().getAway());
                list.add(matchSettleScoreGoal41);

                MatchSettleScore matchSettleScoreCorner41 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner41.setEventCode("corner");
                matchSettleScoreCorner41.setSettleNum("206");
                matchSettleScoreCorner41.setEventName("1ET CR");
                matchSettleScoreCorner41.setPeriodId(33L);
                matchSettleScoreCorner41.setT1(scores41.getCorner().getHome());
                matchSettleScoreCorner41.setT2(scores41.getCorner().getAway());
                list.add(matchSettleScoreCorner41);

                MatchSettleScore matchSettleScoreFaCard41 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard41.setEventCode("fa_card");
                matchSettleScoreFaCard41.setSettleNum("3013");
                matchSettleScoreFaCard41.setEventName("1ET BK");
                matchSettleScoreFaCard41.setPeriodId(33L);
                matchSettleScoreFaCard41.setT1(scores41.getFaCard().getHome());
                matchSettleScoreFaCard41.setT2(scores41.getFaCard().getAway());
                list.add(matchSettleScoreFaCard41);
            }


            Long period42 = 42l;
            FootballScores scores42 = footballScoresMap.get(period42.toString());
            if (scores42!=null){
                //半场进球比分
                MatchSettleScore matchSettleScoreGoal42 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreGoal42.setEventCode("goal");
                matchSettleScoreGoal42.setSettleNum("1018");
                matchSettleScoreGoal42.setEventName("2ET");
                matchSettleScoreGoal42.setPeriodId(43l);
                matchSettleScoreGoal42.setT1(scores42.getGoal().getHome());
                matchSettleScoreGoal42.setT2(scores42.getGoal().getAway());
                list.add(matchSettleScoreGoal42);
                //半场角球比分
                MatchSettleScore matchSettleScoreCorner42 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreCorner42.setEventCode("corner");
                matchSettleScoreCorner42.setSettleNum("207");
                matchSettleScoreCorner42.setEventName("2ET CR");
                matchSettleScoreCorner42.setPeriodId(43L);
                matchSettleScoreCorner42.setT1(scores42.getCorner().getHome());
                matchSettleScoreCorner42.setT2(scores42.getCorner().getAway());
                list.add(matchSettleScoreCorner42);
                //半场罚牌比分
                MatchSettleScore matchSettleScoreFaCard42 = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreFaCard42.setEventCode("fa_card");
                matchSettleScoreFaCard42.setSettleNum("3017");
                matchSettleScoreFaCard42.setEventName("2ET BK");
                matchSettleScoreFaCard42.setPeriodId(43l);
                matchSettleScoreFaCard42.setT1(scores42.getFaCard().getHome());
                matchSettleScoreFaCard42.setT2(scores42.getFaCard().getAway());
                list.add(matchSettleScoreFaCard42);
            }

            if (scores42!=null&&scores41!=null){
                //加时赛全场生成规则
                //加时赛全场进球比分
                MatchSettleScore matchSettleScoreAll = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScoreAll.setEventCode("goal");
                matchSettleScoreAll.setSettleNum("1019");
                matchSettleScoreAll.setEventName("ET");
                matchSettleScoreAll.setPeriodId(110l);
                matchSettleScoreAll.setT1(scores42.getGoal().getHome()+scores41.getGoal().getHome());
                matchSettleScoreAll.setT2(scores42.getGoal().getAway()+scores41.getGoal().getAway());
                list.add(matchSettleScoreAll);
                //加时赛全场角球比分
                MatchSettleScore matchSettleScore2All = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore2All.setEventCode("corner");
                matchSettleScore2All.setSettleNum("208");
                matchSettleScore2All.setEventName("ET CR");
                matchSettleScore2All.setPeriodId(110L);
                matchSettleScore2All.setT1(scores42.getCorner().getHome()+scores41.getCorner().getHome());
                matchSettleScore2All.setT2(scores42.getCorner().getAway()+scores41.getCorner().getAway());
                list.add(matchSettleScore2All);
                //加时赛全场罚牌比分
                MatchSettleScore matchSettleScore3All = FootBallMatchSettleScoreUtils.initMatchSettleScore(standardScoresDto.getStandardMatchId());
                matchSettleScore3All.setEventCode("fa_card");
                matchSettleScore3All.setSettleNum("3018");
                matchSettleScore3All.setEventName("ET BK");
                matchSettleScore3All.setPeriodId(110l);
                matchSettleScore3All.setT1(scores42.getFaCard().getHome()+scores41.getFaCard().getHome());
                matchSettleScore3All.setT2(scores42.getFaCard().getAway()+scores41.getFaCard().getAway());
                list.add(matchSettleScore3All);
            }

        }catch (Exception e){
            log.error(standardScoresDto.getLinkedId()+":Match15MScoresSettleInitFilter error:",e);
        }
        return list;
    }

    @Override
    public List<String> deleteEventPeriodScorefilter(MatchEventInfo data, List<String> list) {
        return list;
    }
}
