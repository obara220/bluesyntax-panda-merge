package com.panda.merge.advertise.dubbo.mycontroller;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IMatchBasketBallAdvertiseApi;
import com.panda.merge.api.IMatchFootballBallAdvertiseApi;
import com.panda.merge.api.IPDBasketBallAdvertiseApi;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchPeriodDto;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.GoFreeThrowDto;
import com.panda.merge.dto.advertise.MatchAdvertiseQueryDto;
import com.panda.merge.dto.advertise.PDBaskectBallMatchStartDto;
import com.panda.merge.dto.advertise.PDBasketBallDeleteEventDto;
import com.panda.merge.dto.advertise.PDBasketBallEditEventDto;
import com.panda.merge.dto.advertise.PDBasketBallEditSixScoreDto;
import com.panda.merge.dto.advertise.PDBasketBallParseContinueDto;
import com.panda.merge.dto.advertise.PDBasketBallPauseDto;
import com.panda.merge.dto.advertise.PDBasketBallSearchEventDto;
import com.panda.merge.dto.advertise.PDBasketBallSendBallDto;
import com.panda.merge.dto.advertise.PDBasketBallSendEventDto;
import com.panda.merge.dto.advertise.SendFreeThrowDto;
import com.panda.merge.dto.advertise.SetFreeThrowDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author warren
 * @since 2024/03/22 12:54:43
 */
@RestController
@RequestMapping("/pdMatch/basketball")
@Slf4j
public class PDBasketballController {

    @Autowired
    private IPDBasketBallAdvertiseApi ipdBasketBallAdvertiseApi;
    @Autowired

    private IMatchBasketBallAdvertiseApi matchBasketBallAdvertiseApi;
    @Autowired

    private IMatchFootballBallAdvertiseApi footballBallAdvertiseApi;

    @PostMapping("/getMatchAdvertiseInfo")
    @ApiOperation(value = "PA赛事报球版查询", notes = "PA赛事报球版查询")
    public Response getMatchAdvertiseInfo(@RequestBody MatchAdvertiseQueryDto matchAdvertiseQueryDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
        matchAdvertiseQueryDto.setRequestId(requestId);
        log.info("PDBasketBallController::getMatchAdvertiseInfo::request-id={},赛事信息查询入参={}", requestId, JSON.toJSONString(matchAdvertiseQueryDto));
        return  ipdBasketBallAdvertiseApi.searchDetail(matchAdvertiseQueryDto);
    }

    @PostMapping("/getEventList")
    @ApiOperation(value = "PD报球版篮球事件消息查询", notes = "PD报球版篮球事件消息查询")
    public Response getMatchEventMessage(@RequestBody PDBasketBallSearchEventDto matchAdvertiseQueryDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
        matchAdvertiseQueryDto.setRequestId(requestId);
        log.info("PDBasketBallController::getMatchEventMessage::request-id={},赛事消息入参={}", requestId, JSON.toJSONString(matchAdvertiseQueryDto));
        return  ipdBasketBallAdvertiseApi.searchEventList(matchAdvertiseQueryDto);
    }

    /**
     *
     * 确认事件
     * */
    @PostMapping("/sendEvent")
    @ApiOperation(value = "修改比分", notes = "修改比分")
    public Response sendEvent(@RequestBody PDBasketBallSendEventDto sendEventDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        sendEventDto.setOperatorId("418");
        sendEventDto.setOperatorName("cptest");
        String linkId = "PD_" + UUID.randomUUID();
        sendEventDto.setLinkedId(linkId);
        sendEventDto.setRequestId(requestId);
        sendEventDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::sendEvent::request-id={},确认事件入参={}", requestId, JSON.toJSONString(sendEventDto));
        Response r  =  ipdBasketBallAdvertiseApi.sendEvent(sendEventDto);
        if(r.isSuccess()){
            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
            BeanUtils.copyProperties(sendEventDto,dto);
            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
        }else {
            return r;
        }

    }

