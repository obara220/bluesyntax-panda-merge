package com.panda.merge.mapper;

import com.panda.merge.model.I18nMarketOdds;
import com.panda.merge.model.I18nMarketOddsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nMarketOddsMapper {
    long countByExample(I18nMarketOddsExample example);

    int deleteByExample(I18nMarketOddsExample example);

    int insert(I18nMarketOdds record);

    int insertSelective(I18nMarketOdds record);

    List<I18nMarketOdds> selectByExample(I18nMarketOddsExample example);

    int updateByExampleSelective(@Param("record") I18nMarketOdds record, @Param("example") I18nMarketOddsExample example);

    int updateByExample(@Param("record") I18nMarketOdds record, @Param("example") I18nMarketOddsExample example);
}