package com.panda.merge.advertise.dubbo;

import com.alibaba.fastjson.JSON;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.event.TennisEventService;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.service.TennisAdvertiseService;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.api.IMatchTennisAdvertiseApi;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.mapper.*;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.IScoresService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 网球报球板1.0
 * */
@Service
@Slf4j
@DubboService
public class MatchTennisAdvertiseApiImpl implements IMatchTennisAdvertiseApi {


    @Autowired
    TennisAdvertiseService tennisAdvertiseService;
    @Autowired
    RedisService redisService;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    IScoresService scoresService;
    @Autowired
    CommonEventService commonEventService;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    MatchTimeInfoMapper timeInfoMapper;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    TennisEventService tennisEventService;
    /**
     * 赛事开始
     * */
    @Override
    public Response matchBegin(TennisAdvertiseDto tennisAdvertiseDto) {
        log.info("::{}::matchBegin的入参:{}", tennisAdvertiseDto.getLinkedId(), JSON.toJSONString(tennisAdvertiseDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(tennisAdvertiseDto.getThirdMatchId());
        try{
            //下发开赛
            return tennisAdvertiseService.matchBegin(tennisAdvertiseDto,response);
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
        }finally {

        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response matchEnd(TennisAdvertiseDto matchVo) {
        log.info("::{}::matchEnd的入参:{}", matchVo.getLinkedId(), JSON.toJSONString(matchVo));
        String key="PA_createMatchAdvertise:"+matchVo.getThirdMatchId();
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(matchVo.getThirdMatchId());
        try{
            //下发开赛
            return tennisAdvertiseService.matchEnd(matchVo,response);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response matchStatusReSet(TennisAdvertiseDto tennisAdvertiseDto) {
        log.info("::{}::matchStatusReSet的入参:{}", tennisAdvertiseDto.getLinkedId(), JSON.toJSONString(tennisAdvertiseDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(tennisAdvertiseDto.getThirdMatchId());
        try{
            //下发开赛
            return tennisAdvertiseService.matchStatusReSet(tennisAdvertiseDto,response);
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
        }
        return Response.failed("服务器错误");


    }


    @Override
    public Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {
        return tennisAdvertiseService.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
    }

    @Override
    public Response setMatchSecondScore(TennisEditSecondScoreDto tennisEditSecondScoreDto) {
        log.info("::{}::setMatchSecondScore的入参:{}", tennisEditSecondScoreDto.getLinkedId(), JSON.toJSONString(tennisEditSecondScoreDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(tennisEditSecondScoreDto.getThirdMatchId());
        try{
            if(response.getData().getMatchTimeInfo()!=null){
                Long period = response.getData().getMatchTimeInfo().getPeriod();
                List<Long> setEndPeriod = Arrays.asList(301L, 302L, 303L, 304L, 305L, 100L, 999L);
                if(setEndPeriod.contains(period)){
                    return Response.failed("当前盘结束状态不允许编辑局内比分");
                }
            }
            tennisEditSecondScoreDto.setLinkedId(response.getData().getThirdMatchInfo().getReferenceId()+"_PD");
            //重新设置linkId
            tennisEditSecondScoreDto.setLinkedId("PD_" + UUID.randomUUID());
            return tennisAdvertiseService.setMatchSecondScore(tennisEditSecondScoreDto,response);
        }catch (Exception e){
            
            log.error("setMatchSecondScore::",e);
        }
        return Response.success();
    }

    @Override
    public Response setMatchLength(PDMatchLengthEditDto pdMatchLengthEditDto) {
        log.info("::{}::setMatchLength的入参:{}", pdMatchLengthEditDto.getLinkedId(), JSON.toJSONString(pdMatchLengthEditDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdMatchLengthEditDto.getThirdMatchId());
        try{
            TennisAdvertiseDto matchDto =new TennisAdvertiseDto();
            BeanUtils.copyProperties(pdMatchLengthEditDto,matchDto);
            matchDto.setRoundType(pdMatchLengthEditDto.getCurrentSet());
            matchDto.setMatchLength(Integer.parseInt(pdMatchLengthEditDto.getMatchLength()+""));
            //下发开赛
            return tennisAdvertiseService.setMatchLength(matchDto,response);
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response setRoundType(PDRoundTypeEditDto pdRoundTypeEditDto) {
        log.info("::{}::setRoundType的入参:{}", pdRoundTypeEditDto.getLinkedId(), JSON.toJSONString(pdRoundTypeEditDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdRoundTypeEditDto.getThirdMatchId());
        try{
            //下发开赛
            return tennisAdvertiseService.setRoundType(pdRoundTypeEditDto,response);
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
        }
        return Response.failed("服务器错误");

    }

    @Override
    public Response setFirstNum(PDFirstNumSetDto pdFirstNumSetDto) {
        log.info("::{}::setFirstNum的入参:{}", pdFirstNumSetDto.getLinkedId(), JSON.toJSONString(pdFirstNumSetDto));
        //设置当前盘
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdFirstNumSetDto.getThirdMatchId());

        return tennisAdvertiseService.setFirstNum(pdFirstNumSetDto,response);

    }

    @Override
    public Response changeSetStatus(PDTennisSetStatusDto pdTennisSetStatusDto) {
        try {
            log.info("::{}::changeSetStatus的入参:{}", pdTennisSetStatusDto.getLinkedId(), JSON.toJSONString(pdTennisSetStatusDto));
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdTennisSetStatusDto.getThirdMatchId());
            return tennisAdvertiseService.changeSetStatus(pdTennisSetStatusDto, response);
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response changeRoundStatus(PDTennisRoundStatusDto pdTennisRoundStatusDto) {
        try {
            log.info("::{}::tennis-changeRoundStatus-->的入参:{}", pdTennisRoundStatusDto.getLinkedId(), JSON.toJSONString(pdTennisRoundStatusDto));
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(pdTennisRoundStatusDto.getThirdMatchId());
            return tennisAdvertiseService.changeRoundStatus(pdTennisRoundStatusDto,response);
        }catch (Exception e){
            
            log.error(":处理数据发生异常:", e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response searchOperatorDetail(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {
        return tennisAdvertiseService.searchOperatorDetail(matchAdvertiseQueryDto);

    }

    @Override
    public Response setMaxRound(MatchTennisEditMaxRoundDto dto) {
        log.info("::{}::setMaxRound的入参:{}", dto.getLinkedId(), JSON.toJSONString(dto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(dto.getThirdMatchId());
        return tennisAdvertiseService.setMaxRound(dto,response);
    }

    @Override
    public Response setSetScore(MatchTennisEditSetScoreDto matchAdvertiseQueryDto) {
        log.info("::{}::setSetScore的入参:{}", matchAdvertiseQueryDto.getLinkedId(), JSON.toJSONString(matchAdvertiseQueryDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(matchAdvertiseQueryDto.getThirdMatchId());
        return tennisAdvertiseService.setSetScore(matchAdvertiseQueryDto,response);
    }

    @Override
    public Response reCountSetScore(MatchTennisReSetScoreDto tennisReSetScoreDto) {
        String linkId = tennisReSetScoreDto.getLinkedId();
        log.info("::{}::reCountSetScore的入参:{}", linkId, JSON.toJSONString(tennisReSetScoreDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(tennisReSetScoreDto.getThirdMatchId());
        Response  setResponse =  tennisAdvertiseService.reCountSetScore(tennisReSetScoreDto, response);

        // 调用公共的类发送消息
//        tennisAdvertiseService.noteEventPush(linkId, response.getData().getThirdMatchInfo(),
//                response.getData().getMatchScoresInfo(), response.getData().getMatchTimeInfo(), "tennis_score_change");

        return setResponse;
    }

    @Override
    public Response eventList(EventListDto eventListDto) {
        log.info("::{}::eventList的入参:{}", eventListDto.getLinkedId(), JSON.toJSONString(eventListDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(eventListDto.getThirdMatchId());
        if (!response.isSuccess()) {
            return response;
        }
        return tennisEventService.eventList(response.getData(), eventListDto);
    }

    @Override
    public Response setMatchOpenBallPlayer(TennisEditSecondScoreDto tennisAdvertiseDto) {
        log.info("::{}::setMatchOpenBallPlayer的入参:{}", tennisAdvertiseDto.getLinkedId(), JSON.toJSONString(tennisAdvertiseDto));
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(tennisAdvertiseDto.getThirdMatchId());
        if (!response.isSuccess()) {
            return response;
        }
        return tennisAdvertiseService.setMatchOpenBall(response, tennisAdvertiseDto);
    }


}
