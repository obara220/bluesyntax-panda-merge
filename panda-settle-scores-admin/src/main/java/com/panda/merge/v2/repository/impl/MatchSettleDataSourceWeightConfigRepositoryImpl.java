package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.model.MatchSettleDataSourceWeightConfig;
import com.panda.merge.model.MatchSettleDataSourceWeightConfigExample;
import com.panda.merge.v2.converter.MatchSettleDataSourceConfigConverter;
import com.panda.merge.v2.converter.MatchSettleDataSourceWeightConfigConverter;
import com.panda.merge.v2.entity.MatchSettleDataSourceWeightConfigEntity;
import com.panda.merge.v2.mapper.MatchSettleDataSourceWeightConfigV2Mapper;
import com.panda.merge.v2.repository.MatchSettleDataSourceWeightConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MatchSettleDataSourceWeightConfigRepositoryV2")
public class MatchSettleDataSourceWeightConfigRepositoryImpl extends ServiceImpl<MatchSettleDataSourceWeightConfigV2Mapper, MatchSettleDataSourceWeightConfigEntity> implements MatchSettleDataSourceWeightConfigRepository {

    @Autowired
    private MatchSettleDataSourceWeightConfigV2Mapper matchSettleDataSourceWeightConfigV2Mapper;
    @Autowired
    private MatchSettleDataSourceWeightConfigConverter matchSettleDataSourceWeightConfigConverter;

    @Override
    public List<MatchSettleDataSourceWeightConfig> selectByExample(MatchSettleDataSourceWeightConfigExample example) {
        List<MatchSettleDataSourceWeightConfigEntity> entities = matchSettleDataSourceWeightConfigV2Mapper.selectByExample(example);
        return matchSettleDataSourceWeightConfigConverter.convertEntityToWeightConfig(entities);
    }

    @Override
    public void deleteByExample(MatchSettleDataSourceWeightConfigExample example) {
        matchSettleDataSourceWeightConfigV2Mapper.deleteByExample(example);
    }
}
