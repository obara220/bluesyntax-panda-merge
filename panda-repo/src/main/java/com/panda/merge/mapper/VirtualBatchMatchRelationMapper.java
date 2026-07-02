package com.panda.merge.mapper;

import com.panda.merge.model.VirtualBatchMatchRelation;
import com.panda.merge.model.VirtualBatchMatchRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualBatchMatchRelationMapper {
    long countByExample(VirtualBatchMatchRelationExample example);

    int deleteByExample(VirtualBatchMatchRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(VirtualBatchMatchRelation record);

    int insertSelective(VirtualBatchMatchRelation record);

    List<VirtualBatchMatchRelation> selectByExample(VirtualBatchMatchRelationExample example);

    VirtualBatchMatchRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") VirtualBatchMatchRelation record, @Param("example") VirtualBatchMatchRelationExample example);

    int updateByExample(@Param("record") VirtualBatchMatchRelation record, @Param("example") VirtualBatchMatchRelationExample example);

    int updateByPrimaryKeySelective(VirtualBatchMatchRelation record);

    int updateByPrimaryKey(VirtualBatchMatchRelation record);
}