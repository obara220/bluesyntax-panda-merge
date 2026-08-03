package com.panda.merge.v2.check.processor;

import com.panda.merge.dto.settle.MatchListSettleDto;
import com.panda.merge.filter.basketball.BasketballScoreFilter;
import com.panda.merge.model.*;
import com.panda.merge.mq.consumer.FlowControlConsumer;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.IMatchSettleService;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.v2.repository.MatchSettleCheckInfoRepository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.repository.MatchSettleThirdBasketScoreRepository;
import com.panda.merge.v2.repository.MatchSettleThirdScoreV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class BasketBallEventProcessor {
    @Autowired
    private IMatchSettleService matchSettleService;
    @Autowired
    MatchSettleThirdBasketScoreRepository matchSettleThirdBasketScoreRepository;
    @Autowired
    BasketballScoreFilter basketballScoreFilter;
    @Autowired
    IWsPushService wsPushService;

    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    MatchSettleScoreV2Repository matchSettleScoreV2Repository;
    @Autowired
    MatchSettleThirdScoreV2Repository matchSettleThirdScoreV2Repository;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;

    @Autowired
    private FlowControlConsumer flowControlConsumer;

    public void processorScore(List<MatchEventInfo> list) {
        MatchSettleInfo matchSettleInfo =matchSettleInfoRepository.getModelMatchSettleInfo(list.get(0).getStandardMatchId());
        if(matchSettleInfo==null){
            return;
        }
        if(matchSettleInfo.getSettleType()==null||matchSettleInfo.getSettleType()==1){
            return;
        }
        Set<Long> limitedMatchIds = flowControlConsumer.getLimitedMatchIds();

        //阶段过滤
        for (MatchEventInfo matchEventInfo : list) {
            if(!matchEventInfo.getDataSourceCode().equals("PD")&&limitedMatchIds.contains(matchEventInfo.getStandardMatchId())) {
                log.info("StandardMatchScoreConsumer 该赛事id:{}以及数据源进行限流了", matchEventInfo.getStandardMatchId());
                continue;
            }
            if(matchEventInfo.getCanceled().equals(1)){
                if(matchEventInfo.getEventCode().equals("score_change")){
                    //篮球比分删除事件逻辑开始
                    canceldScoreChangeEvent(matchEventInfo);
                    if (matchEventInfo.getCanceled() == 0) {
                        continue;
                    }
                    //1.只处理结算2.0的删除事件预警
                    if (matchSettleInfo == null) {
                        //未切换到2.0过不处理
                        continue;
                    } else {
                        //去除历史 即使结算比分
                        matchSettleThirdBasketScoreRepository.deleteSettleScores(matchEventInfo.getThirdMatchId(),matchEventInfo.getThirdEventId());
                        List<String> deleteSettleNumList = new ArrayList<>();
                        basketballScoreFilter.deleteEventPeriodScoreFilter(matchEventInfo,deleteSettleNumList);
                        log.info(matchEventInfo.getLinkId()+":Basketball-DeleteSettleNums:"+deleteSettleNumList);
                        //删除事件标记比分阶段
                        if (deleteSettleNumList.size()!=0){
                            MatchSettleScore matchSettleScore = new MatchSettleScore();
                            matchSettleScore.setHasDeleteEvent(1);
                            matchSettleScore.setCurrentEventStatus(2);
                            MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
                            matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId()).andSettleNumIn(deleteSettleNumList);
                            matchSettleScoreV2Repository.updateByExampleSelective(matchSettleScore,matchSettleScoreExample);
                        }
                        //删除事件标记赛事
                        matchSettleInfo.setHasDeleteEvent(1);
                        matchSettleInfo.setCurrentEventStatus(2);
                        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                        //通知前端刷新赛事相关的
                        wsPushService.pushSettleMatchList(new MatchListSettleDto(matchEventInfo.getStandardMatchId(),matchEventInfo.getEventCode(),null,null,4));
                        //mango预警
                        //1.查询被删除的事件
                        //2.根据被删除的事件拼凑mango预警信息 比分 主客对阵 进行时长 事件发生的球队名称
                        log.info("删除事件芒果预警开始：" + matchEventInfo.getThirdMatchId());
                        matchSettleService.manGoEarlyWarning(matchEventInfo);

                        log.info("删除事件芒果预警结束：");
                    }
                }
            }
        }

    }

    private void canceldScoreChangeEvent(MatchEventInfo matchEventInfo) {
        try {
            if(matchEventInfo.getMatchPeriodId().equals(1L)||matchEventInfo.getMatchPeriodId().equals(2L)){
                return;
            }
            //阶段特殊玩法 只有 4节有这个
            //查询需要删除的比分
            List<MatchSettleScore> list =matchSettleScoreV2Repository.getModelsByItems(matchEventInfo.getStandardMatchId(), null, Arrays.asList(matchEventInfo.getMatchPeriodId()),null,null,null);
            for (MatchSettleScore matchSettleScore : list) {
                //比分过滤 必须是含比分的特殊玩法
                if(StringUtils.isEmpty(matchSettleScore.getExtryInfo())){
                    continue;
                }
                //比分不符合要求
                if(matchEventInfo.getT1()> Integer.parseInt(matchSettleScore.getExtryInfo())&&matchEventInfo.getT2()> Integer.parseInt(matchSettleScore.getExtryInfo())){
                    continue;
                }
                //删除三方比分
                MatchSettleThirdScoreExample thirdScoreExample =new MatchSettleThirdScoreExample();
                thirdScoreExample.createCriteria().andThirdMatchIdEqualTo(matchEventInfo.getThirdMatchId()).andSettleNumEqualTo(matchSettleScore.getSettleNum())
                        .andExtryInfoEqualTo(matchSettleScore.getExtryInfo());
                matchSettleThirdScoreV2Repository.deleteByExample(thirdScoreExample);
                //删除核对日志
                MatchSettleCheckInfoExample checkInfoExample =new MatchSettleCheckInfoExample();
                checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleScore.getId()).andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId());
                matchSettleCheckInfoRepository.deleteByExample(checkInfoExample);
            }
            //全场特殊玩法
            List<MatchSettleScore> scoreFTList =matchSettleScoreV2Repository.getModelsByItems(matchEventInfo.getStandardMatchId(),null,Arrays.asList(0L),null,null,null);
            for (MatchSettleScore matchSettleScore : scoreFTList) {
                //比分过滤 必须是含比分的特殊玩法
                if(StringUtils.isEmpty(matchSettleScore.getExtryInfo())){
                    continue;
                }
                //比分不符合要求
                if(matchEventInfo.getT1()> Integer.parseInt(matchSettleScore.getExtryInfo())&&matchEventInfo.getT2()> Integer.parseInt(matchSettleScore.getExtryInfo())){
                    continue;
                }
                //删除三方比分
                MatchSettleThirdScoreExample thirdScoreExample =new MatchSettleThirdScoreExample();
                thirdScoreExample.createCriteria().andThirdMatchIdEqualTo(matchEventInfo.getThirdMatchId()).andSettleNumEqualTo(matchSettleScore.getSettleNum())
                        .andExtryInfoEqualTo(matchSettleScore.getExtryInfo());
                matchSettleThirdScoreV2Repository.deleteByExample(thirdScoreExample);
                //删除核对日志
                MatchSettleCheckInfoExample checkInfoExample =new MatchSettleCheckInfoExample();
                checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleScore.getId()).andStandardMatchIdEqualTo(matchEventInfo.getStandardMatchId());
                matchSettleCheckInfoRepository.deleteByExample(checkInfoExample);
            }
        }catch (Exception e){
            log.error("cancelScoreChangeEvent error matchEventInfo_linkId"+matchEventInfo.getLinkId(),e);
        }

    }


}
