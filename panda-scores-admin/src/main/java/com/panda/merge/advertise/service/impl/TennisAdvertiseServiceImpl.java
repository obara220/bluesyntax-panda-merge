package com.panda.merge.advertise.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.dto.TennisAdvertiseVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.event.FootBallEventService;
import com.panda.merge.advertise.event.TennisEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.service.FootBallScoreService;
import com.panda.merge.advertise.service.TennisAdvertiseService;
import com.panda.merge.advertise.service.TennisScoreService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.advertise.utils.MatchScoreUtils;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.TennisMatchLengthEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.utils.CustomThreadPoolExecutor;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.advertise.common.Constant.PD;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


@Service
@Slf4j
public class TennisAdvertiseServiceImpl implements TennisAdvertiseService {


//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    FootBallScoreService footBallScoreService;
    @Autowired
    IScoresService scoresService;
    @Autowired
    FootBallEventService footBallEventService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    CommonEventService commonEventService;
    @Autowired
    RedisService redisService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    @Autowired
    IMatchScorePdLogService iMatchScorePdLogService;
    @Autowired
    TennisEventService tennisEventService;
    @Autowired
    TennisScoreService tennisScoreService;
    @Autowired
    MatchScoresPdLogMapper matchScoresPdLogMapper;
//    @Autowired
//    StandardMatchScoresMapper standardMatchScoresMapper;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    @Autowired
    PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    ScoresRedisHelp scoresRedisHelp;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    //网球完赛状态
    List<Long> peroId = Arrays.asList(61L, 80L, 90L, 999L);
    List<Long> setPeriod = Arrays.asList(800L, 900L, 1000L, 1100L, 1200L);
    @Override
    public Response matchBegin(TennisAdvertiseDto matchDto , Response<MatchScoreAndTimeVo> response)
    {
        String linkId = matchDto.getLinkedId();
        log.info("::{}::matchBegin:{}", linkId, JSONObject.toJSONString(matchDto));
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        //1.更新赛事时间
        MatchTimeInfo matchTimeInfoUp = updateMatchTime(matchTimeInfo,response.getData().getStandardMatchInfo());

        // 更新赛事状态
        syncMatchStatus(linkId, thirdMatchInfo, 1, 8L);

        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
        if ( null != matchScoresInfo && !allPeriodScores.containsKey(WHOLE_MATCH)) {
            TennisScores tennisScores = new TennisScores(0l);
            allPeriodScores.put(WHOLE_MATCH, tennisScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(),null);

        }
        //第一盘初始化
        initSet(8L, matchScoresInfo);

        //2.下发滚球状态变更
        eventProducer.sendMatchStatusTopic(matchDto.getLinkedId(), response.getData().getThirdMatchInfo(), 1);

        eventProducer.sendMatchStartStatus( response.getData().getThirdMatchInfo(), matchDto.getLinkedId());

        //3.下发滚球事件变更
        MatchEventInfoDTO eventInfoDTO = buildMatchEventInfoDTO(matchDto.getLinkedId(),"match_status", matchScoresInfo, matchTimeInfo);
        eventInfoDTO.setMatchPeriodId(8l);
        eventInfoDTO.setSecondT1(0);
        eventInfoDTO.setSecondT2(0);
        eventProducer.sendPDEventInfo(eventInfoDTO);


        MatchEventInfoDTO scoreEventInfoDTO = new MatchEventInfoDTO();
        BeanUtils.copyProperties(eventInfoDTO, scoreEventInfoDTO);
        scoreEventInfoDTO.setEventCode("tennis_score_change");
        log.info("::{}::changeSetStatus赛事比分事件的下发:{}", linkId, JSON.toJSONString(scoreEventInfoDTO));
        eventProducer.sendPDEventInfo(scoreEventInfoDTO);

        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),UUID.randomUUID()+"PD");

        //4.推送广播
        redisUtils.pushTenniseScore(matchTimeInfoUp.getThirdMatchId());
        //5记录操作日志
        iMatchScorePdLogService.matchBeginLog(matchDto, matchTimeInfoUp);
        return Response.success();
    }

    MatchTimeInfo updateMatchTime(MatchTimeInfo matchTimeInfo,StandardMatchInfo standardMatchInfo){
        matchTimeInfo.setCurrentRound(1);
        matchTimeInfo.setCurrentSet(1);
        matchTimeInfo.setFirstNum(1);
        matchTimeInfo.setMatchLength(2);
        matchTimeInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchTimeInfo.setPeriod(8L);
        if (matchTimeInfo.getRoundType() == null || matchTimeInfo.getRoundType() == 0) {
            matchTimeInfo.setRoundType(standardMatchInfo.getRoundType());
        }

            HashMap<String, String> map = new HashMap<>();
            if (matchTimeInfo.getRoundType().equals(3)){
                map.put("1","13");
                map.put("2","13");
                map.put("3","13");
            }else if (matchTimeInfo.getRoundType().equals(5)){
                map.put("1","13");
                map.put("2","13");
                map.put("3","13");
                map.put("4","13");
                map.put("5","13");
            }
            String mapJson = JSON.toJSONString(map);
            matchTimeInfo.setPeriodLengthJson(mapJson);

//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
        pdMatchInfoRepository.setRedisMatchTimeInfo(matchTimeInfo,null);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
        return matchTimeInfo;
    }


    @Override
    public Response matchEnd(TennisAdvertiseDto matchDto ,Response<MatchScoreAndTimeVo> response)
    {
        String linkId = matchDto.getLinkedId();
        log.info("::{}::TennisAdvertiseServiceImpl_match999End入参:{}", linkId, JSONObject.toJSONString(matchDto));
        Long period = matchDto.getPeriod();
        if (!peroId.contains(period)) {
            return Response.failed("没有这个阶段");
        }

        //1.修改三方赛事和标准赛事状态
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        Long perid = matchTimeInfo.getPeriod();

        matchTimeInfo.setPeriod(period);
        matchTimeInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//        pdMatchInfoRepository.setRedisMatchTimeInfo(matchTimeInfo,null);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoMapper.selectByPrimaryKey(matchTimeInfo.getThirdMatchId());

        Integer matchStatus = 3;
        // 中断
        if ( 80 == period )
        {
            matchStatus = 10;
        }
        // 延迟
        else if ( 61 == period )
        {
            matchStatus = 7;
        }
        // 取消
        else if ( 90 == period )
        {
            matchStatus = 5;
        }

        // 更新赛事状态
        syncMatchStatus(linkId, thirdMatchInfo, matchStatus, period);

        //2.下发状态变更
        eventProducer.sendMatchStatusTopic(matchDto.getLinkedId(), response.getData().getThirdMatchInfo(), matchStatus);

        //3.下发事件变更
        MatchEventInfoDTO eventInfoDTO = new MatchEventInfoDTO();
        eventInfoDTO.setDataSourceCode(response.getData().getThirdMatchInfo().getDataSourceCode());
        eventInfoDTO.setThirdEventId(matchDto.getLinkedId());
        eventInfoDTO.setSportId(5L);
        eventInfoDTO.setCanceled(0);
        eventInfoDTO.setSourceType("1");
        //eventInfoDTO.setMatchPeriodId(999L);
        eventInfoDTO.setMatchPeriodId(period);
        if ( !Objects.isNull(thirdMatchInfo) )
        {
            eventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        }

        eventInfoDTO.setCopyLinkId(matchDto.getLinkedId());
        eventInfoDTO.setEventTime(System.currentTimeMillis());
        eventInfoDTO.setEventCode("match_status");
        eventInfoDTO.setCanceled(0);
        eventInfoDTO.setSecondsFromStart(0L);
        eventInfoDTO.setSecondT1(0);
        eventInfoDTO.setSecondT2(0);
        log.info("::{}::TennisAdvertiseServiceImpl_match999End,下发参数:{}", linkId, JSON.toJSONString(eventInfoDTO));
        eventProducer.sendPDEventInfo(eventInfoDTO);

        // 推零
        scoresProducer.sendScore(matchDto.getLinkedId(), response.getData().getThirdMatchInfo(),
                response.getData().getMatchScoresInfo(), false);

        MatchEventInfoDTO scoreEventInfoDTO = new MatchEventInfoDTO();
        BeanUtils.copyProperties(eventInfoDTO, scoreEventInfoDTO);
        scoreEventInfoDTO.setEventCode("tennis_score_change");
        log.info("::{}::changeSetStatus赛事比分事件的下发:{}", linkId, JSON.toJSONString(scoreEventInfoDTO));
        eventProducer.sendPDEventInfo(scoreEventInfoDTO);

        String key = RedisConfig.REDIS_KEY_DATABASE+"_"+response.getData().getThirdMatchInfo().getId();
        String value = perid+"_"+ matchTimeInfo.getCurrentSet()+"_"+matchTimeInfo.getCurrentRound();
        redisService.set(key, value, 60 * 60 * 24 * 5);

        //4.推送ws
        redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

        //5记录操作日志
        iMatchScorePdLogService.matchEndLog(matchDto, matchTimeInfo);
        return Response.success();
    }

    @Override
    public Response matchStatusReSet(TennisAdvertiseDto matchDto, Response<MatchScoreAndTimeVo> response)
    {
        String linkId = matchDto.getLinkedId();
        log.info("::{}::赛事恢复matchStatusReSet的入参:{}", linkId, JSON.toJSONString(matchDto));
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        if (matchTimeInfo == null) {
            log.info("::{}::赛事恢复,赛事时间为空:{}", linkId, matchDto.getStandardMatchId());
            return Response.failed();
        }
        String key = RedisConfig.REDIS_KEY_DATABASE+"_"+matchTimeInfo.getThirdMatchId();
        Object value = redisService.get(key);
        log.info("::{}::matchStatusReSet取值, key:{}", linkId, key);
        log.info("::{}::matchStatusReSet取值, value:{}", linkId, value);
        if (value == null) {
            return Response.failed("赛事结束时间过长,无法恢复");
        }
        String str = String.valueOf(value);
        String[] split = str.split("_");
        Long period = Long.parseLong(split[0]);
        String set = split[1];
        String round = split[2];
        log.info("::{}::matchStatusReSet取值, period:{}, set:{}, round:{}", linkId, period, set, round);

        matchTimeInfo.setPeriod(period);
        matchTimeInfo.setCurrentSet(Integer.parseInt(set));
        matchTimeInfo.setCurrentRound(Integer.parseInt(round));
        matchTimeInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());

        //3.下发滚球事件变更
        MatchEventInfoDTO eventInfoDTO = new MatchEventInfoDTO();
        eventInfoDTO.setEventTime(TimeUtils.millsSecondsEast8ZoneGmt());
        eventInfoDTO.setSportId(5L);

        ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
        // 更新赛事状态
        syncMatchStatus(linkId, thirdMatchInfo, 1, period);

        eventInfoDTO.setEventCode("match_status");
        eventInfoDTO.setMatchPeriodId(period);
        eventInfoDTO.setDataSourceCode("PD");
        eventInfoDTO.setCopyLinkId(linkId);
        eventInfoDTO.setSourceType("1");
        log.info("::{}::matchStatusReSet;sendPDEventInfo:{}", linkId, JSON.toJSONString(eventInfoDTO));
        //下发事件
        eventProducer.sendPDEventInfo(eventInfoDTO);

        eventProducer.sendMatchStartStatus( response.getData().getThirdMatchInfo(), linkId);

