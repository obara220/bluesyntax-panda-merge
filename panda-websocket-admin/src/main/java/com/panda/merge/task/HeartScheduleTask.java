package com.panda.merge.task;

import com.panda.merge.cache.MyCacheService;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.request.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;

@Configuration
@Slf4j
public class HeartScheduleTask {

    @Autowired
    RedisService redisService;
    @Autowired

    //3.添加定时任务 3600秒
    @Scheduled(cron = "1 * * * * ?")
    private void configureTasks() {
            log.info("执行静态定时任务时间: " + LocalDateTime.now());

        log.warn("客户端连接 sessionIdPdMatchMap 大小: " + MyCacheService.sessionIdPdMatchMap.size());
        log.warn("客户端连接 sessionIdSettleMatchMap 大小: " + MyCacheService.sessionIdSettleMatchMap.size());
        log.warn("客户端连接 sessionIdSettleMatchListMap 大小: " + MyCacheService.sessionIdSettleMatchListMap.size());
        log.warn("客户端连接 sessionIdAutoSettleDataSourceMap 大小: " + MyCacheService.sessionIdAutoSettleDataSourceMap.size());
        log.warn("客户端连接 sessionIdMatchSettleRollBackSourceMap 大小: " + MyCacheService.sessionIdMatchSettleRollBackSourceMap.size());
        log.warn("客户端连接 sessionIdMatchScoreMap 大小: " + MyCacheService.sessionIdMatchScoreMap.size());
        log.warn("客户端连接 sessionOperatorOnlineMap 大小: " + MyCacheService.sessionOperatorOnlineMap.size());
        Iterator<PdSubCacheVo> it= MyCacheService.sessionIdPdMatchMap.values().iterator();
        while ( it.hasNext()){
            PdSubCacheVo channel= it.next();
            String channelId= channel.getSessionId();
            if(channel.getCreateTime()<System.currentTimeMillis()-60*1000){
                log.warn("失去客户端连接 channel: " + channelId);
                //报球版的订阅通知清理
                MyCacheService.sessionIdPdMatchMap.remove(channelId);

                try {
                    channel.getSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("configureTasks-PdSubCacheVo:",e);
                }
            }
        }

        Iterator<SettleMatchSubCacheVo> it2= MyCacheService.sessionIdSettleMatchMap.values().iterator();
        while ( it2.hasNext()){
            SettleMatchSubCacheVo channel= it2.next();
            String channelId= channel.getSessionId();
            if(channel.getCreateTime()<System.currentTimeMillis()-60*1000){
                log.warn("失去客户端连接 channel: " + channelId);
                //结算的订阅清理
                MyCacheService.sessionIdSettleMatchMap.remove(channelId);
                try {
                    channel.getSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("configureTasks-SettleMatchSubCacheVo:",e);
                }
            }
        }

        Iterator<SettleMatchListSubCacheVo> it3= MyCacheService.sessionIdSettleMatchListMap.values().iterator();
        while ( it3.hasNext()){
            SettleMatchListSubCacheVo channel= it3.next();
            String channelId= channel.getSessionId();
            if(channel.getCreateTime()<System.currentTimeMillis()-60*1000){
                log.warn("失去客户端连接 channel: " + channelId);
                //结算赛事列表的订阅清理
                MyCacheService.sessionIdSettleMatchListMap.remove(channelId);
                try {
                    channel.getSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("configureTasks-SettleMatchListSubCacheVo:",e);
                }
            }
        }

        Iterator<AutoSettleDataSourceSubCacheVo> it4= MyCacheService.sessionIdAutoSettleDataSourceMap.values().iterator();
        while ( it4.hasNext()){
            AutoSettleDataSourceSubCacheVo channel= it4.next();
            String channelId= channel.getSessionId();
            if(channel.getCreateTime()<System.currentTimeMillis()-60*1000){
                log.warn("失去客户端连接 channel: " + channelId);
                //数据商自动结算开关订阅清理
                MyCacheService.sessionIdAutoSettleDataSourceMap.remove(channelId);
                try {
                    channel.getSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("configureTasks-AutoSettleDataSourceSubCacheVo:",e);
                }
            }
        }

        Iterator<MatchSettleRollBackVo> it5= MyCacheService.sessionIdMatchSettleRollBackSourceMap.values().iterator();
        while ( it5.hasNext()){
            MatchSettleRollBackVo channel= it5.next();
            String channelId= channel.getSessionId();
            if(channel.getCreateTime()<System.currentTimeMillis()-60*1000){
                log.warn("失去客户端连接 channel: " + channelId);
                //赛事回滚状态订阅清理
                MyCacheService.sessionIdMatchSettleRollBackSourceMap.remove(channelId);
                try {
                    channel.getSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("configureTasks-MatchSettleRollBackVo:",e);
                }
            }
        }
        Iterator<StandardMatchScoreCatchVo> it6= MyCacheService.sessionIdMatchScoreMap.values().iterator();
        while ( it6.hasNext()){
            StandardMatchScoreCatchVo channel= it6.next();
            String channelId= channel.getSessionId();
            if(channel.getCreateTime()<System.currentTimeMillis()-60*1000){
                log.warn("失去客户端连接 channel: " + channelId);
                //赛事回滚状态订阅清理
                MyCacheService.sessionIdMatchScoreMap.remove(channelId);
                try {
                    channel.getSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("configureTasks-StandardMatchScoreCatchVo:",e);
                }
            }
        }

        Iterator<OperatorOnlineCatchVo> it7= MyCacheService.sessionOperatorOnlineMap.values().iterator();
        while ( it6.hasNext()){
            OperatorOnlineCatchVo channel= it7.next();
            String channelId= channel.getSessionId();
            if(channel.getCreateTime()<System.currentTimeMillis()-60*1000){
                log.warn("失去客户端连接 channel: " + channelId);
                //赛事回滚状态订阅清理
                MyCacheService.sessionOperatorOnlineMap.remove(channelId);
                try {
                    channel.getSession().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("configureTasks-OperatorOnlineCatchVo:",e);
                }
            }
        }
        System.gc();
        log.info("执行静态定时任务结束时间: " + LocalDateTime.now());

    }

}
