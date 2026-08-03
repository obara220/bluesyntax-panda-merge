package com.panda.merge.advertise.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SnookerConstant;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.KickOffDto;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IScoresService;
import com.panda.merge.snooker.dto.SnookerV2Scores;
import com.panda.merge.snooker.service.helper.MatchScoreCommonHelper;
import com.panda.merge.volleyball.dto.VolleyballV2Scores;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Service
@Slf4j
public class CommonAdvertiseServiceImpl implements CommonAdvertiseService {

    @Autowired
    RedisService redisService;
    @Autowired
    IScoresService scoresService;
    @Autowired
    ThirdSportTeamMapper thirdSportTeamMapper;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    ThirdMatchTeamRelationMapper thirdMatchTeamRelationMapper;
    @Autowired
    MatchTimeInfoRepository timeInfoRepository;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;

    @Autowired
    private MatchScoreCommonHelper matchScoreCommonHelper;
    @Override
    public Response<MatchScoreAndTimeVo> checkMatchScoreAndTimeCreate( Long thirdMatchId)
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 1.查询三方赛事-时间
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(thirdMatchId, null);
        if(thirdMatchInfo==null)
        {
            return Response.failed("PA 赛事不存在");
        }

        MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(thirdMatchId);
        if(matchScoresInfo==null)
        {
            return Response.failed("PA赛事比分不存在");
        }

