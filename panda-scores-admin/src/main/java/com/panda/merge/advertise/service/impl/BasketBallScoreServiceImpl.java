package com.panda.merge.advertise.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.service.BasketBallScoreService;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.common.enums.BasketballSixPeriodEnum;
import com.panda.merge.common.enums.PDScoreChangeEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.BasketballScoresExtra;
import com.panda.merge.dto.BasketballScoresPDDto;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballCacheScores;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.PDBaskectBallMatchStartDto;
import com.panda.merge.dto.advertise.PDBasketBallDeleteEventDto;
import com.panda.merge.dto.advertise.PDBasketBallEditEventDto;
import com.panda.merge.dto.advertise.PDBasketBallEditSixScoreDto;
import com.panda.merge.dto.advertise.PDBasketBallPauseDto;
import com.panda.merge.dto.advertise.PDBasketBallSendBallDto;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


@Service
@Slf4j
public class BasketBallScoreServiceImpl implements BasketBallScoreService {
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    IMatchScorePdLogService matchScorePdLogService;

    @Autowired
    private MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;

    @Override
    public MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo,Long periodId) {
        MatchScoreCommonVo matchScoreCommonVo=new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(matchScoresInfo.getT1());
        matchScoreCommonVo.setT2(matchScoresInfo.getT2());
        matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores periodSores= allPeriodScores.get(periodId);
        BasketballScores wholeScore= allPeriodScores.get(WHOLE_MATCH);
        matchScoreCommonVo.setT1(wholeScore.getMatchScore().getHome());
        matchScoreCommonVo.setT2(wholeScore.getMatchScore().getAway());
        Integer matchLength = matchScoresInfo.getMatchLength();
        BasketballScoresExtra scoresExtra = JSON.parseObject(matchScoresInfo.getScoresJsonExtra(),new TypeReference<BasketballScoresExtra>(){});
        if(periodSores==null){
            if(SportPeriodConstant.BasketballPeriod.contans(periodId,matchLength)){
                periodSores= new BasketballScores(0l);
                // 第一次进入加时赛，初始化历史数据及当前加时赛次数为1
                if (periodId == 40) {
                    scoresExtra = new BasketballScoresExtra();
                    Map<Long, CommonItem> historyTimeout = new HashMap<>(16);
                    historyTimeout.put(System.currentTimeMillis(),new CommonItem());
                    scoresExtra.setHistoryTimeout(historyTimeout);
                    scoresExtra.setCurrentTimeout(1);
                    matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(scoresExtra));
                    wholeScore.setTimeout(new CommonItem());
                    allPeriodScores.put(WHOLE_MATCH,wholeScore);
                }
                allPeriodScores.put(periodId,periodSores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
//                matchScoresInfoMapper.updateByPrimaryKey( matchScoresInfo);
                matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            }

            matchScoreCommonVo.setPeriodT1(0);
            matchScoreCommonVo.setPeriodT2(0);
        } else {
            matchScoreCommonVo.setPeriodT1(periodSores.getMatchScore().getHome());
            matchScoreCommonVo.setPeriodT2(periodSores.getMatchScore().getAway());
            if (periodId == 40) {
                Map<Long, CommonItem> historyTimeout = scoresExtra.getHistoryTimeout();
                historyTimeout.put(System.currentTimeMillis(), new CommonItem());
                scoresExtra.setHistoryTimeout(historyTimeout);
                scoresExtra.setCurrentTimeout(scoresExtra.getCurrentTimeout() + 1);
                periodSores.setTimeout(new CommonItem());
                allPeriodScores.put(periodId, periodSores);
                wholeScore.setTimeout(new CommonItem());
                allPeriodScores.put(WHOLE_MATCH,wholeScore);
                matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(scoresExtra));
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
                matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            } else {
                if (periodId != 80) {
                    // 更新常规赛其它阶段对应全场暂停次数
                    Long[] periods = SportPeriodConstant.BasketballPeriod.getWholePeriodsByMatchLength(matchLength);
                    wholeScore.setTimeout(new CommonItem());
                    for (Long periodItem : periods) {
                        if (periodItem == 40) {
                            continue;
                        }
                        BasketballScores basketballScorePeriod = allPeriodScores.get(periodItem);
                        if (!ObjectUtils.isEmpty(basketballScorePeriod)) {
                            CommonItem periodTimeout = basketballScorePeriod.getTimeout();
                            wholeScore.getTimeout().setHome(wholeScore.getTimeout().getHome() + periodTimeout.getHome());
                            wholeScore.getTimeout().setAway(wholeScore.getTimeout().getAway() + periodTimeout.getAway());
                        }
                    }
                    allPeriodScores.put(WHOLE_MATCH, wholeScore);
                    matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                    matchScoresInfo.setModifyTime(System.currentTimeMillis());
                    matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
                }
            }
        }
        if(periodId.equals(999l)){
            matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
            matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        }
        return matchScoreCommonVo;
    }

    @Override
    public boolean checkScoreChangeDelete(MatchScoresInfo matchScoresInfo, ChangeMatchScoreDto changeMatchScoreDto) {
        //1.如果 主队比分 或者 客队比分 比 传入的新比分 大 则为修正事件
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores = JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores periodSores= allPeriodScores.get(changeMatchScoreDto.getPeriod());
        //如果是当前阶段变更则为正常比分变更 暂时
        if(matchScoresInfo.getPeriod().equals(changeMatchScoreDto.getPeriod())){
            return false;
        }else {
            return true;
        }

    }

    @Override
    public MatchScoreCommonVo countScore(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
        MatchScoreCommonVo matchScoreCommonVo=new MatchScoreCommonVo();
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(data.getThirdMatchInfo().getId(), SourceTypeEnum.LIVE_DATA.getCode());
        log.info("::{}::PD篮球报球板比分计算时优先取缓存阶段比分,period={}", matchScoresInfo.getThirdMatchId(), matchScoresInfo.getPeriod());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores periodSores= allPeriodScores.get(changeMatchScoreDto.getPeriod());
        if(periodSores == null){
            periodSores = new BasketballScores(changeMatchScoreDto.getPeriod());
            allPeriodScores.put(changeMatchScoreDto.getPeriod(),periodSores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
//            matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
//            pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
            matchScoreCommonVo.setMatchScoresInfo(matchScoresInfo);
        } else {
            matchScoreCommonVo.setMatchScoresInfo(matchScoresInfo);
        }
        // 记录当前阶段比分变化
        int addT1= changeMatchScoreDto.getPeriodT1()-periodSores.getMatchScore().getHome();
        int addT2= changeMatchScoreDto.getPeriodT2()-periodSores.getMatchScore().getAway();
        matchScoreCommonVo.setAddT1(addT1);
        matchScoreCommonVo.setAddT2(addT2);
        if (addT1 != 0 && addT2 == 0) {
            matchScoreCommonVo.setHomeAway("home");
        }
        if (addT1 == 0 && addT2 != 0) {
            matchScoreCommonVo.setHomeAway("away");
        }
        matchScoreCommonVo.setPeriodT1( changeMatchScoreDto.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(changeMatchScoreDto.getPeriodT2());
        Integer t1 =0;
        Integer t2 =0;
        List<Long> sixPeriod = BasketballSixPeriodEnum.getSixPeriodCode();
        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
            if (entry.getKey().equals(WHOLE_MATCH) || sixPeriod.contains(entry.getKey())) {
                continue;
            }
            t1+=entry.getValue().getMatchScore().getHome();
            t2+=entry.getValue().getMatchScore().getAway();
        }
        t1 += addT1;
        t2 += addT2;
        matchScoreCommonVo.setT1(t1);
        matchScoreCommonVo.setT2(t2);
        return matchScoreCommonVo;
    }

    @Override
    public MatchScoreCommonVo countScoreBasketball(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
        MatchScoreCommonVo matchScoreCommonVo=new MatchScoreCommonVo();
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores periodSores= allPeriodScores.get(changeMatchScoreDto.getPeriod());
        if(periodSores == null){
            periodSores = new BasketballScores(changeMatchScoreDto.getPeriod());
            allPeriodScores.put(changeMatchScoreDto.getPeriod(),periodSores);
            data.getMatchScoresInfo().setT1(matchScoreCommonVo.getT1());
            data.getMatchScoresInfo().setT2(matchScoreCommonVo.getT2());
            data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//            matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        }
//        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        Integer addT1= changeMatchScoreDto.getPeriodT1()-periodSores.getMatchScore().getHome();
        Integer addT2= changeMatchScoreDto.getPeriodT2()-periodSores.getMatchScore().getAway();
        if(addT1!=0){
            matchScoreCommonVo.setHomeAway("home");
        }else if(addT2!=0){
            matchScoreCommonVo.setHomeAway("away");
        }
        matchScoreCommonVo.setPeriodT1( changeMatchScoreDto.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(changeMatchScoreDto.getPeriodT2());
        Integer t1 =0;
        Integer t2 =0;
        List<Long> sixPeriod = BasketballSixPeriodEnum.getSixPeriodCode();
        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
//            if(!entry.getKey().equals(changeMatchScoreDto.getPeriod())){
//            }
            if (entry.getKey().equals(WHOLE_MATCH) || sixPeriod.contains(entry.getKey())) {
                continue;
            }
            t1+=entry.getValue().getMatchScore().getHome();
            t2+=entry.getValue().getMatchScore().getAway();
        }
        t1 += addT1;
        t2 += addT2;
//        t1 += changeMatchScoreDto.getPeriodT1();
//        t2 += changeMatchScoreDto.getPeriodT2();
        matchScoreCommonVo.setT1(t1);
        matchScoreCommonVo.setT2(t2);
        return matchScoreCommonVo;
    }

    @Override
    public int updateScore(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, ChangeMatchScoreDto changeMatchScoreDto) {
        String linkedId = changeMatchScoreDto.getLinkedId();
        Long period = changeMatchScoreDto.getPeriod();
        Long startTimeSecond = changeMatchScoreDto.getMatchTime();
        log.info("::{}::BasketBallScoreServiceImpl_updateScore:{},period:{},score",linkedId,period, JSONObject.toJSONString(matchScoreCommonVo));
        MatchScoresInfo matchScoresInfo=data.getMatchScoresInfo();
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores periodSores= allPeriodScores.get(period);
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(periodSores == null){
            periodSores = new BasketballScores(period);
            allPeriodScores.put(period,periodSores);
        }
        Integer matchLength = data.getMatchTimeInfo().getMatchLength();
        Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period, startTimeSecond, matchLength);
        if (sixPeriod != null) {
            BasketballScores sixPeriodScore = allPeriodScores.get(sixPeriod);
            if (sixPeriodScore == null) {
                sixPeriodScore = new BasketballScores(sixPeriod);
                allPeriodScores.put(sixPeriod, sixPeriodScore);
                CommonItem matchScore = sixPeriodScore.getMatchScore();
                matchScore.setHome(matchScore.getHome() + (changeMatchScoreDto.getPeriodT1() - periodSores.getMatchScore().getHome()));
                matchScore.setAway(matchScore.getAway() + (changeMatchScoreDto.getPeriodT2() - periodSores.getMatchScore().getAway()));
                if (matchScore.getHome() < 0 || matchScore.getAway() < 0) {
                    return PDScoreChangeEnum.NUMBER_LESS_ZERO.getCode();
                }
            } else {
                CommonItem matchScore = sixPeriodScore.getMatchScore();
                matchScore.setHome(matchScore.getHome() + (changeMatchScoreDto.getPeriodT1() - periodSores.getMatchScore().getHome()));
                matchScore.setAway(matchScore.getAway() + (changeMatchScoreDto.getPeriodT2() - periodSores.getMatchScore().getAway()));
                if (matchScore.getHome() < 0 || matchScore.getAway() < 0) {
                    return PDScoreChangeEnum.NUMBER_LESS_ZERO.getCode();
                }
            }
        }
        periodSores.getMatchScore().setHome(matchScoreCommonVo.getPeriodT1());
        periodSores.getMatchScore().setAway(matchScoreCommonVo.getPeriodT2());
        wholeSores.getMatchScore().setHome(matchScoreCommonVo.getT1());
        wholeSores.getMatchScore().setAway(matchScoreCommonVo.getT2());

        ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
        String thirdMatchSourceId = thirdMatchInfo.getThirdMatchSourceId();
        if (changeMatchScoreDto.getPeriodT1() == 0) {
            wholeSores.getThreePointer().setHome(wholeSores.getThreePointer().getHome() - periodSores.getThreePointer().getHome());
            wholeSores.getThreePointerMade().setHome(wholeSores.getThreePointerMade().getHome() - periodSores.getThreePointerMade().getHome());
            wholeSores.getTwoPointer().setHome(wholeSores.getTwoPointer().getHome() - periodSores.getTwoPointer().getHome());
            wholeSores.getTwoPointerMade().setHome(wholeSores.getTwoPointerMade().getHome() - periodSores.getTwoPointerMade().getHome());
            wholeSores.getFreeThrowCount().setHome(wholeSores.getFreeThrowCount().getHome() - periodSores.getFreeThrowCount().getHome());
            wholeSores.getFreeThrowMade().setHome(wholeSores.getFreeThrowMade().getHome() - periodSores.getFreeThrowMade().getHome());

            periodSores.getThreePointer().setHome(0);
            periodSores.getThreePointerMade().setHome(0);
            periodSores.getTwoPointer().setHome(0);
            periodSores.getTwoPointerMade().setHome(0);
            periodSores.getFreeThrowCount().setHome(0);
            periodSores.getFreeThrowMade().setHome(0);

            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
            example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andAddition10IsNull()
                    .andEventCodeEqualTo("score_change").andHomeAwayEqualTo("home").andMatchPeriodIdEqualTo(period).andExtraInfoIsNotNull();
            List<MatchScoresEventInfo> matchScoresEventInfoList = matchScoresEventInfoMapper.selectByExample(example);
            List<Long> list = new ArrayList<>();
            if (!CollectionUtils.isEmpty(matchScoresEventInfoList)) {
                for (MatchScoresEventInfo matchScoresEventInfo : matchScoresEventInfoList) {
                    list.add(matchScoresEventInfo.getId());
                }
            }
            if (!CollectionUtils.isEmpty(list)) {
                matchScoresEventInfoMapper.updateByPrimaryList(list);
            }
        }
        if (changeMatchScoreDto.getPeriodT2() == 0) {
            wholeSores.getThreePointer().setAway(wholeSores.getThreePointer().getAway() - periodSores.getThreePointer().getAway());
            wholeSores.getThreePointerMade().setAway(wholeSores.getThreePointerMade().getAway() - periodSores.getThreePointerMade().getAway());
            wholeSores.getTwoPointer().setAway(wholeSores.getTwoPointer().getAway() - periodSores.getTwoPointer().getAway());
            wholeSores.getTwoPointerMade().setAway(wholeSores.getTwoPointerMade().getAway() - periodSores.getTwoPointerMade().getAway());
            wholeSores.getFreeThrowCount().setAway(wholeSores.getFreeThrowCount().getAway() - periodSores.getFreeThrowCount().getAway());
            wholeSores.getFreeThrowMade().setAway(wholeSores.getFreeThrowMade().getAway() - periodSores.getFreeThrowMade().getAway());

            periodSores.getThreePointer().setAway(0);
            periodSores.getThreePointerMade().setAway(0);
            periodSores.getTwoPointer().setAway(0);
            periodSores.getTwoPointerMade().setAway(0);
            periodSores.getFreeThrowCount().setAway(0);
            periodSores.getFreeThrowMade().setAway(0);

            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
            example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andAddition10IsNull()
                    .andEventCodeEqualTo("score_change").andHomeAwayEqualTo("away").andMatchPeriodIdEqualTo(period).andExtraInfoIsNotNull();
            List<MatchScoresEventInfo> matchScoresEventInfoList = matchScoresEventInfoMapper.selectByExample(example);
            List<Long> list = new ArrayList<>();
            if (!CollectionUtils.isEmpty(matchScoresEventInfoList)) {
                for (MatchScoresEventInfo matchScoresEventInfo : matchScoresEventInfoList) {
                    list.add(matchScoresEventInfo.getId());
                }
            }
            if (!CollectionUtils.isEmpty(list)) {
                matchScoresEventInfoMapper.updateByPrimaryList(list);
            }
        }

        if(matchScoresInfo.getPeriod().equals(period)){
            matchScoresInfo.setPeriodT1(matchScoreCommonVo.getPeriodT1());
            matchScoresInfo.setPeriodT2(matchScoreCommonVo.getPeriodT2());
        }
        matchScoresInfo.setT1(matchScoreCommonVo.getT1());
        matchScoresInfo.setT2(matchScoreCommonVo.getT2());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setSecondsMatchStart(startTimeSecond);
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        log.info("BasketBallScoreServiceImpl_updateScore:{} end score:{}",linkedId,JSONObject.toJSONString(matchScoresInfo));
        return PDScoreChangeEnum.OPERATE_NORMAL.getCode();
    }

    @Override
    public void updateScoreBasketball(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long period, Long startTimeSecond, String linkedId) {
        log.info("BasketBallScoreServiceImpl_updateScore:{},period:{},score",linkedId,period, JSONObject.toJSONString(matchScoreCommonVo));
        MatchScoresInfo matchScoresInfo=data.getMatchScoresInfo();
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores periodSores= allPeriodScores.get(period);
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(periodSores == null){
            periodSores = new BasketballScores(period);
            allPeriodScores.put(period,periodSores);
        }
        Integer matchLength = data.getMatchTimeInfo().getMatchLength();
        Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period, startTimeSecond, matchLength);
        if (sixPeriod != null) {
            BasketballScores sixPeriodScore = allPeriodScores.get(sixPeriod);
            if (sixPeriodScore == null) {
                sixPeriodScore = new BasketballScores(sixPeriod);
                allPeriodScores.put(sixPeriod, sixPeriodScore);
                CommonItem matchScore = sixPeriodScore.getMatchScore();
                if (0 == data.getMatchScoresInfo().getPeriodT1()) {
                    matchScore.setHome(matchScore.getHome() + (matchScoreCommonVo.getPeriodT1() - periodSores.getMatchScore().getHome()));
                } else {
                    matchScore.setHome(matchScore.getHome() + (matchScoreCommonVo.getPeriodT1() - data.getMatchScoresInfo().getPeriodT1()));
                }
                if (0 == data.getMatchScoresInfo().getPeriodT2()) {
                    matchScore.setAway(matchScore.getAway() + (matchScoreCommonVo.getPeriodT2() - periodSores.getMatchScore().getAway()));
                } else {
                    matchScore.setAway(matchScore.getAway() + (matchScoreCommonVo.getPeriodT2() - data.getMatchScoresInfo().getPeriodT2()));
                }
            } else {
                CommonItem matchScore = sixPeriodScore.getMatchScore();
                if (0 == data.getMatchScoresInfo().getPeriodT1()) {
                    matchScore.setHome(matchScore.getHome() + (matchScoreCommonVo.getPeriodT1() - periodSores.getMatchScore().getHome()));
                } else {
                    matchScore.setHome(matchScore.getHome() + (matchScoreCommonVo.getPeriodT1() - data.getMatchScoresInfo().getPeriodT1()));
                }
                if (0 == data.getMatchScoresInfo().getPeriodT2()) {
                    matchScore.setAway(matchScore.getAway() + (matchScoreCommonVo.getPeriodT2() - periodSores.getMatchScore().getAway()));
                } else {
                    matchScore.setAway(matchScore.getAway() + (matchScoreCommonVo.getPeriodT2() - data.getMatchScoresInfo().getPeriodT2()));
                }
            }
        }
        periodSores.getMatchScore().setHome(matchScoreCommonVo.getPeriodT1());
        periodSores.getMatchScore().setAway(matchScoreCommonVo.getPeriodT2());
        wholeSores.getMatchScore().setHome(matchScoreCommonVo.getT1());
        wholeSores.getMatchScore().setAway(matchScoreCommonVo.getT2());
        if(matchScoresInfo.getPeriod().equals(period)){
            matchScoresInfo.setPeriodT1(matchScoreCommonVo.getPeriodT1());
            matchScoresInfo.setPeriodT2(matchScoreCommonVo.getPeriodT2());
        }
        matchScoresInfo.setT1(matchScoreCommonVo.getT1());
        matchScoresInfo.setT2(matchScoreCommonVo.getT2());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setSecondsMatchStart(startTimeSecond);
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        log.info("BasketBallScoreServiceImpl_updateScore:{} end score:{}",linkedId,JSONObject.toJSONString(matchScoresInfo));
    }

    @Override
    public void updateTime(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        data.getMatchScoresInfo().setSecondsMatchStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchScoresInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setSecondFromStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchTimeInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setTimeGo(Integer.parseInt(matchEventInfoDTO.getExtrainfo()));
        data.getMatchTimeInfo().setEventTime(System.currentTimeMillis());
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        data.getMatchTimeInfo().setModifyTime(System.currentTimeMillis());
//        matchTimeInfoMapper.updateByPrimaryKey(data.getMatchTimeInfo());
//        matchScoresInfoMapper.updateByPrimaryKey(  data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(data.getMatchTimeInfo(), null);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
    }

    @Override
    public void updateTimePause(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        data.getMatchScoresInfo().setSecondsMatchStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchScoresInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setSecondFromStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchTimeInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setTimeGo(Integer.parseInt(matchEventInfoDTO.getExtrainfo()));
        data.getMatchTimeInfo().setEventTime(System.currentTimeMillis());
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        data.getMatchTimeInfo().setModifyTime(System.currentTimeMillis());
//        matchTimeInfoMapper.updateByPrimaryKey(data.getMatchTimeInfo());
//        matchScoresInfoMapper.updateByPrimaryKey(  data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(data.getMatchTimeInfo(), null);
//        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
//        pdMatchInfoRepository.onlyUpdateMatchScoresInfoRedis(data.getMatchScoresInfo());
        pdMatchInfoRepository.onlyUpdateMatchScoresInfoRedis(data.getMatchScoresInfo());
    }

    @Override
    public boolean hasExtryPeriod(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return false;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        if(allPeriodScores==null||allPeriodScores.get(40l)==null){
            return false;
        }
        return true;
    }

    @Override
    public BasketballScoresPDDto changeScoreByHomeAwayAndEventCode(Response<MatchScoreAndTimeVo> response, String homeAway, String eventCode) {
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScoresPDDto> allPeriodScores= JsonMapUtils.parseBasketballPDDtoMap(periodFootballScores);
        Long period = response.getData().getMatchTimeInfo().getPeriod();
        BasketballScoresPDDto periodScore = allPeriodScores.get(period);
        BasketballScoresPDDto wholeScore = allPeriodScores.get(WHOLE_MATCH);
        //1.根据事件编码和主客队修改当前阶段比分
        if (ObjectUtil.isNotEmpty(periodScore)) {
            periodScore.changeScoreByPdEventCodeAndHomeAway(homeAway,eventCode);
        }
        //2.根据事件编码和主客队修改总比分
        wholeScore.changeScoreByPdEventCodeAndHomeAway(homeAway,eventCode);
        //3.更新比分表
        response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
//        matchScoresInfoMapper.updateByPrimaryKey(response.getData().getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(), null);
        //4.返回 当前篮球总分呢
        return wholeScore;
    }

    @Override
    public BasketballScores changeScoreBySendBallDto(Response<MatchScoreAndTimeVo> response, PDBasketBallSendBallDto sendBallDto) {
        MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        Long period = matchScoresInfo.getPeriod();
        Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
        Long sixPeriod = MatchPeriodUtils.getBasketSixPeriod(period,sendBallDto.getTimeFromStartSecond(), matchLength);
        BasketballScores periodScore = allPeriodScores.get(period);
        if (ObjectUtils.isEmpty(periodScore)) {
            periodScore = new BasketballScores(period);
        }
        BasketballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
        //6分钟比分生成
        if(sixPeriod!=null && matchLength==7){
            BasketballScores sixPeriodScore =  allPeriodScores.get(sixPeriod);
            if(sixPeriodScore==null){
                sixPeriodScore=new BasketballScores(sixPeriod);
                allPeriodScores.put(sixPeriod,sixPeriodScore);
            }
            sixPeriodScore.changeScoreBysendBallDto(sendBallDto);
            sixPeriodScore.hitTimesOrNot(sendBallDto);
            sixPeriodScore.updateScoreRate(sixPeriodScore);
        }

        //1.根据事件编码和主客队修改当前阶段比分
        if (ObjectUtil.isNotEmpty(periodScore)) {
            periodScore.changeScoreBysendBallDto(sendBallDto);
            periodScore.hitTimesOrNot(sendBallDto);
            periodScore.updateScoreRate(periodScore);
        }
        //2.根据事件编码和主客队修改总比分
        wholeScore.changeScoreBysendBallDto(sendBallDto);
        wholeScore.hitTimesOrNot(sendBallDto);
        wholeScore.updateScoreRate(wholeScore);
        //3.更新比分表
        response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
//        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        if ("home".equals(sendBallDto.getHomeAway())) {
            matchScoresInfo.setPeriodT1(periodScore.getMatchScore().getHome());
            matchScoresInfo.setT1(wholeScore.getMatchScore().getHome());
        }
        if ("away".equals(sendBallDto.getHomeAway())) {
            matchScoresInfo.setPeriodT2(periodScore.getMatchScore().getAway());
            matchScoresInfo.setT2(wholeScore.getMatchScore().getAway());
        }
        matchScoresInfo.setSecondsMatchStart(sendBallDto.getTimeFromStartSecond());
        matchScoresInfo.setRemainingTime(sendBallDto.getTimeFromStartSecond());
        long currentTime = System.currentTimeMillis();
        matchScoresInfo.setEventTime(currentTime);
        matchScoresInfo.setModifyTime(currentTime);
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        matchTimeInfo.setSecondFromStart(sendBallDto.getTimeFromStartSecond());
        matchTimeInfo.setRemainingTime(sendBallDto.getTimeFromStartSecond());
        matchTimeInfo.setEventTime(currentTime);
        matchTimeInfo.setModifyTime(currentTime);
