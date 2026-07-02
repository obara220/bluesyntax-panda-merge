package com.panda.merge.mapper;

import com.panda.merge.model.EuropeConvertMalay;
import com.panda.merge.model.EuropeConvertMalayExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EuropeConvertMalayMapper {
    long countByExample(EuropeConvertMalayExample example);

    int deleteByExample(EuropeConvertMalayExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EuropeConvertMalay record);

    int insertSelective(EuropeConvertMalay record);

    List<EuropeConvertMalay> selectByExample(EuropeConvertMalayExample example);

    EuropeConvertMalay selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EuropeConvertMalay record, @Param("example") EuropeConvertMalayExample example);

    int updateByExample(@Param("record") EuropeConvertMalay record, @Param("example") EuropeConvertMalayExample example);

    int updateByPrimaryKeySelective(EuropeConvertMalay record);

    int updateByPrimaryKey(EuropeConvertMalay record);
}