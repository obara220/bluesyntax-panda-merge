package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mysql.cj.util.StringUtils;
import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.constant.MatchSettleScoreConstant;
import com.panda.merge.dto.MatchSettleEventMessage;
import com.panda.merge.dto.MatchSettleScoreMessage;
import com.panda.merge.dto.OrderSettleCheckEventVO;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchSettleEventMapper;
import com.panda.merge.mapper.MatchSettleScoreMapper;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleEventExample;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleScoreExample;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.service.IMatchSettleLogService;
import com.panda.merge.service.IMatchSettleService;
import com.panda.merge.v2.service.IMatchSettleInfoService;
import com.panda.merge.v2.service.helper.MatchSettleInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.SOLD_MESSAGE;

/**
 * 进球点附近事件重跑
 * @author       Aison
 * @createDate  2020年10月23日10:00:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "ORDER_CHECK_EVENT",
        consumerGroup = "settle-group-"+ "ORDER_CHECK_EVENT",
        consumeThreadMax = 2,
        consumeTimeout = 10000L
)
@DependsOn("settleScoresAdminApplication")
public class OrderEventCheckConsumer implements RocketMQListener<Request<OrderSettleCheckEventVO>> {

    @Autowired
    MatchSettleEventMapper matchSettleEventMapper;
    @Autowired
    MatchSettleScoreMapper matchSettleScoreMapper;
    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;

    @Autowired
    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;
    @Autowired
    IMatchSettleInfoService matchSettleInfoService;
    @Autowired
    IMatchSettleLogService iMatchSettleLogService;
    @Autowired
    IMatchSettleService matchSettleService;
    @Resource
    private MatchSettleInfoHelper matchSettleInfoHelper;
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;
    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;
    @Autowired
    CommonProducer commonProducer;

    @Override
    public void onMessage(Request<OrderSettleCheckEventVO> request) {
        log.info("数据中心ORDER_CHECK_EVENT分流Id:"+datacenterSettleId);
        if(datacenterSettleSwitch||commonProducer.getDatacenterMatchIds(request.getData().getMatchId().toString())){
            log.info("Link::{}::ORDER_CHECK_EVENT数据中心分流Id::{}::",request.getLinkId(),request.getData().getMatchId());
            commonProducer.asyncSend(request, "datacenter-ORDER_CHECK_EVENT");
            return;
        }
        log.info("【OrderEventCheckConsumer:ORDER_CHECK_EVENT】【::"+request.getLinkId()+"::】进球点附近事件重跑");
        if(request==null||request.getData()==null||request.getData().getMatchId()==null||request.getData().getEventIds()==null){
            return;
        }
        String[] ar=request.getData().getEventIds().split(",");
        List<String> eventIdsStr= Arrays.asList(ar);
        List<Long> eventIds = eventIdsStr.stream().map(it->Long.parseLong(it)).collect(Collectors.toList());
        if(eventIds.isEmpty()){
            return ;
        }
        try {
            for (Long eventId : eventIds) {
                //1.查询对应事件
                List<MatchSettleEvent> eventList = searchMatchSettleEventByOrderCheck(request.getData().getMatchId(),eventId);
                //2.事件重跑逻辑
                if(!eventList.isEmpty()){
                    reSettleEventByOrderCheck(eventList,request.getData().getSettleOrderNums());
                }else {
                    //3.如果事件不存在则查询对应比分
                    List<MatchSettleScore> scoreList = searchMatchSettleScoreByOrderCheck(request.getData().getMatchId(),eventId);
                    //3.比分重跑逻辑
                    if(!scoreList.isEmpty()){
                        reSettleScoreByOrderCheck(scoreList,request.getData().getSettleOrderNums());
                    }
                }
            }
        }catch (Exception e){
            log.error("【OrderEventCheckConsumer:"+ SOLD_MESSAGE+"】【::"+request.getLinkId()+"::】进球点附近事件重跑:",e);
        }
        log.info("【OrderEventCheckConsumer:"+ SOLD_MESSAGE+"】【::"+request.getLinkId()+"::】进球点附近事件重跑结束");
    }

    private void reSettleScoreByOrderCheck(List<MatchSettleScore> scoreList, String settleOrderNums) {
        for (MatchSettleScore matchSettleScore : scoreList) {
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
            matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes()+1);
            matchSettleScore.setOperater("system");
            matchSettleScore.setIsGrey(0);
            matchSettleScore.setHasDeleteEvent(0);
            matchSettleScore.setCurrentEventStatus(0);
            matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
            matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
            //2.MQ下发
            MatchSettleScoreMessage Score = new MatchSettleScoreMessage();
            BeanUtils.copyProperties(matchSettleScore,Score);
            Score.setSettleOrderNums(settleOrderNums);
            Score.setLevel(3);
            matchSettleScoresProducer.sendMatchSettleScores(Score);

            //1.比分结算增加操作日志
            //走水设置编码为8
            if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
            iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore, matchSettleScore.getOperater(),
                    OperateLogTypeEnum.SCORE_SETTLE, OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(), "");
            log.info("订单通过审核重跑比分结算:{} ::赛事id: 事件id:{}::"+matchSettleScore.getEventName(),matchSettleScore.getStandardMatchId(), matchSettleScore.getId());
        }



    }


    private void reSettleEventByOrderCheck(List<MatchSettleEvent> eventList, String settleOrderNums) {
        for (MatchSettleEvent matchSettleEvent : eventList) {
            matchSettleEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
            matchSettleEvent.setOperater("system");
            matchSettleEvent.setCurrentEventStatus(0);
            matchSettleEvent.setIsGrey(0);
            matchSettleEvent.setHasDeleteEvent(0);
            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
            //结算时把回滚订单数清零
            matchSettleService.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
            //1.日志
            iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent, matchSettleEvent.getOperater(),
                    OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString(), "","");
            //2.MQ下发
            MatchSettleEventMessage event = new MatchSettleEventMessage();
            BeanUtils.copyProperties(matchSettleEvent, event);
            event.setLevel(3);
            event.setSettleOrderNums(settleOrderNums);
            matchSettleScoresProducer.sendMatchSettleEvent(event);
            matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleEvent.getStandardMatchId());
            log.info("订单通过审核重跑事件结算:{} ::赛事id: 事件id:{}::"+matchSettleEvent.getEventName(),matchSettleEvent.getStandardMatchId(), matchSettleEvent.getId());
        }
    }

    private List<MatchSettleScore> searchMatchSettleScoreByOrderCheck(Long matchId, Long eventId) {
        MatchSettleScoreExample example=new MatchSettleScoreExample();
        example.createCriteria().andStandardMatchIdEqualTo(matchId).andIdEqualTo(eventId)
                .andStatusEqualTo(3);
        List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
        if(list.size()!=0){
            MatchSettleScore matchSettleScore = list.get(0);
            if (((matchSettleScore.getSettleFreeze() != null && matchSettleScore.getSettleFreeze() == 1)
                    ||(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1))) {
                return new ArrayList<>();
            }
        }
        return list;
    }

    private List<MatchSettleEvent> searchMatchSettleEventByOrderCheck(Long matchId, Long eventId) {
        MatchSettleEventExample example=new MatchSettleEventExample();
        example.createCriteria().andStandardMatchIdEqualTo(matchId).andIdEqualTo(eventId)
                .andStatusEqualTo(3);
        List<MatchSettleEvent> list = matchSettleEventMapper.selectByExample(example);
        if(list.size()!=0) {
            MatchSettleEvent matchSettleEvent = list.get(0);
            if (((matchSettleEvent.getSettleFreeze() != null && matchSettleEvent.getSettleFreeze() == 1)
                    ||(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1))) {
                return new ArrayList<>();
            }
        }
        return list;
    }
}
