package com.panda.merge.mapper;

import com.panda.merge.model.ThirdGlobalStatusLog;
import com.panda.merge.model.ThirdGlobalStatusLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdGlobalStatusLogMapper {
    long countByExample(ThirdGlobalStatusLogExample example);

    int deleteByExample(ThirdGlobalStatusLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdGlobalStatusLog record);

    int insertSelective(ThirdGlobalStatusLog record);

    List<ThirdGlobalStatusLog> selectByExample(ThirdGlobalStatusLogExample example);

    ThirdGlobalStatusLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdGlobalStatusLog record, @Param("example") ThirdGlobalStatusLogExample example);

    int updateByExample(@Param("record") ThirdGlobalStatusLog record, @Param("example") ThirdGlobalStatusLogExample example);

    int updateByPrimaryKeySelective(ThirdGlobalStatusLog record);

    int updateByPrimaryKey(ThirdGlobalStatusLog record);
}