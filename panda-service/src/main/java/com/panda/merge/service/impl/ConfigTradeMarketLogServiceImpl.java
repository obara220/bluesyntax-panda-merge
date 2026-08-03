package com.panda.merge.service.impl;


import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.mapper.ConfigTradeMarketLogMapper;
import com.panda.merge.model.ConfigTradeMarketLog;
import com.panda.merge.service.ConfigTradeMarketLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 */
@Service
public class ConfigTradeMarketLogServiceImpl implements ConfigTradeMarketLogService {

    @Autowired
    private ConfigTradeMarketLogMapper configTradeMarketLogMapper;

    @Override
    @Async("LogRecordExecutor")
    public void create(String linkId, TradeMarketConfigDTO tradeMarketConfigDTO) {
        ConfigTradeMarketLog configTradeMarketLog = new ConfigTradeMarketLog();
        BeanUtils.copyProperties(tradeMarketConfigDTO, configTradeMarketLog);
        configTradeMarketLog.setLinkId(linkId);
        configTradeMarketLog.setConfigModifyTime(tradeMarketConfigDTO.getModifyTime());
        configTradeMarketLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeMarketLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeMarketLogMapper.insertSelective(configTradeMarketLog);
      //  return configTradeMarketLog;
    }
}
