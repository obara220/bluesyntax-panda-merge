package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.mapper.MatchSettleScoreMapper;
import com.panda.merge.model.MatchSettleScoreExample;
import com.panda.merge.v2.converter.MatchSettleScoreConverter;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import com.panda.merge.v2.mapper.MatchSettleScoreV3Mapper;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Collections;
import java.util.List;

@Repository
public class MatchSettleScoreRepositoryImpl extends ServiceImpl<MatchSettleScoreV3Mapper, MatchSettleScoreEntity> implements MatchSettleScoreV2Repository {

    @Autowired
    private MatchSettleScoreConverter matchSettleScoreConverter;
/*    @Autowired
    private MatchSettleScoreMapper matchSettleScoreMapper;*/

    @Override
    public boolean updateById(MatchSettleScore matchSettleScore) {
        MatchSettleScoreEntity entity = matchSettleScoreConverter.convertMatchSettleScoreToEntity(matchSettleScore);
        return super.updateById(entity);
    }

    @Override
    public boolean updateBatchById(List<MatchSettleScore> matchSettleScores) {
        List<MatchSettleScoreEntity> entities = matchSettleScoreConverter.convertMatchSettleScoreToEntity(matchSettleScores);
        return super.updateBatchById(entities);
    }

    @Override
    public void saveOrUpdateBatch(List<MatchSettleScore> matchSettleScore) {
        if(CollectionUtils.isEmpty(matchSettleScore)){
            return;
        }
        List<MatchSettleScoreEntity> entities = matchSettleScoreConverter.convertMatchSettleScoreToEntity(matchSettleScore);
        super.saveOrUpdateBatch(entities);
    }

    @Override
    public boolean save(MatchSettleScore matchSettleScore) {
        MatchSettleScoreEntity entity = matchSettleScoreConverter.convertMatchSettleScoreToEntity(matchSettleScore);
        return super.save(entity);
    }

    @Override
    public MatchSettleScore getById(Long id) {
        MatchSettleScoreEntity entity = super.getById(id);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entity);
    }

    @Override
    public List<MatchSettleScore> getByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        List<MatchSettleScoreEntity> entities = super.listByIds(ids);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScore> getModelBySettleNumAndMatchIdIdAndStatus(List<String> settleNumList, Long standardMatchId, List<Integer> status){
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .in(CollectionUtils.isNotEmpty(settleNumList),MatchSettleScoreEntity::getSettleNum, settleNumList)
                .eq(standardMatchId!=null,MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(status),MatchSettleScoreEntity::getStatus, status);
        List<MatchSettleScoreEntity> matchSettleScoreEntities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(matchSettleScoreEntities);
    }

    @Override
    public List<MatchSettleScore> getModelBySettleNumAndMatchIdIdAndNotStatus(List<String> settleNumList, Long standardMatchId, Integer status) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .in(CollectionUtils.isNotEmpty(settleNumList), MatchSettleScoreEntity::getSettleNum, settleNumList)
                .eq(standardMatchId != null, MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .ne(status != null, MatchSettleScoreEntity::getStatus, status);
        List<MatchSettleScoreEntity> matchSettleScoreEntities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(matchSettleScoreEntities);
    }

    @Override
    public List<MatchSettleScore> getModelStandardMatchIdAndNotStatusAndIsGrey(Long standardMatchId, Integer status, Integer isGray) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .ne(status != null, MatchSettleScoreEntity::getStatus, status)
                .eq(isGray != null, MatchSettleScoreEntity::getIsGrey,isGray);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScore> getModelStandardMatchIdAndSettleNumAndIsGrey(Long standardMatchId, List<String> settleNumList, Integer isGray) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(settleNumList), MatchSettleScoreEntity::getSettleNum, settleNumList)
                .eq(isGray != null, MatchSettleScoreEntity::getIsGrey,isGray);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScore> getModelByStandardMatchIdAndNotSettleNum(Long standardMatchId, List<String> settleNumList) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!=null,MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .notIn(CollectionUtils.isNotEmpty(settleNumList),MatchSettleScoreEntity::getSettleNum, settleNumList);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScore> getByStandardMatchIdAndEventCode(Long standardMatchId, String eventCode) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(eventCode), MatchSettleScoreEntity::getEventCode, eventCode);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);

    }

    @Override
    public List<MatchSettleScore> getModelsByItems(Long standardMatchId, List<String> eventCodes, List<Long> periods, Integer status, Integer t1, Integer t2) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleScoreEntity::getEventCode, eventCodes)
                .in(CollectionUtils.isNotEmpty(periods), MatchSettleScoreEntity::getPeriodId, periods)
                .eq(status != null, MatchSettleScoreEntity::getStatus, status)
                .eq(t1 != null, MatchSettleScoreEntity::getT1, t1)
                .eq(t2 != null, MatchSettleScoreEntity::getT2, t2);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScore> getModelByMatchIdAndEventCodeOrderBySettleNum(Long standardMatchId, List<String> eventCodes) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleScoreEntity::getEventCode, eventCodes)
                .orderByDesc(MatchSettleScoreEntity::getSettleNum);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScore> getModelsByItemsAndSettleNums(Long standardMatchId, List<String> eventCodes, List<Long> periods, Integer status, List<String> settleNumList) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleScoreEntity::getEventCode, eventCodes)
                .in(CollectionUtils.isNotEmpty(periods), MatchSettleScoreEntity::getPeriodId, periods)
                .eq(status != null, MatchSettleScoreEntity::getStatus, status)
                .in(CollectionUtils.isNotEmpty(settleNumList), MatchSettleScoreEntity::getSettleNum, settleNumList);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScore> getByMatchIdAndEventCodeAndNotStatus(Long standardMatchId, List<String> eventCodes, Integer status) {
        LambdaQueryWrapper<MatchSettleScoreEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!=null,MatchSettleScoreEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes),MatchSettleScoreEntity::getEventCode, eventCodes)
                .ne(status!=null,MatchSettleScoreEntity::getStatus, status);
        List<MatchSettleScoreEntity> entities = this.list(queryWrapper);
        return matchSettleScoreConverter.convertMatchSettleScoreEntityToScore(entities);
    }

    @Override
    public List<MatchSettleScoreEntity> selectByExample(MatchSettleScoreExample example) {
        return baseMapper.selectByExample(example);
    }

    @Override
    public int updateByExampleSelective(MatchSettleScore matchSettleScore, MatchSettleScoreExample matchSettleScoreExample){
        return baseMapper.updateByExampleSelective(matchSettleScore,matchSettleScoreExample);
    }

}
