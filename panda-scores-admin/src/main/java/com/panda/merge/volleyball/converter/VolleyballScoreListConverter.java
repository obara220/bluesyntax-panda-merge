package com.panda.merge.volleyball.converter;

import com.panda.merge.dto.advertise.v2.PDVolleyballEventDto;
import com.panda.merge.volleyball.dto.VolleyballV2Scores;
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
public interface VolleyballScoreListConverter {

    @Mapping(target = "sportId", source = "sportId")
    @Mapping(target = "thirdMatchId", source = "thirdMatchId")
    @Mapping(target = "setNum", source = "setNum")
    @Mapping(target = "matchScore", source = "scores.matchScore")
    @Mapping(target = "setScore", source = "scores.setScore")
    @Mapping(target = "serve", source = "scores.serve")
    @Mapping(target = "serviceError", source = "scores.serviceError")
    @Mapping(target = "out", source = "scores.out")
    @Mapping(target = "ace", source = "scores.ace")
    @Mapping(target = "kill", source = "scores.kill")
    @Mapping(target = "block", source = "scores.block")
    @Mapping(target = "expulsion", source = "scores.expulsion")
    @Mapping(target = "disqualification", source = "scores.disqualification")
    @Mapping(target = "penalty", source = "scores.penalty")
    @Mapping(target = "error", source = "scores.error")
    @Mapping(target = "kickoff", source = "scores.kickoff")
    PDVolleyballEventDto toPdEventDto(VolleyballV2Scores scores, Integer setNum, String thirdMatchId, Long sportId);
}