//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//        pdMatchInfoRepository.setRedisMatchTimeInfo(matchTimeInfo,null);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
        //删除缓存中的阶段
        redisService.del(key);

        //4.推送ws
        redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

        //5记录操作日志
        iMatchScorePdLogService.matchStatusReSetLog(matchDto);
        return Response.success();
    }

    @Override
    public Response setRoundType(PDRoundTypeEditDto matchDto , Response<MatchScoreAndTimeVo> response) {
        log.info("::{}::setRoundType,参数信息:{}", matchDto.getLinkedId(), JSON.toJSON(matchDto));

        try {
            if(response.getData().getMatchTimeInfo().getPeriod()>0){
                return Response.failed("比赛已经开始不能更换赛制");
            }
            //1.修改三方赛事和标准赛事状态
            StandardMatchInfo standardMatchInfo = response.getData().getStandardMatchInfo();
            if (standardMatchInfo == null) {
                log.info("::{}::标准赛事信息为空:{}", matchDto.getLinkedId(), JSON.toJSON(matchDto));
                return Response.failed();
            }
            ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
            if (thirdMatchInfo == null) {
                log.info("::{}::三方赛事信息为空:{}", matchDto.getLinkedId(), JSON.toJSON(matchDto));
                return Response.failed();
            }
            MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
            MatchTimeInfo matchTimeInfoOid = new  MatchTimeInfo();
            BeanUtils.copyProperties(matchTimeInfo,matchTimeInfoOid);
            if (matchTimeInfo==null) {
                log.info("::{}::赛事时间信息为空:{}", matchDto.getLinkedId(), JSON.toJSON(matchDto));
                return Response.failed();
            }
            long timeMillis = System.currentTimeMillis();

            matchTimeInfo.setRoundType(matchDto.getRoundType());
            thirdMatchInfo.setRoundType(matchDto.getRoundType());
            standardMatchInfo.setRoundType(matchDto.getRoundType());
            thirdMatchInfo.setModifyTime(timeMillis);
            matchTimeInfo.setModifyTime(timeMillis);
            standardMatchInfo.setModifyTime(timeMillis);
            initMatchTime(matchTimeInfo,matchDto.getRoundType());
            thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
            standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
//            matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//            pdMatchInfoRepository.setRedisMatchTimeInfo(matchTimeInfo,null);
            matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
            //4.推送ws
            redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

            //5记录操作日志
            iMatchScorePdLogService.setRoundTypeLog(matchTimeInfoOid,matchTimeInfo,matchDto);
        } catch (Exception e) {
            log.error("::{}::setMatchLength ERROR:", matchDto.getLinkedId(), e);
        }
        return Response.success();

    }

    private void initMatchTime(MatchTimeInfo matchTimeInfo, Integer roundType) {
        if(roundType==3){
            JSONObject setRoundJson=new JSONObject();
            setRoundJson.put("1",13); setRoundJson.put("2",13); setRoundJson.put("3",13);
            matchTimeInfo.setPeriodLengthJson(setRoundJson.toJSONString());

            JSONObject setMatchLengthJson=new JSONObject();
            setMatchLengthJson.put("1",2); setMatchLengthJson.put("2",2); setMatchLengthJson.put("3",2);
            matchTimeInfo.setMatchLengthJson(setMatchLengthJson.toJSONString());
        }else if( roundType==5){
            JSONObject setRoundJson=new JSONObject();
            setRoundJson.put("1",13); setRoundJson.put("2",13); setRoundJson.put("3",13); setRoundJson.put("4",13); setRoundJson.put("5",13);
            matchTimeInfo.setPeriodLengthJson(setRoundJson.toJSONString());

            JSONObject setMatchLengthJson=new JSONObject();
            setMatchLengthJson.put("1",2); setMatchLengthJson.put("2",2); setMatchLengthJson.put("3",2); setMatchLengthJson.put("4",2); setMatchLengthJson.put("5",2);
            matchTimeInfo.setMatchLengthJson(setMatchLengthJson.toJSONString());
        }
    }

    @Override
    public Response setMatchLength(TennisAdvertiseDto matchDto , Response<MatchScoreAndTimeVo> response) {
        log.info("::{}::setMatchLength,参数信息:{}", matchDto.getLinkedId(), JSON.toJSON(matchDto));
        if (matchDto.getCurrentSet() == null || matchDto.getMatchLength() == null) {
            return Response.failed("参数有误! 请校验参数信息");
        }

        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchTimeInfo matchTimeInfoOid = new MatchTimeInfo();
        BeanUtils.copyProperties(matchTimeInfo,matchTimeInfoOid);
        matchTimeInfo.setMatchLength(matchDto.getMatchLength());
        matchTimeInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        String periodLengthJson = matchTimeInfo.getPeriodLengthJson();
        if (periodLengthJson == null || "".equals(periodLengthJson)) {
            initPeriodLength(matchTimeInfo);
        }
        //根据periodLengthJson 字段
        HashMap<String,String> map = JSON.parseObject(periodLengthJson, HashMap.class);
        TennisMatchLengthEnum byCode = TennisMatchLengthEnum.getByCode(String.valueOf(matchDto.getMatchLength()));
        map.put(String.valueOf(matchDto.getCurrentSet()),byCode.getValue());
        matchTimeInfo.setPeriodLengthJson(JSON.toJSONString(map));

        HashMap<String,String> mapMatchLength = JSON.parseObject(matchTimeInfo.getMatchLengthJson(), HashMap.class);
        mapMatchLength.put(String.valueOf(matchDto.getCurrentSet()),matchDto.getMatchLength().toString());
        matchTimeInfo.setMatchLengthJson(JSON.toJSONString(mapMatchLength));
//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);

//        pdMatchInfoRepository.setRedisMatchTimeInfo(matchTimeInfo,null);
        matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo,null);
        //4.推送ws
        redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

        //5记录操作日志
        iMatchScorePdLogService.setMatchLengthLog(matchTimeInfoOid,matchTimeInfo,matchDto);
        return Response.success();
    }

    void  initPeriodLength(MatchTimeInfo matchTimeInfo){
        String periodLengthJson = matchTimeInfo.getPeriodLengthJson();
        HashMap<String, String> map = null;
        if (periodLengthJson == null || "".equals(periodLengthJson)) {
            map = JSON.parseObject(periodLengthJson, HashMap.class);
            if (matchTimeInfo.getRoundType().equals(3)){
                map.put("1","13");
                map.put("2","13");
                map.put("3","13");
            }else if (matchTimeInfo.getRoundType().equals(5)){
                map.put("1","13");
                map.put("2","13");
                map.put("3","13");
                map.put("4","13");
                map.put("5","13");
            }
        }
        String jsonString = JSON.toJSONString(map);
        matchTimeInfo.setPeriodLengthJson(jsonString);
    }

    //网球报球版查询
    @Override
    public Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto)
    {
        String linkId = matchAdvertiseQueryDto.getLinkedId();
        try
        {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(matchAdvertiseQueryDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            MatchScoreUtils.buildTennisScore(response.getData().getMatchScoresInfo(), response.getData().getMatchTimeInfo().getRoundType(), response.getData().getMatchTimeInfo().getPeriodLengthJson());
            //要查询当前 赛制 局制 盘比分 总局比分  全部局比分  全部盘局内比分 当前盘 当前局 赛事状态(是否开始)
            TennisAdvertiseVo tennisAdvertiseVo = new TennisAdvertiseVo();
            tennisAdvertiseVo.setMatchLength(response.getData().getMatchTimeInfo().getMatchLength());
            tennisAdvertiseVo.setRoundType(response.getData().getMatchTimeInfo().getRoundType());
            //比分渲染
            TennisExtryScores tennisExtryScores;
            if (StringUtils.isEmpty(response.getData().getMatchScoresInfo().getScoresJsonExtra())) {
                tennisExtryScores = new TennisExtryScores();
            } else {
                tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJsonExtra())), TennisExtryScores.class);
            }
            //总局比分 盘比分
            JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
            Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
            MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
            if ( null != matchScoresInfo && !allPeriodScores.containsKey(WHOLE_MATCH)) {
                TennisScores tennisScores = new TennisScores(0l);
                allPeriodScores.put(WHOLE_MATCH, tennisScores);
            }
            TennisScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
            tennisAdvertiseVo.setMatchScore(wholeSores.getMatchScore());
            tennisAdvertiseVo.setTotalRoundScore(wholeSores.getSetScore());
            //全部盘局内比分
            Map<Integer, Map<Integer,CommonItem>> currentScoresMap = filterScoresRecordMap(linkId, response.getData().getMatchTimeInfo(), tennisExtryScores.getCurrentScoresMap(), response.getData().getMatchTimeInfo().getPeriodLengthJson());
            tennisAdvertiseVo.setAllSetSecondScore(currentScoresMap);
//            tennisAdvertiseVo.setAllSetSecondScore(tennisExtryScores.getCurrentScoresMap());
            //全部局比分
            Map<Integer, CommonItem> setScore = new HashMap<>();
            for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet()) {
                if (entry.getKey().equals(WHOLE_MATCH)) {
                    continue;
                }
                Integer setNumber = MatchPeriodUtils.getTennisSetByPeriod(entry.getKey());
                if (null == setNumber) {
                    continue;
                }
                CommonItem commonItemVo = new CommonItem();
                BeanUtils.copyProperties(entry.getValue().getSetScore(), commonItemVo);
                setScore.put(setNumber, commonItemVo);
            }
            tennisAdvertiseVo.setAllSetRoundScore(setScore);
            //当前阶段 + 状态
            tennisAdvertiseVo.setPeriod(response.getData().getMatchTimeInfo().getPeriod());
            //当前局
            tennisAdvertiseVo.setCurrentRound(response.getData().getMatchTimeInfo().getCurrentRound());
            //当前盘
            tennisAdvertiseVo.setCurrentSet(response.getData().getMatchTimeInfo().getCurrentSet());
            tennisAdvertiseVo.setFirstNum(response.getData().getMatchTimeInfo().getFirstNum());
            //局制 和 局长
            if (StringUtils.isNotEmpty(response.getData().getMatchTimeInfo().getPeriodLengthJson())) {
                tennisAdvertiseVo.setPeriodLengthJson(JSONObject.parseObject(response.getData().getMatchTimeInfo().getPeriodLengthJson()));
                tennisAdvertiseVo.setMatchLengthJson(JSONObject.parseObject(response.getData().getMatchTimeInfo().getMatchLengthJson()));
            }
            //赛事信息
            tennisAdvertiseVo.setStandardMatchId(response.getData().getStandardMatchInfo().getId());
            tennisAdvertiseVo.setThirdMatchId(response.getData().getThirdMatchInfo().getId().toString());
            tennisAdvertiseVo.setTotalSet((wholeSores.getMatchScore().getHome() + wholeSores.getMatchScore().getAway()));
            tennisAdvertiseVo.setTotalRound((wholeSores.getSetScore().getHome() + wholeSores.getSetScore().getAway()));
            return Response.success(tennisAdvertiseVo);
        }
        catch (Exception e)
        {
            
            log.error("::{}::", linkId, e);
            return Response.failed(e.getMessage());
        }

    }

    @Override
    public Response setMatchSecondScore(TennisEditSecondScoreDto dto,Response<MatchScoreAndTimeVo> response)
    {
        String linkId = dto.getLinkedId();
        log.info("::{}::setMatchSecondScore的入参:{}", linkId, JSON.toJSONString(dto));
        //0.跳分规则判断是否传参正确
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        //1.局获胜判断
        Integer winRoundFlag= 0;

        //singleScore  1:跳分(15/30/40/50)   2:单分(1/2/3/4)
        Integer singleScore = isSingleScore(matchTimeInfo);

        if (singleScore == 1) {
            winRoundFlag= MatchScoreUtils.chargeRoundWin(dto.getT1(),dto.getT2());
        }else{
            winRoundFlag= MatchScoreUtils.singleRoundWin(dto.getT1(),dto.getT2(), matchTimeInfo.getMatchLength());
        }
        //2.如果没有局获胜则直接赋值
        //比分先更正 60=直接获胜 改为50 AD
        //MatchScoreUtils.reSetSecondScores(tennisEditSecondScoreDto);
        if(dto.getT1()==60){
            dto.setT1(50);
        }
        if(dto.getT2()==60){
            dto.setT2(50);
        }
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchScoresInfo odiMatchScoresInfo = new MatchScoresInfo();
        BeanUtils.copyProperties(matchScoresInfo,odiMatchScoresInfo);

        updateSecondScore(dto,response);
        //3.如果局获胜则更新局比分再进行盘比分校验---改为局结束的时候进行局比分修正
        if(winRoundFlag>0){
//            updateRoundScore(winRoundFlag,dto,response);
//            MatchTennisReSetScoreDto matchTennisReSetScoreDto =new MatchTennisReSetScoreDto();
//            matchTennisReSetScoreDto.setCurrentSet(dto.getCurrentSet());
//            matchTennisReSetScoreDto.setThirdMatchId(dto.getThirdMatchId());
//            reCountSetScore(matchTennisReSetScoreDto,response);
//            updateRoundScore(winRoundFlag,dto,response);
        }

        syncMatchStatus(linkId, response.getData().getThirdMatchInfo(), 1, matchTimeInfo.getPeriod());

        //5.推送ws
        redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

        //3.下发滚球事件变更
        MatchEventInfoDTO eventInfoDTO = buildMatchEventInfoDTO(dto.getLinkedId(),"tennis_score_change", matchScoresInfo, matchTimeInfo);
        eventProducer.sendPDEventInfo(eventInfoDTO);

        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),response.getData().getThirdMatchInfo().getId()+"_PD");
        //6.记录日志
        //iMatchScorePdLogService.setMatchSecondScoreLog(odiMatchScoresInfo,response.getData().getMatchScoresInfo(),dto);
