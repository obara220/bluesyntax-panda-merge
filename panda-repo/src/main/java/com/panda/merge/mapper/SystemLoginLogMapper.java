package com.panda.merge.mapper;

import com.panda.merge.model.SystemLoginLog;
import com.panda.merge.model.SystemLoginLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLoginLogMapper {
    long countByExample(SystemLoginLogExample example);

    int deleteByExample(SystemLoginLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SystemLoginLog record);

    int insertSelective(SystemLoginLog record);

    List<SystemLoginLog> selectByExample(SystemLoginLogExample example);

    SystemLoginLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SystemLoginLog record, @Param("example") SystemLoginLogExample example);

    int updateByExample(@Param("record") SystemLoginLog record, @Param("example") SystemLoginLogExample example);

    int updateByPrimaryKeySelective(SystemLoginLog record);

    int updateByPrimaryKey(SystemLoginLog record);
}