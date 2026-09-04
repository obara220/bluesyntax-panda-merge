package com.panda.merge.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.MatchOrderCountDTO;
import com.panda.merge.dto.scores.EditScoreResultStatusRequest;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;


/**
 * 大数据下发：赛事注单数
 * 无注单的赛事将自动关闭赛果开关
 * 需求4117
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_RESULT_ORDER_COUNT_INFO",
        consumerGroup = "scores_group_MATCH_RESULT_ORDER_COUNT_INFO",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class MatchOrderCountConsumer implements RocketMQListener<List<MatchOrderCountDTO>> {

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    RedisService redisService;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
//    /**
//     *
//     * @param s
//     * 下发的数据为已结束并且注单数为0的赛事
//     */
//    @Override
//    public void onMessage(String s) {
//        log.info("MatchOrderCountConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
//        if (datacenterMergeSwitch) {
//            //MQ消息转发给数据中心
//            commonProducer.asyncSend(s, "datacenter-MATCH_RESULT_ORDER_COUNT_INFO",System.currentTimeMillis()+"");
//            return;
//        }
//        StopWatch extWatch = new StopWatch();
//        extWatch.start();
//        if(StrUtil.isEmpty(s)){
//            return;
//        }
//        log.info("无注单自动关闭赛果开关：{}",s);
//        try{
//            //[{"linkId":"76567a0a-784b-419a-b09c-5d30668e880a","timestamp":1755334742428,"mid":3904624,"orderCount":1}]
//            JSONArray array = new JSONArray(s) ;
//            List<MatchOrderCountDTO> list = array.toList(MatchOrderCountDTO.class);
//            if(list.isEmpty()){
//                return;
//            }
//            String key = "AUTO_CLOSE_RESULT_SHOW_SWITCH";
//            String redisMatchIds = "";
//            if(redisService.get(key)!=null){
//                redisMatchIds = (String) redisService.get(key);
//            }
//            List<Long> matchIds = new ArrayList<>();
//            StringBuilder idsStr = new StringBuilder();
//            for (MatchOrderCountDTO dto : list){
//                if(redisMatchIds.contains(dto.getMid().toString())){
//                    continue;
//                }
//                Integer count = dto.getOrderCount();
//                if(count!=0){
//                    return;
//                }
//                matchIds.add(dto.getMid());
//                idsStr.append(dto.getMid()).append(",");
//            }
//            //刷新缓存
//            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellService.getItems(matchIds);
//            if(standardSportMarketSells.isEmpty()){
//                log.info("无注单自动关闭赛果开关：无赛事：{}",matchIds);
//                return;
//            }
//            //发送MQ通知业务侧全部显示/隐藏
//            for (StandardSportMarketSell match : standardSportMarketSells) {
//                EditScoreResultStatusRequest request = new EditScoreResultStatusRequest();
//                request.setStatus(0);
//                //定义类型为2 无注单自动关闭赛果展示
//                request.setType(2);
//                request.setStandardMatchId(match.getMatchInfoId());
//                request.setSportId(match.getSportId());
//                scoresProducer.sendMatchShowStatus(request,match.getMatchInfoId()+"_order");
//                match.setShowResultStatus(0);
//                standardSportMarketSellService.refreshCache(match);
//            }
//            //数据入库
//            if(!matchIds.isEmpty()){
//                standardSportMarketSellMapper.updateShowResultStatusAll(matchIds,System.currentTimeMillis(),0);
//                log.info("无注单自动关闭赛果开关：{}",matchIds);
//            }
//            if(StringUtils.isNotEmpty(idsStr)){
//                redisService.set(key,redisMatchIds+","+matchIds, RedisConfig.REDIS_HOUR_TIME);
//            }
//            extWatch.stop();
//            log.info("无注单自动关闭赛果开关：{},用时:{}",matchIds,extWatch.getTotalTimeMillis());
//       }catch (Exception e){
//           log.error("无注单自动关闭赛果开关异常：{}",e.getMessage(),e);
//       }
//    }



    @Override
    public void onMessage(List<MatchOrderCountDTO> matchOrderCountDTOS) {
        log.info("MatchOrderCountConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(matchOrderCountDTOS, "datacenter-MATCH_RESULT_ORDER_COUNT_INFO",System.currentTimeMillis()+"");
            return;
        }
        StopWatch extWatch = new StopWatch();
        extWatch.start();
        if(matchOrderCountDTOS.isEmpty()){
            log.info("无数据==============================");
            return;
        }
        log.info("无注单自动关闭赛果开关，原始数据：{}",matchOrderCountDTOS);
        try{
            String key = "AUTO_CLOSE_RESULT_SHOW_SWITCH";
            String redisMatchIds = "";
            if(redisService.get(key)!=null){
                redisMatchIds = (String) redisService.get(key);
            }
            List<Long> matchIds = new ArrayList<>();
            StringBuilder idsStr = new StringBuilder();
            for (MatchOrderCountDTO dto : matchOrderCountDTOS){
                if(redisMatchIds.contains(dto.getMid().toString())){
                    continue;
                }
                Integer count = dto.getOrderCount();
                if(count!=0){
                    return;
                }
                matchIds.add(dto.getMid());
                idsStr.append(dto.getMid()).append(",");
            }
            //刷新缓存
            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellService.getItems(matchIds);
            if(standardSportMarketSells.isEmpty()){
                log.info("无注单自动关闭赛果开关：无赛事：{}",matchIds);
                return;
            }
            //发送MQ通知业务侧全部显示/隐藏
            for (StandardSportMarketSell match : standardSportMarketSells) {
                EditScoreResultStatusRequest request = new EditScoreResultStatusRequest();
                request.setStatus(0);
                //定义类型为2 无注单自动关闭赛果展示
                request.setType(2);
                request.setStandardMatchId(match.getMatchInfoId());
                request.setSportId(match.getSportId());
                scoresProducer.sendMatchShowStatus(request,match.getMatchInfoId()+"_order");
                match.setShowResultStatus(0);
                standardSportMarketSellService.refreshCache(match);
            }
            //数据入库
            if(!matchIds.isEmpty()){
                standardSportMarketSellMapper.updateShowResultStatusAll(matchIds,System.currentTimeMillis(),0);
                log.info("无注单自动关闭赛果开关：{}",matchIds);
            }
            if(StringUtils.isNotEmpty(idsStr)){
                redisService.set(key,redisMatchIds+","+matchIds, RedisConfig.REDIS_HOUR_TIME);
            }
            extWatch.stop();
            log.info("无注单自动关闭赛果开关：{},用时:{}",matchIds,extWatch.getTotalTimeMillis());
        }catch (Exception e){
            log.error("无注单自动关闭赛果开关异常：{}",e.getMessage(),e);
        }
    }

}