//        StandardMatchScores score = standardMatchScoresMapper.loadByMatchId(response.getData().getStandardMatchInfo().getId());
//        StandardMatchScores score = scoresRedisHelp.getCatchStandScoreByMatchId(response.getData().getStandardMatchInfo().getId());

//        if(null !=score && DataSourceCodeEnum.PD.code.equals(score.getDataSourceCode())){
//            Integer firstNum = dto.getCurrentSet()==null? 0: Integer.parseInt(dto.getCurrentSet().toString());
//            Integer secondNum = dto.getCurrentRound()==null? 0: Integer.parseInt(dto.getCurrentRound().toString());
//            log.info("::::setMatchSecondScore的比分下发:{}", linkId);
//            saveAndSendStandScore(firstNum,secondNum,response, score,dto,linkId);
//        }else{
//            log.info("报球板比分数据异常:{}，==== {}", linkId,score);
//        }
        //ws推送标准比分
        pushMatchStandScores(response.getData().getStandardMatchInfo().getId(),response.getData().getStandardMatchInfo().getId()+"");
        return Response.success();
    }

    public void pushMatchStandScores(Long standardMatchId,String linkId){
        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchId +"");
        StandardScoreCenter centerStand = new StandardScoreCenter();
        centerStand.setStandardMatchId(standardMatchId);
        //推送标识
        centerStand.setIndex(99);
        log.info("standardMatchScore组装推送标准比分比分中心标准比分:{}", centerStand);
        reqMessage.setData(JSONObject.toJSONString(centerStand, SerializerFeature.DisableCircularReferenceDetect));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("SCORE_CENTER_MATCH_SCORES" +":" +reqMessage.getLinkId(), builder.build());
        log.info("standardMatchScore推送比分中心标准比分:{}", linkId);

    }
    @Override
    public boolean chargeSecondScore(TennisEditSecondScoreDto tennisEditSecondScoreDto,MatchScoreAndTimeVo matchScoreAndTimeVo) {
        //获取当前局制: 1长盘制, 2抢七制,3单人抢十,4双人抢十,5特

        //current  1:网球跳分(15/30/40/50)   2:单分(1/2/3/4)
        Integer current = isSingleScore(matchScoreAndTimeVo.getMatchTimeInfo());

/*        if(!MatchScoreUtils.TENNIS_ROUND_MATCH_LENGTH.get(current).contains(tennisEditSecondScoreDto.getScoreNumber())){
            return false;
        }*/
        if(!MatchScoreUtils.TENNIS_ROUND_MATCH_LENGTH.get(current).contains(tennisEditSecondScoreDto.getT1())){
            return false;
        }
        if(!MatchScoreUtils.TENNIS_ROUND_MATCH_LENGTH.get(current).contains(tennisEditSecondScoreDto.getT2())){
            return false;
        }
        return true;
    }

    //current  1:跳分(15/30/40/50)   2:单分(1/2/3/4)
    Integer isSingleScore(MatchTimeInfo matchTimeInfo){

        //获取当前局制: 1长盘制, 2抢七制,3单人抢十,4双人抢十,5特
        Integer matchLength = matchTimeInfo.getMatchLength();

        //current  1:跳分(15/30/40/50)   2:单分(1/2/3/4)
        Integer current = 1;
        if (matchLength == 2 && matchTimeInfo.getCurrentRound().equals(13)) {
            return current =2;
        }else if (matchLength == 3 && matchTimeInfo.getCurrentRound().equals(1)) {
            return current =2;
        }else if (matchLength == 4 && matchTimeInfo.getCurrentRound().equals(13)) {
            return current =2;
        }else if (matchLength == 5 && matchTimeInfo.getCurrentRound().equals(1)) {
            return current =2;
        }

        return current;
    }

    @Override
    public void updateSecondScore(TennisEditSecondScoreDto tennisEditSecondScoreDto, Response<MatchScoreAndTimeVo> response) {
        log.info("PD网球报球版更新赛事当前局比分开始---赛事ID:{}, 主队比分:{}, 客队比分:{}",tennisEditSecondScoreDto.getStandardMatchId(),tennisEditSecondScoreDto.getT1(),tennisEditSecondScoreDto.getT2());
        //1.更新 score_json 当前局内比分
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchScoresInfo matchScoresInfoOid = new MatchScoresInfo();
        BeanUtils.copyProperties(matchScoresInfo,matchScoresInfoOid);

        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        wholeSores.getCurrentScore().setHome(tennisEditSecondScoreDto.getT1());
        wholeSores.getCurrentScore().setAway(tennisEditSecondScoreDto.getT2());
        Long period = MatchPeriodUtils.getTennisPeriodBySet(tennisEditSecondScoreDto.getCurrentSet());
        TennisScores periodScore = allPeriodScores.get(period);
        if(periodScore==null){
            periodScore=new TennisScores();
            allPeriodScores.put(period,periodScore);
        }
        periodScore.getCurrentScore().setHome(tennisEditSecondScoreDto.getT1());
        periodScore.getCurrentScore().setAway(tennisEditSecondScoreDto.getT2());
        //2.更新 extry_score_json 全部盘的全部局内比分
        TennisExtryScores tennisExtryScores;
        if (StringUtils.isEmpty(response.getData().getMatchScoresInfo().getScoresJsonExtra())) {
            tennisExtryScores = new TennisExtryScores();
        } else {
            tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJsonExtra())), TennisExtryScores.class);
        }
        tennisExtryScores.doCalculation(tennisEditSecondScoreDto);

        response.getData().getMatchScoresInfo().setPeriod(period);
        response.getData().getMatchScoresInfo().setT1( wholeSores.getMatchScore().getHome() );
        response.getData().getMatchScoresInfo().setT2( wholeSores.getMatchScore().getAway() );
        response.getData().getMatchScoresInfo().setPeriodT1( wholeSores.getSetScore().getHome() );
        response.getData().getMatchScoresInfo().setPeriodT2( wholeSores.getSetScore().getAway() );
        response.getData().getMatchScoresInfo().setScoresJsonExtra(JSONObject.toJSONString(tennisExtryScores));
        response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        response.getData().getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        //3.更新入库
