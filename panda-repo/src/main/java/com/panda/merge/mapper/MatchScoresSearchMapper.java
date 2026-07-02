package com.panda.merge.mapper;


import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.dto.scores.MatchScoresStatusDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchScoresSearchMapper {

    List<MatchScoresBetterDto> searchScoresByStandardId(@Param("standardIds") List<Long> standardIds);
    List<MatchScoresBetterDto> searchScoresByStandardIdAndPd(@Param("standardIds") List<Long> standardIds);

    MatchScoresBetterDto selectScoresByStandardId(@Param("matchId") Long matchId);

    List<MatchScoresStatusDto> searchMatchStatusByStandardId(@Param("standardIds") List<Long> standardIds);

    List<MatchScoresBetterDto> searchThirdScoresByStandardId(@Param("standardIds") List<Long> standardIds);

    List<MatchScoresBetterDto> searchScoresByThirdId(@Param("thirdIds")List<Long> thirdIds);

    List<MatchScoresStatusDto> searchMatchStatusByThirdId(@Param("thirdIds") List<Long> thirdIds);

    List<MatchScoresBetterDto> selectScoresByMatchIdPDList(@Param("matchIdList") List<Long> matchIdList);

    List<MatchScoresBetterDto> selectScoresByMatchIdPD2List(@Param("matchIdList") List<Long> matchIdList);
}