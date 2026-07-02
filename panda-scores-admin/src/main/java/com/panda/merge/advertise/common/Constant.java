package com.panda.merge.advertise.common;

import java.util.*;

public  class Constant {

    public final static String PD = "PD";
    public final static String PD2 = "PD2";

    public final static Integer MATCH_START=1;
    public final static Integer MATCH_PAUSE=2;
    public final static Integer MATCH_CONTINUE=3;
    public final static Integer MATCH_END=4;

    public final static String MATCH_ADVERTIS_EVENT_STATUS="MATCH_ADVERTIS_EVENT_STATUS:";

    public final static String IGNORE_PRE = "panda-score-admin:ignore:";

    public final static String MATCH_FOOTBALL_CONFIRM_EVENT_TIME="MATCH_FOOTBALL_CONFIRM_EVENT_TIME:";
    public final static String MATCH_FOOTBALL_CONFIRM_POSSIBLE_TIME="MATCH_FOOTBALL_CONFIRM_POSSIBLE_TIME:";
    /**
     * 缓存缓存已经被消费linkId,key
     */
    public final static String MATCH_EVENT_CONSUMPTION_LINK_ID="MATCH_EVENT_CONSUMPTION_LINK_ID:";
    /**
     * 加时赛比赛阶段key
     */
    public final static String MATCH_BASKETBALL_PERIOD_OT = "BASKETBALL_PAUSE_CONTINUE_OT:";
    /**
     * 常规赛阶段key
     */
    public final static String MATCH_BASKETBALL_PERIOD = "BASKETBALL_PAUSE_CONTINUE:";

    /**
     * 当前最新次数
     */
    public final static String MATCH_BASKETBALL_PERIOD_QT_TIMES="MATCH_BASKETBALL_PERIOD_QT_TIMES:%s";
    /**
     * 报球事件redis缓存
     */
    public final static String ACTION_MONITER_KEY="action_monitor_key:%s";


    /**
     * 报球版点球大战重踢redis缓存
     */
    public final static String PREVENT_DUPLICATION_KEY="prevent_duplication_key:%s";

    public static class BasketBallConstant{
        /**通过赛制得到对应节时长*/
        public static Map<Integer,Integer> matchLenthTimeMap= new HashMap();
        /**通过节时长得到对应赛制*/
        public static Map<Integer,Integer> timeMatchLenthMap= new HashMap();

        public static Set<Long> BASKETL_BALL_PERIODS =new HashSet<>();

        static {
            matchLenthTimeMap.put(0,10);
            matchLenthTimeMap.put(7,12);
            matchLenthTimeMap.put(17,20);
            //新增篮球赛制
            matchLenthTimeMap.put(68,5);
            matchLenthTimeMap.put(73,10);
            timeMatchLenthMap.put(10,0);
            timeMatchLenthMap.put(12,7);
            timeMatchLenthMap.put(20,17);
            //新增篮球赛制
            timeMatchLenthMap.put(5,68);
            timeMatchLenthMap.put(9,73);

            BASKETL_BALL_PERIODS.add(1l);
            BASKETL_BALL_PERIODS.add(2l);
            BASKETL_BALL_PERIODS.add(40l);
            BASKETL_BALL_PERIODS.add(13l);
            BASKETL_BALL_PERIODS.add(14l);
            BASKETL_BALL_PERIODS.add(15l);
            BASKETL_BALL_PERIODS.add(16l);
            BASKETL_BALL_PERIODS.add(21l);
        }

