package com.panda.merge.service.syncScore;

import com.panda.merge.constant.SettleSyncEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: sync score factory
 * @author: Henry Wang
 * @create: 2024-09-13 15:02
 **/
@Component
public class SyncScoreFactory {

    @Resource
    public List<AbstractSyncScoreProcessor> abstractSyncScoreProcessorList;

    public ISyncScoreProcessor getProcessor(SettleSyncEnum settleSyncEnum) {
        return abstractSyncScoreProcessorList.stream().filter(t->t.support(settleSyncEnum)).findAny().orElse(null);
    }
}
