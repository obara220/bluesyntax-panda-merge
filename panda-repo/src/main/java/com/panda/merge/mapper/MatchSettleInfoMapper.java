package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.model.MatchSettleInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSettleInfoMapper {

    long countByExample(MatchSettleInfoExample example);

    int deleteByExample(MatchSettleInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleInfo record);

    int insertSelective(MatchSettleInfo record);

    List<MatchSettleInfo> selectByExampleWithBLOBs(MatchSettleInfoExample example);

    List<MatchSettleInfo> selectByExample(MatchSettleInfoExample example);

    MatchSettleInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleInfo record, @Param("example") MatchSettleInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") MatchSettleInfo record, @Param("example") MatchSettleInfoExample example);

    int updateByExample(@Param("record") MatchSettleInfo record, @Param("example") MatchSettleInfoExample example);

    int updateByPrimaryKeySelective(MatchSettleInfo record);

    int updateByPrimaryKeyWithBLOBs(MatchSettleInfo record);

    int updateByPrimaryKey(MatchSettleInfo record);


    List<MatchSettleInfo> queryDeleteMatchSettleInfo(@Param("beginTime") Long beginTime);

    List<MatchSettleInfo> querySettleInfoNotInStandardMatchInfo(@Param("beginTime") Long beginTime);

}