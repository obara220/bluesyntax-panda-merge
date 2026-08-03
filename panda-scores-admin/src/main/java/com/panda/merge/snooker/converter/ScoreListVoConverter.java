package com.panda.merge.snooker.converter;

import com.panda.merge.constant.MatchInfoConvertEnum;
import com.panda.merge.dto.advertise.v2.PDSnookerEventDto;
import com.panda.merge.snooker.dto.SnookerV2Scores;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

/**
 * 斯诺克比分转换器
 * 用于将SnookerV2Scores转换为PDSnookerEventDto
 */
@Mapper(
    componentModel = "spring", 
    imports = {MatchInfoConvertEnum.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
public interface ScoreListVoConverter {

    /**
     * 将SnookerV2Scores转换为PDSnookerEventDto
     * 
     * @param snookerV2Scores 斯诺克比分对象
     * @param setNum 局数
     * @return PDSnookerEventDto
     */
    @Mapping(target = "sportId", source = "sportId")
    @Mapping(target = "thirdMatchId", source = "thirdMatchId")
    @Mapping(target = "setNum", source = "setNum")
    @Mapping(target = "rerack", expression = "java(snookerV2Scores != null && snookerV2Scores.getRerack() != null ? snookerV2Scores.getRerack().getHome() : 0)")
    @Mapping(target = "directLoss", source = "snookerV2Scores.directLoss")
    @Mapping(target = "concede", source = "snookerV2Scores.concede")
    PDSnookerEventDto convertSnoookerScoreToScore(SnookerV2Scores snookerV2Scores, Integer setNum, String thirdMatchId, Long sportId);
}
