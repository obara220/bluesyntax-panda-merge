package com.panda.merge.component;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;

@Slf4j
@Component
public class MatchEventMarketXCloseProcessor {


    @Autowired
    private RedisService redisService;

    /**
     * 篮球阶段 13：第一节 ，14：第二节 ，15：第三节 ，16：第四节
     */
    public static final List<Integer> BASKETBALL_EVENT_PERIOD = Arrays.asList(13, 14, 15, 16);
    /**
     * 阶段转x节
     */
    public static Map<Integer, Integer> PERIOD_X = new HashMap<Integer, Integer>() {{
        put(13, 1);
        put(14, 2);
        put(15, 3);
        put(16, 4);
    }};
    /**
     * 盘口附加字段需要关盘的阶段
     */
    public static final List<Long> add1 = Arrays.asList(215L, 405L, 406L, 407L);
    public static final List<Long> add2 = Arrays.asList(145L, 147L, 146L);


    /**
     * 子玩法根据阶段下沉到每个阶段
     *
     * @param linkId
     * @param sportId
     * @param matchId          赛事id
     * @param marketCategoryId 支持的玩法
     * @param period           支持的阶段
     * @param matchProgressTime  阶段关盘时间
     */
    public void marketCategoryApportionToPeriod(String linkId, Long sportId, Long matchId, Long marketCategoryId, Integer period, Integer matchProgressTime) {
        try {
            if (!sportId.equals(StandardSportTypeEnum.Basketball.getCode())
                    || period != 5999
                    || (!add1.contains(marketCategoryId) && !add2.contains(marketCategoryId))) {
                return;
            }
            BASKETBALL_EVENT_PERIOD.forEach(periodId -> {
                //赛事 + 阶段
                String key = Constant.REDIS_KEY.MATCH_EVENT_MARKET_X_CLOSE + matchId + "_" + periodId;
                JSONObject obj = new JSONObject();
                obj.put("marketCategoryId", marketCategoryId);//玩法
                obj.put("periodId", periodId);//阶段
                obj.put("matchProgressTime", matchProgressTime);//关盘时间
                obj.put("x", PERIOD_X.get(periodId));//关盘附加字段x值
                if (add1.contains(marketCategoryId)) {
                    obj.put("target", "add1");//关盘x所在的附加字段
                } else if ((add2.contains(marketCategoryId))) {
                    obj.put("target", "add2"); //关盘x所在的附加字段
                }
                redisService.hSet(key, marketCategoryId + "", obj, REDIS_HOUR_TIME * 4);
            });
        } catch (Exception e) {
            log.error("::" + linkId + "::赛事id:" + matchId + ",子玩法:" + marketCategoryId + ",根据阶段下沉到每个阶段，出现异常 ", e);
        }
    }
}
