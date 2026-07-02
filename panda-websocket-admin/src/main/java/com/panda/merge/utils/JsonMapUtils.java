package com.panda.merge.utils;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class JsonMapUtils {
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


    public static Map<Long, BasketballScores> parseBasketballMap(JSONObject jsonObject) {
        Map map = JSONObject.parseObject(jsonObject.toJSONString(), Map.class);
        Map<Long, BasketballScores> map3 = new HashMap<>();
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
}