//        matchScoresInfoMapper.updateByPrimaryKey( response.getData().getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(),null);
        //4.推送ws
        redisUtils.pushTenniseScore(tennisEditSecondScoreDto.getThirdMatchId());

        // 日志改为异步
        CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                iMatchScorePdLogService.updateSecondScoreLog(matchScoresInfoOid, response.getData().getMatchScoresInfo(), tennisEditSecondScoreDto) ));

        log.info("PD网球报球版更新赛事当前局比分结束---赛事ID:{}, 主队比分:{}, 客队比分:{}",tennisEditSecondScoreDto.getStandardMatchId(),tennisEditSecondScoreDto.getT1(),tennisEditSecondScoreDto.getT2());
    }

    @Override
    public void updateRoundScore(Integer winFlag,TennisEditSecondScoreDto tennisEditSecondScoreDto, Response<MatchScoreAndTimeVo> response) {
        //如果 局获胜更新则更新局比分
        log.info("PD网球报球版更新赛事当前局获胜开始---赛事ID:{}",tennisEditSecondScoreDto.getStandardMatchId());

        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchScoresInfo matchScoresInfoOid = new MatchScoresInfo();
        BeanUtils.copyProperties(matchScoresInfo,matchScoresInfoOid);

        //1.更新 score_json 当前局内比分
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        Long period= MatchPeriodUtils.getTennisPeriodBySet(tennisEditSecondScoreDto.getCurrentSet());
        TennisScores periodScore= allPeriodScores.get(period);
        if(periodScore==null){
            periodScore=new TennisScores();
            allPeriodScores.put(period,periodScore);
        }
        //主队|+1
        if(winFlag==1){
            //客队+1
            periodScore.getSetScore().setHome(periodScore.getSetScore().getHome()+1);
            wholeSores.getSetScore().setHome(wholeSores.getSetScore().getHome()+1);
        }else if(winFlag==2){
            periodScore.getSetScore().setAway(periodScore.getSetScore().getAway()+1);
            wholeSores.getSetScore().setAway(wholeSores.getSetScore().getAway()+1);
        }
        response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        response.getData().getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        //3.更新入库
//        matchScoresInfoMapper.updateByPrimaryKey( response.getData().getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(),null);

        //4.推送ws
        redisUtils.pushTenniseScore(tennisEditSecondScoreDto.getThirdMatchId());

        CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                iMatchScorePdLogService.setMatchSecondScoreLog(matchScoresInfoOid, response.getData().getMatchScoresInfo(), tennisEditSecondScoreDto) ));

        log.info("PD网球报球版更新赛事当前局获胜结束---赛事ID:{}, 主队局比分:{}, 客队局比分:{}",
                tennisEditSecondScoreDto.getStandardMatchId(), periodScore.getSetScore().getHome(), periodScore.getSetScore().getAway());
    }

    @Override
    public void updateSetScore(Integer winFlag,TennisEditSecondScoreDto tennisEditSecondScoreDto, Response<MatchScoreAndTimeVo> response) {
        //如果盘获胜更新则更新盘比分

    }

    @Override
    public Response setFirstNum(PDFirstNumSetDto pdFirstNumSetDto, Response<MatchScoreAndTimeVo> response)
    {
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();


        if(!MatchPeriodUtils.TENNIS_PERIOD_END_MAP.containsValue(response.getData().getMatchTimeInfo().getPeriod())){
            return Response.failed("当前盘未结束,不能切换到下一盘");
        }

        Long nowPeriod = MatchPeriodUtils.TENNIS_PERIOD_END_MAP.get((pdFirstNumSetDto.getFirstNum()-1));

        matchTimeInfo.setFirstNum(pdFirstNumSetDto.getFirstNum());
        matchTimeInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchTimeInfoMapper.updateByPrimaryKeySelective(matchTimeInfo);
        matchTimeInfoRepository.updateByPrimaryKey(response.getData().getMatchTimeInfo());
        //4.推送ws
        redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

        return Response.success();
    }

    @Override
    public Response changeSetStatus(PDTennisSetStatusDto pdTennisSetStatusDto, Response<MatchScoreAndTimeVo> response)
    {
        String linkId = pdTennisSetStatusDto.getLinkedId();
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        MatchTimeInfo matchTimeInfoOld = new MatchTimeInfo();
        BeanUtils.copyProperties(matchTimeInfo, matchTimeInfoOld);

        Long eventTime = System.currentTimeMillis();
        Long periodId;
        //盘开始下发盘开始事件  而且编辑 当前盘 和重置当前局
        if( pdTennisSetStatusDto.getSetStatus()==0 )
        {
            //盘开始阶段
            periodId = MatchPeriodUtils.getTennisPeriodBySet(pdTennisSetStatusDto.getCurrentSet());

            //盘比分初始化
            if( null == periodId ) {
                return  Response.failed("赛事阶段有误");
            }
            this.initSet(periodId,response.getData().getMatchScoresInfo());
        } else if( pdTennisSetStatusDto.getSetStatus()==1 ){
            //赛事ID+盘+状态为唯一动作
            String key = pdTennisSetStatusDto.getThirdMatchId()+"_"+
                    pdTennisSetStatusDto.getCurrentSet()+"_"+
                    pdTennisSetStatusDto.getSetStatus();
            if(redisService.hasKey(key)){
                log.error("::{}::changeRoundStatus 重复操作::{}",linkId,key);
                return Response.failed("重复操作,请刷新后重试:"+redisService.get(key));
            }
            //添加rediskey作为当前动作的唯一键，避免多用户同时操作当前盘+状态-5秒
            redisService.set(key,key+"_"+pdTennisSetStatusDto.getOperatorName(),5);
            //盘结束阶段
            periodId = MatchPeriodUtils.getTennisPeriodEndBySet(pdTennisSetStatusDto.getCurrentSet());
            //盘结束会对盘比分获胜计算
            Response r = this.chargeAndUpdateSetWin(pdTennisSetStatusDto,response.getData());
            if(!r.isSuccess()){
                return  r;
            }
            MatchTennisReSetScoreDto matchTennisReSetScoreDto =new MatchTennisReSetScoreDto();
            matchTennisReSetScoreDto.setThirdMatchId(pdTennisSetStatusDto.getThirdMatchId());
            matchTennisReSetScoreDto.setCurrentSet(pdTennisSetStatusDto.getCurrentSet());
            // 结束
            this.reCountSetScore(matchTennisReSetScoreDto,response);

            response.getData().getMatchTimeInfo().setCurrentSet( response.getData().getMatchTimeInfo().getCurrentSet()+1);
        } else {
            return Response.failed("盘开始或者结束传递状态参数错误");
        }
        //局刚开始都是1
        response.getData().getMatchTimeInfo().setCurrentRound(1);
        //更新当前盘和局     更新赛事和赛制时间表阶段
        response.getData().getMatchScoresInfo().setPeriod(periodId);
        response.getData().getMatchTimeInfo().setPeriod(periodId.longValue());
        response.getData().getMatchScoresInfo().setModifyTime(eventTime);
        JSONObject periodFootballScores = JSONObject.parseObject( response.getData().getMatchScoresInfo().getScoresJson() );
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
        for ( Map.Entry<Long, TennisScores> scoreEntry : allPeriodScores.entrySet() )
        {
            CommonItem currentScore = scoreEntry.getValue().getCurrentScore();
            if(currentScore!=null){
                currentScore.setHome(0);
                currentScore.setAway(0);
            }
        }
//        matchScoresInfoMapper.updateByPrimaryKey( response.getData().getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(),null);
//        matchTimeInfoMapper.updateByPrimaryKey( response.getData().getMatchTimeInfo());
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(response.getData().getMatchTimeInfo(),null);
        //盘阶段事件下发
        MatchScoreCommonVo matchScoreCommonVo = MatchEventUtils.getMatchScoreCommonVo(response.getData());
        MatchEventInfoDTO eventInfo = MatchEventUtils.createMatchStatusEvent(response.getData(),0l,0l,eventTime,matchScoreCommonVo,periodId,pdTennisSetStatusDto.getLinkedId(),pdTennisSetStatusDto.getOperatorName());

        //2.更新阶段
        eventInfo.setEventCode("match_status");
        eventInfo.setFirstNum(pdTennisSetStatusDto.getCurrentSet());
        eventInfo.setSecondT1(0);
        eventInfo.setSecondT2(0);
        TennisScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        eventInfo.setT1(wholeSores.getMatchScore().getHome());
        eventInfo.setT2(wholeSores.getMatchScore().getAway());
        if( pdTennisSetStatusDto.getSetStatus()==0 )
        {
            eventInfo.setFirstT1(0);
            eventInfo.setFirstT2(0);
        }else if( pdTennisSetStatusDto.getSetStatus()==1){
            TennisScores periodScore = allPeriodScores.get(periodId);
            if(periodScore!=null){
                eventInfo.setFirstT1(periodScore.getSetScore().getHome());
                eventInfo.setFirstT2(periodScore.getSetScore().getAway());
            }
        }

        commonAdvertiseService.updateMatchStatus(response.getData(),eventInfo);

        // 推零
        scoresProducer.sendScore(pdTennisSetStatusDto.getLinkedId(), response.getData().getThirdMatchInfo(),
                response.getData().getMatchScoresInfo(), false);
        // 防止重复消费
//        Long ThirdMatchId = response.getData().getThirdMatchInfo().getId();
//        if ( null != ThirdMatchId && ThirdMatchId > 0)
//        {
//            String key = Constant.IGNORE_PRE + ThirdMatchId;
//            log.info(":{}::三方事件防重提交key:{}", linkId, key);
//            redisService.set( key, ThirdMatchId.toString(), 2L);
//        }

        // 赛事状态变更事件
        eventProducer.sendPDEventInfo(eventInfo);
        log.info("::{}::changeSetStatus赛事状态事件的下发:{}", linkId, JSON.toJSONString(eventInfo));

        //4.推送ws
        redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

        CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                iMatchScorePdLogService.changeSetStatusLog(matchTimeInfoOld, response.getData().getMatchTimeInfo(), pdTennisSetStatusDto) ));
//        StandardMatchScores score = standardMatchScoresMapper.loadByMatchId(response.getData().getStandardMatchInfo().getId());
//        StandardMatchScores score = scoresRedisHelp.getCatchStandScoreByMatchId(response.getData().getStandardMatchInfo().getId());

//        if(null !=score && DataSourceCodeEnum.PD.code.equals(score.getDataSourceCode())){
            log.info("changeSetStatus赛事状态事件的下发比分:{}", linkId);
//            saveAndSendStandScore(0, pdTennisSetStatusDto.getCurrentSet(),response,  score,null,linkId);
//        }else{
//            log.info("报球板比分数据异常:{}，==== {}", linkId,score);
//        }
        //ws推送标准比分
        pushMatchStandScores(response.getData().getStandardMatchInfo().getId(),response.getData().getStandardMatchInfo().getId()+"");
        return Response.success();
    }

    @Override
    public Response changeRoundStatus(PDTennisRoundStatusDto pdTennisRoundStatusDto, Response<MatchScoreAndTimeVo> response) {
        String linkId = pdTennisRoundStatusDto.getLinkedId();
        // 局结束会对局比分获胜计算 *暂时可由上面自动跳分 第一局开打=第一盘开打
        if( pdTennisRoundStatusDto.getCurrentRound() == 1 && pdTennisRoundStatusDto.getRoundStatus() == 0 ){
            PDTennisSetStatusDto pdTennisSetStatusDto =new PDTennisSetStatusDto();
            BeanUtils.copyProperties(pdTennisRoundStatusDto, pdTennisSetStatusDto);
            pdTennisSetStatusDto.setCurrentSet(pdTennisRoundStatusDto.getCurrentSet());
            pdTennisSetStatusDto.setSetStatus(pdTennisRoundStatusDto.getRoundStatus());
            return this.changeSetStatus(pdTennisSetStatusDto, response);
        }

        //局开始会下发局开始事件   而且编辑 当前局
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        MatchTimeInfo matchTimeInfoOid = new MatchTimeInfo();
        BeanUtils.copyProperties(matchTimeInfo,matchTimeInfoOid);
        //局结束会下发局结束事件 而且会编辑当前局+1
        Long eventTime =System.currentTimeMillis();
        Long periodId;

        //盘开始下发盘开始事件  而且编辑 当前盘 和重置当前局
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        Integer t1 = wholeSores.getCurrentScore().getHome();
        Integer t2 = wholeSores.getCurrentScore().getAway();

        log.info("::{}::tennis-changeRoundStatus-->入参:{}", linkId, JSONObject.toJSONString(pdTennisRoundStatusDto) );
        if(pdTennisRoundStatusDto.getRoundStatus()==0){
            //局开始阶段
            periodId = MatchPeriodUtils.getTennisPeriodByRoundStatus(pdTennisRoundStatusDto);
            //本局比分初始化
            TennisExtryScores tennisExtryScores = null;
            if (StringUtils.isEmpty(response.getData().getMatchScoresInfo().getScoresJsonExtra())) {
                tennisExtryScores = new TennisExtryScores();
            } else {
                tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJsonExtra())), TennisExtryScores.class);
            }
            Map<Integer, CommonItem> map = tennisExtryScores.getCurrentScoresMap().get(pdTennisRoundStatusDto.getCurrentSet());
            if(map==null){
                map = new HashMap<>();
                tennisExtryScores.getCurrentScoresMap().put(pdTennisRoundStatusDto.getCurrentSet().intValue(), map);
            }
            CommonItem commonItem = map.get(pdTennisRoundStatusDto.getCurrentRound());
            if( commonItem == null ){
                commonItem = new CommonItem();
                map.put(pdTennisRoundStatusDto.getCurrentRound(),commonItem);
            }
            response.getData().getMatchScoresInfo().setScoresJsonExtra(JSONObject.toJSONString(tennisExtryScores));

            log.info("::{}::tennis-changeRoundStatus-->allPeriodScores-size:{}, periodId:{}, executeStatus:{}",
                    linkId, allPeriodScores.values().size(), periodId, !SportPeriodConstant.TennisPeriod.contans(periodId) );
//            if( !SportPeriodConstant.TennisPeriod.contans(periodId) ){
            for (TennisScores value : allPeriodScores.values()) {
                value.getCurrentScore().setHome(0);
                value.getCurrentScore().setAway(0);
            }
