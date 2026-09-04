package com.panda.merge.v2.service.helper;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.panda.merge.common.enums.MatchPeriodEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.constant.FootballPeriodValidateEnum;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.dto.settle.PenaltyScoresVo;
import com.panda.merge.dto.settle.UpdateBasketBallSettleScoreDto;
import com.panda.merge.model.*;
import com.panda.merge.respository.MatchSettleFactoryCheckInfoRepository;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.impl.GrayIntervalService;
import com.panda.merge.utils.EndEventUtils;
import com.panda.merge.utils.SettleCheckUtils;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.v2.converter.MatchSettleCheckInfoV2Converter;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.v2.entity.MatchSettleEventEntity;
import com.panda.merge.v2.entity.MatchSettleInfoEntity;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import com.panda.merge.v2.repository.MatchSettleCheckInfoRepository;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.SETTLED;

@Component
@Slf4j
public class MatchSettleCheckInfoHelper {
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    GrayIntervalService grayIntervalService;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;
    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    MatchSettleFactoryCheckInfoRepository matchSettleFactoryCheckInfoRepository;
    @Autowired
    private MatchSettleCheckInfoV2Converter matchSettleCheckInfoV2Converter;

    private static final List<String> allMins15Codes = Arrays.asList(FootballPeriodValidateEnum.GOAL_2.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_3.getCode().toString(),FootballPeriodValidateEnum.GOAL_4.getCode().toString(),FootballPeriodValidateEnum.GOAL_6.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_7.getCode().toString(),FootballPeriodValidateEnum.GOAL_8.getCode().toString());
    @Autowired
    private MatchSettleInfoHelper matchSettleInfoHelper;

    public boolean checkBasketPeriodScoreOrder(MatchSettleScore matchSettleScore) {
        log.info("checkBasketPeriodScoreOrder方法入参:{}",JSONUtil.toJsonStr(matchSettleScore));
        Long standardMatchId = matchSettleScore.getStandardMatchId();
        if (standardMatchId == null || standardMatchId == 0L) {
            return true;
        }
        log.info("checkBasketPeriodScoreOrder查询结算信息请求参数:{}",standardMatchId);
        MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
        log.info("checkBasketPeriodScoreOrder返回查询结算信息:{}", JSONUtil.toJsonStr(matchSettleInfo));
        if (matchSettleInfo == null) {
            return true;
        }
        log.info("checkBasketPeriodScoreOrder查询标准赛事参数:{}",standardMatchId);
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        log.info("checkBasketPeriodScoreOrder返回标准赛事信息:{}",JSONUtil.toJsonStr(standardMatchInfo));
        if (matchSettleInfo.getSettleOrderClosed() != null &&
                matchSettleInfo.getSettleOrderClosed() != 0) {
            return true;
        }
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        statusList.add(0);
        statusList.add(2);
        statusList.add(4);
        //1.根据当前结算编码得到他之前的结算编码
        log.info("checkBasketPeriodScoreOrder查询之前的结算编码参数settleNum:{},matchLength:{}", matchSettleScore.getSettleNum(), standardMatchInfo.getMatchLength());
        List<String> settleNumList = SettleNumUtils.countBasketballScoreSettleNumBefore(matchSettleScore.getSettleNum(), standardMatchInfo.getMatchLength());
        log.info("checkBasketPeriodScoreOrder返回之前的结算编码:{}",JSONUtil.toJsonStr(settleNumList));
        if (settleNumList.size() == 0) {
            return true;
        }
        log.info("checkBasketPeriodScoreOrder结算查询比赛结算分数参数,settleNumList:{},standardMatchId:{},statusList:{}", JSONUtil.toJsonStr(settleNumList), JSONUtil.toJsonStr(standardMatchId), JSONUtil.toJsonStr(statusList));
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList, standardMatchId, statusList);
        log.info("checkBasketPeriodScoreOrder结算返回比赛结算分数参数:{}",JSONUtil.toJsonStr(list));

