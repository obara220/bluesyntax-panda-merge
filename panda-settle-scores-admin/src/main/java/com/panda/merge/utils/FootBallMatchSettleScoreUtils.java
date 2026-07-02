package com.panda.merge.utils;

import com.panda.merge.common.enums.BasketBallSettleNumEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.DownSettleDto;
import com.panda.merge.dto.GrayAreaSettleDto;
import com.panda.merge.model.*;
import com.panda.merge.v2.entity.MatchSettleAbnormalEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FootBallMatchSettleScoreUtils {

    private static final List<String> basketballGrayDelayNum = Arrays.asList(BasketBallSettleNumEnum.BK_Q1041.getCode(),
            BasketBallSettleNumEnum.BK_Q2041.getCode(), BasketBallSettleNumEnum.BK_Q3041.getCode(), BasketBallSettleNumEnum.BK_Q4041.getCode());
    public static List<MatchSettleScore> createInitMatchSettleScores(Long standardMatchId){
        List<MatchSettleScore> list=new ArrayList<>();
        initGoalScore(list,standardMatchId);
        initFiveMinGoalScore(list,standardMatchId);
        initCornerScore(list,standardMatchId);
        initfa_cardScore(list,standardMatchId);
        return  list;
    }
    private static void initGoalScore(List<MatchSettleScore> list,Long standardMatchId) {
        MatchSettleScore matchSettleScore1 =initMatchSettleScore(standardMatchId);
        matchSettleScore1.setEventCode("kick_off");
        matchSettleScore1.setSettleNum("101");
        matchSettleScore1.setEventName("Kick-off");
        matchSettleScore1.setPeriodId(6l);
        list.add(matchSettleScore1);
        MatchSettleScore matchSettleScore2 =initMatchSettleScore(standardMatchId);
        matchSettleScore2.setEventCode("goal");
        matchSettleScore2.setSettleNum("102");
        matchSettleScore2.setEventName("00:00 - 14:59");
        matchSettleScore2.setPeriodId(6l);
        list.add(matchSettleScore2);
        MatchSettleScore matchSettleScore3 =initMatchSettleScore(standardMatchId);
        matchSettleScore3.setEventCode("goal");
        matchSettleScore3.setSettleNum("103");
        matchSettleScore3.setEventName("15:00 - 29:59");
        matchSettleScore3.setPeriodId(6l);
        list.add(matchSettleScore3);
        MatchSettleScore matchSettleScore4 =initMatchSettleScore(standardMatchId);
        matchSettleScore4.setEventCode("goal");
        matchSettleScore4.setSettleNum("104");
        matchSettleScore4.setEventName("30:00 - 1HT");
        matchSettleScore4.setPeriodId(6l);
        list.add(matchSettleScore4);
        MatchSettleScore matchSettleScore5 =initMatchSettleScore(standardMatchId);
        matchSettleScore5.setEventCode("goal");
        matchSettleScore5.setSettleNum("105");
        matchSettleScore5.setEventName("1HT");
        matchSettleScore5.setPeriodId(31l);
        list.add(matchSettleScore5);
        MatchSettleScore matchSettleScore6 =initMatchSettleScore(standardMatchId);
        matchSettleScore6.setEventCode("goal");
        matchSettleScore6.setSettleNum("106");
        matchSettleScore6.setEventName("1HT - 59:59");
        matchSettleScore6.setPeriodId(7l);
        list.add(matchSettleScore6);
        MatchSettleScore matchSettleScore7 =initMatchSettleScore(standardMatchId);
        matchSettleScore7.setEventCode("goal");
        matchSettleScore7.setSettleNum("107");
        matchSettleScore7.setEventName("60:00 - 74:59");
        matchSettleScore7.setPeriodId(7l);
        list.add(matchSettleScore7);
        MatchSettleScore matchSettleScore8 =initMatchSettleScore(standardMatchId);
        matchSettleScore8.setEventCode("goal");
        matchSettleScore8.setSettleNum("108");
        matchSettleScore8.setEventName("75:00 - FT");
        matchSettleScore8.setPeriodId(7l);
        list.add(matchSettleScore8);
        MatchSettleScore matchSettleScore9 =initMatchSettleScore(standardMatchId);
        matchSettleScore9.setEventCode("goal");
        matchSettleScore9.setSettleNum("109");
        matchSettleScore9.setEventName("2HT");
        matchSettleScore9.setPeriodId(8l);
        list.add(matchSettleScore9);
        MatchSettleScore matchSettleScore10 =initMatchSettleScore(standardMatchId);
        matchSettleScore10.setEventCode("goal");
        matchSettleScore10.setSettleNum("1010");
        matchSettleScore10.setEventName("FT");
        matchSettleScore10.setPeriodId(100l);
        list.add(matchSettleScore10);
        MatchSettleScore matchSettleScore11 =initMatchSettleScore(standardMatchId);
        matchSettleScore11.setEventCode("goal");
        matchSettleScore11.setSettleNum("1011");
        matchSettleScore11.setEventName("ET 00:00 - 04:59");
        matchSettleScore11.setPeriodId(41l);
        list.add(matchSettleScore11);
        MatchSettleScore matchSettleScore12 =initMatchSettleScore(standardMatchId);
        matchSettleScore12.setEventCode("goal");
        matchSettleScore12.setSettleNum("1012");
        matchSettleScore12.setEventName("ET 05:00 - 09:59");
        matchSettleScore12.setPeriodId(41l);
        list.add(matchSettleScore12);
        MatchSettleScore matchSettleScore13 =initMatchSettleScore(standardMatchId);
        matchSettleScore13.setEventCode("goal");
        matchSettleScore13.setSettleNum("1013");
        matchSettleScore13.setEventName("ET 10:00 - 1ET");
        matchSettleScore13.setPeriodId(41l);
        list.add(matchSettleScore13);
        MatchSettleScore matchSettleScore14 =initMatchSettleScore(standardMatchId);
        matchSettleScore14.setEventCode("goal");
        matchSettleScore14.setSettleNum("1014");
        matchSettleScore14.setEventName("1ET");
        matchSettleScore14.setPeriodId(33l);
        list.add(matchSettleScore14);
        MatchSettleScore matchSettleScore15 =initMatchSettleScore(standardMatchId);
        matchSettleScore15.setEventCode("goal");
        matchSettleScore15.setSettleNum("1015");
        matchSettleScore15.setEventName("ET 1ET - 19:59");
        matchSettleScore15.setPeriodId(42l);
        list.add(matchSettleScore15);
        MatchSettleScore matchSettleScore16 =initMatchSettleScore(standardMatchId);
        matchSettleScore16.setEventCode("goal");
        matchSettleScore16.setSettleNum("1016");
        matchSettleScore16.setEventName("20:00 - 24:59");
        matchSettleScore16.setPeriodId(42l);
        list.add(matchSettleScore16);
        MatchSettleScore matchSettleScore17 =initMatchSettleScore(standardMatchId);
        matchSettleScore17.setEventCode("goal");
        matchSettleScore17.setSettleNum("1017");
        matchSettleScore17.setEventName("25:00 - ET");
        matchSettleScore17.setPeriodId(42l);
        list.add(matchSettleScore17);
        MatchSettleScore matchSettleScore18 =initMatchSettleScore(standardMatchId);
        matchSettleScore18.setEventCode("goal");
        matchSettleScore18.setSettleNum("1018");
        matchSettleScore18.setEventName("2ET");
        matchSettleScore18.setPeriodId(43l);
        list.add(matchSettleScore18);
        MatchSettleScore matchSettleScore19 =initMatchSettleScore(standardMatchId);
        matchSettleScore19.setEventCode("goal");
        matchSettleScore19.setSettleNum("1019");
        matchSettleScore19.setEventName("ET");
        matchSettleScore19.setPeriodId(110l);
        list.add(matchSettleScore19);
        MatchSettleScore matchSettleScore20 =initMatchSettleScore(standardMatchId);
        matchSettleScore20.setEventCode("goal");
        matchSettleScore20.setSettleNum("1020");
        matchSettleScore20.setEventName("Winner / To Qualify");
        matchSettleScore20.setPeriodId(999l);
        list.add(matchSettleScore20);
        MatchSettleScore matchSettleScore21 =initMatchSettleScore(standardMatchId);
        matchSettleScore21.setEventCode("goal");
        matchSettleScore21.setSettleNum("1021");
        matchSettleScore21.setEventName("Winning Method");
        matchSettleScore21.setPeriodId(999l);
        list.add(matchSettleScore21);
        MatchSettleScore matchSettleScore22 =initMatchSettleScore(standardMatchId);
        matchSettleScore22.setEventCode("goal");
        matchSettleScore22.setSettleNum("1031");
        matchSettleScore22.setEventName("Penalty Shoot-out");
        matchSettleScore22.setPeriodId(999l);
        list.add(matchSettleScore22);
        MatchSettleScore matchSettleScore23 =initMatchSettleScore(standardMatchId);
        matchSettleScore23.setEventCode("goal");
        matchSettleScore23.setSettleNum("1032");
        matchSettleScore23.setEventName("No Extra Time&& Penalty Shoot-out");
        matchSettleScore23.setPeriodId(999l);
        list.add(matchSettleScore23);
        MatchSettleScore matchSettleScore24 =initMatchSettleScore(standardMatchId);
        matchSettleScore24.setEventCode("goal");
        matchSettleScore24.setSettleNum("1033");
        matchSettleScore24.setEventName("No Penalty Shoot-out");
        matchSettleScore24.setPeriodId(999l);
        list.add(matchSettleScore24);

    }
    private static void initCornerScore(List<MatchSettleScore> list,Long standardMatchId) {
        MatchSettleScore matchSettleScore1 =initMatchSettleScore(standardMatchId);
        matchSettleScore1.setEventCode("corner");
        matchSettleScore1.setSettleNum("201");
        matchSettleScore1.setEventName("1HT CR");
        matchSettleScore1.setPeriodId(31L);
        list.add(matchSettleScore1);
        MatchSettleScore matchSettleScore2 =initMatchSettleScore(standardMatchId);
        matchSettleScore2.setEventCode("corner");
        matchSettleScore2.setSettleNum("202");
        matchSettleScore2.setEventName("2HT CR");
        matchSettleScore2.setPeriodId(8L);
        list.add(matchSettleScore2);
        MatchSettleScore matchSettleScore3 =initMatchSettleScore(standardMatchId);
        matchSettleScore3.setEventCode("corner");
        matchSettleScore3.setSettleNum("203");
        matchSettleScore3.setEventName("FT CR");
        matchSettleScore3.setPeriodId(100L);
        list.add(matchSettleScore3);
        MatchSettleScore matchSettleScore4 =initMatchSettleScore(standardMatchId);
        matchSettleScore4.setEventCode("corner");
        matchSettleScore4.setSettleNum("206");
        matchSettleScore4.setEventName("1ET CR");
        matchSettleScore4.setPeriodId(33L);
        list.add(matchSettleScore4);
        MatchSettleScore matchSettleScore5 =initMatchSettleScore(standardMatchId);
        matchSettleScore5.setEventCode("corner");
        matchSettleScore5.setSettleNum("207");
        matchSettleScore5.setEventName("2ET CR");
        matchSettleScore5.setPeriodId(43L);
        list.add(matchSettleScore5);
        MatchSettleScore matchSettleScore6 =initMatchSettleScore(standardMatchId);
        matchSettleScore6.setEventCode("corner");
        matchSettleScore6.setSettleNum("208");
        matchSettleScore6.setEventName("ET CR");
        matchSettleScore6.setPeriodId(110L);
        list.add(matchSettleScore6);
        //15分钟角球
        MatchSettleScore matchSettleScore7 =initMatchSettleScore(standardMatchId);
        matchSettleScore7.setEventCode("corner");
        matchSettleScore7.setSettleNum("2011");
        matchSettleScore7.setEventName("CR 00:00 - 14:59");
        matchSettleScore7.setPeriodId(6l);
        list.add(matchSettleScore7);
        MatchSettleScore matchSettleScore8 =initMatchSettleScore(standardMatchId);
        matchSettleScore8.setEventCode("corner");
        matchSettleScore8.setSettleNum("2012");
        matchSettleScore8.setEventName("CR 15:00 - 29:59");
        matchSettleScore8.setPeriodId(6l);
        list.add(matchSettleScore8);
        MatchSettleScore matchSettleScore9 =initMatchSettleScore(standardMatchId);
        matchSettleScore9.setEventCode("corner");
        matchSettleScore9.setSettleNum("2013");
        matchSettleScore9.setEventName("CR 30:00 - HT");
        matchSettleScore9.setPeriodId(6l);
        list.add(matchSettleScore9);
        MatchSettleScore matchSettleScore10 =initMatchSettleScore(standardMatchId);
        matchSettleScore10.setEventCode("corner");
        matchSettleScore10.setSettleNum("2014");
        matchSettleScore10.setEventName("CR HT - 59:59");
        matchSettleScore10.setPeriodId(7l);
        list.add(matchSettleScore10);
        MatchSettleScore matchSettleScore11 =initMatchSettleScore(standardMatchId);
        matchSettleScore11.setEventCode("corner");
        matchSettleScore11.setSettleNum("2015");
        matchSettleScore11.setEventName("CR 60:00 - 74:59");
        matchSettleScore11.setPeriodId(7l);
        list.add(matchSettleScore11);
        MatchSettleScore matchSettleScore12 =initMatchSettleScore(standardMatchId);
        matchSettleScore12.setEventCode("corner");
        matchSettleScore12.setSettleNum("2016");
        matchSettleScore12.setEventName("CR 75:00 - FT");
        matchSettleScore12.setPeriodId(7l);
        list.add(matchSettleScore12);
        MatchSettleScore matchSettleScore13 =initMatchSettleScore(standardMatchId);
        matchSettleScore13.setEventCode("corner");
        matchSettleScore13.setSettleNum("2017");
        matchSettleScore13.setEventName("EC 00:00 - 04:59");
        matchSettleScore13.setPeriodId(41L);
        list.add(matchSettleScore13);
        MatchSettleScore matchSettleScore14 =initMatchSettleScore(standardMatchId);
        matchSettleScore14.setEventCode("corner");
        matchSettleScore14.setSettleNum("2018");
        matchSettleScore14.setEventName("EC 05:00 - 09:45");
        matchSettleScore14.setPeriodId(41L);
        list.add(matchSettleScore14);
        MatchSettleScore matchSettleScore15 =initMatchSettleScore(standardMatchId);
        matchSettleScore15.setEventCode("corner");
        matchSettleScore15.setSettleNum("2019");
        matchSettleScore15.setEventName("EC 10:00 - 1ET");
        matchSettleScore15.setPeriodId(41L);
        list.add(matchSettleScore15);
        MatchSettleScore matchSettleScore16 =initMatchSettleScore(standardMatchId);
        matchSettleScore16.setEventCode("corner");
        matchSettleScore16.setSettleNum("2020");
        matchSettleScore16.setEventName("EC 1ET - 19:59");
        matchSettleScore16.setPeriodId(42L);
        list.add(matchSettleScore16);
        MatchSettleScore matchSettleScore17 =initMatchSettleScore(standardMatchId);
        matchSettleScore17.setEventCode("corner");
        matchSettleScore17.setSettleNum("2021");
        matchSettleScore17.setEventName("EC 20:00 - 24:59");
        matchSettleScore17.setPeriodId(42L);
        list.add(matchSettleScore17);
        MatchSettleScore matchSettleScore18 =initMatchSettleScore(standardMatchId);
        matchSettleScore18.setEventCode("corner");
        matchSettleScore18.setSettleNum("2022");
        matchSettleScore18.setEventName("EC 25:00 - ET");
        matchSettleScore18.setPeriodId(42L);
        list.add(matchSettleScore18);
    }
    private static void initfa_cardScore(List<MatchSettleScore> list,Long standardMatchId) {
        MatchSettleScore matchSettleScore1 =initMatchSettleScore(standardMatchId);
        matchSettleScore1.setEventCode("fa_card");
        matchSettleScore1.setSettleNum("301");
        matchSettleScore1.setEventName("BK 00:00 - 14:59");
        matchSettleScore1.setPeriodId(6l);
        list.add(matchSettleScore1);
        MatchSettleScore matchSettleScore2 =initMatchSettleScore(standardMatchId);
        matchSettleScore2.setEventCode("fa_card");
        matchSettleScore2.setSettleNum("302");
        matchSettleScore2.setEventName("BK 15:00 - 29:59");
        matchSettleScore2.setPeriodId(6l);
        list.add(matchSettleScore2);
        MatchSettleScore matchSettleScore3 =initMatchSettleScore(standardMatchId);
        matchSettleScore3.setEventCode("fa_card");
        matchSettleScore3.setSettleNum("303");
        matchSettleScore3.setEventName("BK 30:00 - 1HT");
        matchSettleScore3.setPeriodId(6l);
        list.add(matchSettleScore3);
        MatchSettleScore matchSettleScore4 =initMatchSettleScore(standardMatchId);
        matchSettleScore4.setEventCode("fa_card");
        matchSettleScore4.setSettleNum("304");
        matchSettleScore4.setEventName("BK 1HT");
        matchSettleScore4.setPeriodId(31l);
        list.add(matchSettleScore4);

        //[2975需求] 此需求4月份版本,合代码切记别合到3月咯
        MatchSettleScore matchSettleScore4red =initMatchSettleScore(standardMatchId);
        matchSettleScore4red.setEventCode("red_card");
        matchSettleScore4red.setSettleNum("3041");
        matchSettleScore4red.setEventName("1st Half Bookings -red card");
        matchSettleScore4red.setPeriodId(31l);
        list.add(matchSettleScore4red);

        MatchSettleScore matchSettleScore5 =initMatchSettleScore(standardMatchId);
        matchSettleScore5.setEventCode("fa_card");
        matchSettleScore5.setSettleNum("305");
        matchSettleScore5.setEventName("BK 1HT - 59:59");
        matchSettleScore5.setPeriodId(7l);
        list.add(matchSettleScore5);
        MatchSettleScore matchSettleScore6 =initMatchSettleScore(standardMatchId);
        matchSettleScore6.setEventCode("fa_card");
        matchSettleScore6.setSettleNum("306");
        matchSettleScore6.setEventName("BK 60:00 - 74:59");
        matchSettleScore6.setPeriodId(7l);
        list.add(matchSettleScore6);
        MatchSettleScore matchSettleScore7 =initMatchSettleScore(standardMatchId);
        matchSettleScore7.setEventCode("fa_card");
        matchSettleScore7.setSettleNum("307");
        matchSettleScore7.setEventName("BK 75:00 - FT");
        matchSettleScore7.setPeriodId(7l);
        list.add(matchSettleScore7);
        MatchSettleScore matchSettleScore8 =initMatchSettleScore(standardMatchId);
        matchSettleScore8.setEventCode("fa_card");
        matchSettleScore8.setSettleNum("308");
        matchSettleScore8.setEventName("BK 2HT");
        matchSettleScore8.setPeriodId(8l);
        list.add(matchSettleScore8);
        MatchSettleScore matchSettleScore9 =initMatchSettleScore(standardMatchId);
        matchSettleScore9.setEventCode("fa_card");
        matchSettleScore9.setSettleNum("309");
        matchSettleScore9.setEventName("BK FT");
        matchSettleScore9.setPeriodId(100l);
        list.add(matchSettleScore9);
        MatchSettleScore matchSettleScore10 =initMatchSettleScore(standardMatchId);
        matchSettleScore10.setEventCode("fa_card");
        matchSettleScore10.setSettleNum("3010");
        matchSettleScore10.setEventName("EB 00:00 - 04:59");
        matchSettleScore10.setPeriodId(41l);
        list.add(matchSettleScore10);
        MatchSettleScore matchSettleScore11 =initMatchSettleScore(standardMatchId);
        matchSettleScore11.setEventCode("fa_card");
        matchSettleScore11.setSettleNum("3011");
        matchSettleScore11.setEventName("EB 05:00 - 09:45");
        matchSettleScore11.setPeriodId(41L);
        list.add(matchSettleScore11);
        MatchSettleScore matchSettleScore12 =initMatchSettleScore(standardMatchId);
        matchSettleScore12.setEventCode("fa_card");
        matchSettleScore12.setSettleNum("3012");
        matchSettleScore12.setEventName("EB 10:00 - 1ET");
        matchSettleScore12.setPeriodId(41L);
        list.add(matchSettleScore12);
        MatchSettleScore matchSettleScore13 =initMatchSettleScore(standardMatchId);
        matchSettleScore13.setEventCode("fa_card");
        matchSettleScore13.setSettleNum("3013");
        matchSettleScore13.setEventName("1ET BK");
        matchSettleScore13.setPeriodId(33L);
        list.add(matchSettleScore13);
        MatchSettleScore matchSettleScore14 =initMatchSettleScore(standardMatchId);
        matchSettleScore14.setEventCode("fa_card");
        matchSettleScore14.setSettleNum("3014");
        matchSettleScore14.setEventName("EB 1ET - 19:59");
        matchSettleScore14.setPeriodId(42L);
        list.add(matchSettleScore14);
        MatchSettleScore matchSettleScore15 =initMatchSettleScore(standardMatchId);
        matchSettleScore15.setEventCode("fa_card");
        matchSettleScore15.setSettleNum("3015");
        matchSettleScore15.setEventName("EB 20:00 - 24:59");
        matchSettleScore15.setPeriodId(42L);
        list.add(matchSettleScore15);
        MatchSettleScore matchSettleScore16 =initMatchSettleScore(standardMatchId);
        matchSettleScore16.setEventCode("fa_card");
        matchSettleScore16.setSettleNum("3016");
        matchSettleScore16.setEventName("EB 25:00 - ET");
        matchSettleScore16.setPeriodId(42L);
        list.add(matchSettleScore16);
        MatchSettleScore matchSettleScore17 =initMatchSettleScore(standardMatchId);
        matchSettleScore17.setEventCode("fa_card");
        matchSettleScore17.setSettleNum("3017");
        matchSettleScore17.setEventName("2ET BK");
        matchSettleScore17.setPeriodId(43l);
        list.add(matchSettleScore17);
        MatchSettleScore matchSettleScore18 =initMatchSettleScore(standardMatchId);
        matchSettleScore18.setEventCode("fa_card");
        matchSettleScore18.setSettleNum("3018");
        matchSettleScore18.setEventName("ET BK");
        matchSettleScore18.setPeriodId(110l);
        list.add(matchSettleScore18);
    }

    /**
     * 初始化异常结算
     * @param standardMatchId
     * @param sportId
     * @return
     */
    public static MatchSettleAbnormalEntity initMatchSettleAbnormal(Long standardMatchId, Long sportId){
        MatchSettleAbnormalEntity matchSettleAbnormal =new MatchSettleAbnormalEntity();
        matchSettleAbnormal.setModifyTime(System.currentTimeMillis());
        matchSettleAbnormal.setCreateTime(System.currentTimeMillis());
        matchSettleAbnormal.setId(IdGenerator.nextId());
        matchSettleAbnormal.setSettleTimes(0);
        matchSettleAbnormal.setSettleCount(0);
        matchSettleAbnormal.setSportId(sportId);
        matchSettleAbnormal.setStatus(0);
        matchSettleAbnormal.setStandardMatchId(standardMatchId);
        return  matchSettleAbnormal;
    }


    public static MatchSettleScore initMatchSettleScore(Long standardMatchId){
        MatchSettleScore matchSettleScore =new MatchSettleScore();
        matchSettleScore.setModifyTime(System.currentTimeMillis());
        matchSettleScore.setCreateTime(System.currentTimeMillis());
        matchSettleScore.setStatus(0);
        matchSettleScore.setId(IdGenerator.nextId());
        matchSettleScore.setDataSourceCode("PA");
//        matchSettleScore.setT1(0);
//        matchSettleScore.setT2(0);
//        matchSettleScore.setSecondT1(0);
//        matchSettleScore.setSecondT2(0);
//        matchSettleScore.setFirstT1(0);
//        matchSettleScore.setFirstT2(0);
        matchSettleScore.setSettleTimes(0);
        matchSettleScore.setStandardMatchId(standardMatchId);
        matchSettleScore.setSettleTimes(0);
        matchSettleScore.setSettleCount(0);
        matchSettleScore.setCheckNumber(1);
        matchSettleScore.setSportId(1l);
        return  matchSettleScore;
    }
    public static MatchSettleEvent initMatchSettleEvent(Long standardMatchId){
        MatchSettleEvent matchSettleEvent =new MatchSettleEvent();
        matchSettleEvent.setModifyTime(System.currentTimeMillis());
        matchSettleEvent.setCreateTime(System.currentTimeMillis());
        matchSettleEvent.setStatus(0);
        matchSettleEvent.setId(IdGenerator.nextId());
        matchSettleEvent.setDataSourceCode("PA");
        matchSettleEvent.setT1(null);
        matchSettleEvent.setT2(null);
        matchSettleEvent.setEventType(1);
        matchSettleEvent.setSecondT1(null);
        matchSettleEvent.setSecondT2(null);
        matchSettleEvent.setFirstT1(null);
        matchSettleEvent.setFirstT2(null);
        matchSettleEvent.setSettleTimes(0);
        matchSettleEvent.setStandardMatchId(standardMatchId);
        matchSettleEvent.setSettleTimes(0);
        matchSettleEvent.setSettleCount(0);
        matchSettleEvent.setCheckNumber(1);
        matchSettleEvent.setSportId(1l);
        matchSettleEvent.setSettleFreeze(0);
        return  matchSettleEvent;
    }

//    public static List<MatchSettleEvent> createInitMatchSettleEvents(Long standardMatchId) {
//        return createInitMatchSettleEvents(standardMatchId, null);
//    }

    /**
     * 创建初始化的MatchSettleEvent列表，过滤掉已存在的事件
     * @param standardMatchId 标准比赛ID
     * @param existingEvents 已存在的事件列表，如果为null则不进行过滤
     * @return 需要初始化的事件列表
     */
    public static List<MatchSettleEvent> createInitMatchSettleEvents(Long standardMatchId, List<MatchSettleEvent> existingEvents) {
        List<MatchSettleEvent> list=new ArrayList<>();
        initGoalEvent(list,standardMatchId);
        initCornerEvent(list,standardMatchId);
        initfa_cardEvent(list,standardMatchId);
        
        // 为eventType=1的事件创建对应的eventType=3（时段事件）
        initEventType3Events(list, standardMatchId);
        
        // 如果存在已创建的事件，则过滤掉已存在的事件
        if (existingEvents != null && !existingEvents.isEmpty()) {
            list = filterExistingEvents(list, existingEvents);
        }
        return list;
    }

    /**
     * 为eventType=1的事件创建对应的eventType=3（时段事件）
     * eventType=3用于展示时段信息，只在periodId=6L, 7L时创建（加时赛41L, 42L没有eventType=3）
     */
    private static void initEventType3Events(List<MatchSettleEvent> list, Long standardMatchId) {
        // 找出所有eventType=1且periodId在6L, 7L的事件（加时赛41L, 42L没有eventType=3）
        List<MatchSettleEvent> eventType1Events = new ArrayList<>();
        for (MatchSettleEvent event : list) {
            if (event.getEventType() != null && event.getEventType() == 1) {
                Long periodId = event.getPeriodId();
                if (periodId != null && (periodId.equals(6L) || periodId.equals(7L))) {
                    eventType1Events.add(event);
                }
            }
        }
        
        // 为每个eventType=1的事件创建对应的eventType=3事件
        List<MatchSettleEvent> eventType3Events = new ArrayList<>();
        for (MatchSettleEvent eventType1Event : eventType1Events) {
            MatchSettleEvent eventType3Event = initMatchSettleEvent(standardMatchId);
            // 复制eventType=1事件的基本信息
            eventType3Event.setEventCode(eventType1Event.getEventCode());
            eventType3Event.setEventName(eventType1Event.getEventName());
            eventType3Event.setPeriodId(eventType1Event.getPeriodId());
            eventType3Event.setEventOrder(eventType1Event.getEventOrder());
            eventType3Event.setHomeAway(eventType1Event.getHomeAway());
            eventType3Event.setThirdEventSourceId(eventType1Event.getThirdEventSourceId());
            // 设置eventType=3
            eventType3Event.setEventType(3);
            // 使用SettleNumUtils获取eventType=3的settleNum
            String settleNum = SettleNumUtils.getTypeEventSettleNum(eventType3Event.getEventCode(), eventType3Event.getPeriodId(), 3);
            if (settleNum != null && !settleNum.isEmpty()) {
                eventType3Event.setSettleNum(settleNum);
                eventType3Events.add(eventType3Event);
            }
        }
        
        // 将eventType=3事件添加到列表中
        list.addAll(eventType3Events);
    }

    /**
     * 过滤掉已存在的事件
     * 判断标准：standardMatchId、eventCode、settleNum、periodId、eventType、eventOrder 都相同
     */
    private static List<MatchSettleEvent> filterExistingEvents(List<MatchSettleEvent> newEvents, List<MatchSettleEvent> existingEvents) {
        if (newEvents == null || newEvents.isEmpty()) {
            return newEvents;
        }
        if (existingEvents == null || existingEvents.isEmpty()) {
            return newEvents;
        }
        
        // 创建已存在事件的唯一标识集合，用于快速查找
        Set<String> existingKeys = new HashSet<>();
        for (MatchSettleEvent existing : existingEvents) {
            String key = buildEventKey(existing);
            existingKeys.add(key);
        }
        
        // 过滤掉已存在的事件
        List<MatchSettleEvent> filteredList = new ArrayList<>();
        for (MatchSettleEvent newEvent : newEvents) {
            String key = buildEventKey(newEvent);
            if (!existingKeys.contains(key)) {
                filteredList.add(newEvent);
            }
        }
        return filteredList;
    }

    /**
     * 构建事件的唯一标识key
     */
    private static String buildEventKey(MatchSettleEvent event) {
        if (event == null) {
            return null;
        }
        return String.format("%s_%s_%s_%s_%s_%s",
                event.getStandardMatchId(),
                event.getEventCode() != null ? event.getEventCode() : "",
                event.getSettleNum() != null ? event.getSettleNum() : "",
                event.getPeriodId() != null ? event.getPeriodId() : "",
                event.getEventType() != null ? event.getEventType() : "",
                event.getEventOrder() != null ? event.getEventOrder() : "");
    }

    private static void initfa_cardEvent(List<MatchSettleEvent> list, Long standardMatchId) {
        MatchSettleEvent matchSettleScore1 =initMatchSettleEvent(standardMatchId);
        matchSettleScore1.setEventCode("fa_card");
        matchSettleScore1.setSettleNum("3019");
        matchSettleScore1.setEventName("fa_card");
        matchSettleScore1.setPeriodId(6l);
        matchSettleScore1.setEventType(1);
        matchSettleScore1.setEventOrder(1);
        matchSettleScore1.setHomeAway("none");
        matchSettleScore1.setThirdEventSourceId(matchSettleScore1.getId());
        list.add(matchSettleScore1);

        MatchSettleEvent matchSettleScore11 =initMatchSettleEvent(standardMatchId);
        matchSettleScore11.setEventCode("fa_card");
        matchSettleScore11.setSettleNum("3021");
        matchSettleScore11.setEventName("fa_card");
        matchSettleScore11.setPeriodId(6l);
        matchSettleScore11.setThirdEventSourceId(matchSettleScore1.getId());
        matchSettleScore11.setEventType(2);
        matchSettleScore11.setEventOrder(1);
        matchSettleScore11.setHomeAway("none");
        list.add(matchSettleScore11);

        MatchSettleEvent matchSettleScore2 =initMatchSettleEvent(standardMatchId);
        matchSettleScore2.setEventCode("fa_card");
        matchSettleScore2.setSettleNum("3019");
        matchSettleScore2.setEventName("fa_card");
        matchSettleScore2.setPeriodId(6l);
        matchSettleScore2.setEventType(1);
        matchSettleScore2.setEventOrder(2);
        matchSettleScore2.setThirdEventSourceId(matchSettleScore2.getId());
        matchSettleScore2.setHomeAway("none");
        list.add(matchSettleScore2);

        MatchSettleEvent matchSettleScore22 =initMatchSettleEvent(standardMatchId);
        matchSettleScore22.setEventCode("fa_card");
        matchSettleScore22.setSettleNum("3021");
        matchSettleScore22.setEventName("fa_card");
        matchSettleScore22.setPeriodId(6l);
        matchSettleScore22.setThirdEventSourceId(matchSettleScore2.getId());
        matchSettleScore22.setEventType(2);
        matchSettleScore22.setEventOrder(2);
        matchSettleScore22.setHomeAway("none");
        list.add(matchSettleScore22);

        //下半场
        MatchSettleEvent matchSettleScore3 =initMatchSettleEvent(standardMatchId);
        matchSettleScore3.setEventCode("fa_card");
        matchSettleScore3.setSettleNum("3020");
        matchSettleScore3.setEventName("fa_card");
        matchSettleScore3.setPeriodId(7l);
        matchSettleScore3.setEventType(1);
        matchSettleScore3.setEventOrder(1);
        matchSettleScore3.setHomeAway("none");
        matchSettleScore3.setThirdEventSourceId(matchSettleScore3.getId());
        list.add(matchSettleScore3);

        MatchSettleEvent matchSettleScore31 =initMatchSettleEvent(standardMatchId);
        matchSettleScore31.setEventCode("fa_card");
        matchSettleScore31.setSettleNum("3021");
        matchSettleScore31.setEventName("fa_card");
        matchSettleScore31.setPeriodId(7l);
        matchSettleScore31.setThirdEventSourceId(matchSettleScore3.getId());
        matchSettleScore31.setEventType(2);
        matchSettleScore31.setEventOrder(1);
        matchSettleScore31.setHomeAway("none");
        list.add(matchSettleScore31);

        MatchSettleEvent matchSettleScore4 =initMatchSettleEvent(standardMatchId);
        matchSettleScore4.setEventCode("fa_card");
        matchSettleScore4.setSettleNum("3020");
        matchSettleScore4.setEventName("fa_card");
        matchSettleScore4.setPeriodId(7l);
        matchSettleScore4.setEventType(1);
        matchSettleScore4.setEventOrder(2);
        matchSettleScore4.setThirdEventSourceId(matchSettleScore4.getId());
        matchSettleScore4.setHomeAway("none");
        list.add(matchSettleScore4);

        MatchSettleEvent matchSettleScore42 =initMatchSettleEvent(standardMatchId);
        matchSettleScore42.setEventCode("fa_card");
        matchSettleScore42.setSettleNum("3021");
        matchSettleScore42.setEventName("fa_card");
        matchSettleScore42.setPeriodId(7l);
        matchSettleScore42.setThirdEventSourceId(matchSettleScore4.getId());
        matchSettleScore42.setEventType(2);
        matchSettleScore42.setEventOrder(2);
        matchSettleScore42.setHomeAway("none");
        list.add(matchSettleScore42);
        initExtryfa_cardEvent(list,standardMatchId);
    }
    private static void initExtryfa_cardEvent(List<MatchSettleEvent> list, Long standardMatchId) {
        MatchSettleEvent matchSettleScore1 =initMatchSettleEvent(standardMatchId);
        matchSettleScore1.setEventCode("fa_card");
        matchSettleScore1.setSettleNum("3022");
        matchSettleScore1.setEventName("fa_card");
        matchSettleScore1.setPeriodId(41L);
        matchSettleScore1.setEventType(1);
        matchSettleScore1.setEventOrder(1);
        matchSettleScore1.setHomeAway("none");
        matchSettleScore1.setThirdEventSourceId(matchSettleScore1.getId());
        list.add(matchSettleScore1);

        MatchSettleEvent matchSettleScore11 =initMatchSettleEvent(standardMatchId);
        matchSettleScore11.setEventCode("fa_card");
        matchSettleScore11.setSettleNum("3024");
        matchSettleScore11.setEventName("fa_card");
        matchSettleScore11.setPeriodId(41L);
        matchSettleScore11.setThirdEventSourceId(matchSettleScore1.getId());
        matchSettleScore11.setEventType(2);
        matchSettleScore11.setEventOrder(1);
        matchSettleScore11.setHomeAway("none");
        list.add(matchSettleScore11);

        MatchSettleEvent matchSettleScore2 =initMatchSettleEvent(standardMatchId);
        matchSettleScore2.setEventCode("fa_card");
        matchSettleScore2.setSettleNum("3022");
        matchSettleScore2.setEventName("fa_card");
        matchSettleScore2.setPeriodId(41L);
        matchSettleScore2.setEventType(1);
        matchSettleScore2.setEventOrder(2);
        matchSettleScore2.setThirdEventSourceId(matchSettleScore2.getId());
        matchSettleScore2.setHomeAway("none");
        list.add(matchSettleScore2);

        MatchSettleEvent matchSettleScore22 =initMatchSettleEvent(standardMatchId);
        matchSettleScore22.setEventCode("fa_card");
        matchSettleScore22.setSettleNum("3024");
        matchSettleScore22.setEventName("fa_card");
        matchSettleScore22.setPeriodId(41L);
        matchSettleScore22.setThirdEventSourceId(matchSettleScore2.getId());
        matchSettleScore22.setEventType(2);
        matchSettleScore22.setEventOrder(2);
        matchSettleScore22.setHomeAway("none");
        list.add(matchSettleScore22);

        //下半场
        MatchSettleEvent matchSettleScore3 =initMatchSettleEvent(standardMatchId);
        matchSettleScore3.setEventCode("fa_card");
        matchSettleScore3.setSettleNum("3023");
        matchSettleScore3.setEventName("fa_card");
        matchSettleScore3.setPeriodId(42L);
        matchSettleScore3.setEventType(1);
        matchSettleScore3.setEventOrder(1);
        matchSettleScore3.setHomeAway("none");
        matchSettleScore3.setThirdEventSourceId(matchSettleScore3.getId());
        list.add(matchSettleScore3);

        MatchSettleEvent matchSettleScore31 =initMatchSettleEvent(standardMatchId);
        matchSettleScore31.setEventCode("fa_card");
        matchSettleScore31.setSettleNum("3024");
        matchSettleScore31.setEventName("fa_card");
        matchSettleScore31.setPeriodId(42L);
        matchSettleScore31.setThirdEventSourceId(matchSettleScore3.getId());
        matchSettleScore31.setEventType(2);
        matchSettleScore31.setEventOrder(1);
        matchSettleScore31.setHomeAway("none");
        list.add(matchSettleScore31);

        MatchSettleEvent matchSettleScore4 =initMatchSettleEvent(standardMatchId);
        matchSettleScore4.setEventCode("fa_card");
        matchSettleScore4.setSettleNum("3023");
        matchSettleScore4.setEventName("fa_card");
        matchSettleScore4.setPeriodId(42L);
        matchSettleScore4.setEventType(1);
        matchSettleScore4.setEventOrder(2);
        matchSettleScore4.setThirdEventSourceId(matchSettleScore4.getId());
        matchSettleScore4.setHomeAway("none");
        list.add(matchSettleScore4);

        MatchSettleEvent matchSettleScore42 =initMatchSettleEvent(standardMatchId);
        matchSettleScore42.setEventCode("fa_card");
        matchSettleScore42.setSettleNum("3024");
        matchSettleScore42.setEventName("fa_card");
        matchSettleScore42.setPeriodId(42L);
        matchSettleScore42.setThirdEventSourceId(matchSettleScore4.getId());
        matchSettleScore42.setEventType(2);
        matchSettleScore42.setEventOrder(2);
        matchSettleScore42.setHomeAway("none");
        list.add(matchSettleScore42);
    }

    private static void initCornerEvent(List<MatchSettleEvent> list, Long standardMatchId) {
        MatchSettleEvent matchSettleScore1 =initMatchSettleEvent(standardMatchId);
        matchSettleScore1.setEventCode("corner");
        matchSettleScore1.setSettleNum("204");
        matchSettleScore1.setEventName("corner");
        matchSettleScore1.setPeriodId(6l);
        matchSettleScore1.setEventType(1);
        matchSettleScore1.setEventOrder(1);
        matchSettleScore1.setHomeAway("none");
        matchSettleScore1.setThirdEventSourceId(matchSettleScore1.getId());
        list.add(matchSettleScore1);

        MatchSettleEvent matchSettleScore2 =initMatchSettleEvent(standardMatchId);
        matchSettleScore2.setEventCode("corner");
        matchSettleScore2.setSettleNum("204");
        matchSettleScore2.setEventName("corner");
        matchSettleScore2.setPeriodId(6l);
        matchSettleScore2.setEventType(1);
        matchSettleScore2.setEventOrder(2);
        matchSettleScore2.setThirdEventSourceId(matchSettleScore2.getId());
        matchSettleScore2.setHomeAway("none");
        list.add(matchSettleScore2);

        MatchSettleEvent matchSettleScore3 =initMatchSettleEvent(standardMatchId);
        matchSettleScore3.setEventCode("corner");
        matchSettleScore3.setSettleNum("204");
        matchSettleScore3.setEventName("corner");
        matchSettleScore3.setPeriodId(6l);
        matchSettleScore3.setEventType(1);
        matchSettleScore3.setEventOrder(3);
        matchSettleScore3.setThirdEventSourceId(matchSettleScore3.getId());
        matchSettleScore3.setHomeAway("none");
        list.add(matchSettleScore3);

        MatchSettleEvent matchSettleScore4 =initMatchSettleEvent(standardMatchId);
        matchSettleScore4.setEventCode("corner");
        matchSettleScore4.setSettleNum("204");
        matchSettleScore4.setEventName("corner");
        matchSettleScore4.setPeriodId(6l);
        matchSettleScore4.setEventType(1);
        matchSettleScore4.setEventOrder(4);
        matchSettleScore4.setThirdEventSourceId(matchSettleScore4.getId());
        matchSettleScore4.setHomeAway("none");
        list.add(matchSettleScore4);

        MatchSettleEvent matchSettleScore5 =initMatchSettleEvent(standardMatchId);
        matchSettleScore5.setEventCode("corner");
        matchSettleScore5.setSettleNum("204");
        matchSettleScore5.setEventName("corner");
        matchSettleScore5.setPeriodId(6l);
        matchSettleScore5.setEventType(1);
        matchSettleScore5.setEventOrder(5);
        matchSettleScore5.setThirdEventSourceId(matchSettleScore5.getId());
        matchSettleScore5.setHomeAway("none");
        list.add(matchSettleScore5);
        //下半场
        MatchSettleEvent matchSettleScore11 =initMatchSettleEvent(standardMatchId);
        matchSettleScore11.setEventCode("corner");
        matchSettleScore11.setSettleNum("205");
        matchSettleScore11.setEventName("corner");
        matchSettleScore11.setPeriodId(7l);
        matchSettleScore11.setEventType(1);
        matchSettleScore11.setEventOrder(1);
        matchSettleScore11.setHomeAway("none");
        matchSettleScore11.setThirdEventSourceId(matchSettleScore11.getId());
        list.add(matchSettleScore11);

        MatchSettleEvent matchSettleScore21 =initMatchSettleEvent(standardMatchId);
        matchSettleScore21.setEventCode("corner");
        matchSettleScore21.setSettleNum("205");
        matchSettleScore21.setEventName("corner");
        matchSettleScore21.setPeriodId(7l);
        matchSettleScore21.setEventType(1);
        matchSettleScore21.setEventOrder(2);
        matchSettleScore21.setThirdEventSourceId(matchSettleScore21.getId());
        matchSettleScore21.setHomeAway("none");
        list.add(matchSettleScore21);

        MatchSettleEvent matchSettleScore31 =initMatchSettleEvent(standardMatchId);
        matchSettleScore31.setEventCode("corner");
        matchSettleScore31.setSettleNum("205");
        matchSettleScore31.setEventName("corner");
        matchSettleScore31.setPeriodId(7l);
        matchSettleScore31.setEventType(1);
        matchSettleScore31.setEventOrder(3);
        matchSettleScore31.setThirdEventSourceId(matchSettleScore31.getId());
        matchSettleScore31.setHomeAway("none");
        list.add(matchSettleScore31);

        MatchSettleEvent matchSettleScore41 =initMatchSettleEvent(standardMatchId);
        matchSettleScore41.setEventCode("corner");
        matchSettleScore41.setSettleNum("205");
        matchSettleScore41.setEventName("corner");
        matchSettleScore41.setPeriodId(7l);
        matchSettleScore41.setEventType(1);
        matchSettleScore41.setEventOrder(4);
        matchSettleScore41.setThirdEventSourceId(matchSettleScore41.getId());
        matchSettleScore41.setHomeAway("none");
        list.add(matchSettleScore41);

        MatchSettleEvent matchSettleScore51 =initMatchSettleEvent(standardMatchId);
        matchSettleScore51.setEventCode("corner");
        matchSettleScore51.setSettleNum("205");
        matchSettleScore51.setEventName("corner");
        matchSettleScore51.setPeriodId(7l);
        matchSettleScore51.setEventType(1);
        matchSettleScore51.setEventOrder(5);
        matchSettleScore51.setThirdEventSourceId(matchSettleScore51.getId());
        matchSettleScore51.setHomeAway("none");
        list.add(matchSettleScore51);
        //加时赛上半场
        MatchSettleEvent matchSettleScore111 =initMatchSettleEvent(standardMatchId);
        matchSettleScore111.setEventCode("corner");
        matchSettleScore111.setSettleNum("209");
        matchSettleScore111.setEventName("corner");
        matchSettleScore111.setPeriodId(41l);
        matchSettleScore111.setEventType(1);
        matchSettleScore111.setEventOrder(1);
        matchSettleScore111.setHomeAway("none");
        matchSettleScore111.setThirdEventSourceId(matchSettleScore111.getId());
        list.add(matchSettleScore111);

        MatchSettleEvent matchSettleScore211 =initMatchSettleEvent(standardMatchId);
        matchSettleScore211.setEventCode("corner");
        matchSettleScore211.setSettleNum("209");
        matchSettleScore211.setEventName("corner");
        matchSettleScore211.setPeriodId(41l);
        matchSettleScore211.setEventType(1);
        matchSettleScore211.setEventOrder(2);
        matchSettleScore211.setThirdEventSourceId(matchSettleScore211.getId());
        matchSettleScore211.setHomeAway("none");
        list.add(matchSettleScore211);

        MatchSettleEvent matchSettleScore311 =initMatchSettleEvent(standardMatchId);
        matchSettleScore311.setEventCode("corner");
        matchSettleScore311.setSettleNum("209");
        matchSettleScore311.setEventName("corner");
        matchSettleScore311.setPeriodId(41l);
        matchSettleScore311.setEventType(1);
        matchSettleScore311.setEventOrder(3);
        matchSettleScore311.setThirdEventSourceId(matchSettleScore311.getId());
        matchSettleScore311.setHomeAway("none");
        list.add(matchSettleScore311);

        MatchSettleEvent matchSettleScore411 =initMatchSettleEvent(standardMatchId);
        matchSettleScore411.setEventCode("corner");
        matchSettleScore411.setSettleNum("209");
        matchSettleScore411.setEventName("corner");
        matchSettleScore411.setPeriodId(41l);
        matchSettleScore411.setEventType(1);
        matchSettleScore411.setEventOrder(4);
        matchSettleScore411.setThirdEventSourceId(matchSettleScore411.getId());
        matchSettleScore411.setHomeAway("none");
        list.add(matchSettleScore411);

        MatchSettleEvent matchSettleScore511 =initMatchSettleEvent(standardMatchId);
        matchSettleScore511.setEventCode("corner");
        matchSettleScore511.setSettleNum("209");
        matchSettleScore511.setEventName("corner");
        matchSettleScore511.setPeriodId(41l);
        matchSettleScore511.setEventType(1);
        matchSettleScore511.setEventOrder(5);
        matchSettleScore511.setThirdEventSourceId(matchSettleScore511.getId());
        matchSettleScore511.setHomeAway("none");
        list.add(matchSettleScore511);
        //加时赛下半场
        MatchSettleEvent matchSettleScore1111 =initMatchSettleEvent(standardMatchId);
        matchSettleScore1111.setEventCode("corner");
        matchSettleScore1111.setSettleNum("2010");
        matchSettleScore1111.setEventName("corner");
        matchSettleScore1111.setPeriodId(42l);
        matchSettleScore1111.setEventType(1);
        matchSettleScore1111.setEventOrder(1);
        matchSettleScore1111.setHomeAway("none");
        matchSettleScore1111.setThirdEventSourceId(matchSettleScore1111.getId());
        list.add(matchSettleScore1111);

        MatchSettleEvent matchSettleScore2111 =initMatchSettleEvent(standardMatchId);
        matchSettleScore2111.setEventCode("corner");
        matchSettleScore2111.setSettleNum("2010");
        matchSettleScore2111.setEventName("corner");
        matchSettleScore2111.setPeriodId(42l);
        matchSettleScore2111.setEventType(1);
        matchSettleScore2111.setEventOrder(2);
        matchSettleScore2111.setThirdEventSourceId(matchSettleScore2111.getId());
        matchSettleScore2111.setHomeAway("none");
        list.add(matchSettleScore2111);

        MatchSettleEvent matchSettleScore3111 =initMatchSettleEvent(standardMatchId);
        matchSettleScore3111.setEventCode("corner");
        matchSettleScore3111.setSettleNum("2010");
        matchSettleScore3111.setEventName("corner");
        matchSettleScore3111.setPeriodId(42l);
        matchSettleScore3111.setEventType(1);
        matchSettleScore3111.setEventOrder(3);
        matchSettleScore3111.setThirdEventSourceId(matchSettleScore3111.getId());
        matchSettleScore3111.setHomeAway("none");
        list.add(matchSettleScore3111);

        MatchSettleEvent matchSettleScore4111 =initMatchSettleEvent(standardMatchId);
        matchSettleScore4111.setEventCode("corner");
        matchSettleScore4111.setSettleNum("2010");
        matchSettleScore4111.setEventName("corner");
        matchSettleScore4111.setPeriodId(42l);
        matchSettleScore4111.setEventType(1);
        matchSettleScore4111.setEventOrder(4);
        matchSettleScore4111.setThirdEventSourceId(matchSettleScore4111.getId());
        matchSettleScore4111.setHomeAway("none");
        list.add(matchSettleScore4111);

        MatchSettleEvent matchSettleScore5111 =initMatchSettleEvent(standardMatchId);
        matchSettleScore5111.setEventCode("corner");
        matchSettleScore5111.setSettleNum("2010");
        matchSettleScore5111.setEventName("corner");
        matchSettleScore5111.setPeriodId(42l);
        matchSettleScore5111.setEventType(1);
        matchSettleScore5111.setEventOrder(5);
        matchSettleScore5111.setThirdEventSourceId(matchSettleScore5111.getId());
        matchSettleScore5111.setHomeAway("none");
        list.add(matchSettleScore5111);
    }

    private static void initGoalEvent(List<MatchSettleEvent> list, Long standardMatchId) {
        MatchSettleEvent matchSettleScore1 =initMatchSettleEvent(standardMatchId);
        matchSettleScore1.setEventCode("goal");
        matchSettleScore1.setSettleNum("1022");
        matchSettleScore1.setEventName("goal");
        matchSettleScore1.setPeriodId(6l);
        matchSettleScore1.setEventType(1);
        matchSettleScore1.setEventOrder(1);
        matchSettleScore1.setHomeAway("no goal");
        matchSettleScore1.setThirdEventSourceId(matchSettleScore1.getId());
        list.add(matchSettleScore1);

        MatchSettleEvent matchSettleScore11 =initMatchSettleEvent(standardMatchId);
        matchSettleScore11.setEventCode("goal");
        matchSettleScore11.setSettleNum("1024");
        matchSettleScore11.setEventName("goal");
        matchSettleScore11.setPeriodId(6l);
        matchSettleScore11.setThirdEventSourceId(matchSettleScore1.getId());
        matchSettleScore11.setEventType(2);
        matchSettleScore11.setEventOrder(1);
        matchSettleScore11.setHomeAway("no goal");
        list.add(matchSettleScore11);

        MatchSettleEvent matchSettleScore2 =initMatchSettleEvent(standardMatchId);
        matchSettleScore2.setEventCode("goal");
        matchSettleScore2.setSettleNum("1022");
        matchSettleScore2.setEventName("goal");
        matchSettleScore2.setPeriodId(6l);
        matchSettleScore2.setEventType(1);
        matchSettleScore2.setEventOrder(2);
        matchSettleScore2.setHomeAway("no goal");
        matchSettleScore2.setThirdEventSourceId(matchSettleScore2.getId());
        list.add(matchSettleScore2);

        MatchSettleEvent matchSettleScore21 =initMatchSettleEvent(standardMatchId);
        matchSettleScore21.setEventCode("goal");
        matchSettleScore21.setSettleNum("1024");
        matchSettleScore21.setEventName("goal");
        matchSettleScore21.setPeriodId(6l);
        matchSettleScore21.setThirdEventSourceId(matchSettleScore2.getId());
        matchSettleScore21.setEventType(2);
        matchSettleScore21.setEventOrder(2);
        matchSettleScore21.setHomeAway("no goal");
        list.add(matchSettleScore21);
        //下半场
        MatchSettleEvent matchSettleScore3 =initMatchSettleEvent(standardMatchId);
        matchSettleScore3.setEventCode("goal");
        matchSettleScore3.setSettleNum("1023");
        matchSettleScore3.setEventName("goal");
        matchSettleScore3.setPeriodId(7l);
        matchSettleScore3.setEventType(1);
        matchSettleScore3.setEventOrder(1);
        matchSettleScore3.setHomeAway("no goal");
        matchSettleScore3.setThirdEventSourceId(matchSettleScore3.getId());
        list.add(matchSettleScore3);

        MatchSettleEvent matchSettleScore31 =initMatchSettleEvent(standardMatchId);
        matchSettleScore31.setEventCode("goal");
        matchSettleScore31.setSettleNum("1024");
        matchSettleScore31.setEventName("goal");
        matchSettleScore31.setPeriodId(7l);
        matchSettleScore31.setThirdEventSourceId(matchSettleScore3.getId());
        matchSettleScore31.setEventType(2);
        matchSettleScore31.setEventOrder(1);
        matchSettleScore31.setHomeAway("no goal");
        list.add(matchSettleScore31);

        MatchSettleEvent matchSettleScore4 =initMatchSettleEvent(standardMatchId);
        matchSettleScore4.setEventCode("goal");
        matchSettleScore4.setSettleNum("1023");
        matchSettleScore4.setEventName("goal");
        matchSettleScore4.setPeriodId(7l);
        matchSettleScore4.setEventType(1);
        matchSettleScore4.setEventOrder(2);
        matchSettleScore4.setHomeAway("no goal");
        matchSettleScore4.setThirdEventSourceId(matchSettleScore4.getId());
        list.add(matchSettleScore4);

        MatchSettleEvent matchSettleScore41 =initMatchSettleEvent(standardMatchId);
        matchSettleScore41.setEventCode("goal");
        matchSettleScore41.setSettleNum("1024");
        matchSettleScore41.setEventName("goal");
        matchSettleScore41.setPeriodId(7l);
        matchSettleScore41.setThirdEventSourceId(matchSettleScore4.getId());
        matchSettleScore41.setEventType(2);
        matchSettleScore41.setEventOrder(2);
        matchSettleScore41.setHomeAway("no goal");
        list.add(matchSettleScore41);
        initGoalExtryEvent(list,standardMatchId);
    }
    /**
     * 加时赛自动新增事件
     * */
    private static void initGoalExtryEvent(List<MatchSettleEvent> list, Long standardMatchId) {
        MatchSettleEvent matchSettleScore1 =initMatchSettleEvent(standardMatchId);
        matchSettleScore1.setEventCode("goal");
        matchSettleScore1.setSettleNum("1025");
        matchSettleScore1.setEventName("goal");
        matchSettleScore1.setPeriodId(41l);
        matchSettleScore1.setEventType(1);
        matchSettleScore1.setEventOrder(1);
        matchSettleScore1.setHomeAway("no goal");
        matchSettleScore1.setThirdEventSourceId(matchSettleScore1.getId());
        list.add(matchSettleScore1);

        MatchSettleEvent matchSettleScore11 =initMatchSettleEvent(standardMatchId);
        matchSettleScore11.setEventCode("goal");
        matchSettleScore11.setSettleNum("1027");
        matchSettleScore11.setEventName("goal");
        matchSettleScore11.setPeriodId(41l);
        matchSettleScore11.setThirdEventSourceId(matchSettleScore1.getId());
        matchSettleScore11.setEventType(2);
        matchSettleScore11.setEventOrder(1);
        matchSettleScore11.setHomeAway("no goal");
        list.add(matchSettleScore11);

        MatchSettleEvent matchSettleScore2 =initMatchSettleEvent(standardMatchId);
        matchSettleScore2.setEventCode("goal");
        matchSettleScore2.setSettleNum("1025");
        matchSettleScore2.setEventName("goal");
        matchSettleScore2.setPeriodId(41L);
        matchSettleScore2.setEventType(1);
        matchSettleScore2.setEventOrder(2);
        matchSettleScore2.setHomeAway("no goal");
        matchSettleScore2.setThirdEventSourceId(matchSettleScore2.getId());
        list.add(matchSettleScore2);

        MatchSettleEvent matchSettleScore21 =initMatchSettleEvent(standardMatchId);
        matchSettleScore21.setEventCode("goal");
        matchSettleScore21.setSettleNum("1027");
        matchSettleScore21.setEventName("goal");
        matchSettleScore21.setPeriodId(41L);
        matchSettleScore21.setThirdEventSourceId(matchSettleScore2.getId());
        matchSettleScore21.setEventType(2);
        matchSettleScore21.setEventOrder(2);
        matchSettleScore21.setHomeAway("no goal");
        list.add(matchSettleScore21);
        //下半场
        MatchSettleEvent matchSettleScore3 =initMatchSettleEvent(standardMatchId);
        matchSettleScore3.setEventCode("goal");
        matchSettleScore3.setSettleNum("1026");
        matchSettleScore3.setEventName("goal");
        matchSettleScore3.setPeriodId(42l);
        matchSettleScore3.setEventType(1);
        matchSettleScore3.setEventOrder(1);
        matchSettleScore3.setHomeAway("no goal");
        matchSettleScore3.setThirdEventSourceId(matchSettleScore3.getId());
        list.add(matchSettleScore3);

        MatchSettleEvent matchSettleScore31 =initMatchSettleEvent(standardMatchId);
        matchSettleScore31.setEventCode("goal");
        matchSettleScore31.setSettleNum("1027");
        matchSettleScore31.setEventName("goal");
        matchSettleScore31.setPeriodId(42l);
        matchSettleScore31.setThirdEventSourceId(matchSettleScore3.getId());
        matchSettleScore31.setEventType(2);
        matchSettleScore31.setEventOrder(1);
        matchSettleScore31.setHomeAway("no goal");
        list.add(matchSettleScore31);

        MatchSettleEvent matchSettleScore4 =initMatchSettleEvent(standardMatchId);
        matchSettleScore4.setEventCode("goal");
        matchSettleScore4.setSettleNum("1026");
        matchSettleScore4.setEventName("goal");
        matchSettleScore4.setPeriodId(42l);
        matchSettleScore4.setEventType(1);
        matchSettleScore4.setEventOrder(2);
        matchSettleScore4.setHomeAway("no goal");
        matchSettleScore4.setThirdEventSourceId(matchSettleScore4.getId());
        list.add(matchSettleScore4);

        MatchSettleEvent matchSettleScore41 =initMatchSettleEvent(standardMatchId);
        matchSettleScore41.setEventCode("goal");
        matchSettleScore41.setSettleNum("1027");
        matchSettleScore41.setEventName("goal");
        matchSettleScore41.setPeriodId(42l);
        matchSettleScore41.setThirdEventSourceId(matchSettleScore4.getId());
        matchSettleScore41.setEventType(2);
        matchSettleScore41.setEventOrder(2);
        matchSettleScore41.setHomeAway("no goal");
        list.add(matchSettleScore41);
    }

    /**
     * 灰色区间+延迟时间
     * @param matchSettleScore
     * @param second
     * @param grayAreaSettleDto
     * @return
     */
    public static boolean delaySettleSeconds(MatchSettleScore matchSettleScore, Long second, GrayAreaSettleDto grayAreaSettleDto) {
        //角球需要延迟结算 2分钟
        if(matchSettleScore.getEventCode().equals("corner")){
            Integer cornerGrayTime = grayAreaSettleDto==null || grayAreaSettleDto.getCorner15Min()==null?0:grayAreaSettleDto.getCorner15Min();
//            Integer cornerGrayTime = 0;
            if(matchSettleScore.getSettleNum().equals("2011")){
                if(second>899+cornerGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("2012")){
                if(second>1799+cornerGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("2013")){
                return true;
            }
            if(matchSettleScore.getSettleNum().equals("2014")){
                if(second>3599+cornerGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("2015")){
                if(second>4499+cornerGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("2016")){
                return true;
            }
        }else if (matchSettleScore.getEventCode().equals("fa_card")||matchSettleScore.getEventCode().equals("yellow_card")||matchSettleScore.getEventCode().equals("red_card")){
            Integer bookingGrayTime = grayAreaSettleDto==null || grayAreaSettleDto.getBooking15Min()==null?0:grayAreaSettleDto.getBooking15Min();
//            Integer bookingGrayTime = 0;
            if(matchSettleScore.getSettleNum().equals("301")){
                if(second>899+bookingGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("302")){
                if(second>1799+bookingGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("303")){
                return true;
            }
            if(matchSettleScore.getSettleNum().equals("305")){
                if(second>3599+bookingGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("306")){
                if(second>4499+bookingGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("307")){
                return true;
            }
        } else if(CommonConstant.BASKETBALL_SCORE_EVENT_CODE.equals(matchSettleScore.getEventCode())) {
            Integer goalGrayTime = grayAreaSettleDto==null || grayAreaSettleDto.getGoal6Min()==null?30:grayAreaSettleDto.getGoal6Min();
            if(basketballGrayDelayNum.contains(matchSettleScore.getSettleNum())){
                if(second<=360-goalGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
        } else {
            Integer goal15MinGrayTime = grayAreaSettleDto==null || grayAreaSettleDto.getGoal15Min()==null?0:grayAreaSettleDto.getGoal15Min();
//            Integer goal15MinGrayTime = 0;
            if(matchSettleScore.getSettleNum().equals("102")){
                if(second>899+goal15MinGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("103")){
                if(second>1799+goal15MinGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("104")){
                return true;
            }
            if(matchSettleScore.getSettleNum().equals("106")){
                if(second>3599+goal15MinGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("107")){
                if(second>4499+goal15MinGrayTime){
                    return true;
                }else {
                    return false;
                }
            }
            if(matchSettleScore.getSettleNum().equals("108")){
                return true;
            }
            if(!up5Seconds(matchSettleScore,second,grayAreaSettleDto)){
                return false;
            }
            return true;
        }

        return true;

    }

    public static Long getDelaySettleSeconds(MatchSettleScore matchSettleScore, Long second) {
        //角球需要延迟结算 2分钟
        if (matchSettleScore.getEventCode().equals("corner")) {
            if (matchSettleScore.getSettleNum().equals("2011")) {
                return second - 900;
            }
            if (matchSettleScore.getSettleNum().equals("2012")) {
                return second - 1800;
            }
            if (matchSettleScore.getSettleNum().equals("2013")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("201")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("2014")) {
                return second - 3600;
            }
            if (matchSettleScore.getSettleNum().equals("2015")) {
                return second - 4500;
            }
            if (matchSettleScore.getSettleNum().equals("2016")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("202")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("203")) {
                return 0l;
            }
        } else if (matchSettleScore.getEventCode().equals("fa_card") || matchSettleScore.getEventCode().equals("yellow_card") || matchSettleScore.getEventCode().equals("red_card")) {
            if (matchSettleScore.getSettleNum().equals("301")) {
                return second - 900;
            }
            if (matchSettleScore.getSettleNum().equals("302")) {
                return second - 1800;
            }
            if (matchSettleScore.getSettleNum().equals("303")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("304")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("305")) {
                return second - 3600;
            }
            if (matchSettleScore.getSettleNum().equals("306")) {
                return second - 4500;
            }
            if (matchSettleScore.getSettleNum().equals("307")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("308")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("309")) {
                return 0l;
            }
        } else {
            if (matchSettleScore.getSettleNum().equals("102")) {
                return second - 900;
            }
            if (matchSettleScore.getSettleNum().equals("103")) {
                return second - 1800;
            }
            if (matchSettleScore.getSettleNum().equals("104")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("105")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("106")) {
                return second - 3600;
            }
            if (matchSettleScore.getSettleNum().equals("107")) {
                return second - 4500;
            }
            if (matchSettleScore.getSettleNum().equals("108")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("109")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("1010")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("1034")) {
                return second - 300;
            }
            if (matchSettleScore.getSettleNum().equals("1035")) {
                return second - 600;
            }
            if (matchSettleScore.getSettleNum().equals("1036")) {
                return second - 900;
            }
            if (matchSettleScore.getSettleNum().equals("1037")) {
                return second - 1200;
            }
            if (matchSettleScore.getSettleNum().equals("1038")) {
                return second - 1500;
            }
            if (matchSettleScore.getSettleNum().equals("1039")) {
                return second - 1800;
            }
            if (matchSettleScore.getSettleNum().equals("1040")) {
                return second - 2100;
            }
            if (matchSettleScore.getSettleNum().equals("1041")) {
                return second - 2400;
            }
            //上半场最后一个五分钟阶段跟绝杀球
            if (matchSettleScore.getSettleNum().equals("1042")) {
                return second - 2700;
            }
            if (matchSettleScore.getSettleNum().equals("1043")) {
                return 0l;
            }
            if (matchSettleScore.getSettleNum().equals("1044")) {
                return second - 3000;
            }
            if (matchSettleScore.getSettleNum().equals("1045")) {
                return second - 3300;
            }
            if (matchSettleScore.getSettleNum().equals("1046")) {
                return second - 3600;
            }
            if (matchSettleScore.getSettleNum().equals("1047")) {
                return second - 3900;
            }
            if (matchSettleScore.getSettleNum().equals("1048")) {
                return second - 4200;
            }
            if (matchSettleScore.getSettleNum().equals("1049")) {
                return second - 4500;
            }
            if (matchSettleScore.getSettleNum().equals("1050")) {
                return second - 4800;
            }
            if (matchSettleScore.getSettleNum().equals("1051")) {
                return second - 5100;
            }
            //下半场最后一个五分钟阶段跟绝杀球
            if (matchSettleScore.getSettleNum().equals("1052")) {
                return second - 5400;
            }
            if (matchSettleScore.getSettleNum().equals("1053")) {
                return 0l;
            }
        }
        return 0l;
    }

    public static void initFiveMinGoalScore(List<MatchSettleScore> list,Long standardMatchId) {
        MatchSettleScore matchSettleScore1 =initMatchSettleScore(standardMatchId);
        matchSettleScore1.setEventCode("goal");
        matchSettleScore1.setSettleNum("1034");
        matchSettleScore1.setEventName("0:00 - 4:59");
        matchSettleScore1.setPeriodId(6l);
        list.add(matchSettleScore1);
        MatchSettleScore matchSettleScore2 =initMatchSettleScore(standardMatchId);
        matchSettleScore2.setEventCode("goal");
        matchSettleScore2.setSettleNum("1035");
        matchSettleScore2.setEventName("5:00 - 9:59");
        matchSettleScore2.setPeriodId(6l);
        list.add(matchSettleScore2);
        MatchSettleScore matchSettleScore3 =initMatchSettleScore(standardMatchId);
        matchSettleScore3.setEventCode("goal");
        matchSettleScore3.setSettleNum("1036");
        matchSettleScore3.setEventName("10:00 - 14:59");
        matchSettleScore3.setPeriodId(6l);
        list.add(matchSettleScore3);
        MatchSettleScore matchSettleScore4 =initMatchSettleScore(standardMatchId);
        matchSettleScore4.setEventCode("goal");
        matchSettleScore4.setSettleNum("1037");
        matchSettleScore4.setEventName("15:00 - 19:59");
        matchSettleScore4.setPeriodId(6l);
        list.add(matchSettleScore4);
        MatchSettleScore matchSettleScore5 =initMatchSettleScore(standardMatchId);
        matchSettleScore5.setEventCode("goal");
        matchSettleScore5.setSettleNum("1038");
        matchSettleScore5.setEventName("20:00 - 24:59");
        matchSettleScore5.setPeriodId(6l);
        list.add(matchSettleScore5);
        MatchSettleScore matchSettleScore6 =initMatchSettleScore(standardMatchId);
        matchSettleScore6.setEventCode("goal");
        matchSettleScore6.setSettleNum("1039");
        matchSettleScore6.setEventName("25:00 - 29:59");
        matchSettleScore6.setPeriodId(6l);
        list.add(matchSettleScore6);
        MatchSettleScore matchSettleScore7 =initMatchSettleScore(standardMatchId);
        matchSettleScore7.setEventCode("goal");
        matchSettleScore7.setSettleNum("1040");
        matchSettleScore7.setEventName("30:00 - 34:59");
        matchSettleScore7.setPeriodId(6l);
        list.add(matchSettleScore7);
        MatchSettleScore matchSettleScore8 =initMatchSettleScore(standardMatchId);
        matchSettleScore8.setEventCode("goal");
        matchSettleScore8.setSettleNum("1041");
        matchSettleScore8.setEventName("35:00 - 39:59");
        matchSettleScore8.setPeriodId(6l);
        list.add(matchSettleScore8);
        MatchSettleScore matchSettleScore9 =initMatchSettleScore(standardMatchId);
        matchSettleScore9.setEventCode("goal");
        matchSettleScore9.setSettleNum("1042");
        matchSettleScore9.setEventName("40:00 - 45:00 ");
        matchSettleScore9.setPeriodId(6l);
        list.add(matchSettleScore9);
        MatchSettleScore matchSettleScore10 =initMatchSettleScore(standardMatchId);
        matchSettleScore10.setEventCode("goal");
        matchSettleScore10.setSettleNum("1043");
        matchSettleScore10.setEventName("1H Last-minute Goal");
        matchSettleScore10.setPeriodId(6l);
        list.add(matchSettleScore10);
        MatchSettleScore matchSettleScore11 =initMatchSettleScore(standardMatchId);
        matchSettleScore11.setEventCode("goal");
        matchSettleScore11.setSettleNum("1044");
        matchSettleScore11.setEventName("2H - 49:59");
        matchSettleScore11.setPeriodId(7l);
        list.add(matchSettleScore11);
        MatchSettleScore matchSettleScore12 =initMatchSettleScore(standardMatchId);
        matchSettleScore12.setEventCode("goal");
        matchSettleScore12.setSettleNum("1045");
        matchSettleScore12.setEventName("50:00 - 54:59");
        matchSettleScore12.setPeriodId(7l);
        list.add(matchSettleScore12);
        MatchSettleScore matchSettleScore13 =initMatchSettleScore(standardMatchId);
        matchSettleScore13.setEventCode("goal");
        matchSettleScore13.setSettleNum("1046");
        matchSettleScore13.setEventName("55:00 - 59:59");
        matchSettleScore13.setPeriodId(7l);
        list.add(matchSettleScore13);
        MatchSettleScore matchSettleScore14 =initMatchSettleScore(standardMatchId);
        matchSettleScore14.setEventCode("goal");
        matchSettleScore14.setSettleNum("1047");
        matchSettleScore14.setEventName("60:00 - 64:59");
        matchSettleScore14.setPeriodId(7l);
        list.add(matchSettleScore14);
        MatchSettleScore matchSettleScore15 =initMatchSettleScore(standardMatchId);
        matchSettleScore15.setEventCode("goal");
        matchSettleScore15.setSettleNum("1048");
        matchSettleScore15.setEventName("65:00 - 69:59");
        matchSettleScore15.setPeriodId(7l);
        list.add(matchSettleScore15);
        MatchSettleScore matchSettleScore16 =initMatchSettleScore(standardMatchId);
        matchSettleScore16.setEventCode("goal");
        matchSettleScore16.setSettleNum("1049");
        matchSettleScore16.setEventName("70:00 - 74:59");
        matchSettleScore16.setPeriodId(7l);
        list.add(matchSettleScore16);
        MatchSettleScore matchSettleScore17 =initMatchSettleScore(standardMatchId);
        matchSettleScore17.setEventCode("goal");
        matchSettleScore17.setSettleNum("1050");
        matchSettleScore17.setEventName("75:00 - 79:59");
        matchSettleScore17.setPeriodId(7l);
        list.add(matchSettleScore17);
        MatchSettleScore matchSettleScore18 =initMatchSettleScore(standardMatchId);
        matchSettleScore18.setEventCode("goal");
        matchSettleScore18.setSettleNum("1051");
        matchSettleScore18.setEventName("80:00 - 84:59");
        matchSettleScore18.setPeriodId(7l);
        list.add(matchSettleScore18);
        MatchSettleScore matchSettleScore19 =initMatchSettleScore(standardMatchId);
        matchSettleScore19.setEventCode("goal");
        matchSettleScore19.setSettleNum("1052");
        matchSettleScore19.setEventName("85:00 - 90:00");
        matchSettleScore19.setPeriodId(7l);
        list.add(matchSettleScore19);
        MatchSettleScore matchSettleScore20 =initMatchSettleScore(standardMatchId);
        matchSettleScore20.setEventCode("goal");
        matchSettleScore20.setSettleNum("1053");
        matchSettleScore20.setEventName("2H Last-minute Goal");
        matchSettleScore20.setPeriodId(7l);
        list.add(matchSettleScore20);

    }

    public static boolean up5Seconds(MatchSettleScore matchSettleScore, Long second,GrayAreaSettleDto grayAreaSettleDto) {

        Integer goal5MinGrayTime = grayAreaSettleDto == null || grayAreaSettleDto.getGoal5Min()==null?5:grayAreaSettleDto.getGoal5Min();
//        Integer goal5MinGrayTime = 0;
        if(matchSettleScore.getSettleNum().equals("1034")){
            if(second>299+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1035")){
            if(second>599+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1036")){
            if(second>899+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1037")){
            if(second>1199+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1038")){
            if(second>1499+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1039")){
            if(second>1799+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1040")){
            if(second>2099+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1041")){
            if(second>2399+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        /*if(matchSettleScore.getSettleNum().equals("1042")){
            if(second>2699+5){
                return true;
            }else {
                return false;
            }
        }*/
        //上半场最后一个五分钟阶段跟绝杀球
        if(matchSettleScore.getSettleNum().equals("1042") || matchSettleScore.getSettleNum().equals("1043")){
            return true;
        }
        if(matchSettleScore.getSettleNum().equals("1044")){
            if(second>2999+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1045")){
            if(second>3299+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1046")){
            if(second>3599+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1047")){
            if(second>3899+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1048")){
            if(second>4199+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1049")){
            if(second>4499+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1050")){
            if(second>4799+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        if(matchSettleScore.getSettleNum().equals("1051")){
            if(second>5099+goal5MinGrayTime){
                return true;
            }else {
                return false;
            }
        }
        /*if(matchSettleScore.getSettleNum().equals("1052")){
            if(second>5399+5){
                return true;
            }else {
                return false;
            }
        }*/
        //下半场最后一个五分钟阶段跟绝杀球
        if(matchSettleScore.getSettleNum().equals("1052") || matchSettleScore.getSettleNum().equals("1053")){
            return true;
        }
        return true;
    }


    //5分钟 阶段计算
    public static Long get5MinPeriod(Long period,Long secondStart){
        //开场-4:59
        if(period==6&&secondStart<60*5){
            return 5L;
        }
        //5:00 - 9:59
        if(period==6&&secondStart>=60*5&&secondStart<60*10){
            return 10L;
        }
        //10:00 - 14:59
        if(period==6&&secondStart>=60*10&&secondStart<60*15){
            return 15L;
        }
        //15:00 - 19:59
        if(period==6&&secondStart>=60*15&&secondStart<60*20){
            return 20L;
        }
        //20:00 - 24:59
        if(period==6&&secondStart>=60*20&&secondStart<60*25){
            return 25L;
        }
        //25:00 - 29:59
        if(period==6&&secondStart>=60*25&&secondStart<60*30){
            return 30L;
        }
        //30:00 - 34:59
        if(period==6&&secondStart>=60*30&&secondStart<60*35){
            return 35L;
        }
        //35:00 - 39:59
        if(period==6&&secondStart>=60*35&&secondStart<60*40){
            return 40L;
        }
        //40:00 - 45:00
        if(period==6&&secondStart>=60*40&&secondStart<60*45){
            return 45L;
        }
        //1H Last-minute Goal
        if(period==6&&secondStart>60*45){
            return 49L;
        }

        //下半场- 49:59
        if(period==7&&secondStart<60*50){
            return 50L;
        }
        //50:00 - 54:59
        if(period==7&&secondStart>=60*50&&secondStart<60*55){
            return 55L;
        }
        //55:00 - 59:59
        if(period==7&&secondStart>=60*55&&secondStart<60*60){
            return 60L;
        }
        //60:00 - 64:59
        if(period==7&&secondStart>=60*60&&secondStart<60*65){
            return 65L;
        }
        //65:00 - 69:59
        if(period==7&&secondStart>=60*65&&secondStart<60*70){
            return 70L;
        }
        //70:00 - 74:59
        if(period==7&&secondStart>=60*70&&secondStart<60*75){
            return 75L;
        }
        //75:00 - 79:59
        if(period==7&&secondStart>=60*75&&secondStart<60*80){
            return 80L;
        }
        //80:00 - 84:59
        if(period==7&&secondStart>=60*80&&secondStart<60*85){
            return 85L;
        }
        //85:00 - 90:00
        if(period==7&&secondStart>=60*85&&secondStart<60*90){
            return 90L;
        }
        //2H Last-minute Goal
        if(period==7&&secondStart>60*90){
            return 99L;
        }
        return null;
    }
    public static MatchDelaySettleInfo initMatchDelaySettleInfo(MatchSettleScore score, MatchSettleCheckInfo checkInfo){
        MatchDelaySettleInfo matchDelaySettleInfo =new MatchDelaySettleInfo();
//        matchDelaySettleInfo.setId(IdGenerator.nextId());
        matchDelaySettleInfo.setId(UUIdUtils.getId());
        matchDelaySettleInfo.setScoreId(score.getId());
        matchDelaySettleInfo.setStandardMatchId(score.getStandardMatchId());
        matchDelaySettleInfo.setCheckInfoId(checkInfo.getId());
        matchDelaySettleInfo.setDataSourceCode(checkInfo.getDataSourceCode());
        matchDelaySettleInfo.setDelayType(1);//比分
        matchDelaySettleInfo.setSettleStatus(0);//未结算
        matchDelaySettleInfo.setModifyTime(System.currentTimeMillis());
        matchDelaySettleInfo.setCreateTime(System.currentTimeMillis());
        return  matchDelaySettleInfo;
    }
    public static MatchDelaySettleInfo initMatchDelayEventInfo(MatchSettleEvent event, MatchSettleCheckInfo checkInfo){
        MatchDelaySettleInfo matchDelaySettleInfo =new MatchDelaySettleInfo();
//        matchDelaySettleInfo.setId(IdGenerator.nextId());
        matchDelaySettleInfo.setId(UUIdUtils.getId());
        matchDelaySettleInfo.setScoreId(event.getId());
        matchDelaySettleInfo.setStandardMatchId(event.getStandardMatchId());
        matchDelaySettleInfo.setCheckInfoId(checkInfo.getId());
        matchDelaySettleInfo.setDataSourceCode(checkInfo.getDataSourceCode());
        matchDelaySettleInfo.setDelayType(2);//事件
        matchDelaySettleInfo.setSettleStatus(0);//未结算
        matchDelaySettleInfo.setModifyTime(System.currentTimeMillis());
        matchDelaySettleInfo.setCreateTime(System.currentTimeMillis());
        return  matchDelaySettleInfo;
    }


}
