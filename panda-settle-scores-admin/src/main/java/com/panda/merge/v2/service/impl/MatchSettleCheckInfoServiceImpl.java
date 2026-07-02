package com.panda.merge.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.dto.settle.PenaltyScoresVo;
import com.panda.merge.model.*;
import com.panda.merge.utils.SettleCheckUtils;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.v2.entity.MatchSettleInfoEntity;
import com.panda.merge.v2.repository.MatchSettleCheckInfoRepository;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.v2.service.IMatchSettleCheckInfoService;
import com.panda.merge.v2.service.helper.MatchSettleCheckInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.SETTLED;

@Slf4j
@Service("MatchSettleCheckInfoServiceImplV2")
public class MatchSettleCheckInfoServiceImpl implements IMatchSettleCheckInfoService {

    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;
    @Autowired
    private MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    private MatchSettleCheckInfoHelper matchSettleCheckInfoHelper;

    @Override
    public void searchCheckStatusByScoresList(List<MatchSettleScoreDto> matchSettleScoreDtos, String operatorName){
        matchSettleCheckInfoHelper.searchCheckStatusByScoresList(matchSettleScoreDtos, operatorName);
    }

    @Override
    public void searchCheckStatusByEventList(List<MatchSettleEventDto> matchSettleScoreDtos, String OperatorName) {
        matchSettleCheckInfoHelper.searchCheckStatusByEventList(matchSettleScoreDtos,OperatorName);
    }

    @Override
    public void searchCheckStatusByPenalty(PenaltyScoresVo penaltyScoresVo, String operatorName) {
        //有WS推送的情况这个时候没操作人
        if (StringUtils.isEmpty(operatorName)) {
            return;
        }
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(penaltyScoresVo.getStandardMatchId());
        if (StringUtils.isNotEmpty(matchSettleInfo.getLimitUserArray())) {
            JSONArray array = JSONArray.parseArray(matchSettleInfo.getLimitUserArray());
            if (array.contains(operatorName)) {
                penaltyScoresVo.getTeamFirst().setNeedCheck(0);
                for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getHomeEventList()) {
                    matchSettleEventDto.setNeedCheck(0);
                }
                for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getAwayEventList()) {
                    matchSettleEventDto.setNeedCheck(0);
                }
                penaltyScoresVo.getHomeAway5RoundEvent().setNeedCheck(0);
                penaltyScoresVo.getHomeAwayAllRoundEvent().setNeedCheck(0);
                penaltyScoresVo.getGoWaterPenaltyEvent().setNeedCheck(0);
                return;
            }
        }
        Map<Long, MatchSettleEventDto> matchSettleScoreDtoMap = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        for (MatchSettleEventDto matchSettleScoreDto : penaltyScoresVo.getAwayEventList()) {
            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
        }
        for (MatchSettleEventDto matchSettleScoreDto : penaltyScoresVo.getHomeEventList()) {
            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
        }
        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getTeamFirst().getId()), penaltyScoresVo.getTeamFirst());
        ids.add(Long.parseLong(penaltyScoresVo.getTeamFirst().getId()));

        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getHomeAway5RoundEvent().getId()), penaltyScoresVo.getHomeAway5RoundEvent());
        ids.add(Long.parseLong(penaltyScoresVo.getHomeAway5RoundEvent().getId()));

        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getHomeAwayAllRoundEvent().getId()), penaltyScoresVo.getHomeAwayAllRoundEvent());
        ids.add(Long.parseLong(penaltyScoresVo.getHomeAwayAllRoundEvent().getId()));

        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getGoWaterPenaltyEvent().getId()), penaltyScoresVo.getGoWaterPenaltyEvent());
        ids.add(Long.parseLong(penaltyScoresVo.getGoWaterPenaltyEvent().getId()));
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserName(ids,penaltyScoresVo.getStandardMatchId(),operatorName);
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            MatchSettleEventDto matchSettleScoreDto = matchSettleScoreDtoMap.get(matchSettleCheckInfo.getSettleScoreEventId());
            matchSettleScoreDto.setNeedCheck(0);
            if (matchSettleScoreDto != null) {
                if (!(matchSettleCheckInfo.getCheckStatus() != null && matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM)) {
                    matchSettleScoreDto.setNeedCheck(1);
                }
                //如果普通审核员进来，需要返回他个人的状态。除非数据已经结算
                if (matchSettleScoreDto.getStatus() != SETTLED) {
                    matchSettleScoreDto.setStatus(matchSettleCheckInfo.getCheckStatus());
                } else {
                    matchSettleScoreDto.setNeedCheck(0);
                }
            }

            if (matchSettleCheckInfo.getSettleScoreEventId().toString().equals(penaltyScoresVo.getTeamFirst().getId())) {
                if (penaltyScoresVo.getTeamFirst().getStatus() != null && penaltyScoresVo.getTeamFirst().getStatus() != 3) {
                    penaltyScoresVo.getTeamFirst().setHomeAway(matchSettleCheckInfo.getHomeAway());
                }
            }
        }
    }

    @Override
    public Long searchEventTimeByEvent(MatchSettleEvent matchSettleEvent) {
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventId(matchSettleEvent.getId());
        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
        Map<String, List<MatchSettleCheckInfo>> checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
        String key = SettleCheckUtils.countSettleEventCompareKey(matchSettleEvent);
        Long eventTime = 0l;
        for (Map.Entry<String, List<MatchSettleCheckInfo>> stringListEntry : checkGroupMap.entrySet()) {
            if (stringListEntry.getKey().equals(key)) {
                for (MatchSettleCheckInfo matchSettleCheckInfo : stringListEntry.getValue()) {
                    if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
                        if (eventTime == 0l) {
                            eventTime = matchSettleCheckInfo.getCreateTime();
                        } else {
                            if (eventTime > matchSettleCheckInfo.getCreateTime()) {
                                eventTime = matchSettleCheckInfo.getCreateTime();
                            }
                        }
                    }
                }
            }
        }
        return eventTime;
    }

    @Override
    public Long searchEventTimeByScores(MatchSettleScore settleScore) {
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventId(settleScore.getId());
        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
        Long eventTime = 0l;
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
                if (eventTime == 0l) {
                    eventTime = matchSettleCheckInfo.getCreateTime();
                } else {
                    if (eventTime > matchSettleCheckInfo.getCreateTime()) {
                        eventTime = matchSettleCheckInfo.getCreateTime();
                    }
                }
            }
        }
        return eventTime;
    }

    @Override
    public void rollbackScores(MatchSettleScore matchSettleScore) {
        matchSettleCheckInfoHelper.rollbackScores(matchSettleScore);
    }

    @Override
    public void rollbackEvent(MatchSettleEvent matchSettleEvent) {
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserName(Arrays.asList(matchSettleEvent.getId()),matchSettleEvent.getStandardMatchId(), null);
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
            log.info("结算回滚，事件核对数据状态还原 rollbackEvent:{}", matchSettleCheckInfo.getSettleScoreEventId());
        }
    }

    @Override
    public MatchSettleCheckInfo searchCheckInfoByUser(Long scoreEventId, Long standardMatchId, String userName) {
        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
        checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(scoreEventId)
                .andUserNameEqualTo(userName).andStandardMatchIdEqualTo(standardMatchId);
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserName(Arrays.asList(scoreEventId),standardMatchId,userName);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        } else if (list.size() > 1) {
            log.error("::{} 的记录在用户:{}X 下存在并发记录", scoreEventId, userName);
        }
        return list.get(0);
    }

}
