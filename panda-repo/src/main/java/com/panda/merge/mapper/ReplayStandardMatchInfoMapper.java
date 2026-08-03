package com.panda.merge.mapper;

import com.panda.merge.model.ReplayStandardMatchInfo;
import com.panda.merge.model.ReplayStandardMatchInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReplayStandardMatchInfoMapper {
    long countByExample(ReplayStandardMatchInfoExample example);

    int deleteByExample(ReplayStandardMatchInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ReplayStandardMatchInfo record);

    int insertSelective(ReplayStandardMatchInfo record);

    List<ReplayStandardMatchInfo> selectByExample(ReplayStandardMatchInfoExample example);

    ReplayStandardMatchInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ReplayStandardMatchInfo record, @Param("example") ReplayStandardMatchInfoExample example);

    int updateByExample(@Param("record") ReplayStandardMatchInfo record, @Param("example") ReplayStandardMatchInfoExample example);

    int updateByPrimaryKeySelective(ReplayStandardMatchInfo record);

    int updateByPrimaryKey(ReplayStandardMatchInfo record);

    int updateReplayFinish(@Param("standardMatchId") Long standardMatchId,@Param("replayStatus") Integer replayStatus,@Param("lastTimeReplayEndTime") Long lastTimeReplayEndTime);
}