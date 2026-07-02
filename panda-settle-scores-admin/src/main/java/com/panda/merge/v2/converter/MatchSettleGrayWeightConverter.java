package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleGrayWeight;
import com.panda.merge.v2.entity.MatchSettleGrayWeightEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleGrayWeightConverter {

    MatchSettleGrayWeightEntity convertGrayWeightToEntity(MatchSettleGrayWeight matchSettleGrayWeight);

    List<MatchSettleGrayWeightEntity> convertGrayWeightToEntity(List<MatchSettleGrayWeight> matchSettleGrayWeights);

    MatchSettleGrayWeight convertEntityToGrayWeight(MatchSettleGrayWeightEntity matchSettleGrayWeight);

    List<MatchSettleGrayWeight> convertEntityToGrayWeight(List<MatchSettleGrayWeightEntity> matchSettleGrayWeightEntities);

}
