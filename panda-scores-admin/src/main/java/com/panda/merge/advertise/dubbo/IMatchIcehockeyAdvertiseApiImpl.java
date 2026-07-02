package com.panda.merge.advertise.dubbo;

import com.alibaba.fastjson.JSON;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.service.IceHockeyAdvertiseService;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.api.IMatchBasketBallAdvertiseApi;
import com.panda.merge.api.IMatchIcehockeyAdvertiseApi;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchPeriodDto;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchStartTimeDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.EditFaScoreDto;
import com.panda.merge.dto.advertise.MatchAdvertiseQueryDto;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.IMatchScorePdLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.panda.merge.advertise.common.Constant.MATCH_CONTINUE;
import static com.panda.merge.advertise.common.Constant.MATCH_END;
import static com.panda.merge.advertise.common.Constant.MATCH_PAUSE;
import static com.panda.merge.advertise.common.Constant.MATCH_START;


@Service
@Slf4j
@DubboService
public class IMatchIcehockeyAdvertiseApiImpl implements IMatchIcehockeyAdvertiseApi {

    @Autowired
    RedisService redisService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    CommonEventService commonEventService;

    @Autowired
    CommonAdvertiseService commonAdvertiseService;

    @Autowired
    private IceHockeyAdvertiseService iceHockeyAdvertiseService;

    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    IMatchScorePdLogService matchScorePdLogService;
    @Autowired
    IMatchBasketBallAdvertiseApi matchBasketBallAdvertiseApi;

    @Override
    public Response changeMatchStartTime(ChangeMatchStartTimeDto changeMatchStartTimeDto) {
        return matchBasketBallAdvertiseApi.changeMatchStartTime(changeMatchStartTimeDto);
//        String key="PA_createMatchAdvertise:"+changeMatchStartTimeDto.getThirdMatchId();
//        try{
//
//            if(redisService.tryLock(key,key,2,3)) {
//                // 1.查询三方赛事-时间-比分
//                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStartTimeDto.getThirdMatchId());
//                if (!response.isSuccess()) {
//                    return response;
//                }
//                ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
//                Long oldBeginTime = thirdMatchInfo.getBeginTime();
//                MatchTimeInfo timeInfo = response.getData().getMatchTimeInfo();
//                // 2.如果已经开赛则无法修改
//                if (timeInfo.getPeriod() > 0) {
//                    return Response.failed("PA赛事已经开赛无法修改开赛时间");
//                }
//                // 3.修改开赛时间
//                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(thirdMatchInfo.getReferenceId());
//                if (standardMatchInfo == null) {
//                    return Response.failed("PA赛事的标准赛事不存在");
//                }
//                thirdMatchInfo.setBeginTime(changeMatchStartTimeDto.getStartTime());
//                standardMatchInfo.setBeginTime(changeMatchStartTimeDto.getStartTime());
//                standardMatchInfo.setBeginTimeStatus(1); //1 为人工更新 0为 系统更新
//                standardMatchInfo.setModifyTime(System.currentTimeMillis());
//                standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
//                thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
//
//                // 4.修改开赛时间要同时修改开售表
//                StandardSportMarketSellExample standardSportMarketSellExample= new StandardSportMarketSellExample();
//                standardSportMarketSellExample.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
//                List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample( standardSportMarketSellExample);
//                if (standardSportMarketSellList.size()!=0) {
//                    standardSportMarketSellList.forEach(it->{
//                        it.setBeginTime(changeMatchStartTimeDto.getStartTime());
//                        it.setModifyTime( Calendar.getInstance().getTimeInMillis());
//                        standardSportMarketSellMapper.updateByPrimaryKey(it);
//                    });
//                }
//                matchScorePdLogService.changeMatchStartTimeLog(changeMatchStartTimeDto,oldBeginTime,standardMatchInfo);
//                return Response.success();
//            } else {
//                throw new RuntimeException("redis锁失败:");
//            }
//        } catch (Exception e){
//            log.error("::{}::", e);
//            return Response.failed(e.getMessage());
//        } finally {
//            redisService.unLock(key,key);
//        }
    }

