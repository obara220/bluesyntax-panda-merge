package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


public interface StandardMatchInfoMapper {
    long countByExample(StandardMatchInfoExample example);

    int deleteByExample(StandardMatchInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMatchInfo record);

    int insertSelective(StandardMatchInfo record);

    List<StandardMatchInfo> selectByExampleWithBLOBs(StandardMatchInfoExample example);

    List<StandardMatchInfo> selectByExample(StandardMatchInfoExample example);

    StandardMatchInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMatchInfo record, @Param("example") StandardMatchInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") StandardMatchInfo record, @Param("example") StandardMatchInfoExample example);

    int updateByExample(@Param("record") StandardMatchInfo record, @Param("example") StandardMatchInfoExample example);

    int updateByPrimaryKeySelective(StandardMatchInfo record);

    int updateByPrimaryKeyWithBLOBs(StandardMatchInfo record);

    int updateByPrimaryKey(StandardMatchInfo record);
}