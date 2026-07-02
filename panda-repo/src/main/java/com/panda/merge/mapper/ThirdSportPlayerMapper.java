package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportPlayer;
import com.panda.merge.model.ThirdSportPlayerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportPlayerMapper {
    long countByExample(ThirdSportPlayerExample example);

    int deleteByExample(ThirdSportPlayerExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportPlayer record);

    int insertSelective(ThirdSportPlayer record);

    List<ThirdSportPlayer> selectByExampleWithBLOBs(ThirdSportPlayerExample example);

    List<ThirdSportPlayer> selectByExample(ThirdSportPlayerExample example);

    ThirdSportPlayer selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportPlayer record, @Param("example") ThirdSportPlayerExample example);

    int updateByExampleWithBLOBs(@Param("record") ThirdSportPlayer record, @Param("example") ThirdSportPlayerExample example);

    int updateByExample(@Param("record") ThirdSportPlayer record, @Param("example") ThirdSportPlayerExample example);

    int updateByPrimaryKeySelective(ThirdSportPlayer record);

    int updateByPrimaryKeyWithBLOBs(ThirdSportPlayer record);

    int updateByPrimaryKey(ThirdSportPlayer record);
}