package com.panda.merge.advertise.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.*;
import com.panda.merge.advertise.event.BasketEventService;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.BasketBallAdvertiseService;
import com.panda.merge.advertise.service.BasketBallScoreService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.common.enums.PDScoreChangeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.FreeThrowDetailDto;
import com.panda.merge.dto.advertise.PDBasketBallPauseDto;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchEventInfoExample;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresInfoExample;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.repository.ThirdMatchInfoRepository;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Service
@Slf4j
public class BasketBallAdvertiseServiceImpl implements BasketBallAdvertiseService {
    @Autowired
    BasketBallScoreService basketBallScoreService;
    @Autowired
    IScoresService scoresService;
    @Autowired
    BasketEventService basketEventService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    CommonEventService commonEventService;
    @Autowired
    RedisService redisService;
    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    private MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    private ThirdMatchInfoRepository thirdMatchInfoRepository;
    @Autowired
    private StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    private MatchScoreInfoRepository matchScoreInfoRepository;

    public   boolean createMatchScoresInfo(ThirdMatchInfo thirdMatchInfo,String dataSourceCodeOld){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //判断比分是否存在存在则返回已经创建
        MatchScoresInfoExample matchScoresInfoExample = new MatchScoresInfoExample();
        matchScoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfo.getId() );
//        List<MatchScoresInfo> virtualRelations = matchScoresInfoMapper.selectByExample(matchScoresInfoExample);
        MatchScoresInfo virtualRelation = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
        log.info("::{}::创建标准赛事时获取到比分：{}", thirdMatchInfo.getId(), JSONObject.toJSONString(virtualRelation));
        // 当存在比分数据时，查询赛事时间表，如该表为空，按比分表数据初始化赛事时间表
        if (!ObjectUtils.isEmpty(virtualRelation)) {
            MatchTimeInfo matchTimeInfoOld = matchTimeInfoRepository.selectByPrimaryKey(virtualRelation.getId());
            if (ObjectUtils.isEmpty(matchTimeInfoOld)) {
                StandardMatchInfo standardMatchInfo = null;
                if (null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId() > 0) {
                    standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(thirdMatchInfo.getReferenceId());
                }
                Integer roundType = Objects.isNull(standardMatchInfo) ? thirdMatchInfo.getRoundType() : standardMatchInfo.getRoundType();
                scoresService.initMatchTimeInfoByMatchScoresInfo(virtualRelation, roundType);
            }
        }
        MatchScoresInfo matchScoresInfo =null;
        if (ObjectUtils.isEmpty(virtualRelation)) {
            matchScoresInfo = scoresService.createPDMatchScoresInfo(thirdMatchInfo);
            //复制切换数据源之前的数据
            String matchScoreKey ="MATCH_INFO_SCORE_ID:"+thirdMatchInfo.getReferenceId();
            Object ido= redisService.get(matchScoreKey);
            Long scoreId=null;
            if(ido!=null){
                scoreId= Long.parseLong(ido.toString());
            }
            if(scoreId!=null){
//                MatchScoresInfo oldScore = matchScoresInfoMapper.selectByPrimaryKey(scoreId);
                MatchScoresInfo oldScore = matchScoreInfoRepository.selectByPrimaryKey(scoreId);
                if(oldScore==null){
                    log.info("原数据源尚未生成数据");
                    return false;
                }
                if (oldScore.getPeriod()!=null && oldScore.getPeriod().intValue()>0&&(!(oldScore.getDataSourceCode().equals("PD")||oldScore.getDataSourceCode().equals("PD2")))){
                    redisService.set(MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE +":"+ matchScoresInfo.getThirdMatchId(), 0,129600L);
                }
//                ThirdMatchInfo oldMatch =thirdMatchInfoMapper.selectByPrimaryKey(oldScore.getThirdMatchId());
                ThirdMatchInfo oldMatch = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(oldScore.getThirdMatchId());
//                MatchTimeInfoExample matchTimeInfoEx = new MatchTimeInfoExample();
//                matchTimeInfoEx.createCriteria().andThirdMatchIdEqualTo(oldScore.getThirdMatchId() ).andDataSourceTypeEqualTo("1");
//                List<MatchTimeInfo> matchTimeInfos = matchTimeInfoMapper.selectByExample(matchTimeInfoEx);
                MatchTimeInfo matchTimeInfo = matchTimeInfoRepository.selectByThirdMatchId(oldScore.getThirdMatchId(),SourceTypeEnum.LIVE_DATA.getCode());
//                matchTimeInfoEx = new MatchTimeInfoExample();
//                matchTimeInfoEx.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfo.getId() ).andDataSourceTypeEqualTo("1");
//                List<MatchTimeInfo> matchTimeNewInfos = matchTimeInfoMapper.selectByExample(matchTimeInfoEx);
                MatchTimeInfo matchTimeNewInfo = matchTimeInfoRepository.selectByThirdMatchId(thirdMatchInfo.getId(),SourceTypeEnum.LIVE_DATA.getCode());;
//                MatchTimeInfo matchTimeInfo=matchTimeInfos.get(0);
//                MatchTimeInfo matchTimeNewInfo=matchTimeNewInfos.get(0);
                if(!matchScoresInfo.getSportId().equals(5L)){
                    //复制比分时间阶段
                    if(((oldScore.getDataSourceCode().equals("PD")&&matchScoresInfo.getDataSourceCode().equals("PD2"))
                            ||(oldScore.getDataSourceCode().equals("PD2")&&matchScoresInfo.getDataSourceCode().equals("PD")))){

                    }else {
                        copyMatchScoreAndTime(oldScore,matchScoresInfo,matchTimeInfo,matchTimeNewInfo);
                    }
                }
                //复制事件
//                copyMatchEvent(thirdMatchInfo,oldMatch);
            }
            stopWatch.stop();
            log.info("BasketBallAdvertiseServiceImpl-createMatchScoresInfo-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchInfo.getId());
            if(matchScoresInfo==null){
                return false;
            }else {
                return true;
            }
        }else {
            return false;
        }
    }

