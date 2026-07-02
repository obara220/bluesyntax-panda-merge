package com.panda.merge.mapper;

import com.panda.merge.model.PandaOddsConvert;
import com.panda.merge.model.PandaOddsConvertExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PandaOddsConvertMapper {
    long countByExample(PandaOddsConvertExample example);

    int deleteByExample(PandaOddsConvertExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PandaOddsConvert record);

    int insertSelective(PandaOddsConvert record);

    List<PandaOddsConvert> selectByExample(PandaOddsConvertExample example);

    PandaOddsConvert selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PandaOddsConvert record, @Param("example") PandaOddsConvertExample example);

    int updateByExample(@Param("record") PandaOddsConvert record, @Param("example") PandaOddsConvertExample example);

    int updateByPrimaryKeySelective(PandaOddsConvert record);

    int updateByPrimaryKey(PandaOddsConvert record);
}