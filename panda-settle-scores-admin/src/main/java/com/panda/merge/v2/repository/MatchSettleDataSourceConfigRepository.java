package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleDataSourceConfig;
import com.panda.merge.model.MatchSettleDataSourceConfigExample;
import com.panda.merge.v2.entity.MatchSettleDataSourceConfigEntity;

import java.util.List;

public interface MatchSettleDataSourceConfigRepository extends IService<MatchSettleDataSourceConfigEntity> {
    boolean updateDataSourceConfigToRedis(MatchSettleDataSourceConfig matchSettleDataSourceConfig,boolean isInsert);

    MatchSettleDataSourceConfig getByIdFromRedis(Long id);

    List<MatchSettleDataSourceConfig> getMatchSettleDataSourceConfig(Integer level,Long sportId,String dataSourceCode);

    List<MatchSettleDataSourceConfig> selectByExample(MatchSettleDataSourceConfigExample example);

}