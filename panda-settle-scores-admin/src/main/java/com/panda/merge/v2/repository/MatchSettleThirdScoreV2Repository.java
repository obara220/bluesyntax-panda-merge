package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleCheckInfoExample;
import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.model.MatchSettleThirdScoreExample;
import com.panda.merge.v2.entity.MatchSettleThirdScoreEntity;

import java.util.List;

public interface MatchSettleThirdScoreV2Repository extends IService<MatchSettleThirdScoreEntity> {
    List<MatchSettleThirdScore> getModelByStandardMatchIdAndSettleNum(Long standardMatchId, List<String> settleNums);
    List<MatchSettleThirdScore> getModelByMatchIdAndEventCodeOrderBySettleNum(Long standardMatchId, List<String> eventCodes);
    List<MatchSettleThirdScore> getModelByMatchIdAndEventCodeAndSettleNum(Long standardMatchId, List<String> eventCodes, List<String> settleNums);
    boolean updateById(MatchSettleThirdScore matchSettleThirdScore);

    boolean save(MatchSettleThirdScore matchSettleThirdScore);

    MatchSettleThirdScore getById(Long id);

    List<MatchSettleThirdScore> getByMatchIdAndAndDataSourceCodeSettleNum(Long standardMatchId,Long thirdMatchId,String dataSourceCode,List<String> settleNums);

    void deleteByExample(MatchSettleThirdScoreExample matchSettleThirdScoreExample);
}
