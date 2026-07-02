package com.panda.merge.mapper;

import com.panda.merge.model.ConfigPlacenumAutoDiffTrade;
import com.panda.merge.model.ConfigPlacenumAutoDiffTradeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigPlacenumAutoDiffTradeMapper {
    long countByExample(ConfigPlacenumAutoDiffTradeExample example);

    int deleteByExample(ConfigPlacenumAutoDiffTradeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigPlacenumAutoDiffTrade record);

    int insertSelective(ConfigPlacenumAutoDiffTrade record);

    List<ConfigPlacenumAutoDiffTrade> selectByExample(ConfigPlacenumAutoDiffTradeExample example);

    ConfigPlacenumAutoDiffTrade selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigPlacenumAutoDiffTrade record, @Param("example") ConfigPlacenumAutoDiffTradeExample example);

    int updateByExample(@Param("record") ConfigPlacenumAutoDiffTrade record, @Param("example") ConfigPlacenumAutoDiffTradeExample example);

    int updateByPrimaryKeySelective(ConfigPlacenumAutoDiffTrade record);

    int updateByPrimaryKey(ConfigPlacenumAutoDiffTrade record);
}