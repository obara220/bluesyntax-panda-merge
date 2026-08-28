package com.panda.merge.tabletennis.converter;

import com.panda.merge.dto.advertise.v2.PDTableTennisEventDto;
import com.panda.merge.tabletennis.dto.TableTennisV2Scores;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
public interface TableTennisScoreListConverter {

    @Mapping(target = "sportId", source = "sportId")
    @Mapping(target = "thirdMatchId", source = "thirdMatchId")
    @Mapping(target = "setNum", source = "setNum")
    @Mapping(target = "matchScore", source = "scores.matchScore")
    @Mapping(target = "setScore", source = "scores.setScore")
    @Mapping(target = "serve", source = "scores.serve")
    @Mapping(target = "kickoff", source = "scores.kickoff")
    @Mapping(target = "reServe", source = "scores.reServe")
    @Mapping(target = "yellowCard", source = "scores.yellowCard")
    @Mapping(target = "redCard", source = "scores.redCard")
    @Mapping(target = "expediteMode", source = "scores.expediteMode")
    @Mapping(target = "yellowRedCardSameHand", source = "scores.yellowRedCardSameHand")
    PDTableTennisEventDto toPdEventDto(TableTennisV2Scores scores, Integer setNum, String thirdMatchId, Long sportId);
}
