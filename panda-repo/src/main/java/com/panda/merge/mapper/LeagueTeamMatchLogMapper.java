package com.panda.merge.mapper;

import com.panda.merge.model.LeagueTeamMatchLog;
import com.panda.merge.model.LeagueTeamMatchLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeagueTeamMatchLogMapper {
    long countByExample(LeagueTeamMatchLogExample example);

    int deleteByExample(LeagueTeamMatchLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(LeagueTeamMatchLog record);

    int insertSelective(LeagueTeamMatchLog record);

    List<LeagueTeamMatchLog> selectByExample(LeagueTeamMatchLogExample example);

    LeagueTeamMatchLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") LeagueTeamMatchLog record, @Param("example") LeagueTeamMatchLogExample example);

    int updateByExample(@Param("record") LeagueTeamMatchLog record, @Param("example") LeagueTeamMatchLogExample example);

    int updateByPrimaryKeySelective(LeagueTeamMatchLog record);

    int updateByPrimaryKey(LeagueTeamMatchLog record);
}