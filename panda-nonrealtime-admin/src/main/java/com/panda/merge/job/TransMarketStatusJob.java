package com.panda.merge.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.cache.CategoryStatsTimeData;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.MatchBeginProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@JobHandler(value = "TransMarketStatusJob")
public class TransMarketStatusJob extends IJobHandler {
    @Autowired
    private RedisService redisService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    public MatchBeginProducer matchBeginProducer;

    /**
     * 玩法全封或全关转为关的等待时间，单位分钟
     */
    @NacosValue(value = "${category.waitCloseTime:2}", autoRefreshed = true)
    private String waitCloseTime;

    /**
     * 玩法全封或全关转为关的等待处理开关
     */
    @NacosValue(value = "${category.waitCloseTime.Switch:true}", autoRefreshed = true)
    private boolean waitCloseTimeSwitch;

    @Override
    public ReturnT<String> execute(String param) {
        if (!waitCloseTimeSwitch) {
            XxlJobLogger.log("处理开关为关闭状态,不进行处理,TransMarketStatusJob处理结束");
            //log.info("处理开关为关闭状态,不进行处理,TransMarketStatusJob处理结束");
            // 开关关闭时， 删除这两个缓存
            Set<Object> matchIdRawList = redisService.sMembers(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS);
            Set<Long> matchIdList = matchIdRawList.stream().map(e -> (Long)e).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(matchIdList)) {
                redisService.sRemove(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS, matchIdList.toArray());
                List<String> needDelCategoryStatTimeKeyList = matchIdList.stream()
                        .map(matchId -> DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_PROCESSING + matchId))
                        .collect(Collectors.toList());
                redisService.del(needDelCategoryStatTimeKeyList);
            }
            return ReturnT.SUCCESS;
        }
        long waitCloseTimeMills = Long.parseLong(waitCloseTime) * 60 * 1000;
        //log.info("TransMarketStatusJob处理开始");
        XxlJobLogger.log("TransMarketStatusJob处理开始");
        Set<Object> matchIdRawList = redisService.sMembers(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS);
        Set<Long> matchIdList = matchIdRawList.stream().map(e -> (Long)e).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(matchIdList)) {
            //log.info("TransMarketStatusJob没有需要处理的赛事，任务处理结束");
            XxlJobLogger.log("TransMarketStatusJob没有需要处理的赛事，任务处理结束");
            return ReturnT.SUCCESS;
        }
        //log.info("TransMarketStatusJob缓存中的赛事id:{}", JSON.toJSONString(matchIdList));
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(new ArrayList<>(matchIdList));

        Map<Long, StandardMatchInfo> matchMapGroupById = standardMatchInfos.stream()
            .collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity(), (v1, v2) -> v2));
        List<Long> needRemoveMatchIdList = new ArrayList<>();

        for (Long matchId : matchIdList) {
            if (!matchMapGroupById.containsKey(matchId)) {
                // 赛事不存在删除缓存
                needRemoveMatchIdList.add(matchId);
            }
        }
        List<Long> needConfirmMatchIdList = new ArrayList<>();
        matchMapGroupById.forEach((k, v) -> {
            // 完赛清除缓存
            if (YesNoEnum.Y.value.equals(v.getMatchOver())) {
                needRemoveMatchIdList.add(k);
            } else {
                needConfirmMatchIdList.add(k);
            }
        });
        // 再次查库确认完赛状态
        if (!CollectionUtils.isEmpty(needConfirmMatchIdList)) {
            List<StandardMatchInfo> standardMatchInfosFromDb = standardMatchInfoService.getItemByPrimaryKeys(new ArrayList<>(needConfirmMatchIdList));
            Map<Long, StandardMatchInfo> matchMapFromDbGroupById = standardMatchInfosFromDb.stream()
                    .collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity(), (v1, v2) -> v2));
            matchMapFromDbGroupById.forEach((k, v) -> {
                // 完赛清除缓存
                if (YesNoEnum.Y.value.equals(v.getMatchOver())) {
                    needRemoveMatchIdList.add(k);
                }
            });
        }
        // 需要处理的赛事列表,对每场赛事进行处理
        // 删除之前，盘口是否已下发，未下发则下发一次关盘
        for (Long matchId : matchIdList) {
            String categoryStatTimeKey =
                DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_PROCESSING + matchId);
            Map<String, CategoryStatsTimeData> cacheMap = redisService.hGetAll(categoryStatTimeKey);
            List<String> needCloseCategoryList = new ArrayList<>();
            cacheMap.forEach((category, storeData) -> {
                long currentWaitTimeMills = System.currentTimeMillis() - storeData.getTime();
                if (currentWaitTimeMills > waitCloseTimeMills) {
                    if (storeData.isHaveAlreadySend()) {
                        //log.info("赛事id:{}玩法id:{}已下发过，此次不处理", matchId, category);
                        return;
                    }
                    // 需要下发关的玩法
                    needCloseCategoryList.add(category);
                    storeData.setHaveAlreadySend(true);
                }
            });
            if (!CollectionUtils.isEmpty(needCloseCategoryList)) {
                //log.info("赛事id:{}需要下发关盘的玩法id:{}", matchId, JSON.toJSONString(needCloseCategoryList));
                String linkId = IdWorker.getId() + "_Close_Category";
                // 通过mq topic下发需要关盘的玩法到panda-odds-admin
                matchBeginProducer.sendCloseCategory2OddsAdmin(linkId, matchId, needCloseCategoryList);
                needCloseCategoryList.forEach(e -> redisService.hSet(categoryStatTimeKey, e, cacheMap.get(e)));
            }
        }
        // 删除缓存
        if (!CollectionUtils.isEmpty(needRemoveMatchIdList)) {
            //log.info("需要从缓存中移出的赛事id:{}", JSON.toJSONString(needRemoveMatchIdList));
            redisService.sRemove(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_MATCHIDS, needRemoveMatchIdList.toArray());

            List<String> needDelCategoryStatTimeKeyList = needRemoveMatchIdList.stream()
                    .map(matchId -> DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_CATEGORY_TIMING_PROCESSING + matchId))
                    .collect(Collectors.toList());
            redisService.del(needDelCategoryStatTimeKeyList);
        }
        //log.info("TransMarketStatusJob处理结束");
        XxlJobLogger.log("TransMarketStatusJob处理结束");
        return ReturnT.SUCCESS;
    }
}
