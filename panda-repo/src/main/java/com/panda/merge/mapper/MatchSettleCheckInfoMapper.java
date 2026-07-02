package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.model.MatchSettleCheckInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSettleCheckInfoMapper {
    long countByExample(MatchSettleCheckInfoExample example);

    int deleteByExample(MatchSettleCheckInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleCheckInfo record);

    int insertSelective(MatchSettleCheckInfo record);

    List<MatchSettleCheckInfo> selectByExample(MatchSettleCheckInfoExample example);

    MatchSettleCheckInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleCheckInfo record, @Param("example") MatchSettleCheckInfoExample example);

    int updateByExample(@Param("record") MatchSettleCheckInfo record, @Param("example") MatchSettleCheckInfoExample example);

    int updateByPrimaryKeySelective(MatchSettleCheckInfo record);

    int updateByPrimaryKey(MatchSettleCheckInfo record);

    /**
     * 查询核对比分是否已经确认
     * @param users
     * @param checkStatus
     * @param settleNums
     * @param standardMatchId
     * @return
     */
    int countBySettleNumAndUser(@Param("userName")String userName,@Param("checkStatus") Integer checkStatus,@Param("settleNums") List<String> settleNums,@Param("standardMatchId") Long standardMatchId);
}