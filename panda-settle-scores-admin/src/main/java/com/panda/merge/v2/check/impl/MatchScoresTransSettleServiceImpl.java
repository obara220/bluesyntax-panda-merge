package com.panda.merge.v2.check.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.constant.SettleTemplateTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.filter.football.impl.MatchPenaltyEventSettleInitFilter;
import com.panda.merge.filter.football.impl.MatchScoresSettleInitChainFilter;
import com.panda.merge.model.*;
import com.panda.merge.respository.MatchEventInfoRepository;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.service.impl.GrayIntervalService;
import com.panda.merge.service.settleMention.service.SettleMentionFactory;
import com.panda.merge.utils.*;
import com.panda.merge.v2.check.IMatchScoresTransSettleService;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE;
import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.NOT_CONFIRM;
import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.NOT_EDIT;
import static com.panda.merge.constant.RepositoryConstant.MATCH_EVENT_INFO;

@Service
@Slf4j
public class MatchScoresTransSettleServiceImpl implements IMatchScoresTransSettleService {

    @Autowired
    MatchScoresSettleInitChainFilter matchScoresSettleInitChainFilter;
    @Autowired
    MatchPenaltyEventSettleInitFilter matchPenaltyEventSettleInitFilter;
    @Autowired
    RedisService redisService;
    @Autowired
    StandardSportPlayerService standardSportPlayerService;
    @Autowired
    IMatchSettleService matchSettleService;

    @Autowired
    IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    MatchSettleDataSourceConfigService matchSettleDataSourceConfigService;
    @Autowired
    GrayIntervalService grayIntervalService;
    @Autowired
    ISettleTemplateService settleTemplateService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    SettleMentionFactory settleMentionFactory;
    @Autowired
    private IMatchSettleThirdScoreService matchSettleThirdScoreService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    @Autowired
    MatchEventInfoRepository matchEventInfoRepository;
    @Autowired
    MatchSettleFactorCheckInfoRepository matchSettleFactoryCheckInfoRepository;
    @Autowired
    com.panda.merge.v2.service.IMatchSettleCheckInfoService matchSettleCheckInfoService;
    @Autowired
    private MatchSettleBatchCheckServiceHelper matchSettleBatchCheckServiceHelper;
    @Autowired
    private MatchSettleEventV2Repository matchSettleEventV2Repository;
    @Autowired
    private MatchSettleDataSourceSwitchRepository matchSettleDataSourceSwitchRepository;
    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreV2Repository;
    @Autowired
    private MatchSettleThirdEventRepository matchSettleThirdEventRepository;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreV2Repository;
    @Autowired
    private GrayPhaseEventHelper grayPhaseEventHelper;
    
    @Autowired
    private com.panda.merge.service.IDataSourceHeartbeatService dataSourceHeartbeatService;

    private static final String SCORE_EVENT="SCORE_EVENT:";
    
    // 5分钟阶段的settleNum范围
    private static final List<String> FIVE_MIN_PERIODS = Arrays.asList("6005", "6010", "6015", "6020", "6025", "6030", "6035", "6040", "6045", 
                                                                        "7050", "7055", "7060", "7065", "7070", "7075", "7080", "7085", "7090");
    // 15分钟阶段的settleNum范围
    private static final List<String> FIFTEEN_MIN_PERIODS = Arrays.asList("60899", "61799", "62699", "73599", "74499", "75399");

