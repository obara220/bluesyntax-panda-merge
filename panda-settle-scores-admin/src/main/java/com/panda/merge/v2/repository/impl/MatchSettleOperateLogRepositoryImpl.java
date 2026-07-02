package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.model.MatchSettleOperateLog;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleOperateLogConverter;
import com.panda.merge.v2.entity.MatchSettleOperateLogEntity;
import com.panda.merge.v2.mapper.MatchSettleOperateLogV3Mapper;
import com.panda.merge.v2.repository.MatchSettleOperateLogV2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository("MatchSettleOperateLogRepositoryV2")
public class MatchSettleOperateLogRepositoryImpl extends ServiceImpl<MatchSettleOperateLogV3Mapper, MatchSettleOperateLogEntity> implements MatchSettleOperateLogV2Repository {

    @Autowired
    private MatchSettleOperateLogConverter matchSettleOperateLogConverter;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    @Override
    public void saveOrUpdateBatch(List<MatchSettleOperateLog> matchSettleOperateLogs) {
        if(CollectionUtils.isEmpty(matchSettleOperateLogs)){
            return;
        }
        List<MatchSettleOperateLogEntity> entities = matchSettleOperateLogConverter.convertMatchSettleOperateLogToEntity(matchSettleOperateLogs);
        List<Object> objects = entities.stream().map(t->(Object) t).collect(Collectors.toList());
        String linkId = "match-settle-operate-log-" + UUIdUtils.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, objects,
                CommonConstant.SETTLE_OPERATE_LOG_TABLE, true);
    }

    @Override
    public void saveOrUpdateBatch(List<MatchSettleOperateLogEntity> matchSettleOperateLogEntityList, boolean isInsert) {
        List<MatchSettleOperateLog> entities = matchSettleOperateLogConverter.convertEntityToOperateLog(matchSettleOperateLogEntityList);
        if (isInsert) {
            saveOrUpdateBatch(entities);
        }
    }

    @Override
    public void save(MatchSettleOperateLog matchSettleOperateLogs) {
        MatchSettleOperateLogEntity entity = matchSettleOperateLogConverter.convertMatchSettleOperateLogToEntity(matchSettleOperateLogs);
        save(entity);
    }

    @Override
    public boolean save(MatchSettleOperateLogEntity entity) {
        String linkId = "match-settle-operate-log-" + UUIdUtils.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(entity),
                CommonConstant.SETTLE_OPERATE_LOG_TABLE, true);
        return true;
    }
}
