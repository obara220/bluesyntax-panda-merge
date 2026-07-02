package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTournamentTradeItem;
import com.panda.merge.model.ConfigTournamentTradeItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTournamentTradeItemMapper {
    long countByExample(ConfigTournamentTradeItemExample example);

    int deleteByExample(ConfigTournamentTradeItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTournamentTradeItem record);

    int insertSelective(ConfigTournamentTradeItem record);

    List<ConfigTournamentTradeItem> selectByExample(ConfigTournamentTradeItemExample example);

    ConfigTournamentTradeItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTournamentTradeItem record, @Param("example") ConfigTournamentTradeItemExample example);

    int updateByExample(@Param("record") ConfigTournamentTradeItem record, @Param("example") ConfigTournamentTradeItemExample example);

    int updateByPrimaryKeySelective(ConfigTournamentTradeItem record);

    int updateByPrimaryKey(ConfigTournamentTradeItem record);
}