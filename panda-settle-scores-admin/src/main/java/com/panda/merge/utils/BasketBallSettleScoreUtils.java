package com.panda.merge.utils;

import com.panda.merge.model.MatchSettleAbnormal;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class BasketBallSettleScoreUtils {
    public static final List<String> IN_SETTLE_NUM_LIST = new ArrayList<>();
    static {
        IN_SETTLE_NUM_LIST.add("bk_in_q01");
        IN_SETTLE_NUM_LIST.add("bk_in_q02");
        IN_SETTLE_NUM_LIST.add("bk_in_q03");
        IN_SETTLE_NUM_LIST.add("bk_in_q04");
        IN_SETTLE_NUM_LIST.add("bk_in_1ht");
        IN_SETTLE_NUM_LIST.add("bk_in_2ht");
        IN_SETTLE_NUM_LIST.add("bk_in_2htet");
        IN_SETTLE_NUM_LIST.add("bk_in_rg");
        IN_SETTLE_NUM_LIST.add("bk_in_et");
    }

    public static List<MatchSettleScore> createInitMatchSettleScores(Long standardMatchId){
        List<MatchSettleScore> list=new ArrayList<>();
        initPeriodScore(list,standardMatchId);
        return  list;
    }

    private static void initPeriodScore(List<MatchSettleScore> list, Long standardMatchId) {
        //1节比分  bk_q101 ~ bk_q104
        MatchSettleScore matchSettleScore1 =initMatchSettleScore(standardMatchId);
        matchSettleScore1.setEventCode("score_change");
        matchSettleScore1.setSettleNum("bk_q101");
        matchSettleScore1.setEventName("第1节首先获得10分");
        matchSettleScore1.setExtryInfo("10");
        matchSettleScore1.setPeriodId(13l);
        list.add(matchSettleScore1);
        MatchSettleScore matchSettleScore2 =initMatchSettleScore(standardMatchId);
        matchSettleScore2.setEventCode("score_change");
        matchSettleScore2.setSettleNum("bk_q102");
        matchSettleScore2.setEventName("第1节首先获得20分");
        matchSettleScore2.setExtryInfo("20");
        matchSettleScore2.setPeriodId(13l);
        list.add(matchSettleScore2);
        MatchSettleScore matchSettleScore3 =initMatchSettleScore(standardMatchId);
        matchSettleScore3.setEventCode("score_change");
        matchSettleScore3.setSettleNum("bk_q103");
        matchSettleScore3.setExtryInfo("30");
        matchSettleScore3.setEventName("第1节首先获得30分");
        matchSettleScore3.setPeriodId(13l);
        list.add(matchSettleScore3);
        MatchSettleScore matchSettleScore4 =initMatchSettleScore(standardMatchId);
        matchSettleScore4.setEventCode("score_change");
        matchSettleScore4.setSettleNum("bk_q104");
        matchSettleScore4.setEventName("第1节比分");
        matchSettleScore4.setPeriodId(301l);
        list.add(matchSettleScore4);
        //2节比分  bk_q201 ~ bk_q204 bk_1ht
        MatchSettleScore matchSettleScore5 =initMatchSettleScore(standardMatchId);
        matchSettleScore5.setEventCode("score_change");
        matchSettleScore5.setSettleNum("bk_q201");
        matchSettleScore5.setEventName("第2节首先获得10分");
        matchSettleScore5.setExtryInfo("10");
        matchSettleScore5.setPeriodId(14l);
        list.add(matchSettleScore5);
        MatchSettleScore matchSettleScore6 =initMatchSettleScore(standardMatchId);
        matchSettleScore6.setEventCode("score_change");
        matchSettleScore6.setSettleNum("bk_q202");
        matchSettleScore6.setEventName("第2节首先获得20分");
        matchSettleScore6.setExtryInfo("20");
        matchSettleScore6.setPeriodId(14l);
        list.add(matchSettleScore6);
        MatchSettleScore matchSettleScore7 =initMatchSettleScore(standardMatchId);
        matchSettleScore7.setEventCode("score_change");
        matchSettleScore7.setSettleNum("bk_q203");
        matchSettleScore7.setEventName("第2节首先获得30分");
        matchSettleScore7.setExtryInfo("30");
        matchSettleScore7.setPeriodId(14l);
        list.add(matchSettleScore7);
        MatchSettleScore matchSettleScore8 =initMatchSettleScore(standardMatchId);
        matchSettleScore8.setEventCode("score_change");
        matchSettleScore8.setSettleNum("bk_q204");
        matchSettleScore8.setEventName("第2节比分");
        matchSettleScore8.setPeriodId(302l);
        list.add(matchSettleScore8);
        MatchSettleScore matchSettleScore9 =initMatchSettleScore(standardMatchId);
        matchSettleScore9.setEventCode("score_change");
        matchSettleScore9.setSettleNum("bk_1ht");
        matchSettleScore9.setEventName("上半场比分");
        matchSettleScore9.setPeriodId(31l);
        list.add(matchSettleScore9);
        //3节比分  bk_q301 ~ bk_q304
        MatchSettleScore matchSettleScore10 =initMatchSettleScore(standardMatchId);
        matchSettleScore10.setEventCode("score_change");
        matchSettleScore10.setSettleNum("bk_q301");
        matchSettleScore10.setEventName("第3节首先获得10分");
        matchSettleScore10.setExtryInfo("10");
        matchSettleScore10.setPeriodId(15l);
        list.add(matchSettleScore10);
        MatchSettleScore matchSettleScore11 =initMatchSettleScore(standardMatchId);
        matchSettleScore11.setEventCode("score_change");
        matchSettleScore11.setSettleNum("bk_q302");
        matchSettleScore11.setEventName("第3节首先获得20分");
        matchSettleScore11.setExtryInfo("20");
        matchSettleScore11.setPeriodId(15l);
        list.add(matchSettleScore11);
        MatchSettleScore matchSettleScore12 =initMatchSettleScore(standardMatchId);
        matchSettleScore12.setEventCode("score_change");
        matchSettleScore12.setSettleNum("bk_q303");
        matchSettleScore12.setEventName("第3节首先获得30分");
        matchSettleScore12.setExtryInfo("30");
        matchSettleScore12.setPeriodId(15l);
        list.add(matchSettleScore12);
        MatchSettleScore matchSettleScore13 =initMatchSettleScore(standardMatchId);
        matchSettleScore13.setEventCode("score_change");
        matchSettleScore13.setSettleNum("bk_q304");
        matchSettleScore13.setEventName("第3节比分");
        matchSettleScore13.setPeriodId(303L);
        list.add(matchSettleScore13);
        //4节比分  bk_q401 ~ bk_q404  bk_ft_rg
        MatchSettleScore matchSettleScore14 =initMatchSettleScore(standardMatchId);
        matchSettleScore14.setEventCode("score_change");
        matchSettleScore14.setSettleNum("bk_q401");
        matchSettleScore14.setEventName("第4节首先获得10分");
        matchSettleScore14.setExtryInfo("10");
        matchSettleScore14.setPeriodId(16l);
        list.add(matchSettleScore14);
        MatchSettleScore matchSettleScore15 =initMatchSettleScore(standardMatchId);
        matchSettleScore15.setEventCode("score_change");
        matchSettleScore15.setSettleNum("bk_q402");
        matchSettleScore15.setEventName("第4节首先获得20分");
        matchSettleScore15.setExtryInfo("20");
        matchSettleScore15.setPeriodId(16l);
        list.add(matchSettleScore15);
        MatchSettleScore matchSettleScore16 =initMatchSettleScore(standardMatchId);
        matchSettleScore16.setEventCode("score_change");
        matchSettleScore16.setSettleNum("bk_q403");
        matchSettleScore16.setEventName("第4节首先获得30分");
        matchSettleScore16.setExtryInfo("30");
        matchSettleScore16.setPeriodId(16l);
        list.add(matchSettleScore16);
        MatchSettleScore matchSettleScore17 =initMatchSettleScore(standardMatchId);
        matchSettleScore17.setEventCode("score_change");
        matchSettleScore17.setSettleNum("bk_q404");
        matchSettleScore17.setEventName("第4节比分");
        matchSettleScore17.setPeriodId(304l);
        list.add(matchSettleScore17);


        MatchSettleScore matchSettleScore18 =initMatchSettleScore(standardMatchId);
        matchSettleScore18.setEventCode("score_change");
        matchSettleScore18.setSettleNum("bk_ft_rg");
        matchSettleScore18.setEventName("全场比分");
        matchSettleScore18.setPeriodId(100L);
        list.add(matchSettleScore18);

        MatchSettleScore matchSettleScore171 =initMatchSettleScore(standardMatchId);
        matchSettleScore171.setEventCode("score_change");
        matchSettleScore171.setSettleNum("bk_2ht");
        matchSettleScore171.setEventName("下半场 ");
        matchSettleScore171.setPeriodId(8L);
        list.add(matchSettleScore171);




        MatchSettleScore matchSettleScore21 =initMatchSettleScore(standardMatchId);
        matchSettleScore21.setEventCode("score_change");
        matchSettleScore21.setSettleNum("bk_ft_et");
        matchSettleScore21.setEventName("全场 (含加时)");
        matchSettleScore21.setPeriodId(110L);
        list.add(matchSettleScore21);

        MatchSettleScore matchSettleScore1711 =initMatchSettleScore(standardMatchId);
        matchSettleScore1711.setEventCode("score_change");
        matchSettleScore1711.setSettleNum("bk_2htet");
        matchSettleScore1711.setEventName("下半场(含加时) ");
        matchSettleScore1711.setPeriodId(8L);
        list.add(matchSettleScore1711);

        //增加即时比分
        MatchSettleScore matchSettleScore22 =initMatchSettleScore(standardMatchId);
        matchSettleScore22.setEventCode("score_change");
        matchSettleScore22.setSettleNum("bk_in_q01");
        matchSettleScore22.setEventName("第一节即时");
        matchSettleScore22.setPeriodId(13L);

        list.add(matchSettleScore22);

        MatchSettleScore matchSettleScore23 =initMatchSettleScore(standardMatchId);
        matchSettleScore23.setEventCode("score_change");
        matchSettleScore23.setSettleNum("bk_in_q02");
        matchSettleScore23.setEventName("第二节即时");
        matchSettleScore23.setPeriodId(14L);

        list.add(matchSettleScore23);

        MatchSettleScore matchSettleScore24 =initMatchSettleScore(standardMatchId);
        matchSettleScore24.setEventCode("score_change");
        matchSettleScore24.setSettleNum("bk_in_q03");
        matchSettleScore24.setEventName("第三节即时");
        matchSettleScore24.setPeriodId(15L);

        list.add(matchSettleScore24);

        MatchSettleScore matchSettleScore25 =initMatchSettleScore(standardMatchId);
        matchSettleScore25.setEventCode("score_change");
        matchSettleScore25.setSettleNum("bk_in_q04");
        matchSettleScore25.setEventName("第四节即时");
        matchSettleScore25.setPeriodId(16L);

        list.add(matchSettleScore25);

        MatchSettleScore matchSettleScore26 =initMatchSettleScore(standardMatchId);
        matchSettleScore26.setEventCode("score_change");
        matchSettleScore26.setSettleNum("bk_in_1ht");
        matchSettleScore26.setEventName("上半场");
        matchSettleScore26.setPeriodId(1L);

        list.add(matchSettleScore26);

        MatchSettleScore matchSettleScore27 =initMatchSettleScore(standardMatchId);
        matchSettleScore27.setEventCode("score_change");
        matchSettleScore27.setSettleNum("bk_in_2ht");
        matchSettleScore27.setEventName("下半场");
        matchSettleScore27.setPeriodId(2L);

        list.add(matchSettleScore27);

        MatchSettleScore matchSettleScore2711 =initMatchSettleScore(standardMatchId);
        matchSettleScore2711.setEventCode("score_change");
        matchSettleScore2711.setSettleNum("bk_in_2htet");
        matchSettleScore2711.setEventName("下半场即时(含加时) ");
        matchSettleScore2711.setPeriodId(2L);
        list.add(matchSettleScore2711);


        MatchSettleScore matchSettleScore28 =initMatchSettleScore(standardMatchId);
        matchSettleScore28.setEventCode("score_change");
        matchSettleScore28.setSettleNum("bk_in_rg");
        matchSettleScore28.setEventName("全场即时 (不含加时)");
        matchSettleScore28.setPeriodId(100L);

        list.add(matchSettleScore28);

        MatchSettleScore matchSettleScore29 =initMatchSettleScore(standardMatchId);
        matchSettleScore29.setEventCode("score_change");
        matchSettleScore29.setSettleNum("bk_in_et");
        matchSettleScore29.setEventName("全场即时 (不含加时)");
        matchSettleScore29.setPeriodId(110L);

        list.add(matchSettleScore29);

        //2656兜底方案
        MatchSettleScore matchSettleScore30 =initMatchSettleScore(standardMatchId);
        matchSettleScore30.setEventCode("score_change");
        matchSettleScore30.setSettleNum("bk_401");
        matchSettleScore30.setEventName("首个进球队伍");
        matchSettleScore30.setPeriodId(13L);

        list.add(matchSettleScore30);

        MatchSettleScore matchSettleScore31 =initMatchSettleScore(standardMatchId);
        matchSettleScore31.setEventCode("score_change");
        matchSettleScore31.setSettleNum("bk_403");
        matchSettleScore31.setEventName("最后一个进球队伍");
        matchSettleScore31.setPeriodId(110L);

        list.add(matchSettleScore31);

        //3259 篮球6分钟玩法初始化
        MatchSettleScore matchSettleScore32 =initMatchSettleScore(standardMatchId);
        matchSettleScore32.setEventCode("score_change");
        matchSettleScore32.setSettleNum("bk_q1041");
        matchSettleScore32.setEventName("第1节开始-06：01");
        matchSettleScore32.setPeriodId(13L);
        list.add(matchSettleScore32);

        MatchSettleScore matchSettleScore33 =initMatchSettleScore(standardMatchId);
        matchSettleScore33.setEventCode("score_change");
        matchSettleScore33.setSettleNum("bk_q1042");
        matchSettleScore33.setEventName("06：00-第1节结束");
        matchSettleScore33.setPeriodId(13L);
        list.add(matchSettleScore33);

        MatchSettleScore matchSettleScore34 =initMatchSettleScore(standardMatchId);
        matchSettleScore34.setEventCode("score_change");
        matchSettleScore34.setSettleNum("bk_q2041");
        matchSettleScore34.setEventName("第2节开始-06：01");
        matchSettleScore34.setPeriodId(14L);

        list.add(matchSettleScore34);

        MatchSettleScore matchSettleScore35 =initMatchSettleScore(standardMatchId);
        matchSettleScore35.setEventCode("score_change");
        matchSettleScore35.setSettleNum("bk_q2042");
        matchSettleScore35.setEventName("06：00-第2节结束");
        matchSettleScore35.setPeriodId(14L);
        list.add(matchSettleScore35);

        MatchSettleScore matchSettleScore36 =initMatchSettleScore(standardMatchId);
        matchSettleScore36.setEventCode("score_change");
        matchSettleScore36.setSettleNum("bk_q3041");
        matchSettleScore36.setEventName("第3节开始-06：01");
        matchSettleScore36.setPeriodId(15L);
        list.add(matchSettleScore36);

        MatchSettleScore matchSettleScore37 =initMatchSettleScore(standardMatchId);
        matchSettleScore37.setEventCode("score_change");
        matchSettleScore37.setSettleNum("bk_q3042");
        matchSettleScore37.setEventName("06：00-第3节结束");
        matchSettleScore37.setPeriodId(15L);
        list.add(matchSettleScore37);

        MatchSettleScore matchSettleScore38 =initMatchSettleScore(standardMatchId);
        matchSettleScore38.setEventCode("score_change");
        matchSettleScore38.setSettleNum("bk_q4041");
        matchSettleScore38.setEventName("第4节开始-06：01");
        matchSettleScore38.setPeriodId(16L);
        list.add(matchSettleScore38);

        MatchSettleScore matchSettleScore39 =initMatchSettleScore(standardMatchId);
        matchSettleScore39.setEventCode("score_change");
        matchSettleScore39.setSettleNum("bk_q4042");
        matchSettleScore39.setEventName("06：00-第4节结束");
        matchSettleScore39.setPeriodId(16L);
        list.add(matchSettleScore39);

        MatchSettleScore matchSettleScore40 =initMatchSettleScore(standardMatchId);
        matchSettleScore40.setEventCode("score_change");
        matchSettleScore40.setSettleNum("bk_et");
        matchSettleScore40.setEventName("加时赛");
        matchSettleScore40.setPeriodId(9L);
        list.add(matchSettleScore40);

        //全场比分 bk_1st_10~bk_1st_150
        for(int i=1;i<=15;i++){
            Integer x= i*10;
            String settNum = "bk_1st_"+x;
            MatchSettleScore matchSettleScore =initMatchSettleScore(standardMatchId);
            matchSettleScore.setEventCode("score_change");
            matchSettleScore.setSettleNum(settNum);
            matchSettleScore.setEventName("首先获得"+x+"分");
            matchSettleScore.setExtryInfo(x+"");
            matchSettleScore.setPeriodId(0l);
            list.add(matchSettleScore);
        }
    }


    public static MatchSettleScore initMatchSettleScore(Long standardMatchId){
        MatchSettleScore matchSettleScore =new MatchSettleScore();
        matchSettleScore.setModifyTime(System.currentTimeMillis());
        matchSettleScore.setCreateTime(System.currentTimeMillis());
        matchSettleScore.setStatus(0);
        matchSettleScore.setId(IdGenerator.nextId());
        matchSettleScore.setSettleTimes(0);
        matchSettleScore.setStandardMatchId(standardMatchId);
        matchSettleScore.setDataSourceCode("PA");
        matchSettleScore.setCheckNumber(1);
        matchSettleScore.setSportId(2l);
        return  matchSettleScore;
    }


    public static Integer compareInfoScore(Integer home, Integer away, Integer infoScore) {
        if((home>=infoScore&&away>=infoScore)||(home<infoScore&&away<infoScore)){
            return 0;
        }else if(home>=infoScore&&away<infoScore){
            return 1;
        }else if(home<infoScore&&away>=infoScore){
            return -1;
        }else {
            return 0;
        }

    }

    public static Integer beforeInfoScore(Integer home, Integer away, Integer infoScore) {

        if((home<infoScore&&away<infoScore)){
            return 0;
        }else {
            return 1;
        }

    }

    public static List<MatchSettleScore> createBasketInSettleScore(Long standardMatchId) {
        List<MatchSettleScore> list =new ArrayList<>();
        MatchSettleScore matchSettleScore22 =initMatchSettleScore(standardMatchId);
        matchSettleScore22.setEventCode("score_change");
        matchSettleScore22.setSettleNum("bk_in_q01");
        matchSettleScore22.setEventName("第一节即时");
        matchSettleScore22.setPeriodId(13L);

        list.add(matchSettleScore22);

        MatchSettleScore matchSettleScore23 =initMatchSettleScore(standardMatchId);
        matchSettleScore23.setEventCode("score_change");
        matchSettleScore23.setSettleNum("bk_in_q02");
        matchSettleScore23.setEventName("第二节即时");
        matchSettleScore23.setPeriodId(14L);

        list.add(matchSettleScore23);

        MatchSettleScore matchSettleScore24 =initMatchSettleScore(standardMatchId);
        matchSettleScore24.setEventCode("score_change");
        matchSettleScore24.setSettleNum("bk_in_q03");
        matchSettleScore24.setEventName("第三节即时");
        matchSettleScore24.setPeriodId(15L);

        list.add(matchSettleScore24);

        MatchSettleScore matchSettleScore25 =initMatchSettleScore(standardMatchId);
        matchSettleScore25.setEventCode("score_change");
        matchSettleScore25.setSettleNum("bk_in_q04");
        matchSettleScore25.setEventName("第四节即时");
        matchSettleScore25.setPeriodId(16L);

        list.add(matchSettleScore25);

        MatchSettleScore matchSettleScore26 =initMatchSettleScore(standardMatchId);
        matchSettleScore26.setEventCode("score_change");
        matchSettleScore26.setSettleNum("bk_in_1ht");
        matchSettleScore26.setEventName("上半场");
        matchSettleScore26.setPeriodId(1L);

        list.add(matchSettleScore26);

        MatchSettleScore matchSettleScore27 =initMatchSettleScore(standardMatchId);
        matchSettleScore27.setEventCode("score_change");
        matchSettleScore27.setSettleNum("bk_in_2ht");
        matchSettleScore27.setEventName("下半场");
        matchSettleScore27.setPeriodId(2L);

        list.add(matchSettleScore27);

        MatchSettleScore matchSettleScore28 =initMatchSettleScore(standardMatchId);
        matchSettleScore28.setEventCode("score_change");
        matchSettleScore28.setSettleNum("bk_in_rg");
        matchSettleScore28.setEventName("全场即时 (不含加时)");
        matchSettleScore28.setPeriodId(100L);

        list.add(matchSettleScore28);

        MatchSettleScore matchSettleScore29 =initMatchSettleScore(standardMatchId);
        matchSettleScore29.setEventCode("score_change");
        matchSettleScore29.setSettleNum("bk_in_et");
        matchSettleScore29.setEventName("全场即时 (不含加时)");
        matchSettleScore29.setPeriodId(110L);

        list.add(matchSettleScore29);
        return list;
    }

    public static List<MatchSettleScore> createBasket2HTETSettleScore(Long standardMatchId) {
        List<MatchSettleScore> list =new ArrayList<>();
        MatchSettleScore matchSettleScore1711 =initMatchSettleScore(standardMatchId);
        matchSettleScore1711.setEventCode("score_change");
        matchSettleScore1711.setSettleNum("bk_2htet");
        matchSettleScore1711.setEventName("下半场(含加时) ");
        matchSettleScore1711.setPeriodId(8L);
        list.add(matchSettleScore1711);

        MatchSettleScore matchSettleScore2711 =initMatchSettleScore(standardMatchId);
        matchSettleScore2711.setEventCode("score_change");
        matchSettleScore2711.setSettleNum("bk_in_2htet");
        matchSettleScore2711.setEventName("下半场即时(含加时) ");
        matchSettleScore2711.setPeriodId(2L);
        list.add(matchSettleScore2711);

        return list;
    }
}
