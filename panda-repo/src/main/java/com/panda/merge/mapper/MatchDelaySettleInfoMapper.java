package com.panda.merge.mapper;

import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.model.MatchDelaySettleInfoExample;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleEventExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchDelaySettleInfoMapper {

    long countByExample(MatchDelaySettleInfoExample example);

    int deleteByExample(MatchDelaySettleInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchDelaySettleInfo record);

    int insertSelective(MatchDelaySettleInfo record);


    List<MatchDelaySettleInfo> selectByExample(MatchDelaySettleInfoExample example);

    MatchDelaySettleInfo selectByPrimaryKey(Long id);


    int updateSettleStatusByScoreId(@Param("scoreId") Long scoreId, @Param("settleStatus") Integer settleStatus);


    int updateByPrimaryKey(MatchDelaySettleInfo record);

    int updateSettleStatusByCheckInfoId(@Param("checkInfoId") Long checkInfoId, @Param("settleStatus") Integer settleStatus);

    int updateMatchDelaySettleInfoList(@Param("list") List<Long> list,@Param("settleStatus")Integer settleStatus);
}