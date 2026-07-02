package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.MatchPeriodEnum;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.constant.MatchSettleScoreConstant;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.LimitSwitchDto;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.settle.AutoSettleDataSourceDto;
import com.panda.merge.dto.settle.MatchListSettleDto;
import com.panda.merge.filter.basketball.BasketballInstantSettleFilter;
import com.panda.merge.mapper.MatchSettleInfoMapper;
import com.panda.merge.mapper.MatchSettleScoreMapper;
import com.panda.merge.mapper.MatchSettleThirdBasketScoreMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.service.*;
import com.panda.merge.v2.entity.MatchSettleInfoEntity;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.SETTLED;

@Service
@Slf4j
public class BasketballInSettleServiceImpl implements IBasketballInSettleService {
    @Autowired
    MatchSettleThirdBasketScoreMapper matchSettleThirdBasketScoreMapper;
    @Autowired
    MatchSettleScoreMapper matchSettleScoreMapper;
    @Lazy
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;
    @Autowired
    IMatchSettleService matchSettleService;
    @Autowired
    IMatchSettleLogService matchSettleScoreAddLog;
    @Autowired
    RedisService redisService;
    @Autowired
    MatchSettleInfoMapper matchSettleInfoMapper;

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    private final String REDIS_KEY_BASKET_IN_TIME_LIMIT="REDIS_KEY_BASKET_IN_TIME_LIMIT";

