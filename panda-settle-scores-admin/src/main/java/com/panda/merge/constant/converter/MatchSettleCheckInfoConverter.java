package com.panda.merge.constant.converter;

import com.panda.merge.data.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.model.MatchSettleCheckInfo;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleCheckInfoConverter {

    MatchSettleCheckInfoEntity convertCheckInfoToEntity(MatchSettleCheckInfo checkInfo);

    List<MatchSettleCheckInfoEntity> convertCheckInfoToEntity(List<MatchSettleCheckInfo> checkInfos);

}
