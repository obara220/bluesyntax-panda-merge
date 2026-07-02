package com.panda.merge.mapper;

import com.panda.merge.model.ThirdSportOddsFieldsTemplet2020421Delete;
import com.panda.merge.model.ThirdSportOddsFieldsTemplet2020421DeleteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdSportOddsFieldsTemplet2020421DeleteMapper {
    long countByExample(ThirdSportOddsFieldsTemplet2020421DeleteExample example);

    int deleteByExample(ThirdSportOddsFieldsTemplet2020421DeleteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdSportOddsFieldsTemplet2020421Delete record);

    int insertSelective(ThirdSportOddsFieldsTemplet2020421Delete record);

    List<ThirdSportOddsFieldsTemplet2020421Delete> selectByExample(ThirdSportOddsFieldsTemplet2020421DeleteExample example);

    ThirdSportOddsFieldsTemplet2020421Delete selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdSportOddsFieldsTemplet2020421Delete record, @Param("example") ThirdSportOddsFieldsTemplet2020421DeleteExample example);

    int updateByExample(@Param("record") ThirdSportOddsFieldsTemplet2020421Delete record, @Param("example") ThirdSportOddsFieldsTemplet2020421DeleteExample example);

    int updateByPrimaryKeySelective(ThirdSportOddsFieldsTemplet2020421Delete record);

    int updateByPrimaryKey(ThirdSportOddsFieldsTemplet2020421Delete record);
}