    /**
     * 下发球未命中  命中  取消(不发)
     *  //  1 未命中  2投篮命中  3取消投篮
     * */
    @PostMapping("/sendBall")
    @ApiOperation(value = "修改比分", notes = "修改比分")
    public Response sendBall(@RequestBody PDBasketBallSendBallDto sendEventDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        sendEventDto.setOperatorId("418");
        sendEventDto.setOperatorName("cptest");
        String linkId = "PD_" + UUID.randomUUID();
        sendEventDto.setLinkedId(linkId);
        sendEventDto.setRequestId(requestId);
        sendEventDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::sendBall::request-id={},下发球入参={}", requestId, JSON.toJSONString(sendEventDto));
        Response r  =  ipdBasketBallAdvertiseApi.sendBall(sendEventDto);
        if(r.isSuccess()){
            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
            BeanUtils.copyProperties(sendEventDto,dto);
            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
        }else {
            return r;
        }
    }

    /**
     * 比赛开始
     *  // 可能有跳球开始
     * */
    @PostMapping("/gameStart")
    @ApiOperation(value = "修改比分", notes = "修改比分")
    public Response gameStart(@RequestBody PDBaskectBallMatchStartDto sendEventDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        sendEventDto.setOperatorId("418");
        sendEventDto.setOperatorName("cptest");
        sendEventDto.setIpAddress("127.0.0.1");
        String linkId = "PD_" + UUID.randomUUID();
        sendEventDto.setLinkedId(linkId);
        sendEventDto.setRequestId(requestId);
        log.info("PDBasketBallController::gameStart::request-id={},比赛开始入参={}", requestId, JSON.toJSONString(sendEventDto));
        Response r  =  ipdBasketBallAdvertiseApi.gameStart(sendEventDto);
        if(r.isSuccess()){
            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
            BeanUtils.copyProperties(sendEventDto,dto);
            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
        }else {
            return r;
        }
    }


    /**
     *  删除事件
     * */
    @PostMapping("/deleteEvent")
    @ApiOperation(value = " 删除事件", notes = " 删除事件")
    public Response deleteEvent(@RequestBody PDBasketBallDeleteEventDto deleteEventDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        deleteEventDto.setOperatorId("418");
        deleteEventDto.setOperatorName("cptest");
        String linkId = "PD_" + UUID.randomUUID();
        deleteEventDto.setLinkedId(linkId);
        deleteEventDto.setRequestId(requestId);
        deleteEventDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::deleteEvent::request-id={},删除事件入参={}", requestId, JSON.toJSONString(deleteEventDto));
        Response r  =  ipdBasketBallAdvertiseApi.deleteEvent(deleteEventDto);
        if(r.isSuccess()){
            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
            BeanUtils.copyProperties(deleteEventDto,dto);
            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
        }else {
            return r;
        }
    }

    /**
     *  编辑事件
     * */
    @PostMapping("/editEvent")
    @ApiOperation(value = " 删除事件", notes = " 删除事件")
    public Response editEvent(@RequestBody PDBasketBallEditEventDto deleteEventDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        deleteEventDto.setOperatorId("418");
        deleteEventDto.setOperatorName("cptest");
        String linkId = "PD_" + UUID.randomUUID();
        deleteEventDto.setLinkedId(linkId);
        deleteEventDto.setRequestId(requestId);
        deleteEventDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::editEvent::request-id={},编辑事件入参={}", requestId, JSON.toJSONString(deleteEventDto));
        Response r  =  ipdBasketBallAdvertiseApi.editEvent(deleteEventDto);
        if(r.isSuccess()){
            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
            BeanUtils.copyProperties(deleteEventDto,dto);
            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
        }else {
            return r;
        }
    }

    /**
     *   暂停
     * */
    @PostMapping("/pauseOrContinue")
    @ApiOperation(value = "  暂停", notes = "  暂停")
    public Response pauseAndContinue(@RequestBody PDBasketBallPauseDto deleteEventDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        deleteEventDto.setOperatorId("418");
        deleteEventDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        deleteEventDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID();
        deleteEventDto.setLinkedId(linkId);
        deleteEventDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::pauseAndContinue::request-id={},暂停事件入参={}", requestId, JSON.toJSONString(deleteEventDto));
        Response r  =  ipdBasketBallAdvertiseApi.pauseAndContinue(deleteEventDto);
        if(r.isSuccess()){
            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
            BeanUtils.copyProperties(deleteEventDto,dto);
            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
        }else {
            return r;
        }
    }