    /**
     * 需要分表操作
     * */
    private void copyMatchEvent(ThirdMatchInfo thirdMatchInfo, ThirdMatchInfo oldMatch) {
        List<String> list =new ArrayList<>();
        list.add("corner"); list.add("red_card"); list.add("goal"); list.add("yellow_card"); list.add("kick_off_team");
       MatchEventInfoExample matchEventInfoExample= new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().andEventCodeIn(list).andThirdMatchIdEqualTo(oldMatch.getId()).andDataSourceCodeEqualTo(oldMatch.getDataSourceCode());
        List<MatchEventInfo> eventInfoList =matchEventInfoMapper.selectByExample(matchEventInfoExample);
        List<MatchScoresEventInfo> matchScoresEventInfos=new ArrayList<>();
        for (MatchEventInfo matchEventInfo : eventInfoList) {
            MatchScoresEventInfo matchScoresEventInfo =new MatchScoresEventInfo();
            BeanUtils.copyProperties(matchEventInfo,matchScoresEventInfo);
            matchScoresEventInfo.setDataSourceCode("PD");
            matchScoresEventInfo.setId(IdWorker.getId());
            matchScoresEventInfo.setThirdMatchId(thirdMatchInfo.getId());
            matchScoresEventInfo.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
            matchScoresEventInfo.setAddition9("true");
            matchScoresEventInfos.add(matchScoresEventInfo);
            matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        }

    }

    private void copyMatchScoreAndTime(MatchScoresInfo oldScore, MatchScoresInfo matchScoresInfo, MatchTimeInfo oldTimeInfo, MatchTimeInfo matchTimeInfo) {
        //1.复制比分
        Long newScoreId =matchScoresInfo.getId();
        Long newTimeId =matchTimeInfo.getId();
        Long thirdMatchId=matchScoresInfo.getThirdMatchId();
        String thirdSourceMatchId=matchScoresInfo.getThirdMatchSourceId();
        String dataSourceCode =  matchScoresInfo.getDataSourceCode();
        //2.复制时间
        BeanUtils.copyProperties(oldScore,matchScoresInfo);
        BeanUtils.copyProperties(oldTimeInfo,matchTimeInfo);
        // oldTimeInfo 阶段period大于0滚球状态。就标记为中场切换PD,就存redis. key 过期时间36小时
        // 结算那边。判断redis, PD 存在 return 返回，不参与结算

        //重新计算总分
        if(oldScore.getSportId().equals(2l)){
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
            BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            Integer t1 =0,t2=0;
            for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
                if(entry.getKey().equals(WHOLE_MATCH)){
                    continue;
                }
                t1+=entry.getValue().getMatchScore().getHome();
                t2+=entry.getValue().getMatchScore().getAway();
            }
            wholeSores.getMatchScore().setHome(t1);
            wholeSores.getMatchScore().setAway(t2);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
            matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        }else {
            matchScoresInfo.setT1(oldScore.getT1());
            matchScoresInfo.setT2(oldScore.getT2());
            matchScoresInfo.setPeriodT1(oldScore.getPeriodT1());
            matchScoresInfo.setPeriodT2(oldScore.getPeriodT2());
            //足球比分复制
            matchScoresInfo.setScoresJson(oldScore.getScoresJson());
            matchScoresInfo.setScoresJsonExtra(oldScore.getScoresJsonExtra());
        }

        matchScoresInfo.setId(newScoreId);
        matchScoresInfo.setThirdMatchId(thirdMatchId);
        matchScoresInfo.setDataSourceCode(dataSourceCode);
        matchScoresInfo.setThirdMatchSourceId(thirdSourceMatchId);
        matchTimeInfo.setId(newTimeId);
        matchTimeInfo.setThirdMatchId(thirdMatchId);
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
    }

    @Override
    public Response matchStart(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId) {
        //1.计算阶段
        Long nextPeriod= MatchPeriodUtils.BascketBallPeriod.getNextPeriod(matchScoreAndTimeVo.getMatchTimeInfo().getPeriod(),matchScoreAndTimeVo.getMatchTimeInfo().getMatchLength());
        //2 计算时长
        Integer minuts= Constant.BasketBallConstant.matchLenthTimeMap.get(matchScoreAndTimeVo.getMatchTimeInfo().getMatchLength());
        Long startTimeSecond= minuts*60l;
        //加时赛倒计时
        if(nextPeriod.equals(40l)){
            startTimeSecond=5*60l;
        }
        //3.查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo= basketBallScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(),nextPeriod);
        //4.下发阶段事件
        commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,nextPeriod,startTimeSecond,startTimeSecond,System.currentTimeMillis(),matchScoreCommonVo,linkedId,"");
        String oddsDataSourceCode ="PD";
