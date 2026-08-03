package com.panda.merge.v2.service.impl;

import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleGoalStatus;
import com.panda.merge.v2.repository.MatchSettleGoalStatusRepository;
import com.panda.merge.v2.service.IMatchSettleGoalStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class MatchSettleGoalStatusServiceImpl implements IMatchSettleGoalStatusService {

    @Autowired
    private MatchSettleGoalStatusRepository matchSettleGoalStatusRepository;

    @Override
    public void updateMatchSettleCornerStatus(MatchEventInfo matchEventInfo) {
        try {
            MatchSettleGoalStatus goalStatus = matchSettleGoalStatusRepository.getById(matchEventInfo.getThirdMatchId());
            if (goalStatus == null) {
                goalStatus = new MatchSettleGoalStatus();
                goalStatus.setId(matchEventInfo.getThirdMatchId());
                goalStatus.setStandardMatchId(matchEventInfo.getStandardMatchId());
                goalStatus.setDataSourceCode(matchEventInfo.getDataSourceCode());
                goalStatus.setCreateTime(System.currentTimeMillis());
                goalStatus.setModifyTime(System.currentTimeMillis());
                goalStatus.setCornerStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,true);
                log.info("::{}::updateMatchSettleCornerStatus 新增角球确认状态:CONFIRM", matchEventInfo.getLinkId());
            }
        } catch (Exception e) {
            log.error("MatchSettleCheckServiceImpl-updateMatchSettleCornerStatus error:", e);
        }
    }

    @Override
    public void updateMatchSettleGoalStatus(MatchEventInfo matchEventInfo) {
        try {
            log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 开始处理", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
            MatchSettleGoalStatus goalStatus = matchSettleGoalStatusRepository.getById(matchEventInfo.getThirdMatchId());
            if (goalStatus == null) {
                goalStatus = new MatchSettleGoalStatus();
                goalStatus.setId(matchEventInfo.getThirdMatchId());
                goalStatus.setStandardMatchId(matchEventInfo.getStandardMatchId());
                goalStatus.setDataSourceCode(matchEventInfo.getDataSourceCode());
                goalStatus.setCreateTime(System.currentTimeMillis());
                goalStatus.setModifyTime(System.currentTimeMillis());
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,true);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 新增进球确认状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
            }
            if (matchEventInfo.getEventCode().equals("goal") && matchEventInfo.getCanceled() == 0) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.NOT_CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 更新进球确认状态:NOT_CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            if (matchEventInfo.getEventCode().equals("goal") && matchEventInfo.getCanceled() == 1) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 删除逻辑 更新进球确认状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            if (matchEventInfo.getDataSourceCode().equals("SR") && MatchSettleCheckConstant.GoalConfirmEventCode.SR.equals(matchEventInfo.getEventCode())) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            if (matchEventInfo.getDataSourceCode().equals("BG") && MatchSettleCheckConstant.GoalConfirmEventCode.BG.equals(matchEventInfo.getEventCode())) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            if (matchEventInfo.getDataSourceCode().equals("RB") && MatchSettleCheckConstant.GoalConfirmEventCode.RB.equals(matchEventInfo.getEventCode())) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            // 排球（sportId=9）ct=5/6 走 timeout/timeout_over，语义等同于其它球种的 match_status，需一并触发结算。
            boolean volleyballStatusEvent = Long.valueOf(9L).equals(matchEventInfo.getSportId())
                    && ("timeout".equals(matchEventInfo.getEventCode()) || "timeout_over".equals(matchEventInfo.getEventCode()));
            if (MatchSettleCheckConstant.GoalConfirmEventCode.RB.equals(matchEventInfo.getEventCode())
                    || "match_status".equals(matchEventInfo.getEventCode())
                    || volleyballStatusEvent) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            //N01
            if (MatchSettleCheckConstant.GoalConfirmEventCode.N01.equals(matchEventInfo.getEventCode())&& matchEventInfo.getDataSourceCode().equals("N01") ) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            //L01
            if (MatchSettleCheckConstant.GoalConfirmEventCode.LS.equals(matchEventInfo.getEventCode())&& matchEventInfo.getDataSourceCode().equals("LS") ) {
                goalStatus.setGoalStatus(MatchSettleCheckConstant.GoalStatus.CONFIRM);
                goalStatus.setModifyTime(System.currentTimeMillis());
                matchSettleGoalStatusRepository.updateOrInsertMatchSettleGoalStatus(goalStatus,false);
                log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus  进球确认更新状态:CONFIRM", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
                return;
            }
            log.info("linkId::{}::eventId:{} updateMatchSettleGoalStatus 处理完成", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId());
        } catch (Exception e) {
            log.error("linkId::{}::eventId:{} updateMatchSettleGoalStatus error:", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId(), e);
        }
    }
}
