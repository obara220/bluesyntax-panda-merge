//package com.panda.merge.check.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.merge.api.IBasketballMatchScoresSettleApi;
//import com.panda.merge.api.IFootballMatchScoresSettleApi;
//import com.panda.merge.check.IMatchSettleCheckService;
//import com.panda.merge.common.enums.*;
//import com.panda.merge.common.utils.IdWorker;
//import com.panda.merge.config.RedisConfig;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.constant.*;
//import com.panda.merge.dto.*;
//import com.panda.merge.dto.settle.EditMatchSettleEventDto;
//import com.panda.merge.dto.settle.MatchListSettleDto;
//import com.panda.merge.dto.settle.MatchSettleEventDto;
//import com.panda.merge.dto.settle.MatchSettleScoreDto;
//import com.panda.merge.dto.settle.PenaltyScoresVo;
//import com.panda.merge.filter.football.impl.MatchScoresSettleInitChainFilter;
//import com.panda.merge.mapper.*;
//import com.panda.merge.model.*;
//import com.panda.merge.mq.producer.MatchSettleCenterProducer;
//import com.panda.merge.mq.producer.MatchSettleScoresProducer;
//import com.panda.merge.respository.*;
//import com.panda.merge.service.*;
//import com.panda.merge.service.impl.GrayIntervalService;
//import com.panda.merge.service.settleMention.service.SettleMentionFactory;
//import com.panda.merge.utils.EndEventUtils;
//import com.panda.merge.utils.FootBallMatchSettleScoreUtils;
//import com.panda.merge.utils.SettleCheckUtils;
//import com.panda.merge.utils.SettleNumUtils;
//import com.panda.merge.utils.SettleTemplateJsonUtils;
//import com.panda.merge.utils.SettleTemplateWeightUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.tuple.Pair;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.lang.reflect.Array;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.*;
//import static com.panda.merge.utils.SettleNumUtils.fiveMinuteMap;
//
//
//@Service
//@Slf4j
//public class MatchSettleCheckServiceImpl implements IMatchSettleCheckService {
//    @Autowired
//    MatchSettleInfoMapper matchSettleInfoMapper;
//    @Autowired
//    MatchSettleScoreMapper matchSettleScoreMapper;
//    @Autowired
//    RedisService redisService;
//    @Autowired
//    MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;
//    @Autowired
//    MatchSettleEventMapper matchSettleEventMapper;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    MatchSettleCenterProducer matchSettleCenterProducer;
//
//    @Autowired
//    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;
//    @Autowired
//    IBasketballMatchScoresSettleApi basketballMatchScoresSettleApi;
//    @Autowired
//    IMatchSettleLogService matchSettleLogService;
//    @Autowired
//    MatchSettleScoresProducer matchSettleScoresProducer;
//    @Autowired
//    IWsPushService wsPushService;
//    @Autowired
//    IMatchSettleLogService iMatchSettleLogService;
//    @Autowired
//    IMatchSettleService matchSettleService;
//    @Autowired
//    MatchSettleGoalStatusMapper matchSettleGoalStatusMapper;
//    @Autowired
//    IMatchSettleCheckService matchSettleCheckService;
//    @Autowired
//    MatchScoresSettleInitChainFilter matchScoresSettleInitChainFilter;
//    @Autowired
//    MatchSettleDataSourceConfigService matchSettleDataSourceConfigService;
//
//    @Autowired
//    GrayIntervalService grayIntervalService;
//    @Autowired
//    ISettleTemplateService settleTemplateService;
//    @Autowired
//    MatchSettleDataSourceSwitchMapper matchSettleDataSourceSwitchMapper;
//    @Autowired
//    MatchSettleThirdScoreMapper matchSettleThirdScoreMapper;
//
//    @Autowired
//    MatchSettleInfoRepository matchSettleInfoRepository;
//
//    @Autowired
//    MatchDelaySettleInfoMapper matchDelaySettleInfoMapper;
//
//    @Autowired
//    SettleMentionFactory settleMentionFactory;
//    @Autowired
//    MatchSettleGoalStatusRepository matchSettleGoalStatusRepository;
//    @Autowired
//    StandardMatchInfoService standardMatchInfoService;
//    @Autowired
//    StandardSportMarketSellService standardSportMarketSellService;
//    @Autowired
//    MatchEventInfoRepository matchEventInfoRepository;
//    @Autowired
//    ThirdMatchInfoService thirdMatchInfoService;
//    @Autowired
//    MatchSettleFactoryCheckInfoRepository matchSettleFactoryCheckInfoRepository;
//
//    @Value("${spring.profiles.active}")
//    private String env;
//
//    @Value("${settle.slow.sql.cost:3}")
//    private long slowSqlTime;
//
//    //5分钟编码 计算5分钟区间
//    List<String> strings = Arrays.asList("5", "10", "15", "20", "25", "30", "35",
//            "40", "45", "49", "50", "55", "60", "65", "70", "75", "80", "85", "90", "99");
//    private static final List<String> allMins15Codes = Arrays.asList(MatchPeriodEnum.GOAL_2.getCode().toString(),
//            MatchPeriodEnum.GOAL_3.getCode().toString(),MatchPeriodEnum.GOAL_4.getCode().toString(),MatchPeriodEnum.GOAL_6.getCode().toString(),
//            MatchPeriodEnum.GOAL_7.getCode().toString(),MatchPeriodEnum.GOAL_8.getCode().toString());
//    private static final List<String> dataSourceCodeManually = Arrays.asList("PD", "PA", "PD2");
//
//    private static final List<String> basketball6Mns = Arrays.asList(BasketBallSettleNumEnum.BK_Q1041.getCode(), BasketBallSettleNumEnum.BK_Q2041.getCode(),
//            BasketBallSettleNumEnum.BK_Q3041.getCode(), BasketBallSettleNumEnum.BK_Q4041.getCode(), BasketBallSettleNumEnum.BK_Q1042.getCode(),
//            BasketBallSettleNumEnum.BK_Q2042.getCode(), BasketBallSettleNumEnum.BK_Q3042.getCode(), BasketBallSettleNumEnum.BK_Q4042.getCode());
//
//    private static final MatchSettleCheckInfo constantCheckInfo = new MatchSettleCheckInfo();
//
//    List<String> goalDelaySettleNum = Arrays.asList("105","109","1010","102","1034","1035","1036","103","1037","1038","1039","104","1040","1041","1042","106","1044","1045","1046","107","1047","1048","1049","108","1050","1051","1052","1043","1053");
//    List<String> cornerDelaySettleNum = Arrays.asList("201","202","203","2011","2012","2014","2015");
//    List<String> bookingDelaySettleNum = Arrays.asList("308","304","309","301","302","305","306");
//
//    List<String> bookingEventDelaySettleNum = Arrays.asList("3019","3020");
//    List<String> cornerEventDelaySettleNum = Arrays.asList("204","205");
//
//    List<String> goal15SettleNum =  Arrays.asList("102","103","104","106","107","108","105","109","1010");
//    List<String> goal5SettleNum = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040","1041", "1042", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052");
//    //重要核对校验阶段
//    @Override
//    public boolean checkMatchThirdSettleScores(MatchSettleThirdScore matchSettleThirdScore, String linkedId, Long second, CheckIsGreyDto checkIsGreyDto) {
//        log.info("linkId::{}::eventId:{} settleNum:{} checkMatchThirdSettleScores start", linkedId, matchSettleThirdScore.getId(), matchSettleThirdScore.getSettleNum());
//        //1.当前赛事是否切换到2.0如果不是则不生成数据
//        if (!isSettle2(matchSettleThirdScore.getStandardMatchId())) {
//            log.info("linkId::{}::eventId:{} settleNum:{} 当前赛事不是结算2.0 不触发核对比分操作X", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum());
//            return false;
//        }
//        //2.查询需要核对的标准比分ID 做关系绑定
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        example.createCriteria().andSettleNumEqualTo(matchSettleThirdScore.getSettleNum()).andStandardMatchIdEqualTo(matchSettleThirdScore.getStandardMatchId());
//        long settleScoreStartTime = System.currentTimeMillis();
//        List<MatchSettleScore> standardScores = matchSettleScoreMapper.selectByExample(example);
//        long settleScoreCost = System.currentTimeMillis()-settleScoreStartTime;
//        if (settleScoreCost > slowSqlTime) {
//            log.info("linkId::{}::eventId:{} settleNum:{} matchSettleScoreMapper query operation time cost:{}", linkedId, matchSettleThirdScore.getId(), matchSettleThirdScore.getSettleNum(), settleScoreCost);
//        }
//        if (standardScores.size() == 0) {
//            log.info("linkId::{}::eventId:{} settleNum:{} 该事件比分变更没有搜索到需要核对的比分X", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum());
//            return false;
//        }
//        MatchSettleScore matchSettleScore = standardScores.get(0);
//        if (matchSettleScore.getStatus() == 3) {
//            log.info("linkId::{}::eventId:{} settleNum:{} 该事件修改的比分已经结算,无需重新核对比分::{}::", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum(),matchSettleScore.getId());
//            return false;
//        }
//        //如果比分已经被回滚一次 则弃用数据商比分
//        if (matchSettleScore.getSettleCount() != null && matchSettleScore.getSettleCount() > 0) {
//            return false;
//        }
//        //这个时候就可以弄了,取消当前比分的灰色区间 足球才有灰色区间
//        if(checkIsGreyDto!=null&&checkIsGreyDto.getThisDataSourceIsGray()!=null&&checkIsGreyDto.getThisDataSourceIsGray()==0){
//            settleTemplateService.cancelGrayStatus(matchSettleThirdScore,matchSettleScore);
//            log.info("linkId::{}::eventId:{} settleNum:{} 取消灰色区间状态", linkedId, matchSettleThirdScore.getId(), matchSettleThirdScore.getSettleNum());
//        }
//        //3 redis 加锁 锁 结算比分ID 先忽略 TODO
//
//        //3.1 查询比分核对类是否存在
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleScore.getId()).andCheckTypeEqualTo(1).andDataSourceCodeEqualTo(matchSettleThirdScore.getDataSourceCode());
//        long checkInfoStartTime = System.currentTimeMillis();
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        long checkInfoCost = System.currentTimeMillis()-checkInfoStartTime;
//        if (checkInfoCost > slowSqlTime) {
//            log.info("linkId::{}::eventId:{} settleNum:{} matchSettleCheckInfoMapper query operation time cost:{}", linkedId, matchSettleThirdScore.getId(), matchSettleThirdScore.getSettleNum(), checkInfoCost);
//        }
//        MatchSettleCheckInfo matchSettleCheckInfo = null;
//
//        //5分钟编码 计算5分钟区间
//        List<String> strings = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040",
//                "1041", "1042", "1043", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052", "1053");
//        //3.2 已经存在则修改比分
//        boolean createCheck = false;
//        if (list.size() != 0) {
//            if (list.size() > 1) {
//                log.error("linkId::{}::eventId:{} settleNum:{} checkMatchThirdSettleScores 事件核对并发生成:{}", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum(), list.size());
//            }
//            matchSettleCheckInfo = list.get(0);
//            if (!StringUtils.isAnyEmpty(matchSettleThirdScore.getOperater()) && (matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))) {
//                matchSettleCheckInfo.setUserName(matchSettleThirdScore.getOperater() + "(" + matchSettleThirdScore.getDataSourceCode() + ")");
//            }
//            //如果已经核对过的数据商的比分，未结算的，不会再进行结算： 场景
//            // 1. 15分钟比分 错过阶段没结算说明 上个15分钟阶段数据商的比分有问题不准确，所以下一个阶段 也不用数据商的比分结算，除非人工结算掉后，才判断应该没问题
//            //2. 这里有大量频繁的 批次结算操作，去掉重复 结算数据商的操作，能防止并发问题，而且提高性能.同时不会对下游造成很大影响
////            if(matchSettleCheckInfo.getCheckStatus().equals(MatchSettleCheckConstant.CheckStatus.CONFIRM)){
////                log.info("::{}:: 上个15分钟阶段数据商的比分不准确:{}",linkedId,matchSettleCheckInfo);
////                return true;
////            }
//            //比分修正
//            SettleCheckUtils.copyMatchSettleScores(matchSettleThirdScore, matchSettleCheckInfo);
//            matchSettleCheckInfo.setIsGrey(matchSettleThirdScore.getIsGrey());
//            if(matchSettleCheckInfo.getIsGrey()==null){
//                matchSettleCheckInfo.setIsGrey(0);
//            }
////            if (!matchSettleCheckInfo.getDataSourceCode().equals("F01")){
//                //赛事比分入库的时候只做编辑不做自动审核
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
////            }
//            long checkInfoUpdateStartTime = System.currentTimeMillis();
//            matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//            long checkInfoUpdateCost = System.currentTimeMillis()-checkInfoUpdateStartTime;
//            if (checkInfoUpdateCost > slowSqlTime) {
//                log.info("linkId::{}::eventId:{} settleNum:{} matchSettleCheckInfoMapper update operation time cost:{}", linkedId, matchSettleThirdScore.getId(), matchSettleThirdScore.getSettleNum(), checkInfoUpdateCost);
//            }
//            log.info("linkId::{}::eventId:{} settleNum:{} 比分修正 当前数据商比分核对数据:{}", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum(), matchSettleCheckInfo);
//        } else {
//            // 8月 15日新版推送几率很大
//            //3.3 不存在创建核对比分
//            matchSettleCheckInfo = SettleCheckUtils.initMatchSettleScores(matchSettleScore, matchSettleThirdScore);
//            if (!StringUtils.isAnyEmpty(matchSettleThirdScore.getOperater()) && (matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))) {
//                matchSettleCheckInfo.setUserName(matchSettleThirdScore.getOperater() + "(" + matchSettleThirdScore.getDataSourceCode() + ")");
//            }
//            matchSettleCheckInfo.setIsGrey(matchSettleThirdScore.getIsGrey());
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            // BT LS 1X 默认生效
//            if (matchSettleCheckInfo.getDataSourceCode().equals("LS") ||matchSettleCheckInfo.getDataSourceCode().equals("BT") || matchSettleCheckInfo.getDataSourceCode().equals("1X")) {
//                if (matchSettleScore.getEventCode().equals("goal")) {
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//                }
//            }
//            //5分钟编码 计算5分钟区间
//            if (strings.contains(matchSettleThirdScore.getSettleNum())) {
//                matchSettleCheckInfo.setFiveMinSection(fiveMinuteMap.get(matchSettleThirdScore.getSettleNum()));
//            }
//            if (matchSettleCheckInfo.getIsGrey() == null) {
//                matchSettleCheckInfo.setIsGrey(0);
//            }
//            matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//            //插入延迟结算比分数据
//            initDelaySettleScore(matchSettleScore,matchSettleCheckInfo,second);
//            validateDataScoreMismatch(matchSettleCheckInfo, matchSettleThirdScore.getSettleNum(), matchSettleThirdScore.getSportId());
//            log.info("linkId::{}::eventId:{} settleNum:{} 插入当前数据商比分核对数据:{}", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum(), matchSettleCheckInfo);
//            createCheck = true;
//        }
//
//        //4.触发比分核对 建议异步吧
//        log.info("linkId::{}::eventId:{} settleNum:{} 阶段比分核对开始", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum());
//        boolean send3Times = getSend3TimesByCache(matchSettleCheckInfo);
//        //判断上个进球是否已经被确认 如果已经被确认，则自动结算
//        //15灰色区间的话 暂不做直接结算
//        if(matchSettleCheckInfo.getIsGrey()!=null&&matchSettleCheckInfo.getIsGrey()==1){
//            log.info("linkId::{}::eventId:{} settleNum:{} 灰色区间返回", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum());
//            return false;
//        }
//        //罚牌过滤
//        if (matchSettleScore.getEventCode().equals("goal") || matchSettleScore.getEventCode().equals("kick_off")
//                || matchSettleScore.getEventCode().equals("corner")||matchSettleScore.getEventCode().equals("fa_card")||matchSettleScore.getEventCode().equals("yellow_card")
//                ||matchSettleScore.getEventCode().equals("red_card") || CommonConstant.BASKETBALL_SCORE_EVENT_CODE.equals(matchSettleScore.getEventCode())) {
//            //校验进球是否确认
//            if (isMatchGoalStatusConfirm(matchSettleThirdScore.getThirdMatchId(), matchSettleScore.getEventCode())) {
//                //根据时效判定当前是否符合确认时间
//                GrayAreaSettleDto grayAreaSettleDto = new GrayAreaSettleDto();
//                long settleTemplateStartTime = System.currentTimeMillis();
//                MatchSettleTemplate matchSettleTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleScore.getStandardMatchId(),SettleTemplateTypeEnum.GRAY_AREA.code);
//                long settleTemplateCost = System.currentTimeMillis()-settleTemplateStartTime;
//                if (settleTemplateCost > slowSqlTime*4) {
//                    log.info("linkId::{}::eventId:{} settleNum:{} settleTemplateService query operation time cost:{}", linkedId, matchSettleThirdScore.getId(), matchSettleThirdScore.getSettleNum(), settleTemplateCost);
//                }
//                if (matchSettleTemplate!=null && !StringUtils.isAnyEmpty(matchSettleTemplate.getTemplateJson())) {
//                    List<GrayAreaSettleDto> grayAreaSettleDtoList = SettleTemplateJsonUtils.tansferGrayAreaList(matchSettleTemplate.getTemplateJson());
//                    Map<String, GrayAreaSettleDto> dataSourceGrayAreaOldMap = grayAreaSettleDtoList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
//                    grayAreaSettleDto = dataSourceGrayAreaOldMap.get(matchSettleCheckInfo.getDataSourceCode());
//                }
//
//                //灰色区间
//                if (FootBallMatchSettleScoreUtils.delaySettleSeconds(matchSettleScore, second,grayAreaSettleDto)) {
//                    log.info("{} :通过灰色区间加延迟校验,second: {},settleNum: {},grayAreaSettleDto: {}",linkedId,second,matchSettleScore.getSettleNum(),grayAreaSettleDto);
//                    //灰色区间无法做数据商判定确认
//                    createCheck = true;
//                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//                    matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//                    long updateSendTimesStartTime = System.currentTimeMillis();
//                    updateSendTimes(matchSettleCheckInfo);
//                    long updateSendTimesStartTimeCost = System.currentTimeMillis()-updateSendTimesStartTime;
//                    if (updateSendTimesStartTimeCost > slowSqlTime) {
//                        log.info("linkId::{}::eventId:{} settleNum:{} updateSendTimes redis operation time cost:{}", linkedId, matchSettleThirdScore.getId(), matchSettleThirdScore.getSettleNum(), updateSendTimesStartTimeCost);
//                    }
//                    checkCommonMatchSettleScoreEvent(matchSettleScore, matchSettleCheckInfo, createCheck);
//
//                } else {
//                    if (!send3Times) {
//                        //推送更新推送次数
//                        updateSendTimes(matchSettleCheckInfo);
//                        sendCheckMessage(matchSettleScore, matchSettleCheckInfo, true);
//                    }
//                }
//            }
//        }
//        log.info("linkId::{}::eventId:{} settleNum:{} checkMatchThirdSettleScores end", linkedId, matchSettleThirdScore.getId(),matchSettleThirdScore.getSettleNum());
//        return true;
//    }
//
//    private void updateSendTimes(MatchSettleCheckInfo matchSettleCheckInfo) {
//        String key = "SETTLE_SEND_TIMES:" + matchSettleCheckInfo.getId();
//        Object sends = redisService.get(key);
//        if (sends == null) {
//            Integer times = 1;
//            //存6小时
//            redisService.set(key, times, 60 * 60 * 6);
//        } else {
//            try {
//                Integer times = Integer.parseInt(sends.toString());
//                times++;
//                redisService.set(key, times, 60 * 60 * 6);
//            } catch (Exception e) {
//                log.error("MatchSettleCheckServiceImpl-updateSendTimes:", e);
//            }
//        }
//    }
//
//    private boolean getSend3TimesByCache(MatchSettleCheckInfo matchSettleCheckInfo) {
//        String key = "SETTLE_SEND_TIMES:" + matchSettleCheckInfo.getId();
//        Object sends = redisService.get(key);
//        if (sends == null) {
//            return false;
//        } else {
//            try {
//                Integer times = Integer.parseInt(sends.toString());
//                if (times >= 3) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (Exception e) {
//                log.error("MatchSettleCheckServiceImpl-getSend3TimesByCache:", e);
//                return false;
//            }
//        }
//    }
//
//    @Override
//    public boolean checkMatchThirdSettleEvent(MatchSettleThirdEvent matchSettleThirdEvent, String linkedId, Long second) {
//        log.info("linkId::{}::eventId:{} checkMatchThirdSettleEvent start",linkedId, matchSettleThirdEvent.getThirdEventSourceId());
//        //1.当前赛事是否切换到2.0如果不是则不生成数据
//        if (!isSettle2(matchSettleThirdEvent.getStandardMatchId())) {
//            return false;
//        }
//        //2.查询需要核对的标准比分ID 做关系绑定X
//        MatchSettleEventExample example = new MatchSettleEventExample();
//        example.createCriteria().andSettleNumEqualTo(matchSettleThirdEvent.getSettleNum()).andStandardMatchIdEqualTo(matchSettleThirdEvent.getStandardMatchId())
//                .andEventOrderEqualTo(matchSettleThirdEvent.getEventOrder()).andPeriodIdEqualTo(matchSettleThirdEvent.getPeriodId());
//        List<MatchSettleEvent> standardEvents = matchSettleEventMapper.selectByExample(example);
//        if (standardEvents.size() == 0) {
//            log.info("linkId::{}::eventId:{} 该事件变更没有搜索到需要核对的比分", linkedId, matchSettleThirdEvent.getThirdEventSourceId());
//            return false;
//        }
//        MatchSettleEvent matchSettleEvent = standardEvents.get(0);
//        if (matchSettleEvent.getStatus() == 3) {
//            log.info("linkId::{}::eventId:{} 该事件已经结算,无需重新核对比分", linkedId, matchSettleThirdEvent.getThirdEventSourceId());
//            return false;
//        }
//        //如果比分已经被回滚一次 则弃用数据商比分
//        if (matchSettleEvent.getSettleCount() != null && matchSettleEvent.getSettleCount() > 0) {
//            return false;
//        }
//        //3 redis 加锁 锁 结算比分ID 先忽略 TODO
//        //3.0 判断是否灰色区间更新状态
//        if (matchSettleThirdEvent.getIsGrey() != null && matchSettleThirdEvent.getIsGrey() >= 1) {
//            matchSettleEvent.setIsGrey(1);
//            matchSettleEvent.setModifyTime(System.currentTimeMillis());
//            matchSettleEvent.setCurrentEventStatus(1);
//            log.info("linkId::{}::eventId:{} 该事件修改的比分设置为灰色区间", linkedId, matchSettleThirdEvent.getThirdEventSourceId());
//            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//        }
//        //3.1 查询比分核对类是否存在
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleEvent.getId()).andCheckTypeEqualTo(1).andDataSourceCodeEqualTo(matchSettleThirdEvent.getDataSourceCode());
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        MatchSettleCheckInfo matchSettleCheckInfo = null;
//        //3.2 已经存在则修改比分
//        boolean createCheck = false;
//
//        if (list.size() != 0) {
//            if (list.size() > 1) {
//                log.error("linkId::{}::eventId:{} checkMatchThirdSettleScores :{}: 事件核对并发生成:{}", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), list.size());
//            }
//            matchSettleCheckInfo = list.get(0);
//            if (!StringUtils.isAnyEmpty(matchSettleThirdEvent.getOperater()) && (matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))) {
//                matchSettleCheckInfo.setUserName(matchSettleThirdEvent.getOperater() + "(" + matchSettleThirdEvent.getDataSourceCode() + ")");
//            }
//            //比分修正
//            SettleCheckUtils.copyMatchSettleEvent(matchSettleThirdEvent, matchSettleCheckInfo);
//
//            Long minPeriod = FootBallMatchSettleScoreUtils.get5MinPeriod(matchSettleThirdEvent.getPeriodId(), second);
//            matchSettleCheckInfo.setFiveMinSection(minPeriod.toString());
//
//            if(matchSettleThirdEvent.getIsGrey()!=1&&(matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("fa_card")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card"))){
//                //1.计算出角球15分钟区间
//                //2.设置15分钟区间
//                Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(matchSettleThirdEvent.getPeriodId(), second);
//                if(period15!=null){
//                    matchSettleCheckInfo.setFiveMinSection(period15.toString());
//                }
//            }
//
//            matchSettleCheckInfo.setIsGrey(matchSettleThirdEvent.getIsGrey());
////            if (!matchSettleCheckInfo.getDataSourceCode().equals("F01")){
//                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
////            }
//
//
//            matchSettleCheckInfo.setThirdSettleScoreEventId(matchSettleThirdEvent.getId());
//            matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//            log.info("linkId::{}::eventId:{} 比分修正 当前数据商比分核对数据:{}", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleCheckInfo);
//        } else {
//            //3.3 不存在创建核对比分
//            matchSettleCheckInfo = SettleCheckUtils.initMatchSettleEvent(matchSettleEvent, matchSettleThirdEvent);
//            matchSettleCheckInfo.setIsGrey(matchSettleThirdEvent.getIsGrey());
//            String period5 = SportPeriodConstant.FootballPeriod.get5MinCode(matchSettleThirdEvent.getPeriodId(), second);
//            //5分钟灰色区间不做编辑区间
//            if (matchSettleThirdEvent.getIsGrey() != 2) {
//                matchSettleCheckInfo.setFiveMinSection(period5);
//            }
//            if (matchSettleCheckInfo.getEventCode().equals("corner") || matchSettleCheckInfo.getEventCode().equals("fa_card") || matchSettleCheckInfo.getEventCode().equals("yellow_card") || matchSettleCheckInfo.getEventCode().equals("red_card")) {
//                matchSettleCheckInfo.setFiveMinSection(null);
//            }
//            //角球返回是1 则判断为15分钟灰色区间 todo
//            if (matchSettleThirdEvent.getIsGrey() != 1 && (matchSettleCheckInfo.getEventCode().equals("corner") || matchSettleCheckInfo.getEventCode().equals("fa_card") || matchSettleCheckInfo.getEventCode().equals("yellow_card") || matchSettleCheckInfo.getEventCode().equals("red_card"))) {
//                //1.计算出角球15分钟区间
//                //2.设置15分钟区间
//                Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(matchSettleThirdEvent.getPeriodId(), second);
//                if (period15 != null) {
//                    matchSettleCheckInfo.setFiveMinSection(period15.toString());
//                }
//            }
//
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            matchSettleCheckInfo.setThirdSettleScoreEventId(matchSettleThirdEvent.getId());
//            if (!StringUtils.isAnyEmpty(matchSettleThirdEvent.getOperater()) && (matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))) {
//                matchSettleCheckInfo.setUserName(matchSettleThirdEvent.getOperater() + "(" + matchSettleThirdEvent.getDataSourceCode() + ")");
//            }
//            matchSettleCheckInfoMapper.insert(matchSettleCheckInfo);
//            initDelaySettleEvent(matchSettleEvent,matchSettleCheckInfo);
//            log.info("linkId::{}::eventId:{} 插入当前数据商比分核对数据:{}", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleCheckInfo);
//            createCheck = true;
//            //BT 或者 1X的 继续结算
//            if (matchSettleCheckInfo.getDataSourceCode().equals("BT") || matchSettleCheckInfo.getDataSourceCode().equals("1X")) {
//                if (matchSettleEvent.getEventCode().equals("goal")) {
//                    confirmDataSourceGoalSettleEvent(matchSettleCheckInfo);
//                }
//            }
//            //BT 或者 RB 角球 继续结算
//
//            if (matchSettleCheckInfo.getDataSourceCode().equals("BT") || matchSettleCheckInfo.getDataSourceCode().equals("RB")
//                    ||matchSettleCheckInfo.getDataSourceCode().equals("BG")||matchSettleCheckInfo.getDataSourceCode().equals("KO") || matchSettleCheckInfo.getDataSourceCode().equals("PD")
//                    || matchSettleCheckInfo.getDataSourceCode().equals("PD2") || matchSettleCheckInfo.getDataSourceCode().equals("TS")|| matchSettleCheckInfo.getDataSourceCode().equals("F01")|| matchSettleCheckInfo.getDataSourceCode().equals("N01")|| matchSettleCheckInfo.getDataSourceCode().equals("LS")) {
//                if (matchSettleEvent.getEventCode().equals("corner") || matchSettleEvent.getEventCode().equals("fa_card")||matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")) {
//                    confirmDataSourceGoalSettleEvent(matchSettleCheckInfo);
//                }
//            }
//
////            if (matchSettleCheckInfo.getDataSourceCode().equals("F01") ) {
////                if (matchSettleEvent.getEventCode().equals("fa_card")||matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")||matchSettleEvent.getEventCode().equals("corner")) {
////                    confirmDataSourceGoalSettleEvent(matchSettleCheckInfo);
////                }
////            }
//
//        }
//
//        //4.触发比分核对 建议异步吧
//        log.info("linkId::{}::eventId:{} checkMatchThirdSettleEvent end", linkedId, matchSettleThirdEvent.getThirdEventSourceId());
//        //当前进球事件不用自动结算
////        checkCommonMatchSettleScoreEvent(matchSettleEvent,matchSettleCheckInfo,createCheck);
//
//        return true;
//    }
//
//    /**
//     * |公共比分核对功能方法|
//     * 如果五分钟开关打开了，才会核对 5分钟进球的区间。如果关闭的话，就不需要比对五分钟进球的区间
//     * 核心逻辑需要日志加强
//     * response: left:原有结果 right:是否结算
//     */
//    @Override
//    public Pair<Boolean, Boolean> checkCommonMatchSettleScoreEvent(Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo, boolean createCheck) {
//        long start = System.currentTimeMillis();
//        // 已经结算flag
//        boolean alreadySettled = false;
//        log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 事件比分核对开始", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//        //0.查询标准赛事的数据商自动结算状态
//        long matchSettleInfoStartTime = System.currentTimeMillis();
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleCheckInfo.getStandardMatchId());
//        long matchSettleInfoCost = System.currentTimeMillis()-matchSettleInfoStartTime;
//        log.info("eventId::{}::settleEventId:{} matchSettleInfoMapper query operation time cost:{}", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId(), matchSettleInfoCost);
//        String settleNum = "";
//        if (matchSettleScoreEvent instanceof MatchSettleEvent) {
//            MatchSettleEvent matchSettleEvent = (MatchSettleEvent) matchSettleScoreEvent;
//            settleNum = matchSettleEvent.getSettleNum();
//            MatchSettleEvent event = matchSettleEventMapper.selectByPrimaryKey(matchSettleEvent.getId());
//            if (event.getStatus().equals(SETTLED)){
//                matchDelaySettleInfoMapper.updateSettleStatusByScoreId(matchSettleEvent.getId(),SETTLED);
//                alreadySettled = true;
//                log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 该事件修改的比分已经结算,无需重新核对比分", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//                return Pair.of(false, alreadySettled);
//            }
//            //如果比分已经被回滚一次 则弃用数据商比分
//            if (event.getSettleCount() != null && event.getSettleCount() > 0) {
//                return Pair.of(false, alreadySettled);
//            }
//        } else if (matchSettleScoreEvent instanceof MatchSettleScore) {
//            MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
//            settleNum = matchSettleScore.getSettleNum();
//            MatchSettleScore score = matchSettleScoreMapper.selectByPrimaryKey(matchSettleScore.getId());
//            if (score.getStatus().equals(SETTLED)){
//                matchDelaySettleInfoMapper.updateSettleStatusByScoreId(matchSettleScore.getId(),SETTLED);
//                alreadySettled = true;
//                log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 该事件修改的比分已经结算,无需重新核对比分", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//                return Pair.of(false, alreadySettled);
//            }
//            //如果比分已经被回滚一次 则弃用数据商比分
//            if (score.getSettleCount() != null && score.getSettleCount() > 0) {
//                return Pair.of(false, alreadySettled);
//            }
//        }
//        //5分钟编码 计算5分钟区间
//        List<String> fiveMinSettleNums = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040",
//                "1041", "1042", "1043", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052", "1053");
//        //5分钟按钮不打开不做5分钟区间结算
//        if (matchSettleInfo.getFiveMinSwitch() == null || matchSettleInfo.getFiveMinSwitch() != 1) {
//            if (fiveMinSettleNums.contains(settleNum)) {
//                log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 5分钟按钮不打开不处理", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//                matchDelaySettleInfoMapper.updateSettleStatusByCheckInfoId(matchSettleCheckInfo.getId(),2);
//                return Pair.of(false, alreadySettled);
//            }
//        }
//        //篮球结算顺序拦截
//        if (matchSettleScoreEvent instanceof MatchSettleScore) {
//            MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
//            if (matchSettleInfo.getSportId().equals(2L)) {
//                if (!this.checkBasketPeriodScoreOrder(matchSettleScore)) {
//                    log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 篮球结算顺序拦截不处理", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//                    return Pair.of(false, alreadySettled);
//                }
//            }
//        }
//        //需求2477,联赛对应的数据源结算为关闭状态，只显示赛果不参与结算
//        Integer levelDataSourceStatus = matchSettleDataSourceConfigService.getTournamentLevelStatus(matchSettleCheckInfo.getStandardMatchId(), matchSettleCheckInfo.getDataSourceCode(), matchSettleCheckInfo.getEventCode());
//        if (levelDataSourceStatus == null || levelDataSourceStatus.equals(Constant.OUTRIGHT_ZERO)) {
//            log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 只显示赛果不参与结算", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//            matchDelaySettleInfoMapper.updateSettleStatusByCheckInfoId(matchSettleCheckInfo.getId(),2);
//            return Pair.of(false, alreadySettled);
//        }
//        //比分类型灰色区间不走数据商自动结算
//        List<MatchSettleCheckInfo> list =  this.searchSettleCheckInfoListByCheckInfo(matchSettleScoreEvent,matchSettleCheckInfo,matchSettleInfo);
//        //罚牌次序 角球次序才做事件延迟,   阶段的延迟在 灰色区间+延迟结算校验做 delaySettleSeconds
//        List<MatchSettleCheckInfo> oldList = new ArrayList<>(list);
//        log.info(matchSettleCheckInfo.getStandardMatchId()+"delayLog原有核对的条数: {}",oldList.size());
//        if (!matchSettleCheckInfo.getDataSourceCode().equals("PA")){
//            list = moveDelayCheckInfo(matchSettleCheckInfo.getStandardMatchId(),list);
//            log.info(matchSettleCheckInfo.getStandardMatchId()+"delayLog实际核对的条数: {}",list.size());
//        }
////        if (CollectionUtils.isEmpty(list)){
////            log.info("结算核对数据集合为空,停止进行后续结算settleNum: {},standardMatchId: {}",settleNum,matchSettleCheckInfo.getStandardMatchId());
////            return Pair.of(false, alreadySettled);
////        }
//        //阶段比分 数据商灰色区间不走自动结算
//        if (matchSettleScoreEvent instanceof MatchSettleScore) {
//            MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
//            if (matchSettleScore.getIsGrey() != null && matchSettleScore.getIsGrey() != 0 && (!matchSettleCheckInfo.getDataSourceCode().equals("PA"))) {
//                list = new ArrayList<>();
//            }
//        }
//        //设置灰色区间核对数量为2 [3139需求改成权重上限100,1家数据商就能触发结算]
//        Integer needCheckNumber = 1;
//        boolean settled = false;
//        //用来计算当前需要结算的录入核对数据 TODO
//        String checkKey = "";
//        //2.核对比分分组 进球角球会计算 5分钟 和15分钟
//        Map<String, List<MatchSettleCheckInfo>> checkGroupMap = null;
//        Map<String, List<MatchSettleCheckInfo>> oldCheckGroupMap = null;
//        if (matchSettleScoreEvent instanceof MatchSettleEvent && matchSettleInfo.getFiveMinSwitch() != null && matchSettleInfo.getFiveMinSwitch() == 1&&
//                (matchSettleCheckInfo.getEventCode().equals("goal")|| matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card")||matchSettleCheckInfo.getEventCode().equals("fa_card"))) {
//            checkGroupMap = SettleCheckUtils.groupByFiveMinSettleCheck(list);
//            checkKey = SettleCheckUtils.countSettleEventFiveMinCompareKey(matchSettleCheckInfo);
//            oldCheckGroupMap = SettleCheckUtils.groupByFiveMinSettleCheck(oldList);
//        } else {
//            //无需计算五分钟
//            checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
//            checkKey = SettleCheckUtils.countSettleCheckGroupKey(matchSettleCheckInfo);
//            oldCheckGroupMap = SettleCheckUtils.groupBySettleCheck(oldList);
//        }
//        if (matchSettleInfo.getSportId().equals(2L)) {
//            checkGroupMap = SettleCheckUtils.groupByBasketBallCheck(list);
//            oldCheckGroupMap = SettleCheckUtils.groupByBasketBallCheck(oldList);
//            checkKey = SettleCheckUtils.countSettleCheckGroupBasketballKey(matchSettleCheckInfo);
//        }
//        //分组循环计算
//        //查询结算权重模版 通过计算相同的权重之合来判断是否符合条件
//        MatchSettleTemplate matchSettleTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleCheckInfo.getStandardMatchId(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
//        if(matchSettleTemplate==null){
//            log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent matchSettleTemplate为null", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//            return Pair.of(false, alreadySettled);
//        }
//        log.info("eventId::{}::settleEventId:{}:TemplateJson {}",matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId(),matchSettleTemplate.getTemplateJson());
//        // 有核对通过flag
//        boolean hasPassCheck = false;
//        List<String> userLockList = new ArrayList<>();
//        Map<String, List<MatchSettleCheckInfo>> finalCheckGroupMap = new HashMap<>();
//        for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : checkGroupMap.entrySet()) {
////            Integer sameScoreNumber = entry.getValue().size();
//            //计算权重根据工具方法类
//            Integer dataSourceWeightSum = 0;
//            if (matchSettleInfo.getSportId().equals(1L)) {
//                dataSourceWeightSum = SettleTemplateWeightUtils.countFootballWeightDataSourceCheck(matchSettleTemplate, entry.getValue());
//            } else {
//                dataSourceWeightSum = SettleTemplateWeightUtils.countBasketballWeightDataSourceCheck(matchSettleTemplate, entry.getValue());
//            }
//            log.info("eventId::{}::settleEventId:{}:Template:matchId:{},dataSourceWeightSum:{},matchSettleCheckInfo:{}",matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getStandardMatchId(), dataSourceWeightSum,matchSettleCheckInfo);
//            if(dataSourceWeightSum>=100){
//                //判断当前通过的比分是否与输入的比分一致，不一致则不成功
//                boolean tag = entry.getKey().equals(checkKey);
//                log.info("eventId::{}::settleEventId:{}:Tag1matchSettleCheckInfo_id:{},matchSettleCheckInfo:{},Tag:{}",matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId(),matchSettleCheckInfo,tag);
//                if (tag){
//                    hasPassCheck = true;
//                    finalCheckGroupMap.put(entry.getKey(), entry.getValue());
//                } else {
//                    hasPassCheck = false;
//                }
//
//            }
//
//        }
//        for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
////            Integer sameScoreNumber = entry.getValue().size();
//            //计算权重根据工具方法类
//            Integer oldSum =0;
//            if(matchSettleInfo.getSportId().equals(1L)){
//                oldSum = SettleTemplateWeightUtils.countFootballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
//            }else {
//                oldSum = SettleTemplateWeightUtils.countBasketballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
//            }
//            log.info(matchSettleCheckInfo.getStandardMatchId()+":delayLog01-score.id:{} ,check.id: {},oldSum:{}",matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId(),oldSum);
//            if(oldSum>=100){
//                String key = "delaySettle:"+matchSettleCheckInfo.getSettleScoreEventId();
//                Object old = redisService.get(key);
//                if (null==old){
//                    log.info(matchSettleCheckInfo.getStandardMatchId()+":delayLog02-score.id:{} ,check.id: {}",matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
//                    //延迟结算时间
//                    DownSettleDto downSettleDto ;
//                    MatchSettleTemplate downTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleCheckInfo.getStandardMatchId(), SettleTemplateTypeEnum.COUNT_DOWEN.code);
//
//                    if (downTemplate!=null&&!StringUtils.isAnyEmpty(downTemplate.getTemplateJson())){
//                        List<DownSettleDto> dtos = SettleTemplateJsonUtils.tansferDownList(downTemplate.getTemplateJson());
//                        if (!CollectionUtils.isEmpty(dtos)&&!matchSettleCheckInfo.getDataSourceCode().equals("PA")){
//                            log.info(matchSettleCheckInfo.getStandardMatchId()+":delayLog03-score.id:{} ,check.id: {}",matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
//                            downSettleDto = dtos.get(0);
//                            Integer value = 0;
//                            if (matchSettleCheckInfo.getEventCode().equals("goal")){
//                                value = downSettleDto.getGoal15Min();
//                            }else  if (matchSettleCheckInfo.getEventCode().equals("corner")){
//                                value = downSettleDto.getCorner15Min();
//                            }else {
//                                value = downSettleDto.getBooking15Min();
//                            }
//                            redisService.set(key,value,7*24*3600);
//                            if (matchSettleCheckInfo.getCheckType()==1){
//                                log.info(matchSettleCheckInfo.getStandardMatchId()+":delayLog04-score.id:{} ,check.id: {}",matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
//                                wsPushService.pushStandardSettleScores(matchSettleCheckInfo.getStandardMatchId(),matchSettleCheckInfo.getEventCode());
//                            }
//                            if (matchSettleCheckInfo.getCheckType()==2){
//                                log.info(matchSettleCheckInfo.getStandardMatchId()+":delayLog05-score.id:{} ,check.id: {}",matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
//                                wsPushService.pushStandardSettleEvent(matchSettleCheckInfo.getStandardMatchId(),matchSettleCheckInfo.getEventCode());
//                            }
//                        }
//                    }
//
//                }
//            }
//
//        }
//        //如果是次序事件，然后不能通过结算则去掉5分钟/15分钟区间重新计算;结算的时候也去掉5分钟/15分钟次序事件的区间
//        if (!hasPassCheck) {
////            if (matchSettleScoreEvent instanceof MatchSettleEvent && (!matchSettleCheckInfo.getDataSourceCode().equals("PA"))) {
//            if (matchSettleScoreEvent instanceof MatchSettleEvent ) {
//                checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
//                checkKey = SettleCheckUtils.countSettleCheckGroupKey(matchSettleCheckInfo);
//                for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : checkGroupMap.entrySet()) {
////                    Integer sameScoreNumber = entry.getValue().size();
//                    //计算权重根据工具方法类
//                    Integer dataSourceWeightSum = 0;
//                    if (matchSettleInfo.getSportId().equals(1L)) {
//                        dataSourceWeightSum = SettleTemplateWeightUtils.countFootballWeightDataSourceCheck(matchSettleTemplate, entry.getValue());
//                    } else {
//                        dataSourceWeightSum = SettleTemplateWeightUtils.countBasketballWeightDataSourceCheck(matchSettleTemplate, entry.getValue());
//                    }
//                    log.info("eventId::{}::settleEventId:{}:Template:matchId:{},dataSourceWeightSum:{},matchSettleCheckInfo:{}",matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getStandardMatchId(), dataSourceWeightSum,matchSettleCheckInfo);
//                    if (dataSourceWeightSum >= 100) {
//                        //判断当前通过的比分是否与输入的比分一致，不一致则不成功 TODO
//                        boolean tag = entry.getKey().equals(checkKey);
//                        log.info("eventId::{}::settleEventId:{}:Tag2matchSettleCheckInfo_id:{},matchSettleCheckInfo:{},Tag:{}",matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId(),matchSettleCheckInfo,tag);
//                        if (tag){
//                            hasPassCheck = true;
//                            matchSettleCheckInfo.setFiveMinSection(null);
//                            finalCheckGroupMap.put(entry.getKey(), entry.getValue());
//                        } else {
//                            hasPassCheck = false;
//                        }
//                    }
//                }
//            }
//        }
//        //理论结算时间 //数据商数据根据比分创建时间定为结算时间
//        Long eventTime = this.countEventTime(matchSettleScoreEvent, matchSettleCheckInfo);
//        for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : finalCheckGroupMap.entrySet()) {
//            Integer settle_t1 = entry.getValue().get(0).getT1();
//            Integer settle_t2 = entry.getValue().get(0).getT2();
//            if (settled) {
//                continue;
//            }
//            if (matchSettleScoreEvent instanceof MatchSettleScore) {
//                MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
//                //新增校验
//                //数据商阶段比分结算顺序拦截，如果之前的比分没有结算，则不能编辑当前的比分
//                if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE && !this.isAllPeriodScoresBeforeSettled(matchSettleScore)) {
//                    log.info("eventId::{}::settleEventId:{} 阶段比分之前有未结算阶段比分", matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId());
//                    // 请确保上一个比分已结算。
//                    continue;
//                }
//            }
//            if (hasPassCheck) {
//                //1.进行自动结算 只需要结算一次 所以进去后更新结算状态如果已经结算则
////                        footballMatchScoresSettleApi.autoSettle();
//                if (settle_t1 != null && settle_t2 != null) {
//                    if (settle_t1.equals(matchSettleCheckInfo.getT1()) && settle_t2.equals(matchSettleCheckInfo.getT2())) {
//                        if (matchSettleScoreEvent instanceof MatchSettleScore) {
//                            MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
//                            if (!this.checkSettleScoreAndAutoSettleNonEvent(matchSettleScore, matchSettleCheckInfo)) {
//                                return Pair.of(false, alreadySettled);
//                            }
//                        }
//
//                        //统计理论结算时间 无数据商 取当前结算时间
//                        this.updateEventTime(eventTime, matchSettleScoreEvent);
//                        //自动结算主逻辑
//                        this.autoSettle(matchSettleScoreEvent, matchSettleCheckInfo);
//                        alreadySettled = true;
//                    } else {
//                        log.error("eventId::{}::settleEventId:{}:已满足条件但未触发结算,比分类型:{}", matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getCheckType());
//                    }
//                } else {
//                    //统计理论结算时间 无数据商 取当前结算时间
//                    this.updateEventTime(eventTime, matchSettleScoreEvent);
//                    this.autoSettle(matchSettleScoreEvent, matchSettleCheckInfo);
//                    alreadySettled = true;
//                }
//                settled = true;
//                continue;
//            }
//        }
//        //结算成功的话 锁定赛事结算操作失败人员
//        if (settled) {
//            for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : checkGroupMap.entrySet()) {
//                if (finalCheckGroupMap.containsKey(entry.getKey())) {
//                    continue;
//                }
//                for (MatchSettleCheckInfo settleCheckInfo : entry.getValue()) {
//                    if (StringUtils.isNotEmpty(settleCheckInfo.getUserName())) {
//                        if (settleCheckInfo.getUserName().equals(matchSettleCheckInfo.getUserName())) {
//                            continue;
//                        }
//                        if(dataSourceCodeManually.contains(settleCheckInfo.getDataSourceCode())) {
//                            userLockList.add(settleCheckInfo.getUserName());
//                        }
//                    }
//                }
//            }
//            if (userLockList.size() != 0) {
//                log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 锁定赛事结算操作失败人员", matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId());
//                lockUserListByCheckPass(matchSettleCheckInfo.getStandardMatchId(), userLockList);
//            }
//        }
//        if (!hasPassCheck) {
//            sendCheckMessage(matchSettleScoreEvent, matchSettleCheckInfo, true);
//            //比分数量>=需要的数量但 比分不一致返回结算失败
//
//            if (list.size() >= needCheckNumber) {
//                if (list.size() == 1 && matchSettleCheckInfo.getDataSourceCode().equals("PA")) { //人工第一次编辑
//                    log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 人工第一次编辑", matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId());
//                    return Pair.of(false, alreadySettled);
//                } else {
//                    log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 非人工第一次编辑", matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId());
//                    return Pair.of(true, alreadySettled);
//                }
//            }
//        }
//        log.info("eventId::{}::settleEventId:{}:checkCommonMatchSettleScoreEvent 消耗{}ms事件比分核对结束", matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(), System.currentTimeMillis()-start);
//        return Pair.of(false, alreadySettled);
//    }
//
//    private void validateBasketBallSettleScore(MatchSettleScore matchSettleScore) {
//        MatchSettleThirdScoreExample example = new MatchSettleThirdScoreExample();
//        List<String> l = new ArrayList();
//        l.add(matchSettleScore.getSettleNum());
//        example.createCriteria().andSettleNumIn(l).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()) ;
//        List<MatchSettleThirdScore> matchSettleThirdScores =matchSettleThirdScoreMapper.selectByExample(example);
//        boolean tag =false;
//        if (!matchSettleThirdScores.isEmpty()){
//            for (int i =0;i<matchSettleThirdScores.size();i++ ){
//                if (!matchSettleThirdScores.get(i).getT1().equals(matchSettleScore.getT1())||!matchSettleThirdScores.get(i).getT2().equals(matchSettleScore.getT2())){
//                    tag = true;
//                }
//            }
//        }
//        if (tag){
//            matchSettleScore.setCurrentEventTag(1);
//            matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScore.getStandardMatchId());
//            matchSettleInfo.setCurrentEventTag(1);
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//        }
//    }
//
//    /**
//     * 1.如果关闭数据商开关，则 PA事件(人工录入) 以及主事件源的事件 可以触发结算，
//     * 其他事件不能触发结算，如果开启数据商自动结算开关，则可以查询所有关联数据源的比分和事件 做自动核对
//     * 2.是灰色区间比分的时候 有不同的数据商比分核对机制以及 灰色区间触发结算机制 ，如果是事件
//     * 灰色区间应该正常结算但不触发5分钟玩法结算，如果是比分则只能通过PA比分(人工录入比分)触发结算
//     * 3.因为部分情况会造成redis锁失效，所以并发高的时候可能会触发单个事件源产生多个相同的阶段比分，从而数数的时候>1而导致
//     * 单数据商触发结算，所以开头需要先去除当前触发自动结算核对的数据源(除了PA人工录入的之外)，然后上述逻辑得到数据商查询条件后，将当前触发的事件源的数据
//     * 重新加入数据商查询条件，从而得到去重的效果
//     */
//    private List<MatchSettleCheckInfo> searchSettleCheckInfoListByCheckInfo(Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo, MatchSettleInfo matchSettleInfo) {
//        log.info("eventId::{}::settleEventId:{}:searchSettleCheckInfoListByCheckInfo 初始化参数 matchSettleScoreEvent:{} matchSettleInfo:{}",
//                matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId(),matchSettleScoreEvent, matchSettleInfo);
//        List<String> dataSourceCodes = new ArrayList<>();
//        //[3139需求]将足球数据商开关做单独事件控制
//        String eventCode = matchSettleCheckInfo.getEventCode();
//        MatchSettleDataSourceSwitchExample example = new MatchSettleDataSourceSwitchExample();
//        Map<String, Integer> map = null;
//        if (eventCode.equals("corner")) {
//            example.createCriteria().andCornerEqualTo("1").andSportIdEqualTo(matchSettleInfo.getSportId());
//        } else if (eventCode.equals("goal") || eventCode.equals("kick_off") || eventCode.equals("score_change")) {
//            example.createCriteria().andGoalEqualTo("1").andSportIdEqualTo(matchSettleInfo.getSportId());
//        } else {
//            example.createCriteria().andBookingEqualTo("1").andSportIdEqualTo(matchSettleInfo.getSportId());
//        }
//        List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchMapper.selectByExample(example);
//        if (null != switches && switches.size() > 0) {
//            dataSourceCodes = switches.stream().map(MatchSettleDataSourceSwitch::getDataSourceCode).distinct().collect(Collectors.toList());
//        }
//        dataSourceCodes.add("PA");
//        log.info("eventId::{}::settleEventId:{}:searchSettleCheckInfoListByCheckInfo 数据源开关情况", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//        //如果赛事级数据商关闭，则只查主数据源 + PA
//        if (matchSettleInfo.getIsAutoSettleDataSource() != null && matchSettleInfo.getIsAutoSettleDataSource() == 0) {
//            dataSourceCodes = this.getNotMainEventThirdSources(matchSettleInfo, matchSettleCheckInfo.getDataSourceCode());
//        }
//        if (!matchSettleCheckInfo.getDataSourceCode().equals("PA")) {
//            dataSourceCodes.remove(matchSettleCheckInfo.getDataSourceCode());
//        }
//        log.info("eventId::{}::settleEventId:{}:searchSettleCheckInfoListByCheckInfo 赛事级切换后数据源开关情况", matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
//        //--新增条件 20230502 去除重复数据商编码
//        //matchSettleScoreEvent 判断 如果是 事件的 如果是事件的则直接查询，如果是比分的 灰色区间则 不需要查询数据商，只需要查询人工录入的
//        boolean scoreIsGray = false;
//        if (matchSettleScoreEvent instanceof MatchSettleEvent) {
//            MatchSettleEvent matchSettleEvent = (MatchSettleEvent) matchSettleScoreEvent;
//            return this.searchEventSettleCheckByCheckInfo(dataSourceCodes, scoreIsGray, matchSettleEvent, matchSettleCheckInfo, matchSettleInfo);
//        } else if (matchSettleScoreEvent instanceof MatchSettleScore) {
//            MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
//            if ((matchSettleScore.getIsGrey() != null && matchSettleScore.getIsGrey() == 1) || (matchSettleCheckInfo.getIsGrey() != null && matchSettleCheckInfo.getIsGrey() == 1)) {
//                scoreIsGray = true;
//            }
//            return this.searchScoreSettleCheckByCheckInfo(dataSourceCodes, scoreIsGray, matchSettleScore, matchSettleCheckInfo, matchSettleInfo);
//        }
//        //事件的灰色区间查全部
//        return new ArrayList<>();
//    }
//
//    //事件自动结算审核查询
//    private List<MatchSettleCheckInfo> searchEventSettleCheckByCheckInfo(List<String> dataSourceCodes, boolean scoreIsGray, MatchSettleEvent matchSettleScore, MatchSettleCheckInfo matchSettleCheckInfo, MatchSettleInfo matchSettleInfo) {
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        //玩法级赛事自动结算
//        boolean isEventAutoSettle = this.getDataSouceAutoSettle(matchSettleInfo, matchSettleCheckInfo, matchSettleScore.getSettleNum());
//        //关闭数据商只查人工+主数据商
//        if (!isEventAutoSettle) {
//            //关闭数据商自动结算 获取主事件源+PA
//            List<String> mainEventThirdSources = this.getNotMainEventThirdSources(matchSettleInfo, matchSettleCheckInfo.getDataSourceCode());
//            checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleCheckInfo.getSettleScoreEventId())
//                    .andStandardMatchIdEqualTo(matchSettleCheckInfo.getStandardMatchId()).andCheckStatusEqualTo(MatchSettleCheckConstant.CheckStatus.CONFIRM)
//                    .andDataSourceCodeIn(mainEventThirdSources);
//        } else {
//            checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleCheckInfo.getSettleScoreEventId()).andStandardMatchIdEqualTo(matchSettleCheckInfo.getStandardMatchId()).andCheckStatusEqualTo(MatchSettleCheckConstant.CheckStatus.CONFIRM)
//                    //新增条件 20230502 去除重复数据商编码
//                    .andDataSourceCodeIn(dataSourceCodes);
//        }
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        if (!matchSettleCheckInfo.getDataSourceCode().equals("PA")) {
//            if (!isEventAutoSettle) {
//                dataSourceCodes = this.getNotMainEventThirdSources(matchSettleInfo, matchSettleCheckInfo.getDataSourceCode());
//                if (dataSourceCodes.contains(matchSettleCheckInfo.getDataSourceCode())) {
//                    list.add(matchSettleCheckInfo);
//                }
//            } else {
//                list.add(matchSettleCheckInfo);
//            }
//        }
//        return list;
//    }
//
//    //比分自动结算审核查询下
//    private List<MatchSettleCheckInfo> searchScoreSettleCheckByCheckInfo(List<String> dataSourceCodes, boolean scoreIsGray, MatchSettleScore matchSettleScore, MatchSettleCheckInfo matchSettleCheckInfo, MatchSettleInfo matchSettleInfo) {
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        //玩法级赛事自动结算
//        boolean isEventAutoSettle = this.getDataSouceAutoSettle(matchSettleInfo, matchSettleCheckInfo, matchSettleScore.getSettleNum());
//        if (scoreIsGray == false) {
//            //不是灰色区间
//            if (!isEventAutoSettle) {
//                //关闭数据商自动结算 获取主事件源+PA
//                List<String> mainEventThirdSources = this.getNotMainEventThirdSources(matchSettleInfo, matchSettleCheckInfo.getDataSourceCode());
//                checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleCheckInfo.getSettleScoreEventId())
//                        .andStandardMatchIdEqualTo(matchSettleCheckInfo.getStandardMatchId()).andCheckStatusEqualTo(MatchSettleCheckConstant.CheckStatus.CONFIRM)
//                        .andDataSourceCodeIn(mainEventThirdSources);
//            } else {
//                checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleCheckInfo.getSettleScoreEventId())
//                        .andStandardMatchIdEqualTo(matchSettleCheckInfo.getStandardMatchId()).andCheckStatusEqualTo(MatchSettleCheckConstant.CheckStatus.CONFIRM)
//                        //新增条件 20230502 去除重复数据商编码
//                        .andDataSourceCodeIn(dataSourceCodes);
//            }
//            List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//            if (!matchSettleCheckInfo.getDataSourceCode().equals("PA")) {
//                if (!isEventAutoSettle) {
//                    dataSourceCodes = this.getNotMainEventThirdSources(matchSettleInfo, matchSettleCheckInfo.getDataSourceCode());
//                    if (dataSourceCodes.contains(matchSettleCheckInfo.getDataSourceCode())) {
//                        list.add(matchSettleCheckInfo);
//                    }
//                } else {
//                    list.add(matchSettleCheckInfo);
//                }
//            }
//            return list;
//        } else {
//
//            //比分的灰色区间只查人工
//            checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(matchSettleCheckInfo.getSettleScoreEventId())
//                    .andStandardMatchIdEqualTo(matchSettleCheckInfo.getStandardMatchId()).andCheckStatusEqualTo(MatchSettleCheckConstant.CheckStatus.CONFIRM)
//                    .andDataSourceCodeEqualTo("PA");
//            List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//            return list;
//        }
//
//
//    }
//
//    private boolean getDataSouceAutoSettle(MatchSettleInfo matchSettleInfo, MatchSettleCheckInfo matchSettleCheckInfo, String settleNum) {
//        //1.赛事级优先
//        if (matchSettleInfo.getIsAutoSettleDataSource() == null || matchSettleInfo.getIsAutoSettleDataSource() == 0) {
//            return false;
//        }
//        if (matchSettleInfo.getSportId() == 2 && (!basketball6Mns.contains(settleNum))) {
//            return true;
//        }
//
//        //2.赛事级打开则 玩法级优先
//        if (matchSettleCheckInfo.getEventCode().equals("goal") || matchSettleCheckInfo.getEventCode().equals("kick_off") || matchSettleCheckInfo.getEventCode().equals("score_change")) {
//            Integer goalAutoSettle = matchSettleInfo.getGoalAutoSettleDataSource() == null ? 0 : matchSettleInfo.getGoalAutoSettleDataSource();
//            return goalAutoSettle == 1 ? true : false;
//        } else if (matchSettleCheckInfo.getEventCode().equals("corner")) {
//            Integer cornerAutoSettle = matchSettleInfo.getCornerAutoSettleDataSource() == null ? 0 : matchSettleInfo.getCornerAutoSettleDataSource();
//            return cornerAutoSettle == 1 ? true : false;
//        } else {
//            Integer faAutoSettle = matchSettleInfo.getBookingAutoSettleDataSource() == null ? 0 : matchSettleInfo.getBookingAutoSettleDataSource();
//            return faAutoSettle == 1 ? true : false;
//        }
//    }
//
//    /**
//     * 查询标准赛事的主事件源的数据商编码list
//     */
//    private List<String> getNotMainEventThirdSources(MatchSettleInfo matchSettleInfo, String sourceCode) {
//        List<String> list = new ArrayList<>();
//        list.add("PA");
//        if (!matchSettleInfo.getSportId().equals(1l)){
//            List<Long> standardMatchIds = new ArrayList<>();
//            standardMatchIds.add(matchSettleInfo.getStandardMatchId());
//            List<StandardSportMarketSell> sells = standardSportMarketSellService.getItems(standardMatchIds);
//            if (sells.size() == 0) {
//                return list;
//            }
//            String businessEvent = sells.get(0).getBusinessEvent();
//            //如果是标准赛事的开售事件源的则进入 主事件生成逻辑
//            list.add(businessEvent);
//        }
//        //新增条件 20230502 去除重复数据商编码--
//        if (!sourceCode.equals("PA")) {
//            list.remove(sourceCode);
//        }
//        return list;
//    }
//
//    private Long countEventTime(Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo) {
//        Long eventTime = 0l;
//        if (matchSettleScoreEvent instanceof MatchSettleScore) {
//            MatchSettleScore settleScore = (MatchSettleScore) matchSettleScoreEvent;
//            eventTime = searchEventTimeByScores(settleScore);
//        } else if (matchSettleScoreEvent instanceof MatchSettleEvent) {
//            MatchSettleEvent matchSettleEvent = (MatchSettleEvent) matchSettleScoreEvent;
//            eventTime = searchEventTimeByEvent(matchSettleEvent, matchSettleCheckInfo);
//        }
//        if (eventTime == 0l) {
//            eventTime = matchSettleCheckInfo.getModifyTime();
//        }
//        return eventTime;
//    }
//
//
//    private void updateEventTime(Long eventTime, Object matchSettleScoreEvent) {
//        if (eventTime.equals(0l)) {
//            eventTime = System.currentTimeMillis();
//        }
//        if (matchSettleScoreEvent instanceof MatchSettleScore) {
//            MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
//            matchSettleScore.setEventTime(eventTime);
//        }
//        if (matchSettleScoreEvent instanceof MatchSettleEvent) {
//            MatchSettleEvent matchSettleEvent = (MatchSettleEvent) matchSettleScoreEvent;
//            matchSettleEvent.setEventTime(eventTime);
//        }
//    }
//
//    /**
//     * 锁定赛事操作人员
//     */
//    @Override
//    public boolean lockUserListByCheckPass(Long standardMatchId, List<String> userNameList) {
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//        String limitArrayStr = matchSettleInfo.getLimitUserArray();
//        JSONArray limitArray = new JSONArray();
//        if (StringUtils.isNotEmpty(limitArrayStr)) {
//            limitArray = JSONArray.parseArray(limitArrayStr);
//        }
//        //篮球用旧审核员
//        if (matchSettleInfo.getSportId().equals(2l)) {
//            JSONArray jsonArray = JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray());
//            JSONArray newArray = new JSONArray();
//            for (Object o : jsonArray) {
//                if (o == null) {
//                    continue;
//                }
//                if (!userNameList.contains(o.toString())) {
//                    newArray.add(o.toString());
//                }
//            }
//            matchSettleInfo.setAuditorActiveArray(newArray.toJSONString());
//        } else if (matchSettleInfo.getSportId().equals(1L)) {
//            //足球尝试新 审核员
//            try {
//                AuditorFootBallJsonVo auditorFootBallJsonVo = JSONObject.parseObject(matchSettleInfo.getAuditorActiveArray(), AuditorFootBallJsonVo.class);
//                List<String> cornerList = new ArrayList<>();
//                List<String> goalList = new ArrayList<>();
//                List<String> facardList = new ArrayList<>();
//                for (String s : auditorFootBallJsonVo.getCornerAuditorList()) {
//                    if (s == null) {
//                        continue;
//                    }
//                    if (!userNameList.contains(s)) {
//                        cornerList.add(s);
//                    }
//                }
//                for (String s : auditorFootBallJsonVo.getGoalAuditorList()) {
//                    if (s == null) {
//                        continue;
//                    }
//                    if (!userNameList.contains(s)) {
//                        goalList.add(s);
//                    }
//                }
//                for (String s : auditorFootBallJsonVo.getFacardAuditorList()) {
//                    if (s == null) {
//                        continue;
//                    }
//                    if (!userNameList.contains(s)) {
//                        facardList.add(s);
//                    }
//                }
//                auditorFootBallJsonVo.setCornerAuditorList(cornerList);
//                auditorFootBallJsonVo.setFacardAuditorList(facardList);
//                auditorFootBallJsonVo.setGoalAuditorList(goalList);
//                matchSettleInfo.setAuditorActiveArray(JSONObject.toJSONString(auditorFootBallJsonVo));
//            } catch (Exception e) {
//                //报错用旧审核员
//                JSONArray jsonArray = JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray());
//                JSONArray newArray = new JSONArray();
//                for (Object o : jsonArray) {
//                    if (o == null) {
//                        continue;
//                    }
//                    if (!userNameList.contains(o.toString())) {
//                        newArray.add(o.toString());
//                    }
//                }
//                matchSettleInfo.setAuditorActiveArray(newArray.toJSONString());
//            }
//        }
//
//        limitArray.addAll(userNameList);
//        matchSettleInfo.setLimitUserArray(limitArray.toJSONString());
//        matchSettleInfo.setModifyTime(System.currentTimeMillis());
//        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//        //赛事结算2.0审核失败人员预警
//        sendMango(matchSettleInfo.getSportId(), standardMatchId, userNameList);
//        return false;
//    }
//
//    /**
//     * 推送核对比分到下一个人员X
//     */
//    @Override
//    public boolean sendCheckMessage(Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo, boolean createCheck) {
//        log.info("eventId::{}:: sendCheckMessage start", matchSettleCheckInfo.getThirdSettleScoreEventId());
//        Integer checkNumber = 0;
//        if (matchSettleScoreEvent instanceof MatchSettleScore) {
//            MatchSettleScore score = (MatchSettleScore) matchSettleScoreEvent;
//            checkNumber = score.getCheckNumber();
//        }
//        if (matchSettleScoreEvent instanceof MatchSettleEvent) {
//            MatchSettleEvent event = (MatchSettleEvent) matchSettleScoreEvent;
//            checkNumber = event.getCheckNumber();
//        }
//        boolean needSendCheck = false;
//        //1.计算当前需要核对的次序
//        if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
//            //1.1数据商的无需变更次序或者标记为1
//            if (checkNumber == null || checkNumber == 0) {
//                checkNumber = 1;
//            }
//            //1.2判断是否已经创建如果已经创建1则无需推送
//            if (createCheck) {
//                needSendCheck = true;
//            }
//        } else {
//            //2 人工编辑的需要推送到下一个 次序+1
//            needSendCheck = true;
//            if (checkNumber == null || checkNumber == 0) {
//                checkNumber = 1;
//            } else {
//
//                //如果当前操作人是操盘手，则无需更改次序
//                MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleCheckInfo.getStandardMatchId());
//                String str = matchSettleInfo.getAuditorJson();
////                JSONArray array = JSONArray.parseArray(str);
//                //否则是审核员的话才需要更改次序
////                if (array.contains(matchSettleCheckInfo.getUserName())) {
////                    checkNumber++;
////                }
//                if (str.contains(matchSettleCheckInfo.getUserName())) {
//                    checkNumber++;
//                }
//            }
//        }
//        if (needSendCheck) {
//            log.info("eventId::{}:: sendCheckMessage 更新核对比分次序", matchSettleCheckInfo.getThirdSettleScoreEventId());
//            //3.更新核对比分次序
//            if (matchSettleScoreEvent instanceof MatchSettleScore) {
//                MatchSettleScore score = (MatchSettleScore) matchSettleScoreEvent;
//                if (checkNumber >= score.getCheckNumber()) {
//                    score.setCheckNumber(checkNumber);
//                }
//                matchSettleScoreMapper.updateByPrimaryKey(score);
//            }
//            if (matchSettleScoreEvent instanceof MatchSettleEvent) {
//                MatchSettleEvent event = (MatchSettleEvent) matchSettleScoreEvent;
//                if (checkNumber >= event.getCheckNumber()) {
//                    event.setCheckNumber(checkNumber);
//                }
//                matchSettleEventMapper.updateByPrimaryKey(event);
//            }
//
//            //根据 checkNumber 获得当前人员
//            String checkUserName = getNextAuthorName(matchSettleCheckInfo, checkNumber);
//            //生成新的核对记录，方便前端动态查询
//            if (StringUtils.isNotEmpty(checkUserName)) {
//                MatchSettleCheckInfo nextCheckInfo = new MatchSettleCheckInfo();
//                SettleCheckUtils.copyProperties(matchSettleCheckInfo, nextCheckInfo);
//                nextCheckInfo.setUserName(checkUserName);
//                nextCheckInfo.setCheckNumber(checkNumber);
//                nextCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
//                //人工核对需要编码为PA
//                nextCheckInfo.setDataSourceCode("PA");
//                //1.判断是否存在  一个人只能录入一次比分
//                if (matchSettleScoreEvent instanceof MatchSettleScore) {
//                    MatchSettleScore score = (MatchSettleScore) matchSettleScoreEvent;
//                    nextCheckInfo.setEventCode(score.getEventCode());
//                }
//                if (matchSettleScoreEvent instanceof MatchSettleEvent) {
//                    MatchSettleEvent event = (MatchSettleEvent) matchSettleScoreEvent;
//                    nextCheckInfo.setEventCode(event.getEventCode());
//                }
//                //2.不存在则插入
//                this.saveOrUpdateCheckInfo(nextCheckInfo);
//            }
//            //4.推送WS，无需推送审核员名字，前端收到ws推送后，会主动调用接口查询最新状态
//            //5.判断当前这个check是否已经发送，如果已经发送则无需再推送
//            if (this.checkIfNotSend(matchSettleCheckInfo)) {
//                MatchListSettleDto matchListSettleDto = new MatchListSettleDto(matchSettleCheckInfo.getStandardMatchId(), matchSettleCheckInfo.getEventCode(),
//                        matchSettleCheckInfo.getId(), matchSettleCheckInfo.getSettleScoreEventId(), 1);
//                wsPushService.pushSettleMatchList(matchListSettleDto);
//            }
//        }
//        log.info("eventId::{}:: sendCheckMessage end", matchSettleCheckInfo.getThirdSettleScoreEventId());
//        return false;
//    }
//
//    public boolean checkIfNotSend(MatchSettleCheckInfo matchSettleCheckInfo) {
//        String key = "SETTLE_SCORES_CHECK_INFO_SEND:" + matchSettleCheckInfo.getId();
//        Object o = redisService.get(key);
//        if (o == null) {
//            redisService.set(key, key, 9000);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public MatchSettleCheckInfo searchCheckInfoByUser(Long scoreEventId, Long standardMatchId, String userName) {
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        checkInfoExample.createCriteria().andSettleScoreEventIdEqualTo(scoreEventId)
//                .andUserNameEqualTo(userName).andStandardMatchIdEqualTo(standardMatchId);
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        if (CollectionUtil.isEmpty(list)) {
//            return null;
//        } else if (list.size() > 1) {
//            log.error("::{} 的记录在用户:{}X 下存在并发记录", scoreEventId, userName);
//        }
//        return list.get(0);
//    }
//
//    @Override
//    public void searchCheckStatusByScoresList(List<MatchSettleScoreDto> matchSettleScoreDtos, String OperatorName) {
//        //有WS推送的情况这个时候没操作人
//        if (StringUtils.isEmpty(OperatorName)) {
//            return;
//        }
//        Map<Long, MatchSettleScoreDto> matchSettleScoreDtoMap = new HashMap<>();
//        List<Long> ids = new ArrayList<>();
//        for (MatchSettleScoreDto matchSettleScoreDto : matchSettleScoreDtos) {
//            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
//            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
//        }
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDtos.get(0).getStandardMatchId());
//        if (StringUtils.isNotEmpty(matchSettleInfo.getLimitUserArray())) {
//            JSONArray array = JSONArray.parseArray(matchSettleInfo.getLimitUserArray());
//            if (array.contains(OperatorName)) {
//                for (MatchSettleScoreDto matchSettleScoreDto : matchSettleScoreDtos) {
//                    matchSettleScoreDto.setNeedCheck(0);
//                }
//                return;
//            }
//        }
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        checkInfoExample.createCriteria().andStandardMatchIdEqualTo(matchSettleScoreDtos.get(0).getStandardMatchId())
//                .andUserNameEqualTo(OperatorName).andSettleScoreEventIdIn(ids);
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
//            MatchSettleScoreDto matchSettleScoreDto = matchSettleScoreDtoMap.get(matchSettleCheckInfo.getSettleScoreEventId());
//            matchSettleScoreDto.setNeedCheck(0);
//            if (matchSettleScoreDto != null) {
//                if (!(matchSettleCheckInfo.getCheckStatus() != null && matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM)) {
//                    matchSettleScoreDto.setNeedCheck(1);
//                }
//                if (matchSettleScoreDto.getStatus() != SETTLED) {
//                    matchSettleScoreDto.setStatus(matchSettleCheckInfo.getCheckStatus());
//                } else {
//                    matchSettleScoreDto.setNeedCheck(0);
//                }
//            }
//        }
//    }
//
//    private MatchSettleInfo getMatchSettleInfoByStandardMatchId(Long standardMatchId) {
//        MatchSettleInfoExample example = new MatchSettleInfoExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
//        List<MatchSettleInfo> list = matchSettleInfoMapper.selectByExample(example);
//        if (list.size() == 0) {
//            return null;
//        } else {
//            return list.get(0);
//        }
//    }
//
//    @Override
//    public void searchCheckStatusByEventList(List<MatchSettleEventDto> matchSettleScoreDtos, String OperatorName) {
//        //有WS推送的情况这个时候没操作人
//        if (StringUtils.isEmpty(OperatorName)) {
//            return;
//        }
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDtos.get(0).getStandardMatchId());
//        if (StringUtils.isNotEmpty(matchSettleInfo.getLimitUserArray())) {
//            JSONArray array = JSONArray.parseArray(matchSettleInfo.getLimitUserArray());
//            if (array.contains(OperatorName)) {
//                for (MatchSettleEventDto matchSettleScoreDto : matchSettleScoreDtos) {
//                    matchSettleScoreDto.setNeedCheck(0);
//                }
//                return;
//            }
//        }
//        Map<Long, MatchSettleEventDto> matchSettleScoreDtoMap = new HashMap<>();
//        List<Long> ids = new ArrayList<>();
//        for (MatchSettleEventDto matchSettleScoreDto : matchSettleScoreDtos) {
//            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
//            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
//        }
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        checkInfoExample.createCriteria().andStandardMatchIdEqualTo(matchSettleScoreDtos.get(0).getStandardMatchId())
//                .andUserNameEqualTo(OperatorName).andSettleScoreEventIdIn(ids);
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
//            MatchSettleEventDto matchSettleScoreDto = matchSettleScoreDtoMap.get(matchSettleCheckInfo.getSettleScoreEventId());
//            matchSettleScoreDto.setNeedCheck(0);
//            if (matchSettleScoreDto != null) {
//                if (!(matchSettleCheckInfo.getCheckStatus() != null && matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM)) {
//                    matchSettleScoreDto.setNeedCheck(1);
//                }
//                //如果普通审核员进来，需要返回他个人的状态。除非数据已经结算
//                if (matchSettleScoreDto.getStatus() != SETTLED) {
//                    matchSettleScoreDto.setStatus(matchSettleCheckInfo.getCheckStatus());
//                } else {
//                    matchSettleScoreDto.setNeedCheck(0);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void autoSettle(Object matchSettleScoreEventInfo, MatchSettleCheckInfo checkInfo) {
//        log.info("eventId::{}::autoSettle 进入自动结算流程,matchId:{},scoreEventId:{}", checkInfo.getThirdSettleScoreEventId(), checkInfo.getStandardMatchId(), checkInfo.getSettleScoreEventId());
//        if (matchSettleScoreEventInfo instanceof MatchSettleScore) {
//            //比分结算
//            this.settleMatchScore((MatchSettleScore) matchSettleScoreEventInfo, checkInfo);
//        } else if (matchSettleScoreEventInfo instanceof MatchSettleEvent) {
//            //事件结算
//            this.settleMatchSettleEvent((MatchSettleEvent) matchSettleScoreEventInfo, checkInfo);
//        } else {
//            log.error("eventId::{}::autoSettle checkInfo:{}:传入类型错误", checkInfo.getThirdSettleScoreEventId(), checkInfo);
//        }
//        updateMatchCurrentEventStatus(checkInfo.getStandardMatchId());
//        log.info("eventId::{}::autoSettle end", checkInfo.getThirdSettleScoreEventId());
//    }
//
//    /**
//     * 获取下一个审核员
//     */
//    public String getNextAuthorName(MatchSettleCheckInfo matchSettleCheckInfo, Integer checkNumber) {
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleCheckInfo.getStandardMatchId());
//        if (matchSettleInfo == null) {
//            log.error("standardMatchId:{},找不到赛事结算记录", matchSettleCheckInfo.getStandardMatchId());
//            return null;
//        }
//
//        String arrayStr = matchSettleInfo.getAuditorActiveArray();
//        if (StringUtils.isEmpty(arrayStr)) {
//            return null;
//        }
//        //兼容 数组或者 对象类型
//        if (matchSettleInfo.getSportId().equals(2l)) {
//            JSONArray array = JSONArray.parseArray(arrayStr);
//            if (array == null) {
//                return null;
//            }
//            if (checkNumber > array.size()) {
//                return null;
//            }
//            //如果需要第六个人审核，默认推到第五个人
//            if (checkNumber > 5) {
//                return array.get(4).toString();
//            }
//            return array.get(checkNumber - 1) != null ? array.get(checkNumber - 1).toString() : null;
//        } else {
//            //足球改为对象类型
//            try {
//                AuditorFootBallJsonVo auditorFootBallJsonVo = JSONObject.parseObject(matchSettleInfo.getAuditorActiveArray(), AuditorFootBallJsonVo.class);
//                List<String> array;
//                if (matchSettleCheckInfo.getEventCode().equals("goal") || matchSettleCheckInfo.getEventCode().equals("kick_off")) {
//                    array = auditorFootBallJsonVo.getGoalAuditorList();
//                } else if (matchSettleCheckInfo.getEventCode().equals("corner")) {
//                    array = auditorFootBallJsonVo.getCornerAuditorList();
//                } else {
//                    array = auditorFootBallJsonVo.getFacardAuditorList();
//                }
//                if (checkNumber > array.size()) {
//                    return null;
//                }
//                if (checkNumber > 5) {
//                    return array.get(4).toString();
//                }
//                return array.get(checkNumber - 1) != null ? array.get(checkNumber - 1).toString() : null;
//            } catch (Exception e) {
//                log.error("MatchSettleCheckServiceImpl-getNextAuthorName", e);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public void saveOrUpdateCheckInfo(MatchSettleCheckInfo checkInfo) {
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        checkInfoExample.createCriteria().andStandardMatchIdEqualTo(checkInfo.getStandardMatchId())
//                .andUserNameEqualTo(checkInfo.getUserName()).andSettleScoreEventIdEqualTo(checkInfo.getSettleScoreEventId());
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        if (list.size() > 0) {
//            return;
//        } else {
//            matchSettleCheckInfoMapper.insert(checkInfo);
//        }
//    }
//
//    @Override
//    public boolean isAllPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore) {
//        boolean flag = true;
//        //查询赛事结算表 看是否关闭顺序结算控制 为开  (null or 0)
//        Long standardMatchId = matchSettleScore.getStandardMatchId();
//        if (standardMatchId == null || standardMatchId == 0L) {
//            flag = false;
//        }
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//        if (matchSettleInfo == null) {
//            flag = false;
//        }
//
//        if (matchSettleInfo.getSettleOrderClosed() != null &&
//                matchSettleInfo.getSettleOrderClosed() != 0) {
//            return true;
//        }
//
//
//        List<String> settleNumsBefore;
//        if (flag) {
//            settleNumsBefore = SettleNumUtils.getPieriodScoresBeforeSettleNum(matchSettleScore.getSettleNum());
//        } else {
//            settleNumsBefore = SettleNumUtils.getPieriodScoresBeforeSettleNewNum(matchSettleScore.getSettleNum());
//        }
//        if (settleNumsBefore.size() == 0) {
//            return true;
//        }
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        //查询当前编辑的比分之前未结算的比分
//        example.createCriteria().andSettleNumIn(settleNumsBefore).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).
//                andStatusNotEqualTo(SETTLED);
//        List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
//        if (list.size() != 0) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String getBussinessEvent(Long standardMatchId) {
//
//        List<Long> standardMatchIds = new ArrayList<>();
//        standardMatchIds.add(standardMatchId);
//        List<StandardSportMarketSell> eventSells = standardSportMarketSellService.getItems(standardMatchIds);
//        if (eventSells.size() != 0) {
//            StandardSportMarketSell standardSportMarketSell = eventSells.get(0);
//            return standardSportMarketSell.getBusinessEvent();
//        }
//        return "";
//    }
//
//    @Override
//    public void changeHomeAway(List<MatchEventInfo> list) {
//        //只有足球才做主客队相反
//        try {
//            if (list.size() != 0 && list.get(0).getSportId().equals(1L)) {
//                //只有UOF比分才需要主客队互换  事件比分不需要
//                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(list.get(0).getThirdMatchId());
//                if (thirdMatchInfo != null && thirdMatchInfo.getHomeAwayOpposite() != null && 1 == thirdMatchInfo.getHomeAwayOpposite()) {
//                    for (MatchEventInfo matchEventInfo : list) {
//                        Integer t1 = matchEventInfo.getT1();
//                        Integer t2 = matchEventInfo.getT2();
//                        matchEventInfo.setT1(t2);
//                        matchEventInfo.setT2(t1);
//                        if (matchEventInfo.getHomeAway().equals("home")) {
//                            matchEventInfo.setHomeAway("away");
//                        } else if (matchEventInfo.getHomeAway().equals("away")) {
//                            matchEventInfo.setHomeAway("home");
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("MatchSettleCheckServiceImpl-changeHomeAway List<MatchEventInfo>:", e);
////            e.printStackTrace();
//        }
//
//    }
//
//
//    @Override
//    public void changeHomeAway(MatchEventInfo data) {
//        //只有足球才做主客队相反
//        try {
//            if (data.getSportId().equals(1L)) {
//                //只有UOF比分才需要主客队互换  事件比分不需要
//                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(data.getThirdMatchId());
//                if (thirdMatchInfo != null && thirdMatchInfo.getHomeAwayOpposite() != null && 1 == thirdMatchInfo.getHomeAwayOpposite()) {
////                    for (MatchEventInfo matchEventInfo : list) {
//                    Integer t1 = data.getT1();
//                    Integer t2 = data.getT2();
//                    data.setT1(t2);
//                    data.setT2(t1);
//                    if (data.getHomeAway().equals("home")) {
//                        data.setHomeAway("away");
//                    } else if (data.getHomeAway().equals("away")) {
//                        data.setHomeAway("home");
//                    }
////                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("MatchSettleCheckServiceImpl-changeHomeAway MatchEventInfo:", e);
////            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void rollbackScores(MatchSettleScore matchSettleScore) {
//        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//        example.createCriteria().andSettleScoreEventIdEqualTo(matchSettleScore.getId())
//                .andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andCheckDataTypeEqualTo(MatchSettleCheckConstant.CheckDataType.DATA_SOURCE);
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//            matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//            log.info("结算回滚，比分核对数据状态还原 rollbackScores:{}", matchSettleCheckInfo.getSettleScoreEventId());
//        }
//    }
//
//    @Override
//    public void rollbackEvent(MatchSettleEvent matchSettleEvent) {
//        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//        example.createCriteria().andSettleScoreEventIdEqualTo(matchSettleEvent.getId()).andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId());
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
//            matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
//            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            matchSettleCheckInfoMapper.updateByPrimaryKey(matchSettleCheckInfo);
//            log.info("结算回滚，事件核对数据状态还原 rollbackEvent:{}", matchSettleCheckInfo.getSettleScoreEventId());
//        }
//    }
//
//
//    @Override
//    public Long searchEventTimeByEvent(MatchSettleEvent event, MatchSettleCheckInfo checkInfo) {
//        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//        example.createCriteria().andSettleScoreEventIdEqualTo(event.getId());
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
//        Map<String, List<MatchSettleCheckInfo>> checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
//        String key = SettleCheckUtils.countSettleEventCompareKey(checkInfo);
//        Long eventTime = 0l;
//        for (Map.Entry<String, List<MatchSettleCheckInfo>> stringListEntry : checkGroupMap.entrySet()) {
//            if (stringListEntry.getKey().equals(key)) {
//                for (MatchSettleCheckInfo matchSettleCheckInfo : stringListEntry.getValue()) {
//                    if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
//                        if (eventTime == 0l) {
//                            eventTime = matchSettleCheckInfo.getCreateTime();
//                        } else {
//                            if (eventTime > matchSettleCheckInfo.getCreateTime()) {
//                                eventTime = matchSettleCheckInfo.getCreateTime();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return eventTime;
//    }
//
//    @Override
//    public Long searchEventTimeByScores(MatchSettleScore settleScore) {
//        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//        example.createCriteria().andSettleScoreEventIdEqualTo(settleScore.getId());
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
//        Long eventTime = 0l;
//        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
//            if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
//                if (eventTime == 0l) {
//                    eventTime = matchSettleCheckInfo.getCreateTime();
//                } else {
//                    if (eventTime > matchSettleCheckInfo.getCreateTime()) {
//                        eventTime = matchSettleCheckInfo.getCreateTime();
//                    }
//                }
//            }
//        }
//        return eventTime;
//    }
//
//    @Override
//    public Long searchEventTimeByEvent(MatchSettleEvent matchSettleEvent) {
//        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//        example.createCriteria().andSettleScoreEventIdEqualTo(matchSettleEvent.getId());
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
//        Map<String, List<MatchSettleCheckInfo>> checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
//        String key = SettleCheckUtils.countSettleEventCompareKey(matchSettleEvent);
//        Long eventTime = 0l;
//        for (Map.Entry<String, List<MatchSettleCheckInfo>> stringListEntry : checkGroupMap.entrySet()) {
//            if (stringListEntry.getKey().equals(key)) {
//                for (MatchSettleCheckInfo matchSettleCheckInfo : stringListEntry.getValue()) {
//                    if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
//                        if (eventTime == 0l) {
//                            eventTime = matchSettleCheckInfo.getCreateTime();
//                        } else {
//                            if (eventTime > matchSettleCheckInfo.getCreateTime()) {
//                                eventTime = matchSettleCheckInfo.getCreateTime();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return eventTime;
//    }
//
//    @Override
//    public boolean settlePenaltyTeamFirst(MatchSettleEvent event) {
//        //1.结算该事件修改状态
//        if (!event.getSettleNum().equals("-1030")) {
//            return false;
//        }
//        event.setStatus(3);
//        event.setModifyTime(System.currentTimeMillis());
//        //2.将当前的赛事的所有点球事件进行次序计算
//        MatchSettleEventExample eventExample = new MatchSettleEventExample();
//        eventExample.createCriteria().andSettleNumEqualTo("1030").andStandardMatchIdEqualTo(event.getStandardMatchId());
//        List<MatchSettleEvent> penaltyEvents = matchSettleEventMapper.selectByExample(eventExample);
//        for (MatchSettleEvent penaltyEvent : penaltyEvents) {
//            if (event.getHomeAway().equals("home")) {
//                if (penaltyEvent.getHomeAway().equals("home")) {
//                    penaltyEvent.setEventOrder((penaltyEvent.getFirstNum() - 1) * 2 + 1);
//                } else if (penaltyEvent.getHomeAway().equals("away")) {
//                    penaltyEvent.setEventOrder(penaltyEvent.getFirstNum() * 2);
//                }
//            } else if (event.getHomeAway().equals("away")) {
//                if (penaltyEvent.getHomeAway().equals("home")) {
//                    penaltyEvent.setEventOrder(penaltyEvent.getFirstNum() * 2);
//                } else if (penaltyEvent.getHomeAway().equals("away")) {
//                    penaltyEvent.setEventOrder((penaltyEvent.getFirstNum() - 1) * 2 + 1);
//                }
//            }
//            penaltyEvent.setModifyTime(System.currentTimeMillis());
//            matchSettleEventMapper.updateByPrimaryKey(penaltyEvent);
//        }
//        return true;
//    }
//
//
//    private void settleMatchSettleEvent(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfo checkInfo) {
//        log.info("eventId::{}::settleMatchSettleEvent start", checkInfo.getThirdSettleScoreEventId());
//        //1.从核对对象 复制 事件 到结算对象
//        SettleCheckUtils.copyCheckInfoToMatchSettleEvent(checkInfo, matchSettleEvent);
//        //2.修改结算对象状态
//        matchSettleEvent.setStatus(SETTLED);
//        //3.设置结算对象的 是否自动结算方式
//        if (checkInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
//            matchSettleEvent.setIsAutoSettle(1);
//        } else {
//            matchSettleEvent.setIsAutoSettle(0);
//            matchSettleEvent.setOperater(checkInfo.getUserName());
//        }
//        //4.设置结算人 结算次数  是否二次结算等
//        matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//        matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount() == null ? 1 : matchSettleEvent.getSettleCount() + 1);
//        matchSettleEvent.setModifyTime(System.currentTimeMillis());
//        //只有一次结算会走这里
//        matchSettleEvent.setSettleTimes(1);
//        //5分钟区间根据5分钟开关是否打开做校验
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleEvent.getStandardMatchId());
//        if (matchSettleInfo.getFiveMinSwitch() != null && matchSettleInfo.getFiveMinSwitch() == 1) {
//            matchSettleEvent.setFiveMinSection(checkInfo.getFiveMinSection());
//        } else {
//            matchSettleEvent.setFiveMinSection(null);
//        }
//        matchSettleEvent.setIsGrey(0);
//        matchSettleEvent.setHasDeleteEvent(0);
//        matchSettleEvent.setCurrentEventStatus(0);
//        //最终事件处理逻辑
//        this.endEventSettleByEvent(matchSettleEvent);
//        //5.更新结算对象到结算表
//        matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//
//        //更新延迟表
//        matchDelaySettleInfoMapper.updateSettleStatusByScoreId(matchSettleEvent.getId(),3);
//        if (matchSettleEvent.getSettleNum().equals("1028")) {
//            EditMatchSettleEventDto editMatchSettleEventDto = new EditMatchSettleEventDto();
//            editMatchSettleEventDto.setStandardMatchId(matchSettleEvent.getStandardMatchId());
//            editMatchSettleEventDto.setT1(matchSettleEvent.getT1());
//            editMatchSettleEventDto.setT2(matchSettleEvent.getT2());
//            editMatchSettleEventDto.setOperatorName(checkInfo.getUserName());
//            matchSettleService.updateGoWaterPenaltyScores(editMatchSettleEventDto);
//        }
//        //结算时把回滚订单数清零
//        matchSettleService.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
//        matchSettleCheckService.updateMatchCurrentEventStatus(matchSettleEvent.getStandardMatchId());
//        log.info("eventId::{}::settleMatchSettleEvent 开始记录日志", checkInfo.getThirdSettleScoreEventId());
//        //6.日志
//        String userName = "";
//        if (checkInfo.getCheckDataType().equals(2)) {
//            userName = checkInfo.getUserName() + ",(第" + matchSettleEvent.getCheckNumber() + "人)";
//        } else {
//            if (StringUtils.isNotEmpty(checkInfo.getDataSourceCode()) &&
//                    (!checkInfo.getDataSourceCode().equals("PD") && !checkInfo.getDataSourceCode().equals("PD2"))) {
//                userName = checkInfo.getDataSourceCode();
//            } else {
//                if (null != checkInfo.getUserName()) {
//                    userName = checkInfo.getUserName();
//                } else {
//                    userName = checkInfo.getDataSourceCode();
//                }
//            }
//        }
//        iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent, userName,
//                OperateLogTypeEnum.SCORE_SETTLE.getCode().toString(), "", "");
//        // 如果是进球事件 需要编辑进球方式
//        if (matchSettleEvent.getEventCode().equals("goal") && matchSettleEvent.getEventType() == 1) {
//            MatchSettleEvent extryEvent = matchSettleService.getExtryEvent(matchSettleEvent);
//            if (extryEvent != null) {
//                extryEvent.setT1(matchSettleEvent.getT1());
//                extryEvent.setT2(matchSettleEvent.getT2());
//                extryEvent.setHomeAway(matchSettleEvent.getHomeAway());
//                extryEvent.setModifyTime(System.currentTimeMillis());
//                extryEvent.setIsGrey(0);
//                extryEvent.setHasDeleteEvent(0);
//                extryEvent.setCurrentEventStatus(0);
//                matchSettleEventMapper.updateByPrimaryKey(extryEvent);
//            }
//        }
//        // 如果是点球谁先射门球队事件则需要调用
//        if (matchSettleEvent.getSettleNum().equals("-1030")) {
//            this.settlePenaltyTeamFirst(matchSettleEvent);
//        } else {
//            //７.下发MQ
//            if (matchSettleEvent.getPeriodId() == 100 && (matchSettleEvent.getSettleNum().equals(MatchPeriodEnum.GOAL_10.getCode().toString()) ||
//                    matchSettleEvent.getSettleNum().equals(MatchPeriodEnum.Corner_3.getCode().toString()) ||
//                    matchSettleEvent.getSettleNum().equals(MatchPeriodEnum.BOOKINGS_9.getCode().toString()))) {
//                try {
//                    //44612bug手动延迟1S下发,包括全场,进球,角球,发牌比分结算
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    log.error("eventId::{}::标准赛事Id:{} 延迟1s下发全场事件异常: ", checkInfo.getThirdSettleScoreEventId(), matchSettleEvent.getId(), e);
//                }
//            }
//            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
//        }
//
//        //8.ws推送
//        String eventCode = "";
//        if (checkInfo.getEventCode().equals("goal") || checkInfo.getEventCode().equals("no goal")) {
//            eventCode = "goal";
//        } else if (checkInfo.getEventCode().equals("corner")) {
//            eventCode = "corner";
//        } else {
//            eventCode = "fa_card";
//        }
//        wsPushService.pushStandardSettleScores(matchSettleEvent.getStandardMatchId(), eventCode);
//        wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleEvent.getStandardMatchId()
//                , eventCode, checkInfo.getId(), checkInfo.getSettleScoreEventId(), 2));
//        log.info("eventId::{}::settleMatchSettleEvent end", checkInfo.getThirdSettleScoreEventId());
//    }
//
//
//    private void settleMatchScore(MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo) {
//        log.info("eventId::{}::settleMatchScore start", checkInfo.getThirdSettleScoreEventId());
//        //1.从核对对象 复制 比分 到结算对象
//        SettleCheckUtils.copyCheckInfoToMatchSettleScore(checkInfo, matchSettleScore);
//        //2.修改结算对象状态
//        matchSettleScore.setStatus(SETTLED);
//        //3.设置结算对象的 是否自动结算方式
//        if (checkInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
//            matchSettleScore.setIsAutoSettle(1);
//        } else {
//            matchSettleScore.setIsAutoSettle(0);
//            matchSettleScore.setOperater(checkInfo.getUserName());
//        }
//        //4.设置结算人 结算次数  是否二次结算等
//        matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//        matchSettleScore.setSettleCount(matchSettleScore.getSettleCount() == null ? 1 : matchSettleScore.getSettleCount() + 1);
//        matchSettleScore.setModifyTime(System.currentTimeMillis());
//        //只有一次结算会走这里
//        matchSettleScore.setSettleTimes(1);
//
//        matchSettleScore.setIsGrey(0);
//        matchSettleScore.setHasDeleteEvent(0);
//        matchSettleScore.setCurrentEventStatus(0);
//        //当 上半场 全场触发结算的时候 校验 事件是否和 全场比分一致，然后如果一致则走 最后事件结算逻辑
//        this.endEventSettleByScore(matchSettleScore);
//        //篮球结算后去掉比分带入弹框
//        matchSettleScore.setPopupUsers(null);
//        //5.更新结算对象到结算表
//        matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
//        log.info("eventId::{}::settleMatchScore 比分Id::{}:: 当前事件被结算参数:{} ",checkInfo.getThirdSettleScoreEventId(), matchSettleScore.getId(), matchSettleScore);
//        //更新延迟表
//        matchDelaySettleInfoMapper.updateSettleStatusByScoreId(matchSettleScore.getId(),3);
//
//        //70555
//        if (matchSettleScore.getSportId().equals(2L)){
//            validateBasketBallSettleScore(matchSettleScore);
//            basketballMatchScoresSettleApi.verifyScoresIsSame(matchSettleScore);
//        }
//        //结算时把回滚订单数清零
//        matchSettleService.settleRollBackSetNullOrderCount(matchSettleScore.getId());
//        //1.日志
////        matchSettleLogService.matchSettleScoreAddLog(matchSettleScore,
////                matchSettleScore.getOperater(), OperateLogTypeEnum.SCORE_SETTLE.getCode().toString()
////                ,before,matchSettleScoreDto.getIpAddress());
//        //2.MQ下发
//        if (matchSettleScore.getPeriodId()==100 && (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_10.getCode().toString()) ||
//                matchSettleScore.getSettleNum().equals(MatchPeriodEnum.Corner_3.getCode().toString()) ||
//                matchSettleScore.getSettleNum().equals(MatchPeriodEnum.BOOKINGS_9.getCode().toString()))) {
//            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore, 2);
//        } else {
//
//            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
//        }
//
//        if (matchSettleScore.getSettleNum().equals("105") || matchSettleScore.getSettleNum().equals("1010") ||
//                matchSettleScore.getSettleNum().equals("bk_1ht") || matchSettleScore.getSettleNum().equals("bk_ft_et")) {
//            recordScore(matchSettleScore);
//        }
//        //3.WS 推送
//        String eventCode = "";
//        if (checkInfo.getEventCode().equals("fa_card")) {
//            eventCode = "fa_card";
//        } else if (checkInfo.getEventCode().equals("corner")) {
//            eventCode = "corner";
//        } else {
//            eventCode = "goal";
//        }
//        wsPushService.pushStandardSettleScores(matchSettleScore.getStandardMatchId(), eventCode);
//        wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleScore.getStandardMatchId()
//                , eventCode, checkInfo.getId(), checkInfo.getSettleScoreEventId(), 3));
//
//
//        String userName = "";
//        if (checkInfo.getCheckDataType().equals(2)) {
//            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(checkInfo.getStandardMatchId());
//            if (matchSettleInfo.getSportId().equals(2L)){
//                List<String> array =JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray()).toJavaList(String.class);
//                if (!CollectionUtil.isEmpty(array)){
//                    userName = getCheckUserName(checkInfo.getUserName(),array);
//                }else {
//                    userName = checkInfo.getUserName() + ",(第" + matchSettleScore.getCheckNumber() + "人)";
//                }
//
//            }else {
//                userName = checkInfo.getUserName() + ",(第" + matchSettleScore.getCheckNumber() + "人)";
//            }
//
//        } else {
//            if (StringUtils.isNotEmpty(checkInfo.getDataSourceCode()) &&
//                    (!checkInfo.getDataSourceCode().equals("PD") && !checkInfo.getDataSourceCode().equals("PD2"))) {
//                userName = checkInfo.getDataSourceCode();
//            } else {
//                if (null != checkInfo.getUserName()) {
//                    userName = checkInfo.getUserName();
//                } else {
//                    userName = checkInfo.getDataSourceCode();
//                }
//
//            }
//        }
//        iMatchSettleLogService.matchSettleScoreAddLog(matchSettleScore, userName,
//                OperateLogTypeEnum.SCORE_SETTLE, OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(), "");
//        log.info("eventId::{}::settleMatchScore end", checkInfo.getThirdSettleScoreEventId());
//
//    }
//
//    /**
//     * 结算事件的时候
//     * 1. 上半场事件 校验已结算上半场结算 和全场结算是否比分一致 一致则触发 上半场 或者 全场最终事件
//     * 2. 下半场事件 校验已结算 全场比分 是否一致 一致则结算 全场最终事件
//     */
//    public void endEventSettleByEvent(MatchSettleEvent matchSettleEvent) {
//        //1.上半场下半场 进球角球 发牌
//        List<String> eventCodes = EndEventUtils.eventCodesFootballByEventCode(matchSettleEvent.getEventCode());
//        if (eventCodes.size() == 0) {
//            return;
//        }
//        //1.阶段条件获取 上半场 或者全场 上半场事件可能会导致 全场结算 或者 上半场结算
//        //1.2 下半场事件则可能触发全场结算
//        List<Long> periods = EndEventUtils.periodsFootballByEventPeriod(matchSettleEvent.getPeriodId());
//        //不是31 也不是100 事件则直接返回
//        if (periods == null) {
//            return;
//        }
//        //2.查询对应事件编码和阶段编码已经结算的比分 而且比分相同
//        MatchSettleScoreExample scoreExample = new MatchSettleScoreExample();
//        scoreExample.createCriteria().andEventCodeIn(eventCodes).andPeriodIdIn(periods)
//                .andStatusEqualTo(SETTLED).andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId())
//                .andT1EqualTo(matchSettleEvent.getT1()).andT2EqualTo(matchSettleEvent.getT2());
//        List<MatchSettleScore> scoreList = matchSettleScoreMapper.selectByExample(scoreExample);
//        if (scoreList.size() == 0) {
//            return;
//        }
//        for (MatchSettleScore matchSettleScore : scoreList) {
//            //符合全场结算 编辑add2
//            if (matchSettleScore.getPeriodId().equals(100L)) {
//                matchSettleEvent.setAddition2(matchSettleEvent.getHomeAway());
//            }
//            //符合上半场结算 编辑add1
//            if (matchSettleScore.getPeriodId().equals(31L)) {
//                matchSettleEvent.setAddition1(matchSettleEvent.getHomeAway());
//            }
//        }
//        log.info("结算比分编辑最终事件::赛事id：{},事件阶段:{},事件类型:{} add1:{} add2:{}",
//                matchSettleEvent.getStandardMatchId(), matchSettleEvent.getPeriodId(), matchSettleEvent.getEventCode()
//                , matchSettleEvent.getAddition1(), matchSettleEvent.getAddition2());
//    }
//
//    /**
//     * 结算比分的时候校验上半场 全场比分结算 的话 查询对应事件判断是否比分一致，如果上半场一致则触发上半场比分结算
//     * 如果全场结算，则触发全场结算
//     */
//    public void endEventSettleByScore(MatchSettleScore matchSettleScore) {
//        //0.事件编码分类
//        List<String> eventCodes = EndEventUtils.eventCodesFootballByEventCode(matchSettleScore.getEventCode());
//        if (eventCodes.size() == 0) {
//            return;
//        }
//        //1.阶段条件获取 上半场 或者全场
//        List<Long> periods = EndEventUtils.periodsFootballByScorePeriod(matchSettleScore.getPeriodId());
//        //不是31 也不是100 事件则直接返回
//        if (periods == null) {
//            return;
//        }
//        //2.查询对应事件编码和阶段编码已经结算的事件
//        MatchSettleEventExample eventExample = new MatchSettleEventExample();
//        eventExample.createCriteria().andEventCodeIn(eventCodes).andPeriodIdIn(periods)
//                .andStatusEqualTo(SETTLED).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId())
//                .andHomeAwayIn(EndEventUtils.HOME_AWAY);
//        //3.取比分最大的事件
//        List<MatchSettleEvent> eventList = matchSettleEventMapper.selectByExample(eventExample);
//        if (eventList.size() == 0) {
//            return;
//        }
//        Integer t1 = 0;
//        Integer t2 = 0;
//        String homeAway = "none";
//        Long id = null;
//        for (MatchSettleEvent matchSettleEvent : eventList) {
//            if (matchSettleEvent.getT1() != null && matchSettleEvent.getT2() != null) {
//                Integer sum = matchSettleEvent.getT1() + matchSettleEvent.getT2();
//                if ((t1 + t2) <= sum) {
//                    //罚牌比分也是取 事件的 t1 t2
//                    t1 = matchSettleEvent.getT1();
//                    t2 = matchSettleEvent.getT2();
//                    homeAway = matchSettleEvent.getHomeAway();
//                    id = matchSettleEvent.getId();
//                }
//            }
//        }
//        //id= null 取不到对应事件过滤
//        if (id == null) {
//            return;
//        } else {
//            //还有可能 结算的事件比分是0 则无需编辑 或者编辑为none
//            if (!EndEventUtils.HOME_AWAY.contains(homeAway)) {
//                homeAway = "none";
//            }
//        }
//        //4.根据比分最大的事件和结算事件做比对
//        //4.1如果相等 则编辑addition1 或者 addition2 主客队
//        if (matchSettleScore.getT1() != null && matchSettleScore.getT2() != null) {
//            if (matchSettleScore.getT1().equals(t1) && matchSettleScore.getT2().equals(t2)) {
//                //如果是全场打完 则编辑 add2
//                if (matchSettleScore.getPeriodId().equals(100L)) {
//                    matchSettleScore.setAddition2(homeAway);
//                    //如果是上半场休息 则编辑add1
//                } else if (matchSettleScore.getPeriodId().equals(31L)) {
//                    matchSettleScore.setAddition1(homeAway);
//                }
//                log.info("结算比分编辑最终事件::赛事id：{}，选择事件id:{},事件阶段:{},事件类型:{} add1:{} add2:{}",
//                        matchSettleScore.getStandardMatchId(), id, matchSettleScore.getPeriodId(), matchSettleScore.getEventCode()
//                        , matchSettleScore.getAddition1(), matchSettleScore.getAddition2());
//            }
//        } else {
//            //4.2如果不相等 则直接返回
//            return;
//        }
//    }
//
//    private void recordScore(MatchSettleScore matchSettleScore) {
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScore.getStandardMatchId());
//        if (matchSettleInfo != null) {
//
//            matchSettleInfo.setModifyTime(System.currentTimeMillis());
//            if (matchSettleScore.getSettleNum().equals("105") || matchSettleScore.getSettleNum().equals("bk_1ht")) {
//                matchSettleInfo.setH1T1(matchSettleScore.getT1());
//                matchSettleInfo.setH1T2(matchSettleScore.getT2());
//            } else if (matchSettleScore.getSettleNum().equals("1010") || matchSettleScore.getSettleNum().equals("bk_ft_et")) {
//                matchSettleInfo.setFtT1(matchSettleScore.getT1());
//                matchSettleInfo.setFtT2(matchSettleScore.getT2());
//            }
//            //更新结算信息
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//        } else {
//            log.error("参数异常【matchSettleInfos为空! 】");
//        }
//    }
//
//
//    public boolean isSettle2(Long standardMatchId) {
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//        if (matchSettleInfo== null || matchSettleInfo.getSettleType() == 1) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    //人员结算失败发送芒果预警
//    private void sendMango(Long sportId, Long standardMatchId, List<String> userNameList) {
//        for (String userName : userNameList) {
//            //查询标准赛事表
//            StandardMatchInfo standardMatchInfo  = standardMatchInfoService.getItem(standardMatchId);
//            if (standardMatchInfo != null) {
//                String match = standardMatchInfo.getHomeAwayInfo();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String time = sdf.format(new Date());
//                String sport = "FootBall";//默认足球
//                if (sportId.equals(2L)) {
//                    sport = "BasketBall";
//                }
//                /*for(StandardSportTypeEnum em:StandardSportTypeEnum.values()){
//                    if(oldMatchInfo.getSportId().equals(em.code)){
//                        sport = em.toString();
//                    }
//                }*/
//                String data = "[Env]:" + env + "\n" +
//                        "[Time]:" + time + "\n" +
//                        "[Sport]:" + sport + "\n" +
//                        "[Match ID]:" + standardMatchInfo.getMatchManageId() + "\n" +
//                        "[Match]:" + match + "\n" +
//                        "[PIC]:" + userName;
//                String linkId = IdWorker.getId() + "_PERSON_ERROR_SETTLE_MANGO_EARLY_WARNING";
//                matchSettleCenterProducer.personErrorSettleManGoEarlyWarning(linkId, data, "人员错误结算芒果预警");
//            } else {
//                log.info("人员错误结算芒果预警未找到相关赛事：" + standardMatchId);
//            }
//        }
//    }
//
//    /**
//     * 根据赛事id判断该赛事是否可以数据商结算
//     *
//     * @param standardMatchId
//     * @return
//     */
//    public boolean matchIsAutoSettle(long standardMatchId) {
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//        if (matchSettleInfo != null ) {
//
//            if (matchSettleInfo.getIsAutoSettleDataSource() == null || matchSettleInfo.getIsAutoSettleDataSource() == 0) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void confirmGoalDoFilter(List<MatchEventInfo> data) {
//        if (CollectionUtil.isEmpty(data)) {
//            return;
//        }
//        try {
//            for (MatchEventInfo event : data) {
//                if (event.getDataSourceCode().equals("SR") && MatchSettleCheckConstant.GoalConfirmEventCode.SR.equals(event.getEventCode())) {
//                    this.updateMatchSettleGoalStatus(event);
//                    this.confirmDataSourceSettleEvent(event);
//                    continue;
//                }
//                if (event.getDataSourceCode().equals("BG") && MatchSettleCheckConstant.GoalConfirmEventCode.BG.equals(event.getEventCode())) {
//                    this.updateMatchSettleGoalStatus(event);
//                    this.confirmDataSourceSettleEvent(event);
//                    continue;
//                }
//                if (event.getDataSourceCode().equals("RB") && MatchSettleCheckConstant.GoalConfirmEventCode.RB.equals(event.getEventCode())) {
//                    this.updateMatchSettleGoalStatus(event);
//                    this.confirmDataSourceSettleEvent(event);
//                    continue;
//                }
//                if (event.getDataSourceCode().equals("KO") && MatchSettleCheckConstant.GoalConfirmEventCode.KO.equals(event.getEventCode())) {
//                    this.updateMatchSettleGoalStatus(event);
//                    this.confirmDataSourceSettleEvent(event);
//                    continue;
//                }
//                if (event.getDataSourceCode().equals("F01") && MatchSettleCheckConstant.GoalConfirmEventCode.KO.equals(event.getEventCode())) {
//                    this.updateMatchSettleGoalStatus(event);
//                    this.confirmDataSourceSettleEvent(event);
//                    continue;
//                }
//                /*if (event.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode())){
//                    return;
//                }*/
//                if (MatchSettleCheckConstant.GoalConfirmEventCode.PA.equals(event.getEventCode()) || "match_status".equals(event.getEventCode())) {
//                    this.updateMatchSettleGoalStatus(event);
//                    this.confirmDataSourceSettleEvent(event);
//                    continue;
//                }
//                //角球数据商 BG
////                if (event.getDataSourceCode().equals("BG") && MatchSettleCheckConstant.CornerConfirmEventCode.BG.equals(event.getEventCode())) {
////                    this.updateMatchSettleCornerStatus(event);
////                    this.confirmDataSourceSettleEvent(event);
////                    continue;
////                }
//
//            }
//        } catch (Exception e) {
//            log.error("linkId::{}::eventId:{} confirmGoalDoFilter error:", data.get(0).getLinkId(), data.get(0).getThirdEventId(), e);
//        }
//    }
//
//    public void updateMatchSettleCornerStatus(MatchEventInfo matchEventInfo) {
//        try {
//            MatchSettleGoalStatus goalStatus = matchSettleGoalStatusRepository.getMatchSettleGoalStatus(matchEventInfo.getThirdMatchId());
//            if (goalStatus == null) {
//                goalStatus = new MatchSettleGoalStatus();
//                goalStatus.setId(matchEventInfo.getThirdMatchId());
//                goalStatus.setStandardMatchId(matchEventInfo.getStandardMatchId());
//                goalStatus.setDataSourceCode(matchEventInfo.getDataSourceCode());
//                goalStatus.setCreateTime(System.currentTimeMillis());
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                goalStatus.setCornerStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,true);
//                log.info("::{}::updateMatchSettleCornerStatus 新增角球确认状态:CONFIRM", matchEventInfo.getLinkId());
//            }
//            //BG 才确认角球
////            if (matchEventInfo.getEventCode().equals("corner") && matchEventInfo.getCanceled() == 0 && matchEventInfo.getDataSourceCode().equals("BG")) {
////                goalStatus.setCornerStatus(MatchSettleCheckConstant.GoalStatus.NOT_CONFIRM);
////                goalStatus.setModifyTime(System.currentTimeMillis());
////                matchSettleGoalStatusMapper.updateByPrimaryKey(goalStatus);
////                log.info("::{}::updateMatchSettleCornerStatus 更新角球确认状态:NOT_CONFIRM", matchEventInfo.getLinkId());
////                return;
////            }
////            if (matchEventInfo.getEventCode().equals("corner") && matchEventInfo.getCanceled() == 1) {
////                goalStatus.setCornerStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
////                goalStatus.setModifyTime(System.currentTimeMillis());
////                matchSettleGoalStatusMapper.updateByPrimaryKey(goalStatus);
////                log.info("::{}::updateMatchSettleCornerStatus 删除逻辑 更新角球确认状态:CONFIRM", matchEventInfo.getLinkId());
////                return;
////            }
//        } catch (Exception e) {
//            log.error("MatchSettleCheckServiceImpl-updateMatchSettleCornerStatus error:", e);
//        }
//    }
//
//
//    @Override
//    public void updateMatchSettleGoalStatus(MatchEventInfo matchEventInfo) {
//        try {
//            log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 开始处理", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//            MatchSettleGoalStatus goalStatus = matchSettleGoalStatusRepository.getMatchSettleGoalStatus(matchEventInfo.getThirdMatchId());
//            if (goalStatus == null) {
//                goalStatus = new MatchSettleGoalStatus();
//                goalStatus.setId(matchEventInfo.getThirdMatchId());
//                goalStatus.setStandardMatchId(matchEventInfo.getStandardMatchId());
//                goalStatus.setDataSourceCode(matchEventInfo.getDataSourceCode());
//                goalStatus.setCreateTime(System.currentTimeMillis());
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,true);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 新增进球确认状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//            }
//            if (matchEventInfo.getEventCode().equals("goal") && matchEventInfo.getCanceled() == 0) {
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.NOT_CONFIRM);
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 更新进球确认状态:NOT_CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//                return;
//            }
//            if (matchEventInfo.getEventCode().equals("goal") && matchEventInfo.getCanceled() == 1) {
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 删除逻辑 更新进球确认状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//                return;
//            }
//            if (matchEventInfo.getDataSourceCode().equals("SR") && MatchSettleCheckConstant.GoalConfirmEventCode.SR.equals(matchEventInfo.getEventCode())) {
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//                return;
//            }
//            if (matchEventInfo.getDataSourceCode().equals("BG") && MatchSettleCheckConstant.GoalConfirmEventCode.BG.equals(matchEventInfo.getEventCode())) {
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//                return;
//            }
//            if (matchEventInfo.getDataSourceCode().equals("RB") && MatchSettleCheckConstant.GoalConfirmEventCode.RB.equals(matchEventInfo.getEventCode())) {
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//                return;
//            }
//            if (MatchSettleCheckConstant.GoalConfirmEventCode.RB.equals(matchEventInfo.getEventCode()) || "match_status".equals(matchEventInfo.getEventCode())) {
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//                return;
//            }
//            //预计新增f01功能
//            if (MatchSettleCheckConstant.GoalConfirmEventCode.F01.equals(matchEventInfo.getEventCode())&& matchEventInfo.getDataSourceCode().equals("F01") ) {
//                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
//                goalStatus.setModifyTime(System.currentTimeMillis());
//                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
//                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//                return;
//            }
//            log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 处理完成", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
//        } catch (Exception e) {
//            log.error("linkId::{}::eventId:{} updateMatchSettleGoalStatus error:", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId(), e);
//        }
//    }
//
//    @Override
//    public boolean isMatchGoalStatusConfirm(Long thirdMatchId, String eventCode) {
//        MatchSettleGoalStatus goalStatus = matchSettleGoalStatusRepository.getMatchSettleGoalStatus(thirdMatchId);
//        //BT 的先直接过去
//        if (eventCode.equals("goal")) {
//            if (goalStatus == null || goalStatus.getGoalStatus() == null || goalStatus.getGoalStatus() == MatchSettleCheckConstant.GoalStatus.CONFIRM) {
//                return true;
//            }
//            if (goalStatus.getDataSourceCode().equals("BT") || goalStatus.getDataSourceCode().equals("1X") || goalStatus.getDataSourceCode().equals("LS")) {
//                return true;
//            }
//            return false;
//        } else if (eventCode.equals("corner")) {
//            if (goalStatus == null || goalStatus.getCornerStatus() == null || goalStatus.getCornerStatus() == MatchSettleCheckConstant.GoalStatus.CONFIRM) {
//                return true;
//            }
//            if (goalStatus.getDataSourceCode().equals("BT") || goalStatus.getDataSourceCode().equals("RB") || goalStatus.getDataSourceCode().equals("BG") ||
//                    goalStatus.getDataSourceCode().equals("KO") || goalStatus.getDataSourceCode().equals("LS")) {
//                return true;
//            }
//            return false;
//        }
//        return true;
//    }
//
//    //处理三方赛事删除事件 -方案1 物理删除核对事件记录
//    @Override
//    public void canceledCheckMatchThirdSettleEvent(MatchSettleThirdEvent settleThirdEvent, MatchEventInfo data, Integer order) {
//        log.info("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent start", data.getLinkId(), data.getThirdEventId());
//        try {
//            MatchEventInfo oldEvent = this.getOldMatchInfoByCancel(data);
//            if (oldEvent == null) {
//                log.error("linkId::{}::eventId:{} 原被删除事件不存在", data.getLinkId(), data.getThirdEventId());
//                return;
//            }
//            MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//            example.createCriteria().andStandardMatchIdEqualTo(data.getStandardMatchId()).andDataSourceCodeEqualTo(data.getDataSourceCode()).andThirdSettleScoreEventIdEqualTo(settleThirdEvent.getId());
//            //先试下物理删除是否有用呢
//            List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//            if (list.size() == 0) {
//                log.error("linkId::{}::eventId:{} 没有找到被删除事件的核对记录", data.getLinkId(), data.getThirdEventId());
//                return;
//            }
//            MatchSettleCheckInfo matchSettleCheckInfo = list.get(0);
//            matchSettleCheckInfoMapper.deleteByExample(example);
//            MatchSettleEvent matchSettleEvent =matchSettleEventMapper .selectByPrimaryKey(matchSettleCheckInfo.getSettleScoreEventId());
//
//            //2755 删除延迟记录
//            MatchDelaySettleInfoExample delaySettleInfoExample = new MatchDelaySettleInfoExample();
//            delaySettleInfoExample.createCriteria().andStandardMatchIdEqualTo(data.getStandardMatchId()).andDataSourceCodeEqualTo(data.getDataSourceCode()).andCheckInfoIdEqualTo(matchSettleCheckInfo.getId());
//            matchDelaySettleInfoMapper.deleteByExample(delaySettleInfoExample);
//
//            log.info("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent 被删除赛事:{} 引发删除事件",data.getLinkId(), data.getThirdEventId(), data.getStandardMatchId());
//            matchSettleEvent.setHasDeleteEvent(1);
//            matchSettleEvent.setCurrentEventStatus(2);
//            //删除事件标记次序事件
//            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//            List<String> deleteSettleNums = new ArrayList<>();
//            //删除事件标记阶段比分
//            matchScoresSettleInitChainFilter.deleteEventPeriodScorefilter(oldEvent, deleteSettleNums);
//            if (deleteSettleNums.size() != 0) {
//                MatchSettleScore matchSettleScore = new MatchSettleScore();
//                matchSettleScore.setHasDeleteEvent(1);
//                matchSettleScore.setCurrentEventStatus(2);
//                MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
//                matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(data.getStandardMatchId()).andSettleNumIn(deleteSettleNums);
//                matchSettleScoreMapper.updateByExampleSelective(matchSettleScore, matchSettleScoreExample);
//            }
//            //存储删除标记到redis
//            validateDeleteEvent(matchSettleEvent, deleteSettleNums, data);
//            //删除事件标记赛事
//            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleThirdEvent.getStandardMatchId());
//            matchSettleInfo.setHasDeleteEvent(1);
//            matchSettleInfo.setCurrentEventStatus(2);
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//            //通知前端刷新赛事相关的
//            if (data.getEventCode().equals("red_card") || data.getEventCode().equals("yellow_card")) {
//                data.setEventCode("fa_card");
//            }
//            wsPushService.pushSettleMatchList(new MatchListSettleDto(data.getStandardMatchId(), data.getEventCode(), null, null, 4));
//            deleteAuditorCheckInfo(matchSettleCheckInfo.getSettleScoreEventId());
//        } catch (Exception e) {
//            log.error("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent ERROR:", data.getLinkId(), data.getThirdEventId(), e);
//        }
//    }
//
//
//    private void confirmDataSourceSettleEvent(MatchEventInfo event) {
//        //1.1查询当前已经入库的三方进球核对事件
//        MatchSettleCheckInfo goalCheckInfo = this.searchGoalEventCheckInfoByEvent(event);
//        //1.2 更新当前进球核对事件确认为已经进球
//        if (goalCheckInfo == null) {
//            log.info("linkId::{}::eventId:{} confirmDataSourceSettleEvent 找不到进球事件", event.getLinkId(), event.getThirdEventId());
//            return;
//        }
//        //确认进球事件 以及进入比分自动核对
//        //5分钟灰色区间不走自动结算 暂时废弃
////        if (goalCheckInfo.getIsGrey() == 2) {
////            return;
////        }
//
//        goalCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//        goalCheckInfo.setModifyTime(System.currentTimeMillis());
//        matchSettleCheckInfoMapper.updateByPrimaryKey(goalCheckInfo);
//        log.info("linkId::{}::eventId:{} confirmDataSourceSettleEvent 更新进球事件为确认状态", event.getLinkId(), event.getThirdEventId());
//        MatchSettleEvent matchSettleEvent = matchSettleEventMapper.selectByPrimaryKey(goalCheckInfo.getSettleScoreEventId());
//
//        //2.1查询当前已经入库的三方进球结算事件
//        //2.2判断当前结算事件是否结算
//        //2.3如果没结算发起进球事件自动核对
//        if (matchSettleEvent.getStatus() != 3) {
//            if (!matchSettleCheckService.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)) {
//                log.info("linkId::{}::eventId:{} confirmDataSourceSettleEvent {}之前的阶段没结算", event.getLinkId(), event.getThirdEventId(), goalCheckInfo.getStandardMatchId());
//                return;
//            }
//            log.info("linkId::{}::eventId:{} confirmDataSourceSettleEvent 事件比分核对开始", event.getLinkId(), event.getThirdEventId());
//            checkCommonMatchSettleScoreEvent(matchSettleEvent, goalCheckInfo, true);
//            log.info("linkId::{}::eventId:{} confirmDataSourceSettleEvent 事件比分核对结束", event.getLinkId(), event.getThirdEventId());
//        } else {
//            log.info("linkId::{}::eventId:{} confirmDataSourceSettleEvent 当前的结算事件已经结算id:{}", event.getLinkId(), event.getThirdEventId(), matchSettleEvent.getId());
//        }
//        //阶段比分自动确认以及核对
//        //3.1查询符合条件的阶段事件
//        //3.2更新当前阶段比分为已确认
//        //3.3对已确认未结算的比分进行自动核对
//    }
//
//    private void confirmDataSourceGoalSettleEvent(MatchSettleCheckInfo goalCheckInfo) {
//        log.info("eventId::{}::confirmDataSourceGoalSettleEvent start", goalCheckInfo.getThirdSettleScoreEventId());
//        goalCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
//        goalCheckInfo.setModifyTime(System.currentTimeMillis());
//        matchSettleCheckInfoMapper.updateByPrimaryKey(goalCheckInfo);
//        log.info("eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  BT-1X-F01 更新进球事件为确认状态", goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
//        MatchSettleEvent matchSettleEvent = matchSettleEventMapper.selectByPrimaryKey(goalCheckInfo.getSettleScoreEventId());
//        if (!matchSettleCheckService.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)) {
//            log.info("eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  BT-1X-F01 之前的阶段没结算", goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
//            return;
//        }
//        //2.1查询当前已经入库的三方进球结算事件
//        //2.2判断当前结算事件是否结算
//        //2.3如果没结算发起进球事件自动核对
//        if (matchSettleEvent.getStatus() != 3) {
//            log.info("eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  事件比分核对开始", goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
//            checkCommonMatchSettleScoreEvent(matchSettleEvent, goalCheckInfo, true);
//            log.info("eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  事件比分核对结束", goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
//        } else {
//            log.info("eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent 当前的结算事件已经结算id:{}", goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId(), matchSettleEvent.getId());
//        }
//    }
//
//    private MatchSettleCheckInfo searchGoalEventCheckInfoByEvent(MatchEventInfo event) {
//        // 处理match_status事件编码
//        if ("match_status".equals(event.getEventCode())) {
//            MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//            example.createCriteria().andDataSourceCodeEqualTo(event.getDataSourceCode()).andStandardMatchIdEqualTo(event.getStandardMatchId())
//                    .andEventCodeEqualTo("goal").andCheckTypeEqualTo(MatchSettleCheckConstant.CheckType.EVENT_SCORE).andCheckDataTypeEqualTo(MatchSettleCheckConstant.CheckDataType.DATA_SOURCE);
//            example.setOrderByClause("create_time desc");
//            List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//            log.info("::{}::根据match_status编码, 查询到需要确认的进球事件数量:{}", event.getLinkId(), list.size());
//            if (!CollectionUtils.isEmpty(list)) {
//                log.info("::{}::根据match_status编码, 查询到需要确认的进球事件:{}", event.getLinkId(), list.get(0));
//                return list.get(0);
//            }
//            log.info("::{}::根据match_status编码, 没有查询到需要确认的进球事件", event.getLinkId());
//            return null;
//        }
//
//        //1.加时赛处理机制 加时赛的 kick_off会扣除掉已经结算的 全场比分(不含加时赛)
//        if (event.getMatchPeriodId().equals(41l) || event.getMatchPeriodId().equals(42l)) {
//            //1.查询已经结算的全场比分（不包含加时）
//            MatchSettleScoreExample example = new MatchSettleScoreExample();
//            example.createCriteria().andStandardMatchIdEqualTo(event.getStandardMatchId()).andSettleNumEqualTo("1010").andStatusEqualTo(3);
//            List<MatchSettleScore> fullScores = matchSettleScoreMapper.selectByExample(example);
//            //2.扣除全场比分
//            if (fullScores.size() != 0) {
//                MatchSettleScore fullScore = fullScores.get(0);
//                event.setT1(event.getT1() - fullScore.getT1());
//                event.setT2(event.getT2() - fullScore.getT2());
//            }
//        }
//        String eventCode = "goal";
//        if (event.getEventCode().equals("corner_taken")) {
//            eventCode = "corner";
//        }
//        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//        example.createCriteria().andDataSourceCodeEqualTo(event.getDataSourceCode()).andStandardMatchIdEqualTo(event.getStandardMatchId())
//                .andEventCodeEqualTo(eventCode).andT1EqualTo(event.getT1()).andT2EqualTo(event.getT2())
//                .andCheckTypeEqualTo(MatchSettleCheckConstant.CheckType.EVENT_SCORE).andCheckDataTypeEqualTo(MatchSettleCheckConstant.CheckDataType.DATA_SOURCE);
//        example.setOrderByClause("create_time desc");
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(example);
//        log.info("::{}::查询到需要确认的进球事件数量:{}", event.getLinkId(), list.size());
//        if (list.size() != 0) {
//            log.info("::{}::查询到需要确认的进球事件:{}", event.getLinkId(), list.get(0));
//            return list.get(0);
//        } else {
//            log.info("::{}::没有查询到需要确认的进球事件", event.getLinkId());
//            return null;
//        }
//    }
//
//    @Override
//    public void searchCheckStatusByPenalty(PenaltyScoresVo penaltyScoresVo, String operatorName) {
//        //有WS推送的情况这个时候没操作人
//        if (StringUtils.isEmpty(operatorName)) {
//            return;
//        }
//
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(penaltyScoresVo.getStandardMatchId());
//        if (StringUtils.isNotEmpty(matchSettleInfo.getLimitUserArray())) {
//            JSONArray array = JSONArray.parseArray(matchSettleInfo.getLimitUserArray());
//            if (array.contains(operatorName)) {
//                penaltyScoresVo.getTeamFirst().setNeedCheck(0);
//                for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getHomeEventList()) {
//                    matchSettleEventDto.setNeedCheck(0);
//                }
//                for (MatchSettleEventDto matchSettleEventDto : penaltyScoresVo.getAwayEventList()) {
//                    matchSettleEventDto.setNeedCheck(0);
//                }
//                penaltyScoresVo.getHomeAway5RoundEvent().setNeedCheck(0);
//                penaltyScoresVo.getHomeAwayAllRoundEvent().setNeedCheck(0);
//                penaltyScoresVo.getGoWaterPenaltyEvent().setNeedCheck(0);
//                return;
//            }
//        }
//        Map<Long, MatchSettleEventDto> matchSettleScoreDtoMap = new HashMap<>();
//        List<Long> ids = new ArrayList<>();
//        for (MatchSettleEventDto matchSettleScoreDto : penaltyScoresVo.getAwayEventList()) {
//            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
//            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
//        }
//        for (MatchSettleEventDto matchSettleScoreDto : penaltyScoresVo.getHomeEventList()) {
//            matchSettleScoreDtoMap.put(Long.parseLong(matchSettleScoreDto.getId()), matchSettleScoreDto);
//            ids.add(Long.parseLong(matchSettleScoreDto.getId()));
//        }
//        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getTeamFirst().getId()), penaltyScoresVo.getTeamFirst());
//        ids.add(Long.parseLong(penaltyScoresVo.getTeamFirst().getId()));
//
//        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getHomeAway5RoundEvent().getId()), penaltyScoresVo.getHomeAway5RoundEvent());
//        ids.add(Long.parseLong(penaltyScoresVo.getHomeAway5RoundEvent().getId()));
//
//        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getHomeAwayAllRoundEvent().getId()), penaltyScoresVo.getHomeAwayAllRoundEvent());
//        ids.add(Long.parseLong(penaltyScoresVo.getHomeAwayAllRoundEvent().getId()));
//
//        matchSettleScoreDtoMap.put(Long.parseLong(penaltyScoresVo.getGoWaterPenaltyEvent().getId()), penaltyScoresVo.getGoWaterPenaltyEvent());
//        ids.add(Long.parseLong(penaltyScoresVo.getGoWaterPenaltyEvent().getId()));
//
//        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
//        checkInfoExample.createCriteria().andStandardMatchIdEqualTo(penaltyScoresVo.getStandardMatchId())
//                .andUserNameEqualTo(operatorName).andSettleScoreEventIdIn(ids);
//        List<MatchSettleCheckInfo> list = matchSettleCheckInfoMapper.selectByExample(checkInfoExample);
//        for (MatchSettleCheckInfo matchSettleCheckInfo : list) {
//            MatchSettleEventDto matchSettleScoreDto = matchSettleScoreDtoMap.get(matchSettleCheckInfo.getSettleScoreEventId());
//            matchSettleScoreDto.setNeedCheck(0);
//            if (matchSettleScoreDto != null) {
//                if (!(matchSettleCheckInfo.getCheckStatus() != null && matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM)) {
//                    matchSettleScoreDto.setNeedCheck(1);
//                }
//                //如果普通审核员进来，需要返回他个人的状态。除非数据已经结算
//                if (matchSettleScoreDto.getStatus() != SETTLED) {
//                    matchSettleScoreDto.setStatus(matchSettleCheckInfo.getCheckStatus());
//                } else {
//                    matchSettleScoreDto.setNeedCheck(0);
//                }
//            }
//
//            if (matchSettleCheckInfo.getSettleScoreEventId().toString().equals(penaltyScoresVo.getTeamFirst().getId())) {
//                if (penaltyScoresVo.getTeamFirst().getStatus() != null && penaltyScoresVo.getTeamFirst().getStatus() != 3) {
//                    penaltyScoresVo.getTeamFirst().setHomeAway(matchSettleCheckInfo.getHomeAway());
//                }
//            }
//        }
//    }
//
//    @Override
//    public boolean isFiveMinPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore) {
//        //查询赛事结算表 看是否关闭五分钟顺序结算控制 为开  (null or 0)
//        Long standardMatchId = matchSettleScore.getStandardMatchId();
//        if (standardMatchId == null || standardMatchId == 0L) {
//            return true;
//        }
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//        if (matchSettleInfo == null) {
//            return true;
//        }
//
//        if (matchSettleInfo.getFiveMinSwitch() != null &&
//                matchSettleInfo.getFiveMinSwitch() != 0) {
//            return true;
//        }
//
//        List<String> settleNumsBefore;
//
//        settleNumsBefore = SettleNumUtils.getFiveMinPieriodScoresBeforeSettleNum(matchSettleScore.getSettleNum());
//
//        if (settleNumsBefore.size() == 0) {
//            return true;
//        }
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        //查询当前编辑的比分之前未结算的比分
//        example.createCriteria().andSettleNumIn(settleNumsBefore).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).
//                andStatusNotEqualTo(SETTLED);
//        List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
//        if (list.size() != 0) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean isPeriodScoresBeforeSettledByEvent(MatchSettleEvent matchSettleEvent) {
//        String settleNum = null;
//        //根据当前进球事件判断需要判断的阶段比分
//        if (matchSettleEvent.getPeriodId() == 7l) {
//            //获取上半场比分
//            settleNum = "105";
//        } else if (matchSettleEvent.getPeriodId() == 42l) {
//            //获取加时赛上半场比分
//            settleNum = "1014";
//        }
//        //如果为空则不需要判断
//        if (settleNum == null) {
//            return true;
//        }
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        //查询当前编辑的比分之前未结算的比分
//        example.createCriteria().andSettleNumEqualTo(settleNum).andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).
//                andStatusEqualTo(SETTLED).andEventCodeEqualTo("goal");
//        List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
//        if (list.size() != 0) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
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
//                List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoList = matchSettleFactoryCheckInfoRepository.matchSettleFactorCheckInfoListCaseTwo(matchSettleScore.getStandardMatchId(),settleNumList);
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
//    public MatchEventInfo getOldMatchInfoByCancel(MatchEventInfo data) {
////        //0 迭代获取取消事件
////        //1.取消事件
//        List<MatchEventInfo> oldMatchInfos =matchEventInfoRepository.getMatchEventInfoCaseOne(data.getThirdMatchId(),data.getExtraInfo(),data.getDataSourceCode(),data.getSportId());
//        if(oldMatchInfos.size()==0){
//            //事件未消费
//            log.error("canleEvent 事件未消费入库" + data.getEventCode() + "事件ID:" + data.getThirdEventId());
//            return null;
//        }
//        MatchEventInfo oldEvent = oldMatchInfos.get(0);
////        if(oldEvent.getEventCode().equals(DELETE_EVENT)){
////            data.setExtraInfo(oldEvent.getThirdEventId());
////            return getOldMatchInfoByCancel(data);
////        }
//        if (!EffectScoresCode.chargeEffectScores(data.getSportId(), oldEvent.getEventCode())) {
//            return null;
//        }
//        return oldEvent;
////        String eventKey="SCORES_DELETE:"+data.getDataSourceCode()+":"+data.getStandardMatchId()+data.getExtraInfo();
////        Object o= redisService.get(eventKey);
////        try{
////            if(o!=null){
////                MatchEventInfo matchEventInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()),MatchEventInfo.class);
////                return matchEventInfo;
////            }else {
////                return null;
////            }
////        }catch (Exception e){
////            log.error("{}",e);
////            return null;
////        }
//    }
//
//    /**
//     * 入参: 需要结算的比分
//     * 返回:  true 可以结算  false 不能结算
//     * 这个方法改为 通用阶段比分的校验方法
//     * 涵盖 :  1. 比分和事件对比逻辑
//     * 2. 比分和比分对比逻辑
//     * 3.顺序结算按钮开关的阶段比分逻辑
//     * 4.自动补充 无事件逻辑
//     */
//    @Override
//    public boolean checkSettleScoreAndAutoSettleNonEvent(MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo) {
//        try {
//            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScore.getStandardMatchId());
//            if(checkInfo!=null){
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
//    /**
//     * 篮球独有校验机制
//     */
//    @Override
//    public boolean checkBasketPeriodScoreOrder(MatchSettleScore matchSettleScore) {
//        Long standardMatchId = matchSettleScore.getStandardMatchId();
//        if (standardMatchId == null || standardMatchId == 0L) {
//            return true;
//        }
//        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//        if (matchSettleInfo== null) {
//            return true;
//        }
//        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(standardMatchId);
//
//        if (matchSettleInfo.getSettleOrderClosed() != null &&
//                matchSettleInfo.getSettleOrderClosed() != 0) {
//            return true;
//        }
//        List<Integer> statusList = new ArrayList<>();
//        statusList.add(1);
//        statusList.add(0);
//        statusList.add(2);
//        statusList.add(4);
//        //1.根据当前结算编码得到他之前的结算编码
//        List<String> settleNumList = SettleNumUtils.countBasketballScoreSettleNumBefore(matchSettleScore.getSettleNum(), standardMatchInfo.getMatchLength());
//        if (settleNumList.size() == 0) {
//            return true;
//        }
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        example.createCriteria().andSettleNumIn(settleNumList).andStandardMatchIdEqualTo(standardMatchId).andStatusIn(statusList);
//        List<MatchSettleScore> list = matchSettleScoreMapper.selectByExample(example);
//
//        //2.判断之前的结算编码是否已经结算，如果没有结算则不能结算返回false
//        if (list.size() != 0) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean updateMatchGrayStatus(Long standardMatchId) {
//        try {
//            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//            MatchSettleEventExample eventExample = new MatchSettleEventExample();
//            eventExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStatusNotEqualTo(SETTLED).andEventTypeEqualTo(1)
//                    .andIsGreyEqualTo(1);
//            List<MatchSettleEvent> events = matchSettleEventMapper.selectByExample(eventExample);
//            if (events.size() != 0) {
//                matchSettleInfo.setIsGray(1);
//                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                return true;
//            }
//            MatchSettleScoreExample scoreExample = new MatchSettleScoreExample();
//            scoreExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStatusNotEqualTo(SETTLED)
//                    .andIsGreyEqualTo(1);
//            List<MatchSettleScore> scores = matchSettleScoreMapper.selectByExample(scoreExample);
//            if (scores.size() != 0) {
//                matchSettleInfo.setIsGray(1);
//                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                return true;
//            }
//            matchSettleInfo.setIsGray(0);
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//            return false;
//        } catch (Exception e) {
//
//        }
//        return false;
//    }
//
//    private void sendNoneEventSettled(MatchSettleInfo matchSettleInfo, MatchSettleScore matchSettleScore, CheckPeriodEventEquileDto checkPeriodEventEquileDto) {
//        try {
//            //1.查询需要生成的non 事件 没有则生成
//            MatchSettleEvent matchSettleEvent = null;
//            MatchSettleEventExample eventExample = new MatchSettleEventExample();
//            eventExample.createCriteria().andEventCodeEqualTo(matchSettleScore.getEventCode()).andPeriodIdEqualTo(checkPeriodEventEquileDto.getPeriod()).andEventOrderEqualTo(checkPeriodEventEquileDto.getOrderNum())
//                    .andStandardMatchIdEqualTo(matchSettleInfo.getStandardMatchId()).andEventTypeEqualTo(1);
//            List<MatchSettleEvent> list = matchSettleEventMapper.selectByExample(eventExample);
//            if (list.size() != 0) {
//                matchSettleEvent = list.get(0);
//            } else {
//                matchSettleEvent = new MatchSettleEvent();
//                matchSettleEvent.setId(IdWorker.getId());
//                matchSettleEvent.setEventCode(matchSettleScore.getEventCode());
//                matchSettleEvent.setStandardMatchId(matchSettleInfo.getStandardMatchId());
//                matchSettleEvent.setSportId(matchSettleInfo.getSportId());
//                //需要計算
//                String SettleNum = SettleNumUtils.getEventSettleNumByPeriodAndEventCode(matchSettleScore.getEventCode(), checkPeriodEventEquileDto.getPeriod());
//                matchSettleEvent.setSettleNum(SettleNum);
//                matchSettleEvent.setCreateTime(System.currentTimeMillis());
//                matchSettleEvent.setEventType(1);
//                matchSettleEvent.setHasDeleteEvent(0);
//                matchSettleEvent.setGoWaterStatus(0);
//                matchSettleEvent.setIsGrey(0);
//                matchSettleEvent.setPeriodId(checkPeriodEventEquileDto.getPeriod());
//                matchSettleEvent.setStatus(1);
//                matchSettleEvent.setEventOrder(checkPeriodEventEquileDto.getOrderNum());
//                matchSettleEvent.setSettleCount(0);
//                matchSettleEvent.setSettleTimes(0);
//                matchSettleEvent.setCheckNumber(1);
//                matchSettleEvent.setThirdEventSourceId(matchSettleEvent.getId());
//                matchSettleEventMapper.insert(matchSettleEvent);
//                MatchSettleEvent extryEvent = new MatchSettleEvent();
//                extryEvent.setId(IdWorker.getId());
//                extryEvent.setEventCode(matchSettleScore.getEventCode());
//                extryEvent.setStandardMatchId(matchSettleInfo.getStandardMatchId());
//                extryEvent.setSportId(matchSettleInfo.getSportId());
//                //需要計算
//                extryEvent.setSettleNum(SettleNum);
//                extryEvent.setCreateTime(System.currentTimeMillis());
//                extryEvent.setEventType(2);
//                extryEvent.setHasDeleteEvent(0);
//                extryEvent.setGoWaterStatus(0);
//                extryEvent.setIsGrey(0);
//                extryEvent.setPeriodId(checkPeriodEventEquileDto.getPeriod());
//                extryEvent.setStatus(1);
//                extryEvent.setEventOrder(checkPeriodEventEquileDto.getOrderNum());
//                extryEvent.setSettleCount(0);
//                extryEvent.setSettleTimes(0);
//                extryEvent.setCheckNumber(1);
//                extryEvent.setThirdEventSourceId(matchSettleEvent.getId());
//                matchSettleEventMapper.insert(extryEvent);
//            }
//            //2.给none 事件赋值 事件编码 比分 事件顺序 阶段
//            matchSettleEvent.setModifyTime(System.currentTimeMillis());
//            matchSettleEvent.setT1(checkPeriodEventEquileDto.getEventT1());
//            matchSettleEvent.setT2(checkPeriodEventEquileDto.getEventT2());
//            matchSettleEvent.setFirstT1(checkPeriodEventEquileDto.getEventFirstT1());
//            matchSettleEvent.setFirstT2(checkPeriodEventEquileDto.getEventFirstT2());
//            matchSettleEvent.setSecondT1(checkPeriodEventEquileDto.getEventSecondT1());
//            matchSettleEvent.setSecondT2(checkPeriodEventEquileDto.getEventSecondT2());
//            matchSettleEvent.setStatus(3);
//            matchSettleEvent.setSettleCount(1);
//            matchSettleEvent.setSettleTimes(1);
//            matchSettleEvent.setDataSourceCode("PA");
//            matchSettleEvent.setIsAutoSettle(1);
//            matchSettleEvent.setOperater("system");
//            //主客隊
//            String homeAway = SettleNumUtils.getNoneEventHomeAway(matchSettleScore);
//            matchSettleEvent.setHomeAway(homeAway);
//            //3.调用结算方法，结算方式为自动 数据商为自动补充
//            //1.从核对对象 复制 事件 到结算对象
//            //2.修改结算对象状态
//            matchSettleEvent.setStatus(SETTLED);
//            //3.设置结算对象的 是否自动结算方式
//            //4.设置结算人 结算次数  是否二次结算等
//            matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
//            matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount() == null ? 1 : matchSettleEvent.getSettleCount() + 1);
//            matchSettleEvent.setModifyTime(System.currentTimeMillis());
//            //只有一次结算会走这里
//            //5.更新结算对象到结算表
//            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//            //
//            //结算时把回滚订单数清零
////            matchSettleService.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
//
//            //6.日志
//            String userName = "system";
//            iMatchSettleLogService.matchSettleEventAddLog(matchSettleEvent, userName,
//                    OperateLogTypeEnum.SCORE_SETTLE.getCode().toString(), "", "");
//            // 如果是进球事件 需要编辑进球方式
//            if (matchSettleEvent.getEventCode().equals("goal") && matchSettleEvent.getEventType() == 1) {
//                MatchSettleEvent extryEvent = matchSettleService.getExtryEvent(matchSettleEvent);
//                if (extryEvent != null) {
//                    extryEvent.setT1(matchSettleEvent.getT1());
//                    extryEvent.setT2(matchSettleEvent.getT2());
//                    extryEvent.setHomeAway(matchSettleEvent.getHomeAway());
//                    extryEvent.setModifyTime(System.currentTimeMillis());
//                    matchSettleEventMapper.updateByPrimaryKey(extryEvent);
//                }
//            }
//            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
//            //8.ws推送
//            String eventCode = matchSettleScore.getEventCode();
//
//            wsPushService.pushStandardSettleScores(matchSettleEvent.getStandardMatchId(), eventCode);
//            //4.线程等待1~2秒
//            Thread.sleep(1000);
//            matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
//        } catch (InterruptedException e) {
//            log.error("sendNoneEventSettled error:", e);
//        }
//        //5.返回成功
//    }
//
//    /**
//     * 校验当前的阶段比分是否和事件一致
//     */
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
//
////    private boolean normalCheckAutoSettleNonEvent(MatchSettleScore matchSettleScore,MatchSettleCheckInfo checkInfo) {
////        //只需要校验 全场结算的时候 全场= 上半场 +下半场
////        if(matchSettleScore.getEventCode().equals("corner")){
////            if(!matchSettleScore.getSettleNum().equals("203")){
////                return true;
////            }
////        }
////        if(matchSettleScore.getEventCode().equals("fa_card")){
////            if(!matchSettleScore.getSettleNum().equals("309")){
////                return true;
////            }
////        }
////        if (checkInfo == null && matchSettleScore.getEventCode().equals("goal")) {
////            return validGoalSettle(matchSettleScore);
////        } else {
////            if(matchSettleScore.getEventCode().equals("goal")){
////                if(!matchSettleScore.getSettleNum().equals("1010")){
////                    return true;
////                }
////            }
////            if(isPeriodScoreEquile(matchSettleScore, checkInfo)==0){
////                return true;
////            }
////        }
////        return false;
////    }
//
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
//    @Override
//    public void updateMatchCurrentEventStatus(Long standardMatchId) {
//
//        try {
//            int deleteGoal = 0;
//            int grayGoal = 0;
//
//            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
//            if (matchSettleInfo!=null && matchSettleInfo.getSportId()!=null && (matchSettleInfo.getSportId().intValue()!=1 && matchSettleInfo.getSportId().intValue()!=2)){
//                return;
//            }
//            MatchSettleEventExample goalEvent = new MatchSettleEventExample();
//            goalEvent.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStatusNotEqualTo(3);
//            List<MatchSettleEvent> goalEventList = matchSettleEventMapper.selectByExample(goalEvent);
//            for (MatchSettleEvent matchSettleEvent : goalEventList) {
//                if (matchSettleEvent.getIsGrey() != null && matchSettleEvent.getIsGrey() == 1) {
//                    grayGoal = 1;
//                }
//                if (matchSettleEvent.getHasDeleteEvent() != null && matchSettleEvent.getHasDeleteEvent() == 1) {
//                    deleteGoal = 1;
//                }
//            }
//            MatchSettleScoreExample goalScoreExa = new MatchSettleScoreExample();
//            goalScoreExa.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStatusNotEqualTo(3);
//            List<MatchSettleScore> goalScoreList = matchSettleScoreMapper.selectByExample(goalScoreExa);
//
//            for (MatchSettleScore matchSettleScore : goalScoreList) {
//                if (matchSettleScore.getIsGrey() != null && matchSettleScore.getIsGrey() == 1) {
//                    grayGoal = 1;
//                }
//                if (matchSettleScore.getHasDeleteEvent() != null && matchSettleScore.getHasDeleteEvent() == 1) {
//                    deleteGoal = 1;
//                }
//            }
//            if (deleteGoal == 1 && grayGoal == 1) {
//                matchSettleInfo.setIsGray(1);
//                matchSettleInfo.setHasDeleteEvent(1);
//                matchSettleInfo.setCurrentEventStatus(1);
//            } else if (deleteGoal == 1 && grayGoal == 0) {
//                matchSettleInfo.setIsGray(0);
//                matchSettleInfo.setHasDeleteEvent(1);
//                matchSettleInfo.setCurrentEventStatus(2);
//            } else if (deleteGoal == 0 && grayGoal == 1) {
//                matchSettleInfo.setIsGray(1);
//                matchSettleInfo.setHasDeleteEvent(0);
//                matchSettleInfo.setCurrentEventStatus(1);
//            } else {
//                matchSettleInfo.setIsGray(0);
//                matchSettleInfo.setHasDeleteEvent(0);
//                matchSettleInfo.setCurrentEventStatus(0);
//            }
//            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//        }catch (Exception e){
//            log.error("{标准赛事Id:"+standardMatchId+",修改灰色区间标识出错:",e);
//        }
//    }
//
//
//    @Override
//    public void updateMatchFifteenMinGraySettleFactor(Long standardMatchId, String settleNum) {
//
//        try {
//
//            //1,判断是否是上,下的6个15分钟区间
//            String fifteenSettleNum = grayIntervalService.fifteenMinSettleNumMap.get(settleNum);
//            if (StringUtils.isAnyEmpty(fifteenSettleNum)) {
//                return;
//            }
//            //2,判断半场是否已经结算,未结算直接返回
//            MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
//            matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId)
//                    .andSettleNumEqualTo(fifteenSettleNum).andStatusEqualTo(SETTLED);
//            List<MatchSettleScore> matchSettleScoreList = matchSettleScoreMapper.selectByExample(matchSettleScoreExample);
//            if (matchSettleScoreList.isEmpty()) {
//                return;
//            }
//            //3,半场已经结算,判断已经结算的阶段总比分是否跟半场一致,如果一致,取消半场还未结算的灰色区间
//            MatchSettleScore matchSettleScoreHalfTime = matchSettleScoreList.get(NOT_EDIT);
//            List<String> settleNumList = MatchPeriodEnum.getFootBallPeriodSettleNumList(fifteenSettleNum);
//            MatchSettleScoreExample matchSettleNumExample = new MatchSettleScoreExample();
//            matchSettleNumExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId)
//                    .andSettleNumIn(settleNumList).andStatusEqualTo(SETTLED);
//            List<MatchSettleScore> matchSettleNumList = matchSettleScoreMapper.selectByExample(matchSettleNumExample);
//            if (matchSettleNumList.isEmpty()) {
//                return;
//            }
//            Integer sumScoreT1 = 0;
//            Integer sumScoreT2 = 0;
//            for (MatchSettleScore settleScore : matchSettleNumList) {
//                if (settleScore.getT1() != null && settleScore.getT1() > 0) {
//                    sumScoreT1 += settleScore.getT1();
//                }
//                if (settleScore.getT2() != null && settleScore.getT2() > 0) {
//                    sumScoreT2 += settleScore.getT2();
//                }
//            }
//            if (matchSettleScoreHalfTime.getT1() != null && matchSettleScoreHalfTime.getT2() != null && matchSettleScoreHalfTime.getT1().equals(sumScoreT1) && matchSettleScoreHalfTime.getT2().equals(sumScoreT2)) {
//                MatchSettleScoreExample matchSettleScoreGrayExample = new MatchSettleScoreExample();
//                matchSettleScoreGrayExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStatusEqualTo(NOT_EDIT).andSettleNumIn(settleNumList);
//                List<MatchSettleScore> matchSettleScoreGrayList = matchSettleScoreMapper.selectByExample(matchSettleScoreGrayExample);
//                for (MatchSettleScore matchSettleScoreGray : matchSettleScoreGrayList) {
//                    matchSettleScoreGray.setIsGrey(NOT_EDIT);
//                    matchSettleScoreGray.setCurrentEventStatus(NOT_EDIT);
//                    matchSettleScoreMapper.updateByPrimaryKey(matchSettleScoreGray);
//                }
//            }
//        } catch (Exception e) {
//            log.error("标准赛事Id:" + standardMatchId + ",更新15分钟灰色区间:" + settleNum + ",的结算因子出错:", e);
//        }
//    }
//
//    private String getCheckUserName(String userName, List<String> auditors) {
//        int number = auditors.indexOf(userName) + 1;
//        userName = userName + ",(第" + number + "人)";
//        return userName;
//    }
//
//
//    public void validateDeleteEvent(MatchSettleEvent matchSettleEvent, List<String> deleteSettleNums, MatchEventInfo data) {
//        log.info("linkId::{}::eventId:{} addSettleMention with settleEventId:{} start!", data.getLinkId(), data.getThirdEventId(), matchSettleEvent.getId());
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("matchSettleEvent", matchSettleEvent);
//        parameters.put("deleteSettleNums", deleteSettleNums);
//        settleMentionFactory.getProcessor(SettleMentionEnum.FOOTBALL_DELETE_EVENT).addSettleMention(parameters);
//        log.info("linkId::{}::eventId:{} addSettleMention with settleEventId:{} end!", data.getLinkId(), data.getThirdEventId(), matchSettleEvent.getId());
//    }
//
//    private boolean validGoalSettle(MatchSettleScore matchSettleScore) {
//        if (matchSettleScore.getT1() == null || matchSettleScore.getT2() == null) {
//            return false;
//        }
//        List<String> childSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(matchSettleScore.getSettleNum());
//        String parentSettleNum = FootballPeriodValidateEnum.getParentSettleNumList(matchSettleScore.getSettleNum());
//        List<String> brotherSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(parentSettleNum);
//        if (CollectionUtil.isEmpty(childSettleNumList) && parentSettleNum == null) {
//            return true;
//        }
//
//        MatchSettleScoreExample example = new MatchSettleScoreExample();
//        example.createCriteria().andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).andStatusEqualTo(SETTLED).andEventCodeEqualTo(SettleEventCodeEnum.FOOTBALL_GOAL.getValue());
//        List<MatchSettleScore> settleScores = matchSettleScoreMapper.selectByExample(example);
//        Map<String, MatchSettleScore> settleScoreMap = settleScores.stream().collect(Collectors.toMap(MatchSettleScore::getSettleNum, t -> t, (v1, v2) -> v1));
//
//        // valid child nodes
//        int sumScoreT1 = 0;
//        int sumScoreT2 = 0;
//        for (String settleNum : childSettleNumList) {
//            MatchSettleScore settleScore = settleScoreMap.getOrDefault(settleNum, null);
//            if (settleScore == null) {
//                continue;
//            }
//            if (settleScore.getT1() != null && settleScore.getT1() > 0) {
//                sumScoreT1 += settleScore.getT1();
//            }
//            if (settleScore.getT2() != null && settleScore.getT2() > 0) {
//                sumScoreT2 += settleScore.getT2();
//            }
//        }
//        if (sumScoreT1 > matchSettleScore.getT1() || sumScoreT2 > matchSettleScore.getT2()) {
//            return false;
//        }
//        // valid parent nodes
//        if (parentSettleNum == null || !settleScoreMap.containsKey(parentSettleNum)) {
//            return true;
//        }
//        sumScoreT1 = matchSettleScore.getT1();
//        sumScoreT2 = matchSettleScore.getT2();
//        for (String settleNum : brotherSettleNumList) {
//            MatchSettleScore settleScore = settleScoreMap.getOrDefault(settleNum, null);
//            if (settleScore == null) {
//                continue;
//            }
//            if (settleScore.getT1() != null && settleScore.getT1() > 0) {
//                sumScoreT1 += settleScore.getT1();
//            }
//            if (settleScore.getT2() != null && settleScore.getT2() > 0) {
//                sumScoreT2 += settleScore.getT2();
//            }
//        }
//        if (sumScoreT1 > settleScoreMap.get(parentSettleNum).getT1() || sumScoreT2 > settleScoreMap.get(parentSettleNum).getT2()) {
//            return false;
//        }
//        return true;
//    }
//
//    private void validateDataScoreMismatch(MatchSettleCheckInfo matchSettleCheckInfo, String settleNum, Long sportId) {
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("matchSettleCheckInfo", matchSettleCheckInfo);
//        parameters.put("settleNum", settleNum);
//        parameters.put("sportId", sportId);
//        SettleMentionEnum settleMentionEnum = SettleMentionEnum.FOOTBALL_PHASE_SCORE_MISMATCH;
//        if (sportId == 2) {
//            settleMentionEnum = SettleMentionEnum.BASKETBALL_PHASE_SCORE_MISMATCH;
//        }
//        settleMentionFactory.getProcessor(settleMentionEnum).addSettleMention(parameters);
//    }
//
//    private void validateGrayArea(MatchSettleScore matchSettleScore) {
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("matchSettleScore", matchSettleScore);
//        settleMentionFactory.getProcessor(SettleMentionEnum.BASKETBALL_GRAY_AREA).addSettleMention(parameters);
//    }
//
//    private void initDelaySettleScore(MatchSettleScore matchSettleScore, MatchSettleCheckInfo checkInfo,Long second){
//        log.info("开始处理initDelaySettleScore,score.id: {},settleNum: {},checkId :{}",matchSettleScore.getId(),matchSettleScore.getSettleNum(),checkInfo.getId());
//        if (!matchSettleScore.getSportId().equals(1l)){
//            log.info("isDelaySettleScore目前只针对足球StandardMatchId: {},matchSettleScore.id:{}",matchSettleScore.getStandardMatchId(),matchSettleScore.getId());
//            return;
//        }
//        if (checkInfo.getDataSourceCode().equals("PA")){
//            log.info("isDelaySettleScore只处理数据源的比分StandardMatchId: {},matchSettleScore.id:{}",matchSettleScore.getStandardMatchId(),matchSettleScore.getId());
//            return;
//        }
//        boolean isGoal = false;
//        boolean isBooking = false;
//        boolean isCorner = false;
//        try {
//            if (goalDelaySettleNum.contains(matchSettleScore.getSettleNum())) {
//                isGoal = true;
//            }
//            if (bookingDelaySettleNum.contains(matchSettleScore.getSettleNum())) {
//                isBooking = true;
//            }
//            if (cornerDelaySettleNum.contains(matchSettleScore.getSettleNum())) {
//                isCorner = true;
//            }
//            if (isCorner || isBooking || isGoal) {
//                MatchSettleTemplate matchSettleTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleScore.getStandardMatchId(), SettleTemplateTypeEnum.COUNT_DOWEN.code);
//                if (null == matchSettleTemplate) {
//                    log.info("isDelaySettleScore无对应的倒计时模板,不进行延迟结算StandardMatchId: {},score.id: {},settleNum: {}", matchSettleScore.getStandardMatchId(), matchSettleScore.getId(), matchSettleScore.getSettleNum());
//                    return;
//                }
//                long delayTime = 0; //延迟结算秒数
//                List<DownSettleDto> dtoList = SettleTemplateJsonUtils.tansferDownList(matchSettleTemplate.getTemplateJson());
//                if (isGoal) {
//                    delayTime = dtoList.get(0).getGoal15Min();
//                }
//                if (isCorner) {
//                    delayTime = dtoList.get(0).getCorner15Min();
//                }
//                if (isBooking) {
//                    delayTime = dtoList.get(0).getBooking15Min();
//                }
//                log.info("isDelaySettleScore进入延迟结算StandardMatchId:{},matchSettleScore.id:{},settleNum:{},template:{}", matchSettleScore.getStandardMatchId(), matchSettleScore.getId(), matchSettleScore.getSettleNum(), matchSettleTemplate);
//                //获取当前时间过了灰色区间时间多少秒
//                Long secondTag = FootBallMatchSettleScoreUtils.getDelaySettleSeconds(matchSettleScore,second);
//                log.info("{}当前时间过了灰色区间秒数:{}",matchSettleScore.getId(),secondTag);
//                //根据时效判定当前是否符合确认时间
//                GrayAreaSettleDto grayAreaSettleDto = new GrayAreaSettleDto();
//                MatchSettleTemplate gayTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleScore.getStandardMatchId(),SettleTemplateTypeEnum.GRAY_AREA.code);
//                if (gayTemplate!=null && !StringUtils.isAnyEmpty(gayTemplate.getTemplateJson())) {
//                    List<GrayAreaSettleDto> grayAreaSettleDtoList = SettleTemplateJsonUtils.tansferGrayAreaList(gayTemplate.getTemplateJson());
//                    Map<String, GrayAreaSettleDto> dataSourceGrayAreaOldMap = grayAreaSettleDtoList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
//                    grayAreaSettleDto = dataSourceGrayAreaOldMap.get(checkInfo.getDataSourceCode());
//                }
//                if (checkInfo.getEventCode().equals("corner")){
//                        Integer cornerGrayTime = grayAreaSettleDto.getCorner15Min() == null ? 120 : grayAreaSettleDto.getCorner15Min();
//                        delayTime = delayTime + cornerGrayTime - secondTag;
//
//                } else if (matchSettleScore.getEventCode().equals("fa_card")||matchSettleScore.getEventCode().equals("yellow_card")||matchSettleScore.getEventCode().equals("red_card")){
//                        Integer bookingGrayTime = grayAreaSettleDto.getBooking15Min() == null ? 120 : grayAreaSettleDto.getBooking15Min();
//                        delayTime = delayTime + bookingGrayTime - secondTag;
//                }else {
//                        Integer goal15MinGrayTime = grayAreaSettleDto.getGoal15Min() == null ? 30 : grayAreaSettleDto.getGoal15Min();
//                        Integer goal5MinGrayTime = grayAreaSettleDto.getGoal5Min() == null ? 5 : grayAreaSettleDto.getGoal5Min();
//                        if (goal15SettleNum.contains(matchSettleScore.getSettleNum())) {
//                            delayTime = delayTime + goal15MinGrayTime - secondTag;
//                        }
//                        if (goal5SettleNum.contains(matchSettleScore.getSettleNum())) {
//                            delayTime = delayTime + goal5MinGrayTime - secondTag;
//                    }
//
//                }
//                if (delayTime > 0) {
//                    //先查询一次是否已经存在记录
//                    MatchDelaySettleInfoExample example2 = new MatchDelaySettleInfoExample();
//                    example2.createCriteria().andStandardMatchIdEqualTo(checkInfo.getStandardMatchId()).andScoreIdEqualTo(matchSettleScore.getId()).andCheckInfoIdEqualTo(checkInfo.getId());
//                    List<MatchDelaySettleInfo> delays = matchDelaySettleInfoMapper.selectByExample(example2);
//                    if (CollectionUtils.isEmpty(delays)) {
//                        //初始化延迟结算信息表
//                        MatchDelaySettleInfo matchDelaySettleInfo = FootBallMatchSettleScoreUtils.initMatchDelaySettleInfo(matchSettleScore, checkInfo);
//                        matchDelaySettleInfo.setDelayTime(System.currentTimeMillis()+delayTime*1000);
//                        matchDelaySettleInfo.setDelayTimeSecond(delayTime);
//                        matchDelaySettleInfoMapper.insert(matchDelaySettleInfo);
//                    }
////                    else {
////                        MatchDelaySettleInfo matchDelaySettleInfo = delays.get(0);
////                        matchDelaySettleInfo.setDelayTime(System.currentTimeMillis()+delayTime*1000);
////                        matchDelaySettleInfo.setDelayTimeSecond(delayTime);
////                        matchDelaySettleInfoMapper.updateByPrimaryKey(matchDelaySettleInfo);
////                    }
//
//                } else {
//                    log.info("isDelaySettleScore不属于延迟结算的阶段,不进行延迟结算matchSettleScore.id: {},matchSettleScore.settleNum: {}", matchSettleScore.getId(), matchSettleScore.getSettleNum());
//                }
//            }
//            }catch(Exception e){
//                log.error("{},isDelaySettleScore校验初始化延迟结算异常: {}", matchSettleScore.getId(), e.getMessage());
//            }
//    }
//
//    private void initDelaySettleEvent(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfo checkInfo){
//        log.info("initDelaySettleEvent,score.id: {},settleNum: {},checkId :{}",matchSettleEvent.getId(),matchSettleEvent.getSettleNum(),checkInfo.getId());
//        if (!matchSettleEvent.getSportId().equals(1l)){
//            log.info("initDelaySettleEvent目前只针对足球StandardMatchId: {},matchSettleEvent.id:{}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId());
//            return;
//        }
//        if (checkInfo.getDataSourceCode().equals("PA")){
//            log.info("initDelaySettleEvent只处理数据源的比分StandardMatchId: {},matchSettleScore.id:{}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId());
//            return;
//        }
////        boolean isGoal = false;
//        boolean isBooking = false;
//        boolean isCorner = false;
//        try {
////            if (goalDelaySettleNum.contains(matchSettleScore.getSettleNum())){
////                isGoal = true;
////            }
//            if (bookingEventDelaySettleNum.contains(matchSettleEvent.getSettleNum())){
//                isBooking = true;
//            }
//            if (cornerEventDelaySettleNum.contains(matchSettleEvent.getSettleNum())){
//                isCorner = true;
//            }
//            if (isCorner||isBooking){
//                MatchSettleTemplate matchSettleTemplate = settleTemplateService.getTemplateByStandardMatchId(matchSettleEvent.getStandardMatchId(), SettleTemplateTypeEnum.COUNT_DOWEN.code);
//                if (null == matchSettleTemplate){
//                    log.info("initDelaySettleEvent无对应的倒计时模板,不进行延迟结算StandardMatchId: {},event.id: {},settleNum: {}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId(),matchSettleEvent.getSettleNum());
//                    return;
//                }
//                long delayTime = 0; //延迟结算秒数
//                List<DownSettleDto> dtoList = SettleTemplateJsonUtils.tansferDownList(matchSettleTemplate.getTemplateJson());
////                if (isGoal){
////                    delayTime = dtoList.get(0).getGoal15Min();
////                }
//                if (isCorner){
//                    delayTime = dtoList.get(0).getCorner15Min();
//                }
//                if (isBooking){
//                    delayTime = dtoList.get(0).getBooking15Min();
//                }
//                if (delayTime>0){
//                        //先查询一次是否已经存在记录
//                        MatchDelaySettleInfoExample example2 = new MatchDelaySettleInfoExample();
//                        example2.createCriteria().andStandardMatchIdEqualTo(checkInfo.getStandardMatchId()).andScoreIdEqualTo(matchSettleEvent.getId()).andCheckInfoIdEqualTo(checkInfo.getId());
//                        List<MatchDelaySettleInfo> delays = matchDelaySettleInfoMapper.selectByExample(example2);
//                        if (CollectionUtils.isEmpty(delays)){
//                            //初始化延迟结算信息表
//                            MatchDelaySettleInfo matchDelaySettleInfo = FootBallMatchSettleScoreUtils.initMatchDelayEventInfo(matchSettleEvent,checkInfo);
//                            matchDelaySettleInfo.setDelayTime(System.currentTimeMillis()+delayTime*1000);
//                            matchDelaySettleInfo.setDelayTimeSecond(delayTime);
//                            matchDelaySettleInfoMapper.insert(matchDelaySettleInfo);
//                        }
////                        else {
////                            MatchDelaySettleInfo matchDelaySettleInfo = delays.get(0);
////                            matchDelaySettleInfo.setDelayTime(System.currentTimeMillis()+delayTime*1000);
////                            matchDelaySettleInfo.setDelayTimeSecond(delayTime);
////                            matchDelaySettleInfoMapper.updateByPrimaryKey(matchDelaySettleInfo);
////                        }
//                        log.info("initDelaySettleEvent进入延迟结算StandardMatchId: {},matchSettleEvent.id:{}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId());
//                }
//            }else {
//                log.info("initDelaySettleEvent不属于延迟结算的阶段,不进行延迟结算matchSettleScore.id: {},matchSettleEvent.settleNum: {}",matchSettleEvent.getId(),matchSettleEvent.getSettleNum());
//            }
//        }catch (Exception e){
//            log.error("{},initDelaySettleEvent校验初始化延迟结算异常: {}",matchSettleEvent.getId(),e.getMessage());
//        }
//    }
//
//
//    private List<MatchSettleCheckInfo> moveDelayCheckInfo(Long standardMatchId,List<MatchSettleCheckInfo> checkInfos){
//
//        if(CollectionUtils.isEmpty(checkInfos)){
//            return checkInfos;
//        }
//        MatchDelaySettleInfoExample example = new MatchDelaySettleInfoExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
//        List<MatchDelaySettleInfo> delaySettleInfos = matchDelaySettleInfoMapper.selectByExample(example);
//        if (CollectionUtils.isEmpty(delaySettleInfos)){
//            return checkInfos;
//        }
//        Long nowTime = System.currentTimeMillis();
//        Map<Long, MatchDelaySettleInfo> delaySettleInfosMap = delaySettleInfos.stream().collect(Collectors.toMap(MatchDelaySettleInfo::getCheckInfoId, Function.identity(), (v1, v2)->v1));
//        checkInfos = checkInfos.stream().filter(t-> {
//            MatchDelaySettleInfo info =  delaySettleInfosMap.get(t.getId());
//            if (info != null) {
//                if (info.getDelayTime() > nowTime) {
//                    log.info("moveDelayCheckInfo,未到达延迟结算时间,不参与结算核对,standardMatchId: {},id: {},delayTime: {},nowTime: {}",standardMatchId,info.getCheckInfoId(),info.getDelayTime(),nowTime);
//                    return false;
//                }
//            }
//            return true;
//        }).collect(Collectors.toList());
//        return checkInfos;
//    }
//
//    private void deleteAuditorCheckInfo(Long settleScoreEventId) {
//        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
//        example.createCriteria().andSettleScoreEventIdEqualTo(settleScoreEventId);
//        List<MatchSettleCheckInfo> list =  matchSettleCheckInfoMapper.selectByExample(example);
//        if (!CollectionUtils.isEmpty(list) && list.size() == 1 && "PA".equals(list.get(0).getDataSourceCode()) && list.get(0).getCheckStatus()==0) {
//            matchSettleCheckInfoMapper.deleteByPrimaryKey(list.get(0).getId());
//        }
//    }
//
//}