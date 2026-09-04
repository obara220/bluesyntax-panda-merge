package com.panda.merge.snooker.dubbo;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.api.IMatchScoreCommonApi;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;
import com.panda.merge.snooker.service.MatchSnookerService;
import com.panda.merge.snooker.service.impl.MatchFactory;
import com.panda.merge.volleyball.service.MatchVolleyballService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@DubboService
public class MatchScoreCommonController implements IMatchScoreCommonApi {

    @Resource
    private MatchFactory matchFactory;

    @Resource
    private CommonAdvertiseService commonAdvertiseService;

    @Resource
    private MatchSnookerService matchSnookerService;

    @Resource
    private MatchVolleyballService matchVolleyballService;

    @Override
    public Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto) {
        try{
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(changeMatchPeriodV2Dto.getThirdMatchId());
            return matchFactory.getProcessor(changeMatchPeriodV2Dto.getSportId()).getCurrentMatchInfo(changeMatchPeriodV2Dto, matchScoreAndTimeVo);
        } catch (Exception e) {
            log.error("[MatchScoreCommonController]getCurrentMatchInfo linkId:{} error:", changeMatchPeriodV2Dto.getLinkedId(), e);
            return Response.failed(errorMessage(e));
        }
    }

    // NPE 等 e.getMessage()==null 或空字符串时，Response.failed(null) → 前端只能看到 code=500/msg=null。
    // 退到异常类名，让下次告警自带定位线索（同时 log.error 仍保留完整堆栈）。
    private static String errorMessage(Exception e) {
        String msg = e.getMessage();
        return (msg != null && !msg.isEmpty()) ? msg : e.getClass().getSimpleName();
    }

    @Override
    public Response eventList(EventListV2Dto eventListV2Dto) {
        return matchFactory.getProcessor(eventListV2Dto.getSportId()).eventList(eventListV2Dto);
    }

    @Override
    public Response deleteEvent(DeleteEventV2Dto deleteEventV2Dto) {
        try {
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(deleteEventV2Dto.getThirdMatchId());
            return matchFactory.getProcessor(deleteEventV2Dto.getSportId()).deleteEvent(matchScoreAndTimeVo, deleteEventV2Dto);
        } catch (Exception e) {
            log.info("[MatchScoreCommonController]deleteEvent linkId:{} error:", deleteEventV2Dto.getLinkedId(), e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response changeMatchLength(ChangeMatchLengthV2Dto changeMatchLengthV2Dto) {
        try {
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(changeMatchLengthV2Dto.getThirdMatchId());
            return matchFactory.getProcessor(changeMatchLengthV2Dto.getSportId()).changeMatchLength(matchScoreAndTimeVo, changeMatchLengthV2Dto);
        } catch (Exception e) {
            log.info("[MatchScoreCommonController]changeMatchLength linkId:{} error:", changeMatchLengthV2Dto.getLinkedId(), e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response changeScore(ChangeMatchScoreV2Dto changeMatchScoreV2Dto) {
        try {
            log.info("[MatchScoreCommonController]changeScore start linkId::{}:data:{}", changeMatchScoreV2Dto.getLinkedId(), changeMatchScoreV2Dto);
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(changeMatchScoreV2Dto.getThirdMatchId());
            Response response = matchFactory.getProcessor(changeMatchScoreV2Dto.getSportId()).changeScore(matchScoreAndTimeVo, changeMatchScoreV2Dto);
            log.info("[MatchScoreCommonController]changeScore end linkId::{}", changeMatchScoreV2Dto.getLinkedId());
            return response;
        } catch (Exception e) {
            log.error("[MatchScoreCommonController]changeScore linkId::{} error:", changeMatchScoreV2Dto.getLinkedId(), e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response changeMatchStatus(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        try {
            // periodId 兜底已下放到 AbsMatchCommonProcessor.applyDefaultPeriodId（球种可覆写），
            // controller 只做透传；不合法的 controlType 由 service 的 convertEnum 检查返回失败。
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(changeMatchStatusV2Dto.getThirdMatchId());
            return matchFactory.getProcessor(changeMatchStatusV2Dto.getSportId()).changeMatchStatus(matchScoreAndTimeVo, changeMatchStatusV2Dto);
        } catch (Exception e) {
            log.error("[MatchScoreCommonController]changeMatchStatus linkId:{} error:", changeMatchStatusV2Dto.getLinkedId(), e);
            return Response.failed(errorMessage(e));
        }
    }

    @Override
    public Response changeMatchPeriod(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto) {
        try {
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(changeMatchPeriodV2Dto.getThirdMatchId());
            return matchFactory.getProcessor(changeMatchPeriodV2Dto.getSportId()).changeMatchPeriod(matchScoreAndTimeVo, changeMatchPeriodV2Dto);
        } catch (Exception e) {
            log.error("[MatchScoreCommonController]changeMatchPeriod linkId:{} error:", changeMatchPeriodV2Dto.getLinkedId(), e);
            return Response.failed(errorMessage(e));
        }
    }

    @Override
    public Response kickOff(KickOffV2Dto kickOffV2Dto) {
        try {
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(kickOffV2Dto.getThirdMatchId());
            return matchFactory.getProcessor(kickOffV2Dto.getSportId()).kickOff(matchScoreAndTimeVo, kickOffV2Dto);
        } catch (Exception e) {
            log.error("[MatchScoreCommonController]kickOff linkId:{} error:", kickOffV2Dto.getLinkedId(), e);
            return Response.failed(errorMessage(e));
        }
    }

    @Override
    public Response sendEvent(SendEventDto sendEventDto) {
        try {
            log.info("[MatchScoreCommonController]sendEvent start linkId::{}:data:{}", sendEventDto.getLinkedId(), sendEventDto);
            if (sendEventDto.getSportId() == null) {
                return Response.failed("sportId 不能为空");
            }
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(sendEventDto.getThirdMatchId());
            Response response;
            // 仅支持斯诺克（7）和排球（9）：
            // - 斯诺克：MatchSnookerService.sendEvent 处理认输/判输/持杆等定制语义；
            // - 排球：MatchVolleyballService.sendEvent 通过 doCalculation 把 kill / block /
            //         expulsion / disqualification / penalty / error / current_serve_volleyball
            //         等统计字段累加到 scoresJson。其它球种直接拒绝，避免误用通用兜底丢失计分。
            if (Long.valueOf(7L).equals(sendEventDto.getSportId())) {
                response = matchSnookerService.sendEvent(matchScoreAndTimeVo, sendEventDto);
            } else if (Long.valueOf(9L).equals(sendEventDto.getSportId())) {
                response = matchVolleyballService.sendEvent(matchScoreAndTimeVo, sendEventDto);
            } else {
                log.warn("[MatchScoreCommonController]sendEvent unsupported sportId:{} linkId::{}",
                        sendEventDto.getSportId(), sendEventDto.getLinkedId());
                return Response.failed("sendEvent 仅支持斯诺克(7)和排球(9)，sportId=" + sendEventDto.getSportId() + " 不支持");
            }
            log.info("[MatchScoreCommonController]sendEvent end linkId::{}", sendEventDto.getLinkedId());
            return response;
        } catch (Exception e) {
            log.error("[MatchScoreCommonController]sendEvent linkId::{} error:", sendEventDto.getLinkedId(), e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response saveHotkeys(HotkeysSaveDto dto) {
        try {
            log.info("[MatchScoreCommonController]saveHotkeys start sportId:{} userName:{}", dto.getSportId(), dto.getUserName());
            Response response = matchFactory.getProcessor(dto.getSportId()).saveHotkeys(dto);
            log.info("[MatchScoreCommonController]saveHotkeys end sportId:{} userName:{}", dto.getSportId(), dto.getUserName());
            return response;
        } catch (Exception e) {
            log.error("[MatchScoreCommonController]saveHotkeys sportId:{} error:", dto.getSportId(), e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response getHotkeysBySportIdAndUsername(Long sportId, String userName) {
        return matchFactory.getProcessor(sportId).getHotkeysBySportIdAndUsername(sportId, userName);
    }

    @Override
    public Response deleteHotkeysBySportIdAndUsername(Long sportId, String userName) {
        return matchFactory.getProcessor(sportId).deleteHotkeysBySportIdAndUsername(sportId, userName);
    }

    @Override
    public Response deleteHotkeysByUserIds(Long sportId, String userName, List<Long> userIds) {
        return matchFactory.getProcessor(sportId).deleteHotkeysByUserIds(sportId, userName, userIds);
    }
}