//            }
            log.info("{} 开始阶段设置全局局内比分为0-0",linkId);
            //开始阶段设置全局局内比分为0-0
            allPeriodScores.get(WHOLE_MATCH).setCurrentScore(new CommonItem(0,0));
            log.info("开始阶段设置全局局内比分：{}",allPeriodScores);
            response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        } else if( pdTennisRoundStatusDto.getRoundStatus()==1 ) {
            //赛事ID+盘+局+状态为唯一动作
            String key = pdTennisRoundStatusDto.getThirdMatchId()+"_"+
                    pdTennisRoundStatusDto.getCurrentSet()+"_"+
                    pdTennisRoundStatusDto.getCurrentRound()+"_"+
                    pdTennisRoundStatusDto.getRoundStatus();
            if(redisService.hasKey(key)){
                log.error("::{}::changeRoundStatus 重复操作::{}",pdTennisRoundStatusDto.getLinkedId(),key);
                return Response.failed("重复操作,请刷新后重试:"+redisService.get(key));
            }
            // 添加redisKey作为当前动作的唯一键，避免多用户同时操作当前盘+局+状态-5秒
            redisService.set(key,key+"_"+pdTennisRoundStatusDto.getOperatorName(),5);
            //局结束阶段
            if(pdTennisRoundStatusDto.getT1().equals(pdTennisRoundStatusDto.getT2())){
                return Response.failed("比分错误无法结束当前局");
            }
            Integer winFlag = pdTennisRoundStatusDto.getT1() > pdTennisRoundStatusDto.getT2() ? 1 : 2 ;
            TennisEditSecondScoreDto tennisEditSecondScoreDto = new TennisEditSecondScoreDto();
            BeanUtils.copyProperties(pdTennisRoundStatusDto, tennisEditSecondScoreDto);

            this.updateRoundScore(winFlag, tennisEditSecondScoreDto, response);

            periodId = MatchPeriodUtils.getTennisPeriodByRoundStatus(pdTennisRoundStatusDto);
            //盘结束会对盘比分获胜计算
            response.getData().getMatchTimeInfo().setCurrentRound(response.getData().getMatchTimeInfo().getCurrentRound()+1);
        }
        else
        {
            return Response.failed("盘开始或者结束传递状态参数错误");
        }

        //更新当前盘和局     更新赛事和赛制时间表阶段
        response.getData().getMatchScoresInfo().setModifyTime(eventTime);
        response.getData().getMatchTimeInfo().setCurrentSet(pdTennisRoundStatusDto.getCurrentSet());

        pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(),null);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(response.getData().getMatchTimeInfo(),null);

        //盘阶段事件下发
        if( null != periodId )
        {
             periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
             allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
            // 保存的记录比分与下发展示的比分不相同
            MatchScoreCommonVo matchScoreCommonVo = MatchEventUtils.getMatchScoreCommonVo(response.getData());
            MatchEventInfoDTO eventInfo = MatchEventUtils.createMatchStatusEvent(response.getData(),0L,0L,
                    eventTime, matchScoreCommonVo, periodId, linkId, pdTennisRoundStatusDto.getOperatorName());
            //1.设置局数
            eventInfo.setFirstNum(pdTennisRoundStatusDto.getCurrentSet());
            eventInfo.setSecondNum(pdTennisRoundStatusDto.getCurrentRound());
            eventInfo.setSecondT1(pdTennisRoundStatusDto.getT1() == null ? 0 : pdTennisRoundStatusDto.getT1());
            eventInfo.setSecondT2(pdTennisRoundStatusDto.getT2() == null ? 0 : pdTennisRoundStatusDto.getT2());
            eventInfo.setEventCode("match_status");
            eventInfo.setT1(wholeSores.getMatchScore().getHome());
            eventInfo.setT2(wholeSores.getMatchScore().getAway());
            // 得到当前盘
            Integer currentSet = response.getData().getMatchTimeInfo().getCurrentSet();
            Long currentPeriod = MatchPeriodUtils.getTennisPeriodBySet(currentSet);
            if( null != currentPeriod )
            {
                TennisScores currentPeriodScore = allPeriodScores.get(currentPeriod);
                if( null != currentPeriodScore )
                {
                    eventInfo.setFirstT1(currentPeriodScore.getSetScore().getHome());
                    eventInfo.setFirstT2(currentPeriodScore.getSetScore().getAway());
                }
            }

            //2.记录比分
            log.info("::{}::tennis-changeRoundStatus-->比分:{}, 事件:{}",
                    linkId, JSONObject.toJSONString( response.getData().getMatchScoresInfo()), JSONObject.toJSONString(eventInfo));
            commonAdvertiseService.updateMatchStatus( response.getData(), eventInfo);

            eventProducer.sendPDEventInfo(eventInfo);

            // 推零
            scoresProducer.sendScore( linkId, response.getData().getThirdMatchInfo(), response.getData().getMatchScoresInfo(), false);

            // 推送ws
            redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

//            StandardMatchScores score = scoresRedisHelp.getCatchStandScoreByMatchId(response.getData().getStandardMatchInfo().getId());

            log.info("::{}::tennis-changeRoundStatus-->的比分下发", linkId);
//            saveAndSendStandScore(pdTennisRoundStatusDto.getRoundStatus(), pdTennisRoundStatusDto.getCurrentRound(),response, score,null, linkId);

            //ws推送标准比分
            pushMatchStandScores(response.getData().getStandardMatchInfo().getId(),response.getData().getStandardMatchInfo().getId()+"");

            // 日志改为异步
            CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                    iMatchScorePdLogService.changeRoundStatusLog(matchTimeInfoOid, response.getData().getMatchTimeInfo(), pdTennisRoundStatusDto) ));
        }
        return Response.success();
    }

    /**
     * 保存并推送标准比分
     * @param set
     * @param tg
     * @param response
     * @param score
     */
    private void saveAndSendStandScore(Integer tg,Integer set, Response<MatchScoreAndTimeVo> response, StandardMatchScores score,TennisEditSecondScoreDto dto,String linkId) {
        if(response.getData().getMatchScoresInfo()==null || response.getData().getMatchScoresInfo().getScoresJson()==null ){
            log.info("比分异常：{}",linkId);
            return;
        }
        log.info("saveAndSendStandScore:{}",score);
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(score.getMatchId());
        if(standardSportMarketSell==null){
            log.error("网球报球板标准比分异常：{},无开售数据",linkId);
        }
        if(!DataSourceCodeEnum.PD.code.equals(standardSportMarketSell.getBusinessEvent())){
            log.error("网球报球板标准比分异常：{},主事件源：{}",linkId,standardSportMarketSell.getBusinessEvent());
            return;
        }
        score.setDataSourceCode(DataSourceCodeEnum.PD.code);

        Long perId = response.getData().getMatchScoresInfo().getPeriod();
        String scoresJson = response.getData().getMatchScoresInfo().getScoresJson();

        JSONObject newScore = JSONObject.parseObject(scoresJson);
        Map<Long, TennisScores> newPeriodScores = JsonMapUtils.parseTennisMap(newScore);


        JSONObject oldScore = new JSONObject();
        Map<Long, TennisScores> oldPeriodScores = new HashMap<>();
        if(score.getScoreJson()!=null){
            oldScore = JSONObject.parseObject(score.getScoreJson());
            oldPeriodScores = JsonMapUtils.parseTennisMap(oldScore);
        }

        log.info("::{}::报球板比分更新：更新当前阶段比分:",linkId);

        //获取旧比分当前阶段数据
        if(setPeriod.contains(perId)){
            perId = perId /100;
        }else{
            perId = getPeroid(perId);
        }
        if(perId==null){
            perId = response.getData().getMatchScoresInfo().getPeriod();
        }
        log.info("报球板比分更新：更新当前阶段比分::获取阶段{},{}",perId,linkId);
        TennisScores newSc = newPeriodScores.get(perId);
        //赛盘
        int home = 0;
        int away = 0;
        //总局数
        int tgHome = 0;
        int tgAway = 0;
        if(newSc!=null){
            TennisScores whos = newPeriodScores.get(WHOLE_MATCH);
            if(StringUtils.isNotEmpty(score.getScoreJson())){
                //获取旧比分当前阶段数据
                TennisScores sc = oldPeriodScores.get(perId);
                if(sc==null){
                    log.info("报球板比分更新：更新当前阶段比分:原阶段比分为空：{},{}",perId,linkId);
                    sc = new TennisScores();
                }
                //覆盖当前阶段比分
                CommonItem setScore = new CommonItem();
                setScore.setHome(newSc.getSetScore().getHome());
                setScore.setAway(newSc.getSetScore().getAway());
                sc.setSetScore(setScore);
                CommonItem matchScore = new CommonItem();
                matchScore.setHome(newSc.getMatchScore().getHome());
                matchScore.setAway(newSc.getMatchScore().getAway());
                sc.setMatchScore(matchScore);

                CommonItem currentScore = new CommonItem();
                currentScore.setHome(whos.getCurrentScore().getHome());
                currentScore.setAway(whos.getCurrentScore().getAway());
                if(dto!=null){
                    currentScore.setHome(dto.getT1());
                    currentScore.setAway(dto.getT2());
                }
                sc.setCurrentScore(currentScore);
                if(currentScore.getHome()==0 && currentScore.getAway()==0){
                    currentScore.setHome(newSc.getCurrentScore().getHome());
                    currentScore.setAway(newSc.getCurrentScore().getAway());
                }
                oldPeriodScores.get(WHOLE_MATCH).setCurrentScore(currentScore);
                if(dto!=null){
                    /**抢七规则判断*/
                    CommonItem qiangScore = new CommonItem();
                    if(set>12&&
                            ((dto.getT1()>0&&dto.getT1()<=10)||(dto.getT2()>0&&dto.getT2()<=10))){
                        qiangScore.setHome(dto.getT1());
                        qiangScore.setAway(dto.getT2());
                        sc.setQiangScore(qiangScore);
                    }
                }
                oldPeriodScores.put(perId,sc);
                //保存
                scoresJson = JSONUtil.toJsonStr(oldPeriodScores);
            }
        }else{
            scoresJson = score.getScoreJson();
        }
        //计算标准比分
        scoresJson = calcStandScores(scoresJson,perId);

        score.setScoreJson(scoresJson);
        score.setUpdateTime(System.currentTimeMillis());
        log.info("::{}::报球板比分更新：保存比分:{}",linkId,scoresJson);
//        standardMatchScoresMapper.update(score);
        scoresRedisHelp.saveCatchStandScore(score);

        //MQ推送标准比分
        MatchEventInfo data = new MatchEventInfo();
        data.setMatchPeriodId(response.getData().getMatchScoresInfo().getPeriod());
        data.setSportId(SportTypeEnum.TENNIS.getValue());
        data.setLinkId(linkId);
        data.setSecondsFromStart(response.getData().getMatchTimeInfo().getSecondFromStart());
        data.setSecondNum(set);
        data.setFirstNum(tg);
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildStandardMatchScoreCommonScoresDto(score, data,response.getData().getMatchScoresInfo());
        scoresProducer.sendStandardMatchScores(commonScoresDto);

    }

    private String calcStandScores(String scoresJson, Long perId) {
        log.info("计算-1比分:：：：：：：：：：");
        JSONObject periodScores = JSONObject.parseObject(scoresJson);
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodScores);
        TennisScores whosScore = new TennisScores();

//        allPeriodScores.get(WHOLE_MATCH);
        //比分内容
