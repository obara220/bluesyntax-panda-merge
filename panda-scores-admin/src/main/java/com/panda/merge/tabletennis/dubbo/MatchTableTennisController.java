package com.panda.merge.tabletennis.dubbo;

import com.panda.merge.api.IMatchTableTennisApi;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.EditScoreV2Dto;
import com.panda.merge.dto.advertise.v2.EventListV2Dto;
import com.panda.merge.tabletennis.service.MatchTableTennisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 乒乓球报球板特有动作的 Dubbo 入口（scoreList / batchEditScores）。
 * 公共动作（含暂停/重开 controlType=2/3）走
 * {@link com.panda.merge.snooker.dubbo.MatchScoreCommonController}，
 * 由 MatchFactory 根据 sportId=8 分发到 MatchTableTennisServiceImpl。
 */
@Service
@Slf4j
@DubboService
public class MatchTableTennisController implements IMatchTableTennisApi {

    @Resource
    private MatchTableTennisService matchTableTennisService;

    @Override
    public Response scoreList(EventListV2Dto eventListV2Dto) {
        try {
            return matchTableTennisService.scoreList(eventListV2Dto);
        } catch (Exception e) {
            log.error("[MatchTableTennisController]scoreList linkId:{} error:",
                    eventListV2Dto.getLinkedId(), e);
            return Response.failed(e.getMessage());
        }
    }

    @Override
    public Response batchEditScores(EditScoreV2Dto editScoreV2Dto) {
        try {
            return matchTableTennisService.batchEditScores(editScoreV2Dto);
        } catch (Exception e) {
            log.error("[MatchTableTennisController]batchEditScores linkId:{} error:",
                    editScoreV2Dto != null ? editScoreV2Dto.getLinkedId() : "unknown", e);
            return Response.failed(e.getMessage());
        }
    }
}
