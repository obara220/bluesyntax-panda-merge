package com.panda.merge.service.syncScore;

import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.constant.FootballPeriodValidateEnum;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.constant.SettleSyncEnum;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ResultCode;
import com.panda.merge.dto.settle.ConfirmMatchSettleScoreDto;
import com.panda.merge.dto.settle.SettleMatchScoreDto;
import com.panda.merge.dto.settle.UpdateMatchSettleScoreDto;
import com.panda.merge.mapper.MatchSettleScoreMapper;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleScoreExample;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.NOT_EDIT;
import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.SETTLED;

/**
 * @description: sync football score
 * @author: Henry Wang
 * @create: 2024-09-13 15:10
 **/

@Slf4j
@Component
public class FootballSyncScoreProcessor extends AbstractSyncScoreProcessor{

    @Resource
    private MatchSettleScoreMapper matchSettleScoreMapper;


    @Resource
    private IFootballMatchScoresSettleApi iFootballMatchScoresSettleApi;

    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;

    @Override
    protected Object buildData(Object object) {
        if(object == null) {
            return null;
        }
        SettleMatchScoreDto matchSettleScoreDto = (SettleMatchScoreDto) object;
        log.info("[FootballSyncScoreProcessor] syncScore with linkId:{} buildData start!", matchSettleScoreDto.getLinkedId());
        if (matchSettleScoreDto.getT1() == null || matchSettleScoreDto.getT2() == null || !SettleEventCodeEnum.FOOTBALL_GOAL.getValue().equals(matchSettleScoreDto.getEventCode())) {
            return null;
        }
        List<String> childSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(String.valueOf(matchSettleScoreDto.getSettleNum()));
        String parentSettleNum = FootballPeriodValidateEnum.getParentSettleNumList(String.valueOf(matchSettleScoreDto.getSettleNum()));
        List<String> brotherSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(parentSettleNum);
        if (parentSettleNum == null && CollectionUtils.isEmpty(childSettleNumList)) {
            return null;
        }

        MatchSettleScoreExample goalExample = new MatchSettleScoreExample();
        goalExample.createCriteria().andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId()).andEventCodeEqualTo(SettleEventCodeEnum.FOOTBALL_GOAL.getValue());
        List<MatchSettleScore> goalList = matchSettleScoreMapper.selectByExample(goalExample);
        Map<String, MatchSettleScore> goalMap = goalList.stream().collect(Collectors.toMap(MatchSettleScore::getSettleNum, t->t));
        List<UpdateMatchSettleScoreDto> settleScoreDtos = new ArrayList<>();

