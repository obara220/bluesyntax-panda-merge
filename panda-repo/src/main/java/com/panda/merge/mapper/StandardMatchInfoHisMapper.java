package com.panda.merge.mapper;

import com.panda.merge.model.StandardMatchInfoHis;
import com.panda.merge.model.StandardMatchInfoHisExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardMatchInfoHisMapper {
    long countByExample(StandardMatchInfoHisExample example);

    int deleteByExample(StandardMatchInfoHisExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardMatchInfoHis record);

    int insertSelective(StandardMatchInfoHis record);

    List<StandardMatchInfoHis> selectByExampleWithBLOBs(StandardMatchInfoHisExample example);

    List<StandardMatchInfoHis> selectByExample(StandardMatchInfoHisExample example);

    StandardMatchInfoHis selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardMatchInfoHis record, @Param("example") StandardMatchInfoHisExample example);

    int updateByExampleWithBLOBs(@Param("record") StandardMatchInfoHis record, @Param("example") StandardMatchInfoHisExample example);

    int updateByExample(@Param("record") StandardMatchInfoHis record, @Param("example") StandardMatchInfoHisExample example);

    int updateByPrimaryKeySelective(StandardMatchInfoHis record);

    int updateByPrimaryKeyWithBLOBs(StandardMatchInfoHis record);

    int updateByPrimaryKey(StandardMatchInfoHis record);
}