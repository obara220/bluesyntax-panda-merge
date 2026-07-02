package com.panda.merge.mapper;

import com.panda.merge.model.MalayConvertEurope;
import com.panda.merge.model.MalayConvertEuropeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MalayConvertEuropeMapper {
    long countByExample(MalayConvertEuropeExample example);

    int deleteByExample(MalayConvertEuropeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MalayConvertEurope record);

    int insertSelective(MalayConvertEurope record);

    List<MalayConvertEurope> selectByExample(MalayConvertEuropeExample example);

    MalayConvertEurope selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MalayConvertEurope record, @Param("example") MalayConvertEuropeExample example);

    int updateByExample(@Param("record") MalayConvertEurope record, @Param("example") MalayConvertEuropeExample example);

    int updateByPrimaryKeySelective(MalayConvertEurope record);

    int updateByPrimaryKey(MalayConvertEurope record);
}