        //2.判断之前的结算编码是否已经结算，如果没有结算则不能结算返回false
        if (list.size() != 0) {
            return false;
        }
        return true;
    }

    public Long searchEventTimeByScores(MatchSettleScore settleScore) {
        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
        example.createCriteria().andSettleScoreEventIdEqualTo(settleScore.getId());
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


    public void rollbackScores(MatchSettleScore matchSettleScore) {
        List<MatchSettleCheckInfoEntity> list = matchSettleCheckInfoRepository.getBySettleScoreEventIdAndStandardMatchIdAndCheckDataType(matchSettleScore.getId(), matchSettleScore.getStandardMatchId(), MatchSettleCheckConstant.CheckDataType.DATA_SOURCE);
        for (MatchSettleCheckInfoEntity matchSettleCheckInfo : list) {
            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
            matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
            log.info("结算回滚，比分核对数据状态还原 rollbackScores:{}", matchSettleCheckInfo.getSettleScoreEventId());
        }
    }

    public MatchSettleCheckInfoEntity searchCheckInfoByUser(Long scoreEventId, Long standardMatchId, String userName) {
        List<Long> ids = new ArrayList<>();
        ids.add(scoreEventId);
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserName(ids, standardMatchId, userName);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else if (list.size() > 1) {
            log.error("::{} 的记录在用户:{}X 下存在并发记录", scoreEventId, userName);
        }
        return matchSettleCheckInfoV2Converter.convertCheckInfoToEntity(list.get(0));
    }

    public MatchSettleCheckInfoEntity initManualMatchSettleScores(MatchSettleScore matchSettleScore) {
        MatchSettleCheckInfoEntity matchSettleCheckInfo = new MatchSettleCheckInfoEntity();
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setCreateTime(System.currentTimeMillis());
        //核对数据类型1数据商2用户输入
        matchSettleCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
        //1.阶段比分 2次序事件
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
        //比分或事件id
        matchSettleCheckInfo.setSettleScoreEventId(matchSettleScore.getId());
        //赛事id
        matchSettleCheckInfo.setStandardMatchId(matchSettleScore.getStandardMatchId());
        //核对状态
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
        //用户名称
        matchSettleCheckInfo.setUserName(matchSettleScore.getOperater());
        //事件编码
        matchSettleCheckInfo.setEventCode(matchSettleScore.getEventCode());
        //附加字段
        matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
        //主队比分
        matchSettleCheckInfo.setT1(matchSettleScore.getT1());
        //客队比分
        matchSettleCheckInfo.setT2(matchSettleScore.getT2());
        //ID
        matchSettleCheckInfo.setId(IdWorker.getId());
        //是否灰色区间： 1 是 0 不是
        matchSettleCheckInfo.setIsGrey(MatchSettleCheckConstant.IsGrey.IS_NOT_GREY);
        //走水状态
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScore.getGoWaterStatus());
        //次序
        matchSettleCheckInfo.setCheckNumber(matchSettleScore.getCheckNumber());
        return matchSettleCheckInfo;
    }

    public void copyManualMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto, MatchSettleCheckInfoEntity matchSettleCheckInfo) {
        matchSettleCheckInfo.setEventCode(matchSettleScoreDto.getEventCode());
        matchSettleCheckInfo.setExtryInfo(matchSettleScoreDto.getExtryInfo());
        matchSettleCheckInfo.setT1(matchSettleScoreDto.getT1());
        matchSettleCheckInfo.setT2(matchSettleScoreDto.getT2());
        matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfo.setUserName(matchSettleScoreDto.getOperatorName());
        matchSettleCheckInfo.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
        matchSettleCheckInfo.setCheckType(MatchSettleCheckConstant.CheckType.PERIOD_SCORE);
    }

    public void searchCheckStatusByScoresList(List<MatchSettleScoreDto> matchSettleScoreDtos, String operatorName) {
        //有WS推送的情况这个时候没操作人
        if (StringUtils.isEmpty(operatorName)) {
            return;
        }
        Map<Long, MatchSettleScoreDto> matchSettleScoreDtoMap = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        for (MatchSettleScoreDto matchSettleScoreDto : matchSettleScoreDtos) {
            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
        }

        MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDtos.get(0).getStandardMatchId());
        if (StringUtils.isNotEmpty(matchSettleInfo.getLimitUserArray())) {
            JSONArray array = JSONArray.parseArray(matchSettleInfo.getLimitUserArray());
            if (array.contains(operatorName)) {
                for (MatchSettleScoreDto matchSettleScoreDto : matchSettleScoreDtos) {
                    matchSettleScoreDto.setNeedCheck(0);
                }
                return;
            }
        }
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserName(ids, matchSettleScoreDtos.get(0).getStandardMatchId(), operatorName);
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            MatchSettleScoreDto matchSettleScoreDto = matchSettleScoreDtoMap.get(matchSettleCheckInfo.getSettleScoreEventId());
            matchSettleScoreDto.setNeedCheck(0);
            if (matchSettleScoreDto != null) {
                if (!(matchSettleCheckInfo.getCheckStatus() != null && matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM)) {
                    matchSettleScoreDto.setNeedCheck(1);
                }
                if (matchSettleScoreDto.getStatus() != SETTLED) {
                    matchSettleScoreDto.setStatus(matchSettleCheckInfo.getCheckStatus());
                } else {
                    matchSettleScoreDto.setNeedCheck(0);
                }
            }
        }
    }

    public void searchCheckStatusByEventList(List<MatchSettleEventDto> matchSettleScoreDtos, String OperatorName) {
        //有WS推送的情况这个时候没操作人
        if (StringUtils.isEmpty(OperatorName)) {
            return;
        }
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(matchSettleScoreDtos.get(0).getStandardMatchId());
        if (StringUtils.isNotEmpty(matchSettleInfo.getLimitUserArray())) {
            JSONArray array = JSONArray.parseArray(matchSettleInfo.getLimitUserArray());
            if (array.contains(OperatorName)) {
                for (MatchSettleEventDto matchSettleScoreDto : matchSettleScoreDtos) {
                    matchSettleScoreDto.setNeedCheck(0);
                }
                return;
            }
        }
        Map<Long, MatchSettleEventDto> matchSettleScoreDtoMap = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        for (MatchSettleEventDto matchSettleScoreDto : matchSettleScoreDtos) {
            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
        }
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserName(ids, matchSettleScoreDtos.get(0).getStandardMatchId(), OperatorName);
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
        }
    }

    public boolean isAllPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore) {
        boolean flag = true;
        //查询赛事结算表 看是否关闭顺序结算控制 为开  (null or 0)
        Long standardMatchId = matchSettleScore.getStandardMatchId();
        if (standardMatchId == null || standardMatchId == 0L) {
            flag = false;
        }
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(standardMatchId);
        if (matchSettleInfo == null) {
            flag = false;
        }
        if (matchSettleInfo.getSettleOrderClosed() != null &&
                matchSettleInfo.getSettleOrderClosed() != 0) {
            return true;
        }


        List<String> settleNumsBefore;
        if (flag) {
            settleNumsBefore = SettleNumUtils.getPieriodScoresBeforeSettleNum(matchSettleScore.getSettleNum());
        } else {
            settleNumsBefore = SettleNumUtils.getPieriodScoresBeforeSettleNewNum(matchSettleScore.getSettleNum());
        }
        if (settleNumsBefore.size() == 0) {
            return true;
        }
        MatchSettleScoreExample example = new MatchSettleScoreExample();
        //查询当前编辑的比分之前未结算的比分
        example.createCriteria().andSettleNumIn(settleNumsBefore).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).
                andStatusNotEqualTo(SETTLED);
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndNotStatus(settleNumsBefore, matchSettleScore.getStandardMatchId(), SETTLED);
        if (list.size() != 0) {
            return false;
        }
        return true;
    }

private boolean validGoalSettle(MatchSettleScore matchSettleScore) {
    if (matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) {
        return false;
    }
    List<String> childSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(matchSettleScore.getSettleNum());
    String parentSettleNum = FootballPeriodValidateEnum.getParentSettleNumList(matchSettleScore.getSettleNum());
    List<String> brotherSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(parentSettleNum);
    if (CollectionUtils.isEmpty(childSettleNumList) && parentSettleNum == null) {
        return true;
    }

    List<MatchSettleScore> settleScores = matchSettleScoreRepository.getModelsByItems(matchSettleScore.getStandardMatchId(),
            Arrays.asList(SettleEventCodeEnum.FOOTBALL_GOAL.getValue()),null,SETTLED,null,null);
    Map<String, MatchSettleScore> settleScoreMap = settleScores.stream().collect(Collectors.toMap(MatchSettleScore::getSettleNum, t->t, (v1, v2)->v1));

    // valid child nodes
    int sumScoreT1 = 0;
    int sumScoreT2 = 0;
    for(String settleNum : childSettleNumList) {
        MatchSettleScore settleScore = settleScoreMap.getOrDefault(settleNum, null);
        if (settleScore == null) {
            continue;
        }
        if (settleScore.getT1() != null && settleScore.getT1() > 0) {
            sumScoreT1 += settleScore.getT1();
        }
        if (settleScore.getT2() != null && settleScore.getT2() > 0) {
            sumScoreT2 += settleScore.getT2();
        }
    }
    if (sumScoreT1 > matchSettleScore.getT1() || sumScoreT2 > matchSettleScore.getT2()) {
        return false;
    }
    // valid parent nodes
    if (parentSettleNum == null || !settleScoreMap.containsKey(parentSettleNum)) {
        return true;
    }
    sumScoreT1 = matchSettleScore.getT1();
    sumScoreT2 = matchSettleScore.getT2();
    for(String settleNum : brotherSettleNumList){
        MatchSettleScore settleScore = settleScoreMap.getOrDefault(settleNum, null);
        if (settleScore == null) {
            continue;
        }
        if (settleScore.getT1() != null && settleScore.getT1() > 0) {
            sumScoreT1 += settleScore.getT1();
        }
        if (settleScore.getT2() != null && settleScore.getT2() > 0) {
            sumScoreT2 += settleScore.getT2();
        }
    }
    if(sumScoreT1 > settleScoreMap.get(parentSettleNum).getT1() || sumScoreT2 > settleScoreMap.get(parentSettleNum).getT2()){
        return false;
    }
    return true;
}

