package com.panda.merge.snooker.dubbo;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.api.IMatchSnookerApi;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;
import com.panda.merge.snooker.service.MatchSnookerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
@DubboService
public class MatchSnookerController implements IMatchSnookerApi {

    @Resource
    private MatchSnookerService matchSnookerService;

    @Resource
    private CommonAdvertiseService commonAdvertiseService;


    @Override
    public Response scoreList(EventListV2Dto eventListV2Dto) {
        return matchSnookerService.scoreList(eventListV2Dto);
    }

    @Override
    public Response rerack(RerackV2Dto rerackV2Dto) throws Exception {
        try {
            MatchScoreAndTimeVo matchScoreAndTimeVo = commonAdvertiseService.searchMatchScoreAndTime(rerackV2Dto.getThirdMatchId());
            return matchSnookerService.rerack(matchScoreAndTimeVo, rerackV2Dto);
        } catch (Exception e) {
            log.info("[MatchScoreCommonController]changeScore linkId:{} error:", rerackV2Dto.getLinkedId(), e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response batchEditScores(EditScoreV2Dto editScoreV2Dto) {
        try {
            matchSnookerService.batchEditScores(editScoreV2Dto);
            return Response.success();
        } catch (Exception e) {
            log.error("[MatchSnookerController]batchEditScores linkId:{} error:", 
                    editScoreV2Dto != null ? editScoreV2Dto.getLinkedId() : "unknown", e);
            return Response.failed(e.getMessage());
        }
    }
}
