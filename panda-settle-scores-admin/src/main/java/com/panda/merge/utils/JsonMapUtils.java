package com.panda.merge.utils;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.FootballScores;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

public class JsonMapUtils {
    public static String WHOLE_SCORE_PERIOD="-1";

    public static Map<String, Object> transfer15MinsJsonMap(String str) {
        if (StringUtils.isEmpty(str)) {
            return new HashMap<>();
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(str);
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<String, Object> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (Long.parseLong(o.toString()) > 60000L) //60899L
            {
                JSONObject json = (JSONObject) map.get(o);
                map3.put(o.toString(), json);
            }
        }
        return map3;
    }

    public static Map<String, FootballScores> transferFootBallScore(Map<String, Object> allPeriodScores) {
        Map<String, FootballScores> map=new HashMap<>();
        for (Map.Entry<String, Object> entry : allPeriodScores.entrySet()) {
            String key= entry.getKey();
//            FootballScores footballScores= new FootballScores();
            JSONObject jsonObject =JSONObject.parseObject(JSONObject.toJSONString(entry.getValue()));
            FootballScores footballScores =JSONObject.toJavaObject(jsonObject,FootballScores.class);
//            BeanUtils.copyProperties(entry.getValue(),footballScores);
            map.put(key,footballScores);
        }
        return map;
    }
    public static Map<String, BasketballScores>  transferBasketballMap(Map<String, Object> allPeriodScores) {
        Map<String, BasketballScores> map=new HashMap<>();
        for (Map.Entry<String, Object> entry : allPeriodScores.entrySet()) {
            String key= entry.getKey();
//            FootballScores footballScores= new FootballScores();
            JSONObject jsonObject =JSONObject.parseObject(JSONObject.toJSONString(entry.getValue()));
            BasketballScores footballScores =JSONObject.toJavaObject(jsonObject,BasketballScores.class);
//            BeanUtils.copyProperties(entry.getValue(),footballScores);
            map.put(key,footballScores);
        }
        return map;
    }
}

