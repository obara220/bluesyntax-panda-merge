package com.panda.merge.mapper;

import com.panda.merge.model.ConfigCategoryAutoDiffTrade;
import com.panda.merge.model.ConfigCategoryAutoDiffTradeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigCategoryAutoDiffTradeMapper {
    long countByExample(ConfigCategoryAutoDiffTradeExample example);

    int deleteByExample(ConfigCategoryAutoDiffTradeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigCategoryAutoDiffTrade record);

    int insertSelective(ConfigCategoryAutoDiffTrade record);

    List<ConfigCategoryAutoDiffTrade> selectByExample(ConfigCategoryAutoDiffTradeExample example);

    ConfigCategoryAutoDiffTrade selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigCategoryAutoDiffTrade record, @Param("example") ConfigCategoryAutoDiffTradeExample example);

    int updateByExample(@Param("record") ConfigCategoryAutoDiffTrade record, @Param("example") ConfigCategoryAutoDiffTradeExample example);

    int updateByPrimaryKeySelective(ConfigCategoryAutoDiffTrade record);

    int updateByPrimaryKey(ConfigCategoryAutoDiffTrade record);
}