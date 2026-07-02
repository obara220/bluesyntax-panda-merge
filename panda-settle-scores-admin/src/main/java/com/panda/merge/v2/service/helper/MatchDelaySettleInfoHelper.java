package com.panda.merge.v2.service.helper;

import com.panda.merge.config.RedisService;
import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.v2.repository.MatchDelaySettleInfoV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class MatchDelaySettleInfoHelper {

    @Autowired
    private MatchDelaySettleInfoV2Repository matchDelaySettleInfoRepository;

    @Autowired
    private RedisService redisService;

    public void setDelaySettleSecond(Long standardMatchIfo, List<MatchSettleScoreDto> matchSettleScoreDtos){
        List<MatchDelaySettleInfo> matchDelaySettleInfos = matchDelaySettleInfoRepository.getModelByStandardMatchId(standardMatchIfo);
        if (CollectionUtils.isNotEmpty(matchDelaySettleInfos)){
            matchDelaySettleInfos.forEach(d->{
                matchSettleScoreDtos.forEach(s->{
                    if (d.getScoreId().toString().equals(s.getId())){
                        if (s.getIsGrey()==null||s.getIsGrey()!=1){
                            String key = "delaySettle:"+s.getId();
                            Object second = redisService.get(key);
                            if (null!=second){
                                s.setDelayTimeSecond(Long.valueOf(second.toString()));
                            }
                        }

                    }
                });
            });
        }
    }

    public void setDelayEventSecond(Long standardMatchIfo,List<MatchSettleEventDto> matchSettleEventDtos){
        List<MatchDelaySettleInfo> matchDelaySettleInfos = matchDelaySettleInfoRepository.getModelByStandardMatchId(standardMatchIfo);
        if (CollectionUtils.isNotEmpty(matchDelaySettleInfos)){
            matchDelaySettleInfos.forEach(d->{
                matchSettleEventDtos.forEach(s->{
                    if (d.getScoreId().toString().equals(s.getId())){
                        String key = "delaySettle:"+s.getId();
                        Object second = redisService.get(key);
                        if (null!=second){
                            s.setDelayTimeSecond(Long.valueOf(second.toString()));
                        }

                    }
                });
            });
        }
    }

}