        // 处理比分0-0
        if (matchSettleScoreDto.getT1() == 0 && matchSettleScoreDto.getT2() == 0) {
            for (String settleNum : childSettleNumList) {
                MatchSettleScore goal = goalMap.get(settleNum);
                if(NOT_EDIT.equals(goal.getStatus()) && goal.getSettleCount() == 0) {
                    UpdateMatchSettleScoreDto item = new UpdateMatchSettleScoreDto();
                    BeanUtils.copyProperties(matchSettleScoreDto, item);
                    item.setMatchScoreId(goal.getId());
                    item.setSettleNum(settleNum);
                    item.setSettleCount(null);
                    item.setExtryInfo(null);
                    item.setGoWaterStatus(null);
                    settleScoreDtos.add(item);
                }
            }
        } else {
            // process child nodes
            processChildNode(settleScoreDtos, matchSettleScoreDto, childSettleNumList, goalMap);
        }
        // process parent nodes
        processParentNode(settleScoreDtos, matchSettleScoreDto, brotherSettleNumList, parentSettleNum, goalMap);
        log.info("[FootballSyncScoreProcessor] syncScore with linkId:{} buildData end with result:{}!", matchSettleScoreDto.getLinkedId(), settleScoreDtos);
        if(CollectionUtils.isEmpty(settleScoreDtos)){
            return null;
        }
        return settleScoreDtos;
    }

    @Override
    protected void doProcess(Object object) {
        List<UpdateMatchSettleScoreDto> settleScoreDtos = (List<UpdateMatchSettleScoreDto>) object;
        for (UpdateMatchSettleScoreDto settleScoreDto : settleScoreDtos) {
            MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(settleScoreDto.getMatchScoreId());
            if (SETTLED.equals(matchSettleScore.getStatus())) {
                log.info("[FootballSyncScoreProcessor] syncScore doProcess scoreId:{} already settled!", settleScoreDto.getMatchScoreId());
                return;
            }
            Response response = iFootballMatchScoresSettleApi.updateMatchSettleScore(settleScoreDto);
            if (response.getCode() != ResultCode.SUCCESS.getCode()) {
                return;
            }
            ConfirmMatchSettleScoreDto matchSettleScoreDto = new ConfirmMatchSettleScoreDto();
            BeanUtils.copyProperties(settleScoreDto, matchSettleScoreDto);
            response = iFootballMatchScoresSettleApi.confirmMatchSettleScore(matchSettleScoreDto);
            if (response.getCode() != ResultCode.SUCCESS.getCode()) {
                return;
            }
            SettleMatchScoreDto settleMatchScoreDto = new SettleMatchScoreDto();
            BeanUtils.copyProperties(settleScoreDto, settleMatchScoreDto);
            settleMatchScoreDto.setSettleNum(StringUtils.parseInteger(settleScoreDto.getSettleNum()));
            response = iFootballMatchScoresSettleApi.settleMatchScore(settleMatchScoreDto);
            if (response.getCode() != ResultCode.SUCCESS.getCode()) {
                return;
            }
        }
    }

    @Override
    protected SettleSyncEnum settleSync() {
        return SettleSyncEnum.FOOTBALL_SYNC_SCORE;
    }


    private void processChildNode(List<UpdateMatchSettleScoreDto> settleScoreDtos, SettleMatchScoreDto matchSettleScoreDto,
                                  List<String> childSettleNumList, Map<String, MatchSettleScore> goalMap) {
        if (CollectionUtils.isEmpty(childSettleNumList)) {
            return;
        }
        int totalChildSize = childSettleNumList.size();
        int settledNum = 0;
        String unsettledNum = "";
        int t1 = matchSettleScoreDto.getT1();
        int t2 = matchSettleScoreDto.getT2();
        for (String settleNum : childSettleNumList) {
            MatchSettleScore goal = goalMap.get(settleNum);
            if (SETTLED.equals(goal.getStatus())) {
                settledNum += 1;
                t1 -= goal.getT1();
                t2 -= goal.getT2();
            } else {
                unsettledNum = settleNum;
            }
        }
        MatchSettleScore goal = goalMap.get(unsettledNum);
        if (totalChildSize - settledNum == 1 && NOT_EDIT.equals(goal.getStatus()) && goal.getSettleCount() == 0 && t1 >= 0 && t2 >= 0) {
            UpdateMatchSettleScoreDto item = new UpdateMatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScoreDto, item);
            item.setMatchScoreId(goal.getId());
            item.setSettleNum(unsettledNum);
            item.setT1(t1);
            item.setT2(t2);
            item.setSettleCount(null);
            item.setExtryInfo(null);
            item.setGoWaterStatus(null);
            settleScoreDtos.add(item);
        }
    }

    private void processParentNode(List<UpdateMatchSettleScoreDto> settleScoreDtos, SettleMatchScoreDto matchSettleScoreDto,
                                  List<String> brotherSettleNumList, String parentSettleNum, Map<String, MatchSettleScore> goalMap) {
        if (StringUtils.isEmpty(parentSettleNum)) {
            return;
        }
        MatchSettleScore parentScore = goalMap.get(parentSettleNum);
        if (NOT_EDIT.equals(parentScore.getStatus()) && parentScore.getSettleCount() == 0) {
            int t1 = 0;
            int t2 = 0;
            for (String settleNum : brotherSettleNumList) {
                MatchSettleScore goal = goalMap.get(settleNum);
                if (SETTLED.equals(goal.getStatus())) {
                    t1 += goal.getT1();
                    t2 += goal.getT2();
                } else {
                    return;
                }
            }
            if (t1 < 0 || t2 < 0) {
                return;
            }
            UpdateMatchSettleScoreDto item = new UpdateMatchSettleScoreDto();
            BeanUtils.copyProperties(matchSettleScoreDto, item);
            item.setMatchScoreId(parentScore.getId());
            item.setEventCode(parentScore.getEventCode());
            item.setSettleNum(parentSettleNum);
            item.setT1(t1);
            item.setT2(t2);
            item.setSettleCount(null);
            item.setExtryInfo(null);
            item.setGoWaterStatus(null);
            settleScoreDtos.add(item);
        }

        if (SETTLED.equals(parentScore.getStatus())) {
            int t1 = 0;
            int t2 = 0;
            int count = 0;
            MatchSettleScore childMatchSettleScore = null;
            for (String settleNum : brotherSettleNumList) {
                MatchSettleScore goal = goalMap.get(settleNum);
                if (SETTLED.equals(goal.getStatus())) {
                    t1 += goal.getT1();
                    t2 += goal.getT2();
                } else {
                    count+=1;
                    childMatchSettleScore = goal;
                }
            }

            if (count == 1 && childMatchSettleScore != null) {
                t1 = parentScore.getT1() - t1;
                t2 = parentScore.getT2() - t2;
                if (t1 < 0 || t2 < 0) {
                    return;
                }
                UpdateMatchSettleScoreDto item = new UpdateMatchSettleScoreDto();
                BeanUtils.copyProperties(matchSettleScoreDto, item);
                item.setMatchScoreId(childMatchSettleScore.getId());
                item.setEventCode(childMatchSettleScore.getEventCode());
                item.setSettleNum(childMatchSettleScore.getSettleNum());
                item.setT1(t1);
                item.setT2(t2);
                item.setSettleCount(null);
                item.setExtryInfo(null);
                item.setGoWaterStatus(null);
                settleScoreDtos.add(item);
            }

        }
    }
}
