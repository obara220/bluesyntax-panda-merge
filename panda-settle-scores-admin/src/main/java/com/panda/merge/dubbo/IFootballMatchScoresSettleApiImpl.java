//package com.panda.merge.dubbo;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.api.IFootballMatchScoresSettleApi;
//import com.panda.merge.check.IMatchSettleCheckService;
//import com.panda.merge.check.impl.MatchSettleCheckServiceImpl;
//import com.panda.merge.common.enums.*;
//import com.panda.merge.common.utils.TimeUtils;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.*;
//import com.panda.merge.dto.MatchSettleEventMessage;
//import com.panda.merge.dto.MatchSettleScoreMessage;
//import com.panda.merge.dto.Response;
//import com.panda.merge.dto.SettleQueryDTO;
//import com.panda.merge.dto.message.MatchFreezeMessage;
//import com.panda.merge.dto.settle.*;
//import com.panda.merge.mapper.*;
//import com.panda.merge.model.*;
//import com.panda.merge.mq.producer.MatchSettleCenterProducer;
//import com.panda.merge.mq.producer.MatchSettleScoresProducer;
//import com.panda.merge.respository.MatchSettleInfoRepository;
//import com.panda.merge.respository.MatchSettleRollBackInfoRepository;
//import com.panda.merge.respository.StandardMatchInfoRepository;
//import com.panda.merge.service.IMatchSettleLogService;
//import com.panda.merge.service.IMatchSettleService;
//import com.panda.merge.service.IWsPushService;
//import com.panda.merge.service.StandardMatchInfoService;
//import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
//import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
//import com.panda.merge.service.settleMention.dto.FootballSettleMentionDto;
//import com.panda.merge.service.syncScore.SyncScoreFactory;
//import com.panda.merge.utils.*;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.*;
//
//
///**
// * 结算2.0 dubbo服务 KB
// * */
//@Service
//@DubboService
//@Slf4j
//public class IFootballMatchScoresSettleApiImpl implements IFootballMatchScoresSettleApi {
//
//    @Autowired
//    MatchSettleScoreMapper matchSettleScoreMapper;
//    @Autowired
//    MatchSettleEventMapper matchSettleEventMapper;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    MatchSettleScoresProducer matchSettleScoresProducer;
//    @Autowired
//    MatchSettleInfoMapper matchSettleInfoMapper;
//    @Autowired
//    MatchSettleOperateLogMapper matchSettleOperateLogMapper;
//    @Autowired
//    IMatchSettleService matchSettleService;
//    @Autowired
//    IMatchSettleLogService iMatchSettleLogService;
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleThirdScoreMapper matchSettleThirdScoreMapper;
//    @Autowired
//    MatchSettleThirdEventMapper matchSettleThirdEventMapper;
//    @Autowired
//    IWsPushService wsPushService;
//    @Autowired
//    MatchSettleCenterProducer matchSettleCenterProducer;
//    @Autowired
//    IMatchSettleScoreEventMapper iMatchSettleScoreEventMapper;
//    @Autowired
//    IMatchSettleCheckService matchSettleCheckService;
//    @Autowired
//    MatchSettleRollBackInfoMapper matchSettleRollBackInfoMapper;
//    @Autowired
//    SyncScoreFactory syncScoreFactory;
//    @Autowired
//    MatchDelaySettleInfoMapper matchDelaySettleInfoMapper;
//    @Autowired
//    MatchSettleCheckServiceImpl matchSettleCheckServiceImpl;
//    @Autowired
//    MatchSettleInfoRepository matchSettleInfoRepository;
//    @Autowired
//    MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
//    @Autowired
//    StandardMatchInfoService standardMatchInfoService;
//    @Override
//    public List<MatchSettleScoreDto> searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        List<String> eventCodes =new ArrayList<>();
//        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
//            eventCodes.add("fa_card");
//            //2975 --add red_card
//            eventCodes.add("red_card");
//        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
//            eventCodes.add("goal");eventCodes.add("kick_off");
//        }else {
//            eventCodes.add("corner");
//        }
//        MatchSettleScoreExample example =new MatchSettleScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeIn(eventCodes);
//        example.setOrderByClause("settle_num desc");
//        List<MatchSettleScore> list =matchSettleScoreMapper.selectByExample(example);
//
//        Map<String, Integer> deleteStatusMap = new HashMap<>();
//        Map<String, Integer> dataMismatchMap = new HashMap<>();
//        obtainDetailInfo(settleScoreSearchDto, deleteStatusMap, dataMismatchMap);
//        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
//        for (MatchSettleScore matchSettleScore : list) {
//            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
//            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
//            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
//            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
//            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//            matchSettleScoreDto.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
//            matchSettleScoreDto.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
//            matchSettleScoreDto.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
//            matchSettleScoreDto.setIsGrey(matchSettleScore.getIsGrey());
//            //查询阶段比分的时候要过滤角球 阶段比分 界面查询展示效果变更
//            if(settleScoreSearchDto.getEventCode().equals("corner")&&(
//                    matchSettleScore.getSettleNum().equals("201")||matchSettleScore.getSettleNum().equals("202")
//                            ||matchSettleScore.getSettleNum().equals("203")||matchSettleScore.getSettleNum().equals("206")||matchSettleScore.getSettleNum().equals("207")
//            ||matchSettleScore.getSettleNum().equals("208"))){
//                continue;
//            }
//            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
//            matchSettleScoreDtos.add(matchSettleScoreDto);
//        }
//        //查询 当前用户的 阶段比分的明细的审核状态
//        matchSettleCheckService.searchCheckStatusByScoresList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
//        setRollBackStatusScores(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
//        //查询比分的倒计时秒数
//        setDelaySettleSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
//        matchSettleScoreDtos = setFiveMinList(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
//        //查询回滚状态
//        return matchSettleScoreDtos;
//    }
//    /**
//     * 查询三方结算阶段比分
//     * */
//    @Override
//    public ThirdMatchSettleScoresDto searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        //1.查询所有条件符合的三方比分
//        List<String> eventCodes =new ArrayList<>();
//        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
//            eventCodes.add("fa_card");
//            //2975 --add red_card
//            eventCodes.add("red_card");
//        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
//            eventCodes.add("goal");eventCodes.add("kick_off");
//        }else {
//            eventCodes.add("corner");
//        }
//        MatchSettleThirdScoreExample example =new MatchSettleThirdScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeIn(eventCodes);
//        example.setOrderByClause("settle_num desc");
//        List<MatchSettleThirdScore> list =matchSettleThirdScoreMapper.selectByExample(example);
//        List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
//        for (MatchSettleThirdScore matchSettleScore : list) {
//            MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
//            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
//            matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
//            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
//            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//            //查询阶段比分的时候要过滤角球 阶段比分 界面查询展示效果变更
//            if(settleScoreSearchDto.getEventCode().equals("corner")&&(
//                    matchSettleScore.getSettleNum().equals("201")||matchSettleScore.getSettleNum().equals("202")
//                            ||matchSettleScore.getSettleNum().equals("203")||matchSettleScore.getSettleNum().equals("206")||matchSettleScore.getSettleNum().equals("207")
//                            ||matchSettleScore.getSettleNum().equals("208"))){
//                continue;
//            }
//            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
//            matchSettleScoreDtos.add(matchSettleScoreDto);
//        }
//        //2.根据数据商比分组
//        Map<String,List<MatchSettleScoreDto>> map = matchSettleScoreDtos.stream().collect(Collectors.groupingBy(MatchSettleScoreDto::getDataSourceCode));
//        //3.组装数据返回前端
//        ThirdMatchSettleScoresDto thirdMatchSettleScoresDto =new ThirdMatchSettleScoresDto();
//        thirdMatchSettleScoresDto.setEventCode(settleScoreSearchDto.getEventCode());
//        thirdMatchSettleScoresDto.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
//        thirdMatchSettleScoresDto.setThirdMatchScoresMap(map);
//        //4.log日志记录异常报错以及耗时
//        return thirdMatchSettleScoresDto;
//    }
//
//    @Override
//    public List<MatchSettleEventDto> searchMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        List<Long> periods=new ArrayList<>();
//        List<String> eventCodes =new ArrayList<>();
//        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
//            eventCodes.add("yellow_card");
//            eventCodes.add("red_card");eventCodes.add("fa_card");
//        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
//            eventCodes.add("goal");eventCodes.add("no goal");
//        }else {
//            eventCodes.add("corner");
//        }
//        periods.add(6l);periods.add(7l);periods.add(41l);periods.add(42l);
//        MatchSettleEventExample example =new MatchSettleEventExample();
//        example.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeIn(eventCodes).andEventTypeEqualTo(1).andPeriodIdIn(periods);
//        example.setOrderByClause("settle_num desc,event_order desc");
//        List<MatchSettleEvent> list =matchSettleEventMapper.selectByExample(example);
//        List<MatchSettleEventDto> matchSettleScoreDtos=new ArrayList<>();
//        Map<String, Integer> deleteStatusMap = new HashMap<>();
//        Map<String, Integer> dataMismatchMap = new HashMap<>();
//        obtainDetailInfo(settleScoreSearchDto, deleteStatusMap, dataMismatchMap);
//        for (MatchSettleEvent matchSettleScore : list) {
//                MatchSettleEventDto matchSettleScoreDto =new MatchSettleEventDto();
//                BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
//                matchSettleScoreDto.setId(matchSettleScore.getId().toString());
//                matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//               matchSettleScoreDto.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
//               matchSettleScoreDto.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
//               matchSettleScoreDto.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
//               matchSettleScoreDto.setIsGrey(matchSettleScore.getIsGrey());
//               if(null==matchSettleScore.getFifteenMinSection()){
//                   matchSettleScore.setFifteenMinSection(matchSettleScore.getFiveMinSection());
//               }
//                matchSettleScoreDto.setFifteenMinSection(matchSettleScore.getFifteenMinSection());
//                MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
//                matchSettleScoreDtos.add(matchSettleScoreDto);
//        }
//        log.info("syncTest matchSettleScoreDtos: {}", matchSettleScoreDtos);
//        if(!settleScoreSearchDto.getEventCode().equals("corner")){
//            //进球和罚牌要展示多重结算
//            for (MatchSettleEventDto matchSettleScoreDto : matchSettleScoreDtos) {
//                MatchSettleEventExample eventExample =new MatchSettleEventExample();
//                eventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                        .andEventCodeIn(eventCodes).andThirdEventSourceIdEqualTo(matchSettleScoreDto.getThirdEventSourceId())
//                .andIdNotEqualTo(Long.parseLong(matchSettleScoreDto.getId()) );
//                MatchSettleEvent matchSettleEvent =matchSettleService.getExtryEvent(matchSettleScoreDto);
//                MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
//                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
//                matchSettleEventDto.setId(matchSettleEvent.getId().toString());
//                matchSettleEventDto.setScoresPeriodFreeze(matchSettleEvent.getSettleFreeze());
//                matchSettleScoreDto.setExtryEvent(matchSettleEventDto);
//                if(null==matchSettleScoreDto.getFifteenMinSection()){
//                    matchSettleEventDto.setFifteenMinSection(matchSettleScoreDto.getFiveMinSection());
//                }
//                matchSettleScoreDto.setFifteenMinSection(matchSettleScoreDto.getFifteenMinSection());
//                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEventDto);
//            }
//
//            if (settleScoreSearchDto.getEventCode().equals("fa_card")){
//                //查询根据settleNum [上半场,下半场,全场,加时赛上半场,加时赛下半场,加时赛全场]
//                List<String> settleNumFaCard=new ArrayList<>();
//                settleNumFaCard.add("304"); settleNumFaCard.add("308"); settleNumFaCard.add("309");
//                settleNumFaCard.add("3013"); settleNumFaCard.add("3017"); settleNumFaCard.add("3018");
//                MatchSettleScoreExample cornerEx =new MatchSettleScoreExample();
//                cornerEx.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                        .andEventCodeEqualTo("fa_card").andSettleNumIn(settleNumFaCard);
//                List<MatchSettleScore> matchSettleScores =matchSettleScoreMapper.selectByExample(cornerEx);
//                for (MatchSettleScore matchSettleScore : matchSettleScores) {
//                    MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
//                    BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
//                    matchSettleEvent.setId(matchSettleScore.getId().toString());
//                    matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//                    matchSettleEvent.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
//                    matchSettleEvent.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
//                    matchSettleEvent.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
//                    matchSettleEvent.setIsGrey(matchSettleScore.getIsGrey());
//                    MatchEventInfoSettleUtils.checkInfoKey(matchSettleEvent);
//                    matchSettleScoreDtos.add(matchSettleEvent);
//                }
//                matchSettleScoreDtos.sort(new Comparator<MatchSettleEventDto>() {
//                    @Override
//                    public int compare(MatchSettleEventDto o1, MatchSettleEventDto o2) {
//                        return CornerMatchEventSortUtils.compareCornerMatchEventAndScore(o1,o2);
//                    }
//                });
//            }
//
//            //查询 当前用户的 阶段比分的明细的审核状态
//            matchSettleCheckService.searchCheckStatusByEventList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
//            setRollBackStatusEvent(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
//            setDelayEventSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
//            return matchSettleScoreDtos;
//        }else {
//            //查询根据settleNum
//            List<String> settleNumCorner=new ArrayList<>();
//            settleNumCorner.add("201"); settleNumCorner.add("202"); settleNumCorner.add("203");
//            settleNumCorner.add("206"); settleNumCorner.add("207"); settleNumCorner.add("208");
//            MatchSettleScoreExample cornerEx =new MatchSettleScoreExample();
//            cornerEx.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                    .andEventCodeEqualTo("corner").andSettleNumIn(settleNumCorner);
//            List<MatchSettleScore> matchSettleScores =matchSettleScoreMapper.selectByExample(cornerEx);
//            for (MatchSettleScore matchSettleScore : matchSettleScores) {
//                MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
//                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
//                matchSettleEvent.setId(matchSettleScore.getId().toString());
//                matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//                matchSettleEvent.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
//                matchSettleEvent.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
//                matchSettleEvent.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
//                matchSettleEvent.setIsGrey(matchSettleScore.getIsGrey());
//                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEvent);
//                matchSettleScoreDtos.add(matchSettleEvent);
//            }
//            matchSettleScoreDtos.sort(new Comparator<MatchSettleEventDto>() {
//                @Override
//                public int compare(MatchSettleEventDto o1, MatchSettleEventDto o2) {
//                    return CornerMatchEventSortUtils.compareCornerMatchEventAndScore(o1,o2);
//                }
//            });
//            matchSettleCheckService.searchCheckStatusByEventList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
//            setRollBackStatusEvent(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
//            setDelayEventSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
//            return matchSettleScoreDtos;
//        }
//
//    }
//    /**
//     * 查询三方结算事件
//     * */
//    @Override
//    public ThirdMatchSettleEventDto searchThirdMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        //1.根据标准赛事等条件查询到相关事件
//        List<Long> periods=new ArrayList<>();
//        List<String> eventCodes =new ArrayList<>();
//        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
//            eventCodes.add("yellow_card");
//            eventCodes.add("red_card");eventCodes.add("fa_card");
//        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
//            eventCodes.add("goal");eventCodes.add("no goal");
//        }else {
//            eventCodes.add("corner");
//        }
//        periods.add(6l);periods.add(7l);periods.add(41l);periods.add(42l);
//        MatchSettleThirdEventExample example =new MatchSettleThirdEventExample();
//        example.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeIn(eventCodes).andEventTypeEqualTo(1).andPeriodIdIn(periods);
//        example.setOrderByClause("settle_num desc,event_order desc");
//        List<MatchSettleThirdEvent> list =matchSettleThirdEventMapper.selectByExample(example);
//        List<MatchSettleEventDto> matchSettleScoreDtos=new ArrayList<>();
//        for (MatchSettleThirdEvent matchSettleScore : list) {
//            MatchSettleEventDto matchSettleScoreDto =new MatchSettleEventDto();
//            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
//            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
//            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//            matchSettleScoreDtos.add(matchSettleScoreDto);
//        }
//        if(settleScoreSearchDto.getEventCode().equals("corner")){
//            //查询根据settleNum
//            List<String> settleNumCorner=new ArrayList<>();
//            settleNumCorner.add("201"); settleNumCorner.add("202"); settleNumCorner.add("203");
//            settleNumCorner.add("206"); settleNumCorner.add("207"); settleNumCorner.add("208");
//            MatchSettleThirdScoreExample cornerEx =new MatchSettleThirdScoreExample();
//            cornerEx.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                    .andEventCodeEqualTo("corner").andSettleNumIn(settleNumCorner);
//            List<MatchSettleThirdScore> matchSettleScores =matchSettleThirdScoreMapper.selectByExample(cornerEx);
//            for (MatchSettleThirdScore matchSettleScore : matchSettleScores) {
//                MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
//                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
//                matchSettleEvent.setId(matchSettleScore.getId().toString());
//                matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//                matchSettleScoreDtos.add(matchSettleEvent);
//            }
//        }
//        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
//            //查询根据settleNum
//            List<String> settleNumFaCard=new ArrayList<>();
//            settleNumFaCard.add("304"); settleNumFaCard.add("308"); settleNumFaCard.add("309");
//            settleNumFaCard.add("3013"); settleNumFaCard.add("3017"); settleNumFaCard.add("3018");
//            MatchSettleThirdScoreExample cornerEx =new MatchSettleThirdScoreExample();
//            cornerEx.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                    .andEventCodeEqualTo("fa_card").andSettleNumIn(settleNumFaCard);
//            List<MatchSettleThirdScore> matchSettleScores =matchSettleThirdScoreMapper.selectByExample(cornerEx);
//            for (MatchSettleThirdScore matchSettleScore : matchSettleScores) {
//                MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
//                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
//                matchSettleEvent.setId(matchSettleScore.getId().toString());
//                matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//                matchSettleScoreDtos.add(matchSettleEvent);
//            }
//        }
//        //2.根据事件进行分组
//        Map<String,List<MatchSettleEventDto>> map= matchSettleScoreDtos.stream().collect(Collectors.groupingBy(MatchSettleEventDto::getDataSourceCode));
//        Map<String,List<MatchSettleEventExtryInfoDto>> Infomap =new HashMap<>();
//        for (Map.Entry<String, List<MatchSettleEventDto>> entry : map.entrySet()) {
//            List<MatchSettleEventExtryInfoDto>  l = Infomap.get(entry.getKey());
//            if(l==null){
//                l= new ArrayList<>();
//                Infomap.put(entry.getKey(),l);
//            }
//            for (MatchSettleEventDto matchSettleEventDto : entry.getValue()) {
//                MatchSettleEventExtryInfoDto infoDto =new MatchSettleEventExtryInfoDto();
//                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEventDto);
//                BeanUtils.copyProperties(matchSettleEventDto,infoDto);
//                l.add(infoDto);
//            }
//        }
//        //3.组装事件数据
//        ThirdMatchSettleEventDto thirdMatchSettleEventDto =new ThirdMatchSettleEventDto();
//        thirdMatchSettleEventDto.setEventCode(settleScoreSearchDto.getEventCode());
//        thirdMatchSettleEventDto.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
//        thirdMatchSettleEventDto.setThirdMatchEventMap(Infomap);
//        //4.返回前端
//        return thirdMatchSettleEventDto;
//    }
//
//    @Override
//    public Response updateMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
//        String key = CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//
//            if(redisService.tryLock(key,key,2,5)) {
//                //0.加redis锁
//                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
//                if (standardMatchInfo == null) {
//                    return Response.failed("1031931");
//                }
//                MatchSettleScore matchSettleScore = null;
//                MatchSettleScore matchSettleBefore = new MatchSettleScore();
//                String forwScore ="" ;
//                if (matchSettleScoreDto.getMatchScoreId() != null && matchSettleScoreDto.getMatchScoreId() != 0) {
//                    matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                    BeanUtils.copyProperties(matchSettleScore,matchSettleBefore);
//                    if (matchSettleScore == null) {
//                        return Response.failed("1031931");
//                    }
//                    /*if(!matchSettleCheckService.isFiveMinPeriodScoresBeforeSettled(matchSettleScore)){
//                        // 五分钟玩法请确保上一个比分已结算。
//                        return Response.failed("1031946");
//                    }*/
//                    if(!matchSettleCheckService.isAllPeriodScoresBeforeSettled(matchSettleScore) &&
//                            !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_5.getCode().toString()) &&
//                            !matchSettleScoreDto.getSettleNum().equals(MatchPeriodEnum.GOAL_9.getCode().toString())) {
//                        // 请确保上一个比分已结算。
//                        return Response.failed("1031946");
//                    }
//                    if(matchSettleScore.getEventCode().equals("kick_off")||matchSettleScore.getSettleNum().equals("1020")){
//                        if(matchSettleScoreDto.getT1()!=null&&matchSettleScoreDto.getT2()!=null){
//                            if(!((matchSettleScoreDto.getT1()==0&&matchSettleScoreDto.getT2()==1)||(matchSettleScoreDto.getT1()==1&&matchSettleScoreDto.getT2()==0))){
//                                if(matchSettleScoreDto.getGoWaterStatus()==null||matchSettleScoreDto.getGoWaterStatus()==0){
//                                    return Response.failed("1031939");
//                                }
//                            }
//                        }else {
//                            if(matchSettleScoreDto.getGoWaterStatus()==null||matchSettleScoreDto.getGoWaterStatus()==0){
//                                return Response.failed("1031939");
//                            }
//                        }
//                    }
//                    //修改前比分
//                    forwScore= matchSettleScore.getT1()+"-"+ matchSettleScore.getT2();
//                    String t1 =matchSettleScore.getT1()==null ?"":matchSettleScore.getT1().toString();
//                    String t2 =matchSettleScore.getT2()==null ?"":matchSettleScore.getT2().toString();
//                    forwScore= t1+"-"+t2;
//
//                    List<Integer> integers = Arrays.asList(1021,1031,1032,1033);
//                    List<String> corner = Arrays.asList("206","207","208");
//                    if (integers.contains(matchSettleScoreDto.getSettleNum())) {
//                        String extryInfo = matchSettleScore.getExtryInfo();
//                        Integer integer = null;
//                        if (!StringUtils.isBlank(extryInfo) ) {
//                           integer = Integer.valueOf(extryInfo);
//                            forwScore = processedScore(forwScore, matchSettleScoreDto.getSettleNum(), integer);
//                        }else if(matchSettleScore.getGoWaterStatus()!=null && "1".equals(matchSettleScore.getGoWaterStatus().toString())){
//                            forwScore = WinningMethodEnum.Method_8.getCode().toString();
//                        }
//                    }
//                    if (corner.contains(matchSettleScore.getSettleNum())) {
//                        Integer goWaterStatus = matchSettleScore.getGoWaterStatus();
//                        //角球走水 10031
//                        if (goWaterStatus!=null && goWaterStatus.equals(1))  forwScore = OperateLogTypeEnum.SCORES_SETTLE_10031.getCode().toString();
//                    }
//                }else {
//                    return Response.failed("1031931");
//                }
//                matchSettleScore.setT1(matchSettleScoreDto.getT1());
//                matchSettleScore.setT2(matchSettleScoreDto.getT2());
//                matchSettleScore.setStatus(NOT_CONFIRM);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                if(matchSettleScoreDto.getGoWaterStatus()!=null&&matchSettleScoreDto.getGoWaterStatus()==1){
//                    matchSettleScore.setGoWaterStatus(1);
//                }else {
//                    matchSettleScore.setGoWaterStatus(0);
//                }
//                matchSettleScore.setExtryInfo(matchSettleScoreDto.getExtryInfo());
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                //比分判断是否相同
//                if(MatchEventInfoSettleUtils.equileMatchSettleScores(matchSettleBefore,matchSettleScore)){
//                    return Response.failed("1031940");
//                }
//                //1 判断顺序 结算开关是否开启，如果没开启则返回 简单校验逻辑();
//                if (!matchSettleCheckService.checkSettleScoreAndAutoSettleNonEvent(matchSettleScore,null)){
//                    return Response.failed("1031946");
//                }
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//
//                //2.判断更新上半场(5)和全场比分(10) 更新结算信息
//                if (matchSettleScore.getSettleNum().equals("105") || matchSettleScore.getSettleNum().equals("1010")) {
//                    recordScore(matchSettleScoreDto);
//                }
////                ThreadUtils.addTaskThreadPool(new Thread(() ->  wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
////                        matchSettleScoreDto.getEventCode())), "推送WS标准赛事结算比分" +
//                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//
//                //3.操作日志记录
//                iMatchSettleLogService.updateMatchSettleScoreAddLog(matchSettleScoreDto,forwScore,matchSettleScore,standardMatchInfo,OperateLogTypeEnum.EDIT.getCode().toString());
//                log.info("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("[IFootballMatchScoresSettleApiImpl] updateMatchSettleScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//
//    //1021,1031,1032,1033
//    //特殊处理的结算方式
//    public String processedScore(String forwScore, String settleNum, Integer extryInfo) {
//        if (settleNum.equals("1021")) {
//            forwScore= WinningMethodEnum.getWinningMethodByCode(extryInfo).getCode().toString();
//            return forwScore;
//        }
//
//        if (settleNum.equals("1031")) {
//            forwScore= YesNoEnum.getEnum(extryInfo).value.toString();
//            return forwScore;
//        }
//        if (settleNum.equals("1032") || settleNum.equals("1033")) {
//            //1表示走水
//            if (extryInfo.equals(1)) {
//                forwScore= WinningMethodEnum.Method_8.getCode().toString();
//                return forwScore;
//            }
//        }
//        return "-";
//    }
//
//
//    //更新结算表中比分
//     private void recordScore(UpdateMatchSettleScoreDto matchSettleScoreDto){
//         MatchSettleInfo   matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDto.getStandardMatchId());
//         if (matchSettleInfo!=null) {
//
//             matchSettleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//             if (matchSettleScoreDto.getSettleNum().equals("105")) {
//                 matchSettleInfo.setH1T1(matchSettleScoreDto.getT1());
//                 matchSettleInfo.setH1T2(matchSettleScoreDto.getT2());
//             }else if(matchSettleScoreDto.getSettleNum().equals("1010")){
//                 matchSettleInfo.setFtT1(matchSettleScoreDto.getT1());
//                 matchSettleInfo.setFtT2(matchSettleScoreDto.getT2());
//             }
//             //更新结算信息
//             matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//         }else {
//             log.error("参数异常【matchSettleInfos为空! 】");
//         }
//
//     }
//
//    @Override
//    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
//        //0.加redis锁
//        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleScore matchSettleScore =null;
//                matchSettleScore=matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031931");
//                }
//                if(matchSettleScore.getStatus()>=CONFIRM){
//                    return Response.failed("1031934");
//                }
//                matchSettleScore.setStatus(CONFIRM);
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                //2.记录日志
//                //走水 将编码设置为8
//                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
//                iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
//                        OperateLogTypeEnum.CONFIRM_SCORE,"",matchSettleScoreDto.getIpAddress());
//                //推送比分WS
//                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//                log.info("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("[IFootballMatchScoresSettleApiImpl] confirmMatchSettleScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//
//    @Override
//    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
//        log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} and param:{} start!",matchSettleScoreDto.getLinkedId(), matchSettleScoreDto);
//        String key =CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchSettleScoreDto.getStandardMatchId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        log.info("读取SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()+redisService.get("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()));
//        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
//            return Response.failed("1031960");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleScore matchSettleScore =null;
//                matchSettleScore=matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031931");
//                }
//                if(matchSettleScore.getStatus()!=CONFIRM){
//                    return Response.failed("1031932");
//                }
//                Integer settleTimes =matchSettleScore.getSettleTimes();
//                if(settleTimes==null){
//                    settleTimes=0;
//                }
//                if (matchSettleScore.getSettleCount()== null ) {
//                    matchSettleScore.setSettleCount(0);
//                }
//                settleTimes++;
//
//                //二次结算,必须给出结算原因
//                if (matchSettleScore.getSettleCount() >  0 &&
//                        (matchSettleScoreDto.getSettleReason()==null  ||
//                                matchSettleScoreDto.getSettleReason()== 0) ) {
//                    return Response.failed("1031953");
//                }
//
//                String  before= "-";
//                Integer settleReason = matchSettleScore.getSettleReason();
//                if (settleReason != null &&  settleReason != 0 ) {
//                    before = settleReason.toString();
//                    if (settleReason == 118) {
//                        before += ": "+matchSettleScore.getSettleReasonDetail();
//                    }
//                }
//                //这是理论时间不对 应该先查数据商，如果没数据商再赋值当前
//                if(matchSettleScore.getEventTime()==null||matchSettleScore.getEventTime().equals(0l)){
//                    Long eventTime =matchSettleCheckService.searchEventTimeByScores(matchSettleScore);
//                    if(eventTime==0l){
//                        eventTime=matchSettleScore.getModifyTime();
//                    }
//                    matchSettleScore.setEventTime(eventTime);
//                }
//                matchSettleScore.setStatus(SETTLED);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setSettleTimes(settleTimes);
//                matchSettleScore.setSettleCount(matchSettleScore.getSettleCount()+1);
//                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setSettleReason(matchSettleScoreDto.getSettleReason());
//                matchSettleScore.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());
//
//                matchSettleScore.setIsGrey(0);
//                matchSettleScore.setHasDeleteEvent(0);
//                matchSettleScore.setCurrentEventStatus(0);
//                matchSettleCheckService.endEventSettleByScore(matchSettleScore);
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                matchSettleCheckService.updateMatchGrayStatus(matchSettleScore.getStandardMatchId());
//                matchSettleCheckService.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
//                matchSettleCheckService.updateMatchFifteenMinGraySettleFactor(matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum());
//                //结算时把回滚订单数清零
//                matchSettleService.settleRollBackSetNullOrderCount(matchSettleScore.getId());
//                //2.MQ下发
//
//                if (matchSettleScore.getPeriodId()==100 && (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_10.getCode().toString()) ||
//                        matchSettleScore.getSettleNum().equals(MatchPeriodEnum.Corner_3.getCode().toString()) ||
//                        matchSettleScore.getSettleNum().equals(MatchPeriodEnum.BOOKINGS_9.getCode().toString()))) {
//                    matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore, 2);
//                } else {
//                    matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//                }
//
//                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//
//                //1.比分结算增加操作日志
//                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
//                iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE,before,matchSettleScoreDto.getIpAddress());
//                syncScoreFactory.getProcessor(SettleSyncEnum.FOOTBALL_SYNC_SCORE).syncScore(matchSettleScoreDto);
//                log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} end!",matchSettleScoreDto.getLinkedId());
//                return Response.success();
//            }else {
//                log.info("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} obtain redis fail!",matchSettleScoreDto.getLinkedId());
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("[IFootballMatchScoresSettleApiImpl] settleMatchScore with linkId:{} error:",matchSettleScoreDto.getLinkedId(), e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response addMatchSettleEvent(AddMatchSettleEventDto addMatchSettleEventDto) {
//        log.info("addMatchSettleEvent param,addMatchSettleEventDto: {}",addMatchSettleEventDto);
//        if(matchSettleService.checkIfOverSettleTime(addMatchSettleEventDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        String key ="MATCH_SETTLE_INFO:"+ addMatchSettleEventDto.getStandardMatchId();
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(addMatchSettleEventDto.getStandardMatchId());
//                //1.校验
//                if (standardMatchInfo == null) {
//                    return Response.failed("1031931");
//                }
//                MatchSettleEventExample example = new MatchSettleEventExample();
//                example.createCriteria().andSettleNumEqualTo(addMatchSettleEventDto.getSettleNum())
//                        .andStandardMatchIdEqualTo(addMatchSettleEventDto.getStandardMatchId());
//                List<MatchSettleEvent> list = matchSettleEventMapper.selectByExample(example);
//                //2.判断事件序号
//                Integer eventOrder = checkEventOrder(list);
//                if (eventOrder == 0) {
//                    return Response.failed("1031931");
//                }
//                eventOrder++;
//                //3.新增
//                MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
//                //0：未编码（初始化对应事件编码的数据）
//                matchSettleEvent.setStatus(0);
//                matchSettleEvent.setStandardMatchId(addMatchSettleEventDto.getStandardMatchId());
//                matchSettleEvent.setEventCode(addMatchSettleEventDto.getEventCode());
////        matchSettleEvent.setSettleNum(eventOrder.toString());
//                matchSettleEvent.setSettleNum(SettleNumUtils.getEventSettleNum(addMatchSettleEventDto.getEventCode(), addMatchSettleEventDto.getPeriodId()));
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setCreateTime(System.currentTimeMillis());
//                matchSettleEvent.setEventOrder(eventOrder);
//                matchSettleEvent.setSportId(1l);
//                matchSettleEvent.setId(IdGenerator.nextId());
//                matchSettleEvent.setThirdEventSourceId(matchSettleEvent.getId());
//                matchSettleEvent.setDataSourceCode("PA");
//                matchSettleEvent.setPeriodId(addMatchSettleEventDto.getPeriodId());
//                matchSettleEvent.setCheckNumber(1);
//                matchSettleEvent.setEventType(1);
//                matchSettleEvent.setFiveMinSection(addMatchSettleEventDto.getFiveMinSection());
//                //2.2 获得上个事件比分 自动计算比分
//                matchSettleEventMapper.insert(matchSettleEvent);
//                if (!(matchSettleEvent.getEventCode().equals("corner") || matchSettleEvent.getPeriodId().equals(50l))) {
//                    MatchSettleEvent matchSettleEvent2 = new MatchSettleEvent();
//                    BeanUtils.copyProperties(matchSettleEvent, matchSettleEvent2);
//                    matchSettleEvent2.setEventType(2);
//                    matchSettleEvent2.setId(IdGenerator.nextId());
//                    matchSettleEvent2.setSportId(1l);
//                    matchSettleEvent2.setDataSourceCode("PA");
//                        matchSettleEvent2.setCheckNumber(1);
//                    matchSettleEvent2.setSettleNum(SettleNumUtils.getTypeEventSettleNum(matchSettleEvent2.getEventCode(), matchSettleEvent2.getPeriodId()));
//                    matchSettleEventMapper.insert(matchSettleEvent2);
//                }
//                //4.查询事件列表返回
//                if("goal".equals(addMatchSettleEventDto.getEventCode()) && StringUtils.isNotBlank(addMatchSettleEventDto.getOperatorName())){
//                    wsPushService.pushSettleMatchList(new MatchListSettleDto(addMatchSettleEventDto.getStandardMatchId(),
//                            addMatchSettleEventDto.getEventCode(),null,null,5));
//                }else {
//                    wsPushService.pushStandardSettleEvent(addMatchSettleEventDto.getStandardMatchId(),
//                            addMatchSettleEventDto.getEventCode());
//                }
//                redisService.unLock(key,key);
//                return Response.success();
//            }else {
//                return Response.failed();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            return Response.failed();
//        }
//    }
//
//
//    private Integer checkEventOrder(List<MatchSettleEvent> list) {
//        Integer order =1;
//        for (MatchSettleEvent matchSettleEvent : list) {
//            if(matchSettleEvent.getEventOrder()!=null&&matchSettleEvent.getEventOrder()>order){
//                order=matchSettleEvent.getEventOrder();
//            }
//        }
//        return order;
//    }
//    /**
//     * 事件比分编辑
//     * */
//    @Override
//    public Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
//        log.info("editMatchSettleEvent param,editMatchSettleEvent: {}",editMatchSettleEventDto);
//        String key =CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + editMatchSettleEventDto.getStandardMatchId();
//        MatchSettleEvent extryevent = null ;
//        MatchSettleEvent matchSettleEvent = null;
//        MatchSettleEvent matchSettleEventBefore = new MatchSettleEvent();
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                if(editMatchSettleEventDto.getEventCode().equals("goal")){
//                     matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//                    if(matchSettleEvent==null){
//                        return Response.failed("1031935");
//                    }
//                    if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
//                        return Response.failed("1031939");
//                    }
//                    if(!matchSettleCheckService.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)){
//                        return Response.failed("10138");
//                    }
//                    BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//
//                    //1.自动计算进球比分
//                    updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"goal");
//                    //比分校验是否相同
//                    if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//                        matchSettleEvent.setGoWaterStatus(1);
//                    }else {
//                        matchSettleEvent.setGoWaterStatus(0);
//                    }
//                    matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                    matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
//                    matchSettleEvent.setStatus(1);
//                    matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
//                    matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                    //编辑影子事件比分和homeAway
//                    extryevent =matchSettleService.getExtryEvent(matchSettleEvent);
//                    if(extryevent!=null){
//                        MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
//                        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
//                        extryevent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//                        extryevent.setT1(matchSettleEvent.getT1());
//                        extryevent.setT2(matchSettleEvent.getT2());
//                        extryevent.setModifyTime(System.currentTimeMillis());
//                        extryevent.setHomeAway(editMatchSettleEventDto.getHomeAway());
////                        extryevent.setStatus(1);
//                        extryevent.setOperater(editMatchSettleEventDto.getOperatorName());
//                        matchSettleEventMapper.updateByPrimaryKey(extryevent);
//                    }
//
//                }else if(editMatchSettleEventDto.getEventCode().equals("corner")){
//                    //1.事件只编辑比分
//                     matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//                    if(matchSettleEvent!=null){
//                        BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//                        //自动计算角球比分
//                        updateGoalAndCornerEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway(),"corner");
//                        matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//                        matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                        matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
//                        matchSettleEvent.setStatus(1);
//                        matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
//                        matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                        if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//                            matchSettleEvent.setGoWaterStatus(1);
//                        }else {
//                            matchSettleEvent.setGoWaterStatus(0);
//                        }
//                        if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                            return Response.failed("1031940");
//                        }
//                        matchSettleEvent.setModifyTime(System.currentTimeMillis());
//
//                        matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                    }else {
//                        MatchSettleScore matchSettleScore =matchSettleScoreMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//                        if(matchSettleScore!=null) {
//                            //角球阶段比分由人工录入
//                            //比分判断是否相同
//                            if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                                return Response.failed("1031940");
//                            }
//                            if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//                                matchSettleEvent.setGoWaterStatus(1);
//                            }else {
//                                matchSettleEvent.setGoWaterStatus(0);
//                            }
//                            matchSettleScore.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//                            matchSettleScore.setT1(editMatchSettleEventDto.getT1());
//                            matchSettleScore.setT2(editMatchSettleEventDto.getT2());
//                            matchSettleScore.setModifyTime(System.currentTimeMillis());
//                            matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
//                            matchSettleScore.setStatus(1);
//                            matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                        }
//                    }
//                    //2.阶段比分
//                }else if(editMatchSettleEventDto.getEventCode().equals("fa_card")){
//                    //1.根据facard条件设置 主客队和 罚牌类型
//                    if(StringUtils.isEmpty(editMatchSettleEventDto.getHomeAway())){
//                        return Response.failed("1031939");
//                    }
//                     matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//
//                    //2.自动计算罚牌比分
//                    BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//                    updateFaCardEventByInfo(matchSettleEvent,editMatchSettleEventDto.getHomeAway());
//                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                    matchSettleEvent.setStatus(1);
//                    if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//                        matchSettleEvent.setGoWaterStatus(1);
//                    }else {
//                        matchSettleEvent.setGoWaterStatus(0);
//                    }
//                    matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
//                    matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    matchSettleEvent.setFifteenMinSection(editMatchSettleEventDto.getFifteenMinSection()!=null?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
//                    matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                    //3.设置到影子事件中比分 以及主客队 罚牌类型等
//                    extryevent =matchSettleService.getExtryEvent(matchSettleEvent);
//                    if(extryevent!=null){
//                        extryevent.setHomeAway(matchSettleEvent.getHomeAway());
//                        extryevent.setT1(matchSettleEvent.getT1());
//                        extryevent.setT2(matchSettleEvent.getT2());
//                        extryevent.setFirstT1(matchSettleEvent.getFirstT1());
//                        extryevent.setFirstT2(matchSettleEvent.getFirstT2());
//                        extryevent.setSecondT1(matchSettleEvent.getSecondT1());
//                        extryevent.setSecondT2(matchSettleEvent.getSecondT2());
//                        extryevent.setModifyTime(System.currentTimeMillis());
//                        extryevent.setEventCode(matchSettleEvent.getEventCode());
////                        extryevent.setStatus(1);
//                        extryevent.setOperater(editMatchSettleEventDto.getOperatorName());
//                        matchSettleEventMapper.updateByPrimaryKey(extryevent);
//                    }
//                }
//                //2.事件编辑记录日志
//                matchSettleEvent.setEventCode(editMatchSettleEventDto.getEventCode());
//                matchSettleEvent.setHomeAway(editMatchSettleEventDto.getHomeAway());
//                String homeAway = matchSettleEventBefore.getHomeAway();
//                String homeAwayNew = matchSettleEvent.getHomeAway();
//                if ("goal".equals(editMatchSettleEventDto.getEventCode())) {
//                    homeAway = goalProcessRest( matchSettleEventBefore.getHomeAway(),matchSettleEventBefore.getStatus());
//                    homeAwayNew = goalProcessRest( matchSettleEvent.getHomeAway(),matchSettleEvent.getStatus());
//
//                };
//                if ("fa_card".equals(editMatchSettleEventDto.getEventCode())){
//                    homeAway = faCardProcessRest(matchSettleEventBefore.getEventCode(), matchSettleEventBefore.getHomeAway(),matchSettleEventBefore.getStatus());
//                }
//                matchSettleEventBefore.setHomeAway(homeAway);
//                matchSettleEvent.setHomeAway(homeAwayNew);
//                //事件编辑增加日志
//                matchSettleEvent.setFiveMinSection(editMatchSettleEventDto.getFiveMinSection());
//                matchSettleEvent.setFifteenMinSection(StringUtils.isNotEmpty(editMatchSettleEventDto.getFifteenMinSection())?editMatchSettleEventDto.getFifteenMinSection():editMatchSettleEventDto.getFiveMinSection());
////                matchSettleEventBefore.setFiveMinSection(matchSettleEvent.getFiveMinSection());
////                matchSettleEventBefore.setFifteenMinSection(StringUtils.isNotEmpty(matchSettleEvent.getFifteenMinSection())?matchSettleEvent.getFifteenMinSection():matchSettleEvent.getFiveMinSection());
//                log.info("{}--保存15分钟日志:修改前 {}---{}",editMatchSettleEventDto.getEventCode()
//                        ,matchSettleEventBefore.getFiveMinSection(),
//                        matchSettleEventBefore.getFifteenMinSection());
//                log.info("{}--保存15分钟日志:修改后 {}---{}",editMatchSettleEventDto.getEventCode()
//                        ,matchSettleEvent.getFiveMinSection(),
//                        matchSettleEvent.getFifteenMinSection());
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEventBefore,matchSettleEvent,editMatchSettleEventDto.getOperatorName(),
//                        OperateLogTypeEnum.EDIT,editMatchSettleEventDto.getIpAddress());
//                //3.返回查询事件列表
//                wsPushService.pushStandardSettleEvent(editMatchSettleEventDto.getStandardMatchId(),
//                        editMatchSettleEventDto.getEventCode());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-editMatchSettleEvent:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//
//    //罚牌红黄牌特殊处理
//    String faCardProcessRest(String eventCode,String homeAway,Integer status){
//        if (FaCardEnum.Method_5.getMsg().equals(homeAway) ) {
//            if (status == 0) { return "-"; }
//            else { return FaCardEnum.Method_5.getCode().toString();}
//        }
//        if ("home".equals(homeAway) && "yellow_card".equals(eventCode))  return FaCardEnum.Method_1.getCode().toString();
//        if ("away".equals(homeAway) && "yellow_card".equals(eventCode))  return FaCardEnum.Method_2.getCode().toString();
//        if ("home".equals(homeAway) && "red_card".equals(eventCode))  return FaCardEnum.Method_3.getCode().toString();
//        if ("away".equals(homeAway) && "red_card".equals(eventCode))  return FaCardEnum.Method_4.getCode().toString();
//        return homeAway;
//    }
//
//    //进球主客队特殊处理
//    String goalProcessRest(String homeAway,Integer status){
//        if ("no goal".equals(homeAway) || "none".equals(homeAway)) {
//            if (status == 0) {
//                return "-";
//            } else { return OperateLogTypeEnum.type_6.getCode().toString();}
//        }
//        if ("home".equals(homeAway) )  return OperateLogTypeEnum.SCORES_SETTLE_10027.getCode().toString();
//        if ("away".equals(homeAway) )  return OperateLogTypeEnum.SCORES_SETTLE_10028.getCode().toString();
//
//        return homeAway;
//    }
//
//
//
//
//    public void updateGoalAndCornerEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway,String eventCode) {
//        log.info("updateGoalAndCornerEventByInfo param,matchSettleEvent: {},homeAway: {},eventCode: {}",matchSettleEvent,homeAway,eventCode);
//        MatchSettleEventExample eventExample = new MatchSettleEventExample();
//        eventExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId())
//                .andEventCodeEqualTo(eventCode).andPeriodIdLessThanOrEqualTo(matchSettleEvent.getPeriodId()).andIdNotEqualTo(matchSettleEvent.getId())
//                .andEventTypeEqualTo(1);
//        List< MatchSettleEvent> list =matchSettleEventMapper.selectByExample(eventExample);
//        Integer home=0;Integer away=0;
//        matchSettleEvent.setHomeAway(homeAway);
//        list.add(matchSettleEvent);
//        for (MatchSettleEvent event : list) {
//            //1.事件次序过滤 如果是 同一个阶段 就判断 次序 如果不是 则 判断阶段大小
//            if(matchSettleEvent.getPeriodId().equals(event.getPeriodId())){
//                if(matchSettleEvent.getEventOrder()<event.getEventOrder()){
//                    continue;
//                }
//            }
//            //2.常规赛非常常规赛隔离比分
//            if(matchSettleEvent.getPeriodId().equals(6l)||matchSettleEvent.getPeriodId().equals(7l)){
//                if(event.getPeriodId().equals(41l)||event.getPeriodId().equals(42l)){
//                    continue;
//                }
//            }else if(matchSettleEvent.getPeriodId().equals(41l)||matchSettleEvent.getPeriodId().equals(42l)){
//                if(event.getPeriodId().equals(6l)||event.getPeriodId().equals(7l)){
//                    continue;
//                }
//            }
//
//            if(StringUtils.isEmpty(event.getHomeAway())){
//                continue;
//            }
//            if(event.getHomeAway().equals("home")){
//                home++;
//            }else if(event.getHomeAway().equals("away")){
//                away++;
//            }else {
//                //none
//            }
//        }
//        //进球类型   home away  none
//        matchSettleEvent.setT1(home);
//        matchSettleEvent.setT2(away);
//    }
//
//    public void updateFaCardEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway) {
//        List<String> eventCodes =new ArrayList<>();eventCodes.add("yellow_card");
//        eventCodes.add("red_card");eventCodes.add("fa_card");
//        //罚牌类型   1  主队黄牌   2.客队黄牌  3.主队红牌  4.客队红牌  5.没有罚牌  null -
//        if(homeAway.equals("1")){
//            matchSettleEvent.setEventCode("yellow_card");
//            matchSettleEvent.setHomeAway("home");
//        }else if(homeAway.equals("2")){
//            matchSettleEvent.setEventCode("yellow_card");
//            matchSettleEvent.setHomeAway("away");
//        }else if(homeAway.equals("3")){
//            matchSettleEvent.setEventCode("red_card");
//            matchSettleEvent.setHomeAway("home");
//        }else if(homeAway.equals("4")){
//            matchSettleEvent.setEventCode("red_card");
//            matchSettleEvent.setHomeAway("away");
//        }else if(homeAway.equals("5")){
//            matchSettleEvent.setEventCode("fa_card");
//            matchSettleEvent.setHomeAway("none");
//        }else {
//            matchSettleEvent.setHomeAway("none");
//        }
//        List<String> settleNumList=new ArrayList<>();
//        if(matchSettleEvent.getSettleNum().equals("3019")||matchSettleEvent.getSettleNum().equals("3020")){
//            settleNumList.add("3019"); settleNumList.add("3020");
//        }else {
//            settleNumList.add("3022"); settleNumList.add("3023");
//        }
//        //自动计算当前事件比分
//        MatchSettleEventExample eventExample = new MatchSettleEventExample();
//        eventExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId())
//                .andSettleNumIn(settleNumList).andPeriodIdLessThanOrEqualTo(matchSettleEvent.getPeriodId()).andIdNotEqualTo(matchSettleEvent.getId())
//                .andEventTypeEqualTo(1).andStatusEqualTo(3);
//        List< MatchSettleEvent> list =matchSettleEventMapper.selectByExample(eventExample);
//        list.add(matchSettleEvent);
//        countFaCardScoresByEvent(list,matchSettleEvent);
//    }
//
//    private void countFaCardScoresByEvent(List<MatchSettleEvent> list, MatchSettleEvent matchSettleEvent) {
//        //1.计算黄牌数  红牌数  罚牌数
//        Integer red_card_t1 =0; Integer yellow_card_t1=0; Integer fa_card_t1=0;
//        Integer red_card_t2 =0; Integer yellow_card_t2=0; Integer fa_card_t2=0;
//        for (MatchSettleEvent event : list) {
//            if(matchSettleEvent.getPeriodId().equals(event.getPeriodId())){
//                if( matchSettleEvent.getEventOrder()<event.getEventOrder()){
//                    continue;
//                }
//            }
//            if(StringUtils.isNotEmpty(event.getEventCode())){
//                if(event.getEventCode().equals("yellow_card")){
//                    if(event.getHomeAway().equals("home")){
//                        yellow_card_t1++;fa_card_t1++;
//                    }else {
//                        yellow_card_t2++;fa_card_t2++;
//                    }
//                    continue;
//                }
//                if(event.getEventCode().equals("red_card")){
//                    if(event.getHomeAway().equals("home")){
//                        red_card_t1++;fa_card_t1+=2;
//                    }else {
//                        red_card_t2++;fa_card_t2+=2;
//                    }
//                    continue;
//                }
//            }
//        }
//        matchSettleEvent.setT1(fa_card_t1);
//        matchSettleEvent.setT2(fa_card_t2);
//        matchSettleEvent.setFirstT1(yellow_card_t1);
//        matchSettleEvent.setFirstT2(yellow_card_t2);
//        matchSettleEvent.setSecondT1(red_card_t1);
//        matchSettleEvent.setSecondT2(red_card_t2);
//    }
//
//    //进球方式和球员(setGoalMethodAndPlayer)
//    @Override
//    public Response editMatchSettleEventMethodAndPlayer(EditMatchSettleEventDto editMatchSettleEventDto) {
//        log.info("editMatchSettleEventMethodAndPlayer param,editMatchSettleEventDto: {}",editMatchSettleEventDto);
//        String key ="MATCH_SETTLE_INFO:"+ editMatchSettleEventDto.getEventId();
//        if(matchSettleService.checkIfOverSettleTime(editMatchSettleEventDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(editMatchSettleEventDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                MatchSettleEvent matchSettleEventBefore =new MatchSettleEvent();
//                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//                if(editMatchSettleEventDto.getGoWaterStatus()!=null&&editMatchSettleEventDto.getGoWaterStatus()==1){
//                    matchSettleEvent.setGoWaterStatus(1);
//                }
//                matchSettleEvent.setExtryInfo(editMatchSettleEventDto.getExtryInfo());
//                matchSettleEvent.setPlayerNameCode(editMatchSettleEventDto.getMatchPlayerNameCode());
//                matchSettleEvent.setStatus(1);
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setOperater(editMatchSettleEventDto.getOperatorName());
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//
//                //进球方式和球员_操作日志
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEventBefore,matchSettleEvent,editMatchSettleEventDto.getOperatorName(),
//                        OperateLogTypeEnum.PLAYER_AND_GOAL_TYPE,editMatchSettleEventDto.getIpAddress());
//                if("goal".equals(editMatchSettleEventDto.getEventCode()) && StringUtils.isNotBlank(editMatchSettleEventDto.getOperatorName())){
//                    wsPushService.pushSettleMatchList(new MatchListSettleDto(editMatchSettleEventDto.getStandardMatchId(),
//                            editMatchSettleEventDto.getEventCode(),null,null,6));
//                }else {
//                    wsPushService.pushStandardSettleEvent(editMatchSettleEventDto.getStandardMatchId(),
//                            editMatchSettleEventDto.getEventCode());
//                }
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-editMatchSettleEventMethodAndPlayer:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    //事件确认
//    @Override
//    public Response confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto) {
//        log.info("confirmMatchSettleEvent param,matchSettleEventDto: {}",matchSettleEventDto);
//        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + matchSettleEventDto.getStandardMatchId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleEventDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        log.info("事件Id::{}:: 当前事件被确认参数:{} ",matchSettleEventDto.getEventId(),matchSettleEventDto);
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleEventDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(matchSettleEvent.getStatus()!=NOT_CONFIRM){
//                    return Response.failed("1031934");
//                }
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setStatus(CONFIRM);
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                //2.确认记录日志
//                matchSettleEvent.setFifteenMinSection(matchSettleEvent.getFiveMinSection());
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleEventDto.getOperatorName(),
//                        OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(),"",matchSettleEventDto.getIpAddress());
//                //3.返回查询事件列表
//                wsPushService.pushStandardSettleEvent(matchSettleEventDto.getStandardMatchId(),
//                        matchSettleEventDto.getEventCode());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-confirmMatchSettleEvent:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response settleMatchSettleEvent(EditMatchSettleEventDto matchSettleScoreDto) {
//        log.info("settleMatchSettleEvent param,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key = CommonConstant.MATCH_SEQUENCE_SCORE_SETTLE + matchSettleScoreDto.getStandardMatchId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getEventId())){
//            return Response.failed("1031960");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(matchSettleScoreDto.getEventId());
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                if(matchSettleEvent.getStatus()!=CONFIRM){
//                    return Response.failed("1031936");
//                }
//                Integer settleTimes =matchSettleEvent.getSettleTimes();
//                if(settleTimes==null){
//                    settleTimes=0;
//                }
//                if (matchSettleEvent.getSettleCount()== null ) {
//                    matchSettleEvent.setSettleCount(0);
//                }
//                settleTimes++;
//
//                //二次结算,必须给出结算原因
//                if (matchSettleEvent.getSettleCount() >  0 &&
//                        (matchSettleScoreDto.getSettleReason()==null  ||
//                                matchSettleScoreDto.getSettleReason()== 0) ) {
//                    return Response.failed("1031953");
//                }
//
//                String  before= "-";
//                Integer settleReason = matchSettleEvent.getSettleReason();
//                if (settleReason != null &&  settleReason != 0 ) {
//                    before = settleReason.toString();
//                    if (settleReason == 118) {
//                        before += ": "+matchSettleEvent.getSettleReasonDetail();
//                    }
//                }
//                if(!matchSettleEvent.getSettleNum().equals("1028")){
//                    if(matchSettleEvent.getEventTime()==null||matchSettleEvent.getEventTime().equals(0l)){
//                        Long eventTime =matchSettleCheckService.searchEventTimeByEvent(matchSettleEvent);
//                        if(eventTime==0l){
//                            eventTime=matchSettleEvent.getModifyTime();
//                        }
//                        matchSettleEvent.setEventTime(eventTime);
//                    }
//                }
//                matchSettleEvent.setStatus(SETTLED);
//                matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount()+1);
//                matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                matchSettleEvent.setSettleTimes(settleTimes);
//                matchSettleEvent.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//                matchSettleEvent.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleEvent.setSettleReason(matchSettleScoreDto.getSettleReason());
//                matchSettleEvent.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());
//                matchSettleEvent.setIsGrey(0);
//                matchSettleEvent.setHasDeleteEvent(0);
//                matchSettleEvent.setCurrentEventStatus(0);
//                matchSettleCheckService.endEventSettleByEvent(matchSettleEvent);
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                matchSettleCheckService.updateMatchGrayStatus(matchSettleEvent.getStandardMatchId());
//                matchSettleCheckService.updateMatchCurrentEventStatus(matchSettleEvent.getStandardMatchId());
//                log.info("比分Id::{}:: 当前事件被结算参数:{} ",matchSettleScoreDto.getEventId(),matchSettleEvent);
//                //1.日志
//                matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//                matchSettleEvent.setFifteenMinSection(matchSettleScoreDto.getFifteenMinSection());
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,
//                        matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE.getCode().toString()
//                        ,before,matchSettleScoreDto.getIpAddress());
//
//                //2.MQ下发
//                matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
//                wsPushService.pushStandardSettleEvent(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-settleMatchSettleEvent:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response<PenaltyScoresVo> searchPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        PenaltyScoresVo penaltyScoresVo =new PenaltyScoresVo();
//        penaltyScoresVo.setStandardMatchId(settleScoreSearchDto.getStandardMatchId());
//        MatchSettleEventExample homeEventExample =new MatchSettleEventExample();
//        homeEventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1030").andHomeAwayEqualTo("home");
//        homeEventExample.setOrderByClause("event_order desc");
//        List<MatchSettleEvent> homeEvent =matchSettleEventMapper.selectByExample(homeEventExample);
//        List<MatchSettleEventDto> homeEventList=new ArrayList<>();
//        for (MatchSettleEvent matchSettleEvent : homeEvent) {
//            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
//            BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
//            matchSettleEventDto.setId(matchSettleEvent.getId().toString());
//            matchSettleEventDto.setScoresPeriodFreeze(matchSettleEvent.getSettleFreeze());
//            homeEventList.add(matchSettleEventDto);
//        }
//        penaltyScoresVo.setHomeEventList(homeEventList);
//        MatchSettleEventExample awayEventExample =new MatchSettleEventExample();
//        awayEventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1030").andHomeAwayEqualTo("away");
//        awayEventExample.setOrderByClause("event_order desc");
//        List<MatchSettleEvent> awayEvent =matchSettleEventMapper.selectByExample(awayEventExample);
//        List<MatchSettleEventDto> awayEventList=new ArrayList<>();
//        for (MatchSettleEvent matchSettleEvent : awayEvent) {
//            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
//            BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
//            matchSettleEventDto.setId(matchSettleEvent.getId().toString());
//            matchSettleEventDto.setScoresPeriodFreeze(matchSettleEvent.getSettleFreeze());
//            awayEventList.add(matchSettleEventDto);
//        }
//        penaltyScoresVo.setAwayEventList(awayEventList);
//
//        MatchSettleEventExample homeAway5Example =new MatchSettleEventExample();
//        homeAway5Example.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1029");
//        homeAway5Example.setOrderByClause("event_order desc");
//        List<MatchSettleEvent> homeAway5 =matchSettleEventMapper.selectByExample(homeAway5Example);
//        if(homeAway5.size()!=0){
//            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
//            BeanUtils.copyProperties(homeAway5.get(0),matchSettleEventDto);
//            matchSettleEventDto.setId(homeAway5.get(0).getId().toString());
//            matchSettleEventDto.setScoresPeriodFreeze(homeAway5.get(0).getSettleFreeze());
//            penaltyScoresVo.setHomeAway5RoundEvent(matchSettleEventDto);
//        }
//        //查询谁先射门
//        MatchSettleEventExample teamFirstExample =new MatchSettleEventExample();
//        teamFirstExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("-1030");
//        List<MatchSettleEvent> teamFirstList =matchSettleEventMapper.selectByExample(teamFirstExample);
//        if(teamFirstList.size()!=0){
//            MatchSettleEvent event= teamFirstList.get(0);
//            MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
//            BeanUtils.copyProperties(event,matchSettleEventDto);
//            matchSettleEventDto.setId(event.getId().toString());
//            penaltyScoresVo.setTeamFirst(matchSettleEventDto);
//        }else {
//            //旧数据兼容插入一条记录
//            this.addTeamFirstEvent(penaltyScoresVo,settleScoreSearchDto.getStandardMatchId(),homeEventList,awayEventList);
//        }
//        MatchSettleEventExample homeAwayAllExample =new MatchSettleEventExample();
//        homeAwayAllExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1028");
//        homeAwayAllExample.setOrderByClause("event_order desc");
//        List<MatchSettleEvent> homeAwayAll =matchSettleEventMapper.selectByExample(homeAwayAllExample);
//        if(homeAwayAll.size()!=0){
//            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
//            BeanUtils.copyProperties(homeAwayAll.get(0),matchSettleEventDto);
//            matchSettleEventDto.setId(homeAwayAll.get(0).getId().toString());
//            matchSettleEventDto.setScoresPeriodFreeze(homeAwayAll.get(0).getSettleFreeze());
//
//            penaltyScoresVo.setHomeAwayAllRoundEvent(matchSettleEventDto);
//        }
//        //点球大战走水查询
//        MatchSettleEventExample goWaterExample =new MatchSettleEventExample();
//        goWaterExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1054");
//        List<MatchSettleEvent> goWaterEventList =matchSettleEventMapper.selectByExample(goWaterExample);
//        if(goWaterEventList.size()!=0){
//            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
//            BeanUtils.copyProperties(goWaterEventList.get(0),matchSettleEventDto);
//            matchSettleEventDto.setId(goWaterEventList.get(0).getId().toString());
//            matchSettleEventDto.setScoresPeriodFreeze(goWaterEventList.get(0).getSettleFreeze());
//            penaltyScoresVo.setGoWaterPenaltyEvent(matchSettleEventDto);
//        }else {
//            MatchSettleEvent matchSettleScore13 =FootballPenaltySettleEventUtils.initPenaltySettleEvent(settleScoreSearchDto.getStandardMatchId());
//            matchSettleScore13.setEventCode("goal");
//            matchSettleScore13.setSettleNum("1054");
//            matchSettleScore13.setPeriodId(120l);
//            matchSettleScore13.setEventName("点球大战走水");
//            matchSettleEventMapper.insert(matchSettleScore13);
//            MatchSettleEventDto matchSettleEventDto=new MatchSettleEventDto();
//            BeanUtils.copyProperties(matchSettleScore13,matchSettleEventDto);
//            matchSettleEventDto.setId(matchSettleScore13.getId().toString());
//            penaltyScoresVo.setGoWaterPenaltyEvent(matchSettleEventDto);
//        }
//        //如果查询的时候第一轮都是第二个球，则改为第一个球
//        MatchSettleEventDto homeFEvent =null;
//        MatchSettleEventDto awayFEvent =null;
//        for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getAwayEventList()) {
//            if(matchSettleEventDto.getFirstNum()==1){
//                awayFEvent=matchSettleEventDto;
//            }
//        }
//        for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getHomeEventList()) {
//            if(matchSettleEventDto.getFirstNum()==1){
//                homeFEvent=matchSettleEventDto;
//            }
//        }
//        if(awayFEvent!=null&&homeFEvent!=null){
//            if(awayFEvent.getEventOrder()==2&&homeFEvent.getEventOrder()==2){
//                homeFEvent.setEventOrder(1);
//                awayFEvent.setEventOrder(1);
//            }
//        }
//        matchSettleCheckService.searchCheckStatusByPenalty(penaltyScoresVo,settleScoreSearchDto.getOperatorName());
//        setRollBackStatusPenalty(penaltyScoresVo,settleScoreSearchDto.getStandardMatchId());
//        return Response.success(penaltyScoresVo);
//    }
//
//    private void addTeamFirstEvent(PenaltyScoresVo penaltyScoresVo, Long standardMatchId, List<MatchSettleEventDto> homeEventList, List<MatchSettleEventDto> awayEventList) {
//        //先判断主客队
//        String firstTeam="none";
//        Integer status=0;
//        for (MatchSettleEventDto matchSettleEventDto : homeEventList) {
//            if(matchSettleEventDto.getStatus()==3&&matchSettleEventDto.getEventOrder()==1){
//                firstTeam="home";
//                status=3;
//            }
//        }
//        for (MatchSettleEventDto matchSettleEventDto : awayEventList) {
//            if(matchSettleEventDto.getStatus()==3&&matchSettleEventDto.getEventOrder()==1){
//                firstTeam="away";
//                status=3;
//            }
//        }
//        MatchSettleEvent matchSettleEvent =FootballPenaltySettleEventUtils.initPenaltySettleEvent(standardMatchId);
//        matchSettleEvent.setStatus(status);
//        matchSettleEvent.setHomeAway(firstTeam);
//        matchSettleEvent.setEventCode("goal");
//        matchSettleEvent.setSettleNum("-1030");
//        matchSettleEvent.setPeriodId(50l);
//        matchSettleEvent.setFirstNum(0);
//        matchSettleEvent.setEventOrder(0);
//        //通过赛事ID 做数据并发冗余 防止重复生成
//        matchSettleEvent.setId(standardMatchId);
//        matchSettleEventMapper.insert(matchSettleEvent);
//
//        MatchSettleEvent event= matchSettleEvent;
//        MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
//        BeanUtils.copyProperties(event,matchSettleEventDto);
//        matchSettleEventDto.setId(event.getId().toString());
//        penaltyScoresVo.setTeamFirst(matchSettleEventDto);
//    }
//
//    @Override
//    public Response addPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
//        log.info("addPenaltyScores param,settleScoreSearchDto: {}",settleScoreSearchDto);
//        if(matchSettleService.checkIfOverSettleTime(settleScoreSearchDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        //每次新增都是新增一轮点球
//        MatchSettleEventExample homeEventExample =new MatchSettleEventExample();
//        homeEventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1030");
//        homeEventExample.setOrderByClause("event_order desc");
//        List<MatchSettleEvent> homeEvent =matchSettleEventMapper.selectByExample(homeEventExample);
//        Integer round=0;
//        Integer point=0;
//        String lastHomeAway="home";
//        Integer t1 =0;
//        Integer t2 =0;
//        for (MatchSettleEvent matchSettleEvent : homeEvent) {
//            if(matchSettleEvent.getFirstNum()>round){
//                round=matchSettleEvent.getFirstNum();
//            }
//            if(matchSettleEvent.getEventOrder()>point){
//                point=matchSettleEvent.getEventOrder();
//                lastHomeAway=matchSettleEvent.getHomeAway();
//                t1=matchSettleEvent.getT1();
//                t2=matchSettleEvent.getT2();
//            }
//        }
//        //计算最大轮数
//        round++;point++;
//        //计算最大轮数
//        MatchSettleEvent initMatchSettleScoreT1 = FootballPenaltySettleEventUtils.initPenaltySettleEvent(settleScoreSearchDto.getStandardMatchId());
//        initMatchSettleScoreT1.setFirstNum(round);
//        initMatchSettleScoreT1.setEventCode("goal");
//        initMatchSettleScoreT1.setSettleNum("1030");
//        initMatchSettleScoreT1.setPeriodId(50l);
//        initMatchSettleScoreT1.setT1(t1);
//        initMatchSettleScoreT1.setT2(t2);
//        initMatchSettleScoreT1.setHomeAway("home");
//        MatchSettleEvent initMatchSettleScoreT2 = FootballPenaltySettleEventUtils.initPenaltySettleEvent(settleScoreSearchDto.getStandardMatchId());
//        initMatchSettleScoreT2.setFirstNum(round);
//        initMatchSettleScoreT2.setEventCode("goal");
//        initMatchSettleScoreT2.setSettleNum("1030");
//        initMatchSettleScoreT2.setHomeAway("away");
//        initMatchSettleScoreT2.setT1(t1);
//        initMatchSettleScoreT2.setT2(t2);
//        initMatchSettleScoreT2.setPeriodId(50l);
//        //判断球头
//        if(lastHomeAway.equals("home")){
//            initMatchSettleScoreT2.setEventOrder(point);
//            point++;
//            initMatchSettleScoreT1.setEventOrder(point);
//        }else {
//            initMatchSettleScoreT1.setEventOrder(point);
//            point++;
//            initMatchSettleScoreT2.setEventOrder(point);
//        }
//        matchSettleEventMapper.insert(initMatchSettleScoreT1);
//        matchSettleEventMapper.insert(initMatchSettleScoreT2);
//        //新增数据
//        return Response.success();
//    }
//
//
//    @Override
//    public Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
//        log.info("setPenaltyScores param,settleScoreSearchDto: {}",settleScoreSearchDto);
//        String key ="MATCH_SETTLE_INFO:"+ settleScoreSearchDto.getEventId();
//        if(matchSettleService.checkIfOverSettleTime(settleScoreSearchDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                //设置进球
//                MatchSettleEvent matchSettleEvent =matchSettleEventMapper.selectByPrimaryKey(settleScoreSearchDto.getEventId());
//                MatchSettleEvent matchSettleEventBefore =new MatchSettleEvent();
//                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventBefore);
//                //自动计算比分
//                if(matchSettleEvent==null){
//                    return Response.failed("1031935");
//                }
//                //1.判断谁先射门是否已经结算如果没结算则直接返回失败
//                if(!isTeamFirstSettled(settleScoreSearchDto.getStandardMatchId())){
//                    return Response.failed("1031952");
//                }
//                MatchSettleEvent oidMatchSettleEventLog = new MatchSettleEvent();
//                BeanUtils.copyProperties(matchSettleEvent,oidMatchSettleEventLog);
//                matchSettleEvent.setStatus(1);
//                if(!matchSettleEvent.getSettleNum().equals("1030")){
//                    matchSettleEvent.setT1(settleScoreSearchDto.getT1());
//                    matchSettleEvent.setT2(settleScoreSearchDto.getT2());
//                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                    matchSettleEvent.setOperater(settleScoreSearchDto.getOperatorName());
//                    if(settleScoreSearchDto.getGoWaterStatus()!=null&&settleScoreSearchDto.getGoWaterStatus()==1){
//                        matchSettleEvent.setGoWaterStatus(1);
//                    }else {
//                        matchSettleEvent.setGoWaterStatus(0);
//                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    //如果是1028 则编辑走水 1054
//                    if(matchSettleEvent.getSettleNum().equals("1028")){
//                        matchSettleService.updateGoWaterPenaltyScores(settleScoreSearchDto);
//                    }
//                    matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                }else {
//                    boolean isCanCount= countPenaltyScores(settleScoreSearchDto,matchSettleEvent);
//                    if(!isCanCount){
//                        return Response.failed("1031937");
//                    }
//                    matchSettleEvent.setExtryInfo(settleScoreSearchDto.getExtryInfo());
//                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                    matchSettleEvent.setOperater(settleScoreSearchDto.getOperatorName());
//                    if(settleScoreSearchDto.getGoWaterStatus()!=null&&settleScoreSearchDto.getGoWaterStatus()==1){
//                        matchSettleEvent.setGoWaterStatus(1);
//                    }else {
//                        matchSettleEvent.setGoWaterStatus(0);
//                    }
//                    if(MatchEventInfoSettleUtils.equileMatchSettleEvent(matchSettleEventBefore,matchSettleEvent)){
//                        return Response.failed("1031940");
//                    }
//                    matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                }
//                iMatchSettleLogService.matchSettleEventAddLog(oidMatchSettleEventLog,matchSettleEvent,
//                        settleScoreSearchDto.getOperatorName(),OperateLogTypeEnum.EDIT,settleScoreSearchDto.getIpAddress());
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-setPenaltyScores:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    /**
//     * 当球头变更，自动修正事件次序
//     * */
//    private void updatePenaltyEventOrder(String homeAway, Long standardMatchId) {
//        MatchSettleEventExample lastExample =new MatchSettleEventExample();
//        lastExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId)
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1030").andFirstNumGreaterThan(1);
//       List<MatchSettleEvent> list =matchSettleEventMapper.selectByExample(lastExample) ;
//        for (MatchSettleEvent matchSettleEvent : list) {
//            if(matchSettleEvent.getHomeAway().equals(homeAway)){
//                matchSettleEvent.setEventOrder(matchSettleEvent.getFirstNum()*2-1);
//            }else {
//                matchSettleEvent.setEventOrder(matchSettleEvent.getFirstNum()*2);
//            }
//            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//        }
//    }
//
//    private void countPenaltyOrder(EditMatchSettleEventDto matchSettleEvent, MatchSettleEvent settleEvent) {
//        MatchSettleEventExample homeEventExample =new MatchSettleEventExample();
//        homeEventExample.createCriteria().andStandardMatchIdEqualTo(settleEvent.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1030").andFirstNumEqualTo(1);
//        homeEventExample.setOrderByClause("event_order desc");
//        List<MatchSettleEvent> homeEvent =matchSettleEventMapper.selectByExample(homeEventExample);
//        //主队首提球
//        boolean isHomeFirst=false;
//        for (MatchSettleEvent event : homeEvent) {
//            //1.如果有个没有编辑则返回失败
//            if(event.getT1()==null||event.getT2()==null||event.getStatus()==NOT_EDIT){
//                return ;
//            }
//            if(event.getFirstNum()==1&&event.getEventOrder()==1){
//                if(event.getHomeAway().equals("home")){
//                    isHomeFirst=true;
//                }
//            }
//        }
//        if(isHomeFirst){
//            if(settleEvent.getHomeAway().equals("home")){
//                settleEvent.setEventOrder((settleEvent.getFirstNum()-1)*2+1);
//            }else {
//                settleEvent.setEventOrder((settleEvent.getFirstNum()-1)*2+2);
//            }
//        }else {
//            if(settleEvent.getHomeAway().equals("home")){
//                settleEvent.setEventOrder((settleEvent.getFirstNum()-1)*2+2);
//            }else {
//                settleEvent.setEventOrder((settleEvent.getFirstNum()-1)*2+1);
//            }
//        }
//        return ;
//    }
//
//    //重新修正球队的时候比分会计算才错误,建议是先计算次序再计算比分
//    public boolean countPenaltyScores(EditMatchSettleEventDto matchSettleEvent, MatchSettleEvent settleEvent) {
//        MatchSettleEventExample homeEventExample =new MatchSettleEventExample();
//        homeEventExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId())
//                .andEventCodeEqualTo("goal").andSettleNumEqualTo("1030").andEventOrderLessThanOrEqualTo(settleEvent.getEventOrder()).andIdNotEqualTo(matchSettleEvent.getEventId());
//        homeEventExample.setOrderByClause("event_order desc");
//        List<MatchSettleEvent> homeEvent =matchSettleEventMapper.selectByExample(homeEventExample);
//        Integer t1 =0,t2=0;
//        for (MatchSettleEvent event : homeEvent) {
//            //可能 修正 eventOder后会有 5-5 的情况,所以和自己相同的排序的事件要被过滤
//            if(event.getEventOrder()==settleEvent.getEventOrder()){
//                continue;
//            }
//            //1.如果有个没有编辑则返回失败
//            if(event.getT1()==null||event.getT2()==null||event.getStatus()==NOT_EDIT){
//                return false;
//            }
//            if("1".equals(event.getExtryInfo())){
//                if(event.getHomeAway().equals("home")){
//                    t1++;
//                }else {
//                    t2++;
//                }
//            }
//        }
//        if("1".equals(matchSettleEvent.getExtryInfo())){
//            if(settleEvent.getHomeAway().equals("home")){
//                t1++;
//            }else {
//                t2++;
//            }
//        }
//        settleEvent.setT1(t1);
//        settleEvent.setT2(t2);
//        return true;
//    }
//
//    @Override
//    public boolean isTeamFirstSettled(Long standardMatchId) {
//        MatchSettleEventExample eventExample =new MatchSettleEventExample();
//        eventExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andSettleNumEqualTo("-1030");
//        List<MatchSettleEvent> list =matchSettleEventMapper.selectByExample(eventExample);
//        if(list.size()==0){
//            return false;
//        }else {
//            if(list.get(0).getStatus()==SETTLED){
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//
//    /**
//     * 重新结算
//     * */
//    @Override
//    public Response reSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
//        log.info("reSettleMatchEvent param,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                    MatchSettleEvent matchSettleEvent = matchSettleEventMapper.selectByPrimaryKey(matchSettleScoreDto.getEventId());
//                    if(matchSettleEvent==null){
//                        return Response.failed("1031935");
//                    }
//                    Integer settleTimes =matchSettleEvent.getSettleTimes();
//                    if(settleTimes!=null&&settleTimes>0){
//                    }else {
//                        return Response.failed("1031938");
//                    }
////                    settleTimes++;
////                    matchSettleEvent.setStatus(SETTLED);
//                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
//                    matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                    matchSettleEvent.setSettleTimes(settleTimes);
//                    matchSettleEvent.setOperater(matchSettleScoreDto.getOperatorName());
//                    matchSettleEvent.setUserid(matchSettleScoreDto.getOperatorId());
//                    matchSettleEvent.setIsGrey(0);
//                    matchSettleEvent.setHasDeleteEvent(0);
//                    matchSettleEvent.setCurrentEventStatus(0);
//                    matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//                    //结算时把回滚订单数清零
//                    matchSettleService.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
//                    //1.日志
//                    iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleScoreDto.getOperatorName(),
//                            OperateLogTypeEnum.ROLLBACK_EXECUTE.getCode().toString(),"",matchSettleScoreDto.getIpAddress());
//
//                    //2.MQ下发
//                    MatchSettleEventMessage event = new MatchSettleEventMessage();
//                    BeanUtils.copyProperties(matchSettleEvent,event);
//                    event.setLevel(3);
//                    matchSettleScoresProducer.sendMatchSettleEvent(event);
//                    matchSettleCheckService.updateMatchCurrentEventStatus(matchSettleEvent.getStandardMatchId());
//                    return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-reSettleMatchEvent:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response reSettleMatchScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("reSettleMatchScore param,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031935");
//                }
//                Integer settleTimes =matchSettleScore.getSettleTimes();
//                if(settleTimes!=null&&settleTimes>0){
//                }else {
//                    return Response.failed("1031938");
//                }
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//                matchSettleScore.setSettleTimes(settleTimes);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleScore.setIsGrey(0);
//                matchSettleScore.setHasDeleteEvent(0);
//                matchSettleScore.setCurrentEventStatus(0);
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                matchSettleCheckService.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
//                //2.MQ下发
//                MatchSettleScoreMessage Score = new MatchSettleScoreMessage();
//                BeanUtils.copyProperties(matchSettleScore,Score);
//                Score.setLevel(3);
//                matchSettleScoresProducer.sendMatchSettleScores(Score);
//
//
//                //1.比分结算增加操作日志
//                //走水设置编码为8
//                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
//                iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
//                        OperateLogTypeEnum.ROLLBACK_EXECUTE,"",matchSettleScoreDto.getIpAddress());
//
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-reSettleMatchScore:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    /**
//     * 回滚结算
//     * */
//    @Override
//    public Response rollBackSettleMatchScores(UpdateMatchSettleScoreDto matchSettleScoreDto) {
//        log.info("rollBackSettleMatchScores param,matchSettleScoreDto: {}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//            /*if(redisService.tryLock(key,key,2,5)) {*/
//                MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScoreDto.getMatchScoreId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031935");
//                }
//                MatchSettleScore oIdMatchSettleScore = new   MatchSettleScore();
//                BeanUtils.copyProperties(matchSettleScore,oIdMatchSettleScore);
//                matchSettleScore.setGoWaterStatus(0);
//                matchSettleScore.setStatus(NOT_EDIT);
//                matchSettleScore.setT1(null);
//                matchSettleScore.setT2(null);
//                matchSettleScore.setExtryInfo(null);
//                matchSettleScore.setFirstT1(null);
//                matchSettleScore.setFirstT2(null);
//                matchSettleScore.setSecondT1(null);
//                matchSettleScore.setSecondT2(null);
//                matchSettleScore.setSettleTimes(0);
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.ROLL_BACK);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleScore.setSettleReasonDetail(null);
//                matchSettleScore.setSettleReason(null);
//                matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//                //将核对信息进行无效处理
//                matchSettleCheckService.rollbackScores(matchSettleScore);
//                //2.MQ下发
//                matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//                wsPushService.pushStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//
//                //1.记录日志
//                iMatchSettleLogService.matchSettleScoreAddLog(oIdMatchSettleScore,matchSettleScore,matchSettleScoreDto.getOperatorName(),
//                        OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode().toString(),matchSettleScoreDto.getLinkedId(),matchSettleScoreDto.getIpAddress());
//                //回滚新增记录
//                insertRollbackData(matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getMatchScoreId(),1,matchSettleScoreDto.getEventCode(),matchSettleScore.getSettleNum());
//                //回滚保存赛事ID一分钟
//                redisService.set("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId(),System.currentTimeMillis(),60);
//                log.info("添加SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()+redisService.get("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId()));
//                return Response.success();
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-rollBackSettleMatchScores:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response rollBackSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
//        log.info("rollBackSettleMatchEvent param,matchSettleScoreDto:{}",matchSettleScoreDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getEventId();
//        if(matchSettleService.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
//            return Response.failed("1031930");
//        }
//        try {
//           /* if(redisService.tryLock(key,key,2,5)) {*/
//                MatchSettleEvent matchSettleScore = matchSettleEventMapper.selectByPrimaryKey(matchSettleScoreDto.getEventId());
//                if(matchSettleScore==null){
//                    return Response.failed("1031935");
//                }
//                MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
//                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
//                matchSettleScore.setStatus(NOT_EDIT);
//                matchSettleScore.setGoWaterStatus(0);
//                //进球方式和球员玩法无需回滚比分
//                if(matchSettleEvent.getEventType()!=2){
//                    matchSettleScore.setT1(null);
//                    matchSettleScore.setT2(null);
//                    matchSettleScore.setFirstT1(null);
//                    matchSettleScore.setFirstT2(null);
//                    matchSettleScore.setSecondT1(null);
//                    matchSettleScore.setSecondT2(null);
//                    if(!matchSettleScore.getSettleNum().equals("1030")){
//                        matchSettleScore.setHomeAway("none");
//                    }
//                    if(matchSettleScore.getEventCode().equals("3019")||matchSettleScore.getEventCode().equals("3020")||matchSettleScore.getEventCode().equals("3021")||
//                            matchSettleScore.getEventCode().equals("3022")||matchSettleScore.getEventCode().equals("3023")||matchSettleScore.getEventCode().equals("3024")){
//                        matchSettleScore.setEventCode("fa_card");
//                        matchSettleScore.setHomeAway("none");
//                    }
//                }
//                matchSettleScore.setExtryInfo(null);
//                matchSettleScore.setSettleTimes(0);
//                matchSettleScore.setPlayerNameCode(null);
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.ROLL_BACK);
//                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
//                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
//                matchSettleScore.setSettleReasonDetail(null);
//                matchSettleScore.setFiveMinSection(null);
//                matchSettleScore.setSettleReason(null);
//                matchSettleEventMapper.updateByPrimaryKey(matchSettleScore);
//                log.info("事件Id::{}:: 当前事件被回滚,回滚后参数:{} ",matchSettleScoreDto.getEventId(),matchSettleEvent);
//
//                //将核对信息进行无效处理
//                matchSettleCheckService.rollbackEvent(matchSettleScore);
//                //1.日志
//                iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent,matchSettleScore,matchSettleScoreDto.getOperatorName(),
//                        OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE,matchSettleScoreDto.getIpAddress());
//                //2.MQ下发
//                matchSettleScoresProducer.sendMatchSettleEvent(matchSettleScore);
//                wsPushService.pushStandardSettleEvent(matchSettleScoreDto.getStandardMatchId(),
//                        matchSettleScoreDto.getEventCode());
//                //回滚新增记录
//                insertRollbackData(matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getEventId(),2,matchSettleScoreDto.getEventCode(),matchSettleScore.getSettleNum());
//                //回滚保存赛事ID一分钟
//                redisService.set("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getEventId(),matchSettleScoreDto.getEventId(),60);
//                return Response.success();
//        }catch (Exception e){
//            log.error("IFootballMatchScoresSettleApiImpl-rollBackSettleMatchEvent:",e);
//            return Response.failed(e.getMessage());
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }
//    //常规10001, 角球10002, 加时10003, 点球大战10004, 罚牌10005
//    @Override
//    public Response matchPeriodQuery(MatchPeriodQueryDto matchPeriodQueryDto) {
//        List<String> num = Arrays.asList("1011", "1012", "1013", "1014", "1015", "1016", "1017", "1018", "1019");
//        List<String> eventNum = Arrays.asList("201", "202", "203", "206", "207", "208");
//
//        List<String> settleNum = null;
//        //常规10001, 角球10002, 加时10003, 点球大战10004, 罚牌10005
//        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10001L) {
//            //settleNum=   Arrays.asList("101","102","103","104","105","106","107", "108","109","1010");
//            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
//            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
//            settleScoreSearchDto.setEventCode("goal");
//            settleScoreSearchDto.setOperatorName(matchPeriodQueryDto.getOperatorName());
//            List<MatchSettleScoreDto> matchSettleScoreDtos = searchMatchSettleScores(settleScoreSearchDto);
//
//            List<MatchSettleEventDto> matchSettleEventDtos = searchMatchSettleEvent(settleScoreSearchDto);
//            HashMap<String, Object> map = new HashMap<>();
//            if (matchSettleScoreDtos.size() != 0) {
//                matchSettleScoreDtos.removeIf(exe -> num.contains(exe.getSettleNum().toString()));
//                map.put("score",matchSettleScoreDtos);
//                //
//                matchSettleEventDtos.removeIf(exe -> eventNum.contains(exe.getSettleNum()));
//                map.put("event",matchSettleEventDtos);
//            }
//            //结算2.0 优化查询审核状态
//            matchSettleCheckService.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
//            return Response.success(map);
//
//
//        }
//        //角球10002L
//        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10002L) {
//            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
//            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
//            settleScoreSearchDto.setEventCode("corner");
//            settleScoreSearchDto.setOperatorName(matchPeriodQueryDto.getOperatorName());
//            List<MatchSettleScoreDto> matchSettleScoreDtos = searchMatchSettleScores(settleScoreSearchDto);
//
//            List<MatchSettleEventDto> matchSettleEventDtos = searchMatchSettleEvent(settleScoreSearchDto);
//            List<MatchSettleEventDto> eventDtos  =  new ArrayList<>();
//            if (matchSettleEventDtos.size() != 0 ) {
//                for (MatchSettleEventDto dto:matchSettleEventDtos) {
//                 //   String settle = dto.getSettleNum();
//                 //   if (eventNum.contains(settle)) {
//                        eventDtos.add(dto);
//                  //  }
//                }
//            }
//
//            HashMap<String, Object> map = new HashMap<>();
//            if (matchSettleScoreDtos.size() != 0 && eventDtos.size() != 0) {
//            map.put("score",matchSettleScoreDtos);
//            map.put("event",eventDtos);
//            }
//            matchSettleCheckService.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
//            return Response.success(map);
//
//        }
//        //加时赛事10003L
//        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10003L ) {
//            settleNum= num;
//            MatchSettleScoreExample example =new MatchSettleScoreExample();
//            example.createCriteria().andStandardMatchIdEqualTo(matchPeriodQueryDto.getStandardMatchId())
//                    .andSettleNumIn(settleNum);
//            example.setOrderByClause("settle_num desc");
//            List<MatchSettleScore> list =matchSettleScoreMapper.selectByExample(example);
//            List<MatchSettleScoreDto> matchSettleScoreDtos=new ArrayList<>();
//            for (MatchSettleScore matchSettleScore : list) {
//                MatchSettleScoreDto matchSettleScoreDto =new MatchSettleScoreDto();
//                BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
//                matchSettleScoreDto.setSettleNum(matchSettleScore.getSettleNum());
//                matchSettleScoreDto.setId(matchSettleScore.getId().toString());
//                matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
//                //查询阶段比分的时候要过滤角球 阶段比分 界面查询展示效果变更
//                matchSettleScoreDtos.add(matchSettleScoreDto);
//            }
//
//            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
//            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
//            settleScoreSearchDto.setEventCode("goal");
//            List<MatchSettleEventDto> matchSettleEventDtos = searchMatchSettleEvent(settleScoreSearchDto);
//
//            HashMap<String, Object> map = new HashMap<>();
//            if (matchSettleScoreDtos.size() != 0 && matchSettleEventDtos.size() != 0) {
//
//                map.put("score",matchSettleScoreDtos);
//                matchSettleEventDtos.removeIf(exe -> !eventNum.contains(exe.getSettleNum()));
//                map.put("event",matchSettleEventDtos);
//            }
//            matchSettleCheckService.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
//            return Response.success(map);
//
//        }
//        //点球大战10004
//        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10004L ) {
//            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
//            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
//            return   searchPenaltyScores(settleScoreSearchDto);
//        }
//        //罚牌10005
//        if (matchPeriodQueryDto.getCategorySetId().longValue() == 10005L) {
//            MatchSettleScoreSearchDto settleScoreSearchDto =new  MatchSettleScoreSearchDto();
//            settleScoreSearchDto.setStandardMatchId(matchPeriodQueryDto.getStandardMatchId());
//            settleScoreSearchDto.setEventCode("fa_card");
//            settleScoreSearchDto.setOperatorName(matchPeriodQueryDto.getOperatorName());
//            List<MatchSettleScoreDto> matchSettleScoreDtos = searchMatchSettleScores(settleScoreSearchDto);
//            List<MatchSettleEventDto> matchSettleEventDtos = searchMatchSettleEvent(settleScoreSearchDto);
//            HashMap<String, Object> map = new HashMap<>();
//            if (matchSettleScoreDtos.size() != 0) {
//                map.put("score",matchSettleScoreDtos);
//                map.put("event",matchSettleEventDtos);
//            }
//            matchSettleCheckService.searchCheckStatusByScoresList(matchSettleScoreDtos,matchPeriodQueryDto.getOperatorName());
//            return Response.success(map);
//
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response checkScoresOrEvent(MatchCheckSettleScoreEventDto dto) {
//        try {
//            //1.先查询得到对应结果
//            MatchSettleScore matchSettleScore = matchSettleScoreMapper.selectByPrimaryKey(dto.getId());
//            //2.进行核对
//            if (matchSettleScore != null) {
//                return checkMatchSettleScores(matchSettleScore, dto);
//            }
//
//            MatchSettleEvent matchSettleEvent = matchSettleEventMapper.selectByPrimaryKey(dto.getId());
//            if (matchSettleEvent != null) {
//                return checkMatchSettleEvent(matchSettleEvent, dto);
//            }
//            return Response.failed("比分不存在:" + dto.getId());
//        }catch (Exception e){
//            e.printStackTrace();
//            log.error("IFootballMatchScoresSettleApiImpl-checkScoresOrEvent:",e);
//        }
//        return Response.failed("比分异常");
//    }
//
//    private Response checkMatchSettleEvent(MatchSettleEvent matchSettleEvent, MatchCheckSettleScoreEventDto dto) {
//        MatchSettleCheckResultDto resultDto =new MatchSettleCheckResultDto();
//        resultDto.setId(matchSettleEvent.getId());
//        resultDto.setSettleNum(matchSettleEvent.getSettleNum());
//        resultDto.setCheckResult(0);
//        //检查比分
//        if(matchSettleEvent.getEventType()==1){
//            MatchSettleCheckEventDto before = new MatchSettleCheckEventDto();
//            MatchSettleCheckEventDto after = new MatchSettleCheckEventDto();
//            BeanUtils.copyProperties(matchSettleEvent,before);
//            BeanUtils.copyProperties(dto,after);
//            String jsonBefore = JSONObject.toJSONString(before);
//            String jsonAfter = JSONObject.toJSONString(after);
//            if(jsonBefore.equals(jsonAfter)){
//                resultDto.setCheckResult(1);
//            }
//        }else {
//            //检查 球员和进球方式
//            MatchSettleCheckExtryEventDto before = new MatchSettleCheckExtryEventDto();
//            MatchSettleCheckExtryEventDto after = new MatchSettleCheckExtryEventDto();
//            BeanUtils.copyProperties(matchSettleEvent,before);
//            BeanUtils.copyProperties(dto,after);
//            String jsonBefore = JSONObject.toJSONString(before);
//            String jsonAfter = JSONObject.toJSONString(after);
//            if(jsonBefore.equals(jsonAfter)){
//                resultDto.setCheckResult(1);
//            }
//        }
//        return Response.success(resultDto);
//    }
//
//    private Response checkMatchSettleScores(MatchSettleScore matchSettleScore, MatchCheckSettleScoreEventDto dto) {
//        MatchSettleCheckResultDto resultDto =new MatchSettleCheckResultDto();
//        resultDto.setId(matchSettleScore.getId());
//        resultDto.setSettleNum(matchSettleScore.getSettleNum());
//        resultDto.setCheckResult(0);
//        if(dto.getT1()!=null&&dto.getT2()!=null&&dto.getT1()==matchSettleScore.getT1()&&dto.getT2()==matchSettleScore.getT2()){
//            resultDto.setCheckResult(1);
//        }else if(matchSettleScore.getT1()==null&&matchSettleScore.getT2()==null){
//            if(dto.getT1()==null&&dto.getT2()==null){
//                if(dto.getExtryInfo()!=null&& dto.getExtryInfo().equals(matchSettleScore.getExtryInfo())){
//                    resultDto.setCheckResult(1);
//                }
//            }
//        }
//        return Response.success(resultDto);
//    }
//
//
//
//    @Override
//    public Response querySettleType(Long StandardMatchId) {
//
//        //1.查询结算信息
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(StandardMatchId);
//        if ( matchSettleInfo== null ||matchSettleInfo.getSettleType()==1) {
//            return   Response.success(1);
//        }else {
//            return   Response.success(2);
//        }
//    }
//
//    //玩法级重跑和冻结
//    @Override
//    public Response playCategoryFreezeAndReSettle(SettleQueryDTO settleQueryDTO) {
//        log.info("playCategoryFreezeAndReSettle param,settleQueryDTO: {}",settleQueryDTO);
//
//        //赛事级重跑
//        if (settleQueryDTO.getLevel().equals(1)) {
//            return matchReSettle(settleQueryDTO);
//        }
//
//        //玩法级重跑
//        if (settleQueryDTO.getLevel().equals(2) && settleQueryDTO.getExInfo().equals(2)) {
//            return categoryReSettle(settleQueryDTO);
//        }
//
//        //玩法级冻结
//        if (settleQueryDTO.getLevel().equals(2)  &&
//                (settleQueryDTO.getExInfo().equals(0)
//                        || settleQueryDTO.getExInfo().equals(1))) {
//            return categoryFreeze(settleQueryDTO);
//
//        }
//        return Response.failed();
//    }
//
//    //玩法级冻结/解冻
//    private Response categoryFreeze(SettleQueryDTO settleQueryDTO){
//        log.info("categoryFreeze param,settleQueryDTO: {}",settleQueryDTO);
//        MatchSettleScoreSearchDto settleScoreSearchDto =new MatchSettleScoreSearchDto();
//        settleScoreSearchDto.setStandardMatchId(settleQueryDTO.getMatchId());
//        CategoryDto categoryDto =new  CategoryDto();
//        if (settleQueryDTO.getPlayCategory().equals(1)) {
//            categoryDto.setGoal(1);
//            categoryDto.setCorner(0);
//            categoryDto.setFaCard(0);
//            settleScoreSearchDto.setEventCode("goal");
//        }else  if (settleQueryDTO.getPlayCategory().equals(2)) {
//            categoryDto.setGoal(0);
//            categoryDto.setCorner(1);
//            categoryDto.setFaCard(0);
//            settleScoreSearchDto.setEventCode("corner");
//        }else  if (settleQueryDTO.getPlayCategory().equals(3)) {
//            categoryDto.setGoal(0);
//            categoryDto.setCorner(0);
//            categoryDto.setFaCard(1);
//            settleScoreSearchDto.setEventCode("fa_card");
//        }
//        //查询比分
//        List<MatchSettleScoreDto> matchSettleScoreDtos = searchMatchSettleScores(settleScoreSearchDto);
//        //查询事件
//        List<MatchSettleEventDto> matchSettleEventDtos = searchMatchSettleEvent(settleScoreSearchDto);
//        //更新比分
//        List<MatchSettleScore> listScore = new ArrayList<>();
//        List<MatchSettleEvent> listEvent = new ArrayList<>();
//        for (MatchSettleScoreDto dto: matchSettleScoreDtos) {
//            MatchSettleScore matchSettleScore = new MatchSettleScore();
//            BeanUtils.copyProperties(dto,matchSettleScore);
//            matchSettleScore.setModifyTime(System.currentTimeMillis());
//            matchSettleScore.setId(Long.parseLong(dto.getId()));
//            matchSettleScore.setSettleFreeze(settleQueryDTO.getExInfo());
//           // matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//            listScore.add(matchSettleScore);
//        }
//
//        //更新事件
//        for (MatchSettleEventDto dto: matchSettleEventDtos) {
//            MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
//            BeanUtils.copyProperties(dto,matchSettleEvent);
//            matchSettleEvent.setModifyTime(System.currentTimeMillis());
//            matchSettleEvent.setId(Long.parseLong(dto.getId()));
//            matchSettleEvent.setSettleFreeze(settleQueryDTO.getExInfo());
//           // matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//            listEvent.add(matchSettleEvent);
//
//            //进球球员和进球方式冻结
//            if (settleQueryDTO.getPlayCategory().equals(1) && dto.getExtryEvent()!=null) {
//                MatchSettleEventDto extryEvent = dto.getExtryEvent();
//                MatchSettleEvent event = new MatchSettleEvent();
//                BeanUtils.copyProperties(extryEvent,event);
//                event.setModifyTime(System.currentTimeMillis());
//                event.setId(Long.parseLong(extryEvent.getId()));
//                event.setSettleFreeze(settleQueryDTO.getExInfo());
//                listEvent.add(event);
//            }
//
//            //角球特殊次序中的比分冻结
//            if(dto.getEventCode().equals("corner")
//                    &&(dto.getSettleNum().equals("201")
//                    ||dto.getSettleNum().equals("202")
//                    ||dto.getSettleNum().equals("203")
//                    ||dto.getSettleNum().equals("206")
//                    ||dto.getSettleNum().equals("207")
//                    ||dto.getSettleNum().equals("208"))){
//
//                MatchSettleScore matchSettleScore = new MatchSettleScore();
//                BeanUtils.copyProperties(dto,matchSettleScore);
//                matchSettleScore.setModifyTime(System.currentTimeMillis());
//                matchSettleScore.setId(Long.parseLong(dto.getId()));
//                matchSettleScore.setSettleFreeze(settleQueryDTO.getExInfo());
//                listScore.add(matchSettleScore);
//                continue;
//            }
//        }
//
//
//        if (listScore.size() != 0) {
//            iMatchSettleScoreEventMapper.updateScoreByList(listScore);
//        }
//
//        if (listEvent.size() != 0) {
//            iMatchSettleScoreEventMapper.updateEventByList(listEvent);
//        }
//
//
//        //更新结算信息
//        MatchSettleInfo settleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleQueryDTO.getMatchId());
//        if (settleInfo == null) {
//            return Response.failed("1031942");
//        }
//
//        String categoryFreezeStatus = settleInfo.getCategoryFreezeStatus();
//        String forwText ="-";
//        if (!StringUtils.isBlank(categoryFreezeStatus)) {
//            CategoryDto category = JSON.parseObject(categoryFreezeStatus, CategoryDto.class);
//            //更新玩法级的冻结状态
//            if (settleQueryDTO.getPlayCategory().equals(1)) {
//                //记录操作前的冻结状态
//                if (category.getGoal() != null && category.getGoal()==1){
//                    forwText = OperateLogTypeEnum.type_1.getCode().toString();
//                }else{
//                    forwText = OperateLogTypeEnum.type_2.getCode().toString();
//                }
//                //更新进球状态
//                category.setGoal(settleQueryDTO.getExInfo());
//            }else if (settleQueryDTO.getPlayCategory().equals(2)) {
//                //记录操作前的冻结状态
//                if (category.getCorner() != null && category.getCorner()==1){
//                    forwText = OperateLogTypeEnum.type_1.getCode().toString();
//                }else{
//                    forwText = OperateLogTypeEnum.type_2.getCode().toString();
//                }
//                //更新角球状态
//                category.setCorner(settleQueryDTO.getExInfo());
//            }else if (settleQueryDTO.getPlayCategory().equals(3)) {
//               //记录操作前的冻结状态
//                if (category.getFaCard() != null && category.getFaCard()==1){
//                    forwText = OperateLogTypeEnum.type_1.getCode().toString();
//                }else{
//                    forwText = OperateLogTypeEnum.type_2.getCode().toString();
//                }
//                //更新罚牌状态
//                category.setFaCard(settleQueryDTO.getExInfo());
//            }
//
//            String categoryString = JSON.toJSONString(category);
//            settleInfo.setCategoryFreezeStatus(categoryString);
//            settleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        }else{
//            String jsonString = JSON.toJSONString(categoryDto);
//            settleInfo.setCategoryFreezeStatus(jsonString);
//        }
//        matchSettleInfoRepository.updateMatchSettleInfoToRedis(settleInfo,false);
//
//        MatchFreezeMessage matchFreezeMessage = new MatchFreezeMessage();
//        matchFreezeMessage.setMatchId(settleQueryDTO.getMatchId());
//        matchFreezeMessage.setLevel(settleQueryDTO.getLevel());
//        matchFreezeMessage.setPlayCategory(settleQueryDTO.getPlayCategory());
//        matchFreezeMessage.setSportId(settleQueryDTO.getSportId());
//        matchFreezeMessage.setFreezeSettleStatus(settleQueryDTO.getExInfo());
//        matchFreezeMessage.setMins(settleQueryDTO.getMins());
//        matchFreezeMessage.setFreezeTime(settleQueryDTO.getFreezeTime());
//        matchFreezeMessage.setCreateTime(settleQueryDTO.getCreateTime());
//        matchSettleCenterProducer.MatchFreeze(matchFreezeMessage,"玩法级冻结/解冻");
//        //7.日志
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleQueryDTO.getMatchId());
//        iMatchSettleLogService.categoryReSettleAddLog(settleQueryDTO,forwText);
//
//        matchSettleCenterProducer.doSendLogToRiskByType(standardMatchInfo,settleQueryDTO,forwText);
//        return Response.success();
//    }
//
//    //玩法级重跑
//   private Response categoryReSettle(SettleQueryDTO settleQueryDTO){
//        log.info("categoryReSettle param,settleQueryDTO: {}",settleQueryDTO);
//
//       MatchSettleScoreSearchDto settleScoreSearchDto =new MatchSettleScoreSearchDto();
//       settleScoreSearchDto.setStandardMatchId(settleQueryDTO.getMatchId());
//       if (settleQueryDTO.getPlayCategory().equals(1)) {
//           settleScoreSearchDto.setEventCode("goal");
//       }else  if (settleQueryDTO.getPlayCategory().equals(2)) {
//           settleScoreSearchDto.setEventCode("corner");
//       }else  if (settleQueryDTO.getPlayCategory().equals(3)) {
//           settleScoreSearchDto.setEventCode("fa_card");
//       }
//       //查询比分
//       List<MatchSettleScoreDto> matchSettleScoreDtos = searchMatchSettleScores(settleScoreSearchDto);
//       //查询事件
//       List<MatchSettleEventDto> matchSettleEventDtos = searchMatchSettleEvent(settleScoreSearchDto);
//       List<MatchSettleScore> listScore = new ArrayList<>();
//       //更新比分
//       for (MatchSettleScoreDto dto: matchSettleScoreDtos) {
//           MatchSettleScore matchSettleScore = new MatchSettleScore();
//           BeanUtils.copyProperties(dto,matchSettleScore);
//           matchSettleScore.setModifyTime(System.currentTimeMillis());
//           matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//           matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes());
//           matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
//           matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
//           listScore.add(matchSettleScore);
//
//       }
//       if (listScore.size() != 0) {
//           iMatchSettleScoreEventMapper.updateScoreByList(listScore);
//       }
//
//       //更新事件
//       List<MatchSettleEvent> listEvent = new ArrayList<>();
//       for (MatchSettleEventDto dto: matchSettleEventDtos) {
//           MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
//           BeanUtils.copyProperties(dto,matchSettleEvent);
//           matchSettleEvent.setModifyTime(System.currentTimeMillis());
//           matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//           matchSettleEvent.setSettleTimes(dto.getSettleTimes());
//           matchSettleEvent.setOperater(settleQueryDTO.getOperatorName());
//           matchSettleEvent.setUserid(settleQueryDTO.getOperatorId());
//           listEvent.add(matchSettleEvent);
//       }
//
//       if (listEvent.size() != 0) {
//           iMatchSettleScoreEventMapper.updateEventByList(listEvent);
//       }
//
//       //6.结算比分下发
//       MatchSettleScoreMessage matchSettleScore=new MatchSettleScoreMessage();
//       matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
//       matchSettleScore.setSportId(settleQueryDTO.getSportId());
//       matchSettleScore.setLevel(settleQueryDTO.getLevel());
//       matchSettleScore.setPlayCategory(settleQueryDTO.getPlayCategory());
//       matchSettleScore.setSettleNum("0");
//       matchSettleScore.setOperateType(3);
//       matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//
//       //7.结算事件下发
//       MatchSettleEventMessage matchSettleEvent =new MatchSettleEventMessage();
//       matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
//       matchSettleEvent.setSportId(settleQueryDTO.getSportId());
//       matchSettleEvent.setLevel(settleQueryDTO.getLevel());
//       matchSettleEvent.setPlayCategory(settleQueryDTO.getPlayCategory());
//       matchSettleEvent.setSettleNum("0");
//       matchSettleEvent.setOperateType(3);
//       matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
//
//       //7.日志
//       iMatchSettleLogService.categoryReSettleAddLog(settleQueryDTO,"-");
//       return Response.success();
//   }
//
//   //赛事级重跑
//   private Response matchReSettle(SettleQueryDTO settleQueryDTO){
//        log.info("matchReSettle param,settleQueryDTO: {}",settleQueryDTO);
//        //1.查询比分
//        MatchSettleScoreExample example =new MatchSettleScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId());
//        example.setOrderByClause("settle_num desc");
//        List<MatchSettleScore> list =matchSettleScoreMapper.selectByExample(example);
//        //2.更新比分
//        for (MatchSettleScore matchSettleScore: list) {
//            matchSettleScore.setModifyTime(System.currentTimeMillis());
//            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//            matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes());
//            matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
//            matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
//            //matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//        }
//            iMatchSettleScoreEventMapper.updateScoreByList(list);
//        //3.批量查询事件
//        List<Long> periods=new ArrayList<>();
//        periods.add(6l);periods.add(7l);periods.add(41l);periods.add(42l);
//        MatchSettleEventExample matchSettleEventExample =new MatchSettleEventExample();
//        matchSettleEventExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId())
//              .andPeriodIdIn(periods);
//        matchSettleEventExample.setOrderByClause("settle_num desc,event_order desc");
//        List<MatchSettleEvent> eventList =matchSettleEventMapper.selectByExample(matchSettleEventExample);
//        if(eventList.size()==0){
//            return Response.failed("1031935");
//        }
//        //4.批量更新事件
//        for (MatchSettleEvent event :eventList) {
//            event.setModifyTime(System.currentTimeMillis());
//            event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
//            event.setOperater(settleQueryDTO.getOperatorName());
//            event.setUserid(settleQueryDTO.getOperatorId());
//            //matchSettleEventMapper.updateByPrimaryKey(event);
//        }
//       int i = iMatchSettleScoreEventMapper.updateEventByList(eventList);
//
//        MatchSettleEventMessage matchSettleEvent =new MatchSettleEventMessage();
//        matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
//        matchSettleEvent.setSportId(settleQueryDTO.getSportId());
//        matchSettleEvent.setLevel(settleQueryDTO.getLevel());
//        matchSettleEvent.setSettleNum("0");
//        matchSettleEvent.setOperateType(3);
//        //5.结算事件下发
//        matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
//
//
//        //6.结算比分下发
//        MatchSettleScoreMessage matchSettleScore=new MatchSettleScoreMessage();
//        matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
//        matchSettleScore.setSportId(settleQueryDTO.getSportId());
//        matchSettleScore.setLevel(settleQueryDTO.getLevel());
//        matchSettleScore.setSettleNum("0");
//        matchSettleScore.setOperateType(3);
//        matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//
//        //7.日志
//        iMatchSettleLogService.matchReSettleAddLog(settleQueryDTO);
//
//        return Response.success();
//    }
//
//    /**
//     * 新增回滚数据
//     * @param standardMatchId
//     * @param scoreEventId
//     * @param type
//     */
//    private void insertRollbackData(Long standardMatchId,Long scoreEventId,Integer type,String eventCode,String settleNum){
//        log.info("insertRollbackData param,standardMatchId: {},scoreEventId: {},type: {},eventCode: {},settleNum: {}",standardMatchId,scoreEventId,type,eventCode,settleNum);
//        MatchSettleRollBackInfo oldInfo = matchSettleRollBackInfoRepository.getMatchSettleRollBackInfo(scoreEventId);
//        Integer isPenalty =0;
//        if(settleNum.equals("1030")||settleNum.equals("1029")||settleNum.equals("1028")){
//            isPenalty=1;
//        }
//        //多次回滚，存在更新，不存在新增
//        if(oldInfo != null){
//            oldInfo.setRollBackStatus(1);
//            oldInfo.setRollBackOrderCount(0l);
//            oldInfo.setOrderCount(0l);
//            oldInfo.setRollBackTime(System.currentTimeMillis());
//            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(oldInfo,false);
//        } else {
//            MatchSettleRollBackInfo info = new MatchSettleRollBackInfo();
//            info.setId(scoreEventId);
//            info.setSettleScoreEventId(scoreEventId);
//            info.setDataType(type);
//            info.setRollBackStatus(1);
//            info.setRollBackTime(System.currentTimeMillis());
//            info.setStandardMatchId(standardMatchId);
//            info.setCreateTime(System.currentTimeMillis());
//            info.setModifyTime(System.currentTimeMillis());
//            info.setEventCode(eventCode);
//            info.setIsDianQiu(isPenalty);
//            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,true);
//        }
//    }
//
//    /**
//     * 查询比分回滚状态
//     * @param scores
//     * @param stndardMatchId
//     */
//    private void setRollBackStatusScores(List<MatchSettleScoreDto> scores,Long stndardMatchId){
//        if(scores != null && scores.size() > 0){
//            List<MatchSettleRollBackInfo> list =matchSettleRollBackInfoRepository.getMatchSettleRollBackInfoByStandardMatchId(stndardMatchId);
//            Map<String,MatchSettleRollBackInfo> map =new HashMap<>();
//            for (MatchSettleRollBackInfo matchSettleRollBackInfo : list) {
//                map.put(matchSettleRollBackInfo.getId().toString(),matchSettleRollBackInfo);
//            }
//            for (MatchSettleScoreDto score : scores) {
//                MatchSettleRollBackInfo info =map.get(score.getId());
//                if(info!=null){
//                    score.setRollBackStatus(info.getRollBackStatus());
//                    score.setRollBackOrderCount(info.getRollBackOrderCount());
//                }
//            }
//        }
//    }
//
//    /**
//     * 查询事件回滚状态
//     * @param events
//     * @param stndardMatchId
//     */
//    private void setRollBackStatusEvent(List<MatchSettleEventDto> events,Long stndardMatchId){
//        if(events != null && events.size() > 0){
//            List<MatchSettleRollBackInfo> list =matchSettleRollBackInfoRepository.getMatchSettleRollBackInfoByStandardMatchId(stndardMatchId);
//            Map<String,MatchSettleRollBackInfo> map=new HashMap<>();
//            for (MatchSettleRollBackInfo info : list) {
//                map.put(info.getId().toString(),info);
//            }
//            for (MatchSettleEventDto score : events) {
//                MatchSettleRollBackInfo info =map.get(score.getId());
//                if(info!=null){
//                    score.setRollBackStatus(info.getRollBackStatus());
//                    score.setRollBackOrderCount(info.getRollBackOrderCount());
//                }
//            }
//        }
//    }
//    /**
//     * 点球大战查询回滚状态
//     * @param penaltyScoresVo
//     * @param stndardMatchId
//     */
//    private void setRollBackStatusPenalty(PenaltyScoresVo penaltyScoresVo,Long stndardMatchId){
//            List<MatchSettleRollBackInfo> list =matchSettleRollBackInfoRepository.getMatchSettleRollBackInfoByStandardMatchId(stndardMatchId);
//            Map<String,MatchSettleRollBackInfo> map= new HashMap<>();
//            for (MatchSettleRollBackInfo info : list) {
//                map.put(info.getId().toString(),info);
//            }
//            List<MatchSettleEventDto> homeEventList = penaltyScoresVo.getHomeEventList();
//            List<MatchSettleEventDto> awayEventList = penaltyScoresVo.getAwayEventList();
//            MatchSettleEventDto  homeAway5RoundEvent = penaltyScoresVo.getHomeAway5RoundEvent();
//            MatchSettleEventDto  homeAwayAllRoundEvent = penaltyScoresVo.getHomeAwayAllRoundEvent();
//            for (MatchSettleEventDto matchSettleEventDto : homeEventList) {
//                MatchSettleRollBackInfo info= map.get(matchSettleEventDto.getId());
//                if(info!=null){
//                    matchSettleEventDto.setRollBackStatus(info.getRollBackStatus());
//                    matchSettleEventDto.setRollBackOrderCount(info.getRollBackOrderCount());
//                }
//            }
//            for (MatchSettleEventDto matchSettleEventDto : awayEventList) {
//                MatchSettleRollBackInfo info= map.get(matchSettleEventDto.getId());
//                if(info!=null){
//                    matchSettleEventDto.setRollBackStatus(info.getRollBackStatus());
//                    matchSettleEventDto.setRollBackOrderCount(info.getRollBackOrderCount());
//                }
//            }
//            MatchSettleRollBackInfo info5= map.get(homeAway5RoundEvent.getId());
//            if(info5!=null){
//                homeAway5RoundEvent.setRollBackStatus(info5.getRollBackStatus());
//                homeAway5RoundEvent.setRollBackOrderCount(info5.getRollBackOrderCount());
//            }
//            MatchSettleRollBackInfo infoAll= map.get(homeAwayAllRoundEvent.getId());
//            if(infoAll!=null){
//                homeAwayAllRoundEvent.setRollBackStatus(infoAll.getRollBackStatus());
//                homeAwayAllRoundEvent.setRollBackOrderCount(infoAll.getRollBackOrderCount());
//            }
//
//    }
//
//    //设置五分钟阶段玩法数据
//    private List<MatchSettleScoreDto> setFiveMinList(Long matchId,List<MatchSettleScoreDto> matchSettleScoreDtos){
//        MatchSettleInfo settleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchId);
//        List<MatchSettleScoreDto> scoreList = new ArrayList<>();
//        if (settleInfo!=null){
//
//            if(settleInfo.getFiveMinSwitch() != null && settleInfo.getFiveMinSwitch() == 1){
//                List<MatchSettleScoreDto> oneList = new ArrayList<>();
//                List<MatchSettleScoreDto> twoList = new ArrayList<>();
//                List<MatchSettleScoreDto> threeList = new ArrayList<>();
//                List<MatchSettleScoreDto> fourList = new ArrayList<>();
//                List<MatchSettleScoreDto> fiveList = new ArrayList<>();
//                List<MatchSettleScoreDto> sixList = new ArrayList<>();
//                for (int i=0;i<matchSettleScoreDtos.size();i++) {
//                    MatchSettleScoreDto matchSettleScoreDto = matchSettleScoreDtos.get(i);
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036){
//                        oneList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1037 || settleNum == 1038 || settleNum == 1039){
//                        twoList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043){
//                        threeList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1044 || settleNum == 1045 || settleNum == 1046){
//                        fourList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1047 || settleNum == 1048 || settleNum == 1049){
//                        fiveList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){
//                        sixList.add(matchSettleScoreDto);
//                    }
//                }
//                for (MatchSettleScoreDto matchSettleScoreDto : matchSettleScoreDtos) {
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 102){
//                        matchSettleScoreDto.setFiveMinList(oneList);
//                    }
//                    if(settleNum == 103){
//                        matchSettleScoreDto.setFiveMinList(twoList);
//                    }
//                    if(settleNum == 104){
//                        matchSettleScoreDto.setFiveMinList(threeList);
//                    }
//                    if(settleNum == 106){
//                        matchSettleScoreDto.setFiveMinList(fourList);
//                    }
//                    if(settleNum == 107){
//                        matchSettleScoreDto.setFiveMinList(fiveList);
//                    }
//                    if(settleNum == 108){
//                        matchSettleScoreDto.setFiveMinList(sixList);
//                    }
//                }
//
//                for (MatchSettleScoreDto score:matchSettleScoreDtos) {
//                    MatchSettleScoreDto matchSettleScoreDto = score;
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036
//                            || settleNum == 1037 || settleNum == 1038 || settleNum == 1039
//                            || settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043
//                            ||settleNum == 1044 || settleNum == 1045 || settleNum == 1046
//                            || settleNum == 1047 || settleNum == 1048 || settleNum == 1049
//                            || settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){
//
//                    } else {
//                        scoreList.add(score);
//                    }
//                }
//            } else {
//                for (MatchSettleScoreDto score:matchSettleScoreDtos) {
//                    MatchSettleScoreDto matchSettleScoreDto = score;
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036
//                            || settleNum == 1037 || settleNum == 1038 || settleNum == 1039
//                            || settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043
//                            ||settleNum == 1044 || settleNum == 1045 || settleNum == 1046
//                            || settleNum == 1047 || settleNum == 1048 || settleNum == 1049
//                            || settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){
//
//                    } else {
//                        scoreList.add(score);
//                    }
//                }
//            }
//        }
//
//        return scoreList;
//    }
//
//    private void obtainDetailInfo(MatchSettleScoreSearchDto settleScoreSearchDto, Map<String, Integer> deleteStatusMap, Map<String, Integer> dataMismatchMap){
//        SettleEventCodeEnum settleEventCodeEnum = SettleEventCodeEnum.getEventCodeEnum(settleScoreSearchDto.getEventCode());
//        if (settleEventCodeEnum == null) {
//            return;
//        }
//        MentionQueryRequest queryRequest = new MentionQueryRequest();
//        queryRequest.setMatchId(settleScoreSearchDto.getStandardMatchId());
//        Map<String, AbstractMentionStatus> mentionStatusMap = matchSettleService.getAllMentionStatus(queryRequest);
//        log.info("syncTest mentionStatusMap: {}", mentionStatusMap);
//        if (MapUtils.isEmpty(mentionStatusMap)) {
//            return;
//        }
//
//        if (mentionStatusMap.containsKey(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue())) {
//            AbstractMentionStatus.EventStatus eventStatus = mentionStatusMap.get(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue())
//                    .getDetailStatusFieldByEventCode(settleEventCodeEnum);
//            if(eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
//                deleteStatusMap.putAll(eventStatus.getDetailStatus());
//            }
//        }
//        if (mentionStatusMap.containsKey(SettleMentionEnum.FOOTBALL_SCORE_MISMATCH.getValue())) {
//            FootballMentionStatus.EventStatus eventStatus = mentionStatusMap.get(SettleMentionEnum.FOOTBALL_SCORE_MISMATCH.getValue())
//                    .getDetailStatusFieldByEventCode(settleEventCodeEnum);
//            if(eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
//                dataMismatchMap.putAll(eventStatus.getDetailStatus());
//            }
//        }
//    }
//
//    @Override
//    public List<MatchDelaySettleInfo> queryMatchDelaySettleInfoById(Long standardId) {
//        MatchDelaySettleInfoExample example = new MatchDelaySettleInfoExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardId);
//        List<MatchDelaySettleInfo> infos = matchDelaySettleInfoMapper.selectByExample(example);
//        return infos;
//    }
//
//
//
//    private void setDelaySettleSecond(Long standardMatchIfo,List<MatchSettleScoreDto> matchSettleScoreDtos){
//        MatchDelaySettleInfoExample example = new MatchDelaySettleInfoExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardMatchIfo);
//        List<MatchDelaySettleInfo> matchDelaySettleInfos = matchDelaySettleInfoMapper.selectByExample(example);
//        if (CollectionUtils.isNotEmpty(matchDelaySettleInfos)){
//            matchDelaySettleInfos.forEach(d->{
//                matchSettleScoreDtos.forEach(s->{
//                    if (d.getScoreId().toString().equals(s.getId())){
//                        if (s.getIsGrey()==null||s.getIsGrey()!=1){
//                            String key = "delaySettle:"+s.getId();
//                            Object second = redisService.get(key);
//                            if (null!=second){
//                                s.setDelayTimeSecond(Long.valueOf(second.toString()));
//                            }
//                        }
//
//                    }
//                });
//            });
//        }
//    }
//    private void setDelayEventSecond(Long standardMatchIfo,List<MatchSettleEventDto> matchSettleEventDtos){
//        MatchDelaySettleInfoExample example = new MatchDelaySettleInfoExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardMatchIfo);
//        List<MatchDelaySettleInfo> matchDelaySettleInfos = matchDelaySettleInfoMapper.selectByExample(example);
//        if (CollectionUtils.isNotEmpty(matchDelaySettleInfos)){
//            matchDelaySettleInfos.forEach(d->{
//                matchSettleEventDtos.forEach(s->{
//                    if (d.getScoreId().toString().equals(s.getId())){
//                        String key = "delaySettle:"+s.getId();
//                        Object second = redisService.get(key);
//                        if (null!=second){
//                            s.setDelayTimeSecond(Long.valueOf(second.toString()));
//                        }
//
//                    }
//                });
//            });
//        }
//    }
//    @Override
//    public void delayCheckCommonMatchSettleScoreEvent(Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo, boolean createCheck) {
//        log.info("eventId::{}:: job-checkCommonMatchSettleScoreEvent 事件比分核对开始", matchSettleCheckInfo.getThirdSettleScoreEventId());
//        matchSettleCheckServiceImpl.checkCommonMatchSettleScoreEvent(matchSettleScoreEvent,matchSettleCheckInfo,createCheck);
//    }
//}
