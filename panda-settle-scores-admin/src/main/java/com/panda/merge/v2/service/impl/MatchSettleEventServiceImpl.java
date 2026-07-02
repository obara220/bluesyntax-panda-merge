package com.panda.merge.v2.service.impl;

import com.panda.merge.common.enums.BasketBallSettleNumEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.constant.FootballPeriodValidateEnum;
import com.panda.merge.constant.SecondSettleReasonEnum;
import com.panda.merge.dto.settle.EditMatchSettleEventDto;
import com.panda.merge.dto.settle.SettleMatchScoreDto;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.utils.EndEventUtils;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.service.IMatchSettleEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.SETTLED;

@Slf4j
@Repository("MatchSettleEventServiceImplV2")
public class MatchSettleEventServiceImpl implements IMatchSettleEventService {

    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;

    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    @Autowired
    private MatchSettleCenterProducer matchSettleCenterProducer;

    public void endEventSettleByEvent(MatchSettleEvent matchSettleEvent) {
        //1.上半场下半场 进球角球 发牌
        List<String> eventCodes = EndEventUtils.eventCodesFootballByEventCode(matchSettleEvent.getEventCode());
        if(eventCodes.size()==0){
            return;
        }
        //1.阶段条件获取 上半场 或者全场 上半场事件可能会导致 全场结算 或者 上半场结算
        //1.2 下半场事件则可能触发全场结算
        List<Long> periods =  EndEventUtils.periodsFootballByEventPeriod(matchSettleEvent.getPeriodId());
        //不是31 也不是100 事件则直接返回
        if(periods==null){
            return;
        }
        //2.查询对应事件编码和阶段编码已经结算的比分 而且比分相同
        List<MatchSettleScore> scoreList = matchSettleScoreRepository.getModelsByItems(matchSettleEvent.getStandardMatchId(),eventCodes, periods,SETTLED,matchSettleEvent.getT1(),matchSettleEvent.getT2());
        if(scoreList.size()==0){
            return;
        }
        for (MatchSettleScore matchSettleScore : scoreList) {
            //符合全场结算 编辑add2
            if(matchSettleScore.getPeriodId().equals(100L)){
                matchSettleEvent.setAddition2(matchSettleEvent.getHomeAway());
            }
            //符合上半场结算 编辑add1
            if(matchSettleScore.getPeriodId().equals(31L)){
                matchSettleEvent.setAddition1(matchSettleEvent.getHomeAway());
            }
        }
        log.info("结算比分编辑最终事件::赛事id：{},事件阶段:{},事件类型:{} add1:{} add2:{}",
                matchSettleEvent.getStandardMatchId(),matchSettleEvent.getPeriodId(),matchSettleEvent.getEventCode()
                ,matchSettleEvent.getAddition1(),matchSettleEvent.getAddition2());
    }

    @Override
    public boolean settlePenaltyTeamFirst(MatchSettleEvent event) {
        //1.结算该事件修改状态
        if (!event.getSettleNum().equals("-1030")) {
            return false;
        }
        event.setStatus(3);
        event.setModifyTime(System.currentTimeMillis());
        //2.将当前的赛事的所有点球事件进行次序计算
        List<MatchSettleEvent> penaltyEvents = matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(event.getStandardMatchId(), Arrays.asList("1030"));
        for (MatchSettleEvent penaltyEvent : penaltyEvents) {
            if (event.getHomeAway().equals("home")) {
                if (penaltyEvent.getHomeAway().equals("home")) {
                    penaltyEvent.setEventOrder((penaltyEvent.getFirstNum() - 1) * 2 + 1);
                } else if (penaltyEvent.getHomeAway().equals("away")) {
                    penaltyEvent.setEventOrder(penaltyEvent.getFirstNum() * 2);
                }
            } else if (event.getHomeAway().equals("away")) {
                if (penaltyEvent.getHomeAway().equals("home")) {
                    penaltyEvent.setEventOrder(penaltyEvent.getFirstNum() * 2);
                } else if (penaltyEvent.getHomeAway().equals("away")) {
                    penaltyEvent.setEventOrder((penaltyEvent.getFirstNum() - 1) * 2 + 1);
                }
            }
            penaltyEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEventRepository.updateById(penaltyEvent);
        }
        return true;
    }

    @Override
    public void updateGoWaterPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
        //查询
        List<MatchSettleEvent> list =matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(settleScoreSearchDto.getStandardMatchId(), Arrays.asList("1054"));
        if(list.size()==0){
            return;
        }
        MatchSettleEvent matchSettleEvent= list.get(0);
        //结算后不做编辑
        if(matchSettleEvent.getStatus()!=null&&matchSettleEvent.getStatus()==3){
            return;
        }
        matchSettleEvent.setStatus(1);
        matchSettleEvent.setT1(settleScoreSearchDto.getT1());
        matchSettleEvent.setT2(settleScoreSearchDto.getT2());
        matchSettleEvent.setModifyTime(System.currentTimeMillis());
        matchSettleEvent.setOperater(settleScoreSearchDto.getOperatorName());
        matchSettleEventRepository.updateById(matchSettleEvent);
    }

    @Override
    public void secondSettleWarnMango(SettleMatchScoreDto matchSettleScoreDto, Integer sportId) {
        SecondSettleReasonEnum secondSettleReasonEnum = SecondSettleReasonEnum.getByCode(matchSettleScoreDto.getSettleReason());
        if (secondSettleReasonEnum == null) {
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
        StandardSportTournament standardSportTournament =standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());

        String settleName = "";
        if(sportId == 1) {
            FootballPeriodValidateEnum settleNumEnum = FootballPeriodValidateEnum.getEnum(String.valueOf(matchSettleScoreDto.getSettleNum()));
            if (settleNumEnum == null) {
                return;
            }
            settleName = settleNumEnum.getName();
        } else if (sportId == 2){
            BasketBallSettleNumEnum settleNumEnum = BasketBallSettleNumEnum.getEnum(matchSettleScoreDto.getSettleReasonDetail());
            if (settleNumEnum == null) {
                return;
            }
            settleName = settleNumEnum.getValue();
        }


        Date date = new Date(standardMatchInfo.getBeginTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String formattedDate = sdf.format(date);
        String data = "联赛:" + standardSportTournament.getName() + "\n" +
                "比赛时间:" + formattedDate + "\n" +
                "赛事ID:" +standardMatchInfo.getMatchManageId()+"\n"+
                "对阵:" + standardMatchInfo.getHomeAwayInfo() + "\n" +
                "二次结算原因:" + secondSettleReasonEnum.getName() + "\n"+
                "影响阶段:" + settleName;
        String linkId = "SECOND_SETTLE_WARN_" + IdWorker.getId();
        matchSettleCenterProducer.secondSettleWarning(linkId,data,"二次结算告警");
    }
}