//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
        //4.返回 当前篮球总分呢
        return wholeScore;
    }

    @Override
    public BasketballScores getPeriodScore(Response<MatchScoreAndTimeVo> response) {
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        Long period = response.getData().getMatchTimeInfo().getPeriod();
        BasketballScores periodScore = allPeriodScores.get(period);
        return periodScore;
    }

    @Override
    public BasketballScores changeJumpWonScore(Response<MatchScoreAndTimeVo> response, PDBaskectBallMatchStartDto pdBaskectBallMatchStartDto) {
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        Long period = response.getData().getMatchTimeInfo().getPeriod();
        BasketballScores periodScore = allPeriodScores.get(period);
        BasketballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
        CommonItem commonItem = new CommonItem();
        if(pdBaskectBallMatchStartDto.getJumpWonHomeAway().equals("home")){
            commonItem.setHome(1);
        }else {
            commonItem.setAway(1);
        }
        //1.根据事件编码和主客队修改当前阶段比分
        periodScore.setWonJumpBall(commonItem);
        //2.根据事件编码和主客队修改总比分
        wholeScore.setWonJumpBall(commonItem);
        //3.更新比分表
        response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
//        matchScoresInfoMapper.updateByPrimaryKey(response.getData().getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(), null);
        return wholeScore;
    }

    @Override
    public MatchScoreCommonVo doDeleteEvent(Response<MatchScoreAndTimeVo> response, MatchScoresEventInfo matchScoresEventInfo, PDBasketBallDeleteEventDto pdBasketBallDeleteEventDto) {
        //执行删除  1. 删除 6分钟 2 .删除当前阶段 3.删除 总分 4.负分保护=0
        Long sixPeriod = matchScoresEventInfo.getAddition3() == null ? null : Long.valueOf(matchScoresEventInfo.getAddition3());
        String homeAway = matchScoresEventInfo.getHomeAway();
        String scoreStr =  matchScoresEventInfo.getExtraInfo();
        Integer score = 0;
        if(StringUtils.isEmpty(scoreStr)){
            return null;
        }else {
            score = Integer.parseInt(scoreStr);
        }
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        Long period = matchScoresEventInfo.getMatchPeriodId();
        if(sixPeriod!=null){
            BasketballScores scores = allPeriodScores.get(sixPeriod);
            if(scores!=null){
                delete(scores,homeAway,score);
                scores.minusBallCount(homeAway,score,matchScoresEventInfo,"delete");
            }
        }
        BasketballScores periodScores = allPeriodScores.get(period);
        if(periodScores!=null){
            delete(periodScores,homeAway,score);
            periodScores.minusBallCount(homeAway,score,matchScoresEventInfo,"delete");
        }
        BasketballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
        CommonItem commonItem = new CommonItem();
        CommonItem commonOldItem = new CommonItem();
        if(wholeScore!=null){
            commonOldItem.setHome(wholeScore.getMatchScore().getHome());
            commonOldItem.setAway(wholeScore.getMatchScore().getAway());
            delete(wholeScore,homeAway,score);
            wholeScore.minusBallCount(homeAway,score,matchScoresEventInfo,"delete");
            commonItem.setHome(wholeScore.getMatchScore().getHome());
            commonItem.setAway(wholeScore.getMatchScore().getAway());
        }
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setT1(commonItem.getHome());
        matchScoresInfo.setT2(commonItem.getAway());
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
        long currentTime = System.currentTimeMillis();
        matchScoresInfo.setSecondsMatchStart(pdBasketBallDeleteEventDto.getMatchTimeSecond());
        matchScoresInfo.setRemainingTime(pdBasketBallDeleteEventDto.getMatchTimeSecond());
        matchScoresInfo.setEventTime(currentTime);
        matchScoresInfo.setModifyTime(currentTime);
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        matchTimeInfo.setSecondFromStart(pdBasketBallDeleteEventDto.getMatchTimeSecond());
        matchTimeInfo.setRemainingTime(pdBasketBallDeleteEventDto.getMatchTimeSecond());
        matchTimeInfo.setEventTime(currentTime);
        matchTimeInfo.setModifyTime(currentTime);
//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
        MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(commonItem.getHome());
        matchScoreCommonVo.setT2(commonItem.getAway());
        matchScoreCommonVo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoreCommonVo.setPeriodT2(periodScores.getMatchScore().getAway());
        matchScoreCommonVo.setHomeAway(homeAway);
        matchScoresEventInfo.setEventCode("score_delete");
        matchScorePdLogService.deleteEventLog(response.getData(), pdBasketBallDeleteEventDto, commonOldItem, commonItem, matchScoresEventInfo);
        return matchScoreCommonVo;
    }

    @Override
    public void addPauseScore(Response<MatchScoreAndTimeVo> response, PDBasketBallPauseDto pdBasketBallPauseDto) {
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        Long period = response.getData().getMatchTimeInfo().getPeriod();
        BasketballScores periodScores = allPeriodScores.get(period);
        BasketballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
        if(pdBasketBallPauseDto.getHomeAway().equals("home")){
            if (ObjectUtil.isNotEmpty(periodScores)) {
                periodScores.getTimeout().setHome( periodScores.getTimeout().getHome()+1);
            }
            wholeScore.getTimeout().setHome( wholeScore.getTimeout().getHome()+1);
        }else {
            if (ObjectUtil.isNotEmpty(periodScores)) {
                periodScores.getTimeout().setAway( periodScores.getTimeout().getAway()+1);
            }
            wholeScore.getTimeout().setAway( wholeScore.getTimeout().getAway()+1);
        }
        // 更新当前暂停次数时，同步更新当前对应的历史暂停次数，确保两者数据一致
        if (period == 40) {
            BasketballScoresExtra scoresExtra = JSON.parseObject(response.getData().getMatchScoresInfo().getScoresJsonExtra(), new TypeReference<BasketballScoresExtra>() {});
            Map<Long, CommonItem> historyTimeout = scoresExtra.getHistoryTimeout();
            Optional<Long> max = historyTimeout.keySet().stream().max(Long::compareTo);
            AtomicReference<Long> maxKey = new AtomicReference<>();
            max.ifPresent(maxKey::set);
            if (historyTimeout.containsKey(maxKey.get())) {
                CommonItem commonItem = historyTimeout.get(maxKey.get());
                if (pdBasketBallPauseDto.getHomeAway().equals("home")) {
                    if (ObjectUtil.isNotEmpty(periodScores)) {
                        commonItem.setHome(commonItem.getHome() + 1);
                        historyTimeout.put(maxKey.get(), commonItem);
                        scoresExtra.setHistoryTimeout(historyTimeout);
                        allPeriodScores.put(period, periodScores);
                        wholeScore.setTimeout(commonItem);
                        allPeriodScores.put(WHOLE_MATCH, wholeScore);
                    }
                } else {
                    if (ObjectUtil.isNotEmpty(periodScores)) {
                        commonItem.setAway(commonItem.getAway() + 1);
                        historyTimeout.put(maxKey.get(), commonItem);
                        scoresExtra.setHistoryTimeout(historyTimeout);
                        allPeriodScores.put(period, periodScores);
                        wholeScore.setTimeout(commonItem);
                        allPeriodScores.put(WHOLE_MATCH, wholeScore);
                    }
                }
                response.getData().getMatchScoresInfo().setScoresJsonExtra(JSONObject.toJSONString(scoresExtra));
            }
        } else {
            if (period != 80) {
                // 更新常规赛其它阶段对应全场暂停次数
                Integer matchLength = response.getData().getMatchScoresInfo().getMatchLength();
                Long[] periods = SportPeriodConstant.BasketballPeriod.getWholePeriodsByMatchLength(matchLength);
                wholeScore.setTimeout(new CommonItem());
                for (Long periodItem : periods) {
                    BasketballScores basketballScorePeriod = allPeriodScores.get(periodItem);
                    if (!ObjectUtils.isEmpty(basketballScorePeriod)) {
                        CommonItem periodTimeout = basketballScorePeriod.getTimeout();
                        wholeScore.getTimeout().setHome(wholeScore.getTimeout().getHome() + periodTimeout.getHome());
                        wholeScore.getTimeout().setAway(wholeScore.getTimeout().getAway() + periodTimeout.getAway());
                    }
                }
                allPeriodScores.put(WHOLE_MATCH, wholeScore);
            }
        }
        response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoreInfoRepository.updateScoresInfo(response.getData().getMatchScoresInfo());
    }

    @Override
    public MatchScoreCommonVo editEvent(Response<MatchScoreAndTimeVo> response, MatchScoresEventInfo matchScoresEventInfo, PDBasketBallEditEventDto editEventDto) {
        //执行删除  1. 删除 6分钟 2 .删除当前阶段 3.删除 总分 4.负分保护=0
        Long sixPeriod = matchScoresEventInfo.getAddition3() == null ? null : Long.valueOf(matchScoresEventInfo.getAddition3());
        String homeAway = matchScoresEventInfo.getHomeAway();
        Integer oldScore = matchScoresEventInfo.getExtraInfo() == null ? 0 : Integer.parseInt(matchScoresEventInfo.getExtraInfo());
        String oldEventCode = matchScoresEventInfo.getEventCode();
        boolean flag = "score_miss".equals(oldEventCode) || "3p_miss".equals(oldEventCode) || "2p_miss".equals(oldEventCode);
        if (flag) {
            oldScore = 0;
        }
        Integer newScore = editEventDto.getScore();
        if (editEventDto.getBallEventType() != 2) {
            switch (editEventDto.getScore()) {
                case 1:
                    matchScoresEventInfo.setEventCode("score_miss");
                    newScore = 0;
                    break;
                case 2:
                    matchScoresEventInfo.setEventCode("2p_miss");
                    newScore = 0;
                    break;
                case 3:
                    matchScoresEventInfo.setEventCode("3p_miss");
                    newScore = 0;
                    break;
                default:
                    return null;
            }
        }
        if (editEventDto.getBallEventType() == 2) {
            matchScoresEventInfo.setEventCode("score_change");
        }
        int addT1 = 0;
        int addT2 = 0;
        if ("home".equals(homeAway)) {
            addT1 = newScore - oldScore;
        }
        if ("away".equals(homeAway)) {
            addT2 = newScore - oldScore;
        }
        PDBasketBallSendBallDto dto = new PDBasketBallSendBallDto();
        dto.setHomeAway(homeAway);
        dto.setBallEventType(editEventDto.getBallEventType());
        if (newScore == 1 && ObjectUtils.isEmpty(editEventDto.getFreeThrowNumber())) {
            dto.setFreeThrow(true);
            dto.setFreeThrowNumber(newScore);
            dto.setScore(editEventDto.getScore());
        } else if (!ObjectUtils.isEmpty(editEventDto.getFreeThrowNumber())) {
            dto.setFreeThrow(true);
            dto.setScore(editEventDto.getScore());
            dto.setFreeThrowNumber(editEventDto.getFreeThrowNumber());
        } else {
            dto.setScore(editEventDto.getScore());
        }
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        Long period = matchScoresEventInfo.getMatchPeriodId();
        if(sixPeriod!=null){
            BasketballScores scores = allPeriodScores.get(sixPeriod);
            if(scores!=null){
                add(scores,addT1,addT2);
                scores.hitTimesOrNot(dto);
                scores. minusBallCount(homeAway,Integer.valueOf(matchScoresEventInfo.getExtraInfo()),matchScoresEventInfo,oldEventCode);
            }
        }
        BasketballScores periodScores = allPeriodScores.get(period);
        if(periodScores!=null){
            add(periodScores,addT1,addT2);
            periodScores.hitTimesOrNot(dto);
            periodScores.minusBallCount(homeAway,Integer.valueOf(matchScoresEventInfo.getExtraInfo()),matchScoresEventInfo,oldEventCode);
        }
        BasketballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
        CommonItem commonItem = new CommonItem();
        CommonItem commonOldItem = new CommonItem();
        if(wholeScore!=null){
            commonOldItem.setHome(wholeScore.getMatchScore().getHome());
            commonOldItem.setAway(wholeScore.getMatchScore().getAway());
            add(wholeScore,addT1,addT2);
            wholeScore.hitTimesOrNot(dto);
            wholeScore.minusBallCount(homeAway,Integer.valueOf(matchScoresEventInfo.getExtraInfo()),matchScoresEventInfo,oldEventCode);
            commonItem.setHome(wholeScore.getMatchScore().getHome());
            commonItem.setAway(wholeScore.getMatchScore().getAway());
        }
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        matchScoresInfo.setT1(commonItem.getHome());
        matchScoresInfo.setT2(commonItem.getAway());
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        long currentTime = System.currentTimeMillis();
        matchScoresInfo.setSecondsMatchStart(editEventDto.getMatchTimeSecond());
        matchScoresInfo.setRemainingTime(editEventDto.getMatchTimeSecond());
        matchScoresInfo.setEventTime(currentTime);
        matchScoresInfo.setModifyTime(currentTime);
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        matchTimeInfo.setSecondFromStart(editEventDto.getMatchTimeSecond());
        matchTimeInfo.setRemainingTime(editEventDto.getMatchTimeSecond());
        matchTimeInfo.setEventTime(currentTime);
        matchTimeInfo.setModifyTime(currentTime);
//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
        MatchScoreCommonVo matchScoreCommonVo =new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(wholeScore.getMatchScore().getHome());
        matchScoreCommonVo.setT2(wholeScore.getMatchScore().getAway());
        matchScoreCommonVo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoreCommonVo.setPeriodT2(periodScores.getMatchScore().getAway());
        matchScoreCommonVo.setHomeAway(homeAway);
//        if (newScore > 0) {
//            matchScoresEventInfo.setEventCode("score_change");
//        } else {
//            matchScoresEventInfo.setEventCode("score_miss");
//        }
        matchScorePdLogService.editEventLog(response.getData(), editEventDto, commonOldItem, commonItem, matchScoresEventInfo);
        return matchScoreCommonVo;
    }

    @Override
    public int changeSixPeriodScore(Response<MatchScoreAndTimeVo> response, PDBasketBallEditSixScoreDto editSixScoreDto) {
        Map<Long, CommonItem> sixPeriodMap = MatchPeriodUtils.getSixPeriodMap(editSixScoreDto);
        Long sixPeriodId = 0L;
        CommonItem sixPeriodScore = new CommonItem();
        for (Map.Entry<Long, CommonItem> entry : sixPeriodMap.entrySet()) {
            sixPeriodId = entry.getKey();
            sixPeriodScore = entry.getValue();
        }
        String reg = "^[0-9]+$";
        boolean sixPeriodCheck = sixPeriodScore.getHome() != null && !String.valueOf(sixPeriodScore.getHome()).matches(reg)
                || sixPeriodScore.getAway() != null && !String.valueOf(sixPeriodScore.getAway()).matches(reg);
        if (sixPeriodCheck) {
            return PDScoreChangeEnum.NUMBER_LESS_EQUAL_ZERO.getCode();
        }
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        Map<Long, BasketballScores> allScoresMap = JSON.parseObject(matchScoresInfo.getScoresJson(), new TypeReference<Map<Long, BasketballScores>>() {
        });
        BasketballScores sixPeriodScoreDb = allScoresMap.get(sixPeriodId);
        if(sixPeriodScoreDb==null){
            sixPeriodScoreDb = new BasketballScores(sixPeriodId);
        }
        if (sixPeriodScore.getHome().equals(sixPeriodScoreDb.getMatchScore().getHome()) && sixPeriodScore.getAway().equals(sixPeriodScoreDb.getMatchScore().getAway())) {
            return PDScoreChangeEnum.SCORE_EQUAL.getCode();
        }
        // 更新6分钟、阶段、总比分
        Integer sixPeriodHomeSub = sixPeriodScoreDb.getMatchScore().getHome() - sixPeriodScore.getHome();
        Integer sixPeriodAwaySub = sixPeriodScoreDb.getMatchScore().getAway() - sixPeriodScore.getAway();
        sixPeriodScoreDb.getMatchScore().setHome(sixPeriodScore.getHome());
        sixPeriodScoreDb.getMatchScore().setAway(sixPeriodScore.getAway());
        BasketballScores periodScoreDb = allScoresMap.get(editSixScoreDto.getPeriodId());
        periodScoreDb.getMatchScore().setHome(periodScoreDb.getMatchScore().getHome() - sixPeriodHomeSub);
        periodScoreDb.getMatchScore().setAway(periodScoreDb.getMatchScore().getAway() - sixPeriodAwaySub);
        BasketballScores wholeScoreDb = allScoresMap.get(WHOLE_MATCH);
        wholeScoreDb.getMatchScore().setHome(wholeScoreDb.getMatchScore().getHome() - sixPeriodHomeSub);
        wholeScoreDb.getMatchScore().setAway(wholeScoreDb.getMatchScore().getAway() - sixPeriodAwaySub);

        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        String thirdMatchSourceId = thirdMatchInfo.getThirdMatchSourceId();
        if (sixPeriodScore.getHome() == 0) {
            periodScoreDb.getThreePointer().setHome(periodScoreDb.getThreePointer().getHome() - sixPeriodScoreDb.getThreePointer().getHome());
            periodScoreDb.getThreePointerMade().setHome(periodScoreDb.getThreePointerMade().getHome() - sixPeriodScoreDb.getThreePointerMade().getHome());
            periodScoreDb.getTwoPointer().setHome(periodScoreDb.getTwoPointer().getHome() - sixPeriodScoreDb.getTwoPointer().getHome());
            periodScoreDb.getTwoPointerMade().setHome(periodScoreDb.getTwoPointerMade().getHome() - sixPeriodScoreDb.getTwoPointerMade().getHome());
            periodScoreDb.getFreeThrowCount().setHome(periodScoreDb.getFreeThrowCount().getHome() - sixPeriodScoreDb.getFreeThrowCount().getHome());
            periodScoreDb.getFreeThrowMade().setHome(periodScoreDb.getFreeThrowMade().getHome() - sixPeriodScoreDb.getFreeThrowMade().getHome());

            wholeScoreDb.getThreePointer().setHome(wholeScoreDb.getThreePointer().getHome() - sixPeriodScoreDb.getThreePointer().getHome());
            wholeScoreDb.getThreePointerMade().setHome(wholeScoreDb.getThreePointerMade().getHome() - sixPeriodScoreDb.getThreePointerMade().getHome());
            wholeScoreDb.getTwoPointer().setHome(wholeScoreDb.getTwoPointer().getHome() - sixPeriodScoreDb.getTwoPointer().getHome());
            wholeScoreDb.getTwoPointerMade().setHome(wholeScoreDb.getTwoPointerMade().getHome() - sixPeriodScoreDb.getTwoPointerMade().getHome());
            wholeScoreDb.getFreeThrowCount().setHome(wholeScoreDb.getFreeThrowCount().getHome() - sixPeriodScoreDb.getFreeThrowCount().getHome());
            wholeScoreDb.getFreeThrowMade().setHome(wholeScoreDb.getFreeThrowMade().getHome() - sixPeriodScoreDb.getFreeThrowMade().getHome());

            sixPeriodScoreDb.getThreePointer().setHome(0);
            sixPeriodScoreDb.getThreePointerMade().setHome(0);
            sixPeriodScoreDb.getTwoPointer().setHome(0);
            sixPeriodScoreDb.getTwoPointerMade().setHome(0);
            sixPeriodScoreDb.getFreeThrowCount().setHome(0);
            sixPeriodScoreDb.getFreeThrowMade().setHome(0);

            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
            example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andAddition10IsNull()
                    .andEventCodeEqualTo("score_change").andAddition3EqualTo(String.valueOf(sixPeriodId)).andHomeAwayEqualTo("home");
            List<MatchScoresEventInfo> matchScoresEventInfoList = matchScoresEventInfoMapper.selectByExample(example);
            List<Long> list = new ArrayList<>();
            if (!CollectionUtils.isEmpty(matchScoresEventInfoList)) {
                for (MatchScoresEventInfo matchScoresEventInfo : matchScoresEventInfoList) {
                    list.add(matchScoresEventInfo.getId());
                }
            }
            if (!CollectionUtils.isEmpty(list)) {
                matchScoresEventInfoMapper.updateByPrimaryList(list);
            }
        }
        if (sixPeriodScore.getAway() == 0) {
            periodScoreDb.getThreePointer().setAway(periodScoreDb.getThreePointer().getAway() - sixPeriodScoreDb.getThreePointer().getAway());
            periodScoreDb.getThreePointerMade().setAway(periodScoreDb.getThreePointerMade().getAway() - sixPeriodScoreDb.getThreePointerMade().getAway());
            periodScoreDb.getTwoPointer().setAway(periodScoreDb.getTwoPointer().getAway() - sixPeriodScoreDb.getTwoPointer().getAway());
            periodScoreDb.getTwoPointerMade().setAway(periodScoreDb.getTwoPointerMade().getAway() - sixPeriodScoreDb.getTwoPointerMade().getAway());
            periodScoreDb.getFreeThrowCount().setAway(periodScoreDb.getFreeThrowCount().getAway() - sixPeriodScoreDb.getFreeThrowCount().getAway());
            periodScoreDb.getFreeThrowMade().setAway(periodScoreDb.getFreeThrowMade().getAway() - sixPeriodScoreDb.getFreeThrowMade().getAway());

            wholeScoreDb.getThreePointer().setAway(wholeScoreDb.getThreePointer().getAway() - sixPeriodScoreDb.getThreePointer().getAway());
            wholeScoreDb.getThreePointerMade().setAway(wholeScoreDb.getThreePointerMade().getAway() - sixPeriodScoreDb.getThreePointerMade().getAway());
            wholeScoreDb.getTwoPointer().setAway(wholeScoreDb.getTwoPointer().getAway() - sixPeriodScoreDb.getTwoPointer().getAway());
            wholeScoreDb.getTwoPointerMade().setAway(wholeScoreDb.getTwoPointerMade().getAway() - sixPeriodScoreDb.getTwoPointerMade().getAway());
            wholeScoreDb.getFreeThrowCount().setAway(wholeScoreDb.getFreeThrowCount().getAway() - sixPeriodScoreDb.getFreeThrowCount().getAway());
            wholeScoreDb.getFreeThrowMade().setAway(wholeScoreDb.getFreeThrowMade().getAway() - sixPeriodScoreDb.getFreeThrowMade().getAway());

            sixPeriodScoreDb.getThreePointer().setAway(0);
            sixPeriodScoreDb.getThreePointerMade().setAway(0);
            sixPeriodScoreDb.getTwoPointer().setAway(0);
            sixPeriodScoreDb.getTwoPointerMade().setAway(0);
            sixPeriodScoreDb.getFreeThrowCount().setAway(0);
            sixPeriodScoreDb.getFreeThrowMade().setAway(0);

            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
            example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andAddition10IsNull()
                    .andEventCodeEqualTo("score_change").andAddition3EqualTo(String.valueOf(sixPeriodId)).andHomeAwayEqualTo("away");
            List<MatchScoresEventInfo> matchScoresEventInfoList = matchScoresEventInfoMapper.selectByExample(example);
            List<Long> list = new ArrayList<>();
            if (!CollectionUtils.isEmpty(matchScoresEventInfoList)) {
                for (MatchScoresEventInfo matchScoresEventInfo : matchScoresEventInfoList) {
                    list.add(matchScoresEventInfo.getId());
                }
            }
            if (!CollectionUtils.isEmpty(list)) {
                matchScoresEventInfoMapper.updateByPrimaryList(list);
            }
        }
        matchScoresInfo.setT1(wholeScoreDb.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeScoreDb.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScoreDb.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScoreDb.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allScoresMap));
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        return PDScoreChangeEnum.OPERATE_NORMAL.getCode();
    }

    private void add(BasketballScores scores, Integer addT1, Integer addT2) {
        scores.getMatchScore().setHome(scores.getMatchScore().getHome()+addT1);
        scores.getMatchScore().setAway(scores.getMatchScore().getAway()+addT2);
    }

    private void delete(BasketballScores scores, String homeAway, Integer score) {
        if("home".equals(homeAway)){
            scores.getMatchScore().setHome(scores.getMatchScore().getHome()-score);
            if(scores.getMatchScore().getHome()<0){
                scores.getMatchScore().setHome(0);
            }
        }else{
            scores.getMatchScore().setAway(scores.getMatchScore().getAway()-score);
            if(scores.getMatchScore().getAway()<0){
                scores.getMatchScore().setAway(0);
            }
        }
    }


}