        // 2.比分校验 生产出现 scoresJson为空的情况,目前并不知晓为啥
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            if(thirdMatchInfo.getSportId().equals(2L)){
                Map<Long, BasketballScores> periodFootballScores= new HashMap<>();
                BasketballScores basketballScores=new BasketballScores(0L);
                periodFootballScores.put(WHOLE_MATCH,basketballScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (thirdMatchInfo.getSportId().equals(1L)){
                Map<Long, FootballScores> periodFootballScores= new HashMap<>();
                FootballScores basketballScores=new FootballScores(0L);
                periodFootballScores.put(WHOLE_MATCH,basketballScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (thirdMatchInfo.getSportId().equals(5L)) {
                Map<Long, TennisScores> periodFootballScores= new HashMap<>();
                TennisScores tennisScores = new TennisScores(0L);
                periodFootballScores.put(WHOLE_MATCH, tennisScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            }else if(thirdMatchInfo.getSportId().equals(SportTypeEnum.ICE_HOCKEY.getValue())){
               //冰球
                Map<Long, IceHockeyScores> periodIceHockeyScores= new HashMap<>();
                IceHockeyScores iceHockeyScores = new IceHockeyScores(0L);
                periodIceHockeyScores.put(WHOLE_MATCH, iceHockeyScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodIceHockeyScores));
            } else if (SportTypeEnum.SNOOKER.getValue().equals(thirdMatchInfo.getSportId())) {
                Map<Long, SnookerV2Scores> snookerPeriodScores = new HashMap<>();
                snookerPeriodScores.put(WHOLE_MATCH, new SnookerV2Scores());
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(snookerPeriodScores));
            } else if (thirdMatchInfo.getSportId().equals(9L)) {
                Map<Long, VolleyballV2Scores> volleyballPeriodScores = new HashMap<>();
                volleyballPeriodScores.put(WHOLE_MATCH, new VolleyballV2Scores());
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(volleyballPeriodScores));
            }
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        }
        MatchTimeInfo matchTimeInfo = timeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
        if (ObjectUtils.isEmpty(matchTimeInfo))
        {
            matchTimeInfo = createMatchTimeInfo(matchScoresInfo);
//            return Response.failed("PA赛事时间不存在.");
        }
        MatchTimeInfo timeInfo = matchTimeInfo;
        log.info("CommonAdvertiseServiceImpl-checkMatchScoreAndTimeCreate-thirdMatchId={},time={}", thirdMatchId, timeInfo);
        MatchScoreAndTimeVo matchScoreAndTimeVo =new MatchScoreAndTimeVo();
        matchScoreAndTimeVo.setMatchScoresInfo(matchScoresInfo);
        matchScoreAndTimeVo.setThirdMatchInfo(thirdMatchInfo);
        if (null == timeInfo.getSecondFromStart())
        {
            timeInfo.setSecondFromStart(0L);
        }
        if( thirdMatchInfo.getReferenceId()!=null && thirdMatchInfo.getReferenceId()!=0L )
        {
            StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
            matchScoreAndTimeVo.setStandardMatchInfo(standardMatchInfo);
        }
        log.info("{} 获取PD赛事阶段:{},{},{}",timeInfo.getThirdMatchId(),timeInfo.getPeriod(),matchScoresInfo.getPeriod(),!Objects.equals(timeInfo.getPeriod(), matchScoresInfo.getPeriod()));
        // period 被污染为 null（如 updateMatchStatus 传 null matchPeriodId）后，两者都为 null 永远相等，
        // 同步分支不会触发；此处走 matchPeriodValid 从 DB 查最大 period 回填。
        // 若 matchPeriodValid 后仍为 null（DB 中所有行 period 均为 null），兜底置为 0L。
        if (timeInfo.getPeriod() == null || matchScoresInfo.getPeriod() == null) {
            String linkId = "check_" + thirdMatchId;
            matchPeriodValid(linkId, thirdMatchInfo, matchScoresInfo, timeInfo);
        }
        if (timeInfo.getPeriod() == null && matchScoresInfo.getPeriod() == null) {
            timeInfo.setPeriod(0L);
            matchScoresInfo.setPeriod(0L);
            timeInfoRepository.updateByPrimaryKey(timeInfo);
            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        } else if (!Objects.equals(timeInfo.getPeriod(), matchScoresInfo.getPeriod())
                && matchScoresInfo.getPeriod() != null)
        {
            timeInfo.setPeriod(matchScoresInfo.getPeriod());
            timeInfoRepository.updateByPrimaryKey(timeInfo);
        }
        matchScoreAndTimeVo.setMatchTimeInfo(timeInfo);
        stopWatch.stop();
        log.info("CommonAdvertiseServiceImpl-checkMatchScoreAndTimeCreate-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
        return Response.success(matchScoreAndTimeVo);
    }

    /**
     * 更新赛事时间
     * @param matchScoresInfo
     */
    private MatchTimeInfo createMatchTimeInfo(MatchScoresInfo matchScoresInfo) {
        if(matchScoresInfo==null){
            return null;
        }
        MatchTimeInfo matchTimeInfo =new MatchTimeInfo();
        matchTimeInfo.setCreateTime(System.currentTimeMillis());
        matchTimeInfo.setModifyTime(System.currentTimeMillis());
        matchTimeInfo.setDataSourceType(matchScoresInfo.getDataSourceType());
        matchTimeInfo.setTimeGo(0);
        matchTimeInfo.setThirdMatchId(matchScoresInfo.getThirdMatchId());
        matchTimeInfo.setPeriod(matchScoresInfo.getPeriod());
        matchTimeInfo.setSecondFromStart(matchScoresInfo.getSecondsMatchStart());
        matchTimeInfo.setId(matchScoresInfo.getId());
        matchTimeInfo.setMatchLength(matchScoresInfo.getMatchLength());
        matchTimeInfo.setRemainingTime(0L);
        matchTimeInfo.setEventTime(System.currentTimeMillis());
        timeInfoRepository.updateByPrimaryKey(matchTimeInfo);
        return matchTimeInfo;


    }
    @Override
    public void matchPeriodValid( String linkId, ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchTimeInfo timeInfo) {
        Long scoresInfoPeriod = matchScoresInfo.getPeriod();
        Long timeInfoPeriod = timeInfo.getPeriod();
        Boolean reSetScore = null == scoresInfoPeriod || "0".equals(scoresInfoPeriod.toString());
        Boolean resetTime =  null == timeInfoPeriod || "0".equals(timeInfoPeriod.toString());
        Boolean resetStatus = reSetScore || resetTime;
        log.info("::{}::matchPeriodValid; scoresInfoPeriod:{}, timeInfoPeriod:{}, 汇总状态:{}, reSetScore:{}",
                linkId, scoresInfoPeriod, timeInfoPeriod, resetStatus, reSetScore);


        MatchScoresInfoExample scoreExample = new MatchScoresInfoExample();
        scoreExample.createCriteria()
                .andThirdMatchIdEqualTo(thirdMatchInfo.getId())
                .andDataSourceTypeEqualTo(SourceTypeEnum.LIVE_DATA.getCode().toString());
        List<MatchScoresInfo> scoresInfoList = matchScoresInfoMapper.selectByExample(scoreExample);

        List<MatchScoresInfo> pdScoreList = Lists.newArrayList();
        MatchScoresInfo effectiveScoresInfo = matchScoresInfo;
        if ( reSetScore ) {
            MatchScoresInfo dbInfo = matchScoreInfoRepository.queryMatchScoreFromDB(matchScoresInfo.getId());
            if ( Objects.isNull(dbInfo) ) {
                log.info("::{}::matchPeriodValid查询的比分数据为空", linkId);
            } else {
                // 刷新缓存
                matchScoreInfoRepository.updateScoresInfoCache(dbInfo);
                effectiveScoresInfo = dbInfo;
                matchScoresInfo.setPeriod(dbInfo.getPeriod());
            }
        }
        pdScoreList.add(effectiveScoresInfo);

        if ( CollectionUtil.isNotEmpty(scoresInfoList) ) {
            pdScoreList.addAll(scoresInfoList);
        }

        pdScoreList = pdScoreList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection( () -> new TreeSet<>(Comparator.comparing(MatchScoresInfo::getId))), ArrayList::new));