    /**
     *   当前罚球查询
     * */
    @PostMapping("/getFreeThrow")
    @ApiOperation(value = "  当前罚球查询", notes = "  当前罚球查询")
    public Response getFreeThrow(@RequestBody PDBasketBallPauseDto deleteEventDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
        deleteEventDto.setRequestId(requestId);
        log.info("PDBasketBallController::getFreeThrow::request-id={},获取罚球入参={}", requestId, JSON.toJSONString(deleteEventDto));
        return ipdBasketBallAdvertiseApi.getFreeThrow(deleteEventDto);
    }


    /**
     *   罚球设置
     * */
    @PostMapping("/setFreeThrow")
    @ApiOperation(value = "罚球设置", notes = "罚球设置")
    public Response setFreeThrow(@RequestBody SetFreeThrowDto setFreeThrowDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        setFreeThrowDto.setOperatorId("418");
        setFreeThrowDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        setFreeThrowDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID().toString().replaceAll("-","");
        setFreeThrowDto.setLinkedId(linkId);
        setFreeThrowDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::setFreeThrow::request-id={},设置罚球入参={}", requestId, JSON.toJSONString(setFreeThrowDto));
        Response r  =  ipdBasketBallAdvertiseApi.setFreeThrow(setFreeThrowDto);
        if(r.isSuccess()){
            PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
            BeanUtils.copyProperties(setFreeThrowDto,dto);
            return  ipdBasketBallAdvertiseApi.getFreeThrow(dto);
        }else {
            return r;
        }
    }

    /**
     *   罚球进行
     * */
    @PostMapping("/goFreeThrow")
    @ApiOperation(value = "  罚球进行", notes = "  罚球进行")
    public Response goFreeThrow(@RequestBody GoFreeThrowDto setFreeThrowDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        setFreeThrowDto.setOperatorId("418");
        setFreeThrowDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        setFreeThrowDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID();
        setFreeThrowDto.setLinkedId(linkId);
        setFreeThrowDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::goFreeThrow::request-id={},罚球事件入参={}", requestId, JSON.toJSONString(setFreeThrowDto));
        Response r  =  ipdBasketBallAdvertiseApi.goFreeThrow(setFreeThrowDto);
        if(r.isSuccess()){
            PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
            BeanUtils.copyProperties(setFreeThrowDto,dto);
            return  ipdBasketBallAdvertiseApi.getFreeThrow(dto);
        }else {
            return r;
        }
    }

    /**
     *   罚球发送
     * */
    @PostMapping("/sendFreeThrow")
    @ApiOperation(value = "罚球发送", notes = "  罚球发送")
    public Response sendFreeThrow(@RequestBody SendFreeThrowDto setFreeThrowDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        setFreeThrowDto.setOperatorId("418");
        setFreeThrowDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        setFreeThrowDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID();
        setFreeThrowDto.setLinkedId(linkId);
        setFreeThrowDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::sendFreeThrow::request-id={},罚球发送事件入参={}", requestId, JSON.toJSONString(setFreeThrowDto));
        Response r  =  ipdBasketBallAdvertiseApi.sendFreeThrow(setFreeThrowDto);
        if(r.isSuccess()){
            PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
            BeanUtils.copyProperties(setFreeThrowDto, dto);
            return  ipdBasketBallAdvertiseApi.getFreeThrow(dto);
        }else {
            return r;
        }
    }

