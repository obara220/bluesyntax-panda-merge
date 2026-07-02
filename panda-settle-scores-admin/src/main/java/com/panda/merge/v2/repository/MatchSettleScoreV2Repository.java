package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleScoreExample;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;

import java.util.List;

public interface MatchSettleScoreV2Repository extends IService<MatchSettleScoreEntity> {

    boolean updateById(MatchSettleScore matchSettleScore);

    boolean updateBatchById(List<MatchSettleScore> matchSettleScores);

    void saveOrUpdateBatch(List<MatchSettleScore> matchSettleScore);

    boolean save(MatchSettleScore matchSettleScore);

    MatchSettleScore getById(Long id);

    List<MatchSettleScore> getByIds(List<Long> ids);

    List<MatchSettleScore>  getModelByStandardMatchIdAndNotSettleNum(Long standardMatchId, List<String> settleNumList);

    List<MatchSettleScore> getModelStandardMatchIdAndSettleNumAndIsGrey(Long standardMatchId, List<String> settleNumList, Integer isGray);

    List<MatchSettleScore> getModelBySettleNumAndMatchIdIdAndStatus(List<String> settleNumList,Long standardMatchId ,List<Integer> status);

    List<MatchSettleScore> getModelBySettleNumAndMatchIdIdAndNotStatus(List<String> settleNumList,Long standardMatchId ,Integer status);

    List<MatchSettleScore> getModelStandardMatchIdAndNotStatusAndIsGrey(Long standardMatchId, Integer status, Integer isGray);

    List<MatchSettleScore> getByStandardMatchIdAndEventCode(Long standardMatchId, String eventCode);

    List<MatchSettleScore> getModelsByItems(Long standardMatchId,List<String> eventCodes,List<Long> periods, Integer status, Integer t1, Integer t2);

    List<MatchSettleScore> getModelByMatchIdAndEventCodeOrderBySettleNum(Long standardMatchId,List<String> eventCodes);

    List<MatchSettleScore> getModelsByItemsAndSettleNums(Long standardMatchId,List<String> eventCodes,List<Long> periods, Integer status, List<String> settleNumList);

    List<MatchSettleScore>  getByMatchIdAndEventCodeAndNotStatus(Long standardMatchId, List<String> eventCodes,  Integer status);

    List<MatchSettleScoreEntity> selectByExample(MatchSettleScoreExample example);

    int updateByExampleSelective(MatchSettleScore matchSettleScore, MatchSettleScoreExample matchSettleScoreExample);
}
