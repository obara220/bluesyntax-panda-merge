package com.panda.merge.service;

import com.panda.merge.model.ReplayStandardMatchInfo;

public interface ReplayMatchService {

    ReplayStandardMatchInfo getReplayStandardMatchInfo(Long standardMatchId);

    Integer updateReplayStandardMatchInfo(ReplayStandardMatchInfo replayStandardMatchInfo);

    Integer updateReplayStatusStop(Long standardMatchId);

    Integer updateReplayFinish(Long standardMatchId);
}
