package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleFactorCheckInfo;
import com.panda.merge.v2.entity.MatchSettleFactorCheckInfoEntity;

import java.util.List;


public interface MatchSettleFactorCheckInfoRepository extends IService<MatchSettleFactorCheckInfoEntity> {
    List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoListCaseOne(Long standardMatchId, String settleNum);
    List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoListCaseTwo(Long standardMatchId, List<String> settleNums);
     void updateMatchSettleFactorCheckInfoToRedis(MatchSettleFactorCheckInfo info,boolean isInsert);
}