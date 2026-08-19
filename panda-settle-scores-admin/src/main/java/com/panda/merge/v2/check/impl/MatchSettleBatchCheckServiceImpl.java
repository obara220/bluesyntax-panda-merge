package com.panda.merge.v2.check.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.api.IBasketballMatchScoresSettleApi;
import com.panda.merge.common.enums.*;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.settle.*;
import com.panda.merge.filter.football.impl.MatchScoresSettleInitChainFilter;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.respository.MatchEventInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.utils.*;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.repository.*;
import com.panda.merge.v2.service.*;
import com.panda.merge.v2.service.IMatchSettleEventService;
import com.panda.merge.v2.service.IMatchSettleScoreService;
import com.panda.merge.v2.service.helper.*;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.*;
import static com.panda.merge.constant.RepositoryConstant.MATCH_EVENT_INFO;
import static com.panda.merge.utils.SettleNumUtils.fiveMinuteMap;


@Service
@Slf4j
public class MatchSettleBatchCheckServiceImpl implements IMatchSettleBatchCheckService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchScoresSettleInitChainFilter matchScoresSettleInitChainFilter;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private IMatchSettleGoalStatusService matchSettleGoalStatusService;
    @Autowired
    private IMatchSettleScoreService matchSettleScoreService;
    @Autowired
    private IMatchSettleLogService matchSettleLogService;
    @Autowired
    private IMatchSettleEventService matchSettleEventService;



    @Autowired
    IBasketballMatchScoresSettleApi basketballMatchScoresSettleApi;

    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    IMatchSettleDataSourceSwitchService matchSettleDataSourceSwitchService;



    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    private MatchEventInfoRepository matchEventInfoRepository;
    @Autowired
    private MatchSettleScoreHelper matchSettleScoreHelper;
    @Autowired
    private MatchSettleDataSourceSwitchRepository matchSettleDataSourceSwitchRepository;
    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreRepository;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;
    @Autowired
    private MatchDelaySettleInfoV2Repository matchDelaySettleInfoRepository;
    @Autowired
    private MatchSettleGoalStatusRepository matchSettleGoalStatusRepository;
    @Autowired
    private MatchSettleOperateLogV2Repository matchSettleOperateLogRepository;
    @Autowired
    private MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    private MatchSettleInfoHelper matchSettleInfoHelper;
    @Autowired
    private MatchSettleBatchCheckServiceHelper matchSettleBatchCheckServiceHelper;
    @Autowired
    private MatchSettleRollBackInfoHelper matchSettleRoleBackInfoHelper;
    @Autowired
    private MatchSettleTemplateHelper matchSettleTemplateHelper;
    @Autowired
    private IDataSourceHeartbeatService dataSourceHeartbeatService;
    @Autowired
    private MentionStatusHelper mentionStatusHelper;


    private static final List<String> dataSourceCodeManually = Arrays.asList("PD", "PA", "PD2");
    /** 5/15分钟阶段结算不参与的数据源：不用于可用数据源、删除事件/数据不匹配不考虑 */
    private static final Set<String> DATA_SOURCE_5_15_IGNORE = new HashSet<>(Arrays.asList("N01", "N02", "N03", "LS"));

    private static final List<String> basketball6Mns = Arrays.asList(BasketBallSettleNumEnum.BK_Q1041.getCode(), BasketBallSettleNumEnum.BK_Q2041.getCode(),
            BasketBallSettleNumEnum.BK_Q3041.getCode(), BasketBallSettleNumEnum.BK_Q4041.getCode(), BasketBallSettleNumEnum.BK_Q1042.getCode(),
            BasketBallSettleNumEnum.BK_Q2042.getCode(), BasketBallSettleNumEnum.BK_Q3042.getCode(), BasketBallSettleNumEnum.BK_Q4042.getCode());

    private static final List<String> validEventCodeForSettle = Arrays.asList("goal", "kick_off", "corner", "fa_card", "yellow_card", "red_card", CommonConstant.BASKETBALL_SCORE_EVENT_CODE);
    List<String> goalDelaySettleNum = Arrays.asList("102","1034","1035","1036","103","1037","1038","1039","1040","1041","106","1044","1045","1046","107","1047","1048","1049","1050","1051");
    List<String> cornerDelaySettleNum = Arrays.asList("2011","2012","2013","201","2014","2015","2016","202","203","2017","2018","2019","206","2020","2021","2022","207","208");
    List<String> bookingDelaySettleNum = Arrays.asList("301","302","303","304","305","306","307","308","309","3010","3011","3012","3013","3014","3015","3016","3017","3018");
    List<String> basketballDelayNum = Arrays.asList("bk_q404","bk_q304","bk_q204","bk_q104","bk_1ht","bk_2ht","bk_ft_rg","bk_ft_et","bk_2htet","bk_et");
    List<String> bookingEventDelaySettleNum = Arrays.asList("3019","3020","30195","30205");
    List<String> cornerEventDelaySettleNum = Arrays.asList("204","205","2045","2055");

    List<String> goal15SettleNum =  Arrays.asList("102","103","106","107");
    List<String> goal5SettleNum = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040","1041", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051");

    List<String> ALL_5_SETTLE_NUMS = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040",
            "1041", "1042", "1043", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052", "1053");
    List<String> ALL_15_SETTLE_NUMS = Arrays.asList("102", "103", "104", "106", "107", "108", "2011",
            "2012", "2013", "2014", "2015", "2016", "301", "302", "303", "305", "306", "307");
    //重要核对校验阶段
    @Override
    public boolean batchCheckMatchThirdSettleScores(List<MatchSettleThirdScore> matchSettleThirdScores, String linkedId, Long second, CheckIsGreyDto checkIsGreyDto) {
        log.info("linkId::{} batchCheckMatchThirdSettleScores start with size {}", linkedId, matchSettleThirdScores.size());
        //1.当前赛事是否切换到2.0如果不是则不生成数据
        Long standardMatchId = matchSettleThirdScores.get(0).getStandardMatchId();
        Long thirdMatchId = matchSettleThirdScores.get(0).getThirdMatchId();
        String dataSourceCode = matchSettleThirdScores.get(0).getDataSourceCode();
        if (!isSettle2(standardMatchId)) {
            log.info("linkId::{} 当前赛事不是结算2.0 不触发核对比分操作X", linkedId);
            return false;
        }
        //2.查询需要核对的标准比分ID 做关系绑定
        Map<String, MatchSettleThirdScore> thirdScoreMap = matchSettleThirdScores.stream().collect(Collectors.toMap(MatchSettleThirdScore::getSettleNum, Function.identity(), (v1, v2)->v1));
        List<String> settleNums =new ArrayList<>(thirdScoreMap.keySet());
        List<MatchSettleScore> matchSettleScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNums, standardMatchId, null);
        matchSettleScores = matchSettleScores.stream().filter(t->t.getStatus()!=3 && (t.getSettleCount() == null ||  t.getSettleCount() == 0)).collect(Collectors.toList());
        if (matchSettleScores.size() == 0) {
            log.info("linkId::{} 目前所有事件都已结算或者别回滚过", linkedId);
            return false;
        }
        Long sportId = matchSettleScores.get(0).getSportId();
        Map<Long, MatchSettleScore> matchSettleScoreMap = matchSettleScores.stream().collect(Collectors.toMap(MatchSettleScore::getId, Function.identity(), (v1, v2)->v1));
        //这个时候就可以弄了,取消当前比分的灰色区间 足球才有灰色区间
        if(checkIsGreyDto!=null&&checkIsGreyDto.getThisDataSourceIsGray()!=null&&checkIsGreyDto.getThisDataSourceIsGray()==0){
            List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getModelBySportIdAndDataSource(sportId,matchSettleThirdScores.get(0).getDataSourceCode(),"1");
            if(!switches.isEmpty()){
                matchSettleTemplateHelper.batchCancelGrayStatus(dataSourceCode, matchSettleScores);
                log.info("linkId::{} 取消灰色区间状态", linkedId);
            }
        }
        //3 redis 加锁 锁 结算比分ID 先忽略 TODO

        //3.1 查询比分核对类是否存在
        List<Long> scoreIds = matchSettleScores.stream().map(MatchSettleScore::getId).collect(Collectors.toList());
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndDataSourceCode(scoreIds, dataSourceCode);
        Map<Long,MatchSettleCheckInfo> checkInfoMap = list.stream().collect(Collectors.toMap(MatchSettleCheckInfo::getSettleScoreEventId, Function.identity(), (v1, v2)->v1));

        //3.2 已经存在则修改比分

        List<MatchSettleCheckInfo> batchCheckInfos = new ArrayList<>();
        List<Pair<MatchSettleCheckInfo,MatchSettleThirdScore>> insertCheckInfos = new ArrayList<>();
        for (MatchSettleScore matchSettleScore : matchSettleScores) {
            MatchSettleCheckInfo matchSettleCheckInfo = null;
            MatchSettleThirdScore matchSettleThirdScore = thirdScoreMap.get(matchSettleScore.getSettleNum());
            if (checkInfoMap.containsKey(matchSettleScore.getId())) {
                matchSettleCheckInfo = checkInfoMap.get(matchSettleScore.getId());
                if (!StringUtils.isAnyEmpty(matchSettleThirdScore.getOperater()) && (matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
                    matchSettleCheckInfo.setUserName(matchSettleThirdScore.getOperater() + "(" + matchSettleThirdScore.getDataSourceCode() +")");
                }
                //比分修正
                SettleCheckUtils.copyMatchSettleScores(matchSettleThirdScore, matchSettleCheckInfo);
                matchSettleCheckInfo.setIsGrey(matchSettleThirdScore.getIsGrey());
                if(matchSettleCheckInfo.getIsGrey()==null){
                    matchSettleCheckInfo.setIsGrey(0);
                }
//                if (!matchSettleCheckInfo.getDataSourceCode().equals("F01")){
                    //赛事比分入库的时候只做编辑不做自动审核
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//                }
                batchCheckInfos.add(matchSettleCheckInfo);
                log.info("linkId::{} scoreEventId:{} thirdScoreEventId:{} 更新matchSettleCheckInfo信息到数据库", linkedId,matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getThirdSettleScoreEventId());
            } else {
                // 8月 15日新版推送几率很大
                //3.3 不存在创建核对比分
                matchSettleCheckInfo = SettleCheckUtils.initMatchSettleScores(matchSettleScore, matchSettleThirdScore);
                if (!StringUtils.isAnyEmpty(matchSettleThirdScore.getOperater()) && (matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdScore.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
                    matchSettleCheckInfo.setUserName(matchSettleThirdScore.getOperater() + "(" + matchSettleThirdScore.getDataSourceCode() +")");
                }
                matchSettleCheckInfo.setIsGrey(matchSettleThirdScore.getIsGrey());
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                // BT LS 1X 默认生效
                if (matchSettleCheckInfo.getDataSourceCode().equals("BFZX") ||matchSettleCheckInfo.getDataSourceCode().equals("LS") ||matchSettleCheckInfo.getDataSourceCode().equals("BT") || matchSettleCheckInfo.getDataSourceCode().equals("1X")) {
                    if (matchSettleScore.getEventCode().equals("goal")) {
                        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
                    }
                }
                //5分钟编码 计算5分钟区间
                if (ALL_5_SETTLE_NUMS.contains(matchSettleThirdScore.getSettleNum())) {
                    matchSettleCheckInfo.setFiveMinSection(fiveMinuteMap.get(matchSettleThirdScore.getSettleNum()));
                }
                if(matchSettleCheckInfo.getIsGrey()==null){
                    matchSettleCheckInfo.setIsGrey(0);
                }
                batchCheckInfos.add(matchSettleCheckInfo);
                insertCheckInfos.add(Pair.of(matchSettleCheckInfo, matchSettleThirdScore));
                log.info("linkId::{} scoreEventId:{} thirdScoreEventId:{} 插入matchSettleCheckInfo信息到数据库", linkedId, matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getThirdSettleScoreEventId());
            }
        }
        matchSettleCheckInfoRepository.saveOrUpdateBatch(batchCheckInfos);
        log.info("linkId::{} 完成当前数据商比分核对数据入库", linkedId);
        if(!CollectionUtils.isEmpty(insertCheckInfos)) {
            matchSettleBatchCheckServiceHelper.validateDataScoreMismatch(insertCheckInfos, linkedId);
        }

        // 不再需要设置Redis key，数据不一致信息直接从mention status获取

        MatchSettleTemplate countDownTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.COUNT_DOWEN.code);
        log.info("linkId::{} TemplateJson {}", linkedId, countDownTemplate);
        MatchSettleTemplate gayTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(standardMatchId,SettleTemplateTypeEnum.GRAY_AREA.code);
        initDelaySettleScore(matchSettleScoreMap,batchCheckInfos,second,countDownTemplate,gayTemplate,linkedId, sportId);


        batchCheckInfos = batchCheckInfos.stream().filter(t->!(t.getIsGrey()!=null&&t.getIsGrey()==1)).collect(Collectors.toList());
        //15灰色区间的话 暂不做直接结算
        if (CollectionUtils.isEmpty(batchCheckInfos)) {
            log.info("linkId::{} 灰色区间返回", linkedId);
            return false;
        }
        Map<Long, MatchSettleCheckInfo> batchCheckInfoMap = batchCheckInfos.stream().collect(Collectors.toMap(MatchSettleCheckInfo::getSettleScoreEventId, Function.identity(), (v1, v2)->v1));
        // 移除matchSettleScoreMap中灰色区间的阶段和不符合事件编码的阶段
        MatchSettleGoalStatus goalStatus = matchSettleGoalStatusRepository.getByIdFromRedis(thirdMatchId);
        matchSettleScores = matchSettleScores.stream().filter(t->batchCheckInfoMap.containsKey(t.getId()) && validEventCodeForSettle.contains(t.getEventCode()) && isMatchGoalStatusConfirm(goalStatus, t.getEventCode())).collect(Collectors.toList());

        // 解析灰色区间模板
        GrayAreaSettleDto grayAreaSettleDto = new GrayAreaSettleDto();
        if (gayTemplate!=null && !StringUtils.isAnyEmpty(gayTemplate.getTemplateJson())) {
            List<GrayAreaSettleDto> grayAreaSettleDtoList = SettleTemplateJsonUtils.tansferGrayAreaList(gayTemplate.getTemplateJson());
            Map<String, GrayAreaSettleDto> dataSourceGrayAreaOldMap = grayAreaSettleDtoList.stream().collect(Collectors.toMap(GrayAreaSettleDto::getDataSourceCode, obj -> obj, (key1, key2) -> key1));
            grayAreaSettleDto = dataSourceGrayAreaOldMap.get(dataSourceCode);
        }
        // 对于5/15分钟阶段，进行删除事件和数据不一致判断
        // 删除事件：卡住所有5/15分钟阶段结算
        // 数据不一致：只卡住当前阶段和下一个阶段
        // 只对未结算的阶段进行判断
        List<MatchSettleScore> filteredMatchSettleScores = filterUnsettled5Or15MinPeriods(standardMatchId, dataSourceCode, matchSettleScores, linkedId);
        Map<Long, MatchSettleScore> filteredMatchSettleScoreMap = filteredMatchSettleScores.stream().collect(Collectors.toMap(t->t.getId(), Function.identity(),(v1,v2)->v1));
        List<MatchSettleCheckInfo> updatedStatusCheckInfo = new ArrayList<>();
        List<Pair<Object, MatchSettleCheckInfo>> checkMessages = new ArrayList<>();
        List<Pair<Object, MatchSettleCheckInfo>> batchSendCheckMessages = new ArrayList<>();
        for (MatchSettleScore matchSettleScore : matchSettleScores) {
            //灰色区间
            MatchSettleCheckInfo tempCheckInfo = batchCheckInfoMap.get(matchSettleScore.getId());
            if (FootBallMatchSettleScoreUtils.delaySettleSeconds(matchSettleScore, second,grayAreaSettleDto)||tempCheckInfo.getDataSourceCode().equals("BFZX")) {
                log.info("linkId::{} scoreEventId:{} 通过灰色区间加延迟校验,second: {},settleNum: {},grayAreaSettleDto: {}",linkedId,matchSettleScore.getId(), second,matchSettleScore.getSettleNum(),grayAreaSettleDto);
                //灰色区间无法做数据商判定确认
                tempCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
                updatedStatusCheckInfo.add(tempCheckInfo);
                updateSendTimes(tempCheckInfo);

                if (filteredMatchSettleScoreMap.containsKey(matchSettleScore.getId())){
                    checkMessages.add(Pair.of(matchSettleScore, tempCheckInfo));
                }
            } else {
                boolean send3Times = getSend3TimesByCache(tempCheckInfo);
                if (!send3Times) {
                    //推送更新推送次数
                    updateSendTimes(tempCheckInfo);
                    batchSendCheckMessages.add(Pair.of(matchSettleScore, tempCheckInfo));
                }
            }
        }
        if (!CollectionUtils.isEmpty(updatedStatusCheckInfo)) {
            matchSettleCheckInfoRepository.saveOrUpdateBatch(updatedStatusCheckInfo);
            batchCheckCommonMatchSettleScoreEvent(checkMessages, true, linkedId, countDownTemplate);
        }
        if (!CollectionUtils.isEmpty(batchSendCheckMessages)) {
            batchSendCheckMessage(batchSendCheckMessages, true, linkedId);
        }
        log.info("linkId::{} checkMatchThirdSettleScores end", linkedId);
        return true;
    }

    private void updateSendTimes(MatchSettleCheckInfo matchSettleCheckInfo) {
        String key = "SETTLE_SEND_TIMES:" + matchSettleCheckInfo.getId();
        Object sends = redisService.get(key);
        if (sends == null) {
            Integer times = 1;
            //存6小时
            redisService.set(key, times, 60 * 60 * 6);
        } else {
            try {
                Integer times = Integer.parseInt(sends.toString());
                times++;
                redisService.set(key, times, 60 * 60 * 6);
            } catch (Exception e) {
                log.error("MatchSettleCheckServiceImpl-updateSendTimes:", e);
            }
        }
    }

    private boolean getSend3TimesByCache(MatchSettleCheckInfo matchSettleCheckInfo) {
        String key = "SETTLE_SEND_TIMES:" + matchSettleCheckInfo.getId();
        Object sends = redisService.get(key);
        if (sends == null) {
            return false;
        } else {
            try {
                Integer times = Integer.parseInt(sends.toString());
                if (times >= 3) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                log.error("MatchSettleCheckServiceImpl-getSend3TimesByCache:", e);
                return false;
            }
        }
    }

    @Override
    public boolean checkMatchThirdSettleEvent(MatchSettleThirdEvent matchSettleThirdEvent, String linkedId, Long second) {
        log.info("linkId::{}::eventId:{} settleNum:{} checkMatchThirdSettleEvent start",linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum());
        //1.当前赛事是否切换到2.0如果不是则不生成数据
        if (!isSettle2(matchSettleThirdEvent.getStandardMatchId())) {
            return false;
        }
        //2.查询需要核对的标准比分ID 做关系绑定X
        List<MatchSettleEvent> standardEvents = matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(
                matchSettleThirdEvent.getStandardMatchId(), Arrays.asList(matchSettleThirdEvent.getSettleNum()));
        standardEvents = standardEvents.stream().filter(t->matchSettleThirdEvent.getPeriodId().equals(t.getPeriodId())&&matchSettleThirdEvent.getEventOrder().equals(t.getEventOrder())).collect(Collectors.toList());
        if (standardEvents.size() == 0) {
            log.info("linkId::{}::eventId:{} settleNum:{} 该事件变更没有搜索到需要核对的比分", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum());
            return false;
        }
        MatchSettleEvent matchSettleEvent = standardEvents.get(0);
        if (matchSettleEvent.getStatus() == 3) {
            log.info("linkId::{}::eventId:{} settleNum:{} 该事件已经结算,无需重新核对比分", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum());
            return false;
        }
        //如果比分已经被回滚一次 则弃用数据商比分
        if (matchSettleEvent.getSettleCount() != null && matchSettleEvent.getSettleCount() > 0) {
            return false;
        }
        //3 redis 加锁 锁 结算比分ID 先忽略 TODO
        //3.0 判断是否灰色区间更新状态
        if (matchSettleThirdEvent.getIsGrey() != null && matchSettleThirdEvent.getIsGrey() >= 1) {
            matchSettleEvent.setIsGrey(1);
            matchSettleEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEvent.setCurrentEventStatus(1);
            log.info("linkId::{}::eventId:{} settleNum:{} 该事件修改的比分设置为灰色区间", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum());
            matchSettleEventRepository.updateById(matchSettleEvent);
        } else if (matchSettleEvent.getIsGrey() != null && matchSettleEvent.getIsGrey() == 1) {
            matchSettleEvent.setIsGrey(0);
            matchSettleEvent.setModifyTime(System.currentTimeMillis());
            matchSettleEvent.setCurrentEventStatus(0);
            log.info("linkId::{}::eventId:{} settleNum:{} 灰色区间已经被取消了", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum());
            matchSettleEventRepository.updateById(matchSettleEvent);
        }
        //3.1 查询比分核对类是否存在
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndDataSourceCode(Arrays.asList(matchSettleEvent.getId()), matchSettleThirdEvent.getDataSourceCode());
        MatchSettleCheckInfo matchSettleCheckInfo = null;
        log.info("linkId::{}::eventId:{} settleNum:{} checkMatchThirdSettleScores :{}: 事件核对并发生成:{}", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum(), list);
        //3.2 已经存在则修改比分
        boolean createCheck = false;

        if (list.size() != 0) {
            if (list.size() > 1) {
                log.info("linkId::{}::eventId:{} settleNum:{} checkMatchThirdSettleScores :{}: 事件核对并发生成:{}", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum(), list.size());
            }
            matchSettleCheckInfo = list.get(0);
            if (!StringUtils.isAnyEmpty(matchSettleThirdEvent.getOperater()) && (matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
                matchSettleCheckInfo.setUserName(matchSettleThirdEvent.getOperater() + "(" + matchSettleThirdEvent.getDataSourceCode() +")");
            }
            //比分修正
            SettleCheckUtils.copyMatchSettleEvent(matchSettleThirdEvent, matchSettleCheckInfo);

            Long minPeriod = FootBallMatchSettleScoreUtils.get5MinPeriod(matchSettleThirdEvent.getPeriodId(), second);
            matchSettleCheckInfo.setFiveMinSection(minPeriod.toString());

//            if(matchSettleThirdEvent.getIsGrey()!=1&&(matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("fa_card")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card"))){
            if(matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("fa_card")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card")){
                //1.计算出角球15分钟区间
                //2.设置15分钟区间
                Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(matchSettleThirdEvent.getPeriodId(), second);
                if(period15!=null){
                    matchSettleCheckInfo.setFiveMinSection(period15.toString());
                }
            }

            matchSettleCheckInfo.setIsGrey(matchSettleThirdEvent.getIsGrey());
//            if (!matchSettleCheckInfo.getDataSourceCode().equals("F01")){
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
//            }


            matchSettleCheckInfo.setThirdSettleScoreEventId(matchSettleThirdEvent.getId());
            matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
            log.info("linkId::{}::eventId:{} settleNum:{} 比分修正 当前数据商比分核对数据:{}", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum(), matchSettleCheckInfo);
        } else {
            //3.3 不存在创建核对比分
            matchSettleCheckInfo = SettleCheckUtils.initMatchSettleEvent(matchSettleEvent, matchSettleThirdEvent);
            matchSettleCheckInfo.setIsGrey(matchSettleThirdEvent.getIsGrey());
            String period5 = SportPeriodConstant.FootballPeriod.get5MinCode(matchSettleThirdEvent.getPeriodId(), second);
            //5分钟灰色区间不做编辑区间
//            if (matchSettleThirdEvent.getIsGrey() != 2) {
//                matchSettleCheckInfo.setFiveMinSection(period5);
//            }
            matchSettleCheckInfo.setFiveMinSection(period5);
            if(matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("fa_card")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card")){
                matchSettleCheckInfo.setFiveMinSection(null);
            }
            //角球返回是1 则判断为15分钟灰色区间 todo
//            if(matchSettleThirdEvent.getIsGrey()!=1&&(matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("fa_card")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card"))){
            if(matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("fa_card")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card")){
                //1.计算出角球15分钟区间
                //2.设置15分钟区间
                Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(matchSettleThirdEvent.getPeriodId(), second);
                if(period15!=null){
                    matchSettleCheckInfo.setFiveMinSection(period15.toString());
                }
            }

            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
            matchSettleCheckInfo.setThirdSettleScoreEventId(matchSettleThirdEvent.getId());
            if (!StringUtils.isAnyEmpty(matchSettleThirdEvent.getOperater()) && (matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || matchSettleThirdEvent.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode()))){
                matchSettleCheckInfo.setUserName(matchSettleThirdEvent.getOperater() + "(" + matchSettleThirdEvent.getDataSourceCode() +")");
            }
            matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
            initDelaySettleEvent(matchSettleEvent,matchSettleCheckInfo);
            log.info("linkId::{}::eventId:{} settleNum:{} 插入当前数据商比分核对数据:{}", linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum(), matchSettleCheckInfo);
            createCheck = true;
            //BT 或者 1X的 继续结算
            if (matchSettleCheckInfo.getDataSourceCode().equals("BT") || matchSettleCheckInfo.getDataSourceCode().equals("1X")) {
                if (matchSettleEvent.getEventCode().equals("goal")) {
                    confirmDataSourceGoalSettleEvent(matchSettleCheckInfo, linkedId);
                }
            }
            //BT 或者 RB 角球 继续结算

            if (matchSettleCheckInfo.getDataSourceCode().equals("BT") || matchSettleCheckInfo.getDataSourceCode().equals("RB")
                    ||matchSettleCheckInfo.getDataSourceCode().equals("BG")||matchSettleCheckInfo.getDataSourceCode().equals("KO") || matchSettleCheckInfo.getDataSourceCode().equals("PD")
                    || matchSettleCheckInfo.getDataSourceCode().equals("PD2") || matchSettleCheckInfo.getDataSourceCode().equals("TS")|| matchSettleCheckInfo.getDataSourceCode().equals("F01")|| matchSettleCheckInfo.getDataSourceCode().equals("N01")|| matchSettleCheckInfo.getDataSourceCode().equals("BFZX")||matchSettleCheckInfo.getDataSourceCode().equals("LS")||matchSettleCheckInfo.getDataSourceCode().equals("SR")
                    ) {
                if (matchSettleEvent.getEventCode().equals("corner") || matchSettleEvent.getEventCode().equals("fa_card")||matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")) {
                    confirmDataSourceGoalSettleEvent(matchSettleCheckInfo, linkedId);
                }
            }

//            if (matchSettleCheckInfo.getDataSourceCode().equals("F01") ) {
//                if (matchSettleEvent.getEventCode().equals("fa_card")||matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")||matchSettleEvent.getEventCode().equals("corner")) {
//                    confirmDataSourceGoalSettleEvent(matchSettleCheckInfo);
//                }
//            }

        }

        //4.触发比分核对 建议异步吧
        log.info("linkId::{}::eventId:{} settleNum:{} checkMatchThirdSettleEvent end",linkedId, matchSettleThirdEvent.getThirdEventSourceId(), matchSettleThirdEvent.getSettleNum());
        //当前进球事件不用自动结算
//        checkCommonMatchSettleScoreEvent(matchSettleEvent,matchSettleCheckInfo,createCheck);

        return true;
    }
    @Override
    public Map<Long, Pair<Boolean, Boolean>> batchCheckCommonMatchSettleScoreEvent(List<Pair<Object, MatchSettleCheckInfo>> checkMessages, boolean createCheck,
                                                      String linkedId, MatchSettleTemplate downTemplate) {
        Map<Long, Pair<Boolean, Boolean>> res = new HashMap<>();
        if (CollectionUtils.isEmpty(checkMessages)) {
            log.warn("linkedId::{} batchCheckCommonMatchSettleScoreEvent checkMessages is empty, skip", linkedId);
            return res;
        }
        long start = System.currentTimeMillis();
        log.info("linkedId::{} size::{} batchCheckCommonMatchSettleScoreEvent 事件比分核对开始", linkedId, checkMessages.size());
        //0.查询标准赛事的数据商自动结算状态
        MatchSettleCheckInfo matchSettleCheckInfoFirst = checkMessages.get(0).getRight();
        Object matchSettleScoreEventFirst = checkMessages.get(0).getLeft();
        // 判断是次序还是阶段
        boolean isScoreFlag = true;
        if (matchSettleScoreEventFirst instanceof MatchSettleEvent) {
            isScoreFlag = false;
        }

        // 对已经结算的事件进行过滤和更新delaysettle
        List<Long> scoreEventIds = checkMessages.stream().map(t->{
            res.put(t.getRight().getSettleScoreEventId(), Pair.of(false, false));
            return t.getRight().getSettleScoreEventId();
        }).collect(Collectors.toList());
        Map<Long, String> scoreEventIdSettleNumMap = new HashMap<>();
        Map<Long, Boolean> settleScoreEventMap;
        if (isScoreFlag) {
            scoreEventIdSettleNumMap = checkMessages.stream().map(t->(MatchSettleScore)t.getLeft()).collect(Collectors.toMap(MatchSettleScore::getId, MatchSettleScore::getSettleNum, (v1, v2) ->v1));
            List<MatchSettleScore> settleScores = matchSettleScoreRepository.getByIds(scoreEventIds);
            settleScoreEventMap = settleScores.stream().collect(Collectors.toMap(MatchSettleScore::getId, t->SETTLED.equals(t.getStatus()), (v1, v2)->v1));
        } else {
            scoreEventIdSettleNumMap = checkMessages.stream().map(t->(MatchSettleEvent)t.getLeft()).collect(Collectors.toMap(MatchSettleEvent::getId, MatchSettleEvent::getSettleNum, (v1, v2) ->v1));
            List<MatchSettleEvent> settleEvents = matchSettleEventRepository.getByIds(scoreEventIds);
            settleScoreEventMap = settleEvents.stream().collect(Collectors.toMap(MatchSettleEvent::getId, t->SETTLED.equals(t.getStatus()), (v1, v2)->v1));
        }

        Map<Long, String> finalScoreEventIdSettleNumMap = scoreEventIdSettleNumMap;
        List<Long> alreadySettledIds = new ArrayList<>();
        Map<Long, Boolean> finalSettleScoreEventMap = settleScoreEventMap;
        checkMessages = checkMessages.stream().filter(t->{
            Boolean alreadySettled = finalSettleScoreEventMap.get(t.getRight().getSettleScoreEventId());
            alreadySettled = alreadySettled != null && alreadySettled;
            if(alreadySettled) {
                alreadySettledIds.add(t.getRight().getId());
                res.put(t.getRight().getSettleScoreEventId(), Pair.of(false, true));
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        matchDelaySettleInfoRepository.updateStatusByCheckInfoIds(alreadySettledIds, SETTLED);
        log.info("linkedId::{} size::{} batchCheckCommonMatchSettleScoreEvent 完成对已结算事件过滤", linkedId, checkMessages.size());
        if(CollectionUtils.isEmpty(checkMessages)) {
            log.info("linkedId::{} batchCheckCommonMatchSettleScoreEvent 所有事件都已经结算", linkedId);
            return res;
        }

        List<Long> fiveMinSettleIds = new ArrayList<>();
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(matchSettleCheckInfoFirst.getStandardMatchId());
        //5分钟编码 计算5分钟区间
        //5分钟按钮不打开不做5分钟区间结算
        if (matchSettleInfo.getFiveMinSwitch() == null || matchSettleInfo.getFiveMinSwitch() != 1) {
            checkMessages = checkMessages.stream().filter(t->{
                boolean isFiveMinSettleNums = ALL_5_SETTLE_NUMS.contains(finalScoreEventIdSettleNumMap.get(t.getRight().getSettleScoreEventId()));
                if(isFiveMinSettleNums) {
                    fiveMinSettleIds.add(t.getRight().getId());
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        }
        log.info("linkedId::{} size::{} batchCheckCommonMatchSettleScoreEvent 完成对5分钟事件过滤", linkedId, checkMessages.size());
        //需求2477,联赛对应的数据源结算为关闭状态，只显示赛果不参与结算
        List<String> eventCodes = checkMessages.stream().map(t->t.getRight().getEventCode()).distinct().collect(Collectors.toList());
        Map<String, Integer> switchStatus = matchSettleDataSourceSwitchService.getTournamentLevelStatuses(matchSettleCheckInfoFirst.getStandardMatchId(),matchSettleCheckInfoFirst.getDataSourceCode(),eventCodes);
        checkMessages = checkMessages.stream().filter(t -> {
            Integer levelDataSourceStatus = switchStatus.getOrDefault(t.getRight().getEventCode(), null);
            if (levelDataSourceStatus==null || levelDataSourceStatus.equals(Constant.OUTRIGHT_ZERO)){
                fiveMinSettleIds.add(t.getRight().getId());
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        matchDelaySettleInfoRepository.updateStatusByCheckInfoIds(fiveMinSettleIds, 2);
        log.info("linkedId::{} size::{} batchCheckCommonMatchSettleScoreEvent 完成对开关状态过滤", linkedId, checkMessages.size());

        if(CollectionUtils.isEmpty(checkMessages)) {
            log.info("linkedId::{} batchCheckCommonMatchSettleScoreEvent 所有事件都为5分钟区间", linkedId);
            return res;
        }

        //比分类型灰色区间不走数据商自动结算
        MatchSettleTemplate matchSettleTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(matchSettleCheckInfoFirst.getStandardMatchId(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        if(matchSettleTemplate==null){
            log.info("linkedId::{} batchCheckCommonMatchSettleScoreEvent matchSettleTemplate is null", linkedId);
            return res;
        }

        //篮球结算顺序拦截
        if(isScoreFlag && matchSettleInfo.getSportId().equals(2L)){
            StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(matchSettleCheckInfoFirst.getStandardMatchId());
            batchCheckBasketPeriodScoreOrder(checkMessages, matchSettleInfo, standardMatchInfo);
            if(CollectionUtils.isEmpty(checkMessages)) {
                log.info("linkedId::{} batchCheckCommonMatchSettleScoreEvent 篮球结算顺序拦截为空", linkedId);
                return res;
            }
        }

        // 获取所有相关checkInfoss
        List<Long> scoreEventId = checkMessages.stream().map(t->t.getRight().getSettleScoreEventId()).collect(Collectors.toList());
        List<MatchSettleCheckInfo> allMatchSettleCheckInfos = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserName(scoreEventId,matchSettleCheckInfoFirst.getStandardMatchId(),null);
        // bug 92601
        allMatchSettleCheckInfos = allMatchSettleCheckInfos.stream().filter(t->{
            if ("LS".equals(t.getDataSourceCode()) && (ALL_5_SETTLE_NUMS.contains(finalScoreEventIdSettleNumMap.get(t.getSettleScoreEventId()))
                    || ALL_15_SETTLE_NUMS.contains(finalScoreEventIdSettleNumMap.get(t.getSettleScoreEventId())))){
                return false;
            }
            if("N01".equals(t.getDataSourceCode()) || "N02".equals(t.getDataSourceCode()) || "N03".equals(t.getDataSourceCode())){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        Map<Long, List<MatchSettleCheckInfo>> allMatchSettleCheckInfoMap = allMatchSettleCheckInfos.stream().collect(Collectors.groupingBy(MatchSettleCheckInfo::getSettleScoreEventId));
        Map<Long, List<MatchSettleCheckInfo>> checkInfosForSettleMap = this.batchSearchSettleCheckInfoListByCheckInfos(checkMessages,matchSettleInfo,isScoreFlag, linkedId, allMatchSettleCheckInfos);
        Map<Long, List<MatchSettleCheckInfo>> checkInfosForSettleMapWithDelay = checkInfosForSettleMap;
        if (!matchSettleCheckInfoFirst.getDataSourceCode().equals("PA")){
            checkInfosForSettleMapWithDelay = matchSettleBatchCheckServiceHelper.batchMoveDelayCheckInfo(matchSettleCheckInfoFirst.getStandardMatchId(),checkInfosForSettleMap, linkedId);
        }
        List<Pair<Object, MatchSettleCheckInfo>> settledCheckMessages = new ArrayList<>();
        List<Pair<Object, MatchSettleCheckInfo>> nonPastCheckedMessages = new ArrayList<>();
        Map<Long, Map<String, List<MatchSettleCheckInfo>>> checkGroupMapWithCheckId = new HashMap<>();
        Map<Long, Map<String, List<MatchSettleCheckInfo>>> finalCheckGroupMapWithCheckId = new HashMap<>();
        Integer needCheckNumber = 1;
        for(Pair<Object, MatchSettleCheckInfo> checkMessage: checkMessages) {
            MatchSettleCheckInfo matchSettleCheckInfo = checkMessage.getRight();
            Object matchSettleScoreEvent = checkMessage.getLeft();
            log.info("linkedId::{} scoreEventId:{} thirdScoreEventId:{} batchCheckCommonMatchSettleScoreEvent 开始遍历处理", linkedId, matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getThirdSettleScoreEventId());
            List<MatchSettleCheckInfo> oldList = checkInfosForSettleMap.get(matchSettleCheckInfo.getSettleScoreEventId());
            if (oldList == null) {
                oldList = new ArrayList<>();
            }
            log.info("linkedId::{} scoreEventId:{} thirdScoreEventId:{}  LOG1::oldList::{}", linkedId, matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getThirdSettleScoreEventId(),oldList.size());
            List<MatchSettleCheckInfo> list = checkInfosForSettleMapWithDelay.get(matchSettleCheckInfo.getSettleScoreEventId());
            if (list == null) {
                list = new ArrayList<>();
            }
            log.info("linkedId::{} scoreEventId:{} thirdScoreEventId:{}  LOG2::list::{}", linkedId, matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getThirdSettleScoreEventId(),list.size());
            boolean settled = false;
            //用来计算当前需要结算的录入核对数据 TODO
            String checkKey ="";
            //2.核对比分分组 进球角球会计算 5分钟 和15分钟
            Map<String, List<MatchSettleCheckInfo>> checkGroupMap = new HashMap<>();
            Map<String, List<MatchSettleCheckInfo>> oldCheckGroupMap = new HashMap<>();
            if ((!isScoreFlag)&& (matchSettleCheckInfo.getEventCode().equals("goal")|| matchSettleCheckInfo.getEventCode().equals("corner")||matchSettleCheckInfo.getEventCode().equals("yellow_card")||matchSettleCheckInfo.getEventCode().equals("red_card")||matchSettleCheckInfo.getEventCode().equals("fa_card"))) {
                MatchSettleEvent matchSettleEvent = (MatchSettleEvent)matchSettleScoreEvent;
                if(matchSettleInfo.getFiveMinSwitch() != null && matchSettleInfo.getFiveMinSwitch() == 1) {
                    checkGroupMap = SettleCheckUtils.groupByFiveMinSettleCheck(list);
                    checkKey = SettleCheckUtils.countSettleEventFiveMinCompareKey(matchSettleCheckInfo);
                    oldCheckGroupMap = SettleCheckUtils.groupByFiveMinSettleCheck(oldList);
                } else if (matchSettleEvent.getEventType() != 3) {
                    //无需计算五分钟
                    checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
                    checkKey = SettleCheckUtils.countSettleCheckGroupKey(matchSettleCheckInfo);
                    oldCheckGroupMap = SettleCheckUtils.groupBySettleCheck(oldList);
                }
            } else {
                //无需计算五分钟
                checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
                checkKey = SettleCheckUtils.countSettleCheckGroupKey(matchSettleCheckInfo);
                oldCheckGroupMap = SettleCheckUtils.groupBySettleCheck(oldList);
            }
            if(matchSettleInfo.getSportId().equals(2L)){
                checkGroupMap = SettleCheckUtils.groupByBasketBallCheck(list);
                oldCheckGroupMap = SettleCheckUtils.groupByBasketBallCheck(oldList);
                checkKey = SettleCheckUtils.countSettleCheckGroupBasketballKey(matchSettleCheckInfo);
            }
            checkGroupMapWithCheckId.put(matchSettleCheckInfo.getId(), checkGroupMap);
            // 有核对通过flag
            boolean hasPassCheck = false;
            Map<String, List<MatchSettleCheckInfo>> finalCheckGroupMap = new HashMap<>();
            boolean isPaCurrentTrigger = "PA".equals(matchSettleCheckInfo.getDataSourceCode());

            // 判断当前阶段是否是5/15分钟阶段
            String currentSettleNum = finalScoreEventIdSettleNumMap.get(matchSettleCheckInfo.getSettleScoreEventId());
            boolean is5Or15MinPeriod = is5Or15MinPeriod(currentSettleNum);

            if (is5Or15MinPeriod) {
                log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::checkGroupMap size:{} oldCheckGroupMap:{}",
                        linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), checkGroupMap.size(), oldCheckGroupMap.size());
                
                // 5/15分钟阶段：先检查所有可利用的数据源是否都有checkinfo
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleCheckInfo.getStandardMatchId());
                if (standardMatchInfo == null) {
                    log.warn("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::无法获取赛事信息，跳过结算", 
                            linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId());
                    continue;
                }
                
                Integer tournamentLevel = dataSourceHeartbeatService.getTournamentLevel(matchSettleCheckInfo.getStandardMatchId());
                List<String> availableDataSources = getAvailableDataSources(
                        matchSettleCheckInfo.getStandardMatchId(), 
                        matchSettleCheckInfo.getEventCode(),
                        standardMatchInfo.getSportId(),
                        tournamentLevel,
                        linkedId,
                        isPaCurrentTrigger
                );
                
                // 获取当前已有的checkinfo数据源
                Set<String> existingDataSourceCodes = oldList.stream()
                        .map(MatchSettleCheckInfo::getDataSourceCode)
                        .collect(Collectors.toSet());
                
                // 检查是否所有可利用的数据源都有checkinfo
                Set<String> missingDataSources = new HashSet<>(availableDataSources);
                missingDataSources.removeAll(existingDataSourceCodes);
                
                // 如果有PA且PA与某个已存在的数据源一致，则不需要等待其他数据源
                boolean hasPaAndMatches = false;
                if (existingDataSourceCodes.contains("PA")) {
                    // 检查PA是否与某个已存在的非PA数据源一致
                    if (oldCheckGroupMap.size() > 0) {
                        // 查找PA所在的entry
                        for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
                            boolean hasPa = entry.getValue().stream().anyMatch(c -> "PA".equals(c.getDataSourceCode()));
                            if (hasPa) {
                                // 检查PA所在的entry中是否满足结算条件：
                                // 1. 如果有2个或更多PA，可以结算（多个PA互相验证）
                                // 2. 或者除了PA外还有其他数据源（非PA），也可以结算
                                long paCount = entry.getValue().stream()
                                        .filter(c -> "PA".equals(c.getDataSourceCode()))
                                        .count();
                                long totalDataSourceCount = entry.getValue().stream()
                                        .map(MatchSettleCheckInfo::getDataSourceCode)
                                        .distinct()
                                        .count();
                                if (paCount >= 2 || totalDataSourceCount > 1) {
                                    hasPaAndMatches = true;
                                    log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::PA所在的entry中有{}个PA，总数据源数{}，不需要等待其他数据源",
                                            linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), paCount, totalDataSourceCount);
                                    break;
                                }
                            }
                        }
                    }
                }
                
                // PA 触发且已有 PA checkinfo 时，不因缺其他源阻塞进入一致性判断（快通：PA+任一源同组且与输入一致即可结算）
                boolean allDataSourcesAvailable = missingDataSources.isEmpty() || hasPaAndMatches
                        || (isPaCurrentTrigger && existingDataSourceCodes.contains("PA"));
                if (!allDataSourcesAvailable) {
                    log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::等待所有可利用数据源的checkinfo下发。可利用数据源:{}，已有数据源:{}，缺失数据源:{}",
                            linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), 
                            availableDataSources, existingDataSourceCodes, missingDataSources);
                    // 不直接continue，让hasPassCheck保持为false，以便后续重新计算逻辑能正常执行
                    hasPassCheck = false;
                }
                
                // 只有当所有数据源都可用时，才进行一致性检查
                if (allDataSourcesAvailable) {
                    log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::所有可利用数据源{}都有checkinfo，开始检查一致性",
                            linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), availableDataSources);

                    boolean paFastPathSettled = false;
                    if (isPaCurrentTrigger) {
                        for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
                            boolean hasPa = entry.getValue().stream().anyMatch(c -> "PA".equals(c.getDataSourceCode()));
                            long distinctSources = entry.getValue().stream()
                                    .map(MatchSettleCheckInfo::getDataSourceCode).distinct().count();
                            if (hasPa && distinctSources >= 2 && entry.getKey().equals(checkKey)) {
                                hasPassCheck = true;
                                finalCheckGroupMap.put(entry.getKey(), entry.getValue());
                                paFastPathSettled = true;
                                log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::PA触发快通：PA与至少一源同组且与输入一致，直接结算",
                                        linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId());
                                break;
                            }
                        }
                    }
                    
                    if (!paFastPathSettled) {
                    // 检查单数据源结算开关：当只有一个可利用数据源且只有一个checkinfo时，需要检查单数据源结算开关
                    boolean singleDataSourceSettleSwitchEnabled = true;
                    if (availableDataSources.size() == 1 && oldList.size() == 1) {
                        String singleDataSourceCode = availableDataSources.get(0);
                        // PA是人工结算，不需要检查单数据源结算开关
                        if (!"PA".equals(singleDataSourceCode)) {
                            List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getModelBySportIdAndDataSource(
                                    standardMatchInfo.getSportId(), null, null);
                            MatchSettleDataSourceSwitch dataSourceSwitch = null;
                            if (switches != null && !switches.isEmpty()) {
                                dataSourceSwitch = switches.stream()
                                        .filter(s -> singleDataSourceCode.equals(s.getDataSourceCode()))
                                        .findFirst()
                                        .orElse(null);
                            }
                            
                            if (dataSourceSwitch == null || dataSourceSwitch.getSingleDataSourceSettle() == null 
                                    || dataSourceSwitch.getSingleDataSourceSettle() != 1) {
                                log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::单数据源{}的单数据源结算开关未打开，不能结算",
                                        linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), singleDataSourceCode);
                                // 不直接continue，让hasPassCheck保持为false，以便后续重新计算逻辑能正常执行
                                singleDataSourceSettleSwitchEnabled = false;
                                hasPassCheck = false;
                            } else {
                                log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::单数据源{}的单数据源结算开关已打开，可以结算",
                                        linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), singleDataSourceCode);
                            }
                        }
                    }
                    
                    // 5/15分钟阶段：检查所有可用数据源的checkinfo值是否全部相等，或支持审核员结算（PA）
                    // 如果单数据源结算开关未打开，跳过一致性检查，但继续执行后续逻辑
                    if (singleDataSourceSettleSwitchEnabled && oldCheckGroupMap.size() == 1 && checkGroupMap.size() == oldCheckGroupMap.size()) {
                        // 所有数据源值都相等
                        Map.Entry<String, List<MatchSettleCheckInfo>> entry = oldCheckGroupMap.entrySet().iterator().next();
                        if (entry.getValue().size() == 1 && "PA".equals(entry.getValue().get(0).getDataSourceCode())) {

                        } else {
                            // 判断当前通过的比分是否与输入的比分一致，不一致则不成功
                            boolean tag = entry.getKey().equals(checkKey);
                            log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::所有{}个数据源的值全部相等:{}::与输入一致:{}",
                                    linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), entry.getValue().size(), entry.getKey(), tag);
                            if (tag) {
                                hasPassCheck = true;
                                finalCheckGroupMap.put(entry.getKey(), entry.getValue());
                            }
                        }
                    } else if (singleDataSourceSettleSwitchEnabled && oldCheckGroupMap.size() > 1 && checkGroupMap.size() == oldCheckGroupMap.size()) {
                        // 数据源值不一致，需要检查审核员结算（PA）
                        // 查找PA所在的entry（checkinfo值）
                        String paCheckKey = null;
                        List<MatchSettleCheckInfo> paEntryValue = null;
                        for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
                            for (MatchSettleCheckInfo checkInfo : entry.getValue()) {
                                if ("PA".equals(checkInfo.getDataSourceCode())) {
                                    paCheckKey = entry.getKey();
                                    paEntryValue = entry.getValue();
                                    break;
                                }
                            }
                            if (paCheckKey != null) {
                                break;
                            }
                        }

                        if (paCheckKey != null && paEntryValue != null) {
                            // 检查PA所在的entry中是否满足结算条件：
                            // 1. 如果有2个或更多PA，可以结算（多个PA互相验证）
                            // 2. 或者除了PA外还有其他数据源（非PA），也可以结算
                            long paCount = paEntryValue.stream()
                                    .filter(c -> "PA".equals(c.getDataSourceCode()))
                                    .count();
                            long totalDataSourceCount = paEntryValue.stream()
                                    .map(MatchSettleCheckInfo::getDataSourceCode)
                                    .distinct()
                                    .count();

                            if (paCount >= 2 || totalDataSourceCount > 1) {
                                // PA与至少一个其他数据源一致（包括其他PA），检查是否与输入一致
                                boolean tag = paCheckKey.equals(checkKey);
                                log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::审核员结算（PA）所在的entry中有{}个PA，总数据源数{}:{}::与输入一致:{}",
                                        linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), paCount, totalDataSourceCount, paCheckKey, tag);
                                if (tag) {
                                    hasPassCheck = true;
                                    finalCheckGroupMap.put(paCheckKey, paEntryValue);
                                }
                            } else {
                                log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::审核员结算（PA）未与其他数据源一致，不通过结算",
                                        linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId());
                                // 不直接continue，让hasPassCheck保持为false，以便后续重新计算逻辑能正常执行
                                hasPassCheck = false;
                            }
                        } else {
                            log.info("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::数据源值不一致且无审核员结算（PA），不通过结算（共{}种不同值）",
                                    linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), oldCheckGroupMap.size());
                        }
                    } else {
                        log.warn("linkId::{}::5/15分钟阶段{}::scoreEventId:{}::不通过结算。checkGroupMap size:{} oldCheckGroupMap:{}",
                                linkedId, currentSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), checkGroupMap.size(), oldCheckGroupMap.size());
                    }
                    }
                }
            } else {
                // 非5/15分钟阶段：使用原来的权重>=100逻辑
                for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : checkGroupMap.entrySet()) {
                    //计算权重根据工具方法类
                    Integer dataSourceWeightSum =0;
                    if(matchSettleInfo.getSportId().equals(1L)){
                        dataSourceWeightSum = SettleTemplateWeightUtils.countFootballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
                    }else {
                        dataSourceWeightSum = SettleTemplateWeightUtils.countBasketballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
                    }
                    log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{}:Template:matchId:{},dataSourceWeightSum:{},matchSettleCheckInfo:{}",linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getStandardMatchId(), dataSourceWeightSum,matchSettleCheckInfo);
                    if(dataSourceWeightSum>=100){
                        //判断当前通过的比分是否与输入的比分一致，不一致则不成功
                        boolean tag = entry.getKey().equals(checkKey);
                        log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{}:Tag1matchSettleCheckInfo_id:{},matchSettleCheckInfo:{},Tag:{}",linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId(),matchSettleCheckInfo,tag);
                        if (tag){
                            hasPassCheck = true;
                            finalCheckGroupMap.put(entry.getKey(), entry.getValue());
                        } else {
                            hasPassCheck = false;
                        }
                    }
                }
            }
            for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
                //计算权重根据工具方法类
                Integer oldSum =0;
                if(matchSettleInfo.getSportId().equals(1L)){
                    oldSum = SettleTemplateWeightUtils.countFootballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
                }else {
                    oldSum = SettleTemplateWeightUtils.countBasketballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
                }
                log.info("linkedId::{} "+matchSettleCheckInfo.getStandardMatchId()+":delayLog01-score.id:{} ,check.id: {},oldSum:{}",linkedId, matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId(),oldSum);
                if(oldSum>=100 || (matchSettleInfo.getSportId().equals(1L) && hasPassCheck)){
                    String key = "delaySettle:"+matchSettleCheckInfo.getSettleScoreEventId();
                    Object old = redisService.get(key);
                    if (null==old){
                        log.info("linkedId::{} "+matchSettleCheckInfo.getStandardMatchId()+":delayLog02-score.id:{} ,check.id: {}",linkedId, matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
                        //延迟结算时间
                        DownSettleDto downSettleDto ;
                        if (downTemplate!=null&&!StringUtils.isAnyEmpty(downTemplate.getTemplateJson())){
                            List<DownSettleDto> dtos = SettleTemplateJsonUtils.tansferDownList(downTemplate.getTemplateJson());
                            if (!CollectionUtils.isEmpty(dtos)&&!matchSettleCheckInfo.getDataSourceCode().equals("PA")){
                                log.info("linkedId::{} "+matchSettleCheckInfo.getStandardMatchId()+":delayLog03-score.id:{} ,check.id: {}",linkedId, matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
                                downSettleDto = dtos.get(0);
                                Integer value = 0;
                                if (matchSettleCheckInfo.getEventCode().equals("goal")){
                                    value = downSettleDto.getGoal15Min();
                                }else  if (matchSettleCheckInfo.getEventCode().equals("corner")){
                                    value = downSettleDto.getCorner15Min();
                                } else if (matchSettleCheckInfo.getEventCode().equals("score_change")) {
                                    value = downSettleDto.getGoal();
                                } else {
                                    value = downSettleDto.getBooking15Min();
                                }
                                redisService.set(key,value,7*24*3600);
                                if (matchSettleCheckInfo.getCheckType()==1){
                                    log.info("linkedId::{} "+matchSettleCheckInfo.getStandardMatchId()+":delayLog04-score.id:{} ,check.id: {}",linkedId, matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
                                    matchSettleBatchCheckServiceHelper.pushStandardSettleScores(matchSettleCheckInfo.getStandardMatchId(),matchSettleCheckInfo.getEventCode());
                                }
                                if (matchSettleCheckInfo.getCheckType()==2){
                                    log.info("linkedId::{} "+matchSettleCheckInfo.getStandardMatchId()+":delayLog05-score.id:{} ,check.id: {}",linkedId, matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId());
                                    matchSettleBatchCheckServiceHelper.pushStandardSettleEvent(matchSettleCheckInfo.getStandardMatchId(),matchSettleCheckInfo.getEventCode());
                                }
                            }
                        }

                    }
                }
            }
            //如果是次序事件，然后不能通过结算则去掉5分钟/15分钟区间重新计算;结算的时候也去掉5分钟/15分钟次序事件的区间
            if (!hasPassCheck &&!isScoreFlag) {
//            if (matchSettleScoreEvent instanceof MatchSettleEvent && (!matchSettleCheckInfo.getDataSourceCode().equals("PA"))) {
                MatchSettleEvent matchSettleEvent = (MatchSettleEvent)matchSettleScoreEvent;
                if (matchSettleEvent.getEventType() != 3) {
                    oldCheckGroupMap = SettleCheckUtils.groupBySettleCheck(oldList);
                    checkGroupMap = SettleCheckUtils.groupBySettleCheck(list);
                    checkKey = SettleCheckUtils.countSettleCheckGroupKey(matchSettleCheckInfo);

                    // 判断当前阶段是否是5/15分钟阶段（重新计算时也需要判断）
                    String recalcSettleNum = finalScoreEventIdSettleNumMap.get(matchSettleCheckInfo.getSettleScoreEventId());
                    boolean recalcIs5Or15MinPeriod = is5Or15MinPeriod(recalcSettleNum);

                    if (recalcIs5Or15MinPeriod) {
                        // 5/15分钟阶段（重新计算）：先检查所有可利用的数据源是否都有checkinfo
                        StandardMatchInfo recalcStandardMatchInfo = standardMatchInfoService.getItem(matchSettleCheckInfo.getStandardMatchId());
                        if (recalcStandardMatchInfo == null) {
                            log.warn("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::无法获取赛事信息，跳过结算",
                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId());
                            continue;
                        }

                        Integer recalcTournamentLevel = dataSourceHeartbeatService.getTournamentLevel(matchSettleCheckInfo.getStandardMatchId());
                        List<String> recalcAvailableDataSources = getAvailableDataSources(
                                matchSettleCheckInfo.getStandardMatchId(),
                                matchSettleCheckInfo.getEventCode(),
                                recalcStandardMatchInfo.getSportId(),
                                recalcTournamentLevel,
                                linkedId,
                                isPaCurrentTrigger
                        );

                        // 获取当前已有的checkinfo数据源
                        Set<String> recalcExistingDataSourceCodes = oldList.stream()
                                .map(MatchSettleCheckInfo::getDataSourceCode)
                                .collect(Collectors.toSet());

                        // 检查是否所有可利用的数据源都有checkinfo
                        Set<String> recalcMissingDataSources = new HashSet<>(recalcAvailableDataSources);
                        recalcMissingDataSources.removeAll(recalcExistingDataSourceCodes);

                        // 如果有PA且PA与某个已存在的数据源一致，则不需要等待其他数据源
                        boolean recalcHasPaAndMatches = false;
                        if (recalcExistingDataSourceCodes.contains("PA")) {
                            // 检查PA是否与某个已存在的非PA数据源一致
                            if (oldCheckGroupMap.size() > 0) {
                                // 查找PA所在的entry
                                for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
                                    boolean hasPa = entry.getValue().stream().anyMatch(c -> "PA".equals(c.getDataSourceCode()));
                                    if (hasPa) {
                                        // 检查PA所在的entry中是否满足结算条件：
                                        // 1. 如果有2个或更多PA，可以结算（多个PA互相验证）
                                        // 2. 或者除了PA外还有其他数据源（非PA），也可以结算
                                        long paCount = entry.getValue().stream()
                                                .filter(c -> "PA".equals(c.getDataSourceCode()))
                                                .count();
                                        long totalDataSourceCount = entry.getValue().stream()
                                                .map(MatchSettleCheckInfo::getDataSourceCode)
                                                .distinct()
                                                .count();
                                        if (paCount >= 2 || totalDataSourceCount > 1) {
                                            recalcHasPaAndMatches = true;
                                            log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::PA所在的entry中有{}个PA，总数据源数{}，不需要等待其他数据源",
                                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), paCount, totalDataSourceCount);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        // 检查是否所有可利用的数据源都有checkinfo（重新计算）；PA 触发且已有 PA 时不阻塞
                        boolean recalcAllDataSourcesAvailable = recalcMissingDataSources.isEmpty() || recalcHasPaAndMatches
                                || (isPaCurrentTrigger && recalcExistingDataSourceCodes.contains("PA"));
                        if (!recalcAllDataSourcesAvailable) {
                            log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::等待所有可利用数据源的checkinfo下发。可利用数据源:{}，已有数据源:{}，缺失数据源:{}",
                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(),
                                    recalcAvailableDataSources, recalcExistingDataSourceCodes, recalcMissingDataSources);
                            // 不直接continue，让hasPassCheck保持为false，以便后续处理能正常执行
                            hasPassCheck = false;
                        }

                        // 只有当所有数据源都可用时，才进行一致性检查（重新计算）
                        if (recalcAllDataSourcesAvailable) {
                            log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::所有可利用数据源{}都有checkinfo，开始检查一致性",
                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), recalcAvailableDataSources);

                            boolean recalcPaFastPathSettled = false;
                            if (isPaCurrentTrigger) {
                                for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
                                    boolean hasPa = entry.getValue().stream().anyMatch(c -> "PA".equals(c.getDataSourceCode()));
                                    long distinctSources = entry.getValue().stream()
                                            .map(MatchSettleCheckInfo::getDataSourceCode).distinct().count();
                                    if (hasPa && distinctSources >= 2 && entry.getKey().equals(checkKey)) {
                                        hasPassCheck = true;
//                                    matchSettleCheckInfo.setFiveMinSection(null);
                                        finalCheckGroupMap.put(entry.getKey(), entry.getValue());
                                        recalcPaFastPathSettled = true;
                                        log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::PA触发快通：PA与至少一源同组且与输入一致，直接结算",
                                                linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId());
                                        break;
                                    }
                                }
                            }

                            if (!recalcPaFastPathSettled) {
                                // 检查单数据源结算开关：当只有一个可利用数据源且只有一个checkinfo时，需要检查单数据源结算开关
                                boolean recalcSingleDataSourceSettleSwitchEnabled = true;
                                if (recalcAvailableDataSources.size() == 1 && oldList.size() == 1) {
                                    String recalcSingleDataSourceCode = recalcAvailableDataSources.get(0);
                                    // PA是人工结算，不需要检查单数据源结算开关
                                    if (!"PA".equals(recalcSingleDataSourceCode)) {
                                        List<MatchSettleDataSourceSwitch> recalcSwitches = matchSettleDataSourceSwitchRepository.getModelBySportIdAndDataSource(
                                                recalcStandardMatchInfo.getSportId(), null, null);
                                        MatchSettleDataSourceSwitch recalcDataSourceSwitch = null;
                                        if (recalcSwitches != null && !recalcSwitches.isEmpty()) {
                                            recalcDataSourceSwitch = recalcSwitches.stream()
                                                    .filter(s -> recalcSingleDataSourceCode.equals(s.getDataSourceCode()))
                                                    .findFirst()
                                                    .orElse(null);
                                        }

                                        if (recalcDataSourceSwitch == null || recalcDataSourceSwitch.getSingleDataSourceSettle() == null
                                                || recalcDataSourceSwitch.getSingleDataSourceSettle() != 1) {
                                            log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::单数据源{}的单数据源结算开关未打开，不能结算",
                                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), recalcSingleDataSourceCode);
                                            // 不直接continue，让hasPassCheck保持为false，以便后续处理能正常执行
                                            recalcSingleDataSourceSettleSwitchEnabled = false;
                                            hasPassCheck = false;
                                        } else {
                                            log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::单数据源{}的单数据源结算开关已打开，可以结算",
                                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), recalcSingleDataSourceCode);
                                        }
                                    }
                                }

                                // 5/15分钟阶段（重新计算）：检查所有可用数据源的checkinfo值是否全部相等，或支持审核员结算（PA）
                                // 如果单数据源结算开关未打开，跳过一致性检查，但继续执行后续逻辑
                                if (recalcSingleDataSourceSettleSwitchEnabled) {
                                    log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::checkGroupMap size:{} oldCheckGroupMap:{}",
                                            linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), checkGroupMap.size(), oldCheckGroupMap.size());
                                }
                                if (recalcSingleDataSourceSettleSwitchEnabled && oldCheckGroupMap.size() == 1 && checkGroupMap.size() == oldCheckGroupMap.size()) {
                                    // 所有数据源值都相等
                                    Map.Entry<String, List<MatchSettleCheckInfo>> entry = oldCheckGroupMap.entrySet().iterator().next();
                                    if (entry.getValue().size() == 1 && "PA".equals(entry.getValue().get(0).getDataSourceCode())) {

                                    } else {
                                        // 判断当前通过的比分是否与输入的比分一致，不一致则不成功
                                        boolean tag = entry.getKey().equals(checkKey);
                                        log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::所有{}个数据源的值全部相等:{}::与输入一致:{}",
                                                linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), entry.getValue().size(), entry.getKey(), tag);
                                        if (tag) {
                                            hasPassCheck = true;
//                                        matchSettleCheckInfo.setFiveMinSection(null);
                                            finalCheckGroupMap.put(entry.getKey(), entry.getValue());
                                        }
                                    }

                                } else if (recalcSingleDataSourceSettleSwitchEnabled && oldCheckGroupMap.size() > 1 && checkGroupMap.size() == oldCheckGroupMap.size()) {
                                    // 数据源值不一致，需要检查审核员结算（PA）
                                    // 查找PA所在的entry（checkinfo值）
                                    String paCheckKey = null;
                                    List<MatchSettleCheckInfo> paEntryValue = null;
                                    for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : oldCheckGroupMap.entrySet()) {
                                        for (MatchSettleCheckInfo checkInfo : entry.getValue()) {
                                            if ("PA".equals(checkInfo.getDataSourceCode())) {
                                                paCheckKey = entry.getKey();
                                                paEntryValue = entry.getValue();
                                                break;
                                            }
                                        }
                                        if (paCheckKey != null) {
                                            break;
                                        }
                                    }

                                    if (paCheckKey != null && paEntryValue != null) {
                                        // 检查PA所在的entry中是否满足结算条件：
                                        // 1. 如果有2个或更多PA，可以结算（多个PA互相验证）
                                        // 2. 或者除了PA外还有其他数据源（非PA），也可以结算
                                        long paCount = paEntryValue.stream()
                                                .filter(c -> "PA".equals(c.getDataSourceCode()))
                                                .count();
                                        long totalDataSourceCount = paEntryValue.stream()
                                                .map(MatchSettleCheckInfo::getDataSourceCode)
                                                .distinct()
                                                .count();

                                        if (paCount >= 2 || totalDataSourceCount > 1) {
                                            // PA与至少一个其他数据源一致（包括其他PA），检查是否与输入一致
                                            boolean tag = paCheckKey.equals(checkKey);
                                            log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::审核员结算（PA）所在的entry中有{}个PA，总数据源数{}:{}::与输入一致:{}",
                                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), paCount, totalDataSourceCount, paCheckKey, tag);
                                            if (tag) {
                                                hasPassCheck = true;
//                                            matchSettleCheckInfo.setFiveMinSection(null);
                                                finalCheckGroupMap.put(paCheckKey, paEntryValue);
                                            }
                                        } else {
                                            log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::审核员结算（PA）未与其他数据源一致，不通过结算",
                                                    linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId());
                                            // 不直接continue，让hasPassCheck保持为false，以便后续处理能正常执行
                                            hasPassCheck = false;
                                        }
                                    } else {
                                        log.info("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::数据源值不一致且无审核员结算（PA），不通过结算（共{}种不同值）",
                                                linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), oldCheckGroupMap.size());
                                    }
                                } else {
                                    log.warn("linkId::{}::5/15分钟阶段{}（重新计算）::scoreEventId:{}::不通过结算。checkGroupMap size:{} oldCheckGroupMap:{}",
                                            linkedId, recalcSettleNum, matchSettleCheckInfo.getSettleScoreEventId(), checkGroupMap.size(), oldCheckGroupMap.size());
                                }
                            }
                        }
                    } else {
                        // 非5/15分钟阶段：使用原来的权重>=100逻辑
                        for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : checkGroupMap.entrySet()) {
//                    Integer sameScoreNumber = entry.getValue().size();
                            //计算权重根据工具方法类
                            Integer dataSourceWeightSum =0;
                            if(matchSettleInfo.getSportId().equals(1L)){
                                dataSourceWeightSum = SettleTemplateWeightUtils.countFootballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
                            }else {
                                dataSourceWeightSum = SettleTemplateWeightUtils.countBasketballWeightDataSourceCheck(matchSettleTemplate,entry.getValue());
                            }
                            log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{}:Template:matchId:{},dataSourceWeightSum:{},matchSettleCheckInfo:{}",linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getStandardMatchId(), dataSourceWeightSum,matchSettleCheckInfo);
                            if(dataSourceWeightSum>=100){
                                //判断当前通过的比分是否与输入的比分一致，不一致则不成功 TODO
                                boolean tag = entry.getKey().equals(checkKey);
                                log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{}:Tag2matchSettleCheckInfo_id:{},matchSettleCheckInfo:{},Tag:{}",linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(),matchSettleCheckInfo.getId(),matchSettleCheckInfo,tag);
                                if (tag){
                                    hasPassCheck = true;
//                                matchSettleCheckInfo.setFiveMinSection(null);
                                    finalCheckGroupMap.put(entry.getKey(), entry.getValue());
                                } else {
                                    hasPassCheck = false;
                                }
                            }
                        }
                    }
                }
            }
            finalCheckGroupMapWithCheckId.put(matchSettleCheckInfo.getId(), finalCheckGroupMap);

            //理论结算时间 //数据商数据根据比分创建时间定为结算时间
            Long eventTime = this.countEventTime(allMatchSettleCheckInfoMap, matchSettleScoreEvent, matchSettleCheckInfo);
            for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : finalCheckGroupMap.entrySet()) {
                Integer settle_t1 = entry.getValue().get(0).getT1();
                Integer settle_t2 = entry.getValue().get(0).getT2();
                if (settled) {
                    continue;
                }
                if (hasPassCheck) {
                    if (settle_t1 != null && settle_t2 != null) {
                        if (settle_t1.equals(matchSettleCheckInfo.getT1()) && settle_t2.equals(matchSettleCheckInfo.getT2())) {
                            //统计理论结算时间 无数据商 取当前结算时间
                            this.updateEventTime(eventTime, matchSettleScoreEvent);
                            settledCheckMessages.add(Pair.of(matchSettleScoreEvent, matchSettleCheckInfo));
                        } else {
                            log.error("linkedId::{} thirdScoreEventId::{}::scoreEventId:{}:已满足条件但未触发结算,比分类型:{}", linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getCheckType());
                        }
                    } else {
                        //统计理论结算时间 无数据商 取当前结算时间
                        this.updateEventTime(eventTime, matchSettleScoreEvent);
                        settledCheckMessages.add(Pair.of(matchSettleScoreEvent, matchSettleCheckInfo));
                    }
                    settled = true;
                    continue;
                }
            }
            if(!hasPassCheck){
                Object finalObject = matchSettleScoreEvent;
                if(isScoreFlag) {
                    MatchSettleScore  orignal = (MatchSettleScore)matchSettleScoreEvent;
                    MatchSettleScore newSettleScore = new MatchSettleScore();
                    BeanUtils.copyProperties(orignal, newSettleScore);
                    finalObject = newSettleScore;
                }
                nonPastCheckedMessages.add(Pair.of(finalObject, matchSettleCheckInfo));
            }
            log.info("linkedId::{} scoreEventId:{} thirdScoreEventId:{} batchCheckCommonMatchSettleScoreEvent 遍历结束", linkedId, matchSettleCheckInfo.getSettleScoreEventId(), matchSettleCheckInfo.getThirdSettleScoreEventId());
        }

        if(!CollectionUtils.isEmpty(settledCheckMessages) && isScoreFlag) {
            // 过滤掉之前阶段没结算的数据
            Map<String, MatchSettleScore> settleNumMap = checkMessages.stream().map(t->{
                MatchSettleScore  matchSettleScore = (MatchSettleScore)t.getLeft();
                MatchSettleCheckInfo  checkInfo = t.getRight();
                matchSettleScore.setT1(checkInfo.getT1());
                matchSettleScore.setT2(checkInfo.getT2());
                matchSettleScore.setGoWaterStatus(checkInfo.getGoWaterStatus());
                return matchSettleScore;
            }).collect(Collectors.toMap(MatchSettleScore::getSettleNum, t->t, (v1, v2) ->v1));
            log.info("linkedId::{} settleNumMap:{}", linkedId, settleNumMap);
            Iterator<Pair<Object, MatchSettleCheckInfo>> iterator = settledCheckMessages.iterator();
            while(iterator.hasNext()) {
                Pair<Object, MatchSettleCheckInfo> pair = iterator.next();
                MatchSettleScore matchSettleScore = (MatchSettleScore) pair.getLeft();
                MatchSettleCheckInfo matchSettleCheckInfo = pair.getRight();
                //新增校验
                //数据商阶段比分结算顺序拦截，如果之前的比分没有结算，则不能编辑当前的比分
                //2472L01只有阶段，不做拦截校验
                //3848 BFZX 没有次序不能校验
                if (!matchSettleCheckInfo.getDataSourceCode().equals("BFZX")&&matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE
                        && !this.isAllPeriodScoresBeforeSettled(matchSettleInfo, matchSettleScore, linkedId, settleNumMap, matchSettleCheckInfo.getDataSourceCode() )) {
                    log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{} 阶段比分之前有未结算阶段比分", linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
                    // 请确保上一个比分已结算。
                    iterator.remove();
                    continue;
                }
                if (!matchSettleInfoHelper.checkSettleScoreAndAutoSettleNonEvent(matchSettleInfo, matchSettleScore, matchSettleCheckInfo, linkedId, settleNumMap)) {
                    log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{} 检查结算比分和自动结算", linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(), matchSettleCheckInfo.getSettleScoreEventId());
                    iterator.remove();
                }
            }
        }

        //自动结算主逻辑
        if (!CollectionUtils.isEmpty(settledCheckMessages)) {
            this.batchAutoSettle(settledCheckMessages, linkedId);
            //结算成功的话 锁定赛事结算操作失败人员
            // bug 108593
//            Set<String> userLockList = new HashSet<>();
//            for(Pair<Object, MatchSettleCheckInfo> settledCheckMessage : settledCheckMessages) {
//                MatchSettleCheckInfo matchSettleCheckInfo = settledCheckMessage.getRight();
//                res.put(matchSettleCheckInfo.getSettleScoreEventId(), Pair.of(false, true));
//                Map<String, List<MatchSettleCheckInfo>> checkGroupMap = checkGroupMapWithCheckId.get(matchSettleCheckInfo.getId());
//                Map<String, List<MatchSettleCheckInfo>> finalCheckGroupMap = finalCheckGroupMapWithCheckId.get(matchSettleCheckInfo.getId());
//                for (Map.Entry<String, List<MatchSettleCheckInfo>> entry : checkGroupMap.entrySet()) {
//                    if (finalCheckGroupMap.containsKey(entry.getKey())) {
//                        continue;
//                    }
//                    for (MatchSettleCheckInfo settleCheckInfo : entry.getValue()) {
//                        if (StringUtils.isNotEmpty(settleCheckInfo.getUserName())) {
//                            if (settleCheckInfo.getUserName().equals(matchSettleCheckInfo.getUserName())) {
//                                continue;
//                            }
//                            if(dataSourceCodeManually.contains(settleCheckInfo.getDataSourceCode())) {
//                                userLockList.add(settleCheckInfo.getUserName());
//                            }
//                        }
//                    }
//                }
//            }
//            if (userLockList.size() != 0) {
//                log.info("linkedId::{} batchCheckCommonMatchSettleScoreEvent 锁定赛事结算操作失败人员", linkedId);
//                lockUserListByCheckPass(matchSettleInfo, matchSettleCheckInfoFirst.getStandardMatchId(), new ArrayList<>(userLockList));
//            }
        }

        if (!CollectionUtils.isEmpty(nonPastCheckedMessages)) {
            batchSendCheckMessage(nonPastCheckedMessages, true, linkedId);

            for(Pair<Object, MatchSettleCheckInfo> item : nonPastCheckedMessages) {
                MatchSettleCheckInfo matchSettleCheckInfo = item.getRight();
                List<MatchSettleCheckInfo> list = checkInfosForSettleMapWithDelay.get(item.getRight().getSettleScoreEventId());
                if (list.size() >= needCheckNumber) {
                    if (list.size()==1&&matchSettleCheckInfo.getDataSourceCode().equals("PA")){ //人工第一次编辑
                        log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{} 人工第一次编辑", linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId());
                        res.put(matchSettleCheckInfo.getSettleScoreEventId(), Pair.of(false, false));
                    }else {
                        log.info("linkedId::{} thirdScoreEventId::{}::scoreEventId:{} 非人工第一次编辑", linkedId, matchSettleCheckInfo.getThirdSettleScoreEventId(),matchSettleCheckInfo.getSettleScoreEventId());
                        res.put(matchSettleCheckInfo.getSettleScoreEventId(), Pair.of(true, false));
                    }
                }
            }
        }
        log.info("linkedId::{} batchCheckCommonMatchSettleScoreEvent 消耗{}ms事件比分核对结束", linkedId, System.currentTimeMillis()-start);
        return res;
    }

    private Map<Long, List<MatchSettleCheckInfo>> batchSearchSettleCheckInfoListByCheckInfos(List<Pair<Object, MatchSettleCheckInfo>> checkMessages, MatchSettleInfo matchSettleInfo,
                                                                                             boolean isScoreFlag, String linkedId,List<MatchSettleCheckInfo> allMatchSettleCheckInfos ) {
        log.info("linkedId::{} batchSearchSettleCheckInfoListByCheckInfos 批量获取checkInfos start", linkedId);
        MatchSettleCheckInfo matchSettleCheckInfoFirst = checkMessages.get(0).getRight();
        List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getMatchSettleDataSourceSwitchByRedis(matchSettleInfo.getSportId(), null);
        log.info("linkedId::{} batchSearchSettleCheckInfoListByCheckInfos 完成获取所有开关信息", linkedId);
        Map<String, List<String>> dataSourceCodeMap = new HashMap<>();
        dataSourceCodeMap.put("corner", new ArrayList<>());
        dataSourceCodeMap.put("goal", new ArrayList<>());
        dataSourceCodeMap.put("booking", new ArrayList<>());
        for (MatchSettleDataSourceSwitch dataSourceSwitch : switches) {
            if (dataSourceSwitch.getCorner() != null && dataSourceSwitch.getCorner() == 1) {
                List<String> corners = dataSourceCodeMap.get("corner");
                corners.add(dataSourceSwitch.getDataSourceCode());
                corners.add("PA");
                dataSourceCodeMap.put("corner", corners);
            }
            if (dataSourceSwitch.getGoal() != null && dataSourceSwitch.getGoal() == 1) {
                List<String> goals = dataSourceCodeMap.get("goal");
                goals.add(dataSourceSwitch.getDataSourceCode());
                goals.add("PA");
                dataSourceCodeMap.put("goal", goals);
            }
            if (dataSourceSwitch.getBooking() != null && dataSourceSwitch.getBooking() == 1) {
                List<String> bookings = dataSourceCodeMap.get("booking");
                bookings.add(dataSourceSwitch.getDataSourceCode());
                bookings.add("PA");
                dataSourceCodeMap.put("booking", bookings);
            }
        }
        log.info("linkedId::{} batchSearchSettleCheckInfoListByCheckInfos 完成PA数据源添加", linkedId);
        // 查询开售表
        List<Long> standardMatchIds = new ArrayList<>();
        standardMatchIds.add(matchSettleInfo.getStandardMatchId());
        List<StandardSportMarketSell> marketSells = standardSportMarketSellService.getItems(standardMatchIds);
        List<String> notMainDataSourceCodes = this.getNotMainEventThirdSources(matchSettleInfo,matchSettleCheckInfoFirst.getDataSourceCode(),marketSells);
        //如果赛事级数据商关闭，则只查主数据源 + PA
        if(matchSettleInfo.getIsAutoSettleDataSource()!=null&&matchSettleInfo.getIsAutoSettleDataSource()==0){
            dataSourceCodeMap.put("corner", notMainDataSourceCodes);
            dataSourceCodeMap.put("goal", notMainDataSourceCodes);
            dataSourceCodeMap.put("booking", notMainDataSourceCodes);
        }
        if(!matchSettleCheckInfoFirst.getDataSourceCode().equals("PA")){
            dataSourceCodeMap.get("corner").remove(matchSettleCheckInfoFirst.getDataSourceCode());
            dataSourceCodeMap.get("goal").remove(matchSettleCheckInfoFirst.getDataSourceCode());
            dataSourceCodeMap.get("booking").remove(matchSettleCheckInfoFirst.getDataSourceCode());
        }
        log.info("linkedId::{} batchSearchSettleCheckInfoListByCheckInfos 完成只查主数据源 + PA", linkedId);

        // 构建scoreEventId到settleNum的映射（用于判断5/15分钟阶段）
        Map<Long, String> scoreEventIdSettleNumMap = new HashMap<>();
        for (Pair<Object, MatchSettleCheckInfo> checkMessage : checkMessages) {
            if (isScoreFlag) {
                MatchSettleScore matchSettleScore = (MatchSettleScore) checkMessage.getLeft();
                scoreEventIdSettleNumMap.put(matchSettleScore.getId(), matchSettleScore.getSettleNum());
            }
        }

        // 获取所有相关checkInfoss（只获取已确认的）
        List<MatchSettleCheckInfo> matchSettleCheckInfos = allMatchSettleCheckInfos.stream().filter(t->t.getCheckStatus()==MatchSettleCheckConstant.CheckStatus.CONFIRM).collect(Collectors.toList());
        log.info("linkId::{} batchSearchSettleCheckInfoListByCheckInfos 过滤前（已确认状态）checkinfo数量:{}", linkedId, matchSettleCheckInfos.size());
        
        // 按scoreEventId分组，记录每个阶段的checkinfo
        Map<Long, List<MatchSettleCheckInfo>> beforeFilterMap = matchSettleCheckInfos.stream().collect(Collectors.groupingBy(MatchSettleCheckInfo::getSettleScoreEventId));
        for (Map.Entry<Long, List<MatchSettleCheckInfo>> entry : beforeFilterMap.entrySet()) {
            Set<String> dataSourceCodes = entry.getValue().stream().map(MatchSettleCheckInfo::getDataSourceCode).collect(Collectors.toSet());
            log.info("linkId::{} batchSearchSettleCheckInfoListByCheckInfos scoreEventId:{} 过滤前数据源:{}", linkedId, entry.getKey(), dataSourceCodes);
        }

        // 过滤维护状态和连接状态的数据源（只对5/15分钟阶段）；PA 触发批次时保留心跳断连/关的 checkInfo 参与比对
        boolean isPaTriggeredBatch = "PA".equals(matchSettleCheckInfoFirst.getDataSourceCode());
        matchSettleCheckInfos = filterCheckInfosByMaintenanceAndConnection(matchSettleCheckInfos, matchSettleInfo.getStandardMatchId(), scoreEventIdSettleNumMap, linkedId, isPaTriggeredBatch);
        log.info("linkId::{} batchSearchSettleCheckInfoListByCheckInfos 过滤后（维护/连接状态）checkinfo数量:{}", linkedId, matchSettleCheckInfos.size());

        Map<Long, List<MatchSettleCheckInfo>> matchSettleCheckInfoMap = matchSettleCheckInfos.stream().collect(Collectors.groupingBy(MatchSettleCheckInfo::getSettleScoreEventId));

        for(Pair<Object, MatchSettleCheckInfo> checkMessage: checkMessages) {
            log.info("linkedId::{} batchSearchSettleCheckInfoListByCheckInfos 单条处理数据信息checkMessage:{}", linkedId, checkMessage);
            MatchSettleCheckInfo matchSettleCheckInfo = checkMessage.getRight();
            boolean scoreIsGray =false;
            List<String> validDataSourceCodes;
            if (isScoreFlag) {
                MatchSettleScore matchSettleScore = (MatchSettleScore) checkMessage.getLeft();
                if((matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1)||(matchSettleCheckInfo.getIsGrey()!=null&&matchSettleCheckInfo.getIsGrey()==1)){
                    scoreIsGray=true;
                }
                //玩法级赛事自动结算
                if(scoreIsGray==false){
                    validDataSourceCodes = obtainValidDataSourceCodes(dataSourceCodeMap, matchSettleCheckInfo,
                            matchSettleScore.getSettleNum(), matchSettleInfo, notMainDataSourceCodes);

                }else {
                    if(matchSettleCheckInfo.getDataSourceCode().equals("PA")) {
                        validDataSourceCodes = Arrays.asList("PA");
                    } else {
                        validDataSourceCodes = new ArrayList<>();
                    }
                }
            } else {
                MatchSettleEvent matchSettleEvent = (MatchSettleEvent) checkMessage.getLeft();
                if((matchSettleCheckInfo.getDataSourceCode().equals("PA")&& matchSettleEvent.getIsGrey()!=null&&matchSettleEvent.getIsGrey()==1) ||
                        (matchSettleCheckInfo.getIsGrey()!=null&&(matchSettleCheckInfo.getIsGrey()==1 || matchSettleCheckInfo.getIsGrey()==2))){
                    scoreIsGray=true;
                }
                //玩法级赛事自动结算
                if(scoreIsGray==false){
                    validDataSourceCodes = obtainValidDataSourceCodes(dataSourceCodeMap, matchSettleCheckInfo,
                            matchSettleEvent.getSettleNum(), matchSettleInfo, notMainDataSourceCodes);

                }else {
                    if(matchSettleCheckInfo.getDataSourceCode().equals("PA")) {
                        validDataSourceCodes = Arrays.asList("PA");
                    } else {
                        validDataSourceCodes = new ArrayList<>();
                    }
                }
//                validDataSourceCodes = obtainValidDataSourceCodes(dataSourceCodeMap, matchSettleCheckInfo,
//                        matchSettleEvent.getSettleNum(), matchSettleInfo, notMainDataSourceCodes);
            }
            log.info("linkedId::{} dataSourceCodeMap:{} validDataSourceCodes:{}", linkedId, dataSourceCodeMap, validDataSourceCodes);
            // 过滤
            List<MatchSettleCheckInfo> tempRelatedCheckInfos = matchSettleCheckInfoMap.getOrDefault(matchSettleCheckInfo.getSettleScoreEventId(), Collections.emptyList());
            log.info("linkId::{} scoreEventId:{} 过滤前（validDataSourceCodes）checkinfo数量:{}，数据源:{}", 
                    linkedId, matchSettleCheckInfo.getSettleScoreEventId(), tempRelatedCheckInfos.size(),
                    tempRelatedCheckInfos.stream().map(MatchSettleCheckInfo::getDataSourceCode).collect(Collectors.toList()));
            tempRelatedCheckInfos = tempRelatedCheckInfos.stream().filter(t-> validDataSourceCodes.contains(t.getDataSourceCode())).collect(Collectors.toList());
            log.info("linkId::{} scoreEventId:{} 过滤后（validDataSourceCodes）checkinfo数量:{}，数据源:{}", 
                    linkedId, matchSettleCheckInfo.getSettleScoreEventId(), tempRelatedCheckInfos.size(),
                    tempRelatedCheckInfos.stream().map(MatchSettleCheckInfo::getDataSourceCode).collect(Collectors.toList()));
            matchSettleCheckInfoMap.put(matchSettleCheckInfo.getSettleScoreEventId(), tempRelatedCheckInfos);
        }
        log.info("linkedId::{} batchSearchSettleCheckInfoListByCheckInfos 批量获取checkInfos end", linkedId);
        return matchSettleCheckInfoMap;
    }

    private List<String> obtainValidDataSourceCodes(Map<String, List<String>> dataSourceCodeMap, MatchSettleCheckInfo matchSettleCheckInfo, String settleNum, MatchSettleInfo matchSettleInfo, List<String> notMainDataSourceCodes){
        List<String> dataSourceCodes;
        String eventCode = matchSettleCheckInfo.getEventCode();
        if(eventCode.equals("corner")) {
            dataSourceCodes = dataSourceCodeMap.get("corner");
        }else if(eventCode.equals("goal")||eventCode.equals("kick_off")||eventCode.equals("score_change")){
            dataSourceCodes = dataSourceCodeMap.get("goal");
        }else {
            dataSourceCodes = dataSourceCodeMap.get("booking");
        }
        boolean isEventAutoSettle = this.getDataSouceAutoSettle(matchSettleInfo,matchSettleCheckInfo, settleNum);
        //关闭数据商只查人工+主数据商
        if(!isEventAutoSettle){
            //关闭数据商自动结算 获取主事件源+PA
            dataSourceCodes = notMainDataSourceCodes;
        }
        if(!matchSettleCheckInfo.getDataSourceCode().equals("PA")){
            if(!isEventAutoSettle){
                dataSourceCodes = notMainDataSourceCodes;
                if(dataSourceCodes.contains(matchSettleCheckInfo.getDataSourceCode())){
                    dataSourceCodes.add(matchSettleCheckInfo.getDataSourceCode());
                }
            }else {
                dataSourceCodes.add(matchSettleCheckInfo.getDataSourceCode());
            }
        }
        return dataSourceCodes == null ? new ArrayList<>() : dataSourceCodes;
    }

    //事件自动结算审核查询
    private List<MatchSettleCheckInfo> searchEventSettleCheckByCheckInfo(   List<String> dataSourceCodes,boolean scoreIsGray, MatchSettleEvent matchSettleScore, MatchSettleCheckInfo matchSettleCheckInfo, MatchSettleInfo matchSettleInfo) {
        MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
        //玩法级赛事自动结算
        boolean isEventAutoSettle = this.getDataSouceAutoSettle(matchSettleInfo,matchSettleCheckInfo, matchSettleScore.getSettleNum());
        //关闭数据商只查人工+主数据商
        List<MatchSettleCheckInfo> list = new ArrayList<>();
        if(!isEventAutoSettle){
            //关闭数据商自动结算 获取主事件源+PA
            List<String> mainEventThirdSources = this.getNotMainEventThirdSources(matchSettleInfo,matchSettleCheckInfo.getDataSourceCode());
            list = matchSettleCheckInfoRepository.getModelByItems(matchSettleCheckInfo.getSettleScoreEventId(),
                    matchSettleCheckInfo.getStandardMatchId(),MatchSettleCheckConstant.CheckStatus.CONFIRM,mainEventThirdSources);
        } else {
            list = matchSettleCheckInfoRepository.getModelByItems(matchSettleCheckInfo.getSettleScoreEventId(),
                    matchSettleCheckInfo.getStandardMatchId(),MatchSettleCheckConstant.CheckStatus.CONFIRM,dataSourceCodes);
        }
        if(!matchSettleCheckInfo.getDataSourceCode().equals("PA")){
            if(!isEventAutoSettle){
                dataSourceCodes =this.getNotMainEventThirdSources(matchSettleInfo,matchSettleCheckInfo.getDataSourceCode());
                if(dataSourceCodes.contains(matchSettleCheckInfo.getDataSourceCode())){
                    list.add(matchSettleCheckInfo);
                }
            }else {
                list.add(matchSettleCheckInfo);
            }
        }
        return list;
    }

    private boolean getDataSouceAutoSettle(MatchSettleInfo matchSettleInfo, MatchSettleCheckInfo matchSettleCheckInfo, String settleNum) {
        //1.赛事级优先
        if(matchSettleInfo.getIsAutoSettleDataSource() == null || matchSettleInfo.getIsAutoSettleDataSource() == 0) {
            return false;
        }
        if(matchSettleInfo.getSportId() == 2 && (!basketball6Mns.contains(settleNum))){
            return true;
        }

        //2.赛事级打开则 玩法级优先
        if(matchSettleCheckInfo.getEventCode().equals("goal")||matchSettleCheckInfo.getEventCode().equals("kick_off")||matchSettleCheckInfo.getEventCode().equals("score_change")){
            Integer goalAutoSettle= matchSettleInfo.getGoalAutoSettleDataSource()==null?0:matchSettleInfo.getGoalAutoSettleDataSource();
            return goalAutoSettle==1?true:false;
        }else if(matchSettleCheckInfo.getEventCode().equals("corner")){
            Integer cornerAutoSettle= matchSettleInfo.getCornerAutoSettleDataSource()==null?0:matchSettleInfo.getCornerAutoSettleDataSource();
            return cornerAutoSettle==1?true:false;
        }else {
            Integer faAutoSettle= matchSettleInfo.getBookingAutoSettleDataSource()==null?0:matchSettleInfo.getBookingAutoSettleDataSource();
            return faAutoSettle==1?true:false;
        }
    }
    /**
     * 查询标准赛事的主事件源的数据商编码list
     */
    private List<String> getNotMainEventThirdSources(MatchSettleInfo matchSettleInfo,String sourceCode) {
        List<String> list = new ArrayList<>();
        list.add("PA");
        if (!matchSettleInfo.getSportId().equals(1l)){
            List<Long> standardMatchIds = new ArrayList<>();
            standardMatchIds.add(matchSettleInfo.getStandardMatchId());
            List<StandardSportMarketSell> sells = standardSportMarketSellService.getItems(standardMatchIds);
            if (sells.size() == 0) {
                return list;
            }
            String businessEvent = sells.get(0).getBusinessEvent();
            //如果是标准赛事的开售事件源的则进入 主事件生成逻辑
            list.add(businessEvent);
        }
        //新增条件 20230502 去除重复数据商编码--
        if(!sourceCode.equals("PA")){
            list.remove(sourceCode);
        }
        return list;
    }

    private List<String> getNotMainEventThirdSources(MatchSettleInfo matchSettleInfo,String sourceCode, List<StandardSportMarketSell> sells) {
        List<String> list = new ArrayList<>();
        list.add("PA");
        if (!matchSettleInfo.getSportId().equals(1l)){
            if (sells.size() == 0) {
                return list;
            }
            String businessEvent = sells.get(0).getBusinessEvent();
            //如果是标准赛事的开售事件源的则进入 主事件生成逻辑
            list.add(businessEvent);
        }
        //新增条件 20230502 去除重复数据商编码--
        if(!sourceCode.equals("PA")){
            list.remove(sourceCode);
        }
        return list;
    }

    private Long countEventTime(Map<Long, List<MatchSettleCheckInfo>> allMatchSettleCheckInfoMap, Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo) {
        Long eventTime = 0l;
        if (matchSettleScoreEvent instanceof MatchSettleScore) {
            MatchSettleScore settleScore = (MatchSettleScore) matchSettleScoreEvent;
            List<MatchSettleCheckInfo> checkInfoList = allMatchSettleCheckInfoMap.getOrDefault(settleScore.getId(),new ArrayList<>());
            eventTime = searchEventTimeByScores(checkInfoList);
        } else if (matchSettleScoreEvent instanceof MatchSettleEvent) {
            MatchSettleEvent matchSettleEvent = (MatchSettleEvent) matchSettleScoreEvent;
            List<MatchSettleCheckInfo> checkInfoList = allMatchSettleCheckInfoMap.getOrDefault(matchSettleEvent.getId(),new ArrayList<>());
            eventTime = searchEventTimeByEvent(matchSettleCheckInfo, checkInfoList);
        }
        if (eventTime == 0l) {
            eventTime = matchSettleCheckInfo.getModifyTime();
        }
        return eventTime;
    }


    private void updateEventTime(Long eventTime, Object matchSettleScoreEvent) {
        if (eventTime.equals(0l)) {
            eventTime = System.currentTimeMillis();
        }
        if (matchSettleScoreEvent instanceof MatchSettleScore) {
            MatchSettleScore matchSettleScore = (MatchSettleScore) matchSettleScoreEvent;
            matchSettleScore.setEventTime(eventTime);
        }
        if (matchSettleScoreEvent instanceof MatchSettleEvent) {
            MatchSettleEvent matchSettleEvent = (MatchSettleEvent) matchSettleScoreEvent;
            matchSettleEvent.setEventTime(eventTime);
        }
    }

    /**
     * 锁定赛事操作人员
     */
    @Override
    public boolean lockUserListByCheckPass(Long standardMatchId, List<String> userNameList) {
        MatchSettleInfoExample matchSettleInfoExample = new MatchSettleInfoExample();
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
        String limitArrayStr = matchSettleInfo.getLimitUserArray();
        JSONArray limitArray = new JSONArray();
        if (StringUtils.isNotEmpty(limitArrayStr)) {
            limitArray = JSONArray.parseArray(limitArrayStr);
        }
        //篮球用旧审核员
        if(matchSettleInfo.getSportId().equals(2l)){
            JSONArray jsonArray = JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray());
            JSONArray newArray = new JSONArray();
            for (Object o : jsonArray) {
                if (o == null) {
                    continue;
                }
                if (!userNameList.contains(o.toString())) {
                    newArray.add(o.toString());
                }
            }
            matchSettleInfo.setAuditorActiveArray(newArray.toJSONString());
        }else if(matchSettleInfo.getSportId().equals(1L)){
            //足球尝试新 审核员
            try{
                AuditorFootBallJsonVo auditorFootBallJsonVo = JSONObject.parseObject(matchSettleInfo.getAuditorActiveArray(), AuditorFootBallJsonVo.class);
                List<String> cornerList =new ArrayList<>();
                List<String> goalList =new ArrayList<>();
                List<String> facardList =new ArrayList<>();
                for (String s : auditorFootBallJsonVo.getCornerAuditorList()) {
                    if (s == null) {
                        continue;
                    }
                    if (!userNameList.contains(s)) {
                        cornerList.add(s);
                    }
                }
                for (String s : auditorFootBallJsonVo.getGoalAuditorList()) {
                    if (s == null) {
                        continue;
                    }
                    if (!userNameList.contains(s)) {
                        goalList.add(s);
                    }
                }
                for (String s : auditorFootBallJsonVo.getFacardAuditorList()) {
                    if (s == null) {
                        continue;
                    }
                    if (!userNameList.contains(s)) {
                        facardList.add(s);
                    }
                }
                auditorFootBallJsonVo.setCornerAuditorList(cornerList);
                auditorFootBallJsonVo.setFacardAuditorList(facardList);
                auditorFootBallJsonVo.setGoalAuditorList(goalList);
                matchSettleInfo.setAuditorActiveArray(JSONObject.toJSONString(auditorFootBallJsonVo));
            }catch (Exception e){
                //报错用旧审核员
                JSONArray jsonArray = JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray());
                JSONArray newArray = new JSONArray();
                for (Object o : jsonArray) {
                    if (o == null) {
                        continue;
                    }
                    if (!userNameList.contains(o.toString())) {
                        newArray.add(o.toString());
                    }
                }
                matchSettleInfo.setAuditorActiveArray(newArray.toJSONString());
            }
        }

        limitArray.addAll(userNameList);
        matchSettleInfo.setLimitUserArray(limitArray.toJSONString());
        matchSettleInfo.setModifyTime(System.currentTimeMillis());
        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
        //赛事结算2.0审核失败人员预警
        matchSettleBatchCheckServiceHelper.sendMango(matchSettleInfo.getSportId(),standardMatchId, userNameList);
        return false;
    }

    private boolean lockUserListByCheckPass(MatchSettleInfo matchSettleInfo, Long standardMatchId, List<String> userNameList) {
        String limitArrayStr = matchSettleInfo.getLimitUserArray();
        JSONArray limitArray = new JSONArray();
        if (StringUtils.isNotEmpty(limitArrayStr)) {
            limitArray = JSONArray.parseArray(limitArrayStr);
        }
        //篮球用旧审核员
        if(matchSettleInfo.getSportId().equals(2l)){
            JSONArray jsonArray = JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray());
            JSONArray newArray = new JSONArray();
            for (Object o : jsonArray) {
                if (o == null) {
                    continue;
                }
                if (!userNameList.contains(o.toString())) {
                    newArray.add(o.toString());
                }
            }
            matchSettleInfo.setAuditorActiveArray(newArray.toJSONString());
        }else if(matchSettleInfo.getSportId().equals(1L)){
            //足球尝试新 审核员
            try{
                AuditorFootBallJsonVo auditorFootBallJsonVo = JSONObject.parseObject(matchSettleInfo.getAuditorActiveArray(), AuditorFootBallJsonVo.class);
                List<String> cornerList =new ArrayList<>();
                List<String> goalList =new ArrayList<>();
                List<String> facardList =new ArrayList<>();
                for (String s : auditorFootBallJsonVo.getCornerAuditorList()) {
                    if (s == null) {
                        continue;
                    }
                    if (!userNameList.contains(s)) {
                        cornerList.add(s);
                    }
                }
                for (String s : auditorFootBallJsonVo.getGoalAuditorList()) {
                    if (s == null) {
                        continue;
                    }
                    if (!userNameList.contains(s)) {
                        goalList.add(s);
                    }
                }
                for (String s : auditorFootBallJsonVo.getFacardAuditorList()) {
                    if (s == null) {
                        continue;
                    }
                    if (!userNameList.contains(s)) {
                        facardList.add(s);
                    }
                }
                auditorFootBallJsonVo.setCornerAuditorList(cornerList);
                auditorFootBallJsonVo.setFacardAuditorList(facardList);
                auditorFootBallJsonVo.setGoalAuditorList(goalList);
                matchSettleInfo.setAuditorActiveArray(JSONObject.toJSONString(auditorFootBallJsonVo));
            }catch (Exception e){
                //报错用旧审核员
                JSONArray jsonArray = JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray());
                JSONArray newArray = new JSONArray();
                for (Object o : jsonArray) {
                    if (o == null) {
                        continue;
                    }
                    if (!userNameList.contains(o.toString())) {
                        newArray.add(o.toString());
                    }
                }
                matchSettleInfo.setAuditorActiveArray(newArray.toJSONString());
            }
        }

        limitArray.addAll(userNameList);
        matchSettleInfo.setLimitUserArray(limitArray.toJSONString());
        matchSettleInfo.setModifyTime(System.currentTimeMillis());
        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
        //赛事结算2.0审核失败人员预警
        matchSettleBatchCheckServiceHelper.sendMango(matchSettleInfo.getSportId(),standardMatchId, userNameList);
        return false;
    }
    @Override
    public void batchSendCheckMessage(List<Pair<Object, MatchSettleCheckInfo>> checkMessages, boolean createCheck, String linkedId) {
        log.info("linkId::{} batchSendCheckMessage start", linkedId);
        Integer checkNumber = 0;
        List<MatchSettleScore> updateScores = new ArrayList<>();
        List<MatchSettleEvent> updateEvents = new ArrayList<>();
        List<MatchSettleCheckInfo> updateCheckInfos = new ArrayList<>();
        List<MatchListSettleDto> matchListSettleDtos = new ArrayList<>();
        Long standardMatchId = checkMessages.get(0).getRight().getStandardMatchId();
        List<String> userNames = new ArrayList<>();
        List<Long> settleScoreEventIds = new ArrayList<>();

        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
        for (Pair<Object, MatchSettleCheckInfo> checkMessage : checkMessages) {
            Object matchSettleScoreEvent  = checkMessage.getLeft();
            MatchSettleCheckInfo matchSettleCheckInfo  = checkMessage.getRight();
            if (matchSettleScoreEvent instanceof MatchSettleScore) {
                MatchSettleScore score = (MatchSettleScore) matchSettleScoreEvent;
                checkNumber = score.getCheckNumber();
            }
            if (matchSettleScoreEvent instanceof MatchSettleEvent) {
                MatchSettleEvent event = (MatchSettleEvent) matchSettleScoreEvent;
                checkNumber = event.getCheckNumber();
            }
            boolean needSendCheck = false;
            //1.计算当前需要核对的次序
            if (matchSettleCheckInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
                //1.1数据商的无需变更次序或者标记为1
                if (checkNumber == null || checkNumber == 0) {
                    checkNumber = 1;
                }
                //1.2判断是否已经创建如果已经创建1则无需推送
                if (createCheck) {
                    needSendCheck = true;
                }
            } else {
                //2 人工编辑的需要推送到下一个 次序+1
                needSendCheck = true;
                if (checkNumber == null || checkNumber == 0) {
                    checkNumber = 1;
                } else {
                    //如果当前操作人是操盘手，则无需更改次序
                    String str = matchSettleInfo.getAuditorJson();
                    if(str.contains(matchSettleCheckInfo.getUserName())){
                        checkNumber++;
                    }
                }
            }
            if (needSendCheck) {
                log.info("eventId::{}:: sendCheckMessage 更新核对比分次序", matchSettleCheckInfo.getThirdSettleScoreEventId());
                //3.更新核对比分次序
                if (matchSettleScoreEvent instanceof MatchSettleScore) {
                    MatchSettleScore score = (MatchSettleScore) matchSettleScoreEvent;
                    if(checkNumber>=score.getCheckNumber()){
                        score.setCheckNumber(checkNumber);
                    }
                    updateScores.add(score);
                }
                if (matchSettleScoreEvent instanceof MatchSettleEvent) {
                    MatchSettleEvent event = (MatchSettleEvent) matchSettleScoreEvent;
                    if(checkNumber>=event.getCheckNumber()){
                        event.setCheckNumber(checkNumber);
                    }
                    updateEvents.add(event);
                }

                //根据 checkNumber 获得当前人员
                String checkUserName = getNextAuthorName(matchSettleInfo, matchSettleCheckInfo, checkNumber);
                //生成新的核对记录，方便前端动态查询
                if (StringUtils.isNotEmpty(checkUserName)) {
                    MatchSettleCheckInfo nextCheckInfo = new MatchSettleCheckInfo();
                    SettleCheckUtils.copyProperties(matchSettleCheckInfo, nextCheckInfo);
                    nextCheckInfo.setUserName(checkUserName);
                    nextCheckInfo.setCheckNumber(checkNumber);
                    nextCheckInfo.setCheckDataType(MatchSettleCheckConstant.CheckDataType.USER_EDIT);
                    //人工核对需要编码为PA
                    nextCheckInfo.setDataSourceCode("PA");
                    //1.判断是否存在  一个人只能录入一次比分
                    if (matchSettleScoreEvent instanceof MatchSettleScore) {
                        MatchSettleScore score = (MatchSettleScore) matchSettleScoreEvent;
                        nextCheckInfo.setEventCode(score.getEventCode());
                    }
                    if (matchSettleScoreEvent instanceof MatchSettleEvent) {
                        MatchSettleEvent event = (MatchSettleEvent) matchSettleScoreEvent;
                        nextCheckInfo.setEventCode(event.getEventCode());
                    }
                    //2.不存在则插入
                    updateCheckInfos.add(nextCheckInfo);
                    userNames.add(nextCheckInfo.getUserName());
                    settleScoreEventIds.add(nextCheckInfo.getSettleScoreEventId());
                }
                //4.推送WS，无需推送审核员名字，前端收到ws推送后，会主动调用接口查询最新状态
                //5.判断当前这个check是否已经发送，如果已经发送则无需再推送
                if (this.checkIfNotSend(matchSettleCheckInfo)) {
                    MatchListSettleDto matchListSettleDto = new MatchListSettleDto(matchSettleCheckInfo.getStandardMatchId(), matchSettleCheckInfo.getEventCode(),
                            matchSettleCheckInfo.getId(), matchSettleCheckInfo.getSettleScoreEventId(), 1);
                    matchListSettleDtos.add(matchListSettleDto);
                }
            }
        }
        if(!CollectionUtils.isEmpty(updateScores)) {
            matchSettleScoreRepository.saveOrUpdateBatch(updateScores);
        }
        if(!CollectionUtils.isEmpty(updateEvents)) {
            matchSettleEventRepository.saveOrUpdateBatch(updateEvents);
        }
        if(!CollectionUtils.isEmpty(updateCheckInfos)) {
            List<MatchSettleCheckInfo> checkInfoList = matchSettleCheckInfoRepository.getModelBySettleScoreEventIdsAndMatchIdAndUserNames(settleScoreEventIds,standardMatchId,userNames);
            Map<String, Boolean> checkInfoMap = checkInfoList.stream().collect(Collectors.toMap(t->t.getUserName()+"-"+t.getSettleScoreEventId(), t->Boolean.TRUE, (v1, v2)->v1));
            Map<String, MatchSettleCheckInfo> updateCheckInfoMap= updateCheckInfos.stream().collect(Collectors.toMap(t->t.getUserName()+"-"+t.getSettleScoreEventId(), Function.identity(), (v1, v2)->v1));
            List<MatchSettleCheckInfo> filteredUpdateCheckInfos = updateCheckInfoMap.values().stream().filter(t->!checkInfoMap.containsKey(t.getUserName()+"-"+t.getSettleScoreEventId())).collect(Collectors.toList());
            matchSettleCheckInfoRepository.saveOrUpdateBatch(filteredUpdateCheckInfos);
        }
        if(!CollectionUtils.isEmpty(matchListSettleDtos)) {
            for (MatchListSettleDto matchListSettleDto : matchListSettleDtos){
                wsPushService.pushSettleMatchList(matchListSettleDto);
            }
        }
        log.info("linkId::{} batchSendCheckMessage end", linkedId);
    }

    public boolean checkIfNotSend(MatchSettleCheckInfo matchSettleCheckInfo) {
        String key = "SETTLE_SCORES_CHECK_INFO_SEND:" + matchSettleCheckInfo.getId();
        Object o = redisService.get(key);
        if (o == null) {
            redisService.set(key, key, 9000);
            return true;
        }
        return false;
    }

    private void batchAutoSettle(List<Pair<Object, MatchSettleCheckInfo>> settledCheckMessages, String linkedId) {
        log.info("linkedId::{} batchAutoSettle 进入自动结算流程", linkedId);
        MatchSettleCheckInfo checkInfo = settledCheckMessages.get(0).getRight();
        Object matchSettleScoreEventInfo = settledCheckMessages.get(0).getLeft();
        if (matchSettleScoreEventInfo instanceof MatchSettleScore) {
            //比分结算
            List<Pair<MatchSettleScore, MatchSettleCheckInfo>> settleCheckInfoList = settledCheckMessages.stream().map(t->Pair.of((MatchSettleScore)t.getLeft(), t.getRight())).collect(Collectors.toList());
            this.batchSettleMatchScore(settleCheckInfoList, linkedId);
        } else if (matchSettleScoreEventInfo instanceof MatchSettleEvent) {
            //事件结算
            for(Pair<Object, MatchSettleCheckInfo> message : settledCheckMessages) {
                this.settleMatchSettleEvent((MatchSettleEvent) message.getLeft(), message.getRight());
            }
        } else {
            log.error("linkedId::{} batchAutoSettle 传入类型错误", linkedId);
        }
        matchSettleInfoHelper.updateMatchCurrentEventStatus(checkInfo.getStandardMatchId());
        log.info("linkedId::{}::batchAutoSettle end", linkedId);
    }

    /**
     * 获取下一个审核员
     */
    public String getNextAuthorName(MatchSettleCheckInfo matchSettleCheckInfo, Integer checkNumber) {
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(matchSettleCheckInfo.getStandardMatchId());
        if (matchSettleInfo == null) {
            log.error("standardMatchId:{},找不到赛事结算记录", matchSettleCheckInfo.getStandardMatchId());
            return null;
        }
        String arrayStr = matchSettleInfo.getAuditorActiveArray();
        if (StringUtils.isEmpty(arrayStr)) {
            return null;
        }
        //兼容 数组或者 对象类型
        if (matchSettleInfo.getSportId().equals(2l)) {
            JSONArray array = JSONArray.parseArray(arrayStr);
            if (array == null) {
                return null;
            }
            if (checkNumber > array.size()) {
                return null;
            }
            //如果需要第六个人审核，默认推到第五个人
            if (checkNumber > 5) {
                return array.get(4).toString();
            }
            return array.get(checkNumber - 1) != null ? array.get(checkNumber - 1).toString() : null;
        } else {
            //足球改为对象类型
            try {
                AuditorFootBallJsonVo auditorFootBallJsonVo = JSONObject.parseObject(matchSettleInfo.getAuditorActiveArray(), AuditorFootBallJsonVo.class);
                List<String> array;
                if (matchSettleCheckInfo.getEventCode().equals("goal") || matchSettleCheckInfo.getEventCode().equals("kick_off")) {
                    array = auditorFootBallJsonVo.getGoalAuditorList();
                } else if (matchSettleCheckInfo.getEventCode().equals("corner")) {
                    array = auditorFootBallJsonVo.getCornerAuditorList();
                } else {
                    array = auditorFootBallJsonVo.getFacardAuditorList();
                }
                if (checkNumber > array.size()) {
                    return null;
                }
                if (checkNumber > 5) {
                    return array.get(4).toString();
                }
                return array.get(checkNumber - 1) != null ? array.get(checkNumber - 1).toString() : null;
            } catch (Exception e) {
                log.error("MatchSettleCheckServiceImpl-getNextAuthorName", e);
            }
        }
        return null;
    }

    private String getNextAuthorName(MatchSettleInfo matchSettleInfo, MatchSettleCheckInfo matchSettleCheckInfo, Integer checkNumber) {
        String arrayStr = matchSettleInfo.getAuditorActiveArray();
        if (StringUtils.isEmpty(arrayStr)) {
            return null;
        }
        //兼容 数组或者 对象类型
        if (matchSettleInfo.getSportId().equals(2l)) {
            JSONArray array = JSONArray.parseArray(arrayStr);
            if (array == null) {
                return null;
            }
            if (checkNumber > array.size()) {
                return null;
            }
            //如果需要第六个人审核，默认推到第五个人
            if (checkNumber > 5) {
                return array.get(4).toString();
            }
            return array.get(checkNumber - 1) != null ? array.get(checkNumber - 1).toString() : null;
        } else {
            //足球改为对象类型
            try {
                AuditorFootBallJsonVo auditorFootBallJsonVo = JSONObject.parseObject(matchSettleInfo.getAuditorActiveArray(), AuditorFootBallJsonVo.class);
                List<String> array;
                if (matchSettleCheckInfo.getEventCode().equals("goal") || matchSettleCheckInfo.getEventCode().equals("kick_off")) {
                    array = auditorFootBallJsonVo.getGoalAuditorList();
                } else if (matchSettleCheckInfo.getEventCode().equals("corner")) {
                    array = auditorFootBallJsonVo.getCornerAuditorList();
                } else {
                    array = auditorFootBallJsonVo.getFacardAuditorList();
                }
                if (checkNumber > array.size()) {
                    return null;
                }
                if (checkNumber > 5) {
                    return array.get(4).toString();
                }
                return array.get(checkNumber - 1) != null ? array.get(checkNumber - 1).toString() : null;
            } catch (Exception e) {
                log.error("MatchSettleCheckServiceImpl-getNextAuthorName", e);
            }
        }
        return null;
    }

    private boolean isAllPeriodScoresBeforeSettled(MatchSettleInfo matchSettleInfo, MatchSettleScore matchSettleScore, String linkedId, Map<String, MatchSettleScore> settleNumMap, String dataSourceCode) {
        boolean flag = true;
        //查询赛事结算表 看是否关闭顺序结算控制 为开  (null or 0)
        Long standardMatchId = matchSettleScore.getStandardMatchId();
        if (standardMatchId == null || standardMatchId == 0L) {
            flag = false;
        }
        if (matchSettleInfo == null) {
            flag = false;
        }
        if (dataSourceCode.equals("LS") || matchSettleInfo.getSettleOrderClosed() != null &&
                matchSettleInfo.getSettleOrderClosed() != 0) {
            flag = false;
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
        log.info("linkedId::{} scoreEventId::{} isAllPeriodScoresBeforeSettled settleNumsBefore:{}",
                linkedId, matchSettleScore.getId(), settleNumsBefore);
        for (Map.Entry<String, MatchSettleScore> item : settleNumMap.entrySet()) {
            settleNumsBefore.remove(item.getKey());
        }
        log.info("linkedId::{} scoreEventId::{} isAllPeriodScoresBeforeSettled 去除并行settleNum后settleNumsBefore:{}", linkedId, matchSettleScore.getId(), settleNumsBefore);
        if (settleNumsBefore.size() == 0) {
            return true;
        }
        MatchSettleScoreExample example = new MatchSettleScoreExample();
        //查询当前编辑的比分之前未结算的比分
        example.createCriteria().andSettleNumIn(settleNumsBefore).andStandardMatchIdEqualTo(matchSettleScore.getStandardMatchId()).
                andStatusNotEqualTo(SETTLED);
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndNotStatus(settleNumsBefore,matchSettleScore.getStandardMatchId(),SETTLED);
        log.info("linkedId::{} scoreEventId::{} isAllPeriodScoresBeforeSettled 未结算数量:{}", linkedId, matchSettleScore.getId(), list.size());
        if (list.size() != 0) {
            return false;
        }
        return true;
    }

    @Override
    public void changeHomeAway(List<MatchEventInfo> list) {
        //只有足球才做主客队相反
        try {
            if (list.size() != 0 && list.get(0).getSportId().equals(1L)) {
                //只有UOF比分才需要主客队互换  事件比分不需要
                ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(list.get(0).getThirdMatchId());
                log.info("after list thirdMatchInfo:{} ",thirdMatchInfo);
                if (thirdMatchInfo != null && thirdMatchInfo.getHomeAwayOpposite() != null && 1 == thirdMatchInfo.getHomeAwayOpposite()) {
                    for (MatchEventInfo matchEventInfo : list) {
                        Integer t1 = matchEventInfo.getT1();
                        Integer t2 = matchEventInfo.getT2();
                        matchEventInfo.setT1(t2);
                        matchEventInfo.setT2(t1);
                        if (matchEventInfo.getHomeAway().equals("home")) {
                            matchEventInfo.setHomeAway("away");
                        } else if (matchEventInfo.getHomeAway().equals("away")) {
                            matchEventInfo.setHomeAway("home");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("MatchSettleCheckServiceImpl-changeHomeAway List<MatchEventInfo>:", e);
        }
    }

    private Long searchEventTimeByEvent(MatchSettleCheckInfo checkInfo, List<MatchSettleCheckInfo> checkInfoList) {
        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
        Map<String, List<MatchSettleCheckInfo>> checkGroupMap = SettleCheckUtils.groupBySettleCheck(checkInfoList);
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
    private Long searchEventTimeByScores(List<MatchSettleCheckInfo> checkInfoList) {
        //1.先拿数据的比较比分 如果一致则以数据商的时间为准 取最小的时间
        Long eventTime = 0l;
        for (MatchSettleCheckInfo matchSettleCheckInfo : checkInfoList) {
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

    private void settleMatchSettleEvent(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfo checkInfo) {
        log.info("eventId::{}::settleMatchSettleEvent start", checkInfo.getThirdSettleScoreEventId());
        //1.从核对对象 复制 事件 到结算对象
        SettleCheckUtils.copyCheckInfoToMatchSettleEvent(checkInfo, matchSettleEvent);
        //2.修改结算对象状态
        matchSettleEvent.setStatus(SETTLED);
        //3.设置结算对象的 是否自动结算方式
        if (checkInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
            matchSettleEvent.setIsAutoSettle(1);
            //106214  数据商自动结算清空审核员信息
            matchSettleEvent.setOperater(null);
            matchSettleEvent.setUserid(null);
        } else {
            matchSettleEvent.setIsAutoSettle(0);
            matchSettleEvent.setOperater(checkInfo.getUserName());
        }
        //4.设置结算人 结算次数  是否二次结算等
        matchSettleEvent.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
        matchSettleEvent.setSettleCount(matchSettleEvent.getSettleCount() == null ? 1 : matchSettleEvent.getSettleCount() + 1);
        matchSettleEvent.setModifyTime(System.currentTimeMillis());
        //只有一次结算会走这里
        matchSettleEvent.setSettleTimes(1);
        //5分钟区间根据5分钟开关是否打开做校验
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(matchSettleEvent.getStandardMatchId());
        if (matchSettleInfo.getFiveMinSwitch() != null && matchSettleInfo.getFiveMinSwitch() == 1) {
            matchSettleEvent.setFiveMinSection(checkInfo.getFiveMinSection());
        } else {
            matchSettleEvent.setFiveMinSection(null);
        }
        matchSettleEvent.setIsGrey(0);
        matchSettleEvent.setHasDeleteEvent(0);
        matchSettleEvent.setCurrentEventStatus(0);
        //最终事件处理逻辑
        log.info("eventId::{}::settleMatchSettleEvent matchSettleEvent:{} checkInfo:{} matchSettleInfo:{}", checkInfo.getThirdSettleScoreEventId(), matchSettleEvent, checkInfo, matchSettleInfo);
        matchSettleEventService.endEventSettleByEvent(matchSettleEvent);
        //5.更新结算对象到结算表
        matchSettleEventRepository.updateById(matchSettleEvent);

        //更新延迟表
        matchDelaySettleInfoRepository.updateStatusByCheckInfoIds(Arrays.asList(checkInfo.getId()),3);
        if (matchSettleEvent.getSettleNum().equals("1028")) {
            EditMatchSettleEventDto editMatchSettleEventDto = new EditMatchSettleEventDto();
            editMatchSettleEventDto.setStandardMatchId(matchSettleEvent.getStandardMatchId());
            editMatchSettleEventDto.setT1(matchSettleEvent.getT1());
            editMatchSettleEventDto.setT2(matchSettleEvent.getT2());
            editMatchSettleEventDto.setOperatorName(checkInfo.getUserName());
            matchSettleEventService.updateGoWaterPenaltyScores(editMatchSettleEventDto);
        }
        //结算时把回滚订单数清零
        matchSettleRoleBackInfoHelper.settleRollBackSetNullOrderCount(matchSettleEvent.getId());
        matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleEvent.getStandardMatchId());
        log.info("eventId::{}::settleMatchSettleEvent 开始记录日志", checkInfo.getThirdSettleScoreEventId());
        //6.日志
        String userName = "";
        if (checkInfo.getCheckDataType().equals(2)) {
            userName = checkInfo.getUserName() + ",(第" + matchSettleEvent.getCheckNumber() + "人)";
        } else {
            if(StringUtils.isNotEmpty(checkInfo.getDataSourceCode())&&
                    (!checkInfo.getDataSourceCode().equals("PD")&&!checkInfo.getDataSourceCode().equals("PD2"))){
                userName = checkInfo.getDataSourceCode();
            }else {
                if (null!=checkInfo.getUserName()){
                    userName=checkInfo.getUserName();
                }else {
                    userName=checkInfo.getDataSourceCode();
                }
            }
        }
        matchSettleLogService.matchSettleEventAddLog(matchSettleEvent, userName,
                OperateLogTypeEnum.SCORE_SETTLE.getCode().toString(), "", "");
        // 如果是进球事件 需要编辑进球方式
        if (matchSettleEvent.getEventCode().equals("goal") && matchSettleEvent.getEventType() == 1) {
            MatchSettleEvent extryEvent = matchSettleEventRepository.getExtryEvent(matchSettleEvent.getStandardMatchId(),
                    matchSettleEvent.getThirdEventSourceId(),matchSettleEvent.getId(),2, null);
            if (extryEvent != null) {
                extryEvent.setT1(matchSettleEvent.getT1());
                extryEvent.setT2(matchSettleEvent.getT2());
                extryEvent.setHomeAway(matchSettleEvent.getHomeAway());
                extryEvent.setModifyTime(System.currentTimeMillis());
                extryEvent.setIsGrey(0);
                extryEvent.setHasDeleteEvent(0);
                extryEvent.setCurrentEventStatus(0);
                matchSettleEventRepository.updateById(extryEvent);
            }
        }
        // 如果是点球谁先射门球队事件则需要调用
        if (matchSettleEvent.getSettleNum().equals("-1030")) {
            matchSettleEventService.settlePenaltyTeamFirst(matchSettleEvent);
        } else {
            //７.下发MQ
            if (matchSettleEvent.getPeriodId()==100 && (matchSettleEvent.getSettleNum().equals(MatchPeriodEnum.GOAL_10.getCode().toString()) ||
                    matchSettleEvent.getSettleNum().equals(MatchPeriodEnum.Corner_3.getCode().toString()) ||
                    matchSettleEvent.getSettleNum().equals(MatchPeriodEnum.BOOKINGS_9.getCode().toString()))) {
                try {
                    //44612bug手动延迟1S下发,包括全场,进球,角球,发牌比分结算
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("eventId::{}::标准赛事Id:{} 延迟1s下发全场事件异常: ", checkInfo.getThirdSettleScoreEventId(), matchSettleEvent.getId(), e);
                }
            }
            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);
        }

        //8.ws推送
        String eventCode = "";
        if (checkInfo.getEventCode().equals("goal") || checkInfo.getEventCode().equals("no goal")) {
            eventCode = "goal";
        } else if (checkInfo.getEventCode().equals("corner")) {
            eventCode = "corner";
        } else {
            eventCode = "fa_card";
        }
        wsPushService.pushStandardSettleScores(matchSettleEvent.getStandardMatchId(), eventCode);
        wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleEvent.getStandardMatchId()
                , eventCode, checkInfo.getId(), checkInfo.getSettleScoreEventId(), 2));
        log.info("eventId::{}::settleMatchSettleEvent end", checkInfo.getThirdSettleScoreEventId());
    }

    private void batchSettleMatchScore(List<Pair<MatchSettleScore, MatchSettleCheckInfo>> settledCheckMessages, String linkedId) {
        log.info("linkedId::{}::batchSettleMatchScore start", linkedId);
        MatchSettleScore tempSettleScore = settledCheckMessages.get(0).getLeft();

        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(tempSettleScore.getStandardMatchId());
        Map<String, List<MatchSettleThirdScore>> settleThirdScoreMap = new HashMap<>();
        if (tempSettleScore.getSportId().equals(2L)) {
            List<String> settleNums = settledCheckMessages.stream().map(t->t.getLeft().getSettleNum()).collect(Collectors.toList());
            List<MatchSettleThirdScore> matchSettleThirdScores =matchSettleThirdScoreRepository.getModelByStandardMatchIdAndSettleNum(tempSettleScore.getStandardMatchId(),settleNums);
            settleThirdScoreMap = matchSettleThirdScores.stream().collect(Collectors.groupingBy(MatchSettleThirdScore::getSettleNum));
        }

        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(tempSettleScore.getStandardMatchId());
        List<MatchSettleScore> batchUpdateSettleScores = new ArrayList<>();
        List<Long> willSettleCheckInfoIds = new ArrayList<>();
        List<MatchSettleOperateLog> batchUpdateOperateLogs = new ArrayList<>();
        for (Pair<MatchSettleScore, MatchSettleCheckInfo> settledCheckMessage : settledCheckMessages) {
            MatchSettleScore matchSettleScore = settledCheckMessage.getLeft();
            MatchSettleCheckInfo checkInfo = settledCheckMessage.getRight();
            log.info("linkedId::{} scoreEventId:{} thirdScoreEventId:{} settleNum:{} batchSettleMatchScore开始遍历处理", linkedId, checkInfo.getSettleScoreEventId(), checkInfo.getThirdSettleScoreEventId(),matchSettleScore.getSettleNum());
            //1.从核对对象 复制 比分 到结算对象
            SettleCheckUtils.copyCheckInfoToMatchSettleScore(checkInfo, matchSettleScore);
            //2.修改结算对象状态
            matchSettleScore.setStatus(SETTLED);
            //3.设置结算对象的 是否自动结算方式
            if (checkInfo.getCheckDataType() == MatchSettleCheckConstant.CheckDataType.DATA_SOURCE) {
                //需求4547/优化单107713 假如本节有删除事件,当前节的比分不能单独通过数据源结算,只允许审核员+数据源或者审核员+审核员 或者管理员结算

                boolean basketSettleTag = matchSettleScoreHelper.validateBasketSettle(matchSettleScore,linkedId);
                if (basketSettleTag){
                    log.info("{}::,篮球比分有删除事件,不结算::{}",matchSettleScore.getId(),matchSettleScore.getSettleNum());
                    return;
                }

                matchSettleScore.setIsAutoSettle(1);
                //106214 自动结算清空审核员信息
                matchSettleScore.setOperater(null);
                matchSettleScore.setUserid(null);
            } else {
                matchSettleScore.setIsAutoSettle(0);
                matchSettleScore.setOperater(checkInfo.getUserName());
            }
            //4.设置结算人 结算次数  是否二次结算等
            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
            matchSettleScore.setSettleCount(matchSettleScore.getSettleCount() == null ? 1 : matchSettleScore.getSettleCount() + 1);
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            //只有一次结算会走这里
            matchSettleScore.setSettleTimes(1);

            matchSettleScore.setIsGrey(0);
            matchSettleScore.setHasDeleteEvent(0);
            matchSettleScore.setCurrentEventStatus(0);
            //当 上半场 全场触发结算的时候 校验 事件是否和 全场比分一致，然后如果一致则走 最后事件结算逻辑
            matchSettleScoreService.endEventSettleByScore(matchSettleScore);
            //篮球结算后去掉比分带入弹框
            matchSettleScore.setPopupUsers(null);
            //70555
            if (matchSettleScore.getSportId().equals(2L)){
                List<MatchSettleThirdScore> matchSettleThirdScores =settleThirdScoreMap.get(matchSettleScore.getSettleNum());
                boolean tag =false;
                if (null!=matchSettleThirdScores&&!matchSettleThirdScores.isEmpty()){
                    for (int i =0;i<matchSettleThirdScores.size();i++ ){
                        if (!matchSettleThirdScores.get(i).getT1().equals(matchSettleScore.getT1())||!matchSettleThirdScores.get(i).getT2().equals(matchSettleScore.getT2())){
                            tag = true;
                        }
                    }
                }
                if (tag){
                    matchSettleScore.setCurrentEventTag(1);
                    matchSettleInfo.setCurrentEventTag(1);
                }
            }

            String userName = "";
            if (checkInfo.getCheckDataType().equals(2)) {
                if (tempSettleScore.getSportId().equals(2L)){
                    List<String> array =JSONArray.parseArray(matchSettleInfo.getAuditorActiveArray()).toJavaList(String.class);
                    if (!CollectionUtil.isEmpty(array)){
                        userName = getCheckUserName(checkInfo.getUserName(),array);
                    }else {
                        userName = checkInfo.getUserName() + ",(第" + matchSettleScore.getCheckNumber() + "人)";
                    }

                }else {
                    userName = checkInfo.getUserName() + ",(第" + matchSettleScore.getCheckNumber() + "人)";
                }

            } else {
                if(StringUtils.isNotEmpty(checkInfo.getDataSourceCode())&&
                        (!checkInfo.getDataSourceCode().equals("PD")&&!checkInfo.getDataSourceCode().equals("PD2"))){
                    userName = checkInfo.getDataSourceCode();
                }else {
                    if (null!=checkInfo.getUserName()){
                        userName=checkInfo.getUserName();
                    }else {
                        userName=checkInfo.getDataSourceCode();
                    }

                }
            }
            List<MatchSettleOperateLog> operateLogs = matchSettleLogService.batchMatchSettleScoreAddLog(standardMatchInfo, matchSettleScore, userName,
                    OperateLogTypeEnum.SCORE_SETTLE, OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(), "");
            //2.MQ下发
            if (matchSettleScore.getPeriodId()==100 && (matchSettleScore.getSettleNum().equals(MatchPeriodEnum.GOAL_10.getCode().toString()) ||
                    matchSettleScore.getSettleNum().equals(MatchPeriodEnum.Corner_3.getCode().toString()) ||
                    matchSettleScore.getSettleNum().equals(MatchPeriodEnum.BOOKINGS_9.getCode().toString()))) {
                matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore, 2);
            } else {
                matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);
            }
            log.info("linkedId::{} scoreEventId:{} thirdScoreEventId:{} settleNum:{} 成功发送到 MATCH_SETTLE_SCORES", linkedId, checkInfo.getSettleScoreEventId(), checkInfo.getThirdSettleScoreEventId(),matchSettleScore.getSettleNum());
            if (matchSettleScore.getSettleNum().equals("105") || matchSettleScore.getSettleNum().equals("1010")||
                    matchSettleScore.getSettleNum().equals("bk_1ht")|| matchSettleScore.getSettleNum().equals("bk_ft_et")) {
                recordScore(matchSettleScore, matchSettleInfo);
            }

            //3.WS 推送
            String eventCode = "";
            if (checkInfo.getEventCode().equals("fa_card")) {
                eventCode = "fa_card";
            } else if (checkInfo.getEventCode().equals("corner")) {
                eventCode = "corner";
            } else {
                eventCode = "goal";
            }
            wsPushService.pushStandardSettleScores(matchSettleScore.getStandardMatchId(), eventCode);
            wsPushService.pushSettleMatchList(new MatchListSettleDto(matchSettleScore.getStandardMatchId()
                    , eventCode, checkInfo.getId(), checkInfo.getSettleScoreEventId(), 3));
            log.info("linkedId::{} scoreEventId:{} thirdScoreEventId:{} settleNum:{} batchSettleMatchScore 成功发送到 MATCH_SETTLE_SCORES_PUSH MATCH_LIST_SETTLE_PUSH", linkedId, checkInfo.getSettleScoreEventId(), checkInfo.getThirdSettleScoreEventId(),matchSettleScore.getSettleNum());
            batchUpdateSettleScores.add(matchSettleScore);
            willSettleCheckInfoIds.add(checkInfo.getId());
            batchUpdateOperateLogs.addAll(operateLogs);
        }

        //5.更新结算对象到结算表
        matchSettleScoreRepository.saveOrUpdateBatch(batchUpdateSettleScores);
        //更新延迟表
        List<Long> scoreIds = batchUpdateSettleScores.stream().map(MatchSettleScore::getId).collect(Collectors.toList());
        matchDelaySettleInfoRepository.updateStatusByCheckInfoIds(willSettleCheckInfoIds,3);

        // 必须放在batchUpdateSettleScores后面
        matchSettleScoreHelper.verifyScoresIsSame(tempSettleScore.getStandardMatchId());
        //结算时把回滚订单数清零
        matchSettleRoleBackInfoHelper.batchSettleRollBackSetNullOrderCount(scoreIds);
        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
        matchSettleOperateLogRepository.saveOrUpdateBatch(batchUpdateOperateLogs);
        log.info("linkedId::{}::batchSettleMatchScore end", linkedId);
    }

    private void recordScore(MatchSettleScore matchSettleScore, MatchSettleInfo matchSettleInfo) {
        matchSettleInfo.setModifyTime(System.currentTimeMillis());
        if (matchSettleScore.getSettleNum().equals("105")||matchSettleScore.getSettleNum().equals("bk_1ht")) {
            matchSettleInfo.setH1T1(matchSettleScore.getT1());
            matchSettleInfo.setH1T2(matchSettleScore.getT2());
        } else if (matchSettleScore.getSettleNum().equals("1010")||matchSettleScore.getSettleNum().equals("bk_ft_et")) {
            matchSettleInfo.setFtT1(matchSettleScore.getT1());
            matchSettleInfo.setFtT2(matchSettleScore.getT2());
        }
    }

    public boolean isSettle2(Long standardMatchId) {
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(standardMatchId);
        if (matchSettleInfo == null || matchSettleInfo.getSettleType() == 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void confirmGoalDoFilter(List<MatchEventInfo> data) {
        if(CollectionUtil.isEmpty(data)){
            return;
        }
        try {
            for (MatchEventInfo event : data) {
                if (event.getDataSourceCode().equals("SR") && MatchSettleCheckConstant.GoalConfirmEventCode.SR.equals(event.getEventCode())) {
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(event);
                    this.confirmDataSourceSettleEvent(event);
                    continue;
                }
                if (event.getDataSourceCode().equals("BG") && MatchSettleCheckConstant.GoalConfirmEventCode.BG.equals(event.getEventCode())) {
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(event);
                    this.confirmDataSourceSettleEvent(event);
                    continue;
                }
                if (event.getDataSourceCode().equals("RB") && MatchSettleCheckConstant.GoalConfirmEventCode.RB.equals(event.getEventCode())) {
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(event);
                    this.confirmDataSourceSettleEvent(event);
                    continue;
                }
                if (event.getDataSourceCode().equals("KO") && MatchSettleCheckConstant.GoalConfirmEventCode.KO.equals(event.getEventCode())) {
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(event);
                    this.confirmDataSourceSettleEvent(event);
                    continue;
                }
                if (MatchSettleCheckConstant.GoalConfirmEventCode.PA.equals(event.getEventCode())
                        || "match_status".equals(event.getEventCode())) {
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(event);
                    this.confirmDataSourceSettleEvent(event);
                    continue;
                }
                if (event.getDataSourceCode().equals("N01") && MatchSettleCheckConstant.GoalConfirmEventCode.KO.equals(event.getEventCode())) {
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(event);
                    this.confirmDataSourceSettleEvent(event);
                    continue;
                }
                if (event.getDataSourceCode().equals("LS") && MatchSettleCheckConstant.GoalConfirmEventCode.KO.equals(event.getEventCode())) {
                    matchSettleGoalStatusService.updateMatchSettleGoalStatus(event);
                    this.confirmDataSourceSettleEvent(event);
                    continue;
                }

            }
        } catch (Exception e) {
            log.error("linkId::{}::eventId:{} confirmGoalDoFilter error:", data.get(0).getLinkId(), data.get(0).getThirdEventId(), e);
        }
    }

    //处理三方赛事删除事件 -方案1 物理删除核对事件记录
    @Override
    public void canceledCheckMatchThirdSettleEvent(MatchSettleThirdEvent settleThirdEvent, MatchEventInfo data, Integer order) {
        log.info("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent start",data.getLinkId(), data.getThirdEventId());
        try {
            MatchEventInfo oldEvent = this.getOldMatchInfoByCancel(data);
            if (oldEvent == null) {
                log.error("linkId::{}::eventId:{} 原被删除事件不存在", data.getLinkId(), data.getThirdEventId());
                return;
            }
            //先试下物理删除是否有用呢
            List<MatchSettleCheckInfo> list =  matchSettleCheckInfoRepository.getModelByThirdScoreEventIdAndMatchIdAndDataSourceCode(settleThirdEvent.getId(),data.getStandardMatchId(),data.getDataSourceCode());
            if(list.size()==0){
                log.error("linkId::{}::eventId:{} 没有找到被删除事件的核对记录",data.getLinkId(), data.getThirdEventId());
                return;
            }
//            MatchSettleCheckInfo matchSettleCheckInfo =list.get(0);
            matchSettleCheckInfoRepository.deleteByThirdScoreEventIdAndMatchIdAndDataSourceCode(settleThirdEvent.getId(),data.getStandardMatchId(),data.getDataSourceCode());

            for(MatchSettleCheckInfo matchSettleCheckInfo : list) {
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getById(matchSettleCheckInfo.getSettleScoreEventId());

                //2755 删除延迟记录
                MatchDelaySettleInfoExample delaySettleInfoExample = new MatchDelaySettleInfoExample();
                delaySettleInfoExample.createCriteria().andStandardMatchIdEqualTo(data.getStandardMatchId()).andDataSourceCodeEqualTo(data.getDataSourceCode()).andCheckInfoIdEqualTo(matchSettleCheckInfo.getId());
                matchDelaySettleInfoRepository.removeByMatchIdAndDataSourceCodeAndCheckInfoId(data.getStandardMatchId(),data.getDataSourceCode(),matchSettleCheckInfo.getId());

                log.info("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent 被删除赛事:{} 引发删除事件",data.getLinkId(), data.getThirdEventId(), data.getStandardMatchId());
                if (matchSettleCheckInfo.getDataSourceCode() != null && (matchSettleCheckInfo.getDataSourceCode().equals("N01") ||
                        matchSettleCheckInfo.getDataSourceCode().equals("N02") || matchSettleCheckInfo.getDataSourceCode().equals("N03") || matchSettleCheckInfo.getDataSourceCode().equals("LS"))) {
                    log.info("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent V1", matchSettleCheckInfo.getDataSourceCode());
                } else {
                    matchSettleEvent.setHasDeleteEvent(1);
                    matchSettleEvent.setCurrentEventStatus(2);
                    //删除事件标记次序事件
                    matchSettleEventRepository.updateById(matchSettleEvent);
                }
                List<String> deleteSettleNums = new ArrayList<>();
                //删除事件标记阶段比分
                matchScoresSettleInitChainFilter.deleteEventPeriodScorefilter(oldEvent, deleteSettleNums);
                if(deleteSettleNums.size()!=0){
                    List<MatchSettleScore> matchSettleScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(deleteSettleNums,data.getStandardMatchId(),null);
                    if (matchSettleCheckInfo.getDataSourceCode() != null && (matchSettleCheckInfo.getDataSourceCode().equals("N01") ||
                            matchSettleCheckInfo.getDataSourceCode().equals("N02") || matchSettleCheckInfo.getDataSourceCode().equals("N03") || matchSettleCheckInfo.getDataSourceCode().equals("LS"))) {
                        log.info("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent V2", matchSettleCheckInfo.getDataSourceCode());
                    } else {
                        matchSettleScores.forEach(t->{
                            t.setHasDeleteEvent(1);
                            t.setCurrentEventStatus(2);
                        });
                        matchSettleScoreRepository.updateBatchById(matchSettleScores);
                    }
                }
                //存储删除标记到redis
                matchSettleBatchCheckServiceHelper.validateDeleteEvent(matchSettleEvent, deleteSettleNums, data);
                //删除事件标记赛事
                if (matchSettleCheckInfo.getDataSourceCode() != null && (matchSettleCheckInfo.getDataSourceCode().equals("N01") ||
                        matchSettleCheckInfo.getDataSourceCode().equals("N02") || matchSettleCheckInfo.getDataSourceCode().equals("N03") || matchSettleCheckInfo.getDataSourceCode().equals("LS"))) {
                    log.info("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent V3", matchSettleCheckInfo.getDataSourceCode());
                } else {
                    MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(settleThirdEvent.getStandardMatchId());
                    matchSettleInfo.setHasDeleteEvent(1);
                    matchSettleInfo.setCurrentEventStatus(2);
                    matchSettleInfo.setModifyTime(System.currentTimeMillis());
                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                }
                //通知前端刷新赛事相关的
                if (data.getEventCode().equals("red_card") || data.getEventCode().equals("yellow_card")) {
                    data.setEventCode("fa_card");
                }
                wsPushService.pushSettleMatchList(new MatchListSettleDto(data.getStandardMatchId(), data.getEventCode(), null, null, 4));
                matchSettleBatchCheckServiceHelper.deleteAuditorCheckInfo(matchSettleCheckInfo.getSettleScoreEventId());
            }
        }catch (Exception e){
            log.error("linkId::{}::eventId:{} canceledCheckMatchThirdSettleEvent ERROR:",data.getLinkId(), data.getThirdEventId(),e);
        }
    }

    public MatchEventInfo getOldMatchInfoByCancel(MatchEventInfo data) {
//        //0 迭代获取取消事件
//        //1.取消事件
        MatchEventInfoExample matchEventInfoExample=new MatchEventInfoExample();
        matchEventInfoExample.createCriteria().andThirdMatchIdEqualTo(data.getThirdMatchId()).andThirdEventIdEqualTo(data.getExtraInfo()).andDataSourceCodeEqualTo(data.getDataSourceCode()).andSportIdEqualTo(data.getSportId());
        List<MatchEventInfo> oldMatchInfos =matchEventInfoRepository.getMatchEventInfoCaseOne(data.getThirdMatchId(),data.getExtraInfo(),data.getDataSourceCode(),data.getSportId());
        if(oldMatchInfos.size()==0){
            //事件未消费
            cacheCancelEvent(data);
            log.error("canleEvent 事件未消费入库"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
            return null;
        }
        MatchEventInfo oldEvent= oldMatchInfos.get(0);
        if(!EffectScoresCode.chargeEffectScores(data.getSportId(),oldEvent.getEventCode())){
            return  null;
        }
        return oldEvent;
    }

    private boolean isMatchGoalStatusConfirm(MatchSettleGoalStatus goalStatus, String eventCode) {
        //BT 的先直接过去
        if (eventCode.equals("goal")) {
            if (goalStatus == null || goalStatus.getGoalStatus() == null || goalStatus.getGoalStatus() == MatchSettleCheckConstant.GoalStatus.CONFIRM) {
                return true;
            }
            if (goalStatus.getDataSourceCode().equals("BT") || goalStatus.getDataSourceCode().equals("1X")|| goalStatus.getDataSourceCode().equals("LS")|| goalStatus.getDataSourceCode().equals("BFZX")) {
                return true;
            }
            return false;
        } else if (eventCode.equals("corner")) {
            if (goalStatus == null || goalStatus.getCornerStatus() == null || goalStatus.getCornerStatus() == MatchSettleCheckConstant.GoalStatus.CONFIRM) {
                return true;
            }
            if (goalStatus.getDataSourceCode().equals("BT") || goalStatus.getDataSourceCode().equals("RB")||goalStatus.getDataSourceCode().equals("BG")||
                    goalStatus.getDataSourceCode().equals("KO")|| goalStatus.getDataSourceCode().equals("LS")|| goalStatus.getDataSourceCode().equals("BFZX")|| goalStatus.getDataSourceCode().equals("SR")) {
                return true;
            }
            return false;
        }
        return true;
    }

    private void confirmDataSourceSettleEvent(MatchEventInfo event) {
        //1.1查询当前已经入库的三方进球核对事件
        List<MatchSettleCheckInfo> goalCheckInfos = this.searchGoalEventCheckInfoByEvent(event);
        //1.2 更新当前进球核对事件确认为已经进球
        if (CollectionUtils.isEmpty(goalCheckInfos)) {
            log.info("linkId::{}::eventId:{} confirmDataSourceSettleEvent 找不到进球事件", event.getLinkId(), event.getThirdEventId());
            return;
        }
        try {
            List<Long> settleEventIds = goalCheckInfos.stream().map(t->t.getSettleScoreEventId()).collect(Collectors.toList());
            List<MatchSettleEvent> matchSettleEvents = matchSettleEventRepository.getByIds(settleEventIds);
            Map<Long,Integer> matchSettleEventMap  = matchSettleEvents.stream().collect(Collectors.toMap(MatchSettleEvent::getId, MatchSettleEvent::getEventType, (v1, v2)->v1));
            
            // 先收集 eventType=1 的 checkinfo，再收集 eventType=3 的 checkinfo
            List<MatchSettleCheckInfo> eventType1CheckInfos = new ArrayList<>();
            List<MatchSettleCheckInfo> eventType3CheckInfos = new ArrayList<>();
            
            for(MatchSettleCheckInfo goalCheckInfo : goalCheckInfos){
                Long settleScoreEventId = goalCheckInfo.getSettleScoreEventId();
                Integer eventType = matchSettleEventMap.get(settleScoreEventId);
                if (eventType != null && eventType == 1) {
                    eventType1CheckInfos.add(goalCheckInfo);
                } else if (eventType != null && eventType == 3) {
                    eventType3CheckInfos.add(goalCheckInfo);
                }
            }
            
            // 按顺序合并：先 eventType=1，后 eventType=3
            List<MatchSettleCheckInfo> sortedCheckInfos = new ArrayList<>();
            sortedCheckInfos.addAll(eventType1CheckInfos);
            sortedCheckInfos.addAll(eventType3CheckInfos);

            for(MatchSettleCheckInfo matchSettleCheckInfo : sortedCheckInfos) {
                confirmDataSourceGoalSettleEvent(matchSettleCheckInfo, event.getLinkId());
            }
        } catch (Exception e) {
            log.error("linkId::{}::eventId:{} confirmDataSourceSettleEvent 调整checkinfo顺序报错", event.getLinkId(), event.getThirdEventId());
        }

    }

    private void confirmDataSourceGoalSettleEvent(MatchSettleCheckInfo goalCheckInfo, String linkedId) {
        log.info("linkId::{}::eventId::{}::confirmDataSourceGoalSettleEvent start",linkedId,goalCheckInfo.getThirdSettleScoreEventId());
        goalCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
        goalCheckInfo.setModifyTime(System.currentTimeMillis());
        matchSettleCheckInfoRepository.updateById(goalCheckInfo);
        log.info("linkId::{}::eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  BT-1X-F01 更新进球事件为确认状态",linkedId,goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
        MatchSettleEvent matchSettleEvent = matchSettleEventRepository.getById(goalCheckInfo.getSettleScoreEventId());
        if (!matchSettleScoreService.isPeriodScoresBeforeSettledByEvent(matchSettleEvent)) {
            log.info("linkId::{}::eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  BT-1X-F01 之前的阶段没结算", linkedId,goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
            return;
        }
        //2.1查询当前已经入库的三方进球结算事件
        //2.2判断当前结算事件是否结算
        //2.3如果没结算发起进球事件自动核对
        if (matchSettleEvent.getStatus() != 3) {
            MatchSettleTemplate countDownTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(matchSettleEvent.getStandardMatchId(), SettleTemplateTypeEnum.COUNT_DOWEN.code);
            log.info("linkId::{}::eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  事件比分核对开始", linkedId,goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
            batchCheckCommonMatchSettleScoreEvent(Arrays.asList(Pair.of(matchSettleEvent, goalCheckInfo)),true, linkedId,countDownTemplate);
            log.info("linkId::{}::eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent  事件比分核对结束", linkedId,goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId());
        } else {
            log.info("linkId::{}::eventId::{}::standardMatchId::{}::confirmDataSourceGoalSettleEvent 当前的结算事件已经结算id:{}", linkedId,goalCheckInfo.getThirdSettleScoreEventId(), goalCheckInfo.getStandardMatchId(), matchSettleEvent.getId());
        }
    }

    private List<MatchSettleCheckInfo> searchGoalEventCheckInfoByEvent(MatchEventInfo event) {
        if("match_status".equals(event.getEventCode())) {
            List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelByItemsAndOrderCreateTime(event.getStandardMatchId(),
                    event.getDataSourceCode(),"goal",MatchSettleCheckConstant.CheckType.EVENT_SCORE,MatchSettleCheckConstant.CheckDataType.DATA_SOURCE,null,null);
            log.info("::{}::根据match_status编码, 查询到需要确认的进球事件数量:{}", event.getLinkId(), list.size());
            if(!CollectionUtils.isEmpty(list)) {
                log.info("::{}::根据match_status编码, 查询到需要确认的进球事件:{}", event.getLinkId(), list.get(0));
                return list;
            }
            log.info("::{}::根据match_status编码, 没有查询到需要确认的进球事件", event.getLinkId());
            return null;
        }

        //1.加时赛处理机制 加时赛的 kick_off会扣除掉已经结算的 全场比分(不含加时赛)
        if (event.getMatchPeriodId().equals(41l) || event.getMatchPeriodId().equals(42l)) {
            //1.查询已经结算的全场比分（不包含加时）
            List<MatchSettleScore> fullScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(Arrays.asList("1010"),event.getStandardMatchId(),Arrays.asList(3));
            //2.扣除全场比分
            if (fullScores.size() != 0) {
                MatchSettleScore fullScore = fullScores.get(0);
                event.setT1(event.getT1() - fullScore.getT1());
                event.setT2(event.getT2() - fullScore.getT2());
            }
        }
        String eventCode = "goal";
        if(event.getEventCode().equals("corner_taken")){
            eventCode="corner";
        }
        List<MatchSettleCheckInfo> list = matchSettleCheckInfoRepository.getModelByItemsAndOrderCreateTime(event.getStandardMatchId(),
                event.getDataSourceCode(),eventCode,MatchSettleCheckConstant.CheckType.EVENT_SCORE,MatchSettleCheckConstant.CheckDataType.DATA_SOURCE,event.getT1(),event.getT2());
        log.info("::{}::查询到需要确认的进球事件数量:{}", event.getLinkId(), list.size());
        if (list.size() != 0) {
            log.info("::{}::查询到需要确认的进球事件:{}", event.getLinkId(), list);
            return list;
        } else {
            log.info("::{}::没有查询到需要确认的进球事件", event.getLinkId());
            return null;
        }
    }

    private void batchCheckBasketPeriodScoreOrder(List<Pair<Object, MatchSettleCheckInfo>> checkMessages, MatchSettleInfo matchSettleInfo, StandardMatchInfo standardMatchInfo) {
        if (matchSettleInfo.getSettleOrderClosed() != null && matchSettleInfo.getSettleOrderClosed() != 0) {
            return;
        }
        List<Integer > statusList =new ArrayList<>();
        statusList.add(1);   statusList.add(0);   statusList.add(2);  statusList.add(4);
        //1.根据当前结算编码得到他之前的结算编码
        List<String> settleNumList = checkMessages.stream().flatMap(t->SettleNumUtils.countBasketballScoreSettleNumBefore(((MatchSettleScore)t.getLeft()).getSettleNum(),standardMatchInfo.getMatchLength()).stream()).distinct().collect(Collectors.toList());
        if(settleNumList.size()==0){
            return;
        }
        List<MatchSettleScore> unsettledScores =matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList,standardMatchInfo.getId(),statusList);
        Map<String, Integer> settleMap = unsettledScores.stream().collect(Collectors.toMap(t->t.getSettleNum(), t->1, (v1, v2)->v1));
        checkMessages = checkMessages.stream().filter(t-> {
            List<String> settleNums = SettleNumUtils.countBasketballScoreSettleNumBefore(((MatchSettleScore)t.getLeft()).getSettleNum(),standardMatchInfo.getMatchLength());
            for(String settleNum : settleNums) {
                if (settleMap.containsKey(settleNum)){
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private void initDelaySettleScore(Map<Long, MatchSettleScore> matchSettleScores, List<MatchSettleCheckInfo> batchCheckInfos,Long second, MatchSettleTemplate matchSettleTemplate, MatchSettleTemplate gayTemplate, String linkedId, Long sportId){
        log.info("linkId::{} isDelaySettleScore start", linkedId);
        //4547 篮球倒计时
//        if (!sportId.equals(1l)){
//            log.info("linkId::{} isDelaySettleScore目前只针对足球", linkedId);
//            return;
//        }
        if (batchCheckInfos.get(0).getDataSourceCode().equals("PA")){
            log.info("linkId::{} isDelaySettleScore只处理数据源的比分",linkedId);
            return;
        }
        log.info("linkId::{} isDelaySettleScore test1", linkedId);
        try {
            Map<Long,MatchSettleCheckInfo> checkInfoMap = batchCheckInfos.stream().collect(Collectors.toMap(MatchSettleCheckInfo::getSettleScoreEventId, Function.identity(), (v1, v2)->v1));
            Map<Long, Pair<Long, MatchSettleScore>> validCheckInfoIds = new HashMap<>();
            log.info("linkId::{} isDelaySettleScore test2", linkedId);
            for (MatchSettleScore matchSettleScore : matchSettleScores.values()) {
                log.info("linkId::{} isDelaySettleScore test3", linkedId);
                boolean isGoal = false;
                boolean isBooking = false;
                boolean isCorner = false;
                boolean isbasket = false;
                MatchSettleCheckInfo checkInfo = checkInfoMap.get(matchSettleScore.getId());
                if (goalDelaySettleNum.contains(matchSettleScore.getSettleNum())) {
                    isGoal = true;
                }
                if (bookingDelaySettleNum.contains(matchSettleScore.getSettleNum())) {
                    isBooking = true;
                }
                if (cornerDelaySettleNum.contains(matchSettleScore.getSettleNum())) {
                    isCorner = true;
                }
                if(basketballDelayNum.contains(matchSettleScore.getSettleNum())){
                    isbasket = true;
                }
                if (isCorner || isBooking || isGoal || isbasket) {
                    if (null == matchSettleTemplate) {
                        log.info("linkId::{} isDelaySettleScore无对应的倒计时模板,不进行延迟结算StandardMatchId: {},score.id: {},settleNum: {}", linkedId, matchSettleScore.getStandardMatchId(), matchSettleScore.getId(), matchSettleScore.getSettleNum());
                        return;
                    }
                    long delayTime = 0; //延迟结算秒数
                    List<DownSettleDto> dtoList = SettleTemplateJsonUtils.tansferDownList(matchSettleTemplate.getTemplateJson());
                    if (isGoal) {
                        delayTime = dtoList.get(0).getGoal15Min();
                    }
                    if (isCorner) {
                        delayTime = dtoList.get(0).getCorner15Min();
                    }
                    if (isBooking) {
                        delayTime = dtoList.get(0).getBooking15Min();
                    }
                    if (isbasket){
                        delayTime = dtoList.get(0).getGoal();
                    }
                    log.info("linkId::{} isDelaySettleScore进入延迟结算StandardMatchId:{},matchSettleScore.id:{},settleNum:{},template:{}", linkedId, matchSettleScore.getStandardMatchId(), matchSettleScore.getId(), matchSettleScore.getSettleNum(), matchSettleTemplate);
                    //获取当前时间过了灰色区间时间多少秒
                    if (matchSettleScore.getSportId()==1){
                        // 阶段比分消费时，secondFromStart可能为null，需要检查
                        if (second == null) {
                            log.warn("linkId::{} isDelaySettleScore secondFromStart为null，无法计算倒计时，跳过延迟结算。matchSettleScore.id:{},settleNum:{}", linkedId, matchSettleScore.getId(), matchSettleScore.getSettleNum());
                            continue;
                        }
                        Long secondTag = FootBallMatchSettleScoreUtils.getDelaySettleSeconds(matchSettleScore,second);
                        log.info("linkId::{} {}当前时间过了灰色区间秒数:{}",linkedId, matchSettleScore.getId(),secondTag);
                        delayTime = delayTime-secondTag;
                    }
                    if (delayTime > 0) {
                        //先查询一次是否已经存在记录
                        validCheckInfoIds.put(checkInfo.getId(), Pair.of(delayTime, matchSettleScore));
                    } else {
                        log.info("linkId::{} isDelaySettleScore不属于延迟结算的阶段,不进行延迟结算matchSettleScore.id: {},matchSettleScore.settleNum: {}", linkedId, matchSettleScore.getId(), matchSettleScore.getSettleNum());
                    }
                }
            }
            log.info("linkId::{} isDelaySettleScore test4", linkedId);
            if (!CollectionUtils.isEmpty(validCheckInfoIds)) {
                //先查询一次是否已经存在记录
                log.info("linkId::{} isDelaySettleScore test5", linkedId);
                List<MatchDelaySettleInfo> delays = matchDelaySettleInfoRepository.getModelByMatchIdAndCheckIds(batchCheckInfos.get(0).getStandardMatchId(),new ArrayList<>(validCheckInfoIds.keySet()));
                Map<Long, Integer> existDelayMap = delays.stream().collect(Collectors.toMap(MatchDelaySettleInfo::getCheckInfoId, t->1, (v1, v2)->v1));

                List<Pair<Long, MatchSettleScore>> validCheckInfoIdsList = validCheckInfoIds.entrySet().stream().filter(t->!existDelayMap.containsKey(t.getKey())).map(t->t.getValue())
                        .sorted(Comparator.comparing(t->{
                            MatchPeriodEnum matchPeriodEnum = MatchPeriodEnum.getEnum(t.getRight().getSettleNum());
                            return matchPeriodEnum==null? 9999 : matchPeriodEnum.getCode();
                        })).collect(Collectors.toList());
                List<MatchDelaySettleInfo> batchDelay = new ArrayList<>();
                log.info("linkId::{} 延迟结算存储原始数量:{} 过滤后数量:{}",linkedId, validCheckInfoIds.size(),validCheckInfoIdsList.size());
                for(Pair<Long, MatchSettleScore> value : validCheckInfoIdsList) {
                    Long delayTime = value.getLeft();
                    MatchSettleScore matchSettleScore = value.getRight();
                    MatchSettleCheckInfo checkInfo = checkInfoMap.get(matchSettleScore.getId());
                    MatchDelaySettleInfo matchDelaySettleInfo = FootBallMatchSettleScoreUtils.initMatchDelaySettleInfo(matchSettleScore, checkInfo);
                    matchDelaySettleInfo.setDelayTime(System.currentTimeMillis()+delayTime*1000);
                    matchDelaySettleInfo.setDelayTimeSecond(delayTime);
                    batchDelay.add(matchDelaySettleInfo);
                }
                if (!CollectionUtils.isEmpty(batchDelay)) {
                    matchDelaySettleInfoRepository.saveOrUpdateBatch(batchDelay);
                }
            }
        }catch(Exception e){
            log.error("linkId::{} isDelaySettleScore校验初始化延迟结算异常:", linkedId, e);
        }
    }

    private void initDelaySettleEvent(MatchSettleEvent matchSettleEvent, MatchSettleCheckInfo checkInfo){
        log.info("initDelaySettleEvent,score.id: {},settleNum: {},checkId :{}",matchSettleEvent.getId(),matchSettleEvent.getSettleNum(),checkInfo.getId());
        if (!matchSettleEvent.getSportId().equals(1l)){
            log.info("initDelaySettleEvent目前只针对足球StandardMatchId: {},matchSettleEvent.id:{}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId());
            return;
        }
        if (checkInfo.getDataSourceCode().equals("PA")){
            log.info("initDelaySettleEvent只处理数据源的比分StandardMatchId: {},matchSettleScore.id:{}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId());
            return;
        }
        boolean isBooking = false;
        boolean isCorner = false;
        try {
            if (bookingEventDelaySettleNum.contains(matchSettleEvent.getSettleNum())){
                isBooking = true;
            }
            if (cornerEventDelaySettleNum.contains(matchSettleEvent.getSettleNum())){
                isCorner = true;
            }
            if (isCorner||isBooking){
                MatchSettleTemplate matchSettleTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(matchSettleEvent.getStandardMatchId(), SettleTemplateTypeEnum.COUNT_DOWEN.code);
                if (null == matchSettleTemplate){
                    log.info("initDelaySettleEvent无对应的倒计时模板,不进行延迟结算StandardMatchId: {},event.id: {},settleNum: {}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId(),matchSettleEvent.getSettleNum());
                    return;
                }
                long delayTime = 0; //延迟结算秒数
                List<DownSettleDto> dtoList = SettleTemplateJsonUtils.tansferDownList(matchSettleTemplate.getTemplateJson());
                if (isCorner){
                    delayTime = dtoList.get(0).getCorner15Min();
                }
                if (isBooking){
                    delayTime = dtoList.get(0).getBooking15Min();
                }
                if (delayTime>0){
                        //先查询一次是否已经存在记录
                        List<MatchDelaySettleInfo> delays = matchDelaySettleInfoRepository.getModelByMatchIdAndCheckIds(checkInfo.getStandardMatchId(), Arrays.asList(checkInfo.getId()));
                        if (CollectionUtils.isEmpty(delays)){
                            //初始化延迟结算信息表
                            MatchDelaySettleInfo matchDelaySettleInfo = FootBallMatchSettleScoreUtils.initMatchDelayEventInfo(matchSettleEvent,checkInfo);
                            matchDelaySettleInfo.setDelayTime(System.currentTimeMillis()+delayTime*1000);
                            matchDelaySettleInfo.setDelayTimeSecond(delayTime);
                            matchDelaySettleInfoRepository.save(matchDelaySettleInfo);
                        }
                        log.info("initDelaySettleEvent进入延迟结算StandardMatchId: {},matchSettleEvent.id:{}",matchSettleEvent.getStandardMatchId(),matchSettleEvent.getId());
                }
            }else {
                log.info("initDelaySettleEvent不属于延迟结算的阶段,不进行延迟结算matchSettleScore.id: {},matchSettleEvent.settleNum: {}",matchSettleEvent.getId(),matchSettleEvent.getSettleNum());
            }
        }catch (Exception e){
            log.error("{},initDelaySettleEvent校验初始化延迟结算异常: {}",matchSettleEvent.getId(),e.getMessage());
        }
    }

    private String getCheckUserName(String userName,List<String> auditors){
        int number = auditors.indexOf(userName)+1;
        userName =userName + ",(第" + number + "人)";
        return userName;
    }

    public void cacheCancelEvent(MatchEventInfo data){
        String key = MATCH_EVENT_INFO+data.getThirdMatchId()+"_"+data.getExtraInfo()+"_"+data.getDataSourceCode()+"_"+data.getSportId()+"_cancel_event";
        redisService.set(key, 1, RedisConfig.REDIS_HOUR_TIME);
    }

    /**
     * 过滤未结算的5/15分钟阶段：检查删除事件和数据不一致
     * 对于数据不匹配和删除事件，只要发现有5/15分钟的数据不匹配或删除事件，就卡住所有当前以及之后的所有5/15分钟阶段结算
     * 这个是根据进球、角球和罚牌分别来处理的
     * @param standardMatchId 标准赛事ID
     * @param dataSourceCode 数据源编码
     * @param matchSettleScores 未结算的比分列表
     * @param linkedId 链路ID
     * @return 过滤后的比分列表
     */
    @Override
    public List<MatchSettleScore> filterUnsettled5Or15MinPeriods(Long standardMatchId, String dataSourceCode,
                                                                   List<MatchSettleScore> matchSettleScores, String linkedId) {
        try {
            List<MatchSettleScore> filteredScores = new ArrayList<>();

            // 从mention status中一次性获取所有事件类型（goal、corner、fa_card）的删除事件和数据不一致信息
            Map<String, Integer> allDeleteStatusMap = new HashMap<>();
            Map<String, Integer> allDataMismatchMap = new HashMap<>();
            getAllDeleteAndMismatchStatusFromMentionStatus(standardMatchId, linkedId, allDeleteStatusMap, allDataMismatchMap);

            // 按事件类型分组处理：goal、corner、fa_card
            Map<String, List<MatchSettleScore>> scoresByEventCode = matchSettleScores.stream()
                    .filter(score -> is5Or15MinPeriod(score.getSettleNum()))
                    .collect(Collectors.groupingBy(MatchSettleScore::getEventCode));

            // 对于每种事件类型，分别判断是否有数据不匹配或删除事件
            Set<String> blockedSettleNums = new HashSet<>(); // 需要卡住的所有settleNum
            
            for (Map.Entry<String, List<MatchSettleScore>> entry : scoresByEventCode.entrySet()) {
                String eventCode = entry.getKey();
                List<MatchSettleScore> eventScores = entry.getValue();
                
                // 检查该事件类型是否有5/15分钟阶段的数据不匹配或删除事件
                Set<String> eventMismatchOrDeleteSettleNums = new HashSet<>();
                
                for (MatchSettleScore score : eventScores) {
                    String scoreEventIdStr = String.valueOf(score.getId());
                    String settleNum = score.getSettleNum();
                    
                    // 检查是否有删除事件
                    if (allDeleteStatusMap.containsKey(scoreEventIdStr) && 
                        allDeleteStatusMap.get(scoreEventIdStr) != null && 
                        allDeleteStatusMap.get(scoreEventIdStr) == 1) {
                        eventMismatchOrDeleteSettleNums.add(settleNum);
                        log.info("linkId::{}::事件类型{}的5/15分钟阶段{}有删除事件, scoreEventId: {}", 
                                linkedId, eventCode, settleNum, score.getId());
                    }
                    
                    // 检查是否有数据不一致
                    if (allDataMismatchMap.containsKey(scoreEventIdStr) && 
                        allDataMismatchMap.get(scoreEventIdStr) != null && 
                        allDataMismatchMap.get(scoreEventIdStr) == 1) {
                        eventMismatchOrDeleteSettleNums.add(settleNum);
                        log.info("linkId::{}::事件类型{}的5/15分钟阶段{}有数据不一致, scoreEventId: {}", 
                                linkedId, eventCode, settleNum, score.getId());
                    }
                }
                
                // 如果该事件类型有数据不匹配或删除事件，卡住该事件类型的所有当前及后续的5/15分钟阶段
                if (!eventMismatchOrDeleteSettleNums.isEmpty()) {
                    // 获取该事件类型的所有后续5/15分钟阶段
                    Set<String> subsequentSettleNums = getSubsequent5Or15MinPeriodsByEvent(
                            standardMatchId, eventCode, eventMismatchOrDeleteSettleNums);
                    
                    // 卡住当前阶段和所有后续阶段
                    blockedSettleNums.addAll(eventMismatchOrDeleteSettleNums);
                    blockedSettleNums.addAll(subsequentSettleNums);
                    
                    log.info("linkId::{}::事件类型{}检测到5/15分钟阶段数据不匹配或删除事件，将卡住当前及后续阶段: 当前={}, 后续={}", 
                            linkedId, eventCode, eventMismatchOrDeleteSettleNums, subsequentSettleNums);
                }
            }

            // 过滤需要卡住的阶段
            for (MatchSettleScore score : matchSettleScores) {
                String settleNum = score.getSettleNum();

                // 非5/15分钟阶段，直接通过
                if (!is5Or15MinPeriod(settleNum)) {
                    filteredScores.add(score);
                    continue;
                }

                // 检查是否需要卡住
                if (blockedSettleNums.contains(settleNum)) {
                    log.info("linkId::{}::5/15分钟阶段{}需要卡住（数据不匹配或删除事件）", linkedId, settleNum);
                    continue; // 卡住该阶段
                }

                // 所有检查通过，保留该阶段
                filteredScores.add(score);
            }

            return filteredScores;
        } catch (Exception e) {
            log.error("linkId::{}::过滤5/15分钟阶段失败", linkedId, e);
            return matchSettleScores; // 异常时返回原列表
        }
    }

    /**
     * 获取数据不一致阶段的下一个阶段的settleNum集合
     * 只在同一个半场内判断下一个阶段，不跨半场
     * @param mismatchSettleNums 数据不一致的settleNum集合
     * @return 下一个阶段的settleNum集合
     */
    private Set<String> getNextPhaseSettleNums(Set<String> mismatchSettleNums) {
        Set<String> nextPhaseSettleNums = new HashSet<>();
        Map<String, String> allPhases = MatchPeriodEnum.allNextPhases;
        for (String settleNum : mismatchSettleNums) {
            if (allPhases.containsKey(settleNum)) {
                String nextPhase = allPhases.get(settleNum);
                if (nextPhase != null) {
                    nextPhaseSettleNums.add(nextPhase);
                }
            }
        }

        return nextPhaseSettleNums;
    }

    /**
     * 获取所有后续的5/15分钟阶段
     * 当出现5/15分钟比分不一致时，需要卡住所有后续的5/15分钟阶段
     * @param standardMatchId 标准赛事ID
     * @param mismatchSettleNums 数据不一致的settleNum集合
     * @return 所有后续的5/15分钟阶段的settleNum集合
     */
    private Set<String> getAllSubsequent5Or15MinPeriods(Long standardMatchId, Set<String> mismatchSettleNums) {
        Set<String> subsequentSettleNums = new HashSet<>();
        
        try {
            // 合并所有5分钟和15分钟阶段的settleNum列表（保持原始顺序）
            List<String> all5Or15MinSettleNums = new ArrayList<>();
            all5Or15MinSettleNums.addAll(ALL_5_SETTLE_NUMS);
            all5Or15MinSettleNums.addAll(ALL_15_SETTLE_NUMS);
            
            // 查询数据库中该赛事所有5/15分钟阶段的比分（用于确定哪些阶段实际存在）
            List<MatchSettleScore> all5Or15MinScores = matchSettleScoreRepository.getModelsByItems(
                    standardMatchId, null, null, null, null, null);
            Set<String> existingSettleNums = all5Or15MinScores.stream()
                    .filter(score -> is5Or15MinPeriod(score.getSettleNum()))
                    .map(MatchSettleScore::getSettleNum)
                    .collect(Collectors.toSet());
            
            // 找到数据不一致阶段的位置，然后收集所有后续的5/15分钟阶段
            for (String mismatchSettleNum : mismatchSettleNums) {
                int mismatchIndex = all5Or15MinSettleNums.indexOf(mismatchSettleNum);
                if (mismatchIndex >= 0) {
                    // 收集该位置之后的所有5/15分钟阶段（只包含数据库中实际存在的阶段）
                    for (int i = mismatchIndex + 1; i < all5Or15MinSettleNums.size(); i++) {
                        String subsequentSettleNum = all5Or15MinSettleNums.get(i);
                        // 只添加数据库中实际存在的阶段
                        if (existingSettleNums.contains(subsequentSettleNum)) {
                            subsequentSettleNums.add(subsequentSettleNum);
                        }
                    }
                } else {
                    // 如果当前阶段不在预定义列表中，基于settleNum的数值大小来判断后续阶段
                    // 这种情况比较少见，但为了安全起见，我们仍然处理
                    log.warn("settleNum {} 不在预定义的5/15分钟阶段列表中，基于数值大小判断后续阶段", mismatchSettleNum);
                    try {
                        int mismatchNum = Integer.parseInt(mismatchSettleNum);
                        for (String settleNum : existingSettleNums) {
                            try {
                                int settleNumInt = Integer.parseInt(settleNum);
                                if (settleNumInt > mismatchNum && is5Or15MinPeriod(settleNum)) {
                                    subsequentSettleNums.add(settleNum);
                                }
                            } catch (NumberFormatException e) {
                                // 忽略无法解析为数字的settleNum
                            }
                        }
                    } catch (NumberFormatException e) {
                        log.warn("无法将settleNum {} 解析为数字", mismatchSettleNum);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取所有后续的5/15分钟阶段失败", e);
        }
        
        return subsequentSettleNums;
    }

    /**
     * 从mention status中一次性获取所有事件类型（进球、角球、罚牌）的删除事件和数据不一致信息
     * @param standardMatchId 标准赛事ID
     * @param linkedId 链路ID
     * @param deleteStatusMap 输出参数：scoreEventId到删除状态的映射
     * @param dataMismatchMap 输出参数：scoreEventId到数据不一致状态的映射
     */
    private void getAllDeleteAndMismatchStatusFromMentionStatus(Long standardMatchId, String linkedId,
                                                                Map<String, Integer> deleteStatusMap,
                                                                Map<String, Integer> dataMismatchMap) {
        try {
            // 获取所有mention status
            MentionQueryRequest queryRequest = new MentionQueryRequest();
            queryRequest.setMatchId(standardMatchId);
            Map<String, AbstractMentionStatus> mentionStatusMap = mentionStatusHelper.getAllMentionStatus(queryRequest);
            
            if (MapUtils.isEmpty(mentionStatusMap)) {
                return;
            }

            // 获取所有事件类型的删除事件和数据不一致信息：goal, corner, fa_card
            List<SettleEventCodeEnum> eventCodeEnums = Arrays.asList(
                    SettleEventCodeEnum.FOOTBALL_GOAL,
                    SettleEventCodeEnum.FOOTBALL_CORNER,
                    SettleEventCodeEnum.FOOTBALL_FA_CARD
            );

            // 处理删除事件
            if (mentionStatusMap.containsKey(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue())) {
                AbstractMentionStatus deleteMentionStatus = mentionStatusMap.get(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue());
                if (deleteMentionStatus instanceof FootballMentionStatus) {
                    FootballMentionStatus footballMentionStatus = (FootballMentionStatus) deleteMentionStatus;
                    for (SettleEventCodeEnum eventCodeEnum : eventCodeEnums) {
                        FootballMentionStatus.EventStatus eventStatus = footballMentionStatus.getDetailStatusFieldByEventCode(eventCodeEnum);
                        if (eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
                            for (Map.Entry<String, Object> entry : eventStatus.getDetailStatus().entrySet()) {
                                String key = entry.getKey();
                                Object value = entry.getValue();
                                if (value instanceof Map) {
                                    Map<String, Object> valueMap = (Map<String, Object>) value;
                                    // 5/15分钟过滤：删除事件来自 N01/N02/N03/LS 的不参与卡住逻辑
                                    Object dsCode = valueMap.get("dataSourceCode");
                                    if (dsCode != null && DATA_SOURCE_5_15_IGNORE.contains(String.valueOf(dsCode))) {
                                        continue;
                                    }
                                    Object statusValue = valueMap.get("status");
                                    if (statusValue instanceof Integer) {
                                        deleteStatusMap.put(key, (Integer) statusValue);
                                    }
                                } else if (value instanceof Integer) {
                                    deleteStatusMap.put(key, (Integer) value);
                                }
                            }
                        }
                    }
                }
            }

            // 处理数据不一致
            if (mentionStatusMap.containsKey("dataMismatchStatus")) {
                AbstractMentionStatus mentionStatus = mentionStatusMap.get("dataMismatchStatus");
                if (mentionStatus instanceof FootballMentionStatus) {
                    FootballMentionStatus footballMentionStatus = (FootballMentionStatus) mentionStatus;
                    for (SettleEventCodeEnum eventCodeEnum : eventCodeEnums) {
                        FootballMentionStatus.EventStatus eventStatus = footballMentionStatus.getDetailStatusFieldByEventCode(eventCodeEnum);
                        if (eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
                            for (Map.Entry<String, Object> detailEntry : eventStatus.getDetailStatus().entrySet()) {
                                String detailKey = detailEntry.getKey();
                                Object detailValue = detailEntry.getValue();
                                if (detailValue instanceof Integer) {
                                    dataMismatchMap.put(detailKey, (Integer) detailValue);
                                } else if (detailValue instanceof Map) {
                                    Map<String, Object> valueMap = (Map<String, Object>) detailValue;
                                    Object statusValue = valueMap.get("status");
                                    if (statusValue instanceof Integer) {
                                        dataMismatchMap.put(detailKey, (Integer) statusValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            log.info("linkId::{}::从mention status获取删除事件和数据不一致信息，删除事件:{}条，数据不一致:{}条", 
                    linkedId, deleteStatusMap.size(), dataMismatchMap.size());
        } catch (Exception e) {
            log.error("linkId::{}::从mention status获取删除事件和数据不一致信息失败", linkedId, e);
        }
    }
    /**
     * 根据事件类型获取后续的5/15分钟阶段
     * @param standardMatchId 标准赛事ID
     * @param eventCode 事件类型（goal、corner、fa_card）
     * @param mismatchOrDeleteSettleNums 数据不匹配或删除事件的settleNum集合
     * @return 所有后续的5/15分钟阶段的settleNum集合
     */
    private Set<String> getSubsequent5Or15MinPeriodsByEvent(Long standardMatchId, String eventCode, 
                                                              Set<String> mismatchOrDeleteSettleNums) {
        Set<String> subsequentSettleNums = new HashSet<>();
        
        try {
            // 根据事件类型确定对应的5/15分钟阶段列表
            List<String> event5Or15MinSettleNums = getEvent5Or15MinSettleNums(eventCode);
            if (event5Or15MinSettleNums.isEmpty()) {
                log.warn("linkId::{}::事件类型{}没有对应的5/15分钟阶段列表", standardMatchId, eventCode);
                return subsequentSettleNums;
            }
            
            // 根据MatchPeriodEnum的settleOrder对阶段进行排序
            List<String> orderedSettleNums = event5Or15MinSettleNums.stream()
                    .sorted((s1, s2) -> {
                        MatchPeriodEnum enum1 = MatchPeriodEnum.getEnum(s1);
                        MatchPeriodEnum enum2 = MatchPeriodEnum.getEnum(s2);
                        if (enum1 == null || enum2 == null) {
                            return 0;
                        }
                        Integer order1 = enum1.getSettleOrder();
                        Integer order2 = enum2.getSettleOrder();
                        if (order1 == null || order2 == null) {
                            return 0;
                        }
                        return order1.compareTo(order2);
                    })
                    .collect(Collectors.toList());
            
            // 找到数据不匹配或删除事件的阶段位置，然后收集所有后续阶段
            for (String mismatchSettleNum : mismatchOrDeleteSettleNums) {
                int mismatchIndex = orderedSettleNums.indexOf(mismatchSettleNum);
                if (mismatchIndex >= 0) {
                    // 收集该位置之后的所有阶段
                    for (int i = mismatchIndex + 1; i < orderedSettleNums.size(); i++) {
                        subsequentSettleNums.add(orderedSettleNums.get(i));
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取事件类型{}的后续5/15分钟阶段失败", eventCode, e);
        }
        
        return subsequentSettleNums;
    }
    
    /**
     * 根据事件类型获取对应的5/15分钟阶段列表
     * @param eventCode 事件类型（goal、corner、fa_card）
     * @return 5/15分钟阶段列表
     */
    private List<String> getEvent5Or15MinSettleNums(String eventCode) {
        List<String> result = new ArrayList<>();
        
        if ("goal".equals(eventCode)) {
            // 进球：5分钟阶段 + 15分钟阶段
            result.addAll(ALL_5_SETTLE_NUMS);
            result.addAll(Arrays.asList("102", "103", "104", "106", "107", "108"));
        } else if ("corner".equals(eventCode)) {
            // 角球：15分钟阶段
            result.addAll(Arrays.asList("2011", "2012", "2013", "2014", "2015", "2016"));
        } else if ("fa_card".equals(eventCode) || "yellow_card".equals(eventCode) || "red_card".equals(eventCode)) {
            // 罚牌：15分钟阶段
            result.addAll(Arrays.asList("301", "302", "303", "305", "306", "307"));
        }
        
        return result;
    }

    /**
     * 过滤checkinfo数据：检查数据源的维护状态、连接状态、单数据源结算开关和15/5分钟开关
     * 只有在拉取checkinfo数据时才判断，用于决定数据源是否应该参与结算
     * 对于5/15分钟阶段，如果只有一个数据源的checkinfo，需要检查所有开关是否都打开
     * @param checkInfos checkinfo列表
     * @param standardMatchId 标准赛事ID
     * @param scoreEventIdSettleNumMap scoreEventId到settleNum的映射
     * @param linkedId 链路ID
     * @param skipConnectionCheckForPa 本批首条为 PA 时，5/15 分钟阶段仅跳过「断连」校验；心跳开关关闭仍过滤（仍过滤维护期）
     * @return 过滤后的checkinfo列表
     */
    private List<MatchSettleCheckInfo> filterCheckInfosByMaintenanceAndConnection(List<MatchSettleCheckInfo> checkInfos,
                                                                                    Long standardMatchId,
                                                                                    Map<Long, String> scoreEventIdSettleNumMap,
                                                                                    String linkedId,
                                                                                    boolean skipConnectionCheckForPa) {
        try {
            List<MatchSettleCheckInfo> filteredCheckInfos = new ArrayList<>();
            Integer tournamentLevel = dataSourceHeartbeatService.getTournamentLevel(standardMatchId);
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo == null) {
                log.warn("linkId::{}::无法获取赛事信息，跳过checkinfo过滤", linkedId);
                return checkInfos;
            }
            Long sportId = standardMatchInfo.getSportId();

            // 获取模板（用于查询15/5分钟开关）
            MatchSettleTemplate weightTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);

            // 获取单数据源结算开关
            List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getModelBySportIdAndDataSource(sportId, null,null);
            Map<String, MatchSettleDataSourceSwitch> switchMap = switches.stream()
                    .collect(Collectors.toMap(MatchSettleDataSourceSwitch::getDataSourceCode, Function.identity(), (v1, v2) -> v1));

            // 构建模板中数据源的15/5分钟开关映射（数据源 -> singleDataSourceSettle开关值）
            Map<String, Integer> templateSingleDataSourceSettleMap = new HashMap<>();
            if (weightTemplate != null && weightTemplate.getTemplateJson() != null && tournamentLevel != null) {
                try {
                    List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos =
                            SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(weightTemplate.getTemplateJson());
                    for (DataSourceSettleWeightDto dto : dataSourceSettleWeightDtos) {
                        if (dto.getSingleDatasourceSettleSwitch() != null) {
                            templateSingleDataSourceSettleMap.put(dto.getDataSourceCode(), dto.getSingleDatasourceSettleSwitch());
                        }
                    }
                } catch (Exception e) {
                    log.error("linkId::{}::解析模板JSON失败", linkedId, e);
                }
            }

            // 按阶段分组checkinfo
            Map<Long, List<MatchSettleCheckInfo>> checkInfosByScoreEventId = checkInfos.stream()
                    .collect(Collectors.groupingBy(MatchSettleCheckInfo::getSettleScoreEventId));

            for (Map.Entry<Long, List<MatchSettleCheckInfo>> entry : checkInfosByScoreEventId.entrySet()) {
                Long scoreEventId = entry.getKey();
                List<MatchSettleCheckInfo> stageCheckInfos = entry.getValue();
                String settleNum = scoreEventIdSettleNumMap.get(scoreEventId);
                
                Set<String> originalDataSourceCodes = stageCheckInfos.stream()
                        .map(MatchSettleCheckInfo::getDataSourceCode)
                        .collect(Collectors.toSet());
                log.info("linkId::{}::scoreEventId:{} settleNum:{} 开始过滤，原始数据源:{}", 
                        linkedId, scoreEventId, settleNum, originalDataSourceCodes);

                // 只对5/15分钟阶段进行特殊判断
                if (!is5Or15MinPeriod(settleNum)) {
                    // 非5/15分钟阶段，直接通过（不检查维护状态和连接状态）
                    filteredCheckInfos.addAll(stageCheckInfos);
                    log.info("linkId::{}::scoreEventId:{} settleNum:{} 非5/15分钟阶段，直接通过，保留{}个checkinfo", 
                            linkedId, scoreEventId, settleNum, stageCheckInfos.size());
                    continue;
                }

                // 5/15分钟阶段：先过滤维护与心跳/连接（PA 批次仅放宽断连，心跳关仍剔除）
                List<MatchSettleCheckInfo> validCheckInfos = new ArrayList<>();
                for (MatchSettleCheckInfo checkInfo : stageCheckInfos) {
                    if (shouldIncludeCheckInfo(checkInfo, standardMatchId, tournamentLevel, linkedId, skipConnectionCheckForPa)) {
                        validCheckInfos.add(checkInfo);
                    } else {
                        log.info("linkId::{}::scoreEventId:{} settleNum:{} 数据源{}被过滤（维护状态或断连）", 
                                linkedId, scoreEventId, settleNum, checkInfo.getDataSourceCode());
                    }
                }
                
                Set<String> validDataSourceCodes = validCheckInfos.stream()
                        .map(MatchSettleCheckInfo::getDataSourceCode)
                        .collect(Collectors.toSet());
                log.info("linkId::{}::scoreEventId:{} settleNum:{} 维护/连接状态过滤后，有效数据源:{}，数量:{}", 
                        linkedId, scoreEventId, settleNum, validDataSourceCodes, validCheckInfos.size());

                // 判断该阶段是否只有一个数据源
                if (validCheckInfos.isEmpty()) {
                    log.info("linkId::{}::scoreEventId:{} settleNum:{} 5/15分钟阶段，维护/连接状态过滤后无有效checkinfo", 
                            linkedId, scoreEventId, settleNum);
                    continue;
                }

                if (validDataSourceCodes.size() == 1) {
                    // 只有一个数据源，需要检查单数据源结算开关和15/5分钟开关
                    String dataSourceCode = validDataSourceCodes.iterator().next();
                    MatchSettleCheckInfo singleCheckInfo = validCheckInfos.get(0);

                    // PA是人工结算，一定准确，不需要检查开关，直接保留
                    if ("PA".equals(dataSourceCode)) {
                        filteredCheckInfos.addAll(validCheckInfos);
                        log.info("linkId::{}::scoreEventId:{} settleNum:{} 数据源PA，直接保留", 
                                linkedId, scoreEventId, settleNum);
                        continue;
                    }

                    // 1. 检查单数据源结算开关（全局开关）
                    MatchSettleDataSourceSwitch dataSourceSwitch = switchMap.get(dataSourceCode);
                    if (dataSourceSwitch == null || dataSourceSwitch.getSingleDataSourceSettle() == null
                            || dataSourceSwitch.getSingleDataSourceSettle() != 1) {
                        log.info("linkId::{}::scoreEventId:{} settleNum:{} 数据源{}的单数据源结算开关未打开，移除5/15分钟阶段的checkinfo",
                                linkedId, scoreEventId, settleNum, dataSourceCode);
                        continue; // 开关未打开，移除该checkinfo
                    }

                    // 2. 检查当前数据源以及当前联赛等级的15/5分钟开关（模板中的开关）
                    Integer templateSwitch = templateSingleDataSourceSettleMap.get(dataSourceCode);
                    if (templateSwitch == null || templateSwitch != 1) {
                        log.info("linkId::{}::scoreEventId:{} settleNum:{} 数据源{}在联赛等级{}的15/5分钟单数据源结算开关未打开，移除5/15分钟阶段的checkinfo",
                                linkedId, scoreEventId, settleNum, dataSourceCode, tournamentLevel);
                        continue; // 模板开关未打开，移除该checkinfo
                    }

                    // 所有开关都打开，保留该checkinfo
                    filteredCheckInfos.add(singleCheckInfo);
                    log.info("linkId::{}::scoreEventId:{} settleNum:{} 数据源{}所有开关都打开，保留checkinfo", 
                            linkedId, scoreEventId, settleNum, dataSourceCode);
                } else {
                    // 多个数据源，不需要检查开关，直接保留所有有效的checkinfo
                    filteredCheckInfos.addAll(validCheckInfos);
                    log.info("linkId::{}::scoreEventId:{} settleNum:{} 多个数据源（{}个），直接保留所有有效checkinfo，数量:{}", 
                            linkedId, scoreEventId, settleNum, validDataSourceCodes.size(), validCheckInfos.size());
                }
            }

            return filteredCheckInfos;
        } catch (Exception e) {
            log.error("linkId::{}::过滤checkinfo数据失败", linkedId, e);
            return checkInfos; // 异常时返回原列表
        }
    }

    /**
     * 判断checkinfo是否应该被包含（维护期、心跳开关、连接状态）
     * @param skipConnectionCheckForPa PA 触发批次时为 true：心跳开时跳过断连校验；心跳关仍排除
     * @return true表示应该包含
     */
    private boolean shouldIncludeCheckInfo(MatchSettleCheckInfo checkInfo, Long standardMatchId,
                                          Integer tournamentLevel, String linkedId,
                                          boolean skipConnectionCheckForPa) {
        String dataSourceCode = checkInfo.getDataSourceCode();

        // PA是人工结算，一定准确，不需要过滤
        if ("PA".equals(dataSourceCode)) {
            return true;
        }

        // 1. 检查数据源是否在维护状态
        IDataSourceHeartbeatService.DataSourceMaintenanceTime maintenanceTime = dataSourceHeartbeatService.getMaintenanceTime(dataSourceCode);
        if (maintenanceTime != null && maintenanceTime.isInMaintenanceTime(System.currentTimeMillis())) {
            log.info("linkId::{}::数据源{}处于维护状态，跳过checkinfo", linkedId, dataSourceCode);
            return false; // 在维护状态，该数据源不参与结算
        }

        // 2. 心跳开关与连接状态（心跳关一律剔除；PA 批次在心跳开时跳过断连校验）
        if (tournamentLevel != null) {
            // 先检查心跳开关是否开启
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo != null) {
                // 2.1 先根据标准赛事级可用数据源列表进行过滤（由 MATCH_OPERATE_MSG 维护）
                Set<String> matchDataSources = getMatchDataSourcesFromRedis(standardMatchId, linkedId);
                if (matchDataSources != null && !matchDataSources.isEmpty() && !matchDataSources.contains(dataSourceCode)) {
                    log.info("linkId::{}::数据源{}不在标准赛事{}的可用数据源列表中，跳过checkinfo", linkedId, dataSourceCode, standardMatchId);
                    return false;
                }

                List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getMatchSettleDataSourceSwitchByRedis(
                        standardMatchInfo.getSportId(), dataSourceCode);
                boolean heartbeatSwitchEnabled = false;
                if (switches != null && !switches.isEmpty()) {
                    MatchSettleDataSourceSwitch switchConfig = switches.get(0);
                    // dataSourceHeartbeat 为 1 表示开启，0 表示关闭
                    heartbeatSwitchEnabled = switchConfig.getDataSourceHeartbeat() != null 
                            && switchConfig.getDataSourceHeartbeat() == SettleTemplateTypeEnum.ON_CODE.code;
                }
                
                // 心跳关：始终不参与（与是否 PA 批次无关）
                if (!heartbeatSwitchEnabled) {
                    log.info("linkId::{}::数据源{}心跳开关关闭，不参与结算，跳过checkinfo", linkedId, dataSourceCode);
                    return false;
                }
                // 心跳开：PA 批次仅跳过断连校验；否则断连剔除
                if (skipConnectionCheckForPa) {
                    log.debug("linkId::{}::数据源{}PA触发批次，跳过断连校验，保留checkinfo", linkedId, dataSourceCode);
                    return true;
                }
                Boolean connectionStatus = dataSourceHeartbeatService.getMatchConnectionStatus(standardMatchId, dataSourceCode);
                if (connectionStatus == null || !connectionStatus) {
                    log.info("linkId::{}::数据源{}断连，跳过checkinfo", linkedId, dataSourceCode);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断是否是5分钟或15分钟阶段
     * @param settleNum 结算编号
     * @return true表示是5/15分钟阶段
     */
    private boolean is5Or15MinPeriod(String settleNum) {
        if (settleNum == null) {
            return false;
        }
        // 5分钟阶段
        if (ALL_5_SETTLE_NUMS.contains(settleNum)) {
            return true;
        }
        // 15分钟阶段
        if (ALL_15_SETTLE_NUMS.contains(settleNum)) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有可利用的数据源（维护期外、心跳开启；已连接；PA 触发 5/15 时仅放宽断连、心跳关仍不加入）
     * @param standardMatchId 标准赛事ID
     * @param eventCode 事件编码（goal/corner/booking）
     * @param sportId 球种ID
     * @param tournamentLevel 联赛等级
     * @param linkedId 链路ID
     * @param skipConnectionCheckForPa 当前核对为 PA 触发时，5/15 分钟仅跳过断连校验加入列表；心跳关仍不加入
     * @return 可利用的数据源列表
     */
    private List<String> getAvailableDataSources(Long standardMatchId, String eventCode, Long sportId,
                                                   Integer tournamentLevel, String linkedId,
                                                   boolean skipConnectionCheckForPa) {
        List<String> availableDataSources = new ArrayList<>();
        
        try {
            // 0. 从Redis获取该标准赛事下可用的数据源列表（由 MATCH_OPERATE_MSG 消费后写入）
            Set<String> matchDataSources = getMatchDataSourcesFromRedis(standardMatchId, linkedId);

            // 1. 获取所有数据源开关配置
            List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getModelBySportIdAndDataSource(sportId, null, null);
            if (switches == null || switches.isEmpty()) {
                log.warn("linkId::{}::无法获取数据源开关配置", linkedId);
                return availableDataSources;
            }
            
            // 2. 获取模板中的15/5分钟开关配置
//            MatchSettleTemplate weightTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
//            Map<String, Integer> templateSingleDataSourceSettleMap = new HashMap<>();
//            if (weightTemplate != null && weightTemplate.getTemplateJson() != null && tournamentLevel != null) {
//                try {
//                    List<DataSourceSettleWeightDto> dataSourceSettleWeightDtos =
//                            SettleTemplateJsonUtils.tansferDataSourceSettleWeightDtoList(weightTemplate.getTemplateJson());
//                    for (DataSourceSettleWeightDto dto : dataSourceSettleWeightDtos) {
//                        if (dto.getSingleDatasourceSettleSwitch() != null) {
//                            templateSingleDataSourceSettleMap.put(dto.getDataSourceCode(), dto.getSingleDatasourceSettleSwitch());
//                        }
//                    }
//                } catch (Exception e) {
//                    log.error("linkId::{}::解析模板JSON失败", linkedId, e);
//                }
//            }
            
            // 3. 根据事件类型筛选数据源
            for (MatchSettleDataSourceSwitch dataSourceSwitch : switches) {
                String dataSourceCode = dataSourceSwitch.getDataSourceCode();

                // 如果配置了赛事级可用数据源列表，则优先按该列表过滤
                if (matchDataSources != null && !matchDataSources.isEmpty() && !matchDataSources.contains(dataSourceCode)) {
                    log.debug("linkId::{}::数据源{}不在标准赛事{}的可用数据源列表中，跳过", linkedId, dataSourceCode, standardMatchId);
                    continue;
                }
                // 5/15分钟：N01、N02、N03、LS 不参与结算，不加入可利用数据源
                if (DATA_SOURCE_5_15_IGNORE.contains(dataSourceCode)) {
                    continue;
                }
                
                // 检查该数据源是否支持当前事件类型
                boolean supportsEvent = false;
                if ("corner".equals(eventCode)) {
                    supportsEvent = dataSourceSwitch.getCorner() != null && dataSourceSwitch.getCorner() == 1;
                } else if ("goal".equals(eventCode) || "kick_off".equals(eventCode) || "score_change".equals(eventCode)) {
                    supportsEvent = dataSourceSwitch.getGoal() != null && dataSourceSwitch.getGoal() == 1;
                } else if ("yellow_card".equals(eventCode) || "red_card".equals(eventCode) || "fa_card".equals(eventCode)) {
                    supportsEvent = dataSourceSwitch.getBooking() != null && dataSourceSwitch.getBooking() == 1;
                }
                
                if (!supportsEvent) {
                    continue;
                }
                
                // PA是人工结算，不需要等待，不添加到可利用数据源列表中
                // PA的checkinfo仍然会参与结算判断（一致性检查），但不影响等待逻辑
                if ("PA".equals(dataSourceCode)) {
                    continue;
                }
//
//                // 4. 检查15/5分钟开关（模板中的开关）
//                // 注意：单数据源结算开关（全局开关）不影响数据源是否可利用，只影响结算判断
//                Integer templateSwitch = templateSingleDataSourceSettleMap.get(dataSourceCode);
//                if (templateSwitch == null || templateSwitch != 1) {
//                    log.debug("linkId::{}::数据源{}在联赛等级{}的15/5分钟单数据源结算开关未打开", linkedId, dataSourceCode, tournamentLevel);
//                    continue;
//                }
                
                // 5. 检查维护状态
                IDataSourceHeartbeatService.DataSourceMaintenanceTime maintenanceTime = dataSourceHeartbeatService.getMaintenanceTime(dataSourceCode);
                if (maintenanceTime != null && maintenanceTime.isInMaintenanceTime(System.currentTimeMillis())) {
                    log.debug("linkId::{}::数据源{}处于维护状态", linkedId, dataSourceCode);
                    continue;
                }
                
                // 6. 心跳关：始终不加入；心跳开：PA 触发时跳过断连否则校验连接
                boolean heartbeatSwitchEnabled = dataSourceSwitch.getDataSourceHeartbeat() != null
                        && dataSourceSwitch.getDataSourceHeartbeat() == SettleTemplateTypeEnum.ON_CODE.code;
                if (!heartbeatSwitchEnabled) {
                    log.debug("linkId::{}::数据源{}心跳开关关闭，不加入可利用数据源", linkedId, dataSourceCode);
                    continue;
                }
                if (skipConnectionCheckForPa) {
                    availableDataSources.add(dataSourceCode);
                    log.debug("linkId::{}::数据源{}PA触发，加入可利用数据源（仅跳过断连）", linkedId, dataSourceCode);
                    continue;
                }
                Boolean connectionStatus = dataSourceHeartbeatService.getMatchConnectionStatus(standardMatchId, dataSourceCode);
                if (connectionStatus == null || !connectionStatus) {
                    log.debug("linkId::{}::数据源{}断连", linkedId, dataSourceCode);
                    continue;
                }

                availableDataSources.add(dataSourceCode);
            }
            
            log.info("linkId::{}::事件类型{}的可利用数据源:{}", linkedId, eventCode, availableDataSources);
            return availableDataSources;
            
        } catch (Exception e) {
            log.error("linkId::{}::获取可利用数据源失败", linkedId, e);
            return availableDataSources;
        }
    }

    /**
     * 从 Redis 获取某标准赛事下配置的可用数据源列表
     * 该数据由融合服务在 MATCH_OPERATE_MSG 消费后写入
     *
     * @param standardMatchId 标准赛事ID
     * @param linkedId 链路ID
     * @return 可用数据源编码集合，若未配置或读取失败则返回 null 表示不做过滤
     */
    private Set<String> getMatchDataSourcesFromRedis(Long standardMatchId, String linkedId) {
        try {
            String key = CommonConstant.SETTLE_MATCH_DATASOURCES + standardMatchId;
            Object value = redisService.get(key);
            if (value == null) {
                return null;
            }

            if (value instanceof Collection) {
                return ((Collection<?>) value).stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.toSet());
            }

            String json = String.valueOf(value);
            if (StringUtils.isBlank(json)) {
                return null;
            }

            try {
                List<String> list = JSON.parseArray(json, String.class);
                if (CollectionUtils.isEmpty(list)) {
                    return null;
                }
                return new HashSet<>(list);
            } catch (Exception ex) {
                log.warn("linkId::{}::解析标准赛事可用数据源失败, key:{}, raw:{}", linkedId, key, json, ex);
                return null;
            }
        } catch (Exception e) {
            log.error("linkId::{}::从Redis获取标准赛事可用数据源失败, standardMatchId:{}", linkedId, standardMatchId, e);
            return null;
        }
    }
}