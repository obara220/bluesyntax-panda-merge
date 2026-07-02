package com.panda.merge.mapper;

import com.panda.merge.model.SystemTypeDict;
import com.panda.merge.model.SystemTypeDictExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemTypeDictMapper {
    long countByExample(SystemTypeDictExample example);

    int deleteByExample(SystemTypeDictExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SystemTypeDict record);

    int insertSelective(SystemTypeDict record);

    List<SystemTypeDict> selectByExample(SystemTypeDictExample example);

    SystemTypeDict selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SystemTypeDict record, @Param("example") SystemTypeDictExample example);

    int updateByExample(@Param("record") SystemTypeDict record, @Param("example") SystemTypeDictExample example);

    int updateByPrimaryKeySelective(SystemTypeDict record);

    int updateByPrimaryKey(SystemTypeDict record);
}