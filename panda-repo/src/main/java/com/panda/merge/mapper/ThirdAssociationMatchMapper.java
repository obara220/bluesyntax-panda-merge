package com.panda.merge.mapper;

import com.panda.merge.model.ThirdAssociationMatch;
import com.panda.merge.model.ThirdAssociationMatchExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdAssociationMatchMapper {
    long countByExample(ThirdAssociationMatchExample example);

    int deleteByExample(ThirdAssociationMatchExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdAssociationMatch record);

    int insertSelective(ThirdAssociationMatch record);

    List<ThirdAssociationMatch> selectByExample(ThirdAssociationMatchExample example);

    ThirdAssociationMatch selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdAssociationMatch record, @Param("example") ThirdAssociationMatchExample example);

    int updateByExample(@Param("record") ThirdAssociationMatch record, @Param("example") ThirdAssociationMatchExample example);

    int updateByPrimaryKeySelective(ThirdAssociationMatch record);

    int updateByPrimaryKey(ThirdAssociationMatch record);
}