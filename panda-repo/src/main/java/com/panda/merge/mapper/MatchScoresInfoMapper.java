package com.panda.merge.mapper;

import com.panda.merge.dto.scores.MatchScoreDto;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresInfoExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchScoresInfoMapper {
    long countByExample(MatchScoresInfoExample example);

    int deleteByExample(MatchScoresInfoExample example);

    int deleteByPrimaryKey(Long id);


    void batchUpdateByPrimaryKey(List<MatchScoresInfo> record);


    int insert(MatchScoresInfo record);

    int insertSelective(MatchScoresInfo record);

    /**
     * 查询赛事比分
     * @param example
     * @return
     */
    List<MatchScoresInfo> selectByExample(MatchScoresInfoExample example);

    /**
     * 查赛事比分
     * @param id
     * @return
     */
    MatchScoresInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchScoresInfo record, @Param("example") MatchScoresInfoExample example);

    int updateByExample(@Param("record") MatchScoresInfo record, @Param("example") MatchScoresInfoExample example);

    int updateByPrimaryKeySelective(MatchScoresInfo record);

    int updateByPrimaryKey(MatchScoresInfo record);

    /**
     * 查询标准赛事下的所有比分
     * @param matchId
     * @return
     */
    List<MatchScoresInfo> queryScoreByStandardMatchId(@Param("matchId") Long matchId,@Param("dataSourceType") Integer dataSourceType);

    List<MatchScoreDto> queryScoresListByMatchIds(@Param("list") List<Long> matchInfoId);
}