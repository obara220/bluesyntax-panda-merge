//package com.panda.merge.dubbo;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.panda.merge.api.ISettleCenterApi;
//import com.panda.merge.bo.GoalTypeBO;
//import com.panda.merge.bo.StandardPlayerLanguageBO;
//import com.panda.merge.common.enums.GoalTypeEnum;
//import com.panda.merge.common.enums.MatchPeriodEnum;
//import com.panda.merge.common.enums.OperateLogTypeEnum;
//import com.panda.merge.common.enums.StandardSportTypeEnum;
//import com.panda.merge.common.enums.YesNoEnum;
//import com.panda.merge.common.utils.TimeUtils;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.CommonConstant;
//import com.panda.merge.dao.StandardSportPlayerDao;
//import com.panda.merge.dto.*;
//import com.panda.merge.dto.advertise.*;
//import com.panda.merge.dto.message.MatchFreezeMessage;
//import com.panda.merge.dto.message.MatchSettleInfoMessage;
//import com.panda.merge.dto.settle.*;
//import com.panda.merge.mapper.*;
//import com.panda.merge.model.*;
//import com.panda.merge.mq.producer.MatchSettleCenterProducer;
//import com.panda.merge.mq.producer.MatchSettleScoresProducer;
//import com.panda.merge.respository.MatchSettleDataSourceConfigRepository;
//import com.panda.merge.respository.MatchSettleInfoRepository;
//import com.panda.merge.respository.StandardMatchInfoRepository;
//import com.panda.merge.service.*;
//import com.panda.merge.util.CategoryUtils;
//import com.panda.merge.utils.FootBallMatchSettleScoreUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.SETTLED;
//import static com.panda.merge.constant.RepositoryConstant.MATCH_SETTLE_INFO;
//import static com.panda.merge.constant.RepositoryConstant.REDIS_THREE_TIME;
//
///**
// * 结算2.0 dubbo服务
// */
//@Service
//@DubboService
//@Slf4j
//public class MatchSettleCenterApiImpl implements ISettleCenterApi {
//
//    @Autowired
//    IMatchSettleService matchSettleService;
//    @Autowired
//    private MatchSettleInfoMapper matchSettleInfoMapper;
//    @Autowired
//    MatchSettleCenterProducer matchSettleCenterProducer;
//    @Autowired
//    StandardSportPlayerDao standardSportPlayerDao;
//    @Autowired
//    LanguageInternationMapper languageInternationMapper;
//    @Autowired
//    LanguageInternationService languageInternationService;
//    @Autowired
//    MatchSettleScoreMapper matchSettleScoreMapper;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    IMatchSettleLogService iMatchSettleLogService;
//    @Autowired
//    MatchSettleEventMapper matchSettleEventMapper;
//    @Autowired
//    IMatchSettleLogService matchSettleLogService;
//    @Autowired
//    IWsPushService wsPushService;
//    @Autowired
//    MatchGrayIntervalMapper matchGrayIntervalMapper;
//    @Autowired
//    IMatchSettleScoreEventMapper iMatchSettleScoreEventMapper;
//    @Autowired
//    MatchSettleOperateLogMapper matchSettleOperateLogMapper;
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    IMatchSettleAbnormalMapper iMatchSettleAbnormalMapper;
//    @Autowired
//    MatchSettleScoresProducer matchSettleScoresProducer;
//    @Autowired
//    MatchSettleDataSourceConfigMapper matchSettleDataSourceConfigMapper;
//    @Autowired
//    MatchSettleDataSourceWeightConfigMapper matchSettleDataSourceWeightConfigMapper;
//    @Autowired
//    StandardSportMarketSellService standardSportMarketSellService;
//
//    @Autowired
//    StandardSportMarketSellMapper standardSportMarketSellMapper;
//
//
//
//    //系统级数据商自动结算key
//    private static final String AUTO_SETTLE_DATA_SOURCE_KEY = "auto_settle_data_source";
//
//    //结算同步比分中心开关key
//    private static final String SETTLE_SYNC_SCORES_KEY = "settle_sync_scores";
//
//    @Autowired
//    private IMatchSettleAbnormalMapper matchSettleAbnormalMapper;
//    @Autowired
//    private IBasketballInSettleService basketballInSettleService;
//    @Autowired
//    private final  static String REDIS_KEY_BASKET_IN_TIME_LIMIT= "REDIS_KEY_BASKET_IN_TIME_LIMIT";
//    @Autowired
//    MatchSettleInfoRepository matchSettleInfoRepository;
//    @Autowired
//    MatchSettleDataSourceConfigRepository matchSettleDataSourceConfigRepository;
//    @Autowired
//    StandardMatchInfoService standardMatchInfoService;
//    @Override
//    public Response MatchFreeze(MatchFreezeDto matchFreezeDto) {
//        log.info("[MatchSettleCenterApiImpl] MatchFreeze with linkId:{} and param:{} start!",matchFreezeDto.getLinkedId(), matchFreezeDto);
//        String key = CommonConstant.MATCH_PHASE_SCORE_SETTLE+ matchFreezeDto.getMatchId();
//        try {
//            if(redisService.tryLock(key,key,2,5)) {
//                MatchSettleInfo oidMatchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchFreezeDto.getMatchId());
//                if (oidMatchSettleInfo == null) {
//                    return Response.failed("没有找到结算信息 id: " + matchFreezeDto.getMatchId());
//                }
//                String forw = "";
//                String categoryFreezeStatus = null;
//
//                if (oidMatchSettleInfo.getFreezeStatus() == null || oidMatchSettleInfo.getFreezeStatus() == 0) {
//                    forw = "10002";
//                } else {
//                    if (matchFreezeDto.getMins() == null || matchFreezeDto.getMins() == 0) {
//                        forw = "10001";
//                    } else {
//                        //按照分钟冻结  修改前展示 -
//                        forw = "-";
//                    }
//
//                }
//
//                //赛事冻结后 把玩法冻结状态也标记
//                if (matchFreezeDto.getFreezeSettleStatus().equals(1)) {
//                    categoryFreezeStatus = JSON.toJSONString(new CategoryDto(1, 1, 1));
//                } else {
//                    categoryFreezeStatus = JSON.toJSONString(new CategoryDto(0, 0, 0));
//                }
//
//                //2.更新结算信息
//                MatchSettleInfo matchSettleInfo = new MatchSettleInfo();
//                BeanUtils.copyProperties(oidMatchSettleInfo,matchSettleInfo);
//                matchSettleInfo.setCategoryFreezeStatus(categoryFreezeStatus);
//                matchSettleInfo.setFreezeStatus(matchFreezeDto.getFreezeSettleStatus());
//                matchSettleInfo.setModifyTime(System.currentTimeMillis());
//                matchSettleInfo.setId(oidMatchSettleInfo.getId());
//                matchSettleInfo.setSportId(matchFreezeDto.getSportId());
//                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//
//                //更新比分和事件次序中的冻结状态
//                MatchSettleEventExample example = new MatchSettleEventExample();
//                example.createCriteria().andStandardMatchIdEqualTo(matchFreezeDto.getMatchId());
//                List<MatchSettleEvent> matchSettleEvents = matchSettleEventMapper.selectByExample(example);
//                if (matchSettleEvents.size() != 0) {
//                    for (MatchSettleEvent event : matchSettleEvents) {
//                        event.setSettleFreeze(matchFreezeDto.getFreezeSettleStatus());
//                        event.setModifyTime(System.currentTimeMillis());
//
//                    }
//                    iMatchSettleScoreEventMapper.updateEventByList(matchSettleEvents);
//                }
//                MatchSettleScoreExample scoreExample = new MatchSettleScoreExample();
//                scoreExample.createCriteria().andStandardMatchIdEqualTo(matchFreezeDto.getMatchId());
//                List<MatchSettleScore> matchSettleScores = matchSettleScoreMapper.selectByExample(scoreExample);
//                if (matchSettleEvents.size() != 0) {
//                    for (MatchSettleScore score : matchSettleScores) {
//                        score.setSettleFreeze(matchFreezeDto.getFreezeSettleStatus());
//                        score.setModifyTime(System.currentTimeMillis());
//                        matchSettleScoreMapper.updateByPrimaryKeySelective(score);
//                    }
//                    iMatchSettleScoreEventMapper.updateScoreByList(matchSettleScores);
//                }
//
//                //3.MQ下发结算信息
//                MatchFreezeMessage freezeMessage = new MatchFreezeMessage();
//                BeanUtils.copyProperties(matchFreezeDto, freezeMessage);
//                freezeMessage.setLevel(1);
//                matchSettleCenterProducer.MatchFreeze(freezeMessage, "赛事冻结/解冻");
//                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchFreezeDto.getMatchId());
//                //记录操作日志
//                matchSettleLogService.matchFreezeAddLog(standardMatchInfo,matchSettleInfo, forw, matchFreezeDto);
//
//
//                //发给风控日志
//                matchSettleCenterProducer.doSendLogToRisk(standardMatchInfo,matchSettleInfo,freezeMessage,matchFreezeDto);
//
//                log.info("[MatchSettleCenterApiImpl] MatchFreeze with linkId:{} 赛事冻结逻辑结束", matchFreezeDto.getLinkId());
//                return Response.success("处理完毕! LinkId: " + matchFreezeDto.getLinkId());
//            } else {
//                return Response.failed("1031933");
//            }
//        } catch (Exception e) {
//            log.error("[MatchSettleCenterApiImpl] MatchFreeze with linkId:{} error:",matchFreezeDto.getLinkedId(), e);
//            return Response.failed();
//        } finally {
//            redisService.unLock(key,key);
//        }
//    }
//
//    @Override
//    public Response ScoresPeriodFreeze(ScoresPeriodFreezeDto scoresPeriodFreezeDto) {
//        log.info("::{}:: 比分阶段冻结开始  参数: {} ", scoresPeriodFreezeDto.getLinkId(), scoresPeriodFreezeDto.toString());
////        MatchPeriodEnum periodEnum=null;
////        if(scoresPeriodFreezeDto.getSportId().equals(1L)) {
////             periodEnum = MatchPeriodEnum.getEnum(scoresPeriodFreezeDto.getSettleNum());
////        }else {
////
////        }
////        if (periodEnum == null) {
////            return Response.failed("结算比分阶段编号有误!");
////        }
//
//        if (scoresPeriodFreezeDto.getMatchId() == 0) {
//            return Response.failed("比分阶段冻结,赛事id有误!");
//        }
//        MatchSettleScoreExample settleScoreExample = new MatchSettleScoreExample();
//        MatchSettleScoreExample.Criteria criteria = settleScoreExample.createCriteria();
//        criteria.andStandardMatchIdEqualTo(scoresPeriodFreezeDto.getMatchId());
//        criteria.andSportIdEqualTo(scoresPeriodFreezeDto.getSportId());
//        criteria.andSettleNumEqualTo(scoresPeriodFreezeDto.getSettleNum());
//        List<MatchSettleScore> matchSettleScores = matchSettleScoreMapper.selectByExample(settleScoreExample);
//        if (matchSettleScores.size() == 0) {
//            return Response.failed("比分阶段冻结,比分信息为空!");
//        }
//        MatchSettleScore settleScore = matchSettleScores.get(0);
//        Integer settleFreeze = settleScore.getSettleFreeze();
//        String forw = "";
//        if (settleFreeze == null || settleFreeze == 0) {
//            forw = "10002";
//        } else {
//            if (scoresPeriodFreezeDto.getMins() == null || scoresPeriodFreezeDto.getMins() == 0) {
//                forw = "10001";
//            } else {
//                //按照分钟冻结  修改前展示 -
//                forw = "-";
//            }
//        }
//
//
//        MatchSettleScore matchSettleScore = matchSettleScores.get(0);
//        matchSettleScore.setSettleFreeze(scoresPeriodFreezeDto.getFreezeStatus());
//        matchSettleScore.setModifyTime(System.currentTimeMillis());
//        matchSettleScoreMapper.updateByPrimaryKeySelective(matchSettleScore);
//
//        //MQ下发结算信息
//        MatchFreezeMessage freezeMessage = new MatchFreezeMessage();
//        BeanUtils.copyProperties(scoresPeriodFreezeDto, freezeMessage);
//        freezeMessage.setLinkId(scoresPeriodFreezeDto.getLinkId());
//        freezeMessage.setSettleNum(scoresPeriodFreezeDto.getSettleNum());
//        freezeMessage.setFreezeSettleStatus(scoresPeriodFreezeDto.getFreezeStatus());
//        freezeMessage.setSportId(scoresPeriodFreezeDto.getSportId());
//        freezeMessage.setMatchId(scoresPeriodFreezeDto.getMatchId());
//        freezeMessage.setOperatorName(scoresPeriodFreezeDto.getOperatorName());
//        freezeMessage.setOperatorId(scoresPeriodFreezeDto.getOperatorId());
//        freezeMessage.setLevel(3);
//        matchSettleCenterProducer.MatchFreeze(freezeMessage, "比分冻结/解冻");
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScore.getStandardMatchId());
//
//        //记录操作日志
//        matchSettleLogService.scoresPeriodFreezeAddLog(standardMatchInfo,matchSettleScore, forw, scoresPeriodFreezeDto);
//
//
//        matchSettleCenterProducer.operationLogScoresPeriodFreeze(standardMatchInfo,matchSettleScore, forw, scoresPeriodFreezeDto);
//        //WS 推送
////        ThreadUtils.addTaskThreadPool(new Thread(() ->  wsPushService.pushStandardSettleScores(matchSettleScore.getStandardMatchId(),
////                matchSettleScore.getEventCode())), "推送WS标准赛事结算比分" + matchSettleScore.getStandardMatchId());
//        wsPushService.pushStandardSettleScores(matchSettleScore.getStandardMatchId(),
//                matchSettleScore.getEventCode());
//        return Response.success();
//    }
//
//    @Override
//    public Response ScoresPeriodOrderFreeze(ScoresPeriodOrderFreezeDto freezeDto) {
//        log.info("::{}:: 比分阶段(次序)冻结开始  参数: {} ", freezeDto.getLinkId(), freezeDto.toString());
//        String eventId = freezeDto.getEventId();
//        if ("0".equals(eventId) || eventId == null || "".equals(eventId)) {
//            return Response.failed("比分阶段(次序)冻结 id不能为空! linkId:" + freezeDto.getLinkId());
//        }
//        Long eventsId = Long.parseLong(eventId);
//        MatchSettleEvent matchSettleEvent = matchSettleEventMapper.selectByPrimaryKey(eventsId);
//        if (matchSettleEvent == null) {
//            return Response.failed("比分阶段(次序)冻结 比分事件为空! linkId:" + freezeDto.getLinkId());
//        }
//        Integer settleFreeze = freezeDto.getFreezeStatus();
//        String forw = "";
//        if (settleFreeze == null || settleFreeze == 0) {
//            forw = "10002";
//        } else {
//            if (freezeDto.getMins() == null || freezeDto.getMins() == 0) {
//                forw = "10001";
//            } else {
//                //按照分钟冻结  修改前展示 -
//                forw = "-";
//            }
//
//        }
//
//        matchSettleEvent.setSettleFreeze(freezeDto.getFreezeStatus());
//        matchSettleEvent.setModifyTime(System.currentTimeMillis());
//        matchSettleEventMapper.updateByPrimaryKeySelective(matchSettleEvent);
//
//
//        //MQ下发结算信息
//        MatchFreezeMessage matchFreezeDto = new MatchFreezeMessage();
//        BeanUtils.copyProperties(freezeDto, matchFreezeDto);
//        matchFreezeDto.setLinkId(freezeDto.getLinkId());
//        matchFreezeDto.setSettleNum(matchSettleEvent.getSettleNum());
//        matchFreezeDto.setFreezeSettleStatus(freezeDto.getFreezeStatus());
//        matchFreezeDto.setSportId(freezeDto.getSportId());
//        matchFreezeDto.setMatchId(freezeDto.getMatchId());
//        matchFreezeDto.setEventOrder(matchSettleEvent.getEventOrder());
//        matchFreezeDto.setOperatorName(freezeDto.getOperatorName());
//        matchFreezeDto.setOperatorId(freezeDto.getOperatorId());
//        matchFreezeDto.setLevel(3);
//        matchSettleCenterProducer.MatchFreeze(matchFreezeDto, "1".equals(freezeDto.getFreezeStatus()) ? "冻结" : "取消冻结");
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleEvent.getStandardMatchId());
//        //添加操作日志
//        matchSettleLogService.scoresPeriodOrderFreeze(standardMatchInfo,matchSettleEvent, forw, freezeDto);
//        //发送风控冻结日志
//        matchSettleCenterProducer.operationLogScoresPeriodOrderFreeze( standardMatchInfo, matchSettleEvent,  forw,  freezeDto);
//        log.info("::{}:: 比分阶段(次序)冻结完毕 参数: {} ", freezeDto.getLinkId(), matchSettleEvent.toString());
//        //WS 推送
//        wsPushService.pushStandardSettleEvent(matchSettleEvent.getStandardMatchId(),
//                matchSettleEvent.getEventCode());
//        return Response.success();
//
//    }
//
//    @Override
//    public Response settleSwitcher(MatchSettleSwitcherDto matchSettleSwitcherDto) {
//        log.info("::{}:: 结算切换开始  参数: {} ", matchSettleSwitcherDto.getLinkId(), matchSettleSwitcherDto.toString());
//        String linkId = matchSettleSwitcherDto.getLinkId();
//        StandardSportMarketSell standardSportMarketSell =
//                getStandardSportMarketSell(matchSettleSwitcherDto.getMatchId());
//        //联赛等级为16不支持切换 2.0
//        if (standardSportMarketSell == null || standardSportMarketSell.getTournamentLevel().equals(16)) {
//            return Response.failed("1031945");
//        }
//        //1.查询结算信息
//        MatchSettleInfoExample matchSettleInfoExample = new MatchSettleInfoExample();
//        MatchSettleInfoExample.Criteria criteria = matchSettleInfoExample.createCriteria();
//        criteria.andIdEqualTo(matchSettleSwitcherDto.getMatchId());
//        List<MatchSettleInfo> matchSettleInfos = matchSettleInfoMapper.selectByExample(matchSettleInfoExample);
//
//
//        if (matchSettleInfos.size() == 0 && matchSettleSwitcherDto.getSettleType() == 1) {
//            return Response.failed("1031958");
//        }
//        if (matchSettleInfos.size() != 0 && matchSettleInfos.get(0).getSettleType() != null && matchSettleInfos.get(0).getSettleType().equals(2) && matchSettleSwitcherDto.getSettleType().equals(2)) {
//            return Response.failed("1031959");
//        }
//
//        log.info("::{}:: 查询结算信息: {} ", linkId, matchSettleInfos.size() == 0 ? null : matchSettleInfos);
//        //3.新增或更新结算信息, match_settle_info
//        MatchSettleInfo matchSettleInfo = null;
//        //记录修改前结算方式
//        Integer settleType = 1;
//        boolean createNewScore = false;
//        if (matchSettleInfos.size() != 0) {
//            matchSettleInfo = matchSettleInfos.get(0);
//            settleType = matchSettleInfo.getSettleType() != null ? matchSettleInfo.getSettleType() :1;
//        } else {
//            createNewScore = true;
//        }
//        matchSettleInfo = settleInfoInsertOrUpdate(matchSettleInfo, matchSettleSwitcherDto, linkId);
//
//        //5.结算2.0将下发比分初始化
//        if (matchSettleSwitcherDto.getSettleType() == 2 && createNewScore) {
//            //比分初始化
//            if (matchSettleInfo.getSportId().equals(1L)) {
//                matchSettleService.initMatchSettleScore(matchSettleInfo.getStandardMatchId());
//            } else if (matchSettleInfo.getSportId().equals(2L)) {
//                matchSettleService.initBasketballSettleScore(matchSettleInfo.getStandardMatchId());
//            }
//        }
//        //赛事增加结算2.0 标识
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleInfo.getStandardMatchId());
//        if (standardMatchInfo != null) {
//            StandardMatchInfo standardMatchInfoUpdate = new StandardMatchInfo();
//            standardMatchInfoUpdate.setRemark(matchSettleInfo.getSettleType() + "");
//
//            StandardMatchInfoExample exampleMatchInfo = new StandardMatchInfoExample();
//            exampleMatchInfo.createCriteria().andIdEqualTo(standardMatchInfo.getId());
//
//            standardMatchInfoService.updateByExampleSelective(standardMatchInfoUpdate, exampleMatchInfo);
//        }
//        //6.下发MQ给业务结算服务(topic: MATCH_SETTLE_TYPE )
//        MatchSettleInfoMessage matchSettleInfoMessage = new MatchSettleInfoMessage(linkId, matchSettleInfo.getSportId(), matchSettleInfo.getStandardMatchId(), matchSettleInfo.getSettleType());
//        matchSettleCenterProducer.pushMatchSettleType(matchSettleInfoMessage, matchSettleSwitcherDto.getLinkId());
//
//        //7.记录操作日志
//        iMatchSettleLogService.settleSwitcherAddLog(matchSettleInfo, matchSettleSwitcherDto, settleType);
//
//        return Response.success();
//    }
//
//    @Override
//    public Response goalType(String linkId, Long sportId) {
//        log.info("::{}:: 进球方式查询  参数: {} ", linkId, sportId);
//        GoalTypeEnum[] goalTypeEnum = GoalTypeEnum.values();
//        List<GoalTypeEnum> list = Arrays.stream(goalTypeEnum).collect(Collectors.toList());
//        ArrayList<GoalTypeBO> goalTypeBOS = new ArrayList<>();
//        for (GoalTypeEnum goalType : list) {
//            if (goalType.getCode() < 1000) {
//                GoalTypeBO goalTypeBO = new GoalTypeBO();
//                goalTypeBO.setCode(goalType.getCode());
//                goalTypeBO.setEnValue(goalType.getEnValue());
//                goalTypeBO.setZhValue(goalType.getZhValue());
//                goalTypeBOS.add(goalTypeBO);
//            }
//        }
//        return Response.success(goalTypeBOS);
//    }
//
//    @Override
//    public Response goalPlayer(String linkId, Long sportId, Long matchId) {
//
//        log.info("::{}:: 球员查询查询  参数: 赛种: {} 赛事id:{}", linkId, sportId, matchId);
//        StandardSportPlayerDTO dto = new StandardSportPlayerDTO();
//        if (matchId == 0 || sportId == 0 || matchId == null || sportId == null) {
//            return Response.success(null, "球员查询参数不正确, linkId: " + linkId);
//        }
//        dto.setMatchId(matchId);
//        dto.setThirdSportId(sportId);
//        List<StandardSportPlayerDo> standardSportPlayer = standardSportPlayerDao.selectPalyerMyMatchid(dto);
//        if (standardSportPlayer.size() == 0) {
//            return Response.success(null, "球员查询为空! linkId: " + linkId);
//        }
//        log.info("::{}:: 球员查询查询  参数: {} ", linkId, sportId, matchId, standardSportPlayer);
//        List<Long> nameCodes = standardSportPlayer.stream().map(StandardSportPlayerDo::getNameCode).collect(Collectors.toList());
//        log.info("::{}:: 球员查询查询  球种: {}  赛事id: {}  nameCode:{}", linkId, sportId, matchId, nameCodes);
//
//        //nameCode查询多语言
//        LanguageInternationExample languageInternation = new LanguageInternationExample();
//        LanguageInternationExample.Criteria languageInternationCriteria = languageInternation.createCriteria();
//        languageInternationCriteria.andNameCodeIn(nameCodes);
//        languageInternationCriteria.andLanguageTypeIn(Arrays.asList("en", "zs"));
//        List<LanguageInternation> list = languageInternationMapper.selectByExample(languageInternation);
//
//        if (list.size() == 0) {
//            return Response.failed("球员多语言查询为空! linkId: " + linkId);
//        }
//        List<StandardPlayerLanguageBO> playerLanguageBOS = new ArrayList<>();
//        for (int i = 0; i < standardSportPlayer.size(); i++) {
//            StandardPlayerLanguageBO standardPlayerLanguageBO = new StandardPlayerLanguageBO();
//            standardPlayerLanguageBO.setNameCode(standardSportPlayer.get(i).getNameCode().toString());
//            Map<String, String> map = new HashMap<>();
//            for (int j = 0; j < list.size(); j++) {
//                LanguageInternation language = list.get(j);
//                if (language.getNameCode().toString().equals(standardPlayerLanguageBO.getNameCode())) {
//                    map.put(language.getLanguageType(), language.getText());
//                }
//            }
//            standardPlayerLanguageBO.setNames(map);
//            playerLanguageBOS.add(standardPlayerLanguageBO);
//        }
//
//        log.info("::{}:: 球员查询查询结束,赛事id:{},返回参数:{}", linkId, matchId, playerLanguageBOS);
//        return Response.success(playerLanguageBOS, "查询完毕,linkId:" + linkId);
//    }
//
//
//    //新增或者更新结算信息
//    private MatchSettleInfo settleInfoInsertOrUpdate(MatchSettleInfo matchSettleInfo, MatchSettleSwitcherDto matchSettleSwitcherDto, String linkId) {
//
//        if (null == matchSettleInfo && matchSettleSwitcherDto.getSettleType() == 2) {
//            StandardSportMarketSell standardSportMarketSell = getStandardSportMarketSell(matchSettleSwitcherDto.getMatchId());
//            matchSettleInfo = new MatchSettleInfo();
//            matchSettleInfo.setSportId(matchSettleSwitcherDto.getSportId());
//            matchSettleInfo.setSettleType(2);
//            matchSettleInfo.setFreezeStatus(0);
//            matchSettleInfo.setStandardMatchId(matchSettleSwitcherDto.getMatchId());
//            matchSettleInfo.setCreateTime(System.currentTimeMillis());
//            matchSettleInfo.setModifyTime(System.currentTimeMillis());
//            matchSettleInfo.setIsmemo(0);
//            matchSettleInfo.setSettleOrderClosed(0);
//            if (matchSettleInfo.getSportId().equals(2l)) {
//                matchSettleInfo.setSettleOrderClosed(1);
//            }
//            if (StandardSportTypeEnum.FootBall.getCode().equals(matchSettleSwitcherDto.getSportId())) {
//                matchSettleInfo.setFiveMinSwitch(1);
//            } else {
//                matchSettleInfo.setFiveMinSwitch(0);
//            }
//
//            //结算设置操盘手
//            if (standardSportMarketSell != null && !StringUtils.isAnyEmpty(standardSportMarketSell.getLiveTrader())) {
//                matchSettleInfo.setLiveTrader(standardSportMarketSell.getLiveTrader());
//                matchSettleInfo.setLiveTraderId(standardSportMarketSell.getLiveTraderId());
//                ArrayList<String> objects = new ArrayList<>();
//                objects.add(standardSportMarketSell.getLiveTrader());
//                String LiveTrader = JSON.toJSONString(objects);
//                matchSettleInfo.setAllLiveTrader(LiveTrader);
//            }
//
//            //结算设置审核员  改传数组
//            if (standardSportMarketSell != null &&
//                    !StringUtils.isAnyEmpty(standardSportMarketSell.getAuditor())) {
//                JSONArray array = new JSONArray();
//                array.add(standardSportMarketSell.getAuditor());
//                matchSettleInfo.setAuditorJson(array.toJSONString());
//                //设置能操作的活跃审核员
//                JSONArray activeArray = new JSONArray();
//                if (org.apache.commons.lang3.StringUtils.isNotEmpty(matchSettleInfo.getLimitUserArray())) {
//                    JSONArray limitArray = JSONArray.parseArray(matchSettleInfo.getLimitUserArray());
//                    for (Object o : array) {
//                        if (!limitArray.contains(o.toString())) {
//                            activeArray.add(o.toString());
//                        }
//                    }
//                    matchSettleInfo.setAuditorActiveArray(activeArray.toJSONString());
//                } else {
//                    matchSettleInfo.setAuditorActiveArray(array.toJSONString());
//                }
//            }
//            //切换到2.0就初始化玩法级冻结状态
//            CategoryDto categoryFootballDto = new CategoryDto(0, 0, 0);
//            matchSettleInfo.setGoalAutoSettleDataSource(1);
//            switch (matchSettleSwitcherDto.getSportId().intValue()) {
//                case 1: //足球
//                    matchSettleInfo.setCategoryFreezeStatus(JSON.toJSONString(categoryFootballDto));
//                    break;
//                case 2: //篮球
//                    matchSettleInfo.setCategoryFreezeStatus(JSON.toJSONString(new CategoryBasketballDto().unFreeze()));
//                    matchSettleInfo.setGoalAutoSettleDataSource(0);
//                    break;
//                default:
//                    //2.0其他球种暂不支持,先默认给初始化足球
//                    matchSettleInfo.setCategoryFreezeStatus(JSON.toJSONString(categoryFootballDto));
//                    break;
//            }
//            //目前通过主键控制防止出现故障
//            matchSettleInfo.setId(matchSettleInfo.getStandardMatchId());
//
//            //设置赛事级自动结算开关
//            Object isAutoSettleDataSource = redisService.get(AUTO_SETTLE_DATA_SOURCE_KEY);
//            if (isAutoSettleDataSource != null && (boolean) isAutoSettleDataSource) {
//                matchSettleInfo.setIsAutoSettleDataSource(YesNoEnum.Y.value);
//            } else {
//                matchSettleInfo.setIsAutoSettleDataSource(YesNoEnum.N.value);
//            }
//            matchSettleInfo.setCornerAutoSettleDataSource(1);
//            matchSettleInfo.setBookingAutoSettleDataSource(1);
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,true);
//
//            //初始化异常结算
//            settleAbnormalInit(matchSettleInfo.getStandardMatchId(), matchSettleInfo.getSportId());
//
//            log.info("::{}:: 结算信息新增完毕: {} ", linkId, matchSettleInfo.toString());
//        } else if (matchSettleInfo == null && matchSettleSwitcherDto.getSettleType() == 1) {
//            log.info("::{}:: 结算切换参数有误: {} ", linkId, matchSettleSwitcherDto.getSettleType());
//            return matchSettleInfo;
//        } else {
//            if (matchSettleSwitcherDto.getSettleType() != null) {
//                matchSettleInfo.setSettleType(matchSettleSwitcherDto.getSettleType());
//            }
//            if (matchSettleSwitcherDto.getMatchId() != null) {
//                matchSettleInfo.setStandardMatchId(matchSettleSwitcherDto.getMatchId());
//            }
//            matchSettleInfo.setSportId(matchSettleSwitcherDto.getSportId());
//            matchSettleInfo.setModifyTime(System.currentTimeMillis());
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//            log.info("::{}:: 结算信息更新完毕: {} ", linkId, matchSettleInfo.toString());
//        }
//        return matchSettleInfo;
//    }
//
//    private StandardSportMarketSell getStandardSportMarketSell(Long standardMatchId) {
//        if (standardMatchId == null) {
//            return null;
//        }
//        //查询开售信息
//        List<Long> standardMatchIds = new ArrayList<>();
//        standardMatchIds.add(standardMatchId);
//        List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellService.getItems(standardMatchIds);
//        if (CollectionUtils.isEmpty(standardSportMarketSells)) {
//            return null;
//        }
//        return standardSportMarketSells.get(0);
//    }
//
//    //初始化异常结算
//    void settleAbnormalInit(Long standardMatchId, Long sportId) {
//        ArrayList<MatchSettleAbnormal> list = new ArrayList<>();
//        initMatchSettleAbnormalGoal(list, standardMatchId, sportId);
//        initMatchSettleAbnormalCorner(list, standardMatchId, sportId);
//        initMatchSettleAbnormalFaCard(list, standardMatchId, sportId);
//        matchSettleAbnormalMapper.insertByList(list);
//    }
//
//    /**
//     * 异常结算初始化进球
//     *
//     * @param standardMatchId
//     * @return
//     */
//    private ArrayList<MatchSettleAbnormal> initMatchSettleAbnormalGoal(ArrayList<MatchSettleAbnormal> list, Long standardMatchId, Long sportId) {
//        //  ArrayList<MatchSettleAbnormal> list = new ArrayList<>();
//        //上半场
//        MatchSettleAbnormal matchSettleAbnormal1 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal1.setEventCode("goal");
//        matchSettleAbnormal1.setSettleNum("105");
//        matchSettleAbnormal1.setEventName("1HT");
//        matchSettleAbnormal1.setPeriodId(31l);
//        matchSettleAbnormal1.setIsScores(1);
//        list.add(matchSettleAbnormal1);
//
//        //中场休息
///*
//        MatchSettleAbnormal matchSettleAbnormal2 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId,sportId);
//        matchSettleAbnormal2.setEventCode("goal");
//        matchSettleAbnormal2.setSettleNum("109");
//        matchSettleAbnormal2.setEventName("2HT");
//        matchSettleAbnormal2.setPeriodId(100l);
//        matchSettleAbnormal2.setIsScores(1);
//        list.add(matchSettleAbnormal2);
//*/
//
//        //下半场
//        MatchSettleAbnormal matchSettleAbnormal3 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal3.setEventCode("goal");
//        matchSettleAbnormal3.setSettleNum("109");
//        matchSettleAbnormal3.setEventName("2HT");
//        matchSettleAbnormal3.setPeriodId(100l);
//        matchSettleAbnormal3.setIsScores(1);
//        list.add(matchSettleAbnormal3);
//
//        //全场(常规赛)
//        MatchSettleAbnormal matchSettleAbnormal4 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal4.setEventCode("goal");
//        matchSettleAbnormal4.setSettleNum("1010");
//        matchSettleAbnormal4.setEventName("FT");
//        matchSettleAbnormal4.setPeriodId(100l);
//        matchSettleAbnormal4.setIsScores(1);
//        list.add(matchSettleAbnormal4);
//
//        //加时赛上半场
//        MatchSettleAbnormal matchSettleAbnormal5 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal5.setEventCode("goal");
//        matchSettleAbnormal5.setSettleNum("1014");
//        matchSettleAbnormal5.setEventName("1ET");
//        matchSettleAbnormal5.setPeriodId(33l);
//        matchSettleAbnormal5.setIsScores(1);
//        list.add(matchSettleAbnormal5);
//
//        //加时赛中场休息
///*        MatchSettleAbnormal matchSettleAbnormal5 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId,sportId);
//        matchSettleAbnormal5.setEventCode("goal");
//        matchSettleAbnormal5.setSettleNum("1014");
//        matchSettleAbnormal5.setEventName("1ET");
//        matchSettleAbnormal5.setPeriodId(33l);
//        matchSettleAbnormal5.setIsScores(1);
//        list.add(matchSettleAbnormal5);*/
//
//        //加时赛下半场
//        MatchSettleAbnormal matchSettleAbnormal6 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId,sportId);
//        matchSettleAbnormal6.setEventCode("goal");
//        matchSettleAbnormal6.setSettleNum("1018");
//        matchSettleAbnormal6.setEventName("2ET");
//        matchSettleAbnormal6.setPeriodId(110l);
//        matchSettleAbnormal6.setIsScores(1);
//        list.add(matchSettleAbnormal6);
//
//        //加时赛全场
//        MatchSettleAbnormal matchSettleAbnormal7 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal7.setEventCode("goal");
//        matchSettleAbnormal7.setSettleNum("1019");
//        matchSettleAbnormal7.setEventName("ET");
//        matchSettleAbnormal7.setPeriodId(110l);
//        matchSettleAbnormal7.setIsScores(1);
//        list.add(matchSettleAbnormal7);
//
//        //点球总比分
//        MatchSettleAbnormal matchSettleAbnormal8 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal8.setEventCode("goal");
//        matchSettleAbnormal8.setSettleNum("1028");
//        matchSettleAbnormal8.setEventName("Total PEN");
//        matchSettleAbnormal8.setPeriodId(120l);
//        matchSettleAbnormal8.setIsScores(2);
//        list.add(matchSettleAbnormal8);
//
//        //点球前5轮比分
//        MatchSettleAbnormal matchSettleAbnormal9 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal9.setEventCode("goal");
//        matchSettleAbnormal9.setSettleNum("1029");
//        matchSettleAbnormal9.setEventName("Total 1-5");
//        matchSettleAbnormal9.setPeriodId(50l);
//        matchSettleAbnormal9.setIsScores(2);
//        list.add(matchSettleAbnormal9);
//
//        //进球 0-15分钟
//        MatchSettleAbnormal matchSettleAbnormal10 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal10.setEventCode("goal");
//        matchSettleAbnormal10.setSettleNum("102");
//        matchSettleAbnormal10.setEventName("00:00 - 14:59");
//        matchSettleAbnormal10.setPeriodId(6l);
//        matchSettleAbnormal10.setIsScores(1);
//        list.add(matchSettleAbnormal10);
//
//        //进球 15-30分钟
//        MatchSettleAbnormal matchSettleAbnormal11 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal11.setEventCode("goal");
//        matchSettleAbnormal11.setSettleNum("103");
//        matchSettleAbnormal11.setEventName("15:00 - 29:59");
//        matchSettleAbnormal11.setPeriodId(6l);
//        matchSettleAbnormal11.setIsScores(1);
//        list.add(matchSettleAbnormal11);
//
//        //进球 30-45分钟
//        MatchSettleAbnormal matchSettleAbnormal12 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal12.setEventCode("goal");
//        matchSettleAbnormal12.setSettleNum("104");
//        matchSettleAbnormal12.setEventName("30:00 - 1HT");
//        matchSettleAbnormal12.setPeriodId(6l);
//        matchSettleAbnormal12.setIsScores(1);
//        list.add(matchSettleAbnormal12);
//
//        //进球 45-60分钟
//        MatchSettleAbnormal matchSettleAbnormal13 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal13.setEventCode("goal");
//        matchSettleAbnormal13.setSettleNum("106");
//        matchSettleAbnormal13.setEventName("1HT - 59:59");
//        matchSettleAbnormal13.setPeriodId(7l);
//        matchSettleAbnormal13.setIsScores(1);
//        list.add(matchSettleAbnormal13);
//
//        //进球 60-75分钟
//        MatchSettleAbnormal matchSettleAbnormal14 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal14.setEventCode("goal");
//        matchSettleAbnormal14.setSettleNum("107");
//        matchSettleAbnormal14.setEventName("60:00 - 74:59");
//        matchSettleAbnormal14.setPeriodId(7l);
//        matchSettleAbnormal14.setIsScores(1);
//        list.add(matchSettleAbnormal14);
//
//        //进球 75分钟-全场
//        MatchSettleAbnormal matchSettleAbnormal15 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal15.setEventCode("goal");
//        matchSettleAbnormal15.setSettleNum("108");
//        matchSettleAbnormal15.setEventName("75:00 - FT");
//        matchSettleAbnormal15.setPeriodId(7l);
//        matchSettleAbnormal15.setIsScores(1);
//        list.add(matchSettleAbnormal15);
//
//        return list;
//    }
//
//    /**
//     * 异常结算初始化角球
//     *
//     * @param standardMatchId
//     * @return
//     */
//    private ArrayList<MatchSettleAbnormal> initMatchSettleAbnormalCorner(ArrayList<MatchSettleAbnormal> list, Long standardMatchId, Long sportId) {
//        // ArrayList<MatchSettleAbnormal> list = new ArrayList<>();
//        //上半场
//        MatchSettleAbnormal matchSettleAbnormal1 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal1.setEventCode("corner");
//        matchSettleAbnormal1.setSettleNum("201");
//        matchSettleAbnormal1.setEventName("1HT CR");
//        matchSettleAbnormal1.setPeriodId(31l);
//        matchSettleAbnormal1.setIsScores(1);
//        list.add(matchSettleAbnormal1);
//
//        //中场休息
///*
//        MatchSettleAbnormal matchSettleAbnormal2 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId,sportId);
//        matchSettleAbnormal2.setEventCode("corner");
//        matchSettleAbnormal2.setSettleNum("109");
//        matchSettleAbnormal2.setEventName("2HT");
//        matchSettleAbnormal2.setPeriodId(100l);
//        matchSettleAbnormal2.setIsScores(1);
//        list.add(matchSettleAbnormal2);
//*/
//
//        //下半场
//        MatchSettleAbnormal matchSettleAbnormal3 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal3.setEventCode("corner");
//        matchSettleAbnormal3.setSettleNum("202");
//        matchSettleAbnormal3.setEventName("2HT CR");
//        matchSettleAbnormal3.setPeriodId(100l);
//        matchSettleAbnormal3.setIsScores(1);
//        list.add(matchSettleAbnormal3);
//
//        //全场(常规赛)
//        MatchSettleAbnormal matchSettleAbnormal4 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal4.setEventCode("corner");
//        matchSettleAbnormal4.setSettleNum("203");
//        matchSettleAbnormal4.setEventName("FT CR");
//        matchSettleAbnormal4.setPeriodId(100l);
//        matchSettleAbnormal4.setIsScores(1);
//        list.add(matchSettleAbnormal4);
//
//        //加时赛上半场
//        MatchSettleAbnormal matchSettleAbnormal5 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal5.setEventCode("corner");
//        matchSettleAbnormal5.setSettleNum("206");
//        matchSettleAbnormal5.setEventName("1ET CR");
//        matchSettleAbnormal5.setPeriodId(33l);
//        matchSettleAbnormal5.setIsScores(1);
//        list.add(matchSettleAbnormal5);
//
//        //加时赛中场休息
///*        MatchSettleAbnormal matchSettleAbnormal5 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId,sportId);
//        matchSettleAbnormal5.setEventCode("corner");
//        matchSettleAbnormal5.setSettleNum("1014");
//        matchSettleAbnormal5.setEventName("1ET");
//        matchSettleAbnormal5.setPeriodId(33l);
//        matchSettleAbnormal5.setIsScores(1);
//        list.add(matchSettleAbnormal5);*/
//
//        //加时赛下半场
//        MatchSettleAbnormal matchSettleAbnormal6 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal6.setEventCode("corner");
//        matchSettleAbnormal6.setSettleNum("207");
//        matchSettleAbnormal6.setEventName("2ET CR");
//        matchSettleAbnormal6.setPeriodId(110l);
//        matchSettleAbnormal6.setIsScores(1);
//        list.add(matchSettleAbnormal6);
//
//        //加时赛全场
//        MatchSettleAbnormal matchSettleAbnormal7 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal7.setEventCode("corner");
//        matchSettleAbnormal7.setSettleNum("208");
//        matchSettleAbnormal7.setEventName("ET CR");
//        matchSettleAbnormal7.setPeriodId(110l);
//        matchSettleAbnormal7.setIsScores(1);
//        list.add(matchSettleAbnormal7);
//
//        //角球 0 - 15分钟
//        MatchSettleAbnormal matchSettleAbnormal8 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal8.setEventCode("corner");
//        matchSettleAbnormal8.setSettleNum("2011");
//        matchSettleAbnormal8.setEventName("CR 00:00 - 14:59");
//        matchSettleAbnormal8.setPeriodId(6l);
//        matchSettleAbnormal8.setIsScores(1);
//        list.add(matchSettleAbnormal8);
//
//        //角球 15 - 30分钟
//        MatchSettleAbnormal matchSettleAbnormal9 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal9.setEventCode("corner");
//        matchSettleAbnormal9.setSettleNum("2012");
//        matchSettleAbnormal9.setEventName("CR 15:00 - 29:59");
//        matchSettleAbnormal9.setPeriodId(6l);
//        matchSettleAbnormal9.setIsScores(1);
//        list.add(matchSettleAbnormal9);
//
//        //角球 30 - 45分钟
//        MatchSettleAbnormal matchSettleAbnormal10 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal10.setEventCode("corner");
//        matchSettleAbnormal10.setSettleNum("2013");
//        matchSettleAbnormal10.setEventName("CR 30:00 - HT");
//        matchSettleAbnormal10.setPeriodId(6l);
//        matchSettleAbnormal10.setIsScores(1);
//        list.add(matchSettleAbnormal10);
//
//        //角球 45 - 60分钟
//        MatchSettleAbnormal matchSettleAbnormal11 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal11.setEventCode("corner");
//        matchSettleAbnormal11.setSettleNum("2014");
//        matchSettleAbnormal11.setEventName("CR HT - 59:59");
//        matchSettleAbnormal11.setPeriodId(7l);
//        matchSettleAbnormal11.setIsScores(1);
//        list.add(matchSettleAbnormal11);
//
//        //角球 60 - 75分钟
//        MatchSettleAbnormal matchSettleAbnormal12 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal12.setEventCode("corner");
//        matchSettleAbnormal12.setSettleNum("2015");
//        matchSettleAbnormal12.setEventName("CR 60:00 - 74:59");
//        matchSettleAbnormal12.setPeriodId(7l);
//        matchSettleAbnormal12.setIsScores(1);
//        list.add(matchSettleAbnormal12);
//
//        //角球 75 - 全场
//        MatchSettleAbnormal matchSettleAbnormal13 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal13.setEventCode("corner");
//        matchSettleAbnormal13.setSettleNum("2016");
//        matchSettleAbnormal13.setEventName("CR 75:00 - FT");
//        matchSettleAbnormal13.setPeriodId(7l);
//        matchSettleAbnormal13.setIsScores(1);
//        list.add(matchSettleAbnormal13);
//
//        return list;
//    }
//
//    /**
//     * 初始化罚牌
//     *
//     * @param standardMatchId
//     * @return
//     */
//    private ArrayList<MatchSettleAbnormal> initMatchSettleAbnormalFaCard(ArrayList<MatchSettleAbnormal> list, Long standardMatchId, Long sportId) {
//        // ArrayList<MatchSettleAbnormal> list = new ArrayList<>();
//        //上半场
//        MatchSettleAbnormal matchSettleAbnormal1 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal1.setEventCode("fa_card");
//        matchSettleAbnormal1.setSettleNum("304");
//        matchSettleAbnormal1.setEventName("BK 1HT");
//        matchSettleAbnormal1.setPeriodId(31l);
//        matchSettleAbnormal1.setIsScores(1);
//        list.add(matchSettleAbnormal1);
//
//        //中场休息
///*
//        MatchSettleAbnormal matchSettleAbnormal2 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId,sportId);
//        matchSettleAbnormal2.setEventCode("fa_card");
//        matchSettleAbnormal2.setSettleNum("109");
//        matchSettleAbnormal2.setEventName("2HT");
//        matchSettleAbnormal2.setPeriodId(100l);
//        matchSettleAbnormal2.setIsScores(1);
//        list.add(matchSettleAbnormal2);
//*/
//
//        //下半场
//        MatchSettleAbnormal matchSettleAbnormal3 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal3.setEventCode("fa_card");
//        matchSettleAbnormal3.setSettleNum("308");
//        matchSettleAbnormal3.setEventName("BK 2HT");
//        matchSettleAbnormal3.setPeriodId(100l);
//        matchSettleAbnormal3.setIsScores(1);
//        list.add(matchSettleAbnormal3);
//
//        //全场(常规赛)
//        MatchSettleAbnormal matchSettleAbnormal4 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal4.setEventCode("fa_card");
//        matchSettleAbnormal4.setSettleNum("309");
//        matchSettleAbnormal4.setEventName("BK FT");
//        matchSettleAbnormal4.setPeriodId(100l);
//        matchSettleAbnormal4.setIsScores(1);
//        list.add(matchSettleAbnormal4);
//
//        //加时赛上半场
//        MatchSettleAbnormal matchSettleAbnormal5 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal5.setEventCode("fa_card");
//        matchSettleAbnormal5.setSettleNum("3013");
//        matchSettleAbnormal5.setEventName("1ET BK");
//        matchSettleAbnormal5.setPeriodId(33l);
//        matchSettleAbnormal5.setIsScores(1);
//        list.add(matchSettleAbnormal5);
//
//        //加时赛中场休息
///*        MatchSettleAbnormal matchSettleAbnormal5 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId,sportId);
//        matchSettleAbnormal5.setEventCode("fa_card");
//        matchSettleAbnormal5.setSettleNum("1014");
//        matchSettleAbnormal5.setEventName("1ET");
//        matchSettleAbnormal5.setPeriodId(33l);
//        matchSettleAbnormal5.setIsScores(1);
//        list.add(matchSettleAbnormal5);*/
//
//        //加时赛下半场
//        MatchSettleAbnormal matchSettleAbnormal6 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal6.setEventCode("fa_card");
//        matchSettleAbnormal6.setSettleNum("3017");
//        matchSettleAbnormal6.setEventName("2ET BK");
//        matchSettleAbnormal6.setPeriodId(110l);
//        matchSettleAbnormal6.setIsScores(1);
//        list.add(matchSettleAbnormal6);
//
//        //加时赛全场
//        MatchSettleAbnormal matchSettleAbnormal7 = FootBallMatchSettleScoreUtils.initMatchSettleAbnormal(standardMatchId, sportId);
//        matchSettleAbnormal7.setEventCode("fa_card");
//        matchSettleAbnormal7.setSettleNum("3018");
//        matchSettleAbnormal7.setEventName("ET BK");
//        matchSettleAbnormal7.setPeriodId(110l);
//        matchSettleAbnormal7.setIsScores(1);
//        list.add(matchSettleAbnormal7);
//
//        return list;
//    }
//
//    @Override
//    public Response getGlobalAutoSettleStatus() {
//        Object object = redisService.get(AUTO_SETTLE_DATA_SOURCE_KEY);
//        return Response.success(object != null ? (boolean) object : false);
//    }
//
//    @Override
//    public Response changeGlobalAutoSettleStatus(Boolean isEnableAutoSettle, String userName, String ipAddress) {
//        //查询未修改前的系统状态
//        Object object = redisService.get(AUTO_SETTLE_DATA_SOURCE_KEY);
//        boolean status = object != null ? (boolean) object : false;
//
//        //每次更新刷新过期时间设置成10年
//        redisService.set(AUTO_SETTLE_DATA_SOURCE_KEY, isEnableAutoSettle, 60 * 60 * 24 * 30 * 12 * 10);
//
//        //全量更新match_settle_info
//        MatchSettleInfo record = new MatchSettleInfo();
//        record.setIsAutoSettleDataSource(isEnableAutoSettle ? 1 : 0);
//        record.setBookingAutoSettleDataSource(record.getIsAutoSettleDataSource());
//        record.setCornerAutoSettleDataSource(record.getIsAutoSettleDataSource());
//        record.setGoalAutoSettleDataSource(record.getIsAutoSettleDataSource());
//        record.setModifyTime(System.currentTimeMillis());
//        MatchSettleInfoExample settleInfoExample = new MatchSettleInfoExample();
//        settleInfoExample.createCriteria().andSportIdEqualTo(1L);
//        matchSettleInfoMapper.updateByExampleSelective(record, settleInfoExample);
//        settleInfoExample = new MatchSettleInfoExample();
//        settleInfoExample.createCriteria().andSportIdEqualTo(2L);
//        record.setGoalAutoSettleDataSource(0);
//        matchSettleInfoMapper.updateByExampleSelective(record, settleInfoExample);
//
//        //记录日志
//        recordMatchSettleOperateLog(isEnableAutoSettle, userName, ipAddress, status);
//
//        //推送mq 到 ws
//        AutoSettleDataSourceDto autoSettleDataSourceDto = new AutoSettleDataSourceDto();
//        autoSettleDataSourceDto.setIsEnableAutoSettle(isEnableAutoSettle);
//        wsPushService.pushGlobalAutoSettleStatus(autoSettleDataSourceDto);
//        return Response.success();
//    }
//
//    /**
//     * 记录系统级结算操作日志x
//     */
//    private void recordMatchSettleOperateLog(Boolean isEnableAutoSettle,
//                                             String userName, String ipAddress, boolean flag) {
//        MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
//
//        //操作对象id
//        matchSettleOperateLog.setOperateId("-");
//        matchSettleOperateLog.setOperateName("-");
//
//        matchSettleOperateLog.setOperateForwText(flag ? OperateLogTypeEnum.SCORES_SETTLE_10035.getCode().toString() : OperateLogTypeEnum.SCORES_SETTLE_10036.getCode().toString());
//        matchSettleOperateLog.setOperateRearText(isEnableAutoSettle ? OperateLogTypeEnum.SCORES_SETTLE_10035.getCode().toString() : OperateLogTypeEnum.SCORES_SETTLE_10036.getCode().toString());
//        matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode().toString());
//        matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_10034.getCode().toString());
//        matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchSettleOperateLog.setIpAddress(ipAddress);
//        matchSettleOperateLog.setOperateUserName(userName);
//        //操作参数名称
//        matchSettleOperateLog.setOperateParaName("-");
//        matchSettleOperateLog.setOperateMatchId("-");
//        matchSettleOperateLog.setOperateMatchName("-");
//
//        matchSettleOperateLogMapper.insert(matchSettleOperateLog);
//    }
//
//    @Override
//    public Response changeMatchAutoSettleStatus(String standardMatchId, Boolean isEnableAutoSettle, String userName, String ipAddress) {
//        log.info("changeMatchAutoSettleStatus standardMatchId:"+standardMatchId+",isEnableAutoSettle"+isEnableAutoSettle);
//        //查询未修改前的赛事数据源状态
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(Long.valueOf(standardMatchId));
//        Integer isAutoSettleDataSource = matchSettleInfo.getIsAutoSettleDataSource();
//
////        //更新赛事级结算开关
////        MatchSettleInfo record = new MatchSettleInfo();
////        record.setId(matchSettleInfo.getId());
//        matchSettleInfo.setIsAutoSettleDataSource(isEnableAutoSettle ? 1 : 0);
//        if(isEnableAutoSettle){
//            matchSettleInfo.setBookingAutoSettleDataSource(1);
//            matchSettleInfo.setCornerAutoSettleDataSource(1);
//            matchSettleInfo.setGoalAutoSettleDataSource(1);
//            if(matchSettleInfo.getSportId() == 2) {
//                matchSettleInfo.setGoalAutoSettleDataSource(0);
//            }
//        }else {
//            matchSettleInfo.setBookingAutoSettleDataSource(0);
//            matchSettleInfo.setCornerAutoSettleDataSource(0);
//            matchSettleInfo.setGoalAutoSettleDataSource(0);
//        }
//        if(matchSettleInfo.getSportId()==2) {
//
//        }
////        matchSettleInfo.setBookingAutoSettleDataSource(record.getIsAutoSettleDataSource());
////        record.setCornerAutoSettleDataSource(record.getIsAutoSettleDataSource());
////        record.setGoalAutoSettleDataSource(record.getIsAutoSettleDataSource());
//        matchSettleInfo.setModifyTime(System.currentTimeMillis());
//        log.info("changeMatchAutoSettleStatus to update:"+matchSettleInfo);
//        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//        Integer autoSettle = 0;
//        if (isEnableAutoSettle){
//            autoSettle = 1;
//        }
//        //需要恢复 篮球的 redis key
//        if(isEnableAutoSettle) {
//            basketballInSettleService.cleanBasketInSettleCacheScore(standardMatchId);
//        }
//        //记录日志
//        recordMatchSettleOperateLog(standardMatchId, autoSettle, userName, ipAddress, isAutoSettleDataSource,null);
//
//        //推送mq 到 ws
//        AutoSettleDataSourceDto autoSettleDataSourceDto = new AutoSettleDataSourceDto();
//        autoSettleDataSourceDto.setIsEnableAutoSettle(isEnableAutoSettle);
//        autoSettleDataSourceDto.setStandardMatchId(standardMatchId);
//        wsPushService.pushGlobalAutoSettleStatus(autoSettleDataSourceDto);
//
//        return Response.success();
//    }
//
//    @Override
//    public Response changeEventTypeAutoSettleStatus(String type, Long standardMatchId, Boolean isEnableAutoSettle, String userName, String ipAddress) {
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(Long.valueOf(standardMatchId));
//        Integer oldAutoSettleDataSource = null;
//
//        //更新赛事级结算开关
//        Integer autoSettle = 0;
//        String operateParaName = null;
//        if (isEnableAutoSettle){
//            autoSettle = 1;
//        }
//        if(type.equals("corner")){
//            oldAutoSettleDataSource = matchSettleInfo.getCornerAutoSettleDataSource();
//            matchSettleInfo.setCornerAutoSettleDataSource(autoSettle);
//            operateParaName = OperateLogTypeEnum.SCORES_PD_100141.getCode().toString();
//        }
//        if(type.equals("goal")||type.equals("score_change")){
//            oldAutoSettleDataSource = matchSettleInfo.getGoalAutoSettleDataSource();
//            matchSettleInfo.setGoalAutoSettleDataSource(autoSettle);
//            operateParaName = OperateLogTypeEnum.SCORES_PD_100140.getCode().toString();
//        }
//        if(type.equals("facard")){
//            oldAutoSettleDataSource = matchSettleInfo.getBookingAutoSettleDataSource();
//            matchSettleInfo.setBookingAutoSettleDataSource(autoSettle);
//            operateParaName = OperateLogTypeEnum.SCORES_PD_100145.getCode().toString();
//        }
//        matchSettleInfo.setModifyTime(System.currentTimeMillis());
//        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//
//        //记录日志
//        recordMatchSettleOperateLog(standardMatchId.toString(), autoSettle, userName, ipAddress, oldAutoSettleDataSource,operateParaName);
//
//        //推送mq 到 ws
//        AutoSettleDataSourceDto autoSettleDataSourceDto = new AutoSettleDataSourceDto();
//        autoSettleDataSourceDto.setIsEnableAutoSettle(isEnableAutoSettle);
//        autoSettleDataSourceDto.setStandardMatchId(standardMatchId.toString());
//        wsPushService.pushGlobalAutoSettleStatus(autoSettleDataSourceDto);
//        return  Response.success();
//
//    }
//
//    /**
//     * 记录赛事级结算操作日志
//     */
//    private void recordMatchSettleOperateLog(String standardMatchId, Integer autoSettle,
//                                             String userName, String ipAddress, Integer oldAutoSettleDataSource,String operateParaName) {
//        MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
//        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(Long.valueOf(standardMatchId));
//        if (standardMatchInfo != null) {
//            //操作对象id
//            matchSettleOperateLog.setOperateId(standardMatchInfo.getMatchManageId());
//            matchSettleOperateLog.setOperateName(standardMatchInfo.getHomeAwayInfo());
//        }
//
//        String forwText = null;
//        String rearText = null;
//        if (oldAutoSettleDataSource.equals(1)) {
//            forwText = OperateLogTypeEnum.SCORES_SETTLE_10035.getCode().toString();
//        }else{
//            forwText = OperateLogTypeEnum.SCORES_SETTLE_10036.getCode().toString();
//        }
//        if (autoSettle==1) {
//            rearText = OperateLogTypeEnum.SCORES_SETTLE_10035.getCode().toString();
//        }else{
//            rearText = OperateLogTypeEnum.SCORES_SETTLE_10036.getCode().toString();
//        }
//        matchSettleOperateLog.setOperateForwText(forwText);
//        matchSettleOperateLog.setOperateRearText(rearText);
//        matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode() + "-" + StandardSportTypeEnum.getEnum(standardMatchInfo.getSportId()).getCode());
//        matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_100144.getCode().toString());
//        matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchSettleOperateLog.setIpAddress(ipAddress);
//        matchSettleOperateLog.setOperateUserName(userName);
//        //操作参数名称
//        if (StringUtils.isAnyEmpty(operateParaName)){
//            matchSettleOperateLog.setOperateParaName("-");
//        }else{
//            matchSettleOperateLog.setOperateParaName(operateParaName);
//        }
//        matchSettleOperateLog.setOperateMatchId(standardMatchInfo.getMatchManageId());
//        matchSettleOperateLog.setOperateMatchName("-");
//
//        matchSettleOperateLogMapper.insert(matchSettleOperateLog);
//    }
//
//    @Override
//    public Response getGlobalSyncScoresStatus() {
//        Object object = redisService.get(SETTLE_SYNC_SCORES_KEY);
//        //默认为打开
//        if (object == null) {
//            redisService.set(SETTLE_SYNC_SCORES_KEY, true, 60 * 60 * 24 * 30 * 12 * 10);
//            object = true;
//        }
//        return Response.success((boolean) object);
//    }
//
//    @Override
//    public Response changeGlobalSyncScoresStatus(Boolean isEnableSyncScores, String userName, String ipAddress) {
//        //查询未修改前的系统状态
//        boolean globalSyncScoresStatus = (boolean) redisService.get(SETTLE_SYNC_SCORES_KEY);
//        //每次更新刷新过期时间设置成10年
//        redisService.set(SETTLE_SYNC_SCORES_KEY, isEnableSyncScores, 60 * 60 * 24 * 30 * 12 * 10);
//
//        //记录日志
//        recordSettleSyncScoresOperateLog(isEnableSyncScores, userName, ipAddress, globalSyncScoresStatus);
//        return Response.success();
//    }
//
//    @Override
//    public Response matchSettleAbnormal(List<SettleMatchScoreDto> list) {
//        log.info(" 异常结算输开始,用参数信息: {}", JSON.toJSONString(list));
//
//        boolean peroId = true;
//        boolean peroId15 = true;
//
//        //校验是否只有当前阶段或者15分钟阶段
//        if (list.size() == 5) {
//            //校验是否只有当前阶段
//            for (int i = 0; i < 3; i++) {
//                if (list.get(i).getT1() != null && list.get(i).getT2() != null) {
//                    peroId = false;
//                }
//            }
//            //校验是否只有当前15分钟阶段
//            for (int i = 3; i < list.size(); i++) {
//                if (list.get(i).getT1() != null && list.get(i).getT2() != null) {
//                    peroId15 = false;
//                }
//            }
//            //list长度为5 定是传了当前阶段和 15分钟阶段
//            //当前阶段和15分钟阶段必须各自有一条主客不为空的情况
//            if (peroId || peroId15) {
//                return Response.failed("1031955");
//            }
//        }
//
//
//        //校验参数信息
//        for (int i = 0; i < list.size(); i++) {
//            //校验剔除主客为空的信息
//            if (list.get(i).getT1() == null && list.get(i).getT2() == null) {
//                list.remove(i);
//                --i;
//                continue;
//            }
//            //校验是否只填写了主队或客队信息
//            if (list.get(i).getT1() == null || list.get(i).getT2() == null) {
//                return Response.failed("1031955");
//            }
//        }
//        //校验参数信息
//        if (list.size() == 0) {
//            return Response.failed("1031955");
//        }
//
//        List<Long> collect = list.stream().map(SettleMatchScoreDto::getMatchScoreId).collect(Collectors.toList());
//        Map<Long, SettleMatchScoreDto> map = list.stream().collect(Collectors.toMap(SettleMatchScoreDto::getMatchScoreId, Function.identity()));
//        MatchSettleAbnormalExample example = new MatchSettleAbnormalExample();
//        example.createCriteria().andIdIn(collect);
//        List<MatchSettleAbnormal> matchSettleAbnormals = iMatchSettleAbnormalMapper.selectByExample(example);
//        if (matchSettleAbnormals.size() == 0) {
//            return Response.failed("103011");
//        }
//        for (MatchSettleAbnormal matchSettleAbnormal : matchSettleAbnormals) {
//            MatchSettleScore SettleScoreOid = new MatchSettleScore();
//            BeanUtils.copyProperties(matchSettleAbnormal, SettleScoreOid);
//            String before = "-";
//            if (matchSettleAbnormal.getT1() != null && matchSettleAbnormal.getT2() != null) {
//                before = matchSettleAbnormal.getT1() + "-" + matchSettleAbnormal.getT2();
//            }
//            SettleMatchScoreDto settleMatchScoreDto = map.get(matchSettleAbnormal.getId());
//            matchSettleAbnormal.setStatus(SETTLED);
//            matchSettleAbnormal.setOperater(settleMatchScoreDto.getOperatorName());
//            matchSettleAbnormal.setSettleTimes(matchSettleAbnormal.getSettleCount() + 1);
//            matchSettleAbnormal.setSettleCount(matchSettleAbnormal.getSettleCount() + 1);
//            matchSettleAbnormal.setUserid(settleMatchScoreDto.getOperatorId());
//            matchSettleAbnormal.setModifyTime(System.currentTimeMillis());
//            matchSettleAbnormal.setT1(settleMatchScoreDto.getT1());
//            matchSettleAbnormal.setT2(settleMatchScoreDto.getT2());
//
//
//            iMatchSettleAbnormalMapper.updateByPrimaryKey(matchSettleAbnormal);
//            if (matchSettleAbnormal.getIsScores() == 1) {
//                MatchSettleScoreMessage matchSettleScoreMessage = new MatchSettleScoreMessage();
//                BeanUtils.copyProperties(matchSettleAbnormal, matchSettleScoreMessage, "settleTimes", "settleCount");
//                matchSettleScoreMessage.setIsAbnormal(1);
//                matchSettleScoreMessage.setOperateType(1);
//                matchSettleScoresProducer.sendMatchSettleScores(matchSettleScoreMessage);
//            } else if (matchSettleAbnormal.getIsScores() == 2) {
//                MatchSettleEventMessage matchSettleEventMessage = new MatchSettleEventMessage();
//                BeanUtils.copyProperties(matchSettleAbnormal, matchSettleEventMessage, "settleTimes", "settleCount");
//                matchSettleEventMessage.setIsAbnormal(1);
//                matchSettleEventMessage.setOperateType(1);
//                matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEventMessage);
//            }
//            MatchSettleScore matchSettleScore = new MatchSettleScore();
//            BeanUtils.copyProperties(matchSettleAbnormal, matchSettleScore);
//            //1.比分结算增加操作日志
//            iMatchSettleLogService.matchSettleScoreAddLog(SettleScoreOid, matchSettleScore, list.get(0).getOperatorName(), OperateLogTypeEnum.SCORES_SETTLE_10039.getCode().toString(), before, list.get(0).getIpAddress());
//        }
//
//        return Response.success();
//    }
//
//    @Override
//    public Response setSettleOrderClosed(MatchSettleOrderClosedDTO dto) {
//        log.info("设置结算顺序开始,setSettleOrderClosed: {}", JSON.toJSONString(dto));
//        try {
//            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(dto.getMatchId());
//            if (matchSettleInfo != null) {
//
//                MatchSettleInfo info = new MatchSettleInfo();
//                BeanUtils.copyProperties(matchSettleInfo, info);
//                matchSettleInfo.setSettleOrderClosed(dto.getSettleOrderClosed());
//                matchSettleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                //1.比分结算增加操作日志
//                iMatchSettleLogService.setSettleOrderClosedAddLog(info, matchSettleInfo, dto, OperateLogTypeEnum.SCORES_SETTLE_10043);
//
//            }
//
//            return Response.success();
//        } catch (Exception e) {
//            log.error("设置结算顺序异常,赛事id:{} , setSettleOrderClosed: {}", dto.getMatchId(), JSON.toJSONString(e));
//        }
//        return Response.success();
//    }
//
//    /**
//     * 记录系统级结算操作日志
//     */
//    private void recordSettleSyncScoresOperateLog(Boolean isEnableSyncScores,
//                                                  String userName, String ipAddress, boolean flag) {
//        MatchSettleOperateLog matchSettleOperateLog = new MatchSettleOperateLog();
//
//        //操作对象id
//        matchSettleOperateLog.setOperateId("-");
//        matchSettleOperateLog.setOperateName("-");
//
//        matchSettleOperateLog.setOperateForwText(flag ? OperateLogTypeEnum.SCORES_SETTLE_10035.getCode().toString() : OperateLogTypeEnum.SCORES_SETTLE_10036.getCode().toString());
//        matchSettleOperateLog.setOperateRearText(isEnableSyncScores ? OperateLogTypeEnum.SCORES_SETTLE_10035.getCode().toString() : OperateLogTypeEnum.SCORES_SETTLE_10036.getCode().toString());
//        matchSettleOperateLog.setOperateModule(OperateLogTypeEnum.type_7.getCode().toString());
//        matchSettleOperateLog.setOperateType(OperateLogTypeEnum.SCORES_SETTLE_10038.getCode().toString());
//        matchSettleOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchSettleOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        matchSettleOperateLog.setIpAddress(ipAddress);
//        matchSettleOperateLog.setOperateUserName(userName);
//        //操作参数名称
//        matchSettleOperateLog.setOperateParaName("-");
//        matchSettleOperateLog.setOperateMatchId("-");
//        matchSettleOperateLog.setOperateMatchName("-");
//
//        matchSettleOperateLogMapper.insert(matchSettleOperateLog);
//    }
//
//    @Override
//    public Response setFiveMinSwitch(MatchSettleFiveMinSwitchDTO dto) {
//        log.info("设置五分钟玩法开关开始,setFiveMinSwitch: {}", JSON.toJSONString(dto));
//        String key = "MATCH_SETTLE_INFO:" + dto.getMatchId();
//        try {
//            if (redisService.tryLock(key, key, 3, 5)) {
//                MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(dto.getMatchId());
//                if (matchSettleInfo != null) {
//
//                    //判断下5分钟数据是否存在不存在则初始化
//                    checkAndinitFiveMinScore(matchSettleInfo.getStandardMatchId());
//                    MatchSettleInfo info = new MatchSettleInfo();
//                    BeanUtils.copyProperties(matchSettleInfo, info);
//                    matchSettleInfo.setFiveMinSwitch(dto.getFiveMinSwitch());
//                    matchSettleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                    //1.比分结算增加操作日志
//                    iMatchSettleLogService.setFiveMinSwitchLog(info, matchSettleInfo, dto, OperateLogTypeEnum.SCORES_SETTLE_10044);
//
//                }
//                return Response.success();
//            } else {
//                return Response.failed("切换过快,请稍后试下");
//            }
//        } catch (Exception e) {
//            log.error("设置五分钟玩法开关异常,赛事id:{} , setFiveMinSwitch: {}", dto.getMatchId(), JSON.toJSONString(e));
//        } finally {
//            redisService.unLock(key, key);
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response setDataSourceGrayInterval(List<DataSourceGrayIntervalDto> grayIntervalDtoList) {
//        log.info("setDataSourceGrayInterval设置灰色区间入参:{}", JSON.toJSONString(grayIntervalDtoList));
//        if (CollectionUtils.isEmpty(grayIntervalDtoList)) {
//            Response.failed("设置灰色区间参数不能为空!");
//        }
//        Integer tournamentLevel = grayIntervalDtoList.get(0).getTournamentLevel();
//        if ( null == tournamentLevel || tournamentLevel < 0 ) {
//            Response.failed("编辑的联赛等级异常!");
//        }
//
//        MatchGrayIntervalExample grayIntervalExample = new MatchGrayIntervalExample();
//        grayIntervalExample.createCriteria().andTournamentLevelEqualTo(tournamentLevel);
//        List<MatchGrayInterval> dbGrayIntervals = matchGrayIntervalMapper.selectByExample(grayIntervalExample);
//        Map<String, MatchGrayInterval> dsgMap = Maps.newConcurrentMap();
//        if ( !CollectionUtils.isEmpty(dbGrayIntervals) ) {
//            dsgMap = dbGrayIntervals.stream().collect(Collectors.toMap(MatchGrayInterval::getDataSourceCode, Function.identity()));
//        }
//
//        for (DataSourceGrayIntervalDto grayIntervalDto : grayIntervalDtoList) {
//            String dataSourceCode = grayIntervalDto.getDataSourceCode();
//            if ( null != dsgMap && dsgMap.size() > 0 && dsgMap.containsKey(dataSourceCode) ) {
//                MatchGrayInterval dbGray = dsgMap.get(dataSourceCode);
//                dbGray.setModifyTime(System.currentTimeMillis());
//                dbGray.setMin5Goal(grayIntervalDto.getMin5Goal());
//                dbGray.setMin15Goal(grayIntervalDto.getMin15Goal());
//                dbGray.setMin15Bookings(grayIntervalDto.getMin15Bookings());
//                dbGray.setMin15Corner(grayIntervalDto.getMin15Corner());
//                matchGrayIntervalMapper.updateByPrimaryKeySelective(dbGray);
//                iMatchSettleLogService.updateDataSourceGrayIntervalLog(grayIntervalDto,dsgMap.get(dataSourceCode));
//            } else {
//                MatchGrayInterval grayInterval = new MatchGrayInterval();
//                BeanUtils.copyProperties(grayIntervalDto, grayInterval);
//                grayInterval.setModifyTime(System.currentTimeMillis());
//                grayInterval.setCreateTime(System.currentTimeMillis());
//                matchGrayIntervalMapper.insert(grayInterval);
//                iMatchSettleLogService.updateDataSourceGrayIntervalLog(grayIntervalDto,null);
//            }
//        }
//
//        // 缓存的刷新
//        return Response.success();
//    }
//
//    @Override
//    public Response getGrayIntervalByTournamentLevel(DataSourceGrayIntervalDto dto) {
//        log.info("getGrayIntervalByTournamentLevel查询灰色区间列表入参:{}", JSON.toJSONString(dto));
//        Integer tournamentLevel = dto.getTournamentLevel();
//        if ( null == tournamentLevel || tournamentLevel < 0 ) {
//            Response.failed("查询的联赛等级异常!");
//        }
//        List<MatchGrayInterval> grayIntervalList = Lists.newArrayList();
//        MatchGrayIntervalExample grayIntervalExample = new MatchGrayIntervalExample();
//        grayIntervalExample.createCriteria().andTournamentLevelEqualTo(tournamentLevel);
//        List<MatchGrayInterval> dbGrayIntervals = matchGrayIntervalMapper.selectByExample(grayIntervalExample);
//        if ( !CollectionUtils.isEmpty(dbGrayIntervals) ) {
//            grayIntervalList.addAll(dbGrayIntervals);
//        }
//        log.info("getGrayIntervalByTournamentLevel返回结果:{}", JSON.toJSONString(grayIntervalList));
//        return Response.success(grayIntervalList);
//    }
//
//    private void checkAndinitFiveMinScore(Long standardMatchId) {
//
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        example.createCriteria().andSettleNumEqualTo("1053").andStandardMatchIdEqualTo(standardMatchId);
//        List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
//        if (list.size() == 0) {
//            List<MatchSettleScore> matchSettleScores = new ArrayList<>();
//            FootBallMatchSettleScoreUtils.initFiveMinGoalScore(matchSettleScores, standardMatchId);
//            for (MatchSettleScore matchSettleScore : matchSettleScores) {
//                matchSettleScoreMapper.insert(matchSettleScore);
//            }
//        }
//    }
//
//
//    /**
//     * 根据参数SportId,球种类型，获取对应的联赛等级数据源的开关列表
//     * @param matchSettleDataSourceDto
//     * @return
//     */
//    @Override
//    public Response getMatchSettleDataSources(MatchSettleDataSourceDto matchSettleDataSourceDto) {
//
//        MatchSettleDataSourceConfigExample matchSettleDataSourceConfigExample = new MatchSettleDataSourceConfigExample();
//        matchSettleDataSourceConfigExample.createCriteria().andSportIdEqualTo(matchSettleDataSourceDto.getSportId());
//        matchSettleDataSourceConfigExample.setOrderByClause("data_source_code,tournament_level asc");
//        List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList = matchSettleDataSourceConfigMapper.selectByExample(matchSettleDataSourceConfigExample);
//        Map<String,List<MatchSettleDataSourceConfig>> matchSettleDataSourceMap = new HashMap<>();
//        if (!matchSettleDataSourceConfigList.isEmpty()) {
//            matchSettleDataSourceMap = matchSettleDataSourceConfigList.stream().collect(Collectors.groupingBy(MatchSettleDataSourceConfig::getDataSourceCode));
//        }
//        log.info("getMatchSettleDataSources,返回结果:{}", JSON.toJSONString(matchSettleDataSourceMap));
//        return Response.success(matchSettleDataSourceMap);
//    }
//
//
//    /**
//     * 根据球种类型和联赛等级,设置联赛等级对应的结算数据源的开关列表
//     * @param matchSettleDataSourceDto
//     * @return
//     */
//    @Override
//    public Response setLeagueMatchSettleDataSource(MatchSettleDataSourceDto matchSettleDataSourceDto) {
//
//        try {
//            //全部的开关
//            if (matchSettleDataSourceDto.getTournamentLevel().equals(CategoryUtils.UN_LEVEL)){
//                MatchSettleDataSourceConfigExample matchSettleDataSourceConfigUnLevel = new MatchSettleDataSourceConfigExample();
//                matchSettleDataSourceConfigUnLevel.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceDto.getDataSourceCode())
//                        .andTournamentLevelEqualTo(CategoryUtils.UN_LEVEL).andSportIdEqualTo(matchSettleDataSourceDto.getSportId());
//                List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigUnLevelList = matchSettleDataSourceConfigMapper.selectByExample(matchSettleDataSourceConfigUnLevel);
//                if (matchSettleDataSourceConfigUnLevelList.isEmpty()) {
//                    Response.failed("没有联赛对应的数据源记录");
//                }
//                Integer oldStatus = matchSettleDataSourceConfigUnLevelList.get(0).getStatus();
//                MatchSettleDataSourceConfigExample matchSettleDataSourceConfigExample = new MatchSettleDataSourceConfigExample();
//                matchSettleDataSourceConfigExample.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceDto.getDataSourceCode())
//                        .andSportIdEqualTo(matchSettleDataSourceDto.getSportId());
//                List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList = matchSettleDataSourceConfigMapper.selectByExample(matchSettleDataSourceConfigExample);
//                for (MatchSettleDataSourceConfig settleDataSourceConfig:matchSettleDataSourceConfigList) {
//                    settleDataSourceConfig.setStatus(matchSettleDataSourceDto.getStatus());
//                    settleDataSourceConfig.setModifyTime(System.currentTimeMillis());
//                    matchSettleDataSourceConfigMapper.updateByPrimaryKeySelective(settleDataSourceConfig);
//                }
//                iMatchSettleLogService.updateLeagueMatchSettleDataSourceLog(matchSettleDataSourceDto,oldStatus);
//            } else {
//                //更新单条联赛对应的数据源的开关
//                MatchSettleDataSourceConfigExample matchSettleDataSourceConfigExample = new MatchSettleDataSourceConfigExample();
//                matchSettleDataSourceConfigExample.createCriteria().andDataSourceCodeEqualTo(matchSettleDataSourceDto.getDataSourceCode())
//                        .andTournamentLevelEqualTo(matchSettleDataSourceDto.getTournamentLevel()).andSportIdEqualTo(matchSettleDataSourceDto.getSportId());
//                List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList = matchSettleDataSourceConfigMapper.selectByExample(matchSettleDataSourceConfigExample);
//                if (!matchSettleDataSourceConfigList.isEmpty()){
//                    MatchSettleDataSourceConfig settleDataSourceConfig = matchSettleDataSourceConfigList.get(0);
//                    Integer oldStatus = settleDataSourceConfig.getStatus();
//                    settleDataSourceConfig.setStatus(matchSettleDataSourceDto.getStatus());
//                    settleDataSourceConfig.setModifyTime(System.currentTimeMillis());
//                    matchSettleDataSourceConfigMapper.updateByPrimaryKey(settleDataSourceConfig);
//                    iMatchSettleLogService.updateLeagueMatchSettleDataSourceLog(matchSettleDataSourceDto,oldStatus);
//                } else {
//                    Response.failed("没有联赛对应的数据源记录");
//                }
//
//            }
//        }catch (Exception e){
//            log.error("linkId：{},更新联赛等级对应的结算数据源的开关列表：{},异常信息: {}",matchSettleDataSourceDto.getLinkedId(), JSON.toJSONString(matchSettleDataSourceDto),e);
//        }
//        return Response.success();
//    }
//
//    @Override
//    public Response getBasketInSettleTimeLimit(Long sportId) {
//       return Response.success(basketballInSettleService.getBasketInSettleTimeLimit(sportId));
//    }
//
//    @Override
//    public Response setBasketInSettleTimeLimit(SettleTimeLimitDto dto) {
//        //先查redis获得旧的配置
//        List<LimitSwitchDto> oldConfigs = basketballInSettleService.getBasketInSettleTimeLimit(dto.getSportId());
//        if(dto.getLimitSwitchJson()!=null&&dto.getSportId()!=null){
//            List<LimitSwitchDto> newConfigs = new ArrayList<>();
//            JSONArray array = JSONArray.parseArray(dto.getLimitSwitchJson());
//            for (Object object :array){
//                LimitSwitchDto newConfig =   JSONObject.toJavaObject((JSONObject)object,LimitSwitchDto.class);
//                newConfigs.add(newConfig);
//            }
//            oldConfigs.forEach(old->{
//                newConfigs.forEach(n->{
//                    if (old.getLevel()==n.getLevel()){
//                        if(old.getOnOff()!=n.getOnOff()){ //记录开关日志
//                            matchSettleLogService.editBasketBallSetUpConfigLog(old,n,dto);
//                        }
//                        if (old.getLimitSecond()!=n.getLimitSecond()){
//                            matchSettleLogService.editBasketBallTimeLimitConfigLog(old,n,dto);
//                        }
//                    }
//                });
//            });
//
//            //记录日志
//            redisService.setLongTime(REDIS_KEY_BASKET_IN_TIME_LIMIT+"_"+dto.getSportId(),dto.getLimitSwitchJson());
//        }else {
//            return Response.failed("设置非法");
//        }
//        return Response.success("设置成功");
//    }
//
//    /**
//     * 根据参数SportId,球种类型，获取对应的联赛等级数据源的权重及开关列表
//     * @param matchSettleDataSourceWeightDto
//     * @return
//     */
//    @Override
//    public Response getMatchSettleDataSourcesWeight(MatchSettleDataSourceWeightDto matchSettleDataSourceWeightDto) {
//
//        MatchSettleDataSourceWeightConfigExample matchSettleDataSourceWeightConfigExample = new MatchSettleDataSourceWeightConfigExample();
//        matchSettleDataSourceWeightConfigExample.createCriteria().andSportIdEqualTo(matchSettleDataSourceWeightDto.getSportId());
//        matchSettleDataSourceWeightConfigExample.setOrderByClause("data_source_code,tournament_level asc");
//        List<MatchSettleDataSourceWeightConfig> matchSettleDataSourceWeighConfigList = matchSettleDataSourceWeightConfigMapper.selectByExample(matchSettleDataSourceWeightConfigExample);
//        Map<String,List<MatchSettleDataSourceWeightConfig>> matchSettleDataSourceWeightMap = new HashMap<>();
//        if (!matchSettleDataSourceWeighConfigList.isEmpty()) {
//            matchSettleDataSourceWeightMap = matchSettleDataSourceWeighConfigList.stream().collect(Collectors.groupingBy(MatchSettleDataSourceWeightConfig::getDataSourceCode));
//        }
//        log.info("getMatchSettleDataSourcesWeight,返回结果:{}", JSON.toJSONString(matchSettleDataSourceWeightMap));
//        return Response.success(matchSettleDataSourceWeightMap);
//    }
//
//    @Override
//    public Response refreshMatchSettleInfo(MatchSettleInfo info) {
//
//        log.info("刷新结算信息表redis:"+info);
//        String key = MATCH_SETTLE_INFO+info.getId();
////        MatchSettleInfo matchSettleInfo = matchSettleInfoMapper.selectByPrimaryKey(id);
//        if (null!= info){
//            try{
//                redisService.set(key,JSONObject.toJSON(info),REDIS_THREE_TIME);
//            }catch (Exception e){
//                log.error("MatchSettleInfo:redis写入异常key=[{}]MatchSettleInfo[{}]", key,JSONObject.toJSON(info), e);
//            }
//
//        }
//        return null;
//    }
//
//}

