package com.panda.merge.rocketmq.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.A99DataSourceWeightDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Validated
@Component
public class A99DataSourceWeightProcessor extends BaseProcessor {

    /**
     * 玩法集
     * 10001：常规进球 让球、大小
     * 10002：常规角球 角球相关
     * 10003：常规罚牌 罚牌相关
     * 10005：加时进球 加时进球相关
     * 10006：加时角球 加时角球相关
     * 10007：加时罚牌 加时罚牌相关
     */
    List<String> playSets = Arrays.asList("10001","10002","10003","10005","10006","10007");
    public void execute(A99DataSourceWeightDTO dto) {
        log.info("{}::接收A99数据源权重,赛事id:{},早滚:{}", dto.getLinkId(), dto.getMatchId(), dto.getMatchType());
        String weightRedisKey = Constant.REDIS_KEY.RONGHE_A99_DATA_SOURCE_WEIGHT + ":" + dto.getMatchId() + ":" + dto.getMatchType();
        String cautionRedisKey = Constant.REDIS_KEY.RONGHE_A99_DATA_SOURCE_CAUTION_VALUE + ":" + dto.getMatchId() + ":" + dto.getMatchType();
        String a99Configs = dto.getA99ConfigValue();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Map> map = objectMapper.readValue(
                    a99Configs,
                    new TypeReference<List<Map>>() {}
            );
            map.forEach(x -> {
                playSets.forEach(s -> {
                    if(x.containsKey(s)) {
                        log.info("缓存玩法集警戒值, 玩法集id:{}, 警戒值:{}", s, x.get("cautionValue"));
                        redisService.hSet(cautionRedisKey, s, x.get("cautionValue"), 15*24*60*60);
                    }
                    List<Map> weights = (List<Map>)x.get(s);
                    if (CollectionUtil.isNotEmpty(weights)) {
                        weights.forEach(w -> {
                            log.info("{}::缓存数据源权重,赛事id:{},早滚:{},玩法集id:{}, 数据源:{}, 权重:{}", dto.getLinkId(), dto.getMatchId(), dto.getMatchType(), s, w.get("name"), w.get("value"));
                            if (ObjectUtil.isNotEmpty(w.get("name")) && ObjectUtil.isNotEmpty(w.get("value"))) {
                                redisService.hSet(weightRedisKey, s + ":" + w.get("name"), w.get("value"), 15*24*60*60);
                            }
                        });
                    }
                });

            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
