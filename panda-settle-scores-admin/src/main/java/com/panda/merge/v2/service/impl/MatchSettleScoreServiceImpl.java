package com.panda.merge.v2.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.MatchPeriodEnum;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.constant.MatchSettleScoreConstant;
import com.panda.merge.constant.SettleTemplateTypeEnum;
import com.panda.merge.v2.converter.MatchSettleCheckInfoV2Converter;
import com.panda.merge.dto.advertise.MatchFreezeDto;
import com.panda.merge.dto.message.MatchFreezeMessage;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.utils.SettleNumUtils;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.utils.*;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.converter.MatchSettleScoreConverter;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.v2.entity.*;
import com.panda.merge.v2.repository.*;
import com.panda.merge.v2.service.IMatchSettleCheckInfoService;
import com.panda.merge.v2.service.IMatchSettleEventService;
import com.panda.merge.v2.service.IMatchSettleOperateLogService;
import com.panda.merge.v2.service.IMatchSettleScoreService;
import com.panda.merge.v2.service.assemble.MatchSettleScoreAssemble;
import com.panda.merge.v2.service.helper.*;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MatchSettleScoreConstant.MatchSettleScoreStatus.*;

@Slf4j
@Service("MatchSettleScoreServiceImplV2")
public class MatchSettleScoreServiceImpl implements IMatchSettleScoreService {

    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    IMatchSettleCheckInfoService matchSettleCheckInfoService;

    @Autowired
    MatchSettleCheckInfoHelper matchSettleCheckInfoHelper;
    @Autowired
    MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreRepository;

    @Autowired
    IMatchSettleOperateLogService matchSettleOperateLogService;

//    @Autowired
//    IMatchSettleCheckService matchSettleCheckService;

    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;

    @Autowired
    IWsPushService wsPushService;

    @Autowired
    MatchServiceHelper matchServiceHelper;
    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;

    @Autowired
    private MatchSettleScoreConverter matchSettleScoreConverter;

    @Autowired
    private GrayIntervalServiceHelper grayIntervalServiceHelper;

    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;

    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Autowired
    MatchSettleCenterProducer matchSettleCenterProducer;
    @Autowired
    private MatchSettleInfoHelper matchSettleInfoHelper;
    @Autowired
    private MatchSettleScoreHelper matchSettleScoreHelper;

    @Autowired
    private MatchSettleScoreAssemble matchSettleScoreAssemble;
    @Autowired
    private IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    private MatchSettleTemplateHelper matchSettleTemplateHelper;
    @Autowired
    private MatchSettleCheckInfoV2Converter matchSettleCheckInfoV2Converter;
    @Autowired
    private IMatchSettleEventService matchSettleEventService;
    @Autowired
    private MatchDelaySettleInfoHelper matchDelaySettleInfoHelper;

