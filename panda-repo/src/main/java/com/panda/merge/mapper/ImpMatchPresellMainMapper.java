package com.panda.merge.mapper;

import com.panda.merge.model.ImpMatchPresellMain;
import com.panda.merge.model.ImpMatchPresellMainExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpMatchPresellMainMapper {
    long countByExample(ImpMatchPresellMainExample example);

    int deleteByExample(ImpMatchPresellMainExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ImpMatchPresellMain record);

    int insertSelective(ImpMatchPresellMain record);

    List<ImpMatchPresellMain> selectByExample(ImpMatchPresellMainExample example);

    ImpMatchPresellMain selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ImpMatchPresellMain record, @Param("example") ImpMatchPresellMainExample example);

    int updateByExample(@Param("record") ImpMatchPresellMain record, @Param("example") ImpMatchPresellMainExample example);

    int updateByPrimaryKeySelective(ImpMatchPresellMain record);

    int updateByPrimaryKey(ImpMatchPresellMain record);
}