//        StandardSportMarketSellExample example= new StandardSportMarketSellExample();
//        example.createCriteria().andMatchInfoIdEqualTo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
//        List<StandardSportMarketSell> list= standardSportMarketSellMapper.selectByExample(example);
        StandardSportMarketSell standardSportMarketSell = pdMatchInfoRepository.getStandardSportMarketSell(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), null);

        if(standardSportMarketSell!=null){
//            StandardSportMarketSell standardSportMarketSell=list.get(0);
            oddsDataSourceCode=standardSportMarketSell.getMatchStatusSourceCode();
//            ThirdMatchInfoExample example2= new ThirdMatchInfoExample();
//            example2.createCriteria().andReferenceIdEqualTo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId()).andDataSourceCodeEqualTo(oddsDataSourceCode);
//            List<ThirdMatchInfo> thirdMatchInfo= thirdMatchInfoMapper.selectByExample(example2);
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), oddsDataSourceCode, null);
            if(thirdMatchInfo!=null){
                eventProducer.sendMatchStartStatus(thirdMatchInfo,linkedId);
            }
        }
        try {
            Thread.sleep(200);
            MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchTimeEvent(matchScoreAndTimeVo,startTimeSecond,startTimeSecond,System.currentTimeMillis(),1,nextPeriod,linkedId+"_PD");
            //3.发送MQ且记录事件
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        } catch (InterruptedException e) {

        }

        return Response.success();
    }

    @Override
    public Response matchPause(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId) {
        //1.计算时间 当前剩余秒 = 上次倒计时- (现在系统时间-上次事件时间)/1000
//        Long startTimeSecond =  matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart()-(System.currentTimeMillis()- matchScoreAndTimeVo.getMatchTimeInfo().getEventTime())/1000;
        Long startTimeSecond = getMatchTime(matchScoreAndTimeVo.getMatchTimeInfo());
        //2.下发时间暂停
        commonEventService.updateMatchTimeEvent(matchScoreAndTimeVo,matchScoreAndTimeVo.getMatchTimeInfo().getPeriod()
                ,startTimeSecond,startTimeSecond,System.currentTimeMillis(),0,linkedId);

        return Response.success();
    }

    public Response matchPauseBasketball(MatchScoreAndTimeVo matchScoreAndTimeVo, PDBasketBallPauseDto dto) {
        //2.下发时间暂停
        commonEventService.updateBasketballPauseAndContinue(matchScoreAndTimeVo,dto);

        return Response.success();
    }

    @Override
    public Response matchContinueBasketball(MatchScoreAndTimeVo matchScoreAndTimeVo,PDBasketBallPauseDto dto) {
        //1.下发时间继续
        commonEventService.updateBasketballPauseAndContinue(matchScoreAndTimeVo,dto);
        return Response.success();
    }

    @Override
    public Response matchContinue(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId) {
        //1.下发时间继续
        commonEventService.updateMatchTimeEvent(matchScoreAndTimeVo,matchScoreAndTimeVo.getMatchTimeInfo().getPeriod()
                ,matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart(),matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart(),System.currentTimeMillis(),1,linkedId);
        return Response.success();
    }

    @Override
    public Response matchEnd(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId) {
        //1.查询阶段
        Long nextPeriod= MatchPeriodUtils.BascketBallPeriod.getNextPeriod(matchScoreAndTimeVo.getMatchTimeInfo().getPeriod(),matchScoreAndTimeVo.getMatchTimeInfo().getMatchLength());
        //3.查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo= basketBallScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(),matchScoreAndTimeVo.getMatchScoresInfo().getPeriod());
        //4.下发阶段事件
        commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,nextPeriod,0L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId,"");
        return Response.success();
    }

    @Override
    public Response match999End(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusDto changeMatchStatus) {
        MatchScoreCommonVo matchScoreCommonVo= basketBallScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(),999L);
        String linkedId = changeMatchStatus.getLinkedId();
        String userName = changeMatchStatus.getOperatorName();
        //4.下发阶段事件
        if(basketBallScoreService.hasExtryPeriod(matchScoreAndTimeVo.getMatchScoresInfo())){
            commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,110l,0L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId+"_PD",userName);
//            scoresProducer.sendToMQ(matchScoreAndTimeVo.getThirdMatchInfo(),matchScoreAndTimeVo.getMatchScoresInfo(),linkedId);
        }else {
            commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,100l,0L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId+"_PD",userName);
//            scoresProducer.sendToMQ(matchScoreAndTimeVo.getThirdMatchInfo(),matchScoreAndTimeVo.getMatchScoresInfo(),linkedId);
        }
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {

        }
//        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
        if(standardMatchInfo!=null){
            StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
            newStandardMatchInfo.setId(standardMatchInfo.getId());
            newStandardMatchInfo.setMatchStatus(3);
            newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
            matchScoreAndTimeVo.getThirdMatchInfo().setMatchStatus(3);
            matchScoreAndTimeVo.getThirdMatchInfo().setModifyTime(System.currentTimeMillis());
//            thirdMatchInfoMapper.updateByPrimaryKey( matchScoreAndTimeVo.getThirdMatchInfo());
//            standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
            eventProducer.sendMatchStatusTopic(linkedId, matchScoreAndTimeVo.getThirdMatchInfo(), matchScoreAndTimeVo.getThirdMatchInfo().getMatchStatus());
            pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
        }
        commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,999l,0L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId,userName);