    @Async("InstantSettleThreadPool")
    @Override
    public void settleInScoreBySingleDataSource(Request<CommonThirdScoresDto> request , Integer settleSumScore, Integer cacheSumScore, MatchSettleInfo matchSettleInfo) {
        //1.查询当前数据商的所有符合结算要求的历史比分 所以历史比分要提前入库 OK
        CommonItem wholeScore = request.getData().getHomeAwayScore();
        //1.1 条件  三方赛事id 总分>oldSumScore <=settleSumScore
        MatchSettleThirdBasketScoreExample scoreExample =new MatchSettleThirdBasketScoreExample();
        scoreExample.createCriteria().andStandardMatchIdEqualTo(matchSettleInfo.getStandardMatchId()).andThirdMatchIdEqualTo(request.getData().getThirdMatchId())
                .andSumScoreGreaterThan(cacheSumScore).andSumScoreLessThanOrEqualTo(settleSumScore).andPeriodIdEqualTo(request.getData().getPeriodId()); //缺乏总分索引
        //1.2 条件  t1 < matchSettleScore.t1 && t2 <matchSettleScore.t2;
        List<MatchSettleThirdBasketScore> list = matchSettleThirdBasketScoreMapper.selectByExample(scoreExample);
        log.info(":{}: InstantSettleBasketBallScore 10 settleInScoreBySingleDataSource 1  list.size :{}",request.getLinkId(),list.size());
        List<MatchSettleThirdBasketScore> filterList1 =new ArrayList<>();

        //2.1 过滤1 主客队比分比对
        for (MatchSettleThirdBasketScore matchSettleThirdBasketScore : list) {
            //主客队比分比对
            if(matchSettleThirdBasketScore.getT1()<=wholeScore.getHome()&&matchSettleThirdBasketScore.getT2()<=wholeScore.getAway()){
                filterList1.add(matchSettleThirdBasketScore);
            }
        }
        //2.2 过滤2 挑一个结算 目前取总分最大的
        MatchSettleThirdBasketScore max =null;
        Integer sumScore = 0;
        for (MatchSettleThirdBasketScore score : filterList1) {
            if(score.getT1()+score.getT2()>sumScore){
                sumScore= score.getT1()+score.getT2();
                max = score;
            }
        }
        log.info(":{}: InstantSettleBasketBallScore 10 settleInScoreBySingleDataSource 2  max:{}",request.getLinkId(),max);
        //2.3 触发结算
        if(max!=null){
            //2.4 拆解成 N条
            List<MatchSettleScore> needSettleScores= BasketballInstantSettleFilter.transforSettleInScore(max,matchSettleInfo,request.getData().getLinkedId());
            List<String> scoresSettleNums=needSettleScores.stream().map(it->it.getSettleNum()).collect(Collectors.toList());
            MatchSettleScoreExample matchSettleScoreExample =new MatchSettleScoreExample();
            matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(matchSettleInfo.getStandardMatchId())
                    .andSettleNumIn(scoresSettleNums);
            List<MatchSettleScore>  oldSettleScores = matchSettleScoreMapper.selectByExample(matchSettleScoreExample);
            Map<String,MatchSettleScore> oldScoresMap=new HashMap<>();
            for (MatchSettleScore settleScore : oldSettleScores) {
                oldScoresMap.put(settleScore.getSettleNum(),settleScore);
            }
            log.info(":{}: InstantSettleBasketBallScore 10 settleInScoreBySingleDataSource 3  oldScoresMap.size:{}",request.getLinkId(),oldScoresMap.size());
            /**
             * 循环结算
             * */
            for (MatchSettleScore needSettleScore : needSettleScores) {
                try {
                    MatchSettleScore oldSettleScore = oldScoresMap.get(needSettleScore.getSettleNum());
                    if(oldSettleScore==null){
                        log.error(":{}:ERROR InstantSettleBasketBallScore 10 settleInScoreBySingleDataSource 3  oldSettleScore==null:{}",request.getLinkId(),needSettleScore.getSettleNum());
                        continue;
                    }
                    //如果已经结算比分 比需要结算比分大 则跳过
                    if((oldSettleScore.getT1()!=null&&oldSettleScore.getT1()>needSettleScore.getT1())||(oldSettleScore.getT2()!=null&&oldSettleScore.getT2()>needSettleScore.getT2())){
                        continue;
                    }
                    oldSettleScore.setT1(needSettleScore.getT1());
                    oldSettleScore.setT2(needSettleScore.getT2());

                    oldSettleScore.setModifyTime(System.currentTimeMillis());
                    oldSettleScore.setOperater(request.getData().getDataSourceCode());
                    log.info(":{}: InstantSettleBasketBallScore 10 settleInScoreBySingleDataSource 4  autoSettleInScore:{}", request.getLinkId(), oldSettleScore);
                    this.autoSettleInScore(oldSettleScore);
                }catch (Exception e){
                    log.error(request.getLinkId()+":InstantSettleBasketBallScore 10 autoSettleInScore error:",e.getMessage());
                }
            }
            wsPushService.pushBasketballStandardSettleScores(matchSettleInfo.getStandardMatchId(), null);
        }
    }

    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Override
    public void cleanBasketInSettleCacheScore(String standardMatchId) {
        //根据标准赛事 清空 数据商的 结算比分 用于开启结算，取消 4分判断
        List<Long> standardMatchIds = new ArrayList<>();
        standardMatchIds.add(Long.parseLong(standardMatchId));
        List<StandardSportMarketSell> sells = standardSportMarketSellService.getItems(standardMatchIds);
        if (sells.size() == 0) {
            return ;
        }
        String businessEvent = sells.get(0).getBusinessEvent();
        List<ThirdMatchInfo> list = thirdMatchInfoService.getThirdMatchInfoForSettle(Long.parseLong(standardMatchId),businessEvent) ;
        if(list.size()==0){
            return;
        }
        ThirdMatchInfo thirdMatchInfo = list.get(0);
        String key = "SAFE_BASKET_WHOLE_SCORE_KEY "+ thirdMatchInfo.getId();
        redisService.del(key);
    }

    @Override
    public void closeInAutoSettleBySoldMsgChange(Long standardMatchId,String dataSourceCode) {
        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(standardMatchId);
        if(standardMatchInfo.getMatchStatus()==1){
            MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(standardMatchId);
            matchSettleInfo.setIsAutoSettleDataSource(0);
            matchSettleInfo.setModifyTime(System.currentTimeMillis());
            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
            //推送ws 客户端 刷新赛事列表的状态
            AutoSettleDataSourceDto autoSettleDataSourceDto = new AutoSettleDataSourceDto();
            autoSettleDataSourceDto.setIsEnableAutoSettle(false);
            autoSettleDataSourceDto.setStandardMatchId( standardMatchId.toString());
            wsPushService.pushGlobalAutoSettleStatus(autoSettleDataSourceDto);
        }
    }

