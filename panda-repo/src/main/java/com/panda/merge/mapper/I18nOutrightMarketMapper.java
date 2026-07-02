package com.panda.merge.mapper;

import com.panda.merge.model.I18nOutrightMarket;
import com.panda.merge.model.I18nOutrightMarketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nOutrightMarketMapper {
    long countByExample(I18nOutrightMarketExample example);

    int deleteByExample(I18nOutrightMarketExample example);

    int deleteByPrimaryKey(Long id);

    int insert(I18nOutrightMarket record);

    int insertSelective(I18nOutrightMarket record);

    List<I18nOutrightMarket> selectByExample(I18nOutrightMarketExample example);

    I18nOutrightMarket selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") I18nOutrightMarket record, @Param("example") I18nOutrightMarketExample example);

    int updateByExample(@Param("record") I18nOutrightMarket record, @Param("example") I18nOutrightMarketExample example);

    int updateByPrimaryKeySelective(I18nOutrightMarket record);

    int updateByPrimaryKey(I18nOutrightMarket record);
}