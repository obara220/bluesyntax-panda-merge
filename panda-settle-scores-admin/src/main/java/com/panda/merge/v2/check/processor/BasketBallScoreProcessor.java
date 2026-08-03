package com.panda.merge.v2.check.processor;


import cn.hutool.json.JSONUtil;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.*;
import com.panda.merge.dto.settle.AutoSettleDataSourceDto;
import com.panda.merge.filter.basketball.BasketballInstantSettleFilter;
import com.panda.merge.filter.basketball.BasketballScoreFilter;
import com.panda.merge.model.*;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import com.panda.merge.utils.EndEventUtils;
import com.panda.merge.utils.IdGenerator;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.v2.check.IMatchScoresTransSettleService;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.repository.MatchSettleThirdBasketScoreRepository;
import com.panda.merge.v2.repository.MatchSettleThirdScoreV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BasketBallScoreProcessor {
    @Autowired
    private BasketballScoreFilter basketballScoreFilter;
    @Autowired
    private IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    private RedisService redisService;
    @Autowired
    IMatchScoresTransSettleService matchScoresTransSettleService;
    @Autowired
    BasketballInstantSettleFilter basketballInstantSettleFilter;
    @Autowired
    IBasketballInSettleService basketballInSettleService;
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    @Autowired
    MatchSettleScoreV2Repository matchSettleScoreV2Repository;
    @Autowired
    MatchSettleThirdScoreV2Repository matchSettleThirdScoreV2Repository;
    @Autowired
    MatchSettleThirdBasketScoreRepository matchSettleThirdBasketScoreRepository;


    private final Integer SAFE_SCORE= 0;
    private final String SAFE_BASKET_WHOLE_SCORE_KEY="SAFE_BASKET_WHOLE_SCORE_KEY:";

    private final String prefixSettleScoreRedis = "settle_scores_linkId:";

     static private List<String> BASKETBALL_SETTLE_NUM_INDEX =new ArrayList<>();

     private static final List<String> allPhaseSettleNums = new ArrayList<>();
     static {
         allPhaseSettleNums.add("bk_q104");
         allPhaseSettleNums.add("bk_q204");
         allPhaseSettleNums.add("bk_q304");
         allPhaseSettleNums.add("bk_q404");
         allPhaseSettleNums.add("bk_1ht");
         allPhaseSettleNums.add("bk_2ht");
         allPhaseSettleNums.add("bk_in_rg");
     }


    public void processorScore(Request<CommonThirdScoresDto> request) {
        //BUG52971  拦截掉 LinkId包含bk_ft_rg的
        log.info("linkId::{}::processorScore start",request.getData().getLinkedId());
        if (request.getLinkId().contains("bk_ft_rg")||request.getLinkId().contains("bk_et")||request.getLinkId().contains("bk_ft_et")){
            log.info("linkId::{}::linkId不符合格式",request.getData().getLinkedId());
            return;
        }
        if (request.getData().getEventId() == null) {
            log.info("linkId::{}::比分发送两条数据，对于第二条数据不进行处理",request.getLinkId());
            return;
        }
        String prefixSettleScoreKey = prefixSettleScoreRedis+request.getData().getLinkedId();
        Object isExist = redisService.get(prefixSettleScoreKey);
        if (isExist!=null) {
            return;
        }
        redisService.set(prefixSettleScoreKey, "True", 7200);

        //赛事切换2.0 过滤
        MatchSettleInfo matchSettleInfo =matchSettleInfoRepository.getModelMatchSettleInfo(request.getData().getStandardMatchId());
        if(matchSettleInfo==null || matchSettleInfo.getSettleType()==null||matchSettleInfo.getSettleType()==1){
            log.info("linkId::{}::matchSettleInfo is null",request.getData().getLinkedId());
            return;
        }
        //阶段过滤
        if(request.getData().getPeriodId()<1){
            log.info("linkId::{}::阶段过滤",request.getData().getLinkedId());
            return;
        }
        //异步即使结算逻辑开始
        MatchEventInfo eventInfo =matchScoresTransSettleService.getEventFromCache(request.getData().getEventId(),request.getData().getDataSourceCode());
        if (eventInfo == null&&(!request.getData().getDataSourceCode().equals("BFZX"))) {
            log.info("linkId::{}::事件无法查询到,无法判断是否为灰色区间，拒绝处理",request.getLinkId());
            return;
        }
        request.getData().setMatchEventInfo(eventInfo);
        if (!request.getData().getDataSourceCode().equals("BFZX")){
            InstantSettleBasketBallScore(request,matchSettleInfo);
        }

        log.info("linkId::{}::阶段比分处理开始",request.getLinkId());
        String key ="StandardMatchScoreConsumer:"+request.getData().getStandardMatchId();
        try {
            //redis锁 防止里面 查询后插入的问题
            if (redisService.tryLock(key, key, 2, 3)) {
                log.info("linkId::{}::阶段比分获取锁",request.getData().getLinkedId());
                List<String> IN_SETTLE_NUM_LIST = BasketBallSettleScoreUtils.IN_SETTLE_NUM_LIST;
                MatchSettleScoreExample example = new MatchSettleScoreExample();
                example.createCriteria().andStandardMatchIdEqualTo(request.getData().getStandardMatchId())
                        .andSettleNumNotIn(IN_SETTLE_NUM_LIST);
                //赛事比分转化匹配责任链模式
                List<MatchSettleScore> beforeList = matchSettleScoreV2Repository.getModelByStandardMatchIdAndNotSettleNum(
                        request.getData().getStandardMatchId(), IN_SETTLE_NUM_LIST);
                //根据结算顺序排序 todo
                beforeList.sort(new Comparator<MatchSettleScore>() {
                    @Override
                    public int compare(MatchSettleScore o1, MatchSettleScore o2) {
                        Integer x1 = BASKETBALL_SETTLE_NUM_INDEX.indexOf(o1.getSettleNum());
                        Integer x2 = BASKETBALL_SETTLE_NUM_INDEX.indexOf(o2.getSettleNum());
                        return x1 - x2;
                    }
                });
                Map<String, Object> allPeriodScores = request.getData().getScores();
                //1.查询结算阶段比分
                Map<String, BasketballScores> basketballScoresMap = JsonMapUtils.transferBasketballMap(allPeriodScores);
                List<MatchSettleScore> resultlist = new ArrayList<>();
                //2.根据结算阶段比分循环匹配过滤器

                basketballScoreFilter.filter(basketballScoresMap, request.getData(), beforeList, resultlist);
                if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(resultlist)){
                    log.info("linkId::{}::根据结算阶段比分循环匹配过滤器,basketBallScoresMap:{},request_getData:{},beforeList:{},resultList:{}",
                            request.getData(), JSONUtil.toJsonStr(basketballScoresMap), request.getData(), JSONUtil.toJsonStr(beforeList), JSONUtil.toJsonStr(resultlist));
                }
                //3.将结算的阶段比分的过滤器处理比分编辑
                //4.三方比分入库和更新
                saveMatchSettleThirdScores(resultlist, request.getData());
                log.info("linkId::{}::阶段比分处理结束",request.getData().getLinkedId());
                //5.核对比分入库和更新
                //6.自动结算流程开启
                //7.核对比分主流程
                //8.通过核对比分则进入自动结算
                //9.自动结算完毕下发 结算阶段比分TOPIC
            }else {
                log.error("linkId::{}::比分无法获取redis锁",request.getData().getLinkedId());
            }
        }catch(Exception e ){
                log.error("linkId::{}::BasketBallScoreProcessor-processorScore:", request.getData().getLinkedId(), e);
        }finally {
            redisService.unLock(key,key);
        }

    }




    private void saveMatchSettleThirdScores( List<MatchSettleScore> list, CommonThirdScoresDto data) {
        try {
            log.info("linkId::{}::saveMatchSettleThirdScores开始处理",data.getLinkedId());
            if(list.size()==0){
                return;
            }
            //灰色区间处理
            CheckIsGreyDto checkIsGreyDto =null;
            if(data.getMatchEventInfo()!=null){
                checkIsGreyDto= matchScoresTransSettleService.checkIsGreyEvent(data.getMatchEventInfo());
                checkIsGreyDto.setStandardMatchId(data.getStandardMatchId());
                if(checkIsGreyDto.getIsGrey()!=null && checkIsGreyDto.getIsGrey()!=0){
                    //3.1 更新临近2个为灰色区间
                    checkIsGreyDto.setScoresGrey(1);
                    matchScoresTransSettleService.updateGrayMatchSettleScore(checkIsGreyDto,data.getMatchEventInfo().getHomeAway());
                }
            }
            if(checkIsGreyDto!=null){
                checkIsGreyDto.setMatchEventInfo(data.getMatchEventInfo());
            }

            List<String> scoresSettleNums=list.stream().map(it->it.getSettleNum()).collect(Collectors.toList());
            List<MatchSettleThirdScore> oldScoresList =matchSettleThirdScoreV2Repository.getByMatchIdAndAndDataSourceCodeSettleNum(null,data.getThirdMatchId(),data.getDataSourceCode(),scoresSettleNums);
            log.info("linkId::{}::结算比分映射,查询到老的比分有:{}",data.getLinkedId(),oldScoresList.size());
            //有报错 mapkey冲突
//        Map<String,MatchSettleThirdScore>  oldScoresMap=oldScoresList.stream().collect(Collectors.toMap(MatchSettleThirdScore::getSettleNum,it->it));
            Map<String,MatchSettleThirdScore>  oldScoresMap=new HashMap<>();
            for (MatchSettleThirdScore matchSettleThirdScore : oldScoresList) {
                if(oldScoresMap.get(matchSettleThirdScore.getSettleNum())==null){
                    oldScoresMap.put(matchSettleThirdScore.getSettleNum(),matchSettleThirdScore);
                }
            }
            log.info("linkId::{}::结算比分映射,查询到老的转化后map有:{}",data.getLinkedId(),oldScoresMap.size());
            for (MatchSettleScore matchSettleScore : list) {
                //1.查询当前阶段比分
                MatchSettleThirdScore oldScore= oldScoresMap.get(matchSettleScore.getSettleNum());
                if(oldScore==null){
                    oldScore=new MatchSettleThirdScore();
                    BeanUtils.copyProperties(matchSettleScore,oldScore);
                    oldScore.setId(IdGenerator.nextId());
                    oldScore.setCreateTime(System.currentTimeMillis());
                    oldScore.setStandardMatchId(data.getStandardMatchId());
                    oldScore.setThirdMatchId(data.getThirdMatchId());
                    oldScore.setDataSourceCode(data.getDataSourceCode());
                    oldScore.setExtryInfo(matchSettleScore.getExtryInfo());
                    oldScore.setSettleNum(matchSettleScore.getSettleNum());
                    matchSettleThirdScoreV2Repository.save(oldScore);
                    if(oldScore.getT1() == 0 && oldScore.getT2() == 0 && allPhaseSettleNums.contains(oldScore.getSettleNum())) {
                        continue;
                    }
                    matchSettleBatchCheckService.batchCheckMatchThirdSettleScores(Arrays.asList(oldScore),data.getLinkedId(),data.getSecondFromStart(),checkIsGreyDto);
                    continue;
                }
                //2.根据当前阶段比分状态是否被确认判断是否覆盖数据
                if(!(oldScore.getT1()!=null&&oldScore.getT1()==matchSettleScore.getT1()&&oldScore.getT2()!=null&&oldScore.getT2()==matchSettleScore.getT2())){
                    oldScore.setT1(matchSettleScore.getT1());
                    oldScore.setT2(matchSettleScore.getT2());
                    oldScore.setModifyTime(System.currentTimeMillis());
                    oldScore.setDataSourceCode(data.getDataSourceCode());
                    matchSettleThirdScoreV2Repository.updateById(oldScore);
                }
                if(oldScore.getT1() == 0 && oldScore.getT2() == 0 && allPhaseSettleNums.contains(oldScore.getSettleNum())) {
                    continue;
                }
                matchSettleBatchCheckService.batchCheckMatchThirdSettleScores(Arrays.asList(oldScore),data.getLinkedId(),data.getSecondFromStart(),checkIsGreyDto);

            }
            wsPushService.pushThirdBasketballSettleScores(data.getStandardMatchId());
            log.info("linkId::{}::saveMatchSettleThirdScores处理结束",data.getLinkedId());
        }catch (Exception e){
            log.error("linkId::{}::saveMatchSettleThirdScores error:", data.getLinkedId(), e);
        }

    }



    @Async("InstantSettleThreadPool")
    public void InstantSettleBasketBallScore(Request<CommonThirdScoresDto> request, MatchSettleInfo matchSettleInfo) {
        log.info("linkId::{}::InstantSettleBasketBallScore处理开始",request.getLinkId());
        if("LS".equals(request.getData().getMatchEventInfo().getDataSourceCode())){
            log.info("linkId::{}::InstantSettleBasketBallScore 数据源LS次序不处理", request.getLinkId());
            return;
        }
        String rediskey = "InstantSettleBasketBallScore"+request.getData().getThirdMatchId();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleInfo.getStandardMatchId());

        StandardSportTournament standardSportTournament =standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
        //默认值30
        Integer LIMIT_TIME_SECOND = 30;
        List<LimitSwitchDto> dtoList  = basketballInSettleService.getBasketInSettleTimeLimit(2l);

        if (!CollectionUtils.isEmpty(dtoList)){
            for (int i = 0 ;i<dtoList.size();i++){
                if (standardSportTournament.getTournamentLevel()==dtoList.get(i).getLevel()){
                    LIMIT_TIME_SECOND = dtoList.get(i).getLimitSecond();
                }
            }
        }
        log.info("linkId::{}:: standardMatchId:{} 即时结算使用的时间是 {} 秒",request.getLinkId(), matchSettleInfo.getStandardMatchId(),LIMIT_TIME_SECOND);
        try {

            if(redisService.tryLock(rediskey,rediskey,2,3)) {
                //非开打阶段不处理
                if (!EndEventUtils.basketInGameByPeriod(request.getData().getPeriodId())) {
                    return;
                }
                //查询缓存事件
                MatchEventInfo eventInfo = matchScoresTransSettleService.getEventFromCache(request.getData().getEventId(), request.getData().getDataSourceCode());
                log.info("linkId::{}::InstantSettleBasketBallScore 0 eventInfo:{} ", request.getLinkId(), eventInfo);
                //将比分拆解为 各个阶段比分
                List<MatchSettleScore> list = new ArrayList<>();
                StandardMatchInfo match =  standardMatchInfoService.getItem(matchSettleInfo.getStandardMatchId());
                if(match==null){
                    return;
                }
                //计算篮球即时比分和三方结算即使比分
                MatchSettleThirdBasketScore matchSettleThirdBasketScore = basketballInstantSettleFilter.filter(request, matchSettleInfo, eventInfo, list,match);
                log.info("linkId::{}::InstantSettleBasketBallScore 2 matchSettleThirdBasketScore:{} ", request.getLinkId(), matchSettleThirdBasketScore);
                if (list.size() == 0 || matchSettleThirdBasketScore == null) {
                    return;
                }
                //1.将比分转化后入三方比分库而且推送前端展示三方比分
                log.info("linkId::{}::InstantSettleBasketBallScore 3 saveInSettleThirdScores size:{} ", request.getLinkId(), list.size());
                saveInSettleThirdScores(request.getData(), list, eventInfo);
                log.info("linkId::{}::InstantSettleBasketBallScore 4 matchSettleThirdBasketScore :{} ", request.getLinkId(), matchSettleThirdBasketScore);
                matchSettleThirdBasketScoreRepository.save(matchSettleThirdBasketScore);

                //2.进入安全分逻辑

                //2.1 计算当前安全分后的结算总分阈值
                Integer settleSumScore = this.countSettleSumScore(request.getData().getScores());
                log.info("linkId::{}::InstantSettleBasketBallScore 5 settleSumScore :{} ", request.getLinkId(), settleSumScore);
                if (settleSumScore == null) {
                    return;
                }
                //2.2 获取缓存安全分
                String key = SAFE_BASKET_WHOLE_SCORE_KEY + request.getData().getThirdMatchId();
                Object cacheSumScoreO = redisService.get(key);
                log.info("linkId::{}::InstantSettleBasketBallScore 6 cacheSumScoreO :{} ", request.getLinkId(), cacheSumScoreO);
                Integer cacheSumScore;
                if (cacheSumScoreO == null) {
                    cacheSumScore = 0;
                } else {
                    cacheSumScore = Integer.parseInt(cacheSumScoreO.toString());
                }
                //2.6  结算后才 更新结算的缓存安全分
                redisService.set(key, settleSumScore, 9600);
                log.info("linkId::{}::InstantSettleBasketBallScore 7 cacheSumScore :{} ", request.getLinkId(), cacheSumScore);

                //查询即时结算开关
                boolean realtimeOnOff = basketballInSettleService.getRealtimeSwitchOfLevel(matchSettleInfo.getSportId(), standardMatchInfo.getStandardTournamentId());
                if (!realtimeOnOff){
                    return;
                }

                //判断是否是主事件源 非住事件源 返回
                String busEvent = this.getbusinessEvent(request.getData().getStandardMatchId());
                if (busEvent == null || (!busEvent.equals(request.getData().getDataSourceCode()))) {
                    return;
                }
                //2.5 如果安全分比分过大则不处理 默认 >3 分则不处理 而且数据商自动结算开关关闭
                if (cacheSumScore != 0 && (settleSumScore - cacheSumScore) > 3) {
                    //自动关闭数据商结算开关
                    matchSettleInfo.setIsAutoSettleDataSource(0);
                    matchSettleInfo.setModifyTime(System.currentTimeMillis());
                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                    log.info("linkId::{}::InstantSettleBasketBallScore 8 setIsAutoSettleDataSource :{} ", request.getLinkId(), settleSumScore - cacheSumScore);
                    //这里应该要推送WS
                    AutoSettleDataSourceDto autoSettleDataSourceDto = new AutoSettleDataSourceDto();
                    autoSettleDataSourceDto.setIsEnableAutoSettle(false);
                    autoSettleDataSourceDto.setStandardMatchId(matchSettleInfo.getStandardMatchId().toString());
                    wsPushService.pushGlobalAutoSettleStatus(autoSettleDataSourceDto);
                    return;
                }
                //2.4 数据商自动结算开关关闭则直接返回不处理
                if (matchSettleInfo.getIsAutoSettleDataSource() != null && matchSettleInfo.getIsAutoSettleDataSource() == 0) {
                    return;
                }
                log.info(request.getData().getLinkedId()+":即时结算拦截校验:"+request.getData().getSecondFromStart()+"时间设置:"+LIMIT_TIME_SECOND);
                if (request.getData().getSecondFromStart() != null && request.getData().getSecondFromStart() <= LIMIT_TIME_SECOND) {
                    return;
                }
                log.info(request.getData().getLinkedId()+":即时结算拦截校验2:"+request.getData().getSecondFromStart()+"时间设置:"+LIMIT_TIME_SECOND);
                log.info("linkId::{}::InstantSettleBasketBallScore 9 settleInScoreBySingleDataSource ", request.getLinkId());
                //单条结算逻辑
                basketballInSettleService.settleInScoreBySingleDataSource(request, settleSumScore, cacheSumScore, matchSettleInfo);
                log.info("linkId::{}::InstantSettleBasketBallScore处理结束 redisService :key:{},value:{}", request.getLinkId(), key, settleSumScore);
            }else {
                log.error("linkId::{}::InstantSettleBasketBallScore获取redis锁失败",request.getLinkId());
            }
        }catch (Exception e){
            log.error("linkId::{}::InstantSettleBasketBallScore error:",request.getLinkId(), e);
        }finally {
            redisService.unLock(rediskey,rediskey);
        }
    }

    private String getbusinessEvent(Long standardMatchId) {
        List<Long> standardMatchIds = new ArrayList<>();
        standardMatchIds.add(standardMatchId);
        List<StandardSportMarketSell> sells = standardSportMarketSellService.getItems(standardMatchIds);
        if (sells.size() == 0) {
            return null;
        }
        String businessEvent = sells.get(0).getBusinessEvent();
        return businessEvent;
    }

    private Integer countSettleSumScore(Map scores) {
        Map<String, BasketballScores> basketballScoresMap = JsonMapUtils.transferBasketballMap(scores);
        BasketballScores basketballScores =basketballScoresMap.get(new Long(-1).toString());
        if(basketballScores==null||basketballScores.getMatchScore()==null||basketballScores.getMatchScore().getHome()==null||basketballScores.getMatchScore().getAway()==null){
            return null;
        }
        return basketballScores.getMatchScore().getHome()+basketballScores.getMatchScore().getAway()-SAFE_SCORE;
    }

    private void saveInSettleThirdScores(CommonThirdScoresDto data, List<MatchSettleScore> list, MatchEventInfo eventInfo) {
        List<String> scoresSettleNums=list.stream().map(it->it.getSettleNum()).collect(Collectors.toList());
        List<MatchSettleThirdScore> oldScoresList =matchSettleThirdScoreV2Repository.getByMatchIdAndAndDataSourceCodeSettleNum(
                null,data.getThirdMatchId(),data.getDataSourceCode(),scoresSettleNums);

        Map<String,MatchSettleThirdScore>  oldScoresMap=new HashMap<>();
        for (MatchSettleThirdScore matchSettleThirdScore : oldScoresList) {
            if(oldScoresMap.get(matchSettleThirdScore.getSettleNum())==null){
                oldScoresMap.put(matchSettleThirdScore.getSettleNum(),matchSettleThirdScore);
            }
        }
        for (MatchSettleScore matchSettleScore : list) {
            //1.查询当前阶段比分
            MatchSettleThirdScore oldScore= oldScoresMap.get(matchSettleScore.getSettleNum());
            if(oldScore==null){
                oldScore=new MatchSettleThirdScore();
                BeanUtils.copyProperties(matchSettleScore,oldScore);
                oldScore.setId(IdGenerator.nextId());
                oldScore.setCreateTime(System.currentTimeMillis());
                oldScore.setStandardMatchId(data.getStandardMatchId());
                oldScore.setThirdMatchId(data.getThirdMatchId());
                oldScore.setDataSourceCode(data.getDataSourceCode());
                oldScore.setExtryInfo(matchSettleScore.getExtryInfo());
                oldScore.setSettleNum(matchSettleScore.getSettleNum());
                matchSettleThirdScoreV2Repository.save(oldScore);
                continue;
            }
            //2.根据当前阶段比分状态是否被确认判断是否覆盖数据
            if(oldScore.getT1()==null||oldScore.getT2()==null||oldScore.getT1()!=matchSettleScore.getT1()||oldScore.getT2()!=matchSettleScore.getT2()){
                oldScore.setT1(matchSettleScore.getT1());
                oldScore.setT2(matchSettleScore.getT2());
                oldScore.setModifyTime(System.currentTimeMillis());
                oldScore.setDataSourceCode(data.getDataSourceCode());
                matchSettleThirdScoreV2Repository.updateById(oldScore);
            }
        }
        //3.推送ws给前端展示比分
        wsPushService.pushThirdBasketballSettleScores(data.getStandardMatchId());
    }

    static {
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q101");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q102");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q103");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q104");

        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q201");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q202");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q203");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q204");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_1ht");

        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q301");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q302");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q303");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q304");

        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q401");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q402");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q403");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q404");

        BASKETBALL_SETTLE_NUM_INDEX.add("bk_ft_rg");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_2ht");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_ft_et");

        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q1041");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q1042");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q2041");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q2042");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q3041");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q3042");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q4041");
        BASKETBALL_SETTLE_NUM_INDEX.add("bk_q4042");

        for(int i=1;i<=15;i++){
            Integer x= i*10;
            String settNum = "bk_1st_"+x;
            BASKETBALL_SETTLE_NUM_INDEX.add(settNum);
        }
    }

}