    @Override
    public List<MatchSettleScoreDto> searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleScoreAssemble.searchBasketballMatchSettleScores(settleScoreSearchDto);
    }
    @Override
    public Response updateMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        log.info("basketball updateMatchSettleScore-v2,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                //0.加redis锁
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
                if (standardMatchInfo == null) {
                    return Response.failed("1031931");
                }
                String forwScore ="" ;
                MatchSettleScore matchSettleBefore = new MatchSettleScore();
                MatchSettleScore matchSettleScore  = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if (matchSettleScore == null) {
                    return Response.failed("1031931");
                }
                //删除事件不能编辑
                if (null!=matchSettleScore.getHasDeleteEvent()&&matchSettleScore.getHasDeleteEvent()==1){
                    return Response.failed("1031961");
                }
                //篮球结算顺序拦截
                if(matchSettleScore.getSportId().equals(2L)){
                    if(!matchSettleCheckInfoHelper.checkBasketPeriodScoreOrder(matchSettleScore)){
                        return Response.failed("10138");
                    }
                }
                //修改前比分
                forwScore= matchSettleScore.getT1()+"-"+ matchSettleScore.getT2();
                String t1 =matchSettleScore.getT1()==null ?"":matchSettleScore.getT1().toString();
                String t2 =matchSettleScore.getT2()==null ?"":matchSettleScore.getT2().toString();
                forwScore= t1+"-"+t2;

                BeanUtils.copyProperties(matchSettleScore,matchSettleBefore);
                matchSettleScore.setT1(matchSettleScoreDto.getT1());
                matchSettleScore.setT2(matchSettleScoreDto.getT2());
                matchSettleScore.setStatus(NOT_CONFIRM);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                if(matchSettleScoreDto.getGoWaterStatus()!=null&&matchSettleScoreDto.getGoWaterStatus()==1){
                    matchSettleScore.setGoWaterStatus(1);
                }else {
                    matchSettleScore.setGoWaterStatus(0);
                }
                matchSettleScore.setExtryInfo(matchSettleScoreDto.getExtryInfo());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                //比分判断是否相同
//                if(MatchEventInfoSettleUtils.equileMatchSettleScoresV2(matchSettleBefore,matchSettleScore)){
//                    return Response.failed("1031940");
//                }
                matchSettleScoreRepository.updateById(matchSettleScore);
                //2.判断更新上半场(5)和全场比分(10) 更新结算信息
                if (matchSettleScore.getSettleNum().equals("bk_1ht") || matchSettleScore.getSettleNum().equals("bk_ft_rg")) {
                    recordScore(matchSettleScoreDto);
                }

                wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                //3.操作日志记录
                String settleScoreJson = JSON.toJSONString(matchSettleScoreDto);
                UpdateMatchSettleScoreDto updateMatchSettleScoreDto = JSON.parseObject(settleScoreJson,UpdateMatchSettleScoreDto.class);
                updateMatchSettleScoreDto.setBasketBallSettleNum(matchSettleScoreDto.getSettleNum());
                matchSettleOperateLogService.updateMatchSettleScoreAddLog(updateMatchSettleScoreDto,forwScore,matchSettleScore,standardMatchInfo, OperateLogTypeEnum.EDIT.getCode().toString());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-updateMatchSettleScore:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }


    @Override
    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        log.info("比分Id::{}:: 当前比分被确认参数:{} ",matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto);
        //0.加redis锁
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore =null;
                matchSettleScore=matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if(matchSettleScore==null){
                    return Response.failed("1031931");
                }
                if(matchSettleScore.getStatus()>=CONFIRM){
                    return Response.failed("1031934");
                }
                Integer status = matchSettleScore.getStatus();
                matchSettleScore.setStatus(CONFIRM);
                matchSettleScore.setT1(matchSettleScoreDto.getT1());
                matchSettleScore.setT2(matchSettleScoreDto.getT2());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScoreRepository.updateById(matchSettleScore);
                //2.记录日志
                //走水  将编码设置为8
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1)){
                    matchSettleScore.setExtryInfo("8");
                }
                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
                        OperateLogTypeEnum.CONFIRM_SCORE,"",matchSettleScoreDto.getIpAddress());
                wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-confirmMatchSettleScore:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
        log.info("basketball settleMatchScore-v2,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
            return Response.failed("1031960");
        }
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore =null;
                matchSettleScore=matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if(matchSettleScore==null){
                    return Response.failed("1031931");
                }
                if(matchSettleScore.getStatus()!=CONFIRM){
                    return Response.failed("1031932");
                }
                Integer settleTimes =matchSettleScore.getSettleTimes();
                if(settleTimes==null){
                    settleTimes=0;
                }
                if (matchSettleScore.getSettleCount()== null ) {
                    matchSettleScore.setSettleCount(0);
                }
                settleTimes++;

                //二次结算,必须给出结算原因
                if (matchSettleScore.getSettleCount() >  0 &&
                        (matchSettleScoreDto.getSettleReason()==null  ||
                                matchSettleScoreDto.getSettleReason()== 0) ) {
                    return Response.failed("1031953");
                }
                String  before= "-";
                Integer settleReason = matchSettleScore.getSettleReason();
                if (settleReason != null &&  settleReason != 0 ) {
                    before = settleReason.toString();
                    if (settleReason == 118) {
                        before += ": "+matchSettleScore.getSettleReasonDetail();
                    }
                }
                //这是理论时间不对 应该先查数据商，如果没数据商再赋值当前
                if(matchSettleScore.getEventTime()==null||matchSettleScore.getEventTime().equals(0l)){
                    Long eventTime = matchSettleCheckInfoHelper.searchEventTimeByScores(matchSettleScore);
                    if(eventTime==0l){
                        eventTime=matchSettleScore.getModifyTime();
                    }
                    matchSettleScore.setEventTime(eventTime);
                }
                matchSettleScore.setStatus(SETTLED);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleScore.setSettleTimes(settleTimes);
                matchSettleScore.setSettleCount(matchSettleScore.getSettleCount()+1);
                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.SETTLE);
                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScore.setSettleReason(matchSettleScoreDto.getSettleReason());
                matchSettleScore.setSettleReasonDetail(matchSettleScoreDto.getSettleReasonDetail());
                matchSettleScore.setIsGrey(0);
                matchSettleScore.setHasDeleteEvent(0);
                matchSettleScore.setCurrentEventStatus(0);

                List<String> l = new ArrayList();
                l.add(matchSettleScore.getSettleNum());
                List<MatchSettleThirdScore> matchSettleThirdScores =matchSettleThirdScoreRepository.getModelByStandardMatchIdAndSettleNum(matchSettleScore.getStandardMatchId(),l);
                boolean tag =false;
                if (!matchSettleThirdScores.isEmpty()){
                    for (int i =0;i<matchSettleThirdScores.size();i++ ){
                        if (!matchSettleThirdScores.get(i).getT1().equals(matchSettleScore.getT1())||!matchSettleThirdScores.get(i).getT2().equals(matchSettleScore.getT2())){
                            tag = true;
                        }
                    }
                }
                if (tag){
                    matchSettleScore.setCurrentEventTag(1);
                    MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScore.getStandardMatchId());
                    matchSettleInfo.setCurrentEventTag(1);
                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                }
                //篮球结算后去掉比分带入弹框
                matchSettleScore.setPopupUsers(null);
                matchSettleScoreRepository.updateById(matchSettleScore);
                matchSettleInfoHelper.updateMatchGrayStatus(matchSettleScore.getStandardMatchId());
                matchSettleInfoHelper.updateMatchCurrentEventStatus(matchSettleScore.getStandardMatchId());
                //校驗比分更新標記
                matchSettleScoreHelper.verifyScoresIsSame(matchSettleScore.getStandardMatchId());
                //结算时把回滚订单数清零
                matchServiceHelper.settleRollBackSetNullOrderCount(matchSettleScore.getId());
                MatchSettleScore score = new MatchSettleScore();
                BeanUtils.copyProperties(matchSettleScore,score);
                matchSettleScoresProducer.sendMatchSettleScores(score);
                wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                        matchSettleScoreDto.getEventCode());

                //1.比分结算增加操作日志
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1)) {
                    matchSettleScore.setExtryInfo("8");
                }
                if (matchSettleScoreDto.getSettleReason() != null) {
                    matchSettleScoreDto.setSettleReasonDetail(matchSettleScore.getSettleNum());
                    matchSettleEventService.secondSettleWarnMango(matchSettleScoreDto,2);
                }

                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),OperateLogTypeEnum.SCORE_SETTLE,before,matchSettleScoreDto.getIpAddress());


                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-settleMatchScore:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response reSettleMatchScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        log.info("basketball reSettleMatchScore-v2,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
        try {
            if(redisService.tryLock(key,key,2,5)) {
                MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                if(matchSettleScore==null){
                    return Response.failed("1031935");
                }
                Integer settleTimes =matchSettleScore.getSettleTimes();
                if(settleTimes!=null&&settleTimes>0){
                }else {
                    return Response.failed("1031938");
                }
                matchSettleScore.setModifyTime(System.currentTimeMillis());
                matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                matchSettleScore.setSettleTimes(settleTimes);
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
                matchSettleScoreRepository.updateById(matchSettleScore);

                //2.MQ下发
                MatchSettleScoreMessage Score = new MatchSettleScoreMessage();
                BeanUtils.copyProperties(matchSettleScore,Score);
                Score.setLevel(3);
                matchSettleScoresProducer.sendMatchSettleScores(Score);


                //1.比分结算增加操作日志
                //走水设置编码为8
                if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1))  matchSettleScore.setExtryInfo("8");
                matchSettleOperateLogService.matchSettleScoreAddLog(matchSettleScore,matchSettleScoreDto.getOperatorName(),
                        OperateLogTypeEnum.ROLLBACK_EXECUTE,"",matchSettleScoreDto.getIpAddress());

                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-reSettleMatchScore:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response rollBackSettleMatchScores(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        log.info("basketball rollBackSettleMatchScores,matchSettleScoreDto: {}",matchSettleScoreDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleScoreDto.getMatchScoreId();
        if(matchServiceHelper.checkIfOverSettleTime(matchSettleScoreDto.getStandardMatchId())){
            return Response.failed("1031930");
        }
        try {
            MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
            if(matchSettleScore==null){
                return Response.failed("1031935");
            }
            MatchSettleScoreEntity oIdMatchSettleScore = new   MatchSettleScoreEntity();
            BeanUtils.copyProperties(matchSettleScore,oIdMatchSettleScore);
            matchSettleScore.setGoWaterStatus(0);
            matchSettleScore.setStatus(NOT_EDIT);
            matchSettleScore.setT1(null);
            matchSettleScore.setT2(null);
            matchSettleScore.setExtryInfo(null);
            matchSettleScore.setFirstT1(null);
            matchSettleScore.setFirstT2(null);
            matchSettleScore.setSecondT1(null);
            matchSettleScore.setSecondT2(null);
            matchSettleScore.setSettleTimes(0);
            matchSettleScore.setModifyTime(System.currentTimeMillis());
            matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.ROLL_BACK);
            matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
            matchSettleScore.setUserid(matchSettleScoreDto.getOperatorId());
            matchSettleScore.setSettleReasonDetail(null);
            matchSettleScore.setSettleReason(null);
            matchSettleScore.setPopupUsers(null);
            matchSettleScoreRepository.updateById(matchSettleScore);
            //将核对信息进行无效处理
            matchSettleCheckInfoHelper.rollbackScores(matchSettleScore);
            MatchSettleScore score = new MatchSettleScore();
            BeanUtils.copyProperties(matchSettleScore,score);
            //2.MQ下发
            matchSettleScoresProducer.sendMatchSettleScores(score);
            wsPushService.pushBasketballStandardSettleScores(matchSettleScoreDto.getStandardMatchId(),
                    matchSettleScoreDto.getEventCode());

            //1.记录日志
            matchSettleOperateLogService.matchSettleScoreAddLog(oIdMatchSettleScore,matchSettleScore,matchSettleScoreDto.getOperatorName(),
                    OperateLogTypeEnum.ROLLBACK_SCORES_SETTLE.getCode().toString(),matchSettleScoreDto.getLinkedId(),matchSettleScoreDto.getIpAddress());
            //回滚新增记录
            matchServiceHelper.insertRollbackData(matchSettleScoreDto.getStandardMatchId(),matchSettleScoreDto.getMatchScoreId(),1,matchSettleScoreDto.getEventCode(),matchSettleScore.getSettleNum());
            //回滚保存赛事ID一分钟
            redisService.set("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto.getMatchScoreId(),60);
            return Response.success();
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-rollBackSettleMatchScores:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public void updateMatchFifteenMinGraySettleFactor(Long standardMatchId,String settleNum) {
        try {
            //1,判断是否是上,下的6个15分钟区间
            String fifteenSettleNum = grayIntervalServiceHelper.fifteenMinSettleNumMap.get(settleNum);
            if (StringUtils.isAnyEmpty(fifteenSettleNum)) {
                return;
            }
            //2,判断半场是否已经结算,未结算直接返回
            List<MatchSettleScore> matchSettleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(Arrays.asList(fifteenSettleNum), standardMatchId, Arrays.asList(SETTLED));
            if (matchSettleScoreList.isEmpty()){
                return;
            }
            //3,半场已经结算,判断已经结算的阶段总比分是否跟半场一致,如果一致,取消半场还未结算的灰色区间
            MatchSettleScore matchSettleScoreHalfTime = matchSettleScoreList.get(NOT_EDIT);
            List<String> settleNumList = MatchPeriodEnum.getFootBallPeriodSettleNumList(fifteenSettleNum);
            List<MatchSettleScore> matchSettleNumList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList, standardMatchId, Arrays.asList(SETTLED));
            if (matchSettleNumList.isEmpty()){
                return;
            }
            Integer sumScoreT1 = 0;
            Integer sumScoreT2 = 0;
            for (MatchSettleScore settleScore : matchSettleNumList) {
                if (settleScore.getT1() != null && settleScore.getT1() > 0) {
                    sumScoreT1 += settleScore.getT1();
                }
                if (settleScore.getT2() != null && settleScore.getT2() > 0) {
                    sumScoreT2 += settleScore.getT2();
                }
            }
            if (matchSettleScoreHalfTime.getT1() != null && matchSettleScoreHalfTime.getT2() != null && matchSettleScoreHalfTime.getT1().equals(sumScoreT1) && matchSettleScoreHalfTime.getT2().equals(sumScoreT2)) {
                MatchSettleScoreExample matchSettleScoreGrayExample = new MatchSettleScoreExample();
                matchSettleScoreGrayExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andStatusEqualTo(NOT_EDIT).andSettleNumIn(settleNumList);
                List<MatchSettleScore> matchSettleScoreGrayList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList, standardMatchId, Arrays.asList(NOT_EDIT));
                for (MatchSettleScore matchSettleScoreGray:matchSettleScoreGrayList){
                    matchSettleScoreGray.setIsGrey(NOT_EDIT);
                    matchSettleScoreGray.setCurrentEventStatus(NOT_EDIT);
                    MatchSettleScoreEntity matchSettleScoreEntity = matchSettleScoreConverter.convertMatchSettleScoreToEntity(matchSettleScoreGray);
                    matchSettleScoreRepository.updateById(matchSettleScoreEntity);
                }
            }
        }catch (Exception e){
            log.error("标准赛事Id:"+standardMatchId+",更新15分钟灰色区间:"+settleNum+",的结算因子出错:",e);
        }
    }

    public void endEventSettleByScore(MatchSettleScore matchSettleScore) {
        //0.事件编码分类
        List<String> eventCodes = EndEventUtils.eventCodesFootballByEventCode(matchSettleScore.getEventCode());
        if(eventCodes.size()==0){
            return;
        }
        //1.阶段条件获取 上半场 或者全场
        List<Long> periods =  EndEventUtils.periodsFootballByScorePeriod(matchSettleScore.getPeriodId());
        //不是31 也不是100 事件则直接返回
        if(periods==null){
            return;
        }
        //2.查询对应事件编码和阶段编码已经结算的事件
        //3.取比分最大的事件
        List<MatchSettleEvent> eventList = matchSettleEventRepository.getModelsByItems(matchSettleScore.getStandardMatchId(),eventCodes,periods,SETTLED,EndEventUtils.HOME_AWAY);
        if(eventList.size()==0){
            return;
        }
        Integer t1=0;
        Integer t2=0;
        String homeAway ="none";
        Long id =null;
        for (MatchSettleEvent matchSettleEvent : eventList) {
            if(matchSettleEvent.getT1()!=null&&matchSettleEvent.getT2()!=null){
                Integer sum = matchSettleEvent.getT1()+matchSettleEvent.getT2();
                if((t1+t2)<=sum){
                    //罚牌比分也是取 事件的 t1 t2
                    t1=matchSettleEvent.getT1();
                    t2=matchSettleEvent.getT2();
                    homeAway=matchSettleEvent.getHomeAway();
                    id=matchSettleEvent.getId();
                }
            }
        }
        //id= null 取不到对应事件过滤
        if(id==null){
            return;
        }else {
            //还有可能 结算的事件比分是0 则无需编辑 或者编辑为none
            if(!EndEventUtils.HOME_AWAY.contains(homeAway)){
                homeAway="none";
            }
        }
        //4.根据比分最大的事件和结算事件做比对
        //4.1如果相等 则编辑addition1 或者 addition2 主客队
        if(matchSettleScore.getT1()!=null&&matchSettleScore.getT2()!=null){
            if(matchSettleScore.getT1().equals(t1)&&matchSettleScore.getT2().equals(t2)){
                //如果是全场打完 则编辑 add2
                if(matchSettleScore.getPeriodId().equals(100L)){
                    matchSettleScore.setAddition2(homeAway);
                    //如果是上半场休息 则编辑add1
                }else if(matchSettleScore.getPeriodId().equals(31L)) {
                    matchSettleScore.setAddition1(homeAway);
                }
                log.info("结算比分编辑最终事件::赛事id：{}，选择事件id:{},事件阶段:{},事件类型:{} add1:{} add2:{}",
                        matchSettleScore.getStandardMatchId(),id,matchSettleScore.getPeriodId(),matchSettleScore.getEventCode()
                        ,matchSettleScore.getAddition1(),matchSettleScore.getAddition2());
            }
        }else {
            //4.2如果不相等 则直接返回
            return;
        }
    }

    @Override
    public Response querySettleType(Long StandardMatchId) {
        //1.查询结算信息
        MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(StandardMatchId);
        if (matchSettleInfo == null || matchSettleInfo.getSettleType()==1) {
            return   Response.success(1);
        }else {
            return   Response.success(2);
        }
    }

    @Override
    public Response cancelSettleEventTag(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        log.info("basketball cancelSettleEventTag-v2,matchSettleSwitcherDto: {}",matchSettleSwitcherDto);
        try {
            if(matchSettleSwitcherDto.getMatchScoreId()!=null && matchSettleSwitcherDto.getMatchScoreId()>0L){
                MatchSettleScore settleScore = matchSettleScoreRepository.getById(matchSettleSwitcherDto.getMatchScoreId());
                if(settleScore!=null){
                    settleScore.setCurrentEventTag(0);
                    matchSettleScoreRepository.updateById(settleScore);
                }
                boolean tag = true;
                MatchSettleScoreExample example = new MatchSettleScoreExample();
                example.createCriteria().andStandardMatchIdEqualTo(matchSettleSwitcherDto.getMatchId());
                //重新查一遍结算表,判断还有没有提示标记,假如没有则更新结算赛事表信息
                List<MatchSettleScore> settleScores = matchSettleScoreRepository.getByStandardMatchIdAndEventCode(matchSettleSwitcherDto.getMatchId(),null);
                for (int i = 0;i<settleScores.size();i++){
                    if (null!=settleScores.get(i).getCurrentEventTag()&&settleScores.get(i).getCurrentEventTag()==1){
                        tag = false;
                        break;
                    }
                }
                if (tag){
                    MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleSwitcherDto.getMatchId());
                    matchSettleInfo.setCurrentEventTag(0);
                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                }

            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-cancelSettleEventTag:",e);
            return Response.failed();
        }
        return Response.success();
    }

    @Override
    public Response showPopupScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        log.info("showPopupScore-v2传入参数: {}",matchSettleScoreDto);
        //获取当前需要结算的阶段之前的阶段
        List<String> settleNumList = SettleNumUtils.countBasketballScoreSettleNumBefore(matchSettleScoreDto.getSettleNum(),matchSettleScoreDto.getMatchLength());
        List<MatchSettleScore> settleScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList,matchSettleScoreDto.getStandardMatchId(),null);
        if (!settleScores.isEmpty()){
            for (int i = 0;i<settleScores.size();i++){
                if (!settleScores.get(i).getStatus().equals(3)){
                    return Response.failed("该阶段前有未结算的比分，无法同步比分");
                }
            }

        }
        //假如是审核员,则去取核对记录表中的确认比分填充进去
        if(matchSettleScoreDto.getRoleCode()==2){
            List<MatchSettleCheckInfoEntity> checkInfos = matchSettleCheckInfoRepository.getDoShowPopupScore(2,matchSettleScoreDto.getMatchScoreId(),matchSettleScoreDto.getOperatorName(),matchSettleScoreDto.getStandardMatchId());
            if (CollectionUtils.isEmpty(checkInfos)){
                return Response.failed("审核员核对数据,弹框数据异常");
            }else {
                MatchSettleCheckInfoEntity checkInfo = checkInfos.get(0);
                matchSettleScoreDto.setT1(checkInfo.getT1());
                matchSettleScoreDto.setT2(checkInfo.getT2());
            }

        }

        List<MatchSettleScore> needSettleScores = new ArrayList<>();
        List<String> needSettleNum = new ArrayList<>();
        List<BasketBallPopupSettleScoreDto> popupSettleScores = new ArrayList<>();
        //case 1: 确认的是第二节比分 settle_num:bk_q204,则带出 上半场bk_1ht比分:Q1+Q2
        if (matchSettleScoreDto.getSettleNum().equals("bk_q204")){
            needSettleNum.add("bk_1ht");
            int home1ht =matchSettleScoreDto.getT1();
            int away1ht =matchSettleScoreDto.getT2();
            for (int i =0;i<settleScores.size();i++){
                if (settleScores.get(i).getSettleNum().equals("bk_q104")){
                    home1ht = home1ht+settleScores.get(i).getT1();
                    away1ht = away1ht+settleScores.get(i).getT2();
                }
            }
            List<MatchSettleScore> caseOne = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(needSettleNum,matchSettleScoreDto.getStandardMatchId(),null);
            if(caseOne.get(0).getStatus()!=3){
                BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
                dto.setMatchScoreId(caseOne.get(0).getId());
                dto.setStandardMatchId(caseOne.get(0).getStandardMatchId());
                dto.setEventCode(caseOne.get(0).getEventCode());
                dto.setSettleNum(caseOne.get(0).getSettleNum());
                dto.setT1(home1ht);
                dto.setT2(away1ht);
                dto.setSort(1);
                popupSettleScores.add(dto);
            }
        }
        /*case 2:确认的是第四节比分  settle_num:bk_q404,则带出
         1  下半场比分 bk_2ht:Q3+Q4
         2  全场不含加时比分 bk_ft_rg:Q1+Q2+Q3+Q4
         3  假如 全场不含加时比分 bk_ft_rg 不相等,这此赛事无加时赛,需要带出  全场比分(含加时):bk_ft_et比分:Q1+Q2+Q3+Q4
         */
        if (matchSettleScoreDto.getSettleNum().equals("bk_q404")){
            needSettleNum.add("bk_2ht");
            needSettleNum.add("bk_ft_rg");
            int home2ht =matchSettleScoreDto.getT1();
            int away2ht =matchSettleScoreDto.getT2();
            int homeRgEt =matchSettleScoreDto.getT1();
            int awayRgEt =matchSettleScoreDto.getT2();

            for (int i =0;i<settleScores.size();i++){
                if (settleScores.get(i).getSettleNum().equals("bk_q304")){
                    home2ht = home2ht+settleScores.get(i).getT1();
                    away2ht = away2ht+settleScores.get(i).getT2();
                    homeRgEt = homeRgEt+settleScores.get(i).getT1();
                    awayRgEt = awayRgEt+settleScores.get(i).getT2();
                }
                if (settleScores.get(i).getSettleNum().equals("bk_q104")||settleScores.get(i).getSettleNum().equals("bk_q204")){
                    homeRgEt = homeRgEt+settleScores.get(i).getT1();
                    awayRgEt = awayRgEt+settleScores.get(i).getT2();
                }

            }
            if (homeRgEt!=awayRgEt) {
                needSettleNum.add("bk_ft_et");
                needSettleNum.add("bk_2htet");
            }
            List<MatchSettleScore> caseTwo = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(needSettleNum,matchSettleScoreDto.getStandardMatchId(),null);
            for (int i =0;i<caseTwo.size();i++){
                if (caseTwo.get(i).getSettleNum().equals("bk_2ht")&&caseTwo.get(i).getStatus()!=3){
                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
                    dto.setMatchScoreId(caseTwo.get(i).getId());
                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
                    dto.setEventCode(caseTwo.get(i).getEventCode());
                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
                    dto.setT1(home2ht);
                    dto.setT2(away2ht);
                    dto.setSort(2);
                    popupSettleScores.add(dto);
                }
                if (caseTwo.get(i).getSettleNum().equals("bk_ft_rg")&&caseTwo.get(i).getStatus()!=3){
                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
                    dto.setMatchScoreId(caseTwo.get(i).getId());
                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
                    dto.setEventCode(caseTwo.get(i).getEventCode());
                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
                    dto.setT1(homeRgEt);
                    dto.setT2(awayRgEt);
                    dto.setSort(4);
                    popupSettleScores.add(dto);
                }
                if (caseTwo.get(i).getSettleNum().equals("bk_ft_et")&&caseTwo.get(i).getStatus()!=3){
                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
                    dto.setMatchScoreId(caseTwo.get(i).getId());
                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
                    dto.setEventCode(caseTwo.get(i).getEventCode());
                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
                    dto.setT1(homeRgEt);
                    dto.setT2(awayRgEt);
                    dto.setSort(5);
                    popupSettleScores.add(dto);
                }
                if (caseTwo.get(i).getSettleNum().equals("bk_2htet")&&caseTwo.get(i).getStatus()!=3){
                    BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
                    dto.setMatchScoreId(caseTwo.get(i).getId());
                    dto.setStandardMatchId(caseTwo.get(i).getStandardMatchId());
                    dto.setEventCode(caseTwo.get(i).getEventCode());
                    dto.setSettleNum(caseTwo.get(i).getSettleNum());
                    dto.setT1(home2ht);
                    dto.setT2(away2ht);
                    dto.setSort(3);
                    popupSettleScores.add(dto);
                }
            }
        }
        //case 3:确认的比分是 下半场阶段OT bk_et,则需要带出 全场比分(含加时):bk_ft_et比分:Q1+Q2+Q3+Q4+OT
        if (matchSettleScoreDto.getSettleNum().equals("bk_et")){
            needSettleNum.add("bk_ft_et");
            needSettleNum.add("bk_2htet");
            int homeFtEt =matchSettleScoreDto.getT1();
            int awayFtEt =matchSettleScoreDto.getT2();
            int homeHtEt =matchSettleScoreDto.getT1();
            int awayHtEt =matchSettleScoreDto.getT2();
            for (int i =0;i<settleScores.size();i++){
                if (settleScores.get(i).getSettleNum().equals("bk_ft_rg")){
                    homeFtEt = homeFtEt+settleScores.get(i).getT1();
                    awayFtEt = awayFtEt+settleScores.get(i).getT2();
                }
                if (settleScores.get(i).getSettleNum().equals("bk_2ht")){
                    homeHtEt = homeHtEt+settleScores.get(i).getT1();
                    awayHtEt = awayHtEt+settleScores.get(i).getT2();

                }
            }
            if (matchSettleScoreDto.getMatchLength()==73){ //3*3没有下半场含加时
                needSettleNum.remove("bk_2htet");
            }
            List<MatchSettleScore> caseOne = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(needSettleNum,matchSettleScoreDto.getStandardMatchId(),null);
            if (!CollectionUtils.isEmpty(caseOne)){
                for (int i =0;i<caseOne.size();i++){
                    if (caseOne.get(i).getStatus()!=3&&caseOne.get(i).getSettleNum().equals("bk_ft_et")){
                        BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
                        dto.setMatchScoreId(caseOne.get(i).getId());
                        dto.setStandardMatchId(caseOne.get(i).getStandardMatchId());
                        dto.setEventCode(caseOne.get(i).getEventCode());
                        dto.setSettleNum(caseOne.get(i).getSettleNum());
                        dto.setT1(homeFtEt);
                        dto.setT2(awayFtEt);
                        dto.setSort(5);
                        popupSettleScores.add(dto);
                    }
                    if (caseOne.get(i).getStatus()!=3&&caseOne.get(i).getSettleNum().equals("bk_2htet")){
                        BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();
                        dto.setMatchScoreId(caseOne.get(i).getId());
                        dto.setStandardMatchId(caseOne.get(i).getStandardMatchId());
                        dto.setEventCode(caseOne.get(i).getEventCode());
                        dto.setSettleNum(caseOne.get(i).getSettleNum());
                        dto.setT1(homeHtEt);
                        dto.setT2(awayHtEt);
                        dto.setSort(3);
                        popupSettleScores.add(dto);
                    }

                }
            }

        }
        //假如不是高级权限并且非第一审核员,则需要校验他前面的审核员是否有带入比分
        if (null!=matchSettleScoreDto.getRoleCode()&&matchSettleScoreDto.getRoleCode()==2&&!CollectionUtils.isEmpty(popupSettleScores)){
            MatchSettleInfoEntity info = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDto.getStandardMatchId());
            if(StringUtils.isNotEmpty(info.getAuditorJson())){
                ArrayList<String> list = JSON.parseObject(info.getAuditorJson(), ArrayList.class);
                if (!CollectionUtils.isEmpty(list)){
                    int index = list.indexOf(matchSettleScoreDto.getOperatorName());
                    //不是第一审核员
                    if (index>0){
                        List<String> users = list.subList(0,list.indexOf(matchSettleScoreDto.getOperatorName()));
                        List<String> settleNums = popupSettleScores.stream().map(BasketBallPopupSettleScoreDto::getSettleNum).collect(Collectors.toList());
                        boolean tag = true;
                        for (int i =0;i<users.size();i++){
                            //需要带入的阶段是否已经被前面的审核员全部带入过
                            /**
                             * tag 重构空缺，需要补充方法
                             */
                            int num = matchSettleCheckInfoRepository.countBySettleNumAndUser(users.get(i),2,settleNums,matchSettleScoreDto.getStandardMatchId());
                            if (num==settleNums.size()){
                                tag = false;
                                break;
                            }
                        }
                        //假如前面审核员带入的比分条数不等于需要带入的比分条数,则说明前面审核员没有带入完整,此时后续核员不能带入比分
                        if (tag){
                            return Response.failed("该审核员之前的审核员没有带入比分");
                        }
                    }
                }
            }
        }

        //假如此阶段需要弹出比分,则将当前登录用户存入,用来展示弹框
        if (!popupSettleScores.isEmpty()){
            MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
            //由于两个审核员操作确认的时候,后台会先结算比分 此时就不应该有弹出标记
            if (matchSettleScore.getStatus()!=3){
                List<PopupUserDto> popUsers = new ArrayList<>();
                if (null!=matchSettleScore.getPopupUsers()){
                    JSONArray array = JSONArray.parseArray(matchSettleScore.getPopupUsers());
                    if (null!=array){
                        for (Object o : array) {
                            PopupUserDto dto = JSONObject.toJavaObject((JSONObject)o,PopupUserDto.class);
                            popUsers.add(dto);
                        }
                    }
                }
                boolean tag = true;
                for (PopupUserDto p:popUsers){
                    if (null!=p.getPopupUser()&&p.getPopupUser().equals(matchSettleScoreDto.getOperatorName())){
                        tag =false;
                    }
                }
                if (tag){
                    PopupUserDto popupUser = new PopupUserDto();
                    popupUser.setPopupUser(matchSettleScoreDto.getOperatorName());
                    popUsers.add(popupUser);
                    matchSettleScore.setPopupUsers(JSONArray.toJSONString(popUsers));
                    matchSettleScoreRepository.updateById(matchSettleScore);
                }
            }


        }
        return Response.success(popupSettleScores);
    }

    @Override
    public Response confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto) {
        String key ="StandardMatchScoreConsumer:"+basketBallPutInJsonDto.getStandardMatchId();

        if (redisService.tryLock(key, key, 2, 5)) {
            log.info("超级管理员confirmBringInScore入参-v2: {}",basketBallPutInJsonDto);
            Long standardMatchId = basketBallPutInJsonDto.getStandardMatchId();
            List<UpdateBasketBallSettleScoreDto> updateBasketBallSettleScores =new ArrayList<>();
            //解析需要带入的比分
            try {
                JSONArray array = JSONArray.parseArray(basketBallPutInJsonDto.getPutInJson());
                for (Object o : array) {
                    UpdateBasketBallSettleScoreDto updateBasketBallSettleScoreDto = JSONObject.toJavaObject((JSONObject)o,UpdateBasketBallSettleScoreDto.class);
                    updateBasketBallSettleScores.add(updateBasketBallSettleScoreDto);
                }
            }catch (Exception e){
                log.error("confirmBringInScore解析参数异常:",e);
                return Response.failed("解析参数异常");
            }
            log.info("解析后updateBasketBallSettleScores-v2: {}",updateBasketBallSettleScores);
            //判断带入的比分是否已经结算,假如已经结算则不带入
            List<String> settleNums = updateBasketBallSettleScores.stream().map(UpdateBasketBallSettleScoreDto::getSettleNum).collect(Collectors.toList());
            MatchSettleScoreExample example = new MatchSettleScoreExample();
            example.createCriteria().andSettleNumIn(settleNums).andStandardMatchIdEqualTo(standardMatchId).andStatusNotEqualTo(3);
            List<MatchSettleScore> matchSettleScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndNotStatus(settleNums,standardMatchId,3);
            if (!CollectionUtils.isEmpty(matchSettleScores)){
                matchSettleScores.forEach(matchSettleScore -> {
                    updateBasketBallSettleScores.forEach(updateBasketBallSettleScore ->{
                        if (matchSettleScore.getSettleNum().equals(updateBasketBallSettleScore.getSettleNum())){
                            matchSettleScore.setStatus(CONFIRM);
                            matchSettleScore.setT1(updateBasketBallSettleScore.getT1());
                            matchSettleScore.setT2(updateBasketBallSettleScore.getT2());
                            matchSettleScore.setModifyTime(System.currentTimeMillis());
                            matchSettleScoreRepository.updateById(matchSettleScore);
                            if (matchSettleScore.getSettleNum().equals("bk_1ht") || matchSettleScore.getSettleNum().equals("bk_ft_rg")) {

                                UpdateBasketBallSettleScoreDto dto = new UpdateBasketBallSettleScoreDto();
                                dto.setSettleNum(matchSettleScore.getSettleNum());
                                dto.setT1(matchSettleScore.getT1());
                                dto.setT2(matchSettleScore.getT2());
                                dto.setStandardMatchId(matchSettleScore.getStandardMatchId());
                                log.info("进入比分带入列表比分dto: "+dto);
                                recordScore(dto);
                            }
                            //2.记录日志
                            //走水  将编码设置为8
                            if (matchSettleScore.getGoWaterStatus()!=null&&matchSettleScore.getGoWaterStatus().equals(1)){
                                matchSettleScore.setExtryInfo("8");
                            }
                            MatchSettleScore score = new MatchSettleScore();
                            BeanUtils.copyProperties(matchSettleScore,score);
                            matchSettleOperateLogService.matchSettleScoreAddLog(score,updateBasketBallSettleScore.getOperatorName(),
                                    OperateLogTypeEnum.CONFIRM_SCORE,"",updateBasketBallSettleScore.getIpAddress());
                            log.info("updateBasketBallSettleScore_StandardMatchId: {},EventCode: {}",updateBasketBallSettleScore.getStandardMatchId(),updateBasketBallSettleScore.getEventCode());
                            wsPushService.pushBasketballStandardSettleScores(updateBasketBallSettleScore.getStandardMatchId(),
                                    updateBasketBallSettleScore.getEventCode());

                        }

                    });
                });
            }
            return Response.success();
        } else {
            log.error("confirmBringInScore standardMatchId::{}::比分无法获取redis锁",basketBallPutInJsonDto.getStandardMatchId());
        }
        return Response.failed("比分带入时，比分无法获取redis锁,请重试!");
    }

    @Override
    public Response editShowScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        log.info("basketball editShowScore,matchSettleScoreDto-v2: {}",matchSettleScoreDto);
        List<LimitSwitchDto> l = matchServiceHelper.getBasketInSettleTimeLimit(2l);
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreDto.getStandardMatchId());
        StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
        int level = standardSportTournament.getTournamentLevel();
        boolean tag = false;
        for (int i =0;i<l.size();i++){
            if (level==l.get(i).getLevel()){
                tag = l.get(i).getOnOff();
            }
        }
        if (!tag){
            return Response.success();
        }
        List<Long> standardMatchIds = new ArrayList<>();
        standardMatchIds.add(matchSettleScoreDto.getStandardMatchId());
        List<StandardSportMarketSell> sells = standardSportMarketSellService.getItems(standardMatchIds);
        if (sells.size() == 0) {
            return Response.failed();
        }
        String businessEvent = sells.get(0).getBusinessEvent();
        BasketBallPopupSettleScoreDto dto = new BasketBallPopupSettleScoreDto();

        List<MatchSettleThirdScore> thirdScores = matchSettleThirdScoreRepository.getByMatchIdAndAndDataSourceCodeSettleNum(matchSettleScoreDto.getStandardMatchId(),null,businessEvent,Arrays.asList(matchSettleScoreDto.getSettleNum()));
        if (!CollectionUtils.isEmpty(thirdScores)){
            MatchSettleThirdScore thirdScore = thirdScores.get(0);
            if (null!=thirdScore.getT1()&&null!=thirdScore.getT2()){
                dto.setT1(thirdScore.getT1());
                dto.setT2(thirdScore.getT2());
                dto.setSettleNum(thirdScore.getSettleNum());
                return Response.success(dto);
            }
        }

        return Response.success();
    }

