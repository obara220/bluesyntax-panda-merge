package com.panda.merge.job;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.StandardMatchPreResultProducer;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.panda.merge.config.RedisConfig.REDIS_FOUR_SECOND;

/**
 * 提前结算是否关盘检测（定时任务方式每5秒检查一次，为 ( 系统时间-数据最新更新时间 )>＝设置值）
 * 判断数据商,5s是否下发数据，并且推送下游做关盘处理
 *
 * @author : Edison
 * @project Name : panda-merge
 * @package Name : com.panda.merge.job
 * @description : TODO
 * @date: 2022-03-18 13:00
 * @modificationHistory OGBK
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class CheckStandardMatchMarketJob extends BaseProcessor {
    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardMatchPreResultProducer standardMatchPreResultProducer;

    //定义过期时间
    @NacosValue(value = "${category.warning.time}", autoRefreshed = true)
    private String closeTime;
    /**
     * 提前结算开关，false关，true开
     */
    @NacosValue(value = "${market.pre.switch}", autoRefreshed = true)
    private boolean marketPreSwitch;
    //设置每5s检测一次
//    @Scheduled(cron = "*/5 * * * * ?")
    public void check() {
        if (!marketPreSwitch) {
            //log.info("提前结算NACOS关,定时任务不处理");
            return;
        }
        String lockKey = RedisConfig.REDIS_KEY_DATABASE + "::job:checkStandardMatch";

        try {
            if (!redisService.tryLockOnce(lockKey, lockKey, REDIS_FOUR_SECOND)) {
                return;
            }
//            //log.info("【CheckStandardMatchMarketJob 提前结算,获取NACOS定时时间为：{}】", closeTime);
            JSONObject closeTimeObj = JSONObject.parseObject(closeTime);

            //提前结算是否关盘检测缓存 Map<标准盘口ID，标准提前结算盘口参数>
            String checkStandardMatchInfo = Constant.REDIS_KEY.CHECK_STANDARD_MATCH_INFO;
            Map<String, String> checkMap = redisService.hGetAll(checkStandardMatchInfo);

            if (!CollectionUtils.isEmpty(checkMap)) {
                for (Map.Entry<String, String> entry : checkMap.entrySet()) {
                    //获取赛事ID
                    Long standardMatchId = Long.parseLong(String.valueOf(entry.getKey()));
                    //链路ID
                    String linkId = IdWorker.getId() + "_warning";
                    //查询标准赛事
                    StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);

                    if (null == standardMatchInfo) {
                        redisService.hDel(checkStandardMatchInfo, standardMatchId);
//                        //log.info("::{}::【CheckStandardMatchMarketJob 提前结算是否关盘检测任务失败,标准赛事不存在:{}】", linkId, standardMatchId);
                        continue;
                    }
                    //赛事未到LIVE状态、中场休息阶段 不下发
                    if (!standardMatchInfo.getMatchStatus().equals(MatchStatusEnum.Live.value)
                            || Constant.FOOT_BALL_PERIOD_FILTER_WARNING.contains(standardMatchInfo.getMatchPeriodId())) {
//                        //log.info("::{}::【CheckStandardMatchMarketJob 提前结算,赛事处于LIVE状态、中场休息阶段,不下发:{}】", linkId, standardMatchId);
                        continue;
                    }

                    //清除缓存(判定条件:比赛是否结束)
                    if (YesNoEnum.Y.value.equals(standardMatchInfo.getMatchOver())) {
//                        //log.info("::{}::【CheckStandardMatchMarketJob 提前结算是否关盘检测任务,清除提前结算辅助信息缓存，标准赛事ID：{}】", linkId, standardMatchId);
                        redisService.hDel(checkStandardMatchInfo, standardMatchId);
                        continue;
                    }

                    //提前结算概率标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
                    String standardPreMarketKey = Constant.REDIS_KEY.STANDARD_MARKET_PRE_RESULT + standardMatchId;

                    //提前结算概率标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
                    Map<String, StandardMatchMarketPreResultMessage>
                            standardMatchMarketPreResultMessageMap = redisService.hGetAll(standardPreMarketKey);
                    //判定提前结算列表是否存在
                    if (CollectionUtils.isEmpty(standardMatchMarketPreResultMessageMap) || 0 == standardMatchMarketPreResultMessageMap.size()) {
//                        //log.info("【::{}::CheckStandardMatchMarketJob 提前结算是否关盘检测任务失败,提前结算盘口不存在,赛事信息为：{}】", linkId, standardMatchId);
                        continue;
                    }
                    //系统时间 - 获取参数时间 > 8s 表示 （8s内上游没有再下发数据，直接关盘）
                    long nowTime = TimeUtils.millsSecondsEast8ZoneGmt();
                    Set<String> standardMatchMarketPreResultMessage = standardMatchMarketPreResultMessageMap.keySet();

                    List<StandardMatchMarketPreResultMessage> marketPreResultMessageList = new ArrayList<>();
                    //循环并更改 盘口提前结算开关
                    for (String key : standardMatchMarketPreResultMessage) {
                        StandardMatchMarketPreResultMessage marketMessage = standardMatchMarketPreResultMessageMap.get(key);
                        //判定thirdSportSendTime是否为空,为空不进行后面流程
                        if (null == marketMessage.getThirdSportSendTime() || 0L == marketMessage.getThirdSportSendTime()) {
//                            //log.info("【::{}::CheckStandardMatchMarketJob 提前结算是否关盘检测任务失败,第三方数据商下发数据时间为空,结束后续流程:{}】", linkId, marketMessage);
                            continue;
                        }
                        Long marketCategoryId = marketMessage.getMarketCategoryId();
                        Long warningTime = Long.valueOf(closeTimeObj.get(marketCategoryId.toString()).toString());                        //判断时间,盘口滚球状态,数据商盘口状态
                        //判断时间,盘口滚球状态,数据商盘口状态
                        if (0 == marketMessage.getMarketType() && nowTime - marketMessage.getThirdSportSendTime() >= warningTime
                                && 1 == marketMessage.getCashOutStatus()) {
                            //对盘口状态进行更改
                            marketMessage.setCashOutStatus(-1);
                            redisService.hSet(standardPreMarketKey, key, marketMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                            marketPreResultMessageList.add(marketMessage);
//                            //log.info("【::{}::CheckStandardMatchMarketJob 提前结算盘口信息更改成功:{}】", linkId, marketMessage);

                        }
                    }
                    //下发数据
                    if (!CollectionUtils.isEmpty(marketPreResultMessageList)) {
                        standardMatchPreResultProducer.sendStandardMatchPreResult(linkId, standardMatchInfo, standardMatchInfo.getSportId(),
                                marketPreResultMessageList, marketPreResultMessageList.get(0).getMatchPreStatus(), System.currentTimeMillis());
                        //log.info("::{}::CheckStandardMatchMarketJob 提前结算MQ数据下发成功,标准赛事id:{}", linkId, standardMatchInfo.getId());

                    } else {
//                        //log.info("::{}::CheckStandardMatchMarketJob 提前结算,marketPreResultMessageList为空,标准赛事id:{}", linkId, standardMatchInfo.getId());
                        }
                }
            }
        } catch (Exception e) {
            redisService.unLock(lockKey, lockKey);
            log.error("CheckStandardMatchMarketJob 【提前结算任务异常】 Exception:", e);
        }
    }
}
