package com.panda.merge.odds.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.odds.model.FlowControlState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * FlowControlService
 *
 * @description:
 * @date: 7/16/2025
 **/
@Service
@Slf4j
public class FlowControlService {

    @NacosValue(value = "${odds.flowcontrol.enabled:true}", autoRefreshed = true)
    private Boolean isEnabled;

    @Autowired
    private FlowControlConfigService flowControlConfigService;

    public List<OddsWrapper<ThirdMatchMarketDTO>> filter(List<OddsWrapper<ThirdMatchMarketDTO>> marketList) {
        if (!isEnabled) {
            return marketList;
        }
        FlowControlState flowControlState = flowControlConfigService.get();
        if (Objects.isNull(flowControlState) || flowControlState.isMatchFilterDisabled()) {
            return marketList;
        }
        return marketList.stream().filter(wrapper ->

                                           {
                                               if (flowControlState.fcMatchIds.contains(wrapper.getStandardSourceId())) {
                                                   log.info(
                                                           "linkId:{},thirdMatchId:{},standardMathId:{},flow control match " +
                                                                   "close",
                                                           wrapper.getLinkId(),
                                                           wrapper.getThirdMatchId(),
                                                           wrapper.getStandardSourceId());
                                                   return false;
                                               }
                                               return true;
                                           }).collect(Collectors.toList());
    }

    public boolean inFlowControl(Long matchId) {
        if (!isEnabled) {
            return false;
        }
        FlowControlState flowControlState = flowControlConfigService.get();
        if (Objects.isNull(flowControlState) || flowControlState.isMatchFilterDisabled()) {
            return false;
        }
        return flowControlState.fcMatchIds.contains(matchId);
    }
}