    @Override
    public List<LimitSwitchDto> getBasketInSettleTimeLimit(Long sportId ) {
        List<LimitSwitchDto> list = new ArrayList<>();
        try {
            Object o     =redisService.get(REDIS_KEY_BASKET_IN_TIME_LIMIT+"_"+sportId);
            if (o != null) {
                try {
                    JSONArray array = JSONArray.parseArray(o.toString());

                    for (Object object :array){
                        LimitSwitchDto dto =   JSONObject.toJavaObject((JSONObject)object,LimitSwitchDto.class);
                        if (dto.getRealTimeOnOff() == null){
                            dto.setRealTimeOnOff(true);
                        }
                        list.add(dto);
                    }
                } catch (Exception e) {
                    log.error("getBasketInSettleTimeLimit_Error");
                }
            }else {
                //假如初始化给前台展示
                for (int i = 0;i<=20;i++){
                    LimitSwitchDto dto = new LimitSwitchDto();
                    dto.setLevel(i);
                    dto.setLimitSecond(30);
                    dto.setOnOff(true);
                    dto.setRealTimeOnOff(true);
                    list.add(dto);
                }
            }
        }catch (Exception e){
            log.error("getBasketInSettleTimeLimit_getRedisData_Error");
        }
        return list;
    }



    private void autoSettleInScore(MatchSettleScore matchSettleScore) {

        //2.修改结算对象状态
        matchSettleScore.setStatus(SETTLED);
        //3.设置结算对象的 是否自动结算方式
        matchSettleScore.setIsAutoSettle(1);
        matchSettleScore.setSettleTimes(1);
        matchSettleScore.setSettleCount(1);
        //4.设置结算人 结算次数  是否二次结算等
        matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
        matchSettleScore.setModifyTime(System.currentTimeMillis());
        //只有一次结算会走这里
        matchSettleScore.setSettleTimes(1);

        matchSettleScore.setIsGrey(0);
        matchSettleScore.setHasDeleteEvent(0);
        matchSettleScore.setCurrentEventStatus(0);
        //5.更新结算对象到结算表
        matchSettleScoreMapper.updateByPrimaryKey(matchSettleScore);
        log.info("比分Id::{}:: 当前事件被结算参数:{} ", matchSettleScore.getId(), matchSettleScore);
        //结算时把回滚订单数清零
        matchSettleService.settleRollBackSetNullOrderCount(matchSettleScore.getId());
        //1.日志
        //2.MQ下发
        matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);


        String userName =matchSettleScore.getOperater();
        matchSettleScoreAddLog.matchSettleScoreAddLog(matchSettleScore, userName,
                OperateLogTypeEnum.SCORE_SETTLE, OperateLogTypeEnum.CONFIRM_SCORE.getCode().toString(), "");

    }


    @Override
    public boolean getRealtimeSwitchOfLevel(Long sportId, Long standardTournamentId) {
        StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardTournamentId);
        List<LimitSwitchDto> configs = getBasketInSettleTimeLimit(sportId);
        for(LimitSwitchDto limitSwitchDto: configs){
            if (limitSwitchDto.getLevel() == standardSportTournament.getTournamentLevel()) {
                return limitSwitchDto.getRealTimeOnOff();
            }
        }

        return true;
    }


    @Override
    public boolean checkRealtimeAndPSwitch(Long sportId, Long standardMatchId ,Long standardTournamentId) {
        boolean flag = getRealtimeSwitchOfLevel(sportId, standardTournamentId);
        if(flag){
            //检查下关p状态
            MatchSettleInfoEntity matchSettleInfoEntity = matchSettleInfoRepository.getMatchSettleInfo(standardMatchId);
            flag = matchSettleInfoEntity.getIsAutoSettleDataSource() == 1;
        }

        return flag;
    }

}
