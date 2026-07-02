package com.panda.merge.mapper;

import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdMatchInfoMapper {
    long countByExample(ThirdMatchInfoExample example);

    int deleteByExample(ThirdMatchInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdMatchInfo record);

    int insertSelective(ThirdMatchInfo record);

    List<ThirdMatchInfo> selectByExampleWithBLOBs(ThirdMatchInfoExample example);

    List<ThirdMatchInfo> selectByExample(ThirdMatchInfoExample example);

    ThirdMatchInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdMatchInfo record, @Param("example") ThirdMatchInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") ThirdMatchInfo record, @Param("example") ThirdMatchInfoExample example);

    int updateByExample(@Param("record") ThirdMatchInfo record, @Param("example") ThirdMatchInfoExample example);

    int updateByPrimaryKeySelective(ThirdMatchInfo record);

    int updateByPrimaryKeyWithBLOBs(ThirdMatchInfo record);

    int updateByPrimaryKey(ThirdMatchInfo record);

    List<ThirdMatchInfo> selectByMatchIds(@Param("matchIdList") List<Long> matchIdList);
}