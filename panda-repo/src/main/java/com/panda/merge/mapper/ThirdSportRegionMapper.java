package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportRegion;
import com.panda.merge.model.ThirdSportRegionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdSportRegionMapper {
    long countByExample(ThirdSportRegionExample example);

    int deleteByExample(ThirdSportRegionExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdSportRegion record);

    int insertSelective(ThirdSportRegion record);

    List<ThirdSportRegion> selectByExample(ThirdSportRegionExample example);

    ThirdSportRegion selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdSportRegion record, @Param("example") ThirdSportRegionExample example);

    int updateByExample(@Param("record") ThirdSportRegion record, @Param("example") ThirdSportRegionExample example);

    int updateByPrimaryKeySelective(ThirdSportRegion record);

    int updateByPrimaryKey(ThirdSportRegion record);
}