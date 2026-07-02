package com.panda.merge.mapper;

import com.panda.merge.model.ThirdVideoRecord;
import com.panda.merge.model.ThirdVideoRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ThirdVideoRecordMapper {
    long countByExample(ThirdVideoRecordExample example);

    int deleteByExample(ThirdVideoRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ThirdVideoRecord record);

    int insertSelective(ThirdVideoRecord record);

    List<ThirdVideoRecord> selectByExample(ThirdVideoRecordExample example);

    ThirdVideoRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ThirdVideoRecord record, @Param("example") ThirdVideoRecordExample example);

    int updateByExample(@Param("record") ThirdVideoRecord record, @Param("example") ThirdVideoRecordExample example);

    int updateByPrimaryKeySelective(ThirdVideoRecord record);

    int updateByPrimaryKey(ThirdVideoRecord record);
}