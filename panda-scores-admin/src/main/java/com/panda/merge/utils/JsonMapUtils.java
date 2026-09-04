package com.panda.merge.utils;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class JsonMapUtils {
    /**
     * json对象转map通用方法
     *
     * @param jsonObject json对象
     * @param clazz      转换对象
     * @param <T>        对象类型
     * @return map
     */
    public static <T> Map<Long, T> parseJsonObjectToMap(JSONObject jsonObject, Class<T> clazz) {
        Map<Long, T> sourceMap = JSONObject.parseObject(jsonObject.toJSONString(), new TypeReference<Map<Long, T>>() {
        });
        Map<Long, T> targetMap = new HashMap<>();
        for (Long key : sourceMap.keySet()) {
            targetMap.put(Long.parseLong(key.toString()), JSONObject.toJavaObject((JSONObject) sourceMap.get(key), clazz));
        }
        return targetMap;
    }

    /**
     * json对象转map通用方法-适用所有类型
     *
     * @param jsonObject json对象
     * @param clazz      转换对象
     * @param <T>        对象类型
     * @param <K>        对象类型
     * @return 返回数据
     */
    public static <T, K> Map<T, K> parseJsonObjectToMapCommon(JSONObject jsonObject, Class<K> clazz) {
        Map<T, K> sourceMap = JSONObject.parseObject(jsonObject.toJSONString(), new TypeReference<Map<T, K>>() {
        });
        Map<T, K> targetMap = new HashMap<>();
        for (T key : sourceMap.keySet()) {
            targetMap.put(key, JSONObject.toJavaObject((JSONObject) sourceMap.get(key), clazz));
        }
        return targetMap;
    }

    public static Map<Long, JSONObject> parseJsonMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, JSONObject> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), JSONObject.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), JSONObject.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), JSONObject.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), JSONObject.class));
            }
        }
        return map3;
    }

    public static Map<Long, FootballScores> parseFootballMap(JSONObject jsonObject) {
        if(jsonObject==null){
            return new HashMap<>();
        }
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, FootballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), FootballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), FootballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), FootballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), FootballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, HandballScores> parseHandballMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, HandballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), HandballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), HandballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), HandballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), HandballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, BeachVolleyballScores> parseBeachVolleyBallMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, BeachVolleyballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), BeachVolleyballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), BeachVolleyballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BeachVolleyballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BeachVolleyballScores.class));
            }
        }
        return map3;
    }

    public static void main(String[] arr) {
        Map<Long, BasketballScores> map = new HashMap<>();
        BasketballScores x = new BasketballScores(1l);
        x.getMatchScore().setHome(10);
        map.put(0l, x);
        map.put(6l, new BasketballScores(2l));

        String jsonObject = JSONObject.toJSONString(map);
        Map map2 = parseBasketballMap(JSONObject.parseObject(jsonObject));

        System.out.println(map2);
    }

    public static Map<Long, BasketballScores> parseBasketballMap(JSONObject jsonObject) {
        Map<Long, BasketballScores> map3 = new HashMap<>();
        if(jsonObject==null){
            return map3;
        }
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, BasketballScoresPDDto> parseBasketballPDDtoMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, BasketballScoresPDDto> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScoresPDDto.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScoresPDDto.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScoresPDDto.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BasketballScoresPDDto.class));
            }
        }
        return map3;
    }

    public static Map<Long, TennisScores> parseTennisMap(JSONObject jsonObject) {
        Map<Long, TennisScores> map3 = new HashMap<>();
        if(jsonObject==null){
            return map3;
        }
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), TennisScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), TennisScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), TennisScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), TennisScores.class));
            }
        }
        return map3;
    }


    public static Map<Long, BadmintonScores> parseBadmintonMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, BadmintonScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), BadmintonScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), BadmintonScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BadmintonScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BadmintonScores.class));
            }
        }
        return map3;
    }


    public static Map<Long, TableTennisScores> parseTableTennisMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, TableTennisScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), TableTennisScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), TableTennisScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), TableTennisScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), TableTennisScores.class));
            }
        }
        return map3;
    }
    public static Map<Long, CricketBallScores> parseCricketMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, CricketBallScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), CricketBallScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), CricketBallScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), CricketBallScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), CricketBallScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, SnookerScores> parseSnookerMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, SnookerScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), SnookerScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), SnookerScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), SnookerScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), SnookerScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, AmericanFootballScores> parseAmericanFootballMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, AmericanFootballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), AmericanFootballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), AmericanFootballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), AmericanFootballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), AmericanFootballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, VolleyballScores> parseVolleyballMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, VolleyballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, BaseballScores> parseBaseballMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, BaseballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, BaseballScores> parseBaseballLongMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, BaseballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), BaseballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, VolleyballScores> parseVolleyballLongMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, VolleyballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), VolleyballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, IceHockeyScores> parseIceHockeyMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, IceHockeyScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), IceHockeyScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), IceHockeyScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), IceHockeyScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), IceHockeyScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, HockeyScores> parseHockeyMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, HockeyScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), HockeyScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), HockeyScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), HockeyScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), HockeyScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, UKFootballScores> parseUKFootballMap(JSONObject allscores) {
        Map map = JSONObject.parseObject(allscores.toJSONString(), Map.class);
        Map<Long, UKFootballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), UKFootballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), UKFootballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), UKFootballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), UKFootballScores.class));
            }
        }
        return map3;
    }

    public static Map<Long, WaterballScores> parseWaterballMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, WaterballScores> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), WaterballScores.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), WaterballScores.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), WaterballScores.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), WaterballScores.class));
            }
        }
        return map3;
    }

    public static Map<String, Object> transferJsonMap(String str) {
        if (StringUtils.isEmpty(str)) {
            return new HashMap<>();
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(str);
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<String, Object> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            JSONObject json = (JSONObject) map.get(o);
            map3.put(o.toString(), json);
        }
        return map3;
    }

    public static Map<String, Object> transferSimpleJsonMap(String str) {
        if (StringUtils.isEmpty(str)) {
            return new HashMap<>();
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(str);
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<String, Object> map3 = new HashMap<>();
        for (Object o : map.keySet()) {
            //对应结算2.0获取 15分钟比分
//            if (Long.parseLong(o.toString()) > 60000L) //60899L
//            {
//                continue;
//            }
            JSONObject json = (JSONObject) map.get(o);
            map3.put(o.toString(), json);
        }
        return map3;
    }

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



    public static Map<Long, Object> parseObjectMap(JSONObject jsonObject) {
        Map<Long, Object> map3 = new HashMap<>();
        if(jsonObject==null){
            return map3;
        }
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        for (Object o : map.keySet()) {
            if (o instanceof String) {
                map3.put(Long.parseLong(o.toString()), JSONObject.toJavaObject((JSONObject) map.get(o), Object.class));
            } else if (o instanceof Integer) {
                map3.put((Integer) o + 0l, JSONObject.toJavaObject((JSONObject) map.get(o), Object.class));
            } else if (o instanceof Long) {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), Object.class));
            } else {
                map3.put((Long) o, JSONObject.toJavaObject((JSONObject) map.get(o), Object.class));
            }
        }
        return map3;
    }

}

