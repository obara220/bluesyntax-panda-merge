package com.panda.merge.mapper;

import com.panda.merge.model.VirtualBatchNo;
import com.panda.merge.model.VirtualBatchNoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualBatchNoMapper {
    long countByExample(VirtualBatchNoExample example);

    int deleteByExample(VirtualBatchNoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(VirtualBatchNo record);

    int insertSelective(VirtualBatchNo record);

    List<VirtualBatchNo> selectByExample(VirtualBatchNoExample example);

    VirtualBatchNo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") VirtualBatchNo record, @Param("example") VirtualBatchNoExample example);

    int updateByExample(@Param("record") VirtualBatchNo record, @Param("example") VirtualBatchNoExample example);

    int updateByPrimaryKeySelective(VirtualBatchNo record);

    int updateByPrimaryKey(VirtualBatchNo record);
}