package com.panda.merge.v2.converter;

import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.v2.entity.MatchSettleInfoEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface MatchSettleInfoConverter {

    MatchSettleInfoEntity convertMatchSettleInfoToEntity(MatchSettleInfo matchSettleInfo);

    List<MatchSettleInfoEntity> convertMatchSettleInfoToEntity(List<MatchSettleInfo> matchSettleInfo);

    MatchSettleInfo convertMatchSettleInfoEntityToInfo(MatchSettleInfoEntity matchSettleInfoEntity);

    List<MatchSettleInfo> convertMatchSettleInfoEntityToInfo(List<MatchSettleInfoEntity> matchSettleInfoEntity);

}