//        List<StandardScoreDTO> listScore = new ArrayList<>();
        Integer tgHome = 0;
        Integer tgAway = 0;
        Integer setHome = 0;
        Integer setAway = 0;
        int winScore = 6;
        CommonItem currentScore = new CommonItem();
        for (Long periodId : allPeriodScores.keySet()) {
            List<Long> scoreCenterPeriod = Arrays.asList(8L, 9L, 10L, 11L, 12L,441L,442L);
            //查询比分时过滤阶段0 -- 脏数据
            if(!scoreCenterPeriod.contains(periodId)){
                continue;
            }

            TennisScores cc = allPeriodScores.get(periodId);
            if(cc==null){
                continue;
            }
            if(perId == periodId){
                currentScore = cc.getCurrentScore();
            }
            tgHome+=cc.getSetScore().getHome();
            tgAway+=cc.getSetScore().getAway();

            if(cc.getSetScore().getHome()>= winScore+1 || cc.getSetScore().getAway() >= winScore+1 ){
                if(cc.getSetScore().getHome()>cc.getSetScore().getAway() ){
                    setHome = setHome +1;
                }else{
                    setAway = setAway +1;
                }
            }else{
                if(cc.getSetScore().getHome()>cc.getSetScore().getAway()){
                    if(cc.getSetScore().getHome()>=winScore && cc.getSetScore().getHome() - cc.getSetScore().getAway() >= 2){
                        setHome = setHome +1;
                    }
                }else if (cc.getSetScore().getHome()<cc.getSetScore().getAway()){
                    if(cc.getSetScore().getAway()>=winScore && cc.getSetScore().getAway() - cc.getSetScore().getHome() >= 2){
                        setAway = setAway +1;
                    }
                }
            }
        }
        whosScore.getMatchScore().setHome(setHome);
        whosScore.getMatchScore().setAway(setAway);
        whosScore.getSetScore().setHome(tgHome);
        whosScore.getSetScore().setAway(tgAway);
        whosScore.setCurrentScore(currentScore);
        allPeriodScores.put(WHOLE_MATCH,whosScore);
        log.info("计算-1比分结束:：：：：：：：：：");
        return JSONUtil.toJsonStr(allPeriodScores);
    }

    /**
     * 网球标准比分目前只保存局比分，其他阶段暂时忽略
     * @param perId
     * @return
     */
    private Long getPeroid(Long perId) {
        Map<Long, Long> map = new HashMap<>();
        map.put(301L,8L);
        map.put(302L,9L);
        map.put(303L,10L);
        map.put(304L,11L);
        map.put(305L,12L);
        map.put(8L,8L);
        map.put(9L,9L);
        map.put(10L,10L);
        map.put(11L,11L);
        map.put(12L,12L);
        return map.get(perId)!=null?map.get(perId):perId;
    }

//    public static void main(String[] args) {
//        String scoresJson = "{-1:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":2},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":4,\"home\":9}},8:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":2},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":1}},9:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":3},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":3,\"home\":4}},10:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":1,\"home\":4}}}\t";
//        Long periodId = 10L;
//        JSONObject newScore = JSONObject.parseObject(scoresJson);
//        Map<Long, TennisScores> newPeriodScores = JsonMapUtils.parseTennisMap(newScore);
//        //获取旧比分当前阶段数据
//        TennisScores newSc = newPeriodScores.get(periodId);
//        //获取到当前阶段比分
//        log.info("报球板比分更新：获取当前阶段比分:{}",newSc.getSetScore().toString());
//        Integer home = newSc.getSetScore().getHome();
//        Integer away = newSc.getSetScore().getAway();
//
//        String oldJson = "{-1:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":30,\"home\":15},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":2},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":3,\"home\":9}},8:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":2},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":1}},9:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":0,\"home\":0},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":3},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":3,\"home\":4}},10:{\"breakPointCount\":{\"away\":0,\"home\":0},\"breakSuccessCount\":{\"away\":0,\"home\":0},\"breakSuccessRate\":{\"away\":0,\"home\":0},\"currentScore\":{\"away\":30,\"home\":15},\"doubleFoolScore\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":0,\"home\":0},\"qiangScore\":{\"away\":0,\"home\":0},\"scoreNumber\":{\"away\":0,\"home\":0},\"servesFaultCount\":{\"away\":0,\"home\":0},\"servesScoredCount\":{\"away\":0,\"home\":0},\"setScore\":{\"away\":0,\"home\":4}}}\t";
//        JSONObject oldScore = JSONObject.parseObject(oldJson);
//        Map<Long, TennisScores> oldPeriodScores = JsonMapUtils.parseTennisMap(oldScore);
//        System.out.println(JSONUtil.toJsonStr(oldPeriodScores));
//        //获取旧比分当前阶段数据
//        TennisScores sc = oldPeriodScores.get(periodId);
//        if(sc==null){
//            sc = new TennisScores();
//        }
//        //覆盖当前阶段比分
//        CommonItem ci = new CommonItem();
//        ci.setHome(home);
//        ci.setAway(away);
//        sc.setSetScore(ci);
//        //植入旧比分
//        oldPeriodScores.put(periodId,sc);
//        //保存
//        scoresJson = JSONUtil.toJsonStr(oldPeriodScores);
//        System.out.println(scoresJson);
//    }
    @Override
    public Response searchOperatorDetail(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {
        StandardMatchInfoExample example =new StandardMatchInfoExample();
        example.createCriteria().andThirdMatchIdEqualTo(matchAdvertiseQueryDto.getThirdMatchId());
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        if (standardMatchInfos.size() == 0) {
            return Response.failed();
        }
        MatchScoresPdLogExample exampleLog =new MatchScoresPdLogExample();
        exampleLog.createCriteria()
                .andOperateMatchIdEqualTo(standardMatchInfos.get(0).getMatchManageId());

        List<MatchScoresPdLog> matchScoresPdLogs = matchScoresPdLogMapper.selectByExample(exampleLog);

        return Response.success(matchScoresPdLogs);
    }

    @Override
    public Response setMaxRound(MatchTennisEditMaxRoundDto dto, Response<MatchScoreAndTimeVo> response ) {
        if(StringUtils.isEmpty(response.getData().getMatchTimeInfo().getPeriodLengthJson())){
            return Response.failed("数据错误");
        }
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        MatchTimeInfo matchTimeInfoOid = new MatchTimeInfo();
        BeanUtils.copyProperties(matchTimeInfo,matchTimeInfoOid);

        JSONObject jsonObject =JSONObject.parseObject(response.getData().getMatchTimeInfo().getPeriodLengthJson());
        jsonObject.put(dto.getCurrentSet().toString(),dto.getMaxRound());
        response.getData().getMatchTimeInfo().setPeriodLengthJson(jsonObject.toJSONString());
        response.getData().getMatchTimeInfo().setModifyTime(System.currentTimeMillis());
//        matchTimeInfoMapper.updateByPrimaryKey( response.getData().getMatchTimeInfo());
//        pdMatchInfoRepository.setRedisMatchTimeInfo(response.getData().getMatchTimeInfo(),null);
        matchTimeInfoRepository.updateByPrimaryKey(response.getData().getMatchTimeInfo());
        //4.推送ws
        redisUtils.pushTenniseScore(matchTimeInfo.getThirdMatchId());

        iMatchScorePdLogService.setMaxRoundLog(matchTimeInfoOid,
                response.getData().getMatchTimeInfo(),dto,response.getData().getStandardMatchInfo().getId());

        return Response.success();
    }

    Response upScores(MatchScoresInfo matchScoresInfo, MatchTennisEditMaxRoundDto dto)
    {
        if (matchScoresInfo == null)
        {
            return Response.failed("比分数据为空!");
        }

       String scoresJsonExtra = matchScoresInfo.getScoresJsonExtra();
       if (scoresJsonExtra == null)
       {
           return Response.failed("局内比分没有初始化!");
       }
       TennisExtryScores tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(scoresJsonExtra)), TennisExtryScores.class);
       Map<Integer, Map<Integer, CommonItem>> currentScoresMap = tennisExtryScores.getCurrentScoresMap();

       Map<Integer, CommonItem> integerCommonItemMap = currentScoresMap.get(dto.getCurrentSet());
       Map<Integer, CommonItem> map = Maps.newTreeMap();

       for ( int i = 1; i <= dto.getMaxRound(); i++ )
       {
           CommonItem commonItem = integerCommonItemMap.get(i);
           if (commonItem == null)
           {
               CommonItem addCommonItem = new CommonItem();
               addCommonItem.setHome(0);
               addCommonItem.setAway(0);
               map.put( i, addCommonItem );
           }
           else
           {
               map.put(i,integerCommonItemMap.get(i));
           }
       }

       currentScoresMap.put(dto.getCurrentSet(),map);
       tennisExtryScores.setCurrentScoresMap(currentScoresMap);
       matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(tennisExtryScores));
//       matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo,null);

        return Response.success();
    }




    /**
     * 直接编辑盘比分
     * */
    @Override
    public Response setSetScore(MatchTennisEditSetScoreDto matchAdvertiseQueryDto, Response<MatchScoreAndTimeVo> response)
    {
        log.info("setSetScore ：{}",matchAdvertiseQueryDto);

        try {
            MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
            MatchScoresInfo matchScoresInfoOid = new MatchScoresInfo();
            BeanUtils.copyProperties(matchScoresInfo,matchScoresInfoOid);

            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
            TennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            Long  period =MatchPeriodUtils.getTennisPeriodBySet(matchAdvertiseQueryDto.getCurrentSet());
            TennisScores periodSores= allPeriodScores.get(period);
            Integer at1 = matchAdvertiseQueryDto.getT1()-periodSores.getSetScore().getHome();
            Integer at2 = matchAdvertiseQueryDto.getT2()-periodSores.getSetScore().getAway();
            wholeSores.getSetScore().setHome(wholeSores.getSetScore().getHome()+at1);
            wholeSores.getSetScore().setAway(wholeSores.getSetScore().getAway()+at2);
            //先把盘比分还原
            if(periodSores.getSetScore().getHome()>periodSores.getSetScore().getAway()){
                if(wholeSores.getMatchScore().getHome()-1<0){
                    wholeSores.getMatchScore().setHome(0);
                }else {
                    wholeSores.getMatchScore().setHome(wholeSores.getMatchScore().getHome()-1);
                }
            }else {
                if(wholeSores.getMatchScore().getAway()-1<0){
                    wholeSores.getMatchScore().setAway(0);
                }else {
                    wholeSores.getMatchScore().setAway(wholeSores.getMatchScore().getAway() - 1);
                }
            }
            periodSores.getSetScore().setHome(matchAdvertiseQueryDto.getT1());
            periodSores.getSetScore().setAway(matchAdvertiseQueryDto.getT2());

            if(periodSores.getSetScore().getHome()>periodSores.getSetScore().getAway()){
                wholeSores.getMatchScore().setHome(wholeSores.getMatchScore().getHome()+1);
            }else {
                wholeSores.getMatchScore().setAway(wholeSores.getMatchScore().getAway()+1);
            }
            // 下游使用比分的变更
            matchScoresInfo.setT1( wholeSores.getMatchScore().getHome() );
            matchScoresInfo.setT2( wholeSores.getMatchScore().getAway() );
            matchScoresInfo.setPeriodT1( wholeSores.getSetScore().getHome() );
            matchScoresInfo.setPeriodT2( wholeSores.getSetScore().getAway() );
            matchScoresInfo.setScoresJson( JSONObject.toJSONString(allPeriodScores));
//            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(),null);
            //3.下发滚球事件变更
//            MatchEventInfoDTO eventInfoDTO = buildMatchEventInfoDTO(matchAdvertiseQueryDto.getLinkedId(),
//                    "tennis_score_change", matchScoresInfo, response.getData().getMatchTimeInfo());
//            eventInfoDTO.setSecondT1(0);
//            eventInfoDTO.setSecondT2(0);
//            eventProducer.sendPDEventInfo(eventInfoDTO);

            scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),response.getData().getThirdMatchInfo().getId()+"_PD");
            //4.推送ws
            redisUtils.pushTenniseScore(matchScoresInfo.getThirdMatchId());
