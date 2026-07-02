package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.ThirdBetCancelDTO;
import com.panda.merge.dto.ThirdBetCancelRollbackDTO;
import com.panda.merge.dto.ThirdBetSettlementRollbackDTO;
import com.panda.merge.mapper.ThirdBetOperationLogMapper;
import com.panda.merge.model.ThirdBetOperationLog;
import com.panda.merge.service.ThirdBetOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : TODO
 * @date: 2020-09-09 14:41
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Service
public class ThirdBetOperationLogServiceImpl implements ThirdBetOperationLogService {

    @Autowired
    private ThirdBetOperationLogMapper thirdBetOperationLogMapper;

    @Override
    public void thirdBetCancelCreate(ThirdBetCancelDTO thirdBetCancelDTO) {
        ThirdBetOperationLog thirdBetOperationLog = new ThirdBetOperationLog();
        BeanUtils.copyProperties(thirdBetCancelDTO, thirdBetOperationLog);
        thirdBetOperationLog.setId(IdWorker.getId());
        thirdBetOperationLog.setOperationType(Constant.STANDARD_BET_CANCEL);
        thirdBetOperationLog.setSendTimeStamp(thirdBetCancelDTO.getSendTimestamp());
        thirdBetOperationLog.setSourceTimeStamp(thirdBetCancelDTO.getSourceTimestamp());
        thirdBetOperationLog.setThirdMatchSourceId(thirdBetCancelDTO.getThirdSourceMatchId());
        thirdBetOperationLog.setProduct(String.valueOf(thirdBetCancelDTO.getProduct()));
        thirdBetOperationLog.setMarketData(JSON.toJSONString(thirdBetCancelDTO.getMarkets()));
        thirdBetOperationLog.setSendTimeStampPandaData(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdBetOperationLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdBetOperationLogMapper.insertSelective(thirdBetOperationLog);
    }

    @Override
    public void betCancelRollbackCreate(ThirdBetCancelRollbackDTO thirdBetCancelRollbackDTO) {
        ThirdBetOperationLog thirdBetOperationLog = new ThirdBetOperationLog();
        BeanUtils.copyProperties(thirdBetCancelRollbackDTO, thirdBetOperationLog);
        thirdBetOperationLog.setId(IdWorker.getId());
        thirdBetOperationLog.setOperationType(Constant.STANDARD_BET_CANCEL_ROLLBACK);
        thirdBetOperationLog.setSendTimeStamp(thirdBetCancelRollbackDTO.getSendTimestamp());
        thirdBetOperationLog.setSourceTimeStamp(thirdBetCancelRollbackDTO.getSourceTimestamp());
        thirdBetOperationLog.setThirdMatchSourceId(thirdBetCancelRollbackDTO.getThirdSourceMatchId());
        thirdBetOperationLog.setProduct(String.valueOf(thirdBetCancelRollbackDTO.getProduct()));
        thirdBetOperationLog.setMarketData(JSON.toJSONString(thirdBetCancelRollbackDTO.getMarkets()));
        thirdBetOperationLog.setSendTimeStampPandaData(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdBetOperationLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdBetOperationLogMapper.insertSelective(thirdBetOperationLog);
    }

    @Override
    public void betBetSettlementRollbackCreate(ThirdBetSettlementRollbackDTO thirdBetSettlementRollbackDTO) {
        ThirdBetOperationLog thirdBetOperationLog = new ThirdBetOperationLog();
        BeanUtils.copyProperties(thirdBetSettlementRollbackDTO, thirdBetOperationLog);
        thirdBetOperationLog.setId(IdWorker.getId());
        thirdBetOperationLog.setOperationType(Constant.STANDARD_BET_SETTLEMENT_ROLLBACK);
        thirdBetOperationLog.setSendTimeStamp(thirdBetSettlementRollbackDTO.getSendTimestamp());
        thirdBetOperationLog.setSourceTimeStamp(thirdBetSettlementRollbackDTO.getSourceTimestamp());
        thirdBetOperationLog.setThirdMatchSourceId(thirdBetSettlementRollbackDTO.getThirdSourceMatchId());
        thirdBetOperationLog.setProduct(String.valueOf(thirdBetSettlementRollbackDTO.getProduct()));
        thirdBetOperationLog.setMarketData(JSON.toJSONString(thirdBetSettlementRollbackDTO.getMarkets()));
        thirdBetOperationLog.setSendTimeStampPandaData(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdBetOperationLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        thirdBetOperationLogMapper.insertSelective(thirdBetOperationLog);
    }
}
