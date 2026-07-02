package com.panda.merge.utils;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.IceHockeyScores;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 比分工具类
 */
@Slf4j
@Component
public class ScoreUtils {

    /**
     * 将比分阶段字符串转变为阶段Map
     * @param scoresJson
     * @param clazz
     * @param <T>
     * @return
     */
    public <IceHockeyScores> Map<Long, com.panda.merge.dto.IceHockeyScores>  periodJson(String scoresJson, Class<com.panda.merge.dto.IceHockeyScores> clazz) {
//        Map<Long, T> periodMap = new HashMap<>();
//        if (StringUtils.isEmpty(scoresJson)) {
//            return periodMap;
//        }
//        JSONObject periodScores = JSONObject.parseObject(scoresJson);
//        if (!Objects.isNull(periodScores)) {
//            Map jsonMap = JSONObject.parseObject(periodScores.toJSONString(), Map.class);
//            if (!Objects.isNull(jsonMap)) {
//                for (Object obj : jsonMap.keySet()) {
//                    if ( null!= obj ) {
//                        periodMap.put( Long.parseLong(obj.toString()), (T)jsonMap.get(obj));
//                    }
//                }
//            }
//        }
        JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
        Map<Long, com.panda.merge.dto.IceHockeyScores>  allPeriodScores= JsonMapUtils.parseIceHockeyMap(periodFootballScores);
        return allPeriodScores;
    }
}
