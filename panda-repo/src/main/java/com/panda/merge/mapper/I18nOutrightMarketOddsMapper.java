package com.panda.merge.mapper;

import com.panda.merge.model.I18nOutrightMarketOdds;
import com.panda.merge.model.I18nOutrightMarketOddsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nOutrightMarketOddsMapper {
    long countByExample(I18nOutrightMarketOddsExample example);

    int deleteByExample(I18nOutrightMarketOddsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(I18nOutrightMarketOdds record);

    int insertSelective(I18nOutrightMarketOdds record);

    List<I18nOutrightMarketOdds> selectByExample(I18nOutrightMarketOddsExample example);

    I18nOutrightMarketOdds selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") I18nOutrightMarketOdds record, @Param("example") I18nOutrightMarketOddsExample example);

    int updateByExample(@Param("record") I18nOutrightMarketOdds record, @Param("example") I18nOutrightMarketOddsExample example);

    int updateByPrimaryKeySelective(I18nOutrightMarketOdds record);

    int updateByPrimaryKey(I18nOutrightMarketOdds record);
}