//    private boolean checkSettleScoreAndAutoSettleNonEvent(MatchSettleInfo matchSettleInfo, MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo) {
//        try {
//            if (checkInfo != null) {
//                matchSettleScore.setT1(checkInfo.getT1());
//                matchSettleScore.setT2(checkInfo.getT2());
//                matchSettleScore.setGoWaterStatus(checkInfo.getGoWaterStatus());
//            }
//            //1 判断顺序 结算开关是否开启，如果没开启则返回 简单校验逻辑();
//            if (matchSettleInfo.getSettleOrderClosed() != null && matchSettleInfo.getSettleOrderClosed() == 1) {
//                return this.normalCheckAutoSettleNonEvent(matchSettleScore, matchSettleInfo);
//            }
//            //2 阶段事件比分是否一致 不一致则 返回 false 不一致
//            Integer x = isPeriodScoreEquile(matchSettleScore, checkInfo, matchSettleInfo);
//            if (x != 0) {
//                log.error("阶段比分结算拦截1: {}-{} 赛事id:{},原因 x:{}", matchSettleScore.getEventName(), matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(), x);
//                return false;
//            }
//            if (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) || matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
//                return true;
//            } else {
//                //3 判断事件和比分是否一致，不一致则返回 事件结算和比分不一致
//                CheckPeriodEventEquileDto checkPeriodEventEquileDto = isPeriodEventEquile(matchSettleScore);
//                if (!checkPeriodEventEquileDto.isPassCheck()) {
//                    log.info("阶段比分结算拦截2: {}-{} 赛事id:{}, CheckPeriodEvent:{}", matchSettleScore.getEventName(), matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(), checkPeriodEventEquileDto);
//                    return false;
//                }
//            }
//            //4 比分一致则判断是否有无 事件  没有则补充 结算
////            if (checkPeriodEventEquileDto.isNeedNoneEvent()) {
////                this.sendNoneEventSettled(matchSettleInfo, matchSettleScore, checkPeriodEventEquileDto);
////            }
//            //5.返回 true
//            return true;
//        } catch (Exception e) {
//            log.error("checkSettleScoreAndAutoSettleNonEvent error:", e);
//            return false;
//        }
//    }
//
//    private boolean checkSettleScoreAndAutoSettleNonEvent(MatchSettleInfo matchSettleInfo, MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo, String linkedId, Map<String, MatchSettleScore> settleNumMap) {
//        try {
//            if (checkInfo != null) {
//                matchSettleScore.setT1(checkInfo.getT1());
//                matchSettleScore.setT2(checkInfo.getT2());
//                matchSettleScore.setGoWaterStatus(checkInfo.getGoWaterStatus());
//            }
//            //1 判断顺序 结算开关是否开启，如果没开启则返回 简单校验逻辑();
//            if (matchSettleInfo.getSettleOrderClosed() != null && matchSettleInfo.getSettleOrderClosed() == 1) {
//                return this.normalCheckAutoSettleNonEvent(matchSettleScore, matchSettleInfo);
//            }
//            //2 阶段事件比分是否一致 不一致则 返回 false 不一致
//            Integer x = isPeriodScoreEquile(matchSettleScore, checkInfo, matchSettleInfo, linkedId, settleNumMap);
//            if (x != 0) {
//                log.error("linkedId::{} 阶段比分结算拦截1: {}-{} 赛事id:{},原因 x:{}", linkedId, matchSettleScore.getEventName(), matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(), x);
//                return false;
//            }
//            if (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) || matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
//                return true;
//            } else {
//                //3 判断事件和比分是否一致，不一致则返回 事件结算和比分不一致
//                CheckPeriodEventEquileDto checkPeriodEventEquileDto = isPeriodEventEquile(matchSettleScore);
//                if (!checkPeriodEventEquileDto.isPassCheck()) {
//                    log.info("linkedId::{} 阶段比分结算拦截2: {}-{} 赛事id:{}, CheckPeriodEvent:{}", linkedId, matchSettleScore.getEventName(), matchSettleScore.getSettleNum(), matchSettleScore.getStandardMatchId(), checkPeriodEventEquileDto);
//                    return false;
//                }
//            }
//            //4 比分一致则判断是否有无 事件  没有则补充 结算
////            if (checkPeriodEventEquileDto.isNeedNoneEvent()) {
////                this.sendNoneEventSettled(matchSettleInfo, matchSettleScore, checkPeriodEventEquileDto);
////            }
//            //5.返回 true
//            return true;
//        } catch (Exception e) {
//            log.error("linkedId::{} checkSettleScoreAndAutoSettleNonEvent error:", linkedId, e);
//            return false;
//        }
//    }

//    private boolean normalCheckAutoSettleNonEvent(MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo) {
//        //只需要校验 全场结算的时候 全场= 上半场 +下半场
//        if (matchSettleScore.getEventCode().equals("corner")) {
//            if (!matchSettleScore.getSettleNum().equals("203")) {
//                return true;
//            }
//        }
//        if (matchSettleScore.getEventCode().equals("fa_card")) {
//            if (!matchSettleScore.getSettleNum().equals("309")) {
//                return true;
//            }
//        }
//        if (checkInfo == null && matchSettleScore.getEventCode().equals("goal")) {
//            return matchSettleInfoHelper.validGoalSettle(matchSettleScore);
//        } else {
//            if (matchSettleScore.getEventCode().equals("goal")) {
//                if (!matchSettleScore.getSettleNum().equals("1010")) {
//                    return true;
//                }
//            }
//            if (isPeriodScoreEquile(matchSettleScore, checkInfo) == 0) {
//                return true;
//            }
//        }
//        return false;
//    }

