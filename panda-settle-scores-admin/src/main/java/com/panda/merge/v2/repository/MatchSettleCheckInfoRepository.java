package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.model.MatchSettleCheckInfoExample;
import com.panda.merge.v2.entity.MatchSettleCheckInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatchSettleCheckInfoRepository extends IService<MatchSettleCheckInfoEntity> {

    boolean deleteByThirdScoreEventIdAndMatchIdAndDataSourceCode(Long thirdScoreEventId,Long standardMatchId, String dataSourceCode);

    boolean save(MatchSettleCheckInfo matchSettleCheckInfo);

    boolean updateById(MatchSettleCheckInfo matchSettleCheckInfo);

    void saveOrUpdateBatch(List<MatchSettleCheckInfo> matchSettleCheckInfos);

    List<MatchSettleCheckInfo> getModelBySettleScoreEventId(Long scoreEventId);

    List<MatchSettleCheckInfoEntity> getBySettleScoreEventIdAndStandardMatchIdAndCheckDataType(Long scoreEventId,Long matchId,Integer checkType);

    List<MatchSettleCheckInfo> getModelByItems(Long scoreEventId,Long matchId,Integer checkType, List<String> dataSourceCodes);

    List<MatchSettleCheckInfo> getModelBySettleScoreEventIdsAndMatchIdAndUserName(List<Long> scoreEventIds,Long standardMatchId,String userName);

    List<MatchSettleCheckInfo> getModelBySettleScoreEventIdsAndMatchIdAndUserNames(List<Long> scoreEventIds,Long standardMatchId,List<String> userNames);

    List<MatchSettleCheckInfo> getModelBySettleScoreEventIdsAndDataSourceCode(List<Long> scoreEventIds,String dataSourceCode);

    List<MatchSettleCheckInfo> getModelByThirdScoreEventIdAndMatchIdAndDataSourceCode(Long thirdScoreEventId,Long standardMatchId, String dataSourceCode);

    List<MatchSettleCheckInfo> getModelByItemsAndOrderCreateTime(Long standardMatchId, String dataSourceCode, String eventCode,
                                                                 Integer checkType, Integer checkDataType, Integer t1, Integer t2);

    List<MatchSettleCheckInfoEntity> getDoShowPopupScore(Integer checkStatus,Long settleScoreEventId,String userName,Long standardMatchId);

    int countBySettleNumAndUser(@Param("userName")String userName, @Param("checkStatus") Integer checkStatus, @Param("settleNums") List<String> settleNums, @Param("standardMatchId") Long standardMatchId);

    void deleteByExample(MatchSettleCheckInfoExample checkInfoExample);
}