    @Override
    public void tansforScoreSettle(CommonThirdScoresDto data, boolean isStandard) {
        long start =System.currentTimeMillis();
        log.info("linkId::{}::transferScoreSettle start", data.getLinkedId());
        //1.根据阶段判断生成哪些阶段比分
        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(data.getStandardMatchId());
        data.setStandardMatchInfo(standardMatchInfo);
        //0-15 分钟 开球 15-30 30-45 上半场结束 45-60 60-75 75-90 下半场结束 常规赛全场比分 加时赛上半场结束 加时赛下半场结束 加时赛比分
        List<MatchSettleScore> list =new ArrayList<>();
                    Map<String, Object> allPeriodScores = data.getScores();
            Map<String, FootballScores> footballScoresMap = JsonMapUtils.transferFootBallScore(allPeriodScores);
        matchScoresSettleInitChainFilter.filter(footballScoresMap,data,list);
        //2.根据上面获取的matchsettlescore list 循环查询
        if(list.size()==0){
            log.info("linkId::{}::transferScoreSettle MatchSettleScore为空", data.getLinkedId());
            return;
        }
        
        //常规赛比分
        saveMatchSettleThirdScores(footballScoresMap,list,data);
        //点球大战比分
        savePenaltyEventScores(footballScoresMap,data);
        log.info("linkId::{}::transferScoreSettle end with cost time:{}",data.getLinkedId(),System.currentTimeMillis()-start);
    }
    private void savePenaltyEventScores(Map<String, FootballScores> footballScoresMap, CommonThirdScoresDto data) {
        try {
            //点球大战比分  点球大战比分自动生成前5轮 和总比分
            List<MatchSettleEvent> events = new ArrayList<>();
            matchPenaltyEventSettleInitFilter.filter(footballScoresMap, data, events);
            List<String> scoresSettleNums = events.stream().map(it -> it.getSettleNum()).collect(Collectors.toList());
            for (MatchSettleEvent event : events) {
                //1.查询当前阶段比分
                List<MatchSettleEvent> oldEventList = matchSettleEventV2Repository.getModelByStandardMatchIdAndSettleNums(data.getStandardMatchId(), scoresSettleNums);
                if (oldEventList.size() == 0) {
                    log.error("oldEvent==null：{},matchId:{}", event.getSettleNum(), data.getStandardMatchId());
                    continue;
                }
                MatchSettleEvent oldEvent = oldEventList.get(0);
                //2.根据当前阶段比分状态是否被确认判断是否覆盖数据
                if (oldEvent.getStatus() == 3) {
                    continue;
                }
                //3.如果否则覆盖比分
                oldEvent.setModifyTime(System.currentTimeMillis());
                //事件时间计算
                if (oldEvent.getEventTime() != null && oldEvent.getEventTime() > data.getMatchEventInfo().getEventTime()) {
                    oldEvent.setEventTime(data.getMatchEventInfo().getEventTime());
                }else {
                    oldEvent.setEventTime(data.getMatchEventInfo().getEventTime());
                }
                matchSettleEventV2Repository.updateById(oldEvent);
                //更新入库
            }
        }catch (Exception e){
            log.error("MatchScoresTransSettleServiceImpl-savePenaltyEventScores:",e);
        }
    }
    /**
     * 校验当前事件是否为灰色区间
     * */
    public CheckIsGreyDto checkIsGreyEvent(MatchEventInfo matchEventInfo) {
        CheckIsGreyDto checkIsGreyDto = new CheckIsGreyDto();
        //查询当前数据商灰色区间总开关开启状态,假如关闭则直接返回
        List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getModelBySportIdAndDataSource(matchEventInfo.getSportId(),matchEventInfo.getDataSourceCode(),"1");
        if(switches.isEmpty()){
            checkIsGreyDto.setStandardMatchId(matchEventInfo.getStandardMatchId());
            checkIsGreyDto.setMatchEventInfo(matchEventInfo);
            return checkIsGreyDto;
        }
        //模版查询
        MatchSettleTemplate grayTemplate = settleTemplateService.getTemplateByStandardMatchId(matchEventInfo.getStandardMatchId(), SettleTemplateTypeEnum.GRAY_AREA.code);
        log.info("Template:matchId:{},grayTemplate:{}",matchEventInfo.getStandardMatchId(), grayTemplate);
        MatchSettleTemplate weithtTemplate = settleTemplateService.getTemplateByStandardMatchId(matchEventInfo.getStandardMatchId(), SettleTemplateTypeEnum.DATA_SOURCE_WEIGHT.code);
        log.info("Template:matchId:{},weithtTemplate:{}",matchEventInfo.getStandardMatchId(), weithtTemplate);

        checkIsGreyDto.setStandardMatchId(matchEventInfo.getStandardMatchId());
        try {
            log.info("checkIsGreyEvent的入参:{}", JSON.toJSONString(matchEventInfo));
            // 赛事与联赛的判断
            Long standardMatchId = matchEventInfo.getStandardMatchId();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if ( Objects.isNull(standardMatchInfo) ) {
                log.info("matchId:{} checkIsGreyEvent的赛事不存在", matchEventInfo.getStandardMatchId());
                return checkIsGreyDto;
            }
            StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
            if ( Objects.isNull(standardSportTournament) ) {
                log.info("matchId:{} checkIsGreyEvent的联赛不存在", matchEventInfo.getStandardMatchId());
                return checkIsGreyDto;
            }
            Integer tournamentLevel = standardSportTournament.getTournamentLevel();

            checkIsGreyDto.setStandardMatchId(matchEventInfo.getStandardMatchId());
            if (null == matchEventInfo) {
                log.info("matchId:{} checkIsGreyEvent事件无法获取", matchEventInfo.getStandardMatchId());
                return checkIsGreyDto;
            }

            //1.只处理进球角球类型
            if (matchEventInfo.getEventCode().equals("corner")) {
                return grayIntervalService.checkIsGreyCorner(grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            }else if (matchEventInfo.getEventCode().equals("yellow_card")||matchEventInfo.getEventCode().equals("red_card")||matchEventInfo.getEventCode().equals("fa_card")){
                return grayIntervalService.checkIsGreyFaCard(grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            }else if(matchEventInfo.getEventCode().equals("score_change")) {
                return grayIntervalService.checkIsGreyBasketball(grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            } else if (!matchEventInfo.getEventCode().equals("goal")) {
                return checkIsGreyDto;
            }

            checkIsGreyDto = grayIntervalService.checkDataSourceFiveGray(grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
            if (2 == checkIsGreyDto.getIsGrey()) {
                return checkIsGreyDto;
            }

            checkIsGreyDto = grayIntervalService.checkDataSourceFifteenGray(grayTemplate,weithtTemplate,matchEventInfo, tournamentLevel);
        } catch (Exception   e){
            log.error("matchId:{} MatchScoresTransSettleServiceImpl-checkIsGreyEvent:",matchEventInfo.getStandardMatchId(), e);
            return checkIsGreyDto;
        }
        return checkIsGreyDto;
    }


    //常规赛比分逻辑处理
    private void saveMatchSettleThirdScores(Map<String, FootballScores> footballScoresMap,List<MatchSettleScore> list, CommonThirdScoresDto data) {
        long start =System.currentTimeMillis();
        log.info("linkId::{}::saveMatchSettleThirdScores start", data.getLinkedId());
        //1.判断比分是否有灰色区间
        //2.判断灰色区间的类型是哪种15分钟比分
        //3.给灰色区间的比分设置灰色为1
        CheckIsGreyDto checkIsGreyDto =null;
        // 阶段比分消费时（matchEventInfo为null），足球的灰色区间逻辑需要去除
        // 当matchEventInfo为null时，不会执行灰色区间逻辑（因为需要matchEventInfo才能判断灰色区间）
        // 这里明确处理：如果是阶段比分消费（matchEventInfo为null）且是足球，则跳过灰色区间逻辑

        if(data.getMatchEventInfo()!=null){
            // 阶段比分消费时，足球的灰色区间逻辑需要去除
            // 注意：由于matchEventInfo不为null才能进入此分支，所以这里主要确保逻辑清晰
            // 如果未来有需要，可以在这里添加其他判断条件
            checkIsGreyDto= this.checkIsGreyEvent(data.getMatchEventInfo());
            checkIsGreyDto.setStandardMatchId(data.getStandardMatchId());
            if(checkIsGreyDto.getIsGrey()!=null && checkIsGreyDto.getIsGrey()!=0){
                //3.1 更新临近2个为灰色区间
                checkIsGreyDto.setScoresGrey(1);
                this.updateGrayMatchSettleScore(checkIsGreyDto,data.getMatchEventInfo().getHomeAway());
            }
        }
        if(checkIsGreyDto!=null){
            checkIsGreyDto.setMatchEventInfo(data.getMatchEventInfo());
        }

        List<String> scoresSettleNums=list.stream().map(MatchSettleScore::getSettleNum).collect(Collectors.toList());
        log.info("linkId::{}::saveMatchSettleThirdScores scoresSettleNums:{}", data.getLinkedId(), scoresSettleNums);
        List<MatchSettleThirdScore> oldScoresList =matchSettleThirdScoreV2Repository.getByMatchIdAndAndDataSourceCodeSettleNum(null,data.getThirdMatchId(),data.getDataSourceCode(),scoresSettleNums);
        Map<String,MatchSettleThirdScore>  oldScoresMap = oldScoresList.stream().collect(Collectors.toMap(MatchSettleThirdScore::getSettleNum, Function.identity(), (v1, v2) -> v1));

        List<MatchSettleThirdScore> batchSaveThirdScore = new ArrayList<>();
        List<MatchSettleThirdScore> allThirdScore = new ArrayList<>();
        for (MatchSettleScore matchSettleScore : list) {
            MatchSettleThirdScore oldScore= oldScoresMap.get(matchSettleScore.getSettleNum());
            if(oldScore==null){
                oldScore=new MatchSettleThirdScore();
                BeanUtils.copyProperties(matchSettleScore,oldScore);
                oldScore.setId(IdGenerator.nextId());
                oldScore.setCreateTime(System.currentTimeMillis());
                oldScore.setStandardMatchId(data.getStandardMatchId());
                oldScore.setThirdMatchId(data.getThirdMatchId());
                oldScore.setOperater(data.getUserName());
                oldScore.setDataSourceCode(data.getDataSourceCode());
                batchSaveThirdScore.add(oldScore);
            } else if(oldScore.getT1()==null || oldScore.getT2()==null || !oldScore.getT1().equals(matchSettleScore.getT1()) || !oldScore.getT2().equals(matchSettleScore.getT2())){
                oldScore.setT1(matchSettleScore.getT1());
                oldScore.setT2(matchSettleScore.getT2());
                oldScore.setStatus(1);
                oldScore.setModifyTime(System.currentTimeMillis());
                oldScore.setDataSourceCode(data.getDataSourceCode());
                batchSaveThirdScore.add(oldScore);
            }
            allThirdScore.add(oldScore);
        }
        log.info("linkId::{}::saveMatchSettleThirdScores save batchSaveThirdScore with size: {} ", data.getLinkedId(), batchSaveThirdScore.size());
        if (!CollectionUtils.isEmpty(batchSaveThirdScore)) {
            matchSettleThirdScoreService.saveOrUpdateBatch(batchSaveThirdScore);
        }

        //41805 赛事中途切PD,当场赛事之前的数据源不参与结算
        if (data.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
            Object switchPdKey = redisService.get(MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE +":"+ data.getThirdMatchId());
            if (switchPdKey != null) {
                Integer status =0;
                if(switchPdKey.toString().length()>7){
                    status=0;
                }else {
                    status =  Integer.parseInt(switchPdKey.toString());
                }
                if(status==0){
                    log.info("linkId::{}::saveMatchSettleThirdScores 赛事中途切PD", data.getLinkedId());
                    return;
                }
            }
        }
        matchSettleBatchCheckService.batchCheckMatchThirdSettleScores(allThirdScore,data.getLinkedId(),data.getSecondFromStart(),checkIsGreyDto);
        log.info("linkId::{}::saveMatchSettleThirdScores end with cost time:{}",data.getLinkedId(),System.currentTimeMillis()-start);
    }

    public void updateGrayMatchSettleScore(CheckIsGreyDto checkIsGreyDto,String homeAway) {
    try {
        //1.15分钟判断灰色区间isgrey 1
        //2. 5分钟判断灰色区间isgrey 2  其他其他情况返回
        if (checkIsGreyDto.getSettleNum() == null || checkIsGreyDto.getSettleNum().size() == 0) {
            return;
        }

        //灰色区间匹配
        List<MatchSettleScore> list = matchSettleScoreV2Repository.getModelBySettleNumAndMatchIdIdAndStatus(checkIsGreyDto.getSettleNum(),checkIsGreyDto.getStandardMatchId(),null);
        if (null!=list&&!list.isEmpty()) {
            for (MatchSettleScore matchSettleScore : list) {
                matchSettleScore.setIsGrey(1);
                matchSettleScore.setCurrentEventStatus(1);
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScoreV2Repository.updateById(matchSettleScore);
                matchSettleCheckInfoService.rollbackScores(matchSettleScore);

                String fifteenSettleNum = GrayIntervalService.fifteenMinSettleNumMap.get(matchSettleScore.getSettleNum());
                if (!StringUtils.isAnyEmpty(fifteenSettleNum) && checkIsGreyDto.getScoresGrey()!=null && checkIsGreyDto.getScoresGrey().equals(NOT_CONFIRM)) {
                    //需求2592,新增灰色区间时,计算区间的结算因子
                    List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoList = matchSettleFactoryCheckInfoRepository.matchSettleFactorCheckInfoListCaseOne(matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum());
                    if (matchSettleFactorCheckInfoList.isEmpty()) {
                        MatchSettleFactorCheckInfo matchSettleFactorCheckInfo = new MatchSettleFactorCheckInfo();
                        matchSettleFactorCheckInfo.setId(UUIdUtils.getId());
                        matchSettleFactorCheckInfo.setStatus(NOT_EDIT);
                        if (!StringUtils.isAnyEmpty(homeAway)) {
                            if (homeAway.equals(CommUtils.HOME_PARAM)) {
                                matchSettleFactorCheckInfo.setT1(CommUtils.SETTLE_FACTOR);
                                matchSettleFactorCheckInfo.setT2(BigDecimal.ZERO);
                            } else {
                                matchSettleFactorCheckInfo.setT1(BigDecimal.ZERO);
                                matchSettleFactorCheckInfo.setT2(CommUtils.SETTLE_FACTOR);
                            }
                        }
                        matchSettleFactorCheckInfo.setCreateTime(System.currentTimeMillis());
                        matchSettleFactorCheckInfo.setEventTime(matchSettleScore.getEventTime());
                        matchSettleFactorCheckInfo.setSettleNum(matchSettleScore.getSettleNum());
                        matchSettleFactorCheckInfo.setStandardMatchId(matchSettleScore.getStandardMatchId());
                        matchSettleFactorCheckInfo.setSettleScoreEventId(matchSettleScore.getId().toString());
                        matchSettleFactoryCheckInfoRepository.updateMatchSettleFactorCheckInfoToRedis(matchSettleFactorCheckInfo,true);
                    } else {
                        MatchSettleFactorCheckInfo matchSettleFactorCheckInfo = matchSettleFactorCheckInfoList.get(NOT_EDIT);
                        if (!StringUtils.isAnyEmpty(homeAway)) {
                            if (homeAway.equals(CommUtils.HOME_PARAM)) {
                                BigDecimal settleFactorT1 = matchSettleFactorCheckInfo.getT1().add(CommUtils.SETTLE_FACTOR);
                                matchSettleFactorCheckInfo.setT1(settleFactorT1);
                            } else {
                                BigDecimal settleFactorT2 = matchSettleFactorCheckInfo.getT2().add(CommUtils.SETTLE_FACTOR);
                                matchSettleFactorCheckInfo.setT2(settleFactorT2);
                            }
                            matchSettleFactoryCheckInfoRepository.updateMatchSettleFactorCheckInfoToRedis(matchSettleFactorCheckInfo,false);
                        }
                    }
                }

            }
        }
        //灰色区间的时候要判断赛事也是灰色区间
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(checkIsGreyDto.getStandardMatchId());
        matchSettleInfo.setIsGray(1);
        matchSettleInfo.setCurrentEventStatus(1);
        matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
    }catch (Exception e){
        log.error("MatchScoresTransSettleServiceImpl-updateGrayMatchSettleScore:",e);
    }
    }

    /**
     * 转化为结算事件
     * */
    @Override
    @Transactional
    public void tansforEventSettle(MatchEventInfo data ,boolean isStandard) {
        try {
            long start = System.currentTimeMillis();
            log.info("linkId::{}::eventId:{} transferEventSettle 开始处理", data.getLinkId(), data.getThirdEventId());
            //0.阶段过滤
            if (!SportPeriodConstant.FootballPeriod.contans(data.getMatchPeriodId())) {
                return;
            }
            //点球大战不启用数据商
            if(data.getMatchPeriodId().equals(50l)){
                return;
            }
            //eventCode 条件设置
            if (data.getEventCode().equals("yellow_red_card")) {
                data.setEventCode("red_card");
            }
            List<String> eventCodes = new ArrayList<>();
            if (data.getEventCode().equals("goal") || data.getEventCode().equals("penalty_missed")) {
                eventCodes.add("goal");
                if (data.getMatchPeriodId().equals(50l)) {
                    eventCodes.add("penalty_missed");
                }
            } else if (data.getEventCode().equals("corner")) {
                eventCodes.add("corner");
            } else {
                eventCodes.add("yellow_card");
                eventCodes.add("red_card");
            }
            //阶段条件设置
            List<Long> periods = getPeriodByPeriod(data);
            //1.先计算次序
            matchEventInfoRepository.cacheMatchEventInfo(data);
            List<MatchEventInfo> eventInfos = matchEventInfoRepository.getMatchEventInfoCaseTwo(data.getThirdMatchId(),eventCodes,periods,data.getDataSourceCode(),data.getId(),data.getEventTime());
            //删除事件过滤
            List<MatchEventInfo> list = doOldDelEvent(eventInfos);
//            主客队互换逻辑
//            log.info("linkId::{}::eventId:{} before list:{} ",data.getLinkId(), data.getThirdEventId(),list);
//            matchSettleBatchCheckService.changeHomeAway(list);
//            log.info("linkId::{}::eventId:{} after list:{} ",data.getLinkId(), data.getThirdEventId(),list);
            //计算当前的事件次序
            Integer order = countEventOrder(list,data);
            log.info("linkId::{}::eventId:{} 当前计算事件次序为:{} ",data.getLinkId(), data.getThirdEventId(),order);
            //2.1常规赛事件 保留当前比分
            //2.2加时赛事件 扣除 常规赛比分
            //2.3点球大战事件 记录射门次序
            //2.4罚牌 需要计算 罚牌比分 红黄牌 比分
            //删除最近的比分
            if (data.getCanceled() == 1) {
                //删除事件暂时不处理，没有很好的机制处理
                doDelMatchEvent(order, data, list ,isStandard);
                //查询需要删除的事件，如果已经结算，发出预警
                return;
            } else if (checkCancelEvent(data)){
                saveMatchEvent(order, data, list,isStandard);
            }
            log.info("linkId::{}::eventId:{} transferEventSettle 事件耗时{}ms处理完成", data.getLinkId(), System.currentTimeMillis()-start, data.getThirdEventId());
        }catch (Exception e){
            log.error("linkId::{}::eventId:{} transferEventSettle error:", data.getLinkId(), data.getThirdEventId(), e);
        }
    }

    // 检查是否为先发取消进球/角球/罚牌，再来的进球事件
    public boolean checkCancelEvent(MatchEventInfo data){
        String key = MATCH_EVENT_INFO+data.getThirdMatchId()+"_"+data.getThirdEventId()+"_"+data.getDataSourceCode()+"_"+data.getSportId()+"_cancel_event";
        Object object = redisService.get(key);
        if (object != null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isStandardEvent(String dataSourceCode, Long standardMatchId) {
        List<Long> standardMatchIds = new ArrayList<>();
        standardMatchIds.add(standardMatchId);
        List<StandardSportMarketSell>  sells= standardSportMarketSellService.getItems(standardMatchIds);
        if(sells.size()==0){
            return false;
        }
        String businessEvent =sells.get(0).getBusinessEvent();
        //如果是标准赛事的开售事件源的则进入 主事件生成逻辑
        if(dataSourceCode.equals(businessEvent)){
            return true;
        }
        return false;
    }

    /**
     * 计算事件次序
     * */
    private Integer countEventOrder(List<MatchEventInfo> eventInfos, MatchEventInfo data) {
        int order =0;
        for (MatchEventInfo eventInfo : eventInfos) {
            if(data.getMatchPeriodId().equals(eventInfo.getMatchPeriodId())){
                order++;
            }
        }
        return order;
    }

    /**
     * 先过滤删除事件和被删除的事件X
     * */
    private List<MatchEventInfo> doOldDelEvent(List<MatchEventInfo> eventInfos) {
        List<String> delEventThirdList= eventInfos.stream().filter(mfi -> mfi.getCanceled()==1).map(MatchEventInfo::getExtraInfo).collect(Collectors.toList());
        List<MatchEventInfo> list= eventInfos.stream().filter(it->it.getCanceled()!=1&&(!delEventThirdList.contains(it.getThirdEventId()))).collect(Collectors.toList());
        Set<String> thirdEventIdSet =new HashSet<>();
        List<MatchEventInfo> l =new ArrayList<>();
        //根据事件ID过滤重复事件
        for (MatchEventInfo matchEventInfo : list) {
            if(thirdEventIdSet.contains(matchEventInfo.getThirdEventId())){
                continue;
            }else {
                thirdEventIdSet.add(matchEventInfo.getThirdEventId());
                l.add(matchEventInfo);
            }
        }
        return l;
    }

    private void saveMatchEvent(Integer order, MatchEventInfo data, List<MatchEventInfo> eventInfos, boolean isStandard) {
        log.info("linkId::{}::eventId:{} saveMatchEvent start",data.getLinkId(), data.getThirdEventId());
        MatchSettleEvent timeEvent = FootBallMatchSettleScoreUtils.initMatchSettleEvent(data.getStandardMatchId());
        //计算和设置比分  编码 次序修正 去重
        log.info("linkId::{}::eventId:{} show all data MatchEventInfo:{} eventInfos:{}",data.getLinkId(), data.getThirdEventId(), data, eventInfos);
        order=  MatchEventInfoSettleUtils.doCountEventScore(order,timeEvent,data,eventInfos);
//        order++;
        timeEvent.setEventOrder(order);
        timeEvent.setPeriodId(data.getMatchPeriodId());
        timeEvent.setThirdEventSourceId(data.getId());
        timeEvent.setEventType(1);
        timeEvent.setHomeAway(data.getHomeAway());
        timeEvent.setPeriodId(data.getMatchPeriodId());
        //设置settleNum
        timeEvent.setSettleNum(SettleNumUtils.getEventSettleNum(timeEvent.getEventCode(),timeEvent.getPeriodId()));
        //球员查询
        if(data.getPlayer1Id()!=null){
            StandardSportPlayer standardSportPlayer = standardSportPlayerService.getItem(1L, data.getPlayer1Id().toString());
            if (null != standardSportPlayer) {
                timeEvent.setPlayerNameCode(standardSportPlayer.getNameCode().toString());
            }
        }
        // 添加时段类逻辑
        List<MatchSettleEvent> totalEvents = new ArrayList<>();
        totalEvents.add(timeEvent);
        if(timeEvent.getPeriodId() ==6L || timeEvent.getPeriodId() == 7L) {
            MatchSettleEvent timePhaseEvent = new MatchSettleEvent();
            BeanUtils.copyProperties(timeEvent, timePhaseEvent);
            timePhaseEvent.setId(IdGenerator.nextId());
            timePhaseEvent.setEventType(3);
            timePhaseEvent.setSettleNum(SettleNumUtils.getTypeEventSettleNum(timePhaseEvent.getEventCode(), timePhaseEvent.getPeriodId(), 3));
            totalEvents.add(timePhaseEvent);
        }

        for (MatchSettleEvent matchSettleEvent : totalEvents) {
            //三方事件先行比分复制，后可能会被清空
            MatchSettleThirdEvent thirdEvent =new MatchSettleThirdEvent();
            BeanUtils.copyProperties(matchSettleEvent,thirdEvent);
            thirdEvent.setOperater(data.getRemark());
            MatchSettleEventExample matchSettleEventExample =new MatchSettleEventExample();
            matchSettleEventExample.createCriteria().andStandardMatchIdEqualTo(data.getStandardMatchId()).andEventOrderEqualTo(matchSettleEvent.getEventOrder())
                    .andSettleNumEqualTo(matchSettleEvent.getSettleNum()).andPeriodIdEqualTo(data.getMatchPeriodId());
            List<MatchSettleEvent> matchSettleEvents =matchSettleEventV2Repository.getByMatchIdAndSettleNumAndEventOrderAndPeriodId(data.getStandardMatchId(),matchSettleEvent.getSettleNum(),matchSettleEvent.getEventOrder(),data.getMatchPeriodId());

            if(!CollectionUtils.isEmpty(matchSettleEvents)) {
                for(MatchSettleEvent event : matchSettleEvents) {
                    matchSettleEvent.setId(event.getId());
                    validateDataMismatch(matchSettleEvent, data);
                }
            }
            //如果没有事件生成需要新增
            if(matchSettleEvents.size()==0) {
                cleanEventScores(matchSettleEvent);
                matchSettleEvent.setStatus(0);
                matchSettleEvent.setThirdEventSourceId(data.getId());
                matchSettleEventV2Repository.save(matchSettleEvent);
                //常规赛 加时赛的进球和罚牌有附加事件
                if ((!matchSettleEvent.getEventCode().equals("corner")) && (!matchSettleEvent.getPeriodId().equals(50l)) && matchSettleEvent.getEventType() == 1) {
                    MatchSettleEvent extryMatchSettleEvent = FootBallMatchSettleScoreUtils.initMatchSettleEvent(data.getStandardMatchId());
                    BeanUtils.copyProperties(matchSettleEvent, extryMatchSettleEvent);
                    extryMatchSettleEvent.setThirdEventSourceId(data.getId());
                    extryMatchSettleEvent.setId(IdGenerator.nextId());
                    extryMatchSettleEvent.setEventType(2);
                    extryMatchSettleEvent.setStatus(0);
                    extryMatchSettleEvent.setSettleNum(SettleNumUtils.getTypeEventSettleNum(extryMatchSettleEvent.getEventCode(), extryMatchSettleEvent.getPeriodId(), 2));
                    matchSettleEventV2Repository.save(extryMatchSettleEvent);
                }
            }
            thirdEvent.setThirdMatchId(data.getThirdMatchId());
            thirdEvent.setThirdEventSourceId(data.getId());
            thirdEvent.setDataSourceCode(data.getDataSourceCode());
            thirdEvent.setCreateTime(System.currentTimeMillis());
            thirdEvent.setModifyTime(System.currentTimeMillis());
            thirdEvent.setId(IdGenerator.nextId());
            // 设置 eventType 和 secondFromStart
            thirdEvent.setEventType(matchSettleEvent.getEventType());
            if (data.getSecondsFromStart() != null) {
                thirdEvent.setSecondFromStart(data.getSecondsFromStart().intValue());
            }


//            CheckIsGreyDto checkIsGreyDto =this.checkIsGreyEvent(data);
//            checkIsGreyDto.setStandardMatchId(data.getStandardMatchId());
//            if(checkIsGreyDto.getIsGrey()!=null && checkIsGreyDto.getIsGrey()!=0){
//                //3.1 更新临近2个为灰色区间
//                this.updateGrayMatchSettleScore(checkIsGreyDto,data.getHomeAway());
//                thirdEvent.setIsGrey(checkIsGreyDto.getIsGrey());
//            }else{
//                thirdEvent.setIsGrey(0);
//            }
            if (Integer.valueOf(3).equals(matchSettleEvent.getEventType())) {
                CheckIsGreyDto phaseGreyDto = grayPhaseEventHelper.checkIsGreyPhaseEvent(data, data.getLinkId());
                phaseGreyDto.setStandardMatchId(data.getStandardMatchId());
                if (phaseGreyDto.getIsGrey() != null && phaseGreyDto.getIsGrey() != 0) {
                    this.updateGrayMatchSettleScore(phaseGreyDto,data.getHomeAway());
                    thirdEvent.setIsGrey(phaseGreyDto.getIsGrey());
                    log.info("linkId::{}::eventId:{} settleNum:{} eventType=3 命中灰色区间，卡结算",
                            data.getLinkId(), data.getThirdEventId(), thirdEvent.getSettleNum());
                } else {
                    thirdEvent.setIsGrey(0);
                }
            } else {
                thirdEvent.setIsGrey(0);
            }
            matchSettleThirdEventRepository.save(thirdEvent);
            //41805 赛事中途切PD,当场赛事之前的数据源不参与结算
            if (data.getDataSourceCode().equals(DataSourceCodeEnum.PD.getCode()) || data.getDataSourceCode().equals(DataSourceCodeEnum.PD2.getCode())){
                Object switchPdKey = redisService.get(MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE +":"+ data.getThirdMatchId());
                if (switchPdKey != null) {
                    Integer status =0;
                    if(switchPdKey.toString().length()>7){
                        status=0;
                    }else {
                        status =  Integer.parseInt(switchPdKey.toString());
                    }
                    if(status==0){
                        log.info("linkId::{}::eventId:{} saveMatchEvent 赛事中途切PD",data.getLinkId(), data.getThirdEventId());
                        continue;
                    }
                }
            }
            //需求2477,联赛对应的数据源结算为关闭状态，只显示赛果不参与结算
            Integer levelDataSourceStatus = matchSettleBatchCheckServiceHelper.getTournamentLevelStatus(data.getStandardMatchId(),data.getDataSourceCode(),thirdEvent.getEventCode());
            if (levelDataSourceStatus != null && levelDataSourceStatus.equals(Constant.OUTRIGHT_ZERO)){
                log.info("linkId::{}::eventId:{} saveMatchEvent 联赛数据源结算为关闭状态",data.getLinkId(), data.getThirdEventId());
                continue;
            }
            matchSettleBatchCheckService.checkMatchThirdSettleEvent(thirdEvent,data.getLinkId(),data.getSecondsFromStart());
            log.info("linkId::{}::eventId:{} settleNum:{} saveMatchEvent end",data.getLinkId(), data.getThirdEventId(), thirdEvent.getSettleNum());
            //日志结束
        }
    }

    private void cleanEventScores(MatchSettleEvent matchSettleEvent) {

        if(matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")){
            matchSettleEvent.setEventCode("fa_card");
        }
        matchSettleEvent.setHomeAway(null);
        matchSettleEvent.setT1(null);
        matchSettleEvent.setT2(null);
        matchSettleEvent.setFirstT1(null);
        matchSettleEvent.setFirstT2(null);
        matchSettleEvent.setSecondT1(null);
        matchSettleEvent.setSecondT2(null);
        matchSettleEvent.setExtryInfo(null);
        matchSettleEvent.setPlayerNameCode(null);
        matchSettleEvent.setPlayerName(null);
        matchSettleEvent.setGoWaterStatus(0);
    }

    private void doDelMatchEvent(Integer order, MatchEventInfo data, List<MatchEventInfo> eventInfos, boolean isStandard) {
        try {
            log.info("linkId::{}::eventId:{} doDelMatchEvent start",data.getLinkId(), data.getThirdEventId());
            List<String> eventCodes = SettleCheckUtils.getEventCodesByCode(data.getMatchPeriodId(), data.getEventCode());
            List<Long> periods = getPeriodByPeriod(data);

            // 上半场 order= 1  此时 删 第一个错误   // 如果是下半场
            MatchEventInfo oldEvent = matchSettleBatchCheckService.getOldMatchInfoByCancel(data);
            log.info("linkId::{}::eventId:{} oldEvent:{} doDelMatchEvent start",data.getLinkId(), data.getThirdEventId(), oldEvent);
            MatchSettleThirdEventExample matchSettleEventExample = new MatchSettleThirdEventExample();
            matchSettleEventExample.createCriteria().andEventCodeIn(eventCodes).andPeriodIdIn(periods).andStandardMatchIdEqualTo(data.getStandardMatchId()).andThirdEventSourceIdEqualTo(oldEvent.getId());
            List<MatchSettleThirdEvent> list = matchSettleThirdEventRepository.getModelByItemsOrderBySettleNum(data.getStandardMatchId(),eventCodes,periods, null,oldEvent.getId());
            log.info("linkId::{}::eventId:{} size:{} doDelMatchEvent start",data.getLinkId(), data.getThirdEventId(), list.size());
            if (list.size() != 0) {
                for (MatchSettleThirdEvent matchSettleEvent : list) {
                    if (matchSettleEvent.getStatus() <= 1) {
                        matchSettleThirdEventRepository.removeById(matchSettleEvent.getId());
                        matchSettleBatchCheckService.canceledCheckMatchThirdSettleEvent(matchSettleEvent, data, order);
                    }
                }
            }
            log.info("linkId::{}::eventId:{} doDelMatchEvent end",data.getLinkId(), data.getThirdEventId());
        }catch (Exception e){
            log.info("linkId::{}::eventId:{} doDelMatchEvent error: ",data.getLinkId(), data.getThirdEventId(), e);
        }
    }

    private void rollbackEvent(MatchSettleEvent matchSettleEvent, MatchEventInfo data) {
        matchSettleEvent.setT1(null);
        matchSettleEvent.setT2(null);
        matchSettleEvent.setFirstT1(null);
        matchSettleEvent.setFirstT2(null);
        matchSettleEvent.setSecondT1(null);
        matchSettleEvent.setSecondT2(null);
        matchSettleEvent.setExtryInfo(null);
        matchSettleEvent.setPlayerNameCode(null);
        if(data.getEventCode().equals("goal")||data.getEventCode().equals("penalty_missed")){
            if(!data.getMatchPeriodId().equals(50l)){
                matchSettleEvent.setHomeAway("no goal");
            }
            matchSettleEvent.setEventCode("goal");
        }else if(data.getEventCode().equals("corner")){
            matchSettleEvent.setHomeAway("none");
        }else {
            matchSettleEvent.setHomeAway("none");
            matchSettleEvent.setEventCode("fa_card");
        }
    }

    private List<Long> getPeriodByPeriod(MatchEventInfo data){
        List<Long> periods=new ArrayList<>();
        if(data.getMatchPeriodId().equals(6l)){
            periods.add(6l);
        }else if(data.getMatchPeriodId().equals(7l)){
            periods.add(6l);   periods.add(7l);
        }else if(data.getMatchPeriodId().equals(41l)){
            periods.add(41l);
        }else if(data.getMatchPeriodId().equals(42l)){
            periods.add(41l);  periods.add(42l);
        }else if(data.getMatchPeriodId().equals(50l)){
            periods.add(50l);
        }
        return periods;
    }

    public static void main(String[] xx){
//        List<MatchEventInfo> eventInfos =new ArrayList<>();
//        MatchEventInfo matchEventInfo =new MatchEventInfo();
//        matchEventInfo.setExtraInfo("1111");
//        matchEventInfo.setCanceled(0);
//        matchEventInfo.setEventCode("goal");
//        matchEventInfo.setThirdMatchSourceId("1111");
//        eventInfos.add(matchEventInfo);
//        MatchEventInfo matchEventInfo2 =new MatchEventInfo();
//        matchEventInfo2.setExtraInfo("1111");
//        matchEventInfo2.setCanceled(0);
//        matchEventInfo2.setEventCode("goal");
//        matchEventInfo2.setThirdMatchSourceId("111221");
//        eventInfos.add(matchEventInfo2);
//
//        List<String> delEventThirdList= eventInfos.stream().filter(mfi -> mfi.getCanceled()==1).map(MatchEventInfo::getExtraInfo).collect(Collectors.toList());
//        List<MatchEventInfo> list= eventInfos.stream().filter(it->it.getCanceled()!=1&&(!delEventThirdList.contains(it.getThirdMatchSourceId()))).collect(Collectors.toList());
//        System.out.println(list.size());
    }

    /**
     * 校验当前事件五分钟玩法区间是否为灰色区间
     * */
    private CheckIsGreyDto checkFiveMinIsGreyEvent(MatchEventInfo matchEventInfo) {
        CheckIsGreyDto checkIsGreyDto =new CheckIsGreyDto();
        if(matchEventInfo==null){
            log.warn("checkIsGreyEvent 事件无法获取");
            return checkIsGreyDto;
        }
        //1.只处理进球类型
        if(!matchEventInfo.getEventCode().equals("goal")){
            return checkIsGreyDto;
        }
        //2.判断当前阶段区域是否符合15分钟类型
        if((matchEventInfo.getSecondsFromStart()>=(4*60+55)&& matchEventInfo.getSecondsFromStart()<=(5*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(9*60+55)&& matchEventInfo.getSecondsFromStart()<=(10*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(14*60+55)&& matchEventInfo.getSecondsFromStart()<=(15*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(19*60+55)&& matchEventInfo.getSecondsFromStart()<=(20*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(24*60+55)&& matchEventInfo.getSecondsFromStart()<=(25*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(29*60+55)&& matchEventInfo.getSecondsFromStart()<=(30*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(34*60+55)&& matchEventInfo.getSecondsFromStart()<=(35*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(39*60+55)&& matchEventInfo.getSecondsFromStart()<=(40*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(44*60+55)&& matchEventInfo.getSecondsFromStart()<=(45*60))||
                (matchEventInfo.getSecondsFromStart()>=(49*60+55)&& matchEventInfo.getSecondsFromStart()<=(50*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(54*60+55)&& matchEventInfo.getSecondsFromStart()<=(55*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(59*60+55)&& matchEventInfo.getSecondsFromStart()<=(60*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(64*60+55)&& matchEventInfo.getSecondsFromStart()<=(65*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(69*60+55)&& matchEventInfo.getSecondsFromStart()<=(70*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(74*60+55)&& matchEventInfo.getSecondsFromStart()<=(75*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(79*60+55)&& matchEventInfo.getSecondsFromStart()<=(80*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(84*60+55)&& matchEventInfo.getSecondsFromStart()<=(85*60+5))||
                (matchEventInfo.getSecondsFromStart()>=(89*60+55)&& matchEventInfo.getSecondsFromStart()<=(90*60))){
            checkIsGreyDto.setIsGrey(1);
        }
        if(checkIsGreyDto.getIsGrey()==0){
            return checkIsGreyDto;
        }

        if(matchEventInfo.getSecondsFromStart()>=(4*60+55)&& matchEventInfo.getSecondsFromStart()<=(5*60+5)){
            checkIsGreyDto.getSettleNum().add("1034");
            checkIsGreyDto.getSettleNum().add("1035");
        }else if(matchEventInfo.getSecondsFromStart()>=(9*60+55)&& matchEventInfo.getSecondsFromStart()<=(10*60+5)){
            checkIsGreyDto.getSettleNum().add("1035");
            checkIsGreyDto.getSettleNum().add("1036");
        }else if(matchEventInfo.getSecondsFromStart()>=(14*60+55)&& matchEventInfo.getSecondsFromStart()<=(15*60+5)){
            checkIsGreyDto.getSettleNum().add("1036");
            checkIsGreyDto.getSettleNum().add("1037");
        }else if(matchEventInfo.getSecondsFromStart()>=(19*60+55)&& matchEventInfo.getSecondsFromStart()<=(20*60+5)){
            checkIsGreyDto.getSettleNum().add("1037");
            checkIsGreyDto.getSettleNum().add("1038");
        }else if(matchEventInfo.getSecondsFromStart()>=(24*60+55)&& matchEventInfo.getSecondsFromStart()<=(25*60+5)){
            checkIsGreyDto.getSettleNum().add("1038");
            checkIsGreyDto.getSettleNum().add("1039");
        }else if(matchEventInfo.getSecondsFromStart()>=(29*60+55)&& matchEventInfo.getSecondsFromStart()<=(30*60+5)){
            checkIsGreyDto.getSettleNum().add("1039");
            checkIsGreyDto.getSettleNum().add("1040");
        }else if(matchEventInfo.getSecondsFromStart()>=(34*60+55)&& matchEventInfo.getSecondsFromStart()<=(35*60+5)){
            checkIsGreyDto.getSettleNum().add("1040");
            checkIsGreyDto.getSettleNum().add("1041");
        }else if(matchEventInfo.getSecondsFromStart()>=(39*60+55)&& matchEventInfo.getSecondsFromStart()<=(40*60+5)){
            checkIsGreyDto.getSettleNum().add("1041");
            checkIsGreyDto.getSettleNum().add("1042");
        }else if(matchEventInfo.getSecondsFromStart()>=(44*60+55)&& matchEventInfo.getSecondsFromStart()<=(45*60)){
            checkIsGreyDto.getSettleNum().add("1042");
        }else if(matchEventInfo.getSecondsFromStart()>=(49*60+55)&& matchEventInfo.getSecondsFromStart()<=(50*60+5)){
            checkIsGreyDto.getSettleNum().add("1044");
            checkIsGreyDto.getSettleNum().add("1045");
        }else if(matchEventInfo.getSecondsFromStart()>=(54*60+55)&& matchEventInfo.getSecondsFromStart()<=(55*60+5)){
            checkIsGreyDto.getSettleNum().add("1045");
            checkIsGreyDto.getSettleNum().add("1046");
        }else if(matchEventInfo.getSecondsFromStart()>=(59*60+55)&& matchEventInfo.getSecondsFromStart()<=(60*60+5)){
            checkIsGreyDto.getSettleNum().add("1046");
            checkIsGreyDto.getSettleNum().add("1047");
        }else if(matchEventInfo.getSecondsFromStart()>=(64*60+55)&& matchEventInfo.getSecondsFromStart()<=(65*60+5)){
            checkIsGreyDto.getSettleNum().add("1047");
            checkIsGreyDto.getSettleNum().add("1048");
        }else if(matchEventInfo.getSecondsFromStart()>=(69*60+55)&& matchEventInfo.getSecondsFromStart()<=(70*60+5)){
            checkIsGreyDto.getSettleNum().add("1048");
            checkIsGreyDto.getSettleNum().add("1049");
        }else if(matchEventInfo.getSecondsFromStart()>=(74*60+55)&& matchEventInfo.getSecondsFromStart()<=(75*60+5)){
            checkIsGreyDto.getSettleNum().add("1049");
            checkIsGreyDto.getSettleNum().add("1050");
        }else if(matchEventInfo.getSecondsFromStart()>=(79*60+55)&& matchEventInfo.getSecondsFromStart()<=(80*60+5)){
            checkIsGreyDto.getSettleNum().add("1050");
            checkIsGreyDto.getSettleNum().add("1051");
        }else if(matchEventInfo.getSecondsFromStart()>=(84*60+55)&& matchEventInfo.getSecondsFromStart()<=(85*60+5)){
            checkIsGreyDto.getSettleNum().add("1051");
            checkIsGreyDto.getSettleNum().add("1052");
        }else if(matchEventInfo.getSecondsFromStart()>=(89*60+55)&& matchEventInfo.getSecondsFromStart()<=(90*60)){
            checkIsGreyDto.getSettleNum().add("1052");
        }

        return checkIsGreyDto;
    }

    public MatchEventInfo getEventFromCacheByBT(String linkedId, String dataSourceCode) {
        if(linkedId!=null){
            String eventKey=SCORE_EVENT+linkedId;
            try{
                Object o  = redisService.get(eventKey);
                if(o!=null){
                    MatchEventInfo matchEventInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()),MatchEventInfo.class);
                    return matchEventInfo;
                }else {
                    return null;
                }
            }catch (Exception e){
                log.error("MatchScoresTransSettleServiceImpl-getEventFromCacheByBT:",e);
                return null;
            }
        }else {
            return null;
        }
    }

    public MatchEventInfo getEventFromCache(Long eventId,String dataSourceCode) {
        if(eventId!=null){
            String eventKey="SCORES:"+dataSourceCode+":"+eventId;
            try{
                Object o  = redisService.get(eventKey);
                if(o!=null){
                    MatchEventInfo matchEventInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()),MatchEventInfo.class);
                    return matchEventInfo;
                }else {
                    return null;
                }
            }catch (Exception e){
                log.error("MatchScoresTransSettleServiceImpl-getEventFromCache:",e);
                return null;
            }
        }else {
            return null;
        }
    }

    public void validateDataMismatch(MatchSettleEvent matchSettleEvent, MatchEventInfo matchEventInfo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("matchSettleEvent", matchSettleEvent);
        parameters.put("matchEventInfo", matchEventInfo);
        settleMentionFactory.getProcessor(SettleMentionEnum.FOOTBALL_SCORE_MISMATCH).addSettleMention(parameters);
    }
}


