package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleOperateLog;
import com.panda.merge.v2.entity.MatchSettleOperateLogEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleOperateLogConverter {

    MatchSettleOperateLogEntity convertMatchSettleOperateLogToEntity(MatchSettleOperateLog matchSettleOperateLog);

    List<MatchSettleOperateLogEntity> convertMatchSettleOperateLogToEntity(List<MatchSettleOperateLog> matchSettleOperateLogs);

    MatchSettleOperateLog convertEntityToOperateLog(MatchSettleOperateLogEntity matchSettleOperateLogEntity);

    List<MatchSettleOperateLog> convertEntityToOperateLog(List<MatchSettleOperateLogEntity> matchSettleOperateLogEntities);

}
