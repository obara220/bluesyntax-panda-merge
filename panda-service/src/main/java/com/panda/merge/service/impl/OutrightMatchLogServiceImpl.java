package com.panda.merge.service.impl;

import com.google.common.collect.Lists;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.mapper.OutrightMatchLogMapper;
import com.panda.merge.model.OutrightMatchLog;
import com.panda.merge.service.OutrightMatchLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author Kepa
 * @Date 2021/7/15 12:16
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class OutrightMatchLogServiceImpl implements OutrightMatchLogService {

    @Autowired
    private OutrightMatchLogMapper outrightMatchLogMapper;

    @Override
    public boolean saveBatchOutrightMatchRecord(List<Map<String, String>> listParams, OutrightMatchLog outrightMatchLog) {
        List<OutrightMatchLog> list = Lists.newArrayList();
        listParams.stream().forEach( m -> {
            OutrightMatchLog om = new OutrightMatchLog();
            om.setOperatorNumber(outrightMatchLog.getOperatorNumber());
            om.setOperatorName(outrightMatchLog.getOperatorName());
            om.setOperatorId(outrightMatchLog.getOperatorId());
            om.setOperateTargetId(outrightMatchLog.getOperateTargetId());
            om.setOperatorText(m.get("operatorText"));
            om.setOperatorModle(m.get("operatorModle"));
            om.setOperatorTime(TimeUtils.millsSecondsEast8ZoneGmt());
            //list.add(om);
            outrightMatchLogMapper.insert(om);
        });
        return true;
    }


}
