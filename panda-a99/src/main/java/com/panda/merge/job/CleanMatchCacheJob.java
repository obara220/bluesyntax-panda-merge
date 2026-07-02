package com.panda.merge.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.A99ParamConfig;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.job.common.A99MarketOddsCommon;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.processor.A99ThirdMatchMarketProcessor;
import com.panda.merge.service.StandardMatchInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_A99_ODDS_CHANGE_DIFFERENCE_LIVE;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_A99_ODDS_CHANGE_DIFFERENCE_PRE;

@Slf4j
@Component
@JobHandler(value = "CleanMatchCacheJob")
public class CleanMatchCacheJob extends IJobHandler {

    String HOST_ADDRESS = "";

    String KEY = "Ronghe:a99:clean:cache:key";

    @Autowired
    RedisService redisService;

    @Autowired
    private A99MarketOddsCommon marketOddsCommon;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private A99ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private A99ParamConfig a99ParamConfig;
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始执行调度任务===>CleanMatchCacheJob!,param=" + param);
        Set<Long> liveSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS);
        if (CollectionUtil.isNotEmpty(liveSet)) {
            ArrayList<Long> liveMatchIds = new ArrayList<>(liveSet);
            List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(liveMatchIds);//标准赛事ID,标准赛事信息
            Map<Long, StandardMatchInfo> standardMatchInfoMap = standardMatchInfos.stream().collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity(), (v1, v2) -> v1));
            for (Long matchId : liveMatchIds) {
                StandardMatchInfo standardMatchInfo = standardMatchInfoMap.get(matchId);
                if (standardMatchInfo.getMatchOver() == 1) {
                    log.info("滚球赛事id:{},当前赛事已完赛, 即将移出A99滚球赛事列表", matchId, standardMatchInfo.getMatchStatus());
                    cleanCache(matchId, 0);
                }
            }
        }
        XxlJobLogger.log("结束执行调度任务===>CleanMatchCacheJob!");
        return ReturnT.SUCCESS;
    }
    /**
     * 删除缓存
     * @param matchId 赛事id
     * @param matchType 1:早盘  0:滚球
     */
    private void cleanCache(Long matchId, int matchType){
        String lockValue = IdUtil.simpleUUID();
        //清除赛事
        String matchKey = matchType == 0 ? Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS : Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS;
        if (redisService.hHasKey(matchKey, matchId.toString())) {
            try {
                redisService.tryLock(matchKey, lockValue, 3, 3);
                redisService.hDel(matchKey, matchId.toString());
                log.info("A99赛事开关缓存清除成功, Key:{}, item:{}", matchKey, matchId);
            } finally {
                redisService.unLock(matchKey, lockValue);
            }
        }
    /**
     * 10分钟执行一次
     * 检测早盘赛事是否进入滚球或完赛，如果进入滚球，从早盘赛事缓存中清除赛事id, 盘口缓存中清除相关的缓存盘口信息
     * 检测滚球赛事是否完赛，如果完赛，从滚球赛事缓存中清除赛事id，盘口缓存中清除相关的缓存盘口信息
     */
   /* @Scheduled(initialDelay = 10000, fixedRate = 600000)
    public void execute() {
        if (StringUtils.isEmpty(HOST_ADDRESS)) {
            InetAddress address = null;
            try {
                address = getLocalHostExactAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HOST_ADDRESS = address.getHostAddress();
        }
        Object oldAddress = redisService.get(KEY);
        if (oldAddress == null || StringUtils.equals((String) oldAddress, HOST_ADDRESS)) {
            log.info("执行赔率定时计算定时任务,当前执行节点:{}", oldAddress);
            redisService.set(KEY, HOST_ADDRESS, RedisConfig.REDIS_SIXTY_SECOND);

            Set<Long> preSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS);
            if (CollectionUtil.isNotEmpty(preSet)) {
                ArrayList<Long> preMatchIds = new ArrayList<>(preSet);
                List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(preMatchIds);//标准赛事ID,标准赛事信息
                Map<Long, StandardMatchInfo> standardMatchInfoMap = standardMatchInfos.stream().collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity(), (v1, v2) -> v1));
                for (Long matchId : preMatchIds) {
                    StandardMatchInfo standardMatchInfo = standardMatchInfoMap.get(matchId);
                    log.info("赛事id:{}, 赛事状态:{}, 赛事是否完赛:{}", matchId, standardMatchInfo.getMatchStatus(), standardMatchInfo.getMatchOver());
                    if (standardMatchInfo.getMatchStatus() != 0) {
                        log.info("早盘赛事id:{},当前赛事状态为:{}, 即将移出A99早盘赛事列表", matchId, standardMatchInfo.getMatchStatus());
                        cleanCache(matchId, 1);
                    } else if (standardMatchInfo.getMatchOver() == 1) {
                        log.info("早盘赛事id:{},当前赛事已完赛, 即将移出A99早盘赛事列表", matchId, standardMatchInfo.getMatchStatus());
                        cleanCache(matchId, 1);
                    }
                }
            }

            Set<Long> liveSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS);
            if (CollectionUtil.isNotEmpty(liveSet)) {
                ArrayList<Long> liveMatchIds = new ArrayList<>(liveSet);
                List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(liveMatchIds);//标准赛事ID,标准赛事信息
                Map<Long, StandardMatchInfo> standardMatchInfoMap = standardMatchInfos.stream().collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity(), (v1, v2) -> v1));
                for (Long matchId : liveMatchIds) {
                    StandardMatchInfo standardMatchInfo = standardMatchInfoMap.get(matchId);
                    if (standardMatchInfo.getMatchOver() == 1) {
                        log.info("滚球赛事id:{},当前赛事已完赛, 即将移出A99滚球赛事列表", matchId, standardMatchInfo.getMatchStatus());
                        cleanCache(matchId, 0);
                    }
                }
            }
        }
    }*/


        //清除数据源权重
        String weightKey = Constant.REDIS_KEY.RONGHE_A99_DATA_SOURCE_WEIGHT + ":" + matchId + ":" + matchType;
        if (redisService.hasKey(weightKey)) {
            try {
                redisService.tryLock(weightKey, lockValue, 3, 3);
                redisService.del(weightKey);
                log.info("A99赛事权重缓存清除成功, Key:{}", weightKey);
            } finally {
                redisService.unLock(weightKey, lockValue);
            }
        }
        //清除盘口
        String categoryKeyPrefix = matchType == 0 ? Constant.REDIS_KEY.RONGHE_A99_THIRD_MARKET_ODDS_LIVE : Constant.REDIS_KEY.RONGHE_A99_THIRD_MARKET_ODDS_PRE;
        a99ParamConfig.getThirdMarketCategorySourceIds().forEach(categoryId -> {
            String categoryKey = categoryKeyPrefix + matchId + ":" + categoryId;
            if (redisService.hasKey(categoryKey)) {
                try {
                    redisService.tryLock(categoryKey, lockValue, 3, 3);
                    redisService.del(categoryKey);
                    log.info("A99赛事盘口缓存清除成功, Key:{}", categoryKey);
                } finally {
                    redisService.unLock(categoryKey, lockValue);
                }
            }
        });
        //清除赔率差值
        String diffKey = matchType == 0 ? RONGHE_A99_ODDS_CHANGE_DIFFERENCE_LIVE + matchId : RONGHE_A99_ODDS_CHANGE_DIFFERENCE_PRE + matchId;
        if (redisService.hasKey(diffKey)) {
            try {
                redisService.tryLock(diffKey, lockValue, 3, 3);
                redisService.del(diffKey);
                log.info("A99赛事盘口缓存清除成功, Key:{}", diffKey);
            } finally {
                redisService.unLock(diffKey, lockValue);
            }
        }
    }


}
