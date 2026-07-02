package com.panda.merge.mapper;

import com.panda.merge.model.StandardRelationNewStandard;
import com.panda.merge.model.StandardRelationNewStandardExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardRelationNewStandardMapper {
    long countByExample(StandardRelationNewStandardExample example);

    int deleteByExample(StandardRelationNewStandardExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StandardRelationNewStandard record);

    int insertSelective(StandardRelationNewStandard record);

    List<StandardRelationNewStandard> selectByExample(StandardRelationNewStandardExample example);

    StandardRelationNewStandard selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StandardRelationNewStandard record, @Param("example") StandardRelationNewStandardExample example);

    int updateByExample(@Param("record") StandardRelationNewStandard record, @Param("example") StandardRelationNewStandardExample example);

    int updateByPrimaryKeySelective(StandardRelationNewStandard record);

    int updateByPrimaryKey(StandardRelationNewStandard record);
}