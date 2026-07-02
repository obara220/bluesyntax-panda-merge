package com.panda.merge.mapper;

import com.panda.merge.model.NameCodeTable;
import com.panda.merge.model.NameCodeTableExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NameCodeTableMapper {
    long countByExample(NameCodeTableExample example);

    int deleteByExample(NameCodeTableExample example);

    int deleteByPrimaryKey(Long id);

    int insert(NameCodeTable record);

    int insertSelective(NameCodeTable record);

    List<NameCodeTable> selectByExample(NameCodeTableExample example);

    NameCodeTable selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") NameCodeTable record, @Param("example") NameCodeTableExample example);

    int updateByExample(@Param("record") NameCodeTable record, @Param("example") NameCodeTableExample example);

    int updateByPrimaryKeySelective(NameCodeTable record);

    int updateByPrimaryKey(NameCodeTable record);
}