        public static Long[] getWholePeriodsByMatchLength(Integer matchLength){
            if(matchLength==null){
                matchLength = 0;
            }
            if(matchLength==17){
                return new Long[]{0L,1L,31L, 2L,32L,40L,110L,999L};
            }else if(matchLength==1){
                //3X3 1 ,999L
                return new Long[]{0L,1L,100L,999L};
            } else if ( matchLength == 73 ) {
                return new Long[]{0L,21L,100L,32L,40L,110L,999L};
            } else {
                return  new Long[]{0L,13L,301L,14L,302L, 15L,303L,16L,32L,40L,110L,999L};
            }
        }

    }
    public static final List<String> SCORE_EVENT_LIST =new ArrayList<>();
    static {
        SCORE_EVENT_LIST.add("possible_yellow_card");
        SCORE_EVENT_LIST.add("possible_red_card");
        SCORE_EVENT_LIST.add("possible_corner");
        SCORE_EVENT_LIST.add("possible_penalty");
        SCORE_EVENT_LIST.add("possible_goal");
        SCORE_EVENT_LIST.add("dangerous_attack");
        SCORE_EVENT_LIST.add("ball_safe");
        SCORE_EVENT_LIST.add("yellow_card");
        SCORE_EVENT_LIST.add("red_card");
        SCORE_EVENT_LIST.add("corner");
        SCORE_EVENT_LIST.add("goal");
        SCORE_EVENT_LIST.add("canceled_yellow_card");
        SCORE_EVENT_LIST.add("canceled_red_card");
        SCORE_EVENT_LIST.add("canceled_corner");
        SCORE_EVENT_LIST.add("canceled_goal");
        SCORE_EVENT_LIST.add("canceled_penalty");
        SCORE_EVENT_LIST.add("penalty_missed");
        SCORE_EVENT_LIST.add("kick_off"); //2399要求显示kick_off赛事消息
        SCORE_EVENT_LIST.add("kick_off_team");
        SCORE_EVENT_LIST.add("var_reason");//VAR事件
        SCORE_EVENT_LIST.add("attack");
        SCORE_EVENT_LIST.add("yellow_red_card");
        SCORE_EVENT_LIST.add("throw_in");
        SCORE_EVENT_LIST.add("possession");
        SCORE_EVENT_LIST.add("possession_count");
        SCORE_EVENT_LIST.add("ball_possession_percentage");
        SCORE_EVENT_LIST.add("goal_kick");
        SCORE_EVENT_LIST.add("possible_free_kick");
        SCORE_EVENT_LIST.add("free_kick");
        SCORE_EVENT_LIST.add("canceled_goal");
        SCORE_EVENT_LIST.add("offside");
        SCORE_EVENT_LIST.add("shot_on_target");
        SCORE_EVENT_LIST.add("shot_off_target");
        SCORE_EVENT_LIST.add("substitution");
        SCORE_EVENT_LIST.add("injury");
        SCORE_EVENT_LIST.add("water_break");
        // 暂停(临时中断)
        SCORE_EVENT_LIST.add("temporary_interruption");
        SCORE_EVENT_LIST.add("injury_time");
        // 重开(比赛开始)
        SCORE_EVENT_LIST.add("game_on");
        SCORE_EVENT_LIST.add("match_status");
        SCORE_EVENT_LIST.add("possible_video_assistant_referee");
        SCORE_EVENT_LIST.add("video_assistant_referee_over");
        SCORE_EVENT_LIST.add("canceled_video_assistant_referee");
        SCORE_EVENT_LIST.add("canceled_free_kick");
        SCORE_EVENT_LIST.add("penalty");
        SCORE_EVENT_LIST.add("suspension_over");
        SCORE_EVENT_LIST.add("suspension");
        // var
        SCORE_EVENT_LIST.add("possible_var_red_card");
        SCORE_EVENT_LIST.add("possible_var_goal");
        SCORE_EVENT_LIST.add("possible_var_penalty");
        SCORE_EVENT_LIST.add("var_red_card");
        SCORE_EVENT_LIST.add("var_goal");
        SCORE_EVENT_LIST.add("var_penalty");
        SCORE_EVENT_LIST.add("var_yellow_card");
        SCORE_EVENT_LIST.add("canceled_var_red_card");
        SCORE_EVENT_LIST.add("canceled_var_goal");
        SCORE_EVENT_LIST.add("canceled_var_penalty");
        SCORE_EVENT_LIST.add("take_penalty");
        SCORE_EVENT_LIST.add("penalty_first");
        SCORE_EVENT_LIST.add("penalty_goal");
        SCORE_EVENT_LIST.add("penalty_canceled");
        SCORE_EVENT_LIST.add("retake_pen");
        // 编辑足球比分
        SCORE_EVENT_LIST.add("edit_score");
    }

    public static final List<String> penaltyDangerEventCode = Arrays.asList("take_penalty", "penalty_about_to_be_taken");
}
