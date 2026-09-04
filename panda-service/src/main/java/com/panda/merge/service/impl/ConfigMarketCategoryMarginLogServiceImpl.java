package com.panda.merge.service.impl;

import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.MarketMarginDtlDTO;
import com.panda.merge.dto.TradeMarketMarginConfigDTO;
import com.panda.merge.mapper.ConfigMarketCategoryMarginLogMapper;
import com.panda.merge.model.ConfigMarketCategoryMarginLog;
import com.panda.merge.service.ConfigMarketCategoryMarginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-10-20 14:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class ConfigMarketCategoryMarginLogServiceImpl implements ConfigMarketCategoryMarginLogService {

    @Autowired
    private ConfigMarketCategoryMarginLogMapper marketCategoryMarginLogMapper;

    @Override
    public ConfigMarketCategoryMarginLog create(String linkId, Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId,Integer marketType, Integer placeNum, MarketMarginDtlDTO marketMarginDtlDTO, Long operaterId) {
        ConfigMarketCategoryMarginLog marketCategoryMarginLog = new ConfigMarketCategoryMarginLog();
        marketCategoryMarginLog.setId(UUIdUtils.getId());
        marketCategoryMarginLog.setStandardMatchInfoId(standardMatchInfoId);
        marketCategoryMarginLog.setStandardCategoryId(standardCategoryId);
        marketCategoryMarginLog.setChildStandardCategoryId(childStandardCategoryId);
        marketCategoryMarginLog.setMarketType(marketType);
        marketCategoryMarginLog.setPlaceNum(placeNum);
        marketCategoryMarginLog.setOddsType(marketMarginDtlDTO.getOddsType());
        marketCategoryMarginLog.setTimeFrame(marketMarginDtlDTO.getTimeFrame());
        marketCategoryMarginLog.setMargin(marketMarginDtlDTO.getMargin());
        marketCategoryMarginLog.setLinkId(linkId);
        marketCategoryMarginLog.setOperaterId(operaterId);
        marketCategoryMarginLog.setCreateTime(System.currentTimeMillis());
        marketCategoryMarginLog.setModifyTime(System.currentTimeMillis());
//        marketCategoryMarginLogMapper.insertSelective(marketCategoryMarginLog);
        return marketCategoryMarginLog;
    }
}
