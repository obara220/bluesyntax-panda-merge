package com.panda.merge.mapper;

import com.panda.merge.model.ConfigMatchStatus;
import com.panda.merge.model.ConfigMatchStatusExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMatchStatusMapper {
    long countByExample(ConfigMatchStatusExample example);

    int deleteByExample(ConfigMatchStatusExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConfigMatchStatus record);

    int insertSelective(ConfigMatchStatus record);

    List<ConfigMatchStatus> selectByExample(ConfigMatchStatusExample example);

    ConfigMatchStatus selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConfigMatchStatus record, @Param("example") ConfigMatchStatusExample example);

    int updateByExample(@Param("record") ConfigMatchStatus record, @Param("example") ConfigMatchStatusExample example);

    int updateByPrimaryKeySelective(ConfigMatchStatus record);

    int updateByPrimaryKey(ConfigMatchStatus record);
}