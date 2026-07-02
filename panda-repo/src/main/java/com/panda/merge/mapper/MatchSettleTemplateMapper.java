package com.panda.merge.mapper;

import com.panda.merge.model.MatchSettleTemplate;
import com.panda.merge.model.MatchSettleTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchSettleTemplateMapper {
    long countByExample(MatchSettleTemplateExample example);

    int deleteByExample(MatchSettleTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleTemplate record);

    int insertSelective(MatchSettleTemplate record);

    List<MatchSettleTemplate> selectByExample(MatchSettleTemplateExample example);

    MatchSettleTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchSettleTemplate record, @Param("example") MatchSettleTemplateExample example);

    int updateByExample(@Param("record") MatchSettleTemplate record, @Param("example") MatchSettleTemplateExample example);

    int updateByPrimaryKeySelective(MatchSettleTemplate record);

    int updateByPrimaryKey(MatchSettleTemplate record);
}