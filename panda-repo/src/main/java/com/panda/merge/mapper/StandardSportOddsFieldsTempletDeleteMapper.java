package com.panda.merge.mapper;

import com.panda.merge.model.StandardSportOddsFieldsTempletDelete;
import com.panda.merge.model.StandardSportOddsFieldsTempletDeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardSportOddsFieldsTempletDeleteMapper {
    long countByExample(StandardSportOddsFieldsTempletDeleteExample example);

    int deleteByExample(StandardSportOddsFieldsTempletDeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardSportOddsFieldsTempletDelete record);

    int insertSelective(StandardSportOddsFieldsTempletDelete record);

    List<StandardSportOddsFieldsTempletDelete> selectByExample(StandardSportOddsFieldsTempletDeleteExample example);

    StandardSportOddsFieldsTempletDelete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardSportOddsFieldsTempletDelete record, @Param("example") StandardSportOddsFieldsTempletDeleteExample example);

    int updateByExample(@Param("record") StandardSportOddsFieldsTempletDelete record, @Param("example") StandardSportOddsFieldsTempletDeleteExample example);

    int updateByPrimaryKeySelective(StandardSportOddsFieldsTempletDelete record);

    int updateByPrimaryKey(StandardSportOddsFieldsTempletDelete record);
}