//            StandardMatchScores score = scoresRedisHelp.getCatchStandScoreByMatchId(response.getData().getStandardMatchInfo().getId());

//            StandardMatchScores score = standardMatchScoresMapper.loadByMatchId(response.getData().getStandardMatchInfo().getId());
//            if(null !=score && DataSourceCodeEnum.PD.code.equals(score.getDataSourceCode())){
                log.info("setSetScore的比分下发：{}",matchAdvertiseQueryDto.getLinkedId());
//                saveAndSendStandScore(matchAdvertiseQueryDto.getCurrentSet(),0, response, score,null,matchAdvertiseQueryDto.getLinkedId());
//            }else{
//                log.info("报球板比分数据异常:{}，==== {}", matchAdvertiseQueryDto.getLinkedId(),score);
//            }
            //ws推送标准比分
            pushMatchStandScores(response.getData().getStandardMatchInfo().getId(),response.getData().getStandardMatchInfo().getId()+"");
            //增加操作日志
            iMatchScorePdLogService.setSetScoreLog(matchScoresInfoOid, matchScoresInfo,
                    matchAdvertiseQueryDto,response.getData().getStandardMatchInfo().getId());

        } catch (Exception e) {
            log.error("setSetScore ::::",e);
        }
        return Response.success();
    }

    /**
     * 重新计算盘比分
     * */
    @Override
    public Response reCountSetScore(MatchTennisReSetScoreDto tennisReSetScoreDto, Response<MatchScoreAndTimeVo> response)
    {
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchScoresInfo matchScoresInfoOid = new MatchScoresInfo();
        BeanUtils.copyProperties(matchScoresInfo,matchScoresInfoOid);

        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null||StringUtils.isEmpty(response.getData().getMatchScoresInfo().getScoresJsonExtra())){
            return Response.failed("暂无比分");
        }
        //阶段 盘比分
        Long period =MatchPeriodUtils.getTennisPeriodBySet(tennisReSetScoreDto.getCurrentSet());
        TennisScores periodSores= allPeriodScores.get(period);
        if(periodSores==null){
            return Response.failed("暂无比分");
        }
        TennisExtryScores tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJsonExtra())), TennisExtryScores.class);
        Map<Integer,CommonItem> map= tennisExtryScores.getCurrentScoresMap().get(tennisReSetScoreDto.getCurrentSet());
        if(map==null){
            return Response.failed("暂无比分");
        }
        Integer homeSet =0;
        Integer awaySet =0;
        for (Map.Entry<Integer, CommonItem> entry : map.entrySet()) {
            if(entry.getValue().getHome()!=null){
                if(entry.getValue().getHome()>entry.getValue().getAway()){
                    homeSet++;
                }else if(entry.getValue().getHome()<entry.getValue().getAway()){
                    awaySet++;
                }
            }
        }
        periodSores.getSetScore().setHome(homeSet);
        periodSores.getSetScore().setAway(awaySet);
        //重新计算总盘比分
        Integer T1=0;
        Integer T2=0;
        Integer setT1=0;
        Integer setT2=0;
        for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet()) {
            if(WHOLE_MATCH.equals(entry.getKey())){
                continue;
            }
            setT1+=entry.getValue().getSetScore().getHome();
            setT2+=entry.getValue().getSetScore().getAway();
            if(entry.getValue().getSetScore().getHome()>entry.getValue().getSetScore().getAway()){
                T1++;
            }else if(entry.getValue().getSetScore().getHome()<entry.getValue().getSetScore().getAway()){
                T2++;
            }
        }
        wholeSores.getMatchScore().setHome(T1);
        wholeSores.getMatchScore().setAway(T2);
        wholeSores.getSetScore().setHome(setT1);
        wholeSores.getSetScore().setAway(setT2);
        response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        response.getData().getMatchScoresInfo().setT1(T1);
        response.getData().getMatchScoresInfo().setT2(T2);
        response.getData().getMatchScoresInfo().setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());

        response.getData().getMatchScoresInfo().setPeriodT1( wholeSores.getSetScore().getHome() );
        response.getData().getMatchScoresInfo().setPeriodT2( wholeSores.getSetScore().getAway() );
//        matchScoresInfoMapper.updateByPrimaryKey(  response.getData().getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(),null);
        scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),response.getData().getThirdMatchInfo().getId()+"_PD");
        //增加操作日志
        CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                iMatchScorePdLogService.reCountSetScoreLog(matchScoresInfoOid, response.getData().getMatchScoresInfo(),
                        tennisReSetScoreDto,response.getData().getStandardMatchInfo().getId()) ));
//        StandardMatchScores score = standardMatchScoresMapper.loadByMatchId(response.getData().getStandardMatchInfo().getId());
//        StandardMatchScores score = scoresRedisHelp.getCatchStandScoreByMatchId(response.getData().getStandardMatchInfo().getId());
//        if(null !=score && DataSourceCodeEnum.PD.code.equals(score.getDataSourceCode())){
            log.info("::::reCountSetScore的比分下发:{}", tennisReSetScoreDto.getLinkedId());
//            saveAndSendStandScore(tennisReSetScoreDto.getCurrentSet(),tennisReSetScoreDto.getCurrentSet(),response,  score,null, tennisReSetScoreDto.getLinkedId());
//        }else{
//            log.info("报球板比分数据异常:{}，==== {}", tennisReSetScoreDto.getLinkedId(),score);
//        }
        //ws推送标准比分
        pushMatchStandScores(response.getData().getStandardMatchInfo().getId(),response.getData().getStandardMatchInfo().getId()+"");
        return Response.success();
    }

    private Response<Object> chargeAndUpdateSetWin(PDTennisSetStatusDto pdTennisSetStatusDto, MatchScoreAndTimeVo data) {
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);

        //得到当前盘的局比分
        //Long period =MatchPeriodUtils.getTennisPeriodEndBySet(pdTennisSetStatusDto.getCurrentSet());
        Long period =MatchPeriodUtils.getTennisPeriodBySet(pdTennisSetStatusDto.getCurrentSet());
        if(period==null){
            return Response.failed("当前盘不存在");
        }
        TennisScores periodSores= allPeriodScores.get(period);
        if(periodSores.getSetScore().getHome()>periodSores.getSetScore().getAway()){
            wholeSores.getMatchScore().setHome(wholeSores.getMatchScore().getHome()+1);
            periodSores.getMatchScore().setHome(wholeSores.getMatchScore().getHome()+1);
        } else if(periodSores.getSetScore().getHome()<periodSores.getSetScore().getAway()){
            wholeSores.getMatchScore().setAway(wholeSores.getMatchScore().getAway()+1);
            periodSores.getMatchScore().setAway(wholeSores.getMatchScore().getAway()+1);
        }else {
            return Response.failed("当前还是平局,不能结束") ;
        }
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        //3.更新入库
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(),null);
        return Response.success();
    }


    //盘开始初始化 和第一局比分
    MatchScoresInfo initSet(Long currentSet,MatchScoresInfo matchScoresInfo){

        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores= null;
        if (periodFootballScores == null || "".equals(periodFootballScores)) {
            allPeriodScores =new HashMap<>();
        }else{
            allPeriodScores= JsonMapUtils.parseTennisMap(periodFootballScores);
        }

        TennisScores tennisScores = new TennisScores();
        tennisScores.setBreakPointCount(new CommonItem());
        tennisScores.setBreakSuccessCount(new CommonItem());
        tennisScores.setBreakSuccessRate(new CommonItem());
        tennisScores.setCurrentScore(new CommonItem());
        tennisScores.setDoubleFoolScore(new CommonItem());
        tennisScores.setQiangScore(new CommonItem());
        tennisScores.setScoreNumber(new CommonItem());
        tennisScores.setServesFaultCount(new CommonItem());
        tennisScores.setSetScore(new CommonItem());
        tennisScores.setServesScoredCount(new CommonItem());
        tennisScores.setMatchScore(new CommonItem());
        allPeriodScores.put(currentSet,tennisScores);

        TennisExtryScores tennisExtryScores;
        if (StringUtils.isEmpty(matchScoresInfo.getScoresJsonExtra())) {
            tennisExtryScores = new TennisExtryScores();
        } else {
            tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra())), TennisExtryScores.class);
        }
        Map<Integer,CommonItem> map = tennisExtryScores.getCurrentScoresMap().get(currentSet);
        if(map==null){
            map =new HashMap<>();
            tennisExtryScores.getCurrentScoresMap().put(currentSet.intValue(),map);
        }
        CommonItem commonItem =map.get(1);
        if(commonItem==null){
            commonItem =new CommonItem();
            map.put(1,commonItem);
        }
        matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(tennisExtryScores));

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));