//    @Override
//    public Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto) {
//        log.info("basketball cancelDeleteStatus,matchSettleSwitcherDto-v2: {}",matchSettleSwitcherDto);
//        String key ="MATCH_SETTLE_INFO:"+ matchSettleSwitcherDto.getMatchId();
//        //1.查询是否已经存在人工核对事件
//        try {
//            //3.更新或者新增
//            if(redisService.tryLock(key,key,2,5)) {
//                if(matchSettleSwitcherDto.getMatchScoreId()!=null && matchSettleSwitcherDto.getMatchScoreId()>0L){
//                    MatchSettleScore settleScore = matchSettleScoreRepository.getById(matchSettleSwitcherDto.getMatchScoreId());
//                    if(settleScore!=null){
//                        settleScore.setHasDeleteEvent(0);
//                        settleScore.setCurrentEventStatus(settleScore.getIsGrey());
//                        matchSettleScoreRepository.updateById(settleScore);
//                        matchSettleOperateLogService.deleteSettleAlertLog(settleScore,matchSettleSwitcherDto);
//                    }
//                }
//                MatchSettleScoreExample example = new MatchSettleScoreExample();
//                example.createCriteria().andStandardMatchIdEqualTo(matchSettleSwitcherDto.getMatchId());
//                List<MatchSettleScoreEntity> settleScores = matchSettleScoreRepository.getByStandardMatchIdAndEventCode(matchSettleSwitcherDto.getMatchId(),null);
//                int deleteGoal=0;
//                int grayGoal=0;
//                for (MatchSettleScoreEntity matchSettleScore : settleScores) {
//                    if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
//                        grayGoal=1;
//                    }
//                    if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
//                        deleteGoal=1;
//                    }
//                }
//                MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleSwitcherDto.getMatchId());
//                if(deleteGoal==1 && grayGoal==1){
//                    matchSettleInfo.setIsGray(1);
//                    matchSettleInfo.setHasDeleteEvent(1);
//                    matchSettleInfo.setCurrentEventStatus(1);
//                }else if(deleteGoal==1 && grayGoal==0){
//                    matchSettleInfo.setIsGray(0);
//                    matchSettleInfo.setHasDeleteEvent(1);
//                    matchSettleInfo.setCurrentEventStatus(2);
//                }else if(deleteGoal==0 && grayGoal==1){
//                    matchSettleInfo.setIsGray(1);
//                    matchSettleInfo.setHasDeleteEvent(0);
//                    matchSettleInfo.setCurrentEventStatus(1);
//                }else{
//                    matchSettleInfo.setIsGray(0);
//                    matchSettleInfo.setHasDeleteEvent(0);
//                    matchSettleInfo.setCurrentEventStatus(0);
//                }
//                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
//                return Response.success();
//            }else {
//                return Response.failed("1031933");
//            }
//        }catch (Exception e){
//            log.error("BasketballMatchScoresSettleApiImpl-cancelDeleteStatus:",e);
//            return Response.failed();
//        }finally {
//            redisService.unLock(key,key);
//        }
//    }



    @Override
    public Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        log.info("basketball cancelDeleteStatus,matchSettleSwitcherDto-v2: {}",matchSettleSwitcherDto);
        String key ="MATCH_SETTLE_INFO:"+ matchSettleSwitcherDto.getMatchId();
        //1.查询是否已经存在人工核对事件
        try {
            //3.更新或者新增
            if(redisService.tryLock(key,key,2,5)) {
                if(matchSettleSwitcherDto.getMatchScoreId()!=null && matchSettleSwitcherDto.getMatchScoreId()>0L){
                    MatchSettleScore settleScore = matchSettleScoreRepository.getById(matchSettleSwitcherDto.getMatchScoreId());
                    if(settleScore!=null){
                        settleScore.setHasDeleteEvent(0);
                        settleScore.setCurrentEventStatus(settleScore.getIsGrey());
                        matchSettleScoreRepository.updateById(settleScore);
                        matchSettleOperateLogService.deleteSettleAlertLog(settleScore,matchSettleSwitcherDto);
                    }
                }
                MatchSettleScoreExample example = new MatchSettleScoreExample();
                example.createCriteria().andStandardMatchIdEqualTo(matchSettleSwitcherDto.getMatchId());
                List<MatchSettleScore> settleScores = matchSettleScoreRepository.getByStandardMatchIdAndEventCode(matchSettleSwitcherDto.getMatchId(),null);
                int deleteGoal=0;
                int grayGoal=0;
                for (MatchSettleScore matchSettleScore : settleScores) {
                    if(matchSettleScore.getIsGrey()!=null&&matchSettleScore.getIsGrey()==1){
                        grayGoal=1;
                    }
                    if(matchSettleScore.getHasDeleteEvent()!=null&&matchSettleScore.getHasDeleteEvent()==1){
                        deleteGoal=1;
                    }
                }
                MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleSwitcherDto.getMatchId());
                if(deleteGoal==1 && grayGoal==1){
                    matchSettleInfo.setIsGray(1);
                    matchSettleInfo.setHasDeleteEvent(1);
                    matchSettleInfo.setCurrentEventStatus(1);
                }else if(deleteGoal==1 && grayGoal==0){
                    matchSettleInfo.setIsGray(0);
                    matchSettleInfo.setHasDeleteEvent(1);
                    matchSettleInfo.setCurrentEventStatus(2);
                }else if(deleteGoal==0 && grayGoal==1){
                    matchSettleInfo.setIsGray(1);
                    matchSettleInfo.setHasDeleteEvent(0);
                    matchSettleInfo.setCurrentEventStatus(1);
                }else{
                    matchSettleInfo.setIsGray(0);
                    matchSettleInfo.setHasDeleteEvent(0);
                    matchSettleInfo.setCurrentEventStatus(0);
                }
                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                return Response.success();
            }else {
                return Response.failed("1031933");
            }
        }catch (Exception e){
            log.error("BasketballMatchScoresSettleApiImpl-cancelDeleteStatus:",e);
            return Response.failed();
        }finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public boolean isPeriodScoresBeforeSettledByEvent(MatchSettleEvent matchSettleEvent) {
        String settleNum = null;
        //根据当前进球事件判断需要判断的阶段比分
        if (matchSettleEvent.getPeriodId() == 7l) {
            //获取上半场比分
            settleNum = "105";
        } else if (matchSettleEvent.getPeriodId() == 42l) {
            //获取加时赛上半场比分
            settleNum = "1014";
        }
        //如果为空则不需要判断
        if (settleNum == null) {
            return true;
        }
        MatchSettleScoreExample example = new MatchSettleScoreExample();
        //查询当前编辑的比分之前未结算的比分
        example.createCriteria().andSettleNumEqualTo(settleNum).andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).
                andStatusEqualTo(SETTLED).andEventCodeEqualTo("goal");
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelsByItemsAndSettleNums(matchSettleEvent.getStandardMatchId(),Arrays.asList("goal"),null,SETTLED,Arrays.asList(settleNum));
        if (list.size() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isFiveMinPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore) {
        //查询赛事结算表 看是否关闭五分钟顺序结算控制 为开  (null or 0)
        Long standardMatchId = matchSettleScore.getStandardMatchId();
        if (standardMatchId == null || standardMatchId == 0L) {
            return true;
        }
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
        if (matchSettleInfo == null) {
            return true;
        }
        if (matchSettleInfo.getFiveMinSwitch() != null &&
                matchSettleInfo.getFiveMinSwitch() != 0) {
            return true;
        }

        List<String> settleNumsBefore;

        settleNumsBefore = SettleNumUtils.getFiveMinPieriodScoresBeforeSettleNum(matchSettleScore.getSettleNum());

        if (settleNumsBefore.size() == 0) {
            return true;
        }
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndNotStatus(settleNumsBefore,matchSettleScore.getStandardMatchId(),SETTLED);
        if (list.size() != 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAllPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore) {
        boolean flag = true;
        //查询赛事结算表 看是否关闭顺序结算控制 为开  (null or 0)
        Long standardMatchId = matchSettleScore.getStandardMatchId();
        if (standardMatchId == null || standardMatchId == 0L) {
            flag = false;
        }
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getById(standardMatchId);
        if (matchSettleInfo == null) {
            flag = false;
        }
        if (matchSettleInfo.getSettleOrderClosed() != null &&
                matchSettleInfo.getSettleOrderClosed() != 0) {
            return true;
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
        List<MatchSettleScore> list = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndNotStatus(settleNumsBefore,matchSettleScore.getStandardMatchId(),SETTLED);
        if (list.size() != 0) {
            return false;
        }
        return true;
    }

    @Override
    public Response confirmMatchSettleScoreV2(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        log.info("basketball new confirmMatchSettleScore,matchSettleScoreDto-v2: {}",matchSettleScoreDto);
        //赛事id
        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
        //赛事比分id
        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
        //审核员姓名
        String userName = matchSettleScoreDto.getOperatorName();

        //1.判断是否已超过结算时间X
        if (matchServiceHelper.checkIfOverSettleTime(standardMatchId)) {
            return Response.failed("1031930");
        }
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
            return Response.failed("1031960");
        }
        String key = "NEW_MATCH_SETTLE_INFO:" + matchScoreId + "_" + userName;
        try {
            if (redisService.tryLock(key, key, 2, 5)) {
                //2.查询人工核对比分
                MatchSettleCheckInfoEntity matchSettleCheckInfo = matchSettleCheckInfoHelper.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(), matchSettleScoreDto.getStandardMatchId(), matchSettleScoreDto.getOperatorName());
                MatchSettleCheckInfoEntity checkInfo = new MatchSettleCheckInfoEntity();
                BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);

                MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                Integer checkNumber = matchSettleScore.getCheckNumber();
                if (matchSettleScore == null) {
                    return Response.failed("1031935");
                }
                if (matchSettleCheckInfo == null) {
                    return Response.failed("1031934");
                }
                if (matchSettleCheckInfo.getCheckStatus() != MatchSettleCheckConstant.CheckStatus.EDIT) {
                    log.error("人工核对确认比分状态错误:{}", matchSettleCheckInfo);
                    return Response.failed("1031934");
                }

                UpdateMatchSettleScoreDto dto = new UpdateMatchSettleScoreDto();
                BeanUtils.copyProperties(matchSettleScoreDto, dto);
                if (!StringUtil.isNullOrEmpty(matchSettleScore.getSettleNum())) {
                    dto.setSettleNum(matchSettleScore.getSettleNum());
                }
                dto.setSportId(matchSettleScore.getSportId());
                matchSettleOperateLogService.matchSettleCheckScoreAddLog(checkInfo, matchSettleCheckInfo, dto, OperateLogTypeEnum.CONFIRM_SCORE, matchSettleScore.getSettleNum(), checkNumber);

                //3.更新状态
                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
                matchSettleCheckInfo.setModifyTime(System.currentTimeMillis());
                matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                //进入统一核对比分流程
                MatchSettleTemplate countDownTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.COUNT_DOWEN.code);
                MatchSettleCheckInfo checkInfo1 = matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(matchSettleCheckInfo);
                Map<Long, Pair<Boolean, Boolean>> isScoreOrEventDiffMap = matchSettleBatchCheckService.batchCheckCommonMatchSettleScoreEvent(Arrays.asList(Pair.of(matchSettleScore,checkInfo1)),false,matchSettleScoreDto.getLinkedId(),countDownTemplate);
                if (!isScoreOrEventDiffMap.containsKey(matchSettleScore.getId())) {
                    return Response.failed("接口调用-主流程赛事id没找到");
                }
                if (isScoreOrEventDiffMap.get(matchSettleScore.getId()).getLeft()) {
                    //1031947=比分不一致需要下个审核员审核
                    return Response.success("1031947");
                }
            } else {
                return Response.failed("1031933");
            }
        } catch (Exception e) {
            log.error("BasketballNew-confirmMatchSettleScore:", e);
            return Response.failed();
        } finally {
            redisService.unLock(key, key);
        }
        return Response.success();
    }

    @Override
    public Response matchReplayAndFreezeV2(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new matchReplayAndFreeze-v2,settleQueryDTO: {}",settleQueryDTO);
        Response response = null;
        try {
            if (settleQueryDTO.getLevel().equals(1)) {
                switch (settleQueryDTO.getExInfo()) {
                    case 0:
                    case 1://赛事级的冻结、解冻
                        response = basketBallMatchAndPlayFreeze(settleQueryDTO);
                        break;
                    case 2: //赛事级的重跑
                        response = basketBallMatchReSettle(settleQueryDTO);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("BasketballNew-matchReplayAndFreeze:", e);
            return Response.failed();
        }
        return response;
    }

    @Override
    public Response basketBallPlayReSettleV2(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new basketBallPlayReSettle-v2,settleQueryDTO: {}",settleQueryDTO);
        try {
            List<String> settleNumList = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
            if (settleNumList.isEmpty()) {
                return Response.failed();
            }
            MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
            matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andSportIdEqualTo(2L).andSettleNumIn(settleNumList);
            List<MatchSettleScore> matchSettleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList,settleQueryDTO.getMatchId(),null);
            if (!matchSettleScoreList.isEmpty()) {
                for (MatchSettleScore matchSettleScore : matchSettleScoreList) {
                    matchSettleScore.setModifyTime(System.currentTimeMillis());
                    matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
                    matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
                }
                matchSettleScoreRepository.saveOrUpdateBatch(matchSettleScoreList);
            }

            //3.查询事件
            List<MatchSettleEventEntity> eventList = matchSettleEventRepository.getByStandardMatchIdAndSportIdAndSettleNum(settleQueryDTO.getMatchId(),2,settleNumList);
            if (!eventList.isEmpty()) {
                //4.批量更新事件
                for (MatchSettleEventEntity event : eventList) {
                    event.setModifyTime(System.currentTimeMillis());
                    event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    event.setOperater(settleQueryDTO.getOperatorName());
                    event.setUserid(settleQueryDTO.getOperatorId());
                }
                matchSettleEventRepository.saveOrUpdateBatch(eventList);
            }

            //5.结算比分下发
            MatchSettleScoreMessage matchSettleScore = new MatchSettleScoreMessage();
            matchSettleScore.setOperateType(3);
            matchSettleScore.setSportId(settleQueryDTO.getSportId());
            matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
            matchSettleScore.setLevel(settleQueryDTO.getLevelNum());
            if (settleQueryDTO.getLevelNum() == 3) {
                matchSettleScore.setSettleNum(settleNumList.get(0));
            }
            if (settleQueryDTO.getPlayCategoryNum() != null) {
                matchSettleScore.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
            }
            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);

            //6.结算事件下发
            MatchSettleEventMessage matchSettleEvent = new MatchSettleEventMessage();
            matchSettleEvent.setOperateType(3);
            matchSettleEvent.setLevel(settleQueryDTO.getLevelNum());
            matchSettleEvent.setSportId(settleQueryDTO.getSportId());
            matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
            if (settleQueryDTO.getLevelNum() == 3) {
                matchSettleScore.setSettleNum(settleNumList.get(0));
            }
            if (settleQueryDTO.getPlayCategoryNum() != null) {
                matchSettleEvent.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
            }
            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);

            //7.日志
            matchSettleOperateLogService.categoryReSettleAddLog(settleQueryDTO, "-");
        } catch (Exception e) {
            log.error("BasketballNew-basketBallPlayReSettle:", e);
            return Response.failed();
        }
        return Response.success();
    }

    @Override
    public Response playReplayAndFreezeV2(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new playReplayAndFreeze-v2,settleQueryDTO: {}",settleQueryDTO);
        //玩法级重跑、冻结
        Response response = null;
        try {
            if (settleQueryDTO.getLevel().equals(2)) {
                switch (settleQueryDTO.getExInfo()) {
                    case 0:
                    case 1://玩法级的冻结、解冻
                        response = basketBallMatchAndPlayFreeze(settleQueryDTO);
                        break;
                    case 2: //玩法级的重跑
                        response = basketBallPlayReSettle(settleQueryDTO);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("BasketballNew-playReplayAndFreeze:", e);
            return Response.failed();
        }
        return response;
    }

    @Override
    public Response basketBallMatchAndPlayFreezeV2(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new basketBallMatchAndPlayFreeze-v2,settleQueryDTO: {}",settleQueryDTO);
        //查询比分
        try {
            List<String> settleNumList = new LinkedList<>();
            List<MatchSettleScore> settleScoreList = new ArrayList<>();
            MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {
                settleNumList = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
                if (settleNumList.isEmpty()) {
                    return Response.failed();
                }
                settleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList,settleQueryDTO.getMatchId(),null);
            } else {
                settleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(null,settleQueryDTO.getMatchId(),null);
            }

            if (!settleScoreList.isEmpty()) {
                List<MatchSettleScoreEntity> listSettleScore = new ArrayList<>();
                for (MatchSettleScore dto : settleScoreList) {
                    MatchSettleScoreEntity matchSettleScore = new MatchSettleScoreEntity();
                    BeanUtils.copyProperties(dto, matchSettleScore);
                    matchSettleScore.setModifyTime(System.currentTimeMillis());
                    matchSettleScore.setId(dto.getId());
                    matchSettleScore.setSettleFreeze(settleQueryDTO.getExInfo());
                    listSettleScore.add(matchSettleScore);
                }
                //更新比分解冻、冻结
                matchSettleScoreRepository.saveOrUpdateBatch(listSettleScore);
            }

            //查询事件
            List<MatchSettleEventEntity> settleEventList = new ArrayList<>();
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1) && !settleNumList.isEmpty()) {
                settleEventList = matchSettleEventRepository.getByStandardMatchIdAndSportIdAndSettleNum(settleQueryDTO.getMatchId(),null,settleNumList);
            } else {
                settleEventList = matchSettleEventRepository.getByStandardMatchIdAndSportIdAndSettleNum(settleQueryDTO.getMatchId(),null,null);
            }
            if (!settleEventList.isEmpty()) {
                List<MatchSettleEventEntity> listEvent = new ArrayList<>();
                for (MatchSettleEventEntity dto : settleEventList) {
                    MatchSettleEventEntity matchSettleEvent = new MatchSettleEventEntity();
                    BeanUtils.copyProperties(dto, matchSettleEvent);
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setId(dto.getId());
                    matchSettleEvent.setSettleFreeze(settleQueryDTO.getExInfo());
                    listEvent.add(matchSettleEvent);
                }
                //更新事件解冻、冻结
                matchSettleEventRepository.saveOrUpdateBatch(listEvent);
            }

            String freezeText = null;
            MatchFreezeMessage matchFreezeMessage = new MatchFreezeMessage();
            matchFreezeMessage.setMatchId(settleQueryDTO.getMatchId());
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {
                freezeText = "玩法级冻结/解冻";
                matchFreezeMessage.setLevel(settleQueryDTO.getLevelNum());
                if (settleQueryDTO.getLevelNum() == 3) {
                    matchFreezeMessage.setSettleNum(settleNumList.get(0));
                }
                if (settleQueryDTO.getPlayCategoryNum() != null) {
                    matchFreezeMessage.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
                }
            } else {
                freezeText = "赛事级冻结/解冻";
                matchFreezeMessage.setLevel(settleQueryDTO.getLevel());
            }
            matchFreezeMessage.setSportId(settleQueryDTO.getSportId());
            matchFreezeMessage.setFreezeSettleStatus(settleQueryDTO.getExInfo());
            matchSettleCenterProducer.MatchFreeze(matchFreezeMessage, freezeText);

            //7.日志
            String forwText = "-";
            if (settleQueryDTO.getExInfo() == 0) {
                forwText = OperateLogTypeEnum.type_1.getCode().toString();
            }
            if (settleQueryDTO.getExInfo() == 1) {
                forwText = OperateLogTypeEnum.type_2.getCode().toString();
            }
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {

                // 玩法级的冻结、解冻
                MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleQueryDTO.getMatchId());
                if (matchSettleInfo != null && !StringUtils.isAnyEmpty(matchSettleInfo.getCategoryFreezeStatus())) {

                    CategoryBasketballDto categoryBasketballDto = JSONObject.parseObject(matchSettleInfo.getCategoryFreezeStatus(), CategoryBasketballDto.class);
                    switch (settleQueryDTO.getSettleNum()) {
                        case "100":
                            categoryBasketballDto.setBkQ104(settleQueryDTO.getExInfo());
                            break;
                        case "200":
                            categoryBasketballDto.setBkQ204(settleQueryDTO.getExInfo());
                            break;
                        case "300":
                            categoryBasketballDto.setBkQ304(settleQueryDTO.getExInfo());
                            break;
                        case "400":
                            categoryBasketballDto.setBkQ404(settleQueryDTO.getExInfo());
                            break;
                        case "end":
                            categoryBasketballDto.setEnd(settleQueryDTO.getExInfo());
                            break;
                        case "s001":
                            categoryBasketballDto.setFirstNPoint(settleQueryDTO.getExInfo());
                            break;
                        case "1ht":
                            categoryBasketballDto.setBk1ht(settleQueryDTO.getExInfo());
                            break;
                        case "2ht":
                            categoryBasketballDto.setBk2ht(settleQueryDTO.getExInfo());
                            break;
                        case "et":
                            categoryBasketballDto.setBkEt(settleQueryDTO.getExInfo());
                            break;
                        case "rg":
                            categoryBasketballDto.setBkFtRg(settleQueryDTO.getExInfo());
                            break;
                        case "point":
                            categoryBasketballDto.setPoint(settleQueryDTO.getExInfo());
                            break;
                        case "3pt":
                            categoryBasketballDto.setBk3pt(settleQueryDTO.getExInfo());
                            break;
                        case "ast":
                            categoryBasketballDto.setBkAst(settleQueryDTO.getExInfo());
                            break;
                        case "rbd":
                            categoryBasketballDto.setBkRbd(settleQueryDTO.getExInfo());
                            break;
                    }
                    matchSettleInfo.setCategoryFreezeStatus(JSON.toJSONString(categoryBasketballDto));
                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                }
                matchSettleOperateLogService.categoryReSettleAddLog(settleQueryDTO, forwText);
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleQueryDTO.getMatchId());
                matchSettleCenterProducer.doSendLogToRiskByTypeBasketball(standardMatchInfo,settleQueryDTO,forwText);

            } else {
                //赛事级别的冻结、解冻
                MatchFreezeDto matchFreezeDto = new MatchFreezeDto();
                matchFreezeDto.setMatchId(settleQueryDTO.getMatchId());
                matchFreezeDto.setIpAddress(settleQueryDTO.getIpAddress());
                matchFreezeDto.setOperatorName(settleQueryDTO.getOperatorName());
                matchFreezeDto.setSportId(2l);

                MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(settleQueryDTO.getMatchId());
                matchSettleInfo.setSportId(2L);
                matchSettleInfo.setId(settleQueryDTO.getMatchId());
                matchSettleInfo.setFreezeStatus(settleQueryDTO.getExInfo());
                matchSettleInfo.setStandardMatchId(settleQueryDTO.getMatchId());
                String categoryFreezeStatus = null;
                if (settleQueryDTO.getExInfo().equals(0)) {
                    categoryFreezeStatus = JSON.toJSONString(new CategoryBasketballDto().unFreeze());
                } else {
                    categoryFreezeStatus = JSON.toJSONString(new CategoryBasketballDto().builderFreeze());
                }
                matchSettleInfo.setCategoryFreezeStatus(categoryFreezeStatus);
                matchSettleInfo.setModifyTime(System.currentTimeMillis());
                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchFreezeDto.getMatchId());
                matchSettleOperateLogService.matchFreezeAddLog(standardMatchInfo,matchSettleInfo, forwText, matchFreezeDto);

                MatchFreezeMessage freezeMessage = new MatchFreezeMessage();
                BeanUtils.copyProperties(matchFreezeDto, freezeMessage);
                freezeMessage.setLevel(1);
                matchFreezeDto.setFreezeSettleStatus(settleQueryDTO.getExInfo());
                matchSettleCenterProducer.doSendLogToRisk(standardMatchInfo,matchSettleInfo, freezeMessage, matchFreezeDto);
            }
        } catch (Exception e) {
            log.error("BasketballNew-basketBallMatchAndPlayFreeze:", e);
            return Response.failed();
        }
        return Response.success();
    }

    @Override
    public Response basketBallMatchReSettleV2(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new basketBallMatchReSettle,settleQueryDTO: {}",settleQueryDTO);
        try {
            MatchSettleScoreExample example = new MatchSettleScoreExample();
            example.createCriteria().andStandardMatchIdEqualTo(settleQueryDTO.getMatchId()).andStatusEqualTo(MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM);
            example.setOrderByClause("settle_num desc");
            List<MatchSettleScore> settleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(null,settleQueryDTO.getMatchId(),Arrays.asList(MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM));
            if (!settleScoreList.isEmpty()) {
                //1.查询比分
                for (MatchSettleScore matchSettleScore : settleScoreList) {
                    matchSettleScore.setModifyTime(System.currentTimeMillis());
                    matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes());
                    matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
                    matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
                }
                //2.更新比分
                matchSettleScoreRepository.saveOrUpdateBatch(settleScoreList);
            }

            //3.查询事件
            List<MatchSettleEventEntity> eventList = matchSettleEventRepository.getByStandardMatchIdAndStatus(settleQueryDTO.getMatchId(),MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM);
            if (!eventList.isEmpty()) {
                //4.批量更新事件
                for (MatchSettleEventEntity event : eventList) {
                    event.setModifyTime(System.currentTimeMillis());
                    event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    event.setOperater(settleQueryDTO.getOperatorName());
                    event.setUserid(settleQueryDTO.getOperatorId());
                }
                matchSettleEventRepository.saveOrUpdateBatch(eventList);
            }

            //5.结算比分下发
            MatchSettleScoreMessage matchSettleScore = new MatchSettleScoreMessage();
            matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
            matchSettleScore.setSportId(settleQueryDTO.getSportId());
            matchSettleScore.setLevel(settleQueryDTO.getLevel());
            matchSettleScore.setSettleNum("0");
            matchSettleScore.setOperateType(3);
            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);

            //6.结算事件下发
            MatchSettleEventMessage matchSettleEvent = new MatchSettleEventMessage();
            matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
            matchSettleEvent.setSportId(settleQueryDTO.getSportId());
            matchSettleEvent.setLevel(settleQueryDTO.getLevel());
            matchSettleEvent.setSettleNum("0");
            matchSettleEvent.setOperateType(3);
            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);

            //7.日志
            matchSettleOperateLogService.matchReSettleAddLog(settleQueryDTO);
        } catch (Exception e) {
            log.error("BasketballNew-basketBallMatchReSettle:", e);
            return Response.failed();
        }
        return Response.success();
    }

    @Override
    public Response confirmBringInScoreV2(BasketBallPutInJsonDto basketBallPutInJsonDto) {
        String key ="StandardMatchScoreConsumer:"+basketBallPutInJsonDto.getStandardMatchId();

        if (redisService.tryLock(key, key, 2, 5)) {
            log.info("审核员confirmBringInScore入参: {}",basketBallPutInJsonDto);
            Long standardMatchId = basketBallPutInJsonDto.getStandardMatchId();
            //解析需要带入的比分
            List<UpdateBasketBallSettleScoreDto> updateBasketBallSettleScores =new ArrayList<>();
            try {
                JSONArray array = JSONArray.parseArray(basketBallPutInJsonDto.getPutInJson());
                for (Object o : array) {
                    UpdateBasketBallSettleScoreDto updateBasketBallSettleScoreDto = JSONObject.toJavaObject((JSONObject)o,UpdateBasketBallSettleScoreDto.class);
                    updateBasketBallSettleScores.add(updateBasketBallSettleScoreDto);
                }
            }catch (Exception e){
                log.error("confirmBringInScore:解析参数异常",e);
                return Response.failed("解析参数异常");
            }

            //判断带入的比分是否已经结算,假如已经结算则不带入
            MatchSettleTemplate countDownTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(standardMatchId, SettleTemplateTypeEnum.COUNT_DOWEN.code);
            List<String> settleNums = updateBasketBallSettleScores.stream().map(UpdateBasketBallSettleScoreDto::getSettleNum).collect(Collectors.toList());
            List<MatchSettleScore> matchSettleScores = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndNotStatus(settleNums,standardMatchId,3);
            if (!CollectionUtils.isEmpty(matchSettleScores)){
                matchSettleScores.forEach(matchSettleScore -> {
                    updateBasketBallSettleScores.forEach(updateBasketBallSettleScore ->{
                        if (matchSettleScore.getSettleNum().equals(updateBasketBallSettleScore.getSettleNum())){
                            //赛事比分id
                            Long matchScoreId = updateBasketBallSettleScore.getMatchScoreId();
                            //审核员姓名
                            String userName = basketBallPutInJsonDto.getOperatorName();
                            //2.查询人工核对比分
                            MatchSettleCheckInfoEntity matchSettleCheckInfo = matchSettleCheckInfoHelper.searchCheckInfoByUser(matchScoreId, standardMatchId, userName);
                            MatchSettleCheckInfo checkInfo = new MatchSettleCheckInfo();
                            Integer checkNumber = matchSettleScore.getCheckNumber();
                            UpdateMatchSettleScoreDto dto = new UpdateMatchSettleScoreDto();
                            updateBasketBallSettleScore.setOperatorName(userName);
                            BeanUtils.copyProperties(updateBasketBallSettleScore, dto);
                            if (matchSettleCheckInfo == null) {
                                //得到当前用户的次序
                                matchSettleCheckInfo = new MatchSettleCheckInfoEntity();
                                BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);

                                matchSettleCheckInfo = SettleCheckUtils.initManualMatchSettleScoresv2(matchSettleScore);
                                SettleCheckUtils.copyManualMatchSettleScoreV2(updateBasketBallSettleScore, matchSettleCheckInfo);
                                matchSettleCheckInfo.setCheckNumber(SettleCheckUtils.getCheckNumber(checkNumber));
                                matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                                matchSettleCheckInfo.setDataSourceCode("PA");
                                matchSettleCheckInfo.setGoWaterStatus(0);
                                matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
                                matchSettleCheckInfoRepository.save(matchSettleCheckInfo);


                                if (!StringUtil.isNullOrEmpty(matchSettleScore.getSettleNum())) {
                                    dto.setSettleNum(matchSettleScore.getSettleNum());
                                }
                                dto.setSportId(matchSettleScore.getSportId());
                                MatchSettleCheckInfoEntity old = new MatchSettleCheckInfoEntity();
                                BeanUtils.copyProperties(checkInfo,old);
                                MatchSettleCheckInfoEntity entity = new MatchSettleCheckInfoEntity();
                                BeanUtils.copyProperties(matchSettleCheckInfo,entity);
                                matchSettleOperateLogService.matchSettleCheckScoreAddLog(old, entity, dto, OperateLogTypeEnum.EDIT, matchSettleScore.getSettleNum(), checkNumber);
                            }
                            //3.2有比分核对记录则更新
                            //3.更新状态
                            matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
                            SettleCheckUtils.copyManualMatchSettleScoreV2(updateBasketBallSettleScore, matchSettleCheckInfo);
                            matchSettleCheckInfo.setUserName(userName);
                            matchSettleCheckInfo.setGoWaterStatus(0);
                            matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);

                            MatchSettleCheckInfoEntity old2 = new MatchSettleCheckInfoEntity();
                            BeanUtils.copyProperties(checkInfo,old2);
                            MatchSettleCheckInfoEntity entity2 = new MatchSettleCheckInfoEntity();
                            BeanUtils.copyProperties(matchSettleCheckInfo,entity2);
                            if (matchSettleScore.getSettleNum().equals("bk_1ht") || matchSettleScore.getSettleNum().equals("bk_ft_rg")) {
                                UpdateBasketBallSettleScoreDto updateDto = new UpdateBasketBallSettleScoreDto();
                                updateDto.setSettleNum(matchSettleScore.getSettleNum());
                                updateDto.setT1(matchSettleScore.getT1());
                                updateDto.setT2(matchSettleScore.getT2());
                                dto.setStandardMatchId(matchSettleScore.getStandardMatchId());
                                log.info("进入比分带入列表比分dtoV2: "+dto);
                                recordScore(updateDto);
                            }

                            matchSettleOperateLogService.matchSettleCheckScoreAddLog(old2, entity2, dto, OperateLogTypeEnum.CONFIRM_SCORE, matchSettleScore.getSettleNum(), checkNumber);
                            //进入统一核对比分流程
                            MatchSettleCheckInfo checkInfoConvert = matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(matchSettleCheckInfo);
                            matchSettleBatchCheckService.batchCheckCommonMatchSettleScoreEvent(
                                    Arrays.asList(Pair.of(matchSettleScore, checkInfoConvert)),false,basketBallPutInJsonDto.getLinkedId(), countDownTemplate);
                        }

                    });
                });
            }
            return Response.success();
        } else {
            log.error("confirmBringInScore standardMatchId::{}::比分无法获取redis锁",basketBallPutInJsonDto.getStandardMatchId());
        }
        return Response.failed("比分带入时，比分无法获取redis锁,请重试!");
    }

    @Override
    public void verifyScoresIsSame(MatchSettleScore matchSettleScore) {
        matchSettleScoreHelper.verifyScoresIsSame(matchSettleScore.getStandardMatchId());
    }

    @Override
    public void verifyScoresIsSame(Long standardMatchId) {
        matchSettleScoreHelper.verifyScoresIsSame(standardMatchId);
    }


    private void recordScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto){
        MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchSettleScoreDto.getStandardMatchId());
        if (matchSettleInfo!=null) {

            matchSettleInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            if (matchSettleScoreDto.getSettleNum().equals("bk_1ht")) {
                matchSettleInfo.setH1T1(matchSettleScoreDto.getT1());
                matchSettleInfo.setH1T2(matchSettleScoreDto.getT2());
            }else if(matchSettleScoreDto.getSettleNum().equals("bk_ft_rg")){
                matchSettleInfo.setFtT1(matchSettleScoreDto.getT1());
                matchSettleInfo.setFtT2(matchSettleScoreDto.getT2());
            }
            //更新结算信息
            matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
        }else {
            log.error("参数异常【matchSettleInfos为空! 】");
        }

    }

    @Override
    public Response editMatchSettleScoreV2(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        log.info("basketball new editMatchSettleScore,matchSettleScoreDto-v2: {}",matchSettleScoreDto);
        //赛事id
        Long standardMatchId = matchSettleScoreDto.getStandardMatchId();
        //赛事比分id
        Long matchScoreId = matchSettleScoreDto.getMatchScoreId();
        //审核员姓名
        String userName = matchSettleScoreDto.getOperatorName();

        //1.判断是否已超过结算时间
        if (matchServiceHelper.checkIfOverSettleTime(standardMatchId)) {
            return Response.failed("1031930");
        }
        if(redisService.hasKey("SETTLE_ROLLBACK_MATCH_ID_"+matchSettleScoreDto.getMatchScoreId())){
            return Response.failed("1031960");
        }
        String key = "NEW_MATCH_SETTLE_INFO:" + matchScoreId + "_" + userName;
        try {
            if (redisService.tryLock(key, key, 2, 5)) {
                MatchSettleCheckInfoEntity matchSettleCheckInfo = matchSettleCheckInfoHelper.searchCheckInfoByUser(matchSettleScoreDto.getMatchScoreId(), matchSettleScoreDto.getStandardMatchId(), matchSettleScoreDto.getOperatorName());

                if (matchSettleCheckInfo != null && matchSettleCheckInfo.getCheckStatus() == MatchSettleCheckConstant.CheckStatus.CONFIRM) {
                    //还没编辑 该用户已经确认比分
                    return Response.failed("1031933");
                }

                MatchSettleScore matchSettleScore = matchSettleScoreRepository.getById(matchSettleScoreDto.getMatchScoreId());
                Integer checkNumber = matchSettleScore.getCheckNumber();
                if (matchSettleScore == null) {
                    return Response.failed("1031935");
                }
                //删除事件不能编辑
                if (null!=matchSettleScore.getHasDeleteEvent()&&matchSettleScore.getHasDeleteEvent()==1){
                    return Response.failed("1031961");
                }
                //篮球结算顺序拦截
                if(matchSettleScore.getSportId().equals(2L)){
                    if(!matchSettleCheckInfoHelper.checkBasketPeriodScoreOrder(matchSettleScore)){
                        return Response.failed("10138");
                    }
                }
                matchSettleScore.setOperater(matchSettleScoreDto.getOperatorName());
                matchSettleScore.setT2(matchSettleScoreDto.getT2());
                matchSettleScore.setT1(matchSettleScoreDto.getT1());
                matchSettleScore.setGoWaterStatus(matchSettleScoreDto.getGoWaterStatus());
                MatchSettleCheckInfoEntity checkInfo = new MatchSettleCheckInfoEntity();

                //3.1无比分核对记录则初始化该用户的核对记录
                if (matchSettleCheckInfo == null) {
                    //得到当前用户的次序
                    matchSettleCheckInfo = new MatchSettleCheckInfoEntity();
                    BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);
                    matchSettleCheckInfo = matchSettleCheckInfoHelper.initManualMatchSettleScores(matchSettleScore);
                    matchSettleCheckInfoHelper.copyManualMatchSettleScore(matchSettleScoreDto, matchSettleCheckInfo);
                    matchSettleCheckInfo.setCheckNumber(SettleCheckUtils.getCheckNumber(matchSettleScore.getCheckNumber()));
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfo.setDataSourceCode("PA");
                    matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
                    matchSettleCheckInfoRepository.save(matchSettleCheckInfo);
                    log.info("::{}:: 插入比分id为{}，审核员{}比分核对数据:{}", standardMatchId, matchScoreId, userName, matchSettleScoreDto);
                } else {
                    //3.2有比分核对记录则更新
                    BeanUtils.copyProperties(matchSettleCheckInfo, checkInfo);
                    matchSettleCheckInfoHelper.copyManualMatchSettleScore(matchSettleScoreDto, matchSettleCheckInfo);
                    matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.EDIT);
                    matchSettleCheckInfo.setExtryInfo(matchSettleScore.getExtryInfo());
                    matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
                    log.info("::{}:: 更新比分id为{}，审核员{}比分核对数据:{}", standardMatchId, matchScoreId, userName, matchSettleScoreDto);
                }

                String settleScoreJson = JSON.toJSONString(matchSettleScoreDto);
                UpdateMatchSettleScoreDto updateMatchSettleScoreDto = JSON.parseObject(settleScoreJson,UpdateMatchSettleScoreDto.class);
                updateMatchSettleScoreDto.setBasketBallSettleNum(matchSettleScoreDto.getSettleNum());
                matchSettleOperateLogService.matchSettleCheckScoreAddLog(checkInfo,matchSettleCheckInfo,
                        updateMatchSettleScoreDto,OperateLogTypeEnum.EDIT,matchSettleScore.getSettleNum(), checkNumber);
            } else {
                return Response.failed("1031933");
            }
        } catch (Exception e) {
            log.error("BasketballNew-editMatchSettleScore", e);
            return Response.failed();
        } finally {
            redisService.unLock(key, key);
        }
        return Response.success();
    }
    public Response basketBallMatchAndPlayFreeze(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new basketBallMatchAndPlayFreeze-v2,settleQueryDTO: {}",settleQueryDTO);
        //查询比分
        try {
            List<String> settleNumList = new LinkedList<>();
            List<MatchSettleScore> settleScoreList = new ArrayList<>();
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {
                settleNumList = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
                if (settleNumList.isEmpty()) {
                    return Response.failed();
                }
                settleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndNotStatus(settleNumList,settleQueryDTO.getMatchId(),null);
            } else {
                settleScoreList = matchSettleScoreRepository.getModelStandardMatchIdAndNotStatusAndIsGrey(settleQueryDTO.getMatchId(),null,null);
            }

            if (!settleScoreList.isEmpty()) {
                List<MatchSettleScore> listSettleScore = new ArrayList<>();
                for (MatchSettleScore dto : settleScoreList) {
                    MatchSettleScore matchSettleScore = new MatchSettleScore();
                    BeanUtils.copyProperties(dto, matchSettleScore);
                    matchSettleScore.setModifyTime(System.currentTimeMillis());
                    matchSettleScore.setId(dto.getId());
                    matchSettleScore.setSettleFreeze(settleQueryDTO.getExInfo());
                    listSettleScore.add(matchSettleScore);
                }
                //更新比分解冻、冻结
                matchSettleScoreRepository.saveOrUpdateBatch(listSettleScore);
            }

            //查询事件
            List<MatchSettleEvent> settleEventList = new ArrayList<>();
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1) && !settleNumList.isEmpty()) {
                settleEventList = matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(settleQueryDTO.getMatchId(),settleNumList);
            } else {
                settleEventList = matchSettleEventRepository.getModelByStandardMatchIdAndSettleNums(settleQueryDTO.getMatchId(),null);
            }
            if (!settleEventList.isEmpty()) {
                List<MatchSettleEvent> listEvent = new ArrayList<>();
                for (MatchSettleEvent dto : settleEventList) {
                    MatchSettleEvent matchSettleEvent = new MatchSettleEvent();
                    BeanUtils.copyProperties(dto, matchSettleEvent);
                    matchSettleEvent.setModifyTime(System.currentTimeMillis());
                    matchSettleEvent.setId(dto.getId());
                    matchSettleEvent.setSettleFreeze(settleQueryDTO.getExInfo());
                    listEvent.add(matchSettleEvent);
                }
                //更新事件解冻、冻结
                matchSettleEventRepository.saveOrUpdateBatch(listEvent);
            }

            String freezeText = null;
            MatchFreezeMessage matchFreezeMessage = new MatchFreezeMessage();
            matchFreezeMessage.setMatchId(settleQueryDTO.getMatchId());
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {
                freezeText = "玩法级冻结/解冻";
                matchFreezeMessage.setLevel(settleQueryDTO.getLevelNum());
                if (settleQueryDTO.getLevelNum() == 3) {
                    matchFreezeMessage.setSettleNum(settleNumList.get(0));
                }
                if (settleQueryDTO.getPlayCategoryNum() != null) {
                    matchFreezeMessage.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
                }
            } else {
                freezeText = "赛事级冻结/解冻";
                matchFreezeMessage.setLevel(settleQueryDTO.getLevel());
            }
            matchFreezeMessage.setSportId(settleQueryDTO.getSportId());
            matchFreezeMessage.setFreezeSettleStatus(settleQueryDTO.getExInfo());
            matchFreezeMessage.setOperatorId(settleQueryDTO.getOperatorId());
            matchFreezeMessage.setOperatorName(settleQueryDTO.getOperatorName());
            matchSettleCenterProducer.MatchFreeze(matchFreezeMessage, freezeText);

            //7.日志
            String forwText = "-";
            if (settleQueryDTO.getExInfo() == 0) {
                forwText = OperateLogTypeEnum.type_1.getCode().toString();
            }
            if (settleQueryDTO.getExInfo() == 1) {
                forwText = OperateLogTypeEnum.type_2.getCode().toString();
            }
            if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1)) {

                // 玩法级的冻结、解冻
                MatchSettleInfoEntity matchSettleInfo = matchSettleInfoRepository.getMatchSettleInfo(settleQueryDTO.getMatchId());
                if (matchSettleInfo != null && !StringUtils.isAnyEmpty(matchSettleInfo.getCategoryFreezeStatus())) {

                    CategoryBasketballDto categoryBasketballDto = JSONObject.parseObject(matchSettleInfo.getCategoryFreezeStatus(), CategoryBasketballDto.class);
                    switch (settleQueryDTO.getSettleNum()) {
                        case "100":
                            categoryBasketballDto.setBkQ104(settleQueryDTO.getExInfo());
                            break;
                        case "200":
                            categoryBasketballDto.setBkQ204(settleQueryDTO.getExInfo());
                            break;
                        case "300":
                            categoryBasketballDto.setBkQ304(settleQueryDTO.getExInfo());
                            break;
                        case "400":
                            categoryBasketballDto.setBkQ404(settleQueryDTO.getExInfo());
                            break;
                        case "end":
                            categoryBasketballDto.setEnd(settleQueryDTO.getExInfo());
                            break;
                        case "s001":
                            categoryBasketballDto.setFirstNPoint(settleQueryDTO.getExInfo());
                            break;
                        case "1ht":
                            categoryBasketballDto.setBk1ht(settleQueryDTO.getExInfo());
                            break;
                        case "2ht":
                            categoryBasketballDto.setBk2ht(settleQueryDTO.getExInfo());
                            break;
                        case "et":
                            categoryBasketballDto.setBkEt(settleQueryDTO.getExInfo());
                            break;
                        case "rg":
                            categoryBasketballDto.setBkFtRg(settleQueryDTO.getExInfo());
                            break;
                        case "point":
                            categoryBasketballDto.setPoint(settleQueryDTO.getExInfo());
                            break;
                        case "3pt":
                            categoryBasketballDto.setBk3pt(settleQueryDTO.getExInfo());
                            break;
                        case "ast":
                            categoryBasketballDto.setBkAst(settleQueryDTO.getExInfo());
                            break;
                        case "rbd":
                            categoryBasketballDto.setBkRbd(settleQueryDTO.getExInfo());
                            break;
                    }
                    matchSettleInfo.setCategoryFreezeStatus(JSON.toJSONString(categoryBasketballDto));
                    matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                }
                matchSettleOperateLogService.categoryReSettleAddLog(settleQueryDTO, forwText);
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(settleQueryDTO.getMatchId());
                matchSettleCenterProducer.doSendLogToRiskByTypeBasketball(standardMatchInfo,settleQueryDTO,forwText);

            } else {
                //赛事级别的冻结、解冻
                MatchFreezeDto matchFreezeDto = new MatchFreezeDto();
                matchFreezeDto.setMatchId(settleQueryDTO.getMatchId());
                matchFreezeDto.setIpAddress(settleQueryDTO.getIpAddress());
                matchFreezeDto.setOperatorName(settleQueryDTO.getOperatorName());
                matchFreezeDto.setSportId(2l);

                MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(settleQueryDTO.getMatchId());
                matchSettleInfo.setSportId(2L);
                matchSettleInfo.setId(settleQueryDTO.getMatchId());
                matchSettleInfo.setFreezeStatus(settleQueryDTO.getExInfo());
                matchSettleInfo.setStandardMatchId(settleQueryDTO.getMatchId());
                String categoryFreezeStatus = null;
                if (settleQueryDTO.getExInfo().equals(0)) {
                    categoryFreezeStatus = JSON.toJSONString(new CategoryBasketballDto().unFreeze());
                } else {
                    categoryFreezeStatus = JSON.toJSONString(new CategoryBasketballDto().builderFreeze());
                }
                matchSettleInfo.setCategoryFreezeStatus(categoryFreezeStatus);
                matchSettleInfo.setModifyTime(System.currentTimeMillis());
                matchSettleInfoRepository.updateMatchSettleInfoToRedis(matchSettleInfo,false);
                StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchFreezeDto.getMatchId());
                matchSettleOperateLogService.matchFreezeAddLog(standardMatchInfo,matchSettleInfo, forwText, matchFreezeDto);

                MatchFreezeMessage freezeMessage = new MatchFreezeMessage();
                BeanUtils.copyProperties(matchFreezeDto, freezeMessage);
                freezeMessage.setLevel(1);
                matchFreezeDto.setFreezeSettleStatus(settleQueryDTO.getExInfo());
                matchSettleCenterProducer.doSendLogToRisk(standardMatchInfo,matchSettleInfo, freezeMessage, matchFreezeDto);
            }
        } catch (Exception e) {
            log.error("BasketballNew-basketBallMatchAndPlayFreeze:", e);
            return Response.failed();
        }
        return Response.success();
    }
    private Response basketBallMatchReSettle(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new basketBallMatchReSettle,settleQueryDTO: {}",settleQueryDTO);
        try {
            List<Integer> status = new ArrayList<>();
            status.add(MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM);
            List<MatchSettleScore> settleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(null,settleQueryDTO.getMatchId(),status);
            if (!settleScoreList.isEmpty()) {
                //1.查询比分
                for (MatchSettleScore matchSettleScore : settleScoreList) {
                    matchSettleScore.setModifyTime(System.currentTimeMillis());
                    matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    matchSettleScore.setSettleTimes(matchSettleScore.getSettleTimes());
                    matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
                    matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
                }
                //2.更新比分
                matchSettleScoreRepository.saveOrUpdateBatch(settleScoreList);
            }

            //3.查询事件
            List<MatchSettleEventEntity> eventList = matchSettleEventRepository.getByStandardMatchIdAndStatus(settleQueryDTO.getMatchId(),MatchSettleScoreConstant.MatchSettleScoreStatus.CONFIRM);
            if (!eventList.isEmpty()) {
                //4.批量更新事件
                for (MatchSettleEventEntity event : eventList) {
                    event.setModifyTime(System.currentTimeMillis());
                    event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    event.setOperater(settleQueryDTO.getOperatorName());
                    event.setUserid(settleQueryDTO.getOperatorId());
                }
                matchSettleEventRepository.saveOrUpdateBatch(eventList);
            }

            //5.结算比分下发
            MatchSettleScoreMessage matchSettleScore = new MatchSettleScoreMessage();
            matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
            matchSettleScore.setSportId(settleQueryDTO.getSportId());
            matchSettleScore.setLevel(settleQueryDTO.getLevel());
            matchSettleScore.setSettleNum("0");
            matchSettleScore.setOperateType(3);
            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);

            //6.结算事件下发
            MatchSettleEventMessage matchSettleEvent = new MatchSettleEventMessage();
            matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
            matchSettleEvent.setSportId(settleQueryDTO.getSportId());
            matchSettleEvent.setLevel(settleQueryDTO.getLevel());
            matchSettleEvent.setSettleNum("0");
            matchSettleEvent.setOperateType(3);
            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);

            //7.日志
            matchSettleOperateLogService.matchReSettleAddLog(settleQueryDTO);
        } catch (Exception e) {
            log.error("BasketballNew-basketBallMatchReSettle:", e);
            return Response.failed();
        }
        return Response.success();

    }

    private Response basketBallPlayReSettle(SettleQueryDTO settleQueryDTO) {
        log.info("basketball new basketBallPlayReSettle-v2,settleQueryDTO: {}",settleQueryDTO);
        try {
            List<String> settleNumList = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
            if (settleNumList.isEmpty()) {
                return Response.failed();
            }
            List<MatchSettleScore> matchSettleScoreList = matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(settleNumList,settleQueryDTO.getMatchId(),null);
            if (!matchSettleScoreList.isEmpty()) {
                for (MatchSettleScore matchSettleScore : matchSettleScoreList) {
                    matchSettleScore.setModifyTime(System.currentTimeMillis());
                    matchSettleScore.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    matchSettleScore.setOperater(settleQueryDTO.getOperatorName());
                    matchSettleScore.setUserid(settleQueryDTO.getOperatorId());
                }
                matchSettleScoreRepository.saveOrUpdateBatch(matchSettleScoreList);
            }

            //3.查询事件
            List<MatchSettleEventEntity> eventList = matchSettleEventRepository.getByStandardMatchIdAndSportIdAndSettleNum(settleQueryDTO.getMatchId(),2,settleNumList);
            if (!eventList.isEmpty()) {
                //4.批量更新事件
                for (MatchSettleEventEntity event : eventList) {
                    event.setModifyTime(System.currentTimeMillis());
                    event.setOperateType(MatchSettleScoreConstant.MatchSettleOperateType.RE_SETTLE);
                    event.setOperater(settleQueryDTO.getOperatorName());
                    event.setUserid(settleQueryDTO.getOperatorId());
                }
                matchSettleEventRepository.saveOrUpdateBatch(eventList);
            }

            //5.结算比分下发
            MatchSettleScoreMessage matchSettleScore = new MatchSettleScoreMessage();
            matchSettleScore.setOperateType(3);
            matchSettleScore.setSportId(settleQueryDTO.getSportId());
            matchSettleScore.setStandardMatchId(settleQueryDTO.getMatchId());
            matchSettleScore.setLevel(settleQueryDTO.getLevelNum());
            if (settleQueryDTO.getLevelNum() == 3) {
                matchSettleScore.setSettleNum(settleNumList.get(0));
            }
            if (settleQueryDTO.getPlayCategoryNum() != null) {
                matchSettleScore.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
            }
            matchSettleScoresProducer.sendMatchSettleScores(matchSettleScore);

            //6.结算事件下发
            MatchSettleEventMessage matchSettleEvent = new MatchSettleEventMessage();
            matchSettleEvent.setOperateType(3);
            matchSettleEvent.setLevel(settleQueryDTO.getLevelNum());
            matchSettleEvent.setSportId(settleQueryDTO.getSportId());
            matchSettleEvent.setStandardMatchId(settleQueryDTO.getMatchId());
            if (settleQueryDTO.getLevelNum() == 3) {
                matchSettleScore.setSettleNum(settleNumList.get(0));
            }
            if (settleQueryDTO.getPlayCategoryNum() != null) {
                matchSettleEvent.setPlayCategory(settleQueryDTO.getPlayCategoryNum());
            }
            matchSettleScoresProducer.sendMatchSettleEvent(matchSettleEvent);

            //7.日志
            matchSettleOperateLogService.categoryReSettleAddLog(settleQueryDTO, "-");
        } catch (Exception e) {
            log.error("BasketballNew-basketBallPlayReSettle:", e);
            return Response.failed();
        }
        return Response.success();
    }
}
