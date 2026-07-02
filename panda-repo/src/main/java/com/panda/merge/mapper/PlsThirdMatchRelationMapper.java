package com.panda.merge.mapper;

import com.panda.merge.model.PlsThirdMatchRelation;
import com.panda.merge.model.PlsThirdMatchRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PlsThirdMatchRelationMapper {
    long countByExample(PlsThirdMatchRelationExample example);

    int deleteByExample(PlsThirdMatchRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PlsThirdMatchRelation record);

    int insertSelective(PlsThirdMatchRelation record);

    List<PlsThirdMatchRelation> selectByExample(PlsThirdMatchRelationExample example);

    PlsThirdMatchRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PlsThirdMatchRelation record, @Param("example") PlsThirdMatchRelationExample example);

    int updateByExample(@Param("record") PlsThirdMatchRelation record, @Param("example") PlsThirdMatchRelationExample example);

    int updateByPrimaryKeySelective(PlsThirdMatchRelation record);

    int updateByPrimaryKey(PlsThirdMatchRelation record);

}