//    private boolean normalCheckAutoSettleNonEvent(MatchSettleScore matchSettleScore, MatchSettleInfo matchSettleInfo) {
//        //只需要校验 全场结算的时候 全场= 上半场 +下半场
//        if (matchSettleScore.getEventCode().equals("goal")) {
//            if (!matchSettleScore.getSettleNum().equals("1010")) {
//                return true;
//            }
//        }
//        if (matchSettleScore.getEventCode().equals("corner")) {
//            if (!matchSettleScore.getSettleNum().equals("203")) {
//                return true;
//            }
//        }
//        if (matchSettleScore.getEventCode().equals("fa_card")) {
//            if (!matchSettleScore.getSettleNum().equals("309")) {
//                return true;
//            }
//        }
//        if (isPeriodScoreEquile(matchSettleScore, constantCheckInfo, matchSettleInfo) == 0) {
//            return true;
//        }
//        return false;
//    }
//
//    public Integer isPeriodScoreEquile(MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo, MatchSettleInfo matchSettleInfo) {
//        //1.根据当前传入足球的阶段比分的 结算编码 settleNum 得到需要核对的 15分钟比分 或者 5分钟比分 或者半场比分 的结算编码 settleNum
//        //1.2 需要核对的结算编码settleNum list.size ==0 return 0 成功该类型比分无需核对
//        //2.根据上面的 settleNum 和 标准赛事ID  查询 结算阶段比分表已经结算的比分
//        //3.检查 返回已结算的 list size 是否等于  1 步骤的 settleNum list size  不相等则返回结算失败 记 1 还有比分未结算
//        //4.检查已结算的比分之和 是否和  待结算的传参的 比分的主客队是否相等   不相等 返回 2  比分不一致
//        //5. 上述校验通过返回  0  成功
//        /**
//         * 查询当前编辑的比分之前已结算的比分
//         * 判断3个15分钟区间是否都已结算,
//         * 1,未全部结算:如果是上下半场,计算灰色区间进球结算因子,是否等于上下半场比分.
//         * 2,全部结算:核对已经结算的3个15分钟区间比分和是否一致
//         */
//        List<String> settleNumList = MatchPeriodEnum.getFootBallPeriodSettleNumList(matchSettleScore.getSettleNum());
//        if (allMins15Codes.contains(matchSettleScore.getSettleNum()) && (checkInfo != null || matchSettleInfo.getFiveMinSwitch() == 0)) {
//            settleNumList = null;
//        }
//
//        if (settleNumList == null || settleNumList.isEmpty()) {
//            return 0;
//        }
//        if (matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) {
//            return 2;
//        }
//        // 5/15分钟校验
//        if (checkInfo == null) {
//            if ((!(allMins15Codes.contains(matchSettleScore.getSettleNum()) && matchSettleInfo.getFiveMinSwitch() == 0)) && (!validGoalSettle(matchSettleScore))) {
//                return 2;
//            }
//        }
//        MatchSettleScoreExample grayExample = new MatchSettleScoreExample();
//        grayExample.createCriteria().andSettleNumIn(settleNumList).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andIsGreyEqualTo(NOT_CONFIRM);
//        List<MatchSettleScore> grayList = matchSettleScoreMapper.selectByExample(grayExample);
//        if (!grayList.isEmpty()) {
//            if (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) || matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
//                BigDecimal inputScoreT1 = new BigDecimal(matchSettleScore.getT1());
//                BigDecimal inputScoreT2 = new BigDecimal(matchSettleScore.getT2());
//                BigDecimal sumSettleScoreT1 = BigDecimal.ZERO;
//                BigDecimal sumSettleScoreT2 = BigDecimal.ZERO;
//                // 查询出灰色区间结算因子总比分
//                List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoList = matchSettleFactoryCheckInfoRepository.matchSettleFactorCheckInfoListCaseTwo(matchSettleScore.getStandardMatchId(), settleNumList);
//                for (MatchSettleFactorCheckInfo matchSettleFactorCheckInfo : matchSettleFactorCheckInfoList) {
//                    if (matchSettleFactorCheckInfo.getT1() != null) {
//                        sumSettleScoreT1 = sumSettleScoreT1.add(matchSettleFactorCheckInfo.getT1());
//                    }
//                    if (matchSettleFactorCheckInfo.getT2() != null) {
//                        sumSettleScoreT2 = sumSettleScoreT2.add(matchSettleFactorCheckInfo.getT2());
//                    }
//                }
//                //判断输入的上下半场比分,不能小于结算因子
//                if (inputScoreT1.compareTo(sumSettleScoreT1) < NOT_EDIT || inputScoreT2.compareTo(sumSettleScoreT2) < NOT_EDIT) {
//                    return 2;
//                }
//                //输入的上下半场比分等于结算因子,输入比分大于结算因子的情况是:一个数据源,3个15分钟区间有正常进球，当一个数据源加人工比分一致. 可以触发结算
//                if (inputScoreT1.compareTo(sumSettleScoreT1) >= NOT_EDIT && inputScoreT2.compareTo(sumSettleScoreT2) >= NOT_EDIT) {
//                    return 0;
//                }
//            }
//            return 1;
//        } else {
//            MatchSettleScoreExample example = new MatchSettleScoreExample();
//            example.createCriteria().andSettleNumIn(settleNumList).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andStatusEqualTo(SETTLED);
//            List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
//            if (list.isEmpty() || list.size() != settleNumList.size()) {
//                return 1;
//            }
//            Integer sumScoreT1 = 0;
//            Integer sumScoreT2 = 0;
//            for (MatchSettleScore settleScore : list) {
//                if (settleScore.getT1() != null && settleScore.getT1() > 0) {
//                    sumScoreT1 += settleScore.getT1();
//                }
//                if (settleScore.getT2() != null && settleScore.getT2() > 0) {
//                    sumScoreT2 += settleScore.getT2();
//                }
//            }
//            if (matchSettleScore.getT1() != null && matchSettleScore.getT1().equals(sumScoreT1) && matchSettleScore.getT2() != null && matchSettleScore.getT2().equals(sumScoreT2)) {
//                return 0;
//            } else {
//                return 2;
//            }
//        }
//    }
//
//    public Integer isPeriodScoreEquile(MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo, MatchSettleInfo matchSettleInfo, String linkedId, Map<String, MatchSettleScore> settleNumMap) {
//        //1.根据当前传入足球的阶段比分的 结算编码 settleNum 得到需要核对的 15分钟比分 或者 5分钟比分 或者半场比分 的结算编码 settleNum
//        //1.2 需要核对的结算编码settleNum list.size ==0 return 0 成功该类型比分无需核对
//        //2.根据上面的 settleNum 和 标准赛事ID  查询 结算阶段比分表已经结算的比分
//        //3.检查 返回已结算的 list size 是否等于  1 步骤的 settleNum list size  不相等则返回结算失败 记 1 还有比分未结算
//        //4.检查已结算的比分之和 是否和  待结算的传参的 比分的主客队是否相等   不相等 返回 2  比分不一致
//        //5. 上述校验通过返回  0  成功
//        /**
//         * 查询当前编辑的比分之前已结算的比分
//         * 判断3个15分钟区间是否都已结算,
//         * 1,未全部结算:如果是上下半场,计算灰色区间进球结算因子,是否等于上下半场比分.
//         * 2,全部结算:核对已经结算的3个15分钟区间比分和是否一致
//         */
//        List<String> settleNumList = MatchPeriodEnum.getFootBallPeriodSettleNumList(matchSettleScore.getSettleNum());
//        if (allMins15Codes.contains(matchSettleScore.getSettleNum()) && (checkInfo != null || matchSettleInfo.getFiveMinSwitch() == 0)) {
//            settleNumList = null;
//        }
//
//        if (settleNumList == null || settleNumList.isEmpty()) {
//            return 0;
//        }
//        if (matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) {
//            return 2;
//        }
//        // 5/15分钟校验
//        if (checkInfo == null) {
//            if ((!(allMins15Codes.contains(matchSettleScore.getSettleNum()) && matchSettleInfo.getFiveMinSwitch() == 0)) && (!validGoalSettle(matchSettleScore))) {
//                return 2;
//            }
//        }
//        MatchSettleScoreExample grayExample = new MatchSettleScoreExample();
//        grayExample.createCriteria().andSettleNumIn(settleNumList).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andIsGreyEqualTo(NOT_CONFIRM);
//        List<MatchSettleScore> grayList = matchSettleScoreMapper.selectByExample(grayExample);
//        grayList = addUnsettledScoreToList(grayList, settleNumList, linkedId, settleNumMap, true);
//
//        if (!grayList.isEmpty()) {
//            if (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) || matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
//                BigDecimal inputScoreT1 = new BigDecimal(matchSettleScore.getT1());
//                BigDecimal inputScoreT2 = new BigDecimal(matchSettleScore.getT2());
//                BigDecimal sumSettleScoreT1 = BigDecimal.ZERO;
//                BigDecimal sumSettleScoreT2 = BigDecimal.ZERO;
//                // 查询出灰色区间结算因子总比分
//                List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoList = matchSettleFactoryCheckInfoRepository.matchSettleFactorCheckInfoListCaseTwo(matchSettleScore.getStandardMatchId(), settleNumList);
//                for (MatchSettleFactorCheckInfo matchSettleFactorCheckInfo : matchSettleFactorCheckInfoList) {
//                    if (matchSettleFactorCheckInfo.getT1() != null) {
//                        sumSettleScoreT1 = sumSettleScoreT1.add(matchSettleFactorCheckInfo.getT1());
//                    }
//                    if (matchSettleFactorCheckInfo.getT2() != null) {
//                        sumSettleScoreT2 = sumSettleScoreT2.add(matchSettleFactorCheckInfo.getT2());
//                    }
//                }
//                //判断输入的上下半场比分,不能小于结算因子
//                if (inputScoreT1.compareTo(sumSettleScoreT1) < NOT_EDIT || inputScoreT2.compareTo(sumSettleScoreT2) < NOT_EDIT) {
//                    return 2;
//                }
//                //输入的上下半场比分等于结算因子,输入比分大于结算因子的情况是:一个数据源,3个15分钟区间有正常进球，当一个数据源加人工比分一致. 可以触发结算
//                if (inputScoreT1.compareTo(sumSettleScoreT1) >= NOT_EDIT && inputScoreT2.compareTo(sumSettleScoreT2) >= NOT_EDIT) {
//                    return 0;
//                }
//            }
//            return 1;
//        } else {
//            MatchSettleScoreExample example = new MatchSettleScoreExample();
//            example.createCriteria().andSettleNumIn(settleNumList).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andStatusEqualTo(SETTLED);
//            List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
//            list = addUnsettledScoreToList(list, settleNumList, linkedId, settleNumMap, false);
//            if (list.isEmpty() || list.size() != settleNumList.size()) {
//                return 1;
//            }
//            Integer sumScoreT1 = 0;
//            Integer sumScoreT2 = 0;
//            for (MatchSettleScore settleScore : list) {
//                if (settleScore.getT1() != null && settleScore.getT1() > 0) {
//                    sumScoreT1 += settleScore.getT1();
//                }
//                if (settleScore.getT2() != null && settleScore.getT2() > 0) {
//                    sumScoreT2 += settleScore.getT2();
//                }
//            }
//            if (matchSettleScore.getT1() != null && matchSettleScore.getT1().equals(sumScoreT1) && matchSettleScore.getT2() != null && matchSettleScore.getT2().equals(sumScoreT2)) {
//                return 0;
//            } else {
//                return 2;
//            }
//        }
//    }

