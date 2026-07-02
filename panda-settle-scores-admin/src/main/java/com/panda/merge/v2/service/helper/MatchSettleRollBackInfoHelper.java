package com.panda.merge.v2.service.helper;

import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.v2.entity.MatchSettleRollBackInfoEntity;
import com.panda.merge.v2.repository.MatchSettleRollBackInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MatchSettleRollBackInfoHelper {

    @Autowired
    private MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;


    public void settleRollBackSetNullOrderCount(Long id){
        MatchSettleRollBackInfoEntity info = matchSettleRollBackInfoRepository.getMatchSettleRollBackInfo(id);
        if(info != null){
            info.setRollBackOrderCount(0l);
            info.setOrderCount(0L);
            info.setModifyTime(System.currentTimeMillis());
            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,false);
        }
    }

    public void batchSettleRollBackSetNullOrderCount(List<Long> ids){
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            settleRollBackSetNullOrderCount(id);
        }
    }

    public void setRollBackStatusScores(List<MatchSettleScoreDto> scores, Long stndardMatchId){
        if(scores != null && scores.size() > 0){
            List<MatchSettleRollBackInfo> list =matchSettleRollBackInfoRepository.getModelByMatchId(stndardMatchId);
            Map<String,MatchSettleRollBackInfo> map =new HashMap<>();
            for (MatchSettleRollBackInfo matchSettleRollBackInfo : list) {
                map.put(matchSettleRollBackInfo.getId().toString(),matchSettleRollBackInfo);
            }
            for (MatchSettleScoreDto score : scores) {
                MatchSettleRollBackInfo info =map.get(score.getId());
                if(info!=null){
                    score.setRollBackStatus(info.getRollBackStatus());
                    score.setRollBackOrderCount(info.getRollBackOrderCount());
                }
            }
        }
    }

    public void setRollBackStatusEvent(List<MatchSettleEventDto> events, Long stndardMatchId){
        if(events != null && events.size() > 0){
            List<MatchSettleRollBackInfo> list =matchSettleRollBackInfoRepository.getModelByMatchId(stndardMatchId);
            Map<String,MatchSettleRollBackInfo> map=new HashMap<>();
            for (MatchSettleRollBackInfo info : list) {
                map.put(info.getId().toString(),info);
            }
            for (MatchSettleEventDto score : events) {
                MatchSettleRollBackInfo info =map.get(score.getId());
                if(info!=null){
                    score.setRollBackStatus(info.getRollBackStatus());
                    score.setRollBackOrderCount(info.getRollBackOrderCount());
                }
            }
        }
    }
}