        List<Long> ids = pdScoreList.stream().map(MatchScoresInfo::getId).collect(Collectors.toList());
        log.info("::{}::matchPeriodValid比分id:{}", linkId, ids);

        Optional<MatchScoresInfo> maxScore = pdScoreList.stream()
                .max(Comparator.comparing(MatchScoresInfo::getPeriod, Comparator.nullsFirst(Comparator.naturalOrder())));
        Long maxPeriod = scoresInfoPeriod;
        if ( maxScore.isPresent() ) {
            maxPeriod = maxScore.get().getPeriod();
        }

        for ( MatchScoresInfo scoresInfo : pdScoreList ) {
            if ( null == scoresInfo.getPeriod() || scoresInfo.getPeriod() < maxPeriod ) {
                scoresInfo.setPeriod(maxPeriod);
                matchScoreInfoRepository.updateScoresInfo(scoresInfo);
            }
        }
        matchScoresInfo.setPeriod(effectiveScoresInfo.getPeriod());
        if ( maxPeriod != null && (null == timeInfo.getPeriod() || timeInfo.getPeriod() < maxPeriod) ) {
            timeInfo.setPeriod(maxPeriod);
            timeInfoRepository.updateByPrimaryKey(timeInfo);
        }

        // 数据库阶段的校验与同步
        MatchTimeInfoExample timeExample = new MatchTimeInfoExample();
        timeExample.createCriteria().andIdIn(ids);
        List<MatchTimeInfo> matchTimeInfos = matchTimeInfoMapper.selectByExample(timeExample);
        if ( CollectionUtil.isNotEmpty(matchTimeInfos) && maxPeriod != null ) {
            for ( MatchTimeInfo matchTimeInfo : matchTimeInfos) {
                if ( null == matchTimeInfo.getPeriod() || matchTimeInfo.getPeriod() < maxPeriod ) {
                    matchTimeInfo.setPeriod(maxPeriod);
                    log.info("::{}::matchPeriodValid阶段表数据的同步:{}", linkId, matchTimeInfo.getId());
                    timeInfoRepository.updateByPrimaryKey(matchTimeInfo);
                }
            }
        }

    }



    @Override
    public Response<MatchScoreAndTimeVo> checkMatchScoreAndTimeCreateApi(Long thirdMatchId){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //1.查询三方赛事-时间
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(thirdMatchId, null);
        if(thirdMatchInfo==null){
            return Response.failed("PA 赛事不存在");
        }
        MatchScoresInfo matchScoresInfo = scoresService.selectLiveMatchScoreInfo(thirdMatchId);
        if(matchScoresInfo==null){
            return Response.failed("PA赛事比分不存在");
        }
        //比分校验 生产出现 scoresJson为空的情况,目前并不知晓为啥
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            if(thirdMatchInfo.getSportId().equals(2l)){
                Map<Long, BasketballScores> periodFootballScores= new HashMap<>();
                BasketballScores basketballScores=new BasketballScores(0l);
                periodFootballScores.put(WHOLE_MATCH,basketballScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (thirdMatchInfo.getSportId().equals(1l)){
                Map<Long, FootballScores> periodFootballScores= new HashMap<>();
                FootballScores basketballScores=new FootballScores(0l);
                periodFootballScores.put(WHOLE_MATCH,basketballScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (thirdMatchInfo.getSportId().equals(5l)) {
                Map<Long, TennisScores> periodFootballScores= new HashMap<>();
                TennisScores tennisScores = new TennisScores(0l);
                periodFootballScores.put(WHOLE_MATCH, tennisScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (SportTypeEnum.SNOOKER.getValue().equals(thirdMatchInfo.getSportId())) {
                Map<Long, SnookerV2Scores> snookerPeriodScores = new HashMap<>();
                snookerPeriodScores.put(WHOLE_MATCH, new SnookerV2Scores());
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(snookerPeriodScores));
            } else if (thirdMatchInfo.getSportId().equals(9L)) {
                Map<Long, VolleyballV2Scores> volleyballPeriodScores = new HashMap<>();
                volleyballPeriodScores.put(WHOLE_MATCH, new VolleyballV2Scores());
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(volleyballPeriodScores));
            }
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        }
        MatchTimeInfo timeInfo = timeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
        if (timeInfo == null) {
//            MatchTimeInfoExample example = new MatchTimeInfoExample();
//            example.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andDataSourceTypeEqualTo("1");
//            List<MatchTimeInfo> timeInfoList = timeInfoMapper.selectByExample(example);
            MatchTimeInfo matchTimeInfo = timeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
            if (ObjectUtils.isEmpty(matchTimeInfo)) {
                return Response.failed("PA赛事时间不存在!");
            }
            timeInfo = matchTimeInfo;
        }
        log.info("CommonAdvertiseServiceImpl-checkMatchScoreAndTimeCreate-thirdMatchId={},time={}",thirdMatchId,timeInfo);
        MatchScoreAndTimeVo matchScoreAndTimeVo =new MatchScoreAndTimeVo();
        matchScoreAndTimeVo.setMatchScoresInfo(matchScoresInfo);
        matchScoreAndTimeVo.setThirdMatchInfo(thirdMatchInfo);
        if (null == timeInfo.getSecondFromStart()) {
            timeInfo.setSecondFromStart(0L);
        }
        if(thirdMatchInfo.getReferenceId()!=null&&thirdMatchInfo.getReferenceId()!=0l){
//            StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
            matchScoreAndTimeVo.setStandardMatchInfo(standardMatchInfo);
            if (null == timeInfo.getSecondFromStart() && standardMatchInfo != null && standardMatchInfo.getSecondsMatchStart() != null) {
                timeInfo.setSecondFromStart(standardMatchInfo.getSecondsMatchStart().longValue());
            }
        }
        matchScoreAndTimeVo.setMatchTimeInfo(timeInfo);
        stopWatch.stop();
        log.info("CommonAdvertiseServiceImpl-checkMatchScoreAndTimeCreate-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
        return Response.success(matchScoreAndTimeVo);
    }
    @Override
    public Response changeMatchStartStatus(ThirdMatchInfo thirdMatchInfo, String linkId) {


            //3.修改开赛时间
//            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
            if(standardMatchInfo==null){
                return Response.failed("PA赛事的标准赛事不存在");
            }
            thirdMatchInfo.setMatchStatus(1);
            thirdMatchInfo.setModifyTime(System.currentTimeMillis());
        StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
        newStandardMatchInfo.setId(standardMatchInfo.getId());
        newStandardMatchInfo.setMatchStatus(1);
        newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
//            thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
//            standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
        eventProducer.sendMatchStatusTopic(linkId, thirdMatchInfo, thirdMatchInfo.getMatchStatus());
        pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);

            if (standardMatchInfo == null) {
                return Response.failed("PA赛事的标准赛事不存在");

            }
        return Response.success();
    }

    @Override
    public Response changeFootballMatchStartStatus(ThirdMatchInfo thirdMatchInfo, KickOffDto kickOff) {
        //3.修改开赛时间
//        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
        StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
        if (standardMatchInfo == null) {
            return Response.failed("PA赛事的标准赛事不存在");
        }
        StandardSportMarketSellExample marketExample = new StandardSportMarketSellExample();
        marketExample.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
//        List<StandardSportMarketSell> marketSells = standardSportMarketSellMapper.selectByExample(marketExample);
//        if (!CollectionUtils.isEmpty(marketSells)) {
//            String dataSourceCode = "";
//            if (null != kickOff && !ObjectUtils.isEmpty(kickOff.getDataSourceCode())) {
//                dataSourceCode = kickOff.getDataSourceCode();
//            }
//            if (dataSourceCode.equals(marketSells.get(0).getBusinessEvent())) {
//                standardMatchInfo.setMatchStatus(1);
//                standardMatchInfo.setModifyTime(System.currentTimeMillis());
//                standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
//            }
//        }
        StandardSportMarketSell marketSell = pdMatchInfoRepository.getStandardSportMarketSell(standardMatchInfo.getId(), null);
        if (!ObjectUtils.isEmpty(marketSell)) {
            String dataSourceCode = "";
            if (null != kickOff && !ObjectUtils.isEmpty(kickOff.getDataSourceCode())) {
                dataSourceCode = kickOff.getDataSourceCode();
            }
            if (dataSourceCode.equals(marketSell.getBusinessEvent())) {
                StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                newStandardMatchInfo.setId(standardMatchInfo.getId());
                newStandardMatchInfo.setMatchStatus(1);
                newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
//                standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
                pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
            }
        }
        thirdMatchInfo.setMatchStatus(1);
        thirdMatchInfo.setModifyTime(System.currentTimeMillis());
//        thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
        eventProducer.sendMatchStatusTopic(kickOff.getLinkedId(), thirdMatchInfo, thirdMatchInfo.getMatchStatus());

        return Response.success();
    }

    @Override
    public void updateMatchStatus(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        //阶段更新
        data.getMatchTimeInfo().setPeriod(matchEventInfoDTO.getMatchPeriodId());
        data.getMatchScoresInfo().setPeriod(matchEventInfoDTO.getMatchPeriodId());
        //阶段比分更新
        data.getMatchScoresInfo().setPeriodT1(matchEventInfoDTO.getFirstT1());
        data.getMatchScoresInfo().setPeriodT2(matchEventInfoDTO.getFirstT2());
        //时间更新
        if(data.getThirdMatchInfo().getSportId().equals(2l)){
            if(SportPeriodConstant.BasketballPeriod.contans(matchEventInfoDTO.getMatchPeriodId(),data.getMatchTimeInfo().getMatchLength())){
                data.getMatchTimeInfo().setTimeGo(1);
            }else {
                data.getMatchTimeInfo().setTimeGo(0);
            }
        }else if(data.getThirdMatchInfo().getSportId().equals(1l)){
            if(SportPeriodConstant.FootballPeriod.contans(matchEventInfoDTO.getMatchPeriodId())){
                data.getMatchTimeInfo().setTimeGo(1);
            }else {
                data.getMatchTimeInfo().setTimeGo(0);
            }
        }
        //切换到点球大战就初始化点球大战比分
        if(data.getMatchScoresInfo().getSportId().equals(1l)&&matchEventInfoDTO.getMatchPeriodId().equals(50l)){
            initPenaltyScores(data.getMatchScoresInfo());
        }

        if( data.getMatchScoresInfo().getSportId().equals(1l) &&
                ( matchEventInfoDTO.getMatchPeriodId().equals(6L) || matchEventInfoDTO.getMatchPeriodId().equals(7L) ) ){
            initStartMinScores(data.getMatchScoresInfo(), matchEventInfoDTO.getMatchPeriodId() );
        }

//        //再来个 15分钟进球数 初始化 TODO
//        if(data.getMatchScoresInfo().getSportId().equals(1l)&&matchEventInfoDTO.getMatchPeriodId().equals(6L)){
//            init15MinScores(data.getMatchScoresInfo());
//        }
        //再来个 5分钟进球数 初始化 TODO
//        if(data.getMatchScoresInfo().getSportId().equals(1l)&&matchEventInfoDTO.getMatchPeriodId().equals(6L)){
//            init5MinScores(data.getMatchScoresInfo());
//        }
        data.getMatchScoresInfo().setSecondsMatchStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchScoresInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setSecondFromStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchTimeInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setEventTime(System.currentTimeMillis());
//        matchTimeInfoMapper.updateByPrimaryKey(data.getMatchTimeInfo());
//        matchScoresInfoMapper.updateByPrimaryKey(  data.getMatchScoresInfo());
        timeInfoRepository.updateByPrimaryKey(data.getMatchTimeInfo());
//        timeInfoRepository.updateByPrimaryKey(data.getMatchTimeInfo());
        matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
    }

    private void initStartMinScores(MatchScoresInfo matchScoresInfo, Long PeriodId) {
        try{
            //scoresJson比分信息
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
            if (PeriodId.equals(6l)) {

                FootballScores periodSores15 = allPeriodScores.get(60899L);
                if(periodSores15 == null){
                    periodSores15 = FootballScores.createMinFootballScores();
                    allPeriodScores.put(60899L,periodSores15);
                }

                FootballScores periodSores5 = allPeriodScores.get(6005L);
                if(periodSores5 == null){
                    periodSores5 = FootballScores.createMinFootballScores();
                    allPeriodScores.put(6005L,periodSores5);
                }

            }

            if (PeriodId.equals(7l)) {

                FootballScores periodSores60 = allPeriodScores.get(73599L);
                if(periodSores60 == null){
                    periodSores60 = FootballScores.createMinFootballScores();
                    allPeriodScores.put(73599L,periodSores60);
                }

                FootballScores periodSores750 = allPeriodScores.get(7050L);
                if(periodSores750 == null){
                    periodSores750 = FootballScores.createMinFootballScores();
                    allPeriodScores.put(7050L,periodSores750);
                }
            }
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }


    private void init15MinScores(MatchScoresInfo matchScoresInfo) {
        try{
            //scoresJson比分信息
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores periodSores15 = allPeriodScores.get(60899L);
            if(periodSores15 == null){
                periodSores15 = FootballScores.createMinFootballScores();
                allPeriodScores.put(60899L,periodSores15);
            }
            FootballScores periodSores30 = allPeriodScores.get(61799L);
            if(periodSores30 == null){
                periodSores30 = FootballScores.createMinFootballScores();
                allPeriodScores.put(61799L,periodSores30);
            }
            FootballScores periodSores45 = allPeriodScores.get(62699L);
            if(periodSores45 == null){
                periodSores45 = FootballScores.createMinFootballScores();
                allPeriodScores.put(62699L,periodSores45);
            }
            FootballScores periodSores60 = allPeriodScores.get(73599L);
            if(periodSores60 == null){
                periodSores60 = FootballScores.createMinFootballScores();
                allPeriodScores.put(73599L,periodSores60);
            }
            FootballScores periodSores75 = allPeriodScores.get(74499L);
            if(periodSores75 == null){
                periodSores75 = FootballScores.createMinFootballScores();
                allPeriodScores.put(74499L,periodSores75);
            }
            FootballScores periodSores90 = allPeriodScores.get(75399L);
            if(periodSores90 == null){
                periodSores90 = FootballScores.createMinFootballScores();
                allPeriodScores.put(75399L,periodSores90);
            }
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    private void initPenaltyScores(MatchScoresInfo matchScoresInfo) {
        try{
            //scoresJsonExtra 点球信息
            String extraScores =matchScoresInfo.getScoresJsonExtra();
            FootballPenaltyScores footballPenaltyScores=null;
            if(StringUtils.isEmpty(extraScores)){
                footballPenaltyScores =new FootballPenaltyScores(1,1);
            }else {
                footballPenaltyScores= JSONObject.toJavaObject((JSONObject.parseObject(extraScores)) , FootballPenaltyScores.class);
            }
            matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores, SerializerFeature.WriteMapNullValue));
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    @Override
    public ThirdSportTeam getThirdSportTeamByThirdMatch(ThirdMatchInfo thirdMatchInfo, String homeAway) {
        if(!TeamTypeEnum.HOME.code.equals(homeAway)&&!TeamTypeEnum.AWAY.code.equals(homeAway)) {
            return null;
        }
        ThirdMatchTeamRelationExample thirdMatchTeamRelationExample=new ThirdMatchTeamRelationExample();
        thirdMatchTeamRelationExample.createCriteria()
                .andMatchIdEqualTo(thirdMatchInfo.getId());
        List<ThirdMatchTeamRelation> thirdMatchTeamRelations= thirdMatchTeamRelationMapper.selectByExample(thirdMatchTeamRelationExample);
        if(thirdMatchTeamRelations.size()==0){
            return null;
        }
        ThirdMatchTeamRelation thirdMatchTeamRelation=null;
        for (ThirdMatchTeamRelation matchTeamRelation : thirdMatchTeamRelations) {
            if(matchTeamRelation.getMatchPosition().equals(homeAway)){
                thirdMatchTeamRelation=matchTeamRelation;
            }
        }
        ThirdSportTeam thirdSportTeam =thirdSportTeamMapper.selectByPrimaryKey(thirdMatchTeamRelation.getTeamId());
        return thirdSportTeam;
    }

    @Override
    public MatchScoreAndTimeVo searchMatchScoreAndTime(Long thirdMatchId) throws Exception {
        log.info("[CommonAdvertiseServiceImpl]searchMatchScoreAndTime start thirdMatchId::{}",thirdMatchId);
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(thirdMatchId, null);
        if(thirdMatchInfo==null){
            throw new Exception("三方赛事不存在！");
        }
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchId, SourceTypeEnum.LIVE_DATA.getCode());
        if(matchScoresInfo==null){
            matchScoresInfo = scoresService.createPDMatchScoresInfo(thirdMatchInfo);
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, SnookerConstant.MATCH_CURRENT_PERIOD, matchScoresInfo.getPeriod());
        }
        
        // 比分校验：如果 scoresJson 为空或无效，初始化对应运动类型的比分数据
        boolean needInit = false;
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            needInit = true;
        } else {
            // 检查 scoresJson 是否是有效的 JSON（对于斯诺克 / 排球）
            if(thirdMatchInfo.getSportId().equals(7L) || thirdMatchInfo.getSportId().equals(9L)) {
                try {
                    JSONObject.parseObject(matchScoresInfo.getScoresJson());
                } catch (Exception e) {
                    log.warn("[CommonAdvertiseServiceImpl]searchMatchScoreAndTime invalid scoresJson sportId:{} match:{} error:{}",
                            thirdMatchInfo.getSportId(), thirdMatchId, e.getMessage());
                    needInit = true;
                }
            }
        }
        
        if(needInit){
            if(thirdMatchInfo.getSportId().equals(2l)){
                Map<Long, BasketballScores> periodFootballScores= new HashMap<>();
                BasketballScores basketballScores=new BasketballScores(0l);
                periodFootballScores.put(WHOLE_MATCH,basketballScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (thirdMatchInfo.getSportId().equals(1l)){
                Map<Long, FootballScores> periodFootballScores= new HashMap<>();
                FootballScores footballScores=new FootballScores(0l);
                periodFootballScores.put(WHOLE_MATCH,footballScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (thirdMatchInfo.getSportId().equals(5l)) {
                Map<Long, TennisScores> periodFootballScores= new HashMap<>();
                TennisScores tennisScores = new TennisScores(0l);
                periodFootballScores.put(WHOLE_MATCH, tennisScores);
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
            } else if (thirdMatchInfo.getSportId().equals(7l)) {
                // 斯诺克初始化：创建全局比分（wholeScore）和当前局比分（如果存在）
                Map<Long, SnookerV2Scores> allPeriodScores = new HashMap<>();
                SnookerV2Scores wholeScore = new SnookerV2Scores();
                allPeriodScores.put(WHOLE_MATCH, wholeScore);
                
                // 如果当前有阶段信息，也初始化当前阶段的比分
                Long currentPeriod = matchScoresInfo.getPeriod();
                if (currentPeriod != null && currentPeriod > 0 && !currentPeriod.equals(WHOLE_MATCH)) {
                    SnookerV2Scores periodScore = new SnookerV2Scores();
                    allPeriodScores.put(currentPeriod, periodScore);
                }

                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                log.info("[CommonAdvertiseServiceImpl]searchMatchScoreAndTime initialized snooker scores for match:{} period:{}", 
                        thirdMatchId, currentPeriod);
            } else if (thirdMatchInfo.getSportId().equals(9L)) {
                // 排球初始化：与斯诺克对齐，建全局 (-1) 桶 + 当前局桶（若有）
                Map<Long, VolleyballV2Scores> allPeriodScores = new HashMap<>();
                allPeriodScores.put(WHOLE_MATCH, new VolleyballV2Scores());

                Long currentPeriod = matchScoresInfo.getPeriod();
                if (currentPeriod != null && currentPeriod > 0 && !currentPeriod.equals(WHOLE_MATCH)) {
                    allPeriodScores.put(currentPeriod, new VolleyballV2Scores());
                }

                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                log.info("[CommonAdvertiseServiceImpl]searchMatchScoreAndTime initialized volleyball scores for match:{} period:{}",
                        thirdMatchId, currentPeriod);
            }
            // 更新缓存和数据库
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        }
        
        MatchTimeInfo matchTimeInfo = timeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
//        if(matchTimeInfo == null){
//            matchTimeInfo = new MatchTimeInfo();
//            matchTimeInfo.setCreateTime(System.currentTimeMillis());
//            matchTimeInfo.setModifyTime(System.currentTimeMillis());
//            matchTimeInfo.setDataSourceType(matchScoresInfo.getDataSourceType());
//            matchTimeInfo.setTimeGo(0);
//            matchTimeInfo.setThirdMatchId(matchScoresInfo.getThirdMatchId());
//            matchTimeInfo.setPeriod(matchScoresInfo.getPeriod());
//            matchTimeInfo.setSecondFromStart(matchScoresInfo.getSecondsMatchStart());
//            matchTimeInfo.setId(matchScoresInfo.getId());
//            matchTimeInfo.setMatchLength(matchScoresInfo.getMatchLength());
//            matchTimeInfo.setRemainingTime(matchScoresInfo.getRemainingTime());
//            matchTimeInfo.setEventTime(System.currentTimeMillis());
//            timeInfoRepository.updateByPrimaryKey(matchTimeInfo);
//        }
        StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
        MatchScoreAndTimeVo matchScoreAndTimeVo =new MatchScoreAndTimeVo();
        matchScoreAndTimeVo.setThirdMatchInfo(thirdMatchInfo);
        matchScoreAndTimeVo.setMatchScoresInfo(matchScoresInfo);
        matchScoreAndTimeVo.setMatchTimeInfo(matchTimeInfo);
        matchScoreAndTimeVo.setStandardMatchInfo(standardMatchInfo);
        log.info("[CommonAdvertiseServiceImpl]searchMatchScoreAndTime end!");
        return matchScoreAndTimeVo;
    }

    private void init5MinScores(MatchScoresInfo matchScoresInfo) {
        try{
            //scoresJson比分信息
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores periodSores5 = allPeriodScores.get(6005L);
            if(periodSores5 == null){
                periodSores5 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6005L,periodSores5);
            }
            FootballScores periodSores10 = allPeriodScores.get(6010L);
            if(periodSores10 == null){
                periodSores10 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6010L,periodSores10);
            }
            FootballScores periodSores15 = allPeriodScores.get(6015L);
            if(periodSores15 == null){
                periodSores15 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6015L,periodSores15);
            }
            FootballScores periodSores20 = allPeriodScores.get(6020L);
            if(periodSores20 == null){
                periodSores20 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6020L,periodSores20);
            }
            FootballScores periodSores25 = allPeriodScores.get(6025L);
            if(periodSores25 == null){
                periodSores25 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6025L,periodSores25);
            }
            FootballScores periodSores30 = allPeriodScores.get(6030L);
            if(periodSores30 == null){
                periodSores30 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6030L,periodSores30);
            }
            FootballScores periodSores35 = allPeriodScores.get(6035L);
            if(periodSores35 == null){
                periodSores35 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6035L,periodSores35);
            }
            FootballScores periodSores40 = allPeriodScores.get(6040L);
            if(periodSores40 == null){
                periodSores40 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6040L,periodSores40);
            }
            FootballScores periodSores45 = allPeriodScores.get(6045L);
            if(periodSores45 == null){
                periodSores45 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6045L,periodSores45);
            }
            FootballScores periodSores50 = allPeriodScores.get(6050L);
            if(periodSores50 == null){
                periodSores50 = FootballScores.createMinFootballScores();
                allPeriodScores.put(6050L,periodSores50);
            }
            FootballScores periodSores750 = allPeriodScores.get(7050L);
            if(periodSores750 == null){
                periodSores750 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7050L,periodSores750);
            }
            FootballScores periodSores55 = allPeriodScores.get(7055L);
            if(periodSores55 == null){
                periodSores55 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7055L,periodSores55);
            }
            FootballScores periodSores60 = allPeriodScores.get(7060L);
            if(periodSores60 == null){
                periodSores60 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7060L,periodSores60);
            }
            FootballScores periodSores65 = allPeriodScores.get(7065L);
            if(periodSores65 == null){
                periodSores65 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7065L,periodSores65);
            }
            FootballScores periodSores70 = allPeriodScores.get(7070L);
            if(periodSores70 == null){
                periodSores70 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7070L,periodSores70);
            }
            FootballScores periodSores75 = allPeriodScores.get(7075L);
            if(periodSores75 == null){
                periodSores75 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7075L,periodSores75);
            }
            FootballScores periodSores80 = allPeriodScores.get(7080L);
            if(periodSores80 == null){
                periodSores80 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7080L,periodSores80);
            }
            FootballScores periodSores85 = allPeriodScores.get(7085L);
            if(periodSores85 == null){
                periodSores85 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7085L,periodSores85);
            }
            FootballScores periodSores90 = allPeriodScores.get(7090L);
            if(periodSores90 == null){
                periodSores90 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7090L,periodSores90);
            }
            FootballScores periodSores95 = allPeriodScores.get(7095L);
            if(periodSores95 == null){
                periodSores95 = FootballScores.createMinFootballScores();
                allPeriodScores.put(7095L,periodSores95);
            }
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }
}
