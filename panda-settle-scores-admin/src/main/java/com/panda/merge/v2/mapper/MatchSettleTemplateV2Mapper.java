package com.panda.merge.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.model.MatchSettleTemplate;
import com.panda.merge.model.MatchSettleTemplateExample;
import com.panda.merge.v2.entity.MatchSettleTemplateEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MatchSettleTemplateV2Mapper extends BaseMapper<MatchSettleTemplateEntity> {

    long countByExample(MatchSettleTemplateExample example);

    int deleteByExample(MatchSettleTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchSettleTemplateEntity record);

    int insertSelective(MatchSettleTemplateEntity record);

    List<MatchSettleTemplateEntity> selectByExample(MatchSettleTemplateExample example);

    int updateByExampleSelective(@Param("record") MatchSettleTemplateEntity record, @Param("example") MatchSettleTemplateExample example);

    int updateByExample(@Param("record") MatchSettleTemplateEntity record, @Param("example") MatchSettleTemplateExample example);

    int updateByPrimaryKeySelective(MatchSettleTemplateEntity record);

    int updateByPrimaryKey(MatchSettleTemplateEntity record);


}
