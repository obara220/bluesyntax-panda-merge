package com.panda.merge.rocketmq.consumer;


import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.ChangeMatchOverMessage;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.processor.ThirdMatchRefreshCacheProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.MatchEventInfoService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 赛程项目操作【手工开赛和完赛】通知刷新缓存
 * @author :  idol
 * @since 2020年12月26日19:06:54
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = CHANGE_MATCH_OVER,
        consumerGroup = CONSUME_NONREALTIME_GROUP + CHANGE_MATCH_OVER,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdChangeMatchOverConsumer implements RocketMQListener<Request<ChangeMatchOverMessage>> {

    @Autowired
    private ThirdMatchRefreshCacheProcessor thirdMatchRefreshCacheProcessor;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;
//    @Autowired
//    private MatchEventInfoService matchEventInfoService;
    @Autowired
    public RedisService redisService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ChangeMatchOverMessage> dataCenterProducer;

    @Override
    public void onMessage(Request<ChangeMatchOverMessage> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,CHANGE_MATCH_OVER);
            return;
        }
        ChangeMatchOverMessage data = request.getData();
        log.info("::{}::"+CHANGE_MATCH_OVER+",【赛程项目操作】【手工开赛和完赛】通知刷新缓存开始,传入参数: {}", request.getLinkId(), JSON.toJSONString(data));
        //标准赛事下全部三方赛事列表
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(data.getMatchId());
        if(!CollectionUtils.isEmpty(thirdMatchInfos)){
            //======================刷新三方赛事相关缓存开始======================
            for (ThirdMatchInfo thirdMatchInfo: thirdMatchInfos) {
                thirdMatchRefreshCacheProcessor.matchRefreshCache(request.getLinkId(),thirdMatchInfo.getId(),data.getMatchId());
            }
            //======================刷新三方赛事相关缓存结束======================

            //======================因数据商赛事状态，赛事阶段下发错误导致赛事提前完赛再手工开赛处理业务逻辑开始=================================
            //如果是手工开赛
            if(ZERO.equals(data.getMatchOver())){
                //获取当前标准赛事
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(data.getMatchId());
                log.info("::{}::"+CHANGE_MATCH_OVER+",当前标准赛事信息:{}", request.getLinkId(), JSON.toJSONString(standardMatchInfo));
                if(null != standardMatchInfo){
                    //当前标准赛事状态=3（完赛）或者 赛事阶段为999
                    if(MatchStatusEnum.Ended.value.equals(standardMatchInfo.getMatchStatus()) || MatchPeriodForMatchOverEnum.Ended999.value.equals(standardMatchInfo.getMatchPeriodId())){
                        //获取最新开售信息
                        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(data.getMatchId());
                        if (null != standardSportMarketSell) {
                            log.info("::{}::"+CHANGE_MATCH_OVER+",当前赛事开售信息:{}", request.getLinkId(), JSON.toJSONString(standardSportMarketSell));
                            //========================赛事事件处理开始========================
                            //赛事事件源服务商
                            String businessEventSourceCode = StringUtils.isNotBlank(standardSportMarketSell.getBusinessEvent()) ? standardSportMarketSell.getBusinessEvent() : standardMatchInfo.getDataSourceCode();;
                            //当前事件源对应的三方赛事信息
                            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(data.getMatchId(), businessEventSourceCode);
                            log.info("::{}::"+CHANGE_MATCH_OVER+",当前事件源编码对应的三方赛事信息:{}", request.getLinkId(), JSON.toJSONString(thirdMatchInfo));
                            //根据三方数据源赛事ID，赛事阶段为999，事件编码为match_status，是否取消为0(未取消) 查询需要取消的事件列表
                            MatchEventInfoExample matchEventInfoExample = new MatchEventInfoExample();
                            matchEventInfoExample.createCriteria()
                                    .andDataSourceCodeEqualTo(businessEventSourceCode)
                                    .andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId())
                                    .andMatchPeriodIdEqualTo(MatchPeriodForMatchOverEnum.Ended999.value)
                                    .andEventCodeEqualTo(EventCodeEnum.MATCH_STATUS.code)
                                    .andCanceledEqualTo(YesNoEnum.N.value);
                            //需要取消的事件列表（取消掉错误的999事件，三方事件接收处就能正常接收后续的事件）
                            List<MatchEventInfo> matchEventInfos = matchEventInfoMapper.selectByExample(matchEventInfoExample);
                            //需要取消的事件列表（取消掉错误的999事件，三方事件接收处就能正常接收后续的事件）
//                            List<MatchEventInfo> matchEventInfos = matchEventInfoService.getMatchEventInfoByThird(MatchPeriodForMatchOverEnum.Ended999.value, EventCodeEnum.MATCH_STATUS.code
//                                    , thirdMatchInfo.getThirdMatchSourceId(), businessEventSourceCode, YesNoEnum.N.value);
                            if(!CollectionUtils.isEmpty(matchEventInfos)){
                                MatchEventInfo upMatchEventInfo = new MatchEventInfo();
                                upMatchEventInfo.setCanceled(YesNoEnum.Y.value);
                                upMatchEventInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                                matchEventInfoMapper.updateByExampleSelective(upMatchEventInfo,matchEventInfoExample);
//                                for (MatchEventInfo matchEventInfo: matchEventInfos) {
//                                    matchEventInfo.setCanceled(YesNoEnum.Y.value);
//                                    matchEventInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//                                }
//                                matchEventInfoService.updateBatch(matchEventInfos);
                                log.info("::{}::"+CHANGE_MATCH_OVER+",取消的事件列表: {}", request.getLinkId(),JSON.toJSONString(matchEventInfos));
                            }
                            //========================赛事事件处理结束========================
                            //========================赛事状态,赛事阶段处理开始========================
                            //赛事状态源服务商
                            String matchStatusSourceCode = StringUtils.isNotBlank(standardSportMarketSell.getMatchStatusSourceCode()) ? standardSportMarketSell.getMatchStatusSourceCode():standardMatchInfo.getDataSourceCode();
                            if(!businessEventSourceCode.equals(matchStatusSourceCode)){
                                //当前赛事状态源对应的三方赛事信息
                                thirdMatchInfo = thirdMatchInfoService.getItem(data.getMatchId(), matchStatusSourceCode);
                                log.info("::{}::"+CHANGE_MATCH_OVER+",当前状态源编码对应的三方赛事信息:{}", request.getLinkId(), JSON.toJSONString(thirdMatchInfo));
                            }
                            //最新开售状态源对应的三方赛事的赛事状态覆盖标准赛事的赛事状态
                            if(!thirdMatchInfo.getMatchStatus().equals(standardMatchInfo.getMatchStatus())){
                                StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
                                upStandardMatchInfo.setId(standardMatchInfo.getId());
                                upStandardMatchInfo.setMatchStatus(thirdMatchInfo.getMatchStatus());
                                if(StringUtils.isNoneBlank(thirdMatchInfo.getMatchPeriod())){
                                    upStandardMatchInfo.setMatchPeriodId(Long.valueOf(thirdMatchInfo.getMatchPeriod()));
                                }
                                //如果当前阶段是999，则设置为0，避免该赛事被定时任务完赛
                                if(MatchPeriodForMatchOverEnum.Ended999.value.equals(upStandardMatchInfo.getMatchPeriodId())){
                                    upStandardMatchInfo.setMatchPeriodId(MatchPeriodForMatchOverEnum.NOT_STARTED.value);
                                }
                                standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
                                log.info("::{}::"+CHANGE_MATCH_OVER+",修改标准赛事状态和三方赛事状态保持一致完成", request.getLinkId());
                            }
                            //========================赛事状态,赛事阶段处理结束========================
                            //清除已经自动关盘的玩法
                            String autoCloseRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + standardMatchInfo.getId());
                            redisService.del(autoCloseRedisKey);
                        }
                    }
                }
            }
            //======================因数据商赛事状态，赛事阶段下发错误导致赛事提前完赛在手工开赛处理业务逻辑结束=================================
        }else{
            log.info("::{}::"+CHANGE_MATCH_OVER+",根据标准赛事ID刷新赛事缓存开始,未找到标准赛事id{}下三方赛事信息！", request.getLinkId(), data.getMatchId());
        }
        log.info("::{}::"+CHANGE_MATCH_OVER+",【赛程项目操作】【手工开赛和完赛】通知刷新缓存结束", request.getLinkId());
    }


}