//        scoresProducer.sendToMQ(matchScoreAndTimeVo.getThirdMatchInfo(),matchScoreAndTimeVo.getMatchScoresInfo(),linkedId);

        return Response.success();
    }

    @Override
    public Response changeMatchScore(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {

        return changeNowPeriodMatchScore(data,changeMatchScoreDto);

    }

    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo data, Long periodId, String linkedId) {
        //3.查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo= basketBallScoreService.searchCommonMatchScore(data.getMatchScoresInfo(),periodId);
        if(periodId.equals(32l)){
            commonEventService.changeMatchPeriodEvent(data,100l,0L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId+"_PD","");
//            scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),linkedId);
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {

            }
        }
        //4.下发阶段事件
        commonEventService.changeMatchPeriodEvent(data,periodId,0L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId,"");
//        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),linkedId);
        return Response.success();
    }

    @Override
    public Response buildBasketBallAdvertiseVo(MatchScoreAndTimeVo data) {
//        ThirdMatchInfo thirdMatchInfo =data.getThirdMatchInfo();
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoRepository.selectThirdMatchInfoByPrimaryKey(data.getThirdMatchInfo().getId());
//        MatchTimeInfo matchTimeInfo =data.getMatchTimeInfo();
        MatchTimeInfo matchTimeInfo = matchTimeInfoRepository.selectByThirdMatchId(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
//        MatchScoresInfo matchScoresInfo =data.getMatchScoresInfo();
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(),SourceTypeEnum.LIVE_DATA.getCode());
        PDMatchAdvertiseVo pdMatchAdvertiseVo=new PDMatchAdvertiseVo();
        pdMatchAdvertiseVo.setThirdMatchId(String.valueOf(thirdMatchInfo.getId()));
        pdMatchAdvertiseVo.setStandardMatchId(thirdMatchInfo.getReferenceId());
        pdMatchAdvertiseVo.setEventTime(matchTimeInfo.getEventTime());
        pdMatchAdvertiseVo.setMatchBeginTime(thirdMatchInfo.getBeginTime());
        pdMatchAdvertiseVo.setIsGo(matchTimeInfo.getTimeGo()+"");
        //非开赛阶段 0
       if(!SportPeriodConstant.BasketballPeriod.contans(matchTimeInfo.getPeriod(),matchTimeInfo.getMatchLength())){
           pdMatchAdvertiseVo.setIsGo("0");
       }
        pdMatchAdvertiseVo.setPeriod(matchTimeInfo.getPeriod());
        pdMatchAdvertiseVo.setMatchLength(matchTimeInfo.getMatchLength());
        //时间计算
        //1.根据不同的赛制得到不同的倒计时
        Long startTimeSecond= MatchPeriodUtils.getMatchTime(matchTimeInfo);
        //1.计算时间 当前剩余秒 = 上次倒计时- (现在系统时间-上次事件时间)/1000
//         startTimeSecond= matchTimeInfo.getSecondFromStart()-(System.currentTimeMillis()-matchTimeInfo.getEventTime())/1000;
        //2.
        if(startTimeSecond<0l){
            startTimeSecond=0l;
        }
        pdMatchAdvertiseVo.setMatchTime(startTimeSecond);
        //比分计算
        BasketBallScoreVo basketBallScoreVo = this.transforScore(matchScoresInfo);
        pdMatchAdvertiseVo.setBasketBallScore(basketBallScoreVo);
        pdMatchAdvertiseVo.setRestTime(matchTimeInfo.getRestTime());
        //6分钟比分统计
        PDBasketBallAdvertiseVo pdBasketballAdvertiseVo =new PDBasketBallAdvertiseVo();
        this.buildPDSixMinutesScore(pdBasketballAdvertiseVo,matchScoresInfo);
        pdMatchAdvertiseVo.setSixMinuteScoresMap(pdBasketballAdvertiseVo.getSixMinuteScoresMap());
        return Response.success(pdMatchAdvertiseVo);
    }

    @Override
    public Response buildPDBasketBallAdvertiseVo(MatchScoreAndTimeVo data) {
        PDBasketBallAdvertiseVo pdMatchAdvertiseVo =new PDBasketBallAdvertiseVo();
        ThirdMatchInfo thirdMatchInfo =data.getThirdMatchInfo();
        MatchTimeInfo matchTimeInfo =data.getMatchTimeInfo();
        MatchScoresInfo matchScoresInfo =data.getMatchScoresInfo();
        pdMatchAdvertiseVo.setThirdMatchId(String.valueOf(thirdMatchInfo.getId()));
        pdMatchAdvertiseVo.setStandardMatchId(thirdMatchInfo.getReferenceId());
        pdMatchAdvertiseVo.setEventTime(matchTimeInfo.getEventTime());
        pdMatchAdvertiseVo.setMatchBeginTime(thirdMatchInfo.getBeginTime());
        pdMatchAdvertiseVo.setIsGo(matchTimeInfo.getTimeGo()+"");
        //非开赛阶段 0
        if(!SportPeriodConstant.BasketballPeriod.contans(matchTimeInfo.getPeriod(),matchTimeInfo.getMatchLength())){
            pdMatchAdvertiseVo.setIsGo("0");
        }
        pdMatchAdvertiseVo.setPeriod(matchScoresInfo.getPeriod());
        pdMatchAdvertiseVo.setMatchLength(matchTimeInfo.getMatchLength());
        //时间计算
        //1.根据不同的赛制得到不同的倒计时
        Long startTimeSecond= MatchPeriodUtils.getMatchTime(matchTimeInfo);
//        Long startTimeSecond= MatchPeriodUtils.getBreakAndRestartMatchTime(matchTimeInfo);
        //1.计算时间 当前剩余秒 = 上次倒计时- (现在系统时间-上次事件时间)/1000
//         startTimeSecond= matchTimeInfo.getSecondFromStart()-(System.currentTimeMillis()-matchTimeInfo.getEventTime())/1000;
        //2.
        if(startTimeSecond<0l){
            startTimeSecond=0l;
        }
        pdMatchAdvertiseVo.setMatchTime(startTimeSecond);
        pdMatchAdvertiseVo.setRestTime(matchTimeInfo.getRestTime());
        //比分计算
        BasketBallScoreVo basketBallScoreVo = this.transforScore(matchScoresInfo);
        pdMatchAdvertiseVo.setBasketBallScore(basketBallScoreVo);
        if (matchScoresInfo.getPeriod().equals(80L)) {
            Integer periodT1 = matchScoresInfo.getPeriodT1();
            Integer periodT2 = matchScoresInfo.getPeriodT2();
            CommonItem currentScore = new CommonItem();
            currentScore.setHome(periodT1);
            currentScore.setAway(periodT2);
            pdMatchAdvertiseVo.setInterruptPeriodScore(currentScore);
        }
        //比分统计
        this.buildPDAllScore(pdMatchAdvertiseVo,matchScoresInfo);
        // 每次加时历史统计及加时次数
        BasketballScoresExtra scoresExtra = JSON.parseObject(matchScoresInfo.getScoresJsonExtra(),new TypeReference<BasketballScoresExtra>(){});
        pdMatchAdvertiseVo.setBasketballScoresExtra(scoresExtra);
        //6分钟比分统计
        this.buildPDSixMinutesScore(pdMatchAdvertiseVo,matchScoresInfo);
        // 获取罚球状态
        String key = "PD_FREE_THROW:" + matchTimeInfo.getThirdMatchId();
        Object obj = redisService.get(key);
        FreeThrowDetailDto freeThrowDetailDto = JSONObject.parseObject(obj == null ? "" : obj.toString(), new TypeReference<FreeThrowDetailDto>() {
        });
        pdMatchAdvertiseVo.setFreeThrowDetailDto(freeThrowDetailDto);
        return Response.success(pdMatchAdvertiseVo);
    }

    @Override
    public Response buildPDAllScore(MatchScoreAndTimeVo data) {
        PDBasketBallAllScoreDto pdBasketBallAllScoreDto =new PDBasketBallAllScoreDto();
        List<PDBasketBallScoreDto> homeScoreList =new ArrayList<>();
        List<PDBasketBallScoreDto> awayScoreList =new ArrayList<>();
        pdBasketBallAllScoreDto.setHomeScoreList(homeScoreList);
        pdBasketBallAllScoreDto.setAwayScoreList(awayScoreList);
        //主队比分组装
        if(StringUtils.isNotEmpty(data.getMatchScoresInfo().getScoresJson()) ) {
            JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
            Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
            //主队比分
            PDBasketBallScoreDto allPointerH =new PDBasketBallScoreDto("allPointer");
            PDBasketBallScoreDto twoPointerH =new PDBasketBallScoreDto("twoPointer");
            PDBasketBallScoreDto threePointerH =new PDBasketBallScoreDto("threePointer");
            PDBasketBallScoreDto twoPointerMadeH =new PDBasketBallScoreDto("twoPointerMade");
            PDBasketBallScoreDto threePointerMadeH =new PDBasketBallScoreDto("threePointerMade");
            PDBasketBallScoreDto freeThrowMadeH =new PDBasketBallScoreDto("freeThrowMade");
            PDBasketBallScoreDto reboundAttackH =new PDBasketBallScoreDto("reboundAttack");
            PDBasketBallScoreDto reboundDefenseH =new PDBasketBallScoreDto("reboundDefense");
            PDBasketBallScoreDto foulH =new PDBasketBallScoreDto("foul");
            PDBasketBallScoreDto blockH =new PDBasketBallScoreDto("block");
            PDBasketBallScoreDto stealH =new PDBasketBallScoreDto("steal");
            PDBasketBallScoreDto turnoverH =new PDBasketBallScoreDto("turnover");
            PDBasketBallScoreDto assistH =new PDBasketBallScoreDto("assist");
            Map<String,Integer> allPointerHperiodScore =new HashMap<>();
            allPointerH.setPeriodScore(allPointerHperiodScore);
            Map<String,Integer> twoPointerHperiodScore =new HashMap<>();
            twoPointerH.setPeriodScore(twoPointerHperiodScore);
            Map<String,Integer> threePointerHperiodScore =new HashMap<>();
            threePointerH.setPeriodScore(threePointerHperiodScore);
            Map<String,Integer> twoPointerMadeHperiodScore =new HashMap<>();
            twoPointerMadeH.setPeriodScore(twoPointerMadeHperiodScore);
            Map<String,Integer> threePointerMadeHperiodScore =new HashMap<>();
            threePointerMadeH.setPeriodScore(threePointerMadeHperiodScore);
            Map<String,Integer> freeThrowMadeHperiodScore =new HashMap<>();
            freeThrowMadeH.setPeriodScore(freeThrowMadeHperiodScore);
            Map<String,Integer> reboundAttackHperiodScore =new HashMap<>();
            reboundAttackH.setPeriodScore(reboundAttackHperiodScore);
            Map<String,Integer> reboundDefenseHperiodScore =new HashMap<>();
            reboundDefenseH.setPeriodScore(reboundDefenseHperiodScore);
            Map<String,Integer> foulHperiodScore =new HashMap<>();
            foulH.setPeriodScore(foulHperiodScore);
            Map<String,Integer> blockHperiodScore =new HashMap<>();
            blockH.setPeriodScore(blockHperiodScore);
            Map<String,Integer> stealHperiodScore =new HashMap<>();
            stealH.setPeriodScore(stealHperiodScore);
            Map<String,Integer> turnoverHperiodScore =new HashMap<>();
            turnoverH.setPeriodScore(turnoverHperiodScore);
            Map<String,Integer> assistHperiodScore =new HashMap<>();
            assistH.setPeriodScore(assistHperiodScore);
            homeScoreList.add(allPointerH);
            homeScoreList.add(twoPointerH);
            homeScoreList.add(threePointerH);
            homeScoreList.add(twoPointerMadeH);
            homeScoreList.add(threePointerMadeH);
            homeScoreList.add(freeThrowMadeH);
            homeScoreList.add(reboundAttackH);
            homeScoreList.add(reboundDefenseH);
            homeScoreList.add(foulH);
            homeScoreList.add(blockH);
            homeScoreList.add(stealH);
            homeScoreList.add(turnoverH);
            homeScoreList.add(assistH);

            //客队
            PDBasketBallScoreDto allPointerA =new PDBasketBallScoreDto("allPointer");
            PDBasketBallScoreDto twoPointerA =new PDBasketBallScoreDto("twoPointer");
            PDBasketBallScoreDto threePointerA =new PDBasketBallScoreDto("threePointer");
            PDBasketBallScoreDto twoPointerMadeA =new PDBasketBallScoreDto("twoPointerMade");
            PDBasketBallScoreDto threePointerMadeA =new PDBasketBallScoreDto("threePointerMade");
            PDBasketBallScoreDto freeThrowMadeA =new PDBasketBallScoreDto("freeThrowMade");
            PDBasketBallScoreDto reboundAttackA =new PDBasketBallScoreDto("reboundAttack");
            PDBasketBallScoreDto reboundDefenseA =new PDBasketBallScoreDto("reboundDefense");
            PDBasketBallScoreDto foulA =new PDBasketBallScoreDto("foul");
            PDBasketBallScoreDto blockA =new PDBasketBallScoreDto("block");
            PDBasketBallScoreDto stealA =new PDBasketBallScoreDto("steal");
            PDBasketBallScoreDto turnoverA =new PDBasketBallScoreDto("turnover");
            PDBasketBallScoreDto assistA =new PDBasketBallScoreDto("assist");
            Map<String,Integer> allPointerAperiodScore =new HashMap<>();
            allPointerA.setPeriodScore(allPointerAperiodScore);
            Map<String,Integer> twoPointerAperiodScore =new HashMap<>();
            twoPointerA.setPeriodScore(twoPointerAperiodScore);
            Map<String,Integer> threePointerAperiodScore =new HashMap<>();
            threePointerA.setPeriodScore(threePointerAperiodScore);
            Map<String,Integer> twoPointerMadeAperiodScore =new HashMap<>();
            twoPointerMadeA.setPeriodScore(twoPointerMadeAperiodScore);
            Map<String,Integer> threePointerMadeAperiodScore =new HashMap<>();
            threePointerMadeA.setPeriodScore(threePointerMadeAperiodScore);
            Map<String,Integer> freeThrowMadeAperiodScore =new HashMap<>();
            freeThrowMadeA.setPeriodScore(freeThrowMadeAperiodScore);
            Map<String,Integer> reboundAttackAperiodScore =new HashMap<>();
            reboundAttackA.setPeriodScore(reboundAttackAperiodScore);
            Map<String,Integer> reboundDefenseAperiodScore =new HashMap<>();
            reboundDefenseA.setPeriodScore(reboundDefenseAperiodScore);
            Map<String,Integer> foulAperiodScore =new HashMap<>();
            foulA.setPeriodScore(foulAperiodScore);
            Map<String,Integer> blockAperiodScore =new HashMap<>();
            blockA.setPeriodScore(blockAperiodScore);
            Map<String,Integer> stealAperiodScore =new HashMap<>();
            stealA.setPeriodScore(stealAperiodScore);
            Map<String,Integer> turnoverAperiodScore =new HashMap<>();
            turnoverA.setPeriodScore(turnoverAperiodScore);
            Map<String,Integer> assistAperiodScore =new HashMap<>();
            assistA.setPeriodScore(assistAperiodScore);
            awayScoreList.add(allPointerA);
            awayScoreList.add(twoPointerA);
            awayScoreList.add(threePointerA);
            awayScoreList.add(twoPointerMadeA);
            awayScoreList.add(threePointerMadeA);
            awayScoreList.add(freeThrowMadeA);
            awayScoreList.add(reboundAttackA);
            awayScoreList.add(reboundDefenseA);
            awayScoreList.add(foulA);
            awayScoreList.add(blockA);
            awayScoreList.add(stealA);
            awayScoreList.add(turnoverA);
            awayScoreList.add(assistA);

            for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
                if(entry.getKey()<1000L&&entry.getValue()!=null){
                    if(entry.getValue().getTwoPointer()!=null&&entry.getValue().getThreePointer()!=null&&entry.getValue().getFreeThrowMade()!=null){
                        allPointerHperiodScore.put(entry.getKey().toString(),(entry.getValue().getTwoPointerMade().getHome()+entry.getValue().getThreePointerMade().getHome()+entry.getValue().getFreeThrowMade().getHome()));
                        allPointerAperiodScore.put(entry.getKey().toString(),(entry.getValue().getTwoPointerMade().getAway()+entry.getValue().getThreePointerMade().getAway()+entry.getValue().getFreeThrowMade().getAway()));
                    }else {
                        allPointerHperiodScore.put(entry.getKey().toString(),0);
                        allPointerAperiodScore.put(entry.getKey().toString(),0);
                    }
                    twoPointerHperiodScore.put(entry.getKey().toString(),(entry.getValue().getTwoPointer()==null?0:entry.getValue().getTwoPointer().getHome()));
                    twoPointerMadeHperiodScore.put(entry.getKey().toString(),(entry.getValue().getTwoPointerMade()==null?0:entry.getValue().getTwoPointerMade().getHome()));
                    threePointerHperiodScore.put(entry.getKey().toString(),(entry.getValue().getThreePointer()==null?0:entry.getValue().getThreePointer().getHome()));
                    threePointerMadeHperiodScore.put(entry.getKey().toString(),(entry.getValue().getThreePointerMade()==null?0:entry.getValue().getThreePointerMade().getHome()));
                    freeThrowMadeHperiodScore.put(entry.getKey().toString(),(entry.getValue().getFreeThrowMade()==null?0:entry.getValue().getFreeThrowMade().getHome()));
                    reboundAttackHperiodScore.put(entry.getKey().toString(),(entry.getValue().getReboundAttack()==null?0:entry.getValue().getReboundAttack().getHome()));
                    reboundDefenseHperiodScore.put(entry.getKey().toString(),(entry.getValue().getReboundDefense()==null?0:entry.getValue().getReboundDefense().getHome()));
                    foulHperiodScore.put(entry.getKey().toString(),(entry.getValue().getFoul()==null?0:entry.getValue().getFoul().getHome()));
                    blockHperiodScore.put(entry.getKey().toString(),(entry.getValue().getBlock()==null?0:entry.getValue().getBlock().getHome()));
                    stealHperiodScore.put(entry.getKey().toString(),(entry.getValue().getSteal()==null?0:entry.getValue().getSteal().getHome()));
                    turnoverHperiodScore.put(entry.getKey().toString(),(entry.getValue().getTurnover()==null?0:entry.getValue().getTurnover().getHome()));
                    assistHperiodScore.put(entry.getKey().toString(),(entry.getValue().getAssist()==null?0:entry.getValue().getAssist().getHome()));


                    twoPointerAperiodScore.put(entry.getKey().toString(),(entry.getValue().getTwoPointer()==null?0:entry.getValue().getTwoPointer().getAway()));
                    twoPointerMadeAperiodScore.put(entry.getKey().toString(),(entry.getValue().getTwoPointerMade()==null?0:entry.getValue().getTwoPointerMade().getAway()));
                    threePointerAperiodScore.put(entry.getKey().toString(),(entry.getValue().getThreePointer()==null?0:entry.getValue().getThreePointer().getAway()));
                    threePointerMadeAperiodScore.put(entry.getKey().toString(),(entry.getValue().getThreePointerMade()==null?0:entry.getValue().getThreePointerMade().getAway()));
                    freeThrowMadeAperiodScore.put(entry.getKey().toString(),(entry.getValue().getFreeThrowMade()==null?0:entry.getValue().getFreeThrowMade().getAway()));
                    reboundAttackAperiodScore.put(entry.getKey().toString(),(entry.getValue().getReboundAttack()==null?0:entry.getValue().getReboundAttack().getAway()));
                    reboundDefenseAperiodScore.put(entry.getKey().toString(),(entry.getValue().getReboundDefense()==null?0:entry.getValue().getReboundDefense().getAway()));
                    foulAperiodScore.put(entry.getKey().toString(),(entry.getValue().getFoul()==null?0:entry.getValue().getFoul().getAway()));
                    blockAperiodScore.put(entry.getKey().toString(),(entry.getValue().getBlock()==null?0:entry.getValue().getBlock().getAway()));
                    stealAperiodScore.put(entry.getKey().toString(),(entry.getValue().getSteal()==null?0:entry.getValue().getSteal().getAway()));
                    turnoverAperiodScore.put(entry.getKey().toString(),(entry.getValue().getTurnover()==null?0:entry.getValue().getTurnover().getAway()));
                    assistAperiodScore.put(entry.getKey().toString(),(entry.getValue().getAssist()==null?0:entry.getValue().getAssist().getAway()));

                }
            }
        }


        return Response.success(pdBasketBallAllScoreDto);
    }

    /**
     * 6分钟比分构建
     * */
    private void buildPDSixMinutesScore(PDBasketBallAdvertiseVo pdMatchAdvertiseVo, MatchScoresInfo matchScoresInfo) {
        Map<String,CommonItem> map =new HashMap<>();
        if(StringUtils.isNotEmpty(matchScoresInfo.getScoresJson()) ) {
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);

            for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
                if(entry.getKey()>1000L){
                    CommonItem score = entry.getValue().getMatchScore();
                    if(score==null){

                    }
                    map.put(entry.getKey().toString(),score);
                }
            }
        }
        pdMatchAdvertiseVo.setSixMinuteScoresMap(map);
    }
    /**
     * 比分统计构建
     * */
    private void buildPDAllScore(PDBasketBallAdvertiseVo pdMatchAdvertiseVo, MatchScoresInfo matchScoresInfo) {
        Map<String, BasketballScoresPDDto> map =new HashMap<>();
        if(StringUtils.isNotEmpty(matchScoresInfo.getScoresJson()) ) {
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
            for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
                if(entry.getKey()<1000L){
                    BasketballScoresPDDto dto = new BasketballScoresPDDto();
                    BasketballScores score = entry.getValue();
                    BeanUtils.copyProperties(score,dto);
                    dto.buildAllPointer();
                    map.put(entry.getKey().toString(),dto);
                }
            }
        }
        pdMatchAdvertiseVo.setAllScoreMap(map);
    }

    private BasketBallScoreVo transforScore(MatchScoresInfo matchScoresInfo) {
        BasketBallScoreVo basketBallScoreVo = new BasketBallScoreVo();
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson()) ) {
            Map<Long, BasketballScores> periodFootballScores = new HashMap<>();
            BasketballScores basketballScores = new BasketballScores(0l);
            periodFootballScores.put(WHOLE_MATCH, basketballScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            matchScoresInfo.setT1(basketballScores.getMatchScore().getHome());
            matchScoresInfo.setT2(basketballScores.getMatchScore().getAway());
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        //whole
        if(wholeSores!=null){
            basketBallScoreVo.setWhole(wholeSores.getMatchScore());
        }
        //                return new Long[]{1L, 2L,40L};
        //                return  new Long[]{1L, 2L,13L,14L, 15L,16L,40L};
        Long[] wholeperiod = SportPeriodConstant.BasketballPeriod.getWholePeriodsByMatchLength(matchScoresInfo.getMatchLength());
        if( matchScoresInfo.getMatchLength() ==17){
            BasketballScores HT= allPeriodScores.get(wholeperiod[0]);
            BasketballScores HT2= allPeriodScores.get(wholeperiod[1]);
            BasketballScores ET= allPeriodScores.get(wholeperiod[2]);
            if(HT!=null)
            basketBallScoreVo.setHT(HT.getMatchScore());
            if(HT2!=null)
            basketBallScoreVo.setHT2(HT2.getMatchScore());
            if(ET!=null)
            basketBallScoreVo.setET(ET.getMatchScore());
        }else if( matchScoresInfo.getMatchLength() ==73){
            BasketballScores Q1= allPeriodScores.get(21L);
            BasketballScores ET= allPeriodScores.get(40L);
            if(Q1!=null){
                basketBallScoreVo.setQ1(Q1.getMatchScore());
            }
            if(ET!=null){
                basketBallScoreVo.setET(ET.getMatchScore());
            }
        }else {
            BasketballScores Q1= allPeriodScores.get(wholeperiod[2]);
            BasketballScores Q2= allPeriodScores.get(wholeperiod[3]);
            BasketballScores Q3= allPeriodScores.get(wholeperiod[4]);
            BasketballScores Q4= allPeriodScores.get(wholeperiod[5]);
            BasketballScores ET= allPeriodScores.get(wholeperiod[6]);
            if(Q1!=null)
                basketBallScoreVo.setQ1(Q1.getMatchScore());
            if(Q2!=null)
                basketBallScoreVo.setQ2(Q2.getMatchScore());
            if(Q3!=null)
                basketBallScoreVo.setQ3(Q3.getMatchScore());
            if(Q4!=null)
                basketBallScoreVo.setQ4(Q4.getMatchScore());
            if(ET!=null)
                basketBallScoreVo.setET(ET.getMatchScore());
            if(Q1!=null||Q2!=null){
                CommonItem HT =new CommonItem();
                if(Q1!=null){
                    HT.setHome(Q1.getMatchScore().getHome());
                    HT.setAway(Q1.getMatchScore().getAway());
                }
                if(Q2!=null){
                    HT.setHome(Q2.getMatchScore().getHome()+HT.getHome());
                    HT.setAway(Q2.getMatchScore().getAway()+HT.getAway());
                }
                basketBallScoreVo.setHT(HT);
            }
            if(Q3!=null||Q4!=null){
                CommonItem HT2 =new CommonItem();
                if(Q3!=null){
                    HT2.setHome(Q3.getMatchScore().getHome());
                    HT2.setAway(Q3.getMatchScore().getAway());
                }
                if(Q4!=null){
                    HT2.setHome(Q4.getMatchScore().getHome()+HT2.getHome());
                    HT2.setAway(Q4.getMatchScore().getAway()+HT2.getAway());
                }
                basketBallScoreVo.setHT2(HT2);
            }
        }
        return basketBallScoreVo;
    }

    private Response changeNowPeriodMatchScore(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
        //1.计算总比分大小 是否是生成 比分修正事件
        boolean isDeleteEvent = basketBallScoreService.checkScoreChangeDelete(data.getMatchScoresInfo(),changeMatchScoreDto);
        //1.1. 计算下发的事件时间 当前倒计时
        Long startTimeSecond = changeMatchScoreDto.getMatchTime();
        //1.2. 计算总比分  阶段比分
        MatchScoreCommonVo matchScoreCommonVo =basketBallScoreService.countScore(data,changeMatchScoreDto);
        data.setMatchScoresInfo(matchScoreCommonVo.getMatchScoresInfo());
            if(isDeleteEvent){
                //2. 是比分修正事件 不下发 跨阶段修正事件
                int status = basketEventService.addScoreChangeEvent(data,matchScoreCommonVo,changeMatchScoreDto);
                if (status == PDScoreChangeEnum.NUMBER_LESS_ZERO.getCode()) {
                    return Response.failed("当前比分不能小于0");
                }
                //3.1修正事件要冻结结算
                eventProducer.sendFreezeSettle(changeMatchScoreDto,data.getThirdMatchInfo().getReferenceId(),data.getThirdMatchInfo().getSportId());
            }else {
                //3. 不是比分修正事件
                eventProducer.sendFreezeSettle(changeMatchScoreDto,data.getThirdMatchInfo().getReferenceId(),data.getThirdMatchInfo().getSportId());
                int status = basketEventService.addScoreChangeEvent(data,matchScoreCommonVo,changeMatchScoreDto);
                if (status == PDScoreChangeEnum.NUMBER_LESS_ZERO.getCode()) {
                    return Response.failed("当前比分不能小于0");
                }
            }

        //7. 下发比分变更事件  或者比分修正事件
//        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),changeMatchScoreDto.getLinkedId());
        return Response.success();
    }

    /**
     * 获取当前赛事时间
     *
     * @param matchTimeInfo 赛事时间数据
     * @return 当前赛事时间
     */
    private Long getMatchTime(MatchTimeInfo matchTimeInfo) {
        Long startTimeSecond = matchTimeInfo.getSecondFromStart() - (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000;
        // 暂停时赛事时间取字段时间
        if (matchTimeInfo.getTimeGo() == 0) {
            startTimeSecond = matchTimeInfo.getSecondFromStart();
        }
        if (startTimeSecond < 0) {
            startTimeSecond = 0L;
        }
        return startTimeSecond;
    }

//    private Response changePastPeriodMatchScore(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
//        //1.1. 计算下发的事件时间 当前倒计时
//        Long startTimeSecond = changeMatchScoreDto.getMatchTime();
//        //1.2. 计算总比分  阶段比分
//        MatchScoreCommonVo matchScoreCommonVo =basketBallScoreService.countScore(data,changeMatchScoreDto);
//        basketEventService.addScoreCorrectEvent(data,matchScoreCommonVo,startTimeSecond,changeMatchScoreDto.getPeriod(),changeMatchScoreDto.getLinkedId(),changeMatchScoreDto.getOperatorName());
//        //3.1修正事件要冻结结算
//        eventProducer.sendFreezeSettle(changeMatchScoreDto,data.getThirdMatchInfo().getReferenceId(),data.getThirdMatchInfo().getSportId());
//        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),changeMatchScoreDto.getLinkedId());
//        return Response.success();
//    }

}
