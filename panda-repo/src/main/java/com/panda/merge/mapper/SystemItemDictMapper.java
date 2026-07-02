package com.panda.merge.mapper;

import com.panda.merge.model.SystemItemDict;
import com.panda.merge.model.SystemItemDictExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemItemDictMapper {
    long countByExample(SystemItemDictExample example);

    int deleteByExample(SystemItemDictExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SystemItemDict record);

    int insertSelective(SystemItemDict record);

    List<SystemItemDict> selectByExample(SystemItemDictExample example);

    SystemItemDict selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SystemItemDict record, @Param("example") SystemItemDictExample example);

    int updateByExample(@Param("record") SystemItemDict record, @Param("example") SystemItemDictExample example);

    int updateByPrimaryKeySelective(SystemItemDict record);

    int updateByPrimaryKey(SystemItemDict record);
}