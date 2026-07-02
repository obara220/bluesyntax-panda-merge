package com.panda.merge.mapper;

import com.panda.merge.model.OutrightMatchLog;
import com.panda.merge.model.OutrightMatchLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutrightMatchLogMapper {
    long countByExample(OutrightMatchLogExample example);

    int deleteByExample(OutrightMatchLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OutrightMatchLog record);

    int insertSelective(OutrightMatchLog record);

    List<OutrightMatchLog> selectByExample(OutrightMatchLogExample example);

    OutrightMatchLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OutrightMatchLog record, @Param("example") OutrightMatchLogExample example);

    int updateByExample(@Param("record") OutrightMatchLog record, @Param("example") OutrightMatchLogExample example);

    int updateByPrimaryKeySelective(OutrightMatchLog record);

    int updateByPrimaryKey(OutrightMatchLog record);
}