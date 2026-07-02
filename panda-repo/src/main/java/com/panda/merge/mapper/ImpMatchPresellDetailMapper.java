package com.panda.merge.mapper;

import com.panda.merge.model.ImpMatchPresellDetail;
import com.panda.merge.model.ImpMatchPresellDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpMatchPresellDetailMapper {
    long countByExample(ImpMatchPresellDetailExample example);

    int deleteByExample(ImpMatchPresellDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ImpMatchPresellDetail record);

    int insertSelective(ImpMatchPresellDetail record);

    List<ImpMatchPresellDetail> selectByExample(ImpMatchPresellDetailExample example);

    ImpMatchPresellDetail selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ImpMatchPresellDetail record, @Param("example") ImpMatchPresellDetailExample example);

    int updateByExample(@Param("record") ImpMatchPresellDetail record, @Param("example") ImpMatchPresellDetailExample example);

    int updateByPrimaryKeySelective(ImpMatchPresellDetail record);

    int updateByPrimaryKey(ImpMatchPresellDetail record);
}