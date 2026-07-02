package com.panda.merge.mapper;

import com.panda.merge.model.StandardOutrightMarket;
import com.panda.merge.model.StandardOutrightMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardOutrightMarketMapper {
    long countByExample(StandardOutrightMarketExample example);

    int deleteByExample(StandardOutrightMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardOutrightMarket record);

    int insertSelective(StandardOutrightMarket record);

    List<StandardOutrightMarket> selectByExample(StandardOutrightMarketExample example);

    StandardOutrightMarket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardOutrightMarket record, @Param("example") StandardOutrightMarketExample example);

    int updateByExample(@Param("record") StandardOutrightMarket record, @Param("example") StandardOutrightMarketExample example);

    int updateByPrimaryKeySelective(StandardOutrightMarket record);

    int updateByPrimaryKey(StandardOutrightMarket record);
}