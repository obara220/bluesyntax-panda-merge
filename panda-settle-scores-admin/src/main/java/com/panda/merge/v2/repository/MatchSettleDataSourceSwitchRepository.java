package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleDataSourceSwitch;
import com.panda.merge.v2.entity.MatchSettleDataSourceSwitchEntity;

import java.util.List;

public interface MatchSettleDataSourceSwitchRepository extends IService<MatchSettleDataSourceSwitchEntity> {
    boolean updateDataSourceSwitchToRedis(MatchSettleDataSourceSwitch dataSourceSwitch, boolean isInsert);

    List<MatchSettleDataSourceSwitch> getModelBySportIdAndDataSource(Long sportId, String dataSource, String gray);

    List<MatchSettleDataSourceSwitch> getMatchSettleDataSourceSwitchByRedis(Long sportId, String dataSource);

    boolean delMatchSettleDataSourceSwitchBy(Long sportId,String dataSource);
}