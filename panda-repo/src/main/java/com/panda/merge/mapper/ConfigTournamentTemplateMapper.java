package com.panda.merge.mapper;

import com.panda.merge.model.ConfigTournamentTemplate;
import com.panda.merge.model.ConfigTournamentTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTournamentTemplateMapper {
    long countByExample(ConfigTournamentTemplateExample example);

    int deleteByExample(ConfigTournamentTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigTournamentTemplate record);

    int insertSelective(ConfigTournamentTemplate record);

    List<ConfigTournamentTemplate> selectByExample(ConfigTournamentTemplateExample example);

    ConfigTournamentTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigTournamentTemplate record, @Param("example") ConfigTournamentTemplateExample example);

    int updateByExample(@Param("record") ConfigTournamentTemplate record, @Param("example") ConfigTournamentTemplateExample example);

    int updateByPrimaryKeySelective(ConfigTournamentTemplate record);

    int updateByPrimaryKey(ConfigTournamentTemplate record);
}