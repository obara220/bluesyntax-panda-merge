package com.panda.merge.service.impl;


import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.mapper.ConfigTradeMarketMapper;
import com.panda.merge.model.ConfigTradeMarket;
import com.panda.merge.service.ConfigTradeMarketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ConfigTradeMarketServiceImpl implements ConfigTradeMarketService {

    @Autowired
    private ConfigTradeMarketMapper configTradeMarketMapper;

    @Override
    public ConfigTradeMarket create(String linkId, TradeMarketConfigDTO tradeMarketConfigDTO) {
        ConfigTradeMarket configTradeMarket = new ConfigTradeMarket();
        BeanUtils.copyProperties(tradeMarketConfigDTO,configTradeMarket);
        configTradeMarket.setLinkId(linkId);
        configTradeMarket.setConfigModifyTime(tradeMarketConfigDTO.getModifyTime());
        configTradeMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeMarket.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeMarketMapper.insertSelective(configTradeMarket);
        return configTradeMarket;
    }
}
