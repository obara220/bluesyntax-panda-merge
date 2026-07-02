package com.panda.merge.constant;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.panda.merge.common.enums.DataSourceCodeEnum;

import java.util.*;

public class EffectScoresCode {
    private static String effectCodeJson="{1: 'match_status,coverage_status,delete_event,goal,corner,yellow_card,red_card,yellow_red_card,shot_on_target,shot_off_target,offside,dangerous_attack,attack,possession,substitution,penalty_awarded,penalty_shootout_event,free_kick'," +
            " 2: 'match_status,score_correction,coverage_status,period_score,foul,score_change,possession,substitution,rebound,number_free_throw,score_miss,timeout' ," +
            "3: 'batter_advances_to_base_x,coverage_status,match_status,play_start,who_throws_the_first_pitch,current_pitcher_baseball,current_batter_baseball,strike,ball,foul_ball,runner_advances_to_base_x,run_scored,runner_out,batter_out,batter_advances_to_base_,checked_runner,match_status,baseball_stats_correction' ," +
            "4: 'coverage_status,goal,suspension,match_status' ," +
            "5: 'coverage_status,match_status,tennis_score_change,delete_event,tennis_service_fault' ," +
            "6: 'coverage_status,match_status,delete_event,touchdown,extra_point,point2_conversion,field_goal,safety,play_start,rush,challenge,penalty,turn_over,af_kick_off' ," +
            "7: 'coverage_status,match_status,snooker_score_change,ball_pot,snooker_foul,match_status,free_ball' ," +
            "8: 'coverage_status,match_status,red_card,yellow_card,match_status,which_team_serves_first,delete_event,re_serve,table_tennis_score_change,table_tennis_violation,yellowred_card_same_hand,game_winner,match_over,game_start' ," +
            "9: 'coverage_status,match_status,volleyball_score_change,current_serve_volleyball' ," +
            "10: 'coverage_status,match_status,yellow_card,red_card,match_status,delete_event,badminton_score_change,black_card,match_stop_suspension,match_stop_suspension_over' ," +
            "11: 'goal,match_status,delete_event' ,12: '' ,13: 'score_change,match_status,delete_event', " +
            "14: 'delete_event,yellow_card,red_card,coverage_status,match_status,try,penalty_try,conversion,penalty_points,drop_goal,penalty_comp_goal'," +
            "15: 'coverage_status,match_status,goal,delete_event,yellow_card,red_card,green_card,substitution,ball_possession,dangerous_attack,attack,time_start'," +
            "16: 'waterpolo_score_change,match_status,delete_event'}";

    public static Map<Long, List<String>> EFFECT_SCORES_CODE_MAP ;
    public static String YELLOW_RED_CARD="yellow_red_card";
    public static String RED_CARD="red_card";
    public static String DELETE_EVENT="delete_event";
    public static String UNKNOW_EVENT="unknow_event";
    public static String NONE_EVENT="none_event";
    public static String BREAK_POINT_EVENT="break_point";
    public static String BREAK_SUCCESS_EVENT="break_success";


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
            List<String> list= Arrays.asList(eventCodes);
            EFFECT_SCORES_CODE_MAP.put(Long.parseLong(s.toString()), list);
        }
    }

    public static   List<String>   getEffectCodeBySport(Long sportId) {
        return  EffectScoresCode.EFFECT_SCORES_CODE_MAP.get(sportId);
    }

}
