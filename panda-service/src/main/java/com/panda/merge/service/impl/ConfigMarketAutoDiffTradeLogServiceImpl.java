package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.TradeMarketAutoDiffConfigItemDTO;
import com.panda.merge.mapper.ConfigMarketAutoDiffTradeLogMapper;
import com.panda.merge.model.ConfigMarketAutoDiffTradeLog;
import com.panda.merge.service.ConfigMarketAutoDiffTradeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-10-20 15:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class ConfigMarketAutoDiffTradeLogServiceImpl implements ConfigMarketAutoDiffTradeLogService {

    @Autowired
    private ConfigMarketAutoDiffTradeLogMapper marketAutoDiffTradeLogMapper;

    @Override
    public ConfigMarketAutoDiffTradeLog create(String linkId, TradeMarketAutoDiffConfigItemDTO tradeMarketAutoDiffConfigItemDTO, Long matchId, Long operaterId) {
        ConfigMarketAutoDiffTradeLog configMarketAutoDiffTradeLog = new ConfigMarketAutoDiffTradeLog();
        configMarketAutoDiffTradeLog.setId(UUIdUtils.getId());
        configMarketAutoDiffTradeLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketAutoDiffTradeLog.setDiffValue(tradeMarketAutoDiffConfigItemDTO.getDiffValue());
        configMarketAutoDiffTradeLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketAutoDiffTradeLog.setOddsType(tradeMarketAutoDiffConfigItemDTO.getOddType());
        configMarketAutoDiffTradeLog.setStandardMarketId(tradeMarketAutoDiffConfigItemDTO.getMarketId());
        configMarketAutoDiffTradeLog.setStandardCategoryId(tradeMarketAutoDiffConfigItemDTO.getMarketCategoryId());
        configMarketAutoDiffTradeLog.setStandardMatchId(matchId);
        configMarketAutoDiffTradeLog.setLinkId(linkId);
        configMarketAutoDiffTradeLog.setOperaterId(operaterId);
//        marketAutoDiffTradeLogMapper.insertSelective(configMarketAutoDiffTradeLog);
        return configMarketAutoDiffTradeLog;
    }
}
