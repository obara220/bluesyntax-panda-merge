package com.panda.merge.rocketmq.consumer;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.dto.ThirdMatchLineupSimpleDTO;
import com.panda.merge.dto.ThirdMatchPhraseDetail;
import com.panda.merge.mapper.ThirdMatchPhraseMapper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.processor.SoldMessageToEventProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.rocketmq.producer.ThirdMatchPhraseInfoProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 赛程项目操作【三方赛事绑定标准赛事】通知,滚球中途关联补发历史事件
 *  单号：86107	【产品】【生产】赛事滚球中关联三方数据商，关联之前的事件也需要补发至结算
 * @author :  tell
 * @since 2025年3月17日17:13:03
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MATCH_OPERATE_MSG,
        consumerGroup = CONSUME_REALTIME_GROUP + MATCH_OPERATE_MSG,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchJoinStandardMatchConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private SoldMessageToEventProcessor soldMessageToEventProcessor;

    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
    @Resource
    private DataCenterProducer dataCenterProducer;

//    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(MessageExt ext) {
        if (!realtimeSwitch && !realtimeEventSwitch) {
//            if (dataCenterProducer.checkForward(ext,MATCH_OPERATE_MSG)) {
                //ThirdMatchRefreshCacheConsumer来进行转发
//                dataCenterProducer.send(ext,MATCH_OPERATE_MSG);
                return;
//            }
        }
        String linkId = null;
        try {
            linkId = MATCH_OPERATE_MSG + "_" + ext.getProperties().get("KEYS");
            log.info("linkId=【" + linkId + "】【" + MATCH_OPERATE_MSG + "】【赛程项目操作】【三方赛事绑定标准赛事】滚球中途关联补发历史事件开始");
            /** {"operateType":1,"thirdMatchIds":[1853160573632516098],"standardMatchId":3770749}*/
            String message = new String(ext.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(message)) {
                log.info("linkId=【" + linkId + "】【" + MATCH_OPERATE_MSG + "】接收到数据为：{}", message);
            } else {
                JSONObject jsonObject = JSON.parseObject(message);
                //标准赛事信息
                Long standardMatchId = jsonObject.getLong("standardMatchId");
                //获取标准赛事信息
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchId);
                if (null != standardMatchInfo) {
                    //刷新开售缓存并返回最新开售信息
                    StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(standardMatchId);
                    if (null != standardSportMarketSell) {
                        //三方赛事列表
                        JSONArray thirdMatchIds = jsonObject.getJSONArray("thirdMatchIds");
                        if (!CollectionUtils.isEmpty(thirdMatchIds)) {
                            List<Long> list = thirdMatchIds.toJavaList(Long.class);
                            for (Long thirdMatchId : list) {
                                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItemByPrimaryKey(thirdMatchId);
                                //兜底兼容三方赛事信息还未入库成功
                                if (null == thirdMatchInfo.getReferenceId() || thirdMatchInfo.getReferenceId() == 0) {
                                    Thread.sleep(2000);
                                    thirdMatchInfo = thirdMatchInfoService.getItemByPrimaryKey(thirdMatchId);
                                }
                                String matchPeriod = thirdMatchInfo.getMatchPeriod();
                                if(StringUtils.isNotBlank(matchPeriod) && Integer.valueOf(matchPeriod) > 0){
                                    soldMessageToEventProcessor.matchEvent2StandardMatch(linkId,thirdMatchInfo);
                                }else{
                                    log.info("linkId=【" + linkId + "】【" + MATCH_OPERATE_MSG + "】,当前数据源未开赛,无需处理,源赛事ID={},数据源编码={}", linkId, thirdMatchInfo.getThirdMatchSourceId(),thirdMatchInfo.getDataSourceCode());
                                }
                                //赛事分析相关数据特殊处理
                                if(DataSourceCodeEnum.getAnalysisCodeList().contains(thirdMatchInfo.getDataSourceCode())){
                                    sendThirdMatchPhraseInfo(thirdMatchInfo, linkId);
                                }
                            }
                        }
                    }else{
                        log.info("linkId=【" + linkId + "】【" + MATCH_OPERATE_MSG + "】,预开售信息为空,标准赛事id={}", standardMatchId);
                    }
                } else {
                    log.info("linkId=【" + linkId + "】【" + MATCH_OPERATE_MSG + "】,查询标准赛事为空,标准赛事id={}", standardMatchId);
                }
            }
        } catch (Exception e) {
            log.error("linkId=【" + linkId + "】【" + MATCH_OPERATE_MSG + "】【赛程项目操作】【三方赛事绑定标准赛事】滚球中途关联补发历史事件异常:Exception", e);
        } finally {
            log.info("linkId=【" + linkId + "】【" + MATCH_OPERATE_MSG + "】【赛程项目操作】【三方赛事绑定标准赛事】滚球中途关联补发历史事件结束");
        }
    }

    @Autowired
    private ThirdMatchPhraseMapper thirdMatchPhraseMapper;
    @Autowired
    private ThirdMatchPhraseInfoProducer thirdMatchPhraseInfoProducer;

    /**
     * 推送当前赛事历史文字直播数据
     * */
    private void sendThirdMatchPhraseInfo(ThirdMatchInfo thirdMatchInfo, String linkId){
        //============需要补发的文字直播开始============
        ThirdMatchPhraseExample example = new ThirdMatchPhraseExample();
        example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId())
                .andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode()).andSendDataEqualTo(ZERO);
        List<ThirdMatchPhrase> thirdMatchPhrases = thirdMatchPhraseMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(thirdMatchPhrases)){
            //按时间升序
            thirdMatchPhrases = thirdMatchPhraseMapper.selectByExample(example).stream().sorted(Comparator.comparing(ThirdMatchPhrase::getTime).reversed()).collect(Collectors.toList());
            for (ThirdMatchPhrase thirdMatchPhrase: thirdMatchPhrases) {
                ThirdMatchPhraseDetail thirdMatchPhraseDetail = new ThirdMatchPhraseDetail();
                BeanUtil.copyProperties(thirdMatchPhrase, thirdMatchPhraseDetail);
                thirdMatchPhraseDetail.setThirdMatchId(thirdMatchInfo.getId());
                thirdMatchPhraseDetail.setStandardMatchId(thirdMatchInfo.getReferenceId());
                thirdMatchPhraseInfoProducer.pushThirdMatchPhraseInfo(linkId,thirdMatchPhraseDetail);
            }
            ThirdMatchPhrase upItem = new ThirdMatchPhrase();
            upItem.setSendData(ONE);
            thirdMatchPhraseMapper.updateByExampleSelective(upItem,example);
        }
        //============需要补发的文字直播结束============
    }
}