    /**
     *   罚球取消
     * */
    @PostMapping("/cancelFreeThrow")
    @ApiOperation(value = "罚球取消", notes = "  罚球取消")
    public Response cancelFreeThrow(@RequestBody SetFreeThrowDto setFreeThrowDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        setFreeThrowDto.setOperatorId("418");
        setFreeThrowDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        setFreeThrowDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID();
        setFreeThrowDto.setLinkedId(linkId);
        setFreeThrowDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::cancelFreeThrow::request-id={},罚球取消事件入参={}", requestId, JSON.toJSONString(setFreeThrowDto));
        Response r  =  ipdBasketBallAdvertiseApi.cancelFreeThrow(setFreeThrowDto);
        if(r.isSuccess()){
            PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
            BeanUtils.copyProperties(setFreeThrowDto, dto);
            return  ipdBasketBallAdvertiseApi.getFreeThrow(dto);
        }else {
            return r;
        }
    }


    /**
     *   比赛中断/重开
     * */
    @PostMapping("/breakOrReStart")
    @ApiOperation(value = "  比赛中断/重开", notes = "  比赛中断/重开")
    public Response breakOrReStart(@RequestBody PDBasketBallParseContinueDto setFreeThrowDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        setFreeThrowDto.setOperatorId("418");
        setFreeThrowDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        setFreeThrowDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID();
        setFreeThrowDto.setLinkedId(linkId);
        setFreeThrowDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::breakOrReStart::request-id={},比赛中断入参={}", requestId, JSON.toJSONString(setFreeThrowDto));
        return ipdBasketBallAdvertiseApi.breakOrReStart(setFreeThrowDto);

    }

    /**
     *   统计比分查询
     * */
    @PostMapping("/searchAllScore")
    @ApiOperation(value = "  统计比分查询", notes = " 统计比分查询")
    public Response searchAllScore(@RequestBody PDBasketBallParseContinueDto setFreeThrowDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        setFreeThrowDto.setOperatorId("418");
        setFreeThrowDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        setFreeThrowDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID();
        setFreeThrowDto.setLinkedId(linkId);
        setFreeThrowDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::searchAllScore::request-id={},统计比分入参={}", requestId, JSON.toJSONString(setFreeThrowDto));
        return ipdBasketBallAdvertiseApi.searchAllScore(setFreeThrowDto);

    }