    @Override
    public Response changeMatchTime(ChangeMatchTimeDto changeMatchTimeDto) {
        String key="PA_createMatchAdvertise:"+changeMatchTimeDto.getThirdMatchId();
        try{
            if(redisService.tryLock(key,key,2,3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchTimeDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }

                ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
                MatchScoresInfo matchScoresInfo = response.getData().getMatchScoresInfo();
                MatchTimeInfo timeInfo = response.getData().getMatchTimeInfo();
                Long secondFromStart = timeInfo.getSecondFromStart();
                //1.先进行阶段判断非滚球开赛阶段无法下发时间修正
                if (timeInfo.getPeriod()<=0) {
                    return Response.failed("赛事未开始，无法修改进行时间。");
                }

                // 2.针对时间做计算，得到修改后的时间
                timeInfo.setSecondFromStart(changeMatchTimeDto.getMatchTime());
                timeInfo.setEventTime(System.currentTimeMillis());
                timeInfo.setModifyTime(System.currentTimeMillis());
                matchScoresInfo.setEventTime(System.currentTimeMillis());
                matchScoresInfo.setSecondsMatchStart(changeMatchTimeDto.getMatchTime());
                matchScoresInfo.setModifyTime(System.currentTimeMillis());

                // 3.调用公共时间接口做时间下发
                commonEventService.updateMatchTimeEvent(response.getData(), timeInfo.getPeriod(), timeInfo.getSecondFromStart(), timeInfo.getSecondFromStart(), System.currentTimeMillis(), timeInfo.getTimeGo(), changeMatchTimeDto.getLinkedId());
                if(thirdMatchInfo.getSportId().equals(1l)){
                    redisUtils.pushFootBallScore(thirdMatchInfo.getId());
                }
                matchScorePdLogService.changeMatchTimeLog(response.getData(),secondFromStart,changeMatchTimeDto);
                return Response.success();
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e){
            log.error("::changeMatchTime异常::",e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response changeMatchStatus(ChangeMatchStatusDto changeMatchStatus) {
        String linkId = changeMatchStatus.getLinkedId();
        String key = "PA_createMatchAdvertise:" + changeMatchStatus.getThirdMatchId();
        try{
            if (redisUtils.checkRequestLinkId(linkId)) {
                return Response.failed("该linkId已被消费");
            }
            if(redisService.tryLock(key,key,2,3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStatus.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                if (changeMatchStatus.getControlType() == null || !Arrays.asList(1, 2, 3, 4).contains(changeMatchStatus.getControlType()) ) {
                    return Response.failed("操作不符合定义");
                }
                String changeKey = "PA_changeMatchStatus:" + changeMatchStatus.getThirdMatchId();
                if(!redisService.tryLock(changeKey,changeKey,1,2)){
                    return Response.failed("后台正在响应,请稍后再试。");
                }
                redisService.expire(changeKey,1);
                matchScorePdLogService.changeMatchStatusLog(response.getData(),changeMatchStatus);
                // 1.开始
                if (changeMatchStatus.getControlType().equals(MATCH_START)) {
                    if(Arrays.asList(1L, 2L, 3L, 40L).contains(response.getData().getMatchTimeInfo().getPeriod())){
                        return Response.failed("操作不符合定义");
                    }
                    Response r= iceHockeyAdvertiseService.matchStart(response.getData(), linkId);
                    if(r.isSuccess()){
                        return commonAdvertiseService.changeMatchStartStatus(response.getData().getThirdMatchInfo(),linkId);
                    }
                }
                // 2.暂停
                if (changeMatchStatus.getControlType().equals(MATCH_PAUSE)) {
                    return iceHockeyAdvertiseService.matchPause(response.getData(), linkId);
                }
                // 3.继续
                if (changeMatchStatus.getControlType().equals(MATCH_CONTINUE)) {
                    return iceHockeyAdvertiseService.matchContinue(response.getData(), linkId);
                }
                // 4.结束
                if (changeMatchStatus.getControlType().equals(MATCH_END)) {
                    return iceHockeyAdvertiseService.matchEnd(response.getData(), linkId);
                }
                return Response.failed();
            } else {
                throw new RuntimeException("后台正在响应,请稍后再试:");
            }
        } catch (Exception e){
            log.error("::{}::IMatchIcehockeyAdvertiseApiImpl切换赛事事件状态异常", linkId, e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response changeMatchPeriod(ChangeMatchPeriodDto changeMatchPeriodDto) {
        String linkId = changeMatchPeriodDto.getLinkedId();
        String key = "PA_createMatchAdvertise:" + changeMatchPeriodDto.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(changeMatchPeriodDto.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            if( redisService.tryLock(key,key,2,3) ) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchPeriodDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                Long periodId = response.getData().getStandardMatchInfo().getMatchPeriodId();
                Response changeMatchPeriodResponse = iceHockeyAdvertiseService.changeMatchPeriod(response.getData(), changeMatchPeriodDto.getPeriodId(), linkId,changeMatchPeriodDto.getOperatorName());
                matchScorePdLogService.changeMatchPeriodLog(response.getData(),periodId,changeMatchPeriodDto);
                redisUtils.cacheRequestLinkId(changeMatchPeriodDto.getLinkedId());
                return changeMatchPeriodResponse;
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e){
            log.error("::{}::冰球报球板变更比赛阶段异常", linkId, e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response setMatchEnd(ChangeMatchStatusDto changeMatchStatus) {
        String key = "PA_createMatchAdvertise:"+changeMatchStatus.getThirdMatchId();
        try {
            if( redisService.tryLock(key,key,2,3) ) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStatus.getThirdMatchId());
                if ( !response.isSuccess() ) {
                    return response;
                }
                Response iceHockeyResponse = iceHockeyAdvertiseService.match999End(response.getData(), changeMatchStatus.getLinkedId());
                matchScorePdLogService.setMatchEndLog(response.getData(),changeMatchStatus);
                return iceHockeyResponse;
            } else {
                throw new RuntimeException("redis锁失败:");
            }
        } catch (Exception e){
            log.error("::setMatchEnd异常", e );
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key,key);
        }
    }

    @Override
    public Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {
        try{
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(matchAdvertiseQueryDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            return iceHockeyAdvertiseService.buildIceHockeyAdvertiseVo(response.getData());
        }catch (Exception e){
            log.error("::getMatchAdvertiseInfo异常::",e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response editFaScore(EditFaScoreDto editFaScoreDto) {
        try{
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(editFaScoreDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            String scoresJson = response.getData().getMatchScoresInfo().getScoresJson();
            Response<MatchScoreAndTimeVo> responseData = iceHockeyAdvertiseService.editFaScore(response.getData(),editFaScoreDto);
            matchScorePdLogService.editFaScoreLog(response.getData(),scoresJson,editFaScoreDto);
            return responseData;
        }catch (Exception e){
            log.error("::editFaScore异常::",e);
            return Response.failed(e.getMessage());
        }
    }


    @Override
    public Response changeMatchScore(ChangeMatchScoreDto changeMatchScoreDto) {
        String linkId = changeMatchScoreDto.getLinkedId();
        String key = "PA_createMatchAdvertise:" + changeMatchScoreDto.getThirdMatchId();
        log.info("::{}::冰球merge-score变更比分入参:{}", linkId, JSON.toJSONString(changeMatchScoreDto));
        try{
            if( null== changeMatchScoreDto || changeMatchScoreDto.getPeriod() ==0 ){
                log.info("::{}::未开赛阶段变更比分无效", linkId);
                return Response.failed("未开赛阶段变更比分无效!");
            }
            if(redisService.tryLock(key,key,2,3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchScoreDto.getThirdMatchId());
                log.info("::{}::冰球merge-score变更比分,事件、比分校验:{}", linkId, JSON.toJSONString(response));
                if (!response.isSuccess()) {
                    return response;
                }
                Long periodNow = response.getData().getMatchTimeInfo().getPeriod();
                Map<String,String> oldScoreMap = new LinkedHashMap<>();
                Integer periodT1Old = response.getData().getMatchScoresInfo().getPeriodT1();
                Integer periodT2Old = response.getData().getMatchScoresInfo().getPeriodT2();
                oldScoreMap.put("periodT1Old",periodT1Old.toString());
                oldScoreMap.put("periodT2Old",periodT2Old.toString());
                oldScoreMap.put("scoresJson",response.getData().getMatchScoresInfo().getScoresJson());
                if (periodNow.equals(changeMatchScoreDto.getPeriod())) {
                    if (changeMatchScoreDto.getPeriodT1() == periodT1Old && changeMatchScoreDto.getPeriodT2() == periodT2Old) {
                        log.info("::{}::冰球merge-score变更比分,设置的比分和原来比分相等", linkId);
                        return Response.failed("设置的比分和原来比分相等");
                    }
                }
                // 修改当前阶段比分必须只能改一个球队比分?
                Response changeMatchScoreResponse = iceHockeyAdvertiseService.changeMatchScore(linkId, response.getData(), changeMatchScoreDto);
                matchScorePdLogService.changeMatchScoreLog(oldScoreMap,response.getData(),changeMatchScoreDto);
                return changeMatchScoreResponse;
            } else {
                throw new RuntimeException("redis锁失败!");
            }
        } catch (Exception e){
            log.error("::changeMatchScore异常::",e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key,key);
        }
    }

}
