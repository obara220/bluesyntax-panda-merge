package com.panda.merge.advertise.dubbo.mycontroller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.advertise.dubbo.FootballDashboardHotKeyApiImpl;
import com.panda.merge.advertise.dubbo.myfiles.MatchEndCancelRequest;
import com.panda.merge.api.FootballDashboardAdvertiseApi;
import com.panda.merge.api.IMatchBasketBallAdvertiseApi;
import com.panda.merge.api.IMatchFootballBallAdvertiseApi;
import com.panda.merge.api.IScoresCenterApi;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.AllFootballScoreEditDto;
import com.panda.merge.dto.advertise.CancelEventDto;
import com.panda.merge.dto.advertise.ChangeMatchPeriodDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.ConfirmEventDto;
import com.panda.merge.dto.advertise.DeleteEventDto;
import com.panda.merge.dto.advertise.EventListDto;
import com.panda.merge.dto.advertise.EventOperationDto;
import com.panda.merge.dto.advertise.InjuryTimeEventDto;
import com.panda.merge.dto.advertise.IsDangerDto;
import com.panda.merge.dto.advertise.KickOffDto;
import com.panda.merge.dto.advertise.MatchAdvertiseQueryDto;
import com.panda.merge.dto.advertise.NoRetakePenDto;
import com.panda.merge.dto.advertise.PenaltyAddRoundsDto;
import com.panda.merge.dto.advertise.PenaltyScoresEditDto;
import com.panda.merge.dto.advertise.PossibleEventDto;
import com.panda.merge.dto.advertise.RetakePenDto;
import com.panda.merge.dto.advertise.TimeStatusEventDto;
import com.panda.merge.dto.advertise.v2.ChangeMatchScoreV2Dto;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.merge.model.FootballKeyboardSet;
import com.panda.merge.snooker.dubbo.MatchScoreCommonController;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * @author warren
 * @since 2023/12/22 19:32:29
 */
@RestController
@RequestMapping("/pdMatch/football")
@Slf4j
public class PDDashboardController {
    @Autowired
    private FootballDashboardHotKeyApiImpl footballDashboardHotKeyApi;

    @Autowired
    private IMatchFootballBallAdvertiseApi matchFootballBallAdvertiseApi;

    @Autowired
    private FootballDashboardAdvertiseApi footballDashboardAdvertiseApi;

    @Autowired
    private IScoresCenterApi iScoresCenterApi;
    @Autowired
    private IMatchFootballBallAdvertiseApi footballBallAdvertiseApi;

    @Autowired
    private IMatchBasketBallAdvertiseApi matchBasketBallAdvertiseApi;

    @Autowired
    private MatchScoreCommonController matchScoreCommonController;

    @GetMapping("/hotkey")
    public Response getKeyboardByUserName(@RequestParam("userName") String userName) {
        Long thirdMatchId = 123456L;
        return footballDashboardHotKeyApi.getKeyboardByUserNameAndThirdMatchId(userName,thirdMatchId);
    }

    @PostMapping("/hotkey")
    public Response insertKeyboardInfo(@RequestBody FootballKeyboardSet footballKeyboardSet) {
        return footballDashboardHotKeyApi.addKeyboardInfo(footballKeyboardSet);
    }

    @PutMapping("/hotkey")
    public Response updateKeyboardByUserName(@RequestBody FootballKeyboardSet footballKeyboardSet) {
        return footballDashboardHotKeyApi.updateKeyboardByUserName(footballKeyboardSet);
    }

    @DeleteMapping("/hotkey/{userName}")
    public Response removeKeyboardByUserName(@PathVariable("userName") String userName) {
        Long sportId=2L;
        return footballDashboardHotKeyApi.removeKeyboardByUserNameAndThirdMatchId(userName,sportId);
    }

    @DeleteMapping("/userid")
    public Response removeKeyboardByUserIds(@RequestParam("userIds") List<String> userIds) {
//        List<FootballKeyboardSet> list = footballKeyboardSetMapper.selectKeyboardByUserIdList(userIds);
//        int count = footballKeyboardSetMapper.deleteKeyboardByUserIdList(userIds);
//        Map<Object, Object> map = new HashMap<>();
//        map.put("selectList",list);
//        map.put("deleteCount",count);
//        return Response.success(map);
        return footballDashboardHotKeyApi.deleteKeyboardByUserIdList(userIds);
    }

