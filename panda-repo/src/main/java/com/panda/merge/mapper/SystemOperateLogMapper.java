package com.panda.merge.mapper;

import com.panda.merge.model.SystemOperateLog;
import com.panda.merge.model.SystemOperateLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemOperateLogMapper {
    long countByExample(SystemOperateLogExample example);

    int deleteByExample(SystemOperateLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SystemOperateLog record);

    int insertSelective(SystemOperateLog record);

    List<SystemOperateLog> selectByExample(SystemOperateLogExample example);

    SystemOperateLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SystemOperateLog record, @Param("example") SystemOperateLogExample example);

    int updateByExample(@Param("record") SystemOperateLog record, @Param("example") SystemOperateLogExample example);

    int updateByPrimaryKeySelective(SystemOperateLog record);

    int updateByPrimaryKey(SystemOperateLog record);
}