package com.panda.merge.mapper;

import com.panda.merge.model.ConfigOutrightTradeProbability;
import com.panda.merge.model.ConfigOutrightTradeProbabilityExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigOutrightTradeProbabilityMapper {
    long countByExample(ConfigOutrightTradeProbabilityExample example);

    int deleteByExample(ConfigOutrightTradeProbabilityExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigOutrightTradeProbability record);

    int insertSelective(ConfigOutrightTradeProbability record);

    List<ConfigOutrightTradeProbability> selectByExample(ConfigOutrightTradeProbabilityExample example);

    ConfigOutrightTradeProbability selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigOutrightTradeProbability record, @Param("example") ConfigOutrightTradeProbabilityExample example);

    int updateByExample(@Param("record") ConfigOutrightTradeProbability record, @Param("example") ConfigOutrightTradeProbabilityExample example);

    int updateByPrimaryKeySelective(ConfigOutrightTradeProbability record);

    int updateByPrimaryKey(ConfigOutrightTradeProbability record);
}