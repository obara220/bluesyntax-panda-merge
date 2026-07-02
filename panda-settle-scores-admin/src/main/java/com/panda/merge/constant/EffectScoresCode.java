package com.panda.merge.constant;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.panda.merge.common.enums.DataSourceCodeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EffectScoresCode {
    private static String effectCodeJson="{1: 'penalty_missed,goal,corner,yellow_card,red_card,yellow_red_card,play_resumes_after_goal,kick_off,corner_taken,goal_time_modified,redcard_time_modified,yellowcard_time_modified,corner_time_modified'}";
    public static Map<Long, Set<String>> EFFECT_SCORES_CODE_MAP ;
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
    //校验事件编码
    public static boolean chargeEffectScores(Long sportId,String eventCode) {
        Set<String> effectCode= EffectScoresCode.EFFECT_SCORES_CODE_MAP.get(sportId);
        return effectCode.contains(eventCode);
    }
}