//    private CheckPeriodEventEquileDto isPeriodEventEquile(MatchSettleScore matchSettleScore) {
//        CheckPeriodEventEquileDto checkPeriodEventEquileDto = new CheckPeriodEventEquileDto();
//        List<String> goalPeriodSettleNum = new ArrayList<>();
//        goalPeriodSettleNum.add("105");
//        goalPeriodSettleNum.add("109");
//        goalPeriodSettleNum.add("1014");
//        goalPeriodSettleNum.add("1018");
//        List<String> cornerPeriodSettleNum = new ArrayList<>();
//        cornerPeriodSettleNum.add("201");
//        cornerPeriodSettleNum.add("202");
//        cornerPeriodSettleNum.add("206");
//        cornerPeriodSettleNum.add("207");
//        List<String> facardPeriodSettleNum = new ArrayList<>();
//        facardPeriodSettleNum.add("304");
//        facardPeriodSettleNum.add("308");
//        facardPeriodSettleNum.add("3013");
//        facardPeriodSettleNum.add("3017");
//
//        Long period = SettleNumUtils.countEventPeriodBySettleScore(matchSettleScore.getSettleNum());
//        if (period == null) {
//            return checkPeriodEventEquileDto;
//        }
//        MatchSettleEventExample eventExample = new MatchSettleEventExample();
//        Integer homeScore = 0;
//        Integer awayScore = 0;
//        Integer eventT1 = 0;
//        Integer eventT2 = 0;
//        //發牌
//        Integer eventFirstT1 = 0;
//        Integer eventFirstT2 = 0;
//        Integer eventSecondT1 = 0;
//        Integer eventSecondT2 = 0;
//        //过滤不需要校验的阶段比分
//        if (matchSettleScore.getEventCode().equals("goal")) {
//            //过滤不需要校验的阶段比分
//            if (!goalPeriodSettleNum.contains(matchSettleScore.getSettleNum())) {
//                return checkPeriodEventEquileDto;
//            }
//            //预设置需要补充
//            checkPeriodEventEquileDto.setNeedNoneEvent(true);
//            eventExample.createCriteria().andPeriodIdEqualTo(period).andEventCodeEqualTo("goal")
//                    .andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andStatusEqualTo(3).andEventTypeEqualTo(1);
//            List<MatchSettleEvent> goalList = matchSettleEventMapper.selectByExample(eventExample);
//            for (MatchSettleEvent matchSettleEvent : goalList) {
//                if ("home".equals(matchSettleEvent.getHomeAway())) {
//                    homeScore++;
//                } else if ("away".equals(matchSettleEvent.getHomeAway())) {
//                    awayScore++;
//                } else {
//                    //增加阶段 如果是上半场，则必须是上半场无进球 下半场 则 必须是下半场无进球 TODO
//
//                    checkPeriodEventEquileDto.setNeedNoneEvent(false);
//                }
//                if (eventT1 < matchSettleEvent.getT1()) {
//                    eventT1 = matchSettleEvent.getT1();
//                }
//                if (eventT2 < matchSettleEvent.getT2()) {
//                    eventT2 = matchSettleEvent.getT2();
//                }
//            }
//            checkPeriodEventEquileDto.setOrderNum(goalList.size() + 1);
//        }
//        if (matchSettleScore.getEventCode().equals("corner")) {
//            //过滤不需要校验的阶段比分
//            if (!cornerPeriodSettleNum.contains(matchSettleScore.getSettleNum())) {
//                return checkPeriodEventEquileDto;
//            }
//            //预设置需要补充
//            checkPeriodEventEquileDto.setNeedNoneEvent(true);
//            eventExample.createCriteria().andPeriodIdEqualTo(period).andEventCodeEqualTo("corner")
//                    .andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andStatusEqualTo(3).andEventTypeEqualTo(1);
//            List<MatchSettleEvent> goalList = matchSettleEventMapper.selectByExample(eventExample);
//            for (MatchSettleEvent matchSettleEvent : goalList) {
//                if ("home".equals(matchSettleEvent.getHomeAway())) {
//                    homeScore++;
//                } else if ("away".equals(matchSettleEvent.getHomeAway())) {
//                    awayScore++;
//                } else {
//                    checkPeriodEventEquileDto.setNeedNoneEvent(false);
//                }
//                if (eventT1 < matchSettleEvent.getT1()) {
//                    eventT1 = matchSettleEvent.getT1();
//                }
//                if (eventT2 < matchSettleEvent.getT2()) {
//                    eventT2 = matchSettleEvent.getT2();
//                }
//            }
//            checkPeriodEventEquileDto.setOrderNum(goalList.size() + 1);
//        }
//        if (matchSettleScore.getEventCode().equals("fa_card")) {
//            //过滤不需要校验的阶段比分
//            if (!facardPeriodSettleNum.contains(matchSettleScore.getSettleNum())) {
//                return checkPeriodEventEquileDto;
//            }
//            //预设置需要补充
//            checkPeriodEventEquileDto.setNeedNoneEvent(true);
//            List<String> bookingSettleNum = new ArrayList<>();
//            bookingSettleNum.add("fa_card");
//            bookingSettleNum.add("yellow_card");
//            bookingSettleNum.add("red_card");
//            eventExample.createCriteria().andPeriodIdEqualTo(period).andEventCodeIn(bookingSettleNum)
//                    .andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andStatusEqualTo(3).andEventTypeEqualTo(1);
//            List<MatchSettleEvent> goalList = matchSettleEventMapper.selectByExample(eventExample);
//            for (MatchSettleEvent matchSettleEvent : goalList) {
//                if (matchSettleEvent.getEventCode().equals("red_card")) {
//                    if ("home".equals(matchSettleEvent.getHomeAway())) {
//                        homeScore += 2;
//                    } else if ("away".equals(matchSettleEvent.getHomeAway())) {
//                        awayScore += 2;
//                    }
//                } else if (matchSettleEvent.getEventCode().equals("yellow_card")) {
//                    if ("home".equals(matchSettleEvent.getHomeAway())) {
//                        homeScore++;
//                    } else if ("away".equals(matchSettleEvent.getHomeAway())) {
//                        awayScore++;
//                    } else {
//                        checkPeriodEventEquileDto.setNeedNoneEvent(false);
//                    }
//                }
//                if (eventT1 < matchSettleEvent.getT1()) {
//                    eventT1 = matchSettleEvent.getT1();
//                }
//                if (eventT2 < matchSettleEvent.getT2()) {
//                    eventT2 = matchSettleEvent.getT2();
//                }
//                if (eventFirstT1 < matchSettleEvent.getFirstT1()) {
//                    eventFirstT1 = matchSettleEvent.getFirstT1();
//                }
//                if (eventFirstT2 < matchSettleEvent.getFirstT2()) {
//                    eventFirstT2 = matchSettleEvent.getFirstT2();
//                }
//                if (eventSecondT1 < matchSettleEvent.getSecondT1()) {
//                    eventSecondT1 = matchSettleEvent.getSecondT1();
//                }
//                if (eventSecondT2 < matchSettleEvent.getSecondT2()) {
//                    eventSecondT2 = matchSettleEvent.getSecondT2();
//                }
//            }
//            checkPeriodEventEquileDto.setOrderNum(goalList.size() + 1);
//        }
//        checkPeriodEventEquileDto.setEventT1(eventT1);
//        checkPeriodEventEquileDto.setEventT2(eventT2);
//        checkPeriodEventEquileDto.setEventFirstT1(eventFirstT1);
//        checkPeriodEventEquileDto.setEventFirstT2(eventFirstT2);
//        checkPeriodEventEquileDto.setEventSecondT1(eventSecondT1);
//        checkPeriodEventEquileDto.setEventSecondT2(eventSecondT2);
//        checkPeriodEventEquileDto.setPeriod(period);
//        if (matchSettleScore.getT1() != null && matchSettleScore.getT2() != null) {
//            if (homeScore == matchSettleScore.getT1() && awayScore == matchSettleScore.getT2()) {
//                return checkPeriodEventEquileDto;
//            } else {
//                checkPeriodEventEquileDto.setPassCheck(false);
//                return checkPeriodEventEquileDto;
//            }
//        }
//        return checkPeriodEventEquileDto;
//    }

    public void endEventSettleByScore(MatchSettleScore matchSettleScore) {
        //0.事件编码分类
        List<String> eventCodes = EndEventUtils.eventCodesFootballByEventCode(matchSettleScore.getEventCode());
        if (eventCodes.size() == 0) {
            return;
        }
        //1.阶段条件获取 上半场 或者全场
        List<Long> periods = EndEventUtils.periodsFootballByScorePeriod(matchSettleScore.getPeriodId());
        //不是31 也不是100 事件则直接返回
        if (periods == null) {
            return;
        }
        //2.查询对应事件编码和阶段编码已经结算的事件
        //3.取比分最大的事件
        List<MatchSettleEventEntity> eventList = matchSettleEventRepository.getByEventCodeAndPeriodIdAndStatusAndStandardMatchIdAndHomeAway(eventCodes, periods, SETTLED, matchSettleScore.getStandardMatchId(), EndEventUtils.HOME_AWAY);
        if (eventList.size() == 0) {
            return;
        }
        Integer t1 = 0;
        Integer t2 = 0;
        String homeAway = "none";
        Long id = null;
        for (MatchSettleEventEntity matchSettleEvent : eventList) {
            if (matchSettleEvent.getT1() != null && matchSettleEvent.getT2() != null) {
                Integer sum = matchSettleEvent.getT1() + matchSettleEvent.getT2();
                if ((t1 + t2) <= sum) {
                    //罚牌比分也是取 事件的 t1 t2
                    t1 = matchSettleEvent.getT1();
                    t2 = matchSettleEvent.getT2();
                    homeAway = matchSettleEvent.getHomeAway();
                    id = matchSettleEvent.getId();
                }
            }
        }
        //id= null 取不到对应事件过滤
        if (id == null) {
            return;
        } else {
            //还有可能 结算的事件比分是0 则无需编辑 或者编辑为none
            if (!EndEventUtils.HOME_AWAY.contains(homeAway)) {
                homeAway = "none";
            }
        }
        //4.根据比分最大的事件和结算事件做比对
        //4.1如果相等 则编辑addition1 或者 addition2 主客队
        if (matchSettleScore.getT1() != null && matchSettleScore.getT2() != null) {
            if (matchSettleScore.getT1().equals(t1) && matchSettleScore.getT2().equals(t2)) {
                //如果是全场打完 则编辑 add2
                if (matchSettleScore.getPeriodId().equals(100L)) {
                    matchSettleScore.setAddition2(homeAway);
                    //如果是上半场休息 则编辑add1
                } else if (matchSettleScore.getPeriodId().equals(31L)) {
                    matchSettleScore.setAddition1(homeAway);
                }
                log.info("结算比分编辑最终事件::赛事id：{}，选择事件id:{},事件阶段:{},事件类型:{} add1:{} add2:{}",
                        matchSettleScore.getStandardMatchId(), id, matchSettleScore.getPeriodId(), matchSettleScore.getEventCode()
                        , matchSettleScore.getAddition1(), matchSettleScore.getAddition2());
            }
        } else {
            //4.2如果不相等 则直接返回
            return;
        }
    }

    public void updateMatchFifteenMinGraySettleFactor(Long standardMatchId, String settleNum) {

        try {

            //1,判断是否是上,下的6个15分钟区间
            String fifteenSettleNum = grayIntervalService.fifteenMinSettleNumMap.get(settleNum);
            if (StringUtils.isAnyEmpty(fifteenSettleNum)) {
                return;
            }
            //2,判断半场是否已经结算,未结算直接返回
            MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
            matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId)
                    .andSettleNumEqualTo(fifteenSettleNum).andStatusEqualTo(SETTLED);
            List<String> settleNums = new ArrayList<>();
            settleNums.add(fifteenSettleNum);
            List<Integer> status = new ArrayList<>();
            status.add(SETTLED);
            List<MatchSettleScore> matchSettleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNums, standardMatchId, status);
            if (matchSettleScoreList.isEmpty()) {
                return;
            }
            //3,半场已经结算,判断已经结算的阶段总比分是否跟半场一致,如果一致,取消半场还未结算的灰色区间
