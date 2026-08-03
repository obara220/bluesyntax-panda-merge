package com.panda.merge.snooker.service.impl;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 兜底 Processor：用于未实现专属 Processor 的球种（如足球热键兼容委托）。
 *
 * 注意：除热键相关接口外，其它接口统一返回“不支持”，避免误用。
 */
@Service
@Order(Integer.MAX_VALUE)
@Slf4j
public class DefaultMatchCommonProcessor extends AbsMatchCommonProcessor<Object> {

    private static final String NOT_SUPPORTED = "当前球种未实现该通用接口处理器";

    @Override
    public boolean support(Long sportId) {
        return true;
    }

    @Override
    protected Long sportType() {
        return -1L;
    }

    @Override
    public Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto, MatchScoreAndTimeVo matchScoreAndTimeVo) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public Response eventList(EventListV2Dto eventListDto) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public Response deleteEvent(MatchScoreAndTimeVo matchScoreAndTimeVo, DeleteEventV2Dto deleteEventDto) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public Response changeMatchLength(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchLengthV2Dto changeMatchLengthDto) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public Response changeScore(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchScoreV2Dto changeMatchScoreV2Dto) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public Response changeMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public Response kickOff(MatchScoreAndTimeVo matchScoreAndTimeVo, KickOffV2Dto kickOffV2Dto) {
        return Response.failed(NOT_SUPPORTED);
    }

    @Override
    public void addMatchPeriod(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        // no-op
    }

    @Override
    public CommonItem updateMatchScore(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO, Boolean isDelete) {
        return null;
    }
}

