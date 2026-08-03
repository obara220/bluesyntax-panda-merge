package com.panda.merge.service.impl;

import com.panda.merge.common.enums.ReplayStatusEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.mapper.ReplayStandardMatchInfoMapper;
import com.panda.merge.model.ReplayStandardMatchInfo;
import com.panda.merge.model.ReplayStandardMatchInfoExample;
import com.panda.merge.service.ReplayMatchService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ReplayMatchServiceImpl implements ReplayMatchService {

    @Resource
    private ReplayStandardMatchInfoMapper replayStandardMatchInfoMapper;

    @Override
    public ReplayStandardMatchInfo getReplayStandardMatchInfo(Long standardMatchId){
        ReplayStandardMatchInfoExample example = new ReplayStandardMatchInfoExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<ReplayStandardMatchInfo> replayStandardMatchInfos = replayStandardMatchInfoMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(replayStandardMatchInfos)) {
            return replayStandardMatchInfos.get(0);
        }
        return null;
    }

    @Override
    public Integer updateReplayStandardMatchInfo(ReplayStandardMatchInfo replayStandardMatchInfo) {
        return replayStandardMatchInfoMapper.updateByPrimaryKeySelective(replayStandardMatchInfo);
    }

    @Override
    public Integer updateReplayStatusStop(Long standardMatchId) {
        ReplayStandardMatchInfoExample example = new ReplayStandardMatchInfoExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        ReplayStandardMatchInfo record =  new ReplayStandardMatchInfo();
        record.setReplayStatus(ReplayStatusEnum.STOP.getCode());
        record.setLastTimeReplayEndTime(TimeUtils.millsSecondsEast8ZoneGmt());
        return replayStandardMatchInfoMapper.updateByExampleSelective(record,example);
    }

    @Override
    public Integer updateReplayFinish(Long standardMatchId) {
        return replayStandardMatchInfoMapper.updateReplayFinish(standardMatchId,ReplayStatusEnum.STOP.getCode(),TimeUtils.millsSecondsEast8ZoneGmt());
    }
}
