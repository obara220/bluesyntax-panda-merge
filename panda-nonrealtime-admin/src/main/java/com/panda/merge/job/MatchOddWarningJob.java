package com.panda.merge.job;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.model.ConfigTradeType;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.MatchOddWarningProducer;
import com.panda.merge.service.ConfigTradeTypeService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.panda.merge.config.RedisConfig.REDIS_FOUR_SECOND;


/**
 * 赔率告警
 *
 * @author : Bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.job
 * @description : TODO
 * @date: 2021-02-04 13:59
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class MatchOddWarningJob extends BaseProcessor {
    @Autowired
    public StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchOddWarningProducer matchoddWarningProducer;
    @Autowired
    private ConfigTradeTypeService configTradeTypeService;

    private static final int ONE = 60 * 1000;
    private static final int FIVE = 5 * 60 * 1000;

    @Scheduled(cron = "*/5 * * * * ?")
    public void oddsWarning() {
        String lockKey = RedisConfig.REDIS_KEY_DATABASE + "::job:oddsWarning";
        try {
            if (!redisService.tryLockOnce(lockKey, lockKey, REDIS_FOUR_SECOND)) {
                return;
            }
            String oddsWarningKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_WARNING_NEW;
            Map<String, Map<String, Object>> warningListMap = redisService.hGetAll(oddsWarningKey);
            if (!CollectionUtils.isEmpty(warningListMap)) {
                for (Map.Entry<String, Map<String, Object>> entry : warningListMap.entrySet()) {
                    if (entry.getKey().equals("null")) {
                        redisService.hDel(oddsWarningKey, entry.getKey());
                        continue;
                    }
                    String[] key = entry.getKey().split("_");
                    Long standardMatchId = Long.parseLong(key[0]);
                    Long marketCategoryId = Long.parseLong(key[1]);
                    Map<String, Object> infoMap = entry.getValue();
                    if (infoMap.isEmpty()) {
                        redisService.hDel(oddsWarningKey, entry.getKey());
                        continue;
                    }
                    Boolean sign = (Boolean) infoMap.get("sign");
                    //true 发送一次不再下发，赔率主流程会刷新
                    if (sign) {
                        continue;
                    }
                    Long time = (Long) infoMap.get("time");
                    StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
                    if (null == standardMatchInfo) {
                        redisService.hDel(oddsWarningKey, entry.getKey());
                        continue;
                    }
                    //完赛清除缓存
                    if (YesNoEnum.Y.value.equals(standardMatchInfo.getMatchOver())) {
                        redisService.hDel(oddsWarningKey, entry.getKey());
                        continue;
                    }
                    //赛事未到LIVE状态、中场休息阶段 不下发
                    if (!standardMatchInfo.getMatchStatus().equals(MatchStatusEnum.Live.value) || Constant.FOOT_BALL_PERIOD_FILTER_WARNING.contains(standardMatchInfo.getMatchPeriodId())) {
                        String linkId = IdWorker.getId() + "_PERIOD_REST";
                        liftedMatchOddsWarning(linkId, standardMatchInfo, marketCategoryId, entry.getKey());
                        continue;
                    }
                    //自动关盘的玩法不下发
                    String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
                    Object autoCloseMap = redisService.hGet(autoCloseRedisKey, String.valueOf(marketCategoryId));
                    if (!Objects.isNull(autoCloseMap)) {
                        String linkId = IdWorker.getId() + "_AUTO_CLOSE_MARKET";
                        liftedMatchOddsWarning(linkId, standardMatchInfo, marketCategoryId, entry.getKey());
                        continue;
                    }
                    //手动模式不下发
                    ConfigTradeType itemCategory = configTradeTypeService.getItemCategory(standardMatchInfo.getId().toString(), marketCategoryId.toString());
                    if (itemCategory != null && (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.MANUAL.equals(itemCategory.getTradeType()) || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_MODEL.equals(itemCategory.getTradeType()) || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.L_NEW.equals(itemCategory.getTradeType()))) {
                        String linkId = IdWorker.getId() + "_TRADE";
                        liftedMatchOddsWarning(linkId, standardMatchInfo, marketCategoryId, entry.getKey());
                        continue;
                    }

                    //系统时间 - 赔率时间 一分钟赔率未更新发送赔率告警给风控
                    long nowTime = TimeUtils.millsSecondsEast8ZoneGmt();
                    //TRUE 5分钟报警 false 1 分钟报警
                    boolean contains = MarginCategoryConfig.FIVE_MATCH_CATEGORY_ODDS_WARNING.contains(marketCategoryId);
                    long warningTime = contains ? FIVE : ONE;
                    if (nowTime - time >= warningTime) {
                        String linkId = IdWorker.getId() + "_MATCH_ODDS_WARNING";
                        infoMap.put("sign", true);
                        redisService.hSet(oddsWarningKey, entry.getKey(), infoMap);
                        matchoddWarningProducer.sendMatchOddsWarningRisk(linkId, standardMatchId, marketCategoryId, true);
                    }
                }
            }
        } catch (Exception e) {
            log.error("【MatchOddWarningJob 赔率告警任务异常】 Exception:", e);
            redisService.unLock(lockKey, lockKey);
        }
    }

    public void liftedMatchOddsWarning(String linkId, StandardMatchInfo standardMatchInfo, Long marketCategoryId, String Key) {
        Map<String, Object> objectMap = new HashMap<>();
        //足球主流玩法报警机制
        String oddsWarningKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_WARNING_NEW;
        Object obj = redisService.hGet(oddsWarningKey, Key);
        if (!Objects.isNull(obj)) {
            objectMap = (Map<String, Object>) obj;
        }
        if (!CollectionUtils.isEmpty(objectMap)) {
            boolean sign = (boolean) objectMap.get("sign");
            if (sign) {
                //下发风控解除报警 false
                matchoddWarningProducer.sendMatchOddsWarningRisk(linkId, standardMatchInfo.getId(), marketCategoryId, false);
                objectMap.put("sign", false);
                objectMap.put("time", TimeUtils.millsSecondsEast8ZoneGmt());
                redisService.hSet(oddsWarningKey, Key, objectMap);
                log.info("::{}::标准赛事ID:{},标准玩法ID:{},自动关盘玩法解除告警", linkId, standardMatchInfo.getId(), marketCategoryId);
            }
        }
    }
}
