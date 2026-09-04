package com.panda.merge.constant;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class EffectScoresCode {
    private static String effectCodeJson="{1: 'penalty_missed,match_status,coverage_status,delete_event,goal,corner," +
            "yellow_card,red_card,shot_on_target,shot_off_target,offside,dangerous_attack,attack,possession,substitution," +
            "penalty_shootout_event,free_kick,kick_off_team,temporary_interruption,var_reviewing," +
            "var_reason,video_assistant_referee,throw_in,possible_throw_in,ball_safe," +
            "injury_time,possible_video_assistant_referee,canceled_video_assistant_referee," +
            "danger,penalty_awarded,retake_pen,penalty_missed,canceled_penalty,canceled_goal,goal_time_modified,redcard_time_modified,yellowcard_time_modified,corner_time_modified'," +
            " 2: 'possession,period_score_change,match_status,score_correction,coverage_status,period_score,foul,score_change,possession,substitution,rebound,number_free_throw,score_miss,timeout' ," +
            "3: 'batter_advances_to_base_x,coverage_status,match_status,play_start,who_throws_the_first_pitch,current_pitcher_baseball,current_batter_baseball,strike,ball,foul_ball,runner_advances_to_base_x,run_scored,runner_out,batter_out,batter_advances_to_base_,checked_runner,match_status,baseball_stats_correction' ," +
            "4: 'coverage_status,goal,suspension,match_status,5mins_pen,2mins_pen' ," +
            "5: 'coverage_status,match_status,tennis_score_change,delete_event,tennis_service_fault' ," +
            "6: 'coverage_status,match_status,delete_event,touchdown,extra_point,point2_conversion,field_goal,safety,play_start,rush,challenge,penalty,turn_over,af_kick_off' ," +
            "7: 'coverage_status,match_status,snooker_score_change,ball_pot,snooker_foul,match_status,free_ball,ball_possession," +
            "snooker_score_red,snooker_score_yellow,snooker_score_green,snooker_score_brown,snooker_score_blue," +
            "snooker_score_pink,snooker_score_black,free_ball_pot,snooker_foul_red,snooker_foul_yellow,snooke_foul_green," +
            "snookerr_foul_brown,snookerr_foul_blue,snookerr_foul_pink,snookerr_foul_black,snooker_foul_4,snooker_foul_7' ," +
            "8: 'coverage_status,match_status,red_card,yellow_card,match_status,which_team_serves_first,delete_event,re_serve,table_tennis_score_change,table_tennis_violation,yellowred_card_same_hand,game_winner,match_over,game_start' ," +
            "9: 'coverage_status,match_status,volleyball_score_change,current_serve_volleyball,which_team_serves_first,ace,kill,block,service_error,out,penalty,error,expulsion,disqualification,suspension,suspension_over,timeout,timeout_over,delete_event,batch_edit_set_scores' ," +
            "10: 'coverage_status,match_status,yellow_card,red_card,match_status,delete_event,badminton_score_change,black_card,match_stop_suspension,match_stop_suspension_over' ," +
            "11: 'goal,match_status,delete_event' ,12: '' ,13: 'score_change,match_status,delete_event', " +
            "14: 'delete_event,yellow_card,red_card,coverage_status,match_status,try,penalty_try,conversion,penalty_points,drop_goal,penalty_comp_goal'," +
            "15: 'coverage_status,match_status,goal,delete_event,yellow_card,red_card,green_card,substitution,ball_possession,dangerous_attack,attack,time_start'," +
            "16: 'waterpolo_score_change,match_status,delete_event,waterpolo_adjust_time'," +
            "37: 'delivery,match_status,over,point,wicet,to_win_the_toss'}";


    public static Map<Long, Set<String>> EFFECT_SCORES_CODE_MAP ;
    public static String YELLOW_RED_CARD="yellow_red_card";
    public static String RED_CARD="red_card";
    public static String DELETE_EVENT="delete_event";
    public static String UNKNOW_EVENT="unknow_event";
    public static String NONE_EVENT="none_event";
    public static String BREAK_POINT_EVENT="break_point";
    public static String BREAK_SUCCESS_EVENT="break_success";
    public static List<String> FOOTBALL_POSSESSION_EVENT = new ArrayList<>(Arrays.asList("match_status","possible_red_card","possible_yellow_card","possible_corner","possible_penalty","possible_goal","dangerous_attack","ball_safe","yellow_card","red_card","corner","goal","canceled_yellow_card","canceled_red_card","canceled_corner","canceled_goal","canceled_penalty","penalty_missed","kick_off","kick_off_team","var_reason","attack","yellow_red_card","throw_in","possession","goal_kick","possible_free_kick","free_kick","offside","shot_on_target","shot_off_target","substitution","injury","water_break","injury_time","possible_video_assistant_referee","video_assistant_referee_over","canceled_video_assistant_referee","canceled_free_kick","penalty","possible_var_red_card","possible_var_goal","possible_var_penalty","var_red_card","var_goal","var_penalty","var_yellow_card","canceled_var_red_card","canceled_var_goal","canceled_var_penalty","penalty_goal","penalty_canceled","retake_pen"));
    public static List<Long> FOOTBALL_POSSESSION_PERIOD = new ArrayList<>(Arrays.asList(6L,7L, 41L, 42L,100L,999L));

    //斯诺克比分事件
    public static List<String> SNOOKER_SCORES_EVENT = new ArrayList<>(Arrays.asList("match_status","snooker_score_red","snooker_score_yellow","snooker_score_green","snooker_score_brown","snooker_score_blue","snooker_score_pink","snooker_score_black","free_ball_pot","snooker_foul_red","snooker_foul_yellow","snooke_foul_green","snookerr_foul_brown","snookerr_foul_blue","snookerr_foul_pink","snookerr_foul_black","snooker_foul_4","snooker_foul_7"));



    static {
        EFFECT_SCORES_CODE_MAP=new HashMap<>();
        JSONObject jsonObject =JSONObject.parseObject(effectCodeJson);
        for (Object s : jsonObject.keySet()) {
            if(s==null)
                continue;
            String value =jsonObject.get(s).toString();
            String[] eventCodes = value.trim().split(",");
            if(eventCodes.length==0)
                continue;
            EFFECT_SCORES_CODE_MAP.put(Long.parseLong(s.toString()), Sets.newHashSet(eventCodes));
        }
    }

    /**
     * 校验赛种所匹配的事件编码
     * @param sportId
     * @param dataSourceCode
     * @param eventCode
     * @return
     */
    public static boolean chargeEffectScores(Long sportId,String dataSourceCode,String eventCode) {
        if(SportTypeEnum.SNOOKER.getValue().equals(sportId)){
            return true;
        }
        Set<String> effectCode= EffectScoresCode.EFFECT_SCORES_CODE_MAP.get(sportId);
        if (ObjectUtils.isEmpty(effectCode)) {
            return false;
        }
        return effectCode.contains(eventCode);
    }

}
