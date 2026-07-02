package com.panda.merge.service.impl;

import com.panda.merge.model.MatchSettleOperateLog;
import com.panda.merge.respository.MatchSettleOperateLogRepository;
import com.panda.merge.service.IMatchSettleOperateLogService;
import groovy.util.logging.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MatchSettleOperateLogServiceImpl implements IMatchSettleOperateLogService {

    @Resource
    private MatchSettleOperateLogRepository matchSettleOperateLogRepository;

    @Override
    @Async("RemoveDBThreadPool")
    public void saveOrUpdateBatch(List<MatchSettleOperateLog> matchSettleOperateLogs) {
        if(CollectionUtils.isEmpty(matchSettleOperateLogs)){
            return;
        }
        matchSettleOperateLogRepository.saveOrUpdateBatch(matchSettleOperateLogs);
    }
}