    @PostMapping("/confirmEvent")
    public Response confirmEvent(@RequestBody ConfirmEventDto confirmEventDto) {

//      User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        confirmEventDto.setOperatorId("418");
        confirmEventDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(confirmEventDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        confirmEventDto.setLinkedId(id);
//      confirmEventDto.setIpAddress(IpUtils.getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(confirmEventDto,matchAdvertiseQueryDto);
        log.info("开始完成比赛事件+++:{}",confirmEventDto);
        Response response = matchFootballBallAdvertiseApi.confirmEvent(confirmEventDto);
        log.info("执行matchFootballBallAdvertiseApi,返回数据:{}",response);
        if(response.isSuccess()){
            log.info("执行matchFootballBallAdvertiseApi传值为:{}",matchAdvertiseQueryDto);
            // CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() -> continueEvent(confirmEventDto)));
            return matchFootballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        } else {
            return response;
        }
    }


    @PostMapping("/changeScore")
    public Response changeScore(@RequestBody ChangeMatchScoreV2Dto changeMatchScoreV2Dto) {

//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        changeMatchScoreV2Dto.setOperatorId("418");
        changeMatchScoreV2Dto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(confirmEventDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        changeMatchScoreV2Dto.setLinkedId(id);
//        confirmEventDto.setIpAddress(IpUtils.getIpAddress(request));

        return matchScoreCommonController.changeScore(changeMatchScoreV2Dto);

    }
    @PostMapping("/possibleEvent")
    @ApiOperation(value = "可能事件", notes = "可能事件")
    public Response possibleEvent(@RequestBody PossibleEventDto possibleEventDto, HttpServletRequest request) {

//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        possibleEventDto.setOperatorId("418");
        possibleEventDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(possibleEventDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        possibleEventDto.setLinkedId(id);
        possibleEventDto.setIpAddress(getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(possibleEventDto,matchAdvertiseQueryDto);
        Response response=footballBallAdvertiseApi.possibleEvent(possibleEventDto);
        if(response.isSuccess()){
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }

    /**
     * 取消比分事件
     * */
    @PostMapping("/cancelEvent")
    @ApiOperation(value = "取消比分事件", notes = "取消比分事件")
    public Response cancelEvent(@RequestBody CancelEventDto cancelEventDto, HttpServletRequest request){
        String requestId = request.getHeader("request-id");
        cancelEventDto.setRequestId(requestId);
        String lang = request.getHeader("lang");
        cancelEventDto.setLanguage(lang);
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        cancelEventDto.setOperatorId("418");
        cancelEventDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(cancelEventDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        log.info("linkId=::{}::,requestId=::{}::足球报球板取消事件，thirdMatchId={},timeFromStartSecond={},eventCode={},homeAway={}",
                id, requestId, cancelEventDto.getThirdMatchId(), cancelEventDto.getTimeFromStartSecond(), cancelEventDto.getCancelEventCode(), cancelEventDto.getHomeAway());
        cancelEventDto.setLinkedId(id);
        cancelEventDto.setIpAddress("127.0.0.1");
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(cancelEventDto,matchAdvertiseQueryDto);
        Response response=footballBallAdvertiseApi.cancelEvent(cancelEventDto);
        if(response.isSuccess()){
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }

    }

    @PostMapping("/getMatchAdvertiseInfo")
    @ApiOperation(value = "足球报球版查询", notes = "足球报球版查询")
    public Response getMatchAdvertiseInfo(@RequestBody MatchAdvertiseQueryDto matchAdvertiseQueryDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        matchAdvertiseQueryDto.setOperatorId("418");
        matchAdvertiseQueryDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        ThirdMatchInfo oldThirdMatch = thirdMatchInfoService.getById(matchAdvertiseQueryDto.getThirdMatchId());
//        matchAdvertiseQueryDto.setStandardMatchId(oldThirdMatch.getReferenceId());
        matchAdvertiseQueryDto.setLinkedId(id);
        //1.判断是否有高级权限，有则根据数据商编码选择返回，默认返回PD
//        String code = getReportPdCode(matchAdvertiseQueryDto);
//        boolean hasPDPermission = hasPDPermission(user);
        //报球员生效权限最高
//        if(StringUtils.isNotEmpty(code)){
//            matchAdvertiseQueryDto.setDataSourceCode(code);
//        }else if(hasPDPermission){
//            //其次默认权限最高
//            if(StringUtils.isEmpty(matchAdvertiseQueryDto.getDataSourceCode())){
//                matchAdvertiseQueryDto.setDataSourceCode("PD");
//            }
//        }
        //上述校验没给过则默认给PD
        if(StringUtils.isEmpty(matchAdvertiseQueryDto.getDataSourceCode())) {
            matchAdvertiseQueryDto.setDataSourceCode("PD");
        }
//        if(StringUtils.isEmpty(matchAdvertiseQueryDto.getDataSourceCode())){
//            return Response.failed("无权访问或不是本赛事报球员");
//        }
//        Long thirdMatchId = getThirdMatchIdByAdvertiseQuery(matchAdvertiseQueryDto);
//        if(thirdMatchId==null){
//            return Response.failed("报球未生成,请编辑对应报球审核员或者切换到报球版");
//        }
        matchAdvertiseQueryDto.setThirdMatchId(matchAdvertiseQueryDto.getThirdMatchId());
        Response response=footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        return response;
    }

    @PostMapping("/eventList")
    @ApiOperation(value = "eventList", notes = "eventList")
    public Response eventList(@RequestBody EventListDto eventListDto, HttpServletRequest request){
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        eventListDto.setOperatorId("418");
        eventListDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();

        eventListDto.setLinkedId(id);
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(eventListDto,matchAdvertiseQueryDto);
        Response response=footballBallAdvertiseApi.eventList(eventListDto);
        return response;
    }

    @PostMapping("/editAllScore")
    @ApiOperation(value = "参与结算的开关更新X", notes = "参与结算的开关更新")
    public Response editAllScore(@RequestBody AllFootballScoreEditDto allFootballScoreEditDto, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(allFootballScoreEditDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
//        updateSettleStatusDto.setLinkedId(id);
        allFootballScoreEditDto.setOperatorName("cptest");
//        String ipAdrress = IpUtils.getIpAddress(request);
//        allFootballScoreEditDto.setIpAddress(ipAdrress);
        return footballBallAdvertiseApi.editAllScore( allFootballScoreEditDto);
    }


    @PostMapping("/injurytimeevent")
    @ApiOperation(value = "伤停补时事件", notes = "伤停补时事件")
    public Response injuryTimeEvent(@RequestBody InjuryTimeEventDto injuryTimeEventDto, HttpServletRequest req) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(req), SsoUtil.getAppId(req));
        injuryTimeEventDto.setOperatorId("418");
        injuryTimeEventDto.setOperatorName("cptest");
//        if (!userPermission.permissionCheck(injuryTimeEventDto.getThirdMatchId(), user)) {
//            return Response.failed("权限不足");
//        }
        return footballDashboardAdvertiseApi.injuryTimeEvent(injuryTimeEventDto);
//        if (BooleanUtil.isTrue(res.isSuccess())) {
//            injuryTimeEventDto.setLinkedId("PD_" + UUID.randomUUID());
//            injuryTimeEventDto.setIpAddress(IpUtils.getIpAddress(req));
//            MatchAdvertiseQueryDto matchAdvertiseQueryDto = new MatchAdvertiseQueryDto();
//            BeanUtils.copyProperties(injuryTimeEventDto, matchAdvertiseQueryDto);
//            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
//        } else {
//            return res;
//        }
    }

    @PostMapping("/timestatusevent")
    @ApiOperation(value = "时间状态事件", notes = "时间状态事件")
    public Response timeStatusEvent(@RequestBody TimeStatusEventDto timeStatusEventDto, HttpServletRequest req) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(req), SsoUtil.getAppId(req));
        timeStatusEventDto.setOperatorId("418");
        timeStatusEventDto.setOperatorName("cptest");
//        if (!userPermission.permissionCheck(timeStatusEventDto.getThirdMatchId(), user)) {
//            return Response.failed("权限不足");
//        }
        return footballDashboardAdvertiseApi.timeStatusEvent(timeStatusEventDto);
//        if (res.isSuccess()) {
//            timeStatusEventDto.setLinkedId("PD_" + UUID.randomUUID());
//            timeStatusEventDto.setIpAddress(IpUtils.getIpAddress(req));
//            MatchAdvertiseQueryDto matchAdvertiseQueryDto = new MatchAdvertiseQueryDto();
//            BeanUtils.copyProperties(timeStatusEventDto, matchAdvertiseQueryDto);
//            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
//        } else {
//            return res;
//        }
    }

    /**
     * 点球大战编辑接口
     * */
    @PostMapping("/editPenaltyScore")
    @ApiOperation(value = "点球大战编辑接口", notes = "点球大战编辑接口")
    public Response editPenaltyScore(@RequestBody PenaltyScoresEditDto penaltyScoresEditDto, HttpServletRequest request){
        String requestId = request.getHeader("request-id");
        penaltyScoresEditDto.setRequestId(requestId);
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        penaltyScoresEditDto.setOperatorId("418");
        penaltyScoresEditDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(penaltyScoresEditDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        penaltyScoresEditDto.setLinkedId(id);
        penaltyScoresEditDto.setIpAddress("127.0.0.1");
        return footballBallAdvertiseApi.editPenaltyScore(penaltyScoresEditDto);
    }

    /**
     * 点球大战新增轮次接口
     * */
    @PostMapping("/addPenaltyRounds")
    @ApiOperation(value = "点球大战新增轮次接口", notes = "点球大战新增轮次接口")
    public Response addPenaltyRounds(@RequestBody PenaltyAddRoundsDto penaltyAddRoundsDto, HttpServletRequest request){
        String requestId = request.getHeader("request-id");
        penaltyAddRoundsDto.setRequestId(requestId);
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        penaltyAddRoundsDto.setOperatorId("418");
        penaltyAddRoundsDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(penaltyAddRoundsDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        penaltyAddRoundsDto.setLinkedId(id);
        penaltyAddRoundsDto.setIpAddress("127.0.0.1");
        return footballBallAdvertiseApi.addPenaltyRounds(penaltyAddRoundsDto);
    }

    @PostMapping("/addVarEvent")
    @ApiOperation(value = "手动下发var事件", notes = "手动下发var事件")
    public Response addVarEvent(HttpServletRequest request, HttpServletResponse response, @RequestBody EventOperationDto isDangerDto) {

        //生成linkid
        String linkedId = DataSourceCodeEnum.PA.code + UUIdUtils.getId();
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
//        log.info("::{}:: 用户信息: {}", linkedId, JSON.toJSONString(user));
        isDangerDto.setOperatorName("cptest");
        isDangerDto.setOperatorId("418");
        isDangerDto.setLinkedId(linkedId);
        isDangerDto.setIpAddress(getIpAddress(request));
        log.info("::{}:: 赛事：{} , 手动下发var事件消息MQ消息开始", linkedId, isDangerDto.getMatchId());
        Response<MatchEventInfoMessage> matchEventResponse = null;
        try {
            matchEventResponse = iScoresCenterApi.addVarEvent(isDangerDto);
            log.info("::{}:: 赛事：{}, 手动下发var事件消息MQ消息结束", linkedId, isDangerDto.getMatchId());
        } catch (Exception e) {
            log.info("::{}:: 赛事：{} , 手动下发var事件消息异常", linkedId, String.valueOf(e));
            Response.failed(matchEventResponse.getMsg());
        }
        if (matchEventResponse != null && matchEventResponse.getCode() == 200) {
            return Response.success(linkedId);
        } else {
            return Response.failed(matchEventResponse.getMsg());
        }
    }

    @PostMapping("/changeMatchStatus")
    @ApiOperation(value = "暂停/继续", notes = "暂停/继续")
    public Response changeMatchStatus(@RequestBody ChangeMatchStatusDto changeMatchStatus, HttpServletRequest request) {
        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(changeMatchStatus));
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        changeMatchStatus.setOperatorId("418");
        changeMatchStatus.setOperatorName("cptest");
        String id = "PD_" + UUID.randomUUID();
        id.replaceAll("_", "");
        changeMatchStatus.setLinkedId(id);
        changeMatchStatus.setIpAddress(getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto = new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(changeMatchStatus, matchAdvertiseQueryDto);
        Response response = footballDashboardAdvertiseApi.changeMatchStatus(changeMatchStatus);
        if (response.isSuccess()) {
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        } else {
            return response;
        }
    }

    @PostMapping("/changeMatchPeriod")
    @ApiOperation(value = "修改阶段", notes = "修改阶段")
    public Response changeMatchPeriod(@RequestBody ChangeMatchPeriodDto changeMatchPeriodDto, HttpServletRequest request) {

//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        changeMatchPeriodDto.setOperatorId("418");
        changeMatchPeriodDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(changeMatchPeriodDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        changeMatchPeriodDto.setLinkedId(id);
        changeMatchPeriodDto.setIpAddress(getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(changeMatchPeriodDto,matchAdvertiseQueryDto);
        Response response=footballBallAdvertiseApi.changeMatchPeriod(changeMatchPeriodDto);
        if(response.isSuccess()){
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }

    @PostMapping("/changeMatchTime")
//@AuthRequiredPermission("Ronghe:MatchManager:MatchResult:query:page")
    @ApiOperation(value = "修改倒计时", notes = "修改倒计时")
    public Response changeMatchTime(@RequestBody ChangeMatchTimeDto changeMatchTimeDto, HttpServletRequest request) {
        log.info("对阵管理，被修改的比赛id:{}", JSON.toJSONString(changeMatchTimeDto));
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        changeMatchTimeDto.setOperatorId("418");
        changeMatchTimeDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
        id.replaceAll("_","");
        changeMatchTimeDto.setLinkedId(id);
        changeMatchTimeDto.setIpAddress(getIpAddress(request));
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

    /**
     * 比赛结束
     * */
    @PostMapping("/setMatchEnd")
    @ApiOperation(value = "比赛结束", notes = "比赛结束")
    public Response setMatchEnd(@RequestBody ChangeMatchStatusDto changeMatchStatus, HttpServletRequest request) {

//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        changeMatchStatus.setOperatorId("418");
        changeMatchStatus.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(changeMatchStatus.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        changeMatchStatus.setLinkedId(id);
        changeMatchStatus.setIpAddress(getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(changeMatchStatus,matchAdvertiseQueryDto);
        Response response= footballBallAdvertiseApi.setMatchEnd(changeMatchStatus);
        if(response.isSuccess()){
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }

    @PostMapping("/cancelMatchEnd")
    @ApiOperation(value = "参与结算的开关更新", notes = "参与结算的开关更新")
    public Response cancelMatchEnd(@RequestBody MatchEndCancelRequest matchEndCancelRequest, HttpServletRequest request){

//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
//        updateSettleStatusDto.setOperatorId(user.getUserId());
//        updateSettleStatusDto.setOperatorName(user.getUserName());
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(matchEndCancelRequest.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
//        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getById(matchEndCancelRequest.getThirdMatchId());
//        updateSettleStatusDto.setLinkedId(id);
        String ipAdrress = getIpAddress(request);
//        updateSettleStatusDto.setIpAddress(ipAdrress);
        return footballBallAdvertiseApi.cancelMatchEnd(matchEndCancelRequest.getThirdMatchId(),matchEndCancelRequest.getSecondFromStart().intValue(),
                matchEndCancelRequest.getMatchPeriod(),"cptest","418",ipAdrress);
//        if(response.isSuccess()){
//            matchInfoService.toSendModifyMatchInfoData(String.valueOf(thirdMatchInfo.getReferenceId()), thirdMatchInfo.getReferenceId(), true);
//            return Response.success();
//        }else {
//            return response;
//        }

    }

    @PostMapping("/kickOffAfterGoal")
    @ApiOperation(value = "kickOff", notes = "kickOff")
    public Response kickOffAfterGoal(@RequestBody KickOffDto kickOff, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
        kickOff.setRequestId(requestId);
        String lang = request.getHeader("lang");
        kickOff.setLanguage(lang);
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        kickOff.setOperatorId("418");
        kickOff.setOperatorName("cptest");
        String id = "PD_" + UUID.randomUUID();
        kickOff.setLinkedId(id);
        kickOff.setIpAddress("127.0.0.1");
        MatchAdvertiseQueryDto matchAdvertiseQueryDto = new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(kickOff, matchAdvertiseQueryDto);
        Response response = footballBallAdvertiseApi.kickOffAfterGoal(kickOff);
        if (response.isSuccess()) {
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        } else {
            return response;
        }
    }

    @PostMapping("/retakePen")
    @ApiOperation(value = "retakePenDto", notes = "retakePenDto")
    public Response retakePen(@RequestBody RetakePenDto retakePen, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
        retakePen.setRequestId(requestId);
        String lang = request.getHeader("lang");
        retakePen.setLanguage(lang);
        retakePen.setOperatorId("418");
        retakePen.setOperatorName("cptest");
        String id = "PD_" + UUID.randomUUID();
        retakePen.setLinkedId(id);
        retakePen.setIpAddress("127.0.0.1");
        MatchAdvertiseQueryDto matchAdvertiseQueryDto = new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(retakePen, matchAdvertiseQueryDto);
        Response response = footballBallAdvertiseApi.retakePen(retakePen);
        if (response.isSuccess()) {
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        } else {
            return response;
        }
    }

    @PostMapping("/noRetakePen")
    @ApiOperation(value = "noRetakePenDto", notes = "noRetakePenDto")
    public Response noRetakePen(@RequestBody NoRetakePenDto noRetakePenDto, HttpServletRequest request) {
        String requestId = request.getHeader("request-id");
        noRetakePenDto.setRequestId(requestId);
        String lang = request.getHeader("lang");
        noRetakePenDto.setLanguage(lang);
        noRetakePenDto.setOperatorId("418");
        noRetakePenDto.setOperatorName("cptest");
        String id = "PD_" + UUID.randomUUID();
        noRetakePenDto.setLinkedId(id);
        noRetakePenDto.setIpAddress("127.0.0.1");
        MatchAdvertiseQueryDto matchAdvertiseQueryDto = new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(noRetakePenDto, matchAdvertiseQueryDto);
        Response response = footballBallAdvertiseApi.noRetakePen(noRetakePenDto);
        if (response.isSuccess()) {
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        } else {
            return response;
        }
    }

    @PostMapping("/isDanger")
    @ApiOperation(value = "isDanger", notes = "isDanger")
    public Response  isDanger(@RequestBody IsDangerDto isDangerDto, HttpServletRequest request){
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        isDangerDto.setOperatorId("418");
        isDangerDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString().replace("-","");
//        if(!permissionCheck(isDangerDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        isDangerDto.setLinkedId(id);
        isDangerDto.setIpAddress(getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(isDangerDto,matchAdvertiseQueryDto);
        Response response=footballBallAdvertiseApi.isDanger(isDangerDto);
        if(response.isSuccess()){
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }
    @PostMapping("/deleteEvent")
    @ApiOperation(value = "deleteEvent", notes = "deleteEvent")
    public Response deleteEvent(@RequestBody DeleteEventDto deleteEventDto, HttpServletRequest request){
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        deleteEventDto.setOperatorId("418");
        deleteEventDto.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(deleteEventDto.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        deleteEventDto.setLinkedId(id);
        deleteEventDto.setIpAddress(getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(deleteEventDto,matchAdvertiseQueryDto);
        Response response=footballBallAdvertiseApi.deleteEvent(deleteEventDto);
        if(response.isSuccess()){
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }
    }

    @PostMapping("/kickOff")
    @ApiOperation(value = "开球", notes = "开球")
    public Response kickOff(@RequestBody KickOffDto kickOff, HttpServletRequest request) {
//        User user = iUserService.getUserSession(SsoUtil.getUserId(request), SsoUtil.getAppId(request));
        kickOff.setIpAddress(getIpAddress(request));
        kickOff.setOperatorId("418");
        kickOff.setOperatorName("cptest");
        String id= "PD_"+ UUID.randomUUID().toString();
//        if(!permissionCheck(kickOff.getThirdMatchId(),user)){
//            return Response.failed("权限不足");
//        }
        kickOff.setLinkedId(id);
        kickOff.setIpAddress(getIpAddress(request));
        MatchAdvertiseQueryDto matchAdvertiseQueryDto =new MatchAdvertiseQueryDto();
        BeanUtils.copyProperties(kickOff,matchAdvertiseQueryDto);
        Response response=footballBallAdvertiseApi.kickOff(kickOff);
        if(response.isSuccess()){
            return footballBallAdvertiseApi.getMatchAdvertiseInfo(matchAdvertiseQueryDto);
        }else {
            return response;
        }

    }


    //获得request里面的ip地址
    public static String getIpAddress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第⼀个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StrUtil.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }
}
