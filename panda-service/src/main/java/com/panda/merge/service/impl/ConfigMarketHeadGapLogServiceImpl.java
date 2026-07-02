package com.panda.merge.service.impl;

import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.TradeMarketHeadGapConfigDTO;
import com.panda.merge.mapper.ConfigMarketCategoryHeadLogMapper;
import com.panda.merge.model.ConfigMarketCategoryHeadLog;
import com.panda.merge.service.ConfigMarketHeadGapLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-10-03 11:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class ConfigMarketHeadGapLogServiceImpl implements ConfigMarketHeadGapLogService {

    @Autowired
    private ConfigMarketCategoryHeadLogMapper configMarketCategoryHeadLogMapper;

    @Override
    public ConfigMarketCategoryHeadLog create(String linkId, Long operaterId, TradeMarketHeadGapConfigDTO tradeMarketHeadGapConfigDTO) {
        ConfigMarketCategoryHeadLog configMarketCategoryHeadLog = new ConfigMarketCategoryHeadLog();
        configMarketCategoryHeadLog.setId(UUIdUtils.getId());
        configMarketCategoryHeadLog.setStandardMatchInfoId(tradeMarketHeadGapConfigDTO.getStandardMatchInfoId());
        configMarketCategoryHeadLog.setStandardCategoryId(tradeMarketHeadGapConfigDTO.getStandardCategoryId());
        configMarketCategoryHeadLog.setChildStandardCategoryId(tradeMarketHeadGapConfigDTO.getChildStandardCategoryId());
        configMarketCategoryHeadLog.setMarketType(tradeMarketHeadGapConfigDTO.getMarketType());
        configMarketCategoryHeadLog.setMarketHeadGap(tradeMarketHeadGapConfigDTO.getMarketHeadGap());
        configMarketCategoryHeadLog.setLinkId(linkId);
        configMarketCategoryHeadLog.setOperaterId(operaterId);
        configMarketCategoryHeadLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketCategoryHeadLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        configMarketCategoryHeadLogMapper.insertSelective(configMarketCategoryHeadLog);
        return configMarketCategoryHeadLog;
    }
}
