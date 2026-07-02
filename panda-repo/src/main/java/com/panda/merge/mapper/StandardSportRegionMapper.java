package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportRegion;
import com.panda.merge.model.StandardSportRegionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StandardSportRegionMapper {
    long countByExample(StandardSportRegionExample example);

    int deleteByExample(StandardSportRegionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportRegion record);

    int insertSelective(StandardSportRegion record);

    List<StandardSportRegion> selectByExample(StandardSportRegionExample example);

    StandardSportRegion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportRegion record, @Param("example") StandardSportRegionExample example);

    int updateByExample(@Param("record") StandardSportRegion record, @Param("example") StandardSportRegionExample example);

    int updateByPrimaryKeySelective(StandardSportRegion record);

    int updateByPrimaryKey(StandardSportRegion record);
}