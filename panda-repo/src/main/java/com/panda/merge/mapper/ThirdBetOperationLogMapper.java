package com.panda.merge.mapper;

import com.panda.merge.model.ThirdBetOperationLog;
import com.panda.merge.model.ThirdBetOperationLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdBetOperationLogMapper {
    long countByExample(ThirdBetOperationLogExample example);

    int deleteByExample(ThirdBetOperationLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdBetOperationLog record);

    int insertSelective(ThirdBetOperationLog record);

    List<ThirdBetOperationLog> selectByExampleWithBLOBs(ThirdBetOperationLogExample example);

    List<ThirdBetOperationLog> selectByExample(ThirdBetOperationLogExample example);

    ThirdBetOperationLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdBetOperationLog record, @Param("example") ThirdBetOperationLogExample example);

    int updateByExampleWithBLOBs(@Param("record") ThirdBetOperationLog record, @Param("example") ThirdBetOperationLogExample example);

    int updateByExample(@Param("record") ThirdBetOperationLog record, @Param("example") ThirdBetOperationLogExample example);

    int updateByPrimaryKeySelective(ThirdBetOperationLog record);

    int updateByPrimaryKeyWithBLOBs(ThirdBetOperationLog record);

    int updateByPrimaryKey(ThirdBetOperationLog record);
}