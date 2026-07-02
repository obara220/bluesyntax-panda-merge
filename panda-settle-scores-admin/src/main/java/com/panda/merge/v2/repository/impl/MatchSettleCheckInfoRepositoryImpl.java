package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.mapper.MatchSettleCheckInfoMapper;
import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.model.MatchSettleCheckInfoExample;
import com.panda.merge.v2.converter.MatchSettleCheckInfoV2Converter;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.v2.mapper.MatchSettleCheckInfoV3Mapper;
import com.panda.merge.v2.repository.MatchSettleCheckInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MatchSettleCheckInfoRepositoryImplV2")
public class MatchSettleCheckInfoRepositoryImpl extends ServiceImpl<MatchSettleCheckInfoV3Mapper, MatchSettleCheckInfoEntity> implements MatchSettleCheckInfoRepository {
    @Autowired
    private MatchSettleCheckInfoV2Converter matchSettleCheckInfoV2Converter;
    @Autowired
    private MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;

    @Override
    public boolean deleteByThirdScoreEventIdAndMatchIdAndDataSourceCode(Long thirdScoreEventId, Long standardMatchId, String dataSourceCode) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleCheckInfoEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(dataSourceCode), MatchSettleCheckInfoEntity::getDataSourceCode, dataSourceCode)
                .eq(thirdScoreEventId != null, MatchSettleCheckInfoEntity::getThirdSettleScoreEventId, thirdScoreEventId);
        return super.remove(queryWrapper);
    }

    @Override
    public boolean save(MatchSettleCheckInfo matchSettleCheckInfo) {
        MatchSettleCheckInfoEntity entity = matchSettleCheckInfoV2Converter.convertCheckInfoToEntity(matchSettleCheckInfo);
        return super.save(entity);
    }

    @Override
    public boolean updateById(MatchSettleCheckInfo matchSettleCheckInfo) {
        MatchSettleCheckInfoEntity entity = matchSettleCheckInfoV2Converter.convertCheckInfoToEntity(matchSettleCheckInfo);
        return super.updateById(entity);
    }

    @Override
    public void saveOrUpdateBatch(List<MatchSettleCheckInfo> matchSettleCheckInfos) {
        if(CollectionUtils.isEmpty(matchSettleCheckInfos)){
            return;
        }

        List<MatchSettleCheckInfoEntity> entities = matchSettleCheckInfoV2Converter.convertCheckInfoToEntity(matchSettleCheckInfos);
        super.saveOrUpdateBatch(entities);
    }

    @Override
    public List<MatchSettleCheckInfo> getModelBySettleScoreEventId(Long scoreEventId) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(scoreEventId != null, MatchSettleCheckInfoEntity::getSettleScoreEventId, scoreEventId);
        List<MatchSettleCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(entities);
    }

    @Override
    public List<MatchSettleCheckInfoEntity> getBySettleScoreEventIdAndStandardMatchIdAndCheckDataType(Long scoreEventId, Long matchId, Integer checkType) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(scoreEventId != null, MatchSettleCheckInfoEntity::getSettleScoreEventId, scoreEventId)
                .eq(matchId != null, MatchSettleCheckInfoEntity::getStandardMatchId, matchId)
                .eq(checkType != null, MatchSettleCheckInfoEntity::getCheckDataType, checkType);
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleCheckInfo> getModelByItems(Long scoreEventId, Long matchId, Integer checkType, List<String> dataSourceCodes) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(scoreEventId != null, MatchSettleCheckInfoEntity::getSettleScoreEventId, scoreEventId)
                .eq(matchId != null, MatchSettleCheckInfoEntity::getStandardMatchId, matchId)
                .eq(checkType != null, MatchSettleCheckInfoEntity::getCheckDataType, checkType)
                .in(CollectionUtils.isNotEmpty(dataSourceCodes), MatchSettleCheckInfoEntity::getDataSourceCode, dataSourceCodes);
        List<MatchSettleCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(entities);
    }

    @Override
    public List<MatchSettleCheckInfo> getModelBySettleScoreEventIdsAndMatchIdAndUserName(List<Long> scoreEventIds, Long standardMatchId, String userName) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleCheckInfoEntity::getStandardMatchId, standardMatchId)
                .eq( StringUtils.isNotBlank(userName), MatchSettleCheckInfoEntity::getUserName, userName)
                .in( CollectionUtils.isNotEmpty(scoreEventIds), MatchSettleCheckInfoEntity::getSettleScoreEventId, scoreEventIds);
        List<MatchSettleCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(entities);
    }

    @Override
    public List<MatchSettleCheckInfo> getModelBySettleScoreEventIdsAndMatchIdAndUserNames(List<Long> scoreEventIds, Long standardMatchId, List<String> userNames) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleCheckInfoEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(userNames), MatchSettleCheckInfoEntity::getUserName, userNames)
                .in(CollectionUtils.isNotEmpty(scoreEventIds), MatchSettleCheckInfoEntity::getSettleScoreEventId, scoreEventIds);
        List<MatchSettleCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(entities);
    }

    @Override
    public List<MatchSettleCheckInfo> getModelBySettleScoreEventIdsAndDataSourceCode(List<Long> scoreEventIds, String dataSourceCode) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq( StringUtils.isNotBlank(dataSourceCode), MatchSettleCheckInfoEntity::getDataSourceCode, dataSourceCode)
                .in( CollectionUtils.isNotEmpty(scoreEventIds), MatchSettleCheckInfoEntity::getSettleScoreEventId, scoreEventIds);
        List<MatchSettleCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(entities);
    }

    @Override
    public List<MatchSettleCheckInfo> getModelByThirdScoreEventIdAndMatchIdAndDataSourceCode(Long thirdScoreEventId, Long standardMatchId, String dataSourceCode) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleCheckInfoEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(dataSourceCode), MatchSettleCheckInfoEntity::getDataSourceCode, dataSourceCode)
                .eq(thirdScoreEventId != null, MatchSettleCheckInfoEntity::getThirdSettleScoreEventId, thirdScoreEventId);
        List<MatchSettleCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(entities);
    }

    @Override
    public List<MatchSettleCheckInfo> getModelByItemsAndOrderCreateTime(Long standardMatchId, String dataSourceCode, String eventCode,
                                                                        Integer checkType, Integer checkDataType, Integer t1, Integer t2) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleCheckInfoEntity::getStandardMatchId, standardMatchId)
                .eq( StringUtils.isNotBlank(dataSourceCode), MatchSettleCheckInfoEntity::getDataSourceCode, dataSourceCode)
                .eq( StringUtils.isNotBlank(eventCode), MatchSettleCheckInfoEntity::getEventCode, eventCode)
                .eq(checkType != null, MatchSettleCheckInfoEntity::getCheckType, checkType)
                .eq(checkDataType != null, MatchSettleCheckInfoEntity::getCheckDataType, checkDataType)
                .eq(t1 != null, MatchSettleCheckInfoEntity::getT1, t1)
                .eq(t2 != null, MatchSettleCheckInfoEntity::getT2, t2)
                .orderByDesc(MatchSettleCheckInfoEntity::getCreateTime);
        List<MatchSettleCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleCheckInfoV2Converter.convertEntityToCheckInfo(entities);
    }

    @Override
    public List<MatchSettleCheckInfoEntity> getDoShowPopupScore(Integer checkStatus, Long settleScoreEventId, String userName, Long standardMatchId) {
        LambdaQueryWrapper<MatchSettleCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(checkStatus!=null,MatchSettleCheckInfoEntity::getCheckStatus, checkStatus)
                .eq(settleScoreEventId!=null,MatchSettleCheckInfoEntity::getSettleScoreEventId, settleScoreEventId)
                .eq(StringUtils.isNotBlank(userName),MatchSettleCheckInfoEntity::getUserName, userName)
                .eq(standardMatchId!=null,MatchSettleCheckInfoEntity::getStandardMatchId, standardMatchId);
        return this.list(queryWrapper);
    }

    @Override
    public int countBySettleNumAndUser(String userName, Integer checkStatus, List<String> settleNums, Long standardMatchId) {
        return matchSettleCheckInfoMapper.countBySettleNumAndUser(userName,checkStatus,settleNums,standardMatchId);
    }

    @Override
    public void deleteByExample(MatchSettleCheckInfoExample checkInfoExample) {
        matchSettleCheckInfoMapper.deleteByExample(checkInfoExample);
    }
}
