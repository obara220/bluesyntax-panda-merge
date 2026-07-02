package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.v2.converter.MatchSettleGrayWeightConverter;
import com.panda.merge.model.MatchSettleGrayWeight;
import com.panda.merge.v2.entity.MatchSettleGrayWeightEntity;
import com.panda.merge.v2.mapper.MatchSettleGrayWeightV3Mapper;
import com.panda.merge.v2.repository.MatchSettleGrayWeightV2Repository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
public class MatchSettleGrayWeightRepositoryImpl extends ServiceImpl<MatchSettleGrayWeightV3Mapper, MatchSettleGrayWeightEntity> implements MatchSettleGrayWeightV2Repository {

    @Autowired
    private MatchSettleGrayWeightConverter matchSettleGrayWeightConverter;

    @Override
    public boolean updateById(MatchSettleGrayWeight matchSettleGrayWeight) {
        MatchSettleGrayWeightEntity entity = matchSettleGrayWeightConverter.convertGrayWeightToEntity(matchSettleGrayWeight);
        return super.updateById(entity);
    }

    @Override
    public void saveOrUpdateBatch(List<MatchSettleGrayWeight> matchSettleGrayWeights) {
        if (CollectionUtils.isEmpty(matchSettleGrayWeights)) {
            return;
        }
        List<MatchSettleGrayWeightEntity> entities = matchSettleGrayWeightConverter.convertGrayWeightToEntity(matchSettleGrayWeights);
        super.saveOrUpdateBatch(entities);
    }

    @Override
    public boolean save(MatchSettleGrayWeight matchSettleGrayWeight) {
        MatchSettleGrayWeightEntity entity = matchSettleGrayWeightConverter.convertGrayWeightToEntity(matchSettleGrayWeight);
        return super.save(entity);
    }

    @Override
    public MatchSettleGrayWeight getById(Long id) {
        MatchSettleGrayWeightEntity entity = super.getById(id);
        return matchSettleGrayWeightConverter.convertEntityToGrayWeight(entity);
    }

    @Override
    public List<MatchSettleGrayWeight> getByIds(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<MatchSettleGrayWeightEntity> entities = super.listByIds(ids);
        return matchSettleGrayWeightConverter.convertEntityToGrayWeight(entities);
    }

    @Override
    public List<MatchSettleGrayWeight> getByItems(Long standardMatchId, Long sportId, String grayCode, Integer grayArea, Integer grayStatus) {
        LambdaQueryWrapper<MatchSettleGrayWeightEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleGrayWeightEntity::getStandardMatchId, standardMatchId)
                .eq(sportId != null, MatchSettleGrayWeightEntity::getSportId, sportId)
                .eq(StringUtils.isNotBlank(grayCode), MatchSettleGrayWeightEntity::getGrayCode, grayCode)
                .eq(grayArea != null, MatchSettleGrayWeightEntity::getGrayAreaMin, grayArea)
                .eq(grayStatus != null, MatchSettleGrayWeightEntity::getGrayStatus, grayStatus);

        List<MatchSettleGrayWeightEntity> entities = this.list(queryWrapper);
        return matchSettleGrayWeightConverter.convertEntityToGrayWeight(entities);
    }
}
