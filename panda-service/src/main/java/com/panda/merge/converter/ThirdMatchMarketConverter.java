package com.panda.merge.converter;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", imports = {ThirdSportMarketOdds.class, OddsWrapper.class})
@Component
public interface ThirdMatchMarketConverter {

    @Mapping(target = "thirdMatchSourceId", source = "thirdOutrightSourceId")
    ThirdMatchInfo convertOutrightToThirdMatchIn(ThirdOutrightMatchInfo thirdOutrightMatchInfos);

    @Mapping(target = "thirdMatchSourceId", source = "thirdOutrightSourceId")
    List<ThirdMatchInfo> convertOutrightToThirdMatchInfo(List<ThirdOutrightMatchInfo> thirdOutrightMatchInfos);

    @Mappings({
            @Mapping(source = "matchMarketStatus", target = "operateMatchStatus"),
            @Mapping(source = "standrdOutrightMatchEndTime", target = "beginTime"),
            @Mapping(expression = "java(1)", target = "matchType")
    })
    StandardMatchInfoDetail convertOutrightToStandardDetails(StandardOutrightMatchInfo standardOutrightMatchInfos);

    @Mappings({
            @Mapping(source = "matchMarketStatus", target = "operateMatchStatus"),
            @Mapping(source = "standrdOutrightMatchEndTime", target = "beginTime"),
            @Mapping(expression = "java(1)", target = "matchType")
    })
    List<StandardMatchInfoDetail> convertOutrightToStandardDetails(List<StandardOutrightMatchInfo> standardOutrightMatchInfos);

    @Mapping(expression = "java(0)", target = "matchType")
    StandardMatchInfoDetail convertstandardToStandardDetails(StandardMatchInfo standardMatchInfos);

    @Mapping(expression = "java(0)", target = "matchType")
    List<StandardMatchInfoDetail> convertstandardToStandardDetails(List<StandardMatchInfo> standardMatchInfos);


    @Mappings({
            @Mapping(source = "id", target = "matchInfoId"),
            @Mapping(source = "dataSourceCode", target = "preMatchDataProviderCode"),
            @Mapping(source = "sellStatus", target = "preMatchSellStatus")
    })
    StandardSportMarketSell convertOutrightToStandardSportMarketSell(StandardOutrightMatchInfo standardOutrightMatchInfo);

    @Mappings({
            @Mapping(source = "id", target = "matchInfoId"),
            @Mapping(source = "dataSourceCode", target = "preMatchDataProviderCode"),
            @Mapping(source = "sellStatus", target = "preMatchSellStatus")
    })
    List<StandardSportMarketSell> convertOutrightToStandardSportMarketSell(List<StandardOutrightMatchInfo> standardOutrightMatchInfo);

    @Mapping(expression = "java(new ArrayList<ThirdSportMarketOdds>())", target = "data.thirdSportMarketOddsList")
    OddsWrapper<ThirdSportMarketMessage> convertThirdSportMarket(OddsWrapper<ThirdSportMarket> thirdSportMarkets);

    @Mapping(expression = "java(new ArrayList<ThirdSportMarketOdds>())", target = "data.thirdSportMarketOddsList")
    List<OddsWrapper<ThirdSportMarketMessage>> convertThirdSportMarket(List<OddsWrapper<ThirdSportMarket>> thirdSportMarkets);
}
