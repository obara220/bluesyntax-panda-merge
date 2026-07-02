package com.panda.merge.service.impl;

import com.panda.merge.dto.message.StandardMarketOddsResultMessage;
import com.panda.merge.dto.message.StandardMarketResultMessage;
import com.panda.merge.mapper.MatchSettleEventMapper;
import com.panda.merge.mapper.StandardSportMarketOddsMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.IThirdMarketResultTransService;
import com.panda.merge.service.StandardSportPlayerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ThirdMarketResultTransServiceImpl implements IThirdMarketResultTransService {

    @Autowired
    StandardSportMarketOddsMapper standardSportMarketOddsMapper;
    @Autowired
    StandardSportPlayerService standardSportPlayerService;
    @Autowired
    MatchSettleEventMapper matchSettleEventMapper;

    @Override
    public void transFootballPlayerMarketResult(String linkId, StandardMarketResultMessage data, StandardSportMarket standardSportMarket) {
        //根据盘口获取 第X   全场常规赛阶段
        String x =standardSportMarket.getAddition1();
        Integer eventOrder= Integer.parseInt(x);
        //根据赛果获取球员
        String playerId =null;
        for (StandardMarketOddsResultMessage standardMarketOddsResultMessage : data.getMarketOddsResultList()) {
            if(standardMarketOddsResultMessage.getSettlementResult().equals("4")){
                StandardSportMarketOddsExample example =new StandardSportMarketOddsExample();
                example.createCriteria().andRelationMarketIdEqualTo(standardSportMarket.getRelationMarketId()).andRelationMarketOddsIdEqualTo(standardMarketOddsResultMessage.getId());
                List<StandardSportMarketOdds> list =standardSportMarketOddsMapper.selectByExample(example);
                if(list.size()==0){
                    log.warn("::{}:: 投注项不存在={}",linkId);
                    return;
                }
                StandardSportMarketOdds odds= list.get(0);
                //附加字段1 球员ID
                 playerId =odds.getAddition1();
                log.info("::{}:: 进球的球员id={}",linkId,playerId);
                 break;
            }
        }
        //球员类玩法用到 设置playerNameCode
        StandardSportPlayer standardSportPlayer = standardSportPlayerService.getItem(1L, playerId);
        if(playerId==null){
            log.warn("::{}:: 进球的球员id={} 没找到",linkId,playerId);
            return;
        }
        Long playerNameCode =standardSportPlayer.getNameCode();
        if(playerNameCode==null){
            log.warn("::{}:: 进球的球员id={} nameCode为空",linkId,playerId);
            return;
        }
        //根据进球次序获取事件
        MatchSettleEvent matchSettleEvent =getGoalExtryEventByOrderAndMatchId(eventOrder,standardSportMarket.getStandardMatchInfoId());
        if(matchSettleEvent==null){
            log.warn("::{}:: 进球的次序事件没找到 eventOrder:{},matchId:{}",linkId,eventOrder,standardSportMarket.getStandardMatchInfoId());
            return;
        }
        if(matchSettleEvent.getStatus()!=null&&matchSettleEvent.getStatus()<=1){
            matchSettleEvent.setPlayerNameCode(playerNameCode.toString());
            matchSettleEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
        }else {
            log.info("::{}:: 进球的次序事件状态已经被变更 eventOrder:{},matchId:{}",linkId,eventOrder,standardSportMarket.getStandardMatchInfoId());
        }
    }

    @Override
    public void transFootballGoalTypeMarketResult(String linkId, StandardMarketResultMessage data, StandardSportMarket standardSportMarket) {
        //根据 盘口获取 X  阶段  全场常规赛阶段
        String x =standardSportMarket.getAddition1();
        Integer eventOrder= Integer.parseInt(x);
        String goalType=null;
        //根据赛果获取进球方式
        for (StandardMarketOddsResultMessage standardMarketOddsResultMessage : data.getMarketOddsResultList()) {
            if(standardMarketOddsResultMessage.getSettlementResult().equals("4")){
                StandardSportMarketOddsExample example =new StandardSportMarketOddsExample();
                example.createCriteria().andRelationMarketIdEqualTo(standardSportMarket.getRelationMarketId()).andRelationMarketOddsIdEqualTo(standardMarketOddsResultMessage.getId());
               List<StandardSportMarketOdds> list =standardSportMarketOddsMapper.selectByExample(example);
               if(list.size()==0){
                   log.warn("::{}:: 投注项不存在={}",linkId);
                   return;
               }
                StandardSportMarketOdds odds= list.get(0);
                 goalType =odds.getOddsType();
                //Shot,Header,OwnGoal,Penalty,FreeKick,None 射门 投球 乌龙球 点球  任意球 没有
                log.info("::{}:: 进球的方式={}",linkId,goalType);
                break;
            }
        }
        if(goalType==null){
            log.info("::{}:: 进球的进球方式没有找到",linkId);
            return;
        }
        //进球方式 赛果转事件
        String extryInfo =null;
        switch (goalType){
            case "Shot":
                extryInfo="-100";
                break;
            case "Header":
                extryInfo="3";
                break;
            case "OwnGoal":
                extryInfo="2";
                break;
            case "Penalty":
                extryInfo="1";
                break;
            case "FreeKick":
                extryInfo="-200";
                break;
            case "None":
                extryInfo="0";
                break;
            case "FreeKck":
                extryInfo="-200";
                break;
        }
        if(extryInfo==null){
            log.warn("::{}:: 进球的方式={}转化失败",linkId,goalType);
            return;
        }
        MatchSettleEvent matchSettleEvent =getGoalExtryEventByOrderAndMatchId(eventOrder,standardSportMarket.getStandardMatchInfoId());
        if(matchSettleEvent==null){
            log.warn("::{}:: 进球的次序事件没找到 eventOrder:{},matchId:{}",linkId,eventOrder,standardSportMarket.getStandardMatchInfoId());
            return;
        }
        if(matchSettleEvent.getStatus()!=null&&matchSettleEvent.getStatus()<=1){
            matchSettleEvent.setExtryInfo(extryInfo);
            matchSettleEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
        }
    }

    /**
     * 根据 事件次序 以及标准赛事ID 获取结算第几个 进球的 附加字段事件
     * */
    private MatchSettleEvent getGoalExtryEventByOrderAndMatchId(Integer eventOrder, Long standardMatchInfoId) {
        List<Long> periods  =new ArrayList<>();periods.add(6l);periods.add(7l);
        MatchSettleEventExample matchSettleEventExample =new MatchSettleEventExample();
        matchSettleEventExample.createCriteria().andEventTypeEqualTo(2).andStandardMatchIdEqualTo(standardMatchInfoId).
                andPeriodIdIn(periods).andEventCodeEqualTo("goal");
        matchSettleEventExample.setOrderByClause("period_id , event_order");
        List<MatchSettleEvent> list =matchSettleEventMapper.selectByExample(matchSettleEventExample);
        if(list.size()<eventOrder){
            return null;
        }
        int j=0;
        for(int i=0;i<=list.size()-1;i++){
            MatchSettleEvent event =list.get(i);
            if(StringUtils.isNotEmpty(event.getHomeAway())&&(!event.getHomeAway().equals("none"))){
                j++;
            }else {
                continue;
            }
            if(eventOrder==j){
                return event;
            }
        }
        return null;
    }
}
