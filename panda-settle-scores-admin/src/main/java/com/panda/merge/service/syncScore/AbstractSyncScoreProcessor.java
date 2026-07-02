package com.panda.merge.service.syncScore;

import com.panda.merge.constant.SettleSyncEnum;
import com.panda.merge.dto.settle.SettleMatchScoreDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: abstract sync score processor
 * @author: Henry Wang
 * @create: 2024-09-13 14:48
 **/
@Slf4j
@Component
public abstract class AbstractSyncScoreProcessor implements ISyncScoreProcessor{
    @Override
    public void syncScore(Object object) {
        log.info("[AbstractSyncScoreProcessor] syncScore with parameters: {}", object);
        Object data = buildData(object);
        if (data == null) {
            log.info("[AbstractSyncScoreProcessor] syncScore buildData is null!");
            return;
        }
        log.info("[AbstractSyncScoreProcessor] syncScore doProcess start!");
        doProcess(data);
        log.info("[AbstractSyncScoreProcessor] syncScore end!");
    }

    protected abstract Object buildData(Object object);

    protected abstract void doProcess(Object object);

    protected abstract SettleSyncEnum settleSync();

    protected boolean support(SettleSyncEnum settleSyncEnum) {
        return settleSync() == settleSyncEnum;
    }
}