//            MatchSettleScore matchSettleScoreHalfTime = matchSettleScoreList.get(NOT_EDIT);
            MatchSettleScore matchSettleScoreHalfTime = matchSettleScoreList.get(0);
            List<String> settleNumList = MatchPeriodEnum.getFootBallPeriodSettleNumList(fifteenSettleNum);

            List<MatchSettleScore> matchSettleNumList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList, standardMatchId, status);
            if (matchSettleNumList.isEmpty()) {
                return;
            }
            Integer sumScoreT1 = 0;
            Integer sumScoreT2 = 0;
            for (MatchSettleScore settleScore : matchSettleNumList) {
                if (settleScore.getT1() != null && settleScore.getT1() > 0) {
                    sumScoreT1 += settleScore.getT1();
                }
                if (settleScore.getT2() != null && settleScore.getT2() > 0) {
                    sumScoreT2 += settleScore.getT2();
                }
            }
            if (matchSettleScoreHalfTime.getT1() != null && matchSettleScoreHalfTime.getT2() != null && matchSettleScoreHalfTime.getT1().equals(sumScoreT1) && matchSettleScoreHalfTime.getT2().equals(sumScoreT2)) {
                List<Integer> status2 = new ArrayList<>();
                status2.add(0);
                List<MatchSettleScore> matchSettleScoreGrayList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList, standardMatchId, status2);
                for (MatchSettleScore matchSettleScoreGray : matchSettleScoreGrayList) {
                    matchSettleScoreGray.setIsGrey(0);
                    matchSettleScoreGray.setCurrentEventStatus(0);
                    matchSettleScoreRepository.updateById(matchSettleScoreGray);
                }
            }
        } catch (Exception e) {
            log.error("标准赛事Id:" + standardMatchId + ",更新15分钟灰色区间:" + settleNum + ",的结算因子出错:", e);
        }
    }

    public boolean isPeriodScoresBeforeSettledByEvent(MatchSettleEvent matchSettleEvent) {
        String settleNum = null;
        //根据当前进球事件判断需要判断的阶段比分
        if (matchSettleEvent.getPeriodId() == 7l) {
            //获取上半场比分
            settleNum = "105";
        } else if (matchSettleEvent.getPeriodId() == 42l) {
            //获取加时赛上半场比分
            settleNum = "1014";
        }
        //如果为空则不需要判断
        if (settleNum == null) {
            return true;
        }
        MatchSettleScoreExample example = new MatchSettleScoreExample();
        //查询当前编辑的比分之前未结算的比分
        example.createCriteria().andSettleNumEqualTo(settleNum).andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).
                andStatusEqualTo(SETTLED).andEventCodeEqualTo("goal");
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelsByItemsAndSettleNums(matchSettleEvent.getStandardMatchId(), Arrays.asList("goal"), null, SETTLED, Arrays.asList(settleNum));
        if (list.size() != 0) {
            return true;
        }
        return false;
    }

    public Long searchEventTimeByEvent(MatchSettleEvent event, MatchSettleCheckInfo checkInfo) {
        List<Long> ids = new ArrayList<>();
        ids.add(event.getId());
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserNames(ids, null, null);
        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
        Map<String, List<MatchSettleCheckInfo>> checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
        String key = SettleCheckUtils.countSettleEventCompareKey(checkInfo);
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
    public Long searchEventTimeByEvent(MatchSettleEvent matchSettleEvent) {
        List<Long> ids = new ArrayList<>();
        ids.add(matchSettleEvent.getId());
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserNames(ids, null, null);
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
        List<MatchSettleScore> scoreList = matchSettleScoreRepository.getModelsByItems(matchSettleEvent.getStandardMatchId(),eventCodes,periods,SETTLED,matchSettleEvent.getT1(),matchSettleEvent.getT2());
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

    public void searchCheckStatusByPenalty(PenaltyScoresVo penaltyScoresVo, String operatorName) {
        //有WS推送的情况这个时候没操作人
        if (StringUtils.isEmpty(operatorName)) {
            return;
        }

        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(penaltyScoresVo.getStandardMatchId());
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

        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
        checkInfoExample.createCriteria().andStandardMatchIdEqualTo(penaltyScoresVo.getStandardMatchId())
                .andUserNameEqualTo(operatorName).andSettleScoreEventIdIn(ids);
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

    public void rollbackEvent(MatchSettleEvent matchSettleEvent) {
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserNames(Arrays.asList(matchSettleEvent.getId()),matchSettleEvent.getStandardMatchId(),null);
        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
            log.info("结算回滚，事件核对数据状态还原 rollbackEvent:{}", matchSettleCheckInfo.getSettleScoreEventId());
        }
    }
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
    public void updateMatchCurrentEventStatus(Long standardMatchId) {

        try {
            int deleteGoal=0;
            int grayGoal=0;

            MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
            if (matchSettleInfo!=null && matchSettleInfo.getSportId()!=null && (matchSettleInfo.getSportId().intValue()!=1 && matchSettleInfo.getSportId().intValue()!=2)){
                return;
            }
            List<MatchSettleEvent> goalEventList =matchSettleEventRepository.getModelByStandardMatchIdAndNotStatus(standardMatchId,3);
            for (MatchSettleEvent matchSettleEvent : goalEventList) {
                if(matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1){
                    grayGoal=1;
                }
                if(matchSettleEvent.getHasDeleteEvent()!=null&&matchSettleEvent.getHasDeleteEvent()==1){
                    deleteGoal=1;
                }
            }
            List< MatchSettleScore> goalScoreList = matchSettleScoreRepository.getByMatchIdAndEventCodeAndNotStatus(standardMatchId,null,3);

            for (MatchSettleScore matchSettleScore : goalScoreList) {
                if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
                    grayGoal=1;
                }
                if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
                    deleteGoal=1;
                }
            }
            if(deleteGoal==1 && grayGoal==1){
                matchSettleInfo.setIsGray(1);
                matchSettleInfo.setHasDeleteEvent(1);
                matchSettleInfo.setCurrentEventStatus(1);
            }else if(deleteGoal==1 && grayGoal==0){
                matchSettleInfo.setIsGray(0);
                matchSettleInfo.setHasDeleteEvent(1);
                matchSettleInfo.setCurrentEventStatus(2);
            }else if(deleteGoal==0 && grayGoal==1){
                matchSettleInfo.setIsGray(1);
                matchSettleInfo.setHasDeleteEvent(0);
                matchSettleInfo.setCurrentEventStatus(1);
            }else{
                matchSettleInfo.setIsGray(0);
                matchSettleInfo.setHasDeleteEvent(0);
                matchSettleInfo.setCurrentEventStatus(0);
            }
            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
        }catch (Exception e){
            log.error("{标准赛事Id:"+standardMatchId+",修改灰色区间标识出错:",e);
        }
    }


    public boolean checkBasketPeriodScoreOrderV3(MatchSettleScore matchSettleScore) {
        log.info("checkBasketPeriodScoreOrder方法入参:{}",JSONUtil.toJsonStr(matchSettleScore));
        Long standardMatchId = matchSettleScore.getStandardMatchId();
        if (standardMatchId == null || standardMatchId == 0L) {
            return true;
        }
        log.info("checkBasketPeriodScoreOrder查询结算信息请求参数:{}",standardMatchId);
        MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
        log.info("checkBasketPeriodScoreOrder返回查询结算信息:{}", JSONUtil.toJsonStr(matchSettleInfo));
        if (matchSettleInfo == null) {
            return true;
        }
        log.info("checkBasketPeriodScoreOrder查询标准赛事参数:{}",standardMatchId);
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        log.info("checkBasketPeriodScoreOrder返回标准赛事信息:{}",JSONUtil.toJsonStr(standardMatchInfo));
        if (matchSettleInfo.getSettleOrderClosed() != null &&
                matchSettleInfo.getSettleOrderClosed() != 0) {
            return true;
        }
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        statusList.add(0);
        statusList.add(2);
        statusList.add(4);
        //1.根据当前结算编码得到他之前的结算编码
        log.info("checkBasketPeriodScoreOrder查询之前的结算编码参数settleNum:{},matchLength:{}", matchSettleScore.getSettleNum(), standardMatchInfo.getMatchLength());
        List<String> settleNumList = SettleNumUtils.countBasketballScoreSettleNumBefore(matchSettleScore.getSettleNum(), standardMatchInfo.getMatchLength());
        log.info("checkBasketPeriodScoreOrder返回之前的结算编码:{}",JSONUtil.toJsonStr(settleNumList));
        if (settleNumList.size() == 0) {
            return true;
        }
        log.info("checkBasketPeriodScoreOrder结算查询比赛结算分数参数,settleNumList:{},standardMatchId:{},statusList:{}", JSONUtil.toJsonStr(settleNumList), JSONUtil.toJsonStr(standardMatchId), JSONUtil.toJsonStr(statusList));
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList, standardMatchId, statusList);
        log.info("checkBasketPeriodScoreOrder结算返回比赛结算分数参数:{}",JSONUtil.toJsonStr(list));

        //2.判断之前的结算编码是否已经结算，如果没有结算则不能结算返回false
        if (list.size() != 0) {
            return false;
        }
        return true;
    }

    public void endEventSettleByScoreV3(MatchSettleScore matchSettleScore) {
        //0.事件编码分类
        List<String> eventCodes = EndEventUtils.eventCodesFootballByEventCode(matchSettleScore.getEventCode());
        if (eventCodes.size() == 0) {
            return;
        }
        //1.阶段条件获取 上半场 或者全场
        List<Long> periods = EndEventUtils.periodsFootballByScorePeriod(matchSettleScore.getPeriodId());
        //不是31 也不是100 事件则直接返回
        if (periods == null) {
            return;
        }
        //2.查询对应事件编码和阶段编码已经结算的事件
        //3.取比分最大的事件
        List<MatchSettleEventEntity> eventList = matchSettleEventRepository.getByEventCodeAndPeriodIdAndStatusAndStandardMatchIdAndHomeAway(eventCodes, periods, SETTLED, matchSettleScore.getStandardMatchId(), EndEventUtils.HOME_AWAY);
        if (eventList.size() == 0) {
            return;
        }
        Integer t1 = 0;
        Integer t2 = 0;
        String homeAway = "none";
        Long id = null;
        for (MatchSettleEventEntity matchSettleEvent : eventList) {
            if (matchSettleEvent.getT1() != null && matchSettleEvent.getT2() != null) {
                Integer sum = matchSettleEvent.getT1() + matchSettleEvent.getT2();
                if ((t1 + t2) <= sum) {
                    //罚牌比分也是取 事件的 t1 t2
                    t1 = matchSettleEvent.getT1();
                    t2 = matchSettleEvent.getT2();
                    homeAway = matchSettleEvent.getHomeAway();
                    id = matchSettleEvent.getId();
                }
            }
        }
        //id= null 取不到对应事件过滤
        if (id == null) {
            return;
        } else {
            //还有可能 结算的事件比分是0 则无需编辑 或者编辑为none
            if (!EndEventUtils.HOME_AWAY.contains(homeAway)) {
                homeAway = "none";
            }
        }
        //4.根据比分最大的事件和结算事件做比对
        //4.1如果相等 则编辑addition1 或者 addition2 主客队
        if (matchSettleScore.getT1() != null && matchSettleScore.getT2() != null) {
            if (matchSettleScore.getT1().equals(t1) && matchSettleScore.getT2().equals(t2)) {
                //如果是全场打完 则编辑 add2
                if (matchSettleScore.getPeriodId().equals(100L)) {
                    matchSettleScore.setAddition2(homeAway);
                    //如果是上半场休息 则编辑add1
                } else if (matchSettleScore.getPeriodId().equals(31L)) {
                    matchSettleScore.setAddition1(homeAway);
                }
                log.info("结算比分编辑最终事件::赛事id：{}，选择事件id:{},事件阶段:{},事件类型:{} add1:{} add2:{}",
                        matchSettleScore.getStandardMatchId(), id, matchSettleScore.getPeriodId(), matchSettleScore.getEventCode()
                        , matchSettleScore.getAddition1(), matchSettleScore.getAddition2());
            }
        } else {
            //4.2如果不相等 则直接返回
            return;
        }
    }
}
