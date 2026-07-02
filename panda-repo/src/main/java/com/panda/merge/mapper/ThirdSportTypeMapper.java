package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportType;
import com.panda.merge.model.ThirdSportTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdSportTypeMapper {
    long countByExample(ThirdSportTypeExample example);

    int deleteByExample(ThirdSportTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportType record);

    int insertSelective(ThirdSportType record);

    List<ThirdSportType> selectByExample(ThirdSportTypeExample example);

    ThirdSportType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportType record, @Param("example") ThirdSportTypeExample example);

    int updateByExample(@Param("record") ThirdSportType record, @Param("example") ThirdSportTypeExample example);

    int updateByPrimaryKeySelective(ThirdSportType record);

    int updateByPrimaryKey(ThirdSportType record);
}