    /**
     *   六分钟编辑
     * */
    @PostMapping("/editSixScore")
    @ApiOperation(value = "  六分钟编辑", notes = " 六分钟编辑")
    public Response editSixScore(@RequestBody PDBasketBallEditSixScoreDto editSixScoreDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        editSixScoreDto.setOperatorId("418");
        editSixScoreDto.setOperatorName("cptest");
        String requestId = request.getHeader("request-id");
        editSixScoreDto.setRequestId(requestId);
        String linkId = "PD_" + UUID.randomUUID();
        editSixScoreDto.setLinkedId(linkId);
        editSixScoreDto.setIpAddress("127.0.0.1");
        log.info("PDBasketBallController::editSixScore::request-id={},六分钟编辑入参={}", requestId, JSON.toJSONString(editSixScoreDto));
        return ipdBasketBallAdvertiseApi.editSixScore(editSixScoreDto);

    }





//    @PostMapping("/sendBall")
//    @ApiOperation(value = "修改比分", notes = "修改比分")
//    public Response sendBall(@RequestBody PDBasketBallSendBallDto sendEventDto, HttpServletRequest request) {
//        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(sendEventDto));
////        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
//        sendEventDto.setOperatorId("418");
//        sendEventDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        sendEventDto.setLinkedId(id);
////        sendEventDto.setIpAddress(IpUtils.getIpAddress(request));
//        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
//        BeanUtils.copyProperties(sendEventDto,matchAdvertiseQueryDto);
//        Response r= ipdBasketBallAdvertiseApi.sendBall(sendEventDto);
//        if(r.isSuccess()){
//            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
//            dto.setThirdMatchId(sendEventDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
//        }else {
//            return r;
//        }
//    }
//
//    @PostMapping("/sendEvent")
//    @ApiOperation(value = "修改比分", notes = "修改比分")
//    public Response sendEvent(@RequestBody PDBasketBallSendEventDto sendEventDto, HttpServletRequest request) {
//        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(sendEventDto));
////        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
//        sendEventDto.setOperatorId("418");
//        sendEventDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        sendEventDto.setLinkedId(id);
////        sendEventDto.setIpAddress(IpUtils.getIpAddress(request));
//        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
//        BeanUtils.copyProperties(sendEventDto,matchAdvertiseQueryDto);
//        Response r  =  ipdBasketBallAdvertiseApi.sendEvent(sendEventDto);
//        if(r.isSuccess()){
//            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
//            dto.setThirdMatchId(sendEventDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
//        }else {
//            return r;
//        }
//
//    }
//
//    @PostMapping("/gameStart")
//    @ApiOperation(value = "修改比分", notes = "修改比分")
//    public Response gameStart(@RequestBody PDBaskectBallMatchStartDto sendEventDto, HttpServletRequest request) {
//
////        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
////        sendEventDto.setOperatorId(user.getUserId());
////        sendEventDto.setOperatorName(user.getUserName());
//        sendEventDto.setOperatorId("418");
//        sendEventDto.setOperatorName("cptest");
//        sendEventDto.setIpAddress("127.0.0.1");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        sendEventDto.setLinkedId(id);
//        Response r  =  ipdBasketBallAdvertiseApi.gameStart(sendEventDto);
//        if(r.isSuccess()){
//            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
//            dto.setThirdMatchId(sendEventDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
//        }else {
//            return r;
//        }
//    }
//
//    @PostMapping("/breakOrReStart")
//    @ApiOperation(value = "  比赛中断/重开", notes = "  比赛中断/重开")
//    public Response breakOrReStart(@RequestBody PDBasketBallParseContinueDto setFreeThrowDto, HttpServletRequest request) {
////        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
////        setFreeThrowDto.setOperatorId(user.getUserId());
////        setFreeThrowDto.setOperatorName(user.getUserName());
//        setFreeThrowDto.setOperatorId("418");
//        setFreeThrowDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        setFreeThrowDto.setLinkedId(id);
////        setFreeThrowDto.setIpAddress(IpUtils.getIpAddress(request));
//        return ipdBasketBallAdvertiseApi.breakOrReStart(setFreeThrowDto);
//
//    }
//
//    @PostMapping("/pauseOrContinue")
//    @ApiOperation(value = "  暂停", notes = "  暂停")
//    public Response pauseAndContinue(@RequestBody PDBasketBallPauseDto deleteEventDto, HttpServletRequest request) {
//        deleteEventDto.setOperatorId("418");
//        deleteEventDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        deleteEventDto.setLinkedId(id);
//
//        Response r  =  ipdBasketBallAdvertiseApi.pauseAndContinue(deleteEventDto);
//        if(r.isSuccess()){
//            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
//            dto.setThirdMatchId(deleteEventDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
//        }else {
//            return r;
//        }
//    }
//
//    @PostMapping("/editSixScore")
//    @ApiOperation(value = "  六分钟编辑", notes = " 六分钟编辑")
//    public Response editSixScore(@RequestBody PDBasketBallEditSixScoreDto editSixScoreDto, HttpServletRequest request) {
////        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
////        editSixScoreDto.setOperatorId(user.getUserId());
////        editSixScoreDto.setOperatorName(user.getUserName());
//        editSixScoreDto.setOperatorId("418");
//        editSixScoreDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        editSixScoreDto.setLinkedId(id);
////        editSixScoreDto.setIpAddress(IpUtils.getIpAddress(request));
//        return ipdBasketBallAdvertiseApi.editSixScore(editSixScoreDto);
//
//    }
//
//
//    @PostMapping("/getEventList")
//    @ApiOperation(value = "PD报球版篮球事件消息查询", notes = "PD报球版篮球事件消息查询")
//    public Response getMatchEventMessage(@RequestBody PDBasketBallSearchEventDto matchAdvertiseQueryDto, HttpServletRequest request) {
//        log.info("PD报球版篮球事件消息查询,比赛id:{}", JSON.toJSONString(matchAdvertiseQueryDto));
//
//        return  ipdBasketBallAdvertiseApi.searchEventList(matchAdvertiseQueryDto);
//    }
//
//    @PostMapping("/setFreeThrow")
//    @ApiOperation(value = "罚球设置", notes = "罚球设置")
//    public Response setFreeThrow(@RequestBody SetFreeThrowDto setFreeThrowDto, HttpServletRequest request) {
//        setFreeThrowDto.setOperatorId("418");
//        setFreeThrowDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        setFreeThrowDto.setLinkedId(id);
//
//        Response r  =  ipdBasketBallAdvertiseApi.setFreeThrow(setFreeThrowDto);
//        if(r.isSuccess()){
//            PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
//            dto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.getFreeThrow(dto);
//        }else {
//            return r;
//        }
//    }
//
//    @PostMapping("/getFreeThrow")
//    @ApiOperation(value = "  当前罚球查询", notes = "  当前罚球查询")
//    public Response getFreeThrow(@RequestBody PDBasketBallPauseDto deleteEventDto, HttpServletRequest request) {
//        PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
//        dto.setThirdMatchId(deleteEventDto.getThirdMatchId());
//        return ipdBasketBallAdvertiseApi.getFreeThrow(dto);
//    }
//
//    @PostMapping("/goFreeThrow")
//    @ApiOperation(value = "  罚球进行", notes = "  罚球进行")
//    public Response goFreeThrow(@RequestBody GoFreeThrowDto setFreeThrowDto, HttpServletRequest request) {
//        setFreeThrowDto.setOperatorId("418");
//        setFreeThrowDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        setFreeThrowDto.setLinkedId(id);
//        Response r  =  ipdBasketBallAdvertiseApi.goFreeThrow(setFreeThrowDto);
//        if(r.isSuccess()){
//            PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
//            dto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.getFreeThrow(dto);
//        }else {
//            return r;
//        }
//    }
//
//    @PostMapping("/sendFreeThrow")
//    @ApiOperation(value = "罚球发送", notes = "  罚球发送")
//    public Response sendFreeThrow(@RequestBody SendFreeThrowDto setFreeThrowDto, HttpServletRequest request) {
//        setFreeThrowDto.setOperatorId("418");
//        setFreeThrowDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        setFreeThrowDto.setLinkedId(id);
//        Response r  =  ipdBasketBallAdvertiseApi.sendFreeThrow(setFreeThrowDto);
//        if(r.isSuccess()){
//            PDBasketBallPauseDto dto = new PDBasketBallPauseDto();
//            dto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.getFreeThrow(dto);
//        }else {
//            return r;
//        }
//    }
//
//    @PostMapping("/getMatchAdvertiseInfo")
//    @ApiOperation(value = "PA赛事报球版查询", notes = "PA赛事报球版查询")
//    public Response getMatchAdvertiseInfo(@RequestBody MatchAdvertiseQueryDto matchAdvertiseQueryDto, HttpServletRequest request) {
//        MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
//        dto.setThirdMatchId(matchAdvertiseQueryDto.getThirdMatchId());
//        return  ipdBasketBallAdvertiseApi.searchDetail(dto);
//    }
//
    @PostMapping("/changeMatchPeriod")
//@AuthRequiredPermission("Ronghe:MatchManager:MatchResult:query:page")
    @ApiOperation(value = "赛事阶段变更", notes = "赛事阶段变更")
    public Response changeMatchPeriod(@RequestBody ChangeMatchPeriodDto changeMatchPeriodDto, HttpServletRequest request) {
        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(changeMatchPeriodDto));
        changeMatchPeriodDto.setOperatorId("418");
        changeMatchPeriodDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
        id.replaceAll("_","");
        changeMatchPeriodDto.setLinkedId(id);
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(changeMatchPeriodDto,matchAdvertiseQueryDto);
        Response response= matchBasketBallAdvertiseApi.changeMatchPeriod(changeMatchPeriodDto);
//        matchInfoService.sendMatchInfo(changeMatchPeriodDto.getThirdMatchId());
        if(response.isSuccess()){
            return matchBasketBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }
//
//    @PostMapping("/deleteEvent")
//    @ApiOperation(value = " 删除事件", notes = " 删除事件")
//    public Response deleteEvent(@RequestBody PDBasketBallDeleteEventDto deleteEventDto, HttpServletRequest request) {
//        deleteEventDto.setOperatorId("418");
//        deleteEventDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        deleteEventDto.setLinkedId(id);
//        Response r  =  ipdBasketBallAdvertiseApi.deleteEvent(deleteEventDto);
//        if(r.isSuccess()){
//            MatchAdvertiseQueryDto dto = new MatchAdvertiseQueryDto();
//            dto.setThirdMatchId(deleteEventDto.getThirdMatchId());
//            return  ipdBasketBallAdvertiseApi.searchDetail(dto);
//        }else {
//            return r;
//        }
//    }
//
    @PostMapping("/changeMatchScore")
//@AuthRequiredPermission("Ronghe:MatchManager:MatchResult:query:page")
    @ApiOperation(value = "修改比分", notes = "修改比分")
    public Response changeMatchScore(@RequestBody ChangeMatchScoreDto changeMatchScoreDto, HttpServletRequest request) {
        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(changeMatchScoreDto));
        changeMatchScoreDto.setOperatorId("418");
        changeMatchScoreDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
        id.replaceAll("_","");
        changeMatchScoreDto.setLinkedId(id);
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(changeMatchScoreDto,matchAdvertiseQueryDto);
        Response response=matchBasketBallAdvertiseApi.changeMatchScore(changeMatchScoreDto);
        if(response.isSuccess()){
            return matchBasketBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }
//
//    @PostMapping("/searchAllScore")
//    @ApiOperation(value = "  统计比分查询", notes = " 统计比分查询")
//    public Response searchAllScore(@RequestBody PDBasketBallParseContinueDto setFreeThrowDto, HttpServletRequest request) {
//        setFreeThrowDto.setOperatorId("418");
//        setFreeThrowDto.setOperatorName("cptest");
//        String id= "PD_"+ UUID.randomUUID().toString();
//        id.replaceAll("_","");
//        setFreeThrowDto.setLinkedId(id);
//        PDBasketBallParseContinueDto dto = new PDBasketBallParseContinueDto();
//        dto.setThirdMatchId(setFreeThrowDto.getThirdMatchId());
//        return ipdBasketBallAdvertiseApi.searchAllScore(dto);
//    }
//
    @PostMapping("/changeMatchStatus")
//@AuthRequiredPermission("Ronghe:MatchManager:MatchResult:query:page")
    @ApiOperation(value = "暂停/继续/结束/开始", notes = "暂停/继续/结束/开始")
    public Response changeMatchStatus(@RequestBody ChangeMatchStatusDto changeMatchStatus, HttpServletRequest request) {
        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(changeMatchStatus));
        changeMatchStatus.setOperatorId("418");
        changeMatchStatus.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
        id.replaceAll("_","");
        changeMatchStatus.setLinkedId(id);
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(changeMatchStatus,matchAdvertiseQueryDto);
        Response response=matchBasketBallAdvertiseApi.changeMatchStatus(changeMatchStatus);
        if(response.isSuccess()){
            return matchBasketBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }
//
    @PostMapping("/changeMatchTime")
//@AuthRequiredPermission("Ronghe:MatchManager:MatchResult:query:page")
    @ApiOperation(value = "修改倒计时", notes = "修改倒计时")
    public Response changeMatchTime(@RequestBody ChangeMatchTimeDto changeMatchTimeDto, HttpServletRequest request) {
        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(changeMatchTimeDto));
        changeMatchTimeDto.setOperatorId("418");
        changeMatchTimeDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
        id.replaceAll("_","");
        changeMatchTimeDto.setLinkedId(id);
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(changeMatchTimeDto,matchAdvertiseQueryDto);
        Response response= matchBasketBallAdvertiseApi.changeMatchTime(changeMatchTimeDto);
        if(response.isSuccess()){
            if(changeMatchTimeDto.getSportId()!=null&&changeMatchTimeDto.getSportId()==1){
                return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
            }else {
                return matchBasketBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
            }
        }else {
            return response;
        }
    }
}
