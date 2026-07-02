package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.v2.converter.MatchDelaySettleInfoConverter;
import com.panda.merge.v2.entity.MatchDelaySettleInfoEntity;
import com.panda.merge.v2.mapper.MatchDelaySettleInfoV3Mapper;
import com.panda.merge.v2.repository.MatchDelaySettleInfoV2Repository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class MatchDelaySettleInfoRepositoryImpl extends ServiceImpl<MatchDelaySettleInfoV3Mapper, MatchDelaySettleInfoEntity> implements MatchDelaySettleInfoV2Repository {

    @Autowired
    private MatchDelaySettleInfoConverter matchDelaySettleInfoConverter;

    @Override
    public boolean updateById(MatchDelaySettleInfo matchDelaySettleInfo) {
        MatchDelaySettleInfoEntity entity = matchDelaySettleInfoConverter.convertMatchDelaySettleInfoToEntity(matchDelaySettleInfo);
        return super.updateById(entity);
    }

    @Override
    public boolean save(MatchDelaySettleInfo matchDelaySettleInfo) {
        MatchDelaySettleInfoEntity entity = matchDelaySettleInfoConverter.convertMatchDelaySettleInfoToEntity(matchDelaySettleInfo);
        return super.save(entity);
    }

    @Override
    public MatchDelaySettleInfo getById(Long id) {
        MatchDelaySettleInfoEntity entity = super.getById(id);
        return matchDelaySettleInfoConverter.convertEntityToDelaySettle(entity);
    }

    @Override
    public boolean removeByMatchIdAndDataSourceCodeAndCheckInfoId(Long standardMatchId, String dataSourceCode, Long checkInfoId) {
        LambdaQueryWrapper<MatchDelaySettleInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchDelaySettleInfoEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(dataSourceCode), MatchDelaySettleInfoEntity::getDataSourceCode, dataSourceCode)
                .eq(checkInfoId != null, MatchDelaySettleInfoEntity::getCheckInfoId, checkInfoId);
        return super.remove(queryWrapper);
    }

    @Override
    public List<MatchDelaySettleInfo> getModelByStandardMatchId(Long standardMatchId) {
        LambdaQueryWrapper<MatchDelaySettleInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchDelaySettleInfoEntity::getStandardMatchId, standardMatchId);
        List<MatchDelaySettleInfoEntity> entities = this.list(queryWrapper);
        return matchDelaySettleInfoConverter.convertEntityToDelaySettle(entities);
    }

    @Override
    public List<MatchDelaySettleInfo> getModelByMatchIdAndCheckIds(Long standardMatchId, List<Long> checkInfoIds) {
        LambdaQueryWrapper<MatchDelaySettleInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchDelaySettleInfoEntity::getStandardMatchId, standardMatchId)
                .in(checkInfoIds != null, MatchDelaySettleInfoEntity::getCheckInfoId, checkInfoIds);
        List<MatchDelaySettleInfoEntity> entities = this.list(queryWrapper);
        return matchDelaySettleInfoConverter.convertEntityToDelaySettle(entities);
    }

    @Override
    public void saveOrUpdateBatch(List<MatchDelaySettleInfo> matchDelaySettleInfos) {
        if(CollectionUtils.isEmpty(matchDelaySettleInfos)){
            return;
        }
        List<MatchDelaySettleInfoEntity> entities = matchDelaySettleInfoConverter.convertMatchDelaySettleInfoToEntity(matchDelaySettleInfos);
        super.saveOrUpdateBatch(entities);
    }

    @Override
    public void updateStatusByScoreIds(List<Long> scoreIds, Integer status) {
        if(CollectionUtils.isEmpty(scoreIds)){
            return;
        }
        LambdaUpdateWrapper<MatchDelaySettleInfoEntity> chainWrapper = new LambdaUpdateWrapper<>();
        chainWrapper.set(!scoreIds.isEmpty(), MatchDelaySettleInfoEntity::getSettleStatus, status)
                .set(MatchDelaySettleInfoEntity::getModifyTime, System.currentTimeMillis())
                .in(MatchDelaySettleInfoEntity::getScoreId, scoreIds);
        update(chainWrapper);
    }

    @Override
    public void updateStatusByCheckInfoIds(List<Long> checkInfoIds, Integer status) {
        if(CollectionUtils.isEmpty(checkInfoIds)){
            return;
        }
        LambdaUpdateWrapper<MatchDelaySettleInfoEntity> chainWrapper = new LambdaUpdateWrapper<>();
        chainWrapper.set(!checkInfoIds.isEmpty(), MatchDelaySettleInfoEntity::getSettleStatus, status)
                .set(MatchDelaySettleInfoEntity::getModifyTime, System.currentTimeMillis())
                .in(MatchDelaySettleInfoEntity::getCheckInfoId, checkInfoIds);
        update(chainWrapper);
    }
}
