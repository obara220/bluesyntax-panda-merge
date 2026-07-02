package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.mapper.MatchSettleThirdScoreMapper;
import com.panda.merge.model.MatchSettleCheckInfoExample;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.model.MatchSettleThirdScoreExample;
import com.panda.merge.v2.converter.MatchSettleThirdScoreConverter;
import com.panda.merge.v2.entity.MatchSettleThirdScoreEntity;
import com.panda.merge.v2.mapper.MatchSettleThirdScoreV3Mapper;
import com.panda.merge.v2.repository.MatchSettleThirdScoreV2Repository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatchSettleThirdScoreRepositoryImpl extends ServiceImpl<MatchSettleThirdScoreV3Mapper, MatchSettleThirdScoreEntity> implements MatchSettleThirdScoreV2Repository {
    @Autowired
    private MatchSettleThirdScoreConverter matchSettleThirdScoreConverter;
    @Autowired
    private MatchSettleThirdScoreMapper matchSettleThirdScoreMapper;

    @Override
    public List<MatchSettleThirdScore> getModelByStandardMatchIdAndSettleNum(Long standardMatchId, List<String> settleNums) {
        LambdaQueryWrapper<MatchSettleThirdScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!=null,MatchSettleThirdScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(settleNums),MatchSettleThirdScoreEntity::getSettleNum,settleNums);
        List<MatchSettleThirdScoreEntity> entities = this.list(queryWrapper);
        return matchSettleThirdScoreConverter.convertEntityToSettleThirdScore(entities);
    }

    @Override
    public List<MatchSettleThirdScore> getModelByMatchIdAndEventCodeOrderBySettleNum(Long standardMatchId, List<String> eventCodes) {
        LambdaQueryWrapper<MatchSettleThirdScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!=null,MatchSettleThirdScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes),MatchSettleThirdScoreEntity::getEventCode,eventCodes)
                .orderByDesc(MatchSettleThirdScoreEntity::getSettleNum);
        List<MatchSettleThirdScoreEntity> entities = this.list(queryWrapper);
        return matchSettleThirdScoreConverter.convertEntityToSettleThirdScore(entities);
    }

    @Override
    public List<MatchSettleThirdScore> getModelByMatchIdAndEventCodeAndSettleNum(Long standardMatchId, List<String> eventCodes, List<String> settleNums) {
        LambdaQueryWrapper<MatchSettleThirdScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!=null,MatchSettleThirdScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes),MatchSettleThirdScoreEntity::getEventCode,eventCodes)
                .in(CollectionUtils.isNotEmpty(settleNums),MatchSettleThirdScoreEntity::getSettleNum,settleNums);
        List<MatchSettleThirdScoreEntity> entities = this.list(queryWrapper);
        return matchSettleThirdScoreConverter.convertEntityToSettleThirdScore(entities);
    }

    @Override
    public boolean updateById(MatchSettleThirdScore matchSettleThirdScore) {
        MatchSettleThirdScoreEntity entity = matchSettleThirdScoreConverter.convertSettleThirdScoreToEntity(matchSettleThirdScore);
        return super.updateById(entity);
    }

    @Override
    public boolean save(MatchSettleThirdScore matchSettleThirdScore) {
        MatchSettleThirdScoreEntity entity = matchSettleThirdScoreConverter.convertSettleThirdScoreToEntity(matchSettleThirdScore);
        return super.save(entity);
    }

    @Override
    public MatchSettleThirdScore getById(Long id) {
        MatchSettleThirdScoreEntity entity = super.getById(id);
        return matchSettleThirdScoreConverter.convertEntityToSettleThirdScore(entity);
    }

    @Override
    public List<MatchSettleThirdScore> getByMatchIdAndAndDataSourceCodeSettleNum(Long standardMatchId, Long thirdMatchId, String dataSourceCode,List<String> settleNums) {
        LambdaQueryWrapper<MatchSettleThirdScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!=null,MatchSettleThirdScoreEntity::getStandardMatchId, standardMatchId)
                .eq(thirdMatchId!=null,MatchSettleThirdScoreEntity::getThirdMatchId, thirdMatchId)
                .eq(StringUtils.isNotBlank(dataSourceCode),MatchSettleThirdScoreEntity::getDataSourceCode, dataSourceCode)
                .in(CollectionUtils.isNotEmpty(settleNums),MatchSettleThirdScoreEntity::getSettleNum,settleNums);
        List<MatchSettleThirdScoreEntity> entities = this.list(queryWrapper);
        return matchSettleThirdScoreConverter.convertEntityToSettleThirdScore(entities);
    }

    @Override
    public void deleteByExample(MatchSettleThirdScoreExample matchSettleThirdScoreExample) {
        matchSettleThirdScoreMapper.deleteByExample(matchSettleThirdScoreExample);
    }

}
