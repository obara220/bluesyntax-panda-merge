package com.panda.merge.rocketmq.processor;

import cn.hutool.json.JSONUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchAnalysisModifyTimeDTO;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.ThirdMatchPromotionChartExample;
import com.panda.merge.model.ThirdSportTournament;
import com.panda.merge.service.ThirdMatchPromotionChartService;
import com.panda.merge.service.ThirdSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Validated
@Component
public class ThirdMatchAnalysisModifyTimeUpdateProcessor extends BaseProcessor {

    @Resource
    private ThirdSportTournamentService thirdSportTournamentService;
    @Resource
    private ThirdMatchPromotionChartService thirdMatchPromotionChartService;

    @ExceptionHelper
    public void updateThirdMatchAnalysisModifyTime(@Valid Request<ThirdMatchAnalysisModifyTimeDTO> request){
        ThirdMatchAnalysisModifyTimeDTO data = request.getData();
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_ANALYSIS_MODIFY_TIME+"】【::"+request.getLinkId()+"::】更新赛事分析modifyTime开始,请求参数={}", JSONUtil.toJsonStr(data));
        if (data ==null) {
            log.error("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_ANALYSIS_MODIFY_TIME+"】【::"+request.getLinkId()+"::】请求参数data为空");
            return;
        }
        ThirdSportTournament thirdSportTournament = thirdSportTournamentService.getThirdSportTournament(data.getThirdTournamentId());
        if (thirdSportTournament == null) {
            log.error("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_ANALYSIS_MODIFY_TIME+"】【::"+request.getLinkId()+"::】三方联赛数据为空,thirdTournamentId={}", data.getThirdTournamentId());
            return;
        }
        String dataSourceCode = data.getDataSourceCode();
        String thirdSeasonSourceId = thirdSportTournament.getThirdSeasonSourceId();
        String thirdTournamentSourceId = thirdSportTournament.getThirdTournamentSourceId();
        int count = 0;
        switch (data.getTarget()) {
            case THIRD_MATCH_PROMOTION_CHART_API:
                if(StringUtils.isNotBlank(dataSourceCode) && StringUtils.isNotBlank(thirdSeasonSourceId) && StringUtils.isNotBlank(thirdTournamentSourceId)){
                    ThirdMatchPromotionChartExample example = new ThirdMatchPromotionChartExample();
                    example.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andSeasonIdEqualTo(thirdSeasonSourceId).andTournamentIdEqualTo(thirdTournamentSourceId);
                    count = thirdMatchPromotionChartService.updateModifyTimeByExampleSelective(System.currentTimeMillis(),example);
                }
                break;
            default:
        }
        log.info("【"+PROJECT_ID_REALTIME+" ："+THIRD_MATCH_ANALYSIS_MODIFY_TIME+"】【::"+request.getLinkId()+"::】更新赛事分析modifyTime结束,更新条数={}",count);

    }
}
