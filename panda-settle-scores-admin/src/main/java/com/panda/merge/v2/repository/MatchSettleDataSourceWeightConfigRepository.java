package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleDataSourceWeightConfig;
import com.panda.merge.model.MatchSettleDataSourceWeightConfigExample;
import com.panda.merge.v2.entity.MatchSettleDataSourceWeightConfigEntity;

import java.util.List;

public interface MatchSettleDataSourceWeightConfigRepository extends IService<MatchSettleDataSourceWeightConfigEntity> {

    List<MatchSettleDataSourceWeightConfig> selectByExample(MatchSettleDataSourceWeightConfigExample example);

    void deleteByExample(MatchSettleDataSourceWeightConfigExample example);

}