//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo,null);
        return matchScoresInfo;
    }

    /**
     * 拆功能键
     * @param linkId    链路id
     * @param eventCode  事件编码
     * @param matchScoresInfo  赛事比分
     * @param matchTimeInfo  赛事阶段
     * @return
     */
    public MatchEventInfoDTO buildMatchEventInfoDTO(String linkId, String eventCode, MatchScoresInfo matchScoresInfo, MatchTimeInfo matchTimeInfo)
    {
        log.info("::{}::to buildMatchEventInfoDTO param; eventCode:{}, matchScoresInfo:{}, matchTimeInfo:{}",
                linkId, eventCode, JSON.toJSONString(matchScoresInfo), JSON.toJSONString(matchTimeInfo));
        Integer firstT1 = 0, firstT2 = 0, secondT1 = 0, secondT2 = 0 ;

        Long Period = matchTimeInfo.getPeriod();
        Integer currentSet = matchTimeInfo.getCurrentSet();
        Integer currentRound = matchTimeInfo.getCurrentRound();

        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);

        // 计算局比分
        Map<Integer, CommonItem> setScore = Maps.newConcurrentMap();
        for (Map.Entry<Long, TennisScores> entry : allPeriodScores.entrySet())
        {
            if (entry.getKey().equals(WHOLE_MATCH))
            {
                continue;
            }
            Integer setNumber = MatchPeriodUtils.getTennisSetByPeriod(entry.getKey());
            if ( null == setNumber )
            {
                continue;
            }
            CommonItem commonItemVo = new CommonItem();
            BeanUtils.copyProperties(entry.getValue().getSetScore(), commonItemVo);
            setScore.put(setNumber, commonItemVo);
        }

        // 计算局内比分
        TennisExtryScores tennisExtryScores;
        if (StringUtils.isEmpty(matchScoresInfo.getScoresJsonExtra()))
        {
            tennisExtryScores = new TennisExtryScores();
        }
        else
        {
            tennisExtryScores = JSONObject.toJavaObject((JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra())), TennisExtryScores.class);
        }
        Map<Integer, Map<Integer, CommonItem>> currentScoresMap = tennisExtryScores.getCurrentScoresMap();
        Map<String, Integer> secondMap = null;
        // 根据不同的赛事阶段统计不同的比分
        if ( null != Period ) {
            // 第一盘
            if ( "8".equals(Period.toString()) || "800".equals(Period.toString()) || "301".equals(Period.toString()) )
            {
                if ( !Objects.isNull(setScore.get(1)))
                {
                    firstT1 = setScore.get(1).getHome();
                    firstT2 = setScore.get(1).getAway();
                }
                secondMap = statisticsRoundScore(linkId, 1, currentScoresMap);
            }
            //局盘切换的部分
            else if ( "302".equals(Period.toString()) || "303".equals(Period.toString()) || "304".equals(Period.toString()) )
            {
                Integer tCurrentSet = currentSet -1 ;
                if ( !Objects.isNull(setScore.get(tCurrentSet)))
                {
                    firstT1 = setScore.get(tCurrentSet).getHome();
                    firstT2 = setScore.get(tCurrentSet).getAway();
                }
                secondMap = statisticsRoundScore(linkId, tCurrentSet, currentScoresMap);
            }
            // 局结束
//            else if ( "800" .equals(Period.toString()) ||  "900" .equals(Period.toString()) || "1000" .equals(Period.toString()) || "1100" .equals(Period.toString()) )
//            {
//                Integer tCurrentSet = currentSet -1 ;
//                if ( !Objects.isNull(setScore.get(tCurrentSet)))
//                {
//                    firstT1 = setScore.get(tCurrentSet).getHome();
//                    firstT2 = setScore.get(tCurrentSet).getAway();
//                }
//            }
            else
            {
                if ( !Objects.isNull(setScore.get(currentSet)) )
                {
                    firstT1 = setScore.get(currentSet).getHome();
                    firstT2 = setScore.get(currentSet).getAway();
                }
                secondMap = statisticsRoundScore(linkId, currentSet, currentScoresMap);
            }

            // 局内分
            if (null != secondMap  && secondMap.size() > 0 )
            {
                secondT1 = secondMap.get("secondT1");
                secondT2 = secondMap.get("secondT2");
            }
        }

        MatchEventInfoDTO eventInfoDTO = new MatchEventInfoDTO();
        eventInfoDTO.setEventTime(TimeUtils.millsSecondsEast8ZoneGmt());
        eventInfoDTO.setEventCode(eventCode);
        eventInfoDTO.setSportId(matchScoresInfo.getSportId());
        eventInfoDTO.setMatchPeriodId(matchScoresInfo.getPeriod());
        eventInfoDTO.setDataSourceCode(DataSourceCodeEnum.PD.name());
        eventInfoDTO.setCopyLinkId(linkId);
        eventInfoDTO.setSourceType("1");
        eventInfoDTO.setT1(matchScoresInfo.getT1());  //主队盘比分
        eventInfoDTO.setT2(matchScoresInfo.getT2());  //客队盘比分
        eventInfoDTO.setSecondNum(matchTimeInfo.getCurrentRound());
        eventInfoDTO.setSecondT1(secondT1);  //当前主队局内比分
        eventInfoDTO.setSecondT2(secondT2);  //当前客队局内比分
        eventInfoDTO.setFirstNum(matchTimeInfo.getFirstNum());
        eventInfoDTO.setFirstT1(firstT1);   //当前主队局比分
        eventInfoDTO.setFirstT2(firstT2);   //当前客队局比分
        eventInfoDTO.setCanceled(0);
        eventInfoDTO.setSecondsFromStart(0L);
        eventInfoDTO.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());

        eventInfoDTO.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        eventInfoDTO.setMatchLength(matchTimeInfo.getMatchLength());
        log.info("::{}::buildMatchEventInfoDTO resultData:{}", linkId, JSON.toJSONString(eventInfoDTO));
        return eventInfoDTO;
    }

    /**
     * 计算出局内比分
     * @param currentSet
     * @param currentScoresMap
     */
    private static Map<String, Integer> statisticsRoundScore(String linkId, Integer currentSet, Map<Integer, Map<Integer, CommonItem>> currentScoresMap)
    {
        log.info("::{}::currentSet:{}, currentScoresMap:{}", linkId, currentSet, JSON.toJSONString(currentScoresMap));
        Map<String, Integer> secondMap = Maps.newConcurrentMap();
        secondMap.put("secondT1", 0);
        secondMap.put("secondT2", 0);
        Map<Integer, CommonItem> currentRoundMap = currentScoresMap.get(currentSet);
        if ( null == currentScoresMap || null == currentRoundMap || 0 == currentRoundMap.size() )
        {
            return secondMap;
        }
        List<Integer> rounds = currentRoundMap.keySet().stream().sorted(Comparator.comparing(Integer::intValue).reversed()).collect(Collectors.toList());
        //Set<Integer> currentSets = currentRoundMap.keySet();
        if ( !CollectionUtils.isEmpty(rounds) )
        {
            for (Integer index : rounds)
            {
                CommonItem item = currentRoundMap.get(index);
                if (item.getAway() > 0 || item.getHome() > 0 )
                {
                    secondMap.put("secondT1", item.getHome());
                    secondMap.put("secondT2", item.getAway());
                    break;
                }
            }
        }
        log.info("::{}::secondMap:{}", linkId, currentSet, JSON.toJSONString(secondMap));
        return secondMap;
    }

    /**
     * 同步赛事阶段与状态
     * @param linkId
     * @param thirdMatchInfo
     * @param matchStatus
     * @param periodId
     */
    private void syncMatchStatus(String linkId, ThirdMatchInfo thirdMatchInfo, Integer matchStatus, Long periodId)
    {
        log.info("::{}::同步赛事阶段与状态入参, thirdMatchInfo:{}, matchStatus:{}, periodId:{}", linkId, JSON.toJSONString(thirdMatchInfo), matchStatus, periodId);
        if ( null != thirdMatchInfo &&  null != thirdMatchInfo.getReferenceId() && thirdMatchInfo.getReferenceId()>0 )
        {
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
            if( null != standardMatchInfo )
            {

                thirdMatchInfo.setMatchStatus(matchStatus);
                thirdMatchInfo.setModifyTime(System.currentTimeMillis());
                thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);

                standardMatchInfo.setMatchStatus(matchStatus);
                standardMatchInfo.setMatchPeriodId(periodId);
                standardMatchInfo.setModifyTime(System.currentTimeMillis());
                standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
                log.info("::{}::同步赛事阶段与状态完成");
//                eventProducer.sendMatchStatus(thirdMatchInfo,standardMatchInfo.getId()+"_PD_STATUS",matchStatus);
            }
        }
    }

    /**
     * 过滤多余的初始化局内分
     * @param linkId
     * @param matchTimeInfo
     * @param currentScoresMap
     * @return
     */
    public Map<Integer, Map<Integer,CommonItem>> filterScoresRecordMap(String linkId, MatchTimeInfo matchTimeInfo, Map<Integer, Map<Integer,CommonItem>> currentScoresMap,String json)
    {

        log.info("::{}::filterScoresRecordMap的入参:{}", linkId, JSON.toJSONString(currentScoresMap));
        Map<Integer, Map<Integer,CommonItem>> scoresMap = Maps.newTreeMap();
        if ( null == matchTimeInfo || StringUtils.isEmpty(matchTimeInfo.getMatchLengthJson()))
        {
            return currentScoresMap;
        }
        TreeMap<String, Object> treeMap = JSONObject.parseObject(matchTimeInfo.getMatchLengthJson(), TreeMap.class);
        log.info("::{}::treeMap:{}" , linkId, JSON.toJSONString(treeMap) );
//        Map<Integer, Integer> checkMap = Maps.newHashMap();
//        for (Map.Entry<String, Object> entry : treeMap.entrySet())
//        {
//            String type = entry.getValue().toString();
//            TennisMatchLengthEnum byCode = TennisMatchLengthEnum.getByCode(type);
//            checkMap.put( Integer.parseInt(entry.getKey()), Integer.parseInt(byCode.getValue()) );
//        }
        JSONObject checkMap= JSONObject.parseObject(json);
        for (Map.Entry<Integer, Map<Integer, CommonItem>> scoresEntry : currentScoresMap.entrySet() )
        {
            Integer round = scoresEntry.getKey();
            Integer setKey = scoresEntry.getKey();
            Map<Integer, CommonItem> setValue = scoresEntry.getValue();
            if (checkMap.containsKey(round))
            {
                Integer standard = Integer.parseInt(checkMap.get(round).toString());
                Map<Integer, CommonItem> commonMap = scoresEntry.getValue();
                Map<Integer, CommonItem> scoreDetail = Maps.newTreeMap();

                for (int index = 1; index <= standard ; index ++ )
                {
                    if ( commonMap.containsKey(index) ) {
                        scoreDetail.put(index, commonMap.get(index));
                    }
                    else
                    {
                        CommonItem addCommonItem = new CommonItem();
                        addCommonItem.setHome(0);
                        addCommonItem.setAway(0);
                        scoreDetail.put(index, addCommonItem);
                    }
                }

//                for ( Map.Entry<Integer, CommonItem> commonItemEntry : commonMap.entrySet())
//                {
//                    Integer key = commonItemEntry.getKey();
//                    if (key <= standard)
//                    {
//                        scoreDetail.put(key, commonItemEntry.getValue());
//                    }
//                }
                scoresMap.put( setKey, scoreDetail);
            }
            else
            {
                scoresMap.put( setKey, setValue);
            }
        }
        log.info("::{}::scoresMap:{}" , linkId, JSON.toJSONString(scoresMap) );
        return scoresMap;
    }

    /**
     * 公共比分下发方法
     * @param linkId
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @param matchTimeInfo
     * @param eventCode
     */
    public void noteEventPush(String linkId, ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchTimeInfo matchTimeInfo, String eventCode)
    {
        log.info("::{}::noteEventPush入参; thirdMatchInfo:{}, matchScoresInfo:{}, matchTimeInfo:{}, eventCode:{}", linkId, JSON.toJSONString(thirdMatchInfo),
                JSON.toJSONString(matchScoresInfo), JSON.toJSONString(matchTimeInfo), eventCode);

        MatchEventInfoDTO eventInfoDTO = buildMatchEventInfoDTO(linkId, eventCode, matchScoresInfo, matchTimeInfo);
        eventProducer.sendPDEventInfo(eventInfoDTO);

        //4.推送ws
        redisUtils.pushTenniseScore(thirdMatchInfo.getId());

        // 推送给下游的标准与三方比分
        scoresProducer.sendToMQ(thirdMatchInfo, matchScoresInfo, linkId);
    }

    @Override
    public Response setMatchOpenBall(Response<MatchScoreAndTimeVo> response, TennisEditSecondScoreDto tennisAdvertiseDto)
    {
        MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
        MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
        MatchEventInfoDTO eventInfoDTO = buildMatchEventInfoDTO(tennisAdvertiseDto.getLinkedId(),"service_taken", matchScoresInfo, matchTimeInfo);
        JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
        Map<Long, TennisScores> allPeriodScores = JsonMapUtils.parseTennisMap(periodFootballScores);
        TennisScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        eventInfoDTO.setT1(wholeSores.getMatchScore().getHome());
        eventInfoDTO.setT2(wholeSores.getMatchScore().getAway());
        eventInfoDTO.setFirstT1(wholeSores.getMatchScore().getHome());
        eventInfoDTO.setFirstT2(wholeSores.getMatchScore().getAway());
        eventInfoDTO.setSecondT1(wholeSores.getCurrentScore().getHome());
        eventInfoDTO.setSecondT2(wholeSores.getCurrentScore().getAway());
        eventInfoDTO.setAddition3(tennisAdvertiseDto.getHomeAway());
        eventInfoDTO.setHomeAway(tennisAdvertiseDto.getHomeAway());
        log.info("setMatchOpenBall的下发MQ:{}", JSONObject.toJSONString(eventInfoDTO) );
        eventProducer.sendPDEventInfo(eventInfoDTO);
        //增加操作日志
        iMatchScorePdLogService.setMatchOpenBallLog(response.getData(),tennisAdvertiseDto);
        return Response.success();
    }
}
