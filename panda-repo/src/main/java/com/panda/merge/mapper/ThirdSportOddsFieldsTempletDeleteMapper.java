package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportOddsFieldsTempletDelete;
import com.panda.merge.model.ThirdSportOddsFieldsTempletDeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportOddsFieldsTempletDeleteMapper {
    long countByExample(ThirdSportOddsFieldsTempletDeleteExample example);

    int deleteByExample(ThirdSportOddsFieldsTempletDeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportOddsFieldsTempletDelete record);

    int insertSelective(ThirdSportOddsFieldsTempletDelete record);

    List<ThirdSportOddsFieldsTempletDelete> selectByExample(ThirdSportOddsFieldsTempletDeleteExample example);

    ThirdSportOddsFieldsTempletDelete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportOddsFieldsTempletDelete record, @Param("example") ThirdSportOddsFieldsTempletDeleteExample example);

    int updateByExample(@Param("record") ThirdSportOddsFieldsTempletDelete record, @Param("example") ThirdSportOddsFieldsTempletDeleteExample example);

    int updateByPrimaryKeySelective(ThirdSportOddsFieldsTempletDelete record);

    int updateByPrimaryKey(ThirdSportOddsFieldsTempletDelete record);
}