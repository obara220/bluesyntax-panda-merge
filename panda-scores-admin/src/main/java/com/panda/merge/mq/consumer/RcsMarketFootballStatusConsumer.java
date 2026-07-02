package com.panda.merge.mq.consumer;

import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.common.enums.PandaErrorCodeEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.RcsMarketFootballStatusDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.exception.ApiException;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.MatchEventInfoMessage;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.spare.SpareBaseProducer;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.StandardSportMarketSellRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.service.ThirdSportTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.panda.merge.constant.ConstantSystem.RCS_MARKET_FOOTBALL_GOAL_STATUS;
import static com.panda.merge.constant.ConstantSystem.SCORES_EVENT_OPERATE;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;

/**
 * 2713 比中心监听自动开盘（A01下发）
 * status=0时 下发危险事件
 * fymen
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_MARKET_FOOTBALL_GOAL_STATUS",
        consumerGroup = "scores-group-"+RCS_MARKET_FOOTBALL_GOAL_STATUS,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class RcsMarketFootballStatusConsumer implements RocketMQListener<RcsMarketFootballStatusDTO> {
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent;
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;
    @Autowired
    SpareBaseProducer spareBaseProducer;
//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
//    @Autowired
//    StandardMatchInfoMapper matchInfoMapper;
    @Autowired
    private CommonAdvertiseService commonAdvertiseService;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    public ThirdSportTypeService thirdSportTypeService;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    ThirdMatchInfoRepository thirdMatchInfoRepository;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;

    /**
     * 2713 比中心监听自动开盘
     * status=0时 下发危险事件
     * @param rcsMarketFootballStatus 自动开盘数据
     */
    @Override
    public void onMessage(RcsMarketFootballStatusDTO rcsMarketFootballStatus) {
        log.info("RcsMarketFootballStatusConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(rcsMarketFootballStatus.getMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(rcsMarketFootballStatus, "datacenter-RCS_MARKET_FOOTBALL_GOAL_STATUS", rcsMarketFootballStatus.getLinkId());
            return;
        }
        if(null==rcsMarketFootballStatus){
            log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件失败,rcsMarketFootballStatus为空");
            return;
        }
        if( rcsMarketFootballStatus.getMatchId() == null){
            log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件失败,matchId不存在 {}", JSONUtil.toJsonStr(rcsMarketFootballStatus));
            return;
        }
        if(rcsMarketFootballStatus.getStatus()!=0 && rcsMarketFootballStatus.getStatus() !=2){
            log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件失败,matchId不存在或status不等于0==={}", JSONUtil.toJsonStr(rcsMarketFootballStatus));
            return;
        }
        log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件,{}", JSONUtil.toJsonStr(rcsMarketFootballStatus));
        if(StringUtils.isEmpty(rcsMarketFootballStatus.getLinkId())){
            rcsMarketFootballStatus.setLinkId(rcsMarketFootballStatus.getMatchId()+"");
        }
        try{
            Long matchId = rcsMarketFootballStatus.getMatchId();
            //获取标准盘口
//            StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//            example.createCriteria().andMatchInfoIdEqualTo(matchId);
//            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(example);
            StandardSportMarketSell standardSportMarketSells = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(matchId);
            if(null == standardSportMarketSells){
                log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件：查询标准盘口为空==={}",rcsMarketFootballStatus.getLinkId());
                return;
            }
//            StandardMatchInfo matchInfo =  matchInfoMapper.selectByPrimaryKey(matchId);
            StandardMatchInfo matchInfo =  standardMatchInfoRepository.selectStandardMatchPrimaryKey(matchId);
            if(matchInfo==null){
                log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件失败,查询赛事不存在，{},{}", rcsMarketFootballStatus.getLinkId(),matchId);
                return;
            }
            //查询三方赛事信息
            String businessEvent = standardSportMarketSells.getBusinessEvent();
//            ThirdMatchInfoExample thirdMatchExample  =new ThirdMatchInfoExample();
//            thirdMatchExample.createCriteria().andReferenceIdEqualTo(matchId)
//                    .andDataSourceCodeEqualTo(businessEvent);
//            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchExample);
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectByStandardIdAndDataSourceCode(matchId,businessEvent);
            if (thirdMatchInfo == null ) {
                log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件：找不到三方赛事信息!{}",rcsMarketFootballStatus.getLinkId());
                return ;
            }
            //校验三方赛事,赛事比分,赛事事件信息
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreateApi(thirdMatchInfo.getId());
            if (!response.isSuccess()) {
                log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件-创建比分失败：{}，{}",rcsMarketFootballStatus.getLinkId(),response.getMsg());
                return;
            }
            //封装事件信息
            MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
            String sportIdStr = thirdSportTypeService.getThirdSportId(matchInfo.getSportId(),businessEvent);
            if(businessEvent.equals("PD")||businessEvent.equals("PD2")){
                sportIdStr=matchInfo.getSportId().toString();
            }
            if(sportIdStr==null){
                log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件：找不到三方体种信息!{}，{}",rcsMarketFootballStatus.getLinkId(),businessEvent);
                return;
            }
            if("PD".equals(businessEvent) || "PD2".equals(businessEvent)){
                sportIdStr=matchInfo.getSportId().toString();
            }
            matchEventInfoMessage.setSportId(Long.valueOf(sportIdStr));
            matchEventInfoMessage.setMatchPeriodId(matchInfo.getMatchPeriodId());
            if(rcsMarketFootballStatus.getUpdateTime()==null || rcsMarketFootballStatus.getUpdateTime() ==0){
                log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件：updateTime错误!{}，{}",rcsMarketFootballStatus.getLinkId(),rcsMarketFootballStatus.getUpdateTime());
                return;
            }
            if(rcsMarketFootballStatus.getSecondsFromStart()==null || rcsMarketFootballStatus.getSecondsFromStart() ==0){
                log.info("RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件：getSecondsFromStart错误!{}，{}",rcsMarketFootballStatus.getLinkId(),rcsMarketFootballStatus.getSecondsFromStart());
                return;
            }
            Long time = (System.currentTimeMillis() -rcsMarketFootballStatus.getUpdateTime())/1000;
            matchEventInfoMessage.setSecondsFromStart(rcsMarketFootballStatus.getSecondsFromStart() + time);
            processorMathcEvent(matchEventInfoMessage, response, businessEvent, rcsMarketFootballStatus);
            //事件下发到实时服务
            sendMatchEventMessage(matchEventInfoMessage, rcsMarketFootballStatus.getLinkId(),matchId);
        }catch(Exception e){
            log.error("{}RCS_MARKET_FOOTBALL_GOAL_STATUS自动开盘下发危险事件异常:{}",rcsMarketFootballStatus.getLinkId(),e);
        }
    }
    /**
     * 组装下发消息
     * @param matchEventInfoMessage
     * @param response
     * @param businessEvent
     */
    private void processorMathcEvent(MatchEventInfoMessage matchEventInfoMessage, Response<MatchScoreAndTimeVo> response, String businessEvent, RcsMarketFootballStatusDTO rcsMarketFootballStatus) {
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        matchEventInfoMessage.setCanceled(0);//未取消
        matchEventInfoMessage.setDataSourceCode(businessEvent);
        matchEventInfoMessage.setSourceType("1");//常规事件
        matchEventInfoMessage.setEventCode("dangerous_attack");
        matchEventInfoMessage.setEventTime(rcsMarketFootballStatus.getUpdateTime());
        matchEventInfoMessage.setHomeAway("");
        matchEventInfoMessage.setAddition1("auto");
        matchEventInfoMessage.setT1(matchScoresInfo.getT1());
        matchEventInfoMessage.setT2(matchScoresInfo.getT2());
        matchEventInfoMessage.setExtrainfo("1002");
        if(rcsMarketFootballStatus.getStatus()==2){
            matchEventInfoMessage.setExtrainfo("aoAutoOpen");
        }
        matchEventInfoMessage.setCopyLinkId(rcsMarketFootballStatus.getLinkId());
        matchEventInfoMessage.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoMessage.setIsErrorEndEvent(0);
        matchEventInfoMessage.setThirdEventId("PA_Event:"+ UUIdUtils.getId());
        matchEventInfoMessage.setRemark("");
    }

    /**
     * 发送到实时事件
     * @param matchEventInfoDTO
     * @param linkId
     */
    public void sendMatchEventMessage(MatchEventInfoMessage matchEventInfoDTO, String linkId,Long matchId) {
        Request<MatchEventInfoMessage> request=new Request();
        request.setData(matchEventInfoDTO);
        request.setLinkId(linkId);
        MessageBuilder<Request<MatchEventInfoMessage> > builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //通知预售开售赛事完赛
        boolean spareMqFlag = getSpareMqFlag(matchId+"");
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
            request = new Request<>(matchEventInfoDTO, request.getLinkId(), THIRD_MATCH_EVENT_INFO_API, request.getLinkId(), dataSourceCode);
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send("THIRD_MATCH_EVENT_INFO_API:" + matchEventInfoDTO.getThirdMatchSourceId(), builder.build());
        }
        log.info("::{}::RCS_MARKET_FOOTBALL_GOAL_STATUS通知实时服务处理人工下发的事件 request={}", linkId, matchEventInfoDTO);
    }

    /**
     * 判断是否需要发送消息到备用MQ
     * @param standardMatchId 标准比赛ID
     * @return true表示需要发送到备用MQ
     * 1: pandaDataMqGatewayevent !=2 || 标准赛事ID为空，无需切换
     * 2: pandaDataMqGatewayevent = 2
     *    pandaDataMqGatewayMatchId 为空表示全部切换，
     *    pandaDataMqGatewayMatchId 不为空并且包含 standardMatchId 则切换
     *    pandaDataMqGatewayMatchId 不为空并且不包含 standardMatchId 则不切换
     */
    public boolean getSpareMqFlag(String standardMatchId) {
        // 快速失败：不满足基本条件直接返回
        if (pandaDataMqGatewayevent != 2 || org.apache.commons.lang3.StringUtils.isBlank(standardMatchId)) {
            return false;
        }

        // 处理备用MQ配置
        if (org.apache.commons.lang3.StringUtils.isBlank(pandaDataMqGatewayMatchId)) {
            return true;
        }

        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(pandaDataMqGatewayMatchId.split(","))
        );
        return spareMatchIds.contains(standardMatchId);
    }
}
