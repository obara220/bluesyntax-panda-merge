package com.panda.merge.job;

import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
@JobHandler(value = "MatchDelaySettle")
public class MatchDelaySettle extends IJobHandler {
    @DubboReference
    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;
    @Autowired
    MatchDelaySettleInfoMapper matchDelaySettleInfoMapper;
    @Autowired
    MatchSettleEventMapper matchSettleEventMapper;
    @Autowired
    MatchSettleScoreMapper matchSettleScoreMapper;
    @Autowired
    MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;
    @Autowired
    RedisService redisService;
    @Resource(name = "CallRedisThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public ReturnT<String> execute(String param) {
        String keyAll = "matchSettleDelayKey";
        try {
            if (redisService.tryLockOnce(keyAll, keyAll, 30)) {
                Long beginTime = System.currentTimeMillis();
                //log.info("延迟结算开始");
                MatchDelaySettleInfoExample example = new MatchDelaySettleInfoExample();
                example.createCriteria().andSettleStatusEqualTo(0).andDelayTimeLessThan(System.currentTimeMillis());
                List<MatchDelaySettleInfo> delaySettleInfoList = matchDelaySettleInfoMapper.selectByExample(example);
                if (CollectionUtils.isEmpty(delaySettleInfoList)){
                    //log.info("延迟结算MatchDelaySettleInfo数据为空");
                    return ReturnT.SUCCESS;
                }
                List<Long> eventIds = new ArrayList<>();
                List<Long> checkIds = new ArrayList<>();
                List<Long> scoreIds = new ArrayList<>();
                delaySettleInfoList.forEach(delay->{
                    if (!eventIds.contains(delay.getScoreId()) && delay.getDelayType()==2){
                        eventIds.add(delay.getScoreId());
                    }
                    if (!scoreIds.contains(delay.getScoreId()) && delay.getDelayType()==1){
                        scoreIds.add(delay.getScoreId());
                    }
                    checkIds.add(delay.getCheckInfoId());
                });
                List<MatchSettleEvent> events = null;
                if (!CollectionUtils.isEmpty(eventIds)){
                    MatchSettleEventExample eventExample = new MatchSettleEventExample();
                    eventExample.createCriteria().andIdIn(eventIds).andStatusNotEqualTo(3);
                     events = matchSettleEventMapper.selectByExample(eventExample);
                }
                List<MatchSettleScore> scores = null;
                if (!CollectionUtils.isEmpty(scoreIds)){
                    MatchSettleScoreExample scoreExample = new MatchSettleScoreExample();
                    scoreExample.createCriteria().andIdIn(scoreIds).andStatusNotEqualTo(3);
                     scores = matchSettleScoreMapper.selectByExample(scoreExample);
                }
                MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
                checkInfoExample.createCriteria().andIdIn(checkIds);
                List<MatchSettleCheckInfo> checkInfos =matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
                if (CollectionUtils.isEmpty(checkInfos)){
                    //log.info("延迟结算MatchSettleCheckInfo数据为空");
                    return ReturnT.SUCCESS;
                }
                //log.info("延迟结算查询总耗时:{}, event size:{}, score size: {}, checkInfo size:{}",(System.currentTimeMillis()-beginTime), eventIds.size(), scoreIds.size(), checkIds.size());
                Map<Long,MatchSettleEvent> eventMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(events)){
                    eventMap = events.stream().collect(Collectors.toMap(t->t.getId(), Function.identity()));
                }
                Map<Long,MatchSettleScore> scoreMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(scores)){
                    scoreMap = scores.stream().collect(Collectors.toMap(t->t.getId(),Function.identity()));
                }
                Map<Long,MatchSettleCheckInfo> checkInfoMap = checkInfos.stream().collect(Collectors.toMap(t->t.getId(),Function.identity()));

                // 开始处理业务逻辑
                //log.info("延迟结算开始处理业务逻辑");
                int delayInfoSize = delaySettleInfoList.size();
                List<Long> ids = new ArrayList<>();
                List<Future<List<Long>>> futures = new ArrayList<>();
                Map<String, List<MatchDelaySettleInfo>> delaySettleInfoParallels = delaySettleInfoList.stream()
                        .sorted(new Comparator<MatchDelaySettleInfo>() {
                            @Override
                            public int compare(MatchDelaySettleInfo o1, MatchDelaySettleInfo o2) {
                                int timeCompare = o1.getCreateTime().compareTo(o2.getCreateTime());
                                if (timeCompare != 0) {
                                    return timeCompare;
                                }
                                return o1.getId().compareTo(o2.getId());
                            }
                        })
                        .collect(Collectors.groupingBy(t->t.getStandardMatchId()+"-"+t.getDelayType()));
                for (Map.Entry<String, List<MatchDelaySettleInfo>> entry: delaySettleInfoParallels.entrySet()) {
                    if(!CollectionUtils.isEmpty(entry.getValue())){
                        Future<List<Long>> future = threadPoolTaskExecutor.submit(doProcess(entry.getValue(), eventMap, scoreMap, checkInfoMap));
                        futures.add(future);
                    }
                }
                // 等待所有任务完成
                for (Future<List<Long>> future : futures) {
                    try {
                        List<Long> tempIds = future.get(); // 会阻塞直到任务完成
                        ids.addAll(tempIds);
                    } catch (Exception e) {
                        log.error("延迟结算等待任务完成future:{} 报错:",future, e);
                    }
                }
                //2755 需求,假如权重不够100,则需要标记延迟核对数据 结算状态为2 避免重复跑数据
                if (!CollectionUtils.isEmpty(ids)) {
                    matchDelaySettleInfoMapper.updateMatchDelaySettleInfoList(ids, 2);
                }
                int processSize = ids.size();
                //log.info("延迟结算执行用时毫秒:{} origin size:{} process size:{} delay remain size:{}",
//                        (System.currentTimeMillis()-beginTime), delayInfoSize, processSize, delayInfoSize-processSize);
                return ReturnT.SUCCESS;
            }else {
                //log.info("延迟结算: 没有获取到redis总锁!");
            }
        }catch (Exception e){
            log.error("延迟结算异常: {}", e.getMessage());
            return ReturnT.FAIL;
        }finally {
            redisService.unLock(keyAll, keyAll);
        }
        return ReturnT.SUCCESS;
    }

    public Callable<List<Long>> doProcess(List<MatchDelaySettleInfo> matchDelaySettleInfos, Map<Long,MatchSettleEvent> eventMap, Map<Long,MatchSettleScore>scoreMap, Map<Long,MatchSettleCheckInfo>checkInfoMap) {
        return () -> {
            MatchDelaySettleInfo settleInfo = matchDelaySettleInfos.get(0);
            String key = "MatchDelaySettleInfoConsumer:" + settleInfo.getStandardMatchId() + "-" + settleInfo.getDelayType();
            List<Long> idsBasedThread = new ArrayList<>();
            for(MatchDelaySettleInfo delaySettle : matchDelaySettleInfos) {
                if (redisService.tryLock(key, key, 2, 5)){
                    try {
                        if (delaySettle.getDelayType() == 2) {
                            //假如原始数据被删除或者没找到,标记延迟数据,避免多次执行
                            if (null == eventMap.get(delaySettle.getScoreId()) || null == checkInfoMap.get(delaySettle.getCheckInfoId())) {
                                //log.info("延迟结算更新事件:{} scoreEventId:{} checkInfo:{} ",delaySettle.getId(), delaySettle.getScoreId(), delaySettle.getCheckInfoId());
                            } else {
                                //log.info("延迟结算开始结算事件:{} scoreEventId:{} checkInfo:{} ",delaySettle.getId(), delaySettle.getScoreId(), delaySettle.getCheckInfoId());
                                footballMatchScoresSettleApi.delayCheckCommonMatchSettleScoreEvent(eventMap.get(delaySettle.getScoreId()), checkInfoMap.get(delaySettle.getCheckInfoId()), true);
                            }
                        }
                        if (delaySettle.getDelayType() == 1) {
                            //假如原始数据被删除或者没找到,标记延迟数据,避免多次执行
                            if (null == scoreMap.get(delaySettle.getScoreId()) || null == checkInfoMap.get(delaySettle.getCheckInfoId())) {
                                //log.info("延迟结算更新比分:{} scoreEventId:{} checkInfo:{} ",delaySettle.getId(), delaySettle.getScoreId(), delaySettle.getCheckInfoId());
                            } else {
                                //log.info("延迟结算开始结算阶段:{} scoreEventId:{} checkInfo:{} ",delaySettle.getId(), delaySettle.getScoreId(), delaySettle.getCheckInfoId());
                                footballMatchScoresSettleApi.delayCheckCommonMatchSettleScoreEvent(scoreMap.get(delaySettle.getScoreId()), checkInfoMap.get(delaySettle.getCheckInfoId()), true);
                            }
                        }
                    } catch (Exception e) {
                        log.error("延迟结算获取锁异常:{} scoreEventId:{} checkInfo:{} ",delaySettle.getId(), delaySettle.getScoreId(), delaySettle.getCheckInfoId(), e);
                    } finally {
                        idsBasedThread.add(delaySettle.getId());
                        redisService.unLock(key, key);
                    }
                } else {
                    log.error("延迟结算延迟结算获取不到锁:{} scoreEventId:{} checkInfo:{} key:{}",delaySettle.getId(), delaySettle.getScoreId(), delaySettle.getCheckInfoId(), key);
                }
            }
            return idsBasedThread;
        };
    }
}