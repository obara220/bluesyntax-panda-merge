package com.panda.merge.mapper;

import com.panda.merge.model.ThirdVideoBoardCastRecord;
import com.panda.merge.model.ThirdVideoBoardCastRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdVideoBoardCastRecordMapper {
    long countByExample(ThirdVideoBoardCastRecordExample example);

    int deleteByExample(ThirdVideoBoardCastRecordExample example);

    int deleteByPrimaryKey(String id);

    int insert(ThirdVideoBoardCastRecord record);

    int insertSelective(ThirdVideoBoardCastRecord record);

    List<ThirdVideoBoardCastRecord> selectByExampleWithBLOBs(ThirdVideoBoardCastRecordExample example);

    List<ThirdVideoBoardCastRecord> selectByExample(ThirdVideoBoardCastRecordExample example);

    ThirdVideoBoardCastRecord selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ThirdVideoBoardCastRecord record, @Param("example") ThirdVideoBoardCastRecordExample example);

    int updateByExampleWithBLOBs(@Param("record") ThirdVideoBoardCastRecord record, @Param("example") ThirdVideoBoardCastRecordExample example);

    int updateByExample(@Param("record") ThirdVideoBoardCastRecord record, @Param("example") ThirdVideoBoardCastRecordExample example);

    int updateByPrimaryKeySelective(ThirdVideoBoardCastRecord record);

    int updateByPrimaryKeyWithBLOBs(ThirdVideoBoardCastRecord record);

    int updateByPrimaryKey(ThirdVideoBoardCastRecord record);
}